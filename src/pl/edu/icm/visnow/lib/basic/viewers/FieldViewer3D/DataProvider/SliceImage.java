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

import java.awt.image.*;
import java.awt.*;
import java.awt.color.ColorSpace;
import pl.edu.icm.visnow.datamaps.ColorMapManager;
import pl.edu.icm.visnow.datamaps.colormap1d.DefaultColorMap1D;
import pl.edu.icm.visnow.datasets.RegularField;

/**
 *
 * @author  Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class SliceImage {

    private RegularField inField = null;
    private DefaultColorMap1D cMap = ColorMapManager.getInstance().getColorMap1D(ColorMapManager.COLORMAP1D_GRAY);
    private int[] components = {0, 0, 0};
    private int[] weights = {100, 100, 100};
    private boolean multiselImage = false;
    private byte[] colorMapLUT = null;

    /** Creates a new instance of SliceImage */
    public SliceImage(RegularField inField) {
        if (inField.getDims().length != 3) {
            return;
        }
        this.inField = inField;
    }

    public void setColorMap(DefaultColorMap1D cMap) {
        this.cMap = cMap;
    }

    public void setColorMap(int cMap) {
        this.cMap = ColorMapManager.getInstance().getColorMap1D(cMap);

    }

    public void selectComponent(int comp) {
        if (comp < 0 || comp >= inField.getNData()) {
            return;
        }
        components[0] = components[1] = components[2] = comp;
    }

    public void setMultiselImage(boolean multiselImage) {
        this.multiselImage = multiselImage;
    }

    public void selectComponents(int[] comp) {
        if (comp == null || comp.length != 3 ||
                comp[0] < 0 || comp[0] >= inField.getNData() ||
                comp[1] < 0 || comp[1] >= inField.getNData() ||
                comp[2] < 0 || comp[2] >= inField.getNData()) {
            return;
        }
        setComponents(comp);
    }

    public BufferedImage getSlice(int axis, int slice) {
        if (axis < 0 || axis > 2) {
            return null;
        }
        if (slice < 0 || slice >= inField.getDims()[axis]) {
            return null;
        }
        int nn0 = 0, nn1 = 1;
        if (axis == 0) {
            nn0 = 1;
            nn1 = 2;
        }
        if (axis == 1) {
            nn1 = 2;
        }
        int n0 = inField.getDims()[nn0];
        int n1 = inField.getDims()[nn1];
        int i, i0, i1;
        int[] rgba = new int[4];
//        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
//        int[] nBits = {8, 8, 8, 8};
//        ComponentColorModel colorModel = new ComponentColorModel(cs, nBits,
//                true, true, Transparency.TRANSLUCENT, 0);
//        WritableRaster raster =
//                colorModel.createCompatibleWritableRaster(n0, n1);
//        BufferedImage bImage =
//                new BufferedImage(colorModel, raster, false, null);
        
        BufferedImage bImage = new BufferedImage(n0,n1, BufferedImage.TYPE_INT_ARGB);
        WritableRaster raster = bImage.getRaster();        
        
        if (multiselImage) {
            int component = components[0];
            float[] data = inField.get2DSliceData(component, axis, slice);
            cMap = ColorMapManager.getInstance().getColorMap1D(ColorMapManager.COLORMAP1D_RAINBOW);
            colorMapLUT = cMap.getRGBByteColorTable();
            float low = 1;
            float up = inField.getData(component).getMaxv();
            int colorMapSize = (colorMapLUT.length/3);
            float s = (float) colorMapSize / (up - low);
            int c;
            for (i1 = 0; i1 < n1; i1++) {
                for (i0 = 0; i0 < n0; i0++) {
                    float d = data[i1 * n0 + i0];
                    c = (int) ((d - low) * s);
                    if (c < 0) {
                        c = 0;
                    }
                    if (c >= colorMapSize) {
                        c = colorMapSize-1;
                    }
                    for (int k = 0; k < 3; k++) {
                        rgba[k] = (int)(colorMapLUT[3*c + k]&0xff);
                    }
                    rgba[3] = 0x77;
                    if (d == 0) {
                        rgba[3] = 0;
                    }
                    raster.setPixel(i0, i1, rgba);
                }
            }
        } else if ((components[0] == components[1]) && (components[1] == components[2])) {
            colorMapLUT = cMap.getRGBByteColorTable();
            int component = components[0];
            float low = inField.getData(component).getMinv();
            float up = inField.getData(component).getMaxv();
            int colorMapSize = colorMapLUT.length - 1;
            float s = (float) colorMapSize / (up - low);
            int c;
            float d;
            float[] data = inField.get2DSliceData(component, axis, slice);
            for (i1 = 0; i1 < n1; i1++) {
                for (i0 = 0; i0 < n0; i0++) {
                    d = data[i1 * n0 + i0];
                    c = (int) ((d - low) * s);
                    if (c < 0) {
                        c = 0;
                    }
                    if (c > colorMapSize/3) {
                        c = colorMapSize/3;
                    }
                    for (int k = 0; k < 3; k++) {
                        rgba[k] = (int)(colorMapLUT[3*c + k]&0xff);
                    }
                    rgba[3] = 0xFF;
                    raster.setPixel(i0, i1, rgba);
                }
            }
        } else {
            float[][] data = new float[3][];
            float[] low = new float[3];
            float[] up = new float[3];
            for (int j = 0; j < components.length; j++) {
                int component = components[j];
                data[j] = inField.get2DSliceData(component, axis, slice);
                low[j] = inField.getData(component).getMinv();
                up[j] = inField.getData(component).getMaxv();
            }
            for (i1 = 0; i1 < n1; i1++) {
                for (i0 = 0; i0 < n0; i0++) {
                    for (int c = 0; c < 3; c++) {
                        rgba[c] = (int) (255 * weights[c] * (data[c][i1 * n0 + i0] - low[c]) / (100. * (up[c] - low[c])));
                        if (rgba[c] > 255) {
                            rgba[c] = 255;
                        }
                    }
                    rgba[3] = 0xFF;
                    raster.setPixel(i0, i1, rgba);
                }
            }
        }
        return bImage;
    }

    public void setComponents(int[] components) {
        this.components = components;
    }

    public void setWeights(int[] weights) {
        this.weights = weights;
    }
}
