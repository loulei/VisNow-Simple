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

package pl.edu.icm.visnow.lib.utils;

/**
 *
 * @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class ArrayUtils
{
   private ArrayUtils()
   {
      
   }
   
   public static byte[] convertToByteArray(boolean[] in)
   {
      byte[] out = new byte[in.length];
      for (int i = 0; i < out.length; i++)
         out[i] = in[i] ? (byte)1 : 0;
      return out;
   }
      
   public static byte[] convertToByteArray(short[] in, boolean normalize, float min, float max)
   {
      byte[] out = new byte[in.length];
      if (normalize)
      {
         float d = 255 / (max - min);
         for (int i = 0; i < out.length; i++)
            out[i] = (byte)(0xff & (int)(d * (in[i] - min)));
      }
      else
         for (int i = 0; i < out.length; i++)
            out[i] = (byte)(0xff & in[i]);
      return out;
   }
   
   public static byte[] convertToByteArray(int[] in, boolean normalize, float min, float max)
   {
      byte[] out = new byte[in.length];
      if (normalize)
      {
         float d = 255 / (max - min);
         for (int i = 0; i < out.length; i++)
            out[i] = (byte)(0xff & (int)(d * (in[i] - min)));
      }
      else
         for (int i = 0; i < out.length; i++)
            out[i] = (byte)(0xff & in[i]);
      return out;
   }
   
   public static byte[] convertToByteArray(float[] in, boolean normalize, float min, float max)
   {
      byte[] out = new byte[in.length];
      if (normalize)
      {
         float d = 255 / (max - min);
         for (int i = 0; i < out.length; i++)
            out[i] = (byte)(0xff & (int)(d * (in[i] - min)));
      }
      else
         for (int i = 0; i < out.length; i++)
            out[i] = (byte)(0xff & (int)in[i]);
      return out;
   }
   
   public static byte[] convertToByteArray(double[] in, boolean normalize, float min, float max)
   {
      byte[] out = new byte[in.length];
      if (normalize)
      {
         float d = 255 / (max - min);
         for (int i = 0; i < out.length; i++)
            out[i] = (byte)(0xff & (int)(d * (in[i] - min)));
      }
      else
         for (int i = 0; i < out.length; i++)
            out[i] = (byte)(0xff & (int)in[i]);
      return out;
   }
   
   public static short[] convertToShortArray(boolean[] in)
   {
      short[] out = new short[in.length];
      for (int i = 0; i < out.length; i++)
         out[i] = in[i] ? (short)1 : 0;
      return out;
   }
      
   public static short[] convertToShortArray(byte[] in)
   {
      short[] out = new short[in.length];
         for (int i = 0; i < out.length; i++)
            out[i] = (short)(0xff & in[i]);
      return out;
   }
   
   public static short[] convertToShortArray(int[] in, boolean normalize, float min, float max)
   {
      short[] out = new short[in.length];
      if (normalize)
      {
         float d =  (Short.MAX_VALUE - Short.MIN_VALUE)/ (max - min);
         for (int i = 0; i < out.length; i++)
            out[i] = (short)(Short.MIN_VALUE + d * (in[i] - min));
      }
      else
         for (int i = 0; i < out.length; i++)
            out[i] = (short)in[i];
      return out;
   }
   
   public static short[] convertToShortArray(float[] in, boolean normalize, float min, float max)
   {
      short[] out = new short[in.length];
      if (normalize)
      {
         float d =  (Short.MAX_VALUE - Short.MIN_VALUE)/ (max - min);
         for (int i = 0; i < out.length; i++)
            out[i] = (short)(Short.MIN_VALUE + d * (in[i] - min));
      }
      else
         for (int i = 0; i < out.length; i++)
            out[i] = (short)in[i];
      return out;
   }
   
   public static short[] convertToShortArray(double[] in, boolean normalize, float min, float max)
   {
      short[] out = new short[in.length];
      if (normalize)
      {
         float d =  (Short.MAX_VALUE - Short.MIN_VALUE)/ (max - min);
         for (int i = 0; i < out.length; i++)
            out[i] = (short)(Short.MIN_VALUE + d * (in[i] - min));
      }
      else
         for (int i = 0; i < out.length; i++)
            out[i] = (short)in[i];
      return out;
   }
      
   public static int[] convertToIntArray(boolean[] in)
   {
      int[] out = new int[in.length];
      for (int i = 0; i < out.length; i++)
         out[i] = in[i] ? 1 : 0;
      return out;
   }
      
   public static int[] convertToIntArray(byte[] in)
   {
      int[] out = new int[in.length];
      for (int i = 0; i < out.length; i++)
         out[i] = 0xff & in[i];
      return out;
   }
   
   public static int[] convertToIntArray(short[] in)
   {
      int[] out = new int[in.length];
      for (int i = 0; i < out.length; i++)
         out[i] = in[i];
      return out;
   }
   
   public static int[] convertToIntArray(float[] in, boolean normalize, float min, float max)
   {
      int[] out = new int[in.length];
      if (normalize)
      {
         float d = (Integer.MAX_VALUE - Integer.MIN_VALUE) / (max - min);
         for (int i = 0; i < out.length; i++)
            out[i] = (int)(Integer.MIN_VALUE + d * (in[i] - min));
      }
      else
         for (int i = 0; i < out.length; i++)
            out[i] = (int)in[i];
      return out;
   }
   
   public static int[] convertToIntArray(double[] in, boolean normalize, float min, float max)
   {
      int[] out = new int[in.length];
      if (normalize)
      {
         float d = (Integer.MAX_VALUE - Integer.MIN_VALUE) / (max - min);
         for (int i = 0; i < out.length; i++)
            out[i] = (int)(Integer.MIN_VALUE + d * (in[i] - min));
      }
      else
         for (int i = 0; i < out.length; i++)
            out[i] = (int)in[i];
      return out;
   }
   
      
   public static float[] convertToFloatArray(boolean[] in)
   {
      float[] out = new float[in.length];
      for (int i = 0; i < out.length; i++)
         out[i] = in[i] ? 1 : 0;
      return out;
   }
      
   public static float[] convertToFloatArray(byte[] in)
   {
      float[] out = new float[in.length];
      for (int i = 0; i < out.length; i++)
         out[i] = 0xff & in[i];
      return out;
   }
   
   public static float[] convertToFloatArray(short[] in)
   {
      float[] out = new float[in.length];
      for (int i = 0; i < out.length; i++)
         out[i] = in[i];
      return out;
   }
   
   public static float[] convertToFloatArray(int[] in)
   {
      float[] out = new float[in.length];
      for (int i = 0; i < out.length; i++)
         out[i] = in[i];
      return out;
   }
   
   public static float[] convertToFloatArray(double[] in)
   {
      float[] out = new float[in.length];
      for (int i = 0; i < out.length; i++)
         out[i] = (float)in[i];
      return out;
   }
   
   public static float[] convertToFloatArray(String[] in)
   {
      float[] out = new float[in.length];
      for (int i = 0; i < out.length; i++)
         out[i] = (float)in[i].length(); // TODO think about better idea
      return out;
   }
   
      
   public static double[] convertToDoubleArray(boolean[] in)
   {
      double[] out = new double[in.length];
      for (int i = 0; i < out.length; i++)
         out[i] = in[i] ? 1 : 0;
      return out;
   }
      
   public static double[] convertToDoubleArray(byte[] in)
   {
      double[] out = new double[in.length];
      for (int i = 0; i < out.length; i++)
         out[i] = 0xff & in[i];
      return out;
   }
   
   public static double[] convertToDoubleArray(short[] in)
   {
      double[] out = new double[in.length];
      for (int i = 0; i < out.length; i++)
         out[i] = in[i];
      return out;
   }
   
   public static double[] convertToDoubleArray(int[] in)
   {
      double[] out = new double[in.length];
      for (int i = 0; i < out.length; i++)
         out[i] = in[i];
      return out;
   }
   
   public static double[] convertToDoubleArray(float[] in)
   {
      double[] out = new double[in.length];
      for (int i = 0; i < out.length; i++)
         out[i] = in[i];
      return out;
   }
      
   public static float[] vectorNorms(boolean[] in, int veclen)
   {
      float[] out = new float[in.length / veclen];
      for (int i = 0, k = 0; i < out.length; i++)
      {
         out[i] = 0;
         for (int j = 0; j < veclen; j++, k++)
            if (in[k])
               out[i] += 1;
      }
      return out;
   }
      
   public static float[] vectorNorms(byte[] in, int veclen)
   {
      float[] out = new float[in.length / veclen];
      for (int i = 0, k = 0; i < out.length; i++)
      {
         double d = 0;
         for (int j = 0; j < veclen; j++, k++)
            d += (0xff & in[k]) * (0xff & in[k]);
         out[i] = (float)Math.sqrt(d);
      }
      return out;
   }
   
   public static float[] vectorNorms(short[] in, int veclen)
   {
      float[] out = new float[in.length / veclen];
      for (int i = 0, k = 0; i < out.length; i++)
      {
         double d = 0;
         for (int j = 0; j < veclen; j++, k++)
            d += in[k] * in[k];
         out[i] = (float)Math.sqrt(d);
      }
      return out;
   }
   
   public static float[] vectorNorms(int[] in, int veclen)
   {
      float[] out = new float[in.length / veclen];
      for (int i = 0, k = 0; i < out.length; i++)
      {
         double d = 0;
         for (int j = 0; j < veclen; j++, k++)
            d += in[k] * in[k];
         out[i] = (float)Math.sqrt(d);
      }
      return out;
   }
   
   public static float[] vectorNorms(float[] in, int veclen)
   {
      float[] out = new float[in.length / veclen];
      for (int i = 0, k = 0; i < out.length; i++)
      {
         double d = 0;
         for (int j = 0; j < veclen; j++, k++)
            d += in[k] * in[k];
         out[i] = (float)Math.sqrt(d);
      }
      return out;
   }
   
   public static float[] vectorNorms(double[] in, int veclen)
   {
      float[] out = new float[in.length / veclen];
      for (int i = 0, k = 0; i < out.length; i++)
      {
         double d = 0;
         for (int j = 0; j < veclen; j++, k++)
            d += in[k] * in[k];
         out[i] = (float)Math.sqrt(d);
      }
      return out;
   }
      
   
   public static byte[] get2DSlice(byte[] data, int start, int n0, int step0, int n1, int step1, int veclen)
   {
      byte[] out = new byte[n0 * n1 * veclen];
      for (int i1 = 0, k = 0, l1 = start * veclen; i1 < n1; i1++, l1 += step1 * veclen)
         for (int i0 = 0, l0 = l1; i0 < n0; i0++, l0 += step0 * veclen)
            for (int j = 0; j < veclen; j++, k++)
               out[k] = data[l0 + j];
      return out;
   }

   public static float[] get2DFloatSlice(byte[] data, int start, int n0, int step0, int n1, int step1, int veclen)
   {
      float[] out = new float[n0 * n1 * veclen];
      for (int i1 = 0, k = 0, l1 = start * veclen; i1 < n1; i1++, l1 += step1 * veclen)
         for (int i0 = 0, l0 = l1; i0 < n0; i0++, l0 += step0 * veclen)
            for (int j = 0; j < veclen; j++, k++)
               out[k] = (float) (0xFF & data[l0 + j]);
      return out;
   }

   public static float[] get2DNormSlice(byte[] data, int start, int n0, int step0, int n1, int step1, int veclen)
   {
      float[] out;
      if (veclen == 1)
      {
         out = get2DFloatSlice(data, start, n0, step0, n1, step1, veclen);
         for (int i = 0; i < out.length; i++)
            out[i] = Math.abs(out[i]);
         return out;
      }
      out = new float[n0 * n1];
      int i0, i1, j, k, l0, l1;
      for (i1 = k = 0, l1 = start * veclen; i1 < n1; i1++, l1 += step1 * veclen)
         for (i0 = 0, l0 = l1; i0 < n0; i0++, l0 += step0 * veclen, k++)
         {
            double d = 0;
            for (j = 0; j < veclen; j++)
               d += (0xFF & data[l0 + j]) * (0xFF & data[l0 + j]);
            out[k] = (float) Math.sqrt(d);
         }
      return out;
   }
   
   public static byte[] get2DSlice(byte[] data, int[] dims, int axis, int slice, int veclen)
   {
      if (dims == null || dims.length < 2 || axis < 0 || axis >= dims.length || slice < 0 || slice >= dims[axis])
         return null;
      if (dims.length == 2)
      {
         if (axis == 1)
            return get2DSlice(data, slice * dims[0], dims[0], 1, 1, 1, veclen);
         else
            return get2DSlice(data, slice, 1, 1, dims[1], dims[0], veclen);
      }
      else
      {
         switch (axis)
         { 
         case 0:
            return get2DSlice(data, slice, dims[1], dims[0], dims[2], dims[0] * dims[1], veclen);
         case 1:
            return get2DSlice(data, slice * dims[0], dims[0], 1, dims[2], dims[0] * dims[1], veclen);
         case 2:
            return get2DSlice(data, slice * dims[0] * dims[1], dims[0], 1, dims[1], dims[0], veclen);
         }
      }
      return null;
   }

   public static float[] get2DFloatSlice(byte[] data, int[] dims, int axis, int slice, int veclen)
   {
      if (dims == null || dims.length < 2 || axis < 0 || axis >= dims.length || slice < 0 || slice >= dims[axis])
         return null;
      if (dims.length == 2)
      {
         if (axis == 1)
            return get2DFloatSlice(data, slice * dims[0], dims[0], 1, 1, 1, veclen);
         else
            return get2DFloatSlice(data, slice, 1, 1, dims[1], dims[0], veclen);
      }
      else
      {
         switch (axis)
         { 
         case 0:
            return get2DFloatSlice(data, slice, dims[1], dims[0], dims[2], dims[0] * dims[1], veclen);
         case 1:
            return get2DFloatSlice(data, slice * dims[0], dims[0], 1, dims[2], dims[0] * dims[1], veclen);
         case 2:
            return get2DFloatSlice(data, slice * dims[0] * dims[1], dims[0], 1, dims[1], dims[0], veclen);
         }
      }
      return null;
   }

   public static float[] get2DNormSlice(byte[] data, int[] dims, int axis, int slice, int veclen)
   {
      if (dims == null || dims.length < 2 || axis < 0 || axis >= dims.length || slice < 0 || slice >= dims[axis])
         return null;
      if (dims.length == 2)
      {
         if (axis == 1)
            return get2DNormSlice(data, slice * dims[0], dims[0], 1, 1, 1, veclen);
         else
            return get2DNormSlice(data, slice, 1, 1, dims[1], dims[0], veclen);
      }
      else
      {
         switch (axis)
         { 
         case 0:
            return get2DNormSlice(data, slice, dims[1], dims[0], dims[2], dims[0] * dims[1], veclen);
         case 1:
            return get2DNormSlice(data, slice * dims[0], dims[0], 1, dims[2], dims[0] * dims[1], veclen);
         case 2:
            return get2DNormSlice(data, slice * dims[0] * dims[1], dims[0], 1, dims[1], dims[0], veclen);
         }
      }
      return null;
   }

   public static short[] get2DSlice(short[] data, int start, int n0, int step0, int n1, int step1, int veclen)
   {
      short[] out = new short[n0 * n1 * veclen];
      for (int i1 = 0, k = 0, l1 = start * veclen; i1 < n1; i1++, l1 += step1 * veclen)
         for (int i0 = 0, l0 = l1; i0 < n0; i0++, l0 += step0 * veclen)
            for (int j = 0; j < veclen; j++, k++)
               out[k] = data[l0 + j];
      return out;
   }

   public static float[] get2DFloatSlice(short[] data, int start, int n0, int step0, int n1, int step1, int veclen)
   {
      float[] out = new float[n0 * n1 * veclen];
      for (int i1 = 0, k = 0, l1 = start * veclen; i1 < n1; i1++, l1 += step1 * veclen)
         for (int i0 = 0, l0 = l1; i0 < n0; i0++, l0 += step0 * veclen)
            for (int j = 0; j < veclen; j++, k++)
               out[k] = data[l0 + j];
      return out;
   }

   public static float[] get2DNormSlice(short[] data, int start, int n0, int step0, int n1, int step1, int veclen)
   {
      float[] out;
      if (veclen == 1)
      {
         out = get2DFloatSlice(data, start, n0, step0, n1, step1, veclen);
         for (int i = 0; i < out.length; i++)
            out[i] = Math.abs(out[i]);
         return out;
      }
      out = new float[n0 * n1];
      int i0, i1, j, k, l0, l1;
      for (i1 = k = 0, l1 = start * veclen; i1 < n1; i1++, l1 += step1 * veclen)
         for (i0 = 0, l0 = l1; i0 < n0; i0++, l0 += step0 * veclen, k++)
         {
            double d = 0;
            for (j = 0; j < veclen; j++)
               d += data[l0 + j] * data[l0 + j];
            out[k] = (float) Math.sqrt(d);
         }
      return out;
   }
   
   public static short[] get2DSlice(short[] data, int[] dims, int axis, int slice, int veclen)
   {
      if (dims == null || dims.length < 2 || axis < 0 || axis >= dims.length || slice < 0 || slice >= dims[axis])
         return null;
      if (dims.length == 2)
      {
         if (axis == 1)
            return get2DSlice(data, slice * dims[0], dims[0], 1, 1, 1, veclen);
         else
            return get2DSlice(data, slice, 1, 1, dims[1], dims[0], veclen);
      }
      else
      {
         switch (axis)
         { 
         case 0:
            return get2DSlice(data, slice, dims[1], dims[0], dims[2], dims[0] * dims[1], veclen);
         case 1:
            return get2DSlice(data, slice * dims[0], dims[0], 1, dims[2], dims[0] * dims[1], veclen);
         case 2:
            return get2DSlice(data, slice * dims[0] * dims[1], dims[0], 1, dims[1], dims[0], veclen);
         }
      }
      return null;
   }

   public static float[] get2DFloatSlice(short[] data, int[] dims, int axis, int slice, int veclen)
   {
      if (dims == null || dims.length < 2 || axis < 0 || axis >= dims.length || slice < 0 || slice >= dims[axis])
         return null;
      if (dims.length == 2)
      {
         if (axis == 1)
            return get2DFloatSlice(data, slice * dims[0], dims[0], 1, 1, 1, veclen);
         else
            return get2DFloatSlice(data, slice, 1, 1, dims[1], dims[0], veclen);
      }
      else
      {
         switch (axis)
         { 
         case 0:
            return get2DFloatSlice(data, slice, dims[1], dims[0], dims[2], dims[0] * dims[1], veclen);
         case 1:
            return get2DFloatSlice(data, slice * dims[0], dims[0], 1, dims[2], dims[0] * dims[1], veclen);
         case 2:
            return get2DFloatSlice(data, slice * dims[0] * dims[1], dims[0], 1, dims[1], dims[0], veclen);
         }
      }
      return null;
   }

   public static float[] get2DNormSlice(short[] data, int[] dims, int axis, int slice, int veclen)
   {
      if (dims == null || dims.length < 2 || axis < 0 || axis >= dims.length || slice < 0 || slice >= dims[axis])
         return null;
      if (dims.length == 2)
      {
         if (axis == 1)
            return get2DNormSlice(data, slice * dims[0], dims[0], 1, 1, 1, veclen);
         else
            return get2DNormSlice(data, slice, 1, 1, dims[1], dims[0], veclen);
      }
      else
      {
         switch (axis)
         { 
         case 0:
            return get2DNormSlice(data, slice, dims[1], dims[0], dims[2], dims[0] * dims[1], veclen);
         case 1:
            return get2DNormSlice(data, slice * dims[0], dims[0], 1, dims[2], dims[0] * dims[1], veclen);
         case 2:
            return get2DNormSlice(data, slice * dims[0] * dims[1], dims[0], 1, dims[1], dims[0], veclen);
         }
      }
      return null;
   }
   

   public static int[] get2DSlice(int[] data, int start, int n0, int step0, int n1, int step1, int veclen)
   {
      int[] out = new int[n0 * n1 * veclen];
      for (int i1 = 0, k = 0, l1 = start * veclen; i1 < n1; i1++, l1 += step1 * veclen)
         for (int i0 = 0, l0 = l1; i0 < n0; i0++, l0 += step0 * veclen)
            for (int j = 0; j < veclen; j++, k++)
               out[k] = data[l0 + j];
      return out;
   }

   public static float[] get2DFloatSlice(int[] data, int start, int n0, int step0, int n1, int step1, int veclen)
   {
      float[] out = new float[n0 * n1 * veclen];
      for (int i1 = 0, k = 0, l1 = start * veclen; i1 < n1; i1++, l1 += step1 * veclen)
         for (int i0 = 0, l0 = l1; i0 < n0; i0++, l0 += step0 * veclen)
            for (int j = 0; j < veclen; j++, k++)
               out[k] = data[l0 + j];
      return out;
   }

   public static float[] get2DNormSlice(int[] data, int start, int n0, int step0, int n1, int step1, int veclen)
   {
      float[] out;
      if (veclen == 1)
      {
         out = get2DFloatSlice(data, start, n0, step0, n1, step1, veclen);
         for (int i = 0; i < out.length; i++)
            out[i] = Math.abs(out[i]);
         return out;
      }
      out = new float[n0 * n1];
      int i0, i1, j, k, l0, l1;
      for (i1 = k = 0, l1 = start * veclen; i1 < n1; i1++, l1 += step1 * veclen)
         for (i0 = 0, l0 = l1; i0 < n0; i0++, l0 += step0 * veclen, k++)
         {
            double d = 0;
            for (j = 0; j < veclen; j++)
               d += data[l0 + j] * data[l0 + j];
            out[k] = (float) Math.sqrt(d);
         }
      return out;
   }
   
   public static int[] get2DSlice(int[] data, int[] dims, int axis, int slice, int veclen)
   {
      if (dims == null || dims.length < 2 || axis < 0 || axis >= dims.length || slice < 0 || slice >= dims[axis])
         return null;
      if (dims.length == 2)
      {
         if (axis == 1)
            return get2DSlice(data, slice * dims[0], dims[0], 1, 1, 1, veclen);
         else
            return get2DSlice(data, slice, 1, 1, dims[1], dims[0], veclen);
      }
      else
      {
         switch (axis)
         { 
         case 0:
            return get2DSlice(data, slice, dims[1], dims[0], dims[2], dims[0] * dims[1], veclen);
         case 1:
            return get2DSlice(data, slice * dims[0], dims[0], 1, dims[2], dims[0] * dims[1], veclen);
         case 2:
            return get2DSlice(data, slice * dims[0] * dims[1], dims[0], 1, dims[1], dims[0], veclen);
         }
      }
      return null;
   }

   public static float[] get2DFloatSlice(int[] data, int[] dims, int axis, int slice, int veclen)
   {
      if (dims == null || dims.length < 2 || axis < 0 || axis >= dims.length || slice < 0 || slice >= dims[axis])
         return null;
      if (dims.length == 2)
      {
         if (axis == 1)
            return get2DFloatSlice(data, slice * dims[0], dims[0], 1, 1, 1, veclen);
         else
            return get2DFloatSlice(data, slice, 1, 1, dims[1], dims[0], veclen);
      }
      else
      {
         switch (axis)
         { 
         case 0:
            return get2DFloatSlice(data, slice, dims[1], dims[0], dims[2], dims[0] * dims[1], veclen);
         case 1:
            return get2DFloatSlice(data, slice * dims[0], dims[0], 1, dims[2], dims[0] * dims[1], veclen);
         case 2:
            return get2DFloatSlice(data, slice * dims[0] * dims[1], dims[0], 1, dims[1], dims[0], veclen);
         }
      }
      return null;
   }

   public static float[] get2DNormSlice(int[] data, int[] dims, int axis, int slice, int veclen)
   {
      if (dims == null || dims.length < 2 || axis < 0 || axis >= dims.length || slice < 0 || slice >= dims[axis])
         return null;
      if (dims.length == 2)
      {
         if (axis == 1)
            return get2DNormSlice(data, slice * dims[0], dims[0], 1, 1, 1, veclen);
         else
            return get2DNormSlice(data, slice, 1, 1, dims[1], dims[0], veclen);
      }
      else
      {
         switch (axis)
         { 
         case 0:
            return get2DNormSlice(data, slice, dims[1], dims[0], dims[2], dims[0] * dims[1], veclen);
         case 1:
            return get2DNormSlice(data, slice * dims[0], dims[0], 1, dims[2], dims[0] * dims[1], veclen);
         case 2:
            return get2DNormSlice(data, slice * dims[0] * dims[1], dims[0], 1, dims[1], dims[0], veclen);
         }
      }
      return null;
   }


   public static float[] get2DSlice(float[] data, int start, int n0, int step0, int n1, int step1, int veclen)
   {
      float[] out = new float[n0 * n1 * veclen];
      for (int i1 = 0, k = 0, l1 = start * veclen; i1 < n1; i1++, l1 += step1 * veclen)
         for (int i0 = 0, l0 = l1; i0 < n0; i0++, l0 += step0 * veclen)
            for (int j = 0; j < veclen; j++, k++)
               out[k] = data[l0 + j];
      return out;
   }

   public static float[] get2DFloatSlice(float[] data, int start, int n0, int step0, int n1, int step1, int veclen)
   {
      float[] out = new float[n0 * n1 * veclen];
      for (int i1 = 0, k = 0, l1 = start * veclen; i1 < n1; i1++, l1 += step1 * veclen)
         for (int i0 = 0, l0 = l1; i0 < n0; i0++, l0 += step0 * veclen)
            for (int j = 0; j < veclen; j++, k++)
               out[k] = data[l0 + j];
      return out;
   }

   public static float[] get2DNormSlice(float[] data, int start, int n0, int step0, int n1, int step1, int veclen)
   {
      float[] out;
      if (veclen == 1)
      {
         out = get2DFloatSlice(data, start, n0, step0, n1, step1, veclen);
         for (int i = 0; i < out.length; i++)
            out[i] = Math.abs(out[i]);
         return out;
      }
      out = new float[n0 * n1];
      int i0, i1, j, k, l0, l1;
      for (i1 = k = 0, l1 = start * veclen; i1 < n1; i1++, l1 += step1 * veclen)
         for (i0 = 0, l0 = l1; i0 < n0; i0++, l0 += step0 * veclen, k++)
         {
            double d = 0;
            for (j = 0; j < veclen; j++)
               d += data[l0 + j] * data[l0 + j];
            out[k] = (float) Math.sqrt(d);
         }
      return out;
   }
   
   public static float[] get2DSlice(float[] data, int[] dims, int axis, int slice, int veclen)
   {
      if (dims == null || dims.length < 2 || axis < 0 || axis >= dims.length || slice < 0 || slice >= dims[axis])
         return null;
      if (dims.length == 2)
      {
         if (axis == 1)
            return get2DSlice(data, slice * dims[0], dims[0], 1, 1, 1, veclen);
         else
            return get2DSlice(data, slice, 1, 1, dims[1], dims[0], veclen);
      }
      else
      {
         switch (axis)
         { 
         case 0:
            return get2DSlice(data, slice, dims[1], dims[0], dims[2], dims[0] * dims[1], veclen);
         case 1:
            return get2DSlice(data, slice * dims[0], dims[0], 1, dims[2], dims[0] * dims[1], veclen);
         case 2:
            return get2DSlice(data, slice * dims[0] * dims[1], dims[0], 1, dims[1], dims[0], veclen);
         }
      }
      return null;
   }

   public static float[] get2DFloatSlice(float[] data, int[] dims, int axis, int slice, int veclen)
   {
      if (dims == null || dims.length < 2 || axis < 0 || axis >= dims.length || slice < 0 || slice >= dims[axis])
         return null;
      if (dims.length == 2)
      {
         if (axis == 1)
            return get2DFloatSlice(data, slice * dims[0], dims[0], 1, 1, 1, veclen);
         else
            return get2DFloatSlice(data, slice, 1, 1, dims[1], dims[0], veclen);
      }
      else
      {
         switch (axis)
         { 
         case 0:
            return get2DFloatSlice(data, slice, dims[1], dims[0], dims[2], dims[0] * dims[1], veclen);
         case 1:
            return get2DFloatSlice(data, slice * dims[0], dims[0], 1, dims[2], dims[0] * dims[1], veclen);
         case 2:
            return get2DFloatSlice(data, slice * dims[0] * dims[1], dims[0], 1, dims[1], dims[0], veclen);
         }
      }
      return null;
   }

   public static float[] get2DNormSlice(float[] data, int[] dims, int axis, int slice, int veclen)
   {
      if (dims == null || dims.length < 2 || axis < 0 || axis >= dims.length || slice < 0 || slice >= dims[axis])
         return null;
      if (dims.length == 2)
      {
         if (axis == 1)
            return get2DNormSlice(data, slice * dims[0], dims[0], 1, 1, 1, veclen);
         else
            return get2DNormSlice(data, slice, 1, 1, dims[1], dims[0], veclen);
      }
      else
      {
         switch (axis)
         { 
         case 0:
            return get2DNormSlice(data, slice, dims[1], dims[0], dims[2], dims[0] * dims[1], veclen);
         case 1:
            return get2DNormSlice(data, slice * dims[0], dims[0], 1, dims[2], dims[0] * dims[1], veclen);
         case 2:
            return get2DNormSlice(data, slice * dims[0] * dims[1], dims[0], 1, dims[1], dims[0], veclen);
         }
      }
      return null;
   }  

   public static double[] get2DSlice(double[] data, int start, int n0, int step0, int n1, int step1, int veclen)
   {
      double[] out = new double[n0 * n1 * veclen];
      for (int i1 = 0, k = 0, l1 = start * veclen; i1 < n1; i1++, l1 += step1 * veclen)
         for (int i0 = 0, l0 = l1; i0 < n0; i0++, l0 += step0 * veclen)
            for (int j = 0; j < veclen; j++, k++)
               out[k] = data[l0 + j];
      return out;
   }

   public static float[] get2DFloatSlice(double[] data, int start, int n0, int step0, int n1, int step1, int veclen)
   {
      float[] out = new float[n0 * n1 * veclen];
      for (int i1 = 0, k = 0, l1 = start * veclen; i1 < n1; i1++, l1 += step1 * veclen)
         for (int i0 = 0, l0 = l1; i0 < n0; i0++, l0 += step0 * veclen)
            for (int j = 0; j < veclen; j++, k++)
               out[k] = (float)data[l0 + j];
      return out;
   }

   public static float[] get2DNormSlice(double[] data, int start, int n0, int step0, int n1, int step1, int veclen)
   {
      float[] out;
      if (veclen == 1)
      {
         out = get2DFloatSlice(data, start, n0, step0, n1, step1, veclen);
         for (int i = 0; i < out.length; i++)
            out[i] = Math.abs(out[i]);
         return out;
      }
      out = new float[n0 * n1];
      int i0, i1, j, k, l0, l1;
      for (i1 = k = 0, l1 = start * veclen; i1 < n1; i1++, l1 += step1 * veclen)
         for (i0 = 0, l0 = l1; i0 < n0; i0++, l0 += step0 * veclen, k++)
         {
            double d = 0;
            for (j = 0; j < veclen; j++)
               d += data[l0 + j] * data[l0 + j];
            out[k] = (float) Math.sqrt(d);
         }
      return out;
   }
   
   public static double[] get2DSlice(double[] data, int[] dims, int axis, int slice, int veclen)
   {
      if (dims == null || dims.length < 2 || axis < 0 || axis >= dims.length || slice < 0 || slice >= dims[axis])
         return null;
      if (dims.length == 2)
      {
         if (axis == 1)
            return get2DSlice(data, slice * dims[0], dims[0], 1, 1, 1, veclen);
         else
            return get2DSlice(data, slice, 1, 1, dims[1], dims[0], veclen);
      }
      else
      {
         switch (axis)
         { 
         case 0:
            return get2DSlice(data, slice, dims[1], dims[0], dims[2], dims[0] * dims[1], veclen);
         case 1:
            return get2DSlice(data, slice * dims[0], dims[0], 1, dims[2], dims[0] * dims[1], veclen);
         case 2:
            return get2DSlice(data, slice * dims[0] * dims[1], dims[0], 1, dims[1], dims[0], veclen);
         }
      }
      return null;
   }

   public static float[] get2DFloatSlice(double[] data, int[] dims, int axis, int slice, int veclen)
   {
      if (dims == null || dims.length < 2 || axis < 0 || axis >= dims.length || slice < 0 || slice >= dims[axis])
         return null;
      if (dims.length == 2)
      {
         if (axis == 1)
            return get2DFloatSlice(data, slice * dims[0], dims[0], 1, 1, 1, veclen);
         else
            return get2DFloatSlice(data, slice, 1, 1, dims[1], dims[0], veclen);
      }
      else
      {
         switch (axis)
         { 
         case 0:
            return get2DFloatSlice(data, slice, dims[1], dims[0], dims[2], dims[0] * dims[1], veclen);
         case 1:
            return get2DFloatSlice(data, slice * dims[0], dims[0], 1, dims[2], dims[0] * dims[1], veclen);
         case 2:
            return get2DFloatSlice(data, slice * dims[0] * dims[1], dims[0], 1, dims[1], dims[0], veclen);
         }
      }
      return null;
   }

   public static float[] get2DNormSlice(double[] data, int[] dims, int axis, int slice, int veclen)
   {
      if (dims == null || dims.length < 2 || axis < 0 || axis >= dims.length || slice < 0 || slice >= dims[axis])
         return null;
      if (dims.length == 2)
      {
         if (axis == 1)
            return get2DNormSlice(data, slice * dims[0], dims[0], 1, 1, 1, veclen);
         else
            return get2DNormSlice(data, slice, 1, 1, dims[1], dims[0], veclen);
      }
      else
      {
         switch (axis)
         { 
         case 0:
            return get2DNormSlice(data, slice, dims[1], dims[0], dims[2], dims[0] * dims[1], veclen);
         case 1:
            return get2DNormSlice(data, slice * dims[0], dims[0], 1, dims[2], dims[0] * dims[1], veclen);
         case 2:
            return get2DNormSlice(data, slice * dims[0] * dims[1], dims[0], 1, dims[1], dims[0], veclen);
         }
      }
      return null;
   }   
   
 }
