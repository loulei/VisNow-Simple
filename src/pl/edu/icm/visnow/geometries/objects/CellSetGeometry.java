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

import java.util.Arrays;
import javax.media.j3d.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.Color3f;
import pl.edu.icm.visnow.datasets.*;
import pl.edu.icm.visnow.datasets.cells.Cell;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.geometries.events.ColorEvent;
import pl.edu.icm.visnow.geometries.events.ColorListener;
import pl.edu.icm.visnow.geometries.objects.generics.*;
import pl.edu.icm.visnow.geometries.parameters.*;
import pl.edu.icm.visnow.geometries.utils.ColorMapper;
import pl.edu.icm.visnow.geometries.utils.TextureMapper;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.render.RenderEvent;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.render.RenderEventListener;
import pl.edu.icm.visnow.lib.utils.geometry2D.GeometryObject2DStruct;

/**
 *
 * @author Krzysztof S. Nowinski <p> University of Warsaw, ICM
 * @author  Bartosz Borucki, University of Warsaw, ICM
 *
 */
public class CellSetGeometry extends OpenBranchGroup
{

   protected static final boolean[][] resetGeometry =
   {
      {false, true,  true,  true,  true,  true,  true,  true,  true},
      {true,  false, false, false, false, false, false, false, true},
      {true,  false, false, false, false, false, false, false, true},
      {true,  false, false, false, false, false, false, false, true},
      {true,  false, false, false, false, false, false, false, true},
      {true,  false, false, false, false, false, false, false, true},
      {true,  false, false, false, false, false, false, false, true},
      {true,  false, false, false, false, false, false, false, true},
      {true,  true,  true,  true,  true,  true,  true,  true, false}
   };
   protected String name;
   protected boolean ignoreUpdate = false;
   protected Field inField;
   protected CellSet cellSet;
   protected CellArray triangleCellArray = null;
   protected CellArray quadCellArray = null;
   protected CellArray segCellArray = null;
   protected CellArray pointCellArray = null;
   protected CellSetDisplayParams activeParams = null;
   protected CellSetDisplayParams ownParams = new CellSetDisplayParams();
   protected CellSetDisplayParams parentParams = null;
   protected RenderingParams renderingParams = null;
   protected DataMappingParams dataMappingParams = null;
   protected TransformParams transformParams = null;
   protected boolean cellSelectionActive = false;
   protected int colorMode = DataMappingParams.UNCOLORED;
   protected int currentColorMode = -1;
   protected int nNodes = 0;
   protected int nCellNodes = 0;
   protected int nCellEdgeNodes = 0;
   protected int nIndices = 0;
   protected int nPointIndices = 0;
   protected int nEdgeIndices = 0;
   protected int nPoints = 0;
   protected int nTriangles = 0;
   protected int nSegments = 0;
   protected int[] coordIndices = null;
   protected int[] normalsIndices = null;
   protected int[] colorIndices = null;
   protected int[] coordEdgeIndices = null;
   protected int[] colorEdgeIndices = null;
   protected int[] coordPointIndices = null;
   protected int[] colorPointIndices = null;
   protected float[] coords = null;
   protected float[] cellCoords = null;
   protected float[] cellEdgeCoords = null;
   protected float[] normals = null;
   protected float[] cellNormals = null;
   protected float[] uvData = null;
   protected byte[] colors = null;
   protected float[] cellColors = null;
   protected float[] cellEdgeColors = null;
   protected float[][] extents =  {{0, 0, 0}, {0, 0, 0}};
   protected OpenMaterial material;
   protected OpenAppearance appearance = null;
   protected OpenAppearance lineAppearance = null;
   protected Texture2D texture = null;
   protected TextureAttributes texAtt = new TextureAttributes();
   protected ColormapLegend colormapLegend = new ColormapLegend();
   protected OpenShape3D surfaceShape = new OpenShape3D();
   protected OpenShape3D lineShape = new OpenShape3D();
   protected OpenShape3D pointShape = new OpenShape3D();
   protected OpenShape3D frameShape = new OpenShape3D();
   protected IndexedTriangleArray triangleArr = null;
   protected GeometryArray edgeArr = null;
   protected IndexedPointArray pointArr = null;
   protected IndexedLineStripArray boxArr = null;
   protected OpenBranchGroup geometryGroup = new OpenBranchGroup();
   protected OpenTransformGroup transformGroup = new OpenTransformGroup();
   protected OpenTransparencyAttributes transparencyAttributes = new OpenTransparencyAttributes();
   protected boolean structureChanged = true;
   protected boolean isTextureChanged = true;
   protected boolean coordsChanged = true;
   protected boolean colorsChanged = true;
   protected boolean uvCoordsChanged = true;
   protected int mode = 0;
   protected boolean surfaceOrientation = true;
   protected boolean lastSurfaceOrientation = true;
   protected boolean[] shownPoints = null;
   protected boolean[] shownTriangles = null;
   protected boolean[] shownQuads = null;
   protected boolean[] shownSegments = null;
   protected boolean isPicked = false;
   protected Color3f bgrColor = new Color3f(0, 0, 0);
   protected int[] timeRange = null;
   protected float currentT = 0;
   private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(CellSetGeometry.class);
   protected GeometryObject2DStruct outObj2DStruct = new GeometryObject2DStruct();
   
   protected RenderEventListener renderEventListener = new RenderEventListener()
   {
      @Override
      public void renderExtentChanged(RenderEvent e)
      {
         if (ignoreUpdate)
            return;
         
         int extent = e.getUpdateExtent();
         if (currentColorMode < 0)
         {
            currentColorMode = dataMappingParams.getColorMode();
            updateGeometry();
            return;
         }
         validateColorMode();
         if ((extent & (RenderEvent.COLORS | RenderEvent.TRANSPARENCY | RenderEvent.TEXTURE)) != 0)
         {
            if (resetGeometry[currentColorMode][colorMode])
               updateGeometry();
            else if (colorMode == DataMappingParams.UVTEXTURED)
               updateTexture();
            else if (colorMode != DataMappingParams.UNCOLORED)
               updateColors();

            currentColorMode = colorMode;

            return;
         }
         if (extent == RenderEvent.COORDS)
            updateCoords();
         if (extent == RenderEvent.GEOMETRY)
            updateGeometry();
         if (extent == RenderEvent.APPEARANCE)
            updateAppearance();
         currentColorMode = colorMode;
      }
   };

