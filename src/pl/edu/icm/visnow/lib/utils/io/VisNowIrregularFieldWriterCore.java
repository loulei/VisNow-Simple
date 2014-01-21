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
import pl.edu.icm.visnow.datasets.CellArray;
import pl.edu.icm.visnow.datasets.CellSet;
import pl.edu.icm.visnow.datasets.IrregularField;
import pl.edu.icm.visnow.datasets.cells.Cell;
import pl.edu.icm.visnow.datasets.dataarrays.*;
import pl.edu.icm.visnow.lib.basic.writers.FieldWriter.Params;

/**
 * @author Krzysztof Nowinski (know@icm.edu.pl) University of Warsaw,
 * Interdisciplinary Centre for Mathematical and Computational Modelling
 */
public class VisNowIrregularFieldWriterCore extends FieldWriterCore
{

   public static final int MAXLEN = 268435455; //2^28 - 1;
   IrregularField irregularInField;

   public VisNowIrregularFieldWriterCore(IrregularField irregularInField, Params params)
   {
      super(irregularInField, params);
      this.irregularInField = irregularInField;
   }

   private void writeBinary(IrregularField irregularInField, String genFileName, PrintWriter headerWriter, String sName)
   {
      System.out.println("writing " + sName.replaceFirst("vnf", "vnd").replaceFirst("VNF", "vnd"));
      headerWriter.println("file \"" + sName.replaceFirst("vnf", "vnd").replaceFirst("VNF", "vnd") + "\" binary");
      try
      {
         FileImageOutputStream outBinary = new FileImageOutputStream(new File(genFileName + ".vnd"));
         float[] timeSteps = irregularInField.getAllTimesteps();
         if (timeSteps.length == 1)
         {
            if (irregularInField.getMask() != null)
            {
               headerWriter.println("mask");
               boolean[] mask = irregularInField.getMask();
               for (int i = 0; i < mask.length; i++)
               {
                  outBinary.writeBoolean(mask[i]);
               }
            }
            headerWriter.println("coords");
            float[] coords = irregularInField.getCoords();
            outBinary.writeFloats(coords, 0, coords.length);
            for (int i = 0; i < irregularInField.getNData(); i++)
            {
               DataArray da = irregularInField.getData(i);
               if (!da.isSimpleNumeric())
               {
                  continue;
               }
               headerWriter.println(da.getName().replace(' ', '_').replace('.', '_'));
               int rem = da.getNData();
               int off = 0;
               while (rem > 0)
               {
                  switch (da.getType())
                  {
                     case DataArray.FIELD_DATA_BYTE:
                        outBinary.write(da.getBData(), off, Math.min(MAXLEN, rem));
                        break;
                     case DataArray.FIELD_DATA_SHORT:
                        outBinary.writeShorts(da.getSData(), off, Math.min(MAXLEN, rem));
                        break;
                     case DataArray.FIELD_DATA_INT:
                        outBinary.writeInts(da.getIData(), off, Math.min(MAXLEN, rem));
                        break;
                     case DataArray.FIELD_DATA_FLOAT:
                        outBinary.writeFloats(da.getFData(), off, Math.min(MAXLEN, rem));
                        break;
                     case DataArray.FIELD_DATA_DOUBLE:
                        outBinary.writeDoubles(da.getDData(), off, Math.min(MAXLEN, rem));
                        break;
                  }
                  rem -= MAXLEN;
                  off += MAXLEN;
               }

            }
         } else
         {
            for (int step = 0; step < timeSteps.length; step++)
            {
               float t = timeSteps[step];
               headerWriter.println("timestep " + t);
               if (irregularInField.isMaskTimestep(t))
               {
                  headerWriter.println("mask");
                  boolean[] mask = irregularInField.getMask(t);
                  for (int i = 0; i < mask.length; i++)
                  {
                     outBinary.writeBoolean(mask[i]);
                  }
               }
               if (irregularInField.isCoordTimestep(t))
               {
                  headerWriter.println("coords");
                  float[] coords = irregularInField.getCoords(t);
                  outBinary.writeFloats(coords, 0, coords.length);
               }
               for (int i = 0; i < irregularInField.getNData(); i++)
               {
                  DataArray da = irregularInField.getData(i);
                  if (!da.isSimpleNumeric() || !da.isTimestep(t))
                  {
                     continue;
                  }
                  headerWriter.println(da.getName().replace(' ', '_').replace('.', '_'));
                  int rem = da.getNData();
                  int off = 0;
                  while (rem > 0)
                  {
                     switch (da.getType())
                     {
                        case DataArray.FIELD_DATA_BYTE:
                           outBinary.write(((ByteDataArray) da).getData(t), off, Math.min(MAXLEN, rem));
                           break;
                        case DataArray.FIELD_DATA_SHORT:
                           outBinary.writeShorts(((ShortDataArray) da).getData(t), off, Math.min(MAXLEN, rem));
                           break;
                        case DataArray.FIELD_DATA_INT:
                           outBinary.writeInts(((IntDataArray) da).getData(t), off, Math.min(MAXLEN, rem));
                           break;
                        case DataArray.FIELD_DATA_FLOAT:
                           outBinary.writeFloats(((FloatDataArray) da).getData(t), off, Math.min(MAXLEN, rem));
                           break;
                        case DataArray.FIELD_DATA_DOUBLE:
                           outBinary.writeDoubles(((DoubleDataArray) da).getData(t), off, Math.min(MAXLEN, rem));
                           break;
                     }
                     rem -= MAXLEN;
                     off += MAXLEN;
                  }
               }
               headerWriter.println("end");
            }
         }
         for (CellSet cellSet : irregularInField.getCellSets())
         {
            String setName = cellSet.getName().replaceAll("\\s", "_").replace('.', '_');
            for (int iCellArr = 0; iCellArr < Cell.TYPES; iCellArr++)
               if (cellSet.getCellArray(iCellArr) != null)
               {
                  CellArray cellArray = cellSet.getCellArray(iCellArr);
                  headerWriter.println(setName + ":" + Cell.PLURAL_NAMES[iCellArr] + ":nodes");
                  int rem = cellArray.getNodes().length;
                  int off = 0;
                  while (rem > 0)
                  {
                     outBinary.writeInts(cellArray.getNodes(), off, Math.min(MAXLEN, rem));
                     rem -= MAXLEN;
                     off += MAXLEN;
                  }
                  if (cellArray.getDataIndices() != null)
                  {
                     headerWriter.println(setName + ":" + Cell.PLURAL_NAMES[iCellArr] + ":indices");
                     rem = cellArray.getDataIndices().length;
                     off = 0;
                     while (rem > 0)
                     {
                        outBinary.writeInts(cellArray.getDataIndices(), off, Math.min(MAXLEN, rem));
                        rem -= MAXLEN;
                        off += MAXLEN;
                     }
                     outBinary.writeInts(cellArray.getDataIndices(), 0, cellArray.getDataIndices().length);
                  }
                  if (cellArray.getOrientations() != null)
                  {
                     boolean[] bo = cellArray.getOrientations();
                     byte[] b = new byte[cellArray.getOrientations().length];
                     for (int i = 0; i < b.length; i++)
                        b[i] = bo[i] ? (byte)1 : (byte)0;
                     headerWriter.println(setName + ":" + Cell.PLURAL_NAMES[iCellArr] + ":orientations");
                        outBinary.write(b);
                  }
               }
//            int maxCmpNameLen = 0;
//            for (int i = 0; i < cellSet.getNData(); i++)
//               if (cellSet.getData(i).isSimpleNumeric() && 
//                   cellSet.getData(i).getName().length() > maxCmpNameLen)
//                  maxCmpNameLen = cellSet.getData(i).getName().length();
//            for (int i = 0; i < cellSet.getNData(); i++)
//            {
//               DataArray da = cellSet.getData(i);
//               if (da.isSimpleNumeric())
//               {
//                  headerWriter.println(setName + ":" + da.getName().replace("\\s","_").replace('.', '_'));
//               }
//            }
         }
         outBinary.close();
      } catch (IOException e)
      {
         e.printStackTrace();
      }
   }
            
