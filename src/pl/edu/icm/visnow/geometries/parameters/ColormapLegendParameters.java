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

package pl.edu.icm.visnow.geometries.parameters;

import java.awt.Color;
import java.util.ArrayList;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pl.edu.icm.visnow.geometries.objects.ColormapLegend;

/**
 *
 * @author Krzysztof S. Nowinski
 * University of Warsaw, ICM
 */
public class ColormapLegendParameters 
{
   protected int[] colorMapLookup;
   protected float colormapLow = 0;
   protected float colormapUp  = 1;
   protected int Position = ColormapLegend.NONE;
   protected float w = .3f, l = .08f, x = .16f, y = .16f;
   protected Color color = Color.GRAY;
   protected int colormap = 0;
   protected int fontSize = 14;
   protected String name = "", unit = "";
   private boolean displayable = true;

   public boolean isDisplayable()
   {
      return displayable;
   }
   
   public void setDisplayable(boolean displayable)
   {
      this.displayable = displayable;
      fireStateChanged();
   }

   public ColormapLegendParameters()
   {
      
   }
   
   private boolean enabled = true;
   
   public boolean isEnabled()
   {
      return enabled;
   }
   
   public void setEnabled(boolean enabled)
   {
      this.enabled = enabled;
   }

   public Color getColor()
   {
      return color;
   }

   public void setColor(Color Color)
   {
      this.color = Color;
   }


   public int getPosition()
   {
      return Position;
   }

   public void setPosition(int Position)
   {
      this.Position = Position;
   }

   public float getColormapLow()
   {
      return colormapLow;
   }

   public void setColormapLow(float colormapLow)
   {
      this.colormapLow = colormapLow;
   }

   public float getColormapUp()
   {
      return colormapUp;
   }

   public void setColormapUp(float colormapUp)
   {
      this.colormapUp = colormapUp;
   }

   public float getL()
   {
      return l;
   }

   public void setL(float l)
   {
      this.l = l;
   }

   public float getW()
   {
      return w;
   }

   public void setW(float w)
   {
      this.w = w;
   }

   public float getX()
   {
      return x;
   }

   public void setX(float x)
   {
      this.x = x;
   }

   public float getY()
   {
      return y;
   }

   public void setY(float y)
   {
      this.y = y;
   }

   public int getFontSize()
   {
      return fontSize;
   }

   public void setFontSize(int fontSize)
   {
      this.fontSize = fontSize;
   }

   public int getColormap()
   {
      return colormap;
   }

   public void setColormap(int colormap)
   {
      this.colormap = colormap;
      fireStateChanged();
   }

   public String getName()
   {
      return name;
   }

   public String getUnit()
   {
      return unit;
   }
   
   public void setTexts(String name, String unit)
   {
      this.name = name;
      this.unit = unit;
   }

   public int[] getColorMapLookup()
   {
      return colorMapLookup;
   }

   public void setColorMapLookup(int[] colorMapLookup)
   {
      this.colorMapLookup = colorMapLookup;
   }

   /**
    * Utility field holding list of ChangeListeners.
    */
   private transient ArrayList<ChangeListener> changeListenerList =
           new ArrayList<ChangeListener>();

   /**
    * Registers ChangeListener to receive events.
    * @param listener The listener to register.
    */
   public synchronized void addChangeListener(ChangeListener listener)
   {
      changeListenerList.add(listener);
   }

   /**
    * Removes ChangeListener from the list of listeners.
    * @param listener The listener to remove.
    */
   public synchronized void removeChangeListener(ChangeListener listener)
   {
      changeListenerList.remove(listener);
   }

   /**
    * Notifies all registered listeners about the event.
    *
    * @param object Parameter #1 of the <CODE>ChangeEvent<CODE> constructor.
    */
   public void fireStateChanged()
   {
      ChangeEvent e = new ChangeEvent(this);
      for (ChangeListener listener : changeListenerList)
         listener.stateChanged(e);
   }
}