   public CellSetGeometry(String name)
   {
      this.name = name;
      setName(name);
      geometryGroup.addChild(surfaceShape);
      geometryGroup.addChild(lineShape);
      geometryGroup.addChild(pointShape);
      geometryGroup.addChild(frameShape);
      transformGroup.addChild(geometryGroup);
      addChild(transformGroup);
      transformGroup.setPickable(true);
      transparencyAttributes.setTransparencyMode(TransparencyAttributes.NICEST);
      transparencyAttributes.setTransparency(.5f);
      outObj2DStruct.setName(name);
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
      arr.setCapability(IndexedGeometryArray.ALLOW_COLOR_INDEX_READ);
      arr.setCapability(IndexedGeometryArray.ALLOW_COLOR_INDEX_WRITE);
      arr.setCapability(IndexedGeometryArray.ALLOW_COORDINATE_INDEX_READ);
      arr.setCapability(IndexedGeometryArray.ALLOW_COORDINATE_INDEX_WRITE);
      arr.setCapability(IndexedGeometryArray.ALLOW_NORMAL_INDEX_READ);
      arr.setCapability(IndexedGeometryArray.ALLOW_NORMAL_INDEX_WRITE);
   }

   public void setIgnoreUpdate(boolean ignoreUpdate)
   {
      this.ignoreUpdate = ignoreUpdate;
   }

   protected void createBoundaryCellNormals()
   {
      if (cellSet == null || cellSet.getBoundaryCellArrays() == null)
         return;
      for (int m = 0; m < Cell.TYPES2D; m++)
      {
         CellArray ar = cellSet.getBoundaryCellArray(m);
         if (ar == null || ar.getDim() != 2 || ar.getNCells() < 1)
            continue;
         cellNormals = new float[3 * ar.getNCells()];
         if (ar.getType() != Cell.TRIANGLE && ar.getType() != Cell.QUAD)
            continue;
         int n = Cell.nv[ar.getType()];
         float[] v0 = new float[3];
         float[] v1 = new float[3];
         int[] nodes = ar.getNodes();
         boolean[] orientation = ar.getOrientations();
         int i;


         for (i = 0; i < ar.getNCells(); i++)
         {
            for (int j = 0; j < 3; j++)
            {
               v0[j] = coords[3 * nodes[i * n + 1] + j] - coords[3 * nodes[i * n] + j];
               v1[j] = coords[3 * nodes[i * n + 2] + j] - coords[3 * nodes[i * n] + j];
            }
            cellNormals[3 * i] = v0[1] * v1[2] - v0[2] * v1[1];
            cellNormals[3 * i + 1] = v0[2] * v1[0] - v0[0] * v1[2];
            cellNormals[3 * i + 2] = v0[0] * v1[1] - v0[1] * v1[0];
            float r = cellNormals[3 * i] * cellNormals[3 * i]
                    + cellNormals[3 * i + 1] * cellNormals[3 * i + 1]
                    + cellNormals[3 * i + 2] * cellNormals[3 * i + 2];
            r = (float) (Math.sqrt(r));
            if( r != 0) {
                if (orientation[i] == surfaceOrientation)
                for (int j = 0; j < 3; j++)
                    cellNormals[3 * i + j] /= r;
                else
                for (int j = 0; j < 3; j++)
                    cellNormals[3 * i + j] /= -r;
            } else {
                //System.out.println("huhu");
            }
         }
         ar.setCellNormals(cellNormals);
      }
   }

   public void setInData(IrregularField inField, CellSet cellSet)
   {
      if (inField == null || cellSet == null)
         return;
      ownParams = new CellSetDisplayParams(inField, cellSet);
      coordsChanged = this.inField == null
              || inField.getCoordsHash() != this.inField.getCoordsHash();
      if (inField.getNSpace() == 3)
         coords = inField.getCoords();
      else
      {
         int nSp = inField.getNSpace();
         float[] crds = inField.getCoords();
         coords = new float[3 * inField.getNNodes()];
         Arrays.fill(coords, 0.f);
         for (int i = 0; i < inField.getNNodes(); i++)
            for (int j = 0; j < nSp; j++)
               coords[3 * i + j] = crds[nSp * i + j];
      }
      extents = inField.getExtents();
      this.inField = inField;
      structureChanged = (this.cellSet == null || !cellSet.isStructCompatible(this.cellSet));
      this.cellSet = cellSet;
      nNodes = inField.getNNodes();
      createBoundaryCellNormals();
      normals = new float[3 * nNodes];
      for (int i = 0; i < normals.length; i++)
         normals[i] = 0;
      for (int m = Cell.TYPES1D; m < Cell.TYPES2D; m++)
      {
         CellArray ar = cellSet.getBoundaryCellArray(m);
         if (ar == null)
            continue;
         float[] cNormals = ar.getCellNormals();
         for (int i = 0; i < ar.getNCells(); i++)
         {
            try
            {
               int[] cellNodes = ar.getNodes(i);
               for (int j = 0; j < cellNodes.length; j++)
               {
                  int k = cellNodes[j];
                  for (int l = 0; l < 3; l++)
                     normals[3 * k + l] += cNormals[3 * i + l];
               }
            } catch (Exception e)
            {
            }
         }
      }
      for (int i = 0; i < nNodes; i++)
      {
         double d = normals[3 * i] * normals[3 * i] + normals[3 * i + 1] * normals[3 * i + 1] + normals[3 * i + 2] * normals[3 * i + 2];
         if (d == 0)
            normals[3 * i] = 1;
         else
         {
            float r = (float) (Math.sqrt(d));
            for (int j = 0; j < 3; j++)
               normals[3 * i + j] /= r;
         }
      }
   }

