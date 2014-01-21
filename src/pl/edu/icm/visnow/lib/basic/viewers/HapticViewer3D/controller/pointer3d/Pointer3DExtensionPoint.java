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

import com.sun.j3d.utils.geometry.Sphere;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Transform3D;
import javax.vecmath.Vector3f;
import pl.edu.icm.visnow.geometries.objects.generics.OpenAppearance;
import pl.edu.icm.visnow.geometries.objects.generics.OpenTransformGroup;

/**
 * An extension to the arrow pointer meaning that a POINT pick 3D is active (a pick 3D that will
 * use only a picked point).
 * <p/>
 * It shows a small ball next to the arrow's head.
 * <p/>
 * @author Łukasz Czerwiński <czerwinskilukasz1 [#] gmail.com>, ICM, University of Warsaw, 2013
 */
public class Pointer3DExtensionPoint extends Pointer3DExtension {

    public Pointer3DExtensionPoint(float radius, float xOffset, float yOffset, float zOffset) {
        initialize(radius, xOffset, yOffset, zOffset);
    }

    /**
     * Creates small ball to be displayed next to the arrow
     */
    private void initialize(float radius, float xOffset, float yOffset, float zOffset) {

        OpenTransformGroup tra = new OpenTransformGroup("pointer point transform group");
        Transform3D move = new Transform3D();
        move.setTranslation(new Vector3f(xOffset, yOffset, zOffset));
        tra.setTransform(move);
        Sphere sphere = new Sphere(radius);
        OpenAppearance appearance = createAppearance(false);
        appearance.setColoringAttributes(new ColoringAttributes(0.3f, 0.3f, 1.0f, ColoringAttributes.FASTEST));
        sphere.setAppearance(appearance);
        tra.addChild(sphere);
        this.addChild(tra);
    }
}
