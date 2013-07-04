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

package pl.edu.icm.visnow.lib.types;

import pl.edu.icm.visnow.datasets.IrregularField;

/**
 * @author Krzysztof S. Nowinski (know@icm.edu.pl) Warsaw University, Interdisciplinary Centre for
 * Mathematical and Computational Modelling
 */
public class VNIrregularField extends VNField
{

   private boolean computed = false;
   private float[][] valueHistograms = null;
   private float[] minVal = null;
   private float[] avgVal = null;
   private float[] maxVal = null;
   private float[] stdDevVal = null;

   public VNIrregularField()
   {
   }

   public VNIrregularField(IrregularField inField)
   {
      field = inField;
      field.checkPureDim();
   }

   @Override
   public IrregularField getField()
   {
      return (IrregularField) field;
   }

   public void createStats()
   {
      if (field == null || field.getNData() < 1 || computed)
      {
         return;
      }
      avgVal = new float[field.getNData()];
      minVal = new float[field.getNData()];
      maxVal = new float[field.getNData()];
      stdDevVal = new float[field.getNData()];
      valueHistograms = new float[field.getNData()][256];
      
      for (int n = 0; n < field.getNData(); n++)
      {
         float max = maxVal[n] = field.getData(n).getMaxv();
         float min = minVal[n] = field.getData(n).getMinv();
         float a = 0, a2 = 0;
         for (int i = 0; i < 256; i++)
         {
            valueHistograms[n][i] = 0;
         }
         if (min >= max - .001f)
         {
            float med = .5f * (min + max);
            min = med - .0005f;
            max = med + .0005f;
         }

         float d = 255 / (max - min);
         int vlen = field.getData(n).getVeclen();
         float[] values = field.getData(n).getFData();
         for (int i = 0; i < field.getNNodes(); i++)
         {
            float f = 0;
            if (vlen == 1)
               f = values[i];
            else
            {
               f = 0;
               for (int j = 0; j < vlen; j++)
                  f += values[vlen * i + j] * values[vlen * i + j];
               f = (float)(Math.sqrt(f));
            }
            int j = Math.min(255, Math.max(0, (int) (d * (f - min))));
            valueHistograms[n][j] += 1;
            a += f;
            a2 += f * f;
         }

         avgVal[n] = a / values.length;
         stdDevVal[n] = (float) (Math.sqrt(a2 / values.length - avgVal[n] * avgVal[n]));
      }
      computed = true;
   }

   @Override
   public float[] getAvgVal()
   {
      return avgVal;
   }

   @Override
   public float[] getStdDevVal()
   {
      return stdDevVal;
   }

   @Override
   public float[][] getValueHistograms()
   {
      return valueHistograms;
   }

   @Override
   public float[] getMaxVal()
   {
      return maxVal;
   }

   @Override
   public float[] getMinVal()
   {
      return minVal;
   }
}
