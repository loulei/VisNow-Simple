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

import pl.edu.icm.visnow.datamaps.CustomizableColorMap;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.render.RenderEvent;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.render.RenderEventListener;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class ColorComponentParams
{
   private int dataComponent = 0;
   private CustomizableColorMap map = new CustomizableColorMap();
   private int mapType = 0;
   private float dataMin = 0;
   private float dataMax = 0;
   private float cmpMin = .1f;
   private float cmpMax = 1;
   private boolean wrap = false;
   private RenderEventListener listener = null;
   private boolean active = true;
   private boolean adjusting = false;

   public int getDataComponent()
   {
      return dataComponent;
   }

   public void setDataComponent(int dataComponent)
   {
      this.dataComponent = dataComponent;
      fireStateChanged();
   }

   public int getMapType()
   {
      return mapType;
   }

   public void setMapType(int mapType)
   {
      this.mapType = mapType;
      map.setMapIndex(mapType);
      fireStateChanged();
   }

   public float getCmpMax()
   {
      return cmpMax;
   }

   public void setCmpMax(float cmpMax)
   {
      this.cmpMax = cmpMax;
      fireStateChanged();
   }

   public float getCmpMin()
   {
      return cmpMin;
   }

   public void setCmpMin(float cmpMin)
   {
      this.cmpMin = cmpMin;
      fireStateChanged();
   }

   public boolean isWrap()
   {
      return wrap;
   }

   public void setWrap(boolean wrap)
   {
      this.wrap = wrap;
      fireStateChanged();
   }


   public float getDataMax()
   {
      return dataMax;
   }

   public float getDataMin()
   {
      return dataMin;
   }

   public void setDataMinMax(float dataMin, float dataMax)
   {
      this.dataMin = dataMin;
      this.dataMax = dataMax;
      map.setMinmax(dataMin, dataMax);
   }

   public void setListener(RenderEventListener listener)
   {
      this.listener = listener;
      map.addRenderEventListener(listener);
   }

   public void setActive(boolean active)
   {
      this.active = active;
   }

   public CustomizableColorMap getMap()
   {
      return map;
   }
   
   public void copy(ColorComponentParams src)
   {
      dataComponent = src.dataComponent;
      dataMin       = src.dataMin;
      dataMax       = src.dataMax;
      cmpMin        = src.cmpMin;
      cmpMax        = src.cmpMax;
   }

   public void fireStateChanged()
   {
      if (active)
         listener.renderExtentChanged(new RenderEvent(this, RenderEvent.COLORS, adjusting || map.isAdjusting()));
   }

   @Override
   public String toString()
   {
      switch (dataComponent)
      {
      case DataMappingParams.COORDX:
         return String.format("x [%6.3f,%6.3f]->[%6.3f,%6.3f](%d)",dataMin,dataMax,cmpMin,cmpMax,mapType);
      case DataMappingParams.COORDY:
         return String.format("y [%6.3f,%6.3f]->[%6.3f,%6.3f](%d)",dataMin,dataMax,cmpMin,cmpMax,mapType);
      case DataMappingParams.COORDZ:
         return String.format("y [%6.3f,%6.3f]->[%6.3f,%6.3f](%d)",dataMin,dataMax,cmpMin,cmpMax,mapType);
      case DataMappingParams.NORMALX:
         return String.format("normal x [%6.3f,%6.3f]->[%6.3f,%6.3f](%d)",dataMin,dataMax,cmpMin,cmpMax,mapType);
      case DataMappingParams.NORMALY:
         return String.format("normal y [%6.3f,%6.3f]->[%6.3f,%6.3f](%d)",dataMin,dataMax,cmpMin,cmpMax,mapType);
      case DataMappingParams.NORMALZ:
         return String.format("normal z [%6.3f,%6.3f]->[%6.3f,%6.3f](%d)",dataMin,dataMax,cmpMin,cmpMax,mapType);
      case DataMappingParams.INDEXI:
         return String.format("i [%6.3f,%6.3f]->[%6.3f,%6.3f](%d)",dataMin,dataMax,cmpMin,cmpMax,mapType);
      case DataMappingParams.INDEXJ:
         return String.format("j [%6.3f,%6.3f]->[%6.3f,%6.3f](%d)",dataMin,dataMax,cmpMin,cmpMax,mapType);
      default:
         return String.format("cmp=%d [%6.3f,%6.3f]->[%6.3f,%6.3f](%d)",dataComponent,dataMin,dataMax,cmpMin,cmpMax,mapType);
      }
   }
   
   public int[] getRGBColorTable()
   {
      return map.getRGBColorTable();
   }

   public int[] getARGBColorTable()
   {
      return map.getARGBColorTable();
   }
   
   public byte[] getRGBByteColorTable()
   {
      return map.getRGBByteColorTable();
   }

   public byte[] getARGBByteColorTable()
   {
      return map.getARGBByteColorTable();
   }

   public boolean isAdjusting()
   {
      return adjusting || map.isAdjusting();
   }

   public void setAdjusting(boolean adjusting)
   {
      this.adjusting = adjusting;
      map.setAdjusting(adjusting);
   }

}
