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

import pl.edu.icm.visnow.datasets.VNObject;

/**
 *
 * @author Krzysztof S. Nowinski
 * University of Warsaw, ICM
 */
public class CropDown
{
   /**
    * crops and downsizes data array
    * no test for correct parameters
    * (this is done once at field crop/down)
    */
   public static boolean[] cropDownArray(boolean[] data, int veclen, int[] dims, int[] low, int[] up, int[] down)
   {
      int n = 1;
      for (int i = 0; i < dims.length; i++)
         n *= (up[i] - low[i] - 1) / down[i] + 1;
      boolean[] dt = new boolean[n * veclen];
      switch (dims.length)
      {
      case 3:
         for (int i = low[2], l = 0; i < up[2]; i += down[2])
            for (int j = low[1]; j < up[1]; j += down[1])
               for (int k = low[0], m = (i * dims[1] + j) * dims[0]; k < up[0]; k += down[0], l += veclen)
                  for (int c = 0; c < veclen; c++)
                     dt[l + c] = data[(m + k) * veclen + c];
         break;
      case 2:
         for (int j = low[1], l = 0; j < up[1]; j += down[1])
            for (int k = low[0], m = j * dims[0]; k < up[0]; k += down[0], l += veclen)
               for (int c = 0; c < veclen; c++)
                  dt[l + c] = data[(m + k) * veclen + c];
         break;
      case 1:
         for (int k = low[0], l = 0; k < up[0]; k += down[0], l += veclen)
            for (int c = 0; c < veclen; c++)
               dt[l + c] = data[k * veclen + c];
         break;
      }
      return dt;
   }

   public static byte[] cropDownArray(byte[] data, int veclen, int[] dims, int[] low, int[] up, int[] down)
   {
      int n = 1;
      for (int i = 0; i < dims.length; i++)
         n *= (up[i] - low[i] - 1) / down[i] + 1;
      byte[] dt = new byte[n * veclen];
      switch (dims.length)
      {
      case 3:
         for (int i = low[2], l = 0; i < up[2]; i += down[2])
            for (int j = low[1]; j < up[1]; j += down[1])
               for (int k = low[0], m = (i * dims[1] + j) * dims[0]; k < up[0]; k += down[0], l += veclen)
                  for (int c = 0; c < veclen; c++)
                     dt[l + c] = data[(m + k) * veclen + c];
         break;
      case 2:
         for (int j = low[1], l = 0; j < up[1]; j += down[1])
            for (int k = low[0], m = j * dims[0]; k < up[0]; k += down[0], l += veclen)
               for (int c = 0; c < veclen; c++)
                  dt[l + c] = data[(m + k) * veclen + c];
         break;
      case 1:
         for (int k = low[0], l = 0; k < up[0]; k += down[0], l += veclen)
            for (int c = 0; c < veclen; c++)
               dt[l + c] = data[k * veclen + c];
         break;
      }
      return dt;
   }
   public static short[] cropDownArray(short[] data, int veclen, int[] dims, int[] low, int[] up, int[] down)
   {
      int n = 1;
      for (int i = 0; i < dims.length; i++)
         n *= (up[i] - low[i] - 1) / down[i] + 1;
      short[] dt = new short[n * veclen];
      switch (dims.length)
      {
      case 3:
         for (int i = low[2], l = 0; i < up[2]; i += down[2])
            for (int j = low[1]; j < up[1]; j += down[1])
               for (int k = low[0], m = (i * dims[1] + j) * dims[0]; k < up[0]; k += down[0], l += veclen)
                  for (int c = 0; c < veclen; c++)
                     dt[l + c] = data[(m + k) * veclen + c];
         break;
      case 2:
         for (int j = low[1], l = 0; j < up[1]; j += down[1])
            for (int k = low[0], m = j * dims[0]; k < up[0]; k += down[0], l += veclen)
               for (int c = 0; c < veclen; c++)
                  dt[l + c] = data[(m + k) * veclen + c];
         break;
      case 1:
         for (int k = low[0], l = 0; k < up[0]; k += down[0], l += veclen)
            for (int c = 0; c < veclen; c++)
               dt[l + c] = data[k * veclen + c];
         break;
      }
      return dt;
   }

