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

package pl.edu.icm.visnow.lib.basic.mappers.Isolines;

import pl.edu.icm.visnow.datasets.cells.Cell;
import pl.edu.icm.visnow.datasets.CellArray;
import pl.edu.icm.visnow.datasets.CellSet;
import pl.edu.icm.visnow.datasets.IrregularField;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;

/**
 *
 * @author  Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class CellSetIsolines
{

   protected static final int CHUNK = 1000;
   protected float[] thresholds;
   protected float[] inCoords;
   protected int[] triangles = null;
   protected int[] quads = null;
   protected float[] fData;
   protected int nSpace;
   protected float[][] coords;
   protected int[] inds;
   protected int[] maxinds;
   
   protected int nSegs;
   protected float[] outCoords;
   protected float[] outData;

   public CellSetIsolines(IrregularField inField, CellSet cellSet, int inComponent, float[] thr)
   {
      if (cellSet == null || thr == null || thr.length < 1)
         return;
      DataArray inDA = inField.getData(inComponent);
      if (inDA.getVeclen() == 1)
         fData = inField.getData(inComponent).getFData();
      else
         fData = inField.getData(inComponent).getVectorNorms();
      inCoords = inField.getCoords();
      nSpace = inField.getNSpace();
      thresholds = thr;
      CellArray triangleCellArray = cellSet.getBoundaryCellArray(Cell.TRIANGLE);
      CellArray quadCellArray = cellSet.getBoundaryCellArray(Cell.QUAD);
      nSegs = 0;
      coords = new float[thresholds.length][];
      maxinds = new int[thresholds.length];
      inds = new int[thresholds.length];
      for (int i = 0; i < thresholds.length; i++)
      {
         coords[i] = new float[2 * nSpace * CHUNK];
         maxinds[i] = CHUNK;
         inds[i] = 0;
      }
      if (triangleCellArray != null && triangleCellArray.getNodes() != null)
      {
         triangles = triangleCellArray.getNodes();
         for (int i = 0; i < triangles.length; i += 3)
            processTriangle(triangles[i], triangles[i + 1], triangles[i + 2]);
      }
      if (quadCellArray != null && quadCellArray.getNodes() != null)
      {
         quads = quadCellArray.getNodes();
         for (int i = 0; i < quads.length; i += 4)
         {
            processTriangle(quads[i], quads[i + 1], quads[i + 2]);
            processTriangle(quads[i], quads[i + 2], quads[i + 3]);
         }
      }
      outCoords = new float[2 * nSegs * nSpace];
      outData = new float[2 * nSegs];
      int lc = 0, le = 0;
      for (int i = 0; i < coords.length; i++)
      {
         float[] tcoords = coords[i];
         for (int j = 0; j < 2 * nSpace * inds[i]; j++, lc++)
            outCoords[lc] = tcoords[j];
         for (int j = 0; j < 2 * inds[i]; j++, le++)
            outData[le] = thresholds[i];
      }
   }

   private void processTriangle(int p0, int p1, int p2)
   {
      float[] tcoords;
      float v0 = fData[p0];
      float v1 = fData[p1];
      float v2 = fData[p2];
      if (v0 > v1)
      {
         int p = p0;
         p0 = p1;
         p1 = p;
         v0 = fData[p0];
         v1 = fData[p1];
      }
      if (v0 > v2)
      {
         int p = p0;
         p0 = p2;
         p2 = p1;
         p1 = p;
      } else if (v1 > v2)
      {
         int p = p1;
         p1 = p2;
         p2 = p;
      }
      v0 = fData[p0];
      v1 = fData[p1];
      v2 = fData[p2];

      for (int j = 0; j < thresholds.length; j++)
      {
         float t = thresholds[j];
         if (t <= v0 || t >= v2)
            continue;
         int n = inds[j];
         if (n >= maxinds[j])
         {
            tcoords = coords[j];
            coords[j] = new float[2 * nSpace * (maxinds[j] + CHUNK)];
            for (int k = 0; k < tcoords.length; k++)
               coords[j][k] = tcoords[k];
            maxinds[j] += CHUNK;
         }
         tcoords = coords[j];
         if (t <= v1 && t > v0)
         {
            float u = (t - v0) / (v1 - v0);
            float v = (t - v0) / (v2 - v0);
            for (int k = 0; k < nSpace; k++)
            {
               tcoords[2 * nSpace * n + k] = u * inCoords[nSpace * p1 + k] + (1 - u) * inCoords[nSpace * p0 + k];
               tcoords[2 * nSpace * n + nSpace + k] = v * inCoords[nSpace * p2 + k] + (1 - v) * inCoords[nSpace * p0 + k];
            }
         } else if (t > v1 && t < v2)
         {
            float u = (t - v1) / (v2 - v1);
            float v = (t - v0) / (v2 - v0);
            for (int k = 0; k < nSpace; k++)
            {
               tcoords[2 * nSpace * n + k] = u * inCoords[nSpace * p2 + k] + (1 - u) * inCoords[nSpace * p1 + k];
               tcoords[2 * nSpace * n + nSpace + k] = v * inCoords[nSpace * p2 + k] + (1 - v) * inCoords[nSpace * p0 + k];
            }
         }
         inds[j] += 1;
         nSegs += 1;
      }
   }

   public int getnSegs()
   {
      return nSegs;
   }

   public float[] getOutData()
   {
      return outData;
   }

   public float[] getCoords()
   {
      return outCoords;
   }

}
