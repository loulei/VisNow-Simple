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
 * @author Krzysztof S. Nowinski, University of Warsaw, ICM
 * @author Bartosz Borucki, University of Warsaw, ICM 
 *
 */
public class BooleanDataArray extends DataArray
{

   private boolean[] data;
   private TimeData<boolean[]> timeData = new TimeData<boolean[]>();

   /**
    * Creates a new
    * <code>booleanDataArray</code> object.
    *
    * @param	schema	This data array schema.
    * @param	ndata	number of data elements.
    */
   public BooleanDataArray(DataArraySchema schema, int ndata)
   {
      super(schema, ndata);
      setMinv(0.f);
      setMaxv(255.f);
      timeData.clear();
      abstractTimeData = timeData;
   }

   /**
    * Creates a new instance of booleanDataArray
    *
    * @param ndata number of data elements in the booleanDataArray
    * @param veclen vector length (1 for scalar data)
    */
   public BooleanDataArray(int ndata, int veclen)
   {
      super(FIELD_DATA_BOOLEAN, ndata, veclen);
      setMinv(0.f);
      setMaxv(255.f);
      timeData.clear();
      abstractTimeData = timeData;
   }

   /**
    * Creates a new instance of booleanDataArray
    *
    * @param ndata number of data elements in the booleanDataArray
    * @param veclen vector length (1 for scalar data)
    * @param name data array name
    */
   public BooleanDataArray(int ndata, int veclen, String name)
   {
      super(FIELD_DATA_BOOLEAN, ndata, veclen, name, "", null);
      setMinv(0);
      setMaxv(255);
      setPhysMin(0);
      setPhysMax(255);
      timeData.clear();
      abstractTimeData = timeData;
   }

   /**
    * Creates a new instance of booleanDataArray
    *
    * @param data boolean array to be included in the generated booleanDataArray
    * object
    * @param veclen vector length (1 for scalar data)
    * @param name data array name
    */
   public BooleanDataArray(boolean[] data, int veclen, String name, String units, String[] userData)
   {
      super(FIELD_DATA_BOOLEAN, data.length / veclen, veclen, name, units, userData);
      this.data = data;
      timeData.clear();
      timeData.add(data);
      abstractTimeData = timeData;
      recomputeMinMax();
   }

