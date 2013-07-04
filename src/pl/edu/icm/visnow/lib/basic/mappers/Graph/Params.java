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

package pl.edu.icm.visnow.lib.basic.mappers.Graph;

import java.awt.Color;
import pl.edu.icm.visnow.engine.core.ParameterEgg;
import pl.edu.icm.visnow.engine.core.ParameterType;
import pl.edu.icm.visnow.engine.core.Parameters;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class Params extends Parameters
{
   protected static ParameterEgg[] eggs = new ParameterEgg[]
   {
      new ParameterEgg<int[]>("horizontal extents", ParameterType.dependent, new int[]{30,90}),
      new ParameterEgg<int[]>("vertical extents",   ParameterType.dependent, new int[]{75,97}),
      new ParameterEgg<Color>("color",              ParameterType.independent, Color.WHITE),
      new ParameterEgg<Integer>("font size",        ParameterType.independent, 15),
      new ParameterEgg<Float>("line width",         ParameterType.independent, 2.f),
      new ParameterEgg<String[]>("axes labels",     ParameterType.independent, new String[] {"x", "y"}),
      new ParameterEgg<String>("title",             ParameterType.independent, ""),
      new ParameterEgg<DisplayedData[]>("data",     ParameterType.independent, null),
      new ParameterEgg<Boolean>("color legend",     ParameterType.independent, false),
      new ParameterEgg<Boolean>("refresh",          ParameterType.independent, false),
   };
  
   public Params()
   {
      super(eggs);     
      setValue("horizontal extents", new int[] {30, 90});
      setValue("vertical extents", new int[] {75,97});
      setValue("axes labels", new String[]{"x", "y"});      
   }
   
   public int[] getHorizontalExtents()
   {
      return (int[])getValue("horizontal extents");
   }
   
   public void setHorizontalExtents(int low, int up)
   {
      int[] hex = getHorizontalExtents();
      hex[0] = low;
      hex[1] = up;
      fireStateChanged();
   }
   
   public int[] getVerticalExtents()
   {
      return (int[])getValue("vertical extents");
   }
   
   public void setVerticalExtents(int low, int up)
   {
      int[] hex = getVerticalExtents();
      hex[0] = low;
      hex[1] = up;
      fireStateChanged();
   }
   
   public Color getColor()
   {
      return (Color)getValue("color");
   }
   
   public void setColor(Color color)
   {
      setValue("color", color);
      fireStateChanged();
   }
   
    public DisplayedData[] getDisplayedData()
   {
      return (DisplayedData[])getValue("data");
   }
   
   public void setDisplayedData(DisplayedData[] displayedData)
   {
      setValue("data", displayedData);
      fireStateChanged();
   }
   
   public int getFontSize()
   {
      return (Integer)getValue("font size");
   }
   
   public void setFontSize(int size)
   {
      setValue("font size", size);
      fireStateChanged();
   }
   
   public float getLineWidth()
   {
      return (Float)getValue("line width");
   }
   
   public void setLineWidth(float width)
   {
      setValue("line width", width);
      fireStateChanged();
   }
   
   public String[] getAxesLabels()
   {
      return (String[])getValue("axes labels");
   }
   
   public void setAxesLabels(String[] labels)
   {
      setValue("axes labels", labels);
      fireStateChanged();
   }
   
  
   public String getTitle()
   {
      return (String)getValue("title");
   }
   
   public void setTitle(String title)
   {
      setValue("title", title);
      fireStateChanged();
   }
   
   public void updateTable()
   {
      fireStateChanged();
   }
   
   public boolean isColorLegend()
   {
      return (Boolean)getValue("color legend");
   }
   
   public void setColorLegend(boolean cl)
   {
      setValue("color legend", cl);
      fireStateChanged();
   }
   
   public boolean isRefresh()
   {
      boolean r = (Boolean)getValue("refresh");
      setValue("refresh", false);
      return r;
   }
   
   public void setRefresh(boolean r)
   {
      setValue("refresh", false);
      fireStateChanged();
   }
   
}
