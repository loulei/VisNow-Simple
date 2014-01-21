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


package pl.edu.icm.visnow.lib.utils.numeric.splines;

import pl.edu.icm.visnow.gui.events.FloatValueModificationEvent;
import pl.edu.icm.visnow.gui.events.FloatValueModificationListener;

/**
 *
 * @author Krzysztof S. Nowinski
 *   <p>   University of Warsaw, ICM
 */
public class SplineUtilities 
{
   private static final float[] c0 = { -.5f,  1.f,  -.5f, 0.f};
   private static final float[] c1 = { 1.5f, -2.5f,  0.f, 1.f};
   private static final float[] c2 = {-1.5f,  2.f,   .5f, 0.f};
   private static final float[] c3 = {  .5f,  -.5f,  0.f, 0.f};
   private static final float[][] c = {c0, c1, c2, c3};
   private int[] dims; 
   private int vlen; 
   private float[] inData;
   private int[] outDims; 
   private float[] outData;
   private int nData;
   private int nThreads = 1;
   private int[] iPoints;
   private int[] jPoints;
   private int[] kPoints;
   private int[] iLimits;
   private int[] jLimits;
   private int[] kLimits;
   private float[][] iCoeffs;
   private float[][] jCoeffs;
   private float[][] kCoeffs;
   
   class ComputeSplines implements Runnable
   {
      int nThreads      = 1;
      int iThread       = 0;
      public ComputeSplines(int nThreads, int iThread)
      {
         this.nThreads       = nThreads;
         this.iThread        = iThread;
      }