   public static int[] cropDownArray(int[] data, int veclen, int[] dims, int[] low, int[] up, int[] down)
   {
      int n = 1;
      for (int i = 0; i < dims.length; i++)
         n *= (up[i] - low[i] - 1) / down[i] + 1;
      int[] dt = new int[n * veclen];
      switch (dims.length)
      {
      case 3:
         for (int i = low[2], l = 0; i < up[2]; i += down[2])
            for (int j = low[1]; j < up[1]; j += down[1])
               for (int k = low[0], m = (i * dims[1] + j) * dims[0]; k < up[0]; k += down[0], l += veclen)
                  for (int c = 0; c < veclen; c++)
                     dt[l + c] = data[(m + k) * veclen + c];
         break;
      case 2:
         for (int j = low[1], l = 0; j < up[1]; j += down[1])
            for (int k = low[0], m = j * dims[0]; k < up[0]; k += down[0], l += veclen)
               for (int c = 0; c < veclen; c++)
                  dt[l + c] = data[(m + k) * veclen + c];
         break;
      case 1:
         for (int k = low[0], l = 0; k < up[0]; k += down[0], l += veclen)
            for (int c = 0; c < veclen; c++)
               dt[l + c] = data[k * veclen + c];
         break;
      }
      return dt;
   }

   public static float[] cropDownArray(float[] data, int veclen, int[] dims, int[] low, int[] up, int[] down)
   {
      int n = 1;
      for (int i = 0; i < dims.length; i++)
         n *= (up[i] - low[i] - 1) / down[i] + 1;
      float[] dt = new float[n * veclen];
      switch (dims.length)
      {
      case 3:
         for (int i = low[2], l = 0; i < up[2]; i += down[2])
            for (int j = low[1]; j < up[1]; j += down[1])
               for (int k = low[0], m = (i * dims[1] + j) * dims[0]; k < up[0]; k += down[0], l += veclen)
                  for (int c = 0; c < veclen; c++)
                     dt[l + c] = data[(m + k) * veclen + c];
         break;
      case 2:
         for (int j = low[1], l = 0; j < up[1]; j += down[1])
            for (int k = low[0], m = j * dims[0]; k < up[0]; k += down[0], l += veclen)
               for (int c = 0; c < veclen; c++)
                  dt[l + c] = data[(m + k) * veclen + c];
         break;
      case 1:
         for (int k = low[0], l = 0; k < up[0]; k += down[0], l += veclen)
            for (int c = 0; c < veclen; c++)
               dt[l + c] = data[k * veclen + c];
         break;
      }
      return dt;
   }

   public static double[] cropDownArray(double[] data, int veclen, int[] dims, int[] low, int[] up, int[] down)
   {
      int n = 1;
      for (int i = 0; i < dims.length; i++)
         n *= (up[i] - low[i] - 1) / down[i] + 1;
      double[] dt = new double[n * veclen];
      switch (dims.length)
      {
      case 3:
         for (int i = low[2], l = 0; i < up[2]; i += down[2])
            for (int j = low[1]; j < up[1]; j += down[1])
               for (int k = low[0], m = (i * dims[1] + j) * dims[0]; k < up[0]; k += down[0], l += veclen)
                  for (int c = 0; c < veclen; c++)
                     dt[l + c] = data[(m + k) * veclen + c];
         break;
      case 2:
         for (int j = low[1], l = 0; j < up[1]; j += down[1])
            for (int k = low[0], m = j * dims[0]; k < up[0]; k += down[0], l += veclen)
               for (int c = 0; c < veclen; c++)
                  dt[l + c] = data[(m + k) * veclen + c];
         break;
      case 1:
         for (int k = low[0], l = 0; k < up[0]; k += down[0], l += veclen)
            for (int c = 0; c < veclen; c++)
               dt[l + c] = data[k * veclen + c];
         break;
      }
      return dt;
   }
   
