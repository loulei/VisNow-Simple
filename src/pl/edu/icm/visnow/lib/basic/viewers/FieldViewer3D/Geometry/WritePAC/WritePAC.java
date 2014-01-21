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

package pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.WritePAC;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pl.edu.icm.visnow.datasets.IrregularField;
import pl.edu.icm.visnow.engine.core.InputEgg;
import pl.edu.icm.visnow.engine.core.LinkFace;
import pl.edu.icm.visnow.engine.core.ModuleCore;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.ConnectionDescriptor;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.GeometryFieldConverter;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.PointDescriptor;
import pl.edu.icm.visnow.lib.types.VNIrregularField;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;

/**
 * @author Bartosz Borucki (babor@icm.edu.pl) Warsaw University,
 * Interdisciplinary Centre for Mathematical and Computational Modelling
 * 19 July 2013
 */
public class WritePAC extends ModuleCore {

    public static InputEgg[] inputEggs = null;
    public static OutputEgg[] outputEggs = null;
    private WritePACGUI ui = null;
    protected WritePACParams params;
    private IrregularField inField = null;

    public WritePAC() {
        parameters = params = new WritePACParams();
        params.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent evt) {
                writePAC(params.getFileName(), inField, null);
            }
        });
        SwingInstancer.swingRunAndWait(new Runnable() {
            @Override
            public void run() {
                ui = new WritePACGUI();
                ui.setParams(params);
                setPanel(ui);
            }
        });
    }

    @Override
    public void onActive() {
        if (getInputFirstValue("inField") == null) {
            return;
        }
        inField = ((VNIrregularField) getInputFirstValue("inField")).getField();
    }

    @Override
    public void onInputAttach(LinkFace link) {
        onActive();
    }

    
    public static void writePAC(String fileName, IrregularField inField, String infoString) {
        if(inField == null || fileName == null)
            return;
        
        ArrayList<PointDescriptor> pts = new ArrayList<PointDescriptor>();
        ArrayList<ConnectionDescriptor> conn = new ArrayList<ConnectionDescriptor>();
        GeometryFieldConverter.field2pac(inField, null, pts, conn);
        writePAC(fileName, pts, conn, infoString);
    }   
    
    public static void writePAC(String fileName, ArrayList<PointDescriptor> pts, ArrayList<ConnectionDescriptor> conn, String infoString) {
        try {
            File f = new File(fileName);
            BufferedWriter output = new BufferedWriter(new FileWriter(f));

            //output.write("points and connections data file");
            output.write("#PAC data file");
            output.newLine();
            String tmp = "";
            int[] indices;
            float[] coords;
            output.append("points " + pts.size());
            output.newLine();
            for (int i = 0; i < pts.size(); i++) {
                indices = pts.get(i).getIndices();
                coords = pts.get(i).getWorldCoords();
                if(indices != null) {
                    tmp = "" + pts.get(i).getName() + "\t" + 
                               indices[0] + "\t" + indices[1] + "\t" + indices[2] + "\t" + 
                               String.format("%.6e\t%.6e\t%.6e\t", coords[0], coords[1], coords[2]) +
                               pts.get(i).getMembership()+ "\t";
                } else {
                    tmp = "" + pts.get(i).getName() + "\t" + 
                               "-1\t-1\t-1\t" + 
                               String.format("%.6e\t%.6e\t%.6e\t", coords[0], coords[1], coords[2]) +
                               pts.get(i).getMembership()+ "\t";
                }
                output.append(tmp);
                output.newLine();
            }
            output.newLine();

            output.append("connections " + conn.size());
            output.newLine();
            int i1, i2;
            for (int i = 0; i < conn.size(); i++) {
                i1 = pts.indexOf(conn.get(i).getP1());
                i2 = pts.indexOf(conn.get(i).getP2());
                tmp = "" + conn.get(i).getName() + "\t" + i1 + "\t" + i2;
                output.append(tmp);
                output.newLine();
            }
            output.newLine();

            if (infoString != null) {
                output.append(infoString);
                output.newLine();
            }

            output.close();
        } catch (Exception ex) {
        }
    }
}