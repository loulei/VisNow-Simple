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
import pl.edu.icm.visnow.lib.utils.RabinHashFunction;

/**
 *
 * @author Bartosz Borucki, University of Warsaw, ICM
 *
 */
public class LogicDataArray extends DataArray {

    private BitArray data;
    private TimeData<BitArray> timeData = new TimeData<BitArray>();

    /**
     * Creates a new
     * <code>LogicDataArray</code> object.
     *
     * @param	schema	This data array schema.
     * @param	ndata	number of data elements.
     */
    public LogicDataArray(DataArraySchema schema, int ndata) {
        super(schema, ndata);
        data = new BitArray(ndata * schema.getVeclen());
        timeData.clear();
        abstractTimeData = timeData;
        setMinv(0.f);
        setMaxv(1.f);
    }

    /**
     * Creates a new instance of LogicDataArray
     *
     * @param ndata number of data elements in the LogicDataArray
     * @param veclen vector length (1 for scalar data)
     */
    public LogicDataArray(int ndata, int veclen) {
        super(FIELD_DATA_LOGIC, ndata, veclen);
        data = new BitArray(ndata * veclen);
        timeData.clear();
        timeData.add(data);
        abstractTimeData = timeData;
        setMinv(0.f);
        setMaxv(1.f);
    }

    /**
     * Creates a new instance of LogicDataArray
     *
     * @param ndata number of data elements in the LogicDataArray
     * @param veclen vector length (1 for scalar data)
     * @param name data array name
     */
    public LogicDataArray(int ndata, int veclen, String name) {
        super(FIELD_DATA_LOGIC, ndata, veclen, name, "", null);
        data = new BitArray(ndata * veclen);
        timeData.clear();
        timeData.add(data);
        abstractTimeData = timeData;
        setMinv(0);
        setMaxv(1);
        setPhysMin(0);
        setPhysMax(1);
    }

    /**
     * Creates a new instance of LogicDataArray
     *
     * @param data byte array to be included in the generated LogicDataArray
     * object
     * @param veclen vector length (1 for scalar data)
     * @param name data array name
     */
    public LogicDataArray(byte[] data, int veclen, String name, String units, String[] userData) {
        super(FIELD_DATA_LOGIC, data.length / veclen, veclen, name, units, userData);
        this.data = BitArray.createBitArray(data, true);
        timeData.clear();
        timeData.add(this.data);
        abstractTimeData = timeData;
        recomputeMinMax();
    }

    /**
     * Creates a new instance of LogicDataArray
     *
     * @param data byte array to be included in the generated LogicDataArray
     * object
     * @param veclen vector length (1 for scalar data)
     * @param name data array name
     */
    public LogicDataArray(int ndata, int veclen, String name, String units, String[] userData) {
        super(FIELD_DATA_LOGIC, ndata, veclen, name, units, userData);
        data = new BitArray(ndata * veclen);
        timeData.clear();
        timeData.add(data);
        abstractTimeData = timeData;
        recomputeMinMax();
    }

    /**
     * Creates a new instance of LogicDataArray
     *
     * @param data BitArray to be included in the generated LogicDataArray
     * object
     * @param veclen vector length (1 for scalar data)
     * @param name data array name
     */
    public LogicDataArray(BitArray data, int veclen, String name, String units, String[] userData) {
        super(FIELD_DATA_LOGIC, data.size() / veclen, veclen, name, units, userData);
        this.data = data;
        timeData.clear();
        timeData.add(data);
        abstractTimeData = timeData;
        recomputeMinMax();
    }
    
   public LogicDataArray(TimeData<BitArray> tData, int veclen, String name, String units, String[] userData)
   {
        super(FIELD_DATA_LOGIC, (tData == null || tData.get(0) == null) ? -1 : tData.get(0).getSize() / veclen, veclen, name, units, userData);
        abstractTimeData = timeData = tData;
        setCurrentTime(currentTime);
        recomputeMinMax();
   }
    
