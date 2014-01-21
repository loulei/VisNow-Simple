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
exception statement from your version. */
//</editor-fold>


package pl.edu.icm.visnow.geometries.objects;

import java.awt.Color;
import javax.media.j3d.*;
import javax.vecmath.Color3f;
import org.apache.log4j.Logger;
import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.geometries.events.ColorEvent;
import pl.edu.icm.visnow.geometries.events.ColorListener;
import pl.edu.icm.visnow.geometries.objects.generics.OpenBranchGroup;
import pl.edu.icm.visnow.geometries.objects.generics.OpenShape3D;
import pl.edu.icm.visnow.geometries.parameters.AbstractRenderingParams;
import pl.edu.icm.visnow.geometries.parameters.ColorComponentParams;
import pl.edu.icm.visnow.geometries.parameters.DataMappingParams;
import pl.edu.icm.visnow.geometries.parameters.RenderingParams;
import pl.edu.icm.visnow.geometries.utils.ColorMapper;
import pl.edu.icm.visnow.geometries.utils.TextureMapper;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.render.RenderEvent;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.render.RenderEventListener;
import pl.edu.icm.visnow.lib.utils.geometry2D.*;

/**
 *
 * @author Krzysztof S. Nowinski
 *   <p>   University of Warsaw, ICM
 */
public class RegularField2DGeometry extends RegularFieldGeometry
{
   private static final Logger LOGGER = Logger.getLogger(RegularField2DGeometry.class);
   protected OpenShape3D surfaceShape = new OpenShape3D();
   protected OpenShape3D lineShape = new OpenShape3D();
   protected OpenShape3D pointShape = new OpenShape3D();
   protected OpenShape3D frameShape = new OpenShape3D();
   protected boolean lastSurfaceOrientation = true;
   protected boolean surfaceOrientation = true;
   protected boolean[] lastMask = null;
   protected int[] timeRange = null;
   protected float currentT = 0;
   protected Color3f bgrColor = new Color3f(0, 0, 0);

