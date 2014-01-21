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

package pl.edu.icm.visnow.lib.basic.mappers.TubeGlyphs;

import pl.edu.icm.visnow.engine.core.ParameterEgg;
import pl.edu.icm.visnow.engine.core.ParameterType;
import pl.edu.icm.visnow.engine.core.Parameters;

/**
 *
 * @author  Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class Params extends Parameters
{
   public static final int COUNT_CHANGED  = 4;
   public static final int GLYPHS_CHANGED = 3;
   public static final int COORDS_CHANGED = 2;
   public static final int COLORS_CHANGED = 1;
   protected int change = 0;
   protected static ParameterEgg[] eggs = new ParameterEgg[]
   {
      new ParameterEgg<Integer>("component",      ParameterType.dependent, 0),
      new ParameterEgg<Boolean>("constant diam",  ParameterType.dependent, false),
      new ParameterEgg<Boolean>("bgr",            ParameterType.dependent, false),
      new ParameterEgg<Float>("scale",            ParameterType.dependent, .1f),
      new ParameterEgg<Integer>("lod",            ParameterType.independent, 1)
   };

   public Params()
   {
      super(eggs);
   }

   public int getComponent()
   {
      return (Integer)getValue("component");
   }

   public void setComponent(int component)
   {
      setValue("component",component);
      fireStateChanged();
   }

   public int getLod()
   {
      return (Integer)getValue("lod");
   }

   public void setLod(int lod)
   {
      setValue("lod",lod);
      change = Math.max(change, GLYPHS_CHANGED);
      fireStateChanged();
   }

   public boolean isConstantDiam()
   {
      return (Boolean)getValue("constant diam");
   }

   public void setConstantDiam(boolean cDiam)
   {
      setValue("constant diam",cDiam);
      change = Math.max(change, COORDS_CHANGED);
      fireStateChanged();
   }

   public boolean isBgr()
   {
      return (Boolean)getValue("bgr");
   }

   public void setBgr(boolean bgr)
   {
      setValue("bgr",bgr);
      change = Math.max(change, COORDS_CHANGED);
      fireStateChanged();
   }

    public float getScale()
   {
      return (Float)getValue("scale");
   }

   public void setScale(float scale)
   {
      setValue("scale",scale);
      change = Math.max(change, COORDS_CHANGED);
      fireStateChanged();
   }

   public int getChange()
   {
      return change;
   }

   public void setChange(int change)
   {
      this.change = change;
   }


}