   private static boolean[] selection(boolean dataSelectionActive, CellArray a, DataArray sda,
           float selectUnder, float selectOver, int[] nSel, boolean[] validityMask)
   {
      if (a == null)  return null;
      boolean[] sel = new boolean[a.getNCells()];
      for (int i = 0; i < sel.length; i++)
         sel[i] = true;

      if (dataSelectionActive)
      {
         int[] dataInd = a.getDataIndices();
         switch (sda.getType())
         {
            case DataArray.FIELD_DATA_BYTE:
               byte[] bdta = sda.getBData();
               for (int i = 0; i < a.getNCells(); i++)
               {
                  int bb = bdta[dataInd[i]] & 0xff;
                  sel[i] = bb >= selectOver && bb <= selectUnder;
               }
               break;
            case DataArray.FIELD_DATA_SHORT:
               short[] sdta = sda.getSData();
               for (int i = 0; i < a.getNCells(); i++)
               {
                  short ss = sdta[dataInd[i]];
                  sel[i] = ss >= selectOver && ss <= selectUnder;
               }
               break;
            case DataArray.FIELD_DATA_INT:
               int[] idta = sda.getIData();
               for (int i = 0; i < a.getNCells(); i++)
               {
                  int ii = idta[dataInd[i]];
                  sel[i] = ii >= selectOver && ii <= selectUnder;
               }
               break;
            case DataArray.FIELD_DATA_FLOAT:
               float[] fdta = sda.getFData();
               for (int i = 0; i < a.getNCells(); i++)
               {
                  float ff = fdta[dataInd[i]];
                  sel[i] = ff >= selectOver && ff <= selectUnder;
               }
               break;
            case DataArray.FIELD_DATA_DOUBLE:
               double[] ddta = sda.getDData();
               for (int i = 0; i < a.getNCells(); i++)
               {
                  double dd = ddta[dataInd[i]];
                  sel[i] = dd >= selectOver && dd <= selectUnder;
               }
               break;
         }
      }

      if (validityMask != null)
      {
         int nCells = a.getNCells();
         int[] nodes = a.getNodes();
         int nVerts = a.getCellNodes();
         Cell c;
         for (int i = 0; i < nCells; i++)
         {
            if (sel[i])
               for (int v = 0, j = nVerts * i; v < nVerts; v++, j++)
               {
                  if (!validityMask[nodes[j]])
                  {
                     sel[i] = false;
                     break;
                  }
               }
         }
      }

      nSel[0] = 0;
      for (int i = 0; i < sel.length; i++)
         if (sel[i])
            nSel[0] += 1;
      return sel;
   }

   /**
    * checkSelection is called when cells are selected according to cell data arrays
    * shownSegments and shownTriangles boolean arrays indicating, which cells will be rendered are set.
    * In addition, edges with dihedral angles below threshold level are removed from display.
    * nTriangles, nSegments, nPoints and n*Indices are updated.
    */


   protected void checkSelection()
   {
      cellSelectionActive = activeParams.isSelectionActive();
      boolean[] validityMask = inField.getMask();

      nTriangles = 0;
      nSegments = 0;
      nPoints = 0;
      nIndices = nEdgeIndices = nPointIndices = 0;

      if (!cellSelectionActive && validityMask == null)
      {
         if (triangleCellArray != null)
            nTriangles += triangleCellArray.getNCells();
         if (quadCellArray != null)
            nTriangles += 2 * quadCellArray.getNCells();
         if (segCellArray != null)
         {
            shownSegments = new boolean[segCellArray.getNCells()];
            float featureAngle = renderingParams.getMinEdgeDihedral();
            float[] edgeAngles = segCellArray.getCellDihedrals();
            if (edgeAngles != null)
               for (int i = 0; i < segCellArray.getNCells(); i++)
               {
                  shownSegments[i] = edgeAngles[i] >= featureAngle;
                  if (shownSegments[i])
                     nSegments += 1;
               }
            else
            {
               for (int i = 0; i < segCellArray.getNCells(); i++)
                  shownSegments[i] = true;
               nSegments = segCellArray.getNCells();
            }
         }
         if (pointCellArray != null) {
             shownPoints = new boolean[pointCellArray.getNCells()];
             for (int i = 0; i < shownPoints.length; i++) {
                 shownPoints[i] = true;
             }
             nPoints = pointCellArray.getNCells();
         }
         nIndices = 3 * nTriangles;
         nEdgeIndices = 2 * nSegments;
         nPointIndices = nPoints;
         return;
      }

      int selectByComponent = activeParams.getSelectByComponent();
      float selectOver = activeParams.getSelectOver();
      float selectUnder = activeParams.getSelectUnder();
      DataArray sda = cellSet.getData(selectByComponent);
      int[] nSel = new int[1];

      if (triangleCellArray != null)
      {
         shownTriangles = selection(cellSelectionActive, triangleCellArray, sda, selectUnder, selectOver, nSel, validityMask);
         nTriangles = nSel[0];
      }

      if (quadCellArray != null)
      {
         shownQuads = selection(cellSelectionActive, quadCellArray, sda, selectUnder, selectOver, nSel, validityMask);
         nTriangles += 2 * nSel[0];
      }

      if (segCellArray != null)
      {
         shownSegments = selection(cellSelectionActive, segCellArray, sda, selectUnder, selectOver, nSel, validityMask);
         nSegments = 0;
         float featureAngle = renderingParams.getMinEdgeDihedral();
         float[] edgeAngles = segCellArray.getCellDihedrals();
         if (edgeAngles != null)
            for (int i = 0; i < segCellArray.getNCells(); i++)
            {
               if (edgeAngles[i] < featureAngle)
                  shownSegments[i] = false;
               if (shownSegments[i])
                  nSegments += 1;
            }
         else
            nSegments = nSel[0];
      }

      if (pointCellArray != null)
      {
         shownPoints = selection(cellSelectionActive, pointCellArray, sda, selectUnder, selectOver, nSel, validityMask);
         nPoints += nSel[0];
      }

      nIndices = 3 * nTriangles;
      nEdgeIndices = 2 * nSegments;
      nPointIndices = nPoints;
      if (validityMask != null)
         cellSelectionActive = true;
   }

   protected void generateNodeCoordIndices()
   {
      coordIndices = new int[nIndices];
      int k = 0;
      int[] nodes;
      boolean[] orientations;
      if (triangleCellArray != null)
      {
         nodes = triangleCellArray.getNodes();
         orientations = triangleCellArray.getOrientations();
         for (int i = 0; i < triangleCellArray.getNCells(); i++)
         {
            try
            {
               if (cellSelectionActive && !shownTriangles[i])
                  continue;
               coordIndices[k] = nodes[3 * i];
               if (orientations[i] == surfaceOrientation)
               {
                  coordIndices[k + 1] = nodes[3 * i + 1];
                  coordIndices[k + 2] = nodes[3 * i + 2];
               } else
               {
                  coordIndices[k + 1] = nodes[3 * i + 2];
                  coordIndices[k + 2] = nodes[3 * i + 1];
               }
               k += 3;
            } catch (Exception e)
            {
               log.error("" + k + "<" + coordIndices.length + "  " + (3 * i) + "<" + nodes.length);
            }
         }
      }
      if (quadCellArray != null)
      {
         nodes = quadCellArray.getNodes();
         orientations = quadCellArray.getOrientations();
         for (int i = 0; i < quadCellArray.getNCells(); i++)
         {
            if (cellSelectionActive && !shownQuads[i])
               continue;
            coordIndices[k] = coordIndices[k + 3] = nodes[4 * i];
            if (orientations[i] == surfaceOrientation)
            {
               coordIndices[k + 1] = nodes[4 * i + 1];
               coordIndices[k + 2] = nodes[4 * i + 2];
               coordIndices[k + 4] = nodes[4 * i + 2];
               coordIndices[k + 5] = nodes[4 * i + 3];
            } else
            {
               coordIndices[k + 1] = nodes[4 * i + 2];
               coordIndices[k + 2] = nodes[4 * i + 1];
               coordIndices[k + 4] = nodes[4 * i + 3];
               coordIndices[k + 5] = nodes[4 * i + 2];
            }
            k += 6;
         }
      }
   }