   public static String[] cropDownArray(String[] data, int veclen, int[] dims, int[] low, int[] up, int[] down)
   {
      int n = 1;
      for (int i = 0; i < dims.length; i++)
         n *= (up[i] - low[i] - 1) / down[i] + 1;
      String[] dt = new String[n * veclen];
      switch (dims.length)
      {
      case 3:
         for (int i = low[2], l = 0; i < up[2]; i += down[2])
            for (int j = low[1]; j < up[1]; j += down[1])
               for (int k = low[0], m = (i * dims[1] + j) * dims[0]; k < up[0]; k += down[0], l += veclen)
                  for (int c = 0; c < veclen; c++)
                     dt[l + c] = data[(m + k) * veclen + c];
         break;
      case 2:
         for (int j = low[1], l = 0; j < up[1]; j += down[1])
            for (int k = low[0], m = j * dims[0]; k < up[0]; k += down[0], l += veclen)
               for (int c = 0; c < veclen; c++)
                  dt[l + c] = data[(m + k) * veclen + c];
         break;
      case 1:
         for (int k = low[0], l = 0; k < up[0]; k += down[0], l += veclen)
            for (int c = 0; c < veclen; c++)
               dt[l + c] = data[k * veclen + c];
         break;
      }
      return dt;
   }

   public static VNObject[] cropDownArray(VNObject[] data, int veclen, int[] dims, int[] low, int[] up, int[] down)
   {
      int n = 1;
      for (int i = 0; i < dims.length; i++)
         n *= (up[i] - low[i] - 1) / down[i] + 1;
      VNObject[] dt = new VNObject[n * veclen];
      switch (dims.length)
      {
      case 3:
         for (int i = low[2], l = 0; i < up[2]; i += down[2])
            for (int j = low[1]; j < up[1]; j += down[1])
               for (int k = low[0], m = (i * dims[1] + j) * dims[0]; k < up[0]; k += down[0], l += veclen)
                  for (int c = 0; c < veclen; c++)
                     dt[l + c] = data[(m + k) * veclen + c];
         break;
      case 2:
         for (int j = low[1], l = 0; j < up[1]; j += down[1])
            for (int k = low[0], m = j * dims[0]; k < up[0]; k += down[0], l += veclen)
               for (int c = 0; c < veclen; c++)
                  dt[l + c] = data[(m + k) * veclen + c];
         break;
      case 1:
         for (int k = low[0], l = 0; k < up[0]; k += down[0], l += veclen)
            for (int c = 0; c < veclen; c++)
               dt[l + c] = data[k * veclen + c];
         break;
      }
      return dt;
   }


   public static byte[] downArray(byte[] data, int veclen, int[] dims, int[] down)
   {
      return cropDownArray(data, veclen, dims, new int[]{0,0,0}, dims, down);
   }

   public static short[] downArray(short[] data, int veclen, int[] dims, int[] down)
   {
      return cropDownArray(data, veclen, dims, new int[]{0,0,0}, dims, down);
   }

   public static int[] downArray(int[] data, int veclen, int[] dims, int[] down)
   {
      return cropDownArray(data, veclen, dims, new int[]{0,0,0}, dims, down);
   }

   public static float[] downArray(float[] data, int veclen, int[] dims, int[] down)
   {
      return cropDownArray(data, veclen, dims, new int[]{0,0,0}, dims, down);
   }

   public static double[] downArray(double[] data, int veclen, int[] dims, int[] down)
   {
      return cropDownArray(data, veclen, dims, new int[]{0,0,0}, dims, down);
   }

   public static String[] downArray(String[] data, int veclen, int[] dims, int[] down)
   {
      return cropDownArray(data, veclen, dims, new int[]{0,0,0}, dims, down);
   }

   public static VNObject[] downArray(VNObject[] data, int veclen, int[] dims, int[] down)
   {
      return cropDownArray(data, veclen, dims, new int[]{0,0,0}, dims, down);
   }

   private CropDown()
   {
      
   }

}
