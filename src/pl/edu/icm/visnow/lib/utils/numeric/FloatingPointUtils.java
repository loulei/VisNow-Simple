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
package pl.edu.icm.visnow.lib.utils.numeric;

import pl.edu.icm.visnow.system.main.VisNow;

/**
 *
 *
 * @author Piotr Wendykier (piotrw@icm.edu.pl)
 */
public class FloatingPointUtils
{
   /**
    * Tests whether data array contains NaN, Float.NEGATIVE_INFINITY or
    * Float.POSITIVE_INFINITY elements.
    *
    * @param data
    * @return false if any element of data array is NaN, Float.NEGATIVE_INFINITY
    * @throws IllegalArgumentException if any element of data array is NaN, Float.NEGATIVE_INFINIT
    *  and throwing exception is configured 
    * or Float.POSITIVE_INFINITY, true otherwise.
    */
   
   private static final float maxNum = Float.MAX_VALUE / 10;
   private static final float minNum = -maxNum;
   
   public static boolean isFinite(float[] data) throws IllegalArgumentException
   {
      if (data == null)
         throw new IllegalArgumentException("data cannot be null.");
      int n = data.length;
      boolean containsNaN = false;
      boolean containsInf = false;
      float maxVal = -Float.MAX_VALUE;
      float minVal = Float.MAX_VALUE;
      int nanAction = VisNow.getActionOnNaN();
      int infAction = VisNow.getActionOnInf();
      for (int i = 0; i < n; i++)
      {
         float e = data[i];
         if (Float.isNaN(e))
         {
            switch (nanAction)
            {
               case VisNow.NAN_AS_0:
                  data[i] = 0;
                  break;
               case VisNow.NAN_AS_MAX_NUMBER_VAL:
                  data[i] = maxNum;
                  break;
               case VisNow.NAN_AS_MIN_NUMBER_VAL:
                  data[i] = minNum;
                  break;
               case VisNow.EXCEPTION_AT_NAN:
                  throw new IllegalArgumentException("data cannot contain NaN");
               default:
                  break;
            }
            containsNaN = true;
         }
         else if (e == Float.POSITIVE_INFINITY || e == Float.NEGATIVE_INFINITY)
         {
            switch (infAction)
            {
               case VisNow.INF_AS_0:
                  data[i] = 0;
                  break;
               case VisNow.INF_AS_EXTREME_NUMBER_VAL:
                  if (e == Float.POSITIVE_INFINITY)
                     data[i] = maxNum;
                  else
                     data[i] = minNum;
                  break;
               case VisNow.EXCEPTION_AT_INF:
                  throw new IllegalArgumentException("data cannot contain Infinity");
               default:
                  break;
            }
            containsInf = true;
         }
         else
         {
            if (e < minVal)
               minVal = e;
            if (e > maxVal)
               maxVal = e;
         }
      }
      if (!containsNaN && !containsInf)
         return true;
      
      if (containsInf && infAction == VisNow.INF_AS_EXTREME_DATA_VAL)
         for (int i = 0; i < n; i++)
         {
            float e = data[i];
            if (e == Float.POSITIVE_INFINITY)
               data[i] = maxVal;
            if (e == Float.NEGATIVE_INFINITY)
               data[i] = minVal;
         }
      if (containsNaN && nanAction == VisNow.NAN_AS_MAX_DATA_VAL)
         for (int i = 0; i < n; i++)
         {
            float e = data[i];
            if (Float.isNaN(e))
               data[i] = maxVal;
         }
      if (containsNaN && nanAction == VisNow.NAN_AS_MIN_DATA_VAL)
         for (int i = 0; i < n; i++)
         {
            float e = data[i];
            if (Float.isNaN(e))
               data[i] = minVal;
         }
      return false;
   }

   /**
    * Tests whether data array contains NaN, Double.NEGATIVE_INFINITY or
    * Double.POSITIVE_INFINITY elements.
    *
    * @param data
    * @return false if any element of data array is NaN, Double.NEGATIVE_INFINITY
    * or Double.POSITIVE_INFINITY, true otherwise.
    * @throws IllegalArgumentException if any element of data array is NaN, Float.NEGATIVE_INFINIT
    *  and throwing exception is configured 
    */
   public static boolean isFinite(double[] data) throws IllegalArgumentException
   {
      if (data == null)
         throw new IllegalArgumentException("data cannot be null.");
      int n = data.length;
      int nanAction = VisNow.getActionOnNaN();
      int infAction = VisNow.getActionOnInf();
      boolean containsNaN = false;
      boolean containsInf = false;
      double minVal = Double.MAX_VALUE;
      double maxVal = -Double.MAX_VALUE;
      for (int i = 0; i < n; i++)
      {
         double e = data[i];
         if (Double.isNaN(e))
         {
            switch (nanAction)
            {
               case VisNow.NAN_AS_0:
                  data[i] = 0;
                  break;
               case VisNow.NAN_AS_MAX_NUMBER_VAL:
                  data[i] = maxNum;
                  break;
               case VisNow.NAN_AS_MIN_NUMBER_VAL:
                  data[i] = minNum;
                  break;
               case VisNow.EXCEPTION_AT_NAN:
                  throw new IllegalArgumentException("data cannot contain NaN");
               default:
                  break;
            }
            containsNaN = true;
         }
         else if (e == Double.POSITIVE_INFINITY || e == Double.NEGATIVE_INFINITY)
         {
            switch (infAction)
            {
               case VisNow.INF_AS_0:
                  data[i] = 0;
                  break;
               case VisNow.INF_AS_EXTREME_NUMBER_VAL:
                  if (e == Double.POSITIVE_INFINITY)
                     e = maxNum;
                  else
                     e = minNum;
                  break;
               case VisNow.EXCEPTION_AT_NAN:
                  throw new IllegalArgumentException("data cannot contain Infinity");
               default:
                  break;
            }
            containsInf = true;
         }
         else
         {
            if (e < minVal)
               minVal = e;
            if (e > maxVal)
               maxVal = e;
         }
      }
      if (!containsNaN && !containsInf)
         return true;

      if (containsInf && infAction == VisNow.INF_AS_EXTREME_DATA_VAL)
         for (int i = 0; i < n; i++)
         {
            double e = data[i];
            if (e == Double.POSITIVE_INFINITY)
               e = maxVal;
            if (e == Double.NEGATIVE_INFINITY)
               e = minVal;
         }
      if (containsNaN && nanAction == VisNow.NAN_AS_MAX_DATA_VAL)
         for (int i = 0; i < n; i++)
         {
            double e = data[i];
            if (Double.isNaN(e))
               e = maxVal;
         }
      if (containsNaN && nanAction == VisNow.NAN_AS_MIN_DATA_VAL)
         for (int i = 0; i < n; i++)
         {
            double e = data[i];
            if (Double.isNaN(e))
               e = minVal;
         }
      return false;
   }
}
