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

package pl.edu.icm.visnow.gui.widgets.UnboundedRoller;
/*
 * DefaultSubRangeModel.java
 *
 * Created on April 10, 2004, 7:30 PM
 */
import javax.swing.*;
import javax.swing.event.*;
import java.io.Serializable;
import java.util.EventListener;

/**
 *
 * @author  Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class DefaultUnboundedValueModel implements UnboundedValueModel, Serializable
{
   /**
    * Only one <code>ChangeEvent</code> is needed per model instance since the
    * event's only (read-only) state is the source property.  The source
    * of events generated here is always "this".
    */
   protected transient ChangeEvent changeEvent = null;
   
   /** The listeners waiting for model changes. */
   protected EventListenerList listenerList = new EventListenerList();
   
   private float value = 0;
   private float sensitivity = 0.1f;
   private boolean adjusting = false;
   
   /**
    * Initializes all of the properties with default values.
    * Those values are:
    * <ul>
    * <li><code>value</code> = 0
    * <li><code>sensitivity</code> = .1
    * <li><code>adjusting</code> = false
    * </ul>
    */
   public DefaultUnboundedValueModel()
   {
   }
   
   
   
   /**
    * Returns the model's current value.
    * @return the model's current value
    * @see #setValue
    * @see UnboundedRangeModel#getValue
    */
   public float getValue()
   {
      return value;
   }
   
   
   /**
    * Returns the model's sensitivity.
    * @return the model's sensitivity
    * @see #setSensitivity
    * @see UnboundedRangeModel#getVensitivity
    */
   public float getSensitivity()
   {
      return sensitivity;
   }
   
   
   /**
    * Sets the current value of the model. 
    * @see UnboundedRangeModel#setValue
    */
   
   public void setValue(float newValue)
   {
      value = newValue;
   }
   
   /**
    * Sets the current sensitivity of the model. 
    * @see UnboundedRangeModel#setSensitivity
    */
   
   public void setSensitivity(float newSensitivity)
   {
       sensitivity = newSensitivity;
   }
   
   
   
   /**
    * Adds a <code>ChangeListener</code>.  The change listeners are run each
    * time any one of the Bounded Range model properties changes.
    *
    * @param l the ChangeListener to add
    * @see #removeChangeListener
    * @see BoundedRangeModel#addChangeListener
    */
   public void addChangeListener(ChangeListener l)
   {
      listenerList.add(ChangeListener.class, l);
   }
   
   
   /**
    * Removes a <code>ChangeListener</code>.
    *
    * @param l the <code>ChangeListener</code> to remove
    * @see #addChangeListener
    * @see BoundedRangeModel#removeChangeListener
    */
   public void removeChangeListener(ChangeListener l)
   {
      listenerList.remove(ChangeListener.class, l);
   }
   
   
   /**
    * Returns an array of all the change listeners
    * registered on this <code>DefaultBoundedRangeModel</code>.
    *
    * @return all of this model's <code>ChangeListener</code>s
    *         or an empty
    *         array if no change listeners are currently registered
    *
    * @see #addChangeListener
    * @see #removeChangeListener
    *
    * @since 1.4
    */
   public ChangeListener[] getChangeListeners()
   {
      return (ChangeListener[])listenerList.getListeners(
      ChangeListener.class);
   }
   
   
   /**
    * Runs each <code>ChangeListener</code>'s <code>stateChanged</code> method.
    *
    * @see #setRangeProperties
    * @see EventListenerList
    */
   protected void fireStateChanged()
   {
      Object[] listeners = listenerList.getListenerList();
      for (int i = listeners.length - 2; i >= 0; i -=2 )
      {
         if (listeners[i] == ChangeListener.class)
         {
            if (changeEvent == null)
            {
               changeEvent = new ChangeEvent(this);
            }
            ((ChangeListener)listeners[i+1]).stateChanged(changeEvent);
         }
      }
   }
   
   
   /**
    * Returns a string that displays all of the
    * <code>BoundedRangeModel</code> properties.
    */
   @Override
   public String toString()
   {

      String modelString =
      "value=" + getValue() + ", " +
      "sensitivity=" + getSensitivity() + ", " +
      "adj=" + isAdjusting();
      
      return getClass().getName() + "[" + modelString + "]";
   }
   
   /**
    * Returns an array of all the objects currently registered as
    * <code><em>Foo</em>Listener</code>s
    * upon this model.
    * <code><em>Foo</em>Listener</code>s
    * are registered using the <code>add<em>Foo</em>Listener</code> method.
    * <p>
    * You can specify the <code>listenerType</code> argument
    * with a class literal, such as <code><em>Foo</em>Listener.class</code>.
    * For example, you can query a <code>DefaultBoundedRangeModel</code>
    * instance <code>m</code>
    * for its change listeners
    * with the following code:
    *
    * <pre>ChangeListener[] cls = (ChangeListener[])(m.getListeners(ChangeListener.class));</pre>
    *
    * If no such listeners exist,
    * this method returns an empty array.
    *
    * @param listenerType  the type of listeners requested;
    *          this parameter should specify an interface
    *          that descends from <code>java.util.EventListener</code>
    * @return an array of all objects registered as
    *          <code><em>Foo</em>Listener</code>s
    *          on this model,
    *          or an empty array if no such
    *          listeners have been added
    * @exception ClassCastException if <code>listenerType</code> doesn't
    *          specify a class or interface that implements
    *          <code>java.util.EventListener</code>
    *
    * @see #getChangeListeners
    *
    * @since 1.3
    */
   public <T extends EventListener> T[] getListeners(Class<T> listenerType)
   {
      return listenerList.getListeners(listenerType);
   }
   
   
   /**
    * Returns true if the value is in the process of changing
    * as a result of actions being taken by the user.
    *
    * @return the value of the <code>adjusting</code> property
    * @see #setValue
    * @see BoundedRangeModel#getValueIsAdjusting
    */
   public boolean isAdjusting()
   {
      return adjusting;
   }
   
   
   /**
    * Sets the <code>adjusting</code> property.
    *
    * @see #getValueIsAdjusting
    * @see #setValue
    * @see BoundedRangeModel#setValueIsAdjusting
    */
   public void setAdjusting(boolean b)
   {
      adjusting = b;
   }
   
   
}