      public void run()
      {
         int ii, jj, kk;
         switch (dims.length)
         {
         case 3:
            for (int i = iThread; i < dims[2]-1; i += nThreads)
            {
               if (iThread == 0)
                   fireStatusChanged(i/(dims[2]-1.f));
               for (int j = 0; j < dims[1]-1; j++)
                  for (int k = 0; k < dims[0]-1; k++)
                  {
                     int pl = iLimits[i]; int pu = iLimits[i + 1]; 
                     int ql = jLimits[j]; int qu = jLimits[j + 1];
                     int rl = kLimits[k]; int ru = kLimits[k + 1];
                     for (ii = 0; ii < 4; ii++)
                     {
                        int ix = i + ii - 1; if (ix < 0) ix = 0; if (ix >= dims[2]) ix = dims[2]-1;
                        for (jj = 0; jj < 4; jj++)
                        {
                           int jx = j + jj - 1; if (jx < 0) jx = 0; if (jx >= dims[1]) jx = dims[1]-1;
                           for (kk = 0; kk < 4 ; kk++)
                           {
                              int kx = k + kk - 1; if (kx < 0) kx = 0; if (kx >= dims[0]) kx = dims[0]-1;
                              for (int v = 0; v < vlen; v++)
                              {
                                 float u = inData[vlen * ((ix * dims[1] + jx) * dims[0] + kx) + v];
                                 for (int p = pl; p < pu; p++)
                                 {
                                    float ui = u * iCoeffs[p][ii];
                                    for (int q = ql; q < qu; q++)
                                    {
                                       float uij = ui * jCoeffs[q][jj];
                                       for (int r = rl,
                                            l = (p * outDims[1] + q)  * outDims[0] + rl;
                                            r < ru;
                                            r++, l++)
                                          outData[vlen * l + v] += uij * kCoeffs[r][kk];
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
            }
            break;
         case 2:
            for (int j = iThread; j < dims[1]-1; j += nThreads)
               for (int k = 0; k < dims[0]-1; k++)
               {
                  int ql = jLimits[j]; int qu = jLimits[j + 1];
                  int rl = kLimits[k]; int ru = kLimits[k + 1];
                  for (jj = 0; jj < 4; jj++)
                  {
                     int jx = j + jj - 1; if (jx < 0) jx = 0; if (jx >= dims[1]) jx = dims[1]-1;
                     for (kk = 0; kk < 4 ; kk++)
                     {
                        int kx = k + kk - 1; if (kx < 0) kx = 0; if (kx >= dims[0]) kx = dims[0]-1;
                        for (int v = 0; v < vlen; v++)
                        {
                           float u = inData[vlen * (jx * dims[0] + kx) + v];
                           for (int q = ql; q < qu; q++)
                              for (int r = rl,
                                   l = q  * outDims[0] + rl;
                                   r < ru;
                                   r++, l++)
                                 outData[vlen * l + v] += u * jCoeffs[q][jj] * kCoeffs[r][kk];
                        }
                     }
                  }
               }
            break;
         case 1:
            for (int k = iThread; k < dims[0]-1; k += nThreads)
            {
                  int rl = jLimits[k]; int ru = kLimits[k + 1];
                  for (kk = 0; kk < 4 ; kk++)
                  {
                     int kx = k + kk - 1; if (kx < 0) kx = 0; if (kx >= dims[0]) kx = dims[0]-1;
                     for (int v = 0; v < vlen; v++)
                     {
                        float u = inData[vlen * kx + v];
                        for (int r = rl,
                             l = rl;
                             r < ru;
                             r++, l++)
                           outData[vlen * l + v] += u * kCoeffs[r][kk];
                     }
                  }
            }
            break;
         }
      }
   }
   
   public SplineUtilities(int[]dims, int vlen, int density, float[] inData, int nThreads)
   {
      this.dims = dims;
      this.vlen = vlen;
      this.inData = inData;
      outDims = new int[dims.length];
      nData = 1;
      for (int i = 0; i < dims.length; i++)
      {
         outDims[i] = density*(dims[i] - 1) +1;
         nData *= outDims[i];
      }
      kPoints = new int[outDims[0]];
      kLimits = new int[dims[0]];
      kLimits[0] = 0;
      kCoeffs  = new float[outDims[0]][4];
      for (int i = 0; i < outDims[0]; i++)
      {
         float f = (float)i/density;
         kPoints[i] = (int)f;
         if (kPoints[i] >= dims[0] - 1)
            kPoints[i] = dims[0] - 2;
         for (int j = kPoints[i] + 1; j < kLimits.length; j++)
            kLimits[j] = i + 1;
         f -= kPoints[i];
         for (int j = 0; j < 4; j++)
            kCoeffs[i][j] = ((c[j][0] * f + c[j][1])  * f + c[j][2]) * f + c[j][3];
      }
      if (outDims.length > 1)
      {
         jPoints = new int[outDims[1]];
         jLimits = new int[dims[1]+1];
         jLimits[0] = 0;
         jCoeffs  = new float[outDims[1]][4];
         for (int i = 0; i < outDims[1]; i++)
         {
            float f = (float)i/density;
            jPoints[i] = (int)f;
            if (jPoints[i] >= dims[1] - 1)
               jPoints[i] = dims[1] - 2;
            for (int j = jPoints[i] + 1; j < jLimits.length; j++)
               jLimits[j] = i + 1;
            f -= jPoints[i];
            for (int j = 0; j < 4; j++)
               jCoeffs[i][j] = ((c[j][0] * f + c[j][1])  * f + c[j][2]) * f + c[j][3];
         }
      }
      if (outDims.length > 2)
      {
         iPoints = new int[outDims[2]];
         iLimits = new int[dims[2]+1];
         iLimits[0] = 0;
         iCoeffs  = new float[outDims[2]][4];
         for (int i = 0; i < outDims[2]; i++)
         {
            float f = (float)i/density;
            iPoints[i] = (int)f;
            if (iPoints[i] >= dims[2] - 1)
               iPoints[i] = dims[2] - 2;
            for (int j = iPoints[i] + 1; j < iLimits.length; j++)
               iLimits[j] = i + 1;
            f -= iPoints[i];
            for (int j = 0; j < 4; j++)
               iCoeffs[i][j] = ((c[j][0] * f + c[j][1])  * f + c[j][2]) * f + c[j][3];
         }
      }
      outData = new float[vlen * nData];
      for (int i = 0; i < outData.length; i++)
         outData[i] = 0;

      this.nThreads = nThreads;
   }
   
   public SplineUtilities(int[]dims, int vlen, int[] outDims, float[] inData, int nThreads)
   {
      this.dims = dims;
      this.vlen = vlen;
      this.inData = inData;
      this.outDims = outDims;
      nData = 1;
      for (int i = 0; i < dims.length; i++)
         nData *= outDims[i];
      kPoints = new int[outDims[0]];
      kLimits = new int[dims[0]+1];
      kLimits[0] = 0;
      kCoeffs  = new float[outDims[0]][4];
      float d = (float)(outDims[0] - 1)/(dims[0] - 1);
      for (int i = 0; i < outDims[0]; i++)
      {
         float f = i / d;
         kPoints[i] = (int)f;
         if (kPoints[i] >= dims[0] - 1)
            kPoints[i] = dims[0] - 2;
         for (int j = kPoints[i] + 1; j < kLimits.length; j++)
            kLimits[j] = i + 1;
         f -= kPoints[i];
         for (int j = 0; j < 4; j++)
            kCoeffs[i][j] = ((c[j][0] * f + c[j][1])  * f + c[j][2]) * f + c[j][3];
      }
      if (outDims.length > 1)
      {
         jPoints = new int[outDims[1]];
         jLimits = new int[dims[1]+1];
         jLimits[0] = 0;
         jCoeffs  = new float[outDims[1]][4];
         d = (float)(outDims[1] - 1)/(dims[1] - 1);
         for (int i = 0; i < outDims[1]; i++)
         {
            float f = i / d;
            jPoints[i] = (int)f;
            if (jPoints[i] >= dims[1] - 1)
               jPoints[i] = dims[1] - 2;
            for (int j = jPoints[i] + 1; j < jLimits.length; j++)
               jLimits[j] = i + 1;
            f -= jPoints[i];
            for (int j = 0; j < 4; j++)
               jCoeffs[i][j] = ((c[j][0] * f + c[j][1])  * f + c[j][2]) * f + c[j][3];
         }
      }
      if (outDims.length > 2)
      {
         iPoints = new int[outDims[2]];
         iLimits = new int[dims[2]+1];
         iLimits[0] = 0;
         iCoeffs  = new float[outDims[2]][4];
         d = (float)(outDims[2] - 1)/(dims[2] - 1);
         for (int i = 0; i < outDims[2]; i++)
         {
            float f = i / d;
            iPoints[i] = (int)f;
            if (iPoints[i] >= dims[2] - 1)
               iPoints[i] = dims[2] - 2;
            for (int j = iPoints[i] + 1; j < iLimits.length; j++)
               iLimits[j] = i + 1;
            f -= iPoints[i];
            for (int j = 0; j < 4; j++)
               iCoeffs[i][j] = ((c[j][0] * f + c[j][1])  * f + c[j][2]) * f + c[j][3];
         }
      }

      outData = new float[vlen * nData];
      for (int i = 0; i < outData.length; i++)
         outData[i] = 0;

      this.nThreads = nThreads;
   }
   
   public SplineUtilities(int[]dims, int vlen, float[][] affine, float cellSize, float[] inData, int nThreads)
   {
      float xExp, yExp, zExp;
      this.dims = dims;
      this.vlen = vlen;
      this.inData = inData;
      outDims = new int[dims.length];
      nData = 1;
      xExp = affine[0][0]/cellSize;
      outDims[0] = (int)((dims[0] - 1 ) * xExp) + 1;
      kPoints = new int[outDims[0]];
      kLimits = new int[dims[0]+1];
      kLimits[0] = 0;
      kCoeffs  = new float[outDims[0]][4];
      for (int i = 0; i < outDims[0]; i++)
      {
         float f = i / xExp;
         kPoints[i] = (int)f;
         if (kPoints[i] >= dims[0] - 1)
            kPoints[i] = dims[0] - 2;
         for (int j = kPoints[i] + 1; j < kLimits.length; j++)
            kLimits[j] = i + 1;
         f -= kPoints[i];
         for (int j = 0; j < 4; j++)
            kCoeffs[i][j] = ((c[j][0] * f + c[j][1])  * f + c[j][2]) * f + c[j][3];
      }
      if (outDims.length > 1)
      {
         yExp = affine[1][1]/cellSize;
         outDims[1] = (int)((dims[1] - 1 ) * yExp) + 1;
         jPoints = new int[outDims[1]];
         jLimits = new int[dims[1]+1];
         jLimits[0] = 0;
         jCoeffs  = new float[outDims[1]][4];
         for (int i = 0; i < outDims[1]; i++)
         {
            float f = i / yExp;
            jPoints[i] = (int)f;
            if (jPoints[i] >= dims[1] - 1)
               jPoints[i] = dims[1] - 2;
            for (int j = jPoints[i] + 1; j < jLimits.length; j++)
               jLimits[j] = i + 1;
            f -= jPoints[i];
            for (int j = 0; j < 4; j++)
               jCoeffs[i][j] = ((c[j][0] * f + c[j][1])  * f + c[j][2]) * f + c[j][3];
          }
      }
      if (outDims.length > 2)
      {
         zExp = affine[2][2]/cellSize;
         outDims[2] = (int)((dims[2] - 1 ) * zExp) + 1;
         iPoints = new int[outDims[2]];
         iLimits = new int[dims[2]+1];
         iLimits[0] = 0;
         iCoeffs  = new float[outDims[2]][4];
         for (int i = 0; i < outDims[2]; i++)
         {
            float f = i / zExp;
            iPoints[i] = (int)f;
            if (iPoints[i] >= dims[2] - 1)
               iPoints[i] = dims[2] - 2;
            for (int j = iPoints[i] + 1; j < iLimits.length; j++)
               iLimits[j] = i + 1;
            f -= iPoints[i];
            for (int j = 0; j < 4; j++)
               iCoeffs[i][j] = ((c[j][0] * f + c[j][1])  * f + c[j][2]) * f + c[j][3];
         }
      }
      
      for (int i = 0; i < dims.length; i++)
         nData *= outDims[i];
      outData = new float[vlen * nData];
      for (int i = 0; i < outData.length; i++)
         outData[i] = 0;
      this.nThreads = nThreads;
   }
   

   public float[] splineInterpolate()
   {
      Thread[] workThreads = new Thread[nThreads];
      for (int iThread = 0; iThread < nThreads; iThread++)
      {
         workThreads[iThread] = new Thread(new ComputeSplines(nThreads, iThread));
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


   private transient FloatValueModificationListener statusListener = null;

   public void addFloatValueModificationListener(FloatValueModificationListener listener)
   {
      if (statusListener == null)
         this.statusListener = listener;
      else
         System.out.println(""+this+": only one status listener can be added");
   }

   private void fireStatusChanged(float status)
   {
       FloatValueModificationEvent e = new FloatValueModificationEvent(this, status, true);
       if (statusListener != null)
          statusListener.floatValueChanged(e);
   }

   
}
