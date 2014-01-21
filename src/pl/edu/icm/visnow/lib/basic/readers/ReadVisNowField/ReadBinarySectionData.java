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
 exception statement from your version. */
//</editor-fold>
package pl.edu.icm.visnow.lib.basic.readers.ReadVisNowField;

import java.io.IOException;
import javax.imageio.stream.ImageInputStream;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import static pl.edu.icm.visnow.lib.basic.readers.ReadVisNowField.SectionModel.typeLengths;

import pl.edu.icm.visnow.lib.basic.readers.ReadVisNowField.utils.FileSectionSchema;

/**
 *
 * @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class ReadBinarySectionData
{

   public static int readSectionData(SectionModel model, ImageInputStream inStream, String filePath)
   {
      int nComps = model.nItems;
      int nNodes = model.nData;
      int[] offsets = model.offsets;
      int[] types = model.types;
      int[] vlens = model.vlens;
      int[] ind = model.ind;
      boolean[][] boolArrs = model.boolArrs;
      byte[][] byteArrs = model.byteArrs;
      short[][] shortArrs = model.shortArrs;
      int[][] intArrs = model.intArrs;
      float[][] floatArrs = model.floatArrs;
      double[][] dblArrs = model.dblArrs;
      FileSectionSchema schema = model.sectionSchema;
      try
      {
         if (schema.getComponents().isEmpty())
            return 0;
         if (schema.getNComponents() == 1)
         {
            switch (types[0])
            {
               case DataArray.FIELD_DATA_BOOLEAN:
                  byte[] b = new byte[boolArrs[0].length];
                  inStream.readFully(b);
                  for (int i = 0; i < b.length; i++)
                     boolArrs[0][i] = b[i] != 0;
                  break;
               case DataArray.FIELD_DATA_BYTE:
                  inStream.readFully(byteArrs[0]);
                  break;
               case DataArray.FIELD_DATA_SHORT:
                  inStream.readFully(shortArrs[0], 0, shortArrs[0].length);
                  break;
               case DataArray.FIELD_DATA_INT:
                  inStream.readFully(intArrs[0], 0, intArrs[0].length);
                  break;
               case DataArray.FIELD_DATA_FLOAT:
                  inStream.readFully(floatArrs[0], 0, floatArrs[0].length);
                  break;
               case DataArray.FIELD_DATA_DOUBLE:
                  inStream.readFully(dblArrs[0], 0, dblArrs[0].length);
                  break;
            }
            return 0;
         }
         else
            for (int k = 0; k < nNodes; k++)
            {
               int cPos = 0;
               for (int l = 0; l < nComps; l++)
               {
                  int ll = offsets[l] - cPos;
                  cPos = offsets[l] + typeLengths[types[l]];
                  inStream.skipBytes(ll);
                  switch (types[l])
                  {
                     case DataArray.FIELD_DATA_BOOLEAN:
                        boolArrs[l][ind[l]] = inStream.readByte() != 0;
                        ind[l] += vlens[l];
                        break;
                     case DataArray.FIELD_DATA_BYTE:
                        byteArrs[l][ind[l]] = inStream.readByte();
                        ind[l] += vlens[l];
                        break;
                     case DataArray.FIELD_DATA_SHORT:
                        shortArrs[l][ind[l]] = inStream.readShort();
                        ind[l] += vlens[l];
                        break;
                     case DataArray.FIELD_DATA_INT:
                        intArrs[l][ind[l]] = inStream.readInt();
                        ind[l] += vlens[l];
                        break;
                     case DataArray.FIELD_DATA_FLOAT:
                        floatArrs[l][ind[l]] = inStream.readFloat();
                        ind[l] += vlens[l];
                        break;
                     case DataArray.FIELD_DATA_DOUBLE:
                        dblArrs[l][ind[l]] = inStream.readDouble();
                        ind[l] += vlens[l];
                        break;
                  }
               }
            }
      } catch (IOException e)
      {
         System.err.println("ERROR: inStream data file");
         e.printStackTrace();
         return 1;
      }
      return 0;
   }
}
