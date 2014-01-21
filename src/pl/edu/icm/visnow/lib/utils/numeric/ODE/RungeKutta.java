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

package pl.edu.icm.visnow.lib.utils.numeric.ODE;

import java.util.Arrays;

public class RungeKutta
{


   // Fourth order Runge-Kutta for n (nSpace) ordinary differential equations (ODE)
   public static int fourthOrderRK(Deriv g, float[] y0, float absH, int nsteps, float dir, float[] trajectory, float[] vectors)
   {
      int nSpace = y0.length;
      float[] y1 = new float[nSpace];
      float[] y2 = new float[nSpace];
      float[] y3 = new float[nSpace];
      float[] y4 = new float[nSpace];
      float[] y  = new float[nSpace];
      float[] yd = new float[nSpace];
      float[] dy;
      float h = dir*absH;
      int iStep = 0;
      Arrays.fill(vectors, 0);
      Arrays.fill(trajectory, 0);
      System.arraycopy(y0, 0, y, 0, nSpace);
      System.arraycopy(y0, 0, trajectory, 0, nSpace);

      // iteration over allowed steps: iStep is current step number, k is stored step number
      try
      {
         for (iStep = 0; iStep < nsteps; iStep++)
         {
            dy = g.derivn(y);
            if (dy == null)
               break;
            System.arraycopy(dy, 0, vectors, iStep * 3, 3);
            for (int i = 0; i < nSpace; i++)
            {
               y1[i] = h * dy[i];
               yd[i] = y[i] + y1[i] / 2;
            }
            dy = g.derivn(yd);
            if (dy == null)
               break;
            for (int i = 0; i < nSpace; i++)
            {
               y2[i] = h * dy[i];
               yd[i] = y[i] + y2[i] / 2;
            }
            dy = g.derivn(yd);
            if (dy == null)
               break;
            for (int i = 0; i < nSpace; i++)
            {
               y3[i] = h * dy[i];
               yd[i] = y[i] + y3[i];
            }
            dy = g.derivn(yd);
            if (dy == null)
               break;
            for (int i = 0; i < nSpace; i++)
            {
               y4[i] = h * dy[i];
               y[i] += y1[i] / 6 + y2[i] / 3 + y3[i] / 3 + y4[i] / 6;
            }
            System.arraycopy(y, 0, trajectory, iStep * nSpace, nSpace);
         }
         if (iStep < nsteps)
            for (int i = Math.max(iStep, 1); i < nsteps; i ++)
            {
               System.arraycopy(trajectory, nSpace * (i - 1), trajectory, nSpace * i, nSpace);
               System.arraycopy(vectors, 3 * (i - 1), vectors, 3 * i, 3);
            }
      } catch (Exception e)
      {

      }
      return iStep;
   }

   private RungeKutta()
   {
   }
}








