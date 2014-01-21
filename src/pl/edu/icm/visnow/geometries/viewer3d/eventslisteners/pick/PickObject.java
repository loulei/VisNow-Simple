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

import com.sun.j3d.utils.pickfast.PickCanvas;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EventListener;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.IndexedLineStripArray;
import javax.media.j3d.Node;
import javax.media.j3d.PickCylinderSegment;
import javax.media.j3d.PickInfo;
import javax.media.j3d.PickShape;
import javax.media.j3d.Transform3D;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import org.apache.log4j.Logger;
import pl.edu.icm.visnow.application.application.Application;
import pl.edu.icm.visnow.application.frames.tabs.ModulesGUIPanel;
import pl.edu.icm.visnow.geometries.objects.GeometryObject;
import pl.edu.icm.visnow.geometries.objects.GeometryParent;
import pl.edu.icm.visnow.geometries.objects.generics.OpenBranchGroup;
import pl.edu.icm.visnow.geometries.objects.generics.OpenShape3D;
import pl.edu.icm.visnow.geometries.geometryTemplates.Glyph;
import pl.edu.icm.visnow.geometries.geometryTemplates.Templates;
import pl.edu.icm.visnow.geometries.objects.CellSetGeometry;
import pl.edu.icm.visnow.geometries.utils.transform.LocalToWindow;
import pl.edu.icm.visnow.geometries.viewer3d.ObjRotate;
import pl.edu.icm.visnow.lib.templates.visualization.modules.VisualizationModule;
import pl.edu.icm.visnow.lib.utils.numeric.NumericalMethods;
import pl.edu.icm.visnow.lib.basic.viewers.HapticViewer3D.controller.pointer3d.IViewPointer;

/**
 * Class handling picking activities - it handles both creating geometry and drawing it on the scene
 * and notifying modules of a pick event.
 * <p>Emulated 3D pick is a point in 3D.</p>
 * <p>Device 3D pick event is either a point in 3D or a point in 3D and 4x4 matrix storing
 * coordinates (position and rotation) of a 3D pointer (if device enables that). <br/>
 * <p/>
 * First column of the rotation matrix stores vector parallel to X axis of a sensor's
 * pointer, second - Y axis, third - Z axis. One can use the following code to display those 3
 * vectors:</p>
 * <pre>
 *          float[] point = e.getPoint();
 *          Point3f p0 = new Point3f(point);
 *          for (int i = 0; i < 3; ++i) {
 *              Point3f p1 = new Point3f(p0);
 *              rotation.getColumn(i, v);
 *              pts[i].add(v);
 * <p></p>
 *              // here add line p0-p1
 *          }
 * </pre>
 * <p/>
 * @author Krzysztof S. Nowinski University of Warsaw, ICM
 * @author modified by Łukasz Czerwiński <czerwinskilukasz1 [#] gmail.com>, ICM, University of
 * Warsaw, 2013
 */
public class PickObject implements PickTypeListener, PointerChangeListener {

    private static final Logger LOGGER =
            Logger.getLogger(new Throwable().getStackTrace()[0].getClassName());
    protected boolean active = false;
    protected boolean pickDone = false;
//
    protected GeometryParent parent = null;
    protected String name = "3D pick";
    protected GeometryObject outObj = new GeometryObject(name);
    protected GeometryObject currentObj = null;
//
//
    /** Picking line. */
    protected MyPoint3d[] line = null;
    /** Coordinates of a picked point. */
    protected MyPoint3d pickedPoint = null;
    /** Picked plain. */
    protected Transform3D sensorInLocalCoords = new Transform3D();
//
    /**
     * A diamond glyph which marks one end of the picking line in the first phase of picking and
     * the picked point in the second phase.
     */
    protected Glyph gt = new Templates.DiamondTemplate(0);
    protected float scale = .1f;
    protected IndexedLineStripArray glyph = null;
    protected int nvertl, nvertp, nindl, nindp;
    protected int[] stripsl, stripsp, pIndexl, pIndexp, cIndexl, cIndexp;
    protected double[] vertsl, vertsp;
    /** Color of the picking line (blue). */
    protected float[] colors = {.3f, .6f, 1.f};
    /** Color of the result point (green). */
    protected float[] resultColors = {.1f, 1.f, .0f};
//
    protected Canvas3D canvas;
    protected BranchGroup rootGeomObject;
    protected ObjRotate objRotate;
    /** Used to remember rotation of the scene before creating a pick line.
     * This transform is restored after a point was picked. */
    protected Transform3D pickTransform = new Transform3D();
    /** Lock object for setPick() */
    protected final Object pickLock = new Object();
//
// === variables for selecting a module when clicking on geometry object in Viewer ===
    protected PickCanvas pickCanvas;
    private Application application = null;

