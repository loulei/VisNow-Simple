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
package pl.edu.icm.visnow.datasets.dataarrays;

import java.util.ArrayList;
import pl.edu.icm.visnow.datasets.TimeData;
import static pl.edu.icm.visnow.datasets.dataarrays.DataArray.create;

/**
 *
 * @author Bartosz Borucki, University of Warsaw, ICM
 *
 */
public class ComplexDataArray extends DataArray {

    private float[] dataRe;
    private float[] dataIm;
    private TimeData<float[]> timeDataRe = new TimeData<float[]>();
    private TimeData<float[]> timeDataIm = new TimeData<float[]>();

    /**
     * Creates a new
     * <code>ComplexDataArray</code> object.
     *
     * @param	schema	This data array schema.
     * @param	ndata	number of data elements.
     */
    public ComplexDataArray(DataArraySchema schema, int ndata) {
        super(schema, ndata);
        dataRe = new float[ndata * schema.getVeclen()];
        dataIm = new float[ndata * schema.getVeclen()];
        timeDataRe.clear();
        timeDataIm.clear();
        abstractTimeData = timeDataRe;
    }

    /**
     * Creates a new instance of ComplexDataArray
     */
    public ComplexDataArray(int ndata, int veclen) {
        super(FIELD_DATA_COMPLEX, ndata, veclen);
        timeDataRe.clear();
        timeDataIm.clear();
        abstractTimeData = timeDataRe;
    }

    public ComplexDataArray(int ndata, int veclen, String name, String units, String[] userData) {
        super(FIELD_DATA_COMPLEX, ndata, veclen, name, units, userData);
        timeDataRe.clear();
        timeDataIm.clear();
        abstractTimeData = timeDataRe;
    }

    public ComplexDataArray(float[] data, String name, String units, String[] userData) {
        super(FIELD_DATA_COMPLEX, data.length / 2, 1, name, units, userData);

        this.dataRe = new float[data.length / 2];
        this.dataIm = new float[data.length / 2];

        for (int i = 0; i < data.length / 2; i++) {
            this.dataRe[i] = data[2 * i];
            this.dataIm[i] = data[2 * i + 1];
        }
        recomputeMinMax();
        timeDataRe.clear();
        timeDataRe.add(dataRe);
        timeDataIm.clear();
        timeDataIm.add(dataIm);
        abstractTimeData = timeDataRe;
    }

    public ComplexDataArray(int ndata, int veclen, String name) {
        super(FIELD_DATA_COMPLEX, ndata, veclen, name, "", null);
        timeDataRe.clear();
        timeDataIm.clear();
        abstractTimeData = timeDataRe;
    }

    public ComplexDataArray(float[] data, String name) {
        super(FIELD_DATA_COMPLEX, data.length / 2, 1, name, "", null);

        this.dataRe = new float[data.length / 2];
        this.dataIm = new float[data.length / 2];

        for (int i = 0; i < data.length / 2; i++) {
            this.dataRe[i] = data[2 * i];
            this.dataIm[i] = data[2 * i + 1];
        }
        recomputeMinMax();
        timeDataRe.clear();
        timeDataRe.add(dataRe);
        timeDataIm.clear();
        timeDataIm.add(dataIm);
        abstractTimeData = timeDataRe;
    }

    public ComplexDataArray(float[] dataReal, float[] dataImag, int veclen, String name, String units, String[] userData) {
        super(FIELD_DATA_COMPLEX, dataReal.length / veclen, veclen, name, units, userData);
        this.dataRe = dataReal;
        this.dataIm = dataImag;
        recomputeMinMax();
        timeDataRe.clear();
        timeDataRe.add(dataRe);
        timeDataIm.clear();
        timeDataIm.add(dataIm);
        abstractTimeData = timeDataRe;
    }

    public ComplexDataArray(float[] dataReal, float[] dataImag, int veclen, String name) {
        super(FIELD_DATA_COMPLEX, dataReal.length / veclen, veclen, name, "", null);
        this.dataRe = dataReal;
        this.dataIm = dataImag;
        recomputeMinMax();
        timeDataRe.clear();
        timeDataRe.add(dataRe);
        timeDataIm.clear();
        timeDataIm.add(dataIm);
        abstractTimeData = timeDataRe;
    }

