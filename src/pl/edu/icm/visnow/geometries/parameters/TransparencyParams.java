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

import java.util.ArrayList;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.render.RenderEvent;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.render.RenderEventListener;

/**
 *
 * @author know
 */
public class TransparencyParams
{
   private int component = 0;
   private int[] map = new int[256];
   private float dataLow = 0;
   private float dataUp = 255;
   protected boolean active = true;
   protected RenderEventListener listener = null;
   private boolean adjusting = false;

   public TransparencyParams()
   {
      for (int i = 0; i < map.length; i++)
         map[i] = (i + 2) / 3;
   }
   
   public String toString()
   {
      return "cmp "+component+" map=["+map[0]+"..."+map[50]+"..."+map[100]+"..."+map[150]+"..."+map[200]+"..."+map[250]+"]";
   }

   public float getDataUp()
   {
      return dataUp;
   }

   public void setDataUp(float dataUp)
   {
      this.dataUp = dataUp;
      if (active)
         fireStateChanged();
   }
   
   public float getDataLow()
   {
      return dataLow;
   }

   public void setDataLow(float dataLow)
   {
      this.dataLow = dataLow;
      if (active)
         fireStateChanged();
   }

   public int[] getMap()
   {
      return map;
   }
   
   public void setMap(int[] map)
   {
      this.map = map;
      if (active)
         fireStateChanged();
   }

   public int getMap(int index)
   {
      return this.map[index];
   }

   public void setMap(int index, int newMap)
   {
      this.map[index] = newMap;
   }

   public int getComponent()
   {
      return component;
   }

   public void setComponent(int component)
   {
      this.component = component;
      if (active)
         fireStateChanged();
   }


   public void setActive(boolean active)
   {
      this.active = active;
      if (active)
         fireStateChanged();
   }


   /**
    * Get the value of adjusting
    *
    * @return the value of adjusting
    */
   public boolean isAdjusting()
   {
      return adjusting;
   }

   /**
    * Set the value of adjusting
    *
    * @param adjusting new value of adjusting
    */
   public void setAdjusting(boolean adjusting)
   {
      this.adjusting = adjusting;
   }

   /**
    * Utility field holding list of RenderEventListeners.
    */
   private transient ArrayList<RenderEventListener> renderEventListenerList =
           new ArrayList<RenderEventListener>();

   /**
    * Registers RenderEventListener to receive events.
    * @param listener The listener to register.
    */
   public synchronized void addListener(RenderEventListener listener)
   {
      renderEventListenerList.add(listener);
   }

   /**
    * Removes RenderEventListener from the list of listeners.
    * @param listener The listener to remove.
    */
   public synchronized void removeRenderEventListener(RenderEventListener listener)
   {
      renderEventListenerList.remove(listener);
   }

   /**
    * Notifies all registered listeners about the event.
    *
    * @param object Parameter #1 of the <CODE>ChangeEvent<CODE> constructor.
    */
   public void fireStateChanged()
   {
      RenderEvent e = new RenderEvent(this, RenderEvent.TRANSPARENCY);
      for (int i = 0; i < renderEventListenerList.size(); i++) 
          renderEventListenerList.get(i).renderExtentChanged(e);
   }


}
