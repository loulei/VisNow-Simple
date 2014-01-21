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

package pl.edu.icm.visnow.lib.basic.filters.MulticomponentHistogram;

import java.util.ArrayList;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.gui.events.FloatValueModificationEvent;
import pl.edu.icm.visnow.gui.events.FloatValueModificationListener;

/**
 * @author  Bartosz Borucki (babor@icm.edu.pl)
 * Warsaw University, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class Core {

    private Params params = new Params();
    private Field inField = null;
    private RegularField outField = null;

    public Core() {
    }

    public void setParams(Params params) {
        this.params = params;
    }

    public void update() {
        if (inField == null) {
            return;
        }        
        outField = null;
        
        HistogramOperation[] tmpOps = params.getHistogramOperations();
        HistogramOperation[] ops = null;
        int nOps = 0;
        if(tmpOps != null)
            nOps = tmpOps.length;

        nOps++;
        ops = new HistogramOperation[nOps];
        ops[0] = new HistogramOperation(params.isCountLogScale(), params.getLogConstant(), params.isCountDropBackground());
        for (int i = 0; i < nOps-1; i++) {
            ops[i+1] = tmpOps[i];                        
        }                                        

        int[] tmp = params.getDims();
        int nDims = params.getNDims();
        int[] histDims = new int[nDims];
        for (int i = 0; i < nDims; i++) {
            histDims[i] = tmp[i];            
        }
        
        
        //long t0 = System.currentTimeMillis();
        float[][] affine = null;
        float[][] ext = null;
        ArrayList<DataArray> outData = new ArrayList<DataArray>();
        boolean roundByteDimsTo32 = params.isRoundByteDimsTo32();
        
        switch(params.getBinning()) {
            case Params.BINNING_BY_COMPONENTS:
                DataArray[] data = new DataArray[nDims];
                int[] sel = params.getSelectedComponents();
                for (int i = 0; i < nDims; i++) {
                    data[i] = inField.getData(sel[i]);
                }
                
                for (int i = 0; i < ops.length; i++) {
                    float[] hist = null;

                    if(ops[i].getComponent() == null || ops[i].getComponent().getVeclen() == 1) {
                        hist = HistogramBuilder.buildDataHistogram(histDims, roundByteDimsTo32, data, ops[i], params.getFilterConditions(), params.getFilterConditionsLogic());                    
                        if(hist != null) {
                            outData.add(DataArray.create(hist, 1, ops[i].toString()));                
                        }
                    } else {
                        hist = HistogramBuilder.buildVectorDataHistogram(histDims, roundByteDimsTo32, data, ops[i], params.getFilterConditions(), params.getFilterConditionsLogic());                    
                        if(hist != null) {
                            outData.add(DataArray.create(hist, ops[i].getComponent().getVeclen(), ops[i].toString()));                
                        }
                    }
                    
                    fireStatusChanged((float)(i+1)/(float)ops.length);
                }                
                
                outField = new RegularField(histDims);
                for (int i = 0; i < outData.size(); i++) {
                    outField.addData(outData.get(i));                    
                }
                //calculate outField geometry
                affine = new float[4][3];
                ext = new float[2][3];
                for (int i = 0; i < 2; i++) {
                    for (int j = 0; j < 3; j++) {
                        ext[i][j] = 0.0f;
                    }                    
                }
                for (int i = 0; i < histDims.length; i++) {
                    if(roundByteDimsTo32 && data[i].getType() == DataArray.FIELD_DATA_BYTE) {
                        ext[0][i] = 0;
                        ext[1][i] = 255;                    
                    } else {
                        ext[0][i] = data[i].getMinv();
                        ext[1][i] = data[i].getMaxv();                    
                    }
                }                
                outField.setPhysExts(ext);
                
                String[] axesNames = new String[nDims];
                for (int i = 0; i < nDims; i++) {
                    axesNames[i] = data[i].getName();                    
                }
                outField.setAxesNames(axesNames);
                
                if(params.isOutGeometryToData()) {
                    for (int i = 0; i < 3; i++) {
                        affine[0][i] = 0.0f;
                        affine[1][i] = 0.0f;
                        affine[2][i] = 0.0f;
                        affine[3][i] = ext[0][i];                    
                    }
                    for (int i = 0; i < histDims.length; i++) {
                        affine[i][i] = (ext[1][i] - ext[0][i])/(histDims[i]-1);
                    }
                    outField.setAffine(affine);
                }
                
                
                break;
            case Params.BINNING_BY_COORDINATES:

                int[] histCoords = params.getSelectedCoords();
                
                for (int i = 0; i < ops.length; i++) {
                    float[] hist = null;

                    if(ops[i].getComponent() == null || ops[i].getComponent().getVeclen() == 1) {
                        hist = HistogramBuilder.buildCoordsHistogram(histDims, histCoords, inField, ops[i], params.getFilterConditions(), params.getFilterConditionsLogic());                    
                        if(hist != null) {
                            outData.add(DataArray.create(hist, 1, ops[i].toString()));                
                        }
                    } else {
                        hist = HistogramBuilder.buildVectorCoordsHistogram(histDims, histCoords, inField, ops[i], params.getFilterConditions(), params.getFilterConditionsLogic());                    
                        if(hist != null) {
                            if(ops[i].getOperation() == HistogramOperation.Operation.VSTD) {
                                int veclen = ops[i].getComponent().getVeclen();
                                int nData = hist.length/(veclen*veclen);
                                for (int v = 0; v < veclen; v++) {
                                    float[] tmpHist = new float[nData*veclen];
                                    for (int m = 0; m < nData; m++) {
                                        for (int l = 0; l < veclen; l++) {
                                            //tmpHist[m*veclen + l] = hist[m*veclen*veclen + v*veclen + l];
                                            tmpHist[m*veclen + l] = hist[m*veclen*veclen + l*veclen + v];
                                        }                                        
                                    }                                    
                                    outData.add(DataArray.create(tmpHist, veclen, ops[i].toString()+"_"+v));
                                }                                
                            } else {
                                outData.add(DataArray.create(hist, ops[i].getComponent().getVeclen(), ops[i].toString()));                
                            }
                        }
                    }
                    
                    fireStatusChanged((float)(i+1)/(float)ops.length);
                }
                
                outField = new RegularField(histDims);                
                for (int i = 0; i < outData.size(); i++) {
                    outField.addData(outData.get(i));                    
                }
                
                //calculate outField geometry
                affine = new float[4][3];
                ext = inField.getExtents();
                for (int i = 0; i < 3; i++) {
                    affine[0][i] = 0.0f;
                    affine[1][i] = 0.0f;
                    affine[2][i] = 0.0f;
                    affine[3][i] = ext[0][i];                    
                }
                for (int i = 0; i < histDims.length; i++) {
                    affine[i][i] = (ext[1][histCoords[i]] - ext[0][histCoords[i]])/(histDims[i]-1);
                }
                outField.setAffine(affine);
                outField.setPhysExts(outField.getExtents());
                break;
        }


        if(outField.getNData() == 0)
            outField = null;

        fireStatusChanged(1.0f);
        
        //long t1 = System.currentTimeMillis();                
        //float dt = (t1 - t0)/1000;
        //System.out.println("calculation time: "+dt+" s");
        
    }

    public void setInField(Field inField) {
        this.inField = inField;
    }

    public RegularField getOutField() {
        return outField;
    }


//    private abstract class HistogramOperationThread extends Thread{
//        protected float[] hist = null;
//        
//        public float[] getHistogram() {
//            return hist;
//        }
//    }
//    
//    private class DataScalarHistogramOperationThread extends HistogramOperationThread {
//        private int[] histDims;
//        private DataArray[] data; 
//        private HistogramOperation op;
//        private Condition[] filterConditions; 
//        private Condition.Logic[] filterConditionsLogic;       
//        
//        public DataScalarHistogramOperationThread(int[] histDims, DataArray[] data, HistogramOperation op, Condition[] filterConditions, Condition.Logic[] filterConditionsLogic) {
//            this.histDims = histDims;
//            this.data = data;
//            this.op = op;    
//            this.filterConditions = filterConditions;
//            this.filterConditionsLogic = filterConditionsLogic;
//        }
//        
//        @Override
//        public void run() {
//            hist = HistogramBuilder.buildDataHistogram(histDims, data, op, filterConditions, filterConditionsLogic);                                
//        }
//    }
//
//    private class DataVectorHistogramOperationThread extends HistogramOperationThread {
//        private int[] histDims;
//        private DataArray[] data; 
//        private HistogramOperation op;
//        private Condition[] filterConditions; 
//        private Condition.Logic[] filterConditionsLogic;       
//        
//        public DataVectorHistogramOperationThread(int[] histDims, DataArray[] data, HistogramOperation op, Condition[] filterConditions, Condition.Logic[] filterConditionsLogic) {
//            this.histDims = histDims;
//            this.data = data;
//            this.op = op;    
//            this.filterConditions = filterConditions;
//            this.filterConditionsLogic = filterConditionsLogic;
//        }
//        
//        @Override
//        public void run() {
//            hist = HistogramBuilder.buildVectorDataHistogram(histDims, data, op, filterConditions, filterConditionsLogic);                        
//        }
//    }
//
//    private class CoordsScalarHistogramOperationThread extends HistogramOperationThread {
//        private int[] histDims;        
//        private int[] histCoords;        
//        private Field field;
//        private HistogramOperation op;
//        private Condition[] filterConditions; 
//        private Condition.Logic[] filterConditionsLogic;       
//        
//        public CoordsScalarHistogramOperationThread(int[] histDims, int[] histCoords, Field field, HistogramOperation op, Condition[] filterConditions, Condition.Logic[] filterConditionsLogic) {
//            this.histDims = histDims;
//            this.histCoords = histCoords;
//            this.field = field;
//            this.op = op;    
//            this.filterConditions = filterConditions;
//            this.filterConditionsLogic = filterConditionsLogic;
//        }
//        
//        @Override
//        public void run() {
//            hist = HistogramBuilder.buildCoordsHistogram(histDims, histCoords, field, op, filterConditions, filterConditionsLogic);                    
//        }
//    }
//    
//    private class CoordsVectorHistogramOperationThread extends HistogramOperationThread {
//        private int[] histDims;        
//        private int[] histCoords;        
//        private Field field;
//        private HistogramOperation op;
//        private Condition[] filterConditions; 
//        private Condition.Logic[] filterConditionsLogic;       
//        
//        public CoordsVectorHistogramOperationThread(int[] histDims, int[] histCoords, Field field, HistogramOperation op, Condition[] filterConditions, Condition.Logic[] filterConditionsLogic) {
//            this.histDims = histDims;
//            this.histCoords = histCoords;
//            this.field = field;
//            this.op = op;    
//            this.filterConditions = filterConditions;
//            this.filterConditionsLogic = filterConditionsLogic;
//        }
//        
//        @Override
//        public void run() {
//            hist = HistogramBuilder.buildVectorCoordsHistogram(histDims, histCoords, field, op, filterConditions, filterConditionsLogic);                    
//        }
//    }
//    

   private transient FloatValueModificationListener statusListener = null;
   
   public void addFloatValueModificationListener(FloatValueModificationListener listener)
   {
      if (statusListener == null)
         this.statusListener = listener;
      else
         System.out.println(""+this+": only one status listener can be added");
   }
  
   private void fireStatusChanged(float status)
   {
       FloatValueModificationEvent e = new FloatValueModificationEvent(this, status, true);
       if (statusListener != null)
          statusListener.floatValueChanged(e);
   }

}
