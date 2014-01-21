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
exception statement from your version. */
//</editor-fold>

package pl.edu.icm.visnow.lib.utils;

import static java.lang.Math.*;
/*
 * Range.java
 *
 * Created on November 19, 2003, 2:25 PM
 */

/**
 * A class for "nice" range segments. It splits a given range into "buckets" (subranges) that begin
 * and end in some "nice", round numbers, such as: <ul>
 * <li>1, 1.5, 2, 2.5, ... or </li>
 * <li>1, 2, 3, ... or </li>
 * <li>1, 2, 5, 10, 20, 50, ... etc. </li></ul>
 *
 * instead of values computed by a simple formula:
 * <code>(upper_bound - lower_bound) / segment_count)</code>, which usually gives awful numbers with
 * many digits after dot.
 *
 * This class enables to display such "nice" values in legend or use them for displaying isolines.
 *
 * @author Krzysztof S. Nowinski (know@icm.edu.pl) Warsaw University Interdisciplinary Centre for
 * Mathematical and Computational Modelling
 */
public class Range
{

   /**
    * Step between ticks. It could be set to (1, 2 or 5) * 10^N, N - integer
    */
   private float step;
   /**
    * Number of ticks.
    */
   private int nsteps;
   /**
    * Original range, in contrast to {@link #range range}.
    */
   private float[] rangeOrig;
   /**
    * Calculated range - with "nice" numbers.
    */
   private float[] range;
   private static final float[] std_range =
   {
      0.f, 1.f,
   };


   /**
    * Creates a new instance of Range.
    * @param nx suggested number of intervals (actual number of intervals can be different
    * due to round interval step policy)
    * @param low lower bound of the range
    * @param up upper bound of the range
    * @param inner if false, the range will cover the <low, up> interval, if true, the range will
    * be included in this interval
    */
   public Range(int nx, float low, float up, boolean inner)
   {
      if (low >= up)
      {
         float c = .5f * (low + up);
         low = c - 1;
         up = c + 1;
      }
      range = new float[2];
      rangeOrig = new float[2];
      rangeOrig[0] = low;
      rangeOrig[1] = up;
      calculateRange(roundF((up - low) / nx), low, up, inner);
   }

   /**
    * Conveniency wrapper for Range(int nx, float low, float up, boolean inner)
    * @param nx suggested number of intervals (actual number of intervals can be different
    * due to round interval step policy)
    * @param low lower bound of the range
    * @param up upper bound of the range
    * the range will  be included in this interval
    */
   public Range(int nx, float low, float up)
   {
      this(nx, low, up, true);
   }

   public Range(float[] r, int width)
   {
      this(r[0], r[1], width);
   }

   /**
    * Conveniency wrapper for Range(int nx, float low, float up, boolean inner)
    * @param low lower bound of the range
    * @param up upper bound of the range
    * @param width of a widget (slider, colormap legend) covering the interval <low, up> that will be labeled
    * according to the range.step and range.nsteps
    */
   public Range(float low, float up, int width, boolean inner)
   {
      this((int)(Math.max(width, 200) / 50.f), low, up, inner);
   }

   public Range(float low, float up, int width)
   {
      this(low, up, width, true);
   }

   public Range(float[] r, int width, boolean inner)
   {
      this(r[0], r[1], width, inner);
   }

   public Range()
   {
      this(std_range, 400);
   }

   /**
    * Sets {@link #step step} and calculates {@link #range range} (a computed range with "nice"
    * numbers)
    *
    * @param tx range step
    * @param low lower bound of the range
    * @param up upper bound of the range
    * @param inner should be bounds included in the computed {@link #range range}?
    */
   private void calculateRange(float tx, float low, float up, boolean inner)
   {
      step = tx;

      if (up == low)
      {
         range[0] = range[1] = low;
         nsteps = 2;
      } else
      {
         range[0] = ((int) (low / tx)) * tx;
         if (range[0] < low && inner)
            range[0] += tx;
         if (range[0] > low && !inner)
            range[0] -= tx;

         range[1] = ((int) (up / tx)) * tx;
         if (range[1] < up && !inner)
            range[1] += tx;
         if (range[1] > up && inner)
            range[1] -= tx;

         nsteps = (int) ((range[1] - range[0]) / step);
      }
      if (nsteps < 2)
         nsteps = 2;
   }

