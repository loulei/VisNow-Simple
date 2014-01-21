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

import javax.media.j3d.GeometryArray;
import javax.media.j3d.IndexedTriangleArray;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.geometries.geometryTemplates.Glyph;
import pl.edu.icm.visnow.geometries.geometryTemplates.Templates.TubeTemplate;
import pl.edu.icm.visnow.geometries.objects.generics.OpenAppearance;
import pl.edu.icm.visnow.geometries.objects.generics.OpenBranchGroup;
import pl.edu.icm.visnow.geometries.objects.generics.OpenColoringAttributes;
import pl.edu.icm.visnow.geometries.objects.generics.OpenLineAttributes;
import pl.edu.icm.visnow.geometries.objects.generics.OpenMaterial;
import pl.edu.icm.visnow.geometries.objects.generics.OpenShape3D;
import pl.edu.icm.visnow.geometries.objects.generics.OpenTransparencyAttributes;

/**
 *
 * @author vis
 */
public class Plane3D {
    private float[] point = new float[3];
    private float[] vect = new float[3];

    private float[][] base = new float[2][3];

    private float[][] extents = new float[2][3];

    private float[][] baseExtents = null;


    private RegularField field = null;
    private OpenBranchGroup outObject = new OpenBranchGroup("Plane3d");
    private OpenAppearance ap = new OpenAppearance();
    private OpenTransparencyAttributes objTrans =
           new OpenTransparencyAttributes(TransparencyAttributes.NONE,0.f);
    private OpenMaterial mat = new OpenMaterial(new Color3f(0.1f,0.1f,0.1f),
           new Color3f(0.f,0.f,0.f),
           new Color3f(0.5f,0.5f,0.5f),
           new Color3f(0.7f,0.5f,0.1f),
           3.f, OpenMaterial.AMBIENT_AND_DIFFUSE);
    private PolygonAttributes pattr = new PolygonAttributes(
           PolygonAttributes.POLYGON_FILL,
           PolygonAttributes.CULL_NONE, 0.f, true);
    private OpenLineAttributes la = new OpenLineAttributes();
    private OpenColoringAttributes ca = new OpenColoringAttributes();
    private OpenShape3D outShape = new OpenShape3D();

    public Plane3D(float[] p, float[] v, RegularField field) {
        this(p, v, field, true);
    }

    public Plane3D(float[] p, float[] v, RegularField field, boolean update3DObject) {
        this.field = field;

        mat.setShininess(15.f);
        pattr.setBackFaceNormalFlip(true);
        ap.setTransparencyAttributes(objTrans);
        ap.setPolygonAttributes(pattr);
        ap.setMaterial(mat);
        ap.setLineAttributes(la);
        ap.setColoringAttributes(ca);
        outShape.setAppearance(ap);
        outObject.addChild(outShape);



        for (int i = 0; i < 3; i++) {
            vect[i] = v[i];
            point[i] = p[i];
        }
        float vLen = 0;
        for (int i = 0; i < 3; i++) {
            vLen += vect[i]*vect[i];
        }
        vLen = (float) Math.sqrt(vLen);
        for (int i = 0; i < 3; i++) {
            vect[i] /= vLen;
        }

        calculateBase();
        calculateBaseExtents();
        if(update3DObject)
            update3DObject();
    }

    public void setPlaneParams(float[] p, float[] v) {
        for (int i = 0; i < 3; i++) {
            vect[i] = v[i];
            point[i] = p[i];
        }
        float vLen = 0;
        for (int i = 0; i < 3; i++) {
            vLen += vect[i]*vect[i];
        }
        vLen = (float) Math.sqrt(vLen);
        for (int i = 0; i < 3; i++) {
            vect[i] /= vLen;
        }

        calculateBase();
        calculateBaseExtents();
        update3DObject();
    }

