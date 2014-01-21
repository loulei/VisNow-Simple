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
package pl.edu.icm.visnow.lib.basic.mappers.Glyphs;

import java.awt.Color;
import javax.media.j3d.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.Color3f;
import pl.edu.icm.visnow.datamaps.ColorMap;
import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.engine.core.InputEgg;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.geometries.geometryTemplates.Glyph;
import pl.edu.icm.visnow.geometries.geometryTemplates.ScalarGlyphTemplates;
import pl.edu.icm.visnow.geometries.geometryTemplates.VectorGlyphTemplates;
import pl.edu.icm.visnow.geometries.objects.generics.*;
import pl.edu.icm.visnow.geometries.parameters.AbstractRenderingParams;
import pl.edu.icm.visnow.geometries.parameters.ColorComponentParams;
import pl.edu.icm.visnow.geometries.parameters.DataMappingParams;
import pl.edu.icm.visnow.geometries.parameters.RenderingParams;
import pl.edu.icm.visnow.geometries.utils.ColorMapper;
import pl.edu.icm.visnow.geometries.utils.TextureMapper;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.render.RenderEvent;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.render.RenderEventListener;
import pl.edu.icm.visnow.lib.templates.visualization.modules.VisualizationModule;
import pl.edu.icm.visnow.lib.types.VNField;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;

/**
 * @author Krzysztof S. Nowinski (know@icm.edu.pl) Warsaw University, Interdisciplinary Centre for
 * Mathematical and Computational Modelling
 */
public class Glyphs extends VisualizationModule
{

   /**
    * Creates a new instance of CreateGrid
    */
   public static InputEgg[] inputEggs = null;
   public static OutputEgg[] outputEggs = null;
   protected GlyphsGUI ui = null;
   protected GlyphsParams params;
   protected Field inField = null;
   protected float lowv = 0;
   protected float dv = 0;
   protected OpenBranchGroup outGroup = new OpenBranchGroup();
   protected boolean isValidity = false;
   protected boolean[] valid = null;
   protected DataArray glyphDataArray, thresholdDataArray;
   protected boolean isThrRelative = false;
   protected float thr = -Float.MAX_VALUE;
   protected float[] baseCoords = null;
   protected float[] baseU = null;
   protected float[] baseV = null;
   protected float[] baseW = null;
   protected int nGlyphs, nstrip, nvert, nind, ncol;
   protected boolean isNormals = false;
   protected int[] glyphIn = null;
   protected int[] cIndex = null;
   protected int[] pIndex = null;
   protected int[] strips = null;
   protected float[] verts = null;
   protected float[] normals = null;
   protected byte[] colors = null;
   protected float[] uvData = null;
   protected Glyph gt = null;
   protected IndexedGeometryStripArray surf = null;
   protected OpenShape3D surfaces = new OpenShape3D();
   protected OpenAppearance appearance = new OpenAppearance();
   protected OpenTransparencyAttributes transparencyAttributes = new OpenTransparencyAttributes();
   protected OpenLineAttributes lattr = new OpenLineAttributes(1.f, OpenLineAttributes.PATTERN_SOLID, true);
   protected ColorMap colorMap = null;
   private boolean fromUI = false;
   private boolean fromIn = false;
   protected float lastTime = -Float.MAX_VALUE;
   protected int currentColorMode = -1;
   protected Texture2D texture = null;
   protected static final boolean[][] resetGeometry =
   {
      {
         false, true, true, true, true, true, true, true, true
      },
      {
         true, false, false, false, false, false, false, false, true
      },
      {
         true, false, false, false, false, false, false, false, true
      },
      {
         true, false, false, false, false, false, false, false, true
      },
      {
         true, false, false, false, false, false, false, false, true
      },
      {
         true, false, false, false, false, false, false, false, true
      },
      {
         true, false, false, false, false, false, false, false, true
      },
      {
         true, false, false, false, false, false, false, false, true
      },
      {
         true, true, true, true, true, true, true, true, false
      }
   };