   public int getNsteps()
   {
      return nsteps;
   }

   public float[] getRange()
   {
      return this.range;
   }

   public float[] getRangeOrig()
   {
      return this.rangeOrig;
   }

   public float getStep()
   {
      return step;
   }

   public float getLow()
   {
      return this.range[0];
   }

   public float getUp()
   {
      return this.range[1];
   }

   /**
    * rounds a given double to nearest nice value (1*10^k, 2*10^k or 5*10^k) 
    * @param x unrounded step value
    * @return x rounded down to the nearest nice number
    */
   public static double roundD(double x)
   {
      int e = (int)(Math.log10(x) + 1000) - 1000;
      double r = x;
      if (e < 0)
         for (int i = e; i < 0; i++)
            r *= 10;
      else if (e > 0)
         for (int i = 0; i < e; i++)
            r /= 10;
      if (r <2)
         r = 1;
      else if (r < 5)
         r = 2;
      else
         r = 5;
      if (e < 0)
         for (int i = e; i < 0; i++)
            r /= 10;
      else if (e > 0)
         for (int i = 0; i < e; i++)
            r *= 10;
      return r;
   }
   
   /**
    * rounds a given float to nearest nice value (1*10^k, 2*10^k or 5*10^k) 
    * @param x unrounded step value
    * @return x rounded down to the nearest nice number
    */
   public static float roundF(float x)
   {
      return (float)roundD(1. * x);
   }
   
/**
 * creates array of equally spaced floats within a given range to be used e.g. as threshold values for isolines, slider labels etc.
 * @param rMin low range value
 * @param rMax top range value
 * @param d interval (usually a "round" number)
 * @return array of float multiples of d included in [rMin,rMax]
 */
   public static float[] createLinearRange(float rMin, float rMax, float d)
   {
      if (d <= 0 || rMax < rMin)
         return null;
      int l = (int) (rMin / d);
      if (rMin > 0) l += 1;
      int u = (int) (rMax / d);
      if (rMax < 0) u -= 1;
      if (u < l)
         return null;
      float[] t = new float[u - l + 1];
      for (int i = 0; i + l <= u; i++)
         t[i] = (l + i) * d;
      return t;
   }

/**
 * creates array of equally spaced floats within a given range to be used e.g. as threshold values for isolines, slider labels etc.
 * @param nDiv suggested number of values (since interval between values is rounded to 1*10^k, 2*10^k or 5*10^k, the
 * actual number of values returned can be different
 * @param rangeMin low range value
 * @param rangeMax top range value
 * @return array of float multiples of d included in [rMin,rMax]
 */
   public static float[] createLinearRange(int nDiv, float rangeMin, float rangeMax)
   {
      double r = rangeMax - rangeMin;
      if (r <= 0)
         r = 1;
      int iLogr = (int) (log10(r) + 1000) - 1000;
      double mr = r / pow(10., 1. * iLogr);
      float d;
      float rMin;
      float rMax;
      float[] t;
      if (nDiv < 1)
         nDiv = 1;
      while (nDiv > mr)
      {
         mr *= 10;
         iLogr -= 1;
      }
      mr /= nDiv;
      if (mr < 2)
         mr = 2;
      else if (mr < 5)
         mr = 5;
      else
         mr = 10;
      d = (float) mr;
      if (iLogr > 0)
         for (int i = 0; i < iLogr; i++)
            d *= 10;
      if (iLogr < 0)
         for (int i = 0; i > iLogr; i--)
            d /= 10;
      rMin = d * ((int) (rangeMin / d));
      while (rMin < rangeMin)
         rMin += d;
      rMax = d * ((int) (rangeMax / d));
      while (rMax > rangeMax)
         rMax -= d;
      int l = (int) (rMin / d) - (int) (rangeMin / d);
      int u = (int) (rMax / d) - (int) (rangeMin / d);
      t = new float[u - l + 1];
      for (int i = 0; i + l <= u; i++)
         t[i] = rMin + i * d;
      return t;
   }

/**
 * creates array of equally spaced floats within a given range to be used e.g. as threshold values for isolines, slider labels etc.
 * @param nVals suggested number of values (since interval between values is rounded to 1*10^k, 2*10^k or 5*10^k, the
 * actual number of values returned can be different
 * @param rangeMin low range value
 * @param rangeMax top range value
 * @param equallySpacedDecades method of choosing values within a decade
 * @return array of logarithmically spaced values included in [rangeMin,rangeMax]
 * The method computes the number of decades spanning [rangeMin,rangeMax], e.g. 4 decades [.01:.1] [.1:1], [1:10], [10:100]
 * for [.01 ... 100] and divides each decade into equal (if equallySpacedDecades is true) or approximately proportional intervals
 */
   public static float[] createLogRange(int nVals, float rangeMin, float rangeMax, boolean equallySpacedDecades)
   {
      float[][] decades = new float[][]
      {
         {1 },
         {1, 2, 5},
         {1, 2, 3, 4, 6, 8},
         {1, 1.5f, 2, 2.5f, 3, 3.5f, 4, 5, 6, 7, 8, 9},
         {1, 1.2f, 1.4f, 1.6f, 1.8f, 2, 2.5f, 3, 3.5f, 4, 4.5f, 5, 6, 7, 8, 9},
         {1, 1.1f, 1.2f, 1.3f, 1.4f , 1.5f, 1.6f, 1.7f, 1.8f, 1.9f, 2, 2.2f, 2.4f, 2.6f, 2.8f, 3, 3.2f, 3.4f, 3.6f, 3.8f, 4, 4.5f, 5, 5.5f, 6, 6.5f, 7, 7.5f, 8, 8.5f, 9, 9.5f}
      };
      float[][] eqDecades = new float[][]
      {
         {1},
         {1, 5},
         {1, 2, 4, 6, 8},
         {1, 2, 3, 4, 5, 6, 7, 8, 9},
         {1, 1.5f, 2, 2.5f, 3, 3.5f, 4, 4.5f, 5, 5.5f, 6, 6.5f, 7, 7.5f, 8, 8.5f, 9, 9.5f},
         {1, 1.2f, 1.4f, 1.6f, 1.8f, 2, 2.2f, 2.4f, 2.6f, 2.8f, 3, 3.2f, 3.4f, 3.6f, 3.8f, 4, 4.2f, 4.4f, 4.6f, 4.8f,
          5, 5.2f, 5.4f, 5.6f, 5.8f, 6, 6.2f, 6.4f, 6.6f, 6.8f, 7, 7.2f, 7.4f, 7.6f, 7.8f, 8, 8.2f, 8.4f, 8.6f, 8.8f,
          9, 9.2f, 9.4f, 9.6f, 9.8f}
      };
      float[] logThrs = null;
      float[] decadeThrs = null;
      if (equallySpacedDecades)
         decadeThrs = eqDecades[min(nVals / 2, eqDecades.length - 1)];
      else
         decadeThrs = decades[min(nVals, decades.length - 1)];
      nVals = decadeThrs.length;
      try
      {
         if (rangeMin * rangeMax < 0)
         {
            rangeMax = max(abs(rangeMin), abs(rangeMax));
            rangeMin = rangeMax / 1000;
         } else
         {
            float u = min(abs(rangeMin), abs(rangeMax));
            rangeMax = max(abs(rangeMin), abs(rangeMax));
            if (u > 0)
               rangeMin = u;
            else
               rangeMin = rangeMax / 1000;
         }
         double logr0 = log10(rangeMin);
         double logr1 = log10(rangeMax);
         if (logr1 < logr0 + 1)
            logr1 = logr0 + 1;
         int iLogr0 = (int) (logr0 + 100.1) - 100;
         int iLogr1 = (int) (logr1 + 100.9) - 100;
         if (iLogr0 == iLogr1)
            iLogr1 = iLogr0 + 1;
         logThrs = new float[2 * (nVals * (iLogr1 - iLogr0) + 1) + 1];
         for (int i = iLogr0, k = 0; i < iLogr1; i++)
         {
            float v = (float) pow(10, i);
            for (int j = 0; j < nVals; j++, k++)
               logThrs[k] = decadeThrs[j] * v;
         }
         logThrs[nVals * (iLogr1 - iLogr0)] = decadeThrs[0] * (float) pow(10, iLogr1);
         for (int i = 0; i < nVals * (iLogr1 - iLogr0) + 1; i++)
            logThrs[i + nVals * (iLogr1 - iLogr0) + 1] = -logThrs[i];
         logThrs[2 * (nVals * (iLogr1 - iLogr0) + 1)] = 0;
      } catch (Exception e)
      {
      }
      return logThrs;
   }

   
   public static String[] intervals(float r)
   {
      String[] intervalStrings = new String[9];
      int e = (int)(Math.log10(r) + 1000) - 1000;
      if (e < 0)
         for (int i = e; i < 0; i++)
            r *= 10;
      else if (e > 0)
         for (int i = 0; i < e; i++)
            r /= 10;
      int rounded = 2;
      if (r <2)
         rounded = 2;
      else if (r < 5)
         rounded = 1;
      else
         rounded = 0;
      String[] tmpStrings = new String[12];
      for (int i = 3; i >= 0; i--)
      {
         String pre = "", post = "";
         int exp = e - i;
         if (exp > 2 || exp < -3)
            post = "e"+exp;
         else
            switch (exp)
            {
            case 3:
               post = "000";
               break;
            case 2:
               post = "00";
               break;
            case 1:
               post = "0";
               break;
            case 0:
               break;
            case -1:
               pre = ".";
               break;
            case -2:
               pre = ".0";
               break;
            case -3:
               pre = ".00";
               break;
            }
         tmpStrings[3 * i] = pre+"5"+post;
         tmpStrings[3 * i + 1] = pre+"2"+post;
         tmpStrings[3 * i + 2] = pre+"1"+post;
      }
      System.arraycopy(tmpStrings, rounded, intervalStrings, 0, 9);
      return intervalStrings;
   }

