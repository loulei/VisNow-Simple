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
package pl.edu.icm.visnow.lib.basic.mappers.LineSlice;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.log4j.Logger;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.engine.core.InputEgg;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.lib.templates.visualization.modules.OutFieldVisualizationModule;
import pl.edu.icm.visnow.lib.types.VNRegularField;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class LineSlice extends OutFieldVisualizationModule {

    private static final Logger LOGGER = Logger.getLogger(LineSlice.class);
    public static InputEgg[] inputEggs = null;
    public static OutputEgg[] outputEggs = null;
    private LineSliceGUI computeUI = null;
    protected LineSliceParams params;
    protected RegularField inField = null;
    protected int dim;
    protected int[] dims;
    protected int nComps;
    protected DataArray data;
    protected boolean fromUI = false;
    //apparently period between startAction() and onActive() takes the most time    
    protected boolean waitForRun  = false;

    public LineSlice() {
        parameters = params = new LineSliceParams();
        params.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent evt) {
                if (!params.isActive())
                    return;
                fromUI = true;
                if (waitForRun) return;
                waitForRun = true;
                startAction();
            }
        });
        SwingInstancer.swingRunAndWait(new Runnable() {
            @Override
            public void run() {
                computeUI = new LineSliceGUI();
            }
        });
        computeUI.setParams(params);
        ui.addComputeGUI(computeUI);
        setPanel(ui);
    }

    @Override
    public void onDelete() {
        computeUI = null;
    }

    public void update() {
        LOGGER.debug("");

        int ax = params.getAxis();
        int x = params.getXCoord();
        int y = params.getYCoord();
        int z = params.getZCoord();

        int dimNum = inField.getDimNum();
        int[] dims = inField.getDims();

        //validate sliders
        boolean validSlice = true;
        if (ax != 0 && (x < 0 || x >= dims[0])) validSlice = false;
        if (ax != 1 && dimNum >= 2 && (y < 0 || y >= dims[1])) validSlice = false;
        if (ax != 2 && dimNum >= 3 && (z < 0 || z >= dims[2])) validSlice = false;

        //invalid slice - remove output field
        if (!validSlice) {
            outField = null;
            setOutputValue("outField", null);            
        }
        else {
            int[] outDims = new int[1];
            outDims[0] = dims[ax];
            outField = new RegularField(outDims);
            outRegularField = (RegularField) outField;
            outRegularField.setNSpace(inField.getNSpace());
            float[][] affine = inField.getAffine();
            float[][] sliceAffine = new float[4][3];
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 3; j++) {
                    sliceAffine[i][j] = 0;
                }
            }
            for (int i = 0; i < 3; i++) {
                sliceAffine[0][i] = affine[ax][i];
                if (inField.getDims().length == 2)
                    if (ax == 0)
                        sliceAffine[3][i] = affine[3][i] + y * affine[1][i];
                    else
                        sliceAffine[3][i] = affine[3][i] + x * affine[0][i];
                else if (ax == 0)
                    sliceAffine[3][i] = affine[3][i] + y * affine[1][i] + z * affine[2][i];
                else if (ax == 1)
                    sliceAffine[3][i] = affine[3][i] + x * affine[0][i] + z * affine[2][i];
                else
                    sliceAffine[3][i] = affine[3][i] + x * affine[0][i] + y * affine[1][i];
            }
            outRegularField.setAffine(sliceAffine);


            // now we are rewriting values from input data to output array
            // input data is 1D even when there are many dimensions, therefore
            // step variable must be used

            int n = dims[ax];
            int start = 0, step = 1;
            if (inField.getDims().length == 2) {
                if (ax == 0) {
                    start = y * dims[0];
                    step = 1;
                } else if (ax == 1) {
                    start = x;
                    step = dims[0];
                }
            } else if (ax == 0) {
                start = (z * dims[1] + y) * dims[0];
                step = 1;
            } else if (ax == 1) {
                start = z * dims[1] * dims[0] + x;
                step = dims[0];
            } else {
                start = y * dims[0] + x;
                step = dims[0] * dims[1];
            }
            if (inField.getCoords() != null) {
                int veclen = inField.getNSpace();
                float[] inCoords = inField.getCoords();
                float[] outCoords = new float[n * veclen];
                for (int i = 0, j = start; i < n; i++, j += step) {
                    for (int k = 0; k < veclen; k++) {
                        outCoords[i * veclen + k] = inCoords[j * veclen + k];
                    }
                }
                outRegularField.setCoords(outCoords);
            }

            for (int idata = 0; idata < inField.getNData(); idata++) {
                data = inField.getData(idata);
                if (data == null)
                    continue;


                int veclen = data.getVeclen();
                DataArray outDa = null;
                switch (data.getType()) {
                    case DataArray.FIELD_DATA_BYTE:
                        byte[] inBData = data.getBData();
                        byte[] outBData = new byte[n * veclen];
                        for (int i = 0, j = start; i < n; i++, j += step) {
                            for (int k = 0; k < veclen; k++) {
                                outBData[i * veclen + k] = inBData[j * veclen + k];
                            }
                        }
                        outDa = DataArray.create(outBData, veclen, data.getName());
                        break;
                    case DataArray.FIELD_DATA_SHORT:
                        short[] inSData = data.getSData();
                        short[] outSData = new short[n * veclen];
                        for (int i = 0, j = start; i < n; i++, j += step) {
                            for (int k = 0; k < veclen; k++) {
                                outSData[i * veclen + k] = inSData[j * veclen + k];
                            }
                        }
                        outDa = DataArray.create(outSData, veclen, data.getName());
                        break;
                    case DataArray.FIELD_DATA_INT:
                        int[] inIData = data.getIData();
                        int[] outIData = new int[n * veclen];
                        for (int i = 0, j = start; i < n; i++, j += step) {
                            for (int k = 0; k < veclen; k++) {
                                outIData[i * veclen + k] = inIData[j * veclen + k];
                            }
                        }
                        outDa = DataArray.create(outIData, veclen, data.getName());
                        break;
                    case DataArray.FIELD_DATA_FLOAT:
                        float[] inFData = data.getFData();
                        float[] outFData = new float[n * veclen];
                        for (int i = 0, j = start; i < n; i++, j += step) {
                            for (int k = 0; k < veclen; k++) {
                                outFData[i * veclen + k] = inFData[j * veclen + k];
                            }
                        }
                        outDa = DataArray.create(outFData, veclen, data.getName());
                        break;
                    case DataArray.FIELD_DATA_DOUBLE:
                        double[] inDData = data.getDData();
                        double[] outDData = new double[n * veclen];
                        for (int i = 0, j = start; i < n; i++, j += step) {
                            for (int k = 0; k < veclen; k++) {
                                outDData[i * veclen + k] = inDData[j * veclen + k];
                            }
                        }
                        outDa = DataArray.create(outDData, veclen, data.getName());
                        break;

                    //TODO other DataArray types
                }

                if (outDa != null) {
                    outDa.setMinv(data.getMinv());
                    outDa.setMaxv(data.getMaxv());
                    outDa.setPhysMin(data.getPhysMin());
                    outDa.setPhysMax(data.getPhysMax());
                    outRegularField.addData(outDa);
                }

            }
            updateMinMax();
            setOutputValue("outField", new VNRegularField(outRegularField));
        }
        
        prepareOutputGeometry();
        show();
    }

    @Override
    public void onActive() {
        waitForRun = false;
        LOGGER.debug("FromUI: " + fromUI + " inField: " + (getInputFirstValue("inField") == null ? null : "notNull"));

        if (!fromUI || inField == null) {
            if (getInputFirstValue("inField") == null
                    || ((VNRegularField) getInputFirstValue("inField")).getField() == null)
                return;
            RegularField inFld = ((VNRegularField) getInputFirstValue("inField")).getField();
            if (inFld.getDims() == null || inFld.getDims().length == 1 || inFld.getDims().length > 3)
                return;
            inField = inFld;
            dims = inField.getDims();
            computeUI.setDims(dims);
        }
        update();
        fromUI = false;
    }

    private void updateMinMax() {
        if (params.isRecalculateMinMax() && outField != null) {
            for (int i = 0; i < outField.getNData(); i++) {
                outField.getData(i).recomputeMinMax();
            }
        }
    }
    
}
