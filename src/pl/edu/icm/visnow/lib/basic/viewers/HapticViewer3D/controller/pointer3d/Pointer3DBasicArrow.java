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
package pl.edu.icm.visnow.lib.basic.viewers.HapticViewer3D.controller.pointer3d;

import javax.media.j3d.Appearance;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.Group;
import javax.media.j3d.LineArray;
import javax.media.j3d.QuadArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TriangleArray;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import org.apache.log4j.Logger;
import pl.edu.icm.visnow.geometries.objects.generics.OpenBranchGroup;

/**
 *
 * Custom 3D arrow cursor. It is used as a base device pointer.
 * <p/>
 * To this pointer a couple of "extensions" can be added depending on types of pick 3D that are
 * enabled in modules.
 * <p/>
 * The code is partly inspired on the code from constructor of SensorGnomonEcho (see link).
 *
 * @see <a
 * href="http://java3d.sourcearchive.com/documentation/1.5.2plus-pdfsg-3/SensorGnomonEcho_8java-source.html#l00064">SensorGnomonEcho
 * source code</a>
 *
 * @author Łukasz Czerwiński <czerwinskilukasz1 [#] gmail.com>, ICM, University of Warsaw, 2013
 */
public class Pointer3DBasicArrow extends OpenBranchGroup {

    public static final int vertexFormat =
            GeometryArray.COORDINATES | GeometryArray.NORMALS;
//
    private static final Logger LOGGER =
            Logger.getLogger(new Throwable().getStackTrace()[0].getClassName());
//
    /** For transparent elements. */
    protected Appearance nonTransparentApp = null;
    /** For transparent elements. */
    protected Appearance transparentApp = null;

    /**
     *
     * @param baseWidth width of the arrow's head (it's an equilateral triangle)
     *                  TODO MEDIUM: change code to reflect that comment!!
     */
    Pointer3DBasicArrow(float baseWidth) {

        float shaftDepth = 1.8f * baseWidth;
        float shaftBaseSize = 0.3f * baseWidth;
        float headWidth = baseWidth;
        float headDepth = headWidth * (float) Math.sqrt(3) / 2.0f;  // height of an equilateral triangle


        nonTransparentApp = createAppearance(false);
        transparentApp = createAppearance(true);


        Shape3D arrowShaft = createShaft(shaftBaseSize,
                                         shaftDepth, headDepth,
                                         vertexFormat);
        Shape3D arrowHead = createHead(headWidth, headDepth,
                                       shaftBaseSize,
                                       vertexFormat);


        this.addChild(arrowShaft, false);
        this.addChild(arrowHead, false);

    }

    /** Helper function for quick settings of transparency material */
    final void addChild(Shape3D node, boolean transparent) {
        addChildTo(node, transparent, this);
    }

    /** Helper function for quick settings of transparency material */
    final void addChildTo(Shape3D node, boolean transparent, Group group) {
        node.setAppearance(transparent ? transparentApp : nonTransparentApp);
        group.addChild(node);
    }

