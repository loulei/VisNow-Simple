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

package pl.edu.icm.visnow.datamaps;

import java.beans.PropertyChangeListener;
import java.util.Vector;
import pl.edu.icm.visnow.datamaps.colormap1d.ColorMap1D;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.render.RenderEvent;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.render.RenderEventListener;
import pl.edu.icm.visnow.lib.utils.Range;

/**
 *
 * @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class CustomizableColorMap implements ColorMap

{
   public static final int CONTINUOUS = 50;

    /**
     * @return the active
     */
    public Active getActive() {
        return active;
    }
   
   protected static enum mapType {STANDARD, TWOCOLOR, THREECOLOR};
   
   protected int mapIndex = ColorMapManager.COLORMAP1D_RAINBOW;
   protected ColorMap1D map = ColorMapManager.getInstance().getColorMap1D(mapIndex);
   protected float brightness = 0;
   protected int nSteps = 0;
   protected boolean reverse = false;
   protected int[] rgbColorTable = new int[ColorMapManager.SAMPLING_TABLE];
   protected int[] argbColorTable = new int[ColorMapManager.SAMPLING_TABLE];
   protected byte[] rgbByteColorTable = new byte[3 * ColorMapManager.SAMPLING_TABLE];
   protected byte[] argbByteColorTable = new byte[4 * ColorMapManager.SAMPLING_TABLE];
   protected int[] continuousARGBTable = new int[ColorMapManager.SAMPLING_TABLE];
   protected float dataLow = 0;
   protected float dataUp  = 255;
   protected mapType type = mapType.STANDARD;
   protected float[] c0  = {0, 0, 0},  c1 = {1 ,1 ,1},
                     c00 = {0, 0, 1}, c05 = {1, 1, 1}, c10 = {1, 0, 0};
   protected boolean adjusting = false;
   
   public static enum Active{SLEEP, ONETIME, CONTINUOUS};
   private Active active = Active.CONTINUOUS;
   
   public CustomizableColorMap()
   {
      updateColorTables();
   }

   public void setMinmax(float low, float up)
   {
      dataLow = low;
      dataUp = up;
      updateColorTables();
//      fireStateChanged(Active.ONETIME, RenderEvent.COLORS);
   }

   public float getBrightness()
   {
      return brightness;
   }

   public void setBrightness(float brightness)
   {
      this.brightness = brightness;
      updateColorTables();
//      fireStateChanged(Active.ONETIME, RenderEvent.COLORS);
   }
   
   public void setFinalBrightness(float brightness)
   {
      this.brightness = brightness;
      updateColorTables();
      fireStateChanged(Active.ONETIME, RenderEvent.COLORS);
   }

   public void setReverse(boolean reverse)
   {
      this.reverse = reverse;
      updateColorTables();
      fireStateChanged(Active.ONETIME, RenderEvent.COLORS);
   }

   public void setSteps(int steps)
   {
      this.nSteps = steps != CONTINUOUS ? steps : 0;
      updateColorTables();
      fireStateChanged(Active.CONTINUOUS, RenderEvent.COLORS);
   }
   
   public void silentlySetSteps(int steps)
   {
      this.nSteps = steps != CONTINUOUS ? steps : 0;
      updateColorTables();
   }
   
   public void setFinalSteps(int steps)
   {
      this.nSteps = steps != CONTINUOUS ? steps : 0;
      updateColorTables();
      fireStateChanged(Active.ONETIME, RenderEvent.COLORS);
   }

   public int getnSteps()
   {
      return nSteps;
   }

   public int getMapType()
   {
      return mapIndex;
   }

   public void setMapIndex(int mapIndex)
   {
      this.mapIndex = mapIndex;
      map = ColorMapManager.getInstance().getColorMap1D(mapIndex);
      if      (map.getName().equalsIgnoreCase("bicolor"))  type = mapType.TWOCOLOR;
      else if (map.getName().equalsIgnoreCase("tricolor")) type = mapType.THREECOLOR;
      else                                                 type = mapType.STANDARD;
      updateColorTables();
      fireStateChanged(Active.ONETIME, RenderEvent.COLORS);
   }
   
   private void updateAdditionalColorTables()
   {
      for (int i = 0; i < rgbColorTable.length; i++)
      {
          rgbColorTable[i] = argbColorTable[i] | (0xff << 24);
          argbByteColorTable[4 * i + 1] = rgbByteColorTable[3 * i]     = (byte) (0xff & (argbColorTable[i] >> 16));
          argbByteColorTable[4 * i + 2] = rgbByteColorTable[3 * i + 1] = (byte) (0xff & (argbColorTable[i] >> 8));
          argbByteColorTable[4 * i + 3] = rgbByteColorTable[3 * i + 2] = (byte) (0xff &  argbColorTable[i]);
          argbByteColorTable[4 * i]     = (byte) (0xff & (argbColorTable[i] >> 24));
      }
      fireStateChanged(Active.ONETIME, RenderEvent.COLORS);
   }
   
   private void createSteppedARGB()
   {
      int n = argbColorTable.length - 1;
      Range stepRange = new Range(nSteps, dataLow, dataUp);
      float low = stepRange.getLow();
      float d = stepRange.getStep();
      float toInd = n / (dataUp - dataLow);
      int k = 0, l = 0;
      for (int i = -1; i <= dataUp / d; i++)
      {
         k = Math.max(0, (int)((i * d + low - dataLow) * toInd));
         l = Math.min((int)(((i + 1) * d + low - dataLow) * toInd), argbColorTable.length);
         for (int j = k; j < l; j++)
            argbColorTable[j] = argbColorTable[k];
      }
      updateAdditionalColorTables();
   }
   
   public void setColorTables(float[] inc0, float[] inc1)
   {
      if (inc0 != null) c0 = inc0;
      if (inc1 != null) c1 = inc1;
      int n = argbColorTable.length - 1;
      float[] dc = new float[c0.length];
      for (int i = 0; i < dc.length; i++)
         dc[i] = (c1[i] - c0[i]) / n;
      for (int i = 0; i < argbColorTable.length; i++)
      {
         int a = 0xff;
         int r = 0xff & (int)(255 * (c0[0] + i * dc[0]));
         int g = 0xff & (int)(255 * (c0[1] + i * dc[1]));
         int b = 0xff & (int)(255 * (c0[2] + i * dc[2]));
         argbColorTable[i] = (a << 24) | (r << 16) | (g << 8) | b;
      }
      if (nSteps > 0)
         createSteppedARGB();
      else
         updateAdditionalColorTables();
   }
   
   public void setColorTables(float[] inc00, float[] inc05, float[] inc10)
   {
      if (inc00 != null) c00 = inc00;
      if (inc05 != null) c05 = inc05;
      if (inc10 != null) c10 = inc10;
      int n = (argbColorTable.length - 1) / 2;
      float[] dc = new float[c0.length];
      for (int i = 0; i < dc.length; i++)
         dc[i] = (c05[i] - c00[i]) / n;
      for (int i = 0; i < argbColorTable.length / 2; i++)
      {
         int a = 0xff;
         int r = 0xff & (int)(255 * (c00[0] + i * dc[0]));
         int g = 0xff & (int)(255 * (c00[1] + i * dc[1]));
         int b = 0xff & (int)(255 * (c00[2] + i * dc[2]));
         argbColorTable[i] = (a << 24) | (r << 16) | (g << 8) | b;
      }
      for (int i = 0; i < dc.length; i++)
         dc[i] = (c10[i] - c05[i]) / n;
      for (int i = 0; i < argbColorTable.length / 2; i++)
      {
         int a = 0xff;
         int r = 0xff & (int)(255 * (c05[0] + i * dc[0]));
         int g = 0xff & (int)(255 * (c05[1] + i * dc[1]));
         int b = 0xff & (int)(255 * (c05[2] + i * dc[2]));
         argbColorTable[i + argbColorTable.length / 2] = (a << 24) | (r << 16) | (g << 8) | b;
      }
      
      if (nSteps > 0)
         createSteppedARGB();
      else
         updateAdditionalColorTables();
   }
   
   private void updateColorTables()
   {
      switch (type)
      {
         case STANDARD:
            float br = 1 - Math.abs(brightness);
            int[] cTable = map.getARGBColorTable();
            int n = cTable.length - 1;
            for (int i = 0; i < argbColorTable.length; i++)
            {
               int j = cTable[i];
               int a = 0xff & (j >> 24);
               int r = 0xff & (j >> 16);
               int g = 0xff & (j >> 8);
               int b = 0xff & j;
               if ((brightness < 0))
                  j = (((int)(br * r) & 0xff) << 16) | 
                      (((int)(br * g) & 0xff) << 8)  | 
                      ((int)(br * b) & 0xff) | 
                      ((a & 0xff) << 24);
               else
                  j = (((int)(br * r + (1 - br) * 255) & 0xff) << 16) |
                      (((int)(br * g + (1 - br) * 255) & 0xff) << 8)  |
                      ((int)(br * b + (1 - br) * 255) & 0xff) |
                       ((a & 0xff) << 24);
               if (reverse)
                  argbColorTable[n - i] = j;
               else
                  argbColorTable[i] = j;
            }
            break;
         case TWOCOLOR:
            setColorTables(null, null);
            return;
         case THREECOLOR:
            setColorTables(null, null, null);
            return;
            
      }
      if (nSteps > 0)
         createSteppedARGB();
      else
         updateAdditionalColorTables();
   }

   public void setActive(Active active)
   {
      this.active = active;
      if (active != Active.SLEEP)
         fireStateChanged(Active.ONETIME, RenderEvent.COLORS);
   }

   public void setActiveValue(Active active)
   {
      this.active = active;
   }
   
  /**
    * Utility field holding list of RenderEventListeners.
    */
   private Vector<RenderEventListener> renderEventListeners = new Vector<RenderEventListener>();

   /**
    * Registers RenderEventListener to receive events.
    * @param listener The listener to register.
    */
   public synchronized void addRenderEventListener(RenderEventListener listener)
   {
      renderEventListeners.add(listener);
   }

   /**
    * Removes RenderEventListener from the list of listeners.
    * @param listener The listener to remove.
    */
   public synchronized void removeRenderEventListener(RenderEventListener listener)
   {
      renderEventListeners.remove(listener);
   }

   /**
    * Notifies all registered listeners about the event.
    *
    * @param object Parameter #1 of the <CODE>ChangeEvent<CODE> constructor.
    */
   public void fireStateChanged(Active level, int change)
   {
      if (active == Active.SLEEP)
         return;
      if (active == Active.CONTINUOUS || level == Active.ONETIME)
      {
         RenderEvent e = new RenderEvent(this, change);
         for (RenderEventListener  renderEventListener: renderEventListeners)
         {
             renderEventListener.renderExtentChanged(e);
         }
      }
   }
   
   @Override
   public boolean isBuildin()
   {
      return map.isBuildin();
   }

   @Override
   public String getName()
   {
      return "customized" + map.getName();
   }

   @Override
   public int[] getRGBColorTable()
   {
      return rgbColorTable;
   }

   @Override
   public int[] getARGBColorTable()
   {
      return argbColorTable;
   }

   @Override
   public byte[] getRGBByteColorTable()
   {
      return rgbByteColorTable;
   }

   @Override
   public byte[] getARGBByteColorTable()
   {
      return argbByteColorTable;
   }

   public ColorMap1D getMap()
   {
      return map;
   }

   @Override
   public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener)
   {
      
   }

   @Override
   public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener)
   {
      
   }

   public boolean isAdjusting()
   {
      return adjusting;
   }

   public void setAdjusting(boolean adjusting)
   {
      this.adjusting = adjusting;
   }

}
