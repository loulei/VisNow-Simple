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

package pl.edu.icm.visnow.lib.utils.numeric;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class PointwiseLinearAlgebra2D
{
   private static class ComputeSymEigen implements Runnable
   {

      private int nThreads = 1;
      private int iThread = 0;
      private int nData;
      private float[] h = null;
      private float[][] eigVals = null;
      private float[][] eigVect = null;
      
      public ComputeSymEigen(int nThreads, int iThread, 
                             float[] mat, float[][] hEigV, float[][] hEigR)
      {
         this.nThreads = nThreads;
         this.iThread = iThread;
         this.h = mat;
         nData = mat.length / 3;
         this.eigVect = hEigR;
         this.eigVals = hEigV;
      }

      public void run()
      {
         
         int dk =  nData / nThreads;
         int kstart = iThread * dk + Math.min(iThread, nData % nThreads);
         int kend = (iThread + 1) * dk + Math.min(iThread + 1, nData % nThreads);
         float a[][]  = new float[2][2];
         float vals[] = new float[2];
         float v[][]  = new float[2][2];
         for (int k = kstart, l = 3 * kstart; k < kend; k++, l += 3)
         {
            a[0][0] = h[l];
            a[0][1] = a[1][0] = h[l + 1];
            a[1][1] = h[l + 2];
            NumericalMethods.jacobiEigenproblemSolver(a, vals, v, 200);
            if (vals[0] < vals[1])
            {
               eigVals[0][k] = vals[0];
               eigVals[1][k] = vals[1];
               System.arraycopy(v[0], 0, eigVect[0], 2 * k, 2);
               System.arraycopy(v[1], 0, eigVect[1], 2 * k, 2);
            }
            else
            {
               eigVals[0][k] = vals[1];
               eigVals[1][k] = vals[0];
               System.arraycopy(v[0], 0, eigVect[1], 2 * k, 2);
               System.arraycopy(v[1], 0, eigVect[0], 2 * k, 2);
            }
         }
      }
   }
   
   /**
    * Computes pointwise a number of solutions of 2D real symmetric eigenproblems for n points
    * @param nThreads - reasonable number of threads (use number of processing cores as the first estimate)
    * @param mat      - array containing n * 3 floats (a00(x0), a01(x0),a11(x0) a00(x1) ...) 
    * with a00,a01,a11 being entries of upper triangular part of the symmatric matrix A(x)
    * @param eigVals  - array[2][n] to be filled by arrays of point eigenvalues 
    * @param eigVect  = array[2][2n] to be filled by arrays of point eigenvvectors 
    * (for the k-th point eigVals[0][i], eigVals[1][i] will be low and high eigenvalue of the 
    * matrix A(xi), {eigVect[j][2i],eigVect[j][2i+1]} will be corresponding eigenvectors.
    * Both eigVals and eigVest must be properly allocated and passed
    * @return false if mat, eigVals or eigVect were null or of improper dimensions, true otherwise
    */
   public static boolean symEigen(int nThreads, float[] mat, float[][] eigVals, float[][] eigVect)
   {
      if (mat == null || eigVals == null || eigVect == null || eigVals.length != 2 || eigVect.length != 2)
         return false;
      int nData = mat.length / 3;
      if (mat.length != 3 * nData || 
          eigVals[0] == null || eigVals[0].length != nData || eigVect[0] == null || eigVect[0].length != 2 * nData ||
          eigVals[1] == null || eigVals[1].length != nData || eigVect[1] == null || eigVect[1].length != 2 * nData)
         return false;
      Thread[] workThreads = new Thread[nThreads];
      for (int iThread = 0; iThread < nThreads; iThread++)
      {
         workThreads[iThread] = new Thread(new ComputeSymEigen(nThreads, iThread, 
                                                               mat, eigVals, eigVect));
         workThreads[iThread].start();
      }
      for (int iThread = 0; iThread < workThreads.length; iThread++)
         try
         {
            workThreads[iThread].join();
         } catch (Exception e)
         {
         }
      return true;
   }

   private PointwiseLinearAlgebra2D()
   {
   }
}