    private void calculateBase() {
        float[][] tmp = new float[3][3];
        float[] tmpNorm = new float[3];


        float[][] cartBase = {{1.0f,0.0f,0.0f},
                            {0.0f,1.0f,0.0f},
                            {0.0f,0.0f,1.0f}
                            };
        float c;

        for (int i = 0; i < 3; i++) {
            c = (vect[0]*cartBase[i][0] + vect[1]*cartBase[i][1] + vect[2]*cartBase[i][2])/(vect[0]*vect[0] + vect[1]*vect[1] + vect[2]*vect[2]);
            tmpNorm[i] = 0.0f;
            for (int j = 0; j < 3; j++) {
                tmp[i][j] = cartBase[i][j] - vect[j]*c;
                tmpNorm[i] += tmp[i][j]*tmp[i][j];
            }
            tmpNorm[i] = (float)Math.sqrt(tmpNorm[i]);
        }

        float max = Math.max(tmpNorm[0], Math.max(tmpNorm[1], tmpNorm[2]));
        int maxi = 0;
        for (int i = 0; i < 3; i++) {
            if(tmpNorm[i] == max) {
                maxi = i;
                break;
            }
        }

        base[0] = tmp[maxi];
        base[1][0] = vect[2]*base[0][1] - vect[1]*base[0][2];
        base[1][1] = vect[0]*base[0][2] - vect[2]*base[0][0];
        base[1][2] = vect[1]*base[0][0] - vect[0]*base[0][1];

    }

    public float[][] getBase() {
        return base;
    }

    public float[][] getExtents(int[] dims, float[][] affine) {
        float[][] cornerPoints = new float[8][3];
        for (int i = 0; i < 3; i++) {
            cornerPoints[0][i] = affine[3][i];
            cornerPoints[1][i] = affine[3][i] + (dims[0]-1)*affine[0][i];
            cornerPoints[2][i] = affine[3][i] + (dims[0]-1)*affine[0][i] + (dims[1]-1)*affine[1][i];
            cornerPoints[3][i] = affine[3][i] + (dims[1]-1)*affine[1][i];
            cornerPoints[4][i] = affine[3][i] + (dims[2]-1)*affine[2][i];
            cornerPoints[5][i] = affine[3][i] + (dims[0]-1)*affine[0][i] + (dims[2]-1)*affine[2][i];
            cornerPoints[6][i] = affine[3][i] + (dims[0]-1)*affine[0][i] + (dims[1]-1)*affine[1][i] + (dims[2]-1)*affine[2][i];
            cornerPoints[7][i] = affine[3][i] + (dims[1]-1)*affine[1][i] + (dims[2]-1)*affine[2][i];
        }
        
        float[] edgePointsCoordinates = calculatePlaneCutCoords(cornerPoints);
        int cubeNumber = calculateCubeNumber(cornerPoints);
        int[] triangulation = cube_triangulation[cubeNumber];

        if(triangulation == null || triangulation.length == 0)
            return null;

        float[][] outArr = new float[2][3];

        outArr[0][0] = edgePointsCoordinates[triangulation[0]*3];
        outArr[0][1] = edgePointsCoordinates[triangulation[0]*3+1];
        outArr[0][2] = edgePointsCoordinates[triangulation[0]*3+2];
        outArr[1][0] = edgePointsCoordinates[triangulation[0]*3];
        outArr[1][1] = edgePointsCoordinates[triangulation[0]*3+1];
        outArr[1][2] = edgePointsCoordinates[triangulation[0]*3+2];

        for (int i = 1; i < triangulation.length; i++) {
            if(edgePointsCoordinates[triangulation[i]*3] < outArr[0][0])
                outArr[0][0] = edgePointsCoordinates[triangulation[i]*3];
            if(edgePointsCoordinates[triangulation[i]*3 + 1] < outArr[0][1])
                outArr[0][1] = edgePointsCoordinates[triangulation[i]*3 + 1];
            if(edgePointsCoordinates[triangulation[i]*3 + 2] < outArr[0][2])
                outArr[0][2] = edgePointsCoordinates[triangulation[i]*3 + 2];

            if(edgePointsCoordinates[triangulation[i]*3] > outArr[1][0])
                outArr[1][0] = edgePointsCoordinates[triangulation[i]*3];
            if(edgePointsCoordinates[triangulation[i]*3 + 1] > outArr[1][1])
                outArr[1][1] = edgePointsCoordinates[triangulation[i]*3 + 1];
            if(edgePointsCoordinates[triangulation[i]*3 + 2] > outArr[1][2])
                outArr[1][2] = edgePointsCoordinates[triangulation[i]*3 + 2];
        }

        return outArr;
    }