   public RegularField2DGeometry(String name)
   {
      super(name);
      geometries.removeAllChildren();
      geometries.addChild(surfaceShape);
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
            if (extent == RenderEvent.COLORS || extent == RenderEvent.TRANSPARENCY |  extent == RenderEvent.TEXTURE)
            {
               validateColorMode();
               if (resetGeometry[currentColorMode][cMode])
                  updateShapes();
               else if (colorMode == DataMappingParams.UVTEXTURED)
                  updateTextureCoords();
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

   private void setIndexingCapabilities(IndexedGeometryArray arr)
   {
      setStandardCapabilities(arr);
      arr.setCapability(IndexedGeometryArray.ALLOW_COLOR_INDEX_READ);
      arr.setCapability(IndexedGeometryArray.ALLOW_COLOR_INDEX_WRITE);
      arr.setCapability(IndexedGeometryArray.ALLOW_COORDINATE_INDEX_READ);
      arr.setCapability(IndexedGeometryArray.ALLOW_COORDINATE_INDEX_WRITE);
      arr.setCapability(IndexedGeometryArray.ALLOW_NORMAL_INDEX_READ);
      arr.setCapability(IndexedGeometryArray.ALLOW_NORMAL_INDEX_WRITE);
   }

   /**
    * Set the value of field
    *
    * @param inField new value of field
    */
   @Override
   public boolean setField(RegularField inField)
   {
      if (inField == null || inField.getDims() == null || inField.getDims().length != 2)
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

   public void setSurfaceOrientation(boolean surfaceOrientation)
   {
      this.surfaceOrientation = surfaceOrientation;
   }

    private void generateTriangleIndices()
   {
      if (field.getMask() == null || renderingParams.ignoreMask())
      {
         nTriangleStrips = dims[1] - 1;
         triangleStripCounts = new int[nTriangleStrips];
         for (int i = 0; i < nTriangleStrips; i++)
            triangleStripCounts[i] = 2 * dims[0];
         nTriangleIndices = 2 * dims[0] * (dims[1] - 1);
         coordIndices = new int[nTriangleIndices];
         if (renderingParams.getSurfaceOrientation() == surfaceOrientation)
            for (int i = 0, k = 0; i < dims[1] - 1; i++)
               for (int j = 0; j < dims[0]; j++, k += 2)
               {
                  coordIndices[k] = i * dims[0] + j;
                  coordIndices[k + 1] = (i + 1) * dims[0] + j;
               }
         else
         {
            for (int i = 0, k = 0; i < dims[1] - 1; i++)
               for (int j = 0; j < dims[0]; j++, k += 2)
               {
                  coordIndices[k] =  (i + 1) * dims[0] + j;
                  coordIndices[k + 1] = i * dims[0] + j;
               }
         }
      }
      else
      {
         int[] ind = new int[2 * dims[0] * dims[1]];
         int[] stripInd = new int[2 * dims[0]];
         int cl = 0;
         nTriangleStrips = nTriangleIndices = 0;
         boolean[] mask = field.getMask();
         
         for (int i = 0; i < dims[1] - 1; i++)
         {
            if (renderingParams.getSurfaceOrientation() == surfaceOrientation)
               for (int j = 0; j < dims[0]; j++)
               {
                  stripInd[2 * j] = i * dims[0] + j;
                  stripInd[2 * j + 1] = (i + 1) * dims[0] + j;
               }
            else
               for (int j = 0; j < dims[0]; j++)
               {
                  stripInd[2 * j] = (i + 1) * dims[0] + j;
                  stripInd[2 * j + 1] = i * dims[0] + j;
               }
            
            boolean fakeFirstTriangle = false;
            for (int j = 0; j < stripInd.length; j++)
            {
               int l = stripInd[j];
               if (mask[l])
               {
                  if (cl == 0 && j % 2 == 1)
                  {
                     ind[nTriangleIndices + cl] = l;
                     cl += 1;
                     fakeFirstTriangle = true;
                  }
                  ind[nTriangleIndices + cl] = l;
                  cl += 1;
               }
               else
               {
                  if (cl < 3 || cl == 3 && fakeFirstTriangle)
                     cl = 0;
                  else
                  {
                     nTriangleStrips += 1;
                     ind[ind.length - nTriangleStrips] = cl;
                     nTriangleIndices += cl;
                     cl = 0;
                  }
                  fakeFirstTriangle = false;
               }
            }
            if (cl < 3 || cl == 3 && fakeFirstTriangle)
               cl = 0;
            else
            {
               nTriangleStrips += 1;
               ind[ind.length - nTriangleStrips] = cl;
               nTriangleIndices += cl;
               cl = 0;
            }
         }
         if (nTriangleStrips == 0)
            return;
         triangleStripCounts = new int[nTriangleStrips];
         for (int i = 0; i < nTriangleStrips; i++)
            triangleStripCounts[i] = ind[ind.length - i - 1];
         coordIndices = new int[nTriangleIndices];
         System.arraycopy(ind, 0, coordIndices, 0, nTriangleIndices);
      }
   }

   public void generateTriangles()
   {
      if (dims.length != 2)
         return;
      boolean detach = geometry.postdetach();

      generateTriangleIndices();
      if (nTriangleStrips == 0)
         return;
      if (renderingParams.getDisplayMode() == RenderingParams.BACKGROUND || renderingParams.getDisplayMode() == RenderingParams.UNSHADED)
         triangleArr = new IndexedTriangleStripArray(nNodes,
              GeometryArray.COORDINATES |
                 GeometryArray.USE_COORD_INDEX_ONLY |
                 GeometryArray.BY_REFERENCE,
              nTriangleIndices, triangleStripCounts);
      else
         triangleArr = new IndexedTriangleStripArray(nNodes,
                 GeometryArray.COORDINATES |
                 GeometryArray.NORMALS |
                 GeometryArray.USE_COORD_INDEX_ONLY |
                 GeometryArray.BY_REFERENCE,
                 nTriangleIndices, triangleStripCounts);
      setStandardCapabilities(triangleArr);
      triangleArr.setCoordinateIndices(0, coordIndices);
      if(detach) geometry.postattach();
   }

   public void generateColoredTriangles()
   {
      if (dims.length != 2)
         return;
      boolean detach = geometry.postdetach();
      generateTriangleIndices();
      if (nTriangleStrips == 0)
         return;
      triangleArr = new IndexedTriangleStripArray(nNodes,
              GeometryArray.COORDINATES |
              GeometryArray.NORMALS |
              GeometryArray.COLOR_4 |
              GeometryArray.USE_COORD_INDEX_ONLY |
              GeometryArray.BY_REFERENCE,
              nTriangleIndices, triangleStripCounts);
      setStandardCapabilities(triangleArr);
      triangleArr.setCoordinateIndices(0, coordIndices);
      if(detach) geometry.postattach();
   }

   public void generateTexturedTriangles()
   {
      if (dims.length != 2)
         return;
      boolean detach = geometry.postdetach();
      generateTriangleIndices();
      if (nTriangleStrips == 0)
         return;
      triangleArr = new IndexedTriangleStripArray(nNodes,
              GeometryArray.COORDINATES |
              GeometryArray.NORMALS |
              GeometryArray.TEXTURE_COORDINATE_2 |
              GeometryArray.USE_COORD_INDEX_ONLY |
              GeometryArray.BY_REFERENCE,
              nTriangleIndices, triangleStripCounts);
      setStandardCapabilities(triangleArr);
      triangleArr.setCoordinateIndices(0, coordIndices);
      if(detach) geometry.postattach();
   }

   private void generateEdgeIndices()
   {
      if (field.getMask() == null)
      {
         nLineStrips = dims[1] + dims[0];
         lineStripCounts = new int[nLineStrips];
         for (int i = 0; i < dims[1]; i++)
            lineStripCounts[i] = dims[0];
         for (int i = dims[1]; i < dims[1] + dims[0]; i++)
            lineStripCounts[i] = dims[1];
         nLineIndices = 2 * dims[0] * dims[1];
         coordIndices = new int[nLineIndices];
         int k = 0;
         for (int i = 0; i < dims[1]; i++)
            for (int j = 0; j < dims[0]; j++, k++)
               coordIndices[k] = i * dims[0] + j;
         for (int i = 0; i < dims[0]; i++)
            for (int j = 0; j < dims[1]; j++, k++)
               coordIndices[k] = j * dims[0] + i;
      }
      else
      {
         int[] ind = new int[2 * dims[0] * dims[1] +  dims[1] + dims[0]];
         int cl = 0;
         nLineStrips = nLineIndices = 0;
         boolean[] mask = field.getMask();
            for (int i = 0; i < dims[1]; i++)
            {
               for (int j = 0; j < dims[0]; j++)
               {
                  int l = i * dims[0] + j;
                  if (mask[l])
                  {
                     ind[nLineIndices + cl] = l;
                     cl += 1;
                  }
                  else
                  {
                     if (cl < 2)
                        cl = 0;
                     else
                     {
                        nLineStrips += 1;
                        ind[ind.length - nLineStrips] = cl;
                        nLineIndices += cl;
                        cl = 0;
                     }
                  }
               }
               if (cl < 2)
                  cl = 0;
               else
               {
                  nLineStrips += 1;
                  ind[ind.length - nLineStrips] = cl;
                  nLineIndices += cl;
                  cl = 0;
               }
            }
            for (int j = 0; j < dims[0]; j++)
            {
               for (int i = 0; i < dims[1]; i++)
               {
                  int l = i * dims[0] + j;
                  if (mask[l])
                  {
                     ind[nLineIndices + cl] = l;
                     cl += 1;
                  }
                  else
                  {
                     if (cl < 2)
                        cl = 0;
                     else
                     {
                        nLineStrips += 1;
                        ind[ind.length - nLineStrips] = cl;
                        nLineIndices += cl;
                        cl = 0;
                     }
                  }
               }
               if (cl < 2)
                  cl = 0;
               else
               {
                  nLineStrips += 1;
                  ind[ind.length - nLineStrips] = cl;
                  nLineIndices += cl;
                  cl = 0;
               }
            }
         if (nLineStrips == 0)
            return;
         lineStripCounts = new int[nLineStrips];
         for (int i = 0; i < nLineStrips; i++)
            lineStripCounts[i] = ind[ind.length - i - 1];
         coordIndices = new int[nLineIndices];
         System.arraycopy(ind, 0, coordIndices, 0, nLineIndices);
      }
   }

   public void generateEdges()
   {
      if (dims.length != 2)
         return;
      boolean detach = geometry.postdetach();
      generateEdgeIndices();
      if (nLineStrips == 0)
         return;
      edgeArr = new IndexedLineStripArray(nNodes,
              GeometryArray.COORDINATES |
              GeometryArray.USE_COORD_INDEX_ONLY |
              GeometryArray.BY_REFERENCE,
              nLineIndices, lineStripCounts);
      setStandardCapabilities(edgeArr);
      edgeArr.setCoordinateIndices(0, coordIndices);
      if(detach) geometry.postattach();
   }

   public void generateColoredEdges()
   {
      if (dims.length != 2)
         return;
      boolean detach = geometry.postdetach();
      generateEdgeIndices();
      if (nLineStrips == 0)
         return;
      if (renderingParams.isLineLighting())
         edgeArr = new IndexedLineStripArray(nNodes,
                 GeometryArray.COORDINATES |
                 GeometryArray.NORMALS |
                 GeometryArray.COLOR_4 |
                 GeometryArray.USE_COORD_INDEX_ONLY |
                 GeometryArray.BY_REFERENCE,
                 nLineIndices, lineStripCounts);
      else
         edgeArr = new IndexedLineStripArray(nNodes,
                 GeometryArray.COORDINATES |
                 GeometryArray.COLOR_4 |
                 GeometryArray.USE_COORD_INDEX_ONLY |
                 GeometryArray.BY_REFERENCE,
                 nLineIndices, lineStripCounts);
      setStandardCapabilities(edgeArr);
      edgeArr.setCoordinateIndices(0, coordIndices);
      if(detach) geometry.postattach();
   }

   private void generateNodeIndices()
   {
      if (field.getMask() == null)
      {
         nNodePoints = dims[1] * dims[0];
         coordIndices = new int[nNodePoints];
         int k = 0;
         for (int i = 0; i < dims[1]; i++)
            for (int j = 0; j < dims[0]; j++, k++)
               coordIndices[k] = i * dims[0] + j;
      }
      else
      {
         boolean[] mask = field.getMask();
         nNodePoints = 0;
         for (int i = 0; i < mask.length; i++) {
              if(mask[i])
                  nNodePoints++;
         }
         coordIndices = new int[nNodePoints];
         int k = 0;
         int m = 0;
         for (int i = 0; i < dims[1]; i++)
            for (int j = 0; j < dims[0]; j++, k++)
               if(mask[k])
                   coordIndices[m++] = i * dims[0] + j;
      }
   }


   public void generateColoredNodes()
   {
      if (dims.length != 2)
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
      if (dims.length != 2)
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
      normals = field.getNormals();
      if (renderingParams.getSurfaceOrientation() != lastSurfaceOrientation)
         for (int i = 0; i < normals.length; i++)
            normals[i] = -normals[i];
      lastSurfaceOrientation = renderingParams.getSurfaceOrientation();
      if (triangleArr != null)
      {
         triangleArr.setCoordRefFloat(coords);
         if ((triangleArr.getVertexFormat() & GeometryArray.NORMALS) != 0)
            triangleArr.setNormalRefFloat(normals);
      }
      if (edgeArr != null)
         edgeArr.setCoordRefFloat(coords);
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
      if (normals == null)
         normals = field.getNormals();
      if (renderingParams.getSurfaceOrientation() != lastSurfaceOrientation)
         for (int i = 0; i < normals.length; i++)
            normals[i] = -normals[i];
      lastSurfaceOrientation = renderingParams.getSurfaceOrientation();
      if (triangleArr != null)
      {
         triangleArr.setCoordRefFloat(coords);
         triangleArr.setNormalRefFloat(normals);
      }
      if (edgeArr != null)
         edgeArr.setCoordRefFloat(coords);
      if (nodeArr != null)
         nodeArr.setCoordRefFloat(coords);
      if(detach) geometry.postattach();
   }

   private void updateTextureCoords()
   {
      boolean detach = geometry.postdetach();
      int nSpace = field.getNSpace();
      uvData = new float[2 * nNodes];
      ColorComponentParams[] tParams = new ColorComponentParams[]
              {dataMappingParams.getUParams(), dataMappingParams.getVParams()};
      for (int i = 0; i < tParams.length; i++)
      {
         if (tParams[i].getDataComponent() >= 0)
            uvData = TextureMapper.map(field.getData(tParams[i].getDataComponent()),
                                       tParams[i], uvData, i);
         else if (tParams[i].getDataComponent() == DataMappingParams.COORDX ||
                  tParams[i].getDataComponent() == DataMappingParams.COORDY ||
                  tParams[i].getDataComponent() == DataMappingParams.COORDZ)
            uvData = TextureMapper.map(coords, nSpace, extents, tParams[i], uvData, i, .01f);
         else if ( tParams[i].getDataComponent() == DataMappingParams.NORMALX ||
                   tParams[i].getDataComponent() == DataMappingParams.NORMALY ||
                   tParams[i].getDataComponent() == DataMappingParams.NORMALZ)
            uvData = TextureMapper.map(normals, nSpace,
                                       new float[][] {{-1,-1,-1},{1,1,1}}, tParams[i], uvData, i, .01f);
         else if (tParams[i].getDataComponent() == DataMappingParams.INDEXI ||
                  tParams[i].getDataComponent() == DataMappingParams.INDEXJ)
            uvData = TextureMapper.map(dims, tParams[i], uvData, i, .01f);
      }
      if (triangleArr != null)
         triangleArr.setTexCoordRefFloat(0, uvData);
      appearance.setTexture(dataMappingParams.getTexture());
      if(detach) geometry.postattach();
   }

   private class MapColors implements Runnable
   {
      int nThreads      = 1;
      int iThread       = 0;
      int nNds = field.getNNodes();
      public MapColors(int nThreads, int iThread)
      {
         this.nThreads       = nThreads;
         this.iThread        = iThread;
      }

      @Override
      public void run()
      {
         int kstart = nNds * iThread / nThreads;
         int kend = nNds * (iThread+1) / nThreads;
         //map from kstart to kend (excluding kend)
         if(iThread == 0) {
             int a = 1;
         }
         ColorMapper.map(field, dataMappingParams, kstart, kend, kstart, renderingParams.getDiffuseColor(), colors);
         ColorMapper.mapTransparency(field, dataMappingParams.getTransparencyParams(), kstart, kend, colors);
         if (timeRange != null)
            ColorMapper.mapTimeValidityTransparency(timeRange, currentT, kstart, kend, kstart, colors);
      }
   }

   public void updateColors()
   {
      boolean detach = geometry.postdetach();
      if (colors == null || colors.length != 4 * field.getNNodes())
         colors = new byte[4 * field.getNNodes()];
      timeRange = null;
      for (DataArray da : field.getData())
         if (da.getUserData() != null &&
             da.getUserData().length == 1 &&
             "valid time range".equalsIgnoreCase(da.getUserData()[0]))
         {
            timeRange = da.getIData();
            currentT  = field.getCurrentTime();
         }
      int nThreads = pl.edu.icm.visnow.system.main.VisNow.availableProcessors();
      Thread[] workThreads = new Thread[nThreads];
      for (int iThread = 0; iThread < nThreads; iThread++)
      {
         workThreads[iThread] = new Thread(new MapColors(nThreads, iThread));
         workThreads[iThread].start();
      }
      for (int i = 0; i < workThreads.length; i++)
         try
         {
             workThreads[i].join();
         } catch (Exception e)
         {

         }
      //colors = ColorMapper.map(field, dataMappingParams, field.getNNodes(), renderingParams.getAmbientColor(), colors);
      if (triangleArr != null && (triangleArr.getVertexFormat() & GeometryArray.COLOR_4) != 0)
         triangleArr.setColorRefByte(colors);
      if (edgeArr != null)
         edgeArr.setColorRefByte(colors);
      if(nodeArr != null)
          nodeArr.setColorRefByte(colors);

      if(image2D != null) {
          image2D.setColors(colors);
      }
      if(edges2D != null) {
          edges2D.setColors(colors);
      }
      if(points2D != null) {
          points2D.setColors(colors);
      }
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
      else if (dataMappingParams.getColorMode() == DataMappingParams.UVTEXTURED)
         updateTextureCoords();
      if(detach) geometry.postattach();
   }

   public void updateShapes()
   {
      updateGeometry((renderingParams.getDisplayMode() & RenderingParams.SURFACE) != 0,
                     (renderingParams.getDisplayMode() & RenderingParams.EDGES) != 0,
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

   public void updateGeometry(RegularField inField, 
                              boolean showSurface, boolean showEdges, boolean showNodes, 
                              boolean showImage, boolean showBox)
   {
      if (field != inField && !setField(inField))
         return;
      updateGeometry(showSurface, showEdges, showNodes, showImage, showBox);
   }

   public void updateGeometry(boolean showSurface, boolean showEdges, boolean showNodes, 
                              boolean showImage, boolean showBox)
   {
      boolean detach = geometry.postdetach();
      if (surfaceShape != null)
         surfaceShape.removeAllGeometries();
      if (lineShape != null)
         lineShape.removeAllGeometries();
      if (pointShape != null)
         pointShape.removeAllGeometries();
      if (frameShape != null)
         frameShape.removeAllGeometries();
      triangleArr = null;
      edgeArr = null;
      nodeArr = null;
      boxArr = null;

      outObj2DStruct.removeAllChildren();
      outObj2DStruct.setName("regular field 2d geometry");
      //cannot be set to null because render event listener has to be removed first!
//      image2D = null;
      edges2D = null;
      points2D = null;

      if (showSurface)
      {
         int cMode = dataMappingParams.getColorMode();
         if (renderingParams.getShadingMode() == RenderingParams.BACKGROUND)
            cMode = DataMappingParams.UNCOLORED;
         switch (cMode)
         {
         case DataMappingParams.COLORMAPPED:
         case DataMappingParams.RGB:
            generateColoredTriangles();
            appearance.setTexture(null);
            break;
         case DataMappingParams.UVTEXTURED:
            generateTexturedTriangles();
            appearance.setTexture(dataMappingParams.getTexture());
            break;
         case DataMappingParams.UNCOLORED:
            generateTriangles();
            appearance.setTexture(null);
            break;
         }
         surfaceShape.addGeometry(triangleArr);
      }
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
         lineShape.addGeometry(edgeArr);
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
               updateTextureCoords();
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
      if(showImage) {
          createImage2D();
          GeometryObject2DStruct imgStruct = new GeometryObject2DStruct(image2D);
          imgStruct.setName("regular field 2D image");
          outObj2DStruct.addChild(imgStruct);
      }
      if(showEdges) {
          updateEdges2D();
          GeometryObject2DStruct edgStruct = new GeometryObject2DStruct(edges2D);
          edgStruct.setName("regular field 2D edges");
          outObj2DStruct.addChild(edgStruct);
      }
      if(showNodes) {
          updatePoints2D();
          GeometryObject2DStruct ptsStruct = new GeometryObject2DStruct(points2D);
          ptsStruct.setName("regular field 2D nodes");
          outObj2DStruct.addChild(ptsStruct);
      }
      if(showBox) {
          updateBox2D();
          GeometryObject2DStruct boxStruct = new GeometryObject2DStruct(box2D);
          boxStruct.setName("regular field 2D box");
          outObj2DStruct.addChild(boxStruct);
      }

      bgrColor = new Color3f(renderingParams.getBackgroundColor());
      if (renderingParams.getShadingMode() == renderingParams.BACKGROUND)
      {
         appearance.getColoringAttributes().setColor(bgrColor);
         appearance.getMaterial().setDiffuseColor(bgrColor);
      }
      else
         appearance.getColoringAttributes().setColor(renderingParams.getDiffuseColor());
      lineAppearance.getColoringAttributes().setColor(renderingParams.getDiffuseColor());
      lineAppearance.setLineAttributes(renderingParams.getLineAppearance().getLineAttributes());
      if (dataMappingParams.getColorMode() != DataMappingParams.UVTEXTURED)
         updateColors();
      texture = dataMappingParams.getTexture();
      if (texture != null && dataMappingParams.getColorMode() == DataMappingParams.UVTEXTURED)
         appearance.setTexture(texture);
      pointShape.setAppearance(lineAppearance);
      lineShape.setAppearance(lineAppearance);
      surfaceShape.setAppearance(appearance);
      frameShape.setAppearance(lineAppearance);
      if(detach) geometry.postattach();

   }

   public OpenBranchGroup getGeometry(RegularField inField)
   {
      updateGeometry(inField);
      return geometry;
   }

   public OpenBranchGroup getGeometry(boolean showSurface, boolean showEdges) {
       return getGeometry(showSurface, showEdges, false, false, false);
   }

   public OpenBranchGroup getGeometry(boolean showSurface, boolean showEdges, boolean showNodes, boolean showImage, boolean showBox)
   {
      if (field == null)
         return null;
      updateGeometry(showSurface, showEdges, showNodes, showImage, showBox);
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


   private boolean textureComponentValid(int c)
   {
      if (field.getData(c) != null)
         return true;
      if (c == DataMappingParams.COORDX || c == DataMappingParams.NORMALX ||
          c == DataMappingParams.COORDY || c == DataMappingParams.NORMALY ||
          c == DataMappingParams.COORDZ || c == DataMappingParams.NORMALZ ||
          c == DataMappingParams.INDEXI || c == DataMappingParams.INDEXJ)
         return true;
      return false;
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
         if (textureComponentValid(dataMappingParams.getUParams().getDataComponent()) &&
             textureComponentValid(dataMappingParams.getVParams().getDataComponent()))
            return;
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

    private ImageArray2D image2D = null;
    private LineArray2D edges2D = null;
    private PointArray2D points2D = null;
    private BoxArray2D box2D = null;

    private void createImage2D() {
        if (image2D!=null)
            image2D.clearParamListeners();

        image2D = new ImageArray2D();
        image2D.setRenderingParams(renderingParams);
        
        image2D.setField(field);
    }

    private void updateEdges2D() {
        if(edges2D == null) {
            edges2D = new LineArray2D();
            edges2D.setRenderingParams(renderingParams);
        }
        edges2D.setField(field);
        //System.out.println("geometry "+this.getName()+" updated 2D edges object");
    }

    private void updatePoints2D() {
        if(points2D == null) {
            points2D = new PointArray2D();
            points2D.setRenderingParams(renderingParams);
        }
        points2D.setField(field);
        //System.out.println("geometry "+this.getName()+" updated 2D points object");
    }

    private void updateBox2D() {
        if(box2D == null) {
           box2D = new BoxArray2D();
           box2D.setRenderingParams(renderingParams);
        }
        box2D.setField(field);
        //System.out.println("geometry "+this.getName()+" updated 2D points object");
    }
    
   ColorListener bgrColorListener = new ColorListener()
   {
      @Override
      public void colorChoosen(ColorEvent e)
      {
         bgrColor = new Color3f(e.getSelectedColor());
         if (renderingParams.getShadingMode() == RenderingParams.BACKGROUND)
         {
            renderingParams.setDiffuseColor(bgrColor);
            if (appearance != null)
            {
               if (appearance.getColoringAttributes() != null)
                  appearance.getColoringAttributes().setColor(bgrColor);
               appearance.getMaterial().setAmbientColor(bgrColor);
               appearance.getMaterial().setDiffuseColor(bgrColor);
               appearance.getMaterial().setEmissiveColor(bgrColor);
               appearance.getMaterial().setSpecularColor(0,0,0);
            }
            ignoreUpdate = false;
            updateGeometry();
         }
      }
   };
    
   @Override
   public ColorListener getBackgroundColorListener()
   {
      return bgrColorListener;
   }


}
