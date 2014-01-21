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

package pl.edu.icm.visnow.lib.utils.field;

import pl.edu.icm.visnow.gui.events.FloatValueModificationEvent;
import pl.edu.icm.visnow.gui.events.FloatValueModificationListener;

/**
 *
 * @author Krzysztof S. Nowinski
 *   <p>   University of Warsaw, ICM
 */
public class DirectionalFieldSmoothing implements Runnable
{

   private int[] dims;
   private int[] outDims;
   private int down = 1;
   private float[] inData;
   private float[] outData;
   private float[] kernel;
   private float[] kernelDist;
   private int direction = 0;
   private int radius = 5;
   private int nThreads = 1;
   private int iThread = 0;
   private int vlen;
   private float[] inSlice;
   private float[] outSlice;

   private void computeSmoothingKernel(float sigma, int[] dims)
   {
      radius = (int) (4 * sigma);
     
      if (radius >= dims[direction] / 2)
            radius = dims[direction] / 2 - 1;

      kernel = new float[2 * radius + 1];
      kernelDist = new float[2 * radius + 1];
      float s = 0;
      float rd = 4.f / (sigma * sigma);
      for (int i = 0; i <= radius; i++)
         kernel[radius + i] = kernel[radius - i] = (float) Math.exp(-i * i * rd);
      for (int i = 0; i < kernel.length; i++)
         s += kernel[i];
      s = 1 / s;
      for (int i = 0; i < kernel.length; i++)
      {
         kernel[i] *= s;
         if (i == 0)
            kernelDist[i] = 0;
         else
            kernelDist[i] = kernelDist[i - 1] + kernel[i - 1];
      }
   }

   public DirectionalFieldSmoothing(int direction, int[] dims, float[] data, float[] kernel, float[] kernelDist, 
                                    int radius, int nThreads, int iThread)
   {
      this.dims = dims;
      this.inData = this.outData = data;
      this.kernel = kernel;
      this.kernelDist = kernelDist;
      this.direction = direction;
      this.radius = radius;
      this.nThreads = nThreads;
      this.iThread = iThread;
      int nData = dims[0];
      for (int i = 1; i < dims.length; i++)
         nData *= dims[i];
      vlen = data.length / nData;
      outDims = dims;
   }

   public DirectionalFieldSmoothing(int direction, int[] dims, float[] data, float sigma, 
                                    int nThreads, int iThread)
   {
      this.dims = dims;
      this.inData = this.outData = data;
      computeSmoothingKernel(sigma, dims);
      this.direction = direction;
      this.nThreads = nThreads;
      this.iThread = iThread;
      int nData = dims[0];
      for (int i = 1; i < dims.length; i++)
         nData *= dims[i];
      vlen = data.length / nData;
      outDims = dims;
   }

   public DirectionalFieldSmoothing(int direction, int[] dims, int down, float[] inData, float[] outData, float[] kernel, float[] kernelDist, 
                                    int radius, int nThreads, int iThread)
   {
      this.dims = dims;
      this.inData = inData;
      this.outData = outData;
      this.down = down;
      this.kernel = kernel;
      this.kernelDist = kernelDist;
      this.direction = direction;
      this.radius = radius;
      this.nThreads = nThreads;
      this.iThread = iThread;
      int nData = dims[0];
      for (int i = 1; i < dims.length; i++)
         nData *= dims[i];
      vlen = inData.length / nData;
      outDims = new int[dims.length];
      System.arraycopy(dims, 0, outDims, 0, outDims.length);
      outDims[direction] /= down;
   }

   public DirectionalFieldSmoothing(int direction, int[] dims, int down, float[] inData, float[] outData, float sigma, 
                                    int nThreads, int iThread)
   {
      this.dims = dims;
      this.inData = inData;
      this.outData = outData;
      this.down = down;
      this.direction = direction;
      computeSmoothingKernel(down * sigma, dims);
      this.nThreads = nThreads;
      this.iThread = iThread;
      int nData = dims[0];
      for (int i = 1; i < dims.length; i++)
         nData *= dims[i];
      vlen = inData.length / nData;
      outDims = new int[dims.length];
      System.arraycopy(dims, 0, outDims, 0, outDims.length);
      outDims[direction] = (dims[direction] + down - 1) / down;
   }

