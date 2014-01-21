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

package pl.edu.icm.visnow.lib.utils.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import javax.imageio.stream.FileImageOutputStream;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.lib.basic.writers.FieldWriter.Params;
import static pl.edu.icm.visnow.lib.utils.io.FieldWriterCore.LOGGER;

/**
 *
 * @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class AVSRegularFieldWriterCore extends FieldWriterCore
{
   RegularField regularInField;

   public AVSRegularFieldWriterCore(RegularField regularInField, Params params)
   {
      super(regularInField, params);
      this.regularInField = regularInField;
   }

   private void printASCIIColumns(String genFileName)
   {
      printASCIIColumns(genFileName, regularInField);
   }

   private static void printASCIIColumns(String genFileName, RegularField regularInField)
   {
      try
      {
      PrintWriter contentWriter = new PrintWriter(new FileOutputStream(genFileName + ".data"));
      for (int i = 0; i < regularInField.getNNodes(); i++)
      {
         for (int j = 0; j < regularInField.getNData(); j++)
         {
            DataArray da = regularInField.getData(j);
            int vl = da.getVeclen();
            switch (da.getType())
            {
               case DataArray.FIELD_DATA_BYTE:
                  for (int k = 0; k < vl; k++)
                     contentWriter.printf("%3d ", da.getBData()[i * vl + k] & 0xff);
                  break;
               case DataArray.FIELD_DATA_SHORT:
                  for (int k = 0; k < vl; k++)
                     contentWriter.printf("%3d ", da.getSData()[i * vl + k]);
                  break;
               case DataArray.FIELD_DATA_INT:
                  for (int k = 0; k < vl; k++)
                     contentWriter.printf("%6d ", da.getIData()[i * vl + k]);
                  break;
               case DataArray.FIELD_DATA_FLOAT:
                  for (int k = 0; k < vl; k++)
                     contentWriter.printf("%9f ", da.getFData()[i * vl + k]);
                  break;
               case DataArray.FIELD_DATA_DOUBLE:
                  for (int k = 0; k < vl; k++)
                     contentWriter.printf("%12f ", da.getDData()[i * vl + k]);
                  break;
            }
         }
         contentWriter.println();
      }
      contentWriter.close();
      } catch (Exception e)
      {
         System.out.println("could not write file " + genFileName + ".data");
      }
   }

   private static void printAVSLabelsUnits(PrintWriter headerWriter, RegularField regularInField)
   {
      int scalars = 0;
      headerWriter.print("label = ");
      for (int i = 0; i < regularInField.getNData(); i++)
      {
         int vn = regularInField.getData(i).getVeclen();
         scalars += vn;
         if (vn == 1)
            headerWriter.print(regularInField.getData(i).getName().replace(' ', '_') + " ");
         else
            for (int j = 0; j < vn; j++)
               headerWriter.print(regularInField.getData(i).getName().replace(' ', '_') + j + " ");
      }
      headerWriter.println();
      headerWriter.print("unit = ");
      for (int i = 0; i < regularInField.getNData(); i++)
      {
         if (regularInField.getData(i).getVeclen() == 1)
            headerWriter.print(regularInField.getData(i).getUnit().replace(' ', '_') + "_ ");
         else
            for (int j = 0; j < regularInField.getData(i).getVeclen(); j++)
               headerWriter.print(regularInField.getData(i).getUnit().replace(' ', '_') + "_ ");
      }
      headerWriter.println();
   }


   public boolean writeField()
   {
      return writeField(regularInField, params.getFileName(), params.isAscii(), params.isSingleFile());
   }

   public boolean writeField(RegularField regularInField, String fileName, boolean ascii, boolean singleFile)
   {
      String outFileName = fileName;
      boolean allByte = false;
      DataArray da;
      byte[] bData;
      float[] fData;
      int scalars = 0;
      FileImageOutputStream out;
      PrintWriter contentWriter;

      if (outFileName.endsWith(".fld"))
         genFileName = outFileName.substring(0, outFileName.lastIndexOf(".fld"));
      else
         outFileName = genFileName + ".fld";

      try
      {
         PrintWriter headerWriter = new PrintWriter(new FileOutputStream(outFileName));
         headerWriter.println("# AVS field file");
         headerWriter.println("ndim = " + regularInField.getDims().length);
         for (int i = 0; i < regularInField.getDims().length; i++)
         {
            headerWriter.println("dim" + (i + 1) + " = " + regularInField.getDims()[i]);
         }
         headerWriter.println("nspace = " + regularInField.getNSpace());
         allByte = true;
         scalars = 0;
         for (int i = 0; i < regularInField.getNData(); i++)
         {
            scalars += regularInField.getData(i).getVeclen();
            if (regularInField.getData(i).getType() != DataArray.FIELD_DATA_BYTE)
               allByte = false;
         }
         headerWriter.println("veclen = " + scalars);
         if (allByte)
            headerWriter.println("data = byte");
         else
            headerWriter.println("data = xdr_float");
         if (regularInField.getCoords() == null)
         {
            headerWriter.println("field = uniform");
            headerWriter.print("min_ext = ");
            for (int i = 0; i < regularInField.getNSpace(); i++)
               headerWriter.print("" + regularInField.getExtents()[0][i] + " ");
            headerWriter.println();
            headerWriter.print("max_ext = ");
            for (int i = 0; i < regularInField.getNSpace(); i++)
               headerWriter.print("" + regularInField.getExtents()[1][i] + " ");
            headerWriter.println();
            printAVSLabelsUnits(headerWriter, regularInField);
         } else
         {
            headerWriter.println("field = irregular");
            printAVSLabelsUnits(headerWriter, regularInField);
            if (ascii)
            {
               for (int i = 0; i < regularInField.getNSpace(); i++)
               {
                  headerWriter.print("coord " + (i + 1) + " file=" + genFileName + ".coord filetype=ASCII ");
                  headerWriter.print("stride=" + regularInField.getNSpace());
                  if (i > 0)
                     headerWriter.print(" offset=" + i);
                  headerWriter.println();
               }
               contentWriter = new PrintWriter(new FileOutputStream(genFileName + ".coord"));
               float[] coord = regularInField.getCoords();
               for (int i = 0; i < coord.length; i += regularInField.getNSpace())
               {
                  for (int j = 0; j < regularInField.getNSpace(); j++)
                     contentWriter.printf("%7f ", coord[i + j]);
                  contentWriter.println();
               }
               contentWriter.close();
            } else
            {
               for (int i = 0; i < regularInField.getNSpace(); i++)
               {
                  headerWriter.print("coord " + (i + 1) + " file=" + genFileName + ".coord filetype=binary ");
                  headerWriter.print("stride=" + regularInField.getNSpace());
                  if (i > 0)
                     headerWriter.print(" skip=" + (4 * i));
                  headerWriter.println();
               }
               float[] coord = regularInField.getCoords();
               out = new FileImageOutputStream(new File(genFileName + ".coord"));
               out.writeFloats(coord, 0, coord.length);
               out.close();
            }
         }
         if (ascii)
         {
            for (int i = 0, k = 0; i < regularInField.getNData(); i++)
            {
               da = regularInField.getData(i);
               for (int j = 0; j < da.getVeclen(); j++, k++)
               {
                  if (k > 0)
                     headerWriter.println("variable " + (k + 1) + " file=" + genFileName + ".data filetype=ASCII stride=" + scalars + " offset=" + k);
                  else
                     headerWriter.println("variable " + (k + 1) + " file=" + genFileName + ".data filetype=ASCII stride=" + scalars);
               }
            }
            printASCIIColumns(genFileName, regularInField);
         } else
         {
            int dSize = 4;
            if (allByte)
               dSize = 1;
            if (singleFile)
            {
               out = new FileImageOutputStream(new File(genFileName + ".data"));
               for (int i = 0, k = 0; i < regularInField.getNData(); i++)
               {
                  da = regularInField.getData(i);
                  for (int j = 0; j < da.getVeclen(); j++, k++)
                     headerWriter.println("variable " + (k + 1)
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
               for (int i = 0, k = 0; i < regularInField.getNData(); i++)
               {
                  da = regularInField.getData(i);
                  String outFname = genFileName + "_" + da.getName().replace(' ', '_') + ".dat";
                  for (int j = 0; j < da.getVeclen(); j++, k++)
                  {
                     headerWriter.print("variable " + (k + 1) + " file=" + outFname + " filetype=binary");
                     if (da.getVeclen() > 1)
                     {
                        if (k == 0)
                           headerWriter.println(" stride=" + da.getVeclen());
                        else
                           headerWriter.println(" skip=" + (j * dSize) + " stride=" + da.getVeclen());
                     } else
                        headerWriter.println();
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
         headerWriter.close();
         return true;
      } catch (Exception e)
      {
          LOGGER.error("Error writing field", e);
          return false;
      }
   }
}