    public ComplexDataArray(TimeData<float[]> tDataRe, TimeData<float[]> tDataIm, int veclen, String name, String units, String[] userData) {
        super(FIELD_DATA_COMPLEX, (tDataRe == null || tDataRe.get(0) == null) ? -1 : tDataRe.get(0).length / veclen, veclen, name, units, userData);
        timeDataRe = tDataRe;
        timeDataIm = tDataIm;
        abstractTimeData = timeDataRe = tDataRe;
        setCurrentTime(currentTime);
        recomputeMinMax();
    }

    @Override
    public final void recomputeMinMax() {
        float maxv = (float) Math.sqrt(dataRe[0] * dataRe[0] + dataIm[0] * dataIm[0]);
        float minv = (float) Math.sqrt(dataRe[0] * dataRe[0] + dataIm[0] * dataIm[0]);
        int vlen = getVeclen();
        for (int step = 0; step < timeDataRe.size(); step++) {
            float[] dtaRe = timeDataRe.get(step);
            float[] dtaIm = timeDataIm.get(step);
            float tmp;
            if (vlen == 1) {
                for (int i = 1; i < ndata; i++) {
                    tmp = (float) Math.sqrt(dtaRe[i] * dtaRe[i] + dtaIm[i] * dtaIm[i]);
                    if (tmp > maxv) {
                        maxv = tmp;
                    }
                    if (tmp < minv) {
                        minv = tmp;
                    }
                }
            } else {
                for (int i = 1; i < ndata; i++) {
                    tmp = 0;
                    for (int v = 0; v < vlen; v++) {
                        tmp += (float) Math.sqrt(dtaRe[vlen * i + v] * dtaRe[vlen * i + v] + dtaIm[vlen * i + v] * dtaIm[vlen * i + v]);
                    }
                    tmp = (float) Math.sqrt(tmp);
                    if (tmp > maxv) {
                        maxv = tmp;
                    }
                    if (tmp < minv) {
                        minv = tmp;
                    }
                }
            }
        }
        recomputePhysMinMax(minv, maxv);
        setMinv(minv);
        setMaxv(maxv);        
    }

    @Override
    public final void recomputeMinMax(boolean[] mask) {
        float maxv = (float) Math.sqrt(dataRe[0] * dataRe[0] + dataIm[0] * dataIm[0]);
        float minv = (float) Math.sqrt(dataRe[0] * dataRe[0] + dataIm[0] * dataIm[0]);
        int vlen = getVeclen();
        for (int step = 0; step < timeDataRe.size(); step++) {
            float[] dtaRe = timeDataRe.get(step);
            float[] dtaIm = timeDataIm.get(step);
            float tmp;
            if (vlen == 1) {
                for (int i = 1; i < ndata; i++) {
                    if (!mask[i]) {
                        continue;
                    }
                    tmp = (float) Math.sqrt(dtaRe[i] * dtaRe[i] + dtaIm[i] * dtaIm[i]);
                    if (tmp > maxv) {
                        maxv = tmp;
                    }
                    if (tmp < minv) {
                        minv = tmp;
                    }
                }
            } else {
                for (int i = 1; i < ndata; i++) {
                    if (!mask[i]) {
                        continue;
                    }
                    tmp = 0;
                    for (int v = 0; v < vlen; v++) {
                        tmp += (float) Math.sqrt(dtaRe[vlen * i + v] * dtaRe[vlen * i + v] + dtaIm[vlen * i + v] * dtaIm[vlen * i + v]);
                    }
                    tmp = (float) Math.sqrt(tmp);
                    if (tmp > maxv) {
                        maxv = tmp;
                    }
                    if (tmp < minv) {
                        minv = tmp;
                    }
                }
            }
        }
        recomputePhysMinMax(minv, maxv);
        setMinv(minv);
        setMaxv(maxv);        
    }

