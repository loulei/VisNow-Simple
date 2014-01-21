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
package pl.edu.icm.visnow.lib.basic.viewers.HapticViewer3D.model.devices;

/**
 * A class implementing registering and unregistering its usage (to be used by
 * {@link DeviceManager}). It is encouraged to use this class as a base class for all devices that
 * should be managed by
 * <code>DeviceManager</code> (probably all devices that will be used by
 * <code>HapticViewer3D</code>).
 * <p/>
 * @author Łukasz Czerwiński <czerwinskilukasz1 [#] gmail.com>, ICM, University of Warsaw, 2013
 */
public abstract class BasicRegisteredDevice extends BasicDevice {

    /**
     * The Viewer3D object that uses that device in its window.
     * An Object type was used to get independent of classes hierarchy from GUI in Visnow.
     */
    private Object viewerOwner = null;

    public BasicRegisteredDevice(String deviceName_, String deviceFriendlyName_) {
        super(deviceName_, deviceFriendlyName_);
    }

    @Override
    public boolean isOwnedByMe(Object owner) {
        return viewerOwner == owner;
    }

    @Override
    public boolean isUsed() {
        return (viewerOwner != null);
    }

    @Override
    public boolean isAttachable() {
        return true;
    }

    @Override
    public void registerUsage(Object owner) throws DeviceRegisterException {
        if (isUsed()) {
            throw new DeviceRegisterException("Device '" + this.getDeviceName() + "': "
                    + "Cannot register using the device, because it's currently being used!");
        }

        this.viewerOwner = owner;
        
        try {
            onStartUsing();
        } catch (DeviceException ex) {
            throw new DeviceRegisterException(ex.getMessage());
        }

    }

    @Override
    public void assertCanUnregisterUsage(Object owner) throws DeviceRegisterException {

        if (!isUsed()) {
            throw new DeviceRegisterException("Device '" + this.getDeviceName() + "': "
                    + "Cannot unregister an unused device!");
        }

        if (!isOwnedByMe(owner)) {
            throw new DeviceRegisterException("Device '" + this.getDeviceName() + "': "
                    + "Cannot unregister a different owner than the one using this device!");
        }

        if (!isAttachable()) {
            throw new DeviceRegisterException("Device '" + this.getDeviceName() + "': "
                    + "Usage of this device cannot be modified!");
        }
    }

    @Override
    public void unregisterUsage(Object owner) throws DeviceRegisterException {
        assertCanUnregisterUsage(owner);

        try {
            onEndUsing(); // this assumes that a device is used only by one device at the time
        } catch (DeviceException ex) {
            throw new DeviceRegisterException(ex.getMessage());
        }
        
        this.viewerOwner = null;
    }

    /**
     * Called in {@link #registerUsage(java.lang.Object)} after registering the first usage (not
     * first in application life, but after some time of not being used). This method could for
     * example schedule force callback in Phantom device.
     */
    abstract protected void onStartUsing() throws DeviceException;

    /**
     * Called in {@link #unregisterUsage(java.lang.Object)} after unregistering the last usage 
     * (from now on the device will be not used in any Viewer for a time being, but it's possible
     * that after a few whiles it will be attached again). 
     * This method could be used for example to unschedule force callback in Phantom device.
     */
    abstract protected void onEndUsing() throws DeviceException;
}
