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
 * Sorts in ascending order
 *
 * @author Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class HeapSort
{

   public static void sort(int[] a0, boolean ascending)
   {
      heapsortInt(a0.length, 1, a0);
      if (ascending)
         return;
      int n = a0.length - 1;
      for (int i = 0; i < a0.length / 2; i++)
      {
         int j = a0[i];
         a0[i] = a0[n - i];
         a0[n - i] = j;
      }
   }

   public static void sort(int[] a0, int vl, boolean ascending)
   {
      heapsortInt(a0.length / vl, vl, a0);
      if (ascending)
         return;
      int n = a0.length - vl;
      for (int i = 0; i < a0.length / 2; i += vl)
         for (int j = 0; j < vl; j++)
         {
            int k = a0[i + j];
            a0[i + j] = a0[n - i + j];
            a0[n - i + j] = k;
         }
   }

   public static void sort(int[] a0, int n, int vl)
   {
      heapsortInt(n, vl, a0);
   }

   public static void sort(float[] a0)
   {
      heapsortFloat(a0.length, 1, a0);
   }

   public static void sort(float[] a0, int vl)
   {
      heapsortFloat(a0.length / vl, vl, a0);
   }

   public static void sort(float[] a0, int n, int vl)
   {
      heapsortFloat(n, vl, a0);
   }

   public static void sort(double[] a0)
   {
      heapsortDouble(a0.length, 1, a0);
   }

   public static void sort(double[] a0, int vl)
   {
      heapsortDouble(a0.length / vl, vl, a0);
   }

   public static void sort(double[] a0, int n, int vl)
   {
      heapsortDouble(n, vl, a0);
   }

   public static void heapsortInt(int n, int vlen, int a[])
   {
      int k, l, m, v, w, t;
      //buildheap();
      for (v = n / 2 - 1; v >= 0; v--)
      //downheap (v);
      {
         w = 2 * v + 1;
         while (w < n)
         {
            if (w + 1 < n && a[vlen * (w + 1)] > a[vlen * w])
               w++;
            if (a[vlen * v] >= a[vlen * w])
               break;
            for (k = 0, l = v * vlen, m = w * vlen; k < vlen; k++, l++, m++)
            {
               t = a[l];
               a[l] = a[m];
               a[m] = t;
            }
            v = w;
            w = 2 * v + 1;
         }
      }
      while (n > 1)
      {
         n--;
         for (k = 0, l = 0, m = n * vlen; k < vlen; k++, l++, m++)
         {
            t = a[l];
            a[l] = a[m];
            a[m] = t;
         }
         //downheap (0);
         v = 0;
         w = 1;
         while (w < n)
         {
            if (w + 1 < n && a[vlen * (w + 1)] > a[vlen * w])
               w++;
            if (a[vlen * v] >= a[vlen * w])
               break;
            for (k = 0, l = v * vlen, m = w * vlen; k < vlen; k++, l++, m++)
            {
               t = a[l];
               a[l] = a[m];
               a[m] = t;
            }
            v = w;
            w = 2 * v + 1;
         }
      }
   }

   public static void heapsortLong(int n, int vlen, long a[])
   {
      int k, l, m, v, w;
      long t;
      //buildheap();
      for (v = n / 2 - 1; v >= 0; v--)
      //downheap (v);
      {
         w = 2 * v + 1;
         while (w < n)
         {
            if (w + 1 < n && a[vlen * (w + 1)] > a[vlen * w])
               w++;
            if (a[vlen * v] >= a[vlen * w])
               break;
            for (k = 0, l = v * vlen, m = w * vlen; k < vlen; k++, l++, m++)
            {
               t = a[l];
               a[l] = a[m];
               a[m] = t;
            }
            v = w;
            w = 2 * v + 1;
         }
      }
      while (n > 1)
      {
         n--;
         for (k = 0, l = 0, m = n * vlen; k < vlen; k++, l++, m++)
         {
            t = a[l];
            a[l] = a[m];
            a[m] = t;
         }
         //downheap (0);
         v = 0;
         w = 1;
         while (w < n)
         {
            if (w + 1 < n && a[vlen * (w + 1)] > a[vlen * w])
               w++;
            if (a[vlen * v] >= a[vlen * w])
               break;
            for (k = 0, l = v * vlen, m = w * vlen; k < vlen; k++, l++, m++)
            {
               t = a[l];
               a[l] = a[m];
               a[m] = t;
            }
            v = w;
            w = 2 * v + 1;
         }
      }
   }

   /**
    * Sort arrays of vectors by all columns starting from first.
    *
    * @param n
    * @param vlen
    * @param a
    * @param indexes Indexes permutation
    */
   public static void heapsortFloatFull(int n, int vlen, float a[], int indexes[])
   {
      heapsortFloat(n, vlen, a, indexes, 0, 0);
      int len;
      int ind;
      int varNum = 1;
      while (varNum < vlen)
      {
         len = 1;
         ind = 1;
         while (ind < n)
         {
            if (a[ind * vlen + varNum - 1] != a[(ind - 1) * vlen + varNum - 1])
            {
               if (len > 1)
                  heapsortFloat(len, vlen, a, indexes, varNum, ind - len);
               len = 0;
            }
            ++ind;
            ++len;
         }
         if (len > 1)
            heapsortFloat(len, vlen, a, indexes, varNum, ind - len);
         ++varNum;
      }
   }

   /**
    * Sort arrays of vectors by all columns starting from first.
    *
    * @param n
    * @param vlen
    * @param a
    * @param indexes Indexes permutation
    */
   public static void heapsortIntFull(int n, int vlen, int a[], int indexes[])
   {
      heapsortInt(n, vlen, a, indexes, 0, 0);
      int len;
      int ind;
      int varNum = 1;
      while (varNum < vlen)
      {
         len = 1;
         ind = 1;
         while (ind < n)
         {
            if (a[ind * vlen + varNum - 1] != a[(ind - 1) * vlen + varNum - 1])
            {
               if (len > 1)
                  heapsortInt(len, vlen, a, indexes, varNum, ind - len);
               len = 0;
            }
            ++ind;
            ++len;
         }
         if (len > 1)
            heapsortInt(len, vlen, a, indexes, varNum, ind - len);
         ++varNum;
      }
   }

   /**
    * Sorting by varNum. Retrieves also index permutation.
    *
    * @param n Number of vectors to sort
    * @param vlen Vector length
    * @param a Array to sort
    * @param indexes Array of indexes [length = a.length/vlen]
    * @param varNum Sort by varNum
    * @param vectorOffset Offset as vector index
    */
   public static void heapsortFloat(int n, int vlen, float a[], int indexes[], int varNum, int vectorOffset)
   {
      int arrOffset = vectorOffset * vlen;
      assert (varNum < vlen);
      int k, l, m, v, w, ti;
      float t;
      //buildheap();
      for (v = n / 2 - 1; v >= 0; v--) //downheap (v);
      {
         w = 2 * v + 1;
         while (w < n)
         {
            if (w + 1 < n && a[vlen * (w + 1) + varNum + arrOffset] > a[vlen * w + varNum + arrOffset])
               w++;
            if (a[vlen * v + varNum + arrOffset] >= a[vlen * w + varNum + arrOffset])
               break;
            for (k = 0, l = v * vlen, m = w * vlen; k < vlen; k++, l++, m++)
            {
               t = a[l + arrOffset];
               a[l + arrOffset] = a[m + arrOffset];
               a[m + arrOffset] = t;
            }
            ti = indexes[v + vectorOffset];
            indexes[v + vectorOffset] = indexes[w + vectorOffset];
            indexes[w + vectorOffset] = ti;
            v = w;
            w = 2 * v + 1;
         }
      }
      while (n > 1)
      {
         n--;
         for (k = 0, l = 0, m = n * vlen; k < vlen; k++, l++, m++)
         {
            t = a[l + arrOffset];
            a[l + arrOffset] = a[m + arrOffset];
            a[m + arrOffset] = t;
         }
         ti = indexes[vectorOffset];
         indexes[vectorOffset] = indexes[n + vectorOffset];
         indexes[n + vectorOffset] = ti;

         //downheap (0);
         v = 0;
         w = 1;
         while (w < n)
         {
            if (w + 1 < n && a[vlen * (w + 1) + varNum + arrOffset] > a[vlen * w + varNum + arrOffset])
               w++;
            if (a[vlen * v + varNum + arrOffset] >= a[vlen * w + varNum + arrOffset])
               break;
            for (k = 0, l = v * vlen, m = w * vlen; k < vlen; k++, l++, m++)
            {
               t = a[l + arrOffset];
               a[l + arrOffset] = a[m + arrOffset];
               a[m + arrOffset] = t;
            }
            ti = indexes[v + vectorOffset];
            indexes[v + vectorOffset] = indexes[w + vectorOffset];
            indexes[w + vectorOffset] = ti;
            v = w;
            w = 2 * v + 1;
         }
      }
   }

   /**
    * Sorting by varNum. Retrieves also index permutation.
    *
    * @param n Number of vectors to sort
    * @param vlen Vector length
    * @param a Array to sort
    * @param indexes Array of indexes [length = a.length/vlen]
    * @param varNum Sort by varNum
    * @param vectorOffset Offset as vector index
    */
   public static void heapsortInt(int n, int vlen, int a[], int indexes[], int varNum, int vectorOffset)
   {
      int arrOffset = vectorOffset * vlen;
      assert (varNum < vlen);
      int k, l, m, v, w, ti;
      int t;
      //buildheap();
      for (v = n / 2 - 1; v >= 0; v--) //downheap (v);
      {
         w = 2 * v + 1;
         while (w < n)
         {
            if (w + 1 < n && a[vlen * (w + 1) + varNum + arrOffset] > a[vlen * w + varNum + arrOffset])
               w++;
            if (a[vlen * v + varNum + arrOffset] >= a[vlen * w + varNum + arrOffset])
               break;
            for (k = 0, l = v * vlen, m = w * vlen; k < vlen; k++, l++, m++)
            {
               t = a[l + arrOffset];
               a[l + arrOffset] = a[m + arrOffset];
               a[m + arrOffset] = t;
            }
            ti = indexes[v + vectorOffset];
            indexes[v + vectorOffset] = indexes[w + vectorOffset];
            indexes[w + vectorOffset] = ti;
            v = w;
            w = 2 * v + 1;
         }
      }
      while (n > 1)
      {
         n--;
         for (k = 0, l = 0, m = n * vlen; k < vlen; k++, l++, m++)
         {
            t = a[l + arrOffset];
            a[l + arrOffset] = a[m + arrOffset];
            a[m + arrOffset] = t;
         }
         ti = indexes[vectorOffset];
         indexes[vectorOffset] = indexes[n + vectorOffset];
         indexes[n + vectorOffset] = ti;

         //downheap (0);
         v = 0;
         w = 1;
         while (w < n)
         {
            if (w + 1 < n && a[vlen * (w + 1) + varNum + arrOffset] > a[vlen * w + varNum + arrOffset])
               w++;
            if (a[vlen * v + varNum + arrOffset] >= a[vlen * w + varNum + arrOffset])
               break;
            for (k = 0, l = v * vlen, m = w * vlen; k < vlen; k++, l++, m++)
            {
               t = a[l + arrOffset];
               a[l + arrOffset] = a[m + arrOffset];
               a[m + arrOffset] = t;
            }
            ti = indexes[v + vectorOffset];
            indexes[v + vectorOffset] = indexes[w + vectorOffset];
            indexes[w + vectorOffset] = ti;
            v = w;
            w = 2 * v + 1;
         }
      }
   }

   /**
    * Sorting by first element of a vector. Retrieves also index permutation.
    *
    * @param n Number of vectors to sort
    * @param vlen Vector length
    * @param a Array to sort
    * @param indexes Array of indexes [length = a.length/vlen]
    */
   public static void heapsortFloat(int n, int vlen, float a[], int indexes[])
   {
      int k, l, m, v, w, ti;
      float t;
      //buildheap();
      for (v = n / 2 - 1; v >= 0; v--) //downheap (v);
      {
         w = 2 * v + 1;
         while (w < n)
         {
            if (w + 1 < n && a[vlen * (w + 1)] > a[vlen * w])
               w++;
            if (a[vlen * v] >= a[vlen * w])
               break;
            for (k = 0, l = v * vlen, m = w * vlen; k < vlen; k++, l++, m++)
            {
               t = a[l];
               a[l] = a[m];
               a[m] = t;
            }
            ti = indexes[v];
            indexes[v] = indexes[w];
            indexes[w] = ti;
            v = w;
            w = 2 * v + 1;
         }
      }
      while (n > 1)
      {
         n--;
         for (k = 0, l = 0, m = n * vlen; k < vlen; k++, l++, m++)
         {
            t = a[l];
            a[l] = a[m];
            a[m] = t;
         }
         ti = indexes[0];
         indexes[0] = indexes[n];
         indexes[n] = ti;

         //downheap (0);
         v = 0;
         w = 1;
         while (w < n)
         {
            if (w + 1 < n && a[vlen * (w + 1)] > a[vlen * w])
               w++;
            if (a[vlen * v] >= a[vlen * w])
               break;
            for (k = 0, l = v * vlen, m = w * vlen; k < vlen; k++, l++, m++)
            {
               t = a[l];
               a[l] = a[m];
               a[m] = t;
            }
            ti = indexes[v];
            indexes[v] = indexes[w];
            indexes[w] = ti;
            v = w;
            w = 2 * v + 1;
         }
      }
   }

   public static void heapsortFloat(int n, int vlen, float a[])
   {
      int k, l, m, v, w;
      float t;
      //buildheap();
      for (v = n / 2 - 1; v >= 0; v--)
      //downheap (v);
      {
         w = 2 * v + 1;
         while (w < n)
         {
            if (w + 1 < n && a[vlen * (w + 1)] > a[vlen * w])
               w++;
            if (a[vlen * v] >= a[vlen * w])
               break;
            for (k = 0, l = v * vlen, m = w * vlen; k < vlen; k++, l++, m++)
            {
               t = a[l];
               a[l] = a[m];
               a[m] = t;
            }
            v = w;
            w = 2 * v + 1;
         }
      }
      while (n > 1)
      {
         n--;
         for (k = 0, l = 0, m = n * vlen; k < vlen; k++, l++, m++)
         {
            t = a[l];
            a[l] = a[m];
            a[m] = t;
         }
         //downheap (0);
         v = 0;
         w = 1;
         while (w < n)
         {
            if (w + 1 < n && a[vlen * (w + 1)] > a[vlen * w])
               w++;
            if (a[vlen * v] >= a[vlen * w])
               break;
            for (k = 0, l = v * vlen, m = w * vlen; k < vlen; k++, l++, m++)
            {
               t = a[l];
               a[l] = a[m];
               a[m] = t;
            }
            v = w;
            w = 2 * v + 1;
         }
      }
   }

   public static void heapsortDouble(int n, int vlen, double a[])
   {
      int k, l, m, v, w;
      double t;
      //buildheap();
      for (v = n / 2 - 1; v >= 0; v--)
      //downheap (v);
      {
         w = 2 * v + 1;
         while (w < n)
         {
            if (w + 1 < n && a[vlen * (w + 1)] > a[vlen * w])
               w++;
            if (a[vlen * v] >= a[vlen * w])
               break;
            for (k = 0, l = v * vlen, m = w * vlen; k < vlen; k++, l++, m++)
            {
               t = a[l];
               a[l] = a[m];
               a[m] = t;
            }
            v = w;
            w = 2 * v + 1;
         }
      }
      while (n > 1)
      {
         n--;
         for (k = 0, l = 0, m = n * vlen; k < vlen; k++, l++, m++)
         {
            t = a[l];
            a[l] = a[m];
            a[m] = t;
         }
         //downheap (0);
         v = 0;
         w = 1;
         while (w < n)
         {
            if (w + 1 < n && a[vlen * (w + 1)] > a[vlen * w])
               w++;
            if (a[vlen * v] >= a[vlen * w])
               break;
            for (k = 0, l = v * vlen, m = w * vlen; k < vlen; k++, l++, m++)
            {
               t = a[l];
               a[l] = a[m];
               a[m] = t;
            }
            v = w;
            w = 2 * v + 1;
         }
      }
   }

   public static void main(String args[])
   {
      for (int n = 100; n <= 1000000; n *= 2)
      {
         int[] t = new int[n];
         for (int i = 0; i < t.length; i++)
            t[i] = (int) (n * Math.random());
         long s = System.currentTimeMillis();
         sort(t, 2, true);
         System.out.printf("%6d %7d%n",n,System.currentTimeMillis() - s);
         
      }
//      sort(t2, 2, false);
//      for (int i = 0; i < t.length; i += 2)
//         System.out.printf("%4d %4d   %4d %4d   %4d %4d %n", t[i], t[i + 1], t1[i], t1[i + 1], t2[i], t2[i + 1]);
   }

   private HeapSort()
   {
   }
}
