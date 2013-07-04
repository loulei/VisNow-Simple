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

import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.lib.utils.numeric.NumericalMethods;

/**
 * @author  Bartosz Borucki (babor@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class HistogramBuilder {
    
    public static float[] buildDataHistogram(int[] histDims, DataArray[] data, HistogramOperation operation, Condition[] conditions, Condition.Logic[] conditionsLogic) {
        if (histDims == null || data == null || histDims.length != data.length || operation == null) {
            return null;
        }

        if (conditions != null && conditions.length > 1 && (conditionsLogic == null || conditionsLogic.length != conditions.length - 1)) {
            System.out.println("ERROR: conditions logic not compatible with conditions");
            return null;
        }

        int nComps = data.length;
        int nInNodes = data[0].getNData();
        for (int i = 1; i < nComps; i++) {
            if (data[i].getNData() != nInNodes) {
                System.err.println("ERROR creating histogram: bad data sizes!");
                return null;
            }
        }

        float[] minima = new float[nComps];
        float[] scales = new float[nComps];
        int nData = 1;

        for (int i = 0; i < nComps; i++) {
            if (data[i].getType() == DataArray.FIELD_DATA_BYTE) {
                minima[i] = 0;
                if (histDims[i] > 128) {
                    histDims[i] = 256;
                    scales[i] = 1;
                } else if (histDims[i] > 64) {
                    histDims[i] = 128;
                    scales[i] = .5f;
                } else if (histDims[i] > 32) {
                    histDims[i] = 64;
                    scales[i] = .25f;
                } else {
                    histDims[i] = 32;
                    scales[i] = .125f;
                }
            } else {
                minima[i] = data[i].getMinv();
                scales[i] = histDims[i] / (data[i].getMaxv() - minima[i]);
            }
        }
        for (int i = 0; i < histDims.length; i++) {
            nData *= histDims[i];
        }
        
        float[] hist = null, count = null, sum = null;
        switch(operation.getOperation()) {
            case COUNT:
            case SUM:
                hist = new float[nData];
                for (int i = 0; i < nData; i++) {
                    hist[i] = 0;                
                }            
                break;
            case MIN:
                hist = new float[nData];
                for (int i = 0; i < nData; i++) {
                    hist[i] = Float.POSITIVE_INFINITY;                
                }            
                break;
            case MAX:
                hist = new float[nData];
                for (int i = 0; i < nData; i++) {
                    hist[i] = Float.NEGATIVE_INFINITY;                
                }            
                break;
            case AVG:
                hist = new float[nData];
                count = new float[nData];
                for (int i = 0; i < nData; i++) {
                    hist[i] = 0;
                    count[i] = 0;
                }            
                break;
            case STD:
                hist = new float[nData];
                count = new float[nData];
                sum = new float[nData];
                for (int i = 0; i < nData; i++) {
                    hist[i] = 0;
                    count[i] = 0;
                    sum[i] = 0;
                }            
                break;
            default:
                return null;
        }
        
        
        int nCond = 0;
        boolean[] conditionResults = null;
        
        if (conditions != null) {
            nCond = conditions.length;
            conditionResults = new boolean[nCond];
        }
        float v;

        for (int i = 0; i < nInNodes; i++) {
            if(conditions != null) {
                for (int c = 0; c < nCond; c++) {
                    conditionResults[c] = conditions[c].check(i);
                }
                boolean cond = conditionResults[0];
                for (int c = 1; c < nCond; c++) {
                    if(conditionsLogic[c-1] == Condition.Logic.AND) {
                        cond = cond && conditionResults[c];
                    } else if(conditionsLogic[c-1] == Condition.Logic.OR) {
                        cond = cond || conditionResults[c];
                    }
                }
                if (!cond) {
                    continue;
                }
            }

            int m = 0;
            for (int j = nComps - 1; j >= 0; j--) {
                int k = (int) (scales[j] * (data[j].getData(i) - minima[j]));
                if (k < 0) {
                    k = 0;
                }
                if (k >= histDims[j]) {
                    k = histDims[j] - 1;
                }
                m = histDims[j] * m + k;
            }
            
            switch(operation.getOperation()) {
                case COUNT:
                    hist[m] += 1;
                    break;
                case SUM:
                    hist[m] += operation.getComponentValue(i);
                    break;
                case MIN:
                    v = operation.getComponentValue(i);
                    if(v < hist[m])
                        hist[m] = v;
                    break;
                case MAX:
                    v = operation.getComponentValue(i);
                    if(v > hist[m])
                        hist[m] = v;
                    break;
                case AVG:
                    hist[m] += operation.getComponentValue(i);
                    count[m] += 1;
                    break;
                case STD:
                    v = operation.getComponentValue(i);
                    sum[m] += v;
                    hist[m] += v*v;
                    count[m] += 1;
                    break;
            }
        }

        
        switch(operation.getOperation()) {
            case MIN:
                for (int i = 0; i < nData; i++) {
                    if(hist[i] == Float.POSITIVE_INFINITY)
                        hist[i] = 0;
                }            
                break;
            case MAX:
                for (int i = 0; i < nData; i++) {
                    if(hist[i] == Float.NEGATIVE_INFINITY)
                        hist[i] = 0;
                }            
                break;
            case AVG:
                for (int i = 0; i < nData; i++) {
                    if(count[i] > 0)
                        hist[i] = hist[i]/count[i];
                }            
                break;
            case STD:
                for (int i = 0; i < nData; i++) {
                    if(count[i] > 0) {
                        v = sum[i]/count[i];
                        hist[i] = (float) Math.sqrt(hist[i]/count[i] - v*v);
                    }
                }            
                break;
            default:
                break;
        }
        
        
        if (operation.isDropBackgound()) {
            hist[0] = 0;
        }
        if (operation.isLog()) {
            float histMin = Float.POSITIVE_INFINITY;
            float logConst = operation.getLogConst();
            for (int i = 0; i < hist.length; i++) {
                if(hist[i] < histMin) histMin = hist[i];
            }
            
            for (int i = 0; i < hist.length; i++) {
                hist[i] = (float) Math.log(hist[i] - histMin + logConst);
            }
        }
        return hist;
    }

    public static float[] buildVectorDataHistogram(int[] histDims, DataArray[] data, HistogramOperation operation, Condition[] conditions, Condition.Logic[] conditionsLogic) {
        if (histDims == null || data == null || histDims.length != data.length || operation == null || operation.getComponent() == null) {
            return null;
        }

        if (conditions != null && conditions.length > 1 && (conditionsLogic == null || conditionsLogic.length != conditions.length - 1)) {
            System.out.println("ERROR: conditions logic not compatible with cconditions - falling back to unconditioned histogram build");
            return null;
        }

        int nComps = data.length;
        int nInNodes = data[0].getNData();
        for (int i = 1; i < nComps; i++) {
            if (data[i].getNData() != nInNodes) {
                System.err.println("ERROR creating histogram: bad data sizes!");
                return null;
            }
        }

        float[] minima = new float[nComps];
        float[] scales = new float[nComps];
        int nData = 1;

        for (int i = 0; i < nComps; i++) {
            if (data[i].getType() == DataArray.FIELD_DATA_BYTE) {
                minima[i] = 0;
                if (histDims[i] > 128) {
                    histDims[i] = 256;
                    scales[i] = 1;
                } else if (histDims[i] > 64) {
                    histDims[i] = 128;
                    scales[i] = .5f;
                } else if (histDims[i] > 32) {
                    histDims[i] = 64;
                    scales[i] = .25f;
                } else {
                    histDims[i] = 32;
                    scales[i] = .125f;
                }
            } else {
                minima[i] = data[i].getMinv();
                scales[i] = histDims[i] / (data[i].getMaxv() - minima[i]);
            }
        }
        for (int i = 0; i < histDims.length; i++) {
            nData *= histDims[i];
        }
        int veclen = operation.getComponent().getVeclen();
        
        float[] hist = null, count = null, sum = null;
        switch(operation.getOperation()) {
            case SUM:
                hist = new float[nData*veclen];
                for (int i = 0; i < hist.length; i++) {
                    hist[i] = 0;                
                }            
                break;
            case MIN:
                hist = new float[nData*veclen];
                for (int i = 0; i < hist.length; i++) {
                    hist[i] = Float.POSITIVE_INFINITY;                
                }            
                break;
            case MAX:
                hist = new float[nData*veclen];
                for (int i = 0; i < hist.length; i++) {
                    hist[i] = Float.NEGATIVE_INFINITY;                
                }            
                break;
            case AVG:
                hist = new float[nData*veclen];
                for (int i = 0; i < hist.length; i++) {
                    hist[i] = 0;
                }            
                count = new float[nData];
                for (int i = 0; i < count.length; i++) {
                    count[i] = 0;
                }            
                break;
            case STD:
                hist = new float[nData*veclen];
                sum = new float[nData*veclen];
                for (int i = 0; i < hist.length; i++) {
                    hist[i] = 0;
                    sum[i] = 0;
                }            
                count = new float[nData];                
                for (int i = 0; i < count.length; i++) {
                    count[i] = 0;
                }            
                break;
            case VSTD:
                hist = new float[nData*veclen*veclen];
                for (int i = 0; i < hist.length; i++) {
                    hist[i] = 0;
                }                            
                sum = new float[nData*veclen];
                for (int i = 0; i < sum.length; i++) {
                    sum[i] = 0;
                }                            
                count = new float[nData];
                for (int i = 0; i < count.length; i++) {
                    count[i] = 0;
                }            
                
                break;
                
            default:
                return null;
        }
        
        int nCond = 0;
        boolean[] conditionResults = null;
        if(conditions != null) {        
            nCond = conditions.length;
            conditionResults = new boolean[nCond];
        }
        float[] tmp;
        float v;
        int vv = veclen*veclen;

        for (int i = 0; i < nInNodes; i++) {
            if(conditions != null) {        
                for (int c = 0; c < nCond; c++) {
                    conditionResults[c] = conditions[c].check(i);
                }
                boolean cond = conditionResults[0];
                for (int c = 1; c < nCond; c++) {
                    if(conditionsLogic[c-1] == Condition.Logic.AND) {
                        cond = cond && conditionResults[c];
                    } else if(conditionsLogic[c-1] == Condition.Logic.OR) {
                        cond = cond || conditionResults[c];
                    }
                }
                if (!cond) {
                    continue;
                }
            }

            int m = 0;
            for (int j = nComps - 1; j >= 0; j--) {
                int k = (int) (scales[j] * (data[j].getData(i) - minima[j]));
                if (k < 0) {
                    k = 0;
                }
                if (k >= histDims[j]) {
                    k = histDims[j] - 1;
                }
                m = histDims[j] * m + k;
            }
            
            switch(operation.getOperation()) {
                case SUM:
                    tmp = operation.getVectorComponentValue(m);
                    for (int j = 0; j < veclen; j++) {
                        hist[m*veclen + j] += tmp[j];
                    }
                    break;
                case MIN:
                    tmp = operation.getVectorComponentValue(m);
                    for (int j = 0; j < veclen; j++) {
                        if(tmp[j] < hist[m*veclen + j])
                            hist[m*veclen + j] = tmp[j];
                    }
                    break;
                case MAX:
                    tmp = operation.getVectorComponentValue(m);
                    for (int j = 0; j < veclen; j++) {
                        if(tmp[j] > hist[m*veclen + j])
                            hist[m*veclen + j] = tmp[j];
                    }
                    break;
                case AVG:
                    tmp = operation.getVectorComponentValue(m);
                    for (int j = 0; j < veclen; j++) {
                        hist[m*veclen + j] += tmp[j];
                    }                    
                    count[m] += 1;
                    break;
                case STD:
                    tmp = operation.getVectorComponentValue(m);
                    for (int j = 0; j < veclen; j++) {
                        sum[m*veclen + j] += tmp[j];
                        hist[m*veclen + j] += tmp[j]*tmp[j];
                    }
                    count[m] += 1;
                    break;
                case VSTD:
                    tmp = operation.getVectorComponentValue(i);                                        
                    for (int u = 0; u < veclen; u++) {
                        sum[veclen*m + u] += tmp[u];
                    }
                    count[m] += 1;
                    break;
            }
        }

        if(operation.getOperation() == HistogramOperation.Operation.VSTD) {
            for (int i = 0; i < nInNodes; i++) {
                if(conditions != null) {        
                    for (int c = 0; c < nCond; c++) {
                        conditionResults[c] = conditions[c].check(i);
                    }
                    boolean cond = conditionResults[0];
                    for (int c = 1; c < nCond; c++) {
                        if(conditionsLogic[c-1] == Condition.Logic.AND) {
                            cond = cond && conditionResults[c];
                        } else if(conditionsLogic[c-1] == Condition.Logic.OR) {
                            cond = cond || conditionResults[c];
                        }
                    }
                    if (!cond) {
                        continue;
                    }
                }

                int m = 0;
                for (int j = nComps - 1; j >= 0; j--) {
                    int k = (int) (scales[j] * (data[j].getData(i) - minima[j]));
                    if (k < 0) {
                        k = 0;
                    }
                    if (k >= histDims[j]) {
                        k = histDims[j] - 1;
                    }
                    m = histDims[j] * m + k;
                }

                tmp = operation.getVectorComponentValue(i);                                        
                for (int u = 0; u < veclen; u++) {
                    v = sum[veclen*m + u]/count[m];
                    for (int w = 0; w < veclen; w++) {                                                    
                        hist[vv*m + w*veclen + u] += ((tmp[u]-v)*(tmp[w]-v));
                    }                                            
                }
            }
        }
        
        switch(operation.getOperation()) {
            case MIN:
                for (int i = 0; i < hist.length; i++) {
                    if(hist[i] == Float.POSITIVE_INFINITY)
                        hist[i] = 0;
                }            
                break;
            case MAX:
                for (int i = 0; i < hist.length; i++) {
                    if(hist[i] == Float.NEGATIVE_INFINITY)
                        hist[i] = 0;
                }            
                break;
            case AVG:
                for (int i = 0; i < nData; i++) {
                    if(count[i] > 0) {
                        for (int j = 0; j < veclen; j++) {
                            hist[i*veclen + j] = hist[i*veclen + j]/count[i];
                        }
                    }                        
                }            
                break;
            case STD:
                for (int i = 0; i < nData; i++) {
                    if(count[i] > 0) {
                        for (int j = 0; j < veclen; j++) {
                            v = sum[i*veclen + j]/count[i];
                            hist[i*veclen + j] = (float) Math.sqrt(hist[i*veclen + j]/count[i] - v*v);
                        }
                    }
                }            
                break;
            case VSTD:
                double[][] A = new double[veclen][veclen];
                for (int i = 0; i < nData; i++) {
                    if(count[i] > 0) {
                        for (int u = 0; u < veclen; u++) {
                            for (int w = 0; w < veclen; w++) {                                                
                                A[u][w] = hist[vv*i + w*veclen + u]/count[i];
                            }                                            
                        }
                        
                        double[] eigenValues = new double[veclen];
                        double[][] eigenVectors = new double[veclen][veclen];
                        int res = NumericalMethods.jacobiEigenproblemSolver(A, eigenValues, eigenVectors, 100000);                        
                        if(res != -1) {                            
                            for (int u = 0; u < veclen; u++) {
                                for (int w = 0; w < veclen; w++) {                                                
                                    hist[vv*i + w*veclen + u] = (float)(eigenVectors[u][w] * Math.sqrt(eigenValues[u]));
                                }                                            
                            }
                        } else {
                            for (int u = 0; u < veclen; u++) {
                                for (int w = 0; w < veclen; w++) {                                                
                                    hist[vv*i + w*veclen + u] = 0;
                                }                                            
                            }
                        }
                    }
                }            
                break;
            default:
                break;
        }
        
        
        if (operation.isDropBackgound()) {
            for (int i = 0; i < veclen; i++) {
                hist[i] = 0;                
            }
        }
        if (operation.isLog()) {
            float histMin = Float.POSITIVE_INFINITY;
            float logConst = operation.getLogConst();
            for (int i = 0; i < hist.length; i++) {
                if(hist[i] < histMin) histMin = hist[i];
            }
            
            for (int i = 0; i < hist.length; i++) {
                hist[i] = (float) Math.log(hist[i] - histMin + logConst);
            }
        }
        return hist;
    }
    
    public static float[] buildCoordsHistogram(int[] histDims,  int[] histCoords, Field field, HistogramOperation operation, Condition[] conditions, Condition.Logic[] conditionsLogic) {
        if(histDims == null || histCoords == null || field == null || operation == null || histDims.length != histCoords.length)
            return null;
        
        if (conditions != null && conditions.length > 1 && (conditionsLogic == null || conditionsLogic.length != conditions.length - 1)) {
            System.out.println("ERROR: conditions logic not compatible with conditions");
            return null;
        }
        
        int nOutDims = histDims.length;        
        int nSpace = field.getNSpace();
        for (int i = 0; i < histCoords.length; i++) {
            if(histCoords[i] > nSpace)
                return null;                    
        }        
        
        int nData = 1;
        for (int i = 0; i < histDims.length; i++) {
            nData *= histDims[i];
        }
        
        float[] hist = null, count = null, sum = null;
        switch(operation.getOperation()) {
            case COUNT:
            case SUM:
                hist = new float[nData];
                for (int i = 0; i < nData; i++) {
                    hist[i] = 0;                
                }            
                break;
            case MIN:
                hist = new float[nData];
                for (int i = 0; i < nData; i++) {
                    hist[i] = Float.POSITIVE_INFINITY;                
                }            
                break;
            case MAX:
                hist = new float[nData];
                for (int i = 0; i < nData; i++) {
                    hist[i] = Float.NEGATIVE_INFINITY;                
                }            
                break;
            case AVG:
                hist = new float[nData];
                count = new float[nData];
                for (int i = 0; i < nData; i++) {
                    hist[i] = 0;
                    count[i] = 0;
                }            
                break;
            case STD:
                hist = new float[nData];
                count = new float[nData];
                sum = new float[nData];
                for (int i = 0; i < nData; i++) {
                    hist[i] = 0;
                    count[i] = 0;
                    sum[i] = 0;
                }            
                break;
            default:
                return null;
        }
        
        int nNodes = field.getNNodes();        
        float[] coords = field.getCoords();
        float[][] extents = field.getExtents();
        float[] scales = new float[nOutDims];
        float v;
        for (int i = 0; i < nOutDims; i++) {
            scales[i] = histDims[i] / (extents[1][histCoords[i]] - extents[0][histCoords[i]]);
        }
        
        
        int nCond = 0;
        boolean[] conditionResults = null;
        
        if (conditions != null) {
            nCond = conditions.length;
            conditionResults = new boolean[nCond];
        }
        
        
        if(coords == null && field instanceof RegularField) {
            float[][] affine = ((RegularField)field).getAffine();
            int[] dims = ((RegularField)field).getDims();
            float[] p = new float[3];
            
            switch(dims.length) {
                case 3:
                    for (int k = 0; k < dims[2]; k++) {
                        for (int j = 0; j < dims[1]; j++) {
                            for (int i = 0; i < dims[0]; i++) {
                                for (int l = 0; l < 3; l++) {
                                    p[l] = affine[3][l] + k*affine[2][l] + j*affine[1][l] + i*affine[0][l];
                                }
                                
                                if(conditions != null) {
                                    for (int c = 0; c < nCond; c++) {
                                        conditionResults[c] = conditions[c].check(k*dims[0]*dims[1] + j*dims[0] + i);
                                    }
                                    boolean cond = conditionResults[0];
                                    for (int c = 1; c < nCond; c++) {
                                        if(conditionsLogic[c-1] == Condition.Logic.AND) {
                                            cond = cond && conditionResults[c];
                                        } else if(conditionsLogic[c-1] == Condition.Logic.OR) {
                                            cond = cond || conditionResults[c];
                                        }
                                    }
                                    if (!cond) {
                                        continue;
                                    }
                                }

                                int m = 0;
                                for (int l = nOutDims - 1; l >= 0; l--) {
                                    int mm = (int) (scales[l] * (p[histCoords[l]] - extents[0][histCoords[l]]));
                                    if (mm < 0) {
                                        mm = 0;
                                    }
                                    if (mm >= histDims[l]) {
                                        mm = histDims[l] - 1;
                                    }
                                    m = histDims[l] * m + mm;
                                }
                                
                                switch(operation.getOperation()) {
                                    case COUNT:
                                        hist[m]++;
                                        break;
                                    case SUM:
                                        hist[m] += operation.getComponentValue(k*dims[1]*dims[0] + j*dims[0] + i);
                                        break;
                                    case MIN:
                                        v = operation.getComponentValue(k*dims[1]*dims[0] + j*dims[0] + i);
                                        if(v < hist[m])
                                            hist[m] = v;
                                        break;
                                    case MAX:
                                        v = operation.getComponentValue(k*dims[1]*dims[0] + j*dims[0] + i);
                                        if(v > hist[m])
                                            hist[m] = v;
                                        break;
                                    case AVG:
                                        hist[m] += operation.getComponentValue(k*dims[1]*dims[0] + j*dims[0] + i);
                                        count[m] += 1;
                                        break;
                                    case STD:
                                        v = operation.getComponentValue(k*dims[1]*dims[0] + j*dims[0] + i);
                                        sum[m] += v;
                                        hist[m] += v*v;
                                        count[m] += 1;
                                        break;
                                }
                            }                            
                        }                        
                    }
                    break;
                case 2:
                    for (int j = 0; j < dims[1]; j++) {
                        for (int i = 0; i < dims[0]; i++) {
                            for (int l = 0; l < 3; l++) {
                                p[l] = affine[3][l] + j*affine[1][l] + i*affine[0][l];
                            }

                            if(conditions != null) {
                                for (int c = 0; c < nCond; c++) {
                                    conditionResults[c] = conditions[c].check(j*dims[0] + i);
                                }
                                boolean cond = conditionResults[0];
                                for (int c = 1; c < nCond; c++) {
                                    if(conditionsLogic[c-1] == Condition.Logic.AND) {
                                        cond = cond && conditionResults[c];
                                    } else if(conditionsLogic[c-1] == Condition.Logic.OR) {
                                        cond = cond || conditionResults[c];
                                    }
                                }
                                if (!cond) {
                                    continue;
                                }
                            }
                            
                            int m = 0;
                            for (int l = nOutDims - 1; l >= 0; l--) {
                                int mm = (int) (scales[l] * (p[histCoords[l]] - extents[0][histCoords[l]]));
                                if (mm < 0) {
                                    mm = 0;
                                }
                                if (mm >= histDims[l]) {
                                    mm = histDims[l] - 1;
                                }
                                m = histDims[l] * m + mm;
                            }

                            switch(operation.getOperation()) {
                                case COUNT:
                                    hist[m]++;
                                    break;
                                case SUM:
                                    hist[m] += operation.getComponentValue(j*dims[0] + i);
                                    break;
                                case MIN:
                                    v = operation.getComponentValue(j*dims[0] + i);
                                    if(v < hist[m])
                                        hist[m] = v;
                                    break;
                                case MAX:
                                    v = operation.getComponentValue(j*dims[0] + i);
                                    if(v > hist[m])
                                        hist[m] = v;
                                    break;
                                case AVG:
                                    hist[m] += operation.getComponentValue(j*dims[0] + i);
                                    count[m] += 1;
                                    break;
                                case STD:
                                    v = operation.getComponentValue(j*dims[0] + i);
                                    sum[m] += v;
                                    hist[m] += v*v;
                                    count[m] += 1;
                                    break;
                            }
                        }                            
                    }  
                    break;
                case 1:
                    for (int i = 0; i < dims[0]; i++) {
                        for (int l = 0; l < 3; l++) {
                            p[l] = affine[3][l] + i*affine[0][l];
                        }

                        if(conditions != null) {
                            for (int c = 0; c < nCond; c++) {
                                conditionResults[c] = conditions[c].check(i);
                            }
                            boolean cond = conditionResults[0];
                            for (int c = 1; c < nCond; c++) {
                                if(conditionsLogic[c-1] == Condition.Logic.AND) {
                                    cond = cond && conditionResults[c];
                                } else if(conditionsLogic[c-1] == Condition.Logic.OR) {
                                    cond = cond || conditionResults[c];
                                }
                            }
                            if (!cond) {
                                continue;
                            }
                        }
                        
                        int m = 0;
                        for (int l = nOutDims - 1; l >= 0; l--) {
                            int mm = (int) (scales[l] * (p[histCoords[l]] - extents[0][histCoords[l]]));
                            if (mm < 0) {
                                mm = 0;
                            }
                            if (mm >= histDims[l]) {
                                mm = histDims[l] - 1;
                            }
                            m = histDims[l] * m + mm;
                        }

                        switch(operation.getOperation()) {
                            case COUNT:
                                hist[m]++;
                                break;
                            case SUM:
                                hist[m] += operation.getComponentValue(i);
                                break;
                            case MIN:
                                v = operation.getComponentValue(i);
                                if(v < hist[m])
                                    hist[m] = v;
                                break;
                            case MAX:
                                v = operation.getComponentValue(i);
                                if(v > hist[m])
                                    hist[m] = v;
                                break;
                            case AVG:
                                hist[m] += operation.getComponentValue(i);
                                count[m] += 1;
                                break;
                            case STD:
                                v = operation.getComponentValue(i);
                                sum[m] += v;
                                hist[m] += v*v;
                                count[m] += 1;
                                break;
                        }
                    } 
                    break;
            }
        } else if(coords != null) {
            for (int i = 0, c = 0; i < nNodes; i++,c++) {

                if(conditions != null) {
                    for (int cc = 0; cc < nCond; cc++) {
                        conditionResults[cc] = conditions[cc].check(i);
                    }
                    boolean cond = conditionResults[0];
                    for (int cc = 1; cc < nCond; cc++) {
                        if(conditionsLogic[cc-1] == Condition.Logic.AND) {
                            cond = cond && conditionResults[cc];
                        } else if(conditionsLogic[cc-1] == Condition.Logic.OR) {
                            cond = cond || conditionResults[cc];
                        }
                    }
                    if (!cond) {
                        continue;
                    }
                }
                
                int m = 0;
                for (int j = nOutDims-1; j >= 0; j--) {
                    int k = (int) (scales[j] * (coords[nSpace*i + histCoords[j]] - extents[0][histCoords[j]]));
                    if (k < 0) {
                        k = 0;
                    }
                    if (k >= histDims[j]) {
                        k = histDims[j] - 1;
                    }
                    m = histDims[j] * m + k;
                }
                
                switch(operation.getOperation()) {
                    case COUNT:
                        hist[m] += 1;
                        break;
                    case SUM:
                        hist[m] += operation.getComponentValue(i);
                        break;
                    case MIN:
                        v = operation.getComponentValue(i);
                        if(v < hist[m])
                            hist[m] = v;
                        break;
                    case MAX:
                        v = operation.getComponentValue(i);
                        if(v > hist[m])
                            hist[m] = v;
                        break;
                    case AVG:
                        hist[m] += operation.getComponentValue(i);
                        count[m] += 1;
                        break;
                    case STD:
                        v = operation.getComponentValue(i);
                        sum[m] += v;
                        hist[m] += v*v;
                        count[m] += 1;
                        break;
                }
                
            }
        } else {
            return null;
        }

        switch(operation.getOperation()) {
            case MIN:
                for (int i = 0; i < nData; i++) {
                    if(hist[i] == Float.POSITIVE_INFINITY)
                        hist[i] = 0;
                }            
                break;
            case MAX:
                for (int i = 0; i < nData; i++) {
                    if(hist[i] == Float.NEGATIVE_INFINITY)
                        hist[i] = 0;
                }            
                break;
            case AVG:
                for (int i = 0; i < nData; i++) {
                    if(count[i] > 0)
                        hist[i] = hist[i]/count[i];
                }            
                break;
            case STD:
                for (int i = 0; i < nData; i++) {
                    if(count[i] > 0) {
                        v = sum[i]/count[i];
                        hist[i] = (float) Math.sqrt(hist[i]/count[i] - v*v);
                    }
                }            
                break;
            default:
                break;
        }
        
        if (operation.isDropBackgound()) {
            hist[0] = 0;
        }
        if (operation.isLog()) {
            float histMin = Float.POSITIVE_INFINITY;
            float logConst = operation.getLogConst();
            for (int i = 0; i < hist.length; i++) {
                if(hist[i] < histMin) histMin = hist[i];
            }
            
            for (int i = 0; i < hist.length; i++) {
                hist[i] = (float) Math.log(hist[i] - histMin + logConst);
            }
        }
        return hist;
    }

    public static float[] buildVectorCoordsHistogram(int[] histDims, int[] histCoords, Field field, HistogramOperation operation, Condition[] conditions, Condition.Logic[] conditionsLogic) {
        if(histDims == null || histCoords == null || field == null || operation == null || histDims.length != histCoords.length)
            return null;
        
        if (conditions != null && conditions.length > 1 && (conditionsLogic == null || conditionsLogic.length != conditions.length - 1)) {
            System.out.println("ERROR: conditions logic not compatible with conditions");
            return null;
        }
        
        int nOutDims = histDims.length;        
        int nSpace = field.getNSpace();
        for (int i = 0; i < histCoords.length; i++) {
            if(histCoords[i] > nSpace)
                return null;                    
        }        
        
        int nData = 1;
        for (int i = 0; i < histDims.length; i++) {
            nData *= histDims[i];
        }
        
        float[] hist = null, count = null, sum = null;
        int veclen = operation.getComponent().getVeclen();        
        switch(operation.getOperation()) {
            case COUNT:
                hist = new float[nData];
                for (int i = 0; i < hist.length; i++) {
                    hist[i] = 0;                
                }            
                break;                
            case SUM:
                hist = new float[nData*veclen];
                for (int i = 0; i < hist.length; i++) {
                    hist[i] = 0;                
                }            
                break;
            case MIN:
                hist = new float[nData*veclen];
                for (int i = 0; i < hist.length; i++) {
                    hist[i] = Float.POSITIVE_INFINITY;                
                }            
                break;
            case MAX:
                hist = new float[nData*veclen];
                for (int i = 0; i < hist.length; i++) {
                    hist[i] = Float.NEGATIVE_INFINITY;                
                }            
                break;
            case AVG:
                hist = new float[nData*veclen];
                for (int i = 0; i < hist.length; i++) {
                    hist[i] = 0;
                }            
                count = new float[nData];
                for (int i = 0; i < count.length; i++) {
                    count[i] = 0;
                }            
                break;
            case STD:
                hist = new float[nData*veclen];
                sum = new float[nData*veclen];
                for (int i = 0; i < hist.length; i++) {
                    hist[i] = 0;
                    sum[i] = 0;
                }                            
                count = new float[nData];
                for (int i = 0; i < count.length; i++) {
                    count[i] = 0;
                }            
                
                break;
            case VSTD:
                hist = new float[nData*veclen*veclen];
                for (int i = 0; i < hist.length; i++) {
                    hist[i] = 0;
                }                            
                sum = new float[nData*veclen];
                for (int i = 0; i < sum.length; i++) {
                    sum[i] = 0;
                }                            
                count = new float[nData];
                for (int i = 0; i < count.length; i++) {
                    count[i] = 0;
                }            
                
                break;
            default:
                return null;
        }
        
        int nNodes = field.getNNodes();        
        float[] coords = field.getCoords();
        float[][] extents = field.getExtents();
        float[] scales = new float[nOutDims];
        float v;
        int vv = veclen*veclen;
        float[] tmp;
        for (int i = 0; i < nOutDims; i++) {
            scales[i] = histDims[i] / (extents[1][histCoords[i]] - extents[0][histCoords[i]]);
        }
        
        int nCond = 0;
        boolean[] conditionResults = null;
        
        if (conditions != null) {
            nCond = conditions.length;
            conditionResults = new boolean[nCond];
        }
        
        if(coords == null && field instanceof RegularField) {
            
            float[][] affine = ((RegularField)field).getAffine();
            int[] dims = ((RegularField)field).getDims();
            float[] p = new float[3];
            switch(dims.length) {
                case 3:
                    for (int k = 0; k < dims[2]; k++) {
                        for (int j = 0; j < dims[1]; j++) {
                            for (int i = 0; i < dims[0]; i++) {
                                for (int l = 0; l < 3; l++) {
                                    p[l] = affine[3][l] + k*affine[2][l] + j*affine[1][l] + i*affine[0][l];
                                }

                                if(conditions != null) {
                                    for (int c = 0; c < nCond; c++) {
                                        conditionResults[c] = conditions[c].check(k*dims[0]*dims[1] + j*dims[0] + i);
                                    }
                                    boolean cond = conditionResults[0];
                                    for (int c = 1; c < nCond; c++) {
                                        if(conditionsLogic[c-1] == Condition.Logic.AND) {
                                            cond = cond && conditionResults[c];
                                        } else if(conditionsLogic[c-1] == Condition.Logic.OR) {
                                            cond = cond || conditionResults[c];
                                        }
                                    }
                                    if (!cond) {
                                        continue;
                                    }
                                }
                                
                                int m = 0;
                                for (int l = nOutDims - 1; l >= 0; l--) {
                                    int mm = (int) (scales[l] * (p[histCoords[l]] - extents[0][histCoords[l]]));
                                    if (mm < 0) {
                                        mm = 0;
                                    }
                                    if (mm >= histDims[l]) {
                                        mm = histDims[l] - 1;
                                    }
                                    m = histDims[l] * m + mm;
                                }
                                
                                switch(operation.getOperation()) {
                                    case COUNT:
                                        hist[m]++;
                                        break;
                                    case SUM:
                                        tmp = operation.getVectorComponentValue(k*dims[1]*dims[0] + j*dims[0] + i);
                                        for (int l = 0; l < veclen; l++) {
                                            hist[veclen*m+l] += tmp[l];
                                        }
                                        break;
                                    case MIN:
                                        tmp = operation.getVectorComponentValue(k*dims[1]*dims[0] + j*dims[0] + i);
                                        for (int l = 0; l < veclen; l++) {
                                            int idx = veclen*m+l;
                                            if(tmp[l] < hist[idx])
                                                hist[idx] = tmp[l];
                                        }
                                        break;
                                    case MAX:
                                        tmp = operation.getVectorComponentValue(k*dims[1]*dims[0] + j*dims[0] + i);
                                        for (int l = 0; l < veclen; l++) {
                                            int idx = veclen*m+l;
                                            if(tmp[l] > hist[idx])
                                                hist[idx] = tmp[l];
                                        }
                                        break;
                                    case AVG:
                                        tmp = operation.getVectorComponentValue(k*dims[1]*dims[0] + j*dims[0] + i);
                                        for (int l = 0; l < veclen; l++) {
                                            hist[veclen*m+l] += tmp[l];
                                        }
                                        count[m] += 1;
                                        break;
                                    case STD:
                                        tmp = operation.getVectorComponentValue(k*dims[1]*dims[0] + j*dims[0] + i);
                                        for (int l = 0; l < veclen; l++) {
                                            sum[veclen*m+l] += tmp[l];
                                            hist[veclen*m+l] += tmp[l]*tmp[l];
                                        }
                                        count[m] += 1;
                                        break;
                                    case VSTD:
                                        tmp = operation.getVectorComponentValue(k*dims[1]*dims[0] + j*dims[0] + i);                                        
                                        for (int u = 0; u < veclen; u++) {
                                            sum[veclen*m+u] += tmp[u];
                                        }
                                        count[m] += 1;
                                        break;
                                }
                            }                            
                        }                        
                    }

                    if(operation.getOperation() == HistogramOperation.Operation.VSTD) {
                        for (int k = 0; k < dims[2]; k++) {
                            for (int j = 0; j < dims[1]; j++) {
                                for (int i = 0; i < dims[0]; i++) {
                                    for (int l = 0; l < 3; l++) {
                                        p[l] = affine[3][l] + k*affine[2][l] + j*affine[1][l] + i*affine[0][l];
                                    }

                                    if(conditions != null) {
                                        for (int c = 0; c < nCond; c++) {
                                            conditionResults[c] = conditions[c].check(k*dims[0]*dims[1] + j*dims[0] + i);
                                        }
                                        boolean cond = conditionResults[0];
                                        for (int c = 1; c < nCond; c++) {
                                            if(conditionsLogic[c-1] == Condition.Logic.AND) {
                                                cond = cond && conditionResults[c];
                                            } else if(conditionsLogic[c-1] == Condition.Logic.OR) {
                                                cond = cond || conditionResults[c];
                                            }
                                        }
                                        if (!cond) {
                                            continue;
                                        }
                                    }

                                    int m = 0;
                                    for (int l = nOutDims - 1; l >= 0; l--) {
                                        int mm = (int) (scales[l] * (p[histCoords[l]] - extents[0][histCoords[l]]));
                                        if (mm < 0) {
                                            mm = 0;
                                        }
                                        if (mm >= histDims[l]) {
                                            mm = histDims[l] - 1;
                                        }
                                        m = histDims[l] * m + mm;
                                    }

                                    tmp = operation.getVectorComponentValue(k*dims[1]*dims[0] + j*dims[0] + i);                                        
                                    for (int u = 0; u < veclen; u++) {
                                        v = sum[veclen*m + u]/count[m];
                                        for (int w = 0; w < veclen; w++) {                                                    
                                            hist[vv*m + w*veclen + u] += ((tmp[u]-v)*(tmp[w]-v));
                                        }                                            
                                    }
                                }                            
                            }                        
                        }
                    }
                    
                    break;
                case 2:
                    for (int j = 0; j < dims[1]; j++) {
                        for (int i = 0; i < dims[0]; i++) {
                            for (int l = 0; l < 3; l++) {
                                p[l] = affine[3][l] + j*affine[1][l] + i*affine[0][l];
                            }

                            if(conditions != null) {
                                for (int c = 0; c < nCond; c++) {
                                    conditionResults[c] = conditions[c].check(j*dims[0] + i);
                                }
                                boolean cond = conditionResults[0];
                                for (int c = 1; c < nCond; c++) {
                                    if(conditionsLogic[c-1] == Condition.Logic.AND) {
                                        cond = cond && conditionResults[c];
                                    } else if(conditionsLogic[c-1] == Condition.Logic.OR) {
                                        cond = cond || conditionResults[c];
                                    }
                                }
                                if (!cond) {
                                    continue;
                                }
                            }
                            
                            int m = 0;
                            for (int l = nOutDims - 1; l >= 0; l--) {
                                int mm = (int) (scales[l] * (p[histCoords[l]] - extents[0][histCoords[l]]));
                                if (mm < 0) {
                                    mm = 0;
                                }
                                if (mm >= histDims[l]) {
                                    mm = histDims[l] - 1;
                                }
                                m = histDims[l] * m + mm;
                            }

                            switch(operation.getOperation()) {
                                case COUNT:
                                    hist[m]++;
                                    break;
                                case SUM:
                                    tmp = operation.getVectorComponentValue(j*dims[0] + i);
                                    for (int l = 0; l < veclen; l++) {
                                        hist[veclen*m + l] += tmp[l]; 
                                    }
                                    break;
                                case MIN:
                                    tmp = operation.getVectorComponentValue(j*dims[0] + i);
                                    for (int l = 0; l < veclen; l++) {
                                        int idx = veclen*m + l;
                                        if(tmp[l] < hist[idx])
                                            hist[idx] = tmp[l];
                                    }
                                    break;
                                case MAX:
                                    tmp = operation.getVectorComponentValue(j*dims[0] + i);
                                    for (int l = 0; l < veclen; l++) {
                                        int idx = veclen*m + l;
                                        if(tmp[l] > hist[idx])
                                            hist[idx] = tmp[l];
                                    }
                                    break;
                                case AVG:
                                    tmp = operation.getVectorComponentValue(j*dims[0] + i);
                                    for (int l = 0; l < veclen; l++) {
                                        hist[veclen*m + l] += tmp[l];
                                    }
                                    count[m] += 1;
                                    break;
                                case STD:
                                    tmp = operation.getVectorComponentValue(j*dims[0] + i);
                                    for (int l = 0; l < veclen; l++) {
                                        int idx = veclen*m + l;
                                        sum[idx] += tmp[l];
                                        hist[idx] += tmp[l]*tmp[l];
                                    }
                                    count[m] += 1;
                                    break;
                                case VSTD:
                                    tmp = operation.getVectorComponentValue(j*dims[0] + i);                                        
                                    for (int u = 0; u < veclen; u++) {
                                        sum[veclen*m+u] += tmp[u];
                                    }
                                    count[m] += 1;
                                    break;
                                    
                            }
                        }                            
                    }  
                    
                    if(operation.getOperation() == HistogramOperation.Operation.VSTD) {
                        for (int j = 0; j < dims[1]; j++) {
                            for (int i = 0; i < dims[0]; i++) {
                                for (int l = 0; l < 3; l++) {
                                    p[l] = affine[3][l] + j*affine[1][l] + i*affine[0][l];
                                }

                                if(conditions != null) {
                                    for (int c = 0; c < nCond; c++) {
                                        conditionResults[c] = conditions[c].check(j*dims[0] + i);
                                    }
                                    boolean cond = conditionResults[0];
                                    for (int c = 1; c < nCond; c++) {
                                        if(conditionsLogic[c-1] == Condition.Logic.AND) {
                                            cond = cond && conditionResults[c];
                                        } else if(conditionsLogic[c-1] == Condition.Logic.OR) {
                                            cond = cond || conditionResults[c];
                                        }
                                    }
                                    if (!cond) {
                                        continue;
                                    }
                                }

                                int m = 0;
                                for (int l = nOutDims - 1; l >= 0; l--) {
                                    int mm = (int) (scales[l] * (p[histCoords[l]] - extents[0][histCoords[l]]));
                                    if (mm < 0) {
                                        mm = 0;
                                    }
                                    if (mm >= histDims[l]) {
                                        mm = histDims[l] - 1;
                                    }
                                    m = histDims[l] * m + mm;
                                }

                                tmp = operation.getVectorComponentValue(j*dims[0] + i);                                        
                                for (int u = 0; u < veclen; u++) {
                                    v = sum[veclen*m + u]/count[m];
                                    for (int w = 0; w < veclen; w++) {                                                    
                                        hist[vv*m + w*veclen + u] += ((tmp[u]-v)*(tmp[w]-v));
                                    }                                            
                                }
                            }                            
                        }  
                        
                    }                    
                    
                    break;
                case 1:
                    for (int i = 0; i < dims[0]; i++) {
                        for (int l = 0; l < 3; l++) {
                            p[l] = affine[3][l] + i*affine[0][l];
                        }

                        if(conditions != null) {
                            for (int c = 0; c < nCond; c++) {
                                conditionResults[c] = conditions[c].check(i);
                            }
                            boolean cond = conditionResults[0];
                            for (int c = 1; c < nCond; c++) {
                                if(conditionsLogic[c-1] == Condition.Logic.AND) {
                                    cond = cond && conditionResults[c];
                                } else if(conditionsLogic[c-1] == Condition.Logic.OR) {
                                    cond = cond || conditionResults[c];
                                }
                            }
                            if (!cond) {
                                continue;
                            }
                        }
                        
                        int m = 0;
                        for (int l = nOutDims - 1; l >= 0; l--) {
                            int mm = (int) (scales[l] * (p[histCoords[l]] - extents[0][histCoords[l]]));
                            if (mm < 0) {
                                mm = 0;
                            }
                            if (mm >= histDims[l]) {
                                mm = histDims[l] - 1;
                            }
                            m = histDims[l] * m + mm;
                        }

                        switch(operation.getOperation()) {
                            case COUNT:
                                hist[m]++;
                                break;
                            case SUM:
                                tmp = operation.getVectorComponentValue(i);
                                for (int l = 0; l < veclen; l++) {
                                    hist[veclen*m + l] += tmp[l];
                                }                                
                                break;
                            case MIN:
                                tmp = operation.getVectorComponentValue(i);
                                for (int l = 0; l < veclen; l++) {
                                    int idx = veclen*m + l;
                                    if(tmp[l] < hist[idx])
                                        hist[idx] = tmp[l];
                                }
                                break;
                            case MAX:
                                tmp = operation.getVectorComponentValue(i);
                                for (int l = 0; l < veclen; l++) {
                                    int idx = veclen*m + l;
                                    if(tmp[l] > hist[idx])
                                        hist[idx] = tmp[l];
                                }
                                break;
                            case AVG:
                                tmp = operation.getVectorComponentValue(i);
                                for (int l = 0; l < veclen; l++) {
                                    hist[veclen*m + l] += tmp[l];
                                }
                                count[m] += 1;
                                break;
                            case STD:
                                tmp = operation.getVectorComponentValue(i);
                                for (int l = 0; l < veclen; l++) {
                                    int idx = veclen*m + l;
                                    sum[idx] += tmp[l];
                                    hist[idx] += tmp[l]*tmp[l];
                                }
                                count[m] += 1;
                                break;
                            case VSTD:
                                tmp = operation.getVectorComponentValue(i);                                        
                                for (int u = 0; u < veclen; u++) {
                                    sum[veclen*m+u] += tmp[u];
                                }
                                count[m] += 1;
                                break;
                        }
                    } 
                    
                    if(operation.getOperation() == HistogramOperation.Operation.VSTD) {
                        for (int i = 0; i < dims[0]; i++) {
                            for (int l = 0; l < 3; l++) {
                                p[l] = affine[3][l] + i*affine[0][l];
                            }

                            if(conditions != null) {
                                for (int c = 0; c < nCond; c++) {
                                    conditionResults[c] = conditions[c].check(i);
                                }
                                boolean cond = conditionResults[0];
                                for (int c = 1; c < nCond; c++) {
                                    if(conditionsLogic[c-1] == Condition.Logic.AND) {
                                        cond = cond && conditionResults[c];
                                    } else if(conditionsLogic[c-1] == Condition.Logic.OR) {
                                        cond = cond || conditionResults[c];
                                    }
                                }
                                if (!cond) {
                                    continue;
                                }
                            }

                            int m = 0;
                            for (int l = nOutDims - 1; l >= 0; l--) {
                                int mm = (int) (scales[l] * (p[histCoords[l]] - extents[0][histCoords[l]]));
                                if (mm < 0) {
                                    mm = 0;
                                }
                                if (mm >= histDims[l]) {
                                    mm = histDims[l] - 1;
                                }
                                m = histDims[l] * m + mm;
                            }

                            tmp = operation.getVectorComponentValue(i);                                        
                            for (int u = 0; u < veclen; u++) {
                                v = sum[veclen*m + u]/count[m];
                                for (int w = 0; w < veclen; w++) {                                                    
                                    hist[vv*m + w*veclen + u] += ((tmp[u]-v)*(tmp[w]-v));
                                }                                            
                            }
                        } 
                    }
                    
                    break;
            }
            
        } else if(coords != null) {
            for (int i = 0, c = 0; i < nNodes; i++,c++) {

                if(conditions != null) {
                    for (int cc = 0; cc < nCond; cc++) {
                        conditionResults[cc] = conditions[cc].check(i);
                    }
                    boolean cond = conditionResults[0];
                    for (int cc = 1; cc < nCond; cc++) {
                        if(conditionsLogic[cc-1] == Condition.Logic.AND) {
                            cond = cond && conditionResults[cc];
                        } else if(conditionsLogic[cc-1] == Condition.Logic.OR) {
                            cond = cond || conditionResults[cc];
                        }
                    }
                    if (!cond) {
                        continue;
                    }
                }
                
                int m = 0;
                for (int j = nOutDims-1; j >= 0; j--) {
                    int k = (int) (scales[j] * (coords[nSpace*i + histCoords[j]] - extents[0][histCoords[j]]));
                    if (k < 0) {
                        k = 0;
                    }
                    if (k >= histDims[j]) {
                        k = histDims[j] - 1;
                    }
                    m = histDims[j] * m + k;
                }
                
                switch(operation.getOperation()) {
                    case COUNT:
                        hist[m] += 1;
                        break;
                    case SUM:
                        tmp = operation.getVectorComponentValue(i);
                        for (int l = 0; l < veclen; l++) {
                            hist[veclen*m + l] += tmp[l];
                        }                        
                        break;
                    case MIN:
                        tmp = operation.getVectorComponentValue(i);
                        for (int l = 0; l < veclen; l++) {
                            int idx = veclen*m + l;
                            if(tmp[l] < hist[idx])
                                hist[idx] = tmp[l];
                        }
                        break;
                    case MAX:
                        tmp = operation.getVectorComponentValue(i);
                        for (int l = 0; l < veclen; l++) {
                            int idx = veclen*m + l;
                            if(tmp[l] > hist[idx])
                                hist[idx] = tmp[l];
                        }
                        break;
                    case AVG:
                        tmp = operation.getVectorComponentValue(i);
                        for (int l = 0; l < veclen; l++) {
                            hist[veclen*m + l] += tmp[l];
                        }
                        count[m] += 1;
                        break;
                    case STD:
                        tmp = operation.getVectorComponentValue(i);
                        for (int l = 0; l < veclen; l++) {
                            int idx = veclen*m + l;
                            sum[idx] += tmp[l];
                            hist[idx] += tmp[l]*tmp[l];
                        }
                        count[m] += 1;
                        break;
                    case VSTD:
                        tmp = operation.getVectorComponentValue(i);                                        
                        for (int u = 0; u < veclen; u++) {
                            sum[veclen*m+u] += tmp[u];
                        }
                        count[m] += 1;
                        break;
                }
                
            }
            
            if(operation.getOperation() == HistogramOperation.Operation.VSTD) {
                for (int i = 0, c = 0; i < nNodes; i++,c++) {

                    if(conditions != null) {
                        for (int cc = 0; cc < nCond; cc++) {
                            conditionResults[cc] = conditions[cc].check(i);
                        }
                        boolean cond = conditionResults[0];
                        for (int cc = 1; cc < nCond; cc++) {
                            if(conditionsLogic[cc-1] == Condition.Logic.AND) {
                                cond = cond && conditionResults[cc];
                            } else if(conditionsLogic[cc-1] == Condition.Logic.OR) {
                                cond = cond || conditionResults[cc];
                            }
                        }
                        if (!cond) {
                            continue;
                        }
                    }

                    int m = 0;
                    for (int j = nOutDims-1; j >= 0; j--) {
                        int k = (int) (scales[j] * (coords[nSpace*i + histCoords[j]] - extents[0][histCoords[j]]));
                        if (k < 0) {
                            k = 0;
                        }
                        if (k >= histDims[j]) {
                            k = histDims[j] - 1;
                        }
                        m = histDims[j] * m + k;
                    }

                    tmp = operation.getVectorComponentValue(i);                                        
                    for (int u = 0; u < veclen; u++) {
                        v = sum[veclen*m + u]/count[m];
                        for (int w = 0; w < veclen; w++) {                                                    
                            hist[vv*m + w*veclen + u] += ((tmp[u]-v)*(tmp[w]-v));
                        }                                            
                    }

                }
            }
            
        } else {
            return null;
        }

        
        switch(operation.getOperation()) {
            case MIN:
                for (int i = 0; i < hist.length; i++) {
                    if(hist[i] == Float.POSITIVE_INFINITY)
                        hist[i] = 0;
                }            
                break;
            case MAX:
                for (int i = 0; i < hist.length; i++) {
                    if(hist[i] == Float.NEGATIVE_INFINITY)
                        hist[i] = 0;
                }            
                break;
            case AVG:
                for (int i = 0; i < nData; i++) {
                    if(count[i] > 0) {
                        for (int j = 0; j < veclen; j++) {
                            int idx = veclen*i + j;
                            hist[idx] = hist[idx]/count[i];
                        }                                    
                    }
                }            
                break;
            case STD:
                for (int i = 0; i < nData; i++) {
                    if(count[i] > 0) {
                        for (int j = 0; j < veclen; j++) {
                            int idx = veclen*i + j;
                            v = sum[idx]/count[i];
                            hist[idx] = (float) Math.sqrt(hist[idx]/count[i] - v*v);
                        }
                    }
                }            
                break;
            case VSTD:
                double[][] A = new double[veclen][veclen];
                for (int i = 0; i < nData; i++) {
                    if(count[i] > 0) {
                        for (int u = 0; u < veclen; u++) {
                            for (int w = 0; w < veclen; w++) {                                                
                                A[u][w] = hist[vv*i + w*veclen + u]/count[i];
                            }                                            
                        }
                        
                        double[] eigenValues = new double[veclen];
                        double[][] eigenVectors = new double[veclen][veclen];
                        int res = NumericalMethods.jacobiEigenproblemSolver(A, eigenValues, eigenVectors, 100000);                        
                        if(res != -1) {                            
                            for (int u = 0; u < veclen; u++) {
                                for (int w = 0; w < veclen; w++) {                                                
                                    hist[vv*i + w*veclen + u] = (float)(eigenVectors[u][w] * Math.sqrt(eigenValues[u]));
                                }                                            
                            }
                        } else {
                            for (int u = 0; u < veclen; u++) {
                                for (int w = 0; w < veclen; w++) {                                                
                                    hist[vv*i + w*veclen + u] = 0;
                                }                                            
                            }
                        }
                    }
                }            
                break;
            default:
                break;
        }
        
        if (operation.isDropBackgound()) {
            hist[0] = 0;
        }
        if (operation.isLog()) {
            float histMin = Float.POSITIVE_INFINITY;
            float logConst = operation.getLogConst();
            for (int i = 0; i < hist.length; i++) {
                if(hist[i] < histMin) histMin = hist[i];
            }
            
            for (int i = 0; i < hist.length; i++) {
                hist[i] = (float) Math.log(hist[i] - histMin + logConst);
            }
        }
        return hist;
    }

   private HistogramBuilder()
   {
   }
}
