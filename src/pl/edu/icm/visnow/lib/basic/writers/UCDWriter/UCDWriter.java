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

package pl.edu.icm.visnow.lib.basic.writers.UCDWriter;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pl.edu.icm.visnow.datasets.cells.Cell;
import pl.edu.icm.visnow.datasets.CellArray;
import pl.edu.icm.visnow.datasets.CellSet;
import pl.edu.icm.visnow.datasets.CellSetSchema;
import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.datasets.IrregularField;
import pl.edu.icm.visnow.engine.core.InputEgg;
import pl.edu.icm.visnow.engine.core.ModuleCore;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.lib.types.VNField;
import pl.edu.icm.visnow.lib.types.VNIrregularField;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class UCDWriter extends ModuleCore {

    public static InputEgg[] inputEggs = null;
    public static OutputEgg[] outputEggs = null;
    private GUI ui = null;
    protected Params params;
    protected IrregularField inField;

    public UCDWriter() {
        parameters = params = new Params();
        params.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent evt) {
                startAction();
            }
        });
        SwingInstancer.swingRun(new Runnable() {
            public void run() {
                ui = new GUI();
            }
        });
        ui.setParams(params);
        setPanel(ui);
    }

    public void update() {
        String outFileName = params.getFileName();
        DataArray da;
        byte[] bData;
        short[] sData;
        int[] iData;
        float[] fData;
        double[] dData;
        String dataTypes[] = {
            "byte", "short", "integer", "float", "double"
        };
        DataOutputStream out;
        PrintWriter outA;

        try {
            PrintWriter outFile = new PrintWriter(new FileOutputStream(outFileName));
            if (params.isAVS()) {
                int nNodes = inField.getNNodes();
                int nSpace = inField.getNSpace();
                int nNodeData = inField.getNData();
                int nComponents = 0;
                int nCellComponents = 0;
                for (DataArray dta : inField.getData()) {
                    nComponents += dta.getVeclen();
                }
                for (DataArray dta : inField.getCellSet(0).getData()) {
                    nCellComponents += dta.getVeclen();
                }
                int nCellData = inField.getCellSet(0).getNData();
                int nCellSets = inField.getNCellSets();
                int nCells = 0;
                for (int i = 0; i < nCellSets; i++) {
                    CellSet cs = inField.getCellSet(i);
                    for (int j = 0; j < Cell.TYPES; j++) {
                        CellArray ca = cs.getCellArray(j);
                        if (ca != null) {
                            nCells += ca.getNCells();
                        }
                    }
                }
                CellSetSchema css = inField.getCellSet(0).getSchema();
                for (int i = 1; i < nCellSets; i++) {
                    if (!css.isDataCompatibleWith(inField.getCellSet(i).getSchema())) {
                        ui.setResultText("<html><font color=#FF0000>incompatible cell sets<p>try writing VisNow file</html>");
                        return;
                    }
                }
                outFile.println("# AVS unstructured field file");
                outFile.printf("%1d %6d %3d %3d 0 %n", nNodes, nCells, nComponents, nCellComponents);
                float[] coords = inField.getCoords();
                for (int i = 0; i < nNodes; i++) {
                    switch (nSpace) {
                        case 3:
                            outFile.printf("%1d %8.4f %8.4f %8.4f %n", i, coords[3 * i], coords[3 * i + 1], coords[3 * i + 2]);
                            break;
                        case 2:
                            outFile.printf("%1d %8.4f %8.4f %8.4f %n", i, coords[2 * i], coords[2 * i + 1], 0.f);
                            break;
                    }
                }
                for (int i = 0, n = 0; i < nCellSets; i++) {
                    for (int j = 0; j < Cell.TYPES; j++) {
                        int nv = Cell.nv[j];
                        CellArray ca = inField.getCellSet(i).getCellArray(j);
                        if (ca == null) {
                            continue;
                        }
                        for (int k = 0; k < ca.getNCells(); k++, n++) {
                            outFile.printf("%1d 0 %s ", n, Cell.UCDnames[j]);
                            for (int l = 0; l < nv; l++) {
                                outFile.printf("%6d ", ca.getNodes(k)[l]);
                            }
                            outFile.println();
                        }
                    }
                }
                DataArray[] das = new DataArray[nNodeData];
                int[] vlen = new int[nNodeData];
                float[][] data = new float[nNodeData][];
                outFile.printf("%1d ", nNodeData);
                for (int i = 0; i < nNodeData; i++) {
                    das[i] = inField.getData(i);
                    vlen[i] = das[i].getVeclen();
                    data[i] = das[i].getFData();
                    outFile.printf("%2d ", das[i].getVeclen());
                }
                outFile.println();
                for (int i = 0; i < nNodeData; i++) {
                    outFile.printf("%s, %s %n", das[i].getName(), das[i].getUnit());
                }
                for (int i = 0; i < nNodes; i++) {
                    outFile.printf("%1d ", i);
                    for (int j = 0; j < nNodeData; j++) {
                        for (int k = 0; k < vlen[j]; k++) {
                            outFile.printf("%10.4f ", data[j][i * vlen[j] + k]);
                        }
                    }
                    outFile.println();
                }
                ui.setResultText(outFileName + " succesfully written");
            } else {
                outFile.println("# VisNow unstructured field file");
            }
            outFile.close();
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onActive() {
        if (getInputFirstValue("inField") == null) {
            return;
        }
        Field fld = ((VNField) getInputFirstValue("inField")).getField();
        if (fld instanceof IrregularField) {
            inField = (IrregularField) fld;
        } else {
            inField = null;
        }

        update(); //????
        update();
    }
}
