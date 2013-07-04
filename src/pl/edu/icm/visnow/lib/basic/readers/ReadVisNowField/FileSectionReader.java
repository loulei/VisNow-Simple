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

import java.io.LineNumberReader;
import java.util.Scanner;
import javax.imageio.stream.ImageInputStream;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.datasets.dataarrays.BooleanDataArray;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.datasets.dataarrays.StringDataArray;
import pl.edu.icm.visnow.lib.utils.field.Tile;
import pl.edu.icm.visnow.lib.utils.io.DataFileSchema;
import pl.edu.icm.visnow.lib.utils.io.FileSectionSchema;
import pl.edu.icm.visnow.lib.utils.io.RegularFieldIOSchema;

/**
 *
 * @author know
 */
public class FileSectionReader
{

   protected RegularField outField = null;
   protected FileSectionSchema sectionSchema = null;
   protected int fileType;
   protected float time = 0;
   protected LineNumberReader reader = null;
   protected ImageInputStream inStream = null;
   protected Scanner scanner = null;
   protected SectionModel model = null;
   protected int nComps;
   protected byte[][] byteArrs;
   protected short[][] shortArrs;
   protected int[][] intArrs;
   protected float[][] floatArrs;
   protected double[][] dblArrs;
   protected String[][] strArrs;
   protected int[][] tileBds;
   protected String filePath;
   protected boolean streamRead = false;
   protected boolean continuingRead = false;

   public FileSectionReader(RegularField outField, RegularFieldIOSchema schema, LineNumberReader reader, ImageInputStream inStream, Scanner scanner,int fileType)
   {
      this.outField = outField;
      this.reader   = reader;
      this.inStream = inStream;
      this.scanner  = scanner;
      this.fileType = fileType;
   }

   public void setSectionSchema(FileSectionSchema sectionSchema, float time)
   {
      this.sectionSchema = sectionSchema;
      model = new SectionModel(sectionSchema, outField, time);
      nComps = model.nItems;
      byteArrs = model.byteArrs;
      shortArrs = model.shortArrs;
      intArrs = model.intArrs;
      floatArrs = model.floatArrs;
      dblArrs = model.dblArrs;
      strArrs = model.strArrs;
      tileBds = sectionSchema.getTile();
      this.time = time;
   }

   protected void outputError(String text, String fname, int lineNumber, Exception e)
   {
      System.err.println("ERROR: " + text + "; in function " + fname + " line " + lineNumber);
      //e.printStackTrace();   
   }

   public void setFilePath(String filePath)
   {
      this.filePath = filePath;
   }

   public void setContinuingRead(boolean continuingRead)
   {
      this.continuingRead = continuingRead;
   }

   public void setTime(float time)
   {
      this.time = time;
   }

   public void setStreamRead(boolean streamRead)
   {
      this.streamRead = streamRead;
   }

