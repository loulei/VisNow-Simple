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

package pl.edu.icm.visnow.lib.basic.filters.InterpolationToRegularField;

import pl.edu.icm.visnow.engine.core.ParameterEgg;
import pl.edu.icm.visnow.engine.core.ParameterType;
import pl.edu.icm.visnow.engine.core.Parameters;
import pl.edu.icm.visnow.geometries.parameters.TransformParams;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class Params extends Parameters
{

   private static final String RESOLUTION = "resolution";
   private static final String ADJUSTING = "adjusting";
   private static final String OUTPUT = "output";
   private static final String TRANSFORM = "transform params";
   private static final String THREADS = "threads";
   private static final String DIM = "dim";
   private static ParameterEgg[] eggs = new ParameterEgg[]
   {
      new ParameterEgg<Integer>(DIM, ParameterType.independent, 3),
      new ParameterEgg<int[]>(RESOLUTION, ParameterType.independent, null),
      new ParameterEgg<Boolean>(ADJUSTING, ParameterType.independent, false),
      new ParameterEgg<Boolean>(OUTPUT, ParameterType.independent, false),
      new ParameterEgg<TransformParams>(TRANSFORM, ParameterType.independent, null),
      new ParameterEgg<Integer>(THREADS, ParameterType.independent, null)
   };

   public Params()
   {
      super(eggs);
      setValue(RESOLUTION,new int[]{100, 100, 100});
      setValue(TRANSFORM,new TransformParams());
      setValue(THREADS,pl.edu.icm.visnow.system.main.VisNow.availableProcessors());
   }

   public int[] getResolution()
   {
      return (int[]) getValue(RESOLUTION);
   }

   public void setResolution(int[] resolution)
   {
      setValue(RESOLUTION, resolution);
   }

   public boolean isAdjusting()
   {
      return (Boolean) getValue(ADJUSTING);
   }

   public void setAdjusting(boolean adjusting)
   {
      setValue(ADJUSTING, adjusting);
   }

   public boolean isOutput()
   {
      return (Boolean) getValue(OUTPUT);
   }

   public void setOutput(boolean output)
   {
      setValue(OUTPUT, output);
      fireStateChanged();
   }

   public TransformParams getTransform()
   {
      return (TransformParams) getValue(TRANSFORM);
   }

   public void setTransform(TransformParams transform)
   {
      setValue(TRANSFORM, transform);
      fireParameterChanged(TRANSFORM);
   }

   /**
    * Get the value of threads
    *
    * @return the value of threads
    */
   public int getThreads()
   {
      return (Integer) getValue(THREADS);
   }

   /**
    * Set the value of threads
    *
    * @param threads new value of threads
    */
   public void setThreads(int threads)
   {
      setValue(THREADS, threads);
   }

   public int getDim()
   {
      return (Integer) getValue(DIM);
   }

   public void setDim(int dim)
   {
      setValue(DIM, dim);
   }
}
