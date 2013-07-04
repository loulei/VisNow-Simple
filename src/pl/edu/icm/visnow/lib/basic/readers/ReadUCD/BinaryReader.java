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

package pl.edu.icm.visnow.lib.basic.readers.ReadUCD;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import pl.edu.icm.visnow.datasets.CellArray;
import pl.edu.icm.visnow.datasets.CellSet;
import pl.edu.icm.visnow.datasets.IrregularField;
import pl.edu.icm.visnow.datasets.cells.Cell;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.gui.widgets.FileErrorFrame;

/**
 *
 * @author know
 */
public class BinaryReader extends Reader
{
   
   private DataArray[] readDataArrays(ImageInputStream inStream, int nData)
   {
      try
      {
         byte[] dataLabels = new byte[1024];
         inStream.readFully(dataLabels);
         String[] labels = new String(dataLabels).split("\\.");
         byte[] dataUnits = new byte[1024];
         inStream.readFully(dataUnits);
         String[] units = new String(dataUnits).split("\\.");
         int nDataArrays = inStream.readInt();
         DataArray[] dataArrays = new DataArray[nDataArrays];
         int[] vlens = new int[nDataArrays];
         inStream.readFully(vlens, 0, vlens.length);
         for (int i = 0; i < nDataArrays; i++)
             System.out.printf("%20s %20s %3d%n", labels[i], units[i], vlens[i]);
         float[][] arrays = new float[nDataArrays][];
         for (int i = 0; i < nDataArrays; i++)
         {
            arrays[i] = new float[vlens[i] * nData];
            inStream.readFully(arrays[i], 0, arrays[i].length);
         }
         
         for (int i = 0; i < nDataArrays; i++)
            dataArrays[i] = DataArray.create(arrays[i], vlens[i], labels[i], units[i], null);
         return dataArrays;
      } catch (IOException iOException)
      {
         iOException.printStackTrace();
      }
      return null;
   }
   
   public IrregularField readUCD(Params params, FileErrorFrame errorFrame)
   {
      IrregularField outField = null;
      int currentLine = 0;
      String fn = "";
      String filename = params.getFileName();
      try
      {
         fn = new File(filename).getName();
         ImageInputStream inStream = new FileImageInputStream(new File(filename));
         inStream.skipBytes(1);
         int nnodes = inStream.readInt();
         int ncells = inStream.readInt();
         int nnodedata = inStream.readInt();
         int ncelldata = inStream.readInt();
         int nListNodes = inStream.readInt();
         outField = new IrregularField();
         outField.setNNodes(nnodes);
         outField.setNSpace(3);
         
         int[] cellids      = new int[ncells];
         int[] cellmats     = new int[ncells];
         int[] celltypes    = new int[ncells];
         int[] ncellsoftype = new int[Cell.TYPES];
         int maxMat = 0;
         for (int i = 0; i < ncells; i++)
         {
            cellids[i] = inStream.readInt();
            cellmats[i] = inStream.readInt();
            if (cellmats[i] > maxMat)
               maxMat = cellmats[i];
            inStream.skipBytes(4);
            celltypes[i] = inStream.readInt();
         }
         maxMat += 1;
         int[][] cellTypeNumbers = new int[maxMat][Cell.TYPES];
         for (int i = 0; i < cellTypeNumbers.length; i++)
            for (int j = 0; j < cellTypeNumbers[i].length; j++)
               cellTypeNumbers[i][j] = 0;
         int[][] cellArrays = new int[Cell.TYPES][];
         int[][] cellIndices = new int[Cell.TYPES][];
         int[] cellArrayIndices = new int[Cell.TYPES];
         for (int i = 0; i < ncells; i++)
            cellTypeNumbers[cellmats[i]][celltypes[i]] +=  1;
         int[] listNodes = new int[nListNodes];
         inStream.readFully(listNodes, 0, listNodes.length);
         float[] coords = new float[3 * nnodes];
         float[] c = new float[nnodes];
         inStream.readFully(c, 0, c.length);
         for (int i = 0; i < c.length; i++)
            coords[3 * i] = c[i];
         inStream.readFully(c, 0, c.length);
         for (int i = 0; i < c.length; i++)
            coords[3 * i + 1] = c[i];
         inStream.readFully(c, 0, c.length);
         for (int i = 0; i < c.length; i++)
            coords[3 * i + 2] = c[i];
         outField.setCoords(coords);
         DataArray[] dataArrays =  null;
         if (nnodedata >0)
         {
            dataArrays = readDataArrays(inStream, nnodes);
            for (int i = 0; i < dataArrays.length; i++)
               outField.addData(dataArrays[i]);
         }
         int[] tNodes = new int[8];
         if (params.materialsAsSets())
         {
            
         }
         else
         {
            for (int i = 0; i < Cell.TYPES; i++)
            {
               int n = 0;
               for (int j = 0; j < cellTypeNumbers.length; j++)
                  n += cellTypeNumbers[j][i];
               cellArrayIndices[i] = 0;
               cellArrays[i] = null;
               if (n == 0)
                  continue;
               cellArrays[i] = new int[n * Cell.nv[i]];
               cellIndices[i] = new int[n];
            }
            for (int i = 0, j = 0; i < ncells; i++)
            {
               int type = celltypes[i];
               int nv = Cell.nv[type];
               int m = cellArrayIndices[type];
               for (int k = 0; k < tNodes.length; k++)
                   tNodes[k] = -1;
               for (int l = 0; l < nv; l++, j++)
                  tNodes[UCDnodeOrders[type][l]] = listNodes[j] - 1;
               System.arraycopy(tNodes, 0, cellArrays[type], m * nv, nv);
               cellIndices[type][m] = i;
               cellArrayIndices[type] = m + 1;
            }
            CellSet cs = new CellSet();
            for (int i = 0; i < Cell.TYPES; i++)
               if (cellArrays[i] != null)
               {
                  boolean[] orientations = new boolean[cellIndices[i].length];
                  for (int j = 0; j < orientations.length; j++)
                     orientations[j] = true;
                  cs.setCellArray(new CellArray(i, cellArrays[i], orientations, cellIndices[i]));
               }
            if (ncelldata >0)
            {
               dataArrays = readDataArrays(inStream, ncells);
               for (int i = 0; i < dataArrays.length; i++)
                  cs.addData(dataArrays[i]);
            }
            cs.generateDisplayData(coords);
            outField.addCellSet(cs);
         }
         outField.setName(fn);
      } catch (FileNotFoundException e)
      {
         System.out.println("could not open file " + filename);
      } catch (Exception e)
      {
         errorFrame.setErrorData("Error parsing UCD file ", filename, currentLine, e);
         return null;
      }
      return outField;
   }
   
}
