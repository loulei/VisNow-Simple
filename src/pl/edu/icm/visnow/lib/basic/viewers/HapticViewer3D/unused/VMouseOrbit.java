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
package pl.edu.icm.visnow.lib.basic.viewers.HapticViewer3D.unused;

import java.awt.Component;
import java.awt.event.MouseEvent;
import javax.media.j3d.TransformGroup;

/**
 * Modifed version of com.sun.j3d.utils.behaviors.mouse.MouseRotate
 *
 * @author Krzysztof Madejski <krzysztof@madejscy.pl> ICM, University of Warsaw
 */
public class VMouseOrbit extends VMouseBehavior {

    double x_angle, y_angle;
    double x_factor = .05;
    double y_factor = .05;

    /**
     * Creates a rotate behavior given the transform group.
     * <p/>
     * @param transformGroup The transformGroup to operate on.
     */
    public VMouseOrbit(ViewLock vLock, TransformGroup transformGroup) {
        super(vLock, transformGroup);
    }

    /**
     * Creates a default mouse rotate behavior.
     * */
    public VMouseOrbit(ViewLock vLock) {
        super(vLock);
    }

    public VMouseOrbit(ViewLock vLock, Component c) {
        super(vLock, c);
    }

    @Override
    public void initialize() {
        super.initialize();
        x_factor *= -1;
        y_factor *= -1;
    }

    /**
     * Return the x-axis movement multipler.
     * */
    public double getXFactor() {
        return x_factor;
    }

    /**
     * Return the y-axis movement multipler.
     * */
    public double getYFactor() {
        return y_factor;
    }

    /**
     * Set the x-axis amd y-axis movement multipler with factor.
     * */
    public void setFactor(double factor) {
        x_factor = y_factor = factor;
    }

    /**
     * Set the x-axis amd y-axis movement multipler with xFactor and yFactor
     * respectively.
     * */
    public void setFactor(double xFactor, double yFactor) {
        x_factor = xFactor;
        y_factor = yFactor;
    }

    protected void doProcess(MouseEvent evt) {
        int id;
        int dx, dy;

        processMouseEvent(evt);

        id = evt.getID();
        if (!evt.isMetaDown() && !evt.isAltDown()) {
            if (id == MouseEvent.MOUSE_DRAGGED && vLock.ownLock(this)) {
                x = evt.getX();
                y = evt.getY();

                dx = x - x_last;
                dy = y - y_last;

                if (!reset) {
                    x_angle = dy * y_factor;
                    y_angle = dx * x_factor;

                    transformX.rotX(x_angle);
                    transformY.rotY(y_angle);

                    currXform.mul(transformX, initialTrans);
                    currXform.mul(transformY, currXform);

                    // Update xform
                    transformGroup.setTransform(currXform);
                }
            } else if (id == MouseEvent.MOUSE_PRESSED) {
                reset = false;
                initAction(evt);
            } else if (id == MouseEvent.MOUSE_RELEASED) {
                endAction(evt);
            }
        }
    }
}
