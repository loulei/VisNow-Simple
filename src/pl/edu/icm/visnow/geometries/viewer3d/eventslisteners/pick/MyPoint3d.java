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

import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;

/**
 *
 * @author Łukasz Czerwiński <czerwinskilukasz1 [#] gmail.com>, ICM, University of Warsaw, 2013
 */
public class MyPoint3d extends Point3d {

    public MyPoint3d() {
    }

    public MyPoint3d(Point3d p1) {
        super(p1);
    }

    public MyPoint3d(double x, double y, double z) {
        super(x, y, z);
    }

    public MyPoint3d(double[] p) {
        super(p);
    }

    public MyPoint3d(Point3f p1) {
        super(p1);
    }

    public MyPoint3d(Tuple3f t1) {
        super(t1);
    }

    public MyPoint3d(Tuple3d t1) {
        super(t1);
    }

    public void set(int index, double value) {
        switch (index) {
            case 0:
                setX(value);
                break;
            case 1:
                setY(value);
                break;
            case 2:
                setZ(value);
                break;
            default:
                throw new IllegalArgumentException("index in Tuple3d can be only 0, 1 or 2");
        }
    }

    public double get(int index) {
        double result;
        switch (index) {
            case 0:
                result = getX();
                break;
            case 1:
                result = getY();
                break;
            case 2:
                result = getZ();
                break;
            default:
                throw new IllegalArgumentException("index in Tuple3d can be only 0, 1 or 2");
        }
        return result;
    }

    public float[] toFloat() {
        float[] p;
        p = new float[]{(float) x,
                        (float) y,
                        (float) z
        };
        return p;
    }

    public double[] toDouble() {
        double[] p;
        p = new double[]{x, y, z};
        return p;
    }
}
