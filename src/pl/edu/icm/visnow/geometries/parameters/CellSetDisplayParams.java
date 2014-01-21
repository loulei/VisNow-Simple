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

import pl.edu.icm.visnow.datasets.CellSet;
import pl.edu.icm.visnow.datasets.IrregularField;

/**
 *
 * @author Krzysztof S. Nowinski
 *   <p>   University of Warsaw, ICM
 */
public class CellSetDisplayParams
{
   public static final int NO_PICK_INDICATOR  = 0;
   public static final int BOX_PICK_INDICATOR = 1;
   public static final int EDG_PICK_INDICATOR = 2;
   public static final int SRF_PICK_INDICATOR = 3;
   protected DataMappingParams dataMappingParams;
   protected RenderingParams   renderingParams;
   protected TransformParams   transformParams;
   protected boolean active = true;
   protected boolean selectionActive = false;
   protected int selectByComponent   = 0;
   protected float selectOver        = 0;
   protected float selectUnder       = 1;
   protected int pickIndicator       = NO_PICK_INDICATOR;
   protected boolean inheriting = true;

   public CellSetDisplayParams()
   {
      dataMappingParams = new DataMappingParams();
      renderingParams   = new RenderingParams();
      dataMappingParams.getTransparencyParams().addListener(renderingParams.getTransparencyChangeListener());
      transformParams = new TransformParams();
   }

   public CellSetDisplayParams(IrregularField field, CellSet set)
   {
      dataMappingParams = new DataMappingParams(field, set);
      renderingParams   = new RenderingParams();
      dataMappingParams.getTransparencyParams().addListener(renderingParams.getTransparencyChangeListener());
      transformParams = new TransformParams();
   }

   public DataMappingParams getDataMappingParams()
   {
      return dataMappingParams;
   }

   public void setDataMappingParams(DataMappingParams dataMappingParams)
   {
      this.dataMappingParams = dataMappingParams;
   }

   public RenderingParams getRenderingParams()
   {
      return renderingParams;
   }

   public void setRenderingParams(RenderingParams renderingParams)
   {
      this.renderingParams = renderingParams;
   }

   public TransformParams getTransformParams()
   {
      return transformParams;
   }

   public void setTransformParams(TransformParams transformParams)
   {
      this.transformParams = transformParams;
   }

   public boolean isActive()
   {
      return active;
   }

   public void setActive(boolean active)
   {
      this.active = active;
      dataMappingParams.setActive(active);
   }

   public int getSelectByComponent()
   {
      return selectByComponent;
   }

   public void setSelectByComponent(int selectByComponent)
   {
      this.selectByComponent = selectByComponent;
   }

   public float getSelectOver()
   {
      return selectOver;
   }

   public void setSelectOver(float selectOver)
   {
      this.selectOver = selectOver;
   }

   public float getSelectUnder()
   {
      return selectUnder;
   }

   public void setSelectUnder(float selectUnder)
   {
      this.selectUnder = selectUnder;
   }

   public boolean isSelectionActive()
   {
      return selectionActive;
   }

   public void setSelectionActive(boolean selectionActive)
   {
      this.selectionActive = selectionActive;
   }

   public int getPickIndicator()
   {
      return pickIndicator;
   }

   public void setPickIndicator(int pickIndicator)
   {
      this.pickIndicator = pickIndicator;
   }

   public boolean isInheriting()
   {
      return inheriting;
   }

   public void setInheriting(boolean inheriting)
   {
      this.inheriting = inheriting;
   }

   public void copy(CellSetDisplayParams src)
   {
      renderingParams.copy(src.renderingParams);
      transformParams.copy(src.transformParams);
      selectByComponent  = src.selectByComponent;
      selectOver         = src.selectOver;
      selectUnder        = src.selectUnder;
      pickIndicator      = src.pickIndicator;
   }

}
