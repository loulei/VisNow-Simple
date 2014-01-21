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
import pl.edu.icm.visnow.datasets.VNObject;

/**
 *
 * @author babor
 */
public class ObjectDataArray<T extends VNObject> extends DataArray
{

   /**
    * an array holding real part of data
    */
   private T[] data;
   private TimeData<T[]> timeData = new TimeData<T[]>();

   /**
     * Creates a new <code>StringDataArray</code> object.
     *
     * @param	schema	This data array schema.
     * @param	ndata	number of data elements.
     */
   @SuppressWarnings({"unchecked"})
   public ObjectDataArray(DataArraySchema schema, int ndata)
   {
      super(schema, ndata);
      timeData.clear();
      abstractTimeData = timeData;
      schema.setMinv(0.0f);
      schema.setMaxv(1.0f);
      recomputeMinMax();
   }

   /** Creates a new instance of ComplexDataArray */
   @SuppressWarnings({"unchecked"})
   public ObjectDataArray(int ndata, int veclen)
   {
      super(FIELD_DATA_OBJECT, ndata, veclen);
      timeData.clear();
      abstractTimeData = timeData;
      schema.setMinv(0.0f);
      schema.setMaxv(1.0f);
      recomputeMinMax();
   }

   @SuppressWarnings({"unchecked"})
   public ObjectDataArray(int ndata, int veclen, String name)
   {
      super(FIELD_DATA_OBJECT, ndata, veclen, name, "", null);
      timeData.clear();
      abstractTimeData = timeData;
      schema.setMinv(0.0f);
      schema.setMaxv(1.0f);
      recomputeMinMax();
   }

   @SuppressWarnings({"unchecked"})
   public ObjectDataArray(int ndata, int veclen, String name, String units, String[] userData)
   {
      super(FIELD_DATA_OBJECT, ndata, veclen, name, units, userData);
      data = (T[]) (new Object[ndata * veclen]);
      timeData.clear();
      timeData.add(data);
      abstractTimeData = timeData;
      schema.setMinv(0.0f);
      schema.setMaxv(1.0f);
      recomputeMinMax();
   }

   @SuppressWarnings(
   {
      "unchecked"
   })
   public ObjectDataArray(T[] data, int veclen, String name, String units, String[] userData)
   {
      super(FIELD_DATA_OBJECT, data.length / veclen, veclen, name, units, userData);
      this.data = data;
      timeData.clear();
      timeData.add(data);
      abstractTimeData = timeData;
      schema.setMinv(0.0f);
      schema.setMaxv(0.0f);
      recomputeMinMax();
   }

   @SuppressWarnings(
   {
      "unchecked"
   })
   public ObjectDataArray(T[] data, int veclen, String name)
   {
      super(FIELD_DATA_OBJECT, data.length, veclen, name, "", null);
      this.data = data;
      timeData.clear();
      timeData.add(data);
      abstractTimeData = timeData;
      schema.setMinv(0.0f);
      schema.setMaxv(1.0f);
      recomputeMinMax();
   }
   
   @SuppressWarnings(
   {
      "unchecked"
   })
   public ObjectDataArray(TimeData<T[]> tData, int veclen, String name, String units, String[] userData)
   {
        super(FIELD_DATA_OBJECT, (tData == null || tData.get(0) == null) ? -1 : tData.get(0).length / veclen, veclen, name, units, userData);
        abstractTimeData = timeData = tData;
        setCurrentTime(currentTime);
        recomputeMinMax();
   }
   
   

   @SuppressWarnings(
   {
      "unchecked"
   })
   public ObjectDataArray(T[] data, String name)
   {
      super(FIELD_DATA_OBJECT, data.length, 1, name, "", null);
      this.data = data;
      timeData.clear();
      timeData.add(data);
      abstractTimeData = timeData;
      schema.setMinv(0.0f);
      schema.setMaxv(1.0f);
      recomputeMinMax();
   }

   @SuppressWarnings(
   {
      "unchecked"
   })
    @Override
   public ObjectDataArray clone(String newName)
   {
      ObjectDataArray da = new ObjectDataArray(timeData, schema.getVeclen(), newName, schema.getUnit(), schema.getUserData());
      da.setCurrentTime(currentTime);
      return da;
   }

   @SuppressWarnings(
   {
      "unchecked"
   })
    @Override
   public ObjectDataArray cloneDeep(String newName)
   {
       ObjectDataArray da;
       if(schema.getUserData() != null)
            da = new ObjectDataArray((TimeData<T[]>)timeData.clone(), schema.getVeclen(), newName, schema.getUnit(), schema.getUserData().clone());
       else
            da = new ObjectDataArray((TimeData<T[]>)timeData.clone(), schema.getVeclen(), newName, schema.getUnit(), null);
       da.setCurrentTime(currentTime);
       return da;
   }

