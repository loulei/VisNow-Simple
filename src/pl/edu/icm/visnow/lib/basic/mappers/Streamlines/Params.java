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

package pl.edu.icm.visnow.lib.basic.mappers.Streamlines;

import pl.edu.icm.visnow.engine.core.ParameterEgg;
import pl.edu.icm.visnow.engine.core.ParameterType;
import pl.edu.icm.visnow.engine.core.Parameters;

/**
 *
 * @author Krzysztof S. Nowinski
 * University of Warsaw, ICM
 */
public class Params extends Parameters
{
   private boolean dowsizeChanged = true;
   private static ParameterEgg[] eggs = new ParameterEgg[]
   {
      new ParameterEgg<Integer>("vectorComponent", ParameterType.dependent, -1),
      new ParameterEgg<int[]>("down", ParameterType.dependent, new int[] { 5, 5, 5}),
      new ParameterEgg<Integer>("downsize", ParameterType.dependent, 100),
      new ParameterEgg<Float>("step", ParameterType.dependent, .002f),
      new ParameterEgg<Integer>("nForwardSteps", ParameterType.dependent, 500),
      new ParameterEgg<Integer>("nBackwardSteps", ParameterType.dependent, 0)
   };

   public Params()
   {
      super(eggs);
   }

   public int getDownsize()
   {
      return (Integer)getValue("downsize");
   }

   public void setDownsize(int downsize)
   {
      if (downsize != this.getValue("downsize"))
         dowsizeChanged = true;
      setValue("downsize",downsize);
   }

   public int[] getDown()
   {
      return (int[])getValue("down");
   }

   public void setDown(int[] down)
   {
      int[] dwn = (int[])getValue("down");
      for (int i = 0; i < dwn.length; i++)
         if (dwn[i] != down[i]) dowsizeChanged = true;
      setValue("down",down);
      fireStateChanged();
   }


   /**
    * Get the value of nBackwardSteps
    *
    * @return the value of nBackwardSteps
    */
   public int getNBackwardSteps()
   {
      return (Integer)getValue("nBackwardSteps");
   }

   /**
    * Set the value of nBackwardSteps
    *
    * @param nBackwardSteps new value of nBackwardSteps
    */
   public void setNBackwardSteps(int nBackwardSteps)
   {
      setValue("nBackwardSteps",nBackwardSteps);
      fireStateChanged();
   }

   /**
    * Get the value of nForwardSteps
    *
    * @return the value of nForwardSteps
    */
   public int getNForwardSteps()
   {
      return (Integer)getValue("nForwardSteps");
   }

   /**
    * Set the value of nForwardSteps
    *
    * @param nForwardSteps new value of nForwardSteps
    */
   public void setNForwardSteps(int nForwardSteps)
   {
      setValue("nForwardSteps",nForwardSteps);
      fireStateChanged();
   }

   /**
    * Get the value of step
    *
    * @return the value of step
    */
   public float getStep()
   {
      return (Float)getValue("step");
   }

   /**
    * Set the value of step
    *
    * @param step new value of step
    */
   public void setStep(float step)
   {
      setValue("step",step);
      fireStateChanged();
   }

   /**
    * Get the value of vectorComponent
    *
    * @return the value of vectorComponent
    */
   public int getVectorComponent()
   {
      return (Integer)getValue("vectorComponent");
   }

   /**
    * Set the value of vectorComponent
    *
    * @param vectorComponent new value of vectorComponent
    */
   public void setVectorComponent(int vectorComponent)
   {
      setValue("vectorComponent",vectorComponent);
      fireStateChanged();
   }

   public boolean isDowsizeChanged()
   {
      return dowsizeChanged;
   }

   public void setDowsizeChanged(boolean dowsizeChanged)
   {
      this.dowsizeChanged = dowsizeChanged;
   }

}
