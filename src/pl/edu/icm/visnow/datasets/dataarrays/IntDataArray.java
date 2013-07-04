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

import java.util.Vector;
import pl.edu.icm.visnow.datasets.TimeData;
import static pl.edu.icm.visnow.lib.utils.ArrayUtils.*;
import pl.edu.icm.visnow.lib.utils.RabinHashFunction;

/**
 *
 * @author Krzysztof S. Nowinski, University of Warsaw, ICM
 * @author Bartosz Borucki, University of Warsaw, ICM 
 * 
 */
public class IntDataArray extends DataArray
{
   private int[] data;
   private TimeData<int[]> timeData = new TimeData<int[]>();

   /**
    * Creates a new <code>IntDataArray</code> object.
    *
    * @param	schema	This data array schema.
    * @param	ndata	number of data elements.
    */
   public IntDataArray(DataArraySchema schema, int ndata)
   {
      super(schema, ndata);
      setMinv(1.e10f);
      setMaxv(-1.e10f);
      setPhysMin(1.e10f);
      setPhysMax(-1.e10f);
      timeData.clear();
      abstractTimeData = timeData;
   }

   /** Creates a new instance of IntDataArray */
   public IntDataArray(int ndata, int veclen)
   {
      super(FIELD_DATA_INT,ndata,veclen);
      setMinv(1.e10f);
      setMaxv(-1.e10f);
      setPhysMin(1.e10f);
      setPhysMax(-1.e10f);
      timeData.clear();
      abstractTimeData = timeData;
   }

   public IntDataArray(int ndata, int veclen, String name, String units, String[] userData)
   {
      super(FIELD_DATA_INT,ndata,veclen,name, units, userData);
      setMinv(1.e10f);
      setMaxv(-1.e10f);
      setPhysMin(1.e10f);
      setPhysMax(-1.e10f);
      timeData.clear();
      abstractTimeData = timeData;
   }

   public IntDataArray(int[] data, int veclen, String name, String units, String[] userData)
   {
      super(FIELD_DATA_INT,data.length/veclen,veclen,name, units, userData);
      this.data = data;
      timeData.clear();
      timeData.add(data);
      abstractTimeData = timeData;
      recomputeMinMax();
   }

   public IntDataArray(TimeData<int[]> data, int veclen, String name, String units, String[] userData)
   {
      super(FIELD_DATA_INT,data.get(0).length/veclen, veclen,name, units, userData);
      timeData = data;
      abstractTimeData = timeData;
      recomputeMinMax();
   }
   
   public IntDataArray(int ndata, int veclen, String name)
   {
      super(FIELD_DATA_INT,ndata,veclen,name, "", null);
      data = new int[ndata*veclen];
      setMinv(1.e10f);
      setMaxv(-1.e10f);
      setPhysMin(1.e10f);
      setPhysMax(-1.e10f);
      timeData.clear();
      timeData.add(data);
      abstractTimeData = timeData;
   }

   public IntDataArray(int[] data, int veclen, String name)
   {
      super(FIELD_DATA_INT,data.length/veclen,veclen,name, "", null);
      this.data = data;
      timeData.clear();
      timeData.add(data);
      abstractTimeData = timeData;
      recomputeMinMax();
   }
   
    @Override
   public void resetData()
   {
      data = timeData.get(currentFrame);
      timeData.clear();
      timeData.add(data);
   }
   
    @Override
   public void addData(Object d, float time)
   {
      if (d instanceof int[])
      {
         timeData.setData((int[])d, time);
         currentTime = time;
         recomputeMinMax();
         timeData.setCurrentTime(time);
         data = timeData.getData();
      }
   }

   public int[] getNewTimestepData()
   {
      if (invalid)
      {
         invalid = false;
         return data;
      }
      int[] dta = new int[ndata * schema.veclen];
      timeData.add(dta);
      return dta;
   }
      
