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

/**
 *
 * Holds general information about a data array without data array values.
 * Can be used for compatibility checking and field data schema comparison.
 * 
 * @author  Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class DataArraySchema implements Serializable
{

   protected static final String[] typeNames = 
                           {"boolean", "byte   ", "short  ", "int    ", "float  ", "double ", "string ", "logic  ", "object "};
   /**
    * data array name
    */
   protected String name = "";
   /**
    * String describing data physical unit, e.g. "m/sec", "hPa" etc.
    * can be used in legend/axes labeling and data computation modules
    * to ensure physical correctness of computations
    */
   protected String unit = null;
   /**
    * computational data type - currently one of standard Java computational types, string 
    * and object
    */
   protected int type = DataArray.FIELD_DATA_UNKNOWN;
   /**
    * 1 if data are scalar (default)
    * generally, vector length of an individual data item;
    * array of data will be <lt>type<gt>[ndata*veclen]
    */
   protected int veclen = 1;
   /**
    * dimensions of the individual data item as an array 
    * initialized to an one-dimensional array with veclen as an only dimension;
    * When the symmetric flag is set to false, product of all dimensions must equal to veclen,
    * otherwise dims must ne of the form {n,n} and veclen=nx(n+1)/2;
    */
   protected int[] dims = new int[] {veclen};
   /**
    * flag indicating that each data item represents a symmetric array;
    * elements are stored in the order: {m00,m01,...,m0n,m11,...,m1n,...,mnn}
    * checked only when dims.length = 2, dims = {n,n} and veclen=nx(n+1)/2
    */
   protected boolean symmetric = false;
   /**
    * minimum of data values  (need not be equal to true minimum),
    * can be explicitly set by the user.
    * In the case of vector data minv = 0
    */
   protected float minv = 1;
   /**
    * maximum of data values  (need not be equal to true maximum),
    * can be explicitly set by the user.
    * In the case of vector data maxv = max norm(data item)
    */
   protected float maxv = -1;
   /**
    * "physical" minimum of data values  explicitly set by the user.
    * used in the case of byte compressed data
    * In the case of vector data maxv = max norm(data item)
    */
   protected float physMin = 1;
   /**
    * "physical" maximum of data values  explicitly set by the user.
    * used in the case of byte compressed data
    * In the case of vector data maxv = max norm(data item)
    */
   protected float physMax = -1;
   /**
    * vector of minima of data values components  (need not be equal to true min/max),
    * can be explicitly set by the user.
    * (Can be null, if not null must be of length = veclen)
    */
   protected float[] minvect = null;
   /**
    * maxvect :vector of maxima of data values components (need not be equal to true min/max),
    * can be explicitly set by the user.
    * (Can be null, if not null must be of length = veclen)
    */
   protected float[] maxvect = null;
   protected float[] physVectMin = null, physVectMax = null;
   /**
    * user annotations for data meaning
    * currently supported:
    * "RGBCOLOR"  when 3-vector bytes are interpreted as RGB color components
    * "RGBACOLOR" when 4-vector bytes are interpreted as RGB color components with alpha
    * "MAP" followed by a series of <lt>value<gt>:<lt>name<gt> strings for data
    * showing position of objects in space
    */
   protected String[] userData;

   /**
    * The comprehensive constructor setting all fields values
    * @param name - user name for data array
    * @param unit - physical units of data items
    * @param userData - arbitrary String array to be used in modules (e.g. {"map", "value":"name", "value":"name", ...}
    * @param type - one of DataArraySchema.FIELD_DATA_BYTE ... DataArraySchema.FIELD_DATA_COMPLEX
    * @param veclen - number of data components at each element
    * @param dims - dimensions of the individual data item as an array 
    * @param symmetric - indicates that each data element is a symmetric array
    * @param minv   - minimum of data values (0 for vector data)
    * @param maxv   - maximum (norm) of data values
    * @param minvect - array of minimum values of data components
    * @param maxvect - array of maximum values of data components
    * @param physMin - minimum of physical data values
    * @param physMax - maximum of physical data values
    * @param physVectMin - array of physical minimum values of data components
    * @param physVectMax - array of physical maximum values of data components
    */
   public DataArraySchema(String name, String unit, String[] userData,
           int type, int veclen, int[] dims, boolean symmetric,
           float minv, float maxv,
           float[] minvect, float[] maxvect,
           float physMin, float physMax,
           float[] physVectMin, float[] physVectMax)
   {
      if (type > DataArray.MAX_TYPE_VALUE || type < 0)
      {
         return;
      }
      this.name = name;
      this.unit = unit;
      this.userData = userData;
      this.type = type;
      this.veclen = veclen;
      int k = 1;
      for (int i = 0; i < dims.length; i++)
         k *= dims[i];
      if (k == veclen || (dims.length == 2 && dims[0] == dims[1] && veclen == (dims[0] * (dims[0] + 1)) / 2 && symmetric))
      {
         this.dims = dims;
         this.symmetric = symmetric;
      }
      this.minv = minv;
      this.maxv = maxv;
      this.minvect = minvect;
      this.maxvect = maxvect;
      this.physMin = physMin;
      this.physMax = physMax;
      this.physVectMin = physVectMin;
      this.physVectMax = physVectMax;
   }

   /**
    * The comprehensive constructor setting all fields values
    * @param name - user name for data array
    * @param unit - physical units of data items
    * @param userData - arbitrary String array to be used in modules (e.g. {"map", "value":"name", "value":"name", ...}
    * @param type - one of DataArraySchema.FIELD_DATA_BYTE ... DataArraySchema.FIELD_DATA_COMPLEX
    * @param veclen - number of data components at each element
    * @param minv   - minimum of data values (0 for vector data)
    * @param maxv   - maximum (norm) of data values
    * @param minvect - array of minimum values of data components
    * @param maxvect - array of maximum values of data components
    * @param physMin - minimum of physical data values
    * @param physMax - maximum of physical data values
    * @param physVectMin - array of physical minimum values of data components
    * @param physVectMax - array of physical maximum values of data components
    */
   public DataArraySchema(String name, String unit, String[] userData,
           int type, int veclen,
           float minv, float maxv,
           float[] minvect, float[] maxvect,
           float physMin, float physMax,
           float[] physVectMin, float[] physVectMax)
   {
      this(name, unit, userData, type, veclen, new int[]
              {
                 veclen
              }, false,
              minv, maxv, minvect, maxvect, physMin, physMax, physVectMin, physVectMax);
   }

   /**
    * A basic constructor setting required fields values
    * @param name - user name for data array
    * @param unit - physical units of data items
    * @param userData - arbitrary String array to be used in modules (e.g. {"map", "value":"name", "value":"name", ...}
    * @param type - one of DataArray.FIELD_DATA_BYTE ... DataArray.FIELD_DATA_COMPLEX
    * @param veclen - number of data components at each element
    */
   public DataArraySchema(String name, String unit, String[] userData, int type, int veclen)
   {
      this(name, unit, userData, type, veclen, -Float.MAX_VALUE, Float.MAX_VALUE, null, null, -Float.MAX_VALUE, Float.MAX_VALUE, null, null);
   }

   /**
    * A basic constructor setting required fields values
    * @param name - user name for data array
    * @param type - one of DataArray.FIELD_DATA_BYTE ... DataArray.FIELD_DATA_COMPLEX
    * @param veclen - number of data components at each element
    */
   public DataArraySchema(String name, int type, int veclen)
   {
      this(name, "", null, type, veclen);
   }
   
   public String getTypeName()
   {
      if (type < 0 || type >= typeNames.length)
         return " unknown";
      return typeNames[type];
   }

   @Override
   public String toString()
   {
      if (veclen == 1)
         return name + " scalar " + getTypeName();
      else
         return name + " " + veclen+"-vector " + getTypeName();
   }

   public String description()
   {
      return "<TR>"+name+"<TD>"+veclen+"</TD><TD>" +  getTypeName() + 
              String.format("</TD><TD>%6.3f</TD><TD>%6.3f</TD><TD>%6.3f</TD><TD>%6.3f</TD></TR>", 
                            getMinv(),getMaxv(),getPhysMin(),getPhysMax());
   }
   
   /**
    * getter for maxv
    * @return maxv
    */
   public float getMaxv()
   {
      return maxv;
   }

   /**
    * setter for maxv
    * @param maxv - new value for maxv
    */
   public void setMaxv(float maxv)
   {
      this.maxv = maxv;
   }

   /**
    * getter for minv
    * @return minv
    */
   public float getMinv()
   {
      return minv;
   }

   /**
    * setter for minv
    * @param minv - new value for minv
    */
   public void setMinv(float minv)
   {
      this.minv = minv;
   }

   /**
    * getter for name
    * @return name
    */
   public String getName()
   {
      return name;
   }

   /**
    * setter for name
    * @param name - new value for name
    */
   public void setName(String name)
   {
      this.name = name;
   }

   public float getPhysMax()
   {
      return physMax;
   }

   public void setPhysMax(float physMax)
   {
      this.physMax = physMax;
   }

   public float getPhysMin()
   {
      return physMin;
   }

   public void setPhysMin(float physMin)
   {
      this.physMin = physMin;
   }

   public int getType()
   {
      return type;
   }

   public void setType(int type)
   {
      this.type = type;
   }

   public boolean isSimpleNumeric()
   {
      return type == DataArray.FIELD_DATA_BYTE || 
             type == DataArray.FIELD_DATA_SHORT || 
             type == DataArray.FIELD_DATA_INT || 
             type == DataArray.FIELD_DATA_FLOAT || 
             type == DataArray.FIELD_DATA_DOUBLE;
   }

   public String getUnit()
   {
      return unit;
   }

   public void setUnit(String unit)
   {
      this.unit = unit;
   }

   /**
    *
    * getter for veclen property
    * @return	<code>int</code> veclen value.
    */
   public int getVeclen()
   {
      return veclen;
   }

   /**
    *
    * setter for veclen property
    * @param	veclen	The veclen.
    */
   public void setVeclen(int veclen)
   {
      this.veclen = veclen;
   }

   /**
    *
    * getter for dims property
    * @return	<code>int[]</code> dims value.
    */
   public int[] getDims()
   {
      return dims;
   }

   /**
    *
    * getter for symmetric property
    * @return	<code>boolean</code> symmetric value.
    */
   public boolean isSymmetric()
   {
      return symmetric;
   }

   /**
    *
    * setter for dims and symmetric properties 
    * @param	dims - local array dimensions
    * @param symmetric symmetric array indicator
    * checks for parameter compatibility
    */
   public void setMatrixProperties(int[] dims, boolean symmetric)
   {
      int k = 1;
      for (int i = 0; i < dims.length; i++)
         k *= dims[i];
      if (k == veclen || (dims.length == 2 && dims[0] == dims[1] && veclen == (dims[0] * (dims[0] + 1)) / 2 && symmetric))
      {
         this.dims = dims;
         this.symmetric = symmetric;
      }
   }

   /**
    * Basic comparator for DataArraySchema compatibility
    * @param s - DataArraySchema to be checked for compatibility
    * @param checkComponentNames - flag to include components name checking
    * @param  - flag to include components ranges checking
    * @return  - if checkComponentNames and checkComponentRanges: true if name, type, veclen, units and data range of s are equal to this<p>
    * if checkComponentNames true if name, type, veclen and units of s are equal to this<p>
    * if checkComponentRanges true if type, veclen and data range of s are equal to this<p>
    * otherwise, true if type and veclen are equal to this<p>
    */
   public boolean compatibleWith(DataArraySchema s, boolean checkComponentNames, boolean checkComponentRanges)
   {
      boolean compat = false;
      if (checkComponentNames)
         compat = type == s.getType() && veclen == s.getVeclen()
                 && ((name == null && s.getName() == null)
                 || (name != null && s.getName() != null && name.equals(s.getName())))
                 && ((unit == null && s.getUnit() == null)
                 || (unit != null && s.getUnit() != null && unit.equals(s.getUnit())));
      else
         compat = type == s.getType() && veclen == s.getVeclen()
                 && ((unit == null && s.getUnit() == null)
                 || (unit != null && s.getUnit() != null && unit.equals(s.getUnit())));
      if (!compat)
         return false;
      if (checkComponentRanges)
         return minv == s.getMinv() && maxv == s.getMaxv();
      else
         return true;
   }

   /**
    * Basic comparator for DataArraySchema compatibility
    * @param s - DataArraySchema to be checked for compatibility
    * @param checkComponentNames - flag to include components name checking
    * @return  - true if name, type, veclen and units of s are compatible
    */
   public boolean compatibleWith(DataArraySchema s, boolean checkComponentNames)
   {
      if (type != s.getType() || veclen != s.getVeclen())
         return false;
      if (unit != null && s.getUnit() != null && !unit.equals(s.getUnit()))
         return false;
      if (checkComponentNames)
         return type == s.getType() && veclen == s.getVeclen()
                 && ((name == null && s.getName() == null)
                 || (name != null && s.getName() != null && name.equals(s.getName())));
      else
         return true;
   }

   /**
    * Basic comparator for DataArraySchema compatibility
    * @param s - DataArraySchema to be checked for compatibility
    * @return  - true if name, type, veclen and units of s are compatible
    */
   public boolean compatibleWith(DataArraySchema s)
   {
      return compatibleWith(s, true);
   }

   /**
    * Get the value of userData
    *
    * @return the value of userData
    */
   public String[] getUserData()
   {
      return userData;
   }

   /**
    * Set the value of userData
    *
    * @param userData new value of userData
    */
   public void setUserData(String[] userData)
   {
      this.userData = userData;
   }

   /**
    * Get the value of userData at specified index
    *
    * @param index
    * @return the value of userData at specified index
    */
   public String getUserData(int index)
   {
      if (userData == null || index < 0 || index >= userData.length)
         return "";
      return this.userData[index];
   }

   /**
    * Set the value of userData at specified index.
    *
    * @param index
    * @param newUserData new value of userData at specified index
    */
   public void setUserData(int index, String newUserData)
   {
      this.userData[index] = newUserData;
   }
}