    public float[][] getBaseExtents() {
        return baseExtents;
    }


    public void calculateBaseExtents() {
        if(field == null) {
            baseExtents = null;
            return;
        }

        baseExtents = new float[2][3];
        
        float[][] cornerPoints = new float[8][3];
        float[][] affine = field.getAffine();
        int[] dims = field.getDims();
        for (int i = 0; i < 3; i++) {
            cornerPoints[0][i] = affine[3][i];
            cornerPoints[1][i] = affine[3][i] + (dims[0]-1)*affine[0][i];
            cornerPoints[2][i] = affine[3][i] + (dims[0]-1)*affine[0][i] + (dims[1]-1)*affine[1][i];
            cornerPoints[3][i] = affine[3][i] + (dims[1]-1)*affine[1][i];
            cornerPoints[4][i] = affine[3][i] + (dims[2]-1)*affine[2][i];
            cornerPoints[5][i] = affine[3][i] + (dims[0]-1)*affine[0][i] + (dims[2]-1)*affine[2][i];
            cornerPoints[6][i] = affine[3][i] + (dims[0]-1)*affine[0][i] + (dims[1]-1)*affine[1][i] + (dims[2]-1)*affine[2][i];
            cornerPoints[7][i] = affine[3][i] + (dims[1]-1)*affine[1][i] + (dims[2]-1)*affine[2][i];
        }

        float[] edgePointsCoordinates = calculatePlaneCutCoords(cornerPoints);
        int cubeNumber = calculateCubeNumber(cornerPoints);
        int[] triangulation = cube_triangulation[cubeNumber];

        if(triangulation == null || triangulation.length == 0)
            return;

        boolean[] uniq = new boolean[12];
        for (int i = 0; i < uniq.length; i++) {
            uniq[i] = false;
        }
        for (int i = 0; i < triangulation.length; i++) {
            uniq[triangulation[i]] = true;
        }
        int n = 0;
        for (int i = 0; i < uniq.length; i++) {
            if(uniq[i])
                n++;
        }

        triangulation = new int[n];
        for (int i = 0, j = 0; i < uniq.length; i++) {
            if(uniq[i])
                triangulation[j++] = i;
        }


        float[][] tmp = new float[triangulation.length][2];
        for (int i = 0; i < triangulation.length; i++) {
            float[] p = new float[3];
            p[0] = edgePointsCoordinates[3*triangulation[i]];
            p[1] = edgePointsCoordinates[3*triangulation[i]+1];
            p[2] = edgePointsCoordinates[3*triangulation[i]+2];
            tmp[i] = pointInBase(base, point, p);
            if(tmp[i] == null) {
                return;
            }
        }

        float[][] tmp2 = new float[2][2];
        tmp2[0][0] = tmp[0][0];
        tmp2[0][1] = tmp[0][1];
        tmp2[1][0] = tmp[0][0];
        tmp2[1][1] = tmp[0][1];

        for (int i = 1; i < triangulation.length; i++) {
            if(tmp[i][0] < tmp2[0][0])
                tmp2[0][0] = tmp[i][0];
            if(tmp[i][1] < tmp2[0][1])
                tmp2[0][1] = tmp[i][1];

            if(tmp[i][0] > tmp2[1][0])
                tmp2[1][0] = tmp[i][0];
            if(tmp[i][1] > tmp2[1][1])
                tmp2[1][1] = tmp[i][1];
        }

        for (int i = 0; i < 3; i++) {
            baseExtents[0][i] = point[i] + tmp2[0][0]*base[0][i] + tmp2[0][1]*base[1][i];
            baseExtents[1][i] = point[i] + tmp2[1][0]*base[0][i] + tmp2[1][1]*base[1][i];
        }
    }

    public int[] checkCorners2D(float[][] cornerPoints) {
        if(cornerPoints == null)
            return null;

        int[] out = new int[4];
        float value;
        for (int i = 0; i < 4; i++) {
            value = 0;
            for (int j = 0; j < 3; j++) {
                value += vect[j]*(cornerPoints[i][j]-point[j]);
            }
            if(value < 0.0f)
                out[i] = -1;
            else if(value >= 0)
                out[i] = 1;
        }
        return out;
    }