    /**
     *
     * @param canvas         current canvas
     * @param objScene       for picking a module when clicking on geometry object
     * @param appGUIPanel    current application panel - to bring the module responsible for the
     *                       picked object up
     * @param rootGeomObject
     * @param objRotate
     */
    public PickObject(Canvas3D canvas, OpenBranchGroup objScene,
                      OpenBranchGroup rootGeomObject, ObjRotate objRotate) {
        if (canvas == null)
            throw new IllegalArgumentException("canvas cannot be null");

        if (rootGeomObject == null)
            throw new IllegalArgumentException("rootGeomObject cannot be null");

        if (objRotate == null)
            throw new IllegalArgumentException("objRotate cannot be null");


        this.pickCanvas = new PickCanvas(canvas, objScene);
        this.pickCanvas.setMode(PickInfo.PICK_GEOMETRY);
        this.pickCanvas.setFlags(PickInfo.NODE | PickInfo.CLOSEST_INTERSECTION_POINT);

        this.canvas = canvas;
        this.rootGeomObject = rootGeomObject;
        this.objRotate = objRotate;
//
        initArrays();
    }

    /** Constructs arrays used later in pickRayChanged to draw diamond shapes. */
    private void initArrays() {
        nvertl = 2 * gt.getNverts() + 2;
        int nstripl = 2 * gt.getNstrips() + 1;
        nindl = 2 * gt.getNinds() + 2;
        stripsl = new int[nstripl];
        for (int i = 0; i < gt.getNstrips(); i++) {
            stripsl[i] = stripsl[i + gt.getNstrips()] = gt.getStrips()[i];
        }
        stripsl[2 * gt.getNstrips()] = 2;
        pIndexl = new int[nindl];
        cIndexl = new int[nindl];
        for (int i = 0; i < gt.getNinds(); i++) {
            pIndexl[i] = gt.getPntsIndex()[i];
            pIndexl[i + gt.getNinds()] = gt.getPntsIndex()[i] + gt.getNverts();
        }
        pIndexl[2 * gt.getNinds()] = 2 * gt.getNverts();
        pIndexl[2 * gt.getNinds() + 1] = 2 * gt.getNverts() + 1;
        for (int i = 0; i < nindl; i++) {
            cIndexl[i] = 0;
        }
        vertsl = new double[3 * nvertl];

        nvertp = gt.getNverts();
        int nstripp = gt.getNstrips();
        nindp = gt.getNinds();
        stripsp = new int[nstripp];
        System.arraycopy(gt.getStrips(), 0, stripsp, 0, gt.getNstrips());
        pIndexp = new int[nindp];
        cIndexp = new int[nindp];
        System.arraycopy(gt.getPntsIndex(), 0, pIndexp, 0, gt.getNinds());
        for (int i = 0; i < nindp; i++) {
            cIndexp[i] = 0;
        }
        vertsp = new double[3 * nvertp];
    }
//

    /**
     * @return the application
     */
    public Application getApplication() {
        return application;
    }

    /**
     * @param application the application to set
     */
    public void setApplication(Application application) {
        this.application = application;
    }

//
// ================== PICKING A PLAIN =====================
    public static interface PickPlainListener extends EventListener {

        void plainPicked(PickPlainEvent e);
    }

    /* === Picking a 3D plain - event === */
    /** Used for communication between 3D pointer (Pointer3DViewBehavior) and PickObject. It signals
     * a PickObject object that a plane was picked and the event should be sent to modules with a
     * Pick3DListener. */
    public static class PickPlainEvent {

        protected Transform3D sensorInVworld;

        public PickPlainEvent(Transform3D sensorInVworld) {
            this.sensorInVworld = sensorInVworld;
        }

        public Transform3D getSensorInVworld() {
            return sensorInVworld;
        }
    }
//
//
    protected PickPlainListener pickPlainListener = new PickPlainListener() {
        @Override
        public void plainPicked(PickPlainEvent e) {
            consumeDevice3DPick(e.getSensorInVworld());
        }
    };