   public Glyphs()
   {
      parameters = params = new GlyphsParams();
      params.addChangeListener(new ChangeListener()
      {
         @Override
         public void stateChanged(ChangeEvent evt)
         {
            if (fromIn)
               return;
            if (inField != null)
            {
               fromUI = true;
               update();
               fromUI = false;
            }

         }
      });

      appearance.setTransparencyAttributes(transparencyAttributes);
      appearance.getPolygonAttributes().setBackFaceNormalFlip(true);
      surfaces.setCapability(Shape3D.ENABLE_PICK_REPORTING);
      surfaces.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
      surfaces.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
      surfaces.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
      surfaces.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
      surfaces.setCapability(Geometry.ALLOW_INTERSECT);
      surfaces.setCapability(Node.ALLOW_LOCAL_TO_VWORLD_READ);
      surfaces.setAppearance(appearance);
      outGroup.addChild(surfaces);
      outObj.addNode(outGroup);

      RenderEventListener renderListener = new RenderEventListener()
      {
         @Override
         public void renderExtentChanged(RenderEvent e)
         {
            if (!fromIn && inField != null)
            {
               //updateColors();

               int extent = e.getUpdateExtent();
               int cMode = dataMappingParams.getColorMode();
               if (renderingParams.getDisplayMode() == AbstractRenderingParams.BACKGROUND)
                  cMode = DataMappingParams.UNCOLORED;
               if (currentColorMode < 0)
               {
                  updateGeometry();
                  currentColorMode = cMode;
                  return;
               }
               if (extent == RenderEvent.COLORS || extent == RenderEvent.TRANSPARENCY || extent == RenderEvent.TEXTURE)
               {
                  if (resetGeometry[currentColorMode][cMode])
                     updateGeometry();
                  else if (cMode == DataMappingParams.UVTEXTURED)
                     updateTextureCoords();
                  else
                     updateColors();
                  currentColorMode = cMode;
                  return;
               }
               if (extent == RenderEvent.COORDS)
                  updateCoords();
               if (extent == RenderEvent.GEOMETRY)
                  updateGeometry();
               currentColorMode = cMode;
            }
         }
      };
      dataMappingParams.addRenderEventListener(renderListener);
      renderingParams.addRenderEventListener(renderListener);

      SwingInstancer.swingRunAndWait(new Runnable()
      {
         @Override
         public void run()
         {
            ui = new GlyphsGUI();
            ui.setParams(params);
            ui.getDataMappingGUI().setRenderingParams(renderingParams);
            setPanel(ui);
         }
      });
   }

   private boolean isValid(int i)
   {
      if (isValidity && !valid[i])
         return false;
      double d = 0, t = 0;
      if (thresholdDataArray == null)
         return true;
      if (glyphDataArray != null && isThrRelative)
         if (glyphDataArray.getVeclen() == 1)
            d = glyphDataArray.getData(i);
         else
         {
            float[] v = glyphDataArray.getVData(i);
            d = 0;
            for (int j = 0; j < v.length; j++)
               d += v[j] * v[j];
            d = Math.sqrt(d);
         }
      if (thresholdDataArray.getVeclen() == 1)
         t = thresholdDataArray.getData(i);
      else
      {
         float[] v = thresholdDataArray.getVData(i);
         t = 0;
         for (int j = 0; j < v.length; j++)
            t += v[j] * v[j];
         t = Math.sqrt(t);
      }
      if (isThrRelative)
         return d > thr * t;
      else
         return t > thr;
   }

