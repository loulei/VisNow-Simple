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
package pl.edu.icm.visnow.lib.basic.readers.ReadUCD;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.URL;
import java.net.URLConnection;
import pl.edu.icm.visnow.datasets.CellArray;
import pl.edu.icm.visnow.datasets.CellSet;
import pl.edu.icm.visnow.datasets.IrregularField;
import pl.edu.icm.visnow.datasets.cells.Cell;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.gui.widgets.FileErrorFrame;
import pl.edu.icm.visnow.lib.utils.io.InputSource;

/**
 *
 * @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class ASCIIReader extends Reader
{

   public ASCIIReader()
   {

   }

   public IrregularField readTimeUCD(String filename, FileErrorFrame errorFrame)
   {
      return null;
   }

   @Override
   public IrregularField readUCD(Params params, FileErrorFrame errorFrame)
   {
      String filename = params.getFileName();
      String line = null;
      String[] tokens = null;
      int startCellsLine = 0;
      int currentLine = 0;
      LineNumberReader r = null;
      IrregularField outField = null;
      try
      {
         File inFile = new File(filename);
         switch (params.getSource())
         {
            case InputSource.FILE:
               r = new LineNumberReader(new FileReader(new File(filename)));
               break;
            case InputSource.URL:
               URL url = new URL(filename);
               URLConnection urlConnection = url.openConnection();
               r = new LineNumberReader(new InputStreamReader(urlConnection.getInputStream()));
               break;
            case InputSource.GRID:
               r = new LineNumberReader(new FileReader(new File(filename)));
               break;
         }
         tokens = inFile.getName().split("\\\\|/|[.]");
         String fn = "ucd";
         if (tokens.length > 1)
            fn = tokens[tokens.length - 2];
         line = r.readLine().trim();
         while (line.charAt(0) == '#')
            line = r.readLine().trim();
         tokens = line.split("\\s+");
         int nnodes = Integer.parseInt(tokens[0]);
         int ncells = Integer.parseInt(tokens[1]);
         int nnodedata = Integer.parseInt(tokens[2]);
         int ncelldata = Integer.parseInt(tokens[3]);
         outField = new IrregularField(nnodes);
         outField.setNSpace(3);
         int[] ind = new int[nnodes];
         float[] coords = new float[3 * nnodes];
         int maxNode = 0;
         for (int i = 0; i < nnodes; i++)
         {
            line = r.readLine().trim();
            tokens = line.split("\\s+");
            ind[i] = Integer.parseInt(tokens[0]);
            if (ind[i] > maxNode)
               maxNode = ind[i];
            coords[3 * i] = Float.parseFloat(tokens[1]);
            coords[3 * i + 1] = Float.parseFloat(tokens[2]);
            coords[3 * i + 2] = Float.parseFloat(tokens[3]);
         }
         int[] nodeInd = new int[maxNode + 1];
         for (int i = 0; i < nodeInd.length; i++)
            nodeInd[i] = -1;
         for (int i = 0; i < nnodes; i++)
            nodeInd[ind[i]] = i;
         outField.setCoords(coords);
         startCellsLine = r.getLineNumber();
         int[] ncellsoftype = new int[Cell.TYPES];
         for (int i = 0; i < ncells; i++)
         {
            line = r.readLine().trim();
            currentLine = r.getLineNumber();
            tokens = line.split("\\s+");
            int n = -1;
            for (int j = 0; j < Cell.TYPES; j++)
               if (tokens[2].compareTo(Cell.UCDnames[j]) == 0)
               {
                  n = j;
                  ncellsoftype[n] += 1;
                  break;
               }
            if (n == -1)
            {
               errorFrame.setErrorData("Error parsing UCD file: unknown type code of cell " + tokens[2],
                       filename, currentLine, new Exception());
               return null;
            }
         }
         int[][] nodes = new int[Cell.TYPES][];
         boolean[][] orientations = new boolean[Cell.TYPES][];
         int[][] dataIndices = new int[Cell.TYPES][];
         int[][] mat = new int[Cell.TYPES][];
         CellArray[] cells = new CellArray[Cell.TYPES];
         CellSet cellSet = new CellSet(fn);
         for (int i = 0; i < Cell.TYPES; i++)
            if (ncellsoftype[i] > 0)
            {
               nodes[i] = new int[Cell.nv[i] * ncellsoftype[i]];
               orientations[i] = new boolean[ncellsoftype[i]];
               dataIndices[i] = new int[ncellsoftype[i]];
               mat[i] = new int[ncellsoftype[i]];
            }
         r.close();
         switch (params.getSource())
         {
            case InputSource.FILE:
               r = new LineNumberReader(new FileReader(new File(filename)));
               break;
            case InputSource.URL:
               URL url = new URL(filename);
               URLConnection urlConnection = url.openConnection();
               r = new LineNumberReader(new InputStreamReader(urlConnection.getInputStream()));
               break;
            case InputSource.GRID:
               r = new LineNumberReader(new FileReader(new File(filename)));
               break;
         }
         for (int i = 0; i < startCellsLine; i++)
            line = r.readLine();
         for (int j = 0; j < Cell.TYPES; j++)
            ncellsoftype[j] = 0;
         for (int i = 0; i < ncells; i++)
         {
            line = r.readLine().trim();
            currentLine = r.getLineNumber();
            tokens = line.split("\\s+");
            int type = 0;
            for (int j = 0; j < Cell.TYPES; j++)
               if (tokens[2].compareTo(Cell.UCDnames[j]) == 0)
               {
                  type = j;
                  int k = ncellsoftype[type];
                  mat[type][k] = Integer.parseInt(tokens[1]);
                  int n = Cell.nv[type];
                  int[] v = new int[n];
                  for (int l = 0; l < n; l++)
                     v[UCDnodeOrders[type][l]] = nodeInd[Integer.parseInt(tokens[l + 3])];
                  orientations[type][k] = stdCells[type].normalize(v);
                  if (n > Cell.dim[type])
                  {
                     int ior = stdCells[type].geomOrientation(coords, v);
                     if (ior > 0)
                        orientations[type][k] = true;
                     else if (ior < 0)
                        orientations[type][k] = false;
                  }
                  System.arraycopy(v, 0, nodes[type], n * k, n);
                  dataIndices[type][k] = i;
                  ncellsoftype[type] += 1;
                  break;
               }
         }
         int nCellsInSet = 0;
         for (int i = 0; i < Cell.TYPES; i++)
            if (ncellsoftype[i] > 0)
            {
               cells[i] = new CellArray(i, nodes[i], orientations[i], dataIndices[i]);
               cellSet.setCellArray(cells[i]);
               nCellsInSet += orientations[i].length;
            }
         cellSet.setNCells(nCellsInSet);
         if (nnodedata > 0)
         {
            line = r.readLine().trim();
            currentLine = r.getLineNumber();
            tokens = line.split("\\s+");
            int ndatacomps = Integer.parseInt(tokens[0]);
            int[] vlens = new int[ndatacomps];
            float[][] data = new float[ndatacomps][];
            DataArray[] dataarrays = new DataArray[ndatacomps];
            int n = 0;
            for (int i = 0; i < ndatacomps; i++)
            {
               vlens[i] = Integer.parseInt(tokens[i + 1]);
               n += vlens[i];
            }
            if (n != nnodedata)
            {
               System.out.println("number of node data components mismatch");
               return null;
            }
            for (int i = 0; i < ndatacomps; i++)
            {
               line = r.readLine().trim();
               currentLine = r.getLineNumber();
               tokens = line.split(", *");
               data[i] = new float[vlens[i] * nnodes];
               dataarrays[i] = DataArray.create(data[i], vlens[i], tokens[0]);
               if (tokens.length > 1)
                  dataarrays[i].setUnit(tokens[1]);
            }
            for (int j = 0; j < nnodes; j++)
            {
               line = r.readLine().trim();
               currentLine = r.getLineNumber();
               tokens = line.split("\\s+");
               int l = nodeInd[Integer.parseInt(tokens[0])];
               for (int i = 0, m = 1; i < ndatacomps; i++)
               {
                  int vlen = vlens[i];
                  for (int k = 0; k < vlen; k++, m++)
                     data[i][vlen * l + k] = Float.parseFloat(tokens[m]);
               }
            }
            for (int i = 0; i < ndatacomps; i++)
            {
               dataarrays[i].recomputeMinMax();
               outField.addData(dataarrays[i]);
            }
         }
         if (ncelldata > 0)
         {
            line = r.readLine().trim();
            currentLine = r.getLineNumber();
            tokens = line.split("\\s+");
            int ndatacomps = Integer.parseInt(tokens[0]);
            int[] vlens = new int[ndatacomps];
            float[][] data = new float[ndatacomps][];
            DataArray[] dataarrays = new DataArray[ndatacomps];
            int n = 0;
            for (int i = 0; i < ndatacomps; i++)
            {
               vlens[i] = Integer.parseInt(tokens[i + 1]);
               n += vlens[i];
            }
            if (n != ncelldata)
            {
               System.out.println("number of node data components mismatch");
               return null;
            }
            for (int i = 0; i < ndatacomps; i++)
            {
               line = r.readLine().trim();
               currentLine = r.getLineNumber();
               tokens = line.split(", *");
               data[i] = new float[vlens[i] * ncells];
               dataarrays[i] = DataArray.create(data[i], vlens[i], tokens[0]);
               if (tokens.length > 1)
                  dataarrays[i].setUnit(tokens[1]);
            }
            for (int j = 0; j < ncells; j++)
            {
               line = r.readLine().trim();
               currentLine = r.getLineNumber();
               tokens = line.split("\\s+");
               for (int i = 0, m = 1; i < ndatacomps; i++)
               {
                  int vlen = vlens[i];
                  for (int k = 0; k < vlen; k++, m++)
                     data[i][vlen * j + k] = Float.parseFloat(tokens[m]);
               }
            }
            for (int i = 0; i < ndatacomps; i++)
            {
               dataarrays[i].recomputeMinMax();
               cellSet.addData(dataarrays[i]);
            }

         }
         
         if (params.addIndices())
         {
            int[] indices = new int[ncells];
            for (int i = 0; i < indices.length; i++)
               indices[i] = i;
            cellSet.addData(DataArray.create(indices, 1, "indices"));
         }

//         if (params.addOrientations())
//         {
//            int[] ors = new int[ncells];
//            for (int i = 0, k = 0; i < Cell.TYPES; i++)
//            {
//               CellArray ca = cellSet.getCellArray(i);
//               if (ca != null)
//                  for (int j = 0; j < ca.getNCells(); j++)
//                  {
//                     int or = ca.getCell(i).geomOrientation(coords);
//                     if (orientations[i][j])
//                        ors[k] = or;
//                     else
//                        ors[k] = -or;
//                  }
//            }
//            cellSet.addData(DataArray.create(ors, 1, "orientations"));
//         }
         outField.addCellSet(cellSet);
         cellSet.generateDisplayData(coords);
         outField.setName(fn);
      } catch (FileNotFoundException e)
      {
         System.out.println("could not open file " + filename);
      } catch (Exception e)
      {
         errorFrame.setErrorData("Error parsing UCD file ", filename, currentLine, e);
         e.printStackTrace();
         return null;
      }
      return outField;
   }

}
