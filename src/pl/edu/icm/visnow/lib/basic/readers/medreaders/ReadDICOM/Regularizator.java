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

package pl.edu.icm.visnow.lib.basic.readers.medreaders.ReadDICOM;

import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.datasets.RegularFieldInterpolator;
import pl.edu.icm.visnow.gui.events.FloatValueModificationEvent;
import pl.edu.icm.visnow.gui.events.FloatValueModificationListener;

/**
 *
 * @author Bartosz Borucki (babor@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class Regularizator {

    public Regularizator() {
        
    }

    public RegularField regularizeToMin(RegularField inField, int nThreads) {
        if( inField == null)
            return null;

        float[][] inAffine = inField.getAffine();
        float dx = 0, dy = 0, dz = 0;
        for (int i = 0; i < 3; i++) {
            dx += inAffine[0][i]*inAffine[0][i];
            dy += inAffine[1][i]*inAffine[1][i];
            dz += inAffine[2][i]*inAffine[2][i];
        }
        dx = (float)Math.sqrt(dx);
        dy = (float)Math.sqrt(dy);
        dz = (float)Math.sqrt(dz);

        dx = Math.min(dx, Math.min(dy, dz));

        return regularize(inField, nThreads, dx);
    }

    public RegularField regularizeToX(RegularField inField, int nThreads) {
        if( inField == null)
            return null;

        float[][] inAffine = inField.getAffine();
        float dx = 0;
        for (int i = 0; i < 3; i++) {
            dx += inAffine[0][i]*inAffine[0][i];
        }
        dx = (float)Math.sqrt(dx);

        return regularize(inField, nThreads, dx);
    }

    public RegularField regularize(RegularField inField, int nThreads, float voxelSize) {
        if( inField == null)
            return null;

        if(voxelSize <= 0)
            return null;

        float[][] inExtents = inField.getExtents();
        float dx = voxelSize;

        int[] outDims = new int[3];
        outDims[0] = (int)Math.floor((inExtents[1][0]-inExtents[0][0])/dx) + 1;
        outDims[1] = (int)Math.floor((inExtents[1][1]-inExtents[0][1])/dx) + 1;
        outDims[2] = (int)Math.floor((inExtents[1][2]-inExtents[0][2])/dx) + 1;

        float[][] outAffine = new float[4][3];
        outAffine[0][0] = dx;
        outAffine[0][1] = 0;
        outAffine[0][2] = 0;
        outAffine[1][0] = 0;
        outAffine[1][1] = dx;
        outAffine[1][2] = 0;
        outAffine[2][0] = 0;
        outAffine[2][1] = 0;
        outAffine[2][2] = dx;
        outAffine[3][0] = inExtents[0][0];
        outAffine[3][1] = inExtents[0][1];
        outAffine[3][2] = inExtents[0][2];


        RegularField outField = new RegularField(outDims);
        outField.setAffine(outAffine);
        outField.setPhysExts(inField.getPhysExts());

        WorkerThread[] workers = new WorkerThread[nThreads];

        for (int component = 0; component < inField.getNData(); component++) {            
            if(!inField.getData(component).isSimpleNumeric() || inField.getData(component).getVeclen() != 1)
                continue;
            
            DataArray outDataArray = null;
            switch(inField.getData(component).getType()) {
                case DataArray.FIELD_DATA_BYTE:
                    byte[] outBData = new byte[outDims[0]*outDims[1]*outDims[2]];
                    for (int i = 0; i < workers.length; i++) {
                        workers[i] = new WorkerThread(i, nThreads, inField, outField, inField.getData(component).getBData(), outBData);
                        workers[i].start();
                    }
                    for (int i = 0; i < workers.length; i++) {
                        try {
                            workers[i].join();
                        } catch (InterruptedException ex) {
                        }
                    }
                    outDataArray = DataArray.create(outBData, 1, inField.getData(component).getName());
                    break;
                case DataArray.FIELD_DATA_SHORT:
                    short[] outSData = new short[outDims[0]*outDims[1]*outDims[2]];
                    for (int i = 0; i < workers.length; i++) {
                        workers[i] = new WorkerThread(i, nThreads, inField, outField, inField.getData(component).getSData(), outSData);
                        workers[i].start();
                    }
                    for (int i = 0; i < workers.length; i++) {
                        try {
                            workers[i].join();
                        } catch (InterruptedException ex) {
                        }
                    }
                    outField.addData(DataArray.create(outSData, 1, inField.getData(component).getName()));
                    break;
                case DataArray.FIELD_DATA_INT:
                    int[] outIData = new int[outDims[0]*outDims[1]*outDims[2]];
                    for (int i = 0; i < workers.length; i++) {
                        workers[i] = new WorkerThread(i, nThreads, inField, outField, inField.getData(component).getIData(), outIData);
                        workers[i].start();
                    }
                    for (int i = 0; i < workers.length; i++) {
                        try {
                            workers[i].join();
                        } catch (InterruptedException ex) {
                        }
                    }
                    outField.addData(DataArray.create(outIData, 1, inField.getData(0).getName()));
                    break;
                default:
                    float[] outFData = new float[outDims[0]*outDims[1]*outDims[2]];
                    for (int i = 0; i < workers.length; i++) {
                        workers[i] = new WorkerThread(i, nThreads, inField, outField, inField.getData(component).getFData(), outFData);
                        workers[i].start();
                    }
                    for (int i = 0; i < workers.length; i++) {
                        try {
                            workers[i].join();
                        } catch (InterruptedException ex) {
                        }
                    }
                    outField.addData(DataArray.create(outFData, 1, inField.getData(component).getName()));
                    break;
            }
            if(outDataArray != null) {
                outDataArray.setPhysMin(inField.getData(component).getPhysMin());
                outDataArray.setPhysMax(inField.getData(component).getPhysMax());
                outField.addData(outDataArray);
            }
            
        }
        return outField;
    }

    private class WorkerThread extends Thread {
        private RegularField inField = null;
        private RegularField outField = null;
        private byte[] inBData = null;
        private byte[] outBData = null;
        private short[] inSData = null;
        private short[] outSData = null;
        private int[] inIData = null;
        private int[] outIData = null;
        private float[] inFData = null;
        private float[] outFData = null;
        private int nThreads = 0;
        private int iThread = 0;

        public WorkerThread(int iThread, int nThreads, RegularField inField, RegularField outField, byte[] inData, byte[] outData) {
            this.iThread = iThread;
            this.nThreads = nThreads;
            this.inField = inField;
            this.outField = outField;
            this.inBData = inData;
            this.outBData = outData;
        }

        public WorkerThread(int iThread, int nThreads, RegularField inField, RegularField outField, short[] inData, short[] outData) {
            this.iThread = iThread;
            this.nThreads = nThreads;
            this.inField = inField;
            this.outField = outField;
            this.inSData = inData;
            this.outSData = outData;
        }

        public WorkerThread(int iThread, int nThreads, RegularField inField, RegularField outField, int[] inData, int[] outData) {
            this.iThread = iThread;
            this.nThreads = nThreads;
            this.inField = inField;
            this.outField = outField;
            this.inIData = inData;
            this.outIData = outData;
        }

        public WorkerThread(int iThread, int nThreads, RegularField inField, RegularField outField, float[] inData, float[] outData) {
            this.iThread = iThread;
            this.nThreads = nThreads;
            this.inField = inField;
            this.outField = outField;
            this.inFData = inData;
            this.outFData = outData;
        }

        @Override
        public void run() {
            if(inBData != null)
                runByte();
            else if(inSData != null)
                runShort();
            else if(inIData != null)
                runInt();
            else if(inFData != null)
                runFloat();
        }

        public void runByte() {
            int[] inDims = inField.getDims();
            int[] outDims = outField.getDims();
            float[] pIn, pOut;
            byte c;
            for (int k = iThread; k < outDims[2]; k+=nThreads) {
                for (int j = 0; j < outDims[1]; j++) {
                    for (int i = 0; i < outDims[0]; i++) {

                        pOut = outField.getGridCoords(i, j, k);
                        pIn = inField.getFloatIndices(pOut[0], pOut[1], pOut[2]);
                        if(pIn[0] < 0 || pIn[0] >= inDims[0] || pIn[1] < 0 || pIn[1] >= inDims[1] || pIn[2] < 0 || pIn[2] >= inDims[2])
                            c = 0;
                        else
                            c = RegularFieldInterpolator.getInterpolatedScalarData3D(inBData, inDims, pIn[0], pIn[1], pIn[2]);
                        outBData[k*outDims[0]*outDims[1] + j*outDims[0] + i] = c;
                    }
                }
                if(iThread == 0) Regularizator.this.fireStatusChanged((float)(k+1)/(float)(outDims[2]));
            }
        }

        public void runShort() {
            int[] inDims = inField.getDims();
            int[] outDims = outField.getDims();
            float[] pIn, pOut;
            short c;
            for (int k = iThread; k < outDims[2]; k+=nThreads) {
                for (int j = 0; j < outDims[1]; j++) {
                    for (int i = 0; i < outDims[0]; i++) {

                        pOut = outField.getGridCoords(i, j, k);
                        pIn = inField.getFloatIndices(pOut[0], pOut[1], pOut[2]);
                        if(pIn[0] < 0 || pIn[0] >= inDims[0] || pIn[1] < 0 || pIn[1] >= inDims[1] || pIn[2] < 0 || pIn[2] >= inDims[2])
                            c = 0;
                        else
                            c = RegularFieldInterpolator.getInterpolatedScalarData3D(inSData, inDims, pIn[0], pIn[1], pIn[2]);
                        outSData[k*outDims[0]*outDims[1] + j*outDims[0] + i] = c;
                    }
                }
                if(iThread == 0) Regularizator.this.fireStatusChanged((float)(k+1)/(float)(outDims[2]));
            }
        }

        public void runInt() {
            int[] inDims = inField.getDims();
            int[] outDims = outField.getDims();
            float[] pIn, pOut;
            int c;
            for (int k = iThread; k < outDims[2]; k+=nThreads) {
                for (int j = 0; j < outDims[1]; j++) {
                    for (int i = 0; i < outDims[0]; i++) {

                        pOut = outField.getGridCoords(i, j, k);
                        pIn = inField.getFloatIndices(pOut[0], pOut[1], pOut[2]);
                        if(pIn[0] < 0 || pIn[0] >= inDims[0] || pIn[1] < 0 || pIn[1] >= inDims[1] || pIn[2] < 0 || pIn[2] >= inDims[2])
                            c = 0;
                        else
                            c = RegularFieldInterpolator.getInterpolatedScalarData3D(inIData, inDims, pIn[0], pIn[1], pIn[2]);
                        outIData[k*outDims[0]*outDims[1] + j*outDims[0] + i] = c;
                    }
                }
                if(iThread == 0) Regularizator.this.fireStatusChanged((float)(k+1)/(float)(outDims[2]));
            }
        }

        public void runFloat() {
            int[] inDims = inField.getDims();
            int[] outDims = outField.getDims();
            float[] pIn, pOut;
            float c;
            for (int k = iThread; k < outDims[2]; k+=nThreads) {
                for (int j = 0; j < outDims[1]; j++) {
                    for (int i = 0; i < outDims[0]; i++) {

                        pOut = outField.getGridCoords(i, j, k);
                        pIn = inField.getFloatIndices(pOut[0], pOut[1], pOut[2]);
                        if(pIn[0] < 0 || pIn[0] >= inDims[0] || pIn[1] < 0 || pIn[1] >= inDims[1] || pIn[2] < 0 || pIn[2] >= inDims[2])
                            c = 0;
                        else
                            c = RegularFieldInterpolator.getInterpolatedScalarData3D(inFData, inDims, pIn[0], pIn[1], pIn[2]);
                        outFData[k*outDims[0]*outDims[1] + j*outDims[0] + i] = c;
                    }
                }
                if(iThread == 0) Regularizator.this.fireStatusChanged((float)(k+1)/(float)(outDims[2]));
            }
        }


    }

    private transient FloatValueModificationListener statusListener = null;

    public void addFloatValueModificationListener(FloatValueModificationListener listener) {
        if (statusListener == null) {
            this.statusListener = listener;
        } else {
            System.out.println("" + this + ": only one status listener can be added");
        }
    }

    private void fireStatusChanged(float status) {
        FloatValueModificationEvent e = new FloatValueModificationEvent(this, status, true);
        if (statusListener != null) {
            statusListener.floatValueChanged(e);
        }
    }

}
