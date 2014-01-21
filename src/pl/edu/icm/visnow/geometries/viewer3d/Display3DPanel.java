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

package pl.edu.icm.visnow.geometries.viewer3d;

import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.behaviors.mouse.MouseZoom;
import com.sun.j3d.utils.scenegraph.io.SceneGraphFileReader;
import com.sun.j3d.utils.scenegraph.io.SceneGraphFileWriter;
import com.sun.j3d.utils.universe.SimpleUniverse;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.SortedSet;
import javax.media.j3d.*;
import javax.swing.JFrame;
import javax.swing.ToolTipManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.*;
import org.apache.commons.imaging.formats.tiff.constants.TiffConstants;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import pl.edu.icm.visnow.application.application.Application;
import pl.edu.icm.visnow.engine.core.ModuleCore;
import pl.edu.icm.visnow.geometries.events.*;
import pl.edu.icm.visnow.geometries.geometryTemplates.MedicalReper;
import pl.edu.icm.visnow.geometries.geometryTemplates.Reper;
import pl.edu.icm.visnow.geometries.objects.GeometryObject;
import pl.edu.icm.visnow.geometries.objects.GeometryParent;
import pl.edu.icm.visnow.geometries.objects.generics.OpenBranchGroup;
import pl.edu.icm.visnow.geometries.objects.generics.OpenTransformGroup;
import pl.edu.icm.visnow.geometries.parameters.AbstractRenderingParams;
import pl.edu.icm.visnow.geometries.parameters.PointLightParams;
import pl.edu.icm.visnow.geometries.parameters.RenderingParams;
import pl.edu.icm.visnow.geometries.utils.transform.LocalToWindow;
import pl.edu.icm.visnow.geometries.viewer3d.controls.Display3DControlsFrame;
import pl.edu.icm.visnow.geometries.viewer3d.controls.Display3DControlsPanel;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.pick.Pick3DListener;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.pick.PickObject;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.pick.PickTypeListener;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.render.FrameRenderedEvent;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.render.FrameRenderedListener;
import pl.edu.icm.visnow.geometries.viewer3d.lights.*;
import pl.edu.icm.visnow.lib.templates.visualization.modules.VisualizationModule;
import pl.edu.icm.visnow.lib.utils.ImageUtilities;
import pl.edu.icm.visnow.lib.utils.YUVSaver;
import pl.edu.icm.visnow.lib.utils.events.MouseRestingEvent;
import pl.edu.icm.visnow.lib.utils.events.MouseRestingListener;
import pl.edu.icm.visnow.system.main.VisNow;

/**
 *
 * @author Krzysztof S. Nowinski (know@icm.edu.pl) Warsaw University
 * Interdisciplinary Centre for Mathematical and Computational Modelling
 */
public class Display3DPanel
        extends javax.swing.JPanel
        implements GeometryParent, RenderingWindowInterface {


    protected static Logger logger = Logger.getLogger(Display3DPanel.class);
    
    protected boolean debug = VisNow.isDebug();
    // Constants for type of light to use
    protected static final int DIRECTIONAL_LIGHT = 0;
    protected static final int POINT_LIGHT = 1;
    protected static final int SPOT_LIGHT = 2;
    protected static final int TRANSFORMED_OBJECT = 0;
    protected static final int TRANSFORMED_CAMERA = 1;
    protected static final int TRANSFORMED_LIGHT = 2;
    protected static final int TRANSFORMED_CURRENT_OBJECT = 3;
    protected View view = null;
    protected static int lightType = POINT_LIGHT;
    protected SimpleUniverse universe = null;
//
    /*
     * One of OpenBranchGroup objects below is a root for all scene objects
     */
    private OpenBranchGroup windowRootObject = new OpenBranchGroup("objRoot");
    protected OpenBranchGroup objScene = new OpenBranchGroup("objScene");
//
    /*
     * Transformations used to rotate, scale and translate the whole scene with
     * objects.
     */
    protected OpenTransformGroup objScale = new OpenTransformGroup("objScale");
    protected ObjRotate objRotate = new ObjRotate("objRotate");
    protected OpenTransformGroup objTranslate = new OpenTransformGroup("objTranslate");
    /** For rotating the scene by mouse */
    protected MouseRotate mouseRotate = new MouseRotate();
    /** For rotating the scene by mouse */
    protected double mouseRotateSensitivity = .002;
    /** Reper object (3D axes in the corner of the window). Thanks to code in constructor it will be
     * automatically updated when rotating the scene. */
    protected ObjReper objReper = new ObjReper("objReper");
    protected OpenTransformGroup positionedReper = new OpenTransformGroup("posReper");
    protected OpenBranchGroup reperGroup = new OpenBranchGroup("reperGroup");
    /** For moving the scene by mouse */
    protected MouseTranslate mouseTranslate = new MouseTranslate();
    /** For moving the scene by mouse */
    protected double mouseTranslateSensitivity = .002;
    /** For zooming the scene by mouse */
    protected MouseZoom mouseZoom = new MouseZoom();
    protected double mouseScale = 1.;
    protected double externScale = 1.;
    protected double mouseWheelSensitivity = 1.02;
//
    protected Vector3d sceneCenter = new Vector3d(0., 0., 0.);
    
    /** Temporary transform used locally in many methods */
    protected Transform3D tempTransform = new Transform3D();
//
//
    protected ChangeListener renderListener = new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
            boolean action = (Boolean) e.getSource();
            if (action) {
                canvas.startRenderer();
            } else {
                canvas.stopRenderer();
            }
        }
    };
    
    /**
     * Scene properties
     */
    protected Color3f bgColor = new Color3f(0.f, 0.f, 0.f);
    protected LinearFog myFog = new LinearFog();
    protected BoundingSphere bounds =
            new BoundingSphere(new Point3d(0., 0., 0.), 100.0);
    protected ModelClip modelClip = new ModelClip(new Vector4d[]{new Vector4d(-1, 0, 0, 1), new Vector4d(1, 0, 0, -1),
                                                                 new Vector4d(0, -1, 0, 1), new Vector4d(0, 1, 0, -1),
                                                                 new Vector4d(0, 0, -1, 1), new Vector4d(0, 0, 1, -1)},
                                                  new boolean[]{false, false, false, false, false, false});
    protected EditableAmbientLight ambientLight = null;
    protected ArrayList<EditableDirectionalLight> directionalLights = new ArrayList<EditableDirectionalLight>();
    protected ArrayList<TransformGroup> directionalLightTransforms = new ArrayList<TransformGroup>();
    protected ArrayList<EditablePointLight> pointLights = new ArrayList<EditablePointLight>();
    protected ArrayList<TransformGroup> pointLightTransforms = new ArrayList<TransformGroup>();
    protected Background bg = new Background();
    protected J3DGraphics2D vGraphics = null;
//
    /** Main canvas */
    private Canvas3D canvas = null;
