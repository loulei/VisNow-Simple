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
package pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.ReadPAC;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pl.edu.icm.visnow.datasets.IrregularField;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.ConnectionDescriptor;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.GeometryFieldConverter;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.PointDescriptor;
import pl.edu.icm.visnow.lib.templates.visualization.modules.IrregularOutFieldVisualizationModule;
import pl.edu.icm.visnow.lib.types.VNIrregularField;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;

/**
 * @author Bartosz Borucki (babor@icm.edu.pl) Warsaw University,
 * Interdisciplinary Centre for Mathematical and Computational Modelling
 * 19 July 2013
 */
public class ReadPAC extends IrregularOutFieldVisualizationModule {

    public static OutputEgg[] outputEggs = null;
    private ReadPACGUI computeUI = null;
    private boolean firsGeometryRun = true;

    public ReadPAC() {
        SwingInstancer.swingRunAndWait(new Runnable() {
            @Override
            public void run() {
                computeUI = new ReadPACGUI();
                computeUI.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        startAction();
                    }
                });
                ui.addComputeGUI(computeUI);
                setPanel(ui);
            }
        });        
    }
    
    @Override
    public void onActive() {
        if (computeUI.getPath() != null) {
            outField = readPAC(computeUI.getPath());            
            setOutputValue("outField", new VNIrregularField(outField));
            if (outField != null) {
                prepareOutputGeometry();
                if(firsGeometryRun) {
                    firsGeometryRun = false;
                    fieldDisplayParams.getCellSetDisplayParameters(0).getRenderingParams().setLineThickness(5.0f);
                }
                show();
            }
        } else {
            setOutputValue("outField", null);
        }
    }

    @Override
    public void onInitFinishedLocal() {
        if (isForceFlag()) {
            computeUI.activateOpenDialog();
        }
    }

    public static IrregularField readPAC(String filePath) {
        ArrayList<PointDescriptor> pts = new ArrayList<PointDescriptor>();
        ArrayList<ConnectionDescriptor> conn = new ArrayList<ConnectionDescriptor>();
        readPAC(filePath, pts, conn);
        return GeometryFieldConverter.pac2field(pts, conn, true, false, null);
    }
    
    public static void readPAC(String filePath, ArrayList<PointDescriptor> pds, ArrayList<ConnectionDescriptor> cds) {
        if(pds == null || cds == null || filePath == null)
            return;
        
        try {
            File f = new File(filePath);
            BufferedReader input = new BufferedReader(new FileReader(f));

            String line;

            line = input.readLine();
            if (line == null || (!line.equals("points and connections data file") && !line.equals("#PAC data file"))) {
                input.close();
                return;
            }

            line = input.readLine();
            if (line == null || !line.startsWith("points")) {
                input.close();
                return;
            }
            String[] tmp = line.split(" ");
            int nPoints = Integer.parseInt(tmp[1]);

            String[] names = new String[nPoints];
            int[][] pts = new int[nPoints][3];
            float[][] coords = new float[nPoints][3];
            int[] classes = new int[nPoints];

            for (int i = 0; i < nPoints; i++) {
                line = input.readLine();
                tmp = line.split("\t");
                names[i] = new String(tmp[0]);
                pts[i][0] = Integer.parseInt(tmp[1]);
                pts[i][1] = Integer.parseInt(tmp[2]);
                pts[i][2] = Integer.parseInt(tmp[3]);
                if(tmp.length >= 7) {
                    coords[i][0] = Float.parseFloat(tmp[4]);
                    coords[i][1] = Float.parseFloat(tmp[5]);
                    coords[i][2] = Float.parseFloat(tmp[6]);                    
                }
                if (tmp.length >= 8) {
                    classes[i] = Integer.parseInt(tmp[7]);
                } else {
                    classes[i] = -1;
                }
            }

            line = input.readLine();
            line = input.readLine();
            if (line == null || !line.startsWith("connections")) {
                input.close();
                return;
            }
            tmp = line.split(" ");
            int nConns = Integer.parseInt(tmp[1]);
            String[] cnames = new String[nConns];
            int[][] conns = new int[nConns][2];
            for (int i = 0; i < nConns; i++) {
                line = input.readLine();
                if (line == null) {
                    input.close();
                    return;
                }
                tmp = line.split("\t");
                cnames[i] = new String(tmp[0]);
                conns[i][0] = Integer.parseInt(tmp[1]);
                conns[i][1] = Integer.parseInt(tmp[2]);
            }
            input.close();

            for (int i = 0; i < nPoints; i++) {
                if(pts[i][0] == -1 || pts[i][1] == -1 || pts[i][2] == -1) {
                    pds.add(new PointDescriptor(names[i], null, coords[i], classes[i]));                    
                } else {
                    pds.add(new PointDescriptor(names[i], pts[i], coords[i], classes[i]));                    
                }
            }

            for (int i = 0; i < nConns; i++) {
                cds.add(new ConnectionDescriptor(cnames[i], pds.get(conns[i][0]), pds.get(conns[i][1])));
            }
        } catch (Exception ex) {
        }
    }
}
