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

package pl.edu.icm.visnow.gui.widgets.FloatSubRangeSlider;
/*
 * DefaultFloatSubRangeModel.java
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
public class DefaultFloatSubRangeModel implements FloatSubRangeModel, Serializable
{
   /**
    * Only one <code>ChangeEvent</code> is needed per model instance since the
    * event's only (read-only) state is the source property.  The source
    * of events generated here is always "this".
    */
   protected transient ChangeEvent changeEvent = null;

   /** The listeners waiting for model changes. */
   protected EventListenerList listenerList = new EventListenerList();

   private float bottomValue = 0;
   private float lastBottomValue = 0;
   private float topValue = 100;
   private float lastTopValue = 100;
   private float extent = 0;
   private float min = 0;
   private float max = 100;
   private int policy = STOP;
   private int extensionPolicy = IGNORE;

   private boolean adjusting = false;
   private boolean bottomValueChanged = false;
   private boolean topValueChanged = false;

   /**
    * Initializes all of the properties with default values.
    * Those values are:
    * <ul>
    * <li><code>bottomValue</code> = 0
    * <li><code>topValue</code> = 100
    * <li><code>extent</code> = 0
    * <li><code>minimum</code> = 0
    * <li><code>maximum</code> = 100
    * <li><code>policy</code> = STOP
    * <li><code>adjusting</code> = false
    * </ul>
    */
   public DefaultFloatSubRangeModel()
   {
   }


   /**
    * Initializes value, extent, minimum and maximum. Adjusting is false.
    * Throws an <code>IllegalArgumentException</code> if the following
    * constraints aren't satisfied:
    * <pre>
    * min <= bottomValue <= value+extent <= topValue <= max
    * </pre>
    */
   public DefaultFloatSubRangeModel(float bottomValue, float topValue, float extent,
   int policy, float min, float max)
   {
      if ((max >= min) &&
      (bottomValue >= min) &&
      (extent >= 0) &&
      ((bottomValue + extent) <= topValue) &&
      (topValue <= max))
      {
         this.bottomValue = bottomValue;
         this.topValue = topValue;
         this.extent = extent;
         this.policy = policy;
         this.min = min;
         this.max = max;
      }
   }


   /**
    * Returns the model's current value.
    * @return the model's current value
    * @see #setValue
    * @see BoundedRangeModel#getValue
    */
   public float getBottomValue()
   {
      return bottomValue;
   }

   /**
    * Returns the model's current value.
    * @return the model's current value
    * @see #setValue
    * @see BoundedRangeModel#getValue
    */
   public float getTopValue()
   {
      return topValue;
   }


   /**
    * Returns the model's extent.
    * @return the model's extent
    * @see #setExtent
    * @see BoundedRangeModel#getExtent
    */
   public float getExtent()
   {
      return extent;
   }

   /**
    * Returns the model's policy.
    * @return the model's policy
    * @see #setPolicy
    */
   public int getPolicy()
   {
      return policy;
   }


   /**
    * Returns the model's minimum.
    * @return the model's minimum
    * @see #setMinimum
    * @see BoundedRangeModel#getMinimum
    */
   public float getMinimum()
   {
      return min;
   }


   /**
    * Returns the model's maximum.
    * @return  the model's maximum
    * @see #setMaximum
    * @see BoundedRangeModel#getMaximum
    */
   public float getMaximum()
   {
      return max;
   }

   public void setPolicy(int n)
   {
      if (n>=STOP && n<=PUSH)
         setRangeProperties(bottomValue, topValue, extent, n, extensionPolicy,
            min, max, adjusting);
    }


   /**
    * Sets the current topValue of the model. For a slider, that
    * determines where the knob appears. Ensures that the new
    * value, <I>newVal</I> falls within the model's constraints:
    * <pre>
    *     minimum <= value <= value+extent <= maximum
    * </pre>
    *
    * @see BoundedRangeModel#setValue
    */

   public void setTopValue(float n)
   {
      float newTopValue = Math.min(n, max);
      float newBottomValue=bottomValue;
      if (newTopValue-extent<bottomValue)
      {
         if (policy==STOP)
            newTopValue=bottomValue+extent;
         else
         {
            newBottomValue=newTopValue-extent;
            if (newBottomValue<min)
            {
               newBottomValue=min;
               newTopValue=newBottomValue+extent;
            }
         }
      }
      setRangeProperties(newBottomValue, newTopValue, extent, policy, extensionPolicy,
      min, max, adjusting);
   }

   /**
    * Sets the current bottomValue of the model. For a slider, that
    * determines where the knob appears. Ensures that the new
    * value, <I>newVal</I> falls within the model's constraints:
    * <pre>
    *     minimum <= value <= value+extent <= maximum
    * </pre>
    *
    * @see BoundedRangeModel#setValue
    */

   public void setBottomValue(float newVal)
   {
      float newBottomValue = Math.max(newVal, min);
      float newTopValue=topValue;
      if (newBottomValue+extent>topValue)
      {
         if (policy==STOP)
         {
            newBottomValue=topValue-extent;
         }
         else
         {
            newTopValue=newBottomValue+extent;
            if (newTopValue>max)
            {
               newTopValue=max;
               newBottomValue=newTopValue-extent;
            }
         }
      }
      setRangeProperties(newBottomValue, newTopValue, extent, policy, extensionPolicy,
      min, max, adjusting);
   }


   /**
    * Sets the extent to <I>newVal</I> after ensuring that <I>newVal</I>
    * is greater than or equal to zero and falls within the model's
    * constraints:
    * <pre>
    *     minimum <= value <= value+extent <= maximum
    * </pre>
    * @see BoundedRangeModel#setExtent
    */
   public void setExtent(float n)
   {
      float newExtent = Math.min(max-min,Math.max(0, n));
      float newBottomValue = bottomValue;
      float newTopValue = topValue;
      if(bottomValue + newExtent > max)
      {
         newBottomValue  = max - newExtent;
      }
      if (newTopValue<newBottomValue+newExtent)
         newTopValue=newBottomValue+newExtent;
      setRangeProperties(newBottomValue, newTopValue, newExtent, extensionPolicy,
                         policy, min, max, adjusting);
   }


   /**
    * Sets the minimum to <I>newVal</I> after ensuring that <I>newVal</I>
    * that the other three properties obey the model's constraints:
    * <pre>
    *     minimum <= value <= value+extent <= maximum
    * </pre>
    * @see #getMinimum
    * @see BoundedRangeModel#setMinimum
    */
   public void setMinimum(float n)
   {
      float newBottomValue;
      float newTopValue=topValue;
      float newMin = n;
      float newMax = Math.max(newMin, max);
      float newExtent = Math.min(newMax-newMin, extent);
//      if (newExtent < (newMax-newMin)/1000)
//         newExtent = (newMax-newMin)/1000;
      if (newExtent < 0)
         newExtent = 0;
      switch (extensionPolicy)
      {
         case RESET:
            newBottomValue = newMin;
            newTopValue = newMax;
            break;
         case IGNORE:
            newBottomValue = Math.max(bottomValue,newMin);
            break;
         case STRETCH:
            if (bottomValue <= min)
               newBottomValue = newMin;
            else
               newBottomValue = Math.max(bottomValue,newMin);
            break;
         case PULL:
            if (bottomValue <= min)
               newBottomValue = newMin;
            else
               newBottomValue = Math.max(bottomValue,newMin);
            newTopValue = newBottomValue+topValue-bottomValue;
            break;
         default:
            newBottomValue = Math.max(bottomValue,newMin);
            break;
      }
      if (newTopValue>newMax)
         newTopValue = newMax;

      if (newBottomValue+newExtent>newTopValue)
      {
         if (policy==STOP)
         {
            newBottomValue=Math.max(newTopValue-newExtent,newMin);
            newTopValue=newBottomValue+newExtent;
         }
         else
         {
            newTopValue=newBottomValue+newExtent;
            if (newTopValue>max)
            {
               newTopValue=max;
               newBottomValue=newTopValue-newExtent;
            }
         }
      }
      setRangeProperties(newBottomValue, newTopValue, newExtent, policy, extensionPolicy,
              newMin, newMax, adjusting);
   }


   /**
    * Sets the maximum to <I>newVal</I> and modifies <code>bottomValue, topValue</code>
    * according to the <code>extensionPolicy</code> ensuring that <I>newVal</I>
    * that the other three properties obey the model's constraints:
    * <pre>
    *     minimum <= value <= value+extent <= maximum
    * </pre>
    * @see BoundedRangeModel#setMaximum
    */
   public void setMaximum(float n)
   {
      float newTopValue;
      float newBottomValue=bottomValue;
      float newMax = n;
      float newMin = Math.min(newMax, min);
      float newExtent = Math.min(newMax-newMin, extent);
//      if (newExtent < (newMax-newMin)/1000)
//         newExtent = (newMax-newMin)/1000;
      if (newExtent < 0)
         newExtent = 0;
      switch (extensionPolicy)
      {
         case RESET:
            newBottomValue = newMin;
            newTopValue = newMax;
            break;
         case IGNORE:
            newTopValue = Math.min(topValue,newMax);
            break;
         case STRETCH:
            if (topValue >= max)
               newTopValue = newMax;
            else
               newTopValue = Math.min(topValue,newMax);
            break;
         case PULL:
            if (topValue >= max)
               newTopValue = newMax;
            else
               newTopValue = Math.min(topValue,newMax);
            newBottomValue = newTopValue-topValue+bottomValue;
            break;
         default:
            newTopValue = Math.min(topValue,newMax);
            break;
      }
      if (newBottomValue<newMin)
         newBottomValue = newMin;
      if (newBottomValue+newExtent>newTopValue)
      {
         if (policy==STOP)
         {
            newTopValue=Math.min(newBottomValue+newExtent, newMax);
            newBottomValue = newTopValue-newExtent;
         }
         else
         {
            newBottomValue=newTopValue-newExtent;
            if (newBottomValue<min)
            {
               newBottomValue=min;
               newTopValue=newBottomValue+newExtent;
            }
         }
      }
      setRangeProperties(newBottomValue, newTopValue, newExtent, policy, extensionPolicy,
                         newMin, newMax, adjusting);
   }

   public void setMinMax(float m, float n)
   {
      if (m>n)
         return;
      float newBottomValue;
      float newTopValue;
      float newMin = m;
      float newMax = n;
      if (bottomValue == min)
         newBottomValue = newMin;
      else
         newBottomValue = Math.max(bottomValue,newMin);
      if (topValue == max)
         newTopValue = newMax;
      else
         newTopValue = Math.min(topValue,newMin);
      float newExtent = Math.min(newMax-newMin, extent);
      if (newExtent < (newMax-newMin)/1000)
         newExtent = (newMax-newMin)/1000;
      if (newBottomValue+newExtent>newTopValue)
      {
         if (policy==STOP)
         {
            newBottomValue=Math.max(newTopValue-newExtent,newMin);
            newTopValue=newBottomValue+newExtent;
         }
         else
         {
            newTopValue=newBottomValue+newExtent;
            if (newTopValue>max)
            {
               newTopValue=max;
               newBottomValue=newTopValue-newExtent;
            }
         }
      }

      if (newBottomValue+newExtent>newTopValue)
      {
         if (policy==STOP)
         {
            newTopValue=Math.min(newBottomValue+newExtent, newMax);
            newBottomValue = newTopValue-newExtent;
         }
         else
         {
            newBottomValue=newTopValue-newExtent;
            if (newBottomValue<min)
            {
               newBottomValue=min;
               newTopValue=newBottomValue+newExtent;
            }
         }
      }
      setRangeProperties(newBottomValue, newTopValue, newExtent, policy, extensionPolicy,
                         newMin, newMax, adjusting);
   }


   /**
    * Sets all of the <code>BoundedRangeModel</code> properties after forcing
    * the arguments to obey the usual constraints:
    * <pre>
    *     minimum <= bo   ttomValue <= bottomValue+extent <= topValue <= maximum
    * </pre>
    * <p>
    * At most, one <code>ChangeEvent</code> is generated.
    *
    * @see BoundedRangeModel#setRangeProperties
    * @see #setValue
    * @see #setExtent
    * @see #setMinimum
    * @see #setMaximum
    * @see #setValueIsAdjusting
    */
   public void setRangeProperties(float newBottomValue, float newTopValue,
   float newExtent, int newPolicy, int newExtensionPolicy, float newMin, float newMax,
   boolean newAdjusting)
   {
      boolean isChange =
      (newBottomValue != bottomValue) ||
      (newTopValue != topValue) ||
      (newExtent != extent) ||
      (newPolicy != policy) ||
      (newExtensionPolicy != extensionPolicy) ||
      (newMin != min) ||
      (newMax != max) ||
      (newAdjusting != adjusting);

      bottomValueChanged = (newBottomValue != bottomValue);
      topValueChanged    = (newTopValue != topValue);
      if (isChange)
      {
         lastBottomValue = bottomValue;
         bottomValue = newBottomValue;
         lastTopValue = topValue;
         topValue = newTopValue;
         extent = newExtent;
         policy = newPolicy;
         extensionPolicy = newExtensionPolicy;
         min = newMin;
         max = newMax;
         adjusting = newAdjusting;
         if (min >= max)
            max = min + .0001f;
         fireStateChanged();
      }
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
      return listenerList.getListeners(ChangeListener.class);
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
      String strPol = null;
      if (policy==STOP)
         strPol="STOP";
      else
         strPol="PUSH";
      String strExtPol = null;
      
      String modelString =
      "bottom =" + getBottomValue() + ", " +
      "top    =" + getTopValue() + ", " +
      "ext    =" + getExtent() + ", " +
      "policy =" + strPol + ", " +
      "ext pol=" + strExtPol + ", " +
      "min =" + getMinimum() + ", " +
      "max =" + getMaximum() + ", " +
      "adj =" + isAdjusting();

      return "DefaultFloatSubRangeModel" + "[" + modelString + "]";
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
      setRangeProperties(bottomValue, topValue, extent, policy, extensionPolicy, min, max, b);
   }

   public boolean isBottomValueChanged()
   {
      return bottomValueChanged;
   }

   public boolean isTopValueChanged()
   {
      return topValueChanged;
   }

   public float getLastBottomValue()
   {
      return lastBottomValue;
   }

   public float getLastTopValue()
   {
      return lastTopValue;
   }

   public int getExtensionPolicy()
   {
      return extensionPolicy;
   }

   public void setExtensionPolicy(int n)
   {
      if (n>=RESET && n<=PULL)
         setRangeProperties(bottomValue, topValue, extent, policy, n,
            min, max, adjusting);
   }

}