    @Override
    public LogicDataArray clone(String newName) {
        LogicDataArray da = new LogicDataArray(new BitArray(data), schema.getVeclen(), newName, schema.getUnit(), schema.getUserData());
        da.setCurrentTime(currentTime);
        return da;
    }

    @Override
    public LogicDataArray cloneDeep(String newName) {
       LogicDataArray da;
       if(schema.getUserData() != null)
            da = new LogicDataArray((TimeData<BitArray>)timeData.clone(), schema.getVeclen(), newName, schema.getUnit(), schema.getUserData().clone());
       else
            da = new LogicDataArray((TimeData<BitArray>)timeData.clone(), schema.getVeclen(), newName, schema.getUnit(), null);
       da.setCurrentTime(currentTime);
       return da;
    }

    @Override
    public float[] get2DSlice(int start, int n0, int step0, int n1, int step1) {
        if (data == null)
           setCurrentTime(currentTime);
        int veclen = schema.getVeclen();
        float[] out = new float[n0 * n1 * veclen];
        int i0, i1, j, k, l0, l1;
        for (i1 = k = 0, l1 = start * veclen; i1 < n1; i1++, l1 += step1 * veclen) {
            for (i0 = 0, l0 = l1; i0 < n0; i0++, l0 += step0 * veclen) {
                for (j = 0; j < veclen; j++, k++) {
                    out[k] = (float) (0xFF & data.getByteValueAtIndex(l0 + j));
                }
            }
        }
        return out;
    }

    @Override
    public float[] get2DNormSlice(int start, int n0, int step0, int n1, int step1) {
        if (data == null)
           setCurrentTime(currentTime);
        int veclen = schema.getVeclen();
        if (veclen == 1) {
            return get2DSlice(start, n0, step0, n1, step1);
        }
        if (data == null) {
            setCurrentTime(currentTime);
        }
        float[] out = new float[n0 * n1];
        int i0, i1, j, k, l0, l1;
        for (i1 = k = 0, l1 = start * veclen; i1 < n1; i1++, l1 += step1 * veclen) {
            for (i0 = 0, l0 = l1; i0 < n0; i0++, l0 += step0 * veclen, k++) {
                double d = 0;
                for (j = 0; j < veclen; j++) {
                    d += (float) (0xFF & data.getByteValueAtIndex(l0 + j)) * (float) (0xFF & data.getByteValueAtIndex(l0 + j));
                }
                out[k] = (float) Math.sqrt(d);
            }
        }
        return out;
    }

    public byte[] get2DByteSlice(int start, int n0, int step0, int n1, int step1) {
        if (data == null)
           setCurrentTime(currentTime);
        int veclen = schema.getVeclen();
        byte[] out = new byte[n0 * n1 * veclen];
        int i0, i1, j, k, l0, l1;
        for (i1 = k = 0, l1 = start * veclen; i1 < n1; i1++, l1 += step1 * veclen) {
            for (i0 = 0, l0 = l1; i0 < n0; i0++, l0 += step0 * veclen) {
                for (j = 0; j < veclen; j++, k++) {
                    out[k] = data.getByteValueAtIndex(l0 + j);
                }
            }
        }
        return out;
    }

    @Override
    public float[] get1DSlice(int start, int n, int step) {
        if (data == null)
           setCurrentTime(currentTime);
        int veclen = schema.getVeclen();
        float[] out = new float[n * veclen];
        int i, j, k, l;
        for (i = k = 0, l = start * veclen; i < n; i++, l += step * veclen) {
            for (j = 0; j < veclen; j++, k++) {
                out[k] = (float) (0xFF & data.getByteValueAtIndex(l + j));
            }
        }
        return out;
    }

    @Override
    public float[] getVData(int start) {
        if (data == null)
           setCurrentTime(currentTime);
        int veclen = schema.getVeclen();
        float[] out = new float[veclen];
        int i, j;
        for (i = 0, j = start * veclen; i < veclen; i++, j++) {
            out[i] = (float) (0xFF & data.getByteValueAtIndex(j));
        }
        return out;
    }

