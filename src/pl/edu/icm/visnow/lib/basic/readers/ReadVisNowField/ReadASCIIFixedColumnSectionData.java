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

import java.io.IOException;
import java.io.LineNumberReader;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.lib.utils.io.FileIOUtils;
import pl.edu.icm.visnow.lib.utils.io.FileSectionSchema;

/**
 *
 * @author know
 */
public class ReadASCIIFixedColumnSectionData 
{
   public static int readSectionData(SectionModel model, LineNumberReader reader, String filePath)
   {
      int nComps               = model.nItems;
      int nNodes               = model.nNodes;
      int[] offsets            = model.offsets;
      int[] offsets1           = model.offsets1;
      int[] types              = model.types;
      int[] vlens              = model.vlens;
      int[] ind                = model.ind;
      byte[][] byteArrs        = model.byteArrs;
      short[][] shortArrs      = model.shortArrs;
      int[][]   intArrs        = model.intArrs;
      float[][] floatArrs      = model.floatArrs;
      double[][] dblArrs       = model.dblArrs;
      FileSectionSchema schema = model.sectionSchema;
      try
      {
         String line = "";
         if (schema.getComponents().isEmpty())
            return 0;
         for (int k = 0; k < nNodes; k++)
         {
            line = FileIOUtils.nextLine(reader, false, false);
            if (line == null)
               return 1;
            line = line.trim();
            for (int l = 0; l < nComps; l++)
            {
               String item = line.substring(offsets[l], offsets1[l]);
               switch (types[l])
               {
                  case DataArray.FIELD_DATA_BOOLEAN:
                     byteArrs[l][ind[l]] = (byte)((item.startsWith("1") || item.startsWith("t")) ? 1 : 0);
                     ind[l] += vlens[l];
                     break;
                  case DataArray.FIELD_DATA_BYTE:
                     byteArrs[l][ind[l]] = (byte) (0xff & Integer.parseInt(item));
                     ind[l] += vlens[l];
                     break;
                  case DataArray.FIELD_DATA_SHORT:
                     shortArrs[l][ind[l]] = Short.parseShort(item);
                     ind[l] += vlens[l];
                     break;
                  case DataArray.FIELD_DATA_INT:
                     intArrs[l][ind[l]] = Integer.parseInt(item);
                     ind[l] += vlens[l];
                     break;
                  case DataArray.FIELD_DATA_FLOAT:
                     floatArrs[l][ind[l]] = Float.parseFloat(item);
                     ind[l] += vlens[l];
                     break;
                  case DataArray.FIELD_DATA_DOUBLE:
                     dblArrs[l][ind[l]] = Double.parseDouble(item);
                     ind[l] += vlens[l];
                     break;
               }
            }
         }
         return 0;
      } catch (ArrayIndexOutOfBoundsException e)
      {
         outputError("error in data file: line too short " + filePath, filePath, reader.getLineNumber(), null);
         return 2;
      } catch (NumberFormatException e)
      {
         outputError("error in data file: bad number format " + filePath, filePath, reader.getLineNumber(), null);
         return 2;
      } catch (IOException e)
      {
         outputError("error in data file " + filePath, filePath, reader.getLineNumber(), null);
         return 2;
      }
      
   }
   
   static void outputError(String text, String fname, int lineNumber, Exception e)
   {
         System.err.println("ERROR: " + text + "; in function " + fname + " line " + lineNumber);
         //e.printStackTrace();   
   }

} 