   @Override
   public void setCurrentFrame(int currentFrame)
   {
      //currentFrame = Math.max(0, Math.min(currentFrame, timeData.size()) - 1);
      currentFrame = Math.max(0, Math.min(currentFrame, timeData.size()));
      //data = timeData.get(currentFrame);
      data = timeData.getData(timeData.getTime(currentFrame));
      this.currentFrame = currentFrame;
   }
   
   @Override
   public void setCurrentTime(float currentTime)
   {
      if (currentTime == timeData.getCurrentTime() && data != null)
         return;
      timeData.setCurrentTime(currentTime);
      data = timeData.getData();
   }
   

    @Override
   public final void recomputeMinMax()
   {
      float minv = Float.MAX_VALUE;
      float maxv = -Float.MAX_VALUE;
      for (int step = 0; step < timeData.size(); step ++)
      {
         int[] dta = timeData.get(step);
         int vlen = getVeclen();
         if (vlen == 1)
         {
            for (int i = 0; i < dta.length; i++)
            {
               if (dta[i] < minv)
                  minv = dta[i];
               if (dta[i] > maxv)
                  maxv = dta[i];
            }
            setMinv(minv);
            setMaxv(maxv);
            setPhysMin(minv);
            setPhysMax(maxv);
         } else
         {
            for (int i = 0; i < dta.length; i += vlen)
            {
               float v = 0;
               for (int j = 0; j < vlen; j++)
                  v += dta[i + j] * dta[i + j];
               if (v > maxv)
                  maxv = v;
            }
            maxv = (float) Math.sqrt(maxv);
            setMinv(0);
            setMaxv(maxv);
            setPhysMin(0);
            setPhysMax(maxv);
         }
         hash = RabinHashFunction.hash(dta);
      }
   }

    @Override
   public final void recomputeMinMax(boolean[] mask)
   {
      float minv = Float.MAX_VALUE;
      float maxv = -Float.MAX_VALUE;
      for (int step = 0; step < timeData.size(); step ++)
      {
         int[] dta = timeData.get(step);
         int vlen = getVeclen();
         if (vlen == 1)
         {
            for (int i = 0; i < dta.length; i++)
            {
               if (!mask[i])
                  continue;
               if (dta[i] < minv)
                  minv = dta[i];
               if (dta[i] > maxv)
                  maxv = dta[i];
            }
            setMinv(minv);
            setMaxv(maxv);
            setPhysMin(minv);
            setPhysMax(maxv);
         } else
         {
            for (int i = 0, m = 0; i < dta.length; i += vlen, m++)
            {
               if (!mask[m])
                  continue;
               float v = 0;
               for (int j = 0; j < vlen; j++)
                  v += dta[i + j] * dta[i + j];
               if (v > maxv)
                  maxv = v;
            }
            maxv = (float) Math.sqrt(maxv);
            setMinv(0);
            setMaxv(maxv);
            setPhysMin(0);
            setPhysMax(maxv);
         }
         hash = RabinHashFunction.hash(dta);
      }
   }
   
    @Override
   public IntDataArray clone(String newName)
   {
      IntDataArray da = new IntDataArray(timeData, schema.getVeclen(), newName, schema.getUnit(), schema.getUserData());
      da.setCurrentTime(currentTime);
      return da;
   }

    @Override
   public IntDataArray cloneDeep(String newName)
   {
       IntDataArray da;
       if(schema.getUserData() != null)
            da = new IntDataArray((TimeData<int[]>)timeData.clone(), schema.getVeclen(), newName, schema.getUnit(), schema.getUserData().clone());
       else
            da = new IntDataArray((TimeData<int[]>)timeData.clone(), schema.getVeclen(), newName, schema.getUnit(), null);
       da.setCurrentTime(currentTime);
       return da;
   }