   private void prepareLocalCoords()
   {
      if (glyphDataArray == null)
         return;
      if (glyphDataArray.getVeclen() > 3)
      {
         baseU = null;
         baseV = null;
         baseW = null;
         return;
      }
      float[] um =
      {
         0, 0, 0
      };
      float[] vm =
      {
         0, 0, 0
      };
      float[] wm =
      {
         0, 0, 0
      };

      baseU = new float[3 * nGlyphs];
      baseV = new float[3 * nGlyphs];
      baseW = new float[3 * nGlyphs];
      float[] p =
      {
         0, 0, 0
      };
      int vlen = glyphDataArray.getVeclen();
      float[] gData = glyphDataArray.getFData();
      for (int i = 0; i < nGlyphs; i++)
      {
         System.arraycopy(gData, vlen * glyphIn[i], p, 0, vlen);
         float pn = p[0] * p[0] + p[1] * p[1] + p[2] * p[2];
         if (pn < 1e-20)
         {
            for (int j = 0; j < 3; j++)
               baseU[3 * i + j] = baseV[3 * i + j] = baseW[3 * i + j] = 0;
            baseU[3 * i] = baseV[3 * i + 1] = baseW[3 * i + 2] = 1;
            continue;
         }
         pn = (float) Math.sqrt(pn);
         for (int j = 0; j < p.length; j++)
            um[j] = p[j] / pn;
         if (Math.abs(um[0]) > Math.abs(um[1]) && Math.abs(um[0]) > Math.abs(um[2]))
         {
            vm[0] = vm[1] = 0;
            vm[2] = 1;
         } else
         {
            vm[1] = vm[2] = 0;
            vm[0] = 1;
         }
         wm[0] = um[1] * vm[2] - um[2] * vm[1];
         wm[1] = um[2] * vm[0] - um[0] * vm[2];
         wm[2] = um[0] * vm[1] - um[1] * vm[0];
         pn = (float) Math.sqrt(wm[0] * wm[0] + wm[1] * wm[1] + wm[2] * wm[2]);
         for (int j = 0; j < 3; j++)
            wm[j] /= pn;
         vm[0] = um[2] * wm[1] - um[1] * wm[2];
         vm[1] = um[0] * wm[2] - um[2] * wm[0];
         vm[2] = um[1] * wm[0] - um[0] * wm[1];
         System.arraycopy(um, 0, baseW, 3 * i, 3);
         System.arraycopy(vm, 0, baseV, 3 * i, 3);
         System.arraycopy(wm, 0, baseU, 3 * i, 3);
      }
   }

   private void prepareGlyphCount()
   {
      nGlyphs = 0;
      glyphDataArray = inField.getData(params.getComponent());
      if (glyphDataArray != null && glyphDataArray.getVeclen() > 3)
         return;
      thr = params.getThr();
      thresholdDataArray = null;
      if (params.getThrComponent() >= 0)
         thresholdDataArray = inField.getData(params.getThrComponent());
      int[] gl = new int[inField.getNNodes()];
      for (int i = 0; i < gl.length; i++)
         gl[i] = -1;
      nGlyphs = 0;
      surf = null;
      if (inField instanceof RegularField)
      {
         RegularField inRegularField = (RegularField) inField;
         if (inRegularField.getDims() == null)
            return;
         int[] dims = inRegularField.getDims();
         int[] low = params.getLowCrop();
         int[] up = params.getUpCrop();
         int[] down = params.getDown();
         switch (dims.length)
         {
            case 3:
               for (int i = low[2]; i < up[2]; i += down[2])
                  for (int j = low[1]; j < up[1]; j += down[1])
                     for (int k = low[0], l = (dims[1] * i + j) * dims[0] + low[0]; k < up[0]; k += down[0], l += down[0])
                        if (isValid(l))
                        {
                           gl[nGlyphs] = l;
                           nGlyphs += 1;
                        }
               break;
            case 2:
               for (int j = low[1]; j < up[1]; j += down[1])
                  for (int k = low[0], l = j * dims[0] + low[0]; k < up[0]; k += down[0], l += down[0])
                     if (isValid(l))
                     {
                        gl[nGlyphs] = l;
                        nGlyphs += 1;
                     }
               break;
            case 1:
               for (int k = low[0], l = low[0]; k < up[0]; k += down[0], l += down[0])
                  if (isValid(l))
                  {
                     gl[nGlyphs] = l;
                     nGlyphs += 1;
                  }
         }
      } else
      {
         int downsize = params.getDownsize();
         for (int i = 0; i < inField.getNNodes(); i += downsize)
            if (isValid(i))
            {
               gl[nGlyphs] = i;
               nGlyphs += 1;
            }
      }
      glyphIn = new int[nGlyphs];
      System.arraycopy(gl, 0, glyphIn, 0, nGlyphs);
      colors = new byte[3 * nGlyphs];
      baseCoords = new float[3 * nGlyphs];
      updateBaseCoords();
   }

