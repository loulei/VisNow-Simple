 //<editor-fold defaultstate="collapsed" desc=" COPYRIGHT AND LICENSE ">
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
exception statement from your version.
*/
//</editor-fold>

package pl.edu.icm.visnow.lib.basic.readers.ReadVisNowField.utils;

import pl.edu.icm.visnow.datasets.CellSet;
import pl.edu.icm.visnow.datasets.DataContainer;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class ComponentIOSchema extends DataElementIOSchema
{
   
   
   protected int component  = 0;
   protected String cmpName = null;

   public ComponentIOSchema(DataContainer dataset, int component, int coord, int type, int veclen, int nData, int offsetFrom, int offsetTo)
   {
      super(dataset, coord, type, veclen, nData, offsetFrom, offsetTo);
      this.component = component;
   }
   
   @Override
   public String toString()
   {
      StringBuilder s = new StringBuilder("[");
      if (cmpName == null)   s.append("cmp ");
      if (dataset instanceof CellSet) s.append("" + ((CellSet)dataset).getName() + ".");
      if (cmpName == null)   s.append(""+ component);
      else                   s.append(cmpName);
      if (coord != -1)       s.append("."+ coord + " ");
                             s.append(" "+typeNames[type]);
                             s.append(" off " + offsetFrom);
      if (offsetTo != -1)    s.append("-" + offsetTo);
      return s.toString()+"]";                 
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

   public String getCmpName()
   {
      return cmpName;
   }

   public void setComponent(int component)
   {
      this.component = component;
   }

   public void setCmpName(String cmpName)
   {
      this.cmpName = cmpName;
   }
}