    /**
     * Sets pick 3D data in PickObject. Should be called after picking a plane using a 3D pointing
     * device.
     * This method computes coordinates in local coordinates of
     * <code>rootObject</code> geometry, stores it in this object and fires a Pick3DEvent.
     * <p/>
     * @param sensorInVworld transform consisting of all data about sensor position and rotation (in
     *                       vworld coordinates)
     */
    public void consumeDevice3DPick(Transform3D sensorInVworld) {

        /* get Vworld to local transformation */
        Transform3D _sensorInLocalCoords = new Transform3D();
        Transform3D VworldToLocal = new Transform3D();
        rootGeomObject.getLocalToVworld(VworldToLocal);
        VworldToLocal.invert();

        /* calculate sensor in local coordinates */
        _sensorInLocalCoords.mul(VworldToLocal, sensorInVworld);


        MyPoint3d position = new MyPoint3d();
        _sensorInLocalCoords.transform(position);
        setPick(position, _sensorInLocalCoords);
    }
// ================== PICKING A MODULE =====================

    public static interface PickModuleListener extends EventListener {

        /** Handles picking a 3D point, the call should result in selecting a module that generated
         * the geometry that is the closest from the picked 3D point. */
        void modulePointPicked(PickModuleEvent e);
    }

    /* === Picking a module - event === */
    /** Used for communication between 3D pointer (Pointer3DViewBehavior) and PickObject. It signals
     * a PickObject object that a plain was picked and the event should be sent to modules with a
     * Pick3DListener. */
    public static class PickModuleEvent {

        protected Transform3D sensorInVworld;

        public PickModuleEvent(Transform3D sensorInVworld) {
            this.sensorInVworld = sensorInVworld;
        }

        public Transform3D getSensorInVworld() {
            return sensorInVworld;
        }
    }
//
//
    protected PickModuleListener pickModuleListener = new PickModuleListener() {
        @Override
        public void modulePointPicked(PickModuleEvent e) {
            setPickModulePoint(e.getSensorInVworld());
        }
    };

    public void consume2DSelectModule(int X, int Y) {
        pickCanvas.setShapeLocation(X, Y);
        handleSelectModule();
    }

    /**
     * SHOULD BE CALLED ONLY BY consume2DSelectModule and consume3DSelectModule.
     */
    protected void handleSelectModule() {
        PickInfo[] pickInfos = pickCanvas.pickAllSorted();
        if (pickInfos != null) {
            String pickedCSName = "";
            Point3d pickPoint = new Point3d();
            pickCheck:
            for (PickInfo pickInfo : pickInfos) {
                Node pickedNode = pickInfo.getNode();
                pickPoint = pickInfo.getClosestIntersectionPoint();
                while (pickedNode != null) {
                    if (pickedNode instanceof CellSetGeometry) {
                        pickedCSName = ((CellSetGeometry) pickedNode).getName();
                        ((CellSetGeometry) pickedNode).flipPicked();
                    }
                    if (pickedNode.getUserData() != null) {
                        LOGGER.info("" + pickedNode.getUserData() + " "
                                + "[" + pickPoint.x + "," + pickPoint.y + "," + pickPoint.z + "]");
                        if (pickedNode.getUserData() instanceof VisualizationModule.ModuleIdData) {
                            VisualizationModule.ModuleIdData vmId =
                                    (VisualizationModule.ModuleIdData) pickedNode.getUserData();
                            VisualizationModule vm = vmId.getModule();
                            if(application != null)
                                application.getFrames().getGuiPanel().selectModule(vmId.getModuleId());
                            vm.processPickInfo(pickedCSName, pickInfo);
                            currentObj = vm.getOutObject();
                        }
                        break pickCheck;
                    }
                    pickedNode = pickedNode.getParent();
                }
            }
            this.setPoint(pickPoint);
        }
    }
//
//

    protected void setPickModulePoint(Transform3D sensorInVworld) {
        Point3d position = new Point3d();
        sensorInVworld.transform(position);

        consume3DSelectModule(position);
    }

    protected void consume3DSelectModule(Point3d position) {
        switch (moduleMode) {
            case 1:
                double sphereRadius = 0.2;
//                PickBounds pickBounds = new PickBounds(new BoundingSphere(position, sphereRadius));
//                pickCanvas.setShape(pickBounds, new Point3d(0, 0, 0));
                pickCanvas.setShapeBounds(new BoundingSphere(position, sphereRadius), new Point3d(0, 0, 0));
                break;

            case 2: {
                double cylinderRadius = 0.2; //TODO: change to size in Vworld that correspond to the tolerance in pick2Dmodule
                Point3d end = (Point3d) position.clone();
                end.z += cylinderRadius;

                PickShape pickShape = new PickCylinderSegment(position, end, cylinderRadius);
                pickCanvas.setShape(pickShape, position);
            }
            break;

            case 3: {
                double cylinderRadius = 0.2; //TODO: change to size in Vworld that correspond to the tolerance in pick2Dmodule
                Point3d end = (Point3d) position.clone();
                end.z += cylinderRadius;

                PickShape pickShape = new PickCylinderSegment(position, end, cylinderRadius);
                pickCanvas.setShape(pickShape, new Point3d(0, 0, 0));
            }
            break;

        }
        handleSelectModule();
    }
    protected int moduleMode = 2;

