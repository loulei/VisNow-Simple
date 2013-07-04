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

package pl.edu.icm.visnow.lib.basic.mappers.Glyphs;

import javax.swing.event.ChangeEvent;
import pl.edu.icm.visnow.engine.core.ParameterEgg;
import pl.edu.icm.visnow.engine.core.ParameterType;
import pl.edu.icm.visnow.engine.core.Parameters;

/**
 *
 * @author  Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class GlyphsParams extends Parameters
{
   public static final int GEOMETRY_CHANGED  = 3;
   public static final int GLYPHS_CHANGED = 2;
   public static final int COORDS_CHANGED = 1;
   protected int change = 0;
   protected static ParameterEgg[] eggs = new ParameterEgg[]
   {
      new ParameterEgg<Integer>("component",          ParameterType.dependent, 0),
      new ParameterEgg<Integer>("thrComponent",       ParameterType.dependent, -1),
      new ParameterEgg<int[]>("down",                 ParameterType.dependent, new int[] { 5, 5, 5}),
      new ParameterEgg<Integer>("downsize",           ParameterType.dependent, 10),
      new ParameterEgg<int[]>("lowCrop",              ParameterType.dependent, new int[] {0, 0, 0}),
      new ParameterEgg<int[]>("upCrop",               ParameterType.dependent, new int[] {1, 1, 1}),
      new ParameterEgg<Integer>("type",               ParameterType.independent, 0),
      new ParameterEgg<Boolean>("constant diam",      ParameterType.dependent, false),
      new ParameterEgg<Boolean>("constant thickness", ParameterType.dependent, false),
      new ParameterEgg<Float>("scale",                ParameterType.dependent, .1f),
      new ParameterEgg<Float>("thickness",            ParameterType.dependent, .1f),
      new ParameterEgg<Float>("line thickness",       ParameterType.dependent, .1f),
      new ParameterEgg<Float>("smax",                 ParameterType.dependent, .1f),
      new ParameterEgg<Float>("thr",                  ParameterType.dependent, .1f),
      new ParameterEgg<Integer>("lod",                ParameterType.independent, 1),
      new ParameterEgg<Boolean>("thrRelative",        ParameterType.independent, false),
      new ParameterEgg<Boolean>("useAbs",             ParameterType.independent, true),
      new ParameterEgg<Boolean>("useSqrt",            ParameterType.independent, false),
   };

   public GlyphsParams()
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
      if (isThrRelative() && getThrComponent() >= 0)
         change = GEOMETRY_CHANGED;
      else
         change = Math.max(change, GLYPHS_CHANGED);
      fireStateChanged();
   }

   public int getThrComponent()
   {
      return (Integer)getValue("thrComponent");
   }

   public void setThrComponent(int component)
   {
      setValue("thrComponent",component);
      change = GEOMETRY_CHANGED;
      fireStateChanged();
   }

   public int getDownsize()
   {
      return (Integer)getValue("downsize");
   }

   public void setDownsize(int downsize)
   {
      setValue("downsize",downsize);
      change = GEOMETRY_CHANGED;
      fireStateChanged();
   }

   public int[] getDown()
   {
      return (int[])getValue("down");
   }

   public void setDown(int[] down)
   {
      setValue("down",down);
      change = GEOMETRY_CHANGED;
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

   public int[] getLowCrop()
   {
      return (int[])getValue("lowCrop");
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

   public boolean isConstantThickness()
   {
      return (Boolean)getValue("constant thickness");
   }

   public void setConstantThickness(boolean constantThickness)
   {
      setValue("constant thickness",constantThickness);
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

    public float getThickness()
   {
      return (Float)getValue("thickness");
   }

   public void setThickness(float thickness)
   {
      setValue("thickness",thickness);
      change = Math.max(change, COORDS_CHANGED);
      fireStateChanged();
   }

    public float getLineThickness()
   {
      return (Float)getValue("line thickness");
   }

   public void setLineThickness(float thickness)
   {
      setValue("line thickness",thickness);
      change = Math.max(change, COORDS_CHANGED);
      fireStateChanged();
   }

   public float getSmax()
   {
      return (Float)getValue("smax");
   }

   public void setSmax(float smax)
   {
      setValue("smax",smax);
      fireStateChanged();
   }

   public float getThr()
   {
      return (Float)getValue("thr");
   }

   public void setThr(float thr)
   {
      setValue("thr",thr);
      change = GEOMETRY_CHANGED;
      fireStateChanged();
   }

   public int getType()
   {
      return (Integer)getValue("type");
   }

   public void setType(int type)
   {
      setValue("type",type);
      change = Math.max(change, GLYPHS_CHANGED);
      fireStateChanged();
   }

   public int[] getUpCrop()
   {
      return (int[])getValue("upCrop");
   }

   public void setCrop(int[] lowCrop, int[] upCrop)
   {
      setValue("lowCrop",lowCrop);
      setValue("upCrop",upCrop);
      change = GEOMETRY_CHANGED;
      fireStateChanged();
   }

   public boolean isUseAbs()
   {
      return (Boolean)getValue("useAbs");
   }

   public void setUseAbs(boolean useAbs)
   {
      setValue("useAbs",useAbs);
      change = Math.max(change, COORDS_CHANGED);
      fireStateChanged();
   }

   public boolean isUseSqrt()
   {
      return (Boolean)getValue("useSqrt");
   }

   public void setUseSqrt(boolean useSqrt)
   {
      setValue("useSqrt",useSqrt);
      change = Math.max(change, COORDS_CHANGED);
      fireStateChanged();
   }

   public boolean isThrRelative()
   {
      return (Boolean)getValue("thrRelative");
   }

   public void setThrRelative(boolean thrRelative)
   {
      setValue("thrRelative",thrRelative);
      change = GEOMETRY_CHANGED;
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
   
   @Override
   public void fireStateChanged()
   {
      if (!active)
         return;
      ChangeEvent e = new ChangeEvent(this);
      for (int i = 0; i < changeListenerList.size(); i++) {
          changeListenerList.get(i).stateChanged(e);          
       }
   }
   
}
