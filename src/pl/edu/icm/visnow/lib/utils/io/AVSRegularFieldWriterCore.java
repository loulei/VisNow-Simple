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

package pl.edu.icm.visnow.lib.utils.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import javax.imageio.stream.FileImageOutputStream;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.lib.basic.writers.FieldWriter.Params;

/**
 *
 * @author know
 */
public class AVSRegularFieldWriterCore extends FieldWriterCore
{
   
   public AVSRegularFieldWriterCore(RegularField inField, Params params)
   {
      super(inField, params);
   }
   
   private void printASCIIColumns(String genFileName)
   {
      printASCIIColumns(genFileName, inField);
   }
  
   private static void printASCIIColumns(String genFileName, RegularField inField)
   {
      try
      {
      PrintWriter outA = new PrintWriter(new FileOutputStream(genFileName + ".data"));
      for (int i = 0; i < inField.getNNodes(); i++)
      {
         for (int j = 0; j < inField.getNData(); j++)
         {
            DataArray da = inField.getData(j);
            int vl = da.getVeclen();
            switch (da.getType())
            {
               case DataArray.FIELD_DATA_BYTE:
                  for (int k = 0; k < vl; k++)
                     outA.printf("%3d ", da.getBData()[i * vl + k] & 0xff);
                  break;
               case DataArray.FIELD_DATA_SHORT:
                  for (int k = 0; k < vl; k++)
                     outA.printf("%3d ", da.getSData()[i * vl + k]);
                  break;
               case DataArray.FIELD_DATA_INT:
                  for (int k = 0; k < vl; k++)
                     outA.printf("%6d ", da.getIData()[i * vl + k]);
                  break;
               case DataArray.FIELD_DATA_FLOAT:
                  for (int k = 0; k < vl; k++)
                     outA.printf("%9f ", da.getFData()[i * vl + k]);
                  break;
               case DataArray.FIELD_DATA_DOUBLE:
                  for (int k = 0; k < vl; k++)
                     outA.printf("%12f ", da.getDData()[i * vl + k]);
                  break;
            }
         }
         outA.println();
      }
      outA.close();
      } catch (Exception e)
      {
         System.out.println("could not write file " + genFileName + ".data");
      }
   }
   
   private static void printAVSLabelsUnits(PrintWriter header, RegularField inField)
   {
      int scalars = 0;
      header.print("label = ");
      for (int i = 0; i < inField.getNData(); i++)
      {
         int vn = inField.getData(i).getVeclen();
         scalars += vn;
         if (vn == 1)
            header.print(inField.getData(i).getName().replace(' ', '_') + " ");
         else
            for (int j = 0; j < vn; j++)
               header.print(inField.getData(i).getName().replace(' ', '_') + j + " ");
      }
      header.println();
      header.print("unit = ");
      for (int i = 0; i < inField.getNData(); i++)
      {
         if (inField.getData(i).getVeclen() == 1)
            header.print(inField.getData(i).getUnit().replace(' ', '_') + "_ ");
         else
            for (int j = 0; j < inField.getData(i).getVeclen(); j++)
               header.print(inField.getData(i).getUnit().replace(' ', '_') + "_ ");
      }
      header.println();
   }
     
      
   public void writeField()
   {
      writeField(inField, params.getFileName(), params.isAscii(), params.isSingleFile());
   }
   