   @Override
   public float[] get2DSlice(int start, int n0, int step0, int n1, int step1)
   {
      if (data == null)
         setCurrentTime(currentTime);
      int veclen = schema.getVeclen();
      float[] out = new float[n0 * n1 * veclen];
      int i0, i1, j, k, l0, l1;
      for (i1 = k = 0, l1 = start * veclen; i1 < n1; i1++, l1 += step1 * veclen)
      {
         for (i0 = 0, l0 = l1; i0 < n0; i0++, l0 += step0 * veclen)
         {
            for (j = 0; j < veclen; j++, k++)
            {
               out[k] = data[l0 + j].toFloat();
            }
         }
      }
      return out;
   }
   
   @Override
   public float[] get2DNormSlice(int start, int n0, int step0, int n1, int step1)
   {
      int veclen = schema.getVeclen();
      if (veclen == 1)
         return get2DSlice(start, n0, step0, n1, step1);
      if (data == null)
         setCurrentTime(currentTime);
      float[] out = new float[n0*n1];
      int i0,i1,j,k,l0,l1;
      for (i1=k=0,l1=start*veclen;i1<n1; i1++,l1+=step1*veclen)
         for (i0=0,l0=l1;i0<n0; i0++, l0+=step0*veclen,k++)
         {
            double d = 0;
            for (j=0;j<veclen;j++)
               d += data[l0+j].toFloat() *  data[l0+j].toFloat();
            out[k] = (float)Math.sqrt(d);
         }
      return out;
   }


   @Override
   public float[] get1DSlice(int start, int n, int step)
   {
      if (data == null)
         setCurrentTime(currentTime);
      int veclen = schema.getVeclen();
      float[] out = new float[n * veclen];
      for (int i = 0, k = 0, l = start * veclen; i < n; i++, l += step * veclen)
      {
         for (int j = 0; j < veclen; j++, k++)
         {
            out[k] = data[l + j].toFloat();
         }
      }
      return out;
   }

    @Override
   public float[] getVData(int start)
   {
      if (data == null)
         setCurrentTime(currentTime);
      int veclen = schema.getVeclen();
      float[] out = new float[veclen];
      for (int i = 0, j = start * veclen; i < veclen; i++, j++)
      {
         out[i] = data[j].toFloat();
      }
      return out;
   }

    @Override
   public byte[] getBData()
   {
      if (data == null)
         setCurrentTime(currentTime);
      byte[] v = new byte[data.length];
      for (int i = 0; i < v.length; i++)
      {
         v[i] = (byte) data[i].toFloat();
      }
      return v;
   }

    @Override
   public short[] getSData()
   {
      if (data == null)
         setCurrentTime(currentTime);
      short[] v = new short[data.length];
      for (int i = 0; i < v.length; i++)
      {
         v[i] = (short) data[i].toFloat();
      }
      return v;
   }

    @Override
   public int[] getIData()
   {
      if (data == null)
         setCurrentTime(currentTime);
      int[] v = new int[data.length];
      for (int i = 0; i < v.length; i++)
      {
         v[i] = (int) data[i].toFloat();
      }
      return v;
   }

    @Override
   public float[] getFData()
   {
      if (data == null)
         setCurrentTime(currentTime);
      float[] v = new float[data.length];
      for (int i = 0; i < v.length; i++)
      {
         v[i] = data[i].toFloat();
      }
      return v;
   }
      
   @Override
   public float[] getNormFData()
   {
      int veclen = schema.getVeclen();
      if (veclen == 1)
         return getFData();
      if (data == null)
         setCurrentTime(currentTime);
      float[] outData = new float[data.length / veclen];
      for (int i = 0, k = 0; i < outData.length; i++)
      {
         double d = 0;
         for (int j = 0; j < veclen; j++, k++)
            d += data[k].toFloat() * data[k].toFloat();
         outData[i] = (float)Math.sqrt(d);
      }
      return outData;
   }

    @Override
   public double[] getDData()
   {
      if (data == null)
         setCurrentTime(currentTime);
      double[] v = new double[data.length];
      for (int i = 0; i < v.length; i++)
      {
         v[i] = data[i].toFloat();
      }
      return v;
   }

   public String[] getStringData()
   {
      if (data == null)
         setCurrentTime(currentTime);
      String[] out = new String[data.length];
      for (int i = 0; i < out.length; i++)
      {
         out[i] = data[i].toString();
      }
      return out;
   }
   
   public T[] getObjData()
   {
      if (data == null)
         setCurrentTime(currentTime);
      return data;
   }

   public T getObjData(int i)
   {
      if (data == null)
         setCurrentTime(0);
      if (data != null && data.length > 0)
      {
         return data[i];
      }
      return null;
   }

   @Override
   public float getData(int i)
   {
      if (data == null)
         setCurrentTime(currentTime);
      return data[i].toFloat();
   }
   @Override
   public void resetData()
   {
      data = timeData.getData(currentTime);
      timeData.clear();
      timeData.setData(data, 0);
   }