   protected void generateCellColorIndices()
   {
      if (nIndices == 0)
         return;
      colorIndices = new int[nIndices];
      int[] cindices;
      int k = 0;

      if (triangleCellArray != null)
      {
	  if(triangleCellArray.getDataIndices() == null )
		  throw new RuntimeException("data indices are missing, perphaps you've added dataArray without indices?");

         cindices = triangleCellArray.getDataIndices();
         for (int i = 0; i < triangleCellArray.getNCells(); i++)
            if (!cellSelectionActive || shownTriangles[i])
               for (int j = 0; j < 3; j++, k++)
                  colorIndices[k] = cindices[i];
      }

      if (quadCellArray != null)
      {
         cindices = quadCellArray.getDataIndices();
         for (int i = 0; i < quadCellArray.getNCells(); i++)
            if (!cellSelectionActive || shownQuads[i])
               for (int j = 0; j < 6; j++, k++)
                  colorIndices[k] = cindices[i];
      }
   }

   protected void generateCellCoordIndices()
   {
      int n2DCells = 0;
      if (nTriangles != 0)
      {
         nCellNodes = 3 * nTriangles;
         coordIndices = new int[nIndices];
         normalsIndices = new int[nIndices];
         int[] nodes;
         boolean[] orientations;
         int k = 0, ni = 0;
         if (triangleCellArray != null)
         {
            n2DCells += triangleCellArray.getNCells();
            nodes = triangleCellArray.getNodes();
            orientations = triangleCellArray.getOrientations();
            for (int i = 0; i < triangleCellArray.getNCells(); i++, ni++)
               if (!cellSelectionActive || shownTriangles[i])
               {
                  coordIndices[k] = nodes[3 * i];
                  if (orientations[i] == surfaceOrientation)
                  {
                     coordIndices[k + 1] = nodes[3 * i + 1];
                     coordIndices[k + 2] = nodes[3 * i + 2];
                  } else
                  {
                     coordIndices[k + 1] = nodes[3 * i + 2];
                     coordIndices[k + 2] = nodes[3 * i + 1];
                  }
                  for (int j = 0; j < 3; j++, k++)
                     normalsIndices[k] = ni;
               }
         }
         if (quadCellArray != null)
         {
            n2DCells += quadCellArray.getNCells();
            nodes = quadCellArray.getNodes();
            orientations = quadCellArray.getOrientations();
            for (int i = 0; i < quadCellArray.getNCells(); i++, ni++)
               if (!cellSelectionActive || shownQuads[i])
               {
                  if (orientations[i] == surfaceOrientation)
                  {
                     coordIndices[k] = nodes[4 * i];
                     coordIndices[k + 1] = nodes[4 * i + 1];
                     coordIndices[k + 2] = nodes[4 * i + 2];
                     coordIndices[k + 3] = nodes[4 * i];
                     coordIndices[k + 4] = nodes[4 * i + 2];
                     coordIndices[k + 5] = nodes[4 * i + 3];
                  } else
                  {
                     coordIndices[k] = nodes[4 * i];
                     coordIndices[k + 1] = nodes[4 * i + 2];
                     coordIndices[k + 2] = nodes[4 * i + 1];
                     coordIndices[k + 3] = nodes[4 * i];
                     coordIndices[k + 4] = nodes[4 * i + 3];
                     coordIndices[k + 5] = nodes[4 * i + 2];
                  }
                  for (int j = 0; j < 6; j++)
                     normalsIndices[k + j] = ni;
                  k += 6;
               }
         }
      }
      cellNormals = new float[3 * n2DCells];
   }

   protected void generateEdgeIndices()
   {
      if (nSegments > 0)
      {
         coordEdgeIndices = new int[2 * nSegments];
         int[] nodes = segCellArray.getNodes();
         int k = 0;
         for (int i = 0; i < segCellArray.getNCells(); i++)
            if (shownSegments[i])
            {
               try
               {
                  coordEdgeIndices[k] = nodes[2 * i];
                  coordEdgeIndices[k + 1] = nodes[2 * i + 1];
               } catch (ArrayIndexOutOfBoundsException ex)
               {
                  log.error("" + ex.toString() + " cell=" + i + " out of " + segCellArray.getNCells() + ", nodes length = " + nodes.length
                          + " to indices " + k + "," + (k + 1) + " in coordEdgeIndices of length " + (2 * nSegments));
               }
               k += 2;
            }
      }
   }

   protected void generateTriangles()
   {
      if (nTriangles == 0)
         return;
      if (dataMappingParams.isCellDataMapped())
         generateCellColorIndices();
      else
         colorIndices = coordIndices;
      int nv = nNodes;

      int vertexFormat = GeometryArray.COORDINATES | GeometryArray.BY_REFERENCE;
      if (renderingParams.getShadingMode() != RenderingParams.UNSHADED && renderingParams.getShadingMode() != RenderingParams.BACKGROUND)
         vertexFormat |= GeometryArray.NORMALS;
      if (renderingParams.getShadingMode() != RenderingParams.BACKGROUND)
      {
         if (colorMode == DataMappingParams.COLORMAPPED || colorMode == DataMappingParams.RGB)
            vertexFormat |= GeometryArray.COLOR_4;
         if (colorMode == DataMappingParams.UVTEXTURED)
            vertexFormat |= GeometryArray.TEXTURE_COORDINATE_2;
      }
      if ((vertexFormat & GeometryArray.NORMALS) != 0)
      {
         if (renderingParams.getShadingMode() == RenderingParams.GOURAUD_SHADED && !dataMappingParams.isCellDataMapped())
            vertexFormat |= GeometryArray.USE_COORD_INDEX_ONLY;
      }
      triangleArr = new IndexedTriangleArray(nv, vertexFormat, nIndices);
      setStandardCapabilities(triangleArr);
      setIndexingCapabilities(triangleArr);
      if ((triangleArr.getVertexFormat() & (GeometryArray.COLOR_4 | GeometryArray.COLOR_4)) != 0 &&
          (triangleArr.getVertexFormat() & GeometryArray.USE_COORD_INDEX_ONLY) == 0)
         triangleArr.setColorIndices(0, colorIndices);
      if ((triangleArr.getVertexFormat() & (GeometryArray.TEXTURE_COORDINATE_2 | GeometryArray.TEXTURE_COORDINATE_3)) != 0)
      {
         if ((vertexFormat & GeometryArray.USE_COORD_INDEX_ONLY) == 0)
            triangleArr.setTextureCoordinateIndices(0, 0, coordIndices);
      }
      if ((triangleArr.getVertexFormat() & GeometryArray.USE_COORD_INDEX_ONLY) == 0
              && (vertexFormat & GeometryArray.NORMALS) != 0)
         triangleArr.setNormalIndices(0, normalsIndices);
      for (int i = 0; i < coordIndices.length; i++)
         if (coordIndices[i] < 0) coordIndices[i] = 0;
      triangleArr.setCoordinateIndices(0, coordIndices);
   }