   public static void writeField(RegularField inField, String fileName, boolean ascii, boolean singleFile)
   {
      String outFileName = fileName;
      String genFileName = fileName;
      boolean allByte = false;
      DataArray da;
      byte[] bData;
      float[] fData;
      int scalars = 0;
      FileImageOutputStream out;
      PrintWriter outA;

      if (outFileName.endsWith(".fld"))
         genFileName = outFileName.substring(0, outFileName.lastIndexOf(".fld"));
      else
         outFileName = genFileName + ".fld";
      
      try
      {
         PrintWriter header = new PrintWriter(new FileOutputStream(outFileName));
         header.println("# AVS field file");
         header.println("ndim = " + inField.getDims().length);
         for (int i = 0; i < inField.getDims().length; i++)
         {
            header.println("dim" + (i + 1) + " = " + inField.getDims()[i]);
         }
         header.println("nspace = " + inField.getNSpace());
         allByte = true;
         scalars = 0;
         for (int i = 0; i < inField.getNData(); i++)
         {
            scalars += inField.getData(i).getVeclen();
            if (inField.getData(i).getType() != DataArray.FIELD_DATA_BYTE)
               allByte = false;
         }
         header.println("veclen = " + scalars);
         if (allByte)
            header.println("data = byte");
         else
            header.println("data = xdr_float");
         if (inField.getCoords() == null)
         {
            header.println("field = uniform");
            header.print("min_ext = ");
            for (int i = 0; i < inField.getNSpace(); i++)
               header.print("" + inField.getExtents()[0][i] + " ");
            header.println();
            header.print("max_ext = ");
            for (int i = 0; i < inField.getNSpace(); i++)
               header.print("" + inField.getExtents()[1][i] + " ");
            header.println();
            printAVSLabelsUnits(header, inField);
         } else
         {
            header.println("field = irregular");
            printAVSLabelsUnits(header, inField);
            if (ascii)
            {
               for (int i = 0; i < inField.getNSpace(); i++)
               {
                  header.print("coord " + (i + 1) + " file=" + genFileName + ".coord filetype=ASCII ");
                  header.print("stride=" + inField.getNSpace());
                  if (i > 0)
                     header.print(" offset=" + i);
                  header.println();
               }
               outA = new PrintWriter(new FileOutputStream(genFileName + ".coord"));
               float[] coord = inField.getCoords();
               for (int i = 0; i < coord.length; i += inField.getNSpace())
               {
                  for (int j = 0; j < inField.getNSpace(); j++)
                     outA.printf("%7f ", coord[i + j]);
                  outA.println();
               }
               outA.close();
            } else
            {
               for (int i = 0; i < inField.getNSpace(); i++)
               {
                  header.print("coord " + (i + 1) + " file=" + genFileName + ".coord filetype=binary ");
                  header.print("stride=" + inField.getNSpace());
                  if (i > 0)
                     header.print(" skip=" + (4 * i));
                  header.println();
               }
               float[] coord = inField.getCoords();
               out = new FileImageOutputStream(new File(genFileName + ".coord"));
               out.writeFloats(coord, 0, coord.length);
               out.close();
            }
         }
         if (ascii)
         {
            for (int i = 0, k = 0; i < inField.getNData(); i++)
            {
               da = inField.getData(i);
               for (int j = 0; j < da.getVeclen(); j++, k++)
               {
                  if (k > 0)
                     header.println("variable " + (k + 1) + " file=" + genFileName + ".data filetype=ASCII stride=" + scalars + " offset=" + k);
                  else
                     header.println("variable " + (k + 1) + " file=" + genFileName + ".data filetype=ASCII stride=" + scalars);
               }
            }
            printASCIIColumns(genFileName, inField);
         } else
         {
            int dSize = 4;
            if (allByte)
               dSize = 1;
            if (singleFile)
            {
               out = new FileImageOutputStream(new File(genFileName + ".data"));
               for (int i = 0, k = 0; i < inField.getNData(); i++)
               {
                  da = inField.getData(i);
                  for (int j = 0; j < da.getVeclen(); j++, k++)
                     header.println("variable " + (k + 1)
                             + " file=" + genFileName + ".data filetype=binary skip ="
                             + (out.getStreamPosition() + (j * dSize)) + " stride=" + da.getVeclen());
                  if (allByte)
                  {
                     bData = da.getBData();
                     out.write(bData, 0, bData.length);
                  } else
                  {
                     fData = da.getFData();
                     out.writeFloats(fData, 0, fData.length);
                  }
               }
               out.close();
            } else
            {
               for (int i = 0, k = 0; i < inField.getNData(); i++)
               {
                  da = inField.getData(i);
                  String outFname = genFileName + "_" + da.getName().replace(' ', '_') + ".dat";
                  for (int j = 0; j < da.getVeclen(); j++, k++)
                  {
                     header.print("variable " + (k + 1) + " file=" + outFname + " filetype=binary");
                     if (da.getVeclen() > 1)
                     {
                        if (k == 0)
                           header.println(" stride=" + da.getVeclen());
                        else
                           header.println(" skip=" + (j * dSize) + " stride=" + da.getVeclen());
                     } else
                        header.println();
                  }
                  out = new FileImageOutputStream(new File(outFname));
                  if (allByte)
                  {
                     bData = da.getBData();
                     out.write(bData, 0, bData.length);
                  } else
                  {
                     fData = da.getFData();
                     out.writeFloats(fData, 0, fData.length);
                  }
                  out.close();
               }
            }
         }
         header.close();
         return;
      } catch (Exception e)
      {
      }
   }
   
}
