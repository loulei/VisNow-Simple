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
exception statement from your version. */

package pl.edu.icm.visnow.lib.basic.readers.ReadVisNowField;

import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.datasets.dataarrays.*;
import pl.edu.icm.visnow.lib.utils.io.ComponentIOSchema;
import pl.edu.icm.visnow.lib.utils.io.FileSectionSchema;

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
   
   RegularField outField;
   FileSectionSchema sectionSchema;
   float time = 0;
   int nNodes = 1;  
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
   byte[][] byteArrs;
   short[][] shortArrs;
   int[][] intArrs;
   float[][] floatArrs;
   double[][] dblArrs;
   String[][] strArrs;
   int[] ind;

   SectionModel(FileSectionSchema sectionSchema, RegularField outField, float time)
   {
      this.sectionSchema = sectionSchema;
      nItems = 0;
      nNodes = outField.getNNodes();
      cindex = new int[sectionSchema.getNComponents()];
      for (int sectionSchemaCompIndex = 0; sectionSchemaCompIndex < sectionSchema.getNComponents(); sectionSchemaCompIndex++)
      {
         ComponentIOSchema comp = sectionSchema.getComponent(sectionSchemaCompIndex);
         cindex[sectionSchemaCompIndex] = nItems;
         if (sectionSchema.isSingleComponent()) //everything will be read with a single command
         {
            nItems = 1;
            break;
         }
         if (comp.getCoord() >= 0)              // single coordinate
            nItems += 1;
         else
         {
            if (comp.getComponent() < outField.getNData())
               nItems += outField.getData(comp.getComponent()).getVeclen();
            else if (comp.getComponent() == outField.getNData())
               nItems += outField.getNSpace();
            else
               nItems += 1;                     // whole component read
         }
      }
      this.time = time;
      comps =     new int[nItems];
      coords =    new int[nItems];
      offsets =   new int[nItems];        
      offsets1 =  new int[nItems];
      types =     new int[nItems];
      vlens =     new int[nItems];
      ind =       new int[nItems];
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
         ComponentIOSchema comp = sectionSchema.getComponent(sectionSchemaCompIndex);
         int cmp = comp.getComponent();
         int coord = comp.getCoord();
         int nCoords = 1;
         int vlen = 1;
         if (cmp < outField.getNData())
            vlen = outField.getData(cmp).getVeclen();  // veclen of standard data componnet
         else if (cmp == outField.getNData())
            vlen = outField.getNSpace();               // veclen of coord arrays
         if (coord == -1)
            nCoords = vlen;                            // all coordinates read in proper order 
         // allocating / getting data arrays
         if (sectionSchema.getTile() != null)
         {
            int tileSize = sectionSchema.getTileSize();
            switch (comp.getType())
            {
            case DataArray.FIELD_DATA_BOOLEAN:
               byteArrs[modelComponentIndex] = new byte[tileSize * nCoords];
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
            switch (comp.getType())
            {
            case DataArray.FIELD_DATA_BOOLEAN:
               if (coord != -1)
                  byteArrs[modelComponentIndex] = new byte[outField.getNNodes()];
               else
                  byteArrs[modelComponentIndex] = new byte[vlen * outField.getNNodes()];
               break;
            case DataArray.FIELD_DATA_BYTE:
               if (coord != -1)
                  byteArrs[modelComponentIndex] = new byte[outField.getNNodes()];
               else if (cmp < outField.getNData())
                  byteArrs[modelComponentIndex] = ((ByteDataArray) outField.getData(cmp)).produceBData(time);
               break;
            case DataArray.FIELD_DATA_SHORT:
               if (coord != -1)
                  shortArrs[modelComponentIndex] = new short[outField.getNNodes()];
               else if (cmp < outField.getNData())
                  shortArrs[modelComponentIndex] = ((ShortDataArray) outField.getData(cmp)).produceSData(time);
               break;
            case DataArray.FIELD_DATA_INT:
               if (coord != -1)
                  intArrs[modelComponentIndex] = new int[outField.getNNodes()];
               else if (cmp < outField.getNData())
                  intArrs[modelComponentIndex] = ((IntDataArray) outField.getData(cmp)).produceIData(time);
               break;
            case DataArray.FIELD_DATA_FLOAT:
               if (coord != -1)
                  floatArrs[modelComponentIndex] = new float[outField.getNNodes()];
               else if (cmp < outField.getNData())
                  floatArrs[modelComponentIndex] = ((FloatDataArray) outField.getData(cmp)).produceFData(time);
               else if (cmp == outField.getNData())
                  floatArrs[modelComponentIndex] = outField.getNewTimestepCoords();
               break;
            case DataArray.FIELD_DATA_DOUBLE:
               if (coord != -1)
                  dblArrs[modelComponentIndex] = new double[outField.getNNodes()];
               else if (cmp < outField.getNData())
                  dblArrs[modelComponentIndex] = ((DoubleDataArray) outField.getData(cmp)).produceDData(time);
               break;
            case DataArray.FIELD_DATA_STRING:
               if (coord != -1)
                  strArrs[modelComponentIndex] = new String[outField.getNNodes()];
               else if (cmp < outField.getNData())
                  strArrs[modelComponentIndex] = ((StringDataArray) outField.getData(cmp)).produceStringData(time);
               break;
            }
         }         
         if (sectionSchema.isSingleComponent())
            nCoords = 1;
         for (int l = 0; l < nCoords; l++)
         {
            comps[modelComponentIndex + l]     = cmp;
            types[modelComponentIndex + l]     = comp.getType();
            vlens[modelComponentIndex + l]     = nCoords;
            byteArrs[modelComponentIndex + l]  = byteArrs[modelComponentIndex];
            shortArrs[modelComponentIndex + l] = shortArrs[modelComponentIndex];
            intArrs[modelComponentIndex + l]   = intArrs[modelComponentIndex];
            floatArrs[modelComponentIndex + l] = floatArrs[modelComponentIndex];
            dblArrs[modelComponentIndex+l]     = dblArrs[modelComponentIndex];
            strArrs[modelComponentIndex+l]     = strArrs[modelComponentIndex];
            
            offsets[modelComponentIndex+l]       = comp.getOffsetFrom() + l;
//            offsets[modelComponentIndex+l]       = comp.getOffsetFrom() + l * typeLengths[comp.getType()];
            offsets1[modelComponentIndex+l]      = comp.getOffsetTo() + 1 * typeLengths[comp.getType()];
            ind[modelComponentIndex + l]       = l;
            if (coord == -1 && !sectionSchema.isSingleComponent())
               coords[modelComponentIndex + l] = l;
            else
               coords[modelComponentIndex + l] = coord;
         }   
         modelComponentIndex += nCoords;
      }
   }

   SectionModel(FileSectionSchema sectionSchema, RegularField outField)
   {
      this(sectionSchema, outField, 0);
   }
}
