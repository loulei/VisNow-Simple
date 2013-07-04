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

import java.io.Serializable;
import java.util.Vector;
import pl.edu.icm.visnow.datasets.Hashable;
import pl.edu.icm.visnow.datasets.TimeData;
import pl.edu.icm.visnow.datasets.VNObject;
import static pl.edu.icm.visnow.lib.utils.ArrayUtils.*;
import pl.edu.icm.visnow.lib.utils.VNFloatFormatter;
/*
 * DataArray.java
 *
 * Created on July 23, 2004, 11:11 AM
 */

/**
 *
 * @author Krzysztof S. Nowinski, University of Warsaw, ICM 
 * @author Bartosz Borucki, University of Warsaw, ICM 
 * The base class for all data arrays used in Field objects 
 * Holds data array by itself and additional information 
 * necessary for proper interpretation of data
 */
abstract public class DataArray implements Serializable, Hashable
{
   public static final int FIELD_DATA_UNKNOWN = -1;
   public static final int FIELD_DATA_BOOLEAN = 0;
   public static final int FIELD_DATA_BYTE = 1;
   public static final int FIELD_DATA_SHORT = 2;
   public static final int FIELD_DATA_INT = 3;
   public static final int FIELD_DATA_FLOAT = 4;
   public static final int FIELD_DATA_DOUBLE = 5;
   public static final int FIELD_DATA_COMPLEX = 6;
   public static final int FIELD_DATA_STRING = 7;
   public static final int FIELD_DATA_LOGIC = 8;
   public static final int FIELD_DATA_OBJECT = 9;
   public static final int MAX_TYPE_VALUE = 16;
   public static final int TIME_DATA_OK = 0;
   public static final int TIME_BEFORE_FIRST = 1;
   public static final int TIME_AFTER_LAST = 2;
   public static final int TIME_ENTRIES_BAD = -1;
   public static final int[] itemLength = {1, 1, 2, 4, 4, 8};
   protected DataArraySchema schema;
   protected int ndata = 0;
   protected long timestamp;
   protected long hash = 0;
   protected int currentFrame = 0;
   protected boolean invalid = false;
   protected TimeData abstractTimeData;
   protected float currentTime = 0;
   protected int[]   timeInterpolationSteps  = new int[2];
   protected float[] timeInterpolationCoeffs = new float[2];
   

   public DataArray(DataArraySchema schema, int ndata)
   {
      this.schema = schema;
      this.ndata = ndata;
      this.timestamp = System.currentTimeMillis();
      currentFrame = 0;
      currentTime  = 0;
   }

   public DataArray(int type, int ndata, int veclen)
   {
      if (type <= 0 || type > MAX_TYPE_VALUE)
         return;
      schema = new DataArraySchema("Data", type, veclen);
      this.ndata = ndata;
      this.timestamp = System.currentTimeMillis();
      currentFrame = 0;
      currentTime  = 0;
   }

   public DataArray(int type, int ndata, int veclen, String name, String units, String[] userData)
   {
      if (type <= 0 || type > MAX_TYPE_VALUE)
         return;
      schema = new DataArraySchema(name, units, userData, type, veclen);
      this.ndata = ndata;
      this.timestamp = System.currentTimeMillis();
      currentFrame = 0;
      currentTime  = 0;
   }

   @Override
   public String toString()
   {
      return schema.toString();
   }