   private void updateBaseCoords()
   {
      for (int i = 0; i < baseCoords.length; i++)
         baseCoords[i] = 0;
      if (inField.getCoords() != null)
      {
         int nSp = inField.getNSpace();
         float[] fldCoords = inField.getCoords();
         for (int i = 0; i < nGlyphs; i++)
            for (int j = 0; j < nSp; j++)
               baseCoords[3 * i + j] = fldCoords[nSp * glyphIn[i] + j];
      } else if (inField instanceof RegularField)
      {
         float[][] inAff = ((RegularField) inField).getAffine();
         int[] dims = ((RegularField) inField).getDims();
         int i0 = 0, i1 = 0, i2 = 0;
         for (int i = 0; i < nGlyphs; i++)
         {
            int j = glyphIn[i];
            i0 = j % dims[0];
            if (dims.length > 1)
            {
               j /= dims[0];
               i1 = j % dims[1];
               if (dims.length > 2)
                  i2 = j / dims[1];
            }
            for (int k = 0; k < 3; k++)
               baseCoords[3 * i + k] = inAff[3][k] + i0 * inAff[0][k] + i1 * inAff[1][k] + i2 * inAff[2][k];
         }
      }
   }

   public void updateColors()
   {
      colors = ColorMapper.mapColorsIndexed(inField, dataMappingParams, glyphIn, renderingParams.getDiffuseColor(), colors);       
      if(dataMappingParams.getTransparencyParams().getComponent() < 0 && params.getTransparency() == 0) {
         appearance.getTransparencyAttributes().setTransparencyMode(TransparencyAttributes.NONE);
      } else {
         appearance.getTransparencyAttributes().setTransparencyMode(TransparencyAttributes.NICEST);
         colors = ColorMapper.mapTransparencyIndexed(inField, dataMappingParams.getTransparencyParams(), glyphIn, colors);       
      }
      surf.setColors(0, colors);
   }

   public void updateGeometry()
   {
      //boolean detach = outGroup.postdetach();
      surfaces.removeAllGeometries();
      surf = null;

      int cMode = dataMappingParams.getColorMode();
      if (renderingParams.getShadingMode() == RenderingParams.BACKGROUND)
         cMode = DataMappingParams.UNCOLORED;
      generateGlyphs(cMode);
      surfaces.addGeometry(surf);
      outObj.setExtents(inField.getExtents());

      appearance.setUserData(this);
      appearance.getColoringAttributes().setColor(renderingParams.getDiffuseColor());
      if (appearance.getMaterial() != null)
      {
         appearance.getMaterial().setAmbientColor(renderingParams.getAmbientColor());
         appearance.getMaterial().setDiffuseColor(renderingParams.getDiffuseColor());
         appearance.getMaterial().setSpecularColor(renderingParams.getSpecularColor());
      }

      Color bgrColor2 = renderingParams.getBackgroundColor();
      float[] bgrColorComps = new float[3];
      bgrColor2.getColorComponents(bgrColorComps);
      if (renderingParams.getShadingMode() == AbstractRenderingParams.BACKGROUND)
         appearance.getColoringAttributes().setColor(new Color3f(bgrColorComps[0], bgrColorComps[1], bgrColorComps[2]));
      else
         appearance.getColoringAttributes().setColor(renderingParams.getDiffuseColor());

      if (dataMappingParams.getColorMode() != DataMappingParams.UVTEXTURED)
         updateColors();
      else
         updateTextureCoords();
   }