   public int readSection()
   {
      switch (fileType)
      {
      case DataFileSchema.BIG_ENDIAN:
      case DataFileSchema.LITTLE_ENDIAN:
         ReadBinarySectionData.readSectionData(model, inStream, filePath);
         break;
      case DataFileSchema.ASCII:
         ReadASCIIContinuousSectionData.readSectionData(model, scanner, filePath);
         break;
      case DataFileSchema.COLUMN:
         ReadASCIIColumnSectionData.readSectionData(model, reader, filePath);
         break;
      case DataFileSchema.FIXED_COLUMN:
         ReadASCIIFixedColumnSectionData.readSectionData(model, reader, filePath);
      }
      if (sectionSchema.getComponents().isEmpty())
         return 0;
      if (tileBds != null)
      {
         for (int iComp = 0; iComp < model.nItems; iComp++)
         {
            int cmp = model.comps[iComp];
            int coord = model.coords[iComp];
            if (cmp < outField.getNData())
            {
               DataArray ar = outField.getData(cmp);
               switch (ar.getType())
               {
               case DataArray.FIELD_DATA_BYTE:
                  Tile.putTile(tileBds, outField.getDims(),
                          ar.produceBData(time), byteArrs[iComp],
                          ar.getVeclen(), coord);
                  break;
               case DataArray.FIELD_DATA_SHORT:
                  Tile.putTile(tileBds, outField.getDims(),
                          ar.produceSData(time), shortArrs[iComp],
                          ar.getVeclen(), coord);
                  break;
               case DataArray.FIELD_DATA_INT:
                  Tile.putTile(tileBds, outField.getDims(),
                          ar.produceIData(time), intArrs[iComp],
                          ar.getVeclen(), coord);
                  break;
               case DataArray.FIELD_DATA_FLOAT:
                  Tile.putTile(tileBds, outField.getDims(),
                          ar.produceFData(time), floatArrs[iComp],
                          ar.getVeclen(), coord);
                  break;
               case DataArray.FIELD_DATA_DOUBLE:
                  Tile.putTile(tileBds, outField.getDims(),
                          ar.produceDData(time), dblArrs[iComp],
                          ar.getVeclen(), coord);
                  break;
               default:
                  break;
               }
            } else if (cmp == outField.getNData())
            {
               Tile.putTile(tileBds, outField.getDims(),
                       outField.produceCoords(time), floatArrs[iComp],
                       outField.getNSpace(), coord);
            } else if (cmp == outField.getNData() + 1)
            {
               Tile.putTile(tileBds, outField.getDims(),
                       outField.produceMask(time), byteArrs[iComp],
                       1, coord);
            }
         }
      } else
      {
         for (int iComp = 0; iComp < model.nItems; iComp++)
         {
            int cmp = model.comps[iComp];
            int coord = model.coords[iComp];
            if (coord >= 0)
            {
               if (cmp < outField.getNData())
               {
                  DataArray ar = outField.getData(cmp);
                  int vlen = ar.getVeclen();
                  switch (ar.getType())
                  {
                  case DataArray.FIELD_DATA_BOOLEAN:
                     boolean[] blTarget = ((BooleanDataArray) ar).produceBoolData(time);
                     byte[] blSource = byteArrs[iComp];
                     for (int i = 0, j = coord; j < blTarget.length; i++, j += vlen)
                        blTarget[j] = blSource[i] != 0;
                     break;
                  case DataArray.FIELD_DATA_BYTE:
                     byte[] bTarget = ar.produceBData(time);
                     byte[] bSource = byteArrs[iComp];
                     if (bSource != bTarget)
                        for (int i = 0, j = coord; j < bTarget.length; i++, j += vlen)
                           bTarget[j] = bSource[i];
                     break;
                  case DataArray.FIELD_DATA_SHORT:
                     short[] sTarget = ar.produceSData(time);
                     short[] sSource = shortArrs[iComp];
                     if (sSource != sTarget)
                        for (int i = 0, j = coord; j < sTarget.length; i++, j += vlen)
                           sTarget[j] = sSource[i];
                     break;
                  case DataArray.FIELD_DATA_INT:
                     int[] iTarget = ar.produceIData(time);
                     int[] iSource = intArrs[iComp];
                     if (iSource != iTarget)
                        for (int i = 0, j = coord; j < iTarget.length; i++, j += vlen)
                           iTarget[j] = iSource[i];
                     break;
                  case DataArray.FIELD_DATA_FLOAT:
                     float[] fTarget = ar.produceFData(time);
                     float[] fSource = floatArrs[iComp];
                     if (fSource != fTarget)
                        for (int i = 0, j = coord; j < fTarget.length; i++, j += vlen)
                           fTarget[j] = fSource[i];
                     break;
                  case DataArray.FIELD_DATA_DOUBLE:
                     double[] dTarget = ar.produceDData(time);
                     double[] dSource = dblArrs[iComp];
                     if (dSource != dTarget)
                     for (int i = 0, j = coord; j < dTarget.length; i++, j += vlen)
                        dTarget[j] = dSource[i];
                     break;
                  case DataArray.FIELD_DATA_STRING:
                     String[] stTarget =  ((StringDataArray) ar).produceStringData(time);
                     String[] stSource = strArrs[iComp];
                     if (stSource != stTarget)
                     for (int i = 0, j = coord; j < stTarget.length; i++, j += vlen)
                        stTarget[j] = stSource[i];
                     break;
                  default:
                     break;
                  }
               } else if (cmp == outField.getNData())
               {
                  float[] fTarget = outField.produceCoords(time);
                  float[] fSource = floatArrs[iComp];
                  int vlen = outField.getNSpace();
                  for (int i = 0, j = coord; i < fSource.length; i++, j += vlen)
                     fTarget[j] = fSource[i];
               } else if (cmp == outField.getNData() + 1)
               {
                  boolean[] bTarget = outField.produceMask(time);
                  byte[] bSource = byteArrs[iComp];
                  for (int i = 0; i < bSource.length; i++)
                     bTarget[i] = bSource[i] != 0;
               }
            } else if (cmp == outField.getNData() + 1)
            {
               boolean[] bTarget = outField.produceMask(time);
               byte[] bSource = byteArrs[iComp];
               for (int i = 0; i < bSource.length; i++)
                  bTarget[i] = bSource[i] != 0;
            } else if (cmp < outField.getNData() && outField.getData(cmp).getType() == DataArray.FIELD_DATA_BOOLEAN)
            {
               boolean[] blTarget = ((BooleanDataArray) outField.getData(cmp)).produceBoolData(time);
               byte[] blSource = byteArrs[iComp];
               for (int i = 0, j = coord; i < blSource.length; i++)
                  blTarget[i] = blSource[i] != 0;
            }

         }
      }
      return 0;
   }
}
