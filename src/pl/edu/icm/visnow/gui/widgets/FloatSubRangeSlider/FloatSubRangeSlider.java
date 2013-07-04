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
 * FloatSubRangeSlider.java
 *
 * Created on April 13, 2004, 3:11 PM
 */

import java.awt.Dimension;
import java.awt.Insets;
import java.io.Serializable;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pl.edu.icm.visnow.lib.utils.VisNowCallTrace;

/**
 *
 * @author  Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class FloatSubRangeSlider extends JComponent implements ChangeListener, Serializable
{

   private FloatSubRangeModel model;
   private Insets insets = new Insets(3, 3, 3, 3);
   private boolean paintTicks = false;
   private boolean paintLabels = false;
   private boolean silentUpdate = false;
   /**
    * Holds value of property decimalScale.
    */
   private int decimalScale = 0;

   /** Creates a new instance of FloatSubRangeSlider */
   public FloatSubRangeSlider()
   {
      init(new DefaultFloatSubRangeModel());
   }

   public FloatSubRangeSlider(FloatSubRangeModel m)
   {
      init(m);
   }

   protected void init(FloatSubRangeModel m)
   {
      setModel(m);
      setMinimumSize(new Dimension(60, 45));
      setPreferredSize(new Dimension(200, 55));
      UIManager.put(FloatSubRangeSliderUI.UI_CLASS_ID, "BasicSubRangeSliderUI");
      updateUI();
   }

   public void setUI(FloatSubRangeSliderUI ui)
   {
      super.setUI(ui);
   }

   @Override
   public void updateUI()
   {
      setUI(new BasicFloatSubRangeSliderUI());
      invalidate();
   }

   @Override
   public String getUIClassID()
   {
      return FloatSubRangeSliderUI.UI_CLASS_ID;
   }
   
   @Override
   public String toString()
   {
      return model.toString();
   }

   public void setModel(FloatSubRangeModel m)
   {
      FloatSubRangeModel old = model;
      if (old != null)
         old.removeChangeListener(this);

      if (m == null)
         model = new DefaultFloatSubRangeModel();
      else
         model = m;
      model.addChangeListener(this);
      firePropertyChange("model", old, model);
   }

   public FloatSubRangeModel getModel()
   {
      return model;
   }

   public void reset()
   {
      model.setExtent(0);
      model.setBottomValue(model.getMinimum());
      model.setTopValue(model.getMaximum());
   }

   public void stateChanged(ChangeEvent e)
   {
      repaint();
//      if (model.getBottomValue() == model.getTopValue())
//      {
//      System.out.println(""+this);
//         VisNowCallTrace.trace();
//      }
      if (model.isBottomValueChanged() || model.isTopValueChanged())
         fireStateChanged();
      if (model.isBottomValueChanged())
         firePropertyChange("bottomValue", model.getLastBottomValue(), model.getBottomValue());
      if (model.isTopValueChanged())
         firePropertyChange("topValue", model.getLastTopValue(), model.getTopValue());
   }

   public float getBottomValue()
   {
      return model.getBottomValue();
   }

   public void setBottomValue(float v, boolean silent)
   {
      float old = getBottomValue();
      if (v != old)
      {
         model.setBottomValue(v);
         if (!silent)
         {
            firePropertyChange("bottomValue", old, v);
            fireStateChanged();
         }
      }
   }

   public void setBottomValue(float v, boolean silent, boolean force)
   {
      float old = getBottomValue();
      if (v != old || force)
      {
         model.setBottomValue(v);
         if (!silent)
         {
            firePropertyChange("bottomValue", old, v);
            fireStateChanged();
         }
      }
   }

   public void setBottomValue(float v)
   {
      setBottomValue(v, false);
   }

   public float getTopValue()
   {
      return model.getTopValue();
   }

   public void setTopValue(float v, boolean silent)
   {
      float old = getTopValue();
      if (v != old)
      {
         model.setTopValue(v);
         if (!silent)
         {
            firePropertyChange("topValue", old, v);
            fireStateChanged();
         }
      }
   }

   public void setTopValue(float v, boolean silent, boolean force)
   {
      float old = getTopValue();
      if (v != old || force)
      {
         model.setTopValue(v);
         if (!silent)
         {
            firePropertyChange("topValue", old, v);
            fireStateChanged();
         }
      }
   }

   public void setTopValue(float v)
   {
      setTopValue(v, false);
   }

   public void setValues(float min, float low, float up, float max)
   {
      if (!(min <= low && low <= up && up <= max) || low + model.getExtent() > up)
         return;
      model.setRangeProperties(low, up,
              model.getExtent(), model.getPolicy(), model.getExtensionPolicy(),
              min, max, false);

   }

   public boolean isAdjusting()
   {
      return model.isAdjusting();
   }

   public void setAdjusting(boolean b)
   {
      boolean old = isAdjusting();
      if (b != old)
      {
         model.setAdjusting(b);
         firePropertyChange("Adjusting", old, b);
      }
   }

   public float getExtent()
   {
      return model.getExtent();
   }

   public void setExtent(float v)
   {
      float old = getExtent();
      if (v != old)
      {
         model.setExtent(v);
         firePropertyChange("extent", old, v);
      }
   }

   public int getPolicy()
   {
      return model.getPolicy();
   }

   public void setPolicy(int v)
   {
      int old = getPolicy();
      if ((v != old)
              && (v <= FloatSubRangeModel.PUSH)
              && (v >= FloatSubRangeModel.STOP))
      {
         model.setPolicy(v);
         firePropertyChange("policy", old, v);
      }
   }

   public int getExtensionPolicy()
   {
      return model.getExtensionPolicy();
   }

   public void setExtensionPolicy(int v)
   {
      int old = getExtensionPolicy();
      if ((v != old)
              && (v >= FloatSubRangeModel.RESET)
              && (v <= FloatSubRangeModel.PULL))
      {
         model.setExtensionPolicy(v);
         firePropertyChange("extensionPolicy", old, v);
      }
   }

   public float getMinimum()
   {
      return model.getMinimum();
   }

   public void setMinimum(float m)
   {
      float old = getMinimum();
      if (m != old)
      {
         model.setMinimum(m);
         insets.left = 3 + 3 * (int) Math.log(Math.abs(m) + 1.);
         firePropertyChange("minimum", old, m);
      }
   }

   public float getMaximum()
   {
      return model.getMaximum();
   }

   public void setMaximum(float m)
   {
      float old = getMaximum();
      if (m != old)
      {
         model.setMaximum(m);
         insets.right = 3 + 3 * (int) Math.log(Math.abs(m) + 1.);
         firePropertyChange("maximum", old, m);
      }
   }
   
   public void setMinMax(float min, float max)
   {
      model.setMinMax(min, max);
   }

   public void setInsets(Insets i)
   {
      insets = i;
   }

   public void setInsets(int top, int left, int bottom, int right)
   {
      insets = new Insets(top, left, bottom, right);
   }

   @Override
   public Insets getInsets()
   {
      return insets;
   }

   /**
    * Getter for property paintLabels.
    * @return Value of property paintLabels.
    */
   public boolean isPaintLabels()
   {
      return paintLabels;
   }

   /**
    * Setter for property paintLabels.
    * @param paintLabels New value of property paintLabels.
    */
   public void setPaintLabels(boolean paintLabels)
   {
      boolean old = this.paintLabels;
      this.paintLabels = paintLabels;
      if (old != paintLabels)
         firePropertyChange("labels", old, paintLabels);
   }

   /**
    * Getter for property paintTicks.
    * @return Value of property paintTicks.
    */
   public boolean isPaintTicks()
   {
      return paintTicks;
   }

   /**
    * Setter for property paintTicks.
    * @param paintTicks New value of property paintTicks.
    */
   public void setPaintTicks(boolean paintTicks)
   {
      boolean old = this.paintTicks;
      this.paintTicks = paintTicks;
      if (old != paintTicks)
         firePropertyChange("ticks", old, paintTicks);
   }

   /**
    * Getter for property decimalScale.
    * @return Value of property decimalScale.
    */
   public int getDecimalScale()
   {
      return this.decimalScale;
   }

   /**
    * Setter for property decimalScale.
    * @param decimalScale New value of property decimalScale.
    */
   public void setDecimalScale(int decimalScale)
   {
      this.decimalScale = decimalScale;
   }

   public void setSilentUpdate(boolean silentUpdate)
   {
      this.silentUpdate = silentUpdate;
   }
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
   private void fireStateChanged()
   {
      if (silentUpdate)
         return;
      ChangeEvent e = new ChangeEvent(this);
      for (ChangeListener l : changeListenerList)
         l.stateChanged(e);
   }
   
   private boolean enabled = true;
   
   @Override
   public void setEnabled(boolean enabled) {
        boolean oldEnabled = isEnabled();
        super.setEnabled(enabled);
        firePropertyChange("enabled", oldEnabled, enabled);
        if (enabled != oldEnabled) {
            this.enabled = enabled;            
            repaint();
        }
       
   }
   
   @Override
   public boolean isEnabled() {
       return this.enabled;
   }
   
}