   private void generateGlyphs(int cMode)
   {
      if (nGlyphs < 1)
         return;
      glyphDataArray = inField.getData(params.getComponent());
      if (glyphDataArray != null && glyphDataArray.getVeclen() > 3)
         return;
      int type = params.getType();
      int lod = params.getLod();

      if (glyphDataArray == null || glyphDataArray.getVeclen() == 1)
         gt = ScalarGlyphTemplates.glyph(type, lod);
      else
         gt = VectorGlyphTemplates.glyph(type, lod);
      nstrip = nGlyphs * gt.getNstrips();
      nvert = nGlyphs * gt.getNverts();
      nind = nGlyphs * gt.getNinds();
      ncol = nGlyphs;
      strips = new int[nstrip];
      verts = new float[3 * nvert];
      normals = new float[3 * nvert];
      pIndex = new int[nind];
      cIndex = new int[nind];
      makeIndices();
      if (glyphDataArray != null && glyphDataArray.getVeclen() > 1)
         prepareLocalCoords();
      if (verts == null || verts.length != 3 * nGlyphs * gt.getNverts())
         verts = new float[3 * nGlyphs * gt.getNverts()];

      int verticesMode = GeometryArray.COORDINATES;
      isNormals = false;
      if (gt.getType() != Glyph.LINE_STRIPS)
      {
         verticesMode |= GeometryArray.NORMALS;
         isNormals = true;
      }
      if (cMode == DataMappingParams.UVTEXTURED)
         verticesMode |= GeometryArray.TEXTURE_COORDINATE_2;
      else
         verticesMode |= GeometryArray.COLOR_4;

      if (isNormals && (normals == null || normals.length != 3 * nGlyphs * gt.getNverts()))
         normals = new float[3 * nGlyphs * gt.getNverts()];

      switch (gt.getType())
      {
         case Glyph.TRIANGLE_STRIPS:
            surf = new IndexedTriangleStripArray(nvert, verticesMode, nind, strips);
            break;
         case Glyph.TRIANGLE_FANS:
            surf = new IndexedTriangleFanArray(nvert, verticesMode, nind, strips);
            break;
         case Glyph.LINE_STRIPS:
            surf = new IndexedLineStripArray(nvert, verticesMode, nind, strips);
            break;
      }
      surf.setCapability(IndexedLineStripArray.ALLOW_COUNT_READ);
      surf.setCapability(IndexedLineStripArray.ALLOW_FORMAT_READ);
      surf.setCapability(IndexedLineStripArray.ALLOW_COORDINATE_INDEX_READ);
      surf.setCapability(IndexedLineStripArray.ALLOW_COORDINATE_READ);
      surf.setCapability(IndexedLineStripArray.ALLOW_COORDINATE_WRITE);
      if (cMode == DataMappingParams.UVTEXTURED)
      {
         surf.setCapability(IndexedLineStripArray.ALLOW_TEXCOORD_READ);
         surf.setCapability(IndexedLineStripArray.ALLOW_TEXCOORD_WRITE);
         surf.setCapability(IndexedLineStripArray.ALLOW_TEXCOORD_INDEX_READ);
         surf.setCapability(IndexedLineStripArray.ALLOW_TEXCOORD_INDEX_WRITE);
      } else
      {
         surf.setCapability(IndexedLineStripArray.ALLOW_COLOR_READ);
         surf.setCapability(IndexedLineStripArray.ALLOW_COLOR_WRITE);
         surf.setColorIndices(0, cIndex);
      }
      surf.setCoordinates(0, verts);
      surf.setCoordinateIndices(0, pIndex);
      if (isNormals)
      {
         surf.setCapability(IndexedLineStripArray.ALLOW_NORMAL_READ);
         surf.setCapability(IndexedLineStripArray.ALLOW_NORMAL_WRITE);
         surf.setNormals(0, normals);
         surf.setNormalIndices(0, pIndex);
      }
      OpenMaterial mat = new OpenMaterial();
      mat.setShininess(15.f);
      mat.setColorTarget(OpenMaterial.AMBIENT_AND_DIFFUSE);
      appearance.setMaterial(mat);
      PolygonAttributes pattr = new PolygonAttributes(
              PolygonAttributes.POLYGON_FILL,
              PolygonAttributes.CULL_NONE, 0.f, true);
      appearance.setPolygonAttributes(pattr);
      OpenColoringAttributes colAttrs = new OpenColoringAttributes();
      colAttrs.setColor(renderingParams.getDiffuseColor());
      appearance.setColoringAttributes(colAttrs);
      appearance.setLineAttributes(lattr);
      updateCoords();
   }

