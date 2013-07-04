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

/**
 *
 * @author know
 */
public class Derivatives
{
   private Derivatives(){}

   private static class Compute3DScalar implements Runnable
   {

      private int nThreads = 1;
      private int iThread = 0;
      private int[] dims;
      private float[] invJacobian;
      private float[] inFData = null;
      private float[] outFData = null;

      public Compute3DScalar(int nThreads, int iThread, int[] dims, float[] invJacobian, float[] inFData, float[] outFData)
      {
         this.nThreads = nThreads;
         this.iThread  = iThread;
         this.dims = dims;
         this.invJacobian = invJacobian;
         this.inFData  = inFData;
         this.outFData = outFData;
      }

      public void run()
      {
         int m = dims[0] * dims[1];
         float[] v = new float[3];
         float[] w = new float[3];
         int dk = dims[2] / nThreads;
         int kstart = iThread * dk + Math.min(iThread, dims[2] % nThreads);
         int kend = (iThread + 1) * dk + Math.min(iThread + 1, dims[2] % nThreads);
         for (int i = kstart; i < kend; i++)
            for (int j = 0, n = i * m; j < dims[1]; j++)
               for (int k = 0; k < dims[0]; k++, n++)
               {
                  if (k == 0)
                     v[0] = inFData[n + 1] - inFData[n];
                  else if (k == dims[0] - 1)
                     v[0] = inFData[n] - inFData[n - 1];
                  else
                     v[0] = .5f * (inFData[n + 1] - inFData[n - 1]);
                  if (j == 0)
                     v[1] = inFData[n + dims[0]] - inFData[n];
                  else if (j == dims[1] - 1)
                     v[1] = inFData[n] - inFData[n - dims[0]];
                  else
                     v[1] = .5f * (inFData[n + dims[0]] - inFData[n - dims[0]]);
                  if (i == 0)
                     v[2] = inFData[n + m] - inFData[n];
                  else if (i == dims[2] - 1)
                     v[2] = inFData[n] - inFData[n - m];
                  else
                     v[2] = .5f * (inFData[n + m] - inFData[n - m]);
                  int l = 0;
                  if (invJacobian.length > 9)
                     l = 9 * n;
                  for (int p = 0; p < 3; p++)
                  {
                     w[p] = 0;
                     for (int q = 0; q < 3; q++)
                        w[p] += invJacobian[l + 3 * p + q] * v[q];
                  }
                  System.arraycopy(w, 0, outFData, 3 * n, 3);
               }
      }
   }

   private static class Compute3DVector implements Runnable
   {

      private int nThreads = 1;
      private int iThread = 0;
      private int[] dims;
      private float[] invJacobian;
      private float[] inFData = null;
      private float[] outFData = null; //ordered: dv0/di0, dv1/di0, dv2/di0, dv0/di1, dv1/di1, dv2/di1, dv0/di2, dv1/di2, dv2/di2 

      public Compute3DVector(int nThreads, int iThread, int[] dims, float[] invJacobian, float[] inFData, float[] outFData)
      {
         this.nThreads = nThreads;
         this.iThread = iThread;
         this.dims = dims;
         this.invJacobian = invJacobian;
         this.inFData = inFData;
         this.outFData = outFData;
      }

