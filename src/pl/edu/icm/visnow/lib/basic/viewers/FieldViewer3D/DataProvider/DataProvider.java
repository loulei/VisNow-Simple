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

package pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.DataProvider;

import java.awt.Color;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import pl.edu.icm.visnow.datamaps.ColorMap;
import pl.edu.icm.visnow.datamaps.ColorMapManager;
import pl.edu.icm.visnow.datamaps.colormap1d.ColorMap1D;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.datasets.RegularFieldInterpolator;
import pl.edu.icm.visnow.datasets.dataarrays.BitArray;
import pl.edu.icm.visnow.datasets.dataarrays.ComplexDataArray;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.datasets.dataarrays.LogicDataArray;
import pl.edu.icm.visnow.datasets.dataarrays.StringDataArray;
import pl.edu.icm.visnow.geometries.objects.GeometryObject;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.GeometryParams;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.ViewPanels.OrthosliceNumberChangedEvent;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;
import pl.edu.icm.visnow.lib.utils.numeric.NumericalMethods;

/**
 * @author  Bartosz Borucki (babor@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class DataProvider implements DataProviderParamsListener {

    private RegularField inField = null;
    private RegularField auxField = null;
    private RegularField overlayField = null;
    private DataProviderParams params = new DataProviderParams();
    private DataProviderUI ui = null;
    private BufferedImage[] orthosliceImages = new BufferedImage[3];
    private BufferedImage[] orthosliceOverlayImages = new BufferedImage[3];
    private BufferedImage customSliceImage = null;
    private BufferedImage[] customOrthoSlicesImages = new BufferedImage[3];
    private BufferedImage[] customOrthoSlicesOverlays = new BufferedImage[3];
    private BufferedImage singleImage = null;
    private GeometryObject customSlice3DPlane = new GeometryObject("CustomSlice");
    private GeometryObject customOrthoSlices3DPlanes = new GeometryObject("CustomOrthoSlices");
    private SliceImage sliceImage = null;
    private SliceImage overlaySliceImage = null;
    private GeometryParams geometryParams = null;
    private RegularFieldIsoline[] mapIsoline = new RegularFieldIsoline[3];
    private RegularFieldIsoline[][] mapIsolines = new RegularFieldIsoline[3][];
    private Plane3D customPlane = null;
    private Plane3D[] customOrthoPlanes = new Plane3D[3];
    private ColorMap colorMap = null;
    private ColorMap1D overlayColorMap = ColorMapManager.getInstance().getColorMap1D(ColorMapManager.COLORMAP1D_RAINBOW);
    private int[] colorMapLUT = null;
    private int[] overlayColorMapLUT = overlayColorMap.getRGBColorTable();

    public DataProvider() {
        this(null);
    }

    public DataProvider(final RegularField field) {
        SwingInstancer.swingRun(new Runnable() {
            @Override
            public void run() {
                ui = new DataProviderUI();
                DataProvider.this.inField = field;
                for (int i = 0; i < 3; i++) {
                    orthosliceImages[i] = null;
                    orthosliceOverlayImages[i] = null;
                    customOrthoSlicesImages[i] = null;
                    customOrthoSlicesOverlays[i] = null;
                }
                singleImage = null;

                customPlane = new Plane3D(params.getCustomPlanePoint(), params.getCustomPlaneVector(), inField);
                for (int i = 0; i < 3; i++) {
                    customOrthoPlanes[i] = new Plane3D(params.getCustomOrthoPlanesPoint(), params.getCustomOrthoPlanesVector(i), inField);
                }

                params.addDataProviderParamsListener(DataProvider.this);
                params.getDataMappingParams().setInField(field);
                DataProvider.this.colorMap = params.getDataMappingParams().getColorMap0Params().getMap();
                DataProvider.this.colorMapLUT = DataProvider.this.colorMap.getRGBColorTable();
                ui.setDataProvider(DataProvider.this);        

                customSlice3DPlane.addNode(customPlane.getBoundedPlane3D());
                for (int i = 0; i < 3; i++) {
                    customOrthoSlices3DPlanes.addNode(customOrthoPlanes[i].getBoundedPlane3D());
                }                
            }
        });
    }

    public DataProviderUI getUI() {
        return ui;
    }

    public BufferedImage getOrthoSlice(int axis) {
        if (inField == null || axis < 0 || axis > 2) {
            return null;
        }

        if (orthosliceImages[axis] == null) {
            updateOrthosliceImage(axis, true);
        }

        return orthosliceImages[axis];
    }

    public BufferedImage getOrthoSliceOverlay(int axis) {
        if (axis < 0 || axis > 2 || (!params.isSimpleOverlay() && overlayField == null)) {
            return null;
        }

        if (orthosliceOverlayImages[axis] == null) {
            updateOrthosliceOverlay(axis, true);
        }

        return orthosliceOverlayImages[axis];
    }

    public void resetIsolines() {
        for (int i = 0; i < 3; i++) {
            mapIsoline[i] = null;
        }
        fireDataProviderOrthosliceUpdated(-1);
    }

    public void resetOverlays() {
        for (int i = 0; i < 3; i++) {
            orthosliceOverlayImages[i] = null;
            customOrthoSlicesOverlays[i] = null;
        }
        fireDataProviderOverlayUpdated(-1);
    }

    public void updateAll() {
        updateOrthosliceImages();
        updateOrthosliceIsolines();
        updateOrthosliceOverlays();
        updateCustomPlane();
        updateCustomOrthoPlanes();
        updateCustomOrthoPlanesOverlays();
        updateSingleImage();
    }

    public void updateOrthosliceImages() {
        if (inField == null) {
            return;
        }

        for (int i = 0; i < 3; i++) {
            updateOrthosliceImage(i, true);
        }
        fireDataProviderOrthosliceUpdated(-1);
    }

    public void updateOrthosliceIsolines()
   {
      for (int i = 0; i < 3; i++)
      {
         updateOrthosliceIsoline(i, true);
         updateOrthosliceIsolines(i, true);
      }
      fireDataProviderOrthosliceUpdated(-1);
    }

    public void updateOrthosliceOverlays() {
        for (int i = 0; i < 3; i++) {
            updateOrthosliceOverlay(i, true);
        }
        fireDataProviderOverlayUpdated(-1);
    }

   public void updateOrthosliceIsoline(int axis, boolean silent)
   {
      if (auxField != null && auxField.getDims().length == 3)
      {
         int[] fDims = inField.getDims();
         int[] dims = auxField.getDims();
         for (int i = 0; i < 3; i++)
         {
            if (dims[i] != fDims[i])
            {
               mapIsoline[axis] = null;
               return;
            }
         }

         float[] fData = auxField.get2DSliceData(0, axis, params.getOrthosliceNumber(axis));
         int[] dd = new int[2];
         switch (axis)
         {
            case 0:
               dd[0] = dims[1];
               dd[1] = dims[2];
               break;
            case 1:
               dd[0] = dims[0];
               dd[1] = dims[2];
               break;
            case 2:
               dd[0] = dims[0];
               dd[1] = dims[1];
               break;
         }
         mapIsoline[axis] = new RegularFieldIsoline(params.getIsolineThreshold(), dd, fData);
         if (!silent)
            fireDataProviderOrthosliceUpdated(axis);
      }
   }

   public void updateOrthosliceIsolines(int axis, boolean silent)
   {
      if (inField != null && inField.getDims().length == 3)
      {
         int[] dims = inField.getDims();

         float[] fData = inField.get2DSliceData(0, axis, params.getOrthosliceNumber(axis));
         int[] dd = new int[2];
         switch (axis)
         {
            case 0:
               dd[0] = dims[1];
               dd[1] = dims[2];
               break;
            case 1:
               dd[0] = dims[0];
               dd[1] = dims[2];
               break;
            case 2:
               dd[0] = dims[0];
               dd[1] = dims[1];
               break;
         }
         if (params.getIsolineThresholds() != null && params.getIsolineThresholds().length == 2)
         {
            mapIsolines[axis] = new RegularFieldIsoline[2];
            for (int i = 0; i < params.getIsolineThresholds().length; i++)
               mapIsolines[axis][i] = new RegularFieldIsoline(params.getIsolineThresholds()[i], dd, fData);
         }
         if (!silent)
            fireDataProviderOrthosliceUpdated(axis);
      }
   }

    public ArrayList<float[][]> getIsoline(int axis) {
        if (mapIsoline != null && mapIsoline[axis] != null) 
            return mapIsoline[axis].getLines();
        return null;
    }
    
   public ArrayList<float[][]>[] getIsolines(int axis)
   {
      if (mapIsolines[axis] == null || mapIsolines[axis].length != 2)
         return null;
      ArrayList<float[][]>[] isolines = new ArrayList[2];
      for (int i = 0; i < isolines.length; i++)
         isolines[i] = mapIsolines[axis][i].getLines();
      return isolines;
   }

    public void updateOrthosliceOverlay(int axis, boolean silent) {
        orthosliceOverlayImages[axis] = null;        
        
        if(params.isSimpleOverlay() && inField != null) {
            int component = params.getSimpleOverlayComponent();
            int[] dims = inField.getDims();
            int w = 0, h = 0, slice;
            slice = params.getOrthosliceNumber(axis);
            if (slice < 0) {
                slice = 0;
            }
            if (slice >= dims[axis]) {
                slice = dims[axis] - 1;
            }
            
            
            float[] emptyPixel = new float[4];
            float[] overlaidPixel = new float[4];            
            Color c = params.getSimpleOverlayColor();            
            
            if(params.isSimpleOverlayMask()) {
                if(params.isSimpleOverlayInvert()) {
                    emptyPixel = new float[]{c.getRed(),c.getGreen(),c.getBlue(),0.0f};
                    overlaidPixel = new float[]{0.0f,0.0f,0.0f,255.0f};
                } else {
                    emptyPixel = new float[]{0.0f,0.0f,0.0f,255.0f};
                    overlaidPixel = new float[]{c.getRed(),c.getGreen(),c.getBlue(),0.0f};
                }
            } else {
                if(params.isSimpleOverlayInvert()) {
                    emptyPixel = new float[]{c.getRed(),c.getGreen(),c.getBlue(),255.0f};
                    overlaidPixel = new float[]{0.0f,0.0f,0.0f,0.0f};
                } else {
                    emptyPixel = new float[]{0.0f,0.0f,0.0f,0.0f};
                    overlaidPixel = new float[]{c.getRed(),c.getGreen(),c.getBlue(),255.0f};
                }
            }
            
            float low = params.getSimpleOverlayLow();
            float up = params.getSimpleOverlayUp();

            switch(inField.getData(component).getType()) {
                case DataArray.FIELD_DATA_BYTE:
                    byte[] bData = inField.getData(component).getBData();
                    int b;
                    switch (axis) {
                        case 0:
                            w = dims[1];
                            h = dims[2];
                            orthosliceOverlayImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                            for (int j = 0; j < h; j++) {
                                for (int i = 0; i < w; i++) {
                                    b = (int)(bData[j * dims[0] * dims[1] + i * dims[0] + slice]&0xFF);
                                    if(b < low || b > up) {
                                        orthosliceOverlayImages[axis].getRaster().setPixel(i, j, emptyPixel);                                
                                    } else {
                                        orthosliceOverlayImages[axis].getRaster().setPixel(i, j, overlaidPixel);                                
                                    }
                                }
                            }
                            break;
                        case 1:
                            w = dims[0];
                            h = dims[2];
                            orthosliceOverlayImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                            for (int j = 0; j < h; j++) {
                                for (int i = 0; i < w; i++) {
                                    b = (int)(bData[j * dims[0] * dims[1] + slice * dims[0] + i]&0xFF);
                                    if(b < low || b > up) {
                                        orthosliceOverlayImages[axis].getRaster().setPixel(i, j, emptyPixel);                                
                                    } else {
                                        orthosliceOverlayImages[axis].getRaster().setPixel(i, j, overlaidPixel);                                
                                    }
                                }
                            }
                            break;
                        case 2:
                            w = dims[0];
                            h = dims[1];
                            orthosliceOverlayImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                            for (int j = 0; j < h; j++) {
                                for (int i = 0; i < w; i++) {
                                    b = (int)(bData[slice * dims[0] * dims[1] + j * dims[0] + i]&0xFF);
                                    if(b < low || b > up) {
                                        orthosliceOverlayImages[axis].getRaster().setPixel(i, j, emptyPixel);                                
                                    } else {
                                        orthosliceOverlayImages[axis].getRaster().setPixel(i, j, overlaidPixel);                                
                                    }
                                }
                            }
                            break;
                    }
                    break;
                case DataArray.FIELD_DATA_SHORT:
                    short[] sData = inField.getData(component).getSData();
                    short s;
                    switch (axis) {
                        case 0:
                            w = dims[1];
                            h = dims[2];
                            orthosliceOverlayImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                            for (int j = 0; j < h; j++) {
                                for (int i = 0; i < w; i++) {
                                    s = sData[j * dims[0] * dims[1] + i * dims[0] + slice];
                                    if(s < low || s > up) {
                                        orthosliceOverlayImages[axis].getRaster().setPixel(i, j, emptyPixel);                                
                                    } else {
                                        orthosliceOverlayImages[axis].getRaster().setPixel(i, j, overlaidPixel);                                
                                    }
                                }
                            }
                            break;
                        case 1:
                            w = dims[0];
                            h = dims[2];
                            orthosliceOverlayImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                            for (int j = 0; j < h; j++) {
                                for (int i = 0; i < w; i++) {
                                    s = sData[j * dims[0] * dims[1] + slice * dims[0] + i];
                                    if(s < low || s > up) {
                                        orthosliceOverlayImages[axis].getRaster().setPixel(i, j, emptyPixel);                                
                                    } else {
                                        orthosliceOverlayImages[axis].getRaster().setPixel(i, j, overlaidPixel);                                
                                    }
                                }
                            }
                            break;
                        case 2:
                            w = dims[0];
                            h = dims[1];
                            orthosliceOverlayImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                            for (int j = 0; j < h; j++) {
                                for (int i = 0; i < w; i++) {
                                    s = sData[slice * dims[0] * dims[1] + j * dims[0] + i];
                                    if(s < low || s > up) {
                                        orthosliceOverlayImages[axis].getRaster().setPixel(i, j, emptyPixel);                                
                                    } else {
                                        orthosliceOverlayImages[axis].getRaster().setPixel(i, j, overlaidPixel);                                
                                    }
                                }
                            }
                            break;
                    }
                    break;
                case DataArray.FIELD_DATA_INT:
                    int[] iData = inField.getData(component).getIData();
                    int ii;
                    switch (axis) {
                        case 0:
                            w = dims[1];
                            h = dims[2];
                            orthosliceOverlayImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                            for (int j = 0; j < h; j++) {
                                for (int i = 0; i < w; i++) {
                                    ii = iData[j * dims[0] * dims[1] + i * dims[0] + slice];
                                    if(ii< low || ii > up) {
                                        orthosliceOverlayImages[axis].getRaster().setPixel(i, j, emptyPixel);                                
                                    } else {
                                        orthosliceOverlayImages[axis].getRaster().setPixel(i, j, overlaidPixel);                                
                                    }
                                }
                            }
                            break;
                        case 1:
                            w = dims[0];
                            h = dims[2];
                            orthosliceOverlayImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                            for (int j = 0; j < h; j++) {
                                for (int i = 0; i < w; i++) {
                                    ii = iData[j * dims[0] * dims[1] + slice * dims[0] + i];
                                    if(ii < low || ii > up) {
                                        orthosliceOverlayImages[axis].getRaster().setPixel(i, j, emptyPixel);                                
                                    } else {
                                        orthosliceOverlayImages[axis].getRaster().setPixel(i, j, overlaidPixel);                                
                                    }
                                }
                            }
                            break;
                        case 2:
                            w = dims[0];
                            h = dims[1];
                            orthosliceOverlayImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                            for (int j = 0; j < h; j++) {
                                for (int i = 0; i < w; i++) {
                                    ii = iData[slice * dims[0] * dims[1] + j * dims[0] + i];
                                    if(ii < low || ii > up) {
                                        orthosliceOverlayImages[axis].getRaster().setPixel(i, j, emptyPixel);                                
                                    } else {
                                        orthosliceOverlayImages[axis].getRaster().setPixel(i, j, overlaidPixel);                                
                                    }
                                }
                            }
                            break;
                    }
                    break;
                case DataArray.FIELD_DATA_FLOAT:
                    float[] fData = inField.getData(component).getFData();
                    float f;
                    switch (axis) {
                        case 0:
                            w = dims[1];
                            h = dims[2];
                            orthosliceOverlayImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                            for (int j = 0; j < h; j++) {
                                for (int i = 0; i < w; i++) {
                                    f = fData[j * dims[0] * dims[1] + i * dims[0] + slice];
                                    if(f < low || f > up) {
                                        orthosliceOverlayImages[axis].getRaster().setPixel(i, j, emptyPixel);                                
                                    } else {
                                        orthosliceOverlayImages[axis].getRaster().setPixel(i, j, overlaidPixel);                                
                                    }
                                }
                            }
                            break;
                        case 1:
                            w = dims[0];
                            h = dims[2];
                            orthosliceOverlayImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                            for (int j = 0; j < h; j++) {
                                for (int i = 0; i < w; i++) {
                                    f = fData[j * dims[0] * dims[1] + slice * dims[0] + i];
                                    if(f < low || f > up) {
                                        orthosliceOverlayImages[axis].getRaster().setPixel(i, j, emptyPixel);                                
                                    } else {
                                        orthosliceOverlayImages[axis].getRaster().setPixel(i, j, overlaidPixel);                                
                                    }
                                }
                            }
                            break;
                        case 2:
                            w = dims[0];
                            h = dims[1];
                            orthosliceOverlayImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                            for (int j = 0; j < h; j++) {
                                for (int i = 0; i < w; i++) {
                                    f = fData[slice * dims[0] * dims[1] + j * dims[0] + i];
                                    if(f < low || f > up) {
                                        orthosliceOverlayImages[axis].getRaster().setPixel(i, j, emptyPixel);                                
                                    } else {
                                        orthosliceOverlayImages[axis].getRaster().setPixel(i, j, overlaidPixel);                                
                                    }
                                }
                            }
                            break;
                    }
                    break;
                case DataArray.FIELD_DATA_DOUBLE:
                    double[] dData = inField.getData(component).getDData();
                    double d;
                    switch (axis) {
                        case 0:
                            w = dims[1];
                            h = dims[2];
                            orthosliceOverlayImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                            for (int j = 0; j < h; j++) {
                                for (int i = 0; i < w; i++) {
                                    d = dData[j * dims[0] * dims[1] + i * dims[0] + slice];
                                    if(d < low || d > up) {
                                        orthosliceOverlayImages[axis].getRaster().setPixel(i, j, emptyPixel);                                
                                    } else {
                                        orthosliceOverlayImages[axis].getRaster().setPixel(i, j, overlaidPixel);                                
                                    }
                                }
                            }
                            break;
                        case 1:
                            w = dims[0];
                            h = dims[2];
                            orthosliceOverlayImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                            for (int j = 0; j < h; j++) {
                                for (int i = 0; i < w; i++) {
                                    d = dData[j * dims[0] * dims[1] + slice * dims[0] + i];
                                    if(d < low || d > up) {
                                        orthosliceOverlayImages[axis].getRaster().setPixel(i, j, emptyPixel);                                
                                    } else {
                                        orthosliceOverlayImages[axis].getRaster().setPixel(i, j, overlaidPixel);                                
                                    }
                                }
                            }
                            break;
                        case 2:
                            w = dims[0];
                            h = dims[1];
                            orthosliceOverlayImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                            for (int j = 0; j < h; j++) {
                                for (int i = 0; i < w; i++) {
                                    d = dData[slice * dims[0] * dims[1] + j * dims[0] + i];
                                    if(d < low || d > up) {
                                        orthosliceOverlayImages[axis].getRaster().setPixel(i, j, emptyPixel);                                
                                    } else {
                                        orthosliceOverlayImages[axis].getRaster().setPixel(i, j, overlaidPixel);                                
                                    }
                                }
                            }
                            break;
                    }
                    break;
                    
            }
            
            
        } else {
            if (overlayField != null && overlaySliceImage != null && inField.getDims().length == 3)
                orthosliceOverlayImages[axis] = overlaySliceImage.getSlice(axis, params.getOrthosliceNumber(axis));
        }            
            
        if (!silent) {
            fireDataProviderOverlayUpdated(axis);
        }

    }

    public Object getFieldValue(int i, int j, int k) {
        if (inField == null || inField.getDims().length != 3) {
            return null;
        }

        if (params.getMappingMode() == DataProviderParams.MAPPING_MODE_RGB)
            return null;

        int[] dims = inField.getDims();

        if(i < 0) i = 0;
        if(i >= dims[0]) i = dims[0]-1;
        if(j < 0) j = 0;
        if(j >= dims[1]) j = dims[1]-1;
        if(k < 0) k = 0;
        if(k >= dims[2]) k = dims[2]-1;

        int component = 0;
        
        switch(params.getMappingMode()) {
            case DataProviderParams.MAPPING_MODE_FAST:
                component = params.getSingleComponent();
                break;
            case DataProviderParams.MAPPING_MODE_COLORMAPPED:
                component = params.getDataMappingParams().getColorMap0Params().getDataComponent();
                break;
            case DataProviderParams.MAPPING_MODE_RGB:
                //TODO
                break;                
        }
        
        DataArray da = inField.getData(component); 
        int vlen = da.getVeclen();
        switch (da.getType()) {
            case DataArray.FIELD_DATA_BYTE:
                byte[] bData = da.getBData();
                if(vlen == 1) {
                    return bData[dims[0]*dims[1]*k + dims[0]*j + i];
                } else {
                    byte[] out = new byte[vlen];
                    for (int v = 0; v < vlen; v++) {
                        out[v] = bData[(dims[0]*dims[1]*k + dims[0]*j + i)*vlen + v];
                    }
                    return out;                    
                }                    
            case DataArray.FIELD_DATA_SHORT:
                short[] sData = da.getSData();
                if(vlen == 1) {
                    return sData[dims[0]*dims[1]*k + dims[0]*j + i];
                } else {
                    short[] out = new short[vlen];
                    for (int v = 0; v < vlen; v++) {
                        out[v] = sData[(dims[0]*dims[1]*k + dims[0]*j + i)*vlen + v];
                    }
                    return out;                    
                }                    
            case DataArray.FIELD_DATA_INT:
                int[] iData = da.getIData();
                if(vlen == 1) {
                    return iData[dims[0]*dims[1]*k + dims[0]*j + i]&0xff;
                } else {
                    int[] out = new int[vlen];
                    for (int v = 0; v < vlen; v++) {
                        out[v] = iData[(dims[0]*dims[1]*k + dims[0]*j + i)*vlen + v];
                    }
                    return out;                    
                }                    
            case DataArray.FIELD_DATA_FLOAT:
                float[] fData = da.getFData();
                if(vlen == 1) {
                    return fData[dims[0]*dims[1]*k + dims[0]*j + i];
                } else {
                    float[] out = new float[vlen];
                    for (int v = 0; v < vlen; v++) {
                        out[v] = fData[(dims[0]*dims[1]*k + dims[0]*j + i)*vlen + v];
                    }
                    return out;                    
                }                    
            case DataArray.FIELD_DATA_DOUBLE:
                double[] dData = da.getDData();
                if(vlen == 1) {
                    return dData[dims[0]*dims[1]*k + dims[0]*j + i];
                } else {
                    double[] out = new double[vlen];
                    for (int v = 0; v < vlen; v++) {
                        out[v] = dData[(dims[0]*dims[1]*k + dims[0]*j + i)*vlen + v];
                    }
                    return out;                    
                }                    
            case DataArray.FIELD_DATA_STRING:
                String[] strData = ((StringDataArray)da).getStringData();
                if(vlen == 1) {
                    return strData[dims[0]*dims[1]*k + dims[0]*j + i];
                } else {
                    String[] out = new String[vlen];
                    for (int v = 0; v < vlen; v++) {
                        out[v] = strData[(dims[0]*dims[1]*k + dims[0]*j + i)*vlen + v];
                    }
                    return out;                    
                }                    
            case DataArray.FIELD_DATA_LOGIC:
                BitArray bitData = ((LogicDataArray)da).getBitArray();
                if(vlen == 1) {
                    return bitData.getValueAtIndex(dims[0]*dims[1]*k + dims[0]*j + i);
                } else {
                    boolean[] out = new boolean[vlen];
                    for (int v = 0; v < vlen; v++) {
                        out[v] = bitData.getValueAtIndex((dims[0]*dims[1]*k + dims[0]*j + i)*vlen + v);
                    }
                    return out;                    
                }                    
            case DataArray.FIELD_DATA_COMPLEX:
                float[] fReData = ((ComplexDataArray)da).getFRealData();
                float[] fImData = ((ComplexDataArray)da).getFImagData();
                float[][] out = new float[vlen][];
                if(vlen == 1) {
                    out[0] = new float[]{fReData[dims[0]*dims[1]*k + dims[0]*j + i],fImData[dims[0]*dims[1]*k + dims[0]*j + i]};                    
                } else {
                    for (int v = 0; v < vlen; v++) {
                        out[v] = new float[]{fReData[(dims[0]*dims[1]*k + dims[0]*j + i)*vlen + v],fImData[(dims[0]*dims[1]*k + dims[0]*j + i)*vlen + v]};
                    }
                }                    
                return out;                    
            default:
                return null;
        }
    }

    public int[] getOrthoSliceRGB(int axis, int x, int y) {
        if (inField == null || sliceImage == null || inField.getDims().length != 3) {
            return null;
        }

        int[] pixel = null;
        pixel = orthosliceImages[axis].getRaster().getPixel(x, y, pixel);
        return pixel;
    }

    public void updateOrthosliceImage(int axis, boolean silent) {
        if (inField == null || sliceImage == null || inField.getDims().length != 3) {
            return;
        }

        int component = 0;
        int mode = DataProviderParams.MAPPING_MODE_COLORMAPPED;
        if (params.getMappingMode() == DataProviderParams.MAPPING_MODE_COLORMAPPED) {
            mode = DataProviderParams.MAPPING_MODE_COLORMAPPED;
            component = params.getDataMappingParams().getColorMap0Params().getDataComponent();
        } else if (params.getMappingMode() == DataProviderParams.MAPPING_MODE_RGB) {
            mode = DataProviderParams.MAPPING_MODE_RGB;
        } else if (params.getMappingMode() == DataProviderParams.MAPPING_MODE_FAST) {
            mode = DataProviderParams.MAPPING_MODE_FAST;
            component = params.getSingleComponent();
        }

        int[] dims = null;
        int w, h, slice;
        dims = inField.getDims();
        slice = params.getOrthosliceNumber(axis);
        if (slice < 0) {
            slice = 0;
        }
        if (slice >= dims[axis]) {
            slice = dims[axis] - 1;
        }

        switch (mode) {
            case DataProviderParams.MAPPING_MODE_COLORMAPPED:
                float low = params.getDataMappingParams().getColorMap0Params().getDataMin();
                float up = params.getDataMappingParams().getColorMap0Params().getDataMax();
                int colorMapSize = colorMapLUT.length - 1;
                float cs = (float) colorMapSize / (up - low);
                int c;
                double val1, val;
                int veclen = inField.getData(component).getVeclen();

                switch (inField.getData(component).getType()) {
                    case DataArray.FIELD_DATA_BYTE:
                        byte[] bData = inField.getData(component).getBData();
                        switch (axis) {
                            case 0:
                                w = dims[1];
                                h = dims[2];
                                orthosliceImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                                for (int j = 0; j < h; j++) {
                                    for (int i = 0; i < w; i++) {
                                        if(veclen == 1) {
                                            c = (int) (((float) (0xFF & bData[j * dims[0] * dims[1] + i * dims[0] + slice]) - low) * cs);
                                        } else {
                                            val = 0;
                                            for (int v = 0; v < veclen; v++) {
                                                val1 = (double) (0xFF & bData[(j * dims[0] * dims[1] + i * dims[0] + slice)*veclen + v]);
                                                val += val1*val1;
                                            }
                                            c = (int) ((Math.sqrt(val) - low) * cs);
                                        }
                                        if (c < 0) {
                                            c = 0;
                                        }
                                        if (c > colorMapSize) {
                                            c = colorMapSize;
                                        }
                                        orthosliceImages[axis].setRGB(i, j, colorMapLUT[c]);
                                    }
                                }
                                break;
                            case 1:
                                w = dims[0];
                                h = dims[2];
                                orthosliceImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                                for (int j = 0; j < h; j++) {
                                    for (int i = 0; i < w; i++) {
                                        if(veclen == 1) {
                                            c = (int) (((float) (0xFF & bData[j * dims[0] * dims[1] + slice * dims[0] + i]) - low) * cs);
                                        } else {
                                            val = 0;
                                            for (int v = 0; v < veclen; v++) {
                                                val1 = (double) (0xFF & bData[(j * dims[0] * dims[1] + slice * dims[0] + i)*veclen + v]);
                                                val += val1*val1;
                                            }
                                            c = (int) ((Math.sqrt(val) - low) * cs);
                                        }
                                        if (c < 0) {
                                            c = 0;
                                        }
                                        if (c > colorMapSize) {
                                            c = colorMapSize;
                                        }
                                        orthosliceImages[axis].setRGB(i, j, colorMapLUT[c]);
                                    }
                                }
                                break;
                            case 2:
                                w = dims[0];
                                h = dims[1];
                                orthosliceImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                                for (int j = 0; j < h; j++) {
                                    for (int i = 0; i < w; i++) {
                                        if(veclen == 1) {
                                            c = (int) (((float) (0xFF & bData[slice * dims[0] * dims[1] + j * dims[0] + i]) - low) * cs);
                                        } else {
                                            val = 0;
                                            for (int v = 0; v < veclen; v++) {
                                                val1 = (double) (0xFF & bData[(slice * dims[0] * dims[1] + j * dims[0] + i)*veclen + v]);
                                                val += val1*val1;
                                            }
                                            c = (int) ((Math.sqrt(val) - low) * cs);
                                        }
                                        if (c < 0) {
                                            c = 0;
                                        }
                                        if (c > colorMapSize) {
                                            c = colorMapSize;
                                        }
                                        orthosliceImages[axis].setRGB(i, j, colorMapLUT[c]);
                                    }
                                }
                                break;
                        }
                        break;
                    case DataArray.FIELD_DATA_SHORT:
                        short[] sData = inField.getData(component).getSData();
                        switch (axis) {
                            case 0:
                                w = dims[1];
                                h = dims[2];
                                orthosliceImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                                for (int j = 0; j < h; j++) {
                                    for (int i = 0; i < w; i++) {
                                        if(veclen == 1) {
                                            c = (int) (((float) sData[j * dims[0] * dims[1] + i * dims[0] + slice] -low) * cs);
                                        } else {
                                            val = 0;
                                            for (int v = 0; v < veclen; v++) {
                                                val1 = (double) sData[(j * dims[0] * dims[1] + i * dims[0] + slice)*veclen + v];
                                                val += val1*val1;
                                            }
                                            c = (int) ((Math.sqrt(val) - low) * cs);
                                        }
                                        if (c < 0) {
                                            c = 0;
                                        }
                                        if (c > colorMapSize) {
                                            c = colorMapSize;
                                        }
                                        orthosliceImages[axis].setRGB(i, j, colorMapLUT[c]);
                                    }
                                }
                                break;
                            case 1:
                                w = dims[0];
                                h = dims[2];
                                orthosliceImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                                for (int j = 0; j < h; j++) {
                                    for (int i = 0; i < w; i++) {
                                        if(veclen == 1) {
                                            c = (int) (((float) sData[j * dims[0] * dims[1] + slice * dims[0] + i] - low) * cs);
                                        } else {
                                            val = 0;
                                            for (int v = 0; v < veclen; v++) {
                                                val1 = (double) sData[(j * dims[0] * dims[1] + slice * dims[0] + i)*veclen + v];
                                                val += val1*val1;
                                            }
                                            c = (int) ((Math.sqrt(val) - low) * cs);
                                        }
                                        if (c < 0) {
                                            c = 0;
                                        }
                                        if (c > colorMapSize) {
                                            c = colorMapSize;
                                        }
                                        orthosliceImages[axis].setRGB(i, j, colorMapLUT[c]);
                                    }
                                }
                                break;
                            case 2:
                                w = dims[0];
                                h = dims[1];
                                orthosliceImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                                for (int j = 0; j < h; j++) {
                                    for (int i = 0; i < w; i++) {
                                        if(veclen == 1) {
                                            c = (int) (((float) sData[slice * dims[0] * dims[1] + j * dims[0] + i] - low) * cs);
                                        } else {
                                            val = 0;
                                            for (int v = 0; v < veclen; v++) {
                                                val1 = (double) sData[(slice * dims[0] * dims[1] + j * dims[0] + i)*veclen + v];
                                                val += val1*val1;
                                            }
                                            c = (int) ((Math.sqrt(val) - low) * cs);
                                        }
                                        if (c < 0) {
                                            c = 0;
                                        }
                                        if (c > colorMapSize) {
                                            c = colorMapSize;
                                        }
                                        orthosliceImages[axis].setRGB(i, j, colorMapLUT[c]);
                                    }
                                }
                                break;
                        }
                        break;
                    case DataArray.FIELD_DATA_INT:
                        int[] iData = inField.getData(component).getIData();
                        switch (axis) {
                            case 0:
                                w = dims[1];
                                h = dims[2];
                                orthosliceImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                                for (int j = 0; j < h; j++) {
                                    for (int i = 0; i < w; i++) {
                                        if(veclen == 1) {
                                            c = (int) (((float) iData[j * dims[0] * dims[1] + i * dims[0] + slice] - low) * cs);
                                        } else {
                                            val = 0;
                                            for (int v = 0; v < veclen; v++) {
                                                val1 = (double) iData[(j * dims[0] * dims[1] + i * dims[0] + slice)*veclen + v];
                                                val += val1*val1;
                                            }
                                            c = (int) ((Math.sqrt(val) - low) * cs);
                                        }
                                        if (c < 0) {
                                            c = 0;
                                        }
                                        if (c > colorMapSize) {
                                            c = colorMapSize;
                                        }
                                        orthosliceImages[axis].setRGB(i, j, colorMapLUT[c]);
                                    }
                                }
                                break;
                            case 1:
                                w = dims[0];
                                h = dims[2];
                                orthosliceImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                                for (int j = 0; j < h; j++) {
                                    for (int i = 0; i < w; i++) {
                                        if(veclen == 1) {
                                            c = (int) (((float) iData[j * dims[0] * dims[1] + slice * dims[0] + i] - low) * cs);
                                        } else {
                                            val = 0;
                                            for (int v = 0; v < veclen; v++) {
                                                val1 = (double) iData[(j * dims[0] * dims[1] + slice * dims[0] + i)*veclen + v];
                                                val += val1*val1;
                                            }
                                            c = (int) ((Math.sqrt(val) - low) * cs);
                                        }
                                        if (c < 0) {
                                            c = 0;
                                        }
                                        if (c > colorMapSize) {
                                            c = colorMapSize;
                                        }
                                        orthosliceImages[axis].setRGB(i, j, colorMapLUT[c]);
                                    }
                                }
                                break;
                            case 2:
                                w = dims[0];
                                h = dims[1];
                                orthosliceImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                                for (int j = 0; j < h; j++) {
                                    for (int i = 0; i < w; i++) {
                                        if(veclen == 1) {
                                            c = (int) (((float) iData[slice * dims[0] * dims[1] + j * dims[0] + i] - low) * cs);
                                        } else {
                                            val = 0;
                                            for (int v = 0; v < veclen; v++) {
                                                val1 = (double) iData[(slice * dims[0] * dims[1] + j * dims[0] + i)*veclen + v];
                                                val += val1*val1;
                                            }
                                            c = (int) ((Math.sqrt(val) - low) * cs);
                                        }
                                        if (c < 0) {
                                            c = 0;
                                        }
                                        if (c > colorMapSize) {
                                            c = colorMapSize;
                                        }
                                        orthosliceImages[axis].setRGB(i, j, colorMapLUT[c]);
                                    }
                                }
                                break;
                        }
                        break;
                    case DataArray.FIELD_DATA_FLOAT:
                        float[] fData = inField.getData(component).getFData();
                        switch (axis) {
                            case 0:
                                w = dims[1];
                                h = dims[2];
                                orthosliceImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                                for (int j = 0; j < h; j++) {
                                    for (int i = 0; i < w; i++) {
                                        if(veclen == 1) {
                                            c = (int) ((fData[j * dims[0] * dims[1] + i * dims[0] + slice] - low) * cs);
                                        } else {
                                            val = 0;
                                            for (int v = 0; v < veclen; v++) {
                                                val1 = fData[(j * dims[0] * dims[1] + i * dims[0] + slice)*veclen + v];
                                                val += val1*val1;
                                            }
                                            c = (int) ((Math.sqrt(val) - low) * cs);
                                        }                                        
                                        if (c < 0) {
                                            c = 0;
                                        }
                                        if (c > colorMapSize) {
                                            c = colorMapSize;
                                        }
                                        orthosliceImages[axis].setRGB(i, j, colorMapLUT[c]);
                                    }
                                }
                                break;
                            case 1:
                                w = dims[0];
                                h = dims[2];
                                orthosliceImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                                for (int j = 0; j < h; j++) {
                                    for (int i = 0; i < w; i++) {
                                        if(veclen == 1) {
                                            c = (int) ((fData[j * dims[0] * dims[1] + slice * dims[0] + i] - low) * cs);
                                        } else {
                                            val = 0;
                                            for (int v = 0; v < veclen; v++) {
                                                val1 = fData[(j * dims[0] * dims[1] + slice * dims[0] + i)*veclen + v];
                                                val += val1*val1;
                                            }
                                            c = (int) ((Math.sqrt(val) - low) * cs);
                                        }                                        
                                        if (c < 0) {
                                            c = 0;
                                        }
                                        if (c > colorMapSize) {
                                            c = colorMapSize;
                                        }
                                        orthosliceImages[axis].setRGB(i, j, colorMapLUT[c]);
                                    }
                                }
                                break;
                            case 2:
                                w = dims[0];
                                h = dims[1];
                                orthosliceImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                                for (int j = 0; j < h; j++) {
                                    for (int i = 0; i < w; i++) {
                                        if(veclen == 1) {
                                            c = (int) ((fData[slice * dims[0] * dims[1] + j * dims[0] + i] - low) * cs);
                                        } else {
                                            val = 0;
                                            for (int v = 0; v < veclen; v++) {
                                                val1 = fData[(slice * dims[0] * dims[1] + j * dims[0] + i)*veclen + v];
                                                val += val1*val1;
                                            }
                                            c = (int) ((Math.sqrt(val) - low) * cs);
                                        }                                                                                
                                        if (c < 0) {
                                            c = 0;
                                        }
                                        if (c > colorMapSize) {
                                            c = colorMapSize;
                                        }
                                        orthosliceImages[axis].setRGB(i, j, colorMapLUT[c]);
                                    }
                                }
                                break;
                        }
                        break;
                    case DataArray.FIELD_DATA_DOUBLE:
                        double[] dData = inField.getData(component).getDData();
                        switch (axis) {
                            case 0:
                                w = dims[1];
                                h = dims[2];
                                orthosliceImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                                for (int j = 0; j < h; j++) {
                                    for (int i = 0; i < w; i++) {
                                        if(veclen == 1) {
                                            c = (int) (((float) dData[j * dims[0] * dims[1] + i * dims[0] + slice] - low) * cs);
                                        } else {
                                            val = 0;
                                            for (int v = 0; v < veclen; v++) {
                                                val1 = dData[(j * dims[0] * dims[1] + i * dims[0] + slice)*veclen + v];
                                                val += val1*val1;
                                            }
                                            c = (int) ((Math.sqrt(val) - low) * cs);
                                        }                                                                                
                                        if (c < 0) {
                                            c = 0;
                                        }
                                        if (c > colorMapSize) {
                                            c = colorMapSize;
                                        }
                                        orthosliceImages[axis].setRGB(i, j, colorMapLUT[c]);
                                    }
                                }
                                break;
                            case 1:
                                w = dims[0];
                                h = dims[2];
                                orthosliceImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                                for (int j = 0; j < h; j++) {
                                    for (int i = 0; i < w; i++) {
                                        if(veclen == 1) {
                                            c = (int) (((float) dData[j * dims[0] * dims[1] + slice * dims[0] + i] - low) * cs);
                                        } else {
                                            val = 0;
                                            for (int v = 0; v < veclen; v++) {
                                                val1 = dData[(j * dims[0] * dims[1] + slice * dims[0] + i)*veclen + v];
                                                val += val1*val1;
                                            }
                                            c = (int) ((Math.sqrt(val) - low) * cs);
                                        }                                                                                
                                        if (c < 0) {
                                            c = 0;
                                        }
                                        if (c > colorMapSize) {
                                            c = colorMapSize;
                                        }
                                        orthosliceImages[axis].setRGB(i, j, colorMapLUT[c]);
                                    }
                                }
                                break;
                            case 2:
                                w = dims[0];
                                h = dims[1];
                                orthosliceImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                                for (int j = 0; j < h; j++) {
                                    for (int i = 0; i < w; i++) {
                                        if(veclen == 1) {
                                            c = (int) (((float) dData[slice * dims[0] * dims[1] + j * dims[0] + i] - low) * cs);
                                        } else {
                                            val = 0;
                                            for (int v = 0; v < veclen; v++) {
                                                val1 = dData[(slice * dims[0] * dims[1] + j * dims[0] + i)*veclen + v];
                                                val += val1*val1;
                                            }
                                            c = (int) ((Math.sqrt(val) - low) * cs);
                                        }                                                                                
                                        if (c < 0) {
                                            c = 0;
                                        }
                                        if (c > colorMapSize) {
                                            c = colorMapSize;
                                        }
                                        orthosliceImages[axis].setRGB(i, j, colorMapLUT[c]);
                                    }
                                }
                                break;
                        }
                        break;
                    case DataArray.FIELD_DATA_COMPLEX:
                        float[] fAbsData = ((ComplexDataArray) inField.getData(component)).getFAbsData();
                        switch (axis) {
                            case 0:
                                w = dims[1];
                                h = dims[2];
                                orthosliceImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                                for (int j = 0; j < h; j++) {
                                    for (int i = 0; i < w; i++) {
                                        if(veclen == 1) {
                                            c = (int) ((fAbsData[j * dims[0] * dims[1] + i * dims[0] + slice] - low) * cs);
                                        } else {
                                            val = 0;
                                            for (int v = 0; v < veclen; v++) {
                                                val1 = fAbsData[(j * dims[0] * dims[1] + i * dims[0] + slice)*veclen + v];
                                                val += val1*val1;
                                            }
                                            c = (int) ((Math.sqrt(val) - low) * cs);
                                        }                                        
                                        if (c < 0) {
                                            c = 0;
                                        }
                                        if (c > colorMapSize) {
                                            c = colorMapSize;
                                        }
                                        orthosliceImages[axis].setRGB(i, j, colorMapLUT[c]);
                                    }
                                }
                                break;
                            case 1:
                                w = dims[0];
                                h = dims[2];
                                orthosliceImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                                for (int j = 0; j < h; j++) {
                                    for (int i = 0; i < w; i++) {
                                        if(veclen == 1) {
                                            c = (int) ((fAbsData[j * dims[0] * dims[1] + slice * dims[0] + i] - low) * cs);
                                        } else {
                                            val = 0;
                                            for (int v = 0; v < veclen; v++) {
                                                val1 = fAbsData[(j * dims[0] * dims[1] + slice * dims[0] + i)*veclen + v];
                                                val += val1*val1;
                                            }
                                            c = (int) ((Math.sqrt(val) - low) * cs);
                                        }                                        
                                        if (c < 0) {
                                            c = 0;
                                        }
                                        if (c > colorMapSize) {
                                            c = colorMapSize;
                                        }
                                        orthosliceImages[axis].setRGB(i, j, colorMapLUT[c]);
                                    }
                                }
                                break;
                            case 2:
                                w = dims[0];
                                h = dims[1];
                                orthosliceImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                                for (int j = 0; j < h; j++) {
                                    for (int i = 0; i < w; i++) {
                                        if(veclen == 1) {
                                            c = (int) ((fAbsData[slice * dims[0] * dims[1] + j * dims[0] + i] - low) * cs);
                                        } else {
                                            val = 0;
                                            for (int v = 0; v < veclen; v++) {
                                                val1 = fAbsData[(slice * dims[0] * dims[1] + j * dims[0] + i)*veclen + v];
                                                val += val1*val1;
                                            }
                                            c = (int) ((Math.sqrt(val) - low) * cs);
                                        }                                        
                                        if (c < 0) {
                                            c = 0;
                                        }
                                        if (c > colorMapSize) {
                                            c = colorMapSize;
                                        }
                                        orthosliceImages[axis].setRGB(i, j, colorMapLUT[c]);
                                    }
                                }
                                break;
                        }
                        break;
                    case DataArray.FIELD_DATA_LOGIC:
                        BitArray logicData = ((LogicDataArray) inField.getData(component)).getBitArray();
                        switch (axis) {
                            case 0:
                                w = dims[1];
                                h = dims[2];
                                orthosliceImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                                for (int j = 0; j < h; j++) {
                                    for (int i = 0; i < w; i++) {
                                        if(veclen == 1) {
                                            c = (int) (((float) (0xFF & logicData.getByteValueAtIndex(j * dims[0] * dims[1] + i * dims[0] + slice)) - low) * cs);
                                        } else {
                                            val = 0;
                                            for (int v = 0; v < veclen; v++) {
                                                val1 = (float) (0xFF & logicData.getByteValueAtIndex((j * dims[0] * dims[1] + i * dims[0] + slice)*veclen + v));
                                                val += val1*val1;
                                            }
                                            c = (int) ((Math.sqrt(val) - low) * cs);
                                        }                                        
                                        if (c < 0) {
                                            c = 0;
                                        }
                                        if (c > colorMapSize) {
                                            c = colorMapSize;
                                        }
                                        orthosliceImages[axis].setRGB(i, j, colorMapLUT[c]);
                                    }
                                }
                                break;
                            case 1:
                                w = dims[0];
                                h = dims[2];
                                orthosliceImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                                for (int j = 0; j < h; j++) {
                                    for (int i = 0; i < w; i++) {
                                        if(veclen == 1) {
                                            c = (int) (((float) (0xff & logicData.getByteValueAtIndex(j * dims[0] * dims[1] + slice * dims[0] + i)) - low) * cs);
                                        } else {
                                            val = 0;
                                            for (int v = 0; v < veclen; v++) {
                                                val1 = (float) (0xFF & logicData.getByteValueAtIndex((j * dims[0] * dims[1] + slice * dims[0] + i)*veclen + v));
                                                val += val1*val1;
                                            }
                                            c = (int) ((Math.sqrt(val) - low) * cs);
                                        }                                        
                                        if (c < 0) {
                                            c = 0;
                                        }
                                        if (c > colorMapSize) {
                                            c = colorMapSize;
                                        }
                                        orthosliceImages[axis].setRGB(i, j, colorMapLUT[c]);
                                    }
                                }
                                break;
                            case 2:
                                w = dims[0];
                                h = dims[1];
                                orthosliceImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                                for (int j = 0; j < h; j++) {
                                    for (int i = 0; i < w; i++) {
                                        if(veclen == 1) {
                                            c = (int) (((float) (0xff & logicData.getByteValueAtIndex(slice * dims[0] * dims[1] + j * dims[0] + i)) - low) * cs);
                                        } else {
                                            val = 0;
                                            for (int v = 0; v < veclen; v++) {
                                                val1 = (float) (0xFF & logicData.getByteValueAtIndex((slice * dims[0] * dims[1] + j * dims[0] + i)*veclen + v));
                                                val += val1*val1;
                                            }
                                            c = (int) ((Math.sqrt(val) - low) * cs);
                                        }                                        
                                        if (c < 0) {
                                            c = 0;
                                        }
                                        if (c > colorMapSize) {
                                            c = colorMapSize;
                                        }
                                        orthosliceImages[axis].setRGB(i, j, colorMapLUT[c]);
                                    }
                                }
                                break;
                        }
                        break;
                    default:
                        return;
                }
                break;
            case DataProviderParams.MAPPING_MODE_RGB:
                sliceImage.setComponents(params.getRgbComponents());
                sliceImage.setWeights(params.getRgbComponentWeights());
                orthosliceImages[axis] = sliceImage.getSlice(axis, slice);
                break;
            case DataProviderParams.MAPPING_MODE_FAST:
                WritableRaster raster;
                float fMax = inField.getData(component).getMaxv();
                float fMin = inField.getData(component).getMinv();
                float fs = 255.0f / (fMax - fMin);

                switch (inField.getData(component).getType()) {
                    case DataArray.FIELD_DATA_BYTE:
                        byte[] bData = inField.getData(component).getBData();
                        switch (axis) {
                            case 0:
                                w = dims[1];
                                h = dims[2];
                                orthosliceImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
                                raster = orthosliceImages[axis].getRaster();
                                for (int j = 0; j < h; j++) {
                                    for (int i = 0; i < w; i++) {
                                        raster.setSample(i, j, 0, (int) (0xFF & bData[j * dims[0] * dims[1] + i * dims[0] + slice]));
                                    }
                                }
                                break;
                            case 1:
                                w = dims[0];
                                h = dims[2];
                                orthosliceImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
                                raster = orthosliceImages[axis].getRaster();
                                for (int j = 0; j < h; j++) {
                                    for (int i = 0; i < w; i++) {
                                        raster.setSample(i, j, 0, (int) (0xff & bData[j * dims[0] * dims[1] + slice * dims[0] + i]));
                                    }
                                }
                                break;
                            case 2:
                                w = dims[0];
                                h = dims[1];
                                orthosliceImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
                                raster = orthosliceImages[axis].getRaster();
                                for (int j = 0; j < h; j++) {
                                    for (int i = 0; i < w; i++) {
                                        raster.setSample(i, j, 0, (int) (0xff & bData[slice * dims[0] * dims[1] + j * dims[0] + i]));
                                    }
                                }
                                break;
                        }
                        break;
                    case DataArray.FIELD_DATA_SHORT:
                        short[] sData = inField.getData(component).getSData();
                        switch (axis) {
                            case 0:
                                w = dims[1];
                                h = dims[2];
                                orthosliceImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
                                raster = orthosliceImages[axis].getRaster();
                                for (int j = 0; j < h; j++) {
                                    for (int i = 0; i < w; i++) {
                                        raster.setSample(i, j, 0, ((float) (sData[j * dims[0] * dims[1] + i * dims[0] + slice]) - fMin) * fs);
                                    }
                                }
                                break;
                            case 1:
                                w = dims[0];
                                h = dims[2];
                                orthosliceImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
                                raster = orthosliceImages[axis].getRaster();
                                for (int j = 0; j < h; j++) {
                                    for (int i = 0; i < w; i++) {
                                        raster.setSample(i, j, 0, ((float) (sData[j * dims[0] * dims[1] + slice * dims[0] + i]) - fMin) * fs);
                                    }
                                }
                                break;
                            case 2:
                                w = dims[0];
                                h = dims[1];
                                orthosliceImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
                                raster = orthosliceImages[axis].getRaster();
                                for (int j = 0; j < h; j++) {
                                    for (int i = 0; i < w; i++) {
                                        raster.setSample(i, j, 0, ((float) (sData[slice * dims[0] * dims[1] + j * dims[0] + i]) - fMin) * fs);
                                    }
                                }
                                break;
                        }
                        break;
                    case DataArray.FIELD_DATA_INT:
                        int[] iData = inField.getData(component).getIData();
                        switch (axis) {
                            case 0:
                                w = dims[1];
                                h = dims[2];
                                orthosliceImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
                                raster = orthosliceImages[axis].getRaster();
                                for (int j = 0; j < h; j++) {
                                    for (int i = 0; i < w; i++) {
                                        raster.setSample(i, j, 0, ((float) iData[j * dims[0] * dims[1] + i * dims[0] + slice] - fMin) * fs);
                                    }
                                }
                                break;
                            case 1:
                                w = dims[0];
                                h = dims[2];
                                orthosliceImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
                                raster = orthosliceImages[axis].getRaster();
                                for (int j = 0; j < h; j++) {
                                    for (int i = 0; i < w; i++) {
                                        raster.setSample(i, j, 0, ((float) iData[j * dims[0] * dims[1] + slice * dims[0] + i] - fMin) * fs);
                                    }
                                }
                                break;
                            case 2:
                                w = dims[0];
                                h = dims[1];
                                orthosliceImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
                                raster = orthosliceImages[axis].getRaster();
                                for (int j = 0; j < h; j++) {
                                    for (int i = 0; i < w; i++) {
                                        raster.setSample(i, j, 0, ((float) iData[slice * dims[0] * dims[1] + j * dims[0] + i] - fMin) * fs);
                                    }
                                }
                                break;
                        }
                        break;
                    case DataArray.FIELD_DATA_FLOAT:
                        float[] fData = inField.getData(component).getFData();
                        switch (axis) {
                            case 0:
                                w = dims[1];
                                h = dims[2];
                                orthosliceImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
                                raster = orthosliceImages[axis].getRaster();
                                for (int j = 0; j < h; j++) {
                                    for (int i = 0; i < w; i++) {
                                        raster.setSample(i, j, 0, (fData[j * dims[0] * dims[1] + i * dims[0] + slice] - fMin) * fs);
                                    }
                                }
                                break;
                            case 1:
                                w = dims[0];
                                h = dims[2];
                                orthosliceImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
                                raster = orthosliceImages[axis].getRaster();
                                for (int j = 0; j < h; j++) {
                                    for (int i = 0; i < w; i++) {
                                        raster.setSample(i, j, 0, (fData[j * dims[0] * dims[1] + slice * dims[0] + i] - fMin) * fs);
                                    }
                                }
                                break;
                            case 2:
                                w = dims[0];
                                h = dims[1];
                                orthosliceImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
                                raster = orthosliceImages[axis].getRaster();
                                for (int j = 0; j < h; j++) {
                                    for (int i = 0; i < w; i++) {
                                        raster.setSample(i, j, 0, (fData[slice * dims[0] * dims[1] + j * dims[0] + i] - fMin) * fs);
                                    }
                                }
                                break;
                        }
                        break;
                    case DataArray.FIELD_DATA_DOUBLE:
                        double[] dData = inField.getData(component).getDData();
                        double dMax = (double) inField.getData(component).getMaxv();
                        double dMin = (double) inField.getData(component).getMinv();
                        double ds = 255.0 / (dMax - dMin);
                        switch (axis) {
                            case 0:
                                w = dims[1];
                                h = dims[2];
                                orthosliceImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
                                raster = orthosliceImages[axis].getRaster();
                                for (int j = 0; j < h; j++) {
                                    for (int i = 0; i < w; i++) {
                                        raster.setSample(i, j, 0, (dData[j * dims[0] * dims[1] + i * dims[0] + slice] - dMin) * ds);
                                    }
                                }
                                break;
                            case 1:
                                w = dims[0];
                                h = dims[2];
                                orthosliceImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
                                raster = orthosliceImages[axis].getRaster();
                                for (int j = 0; j < h; j++) {
                                    for (int i = 0; i < w; i++) {
                                        raster.setSample(i, j, 0, (dData[j * dims[0] * dims[1] + slice * dims[0] + i] - dMin) * ds);
                                    }
                                }
                                break;
                            case 2:
                                w = dims[0];
                                h = dims[1];
                                orthosliceImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
                                raster = orthosliceImages[axis].getRaster();
                                for (int j = 0; j < h; j++) {
                                    for (int i = 0; i < w; i++) {
                                        raster.setSample(i, j, 0, (dData[slice * dims[0] * dims[1] + j * dims[0] + i] - dMin) * ds);
                                    }
                                }
                                break;
                        }
                        break;
                    case DataArray.FIELD_DATA_COMPLEX:
                        float[] fAbsData = ((ComplexDataArray) inField.getData(component)).getFAbsData();
                        float fAbsMax = inField.getData(component).getMaxv();
                        float fAbsMin = inField.getData(component).getMinv();
                        float fAbss = 255.0f / (fAbsMax - fAbsMin);
                        switch (axis) {
                            case 0:
                                w = dims[1];
                                h = dims[2];
                                orthosliceImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
                                raster = orthosliceImages[axis].getRaster();
                                for (int j = 0; j < h; j++) {
                                    for (int i = 0; i < w; i++) {
                                        raster.setSample(i, j, 0, (fAbsData[j * dims[0] * dims[1] + i * dims[0] + slice] - fAbsMin) * fAbss);
                                    }
                                }
                                break;
                            case 1:
                                w = dims[0];
                                h = dims[2];
                                orthosliceImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
                                raster = orthosliceImages[axis].getRaster();
                                for (int j = 0; j < h; j++) {
                                    for (int i = 0; i < w; i++) {
                                        raster.setSample(i, j, 0, (fAbsData[j * dims[0] * dims[1] + slice * dims[0] + i] - fAbsMin) * fAbss);
                                    }
                                }
                                break;
                            case 2:
                                w = dims[0];
                                h = dims[1];
                                orthosliceImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
                                raster = orthosliceImages[axis].getRaster();
                                for (int j = 0; j < h; j++) {
                                    for (int i = 0; i < w; i++) {
                                        raster.setSample(i, j, 0, (fAbsData[slice * dims[0] * dims[1] + j * dims[0] + i] - fAbsMin) * fAbss);
                                    }
                                }
                                break;
                        }
                        break;
                    case DataArray.FIELD_DATA_LOGIC:
                        LogicDataArray data = (LogicDataArray) inField.getData(component);
                        switch (axis) {
                            case 0:
                                w = dims[1];
                                h = dims[2];
                                orthosliceImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
                                raster = orthosliceImages[axis].getRaster();
                                for (int j = 0; j < h; j++) {
                                    for (int i = 0; i < w; i++) {
                                        raster.setSample(i, j, 0, 255 * data.getByteValue(j * dims[0] * dims[1] + i * dims[0] + slice));
                                    }
                                }
                                break;
                            case 1:
                                w = dims[0];
                                h = dims[2];
                                orthosliceImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
                                raster = orthosliceImages[axis].getRaster();
                                for (int j = 0; j < h; j++) {
                                    for (int i = 0; i < w; i++) {
                                        raster.setSample(i, j, 0, 255 * data.getByteValue(j * dims[0] * dims[1] + slice * dims[0] + i));
                                    }
                                }
                                break;
                            case 2:
                                w = dims[0];
                                h = dims[1];
                                orthosliceImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
                                raster = orthosliceImages[axis].getRaster();
                                for (int j = 0; j < h; j++) {
                                    for (int i = 0; i < w; i++) {
                                        raster.setSample(i, j, 0, 255 * data.getByteValue(slice * dims[0] * dims[1] + j * dims[0] + i));
                                    }
                                }
                                break;
                        }
                        break;
                }
                break;
        }

        if (!silent) {
            fireDataProviderOrthosliceUpdated(axis);
        }
    }

    public void updateSingleImage() {
        if (inField == null || inField.getDims().length != 2) {
            return;
        }

        int component = 0;
        int mode = DataProviderParams.MAPPING_MODE_COLORMAPPED;
        if (params.getMappingMode() == DataProviderParams.MAPPING_MODE_COLORMAPPED) {
            mode = DataProviderParams.MAPPING_MODE_COLORMAPPED;
            //component = params.getSingleComponent();
            component = params.getDataMappingParams().getColorMap0Params().getDataComponent();
        } else if (params.getMappingMode() == DataProviderParams.MAPPING_MODE_RGB) {
            mode = DataProviderParams.MAPPING_MODE_RGB;
        } else if (params.getMappingMode() == DataProviderParams.MAPPING_MODE_FAST) {
            mode = DataProviderParams.MAPPING_MODE_FAST;
            component = params.getSingleComponent();
        }

        int[] dims = null;
        int w, h;
        dims = inField.getDims();
        if (dims == null || dims.length != 2) {
            return;
        }

        w = dims[0];
        h = dims[1];

        switch (mode) {
            case DataProviderParams.MAPPING_MODE_COLORMAPPED:

                float low = params.getDataMappingParams().getColorMap0Params().getDataMin();
                float up = params.getDataMappingParams().getColorMap0Params().getDataMax();
                int colorMapSize = colorMapLUT.length - 1;
                float cs = (float) colorMapSize / (up - low);

                float min = inField.getData(component).getMinv();
                float max = inField.getData(component).getMaxv();
                float s = 255.0f / (max - min);

                int c;

                switch (inField.getData(component).getType()) {
                    case DataArray.FIELD_DATA_BYTE:
                        byte[] bData = inField.getData(component).getBData();
                        singleImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                        for (int j = 0; j < h; j++) {
                            for (int i = 0; i < w; i++) {
                                c = (int) (((float) (0xFF & bData[j * dims[0] + i]) - low) * cs);
                                if (c < 0) {
                                    c = 0;
                                }
                                if (c > colorMapSize) {
                                    c = colorMapSize;
                                }
                                singleImage.setRGB(i, j, colorMapLUT[c]);
                            }
                        }
                        break;
                    case DataArray.FIELD_DATA_SHORT:
                        short[] sData = inField.getData(component).getSData();
                        singleImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                        for (int j = 0; j < h; j++) {
                            for (int i = 0; i < w; i++) {
                                c = (int) (((float) (sData[j * dims[0] + i]) - low) * cs);
                                if (c < 0) {
                                    c = 0;
                                }
                                if (c > colorMapSize) {
                                    c = colorMapSize;
                                }
                                singleImage.setRGB(i, j, colorMapLUT[c]);
                            }
                        }
                        break;
                    case DataArray.FIELD_DATA_INT:
                        int[] iData = inField.getData(component).getIData();
                        singleImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                        for (int j = 0; j < h; j++) {
                            for (int i = 0; i < w; i++) {
                                c = (int) (((float) (iData[j * dims[0] + i]) - low) * cs);
                                if (c < 0) {
                                    c = 0;
                                }
                                if (c > colorMapSize) {
                                    c = colorMapSize;
                                }
                                singleImage.setRGB(i, j, colorMapLUT[c]);
                            }
                        }
                        break;
                    case DataArray.FIELD_DATA_FLOAT:
                        float[] fData = inField.getData(component).getFData();
                        singleImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                        for (int j = 0; j < h; j++) {
                            for (int i = 0; i < w; i++) {
                                c = (int) (((fData[j * dims[0] + i]) - low) * cs);
                                if (c < 0) {
                                    c = 0;
                                }
                                if (c > colorMapSize) {
                                    c = colorMapSize;
                                }
                                singleImage.setRGB(i, j, colorMapLUT[c]);
                            }
                        }
                        break;
                    case DataArray.FIELD_DATA_DOUBLE:
                        double[] dData = inField.getData(component).getDData();
                        singleImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                        for (int j = 0; j < h; j++) {
                            for (int i = 0; i < w; i++) {
                                c = (int) (((dData[j * dims[0] + i]) - low) * cs);
                                if (c < 0) {
                                    c = 0;
                                }
                                if (c > colorMapSize) {
                                    c = colorMapSize;
                                }
                                singleImage.setRGB(i, j, colorMapLUT[c]);
                            }
                        }
                        break;
                    case DataArray.FIELD_DATA_COMPLEX:
                        float[] fAbsData = ((ComplexDataArray) inField.getData(component)).getFAbsData();
                        singleImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                        for (int j = 0; j < h; j++) {
                            for (int i = 0; i < w; i++) {
                                c = (int) (((fAbsData[j * dims[0] + i]) - low) * cs);
                                if (c < 0) {
                                    c = 0;
                                }
                                if (c > colorMapSize) {
                                    c = colorMapSize;
                                }
                                singleImage.setRGB(i, j, colorMapLUT[c]);
                            }
                        }
                        break;
                    case DataArray.FIELD_DATA_LOGIC:
                        BitArray logicData = ((LogicDataArray) inField.getData(component)).getBitArray();
                        singleImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                        for (int j = 0; j < h; j++) {
                            for (int i = 0; i < w; i++) {
                                c = (int) (((float) (0xFF & logicData.getByteValueAtIndex(j * dims[0] + i)) - low) * cs);
                                if (c < 0) {
                                    c = 0;
                                }
                                if (c > colorMapSize) {
                                    c = colorMapSize;
                                }
                                singleImage.setRGB(i, j, colorMapLUT[c]);
                            }
                        }
                        break;
                    default:
                        return;
                }
                break;
            case DataProviderParams.MAPPING_MODE_RGB:
                sliceImage.setComponents(params.getRgbComponents());
                sliceImage.setWeights(params.getRgbComponentWeights());
                singleImage = sliceImage.getSlice(2, 0);
                break;
            case DataProviderParams.MAPPING_MODE_FAST:
                WritableRaster raster;
                float fMax = inField.getData(component).getMaxv();
                float fMin = inField.getData(component).getMinv();
                float fs = 255.0f / (fMax - fMin);

                switch (inField.getData(component).getType()) {
                    case DataArray.FIELD_DATA_BYTE:
                        byte[] bData = inField.getData(component).getBData();
                        singleImage = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
                        raster = singleImage.getRaster();
                        for (int j = 0; j < h; j++) {
                            for (int i = 0; i < w; i++) {
                                raster.setSample(i, j, 0, (int) (0xFF & bData[j * dims[0] + i]));
                            }
                        }
                        break;
                    case DataArray.FIELD_DATA_SHORT:
                        short[] sData = inField.getData(component).getSData();
                        singleImage = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
                        raster = singleImage.getRaster();
                        for (int j = 0; j < h; j++) {
                            for (int i = 0; i < w; i++) {
                                raster.setSample(i, j, 0, ((float) (sData[j * dims[0] + i]) - fMin) * fs);
                            }
                        }
                        break;
                    case DataArray.FIELD_DATA_INT:
                        int[] iData = inField.getData(component).getIData();
                        singleImage = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
                        raster = singleImage.getRaster();
                        for (int j = 0; j < h; j++) {
                            for (int i = 0; i < w; i++) {
                                raster.setSample(i, j, 0, ((float) (iData[j * dims[0] + i]) - fMin) * fs);
                            }
                        }
                        break;
                    case DataArray.FIELD_DATA_FLOAT:
                        float[] fData = inField.getData(component).getFData();
                        singleImage = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
                        raster = singleImage.getRaster();
                        for (int j = 0; j < h; j++) {
                            for (int i = 0; i < w; i++) {
                                raster.setSample(i, j, 0, (fData[j * dims[0] + i] - fMin) * fs);
                            }
                        }
                        break;
                    case DataArray.FIELD_DATA_DOUBLE:
                        double[] dData = inField.getData(component).getDData();
                        double dMax = (double) inField.getData(component).getMaxv();
                        double dMin = (double) inField.getData(component).getMinv();
                        double ds = 255.0 / (dMax - dMin);
                        singleImage = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
                        raster = singleImage.getRaster();
                        for (int j = 0; j < h; j++) {
                            for (int i = 0; i < w; i++) {
                                raster.setSample(i, j, 0, (dData[j * dims[0] + i] - dMin) * ds);
                            }
                        }
                        break;
                    case DataArray.FIELD_DATA_COMPLEX:
                        float[] fAbsData = ((ComplexDataArray) inField.getData(component)).getFAbsData();
                        float fAbsMax = inField.getData(component).getMaxv();
                        float fAbsMin = inField.getData(component).getMinv();
                        float fAbss = 255.0f / (fAbsMax - fAbsMin);
                        singleImage = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
                        raster = singleImage.getRaster();
                        for (int j = 0; j < h; j++) {
                            for (int i = 0; i < w; i++) {
                                raster.setSample(i, j, 0, (fAbsData[j * dims[0] + i] - fAbsMin) * fAbss);
                            }
                        }
                        break;
                    case DataArray.FIELD_DATA_LOGIC:
                        LogicDataArray data = (LogicDataArray) inField.getData(component);
                        singleImage = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
                        raster = singleImage.getRaster();
                        for (int j = 0; j < h; j++) {
                            for (int i = 0; i < w; i++) {
                                raster.setSample(i, j, 0, 255 * data.getByteValue(j * dims[0] + i));
                            }
                        }
                        break;
                }
                break;
        }

        fireDataProviderSingleDataUpdated();
    }

    public BufferedImage getSingleImage() {
        return singleImage;
    }

    public BufferedImage getCustomSliceImage() {
        return customSliceImage;
    }

    public void updateCustomPlane() {
        customPlane.setPlaneParams(params.getCustomPlanePoint(), params.getCustomPlaneVector());
        updateCustomPlaneImage();
    }

    private void updateCustomPlaneImage() {
        if (inField == null || inField.getDims().length != 3) {
            customSliceImage = null;
            return;
        }

        float[][] affine = inField.getAffine();
        int[] dims = inField.getDims();

        float[][] base = customPlane.getBase();
        float[][] extents = customPlane.getBaseExtents();
        params.setCustomPlaneBase(base);
        params.setCustomPlaneExtents(extents);

        float[] norm = new float[2];
        norm[0] = 0;
        norm[1] = 0;
        float[] upp = new float[3];
        for (int i = 0; i < 3; i++) {
            upp[i] = 0;
            for (int j = 0; j < 3; j++) {
                upp[i] += affine[i][j] * affine[i][j];
            }
        }
        for (int i = 0; i < 3; i++) {
            norm[0] += base[0][i] * base[0][i] / upp[i];
            norm[1] += base[1][i] * base[1][i] / upp[i];
        }
        norm[0] = (float) Math.sqrt(norm[0]);
        norm[1] = (float) Math.sqrt(norm[1]);

        for (int i = 0; i < 3; i++) {
            base[0][i] = base[0][i] / norm[0];
            base[1][i] = base[1][i] / norm[1];
        }

        float[] baseResolution = new float[2];
        for (int i = 0; i < 2; i++) {
            baseResolution[i] = 0;
            for (int j = 0; j < 3; j++) {
                baseResolution[i] += base[i][j] * base[i][j];
            }
            baseResolution[i] = (float) Math.sqrt(baseResolution[i]);
        }
        params.setCustomPlaneUPPW(baseResolution[0]);
        params.setCustomPlaneUPPH(baseResolution[1]);


        float[] tmp = new float[3];
        for (int i = 0; i < 3; i++) {
            tmp[i] = Math.abs(base[0][i] + base[1][i]);
        }
        float min = tmp[0];
        int mini = 0;
        if (tmp[1] < min) {
            min = tmp[1];
            mini = 1;
        }
        if (tmp[2] < min) {
            min = tmp[2];
            mini = 2;
        }

        int i0 = 0, i1 = 1;
        switch (mini) {
            case 0:
                i0 = 1;
                i1 = 2;
                break;
            case 1:
                i0 = 0;
                i1 = 2;
                break;
            case 2:
                i0 = 0;
                i1 = 1;
                break;
        }

        int w, h;
        if (extents == null) {
            customSliceImage = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY);
            fireDataProviderCustomSliceUpdated();
            return;
        }
        for (int i = 0; i < 3; i++) {
            tmp[i] = extents[1][i] - extents[0][i];
        }
        float detA = base[0][i0] * base[1][i1] - base[1][i0] * base[0][i1];
        w = (int) Math.ceil(Math.abs((tmp[i0] * base[1][i1] - base[1][i0] * tmp[i1]) / detA)) + 1;
        h = (int) Math.ceil(Math.abs((tmp[i1] * base[0][i0] - base[0][i1] * tmp[i0]) / detA)) + 1;

        if (w <= 0 || h <= 0) {
            customSliceImage = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY);
            fireDataProviderCustomSliceUpdated();
            return;
        }

        float[][] A = new float[3][3];
        float[] b = new float[3];
        float[][] b0 = new float[2][3];

        A[0][0] = affine[0][0];
        A[1][0] = affine[0][1];
        A[2][0] = affine[0][2];
        A[0][1] = affine[1][0];
        A[1][1] = affine[1][1];
        A[2][1] = affine[1][2];
        A[0][2] = affine[2][0];
        A[1][2] = affine[2][1];
        A[2][2] = affine[2][2];

        int[] comps = params.getRgbComponents();
        int component = 0;
        int mode = DataProviderParams.MAPPING_MODE_COLORMAPPED;
        if (params.getMappingMode() == DataProviderParams.MAPPING_MODE_COLORMAPPED) {
            mode = DataProviderParams.MAPPING_MODE_COLORMAPPED;
            //component = params.getSingleComponent();
            component = params.getDataMappingParams().getColorMap0Params().getDataComponent();
        } else if (params.getMappingMode() == DataProviderParams.MAPPING_MODE_RGB) {
            mode = DataProviderParams.MAPPING_MODE_RGB;
        } else if (params.getMappingMode() == DataProviderParams.MAPPING_MODE_FAST) {
            mode = DataProviderParams.MAPPING_MODE_FAST;
            component = params.getSingleComponent();
        }

        int x, y, z, off;
        b0 = new float[2][3];
        for (int k = 0; k < 3; k++) {
            b[k] = extents[0][k] - affine[3][k];
        }
        tmp = NumericalMethods.lsolve3x3(A, b);
        b0[0] = NumericalMethods.lsolve3x3(A, base[0]);
        b0[1] = NumericalMethods.lsolve3x3(A, base[1]);

        switch (mode) {
            case DataProviderParams.MAPPING_MODE_COLORMAPPED:
                float low = params.getDataMappingParams().getColorMap0Params().getDataMin();
                float up = params.getDataMappingParams().getColorMap0Params().getDataMax();
                int colorMapSize = colorMapLUT.length - 1;
                float cs = (float) colorMapSize / (up - low);
                int veclen = inField.getData(component).getVeclen();
                int c;

                customSliceImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                switch (inField.getData(component).getType()) {
                    case DataArray.FIELD_DATA_BYTE:
                        RegularFieldInterpolator.interpolateFieldToSliceColormappedImage(inField.getData(component).getBData(), veclen, dims, tmp, b0, customSliceImage, colorMapLUT, low, up, params.getSliceFillColor(), w, h, params.isCustomOrthoPlanesInterpolation());
                        break;
                    case DataArray.FIELD_DATA_SHORT:
                        RegularFieldInterpolator.interpolateFieldToSliceColormappedImage(inField.getData(component).getSData(), veclen, dims, tmp, b0, customSliceImage, colorMapLUT, low, up, params.getSliceFillColor(), w, h, params.isCustomOrthoPlanesInterpolation());
                        break;
                    case DataArray.FIELD_DATA_INT:
                        RegularFieldInterpolator.interpolateFieldToSliceColormappedImage(inField.getData(component).getIData(), veclen, dims, tmp, b0, customSliceImage, colorMapLUT, low, up, params.getSliceFillColor(), w, h, params.isCustomOrthoPlanesInterpolation());
                        break;
                    case DataArray.FIELD_DATA_FLOAT:
                        RegularFieldInterpolator.interpolateFieldToSliceColormappedImage(inField.getData(component).getFData(), veclen, dims, tmp, b0, customSliceImage, colorMapLUT, low, up, params.getSliceFillColor(), w, h, params.isCustomOrthoPlanesInterpolation());
                        break;
                    case DataArray.FIELD_DATA_DOUBLE:
                        RegularFieldInterpolator.interpolateFieldToSliceColormappedImage(inField.getData(component).getDData(), veclen, dims, tmp, b0, customSliceImage, colorMapLUT, low, up, params.getSliceFillColor(), w, h, params.isCustomOrthoPlanesInterpolation());
                        break;
                    case DataArray.FIELD_DATA_COMPLEX:
                        RegularFieldInterpolator.interpolateFieldToSliceColormappedImage(((ComplexDataArray) inField.getData(component)).getFAbsData(), veclen, dims, tmp, b0, customSliceImage, colorMapLUT, low, up, params.getSliceFillColor(), w, h, params.isCustomOrthoPlanesInterpolation());
                        break;
                    case DataArray.FIELD_DATA_LOGIC:
                        if (params.isCustomPlaneInterpolation()) {
                            byte[] byteData = ((LogicDataArray) inField.getData(component)).getBitArray().getByteArray();
                            byte[] interpolatedData;
                            for (int j = 0; j < h; j++) {
                                for (int i = 0; i < w; i++) {
                                    x = (int) tmp[0];
                                    y = (int) tmp[1];
                                    z = (int) tmp[2];
                                    if (x >= 0 && x < dims[0] && y >= 0 && y < dims[1] && z >= 0 && z < dims[2]) {
                                        interpolatedData = inField.getInterpolatedData(byteData, tmp[0], tmp[1], tmp[2]);
                                        if (interpolatedData != null && interpolatedData.length == 1) {
                                            c = (int) (((float) (0xFF & interpolatedData[0]) - low) * cs);
                                            if (c < 0) {
                                                c = 0;
                                            }
                                            if (c > colorMapSize) {
                                                c = colorMapSize;
                                            }
                                            customSliceImage.setRGB(i, j, colorMapLUT[c]);
                                        }
                                    } else {
                                        customSliceImage.setRGB(i, j, params.getSliceFillColor());
                                    }
                                    tmp[0] += b0[0][0];
                                    tmp[1] += b0[0][1];
                                    tmp[2] += b0[0][2];
                                }
                                tmp[0] -= w * b0[0][0];
                                tmp[1] -= w * b0[0][1];
                                tmp[2] -= w * b0[0][2];
                                tmp[0] += b0[1][0];
                                tmp[1] += b0[1][1];
                                tmp[2] += b0[1][2];
                            }
                        } else {
                            BitArray logicData = ((LogicDataArray) inField.getData(component)).getBitArray();
                            tmp[0] += 0.5f;
                            tmp[1] += 0.5f;
                            tmp[2] += 0.5f;
                            for (int j = 0; j < h; j++) {
                                for (int i = 0; i < w; i++) {
                                    x = (int) tmp[0];
                                    y = (int) tmp[1];
                                    z = (int) tmp[2];
                                    if (x >= 0 && x < dims[0] && y >= 0 && y < dims[1] && z >= 0 && z < dims[2]) {
                                        c = (int) (((float) (0xff & logicData.getByteValueAtIndex(z * dims[0] * dims[1] + y * dims[0] + x)) - low) * cs);
                                        if (c < 0) {
                                            c = 0;
                                        }
                                        if (c > colorMapSize) {
                                            c = colorMapSize;
                                        }
                                        customSliceImage.setRGB(i, j, colorMapLUT[c]);
                                    } else {
                                        customSliceImage.setRGB(i, j, params.getSliceFillColor());
                                    }
                                    tmp[0] += b0[0][0];
                                    tmp[1] += b0[0][1];
                                    tmp[2] += b0[0][2];
                                }
                                tmp[0] -= w * b0[0][0];
                                tmp[1] -= w * b0[0][1];
                                tmp[2] -= w * b0[0][2];
                                tmp[0] += b0[1][0];
                                tmp[1] += b0[1][1];
                                tmp[2] += b0[1][2];
                            }
                        }
                        break;
                    default:
                        return;
                }
                break;
            case DataProviderParams.MAPPING_MODE_RGB:
                int[] rgba = new int[4];
                ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_sRGB);
                int[] nBits = {8, 8, 8, 8};
                ComponentColorModel colorModel = new ComponentColorModel(colorSpace, nBits, true, true, Transparency.TRANSLUCENT, 0);
                WritableRaster raster = colorModel.createCompatibleWritableRaster(w, h);
                customSliceImage = new BufferedImage(colorModel, raster, false, null);

                float[] fieldDataR = {};
                float[] fieldDataG = {};
                float[] fieldDataB = {};
                float[] lows = new float[3];
                float[] ups = new float[3];
                int[] weights = params.getRgbComponentWeights();
                for (int j = 0; j < comps.length; j++) {
                    lows[j] = inField.getData(comps[j]).getMinv();
                    ups[j] = inField.getData(comps[j]).getMaxv();
                }
                fieldDataR = inField.getData(comps[0]).getFData();
                fieldDataG = inField.getData(comps[1]).getFData();
                fieldDataB = inField.getData(comps[2]).getFData();

                if (params.isCustomPlaneInterpolation()) {
                    for (int j = 0; j < h; j++) {
                        for (int i = 0; i < w; i++) {
                            x = (int) tmp[0];
                            y = (int) tmp[1];
                            z = (int) tmp[2];

                            float[] data1 = new float[3];
                            float[][] tmpData = new float[3][];
                            if (x >= 0 && x < dims[0] && y >= 0 && y < dims[1] && z >= 0 && z < dims[2]) {
                                off = z * dims[0] * dims[1] + y * dims[0] + x;
                                tmpData[0] = inField.getInterpolatedData(fieldDataR, tmp[0], tmp[1], tmp[2]);
                                tmpData[1] = inField.getInterpolatedData(fieldDataG, tmp[0], tmp[1], tmp[2]);
                                tmpData[2] = inField.getInterpolatedData(fieldDataB, tmp[0], tmp[1], tmp[2]);
                                data1[0] = tmpData[0][0];
                                data1[1] = tmpData[1][0];
                                data1[2] = tmpData[2][0];
                                for (int s = 0; s < 3; s++) {
                                    rgba[s] = (int) (255 * weights[s] * (data1[s] - lows[s]) / (100. * (ups[s] - lows[s])));
                                    if (rgba[s] > 255) {
                                        rgba[s] = 255;
                                    }
                                }
                            } else {
                                for (int s = 0; s < 3; s++) {
                                    rgba[s] = 128;
                                }
                            }
                            rgba[3] = 0xFF;
                            raster.setPixel(i, j, rgba);
                            tmp[0] += b0[0][0];
                            tmp[1] += b0[0][1];
                            tmp[2] += b0[0][2];
                        }
                        tmp[0] -= w * b0[0][0];
                        tmp[1] -= w * b0[0][1];
                        tmp[2] -= w * b0[0][2];
                        tmp[0] += b0[1][0];
                        tmp[1] += b0[1][1];
                        tmp[2] += b0[1][2];
                    }
                } else {
                    tmp[0] += 0.5f;
                    tmp[1] += 0.5f;
                    tmp[2] += 0.5f;
                    for (int j = 0; j < h; j++) {
                        for (int i = 0; i < w; i++) {
                            x = (int) tmp[0];
                            y = (int) tmp[1];
                            z = (int) tmp[2];

                            float[] data2 = new float[3];
                            if (x >= 0 && x < dims[0] && y >= 0 && y < dims[1] && z >= 0 && z < dims[2]) {
                                off = z * dims[0] * dims[1] + y * dims[0] + x;
                                data2[0] = fieldDataR[off];
                                data2[1] = fieldDataG[off];
                                data2[2] = fieldDataB[off];
                                for (int s = 0; s < 3; s++) {
                                    rgba[s] = (int) (255 * weights[s] * (data2[s] - lows[s]) / (100. * (ups[s] - lows[s])));
                                    if (rgba[s] > 255) {
                                        rgba[s] = 255;
                                    }
                                }
                            } else {
                                for (int s = 0; s < 3; s++) {
                                    rgba[s] = 128;
                                }
                            }
                            rgba[3] = 0xFF;
                            raster.setPixel(i, j, rgba);
                            tmp[0] += b0[0][0];
                            tmp[1] += b0[0][1];
                            tmp[2] += b0[0][2];
                        }
                        tmp[0] -= w * b0[0][0];
                        tmp[1] -= w * b0[0][1];
                        tmp[2] -= w * b0[0][2];
                        tmp[0] += b0[1][0];
                        tmp[1] += b0[1][1];
                        tmp[2] += b0[1][2];
                    }
                }
                break;
            case DataProviderParams.MAPPING_MODE_FAST:
                customSliceImage = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
                WritableRaster fastRaster = customSliceImage.getRaster();
                switch (inField.getData(component).getType()) {
                    case DataArray.FIELD_DATA_BYTE:
                        byte[] bData = inField.getData(component).getBData();
                        byte[] bd;
                        if (params.isCustomPlaneInterpolation()) {
                            for (int j = 0; j < h; j++) {
                                for (int i = 0; i < w; i++) {
                                    x = (int) tmp[0];
                                    y = (int) tmp[1];
                                    z = (int) tmp[2];
                                    if (x >= 0 && x < dims[0] && y >= 0 && y < dims[1] && z >= 0 && z < dims[2]) {
                                        bd = inField.getInterpolatedData(bData, tmp[0], tmp[1], tmp[2]);
                                        if (bd != null && bd.length == 1) {
                                            fastRaster.setSample(i, j, 0, (int) (0xff & bd[0]));
                                        }
                                    } else {
                                        fastRaster.setSample(i, j, 0, 128);
                                    }
                                    tmp[0] += b0[0][0];
                                    tmp[1] += b0[0][1];
                                    tmp[2] += b0[0][2];
                                }
                                tmp[0] -= w * b0[0][0];
                                tmp[1] -= w * b0[0][1];
                                tmp[2] -= w * b0[0][2];
                                tmp[0] += b0[1][0];
                                tmp[1] += b0[1][1];
                                tmp[2] += b0[1][2];
                            }
                        } else {
                            tmp[0] += 0.5f;
                            tmp[1] += 0.5f;
                            tmp[2] += 0.5f;
                            for (int j = 0; j < h; j++) {
                                for (int i = 0; i < w; i++) {
                                    x = (int) tmp[0];
                                    y = (int) tmp[1];
                                    z = (int) tmp[2];
                                    if (x >= 0 && x < dims[0] && y >= 0 && y < dims[1] && z >= 0 && z < dims[2]) {
                                        fastRaster.setSample(i, j, 0, (int) (0xff & bData[z * dims[0] * dims[1] + y * dims[0] + x]));
                                    } else {
                                        fastRaster.setSample(i, j, 0, 128);
                                    }
                                    tmp[0] += b0[0][0];
                                    tmp[1] += b0[0][1];
                                    tmp[2] += b0[0][2];
                                }
                                tmp[0] -= w * b0[0][0];
                                tmp[1] -= w * b0[0][1];
                                tmp[2] -= w * b0[0][2];
                                tmp[0] += b0[1][0];
                                tmp[1] += b0[1][1];
                                tmp[2] += b0[1][2];
                            }
                        }
                        break;
                    case DataArray.FIELD_DATA_SHORT:
                        short[] sData = inField.getData(component).getSData();
                        short[] sd;
                        if (params.isCustomPlaneInterpolation()) {
                            for (int j = 0; j < h; j++) {
                                for (int i = 0; i < w; i++) {
                                    x = (int) tmp[0];
                                    y = (int) tmp[1];
                                    z = (int) tmp[2];
                                    if (x >= 0 && x < dims[0] && y >= 0 && y < dims[1] && z >= 0 && z < dims[2]) {
                                        sd = inField.getInterpolatedData(sData, tmp[0], tmp[1], tmp[2]);
                                        if (sd != null && sd.length == 1) {
                                            fastRaster.setSample(i, j, 0, (int) sd[0]);
                                        }
                                    } else {
                                        fastRaster.setSample(i, j, 0, 128);
                                    }
                                    tmp[0] += b0[0][0];
                                    tmp[1] += b0[0][1];
                                    tmp[2] += b0[0][2];
                                }
                                tmp[0] -= w * b0[0][0];
                                tmp[1] -= w * b0[0][1];
                                tmp[2] -= w * b0[0][2];
                                tmp[0] += b0[1][0];
                                tmp[1] += b0[1][1];
                                tmp[2] += b0[1][2];
                            }
                        } else {
                            tmp[0] += 0.5f;
                            tmp[1] += 0.5f;
                            tmp[2] += 0.5f;
                            for (int j = 0; j < h; j++) {
                                for (int i = 0; i < w; i++) {
                                    x = (int) tmp[0];
                                    y = (int) tmp[1];
                                    z = (int) tmp[2];
                                    if (x >= 0 && x < dims[0] && y >= 0 && y < dims[1] && z >= 0 && z < dims[2]) {
                                        fastRaster.setSample(i, j, 0, (int) sData[z * dims[0] * dims[1] + y * dims[0] + x]);
                                    } else {
                                        fastRaster.setSample(i, j, 0, 128);
                                    }
                                    tmp[0] += b0[0][0];
                                    tmp[1] += b0[0][1];
                                    tmp[2] += b0[0][2];
                                }
                                tmp[0] -= w * b0[0][0];
                                tmp[1] -= w * b0[0][1];
                                tmp[2] -= w * b0[0][2];
                                tmp[0] += b0[1][0];
                                tmp[1] += b0[1][1];
                                tmp[2] += b0[1][2];
                            }
                        }
                        break;
                    case DataArray.FIELD_DATA_INT:
                        int[] iData = inField.getData(component).getIData();
                        int[] id;
                        if (params.isCustomPlaneInterpolation()) {
                            for (int j = 0; j < h; j++) {
                                for (int i = 0; i < w; i++) {
                                    x = (int) tmp[0];
                                    y = (int) tmp[1];
                                    z = (int) tmp[2];
                                    if (x >= 0 && x < dims[0] && y >= 0 && y < dims[1] && z >= 0 && z < dims[2]) {
                                        id = inField.getInterpolatedData(iData, tmp[0], tmp[1], tmp[2]);
                                        if (id != null && id.length == 1) {
                                            fastRaster.setSample(i, j, 0, id[0]);
                                        }
                                    } else {
                                        fastRaster.setSample(i, j, 0, 128);
                                    }
                                    tmp[0] += b0[0][0];
                                    tmp[1] += b0[0][1];
                                    tmp[2] += b0[0][2];
                                }
                                tmp[0] -= w * b0[0][0];
                                tmp[1] -= w * b0[0][1];
                                tmp[2] -= w * b0[0][2];
                                tmp[0] += b0[1][0];
                                tmp[1] += b0[1][1];
                                tmp[2] += b0[1][2];
                            }
                        } else {
                            tmp[0] += 0.5f;
                            tmp[1] += 0.5f;
                            tmp[2] += 0.5f;
                            for (int j = 0; j < h; j++) {
                                for (int i = 0; i < w; i++) {
                                    x = (int) tmp[0];
                                    y = (int) tmp[1];
                                    z = (int) tmp[2];
                                    if (x >= 0 && x < dims[0] && y >= 0 && y < dims[1] && z >= 0 && z < dims[2]) {
                                        fastRaster.setSample(i, j, 0, iData[z * dims[0] * dims[1] + y * dims[0] + x]);
                                    } else {
                                        fastRaster.setSample(i, j, 0, 128);
                                    }
                                    tmp[0] += b0[0][0];
                                    tmp[1] += b0[0][1];
                                    tmp[2] += b0[0][2];
                                }
                                tmp[0] -= w * b0[0][0];
                                tmp[1] -= w * b0[0][1];
                                tmp[2] -= w * b0[0][2];
                                tmp[0] += b0[1][0];
                                tmp[1] += b0[1][1];
                                tmp[2] += b0[1][2];
                            }
                        }
                        break;
                    case DataArray.FIELD_DATA_FLOAT:
                        float[] fData = inField.getData(component).getFData();
                        float fMax = inField.getData(component).getMaxv();
                        float fMin = inField.getData(component).getMinv();
                        float fs = 255.0f / (fMax - fMin);
                        float[] fd;
                        if (params.isCustomPlaneInterpolation()) {
                            for (int j = 0; j < h; j++) {
                                for (int i = 0; i < w; i++) {
                                    x = (int) tmp[0];
                                    y = (int) tmp[1];
                                    z = (int) tmp[2];
                                    if (x >= 0 && x < dims[0] && y >= 0 && y < dims[1] && z >= 0 && z < dims[2]) {
                                        fd = inField.getInterpolatedData(fData, tmp[0], tmp[1], tmp[2]);
                                        if (fd != null && fd.length == 1) {
                                            fastRaster.setSample(i, j, 0, (fd[0] - fMin) * fs);
                                        }
                                    } else {
                                        fastRaster.setSample(i, j, 0, 128);
                                    }
                                    tmp[0] += b0[0][0];
                                    tmp[1] += b0[0][1];
                                    tmp[2] += b0[0][2];
                                }
                                tmp[0] -= w * b0[0][0];
                                tmp[1] -= w * b0[0][1];
                                tmp[2] -= w * b0[0][2];
                                tmp[0] += b0[1][0];
                                tmp[1] += b0[1][1];
                                tmp[2] += b0[1][2];
                            }
                        } else {
                            tmp[0] += 0.5f;
                            tmp[1] += 0.5f;
                            tmp[2] += 0.5f;
                            for (int j = 0; j < h; j++) {
                                for (int i = 0; i < w; i++) {
                                    x = (int) tmp[0];
                                    y = (int) tmp[1];
                                    z = (int) tmp[2];
                                    if (x >= 0 && x < dims[0] && y >= 0 && y < dims[1] && z >= 0 && z < dims[2]) {
                                        fastRaster.setSample(i, j, 0, (fData[z * dims[0] * dims[1] + y * dims[0] + x] - fMin) * fs);
                                    } else {
                                        fastRaster.setSample(i, j, 0, 128);
                                    }
                                    tmp[0] += b0[0][0];
                                    tmp[1] += b0[0][1];
                                    tmp[2] += b0[0][2];
                                }
                                tmp[0] -= w * b0[0][0];
                                tmp[1] -= w * b0[0][1];
                                tmp[2] -= w * b0[0][2];
                                tmp[0] += b0[1][0];
                                tmp[1] += b0[1][1];
                                tmp[2] += b0[1][2];
                            }
                        }
                        break;
                    case DataArray.FIELD_DATA_DOUBLE:
                        double[] dData = inField.getData(component).getDData();
                        double dMax = inField.getData(component).getMaxv();
                        double dMin = inField.getData(component).getMinv();
                        double ds = 255.0f / (dMax - dMin);
                        double[] dd;
                        if (params.isCustomPlaneInterpolation()) {
                            for (int j = 0; j < h; j++) {
                                for (int i = 0; i < w; i++) {
                                    x = (int) tmp[0];
                                    y = (int) tmp[1];
                                    z = (int) tmp[2];
                                    if (x >= 0 && x < dims[0] && y >= 0 && y < dims[1] && z >= 0 && z < dims[2]) {
                                        dd = inField.getInterpolatedData(dData, tmp[0], tmp[1], tmp[2]);
                                        if (dd != null && dd.length == 1) {
                                            fastRaster.setSample(i, j, 0, (dd[0] - dMin) * ds);
                                        }
                                    } else {
                                        fastRaster.setSample(i, j, 0, 128);
                                    }
                                    tmp[0] += b0[0][0];
                                    tmp[1] += b0[0][1];
                                    tmp[2] += b0[0][2];
                                }
                                tmp[0] -= w * b0[0][0];
                                tmp[1] -= w * b0[0][1];
                                tmp[2] -= w * b0[0][2];
                                tmp[0] += b0[1][0];
                                tmp[1] += b0[1][1];
                                tmp[2] += b0[1][2];
                            }
                        } else {
                            tmp[0] += 0.5f;
                            tmp[1] += 0.5f;
                            tmp[2] += 0.5f;
                            for (int j = 0; j < h; j++) {
                                for (int i = 0; i < w; i++) {
                                    x = (int) tmp[0];
                                    y = (int) tmp[1];
                                    z = (int) tmp[2];
                                    if (x >= 0 && x < dims[0] && y >= 0 && y < dims[1] && z >= 0 && z < dims[2]) {
                                        fastRaster.setSample(i, j, 0, (dData[z * dims[0] * dims[1] + y * dims[0] + x] - dMin) * ds);
                                    } else {
                                        fastRaster.setSample(i, j, 0, 128);
                                    }
                                    tmp[0] += b0[0][0];
                                    tmp[1] += b0[0][1];
                                    tmp[2] += b0[0][2];
                                }
                                tmp[0] -= w * b0[0][0];
                                tmp[1] -= w * b0[0][1];
                                tmp[2] -= w * b0[0][2];
                                tmp[0] += b0[1][0];
                                tmp[1] += b0[1][1];
                                tmp[2] += b0[1][2];
                            }
                        }
                        break;
                    case DataArray.FIELD_DATA_COMPLEX:
                        float[] fAbsData = ((ComplexDataArray) inField.getData(component)).getFAbsData();
                        float fAbsMax = inField.getData(component).getMaxv();
                        float fAbsMin = inField.getData(component).getMinv();
                        float fAbss = 255.0f / (fAbsMax - fAbsMin);
                        float[] fad;
                        if (params.isCustomPlaneInterpolation()) {
                            for (int j = 0; j < h; j++) {
                                for (int i = 0; i < w; i++) {
                                    x = (int) tmp[0];
                                    y = (int) tmp[1];
                                    z = (int) tmp[2];
                                    if (x >= 0 && x < dims[0] && y >= 0 && y < dims[1] && z >= 0 && z < dims[2]) {
                                        fad = inField.getInterpolatedData(fAbsData, tmp[0], tmp[1], tmp[2]);
                                        if (fad != null && fad.length == 1) {
                                            fastRaster.setSample(i, j, 0, (fad[0] - fAbsMin) * fAbss);
                                        }
                                    } else {
                                        fastRaster.setSample(i, j, 0, 128);
                                    }
                                    tmp[0] += b0[0][0];
                                    tmp[1] += b0[0][1];
                                    tmp[2] += b0[0][2];
                                }
                                tmp[0] -= w * b0[0][0];
                                tmp[1] -= w * b0[0][1];
                                tmp[2] -= w * b0[0][2];
                                tmp[0] += b0[1][0];
                                tmp[1] += b0[1][1];
                                tmp[2] += b0[1][2];
                            }
                        } else {
                            tmp[0] += 0.5f;
                            tmp[1] += 0.5f;
                            tmp[2] += 0.5f;
                            for (int j = 0; j < h; j++) {
                                for (int i = 0; i < w; i++) {
                                    x = (int) tmp[0];
                                    y = (int) tmp[1];
                                    z = (int) tmp[2];
                                    if (x >= 0 && x < dims[0] && y >= 0 && y < dims[1] && z >= 0 && z < dims[2]) {
                                        fastRaster.setSample(i, j, 0, (fAbsData[z * dims[0] * dims[1] + y * dims[0] + x] - fAbsMin) * fAbss);
                                    } else {
                                        fastRaster.setSample(i, j, 0, 128);
                                    }
                                    tmp[0] += b0[0][0];
                                    tmp[1] += b0[0][1];
                                    tmp[2] += b0[0][2];
                                }
                                tmp[0] -= w * b0[0][0];
                                tmp[1] -= w * b0[0][1];
                                tmp[2] -= w * b0[0][2];
                                tmp[0] += b0[1][0];
                                tmp[1] += b0[1][1];
                                tmp[2] += b0[1][2];
                            }
                        }
                        break;
                    case DataArray.FIELD_DATA_LOGIC:
                        if (params.isCustomPlaneInterpolation()) {
                            byte[] byteData = ((LogicDataArray) inField.getData(component)).getBitArray().getByteArray();
                            byte[] interpolatedData;
                            for (int j = 0; j < h; j++) {
                                for (int i = 0; i < w; i++) {
                                    x = (int) tmp[0];
                                    y = (int) tmp[1];
                                    z = (int) tmp[2];
                                    if (x >= 0 && x < dims[0] && y >= 0 && y < dims[1] && z >= 0 && z < dims[2]) {
                                        interpolatedData = inField.getInterpolatedData(byteData, tmp[0], tmp[1], tmp[2]);
                                        if (interpolatedData != null && interpolatedData.length == 1) {
                                            fastRaster.setSample(i, j, 0, 255 * (int) (0xFF & interpolatedData[0]));
                                        }
                                    } else {
                                        fastRaster.setSample(i, j, 0, 128);
                                    }
                                    tmp[0] += b0[0][0];
                                    tmp[1] += b0[0][1];
                                    tmp[2] += b0[0][2];
                                }
                                tmp[0] -= w * b0[0][0];
                                tmp[1] -= w * b0[0][1];
                                tmp[2] -= w * b0[0][2];
                                tmp[0] += b0[1][0];
                                tmp[1] += b0[1][1];
                                tmp[2] += b0[1][2];
                            }
                        } else {
                            BitArray logicData = ((LogicDataArray) inField.getData(component)).getBitArray();
                            tmp[0] += 0.5f;
                            tmp[1] += 0.5f;
                            tmp[2] += 0.5f;
                            for (int j = 0; j < h; j++) {
                                for (int i = 0; i < w; i++) {
                                    x = (int) tmp[0];
                                    y = (int) tmp[1];
                                    z = (int) tmp[2];
                                    if (x >= 0 && x < dims[0] && y >= 0 && y < dims[1] && z >= 0 && z < dims[2]) {
                                        fastRaster.setSample(i, j, 0, 255 * (int) (0xff & logicData.getByteValueAtIndex(z * dims[0] * dims[1] + y * dims[0] + x)));
                                    } else {
                                        fastRaster.setSample(i, j, 0, 128);
                                    }
                                    tmp[0] += b0[0][0];
                                    tmp[1] += b0[0][1];
                                    tmp[2] += b0[0][2];
                                }
                                tmp[0] -= w * b0[0][0];
                                tmp[1] -= w * b0[0][1];
                                tmp[2] -= w * b0[0][2];
                                tmp[0] += b0[1][0];
                                tmp[1] += b0[1][1];
                                tmp[2] += b0[1][2];
                            }
                        }
                        break;
                    default:
                        return;
                }
                break;
        }
        fireDataProviderCustomSliceUpdated();
    }

    public GeometryObject getCustomSlice3DPlane() {
        return customSlice3DPlane;
    }

    /**
     * @return the params
     */
    public DataProviderParams getParams() {
        return params;
    }

    /**
     * @return the inField
     */
    public RegularField getInField() {
        return inField;
    }

    public RegularField getOverlayField() {
        return overlayField;
    }

    /**
     * @param inField the inField to set
     */
    public void setInField(RegularField field) {
        this.inField = field;
        params.setSilent(true);
        if (inField == null) {
            this.sliceImage = null;
            this.overlaySliceImage = null;
            int[] d = {10, 10, 10};
            params.setDims(d);            
            params.getDataMappingParams().setInField(null);            
            params.setSilent(false);
            this.ui.setInfield(field);
            updateAll();
            return;
        }

        if (params.getSingleComponent() >= inField.getNData()) {
            params.setSingleComponent(0);
        }

        int[] comps = params.getRgbComponents();
        for (int i = 0; i < comps.length; i++) {
            if (comps[i] >= inField.getNData()) {
                comps[i] = 0;
            }
        }
        params.setRgbComponents(comps);

        int[] dims = this.inField.getDims();
        float[][] affine = field.getAffine();
        float[] baseCellSize = {0,0,0};
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                baseCellSize[i] += affine[i][j]*affine[i][j];
            }
            baseCellSize[i] = (float)Math.sqrt(baseCellSize[i]);
        }

        params.setDims(dims);
        params.setPoint0(affine[3]);
        params.getDataMappingParams().setInField(field);

        if (dims != null && dims.length == 3) {
            params.setUPPS(baseCellSize[0], baseCellSize[1], baseCellSize[2]);
            this.sliceImage = new SliceImage(inField);
            sliceImage.setWeights(params.getRgbComponentWeights());
            customPlane.setField(field);
            for (int i = 0; i < 3; i++) {
                customOrthoPlanes[i].setField(field);
            }
        } else if (dims != null && dims.length == 2) {
            params.setUPPS(baseCellSize[0], baseCellSize[1], baseCellSize[1]);
            this.sliceImage = new SliceImage(inField);
            sliceImage.setWeights(params.getRgbComponentWeights());
        }
        params.setSilent(false);

        this.ui.setInfield(field);
        updateAll();
    }

    public void setOverlayField(RegularField field) {
        if(inField == null)
            return;

        if(field == null) {
            this.overlayField = null;
            this.overlaySliceImage = null;
            return;
        }
        
        int[] inFieldDims = inField.getDims();
        int[] overlayFieldDims = field.getDims();
        if(inFieldDims.length != overlayFieldDims.length || overlayFieldDims.length != 3) {
            System.err.println("ERROR: overlay field dimensions do not match data field dimensions");
            return;
        }
        if(inFieldDims[0] != overlayFieldDims[0] || inFieldDims[1] != overlayFieldDims[1] || inFieldDims[2] != overlayFieldDims[2]) {
            System.err.println("ERROR: overlay field dimensions do not match data field dimensions");
            return;
        }
        
        float[][] inFieldAffine = inField.getAffine();
        float[][] overlayFieldAffine = field.getAffine();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                if(overlayFieldAffine[i][j] != inFieldAffine[i][j]) {
                    System.err.println("ERROR: overlay field affine do not match data field affine");
                    return;
                }
            }            
        }

        this.overlayField = field;
        this.overlaySliceImage = new SliceImage(overlayField);
        overlaySliceImage.setMultiselImage(true);
    }

    @Override
    public void onOrthosliceNumberChanged(OrthosliceNumberChangedEvent evt) {
        if (evt.getAxis() == -1) {
            updateOrthosliceImages();
            updateOrthosliceIsolines();
            updateOrthosliceOverlays();
        } else {
            updateOrthosliceImage(evt.getAxis(), false);
            updateOrthosliceIsoline(evt.getAxis(), false);
            updateOrthosliceIsolines(evt.getAxis(), false);
            updateOrthosliceOverlay(evt.getAxis(), false);
        }
    }

    @Override
    public void onRgbComponentChanged(RgbComponentChangedEvent evt) {
        updateOrthosliceImages();
        updateCustomPlaneImage();
        updateCustomOrthoPlanesImages();
        updateSingleImage();
    }

    @Override
    public void onColormapChanged(ColormapChangedEvent evt) {
        updateColorMap();
        updateOrthosliceImages();
        updateCustomPlaneImage();
        updateCustomOrthoPlanesImages();
        updateSingleImage();
    }

    @Override
    public void onRgbComponentWeightChanged(RgbComponentWeightChangedEvent evt) {
        updateOrthosliceImages();
        updateCustomPlaneImage();
        updateCustomOrthoPlanesImages();
        updateSingleImage();
    }

    @Override
    public void onOverlayChanged(DataProviderParamsEvent evt) {
        updateOrthosliceOverlays();
        updateCustomOrthoPlanesOverlays();
    }

    @Override
    public void onOverlayOpacityChanged(DataProviderParamsEvent evt) {
        fireDataProviderOverlayUpdated(-1);
    }
    
    @Override
    public void onCustomPlaneChanged(CustomPlaneChangedEvent evt) {
        updateCustomPlane();
    }

    @Override
    public void onIsolineThresholdChanged(IsolineThresholdChangedEvent evt) {
        updateOrthosliceIsolines();
    }
    private transient ArrayList<DataProviderListener> listenerList = new ArrayList<DataProviderListener>();

    public synchronized void addDataProviderListener(DataProviderListener listener) {
        listenerList.add(listener);
    }

    public synchronized void removeDataProviderListener(DataProviderListener listener) {
        listenerList.remove(listener);
    }

    public void centerSlices() {
        if (inField == null || inField.getDims().length != 3) {
            return;
        }

        int[] dims = inField.getDims();
        params.setOrthosliceNumbers(dims[0] / 2, dims[1] / 2, dims[2] / 2);



        float[][] affine = inField.getAffine();
        float[] p0 = new float[3];
        p0[0] = affine[3][0] + (affine[0][0] * (dims[0] - 1) + affine[1][0] * (dims[1] - 1) + affine[2][0] * (dims[2] - 1)) / 2;
        p0[1] = affine[3][1] + (affine[0][1] * (dims[0] - 1) + affine[1][1] * (dims[1] - 1) + affine[2][1] * (dims[2] - 1)) / 2;
        p0[2] = affine[3][2] + (affine[0][2] * (dims[0] - 1) + affine[1][2] * (dims[1] - 1) + affine[2][2] * (dims[2] - 1)) / 2;

        float[] v = {1.0f, 0.0f, 0.0f};
        params.setCustomPlaneParams(p0.clone(), v);

        float[][] v0 = {{1.0f, 0.0f, 0.0f}, {0.0f, -1.0f, 0.0f}, {0.0f, 0.0f, -1.0f}};
        params.setCustomOrthoPlanesParams(p0.clone(), v0);
    }

    public void resetCustomPlane() {
        if (inField == null || inField.getDims().length != 3) {
            return;
        }

        float[][] pts = inField.getExtents();
        float[] p = new float[3];
        p[0] = (pts[1][0] + pts[0][0]) / 2;
        p[1] = (pts[1][1] + pts[0][1]) / 2;
        p[2] = (pts[1][2] + pts[0][2]) / 2;
        float[] v = {0.0f, 0.0f, 1.0f};
        params.setCustomPlaneParams(p, v);
    }

    public void resetCustomOrthoPlanes() {
        if (inField == null || inField.getDims().length != 3) {
            return;
        }

        float[][] pts = inField.getExtents();
        float[] p = new float[3];
        p[0] = (pts[1][0] + pts[0][0]) / 2;
        p[1] = (pts[1][1] + pts[0][1]) / 2;
        p[2] = (pts[1][2] + pts[0][2]) / 2;
        float[][] v = {{-1.0f, 0.0f, 0.0f}, {0.0f, 1.0f, 0.0f}, {0.0f, 0.0f, -1.0f}};
        params.setCustomOrthoPlanesParams(p, v);
    }

    private void fireDataProviderOrthosliceUpdated(int axis) {
        for (DataProviderListener listener : listenerList) {
            listener.onDataProviderOrthosliceUpdated(axis);
        }
    }

    private void fireDataProviderOverlayUpdated(int axis) {
        for (DataProviderListener listener : listenerList) {
            listener.onDataProviderOverlayUpdated(axis);
        }
    }

    private void fireDataProviderCustomSliceUpdated() {
        for (DataProviderListener listener : listenerList) {
            listener.onDataProviderCustomSliceUpdated();
        }
    }

    private void fireDataProviderSingleDataUpdated() {
        for (DataProviderListener listener : listenerList) {
            listener.onDataProviderSingleDataUpdated();
        }
    }

    /**
     * @return the geometryParams
     */
    public GeometryParams getGeometryParams() {
        return geometryParams;
    }

    /**
     * @param geometryParams the geometryParams to set
     */
    public void setGeometryParams(GeometryParams geometryParams) {
        this.geometryParams = geometryParams;
    }

    /**
     * @return the auxField
     */
    public RegularField getAuxField() {
        return auxField;
    }

    /**
     * @param auxField the auxField to set
     */
    public void setAuxField(RegularField auxField) {
        if (inField == null || inField.getDims().length != 3 || auxField == null || auxField.getDims() == null || auxField.getDims().length != 3) {
            auxField = null;
            for (int i = 0; i < 3; i++) {
                mapIsoline[i] = null;
            }
            return;
        }
        int[] d = auxField.getDims();
        int[] dims = inField.getDims();
        for (int i = 0; i < d.length; i++) {
            if (d[i] != dims[i]) {
                return;
            }
        }
        this.auxField = auxField;
        for (int i = 0; i < 3; i++) {
            mapIsoline[i] = null;

        }
    }

    @Override
    public void onCustomOrthoPlaneChanged(CustomOrthoPlaneChangedEvent evt) {
        boolean[] mask = evt.getChangeMask();
        if (mask == null || mask.length != 4) {
            updateCustomOrthoPlanes();
            return;
        }

        if (mask[3]) { //vector update
            updateCustomOrthoPlanes();
            return;
        }

        if (mask[0] || mask[1] || mask[2]) { //point update
            updateCustomOrthoPlanesParams(true);
        }

        if (mask[0]) {
            updateCustomOrthoPlaneImage(0, true);
            updateCustomOrthoPlaneOverlay(0, true);
        }
        if (mask[1]) {
            updateCustomOrthoPlaneImage(1, true);
            updateCustomOrthoPlaneOverlay(1, true);
        }
        if (mask[2]) {
            updateCustomOrthoPlaneImage(2, true);
            updateCustomOrthoPlaneOverlay(2, true);
        }

        if (mask[0] || mask[1] || mask[2]) {
            if (mask[0]) {
                fireDataProviderCustomOrthoSliceUpdated(0);
            }
            if (mask[1]) {
                fireDataProviderCustomOrthoSliceUpdated(1);
            }
            if (mask[2]) {
                fireDataProviderCustomOrthoSliceUpdated(2);
            }
        }
    }

    public BufferedImage getCustomOrthoSliceImage(int axis) {
        return customOrthoSlicesImages[axis];
    }

    public BufferedImage getCustomOrthoSliceOverlay(int axis) {
        if ((overlayField == null && !params.isSimpleOverlay()) || axis < 0 || axis > 2) {
            return null;
        }

        if (customOrthoSlicesOverlays[axis] == null) {
            updateCustomOrthoPlaneOverlay(axis, true);
        }

        return customOrthoSlicesOverlays[axis];
    }


    private void updateCustomOrthoPlanesParams(boolean silent) {
        for (int i = 0; i < 3; i++) {
            updateCustomOrthoPlaneParams(i, true);
        }
        if (!silent) {
            fireDataProviderCustomOrthoSliceUpdated(-1);
        }
    }

    public void updateCustomOrthoPlanesImages() {
        for (int i = 0; i < 3; i++) {
            updateCustomOrthoPlaneImage(i, true);
        }
        fireDataProviderCustomOrthoSliceUpdated(-1);
    }

    public void updateCustomOrthoPlanesOverlays() {
        for (int i = 0; i < 3; i++) {
            updateCustomOrthoPlaneOverlay(i, true);
        }
        fireDataProviderOrthosliceUpdated(-1);
    }

    private void updateCustomOrthoPlanes() {
        for (int i = 0; i < 3; i++) {
            updateCustomOrthoPlaneParams(i, true);
            updateCustomOrthoPlaneImage(i, true);
            updateCustomOrthoPlaneOverlay(i, true);
        }
        fireDataProviderCustomOrthoSliceUpdated(-1);
    }

    private void updateCustomOrthoPlane(int axis, boolean silent) {
        updateCustomOrthoPlaneParams(axis, true);
        updateCustomOrthoPlaneImage(axis, true);
        updateCustomOrthoPlaneOverlay(axis, true);
        if (!silent) {
            fireDataProviderCustomOrthoSliceUpdated(axis);
        }
    }

    private void updateCustomOrthoPlaneParams(int axis, boolean silent) {
        if (inField == null || inField.getDims().length != 3) {
            return;
        }

        customOrthoPlanes[axis].setPlaneParams(params.getCustomOrthoPlanesPoint(), params.getCustomOrthoPlanesVector(axis));

        float[][] affine = inField.getAffine();

        float[][] base = customOrthoPlanes[axis].getBase();
        float[][] extents = customOrthoPlanes[axis].getBaseExtents();
        params.setCustomOrthoPlanesBase(axis, base);
        params.setCustomOrthoPlanesExtents(axis, extents);

        float[] norm = new float[2];
        norm[0] = 0;
        norm[1] = 0;
        float[] upp = new float[3];
        for (int i = 0; i < 3; i++) {
            upp[i] = 0;
            for (int j = 0; j < 3; j++) {
                upp[i] += affine[i][j] * affine[i][j];
            }
        }
        for (int i = 0; i < 3; i++) {
            norm[0] += base[0][i] * base[0][i] / upp[i];
            norm[1] += base[1][i] * base[1][i] / upp[i];
        }
        norm[0] = (float) Math.sqrt(norm[0]);
        norm[1] = (float) Math.sqrt(norm[1]);

        for (int i = 0; i < 3; i++) {
            base[0][i] = base[0][i] / norm[0];
            base[1][i] = base[1][i] / norm[1];
        }

        float[] baseResolution = new float[2];
        for (int i = 0; i < 2; i++) {
            baseResolution[i] = 0;
            for (int j = 0; j < 3; j++) {
                baseResolution[i] += base[i][j] * base[i][j];
            }
            baseResolution[i] = (float) Math.sqrt(baseResolution[i]);
        }
        params.setCustomOrthoPlanesUPPW(axis, baseResolution[0]);
        params.setCustomOrthoPlanesUPPH(axis, baseResolution[1]);


        if (!silent) {
            fireDataProviderCustomOrthoSliceUpdated(axis);
        }
    }

    private void updateCustomOrthoPlaneImage(int axis, boolean silent) {
        if (inField == null || inField.getDims().length != 3) {
            customOrthoSlicesImages[axis] = null;
            return;
        }

        float[][] affine = inField.getAffine();
        int[] dims = inField.getDims();
        float[][] base = customOrthoPlanes[axis].getBase();
        float[][] extents = customOrthoPlanes[axis].getBaseExtents();

        float[] tmp = new float[3];
        for (int i = 0; i < 3; i++) {
            tmp[i] = Math.abs(base[0][i] + base[1][i]);
        }
        float min = tmp[0];
        int mini = 0;
        if (tmp[1] < min) {
            min = tmp[1];
            mini = 1;
        }
        if (tmp[2] < min) {
            min = tmp[2];
            mini = 2;
        }

        int i0 = 0, i1 = 1;
        switch (mini) {
            case 0:
                i0 = 1;
                i1 = 2;
                break;
            case 1:
                i0 = 0;
                i1 = 2;
                break;
            case 2:
                i0 = 0;
                i1 = 1;
                break;
        }

        int w, h;
        if (extents == null) {
            customOrthoSlicesImages[axis] = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY);
            if (!silent) {
                fireDataProviderCustomOrthoSliceUpdated(axis);
            }
            return;
        }
        for (int i = 0; i < 3; i++) {
            tmp[i] = extents[1][i] - extents[0][i];
        }
        float detA = base[0][i0] * base[1][i1] - base[1][i0] * base[0][i1];
        w = (int) Math.round(Math.abs((tmp[i0] * base[1][i1] - base[1][i0] * tmp[i1]) / detA)) + 1;
        h = (int) Math.round(Math.abs((tmp[i1] * base[0][i0] - base[0][i1] * tmp[i0]) / detA)) + 1;

        if (w <= 0 || h <= 0) {
            customOrthoSlicesImages[axis] = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY);
            if (!silent) {
                fireDataProviderCustomOrthoSliceUpdated(axis);
            }
            return;
        }

        float[][] A = new float[3][3];
        float[] b = new float[3];
        float[][] b0 = new float[2][3];

        A[0][0] = affine[0][0];
        A[1][0] = affine[0][1];
        A[2][0] = affine[0][2];
        A[0][1] = affine[1][0];
        A[1][1] = affine[1][1];
        A[2][1] = affine[1][2];
        A[0][2] = affine[2][0];
        A[1][2] = affine[2][1];
        A[2][2] = affine[2][2];

        int[] comps = params.getRgbComponents();
        int component = 0;
        int mode = DataProviderParams.MAPPING_MODE_COLORMAPPED;
        if (params.getMappingMode() == DataProviderParams.MAPPING_MODE_COLORMAPPED) {
            mode = DataProviderParams.MAPPING_MODE_COLORMAPPED;
            //component = params.getSingleComponent();
            component = params.getDataMappingParams().getColorMap0Params().getDataComponent();
        } else if (params.getMappingMode() == DataProviderParams.MAPPING_MODE_RGB) {
            mode = DataProviderParams.MAPPING_MODE_RGB;
        } else if (params.getMappingMode() == DataProviderParams.MAPPING_MODE_FAST) {
            mode = DataProviderParams.MAPPING_MODE_FAST;
            component = params.getSingleComponent();
        }

        int x, y, z, off;
        b0 = new float[2][3];
        for (int k = 0; k < 3; k++) {
            b[k] = extents[0][k] - affine[3][k];
        }
        tmp = NumericalMethods.lsolve3x3(A, b);

        b0[0] = NumericalMethods.lsolve3x3(A, base[0]);
        b0[1] = NumericalMethods.lsolve3x3(A, base[1]);

        switch (mode) {
            case DataProviderParams.MAPPING_MODE_COLORMAPPED:
                float low = params.getDataMappingParams().getColorMap0Params().getDataMin();
                float up = params.getDataMappingParams().getColorMap0Params().getDataMax();
                int veclen = inField.getData(component).getVeclen();

                customOrthoSlicesImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                switch (inField.getData(component).getType()) {
                    case DataArray.FIELD_DATA_BYTE:
                        RegularFieldInterpolator.interpolateFieldToSliceColormappedImage(inField.getData(component).getBData(), veclen, dims, tmp, b0, customOrthoSlicesImages[axis], colorMapLUT, low, up, params.getSliceFillColor(), w, h, params.isCustomOrthoPlanesInterpolation());
                        break;
                    case DataArray.FIELD_DATA_SHORT:
                        RegularFieldInterpolator.interpolateFieldToSliceColormappedImage(inField.getData(component).getSData(), veclen, dims, tmp, b0, customOrthoSlicesImages[axis], colorMapLUT, low, up, params.getSliceFillColor(), w, h, params.isCustomOrthoPlanesInterpolation());
                        break;
                    case DataArray.FIELD_DATA_INT:
                        RegularFieldInterpolator.interpolateFieldToSliceColormappedImage(inField.getData(component).getIData(), veclen, dims, tmp, b0, customOrthoSlicesImages[axis], colorMapLUT, low, up, params.getSliceFillColor(), w, h, params.isCustomOrthoPlanesInterpolation());
                        break;
                    case DataArray.FIELD_DATA_FLOAT:
                        RegularFieldInterpolator.interpolateFieldToSliceColormappedImage(inField.getData(component).getFData(), veclen, dims, tmp, b0, customOrthoSlicesImages[axis], colorMapLUT, low, up, params.getSliceFillColor(), w, h, params.isCustomOrthoPlanesInterpolation());
                        break;
                    case DataArray.FIELD_DATA_DOUBLE:
                        RegularFieldInterpolator.interpolateFieldToSliceColormappedImage(inField.getData(component).getDData(), veclen, dims, tmp, b0, customOrthoSlicesImages[axis], colorMapLUT, low, up, params.getSliceFillColor(), w, h, params.isCustomOrthoPlanesInterpolation());
                        break;
                    case DataArray.FIELD_DATA_COMPLEX:
                        RegularFieldInterpolator.interpolateFieldToSliceColormappedImage(((ComplexDataArray) inField.getData(component)).getFAbsData(), veclen, dims, tmp, b0, customOrthoSlicesImages[axis], colorMapLUT, low, up, params.getSliceFillColor(), w, h, params.isCustomOrthoPlanesInterpolation());
                        break;
                    case DataArray.FIELD_DATA_LOGIC:
                        int colorMapSize = colorMapLUT.length - 1;
                        float cs = (float) colorMapSize / (up - low);
                        int c;
                        if (params.isCustomOrthoPlanesInterpolation()) {
                            byte[] byteData = ((LogicDataArray) inField.getData(component)).getBitArray().getByteArray();
                            for (int j = 0; j < h; j++) {
                                for (int i = 0; i < w; i++) {
                                    x = (int) tmp[0];
                                    y = (int) tmp[1];
                                    z = (int) tmp[2];
                                    if (x >= 0 && x < dims[0] && y >= 0 && y < dims[1] && z >= 0 && z < dims[2]) {
                                        c = (int) (((float) (0xFF & RegularFieldInterpolator.getInterpolatedScalarData(byteData, dims, tmp[0], tmp[1], tmp[2])) - low) * cs);
                                        if (c < 0) {
                                            c = 0;
                                        }
                                        if (c > colorMapSize) {
                                            c = colorMapSize;
                                        }
                                        customOrthoSlicesImages[axis].setRGB(i, j, colorMapLUT[c]);
                                    } else {
                                        customOrthoSlicesImages[axis].setRGB(i, j, params.getSliceFillColor());
                                    }
                                    tmp[0] += b0[0][0];
                                    tmp[1] += b0[0][1];
                                    tmp[2] += b0[0][2];
                                }
                                tmp[0] -= w * b0[0][0];
                                tmp[1] -= w * b0[0][1];
                                tmp[2] -= w * b0[0][2];
                                tmp[0] += b0[1][0];
                                tmp[1] += b0[1][1];
                                tmp[2] += b0[1][2];
                            }
                        } else {
                            BitArray logicData = ((LogicDataArray) inField.getData(component)).getBitArray();
                            tmp[0] += 0.5f;
                            tmp[1] += 0.5f;
                            tmp[2] += 0.5f;
                            for (int j = 0; j < h; j++) {
                                for (int i = 0; i < w; i++) {
                                    x = (int) tmp[0];
                                    y = (int) tmp[1];
                                    z = (int) tmp[2];
                                    if (x >= 0 && x < dims[0] && y >= 0 && y < dims[1] && z >= 0 && z < dims[2]) {
                                        c = (int) (((float) (0xff & logicData.getByteValueAtIndex(z * dims[0] * dims[1] + y * dims[0] + x)) - low) * cs);
                                        if (c < 0) {
                                            c = 0;
                                        }
                                        if (c > colorMapSize) {
                                            c = colorMapSize;
                                        }
                                        customOrthoSlicesImages[axis].setRGB(i, j, colorMapLUT[c]);
                                    } else {
                                        customOrthoSlicesImages[axis].setRGB(i, j, params.getSliceFillColor());
                                    }
                                    tmp[0] += b0[0][0];
                                    tmp[1] += b0[0][1];
                                    tmp[2] += b0[0][2];
                                }
                                tmp[0] -= w * b0[0][0];
                                tmp[1] -= w * b0[0][1];
                                tmp[2] -= w * b0[0][2];
                                tmp[0] += b0[1][0];
                                tmp[1] += b0[1][1];
                                tmp[2] += b0[1][2];
                            }
                        }
                        break;
                    default:
                        return;
                }
                break;
            case DataProviderParams.MAPPING_MODE_RGB:
                int[] rgba = new int[4];
                ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_sRGB);
                int[] nBits = {8, 8, 8, 8};
                ComponentColorModel colorModel = new ComponentColorModel(colorSpace, nBits, true, true, Transparency.TRANSLUCENT, 0);
                WritableRaster raster = colorModel.createCompatibleWritableRaster(w, h);
                customOrthoSlicesImages[axis] = new BufferedImage(colorModel, raster, false, null);

                float[] fieldDataR = {};
                float[] fieldDataG = {};
                float[] fieldDataB = {};
                float[] lows = new float[3];
                float[] ups = new float[3];
                int[] weights = params.getRgbComponentWeights();
                for (int j = 0; j < comps.length; j++) {
                    lows[j] = inField.getData(comps[j]).getMinv();
                    ups[j] = inField.getData(comps[j]).getMaxv();
                }
                fieldDataR = inField.getData(comps[0]).getFData();
                fieldDataG = inField.getData(comps[1]).getFData();
                fieldDataB = inField.getData(comps[2]).getFData();

                if (params.isCustomOrthoPlanesInterpolation()) {
                    for (int j = 0; j < h; j++) {
                        for (int i = 0; i < w; i++) {
                            x = (int) tmp[0];
                            y = (int) tmp[1];
                            z = (int) tmp[2];

                            float[] data1 = new float[3];
                            float[][] tmpData = new float[3][];
                            if (x >= 0 && x < dims[0] && y >= 0 && y < dims[1] && z >= 0 && z < dims[2]) {
                                off = z * dims[0] * dims[1] + y * dims[0] + x;
                                tmpData[0] = inField.getInterpolatedData(fieldDataR, tmp[0], tmp[1], tmp[2]);
                                tmpData[1] = inField.getInterpolatedData(fieldDataG, tmp[0], tmp[1], tmp[2]);
                                tmpData[2] = inField.getInterpolatedData(fieldDataB, tmp[0], tmp[1], tmp[2]);
                                data1[0] = tmpData[0][0];
                                data1[1] = tmpData[1][0];
                                data1[2] = tmpData[2][0];
                                for (int s = 0; s < 3; s++) {
                                    rgba[s] = (int) (255 * weights[s] * (data1[s] - lows[s]) / (100. * (ups[s] - lows[s])));
                                    if (rgba[s] > 255) {
                                        rgba[s] = 255;
                                    }
                                }
                            } else {
                                for (int s = 0; s < 3; s++) {
                                    rgba[s] = 128;
                                }
                            }
                            rgba[3] = 0xFF;
                            raster.setPixel(i, j, rgba);
                            tmp[0] += b0[0][0];
                            tmp[1] += b0[0][1];
                            tmp[2] += b0[0][2];
                        }
                        tmp[0] -= w * b0[0][0];
                        tmp[1] -= w * b0[0][1];
                        tmp[2] -= w * b0[0][2];
                        tmp[0] += b0[1][0];
                        tmp[1] += b0[1][1];
                        tmp[2] += b0[1][2];
                    }
                } else {
                    tmp[0] += 0.5f;
                    tmp[1] += 0.5f;
                    tmp[2] += 0.5f;
                    for (int j = 0; j < h; j++) {
                        for (int i = 0; i < w; i++) {
                            x = (int) tmp[0];
                            y = (int) tmp[1];
                            z = (int) tmp[2];

                            float[] data2 = new float[3];
                            if (x >= 0 && x < dims[0] && y >= 0 && y < dims[1] && z >= 0 && z < dims[2]) {
                                off = z * dims[0] * dims[1] + y * dims[0] + x;
                                data2[0] = fieldDataR[off];
                                data2[1] = fieldDataG[off];
                                data2[2] = fieldDataB[off];
                                for (int s = 0; s < 3; s++) {
                                    rgba[s] = (int) (255 * weights[s] * (data2[s] - lows[s]) / (100. * (ups[s] - lows[s])));
                                    if (rgba[s] > 255) {
                                        rgba[s] = 255;
                                    }
                                }
                            } else {
                                for (int s = 0; s < 3; s++) {
                                    rgba[s] = 128;
                                }
                            }
                            rgba[3] = 0xFF;
                            raster.setPixel(i, j, rgba);
                            tmp[0] += b0[0][0];
                            tmp[1] += b0[0][1];
                            tmp[2] += b0[0][2];
                        }
                        tmp[0] -= w * b0[0][0];
                        tmp[1] -= w * b0[0][1];
                        tmp[2] -= w * b0[0][2];
                        tmp[0] += b0[1][0];
                        tmp[1] += b0[1][1];
                        tmp[2] += b0[1][2];
                    }
                }
                break;
            case DataProviderParams.MAPPING_MODE_FAST:
                customOrthoSlicesImages[axis] = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
                WritableRaster fastRaster = customOrthoSlicesImages[axis].getRaster();
                switch (inField.getData(component).getType()) {
                    case DataArray.FIELD_DATA_BYTE:
                        RegularFieldInterpolator.interpolateScalarFieldToSliceRaster(inField.getData(component).getBData(), dims, tmp, b0, fastRaster, w, h, params.isCustomOrthoPlanesInterpolation());
                        break;
                    case DataArray.FIELD_DATA_SHORT:
                        short[] sData = inField.getData(component).getSData();
                        short sMax = (short) inField.getData(component).getMaxv();
                        short sMin = (short) inField.getData(component).getMinv();
                        float ss = 255.0f / (sMax - sMin);
                        RegularFieldInterpolator.interpolateScalarFieldToSliceRaster(sData, dims, tmp, b0, sMin, sMax, ss, fastRaster, w, h, params.isCustomOrthoPlanesInterpolation());

                        break;
                    case DataArray.FIELD_DATA_INT:
                        int[] iData = inField.getData(component).getIData();
                        int iMax = (int) inField.getData(component).getMaxv();
                        int iMin = (int) inField.getData(component).getMinv();
                        float is = 255.0f / (iMax - iMin);
                        RegularFieldInterpolator.interpolateScalarFieldToSliceRaster(iData, dims, tmp, b0, iMin, iMax, is, fastRaster, w, h, params.isCustomOrthoPlanesInterpolation());
                        break;
                    case DataArray.FIELD_DATA_FLOAT:
                        float[] fData = inField.getData(component).getFData();
                        float fMax = inField.getData(component).getMaxv();
                        float fMin = inField.getData(component).getMinv();
                        float fs = 255.0f / (fMax - fMin);
                        RegularFieldInterpolator.interpolateScalarFieldToSliceRaster(fData, dims, tmp, b0, fMin, fMax, fs, fastRaster, w, h, params.isCustomOrthoPlanesInterpolation());
                        break;
                    case DataArray.FIELD_DATA_DOUBLE:
                        double[] dData = inField.getData(component).getDData();
                        double dMax = inField.getData(component).getMaxv();
                        double dMin = inField.getData(component).getMinv();
                        double ds = 255.0f / (dMax - dMin);
                        RegularFieldInterpolator.interpolateScalarFieldToSliceRaster(dData, dims, tmp, b0, dMin, dMax, ds, fastRaster, w, h, params.isCustomOrthoPlanesInterpolation());
                        break;
                    case DataArray.FIELD_DATA_COMPLEX:
                        float[] fAbsData = ((ComplexDataArray) inField.getData(component)).getFAbsData();
                        float fAbsMax = inField.getData(component).getMaxv();
                        float fAbsMin = inField.getData(component).getMinv();
                        float fAbss = 255.0f / (fAbsMax - fAbsMin);
                        RegularFieldInterpolator.interpolateScalarFieldToSliceRaster(fAbsData, dims, tmp, b0, fAbsMin, fAbsMax, fAbss, fastRaster, w, h, params.isCustomOrthoPlanesInterpolation());
                        break;
                    case DataArray.FIELD_DATA_LOGIC:
                        if (params.isCustomOrthoPlanesInterpolation()) {
                            byte[] byteData = ((LogicDataArray) inField.getData(component)).getBitArray().getByteArray();
                            byte[] interpolatedData;
                            for (int j = 0; j < h; j++) {
                                for (int i = 0; i < w; i++) {
                                    x = (int) tmp[0];
                                    y = (int) tmp[1];
                                    z = (int) tmp[2];
                                    if (x >= 0 && x < dims[0] && y >= 0 && y < dims[1] && z >= 0 && z < dims[2]) {
                                        interpolatedData = inField.getInterpolatedData(byteData, tmp[0], tmp[1], tmp[2]);
                                        if (interpolatedData != null && interpolatedData.length == 1) {
                                            fastRaster.setSample(i, j, 0, 255 * (int) (0xFF & interpolatedData[0]));
                                        }
                                    } else {
                                        fastRaster.setSample(i, j, 0, 0);
                                    }
                                    tmp[0] += b0[0][0];
                                    tmp[1] += b0[0][1];
                                    tmp[2] += b0[0][2];
                                }
                                tmp[0] -= w * b0[0][0];
                                tmp[1] -= w * b0[0][1];
                                tmp[2] -= w * b0[0][2];
                                tmp[0] += b0[1][0];
                                tmp[1] += b0[1][1];
                                tmp[2] += b0[1][2];
                            }
                        } else {
                            BitArray logicData = ((LogicDataArray) inField.getData(component)).getBitArray();
                            tmp[0] += 0.5f;
                            tmp[1] += 0.5f;
                            tmp[2] += 0.5f;
                            for (int j = 0; j < h; j++) {
                                for (int i = 0; i < w; i++) {
                                    x = (int) tmp[0];
                                    y = (int) tmp[1];
                                    z = (int) tmp[2];
                                    if (x >= 0 && x < dims[0] && y >= 0 && y < dims[1] && z >= 0 && z < dims[2]) {
                                        fastRaster.setSample(i, j, 0, 255 * (int) (0xff & logicData.getByteValueAtIndex(z * dims[0] * dims[1] + y * dims[0] + x)));
                                    } else {
                                        fastRaster.setSample(i, j, 0, 0);
                                    }
                                    tmp[0] += b0[0][0];
                                    tmp[1] += b0[0][1];
                                    tmp[2] += b0[0][2];
                                }
                                tmp[0] -= w * b0[0][0];
                                tmp[1] -= w * b0[0][1];
                                tmp[2] -= w * b0[0][2];
                                tmp[0] += b0[1][0];
                                tmp[1] += b0[1][1];
                                tmp[2] += b0[1][2];
                            }
                        }
                        break;
                    default:
                        return;
                }
                break;
        }
        if (!silent) {
            fireDataProviderCustomOrthoSliceUpdated(axis);
        }
    }

    private void updateCustomOrthoPlaneOverlay(int axis, boolean silent) {
        for (int i = 0; i < customOrthoSlicesOverlays.length; i++) {
            customOrthoSlicesOverlays[i] = null;
        }
        
        if(params.isSimpleOverlay()) {
            if(inField == null)
                return;
            
            float[][] affine = inField.getAffine();
            int[] dims = inField.getDims();
            float[][] base = customOrthoPlanes[axis].getBase();
            float[][] extents = customOrthoPlanes[axis].getBaseExtents();

            float[] tmp = new float[3];
            for (int i = 0; i < 3; i++) {
                tmp[i] = Math.abs(base[0][i] + base[1][i]);
            }
            float min = tmp[0];
            int mini = 0;
            if (tmp[1] < min) {
                min = tmp[1];
                mini = 1;
            }
            if (tmp[2] < min) {
                min = tmp[2];
                mini = 2;
            }

            int i0 = 0, i1 = 1;
            switch (mini) {
                case 0:
                    i0 = 1;
                    i1 = 2;
                    break;
                case 1:
                    i0 = 0;
                    i1 = 2;
                    break;
                case 2:
                    i0 = 0;
                    i1 = 1;
                    break;
            }

            int w, h;
            if (extents == null) {
                customOrthoSlicesOverlays[axis] = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY);
                if (!silent) {
                    fireDataProviderCustomOrthoSliceUpdated(axis);
                }
                return;
            }
            for (int i = 0; i < 3; i++) {
                tmp[i] = extents[1][i] - extents[0][i];
            }
            float detA = base[0][i0] * base[1][i1] - base[1][i0] * base[0][i1];
            w = (int) Math.round(Math.abs((tmp[i0] * base[1][i1] - base[1][i0] * tmp[i1]) / detA)) + 1;
            h = (int) Math.round(Math.abs((tmp[i1] * base[0][i0] - base[0][i1] * tmp[i0]) / detA)) + 1;

            if (w <= 0 || h <= 0) {
                customOrthoSlicesOverlays[axis] = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY);
                if (!silent) {
                    fireDataProviderCustomOrthoSliceUpdated(axis);
                }
                return;
            }

            float[][] A = new float[3][3];
            float[] b = new float[3];
            float[][] b0 = new float[2][3];

            A[0][0] = affine[0][0];
            A[1][0] = affine[0][1];
            A[2][0] = affine[0][2];
            A[0][1] = affine[1][0];
            A[1][1] = affine[1][1];
            A[2][1] = affine[1][2];
            A[0][2] = affine[2][0];
            A[1][2] = affine[2][1];
            A[2][2] = affine[2][2];

            int component = params.getSimpleOverlayComponent();
            b0 = new float[2][3];
            for (int k = 0; k < 3; k++) {
                b[k] = extents[0][k] - affine[3][k];
            }
            tmp = NumericalMethods.lsolve3x3(A, b);

            b0[0] = NumericalMethods.lsolve3x3(A, base[0]);
            b0[1] = NumericalMethods.lsolve3x3(A, base[1]);

            float[] emptyPixel = new float[4];
            float[] overlaidPixel = new float[4];            
            Color c = params.getSimpleOverlayColor();            
            
            if(params.isSimpleOverlayMask()) {
                if(params.isSimpleOverlayInvert()) {
                    emptyPixel = new float[]{c.getRed(),c.getGreen(),c.getBlue(),0.0f};
                    overlaidPixel = new float[]{0.0f,0.0f,0.0f,255.0f};
                } else {
                    emptyPixel = new float[]{0.0f,0.0f,0.0f,255.0f};
                    overlaidPixel = new float[]{c.getRed(),c.getGreen(),c.getBlue(),0.0f};
                }
            } else {
                if(params.isSimpleOverlayInvert()) {
                    emptyPixel = new float[]{c.getRed(),c.getGreen(),c.getBlue(),255.0f};
                    overlaidPixel = new float[]{0.0f,0.0f,0.0f,0.0f};
                } else {
                    emptyPixel = new float[]{0.0f,0.0f,0.0f,0.0f};
                    overlaidPixel = new float[]{c.getRed(),c.getGreen(),c.getBlue(),255.0f};
                }
            }
            
            float low = params.getSimpleOverlayLow();
            float up = params.getSimpleOverlayUp();
            
            switch(inField.getData(component).getType()) {
                case DataArray.FIELD_DATA_BYTE:
                    byte[] outBData = new byte[w*h];            
                    RegularFieldInterpolator.interpolateScalarFieldToSlice(inField.getData(component).getBData(), dims, tmp, b0, outBData, w, h, params.isCustomOrthoPlanesInterpolation());
                    customOrthoSlicesOverlays[axis] = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                    int bb;
                    for (int j = 0; j < h; j++) {
                        for (int i = 0; i < w; i++) {
                            bb = (int)(outBData[j * w + i]&0xff);
                            if(bb < low || bb > up) {
                                customOrthoSlicesOverlays[axis].getRaster().setPixel(i, j, emptyPixel);                                
                            } else {
                                customOrthoSlicesOverlays[axis].getRaster().setPixel(i, j, overlaidPixel);                                
                            }
                        }
                    }
                    break;
                case DataArray.FIELD_DATA_SHORT:
                    short[] outSData = new short[w*h];            
                    RegularFieldInterpolator.interpolateScalarFieldToSlice(inField.getData(component).getSData(), dims, tmp, b0, outSData, w, h, params.isCustomOrthoPlanesInterpolation());
                    customOrthoSlicesOverlays[axis] = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                    short ss;
                    for (int j = 0; j < h; j++) {
                        for (int i = 0; i < w; i++) {
                            ss = outSData[j * w + i];
                            if(ss < low || ss > up) {
                                customOrthoSlicesOverlays[axis].getRaster().setPixel(i, j, emptyPixel);                                
                            } else {
                                customOrthoSlicesOverlays[axis].getRaster().setPixel(i, j, overlaidPixel);                                
                            }
                        }
                    }
                    break;
                case DataArray.FIELD_DATA_INT:
                    int[] outIData = new int[w*h];            
                    RegularFieldInterpolator.interpolateScalarFieldToSlice(inField.getData(component).getIData(), dims, tmp, b0, outIData, w, h, params.isCustomOrthoPlanesInterpolation());
                    customOrthoSlicesOverlays[axis] = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                    int ii;
                    for (int j = 0; j < h; j++) {
                        for (int i = 0; i < w; i++) {
                            ii = outIData[j * w + i];
                            if(ii < low || ii > up) {
                                customOrthoSlicesOverlays[axis].getRaster().setPixel(i, j, emptyPixel);                                
                            } else {
                                customOrthoSlicesOverlays[axis].getRaster().setPixel(i, j, overlaidPixel);                                
                            }
                        }
                    }
                    break;
                case DataArray.FIELD_DATA_FLOAT:
                    float[] outFData = new float[w*h];            
                    RegularFieldInterpolator.interpolateScalarFieldToSlice(inField.getData(component).getFData(), dims, tmp, b0, outFData, w, h, params.isCustomOrthoPlanesInterpolation());
                    customOrthoSlicesOverlays[axis] = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                    float ff;
                    for (int j = 0; j < h; j++) {
                        for (int i = 0; i < w; i++) {
                            ff = outFData[j * w + i];
                            if(ff < low || ff > up) {
                                customOrthoSlicesOverlays[axis].getRaster().setPixel(i, j, emptyPixel);                                
                            } else {
                                customOrthoSlicesOverlays[axis].getRaster().setPixel(i, j, overlaidPixel);                                
                            }
                        }
                    }
                    break;
                case DataArray.FIELD_DATA_DOUBLE:
                    double[] outDData = new double[w*h];            
                    RegularFieldInterpolator.interpolateScalarFieldToSlice(inField.getData(component).getDData(), dims, tmp, b0, outDData, w, h, params.isCustomOrthoPlanesInterpolation());
                    customOrthoSlicesOverlays[axis] = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                    double dd;
                    for (int j = 0; j < h; j++) {
                        for (int i = 0; i < w; i++) {
                            dd = outDData[j * w + i];
                            if(dd < low || dd > up) {
                                customOrthoSlicesOverlays[axis].getRaster().setPixel(i, j, emptyPixel);                                
                            } else {
                                customOrthoSlicesOverlays[axis].getRaster().setPixel(i, j, overlaidPixel);                                
                            }
                        }
                    }
                    break;
            }
            
            if (!silent) {
                fireDataProviderOverlayUpdated(axis);
            }
            
        } else {
            if (overlayField == null || overlayField.getDims().length != 3) {
                customOrthoSlicesOverlays[axis] = null;
                return;
            }

            float[][] affine = overlayField.getAffine();
            int[] dims = overlayField.getDims();
            float[][] base = customOrthoPlanes[axis].getBase();
            float[][] extents = customOrthoPlanes[axis].getBaseExtents();

            float[] tmp = new float[3];
            for (int i = 0; i < 3; i++) {
                tmp[i] = Math.abs(base[0][i] + base[1][i]);
            }
            float min = tmp[0];
            int mini = 0;
            if (tmp[1] < min) {
                min = tmp[1];
                mini = 1;
            }
            if (tmp[2] < min) {
                min = tmp[2];
                mini = 2;
            }

            int i0 = 0, i1 = 1;
            switch (mini) {
                case 0:
                    i0 = 1;
                    i1 = 2;
                    break;
                case 1:
                    i0 = 0;
                    i1 = 2;
                    break;
                case 2:
                    i0 = 0;
                    i1 = 1;
                    break;
            }

            int w, h;
            if (extents == null) {
                customOrthoSlicesOverlays[axis] = null;
                if (!silent) {
                    fireDataProviderOverlayUpdated(axis);
                }
                return;
            }
            for (int i = 0; i < 3; i++) {
                tmp[i] = extents[1][i] - extents[0][i];
            }
            float detA = base[0][i0] * base[1][i1] - base[1][i0] * base[0][i1];
            w = (int) Math.round(Math.abs((tmp[i0] * base[1][i1] - base[1][i0] * tmp[i1]) / detA)) + 1;
            h = (int) Math.round(Math.abs((tmp[i1] * base[0][i0] - base[0][i1] * tmp[i0]) / detA)) + 1;

            if (w <= 0 || h <= 0) {
                customOrthoSlicesOverlays[axis] = null;
                if (!silent) {
                    fireDataProviderOverlayUpdated(axis);
                }
                return;
            }

            float[][] A = new float[3][3];
            float[] b = new float[3];
            float[][] b0 = new float[2][3];
            A[0][0] = affine[0][0];
            A[1][0] = affine[0][1];
            A[2][0] = affine[0][2];
            A[0][1] = affine[1][0];
            A[1][1] = affine[1][1];
            A[2][1] = affine[1][2];
            A[0][2] = affine[2][0];
            A[1][2] = affine[2][1];
            A[2][2] = affine[2][2];

            int component = 0;
            b0 = new float[2][3];
            for (int k = 0; k < 3; k++) {
                b[k] = extents[0][k] - affine[3][k];
            }
            tmp = NumericalMethods.lsolve3x3(A, b);
            b0[0] = NumericalMethods.lsolve3x3(A, base[0]);
            b0[1] = NumericalMethods.lsolve3x3(A, base[1]);

            ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_sRGB);
            int[] nBits = {8, 8, 8, 8};
            ComponentColorModel colorModel = new ComponentColorModel(colorSpace, nBits, true, true, Transparency.TRANSLUCENT, 0);
            WritableRaster raster = colorModel.createCompatibleWritableRaster(w, h);
            customOrthoSlicesOverlays[axis] = new BufferedImage(colorModel, raster, false, null);
            float low = 1;
            float up = overlayField.getData(component).getMaxv();
            RegularFieldInterpolator.interpolateScalarFieldToSliceColormappedImage(overlayField.getData(component).getBData(), dims, tmp, b0, customOrthoSlicesOverlays[axis], overlayColorMapLUT, low, up, params.getSliceFillColor(), w, h, false, 0, 0x77);
            if (!silent) {
                fireDataProviderOverlayUpdated(axis);
            }
        }
        
    }

    public GeometryObject getCustomOrthoSlices3DPlanes() {
        return customOrthoSlices3DPlanes;
    }

    private void fireDataProviderCustomOrthoSliceUpdated(int axis) {
        for (DataProviderListener listener : listenerList) {
            listener.onDataProviderCustomOrthoSliceUpdated(axis);
        }
    }

    private void updateColorMap() {
        this.colorMap = params.getDataMappingParams().getColorMap0Params().getMap();
        this.colorMapLUT = colorMap.getRGBColorTable();        
    }

    public static RegularField createCustomSliceField(float[] p, float[] v, RegularField field, int component) {
        if (field == null || field.getDims().length != 3) {
            return null;
        }

        float[][] affine = field.getAffine();
        int[] dims = field.getDims();

        float[] find = field.getFloatIndices(p[0], p[1], p[2]);
        if(find[0] < 0 || find[0] >= dims[0] || find[1] < 0 || find[1] >= dims[1] || find[2] < 0 || find[2] >= dims[2])
            return null;

        SimplifiedPlane3D p3d = new SimplifiedPlane3D(p, v, field);
        float[][] base = p3d.getBase();
        float[][] extents = p3d.getBaseExtents();

        float[] norm = new float[2];
        norm[0] = 0;
        norm[1] = 0;
        float[] upp = new float[3];
        for (int i = 0; i < 3; i++) {
            upp[i] = 0;
            for (int j = 0; j < 3; j++) {
                upp[i] += affine[i][j] * affine[i][j];
            }
        }
        for (int i = 0; i < 3; i++) {
            norm[0] += base[0][i] * base[0][i] / upp[i];
            norm[1] += base[1][i] * base[1][i] / upp[i];
        }
        norm[0] = (float) Math.sqrt(norm[0]);
        norm[1] = (float) Math.sqrt(norm[1]);

        for (int i = 0; i < 3; i++) {
            base[0][i] = base[0][i] / norm[0];
            base[1][i] = base[1][i] / norm[1];
        }
        

        float[] tmp = new float[3];
        for (int i = 0; i < 3; i++) {
            tmp[i] = Math.abs(base[0][i] + base[1][i]);
        }
        float min = tmp[0];
        int mini = 0;
        if (tmp[1] < min) {
            min = tmp[1];
            mini = 1;
        }
        if (tmp[2] < min) {
            min = tmp[2];
            mini = 2;
        }

        int i0 = 0, i1 = 1;
        switch (mini) {
            case 0:
                i0 = 1;
                i1 = 2;
                break;
            case 1:
                i0 = 0;
                i1 = 2;
                break;
            case 2:
                i0 = 0;
                i1 = 1;
                break;
        }

        int w, h;
        if (extents == null) {
            return null;
        }
        for (int i = 0; i < 3; i++) {
            tmp[i] = extents[1][i] - extents[0][i];
        }
        float detA = base[0][i0] * base[1][i1] - base[1][i0] * base[0][i1];
        w = (int) Math.round(Math.abs((tmp[i0] * base[1][i1] - base[1][i0] * tmp[i1]) / detA)) + 1;
        h = (int) Math.round(Math.abs((tmp[i1] * base[0][i0] - base[0][i1] * tmp[i0]) / detA)) + 1;

        if (w <= 1 || h <= 1) {
            return null;
        }

        float[][] A = new float[3][3];
        float[] b = new float[3];
        float[][] b0 = new float[2][3];

        A[0][0] = affine[0][0];
        A[1][0] = affine[0][1];
        A[2][0] = affine[0][2];
        A[0][1] = affine[1][0];
        A[1][1] = affine[1][1];
        A[2][1] = affine[1][2];
        A[0][2] = affine[2][0];
        A[1][2] = affine[2][1];
        A[2][2] = affine[2][2];

        int x, y, z, off;
        b0 = new float[2][3];
        for (int k = 0; k < 3; k++) {
            b[k] = extents[0][k] - affine[3][k];
        }
        tmp = NumericalMethods.lsolve3x3(A, b);

        b0[0] = NumericalMethods.lsolve3x3(A, base[0]);
        b0[1] = NumericalMethods.lsolve3x3(A, base[1]);

        byte[] outData;
        int[] outDims;
        float[][] outAffine;

        outData = new byte[w*h];
        outDims = new int[]{w,h};
        RegularFieldInterpolator.interpolateScalarFieldToSlice(field.getData(component).getBData(), dims, tmp, b0, outData, w, h, true);


        outAffine = new float[4][3];
        for (int i = 0; i < 3; i++) {
            outAffine[0][i] = base[0][i];
            outAffine[1][i] = base[1][i];
            outAffine[2][i] = 0.0f;
            outAffine[3][i] = extents[0][i];
        }


        RegularField out = new RegularField(outDims);
        out.setNSpace(3);
        out.setAffine(outAffine);
        out.addData(DataArray.create(outData, 1, "slice_"+field.getData(component).getName()));
        return out;
    }

}
