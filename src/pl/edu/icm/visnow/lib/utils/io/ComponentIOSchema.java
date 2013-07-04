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

package pl.edu.icm.visnow.lib.utils.io;

import pl.edu.icm.visnow.datasets.dataarrays.DataArray;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class ComponentIOSchema
{

   public static final int COORDS = -1;
   public static final int MASK   = -2;
   public static final int GLOBAL = -1;
   protected String[] typeNames = {"boolean","byte","short","int","float","double","complex","string"};
   protected int dataset    = GLOBAL;
   protected int component  = 0;
   protected String cmpName = null;
   protected int coord      = -1;
   protected int type       = DataArray.FIELD_DATA_FLOAT;
   protected int offsetFrom = 0;
   protected int offsetTo   = -1;
   protected int current    = 0;
   

   public ComponentIOSchema(int dataset, int component, int coord, int type, int offsetFrom, int offsetTo)
   {
      this.dataset    = dataset;
      this.component  = component;
      this.coord      = coord;
      this.type       = type;
      this.offsetFrom = offsetFrom;
      this.offsetTo   = offsetTo;
   }
   
   public String toString()
   {
      StringBuffer s = new StringBuffer("[");
      if (cmpName == null)   s.append("cmp ");
      if (dataset != GLOBAL) s.append("" + dataset + ".");
      if (cmpName == null)   s.append(""+ component);
      else                   s.append(cmpName);
      if (coord != -1)       s.append("."+ coord + " ");
                             s.append(" "+typeNames[type]);
                             s.append(" off " + offsetFrom);
      if (offsetTo != -1)    s.append("-" + offsetTo);
      return s.toString()+"]";                 
   }

   /**
    * Get the value of offsetTo
    *
    * @return the value of offsetTo
    */
   public int getOffsetTo()
   {
      return offsetTo;
   }

   /**
    * Set the value of offsetTo
    *
    * @param offsetTo new value of offsetTo
    */
   public void setOffsetTo(int offsetTo)
   {
      this.offsetTo = offsetTo;
   }

   /**
    * Get the value of type
    *
    * @return the value of type
    */
   public int getType()
   {
      return type;
   }

   /**
    * Set the value of type
    *
    * @param type new value of type
    */
   public void setType(int type)
   {
      this.type = type;
   }

   /**
    * Get the value of offsetFrom
    *
    * @return the value of offsetFrom
    */
   public int getOffsetFrom()
   {
      return offsetFrom;
   }

   /**
    * Set the value of offsetFrom
    *
    * @param offsetFrom new value of offsetFrom
    */
   public void setOffsetFrom(int offsetFrom)
   {
      this.offsetFrom = offsetFrom;
   }

   /**
    * Get the value of coord
    *
    * @return the value of coord
    */
   public int getCoord()
   {
      return coord;
   }

   /**
    * Set the value of coord
    *
    * @param coord new value of coord
    */
   public void setCoord(int coord)
   {
      this.coord = coord;
   }

   /**
    * Get the value of component
    *
    * @return the value of component
    */
   public int getComponent()
   {
      return component;
   }

   /**
    * Set the value of component
    *
    * @param component new value of component
    */
   public void setComponent(int component)
   {
      this.component = component;
   }

   /**
    * Get the value of dataset
    *
    * @return the value of dataset
    */
   public int getDataset()
   {
      return dataset;
   }

   /**
    * Set the value of dataset
    *
    * @param dataset new value of dataset
    */
   public void setDataset(int dataset)
   {
      this.dataset = dataset;
   }

   public int getCurrent()
   {
      return current;
   }

   public void setCurrent(int current)
   {
      this.current = current;
   }

   public String getCmpName()
   {
      return cmpName;
   }

   public void setCmpName(String cmpName)
   {
      this.cmpName = cmpName;
   }

}
