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

import com.sun.j3d.utils.behaviors.mouse.MouseZoom;
import com.sun.j3d.utils.behaviors.sensor.SensorEvent;
import com.sun.j3d.utils.behaviors.sensor.SensorReadListener;
import com.sun.j3d.utils.behaviors.vp.WandViewBehavior;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Enumeration;
import java.util.Map.Entry;
import javax.media.j3d.Bounds;
import javax.media.j3d.Group;
import javax.media.j3d.PhysicalEnvironment;
import javax.media.j3d.Sensor;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import org.apache.log4j.Logger;
import pl.edu.icm.visnow.geometries.objects.generics.OpenBranchGroup;
import pl.edu.icm.visnow.geometries.objects.generics.OpenTransformGroup;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.pick.PickObject;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.pick.PickType;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.pick.PickTypeListenerPointer;
import pl.edu.icm.visnow.lib.basic.viewers.HapticViewer3D.IDragListener;
import pl.edu.icm.visnow.lib.basic.viewers.HapticViewer3D.ITrackerToVworldGetter;
import pl.edu.icm.visnow.lib.basic.viewers.HapticViewer3D.model.devices.IPassiveDevice;
import pl.edu.icm.visnow.lib.basic.viewers.HapticViewer3D.unused.VMouseOrbit;
import pl.edu.icm.visnow.lib.basic.viewers.HapticViewer3D.unused.VMouseRotate;
import pl.edu.icm.visnow.lib.basic.viewers.HapticViewer3D.unused.VMouseTranslate;
import pl.edu.icm.visnow.lib.basic.viewers.HapticViewer3D.unused.ViewLock;

/* 
 * TODO: consider using in various classes (HapticViewer3D etc.) always IViewPointer instead of Pointer3DViewBehavior or use an abstract class implementing only some methods.
 */
/**
 * The base 3D pointer class. Currently this is the only class implementing IViewPointer interface.
 * <p/>
 * @author Krzysztof Madejski <krzysztof@madejscy.pl> ICM, University of Warsaw
 * @author Łukasz Czerwiński <czerwinskilukasz1 [#] gmail.com>, ICM, University of
 * Warsaw, 2013
 */
