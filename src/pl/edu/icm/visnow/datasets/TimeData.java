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

package pl.edu.icm.visnow.datasets;

import java.io.Serializable;
import java.util.ArrayList;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import static pl.edu.icm.visnow.lib.utils.ArrayUtils.*;
import static pl.edu.icm.visnow.lib.utils.CropDown.*;
import pl.edu.icm.visnow.lib.utils.numeric.FloatingPointUtils;

/**
 * 
 * Holds a series of data for various moments of time. T is expected to be array class of objects supported by 
 * DataArray. Time interpolation is supported in the case of basic numerical type arrays. Note: array size consistency is not checked
 * (T is a general template).
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class TimeData<T> implements Cloneable, Serializable
{
   
   public static enum Position {BEFORE, IN, AFTER, EMPTY};
   private int type;
   private ArrayList<Float> timeSeries = new ArrayList<Float>();
   private ArrayList<T>     dataSeries = new ArrayList<T>();
   private float currentTime = 0;
   private T data;
   
   public TimeData()
   {
   }
   
   public TimeData(ArrayList<Float> timeSeries, ArrayList<T> dataSeries, float currentTime)
   {
      this.timeSeries  = timeSeries;
      this.dataSeries  = dataSeries;
      this.currentTime = currentTime;
      if (dataSeries != null && !dataSeries.isEmpty())
      {
         T d0 = dataSeries.get(0);
         if (d0 instanceof double[])
            type = DataArray.FIELD_DATA_DOUBLE;
         else if (d0 instanceof float[])
            type = DataArray.FIELD_DATA_FLOAT;
         else if (d0 instanceof int[])
            type = DataArray.FIELD_DATA_INT;
         else if (d0 instanceof short[])
            type = DataArray.FIELD_DATA_SHORT;
         else if (d0 instanceof byte[])   
            type = DataArray.FIELD_DATA_BYTE;
         else if (d0 instanceof boolean[])   
            type = DataArray.FIELD_DATA_BOOLEAN;
      }
   }
   
   @Override
   public TimeData clone()
   {
      return new TimeData<T>(timeSeries, dataSeries, currentTime);
   }
   /**
    * time series compatibility check
    * @param t TimeData to compare with
    * @return true if time series vectors are identical, false if differ in length or content
    */
   public boolean isTimeCompatible(TimeData t)
   {
      if (t == null)
         return false;
      ArrayList<Float> sMoments = t.getTimeSeries();
      if (sMoments.size() != timeSeries.size())
         return false;
      for (int i = 0; i < timeSeries.size(); i++)
         if (timeSeries.get(i) != sMoments.get(i))
            return false;
      return true;
   }

   /**
    * Set the value of data
    * Sets a new (data, time moment) pair.
    * @param d0 new data
    * @param t new moment
    */
   public void setData(T d0, float t)
   {
      int k = -1;
      if (dataSeries.isEmpty())
      {
         if (d0 instanceof float[])
         {
            type = DataArray.FIELD_DATA_FLOAT;
            if (!FloatingPointUtils.isFinite((float[])d0))
            {
               
            }
         }
         if (d0 instanceof double[])
         {
            type = DataArray.FIELD_DATA_DOUBLE;
            if (!FloatingPointUtils.isFinite((double[])d0))
            {
               
            }
         }
         if (d0 instanceof int[])
            type = DataArray.FIELD_DATA_INT;
         else if (d0 instanceof short[])
            type = DataArray.FIELD_DATA_SHORT;
         else if (d0 instanceof byte[])   
            type = DataArray.FIELD_DATA_BYTE;
         else if (d0 instanceof boolean[])   
            type = DataArray.FIELD_DATA_BOOLEAN;
      }
      for (int i = 0; i < timeSeries.size(); i++)
         if (timeSeries.get(i) >= t)
         {
            k = i;
            break;
         }
 
      if (k == -1)
      {
         timeSeries.add(t);
         dataSeries.add(d0);
      }
      else if (timeSeries.get(k) == t)
         dataSeries.set(k, d0);
      else {
         timeSeries.add(k, t);
         dataSeries.add(k, d0);         
      }
      if (dataSeries.size() == 1 || t == currentTime)
         data = d0;
   }

   public int getType()
   {
      return type;
   }
   
   public int getLenth()
   {
      if (dataSeries.isEmpty())
         return -1;
      Object d0 = dataSeries.get(0);
         if (d0 instanceof double[])
            return ((double[])d0).length;
         else if (d0 instanceof float[])
            return ((float[])d0).length;
         else if (d0 instanceof int[])
            return ((int[])d0).length;
         else if (d0 instanceof short[])
            return ((short[])d0).length;
         else if (d0 instanceof byte[])   
            return ((byte[])d0).length;
         else if (d0 instanceof boolean[])    
            return ((boolean[])d0).length;
         return -1;
   }
   
   /**
    * Checks if a given time moment is within time range
    * @param time
    * @return EMPTY if no timesteps are present, BEFORE if the time parameter is before the earliest stored moment, 
    * IN if time is within the range of available timesteps, AFTER - when after all the stored timesteps
    */
   public Position getTimePosition(float time)
   {
      if (timeSeries.isEmpty())
         return Position.EMPTY;
      if (time < timeSeries.get(0))
         return Position.BEFORE;
      else if (time > timeSeries.get(timeSeries.size()-1))
         return Position.AFTER;
      else
         return Position.IN;
   }
   
   public Position setCurrentFrame(int frame)
   {
      if (timeSeries.isEmpty())
         return Position.EMPTY;
      if (frame < 0)
         return Position.BEFORE;
      if (frame >= timeSeries.size())
         return Position.AFTER;
      currentTime = timeSeries.get(frame);
      data = dataSeries.get(frame);
      return Position.IN;
   }

   /**
    * Set the value of time
    * sets current time, recomputes (selects or interpolates the values of data.
    * 
    * @param time new value of time
    */
   public Position setCurrentTime(float time)
   {
      currentTime = time;
      data = getData(time);
      return getTimePosition(time);
   }

   public float getCurrentTime()
   {
      return currentTime;
   }
   