    @Override
    public void recomputeMinMax(TimeData<boolean[]> timeMask) {
        float maxv = (float) Math.sqrt(dataRe[0] * dataRe[0] + dataIm[0] * dataIm[0]);
        float minv = (float) Math.sqrt(dataRe[0] * dataRe[0] + dataIm[0] * dataIm[0]);
        int vlen = getVeclen();
        ArrayList<Float> timeline = timeDataRe.getTimeSeries();
        for (int step = 0; step < timeDataRe.size(); step++) {
            float[] dtaRe = timeDataRe.get(step);
            float[] dtaIm = timeDataIm.get(step);
            boolean[] mask = timeMask.getData(timeline.get(step));
            float tmp;
            if (vlen == 1) {
                for (int i = 1; i < ndata; i++) {
                    if (!mask[i]) {
                        continue;
                    }
                    tmp = (float) Math.sqrt(dtaRe[i] * dtaRe[i] + dtaIm[i] * dtaIm[i]);
                    if (tmp > maxv) {
                        maxv = tmp;
                    }
                    if (tmp < minv) {
                        minv = tmp;
                    }
                }
            } else {
                for (int i = 1; i < ndata; i++) {
                    if (!mask[i]) {
                        continue;
                    }
                    tmp = 0;
                    for (int v = 0; v < vlen; v++) {
                        tmp += (float) Math.sqrt(dtaRe[vlen * i + v] * dtaRe[vlen * i + v] + dtaIm[vlen * i + v] * dtaIm[vlen * i + v]);
                    }
                    tmp = (float) Math.sqrt(tmp);
                    if (tmp > maxv) {
                        maxv = tmp;
                    }
                    if (tmp < minv) {
                        minv = tmp;
                    }
                }
            }
        }
        recomputePhysMinMax(minv, maxv);
        setMinv(minv);
        setMaxv(maxv);        
    }

    @Override
    public ComplexDataArray clone(String newName) {
        ComplexDataArray da;
        if (schema.getUserData() != null) {
            da = new ComplexDataArray(timeDataRe, timeDataIm, schema.getVeclen(), newName, schema.getUnit(), schema.getUserData());
        }else{
            da = new ComplexDataArray(timeDataRe, timeDataIm, schema.getVeclen(), newName, schema.getUnit(), null);
        }
        da.setCurrentTime(currentTime);
        return da;
    }

    @Override
    public ComplexDataArray cloneDeep(String newName) {
        ComplexDataArray da;
        if (schema.getUserData() != null) {
            da = new ComplexDataArray((TimeData<float[]>) timeDataRe.clone(), (TimeData<float[]>) timeDataIm.clone(), schema.getVeclen(), newName, schema.getUnit(), schema.getUserData().clone());
        } else {
            da = new ComplexDataArray((TimeData<float[]>) timeDataRe.clone(), (TimeData<float[]>) timeDataIm.clone(), schema.getVeclen(), newName, schema.getUnit(), null);
        }
        da.setCurrentTime(currentTime);
        return da;
    }

    public byte[] getBRealData() {
        if (dataRe == null || dataIm == null) {
            setCurrentTime(currentTime);
        }
        byte[] out = new byte[dataRe.length];
        for (int i = 0; i < out.length; i++) {
            out[i] = (byte) dataRe[i];
        }
        return out;
    }

    public byte[] getBImagData() {
        if (dataRe == null || dataIm == null) {
            setCurrentTime(currentTime);
        }
        byte[] out = new byte[dataIm.length];
        for (int i = 0; i < out.length; i++) {
            out[i] = (byte) dataIm[i];
        }
        return out;
    }

    public byte[] getBAbsData() {
        if (dataRe == null || dataIm == null) {
            setCurrentTime(currentTime);
        }
        byte[] out = new byte[dataRe.length];
        for (int i = 0; i < out.length; i++) {
            out[i] = (byte) Math.sqrt(dataRe[i] * dataRe[i] + dataIm[i] * dataIm[i]);
        }
        return out;
    }

    public byte[] getBArgData() {
        if (dataRe == null || dataIm == null) {
            setCurrentTime(currentTime);
        }
        byte[] out = new byte[dataRe.length];
        for (int i = 0; i < out.length; i++) {
            out[i] = (byte) Math.atan2(dataIm[i], dataRe[i]);
        }
        return out;
    }

    public short[] getSRealData() {
        if (dataRe == null || dataIm == null) {
            setCurrentTime(currentTime);
        }
        short[] out = new short[dataRe.length];
        for (int i = 0; i < out.length; i++) {
            out[i] = (short) dataRe[i];
        }
        return out;
    }

    public short[] getSImagData() {
        if (dataRe == null || dataIm == null) {
            setCurrentTime(currentTime);
        }
        short[] out = new short[dataIm.length];
        for (int i = 0; i < out.length; i++) {
            out[i] = (short) dataIm[i];
        }
        return out;
    }

    public short[] getSAbsData() {
        if (dataRe == null || dataIm == null) {
            setCurrentTime(currentTime);
        }
        short[] out = new short[dataRe.length];
        for (int i = 0; i < out.length; i++) {
            out[i] = (short) Math.sqrt(dataRe[i] * dataRe[i] + dataIm[i] * dataIm[i]);
        }
        return out;
    }