    public String description() {
        StringBuilder s = new StringBuilder();
        s.append("<TR>").append(schema.getName()).append("<TD>").append(schema.getVeclen()).append("</TD><TD>");
        switch (schema.getType()) {
            case DataArray.FIELD_DATA_BYTE:
                s.append(" byte");
                break;
            case DataArray.FIELD_DATA_SHORT:
                s.append(" short");
                break;
            case DataArray.FIELD_DATA_INT:
                s.append(" int");
                break;
            case DataArray.FIELD_DATA_FLOAT:
                s.append(" float");
                break;
            case DataArray.FIELD_DATA_DOUBLE:
                s.append(" double");
                break;
            case DataArray.FIELD_DATA_COMPLEX:
                s.append(" complex");
                break;
            case DataArray.FIELD_DATA_LOGIC:
                s.append(" logic");
                break;
            case FIELD_DATA_STRING:
                s.append(" string");
                break;
            case FIELD_DATA_OBJECT:
                ObjectDataArray oda = (ObjectDataArray) this;
                if (oda.getObjData(0) != null) {
                    s.append(oda.getObjData(0).getClass().getName());
                } else {
                    s.append(" object");
                }
                break;
            default:
                s.append(" unknown");
                break;
        }

        s.append("</TD><TD>");
        s.append("" + getNFrames());
        s.append("</TD><TD>");
        s.append(VNFloatFormatter.defaultRangeFormat(getMinv()));
        s.append("</TD><TD>");
        s.append(VNFloatFormatter.defaultRangeFormat(getMaxv()));
        s.append("</TD><TD>");
        s.append(VNFloatFormatter.defaultRangeFormat(getPhysMin()));
        s.append("</TD><TD>");
        s.append(VNFloatFormatter.defaultRangeFormat(getPhysMax()));
        s.append("</TD></TR>");

        return s.toString();
    }

   public static DataArray create(int type, int ndata, int veclen)
   {
      switch (type)
      {
      case FIELD_DATA_BYTE:
         return new ByteDataArray(ndata, veclen);
      case FIELD_DATA_SHORT:
         return new ShortDataArray(ndata, veclen);
      case FIELD_DATA_INT:
         return new IntDataArray(ndata, veclen);
      case FIELD_DATA_FLOAT:
         return new FloatDataArray(ndata, veclen);
      case FIELD_DATA_DOUBLE:
         return new DoubleDataArray(ndata, veclen);
      case FIELD_DATA_COMPLEX:
         return new ComplexDataArray(ndata, veclen);
      case FIELD_DATA_LOGIC:
         return new LogicDataArray(ndata, veclen);
      case FIELD_DATA_STRING:
         return new StringDataArray(ndata, veclen);
      default:
         return null;
      }
   }

   public static DataArray create(DataArraySchema schema, int ndata)
   {
      switch (schema.getType())
      {
      case FIELD_DATA_BYTE:
         return new ByteDataArray(schema, ndata);
      case FIELD_DATA_SHORT:
         return new ShortDataArray(schema, ndata);
      case FIELD_DATA_INT:
         return new IntDataArray(schema, ndata);
      case FIELD_DATA_FLOAT:
         return new FloatDataArray(schema, ndata);
      case FIELD_DATA_DOUBLE:
         return new DoubleDataArray(schema, ndata);
      case FIELD_DATA_COMPLEX:
         return new ComplexDataArray(schema, ndata);
      case FIELD_DATA_LOGIC:
         return new LogicDataArray(schema, ndata);
      case FIELD_DATA_STRING:
         return new StringDataArray(schema, ndata);
      case FIELD_DATA_OBJECT:
         return new ObjectDataArray(schema, ndata);
      default:
         return null;
      }
   }

   public static DataArray create(int type, int ndata, int veclen, String name, String unit, String[] userData)
   {
      switch (type)
      {
      case FIELD_DATA_BYTE:
         return new ByteDataArray(ndata, veclen, name, unit, userData);
      case FIELD_DATA_SHORT:
         return new ShortDataArray(ndata, veclen, name, unit, userData);
      case FIELD_DATA_INT:
         return new IntDataArray(ndata, veclen, name, unit, userData);
      case FIELD_DATA_FLOAT:
         return new FloatDataArray(ndata, veclen, name, unit, userData);
      case FIELD_DATA_DOUBLE:
         return new DoubleDataArray(ndata, veclen, name, unit, userData);
      case FIELD_DATA_COMPLEX:
         return new ComplexDataArray(ndata, veclen, name, unit, userData);
      case FIELD_DATA_LOGIC:
         return new LogicDataArray(ndata, veclen, name, unit, userData);
      case FIELD_DATA_STRING:
         return new StringDataArray(ndata, veclen, name, unit, userData);
      case FIELD_DATA_OBJECT:
         return new ObjectDataArray(ndata, veclen, name, unit, userData);
      default:
         return null;
      }
   }

   @SuppressWarnings({"unchecked"})
   