public class Pointer3DViewBehavior
        extends WandViewBehavior
        implements IViewPointer, PickTypeListenerPointer, ITrackerToVworldGetter {

    /**
     * I (Łukasz Czerwiński) think that a static physEnv is a mistake, because of possible many
     * Views.
     * TODO MEDIUM: check it in Java3D documentation and probably reverse the dependency -
     * PhysicalEnvironment should be created in ViewerDeviceManager for each view once and every
     * pointer that is being attached to this view, should fetch its PhysicalEnvironment.
     */
    protected static PhysicalEnvironment physEnv = new PhysicalEnvironment();
//
    /** View object used by a Viewer3D which is displaying this pointer */
    protected View view;
//
    protected boolean pressed = false;
//
    /**
     * Set it only using setPickingState().
     */
    protected boolean pickModule3DActive = false;
//
    /** Sensor 6D that is used for reading position of a device. Device with multiple sensors was
     * never seen and never tested with this code. */
    Sensor sensor;
    /** Set it only using setLoosePointer. */
    protected boolean loosePointer = false;
    /** Transform needed for loose pointer to act correctly. */
    protected Transform3D sensorToCursorTransform = new Transform3D();
    /** Stores transform sensor coords -> vworld coords. */
    protected Transform3D sensorInVworld = new Transform3D();
//
// === used for pick 3D ===
    protected boolean pick3DPlainActive = false;
    /**
     * Set to true when a 3D pick was performed and the button still is being pressed.
     * Use to prevent from firing selecting a module pointed by 3D pointer. */
    protected boolean pick3DPlainPerformed = false;
// === ====
    //TODO UNUSED: is mouse needed here???
    //mouse
    protected ViewLock vLock = new ViewLock();
    protected VMouseRotate myMouseRotate = new VMouseRotate(vLock);
    protected VMouseOrbit myMouseOrbit = new VMouseOrbit(vLock);
    protected VMouseTranslate myMouseTranslate = new VMouseTranslate(vLock);
    protected double mouseRotateSensitivity = .004;
    protected double mouseTranslateSensitivity = .002;
    protected MouseZoom myMouseZoom = new MouseZoom();
//
    static final Logger LOGGER =
            Logger.getLogger(new Throwable().getStackTrace()[0].getClassName());
    /* 3D cursors (called "echo") */
    /** A transform group that will be displayed as an echo. It must be set as an echo BEFORE
     * start of the program. To this group a branch group with current cursor geometry will be
     * attached on runtime.
     */
    protected OpenTransformGroup echoTransformGroup = new OpenTransformGroup();
    /** A group that contains geometry for normal cursor - moving and grabing. */
    protected OpenBranchGroup echoNormal;
    /** A cursor extension showing that "knife" pick mode is active (a plain parallel to the arrow). */
    protected Pointer3DExtension echoPickPlainKnife;
    /** A cursor extension showing that "hoe" pick mode is active (a plain perpendicular to the
     * arrow). */
    protected Pointer3DExtension echoPickPlainHoe;
    /** A cursor extension showing that point pick mode is active (a small ball next to the arrow) */
    protected Pointer3DExtension echoPickPoint;
    /** A group that contains geometry for cursor used in 3D picking ("hammer" mode). */
//    protected OpenBranchGroup echoPickPlainHammer;
    /* Colors for cursors */
    /** Color for a normal cursor - no button pressed */
    static final Color3f pointerNormalColor = new Color3f(1.0f, 1.0f, 0.0f);
    /** Color when button pressed */
    static final Color3f pointerPressedColor = new Color3f(0.0f, 1.0f, 0.0f);
    /** Color when picking plain mode is on (and button is not pressed) */
    static final Color3f pointerPickPlainColor = new Color3f(0.5f, 0.5f, 1.0f);
    /** Lock for all operations using sensor, 3D pointer and variables. */
    final protected Object pointerLock = new Object();
    protected EnumMap<PickType, Integer> pickTypes = null;
    /**
     * Key listener handling pressing CTRL or SHIFT. To be initialized in constructor. It is
     * intended to be registered in
     * {@link pl.edu.icm.visnow.lib.basic.viewers.HapticViewer3D.HapticViewer3D}'s canvas by
     * {@link pl.edu.icm.visnow.lib.basic.viewers.HapticViewer3D.ViewerDeviceManager} class.
     * <p/>
     * The field was marked as final to guarantee that multiple calls to
     * <code>getKeyListener</code> will return the same object.
     */
    private final KeyListener keyListener;
    protected final Pointer3DViewBehaviorSupport support = new Pointer3DViewBehaviorSupport();

    @Override
    public void onPicksInit(final EnumMap<PickType, Integer> pickTypes) {
        this.pickTypes = pickTypes;
        if (!pickTypes.isEmpty())
            updateEchoAppearance();
    }

    /**
     * @param pickType  a type of pick that was just enabled
     * @param pickTypes a map storing number of modules handling active pick of each type. For a
     *                  type without active module
     *                  handling it there could be no key or key with value 0.
     * <p/>
     */
    @Override
    public void onPickTurnedOn(PickType pickType, final EnumMap<PickType, Integer> pickTypes) {
        this.pickTypes = pickTypes; // milimetr: not needed, because of onPicksInit stores this reference
        if (pickTypes.get(pickType) == 1) // update only when a new pick type was enabled
            updateEchoAppearance();
    }

    /**
     * @param pickType  a type of pick that was just disabled
     * @param pickTypes a map storing number of modules handling active pick of each type. For a
     *                  type without active module
     *                  handling it there could be no key or key with value 0.
     * <p/>
     */
    @Override
    public void onPickTurnedOff(PickType pickType, final EnumMap<PickType, Integer> pickTypes) {
        this.pickTypes = pickTypes; // milimetr: not needed, because of onPicksInit stores this reference
        Integer pickCount = pickTypes.get(pickType);
        if (pickCount == null || pickCount == 0) // update only if a pick type was completely disabled
            updateEchoAppearance();
    }

    public Pointer3DViewBehavior(View view,
                                 Sensor sensor,
                                 double echoSize) {
        super(sensor, null, view, new TransformGroup(), null, null); // WandViewBehavior.GNOMON, echoSize);
        this.setEchoSize(echoSize);

        if (view == null) {
            throw new IllegalArgumentException("view cannot be null!");
        }

        this.view = view;

        if (sensor == null) {
            throw new IllegalArgumentException("sensor must not be null");
        }

        keyListener = new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {

                /* Enable 3D pick */
                if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                    setPick3DPlaneState(true);
                }

                /* Enable loose pointer */
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    LOGGER.info("SHIFT pressed!");
                    if (!loosePointer && (e.getModifiersEx() & KeyEvent.SHIFT_DOWN_MASK) != 0) {
                        setLoosePointer(true); //loosePointer = true;
                    }
                }

                //TODO MEDIUM: use a modifier key instead of 'm' character
                if (e.getKeyChar() == 'm' || e.getKeyChar() == 'M')
                    switchPickModule3DActive();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                /* Disable pick 3D */
                if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                    LOGGER.info("CTRL released!");
                    setPick3DPlaneState(false);
                }

                /* Disable loose pointer */
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    LOGGER.info("SHIFT released!");
                    if (loosePointer && (e.getModifiersEx() & KeyEvent.SHIFT_DOWN_MASK) == 0) {
                        setLoosePointer(false); //loosePointer = false;
                    }
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }
        };



    }

    public static PhysicalEnvironment getPhysEnvironment() {
        return physEnv;
    }

    /**
     * Returns a key listener created in constructor. It is guaranteed that multiple calls to
     * <code>getKeyListener</code> on the same object will return the same KeyListener object.
     * <p/>
     * @return key listener (could be null, but it's not a problem
     *         for <code>canvas.addKeyListener()</code>)
     */
    public KeyListener getKeyListener() {
        return keyListener;
    }
    VisEchoReadListener6D visEchoListener;
    HapticGrabViewListener6D grabViewListener;

    @Override
    protected void configureSensorActions() {
        sensor = getSensor6D();
        if (sensor != null) {
            setButtonAction6D(0, NONE);
            setReadAction6D(NONE);

            //TODO UNUSED: mouse - delete
//            setButtonAction6D(0, TRANSLATE_FORWARD);  // milimetr
//            setReadAction6D(ECHO);  // milimetr

            //mouse  - from revision 4336
        /*
             MouseInputDevice mid = new MouseInputDevice(view.getCanvas3D(0));
             mid.initialize();
             view.getPhysicalEnvironment().addInputDevice(mid);
             Sensor2D(new Object[]{mid.getSensor(0)});
             setReadAction2D(NONE);
             setButtonAction2D(0, TRANSLATION);
             setButtonAction2D(1, ROTATION);
             //setScaleSpeed(NONE, BEAM);
             setTranslationSpeed(1, VIRTUAL_UNITS, PER_SECOND);
             setRotationSpeed(90, DEGREES, PER_SECOND);
             */

            super.configureSensorActions();

            //set our listeners
            visEchoListener = new VisEchoReadListener6D();
            getSensorEventAgent().addSensorReadListener(sensor,
                                                        visEchoListener);

            grabViewListener = new HapticGrabViewListener6D(this);
            getSensorEventAgent().addSensorButtonListener(sensor,
                                                          0,
                                                          grabViewListener);

        }
    }

    //TODO UNUSED: mouse in Pointer3DViewBehavior is not used - delete 
    @Override
    public void setSchedulingBounds(Bounds region) {
        if (myMouseRotate != null) {//TODO: it seems that myMouseRotate is never null!
            myMouseRotate.setSchedulingBounds(region);
            myMouseTranslate.setSchedulingBounds(region);
            myMouseZoom.setSchedulingBounds(region);
        }
        super.setSchedulingBounds(region);
    }

    /**
     *
     * Creates pointer arrows. This method is called in initialize() method.
     *
     * @see <a
     * href="http://java3d.sourcearchive.com/documentation/1.5.2plus-pdfsg-3/WandViewBehavior_8java-source.html#l00863">Java
     * source code for parent method</a>
     * @see <a
     * href="http://java3d.sourcearchive.com/documentation/1.5.2plus-pdfsg-3/SensorGnomonEcho_8java-source.html#l00064">SensorGnomonEcho
     * geometry</a>
     */
    @Override
    protected void configureEcho() {
//        super.configureEcho();

        /* Create all needed pointers */
        float echoSize = (float) getEchoSize();
        echoPickPlainKnife = new Pointer3DExtensionSliceKnife(echoSize);
        echoPickPlainHoe = new Pointer3DExtensionSliceHoe(echoSize);

        float radius = echoSize / 5;
        echoPickPoint = new Pointer3DExtensionPoint(radius, 2 * radius, radius, radius);
//        echoPickPlainHammer = new SlicePointerArrow3DHammer((float) getEchoSize());
        echoNormal = new Pointer3DBasicArrow((float) getEchoSize());
        echoTransformGroup.addChild(echoNormal);
        setEchoTransformGroup(echoTransformGroup);

        updateEchoAppearance();

//        getEchoGeometry().setPickable(false); // will be null for custom echo
//        getEchoGeometry().setPickable(true);  // milimetr
    }

    /**
     * Updates shape and color of echo (3D cursor) according to current situation (pressed/not
     * pressed, pick 3D active or not).
     */
    protected void updateEchoAppearance() {

        echoTransformGroup.removeAllChildren();
        echoTransformGroup.addChild(echoNormal);

        if (pickTypes != null) {
            /* pickTypes could be null only when onPicksInit() was not yet called 
             * (updateEchoAppearance() was called by u.getViewingPlatform().setViewPlatformBehavior(currentPointer3D) ). 
             * Later it should always be not null.
             */

            for (Entry<PickType, Integer> entry : pickTypes.entrySet()) {
                Integer count = entry.getValue();
                if (count > 0) {
                    PickType pickType = entry.getKey();
                    OpenBranchGroup pointerExt = null;
                    switch (pickType) {
                        case PLAIN_KNIFE:
                            pointerExt = echoPickPlainKnife;
                            break;
                        case PLAIN_HOE:
                            pointerExt = echoPickPlainHoe;
                            break;
                        case POINT:
                            pointerExt = echoPickPoint;
                    }
                    if (pointerExt != null)
                        echoTransformGroup.addChild(pointerExt);
                }
            }
        }


        Color3f color;
        if (pressed) {
            color = pointerPressedColor;
        } else {
            if (pick3DPlainActive)
                color = pointerPickPlainColor;
            else
                color = pointerNormalColor;
        }
        setEchoColor(color);
        setEchoTransparency(0);
    }

    @Override
    public void setEchoColor(Color3f color) {
        super.setEchoColor(color);

        if (echoTransformGroup == null)
            return;
        setColorTransformGroup(echoTransformGroup, color);
    }

    private void setColorTransformGroup(Group group, Color3f color) {

        Enumeration e = group.getAllChildren();

        while (e.hasMoreElements()) {
            Object el = e.nextElement();
            /* element el should be either Shape3D or Group (BranchGroup or TransformGroup) */
            if (el instanceof Shape3D) {
                Shape3D shape = (Shape3D) el;
                shape.getAppearance().getMaterial().setDiffuseColor(color);
            } else {
                Group g = (Group) el;
                setColorTransformGroup(g, color);
            }
        }
    }

    /**
     * Initializator loaded automatically by Java3D.
     */
    @Override
    public void initialize() {

        //mouse
        myMouseOrbit.setFactor(mouseRotateSensitivity);
        myMouseRotate.setFactor(mouseRotateSensitivity);
        myMouseTranslate.setFactor(mouseTranslateSensitivity);
        myMouseRotate.setTransformGroup(targetTG);
        myMouseOrbit.setTransformGroup(targetTG);
        myMouseTranslate.setTransformGroup(targetTG);

        myMouseOrbit.setSchedulingBounds(getSchedulingBounds());
        myMouseRotate.setSchedulingBounds(getSchedulingBounds());
        myMouseTranslate.setSchedulingBounds(getSchedulingBounds());
        myMouseZoom.setSchedulingBounds(getSchedulingBounds());
//TODO UNUSED: mouse - delete
        /*
         javax.media.j3d.BranchGroup mouseBehaviours = new BranchGroup();
         mouseBehaviours.addChild(myMouseOrbit);
         mouseBehaviours.addChild(myMouseTranslate);
         getViewingPlatform().addChild(mouseBehaviours);
         */

        if (getSensor6D() != null) {
            configureEcho();
        }

        super.initialize();

    }

    public void initializeTargetTG(OpenTransformGroup objRotate) {
        targetTG = objRotate;
    }

    @Override
    public void pointerActivate(IPassiveDevice pointer) {
    }

    private void setLoosePointer(boolean value) {
        if (loosePointer != value) {
            loosePointer = value;
            updateEchoAppearance();
            firePointerStateChanged();
        }
    }

    @Override
    public boolean isPickModule3DActive() {
        return pickModule3DActive;
    }

    /** Switches value of {@link #pickModule3DActive}, updates 3D pointer and notifies
     * <code>PointerStateListener</code>s */
    private void switchPickModule3DActive() {
        pickModule3DActive = !pickModule3DActive;
        LOGGER.info("picking a module set to: " + String.valueOf(pickModule3DActive));

        updateEchoAppearance();
        firePointerStateChanged();
    }

    private void setPick3DPlaneState(boolean value) {
        if (pick3DPlainActive != value) {
            pick3DPlainActive = value;
            if (pick3DPlainActive)
                pick3DPlainPerformed = false;
            updateEchoAppearance();
            firePointerStateChanged();
        }
    }

    private void setPressed(boolean value) {
        if (pressed != value) {
            pressed = value;
            if (!pressed)
                pick3DPlainPerformed = false;
            updateEchoAppearance();
            firePointerStateChanged();
        }
    }

    @Override
    public boolean getLoosePointer() {
        return loosePointer;
    }
