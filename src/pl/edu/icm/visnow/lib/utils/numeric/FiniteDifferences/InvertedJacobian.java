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

package pl.edu.icm.visnow.lib.utils.numeric.FiniteDifferences;

import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.lib.utils.numeric.NumericalMethods;

/**
 *
 * @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class InvertedJacobian
{
   private InvertedJacobian(){}
   
   private static class ComputeInvertedJacobian3D implements Runnable
   {

      private int nThreads = 1;
      private int iThread = 0;
      private int[] dims;
      private float[] inFData = null;
      private float[] outFData = null; //ordered: dv0/di0, dv1/di0, dv2/di0, dv0/di1, dv1/di1, dv2/di1, dv0/di2, dv1/di2, dv2/di2 

      public ComputeInvertedJacobian3D(int nThreads, int iThread, int[] dims, float[] inFData, float[] outFData)
      {
         this.nThreads = nThreads;
         this.iThread = iThread;
         this.dims = dims;
         this.inFData = inFData;
         this.outFData = outFData;
      }

      public void run()
      {
         int off1 = 3 * dims[0];
         int off2 = 3 * dims[0] * dims[1];
         float[][] d = new float[3][3];
         float[][] dinv = new float[3][3];
         int dk = dims[2] / nThreads;
         int kstart = iThread * dk + Math.min(iThread, dims[2] % nThreads);
         int kend = (iThread + 1) * dk + Math.min(iThread + 1, dims[2] % nThreads);
         for (int i = kstart; i < kend; i++)
         {
            for (int j = 0, n = 3 * i * off2, m = i * off2; j < dims[1]; j++)
               for (int k = 0; k < dims[0]; k++)
               {
                  for (int l = 0; l < 3; l++, m++)
                  {
                     if (k == 0)
                        d[0][l] = inFData[m + 3] - inFData[m];
                     else if (k == dims[0] - 1)
                        d[0][l] = inFData[m] - inFData[m - 3];
                     else
                        d[0][l] = .5f * (inFData[m + 3] - inFData[m - 3]);
                     if (j == 0)
                        d[1][l] = inFData[m + off1] - inFData[m];
                     else if (j == dims[1] - 1)
                        d[1][l] = inFData[m] - inFData[m - off1];
                     else
                        d[1][l] = .5f * (inFData[m + off1] - inFData[m - off1]);
                     if (i == 0)
                        d[2][l] = inFData[m + off2] - inFData[m];
                     else if (i == dims[2] - 1)
                        d[2][l] = inFData[m] - inFData[m - off2];
                     else
                        d[2][l] = .5f * (inFData[m + off2] - inFData[m - off2]);
                  }
                  NumericalMethods.invert(d, dinv);
                  for (int l = 0; l < 3; l++)
                     for (int p = 0; p < 3; p++, n++)
                        outFData[n] = dinv[l][p];
               }
         }
      }
   }

   private static class ComputeInvertedJacobian2D implements Runnable
   {

      private int nThreads = 1;
      private int iThread = 0;
      private int[] dims;
      private float[] inFData = null;
      private float[] outFData = null; //ordered: dv0/di0, dv1/di0, dv0/di1, dv1/di1

      public ComputeInvertedJacobian2D(int nThreads, int iThread, int[] dims, float[] inFData, float[] outFData)
      {
         this.nThreads = nThreads;
         this.iThread = iThread;
         this.dims = dims;
         this.inFData = inFData;
         this.outFData = outFData;
      }

      public void run()
      {
         float[][] d = new float[2][2];
         float[][] dinv = new float[2][2];
         int off1 = 2 * dims[0];
         int dk = dims[1] / nThreads;
         int kstart = iThread * dk + Math.min(iThread, dims[1] % nThreads);
         int kend = (iThread + 1) * dk + Math.min(iThread + 1, dims[1] % nThreads);
         for (int j = kstart; j < kend; j++)
            for (int k = 0, n = 2 * j * off1, m = j * off1; k < dims[0]; k++)
            {
               for (int l = 0; l < 2; l++, m++)
               {
                  if (k == 0)
                     d[0][l] = inFData[m + 2] - inFData[m];
                  else if (k == dims[0] - 1)
                     d[0][l] = inFData[n] - inFData[m - 2];
                  else
                     d[0][l] = .5f * (inFData[m + 2] - inFData[m - 2]);
                  if (j == 0)
                     d[1][l] = inFData[m + off1] - inFData[m];
                  else if (j == dims[1] - 1)
                     d[1][l] = inFData[m] - inFData[m - off1];
                  else
                     d[1][l] = .5f * (inFData[m + off1] - inFData[m - off1]);
               }
               NumericalMethods.invert(d, dinv);
               for (int l = 0; l < 2; l++)
                  for (int p = 0; p < 2; p++, n++)
                     outFData[n] = dinv[l][p];
            }
      }
   }
   
   public static float[] computeInvertedJacobian(int nThreads, RegularField inField)
   {
      Thread[] workThreads;
      float[] invJacobian = null;
      if (inField == null)
         return null;
      int[] dims = inField.getDims();
      int dim = dims.length;
      float[] coords = inField.getCoords();
      if (coords == null)
      {
         float[][] iA = inField.getInvAffine();
         invJacobian = new float[dim * dim];
         for (int i = 0, k = 0; i < dim; i++)
            for (int j = 0; j < dim; j++, k++)
               invJacobian[k] = iA[i][j];
         return invJacobian;
      }       
      invJacobian = new float[dim * dim * inField.getNNodes()];
      workThreads = new Thread[nThreads];
      switch (dim)
      {
      case 3:
         for (int iThread = 0; iThread < nThreads; iThread++)
         {
            workThreads[iThread] = new Thread(new ComputeInvertedJacobian3D(nThreads, iThread, dims, coords, invJacobian));
            workThreads[iThread].start();
         }
         for (int i = 0; i < workThreads.length; i++)
            try
            {
               workThreads[i].join();
            } catch (Exception e)
            {
            }
         break;
      case 2:
         for (int iThread = 0; iThread < nThreads; iThread++)
         {
            workThreads[iThread] = new Thread(new ComputeInvertedJacobian2D(nThreads, iThread, dims, coords, invJacobian));
            workThreads[iThread].start();
         }
         for (int i = 0; i < workThreads.length; i++)
            try
            {
               workThreads[i].join();
            } catch (Exception e)
            {
            }
         break;
      }
      return invJacobian;
   }
   
}
