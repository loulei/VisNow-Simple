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

package pl.edu.icm.visnow.lib.basic.mappers.TubeGlyphs;


import javax.media.j3d.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pl.edu.icm.visnow.datasets.CellArray;
import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.datasets.IrregularField;
import pl.edu.icm.visnow.datasets.cells.Cell;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.engine.core.InputEgg;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.geometries.geometryTemplates.Glyph;
import pl.edu.icm.visnow.geometries.geometryTemplates.Templates;
import pl.edu.icm.visnow.geometries.objects.generics.*;
import pl.edu.icm.visnow.geometries.utils.ColorMapper;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.render.RenderEvent;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.render.RenderEventListener;
import pl.edu.icm.visnow.lib.templates.visualization.modules.VisualizationModule;
import pl.edu.icm.visnow.lib.types.VNField;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;

/**
 * @author  Krzysztof S. Nowinski (know@icm.edu.pl)
 * Warsaw University, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class TubeGlyphs extends VisualizationModule
{

   public static InputEgg[] inputEggs = null;
   public static OutputEgg[] outputEggs = null;
   protected GUI ui = null;
   private boolean fromUI = false;
   private boolean fromIn = false;
   protected Params params;
   protected IrregularField inField = null;
   protected OpenBranchGroup outGroup = null;
   protected DataArray data, colorData;
   protected int nGlyphs, nstrip, nvert, nind, ncol;
   protected int[] glyphV = null;
   protected int[] cIndex = null;
   protected int[] pIndex = null;
   protected int[] strips = null;
   protected float[] verts = null;
   protected float[] normals = null;
   protected byte[] colors = null;
   protected Glyph gt = null;
   protected IndexedGeometryStripArray surf;
   protected OpenAppearance app;
   protected OpenShape3D surfaces;
//
   public TubeGlyphs()
   {
      parameters = params = new Params();
      params.addChangeListener(new ChangeListener()
      {
         public void stateChanged(ChangeEvent evt)
         {
            if (fromIn)
               return;
            fromUI = true;
            switch (params.getChange())
            {
               case Params.COUNT_CHANGED:
               case Params.GLYPHS_CHANGED:
                  createTubeGlyphs();
                  break;
               case Params.COORDS_CHANGED:
                  updateCoords();
                  break;
               case Params.COLORS_CHANGED:
                  updateColors();
                  break;
            }
         }
      });
      dataMappingParams.addRenderEventListener(new RenderEventListener()
      {
         @Override
         public void renderExtentChanged(RenderEvent e)
         {
            updateColors();
         }
      });
      renderingParams.addRenderEventListener(new RenderEventListener()
      {
         @Override
         public void renderExtentChanged(RenderEvent e)
         {
            updateColors();
         }
      });
      SwingInstancer.swingRun(new Runnable()
      {
         @Override
         public void run()
         {
            ui = new GUI();
            ui.setParams(params, dataMappingParams, renderingParams);
            setPanel(ui);
         }
      });
   }

   public void createTubeGlyphs()
   {
      if (outGroup != null)
         outGroup.detach();
      outGroup = new OpenBranchGroup();
      data = inField.getData(params.getComponent());
      gt = new Templates.TubeTemplate(params.getLod());
      nGlyphs = 0;
      for (int i = 0; i < inField.getNCellSets(); i++)
         if (inField.getCellSet(i).getBoundaryCellArray(Cell.SEGMENT) != null)
            nGlyphs += inField.getCellSet(i).getBoundaryCellArray(Cell.SEGMENT).getNCells();
      if (nGlyphs == 0)
         return;
      nstrip = nGlyphs * gt.getNstrips();
      nvert = nGlyphs * gt.getNverts();
      nind = nGlyphs * gt.getNinds();
      ncol = 2 * nGlyphs;
      glyphV = new int[2 * nGlyphs];
      strips = new int[nstrip];
      verts = new float[3 * nvert];
      normals = new float[3 * nvert];
      pIndex = new int[nind];
      cIndex = new int[nind];
      colors = new byte[3 * ncol];
      makeIndices();
      surf = new IndexedTriangleStripArray(nvert,
              GeometryArray.COORDINATES | GeometryArray.NORMALS | GeometryArray.COLOR_4,
              nind, strips);
      surf.setCapability(IndexedLineStripArray.ALLOW_COUNT_READ);
      surf.setCapability(IndexedLineStripArray.ALLOW_FORMAT_READ);
      surf.setCapability(IndexedLineStripArray.ALLOW_COORDINATE_INDEX_READ);
      surf.setCapability(IndexedLineStripArray.ALLOW_COORDINATE_READ);
      surf.setCapability(IndexedLineStripArray.ALLOW_COORDINATE_WRITE);
      surf.setCapability(IndexedLineStripArray.ALLOW_COLOR_READ);
      surf.setCapability(IndexedLineStripArray.ALLOW_COLOR_WRITE);
      surf.setCoordinateIndices(0, pIndex);
      surf.setColorIndices(0, cIndex);
      if (normals != null)
      {
         surf.setCapability(IndexedLineStripArray.ALLOW_NORMAL_READ);
         surf.setCapability(IndexedLineStripArray.ALLOW_NORMAL_WRITE);
         surf.setNormalIndices(0, pIndex);
      }
      surfaces = new OpenShape3D();
      updateColors();
      updateCoords();      
      surfaces.setCapability(Shape3D.ENABLE_PICK_REPORTING);
      surfaces.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
      surfaces.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
      surfaces.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
      surfaces.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
      surfaces.setCapability(Geometry.ALLOW_INTERSECT);
      surfaces.setCapability(Node.ALLOW_LOCAL_TO_VWORLD_READ);
      surfaces.addGeometry(surf);
      outGroup.addChild(surfaces);
      outObj.addNode(outGroup);
      outObj.setExtents(inField.getExtents());
   }
   
   private void updateCoords()
   {
      float scale = params.getScale();
      float[] tVerts = gt.getVerts();
      float[] tNormals = gt.getNormals();
      float[] coords = inField.getCoords();
      for (int k = 0, ivert = 0; k < nGlyphs; k++)
      {
         float[] p = new float[3];
         int k0 = glyphV[2 * k];
         int k1 = glyphV[2 * k + 1];
         float sn0, sn1;
         float pm = 0;
         int m = -1;
         float vn, wn;
         for (int i = 0; i < 3; i++)
         {
            p[i] = coords[3 * k1 + i] - coords[3 * k0 + i];
            if (Math.abs(p[i]) > pm)
            {
               m = i;
               pm = Math.abs(p[i]);
            }
         }
         float[] v = new float[3];
         float[] w = new float[3];
         if (m == -1)
         {
            for (int i = 0; i < p.length; i++)
               v[i] = w[i] = 0;
            vn = wn = sn0 = sn1 = 0;
         } else
         {
            if (m != 0)
            {
               w[0] = 0;
               w[1] = p[2];
               w[2] = -p[1];
            } else
            {
               w[0] = -p[2];
               w[1] = 0;
               w[2] = p[0];
            }
            v[0] = p[1] * w[2] - p[2] * w[1];
            v[1] = p[2] * w[0] - p[0] * w[2];
            v[2] = p[0] * w[1] - p[1] * w[0];
            vn = (float) (1 / Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]));
            w[0] = p[1] * v[2] - p[2] * v[1];
            w[1] = p[2] * v[0] - p[0] * v[2];
            w[2] = p[0] * v[1] - p[1] * v[0];
            wn = (float) (1 / Math.sqrt(w[0] * w[0] + w[1] * w[1] + w[2] * w[2]));
            if (data != null && !params.isConstantDiam())
            {
               sn0 = Math.abs(data.getData(k0) * scale);
               sn1 = Math.abs(data.getData(k1) * scale);
            } else
               sn0 = sn1 = scale;
         }
         for (int l = 0; l < gt.getNverts(); l += 2, ivert += 6)
            for (int i = 0; i < 3; i++)
            {
               verts[ivert + i]       = vn * sn0 * tVerts[3 * l] *     v[i] + wn * sn0 * tVerts[3 * l + 1] * w[i] + coords[3 * k0 + i];
               verts[ivert + 3 + i]   = vn * sn1 * tVerts[3 * l + 3] * v[i] + wn * sn1 * tVerts[3 * l + 4] * w[i] + coords[3 * k1 + i];
               normals[ivert]         = vn * tNormals[3 * l] *     v[i] + wn * tNormals[3 * l + 1] * w[i] + p[i] * tNormals[3 * l + 2] / pm;
               normals[ivert + 3 + i] = vn * tNormals[3 * l + 3] * v[i] + wn * tNormals[3 * l + 4] * w[i] + p[i] * tNormals[3 * l + 5] / pm;
            }
      }
      surf.setCoordinates(0, verts);
      if (normals != null)
         surf.setNormals(0, normals);
   }
   
   public void updateColors()
   {
      if (colors== null || 6 * nGlyphs != colors.length)
         colors = new byte[6 * nGlyphs];
      if (params.isBgr())
      {
         app = new OpenAppearance();
         app.setColoringAttributes(new ColoringAttributes(1.f, 1.f, 1.f, 0));
         app.setPolygonAttributes(new PolygonAttributes(
                 PolygonAttributes.POLYGON_FILL,
                 PolygonAttributes.CULL_FRONT, 0.f, true));
         for (int i = 0; i < colors.length; i++)
            colors[i] = 0;
      } else
      {
         OpenMaterial mat = new OpenMaterial();
         mat.setDiffuseColor(renderingParams.getDiffuseColor());
         mat.setShininess(15.f);
         mat.setColorTarget(OpenMaterial.AMBIENT_AND_DIFFUSE);
         app = new OpenAppearance();
         app.setMaterial(mat);
         app.setColoringAttributes(new ColoringAttributes(1.f, 1.f, 1.f, 0));
         PolygonAttributes pattr = new PolygonAttributes(
                 PolygonAttributes.POLYGON_FILL,
                 PolygonAttributes.CULL_NONE, 0.f, true);
         app.setPolygonAttributes(pattr);
         OpenLineAttributes lattr = new OpenLineAttributes(1.f,
                 OpenLineAttributes.PATTERN_SOLID, true);
         app.setLineAttributes(lattr);
         colors = ColorMapper.mapColorsIndexed(inField, dataMappingParams, glyphV, renderingParams.getDiffuseColor(), colors);
      }
      surf.setColors(0, colors);
      surfaces.setAppearance(app);
   }
   
   protected void makeIndices()
   {
      for (int s = 0, ivert = 0; s < inField.getNCellSets(); s++)
      {
         CellArray seg = inField.getCellSet(s).getBoundaryCellArray(Cell.SEGMENT);
         if (seg == null)
            continue;
         int[] segNodes = seg.getNodes();
         for (int k = 0; k < seg.getNCells(); k++, ivert += 2)
         {
            glyphV[ivert] = segNodes[2 * k];
            glyphV[ivert + 1] = segNodes[2 * k + 1];      
         }
      }
      int istrip = 0, iind = 0, ivert = 0, icol = 0;
      for (int n = 0; n < nGlyphs; n++)
      {
         for (int i = 0; i < gt.getNstrips(); i++)
         {
            strips[istrip] = gt.getStrips()[i];
            istrip += 1;
         }
         for (int i = 0; i < gt.getNinds(); i++)
         {
            pIndex[iind] = ivert + gt.getPntsIndex()[i];
            cIndex[iind] = icol + i%2;
            iind += 1;
         }
         ivert += gt.getNverts();
         icol += 2;
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
         if (inFld == null || !(inFld instanceof IrregularField))
            return;
         if (inField != inFld)
         {
            inField = (IrregularField)inFld;
            createTubeGlyphs();
            ui.setInData(inField);
         }
         params.setChange(Params.COUNT_CHANGED);
         fromIn = false;
      }
      else
         createTubeGlyphs();
      fromUI = false;
   }
}
