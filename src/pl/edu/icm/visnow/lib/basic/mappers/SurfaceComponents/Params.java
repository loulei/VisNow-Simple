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

package pl.edu.icm.visnow.lib.basic.mappers.SurfaceComponents;

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
   private boolean minComponentSizeChanged = false;
   private static ParameterEgg[] eggs = new ParameterEgg[]
   {
      new ParameterEgg<Integer>("minComponentSize", ParameterType.dependent, 20),
      new ParameterEgg<Integer>("separateComponents", ParameterType.dependent, 20),
      new ParameterEgg<Integer>("modifiedComponent", ParameterType.dependent, -1),
      new ParameterEgg<String>("modifiedName", ParameterType.dependent, ""),
      new ParameterEgg<Boolean>("modifiedSelection", ParameterType.dependent, false),
      new ParameterEgg<Boolean>("colorByComponents", ParameterType.dependent, true),
      new ParameterEgg<Boolean>("output", ParameterType.dependent, false),
   };

   public Params()
   {
      super(eggs);
   }

   public int getMinComponentSize()
   {
      return (Integer)getValue("minComponentSize");
   }

   public void setMinComponentSize(int minComponentSize)
   {
      setValue("minComponentSize", minComponentSize);
      minComponentSizeChanged = true;
      fireStateChanged();
   }

   public int getSeparateComponents()
   {
      return (Integer)getValue("separateComponents");
   }

   public void setSeparateComponents(int separateComponents)
   {
      setValue("separateComponents", separateComponents);
      fireStateChanged();
   }

   public int getModifiedComponent()
   {
       int mC = (Integer)getValue("modifiedComponent");
       setValue("modifiedComponent", -1);
       return mC;
   }

   public void setModifiedComponent(int modifiedComponent)
   {
      setValue("modifiedComponent", modifiedComponent);
      fireStateChanged();
   }

   public String getModifiedName()
   {
      return (String)getValue("modifiedName");
   }

   public void setModifiedName(String name)
   {
      setValue("modifiedName", name);
   }

   public boolean getModifiedSelection()
   {
      return (Boolean)getValue("modifiedSelection");
   }

   public void setModifiedSelection(boolean modifiedSelection)
   {
      setValue("modifiedSelection",modifiedSelection);
   }

   public boolean colorByComponents()
   {
      return (Boolean)getValue("colorByComponents");
   }

   public void setColorByComponents(boolean colorByComponents)
   {
      setValue("colorByComponents",colorByComponents);
   }

   public boolean isMinComponentSizeChanged()
   {
      boolean t = minComponentSizeChanged;
      minComponentSizeChanged = false;
      return t;
   }

   public void setOutput(boolean output)
   {
      setValue("output",output);
      if (output)
         fireStateChanged();
   }

   public boolean output()
   {
      return (Boolean)getValue("output");
   }
}