   public static final void main(String[] args)
   {
      String[] intervalStrings;
      intervalStrings = intervals(.234f);
      for (int i = 0; i < intervalStrings.length; i++)
         System.out.println(intervalStrings[i]);
      System.out.println("");
      intervalStrings = intervals(.0553f);
      for (int i = 0; i < intervalStrings.length; i++)
         System.out.println(intervalStrings[i]);
      System.out.println("");
      intervalStrings = intervals(.00553f);
      for (int i = 0; i < intervalStrings.length; i++)
         System.out.println(intervalStrings[i]);
      System.out.println("");
      intervalStrings = intervals(.000553f);
      for (int i = 0; i < intervalStrings.length; i++)
         System.out.println(intervalStrings[i]);
      System.out.println("");
      intervalStrings = intervals(.0000553f);
      for (int i = 0; i < intervalStrings.length; i++)
         System.out.println(intervalStrings[i]);
      System.out.println("");
      intervalStrings = intervals(5.34f);
      for (int i = 0; i < intervalStrings.length; i++)
         System.out.println(intervalStrings[i]);
      System.out.println("");
      intervalStrings = intervals(53.4f);
      for (int i = 0; i < intervalStrings.length; i++)
         System.out.println(intervalStrings[i]);
      System.out.println("");
      intervalStrings = intervals(553f);
      for (int i = 0; i < intervalStrings.length; i++)
         System.out.println(intervalStrings[i]);
      System.out.println("");
      intervalStrings = intervals(5530f);
      for (int i = 0; i < intervalStrings.length; i++)
         System.out.println(intervalStrings[i]);
      System.out.println("");
      intervalStrings = intervals(55300f);
      for (int i = 0; i < intervalStrings.length; i++)
         System.out.println(intervalStrings[i]);
      System.out.println("");
      intervalStrings = intervals(553000f);
      for (int i = 0; i < intervalStrings.length; i++)
         System.out.println(intervalStrings[i]);
      System.out.println("");

   }
}
