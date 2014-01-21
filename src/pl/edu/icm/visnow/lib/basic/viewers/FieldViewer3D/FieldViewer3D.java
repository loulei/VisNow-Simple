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
package pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pl.edu.icm.visnow.datasets.IrregularField;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.engine.core.InputEgg;
import pl.edu.icm.visnow.engine.core.LinkFace;
import pl.edu.icm.visnow.engine.core.ModuleCore;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.geometries.objects.GeometryObject;
import pl.edu.icm.visnow.lib.basic.mappers.VolumeRenderer.FieldPick.FieldPickEvent;
import pl.edu.icm.visnow.lib.basic.mappers.VolumeRenderer.FieldPick.FieldPickListener;
import pl.edu.icm.visnow.lib.basic.mappers.VolumeRenderer.VolumeRenderer;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.DataProvider.DataProvider;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.CalculableParams;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.ConnectionDescriptor;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.GeometryFieldConverter;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.GeometryParams;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.GeometryUI;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.Glypher;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.PointDescriptor;
import pl.edu.icm.visnow.lib.types.VNGeometryObject;
import pl.edu.icm.visnow.lib.types.VNIrregularField;
import pl.edu.icm.visnow.lib.types.VNRegularField;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;

/**
 * @author Bartosz Borucki (babor@icm.edu.pl) University of Warsaw,
 * Interdisciplinary Centre for Mathematical and Computational Modelling
 */
public class FieldViewer3D extends ModuleCore {

    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(FieldViewer3D.class);
    private FieldDisplay3DFrame display3DFrame = null;
    private VolumeRenderer volRender = null;
    private RegularField currentField = null;
    private GUI ui = null;
    private DataProvider dp = null;
    private GeometryUI geomUI = null;
    private GeometryParams gparams = new GeometryParams();
    //private CalculableUI calcUI = null;
    private CalculableParams cparams = new CalculableParams();
    private Glypher glypher = new Glypher();
    private boolean fromGeomUI = false;
    private Vector<GeometryObject> inObjects = new Vector<GeometryObject>();