    public void setPickModuleMode(int i) {
        switch (i) {
            case 1:
                LOGGER.info("Pick module mode switched to: sphere");
                moduleMode = 1;
                break;
            case 2:
                LOGGER.info("Pick module mode switched to: cylinder with offset");
                moduleMode = 2;
                break;
            case 3:
                LOGGER.info("Pick module mode switched to: cylinder 0,0");
                moduleMode = 3;
                break;
        }
    }

// ========== EMULATED 3D PICK - 3D pick using a 2D device (mouse) ================================
    /**
     *
     * @param X X coord of the clicked point
     * @param Y Y coord of the clicked point
     * @return true if pick was consumed, false - otherwise
     */
    public boolean consumeEmulated3DPick(int X, int Y) {
        if (!isActive())
            return false;

        MyPoint3d[] pickedLine = computeLineCoords(X, Y);
        if (line == null) {  // first phase of picking: drawing a line
            pickDone = true;

            line = pickedLine;
            createPickLineGeometry();

            objRotate.getTransform(pickTransform);
        } else { // second phase of picking: picking a point on the line
            computePickedPoint(pickedLine);
            createPickPointGeometry(pickedLine);
            firePickChanged();

            pickDone = true;
            objRotate.setTransform(pickTransform);
        }

        return true;
    }
//
// =================================
//

    public boolean isPickLineActive() {
        return line != null;
    }

    public GeometryObject getOutObject() {
        return outObj;
    }

    /**
     * Sets a picked point to
     * <code>point</code>. To be called after picking a point by mouse.
     * <p/>
     * @param point picked point
     */
    public void setPoint(Point3d point) {
        setPick(new MyPoint3d(point), null);
    }

    /**
     * This should be the only method that modifies position of a picked point and transform of a 3D
     * device cursor. After that it fires firePickChanged() to notify all modules about changes.
     * <p/>
     * @param point               picked point
     * @param sensorInLocalCoords transform describing position and rotation of a 3d cursor, set to
     *                            null if not applicable (mouse pick)
     */
    protected void setPick(MyPoint3d point, Transform3D sensorInLocalCoords) {
        synchronized (pickLock) {
            this.pickedPoint = new MyPoint3d(point);
            this.sensorInLocalCoords = sensorInLocalCoords;
            firePickChanged();
        }
    }
    /**
     * Utility field holding list of PickListeners.
     */
    protected final transient ArrayList<Pick3DListener> pick3DListenerList =
            new ArrayList<Pick3DListener>();
    protected final Object pick3DListenerListLock = new Object();

    /**
     * Registers Pick3DListener to receive events.
     *
     * @param listener The listener to register.
     */
    public void addPick3DListener(Pick3DListener listener) {
        synchronized (pick3DListenerListLock) {
            if (listener == null)
                return;

            pick3DListenerList.add(listener);
            listener.setPickTypeListener(this);
        }
    }

    /**
     * Removes Pick3DListener from the list of listeners.
     *
     * @param listener The listener to remove.
     */
    public void removePick3DListener(Pick3DListener listener) {
        synchronized (pick3DListenerListLock) {
            pick3DListenerList.remove(listener);
            listener.removePickTypeListener(this);
        }
    }

