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
import static pl.edu.icm.visnow.lib.utils.ArrayUtils.*;

/**
 *
 * @author Bartosz Borucki, University of Warsaw, ICM 
 * 
 */
public class StringDataArray extends DataArray
{
   /**
    * an array holding real part of data
    */
   private String[] data;
   private TimeData<String[]> timeData = new TimeData<String[]>();

   /**
    * Creates a new <code>StringDataArray</code> object.
    *
    * @param	schema	This data array schema.
    * @param	ndata	number of data elements.
    */
   public StringDataArray(DataArraySchema schema, int ndata)
   {
      super(schema, ndata);
      timeData.clear();
      abstractTimeData = timeData;
      recomputeMinMax();
   }


   /** Creates a new instance of ComplexDataArray */
   public StringDataArray(int ndata, int veclen)
   {
      super(FIELD_DATA_STRING,ndata,veclen);
      timeData.clear();
      abstractTimeData = timeData;
      recomputeMinMax();
   }

   public StringDataArray(int ndata, int veclen, String name, String units, String[] userData)
   {
      super(FIELD_DATA_STRING,ndata,veclen,name, units, userData);
      timeData.clear();
      abstractTimeData = timeData;
      recomputeMinMax();
   }

   public StringDataArray(String[] data, int veclen, String name, String units, String[] userData)
   {
      super(FIELD_DATA_STRING,data.length/veclen,veclen,name, units, userData);
      this.data = data;
      timeData.clear();
      timeData.add(data);
      abstractTimeData = timeData;
      recomputeMinMax();
   }

   public StringDataArray(String[] data, String name, String units, String[] userData)
   {
      this(data, 1, name , units, userData);
   }

   public StringDataArray(int ndata, int veclen, String name)
   {
      super(FIELD_DATA_STRING,ndata,veclen,name, "", null);
      data = new String[ndata*veclen];
      timeData.clear();
      timeData.add(data);
      abstractTimeData = timeData;
      recomputeMinMax();
   }

   public StringDataArray(String[] data, String name) {
      super(FIELD_DATA_STRING,data.length,1,name, "", null);
      this.data = data;
      timeData.clear();
      timeData.add(data);
      abstractTimeData = timeData;
      recomputeMinMax();
   }
   
    @Override
   public int getNFrames()
   {
      return 1;
   }

   public StringDataArray(TimeData<String[]> tData, int veclen, String name, String units, String[] userData)
   {
        super(FIELD_DATA_STRING, (tData == null || tData.get(0) == null) ? -1 : tData.get(0).length / veclen, veclen, name, units, userData);
        abstractTimeData = timeData = tData;
        setCurrentTime(currentTime);
        recomputeMinMax();
   }
   