   public BooleanDataArray(TimeData<boolean[]> tData, int veclen, String name, String units, String[] userData)
   {
        super(FIELD_DATA_BOOLEAN, (tData == null || tData.get(0) == null) ? -1 : tData.get(0).length / veclen, veclen, name, units, userData);
        abstractTimeData = timeData = tData;
        setCurrentTime(currentTime);
        recomputeMinMax();
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
      if (d instanceof boolean[])
      {
         timeData.setData((boolean[]) d, time);
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
   public BooleanDataArray clone(String newName)
   {
      BooleanDataArray da = new BooleanDataArray(timeData, schema.getVeclen(), newName, schema.getUnit(), schema.getUserData());
      da.setCurrentTime(currentTime);
      return da;
   }

    @Override
   public BooleanDataArray cloneDeep(String newName)
   {
      BooleanDataArray da;
      if (schema.getUserData() != null)
         da = new BooleanDataArray((TimeData<boolean[]>) timeData.clone(), schema.getVeclen(), newName, schema.getUnit(), schema.getUserData().clone());
      else
         da = new BooleanDataArray((TimeData<boolean[]>) timeData.clone(), schema.getVeclen(), newName, schema.getUnit(), null);
      da.setCurrentTime(currentTime);
      return da;
   }

   @Override
   public float[] get2DSlice(int start, int n0, int step0, int n1, int step1)
   {
      int veclen = schema.getVeclen();
      float[] out = new float[n0 * n1 * veclen];
      int i0, i1, j, k, l0, l1;
      for (i1 = k = 0, l1 = start * veclen; i1 < n1; i1++, l1 += step1 * veclen)
         for (i0 = 0, l0 = l1; i0 < n0; i0++, l0 += step0 * veclen)
            for (j = 0; j < veclen; j++, k++)
               out[k] = data[l0 + j] ? 1 : 0;
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
            out[k] = 0;
            for (j=0;j<veclen;j++)
               if (data[l0+j])
               {
                  out[k] = 1;
                  break;
               }
         }
      return out;
   }


   public boolean[] get2DbooleanSlice(int start, int n0, int step0, int n1, int step1)
   {
      int veclen = schema.getVeclen();
      boolean[] out = new boolean[n0 * n1 * veclen];
      int i0, i1, j, k, l0, l1;
      for (i1 = k = 0, l1 = start * veclen; i1 < n1; i1++, l1 += step1 * veclen)
         for (i0 = 0, l0 = l1; i0 < n0; i0++, l0 += step0 * veclen)
            for (j = 0; j < veclen; j++, k++)
               out[k] = data[l0 + j];
      return out;
   }

   @Override
   public float[] get1DSlice(int start, int n, int step)
   {
      int veclen = schema.getVeclen();
      float[] out = new float[n * veclen];
      int i, j, k, l;
      for (i = k = 0, l = start * veclen; i < n; i++, l += step * veclen)
         for (j = 0; j < veclen; j++, k++)
            out[k] = data[l + j] ? 1 : 0;
      return out;
   }

   @Override
   public float[] getVData(int start)
   {
      int veclen = schema.getVeclen();
      float[] out = new float[veclen];
      int i, j;
      for (i = 0, j = start * veclen; i < veclen; i++, j++)
         out[i] = data[j] ? 1 : 0;
      return out;
   }

   public boolean[] getBoolData()
   {
      return data;
   }

   @Override
   public byte[] getBData()
   {
      byte[] outData = new byte[data.length];
      for (int i = 0; i < outData.length; i++)
         outData[i] = data[i] ? (byte) 1 : (byte) 0;
      return outData;
   }

   @Override
   public short[] getSData()
   {
      short[] outData = new short[data.length];
      for (int i = 0; i < data.length; i++)
         outData[i] = data[i] ? (short) 1 : (short) 0;
      return outData;
   }

   @Override
   public int[] getIData()
   {
      int[] outData = new int[data.length];
      for (int i = 0; i < data.length; i++)
         outData[i] = data[i] ? 1 : 0;
      return outData;
   }

   @Override
   public float[] getFData()
   {
      float[] outData = new float[data.length];
      for (int i = 0; i < data.length; i++)
         outData[i] = data[i] ? 1 : 0;
      return outData;
   }

   @Override
   public float[] getNormFData()
   {
      int veclen = schema.getVeclen();
      if (veclen == 1)
         return getFData();
      float[] outData = new float[data.length / veclen];
      for (int i = 0, k = 0; i < outData.length; i++)
      {
         for (int j = 0; j < veclen; j++, k++)
            if (data[k])
               outData[i] = 1;
      }
      return outData;
   }
   
   @Override
   public double[] getDData()
   {
      double[] outData = new double[data.length];
      for (int i = 0; i < data.length; i++)
         outData[i] = data[i] ? 1 : 0;
      return outData;
   }

   public boolean[] produceBoolData(float time)
   {
      return timeData.produceData(time, getType(), getVeclen() * ndata);
   }
   
   public boolean[] getData(float time)
   {
      return timeData.getData(time);
   }
   
    @Override
   public byte[] produceBData(float time)
   {
      return null;
   }

    @Override
   public short[] produceSData(float time)
   {
      return null;
   }

    @Override
   public int[] produceIData(float time)
   {
      return null;
   }

    @Override
   public float[] produceFData(float time)
   {
      return null;
   }

    @Override
   public double[] produceDData(float time)
   {
      return null;
   }

    @Override
   public float getData(int i)
   {
      return data[i] ? 1 : 0;
   }
   
    @Override
   public void setTimeData(TimeData tData)
   {
      if (tData == null || tData.isEmpty())
         return;
      if (!(tData.get(0) instanceof boolean[]) || ((boolean[])(tData.get(0))).length != ndata * getVeclen())
         return;
      abstractTimeData = timeData = tData;
      setCurrentTime(currentTime);
      recomputeMinMax();
   }
      
    @Override
   public TimeData<boolean[]> getTimeData()
   {
      return timeData;
   }

    @Override
    public Object getData() {
        return getBoolData();
    }

    @Override
   public final void recomputeMinMax()
   {
      setMinv(0);
      setMaxv(1);
      setPhysMin(0);
      setPhysMax(1);
   }

    @Override
   public final void recomputeMinMax(boolean[] mask)
   {
       recomputeMinMax();
   }

    @Override
    public void recomputeMinMax(TimeData<boolean[]> timeMask) 
    {
        recomputeMinMax();
    }
    
   
}