   @Override
   public float[] get2DSlice(int start, int n0, int step0, int n1, int step1)
   {
      if (data == null)
         setCurrentTime(currentTime);
      int veclen = schema.getVeclen();
      float[] out = new float[n0*n1*veclen];
      int i0,i1,j,k,l0,l1;
      for (i1=k=0,l1=start*veclen;i1<n1; i1++,l1+=step1*veclen)
         for (i0=0,l0=l1;i0<n0; i0++, l0+=step0*veclen)
            for (j=0;j<veclen;j++,k++)
               out[k]=(float)data[l0+j];
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
               d += data[l0+j] *  data[l0+j];
            out[k] = (float)Math.sqrt(d);
         }
      return out;
   }

   public int[] get2DIntSlice(int start, int n0, int step0, int n1, int step1)
   {
      if (data == null)
         setCurrentTime(currentTime);
      int veclen = schema.getVeclen();
      int[] out = new int[n0*n1*veclen];
      int i0,i1,j,k,l0,l1;
      for (i1=k=0,l1=start*veclen;i1<n1; i1++,l1+=step1*veclen)
         for (i0=0,l0=l1;i0<n0; i0++, l0+=step0*veclen)
            for (j=0;j<veclen;j++,k++)
               out[k]=data[l0+j];
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
         for (int j = 0; j < veclen; j++, k++)
            out[k] = (float) data[l + j];
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
         out[i] = (float) data[j];
      return out;
   }
   
   @Override
   public byte[] getBData()
   {
      if (data == null)
         setCurrentTime(currentTime);
      return convertToByteArray(data, false, 0, 1);
   }

   @Override
   public short[] getSData()
   {
      if (data == null)
         setCurrentTime(currentTime);
      return convertToShortArray(data, false, 0, 1);
   }

   @Override
   public int[] getIData()
   {
      if (data == null)
         setCurrentTime(currentTime);
      return data;
   }

   @Override
   public float[]  getFData()
   {
      if (data == null)
         setCurrentTime(currentTime);
      return convertToFloatArray(data);
   }
   
   @Override
   public double[] getDData()
   {
      if (data == null)
         setCurrentTime(currentTime);
      return convertToDoubleArray(data);
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
            d += data[k] * data[k];
         outData[i] = (float)Math.sqrt(d);
      }
      return outData;
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

      return timeData.produceData(time, getType(), getVeclen() * ndata);
   }
   
   public int[] getData(float time)
   {
      return timeData.getData(time);
   }
    @Override
   public float[]  produceFData(float time)
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
      return (float)data[i];
   }
      
    @Override
   public void setTimeData(TimeData tData)
   {
      if (tData == null || tData.isEmpty())
         return;
      if (!(tData.get(0) instanceof int[]) || ((int[])(tData.get(0))).length != ndata * getVeclen())
         return;
      abstractTimeData = timeData = tData;
      recomputeMinMax();
      setCurrentTime(currentTime);
   }

    @Override
   public TimeData<int[]> getTimeData()
   {
      return timeData;
   }

    @Override
    public Object getData() {
        return getIData();
    }

    @Override
    public void recomputeMinMax(TimeData<boolean[]> timeMask) {
      float minv = Float.MAX_VALUE;
      float maxv = -Float.MAX_VALUE;
      Vector<Float> timeline = timeData.getTimeSeries();
      for (int step = 0; step < timeline.size(); step++) {
         int[] dta = timeData.getData(timeline.get(step));
         boolean[] mask = timeMask.getData(timeline.get(step));
         int vlen = getVeclen();
         if (vlen == 1)
         {
            for (int i = 0; i < dta.length; i++)
            {
               if (!mask[i])
                  continue;
               if (dta[i] < minv)
                  minv = dta[i];
               if (dta[i] > maxv)
                  maxv = dta[i];
            }
            setMinv(minv);
            setMaxv(maxv);
            setPhysMin(minv);
            setPhysMax(maxv);
         } else
         {
            for (int i = 0, m = 0; i < dta.length; i += vlen, m++)
            {
               if (!mask[m])
                  continue;
               float v = 0;
               for (int j = 0; j < vlen; j++)
                  v += dta[i + j] * dta[i + j];
               if (v > maxv)
                  maxv = v;
            }
            maxv = (float) Math.sqrt(maxv);
            setMinv(0);
            setMaxv(maxv);
            setPhysMin(0);
            setPhysMax(maxv);
         }
         hash = RabinHashFunction.hash(dta);
      }
    }
   
}