   int iCol = 0;
   
   private void prepareColumns(DataArray da,
                               boolean[][] boolArrs,  byte[][]   byteArrs,  
                               short[][]   shortArrs, int[][]    intArrs, 
                               float[][]   floatArrs, double[][] dblArrs,
                               int[] types, int[] vlens, int[] ind)
   {
      for (int j = 0; j < da.getVeclen(); j++, iCol++)
      {
         types[iCol] = da.getType();
         ind[iCol] = j;
         vlens[iCol] = da.getVeclen();
         switch (types[iCol])
         {
            case DataArray.FIELD_DATA_BYTE:
               byteArrs[iCol] = da.getBData();
               break;
            case DataArray.FIELD_DATA_SHORT:
               shortArrs[iCol] = da.getSData();
               break;
            case DataArray.FIELD_DATA_INT:
               intArrs[iCol] = da.getIData();
               break;
            case DataArray.FIELD_DATA_FLOAT:
               floatArrs[iCol] = da.getFData();
               break;
            case DataArray.FIELD_DATA_DOUBLE:
               dblArrs[iCol] = da.getDData();
               break;
         }
      }

   }
   
   private void printColumns(PrintWriter outA, int nCols, 
                             boolean[][] boolArrs,  byte[][]   byteArrs,  
                             short[][]   shortArrs, int[][]    intArrs, 
                             float[][]   floatArrs, double[][] dblArrs,
                             int[] types, int[] vlens, int[] ind)
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
               outA.printf(Locale.US, "%3d ", byteArrs[l][ind[l]] & 0xff);
               ind[l] += vlens[l];
               break;
            case DataArray.FIELD_DATA_SHORT:
               outA.printf(Locale.US, "%4d ", shortArrs[l][ind[l]]);
               ind[l] += vlens[l];
               break;
            case DataArray.FIELD_DATA_INT:
               outA.printf(Locale.US, "%6d ", intArrs[l][ind[l]]);
               ind[l] += vlens[l];
               break;
            case DataArray.FIELD_DATA_FLOAT:
               outA.printf(Locale.US, "%9.4f ", floatArrs[l][ind[l]]);
               ind[l] += vlens[l];
               break;
            case DataArray.FIELD_DATA_DOUBLE:
               outA.printf(Locale.US, "%13.6f ", dblArrs[l][ind[l]]);
               ind[l] += vlens[l];
               break;
         }
      }
      outA.println();
   }

   private void writeASCII(IrregularField irregularInField, String genFileName, PrintWriter headerWriter, String sName)
   {
      System.out.println("writing " + sName.replaceFirst("vnf", "txt").replaceFirst("VNF", "txt"));
      headerWriter.println("file \"" + sName.replaceFirst("vnf", "txt").replaceFirst("VNF", "txt") + "\" ascii col");
      DataArray da;
      int[] dataFormLengths =
      {
         4, 4, 5, 7, 10, 14
      };
      try
      {
         PrintWriter outA = new PrintWriter(new FileOutputStream(genFileName + ".txt"));
         float[] timeSteps = irregularInField.getAllTimesteps();
         if (timeSteps.length == 1)
         {
            headerWriter.println("skip 1");
            headerWriter.print("coords, ");
            int nCols = 0;
            outA.printf("%" + (10 * irregularInField.getNSpace() - 2) + "s  ", "coordinates");
            nCols += irregularInField.getNSpace();
            if (irregularInField.getMask() != null)
               headerWriter.print("mask, ");      
            for (int i = 0; i < irregularInField.getNData(); i++)
            {
               da = irregularInField.getData(i);
               if (!da.isSimpleNumeric())
                  continue;
               nCols += da.getVeclen();
               headerWriter.print(da.getName().replace(' ', '_').replace('.', '_') + ", ");
               String entry = da.getName().replace(' ', '_').replace('.', '_')
                       + "                                                                         ";
               outA.print(" " + (entry).substring(0, da.getVeclen() * dataFormLengths[da.getType()] - 1));
            }
            if (irregularInField.getMask() != null)
            {
               outA.printf("mask");
               nCols += 1;
            }
            outA.println();
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
            iCol = 0;

            if (irregularInField.getMask() != null)
            {
               types[iCol] = DataArray.FIELD_DATA_BOOLEAN;
               ind[iCol] = 0;
               vlens[iCol] = 1;
               iCol += 1;
               boolArrs[iCol] = irregularInField.getMask();
            }
            for (int j = 0; j < irregularInField.getNSpace(); j++, iCol++)
            {
               types[iCol] = DataArray.FIELD_DATA_FLOAT;
               ind[iCol] = j;
               vlens[iCol] = irregularInField.getNSpace();
               floatArrs[iCol] = irregularInField.getCoords();
            }
            for (int i = 0; i < irregularInField.getNData(); i++)
            {
               da = irregularInField.getData(i);
               if (da.isSimpleNumeric())
                  prepareColumns(da,
                                 boolArrs, byteArrs, shortArrs, intArrs, floatArrs, dblArrs,
                                 types, vlens, ind);
            }
            for (int k = 0; k < irregularInField.getNNodes(); k++)
               printColumns(outA, nCols, 
                            boolArrs, byteArrs, shortArrs, intArrs, floatArrs, dblArrs,
                            types, vlens, ind);
            
         } else
         {
            for (int step = 0; step < timeSteps.length; step++)
            {
               float t = timeSteps[step];
               headerWriter.println("timestep " + t);
               headerWriter.print("skip 1, ");
               int nCols = 0;
               if (irregularInField.isMaskTimestep(t))
               {
                  headerWriter.print("mask, ");
                  outA.printf("mask");
                  nCols += 1;
               }
               if (irregularInField.isCoordTimestep(t))
               {
                  headerWriter.print("coords, ");
                  outA.printf("%" + (10 * irregularInField.getNSpace() - 2) + "s  ", "coordinates");
                  nCols += irregularInField.getNSpace();
               }
               for (int i = 0; i < irregularInField.getNData(); i++)
               {
                  da = irregularInField.getData(i);
                  if (!da.isSimpleNumeric() || !da.isTimestep(t))
                     continue;
                  nCols += da.getVeclen();
                  headerWriter.print(da.getName().replace(' ', '_').replace('.', '_') + ", ");
                  String entry = da.getName().replace(' ', '_').replace('.', '_')
                          + "                                                                         ";
                  outA.print(" " + (entry).substring(0, da.getVeclen() * dataFormLengths[da.getType()] - 1));
               }
               outA.println();
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
               iCol = 0;

               if (irregularInField.isMaskTimestep(t))
               {
                  types[iCol] = DataArray.FIELD_DATA_BOOLEAN;
                  ind[iCol] = 0;
                  vlens[iCol] = 1;
                  iCol += 1;
                  boolArrs[iCol] = irregularInField.getMask();
               }
               if (irregularInField.isCoordTimestep(t))
                  for (int j = 0; j < irregularInField.getNSpace(); j++, iCol++)
                  {
                     types[iCol] = DataArray.FIELD_DATA_FLOAT;
                     ind[iCol] = j;
                     vlens[iCol] = irregularInField.getNSpace();
                     floatArrs[iCol] = irregularInField.getCoords();
               }
               for (int i = 0; i < irregularInField.getNData(); i++)
               {
                  da = irregularInField.getData(i);
                  if (!da.isSimpleNumeric() || !da.isTimestep(t))
                     continue;
                  prepareColumns(da,
                                 boolArrs, byteArrs, shortArrs, intArrs, floatArrs, dblArrs,
                                 types, vlens, ind);
               }
               for (int k = 0; k < irregularInField.getNNodes(); k++)
                  printColumns(outA, nCols, 
                               boolArrs, byteArrs, shortArrs, intArrs, floatArrs, dblArrs,
                               types, vlens, ind);
            }
         }
         for (CellSet cellSet : irregularInField.getCellSets())
         {
            String setName = cellSet.getName().replaceAll("\\s", "_").replace('.', '_');
            headerWriter.println("skip 1");
            outA.println(setName);
            
            for (int iCellArr = 0; iCellArr < Cell.TYPES; iCellArr++)
               if (cellSet.getCellArray(iCellArr) != null)
               {
                  headerWriter.println("skip 2");
                  CellArray cellArray = cellSet.getCellArray(iCellArr);
                  outA.println(Cell.PLURAL_NAMES[iCellArr]);
                  CellArray ca = cellSet.getCellArray(iCellArr);
                  int[] nodes = ca.getNodes();
                  int nn = Cell.nv[iCellArr];
                  headerWriter.print(setName + ":" + Cell.PLURAL_NAMES[iCellArr] + ":nodes, ");
                  outA.printf("%" + (11 * nn) + "s  ", "nodes     ");
                  if (cellArray.getDataIndices() != null)
                  {
                     headerWriter.print(  setName + ":" + Cell.PLURAL_NAMES[iCellArr] + ":indices, ");
                     outA.print("   indices");
                  }
                  if (cellArray.getOrientations() != null)
                  {
                     headerWriter.print(  setName + ":" + Cell.PLURAL_NAMES[iCellArr] + ":orientations, ");
                     outA.print(" orientations");
                  }
                  outA.println();
                  headerWriter.println();
                  for (int i = 0; i < ca.getnCells(); i++)
                  {
                     for (int j = 0; j < nn; j++)
                        outA.printf("%10d ", nodes[nn * i + j]);
                     if (cellArray.getDataIndices() != null)
                        outA.printf("%10d ", cellArray.getDataIndices()[i]);
                     if (cellArray.getOrientations() != null)
                        outA.print(cellArray.getOrientations()[i] ? "         1" : "         0");
                     outA.println();
                  }
               }
            if (cellSet.getNData() != 0)
            {
               headerWriter.println("skip 1");
               int nCols = 0;
               for (int i = 0; i < cellSet.getNData(); i++)
               {
                  da = cellSet.getData(i);
                  if (da.isSimpleNumeric())
                  {
                     headerWriter.println(setName + ":" + da.getName().replace("\\s","_").replace('.', '_'));
                     String entry = da.getName().replace(' ', '_').replace('.', '_')
                             + "                                                                         ";
                     outA.print(" " + (entry).substring(0, da.getVeclen() * dataFormLengths[da.getType()] - 1));
                     nCols += da.getVeclen();
                  }
               }
               outA.println();
               iCol = 0;
               boolean[][] boolArrs = new boolean[nCols][];
               byte[][] byteArrs = new byte[nCols][];
               short[][] shortArrs = new short[nCols][];
               int[][] intArrs = new int[nCols][];
               float[][] floatArrs = new float[nCols][];
               double[][] dblArrs = new double[nCols][];
               int[] types = new int[nCols];
               int[] vlens = new int[nCols];
               int[] ind = new int[nCols];
               for (int i = 0; i < cellSet.getNData(); i++)
               {
                  da = cellSet.getData(i);
                  if (da.isSimpleNumeric())
                     prepareColumns(da,
                                    boolArrs, byteArrs, shortArrs, intArrs, floatArrs, dblArrs,
                                    types, vlens, ind);
               }
               
               for (int j = 0; j < cellSet.getData(0).getNData(); j++)
                  printColumns(outA, nCols, 
                               boolArrs, byteArrs, shortArrs, intArrs, floatArrs, dblArrs,
                               types, vlens, ind);
            }
         }
         outA.close();
      } catch (FileNotFoundException e)
      {
         e.printStackTrace();
      }
   }

   @Override
   public boolean writeField()
   {
      return writeField(irregularInField, params.getFileName(), params.isAscii(), params.isSingleFile());
   }
   
   private void writeDataArrayDescription(String prefix, int maxCmpNameLen, DataArray da)
   {
            headerWriter.printf(Locale.US, "component %" + (maxCmpNameLen + prefix.length()) + "s %7s", 
                                           prefix + da.getName().replace(' ', '_').replace('.', '_'), dataTypes[da.getType()]);
            if (da.getVeclen() > 1)
            {
               if (da.getDims()[0] != da.getVeclen())
               {
                  headerWriter.print(", array " + da.getDims()[0]);
                  if (da.isSymmetric())
                     headerWriter.print(", sym");
               } else
                  headerWriter.print(", vector " + da.getVeclen());
            }
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

   public boolean writeField(IrregularField irregularInField, String fileName, boolean ascii, boolean single)
   {
      if (irregularInField == null)
         return false;
      String outFileName = fileName;
      genFileName = fileName;
      DataArray da;

      if (outFileName.endsWith(".vnf"))
         genFileName = outFileName.substring(0, outFileName.lastIndexOf(".vnf"));
      else
         outFileName = genFileName + ".vnf";
      File outFile = new File(outFileName);

      try
      {
         headerWriter = new PrintWriter(new FileOutputStream(outFile));
         headerWriter.println("#VisNow irregular field");
         if (irregularInField.getName() != null)
            headerWriter.print("field \"" + irregularInField.getName() + "\"");
         headerWriter.print(", nnodes = " + irregularInField.getNNodes());
         if (irregularInField.isMask())
            headerWriter.print(", mask");
         headerWriter.println();
         int maxCmpNameLen = 0;
         for (int i = 0; i < irregularInField.getNData(); i++)
            if (irregularInField.getData(i).isSimpleNumeric() && 
                irregularInField.getData(i).getName().length() > maxCmpNameLen)
               maxCmpNameLen = irregularInField.getData(i).getName().length();
         for (int i = 0; i < irregularInField.getNData(); i++)
         {
            da = irregularInField.getData(i);
            if (da.isSimpleNumeric())
               writeDataArrayDescription("", maxCmpNameLen, da);
         }
         for (CellSet cellSet : irregularInField.getCellSets())
         {
            maxCmpNameLen = 0;
            int nData = 0;
            for (int i = 0; i < cellSet.getNData(); i++)
               if (cellSet.getData(i).isSimpleNumeric())
               {
                  nData += 1;
                  if (cellSet.getData(i).getName().length() > maxCmpNameLen)
                     maxCmpNameLen = cellSet.getData(i).getName().length();
               }
            headerWriter.print("CellSet "+cellSet.getName().replaceAll("\\s", "_").replace('.', '_'));
            if (nData > 0)
               headerWriter.println(", nData " + cellSet.getData(0).getNData());
            else
               headerWriter.println();
            String filler = "             ";
            for (int iCellArr = 0; iCellArr < Cell.TYPES; iCellArr++)
               if (cellSet.getCellArray(iCellArr) != null)
               {
                  CellArray cellArray = cellSet.getCellArray(iCellArr);
                  if (cellArray.getNCells() > 1)
                     headerWriter.printf("%s%s%7d%n", Cell.PLURAL_NAMES[iCellArr], 
                                                      filler.substring(0, 12 - Cell.PLURAL_NAMES[iCellArr].length()), 
                                                      cellArray.getNCells());
                  else
                     headerWriter.printf("%s%s%7d%n", Cell.NAMES[iCellArr], 
                                                      filler.substring(0, 12 - Cell.NAMES[iCellArr].length()), 
                                                      cellArray.getNCells());
               }
            for (int i = 0; i < cellSet.getNData(); i++)
            {
               da = cellSet.getData(i);
               if (da.isSimpleNumeric())
                  writeDataArrayDescription("", maxCmpNameLen, da);
            }
         }
         String sName = outFile.getName();
//         if (irregularInField.getNNodes() < 500)
//            writeASCII(irregularInField, genFileName, headerWriter, sName);
//         else
            writeBinary(irregularInField, genFileName, headerWriter, sName);
         headerWriter.close();
         return true;
      } catch (FileNotFoundException e)
      {
         LOGGER.error("Error writing field", e);
         return false;
      }
   }

}