    @Override
    public byte[] getBData() {
        if (data == null)
           setCurrentTime(currentTime);
        return data.getByteArray();
    }

    public byte[] getBSubData(int offset, int length) {
        if (data == null)
           setCurrentTime(currentTime);
        return data.getByteSubArray(offset, length);
    }

    @Override
    public short[] getSData() {
        if (data == null)
           setCurrentTime(currentTime);
        return data.getShortArray();
    }

    public short[] getSSubData(int offset, int length) {
        if (data == null)
           setCurrentTime(currentTime);
        return data.getShortSubArray(offset, length);
    }

    @Override
    public int[] getIData() {
        if (data == null)
           setCurrentTime(currentTime);
        return data.getIntArray();
    }

    public int[] getISubData(int offset, int length) {
        if (data == null)
           setCurrentTime(currentTime);
        return data.getIntSubArray(offset, length);
    }

    @Override
    public float[] getFData() {
        if (data == null)
           setCurrentTime(currentTime);
        return data.getFloatArray();
    }

    @Override
    public float[] getNormFData() {
        if (data == null)
           setCurrentTime(currentTime);
        return data.getFloatArray();
    }

    public float[] getFSubData(int offset, int length) {
        if (data == null)
           setCurrentTime(currentTime);
        return data.getFloatSubArray(offset, length);
    }

    @Override
    public double[] getDData() {
        if (data == null)
           setCurrentTime(currentTime);
        return data.getDoubleArray();
    }

    public double[] getDSubData(int offset, int length) {
        if (data == null)
           setCurrentTime(currentTime);
        return data.getDoubleSubArray(offset, length);
    }

    @Override
    public float getData(int i) {
        if (data == null)
           setCurrentTime(currentTime);
        return (float) data.getIntValueAtIndex(i);
    }

    public BitArray getBitArray() {
        if (data == null)
           setCurrentTime(currentTime);
        return data;
    }

    public boolean getBooleanValue(int i) {
        if (data == null)
           setCurrentTime(currentTime);
        return data.getValueAtIndex(i);
    }

    public byte getByteValue(int i) {
        if (data == null)
           setCurrentTime(currentTime);
        return data.getByteValueAtIndex(i);
    }

    public int getIntValue(int i) {
        if (data == null)
           setCurrentTime(currentTime);
        return data.getIntValueAtIndex(i);
    }

    @Override
    public void resetData() {
        data = timeData.getData(currentTime);
        timeData.clear();
        timeData.setData(data, 0);
    }

    @Override
    public void addData(Object d, float time) {
        if (d instanceof BitArray) {
            timeData.setData((BitArray) d, time);
            currentTime = time;
            timeData.setCurrentTime(time);
            data = timeData.getData();
            recomputeMinMax();
        }
    }

    @Override
    public final void setCurrentTime(float currentTime) {
        if (currentTime == timeData.getCurrentTime() && data != null) {
            return;
        }
        timeData.setCurrentTime(currentTime);
        data = timeData.getData();
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
      if (tData == null || tData.isEmpty())
         return;
      if (!(tData.get(0) instanceof BitArray) || ((BitArray)(tData.get(0))).getSize() != ndata * getVeclen())
         return;
      abstractTimeData = timeData = tData;
      setCurrentTime(currentTime);
      recomputeMinMax();
    }

    @Override
    public TimeData getTimeData() {
        return timeData;
    }

    @Override
    public Object getData() {
        return getBData();
    }

    @Override
    public final void recomputeMinMax() {
        setMinv(0);
        setMaxv(1);
        setPhysMin(0);
        setPhysMax(1);
        hash = RabinHashFunction.hash(data.getByteArray());
    }

    @Override
    public void recomputeMinMax(boolean[] mask) {
        recomputeMinMax();
    }
    
    @Override
    public void recomputeMinMax(TimeData<boolean[]> timeMask) {
        recomputeMinMax();
    }
}
