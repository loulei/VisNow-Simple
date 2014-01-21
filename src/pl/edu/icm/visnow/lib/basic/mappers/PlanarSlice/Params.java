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

package pl.edu.icm.visnow.lib.basic.mappers.PlanarSlice;

import pl.edu.icm.visnow.engine.core.ParameterEgg;
import pl.edu.icm.visnow.engine.core.ParameterType;
import pl.edu.icm.visnow.engine.core.Parameters;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class Params extends Parameters
{
   private static final String AXIS       = "axis";
   private static final String TRANSFORM  = "transform params";
   private static final String THREADS    = "threads";
   private static final String RIGHT_SIDE = "right side";
   private static final String COEFFS     = "coeffs";
   private static final String TYPE       = "type";
   private boolean adjusting = false;
   
   private static ParameterEgg[] eggs = new ParameterEgg[]
   {
      new ParameterEgg<Integer>(AXIS, ParameterType.independent, 2),
      new ParameterEgg<Integer>(TYPE, ParameterType.independent, 0),
      new ParameterEgg<float[]>(COEFFS, ParameterType.independent, null),
      new ParameterEgg<Float>(RIGHT_SIDE, ParameterType.independent, .0f),
      new ParameterEgg<Integer>(THREADS, ParameterType.independent, pl.edu.icm.visnow.system.main.VisNow.availableProcessors())
   };


   public Params()
   {
      super(eggs);
      setValue(COEFFS, new float[]{0,0,1});
   }

   public int getAxis()
   {
      return (Integer)getValue(AXIS);
   }

   public void setAxis(int axis)
   {
      setValue(AXIS,axis);
      fireStateChanged();
   }

   public int getType()
   {
      return (Integer)getValue(TYPE);
   }

   public void setType(int type)
   {
      setValue(TYPE,type);
      fireStateChanged();
   }

   public boolean isAdjusting()
   {
      return adjusting;
   }

   public void setAdjusting(boolean adjusting)
   {
      this.adjusting = adjusting;
   }
   
    public float[] getCoeffs()
    {
       return (float[])getValue(COEFFS);
    }
    
    public void setCoeffs(float[] coeffs)
    {
       setValue(COEFFS, coeffs);
       fireStateChanged();
    }
   
    public int getThreads() {
        return (Integer)getValue(THREADS);
    }

    public void setThreads(int threads) {
        setValue(THREADS, threads);
    }

    public float getRightSide()
    {
       return (Float)getValue(RIGHT_SIDE);
    }
    
    public void setRightSide(float rightSide)
    {
       setValue(RIGHT_SIDE, rightSide);
       fireStateChanged();
    }
}
