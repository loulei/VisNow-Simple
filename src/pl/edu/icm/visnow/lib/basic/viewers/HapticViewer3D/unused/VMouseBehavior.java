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

import com.sun.j3d.utils.behaviors.mouse.MouseBehavior;
import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnAWTEvent;
import javax.media.j3d.WakeupOnBehaviorPost;

/**
 *
 * @author Krzysztof Madejski <krzysztof@madejscy.pl> ICM, University of Warsaw
 */
public abstract class VMouseBehavior extends MouseBehavior {

    protected final ViewLock vLock;
    protected Transform3D initialTrans = new Transform3D();

    public VMouseBehavior(ViewLock vLock, TransformGroup transformGroup) {
        super(transformGroup);
        this.vLock = vLock;
    }

    /**
     * Creates a default mouse rotate behavior.
     * */
    public VMouseBehavior(ViewLock vLock) {
        super(0);
        this.vLock = vLock;
    }

    public VMouseBehavior(ViewLock vLock, Component c) {
        super(c, 0);
        this.vLock = vLock;
    }

    protected abstract void doProcess(MouseEvent evt);

    protected void initAction(MouseEvent evt) {
        if (vLock.lock(this)) {
            x_last = evt.getX();
            y_last = evt.getY();
            transformGroup.getTransform(initialTrans);
        }
    }

    protected void endAction(MouseEvent evt) {
        if (vLock.ownLock(this)) {
            vLock.releease(this);
        }
    }

    public void processStimulus(Enumeration criteria) {
        WakeupCriterion wakeup;
        AWTEvent[] events;
        MouseEvent evt;

        while (criteria.hasMoreElements()) {
            wakeup = (WakeupCriterion) criteria.nextElement();
            if (wakeup instanceof WakeupOnAWTEvent) {
                events = ((WakeupOnAWTEvent) wakeup).getAWTEvent();
                if (events.length > 0) {
                    evt = (MouseEvent) events[events.length - 1];
                    doProcess(evt);
                }
            } else if (wakeup instanceof WakeupOnBehaviorPost) {
                while (true) {
                    // access to the queue must be synchronized
                    synchronized (mouseq) {
                        if (mouseq.isEmpty()) {
                            break;
                        }
                        evt = (MouseEvent) mouseq.remove(0);
                        // consolidate MOUSE_DRAG events
                        while ((evt.getID() == MouseEvent.MOUSE_DRAGGED)
                                && !mouseq.isEmpty()
                                && (((MouseEvent) mouseq.get(0)).getID()
                                == MouseEvent.MOUSE_DRAGGED)) {
                            evt = (MouseEvent) mouseq.remove(0);
                        }
                    }
                    doProcess(evt);
                }
            }

        }
        wakeupOn(mouseCriterion);
    }
}
