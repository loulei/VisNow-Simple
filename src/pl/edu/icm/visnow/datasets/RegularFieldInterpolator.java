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

package pl.edu.icm.visnow.datasets;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 * @author Bartosz Borucki (babor@icm.edu.pl) University of Warsaw,
 * Interdisciplinary Centre for Mathematical and Computational Modelling
 */
public class RegularFieldInterpolator {

    public static byte[] getInterpolatedData(byte[] data, int[] dims, float u, float v, float w) {
        if (data == null || dims == null) {
            return null;
        }
        byte[] c;
        int vlen;
        int inexact = 0;
        int i, j, k, m, n0, n1;
        switch (dims.length) {
            case 3:
                vlen = data.length / (dims[0] * dims[1] * dims[2]);
                if (data.length != vlen * dims[0] * dims[1] * dims[2]) {
                    return null;
                }
                c = new byte[vlen];
                if (vlen == 1) {
                    c[0] = getInterpolatedScalarData(data, dims, u, v, w);
                    return c;
                }
                if (u < 0) {
                    u = 0;
                }
                if (u > dims[0] - 1) {
                    u = dims[0] - 1;
                }
                if (v < 0) {
                    v = 0;
                }
                if (v > dims[1] - 1) {
                    v = dims[1] - 1;
                }
                if (w < 0) {
                    w = 0;
                }
                if (w > dims[2] - 1) {
                    w = dims[2] - 1;
                }
                inexact = 0;
                i = (int) u;
                u -= i;
                if (u != 0) {
                    inexact += 1;
                }
                j = (int) v;
                v -= j;
                if (v != 0) {
                    inexact += 2;
                }
                k = (int) w;
                w -= k;
                if (w != 0) {
                    inexact += 4;
                }
                m = vlen * ((dims[1] * k + j) * dims[0] + i);
                n0 = vlen * dims[0];
                n1 = vlen * dims[0] * dims[1];
                switch (inexact) {
                    case 0:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = data[m + l];
                        }
                        break;
                    case 1:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = (byte) (u * (0xFF & data[m + l + vlen]) + (1 - u) * (0xFF & data[m + l]));
                        }
                        break;
                    case 2:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = (byte) (v * (0xFF & data[m + l + n0]) + (1 - v) * (0xFF & data[m + l]));
                        }
                        break;
                    case 3:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = (byte) (v * (u * (0xFF & data[m + l + n0 + vlen]) + (1 - u) * (0xFF & data[m + l + n0]))
                                    + (1 - v) * (u * (0xFF & data[m + l + vlen]) + (1 - u) * (0xFF & data[m + l])));
                        }
                        break;
                    case 4:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = (byte) (w * (0xFF & data[m + l + n1]) + (1 - w) * (0xFF & data[m + l]));
                        }
                        break;
                    case 5:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = (byte) (w * (u * (0xFF & data[m + l + n1 + vlen]) + (1 - u) * (0xFF & data[m + l + n1]))
                                    + (1 - w) * (u * (0xFF & data[m + l + vlen]) + (1 - u) * (0xFF & data[m + l])));
                        }
                        break;
                    case 6:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = (byte) (w * (v * (0xFF & data[m + l + n1 + n0]) + (1 - v) * (0xFF & data[m + l + n1]))
                                    + (1 - w) * (v * (0xFF & data[m + l + n0]) + (1 - v) * (0xFF & data[m + l])));
                        }
                        break;
                    case 7:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = (byte) (w * (v * (u * (0xFF & data[m + l + n1 + n0 + vlen]) + (1 - u) * (0xFF & data[m + l + n1 + n0]))
                                    + (1 - v) * (u * (0xFF & data[m + l + n1 + vlen]) + (1 - u) * (0xFF & data[m + l + n1])))
                                    + (1 - w) * (v * (u * (0xFF & data[m + l + n0 + vlen]) + (1 - u) * (0xFF & data[m + l + n0]))
                                    + (1 - v) * (u * (0xFF & data[m + l + vlen]) + (1 - u) * (0xFF & data[m + l]))));
                        }
                        break;
                }
                return c;
            case 2:
                vlen = data.length / (dims[0] * dims[1]);
                if (data.length != vlen * dims[0] * dims[1]) {
                    return null;
                }
                c = new byte[vlen];
                if (vlen == 1) {
                    c[0] = getInterpolatedScalarData(data, dims, u, v, w);
                    return c;
                }
                if (u < 0) {
                    u = 0;
                }
                if (u > dims[0] - 1) {
                    u = dims[0] - 1;
                }
                if (v < 0) {
                    v = 0;
                }
                if (v > dims[1] - 1) {
                    v = dims[1] - 1;
                }
                inexact = 0;
                i = (int) u;
                u -= i;
                if (u != 0) {
                    inexact += 1;
                }
                j = (int) v;
                v -= j;
                if (v != 0) {
                    inexact += 2;
                }
                m = vlen * (j * dims[0] + i);
                n0 = vlen * dims[0];
                switch (inexact) {
                    case 0:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = data[m + l];
                        }
                        break;
                    case 1:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = (byte) (u * (0xFF & data[m + l + vlen]) + (1 - u) * (0xFF & data[m + l]));
                        }
                        break;
                    case 2:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = (byte) (v * (0xFF & data[m + l + n0]) + (1 - v) * (0xFF & data[m + l]));
                        }
                        break;
                    case 3:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = (byte) (v * (u * (0xFF & data[m + l + n0 + vlen]) + (1 - u) * (0xFF & data[m + l + n0]))
                                    + (1 - v) * (u * (0xFF & data[m + l + vlen]) + (1 - u) * (0xFF & data[m + l])));
                        }
                        break;
                }
                return c;
            case 1:
                vlen = data.length / dims[0];
                if (data.length != vlen * dims[0]) {
                    return null;
                }
                c = new byte[vlen];
                if (vlen == 1) {
                    c[0] = getInterpolatedScalarData(data, dims, u, v, w);
                    return c;
                }
                if (u < 0) {
                    u = 0;
                }
                if (u > dims[0] - 1) {
                    u = dims[0] - 1;
                }
                inexact = 0;
                i = (int) u;
                u -= i;
                if (u != 0) {
                    inexact += 1;
                }
                m = vlen * i;
                switch (inexact) {
                    case 0:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = data[m + l];
                        }
                        break;
                    case 1:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = (byte) (u * (0xFF & data[m + l + vlen]) + (1 - u) * (0xFF & data[m + l]));
                        }
                        break;
                }
                return c;
        }
        return null;
    }

    public static byte getInterpolatedScalarData(byte[] data, int[] dims, float u, float v, float w) {
        if (data == null || dims == null) {
            return 0;
        }

        switch (dims.length) {
            case 3:
                return getInterpolatedScalarData3D(data, dims, u, v, w);
            case 2:
                return getInterpolatedScalarData2D(data, dims, u, v, w);
            case 1:
                return getInterpolatedScalarData1D(data, dims, u, v, w);
        }
        return 0;
    }

    public static byte getInterpolatedScalarData3D(byte[] data, int[] dims, float u, float v, float w) {
        if (data == null || dims == null || dims.length != 3) {
            return 0;
        }
        byte c = 0;
        int inexact = 0;
        int i, j, k, m, n0, n1;
        if (data.length != dims[0] * dims[1] * dims[2]) {
            return 0;
        }
        if (u < 0) {
            u = 0;
        }
        if (u > dims[0] - 1) {
            u = dims[0] - 1;
        }
        if (v < 0) {
            v = 0;
        }
        if (v > dims[1] - 1) {
            v = dims[1] - 1;
        }
        if (w < 0) {
            w = 0;
        }
        if (w > dims[2] - 1) {
            w = dims[2] - 1;
        }
        inexact = 0;
        i = (int) u;
        u -= i;
        if (u != 0) {
            inexact += 1;
        }
        j = (int) v;
        v -= j;
        if (v != 0) {
            inexact += 2;
        }
        k = (int) w;
        w -= k;
        if (w != 0) {
            inexact += 4;
        }
        m = (dims[1] * k + j) * dims[0] + i;
        n0 = dims[0];
        n1 = dims[0] * dims[1];
        switch (inexact) {
            case 0:
                c = data[m];
                break;
            case 1:
                c = (byte) (u * (0xFF & data[m + 1]) + (1 - u) * (0xFF & data[m]));
                break;
            case 2:
                c = (byte) (v * (0xFF & data[m + n0]) + (1 - v) * (0xFF & data[m]));
                break;
            case 3:
                c = (byte) (v * (u * (0xFF & data[m + n0 + 1]) + (1 - u) * (0xFF & data[m + n0]))
                        + (1 - v) * (u * (0xFF & data[m + 1]) + (1 - u) * (0xFF & data[m])));
                break;
            case 4:
                c = (byte) (w * (0xFF & data[m + n1]) + (1 - w) * (0xFF & data[m]));
                break;
            case 5:
                c = (byte) (w * (u * (0xFF & data[m + n1 + 1]) + (1 - u) * (0xFF & data[m + n1]))
                        + (1 - w) * (u * (0xFF & data[m + 1]) + (1 - u) * (0xFF & data[m])));
                break;
            case 6:
                c = (byte) (w * (v * (0xFF & data[m + n1 + n0]) + (1 - v) * (0xFF & data[m + n1]))
                        + (1 - w) * (v * (0xFF & data[m + n0]) + (1 - v) * (0xFF & data[m])));
                break;
            case 7:
                c = (byte) (w * (v * (u * (0xFF & data[m + n1 + n0 + 1]) + (1 - u) * (0xFF & data[m + n1 + n0]))
                        + (1 - v) * (u * (0xFF & data[m + n1 + 1]) + (1 - u) * (0xFF & data[m + n1])))
                        + (1 - w) * (v * (u * (0xFF & data[m + n0 + 1]) + (1 - u) * (0xFF & data[m + n0]))
                        + (1 - v) * (u * (0xFF & data[m + 1]) + (1 - u) * (0xFF & data[m]))));
                break;
        }
        return c;
    }
    
    public static byte getInterpolatedScalarData2D(byte[] data, int[] dims, float u, float v, float w) {
        if (data == null || dims == null || dims.length != 2) {
            return 0;
        }
        byte c = 0;
        int inexact = 0;
        int i, j, k, m, n0, n1;
        if (data.length != dims[0] * dims[1]) {
            return 0;
        }
        if (u < 0) {
            u = 0;
        }
        if (u > dims[0] - 1) {
            u = dims[0] - 1;
        }
        if (v < 0) {
            v = 0;
        }
        if (v > dims[1] - 1) {
            v = dims[1] - 1;
        }
        inexact = 0;
        i = (int) u;
        u -= i;
        if (u != 0) {
            inexact += 1;
        }
        j = (int) v;
        v -= j;
        if (v != 0) {
            inexact += 2;
        }
        m = j * dims[0] + i;
        n0 = dims[0];
        switch (inexact) {
            case 0:
                c = data[m];
                break;
            case 1:
                c = (byte) (u * (0xFF & data[m + 1]) + (1 - u) * (0xFF & data[m]));
                break;
            case 2:
                c = (byte) (v * (0xFF & data[m + n0]) + (1 - v) * (0xFF & data[m]));
                break;
            case 3:
                c = (byte) (v * (u * (0xFF & data[m + n0 + 1]) + (1 - u) * (0xFF & data[m + n0]))
                        + (1 - v) * (u * (0xFF & data[m + 1]) + (1 - u) * (0xFF & data[m])));
                break;
        }
        return c;
    }

    public static byte getInterpolatedScalarData1D(byte[] data, int[] dims, float u, float v, float w) {
        if (data == null || dims == null || dims.length != 1) {
            return 0;
        }
        byte c = 0;
        int inexact = 0;
        int i;
        if (data.length != dims[0]) {
            return 0;
        }
        if (u < 0) {
            u = 0;
        }
        if (u > dims[0] - 1) {
            u = dims[0] - 1;
        }
        inexact = 0;
        i = (int) u;
        u -= i;
        if (u != 0) {
            inexact += 1;
        }

        switch (inexact) {
            case 0:
                c = data[i];
                break;
            case 1:
                c = (byte) (u * (0xFF & data[i + 1]) + (1 - u) * (0xFF & data[i]));
                break;
        }
        return c;
    }
    
    public static void interpolateScalarFieldToSlice(byte[] bData, int[] dims, float[] p0, float[][] base,
            byte[] outData, int w, int h, boolean useTrilinearInterpolation) {
        if (bData == null || dims == null || outData == null || p0 == null || base == null || w <= 0 || h <= 0) {
            return;
        }

        if (outData.length != w * h) {
            return;
        }

        int x, y, z;
        float[] tmp = new float[3];
        tmp[0] = p0[0];
        tmp[1] = p0[1];
        tmp[2] = p0[2];

        if (useTrilinearInterpolation) {
            for (int j = 0, c = 0; j < h; j++) {
                for (int i = 0; i < w; i++, c++) {
                    x = (int) tmp[0];
                    y = (int) tmp[1];
                    z = (int) tmp[2];
                    if (x >= 0 && x < dims[0] && y >= 0 && y < dims[1] && z >= 0 && z < dims[2]) {
                        outData[c] = getInterpolatedScalarData(bData, dims, tmp[0], tmp[1], tmp[2]);
                    } else {
                        outData[c] = 0;
                    }
                    tmp[0] += base[0][0];
                    tmp[1] += base[0][1];
                    tmp[2] += base[0][2];
                }
                tmp[0] -= w * base[0][0];
                tmp[1] -= w * base[0][1];
                tmp[2] -= w * base[0][2];
                tmp[0] += base[1][0];
                tmp[1] += base[1][1];
                tmp[2] += base[1][2];
            }
        } else {
            tmp[0] += 0.5f;
            tmp[1] += 0.5f;
            tmp[2] += 0.5f;
            for (int j = 0, c = 0; j < h; j++) {
                for (int i = 0; i < w; i++, c++) {
                    x = (int) tmp[0];
                    y = (int) tmp[1];
                    z = (int) tmp[2];
                    if (x >= 0 && x < dims[0] && y >= 0 && y < dims[1] && z >= 0 && z < dims[2]) {
                        outData[c] = bData[z * dims[0] * dims[1] + y * dims[0] + x];
                    } else {
                        outData[c] = 0;
                    }
                    tmp[0] += base[0][0];
                    tmp[1] += base[0][1];
                    tmp[2] += base[0][2];
                }
                tmp[0] -= w * base[0][0];
                tmp[1] -= w * base[0][1];
                tmp[2] -= w * base[0][2];
                tmp[0] += base[1][0];
                tmp[1] += base[1][1];
                tmp[2] += base[1][2];
            }
        }
    }

    public static void interpolateScalarFieldToSliceRaster(byte[] bData, int[] dims, float[] p0, float[][] base,
            WritableRaster raster, int w, int h, boolean useTrilinearInterpolation) {
        if (bData == null || dims == null || raster == null || p0 == null || base == null || w <= 0 || h <= 0) {
            return;
        }

        if (raster.getWidth() != w || raster.getHeight() != h) {
            return;
        }

        int x, y, z;
        float[] tmp = new float[3];
        tmp[0] = p0[0];
        tmp[1] = p0[1];
        tmp[2] = p0[2];

        if (useTrilinearInterpolation) {
            for (int j = 0; j < h; j++) {
                for (int i = 0; i < w; i++) {
                    x = (int) tmp[0];
                    y = (int) tmp[1];
                    z = (int) tmp[2];
                    if (x >= 0 && x < dims[0] && y >= 0 && y < dims[1] && z >= 0 && z < dims[2]) {
                        raster.setSample(i, j, 0, 0xFF & getInterpolatedScalarData(bData, dims, tmp[0], tmp[1], tmp[2]));
                    } else {
                        raster.setSample(i, j, 0, 0);
                    }
                    tmp[0] += base[0][0];
                    tmp[1] += base[0][1];
                    tmp[2] += base[0][2];
                }
                tmp[0] -= w * base[0][0];
                tmp[1] -= w * base[0][1];
                tmp[2] -= w * base[0][2];
                tmp[0] += base[1][0];
                tmp[1] += base[1][1];
                tmp[2] += base[1][2];
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
                        raster.setSample(i, j, 0, 0xFF & bData[z * dims[0] * dims[1] + y * dims[0] + x]);
                    } else {
                        raster.setSample(i, j, 0, 0);
                    }
                    tmp[0] += base[0][0];
                    tmp[1] += base[0][1];
                    tmp[2] += base[0][2];
                }
                tmp[0] -= w * base[0][0];
                tmp[1] -= w * base[0][1];
                tmp[2] -= w * base[0][2];
                tmp[0] += base[1][0];
                tmp[1] += base[1][1];
                tmp[2] += base[1][2];
            }
        }
    }

    public static void interpolateFieldToSliceColormappedImage(byte[] bData, int veclen, int[] dims, float[] p0, float[][] base,
            BufferedImage image, int[] colorMapLUT, float low, float up,
            int fillColor, int w, int h, boolean useTrilinearInterpolation) {
        if (veclen == 1) {
            interpolateScalarFieldToSliceColormappedImage(bData, dims, p0, base, image, colorMapLUT, low, up, fillColor, w, h, useTrilinearInterpolation);
        } else {
            interpolateVectorFieldToSliceColormappedImage(bData, veclen, dims, p0, base, image, colorMapLUT, low, up, fillColor, w, h, useTrilinearInterpolation);
        }
    }
    
    public static void interpolateScalarFieldToSliceColormappedImage(byte[] bData, int[] dims, float[] p0, float[][] base,
            BufferedImage image, int[] colorMapLUT, float low, float up,
            int fillColor, int w, int h, boolean useTrilinearInterpolation) {
        if (bData == null || dims == null || image == null || p0 == null || base == null || w <= 0 || h <= 0 || colorMapLUT == null) {
            return;
        }

        if (image.getWidth() != w || image.getHeight() != h) {
            return;
        }

        int x, y, z;
        float[] tmp = new float[3];
        tmp[0] = p0[0];
        tmp[1] = p0[1];
        tmp[2] = p0[2];

        int colorMapSize = colorMapLUT.length - 1;
        float cs = (float) colorMapSize / (up - low);
        int c;

        if (useTrilinearInterpolation) {
            for (int j = 0; j < h; j++) {
                for (int i = 0; i < w; i++) {
                    x = (int) tmp[0];
                    y = (int) tmp[1];
                    z = (int) tmp[2];
                    if (x >= 0 && x < dims[0] && y >= 0 && y < dims[1] && z >= 0 && z < dims[2]) {
                        c = (int) (((float) (0xFF & getInterpolatedScalarData(bData, dims, tmp[0], tmp[1], tmp[2])) - low) * cs);
                        if (c < 0) {
                            c = 0;
                        }
                        if (c > colorMapSize) {
                            c = colorMapSize;
                        }
                        image.setRGB(i, j, colorMapLUT[c]);
                    } else {
                        image.setRGB(i, j, fillColor);
                    }
                    tmp[0] += base[0][0];
                    tmp[1] += base[0][1];
                    tmp[2] += base[0][2];
                }
                tmp[0] -= w * base[0][0];
                tmp[1] -= w * base[0][1];
                tmp[2] -= w * base[0][2];
                tmp[0] += base[1][0];
                tmp[1] += base[1][1];
                tmp[2] += base[1][2];
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
                        c = (int) (((float) (0xFF & bData[z * dims[0] * dims[1] + y * dims[0] + x]) - low) * cs);
                        if (c < 0) {
                            c = 0;
                        }
                        if (c > colorMapSize) {
                            c = colorMapSize;
                        }
                        image.setRGB(i, j, colorMapLUT[c]);
                    } else {
                        image.setRGB(i, j, fillColor);
                    }
                    tmp[0] += base[0][0];
                    tmp[1] += base[0][1];
                    tmp[2] += base[0][2];
                }
                tmp[0] -= w * base[0][0];
                tmp[1] -= w * base[0][1];
                tmp[2] -= w * base[0][2];
                tmp[0] += base[1][0];
                tmp[1] += base[1][1];
                tmp[2] += base[1][2];
            }
        }
    }

    public static void interpolateVectorFieldToSliceColormappedImage(byte[] bData, int veclen, int[] dims, float[] p0, float[][] base,
            BufferedImage image, int[] colorMapLUT, float low, float up,
            int fillColor, int w, int h, boolean useTrilinearInterpolation) {
        if (bData == null || dims == null || image == null || p0 == null || base == null || w <= 0 || h <= 0 || colorMapLUT == null) {
            return;
        }

        if (image.getWidth() != w || image.getHeight() != h) {
            return;
        }

        int x, y, z;
        float[] tmp = new float[3];
        tmp[0] = p0[0];
        tmp[1] = p0[1];
        tmp[2] = p0[2];

        int colorMapSize = colorMapLUT.length - 1;
        float cs = (float) colorMapSize / (up - low);
        int c;
        double val,val1;
        byte[] vect;

        if (useTrilinearInterpolation) {
            for (int j = 0; j < h; j++) {
                for (int i = 0; i < w; i++) {
                    x = (int) tmp[0];
                    y = (int) tmp[1];
                    z = (int) tmp[2];
                    if (x >= 0 && x < dims[0] && y >= 0 && y < dims[1] && z >= 0 && z < dims[2]) {
                        vect = getInterpolatedData(bData, dims, tmp[0], tmp[1], tmp[2]);
                        val = 0;
                        for (int k = 0; k < vect.length; k++) {
                            val += (float)(0xFF & vect[k])*(float)(0xFF & vect[k]);                            
                        }
                        val = Math.sqrt(val);
                        c = (int) ((val - low) * cs);
                        if (c < 0) {
                            c = 0;
                        }
                        if (c > colorMapSize) {
                            c = colorMapSize;
                        }
                        image.setRGB(i, j, colorMapLUT[c]);
                    } else {
                        image.setRGB(i, j, fillColor);
                    }
                    tmp[0] += base[0][0];
                    tmp[1] += base[0][1];
                    tmp[2] += base[0][2];
                }
                tmp[0] -= w * base[0][0];
                tmp[1] -= w * base[0][1];
                tmp[2] -= w * base[0][2];
                tmp[0] += base[1][0];
                tmp[1] += base[1][1];
                tmp[2] += base[1][2];
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
                        val = 0;
                        for (int k = 0; k < veclen; k++) {
                            val1 = (float) (0xFF & bData[(z * dims[0] * dims[1] + y * dims[0] + x)*veclen + k]);
                            val += val1*val1;
                        }
                        val = Math.sqrt(val);                        
                        c = (int) ((val - low) * cs);
                        if (c < 0) {
                            c = 0;
                        }
                        if (c > colorMapSize) {
                            c = colorMapSize;
                        }
                        image.setRGB(i, j, colorMapLUT[c]);
                    } else {
                        image.setRGB(i, j, fillColor);
                    }
                    tmp[0] += base[0][0];
                    tmp[1] += base[0][1];
                    tmp[2] += base[0][2];
                }
                tmp[0] -= w * base[0][0];
                tmp[1] -= w * base[0][1];
                tmp[2] -= w * base[0][2];
                tmp[0] += base[1][0];
                tmp[1] += base[1][1];
                tmp[2] += base[1][2];
            }
        }
    }
    
    public static void interpolateScalarFieldToSliceColormappedImage(byte[] bData, int[] dims, float[] p0, float[][] base,
            BufferedImage image, int[] colorMapLUT, float low, float up,
            int fillColor, int w, int h, boolean useTrilinearInterpolation,
            int transparentData, int dataTransparency) {
        if (bData == null || dims == null || image == null || p0 == null || base == null || w <= 0 || h <= 0 || colorMapLUT == null) {
            return;
        }

        if (image.getWidth() != w || image.getHeight() != h) {
            return;
        }

        int x, y, z;
        float[] tmp = new float[3];
        tmp[0] = p0[0];
        tmp[1] = p0[1];
        tmp[2] = p0[2];

        int colorMapSize = colorMapLUT.length - 1;
        float cs = (float) colorMapSize / (up - low);
        int c;
        int d;

        if (useTrilinearInterpolation) {
            for (int j = 0; j < h; j++) {
                for (int i = 0; i < w; i++) {
                    x = (int) tmp[0];
                    y = (int) tmp[1];
                    z = (int) tmp[2];
                    if (x >= 0 && x < dims[0] && y >= 0 && y < dims[1] && z >= 0 && z < dims[2]) {
                        d = (int) (0xFF & getInterpolatedScalarData(bData, dims, tmp[0], tmp[1], tmp[2]));
                        c = (int) (((float) d - low) * cs);
                        if (c < 0) {
                            c = 0;
                        }
                        if (c > colorMapSize) {
                            c = colorMapSize;
                        }
                        image.setRGB(i, j, colorMapLUT[c]);

                        if (d == transparentData) {
                            image.getRaster().setSample(i, j, 3, 0);
                        } else {
                            image.getRaster().setSample(i, j, 3, dataTransparency);
                        }

                    } else {
                        image.setRGB(i, j, fillColor);
                    }
                    tmp[0] += base[0][0];
                    tmp[1] += base[0][1];
                    tmp[2] += base[0][2];
                }
                tmp[0] -= w * base[0][0];
                tmp[1] -= w * base[0][1];
                tmp[2] -= w * base[0][2];
                tmp[0] += base[1][0];
                tmp[1] += base[1][1];
                tmp[2] += base[1][2];
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
                        d = (int) (0xFF & bData[z * dims[0] * dims[1] + y * dims[0] + x]);
                        c = (int) (((float) d - low) * cs);
                        if (c < 0) {
                            c = 0;
                        }
                        if (c > colorMapSize) {
                            c = colorMapSize;
                        }
                        image.setRGB(i, j, colorMapLUT[c]);

                        if (d == transparentData) {
                            image.getRaster().setSample(i, j, 3, 0);
                        } else {
                            image.getRaster().setSample(i, j, 3, dataTransparency);
                        }

                    } else {
                        image.setRGB(i, j, fillColor);
                    }
                    tmp[0] += base[0][0];
                    tmp[1] += base[0][1];
                    tmp[2] += base[0][2];
                }
                tmp[0] -= w * base[0][0];
                tmp[1] -= w * base[0][1];
                tmp[2] -= w * base[0][2];
                tmp[0] += base[1][0];
                tmp[1] += base[1][1];
                tmp[2] += base[1][2];
            }
        }
    }

    public static short[] getInterpolatedData(short[] data, int[] dims, float u, float v, float w) {
        if (data == null || dims == null) {
            return null;
        }
        short[] c;
        int vlen;
        int inexact = 0;
        int i, j, k, m, n0, n1;
        switch (dims.length) {
            case 3:
                vlen = data.length / (dims[0] * dims[1] * dims[2]);
                if (data.length != vlen * dims[0] * dims[1] * dims[2]) {
                    return null;
                }
                c = new short[vlen];
                if (vlen == 1) {
                    c[0] = getInterpolatedScalarData(data, dims, u, v, w);
                    return c;
                }
                if (u < 0) {
                    u = 0;
                }
                if (u > dims[0] - 1) {
                    u = dims[0] - 1;
                }
                if (v < 0) {
                    v = 0;
                }
                if (v > dims[1] - 1) {
                    v = dims[1] - 1;
                }
                if (w < 0) {
                    w = 0;
                }
                if (w > dims[2] - 1) {
                    w = dims[2] - 1;
                }
                inexact = 0;
                i = (int) u;
                u -= i;
                if (u != 0) {
                    inexact += 1;
                }
                j = (int) v;
                v -= j;
                if (v != 0) {
                    inexact += 2;
                }
                k = (int) w;
                w -= k;
                if (w != 0) {
                    inexact += 4;
                }
                m = vlen * ((dims[1] * k + j) * dims[0] + i);
                n0 = vlen * dims[0];
                n1 = vlen * dims[0] * dims[1];
                switch (inexact) {
                    case 0:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = data[m + l];
                        }
                        break;
                    case 1:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = (short) (u * data[m + l + vlen] + (1 - u) * data[m + l]);
                        }
                        break;
                    case 2:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = (short) (v * data[m + l + n0] + (1 - v) * data[m + l]);
                        }
                        break;
                    case 3:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = (short) (v * (u * data[m + l + n0 + vlen] + (1 - u) * data[m + l + n0])
                                    + (1 - v) * (u * data[m + l + vlen] + (1 - u) * data[m + l]));
                        }
                        break;
                    case 4:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = (short) (w * data[m + l + n1] + (1 - w) * data[m + l]);
                        }
                        break;
                    case 5:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = (short) (w * (u * data[m + l + n1 + vlen] + (1 - u) * data[m + l + n1])
                                    + (1 - w) * (u * data[m + l + vlen] + (1 - u) * data[m + l]));
                        }
                        break;
                    case 6:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = (short) (w * (v * data[m + l + n1 + n0] + (1 - v) * data[m + l + n1])
                                    + (1 - w) * (v * data[m + l + n0] + (1 - v) * data[m + l]));
                        }
                        break;
                    case 7:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = (short) (w * (v * (u * data[m + l + n1 + n0 + vlen] + (1 - u) * data[m + l + n1 + n0])
                                    + (1 - v) * (u * data[m + l + n1 + vlen] + (1 - u) * data[m + l + n1]))
                                    + (1 - w) * (v * (u * data[m + l + n0 + vlen] + (1 - u) * data[m + l + n0])
                                    + (1 - v) * (u * data[m + l + vlen] + (1 - u) * data[m + l])));
                        }
                        break;
                }
                return c;
            case 2:
                vlen = data.length / (dims[0] * dims[1]);
                if (data.length != vlen * dims[0] * dims[1]) {
                    return null;
                }
                c = new short[vlen];
                if (vlen == 1) {
                    c[0] = getInterpolatedScalarData(data, dims, u, v, w);
                    return c;
                }
                if (u < 0) {
                    u = 0;
                }
                if (u > dims[0] - 1) {
                    u = dims[0] - 1;
                }
                if (v < 0) {
                    v = 0;
                }
                if (v > dims[1] - 1) {
                    v = dims[1] - 1;
                }
                inexact = 0;
                i = (int) u;
                u -= i;
                if (u != 0) {
                    inexact += 1;
                }
                j = (int) v;
                v -= j;
                if (v != 0) {
                    inexact += 2;
                }

                m = vlen * (j * dims[0] + i);
                n0 = vlen * dims[0];
                switch (inexact) {
                    case 0:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = data[m + l];
                        }
                        break;
                    case 1:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = (short) (u * data[m + l + vlen] + (1 - u) * data[m + l]);
                        }
                        break;
                    case 2:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = (short) (v * data[m + l + n0] + (1 - v) * data[m + l]);
                        }
                        break;
                    case 3:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = (short) (v * (u * data[m + l + n0 + vlen] + (1 - u) * data[m + l + n0])
                                    + (1 - v) * (u * data[m + l + vlen] + (1 - u) * data[m + l]));
                        }
                        break;
                }
                return c;
            case 1:
                vlen = data.length / dims[0];
                if (data.length != vlen * dims[0]) {
                    return null;
                }
                c = new short[vlen];
                if (vlen == 1) {
                    c[0] = getInterpolatedScalarData(data, dims, u, v, w);
                    return c;
                }
                if (u < 0) {
                    u = 0;
                }
                if (u > dims[0] - 1) {
                    u = dims[0] - 1;
                }
                inexact = 0;
                i = (int) u;
                u -= i;
                if (u != 0) {
                    inexact += 1;
                }
                m = vlen * i;
                switch (inexact) {
                    case 0:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = data[m + l];
                        }
                        break;
                    case 1:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = (short) (u * data[m + l + vlen] + (1 - u) * data[m + l]);
                        }
                        break;
                }
                return c;
        }
        return null;
    }

    public static short getInterpolatedScalarData(short[] data, int[] dims, float u, float v, float w) {
        if (data == null || dims == null) {
            return 0;
        }

        switch (dims.length) {
            case 3:
                return getInterpolatedScalarData3D(data, dims, u, v, w);
            case 2:
                return getInterpolatedScalarData2D(data, dims, u, v, w);
            case 1:
                return getInterpolatedScalarData1D(data, dims, u, v, w);
        }
        return 0;
    }

    public static short getInterpolatedScalarData3D(short[] data, int[] dims, float u, float v, float w) {
        if (data == null || dims == null || dims.length != 3) {
            return 0;
        }
        short c = 0;
        int inexact = 0;
        int i, j, k, m, n0, n1;
        if (data.length != dims[0] * dims[1] * dims[2]) {
            return 0;
        }
        if (u < 0) {
            u = 0;
        }
        if (u > dims[0] - 1) {
            u = dims[0] - 1;
        }
        if (v < 0) {
            v = 0;
        }
        if (v > dims[1] - 1) {
            v = dims[1] - 1;
        }
        if (w < 0) {
            w = 0;
        }
        if (w > dims[2] - 1) {
            w = dims[2] - 1;
        }
        i = (int) u;
        u -= i;
        if (u != 0) {
            inexact += 1;
        }
        j = (int) v;
        v -= j;
        if (v != 0) {
            inexact += 2;
        }
        k = (int) w;
        w -= k;
        if (w != 0) {
            inexact += 4;
        }

        m = (dims[1] * k + j) * dims[0] + i;
        n0 = dims[0];
        n1 = dims[0] * dims[1];
        switch (inexact) {
            case 0:
                c = data[m];
                break;
            case 1:
                c = (short) (u * data[m + 1] + (1 - u) * data[m]);
                break;
            case 2:
                c = (short) (v * data[m + n0] + (1 - v) * data[m]);
                break;
            case 3:
                c = (short) (v * (u * data[m + n0 + 1] + (1 - u) * data[m + n0])
                        + (1 - v) * (u * data[m + 1] + (1 - u) * data[m]));
                break;
            case 4:
                c = (short) (w * data[m + n1] + (1 - w) * data[m]);
                break;
            case 5:
                c = (short) (w * (u * data[m + n1 + 1] + (1 - u) * data[m + n1])
                        + (1 - w) * (u * data[m + 1] + (1 - u) * data[m]));
                break;
            case 6:
                c = (short) (w * (v * data[m + n1 + n0] + (1 - v) * data[m + n1])
                        + (1 - w) * (v * data[m + n0] + (1 - v) * data[m]));
                break;
            case 7:
                c = (short) (w * (v * (u * data[m + n1 + n0 + 1] + (1 - u) * data[m + n1 + n0])
                        + (1 - v) * (u * data[m + n1 + 1] + (1 - u) * data[m + n1]))
                        + (1 - w) * (v * (u * data[m + n0 + 1] + (1 - u) * data[m + n0])
                        + (1 - v) * (u * data[m + 1] + (1 - u) * data[m])));
                break;
        }
        return c;
    }

    public static short getInterpolatedScalarData2D(short[] data, int[] dims, float u, float v, float w) {
        if (data == null || dims == null || dims.length != 2) {
            return 0;
        }
        short c = 0;
        int inexact = 0;
        int i, j, k, m, n0, n1;
        if (data.length != dims[0] * dims[1]) {
            return 0;
        }
        if (u < 0) {
            u = 0;
        }
        if (u > dims[0] - 1) {
            u = dims[0] - 1;
        }
        if (v < 0) {
            v = 0;
        }
        if (v > dims[1] - 1) {
            v = dims[1] - 1;
        }
        i = (int) u;
        u -= i;
        if (u != 0) {
            inexact += 1;
        }
        j = (int) v;
        v -= j;
        if (v != 0) {
            inexact += 2;
        }

        m = j * dims[0] + i;
        n0 = dims[0];
        switch (inexact) {
            case 0:
                c = data[m];
                break;
            case 1:
                c = (short) (u * data[m + 1] + (1 - u) * data[m]);
                break;
            case 2:
                c = (short) (v * data[m + n0] + (1 - v) * data[m]);
                break;
            case 3:
                c = (short) (v * (u * data[m + n0 + 1] + (1 - u) * data[m + n0])
                        + (1 - v) * (u * data[m + 1] + (1 - u) * data[m]));
                break;
        }
        return c;
    }

    public static short getInterpolatedScalarData1D(short[] data, int[] dims, float u, float v, float w) {
        if (data == null || dims == null || dims.length != 1) {
            return 0;
        }
        short c = 0;
        int inexact = 0;
        int i;
        if (data.length != dims[0]) {
            return 0;
        }
        if (u < 0) {
            u = 0;
        }
        if (u > dims[0] - 1) {
            u = dims[0] - 1;
        }
        i = (int) u;
        u -= i;
        if (u != 0) {
            inexact += 1;
        }
        switch (inexact) {
            case 0:
                c = data[i];
                break;
            case 1:
                c = (short) (u * data[i + 1] + (1 - u) * data[i]);
                break;
        }
        return c;
    }

    public static void interpolateScalarFieldToSlice(short[] sData, int[] dims, float[] p0, float[][] base,
            short[] outData, int w, int h, boolean useTrilinearInterpolation) {
        interpolateScalarFieldToSlice(sData, dims, p0, base,
                (short) 0, (short) 1, 1.0f, outData,
                w, h, useTrilinearInterpolation);
    }

    public static void interpolateScalarFieldToSlice(short[] sData, int[] dims, float[] p0, float[][] base,
            short sMin, short sMax, float ss, short[] outData,
            int w, int h, boolean useTrilinearInterpolation) {
        if (sData == null || dims == null || outData == null || p0 == null || base == null || w <= 0 || h <= 0) {
            return;
        }

        if (outData.length != w * h) {
            return;
        }

        int x, y, z;
        short v;
        float[] tmp = new float[3];
        tmp[0] = p0[0];
        tmp[1] = p0[1];
        tmp[2] = p0[2];

        if (useTrilinearInterpolation) {
            for (int j = 0, c = 0; j < h; j++) {
                for (int i = 0; i < w; i++, c++) {
                    x = (int) tmp[0];
                    y = (int) tmp[1];
                    z = (int) tmp[2];
                    if (x >= 0 && x < dims[0] && y >= 0 && y < dims[1] && z >= 0 && z < dims[2]) {
                        v = getInterpolatedScalarData(sData, dims, tmp[0], tmp[1], tmp[2]);
                        outData[c] = (short) ((v - sMin) * ss);
                    } else {
                        outData[c] = 0;
                    }
                    tmp[0] += base[0][0];
                    tmp[1] += base[0][1];
                    tmp[2] += base[0][2];
                }
                tmp[0] -= w * base[0][0];
                tmp[1] -= w * base[0][1];
                tmp[2] -= w * base[0][2];
                tmp[0] += base[1][0];
                tmp[1] += base[1][1];
                tmp[2] += base[1][2];
            }
        } else {
            tmp[0] += 0.5f;
            tmp[1] += 0.5f;
            tmp[2] += 0.5f;
            for (int j = 0, c = 0; j < h; j++) {
                for (int i = 0; i < w; i++, c++) {
                    x = (int) tmp[0];
                    y = (int) tmp[1];
                    z = (int) tmp[2];
                    if (x >= 0 && x < dims[0] && y >= 0 && y < dims[1] && z >= 0 && z < dims[2]) {
                        outData[c] = (short) ((sData[z * dims[0] * dims[1] + y * dims[0] + x] - sMin) * ss);
                    } else {
                        outData[c] = 0;
                    }
                    tmp[0] += base[0][0];
                    tmp[1] += base[0][1];
                    tmp[2] += base[0][2];
                }
                tmp[0] -= w * base[0][0];
                tmp[1] -= w * base[0][1];
                tmp[2] -= w * base[0][2];
                tmp[0] += base[1][0];
                tmp[1] += base[1][1];
                tmp[2] += base[1][2];
            }
        }
    }

    public static void interpolateScalarFieldToSliceRaster(short[] sData, int[] dims, float[] p0, float[][] base,
            short sMin, short sMax, float ss, WritableRaster raster,
            int w, int h, boolean useTrilinearInterpolation) {
        if (sData == null || dims == null || raster == null || p0 == null || base == null || w <= 0 || h <= 0) {
            return;
        }

        if (raster.getWidth() != w || raster.getHeight() != h) {
            return;
        }

        int x, y, z;
        short v;
        float[] tmp = new float[3];
        tmp[0] = p0[0];
        tmp[1] = p0[1];
        tmp[2] = p0[2];

        if (useTrilinearInterpolation) {
            for (int j = 0; j < h; j++) {
                for (int i = 0; i < w; i++) {
                    x = (int) tmp[0];
                    y = (int) tmp[1];
                    z = (int) tmp[2];
                    if (x >= 0 && x < dims[0] && y >= 0 && y < dims[1] && z >= 0 && z < dims[2]) {
                        v = getInterpolatedScalarData(sData, dims, tmp[0], tmp[1], tmp[2]);
                        raster.setSample(i, j, 0, (short) ((v - sMin) * ss));
                    } else {
                        raster.setSample(i, j, 0, 0);
                    }
                    tmp[0] += base[0][0];
                    tmp[1] += base[0][1];
                    tmp[2] += base[0][2];
                }
                tmp[0] -= w * base[0][0];
                tmp[1] -= w * base[0][1];
                tmp[2] -= w * base[0][2];
                tmp[0] += base[1][0];
                tmp[1] += base[1][1];
                tmp[2] += base[1][2];
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
                        raster.setSample(i, j, 0, (short) ((sData[z * dims[0] * dims[1] + y * dims[0] + x] - sMin) * ss));
                    } else {
                        raster.setSample(i, j, 0, 0);
                    }
                    tmp[0] += base[0][0];
                    tmp[1] += base[0][1];
                    tmp[2] += base[0][2];
                }
                tmp[0] -= w * base[0][0];
                tmp[1] -= w * base[0][1];
                tmp[2] -= w * base[0][2];
                tmp[0] += base[1][0];
                tmp[1] += base[1][1];
                tmp[2] += base[1][2];
            }
        }
    }

    public static void interpolateFieldToSliceColormappedImage(short[] sData, int veclen, int[] dims, float[] p0, float[][] base,
            BufferedImage image, int[] colorMapLUT, float low, float up,
            int fillColor, int w, int h, boolean useTrilinearInterpolation) {
        if (veclen == 1) {
            interpolateScalarFieldToSliceColormappedImage(sData, dims, p0, base, image, colorMapLUT, low, up, fillColor, w, h, useTrilinearInterpolation);
        } else {
            interpolateVectorFieldToSliceColormappedImage(sData, veclen, dims, p0, base, image, colorMapLUT, low, up, fillColor, w, h, useTrilinearInterpolation);
        }
    }
    
    private static void interpolateScalarFieldToSliceColormappedImage(short[] sData, int[] dims, float[] p0, float[][] base,
            BufferedImage image, int[] colorMapLUT, float low, float up,
            int fillColor, int w, int h, boolean useTrilinearInterpolation) {
        if (sData == null || dims == null || image == null || p0 == null || base == null || w <= 0 || h <= 0 || colorMapLUT == null) {
            return;
        }

        if (image.getWidth() != w || image.getHeight() != h) {
            return;
        }

        int x, y, z;
        float[] tmp = new float[3];
        tmp[0] = p0[0];
        tmp[1] = p0[1];
        tmp[2] = p0[2];

        int colorMapSize = colorMapLUT.length - 1;
        float cs = (float) colorMapSize / (up - low);
        int c;

        if (useTrilinearInterpolation) {
            for (int j = 0; j < h; j++) {
                for (int i = 0; i < w; i++) {
                    x = (int) tmp[0];
                    y = (int) tmp[1];
                    z = (int) tmp[2];
                    if (x >= 0 && x < dims[0] && y >= 0 && y < dims[1] && z >= 0 && z < dims[2]) {
                        c = (int) (((float) getInterpolatedScalarData(sData, dims, tmp[0], tmp[1], tmp[2]) - low) * cs);
                        if (c < 0) {
                            c = 0;
                        }
                        if (c > colorMapSize) {
                            c = colorMapSize;
                        }
                        image.setRGB(i, j, colorMapLUT[c]);
                    } else {
                        image.setRGB(i, j, fillColor);
                    }
                    tmp[0] += base[0][0];
                    tmp[1] += base[0][1];
                    tmp[2] += base[0][2];
                }
                tmp[0] -= w * base[0][0];
                tmp[1] -= w * base[0][1];
                tmp[2] -= w * base[0][2];
                tmp[0] += base[1][0];
                tmp[1] += base[1][1];
                tmp[2] += base[1][2];
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
                        c = (int) (((float) sData[z * dims[0] * dims[1] + y * dims[0] + x] - low) * cs);
                        if (c < 0) {
                            c = 0;
                        }
                        if (c > colorMapSize) {
                            c = colorMapSize;
                        }
                        image.setRGB(i, j, colorMapLUT[c]);
                    } else {
                        image.setRGB(i, j, fillColor);
                    }
                    tmp[0] += base[0][0];
                    tmp[1] += base[0][1];
                    tmp[2] += base[0][2];
                }
                tmp[0] -= w * base[0][0];
                tmp[1] -= w * base[0][1];
                tmp[2] -= w * base[0][2];
                tmp[0] += base[1][0];
                tmp[1] += base[1][1];
                tmp[2] += base[1][2];
            }
        }
    }

    public static void interpolateVectorFieldToSliceColormappedImage(short[] sData, int veclen, int[] dims, float[] p0, float[][] base,
            BufferedImage image, int[] colorMapLUT, float low, float up,
            int fillColor, int w, int h, boolean useTrilinearInterpolation) {
        if (sData == null || dims == null || image == null || p0 == null || base == null || w <= 0 || h <= 0 || colorMapLUT == null) {
            return;
        }

        if (image.getWidth() != w || image.getHeight() != h) {
            return;
        }

        int x, y, z;
        float[] tmp = new float[3];
        tmp[0] = p0[0];
        tmp[1] = p0[1];
        tmp[2] = p0[2];

        int colorMapSize = colorMapLUT.length - 1;
        float cs = (float) colorMapSize / (up - low);
        int c;
        double val,val1;
        short[] vect;

        if (useTrilinearInterpolation) {
            for (int j = 0; j < h; j++) {
                for (int i = 0; i < w; i++) {
                    x = (int) tmp[0];
                    y = (int) tmp[1];
                    z = (int) tmp[2];
                    if (x >= 0 && x < dims[0] && y >= 0 && y < dims[1] && z >= 0 && z < dims[2]) {
                        vect = getInterpolatedData(sData, dims, tmp[0], tmp[1], tmp[2]);
                        val = 0;
                        for (int k = 0; k < vect.length; k++) {
                            val += (double)vect[k]*(double)vect[k];                            
                        }
                        val = Math.sqrt(val);
                        c = (int) ((val - low) * cs);
                        if (c < 0) {
                            c = 0;
                        }
                        if (c > colorMapSize) {
                            c = colorMapSize;
                        }
                        image.setRGB(i, j, colorMapLUT[c]);
                    } else {
                        image.setRGB(i, j, fillColor);
                    }
                    tmp[0] += base[0][0];
                    tmp[1] += base[0][1];
                    tmp[2] += base[0][2];
                }
                tmp[0] -= w * base[0][0];
                tmp[1] -= w * base[0][1];
                tmp[2] -= w * base[0][2];
                tmp[0] += base[1][0];
                tmp[1] += base[1][1];
                tmp[2] += base[1][2];
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
                        val = 0;
                        for (int k = 0; k < veclen; k++) {
                            val1 = (float) sData[(z * dims[0] * dims[1] + y * dims[0] + x)*veclen + k];
                            val += val1*val1;
                        }
                        val = Math.sqrt(val);                        
                        c = (int) ((val - low) * cs);
                        if (c < 0) {
                            c = 0;
                        }
                        if (c > colorMapSize) {
                            c = colorMapSize;
                        }
                        image.setRGB(i, j, colorMapLUT[c]);
                    } else {
                        image.setRGB(i, j, fillColor);
                    }
                    tmp[0] += base[0][0];
                    tmp[1] += base[0][1];
                    tmp[2] += base[0][2];
                }
                tmp[0] -= w * base[0][0];
                tmp[1] -= w * base[0][1];
                tmp[2] -= w * base[0][2];
                tmp[0] += base[1][0];
                tmp[1] += base[1][1];
                tmp[2] += base[1][2];
            }
        }
    }
    
    public static int[] getInterpolatedData(int[] data, int[] dims, float u, float v, float w) {
        if (data == null || dims == null) {
            return null;
        }
        int[] c;
        int vlen;
        int inexact = 0;
        int i, j, k, m, n0, n1;
        switch (dims.length) {
            case 3:
                vlen = data.length / (dims[0] * dims[1] * dims[2]);
                if (data.length != vlen * dims[0] * dims[1] * dims[2]) {
                    return null;
                }
                c = new int[vlen];
                if (vlen == 1) {
                    c[0] = getInterpolatedScalarData(data, dims, u, v, w);
                    return c;
                }
                if (u < 0) {
                    u = 0;
                }
                if (u > dims[0] - 1) {
                    u = dims[0] - 1;
                }
                if (v < 0) {
                    v = 0;
                }
                if (v > dims[1] - 1) {
                    v = dims[1] - 1;
                }
                if (w < 0) {
                    w = 0;
                }
                if (w > dims[2] - 1) {
                    w = dims[2] - 1;
                }
                i = (int) u;
                u -= i;
                if (u != 0) {
                    inexact += 1;
                }
                j = (int) v;
                v -= j;
                if (v != 0) {
                    inexact += 2;
                }
                k = (int) w;
                w -= k;
                if (w != 0) {
                    inexact += 4;
                }
                m = vlen * ((dims[1] * k + j) * dims[0] + i);
                n0 = vlen * dims[0];
                n1 = vlen * dims[0] * dims[1];
                switch (inexact) {
                    case 0:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = data[m + l];
                        }
                        break;
                    case 1:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = (int) (u * data[m + l + vlen] + (1 - u) * data[m + l]);
                        }
                        break;
                    case 2:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = (int) (v * data[m + l + n0] + (1 - v) * data[m + l]);
                        }
                        break;
                    case 3:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = (int) (v * (u * data[m + l + n0 + vlen] + (1 - u) * data[m + l + n0])
                                    + (1 - v) * (u * data[m + l + vlen] + (1 - u) * data[m + l]));
                        }
                        break;
                    case 4:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = (int) (w * data[m + l + n1] + (1 - w) * data[m + l]);
                        }
                        break;
                    case 5:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = (int) (w * (u * data[m + l + n1 + vlen] + (1 - u) * data[m + l + n1])
                                    + (1 - w) * (u * data[m + l + vlen] + (1 - u) * data[m + l]));
                        }
                        break;
                    case 6:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = (int) (w * (v * data[m + l + n1 + n0] + (1 - v) * data[m + l + n1])
                                    + (1 - w) * (v * data[m + l + n0] + (1 - v) * data[m + l]));
                        }
                        break;
                    case 7:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = (int) (w * (v * (u * data[m + l + n1 + n0 + vlen] + (1 - u) * data[m + l + n1 + n0])
                                    + (1 - v) * (u * data[m + l + n1 + vlen] + (1 - u) * data[m + l + n1]))
                                    + (1 - w) * (v * (u * data[m + l + n0 + vlen] + (1 - u) * data[m + l + n0])
                                    + (1 - v) * (u * data[m + l + vlen] + (1 - u) * data[m + l])));
                        }
                        break;
                }
                return c;
            case 2:
                vlen = data.length / (dims[0] * dims[1]);
                if (data.length != vlen * dims[0] * dims[1]) {
                    return null;
                }
                c = new int[vlen];
                if (vlen == 1) {
                    c[0] = getInterpolatedScalarData(data, dims, u, v, w);
                    return c;
                }
                if (u < 0) {
                    u = 0;
                }
                if (u > dims[0] - 1) {
                    u = dims[0] - 1;
                }
                if (v < 0) {
                    v = 0;
                }
                if (v > dims[1] - 1) {
                    v = dims[1] - 1;
                }
                i = (int) u;
                u -= i;
                if (u != 0) {
                    inexact += 1;
                }
                j = (int) v;
                v -= j;
                if (v != 0) {
                    inexact += 2;
                }
                m = vlen * (j * dims[0] + i);
                n0 = vlen * dims[0];
                switch (inexact) {
                    case 0:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = data[m + l];
                        }
                        break;
                    case 1:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = (int) (u * data[m + l + vlen] + (1 - u) * data[m + l]);
                        }
                        break;
                    case 2:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = (int) (v * data[m + l + n0] + (1 - v) * data[m + l]);
                        }
                        break;
                    case 3:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = (int) (v * (u * data[m + l + n0 + vlen] + (1 - u) * data[m + l + n0])
                                    + (1 - v) * (u * data[m + l + vlen] + (1 - u) * data[m + l]));
                        }
                        break;
                }
                return c;
            case 1:
                vlen = data.length / dims[0];
                if (data.length != vlen * dims[0]) {
                    return null;
                }
                c = new int[vlen];
                if (vlen == 1) {
                    c[0] = getInterpolatedScalarData(data, dims, u, v, w);
                    return c;
                }
                if (u < 0) {
                    u = 0;
                }
                if (u > dims[0] - 1) {
                    u = dims[0] - 1;
                }
                i = (int) u;
                u -= i;
                if (u != 0) {
                    inexact += 1;
                }
                m = vlen * i;
                switch (inexact) {
                    case 0:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = data[m + l];
                        }
                        break;
                    case 1:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = (int) (u * data[m + l + vlen] + (1 - u) * data[m + l]);
                        }
                        break;
                }
                return c;
        }
        return null;
    }

    public static int getInterpolatedScalarData(int[] data, int[] dims, float u, float v, float w) {
        if (data == null || dims == null) {
            return 0;
        }

        switch (dims.length) {
            case 3:
                return getInterpolatedScalarData3D(data, dims, u, v, w);
            case 2:
                return getInterpolatedScalarData2D(data, dims, u, v, w);
            case 1:
                return getInterpolatedScalarData1D(data, dims, u, v, w);
        }
        return 0;
    }

    public static int getInterpolatedScalarData3D(int[] data, int[] dims, float u, float v, float w) {
        if (data == null || dims == null || dims.length != 3) {
            return 0;
        }
        int c = 0;
        int inexact = 0;
        int i, j, k, m, n0, n1;
        if (data.length != dims[0] * dims[1] * dims[2]) {
            return 0;
        }
        if (u < 0) {
            u = 0;
        }
        if (u > dims[0] - 1) {
            u = dims[0] - 1;
        }
        if (v < 0) {
            v = 0;
        }
        if (v > dims[1] - 1) {
            v = dims[1] - 1;
        }
        if (w < 0) {
            w = 0;
        }
        if (w > dims[2] - 1) {
            w = dims[2] - 1;
        }
        i = (int) u;
        u -= i;
        if (u != 0) {
            inexact += 1;
        }
        j = (int) v;
        v -= j;
        if (v != 0) {
            inexact += 2;
        }
        k = (int) w;
        w -= k;
        if (w != 0) {
            inexact += 4;
        }
        m = (dims[1] * k + j) * dims[0] + i;
        n0 = dims[0];
        n1 = dims[0] * dims[1];
        switch (inexact) {
            case 0:
                c = data[m];
                break;
            case 1:
                c = (int) (u * data[m + 1] + (1 - u) * data[m]);
                break;
            case 2:
                c = (int) (v * data[m + n0] + (1 - v) * data[m]);
                break;
            case 3:
                c = (int) (v * (u * data[m + n0 + 1] + (1 - u) * data[m + n0])
                        + (1 - v) * (u * data[m + 1] + (1 - u) * data[m]));
                break;
            case 4:
                c = (int) (w * data[m + n1] + (1 - w) * data[m]);
                break;
            case 5:
                c = (int) (w * (u * data[m + n1 + 1] + (1 - u) * data[m + n1])
                        + (1 - w) * (u * data[m + 1] + (1 - u) * data[m]));
                break;
            case 6:
                c = (int) (w * (v * data[m + n1 + n0] + (1 - v) * data[m + n1])
                        + (1 - w) * (v * data[m + n0] + (1 - v) * data[m]));
                break;
            case 7:
                c = (int) (w * (v * (u * data[m + n1 + n0 + 1] + (1 - u) * data[m + n1 + n0])
                        + (1 - v) * (u * data[m + n1 + 1] + (1 - u) * data[m + n1]))
                        + (1 - w) * (v * (u * data[m + n0 + 1] + (1 - u) * data[m + n0])
                        + (1 - v) * (u * data[m + 1] + (1 - u) * data[m])));
                break;
        }
        return c;
    }

    public static int getInterpolatedScalarData2D(int[] data, int[] dims, float u, float v, float w) {
        if (data == null || dims == null || dims.length != 2) {
            return 0;
        }
        int c = 0;
        int inexact = 0;
        int i, j, k, m, n0, n1;
        if (data.length != dims[0] * dims[1]) {
            return 0;
        }
        if (u < 0) {
            u = 0;
        }
        if (u > dims[0] - 1) {
            u = dims[0] - 1;
        }
        if (v < 0) {
            v = 0;
        }
        if (v > dims[1] - 1) {
            v = dims[1] - 1;
        }
        i = (int) u;
        u -= i;
        if (u != 0) {
            inexact += 1;
        }
        j = (int) v;
        v -= j;
        if (v != 0) {
            inexact += 2;
        }
        m = j * dims[0] + i;
        n0 = dims[0];
        switch (inexact) {
            case 0:
                c = data[m];
                break;
            case 1:
                c = (int) (u * data[m + 1] + (1 - u) * data[m]);
                break;
            case 2:
                c = (int) (v * data[m + n0] + (1 - v) * data[m]);
                break;
            case 3:
                c = (int) (v * (u * data[m + n0 + 1] + (1 - u) * data[m + n0])
                        + (1 - v) * (u * data[m + 1] + (1 - u) * data[m]));
                break;
        }
        return c;
    }

    public static int getInterpolatedScalarData1D(int[] data, int[] dims, float u, float v, float w) {
        if (data == null || dims == null || dims.length != 1) {
            return 0;
        }
        int c = 0;
        int inexact = 0;
        int i;
        if (data.length != dims[0]) {
            return 0;
        }
        if (u < 0) {
            u = 0;
        }
        if (u > dims[0] - 1) {
            u = dims[0] - 1;
        }
        i = (int) u;
        u -= i;
        if (u != 0) {
            inexact += 1;
        }
        switch (inexact) {
            case 0:
                c = data[i];
                break;
            case 1:
                c = (int) (u * data[i + 1] + (1 - u) * data[i]);
                break;
        }
        return c;
    }

    public static void interpolateScalarFieldToSlice(int[] iData, int[] dims, float[] p0, float[][] base,
            int[] outData, int w, int h, boolean useTrilinearInterpolation) {
        interpolateScalarFieldToSlice(iData, dims, p0, base,
                0, 1, 1.0f, outData,
                w, h, useTrilinearInterpolation);
    }

    public static void interpolateScalarFieldToSlice(int[] iData, int[] dims, float[] p0, float[][] base,
            int iMin, int iMax, float is, int[] outData,
            int w, int h, boolean useTrilinearInterpolation) {
        if (iData == null || dims == null || outData == null || p0 == null || base == null || w <= 0 || h <= 0) {
            return;
        }

        if (outData.length != w * h) {
            return;
        }

        int x, y, z;
        int v;
        float[] tmp = new float[3];
        tmp[0] = p0[0];
        tmp[1] = p0[1];
        tmp[2] = p0[2];

        if (useTrilinearInterpolation) {
            for (int j = 0, c = 0; j < h; j++) {
                for (int i = 0; i < w; i++, c++) {
                    x = (int) tmp[0];
                    y = (int) tmp[1];
                    z = (int) tmp[2];
                    if (x >= 0 && x < dims[0] && y >= 0 && y < dims[1] && z >= 0 && z < dims[2]) {
                        v = getInterpolatedScalarData(iData, dims, tmp[0], tmp[1], tmp[2]);
                        outData[c] = (int) ((v - iMin) * is);
                    } else {
                        outData[c] = 0;
                    }
                    tmp[0] += base[0][0];
                    tmp[1] += base[0][1];
                    tmp[2] += base[0][2];
                }
                tmp[0] -= w * base[0][0];
                tmp[1] -= w * base[0][1];
                tmp[2] -= w * base[0][2];
                tmp[0] += base[1][0];
                tmp[1] += base[1][1];
                tmp[2] += base[1][2];
            }
        } else {
            tmp[0] += 0.5f;
            tmp[1] += 0.5f;
            tmp[2] += 0.5f;
            for (int j = 0, c = 0; j < h; j++) {
                for (int i = 0; i < w; i++, c++) {
                    x = (int) tmp[0];
                    y = (int) tmp[1];
                    z = (int) tmp[2];
                    if (x >= 0 && x < dims[0] && y >= 0 && y < dims[1] && z >= 0 && z < dims[2]) {
                        outData[c] = (int) ((iData[z * dims[0] * dims[1] + y * dims[0] + x] - iMin) * is);
                    } else {
                        outData[c] = 0;
                    }
                    tmp[0] += base[0][0];
                    tmp[1] += base[0][1];
                    tmp[2] += base[0][2];
                }
                tmp[0] -= w * base[0][0];
                tmp[1] -= w * base[0][1];
                tmp[2] -= w * base[0][2];
                tmp[0] += base[1][0];
                tmp[1] += base[1][1];
                tmp[2] += base[1][2];
            }
        }
    }

    public static void interpolateScalarFieldToSliceRaster(int[] iData, int[] dims, float[] p0, float[][] base,
            int iMin, int iMax, float is, WritableRaster raster,
            int w, int h, boolean useTrilinearInterpolation) {
        if (iData == null || dims == null || raster == null || p0 == null || base == null || w <= 0 || h <= 0) {
            return;
        }

        if (raster.getWidth() != w || raster.getHeight() != h) {
            return;
        }

        int x, y, z;
        int v;
        float[] tmp = new float[3];
        tmp[0] = p0[0];
        tmp[1] = p0[1];
        tmp[2] = p0[2];

        if (useTrilinearInterpolation) {
            for (int j = 0; j < h; j++) {
                for (int i = 0; i < w; i++) {
                    x = (int) tmp[0];
                    y = (int) tmp[1];
                    z = (int) tmp[2];
                    if (x >= 0 && x < dims[0] && y >= 0 && y < dims[1] && z >= 0 && z < dims[2]) {
                        v = getInterpolatedScalarData(iData, dims, tmp[0], tmp[1], tmp[2]);
                        raster.setSample(i, j, 0, (int) ((v - iMin) * is));
                    } else {
                        raster.setSample(i, j, 0, 0);
                    }
                    tmp[0] += base[0][0];
                    tmp[1] += base[0][1];
                    tmp[2] += base[0][2];
                }
                tmp[0] -= w * base[0][0];
                tmp[1] -= w * base[0][1];
                tmp[2] -= w * base[0][2];
                tmp[0] += base[1][0];
                tmp[1] += base[1][1];
                tmp[2] += base[1][2];
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
                        raster.setSample(i, j, 0, (int) ((iData[z * dims[0] * dims[1] + y * dims[0] + x] - iMin) * is));
                    } else {
                        raster.setSample(i, j, 0, 0);
                    }
                    tmp[0] += base[0][0];
                    tmp[1] += base[0][1];
                    tmp[2] += base[0][2];
                }
                tmp[0] -= w * base[0][0];
                tmp[1] -= w * base[0][1];
                tmp[2] -= w * base[0][2];
                tmp[0] += base[1][0];
                tmp[1] += base[1][1];
                tmp[2] += base[1][2];
            }
        }
    }
    
    public static void interpolateFieldToSliceColormappedImage(int[] iData, int veclen, int[] dims, float[] p0, float[][] base,
            BufferedImage image, int[] colorMapLUT, float low, float up,
            int fillColor, int w, int h, boolean useTrilinearInterpolation) {
        if (veclen == 1) {
            interpolateScalarFieldToSliceColormappedImage(iData, dims, p0, base, image, colorMapLUT, low, up, fillColor, w, h, useTrilinearInterpolation);
        } else {
            interpolateVectorFieldToSliceColormappedImage(iData, veclen, dims, p0, base, image, colorMapLUT, low, up, fillColor, w, h, useTrilinearInterpolation);
        }
    }

    public static void interpolateScalarFieldToSliceColormappedImage(int[] iData, int[] dims, float[] p0, float[][] base,
            BufferedImage image, int[] colorMapLUT, float low, float up,
            int fillColor, int w, int h, boolean useTrilinearInterpolation) {
        if (iData == null || dims == null || image == null || p0 == null || base == null || w <= 0 || h <= 0 || colorMapLUT == null) {
            return;
        }

        if (image.getWidth() != w || image.getHeight() != h) {
            return;
        }

        int x, y, z;
        float[] tmp = new float[3];
        tmp[0] = p0[0];
        tmp[1] = p0[1];
        tmp[2] = p0[2];

        int colorMapSize = colorMapLUT.length - 1;
        float cs = (float) colorMapSize / (up - low);
        int c;

        if (useTrilinearInterpolation) {
            for (int j = 0; j < h; j++) {
                for (int i = 0; i < w; i++) {
                    x = (int) tmp[0];
                    y = (int) tmp[1];
                    z = (int) tmp[2];
                    if (x >= 0 && x < dims[0] && y >= 0 && y < dims[1] && z >= 0 && z < dims[2]) {
                        c = (int) (((float) getInterpolatedScalarData(iData, dims, tmp[0], tmp[1], tmp[2]) - low) * cs);
                        if (c < 0) {
                            c = 0;
                        }
                        if (c > colorMapSize) {
                            c = colorMapSize;
                        }
                        image.setRGB(i, j, colorMapLUT[c]);
                    } else {
                        image.setRGB(i, j, fillColor);
                    }
                    tmp[0] += base[0][0];
                    tmp[1] += base[0][1];
                    tmp[2] += base[0][2];
                }
                tmp[0] -= w * base[0][0];
                tmp[1] -= w * base[0][1];
                tmp[2] -= w * base[0][2];
                tmp[0] += base[1][0];
                tmp[1] += base[1][1];
                tmp[2] += base[1][2];
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
                        c = (int) (((float) iData[z * dims[0] * dims[1] + y * dims[0] + x] - low) * cs);
                        if (c < 0) {
                            c = 0;
                        }
                        if (c > colorMapSize) {
                            c = colorMapSize;
                        }
                        image.setRGB(i, j, colorMapLUT[c]);
                    } else {
                        image.setRGB(i, j, fillColor);
                    }
                    tmp[0] += base[0][0];
                    tmp[1] += base[0][1];
                    tmp[2] += base[0][2];
                }
                tmp[0] -= w * base[0][0];
                tmp[1] -= w * base[0][1];
                tmp[2] -= w * base[0][2];
                tmp[0] += base[1][0];
                tmp[1] += base[1][1];
                tmp[2] += base[1][2];
            }
        }
    }
    
    public static void interpolateVectorFieldToSliceColormappedImage(int[] iData, int veclen, int[] dims, float[] p0, float[][] base,
            BufferedImage image, int[] colorMapLUT, float low, float up,
            int fillColor, int w, int h, boolean useTrilinearInterpolation) {
        if (iData == null || dims == null || image == null || p0 == null || base == null || w <= 0 || h <= 0 || colorMapLUT == null) {
            return;
        }

        if (image.getWidth() != w || image.getHeight() != h) {
            return;
        }

        int x, y, z;
        float[] tmp = new float[3];
        tmp[0] = p0[0];
        tmp[1] = p0[1];
        tmp[2] = p0[2];

        int colorMapSize = colorMapLUT.length - 1;
        float cs = (float) colorMapSize / (up - low);
        int c;
        double val,val1;
        int[] vect;

        if (useTrilinearInterpolation) {
            for (int j = 0; j < h; j++) {
                for (int i = 0; i < w; i++) {
                    x = (int) tmp[0];
                    y = (int) tmp[1];
                    z = (int) tmp[2];
                    if (x >= 0 && x < dims[0] && y >= 0 && y < dims[1] && z >= 0 && z < dims[2]) {
                        vect = getInterpolatedData(iData, dims, tmp[0], tmp[1], tmp[2]);
                        val = 0;
                        for (int k = 0; k < vect.length; k++) {
                            val += (double)vect[k]*(double)vect[k];                            
                        }
                        val = Math.sqrt(val);
                        c = (int) ((val - low) * cs);
                        if (c < 0) {
                            c = 0;
                        }
                        if (c > colorMapSize) {
                            c = colorMapSize;
                        }
                        image.setRGB(i, j, colorMapLUT[c]);
                    } else {
                        image.setRGB(i, j, fillColor);
                    }
                    tmp[0] += base[0][0];
                    tmp[1] += base[0][1];
                    tmp[2] += base[0][2];
                }
                tmp[0] -= w * base[0][0];
                tmp[1] -= w * base[0][1];
                tmp[2] -= w * base[0][2];
                tmp[0] += base[1][0];
                tmp[1] += base[1][1];
                tmp[2] += base[1][2];
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
                        val = 0;
                        for (int k = 0; k < veclen; k++) {
                            val1 = (float) iData[(z * dims[0] * dims[1] + y * dims[0] + x)*veclen + k];
                            val += val1*val1;
                        }
                        val = Math.sqrt(val);                        
                        c = (int) ((val - low) * cs);
                        if (c < 0) {
                            c = 0;
                        }
                        if (c > colorMapSize) {
                            c = colorMapSize;
                        }
                        image.setRGB(i, j, colorMapLUT[c]);
                    } else {
                        image.setRGB(i, j, fillColor);
                    }
                    tmp[0] += base[0][0];
                    tmp[1] += base[0][1];
                    tmp[2] += base[0][2];
                }
                tmp[0] -= w * base[0][0];
                tmp[1] -= w * base[0][1];
                tmp[2] -= w * base[0][2];
                tmp[0] += base[1][0];
                tmp[1] += base[1][1];
                tmp[2] += base[1][2];
            }
        }
    }
    

    public static float[] getInterpolatedData(float[] data, int[] dims, float u, float v, float w) {
        if (data == null || dims == null) {
            return null;
        }
        float[] c;
        int vlen;
        int inexact = 0;
        int i, j, k, m, n0, n1;
        switch (dims.length) {
            case 3:
                vlen = data.length / (dims[0] * dims[1] * dims[2]);
                if (data.length != vlen * dims[0] * dims[1] * dims[2]) {
                    return null;
                }
                c = new float[vlen];
                if (vlen == 1) {
                    c[0] = getInterpolatedScalarData(data, dims, u, v, w);
                    return c;
                }
                if (u < 0) {
                    u = 0;
                }
                if (u > dims[0] - 1) {
                    u = dims[0] - 1;
                }
                if (v < 0) {
                    v = 0;
                }
                if (v > dims[1] - 1) {
                    v = dims[1] - 1;
                }
                if (w < 0) {
                    w = 0;
                }
                if (w > dims[2] - 1) {
                    w = dims[2] - 1;
                }
                i = (int) u;
                u -= i;
                if (u != 0) {
                    inexact += 1;
                }
                j = (int) v;
                v -= j;
                if (v != 0) {
                    inexact += 2;
                }
                k = (int) w;
                w -= k;
                if (w != 0) {
                    inexact += 4;
                }
                m = vlen * ((dims[1] * k + j) * dims[0] + i);
                n0 = vlen * dims[0];
                n1 = vlen * dims[0] * dims[1];
                switch (inexact) {
                    case 0:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = data[m + l];
                        }
                        break;
                    case 1:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = u * data[m + l + vlen] + (1 - u) * data[m + l];
                        }
                        break;
                    case 2:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = v * data[m + l + n0] + (1 - v) * data[m + l];
                        }
                        break;
                    case 3:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = v * (u * data[m + l + n0 + vlen] + (1 - u) * data[m + l + n0])
                                    + (1 - v) * (u * data[m + l + vlen] + (1 - u) * data[m + l]);
                        }
                        break;
                    case 4:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = w * data[m + l + n1] + (1 - w) * data[m + l];
                        }
                        break;
                    case 5:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = w * (u * data[m + l + n1 + vlen] + (1 - u) * data[m + l + n1])
                                    + (1 - w) * (u * data[m + l + vlen] + (1 - u) * data[m + l]);
                        }
                        break;
                    case 6:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = w * (v * data[m + l + n1 + n0] + (1 - v) * data[m + l + n1])
                                    + (1 - w) * (v * data[m + l + n0] + (1 - v) * data[m + l]);
                        }
                        break;
                    case 7:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = w * (v * (u * data[m + l + n1 + n0 + vlen] + (1 - u) * data[m + l + n1 + n0])
                                    + (1 - v) * (u * data[m + l + n1 + vlen] + (1 - u) * data[m + l + n1]))
                                    + (1 - w) * (v * (u * data[m + l + n0 + vlen] + (1 - u) * data[m + l + n0])
                                    + (1 - v) * (u * data[m + l + vlen] + (1 - u) * data[m + l]));
                        }
                        break;
                }
                return c;
            case 2:
                vlen = data.length / (dims[0] * dims[1]);
                if (data.length != vlen * dims[0] * dims[1]) {
                    return null;
                }
                c = new float[vlen];
                if (vlen == 1) {
                    c[0] = getInterpolatedScalarData(data, dims, u, v, w);
                    return c;
                }
                if (u < 0) {
                    u = 0;
                }
                if (u > dims[0] - 1) {
                    u = dims[0] - 1;
                }
                if (v < 0) {
                    v = 0;
                }
                if (v > dims[1] - 1) {
                    v = dims[1] - 1;
                }
                i = (int) u;
                u -= i;
                if (u != 0) {
                    inexact += 1;
                }
                j = (int) v;
                v -= j;
                if (v != 0) {
                    inexact += 2;
                }
                m = vlen * (j * dims[0] + i);
                n0 = vlen * dims[0];
                switch (inexact) {
                    case 0:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = data[m + l];
                        }
                        break;
                    case 1:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = u * data[m + l + vlen] + (1 - u) * data[m + l];
                        }
                        break;
                    case 2:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = v * data[m + l + n0] + (1 - v) * data[m + l];
                        }
                        break;
                    case 3:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = v * (u * data[m + l + n0 + vlen] + (1 - u) * data[m + l + n0])
                                    + (1 - v) * (u * data[m + l + vlen] + (1 - u) * data[m + l]);
                        }
                        break;
                }
                return c;
            case 1:
                vlen = data.length / dims[0];
                if (data.length != vlen * dims[0]) {
                    return null;
                }
                c = new float[vlen];
                if (vlen == 1) {
                    c[0] = getInterpolatedScalarData(data, dims, u, v, w);
                    return c;
                }
                if (u < 0) {
                    u = 0;
                }
                if (u > dims[0] - 1) {
                    u = dims[0] - 1;
                }
                i = (int) u;
                u -= i;
                if (u != 0) {
                    inexact += 1;
                }
                m = vlen * i;
                switch (inexact) {
                    case 0:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = data[m + l];
                        }
                        break;
                    case 1:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = u * data[m + l + vlen] + (1 - u) * data[m + l];
                        }
                        break;
                }
                return c;
        }
        return null;
    }

    public static float getInterpolatedScalarData(float[] data, int[] dims, float u, float v, float w) {
        if (data == null || dims == null) {
            return 0.0f;
        }

        switch (dims.length) {
            case 3:
                return getInterpolatedScalarData3D(data, dims, u, v, w);
            case 2:
                return getInterpolatedScalarData2D(data, dims, u, v, w);
            case 1:
                return getInterpolatedScalarData1D(data, dims, u, v, w);
        }
        return 0.0f;
    }

    public static float getInterpolatedScalarData3D(float[] data, int[] dims, float u, float v, float w) {
        if (data == null || dims == null || dims.length != 3) {
            return 0.0f;
        }
        float c = 0.0f;
        int inexact = 0;
        int i, j, k, m, n0, n1;
        if (data.length != dims[0] * dims[1] * dims[2]) {
            return 0.0f;
        }
        if (u < 0) {
            u = 0;
        }
        if (u > dims[0] - 1) {
            u = dims[0] - 1;
        }
        if (v < 0) {
            v = 0;
        }
        if (v > dims[1] - 1) {
            v = dims[1] - 1;
        }
        if (w < 0) {
            w = 0;
        }
        if (w > dims[2] - 1) {
            w = dims[2] - 1;
        }
        i = (int) u;
        u -= i;
        if (u != 0) {
            inexact += 1;
        }
        j = (int) v;
        v -= j;
        if (v != 0) {
            inexact += 2;
        }
        k = (int) w;
        w -= k;
        if (w != 0) {
            inexact += 4;
        }
        m = (dims[1] * k + j) * dims[0] + i;
        n0 = dims[0];
        n1 = dims[0] * dims[1];
        switch (inexact) {
            case 0:
                c = data[m];
                break;
            case 1:
                c = u * data[m + 1] + (1 - u) * data[m];
                break;
            case 2:
                c = v * data[m + n0] + (1 - v) * data[m];
                break;
            case 3:
                c = v * (u * data[m + n0 + 1] + (1 - u) * data[m + n0])
                        + (1 - v) * (u * data[m + 1] + (1 - u) * data[m]);
                break;
            case 4:
                c = w * data[m + n1] + (1 - w) * data[m];
                break;
            case 5:
                c = w * (u * data[m + n1 + 1] + (1 - u) * data[m + n1])
                        + (1 - w) * (u * data[m + 1] + (1 - u) * data[m]);
                break;
            case 6:
                c = w * (v * data[m + n1 + n0] + (1 - v) * data[m + n1])
                        + (1 - w) * (v * data[m + n0] + (1 - v) * data[m]);
                break;
            case 7:
                c = w * (v * (u * data[m + n1 + n0 + 1] + (1 - u) * data[m + n1 + n0])
                        + (1 - v) * (u * data[m + n1 + 1] + (1 - u) * data[m + n1]))
                        + (1 - w) * (v * (u * data[m + n0 + 1] + (1 - u) * data[m + n0])
                        + (1 - v) * (u * data[m + 1] + (1 - u) * data[m]));
                break;
        }
        return c;
    }

    public static float getInterpolatedScalarData2D(float[] data, int[] dims, float u, float v, float w) {
        if (data == null || dims == null || dims.length != 2) {
            return 0.0f;
        }
        float c = 0.0f;
        int inexact = 0;
        int i, j, k, m, n0, n1;
        if (data.length != dims[0] * dims[1]) {
            return 0.0f;
        }
        if (u < 0) {
            u = 0;
        }
        if (u > dims[0] - 1) {
            u = dims[0] - 1;
        }
        if (v < 0) {
            v = 0;
        }
        if (v > dims[1] - 1) {
            v = dims[1] - 1;
        }
        i = (int) u;
        u -= i;
        if (u != 0) {
            inexact += 1;
        }
        j = (int) v;
        v -= j;
        if (v != 0) {
            inexact += 2;
        }
        m = j * dims[0] + i;
        n0 = dims[0];
        switch (inexact) {
            case 0:
                c = data[m];
                break;
            case 1:
                c = u * data[m + 1] + (1 - u) * data[m];
                break;
            case 2:
                c = v * data[m + n0] + (1 - v) * data[m];
                break;
            case 3:
                c = v * (u * data[m + n0 + 1] + (1 - u) * data[m + n0])
                        + (1 - v) * (u * data[m + 1] + (1 - u) * data[m]);
                break;
        }
        return c;
    }

    public static float getInterpolatedScalarData1D(float[] data, int[] dims, float u, float v, float w) {
        if (data == null || dims == null || dims.length != 1) {
            return 0.0f;
        }
        float c = 0.0f;
        int inexact = 0;
        int i;
        if (data.length != dims[0]) {
            return 0.0f;
        }
        if (u < 0) {
            u = 0;
        }
        if (u > dims[0] - 1) {
            u = dims[0] - 1;
        }
        i = (int) u;
        u -= i;
        if (u != 0) {
            inexact += 1;
        }
        switch (inexact) {
            case 0:
                c = data[i];
                break;
            case 1:
                c = u * data[i + 1] + (1 - u) * data[i];
                break;
        }
        return c;
    }

    public static void interpolateScalarFieldToSlice(float[] fData, int[] dims, float[] p0, float[][] base,
            float[] outData, int w, int h, boolean useTrilinearInterpolation) {
        interpolateScalarFieldToSlice(fData, dims, p0, base,
                0.0f, 1.0f, 1.0f, outData,
                w, h, useTrilinearInterpolation);
    }

    public static void interpolateScalarFieldToSlice(float[] fData, int[] dims, float[] p0, float[][] base,
            float fMin, float fMax, float fs, float[] outData,
            int w, int h, boolean useTrilinearInterpolation) {
        if (fData == null || dims == null || outData == null || p0 == null || base == null || w <= 0 || h <= 0) {
            return;
        }

        if (outData.length != w * h) {
            return;
        }

        int x, y, z;
        float v;
        float[] tmp = new float[3];
        tmp[0] = p0[0];
        tmp[1] = p0[1];
        tmp[2] = p0[2];

        if (useTrilinearInterpolation) {
            for (int j = 0, c = 0; j < h; j++) {
                for (int i = 0; i < w; i++, c++) {
                    x = (int) tmp[0];
                    y = (int) tmp[1];
                    z = (int) tmp[2];
                    if (x >= 0 && x < dims[0] && y >= 0 && y < dims[1] && z >= 0 && z < dims[2]) {
                        v = getInterpolatedScalarData(fData, dims, tmp[0], tmp[1], tmp[2]);
                        outData[c] = (v - fMin) * fs;
                    } else {
                        outData[c] = 0;
                    }
                    tmp[0] += base[0][0];
                    tmp[1] += base[0][1];
                    tmp[2] += base[0][2];
                }
                tmp[0] -= w * base[0][0];
                tmp[1] -= w * base[0][1];
                tmp[2] -= w * base[0][2];
                tmp[0] += base[1][0];
                tmp[1] += base[1][1];
                tmp[2] += base[1][2];
            }
        } else {
            tmp[0] += 0.5f;
            tmp[1] += 0.5f;
            tmp[2] += 0.5f;
            for (int j = 0, c = 0; j < h; j++) {
                for (int i = 0; i < w; i++, c++) {
                    x = (int) tmp[0];
                    y = (int) tmp[1];
                    z = (int) tmp[2];
                    if (x >= 0 && x < dims[0] && y >= 0 && y < dims[1] && z >= 0 && z < dims[2]) {
                        outData[c] = (fData[z * dims[0] * dims[1] + y * dims[0] + x] - fMin) * fs;
                    } else {
                        outData[c] = 0;
                    }
                    tmp[0] += base[0][0];
                    tmp[1] += base[0][1];
                    tmp[2] += base[0][2];
                }
                tmp[0] -= w * base[0][0];
                tmp[1] -= w * base[0][1];
                tmp[2] -= w * base[0][2];
                tmp[0] += base[1][0];
                tmp[1] += base[1][1];
                tmp[2] += base[1][2];
            }
        }
    }

    public static void interpolateScalarFieldToSliceRaster(float[] fData, int[] dims, float[] p0, float[][] base,
            float fMin, float fMax, float fs, WritableRaster raster,
            int w, int h, boolean useTrilinearInterpolation) {
        if (fData == null || dims == null || raster == null || p0 == null || base == null || w <= 0 || h <= 0) {
            return;
        }

        if (raster.getWidth() != w || raster.getHeight() != h) {
            return;
        }

        int x, y, z;
        float v;
        float[] tmp = new float[3];
        tmp[0] = p0[0];
        tmp[1] = p0[1];
        tmp[2] = p0[2];

        if (useTrilinearInterpolation) {
            for (int j = 0; j < h; j++) {
                for (int i = 0; i < w; i++) {
                    x = (int) tmp[0];
                    y = (int) tmp[1];
                    z = (int) tmp[2];
                    if (x >= 0 && x < dims[0] && y >= 0 && y < dims[1] && z >= 0 && z < dims[2]) {
                        v = getInterpolatedScalarData(fData, dims, tmp[0], tmp[1], tmp[2]);
                        raster.setSample(i, j, 0, (v - fMin) * fs);
                    } else {
                        raster.setSample(i, j, 0, 0);
                    }
                    tmp[0] += base[0][0];
                    tmp[1] += base[0][1];
                    tmp[2] += base[0][2];
                }
                tmp[0] -= w * base[0][0];
                tmp[1] -= w * base[0][1];
                tmp[2] -= w * base[0][2];
                tmp[0] += base[1][0];
                tmp[1] += base[1][1];
                tmp[2] += base[1][2];
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
                        raster.setSample(i, j, 0, (fData[z * dims[0] * dims[1] + y * dims[0] + x] - fMin) * fs);
                    } else {
                        raster.setSample(i, j, 0, 0);
                    }
                    tmp[0] += base[0][0];
                    tmp[1] += base[0][1];
                    tmp[2] += base[0][2];
                }
                tmp[0] -= w * base[0][0];
                tmp[1] -= w * base[0][1];
                tmp[2] -= w * base[0][2];
                tmp[0] += base[1][0];
                tmp[1] += base[1][1];
                tmp[2] += base[1][2];
            }
        }
    }

    public static void interpolateFieldToSliceColormappedImage(float[] fData, int veclen, int[] dims, float[] p0, float[][] base,
            BufferedImage image, int[] colorMapLUT, float low, float up,
            int fillColor, int w, int h, boolean useTrilinearInterpolation) {
        if (veclen == 1) {
            interpolateScalarFieldToSliceColormappedImage(fData, dims, p0, base, image, colorMapLUT, low, up, fillColor, w, h, useTrilinearInterpolation);
        } else {
            interpolateVectorFieldToSliceColormappedImage(fData, veclen, dims, p0, base, image, colorMapLUT, low, up, fillColor, w, h, useTrilinearInterpolation);
        }
    }

    public static void interpolateScalarFieldToSliceColormappedImage(float[] fData, int[] dims, float[] p0, float[][] base,
            BufferedImage image, int[] colorMapLUT, float low, float up,
            int fillColor, int w, int h, boolean useTrilinearInterpolation) {
        if (fData == null || dims == null || image == null || p0 == null || base == null || w <= 0 || h <= 0 || colorMapLUT == null) {
            return;
        }

        if (image.getWidth() != w || image.getHeight() != h) {
            return;
        }

        int x, y, z;
        float[] tmp = new float[3];
        tmp[0] = p0[0];
        tmp[1] = p0[1];
        tmp[2] = p0[2];

        int colorMapSize = colorMapLUT.length - 1;
        float cs = (float) colorMapSize / (up - low);
        int c;

        if (useTrilinearInterpolation) {
            for (int j = 0; j < h; j++) {
                for (int i = 0; i < w; i++) {
                    x = (int) tmp[0];
                    y = (int) tmp[1];
                    z = (int) tmp[2];
                    if (x >= 0 && x < dims[0] && y >= 0 && y < dims[1] && z >= 0 && z < dims[2]) {
                        c = (int) ((getInterpolatedScalarData(fData, dims, tmp[0], tmp[1], tmp[2]) - low) * cs);
                        if (c < 0) {
                            c = 0;
                        }
                        if (c > colorMapSize) {
                            c = colorMapSize;
                        }
                        image.setRGB(i, j, colorMapLUT[c]);
                    } else {
                        image.setRGB(i, j, fillColor);
                    }
                    tmp[0] += base[0][0];
                    tmp[1] += base[0][1];
                    tmp[2] += base[0][2];
                }
                tmp[0] -= w * base[0][0];
                tmp[1] -= w * base[0][1];
                tmp[2] -= w * base[0][2];
                tmp[0] += base[1][0];
                tmp[1] += base[1][1];
                tmp[2] += base[1][2];
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
                        c = (int) ((fData[z * dims[0] * dims[1] + y * dims[0] + x] - low) * cs);
                        if (c < 0) {
                            c = 0;
                        }
                        if (c > colorMapSize) {
                            c = colorMapSize;
                        }
                        image.setRGB(i, j, colorMapLUT[c]);
                    } else {
                        image.setRGB(i, j, fillColor);
                    }
                    tmp[0] += base[0][0];
                    tmp[1] += base[0][1];
                    tmp[2] += base[0][2];
                }
                tmp[0] -= w * base[0][0];
                tmp[1] -= w * base[0][1];
                tmp[2] -= w * base[0][2];
                tmp[0] += base[1][0];
                tmp[1] += base[1][1];
                tmp[2] += base[1][2];
            }
        }
    }

    public static void interpolateVectorFieldToSliceColormappedImage(float[] fData, int veclen, int[] dims, float[] p0, float[][] base,
            BufferedImage image, int[] colorMapLUT, float low, float up,
            int fillColor, int w, int h, boolean useTrilinearInterpolation) {
        if (fData == null || dims == null || image == null || p0 == null || base == null || w <= 0 || h <= 0 || colorMapLUT == null) {
            return;
        }

        if (image.getWidth() != w || image.getHeight() != h) {
            return;
        }

        if (veclen == 1) {
            interpolateScalarFieldToSliceColormappedImage(fData, dims, p0, base, image, colorMapLUT, low, up, fillColor, w, h, useTrilinearInterpolation);
            return;
        }

        int x, y, z;
        float[] tmp = new float[3];
        tmp[0] = p0[0];
        tmp[1] = p0[1];
        tmp[2] = p0[2];

        int colorMapSize = colorMapLUT.length - 1;
        float cs = (float) colorMapSize / (up - low);
        int c;
        float[] vect;
        double val, val1;

        if (useTrilinearInterpolation) {
            for (int j = 0; j < h; j++) {
                for (int i = 0; i < w; i++) {
                    x = (int) tmp[0];
                    y = (int) tmp[1];
                    z = (int) tmp[2];
                    if (x >= 0 && x < dims[0] && y >= 0 && y < dims[1] && z >= 0 && z < dims[2]) {
                        vect = getInterpolatedData(fData, dims, tmp[0], tmp[1], tmp[2]);
                        val = 0;
                        for (int k = 0; k < vect.length; k++) {
                            val += vect[k] * vect[k];
                        }
                        val = Math.sqrt(val);
                        c = (int) ((val - low) * cs);
                        if (c < 0) {
                            c = 0;
                        }
                        if (c > colorMapSize) {
                            c = colorMapSize;
                        }
                        image.setRGB(i, j, colorMapLUT[c]);
                    } else {
                        image.setRGB(i, j, fillColor);
                    }
                    tmp[0] += base[0][0];
                    tmp[1] += base[0][1];
                    tmp[2] += base[0][2];
                }
                tmp[0] -= w * base[0][0];
                tmp[1] -= w * base[0][1];
                tmp[2] -= w * base[0][2];
                tmp[0] += base[1][0];
                tmp[1] += base[1][1];
                tmp[2] += base[1][2];
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
                        val = 0;
                        for (int v = 0; v < veclen; v++) {
                            val1 = fData[(z * dims[0] * dims[1] + y * dims[0] + x) * veclen + v];
                            val += val1 * val1;
                        }
                        val = Math.sqrt(val);
                        c = (int) ((val - low) * cs);

                        if (c < 0) {
                            c = 0;
                        }
                        if (c > colorMapSize) {
                            c = colorMapSize;
                        }
                        image.setRGB(i, j, colorMapLUT[c]);
                    } else {
                        image.setRGB(i, j, fillColor);
                    }
                    tmp[0] += base[0][0];
                    tmp[1] += base[0][1];
                    tmp[2] += base[0][2];
                }
                tmp[0] -= w * base[0][0];
                tmp[1] -= w * base[0][1];
                tmp[2] -= w * base[0][2];
                tmp[0] += base[1][0];
                tmp[1] += base[1][1];
                tmp[2] += base[1][2];
            }
        }
    }

    public static double[] getInterpolatedData(double[] data, int[] dims, float u, float v, float w) {
        if (data == null || dims == null) {
            return null;
        }
        double[] c;
        int vlen;
        int inexact = 0;
        int i, j, k, m, n0, n1;
        switch (dims.length) {
            case 3:
                vlen = data.length / (dims[0] * dims[1] * dims[2]);
                if (data.length != vlen * dims[0] * dims[1] * dims[2]) {
                    return null;
                }
                c = new double[vlen];
                if (vlen == 1) {
                    c[0] = getInterpolatedScalarData(data, dims, u, v, w);
                    return c;
                }
                if (u < 0) {
                    u = 0;
                }
                if (u > dims[0] - 1) {
                    u = dims[0] - 1;
                }
                if (v < 0) {
                    v = 0;
                }
                if (v > dims[1] - 1) {
                    v = dims[1] - 1;
                }
                if (w < 0) {
                    w = 0;
                }
                if (w > dims[2] - 1) {
                    w = dims[2] - 1;
                }
                i = (int) u;
                u -= i;
                if (u != 0) {
                    inexact += 1;
                }
                j = (int) v;
                v -= j;
                if (v != 0) {
                    inexact += 2;
                }
                k = (int) w;
                w -= k;
                if (w != 0) {
                    inexact += 4;
                }
                m = vlen * ((dims[1] * k + j) * dims[0] + i);
                n0 = vlen * dims[0];
                n1 = vlen * dims[0] * dims[1];
                switch (inexact) {
                    case 0:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = data[m + l];
                        }
                        break;
                    case 1:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = u * data[m + l + vlen] + (1 - u) * data[m + l];
                        }
                        break;
                    case 2:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = v * data[m + l + n0] + (1 - v) * data[m + l];
                        }
                        break;
                    case 3:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = v * (u * data[m + l + n0 + vlen] + (1 - u) * data[m + l + n0])
                                    + (1 - v) * (u * data[m + l + vlen] + (1 - u) * data[m + l]);
                        }
                        break;
                    case 4:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = w * data[m + l + n1] + (1 - w) * data[m + l];
                        }
                        break;
                    case 5:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = w * (u * data[m + l + n1 + vlen] + (1 - u) * data[m + l + n1])
                                    + (1 - w) * (u * data[m + l + vlen] + (1 - u) * data[m + l]);
                        }
                        break;
                    case 6:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = w * (v * data[m + l + n1 + n0] + (1 - v) * data[m + l + n1])
                                    + (1 - w) * (v * data[m + l + n0] + (1 - v) * data[m + l]);
                        }
                        break;
                    case 7:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = w * (v * (u * data[m + l + n1 + n0 + vlen] + (1 - u) * data[m + l + n1 + n0])
                                    + (1 - v) * (u * data[m + l + n1 + vlen] + (1 - u) * data[m + l + n1]))
                                    + (1 - w) * (v * (u * data[m + l + n0 + vlen] + (1 - u) * data[m + l + n0])
                                    + (1 - v) * (u * data[m + l + vlen] + (1 - u) * data[m + l]));
                        }
                        break;
                }
                return c;
            case 2:
                vlen = data.length / (dims[0] * dims[1]);
                if (data.length != vlen * dims[0] * dims[1]) {
                    return null;
                }
                c = new double[vlen];
                if (vlen == 1) {
                    c[0] = getInterpolatedScalarData(data, dims, u, v, w);
                    return c;
                }

                if (u < 0) {
                    u = 0;
                }
                if (u > dims[0] - 1) {
                    u = dims[0] - 1;
                }
                if (v < 0) {
                    v = 0;
                }
                if (v > dims[1] - 1) {
                    v = dims[1] - 1;
                }
                i = (int) u;
                u -= i;
                if (u != 0) {
                    inexact += 1;
                }
                j = (int) v;
                v -= j;
                if (v != 0) {
                    inexact += 2;
                }
                m = vlen * (j * dims[0] + i);
                n0 = vlen * dims[0];
                switch (inexact) {
                    case 0:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = data[m + l];
                        }
                        break;
                    case 1:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = u * data[m + l + vlen] + (1 - u) * data[m + l];
                        }
                        break;
                    case 2:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = v * data[m + l + n0] + (1 - v) * data[m + l];
                        }
                        break;
                    case 3:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = v * (u * data[m + l + n0 + vlen] + (1 - u) * data[m + l + n0])
                                    + (1 - v) * (u * data[m + l + vlen] + (1 - u) * data[m + l]);
                        }
                        break;
                }
                return c;
            case 1:
                vlen = data.length / dims[0];
                if (data.length != vlen * dims[0]) {
                    return null;
                }
                c = new double[vlen];
                if (vlen == 1) {
                    c[0] = getInterpolatedScalarData(data, dims, u, v, w);
                    return c;
                }
                if (u < 0) {
                    u = 0;
                }
                if (u > dims[0] - 1) {
                    u = dims[0] - 1;
                }
                i = (int) u;
                u -= i;
                if (u != 0) {
                    inexact += 1;
                }
                m = vlen * i;
                switch (inexact) {
                    case 0:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = data[m + l];
                        }
                        break;
                    case 1:
                        for (int l = 0; l < vlen; l++) {
                            c[l] = u * data[m + l + vlen] + (1 - u) * data[m + l];
                        }
                        break;
                }
                return c;
        }
        return null;
    }

    public static double getInterpolatedScalarData(double[] data, int[] dims, float u, float v, float w) {
        if (data == null || dims == null) {
            return 0.0;
        }

        switch (dims.length) {
            case 3:
                return getInterpolatedScalarData3D(data, dims, u, v, w);
            case 2:
                return getInterpolatedScalarData2D(data, dims, u, v, w);
            case 1:
                return getInterpolatedScalarData1D(data, dims, u, v, w);
        }
        return 0.0f;
    }

    public static double getInterpolatedScalarData3D(double[] data, int[] dims, float u, float v, float w) {
        if (data == null || dims == null || dims.length != 3) {
            return 0.0;
        }
        double c = 0.0;
        int inexact = 0;
        int i, j, k, m, n0, n1;
        if (data.length != dims[0] * dims[1] * dims[2]) {
            return 0.0;
        }
        if (u < 0) {
            u = 0;
        }
        if (u > dims[0] - 1) {
            u = dims[0] - 1;
        }
        if (v < 0) {
            v = 0;
        }
        if (v > dims[1] - 1) {
            v = dims[1] - 1;
        }
        if (w < 0) {
            w = 0;
        }
        if (w > dims[2] - 1) {
            w = dims[2] - 1;
        }
        i = (int) u;
        u -= i;
        if (u != 0) {
            inexact += 1;
        }
        j = (int) v;
        v -= j;
        if (v != 0) {
            inexact += 2;
        }
        k = (int) w;
        w -= k;
        if (w != 0) {
            inexact += 4;
        }
        m = (dims[1] * k + j) * dims[0] + i;
        n0 = dims[0];
        n1 = dims[0] * dims[1];
        switch (inexact) {
            case 0:
                c = data[m];
                break;
            case 1:
                c = u * data[m + 1] + (1 - u) * data[m];
                break;
            case 2:
                c = v * data[m + n0] + (1 - v) * data[m];
                break;
            case 3:
                c = v * (u * data[m + n0 + 1] + (1 - u) * data[m + n0])
                        + (1 - v) * (u * data[m + 1] + (1 - u) * data[m]);
                break;
            case 4:
                c = w * data[m + n1] + (1 - w) * data[m];
                break;
            case 5:
                c = w * (u * data[m + n1 + 1] + (1 - u) * data[m + n1])
                        + (1 - w) * (u * data[m + 1] + (1 - u) * data[m]);
                break;
            case 6:
                c = w * (v * data[m + n1 + n0] + (1 - v) * data[m + n1])
                        + (1 - w) * (v * data[m + n0] + (1 - v) * data[m]);
                break;
            case 7:
                c = w * (v * (u * data[m + n1 + n0 + 1] + (1 - u) * data[m + n1 + n0])
                        + (1 - v) * (u * data[m + n1 + 1] + (1 - u) * data[m + n1]))
                        + (1 - w) * (v * (u * data[m + n0 + 1] + (1 - u) * data[m + n0])
                        + (1 - v) * (u * data[m + 1] + (1 - u) * data[m]));
                break;
        }
        return c;
    }

    public static double getInterpolatedScalarData2D(double[] data, int[] dims, float u, float v, float w) {
        if (data == null || dims == null || dims.length != 2) {
            return 0.0f;
        }
        double c = 0.0;
        int inexact = 0;
        int i, j, m, n0;
        if (data.length != dims[0] * dims[1]) {
            return 0.0f;
        }
        if (u < 0) {
            u = 0;
        }
        if (u > dims[0] - 1) {
            u = dims[0] - 1;
        }
        if (v < 0) {
            v = 0;
        }
        if (v > dims[1] - 1) {
            v = dims[1] - 1;
        }
        i = (int) u;
        u -= i;
        if (u != 0) {
            inexact += 1;
        }
        j = (int) v;
        v -= j;
        if (v != 0) {
            inexact += 2;
        }
        m = j * dims[0] + i;
        n0 = dims[0];
        switch (inexact) {
            case 0:
                c = data[m];
                break;
            case 1:
                c = u * data[m + 1] + (1 - u) * data[m];
                break;
            case 2:
                c = v * data[m + n0] + (1 - v) * data[m];
                break;
            case 3:
                c = v * (u * data[m + n0 + 1] + (1 - u) * data[m + n0])
                        + (1 - v) * (u * data[m + 1] + (1 - u) * data[m]);
                break;
        }
        return c;
    }

    public static double getInterpolatedScalarData1D(double[] data, int[] dims, float u, float v, float w) {
        if (data == null || dims == null || dims.length != 1) {
            return 0.0;
        }
        double c = 0.0;
        int inexact = 0;
        int i;
        if (data.length != dims[0]) {
            return 0.0;
        }
        if (u < 0) {
            u = 0;
        }
        if (u > dims[0] - 1) {
            u = dims[0] - 1;
        }
        i = (int) u;
        u -= i;
        if (u != 0) {
            inexact += 1;
        }
        switch (inexact) {
            case 0:
                c = data[i];
                break;
            case 1:
                c = u * data[i + 1] + (1 - u) * data[i];
                break;
        }
        return c;
    }

    public static void interpolateScalarFieldToSlice(double[] dData, int[] dims, float[] p0, float[][] base,
            double[] outData, int w, int h, boolean useTrilinearInterpolation) {
        interpolateScalarFieldToSlice(dData, dims, p0, base,
                0.0, 1.0, 1.0, outData,
                w, h, useTrilinearInterpolation);
    }

    public static void interpolateScalarFieldToSlice(double[] dData, int[] dims, float[] p0, float[][] base,
            double dMin, double dMax, double ds, double[] outData,
            int w, int h, boolean useTrilinearInterpolation) {
        if (dData == null || dims == null || outData == null || p0 == null || base == null || w <= 0 || h <= 0) {
            return;
        }

        if (outData.length != w * h) {
            return;
        }

        int x, y, z;
        double v;
        float[] tmp = new float[3];
        tmp[0] = p0[0];
        tmp[1] = p0[1];
        tmp[2] = p0[2];

        if (useTrilinearInterpolation) {
            for (int j = 0, c = 0; j < h; j++) {
                for (int i = 0; i < w; i++, c++) {
                    x = (int) tmp[0];
                    y = (int) tmp[1];
                    z = (int) tmp[2];
                    if (x >= 0 && x < dims[0] && y >= 0 && y < dims[1] && z >= 0 && z < dims[2]) {
                        v = getInterpolatedScalarData(dData, dims, tmp[0], tmp[1], tmp[2]);
                        outData[c] = (v - dMin) * ds;
                    } else {
                        outData[c] = 0;
                    }
                    tmp[0] += base[0][0];
                    tmp[1] += base[0][1];
                    tmp[2] += base[0][2];
                }
                tmp[0] -= w * base[0][0];
                tmp[1] -= w * base[0][1];
                tmp[2] -= w * base[0][2];
                tmp[0] += base[1][0];
                tmp[1] += base[1][1];
                tmp[2] += base[1][2];
            }
        } else {
            tmp[0] += 0.5f;
            tmp[1] += 0.5f;
            tmp[2] += 0.5f;
            for (int j = 0, c = 0; j < h; j++) {
                for (int i = 0; i < w; i++, c++) {
                    x = (int) tmp[0];
                    y = (int) tmp[1];
                    z = (int) tmp[2];
                    if (x >= 0 && x < dims[0] && y >= 0 && y < dims[1] && z >= 0 && z < dims[2]) {
                        outData[c] = (dData[z * dims[0] * dims[1] + y * dims[0] + x] - dMin) * ds;
                    } else {
                        outData[c] = 0;
                    }
                    tmp[0] += base[0][0];
                    tmp[1] += base[0][1];
                    tmp[2] += base[0][2];
                }
                tmp[0] -= w * base[0][0];
                tmp[1] -= w * base[0][1];
                tmp[2] -= w * base[0][2];
                tmp[0] += base[1][0];
                tmp[1] += base[1][1];
                tmp[2] += base[1][2];
            }
        }
    }

    public static void interpolateScalarFieldToSliceRaster(double[] dData, int[] dims, float[] p0, float[][] base,
            double dMin, double dMax, double ds, WritableRaster raster,
            int w, int h, boolean useTrilinearInterpolation) {
        if (dData == null || dims == null || raster == null || p0 == null || base == null || w <= 0 || h <= 0) {
            return;
        }

        if (raster.getWidth() != w || raster.getHeight() != h) {
            return;
        }

        int x, y, z;
        double v;
        float[] tmp = new float[3];
        tmp[0] = p0[0];
        tmp[1] = p0[1];
        tmp[2] = p0[2];

        if (useTrilinearInterpolation) {
            for (int j = 0; j < h; j++) {
                for (int i = 0; i < w; i++) {
                    x = (int) tmp[0];
                    y = (int) tmp[1];
                    z = (int) tmp[2];
                    if (x >= 0 && x < dims[0] && y >= 0 && y < dims[1] && z >= 0 && z < dims[2]) {
                        v = getInterpolatedScalarData(dData, dims, tmp[0], tmp[1], tmp[2]);
                        raster.setSample(i, j, 0, (v - dMin) * ds);
                    } else {
                        raster.setSample(i, j, 0, 0);
                    }
                    tmp[0] += base[0][0];
                    tmp[1] += base[0][1];
                    tmp[2] += base[0][2];
                }
                tmp[0] -= w * base[0][0];
                tmp[1] -= w * base[0][1];
                tmp[2] -= w * base[0][2];
                tmp[0] += base[1][0];
                tmp[1] += base[1][1];
                tmp[2] += base[1][2];
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
                        raster.setSample(i, j, 0, (dData[z * dims[0] * dims[1] + y * dims[0] + x] - dMin) * ds);
                    } else {
                        raster.setSample(i, j, 0, 0);
                    }
                    tmp[0] += base[0][0];
                    tmp[1] += base[0][1];
                    tmp[2] += base[0][2];
                }
                tmp[0] -= w * base[0][0];
                tmp[1] -= w * base[0][1];
                tmp[2] -= w * base[0][2];
                tmp[0] += base[1][0];
                tmp[1] += base[1][1];
                tmp[2] += base[1][2];
            }
        }
    }

    public static void interpolateFieldToSliceColormappedImage(double[] dData, int veclen, int[] dims, float[] p0, float[][] base,
            BufferedImage image, int[] colorMapLUT, float low, float up,
            int fillColor, int w, int h, boolean useTrilinearInterpolation) {
        if (veclen == 1) {
            interpolateScalarFieldToSliceColormappedImage(dData, dims, p0, base, image, colorMapLUT, low, up, fillColor, w, h, useTrilinearInterpolation);
        } else {
            interpolateVectorFieldToSliceColormappedImage(dData, veclen, dims, p0, base, image, colorMapLUT, low, up, fillColor, w, h, useTrilinearInterpolation);
        }
    }
    
    public static void interpolateScalarFieldToSliceColormappedImage(double[] dData, int[] dims, float[] p0, float[][] base,
            BufferedImage image, int[] colorMapLUT, double low, double up,
            int fillColor, int w, int h, boolean useTrilinearInterpolation) {
        if (dData == null || dims == null || image == null || p0 == null || base == null || w <= 0 || h <= 0 || colorMapLUT == null) {
            return;
        }

        if (image.getWidth() != w || image.getHeight() != h) {
            return;
        }

        int x, y, z;
        float[] tmp = new float[3];
        tmp[0] = p0[0];
        tmp[1] = p0[1];
        tmp[2] = p0[2];

        int colorMapSize = colorMapLUT.length - 1;
        double cs = (double) colorMapSize / (up - low);
        int c;

        if (useTrilinearInterpolation) {
            for (int j = 0; j < h; j++) {
                for (int i = 0; i < w; i++) {
                    x = (int) tmp[0];
                    y = (int) tmp[1];
                    z = (int) tmp[2];
                    if (x >= 0 && x < dims[0] && y >= 0 && y < dims[1] && z >= 0 && z < dims[2]) {
                        c = (int) ((getInterpolatedScalarData(dData, dims, tmp[0], tmp[1], tmp[2]) - low) * cs);
                        if (c < 0) {
                            c = 0;
                        }
                        if (c > colorMapSize) {
                            c = colorMapSize;
                        }
                        image.setRGB(i, j, colorMapLUT[c]);
                    } else {
                        image.setRGB(i, j, fillColor);
                    }
                    tmp[0] += base[0][0];
                    tmp[1] += base[0][1];
                    tmp[2] += base[0][2];
                }
                tmp[0] -= w * base[0][0];
                tmp[1] -= w * base[0][1];
                tmp[2] -= w * base[0][2];
                tmp[0] += base[1][0];
                tmp[1] += base[1][1];
                tmp[2] += base[1][2];
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
                        c = (int) ((dData[z * dims[0] * dims[1] + y * dims[0] + x] - low) * cs);
                        if (c < 0) {
                            c = 0;
                        }
                        if (c > colorMapSize) {
                            c = colorMapSize;
                        }
                        image.setRGB(i, j, colorMapLUT[c]);
                    } else {
                        image.setRGB(i, j, fillColor);
                    }
                    tmp[0] += base[0][0];
                    tmp[1] += base[0][1];
                    tmp[2] += base[0][2];
                }
                tmp[0] -= w * base[0][0];
                tmp[1] -= w * base[0][1];
                tmp[2] -= w * base[0][2];
                tmp[0] += base[1][0];
                tmp[1] += base[1][1];
                tmp[2] += base[1][2];
            }
        }
    }

    public static void interpolateVectorFieldToSliceColormappedImage(double[] dData, int veclen, int[] dims, float[] p0, float[][] base,
            BufferedImage image, int[] colorMapLUT, float low, float up,
            int fillColor, int w, int h, boolean useTrilinearInterpolation) {
        if (dData == null || dims == null || image == null || p0 == null || base == null || w <= 0 || h <= 0 || colorMapLUT == null) {
            return;
        }

        if (image.getWidth() != w || image.getHeight() != h) {
            return;
        }

        int x, y, z;
        float[] tmp = new float[3];
        tmp[0] = p0[0];
        tmp[1] = p0[1];
        tmp[2] = p0[2];

        int colorMapSize = colorMapLUT.length - 1;
        float cs = (float) colorMapSize / (up - low);
        int c;
        double val,val1;
        double[] vect;

        if (useTrilinearInterpolation) {
            for (int j = 0; j < h; j++) {
                for (int i = 0; i < w; i++) {
                    x = (int) tmp[0];
                    y = (int) tmp[1];
                    z = (int) tmp[2];
                    if (x >= 0 && x < dims[0] && y >= 0 && y < dims[1] && z >= 0 && z < dims[2]) {
                        vect = getInterpolatedData(dData, dims, tmp[0], tmp[1], tmp[2]);
                        val = 0;
                        for (int k = 0; k < vect.length; k++) {
                            val += vect[k]*vect[k];                            
                        }
                        val = Math.sqrt(val);
                        c = (int) ((val - low) * cs);
                        if (c < 0) {
                            c = 0;
                        }
                        if (c > colorMapSize) {
                            c = colorMapSize;
                        }
                        image.setRGB(i, j, colorMapLUT[c]);
                    } else {
                        image.setRGB(i, j, fillColor);
                    }
                    tmp[0] += base[0][0];
                    tmp[1] += base[0][1];
                    tmp[2] += base[0][2];
                }
                tmp[0] -= w * base[0][0];
                tmp[1] -= w * base[0][1];
                tmp[2] -= w * base[0][2];
                tmp[0] += base[1][0];
                tmp[1] += base[1][1];
                tmp[2] += base[1][2];
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
                        val = 0;
                        for (int k = 0; k < veclen; k++) {
                            val1 = dData[(z * dims[0] * dims[1] + y * dims[0] + x)*veclen + k];
                            val += val1*val1;
                        }
                        val = Math.sqrt(val);                        
                        c = (int) ((val - low) * cs);
                        if (c < 0) {
                            c = 0;
                        }
                        if (c > colorMapSize) {
                            c = colorMapSize;
                        }
                        image.setRGB(i, j, colorMapLUT[c]);
                    } else {
                        image.setRGB(i, j, fillColor);
                    }
                    tmp[0] += base[0][0];
                    tmp[1] += base[0][1];
                    tmp[2] += base[0][2];
                }
                tmp[0] -= w * base[0][0];
                tmp[1] -= w * base[0][1];
                tmp[2] -= w * base[0][2];
                tmp[0] += base[1][0];
                tmp[1] += base[1][1];
                tmp[2] += base[1][2];
            }
        }
    }
    
    private RegularFieldInterpolator() {
    }
}