   protected void generateEdges()
   {
      edgeArr = null;
      if (nSegments == 0)
         return;
      edgeArr = new IndexedLineArray(nNodes,
              GeometryArray.COORDINATES |
              GeometryArray.USE_COORD_INDEX_ONLY |
              GeometryArray.BY_REFERENCE,
              2 * nSegments);
      setStandardCapabilities(edgeArr);
      ((IndexedLineArray) edgeArr).setCoordinateIndices(0, coordEdgeIndices);
   }

   protected void generateNodeColoredEdges()
   {
      edgeArr = null;
      if (nSegments == 0)
         return;
      if (renderingParams.isLineLighting())
         edgeArr = new IndexedLineArray(nNodes,
                 GeometryArray.COORDINATES |
                 GeometryArray.NORMALS |
                 GeometryArray.COLOR_4 |
                 GeometryArray.USE_COORD_INDEX_ONLY |
                 GeometryArray.BY_REFERENCE,
                 2 * nSegments);
      else
         edgeArr = new IndexedLineArray(nNodes,
                 GeometryArray.COORDINATES |
                 GeometryArray.COLOR_4 |
                 GeometryArray.USE_COORD_INDEX_ONLY |
                 GeometryArray.BY_REFERENCE,
                 2 * nSegments);
      setStandardCapabilities(edgeArr);
      for (int i = 0; i < coordEdgeIndices.length; i++)
         if (coordEdgeIndices[i] < 0) coordEdgeIndices[i] = 0;
      ((IndexedLineArray) edgeArr).setCoordinateIndices(0, coordEdgeIndices);
   }

   protected void generateCellColoredEdges()
   {
      edgeArr = null;
      CellArray segmentArray = cellSet.getBoundaryCellArray(Cell.SEGMENT);
      if (segmentArray == null || segmentArray.getNCells() == 0)
         return;
      nSegments = segmentArray.getNCells();
      nCellEdgeNodes = 2 * nSegments;
      coordEdgeIndices = segmentArray.getNodes();
      edgeArr = new IndexedLineArray(nNodes, GeometryArray.COORDINATES |
                                             GeometryArray.COLOR_4 |
                                             GeometryArray.BY_REFERENCE, nCellEdgeNodes);
      setStandardCapabilities(edgeArr);
      setIndexingCapabilities((IndexedLineArray)edgeArr);
      colorEdgeIndices = new int[nCellEdgeNodes];
      int[] cindices = segmentArray.getDataIndices();
      for (int i = 0, k = 0; i < segmentArray.getNCells(); i++)
         for (int j = 0; j < 2; j++, k++)
            colorEdgeIndices[k] = cindices[i];
      ((IndexedLineArray)edgeArr).setColorIndices(0, colorEdgeIndices);
      ((IndexedLineArray)edgeArr).setCoordinateIndices(0, coordEdgeIndices);
   }

   protected void generatePoints()
   {
      if (nPoints > 0)
      {
         pointCellArray = cellSet.getBoundaryCellArray(Cell.POINT);
		   assert (pointCellArray!=null) : "no points in boundary! (boundary not calculated?)";
         int[] pointIndices = new int[nPoints];
         int[] nodes = pointCellArray.getNodes();
         int k = 0;
         for (int i = 0; i < pointCellArray.getNCells(); i++)
            if (shownPoints[i])
            {
               pointIndices[k] = nodes[i];
               k++;
            }

         if(colorMode == DataMappingParams.UNCOLORED ||  dataMappingParams.isCellDataMapped())
                pointArr = new IndexedPointArray(nNodes,
                        GeometryArray.COORDINATES |
                        GeometryArray.USE_COORD_INDEX_ONLY |
                        GeometryArray.BY_REFERENCE,
                        nPoints);
         else
                pointArr = new IndexedPointArray(nNodes,
                        GeometryArray.COORDINATES |
                        GeometryArray.COLOR_4 |
                        GeometryArray.USE_COORD_INDEX_ONLY |
                        GeometryArray.BY_REFERENCE,
                        nPoints);
             
         setStandardCapabilities(pointArr);
         pointArr.setCoordinateIndices(0, pointIndices);

      }
   }

   public void updateCoords(float[] crds)
   {
      if (crds.length == 3 * nNodes)
         coords = crds;
      else
      {
         int nSp = crds.length / nNodes;
         coords = new float[3 * inField.getNNodes()];
         Arrays.fill(coords, 0.f);
         for (int i = 0; i < inField.getNNodes(); i++)
            for (int j = 0; j < nSp; j++)
               coords[3 * i + j] = crds[nSp * i + j];
      }
      updateCoords(true);
   }

   public void updateCoords()
   {
      updateCoords(!ignoreUpdate);
   }

   public void updateCoords(boolean force)
   {
      if (!force || coords == null)
         return;
      boolean detach = this.postdetach();
      if (triangleArr != null)
      {
         triangleArr.setCoordRefFloat(coords);
         if ((triangleArr.getVertexFormat() & GeometryArray.NORMALS) != 0)
         {
            if (dataMappingParams.isCellDataMapped() || renderingParams.getShadingMode() == RenderingParams.FLAT_SHADED)
            {
               float[] cNormals;
               int k = 0;
               if (triangleCellArray != null)
               {
                  cNormals = triangleCellArray.getCellNormals();
                  if (cNormals == null)
                  {
                     createBoundaryCellNormals();
                     cNormals = triangleCellArray.getCellNormals();
                  }
                  for (int i = 0; i < triangleCellArray.getNCells(); i++)
                     if (!cellSelectionActive || shownTriangles[i])
                        for (int l = 0; l < 3; l++, k++)
                           cellNormals[k] = cNormals[3 * i + l];
               }
               if (quadCellArray != null)
               {
                  cNormals = quadCellArray.getCellNormals();
                  if (cNormals == null)
                  {
                     createBoundaryCellNormals();
                     cNormals = quadCellArray.getCellNormals();
                  }
                  for (int i = 0; i < quadCellArray.getNCells(); i++)
                     if (!cellSelectionActive || shownQuads[i])
                        for (int l = 0; l < 3; l++, k++)
                           cellNormals[k] = cNormals[3 * i + l];
               }
               triangleArr.setNormalRefFloat(cellNormals);
            } else
               triangleArr.setNormalRefFloat(normals);
         }
      }
      if (edgeArr != null)
         edgeArr.setCoordRefFloat(coords);
      if (pointArr != null)
         pointArr.setCoordRefFloat(coords);
      if(detach) this.postattach();
   }