    /**
     * Creates a new instance of TestViewer3D
     */
    public FieldViewer3D() {
        volRender = new VolumeRenderer(false);
        dp = new DataProvider();

        glypher.setParams(gparams);
        glypher.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                display3DFrame.getDisplay3DPanel().repaint();
            }
        });

        volRender.addPick3DListener(new FieldPickListener() {
            @Override
            public void handlePick3D(FieldPickEvent e) {
                gparams.addPoint(e.getIndices());
            }
        });


        SwingInstancer.swingRunAndWait(new Runnable() {
            @Override
            public void run() {
                ui = new GUI();
                display3DFrame = new FieldDisplay3DFrame();
                display3DFrame.setTitle("FieldViewer3D");
                display3DFrame.setLocation(100, 100);
                display3DFrame.setDataProvider(dp);
                display3DFrame.getDisplay3DPanel().addChild(volRender.getOutObject());
                display3DFrame.setGeometryParams(gparams);
                display3DFrame.setCalculableParams(cparams);
                display3DFrame.getDisplay3DPanel().addChild(glypher.getGlyphsObject());
                geomUI = new GeometryUI(display3DFrame);
                geomUI.setParams(gparams, cparams);

                geomUI.addChangeListener(display3DFrame.getManager());
                display3DFrame.addUI(dp.getUI(), "Slices");
                display3DFrame.addUI(volRender.getVolRenderUI(), "3D View");
                display3DFrame.addUI(geomUI, "Geometry");

                ui.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent evt) {
                        dp.getUI().setSimpleGUI(ui.isSimpleGUI());
                        volRender.getVolRenderUI().setPresentation(ui.isSimpleGUI());
                        geomUI.setSimpleGUI(ui.isSimpleGUI());

                        display3DFrame.setSimpleGUI(ui.isSimpleGUI());
                        display3DFrame.setVisible(true);
                    }
                });

                geomUI.getOutputPointsButton().addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        fromGeomUI = true;
                        startAction();
                    }
                });

                setPanel(ui);
                display3DFrame.setVisible(true);
            }
        });
    }

    @Override
    public void onDelete() {
        //WTF-MUI: getUI().removeAll();
        ui = null;
        display3DFrame.getDisplay3DPanel().clearCanvas();
        if (display3DFrame.getDisplay3DPanel().getControlsFrame() != null) {
            display3DFrame.getDisplay3DPanel().getControlsFrame().dispose();
        }
        if (display3DFrame.getDisplay3DPanel().getTransientControlsFrame() != null) {
            display3DFrame.getDisplay3DPanel().getTransientControlsFrame().dispose();
        }
        display3DFrame.dispose();
        volRender = null;
        display3DFrame = null;
        dp = null;
    }
    public static InputEgg[] inputEggs = null;
    public static OutputEgg[] outputEggs = null;

    @Override
    public void onActive() {
        if (!fromGeomUI) {
            //gparams.clearConenctions();
            gparams.clearPoints();
            if (getInputFirstValue("inField") == null || ((VNRegularField) getInputFirstValue("inField")).getField() == null) {
                dp.setInField(null);
                volRender.setInField(null);
                gparams.setInField(null);
                currentField = null;
                return;
            }

            RegularField inField = ((VNRegularField) getInputFirstValue("inField")).getField();
            if (inField != currentField) {
                currentField = inField;
                if (inField == null || (inField.getDims().length != 3 && inField.getDims().length != 2)) {
                    inField = null;
                }

                dp.setInField(inField);
                gparams.setInField(inField);
                if (inField.getDims().length == 3) {
                    volRender.setInField(inField);
//                    SwingInstancer.swingRunAndWait(new Runnable() {
//                        @Override
//                        public void run() {
//                            display3DFrame.getDisplay3DPanel().reset();
//  TU                           display3DFrame.setMode(FieldDisplay3DFrame.MODE_3D);
//                            display3DFrame.setUIVisible("3D View", true);
//                            dp.resetCustomPlane();
//                            dp.resetCustomOrthoPlanes();
//                            dp.updateOrthosliceImages();
//                            dp.centerSlices();
//                        }
//                    });
                } else {
//                    SwingInstancer.swingRunAndWait(new Runnable() {
//                        @Override
//                        public void run() {
// i TU                            display3DFrame.setMode(FieldDisplay3DFrame.MODE_2D);
//                            display3DFrame.setUIVisible("3D View", false);
//                        }
//                    });
                }
            }

            if (getInputFirstValue("inPointsGeometryField") != null
                    && ((VNIrregularField) getInputFirstValue("inPointsGeometryField")).getField() != null) {
                IrregularField inPointsGeometryField = ((VNIrregularField) getInputFirstValue("inPointsGeometryField")).getField();
                ArrayList<PointDescriptor> pointsDescriptors = new ArrayList<PointDescriptor>();
                ArrayList<ConnectionDescriptor> connectionDescriptors = new ArrayList<ConnectionDescriptor>();
                GeometryFieldConverter.field2pac(inPointsGeometryField, inField, pointsDescriptors, connectionDescriptors);
                
                PointDescriptor pd;
                for (int i = 0; i < pointsDescriptors.size(); i++) {
                    pd = pointsDescriptors.get(i);
                    gparams.addPoint(pd.getName(), pd.getIndices(), pd.getMembership());                    
                }
                ConnectionDescriptor cd;
                for (int i = 0; i < connectionDescriptors.size(); i++) {
                    cd = connectionDescriptors.get(i);
                    gparams.addConnection(gparams.getPointsDescriptorByName(cd.getP1().getName()),gparams.getPointsDescriptorByName(cd.getP2().getName()),cd.getName());                    
                }
            }

            glypher.setParams(gparams);

            geomUI.setParams(gparams, cparams);

            display3DFrame.setGeometryParams(gparams);

            if (inField.getDims().length == 3) {
                display3DFrame.getDisplay3DPanel().clearAllGeometry();
                display3DFrame.getDisplay3DPanel().addChild(volRender.getOutObject());
                display3DFrame.getDisplay3DPanel().addChild(glypher.getGlyphsObject());
                Vector ins = getInputValues("inObject");
                for (Object obj : ins) {
                    if ((VNGeometryObject) obj != null && ((VNGeometryObject) obj).getGeometryObject() != null) {
                        display3DFrame.getDisplay3DPanel().addChild(((VNGeometryObject) obj).getGeometryObject());
                    }
                }
                display3DFrame.getDisplay3DPanel().repaint();
            }
        }
        setOutputValue("outPointsGeometryField", new VNIrregularField(gparams.getPointsGeometryField(gparams.isAddFieldData())));
        fromGeomUI = false;
    }

    @Override
    public void onInputDetach(LinkFace link) {
        onActive();
    }

    @Override
    public void onInputAttach(LinkFace link) {
        if (link.getInput().getName().equals("inObject")) {
            onActive();
        }
    }
    
    @Override
    public boolean isViewer() {
        return true;
    }
    
}
