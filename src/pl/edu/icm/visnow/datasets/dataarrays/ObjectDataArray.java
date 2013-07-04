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
      schema.setMinv(0.0f);
      schema.setMaxv(1.0f);
      recomputeMinMax();
   }

   /** Creates a new instance of ComplexDataArray */
   @SuppressWarnings({"unchecked"})
   public ObjectDataArray(int ndata, int veclen)
   {
      super(FIELD_DATA_OBJECT, ndata, veclen);
      schema.setMinv(0.0f);
      schema.setMaxv(1.0f);
      recomputeMinMax();
   }

   @SuppressWarnings({"unchecked"})
   public ObjectDataArray(int ndata, int veclen, String name)
   {
      super(FIELD_DATA_OBJECT, ndata, veclen, name, "", null);
      schema.setMinv(0.0f);
      schema.setMaxv(1.0f);
      recomputeMinMax();
   }

   @SuppressWarnings({"unchecked"})
   public ObjectDataArray(int ndata, int veclen, String name, String units, String[] userData)
   {
      super(FIELD_DATA_OBJECT, ndata, veclen, name, units, userData);
      data = (T[]) (new Object[ndata * veclen]);
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
      schema.setMinv(0.0f);
      schema.setMaxv(1.0f);
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
      schema.setMinv(0.0f);
      schema.setMaxv(1.0f);
      recomputeMinMax();
   }
   
    @Override
   public int getNFrames()
   {
      return 1;
   }

    @Override
   public final void recomputeMinMax()
   {
      int vlen = getVeclen();
      if (vlen == 1)
      {
         float minv = data[0].toFloat();
         float maxv = data[0].toFloat();
         for (int i = 0; i < data.length; i++)
         {
            if (data[i].toFloat() < minv)
            {
               minv = data[i].toFloat();
            }
            if (data[i].toFloat() > maxv)
            {
               maxv = data[i].toFloat();
            }
         }
         setMinv(minv);
         setMaxv(maxv);
         setPhysMin(minv);
         setPhysMax(maxv);
      } else
      {
         float maxv = 0;
         for (int i = 0; i < data.length; i += vlen)
         {
            float v = 0;
            for (int j = 0; j < vlen; j++)
            {
               v += data[i + j].toFloat() * data[i + j].toFloat();
            }
            v = (float) Math.sqrt(v);
            if (v > maxv)
            {
               maxv = v;
            }
         }
         setMinv(0);
         setMaxv(maxv);
         setPhysMin(0);
         setPhysMax(maxv);
      }
   }

    @Override
   public final void recomputeMinMax(boolean[] mask)
   {
      int vlen = getVeclen();
      if (vlen == 1)
      {
         float minv = data[0].toFloat();
         float maxv = data[0].toFloat();
         for (int i = 0; i < data.length; i++)
         {
            if (!mask[i])
               continue;
            if (data[i].toFloat() < minv)
            {
               minv = data[i].toFloat();
            }
            if (data[i].toFloat() > maxv)
            {
               maxv = data[i].toFloat();
            }
         }
         setMinv(minv);
         setMaxv(maxv);
         setPhysMin(minv);
         setPhysMax(maxv);
      } else
      {
         float maxv = 0;
         for (int i=0, m = 0; i<data.length; i += vlen, m++)
         {
            if (!mask[m]) continue;
            float v = 0;
            for (int j = 0; j < vlen; j++)
            {
               v += data[i + j].toFloat() * data[i + j].toFloat();
            }
            v = (float) Math.sqrt(v);
            if (v > maxv)
            {
               maxv = v;
            }
         }
         setMinv(0);
         setMaxv(maxv);
         setPhysMin(0);
         setPhysMax(maxv);
      }
   }

   @SuppressWarnings(
   {
      "unchecked"
   })
    @Override
   public ObjectDataArray clone(String newName)
   {
      return new ObjectDataArray(data, schema.getVeclen(), newName, schema.getUnit(), schema.getUserData());
   }

   @SuppressWarnings(
   {
      "unchecked"
   })
    @Override
   public ObjectDataArray cloneDeep(String newName)
   {
      return new ObjectDataArray(data.clone(), schema.getVeclen(), new String(newName), new String(schema.getUnit()), schema.getUserData().clone());
   }

   @Override
   public float[] get2DSlice(int start, int n0, int step0, int n1, int step1)
   {
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
      return null;
   }

    @Override
   public short[] getSData()
   {
      if (data == null)
         setCurrentTime(0);
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
         setCurrentTime(0);
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
         setCurrentTime(0);
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
      return null;
   }

   public String[] getStringData()
   {
      if (data == null)
         setCurrentTime(0);
      String[] out = new String[data.length];
      for (int i = 0; i < out.length; i++)
      {
         out[i] = data[i].toString();
      }
      return out;
   }
   
   public T[] getObjData()
   {
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
      return data[i].toFloat();
   }
   @Override
   public void resetData()
   {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public void addData(Object d, float time)
   {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public void setCurrentTime(float currentTime)
   {
      throw new UnsupportedOperationException("Not supported yet.");
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
      throw new UnsupportedOperationException("Not supported yet.");
   }
   
    @Override
   public TimeData getTimeData()
   {
      throw new UnsupportedOperationException("Not supported yet.");
   }
   
   @Override
   public void setCurrentFrame(int currentFrame)
   {
      //currentFrame = Math.max(0, Math.min(currentFrame, timeData.size()));
      //data = timeData.get(currentFrame);
      this.currentFrame = currentFrame;
   }

    @Override
    public Object getData() {
        return getObjData();
    }

    @Override
    public void recomputeMinMax(TimeData<boolean[]> timeMask) {
    }
    
}