    protected static Shape3D createShaft(float shaftBaseSize,
                                         float baseDepth, float headDepth,
                                         int vertexFormat) {

        float offset = shaftBaseSize / 2.0f;


//<editor-fold defaultstate="collapsed" desc=" some helper constants ">
        final int FRONT = 0;
        final int BACK = 1;
        final int LEFT = 2;
        final int RIGHT = 3;
        final int TOP = 4;
        final int BOTTOM = 5;

        final int SIZE = 6 * 4;
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=" cube coordinates ">
        Point3f[][] cube = new Point3f[6][4];
        cube[FRONT][3] = new Point3f(-offset, offset, baseDepth + headDepth);
        cube[FRONT][2] = new Point3f(offset, offset, baseDepth + headDepth);
        cube[FRONT][1] = new Point3f(offset, -offset, baseDepth + headDepth);
        cube[FRONT][0] = new Point3f(-offset, -offset, baseDepth + headDepth);

        cube[BACK][0] = new Point3f(offset, -offset, headDepth);
        cube[BACK][1] = new Point3f(-offset, -offset, headDepth);
        cube[BACK][2] = new Point3f(-offset, offset, headDepth);
        cube[BACK][3] = new Point3f(offset, offset, headDepth);

        cube[LEFT][3] = cube[BACK][1];
        cube[LEFT][2] = cube[FRONT][0];
        cube[LEFT][1] = cube[FRONT][3];
        cube[LEFT][0] = cube[BACK][2];

        cube[RIGHT][3] = cube[FRONT][1];
        cube[RIGHT][2] = cube[BACK][0];
        cube[RIGHT][1] = cube[BACK][3];
        cube[RIGHT][0] = cube[FRONT][2];

        cube[TOP][3] = cube[FRONT][3];
        cube[TOP][2] = cube[FRONT][2];
        cube[TOP][1] = cube[BACK][3];
        cube[TOP][0] = cube[BACK][2];

        cube[BOTTOM][0] = cube[FRONT][0];
        cube[BOTTOM][1] = cube[FRONT][1];
        cube[BOTTOM][2] = cube[BACK][0];
        cube[BOTTOM][3] = cube[BACK][1];
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=" transform group ">
        int v = 0;
        Point3f[] vertices = new Point3f[SIZE];

        for (int i = 0; i < 6; ++i) {
            for (int j = 3; j >= 0; --j) {
                vertices[v++] = cube[i][j];
            }
        }
        QuadArray qa = new QuadArray(SIZE, vertexFormat);
        qa.setCoordinates(0, vertices);
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=" normals for light ">

        QuadArray debugNormalLines = new QuadArray(4 * 4, LineArray.COORDINATES);
        {
            Vector3f v0 = new Vector3f();
            Vector3f v1 = new Vector3f();
            Vector3f[] normals = new Vector3f[SIZE];

            Point3f[] debugNormaLinesPoints = new Point3f[4 * 4];

            Vector3f v_temp = new Vector3f();
            v_temp.sub(vertices[1], vertices[0]);
            float thickness = v_temp.length() / 2;

            for (int i = 0; i < SIZE; i += 4) {

                v0.sub(vertices[i + 1], vertices[i]);

                v1.sub(vertices[i + 2], vertices[i]);

                Vector3f n = new Vector3f();
                n.cross(v0, v1);  //  right hand rule
                n.normalize();
                Vector3f n_debug = new Vector3f(n);
                n_debug.scale(thickness);

                for (int a = i; a < i + 4; ++a) {
                    normals[a] = n;

                    if (i == 0) {
                        Point3f p;
                        p = new Point3f(vertices[a]);
                        debugNormaLinesPoints[4 * a] = p;
                        p = new Point3f(vertices[a]);
                        p.add(n);
                        debugNormaLinesPoints[4 * a + 1] = p;

                        p = new Point3f(p);
                        p.add(new Point3f(0, 0, thickness));

                        debugNormaLinesPoints[4 * a + 2] = p;

                        p = new Point3f(vertices[a]);
                        p.add(new Point3f(0, 0, thickness));

                        debugNormaLinesPoints[4 * a + 3] = p;
                    }
                }

            }
            qa.setNormals(0, normals);

            debugNormalLines.setCoordinates(0, debugNormaLinesPoints);
        }
//</editor-fold>


        Shape3D arrowShaft = new Shape3D(qa);
        arrowShaft.addGeometry(debugNormalLines);
        return arrowShaft;
    }

