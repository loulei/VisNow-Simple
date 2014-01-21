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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
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

   public static final int MAXLEN = 268435455; //2^28 - 1;
   RegularField regularInField;

   public VisNowRegularFieldWriterCore(RegularField regularInField, Params params)
   {
      super(regularInField, params);
      this.regularInField = regularInField;
   }

   private void writeBinary()
   {
      System.out.println("writing " + genFileName + ".vnd");
      headerWriter.println("file \"" + genFileName + ".vnd\" binary");
      try
      {
         contentOutput = new FileImageOutputStream(new File(outFileName + ".vnd"));
         float[] timeSteps = regularInField.getAllTimesteps();
         if (timeSteps.length == 1)
         {
            if (regularInField.getMask() != null)
            {
               headerWriter.println("mask");
               boolean[] mask = regularInField.getMask();
               for (int i = 0; i < mask.length; i++)
                  contentOutput.writeBoolean(mask[i]);
            }
            if (regularInField.getCoords() != null)
            {
               headerWriter.println("coords");
               float[] coords = regularInField.getCoords();
               contentOutput.writeFloats(coords, 0, coords.length);
            }
            for (int i = 0; i < regularInField.getNData(); i++)
            {
               DataArray da = regularInField.getData(i);
               if (!da.isSimpleNumeric())
                  continue;
               headerWriter.println(da.getName().replace(' ', '_').replace('.', '_'));
               int rem = da.getNData();
               int off = 0;
               while (rem > 0)
               {
                  switch (da.getType())
                  {
                     case DataArray.FIELD_DATA_BYTE:
                        contentOutput.write(da.getBData(), off, Math.min(MAXLEN, rem));
                        break;
                     case DataArray.FIELD_DATA_SHORT:
                        contentOutput.writeShorts(da.getSData(), off, Math.min(MAXLEN, rem));
                        break;
                     case DataArray.FIELD_DATA_INT:
                        contentOutput.writeInts(da.getIData(), off, Math.min(MAXLEN, rem));
                        break;
                     case DataArray.FIELD_DATA_FLOAT:
                        contentOutput.writeFloats(da.getFData(), off, Math.min(MAXLEN, rem));
                        break;
                     case DataArray.FIELD_DATA_DOUBLE:
                        contentOutput.writeDoubles(da.getDData(), off, Math.min(MAXLEN, rem));
                        break;
                  }
                  rem -= MAXLEN;
                  off += MAXLEN;
               }

            }
         } else
            for (int step = 0; step < timeSteps.length; step++)
            {
               float t = timeSteps[step];
               headerWriter.println("timestep " + t);
               if (regularInField.isMaskTimestep(t))
               {
                  headerWriter.println("mask");
                  boolean[] mask = regularInField.getMask(t);
                  for (int i = 0; i < mask.length; i++)
                     contentOutput.writeBoolean(mask[i]);
               }
               if (regularInField.isCoordTimestep(t))
               {
                  headerWriter.println("coords");
                  float[] coords = regularInField.getCoords(t);
                  contentOutput.writeFloats(coords, 0, coords.length);
               }
               for (int i = 0; i < regularInField.getNData(); i++)
               {
                  DataArray da = regularInField.getData(i);
                  if (!da.isSimpleNumeric() || !da.isTimestep(t))
                     continue;
                  headerWriter.println(da.getName().replace(' ', '_').replace('.', '_'));
                  int rem = da.getNData();
                  int off = 0;
                  while (rem > 0)
                  {
                     switch (da.getType())
                     {
                        case DataArray.FIELD_DATA_BYTE:
                           contentOutput.write(((ByteDataArray) da).getData(t), off, Math.min(MAXLEN, rem));
                           break;
                        case DataArray.FIELD_DATA_SHORT:
                           contentOutput.writeShorts(((ShortDataArray) da).getData(t), off, Math.min(MAXLEN, rem));
                           break;
                        case DataArray.FIELD_DATA_INT:
                           contentOutput.writeInts(((IntDataArray) da).getData(t), off, Math.min(MAXLEN, rem));
                           break;
                        case DataArray.FIELD_DATA_FLOAT:
                           contentOutput.writeFloats(((FloatDataArray) da).getData(t), off, Math.min(MAXLEN, rem));
                           break;
                        case DataArray.FIELD_DATA_DOUBLE:
                           contentOutput.writeDoubles(((DoubleDataArray) da).getData(t), off, Math.min(MAXLEN, rem));
                           break;
                     }
                     rem -= MAXLEN;
                     off += MAXLEN;
                  }
               }
               headerWriter.println("end");
            }
         contentOutput.close();
      } catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   private void writeASCII()
   {
      System.out.println("writing " + genFileName + ".txt");
      headerWriter.println("file \"" + genFileName + ".txt\" ascii col");
      DataArray da;
      int[] dataFormLengths =
      {
         4, 4, 5, 7, 10, 14
      };
      try
      {
         contentWriter = new PrintWriter(new FileOutputStream(outFileName + ".txt"));
         float[] timeSteps = regularInField.getAllTimesteps();
         if (timeSteps.length == 1)
         {
            headerWriter.println("skip 1");
            int nCols = 0;
            if (regularInField.getMask() != null)
            {
               headerWriter.print("mask, ");
               contentWriter.printf(Locale.US, "mask");
               nCols += 1;
            }
            if (regularInField.getCoords() != null)
            {
               headerWriter.print("coords, ");
               contentWriter.printf(Locale.US, "%" + (10 * regularInField.getNSpace() - 2) + "s  ", "coordinates");
               nCols += regularInField.getNSpace();
            }
            for (int i = 0; i < regularInField.getNData(); i++)
            {
               da = regularInField.getData(i);
               if (!da.isSimpleNumeric())
                  continue;
               nCols += da.getVeclen();
               headerWriter.print(da.getName().replace(' ', '_').replace('.', '_') + ", ");
               String entry = da.getName().replace(' ', '_').replace('.', '_')
                       + "                                                                         ";
               contentWriter.print(" " + (entry).substring(0, da.getVeclen() * dataFormLengths[da.getType()] - 1));
            }
            contentWriter.println();
            headerWriter.println();
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

            if (regularInField.getMask() != null)
            {
               types[iCol] = DataArray.FIELD_DATA_BOOLEAN;
               ind[iCol] = 0;
               vlens[iCol] = 1;
               iCol += 1;
               boolArrs[iCol] = regularInField.getMask();
            }
            if (regularInField.getCoords() != null)
               for (int j = 0; j < regularInField.getNSpace(); j++, iCol++)
               {
                  types[iCol] = DataArray.FIELD_DATA_FLOAT;
                  ind[iCol] = j;
                  vlens[iCol] = regularInField.getNSpace();
                  floatArrs[iCol] = regularInField.getCoords();
               }
            for (int i = 0; i < regularInField.getNData(); i++)
            {
               da = regularInField.getData(i);
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
                        byteArrs[iCol] = regularInField.getData(i).getBData();
                        break;
                     case DataArray.FIELD_DATA_SHORT:
                        shortArrs[iCol] = regularInField.getData(i).getSData();
                        break;
                     case DataArray.FIELD_DATA_INT:
                        intArrs[iCol] = regularInField.getData(i).getIData();
                        break;
                     case DataArray.FIELD_DATA_FLOAT:
                        floatArrs[iCol] = regularInField.getData(i).getFData();
                        break;
                     case DataArray.FIELD_DATA_DOUBLE:
                        dblArrs[iCol] = regularInField.getData(i).getDData();
                        break;
                  }
               }
            }
            for (int k = 0; k < regularInField.getNNodes(); k++)
            {
               for (int l = 0; l < nCols; l++)
                  switch (types[l])
                  {
                     case DataArray.FIELD_DATA_BOOLEAN:
                        contentWriter.print(boolArrs[l][ind[l]] ? "  1 " : "  0 ");
                        ind[l] += vlens[l];
                        break;
                     case DataArray.FIELD_DATA_BYTE:
                        contentWriter.printf(Locale.US, "%3d ", byteArrs[l][ind[l]] & 0xff);
                        ind[l] += vlens[l];
                        break;
                     case DataArray.FIELD_DATA_SHORT:
                        contentWriter.printf(Locale.US, "%4d ", shortArrs[l][ind[l]]);
                        ind[l] += vlens[l];
                        break;
                     case DataArray.FIELD_DATA_INT:
                        contentWriter.printf(Locale.US, "%6d ", intArrs[l][ind[l]]);
                        ind[l] += vlens[l];
                        break;
                     case DataArray.FIELD_DATA_FLOAT:
                        contentWriter.printf(Locale.US, "%9.4f ", floatArrs[l][ind[l]]);
                        ind[l] += vlens[l];
                        break;
                     case DataArray.FIELD_DATA_DOUBLE:
                        contentWriter.printf(Locale.US, "%13.6f ", dblArrs[l][ind[l]]);
                        ind[l] += vlens[l];
                        break;
                  }
               contentWriter.println();
            }
         } else
            for (int step = 0; step < timeSteps.length; step++)
            {
               float t = timeSteps[step];
               headerWriter.println("timestep " + t);
               headerWriter.println("skip 1");
               int nCols = 0;
               if (regularInField.isMaskTimestep(t))
               {
                  headerWriter.print("mask, ");
                  contentWriter.printf(Locale.US, "mask");
                  nCols += 1;
               }
               if (regularInField.isCoordTimestep(t))
               {
                  headerWriter.print("coords, ");
                  contentWriter.printf(Locale.US, "%" + (10 * regularInField.getNSpace() - 2) + "s  ", "coordinates");
                  nCols += regularInField.getNSpace();
               }
               for (int i = 0; i < regularInField.getNData(); i++)
               {
                  da = regularInField.getData(i);
                  if (!da.isSimpleNumeric() || !da.isTimestep(t))
                     continue;
                  nCols += da.getVeclen();
                  headerWriter.print(da.getName().replace(' ', '_').replace('.', '_') + ", ");
                  String entry = da.getName().replace(' ', '_').replace('.', '_')
                          + "                                                                         ";
                  contentWriter.print(" " + (entry).substring(0, da.getVeclen() * dataFormLengths[da.getType()] - 1));
               }
               contentWriter.println();
               headerWriter.println();
               headerWriter.println("end");

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

               if (regularInField.isMaskTimestep(t))
               {
                  types[iCol] = DataArray.FIELD_DATA_BOOLEAN;
                  ind[iCol] = 0;
                  vlens[iCol] = 1;
                  iCol += 1;
                  boolArrs[iCol] = regularInField.getMask();
               }
               if (regularInField.isCoordTimestep(t))
                  for (int j = 0; j < regularInField.getNSpace(); j++, iCol++)
                  {
                     types[iCol] = DataArray.FIELD_DATA_FLOAT;
                     ind[iCol] = j;
                     vlens[iCol] = regularInField.getNSpace();
                     floatArrs[iCol] = regularInField.getCoords();
                  }
               for (int i = 0; i < regularInField.getNData(); i++)
               {
                  da = regularInField.getData(i);
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
                           byteArrs[iCol] = ((ByteDataArray) da).getData(t);
                           break;
                        case DataArray.FIELD_DATA_SHORT:
                           shortArrs[iCol] = ((ShortDataArray) da).getData(t);
                           break;
                        case DataArray.FIELD_DATA_INT:
                           intArrs[iCol] = ((IntDataArray) da).getData(t);
                           break;
                        case DataArray.FIELD_DATA_FLOAT:
                           floatArrs[iCol] = ((FloatDataArray) da).getData(t);
                           break;
                        case DataArray.FIELD_DATA_DOUBLE:
                           dblArrs[iCol] = ((DoubleDataArray) da).getData(t);
                           break;
                     }
                  }
               }
               for (int k = 0; k < regularInField.getNNodes(); k++)
               {
                  for (int l = 0; l < nCols; l++)
                     switch (types[l])
                     {
                        case DataArray.FIELD_DATA_BOOLEAN:
                           contentWriter.print(boolArrs[l][ind[l]] ? "  1 " : "  0 ");
                           ind[l] += vlens[l];
                           break;
                        case DataArray.FIELD_DATA_BYTE:
                           contentWriter.printf(Locale.US, "%3d ", byteArrs[l][ind[l]] & 0xff);
                           ind[l] += vlens[l];
                           break;
                        case DataArray.FIELD_DATA_SHORT:
                           contentWriter.printf(Locale.US, "%4d ", shortArrs[l][ind[l]]);
                           ind[l] += vlens[l];
                           break;
                        case DataArray.FIELD_DATA_INT:
                           contentWriter.printf(Locale.US, "%6d ", intArrs[l][ind[l]]);
                           ind[l] += vlens[l];
                           break;
                        case DataArray.FIELD_DATA_FLOAT:
                           contentWriter.printf(Locale.US, "%9.4f ", floatArrs[l][ind[l]]);
                           ind[l] += vlens[l];
                           break;
                        case DataArray.FIELD_DATA_DOUBLE:
                           contentWriter.printf(Locale.US, "%13.6f ", dblArrs[l][ind[l]]);
                           ind[l] += vlens[l];
                           break;
                     }
                  contentWriter.println();
               }
            }
         contentWriter.close();
      } catch (FileNotFoundException e)
      {
         e.printStackTrace();
      }
   }

   @Override
   public boolean writeField()
   {
      return writeField(regularInField, params.getFileName(), params.isAscii(), params.isSingleFile());
   }

   public boolean writeField(RegularField regularInField, String fileName, boolean ascii, boolean single)
   {
      if (regularInField == null)
         return false;
      String headerFileName = fileName;
      outFileName = fileName;
      DataArray da;

      if (outFileName.endsWith(".vnf"))
      {
         outFileName = outFileName.substring(0, outFileName.lastIndexOf(".vnf"));
      }
      else
         headerFileName = genFileName + ".vnf";
      File headerFile = new File(headerFileName);
      genFileName = headerFile.getName();
      genFileName = genFileName.substring(0, genFileName.lastIndexOf(".vnf"));
      try
      {
         headerWriter = new PrintWriter(new FileOutputStream(headerFile));
         headerWriter.println("#VisNow regular field");
         if (regularInField.getName() != null && !regularInField.getName().trim().isEmpty())
            headerWriter.print("field \"" + regularInField.getName() + "\",");
         headerWriter.print(" dims ");
         for (int i = 0; i < regularInField.getDims().length; i++)
            headerWriter.print(" " + regularInField.getDims()[i]);
         if (regularInField.isMask())
            headerWriter.print(", mask");
         if (regularInField.getCoords() != null)
            headerWriter.print(", coords");
         headerWriter.println();
         if (regularInField.getCoords() == null)
         {
            float[][] af = regularInField.getAffine();
            headerWriter.printf(Locale.US, "origin %10.4e %10.4e %10.4e %n", af[3][0], af[3][1], af[3][2]);
            for (int i = 0; i < 3; i++)
               headerWriter.printf(Locale.US, "    v%d %10.4e %10.4e %10.4e %n", i, af[i][0], af[i][1], af[i][2]);
         }
         int maxCmpNameLen = 0;
         for (int i = 0; i < regularInField.getNData(); i++)
            if (regularInField.getData(i).isSimpleNumeric() && regularInField.getData(i).getName().length() > maxCmpNameLen)
               maxCmpNameLen = regularInField.getData(i).getName().length();
         for (int i = 0; i < regularInField.getNData(); i++)
         {
            da = regularInField.getData(i);
            if (!da.isSimpleNumeric())
               continue;
            headerWriter.printf(Locale.US, "component %" + maxCmpNameLen + "s %7s", da.getName().replace(' ', '_').replace('.', '_'), dataTypes[da.getType()]);
            if (da.getVeclen() > 1)
               if (da.getDims()[0] != da.getVeclen())
               {
                  headerWriter.print(", array " + da.getDims()[0]);
                  if (da.isSymmetric())
                     headerWriter.print(", sym");
               } else
                  headerWriter.print(", vector " + da.getVeclen());
            if (da.getUnit() != null && !da.getUnit().isEmpty())
               headerWriter.print(", unit " + da.getUnit());
            if (da.getUserData() != null)
            {
               headerWriter.print(", user:");
               String[] udata = da.getUserData();
               for (int j = 0; j < udata.length; j++)
               {
                  if (j > 0)
                     headerWriter.print(";");
                  headerWriter.print("\"" + udata[j] + "\"");
               }
            }
            headerWriter.println();
         }
         if (ascii)
            writeASCII();
         else
            writeBinary();
         headerWriter.close();
         return true;
      } catch (FileNotFoundException e)
      {
         LOGGER.error("Error writing field", e);
         return false;
      }
   }
}
