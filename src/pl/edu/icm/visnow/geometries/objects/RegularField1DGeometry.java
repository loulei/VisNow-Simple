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

import javax.media.j3d.GeometryArray;
import javax.media.j3d.LineStripArray;
import javax.swing.event.ChangeEvent;
import javax.vecmath.Color3f;
import pl.edu.icm.visnow.datamaps.ColorMapManager;
import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.geometries.objects.generics.OpenBranchGroup;
import pl.edu.icm.visnow.geometries.objects.generics.OpenShape3D;
import pl.edu.icm.visnow.geometries.parameters.DataMappingParams;
import pl.edu.icm.visnow.geometries.parameters.RegularFieldDisplayParams;
import pl.edu.icm.visnow.geometries.utils.ColorMapper;

/**
 *
 * @author Krzysztof S. Nowinski
 *   <p>   University of Warsaw, ICM
 */
public class RegularField1DGeometry extends RegularFieldGeometry
{
   protected LineStripArray polyline = null;
   protected OpenShape3D lineShape = new OpenShape3D();

   public RegularField1DGeometry(String name)
   {
      super(name);
      geometry.removeAllChildren();
      geometry.addChild(lineShape);
   }

   public RegularField1DGeometry(RegularField inField)
   {
      super(inField.getName());
      if (inField == null || inField.getDims() == null ||
          inField.getDims().length != 1)
         return;
      structureChanged = (this.field == null || !inField.isStructureCompatibleWith(this.field));
      dims = inField.getDims();
      nNodes = inField.getNNodes();
      this.field = inField;
      geometry.removeAllChildren();
      geometry.addChild(lineShape);
   }

   public void generateEdges()
   {
      if (dims.length != 1)
         return;
      boolean detach = geometry.postdetach();
      nNodes = dims[0];
      lineStripCounts = new int[]
              {
                 dims[0]
              };
      polyline = new LineStripArray(nNodes, GeometryArray.COORDINATES, lineStripCounts);
      polyline.setCapability(GeometryArray.ALLOW_COORDINATE_READ);
      polyline.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);
      if(detach) geometry.postattach();
   }

   public void generateColoredEdges()
   {
      if (dims.length != 1)
         return;
      boolean detach = geometry.postdetach();
      nNodes = dims[0];
      lineStripCounts = new int[]
              {
                 dims[0]
              };
      polyline = new LineStripArray(nNodes, GeometryArray.COORDINATES | GeometryArray.COLOR_4, lineStripCounts);
      polyline.setCapability(GeometryArray.ALLOW_COORDINATE_READ);
      polyline.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);
      polyline.setCapability(GeometryArray.ALLOW_COLOR_READ);
      polyline.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
      if(detach) geometry.postattach();
   }

   /**
    * Set the value of field
    *
    * @param field new value of field
    */
   @Override
   public boolean setField(RegularField inField)
   {
      if (inField == null || inField.getDims() == null || inField.getDims().length != 1)
         return false;
      newParams = false;
      if (field == null || !inField.isFullyCompatibleWith(field))
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
         transformParams = fieldDisplayParams.getTransformParams();
         fieldDisplayParams.getTransformParams().addChangeListener(new javax.swing.event.ChangeListener()
         {

            public void stateChanged(ChangeEvent evt)
            {
               transformedGeometries.setTransform(transformParams.getTransform());
            }
         });
      }
      transformedGeometries.setTransform(transformParams.getTransform());
      dims = inField.getDims();
      nNodes = inField.getNNodes();
      structureChanged = (field == null || !inField.isStructureCompatibleWith(this.field));
      dataChanged = (field == null || !inField.isDataCompatibleWith(this.field));
      this.field = inField;
      name = inField.getName();
      if (inField.getCoords() != null)
         coords = inField.getCoords();
      else
         coords = inField.getCoordsFromAffine();
      if (structureChanged || dataChanged)
         clearAllGeometry();
      return true;
   }
   
   public void updateCoords(float[] coords)
   {
      this.coords = coords;
      updateCoords(true);
   }

   public void updateCoords()
   {
      updateCoords(true);
   }

   public void updateCoords(boolean force)
   {
      boolean detach = geometry.postdetach(); 
      polyline.setCoordinates(0, coords);
      if(detach) geometry.postattach();
   }

   public void updateColors()
   {
      boolean detach = geometry.postdetach();
      colorMap = ColorMapManager.getInstance().
                 getColorMap1D(dataMappingParams.getColorMap0Params().getMapType());
      colors = ColorMapper.map(field, dataMappingParams, field.getNNodes(), new Color3f(1,1,1), colors);
      if (polyline != null)
         polyline.setColors(0, colors);
      if(detach) geometry.postattach();
   }
   
   public void updateDataMap()
   {
      if (dataMappingParams.getColorMode() == DataMappingParams.COLORMAPPED || 
          dataMappingParams.getColorMode() == DataMappingParams.COLORMAPPED2D ||
          dataMappingParams.getColorMode() == DataMappingParams.COLORED )
         updateColors();
   }
   
   public void updateGeometry(RegularField inField)
   {
      if (!setField(inField))
         return;
      updateGeometry();
   }

   public void updateGeometry()
   {
      boolean detach = geometry.postdetach();
      lineShape.removeAllGeometries();
      switch (dataMappingParams.getColorMode())
      {
         case DataMappingParams.COLORMAPPED:
         case DataMappingParams.RGB:
            generateColoredEdges();
            break;
         case DataMappingParams.UVTEXTURED:
         case DataMappingParams.UNCOLORED:
            generateEdges();
            break;
      }
      lineShape.addGeometry(polyline);
      structureChanged = false;
      updateCoords();
      switch (dataMappingParams.getColorMode())
      {
         case DataMappingParams.COLORMAPPED:
         case DataMappingParams.RGB:
            updateColors();
            break;
         case DataMappingParams.UVTEXTURED:
         case DataMappingParams.UNCOLORED:
            break;
      }
      appearance.getColoringAttributes().setColor(renderingParams.getDiffuseColor());
      appearance.setLineAttributes(renderingParams.getLineAppearance().getLineAttributes());
      lineShape.setAppearance(appearance);
      if(detach) geometry.postattach();
   }

   public OpenBranchGroup getGeometry(RegularField inField)
   {
      updateGeometry(inField);
      return geometry;
   }

   public OpenBranchGroup getGeometry(Field inField)
   {
      if (!(inField instanceof RegularField))
         return null;
      return getGeometry((RegularField) inField);
   }

   @Override
   public void createGeometry(Field inField)
   {
      if (inField instanceof RegularField)
         updateGeometry((RegularField) inField);
   }

   public void updateGeometry(Field inField)
   {
      if (inField instanceof RegularField)
         updateGeometry((RegularField) inField);
   }

   public OpenBranchGroup getGeometry()
   {
      updateGeometry();
      return geometry;
   }
   
   @Override
   public Field getField()
   {
      return field;
   }
   
}