   public DirectionalFieldSmoothing(int direction, int[] dims, int down, float[] inData, float[] outData, 
                                    int nThreads, int iThread)
   {
      this.dims = dims;
      this.inData = inData;
      this.outData = outData;
      this.down =  down;
      this.direction = direction;
      computeSmoothingKernel((float) down, dims);
      this.nThreads = nThreads;
      this.iThread = iThread;
      int nData = dims[0];
      for (int i = 1; i < dims.length; i++)
         nData *= dims[i];
      vlen = inData.length / nData;
      outDims = new int[dims.length];
      System.arraycopy(dims, 0, outDims, 0, outDims.length);
      outDims[direction] = (dims[direction] + down - 1) / down;
   }

   private void smooth(int down)
   {
      int r = kernel.length / 2;
      int n = inSlice.length / vlen;
      for (int m = 0; m < vlen; m++)
      {
         for (int i = 0, ii = 0; i < n; i += down, ii++)
         {
            float s = 0;
            if (i < r)
            {
               int k = r - i;
               for (int j = 0; j <= i + r; j++, k++)
                  s += inSlice[j * vlen + m] * kernel[k];
               outSlice[ii * vlen + m] = s / kernelDist[r + i + 1];
            }
            else if (i < n - r)
            {
               int k = i - r;
               for (int j = 0; j < 2 * r + 1 && k < n; j++, k++)
                  s += inSlice[k * vlen + m] * kernel[j];
               outSlice[ii * vlen + m] = s;
            }
            else
            {
                int k = i - r;
                for (int j = 0; k < n; j++, k++)
                   s += inSlice[k * vlen + m] * kernel[j];
                outSlice[ii * vlen + m] = s / kernelDist[r + n - i];
            }
         }
      }
   }

   public void run()
   {
      int start = 0;
      int nDims = dims.length;
      int step = 1;
      int nSlices = 1;

      switch (direction)
      {
      case 0:
         inSlice = new float[dims[0] * vlen];
         outSlice = new float[outDims[0] * vlen];
         nSlices = dims[1];
         if (nDims == 3)
            nSlices *= dims[2];
         for (int i = iThread; i < nSlices; i += nThreads)
         {
            System.arraycopy(inData, i * dims[0] * vlen, inSlice, 0, inSlice.length);
            smooth(down);
            System.arraycopy(outSlice, 0, outData, i * outDims[0] * vlen, outSlice.length);
         }
         break;
      case 1:
         step = dims[0];
         inSlice = new float[dims[1] * vlen];
         outSlice = new float[outDims[1] * vlen];
         nSlices = dims[0];
         if (nDims == 3)
            nSlices *= dims[2];
         for (int i = iThread; i < nSlices; i += nThreads)
         {
            int p = i / dims[0];
            int q = i % dims[0];
            if (nDims == 3)
               start = p * dims[0] * dims[1] + q;
            else
               start = i;
            for (int j = 0, l = 0; j < dims[1]; j++)
               for (int k = 0; k < vlen; k++, l++)
                  inSlice[l] = inData[(start + j * step) * vlen + k];
            smooth(down);
            if (nDims == 3)
               start = p * dims[0] * outDims[1] + q;
            for (int j = 0, l = 0; j < outDims[1]; j++)
               for (int k = 0; k < vlen; k++, l++)
                  try {
                  outData[(start + j * step) * vlen + k] = outSlice[l];
                  } catch (Exception e)
                  {
                     System.out.println("1 "+start+" "+j+" "+step+" "+(start + j * step)+" "+(start + j * step) * vlen + k);
                  }
         }
         break;
      case 2:
         step = dims[0] * dims[1];
         inSlice = new float[dims[2] * vlen];
         outSlice = new float[outDims[2] * vlen];
         nSlices = dims[0] * dims[1];
         for (int i = iThread; i < nSlices; i += nThreads)
         {
            if (i >= nSlices)
               continue;
            if (iThread == 0)
               fireStatusChanged((float)i/nSlices);
            for (int j = 0, l = 0; j < dims[2]; j++)
               for (int k = 0; k < vlen; k++, l++)
                  inSlice[l] = inData[(i + j * step) * vlen + k];
            smooth(down);
            for (int j = 0, l = 0; j < outDims[2]; j++)
               for (int k = 0; k < vlen; k++, l++)
                  try {
                     outData[(i + j * step) * vlen + k] = outSlice[l];
                  } catch (Exception e)
                  {
                     System.out.println("2 "+i+" "+j+" "+step+" "+(i + j * step)+" "+((i + j * step) * vlen + k)+" "+l);
                  }
                  
         }
         break;
      }
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