    public float getPlaneCoord2D(int axis0, float point0, int axis1, float point1) {
        float result = 0.0f;

        int myAxis;
        if(axis0 == 0 && axis1 == 1 || axis0 == 1 && axis1 == 0)
            myAxis = 2;
        else if(axis0 == 0 && axis1 == 2 || axis0 == 2 && axis1 == 0)
            myAxis = 1;
        else
            myAxis = 0;


        result -= (vect[axis0]*(point0-point[axis0]) + vect[axis1]*(point1-point[axis1]));
        result /= vect[myAxis];
        result += point[myAxis];
        return result;
    }
    

    public int[] checkCorners3D(float[][] cornerPoints) {
        if(cornerPoints == null)
            return null;

        int[] out = new int[8];
        float value;
        for (int i = 0; i < 8; i++) {
            value = 0;
            for (int j = 0; j < 3; j++) {
                value += vect[j]*(cornerPoints[i][j]-point[j]);
            }
            if(value < 0.0f) {
                out[7-i] = -1;
            } else if(value >= 0) {
                out[7-i] = 1;
            }
        }
        return out;
    }

    private int calculateCubeNumber(float[][] cornerPoints) {
        if(cornerPoints == null)
            return 0;

        int[] corners = checkCorners3D(cornerPoints);

        int out = 0;
        for (int i = 0; i < 8; i++) {
            if(corners[7-i] == 1) {
                out += Math.pow(2, i);
            }
        }
        return out;
    }

    private int[][] edgePoints = {
            {0,1},
            {3,2},
            {4,5},
            {7,6},
            {0,3},
            {1,2},
            {4,7},
            {5,6},
            {0,4},
            {1,5},
            {3,7},
            {2,6}
        };

    private float[] calculatePlaneCutCoords(float[][] cornerPoints) {
        float[] outArr = new float[12*3];
        float[] tmp;
        for (int i = 0; i < 12; i++) {
            tmp = new float[3];
            tmp = calculateEdgeCutCoords(cornerPoints[edgePoints[i][0]], cornerPoints[edgePoints[i][1]]);
            outArr[i*3] = tmp[0];
            outArr[i*3+1] = tmp[1];
            outArr[i*3+2] = tmp[2];
        }
        return outArr;
    }

    private float[] calculateEdgeCutCoords(float[] p1, float[] p2) {
        float[] outArr = new float[3];
        float[] u = new float[3];
        for (int i = 0; i < 3; i++) {
            u[i] = p2[i] - p1[i];
        }
        float a,b;
        a = vect[0]*u[0] + vect[1]*u[1] + vect[2]*u[2];
        if(a == 0.0f) {
            outArr[0] = 0;
            outArr[1] = 0;
            outArr[2] = 0;

            //System.tmp2.println("none");
            return outArr;
        }

        b = vect[0]*p1[0] + vect[1]*p1[1] + vect[2]*p1[2] - vect[0]*point[0] - vect[1]*point[1] - vect[2]*point[2];
        for (int i = 0; i < 3; i++) {
            outArr[i] = p1[i] - u[i]*b/a;
        }
        //System.tmp2.println("result = ("+tmp2[0]+","+tmp2[1]+","+tmp2[2]+")");
        return outArr;
    }

    public OpenBranchGroup getBoundedPlane3D() {
        return outObject;
    }

