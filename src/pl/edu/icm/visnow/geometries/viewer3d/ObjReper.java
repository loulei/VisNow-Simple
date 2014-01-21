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
package pl.edu.icm.visnow.geometries.viewer3d;

import com.sun.j3d.utils.behaviors.mouse.MouseBehaviorCallback;
import javax.media.j3d.Transform3D;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import pl.edu.icm.visnow.geometries.objects.generics.OpenTransformGroup;

/**
 *
 * @author Łukasz Czerwiński <czerwinskilukasz1 [#] gmail.com>, ICM, University of Warsaw, 2013
 */
public class ObjReper
        extends OpenTransformGroup
        implements ObjRotate.Listener {

    protected Transform3D reperTransform = new Transform3D();

    public ObjReper() {
    }

    public ObjReper(String name) {
        super(name);
    }

    /**
     * Fired when scene transform has changed, e.g. scene was moved or rotated.
     * <p/>
     * @param newTransform
     */
    @Override
    public void sceneTransformChanged(Transform3D newTransform) {
        applyRotationFromTranslation(newTransform);
    }

    /**
     * Rotates reper according to a rotation from a transform given in parameter.
     * Used to apply to reper current rotation of a 3D scene (objRotate).
     * <p/>
     * @param newTransform A transform which rotation will be applied to reper.
     */
    protected void applyRotationFromTranslation(Transform3D newTransform) {

        // Remember old transform
        this.getTransform(reperTransform);
        // Compute new rotation
        Vector3d translation = new Vector3d();
        reperTransform.get(translation);
        reperTransform.setTranslation(new Vector3f(0, 0, 0));

        // now reperTransform stores only rotation
        // Apply new transform
        reperTransform.set(newTransform);
        // Restore old translation (rotation will be new);
        reperTransform.setTranslation(translation);
        this.setTransform(reperTransform);

    }

    /**
     * @deprecated Should not be used. Instead manipulate using objRotate object!
     */
    @Override
    public void setTransform(Transform3D t1) {
        super.setTransform(t1);
    }

    /**
     * This method should prevent viewer from keeping huge reper in some situations, especially on
     * Mac OS X.
     * @see Viewer3D#kick() "kick in Viewer3D"
     */
    
    void refreshTransform() {
        Transform3D t3d = new Transform3D();
        this.getTransform(t3d);
        this.setTransform(t3d);
    }
    
    
}
