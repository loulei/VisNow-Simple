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
import pl.edu.icm.visnow.datasets.CellArray;
import pl.edu.icm.visnow.datasets.CellSet;
import pl.edu.icm.visnow.datasets.IrregularField;
import pl.edu.icm.visnow.datasets.cells.Cell;
import pl.edu.icm.visnow.datasets.cells.SimplexPosition;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class ComputeIrregularFieldStreamlines extends ComputeStreamlines
{
   private IrregularField inField = null;
 
   public ComputeIrregularFieldStreamlines(IrregularField inField, Params params)
   {
      super(inField, params);
      this.inField = inField;
      if (inField.getGeoTree() == null)
      {
         System.out.println("creating cell tree");
         long start = System.currentTimeMillis();
         inField.createGeoTree();
         System.out.println("cell tree created in " + ((float)(System.currentTimeMillis() -start)) / 1000 + "seconds");
      }
   }

   
   @Override
   public synchronized void updateStreamlines()
   {
      nForward = params.getNForwardSteps();
      nBackward = params.getNBackwardSteps();
      nSpace = inField.getNSpace();

      downCoords = new float[nSrc][3*nBackward];
      upCoords = new float[nSrc][3*nForward];
      downVectors = new float[nSrc][3*nBackward];
      upVectors = new float[nSrc][3*nForward];
      float[][] xt = inField.getExtents();
      nThreads = Runtime.getRuntime().availableProcessors();
      Thread[] workThreads = new Thread[nThreads];
      threadProgress = new int[nThreads];
      for (int i = 0; i < nThreads; i++)
         threadProgress[i] = 0;
      for (int iThread = 0; iThread < nThreads; iThread++)
      {
         workThreads[iThread] = new Thread(new Streamline(iThread));
         workThreads[iThread].start();
      }
      for (int i = 0; i < workThreads.length; i++)
         try {workThreads[i].join();}
         catch (Exception e) {}
      nvert = nSrc * (nBackward + nForward);
      lines = new int[2*nSrc*(nBackward + nForward-1)];
      coords = new float[3 * nvert];
      indices = new int[nvert];
      vectors = new float[3 * nvert];
      for (int i = 0; i < coords.length; i++)
      coords[i] = 0;
      for (int i = 0; i < nSrc; i++)
      {
         int j = i;
         for (int k = nBackward - 1; k >= 0; k--, j += nSrc)
         {
            for (int l = 0; l < nSpace; l++)
            {
               coords[3 * j + l] = downCoords[i][nSpace * k + l];
               vectors[j] = downVectors[i][k];
            }
            indices[j] = -k;
         }
         for (int k = 0; k < nForward; k++, j += nSrc)
         {
            for (int l = 0; l < nSpace; l++)
            {
               coords[3 * j + l] = upCoords[i][nSpace * k + l];
               vectors[j] = upVectors[i][k];
            }
            indices[j] = k;
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
      private int iThread;
      private SimplexPosition interp = new SimplexPosition();
      private IrregularVectInterpolate vInt = new IrregularVectInterpolate(inField, interp);
      
      public Streamline(int iThread)
      {
         this.iThread        = iThread;
      }

      public void run()
      {
         float[] fs = new float[trueDim];
         int dk = nSrc/nThreads;
         int kstart = iThread       * dk + Math.min(iThread,     nSrc % nThreads);
         int kend   = (iThread + 1) * dk + Math.min(iThread + 1, nSrc % nThreads);
         for (int n = kstart ; n < kend; n++)
         {
            if (iThread == 0)
               fireStatusChanged((float)n/nSrc);
            try
            {
               System.arraycopy(startCoords, trueDim * n, fs, 0, fs.length);
               fromSteps[n] = RungeKutta.fourthOrderRK(vInt, fs, params.getStep(), nBackward, -1, downCoords[n], downVectors[n]);
               toSteps[n]   = RungeKutta.fourthOrderRK(vInt, fs, params.getStep(), nForward, 1, upCoords[n], upVectors[n]);
            } catch (Exception e)
            {
               System.out.println("null at " +n +" from " +nSrc);
            }
         }
      }
   }

   private class IrregularVectInterpolate implements Deriv
   {
      private IrregularField fld;
      private SimplexPosition interp;

      public IrregularVectInterpolate(IrregularField fld, SimplexPosition interp)
      {
         this.fld = fld;
         this.interp = interp;
      }
      
      @Override
      public float[] derivn(float[] y) throws Exception
      {
         float[] p = new float[y.length];
         if (fld.getFieldCoords(y, interp))
         {
            float[] q = new float[] {0,0,0};
            for (int i = 0; i < 4; i++)
            {
               int k = vlen * interp.verts[i];
               for (int j = 0; j < vlen; j++)
                  q[j] += interp.coords[i] * vects[k + j];
            }
            return q;
         }
         else
            return null;
      }
   }
   
}
