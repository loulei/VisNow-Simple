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
exception statement from your version.
*/
//</editor-fold>

package pl.edu.icm.visnow.lib.basic.mappers.RibbonPlot;

import pl.edu.icm.visnow.engine.core.ParameterEgg;
import pl.edu.icm.visnow.engine.core.ParameterType;
import pl.edu.icm.visnow.engine.core.Parameters;

/**
 *
 * @author Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class Params extends Parameters
{

   protected static final String COMPONENT = "component";
   protected static final String DIRECTION = "direction";
   protected static final String SCALE = "scale";
   protected static final String RIBBON = "ribbon";
   protected static final String AXES = "axes";
   protected static final String ZERO_BASED = "zeroBased";

   public Params()
   {
      super(eggs);
   }
   private static ParameterEgg[] eggs = new ParameterEgg[]
   {
      new ParameterEgg<Integer>(COMPONENT, ParameterType.dependent, 0),
      new ParameterEgg<Integer>(DIRECTION, ParameterType.dependent, 0),
      new ParameterEgg<Float>(SCALE, ParameterType.dependent, 0f),
      new ParameterEgg<Boolean>(RIBBON, ParameterType.independent, true),
      new ParameterEgg<Boolean>(AXES, ParameterType.independent, false),
      new ParameterEgg<Boolean>(ZERO_BASED, ParameterType.independent, true)
   };

   public int getComponent()
   {
      return (Integer) getValue(COMPONENT);
   }

   public void setComponent(int component)
   {
      setValue(COMPONENT, component);
      fireStateChanged();
   }

   public int getAxis()
   {
      return (Integer) getValue(DIRECTION);
   }

   public void setAxis(int axis)
   {
      setValue(DIRECTION, axis);
      fireStateChanged();
   }

   public float getScale()
   {
      return (Float) getValue(SCALE);
   }

   public void setScale(float scale)
   {
      setValue(SCALE, scale);
      fireStateChanged();
   }

   public boolean isRibbon()
   {
      return (Boolean) getValue(RIBBON);
   }

   public void setRibbon(boolean ribbon)
   {
      setValue(RIBBON, ribbon);
      fireStateChanged();
   }
   
   public boolean showAxes()
   {
      return (Boolean) getValue(AXES);
   }

   public void setShowAxes(boolean axes)
   {
      setValue(AXES, axes);
      fireStateChanged();
   }

   public boolean isZeroBased()
   {
      return (Boolean) getValue(ZERO_BASED);
   }

   public void setZeroBased(boolean zeroBased)
   {
      setValue(ZERO_BASED, zeroBased);
      fireStateChanged();
   }

}