//
    protected boolean perspective = true;
    protected boolean renderDone = false;
    protected boolean offScreenRenderDone = false;
    protected LocalToWindow locToWin = null;
    protected BufferedImage im = null;
    protected ImageComponent2D imC = null;
    /*
     * used for proper on- anf offscreen rendering of titles and other window space elements
     */
    protected int effectiveHeight = 512;
    protected int effectiveWidth = 512;
    /** 
     * Object used for output view content (images and movies)
     * An off-screen canvas, used for making screenshots only. 
     */
    protected Canvas3D offScreenCanvas = null;
    protected View offScreenView = null;
    protected Screen3D offScreen = null;
    protected LocalToWindow offScreenLocToWin = null;
    protected boolean storingJPEG = false;
    protected boolean storingPNG = false;
    protected boolean storingFrames = false;
    protected boolean yuv = false;
    protected YUVSaver yuvSaver = null;
    /**
     * Display control UI
     */
    private Display3DControlsFrame controlsFrame = null;
    private JFrame transientControlsFrame = null;
    protected Display3DControlsPanel controlsPanel = null;
    
    protected ArrayList<Title> titles = null;
    protected AbstractRenderingParams objectDisplayParams = new RenderingParams(this);
    
    protected GeometryObject rootObject = new GeometryObject("root_object") {
        @Override
        public void updateExtents() {
            if (lockView) {
                return;
            }

            super.updateExtents();
            rootDim = 0;
            rootExtents = rootObject.getExtents();
            for (int i = 0; i < 3; i++) {
                if(rootExtents[0][i] == Float.MAX_VALUE || rootExtents[0][i] == -Float.MAX_VALUE || rootExtents[1][i] == Float.MAX_VALUE || rootExtents[1][i] == -Float.MAX_VALUE)
                    continue;                
                if (rootDim < rootExtents[1][i] - rootExtents[0][i]) {
                    rootDim = rootExtents[1][i] - rootExtents[0][i];
                }
            }
            if (rootDim == 0) {
                rootDim = 1;
            }
            sceneCenter = new Vector3d(-(rootExtents[1][0] + rootExtents[0][0]) / 2.0,
                                       -(rootExtents[1][1] + rootExtents[0][1]) / 2.0,
                                       -(rootExtents[1][2] + rootExtents[0][2]) / 2.0);

            Transform3D tr = new Transform3D();
            tr.setTranslation(sceneCenter);
            objTranslate.setTransform(tr);

            Display3DPanel.this.setScale(1.2 / rootDim);
            controlsPanel.setExtents(rootExtents);
        }
    };
    
    protected final ArrayList<FrameRenderedListener> frameRenderedListeners = new ArrayList<FrameRenderedListener>();
    protected PickObject pickObject;
    protected ArrayList<ColorListener> bgrColorListeners = new ArrayList<ColorListener>();
    /*
     * Stereo settings - active only if stareo view is on
     */
    protected Point3d defaultLeftEye = new Point3d();
    protected Point3d defaultRightEye = new Point3d();
    protected float eyeSeparation = .5f;
    protected Point3d leftEye = null;
    protected Point3d rightEye = null;
    
    protected Thread mouseObserverThread = null;
    protected boolean mouseOn = false;
    protected int timeToPopup = 5;
    protected boolean stereoActive = false;
    protected int transformedNode = TRANSFORMED_OBJECT;
    private OpenBranchGroup myFogGroup = new OpenBranchGroup();
    protected Transform3D pickTransform = new Transform3D();
    protected float[][] rootExtents = new float[][]{{-1, -1, -1}, {1, 1, 1}};
    protected float rootDim = 1;
    private Transform3D initialCameraTransform = new Transform3D();
    protected JFrame parentFrame = null;
    protected boolean moveCameraMode = false;
    protected boolean showReper = true;
    protected int reperSize = 50;
    protected GeometryObject reper = new Reper();
    protected String name;
    protected int renderCounter = 0;
    protected int swapCounter = 0;
    protected Cursor crosshairCursor;    
    private Application application = null;

    /**
     * Creates new form Display3DPanel
     */
    public Display3DPanel() {
        initComponents();
        effectiveHeight = getHeight();
        effectiveWidth = getWidth();
        logger.debug("creating Display3DPanel");
        this.setMinimumSize(new Dimension(200, 200));
        this.setPreferredSize(new Dimension(800, 600));
        GraphicsConfigTemplate3D template = new GraphicsConfigTemplate3D();
        template.setStereo(GraphicsConfigTemplate3D.PREFERRED);
        
        // Get the GraphicsConfiguration that best fits our needs.
        logger.debug("getting config");
        GraphicsConfiguration gcfg =
                GraphicsEnvironment.getLocalGraphicsEnvironment().
                getDefaultScreenDevice().getBestConfiguration(template);
        logger.debug("creating canvas");
        canvas = new Canvas3D(gcfg) {
            @Override
            public void postRender() {
                vGraphics = super.getGraphics2D();
                vGraphics.setFont(new Font("sans-serif", Font.PLAIN, 10));
                vGraphics.setColor(Color.YELLOW);
                locToWin.update();
                fireProjectionChanged(new ProjectionEvent(this, locToWin));
                draw2D(vGraphics, locToWin, effectiveWidth, effectiveHeight);
                vGraphics.flush(false);
            }

            @Override
            public void postSwap() {
                if (postRenderSilent || waitForExternalTrigger) {
                    return;
                }
                if (!(storingJPEG || storingPNG || storingFrames)) {
                    fireFrameRendered();
                }
                if (storingFrames) {
                    if (storingJPEG) {
                        writeImage(new File(controlsPanel.getMovieCreationPanel().getCurrentFrameFileName()));
                    } else {
                        writeYUV(controlsPanel.getMovieCreationPanel().getGenericFrameFileName());
                    }
                }
            }
        };
        canvas.setStereoEnable(false);
        add("Center", canvas);

        pickObject = new PickObject(canvas, objScene,
                rootObject.getGeometryObj(), objRotate);

        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.isShiftDown()) {
//              if (e.isShiftDown())
//                 System.out.println(""+e.getX() + " " + e.getY());
                }
                if (evt.getButton() == MouseEvent.BUTTON1) {
                    if (evt.getClickCount() > 1) {
                        reset();
                    } else if (pickObject.consumeEmulated3DPick(evt.getX(),
                                                                evt.getY())) {
                        /* Nothing must be done - everything was done in consumeEmulated3DPick() */
                    } else {
                        pickObject.consume2DSelectModule(evt.getX(),
                                                         evt.getY());
                    }
                    rootObject.firePickChanged(evt, locToWin);
                }
                if (evt.getButton() == MouseEvent.BUTTON3) {
                    if (getControlsFrame() != null) {
                        getControlsFrame().setBounds(evt.getXOnScreen() - 130, evt.getYOnScreen(), 240, 500);
                        getControlsFrame().setVisible(true);
                    } else if (getTransientControlsFrame() != null) {
                        getTransientControlsFrame().setBounds(evt.getXOnScreen() - 130, evt.getYOnScreen(), 240, 500);
                        getTransientControlsFrame().setVisible(true);
                    } else if(application!= null) {
                        application.getFrames().getGuiPanel().selectModule(name);
                    }
                }
                if (evt.getButton() == MouseEvent.BUTTON2) {
                    reset();
                }
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                mouseOn = true;
                mouseObserverThread = new Thread(new MouseObserver());
                mouseObserverThread.start();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                mouseObserverThread = null;
                mouseOn = false;
            }
        });

        canvas.addMouseMotionListener(new java.awt.event.MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
//              if (e.isShiftDown())
//                 System.out.println(""+e.getX() + " " + e.getY());
            }

            @Override
            public void mouseMoved(MouseEvent e) {
            }
        });

        canvas.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            @Override
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                rescaleFromMouseWheel(evt);
            }
        });

        canvas.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent evt) {
                formKeyTyped(evt);
            }

            @Override
            public void keyPressed(KeyEvent evt) {
                formKeyPressed(evt);
            }

            @Override
            public void keyReleased(KeyEvent evt) {
                formKeyReleased(evt);
            }
        });

        ToolTipManager.sharedInstance().setLightWeightPopupEnabled(true);
        universe = new SimpleUniverse(canvas);
        view = canvas.getView();
        view.setProjectionPolicy(View.PERSPECTIVE_PROJECTION);
        view.setTransparencySortingPolicy(View.TRANSPARENCY_SORT_GEOMETRY);

        objScene.addChild(rootObject.getGeometryObj());
        rootObject.setRenderingWindow(this);
        rootObject.getGeometryObj().setUserData(null);

        bg.setCapability(Background.ALLOW_COLOR_WRITE);
        bg.setCapability(Background.ALLOW_COLOR_READ);
        bg.setCapability(Background.ALLOW_IMAGE_WRITE);
        bg.setCapability(Background.ALLOW_IMAGE_READ);
        bg.setCapability(Background.ALLOW_IMAGE_SCALE_MODE_READ);
        bg.setCapability(Background.ALLOW_IMAGE_SCALE_MODE_WRITE);
        bg.setImageScaleMode(Background.SCALE_FIT_ALL);
        bg.setApplicationBounds(bounds);

        myFog.setCapability(LinearFog.ALLOW_DISTANCE_WRITE);
        myFog.setCapability(LinearFog.ALLOW_COLOR_WRITE);
        myFog.setInfluencingBounds(bounds);
        myFog.setFrontDistance(1.);
        myFog.setBackDistance(4.);
        myFogGroup.addChild(myFog);


        mouseRotate.setTransformGroup(objRotate);
        mouseRotate.setSchedulingBounds(bounds);
        mouseRotate.setFactor(mouseRotateSensitivity);

        mouseTranslate.setTransformGroup(objRotate);
        mouseTranslate.setSchedulingBounds(bounds);
        mouseTranslate.setFactor(mouseTranslateSensitivity);

        mouseZoom.setTransformGroup(objRotate);
        mouseZoom.setSchedulingBounds(bounds);

        windowRootObject.addChild(mouseRotate);
        windowRootObject.addChild(mouseTranslate);
        windowRootObject.addChild(mouseZoom);

        objTranslate.addChild(objScene);
        objScale.addChild(objTranslate);
        objRotate.addChild(bg);
        objRotate.addChild(objScale);
        windowRootObject.addChild(objRotate);

        ambientLight = new EditableAmbientLight(new Color3f(.25f, .25f, .25f), bounds, "ambient light", true);
        windowRootObject.addChild(ambientLight.getLight());

        directionalLights.add(new EditableDirectionalLight(new Color3f(0.4f, 0.4f, 0.4f),
                                                           new Vector3f(.1f, .08f, -.5f),
                                                           bounds, "light 1", true, true));
        directionalLights.add(new EditableDirectionalLight(new Color3f(0.2f, 0.15f, 0.1f),
                                                           new Vector3f(-1.f, -0.4f, -.5f),
                                                           bounds, "light 2", true, false));
        directionalLights.add(new EditableDirectionalLight(new Color3f(0.1f, 0.1f, 0.15f),
                                                           new Vector3f(0.f, 0.6f, -1.f),
                                                           bounds, "light 3", true, false));
        directionalLights.add(new EditableDirectionalLight(new Color3f(0.1f, 0.1f, 0.15f),
                                                           new Vector3f(0.f, -0.6f, -1.f),
                                                           bounds, "light 3", false, false));
        for (int i = 0; i < directionalLights.size(); i++) {
            TransformGroup lightTransform = new TransformGroup();
            lightTransform.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
            lightTransform.setName("light" + i + "Transform");
            directionalLightTransforms.add(lightTransform);
            OpenBranchGroup lightGroup = new OpenBranchGroup();
            EditableDirectionalLight light = directionalLights.get(i);
            lightGroup.addChild(light.getLight());
            lightGroup.addChild(light.getBackLight());
            lightTransform.addChild(lightGroup);
            windowRootObject.addChild(lightTransform);
        }
        modelClip.setCapability(ModelClip.ALLOW_ENABLE_READ);
        modelClip.setCapability(ModelClip.ALLOW_ENABLE_WRITE);
        modelClip.setCapability(ModelClip.ALLOW_PLANE_READ);
        modelClip.setCapability(ModelClip.ALLOW_PLANE_WRITE);
        modelClip.setInfluencingBounds(bounds);
        objScene.addChild(modelClip);

        pointLights.add(new EditablePointLight(new Color3f(0.4f, 0.4f, 0.4f),
                                               bounds, null, null, "light 1", false));
        pointLights.add(new EditablePointLight(new Color3f(0.4f, 0.4f, 0.4f),
                                               bounds, null, null, "light 1", false));
        pointLights.add(new EditablePointLight(new Color3f(0.4f, 0.4f, 0.4f),
                                               bounds, null, null, "light 1", false));
        for (int i = 0; i < pointLights.size(); i++)
       {
          TransformGroup lightTransform = new TransformGroup();
          lightTransform.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
          lightTransform.setName("light" + i + "Transform");
          pointLightTransforms.add(lightTransform);
          OpenBranchGroup lightGroup = new OpenBranchGroup();
          EditablePointLight light = pointLights.get(i);
          lightGroup.addChild(light.getLight());
          lightTransform.addChild(lightGroup);
          windowRootObject.addChild(lightTransform);
       }
       alternateTransformObj.addChild(alternateRootObj);
       alternateTransormBranch.addChild(alternateTransformObj);
       windowRootObject.addChild(alternateTransormBranch);

        universe.getViewingPlatform().setNominalViewingTransform();
        universe.addBranchGraph(windowRootObject);

        view.getPhysicalBody().getLeftEyePosition(defaultLeftEye);
        view.getPhysicalBody().getRightEyePosition(defaultRightEye);
        controlsFrame = new Display3DControlsFrame(this);
        controlsPanel = controlsFrame.getControlPanel();
        universe.getViewingPlatform().getViewPlatformTransform().getTransform(initialCameraTransform);

        objReper.addChild(reper.getGeometryObj());
        positionedReper.addChild(objReper);
        positionedReper.setTransform(new Transform3D(new float[]{.15f, 0, 0, -.8f,
                                                                 0, .15f, 0, .76f,
                                                                 0, 0, .15f, 0,
                                                                 0, 0, 0, 1}));
        reperGroup.addChild(positionedReper);
        OpenBranchGroup reperLightGroup = new OpenBranchGroup();
        reperLightGroup.addChild(new AmbientLight(new Color3f(.6f, .6f, .6f)));
        reperLightGroup.addChild(new EditableDirectionalLight(new Color3f(0.4f, 0.4f, 0.4f),
                                                              new Vector3f(.1f, .08f, -1f),
                                                              bounds, "light 1", true, true).getLight());
        reperGroup.addChild(reperLightGroup);
        universe.addBranchGraph(reperGroup);
        locToWin = new LocalToWindow(rootObject.getGeometryObj(), canvas);

        /**
         * The line below binds reper with scene rotation when using any method that calls
         * objRotate.setTransform(), so: keyboard (explicitly calling), mouse (Java3D is calling
         * setTransform) and some custom ways (e.g. in haptic pointer)
         */
        objRotate.addTransformListener(objReper);
