 //<editor-fold defaultstate="collapsed" desc=" COPYRIGHT AND LICENSE ">
/* VisNow
 Copyright (C) 2006-2013 University of Warsaw, ICM

 This file is part of GNU Classpath.

 GNU Classpath is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2, or (at your option)
 any later version.

 GNU Classpath is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with GNU Classpath; see the file COPYING.  If not, write to the
 University of Warsaw, Interdisciplinary Centre for Mathematical and
 Computational Modelling, Pawinskiego 5a, 02-106 Warsaw, Poland.

 Linking this library statically or dynamically with other modules is
 making a combined work based on this library.  Thus, the terms and
 conditions of the GNU General Public License cover the whole
 combination.

 As a special exception, the copyright holders of this library give you
 permission to link this library with independent modules to produce an
 executable, regardless of the license terms of these independent
 modules, and to copy and distribute the resulting executable under
 terms of your choice, provided that you also meet, for each linked
 independent module, the terms and conditions of the license of that
 module.  An independent module is a module which is not derived from
 or based on this library.  If you modify this library, you may extend
 this exception to your version of the library, but you are not
 obligated to do so.  If you do not wish to do so, delete this
 exception statement from your version.
 */
//</editor-fold>

package pl.edu.icm.visnow.lib.basic.readers.ReadVisNowField;

import pl.edu.icm.visnow.datasets.DataContainer;
import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.datasets.dataarrays.*;
import pl.edu.icm.visnow.lib.basic.readers.ReadVisNowField.utils.BooleanArrayIOSchema;
import pl.edu.icm.visnow.lib.basic.readers.ReadVisNowField.utils.ComponentIOSchema;
import pl.edu.icm.visnow.lib.basic.readers.ReadVisNowField.utils.DataElementIOSchema;
import pl.edu.icm.visnow.lib.basic.readers.ReadVisNowField.utils.FileSectionSchema;
import pl.edu.icm.visnow.lib.basic.readers.ReadVisNowField.utils.IntArrayIOSchema;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
/**
 * Section model holds all data necessary for reading a file section<p>
 * In the case of a single component section, data table is produced and hold in a proper ...Arrs table<p>
 * In general, each data item described in the sectionSchema provides: <p>
 * an entry in ...Arrs table providing place where the data will be read in<p>
 * 
 *
 */
class SectionModel
{
   public static int[] typeLengths = new int[]
   {
      1, 1, 2, 4, 4, 8, 8, 1
   };
   
   Field outField;
   FileSectionSchema sectionSchema;
   float time = 0;
   int nData = 1;  
   int nItems;         //number of items to be read for each node
   /**
    * 
    */
   int[] cindex;
   int[] comps;
   int[] coords;
   /**
    * offset of the (first) item from the beginning of data series 
    * in bytes for binary files
    * in items for ascii continuous or column files
    * in characters for ascii fixed column files (position of the first char to be read)
    */
   int[] offsets;
   /**
    * offset of the last character of the item from the beginning of data series
    * in characters, used only for ascii fixed column files (position of the last char to be read)
    */
   int[] offsets1;
   /**
    * component type (as in DataArray class - see constant values there)
    */
   int[] types;
   int[] vlens;
   boolean[][] boolArrs;
   byte[][] byteArrs;
   short[][] shortArrs;
   int[][] intArrs;
   float[][] floatArrs;
   double[][] dblArrs;
   String[][] strArrs;
   DataElementIOSchema[] schemas;
   int[] ind;