   private void updateTextureCoords()
   {
      boolean detach = outGroup.postdetach();
      int nSpace = inField.getNSpace();
      int nNodes = inField.getNNodes();
      float[] uvDataTmp = new float[2 * nNodes];
      ColorComponentParams[] tParams = new ColorComponentParams[]
      {
         dataMappingParams.getUParams(), dataMappingParams.getVParams()
      };
      for (int i = 0; i < tParams.length; i++)
         if (tParams[i].getDataComponent() >= 0)
            uvDataTmp = TextureMapper.map(inField.getData(tParams[i].getDataComponent()), tParams[i], uvDataTmp, i);
         else if (tParams[i].getDataComponent() == DataMappingParams.COORDX
                 || tParams[i].getDataComponent() == DataMappingParams.COORDY
                 || tParams[i].getDataComponent() == DataMappingParams.COORDZ)
            uvDataTmp = TextureMapper.map(inField.getCoords(), nSpace, inField.getExtents(), tParams[i], uvDataTmp, i, .01f);
         else if (tParams[i].getDataComponent() == DataMappingParams.NORMALX
                 || tParams[i].getDataComponent() == DataMappingParams.NORMALY
                 || tParams[i].getDataComponent() == DataMappingParams.NORMALZ)
            uvData = TextureMapper.map(normals, nSpace, inField.getExtents(), tParams[i], uvDataTmp, i, .01f);

      uvData = new float[2 * nvert];
      float u, v;
      int gVerts = gt.getNverts();
      for (int i = 0; i < nGlyphs; i++)
      {
         u = uvDataTmp[2 * glyphIn[i]];
         v = uvDataTmp[2 * glyphIn[i] + 1];
         for (int j = 0; j < gVerts; j++)
         {
            uvData[2 * (gVerts * i + j)] = u;
            uvData[2 * (gVerts * i + j) + 1] = v;
         }
      }

      surf.setTextureCoordinates(0, 0, uvData);
      surf.setTextureCoordinateIndices(0, 0, pIndex);
      appearance.setTexture(dataMappingParams.getTexture());
      if (detach)
         outGroup.postattach();
   }

   private void updateCoords()
   {
      lattr.setLineWidth(params.getLineThickness());
      float scale = params.getScale();
      float s = 0;
      float st = 0;
      boolean useAbs = params.isUseAbs();
      boolean useSqrt = params.isUseSqrt();
      float[] tVerts = gt.getVerts();
      float[] tNorms = gt.getNormals();
      float[] p = new float[3];
      float[] u = new float[3];
      float[] v = new float[3];
      float[] w = new float[3];
      if (glyphDataArray == null)
      {
         s = scale;
         for (int i = 0, k = 0; i < nGlyphs; i++)
         {
            System.arraycopy(baseCoords, 3 * i, p, 0, 3);
            if (isNormals)
               for (int j = 0, m = 0; j < tVerts.length / 3; j++)
                  for (int l = 0; l < 3; l++, k++, m++)
                  {
                     verts[k] = p[l] + s * tVerts[m];
                     normals[k] = tNorms[m];
                  }
            else
               for (int j = 0, m = 0; j < tVerts.length / 3; j++)
                  for (int l = 0; l < 3; l++, k++, m++)
                     verts[k] = p[l] + s * tVerts[m];
         }
      } else if (glyphDataArray.getVeclen() == 1)
         for (int i = 0, k = 0; i < nGlyphs; i++)
         {
            System.arraycopy(baseCoords, 3 * i, p, 0, 3);
            if (params.isConstantDiam())
               s = scale;
            else
            {
               s = glyphDataArray.getData(glyphIn[i]);
               if (useAbs || useSqrt)
                  s = Math.abs(s);
               if (useSqrt)
                  s = (float) Math.sqrt(s);
               s *= scale;
            }
            if (isNormals)
               for (int j = 0, m = 0; j < tVerts.length / 3; j++)
                  for (int l = 0; l < 3; l++, k++, m++)
                  {
                     verts[k] = p[l] + s * tVerts[m];
                     normals[k] = tNorms[m];
                  }
            else
               for (int j = 0, m = 0; j < tVerts.length / 3; j++)
                  for (int l = 0; l < 3; l++, k++, m++)
                     verts[k] = p[l] + s * tVerts[m];
         }
      else
         for (int i = 0, k = 0; i < nGlyphs; i++)
         {
            System.arraycopy(baseCoords, 3 * i, p, 0, 3);
            System.arraycopy(baseU, 3 * i, u, 0, 3);
            System.arraycopy(baseV, 3 * i, v, 0, 3);
            System.arraycopy(baseW, 3 * i, w, 0, 3);
            float[] vs = glyphDataArray.getVData(glyphIn[i]);
            if (params.isConstantDiam())
               s = scale;
            else
            {
               s = 0;
               for (int j = 0; j < vs.length; j++)
                  s += vs[j] * vs[j];
               s = (float) Math.sqrt(s);
               if (useSqrt)
                  s = (float) Math.sqrt(s);
               s *= scale;
            }
            if (params.isConstantThickness())
               st = params.getThickness();
            else
               st = s;
            if (isNormals)
               for (int j = 0; j < tVerts.length / 3; j++)
                  for (int l = 0; l < 3; l++, k++)
                  {
                     verts[k] = p[l] + st * (tVerts[3 * j] * u[l] + tVerts[3 * j + 1] * v[l]) + s * tVerts[3 * j + 2] * w[l];
                     normals[k] = tNorms[3 * j] * u[l] + tNorms[3 * j + 1] * v[l] + tNorms[3 * j + 2] * w[l];
                  }
            else
               for (int j = 0; j < tVerts.length / 3; j++)
                  for (int l = 0; l < 3; l++, k++)
                     verts[k] = p[l] + st * (tVerts[3 * j] * u[l] + tVerts[3 * j + 1] * v[l]) + s * tVerts[3 * j + 2] * w[l];
         }
      surf.setCoordinates(0, verts);
      if (isNormals)
         surf.setNormals(0, normals);
   }

