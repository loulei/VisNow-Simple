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

package pl.edu.icm.visnow.datamaps;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import pl.edu.icm.visnow.datamaps.colormap1d.DefaultColorMap1D;
import pl.edu.icm.visnow.datamaps.colormap2d.ColorMap2D;

/**
 * @author  Michał Łyczek (lyczek@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class ColorMapComboboxModel implements ComboBoxModel
{

   private int dim = 1;
   private int index = 0;

   public void setSelectedItem(Object anItem)
   {
      switch (dim)
      {
      case 1:
         index = ColorMapManager.getInstance().getColorMap1DIndex((DefaultColorMap1D) anItem);
         break;
      case 2:
      default:
         index = ColorMapManager.getInstance().getColorMap2DIndex((ColorMap2D) anItem);
      }
      if (index < 0) index = 0;
   }

   public Object getSelectedItem()
   {
      switch (dim)
      {
      case 1:
         return ColorMapManager.getInstance().getColorMap1D(index);
      case 2:
      default:
         return ColorMapManager.getInstance().getColorMap2D(index);
      }
   }

   public int getSize()
   {
      switch (dim)
      {
      case 1:
         return ColorMapManager.getInstance().getColorMap1DCount();
      case 2:
      default:
         return ColorMapManager.getInstance().getColorMap2DCount();
      }
   }

   public Object getElementAt(int index)
   {
      switch (dim)
      {
      case 1:
         return ColorMapManager.getInstance().getColorMap1D(index);
      case 2:
      default:
         return ColorMapManager.getInstance().getColorMap2D(index);
      }
   }

   public ColorMapComboboxModel(int dim)
   {
      this.dim = dim;
      this.listDataListeners = new Vector<ListDataListener>();
      ColorMapManager.getInstance().propertyChangeSupport.addPropertyChangeListener(new PropertyChangeListener()
      {
         public void propertyChange(PropertyChangeEvent evt)
         {
            fireListDataListeners();
         }
      });
   }
   
   final protected Vector<ListDataListener> listDataListeners;

   public void fireListDataListeners()
   {
      for (ListDataListener l : listDataListeners)
      {
         switch (dim)
         {
         case 1:
            l.intervalAdded(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, ColorMapManager.getInstance().getColorMap1DCount()));
            break;
         case 2:
            l.intervalAdded(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, ColorMapManager.getInstance().getColorMap2DCount()));
         }
      }
   }

   public void addListDataListener(ListDataListener l)
   {
      listDataListeners.add(l);
   }

   public void removeListDataListener(ListDataListener l)
   {
      listDataListeners.remove(l);
   }
}