   SectionModel(FileSectionSchema sectionSchema, Field outField, float time)
   {
      this.sectionSchema = sectionSchema;
      nItems = 0;
      nData = -1;
      cindex = new int[sectionSchema.getNComponents()];
      for (int sectionSchemaCompIndex = 0; sectionSchemaCompIndex < sectionSchema.getNComponents(); sectionSchemaCompIndex++)
      {
         int k = sectionSchema.getComponent(sectionSchemaCompIndex).getnData();
         if (nData == -1)
            nData = k; 
         if (nData != k)
         {
            System.out.println("all data items in a file section must have the same number of items");
            return;
         }
         if (sectionSchema.getComponent(sectionSchemaCompIndex) instanceof IntArrayIOSchema)
         {
            IntArrayIOSchema iArr = (IntArrayIOSchema)sectionSchema.getComponent(sectionSchemaCompIndex);
            cindex[sectionSchemaCompIndex] = nItems;
            if (sectionSchema.isSingleComponent()) //everything will be read with a single command
               nItems = 1;
            else if (iArr.getCoord() >= 0)              // single coordinate
               nItems += 1;
            else
               nItems += iArr.getVeclen();
         }
         else if (sectionSchema.getComponent(sectionSchemaCompIndex) instanceof BooleanArrayIOSchema)
         {
            BooleanArrayIOSchema bArr = (BooleanArrayIOSchema)sectionSchema.getComponent(sectionSchemaCompIndex);
            if (sectionSchema.isSingleComponent()) //everything will be read with a single command
               nItems = 1;
            else if (bArr.getCoord() >= 0)              // single coordinate
               nItems += 1;
            else
               nItems += bArr.getVeclen();
         }
         else
         {
            ComponentIOSchema comp = (ComponentIOSchema)sectionSchema.getComponent(sectionSchemaCompIndex);
            cindex[sectionSchemaCompIndex] = nItems;
            if (sectionSchema.isSingleComponent()) //everything will be read with a single command
               nItems = 1;
                 else if (comp.getCoord() >= 0)              // single coordinate
               nItems += 1;
            else
            {
               if (comp.getComponent() < outField.getNData())
                  nItems += outField.getData(comp.getComponent()).getVeclen();
               else if (comp.getCmpName().startsWith("coord"))
                  nItems += outField.getNSpace();
               else
                  nItems += 1;                     // whole component read
            }
         }
      }
      this.time = time;
      this.outField = outField;
      schemas =   new DataElementIOSchema[nItems];
      comps =     new int[nItems];
      coords =    new int[nItems];
      offsets =   new int[nItems];        
      offsets1 =  new int[nItems];
      types =     new int[nItems];
      vlens =     new int[nItems];
      ind =       new int[nItems];
      boolArrs =  new boolean[nItems][];
      byteArrs =  new byte[nItems][];
      shortArrs = new short[nItems][];
      intArrs =   new int[nItems][];
      floatArrs = new float[nItems][];
      dblArrs =   new double[nItems][];
      strArrs =   new String[nItems][];
      for (int sectionSchemaCompIndex = 0, modelComponentIndex = 0; 
               sectionSchemaCompIndex < sectionSchema.getNComponents(); 
               sectionSchemaCompIndex++)
      {
         DataElementIOSchema deSchema = sectionSchema.getComponent(sectionSchemaCompIndex);
         int cmp = -1;
         int coord = deSchema.getCoord();
         int nCoords = 1;
         int vlen = deSchema.getVeclen();
         if (coord == -1)
            nCoords = vlen;                            // all coordinates read in proper order 
         schemas[modelComponentIndex] = deSchema;
         if (sectionSchema.isSingleComponent())
            nCoords = 1;
         if (deSchema instanceof IntArrayIOSchema)
            intArrs[modelComponentIndex] = ((IntArrayIOSchema)deSchema).getIntArray();
         else if (deSchema instanceof BooleanArrayIOSchema)
            boolArrs[modelComponentIndex] = new boolean[nData * vlen];
         else 
         {
            ComponentIOSchema compIOSchema = (ComponentIOSchema)deSchema;
            cmp = compIOSchema.getComponent();
            // allocating / getting data arrays
            if (sectionSchema.getTile() != null)
            {
               int tileSize = nData = sectionSchema.getTileSize();
               switch (compIOSchema.getType())
               {
               case DataArray.FIELD_DATA_BOOLEAN:
                  boolArrs[modelComponentIndex] = new boolean[tileSize * nCoords];
                  break;
               case DataArray.FIELD_DATA_BYTE:
                  byteArrs[modelComponentIndex] = new byte[tileSize * nCoords];
                  break;
               case DataArray.FIELD_DATA_SHORT:
                  shortArrs[modelComponentIndex] = new short[tileSize * nCoords];
                  break;
               case DataArray.FIELD_DATA_INT:
                  intArrs[modelComponentIndex] = new int[tileSize * nCoords];
                  break;
               case DataArray.FIELD_DATA_FLOAT:
                  floatArrs[modelComponentIndex] = new float[tileSize * nCoords];
                  break;
               case DataArray.FIELD_DATA_DOUBLE:
                  dblArrs[modelComponentIndex] = new double[tileSize * nCoords];
                  break;
               case DataArray.FIELD_DATA_STRING:
                  strArrs[modelComponentIndex] = new String[tileSize * nCoords];
                  break;
               }
            } else
            {
               DataContainer container = compIOSchema.getDataset();
               switch (compIOSchema.getType())
               {
               case DataArray.FIELD_DATA_BOOLEAN:
                  if (coord != -1)
                     boolArrs[modelComponentIndex] = new boolean[nData];
                  else
                     boolArrs[modelComponentIndex] = new boolean[vlen * nData];
                  break;
               case DataArray.FIELD_DATA_BYTE:
                  if (coord != -1)
                     byteArrs[modelComponentIndex] = new byte[nData];
                  else if (cmp < container.getNData())
                     byteArrs[modelComponentIndex] = ((ByteDataArray) container.getData(cmp)).produceBData(time);
                  break;
               case DataArray.FIELD_DATA_SHORT:
                  if (coord != -1)
                     shortArrs[modelComponentIndex] = new short[nData];
                  else if (cmp < container.getNData())
                     shortArrs[modelComponentIndex] = ((ShortDataArray) container.getData(cmp)).produceSData(time);
                  break;
               case DataArray.FIELD_DATA_INT:
                  if (coord != -1)
                     intArrs[modelComponentIndex] = new int[nData];
                  else if (cmp < container.getNData())
                     intArrs[modelComponentIndex] = ((IntDataArray) container.getData(cmp)).produceIData(time);
                  break;
               case DataArray.FIELD_DATA_FLOAT:
                  if (coord != -1)
                     floatArrs[modelComponentIndex] = new float[nData];
                  else if (cmp < container.getNData())
                     floatArrs[modelComponentIndex] = ((FloatDataArray) container.getData(cmp)).produceFData(time);
                  else if (cmp == outField.getNData())
                     floatArrs[modelComponentIndex] = outField.getNewTimestepCoords();
                  break;
               case DataArray.FIELD_DATA_DOUBLE:
                  if (coord != -1)
                     dblArrs[modelComponentIndex] = new double[outField.getNNodes()];
                  else if (cmp < outField.getNData())
                     dblArrs[modelComponentIndex] = ((DoubleDataArray) container.getData(cmp)).produceDData(time);
                  break;
               case DataArray.FIELD_DATA_STRING:
                  if (coord != -1)
                     strArrs[modelComponentIndex] = new String[outField.getNNodes()];
                  else if (cmp < outField.getNData())
                     strArrs[modelComponentIndex] = ((StringDataArray) container.getData(cmp)).produceStringData(time);
                  break;
               }
            }      
         }   
         if (sectionSchema.isSingleComponent())
            nCoords = 1;
         for (int l = 0; l < nCoords; l++)
         {
            schemas[modelComponentIndex + l]   = schemas[modelComponentIndex];
            comps[modelComponentIndex + l]     = cmp;
            types[modelComponentIndex + l]     = deSchema.getType();
            vlens[modelComponentIndex + l]     = nCoords;
            boolArrs[modelComponentIndex + l]  = boolArrs[modelComponentIndex];
            byteArrs[modelComponentIndex + l]  = byteArrs[modelComponentIndex];
            shortArrs[modelComponentIndex + l] = shortArrs[modelComponentIndex];
            intArrs[modelComponentIndex + l]   = intArrs[modelComponentIndex];
            floatArrs[modelComponentIndex + l] = floatArrs[modelComponentIndex];
            dblArrs[modelComponentIndex+l]     = dblArrs[modelComponentIndex];
            strArrs[modelComponentIndex+l]     = strArrs[modelComponentIndex];
            offsets[modelComponentIndex+l]     = deSchema.getOffsetFrom();
            offsets1[modelComponentIndex+l]    = deSchema.getOffsetTo();
            ind[modelComponentIndex + l]       = l;
            if (coord == -1 && !sectionSchema.isSingleComponent())
               coords[modelComponentIndex + l] = l;
            else
               coords[modelComponentIndex + l] = coord;
         }   
         modelComponentIndex += nCoords;
      }
   }

   SectionModel(FileSectionSchema sectionSchema, Field outField)
   {
      this(sectionSchema, outField, 0);
   }
}
