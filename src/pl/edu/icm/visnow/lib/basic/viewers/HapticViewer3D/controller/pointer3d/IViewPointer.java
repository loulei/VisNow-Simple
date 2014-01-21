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

import javax.media.j3d.Bounds;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.pick.PickObject;
import pl.edu.icm.visnow.lib.basic.viewers.HapticViewer3D.model.devices.IPassiveDevice;

/**
 * Should be implemented by all device pointers used in VisNow. Currenlty there is only one such
 * pointer: {@link Pointer3DViewBehavior}.
 * <p/>
 * @author Krzysztof Madejski <krzysztof@madejscy.pl> ICM, University of Warsaw
 * @author modified by Łukasz Czerwiński <czerwinskilukasz1 [#] gmail.com>, ICM, University of
 * Warsaw, 2013
 */
public interface IViewPointer {

    public void pointerActivate(IPassiveDevice pointer);

    /* temporary debug functions */
    public boolean isPickModule3DActive();

    public boolean getLoosePointer();

//TODO MEDIUM refactor: move PointerStateListener outside from Pointer3DViewBehavior!
    public void addPointerStateListener(Pointer3DViewBehavior.PointerStateListener listener);

    public void setSchedulingBounds(Bounds region);

//TODO MEDIUM refactor: move PickModuleListener and PickPlainListener outside from PickObject - maybe here?
    public void setPickModuleListener(PickObject.PickModuleListener l);
    public void removePickModuleListener();

    public void setPickPlainListener(PickObject.PickPlainListener l);
    public void removePickPlainListener();
}
