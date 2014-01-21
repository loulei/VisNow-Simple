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

import javax.media.j3d.BranchGroup;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.IndexedLineStripArray;
import javax.media.j3d.LineStripArray;
import javax.media.j3d.PointArray;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.Color3f;
import org.apache.log4j.Logger;
import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.geometries.objects.generics.OpenAppearance;
import pl.edu.icm.visnow.geometries.objects.generics.OpenBranchGroup;
import pl.edu.icm.visnow.geometries.objects.generics.OpenColoringAttributes;
import pl.edu.icm.visnow.geometries.objects.generics.OpenLineAttributes;
import pl.edu.icm.visnow.geometries.objects.generics.OpenShape3D;
import pl.edu.icm.visnow.geometries.parameters.DataMappingParams;
import pl.edu.icm.visnow.geometries.parameters.RegularField3dParams;
import pl.edu.icm.visnow.geometries.parameters.RegularFieldDisplayParams;
import pl.edu.icm.visnow.geometries.parameters.TransformParams;
import pl.edu.icm.visnow.geometries.utils.ColorMapper;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.render.RenderEvent;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.render.RenderEventListener;
import pl.edu.icm.visnow.lib.utils.field.RegularFieldProjection;
import pl.edu.icm.visnow.lib.utils.field.SliceRegularField;

/**
 *
 * @author Krzysztof S. Nowinski
 * <p> University of Warsaw, ICM
 */
public class RegularField3DGeometry extends RegularFieldGeometry {

    protected RegularField3dParams mapParams = new RegularField3dParams();
    protected RegularField2DGeometry[][] faces = new RegularField2DGeometry[3][2];
    protected RegularField[][] faceFields = new RegularField[3][2];
    protected OpenShape3D outlineShape = new OpenShape3D();
    protected OpenLineAttributes boxLineAttr = new OpenLineAttributes();
    protected OpenColoringAttributes boxColorAttr = new OpenColoringAttributes();
    protected OpenAppearance boxApp = new OpenAppearance();
    protected OpenShape3D gridShape = new OpenShape3D();
    protected int currentFaceMode = RegularField3dParams.SLICE;
    protected RegularFieldDisplayParams faceParams;
    protected float[] boxVerts;
    protected IndexedLineStripArray box = null;
    protected int nGridVerts;
    protected int[][] gridCrds;
    protected float[] gridVerts;
    protected GeometryArray grid = null;
    protected int[] gridIndices = null;
    protected byte[] cols = null;
    static Logger logger = Logger.getLogger(RegularField3DGeometry.class);

    public RegularField3DGeometry(String name) {
        super(name);
        boxApp.setLineAttributes(boxLineAttr);
        boxApp.setColoringAttributes(boxColorAttr);
        geometries.addChild(outlineShape);
        geometries.addChild(gridShape);
        renderEventListener = new RenderEventListener() {
            @Override
            public void renderExtentChanged(RenderEvent e) {
                if (ignoreUpdate)
                   return;
                if (e.getUpdateExtent() == RenderEvent.COLORS)
                    updateColors();
                else if (e.getUpdateExtent() == RenderEvent.COORDS)
                    updateCoords();
                else if (e.getUpdateExtent() != RenderEvent.GEOMETRY)
                    updateDataMap();
                else
                    updateGeometry();
            }
        };
    }

    /**
     * Get the value of field
     *
     * @return the value of field
     */
    public Field getInField() {
        return field;
    }