    /**
     * Notifies all registered listeners (probably modules) about the pick.
     */
    protected void firePickChanged() {
        synchronized (pick3DListenerListLock) {
            Pick3DEvent e = new Pick3DEvent(this, pickedPoint, sensorInLocalCoords);
            LOGGER.info("point: " + pickedPoint.x + ", " + pickedPoint.y + ", " + pickedPoint.z + "");
            for (Pick3DListener listener : pick3DListenerList) {
                listener.pick3DChanged(e);
            }
        }
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    /**
     * Translates mouse coords (X, Y) to window coords and calculate ends of the pick line.
     */
    protected MyPoint3d[] computeLineCoords(int X, int Y) {
        LocalToWindow locToWin = new LocalToWindow(rootGeomObject, canvas);
        int[] wcrds = new int[2];
        float z = -locToWin.transformPt(new double[]{0, 0, 0}, wcrds);
        Point2d mouseCoords = new Point2d(X, Y);
        MyPoint3d[] clickedLine = new MyPoint3d[2];
        clickedLine[0] = new MyPoint3d();
        clickedLine[1] = new MyPoint3d();
        locToWin.reverseTransformPt(mouseCoords, -1.1f - z, clickedLine[0]);
        locToWin.reverseTransformPt(mouseCoords, -.9f - z, clickedLine[1]);
        return clickedLine;
    }

    /**
     * Creates geometry objects that form a line with diamonds on its both ends and adds it to
     * Java3D scene (via rootGeometryObj). Used in the first phase of picking 3D using mouse.
     */
    protected void createPickLineGeometry() {
        outObj.clearAllGeometry();

        glyph = new IndexedLineStripArray(nvertl,
                                          GeometryArray.COORDINATES
                | GeometryArray.COLOR_3,
                                          nindl, stripsl);
        double dx = line[1].x - line[0].x;
        double dy = line[1].y - line[0].y;
        double dz = line[1].z - line[0].z;
        scale = .01f * (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        for (int j = 0; j < 3; j++) {
            for (int i = 0; i < gt.getNverts(); i++) {
                vertsl[3 * i + j] = line[0].get(j) + scale * gt.getVerts()[3 * i + j];
                vertsl[3 * (i + gt.getNverts()) + j] = line[1].get(j) + scale * gt.getVerts()[3 * i + j];
            }
            vertsl[6 * gt.getNverts() + j] = line[0].get(j);
            vertsl[6 * gt.getNverts() + 3 + j] = line[1].get(j);
        }
        glyph.setColors(0, colors);
        glyph.setCoordinates(0, vertsl);
        glyph.setCoordinateIndices(0, pIndexl);
        glyph.setColorIndices(0, cIndexl);
        OpenShape3D pickLine = new OpenShape3D();
        pickLine.addGeometry(glyph);
        OpenBranchGroup pLine = new OpenBranchGroup();
        pLine.addChild(pickLine);
        outObj.addNode(pLine);
    }

    /**
     * Having points c[0] and c[1] (clicked point is in the middle) compute the picked point -
     * chooses (probably) the point on the
     * <code>line</code> that is the nearest one from the clicked point.
     * <p/>
     * @param c
     */
    protected void computePickedPoint(MyPoint3d[] c) {
        double[][] p = new double[2][3];
        double[][] v = new double[2][3];
        double[][] a = new double[2][2];
        double[] b = new double[2];
        a[0][0] = a[0][1] = b[0] = a[1][0] = a[1][1] = b[1] = 0;
        for (int i = 0; i < 3; i++) {
            p[0][i] = c[0].get(i);
            v[0][i] = c[1].get(i) - c[0].get(i);
            p[1][i] = line[0].get(i);
            v[1][i] = line[1].get(i) - line[0].get(i);
            a[0][0] += v[0][i] * v[0][i];
            a[0][1] -= v[0][i] * v[1][i];
            b[0] += v[0][i] * (p[1][i] - p[0][i]);
            a[1][0] -= v[0][i] * v[1][i];
            a[1][1] += v[1][i] * v[1][i];
            b[1] += v[1][i] * (p[0][i] - p[1][i]);
        }
        double[] t = NumericalMethods.dlsolve(a, b);
        pickedPoint = new MyPoint3d();
        for (int i = 0; i < 3; i++) {
            pickedPoint.set(i, (p[0][i] + t[0] * v[0][i] + p[1][i] + t[1] * v[1][i]) / 2);
        }
    }

    /**
     * Creates geometry object that shows a picked point (diamond object). Used in the second phase
     * of picking 3D using mouse and in picking 3D using a 3D device.
     */
    protected void createPickPointGeometry(MyPoint3d[] c) {

        outObj.clearAllGeometry();
        glyph = new IndexedLineStripArray(nvertp,
                                          GeometryArray.COORDINATES
                | GeometryArray.COLOR_3,
                                          nindp, stripsp);

        double dx = c[1].x - c[0].x;
        double dy = c[1].y - c[0].y;
        double dz = c[1].z - c[0].z;

        scale = .01f * (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        for (int j = 0; j < 3; j++) {
            for (int i = 0; i < gt.getNverts(); i++) {
                vertsp[3 * i + j] = pickedPoint.get(j) + scale * gt.getVerts()[3 * i + j];
            }
        }
        glyph.setColors(0, resultColors);
        glyph.setCoordinates(0, vertsp);
        glyph.setCoordinateIndices(0, pIndexp);
        glyph.setColorIndices(0, cIndexp);
        OpenShape3D pickLine = new OpenShape3D();
        pickLine.addGeometry(glyph);
        OpenBranchGroup pLine = new OpenBranchGroup();
        pLine.addChild(pickLine);
        outObj.addNode(pLine);
        line = null;
    }
//
// =========== currently active PickTypes  ===========
//
    /**
     * A map storing number of modules handling active pick of each type. For a type of pick which
     * isn't handled by any module there could be either no key in this map or a key with value 0.
     */
    final protected EnumMap<PickType, Integer> pickTypes = new EnumMap<PickType, Integer>(PickType.class);
    IViewPointer currentPointer3D = null;

    @Override
    public void onPointer3DChanged(IViewPointer newPointer3D) {
        if (newPointer3D == currentPointer3D)  // should not happen
            return;

        // register or unregister listening by PickObject to 3D pick and selecting a module
        if (newPointer3D == null) {
            // pointer was disabled - unregister
            if (currentPointer3D != null) {
                currentPointer3D.removePickPlainListener();
                currentPointer3D.removePickModuleListener();
            }
        } else {
            // pointer was enabled - register
            newPointer3D.setPickPlainListener(pickPlainListener);
            newPointer3D.setPickModuleListener(pickModuleListener);
        }

        // below we are interested only in pointers with interface PickTypeListenerPointer 
        // other pointers are treated as if there were no pointer
        if (currentPointer3D != null && currentPointer3D instanceof PickTypeListenerPointer) {
            PickTypeListenerPointer pickListenerPointer = (PickTypeListenerPointer) currentPointer3D;
            this.removePickTypeListenerPointer(pickListenerPointer);
        }
        if (newPointer3D != null && newPointer3D instanceof PickTypeListenerPointer) {
            PickTypeListenerPointer pickListenerPointer = (PickTypeListenerPointer) newPointer3D;
            this.addPickTypeListenerPointer(pickListenerPointer);
            pickListenerPointer.onPicksInit(pickTypes);
        }

        currentPointer3D = newPointer3D;
    }

    /**
     * Notifies pointer (pointers) that
     * <code>pickType</code> was turned on. Note that this can mean that this
     * <code>pickType</code> could have been already enabled by a module and now it has just been
     * enabled by the next one. Nevertheless pointer will be notified.
     * <p/>
     * @param pickType
     */
    @Override
    public void onPickTurnedOn(PickType pickType) {
        Integer count = pickTypes.get(pickType);
        pickTypes.put(pickType, count == null ? 1 : count + 1);
        //LOGGER.info("turning on " + pickType + ", new state: " + pickTypes);
        if (currentPointer3D != null && currentPointer3D instanceof PickTypeListenerPointer)
            ((PickTypeListenerPointer) currentPointer3D).onPickTurnedOn(pickType, pickTypes);
    }

    @Override
    public void onPickTurnedOff(PickType pickType) {
        Integer count = pickTypes.get(pickType);
        if (count == null)
            throw new IllegalStateException("Could not decrement number of instances of pick type when it's zero already!");

        pickTypes.put(pickType, count - 1);
        //LOGGER.info("turning off " + pickType + ", new state: " + pickTypes);
        if (currentPointer3D != null && currentPointer3D instanceof PickTypeListenerPointer)
            ((PickTypeListenerPointer) currentPointer3D).onPickTurnedOff(pickType, pickTypes);
    }
// ======= PickTypeListenerPointer  ========
    protected static ArrayList<PickTypeListenerPointer> pickTypeListenerPointers =
            new ArrayList<PickTypeListenerPointer>();

    public void addPickTypeListenerPointer(PickTypeListenerPointer l) {
        pickTypeListenerPointers.add(l);
    }

    public void removePickTypeListenerPointer(PickTypeListenerPointer l) {
        pickTypeListenerPointers.remove(l);
    }

    protected void firePickTypeTurnedOn(PickType pickType) {
        for (PickTypeListenerPointer l : pickTypeListenerPointers) {
            l.onPickTurnedOn(pickType, null);
        }
    }
// ==========================

    public GeometryObject getCurrentObj() {
        return currentObj;
    }
}
