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

package pl.edu.icm.visnow.lib.basic.filters.AnisotropicDenoiser;

import pl.edu.icm.visnow.engine.core.Parameter;
import pl.edu.icm.visnow.engine.core.ParameterEgg;
import pl.edu.icm.visnow.engine.core.ParameterType;
import pl.edu.icm.visnow.engine.core.Parameters;

/**
 *
 * @author Krzysztof S. Nowinski
 *         Warsaw University, ICM
 */
public class Params extends Parameters
{

   public enum CoreType
   {
      CPU, GPU
   }
   public static final String METHOD = "method";
   public static final String COMPONENTS = "components";
   public static final String RADIUS = "radius";
   public static final String SLOPE = "slope";
   public static final String SLOPE1 = "slope1";
   public static final String NATIVE = "useNative";
   public static final String CORE = "core";
   public static final String NTHREADS = "nThreads";
   public static final String ITERATIONS ="iterations";
   public static final String PRESMOOTH_RADIUS = "presmoothRadius";
   public static final String PRESMOOTH = "presmooth";
   public static final String COMPUTE_WEIGHTS = "computeWeights";
   public static final String COMPUTE_BY_SLICE = "computeBySlice";
   public static final String COMPUTE = "compute";
   public static final String COMPUTE_SIGMA = "computeSigma";
   public static final String NORMALIZE_SIGMA = "normalizeSigma";
   
   
   
   public static final int AVERAGE = 0;
   public static final int MEDIAN = 1;
   private static ParameterEgg[] eggs = new ParameterEgg[]
   {
      new ParameterEgg<Integer>(METHOD, ParameterType.dependent, AVERAGE),
      new ParameterEgg<int[][]>(COMPONENTS, ParameterType.dependent, new int[][]{{0}}),
      new ParameterEgg<Integer>(RADIUS, ParameterType.dependent, 2),
      new ParameterEgg<Float>(SLOPE, ParameterType.dependent, 2.f),
      new ParameterEgg<Float>(SLOPE1, ParameterType.dependent, 4.f),
      new ParameterEgg<Boolean>(NATIVE, ParameterType.independent, false),
      new ParameterEgg<CoreType>(CORE, ParameterType.independent, CoreType.CPU),
      new ParameterEgg<Integer>(NTHREADS, ParameterType.independent,null ),
      new ParameterEgg<Integer>(ITERATIONS, ParameterType.dependent, 1),
      new ParameterEgg<Integer>(PRESMOOTH_RADIUS, ParameterType.dependent, 5),
      new ParameterEgg<Boolean>(COMPUTE_SIGMA, ParameterType.independent, false),
      new ParameterEgg<Boolean>(NORMALIZE_SIGMA, ParameterType.independent, false),
      new ParameterEgg<Boolean>(PRESMOOTH, ParameterType.independent, false),
      new ParameterEgg<Boolean>(COMPUTE_WEIGHTS, ParameterType.independent, false),
      new ParameterEgg<Boolean>(COMPUTE_BY_SLICE, ParameterType.independent, false),
      new ParameterEgg<Boolean>(COMPUTE, ParameterType.independent, false)
   };

   public Params()
   {
      super(eggs);
      setValue(COMPONENTS, new Integer[][] {{0, 0}});
      setValue(NTHREADS,pl.edu.icm.visnow.system.main.VisNow.availableProcessors());
   }

   /**
    * Get the value of normalizeSigma
    *
    * @return the value of normalizeSigma
    */
   public boolean isNormalizeSigma()
   {
      return (Boolean)getValue(NORMALIZE_SIGMA);
   }

   /**
    * Set the value of normalizeSigma
    *
    * @param normalizeSigma new value of normalizeSigma
    */
   public void setNormalizeSigma(boolean normalizeSigma)
   {
      setValue(NORMALIZE_SIGMA, normalizeSigma);
   }

   /**
    * Get the value of computeSigma
    *
    * @return the value of computeSigma
    */
   public boolean isComputeSigma()
   {
      return (Boolean)getValue(COMPUTE_SIGMA);
   }

   /**
    * Set the value of computeSigma
    *
    * @param computeSigma new value of computeSigma
    */
   public void setComputeSigma(boolean computeSigma)
   {
      setValue(COMPUTE_SIGMA, computeSigma);
   }

   /**
    * Get the value of iterations
    *
    * @return the value of iterations
    */
   public int getIterations()
   {
      return (Integer)getValue(ITERATIONS);
   }

   /**
    * Set the value of iterations
    *
    * @param iterations new value of iterations
    */
   public void setIterations(int iterations)
   {
      setValue(ITERATIONS,iterations);
   }

   public CoreType getCore()
   {
      return (CoreType)getValue(CORE);
   }

   public void setCore(CoreType core)
   {
      setValue(CORE, core);
   }

   public int getMethod()
   {
      return (Integer)getValue(METHOD);
   }

   public void setMethod(int method)
   {
      setValue(METHOD, method);
   }

   public int getComponentsNumber()
   {
      return ((int[][])getValue(COMPONENTS)).length;
   }

   public int getComponent(int i)
   {
      return ((int[][])getValue(COMPONENTS))[i][0];
   }

   public void setComponents(int[][] components)
   {
      setValue(COMPONENTS, components);
   }

   public int getAnisotropyComponent(int i)
   {
      return ((int[][])getValue(COMPONENTS))[i][1];
   }

   public int getRadius()
   {
      return (Integer)getValue(RADIUS);
   }

   public void setRadius(int radius)
   {
      setValue(RADIUS, radius);
   }

   public float getSlope()
   {
      return (Float)getValue(SLOPE);
   }

   public void setSlope(float slope)
   {
      setValue(SLOPE, slope);
   }

   public float getSlope1()
   {
      return (Float)getValue(SLOPE1);
   }

   public void setSlope1(float slope1)
   {
      setValue(SLOPE1, slope1);
   }

   public int getNThreads()
   {
      return (Integer)getValue(NTHREADS);
   }

   public void setNThreads(int nThreads)
   {
      setValue(NTHREADS, nThreads);
   }

   public boolean isUseNative()
   {
      return (Boolean)getValue(NATIVE);
   }

   public void setUseNative(boolean useNative)
   {
      setValue(NATIVE, useNative);
   }

   public int getPresmoothRadius()
   {
      return (Integer)getValue(PRESMOOTH_RADIUS);
   }

   public void setPresmoothRadius(int presmoothRadius)
   {
      setValue(PRESMOOTH_RADIUS, presmoothRadius);
   }

   public boolean isPresmooth()
   {
      return (Boolean)getValue(PRESMOOTH);
   }

   public void setPresmooth(boolean presmooth)
   {
      setValue(PRESMOOTH, presmooth);
      if (presmooth)
         fireStateChanged();
   }

   public boolean isComputeWeights()
   {
      return (Boolean)getValue(COMPUTE_WEIGHTS);
   }

   public void setComputeWeights(boolean computeWeights)
   {
      setValue(COMPUTE_WEIGHTS, computeWeights);
      if (computeWeights)
         fireStateChanged();
   }

   public boolean isCompute()
   {
      return (Boolean)getValue(COMPUTE);
   }

   public void setCompute(boolean compute)
   {
      setValue(COMPUTE, compute);
      if (compute)
         fireStateChanged();
   }
   
   public boolean isComputeBySlice()
   {
      return (Boolean)getValue(COMPUTE_BY_SLICE);
   }

   public void setComputeBySlice(boolean compute)
   {
      setValue(COMPUTE_BY_SLICE, compute);
   }
}