   public void update()
   {
      if (params.getChange() == GlyphsParams.GEOMETRY_CHANGED)
      {
         prepareGlyphCount();
         currentColorMode = dataMappingParams.getColorMode();
      }
      if (nGlyphs < 1)
         return;
      if (params.getChange() >= GlyphsParams.GLYPHS_CHANGED)
         updateGeometry();
      if (params.getChange() >= GlyphsParams.COORDS_CHANGED)
         updateCoords();
      if (dataMappingParams.getColorMode() != DataMappingParams.UVTEXTURED)
         updateColors();
      appearance.getTransparencyAttributes().setTransparency(params.getTransparency());
      if (params.getTransparency() == 0)
         appearance.getTransparencyAttributes().setTransparencyMode(TransparencyAttributes.NONE);
      else
         appearance.getTransparencyAttributes().setTransparencyMode(TransparencyAttributes.NICEST);
      params.setChange(0);

      outObj.clearAllGeometry();
      outObj.addNode(outGroup);      
      outObj.setExtents(inField.getExtents());
   }

   protected void makeIndices()
   {
      int istrip = 0, iind = 0, ivert = 0, icol = 0;
      for (int n = 0; n < nGlyphs; n++)
      {
         for (int i = 0; i < gt.getNstrips(); i++, istrip++)
            strips[istrip] = gt.getStrips()[i];
         for (int i = 0; i < gt.getNinds(); i++, iind++)
         {
            pIndex[iind] = ivert + gt.getPntsIndex()[i];
            cIndex[iind] = icol;
         }
         ivert += gt.getNverts();
         icol += 1;
      }
   }

   @Override
   public void onActive()
   {
      if (!fromUI)
      {
         fromIn = true;
         if (getInputFirstValue("inField") == null)
            return;
         Field inFld = ((VNField) getInputFirstValue("inField")).getField();
         if (inFld == null)
            return;
         if (inField != inFld)
         {
            inField = inFld;
            outObj.setExtents(inField.getExtents());
            lastTime = inField.getCurrentTime();
            dataMappingParams.setInField(inFld);
            ui.setInData(inField, dataMappingParams);
            isValidity = inField.isMask();
            valid = inField.getMask();
            params.setChange(GlyphsParams.GEOMETRY_CHANGED);
         } else //if (lastTime != inFld.getCurrentTime())
         {
            lastTime = inField.getCurrentTime();
            isValidity = inField.isMask();
            valid = inField.getMask();
            params.setChange(GlyphsParams.GEOMETRY_CHANGED);
         }
         fromIn = false;
      }
      update();
      fromUI = false;
   }
}
