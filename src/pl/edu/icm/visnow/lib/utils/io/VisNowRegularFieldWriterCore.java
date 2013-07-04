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
import java.io.IOException;
import java.io.PrintWriter;
import javax.imageio.stream.FileImageOutputStream;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.datasets.dataarrays.*;
import pl.edu.icm.visnow.lib.basic.writers.FieldWriter.Params;

/**
 * @author Bartosz Borucki (babor@icm.edu.pl) University of Warsaw,
 * Interdisciplinary Centre for Mathematical and Computational Modelling
 */
public class VisNowRegularFieldWriterCore extends FieldWriterCore
{

   public VisNowRegularFieldWriterCore(RegularField inField, Params params)
   {
      super(inField, params);
   }

   private static void writeBinary(RegularField inField, String genFileName, PrintWriter header, String sName)
   {
      System.out.println("writing " + sName.replaceFirst("vnf", "vnd").replaceFirst("VNF", "vnd"));
      header.println("file \"" + sName.replaceFirst("vnf", "vnd").replaceFirst("VNF", "vnd") + "\" binary");
      try
      {
         FileImageOutputStream outBinary = new FileImageOutputStream(new File(genFileName + ".vnd"));
         float[] timeSteps = inField.getAllTimesteps();
         if (timeSteps.length == 1)
         {
            if (inField.getMask() != null)
            {
               header.println("mask");
               boolean[] mask = inField.getMask();
               for (int i = 0; i < mask.length; i++)
                  outBinary.writeBoolean(mask[i]);
            }
            if (inField.getCoords() != null)
            {
               header.println("coords");
               float[] coords = inField.getCoords();
               outBinary.writeFloats(coords, 0, coords.length);
            }
            for (int i = 0; i < inField.getNData(); i++)
            {
               DataArray da = inField.getData(i);
               if (!da.isSimpleNumeric())
                  continue;
               header.println(da.getName().replace(' ', '_').replace('.', '_'));
               switch (da.getType())
               {
               case DataArray.FIELD_DATA_BYTE:
                  outBinary.write(da.getBData());
                  break;
               case DataArray.FIELD_DATA_SHORT:
                  outBinary.writeShorts(da.getSData(), 0, da.getSData().length);
                  break;
               case DataArray.FIELD_DATA_INT:
                  outBinary.writeInts(da.getIData(), 0, da.getIData().length);
                  break;
               case DataArray.FIELD_DATA_FLOAT:
                  outBinary.writeFloats(da.getFData(), 0, da.getFData().length);
                  break;
               case DataArray.FIELD_DATA_DOUBLE:
                  outBinary.writeDoubles(da.getDData(), 0, da.getDData().length);
                  break;
               }
            }
         } else
         {
            for (int step = 0; step < timeSteps.length; step++)
            {
               float t = timeSteps[step];
               header.println("timestep " + t);
               if (inField.isMaskTimestep(t))
               {
                  header.println("mask");
                  boolean[] mask = inField.getMask(t);
                  for (int i = 0; i < mask.length; i++)
                     outBinary.writeBoolean(mask[i]);
               }
               if (inField.isCoordTimestep(t))
               {
                  header.println("coords");
                  float[] coords = inField.getCoords(t);
                  outBinary.writeFloats(coords, 0, coords.length);
               }
               for (int i = 0; i < inField.getNData(); i++)
               {
                  DataArray da = inField.getData(i);
                  if (!da.isSimpleNumeric() || !da.isTimestep(t))
                     continue;
                  header.println(da.getName().replace(' ', '_').replace('.', '_'));
                  switch (da.getType())
                  {
                  case DataArray.FIELD_DATA_BYTE:
                     outBinary.write(((ByteDataArray)da).getData(t));
                     break;
                  case DataArray.FIELD_DATA_SHORT:
                     short[] outs = ((ShortDataArray)da).getData(t);
                     outBinary.writeShorts(outs, 0, outs.length);
                     break;
                  case DataArray.FIELD_DATA_INT:
                     int[] outi = ((IntDataArray)da).getData(t);
                     outBinary.writeInts(outi, 0, outi.length);
                     break;
                  case DataArray.FIELD_DATA_FLOAT:
                     float[] outf = ((FloatDataArray)da).getData(t);
                     outBinary.writeFloats(outf, 0, outf.length);
                     break;
                  case DataArray.FIELD_DATA_DOUBLE:
                     double[] outd = ((DoubleDataArray)da).getData(t);
                     outBinary.writeDoubles(outd, 0, outd.length);
                     break;
                  }
               }
               header.println("end");
            }
         }
         outBinary.close();
      } catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   private static void writeASCII(RegularField inField, String genFileName, PrintWriter header, String sName)
   {
      System.out.println("writing " + sName.replaceFirst("vnf", "txt").replaceFirst("VNF", "txt"));
      header.println("file \"" + sName.replaceFirst("vnf", "txt").replaceFirst("VNF", "txt") + "\" ascii col");
      DataArray da;
      int[] dataFormLengths =
      {
         4, 4, 5, 7, 10, 14
      };
      try
      {
         PrintWriter outA = new PrintWriter(new FileOutputStream(genFileName + ".txt"));
         float[] timeSteps = inField.getAllTimesteps();
         if (timeSteps.length == 1)
         {
            header.print("skip 1, ");
            int nCols = 0;
            if (inField.getMask() != null)
            {
               header.print("mask, ");
               outA.printf("mask");
               nCols += 1;
            }
            if (inField.getCoords() != null)
            {
               header.print("coords, ");
               outA.printf("%" + (10 * inField.getNSpace() - 2) + "s  ", "coordinates");
               nCols += inField.getNSpace();
            }
            for (int i = 0; i < inField.getNData(); i++)
            {
               da = inField.getData(i);
               if (!da.isSimpleNumeric())
                  continue;
               nCols += da.getVeclen();
               header.print(da.getName().replace(' ', '_').replace('.', '_') + ", ");
               String entry = da.getName().replace(' ', '_').replace('.', '_') + 
                              "                                                                         ";
               outA.print(" " + (entry).substring(0, da.getVeclen() * dataFormLengths[da.getType()] - 1));
            }
            outA.println();
            header.println();
            boolean[][] boolArrs = new boolean[nCols][];
            byte[][] byteArrs = new byte[nCols][];
            short[][] shortArrs = new short[nCols][];
            int[][] intArrs = new int[nCols][];
            float[][] floatArrs = new float[nCols][];
            double[][] dblArrs = new double[nCols][];
            int[] types = new int[nCols];
            int[] vlens = new int[nCols];
            int[] ind = new int[nCols];
            int iCol = 0;

            if (inField.getMask() != null)
            {
               types[iCol] = DataArray.FIELD_DATA_BOOLEAN;
               ind[iCol] = 0;
               vlens[iCol] = 1;
               iCol += 1;
               boolArrs[iCol] = inField.getMask();
            }
            if (inField.getCoords() != null)
               for (int j = 0; j < inField.getNSpace(); j++, iCol++)
               {
                  types[iCol] = DataArray.FIELD_DATA_FLOAT;
                  ind[iCol] = j;
                  vlens[iCol] = inField.getNSpace();
                  floatArrs[iCol] = inField.getCoords();
               }
            for (int i = 0; i < inField.getNData(); i++)
            {
               da = inField.getData(i);
               if (!da.isSimpleNumeric())
                  continue;
               for (int j = 0; j < da.getVeclen(); j++, iCol++)
               {
                  types[iCol] = da.getType();
                  ind[iCol] = j;
                  vlens[iCol] = da.getVeclen();
                  switch (types[iCol])
                  {
                  case DataArray.FIELD_DATA_BYTE:
                     byteArrs[iCol] = inField.getData(i).getBData();
                     break;
                  case DataArray.FIELD_DATA_SHORT:
                     shortArrs[iCol] = inField.getData(i).getSData();
                     break;
                  case DataArray.FIELD_DATA_INT:
                     intArrs[iCol] = inField.getData(i).getIData();
                     break;
                  case DataArray.FIELD_DATA_FLOAT:
                     floatArrs[iCol] = inField.getData(i).getFData();
                     break;
                  case DataArray.FIELD_DATA_DOUBLE:
                     dblArrs[iCol] = inField.getData(i).getDData();
                     break;
                  }
               }
            }
            for (int k = 0; k < inField.getNNodes(); k++)
            {
               for (int l = 0; l < nCols; l++)
               {
                  switch (types[l])
                  {
                  case DataArray.FIELD_DATA_BOOLEAN:
                     outA.print(boolArrs[l][ind[l]] ? "  1 " : "  0 ");
                     ind[l] += vlens[l];
                     break;
                  case DataArray.FIELD_DATA_BYTE:
                     outA.printf("%3d ", byteArrs[l][ind[l]] & 0xff);
                     ind[l] += vlens[l];
                     break;
                  case DataArray.FIELD_DATA_SHORT:
                     outA.printf("%4d ", shortArrs[l][ind[l]]);
                     ind[l] += vlens[l];
                     break;
                  case DataArray.FIELD_DATA_INT:
                     outA.printf("%6d ", intArrs[l][ind[l]]);
                     ind[l] += vlens[l];
                     break;
                  case DataArray.FIELD_DATA_FLOAT:
                     outA.printf("%9.4f ", floatArrs[l][ind[l]]);
                     ind[l] += vlens[l];
                     break;
                  case DataArray.FIELD_DATA_DOUBLE:
                     outA.printf("%13.6f ", dblArrs[l][ind[l]]);
                     ind[l] += vlens[l];
                     break;
                  }
               }
               outA.println();
            }
         }
         else
         {
            for (int step = 0; step < timeSteps.length; step++)
            {
               float t = timeSteps[step];
               header.println("timestep " + t);
               header.print("skip 1, ");
               int nCols = 0;
               if (inField.isMaskTimestep(t))
               {
                  header.print("mask, ");
                  outA.printf("mask");
                  nCols += 1;
               }
               if (inField.isCoordTimestep(t))
               {
                  header.print("coords, ");
                  outA.printf("%" + (10 * inField.getNSpace() - 2) + "s  ", "coordinates");
                  nCols += inField.getNSpace();
               }
               for (int i = 0; i < inField.getNData(); i++)
               {
                  da = inField.getData(i);
                  if (!da.isSimpleNumeric() || !da.isTimestep(t))
                     continue;
                  nCols += da.getVeclen();
                  header.print(da.getName().replace(' ', '_').replace('.', '_') + ", ");
                  String entry = da.getName().replace(' ', '_').replace('.', '_') + 
                                 "                                                                         ";
                  outA.print(" " + (entry).substring(0, da.getVeclen() * dataFormLengths[da.getType()] - 1));
               }
               outA.println();
               header.println();
               header.println("end");
               
               boolean[][] boolArrs = new boolean[nCols][];
               byte[][] byteArrs = new byte[nCols][];
               short[][] shortArrs = new short[nCols][];
               int[][] intArrs = new int[nCols][];
               float[][] floatArrs = new float[nCols][];
               double[][] dblArrs = new double[nCols][];
               int[] types = new int[nCols];
               int[] vlens = new int[nCols];
               int[] ind = new int[nCols];
               int iCol = 0;

               if (inField.isMaskTimestep(t))
               {
                  types[iCol] = DataArray.FIELD_DATA_BOOLEAN;
                  ind[iCol] = 0;
                  vlens[iCol] = 1;
                  iCol += 1;
                  boolArrs[iCol] = inField.getMask();
               }
               if (inField.isCoordTimestep(t))
                  for (int j = 0; j < inField.getNSpace(); j++, iCol++)
                  {
                     types[iCol] = DataArray.FIELD_DATA_FLOAT;
                     ind[iCol] = j;
                     vlens[iCol] = inField.getNSpace();
                     floatArrs[iCol] = inField.getCoords();
                  }
               for (int i = 0; i < inField.getNData(); i++)
               {
                  da = inField.getData(i);
                  if (!da.isSimpleNumeric() || !da.isTimestep(t))
                     continue;
                  for (int j = 0; j < da.getVeclen(); j++, iCol++)
                  {
                     types[iCol] = da.getType();
                     ind[iCol] = j;
                     vlens[iCol] = da.getVeclen();
                     switch (types[iCol])
                     {
                     case DataArray.FIELD_DATA_BYTE:
                        byteArrs[iCol]  = ((ByteDataArray)da).getData(t);
                        break;
                     case DataArray.FIELD_DATA_SHORT:
                        shortArrs[iCol] = ((ShortDataArray)da).getData(t);
                        break;
                     case DataArray.FIELD_DATA_INT:
                        intArrs[iCol]   = ((IntDataArray)da).getData(t);
                        break;
                     case DataArray.FIELD_DATA_FLOAT:
                        floatArrs[iCol] = ((FloatDataArray)da).getData(t);
                        break;
                     case DataArray.FIELD_DATA_DOUBLE:
                        dblArrs[iCol]   = ((DoubleDataArray)da).getData(t);
                        break;
                     }
                  }
               }
               for (int k = 0; k < inField.getNNodes(); k++)
               {
                  for (int l = 0; l < nCols; l++)
                  {
                     switch (types[l])
                     {
                     case DataArray.FIELD_DATA_BOOLEAN:
                        outA.print(boolArrs[l][ind[l]] ? "  1 " : "  0 ");
                        ind[l] += vlens[l];
                        break;
                     case DataArray.FIELD_DATA_BYTE:
                        outA.printf("%3d ", byteArrs[l][ind[l]] & 0xff);
                        ind[l] += vlens[l];
                        break;
                     case DataArray.FIELD_DATA_SHORT:
                        outA.printf("%4d ", shortArrs[l][ind[l]]);
                        ind[l] += vlens[l];
                        break;
                     case DataArray.FIELD_DATA_INT:
                        outA.printf("%6d ", intArrs[l][ind[l]]);
                        ind[l] += vlens[l];
                        break;
                     case DataArray.FIELD_DATA_FLOAT:
                        outA.printf("%9.4f ", floatArrs[l][ind[l]]);
                        ind[l] += vlens[l];
                        break;
                     case DataArray.FIELD_DATA_DOUBLE:
                        outA.printf("%13.6f ", dblArrs[l][ind[l]]);
                        ind[l] += vlens[l];
                        break;
                     }
                  }
                  outA.println();
               }
            }
         }
         outA.close();
      } catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   public void writeField()
   {
      writeField(inField, params.getFileName(), params.isAscii(), params.isSingleFile());
   }

   public static void writeField(RegularField inField, String fileName, boolean ascii, boolean singleFile)
   {
      if (inField == null)
         return;
      String outFileName = fileName;
      String genFileName = fileName;
      DataArray da;
      String dataTypes[] =
      {
         "boolean", "byte", "short", "integer", "float", "double"
      };

      if (outFileName.endsWith(".vnf"))
         genFileName = outFileName.substring(0, outFileName.lastIndexOf(".vnf"));
      else
         outFileName = genFileName + ".vnf";
      File outFile = new File(outFileName);

      try
      {
         PrintWriter header = new PrintWriter(new FileOutputStream(outFile));
         header.println("#VisNow regular field");
         if (inField.getName() != null)
            header.print("field \"" + inField.getName() + "\"");
         header.print(", dims: ");
         for (int i = 0; i < inField.getDims().length; i++)
            header.print(" " + inField.getDims()[i]);
         if (inField.isMask())
            header.print(", mask");
         if (inField.getCoords() != null)
            header.print(", coords");
         header.println();
         if (inField.getCoords() == null)
         {
            float[][] af = inField.getAffine();
            header.printf("origin %10.4e %10.4e %10.4e %n", af[3][0], af[3][1], af[3][2]);
            for (int i = 0; i < 3; i++)
               header.printf("    v%d %10.4e %10.4e %10.4e %n", i, af[i][0], af[i][1], af[i][2]);
         }
         int maxCmpNameLen = 0;
         for (int i = 0; i < inField.getNData(); i++)
            if (inField.getData(i).isSimpleNumeric() && inField.getData(i).getName().length() > maxCmpNameLen)
               maxCmpNameLen = inField.getData(i).getName().length();
         for (int i = 0; i < inField.getNData(); i++)
         {
            da = inField.getData(i);
            if (!da.isSimpleNumeric())
               continue;
            header.printf("component %" + maxCmpNameLen + "s %7s", da.getName().replace(' ', '_').replace('.', '_'), dataTypes[da.getType()]);
            if (da.getVeclen() > 1)
            {
               if (da.getDims()[0] != da.getVeclen())
               {
                  header.print(", array " + da.getDims()[0]);
                  if (da.isSymmetric())
                     header.print(", sym");
               } else
                  header.print(", vector " + da.getVeclen());
            }
            if (da.getUnit() != null && !da.getUnit().isEmpty())
               header.print(", unit " + da.getUnit());
            if (da.getUserData() != null)
            {
               header.print(", user:");
               String[] udata = da.getUserData();
               for (int j = 0; j < udata.length; j++)
               {
                  if (j > 0)
                     header.print(";");
                  header.print("\"" + udata[j] + "\"");
               }
            }
            header.println();
         }
         String sName = outFile.getName();
         if (ascii)
            writeASCII(inField, genFileName, header, sName);
         else
            writeBinary(inField, genFileName, header, sName);
         header.close();
      } catch (Exception e)
      {
      }
   }
}