   protected void updateTextureCoords()
   {
      if (triangleArr == null ||
         (triangleArr.getVertexFormat() & (GeometryArray.ALLOW_TEXCOORD_WRITE |
                                           GeometryArray.TEXTURE_COORDINATE_2 |
                                           GeometryArray.TEXTURE_COORDINATE_3 |
                                           GeometryArray.TEXTURE_COORDINATE_4)) == 0)
         return;
      boolean detach = this.postdetach();
      int nSpace = inField.getNSpace();

      uvData = new float[2 * nNodes];
      ColorComponentParams[] tParams = new ColorComponentParams[]
      {
         dataMappingParams.getUParams(), dataMappingParams.getVParams()
      };
      for (int i = 0; i < tParams.length; i++)
      {
         if (tParams[i].getDataComponent() >= 0)
            uvData = TextureMapper.map(inField.getData(tParams[i].getDataComponent()),
                    tParams[i], uvData, i);
         else if (tParams[i].getDataComponent() == DataMappingParams.COORDX
                 || tParams[i].getDataComponent() == DataMappingParams.COORDY
                 || tParams[i].getDataComponent() == DataMappingParams.COORDZ)
            uvData = TextureMapper.map(coords, nSpace, extents, tParams[i], uvData, i, .01f);
         else if (tParams[i].getDataComponent() == DataMappingParams.NORMALX
                 || tParams[i].getDataComponent() == DataMappingParams.NORMALY
                 || tParams[i].getDataComponent() == DataMappingParams.NORMALZ)
            uvData = TextureMapper.map(normals, nSpace,new float[][] {{-1, -1, -1}, {1, 1, 1}}, tParams[i], uvData, i, .01f);
      }
      triangleArr.setTexCoordRefFloat(0, uvData);
      if(detach) this.postattach();
   }

   private class MapColors implements Runnable
   {
      int nThreads      = 1;
      int iThread       = 0;
      int nNds = dataContainer.getNNodes();
      public MapColors(int nThreads, int iThread)
      {
         this.nThreads       = nThreads;
         this.iThread        = iThread;
      }

      @Override
      public void run()
      {
         int kstart = (nNds * iThread) / nThreads;
         int kend = (nNds * (iThread+1)) / nThreads;
         ColorMapper.map(dataContainer, dataMappingParams, kstart, kend, kstart, renderingParams.getDiffuseColor(), colors);
         ColorMapper.mapTransparency(dataContainer, dataMappingParams.getTransparencyParams(), kstart, kend, colors);
         if (timeRange != null)
            ColorMapper.mapTimeValidityTransparency(timeRange, currentT, kstart, kend, kstart, colors);
      }
   }

   private DataContainer dataContainer = null;

   /**
    * Is responsible for drawing data.
    */
   public void updateColors()
   {
      boolean detach = this.postdetach();
      timeRange = null;
      if (dataMappingParams.isCellDataMapped())
         dataContainer = cellSet;
      else
         dataContainer = inField;
      if (colors == null || colors.length != 4 * dataContainer.getNNodes())
         colors = new byte[4 * dataContainer.getNNodes()];
      material.setAmbientColor(renderingParams.getAmbientColor());
      material.setDiffuseColor(renderingParams.getDiffuseColor());
      material.setSpecularColor(renderingParams.getSpecularColor());
      appearance.getColoringAttributes().setColor(renderingParams.getDiffuseColor());
      // colors array will be filled in threads started below

      for (DataArray da : dataContainer.getData())
         if (da.getUserData() != null &&
             da.getUserData().length == 1 &&
             "valid time range".equalsIgnoreCase(da.getUserData()[0]))
         {
            timeRange = da.getIData();
            currentT  = dataContainer.getCurrentTime();
         }
      int nThreads = pl.edu.icm.visnow.system.main.VisNow.availableProcessors();
      Thread[] workThreads = new Thread[nThreads];
      for (int iThread = 0; iThread < nThreads; iThread++)
      {
         workThreads[iThread] = new Thread(new MapColors(nThreads, iThread));
         workThreads[iThread].start();
      }
      for (Thread workThread : workThreads)
         try
         {
            workThread.join();
         }catch (InterruptedException e)
         {

         }

      if (triangleArr != null
              && renderingParams.getShadingMode() != RenderingParams.BACKGROUND
              && (colorMode == DataMappingParams.COLORMAPPED
              || colorMode == DataMappingParams.RGB))
      {
         if ((triangleArr.getVertexFormat() & (GeometryArray.COLOR_3 | GeometryArray.COLOR_4)) == 0)
            generateTriangles();

         // color elements
         triangleArr.setColorRefByte(colors);
      }
      if (edgeArr != null
              && (colorMode == DataMappingParams.COLORMAPPED
              || colorMode == DataMappingParams.RGB))
      {
         // if edges don't have (RGB) or (ARGB), create colored edges
         if ((edgeArr.getVertexFormat() & (GeometryArray.COLOR_3 | GeometryArray.COLOR_4)) == 0)
         {
            if (dataMappingParams.isCellDataMapped())
               generateCellColoredEdges();
            else
               generateNodeColoredEdges();
         }

         // color elements
         edgeArr.setColorRefByte(colors);
      }
      if (pointArr != null
              && (colorMode == DataMappingParams.COLORMAPPED
              || colorMode == DataMappingParams.RGB) && !dataMappingParams.isCellDataMapped())
         // color elements
         pointArr.setColorRefByte(colors);
      if(detach) this.postattach();
   }

   public void updateAppearance()
   {
      appearance.getLineAttributes().setLineWidth(renderingParams.getLineThickness());
      appearance.getLineAttributes().setLinePattern(renderingParams.getLineStyle());
      appearance.getPointAttributes().setPointSize(renderingParams.getLineThickness());
      appearance.getPolygonAttributes().setBackFaceNormalFlip(renderingParams.isLightedBackside());
   }

   public void updateTexture()
   {
      if (ignoreUpdate)
         return;
      boolean detach = this.postdetach();
      texture = dataMappingParams.getTexture();
      if (texture != null && dataMappingParams.getColorMode() == DataMappingParams.UVTEXTURED)
      {
         appearance.setTexture(texture);
      }
      updateTextureCoords();
      if(detach) this.postattach();
   }

