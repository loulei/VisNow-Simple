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
 * A device class that is encouraged to be used as a base class for all concrete devices (to be more
 * precise: use {@link BasicRegisteredDevice} as a base class).
 * <p/>
 * It handles storing internal device name (used by an underlying device library) and device
 * friendly name which will be shown to the user.
 * <p/>
 * @author Łukasz Czerwiński <czerwinskilukasz1 [#] gmail.com>, ICM, University of Warsaw, 2013
 */
public abstract class BasicDevice implements IPassiveDevice {

    /**
     * Internal device name to be used by underlying device libraries for identifying the device.
     */
    protected String deviceName;
    /**
     * A friendly name to be displayed to the user.
     */
    protected String deviceFriendlyName;

    BasicDevice(String deviceName_, String deviceFriendlyName_) {
        this.deviceName = deviceName_;
        this.deviceFriendlyName = deviceFriendlyName_;
    }

    @Override
    public String getDeviceName() {
        return deviceName;
    }

    @Override
    public String getDeviceFriendlyName() {
        return deviceFriendlyName;
    }
    

}
