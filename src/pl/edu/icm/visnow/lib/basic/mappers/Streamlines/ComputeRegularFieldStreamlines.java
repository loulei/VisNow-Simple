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

package pl.edu.icm.visnow.lib.basic.mappers.Streamlines;


import pl.edu.icm.visnow.lib.utils.numeric.ODE.Deriv;
import pl.edu.icm.visnow.lib.utils.numeric.ODE.RungeKutta;
import pl.edu.icm.visnow.datasets.*;
import pl.edu.icm.visnow.datasets.cells.Cell;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.lib.utils.numeric.NumericalMethods;

/**
 *
 * @author  Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class ComputeRegularFieldStreamlines extends ComputeStreamlines
{
   private RegularField inField = null;
   private int[] dims = null;
   private float[][] affine = null;
   private float[][] invAffine = null;
   private float[] fldCoords = null;
   private float[] pullVects = null;

   /** Creates a new instance of TextureVolRender */
   public ComputeRegularFieldStreamlines(RegularField inField, Params params)
   {
      super(inField, params);
      this.inField = inField;
      dims = inField.getDims();
      affine = inField.getAffine();
      fldCoords = inField.getCoords();
      if (fldCoords == null)
         invAffine = inField.getInvAffine();
      else
      {
         invAffine = new float[4][3];
         for (int i = 0; i < 3; i++)
         {
            for (int j = 0; j < 4; j++)
               invAffine[j][i] = 0;
            invAffine[i][i] = 1;
         }
      }
   }

   @Override
   public void setStartPoints(Field startPoints)
   {
      float[] st = null;
      nSrc = startPoints.getNNodes();
      if (startPoints.getCoords() != null)
         st = startPoints.getCoords();
      else if (startPoints instanceof RegularField)
         st = ((RegularField)startPoints).getCoordsFromAffine();
      if (inField.getCoords() != null && inField.getDims().length == 3)
      {
         startCoords = new float[3 * startPoints.getNNodes()];
         if (inField.getGeoTree() == null)
            inField.createGeoTree();
         for (int i = 0; i < startPoints.getNNodes(); i++)
         {
            float[] p = inField.getFloatIndices(st[3 * i], st[3 * i + 1], st[3 * i + 2]);
            System.arraycopy(p, 0, startCoords, 3 * i, 3);
         }
      }
      else
         startCoords = st;
      fromSteps = new int[nSrc];
      toSteps   = new int[nSrc];
   }
   
   public synchronized void pullVectors()
   {
      vects = inField.getData(params.getVectorComponent()).getFData();
      if (fldCoords == null)
      {
         pullVects = new float[vects.length];
         float[][] iaf = inField.getInvAffine();
         int vl = inField.getData(params.getVectorComponent()).getVeclen();
         for (int i = 0; i < vects.length; i += vl)
            for (int k = 0; k < vl; k++)
            {
               pullVects[i + k] = 0;
               for (int l = 0; l < vl; l++)
                  pullVects[i + k] += iaf[k][l] * vects[i + l];
            }
      }
      else
         pullVects = NumericalMethods.pullVectorField(dims, fldCoords, vects);
   }
   
   @Override
   public synchronized void updateStreamlines()
   {
      nForward = params.getNForwardSteps();
      nBackward = params.getNBackwardSteps();
      pullVectors();
      downCoords = new float[nSrc][trueDim * nBackward];
      upCoords = new float[nSrc][trueDim * nForward];
      downVectors = new float[nSrc][trueDim * nBackward];
      upVectors = new float[nSrc][trueDim * nForward];
      float[][] xt = inField.getExtents();
      eps0 = 0.f;
      for (int i = 0; i < nSpace; i++)
         eps0 += (xt[1][i] - xt[0][i]);
      nThreads = Runtime.getRuntime().availableProcessors();
      Thread[] workThreads = new Thread[nThreads];
      threadProgress = new int[nThreads];
      for (int i = 0; i < nThreads; i++)
         threadProgress[i] = 0;
      for (int n = 0; n < nThreads; n++)
      {
         workThreads[n] = new Thread(new Streamline(n));
         workThreads[n].start();
      }
      for (int i = 0; i < workThreads.length; i++)
         try
         {
            workThreads[i].join();
         } catch (Exception e)
         {
         }
      nvert = nSrc * (nBackward + nForward);
      lines = new int[2 * nSrc * (nBackward + nForward - 1)];
      coords = new float[3 * nvert];
      indices = new int[nvert];
      vectors = new float[3 * nvert];
      for (int i = 0; i < coords.length; i++)
         coords[i] = 0;
      if (fldCoords == null) 
      {
         for (int i = 0; i < nSrc; i++)
         {
            int j = i;
            for (int k = nBackward - 1; k >= 0; k--, j += nSrc)
            {
               for (int l = 0; l < trueDim; l++)
               {
                  coords[3 * j + l] = downCoords[i][trueDim * k + l];
                  vectors[3 * j + l] = downVectors[i][trueDim * k + l];
               }
               indices[j] = -k;
            }
            for (int k = 0; k < nForward; k++, j += nSrc)
            {
               for (int l = 0; l < trueDim; l++)
               {
                  coords[3 * j + l] = upCoords[i][trueDim * k + l];
                  vectors[3 * j + l] = upVectors[i][trueDim * k + l];
               }
               indices[j] = k;
            }
         }
      }
      else
      {
         float u, v, w;
         int i, j, k, l, m, n0 = 3 * dims[0], n1 = 3 * dims[0] * dims[1];
         int inexact;
         for (int n = 0; n < nSrc; n++)
         {  
            int nn = n;
            for (int kk = nBackward - 1; kk >= 0; kk--, nn += nSrc)
            {
               inexact = 0;
               u = downCoords[n][nSpace * kk    ]; if (u < 0) u = 0; if (u >= dims[0]) u = dims[0] - 1;
               v = downCoords[n][nSpace * kk + 1]; if (v < 0) v = 0; if (v >= dims[1]) v = dims[1] - 1;
               w = downCoords[n][nSpace * kk + 2]; if (w < 0) w = 0; if (w >= dims[2]) w = dims[2] - 1;
               i = (int) u; u -= i; if (u != 0 && i < dims[0] - 1) inexact += 1;
               j = (int) v; v -= j; if (v != 0 && j < dims[1] - 1) inexact += 2;
               k = (int) w; w -= k; if (w != 0 && k < dims[2] - 1) inexact += 4;
                m = 3 * ((dims[1] * k + j) * dims[0] + i);
                n0 = 3 * dims[0];
                n1 = 3 * dims[0] * dims[1];
               switch (inexact)
               {
               case 0:
                  for (l = 0; l < 3; l++)
                  {
                     coords[3 * nn + l] = fldCoords[m + l];
                     vectors[3 * nn + l] = vects[m + l];
                  }
                  break;
               case 1:
                  for (l = 0; l < 3; l++)
                  {
                     coords[3 * nn + l] = u * fldCoords[m + l + 3] + (1 - u) * fldCoords[m + l];
                     vectors[3 * nn + l] = u * vects[m + l + 3] + (1 - u) * vects[m + l];
                  }
                  break;
               case 2:
                  for (l = 0; l < 3; l++)
                  {
                     coords[3 * nn + l] = v * fldCoords[m + l + n0] + (1 - v) * fldCoords[m + l];
                     vectors[3 * nn + l] = v * vects[m + l + n0] + (1 - v) * vects[m + l];
                  }
                  break;
               case 3:
                  for (l = 0; l < 3; l++)
                  {
                     coords[3 * nn + l] = v * (u * fldCoords[m + l + n0 + 3] + (1 - u) * fldCoords[m + l + n0])
                             + (1 - v) * (u * fldCoords[m + l + 3] + (1 - u) * fldCoords[m + l]);
                     vectors[3 * nn + l] = v * (u * vects[m + l + n0 + 3] + (1 - u) * vects[m + l + n0])
                             + (1 - v) * (u * vects[m + l + 3] + (1 - u) * vects[m + l]);
                  }
                  break;
               case 4:
                  for (l = 0; l < 3; l++)
                  {
                     coords[3 * nn + l] = w * fldCoords[m + l + n1] + (1 - w) * fldCoords[m + l];
                     vectors[3 * nn + l] = w * vects[m + l + n1] + (1 - w) * vects[m + l];
                  }
                  break;
               case 5:
                  for (l = 0; l < 3; l++)
                  {
                     coords[3 * nn + l] = w * (u * fldCoords[m + l + n1 + 3] + (1 - u) * fldCoords[m + l + n1])
                             + (1 - w) * (u * fldCoords[m + l + 3] + (1 - u) * fldCoords[m + l]);
                     vectors[3 * nn + l] = w * (u * vects[m + l + n1 + 3] + (1 - u) * vects[m + l + n1])
                             + (1 - w) * (u * vects[m + l + 3] + (1 - u) * vects[m + l]);
                  }
                  break;
               case 6:
                  for (l = 0; l < 3; l++)
                  {
                     coords[3 * nn + l] = w * (v * fldCoords[m + l + n1 + n0] + (1 - v) * fldCoords[m + l + n1])
                             + (1 - w) * (v * fldCoords[m + l + n0] + (1 - v) * fldCoords[m + l]);
                     vectors[3 * nn + l] = w * (v * vects[m + l + n1 + n0] + (1 - v) * vects[m + l + n1])
                             + (1 - w) * (v * vects[m + l + n0] + (1 - v) * vects[m + l]);
                  }
                  break;
               case 7:
                  for (l = 0; l < 3; l++)
                  {
                     coords[3 * nn + l] = w * (v * (u * fldCoords[m + l + n1 + n0 + 3] + (1 - u) * fldCoords[m + l + n1 + n0])
                             + (1 - v) * (u * fldCoords[m + l + n1 + 3] + (1 - u) * fldCoords[m + l + n1]))
                             + (1 - w) * (v * (u * fldCoords[m + l + n0 + 3] + (1 - u) * fldCoords[m + l + n0])
                             + (1 - v) * (u * fldCoords[m + l + 3] + (1 - u) * fldCoords[m + l]));
                     vectors[3 * nn + l] = w * (v * (u * vects[m + l + n1 + n0 + 3] + (1 - u) * vects[m + l + n1 + n0])
                             + (1 - v) * (u * vects[m + l + n1 + 3] + (1 - u) * vects[m + l + n1]))
                             + (1 - w) * (v * (u * vects[m + l + n0 + 3] + (1 - u) * vects[m + l + n0])
                             + (1 - v) * (u * vects[m + l + 3] + (1 - u) * vects[m + l]));
                  }
                  break;
               }
               indices[nn] = -kk;
            }
            for (int kk = 0; kk < nForward; kk++, nn += nSrc)
            {
               inexact = 0;
               u = upCoords[n][nSpace * kk    ]; if (u < 0) u = 0; if (u >= dims[0]) u = dims[0] - 1;
               v = upCoords[n][nSpace * kk + 1]; if (v < 0) v = 0; if (v >= dims[1]) v = dims[1] - 1;
               w = upCoords[n][nSpace * kk + 2]; if (w < 0) w = 0; if (w >= dims[2]) w = dims[2] - 1;
               i = (int) u; u -= i; if (u != 0 && i < dims[0] - 1) inexact += 1;
               j = (int) v; v -= j; if (v != 0 && j < dims[1] - 1) inexact += 2;
               k = (int) w; w -= k; if (w != 0 && k < dims[2] - 1) inexact += 4;
               m = 3 * ((dims[1] * k + j) * dims[0] + i);
               switch (inexact)
               {
               case 0:
                  for (l = 0; l < 3; l++)
                  {
                     coords[3 * nn + l] = fldCoords[m + l];
                     vectors[3 * nn + l] = vects[m + l];
                  }
                  break;
               case 1:
                  for (l = 0; l < 3; l++)
                  {
                     coords[3 * nn + l] = u * fldCoords[m + l + 3] + (1 - u) * fldCoords[m + l];
                     vectors[3 * nn + l] = u * vects[m + l + 3] + (1 - u) * vects[m + l];
                  }
                  break;
               case 2:
                  for (l = 0; l < 3; l++)
                  {
                     coords[3 * nn + l] = v * fldCoords[m + l + n0] + (1 - v) * fldCoords[m + l];
                     vectors[3 * nn + l] = v * vects[m + l + n0] + (1 - v) * vects[m + l];
                  }
                  break;
               case 3:
                  for (l = 0; l < 3; l++)
                  {
                     coords[3 * nn + l] = v * (u * fldCoords[m + l + n0 + 3] + (1 - u) * fldCoords[m + l + n0])
                             + (1 - v) * (u * fldCoords[m + l + 3] + (1 - u) * fldCoords[m + l]);
                     vectors[3 * nn + l] = v * (u * vects[m + l + n0 + 3] + (1 - u) * vects[m + l + n0])
                             + (1 - v) * (u * vects[m + l + 3] + (1 - u) * vects[m + l]);
                  }
                  break;
               case 4:
                  for (l = 0; l < 3; l++)
                  {
                     coords[3 * nn + l] = w * fldCoords[m + l + n1] + (1 - w) * fldCoords[m + l];
                     vectors[3 * nn + l] = w * vects[m + l + n1] + (1 - w) * vects[m + l];
                  }
                  break;
               case 5:
                  for (l = 0; l < 3; l++)
                  {
                     coords[3 * nn + l] = w * (u * fldCoords[m + l + n1 + 3] + (1 - u) * fldCoords[m + l + n1])
                             + (1 - w) * (u * fldCoords[m + l + 3] + (1 - u) * fldCoords[m + l]);
                     vectors[3 * nn + l] = w * (u * vects[m + l + n1 + 3] + (1 - u) * vects[m + l + n1])
                             + (1 - w) * (u * vects[m + l + 3] + (1 - u) * vects[m + l]);
                  }
                  break;
               case 6:
                  for (l = 0; l < 3; l++)
                  {
                     coords[3 * nn + l] = w * (v * fldCoords[m + l + n1 + n0] + (1 - v) * fldCoords[m + l + n1])
                             + (1 - w) * (v * fldCoords[m + l + n0] + (1 - v) * fldCoords[m + l]);
                     vectors[3 * nn + l] = w * (v * vects[m + l + n1 + n0] + (1 - v) * vects[m + l + n1])
                             + (1 - w) * (v * vects[m + l + n0] + (1 - v) * vects[m + l]);
                  }
                  break;
               case 7:
                  for (l = 0; l < 3; l++)
                  {
                     coords[3 * nn + l] = w * (v * (u * fldCoords[m + l + n1 + n0 + 3] + (1 - u) * fldCoords[m + l + n1 + n0])
                             + (1 - v) * (u * fldCoords[m + l + n1 + 3] + (1 - u) * fldCoords[m + l + n1]))
                             + (1 - w) * (v * (u * fldCoords[m + l + n0 + 3] + (1 - u) * fldCoords[m + l + n0])
                             + (1 - v) * (u * fldCoords[m + l + 3] + (1 - u) * fldCoords[m + l]));
                     vectors[3 * nn + l] = w * (v * (u * vects[m + l + n1 + n0 + 3] + (1 - u) * vects[m + l + n1 + n0])
                             + (1 - v) * (u * vects[m + l + n1 + 3] + (1 - u) * vects[m + l + n1]))
                             + (1 - w) * (v * (u * vects[m + l + n0 + 3] + (1 - u) * vects[m + l + n0])
                             + (1 - v) * (u * vects[m + l + 3] + (1 - u) * vects[m + l]));
                  }
                  break;
               }
               indices[nn] = -kk;
            }
         }
      }
      downCoords = null;
      upCoords = null;
      downVectors = null;
      upVectors = null;
      for (int i = 0, j = 0; i < nSrc; i++)
         for (int k = 0, l = i *(nBackward + nForward); k < nBackward + nForward-1; k++, j+=2, l++)
         {
            lines[j] = i+k*nSrc;
            lines[j+1] = lines[j] + nSrc;
         }
      boolean[] edgeOrientations = new boolean[lines.length/2];
      for (int i = 0; i < edgeOrientations.length; i++)
         edgeOrientations[i] = true;
      outField = new IrregularField();
      outField.setNNodes(nvert);
      outField.setNSpace(3);
      outField.setCoords(coords);
      DataArray da = DataArray.create(indices, 1, "steps");
      da.setMinv(-nBackward);
      da.setMaxv(nForward);
      outField.addData(da);
      DataArray dn = DataArray.create(vectors, 3, "vectors");
      outField.addData(dn);
      CellArray streamLines = new CellArray(Cell.SEGMENT, lines, edgeOrientations, null);
      CellSet cellSet = new CellSet(inField.getName()+" "+inField.getData(params.getVectorComponent()).getName()+" streamlines");
      cellSet.setBoundaryCellArray(streamLines);
      cellSet.setCellArray(streamLines);
      outField.addCellSet(cellSet);
      outField.setExtents(inField.getExtents());
   }

   
   private class Streamline implements Runnable
   {
      private int nThread;
      private RegularVectInterpolate vInt = new RegularVectInterpolate();
      public Streamline(int nThread)
      {
         this.nThread = nThread;
      }

      @Override
      public void run()
      {
         float[] fs = new float[trueDim];

         for (int n = nThread; n < nSrc; n+=nThreads)
         {
            if (nThread == 0)
               fireStatusChanged((float)n/nSrc);
            try
            {
               System.arraycopy(startCoords, trueDim * n, fs, 0, fs.length);
               fromSteps[n] = RungeKutta.fourthOrderRK(vInt, fs, params.getStep(), nBackward, -1, downCoords[n], downVectors[n]);
               toSteps[n]   = RungeKutta.fourthOrderRK(vInt, fs, params.getStep(), nForward,   1, upCoords[n],   upVectors[n]);
              
            } catch (Exception e)
            {
               System.out.println("null at " +n +" from " +nSrc);
            }
 
         }

      }
   }

   private class RegularVectInterpolate implements Deriv
   {
      @Override
      public float[] derivn(float[] y) throws Exception
      {
         float[] p = new float[y.length];
         float[] q = new float[3];
         for (int i = 0; i < y.length; i++)
            p[i] = y[i] - affine[3][i];
         for (int i = 0; i < dims.length; i++)
         {
            q[i] = 0;
            for (int j = 0; j < p.length; j++)
               q[i] += invAffine[j][i] * p[j];
            if (q[i] < 0 || q[i] >= dims[i])
               return null;
         }
         p = inField.getInterpolatedData(pullVects, q[0], q[1], q[2]);
         return p;
      }
   }
   
}