/**
 * sets the data d as data at the moment 0
 * (this is a default for time independent data)
 * @param d 
 */
   public void add(T d)
   {
      if (timeSeries.isEmpty())
         setData(d, 0);
      else
         setData(d, getEndTime() + 1);
      data = d;
   }

   public boolean isTimestep(float t)
   {
      for (int i = 0; i < timeSeries.size(); i++)
         if (t == timeSeries.get(i))
            return true;
      return false;
   }
   
   public T getData()
   {
      return data;
   }
   
   @SuppressWarnings("unchecked")
   public T produceData(float time, int type, int n)
   {
      for (int i = 0; i < timeSeries.size(); i++)
         if (timeSeries.get(i) == time)
            return dataSeries.get(i);
      T newData = null;
      switch (type)
      {
         case DataArray.FIELD_DATA_BOOLEAN:
            newData = (T)(new boolean[n]);
            break;
         case DataArray.FIELD_DATA_BYTE:
            newData = (T)(new byte[n]);
            break;
         case DataArray.FIELD_DATA_SHORT:
            newData = (T)(new short[n]);
            break;
         case DataArray.FIELD_DATA_INT:
            newData = (T)(new int[n]);
            break;
         case DataArray.FIELD_DATA_FLOAT:
            newData = (T)(new float[n]);
            break;
         case DataArray.FIELD_DATA_DOUBLE:
            newData = (T)(new double[n]);
            break;
         case DataArray.FIELD_DATA_STRING:
            newData = (T)(new String[n]);
            break;
         case DataArray.FIELD_DATA_OBJECT:
            newData = (T)(new VNObject[n]);
            break;
      }
      setData(newData, time);
      return newData;
            
   }
   
   public int getStep(float time)
   {
      if (timeSeries.isEmpty())
         return -1;
      for (int i = 0; i < timeSeries.size(); i++)
         if (timeSeries.get(i) == time)
            return i;
      return -1;
   }
   
   public T getData(float time)
   {
      if (timeSeries.isEmpty())
         return null;
      for (int i = 0; i < timeSeries.size(); i++)
         if (timeSeries.get(i) == time)
            return dataSeries.get(i);
      int k = -1;
      T outData = null;
      for (int i = 0; i < timeSeries.size(); i++)
         if (timeSeries.get(i) >= time)
         {
            k = i;
            break;
         }
      if (k == -1)
         outData = dataSeries.get(dataSeries.size() -  1);
      else if (k == 0 || timeSeries.get(k) == time)
         outData = dataSeries.get(k);
      else
      {
         T d0 = dataSeries.get(k - 1);
         T d1 = dataSeries.get(k);
         float u = (time - timeSeries.get(k - 1)) / (timeSeries.get(k) - timeSeries.get(k - 1));
         if (d0 instanceof double[])
         {
            double[] dd0 = (double[]) d0;
            double[] dd1 = (double[]) d1;
            int n = Math.min(dd0.length, dd1.length);
            double[] outd = new double[n];
            for (int i = 0; i < n; i++)
               outd[i] = u * dd1[i] + (1 - u) * dd0[i];
            outData = (T)outd;
         }
         else if (d0 instanceof float[])
         {
            float[] dd0 = (float[]) d0;
            float[] dd1 = (float[]) d1;
            int n = Math.min(dd0.length, dd1.length);
            float[] outd = new float[n];
            for (int i = 0; i < n; i++)
               outd[i] = u * dd1[i] + (1 - u) * dd0[i];
            outData = (T)outd;
         }
         else if (d0 instanceof int[])
         {
            int[] dd0 = (int[]) d0;
            int[] dd1 = (int[]) d1;
            int n = Math.min(dd0.length, dd1.length);
            int[] outd = new int[n];
            for (int i = 0; i < n; i++)
               outd[i] = (int)(u * dd1[i] + (1 - u) * dd0[i]);
            outData = (T)outd;
         }
         else if (d0 instanceof short[])
         {
            short[] dd0 = (short[]) d0;
            short[] dd1 = (short[]) d1;
            int n = Math.min(dd0.length, dd1.length);
            short[] outd = new short[n];
            for (int i = 0; i < n; i++)
               outd[i] = (short)(u * dd1[i] + (1 - u) * dd0[i]);
            outData = (T)outd;
         }
         else if (d0 instanceof byte[])
         {
            byte[] dd0 = (byte[]) d0;
            byte[] dd1 = (byte[]) d1;
            int n = Math.min(dd0.length, dd1.length);
            byte[] outd = new byte[n];
            for (int i = 0; i < n; i++)
               outd[i] = (byte)(0xff&(int)(u * (0xff&(int)dd1[i]) + (1 - u) * (0xff&(int)dd0[i])));
            outData = (T)outd;
         }
         else if (u < .5)
            outData = d0;
         else
            outData = d1;
      }
      return outData;
   }
   
   public ArrayList<T> getAllData()
   {
      return dataSeries;
   }
   
   public T get(int i)
   {
      if (i < 0 || i >= dataSeries.size())
         return null;
      return dataSeries.get(i);
   }
   
   public int getNSteps()
   {
      return dataSeries.size();
   }
   
   public int size()
   {
      return dataSeries.size();
   }
   
   public boolean isEmpty()
   {
      return dataSeries.isEmpty();
   }
   
   public float getTime(int frame)
   {
      if(frame < 0) 
          frame = 0;
      if(frame > timeSeries.size()-1)
          frame = timeSeries.size()-1;
      
      return timeSeries.get(frame);
   }
     
   public float getStartTime()
   {
      if (timeSeries.isEmpty())
         return Float.MAX_VALUE;
      return timeSeries.get(0);
   }    
   
   public float getEndTime()
   {
      if (timeSeries.isEmpty())
         return -Float.MAX_VALUE;
      return timeSeries.get(timeSeries.size() - 1);
   }
   
   public void clear()
   {
      timeSeries.clear();
      dataSeries.clear();
      data = null;
   }

   public ArrayList<Float> getTimeSeries()
   {
      return timeSeries;
   }

   
   public float[] getTimeline() {
       float[] out = new float[timeSeries.size()];
       for (int i = 0; i < out.length; i++) {
           out[i] = timeSeries.get(i);
       }
       return out;      
   }
   
   public TimeData cropDown(int veclen, int[] dims, int[] low, int[] up, int[] down)
   {
      TimeData<T> td = new TimeData<T>();
      if (dataSeries.isEmpty())
         return td;
      for (int timestep = 0; timestep < dataSeries.size(); timestep++)
      {
         Object d = dataSeries.get(timestep);
         if (d instanceof boolean[])
            td.setData((T)cropDownArray((boolean[])d, veclen, dims, low, up, down), timeSeries.get(timestep));
         else if (d instanceof byte[])
            td.setData((T)cropDownArray((byte[])d, veclen, dims, low, up, down), timeSeries.get(timestep));
         else if (d instanceof short[])
            td.setData((T)cropDownArray((short[])d, veclen, dims, low, up, down), timeSeries.get(timestep));
         else if (d instanceof int[])
            td.setData((T)cropDownArray((int[])d, veclen, dims, low, up, down), timeSeries.get(timestep));
         else if (d instanceof float[])
            td.setData((T)cropDownArray((float[])d, veclen, dims, low, up, down), timeSeries.get(timestep));
         else if (d instanceof double[])
            td.setData((T)cropDownArray((double[])d, veclen, dims, low, up, down), timeSeries.get(timestep));
         else if (d instanceof String[])
            td.setData((T)cropDownArray((String[])d, veclen, dims, low, up, down), timeSeries.get(timestep));
         else if (d instanceof VNObject[])
            td.setData((T)cropDownArray((VNObject[])d, veclen, dims, low, up, down), timeSeries.get(timestep));
      }
      td.setCurrentTime(currentTime);
      return td;
   }
   
   public TimeData<byte[]> convertToByte(boolean normalize, float min, float max)
   {
      TimeData<byte[]> td = new TimeData<byte[]>();
      if (dataSeries.isEmpty())
         return td;
      for (int timestep = 0; timestep < dataSeries.size(); timestep++)
      {
         Object d = dataSeries.get(timestep);
         if (d instanceof boolean[])
            td.setData(convertToByteArray((boolean[])d), timeSeries.get(timestep));
         else if (d instanceof byte[])
            td.setData((byte[])d, timeSeries.get(timestep));
         else if (d instanceof short[])
            td.setData(convertToByteArray((short[])d, normalize, min, max), timeSeries.get(timestep));
         else if (d instanceof int[])
            td.setData(convertToByteArray((int[])d, normalize, min, max), timeSeries.get(timestep));
         else if (d instanceof float[])
            td.setData(convertToByteArray((float[])d, normalize, min, max), timeSeries.get(timestep));
         else if (d instanceof double[])
            td.setData(convertToByteArray((double[])d, normalize, min, max), timeSeries.get(timestep));
      }
      td.setCurrentTime(currentTime);
      return td;
   }
   
   public TimeData<short[]> convertToShort(boolean normalize, float min, float max)
   {
      TimeData<short[]> td = new TimeData<short[]>();
      if (dataSeries.isEmpty())
         return td;
      for (int timestep = 0; timestep < dataSeries.size(); timestep++)
      {
         Object d = dataSeries.get(timestep);
         if (d instanceof boolean[])
            td.setData(convertToShortArray((boolean[])d), timeSeries.get(timestep));
         else if (d instanceof byte[])
            td.setData(convertToShortArray((byte[])d), timeSeries.get(timestep));
         else if (d instanceof short[])
            td.setData((short[])d, timeSeries.get(timestep));
         else if (d instanceof int[])
            td.setData(convertToShortArray((int[])d, normalize, min, max), timeSeries.get(timestep));
         else if (d instanceof float[])
            td.setData(convertToShortArray((float[])d, normalize, min, max), timeSeries.get(timestep));
         else if (d instanceof double[])
            td.setData(convertToShortArray((double[])d, normalize, min, max), timeSeries.get(timestep));
      }
      td.setCurrentTime(currentTime);
      return td;
   }
          
   public TimeData<int[]> convertToInt(boolean normalize, float min, float max)
   {
      TimeData<int[]> td = new TimeData<int[]>();
      if (dataSeries.isEmpty())
         return td;
      for (int timestep = 0; timestep < dataSeries.size(); timestep++)
      {
         Object d = dataSeries.get(timestep);
         if (d instanceof boolean[])
            td.setData(convertToIntArray((boolean[])d), timeSeries.get(timestep));
         else if (d instanceof byte[])
            td.setData(convertToIntArray((byte[])d), timeSeries.get(timestep));
         else if (d instanceof short[])
            td.setData(convertToIntArray((short[])d), timeSeries.get(timestep));
         else if (d instanceof int[])
            td.setData((int[])d, timeSeries.get(timestep));
         else if (d instanceof float[])
            td.setData(convertToIntArray((float[])d, normalize, min, max), timeSeries.get(timestep));
         else if (d instanceof double[])
            td.setData(convertToIntArray((double[])d, normalize, min, max), timeSeries.get(timestep));
      }
      td.setCurrentTime(currentTime);
      return td;
   }
   
   public TimeData<float[]> convertToFloat(boolean normalize, float min, float max)
   {
      TimeData<float[]> td = new TimeData<float[]>();
      if (dataSeries.isEmpty())
         return td;
      for (int timestep = 0; timestep < dataSeries.size(); timestep++)
      {
         Object d = dataSeries.get(timestep);
         if (d instanceof boolean[])
            td.setData(convertToFloatArray((boolean[])d), timeSeries.get(timestep));
         else if (d instanceof byte[])
            td.setData(convertToFloatArray((byte[])d), timeSeries.get(timestep));
         else if (d instanceof short[])
            td.setData(convertToFloatArray((short[])d), timeSeries.get(timestep));
         else if (d instanceof int[])
            td.setData(convertToFloatArray((int[])d), timeSeries.get(timestep));
         else if (d instanceof float[])
            td.setData((float[])d, timeSeries.get(timestep));
         else if (d instanceof double[])
            td.setData(convertToFloatArray((double[])d), timeSeries.get(timestep));
      }
      td.setCurrentTime(currentTime);
      return td;
   }
   
   public TimeData<double[]> convertToDouble(boolean normalize, float min, float max)
   {
      TimeData<double[]> td = new TimeData<double[]>();
      if (dataSeries.isEmpty())
         return td;
      for (int timestep = 0; timestep < dataSeries.size(); timestep++)
      {
         Object d = dataSeries.get(timestep);
         if (d instanceof boolean[])
            td.setData(convertToDoubleArray((boolean[])d), timeSeries.get(timestep));
         else if (d instanceof byte[])
            td.setData(convertToDoubleArray((byte[])d), timeSeries.get(timestep));
         else if (d instanceof short[])
            td.setData(convertToDoubleArray((short[])d), timeSeries.get(timestep));
         else if (d instanceof int[])
            td.setData(convertToDoubleArray((int[])d), timeSeries.get(timestep));
         else if (d instanceof float[])
            td.setData(convertToDoubleArray((float[])d), timeSeries.get(timestep));
         else if (d instanceof double[])
            td.setData((double[])d, timeSeries.get(timestep));
      }
      td.setCurrentTime(currentTime);
      return td;
   }
   
   public TimeData<T> concatenate(ArrayList<TimeData<T>> concatenatedData)
   {
      TimeData<T> out = new TimeData<T>();
      int n = getLenth();
      for (int i = 0; i < concatenatedData.size(); i++)
      {
         TimeData cD = concatenatedData.get(i);
         if (!isTimeCompatible(cD))
            return null;
         n += cD.getLenth();
      }
      Object d = dataSeries.get(0);
      if (d instanceof boolean[])
      {
         for (int i = 0; i < getNSteps(); i++)
         {
            int k = 0;
            boolean[] outData = new boolean[n];
            System.arraycopy((boolean[])d, 0, outData, k, getLenth());
            k += getLenth();
            for (int j = 0; j < concatenatedData.size(); j++)
            {
               TimeData cD = concatenatedData.get(j);
               System.arraycopy((boolean[])cD.get(i), 0, outData, k, cD.getLenth());
               k += cD.getLenth();
            }
            out.setData((T)outData, getTime(i));
         }
      }
      else if (d instanceof byte[])
      {
         for (int i = 0; i < getNSteps(); i++)
         {
            int k = 0;
            byte[] outData = new byte[n];
            System.arraycopy((byte[])d, 0, outData, k, getLenth());
            k += getLenth();
            for (int j = 0; j < concatenatedData.size(); j++)
            {
               TimeData cD = concatenatedData.get(j);
               System.arraycopy((byte[])cD.get(i), 0, outData, k, cD.getLenth());
               k += cD.getLenth();
            }
            out.setData((T)outData, getTime(i));
         }
      }
      else if (d instanceof short[])
      {
         for (int i = 0; i < getNSteps(); i++)
         {
            int k = 0;
            short[] outData = new short[n];
            System.arraycopy((short[])d, 0, outData, k, getLenth());
            k += getLenth();
            for (int j = 0; j < concatenatedData.size(); j++)
            {
               TimeData cD = concatenatedData.get(j);
               System.arraycopy((short[])cD.get(i), 0, outData, k, cD.getLenth());
               k += cD.getLenth();
            }
            out.setData((T)outData, getTime(i));
         }
      }
      else if (d instanceof int[])
      {
         for (int i = 0; i < getNSteps(); i++)
         {
            int k = 0;
            int[] outData = new int[n];
            System.arraycopy((int[])d, 0, outData, k, getLenth());
            k += getLenth();
            for (int j = 0; j < concatenatedData.size(); j++)
            {
               TimeData cD = concatenatedData.get(j);
               System.arraycopy((int[])cD.get(i), 0, outData, k, cD.getLenth());
               k += cD.getLenth();
            }
            out.setData((T)outData, getTime(i));
         }
      }
      else if (d instanceof float[])
      {
         for (int i = 0; i < getNSteps(); i++)
         {
            int k = 0;
            float[] outData = new float[n];
            System.arraycopy((float[])d, 0, outData, k, getLenth());
            k += getLenth();
            for (int j = 0; j < concatenatedData.size(); j++)
            {
               TimeData cD = concatenatedData.get(j);
               System.arraycopy((float[])cD.get(i), 0, outData, k, cD.getLenth());
               k += cD.getLenth();
            }
            out.setData((T)outData, getTime(i));
         }
      }
      else if (d instanceof double[])
      {
         for (int i = 0; i < getNSteps(); i++)
         {
            int k = 0;
            double[] outData = new double[n];
            System.arraycopy((double[])d, 0, outData, k, getLenth());
            k += getLenth();
            for (int j = 0; j < concatenatedData.size(); j++)
            {
               TimeData cD = concatenatedData.get(j);
               System.arraycopy((double[])cD.get(i), 0, outData, k, cD.getLenth());
               k += cD.getLenth();
            }
            out.setData((T)outData, getTime(i));
         }
      }
      
      return out;
   }
   
   public TimeData<T> get2DTimeDataSlice(int[] dims, int axis, int slice, int veclen)
   {
      if (dataSeries.isEmpty())
         return this;
      Object d = dataSeries.get(0); 
      if (d instanceof byte[])
      {
         TimeData<byte[]> s = new TimeData<byte[]>();
         for (int timestep = 0; timestep < dataSeries.size(); timestep++)
            s.setData(get2DSlice((byte[])dataSeries.get(timestep), dims, axis, slice, veclen), timeSeries.get(timestep));
         s.setCurrentTime(currentTime);
         return (TimeData<T>)s;
      }
      if (d instanceof short[])
      {
         TimeData<short[]> s = new TimeData<short[]>();
         for (int timestep = 0; timestep < dataSeries.size(); timestep++)
            s.setData(get2DSlice((short[])dataSeries.get(timestep), dims, axis, slice, veclen), timeSeries.get(timestep));
         s.setCurrentTime(currentTime);
         return (TimeData<T>)s;
      }
      if (d instanceof int[])
      {
         TimeData<int[]> s = new TimeData<int[]>();
         for (int timestep = 0; timestep < dataSeries.size(); timestep++)
            s.setData(get2DSlice((int[])dataSeries.get(timestep), dims, axis, slice, veclen), timeSeries.get(timestep));
         s.setCurrentTime(currentTime);
         return (TimeData<T>)s;
      }
      if (d instanceof float[])
      {
         TimeData<float[]> s = new TimeData<float[]>();
         for (int timestep = 0; timestep < dataSeries.size(); timestep++)
            s.setData(get2DSlice((float[])dataSeries.get(timestep), dims, axis, slice, veclen), timeSeries.get(timestep));
         s.setCurrentTime(currentTime);
         return (TimeData<T>)s;
      }
      if (d instanceof double[])
      {
         TimeData<double[]> s = new TimeData<double[]>();
         for (int timestep = 0; timestep < dataSeries.size(); timestep++)
            s.setData(get2DSlice((double[])dataSeries.get(timestep), dims, axis, slice, veclen), timeSeries.get(timestep));
         s.setCurrentTime(currentTime);
         return (TimeData<T>)s;
      }
      return null;
   }
      
   public TimeData<float[]> get2DTimeDataFloatSlice (int[] dims, int axis, int slice, int veclen)
   {
      TimeData<float[]> s = new TimeData<float[]>();
      if (dataSeries.isEmpty())
         return s;
      Object d = dataSeries.get(0); 
      if (d instanceof byte[])
      {
         for (int timestep = 0; timestep < dataSeries.size(); timestep++)
            s.setData(get2DFloatSlice((byte[])dataSeries.get(timestep), dims, axis, slice, veclen), timeSeries.get(timestep));
         s.setCurrentTime(currentTime);
      }
      if (d instanceof short[])
      {
         for (int timestep = 0; timestep < dataSeries.size(); timestep++)
            s.setData(get2DFloatSlice((short[])dataSeries.get(timestep), dims, axis, slice, veclen), timeSeries.get(timestep));
         s.setCurrentTime(currentTime);
      }
      if (d instanceof int[])
      {
         for (int timestep = 0; timestep < dataSeries.size(); timestep++)
            s.setData(get2DFloatSlice((int[])dataSeries.get(timestep), dims, axis, slice, veclen), timeSeries.get(timestep));
         s.setCurrentTime(currentTime);
      }
      if (d instanceof float[])
      {
         for (int timestep = 0; timestep < dataSeries.size(); timestep++)
            s.setData(get2DFloatSlice((float[])dataSeries.get(timestep), dims, axis, slice, veclen), timeSeries.get(timestep));
         s.setCurrentTime(currentTime);
      }
      if (d instanceof double[])
      {
         for (int timestep = 0; timestep < dataSeries.size(); timestep++)
            s.setData(get2DFloatSlice((double[])dataSeries.get(timestep), dims, axis, slice, veclen), timeSeries.get(timestep));
         s.setCurrentTime(currentTime);
      }
      return s;
   }   
   
   public TimeData<float[]> get2DTimeDataNormSlice (int[] dims, int axis, int slice, int veclen)
   {
      TimeData<float[]> s = new TimeData<float[]>();
      if (dataSeries.isEmpty())
         return s;
      Object d = dataSeries.get(0); 
      if (d instanceof byte[])
      {
         for (int timestep = 0; timestep < dataSeries.size(); timestep++)
            s.setData(get2DNormSlice((byte[])dataSeries.get(timestep), dims, axis, slice, veclen), timeSeries.get(timestep));
         s.setCurrentTime(currentTime);
      }
      if (d instanceof short[])
      {
         for (int timestep = 0; timestep < dataSeries.size(); timestep++)
            s.setData(get2DNormSlice((short[])dataSeries.get(timestep), dims, axis, slice, veclen), timeSeries.get(timestep));
         s.setCurrentTime(currentTime);
      }
      if (d instanceof int[])
      {
         for (int timestep = 0; timestep < dataSeries.size(); timestep++)
            s.setData(get2DNormSlice((int[])dataSeries.get(timestep), dims, axis, slice, veclen), timeSeries.get(timestep));
         s.setCurrentTime(currentTime);
      }
      if (d instanceof float[])
      {
         for (int timestep = 0; timestep < dataSeries.size(); timestep++)
            s.setData(get2DNormSlice((float[])dataSeries.get(timestep), dims, axis, slice, veclen), timeSeries.get(timestep));
         s.setCurrentTime(currentTime);
      }
      if (d instanceof double[])
      {
         for (int timestep = 0; timestep < dataSeries.size(); timestep++)
            s.setData(get2DNormSlice((double[])dataSeries.get(timestep), dims, axis, slice, veclen), timeSeries.get(timestep));
         s.setCurrentTime(currentTime);
      }
      return s;
   }
}
   
