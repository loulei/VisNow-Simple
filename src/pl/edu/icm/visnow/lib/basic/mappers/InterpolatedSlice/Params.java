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
package pl.edu.icm.visnow.lib.basic.mappers.InterpolatedSlice;

import pl.edu.icm.visnow.engine.core.ParameterEgg;
import pl.edu.icm.visnow.engine.core.ParameterType;
import pl.edu.icm.visnow.engine.core.Parameters;
import pl.edu.icm.visnow.geometries.parameters.TransformParams;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.pick.Pick3DListener;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class Params extends Parameters {

    private static final String AXIS = "axis";
    private static final String RESOLUTION = "resolution";
    private static final String ADJUSTING = "adjusting";
    private static final String TRANSFORM = "transform params";
    private static final String THREADS = "threads";
    /**
     * To be set by FieldSlicePlane using setPick3DListener(). This Pick3DListener was moved from
     * FieldSlicePlane to Params to enable synchronizing a push button with active / non-active
     * state of the 3D pick listener.
     */
    protected Pick3DListener pick3DListener = null;
//
    private static ParameterEgg[] eggs = new ParameterEgg[]{
        new ParameterEgg<Integer>(AXIS, ParameterType.independent, 2),
        new ParameterEgg<int[]>(RESOLUTION, ParameterType.independent, null),
        new ParameterEgg<Boolean>(ADJUSTING, ParameterType.independent, false),
        new ParameterEgg<TransformParams>(TRANSFORM, ParameterType.independent, null),
        new ParameterEgg<Integer>(THREADS, ParameterType.independent, pl.edu.icm.visnow.system.main.VisNow.availableProcessors())
    };

   public Params()
   {
      super(eggs);
      setValue(TRANSFORM, new TransformParams());
      setValue(RESOLUTION, new int[]{100, 100});
   }

    public int getAxis() {
        return (Integer) getValue(AXIS);
    }

    public void setAxis(int axis) {
        setValue(AXIS, axis);
        fireStateChanged();
    }

    public int[] getResolution() {
        return (int[]) getValue(RESOLUTION);
    }

    public void setResolution(int[] resolution) {
        setValue(RESOLUTION, resolution);
        fireStateChanged();
    }

    public boolean isAdjusting() {
        return (Boolean) getValue(ADJUSTING);
    }

    public void setAdjusting(boolean adjusting) {
        setValue(ADJUSTING, adjusting);
    }

    public TransformParams getTransform() {
        return (TransformParams) getValue(TRANSFORM);
    }

    public void setTransform(TransformParams transform) {
        setValue(TRANSFORM, transform);
        fireStateChanged();
    }

    /**
     * Get the value of threads
     *
     * @return the value of threads
     */
    public int getThreads() {
        return (Integer) getValue(THREADS);
    }

    /**
     * Set the value of threads
     *
     * @param threads new value of threads
     */
    public void setThreads(int threads) {
        setValue(THREADS, threads);
    }

    public void setPick3DListener(Pick3DListener pick3DListener) {
        this.pick3DListener = pick3DListener;
    }

    @Override
    public Pick3DListener getPick3DListener() {
        return pick3DListener;
    }
}