    protected static Shape3D createHead(float headWidth, float headDepth,
                                        float shaftBaseSize,
                                        int vertexFormat) {
        float headOffset = headWidth / 2.0f;
        float baseOffset = shaftBaseSize / 2.0f;

//<editor-fold defaultstate="collapsed" desc=" define some constants ">
        final int LEFT = 0;
        final int RIGHT = 1;
        final int BACK = 2;
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=" define points for head of the arrow ">
        Point3f[][] headPoints = new Point3f[3][2];

        headPoints[LEFT][0] = new Point3f(-headOffset, -baseOffset, headDepth);
        headPoints[LEFT][1] = new Point3f(-headOffset, baseOffset, headDepth);

        headPoints[RIGHT][0] = new Point3f(headOffset, -baseOffset, headDepth);
        headPoints[RIGHT][1] = new Point3f(headOffset, baseOffset, headDepth);

        headPoints[BACK][0] = new Point3f(0, -baseOffset, 0);
        headPoints[BACK][1] = new Point3f(0, baseOffset, 0);
//</editor-fold>

        Shape3D arrowHead = new Shape3D();

//<editor-fold defaultstate="collapsed" desc=" 3 quads for the head of the arrow ">
        {
//            final int UP = 0;
//            final int DOWN = 1;

            final int RECT_FRONT = 0;
            final int RECT_LEFT = 1;
            final int RECT_RIGHT = 2;

            QuadArray qaHead = new QuadArray(3 * 4, vertexFormat);

            Point3f[][] headRects = new Point3f[3][4];
            headRects[RECT_FRONT][0] = headPoints[LEFT][1];
            headRects[RECT_FRONT][1] = headPoints[LEFT][0];
            headRects[RECT_FRONT][2] = headPoints[RIGHT][0];
            headRects[RECT_FRONT][3] = headPoints[RIGHT][1];

            headRects[RECT_LEFT][0] = headPoints[LEFT][0];
            headRects[RECT_LEFT][1] = headPoints[LEFT][1];
            headRects[RECT_LEFT][2] = headPoints[BACK][1];
            headRects[RECT_LEFT][3] = headPoints[BACK][0];

            headRects[RECT_RIGHT][0] = headPoints[RIGHT][1];
            headRects[RECT_RIGHT][1] = headPoints[RIGHT][0];
            headRects[RECT_RIGHT][2] = headPoints[BACK][0];
            headRects[RECT_RIGHT][3] = headPoints[BACK][1];

            int v = 0;
            Point3f[] qaVertices = new Point3f[3 * 4];
            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 4; ++j) {
                    qaVertices[v++] = headRects[i][j];
                }
            }

            qaHead.setCoordinates(0, qaVertices);

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=" normals for light for quads ">
            {
                Vector3f v0 = new Vector3f();
                Vector3f v1 = new Vector3f();
                Vector3f[] normals = new Vector3f[3 * 4];

                for (int i = 0; i < 3 * 4; i += 4) {

                    v0.sub(qaVertices[i + 1], qaVertices[i]);
                    v1.sub(qaVertices[i + 2], qaVertices[i]);

                    Vector3f n = new Vector3f();
                    n.cross(v0, v1);  //  right hand rule
                    n.normalize();

                    normals[i] = n;
                    normals[i + 1] = n;
                    normals[i + 2] = n;
                    normals[i + 3] = n;
                }
                qaHead.setNormals(0, normals);

            }

            arrowHead.addGeometry(qaHead);
        }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=" 2 triangles for the head of the arrow ">
        {
            int v = 0;
            Point3f[] trVertices = new Point3f[2 * 3];
            for (int i = 2; i >= 0; --i) { // reversed order because of lighting
                trVertices[v++] = headPoints[i][0];
            }

            for (int i = 0; i < 3; ++i) {
                trVertices[v++] = headPoints[i][1];
            }

            TriangleArray taHead = new TriangleArray(2 * 3, vertexFormat);
            taHead.setCoordinates(0, trVertices);


// normals for light for quads
            {
                Vector3f v0 = new Vector3f();
                Vector3f v1 = new Vector3f();
                Vector3f[] normals = new Vector3f[2 * 3];

                for (int i = 0; i < 2 * 3; i += 3) {

                    v0.sub(trVertices[i + 1], trVertices[i]);
                    v1.sub(trVertices[i + 2], trVertices[i]);

                    Vector3f n = new Vector3f();
                    n.cross(v0, v1);  //  right hand rule
                    n.normalize();

                    normals[i] = n;
                    normals[i + 1] = n;
                    normals[i + 2] = n;
                }
                taHead.setNormals(0, normals);

            }


            arrowHead.addGeometry(taHead);
        }
//</editor-fold>

        return arrowHead;
    }
}