      public void run()
      {
         int off1 = 3 * dims[0];
         int off2 = 3 * dims[0] * dims[1];
         float[] v = new float[3];
         float[] w = new float[3];
         int dk = dims[2] / nThreads;
         int kstart = iThread * dk + Math.min(iThread, dims[2] % nThreads);
         int kend = (iThread + 1) * dk + Math.min(iThread + 1, dims[2] % nThreads);
         for (int i = kstart; i < kend; i++)
         {
            for (int j = 0, n = 3 * i * off2, m = i * off2; j < dims[1]; j++)
               for (int k = 0; k < dims[0]; k++, n += 9)
                  for (int l = 0; l < 3; l++, m++)
                  {
                     if (k == 0)
                        v[0] = inFData[m + 3] - inFData[m];
                     else if (k == dims[0] - 1)
                        v[0] = inFData[m] - inFData[m - 3];
                     else
                        v[0] = .5f * (inFData[m + 3] - inFData[m - 3]);
                     if (j == 0)
                        v[1] = inFData[m + off1] - inFData[m];
                     else if (j == dims[1] - 1)
                        v[1] = inFData[m] - inFData[m - off1];
                     else
                        v[1] = .5f * (inFData[m + off1] - inFData[m - off1]);
                     if (i == 0)
                        v[2] = inFData[m + off2] - inFData[m];
                     else if (i == dims[2] - 1)
                        v[2] = inFData[m] - inFData[m - off2];
                     else
                        v[2] = .5f * (inFData[m + off2] - inFData[m - off2]);
                     int s = 0;
                     if (invJacobian.length > 9)
                        s = n;
                     for (int p = 0; p < 3; p++)
                     {
                        w[p] = 0;
                        for (int q = 0; q < 3; q++)
                           w[p] += invJacobian[s + 3 * p + q] * v[q];
                        outFData[n + l + 3 * p] = w[p];
                     }
                  }
         }
      }
   }

   private static class Compute2DScalar implements Runnable
   {

      private int nThreads = 1;
      private int iThread = 0;
      private int[] dims;
      private float[] invJacobian;
      private float[] inFData = null;
      private float[] outFData = null;

      public Compute2DScalar(int nThreads, int iThread, int[] dims, float[] invJacobian, float[] inFData, float[] outFData)
      {
         this.nThreads = nThreads;
         this.iThread = iThread;
         this.dims = dims;
         this.invJacobian = invJacobian;
         this.inFData = inFData;
         this.outFData = outFData;
      }

      public void run()
      {
         int m = dims[0];
         float[] v = new float[2];
         float[] w = new float[2];
         int dk = dims[1] / nThreads;
         int kstart = iThread * dk + Math.min(iThread, dims[1] % nThreads);
         int kend = (iThread + 1) * dk + Math.min(iThread + 1, dims[1] % nThreads);
         for (int j = kstart; j < kend; j++)
            for (int k = 0, n = j * m; k < dims[0]; k++, n++)
            {
               if (k == 0)
                  v[0] = inFData[n + 1] - inFData[n];
               else if (k == dims[0] - 1)
                  v[0] = inFData[n] - inFData[n - 1];
               else
                  v[0] = .5f * (inFData[n + 1] - inFData[n - 1]);
               if (j == 0)
                  v[1] = inFData[n + m] - inFData[n];
               else if (j == dims[1] - 1)
                  v[1] = inFData[n] - inFData[n - m];
               else
                  v[1] = .5f * (inFData[n + m] - inFData[n - m]);
               int l = 0;
               if (invJacobian.length > 4)
                  l = 4 * n;
               for (int p = 0; p < 2; p++)
               {
                  w[p] = 0;
                  for (int q = 0; q < 2; q++)
                     w[p] += invJacobian[l + 2 * p + q] * v[q];
               }
               System.arraycopy(w, 0, outFData, 2 * n, 2);
            }
      }
   }

   private static class Compute2DVector implements Runnable
   {

      private int nThreads = 1;
      private int iThread = 0;
      private int[] dims;
      private float[] invJacobian;
      private float[] inFData = null;
      private float[] outFData = null; //ordered: dv0/di0, dv1/di0, dv2/di0, dv0/di1, dv1/di1, dv2/di1, dv0/di2, dv1/di2, dv2/di2 

      public Compute2DVector(int nThreads, int iThread, int[] dims, float[] invJacobian, float[] inFData, float[] outFData)
      {
         this.nThreads = nThreads;
         this.iThread = iThread;
         this.dims = dims;
         this.invJacobian = invJacobian;
         this.inFData = inFData;
         this.outFData = outFData;
      }

