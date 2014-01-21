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

package pl.edu.icm.visnow.lib.basic.mappers.RegularFieldSlice;

import pl.edu.icm.visnow.engine.core.ParameterEgg;
import pl.edu.icm.visnow.engine.core.ParameterType;
import pl.edu.icm.visnow.engine.core.Parameters;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class RegularFieldSliceParams extends Parameters {


   private static final String AXIS = "axis";
   private static final String SLICE = "slice";
   private static final String ADJUSTING = "adjusting";
   private static final String RECALCULATE = "recalculate";
   private static final String SHOW2D = "show 2d";
   private static final String XEXT = "x extents";
   private static final String YEXT = "y extents";
   

   private static ParameterEgg[] eggs = new ParameterEgg[]
   {
      new ParameterEgg<Integer>(AXIS, ParameterType.dependent, 2),
      new ParameterEgg<Integer>(SLICE, ParameterType.dependent, 0),
      new ParameterEgg<Boolean>(ADJUSTING, ParameterType.dependent, false),
      new ParameterEgg<Boolean>(RECALCULATE, ParameterType.dependent, false),
      new ParameterEgg<Boolean>(SHOW2D, ParameterType.dependent, false),
      new ParameterEgg<float[]>(XEXT, ParameterType.independent, null),
      new ParameterEgg<float[]>(YEXT, ParameterType.independent, null)
   };

   public RegularFieldSliceParams()
   {
      super(eggs);
      setValue(XEXT, new float[]{.8f, .95f});
      setValue(YEXT, new float[]{.05f, .20f});
   }

   public int getAxis()
   {
      return (Integer)getValue(AXIS);
   }

   public void setAxis(int axis)
   {
      setValue(AXIS, axis);
      fireStateChanged();
   }

   public int getSlice()
   {
      return (Integer)getValue(SLICE);
   }

   public void setSlice(int slice)
   {
      setValue(SLICE, slice);
      fireStateChanged();
   }
   
   public boolean isAdjusting()
   {
      return(Boolean)getValue(ADJUSTING);
   }
   
   public void setAdjusting(boolean adjusting)
   {
      setValue(ADJUSTING, adjusting);
   }
   
   public boolean isRecalculate()
   {
      return(Boolean)getValue(RECALCULATE);
   }
   
   public void setRecalculate(boolean recalculate)
   {
      setValue(RECALCULATE, recalculate);
      fireStateChanged();
   }
   
   public boolean isShow2D()
   {
      return(Boolean)getValue(SHOW2D);
   }
   
   public void setShow2D(boolean show2d)
   {
      setValue(SHOW2D, show2d);
   }

   public float[] getXExt()
   {
      return (float[])getValue(XEXT);
   }

   public void setXExt(float[] xext)
   {
      setValue(XEXT, xext);
      fireStateChanged();
   }
   public float[] getYExt()
   {
      return (float[])getValue(YEXT);
   }

   public void setYExt(float[] yext)
   {
      setValue(YEXT, yext);
      fireStateChanged();
   }

}
