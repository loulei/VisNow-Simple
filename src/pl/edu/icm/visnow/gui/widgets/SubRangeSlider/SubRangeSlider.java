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
exception statement from your version. */
//</editor-fold>


package pl.edu.icm.visnow.gui.widgets.SubRangeSlider;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author  Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class SubRangeSlider extends JComponent implements ChangeListener, Serializable
{

   private SubRangeModel model;
   private Insets insets = new Insets(3, 3, 3, 3);
   private boolean paintTicks = false;
   private boolean paintLabels = false;
   Map<Integer,String> valLabels = null;
   
   /**
    * Holds value of property decimalScale.
    */
   private int decimalScale = 0;

   /** Creates a new instance of SubRangeSlider */
   public SubRangeSlider()
   {
      init(new DefaultSubRangeModel());
      setFont(new Font(Font.DIALOG, Font.PLAIN, 9));
   }

   public SubRangeSlider(SubRangeModel m)
   {
      init(m);
      setFont(new Font(Font.DIALOG, Font.PLAIN, 9));
   }

   protected final void init(SubRangeModel m)
   {
      setModel(m);
      setMinimumSize(new Dimension(60, 45));
      setPreferredSize(new Dimension(200, 55));
      UIManager.put(SubRangeSliderUI.UI_CLASS_ID, "BasicSubRangeSliderUI");
      updateUI();
   }

   public void setUI(SubRangeSliderUI ui)
   {
      super.setUI(ui);
   }

   @Override
   public void updateUI()
   {
//      setUI((SubRangeSliderUI)UIManager.getUI(this));
      setUI(new BasicSubRangeSliderUI());
      invalidate();
   }

   @Override
   public String getUIClassID()
   {
      return SubRangeSliderUI.UI_CLASS_ID;
   }
 
   @Override
   public String toString()
   {
      return model.toString();
   }
   
   public void setModel(SubRangeModel m)
   {
      SubRangeModel old = model;
      if (old != null)
         old.removeChangeListener(this);

      if (m == null)
         model = new DefaultSubRangeModel();
      else
         model = m;
      model.addChangeListener(this);
      firePropertyChange("model", old, model);
   }

   public SubRangeModel getModel()
   {
      return model;
   }

   public void reset()
   {
      model.setExtent(0);
      model.setBottomValue(model.getMinimum());
      model.setTopValue(model.getMaximum());
   }

   @Override
   public void stateChanged(ChangeEvent e)
   {
      repaint();
      if (model.isBottomValueChanged() || model.isTopValueChanged())
         fireStateChanged();
      if (model.isBottomValueChanged())
         firePropertyChange("bottomValue", model.getLastBottomValue(), model.getBottomValue());
      if (model.isTopValueChanged())
         firePropertyChange("topValue", model.getLastTopValue(), model.getTopValue());
   }

   public int getBottomValue()
   {
      return model.getBottomValue();
   }

   public void setBottomValue(int v, boolean silent)
   {
      int old = getBottomValue();
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

   public void setBottomValue(int v, boolean silent, boolean force)
   {
      int old = getBottomValue();
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

   public void setBottomValue(int v)
   {
      setBottomValue(v, false);
   }

   public int getTopValue()
   {
      return model.getTopValue();
   }

   public void setTopValue(int v, boolean silent)
   {
      int old = getTopValue();
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

   public void setTopValue(int v, boolean silent, boolean force)
   {
      int old = getTopValue();
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

   public void setTopValue(int v)
   {
      setTopValue(v, false);
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

   public int getExtent()
   {
      return model.getExtent();
   }

   public void setExtent(int v)
   {
      int old = getExtent();
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
              && (v <= SubRangeModel.PUSH)
              && (v >= SubRangeModel.STOP))
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
              && (v >= SubRangeModel.RESET)
              && (v <= SubRangeModel.PULL))
      {
         model.setExtensionPolicy(v);
         firePropertyChange("extensionPolicy", old, v);
      }
   }

   public int getMinimum()
   {
      return model.getMinimum();
   }

   public void setMinimum(int m)
   {
      int old = getMinimum();
      if (m != old)
      {
         model.setMinimum(m);
         insets.left = 3 + 3 * (int) Math.log(Math.abs(m) + 1.);
         firePropertyChange("minimum", old, m);
      }
   }

   public int getMaximum()
   {
      return model.getMaximum();
   }

   public void setMaximum(int m)
   {
      int old = getMaximum();
      if (m != old)
      {
         model.setMaximum(m);
         insets.right = 3 + 3 * (int) Math.log(Math.abs(m) + 1.);
         firePropertyChange("maximum", old, m);
      }
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
         firePropertyChange("ticks", old, paintLabels);
   }

   public Map<Integer, String> getValLabels()
   {
      return valLabels;
   }

   public void setValLabels(Map<Integer, String> valLabels)
   {
      this.valLabels = valLabels;
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
      ChangeEvent e = new ChangeEvent(this);
      for (ChangeListener l : changeListenerList)
         l.stateChanged(e);
   }
}