      public void run()
      {
         int off1 = 2 * dims[0];
         float[] v = new float[2];
         float[] w = new float[2];
         int dk = dims[1] / nThreads;
         int kstart = iThread * dk + Math.min(iThread, dims[1] % nThreads);
         int kend = (iThread + 1) * dk + Math.min(iThread + 1, dims[1] % nThreads);
         for (int j = kstart; j < kend; j++)
            for (int k = 0, n = 2 * j * off1, m = j * off1; k < dims[0]; k++, n += 4)
               for (int l = 0; l < 2; l++, m++)
               {
                  if (k == 0)
                     v[0] = inFData[m + 2] - inFData[m];
                  else if (k == dims[0] - 1)
                     v[0] = inFData[m] - inFData[m - 2];
                  else
                     v[0] = .5f * (inFData[m + 2] - inFData[m - 2]);
                  if (j == 0)
                     v[1] = inFData[m + off1] - inFData[m];
                  else if (j == dims[1] - 1)
                     v[1] = inFData[m] - inFData[m - off1];
                  else
                     v[1] = .5f * (inFData[m + off1] - inFData[m - off1]);
                  int s = 0;
                  if (invJacobian.length > 4)
                     s = n;
                  for (int p = 0; p < 2; p++)
                  {
                     w[p] = 0;
                     for (int q = 0; q < 2; q++)
                        w[p] += invJacobian[s + 2 * p + q] * v[q];
                     outFData[n + l + 2 * p] = w[p];
                  }
               }
      }
   }

   public static float[] computeDerivatives(int nThreads, RegularField inField, float[] inData, float[] invJacobian)
   {
      Thread[] workThreads;
      workThreads = new Thread[nThreads];
      int[] dims = inField.getDims();
      int dim = dims.length;
      float[] outData = new float[dim * inData.length];
      for (int iThread = 0; iThread < nThreads; iThread++)
      {
         if (inData.length / inField.getNNodes() == 1)
         {
            if (dim == 3)
               workThreads[iThread] = new Thread(new Compute3DScalar(nThreads, iThread, dims, invJacobian, inData, outData));
            else if (dim == 2)
               workThreads[iThread] = new Thread(new Compute2DScalar(nThreads, iThread, dims, invJacobian, inData, outData));
         }
         else if (inData.length / inField.getNNodes() == dim)
         {
            if (dim == 3)
               workThreads[iThread] = new Thread(new Compute3DVector(nThreads, iThread, dims, invJacobian, inData, outData));
            else if (dim == 2)
               workThreads[iThread] = new Thread(new Compute2DVector(nThreads, iThread, dims, invJacobian, inData, outData));
         }
         workThreads[iThread].start();
      }
      for (int i = 0; i < workThreads.length; i++)
         try
         {
            workThreads[i].join();
         } catch (Exception e)
         {
         }
      return outData;
   }
   
   public static float[] symmetrize(int dim, float[] in)
   {
      if (dim == 1)
         return in;
      int nData = in.length / (dim * dim);
      int d = (dim * (dim + 1)) / 2;
      float[] h = new float[nData * d];
      if (dim ==2)
         for (int k = 0, l = 0, m = 0; k < nData; k++, l += 3, m += 4)
         {
            h[l]     = in[m];
            h[l + 1] = .5f * (in[m + 1] + in[m + 2]);
            h[l + 2] = in[m + 3];
         }
      else if (dim == 3)
         for (int k = 0, l = 0, m = 0; k < nData; k++, l += 6, m += 9)
         {
            h[l]     = in[m];
            h[l + 1] = .5f * (in[m + 1] + in[m + 3]);
            h[l + 2] = .5f * (in[m + 2] + in[m + 6]);
            h[l + 3] = in[m + 4];
            h[l + 4] = .5f * (in[m + 5] + in[m + 7]);
            h[l + 5] = in[m + 8];
         }
      return h;
   }
   
}
