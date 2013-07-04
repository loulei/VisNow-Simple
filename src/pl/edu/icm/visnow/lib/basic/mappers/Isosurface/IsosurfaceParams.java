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

package pl.edu.icm.visnow.lib.basic.mappers.Isosurface;

import pl.edu.icm.visnow.engine.core.ParameterEgg;
import pl.edu.icm.visnow.engine.core.ParameterType;
import pl.edu.icm.visnow.engine.core.Parameters;

/**
 *
 * 
 * @author  Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class IsosurfaceParams extends Parameters {
   protected static final String DOWNSIZE     = "downsize";
   protected static final String ISOCOMPONENT = "isoComponent";
   protected static final String LOW          = "low";
   protected static final String SMOOTHING    = "smoothing";
   protected static final String UNCERTAINTY  = "uncertainty";
   protected static final String SMOOTHSTEPS  = "smoothSteps";
   protected static final String THRESHOLDS   = "thresholds";
   protected static final String TIME         = "time";
   protected static final String UP           = "up";
   protected static final String SEPARATE     = "separate";

    /**
     * isoComponent - data component for isosurfacing
     * threshold - isosurface threshold
     * low  - input field will be cropped from below according to these indices before isosurfacing
     * up   - input field will be cropped from above according to these indices before isosurfacing
     * downsize - input field will be downsized according to these indices before isosurfacing
     */

    private boolean recompute = true;


    public IsosurfaceParams() 
    {
        super(eggs);
    }
    
    private static ParameterEgg[] eggs = new ParameterEgg[]
    {
        new ParameterEgg<Integer>(ISOCOMPONENT, ParameterType.dependent, 0),
        new ParameterEgg<int[]>(DOWNSIZE, ParameterType.dependent, new int[]{2, 2, 2}),
        new ParameterEgg<float[]>(THRESHOLDS, ParameterType.dependent, new float[]{127}),
        new ParameterEgg<int[]>(LOW, ParameterType.dependent, new int[]{0, 0, 0}),
        new ParameterEgg<int[]>(UP, ParameterType.dependent, new int[]{0, 0, 0}),
        new ParameterEgg<Integer>(SMOOTHSTEPS, ParameterType.independent, 0),
        new ParameterEgg<Boolean>(SMOOTHING, ParameterType.independent, false),
        new ParameterEgg<Boolean>(UNCERTAINTY, ParameterType.independent, false),
        new ParameterEgg<Boolean>(SEPARATE, ParameterType.independent, false),
        new ParameterEgg<Float>(TIME, ParameterType.independent, 0.f)
    };

   public int[] getDownsize()
   {
      return (int[]) getValue(DOWNSIZE);
   }

   public void setDownsize(int[] downsize)
   {
      this.setValue(DOWNSIZE, downsize);
      recompute = true;
      fireStateChanged();
   }

   public int getIsoComponent()
   {
      return (Integer) getValue(ISOCOMPONENT);
   }

   public void setIsoComponent(int isoComponent)
   {
      this.setValue(ISOCOMPONENT, isoComponent);
      fireStateChanged();
   }

   public float[] getThresholds()
   {
      return (float[]) getValue(THRESHOLDS);
   }

   public void setThresholds(float[] thresholds)
   {
      this.setValue(THRESHOLDS, thresholds);
      recompute = true;
      fireStateChanged();
   }

   public int[] getLow()
   {
      return (int[]) getValue(LOW);
   }

   public void setLow(int[] low)
   {
      setValue(LOW, low);
      recompute = true;
      fireStateChanged();
   }

   public int[] getUp()
   {
      return (int[]) getValue(UP);
   }

   public void setUp(int[] up)
   {
      setValue(UP, up);
      recompute = true;
      fireStateChanged();
   }

   /**
    * Get the value of recompute
    *
    * @return the value of recompute
    */
   public boolean isRecompute()
   {
      return recompute;
   }

   /**
    * Set the value of recompute
    *
    * @param recompute new value of recompute
    */
   public void setRecompute(boolean recompute)
   {
      this.recompute = recompute;
   }

   public int getSmoothSteps()
   {
      return (Integer) getValue(SMOOTHSTEPS);
   }

   public void setSmoothSteps(int smoothSteps)
   {
      setValue(SMOOTHSTEPS, smoothSteps);
      if (isSmoothing())
         fireStateChanged();
   }

   public boolean isSmoothing()
   {
      return (Boolean) getValue(SMOOTHING);
   }

   public void setSmoothing(boolean smoothing)
   {
      setValue(SMOOTHING, smoothing);
      fireStateChanged();
   }

   public boolean isUncertainty()
   {
      return (Boolean) getValue(UNCERTAINTY);
   }

   public void setUncertainty(boolean uncertainty)
   {
      setValue(UNCERTAINTY, uncertainty);
   }
   
   public boolean isSeparate()
   {
      return (Boolean) getValue(SEPARATE);
   }

   public void setSeparate(boolean separate)
   {
      setValue(SEPARATE, separate);
   }
   
   public float getTime()
   {
      return (Float)getValue(TIME);
   }

   public void setTime(float time)
   {
      setValue(TIME, time);
      fireStateChanged();
   }
}