    public short[] getSArgData() {
        if (dataRe == null || dataIm == null) {
            setCurrentTime(currentTime);
        }
        short[] out = new short[dataRe.length];
        for (int i = 0; i < out.length; i++) {
            out[i] = (short) Math.atan2(dataIm[i], dataRe[i]);
        }
        return out;
    }

    public int[] getIRealData() {
        if (dataRe == null || dataIm == null) {
            setCurrentTime(currentTime);
        }
        int[] out = new int[dataRe.length];
        for (int i = 0; i < out.length; i++) {
            out[i] = (int) dataRe[i];
        }
        return out;
    }

    public int[] getIImagData() {
        if (dataRe == null || dataIm == null) {
            setCurrentTime(currentTime);
        }
        int[] out = new int[dataIm.length];
        for (int i = 0; i < out.length; i++) {
            out[i] = (int) dataIm[i];
        }
        return out;
    }

    public int[] getIAbsData() {
        if (dataRe == null || dataIm == null) {
            setCurrentTime(currentTime);
        }
        int[] out = new int[dataRe.length];
        for (int i = 0; i < out.length; i++) {
            out[i] = (int) Math.sqrt(dataRe[i] * dataRe[i] + dataIm[i] * dataIm[i]);
        }
        return out;
    }

    public int[] getIArgData() {
        if (dataRe == null || dataIm == null) {
            setCurrentTime(currentTime);
        }
        int[] out = new int[dataRe.length];
        for (int i = 0; i < out.length; i++) {
            out[i] = (int) Math.atan2(dataIm[i], dataRe[i]);
        }
        return out;
    }

    public float[] getFRealData() {
        if (dataRe == null || dataIm == null) {
            setCurrentTime(currentTime);
        }
        return dataRe;
    }

    public float[] getFImagData() {
        if (dataRe == null || dataIm == null) {
            setCurrentTime(currentTime);
        }
        return dataIm;
    }

    public float[] getFAbsData() {
        if (dataRe == null || dataIm == null) {
            setCurrentTime(currentTime);
        }
        float[] out = new float[dataRe.length];
        for (int i = 0; i < dataRe.length; i++) {
            out[i] = (float) Math.sqrt(dataRe[i] * dataRe[i] + dataIm[i] * dataIm[i]);
        }
        return out;
    }

    public float[] getFArgData() {
        if (dataRe == null || dataIm == null) {
            setCurrentTime(currentTime);
        }
        float[] out = new float[dataRe.length];
        for (int i = 0; i < out.length; i++) {
            out[i] = (float) Math.atan2(dataIm[i], dataRe[i]);
        }
        return out;
    }

    @Override
    public float[] get2DSlice(int start, int n0, int step0, int n1, int step1) {
        if (dataRe == null || dataIm == null) {
            setCurrentTime(currentTime);
        }

        int veclen = schema.getVeclen();
        float[] out = new float[n0 * n1 * veclen];
        int i0, i1, j, k, l0, l1;
        for (i1 = k = 0, l1 = start * veclen; i1 < n1; i1++, l1 += step1 * veclen) {
            for (i0 = 0, l0 = l1; i0 < n0; i0++, l0 += step0 * veclen) {
                for (j = 0; j < veclen; j++, k++) {
                    out[k] = (float) Math.sqrt(dataRe[l0 + j] * dataRe[l0 + j] + dataIm[l0 + j] * dataIm[l0 + j]);
                }
            }
        }
        return out;
    }

    @Override
    public float[] get2DNormSlice(int start, int n0, int step0, int n1, int step1) {
        if (dataRe == null || dataIm == null) {
            setCurrentTime(currentTime);
        }

        int veclen = schema.getVeclen();
        if (veclen == 1) {
            return get2DSlice(start, n0, step0, n1, step1);
        }
        float[] out = new float[n0 * n1];
        int i0, i1, j, k, l0, l1;
        for (i1 = k = 0, l1 = start * veclen; i1 < n1; i1++, l1 += step1 * veclen) {
            for (i0 = 0, l0 = l1; i0 < n0; i0++, l0 += step0 * veclen, k++) {
                double d = 0;
                for (j = 0; j < veclen; j++) {
                    d += dataRe[l0 + j] * dataRe[l0 + j] + dataIm[l0 + j] * dataIm[l0 + j];
                }
                out[k] = (float) Math.sqrt(d);
            }
        }
        return out;
    }

