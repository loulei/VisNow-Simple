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

package pl.edu.icm.visnow.lib.basic.filters.CropDown;

import pl.edu.icm.visnow.engine.core.ParameterEgg;
import pl.edu.icm.visnow.engine.core.ParameterType;
import pl.edu.icm.visnow.engine.core.Parameters;

/**
 *
 * @author  Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class Params extends Parameters
{

   /**
    * low  - input field will be cropped from below according to these indices before isosurfacing
    * up   - input field will be cropped from above according to these indices before isosurfacing
    * downsize - input field will be downsized according to these indices before isosurfacing
    */

   
   private static ParameterEgg[] eggs = new ParameterEgg[]
   {
      new ParameterEgg<int[]>("downsize", ParameterType.dependent, new int[]{2, 2, 2}),
      new ParameterEgg<int[]>("up", ParameterType.dependent, new int[]{1, 1, 1}),
      new ParameterEgg<int[]>("low", ParameterType.dependent, new int[]{0, 0, 0})
   };

   public Params()
   {
      super(eggs);
   }

   /**
    * Get the value of low
    *
    * @return the value of low
    */
   public int[] getLow()
   {
      return (int[])getValue("low");
   }

   /**
    * Set the value of low
    *
    * @param low new value of low
    */
   public void setLow(int[] low)
   {
      setValue("low",low);
   }

   /**
    * Get the value of low at specified index
    *
    * @param index
    * @return the value of low at specified index
    */
   public int getLow(int index)
   {
      return ((int[])getValue("low"))[index];
   }

   /**
    * Set the value of low at specified index.
    *
    * @param index
    * @param newLow new value of low at specified index
    */
   public void setLow(int index, int newLow)
   {
      ((int[])getValue("low"))[index] = newLow;
   }

   /**
    * Get the value of up
    *
    * @return the value of up
    */
   public int[] getUp()
   {
      return (int[])getValue("up");
   }

   /**
    * Set the value of up
    *
    * @param up new value of up
    */
   public void setUp(int[] up)
   {
      setValue("up", up);
   }

   /**
    * Get the value of up at specified index
    *
    * @param index
    * @return the value of up at specified index
    */
   public int getUp(int index)
   {
      return ((int[])getValue("up"))[index];
   }

   /**
    * Set the value of up at specified index.
    *
    * @param index
    * @param newUp new value of up at specified index
    */
   public void setUp(int index, int newUp)
   {
      ((int[])getValue("up"))[index] = newUp;
   }

   /**
    * Get the value of downsize
    *
    * @return the value of downsize
    */
   public int[] getDownsize()
   {
      return (int[])getValue("downsize");
   }

   /**
    * Set the value of downsize
    *
    * @param downsize new value of downsize
    */
   public void setDownsize(int[] downsize)
   {
      setValue("downsize", downsize);
   }

   /**
    * Get the value of downsize at specified index
    *
    * @param index
    * @return the value of downsize at specified index
    */
   public int getDownsize(int index)
   {
      return ((int[])getValue("downsize"))[index];
   }

   /**
    * Set the value of downsize at specified index.
    *
    * @param index
    * @param newDownsize new value of downsize at specified index
    */
   public void setDownsize(int index, int newDownsize)
   {
      ((int[])getValue("downsize"))[index] = newDownsize;
   }

}