   public static <T extends VNObject> DataArray create(int ndata, int veclen, String name)
   {
      T[] data = (T[]) (new Object[ndata * veclen]);
      return new ObjectDataArray(data, veclen, name);
   }

   public static DataArray create(byte[] data, int veclen, String name, String units, String[] userData)
   {
      return new ByteDataArray(data, veclen, name, units, userData);
   }

   public static DataArray create(short[] data, int veclen, String name, String units, String[] userData)
   {
      return new ShortDataArray(data, veclen, name, units, userData);
   }

   public static DataArray create(int[] data, int veclen, String name, String units, String[] userData)
   {
      return new IntDataArray(data, veclen, name, units, userData);
   }

   public static DataArray create(float[] data, int veclen, String name, String units, String[] userData)
   {
      return new FloatDataArray(data, veclen, name, units, userData);
   }

   public static DataArray create(double[] data, int veclen, String name, String units, String[] userData)
   {
      return new DoubleDataArray(data, veclen, name, units, userData);
   }

   public static DataArray create(float[] dataReal, float[] dataImag, int veclen, String name, String units, String[] userData)
   {
      return new ComplexDataArray(dataReal, dataImag, veclen, name, units, userData);
   }

   public static DataArray create(BitArray data, int veclen, String name, String units, String[] userData)
   {
      return new LogicDataArray(data, veclen, name, units, userData);
   }

   public static DataArray create(String[] data, int veclen, String name, String units, String[] userData)
   {
      return new StringDataArray(data, veclen, name, units, userData);
   }
   
   public static DataArray create(VNObject[] data, int veclen, String name, String units, String[] userData)
   {
      return new ObjectDataArray(data, veclen, name, units, userData);
   }

   public static DataArray create(byte[] data, int veclen, String name)
   {
      return create(data, veclen, name, "", null);
   }

   public static DataArray create(short[] data, int veclen, String name)
   {
      return create(data, veclen, name, "", null);
   }

   public static DataArray create(int[] data, int veclen, String name)
   {
      return create(data, veclen, name, "", null);
   }

   public static DataArray create(float[] data, int veclen, String name)
   {
      return create(data, veclen, name, "", null);
   }

   public static DataArray create(double[] data, int veclen, String name)
   {
      return create(data, veclen, name, "", null);
   }

   public static DataArray create(float[] dataReal, float[] dataImag, int veclen, String name)
   {
      return create(dataReal, dataImag, veclen, name, "", null);
   }

   public static DataArray create(BitArray data, int veclen, String name)
   {
      return create(data, veclen, name, "", null);
   }

   public static DataArray create(String[] data, int veclen, String name)
   {
      return create(data, veclen, name, "", null);
   }
   
   public static DataArray create(VNObject[] data, int veclen, String name)
   {
      return create(data, veclen, name, "", null);
   }
   
      
   abstract public void resetData();
   abstract public void addData(Object d, float time);
   public int getNFrames()
   {
       
      return abstractTimeData.size();
   }


   /**
    * returns a clone with the same (not copied) data and weclen and new name
    * @param name - new data array name
    * @return cloned data array
    */
   abstract public DataArray clone(String name);

   abstract public DataArray cloneDeep(String name);

   public int getType()
   {
      return schema.getType();
   }

   /**
     *
     * getter for dims property
     * @return	<code>int[]</code> dims value.
     */
   public int[] getDims()
   {
      return schema.getDims();
   }

   /**
     *
     * getter for symmetric property
     * @return	<code>boolean</code> symmetric value.
     */
   public boolean isSymmetric()
   {
      return schema.isSymmetric();
   }

   /**
     *
     * setter for dims and symmetric properties 
     * @param	dims - local array dimensions
     * @param symmetric symmetric array indicator
     * checks for partameter compatibility
     */
   public void setMatrixProperties(int[] dims, boolean symmetric)
   {
      schema.setMatrixProperties(dims, symmetric);
   }

   /**
    * Getter for property maxv.
    * @return Value of property maxv.
    */
   public float getMaxv()
   {
      return schema.getMaxv();
   }

   /**
    * Setter for property maxv.
    * @param maxv New value of property maxv.
    */
   public void setMaxv(float maxv)
   {
      schema.setMaxv(maxv);
   }