//        mouseRotate.setupCallback(objReper); // it is not needed, because Java3D calls objRotate.setTranform which fires callback to the reper
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public void setControlsPanel(Display3DControlsPanel controlsPanel) {
        this.controlsPanel = controlsPanel;
    }

    public void setControlsFrame(Display3DControlsFrame controlsFrame) {
        this.controlsFrame = controlsFrame;
    }

    private void rescaleFromMouseWheel(java.awt.event.MouseWheelEvent evt) {
        if (lockView) {
            return;
        }

        int notches = evt.getWheelRotation();
        if (notches < 0) {
            mouseScale /= mouseWheelSensitivity;
        } else {
            mouseScale *= mouseWheelSensitivity;
        }
        tempTransform = new Transform3D(new Matrix3d(1., 0., 0.,
                                                 0., 1., 0.,
                                                 0., 0., 1.),
                                    //sceneCenter,
                                    new Vector3d(0.0, 0.0, 0.0),
                                    externScale * mouseScale);
        objScale.setTransform(tempTransform);
    }

    /**
     * Forwards this request to a PickObject object.
     * <p/>
     * @param listener a listener to be added
     */
    @Override
    public void addPick3DListener(Pick3DListener listener) {
        pickObject.addPick3DListener(listener);
    }

    /**
     * Forwards this request to a PickObject object.
     * <p/>
     * @param listener a listener to be removed
     */
    @Override
    public void removePick3DListener(Pick3DListener listener) {
        pickObject.removePick3DListener(listener);
    }

    public PickObject getPickObject() {
        return pickObject;
    }

    @Override
    public void setExtents(float[][] ext) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isMoveCameraMode() {
        return this.moveCameraMode;
    }

    public void setMoveCameraMode(boolean setCameraMode) {
        if (moveCameraMode == setCameraMode) {
            return;
        }

        moveCameraMode = setCameraMode;
    }

    /**
     * @return the controlsFrame
     */
    public Display3DControlsFrame getControlsFrame() {
        return controlsFrame;
    }

    /**
     * @return the transientControlsFrame
     */
    public JFrame getTransientControlsFrame() {
        return transientControlsFrame;
    }

    /**
     * @return the windowRootObject
     */
    public OpenBranchGroup getWindowRootObject() {
        return windowRootObject;
    }

    /**
     * @return rotate object
     */
    public ObjRotate getRotateObject() {
        return objRotate;
    }

    public ObjReper getReperObject() {
        return objReper;
    }

    @Override
    public PickTypeListener getPickTypeListener() {
        return pickObject;
    }

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
        pickObject.setApplication(application);
    }

    private class MouseObserver implements Runnable {

        int timer = 0, lastX = -1, lastY = -1;

        @Override
        public void run() {
            while (mouseOn && !mouseRestingListenerList.isEmpty()) {
                Point p = getMousePosition();
                if (p == null) {
                    continue;
                }
                if ((int) p.getX() != lastX || (int) p.getY() != lastY) {
                    timer = 0;
                    lastX = (int) p.getX();
                    lastY = (int) p.getY();
                } else {
                    timer += 1;
                    if (timer == timeToPopup) {
                        fireMouseResting(lastX, lastY);
                    }
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                }
            }
            fireMouseResting(-1, -1);
        }
    }

    private void formKeyTyped(KeyEvent evt) {
        if (evt.getKeyChar() == ' ') {
            if (controlsFrame != null) {
                controlsFrame.setBounds(30, 30, 220, 500);
                controlsFrame.setVisible(true);
            } else if (transientControlsFrame != null) {
                transientControlsFrame.setBounds(30, 30, 240, 500);
                transientControlsFrame.setVisible(true);
            } else if(application != null) {
                application.getFrames().getGuiPanel().selectModule(name);
            }
        } else if (evt.getKeyChar() == 'c') {
            moveCamera();
        } else if (evt.getKeyChar() == 'o') {
            moveScene();
        } else if (evt.getKeyChar() == 'p') {
            boolean p = perspective;
            controlsPanel.togglePerspective();
            setDisplayMode(!p);
        } else if (evt.getKeyChar() == 's') {
            boolean p = stereoActive;
            controlsPanel.toggleStereo();
            setStereoEnable(!p);
        } else if (evt.getKeyChar() == '.') {
            controlsPanel.togglePick3D();
            setPick3DActive(!pickObject.isActive());
        }
        else if (evt.getKeyChar() == 'm')
        {
           moveCurrentObject();
        }
    }
    //TODO - chnge mapping or add switch
    private static final int CAMERA_UP_KEY = KeyEvent.VK_U;
    private static final int CAMERA_DOWN_KEY = KeyEvent.VK_J;
    private static final int CAMERA_LEFT_KEY = KeyEvent.VK_H;
    private static final int CAMERA_RIGHT_KEY = KeyEvent.VK_K;
    private static final int CAMERA_FORWARD_KEY = KeyEvent.VK_O;
    private static final int CAMERA_BACKWARD_KEY = KeyEvent.VK_L;
    private boolean cameraUpKeyPressed = false;
    private boolean cameraDownKeyPressed = false;
    private boolean cameraLeftKeyPressed = false;
    private boolean cameraRightKeyPressed = false;
    private boolean cameraForwardKeyPressed = false;
    private boolean cameraBackwardKeyPressed = false;

    private void formKeyReleased(KeyEvent evt) {

        switch (evt.getKeyCode()) {
            case CAMERA_UP_KEY:
                cameraUpKeyPressed = false;
                processCameraKeys(evt);
                break;
            case CAMERA_DOWN_KEY:
                cameraDownKeyPressed = false;
                processCameraKeys(evt);
                break;
            case CAMERA_LEFT_KEY:
                cameraLeftKeyPressed = false;
                processCameraKeys(evt);
                break;
            case CAMERA_RIGHT_KEY:
                cameraRightKeyPressed = false;
                processCameraKeys(evt);
                break;
            case CAMERA_FORWARD_KEY:
                cameraForwardKeyPressed = false;
                processCameraKeys(evt);
                break;
            case CAMERA_BACKWARD_KEY:
                cameraBackwardKeyPressed = false;
                processCameraKeys(evt);
                break;
            case KeyEvent.VK_BACK_SPACE:
                reset();
                break;
            default:
                break;
        }

    }

    private void formKeyPressed(KeyEvent evt) {
        if (lockView) {
            return;
        }

        double dAngle = Math.PI / 144;
        double dScale = 129. / 128.;
        if (storingFrames) {
            dAngle = Math.PI / 360;
            dScale = 513. / 512.;
        }
        if ((evt.getModifiers() & KeyEvent.CTRL_MASK) != 0) {
            dAngle = Math.PI / 2;
        }
        if ((evt.getModifiers() & KeyEvent.SHIFT_MASK) != 0) {
            dAngle = Math.PI / 6;
        }
        objRotate.getTransform(tempTransform);
        Transform3D rot = new Transform3D();
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_NUMPAD5:
                reset();
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_KP_DOWN:
            case KeyEvent.VK_NUMPAD2:
                rot.rotX(dAngle);
                break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_KP_UP:
            case KeyEvent.VK_NUMPAD8:
                rot.rotX(-dAngle);
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_KP_LEFT:
            case KeyEvent.VK_NUMPAD4:
                rot.rotY(-dAngle);
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_KP_RIGHT:
            case KeyEvent.VK_NUMPAD6:
                rot.rotY(dAngle);
                break;
            case KeyEvent.VK_PAGE_UP:
            case KeyEvent.VK_NUMPAD9:
                rot.rotZ(dAngle);
                break;
            case KeyEvent.VK_PAGE_DOWN:
            case KeyEvent.VK_NUMPAD3:
                rot.rotZ(-dAngle);
                break;
        }
        rot.mul(tempTransform);
        objRotate.setTransform(rot);
        
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_HOME:
            case KeyEvent.VK_NUMPAD7:
                mouseScale *= dScale;
                break;
            case KeyEvent.VK_END:
            case KeyEvent.VK_NUMPAD1:
                mouseScale /= dScale;
                break;
        }
        tempTransform = new Transform3D(new Matrix3d(1., 0., 0.,
                                                 0., 1., 0.,
                                                 0., 0., 1.),
                                    //sceneCenter,
                                    new Vector3d(0.0, 0.0, 0.0),
                                    externScale * mouseScale);
        objScale.setTransform(tempTransform);
        switch (evt.getKeyCode()) {

            case CAMERA_UP_KEY:
                cameraUpKeyPressed = true;
                processCameraKeys(evt);
                break;
            case CAMERA_DOWN_KEY:
                cameraDownKeyPressed = true;
                processCameraKeys(evt);
                break;
            case CAMERA_LEFT_KEY:
                cameraLeftKeyPressed = true;
                processCameraKeys(evt);
                break;
            case CAMERA_RIGHT_KEY:
                cameraRightKeyPressed = true;
                processCameraKeys(evt);
                break;
            case CAMERA_FORWARD_KEY:
                cameraForwardKeyPressed = true;
                processCameraKeys(evt);
                break;
            case CAMERA_BACKWARD_KEY:
                cameraBackwardKeyPressed = true;
                processCameraKeys(evt);
                break;

// TODO milimetr: delete the code below after testing
            case '8':
                pickObject.setPickModuleMode(1);
                break;
            case '9':
                pickObject.setPickModuleMode(2);
                break;
            case '0':
                pickObject.setPickModuleMode(3);
                break;

            default:
                break;
        }
    }

    private void processCameraKeys(KeyEvent evt) {
        double dAngle = Math.PI / 144;
        double dScale = 129. / 128.;
        if (storingFrames) {
            dAngle = Math.PI / 360;
            dScale = 513. / 512.;
        }
        if ((evt.getModifiers() & KeyEvent.CTRL_MASK) != 0) {
            dAngle = Math.PI / 2;
        }

        TransformGroup cam = universe.getViewingPlatform().getViewPlatformTransform();
        Transform3D camTr;
        Transform3D cRot;
        Transform3D tmpTr;
        Vector3d cMov;

        if (cameraUpKeyPressed) {
            camTr = new Transform3D();
            cam.getTransform(camTr);
            cRot = new Transform3D();
            cRot.rotX(-dAngle);
            camTr.mul(cRot);
            cam.setTransform(camTr);
        }

        if (cameraDownKeyPressed) {
            camTr = new Transform3D();
            cam.getTransform(camTr);
            cRot = new Transform3D();
            cRot.rotX(dAngle);
            camTr.mul(cRot);
            cam.setTransform(camTr);
        }

        if (cameraRightKeyPressed) {
            camTr = new Transform3D();
            cam.getTransform(camTr);
            cRot = new Transform3D();
            cRot.rotY(-dAngle);
            camTr.mul(cRot);
            cam.setTransform(camTr);
        }

        if (cameraLeftKeyPressed) {
            camTr = new Transform3D();
            cam.getTransform(camTr);
            cRot = new Transform3D();
            cRot.rotY(dAngle);
            camTr.mul(cRot);
            cam.setTransform(camTr);
        }

        if (cameraForwardKeyPressed) {
            camTr = new Transform3D();
            cam.getTransform(camTr);
            cMov = new Vector3d();
            cMov.x = 0;
            cMov.y = 0;
            cMov.z = -(dScale / 50);

            tmpTr = new Transform3D();
            cam.getTransform(tmpTr);
            tmpTr.set(new Vector3d());
            tmpTr.transform(cMov);

            tmpTr.set(cMov);
            camTr.mul(tmpTr);
            cam.setTransform(camTr);
        }

        if (cameraBackwardKeyPressed) {
            camTr = new Transform3D();
            cam.getTransform(camTr);
            cMov = new Vector3d();
            cMov.x = 0;
            cMov.y = 0;
            cMov.z = (dScale / 50);

            tmpTr = new Transform3D();
            cam.getTransform(tmpTr);
            tmpTr.set(new Vector3d());
            tmpTr.transform(cMov);

            tmpTr.set(cMov);
            camTr.mul(tmpTr);
            cam.setTransform(camTr);
        }

    }

    public void setBackgroundColor(Color3f c) {
        bgColor = c;
        bg.setColor(c);
        myFog.setColor(bgColor);
        fireBgrColorChanged();
    }

    public void setBackgroundColor(Color c) {
        bgColor = new Color3f(c);
        bg.setImage(null);
        bg.setColor(bgColor);
        myFog.setColor(bgColor);
        fireBgrColorChanged();
    }

    public void setBackgroundGradient(Color c0, Color c1, Color c2) {
        bgColor = new Color3f(c0.getColorComponents(null));
        myFog.setColor(bgColor);
        fireBgrColorChanged();
        int r0 = c0.getRed(), r1 = c1.getRed(), r2 = c2.getRed();
        int g0 = c0.getGreen(), g1 = c1.getGreen(), g2 = c2.getGreen();
        int b0 = c0.getBlue(), b1 = c1.getBlue(), b2 = c2.getBlue();
        int[] bgrData = new int[256 * 256];
        int k = 0;
        for (int i = 0; i < 128; i++) {
            float t = i / 127.f;
            int r = (int) (t * r1 + (1 - t) * r0);
            int g = (int) (t * g1 + (1 - t) * g0);
            int b = (int) (t * b1 + (1 - t) * b0);
            int c = ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
            for (int j = 0; j < 256; j++, k++) {
                bgrData[k] = c;
            }
        }
        for (int i = 0; i < 128; i++) {
            float t = i / 127.f;
            int r = (int) (t * r2 + (1 - t) * r1);
            int g = (int) (t * g2 + (1 - t) * g1);
            int b = (int) (t * b2 + (1 - t) * b1);
            int c = ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
            for (int j = 0; j < 256; j++, k++) {
                bgrData[k] = c;
            }
        }
        BufferedImage bgrImage = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        bgrImage.setRGB(0, 0, 256, 256, bgrData, 0, 256);
        ImageComponent2D bgrImageComponent = new ImageComponent2D(ImageComponent2D.FORMAT_RGBA, bgrImage);
        bg.setImage(bgrImageComponent);
    }
    BufferedImage baseBgrImage;

    public void setBgrImageBrightness(float t) {
        if (baseBgrImage == null) {
            return;
        }
        int w = baseBgrImage.getWidth();
        int h = baseBgrImage.getHeight();
        int[] bgrData = baseBgrImage.getRGB(0, 0, w, h, null, 0, w);
        for (int i = 0; i < bgrData.length; i++) {
            int imc = bgrData[i];
            int r = (int) (t * ((imc >> 16) & 0xff));
            int g = (int) (t * ((imc >> 8) & 0xff));
            int b = (int) (t * (imc & 0xff));
            bgrData[i] = ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
        }
        BufferedImage bgrImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        bgrImage.setRGB(0, 0, w, h, bgrData, 0, w);
        ImageComponent2D bgrImageComponent = new ImageComponent2D(ImageComponent2D.FORMAT_RGBA, bgrImage);
        bg.setImage(bgrImageComponent);
    }

    public void setBackgroundImage(String imageFileName) {
        baseBgrImage = ImageUtilities.loadImage(imageFileName);
        setBgrImageBrightness(1.f);
    }

    public Color getBackgroundColor() {
        return bgColor.get();
    }

    public void reset() {
        if (lockView) {
            return;
        }
        mouseScale = 1.;
        objRotate.setTransform(new Transform3D());
        Transform3D tr = new Transform3D();
        tr.setTranslation(sceneCenter);
        objTranslate.setTransform(tr);
        tempTransform = new Transform3D(new Matrix3d(1., 0., 0.,
                                                 0., 1., 0.,
                                                 0., 0., 1.),
                                    //sceneCenter,
                                    new Vector3d(0.0, 0.0, 0.0),
                                    externScale * mouseScale);
        objScale.setTransform(tempTransform);
        universe.getViewingPlatform().getViewPlatformTransform().setTransform(initialCameraTransform);
    }

   public void setDisplayMode(boolean mode)
   {
      perspective = mode;
      if (perspective)
      {
         view.setProjectionPolicy(View.PERSPECTIVE_PROJECTION);
         if (offScreenView != null)
            offScreenView.setProjectionPolicy(View.PERSPECTIVE_PROJECTION);
      } else
      {
         view.setProjectionPolicy(View.PARALLEL_PROJECTION);
         if (offScreenView != null)
            offScreenView.setProjectionPolicy(View.PARALLEL_PROJECTION);
      }
   }

   @Override
   public void setScale(double scale)
   {
      if (lockView)
         return;

      externScale = scale;
      tempTransform = new Transform3D(new Matrix3d(1., 0., 0.,
                                                   0., 1., 0.,
                                                   0., 0., 1.),
              // sceneCenter,
              new Vector3d(0.0, 0.0, 0.0),
              externScale * mouseScale);
      objScale.setTransform(tempTransform);
   }

   public void newOffScreen()
   {
      newOffScreen(this.getWidth(), this.getHeight());
   }

   public Dimension getOffScreenSize()
   {
      if (this.offScreenCanvas == null)
         return null;
      return offScreenCanvas.getSize();
   }

   public void newOffScreen(int w, int h)
   {
      if (w * h == 0)
      {
         logger.error("bad window size");
         return;
      }
      im = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
      imC = new ImageComponent2D(ImageComponent2D.FORMAT_RGB, im, true, false);
      imC.setCapability(ImageComponent2D.ALLOW_IMAGE_READ);
      offScreenCanvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration(), true)
      {
         @Override
         public void postRender()
         {
            int stdEffectiveWidth = effectiveWidth;
            int stdEffectiveHeight = effectiveHeight;
            effectiveWidth = offScreenCanvas.getWidth();
            effectiveHeight = offScreenCanvas.getHeight();
            J3DGraphics2D vGraphics = super.getGraphics2D();
            vGraphics.setFont(new Font("sans-serif", Font.PLAIN, 10));
            vGraphics.setColor(Color.YELLOW);
            offScreenLocToWin = new LocalToWindow(objScene, offScreenCanvas);
            offScreenLocToWin.update();
            fireProjectionChanged(new ProjectionEvent(this, offScreenLocToWin));
            draw2D(vGraphics, offScreenLocToWin, effectiveWidth, effectiveHeight);
            vGraphics.flush(false);
            effectiveWidth = stdEffectiveWidth;
            effectiveHeight = stdEffectiveHeight;
            offScreenRenderDone = true;
         }
      };
      offScreenView = offScreenCanvas.getView();
      offScreen = offScreenCanvas.getScreen3D();
      offScreenCanvas.setOffScreenLocation(0, 0);
      offScreenCanvas.setOffScreenBuffer(imC);
      offScreen.setSize(w, h);
      double width = 0.0254 / 90.0 * w;
      double height = 0.0254 / 90.0 * h;
      offScreen.setPhysicalScreenWidth(width);
      offScreen.setPhysicalScreenHeight(height);
      universe.getViewer().getView().addCanvas3D(offScreenCanvas);
      if (offScreenView != null)
         if (perspective)
         offScreenView.setProjectionPolicy(View.PERSPECTIVE_PROJECTION);
         else
            offScreenView.setProjectionPolicy(View.PARALLEL_PROJECTION);
   }

   public void clearOffScreen()
   {
      if (offScreenCanvas != null)
         universe.getViewer().getView().removeCanvas3D(offScreenCanvas);
      offScreenCanvas = null;
   }

   public void writeScene(String sceneFile)
   {
      try
      {
         SceneGraphFileWriter scnWriter
                 = new SceneGraphFileWriter(new File(sceneFile), universe, true, "", null);
         scnWriter.close();
      } catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   public void readScene(String sceneFile)
   {
      try
      {
         SceneGraphFileReader scnReader = new SceneGraphFileReader(new File(sceneFile));
         universe = scnReader.readUniverse(true);
         scnReader.close();
      } catch (Exception e)
      {
      }
   }

   public void writeImage(File file)
   {
      if (offScreenCanvas == null)
      {
         fireFrameRendered();
         return;
      }
      offScreenRenderDone = false;
      try
      {
         offScreenCanvas.renderOffScreenBuffer();
      } catch (NullPointerException c)
      {
         logger.error("Null pointer in offC.renderOffScreenBuffer()");
         fireFrameRendered();
         return;
      }
      if (!storingFrames)
         offScreenCanvas.waitForOffScreenRendering();
      while (!offScreenRenderDone)
         try
         {
            wait(10);
         } catch (InterruptedException c)
         {
         }

      String ext = FilenameUtils.getExtension(file.getAbsolutePath()).toLowerCase();
      if (ext == null)
         return;
      try
      {
         BufferedImage img = new BufferedImage(im.getWidth(), im.getHeight(), BufferedImage.TYPE_INT_RGB);
         img.createGraphics().drawImage(im, null, null);
         if (ext.equals("jpg") || ext.equals("jpeg"))
            ImageUtilities.writeJpeg(img, 1.0f, file);
         else if (ext.equals("png"))
            ImageUtilities.writePng(img, file);
         else if (ext.equals("gif"))
            ImageUtilities.writeGif(img, file);
         else if (ext.equals("tif") || ext.equals("tiff"))
            ImageUtilities.writeTiff(img, TiffConstants.TIFF_COMPRESSION_UNCOMPRESSED, file);
         else if (ext.equals("bmp"))
            ImageUtilities.writeBmp(img, file);
         else if (ext.equals("pcx"))
            ImageUtilities.writePcx(img, file);
         else
            throw new IllegalArgumentException("Invalid file extension " + ext);
      } catch (IOException e)
      {
         logger.error("I/O exception for " + file.getAbsolutePath());
      }

      fireFrameRendered();
   }

   public void writeYUV(String genericFileName)
   {
      if (offScreenCanvas == null)
      {
         fireFrameRendered();
         return;
      }
      offScreenRenderDone = false;
      offScreenCanvas.renderOffScreenBuffer();
      if (!storingFrames)
         offScreenCanvas.waitForOffScreenRendering();
      while (!offScreenRenderDone)
         try
         {
            wait(10);
         } catch (InterruptedException c)
         {
         }
      int[] content = null;
      content = im.getData().getPixels(0, 0, im.getWidth(), im.getHeight(), content);
      boolean doWrite = false;
      testcol:
      for (int k = 0; k < 3; k++)
      {
         int cont = content[k];
         for (int i = k; i < content.length; i += 3)
            if (content[i] != cont)
            {
               doWrite = true;
               break testcol;
         }
      }
      if (doWrite)
      {
         if (yuvSaver == null || yuvSaver.getHeight() != im.getHeight() || yuvSaver.getWidth() != im.getWidth())
            yuvSaver = new YUVSaver(im.getWidth(), im.getHeight(), genericFileName);
         yuvSaver.saveEncoded(im, controlsPanel.getMovieCreationPanel().getCurrentFrameNumber());
      }
      fireFrameRendered();
   }

    public void setFogActive(boolean active) {
        if (active) {
            objRotate.addChild(myFogGroup);
        } else {
            objRotate.removeChild(myFogGroup);
        }
    }

    public void setFogRange(double front, double back) {
        myFog.setFrontDistance(front);
        myFog.setBackDistance(back);
    }

    public void drawLocal2D(J3DGraphics2D vGraphics, LocalToWindow ltw, int w, int h) {
        if (titles == null || titles.isEmpty()) {
            return;
        }
        Font f = vGraphics.getFont();
        Color c = vGraphics.getColor();
        for (Title title : titles) {
            float fh = h * title.getFontHeight();
            if (fh < 5) {
                fh = 5;
            }
            Font actualFont = title.getFont().deriveFont(fh);
            vGraphics.setFont(actualFont);
            FontMetrics fm = vGraphics.getFontMetrics();
            int strWidth = fm.stringWidth(title.getTitle());
            vGraphics.setColor(title.getColor());
            int xPos = w / 50;
            if (title.getHorizontalPosition() == Title.CENTER) {
                xPos = (w - strWidth) / 2;
            }
            if (title.getHorizontalPosition() == Title.RIGHT) {
                xPos = w - strWidth - getWidth() / 50;
            }
            vGraphics.drawString(title.getTitle(), xPos,
                                 (int) (effectiveHeight * title.getVerticalPosition()) + actualFont.getSize());
        }
        vGraphics.setFont(f);
        vGraphics.setColor(c);
    }

    @Override
    public int getAreaWidth() {
        return this.getWidth();
    }

    @Override
    public int getAreaHeight() {
        return effectiveHeight;
    }

    @Override
    public void draw2D(J3DGraphics2D vGraphics, LocalToWindow ltw, int h, int w) {
        try {
            drawLocal2D(vGraphics, ltw, h, w);
            rootObject.draw2D(vGraphics, ltw, h, w);
        } catch (Exception e) {
        }
    }

   @Override
   public void addChild(GeometryObject child)
   {
      synchronized (this)
      {
         boolean wasRendererRunning = canvas.isRendererRunning();
         try
         {
             if(rootObject.getChildren().contains(child))
                 return;
             
            if (wasRendererRunning)
               canvas.stopRenderer();
            child.setCurrentViewer(this);
            child.setRenderingListener(renderListener);
            rootObject.addChild(child);
            addBgrColorListener(child.getBackgroundColorListener());
            fireBgrColorChanged();
            //updateExtents();
            if (wasRendererRunning)
            {
               Thread.sleep(100);
               canvas.startRenderer();
            }
         } catch (Exception e)
         {
            if (wasRendererRunning)
            {
               canvas.startRenderer();
            }
         }
      }
   }

   @Override
   public boolean removeChild(GeometryObject child)
   {
      synchronized (this)
      {

         boolean success = rootObject.removeChild(child);
         if (success)
         {
            child.setCurrentViewer(null);
            child.setParentGeom(null);
         }
         return success;
      }
   }

    @Override
    public OpenBranchGroup getGeometryObj() {
        return rootObject.getGeometryObj();
    }

    @Override
    public void clearAllGeometry() {
        SortedSet<GeometryObject> children = rootObject.getChildren();
        for (GeometryObject child : children) {
            child.setCurrentViewer(null);
            child.setParentGeom(null);
        }
        rootObject.clearAllGeometry();

        addChild(pickObject.getOutObject());
    }

    @Override
    public void addNode(Node node) {
        synchronized (this) {
            if (node instanceof OpenBranchGroup) {
                ((OpenBranchGroup) node).setCurrentViewer(this);
            } else if (node instanceof OpenTransformGroup) {
                ((OpenTransformGroup) node).setCurrentViewer(this);
            }

            objScene.addChild(node);
        }
    }

    public void setSaveYUV(boolean saveYUV) {
        yuv = saveYUV;
    }

    @Override
    public void addFrameRenderedListener(FrameRenderedListener l) {
        if (l != null) {
            synchronized (frameRenderedListeners) {
                frameRenderedListeners.add(l);
            }
        }
    }

    @Override
    public void removeFrameRenderedListener(FrameRenderedListener l) {
        if (l != null) {
            synchronized (frameRenderedListeners) {
                frameRenderedListeners.remove(l);
            }
        }
    }

    protected void fireFrameRendered() {
        FrameRenderedEvent e = new FrameRenderedEvent(this);
        synchronized (frameRenderedListeners) {
            for (FrameRenderedListener l : frameRenderedListeners) {
                l.frameRendered(e);
            }
        }
        animate();
    }

    @Override
    public void addBgrColorListener(ColorListener l) {
        if (l != null) {
            bgrColorListeners.add(l);
        }
    }

    @Override
    public void removeBgrColorListener(ColorListener l) {
        if (l != null) {
            bgrColorListeners.remove(l);
        }
    }

    protected void fireBgrColorChanged() {
        ColorEvent e = new ColorEvent(this, bgColor.get());
        for (ColorListener l : bgrColorListeners) {
            l.colorChoosen(e);
        }
    }

    public void setTitles(ArrayList<Title> titles) {
        this.titles = titles;
        this.forceRender();
    }

    public void setStoringJPEG(boolean storingJPEG) {
        this.storingJPEG = storingJPEG;
    }

    public void setStoringPNG(boolean storingPNG) {
        this.storingPNG = storingPNG;
    }

    public void setStoringFrames(boolean storingFrames) {
        this.storingFrames = storingFrames;
    }

    public boolean isStoringFrames() {
        return this.storingFrames;
    }

    public void animate(double[] params) {
        if (lockView) {
            return;
        }

        objRotate.getTransform(tempTransform);
        Transform3D rot = new Transform3D();
        Transform3D tmp = new Transform3D();
        tmp.rotX(params[0]);
        rot.mul(tmp);
        tmp.rotY(params[1]);
        rot.mul(tmp);
        tmp.rotZ(params[2]);
        rot.mul(tmp);
        rot.mul(tempTransform);
        objRotate.setTransform(rot);
        objTranslate.getTransform(rot);
        Vector3d trans = new Vector3d(params[3], params[4], params[5]);
        tmp = new Transform3D();
        tmp.setTranslation(trans);
        rot.mul(tmp);
        objTranslate.setTransform(rot);
        mouseScale *= params[6];
        tempTransform = new Transform3D(new Matrix3d(1., 0., 0.,
                                                 0., 1., 0.,
                                                 0., 0., 1.),
                                    //sceneCenter,
                                    new Vector3d(0.0, 0.0, 0.0),
                                    externScale * mouseScale);
        objScale.setTransform(tempTransform);
    }

    public void animate() {
        if (controlsPanel != null && controlsPanel.animate()) {
            animate(controlsPanel.getAnimationParams());
        } else if (storingFrames) {
        }
    }

    @Override
    public void updateExtents() {
        //rootObject overrides updateExtents method on declaration
        rootObject.updateExtents();
    }

    @Override
    public AbstractRenderingParams getRenderingParams() {
        return objectDisplayParams;
    }

    @Override
    public String toString() {
        return "scene";
    }

    @Override
    public void printDebugInfo() {
        System.out.println("current view locale has "
                + universe.getLocale().numBranchGraphs() + " active obiects");
        windowRootObject.printDebugInfo();
        objScene.printDebugInfo();
    }

    @Override
    public void setTransparency() {
        rootObject.setTransparency();
    }

    @Override
    public void setLineThickness() {
        rootObject.setLineThickness();
    }

    @Override
    public void setLineStyle() {
        rootObject.setLineStyle();
    }

    @Override
    public void setShininess() {
        rootObject.setShininess();
    }

    @Override
    public void setTransform(Transform3D transform) {
        if (lockView) {
            return;
        }

        objScale.setTransform(transform);
    }

    public Canvas3D getCanvas() {
        return canvas;
    }

    @Override
    public SortedSet<GeometryObject> getChildren() {
        return rootObject.getChildren();
    }

    public void setMouseRotateSensitivity(double mouseRotateSensitivityFactor) {
        mouseRotate.setFactor(mouseRotateSensitivityFactor * mouseRotateSensitivity);
    }

    public void setMouseTranslateSensitivity(double mouseTranslateSensitivityFactor) {
        mouseTranslate.setFactor(mouseTranslateSensitivityFactor * mouseTranslateSensitivity);
        mouseZoom.setFactor(mouseTranslateSensitivityFactor * mouseTranslateSensitivity);
    }

    public void setMouseWheelSensitivity(double step) {
        this.mouseWheelSensitivity = step;
    }

    public void setStereoEnable(boolean stereo) {
        if (canvas != null) {
            canvas.setStereoEnable(stereo);
        }
    }

    public boolean getStereoAvailable() {
        return canvas.getStereoAvailable();
    }

    public void setStereoSeparation(float s) {
        if (s > 10) {
            return;
        }
        double[] defLEye = new double[3];
        double[] defREye = new double[3];
        double[] lEye = new double[3];
        double[] rEye = new double[3];
        defaultLeftEye.get(defLEye);
        defaultRightEye.get(defREye);
        for (int i = 0; i < defLEye.length; i++) {
            lEye[i] = defLEye[i] + (1. - s) * (defREye[i] - defLEye[i]) / 2;
            rEye[i] = defREye[i] + (1. - s) * (defLEye[i] - defREye[i]) / 2;
        }
        view.getPhysicalBody().setLeftEyePosition(new Point3d(lEye));
        view.getPhysicalBody().setRightEyePosition(new Point3d(rEye));
    }

    @Override
    public void setColor() {
    }
    /**
     * Utility field holding list of ProjectionListeners.
     */
    private transient ArrayList<ProjectionListener> projectionListenerList = new ArrayList<ProjectionListener>();

    /**
     * Registers ProjectionListener to receive events.
     *
     * @param listener The listener to register.
     */
    @Override
    public synchronized void addProjectionListener(ProjectionListener listener) {
        if (listener != null) {
            projectionListenerList.add(listener);
        }
    }

    /**
     * Removes ProjectionListener from the list of listeners.
     *
     * @param listener The listener to remove.
     */
    @Override
    public synchronized void removeProjectionListener(ProjectionListener listener) {
        if (listener != null) {
            projectionListenerList.remove(listener);
        }
    }

    /**
     * Notifies all registered listeners about the event.
     *
     * @param event The event to be fired
     */
    private void fireProjectionChanged(ProjectionEvent evt) {
        try {
            for (ProjectionListener listener : projectionListenerList) {
                listener.projectionChanged(evt);
            }
        } catch (java.util.ConcurrentModificationException e) {
            try {
                Thread.sleep(50);
            } catch (Exception x) {
            }
        }
    }
    /**
     * Utility field holding list of MouseRestingListeners.
     */
    private transient ArrayList<MouseRestingListener> mouseRestingListenerList = new ArrayList<MouseRestingListener>();

    /**
     * Registers MouseRestingListener to receive events.
     *
     * @param listener The listener to register.
     */
    public synchronized void addMouseRestingListener(MouseRestingListener listener) {
        if (listener != null) {
            mouseRestingListenerList.add(listener);
        }
    }

    /**
     * Removes MouseRestingListener from the list of listeners.
     *
     * @param listener The listener to remove.
     */
    public synchronized void removeMouseRestingListener(MouseRestingListener listener) {
        if (listener != null) {
            mouseRestingListenerList.remove(listener);
        }
    }

    /**
     * Notifies all registered listeners about the event.
     *
     * @param source object.
     * @param ix     x mouse position.
     * @param iy     y mouse position.
     *
     */
    private void fireMouseResting(int ix, int iy) {
        MouseRestingEvent e = new MouseRestingEvent(this, ix, iy);
        for (MouseRestingListener listener : mouseRestingListenerList) {
            listener.mouseResting(e);
        }
    }

    public void startRenderer() {
        canvas.startRenderer();
    }

    public void displayControls() {
        if (controlsFrame != null) {
            controlsFrame.setVisible(true);
        }
    }

    public void moveCamera() {
        if (parentFrame != null && parentFrame instanceof Display3DFrame) {
            ((Display3DFrame) parentFrame).getTransformMenu().setText("transforming camera");
        }
        transformedNode = TRANSFORMED_CAMERA;
        mouseRotate.setTransformGroup(universe.getViewingPlatform().getViewPlatformTransform());
        mouseTranslate.setTransformGroup(universe.getViewingPlatform().getViewPlatformTransform());
        mouseZoom.setTransformGroup(universe.getViewingPlatform().getViewPlatformTransform());
    }

    public void moveScene() {
        if (parentFrame != null && parentFrame instanceof Display3DFrame) {
            ((Display3DFrame) parentFrame).getTransformMenu().setText("transforming object");
        }
        transformedNode = TRANSFORMED_OBJECT;
        mouseRotate.setTransformGroup(objRotate);
        mouseTranslate.setTransformGroup(objRotate);
        mouseZoom.setTransformGroup(objRotate);
    }
    
   /**
   * if currently selected module (module visible in the controls panel)
   * is a visualization module and its geometry output is connected to the viewer,
   * currentModule is this currently selected module, 
   * otherwise currentModule is set to null
   * currentObject is the currentModule generated object or null if currentModule is null
   */
   protected GeometryObject currentObject = null;
   protected VisualizationModule  currentModule = null;
   
   protected void updateCurrentModule()
   {
      if (application == null)
      {
         currentObject = null;
         currentModule = null;
         return;
      }
      ModuleCore currentCore = application.getFrames().getGuiPanel().getCurrentCore();
      if (currentCore == null || !(currentCore instanceof VisualizationModule))
      {
         currentObject = null;
         currentModule = null;
         return;
      }
      currentModule = (VisualizationModule)currentCore;
      currentObject = currentModule.getOutObject();
      if (rootObject.isAncestor(currentObject))
         return;
      currentObject = null;
      currentModule = null;
   }

   /**
    * transformingCurrentObject is an indicator for mouse transformation modes - 
    * if true, the mouse transforms only the current object
    */
   protected boolean transformingCurrentObject = false; 
   /**
    * simple switch of mouse behavior to transform a selected object caused 
    * the transformations to be executed in local coordinates with an unnatural 
    * effect of mouse motions.
    * To overcome this, a special alternate branch is added to vWorld for tempoarry attaching 
    * the current object
    */
   protected OpenBranchGroup alternateTransormBranch = new OpenBranchGroup();
   protected OpenTransformGroup alternateTransformObj = new OpenTransformGroup();
   protected OpenBranchGroup alternateRootObj = new OpenBranchGroup();
   
       
   private   OpenTransformGroup tg = null;
   private   OpenBranchGroup pg = null, ppg = null;
   private   Matrix4f invLTW = new Matrix4f();
      
      
   protected void moveCurrentObject()
   {
      Transform3D tr = null;
      updateCurrentModule();
      if (currentObject == null)
          return;
      transformingCurrentObject = !transformingCurrentObject;
      if (transformingCurrentObject)
      {
         System.out.println("start: "+externScale+"*"+mouseScale);
         if (parentFrame != null && parentFrame instanceof Display3DFrame)
            ((Display3DFrame) parentFrame).getTransformMenu().setText("transforming object");
         tg = currentObject.getTransformObj();
         pg = (OpenBranchGroup)tg.getParent();
         ppg = (OpenBranchGroup)pg.getParent();
         LocalToWindow ltw = new LocalToWindow(tg.getChild(0), canvas);
         ltw.update();
         tr = ltw.getLocalToVworld();
         float[] t = new float[16];
         tr.get(t);
         for (int i = 0; i < t.length; i += 4)
            System.out.printf("%5.3f %5.3f %5.3f %5.3f %n", t[i], t[i+1], t[i+2], t[i+3]);
         Matrix4f cltwMatrix = new Matrix4f();
         tr.get(cltwMatrix);
         invLTW.invert(cltwMatrix);
         float[] row = new float[4];
         System.out.println("inv");
         for (int i = 0; i < 4; i++)
         {
            invLTW.getRow(i, row);
            System.out.printf("%5.3f %5.3f %5.3f %5.3f %n", row[0], row[1],row[2], row[3]);
         }
         ppg.removeChild(pg);
         tg.setTransform(tr);
         alternateRootObj.addChild(pg);
         transformedNode = TRANSFORMED_OBJECT;
         mouseRotate.setTransformGroup(tg);
         mouseTranslate.setTransformGroup(tg);
         mouseZoom.setTransformGroup(tg);
      }
      else
      {
         if (tg == null)
            return;
         System.out.println("end:    "+externScale+"*"+mouseScale);
         LocalToWindow ltw = new LocalToWindow(tg.getChild(0), canvas);
         ltw.update();
         tr = ltw.getLocalToVworld();
         Matrix4f trMatrix = new Matrix4f();
         tr.get(trMatrix);
         float[] t = new float[16];
         tr.get(t);
         for (int i = 0; i < t.length; i += 4)
            System.out.printf("%5.3f %5.3f %5.3f %5.3f %n", t[i], t[i+1], t[i+2], t[i+3]);
         Matrix4f objMatrix = new Matrix4f();
         objMatrix.mul(invLTW, trMatrix);
         alternateRootObj.removeChild(pg);
         tr.set(objMatrix);
         tr.get(t);
         for (int i = 0; i < t.length; i += 4)
            System.out.printf("%5.3f %5.3f %5.3f %5.3f %n", t[i], t[i+1], t[i+2], t[i+3]);
         tg.setTransform(tr);
         ppg.addChild(pg);
         moveScene();
      }
   }

    public ArrayList<EditableDirectionalLight> getDirectionalLights() {
        return directionalLights;
    }

    public EditableDirectionalLight getDirectionalLight(int n) {
        if (n < 0 || n >= directionalLights.size()) {
            return null;
        }
        return directionalLights.get(n);
    }

    public EditablePointLight getPointLight(int n) {
        if (n < 0 || n >= pointLights.size()) {
            return null;
        }
        return pointLights.get(n);
    }

    public void moveDirectionalLight(int l) {
        if (parentFrame != null && parentFrame instanceof Display3DFrame) {
            ((Display3DFrame) parentFrame).getTransformMenu().setText("transforming light");
        }
        transformedNode = TRANSFORMED_LIGHT;
        mouseRotate.setTransformGroup(directionalLightTransforms.get(l));
    }

    public void modifyDirectionalLight(int k, Color3f color, boolean on, boolean biDirectional) {
        if (k < 0 || k >= directionalLights.size()) {
            return;
        }
        EditableDirectionalLight l = directionalLights.get(k);
        l.setEnabled(on);
        l.setBiDirectional(biDirectional);
        l.setLightColor(color);
    }

    public EditableAmbientLight getAmbientLight() {
        return ambientLight;
    }

    public ArrayList<EditablePointLight> getPointLights() {
        return pointLights;
    }

    public void movePointLight(int l) {
        if (parentFrame != null && parentFrame instanceof Display3DFrame) {
            ((Display3DFrame) parentFrame).getTransformMenu().setText("transforming light");
        }
        transformedNode = TRANSFORMED_LIGHT;
        mouseTranslate.setTransformGroup(pointLightTransforms.get(l));
        mouseZoom.setTransformGroup(pointLightTransforms.get(l));
    }

    public void resetPointLightPosition(int l) {
        if (l < 0 || l >= pointLightTransforms.size()) {
            return;
        }
        pointLightTransforms.get(l).setTransform(new Transform3D());
    }

    public void modifyPointLight(int k, PointLightParams par, boolean on) {
        if (k < 0 || k >= pointLights.size()) {
            return;
        }
        EditablePointLight l = pointLights.get(k);
        l.setEnabled(on);
        Color c = par.getColor();
        l.setLightColor(new Color3f(c.getRed() / 512.f, c.getGreen() / 512.f, c.getGreen() / 512.f));
        l.setAttenuation(par.getConstantAttenuation(), par.getLinearAttenuation(), par.getQuadraticAttenuation());
    }

    private void initializeOnceCrosshairCursor() {
        if (crosshairCursor == null) {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            BufferedImage crosshairCursorImage = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
            int[] crosshairCursorRaster = new int[32 * 32];
            for (int i = 0; i < crosshairCursorRaster.length; i++) {
                crosshairCursorRaster[i] = 0;
            }
            for (int i = 0; i < 14; i++) {
                crosshairCursorRaster[15 * 32 + i] = crosshairCursorRaster[15 * 32 + 31 - i] = 0xff888888;
                crosshairCursorRaster[15 + 32 * i] = crosshairCursorRaster[15 + 32 * (31 - i)] = 0xff888888;
            }
            crosshairCursorImage.setRGB(0, 0, 32, 32, crosshairCursorRaster, 0, 32);
            crosshairCursor = toolkit.createCustomCursor(crosshairCursorImage, new Point(15, 15), "crosshairCursor");
        }
    }

    public void setPick3DActive(boolean pick3DActive) {

        if (!pick3DActive) {
            this.removeChild(pickObject.getOutObject());
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } else {
            this.addChild(pickObject.getOutObject());
//            setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR)); // not needed?
            initializeOnceCrosshairCursor();
            setCursor(crosshairCursor);
        }

        this.pickObject.setActive(pick3DActive);

    }

    public void setCrosshairCursor() {
    }

    public Bounds getJ3DBounds() {
        return bounds;
    }

    public SimpleUniverse getUniverse() {
        return universe;
    }

    public ModelClip getModelClip() {
        return modelClip;
    }

    public void setClipRange(double front, double back) {
        view.setFrontClipDistance(front);
        view.setBackClipDistance(back);
    }

    public void setParentFrame(JFrame parentFrame) {
        this.parentFrame = parentFrame;
    }

    public void setFoV(int fov) {
        if (fov < 20) {
            fov = 20;
        }
        if (fov > 160) {
            fov = 160;
        }
        view.setFieldOfView(Math.PI * fov / 180.);
    }

    public void setReperSize(int size) {
        reperSize = size;
        if (size > 10) {
            resizeAxesGlyph();
            if (!showReper) {
                objReper.addChild(reper.getGeometryObj());
            }
            showReper = true;
        } else {
            objReper.removeAllChildren();
            showReper = false;
        }
    }

    private void resizeAxesGlyph() {
        float s = (float) reperSize / this.getWidth() + .05f;
        float y = (float) this.getHeight() / this.getWidth();
        positionedReper.setTransform(new Transform3D(new float[]{s, 0, 0, -(1 - s),
                                                                 0, s, 0, -(y - s),
                                                                 0, 0, s, 0,
                                                                 0, 0, 0, 1}));
    }

    public void clearCanvas() {
        try {
            canvas.stopRenderer();
            Thread.sleep(200);
            remove(canvas);
            Thread.sleep(200);
            rootObject.clearAllGeometry();
            Thread.sleep(200);
            canvas = null;
        } catch (Exception e) {
        }
    }

    public void setTransientControlsFrame(JFrame transientControlsFrame) {
        this.transientControlsFrame = transientControlsFrame;
    }

   // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
   private void initComponents()
   {

      setBorder(javax.swing.BorderFactory.createEtchedBorder());
      setMinimumSize(new java.awt.Dimension(500, 350));
      setPreferredSize(new java.awt.Dimension(1000, 600));
      addComponentListener(new java.awt.event.ComponentAdapter()
      {
         public void componentResized(java.awt.event.ComponentEvent evt)
         {
            formComponentResized(evt);
         }
      });
      setLayout(new java.awt.BorderLayout());
   }// </editor-fold>//GEN-END:initComponents

   private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
       controlsPanel.setWindowDimensions(this.getWidth(), this.getHeight());
       effectiveHeight = getHeight();
       effectiveWidth = getWidth();
       resizeAxesGlyph();
   }//GEN-LAST:event_formComponentResized

    public static enum ReperType {

        AXES, MEDICAL
    };

    public void setReperType(ReperType reperType) {
        objReper.removeAllChildren();
        switch (reperType) {
            case AXES:
                reper = new Reper();
                break;
            case MEDICAL:
                reper = new MedicalReper();
                break;
        }
        objReper.addChild(reper.getGeometryObj());
    }
   // Variables declaration - do not modify//GEN-BEGIN:variables
   // End of variables declaration//GEN-END:variables
    private boolean postRenderSilent = false;

    public void setPostRenderSilent(boolean value) {
        if (storingFrames) {
            if (!value) {
                if (!waitForExternalTrigger) {
                    canvas.startRenderer();
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException ex) {
                    }
                }
                this.postRenderSilent = value;
            } else {
                this.postRenderSilent = value;
                canvas.stopRenderer();
            }
        } else {
            this.postRenderSilent = value;
        }
    }

    public void forceRender() {
        canvas.stopRenderer();
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
        }
        waitForExternalTrigger = false;
        canvas.startRenderer();
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
        }
    }
    protected boolean waitForExternalTrigger = false;

    public void setWaitForExternalTrigger(boolean value) {
        this.waitForExternalTrigger = value;
    }

    public boolean isWaitingForExternalTrigger() {
        return waitForExternalTrigger;
    }

    public Display3DControlsPanel getControlsPanel() {
        return controlsPanel;
    }

    @Override
    public void refresh() {
        forceRender();
    }
    private boolean lockView = false;

    /**
     * @return the lockView
     */
    public boolean isLockView() {
        return lockView;
    }

    /**
     * @param lockView the lockView to set
     */
    public void setLockView(boolean lockView) {
        this.lockView = lockView;
        if (this.parentFrame != null && parentFrame instanceof Display3DFrame) {
            ((Display3DFrame) parentFrame).setLockInfo(lockView);
        }
        if (this.controlsPanel != null) {
            controlsPanel.setLockViewButtonState(lockView);
        }

        mouseRotate.setEnable(!lockView);
        mouseTranslate.setEnable(!lockView);
        mouseZoom.setEnable(!lockView);
    }

    /**
     * This method should prevent viewer from keeping huge reper in some situations, especially on
     * Mac OS X.
     */
    public void kick() {
        objReper.refreshTransform();
    }
}