   public void updataDataMap()
   {
      boolean detach = this.postdetach();
      if (dataMappingParams.getColorMode() == DataMappingParams.COLORMAPPED ||
          dataMappingParams.getColorMode() == DataMappingParams.COLORMAPPED2D ||
          dataMappingParams.getColorMode() == DataMappingParams.COLORED )
         updateColors();
      else if (dataMappingParams.getColorMode() == DataMappingParams.UVTEXTURED)
         updateTexture();
      if(detach) this.postattach();
   }

   private boolean textureComponentValid(int c)
   {
      if (inField.getData(c) != null)
         return true;
      return c == DataMappingParams.COORDX || c == DataMappingParams.NORMALX
              || c == DataMappingParams.COORDY || c == DataMappingParams.NORMALY
              || c == DataMappingParams.COORDZ || c == DataMappingParams.NORMALZ;
   }

   private void validateColorMode()
   {
      colorMode = dataMappingParams.getColorMode();
      dataContainer = inField;
      if (dataMappingParams.isCellDataMapped())
         dataContainer = cellSet;
// check if color mode and selected components combination is valid; fall back to UNCOLORED otherwise
      switch (dataMappingParams.getColorMode())
      {
         case DataMappingParams.COLORMAPPED:
            if (dataContainer.getData(dataMappingParams.getColorMap0Params().getDataComponent()) != null)
               return;
            break;
         case DataMappingParams.RGB:
            if (dataContainer.getData(dataMappingParams.getRedParams().getDataComponent()) != null
                    || dataContainer.getData(dataMappingParams.getGreenParams().getDataComponent()) != null
                    || dataContainer.getData(dataMappingParams.getBlueParams().getDataComponent()) != null)
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

   public void updateGeometry()
   {
      if (ignoreUpdate || dataMappingParams == null || renderingParams == null)
         return;
      dataMappingParams.setParentObjectSize(nNodes);
      boolean detach = this.postdetach();
      mode = renderingParams.getDisplayMode();
      if (isPicked && activeParams.getPickIndicator() == CellSetDisplayParams.EDG_PICK_INDICATOR)
         mode |= AbstractRenderingParams.EDGES;
      if (isPicked && activeParams.getPickIndicator() == CellSetDisplayParams.SRF_PICK_INDICATOR)
         mode |= AbstractRenderingParams.SURFACE;
         
      pointCellArray = cellSet.getCellArray(Cell.POINT);
      if ((mode & RenderingParams.NODES) != 0)
         pointCellArray = cellSet.getBoundaryCellArray(Cell.POINT);
      segCellArray = cellSet.getBoundaryCellArray(Cell.SEGMENT);
      triangleCellArray = cellSet.getBoundaryCellArray(Cell.TRIANGLE);
      quadCellArray = cellSet.getBoundaryCellArray(Cell.QUAD);

      if( segCellArray == null && triangleCellArray == null && quadCellArray == null && pointCellArray == null )
          throw new RuntimeException("[CellSetGeometry.updateGeometry()]: boundary cell arrays are empty, nothing to visualize (tip: try calling generateExternFaces() on your cell set)");

      outObj2DStruct.removeAllChildren();
      outObj2DStruct.setName("irregular geometry");


      checkSelection();
      if (nIndices + nEdgeIndices + nPointIndices == 0) {
         if(detach) this.postattach();
         return;
      }
      surfaceOrientation = renderingParams.getSurfaceOrientation();

      if (nIndices != 0)
      {
         if (surfaceOrientation != lastSurfaceOrientation)
         {
            if (cellNormals != null)
               for (int i = 0; i < cellNormals.length; i++)
                  cellNormals[i] = -cellNormals[i];
            if (normals != null)
               for (int i = 0; i < normals.length; i++)
                  normals[i] = -normals[i];
            lastSurfaceOrientation = surfaceOrientation;
         }
      }
      if (dataMappingParams.isCellDataMapped() || renderingParams.getShadingMode() == RenderingParams.FLAT_SHADED)
         generateCellCoordIndices();
      else
         generateNodeCoordIndices();
      generateEdgeIndices();
      if (dataMappingParams.getColorModeChanged() > 0)
         validateColorMode();
      coords = inField.getCoords();
      if (structureChanged || 
          dataMappingParams.getModeChanged() > 0 || 
          dataMappingParams.getColorModeChanged() > 1)
      {
         if (surfaceShape.numGeometries() > 0)
            surfaceShape.removeAllGeometries();
         if (lineShape.numGeometries() > 0)
            lineShape.removeAllGeometries();
         if (pointShape.numGeometries() > 0)
            pointShape.removeAllGeometries();
         if (frameShape.numGeometries() > 0)
            frameShape.removeAllGeometries();
         if (material == null)
         {
            material = new OpenMaterial();
         }
         material.setAmbientColor(renderingParams.getAmbientColor());
         material.setDiffuseColor(renderingParams.getDiffuseColor());
         material.setSpecularColor(renderingParams.getSpecularColor());
         
         lineAppearance.getColoringAttributes().setColor(renderingParams.getDiffuseColor());
         
         if (renderingParams.getShadingMode() == RenderingParams.BACKGROUND)
         {
            appearance.getColoringAttributes().setColor(bgrColor);
            material.setDiffuseColor(bgrColor);
         } else
            appearance.getColoringAttributes().setColor(renderingParams.getDiffuseColor());
         texture = dataMappingParams.getTexture();
         if (texture != null && dataMappingParams.getColorMode() == DataMappingParams.UVTEXTURED)
            appearance.setTexture(texture);
         else
            appearance.setTexture(null);
         
         lineShape.setAppearance(lineAppearance);
         frameShape.setAppearance(lineAppearance);
         pointShape.setAppearance(lineAppearance);
         surfaceShape.setAppearance(appearance);
         triangleArr = null;
         edgeArr = null;
         pointArr = null;
         if ((mode & RenderingParams.SURFACE) != 0)
         {
            generateTriangles();
            if (triangleArr != null)
               try
               {
                  surfaceShape.addGeometry(triangleArr);
               } catch (Exception e)
               {
                  //FIXME: change println to Logger
                  System.out.println("j3d error at surfaceShape.addGeometry(triangleArr)");
               }
            else
               mode |= RenderingParams.EDGES;
         }
         if ((mode & RenderingParams.EDGES) != 0)
         {
            if (dataMappingParams.isCellDataMapped())
               switch (colorMode)
               {
                  case DataMappingParams.COLORMAPPED:
                  case DataMappingParams.RGB:
                     generateCellColoredEdges();
                     break;
                  case DataMappingParams.UVTEXTURED:
                  case DataMappingParams.UNCOLORED:
                     generateEdges();
                     break;
               }
            else
               switch (colorMode)
               {
                  case DataMappingParams.COLORMAPPED:
                  case DataMappingParams.RGB:
                     generateNodeColoredEdges();
                     break;
                  case DataMappingParams.UVTEXTURED:
                  case DataMappingParams.UNCOLORED:
                     generateEdges();
                     break;
               }
            if (edgeArr != null)
               lineShape.addGeometry(edgeArr);
         }
        if ((mode & RenderingParams.NODES) != 0) {
            generatePoints();
            pointShape.addGeometry(pointArr);
         }
         structureChanged = false;
      }
      updateCoords();
      if (isPicked &&
          activeParams.getPickIndicator() == CellSetDisplayParams.BOX_PICK_INDICATOR ||
         (activeParams.getRenderingParams().getDisplayMode() & AbstractRenderingParams.OUTLINE_BOX) != 0)
      {
         updateExtents();
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
                  boxVerts[m] = extents[k][0];
                  boxVerts[m + 1] = extents[j][1];
                  boxVerts[m + 2] = extents[i][2];
               }
         boxArr.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);
         boxArr.setCoordinates(0, boxVerts);
         frameShape.addGeometry(boxArr);
      }
      switch (colorMode)
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
      if(detach) this.postattach();
   }

