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

package pl.edu.icm.visnow.gui.widgets.UnboundedRoller;

import java.util.ArrayList;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pl.edu.icm.visnow.gui.events.FloatValueModificationEvent;
import pl.edu.icm.visnow.gui.events.FloatValueModificationListener;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class ExtendedUnboundedRoller extends javax.swing.JPanel
{
   private float initValue = 0;
   private float value = initValue;
   private int precision = 4;

   /** Creates new form ExtendedUnboundedRoller */
   public ExtendedUnboundedRoller()
   {
      initComponents();
      roller.addFloatValueModificationListener(new FloatValueModificationListener()
      {
         public void floatValueChanged(FloatValueModificationEvent e)
         {
            value = e.getVal();
            field.setText(String.format("%7."+precision+"f", e.getVal()));
            fireStateChanged();
         }
      });
   }

   /** This method is called from within the constructor to
    * initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is
    * always regenerated by the Form Editor.
    */
   @SuppressWarnings("unchecked")
   // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
   private void initComponents()
   {
      java.awt.GridBagConstraints gridBagConstraints;

      roller = new pl.edu.icm.visnow.gui.widgets.UnboundedRoller.UnboundedRoller();
      label = new javax.swing.JLabel();
      field = new javax.swing.JTextField();
      resetButton = new javax.swing.JButton();

      setMinimumSize(new java.awt.Dimension(160, 22));
      setPreferredSize(new java.awt.Dimension(200, 25));
      setLayout(new java.awt.GridBagLayout());

      roller.setMinimumSize(new java.awt.Dimension(60, 20));
      roller.setName("roller"); // NOI18N
      roller.setPreferredSize(new java.awt.Dimension(100, 20));

      javax.swing.GroupLayout rollerLayout = new javax.swing.GroupLayout(roller);
      roller.setLayout(rollerLayout);
      rollerLayout.setHorizontalGroup(
         rollerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGap(0, 54, Short.MAX_VALUE)
      );
      rollerLayout.setVerticalGroup(
         rollerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGap(0, 24, Short.MAX_VALUE)
      );

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.ipadx = 140;
      gridBagConstraints.ipady = 10;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
      add(roller, gridBagConstraints);

      label.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
      label.setText("label");
      label.setMaximumSize(new java.awt.Dimension(50, 15));
      label.setMinimumSize(new java.awt.Dimension(50, 15));
      label.setName("label"); // NOI18N
      label.setPreferredSize(new java.awt.Dimension(50, 15));
      label.setRequestFocusEnabled(false);
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
      add(label, gridBagConstraints);

      field.setText("0");
      field.setMaximumSize(new java.awt.Dimension(60, 19));
      field.setMinimumSize(new java.awt.Dimension(60, 19));
      field.setName("field"); // NOI18N
      field.setPreferredSize(new java.awt.Dimension(60, 19));
      field.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            fieldActionPerformed(evt);
         }
      });
      field.addFocusListener(new java.awt.event.FocusAdapter()
      {
         public void focusLost(java.awt.event.FocusEvent evt)
         {
            fieldFocusLost(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
      add(field, gridBagConstraints);

      resetButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pl/edu/icm/visnow/gui/icons/reset.png"))); // NOI18N
      resetButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
      resetButton.setMaximumSize(new java.awt.Dimension(18, 18));
      resetButton.setMinimumSize(new java.awt.Dimension(18, 18));
      resetButton.setName("resetButton"); // NOI18N
      resetButton.setPreferredSize(new java.awt.Dimension(18, 18));
      resetButton.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            resetButtonActionPerformed(evt);
         }
      });
      add(resetButton, new java.awt.GridBagConstraints());
   }// </editor-fold>//GEN-END:initComponents

   private void resetButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_resetButtonActionPerformed
   {//GEN-HEADEREND:event_resetButtonActionPerformed
       reset();
   }//GEN-LAST:event_resetButtonActionPerformed

   private void fieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_fieldActionPerformed
   {//GEN-HEADEREND:event_fieldActionPerformed
      try
      {
         value = Float.parseFloat(field.getText());
         roller.setOutValue(value);
         fireStateChanged();
      } catch (Exception e)
      {
      }
   }//GEN-LAST:event_fieldActionPerformed

   private void fieldFocusLost(java.awt.event.FocusEvent evt)//GEN-FIRST:event_fieldFocusLost
   {//GEN-HEADEREND:event_fieldFocusLost
      try
      {
         value = Float.parseFloat(field.getText());
         roller.setOutValue(value);
         fireStateChanged();
      } catch (Exception e)
      {
      }
   }//GEN-LAST:event_fieldFocusLost

   public void setLabel(String label)
   {
      this.label.setText(label);
   }

   public float getValue()
   {
      return value;
   }

   public void setValue(float value)
   {
      this.value = value;
      roller.setOutValue(value);
      field.setText(String.format("%7."+precision+"f", value));
      fireStateChanged();
   }

   public void setInitValue(float initValue)
   {
      this.initValue = initValue;
   }

   public void setPrecision(int precision)
   {
      if (precision < 0) precision = 0;
      if (precision > 5) precision = 5;
      this.precision = precision;
   }
   
   public void setSensitivity(float sensitivity)
   {
      roller.setSensitivity(sensitivity);
      double sl = Math.log10(sensitivity);
      if (sl < 0)
         precision = (int)(1-sl);
      else
         precision = 1;
   }

   public void reset()
   {
      value = initValue;
      roller.setOutValue(value);
      field.setText(String.format("%7."+precision+"f", value));
      fireStateChanged();
   }

   public boolean isAdjusting()
   {
      return roller.isAdjusting();
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

   private boolean enabled = true;
   
   @Override
   public void setEnabled(boolean enabled) {
       this.enabled = enabled;
       label.setEnabled(enabled);
       roller.setEnabled(enabled);
       field.setEnabled(enabled);
       resetButton.setEnabled(enabled);       
   }
    
   @Override
   public boolean isEnabled() {
       return enabled;       
   }

   // Variables declaration - do not modify//GEN-BEGIN:variables
   protected javax.swing.JTextField field;
   protected javax.swing.JLabel label;
   protected javax.swing.JButton resetButton;
   protected pl.edu.icm.visnow.gui.widgets.UnboundedRoller.UnboundedRoller roller;
   // End of variables declaration//GEN-END:variables
}
