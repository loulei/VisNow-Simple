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
package pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.pick;

import java.util.EventObject;
import javax.media.j3d.Transform3D;
import javax.swing.event.ChangeEvent;
import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix3f;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;

/**
 * An event used for notifyng modules that a mouse pick (so called "emulated 3D pick") or a "device
 * 3D pick" was performed in the viewer.
 * <p>When an emulated 3D pick was performed or a device 3D pick that returns only a 3D point,
 * <code>point</code> will store the point (use
 * <code>getPoint()</code> to get it).
 * </p>
 * <p>When also a plain (a direction) was picked,
 * <code>point</code> will store the point picked, while
 * <code>sensorInLocal</code> will store transform matrix in rootObj coordinates (contains rotation
 * information as well as position).
 * Using these one can reconstruct the plain that was picked by a device or rotation of a 3D cursor.
 * </p>
 * <p/>
 * @author Krzysztof S. Nowinski University of Warsaw, ICM
 * @author Łukasz Czerwiński <czerwinskilukasz1 [#] gmail.com>, ICM, University of Warsaw, 2013
 * <p/>
 * @see PickObject see sample code in PickObject class
 */
public class Pick3DEvent extends EventObject {

    /**
     * Point that was picked by any of emulated 3D pick or device 3D pick (will never be null).
     */
    protected float[] point = null;
    /**
     * When a plain was picked,
     * <code>sensorInLocal</code> and
     * <code>point</code> will be filled.
     * They can be used to reconstruct the equation of a plain or to get vectors with directions.
     */
    protected Transform3D sensorInLocal = null;

    /**
     *
     * @param source        object that generated Pick3DEvent
     * @param point         will be copied to the MyPoint3d object
     * @param sensorInLocal a reference will be stored
     */
    public Pick3DEvent(Object source, Point3d point, Transform3D sensorInLocal) {
        super(source);

        if (point == null)
            throw new IllegalArgumentException("point cannot be null");

        this.point = new float[]{
            (float) point.x,
            (float) point.y,
            (float) point.z
        };
        this.sensorInLocal = sensorInLocal;
    }
//
//    public Pick3DEvent(Object source, float[] point) {
//        super(source);
//        this.point = point;
//    }

    /** Retuns the picked point. It will be always non-null. */
    public float[] getPoint() {
        return point;
    }

    /**
     * @return true when pick 3D was performed, false otherwise
     */
    public boolean isPick3D() {
        return sensorInLocal != null;
    }

    /**
     * Returns transform matrix in rootObj coordinates or null if it's not a pick 3D.
     * <p/>
     * @return transform matrix or null if it's not a pick 3D
     */
    public Transform3D getSensorInLocal() {
        return sensorInLocal;
    }

    /**
     * Returns rotation matrix or null if it's not a pick 3D.
     * <p/>
     * @return rotation matrix or null if not a pick 3D
     */
    public Matrix3f getRotation3f() {
        if (sensorInLocal == null)
            return null;

        Matrix3f rotation = new Matrix3f();
        sensorInLocal.getRotationScale(rotation);
        return rotation;
    }

    /**
     * Returns rotation matrix or null if it's not a pick 3D.
     * <p/>
     * @return rotation matrix or null if not a pick 3D
     */
    public Matrix3d getRotation3d() {
        if (sensorInLocal == null)
            return null;

        Matrix3d rotation = new Matrix3d();
        sensorInLocal.getRotationScale(rotation);
        return rotation;
    }

    /**
     * Rotates the transform 90 degrees clockwise. It is useful for conversion hoe -> knife.
     * <p/>
     * NOTE: This will change output from getRotation3f(), getRotation3d() and getSensorInLocal().
     * It will not change getPoint() as point is stored in a separate variable.
     */
    public void changeRotateXClockWise() {
        Transform3D rotate = new Transform3D();
        rotate.rotX(Math.PI / 2);
        sensorInLocal.mul(sensorInLocal, rotate);
    }
}
