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

import javax.media.j3d.GeometryArray;
import javax.media.j3d.QuadArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.vecmath.Color4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import pl.edu.icm.visnow.geometries.objects.generics.OpenAppearance;
import pl.edu.icm.visnow.geometries.objects.generics.OpenTransformGroup;
import static pl.edu.icm.visnow.lib.basic.viewers.HapticViewer3D.controller.pointer3d.Pointer3DExtensionSlice.createPlain;

/**
 * An extension to the arrow pointer meaning that a PLAIN pick 3D is active (a pick 3D that will
 * use a picked point and orientation of a cursor to determine a plain in 3D).
 * <p/>
 * It shows a square depicting a plain which will be picked. By default the square is parallel to
 * the arrow's shaft.
 * <p/>
 * @author Łukasz Czerwiński <czerwinskilukasz1 [#] gmail.com>, ICM, University of Warsaw, 2013
 */
public class Pointer3DExtensionSlice extends Pointer3DExtension {

    /**
     * The simpliest constructor. Sample plain located in the arrow's head will be parallel to the
     * arrow's head.
     * <p/>
     * @param baseWidth Base width of the arrow
     */
    public Pointer3DExtensionSlice(float baseWidth) {
        this(baseWidth, false, null);
    }

    /**
     * The simpliest constructor. Sample plain located in the arrow's head will be parallel to the
     * arrow's head.
     * <p/>
     * @param baseWidth Base width of the arrow
     */
    public Pointer3DExtensionSlice(float baseWidth, boolean transparent) {
        this(baseWidth, transparent, null);
    }

    /**
     *
     * @param baseWidth      Width of the arrow's head (it's an equilateral triangle)
     * @param plainTransform Transform to be applied to the sample plain located in the arrow's
     *                       head.<br/>
     * If set to null, no transform will be applied - sample plain will
     * remain parallel to the arrow's head.
     */
    public Pointer3DExtensionSlice(float baseWidth, boolean transparent, Transform3D plainTransform) {
        super("Pointer3DExtensionSlice");

        OpenTransformGroup tra = new OpenTransformGroup("pointer slice transform group");
        Shape3D plain = createPlain(2 * baseWidth, 2 * baseWidth, vertexFormat);

        OpenAppearance appearance = createAppearance(transparent);
        plain.setAppearance(appearance);

        tra.addChild(plain);

        if (plainTransform != null)
            tra.setTransform(plainTransform);

        this.addChild(tra);
    }

    protected static Shape3D createPlain(float plainWidth,
                                         float plainHeight,
                                         int vertexFormat) {

//<editor-fold defaultstate="collapsed" desc=" create plane (two rectangles) ">
        final int SIZE = 8;
        Point3f[] vertices = new Point3f[SIZE];
        float offsetH = plainHeight / 2;
        float offsetW = plainWidth / 2;

        vertices[0] = new Point3f(-offsetH, 0, offsetW);
        vertices[1] = new Point3f(-offsetH, 0, -offsetW);
        vertices[2] = new Point3f(offsetH, 0, -offsetW);
        vertices[3] = new Point3f(offsetH, 0, offsetW);

        for (int i = 4; i < 8; ++i) {
            vertices[i] = vertices[7 - i];
        }
        QuadArray qa = new QuadArray(SIZE, vertexFormat | GeometryArray.COLOR_4);
        qa.setCoordinates(0, vertices);
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=" normals for light ">

        {
            Vector3f v1 = new Vector3f();
            Vector3f v2 = new Vector3f();
            Vector3f[] normals = new Vector3f[2 * 4];

            for (int i = 0; i <= 1; ++i) {
                v1.sub(vertices[4 * i], vertices[4 * i + 1]);
                v2.sub(vertices[4 * i + 1], vertices[4 * i + 2]);

                Vector3f n0 = new Vector3f();
                n0.cross(v1, v2);
                n0.normalize();

                for (int j = 4 * i; j < 4 * i + 4; ++j) {
                    normals[j] = n0;
                }
            }
            qa.setNormals(0, normals);

        }

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=" color of the plain ">

        /* Alpha works only when transparency was enabled in appearance (PointerBasicArrow3D)! */

        Color4f[] colors = new Color4f[8];
        colors[0] = new Color4f(1.0f, 0.0f, 0.0f, 0.3f);
        colors[1] = new Color4f(0.0f, 1.0f, 0.0f, 0.3f);
        colors[2] = new Color4f(1.0f, 0.0f, 0.0f, 0.3f);
        colors[3] = new Color4f(1.0f, 1.0f, 0.0f, 0.3f);

        for (int i = 4; i < 8; ++i) {
            colors[i] = colors[7 - i];
        }

        qa.setColors(0, colors);
//</editor-fold>

        Shape3D plain = new Shape3D(qa);
        return plain;
    }
}
