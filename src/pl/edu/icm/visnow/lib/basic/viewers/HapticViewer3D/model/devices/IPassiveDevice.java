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

import javax.media.j3d.Transform3D;
import javax.vecmath.Tuple3f;
import pl.edu.icm.visnow.lib.basic.viewers.HapticViewer3D.model.devices.mouse.DummyMouseRegisteredDevice;

/**
 * Passive 3D device (read-only parameters, no forces to be commanded - in opposite to a so called
 * active device).
 *
 * The policy of using the devices is that NO ONE should have IPassiveDevice reference if he didn't
 * claimed using this device earlier (registered its usage in {@link DeviceManager} with a reference
 * to a Viewer3D).<br/>
 * When not having a device reference, ALL actions on a device (e.g. checking whether it's used or
 * used by me etc.) should be done using String name as an identifier or {@link DeviceName} object!
 *
 * @author Krzysztof Madejski <krzysztof@madejscy.pl> ICM, University of Warsaw
 * @author modified by Łukasz Czerwiński <czerwinskilukasz1 [#] gmail.com>, ICM, University of
 * Warsaw, 2013
 */
public interface IPassiveDevice {

// <editor-fold defaultstate="collapsed" desc=" Info about the device ">    
    /**
     * Returns type of a device.
     * <p/>
     * @return type of a device - haptic, tablet, mouse or other
     */
    public DeviceType getDeviceType();

    /**
     * Gets position of the device end-point
     *
     * @param position Initialized Tuple3f object to be set
     */
    public void getPosition(Tuple3f position);

    /**
     * Returns current velocity of the device end-point
     *
     * @param velocity Initialized Tuple3f object to be set
     */
    public void getVelocity(Tuple3f velocity);

    /**
     * Return a 3D transform of the device
     *
     * @param transform Initialized Transform3D object to be set
     */
    public void getTransform(Transform3D transform);

    /**
     * Get a single button's state (attention: first button is probably number 1!).
     * True means pressed, false - otherwise.
     *
     * @param no button number (probably starts from 1!)
     * @return 1 means pressed, 0 - otherwise.
     * @throws Exception When an invalid button number is passed.
     */
    public int getButton(int no) throws Exception;

    /**
     * Fills in an array with buttons' state (true for pressed and false otherwise). First button
     * will be stored in [0]
     * index.
     * The Exception for wrong button argument should never be thrown in this method, but just in
     * case...
     *
     * @param buttons Array for buttons' state, first button will be stored in [0] index!
     */
    public void getButtons(int[] buttons);

    /**
     * Returns the number of buttons.
     *
     * @return The number of buttons
     */
    public int getButtonsCount();

    /**
     * @return position mean update rate in hertz
     */
    public int getPositionMeanUpdateRate();

    /**
     * @return position instantaneous update rate in hertz
     */
    public int getPositionInstUpdateRate();

//
//    /**
//     * Returns the bit mask of pressed buttons on the device.
//     *
//     * @return An int bit-mask representing pressed buttons
//     */
//    public int getButtonsMask();
    /**
     * Returns the device vendor.
     *
     * @return Device vendor name
     */
    public String getVendor();

    /**
     * Returns the device version. Vendor specific.
     *
     * @return Device version
     */
    public String getVersion();

    /**
     * Returns the device id or serial number.
     *
     * @return Device id or serial numver
     */
    public String getID();

    /**
     * Returns the device internal name. Possibly could be user-unfriendly, e.g. a series of random
     * letters and digits.
     *
     * @return internal name of the device
     */
    public String getDeviceName();

    /**
     * Returns a friendly name for the device.
     *
     * @return a friendly name of the device
     */
    public String getDeviceFriendlyName();

    /**
     * Closes connection with the device. Called when the device will not be used anymore (when
     * VisNow is closing).
     */
    public void close();

// </editor-fold>  
//
// <editor-fold defaultstate="collapsed" desc=" Managing the device ">    
    /**
     * Tests whether the device is used (attached) to some object (Viewer3D) or not.
     *
     * @return true when it is attached to some object, false otherwise
     */
    public boolean isUsed();

    /**
     * Can be attached and detached (start being used and end being used) by a Viewer3D.
     * Use it to check in view whether a button for attaching the device to a current Viewer3D
     * should be displayed.
     *
     * @return true if it can be, false - otherwise (for mouse it will return false - see
     *         {@link DummyMouseRegisteredDevice DummyMouseRegisteredDevice})
     *
     * @see DummyMouseRegisteredDevice
     *
     */
    public boolean isAttachable();

    /**
     * Tests whether the device is attached to the
     * <code>owner</code> object. It does NOT mean that
     * it's the only object that is using it. Currently no such method exists, but it's not needed
     * (yet?).
     *
     * @param owner Viewer3D object that uses this device. A type Object (instead of Viewer3D)
     *              was used to avoid creating
     *              a direct link between MODEL and VIEW.
     * @return true, if the device was attached to the <code>owner</code> Viewer, false otherwise
     */
    public boolean isOwnedByMe(Object owner);

//    
//    /**
//     * Can be dettached (stop being used) by a Viewer3D.
//     * Use it to check in view whether a button for attaching the device to a current Viewer3D
//     * should be clickable.
//     *
//     * @return true if it can be, false - otherwise (for mouse it will return false - see
//     *         {@link DummyMouseRegisteredDevice DummyMouseRegisteredDevice})
//     *
//     * @see DummyMouseRegisteredDevice
//     *
//     */
//    public boolean isDetachable();
//    /**
//     * For checking that the given controller is the one that uses the device.
//     * The purpose of that method is to protect the controller from IPassiveDevice from being
//     * reference by anyone and to be unregistered by anyone except for the controller itself.
//     *
//     * @param controller Controller to be checked
//     * @return true if the <code>controller</code> is the one that uses the device and false
//     *         otherwise
//     */
//    public boolean isControlledByMe(InputDevicePointerController controller);
    /**
     *
     * Registers the given controller as the only one to be using the device. It makes the device be
     * used by someone and notifies DeviceManager about that.
     *
     * @param owner      Viewer3D that is using this device
     * @throws DeviceRegisterException if there is already a registered controller (either another
     *                                 one or even this one)
     *                                 NOTE: To be used ONLY by DeviceManager!
     */
    void registerUsage(Object owner) throws DeviceRegisterException;

    /**
     * Unregisters the given controller from the device. It makes the device be
     * used by nobody and notifies DeviceManager about that.
     *
     * Note that although only exactly one controller can be registered in a device in the same
     * time, the controller must be provided in parameter. It is to enforce that no one will
     * unregister the controller except for itself.
     *
     * @throws DeviceRegisterException if the <code>controller</code> given is not the same as the
     *                                 registered one
     *                                 NOTE: To be used ONLY by DeviceManager!
     */
    void unregisterUsage(Object owner) throws DeviceRegisterException;

    /**
     *
     * Throws
     * <code>DeviceRegisterException</code> if unregistering by <owner>is not possible</owner>. If
     * everything is ok, nothing will happen.
     * <p/>
     * @param owner Object that would like to unregister using this device
     * <p/>
     * @throws DeviceRegisterException if the device is
     *                                 currently unused or
     *                                 not owned
     *                                 by <code>owner</code>
     */
    void assertCanUnregisterUsage(Object owner) throws DeviceRegisterException;

// </editor-fold> 
    public class DeviceRegisterException extends Exception {

        public DeviceRegisterException(String message) {
            super(message);
        }
    }

    public class DeviceException extends Exception {

        public DeviceException(String message) {
            super(message);
        }
    }
}
