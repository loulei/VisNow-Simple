//<editor-fold defaultstate="collapsed" desc=" COPYRIGHT AND LICENSE ">
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
exception statement from your version.
*/
//</editor-fold>

package pl.edu.icm.visnow.geometries.objects;

import java.awt.Color;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.IndexedLineStripArray;
import javax.media.j3d.IndexedPointArray;
import javax.media.j3d.LineStripArray;
import javax.vecmath.Color3f;
import org.apache.log4j.Logger;
import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.datasets.RegularField;
import static pl.edu.icm.visnow.geometries.objects.RegularFieldGeometry.resetGeometry;
import pl.edu.icm.visnow.geometries.objects.generics.OpenBranchGroup;
import pl.edu.icm.visnow.geometries.objects.generics.OpenShape3D;
import pl.edu.icm.visnow.geometries.parameters.AbstractRenderingParams;
import pl.edu.icm.visnow.geometries.parameters.DataMappingParams;
import pl.edu.icm.visnow.geometries.parameters.RenderingParams;
import pl.edu.icm.visnow.geometries.utils.ColorMapper;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.render.RenderEvent;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.render.RenderEventListener;

/**
 *
 * @author Krzysztof S. Nowinski
 *   <p>   University of Warsaw, ICM
 */
public class RegularField1DGeometry extends RegularFieldGeometry
{
   private static final Logger LOGGER = Logger.getLogger(RegularField1DGeometry.class);
   protected OpenShape3D lineShape = new OpenShape3D();
   protected OpenShape3D pointShape = new OpenShape3D();
   protected OpenShape3D frameShape = new OpenShape3D();
   protected LineStripArray polyline = null;
   protected boolean[] lastMask = null;

   public RegularField1DGeometry(String name)
   {
      super(name);
      geometries.removeAllChildren();
      geometries.addChild(lineShape);
      geometries.addChild(pointShape);
      geometries.addChild(frameShape);
      renderEventListener = new RenderEventListener()
      {
         @Override
         public void renderExtentChanged(RenderEvent e)
         {
            if (ignoreUpdate)
               return;
            int extent = e.getUpdateExtent();
            int cMode = dataMappingParams.getColorMode();
            if (renderingParams.getDisplayMode() == AbstractRenderingParams.BACKGROUND)
               cMode = DataMappingParams.UNCOLORED;
            if (currentColorMode < 0)
            {
               currentColorMode = cMode;
               updateShapes();
               return;
            }
            if (extent == RenderEvent.COLORS || extent == RenderEvent.TRANSPARENCY || extent == RenderEvent.TEXTURE)
            {
               validateColorMode();
               if (resetGeometry[currentColorMode][cMode])
                  updateShapes();
               else
                  updateColors();
               currentColorMode = cMode;
               return;
            }
            if (extent == RenderEvent.COORDS)
               updateCoords();
            if (extent == RenderEvent.GEOMETRY)
               updateShapes();
            currentColorMode = cMode;
         }
      };
    }

