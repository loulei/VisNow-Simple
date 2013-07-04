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

package pl.edu.icm.visnow.lib.basic.mappers.VolumeRenderer;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import javax.media.j3d.*;
import javax.vecmath.Color3f;
import pl.edu.icm.visnow.datamaps.ColorMapManager;
import pl.edu.icm.visnow.datamaps.colormap2d.ColorMap2D;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.geometries.objects.generics.OpenAppearance;
import pl.edu.icm.visnow.geometries.objects.generics.OpenBranchGroup;
import pl.edu.icm.visnow.geometries.objects.generics.OpenMaterial;
import pl.edu.icm.visnow.geometries.objects.generics.OpenShape3D;
import pl.edu.icm.visnow.geometries.parameters.DataMappingParams;
import pl.edu.icm.visnow.geometries.utils.ColorMapper;
import pl.edu.icm.visnow.geometries.utils.TextureMapper;
import pl.edu.icm.visnow.gui.events.FloatValueModificationEvent;
import pl.edu.icm.visnow.gui.events.FloatValueModificationListener;
import pl.edu.icm.visnow.system.main.VisNow;

/**
 *
 * @author  Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public final class TextureVolumeRenderer extends OpenBranchGroup {

    private RegularField inField;
    private QuadArray[][] volMeshes = new QuadArray[3][2];
    private int n2, n1, n0;       //real data array dims
    private int nn0, nn1, nn2;    //data array dims scaled up to power of 2
    private int nvert = 0;
    private float[] coords = null;
    private float[] texCoords = null;
    private int[] trMap = new int[256];
    private OpenShape3D tr = null;
    private ImageComponent3D i3d = null;
    private Texture3D tx3d = null;
    private TransparencyAttributes ta = null;
    private OpenAppearance ap = new OpenAppearance();
    private int axis;
    private boolean dir;
    private int minX;
    private Params params;
    private DataMappingParams dataMappingParams = null;
    private int minY, minZ, maxX, maxY, maxZ;
    private TransformGroup tg = null;
    private BufferedImage[] texImages;
    private int[][] texSlices;
    private int nThreads = Runtime.getRuntime().availableProcessors();
    private int[] textureImageData = null;
    private Runtime runtime = Runtime.getRuntime();
    long used = (runtime.totalMemory() - runtime.freeMemory()) / 1024;

    public TextureVolumeRenderer(RegularField inField, Params params, boolean needPow2Textures) {
        runtime.gc();
        this.setCapability(BranchGroup.ALLOW_LOCAL_TO_VWORLD_READ);
        this.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        this.setCapability(BranchGroup.ALLOW_DETACH);
        for (int i = 0; i < trMap.length; i++) {
            trMap[i] = (byte) (i / 3);
        }
        tr = new OpenShape3D();
        this.inField = inField;
        this.params = params;
        dataMappingParams = params.getDataMappingParams();

        int[] dims = inField.getDims();
        int i, k;
        n2 = dims[2];
        n1 = dims[1];
        n0 = dims[0];
        if (needPow2Textures) {
            nn0 = nn1 = nn2 = 0;
            for (i = 0, k = 1; i < 20; i++, k *= 2) {
                if (k >= n0 && nn0 == 0) nn0 = k;
                if (k >= n1 && nn1 == 0) nn1 = k;
                if (k >= n2 && nn2 == 0) nn2 = k;
            }
        } else {
            nn0 = n0;
            nn1 = n1;
            nn2 = n2;
        }
        OpenMaterial mat = new OpenMaterial(new Color3f(0.7f, 0.7f, 0.7f),
                                            new Color3f(0.1f, 0.1f, 0.1f),
                                            new Color3f(1.0f, 1.0f, 1.0f),
                                            new Color3f(0.7f, 0.7f, 0.7f),
                                            .5f, OpenMaterial.AMBIENT_AND_DIFFUSE);


        int nn = n0;
        if (nn < n1) {
            nn = n1;
        }
        if (nn < n2) {
            nn = n2;
        }
        minX = minY = minZ = 0;
        maxX = n0 - 1;
        maxY = n1 - 1;
        maxZ = n2 - 1;

        ap.setCapability(OpenAppearance.ALLOW_TEXTURE_WRITE);
        ta = new TransparencyAttributes();
        ta.setTransparencyMode(TransparencyAttributes.BLENDED);
        ta.setCapability(TransparencyAttributes.ALLOW_VALUE_READ);
        ta.setCapability(TransparencyAttributes.ALLOW_VALUE_WRITE);
        ta.setTransparency(0.5f);
        ap.setTransparencyAttributes(ta);
        ap.setTexture(tx3d);
        ap.setMaterial(mat);
        tr.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
        tr.setAppearance(ap);

        //updateMesh();

        float[][] sourceAffine = inField.getAffine();
        float[] matrix = new float[16];
        for (int l = 0; l < 3; l++) {
            for (int j = 0; j < 4; j++) {
                matrix[4 * l + j] = sourceAffine[j][l];
            }
        }
        for (int j = 12; j < matrix.length; j++)
            matrix[j] = 0;
        matrix[15] = 1;

        //updateTexture();

        tg = new TransformGroup(new Transform3D(matrix));
        tg.addChild(tr);
        this.addChild(tg);
    }

    private void createMeshes() {
        int i0, i1, i2, k;
        float[][] ptsc = new float[2][3];
        float x, y, z, tx, ty, tz;

        ptsc[0][0] = minX;
        ptsc[1][0] = maxX;
        ptsc[0][1] = minY;
        ptsc[1][1] = maxY;
        ptsc[0][2] = minZ;
        ptsc[1][2] = maxZ;
        float[][] tsc = new float[2][3];
        tsc[0][0] = minX / (nn0 - 1.f);
        tsc[1][0] = maxX / (nn0 - 1.f);
        tsc[0][1] = (minY + nn1 - n1) / (nn1 - 1.f);
        tsc[1][1] = (maxY + nn1 - n1) / (nn1 - 1.f);
        tsc[0][2] = minZ / (nn2 - 1.f);
        tsc[1][2] = maxZ / (nn2 - 1.f);

        nvert = 4 * (maxZ - minZ + 1);
        volMeshes[2][0] = new QuadArray(nvert, GeometryArray.COORDINATES
                | GeometryArray.TEXTURE_COORDINATE_3);
        volMeshes[2][1] = new QuadArray(nvert, GeometryArray.COORDINATES
                | GeometryArray.TEXTURE_COORDINATE_3);
        coords = new float[3 * nvert];
        texCoords = new float[3 * nvert];
        for (i2 = minZ, k = 0; i2 <= maxZ; i2++, k += 4) {
            z = i2;
            coords[3 * k] = ptsc[0][0];
            coords[3 * k + 1] = ptsc[0][1];
            coords[3 * k + 2] = z;
            coords[3 * k + 3] = ptsc[1][0];
            coords[3 * k + 4] = ptsc[0][1];
            coords[3 * k + 5] = z;
            coords[3 * k + 6] = ptsc[1][0];
            coords[3 * k + 7] = ptsc[1][1];
            coords[3 * k + 8] = z;
            coords[3 * k + 9] = ptsc[0][0];
            coords[3 * k + 10] = ptsc[1][1];
            coords[3 * k + 11] = z;
            tz = i2 / (nn2 - 1.f);
            texCoords[3 * k] = tsc[0][0];
            texCoords[3 * k + 1] = tsc[0][1];
            texCoords[3 * k + 2] = tz;
            texCoords[3 * k + 3] = tsc[1][0];
            texCoords[3 * k + 4] = tsc[0][1];
            texCoords[3 * k + 5] = tz;
            texCoords[3 * k + 6] = tsc[1][0];
            texCoords[3 * k + 7] = tsc[1][1];
            texCoords[3 * k + 8] = tz;
            texCoords[3 * k + 9] = tsc[0][0];
            texCoords[3 * k + 10] = tsc[1][1];
            texCoords[3 * k + 11] = tz;
        }
        if(VisNow.getOsType() == VisNow.OsType.OS_MAC)
            fixTexCoords();
        volMeshes[2][0].setCoordinates(0, coords);
        volMeshes[2][0].setTextureCoordinates(0, 0, texCoords);
        volMeshes[2][0].setCapability(GeometryArray.ALLOW_TEXCOORD_READ);
        volMeshes[2][0].setCapability(GeometryArray.ALLOW_TEXCOORD_WRITE);
        for (i2 = maxZ, k = 0; i2 >= minZ; i2--, k += 4) {
            z = i2;
            coords[3 * k] = ptsc[0][0];
            coords[3 * k + 1] = ptsc[0][1];
            coords[3 * k + 2] = z;
            coords[3 * k + 3] = ptsc[1][0];
            coords[3 * k + 4] = ptsc[0][1];
            coords[3 * k + 5] = z;
            coords[3 * k + 6] = ptsc[1][0];
            coords[3 * k + 7] = ptsc[1][1];
            coords[3 * k + 8] = z;
            coords[3 * k + 9] = ptsc[0][0];
            coords[3 * k + 10] = ptsc[1][1];
            coords[3 * k + 11] = z;
            tz = i2 / (nn2 - 1.f);
            texCoords[3 * k] = tsc[0][0];
            texCoords[3 * k + 1] = tsc[0][1];
            texCoords[3 * k + 2] = tz;
            texCoords[3 * k + 3] = tsc[1][0];
            texCoords[3 * k + 4] = tsc[0][1];
            texCoords[3 * k + 5] = tz;
            texCoords[3 * k + 6] = tsc[1][0];
            texCoords[3 * k + 7] = tsc[1][1];
            texCoords[3 * k + 8] = tz;
            texCoords[3 * k + 9] = tsc[0][0];
            texCoords[3 * k + 10] = tsc[1][1];
            texCoords[3 * k + 11] = tz;
        }
        if(VisNow.getOsType() == VisNow.OsType.OS_MAC)
            fixTexCoords();
        volMeshes[2][1].setCoordinates(0, coords);
        volMeshes[2][1].setTextureCoordinates(0, 0, texCoords);
        volMeshes[2][1].setCapability(GeometryArray.ALLOW_TEXCOORD_READ);
        volMeshes[2][1].setCapability(GeometryArray.ALLOW_TEXCOORD_WRITE);

        nvert = 4 * (maxY - minY + 1);
        volMeshes[1][0] = new QuadArray(nvert, GeometryArray.COORDINATES
                | GeometryArray.TEXTURE_COORDINATE_3);
        volMeshes[1][1] = new QuadArray(nvert, GeometryArray.COORDINATES
                | GeometryArray.TEXTURE_COORDINATE_3);
        coords = new float[3 * nvert];
        texCoords = new float[3 * nvert];
        for (i1 = minY, k = 0; i1 <= maxY; i1++, k += 4) {
            y = i1;
            coords[3 * k] = ptsc[0][0];
            coords[3 * k + 1] = y;
            coords[3 * k + 2] = ptsc[0][2];
            coords[3 * k + 3] = ptsc[1][0];
            coords[3 * k + 4] = y;
            coords[3 * k + 5] = ptsc[0][2];
            coords[3 * k + 6] = ptsc[1][0];
            coords[3 * k + 7] = y;
            coords[3 * k + 8] = ptsc[1][2];
            coords[3 * k + 9] = ptsc[0][0];
            coords[3 * k + 10] = y;
            coords[3 * k + 11] = ptsc[1][2];
            ty = (i1 + nn1 - n1) / (nn1 - 1.f);
            texCoords[3 * k] = tsc[0][0];
            texCoords[3 * k + 1] = ty;
            texCoords[3 * k + 2] = tsc[0][2];
            texCoords[3 * k + 3] = tsc[1][0];
            texCoords[3 * k + 4] = ty;
            texCoords[3 * k + 5] = tsc[0][2];
            texCoords[3 * k + 6] = tsc[1][0];
            texCoords[3 * k + 7] = ty;
            texCoords[3 * k + 8] = tsc[1][2];
            texCoords[3 * k + 9] = tsc[0][0];
            texCoords[3 * k + 10] = ty;
            texCoords[3 * k + 11] = tsc[1][2];
        }
        if(VisNow.getOsType() == VisNow.OsType.OS_MAC)
            fixTexCoords();
        volMeshes[1][0].setCoordinates(0, coords);
        volMeshes[1][0].setTextureCoordinates(0, 0, texCoords);
        volMeshes[1][0].setCapability(GeometryArray.ALLOW_TEXCOORD_READ);
        volMeshes[1][0].setCapability(GeometryArray.ALLOW_TEXCOORD_WRITE);
        for (i1 = maxY, k = 0; i1 >= minY; i1--, k += 4) {
            y = i1;
            coords[3 * k] = ptsc[0][0];
            coords[3 * k + 1] = y;
            coords[3 * k + 2] = ptsc[0][2];
            coords[3 * k + 3] = ptsc[1][0];
            coords[3 * k + 4] = y;
            coords[3 * k + 5] = ptsc[0][2];
            coords[3 * k + 6] = ptsc[1][0];
            coords[3 * k + 7] = y;
            coords[3 * k + 8] = ptsc[1][2];
            coords[3 * k + 9] = ptsc[0][0];
            coords[3 * k + 10] = y;
            coords[3 * k + 11] = ptsc[1][2];
            ty = (i1 + nn1 - n1) / (nn1 - 1.f);
            texCoords[3 * k] = tsc[0][0];
            texCoords[3 * k + 1] = ty;
            texCoords[3 * k + 2] = tsc[0][2];
            texCoords[3 * k + 3] = tsc[1][0];
            texCoords[3 * k + 4] = ty;
            texCoords[3 * k + 5] = tsc[0][2];
            texCoords[3 * k + 6] = tsc[1][0];
            texCoords[3 * k + 7] = ty;
            texCoords[3 * k + 8] = tsc[1][2];
            texCoords[3 * k + 9] = tsc[0][0];
            texCoords[3 * k + 10] = ty;
            texCoords[3 * k + 11] = tsc[1][2];
        }
        if(VisNow.getOsType() == VisNow.OsType.OS_MAC)
            fixTexCoords();
        volMeshes[1][1].setCoordinates(0, coords);
        volMeshes[1][1].setTextureCoordinates(0, 0, texCoords);
        volMeshes[1][1].setCapability(GeometryArray.ALLOW_TEXCOORD_READ);
        volMeshes[1][1].setCapability(GeometryArray.ALLOW_TEXCOORD_WRITE);

        nvert = 4 * (maxX - minX + 1);
        volMeshes[0][0] = new QuadArray(nvert, GeometryArray.COORDINATES
                | GeometryArray.TEXTURE_COORDINATE_3);
        volMeshes[0][1] = new QuadArray(nvert, GeometryArray.COORDINATES
                | GeometryArray.TEXTURE_COORDINATE_3);
        coords = new float[3 * nvert];
        texCoords = new float[3 * nvert];
        for (i0 = minX, k = 0; i0 <= maxX; i0++, k += 4) {
            x = i0;
            coords[3 * k] = x;
            coords[3 * k + 1] = ptsc[0][1];
            coords[3 * k + 2] = ptsc[0][2];
            coords[3 * k + 3] = x;
            coords[3 * k + 4] = ptsc[1][1];
            coords[3 * k + 5] = ptsc[0][2];
            coords[3 * k + 6] = x;
            coords[3 * k + 7] = ptsc[1][1];
            coords[3 * k + 8] = ptsc[1][2];
            coords[3 * k + 9] = x;
            coords[3 * k + 10] = ptsc[0][1];
            coords[3 * k + 11] = ptsc[1][2];
            tx = i0 / (nn0 - 1.f);
            texCoords[3 * k] = tx;
            texCoords[3 * k + 1] = tsc[0][1];
            texCoords[3 * k + 2] = tsc[0][2];
            texCoords[3 * k + 3] = tx;
            texCoords[3 * k + 4] = tsc[1][1];
            texCoords[3 * k + 5] = tsc[0][2];
            texCoords[3 * k + 6] = tx;
            texCoords[3 * k + 7] = tsc[1][1];
            texCoords[3 * k + 8] = tsc[1][2];
            texCoords[3 * k + 9] = tx;
            texCoords[3 * k + 10] = tsc[0][1];
            texCoords[3 * k + 11] = tsc[1][2];
        }
        if(VisNow.getOsType() == VisNow.OsType.OS_MAC)
            fixTexCoords();
        volMeshes[0][0].setCoordinates(0, coords);
        volMeshes[0][0].setTextureCoordinates(0, 0, texCoords);
        volMeshes[0][0].setCapability(GeometryArray.ALLOW_TEXCOORD_READ);
        volMeshes[0][0].setCapability(GeometryArray.ALLOW_TEXCOORD_WRITE);
        for (i0 = maxX, k = 0; i0 >= minX; i0--, k += 4) {
            x = i0;
            coords[3 * k] = x;
            coords[3 * k + 1] = ptsc[0][1];
            coords[3 * k + 2] = ptsc[0][2];
            coords[3 * k + 3] = x;
            coords[3 * k + 4] = ptsc[1][1];
            coords[3 * k + 5] = ptsc[0][2];
            coords[3 * k + 6] = x;
            coords[3 * k + 7] = ptsc[1][1];
            coords[3 * k + 8] = ptsc[1][2];
            coords[3 * k + 9] = x;
            coords[3 * k + 10] = ptsc[0][1];
            coords[3 * k + 11] = ptsc[1][2];
            tx = i0 / (nn0 - 1.f);
            texCoords[3 * k] = tx;
            texCoords[3 * k + 1] = tsc[0][1];
            texCoords[3 * k + 2] = tsc[0][2];
            texCoords[3 * k + 3] = tx;
            texCoords[3 * k + 4] = tsc[1][1];
            texCoords[3 * k + 5] = tsc[0][2];
            texCoords[3 * k + 6] = tx;
            texCoords[3 * k + 7] = tsc[1][1];
            texCoords[3 * k + 8] = tsc[1][2];
            texCoords[3 * k + 9] = tx;
            texCoords[3 * k + 10] = tsc[0][1];
            texCoords[3 * k + 11] = tsc[1][2];
        }
        if(VisNow.getOsType() == VisNow.OsType.OS_MAC)
            fixTexCoords();
        volMeshes[0][1].setCoordinates(0, coords);
        volMeshes[0][1].setTextureCoordinates(0, 0, texCoords);
        volMeshes[0][1].setCapability(GeometryArray.ALLOW_TEXCOORD_READ);
        volMeshes[0][1].setCapability(GeometryArray.ALLOW_TEXCOORD_WRITE);
    }
    
    private void fixTexCoords() {
        float FIX = 0.01f;
        if(texCoords == null)
            return;
        
        for (int i = 0; i < texCoords.length; i++) {
            if(texCoords[i] == 0.0f) texCoords[i] += FIX;
            if(texCoords[i] == 1.0f) texCoords[i] -= FIX;            
        }        
    }

    private void checkTexImages() {
        if (texImages == null || texImages.length != nn2 || texImages[0] == null || texImages[0].getWidth() != nn0 || texImages[0].getHeight() != nn1) {
            texImages = new BufferedImage[nn2];
            texSlices = new int[nn2][];
            for (int i = 0; i < texImages.length; i++) {
                texImages[i] = new BufferedImage(nn0, nn1, BufferedImage.TYPE_INT_ARGB);
                texSlices[i] = ((DataBufferInt) texImages[i].getRaster().getDataBuffer()).getData();
            }
        }
    }

    private void updateObjectTexture() {
        i3d = new ImageComponent3D(ImageComponent3D.FORMAT_RGBA, texImages, true, true);
        tx3d = new Texture3D(Texture3D.BASE_LEVEL, Texture3D.RGBA, nn0, nn1, nn2);
        tx3d.setCapability(Texture3D.ALLOW_IMAGE_READ);
        tx3d.setCapability(Texture3D.ALLOW_IMAGE_WRITE);        
        tx3d.setMagFilter(Texture3D.BASE_LEVEL_LINEAR);
        tx3d.setMinFilter(Texture3D.BASE_LEVEL_LINEAR);                
        tx3d.setBoundaryModeS(Texture.CLAMP_TO_BOUNDARY);
        tx3d.setBoundaryModeT(Texture.CLAMP_TO_BOUNDARY);
        tx3d.setBoundaryModeR(Texture.CLAMP_TO_BOUNDARY);
        tx3d.setEnable(true);
        tx3d.setImage(0, i3d);
        tr.getAppearance().setTexture(tx3d);
        runtime.gc();
    }

    private class MapColors implements Runnable {

        int nThreads = 1;
        int iThread = 0;
        byte[] byteColorSlice = null;

        public MapColors(int nThreads, int iThread) {
            this.nThreads = nThreads;
            this.iThread = iThread;
            byteColorSlice = new byte[4 * nn0 * nn1];
        }

        @Override
        public void run() {
            int dk = nn2 / nThreads;
            int kstart = iThread * dk + Math.min(iThread, nn2 % nThreads);
            int kend = (iThread + 1) * dk + Math.min(iThread + 1, nn2 % nThreads);
            if (kend > nn2) kend = nn2;
            for (int i = kstart; i < kend; i++) {
                int[] texSlice = texSlices[i];
                ColorMapper.map(inField, dataMappingParams, i * nn0 * nn1, (i + 1) * nn0 * nn1, byteColorSlice);
                for (int j = 0; j < nn0 * nn1; j++)
                    texSlice[j] = (texSlice[j] & 0xff000000) | 
                                 ((byteColorSlice[4 * j] & 0xff) << 16) | 
                                 ((byteColorSlice[4 * j + 1] & 0xff) << 8) | 
                                  (byteColorSlice[4 * j + 2] & 0xff);
                texImages[i].setRGB(0, 0, nn0, nn1, texSlices[i], 0, nn0);
            }
        }
    }

    public void updateTextureColors() {
        checkTexImages();
        Thread[] workThreads = new Thread[nThreads];
        for (int iThread = 0; iThread < nThreads; iThread++) {
            workThreads[iThread] = new Thread(new MapColors(nThreads, iThread));
            workThreads[iThread].start();
        }
        for (int i = 0; i < workThreads.length; i++)
            try {
                workThreads[i].join();
            } catch (Exception e) {
            }
        updateObjectTexture();
    }

    private class MapTransparency implements Runnable {

        int nThreads = 1;
        int iThread = 0;

        public MapTransparency(int nThreads, int iThread) {
            this.nThreads = nThreads;
            this.iThread = iThread;
        }

        @Override
        public void run() {
            int dk = nn2 / nThreads;
            int kstart = iThread * dk + Math.min(iThread, nn2 % nThreads);
            int kend = (iThread + 1) * dk + Math.min(iThread + 1, nn2 % nThreads);
            if (kend > nn2) kend = nn2;
            for (int i = kstart; i < kend; i++) {
                mapTransparency(i * nn0 * nn1, texSlices[i]);
                texImages[i].setRGB(0, 0, nn0, nn1, texSlices[i], 0, nn0);
            }

        }
    }

    public void updateTextureTransparency() {
        checkTexImages();
        Thread[] workThreads = new Thread[nThreads];
        for (int iThread = 0; iThread < nThreads; iThread++) {
            workThreads[iThread] = new Thread(new MapTransparency(nThreads, iThread));
            workThreads[iThread].start();
        }
        for (int i = 0; i < workThreads.length; i++)
            try {
                workThreads[i].join();
            } catch (Exception e) {
            }
        updateObjectTexture();
    }

    private class MapTexture implements Runnable {

        int nThreads = 1;
        int iThread = 0;
        byte[] byteColorSlice = null;

        public MapTexture(int nThreads, int iThread) {
            this.nThreads = nThreads;
            this.iThread = iThread;
            byteColorSlice = new byte[4 * nn0 * nn1];
        }

        @Override
        public void run() {
            int dk = nn2 / nThreads;
            int kstart = iThread * dk + Math.min(iThread, nn2 % nThreads);
            int kend = (iThread + 1) * dk + Math.min(iThread + 1, nn2 % nThreads);
            if (kend > nn2) kend = nn2;
            for (int i = kstart; i < kend; i++) {
                int[] texSlice = texSlices[i];
                ColorMapper.map(inField, dataMappingParams, i * nn0 * nn1, (i + 1) * nn0 * nn1, byteColorSlice);
                for (int j = 0; j < nn0 * nn1; j++)
                    texSlice[j] = (texSlice[j] & 0xff000000) | ((byteColorSlice[4 * j] & 0xff) << 16)
                            | ((byteColorSlice[4 * j + 1] & 0xff) << 8) | (byteColorSlice[4 * j + 2] & 0xff);
                mapTransparency(i * nn0 * nn1, texSlice);
                texImages[i].setRGB(0, 0, nn0, nn1, texSlice, 0, nn0);
            }
        }
    }

    public void updateTexture() {
        checkTexImages();
        Thread[] workThreads = new Thread[nThreads];
        for (int iThread = 0; iThread < nThreads; iThread++) {
            workThreads[iThread] = new Thread(new MapTexture(nThreads, iThread));
            workThreads[iThread].start();
        }
        for (int i = 0; i < workThreads.length; i++)
            try {
                workThreads[i].join();
            } catch (Exception e) {
            }
        updateObjectTexture();
    }

    private class MapTextureFromUV implements Runnable {

        int nThreads = 1;
        int iThread = 0;
        byte[] byteColorSlice = null;

        public MapTextureFromUV(int nThreads, int iThread) {
            this.nThreads = nThreads;
            this.iThread = iThread;
        }

        @Override
        public void run() {
            int dk = nn2 / nThreads;
            int kstart = iThread * dk + Math.min(iThread, nn2 % nThreads);
            int kend = (iThread + 1) * dk + Math.min(iThread + 1, nn2 % nThreads);
            if (kend > nn2) kend = nn2;
            for (int i = kstart; i < kend; i++) {
                int[] texSlice = texSlices[i];
                TextureMapper.map(inField, dataMappingParams, textureImageData,
                                  i * nn0 * nn1, (i + 1) * nn0 * nn1, texSlice);
                texImages[i].setRGB(0, 0, nn0, nn1, texSlice, 0, nn0);
            }
        }
    }

    public void updateTextureFromUV() {
        if (dataMappingParams.getTextureImage() == null
                || inField.getData(dataMappingParams.getUParams().getDataComponent()) == null
                || inField.getData(dataMappingParams.getVParams().getDataComponent()) == null)
            return;
        BufferedImage textureImage = dataMappingParams.getTextureImage();
        int dim = textureImage.getWidth() - 1;
        textureImageData = textureImage.getRGB(0, 0,
                                               textureImage.getWidth(), textureImage.getHeight(),
                                               null, 0, textureImage.getWidth());
        checkTexImages();
        Thread[] workThreads = new Thread[nThreads];
        for (int iThread = 0; iThread < nThreads; iThread++) {
            workThreads[iThread] = new Thread(new MapTextureFromUV(nThreads, iThread));
            workThreads[iThread].start();
        }
        for (int i = 0; i < workThreads.length; i++)
            try {
                workThreads[i].join();
            } catch (Exception e) {
            }
        updateObjectTexture();
    }

    private int[] mapTransparency(int start, int[] colors) {
        if (colors == null || colors.length != nn0 * nn1)
            colors = new int[nn0 * nn1];
        for (int i = 0; i < colors.length; i++)
            colors[i] &= 0xffffff;
        DataArray trData = inField.getData(params.getDataMappingParams().getTransparencyParams().getComponent());
        boolean[] valid = inField.getMask();
        boolean isValidity = inField.isMask();
        if (trData == null)
            for (int i = 0; i < colors.length; i++)
                colors[i] |= 0xff << 24;
        else {
            int[] transp = params.getDataMappingParams().getTransparencyParams().getMap();
            float low = params.getDataMappingParams().getTransparencyParams().getDataLow();
            float up = params.getDataMappingParams().getTransparencyParams().getDataUp();
            if (up <= low) up = low + .01f;
            float d = 255 / (up - low);
            if (trData.getVeclen() == 1)
                switch (trData.getType()) {
                    case DataArray.FIELD_DATA_BYTE:
                        byte[] bData = trData.getBData();
                        for (int i = 0, l = start, m = 0; i < n1; i++, m += nn0 - n0)
                            for (int j = 0; j < n0; j++, l++, m++) {
                                if (isValidity && !valid[l])
                                    continue;
                                int k = (int) (d * ((0xff & bData[l]) - low));
                                if (k < 0) k = 0;
                                if (k > 255) k = 255;
                                colors[m] |= (0xff & transp[k]) << 24;
                            }
                        break;
                    case DataArray.FIELD_DATA_SHORT:
                        short[] sData = trData.getSData();
                        for (int i = 0, l = start, m = 0; i < n1; i++, m += nn0 - n0)
                            for (int j = 0; j < n0; j++, l++, m++) {
                                if (isValidity && !valid[l])
                                    continue;
                                int k = (int) (d * (sData[l] - low));
                                if (k < 0) k = 0;
                                if (k > 255) k = 255;
                                colors[m] |= (0xff & transp[k]) << 24;
                            }
                        break;
                    case DataArray.FIELD_DATA_INT:
                        int[] iData = trData.getIData();
                        for (int i = 0, l = start, m = 0; i < n1; i++, m += nn0 - n0)
                            for (int j = 0; j < n0; j++, l++, m++) {
                                if (isValidity && !valid[l])
                                    continue;
                                int k = (int) (d * (iData[l] - low));
                                if (k < 0) k = 0;
                                if (k > 255) k = 255;
                                colors[m] |= (0xff & transp[k]) << 24;
                            }
                        break;
                    case DataArray.FIELD_DATA_FLOAT:
                        float[] fData = trData.getFData();
                        for (int i = 0, l = start, m = 0; i < n1; i++, m += nn0 - n0)
                            for (int j = 0; j < n0; j++, l++, m++) {
                                if (isValidity && !valid[l])
                                    continue;
                                int k = (int) (d * (fData[l] - low));
                                if (k < 0) k = 0;
                                if (k > 255) k = 255;
                                colors[m] |= (0xff & transp[k]) << 24;
                            }
                        break;
                    case DataArray.FIELD_DATA_DOUBLE:
                        double[] dData = trData.getDData();
                        for (int i = 0, l = start, m = 0; i < n1; i++, m += nn0 - n0)
                            for (int j = 0; j < n0; j++, l++, m++) {
                                if (isValidity && !valid[l])
                                    continue;
                                int k = (int) (d * (dData[l] - low));
                                if (k < 0) k = 0;
                                if (k > 255) k = 255;
                                colors[m] |= (0xff & transp[k]) << 24;
                            }
                        break;
                }
            else {
                double v;
                d = 255 / up;
                int vl = trData.getVeclen();
                switch (trData.getType()) {
                    case DataArray.FIELD_DATA_BYTE:
                        byte[] bData = trData.getBData();
                        for (int i = 0, l = start, m = 0; i < n1; i++, m += nn0 - n0)
                            for (int j = 0; j < n0; j++, l++, m++) {
                                if (isValidity && !valid[l])
                                    continue;
                                v = 0;
                                for (int p = 0, k = vl * l; p < vl; p++, k++)
                                    v += (0xff & bData[k]) * (0xff & bData[k]);
                                int k = (int) (d * Math.sqrt(v));
                                if (k < 0) k = 0;
                                if (k > 255) k = 255;
                                colors[m] |= (0xff & transp[k]) << 24;
                            }
                        break;
                    case DataArray.FIELD_DATA_SHORT:
                        short[] sData = trData.getSData();
                        for (int i = 0, l = start, m = 0; i < n1; i++, m += nn0 - n0)
                            for (int j = 0; j < n0; j++, l++, m++) {
                                if (isValidity && !valid[l])
                                    continue;
                                v = 0;
                                for (int p = 0, k = vl * l; p < vl; p++, k++)
                                    v += sData[k] * sData[k];
                                int k = (int) (d * Math.sqrt(v));
                                if (k < 0) k = 0;
                                if (k > 255) k = 255;
                                colors[m] |= (0xff & transp[k]) << 24;
                            }
                        break;
                    case DataArray.FIELD_DATA_INT:
                        int[] iData = trData.getIData();
                        for (int i = 0, l = start, m = 0; i < n1; i++, m += nn0 - n0)
                            for (int j = 0; j < n0; j++, l++, m++) {
                                if (isValidity && !valid[l])
                                    continue;
                                v = 0;
                                for (int p = 0, k = vl * l; p < vl; p++, k++)
                                    v += iData[k] * iData[k];
                                int k = (int) (d * Math.sqrt(v));
                                if (k < 0) k = 0;
                                if (k > 255) k = 255;
                                colors[m] |= (0xff & transp[k]) << 24;
                            }
                        break;
                    case DataArray.FIELD_DATA_FLOAT:
                        float[] fData = trData.getFData();
                        for (int i = 0, l = start, m = 0; i < n1; i++, m += nn0 - n0)
                            for (int j = 0; j < n0; j++, l++, m++) {
                                if (isValidity && !valid[l])
                                    continue;
                                v = 0;
                                for (int p = 0, k = vl * l; p < vl; p++, k++)
                                    v += fData[k] * fData[k];
                                int k = (int) (d * Math.sqrt(v));
                                if (k < 0) k = 0;
                                if (k > 255) k = 255;
                                colors[m] |= (0xff & transp[k]) << 24;
                            }
                        break;
                    case DataArray.FIELD_DATA_DOUBLE:
                        double[] dData = trData.getDData();
                        for (int i = 0, l = start, m = 0; i < n1; i++, m += nn0 - n0)
                            for (int j = 0; j < n0; j++, l++, m++) {
                                if (isValidity && !valid[l])
                                    continue;
                                v = 0;
                                for (int p = 0, k = vl * l; p < vl; p++, k++)
                                    v += dData[k] * dData[k];
                                int k = (int) (d * Math.sqrt(v));
                                if (k < 0) k = 0;
                                if (k > 255) k = 255;
                                colors[m] |= (0xff & transp[k]) << 24;
                            }
                        break;
                }
            }
        }
        return colors;
    }

    public int getDir() {
        return axis;
    }

    public void setDir(int axis, boolean dir) {
        this.axis = axis;
        this.dir = dir;
        if (dir) {
            tr.setGeometry(volMeshes[axis][1]);
        } else {
            tr.setGeometry(volMeshes[axis][0]);
        }
    }

    public void updateMesh() {
        createMeshes();
        if (dir) {
            tr.setGeometry(volMeshes[axis][1]);
        } else {
            tr.setGeometry(volMeshes[axis][0]);
        }
    }

    public void setCrop(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.minZ = minZ;
        this.maxZ = maxZ;
        createMeshes();
        if (dir) {
            tr.setGeometry(volMeshes[axis][1]);
        } else {
            tr.setGeometry(volMeshes[axis][0]);
        }
    }

    public void setCrop(int[] low, int[] up) {
        this.minX = low[0];
        this.maxX = up[0];
        this.minY = low[1];
        this.maxY = up[1];
        this.minZ = low[2];
        this.maxZ = up[2];
        createMeshes();
        if (dir) {
            tr.setGeometry(volMeshes[axis][1]);
        } else {
            tr.setGeometry(volMeshes[axis][0]);
        }
    }

    public void setTranspComp(int c) {
        if (c < 0 || c >= inField.getNData()) {
            return;
        }
        updateTexture();
        ap.setTexture(tx3d);
    }
    private transient FloatValueModificationListener statusListener = null;

    public void clearFloatValueModificationListener() {
        statusListener = null;
    }

    public void addFloatValueModificationListener(FloatValueModificationListener listener) {
        if (statusListener == null) {
            this.statusListener = listener;
        } else {
            System.out.println("" + this + ": only one status listener can be added");
        }
    }

    private void fireStatusChanged(float status) {
        FloatValueModificationEvent e = new FloatValueModificationEvent(this, status, true);
        if (statusListener != null) {
            statusListener.floatValueChanged(e);
        }
    }

    public TransformGroup getTg() {
        return tg;
    }

    private int[] mapColorTransparency(int start, int[] colors) {
        int nColors = ColorMapManager.SAMPLING;
        int mColors = 0;
        ColorMap2D colorMap2D = null;
        int[] texData = null;
        if (colors == null || colors.length != nn0 * nn1)
            colors = new int[nn0 * nn1];
        for (int i = 0; i < colors.length; i++)
            colors[i] = 0;
        DataArray uData = inField.getData(dataMappingParams.getUParams().getDataComponent());
        DataArray vData = inField.getData(dataMappingParams.getVParams().getDataComponent());
        if (!dataMappingParams.isUseColormap2D() && dataMappingParams.getTextureImage() == null
                || uData == null || vData == null)
            return colors;
        if (dataMappingParams.isUseColormap2D()) {
            colorMap2D = (ColorMap2D) ColorMapManager.getInstance().getColorMap2DListModel().getElementAt(dataMappingParams.getColorMap2DIndex());
            texData = colorMap2D.getARGBColorTable();
            nColors = ColorMapManager.SAMPLING;
            mColors = ColorMapManager.SAMPLING;
        } else {
            BufferedImage textureImage = dataMappingParams.getTextureImage();
            mColors = textureImage.getHeight();
            nColors = textureImage.getWidth();
            texData = textureImage.getRGB(0, 0, nColors, mColors, null, 0, 1);
        }
        float low = uData.getMinv();
        float d = nColors / (uData.getMaxv() - low);
        if (uData.getVeclen() == 1)
            switch (uData.getType()) {
                case DataArray.FIELD_DATA_BYTE:
                    byte[] bData = uData.getBData();
                    for (int i = 0, l = start, m = 0; i < n1; i++, m += nn0 - n0)
                        for (int j = 0; j < n0; j++, l++, m++) {
                            int k = (int) (d * ((0xff & bData[l]) - low));
                            if (k < 0)
                                k = 0;
                            if (k > nColors)
                                k = nColors;
                            colors[m] = k;
                        }
                    break;
                case DataArray.FIELD_DATA_SHORT:
                    short[] sData = uData.getSData();
                    for (int i = 0, l = start, m = 0; i < n1; i++, m += nn0 - n0)
                        for (int j = 0; j < n0; j++, l++, m++) {
                            int k = (int) (d * (sData[l] - low));
                            if (k < 0)
                                k = 0;
                            if (k > nColors)
                                k = nColors;
                            colors[m] = k;
                        }
                    break;
                case DataArray.FIELD_DATA_INT:
                    int[] iData = uData.getIData();
                    for (int i = 0, l = start, m = 0; i < n1; i++, m += nn0 - n0)
                        for (int j = 0; j < n0; j++, l++, m++) {
                            int k = (int) (d * (iData[l] - low));
                            if (k < 0)
                                k = 0;
                            if (k > nColors)
                                k = nColors;
                            colors[m] = k;
                        }
                    break;
                case DataArray.FIELD_DATA_FLOAT:
                    float[] fData = uData.getFData();
                    for (int i = 0, l = start, m = 0; i < n1; i++, m += nn0 - n0)
                        for (int j = 0; j < n0; j++, l++, m++) {
                            int k = (int) (d * (fData[l] - low));
                            if (k < 0)
                                k = 0;
                            if (k > nColors)
                                k = nColors;
                            colors[m] = k;
                        }
                    break;
                case DataArray.FIELD_DATA_DOUBLE:
                    double[] dData = uData.getDData();
                    for (int i = 0, l = start, m = 0; i < n1; i++, m += nn0 - n0)
                        for (int j = 0; j < n0; j++, l++, m++) {
                            int k = (int) (d * (dData[l] - low));
                            if (k < 0)
                                k = 0;
                            if (k > nColors)
                                k = nColors;
                            colors[m] = k;
                        }
                    break;
            }
        else {
            double v;
            int vl = uData.getVeclen();
            switch (uData.getType()) {
                case DataArray.FIELD_DATA_BYTE:
                    byte[] bData = uData.getBData();
                    for (int i = 0, l = start, m = 0; i < n1; i++, m += nn0 - n0)
                        for (int j = 0; j < n0; j++, l++, m++) {
                            v = 0;
                            for (int p = 0, k = vl * l; p < vl; p++, k++)
                                v += (0xff & bData[k]) * (0xff & bData[k]);
                            int k = (int) (d * (Math.sqrt(v) - low));
                            if (k < 0)
                                k = 0;
                            if (k > nColors)
                                k = nColors;
                            colors[m] = k;
                        }
                    break;
                case DataArray.FIELD_DATA_SHORT:
                    short[] sData = uData.getSData();
                    for (int i = 0, l = start, m = 0; i < n1; i++, m += nn0 - n0)
                        for (int j = 0; j < n0; j++, l++, m++) {
                            v = 0;
                            for (int p = 0, k = vl * l; p < vl; p++, k++)
                                v += sData[k] * sData[k];
                            int k = (int) (d * (Math.sqrt(v) - low));
                            if (k < 0)
                                k = 0;
                            if (k > nColors)
                                k = nColors;
                            colors[m] = k;
                        }
                    break;
                case DataArray.FIELD_DATA_INT:
                    int[] iData = uData.getIData();
                    for (int i = 0, l = start, m = 0; i < n1; i++, m += nn0 - n0)
                        for (int j = 0; j < n0; j++, l++, m++) {
                            v = 0;
                            for (int p = 0, k = vl * l; p < vl; p++, k++)
                                v += iData[k] * iData[k];
                            int k = (int) (d * (Math.sqrt(v) - low));
                            if (k < 0)
                                k = 0;
                            if (k > nColors)
                                k = nColors;
                            colors[m] = k;
                        }
                    break;
                case DataArray.FIELD_DATA_FLOAT:
                    float[] fData = uData.getFData();
                    for (int i = 0, l = start, m = 0; i < n1; i++, m += nn0 - n0)
                        for (int j = 0; j < n0; j++, l++, m++) {
                            v = 0;
                            for (int p = 0, k = vl * l; p < vl; p++, k++)
                                v += fData[k] * fData[k];
                            int k = (int) (d * (Math.sqrt(v) - low));
                            if (k < 0)
                                k = 0;
                            if (k > nColors)
                                k = nColors;
                            colors[m] = k;
                        }
                    break;
                case DataArray.FIELD_DATA_DOUBLE:
                    double[] dData = uData.getDData();
                    for (int i = 0, l = start, m = 0; i < n1; i++, m += nn0 - n0)
                        for (int j = 0; j < n0; j++, l++, m++) {
                            v = 0;
                            for (int p = 0, k = vl * l; p < vl; p++, k++)
                                v += dData[k] * dData[k];
                            int k = (int) (d * (Math.sqrt(v) - low));
                            if (k < 0)
                                k = 0;
                            if (k > nColors)
                                k = nColors;
                            colors[m] = k;
                        }
                    break;
            }
        }

        low = vData.getMinv();
        d = mColors / (vData.getMaxv() - low);
        if (vData.getVeclen() == 1)
            switch (vData.getType()) {
                case DataArray.FIELD_DATA_BYTE:
                    byte[] bData = vData.getBData();
                    for (int i = 0, l = start, m = 0; i < n1; i++, m += nn0 - n0)
                        for (int j = 0; j < n0; j++, l++, m++) {
                            int k = (int) (d * ((0xff & bData[l]) - low));
                            if (k < 0)
                                k = 0;
                            if (k > mColors)
                                k = mColors;
                            colors[m] = texData[k * nColors + colors[m]];
                        }
                    break;
                case DataArray.FIELD_DATA_SHORT:
                    short[] sData = vData.getSData();
                    for (int i = 0, l = start, m = 0; i < n1; i++, m += nn0 - n0)
                        for (int j = 0; j < n0; j++, l++, m++) {
                            int k = (int) (d * (sData[l] - low));
                            if (k < 0)
                                k = 0;
                            if (k > mColors)
                                k = mColors;
                            colors[m] = texData[k * nColors + colors[m]];
                        }
                    break;
                case DataArray.FIELD_DATA_INT:
                    int[] iData = vData.getIData();
                    for (int i = 0, l = start, m = 0; i < n1; i++, m += nn0 - n0)
                        for (int j = 0; j < n0; j++, l++, m++) {
                            int k = (int) (d * (iData[l] - low));
                            if (k < 0)
                                k = 0;
                            if (k > mColors)
                                k = mColors;
                            colors[m] = texData[k * nColors + colors[m]];
                        }
                    break;
                case DataArray.FIELD_DATA_FLOAT:
                    float[] fData = vData.getFData();
                    for (int i = 0, l = start, m = 0; i < n1; i++, m += nn0 - n0)
                        for (int j = 0; j < n0; j++, l++, m++) {
                            int k = (int) (d * (fData[l] - low));
                            if (k < 0)
                                k = 0;
                            if (k > mColors)
                                k = mColors;
                            colors[m] = texData[k * nColors + colors[m]];
                        }
                    break;
                case DataArray.FIELD_DATA_DOUBLE:
                    double[] dData = vData.getDData();
                    for (int i = 0, l = start, m = 0; i < n1; i++, m += nn0 - n0)
                        for (int j = 0; j < n0; j++, l++, m++) {
                            int k = (int) (d * (dData[l] - low));
                            if (k < 0)
                                k = 0;
                            if (k > mColors)
                                k = mColors;
                            colors[m] = texData[k * nColors + colors[m]];
                        }
                    break;
            }
        else {
            double v;
            int vl = vData.getVeclen();
            switch (vData.getType()) {
                case DataArray.FIELD_DATA_BYTE:
                    byte[] bData = vData.getBData();
                    for (int i = 0, l = start, m = 0; i < n1; i++, m += nn0 - n0)
                        for (int j = 0; j < n0; j++, l++, m++) {
                            v = 0;
                            for (int p = 0, k = vl * l; p < vl; p++, k++)
                                v += (0xff & bData[k]) * (0xff & bData[k]);
                            int k = (int) (d * (Math.sqrt(v) - low));
                            if (k < 0)
                                k = 0;
                            if (k > mColors)
                                k = mColors;
                            colors[m] = texData[k * nColors + colors[m]];
                        }
                    break;
                case DataArray.FIELD_DATA_SHORT:
                    short[] sData = vData.getSData();
                    for (int i = 0, l = start, m = 0; i < n1; i++, m += nn0 - n0)
                        for (int j = 0; j < n0; j++, l++, m++) {
                            v = 0;
                            for (int p = 0, k = vl * l; p < vl; p++, k++)
                                v += sData[k] * sData[k];
                            int k = (int) (d * (Math.sqrt(v) - low));
                            if (k < 0)
                                k = 0;
                            if (k > mColors)
                                k = mColors;
                            colors[m] = texData[k * nColors + colors[m]];
                        }
                    break;
                case DataArray.FIELD_DATA_INT:
                    int[] iData = vData.getIData();
                    for (int i = 0, l = start, m = 0; i < n1; i++, m += nn0 - n0)
                        for (int j = 0; j < n0; j++, l++, m++) {
                            v = 0;
                            for (int p = 0, k = vl * l; p < vl; p++, k++)
                                v += iData[k] * iData[k];
                            int k = (int) (d * (Math.sqrt(v) - low));
                            if (k < 0)
                                k = 0;
                            if (k > mColors)
                                k = mColors;
                            colors[m] = texData[k * nColors + colors[m]];
                        }
                    break;
                case DataArray.FIELD_DATA_FLOAT:
                    float[] fData = vData.getFData();
                    for (int i = 0, l = start, m = 0; i < n1; i++, m += nn0 - n0)
                        for (int j = 0; j < n0; j++, l++, m++) {
                            v = 0;
                            for (int p = 0, k = vl * l; p < vl; p++, k++)
                                v += fData[k] * fData[k];
                            int k = (int) (d * (Math.sqrt(v) - low));
                            if (k < 0)
                                k = 0;
                            if (k > mColors)
                                k = mColors;
                            colors[m] = texData[k * nColors + colors[m]];
                        }
                    break;
                case DataArray.FIELD_DATA_DOUBLE:
                    double[] dData = vData.getDData();
                    for (int i = 0, l = start, m = 0; i < n1; i++, m += nn0 - n0)
                        for (int j = 0; j < n0; j++, l++, m++) {
                            v = 0;
                            for (int p = 0, k = vl * l; p < vl; p++, k++)
                                v += dData[k] * dData[k];
                            int k = (int) (d * (Math.sqrt(v) - low));
                            if (k < 0)
                                k = 0;
                            if (k > mColors)
                                k = mColors;
                            colors[m] = texData[k * nColors + colors[m]];
                        }
                    break;
            }
        }
        return colors;
    }
}