   @Override
   public void addData(Object d, float time)
   {
      if (d instanceof VNObject[])
      {
         timeData.setData((T[])d, time);
         currentTime = time;
         timeData.setCurrentTime(time);
         data = timeData.getData();
         recomputeMinMax();
      }
   }

   @Override
   public final void setCurrentTime(float currentTime)
   {
      if (currentTime == timeData.getCurrentTime() && data != null)
         return;
      timeData.setCurrentTime(currentTime);
      data = timeData.getData();
   }
 
   @Override
   public byte[] produceBData(float time)
   {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public short[] produceSData(float time)
   {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public int[] produceIData(float time)
   {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public float[] produceFData(float time)
   {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public double[] produceDData(float time)
   {
      throw new UnsupportedOperationException("Not supported yet.");
   }
   
    @Override
   public void setTimeData(TimeData tData)
   {
      if (tData == null || tData.isEmpty())
         return;
      if (!(tData.get(0) instanceof VNObject[]) || ((VNObject[])(tData.get(0))).length != ndata * getVeclen())
         return;
      abstractTimeData = timeData = tData;
      setCurrentTime(currentTime);
      recomputeMinMax();
   }
   
    @Override
   public TimeData getTimeData()
   {
      return timeData;
   }
   
    @Override
    public Object getData() {
        return getObjData();
    }

    @Override
    public final void recomputeMinMax() {
        float minv = Float.MAX_VALUE;
        float maxv = -Float.MAX_VALUE;
        float vv, v;
        for (int step = 0; step < timeData.size(); step++) {
            T[] dta = timeData.get(step);
            int vlen = getVeclen();
            if (vlen == 1) {
                for (int i = 0; i < dta.length; i++) {
                    v = dta[i].toFloat();
                    if (v < minv) {
                        minv = v;
                    }
                    if (v > maxv) {
                        maxv = v;
                    }
                }
            } else {
                for (int i = 0; i < dta.length; i += vlen) {
                    vv = 0;
                    for (int j = 0; j < vlen; j++) {
                        v = dta[i + j].toFloat();
                        vv += v * v;
                    }
                    vv = (float) Math.sqrt(vv);
                    if (vv > maxv) {
                        maxv = vv;
                    }
                    if (vv < minv) {
                        minv = vv;
                    }
                }                
            }
        }
        recomputePhysMinMax(minv, maxv);
        setMinv(minv);
        setMaxv(maxv);        
    }

    @Override
   public final void recomputeMinMax(boolean[] mask)
   {
        float minv = Float.MAX_VALUE;
        float maxv = -Float.MAX_VALUE;
        float vv, v;
        for (int step = 0; step < timeData.size(); step++) {
            T[] dta = timeData.get(step);
            int vlen = getVeclen();
            if (vlen == 1) {
                for (int i = 0; i < dta.length; i++) {
                    if (!mask[i]) {
                        continue;
                    }
                    v = dta[i].toFloat();
                    if (v < minv) {
                        minv = v;
                    }
                    if (v > maxv) {
                        maxv = v;
                    }
                }
            } else {
                for (int i = 0, m = 0; i < dta.length; i += vlen, m++) {
                    if (!mask[m]) {
                        continue;
                    }
                    vv = 0;
                    for (int j = 0; j < vlen; j++) {
                        v = dta[i + j].toFloat();
                        vv += v * v;
                    }
                    vv = (float) Math.sqrt(vv);
                    if (vv > maxv) {
                        maxv = vv;
                    }
                    if (vv < minv) {
                        minv = vv;
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
        float minv = Float.MAX_VALUE;
        float maxv = -Float.MAX_VALUE;
        float vv, v;
        ArrayList<Float> timeline = timeData.getTimeSeries();
        for (int step = 0; step < timeline.size(); step++) {
            T[] dta = timeData.get(step);
            boolean[] mask = timeMask.getData(timeline.get(step));
            int vlen = getVeclen();
            if (vlen == 1) {
                for (int i = 0; i < dta.length; i++) {
                    if (!mask[i]) {
                        continue;
                    }
                    v = dta[i].toFloat();
                    if (v < minv) {
                        minv = v;
                    }
                    if (v > maxv) {
                        maxv = v;
                    }
                }
            } else {
                for (int i = 0, m = 0; i < dta.length; i += vlen, m++) {
                    if (!mask[m]) {
                        continue;
                    }
                    vv = 0;
                    for (int j = 0; j < vlen; j++) {
                        v = dta[i + j].toFloat();
                        vv += v * v;
                    }
                    vv = (float) Math.sqrt(vv);
                    if (vv > maxv) {
                        maxv = vv;
                    }
                    if (vv < minv) {
                        minv = vv;
                    }
                }                
            }
        }
        recomputePhysMinMax(minv, maxv);
        setMinv(minv);
        setMaxv(maxv);        
    }
    
}
