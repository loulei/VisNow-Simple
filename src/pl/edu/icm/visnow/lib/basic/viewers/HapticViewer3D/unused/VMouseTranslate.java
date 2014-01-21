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
import javax.vecmath.Vector3d;

/**
 * Modifed version of com.sun.j3d.utils.behaviors.mouse.MouseTranslate
 *
 * @author Krzysztof Madejski <krzysztof@madejscy.pl> ICM, University of Warsaw
 */
public class VMouseTranslate extends VMouseBehavior {

    double x_factor = .02;
    double y_factor = .02;
    Vector3d translation = new Vector3d();

    /**
     * Creates a mouse translate behavior given the transform group.
     * <p/>
     * @param transformGroup The transformGroup to operate on.
     */
    public VMouseTranslate(ViewLock vLock, TransformGroup transformGroup) {
        super(vLock, transformGroup);
    }

    /**
     * Creates a default translate behavior.
     */
    public VMouseTranslate(ViewLock vLock) {
        super(vLock);
    }

    public VMouseTranslate(ViewLock vLock, Component c) {
        super(vLock, c);
    }

    public void initialize() {
        super.initialize();
        if ((flags & INVERT_INPUT) == INVERT_INPUT) {
            invert = true;
            x_factor *= -1;
            y_factor *= -1;
        }
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
        if (!evt.isAltDown() && evt.isMetaDown()) {
            if ((id == MouseEvent.MOUSE_DRAGGED)
                    && vLock.ownLock(this)) {

                x = evt.getX();
                y = evt.getY();

                dx = x - x_last;
                dy = y - y_last;

                if ((!reset) && ((Math.abs(dy) < 50) && (Math.abs(dx) < 50))) {
                    //System.out.println("dx " + dx + " dy " + dy);
                    transformGroup.getTransform(currXform);

                    translation.x = dx * x_factor;
                    translation.y = -dy * y_factor;

                    transformX.set(translation);

                    if (invert) {
                        currXform.mul(currXform, transformX);
                    } else {
                        currXform.mul(transformX, currXform);
                    }

                    transformGroup.setTransform(currXform);
                } else {
                    reset = false;
                }
                x_last = x;
                y_last = y;
            } else if (id == MouseEvent.MOUSE_PRESSED) {
                initAction(evt);
            } else if (id == MouseEvent.MOUSE_RELEASED) {
                endAction(evt);
            }
        }
    }
}