   /**
    * Getter for property minv.
    * @return Value of property minv.
    */
   public float getMinv()
   {
      return schema.getMinv();
   }

   /**
    * Setter for property minv.
    * @param minv New value of property minv.
    */
   public void setMinv(float minv)
   {
      schema.setMinv(minv);
   }

   /**
    * Getter for property veclen.
    * @return Value of property veclen.
    */
   public int getVeclen()
   {
      return schema.getVeclen();
   }

   public int getNData()
   {
      return ndata;
   }

   /**
    * Setter for property veclen.
    * @param veclen New value of property veclen.
    */
   public void setVeclen(int veclen)
   {
      schema.setVeclen(veclen);
   }

   /**
    * Getter for property name.
    * @return Value of property name.
    */
   public java.lang.String getName()
   {
      return schema.getName();
   }

   /**
    * Setter for property name.
    * @param name New value of property name.
    */
   public void setName(java.lang.String name)
   {
      schema.setName(name);
   }

   public float getPhysMax()
   {
      return schema.getPhysMax();
   }

   public void setPhysMax(float physMax)
   {
      schema.setPhysMax(physMax);
   }

   public float getPhysMin()
   {
      return schema.getPhysMin();
   }

   public void setPhysMin(float physMin)
   {
      schema.setPhysMin(physMin);
   }

   public String getUnit()
   {
      return schema.getUnit();
   }

   public void setUnit(String unit)
   {
      schema.setUnit(unit);
   }

   public boolean isSimpleNumeric()
   {
      return schema.isSimpleNumeric();
   }

   /**
     * getter for array schema
     * @return schema
     */
   public DataArraySchema getSchema()
   {
      return schema;
   }

   public boolean compatibleWith(DataArray a)
   {
      return schema.compatibleWith(a.getSchema());
   }

   public boolean fullyCompatibleWith(DataArray a, boolean checkComponentNames)
   {
      return schema.compatibleWith(a.getSchema(), checkComponentNames) && 
             ndata == a.getNData() &&
             getTimeData().isTimeCompatible(a.getTimeData());
   }
   
   public boolean fullyCompatibleWith(DataArray a)
   {
      return schema.compatibleWith(a.getSchema()) && 
             ndata == a.getNData() &&
             getTimeData().isTimeCompatible(a.getTimeData());
   }
   

   abstract public void recomputeMinMax();

   abstract public void recomputeMinMax(boolean[] mask);
   
   abstract public void recomputeMinMax(TimeData<boolean[]> timeMask);

   abstract public float[] get2DSlice(int start, int n0, int step0, int n1, int step1);
   
   abstract public float[] get2DNormSlice(int start, int n0, int step0, int n1, int step1);

   abstract public float[] get1DSlice(int start, int n, int step);

   abstract public float[] getVData(int start);
   
   public float[] getVectorNorms()
   {
      switch (getType())
      {
      case FIELD_DATA_BYTE:
         return vectorNorms(getBData(), getVeclen());
      case FIELD_DATA_SHORT:
         return vectorNorms(getSData(), getVeclen());
      case FIELD_DATA_INT:
         return vectorNorms(getIData(), getVeclen());
      case FIELD_DATA_FLOAT:
         return vectorNorms(getFData(), getVeclen());
      case FIELD_DATA_DOUBLE:
         return vectorNorms(getDData(), getVeclen());
      default:
         return null;
      }
   }
  
   abstract public byte[] getBData();
   abstract public short[] getSData();
   abstract public int[] getIData();
   abstract public float[] getFData();
   abstract public float[] getNormFData();
   abstract public double[] getDData();
   abstract public Object getData();
  
   abstract public byte[] produceBData(float time);
   abstract public short[] produceSData(float time);
   abstract public int[] produceIData(float time);
   abstract public float[] produceFData(float time);
   abstract public double[] produceDData(float time);

   abstract public float getData(int i);
   
   public static final int TILE_OK = 0;
   public static final int TILE_TYPE_ERROR = 3;
   public static final int TILE_SIZE_ERROR = 2;
   public static final int TILE_DIMS_ERROR = 1;

   /**
    * Get the value of userData
    *
    * @return the value of userData
    */
   public String[] getUserData()
   {
      return schema.getUserData();
   }