    @Override
    public float[] get1DSlice(int start, int n, int step) {
        if (dataRe == null || dataIm == null) {
            setCurrentTime(currentTime);
        }
        int veclen = schema.getVeclen();
        float[] out = new float[n * veclen];
        int i, j, k, l;
        for (i = k = 0, l = start * veclen; i < n; i++, l += step * veclen) {
            for (j = 0; j < veclen; j++, k++) {
                out[k] = (float) Math.sqrt(dataRe[l + j] * dataRe[l + j] + dataIm[l + j] * dataIm[l + j]);
            }
        }
        return out;
    }

    @Override
    public float[] getVData(int start) {
        return null;
    }

    @Override
    public byte[] getBData() {
        if (dataRe == null || dataIm == null) {
            setCurrentTime(currentTime);
        }
        byte[] out = new byte[dataRe.length];
        for (int i = 0; i < out.length; i++) {
            out[i] = (byte) Math.sqrt(dataRe[i] * dataRe[i] + dataIm[i] * dataIm[i]);
        }
        return out;
    }

    @Override
    public short[] getSData() {
        if (dataRe == null || dataIm == null) {
            setCurrentTime(currentTime);
        }
        short[] out = new short[dataRe.length];
        for (int i = 0; i < out.length; i++) {
            out[i] = (short) Math.sqrt(dataRe[i] * dataRe[i] + dataIm[i] * dataIm[i]);
        }
        return out;
    }

    /**
     * Returns truncated (to integer) norm of complex numbers. Real numbers are rounded toward zero (discarding the mantissa).
     */
    @Override
    public int[] getIData() {
        if (dataRe == null || dataIm == null) {
            setCurrentTime(currentTime);
        }
        int[] out = new int[dataRe.length];
        for (int i = 0; i < out.length; i++) {
            out[i] = (int) Math.sqrt(dataRe[i] * dataRe[i] + dataIm[i] * dataIm[i]);
        }
        return out;
    }

    @Override
    public float[] getFData() {
        if (dataRe == null || dataIm == null) {
            setCurrentTime(currentTime);
        }
        float[] out = new float[dataRe.length];
        for (int i = 0; i < out.length; i++) {
            out[i] = (float) Math.sqrt(dataRe[i] * dataRe[i] + dataIm[i] * dataIm[i]);
        }
        return out;
    }

    //TODO: actually what is the functionality of this method? square root from sum of norms of vector elements? Why not sum of square norms?
    @Override
    public float[] getNormFData() {
        if (dataRe == null || dataIm == null) {
            setCurrentTime(currentTime);
        }
        int veclen = schema.getVeclen();
        if (veclen == 1) {
            return getFData();
        }
        if (dataRe == null) {
            setCurrentTime(currentTime);
        }
        float[] outData = new float[dataRe.length / veclen];
        for (int i = 0, k = 0; i < outData.length; i++) {
            double d = 0;
            for (int j = 0; j < veclen; j++, k++) {
                d += dataRe[k] * dataRe[k] + dataIm[k] * dataIm[k];
            }
            outData[i] = (float) Math.sqrt(d);
        }
        return outData;
    }
    
    /**
     * Returns norms of complex numbers.
     * @return 
     */
    @Override
    public double[] getDData() {
        if (dataRe == null || dataIm == null) {
            setCurrentTime(currentTime);
        }
        double[] out = new double[dataRe.length];
        for (int i = 0; i < out.length; i++) {
            out[i] = Math.sqrt(dataRe[i] * dataRe[i] + dataIm[i] * dataIm[i]);
        }
        return out;
    }

    @Override
    public float getData(int i) {
        if (dataRe == null || dataIm == null) {
            setCurrentTime(currentTime);
        }
        return (float) Math.sqrt(dataRe[i] * dataRe[i] + dataIm[i] * dataIm[i]);
    }

    public static float[] log10(float[] in) {
        float[] out = new float[in.length];
        for (int i = 0; i < in.length; i++) {
            if (in[i] + 1 < 1) {
                return null;
            }
            out[i] = (float) Math.log10(in[i]);
        }
        return out;
    }