    public void update3DObject() {
        if(field == null) {
            return;
        }

        int[] dims = field.getDims();
        float[][] affine = field.getAffine();

        float[][] cornerPoints = new float[8][3];
        for (int i = 0; i < 3; i++) {
            cornerPoints[0][i] = affine[3][i];
            cornerPoints[1][i] = affine[3][i] + (dims[0]-1)*affine[0][i];
            cornerPoints[2][i] = affine[3][i] + (dims[0]-1)*affine[0][i] + (dims[1]-1)*affine[1][i];
            cornerPoints[3][i] = affine[3][i] + (dims[1]-1)*affine[1][i];
            cornerPoints[4][i] = affine[3][i] + (dims[2]-1)*affine[2][i];
            cornerPoints[5][i] = affine[3][i] + (dims[0]-1)*affine[0][i] + (dims[2]-1)*affine[2][i];
            cornerPoints[6][i] = affine[3][i] + (dims[0]-1)*affine[0][i] + (dims[1]-1)*affine[1][i] + (dims[2]-1)*affine[2][i];
            cornerPoints[7][i] = affine[3][i] + (dims[1]-1)*affine[1][i] + (dims[2]-1)*affine[2][i];
        }

        int cubeNumber = calculateCubeNumber(cornerPoints);
        int[] triangulation = cube_triangulation[cubeNumber];
        float[] edgePointsCoordinates = calculatePlaneCutCoords(cornerPoints);

        int nVertices = 12;
        int nIndices = triangulation.length;
        if(nIndices <= 0) {
            return;
        }

        IndexedTriangleArray ta = new IndexedTriangleArray(
                nVertices,
                GeometryArray.COORDINATES |
                GeometryArray.USE_COORD_INDEX_ONLY,
                nIndices);
        ta.setCapability(GeometryArray.ALLOW_COORDINATE_READ);
        ta.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);
        ta.setCoordinates(0, edgePointsCoordinates);
        ta.setCoordinateIndices(0, triangulation);
        outShape.setGeometry(ta);
        

//        //base axes
//        int nstrip = 2*ct.getNstrips();
//        int nverts = 2*ct.getNverts();
//        int[] strips = new int[nstrip];
//        for (int i = 0; i < strips.length; i++)
//          strips[i] = ct.getStrips()[0];
//        float[] verts = new float[3 * nverts];
//        float[] normals = new float[3 * nverts];
//        float[] colors = new float[3 * nverts];
//        int ivert = 0;
//
//        float r0 = 0.002f;
//        float[] c0 = {0,255,0};
//        float[] c1 = {0,255,0};
//        float[] p1 = new float[3];
//
//        for (int i = 0; i < 3; i++) {
//            p1[i] = point[i] + base[0][i]/10;
//        }
//        addTubeSegment(point, p1, r0, r0, c0, c0, ivert, verts, normals, colors);
//        ivert += ct.getNverts();
//
//        for (int i = 0; i < 3; i++) {
//            p1[i] = point[i] + base[1][i]/10;
//        }
//        addTubeSegment(point, p1, r0, r0, c1, c1, ivert, verts, normals, colors);
//
//        TriangleStripArray surf = new TriangleStripArray(nverts,
//                 GeometryArray.COORDINATES | GeometryArray.NORMALS | GeometryArray.COLOR_3,
//                 strips);
//        surf.setCapability(TriangleStripArray.ALLOW_COUNT_READ);
//        surf.setCapability(TriangleStripArray.ALLOW_FORMAT_READ);
//        surf.setCapability(TriangleStripArray.ALLOW_COORDINATE_READ);
//        surf.setCapability(TriangleStripArray.ALLOW_COORDINATE_WRITE);
//        surf.setCapability(TriangleStripArray.ALLOW_COLOR_READ);
//        surf.setCapability(TriangleStripArray.ALLOW_COLOR_WRITE);
//        surf.setCapability(TriangleStripArray.ALLOW_COORDINATE_READ);
//        surf.setCapability(TriangleStripArray.ALLOW_COORDINATE_WRITE);
//        surf.setCoordinates(0, verts);
//        surf.setColors(0, colors);
//        surf.setCapability(GeometryArray.ALLOW_NORMAL_READ);
//        surf.setCapability(GeometryArray.ALLOW_NORMAL_WRITE);
//        surf.setNormals(0, normals);
//
//        surfaces.setGeometry(surf);
    }

    private Glyph ct = new TubeTemplate(16);

    private void addTubeSegment(float[] p0, float[] p1, float r0, float r1, float[] c0, float[] c1,
                                int ivert, float[] verts, float[] normals, float[] colors) {
        float[] u = {0,0,0};
        float[] v = {0,0,0};
        float[] w = new float[3];
        for (int i = 0; i < 3; i++)
           w[i] = p1[i]-p0[i];
        float s = (float) (Math.sqrt(w[0]*w[0] + w[1]*w[1] + w[2]*w[2]));
        int nverts = ct.getNverts();
        float[] tVerts = ct.getVerts();
        float[] tNorms = ct.getNormals();
        if (s > 0.f)
        {
            for (int i = 0; i < 3; i++)
               w[i]/=s;
            int k = 0;
            if (Math.abs(w[1])<Math.abs(w[0]))
               k = 1;
            if (Math.abs(w[2])<Math.abs(w[k]))
               k = 2;
            u[k] = 1;
            for (int i = 0; i < 3; i++)
               u[i] -= w[i]*w[k];
            s = (float) (Math.sqrt(u[0]*u[0] + u[1]*u[1] + u[2]*u[2]));
            for (int i = 0; i < 3; i++)
               u[i]/=s;
            v[0] = u[1]*w[2]-u[2]*w[1];
            v[1] = u[2]*w[0]-u[0]*w[2];
            v[2] = u[0]*w[1]-u[1]*w[0];
         }
        else
           for (int m = 0; m < 3; m++)
               u[m] = v[m] = 0;
        for (int l = 0; l < nverts; l+=2)
            for (int m = 0; m < 3; m++)
            {
                verts[3*(ivert+l) + m] = r0*(float)(tVerts[3*l] * u[m] + tVerts[3*l+1] * v[m]) + p0[m];
                normals[3*(ivert+l) + m] =  (float)(tNorms[3*l] * u[m] + tNorms[3*l+1] * v[m]);
                colors[3*(ivert+l) + m] = c0[m];
            }
        for (int l = 1; l < nverts; l+=2)
            for (int m = 0; m < 3; m++)
            {
                verts[3*(ivert+l) + m] = r1*(float)(tVerts[3*l] * u[m] + tVerts[3*l+1] * v[m]) + p1[m];
                normals[3*(ivert+l) + m] =  (float)(tNorms[3*l] * u[m] + tNorms[3*l+1] * v[m]);
                colors[3*(ivert+l) + m] = c1[m];
            }
    }


    private float[] pointInBase(float[][] base, float[] p0, float[] p) {
        float[] tmp = new float[3];
        for (int i = 0; i < 3; i++) {
            tmp[i] = Math.abs(base[0][i] + base[1][i]);
        }
        float min = tmp[0];
        int mini = 0;
        if(tmp[1] < min) {
            min = tmp[1];
            mini = 1;
        }
        if(tmp[2] < min) {
            min = tmp[2];
            mini = 2;
        }

        int i0=0,i1=1;
        switch(mini) {
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

        for (int i = 0; i < 3; i++) {
            tmp[i] = p[i] - p0[i];
        }
        float detA = base[0][i0]*base[1][i1] - base[1][i0]*base[0][i1];
        if(detA == 0.0f)
            return null;

        float[] out = new float[2];
        out[0] = (tmp[i0]*base[1][i1] - base[1][i0]*tmp[i1])/detA;
        out[1] = (tmp[i1]*base[0][i0] - base[0][i1]*tmp[i0])/detA;
        return out;
    }


    private int[][] cube_triangulation = {
        {},
        {0,4,8},
        {0,5,9},
        {4,5,8,5,8,9},
        {1,5,11},
        {},
        {1,9,11,0,1,9},
        {1,4,8,1,8,9,1,9,11},
        {1,4,10},
        {0,1,10,0,8,10},
        {},
        {1,5,10,5,8,10,5,8,9},
        {4,10,11,4,5,11},
        {0,5,11,0,10,11,0,8,10},
        {0,4,9,4,9,11,4,10,11},
        {8,9,11,8,10,11},
        {2,6,8},
        {0,2,4,2,4,6},
        {},
        {2,5,9,2,4,5,2,4,6},
        {},
        {},
        {},
        {},
        {},
        {2,6,10,0,2,10,0,1,10},
        {},
        {1,6,10,1,5,6,2,5,6,2,5,9},
        {},
        {},
        {},
        {2,6,10,2,10,11,2,9,11},
        {2,7,9},
        {},
        {0,5,7,0,2,7},
        {2,7,8,5,7,8,4,5,8},
        {},
        {},
        {1,7,11,0,1,7,0,2,7},
        {2,4,8,2,4,7,1,4,7,1,7,11},
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        {2,7,8,7,8,10,7,10,11},
        {6,8,9,6,7,9},
        {0,4,9,4,6,9,6,7,9},
        {0,6,8,0,6,7,0,5,7},
        {5,6,7,4,5,6},
        {},
        {},
        {},
        {1,7,11,1,6,7,1,4,6},
        {},
        {},
        {},
        {1,5,10,5,7,10,6,7,10},
        {},
        {},
        {},
        {6,7,10,7,10,11},
        {3,7,11},
        {},
        {},
        {},
        {1,3,5,3,5,7},
        {},
        {3,7,9,1,3,9,0,1,9},
        {},
        {},
        {},
        {},
        {},
        {3,4,10,3,4,5,3,5,7},
        {},
        {0,4,10,0,9,10,3,9,10,3,7,9},
        {3,7,9,3,8,9,3,8,10},
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        {2,3,11,2,9,11},
        {},
        {0,5,11,0,2,11,2,3,11},
        {},
        {2,5,9,2,3,5,1,3,5},
        {},
        {0,2,3,0,1,3},
        {1,4,8,1,3,8,2,3,8},
        {},
        {},
        {},
        {},
        {},
        {},
        {3,4,10,2,3,4,0,2,4},
        {3,8,10,2,3,8},
        {3,6,11,6,9,11,6,8,9},
        {},
        {3,6,8,3,8,11,0,8,11,0,5,11},
        {3,6,11,4,6,11,4,5,11},
        {},
        {},
        {0,6,8,0,1,6,1,3,6},
        {1,4,6,1,3,6},
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        {3,6,10},
        {3,6,10},
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        {1,4,6,1,3,6},
        {0,6,8,0,1,6,1,3,6},
        {},
        {},
        {3,6,11,4,6,11,4,5,11},
        {3,6,8,3,8,11,0,8,11,0,5,11},
        {},
        {3,6,11,6,9,11,6,8,9},
        {3,8,10,2,3,8},
        {3,4,10,2,3,4,0,2,4},
        {},
        {},
        {},
        {},
        {},
        {},
        {1,4,8,1,3,8,2,3,8},
        {0,2,3,0,1,3},
        {},
        {2,5,9,2,3,5,1,3,5},
        {},
        {0,5,11,0,2,11,2,3,11},
        {},
        {2,3,11,2,9,11},
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        {3,7,9,3,8,9,3,8,10},
        {0,4,10,0,9,10,3,9,10,3,7,9},
        {},
        {3,4,10,3,4,5,3,5,7},
        {},
        {},
        {},
        {},
        {},
        {3,7,9,1,3,9,0,1,9},
        {},
        {1,3,5,3,5,7},
        {},
        {},
        {},
        {3,7,11},
        {6,7,10,7,10,11},
        {},
        {},
        {},
        {1,5,10,5,7,10,6,7,10},
        {},
        {},
        {},
        {1,7,11,1,6,7,1,4,6},
        {},
        {},
        {},
        {5,6,7,4,5,6},
        {0,6,8,0,6,7,0,5,7},
        {0,4,9,4,6,9,6,7,9},
        {6,8,9,6,7,9},
        {2,7,8,7,8,10,7,10,11},
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        {2,4,8,2,4,7,1,4,7,1,7,11},
        {1,7,11,0,1,7,0,2,7},
        {},
        {},
        {2,7,8,5,7,8,4,5,8},
        {0,5,7,0,2,7},
        {},
        {2,7,9},
        {2,6,10,2,10,11,2,9,11},
        {},
        {},
        {},
        {1,6,10,1,5,6,2,5,6,2,5,9},
        {},
        {2,6,10,0,2,10,0,1,10},
        {},
        {},
        {},
        {},
        {},
        {2,5,9,2,4,5,2,4,6},
        {},
        {0,2,4,2,4,6},
        {2,6,8},
        {8,9,11,8,10,11},
        {0,4,9,4,9,11,4,10,11},
        {0,5,11,0,10,11,0,8,10},
        {4,10,11,4,5,11},
        {1,5,10,5,8,10,5,8,9},
        {},
        {0,1,10,0,8,10},
        {1,4,10},
        {1,4,8,1,8,9,1,9,11},
        {1,9,11,0,1,9},
        {},
        {1,5,11},
        {4,5,8,5,8,9},
        {0,5,9},
        {0,4,8},
        {}
    };

    /**
     * @param field the field to set
     */
    public void setField(RegularField field) {
        this.field = field;
        calculateBaseExtents();
        update3DObject();
    }

}