   private void setStandardCapabilities(GeometryArray arr)
   {
      arr.setCapability(GeometryArray.ALLOW_REF_DATA_READ);
      arr.setCapability(GeometryArray.ALLOW_REF_DATA_WRITE);
      arr.setCapability(GeometryArray.ALLOW_COORDINATE_READ);
      arr.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);
      arr.setCapability(GeometryArray.ALLOW_COLOR_READ);
      arr.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
      arr.setCapability(GeometryArray.ALLOW_NORMAL_READ);
      arr.setCapability(GeometryArray.ALLOW_NORMAL_WRITE);
      arr.setCapability(GeometryArray.ALLOW_TEXCOORD_READ);
      arr.setCapability(GeometryArray.ALLOW_TEXCOORD_WRITE);
   }

   @Override
   public boolean setField(RegularField inField)
   {
      if (inField == null || inField.getDims() == null || inField.getDims().length != 1)
         return false;
      if (inField.getCoords() != null)
      {
         coordsChanged = this.field == null || inField.getCoordsHash() != this.field.getCoordsHash();
         coords = inField.getCoords();
      } else
      {
         coords = inField.getCoordsFromAffine();
         coordsChanged = true;
      }
      super.setField(inField);
      normals = inField.getNormals();
      return true;
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
      polyline = new LineStripArray(nNodes, GeometryArray.COORDINATES | GeometryArray.BY_REFERENCE, lineStripCounts);
      setStandardCapabilities(polyline);
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
      polyline = new LineStripArray(nNodes, GeometryArray.COORDINATES | GeometryArray.COLOR_4 | GeometryArray.BY_REFERENCE, lineStripCounts);
       setStandardCapabilities(polyline);
      if(detach) geometry.postattach();
   }

    private void generateNodeIndices() {
        if (field.getMask() == null) {
            nNodePoints = dims[0];
            coordIndices = new int[nNodePoints];
            int k = 0;
            for (int j = 0; j < dims[0]; j++, k++) {
                coordIndices[k] = j;
            }
        } else {
            boolean[] mask = field.getMask();
            nNodePoints = 0;
            for (int i = 0; i < mask.length; i++) {
                if (mask[i])
                    nNodePoints++;
            }
            coordIndices = new int[nNodePoints];
            int m = 0;
            for (int j = 0; j < dims[0]; j++) {
                if (mask[j])
                    coordIndices[m++] = j;
            }
        }
    }
   
   public void generateColoredNodes()
   {
      if (dims.length != 1)
         return;
      boolean detach = geometry.postdetach();
      generateNodeIndices();
      if (nNodePoints == 0)
         return;
      nodeArr = new IndexedPointArray(nNodes,
              GeometryArray.COORDINATES |
              GeometryArray.COLOR_4 |
              GeometryArray.USE_COORD_INDEX_ONLY |
              GeometryArray.BY_REFERENCE,
              nNodePoints);
      setStandardCapabilities(nodeArr);
      nodeArr.setCoordinateIndices(0, coordIndices);
      if(detach) geometry.postattach();
   }

   public void generateNodes()
   {
      if (dims.length != 1)
         return;
      boolean detach = geometry.postdetach();
      generateNodeIndices();
      if (nNodePoints == 0)
         return;
      nodeArr = new IndexedPointArray(nNodes,
              GeometryArray.COORDINATES |
              GeometryArray.USE_COORD_INDEX_ONLY |
              GeometryArray.BY_REFERENCE,
              nNodePoints);
      setStandardCapabilities(nodeArr);
      nodeArr.setCoordinateIndices(0, coordIndices);
      if(detach) geometry.postattach();
   }

   @Override
   public void updateCoords()
   {
      updateCoords(!ignoreUpdate);
   }

   @Override
   public void updateCoords(boolean force)
   {
      if (!force || field == null)
         return;
      boolean detach = geometry.postdetach();
      coords = field.getCoords();
      if (coords == null)
         coords = field.updateCoordsFromAffine();
      if (polyline != null)
         polyline.setCoordRefFloat(coords);
      if (nodeArr != null)
         nodeArr.setCoordRefFloat(coords);
      if(detach) geometry.postattach();
   }

   @Override
   public void updateCoords(float[] newCoords)
   {
      if (field == null || field.getDims() == null || newCoords.length != 3*field.getNNodes())
      {
         System.out.println("bad new coords");
         return;
      }
      boolean detach = geometry.postdetach();
      coords = newCoords;
      if (polyline != null)
         polyline.setCoordRefFloat(coords);
      if (nodeArr != null)
         nodeArr.setCoordRefFloat(coords);
      if(detach) geometry.postattach();
   }

   public void updateColors()
   {
      boolean detach = geometry.postdetach();
      if (colors == null || colors.length != 4 * field.getNNodes())
         colors = new byte[4 * field.getNNodes()];
      ColorMapper.map(field, dataMappingParams, renderingParams.getDiffuseColor(), colors);
      ColorMapper.mapTransparency(field, dataMappingParams.getTransparencyParams(), colors);      
      if (polyline != null)
         polyline.setColorRefByte(colors);
      if(nodeArr != null)
          nodeArr.setColorRefByte(colors);
      if(detach) geometry.postattach();
   }

   @Override
   public void updateDataMap()
   {
      boolean detach = geometry.postdetach();
      if (dataMappingParams.getColorMode() == DataMappingParams.COLORMAPPED ||
          dataMappingParams.getColorMode() == DataMappingParams.COLORMAPPED2D ||
          dataMappingParams.getColorMode() == DataMappingParams.COLORED)
         updateColors();
      if(detach) geometry.postattach();
   }

   public void updateShapes()
   {
      updateGeometry((renderingParams.getDisplayMode() & RenderingParams.EDGES) != 0,
                     (renderingParams.getDisplayMode() & RenderingParams.NODES) != 0,
                     (renderingParams.getDisplayMode() & RenderingParams.IMAGE) != 0,
                     (renderingParams.getDisplayMode() & RenderingParams.OUTLINE_BOX) != 0
              );
      if(geometries.getParent() == null)
            transformedGeometries.addChild(geometries);
      if(transformedGeometries.getParent() == null)
            geometry.addChild(transformedGeometries);
   }

   public void updateData()
   {
      boolean restructure = false;
      boolean[] mask = field.getMask();
      if (mask != null)
         if (lastMask == null || lastMask.length != mask.length)
         {
            restructure = true;
            lastMask = mask;
         }
      else
         {
            for (int i = 0; i < mask.length; i++)
               if (mask[i] != lastMask[i])
               {
                  restructure = true;
                  break;
               }
         }
      updateExtents();
      if (restructure)
         updateShapes();
      else
      {
         updateColors();
         updateCoords();
      }      
   }

   public void updateGeometry(RegularField inField, boolean showEdges, boolean showNodes, boolean showImage, boolean showBox)
   {
      if (field != inField && !setField(inField))
         return;
      updateGeometry(showEdges, showNodes, showImage, showBox);
   }

   public void updateGeometry(boolean showEdges, boolean showNodes, boolean showImage, boolean showBox)
   {
      boolean detach = geometry.postdetach();
      if (lineShape != null)
         lineShape.removeAllGeometries();
      if (pointShape != null)
         pointShape.removeAllGeometries();
      if (frameShape != null)
         frameShape.removeAllGeometries();
      polyline = null;
      nodeArr = null;
      boxArr = null;

      if (showEdges)
      {
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
      }
      if (showNodes)
      {
         switch (dataMappingParams.getColorMode())
         {
         case DataMappingParams.COLORMAPPED:
         case DataMappingParams.RGB:
            generateColoredNodes();
            break;
         case DataMappingParams.UVTEXTURED:
         case DataMappingParams.UNCOLORED:
            generateNodes();
            break;
         }
         pointShape.addGeometry(nodeArr);
      }
      if(showBox) {
          //updateExtents();
        float[][] ext = field.getExtents();
        float[] boxVerts = new float[24];
        boxArr = new IndexedLineStripArray(8, GeometryArray.COORDINATES, 24, new int[]
                {
                   2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2
                });
        boxArr.setCoordinateIndices(0, new int[]
                {
                   0, 1, 2, 3, 4, 5, 6, 7, 0, 2, 1, 3, 4, 6, 5, 7, 0, 4, 1, 5, 2, 6, 3, 7
                });
        for (int i = 0; i < 2; i++)
           for (int j = 0; j < 2; j++)
              for (int k = 0; k < 2; k++)
              {
                 int m = 3 * (4 * i + 2 * j + k);
//                 boxVerts[m] = (extents[k][0]==Float.MAX_VALUE || extents[k][0] == -Float.MAX_VALUE)?ownExtents[k][0]:extents[k][0];
//                 boxVerts[m + 1] = (extents[j][1]==Float.MAX_VALUE || extents[j][1] == -Float.MAX_VALUE)?ownExtents[j][1]:extents[j][1];
//                 boxVerts[m + 2] = (extents[i][2]==Float.MAX_VALUE || extents[i][2] == -Float.MAX_VALUE)?ownExtents[i][2]:extents[i][2];
                 boxVerts[m] = ext[k][0];
                 boxVerts[m + 1] = ext[j][1];
                 boxVerts[m + 2] = ext[i][2];
              }
         boxArr.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);
         boxArr.setCoordinates(0, boxVerts);
         frameShape.addGeometry(boxArr);          
      }
      
      structureChanged = false;
      updateCoords();
      switch (dataMappingParams.getColorMode())
      {
            case DataMappingParams.COLORMAPPED:
            case DataMappingParams.RGB:
               updateColors();
               break;
            case DataMappingParams.UVTEXTURED:
               break;
            case DataMappingParams.UNCOLORED:
               break;
      }
      appearance = renderingParams.getAppearance();
      appearance.setUserData(this);
      lineAppearance = renderingParams.getLineAppearance();
      lineAppearance.setUserData(this);
      if (appearance.getMaterial() != null)
      {
         appearance.getMaterial().setAmbientColor(renderingParams.getAmbientColor());
         appearance.getMaterial().setDiffuseColor(renderingParams.getDiffuseColor());
         appearance.getMaterial().setSpecularColor(renderingParams.getSpecularColor());
      }

      Color bgrColor = renderingParams.getBackgroundColor();
      float[] bgrColorComps = new float[3];
      bgrColor.getColorComponents(bgrColorComps);
      if (renderingParams.getShadingMode() == AbstractRenderingParams.BACKGROUND)
      {
         appearance.getColoringAttributes().setColor(new Color3f(bgrColorComps[0], bgrColorComps[1], bgrColorComps[2]));
      }
      else
         appearance.getColoringAttributes().setColor(renderingParams.getDiffuseColor());
      lineAppearance.getColoringAttributes().setColor(renderingParams.getDiffuseColor());
      lineAppearance.setLineAttributes(renderingParams.getLineAppearance().getLineAttributes());
      if (dataMappingParams.getColorMode() != DataMappingParams.UVTEXTURED)
         updateColors();
      pointShape.setAppearance(lineAppearance);
      lineShape.setAppearance(lineAppearance);
      frameShape.setAppearance(lineAppearance);
      if(detach) geometry.postattach();
   }

   public OpenBranchGroup getGeometry(RegularField inField)
   {
      updateGeometry(inField);
      return geometry;
   }

   @Override
   public OpenBranchGroup getGeometry(Field inField)
   {
      if (!(inField instanceof RegularField))
         return null;
      field = (RegularField) inField;
      return getGeometry((RegularField) inField);
   }

   @Override
   public void createGeometry(Field inField)
   {
      if (!(inField instanceof RegularField))
         return;
      updateGeometry((RegularField) inField);
   }

   public void updateGeometry(RegularField inField)
   {
      setField(inField);
      updateShapes();
   }

   @Override
   public void updateGeometry(Field inField)
   {
      if (inField instanceof RegularField)
         updateGeometry((RegularField) inField);
   }



   private void validateColorMode()
   {
      colorMode = dataMappingParams.getColorMode();
// check if color mode and selected components combination is valid; fall back to UNCOLORED otherwise
      switch (dataMappingParams.getColorMode())
      {
      case DataMappingParams.COLORMAPPED:
         if (field.getData(dataMappingParams.getColorMap0Params().getDataComponent()) != null)
            return;
         break;
      case DataMappingParams.RGB:
         if (field.getData(dataMappingParams.getRedParams().getDataComponent())   != null ||
             field.getData(dataMappingParams.getGreenParams().getDataComponent()) != null ||
             field.getData(dataMappingParams.getBlueParams().getDataComponent()) != null)
            return;
         break;
      case DataMappingParams.UVTEXTURED:
         break;
      }
      colorMode = DataMappingParams.UNCOLORED;
   }

    @Override
   public void updateGeometry()
   {
      if (ignoreUpdate)
         return;
      dataMappingParams.setParentObjectSize(nNodes);
      updateShapes();
   }
}
