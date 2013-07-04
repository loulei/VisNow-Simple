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

package pl.edu.icm.visnow.geometries.objects;

import javax.media.j3d.IndexedLineStripArray;
import javax.media.j3d.IndexedPointArray;
import javax.media.j3d.IndexedTriangleStripArray;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.geometries.objects.generics.OpenBranchGroup;
import pl.edu.icm.visnow.geometries.parameters.DataMappingParams;
import pl.edu.icm.visnow.geometries.parameters.RegularFieldDisplayParams;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.render.RenderEventListener;

/**
 *
 * @author Krzysztof S. Nowinski
 *   <p>   University of Warsaw, ICM
 */
public abstract class RegularFieldGeometry extends FieldGeometry
{
   protected RegularField field;
   protected int colorMode                         = DataMappingParams.UNCOLORED;
   protected int currentColorMode                  = -1;
   protected int nTriangleStrips                   = 0;
   protected int nLineStrips                       = 0;
   protected int nNodePoints                       = 0;
   protected int[] dims                            = null;
   protected int[] lineStripCounts                 = null;
   protected int[] triangleStripCounts             = null;
   protected IndexedTriangleStripArray triangleArr = null;
   protected IndexedLineStripArray edgeArr         = null;
   protected IndexedPointArray nodeArr             = null;
   protected RegularFieldDisplayParams fieldDisplayParams;
   protected RenderEventListener renderEventListener;

   protected static final boolean[][] resetGeometry = 
   {{false,true, true, true, true, true, true, true, true},
    {true, false,false,false,false,false,false,false,true},
    {true, false,false,false,false,false,false,false,true},
    {true, false,false,false,false,false,false,false,true},
    {true, false,false,false,false,false,false,false,true},
    {true, false,false,false,false,false,false,false,true},
    {true, false,false,false,false,false,false,false,true},
    {true, false,false,false,false,false,false,false,true},
    {true, true, true, true, true, true, true, true, false}};

   public RegularFieldGeometry(String name)
   {
      setName(name);
      transformedGeometries.addChild(geometries);
      geometry.addChild(transformedGeometries);
      clearGeometries2D();
      addGeometry2D(colormapLegend);
   }

   public boolean setField(RegularField inField, boolean createParams)
   {
      if (inField == null)
         return false;
      if (createParams)
          return setField(inField);
      dims = inField.getDims();
      nNodes = inField.getNNodes();
      structureChanged = (field == null || !inField.isStructureCompatibleWith(this.field));
      dataChanged      = (field == null || !inField.isDataCompatibleWith(this.field));
      this.field = inField;
      name = inField.getName();
      extents = inField.getExtents();
      if (structureChanged || dataChanged)
         clearAllGeometry();
      return true;
   }


   /**
    * Set the value of field
    *
    * @param field new value of field
    */
   public boolean setField(RegularField inField)
   {
      if (inField == null)
         return false;
      newParams = false;
      if (field == null || (field.getDims().length == 3 && inField.getDims().length != 3) || (field.getDims().length != 3 && inField.getDims().length == 3))
      {
         fieldDisplayParams = new RegularFieldDisplayParams(inField);
         dataMappingParams = fieldDisplayParams.getMappingParams();
         colormapLegend.setParams(dataMappingParams.getColormapLegendParameters());
         DataArray da = inField.getData(dataMappingParams.getColorMap0Params().getDataComponent());
         renderingParams = fieldDisplayParams.getDisplayParams();
         if (renderEventListener != null)
         {
            dataMappingParams.addRenderEventListener(renderEventListener);
            renderingParams.addRenderEventListener(renderEventListener);
         }
         newParams = true;
      }
      transformParams = fieldDisplayParams.getTransformParams();
      fieldDisplayParams.getTransformParams().addChangeListener(new ChangeListener()
      {
         public void stateChanged(ChangeEvent evt)
         {
            transformedGeometries.setTransform(transformParams.getTransform());
         }
      });
      transformedGeometries.setTransform(transformParams.getTransform());
      dims = inField.getDims();
      nNodes = inField.getNNodes();
      structureChanged = (field == null || !inField.isStructureCompatibleWith(this.field));
      dataChanged = (field == null || !inField.isDataCompatibleWith(this.field));
      this.field = inField;
      name = inField.getName();
      if (structureChanged || dataChanged)
         clearAllGeometry();
      return true;
   }

   /**
    * Get the value of fieldDisplayParams
    *
    * @return the value of fieldDisplayParams
    */
   public RegularFieldDisplayParams getFieldDisplayParams()
   {
      return fieldDisplayParams;
   }

   /**
    * Set the value of fieldDisplayParams
    *
    * @param fieldDisplayParams new value of fieldDisplayParams
    */
   public void setFieldDisplayParams(RegularFieldDisplayParams fieldDisplayParams)
   {
      if (fieldDisplayParams == null)
         return;
      this.fieldDisplayParams = fieldDisplayParams;
      if (dataMappingParams != null)
         dataMappingParams.removeRenderEventListener(renderEventListener);
      if (renderingParams != null)
         renderingParams.removeRenderEventListener(renderEventListener);
      dataMappingParams = fieldDisplayParams.getMappingParams();
      colormapLegend.setParams(dataMappingParams.getColormapLegendParameters());
      DataArray da = field.getData(dataMappingParams.getColorMap0Params().getDataComponent());
      renderingParams = fieldDisplayParams.getDisplayParams();
      transformParams = fieldDisplayParams.getTransformParams();
      appearance = renderingParams.getAppearance();
      appearance.setUserData(this);
      lineAppearance = renderingParams.getLineAppearance();
      lineAppearance.setUserData(this);
      if (renderEventListener != null)
      {
         dataMappingParams.addRenderEventListener(renderEventListener);
         renderingParams.addRenderEventListener(renderEventListener);
      }
      transformParams = fieldDisplayParams.getTransformParams();
      fieldDisplayParams.getTransformParams().addChangeListener(new ChangeListener()
      {
         public void stateChanged(ChangeEvent evt)
         {
            transformedGeometries.setTransform(transformParams.getTransform());
         }
      });
      transformedGeometries.setTransform(transformParams.getTransform());
   }

   public OpenBranchGroup getGeometry()
   {
      updateGeometry();
      return geometry;
   }

   public OpenBranchGroup getGeometryObject()
   {
      return geometry;
   }
   
   @Override
   public boolean setField(Field inField, boolean createParams)
   {
      if (inField == null || !(inField instanceof RegularField))
         return false;
      return setField((RegularField)inField, createParams);
   }

   @Override
   public boolean setField(Field inField)
   {
      if (inField == null || !(inField instanceof RegularField))
         return false;
      return setField((RegularField)inField);
   }

   @Override
   public Field getField()
   {
      return field;
   }   
}