///* === PointerStateListener - used for displaying pickModule3DActive and loosePointer in GUI panel === */
    private ArrayList<PointerStateListener> pointerStateListeners =
            new ArrayList<PointerStateListener>();

    @Override
    public void addPointerStateListener(PointerStateListener l) {
        pointerStateListeners.add(l);
        l.pointerStateChanged();
    }

    public void removePointerStateListener(PointerStateListener l) {
        pointerStateListeners.remove(l);
    }

    public void firePointerStateChanged() {
        for (PointerStateListener l : pointerStateListeners) {
            l.pointerStateChanged();
        }
    }
//
    /** PickPlainListener - for notifying that a plain was picked */
    private PickObject.PickPlainListener pickPlainListener = null;
//

    /** Sets pick listener to a given object. Pass null to remove current listener or call
     * <code>removePickPlainListener()</code>. */
    @Override
    public void setPickPlainListener(PickObject.PickPlainListener l) {
        pickPlainListener = l;
    }

    /** Removes pick listener. */
    @Override
    public void removePickPlainListener() {
        setPickPlainListener(null);
    }
//
    /** PickModuleListener - for notifying that a module was picked
     * (a point in 3D was picked and PickObject must select a module that rendered the geometry
     * that was just picked)
     */
    private PickObject.PickModuleListener pickModuleListener = null;

    /** Sets pick listener to a given object. Pass null to remove current listener or call
     * <code>removePickPlainListener()</code>. */
    @Override
    public void setPickModuleListener(PickObject.PickModuleListener l) {
        pickModuleListener = l;
    }

    /** Removes pick module listener. */
    @Override
    public void removePickModuleListener() {
        setPickModuleListener(null);
    }

    /**
     * Process picking a plain (3D pick). An event will be fired iff picking a plainis active (
     * <code>pick3DPlainActive</code> is true).
     * <p/>
     * @param sensorInVworld a transform containing position and rotation of the 3D pointer
     * @return true if an event for 3D pick was fired, false - otherwise
     */
    public boolean processPlain3DPicked(Transform3D sensorInVworld) {
        if (pick3DPlainActive) {
            firePlainPicked(sensorInVworld);
            return true;
        } else
            return false;
    }

    /**
     * Notifies pick plain listener (object of a PickObject class) that pick 3D has occured.
     * <p/>
     * @param sensorInVworld position and rotation of sensor in Vworld coordinates
     */
    protected void firePlainPicked(Transform3D sensorInVworld) {
        pick3DPlainPerformed = true;
        if (pickPlainListener != null) {
            Point3d position = new Point3d();
            sensorInVworld.transform(position);
            LOGGER.info("Plain picked! \n Transform: \n" + sensorInVworld + "\nPoint: " + position); // + ", \nrotationMatrix: \n" + rotationMatrix);
            PickObject.PickPlainEvent e = new PickObject.PickPlainEvent(sensorInVworld);
            pickPlainListener.plainPicked(e);
        }
    }

    /**
     * Process pick selecting a module. An event will be fired iff picking module 3D is active (
     * <code>pickModule3DActive</code> is true) and if picking a plain wasn't performed yet (prevent
     * from firing two events when both picking a module and picking a plain is active).
     * <p/>
     * @param sensorInVworld a transform containing position and rotation of the 3D pointer
     * @return true if an event for module picking was fired, false - otherwise
     */
    public boolean processModule3DPicked(Transform3D sensorInVworld) {
        // if didn't drag (so pressed) and no pick 3D was performed, perform a module pick
        if (!pick3DPlainPerformed && pickModule3DActive) {
            fireModulePointPicked(sensorInVworld);
            return true;
        } else
            return false;
    }

    protected void fireModulePointPicked(Transform3D sensorInVworld) {
        if (pickModuleListener != null) {
            Point3d position = new Point3d();
            sensorInVworld.transform(position);
            LOGGER.info("Module point picked! \n Transform: \n" + sensorInVworld + "\nPoint: " + position); // + ", \nrotationMatrix: \n" + rotationMatrix);
            PickObject.PickModuleEvent e = new PickObject.PickModuleEvent(sensorInVworld);
            pickModuleListener.modulePointPicked(e);
        }
    }

    /**
     * Read position from the sensor and stores it in
     * <code>out_sensorInVworld</code>. It takes into account
     * <code>sensorToCursorTransform</code> transform, which stores "correction" needed because
     * of the loose pointer.
     * <p/>
     * @param s                  sensor whose position will be read
     * @param out_sensorInVworld transform which will store position of the cursor
     */
    protected void getSensorInVworld(Sensor s, Transform3D out_sensorInVworld) {
        view.getSensorToVworld(s, out_sensorInVworld);

//            
//            s.getRead(sensorToTracker);
//            sensorInVworld.mul(trackerToVworld, sensorToTracker);
        out_sensorInVworld.mul(sensorToCursorTransform, out_sensorInVworld); // apply correction related to loose pointer
    }
    private static Sensor dummySensor = new Sensor(null) {
        @Override
        public void getRead(Transform3D read) {
            read.setIdentity();
        }
    };

    /**
     * Gets a device(=tracker)-to-vworld transform and stores it in
     * <code>out_sensorInVworld</code>. It takes into account
     * <code>sensorToCursorTransform</code> transform, which stores "correction" needed because
     * of the loose pointer.
     * <p/>
     * Uses a dummy sensor to get tracker-to-vworld trasform instead of sensor's position in vworld
     * matrix.
     * <p/>
     * @param out_trackerToVworld transform which will store position of the cursor
     */
    @Override
    public void getTrackerToVworld(Transform3D out_trackerToVworld) {
        view.getSensorToVworld(dummySensor, out_trackerToVworld);
        out_trackerToVworld.mul(sensorToCursorTransform, out_trackerToVworld); // apply correction related to loose pointer
    }

