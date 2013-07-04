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

import java.util.ArrayList;
import javax.media.j3d.Transform3D;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.Matrix4f;

/**
 *
 * @author Krzysztof S. Nowinski
 * University of Warsaw, ICM
 */
public class TransformParams 
{
   protected Transform3D transform  = new Transform3D();
   protected boolean adjusting;

   /**
    * Get the value of adjusting
    *
    * @return the value of adjusting
    */
   public boolean isAdjusting()
   {
      return adjusting;
   }

   /**
    * Set the value of adjusting
    *
    * @param adjusting new value of adjusting
    */
   public void setAdjusting(boolean adjusting)
   {
      this.adjusting = adjusting;
   }

   public Transform3D getTransform()
   {
      return transform;
   }

   public void setTransform(Transform3D transform)
   {
      this.transform = transform;
      fireStateChanged();
   }

   public void resetTransform()
   {
      this.transform = new Transform3D();
      fireStateChanged();
   }

   public float[][] getMatrix()
   {
      Matrix4f tr = new Matrix4f();
      transform.get(tr);
      float[][] trMatrix = new float[4][4];
      for (int i = 0; i < trMatrix.length; i++)
         tr.getRow(i, trMatrix[i]);
      return trMatrix;
   }

   public void copy(TransformParams src)
   {
      transform = src.transform;
   }
    /**
    * Utility field holding list of ChangeListeners.
    */
   private transient ArrayList<ChangeListener> changeListenerList =
           new ArrayList<ChangeListener>();

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
      ChangeEvent e = new ChangeEvent(this);
      for (ChangeListener listener : changeListenerList)
         listener.stateChanged(e);
   }

}