    public static float[] log(float[] in) {
        float[] out = new float[in.length];
        for (int i = 0; i < in.length; i++) {
            if (in[i] + 1 < 1) {
                return null;
            }
            out[i] = (float) Math.log(in[i]);
        }
        return out;
    }

    @Override
    public void resetData() {
        dataRe = timeDataRe.getData(currentTime);
        timeDataRe.clear();
        timeDataRe.setData(dataRe, 0);
        dataIm = timeDataIm.getData(currentTime);
        timeDataIm.clear();
        timeDataIm.setData(dataIm, 0);
    }

    @Override
    public void addData(Object d, float time) {
        if (d instanceof float[][]) {
            timeDataRe.setData(((float[][]) d)[0], time);
            timeDataIm.setData(((float[][]) d)[1], time);
            currentTime = time;
            timeDataRe.setCurrentTime(time);
            timeDataIm.setCurrentTime(time);
            dataRe = timeDataRe.getData();
            dataIm = timeDataIm.getData();
            recomputeMinMax();
        } else if (d instanceof float[]) {
            float[] inData = (float[]) d;
            dataRe = new float[ndata];
            dataIm = new float[ndata];
            for (int i = 0; i < ndata; i++) {
                dataRe[i] = inData[2 * i];
                dataIm[i] = inData[2 * i + 1];
            }
            timeDataRe.setData(dataRe, time);
            timeDataIm.setData(dataIm, time);
            currentTime = time;
            timeDataRe.setCurrentTime(time);
            timeDataIm.setCurrentTime(time);
            dataRe = timeDataRe.getData();
            dataIm = timeDataIm.getData();
            recomputeMinMax();
        }
    }

    @Override
    public final void setCurrentTime(float currentTime) {
        if (currentTime == timeDataRe.getCurrentTime() && dataRe != null && currentTime == timeDataIm.getCurrentTime() && dataIm != null) {
            return;
        }
        timeDataRe.setCurrentTime(currentTime);
        dataRe = timeDataRe.getData();
        timeDataIm.setCurrentTime(currentTime);
        dataIm = timeDataIm.getData();
    }

    @Override
    public byte[] produceBData(float time) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public short[] produceSData(float time) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int[] produceIData(float time) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public float[] produceFData(float time) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double[] produceDData(float time) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setTimeData(TimeData tData) {
        if (tData == null || tData.isEmpty()) {
            return;
        }
        if (!(tData.get(0) instanceof float[]) || ((float[]) (tData.get(0))).length != ndata * getVeclen()) {
            return;
        }
        abstractTimeData = timeDataRe = tData;
        setCurrentTime(currentTime);
        recomputeMinMax();
    }

    public void setTimeData(TimeData tDataRe, TimeData tDataIm) {
        if (tDataRe == null || tDataRe.isEmpty() || tDataIm == null || tDataIm.isEmpty()) {
            return;
        }
        if (!(tDataRe.get(0) instanceof float[]) || ((float[]) (tDataRe.get(0))).length != ndata * getVeclen()
                || !(tDataIm.get(0) instanceof float[]) || ((float[]) (tDataIm.get(0))).length != ndata * getVeclen()) {
            return;
        }
        //TODO: this should be probably solved in some other way. Now abstactTimeData used many times in DataArray is set to real part of this complex time data which may lead to incorrect behaviour.
        abstractTimeData = timeDataRe = tDataRe;
        timeDataIm = tDataIm;
        setCurrentTime(currentTime);
    }

    @Override
    public TimeData getTimeData() {
        return null;
    }

    public TimeData getTimeDataReal() {
        return timeDataRe;
    }

    public TimeData getTimeDataImag() {
        return timeDataIm;
    }

    @Override
    public Object getData() {
        return getFData();
    }

    @Override
    public DataArray get2DSlice(int[] dims, int axis, int slice) {
        if (dims == null || dims.length != 3 || axis < 0 || axis > 2 || slice < 0 || slice >= dims[axis]) {
            return null;
        }
        TimeData slicedTimeDataRe = timeDataRe.get2DTimeDataSlice(dims, axis, slice, getVeclen());
        TimeData slicedTimeDataIm = timeDataIm.get2DTimeDataSlice(dims, axis, slice, getVeclen());
        DataArray da = create(getType(), getNData() / dims[axis], getVeclen(), getName(), getUnit(), getUserData());
        ((ComplexDataArray)da).setTimeData(slicedTimeDataRe, slicedTimeDataIm);
        da.setCurrentTime(currentTime);
        return da;
    }
}
