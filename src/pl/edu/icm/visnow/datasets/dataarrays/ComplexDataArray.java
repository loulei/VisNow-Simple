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

import pl.edu.icm.visnow.datasets.TimeData;

/**
 *
 * @author Bartosz Borucki, University of Warsaw, ICM
 *
 */
public class ComplexDataArray extends DataArray {

    private float[] dataRe;
    private float[] dataIm;
    private TimeData<float[]> timeData = new TimeData<float[]>();

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
        schema.setMinv(1.e10f);
        schema.setMaxv(-1.e10f);
        timeData.clear();
        abstractTimeData = timeData;
    }

    /**
     * Creates a new instance of ComplexDataArray
     */
    public ComplexDataArray(int ndata, int veclen) {
        super(FIELD_DATA_COMPLEX, ndata, veclen);
        //dataRe = new float[ndata*veclen];
        //dataIm = new float[ndata*veclen];
        schema.setMinv(1.e10f);
        schema.setMaxv(-1.e10f);
        timeData.clear();
        abstractTimeData = timeData;
    }

    public ComplexDataArray(int ndata, int veclen, String name, String units, String[] userData) {
        super(FIELD_DATA_COMPLEX, ndata, veclen, name, units, userData);
        //dataRe = new float[ndata*veclen];
        //dataIm = new float[ndata*veclen];
        schema.setMinv(1.e10f);
        schema.setMaxv(-1.e10f);
        timeData.clear();
        abstractTimeData = timeData;
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
        timeData.clear();
        timeData.add(dataRe);
        timeData.add(dataIm);
        abstractTimeData = timeData;
    }

    public ComplexDataArray(int ndata, int veclen, String name) {
        super(FIELD_DATA_COMPLEX, ndata, veclen, name, "", null);
        //dataRe = new float[ndata*veclen];
        //dataIm = new float[ndata*veclen];
        schema.setMinv(1.e10f);
        schema.setMaxv(-1.e10f);
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
        timeData.clear();
        timeData.add(dataRe);
        timeData.add(dataIm);
        abstractTimeData = timeData;
    }

    public ComplexDataArray(float[] dataReal, float[] dataImag, int veclen, String name, String units, String[] userData) {
        super(FIELD_DATA_COMPLEX, dataReal.length / veclen, veclen, name, units, userData);
        this.dataRe = dataReal;
        this.dataIm = dataImag;
        recomputeMinMax();
        timeData.clear();
        timeData.add(dataRe);
        timeData.add(dataIm);
        abstractTimeData = timeData;
    }

    public ComplexDataArray(float[] dataReal, float[] dataImag, int veclen, String name) {
        super(FIELD_DATA_COMPLEX, dataReal.length / veclen, veclen, name, "", null);
        this.dataRe = dataReal;
        this.dataIm = dataImag;
        recomputeMinMax();
        timeData.clear();
        timeData.add(dataRe);
        timeData.add(dataIm);
        abstractTimeData = timeData;
    }

    @Override
    public final void recomputeMinMax() {
        float maxv = (float) Math.sqrt(dataRe[0] * dataRe[0] + dataIm[0] * dataIm[0]);
        float minv = (float) Math.sqrt(dataRe[0] * dataRe[0] + dataIm[0] * dataIm[0]);

        float tmp;
        for (int i = 1; i < dataRe.length; i++) {
            tmp = (float) Math.sqrt(dataRe[i] * dataRe[i] + dataIm[i] * dataIm[i]);
            if (tmp > maxv) {
                maxv = tmp;
            }
            if (tmp < minv) {
                minv = tmp;
            }
        }
        schema.setMinv(minv);
        schema.setMaxv(maxv);
        schema.setPhysMin(minv);
        schema.setPhysMax(maxv);
    }

    @Override
    public final void recomputeMinMax(boolean[] mask) {
        float maxv = (float) Math.sqrt(dataRe[0] * dataRe[0] + dataIm[0] * dataIm[0]);
        float minv = (float) Math.sqrt(dataRe[0] * dataRe[0] + dataIm[0] * dataIm[0]);

        float tmp;
        for (int i = 1; i < dataRe.length; i++) {
            if (!mask[i]) {
                continue;
            }
            tmp = (float) Math.sqrt(dataRe[i] * dataRe[i] + dataIm[i] * dataIm[i]);
            if (tmp > maxv) {
                maxv = tmp;
            }
            if (tmp < minv) {
                minv = tmp;
            }
        }
        schema.setMinv(minv);
        schema.setMaxv(maxv);
        schema.setPhysMin(minv);
        schema.setPhysMax(maxv);
    }

    @Override
    public ComplexDataArray clone(String newName) {
        return new ComplexDataArray(dataRe, dataIm, schema.getVeclen(), newName);
    }

    @Override
    public ComplexDataArray cloneDeep(String newName) {
        return new ComplexDataArray(dataRe.clone(), dataIm.clone(), schema.getVeclen(), new String(newName));
    }

    public byte[] getBRealData() {
        byte[] out = new byte[dataRe.length];
        for (int i = 0; i < out.length; i++) {
            out[i] = (byte) dataRe[i];
        }
        return out;
    }

    public byte[] getBImagData() {
        byte[] out = new byte[dataIm.length];
        for (int i = 0; i < out.length; i++) {
            out[i] = (byte) dataIm[i];
        }
        return out;
    }

    public byte[] getBAbsData() {
        byte[] out = new byte[dataRe.length];
        for (int i = 0; i < out.length; i++) {
            out[i] = (byte) Math.sqrt(dataRe[i] * dataRe[i] + dataIm[i] * dataIm[i]);
        }
        return out;
    }

    public byte[] getBArgData() {
        byte[] out = new byte[dataRe.length];
        for (int i = 0; i < out.length; i++) {
            out[i] = (byte) Math.atan2(dataIm[i], dataRe[i]);
        }
        return out;
    }

    public short[] getSRealData() {
        short[] out = new short[dataRe.length];
        for (int i = 0; i < out.length; i++) {
            out[i] = (short) dataRe[i];
        }
        return out;
    }

    public short[] getSImagData() {
        short[] out = new short[dataIm.length];
        for (int i = 0; i < out.length; i++) {
            out[i] = (short) dataIm[i];
        }
        return out;
    }

    public short[] getSAbsData() {
        short[] out = new short[dataRe.length];
        for (int i = 0; i < out.length; i++) {
            out[i] = (short) Math.sqrt(dataRe[i] * dataRe[i] + dataIm[i] * dataIm[i]);
        }
        return out;
    }

    public short[] getSArgData() {
        short[] out = new short[dataRe.length];
        for (int i = 0; i < out.length; i++) {
            out[i] = (short) Math.atan2(dataIm[i], dataRe[i]);
        }
        return out;
    }

    public int[] getIRealData() {
        int[] out = new int[dataRe.length];
        for (int i = 0; i < out.length; i++) {
            out[i] = (int) dataRe[i];
        }
        return out;
    }

    public int[] getIImagData() {
        int[] out = new int[dataIm.length];
        for (int i = 0; i < out.length; i++) {
            out[i] = (int) dataIm[i];
        }
        return out;
    }

    public int[] getIAbsData() {
        int[] out = new int[dataRe.length];
        for (int i = 0; i < out.length; i++) {
            out[i] = (int) Math.sqrt(dataRe[i] * dataRe[i] + dataIm[i] * dataIm[i]);
        }
        return out;
    }

    public int[] getIArgData() {
        int[] out = new int[dataRe.length];
        for (int i = 0; i < out.length; i++) {
            out[i] = (int) Math.atan2(dataIm[i], dataRe[i]);
        }
        return out;
    }

    public float[] getFRealData() {
        return dataRe;
    }

    public float[] getFImagData() {
        return dataIm;
    }

    public float[] getFAbsData() {
        float[] out = new float[dataRe.length];
        for (int i = 0; i < dataRe.length; i++) {
            out[i] = (float) Math.sqrt(dataRe[i] * dataRe[i] + dataIm[i] * dataIm[i]);
        }
        return out;
    }

    public float[] getFArgData() {
        float[] out = new float[dataRe.length];
        for (int i = 0; i < out.length; i++) {
            out[i] = (float) Math.atan2(dataIm[i], dataRe[i]);
        }
        return out;
    }

    @Override
    public float[] get2DSlice(int start, int n0, int step0, int n1, int step1) {
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
        byte[] out = new byte[dataRe.length];
        for (int i = 0; i < out.length; i++) {
            out[i] = (byte) Math.sqrt(dataRe[i] * dataRe[i] + dataIm[i] * dataIm[i]);
        }
        return out;
    }

    @Override
    public short[] getSData() {
        short[] out = new short[dataRe.length];
        for (int i = 0; i < out.length; i++) {
            out[i] = (short) Math.sqrt(dataRe[i] * dataRe[i] + dataIm[i] * dataIm[i]);
        }
        return out;
    }

    @Override
    public int[] getIData() {
        int[] out = new int[dataRe.length];
        for (int i = 0; i < out.length; i++) {
            out[i] = (int) Math.sqrt(dataRe[i] * dataRe[i] + dataIm[i] * dataIm[i]);
        }
        return out;
    }

    @Override
    public float[] getFData() {
        float[] out = new float[dataRe.length];
        for (int i = 0; i < out.length; i++) {
            out[i] = (float) Math.sqrt(dataRe[i] * dataRe[i] + dataIm[i] * dataIm[i]);
        }
        return out;
    }

    @Override
    public float[] getNormFData() {
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

    @Override
    public double[] getDData() {
        double[] out = new double[dataRe.length];
        for (int i = 0; i < out.length; i++) {
            out[i] = Math.sqrt(dataRe[i] * dataRe[i] + dataIm[i] * dataIm[i]);
        }
        return out;
    }

    @Override
    public float getData(int i) {
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addData(Object d, float time) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setCurrentTime(float currentTime) {
        timeData.setCurrentTime(currentTime);
        dataRe = timeData.getData(2*currentTime);
        dataIm = timeData.getData(2*currentTime+1);
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
        abstractTimeData = timeData = tData;
//        recomputeMinMax();
        setCurrentTime(currentTime);
    }

    @Override
    public TimeData getTimeData() {
        return timeData;
    }

    @Override
    public void setCurrentFrame(int currentFrame) {
        //currentFrame = Math.max(0, Math.min(currentFrame, timeData.size()));
        //data = timeData.get(currentFrame);
        this.currentFrame = currentFrame;
    }

    @Override
    public Object getData() {
        return getFData();
    }

    @Override
    public void recomputeMinMax(TimeData<boolean[]> timeMask) {
    }
}
