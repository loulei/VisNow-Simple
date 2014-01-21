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
package pl.edu.icm.visnow.lib.basic.viewers.HapticViewer3D.model.devices.mouse;

import javax.media.j3d.Transform3D;
import javax.vecmath.Tuple3f;
import pl.edu.icm.visnow.lib.basic.viewers.HapticViewer3D.model.devices.BasicRegisteredDevice;
import pl.edu.icm.visnow.lib.basic.viewers.HapticViewer3D.model.devices.DeviceType;

/**
 * Only for DeviceManager. This class was created for DeviceManager to have a pointer to
 * IPassiveDevice which is mouse.
 * This device is always used and it is always owned by all Viewers. It also can be neither
 * registered nor unregistered : ]
 *
 * @author Łukasz Czerwiński <czerwinskilukasz1 [#] gmail.com>, ICM, University of Warsaw, 2013
 */
public class DummyMouseRegisteredDevice extends BasicRegisteredDevice {

    public static final String MOUSE_NAME = "MOUSE_DEVICE";
    public static final String FRIENDLY_MOUSE_NAME = "Mouse";
    public static DummyMouseRegisteredDevice theOnlyDevice = null;

    /**
     *
     */
    private DummyMouseRegisteredDevice() {
        super(MOUSE_NAME, FRIENDLY_MOUSE_NAME);

        /* just in case, check singleton */
        if (theOnlyDevice != null)
            throw new IllegalStateException("Cannot initialize a second DummyMouseDevice - "
                    + " there can be only one mouse in the application! :) ");
    }

    public static DummyMouseRegisteredDevice getInstance() {
        if (theOnlyDevice == null)
            theOnlyDevice = new DummyMouseRegisteredDevice();
        return theOnlyDevice;
    }

    /**
     * Mouse device acts as if it was attached to all Viewers.
     * <p/>
     * @param owner Owner Viewer
     * @return always true
     */
    @Override
    public boolean isOwnedByMe(Object owner) {
        return true;
    }

    /**
     * Mouse device acts as if it was always used by someone.
     * <p/>
     * @return always true
     */
    @Override
    public boolean isUsed() {
        return true;
    }

    @Override
    public boolean isAttachable() {
        return false;
    }

    @Override
    public void getPosition(Tuple3f position) {
        throw new UnsupportedOperationException("This operation is not supported by " + getDeviceFriendlyName());
    }

    @Override
    public void getVelocity(Tuple3f velocity) {
        throw new UnsupportedOperationException("This operation is not supported by " + getDeviceFriendlyName());
    }

    @Override
    public void getTransform(Transform3D transform) {
        throw new UnsupportedOperationException("This operation is not supported by " + getDeviceFriendlyName());
    }

    @Override
    public int getButton(int no) throws Exception {
        throw new UnsupportedOperationException("This operation is not supported by " + getDeviceFriendlyName());
    }

    @Override
    public void getButtons(int[] buttons) {
        throw new UnsupportedOperationException("This operation is not supported by " + getDeviceFriendlyName());
    }

    @Override
    public int getButtonsCount() {
        throw new UnsupportedOperationException("This operation is not supported by " + getDeviceFriendlyName());
    }

    @Override
    public int getPositionMeanUpdateRate() {
        throw new UnsupportedOperationException("This operation is not supported by " + getDeviceFriendlyName());
    }

    @Override
    public int getPositionInstUpdateRate() {
        throw new UnsupportedOperationException("This operation is not supported by " + getDeviceFriendlyName());
    }

    @Override
    public String getVendor() {
        throw new UnsupportedOperationException("This operation is not supported by " + getDeviceFriendlyName());
    }

    @Override
    public String getVersion() {
        throw new UnsupportedOperationException("This operation is not supported by " + getDeviceFriendlyName());
    }

    @Override
    public String getID() {
        throw new UnsupportedOperationException("This operation is not supported by " + getDeviceFriendlyName());
    }

    /** No Java3D pointer, so this method is not used. */
    @Override
    public void close() {
        throw new UnsupportedOperationException("This operation is not supported by " + getDeviceFriendlyName());
    }

    @Override
    public DeviceType getDeviceType() {
        return DeviceType.MOUSE_DEVICE;
    }

    @Override
    protected void onStartUsing() {
        throw new UnsupportedOperationException("This operation is not supported by " + getDeviceFriendlyName());
    }

    @Override
    protected void onEndUsing() {
        throw new UnsupportedOperationException("This operation is not supported by " + getDeviceFriendlyName());
    }
}
