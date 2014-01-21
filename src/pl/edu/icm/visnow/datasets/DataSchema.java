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

import pl.edu.icm.visnow.datasets.dataarrays.DataArraySchema;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Vector;

/**
 *
 * @author  Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class DataSchema implements Serializable
{
   /**
    * a list holding schemas of all data components in the field
    */
   protected ArrayList<DataArraySchema> componentSchemas = new ArrayList<DataArraySchema>();
   
   /**
    * adds new schema to componentSchemas
    * @param	s	New schema
    */
   public void addDataArraySchema(DataArraySchema s)
   {
      componentSchemas.add(s);
   }
   
    public void removeDataArraySchema(DataArraySchema s) {
        componentSchemas.remove(s);        
    }
   
   /**
    * extracts schemas from data components and stores them in <code>componentSchemas</code>
    * 
    * @param	data	The data.
    */
   public void getSchemasFromData(ArrayList<DataArray> data)
   {
      componentSchemas.clear(); 
      for (int i = 0; i < data.size(); i++)
         componentSchemas.add(data.get(i).getSchema());
   }

   public DataArraySchema getSchema(int i)
   {
      if (i < 0 || i >= componentSchemas.size())
         return null;
      return componentSchemas.get(i);
   }

   /**
    * Getter for the componentSchemas property
    * 
    * @return	<code>>componentSchemas</code> vector.
    */
   public ArrayList<DataArraySchema> getComponentSchemas() {
      return componentSchemas;
   }

   /**
    * setter for the componentSchemas property
    * @param	componentSchemas - a <code>ArrayList<lt>DataArraySchema<gt></code>	
    * vector holding new value for componentSchemas.
    */
   public void setComponentSchemas(ArrayList<DataArraySchema> componentSchemas) {
      this.componentSchemas = componentSchemas;
   }

   /**
    * @param	s compared <code>DataSchema</code>
    * @param	checkComponentNames - flag to include components name checking
     * @param checkComponentRanges
    * @return	<code>true</code> if dataComponents are item by item compatible with s,  <code>false</code> otherwise.
    */
   public boolean isDataCompatibleWith(DataSchema s, boolean checkComponentNames, boolean checkComponentRanges)
   {
      if (s == null)
         return false;

      if (componentSchemas.size() != s.getComponentSchemas().size())
         return false;
      for (int i = 0; i < componentSchemas.size(); i++)
         if (!componentSchemas.get(i).compatibleWith(s.getComponentSchemas().get(i), checkComponentNames, checkComponentRanges))
            return false;
      return true;
   }

   /**
    * @param	s compared <code>DataSchema</code>
    * @param	checkComponentNames - flag to include components name checking
    * @return	<code>true</code> if dataComponents are item by item compatible with s,  <code>false</code> otherwise.
    */
   public boolean isDataCompatibleWith(DataSchema s, boolean checkComponentNames)
   {
      if (s == null)
         return false;

      if (componentSchemas.size() != s.getComponentSchemas().size())
         return false;
      for (int i = 0; i < componentSchemas.size(); i++)
         if (!componentSchemas.get(i).compatibleWith(s.getComponentSchemas().get(i), checkComponentNames))
            return false;
      return true;
   }

   /**
    * @param	s compared <code>DataSchema</code>
    * @return	<code>true</code> if dataComponents are item by item compatible with s,  <code>false</code> otherwise.
    */
   public boolean isDataCompatibleWith(DataSchema s) {
       return isDataCompatibleWith(s, true);
   }


}