//TODO UNUSED: PointerStateListener - DELETE it or attach GUI
    public interface PointerStateListener {

        void pointerStateChanged();
    }

    /* === end of PointerStateListener === */
    /**/
    public class VisEchoReadListener6D implements SensorReadListener {

        protected boolean lastLoosePointer = false;
        Transform3D tempTransform = new Transform3D();

        @Override
        public void read(SensorEvent e) {
            synchronized (pointerLock) {

                if (lastLoosePointer && !loosePointer) {
                    /* Loose pointer mode has been just disabled, so update sensorToCursorTransform 
                     * transform to reflect the new position - thanks to that cursor will not return 
                     * to the position before enabling loose pointer mode.
                     * 
                     * This will not change the rotation of a cursor.
                     */

                    lastLoosePointer = loosePointer;

                    //S_new = S * D1 * D2^-1 
                    //XXX: what is this formula for?? It doesn't reflect the code below :/
                    Transform3D tmp = new Transform3D();
                    Vector3d u = new Vector3d();
                    Vector3d v = new Vector3d();
                    Sensor s = e.getSensor(); // this should be the very same sensor as sensor variable in Pointer3DViewBehavior
                    view.getSensorToVworld(s, tmp);
                    tmp.get(u);
                    sensorInVworld.get(v);
                    v.sub(u);
                    sensorToCursorTransform.set(v);
                }
                lastLoosePointer = loosePointer;

                if (loosePointer) {
                    return;
                }


                Sensor s = e.getSensor();

                // set hotspot to a non-zero value - TODO: is that needed? probably it should be done once
                s.setHotspot(new Point3d(0, 0, 0.25));

                // read buttons
                int num = s.getSensorButtonCount();
                int[] btns = new int[num];
                s.lastButtons(btns);

                // read position and forward it to Java3D 3D device cursor (update position of the echo)
                getSensorInVworld(s, sensorInVworld);
                updateEcho(s, sensorInVworld);

//                echoTransformGroup.getLocalToVworld(tempTransform);
//                LOGGER.info("echoTransformGroup.getLocalToVworld(): \n" + tempTransform);
            }
        }
//
    }

    /**
     * Deals with rotating the scene - grabbing it by a haptic device.
     * <p/>
     * @author Krzysztof Madejski <krzysztof@madejscy.pl> ICM, University of Warsaw
     */
    public class BaseGrabViewListener6D extends GrabViewListener6D {

        protected Transform3D t3d = new Transform3D();
        protected Transform3D initialVworldToSensor = new Transform3D();
        //
        protected boolean dragged = false;
        //
        /** Should the transform be inverted. For haptic device certainly not - when pushing the
         * device handle, the object dragged should be also moved inwards, not towards the
         * screen;
         * the same left and right - should be the same in haptic handle and on the screen */
        protected boolean invert = false;

        public BaseGrabViewListener6D() {
        }

        public BaseGrabViewListener6D(boolean invert) {
            this.invert = invert;
        }

        /**
         * After initAction from WandViewBehavior we must correct sensorInVworld coordinates
         * (must reflect moves done using loose pointer).
         * <p/>
         * @param s sensor that is starting an action
         */
        @Override
        protected void initAction(Sensor s) {
            super.initAction(s);

            /* Update sensorInVworld transform 
             * (in super.initAction() sensorToCursorTransform is of course not handled). */
            getSensorInVworld(s, sensorToVworld);
        }

        @Override
        public void pressed(SensorEvent e) {
            synchronized (pointerLock) {

                support.fireDragEvent(new IDragListener.DragEvent(true));

                initAction(e.getSensor());  // call first to call initAction and to get current sensorInVworld value
                // this should be the very same sensor as sensor variable in Pointer3DViewBehavior

                dragged = false;
                setPressed(true);

                // Save the inverse of the initial sensorInVworld (copied from GrabViewListener6D)
                initialVworldToSensor.invert(sensorToVworld);

                // if pick 3D is active, fire event!
                if (pick3DPlainActive) {

                    Transform3D pickTransform = new Transform3D(sensorToVworld);

                    firePlainPicked(pickTransform);
                }

            }
        }

        /**
         * Most of the code is copied from GrabViewListener6D (Java3D source code).
         * Only the "if (invert)" check is added by me (in Java3D the inversion was without a
         * condition).
         */
        @Override
        public void dragged(SensorEvent e) {
            synchronized (pointerLock) {
                dragged = true;

                // Get sensor read relative to the static view at the time of the
                // button-down.
                Sensor s = e.getSensor(); // this should be the very same sensor as sensor variable in Pointer3DViewBehavior
                getSensorInVworld(s, sensorToVworld);

                // Solve for T, where T x initialSensorToVworld = sensorInVworld
                t3d.mul(sensorToVworld, initialVworldToSensor);

                // Move T to the view side by inverting it, and then applying it
                // to the static view transform.
                if (invert)
                    t3d.invert();
                t3d.mul(viewPlatformToVworld);
                targetTG.setTransform(t3d);

                updateEcho(s, sensorToVworld);
            }
        }

        @Override
        public void released(SensorEvent e) {
            synchronized (pointerLock) {
                processModule3DPicked(sensorToVworld);
                setPressed(false);
                super.released(e); // calls endAction()

                support.fireDragEvent(new IDragListener.DragEvent(false));
            }
        }
    }

    /**
     * Handles dealing with forces when dragging. Rotation transform is dealt by
     * BaseGrabViewListener6D
     * <p/>
     * @author Krzysztof Madejski <krzysztof@madejscy.pl> ICM, University of Warsaw
     */
    public class HapticGrabViewListener6D extends BaseGrabViewListener6D {

        private Pointer3DViewBehavior pointer;

        public HapticGrabViewListener6D(Pointer3DViewBehavior pointer) {
            super(false); // do not invert the transformation
            this.pointer = pointer;
        }

        @Override
        public void pressed(SensorEvent e) {
            synchronized (pointerLock) {
                super.pressed(e); // should be fired first because of initAction() fired in it
                //earlier here forces from force context were disabled
            }
        }

        @Override
        public void dragged(SensorEvent e) {
            synchronized (pointerLock) {
                super.dragged(e);
            }
        }

        @Override
        public void released(SensorEvent e) {
            synchronized (pointerLock) {

                pointer.setEchoColor(pointerNormalColor); //TODO: try to delete it from here - colors should be only in updateEcho()

                super.released(e); // calls endAction(), not sure whether it should be the last call or something could be called later
            }
        }
    }

    /* ========= support for IDragListener =========== */
    public void addDragListener(IDragListener listener) {
        support.addDragListener(listener);
    }

    public void removeDragListener(IDragListener listener) {
        support.removeDragListener(listener);
    }
}
//revised.