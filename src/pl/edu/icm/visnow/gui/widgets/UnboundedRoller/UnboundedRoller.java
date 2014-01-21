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
 * SubRangeSlider.java
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
import pl.edu.icm.visnow.gui.events.FloatValueModificationEvent;
import pl.edu.icm.visnow.gui.events.FloatValueModificationListener;


/**
 *
 * @author  Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class UnboundedRoller extends JComponent implements ChangeListener, Serializable
{
   private UnboundedValueModel model;
   private Insets insets = new Insets(3, 3, 3, 3);
   
   /** Creates a new instance of SubRangeSlider */
   public UnboundedRoller()
   {
      init(new DefaultUnboundedValueModel());
   }
   
   protected final void init(UnboundedValueModel m)
   {
      setModel(m);
      setMinimumSize(new Dimension(60, 45));
      setPreferredSize(new Dimension(200, 55));
      UIManager.put(UnboundedRollerUI.UI_CLASS_ID, "BasicUnboundedRollerUI");
      updateUI();
   }
   
   
   
   public void setUI(UnboundedRollerUI ui)
   {
      super.setUI(ui);
   }
   
   @Override
   public void updateUI()
   {
      setUI(new BasicUnboundedRollerUI());
      invalidate();
   }
   
   @Override
   public String getUIClassID()
   {
      return UnboundedRollerUI.UI_CLASS_ID;
   }
   
   public void setModel(UnboundedValueModel m)
   {
      UnboundedValueModel old = model;
      if (old != null)
         old.removeChangeListener(this);
      
      if (m == null)
         model = new DefaultUnboundedValueModel();
      else
         model = m;
      model.addChangeListener(this);
      
      firePropertyChange("model", old, model);
   }
   
   public UnboundedValueModel getModel()
   {
      return model;
   }
   
   public void reset()
   {
      model.setValue(0);
      model.setSensitivity(1.f);
   }
   
   public void stateChanged(ChangeEvent e)
   {
      repaint();
   }
   
   public float getValue()
   {
      return model.getValue();
   }
   
   public void setValue(float v)
   {
      float old = getValue();
      if (v != old)
      {
         model.setValue(v);
         fireStateChanged();
      }
   }
   
   public void setOutValue(float v)
   {
      float old = getValue();
      if (v != old)
      model.setValue(v);
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
         fireStateChanged();
      }
   }
   
    public float getSensitivity() 
    {
       return model.getSensitivity();
    }

    public void setSensitivity(float m) {
        float old = getSensitivity();
        if (m != old) 
            model.setSensitivity(m);
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
    * Utility field holding list of ChangeListeners.
    */
   private transient ArrayList<FloatValueModificationListener> floatValueModificationListenerList = new ArrayList<FloatValueModificationListener>();

   /**
    * Registers ChangeListener to receive events.
    * @param listener The listener to register.
    */
   public synchronized void addFloatValueModificationListener(FloatValueModificationListener listener)
   {
      floatValueModificationListenerList.add(listener);
   }

   /**
    * Removes ChangeListener from the list of listeners.
    * @param listener The listener to remove.
    */
   public synchronized void removeChangeListener(FloatValueModificationListener listener)
   {
      floatValueModificationListenerList.remove(listener);
   }

   /**
    * Notifies all registered listeners about the event.
    *
    * @param object Parameter #1 of the <CODE>ChangeEvent<CODE> constructor.
    */
   private void fireStateChanged()
   {
      FloatValueModificationEvent e = new FloatValueModificationEvent(this, model.getValue(), model.isAdjusting());
      for (FloatValueModificationListener listener : floatValueModificationListenerList)
      {
         listener.floatValueChanged(e);
      }
   }
   
   private boolean enabled = true;

   @Override
   public void setEnabled(boolean enabled) {
       this.enabled = enabled;
       repaint();
   }
    
   @Override
   public boolean isEnabled() {
       return enabled;       
   }
   

}