   /**
    * Set the value of userData
    *
    * @param userData new value of userData
    */
   public void setUserData(String[] userData)
   {
      schema.setUserData(userData);
   }

   /**
    * Get the value of userData at specified index
    *
    * @param index
    * @return the value of userData at specified index
    */
   public String getUserData(int index)
   {
      return schema.getUserData(index);
   }

   /**
    * Set the value of userData at specified index.
    *
    * @param index
    * @param newUserData new value of userData at specified index
    */
   public void setUserData(int index, String newUserData)
   {
      schema.setUserData(index, newUserData);
   }

   protected void updateTimestamp()
   {
      timestamp = System.currentTimeMillis();
   }

   public boolean changedSince(long timestamp)
   {
      return this.timestamp > timestamp;
   }

   public long getHash()
   {
      return hash;
   }

   public static String getTypeName(int type)
   {
      switch (type)
      {
      case FIELD_DATA_BYTE:
         return "byte";
      case FIELD_DATA_SHORT:
         return "short";
      case FIELD_DATA_INT:
         return "int";
      case FIELD_DATA_FLOAT:
         return "float";
      case FIELD_DATA_DOUBLE:
         return "double";
      case FIELD_DATA_COMPLEX:
         return "complex";
      case FIELD_DATA_LOGIC:
         return "logic";
      case FIELD_DATA_STRING:
         return "string";
      default:
         return "";
      }
   }
   
   abstract public void setTimeData(TimeData tData);
   
   public float getTime(int frame)
   {
      return abstractTimeData.getTime(frame);
   }
   
   public int getCurrentFrame()
   {
      return currentFrame;
   }

   abstract public void setCurrentFrame(int currentFrame);
//   {
//      this.currentFrame = currentFrame;
//   }

   public float getCurrentTime()
   {
      return currentTime;
   }

   abstract public void setCurrentTime(float currentTime);
   
   public float getStartTime()
   {
      if (abstractTimeData == null)
         return 0;
      return abstractTimeData.getStartTime();
   }
   
   public float getEndTime()
   {      
      if (abstractTimeData == null)
         return 1;
      return abstractTimeData.getEndTime();
   }
   
   public boolean isTimestep(float t)
   {
      return abstractTimeData != null && abstractTimeData.isTimestep(t);
   }

   public boolean isTimeDependant()
   {      
      return (abstractTimeData.size() > 1);
   }
   
   public Vector<Float> getTimeSeries()
   {
      return abstractTimeData.getTimeSeries();
   }
   
   public DataArray get2DSlice(int[] dims, int axis, int slice)
   {
      if (dims == null || dims.length != 3 || axis < 0 || axis > 2 || slice < 0 || slice >= dims[axis])
         return null;
      TimeData slicedTimeData = abstractTimeData.get2DTimeDataSlice(dims, axis, slice, getVeclen());
      DataArray da = create(getType(), getNData() / dims[axis], getVeclen(), getName(), getUnit(), getUserData());
      da.setTimeData(slicedTimeData);
      da.setCurrentTime(currentTime);
      return da;
   }
   
   public DataArray concatenate(Vector<DataArray> concatenatedArrays)
   {
      int n = getNData();
      for (int i = 0; i < concatenatedArrays.size(); i++)
      {
         DataArray cD = concatenatedArrays.get(i);
//         if (!is(cD))
//            return null;
//         n += cD.getLenth();
      }
      return null;
      
   }

   /**
    * Basic comparator for DataArray compatibility
    * @param s - DataArray to be checked for compatibility
    * @return  - true if name, type, veclen, units and time vectors of s are compatible
    */
   public boolean compatibleWith(DataArray s, boolean checkComponentNames)
   {
      if (!schema.compatibleWith(s.getSchema(), checkComponentNames))
              return false;
      Vector<Float> tSeries = getTimeSeries();
      Vector<Float> sSeries = s.getTimeSeries();
      if (sSeries.size() != tSeries.size())
         return false;
      for (int i = 0; i < tSeries.size(); i++)
         if (tSeries.get(i) != sSeries.get(i))
            return false;
      return true;
   }

   abstract public TimeData getTimeData();
   
}