   public void setParams(CellSetDisplayParams params)
   {
      if (params == null)
         return;
      this.activeParams = params;
      dataMappingParams = params.getDataMappingParams();
      colormapLegend.setParams(dataMappingParams.getColormapLegendParameters());
      renderingParams = params.getRenderingParams();
      transformParams = params.getTransformParams();
      appearance = renderingParams.getAppearance();
      appearance.setUserData(this);
      material = (OpenMaterial) appearance.getMaterial();
      lineAppearance = renderingParams.getLineAppearance();
      lineAppearance.setUserData(this);
      dataMappingParams.addRenderEventListener(renderEventListener);
      renderingParams.addRenderEventListener(renderEventListener);
      transformParams.addChangeListener(new ChangeListener()
      {

         @Override
         public void stateChanged(ChangeEvent evt)
         {
            transformGroup.setTransform(transformParams.getTransform());
         }
      });
      transformGroup.setTransform(transformParams.getTransform());
   }

   public ColormapLegend getColormapLegend()
   {
      return colormapLegend;
   }

   public void updateExtents()
   {
      for (int i = 0; i < 3; i++)
      {
         extents[0][i] = Float.MAX_VALUE;
         extents[1][i] = -Float.MAX_VALUE;
      }
      if (triangleCellArray != null)
      {
         int[] nodes = triangleCellArray.getNodes();
         for (int i = 0; i < nodes.length; i++)
         {
            int j = 3 * nodes[i];
            for (int k = 0; k < 3; k++)
            {
               if (coords[j + k] > extents[1][k])
                  extents[1][k] = coords[j + k];
               if (coords[j + k] < extents[0][k])
                  extents[0][k] = coords[j + k];
            }
         }
      }
      if (quadCellArray != null)
      {
         int[] nodes = quadCellArray.getNodes();
         for (int i = 0; i < nodes.length; i++)
         {
            int j = 3 * nodes[i];
            for (int k = 0; k < 3; k++)
            {
               if (coords[j + k] > extents[1][k])
                  extents[1][k] = coords[j + k];
               if (coords[j + k] < extents[0][k])
                  extents[0][k] = coords[j + k];
            }
         }
      }
      if (segCellArray != null)
      {
         int[] nodes = segCellArray.getNodes();
         for (int i = 0; i < nodes.length; i++)
         {
            int j = 3 * nodes[i];
            for (int k = 0; k < 3; k++)
            {
               if (coords[j + k] > extents[1][k])
                  extents[1][k] = coords[j + k];
               if (coords[j + k] < extents[0][k])
                  extents[0][k] = coords[j + k];
            }
         }
      }
      if (pointCellArray != null)
      {
         int[] nodes = pointCellArray.getNodes();
         for (int i = 0; i < nodes.length; i++)
         {
            int j = 3 * nodes[i];
            for (int k = 0; k < 3; k++)
            {
               if (coords[j + k] > extents[1][k])
                  extents[1][k] = coords[j + k];
               if (coords[j + k] < extents[0][k])
                  extents[0][k] = coords[j + k];
            }
         }
      }
   }

   public DataMappingParams getDataMappingParams()
   {
      return dataMappingParams;
   }

   public void setPicked(boolean picked)
   {
      this.isPicked = picked;
      updateGeometry();
   }

   public void flipPicked()
   {
      isPicked = !isPicked;
      cellSet.setSelected(isPicked);
      updateGeometry();
   }

   public Transform3D getTransform()
   {
      return transformParams.getTransform();
   }

   public CellSetDisplayParams getParams()
   {
      return activeParams;
   }

   public CellSet getCellSet()
   {
      return cellSet;
   }
   
   ColorListener bgrColorListener = new ColorListener()
   {
      @Override
      public void colorChoosen(ColorEvent e)
      {
         bgrColor = new Color3f(e.getSelectedColor());
         if (renderingParams.getShadingMode() == RenderingParams.BACKGROUND)
         {
            boolean detach = CellSetGeometry.this.postdetach();
            renderingParams.setDiffuseColor(bgrColor);
            if (appearance != null && appearance.getColoringAttributes() != null)
               appearance.getColoringAttributes().setColor(bgrColor);
            material.setAmbientColor(bgrColor);
            material.setDiffuseColor(bgrColor);
            material.setEmissiveColor(bgrColor);
            ignoreUpdate = false;
            updateGeometry();
            if(detach) CellSetGeometry.this.postattach();
         }
      }
   };

   public void setOwnParams()
   {
      if (ownParams == null)
         return;
      activeParams = ownParams;
      dataMappingParams = activeParams.getDataMappingParams();
      renderingParams   = activeParams.getRenderingParams();
      transformParams   = activeParams.getTransformParams();
      updateGeometry();
   }

   public void setParentParams(CellSetDisplayParams parentParams)
   {
      if (parentParams == null)
         return;
      this.parentParams = parentParams;
   }

   public void inheritParams()
   {
      if (parentParams == null)
         return;
      activeParams = parentParams;
      dataMappingParams = activeParams.getDataMappingParams();
      renderingParams   = activeParams.getRenderingParams();
      transformParams   = activeParams.getTransformParams();
      updateGeometry();
   }

   public ColorListener getBgrColorListener()
   {
      return bgrColorListener;
   }

   public GeometryObject2DStruct getGeometryObj2DStruct()
   {
      return outObj2DStruct;
   }

}