    /**
     * Set the value of field
     *
     * @param field new value of field
     */
    @Override
    public boolean setField(RegularField inField) {
        super.setField(inField);
        faceParams = new RegularFieldDisplayParams(null, fieldDisplayParams.getMappingParams(),
                fieldDisplayParams.getDisplayParams(),
                new TransformParams());
        mapParams = fieldDisplayParams.getContent3DParams();
        mapParams.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent evt) {
                updateGeometry();
            }
        });
        dataMappingParams = fieldDisplayParams.getMappingParams();
        dataMappingParams.addRenderEventListener(new RenderEventListener() {
            public void renderExtentChanged(RenderEvent e) {
                updateColors();
            }
        });


        if (inField == null || inField.getDims() == null || inField.getDims().length != 3)
            return false;
        dims = inField.getDims();
        nNodes = inField.getNNodes();
        coords = inField.getCoords();
        this.field = inField;
        faceFields[0][0] = SliceRegularField.sliceField(inField, 0, 0);
        faceFields[0][1] = SliceRegularField.sliceField(inField, 0, dims[0] - 1);
        faceFields[1][0] = SliceRegularField.sliceField(inField, 1, 0);
        faceFields[1][1] = SliceRegularField.sliceField(inField, 1, dims[1] - 1);
        faceFields[2][0] = SliceRegularField.sliceField(inField, 2, 0);
        faceFields[2][1] = SliceRegularField.sliceField(inField, 2, dims[2] - 1);
        makeGrid();
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 2; j++) {
                //remove old listeners (with references to old faces)
                if (faces[i][j] != null) faces[i][j].clearParamListeners();
                faces[i][j] = new RegularField2DGeometry("");
                faces[i][j].setField(faceFields[i][j], false);
                faces[i][j].setFieldDisplayParams(faceParams);
            }
        faces[1][0].setSurfaceOrientation(false);
        faces[0][1].setSurfaceOrientation(false);
        faces[2][1].setSurfaceOrientation(false);
        return true;
    }

    public void setMapParams(RegularField3dParams mapParams) {
        this.mapParams = mapParams;
    }

    private void updateBoxCoords() {
        boolean detach = geometry.postdetach();
        if (coords == null) {
            float[][] af = field.getAffine();
            for (int i = 0; i < 3; i++) {
                boxVerts[i] = af[3][i];
                boxVerts[i + 3] = af[3][i] + (dims[0] - 1) * af[0][i];
                boxVerts[i + 6] = af[3][i] + (dims[1] - 1) * af[1][i];
                boxVerts[i + 9] = af[3][i] + (dims[1] - 1) * af[1][i] + (dims[0] - 1) * af[0][i];
                boxVerts[i + 12] = af[3][i] + (dims[2] - 1) * af[2][i];
                boxVerts[i + 15] = af[3][i] + (dims[2] - 1) * af[2][i] + (dims[0] - 1) * af[0][i];
                boxVerts[i + 18] = af[3][i] + (dims[2] - 1) * af[2][i] + (dims[1] - 1) * af[1][i];
                boxVerts[i + 21] = af[3][i] + (dims[2] - 1) * af[2][i] + (dims[1] - 1) * af[1][i] + (dims[0] - 1) * af[0][i];
            }
        } else {
            int k = 0;
            int l = 0, s = 3;
            for (int i = 0; i < dims[0]; i++, l += s)
                for (int j = 0; j < 3; j++, k++)
                    boxVerts[k] = coords[l + j];
            l = 3 * dims[0] * (dims[1] - 1);
            for (int i = 0; i < dims[0]; i++, l += s)
                for (int j = 0; j < 3; j++, k++)
                    boxVerts[k] = coords[l + j];
            l = 3 * dims[0] * dims[1] * (dims[2] - 1);
            for (int i = 0; i < dims[0]; i++, l += s)
                for (int j = 0; j < 3; j++, k++)
                    boxVerts[k] = coords[l + j];
            l = 3 * dims[0] * dims[1] * (dims[2] - 1) + 3 * dims[0] * (dims[1] - 1);
            for (int i = 0; i < dims[0]; i++, l += s)
                for (int j = 0; j < 3; j++, k++)
                    boxVerts[k] = coords[l + j];

            // i1 axis edges
            s = 3 * dims[0];
            l = 0;
            for (int i = 0; i < dims[1]; i++, l += s)
                for (int j = 0; j < 3; j++, k++)
                    boxVerts[k] = coords[l + j];
            l = 3 * (dims[0] - 1);
            for (int i = 0; i < dims[1]; i++, l += s)
                for (int j = 0; j < 3; j++, k++)
                    boxVerts[k] = coords[l + j];
            l = 3 * dims[0] * dims[1] * (dims[2] - 1);
            for (int i = 0; i < dims[1]; i++, l += s)
                for (int j = 0; j < 3; j++, k++)
                    boxVerts[k] = coords[l + j];
            l = 3 * dims[0] * dims[1] * (dims[2] - 1) + 3 * (dims[0] - 1);
            for (int i = 0; i < dims[1]; i++, l += s)
                for (int j = 0; j < 3; j++, k++)
                    boxVerts[k] = coords[l + j];

            // i2 axis edges
            s = 3 * dims[0] * dims[1];
            l = 0;
            for (int i = 0; i < dims[2]; i++, l += s)
                for (int j = 0; j < 3; j++, k++)
                    boxVerts[k] = coords[l + j];
            l = 3 * (dims[0] - 1);
            for (int i = 0; i < dims[2]; i++, l += s)
                for (int j = 0; j < 3; j++, k++)
                    boxVerts[k] = coords[l + j];
            l = 3 * dims[0] * (dims[1] - 1);
            for (int i = 0; i < dims[2]; i++, l += s)
                for (int j = 0; j < 3; j++, k++)
                    boxVerts[k] = coords[l + j];
            l = 3 * dims[0] * (dims[1] - 1) + 3 * (dims[0] - 1);
            for (int i = 0; i < dims[2]; i++, l += s)
                for (int j = 0; j < 3; j++, k++)
                    boxVerts[k] = coords[l + j];
        }
        box.setCoordinates(0, boxVerts);
        if (detach) geometry.postattach();
    }

    private void makeOutlineBox() {
        boolean detach = geometry.postdetach();
        outlineShape.removeAllGeometries();
        if (field == null) {
            if (detach) geometry.postattach();
            return;
        }

        if (coords == null) {
            boxVerts = new float[24];
            box = new IndexedLineStripArray(8, GeometryArray.COORDINATES, 24, new int[]{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2});

            box.setCoordinateIndices(0, new int[]{0, 1, 2, 3, 4, 5, 6, 7, 0, 2, 1, 3, 4, 6, 5, 7, 0, 4, 1, 5, 2, 6, 3, 7});
        } else {
            boxVerts = new float[3 * 4 * (dims[0] + dims[1] + dims[2])];
            box = new IndexedLineStripArray(4 * (dims[0] + dims[1] + dims[2]),
                    GeometryArray.COORDINATES,
                    4 * (dims[0] + dims[1] + dims[2]),
                    new int[]{
                dims[0], dims[0], dims[0], dims[0],
                dims[1], dims[1], dims[1], dims[1],
                dims[2], dims[2], dims[2], dims[2]
            });

            int[] cInd = new int[4 * (dims[0] + dims[1] + dims[2])];
            for (int i = 0; i < cInd.length; i++)
                cInd[i] = i;
            box.setCoordinateIndices(0, cInd);
        }
        box.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);
        updateBoxCoords();

        boxLineAttr.setLineWidth(mapParams.getBoxLineWidth());
        boxColorAttr.setColor(new Color3f(mapParams.getBoxColor()));
        outlineShape.addGeometry(box);
        outlineShape.setAppearance(boxApp);
        if (detach) geometry.postattach();
    }

    private void makeFaces() {
        RegularField s = null;
        if (mapParams.getDataMap() == RegularField3dParams.SLICE) {
            faceFields[0][0] = SliceRegularField.sliceField(field, 0, 0);
            faceFields[0][1] = SliceRegularField.sliceField(field, 0, dims[0] - 1);
            faceFields[1][0] = SliceRegularField.sliceField(field, 1, 0);
            faceFields[1][1] = SliceRegularField.sliceField(field, 1, dims[1] - 1);
            faceFields[2][0] = SliceRegularField.sliceField(field, 2, 0);
            faceFields[2][1] = SliceRegularField.sliceField(field, 2, dims[2] - 1);
        } else {
            RegularFieldProjection projection = new RegularFieldProjection();
            s = projection.fieldProjection(field, 0, mapParams.getDataMap(), 0);
            faceFields[0][0].clearData();
            faceFields[0][1].clearData();
            faceFields[0][0].setData(s.getData());
            faceFields[0][1].setData(s.getData());
            s = projection.fieldProjection(field, 1, mapParams.getDataMap(), 0);
            faceFields[1][0].clearData();
            faceFields[1][1].clearData();
            faceFields[1][0].setData(s.getData());
            faceFields[1][1].setData(s.getData());
            s = projection.fieldProjection(field, 2, mapParams.getDataMap(), 0);
            faceFields[2][0].clearData();
            faceFields[2][1].clearData();
            faceFields[2][0].setData(s.getData());
            faceFields[2][1].setData(s.getData());
        }
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 2; j++)
                faces[i][j].setField(faceFields[i][j], false);
    }

    private void updateGridCoords() {
        boolean detach = geometry.postdetach();
        if (coords == null) {
            float[][] af = field.getAffine();
            int n = 0;
            for (int j = 0; j < nGridVerts; j++) {
                int i = gridIndices[j];
                int i0 = i % dims[0];
                i /= dims[0];
                int i1 = i % dims[1];
                int i2 = i / dims[1];
                for (int m = 0; m < 3; m++, n++)
                    gridVerts[n] = af[3][m] + i0 * af[0][m] + i1 * af[1][m] + i2 * af[2][m];
            }
        } else {
            int n = 0;
            for (int j = 0; j < nGridVerts; j++) {
                int i = gridIndices[j];
                for (int m = 0; m < 3; m++, n++)
                    gridVerts[n] = coords[3 * i + m];
            }
        }
        if (mapParams.getGridType() == RegularField3dParams.GRID_TYPE_LINES)
            grid.setCoordinates(0, gridVerts);
        else if (mapParams.getGridType() == RegularField3dParams.GRID_TYPE_POINTS)
            grid.setCoordinates(0, gridVerts);

        if (detach) geometry.postattach();
    }

    private void makeGrid() {
        boolean detach = geometry.postdetach();
        gridShape.removeAllGeometries();
        if (field == null || mapParams.getGridType() < RegularField3dParams.GRID_TYPE_POINTS) {
            if (detach) geometry.postattach();
            return;
        }
        int grDens = (dims[0] + dims[1] + dims[2]) / mapParams.getGridLines();
        if (grDens < 1)
            grDens = 1;
        int[] gridDens = new int[]{
            ((int)(dims[0] / grDens)) <2 ? 2 : dims[0] / grDens , ((int)(dims[1] / grDens)) <2 ? 2 : dims[1] / grDens, ((int)(dims[2] / grDens) <2) ? 2 : dims[2] / grDens 
        };
        gridCrds = new int[3][];
        for (int i = 0; i < gridCrds.length; i++)
            if (gridDens[i] <= 1)
                gridCrds[i] = new int[]{
                    0, dims[i] - 1
                };
            else {
                int l = (dims[i] - 1) / gridDens[i] + 1;
                gridCrds[i] = new int[(dims[i] - 1) / l + 1];
                int m = (dims[i] - 1) % l;
                for (int j = 0, k = 0; j < gridCrds[i].length; j++) {
                    gridCrds[i][j] = k;
                    k += l;
                    if (j < m)
                        k += 1;
                }
            }
        if (mapParams.getGridType() == RegularField3dParams.GRID_TYPE_LINES) {
            int nGridLines = gridCrds[0].length * gridCrds[1].length + gridCrds[0].length * gridCrds[2].length + gridCrds[1].length * gridCrds[2].length;
            nGridVerts = gridCrds[0].length * gridCrds[1].length * dims[2] + gridCrds[0].length * gridCrds[2].length * dims[1] + gridCrds[1].length * gridCrds[2].length * dims[0];
            gridIndices = new int[nGridVerts];
            int[] gridPolylines = new int[nGridLines];
            int n = 0, m = 0;
            for (int i = 0; i < gridCrds[1].length; i++)
                for (int j = 0; j < gridCrds[0].length; j++, m++) {
                    gridPolylines[m] = dims[2];
                    for (int k = 0, l = gridCrds[1][i] * dims[0] + gridCrds[0][j]; k < dims[2]; k++, l += dims[0] * dims[1], n++)
                        gridIndices[n] = l;
                }
            for (int i = 0; i < gridCrds[2].length; i++)
                for (int j = 0; j < gridCrds[0].length; j++, m++) {
                    gridPolylines[m] = dims[1];
                    for (int k = 0, l = gridCrds[2][i] * dims[0] * dims[1] + gridCrds[0][j]; k < dims[1]; k++, l += dims[0], n++)
                        gridIndices[n] = l;
                }
            for (int i = 0; i < gridCrds[2].length; i++)
                for (int j = 0; j < gridCrds[1].length; j++, m++) {
                    gridPolylines[m] = dims[0];
                    for (int k = 0, l = (gridCrds[2][i] * dims[1] + gridCrds[1][j]) * dims[0]; k < dims[0]; k++, l += 1, n++)
                        gridIndices[n] = l;
                }

            grid = new LineStripArray(nGridVerts,
                    GeometryArray.COORDINATES | GeometryArray.COLOR_4,
                    gridPolylines);
        } else if (mapParams.getGridType() == RegularField3dParams.GRID_TYPE_POINTS) {
            nGridVerts = gridDens[0] * gridDens[1] * gridDens[2];
            gridIndices = new int[nGridVerts];
            for (int i = 0, n = 0; i < gridCrds[2].length; i++)
                for (int j = 0; j < gridCrds[1].length; j++)
                    for (int k = 0; k < gridCrds[0].length; k++, n++)
                        gridIndices[n] = (gridCrds[2][i] * dims[1] + gridCrds[1][j]) * dims[0] + gridCrds[0][k];
            grid = new PointArray(nGridVerts, GeometryArray.COORDINATES | GeometryArray.COLOR_4);
        }
        gridVerts = new float[3 * nGridVerts];
        grid.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
        grid.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);
        gridShape.addGeometry(grid);
        gridShape.setAppearance(fieldDisplayParams.getDisplayParams().getLineAppearance());
        updateGridCoords();
        updateColors();
        if (detach) geometry.postattach();
    }

    public void updateGeometry(RegularField inField) {
//      geometry.removeAllChildren();
        if (!setField(inField) || mapParams == null)
            return;
        updateGeometry();
    }

    public void createGeometry(RegularField inField) {
        if (!setField(inField))
            return;
        updateGeometry(inField);
    }

    @Override
    public void createGeometry(Field inField) {
        if (!(inField instanceof RegularField))
            return;
        createGeometry((RegularField) inField);
    }

    public OpenBranchGroup getGeometry(RegularField inField) {
        createGeometry(inField);
        return geometry;
    }

    @Override
    public OpenBranchGroup getGeometry(Field inField) {
        if (!(inField instanceof RegularField))
            return null;
        return getGeometry((RegularField) inField);
    }

    public void updateColors() {
//      boolean detach = geometry.postdetach();
        if (mapParams.getGridType() == RegularField3dParams.GRID_TYPE_POINTS
                || mapParams.getGridType() == RegularField3dParams.GRID_TYPE_LINES) {
            cols = ColorMapper.mapColorsIndexed(field, dataMappingParams, gridIndices, renderingParams.getAmbientColor(), cols);
            grid.setColors(0, cols);
        } else {
            boxColorAttr.setColor(new Color3f(mapParams.getBoxColor()));
        }
//      if(detach) geometry.postattach();
    }

    @Override
    public void updateDataMap() {
        if (dataMappingParams.getColorMode() == DataMappingParams.COLORMAPPED
                || dataMappingParams.getColorMode() == DataMappingParams.COLORMAPPED2D
                || dataMappingParams.getColorMode() == DataMappingParams.COLORED)
            updateColors();
    }

    @Override
    public void updateCoords(float[] coords) {
        this.coords = coords;
        updateCoords(true);
    }

    @Override
    public void updateCoords() {
        updateCoords(!ignoreUpdate);
    }

    @Override
    public void updateCoords(boolean force) {
        if(!force || field == null)
            return;
        
        boolean detach = geometry.postdetach();
        coords = field.getCoords();
        if (mapParams.getGridType() == RegularField3dParams.GRID_TYPE_OUTLINE
                || mapParams.getGridType() == RegularField3dParams.GRID_TYPE_POINTS)
            makeOutlineBox();
        else
            outlineShape.removeAllGeometries();
        updateBoxCoords();
        if (mapParams.getGridType() >= RegularField3dParams.GRID_TYPE_POINTS)
            updateGridCoords();
        for (int i = 0; i < 3; i++) {
            SliceRegularField.sliceCoordsUpdate(field, i, 0, faceFields[i][0]);
            SliceRegularField.sliceCoordsUpdate(field, i, dims[i] - 1, faceFields[i][1]);
            for (int j = 0; j < 2; j++)
                faces[i][j].updateCoords();
        }
        if (detach) geometry.postattach();
    }

    @Override
    public void updateGeometry(Field inField) {
        if (inField instanceof RegularField)
            updateGeometry((RegularField) inField);
    }

    @Override
    public void updateGeometry() {
      if (ignoreUpdate)
         return;
      
        boolean detach = geometry.postdetach();

        int i = 0, j = 0;
        try {
            for (i = geometries.numChildren() - 1; i >= 0; i--)
                if (geometries.getChild(i) instanceof BranchGroup)
                    geometries.removeChild(i);
            if (mapParams == null)
                return;
            if (mapParams.getGridType() == RegularField3dParams.GRID_TYPE_OUTLINE
                    || mapParams.getGridType() == RegularField3dParams.GRID_TYPE_POINTS)
                makeOutlineBox();
            else
                outlineShape.removeAllGeometries();
            makeGrid();
            if (mapParams.getDataMap() != currentFaceMode) {
                currentFaceMode = mapParams.getDataMap();
                makeFaces();
            }
            for (i = 0; i < 3; i++)
                for (j = 0; j < 2; j++)
                    if (mapParams.isSurFaces(i, j) || mapParams.isGridFaces(i, j))
                        geometries.addChild(faces[i][j].getGeometry(mapParams.isSurFaces(i, j),
                                mapParams.isGridFaces(i, j)));
        } catch (javax.media.j3d.RestrictedAccessException ex) {
            logger.error("access " + i + " " + j);
            ex.printStackTrace();
        } catch (javax.media.j3d.MultipleParentException ex) {
            logger.error("parent " + i + " " + j);
        }
        
      if(geometries.getParent() == null)
            transformedGeometries.addChild(geometries);
      if(transformedGeometries.getParent() == null)
            geometry.addChild(transformedGeometries);
        

        if (detach) geometry.postattach();
    }

    @Override
    public Field getField() {
        return field;
    }
}
