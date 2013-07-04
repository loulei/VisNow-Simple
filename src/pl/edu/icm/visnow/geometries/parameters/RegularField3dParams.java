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

package pl.edu.icm.visnow.geometries.parameters;

import java.awt.Color;
import java.util.ArrayList;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Krzysztof S. Nowinski
 *   <p>   University of Warsaw, ICM
 */
public class RegularField3dParams
{

   public static final int SLICE   = 0;
   public static final int AVG     = 1;
   public static final int MAX     = 2;
   public static final int STDDEV  = 3;
   public static final int GRID_TYPE_NONE    = 0;
   public static final int GRID_TYPE_OUTLINE = 1;
   public static final int GRID_TYPE_POINTS  = 2;
   public static final int GRID_TYPE_LINES   = 3;
   //<editor-fold defaultstate="collapsed" desc=" Active ">
   protected boolean active = true;

   public boolean isActive()
   {
      return active;
   }

   public void setActive(boolean active)
   {
      this.active = active;
      fireStateChanged();
   }
   //</editor-fold>
   protected boolean[][] surFaces  = {{false, false}, {false, false}, {false, false}};
   protected boolean[][] gridFaces = {{false, false}, {false, false}, {false, false}};
   protected float boxLineWidth = 1;
   protected Color boxColor = Color.LIGHT_GRAY;
   protected int dataMap = SLICE;
   protected int gridLines = 20;
   protected int gridType = GRID_TYPE_OUTLINE;

   /**
    * Get the value of dataMap
    *
    * @return the value of dataMap
    */
   public int getDataMap()
   {
      return dataMap;
   }

   /**
    * Set the value of dataMap
    *
    * @param dataMap new value of dataMap
    */
   public void setDataMap(int dataMap)
   {
      this.dataMap = dataMap;
      fireStateChanged();
   }

   /**
    * Get the value of boxColor
    *
    * @return the value of boxColor
    */
   public Color getBoxColor()
   {
      return boxColor;
   }

   /**
    * Set the value of boxColor
    *
    * @param boxColor new value of boxColor
    */
   public void setBoxColor(Color boxColor)
   {
      this.boxColor = boxColor;
      fireStateChanged();
   }

   /**
    * Get the value of boxLineWidth
    *
    * @return the value of boxLineWidth
    */
   public float getBoxLineWidth()
   {
      return boxLineWidth;
   }

   /**
    * Set the value of boxLineWidth
    *
    * @param boxLineWidth new value of boxLineWidth
    */
   public void setBoxLineWidth(float boxLineWidth)
   {
      this.boxLineWidth = boxLineWidth;
      fireStateChanged();
   }

   /**
    * Get the value of surFaces
    *
    * @return the value of surFaces
    */
   public boolean[][] isSurFaces()
   {
      return surFaces;
   }

   /**
    * Set the value of surFaces
    *
    * @param surFaces new value of surFaces
    */
   public void setSurFaces(boolean[][] surFaces)
   {
      this.surFaces = surFaces;
      fireStateChanged();
   }

   /**
    * Get the value of surFaces at specified [ind][side]
    *
    * @param ind
    * @param side
    * @return the value of surFaces at specified [ind][side]
    */
   public boolean isSurFaces(int ind, int side)
   {
      return this.surFaces[ind][side];
   }

   /**
    * Set the value of surFaces at specified ind][side.
    *
    * @param ind
    * @param side
    * @param newSurFaces new value of surFaces at specified [ind][side]
    */
   public void setSurFaces(int ind, int side, boolean newSurFaces)
   {
      this.surFaces[ind][side] = newSurFaces;
      fireStateChanged();
   }

   /**
    * Get the value of gridFaces
    *
    * @return the value of gridFaces
    */
   public boolean[][] isGridFaces()
   {
      return gridFaces;
   }

   /**
    * Set the value of gridFaces
    *
    * @param gridFaces new value of gridFaces
    */
   public void setGridFaces(boolean[][] gridFaces)
   {
      this.gridFaces = gridFaces;
      fireStateChanged();
   }

   /**
    * Get the value of gridFaces at specified [ind][side]
    *
    * @param ind
    * @param side
    * @return the value of gridFaces at specified ind][side
    */
   public boolean isGridFaces(int ind, int side)
   {
      return this.gridFaces[ind][side];
   }

   /**
    * Set the value of gridFaces at specified [ind][side].
    *
    * @param ind
    * @param side
    * @param newGridFaces new value of gridFaces at specified [ind][side]
    */
   public void setGridFaces(int ind, int side, boolean newGridFaces)
   {
      this.gridFaces[ind][side] = newGridFaces;
      fireStateChanged();
   }


   /**
    * Get the value of gridLines
    *
    * @return the value of gridLines
    */
   public int getGridLines()
   {
      return gridLines;
   }

   /**
    * Set the value of gridLines
    *
    * @param gridLines new value of gridLines
    */
   public void setGridLines(int gridLines)
   {
      this.gridLines = gridLines;
      fireStateChanged();
   }

   /**
    * Get the value of gridType
    *
    * @return the value of gridType
    */
   public int getGridType()
   {
      return gridType;
   }

   /**
    * Set the value of gridType
    *
    * @param gridType new value of gridType
    */
   public void setGridType(int gridType)
   {
      this.gridType = gridType;
      fireStateChanged();
   }
   //<editor-fold defaultstate="collapsed" desc=" Change listeners ">
   /**
    * Utility field holding list of ChangeListeners.
    */
   private transient ArrayList<ChangeListener> changeListenerList = new ArrayList<ChangeListener>();

   /**
    * Registers ChangeListener to receive events.
    * @param listener The listener to register.
    */
   public synchronized void addChangeListener(ChangeListener listener)
   {
      changeListenerList.add(listener);
   }

   /**
    * Removes ChangeListener from the list of listeners.
    * @param listener The listener to remove.
    */
   public synchronized void removeChangeListener(ChangeListener listener)
   {
      changeListenerList.remove(listener);
   }

   /**
    * Notifies all registered listeners about the event.
    *
    * @param object Parameter #1 of the <CODE>ChangeEvent<CODE> constructor.
    */
   public void fireStateChanged()
   {
      if (active)
      {
         ChangeEvent e = new ChangeEvent(this);
         for (ChangeListener listener : changeListenerList)
            listener.stateChanged(e);
      }
   }
   //</editor-fold>
}