    @Override
   public final void recomputeMinMax()
   {
      float minv = Float.MAX_VALUE;
      float maxv = -Float.MAX_VALUE;
      int len;
      for (int step = 0; step < timeData.size(); step ++)
      {
         String[] dta = timeData.get(step);         
         int vlen = getVeclen();
         if (vlen == 1)
         {
            for (int i = 0; i < dta.length; i++)
            {
               len = dta[i].length();
               if (len < minv)
                  minv = len;
               if (len > maxv)
                  maxv = len;
            }
         } else
         {
            for (int i = 0; i < dta.length; i += vlen)
            {
               float v = 0;
               for (int j = 0; j < vlen; j++) {
                  len = dta[i + j].length();
                  v += len * len;
               }
               v = (float) Math.sqrt(v);
               if (v > maxv) {
                  maxv = v;
               }
               if (v < minv) {
                  minv = v;
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
      int len;
      for (int step = 0; step < timeData.size(); step ++)
      {
         String[] dta = timeData.get(step);         
         int vlen = getVeclen();
         if (vlen == 1)
         {
            for (int i = 0; i < dta.length; i++)
            {
               if (!mask[i])
                  continue;
               len = dta[i].length();
               if (len < minv)
                  minv = len;
               if (len > maxv)
                  maxv = len;
            }
         } else
         {
            for (int i = 0, m = 0; i < dta.length; i += vlen, m++)
            {
               if (!mask[m])
                  continue;
               float v = 0;
               for (int j = 0; j < vlen; j++) {
                  len = dta[i + j].length();
                  v += len * len;
               }
               v = (float) Math.sqrt(v);
               if (v > maxv) {
                  maxv = v;
               }
               if (v < minv) {
                  minv = v;
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
      ArrayList<Float> timeline = timeData.getTimeSeries();
      int len;
      for (int step = 0; step < timeline.size(); step ++)
      {
         String[] dta = timeData.get(step);         
         boolean[] mask = timeMask.getData(timeline.get(step));
         int vlen = getVeclen();
         if (vlen == 1)
         {
            for (int i = 0; i < dta.length; i++)
            {
               if (!mask[i])
                  continue;
               len = dta[i].length();
               if (len < minv)
                  minv = len;
               if (len > maxv)
                  maxv = len;
            }
         } else
         {
            for (int i = 0, m = 0; i < dta.length; i += vlen, m++)
            {
               if (!mask[m])
                  continue;
               float v = 0;
               for (int j = 0; j < vlen; j++) {
                  len = dta[i + j].length();
                  v += len * len;
               }
               v = (float) Math.sqrt(v);
               if (v > maxv) {
                  maxv = v;
               }
               if (v < minv) {
                  minv = v;
               }
            }
         }
      }
      recomputePhysMinMax(minv, maxv);
      setMinv(minv);
      setMaxv(maxv);        
    }
   
   
    @Override
   public StringDataArray clone(String newName)
   {
      return new StringDataArray(data, schema.getVeclen(), newName, schema.getUnit(), schema.getUserData());
   }

    @Override
   public StringDataArray cloneDeep(String newName)
   {
        if(schema.getUserData() != null)
            return new StringDataArray(data.clone(), schema.getVeclen(), new String(newName), new String(schema.getUnit()), schema.getUserData().clone());
        else
            return new StringDataArray(data.clone(), schema.getVeclen(), new String(newName), new String(schema.getUnit()), null);
   }

   @Override
   public float[] get2DSlice(int start, int n0, int step0, int n1, int step1)
   {
      return null;
   }

   @Override
   public float[] get2DNormSlice(int start, int n0, int step0, int n1, int step1)
   {
      return null;
   }

   @Override
   public float[] get1DSlice(int start, int n, int step)
   {
      return null;
   }

    @Override
    public float[] getVData(int start) {
        return null;
    }

    
    @Override
    public byte[] getBData() 
    {
        return null;
    }

    @Override
    public short[] getSData() 
    {
        return null;
    }

    @Override
    public int[] getIData() 
    {
        return null;
    }

    @Override
    public float[]  getFData() 
    {
      if (data == null)
         setCurrentTime(currentTime);
      return convertToFloatArray(data);
    }

    @Override
    public float[]  getNormFData() 
    {
        return null;
    }

    @Override
    public double[] getDData() 
    {
        return null;
    }

    public String[] getStringData()
    {
       return data;
    }

    @Override
   public float getData(int i) {
        return 0;
    }

   @Override
   public void resetData()
   {
      data = timeData.getData(currentTime);
      timeData.clear();
      timeData.add(data);
   }
   
    @Override
   public void addData(Object d, float time)
   {
      if (d instanceof String[])
      {
         timeData.setData((String[]) d, time);
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
   
   public String[] produceStringData(float time)
   {
      return timeData.produceData(time, getType(), getVeclen() * ndata);
   }
   
   public String[] getData(float time)
   {
      return timeData.getData(time);
   }
   
    @Override
   public void setTimeData(TimeData tData)
   {
      if (tData == null || tData.isEmpty())
         return;
      if (!(tData.get(0) instanceof String[]) || ((String[])(tData.get(0))).length != ndata * getVeclen())
         return;
      abstractTimeData = timeData = tData;
      setCurrentTime(currentTime);
      recomputeMinMax();
   }

    @Override
   public TimeData<String[]> getTimeData()
   {
      return timeData;
   }

    @Override
    public Object getData() {
        return getStringData();
    }

}

