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

package pl.edu.icm.visnow.lib.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;

/**
 *
 * @author Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class DownsizeUI extends javax.swing.JPanel
{

   private int[] downsize =
   {
      3, 3, 3
   };
   private int[] defaultDownsize =
   {
      3, 3, 3
   };
   private boolean active = true;

   /**
    * Creates new form DownsizeUI
    */
   public DownsizeUI()
   {
      initComponents();
   }

   public void setPresentation(boolean simple)
   {
      GridBagConstraints gridBagConstraints;
      Dimension simpleDim = new Dimension(200, 45);
      Dimension expertDim = new Dimension(220, 60);
      if (simple)
      {
         this.removeAll();
         setMinimumSize(simpleDim);
         setPreferredSize(simpleDim);
         setMaximumSize(simpleDim);

         gridBagConstraints = new java.awt.GridBagConstraints();
         gridBagConstraints.gridx = 1;
         gridBagConstraints.gridy = 0;
         gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
         gridBagConstraints.weightx = 1.0;
         gridBagConstraints.weighty = 1.0;
         gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
         add(noDownButton, gridBagConstraints);

         gridBagConstraints = new java.awt.GridBagConstraints();
         gridBagConstraints.gridx = 0;
         gridBagConstraints.gridy = 0;
         gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
         gridBagConstraints.weightx = 1.0;
         gridBagConstraints.weighty = 1.0;
         gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
         add(defaultButton, gridBagConstraints);
      } else
      {
         this.removeAll();
         setMinimumSize(expertDim);
         setPreferredSize(expertDim);
         setMaximumSize(expertDim);
         gridBagConstraints = new java.awt.GridBagConstraints();
         gridBagConstraints.gridx = 0;
         gridBagConstraints.gridy = 0;
         gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 8);
         add(jLabel1, gridBagConstraints);

         jLabel2.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
         jLabel2.setText("y");
         gridBagConstraints = new java.awt.GridBagConstraints();
         gridBagConstraints.gridx = 2;
         gridBagConstraints.gridy = 0;
         gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 8);
         add(jLabel2, gridBagConstraints);

         jLabel3.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
         jLabel3.setText("z");
         gridBagConstraints = new java.awt.GridBagConstraints();
         gridBagConstraints.gridx = 4;
         gridBagConstraints.gridy = 0;
         gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 8);
         add(jLabel3, gridBagConstraints);

         gridBagConstraints = new java.awt.GridBagConstraints();
         gridBagConstraints.gridx = 1;
         gridBagConstraints.gridy = 0;
         gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
         gridBagConstraints.weightx = 1.0;
         add(xSpinner, gridBagConstraints);

         ySpinner.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
         ySpinner.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(3), Integer.valueOf(1), null, Integer.valueOf(1)));
         ySpinner.addChangeListener(new javax.swing.event.ChangeListener()
         {
            public void stateChanged(javax.swing.event.ChangeEvent evt)
            {
               ySpinnerStateChanged(evt);
            }
         });
         gridBagConstraints = new java.awt.GridBagConstraints();
         gridBagConstraints.gridx = 3;
         gridBagConstraints.gridy = 0;
         gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
         gridBagConstraints.weightx = 1.0;
         add(ySpinner, gridBagConstraints);

         gridBagConstraints = new java.awt.GridBagConstraints();
         gridBagConstraints.gridx = 5;
         gridBagConstraints.gridy = 0;
         gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
         gridBagConstraints.weightx = 1.0;
         add(zSpinner, gridBagConstraints);

         gridBagConstraints = new java.awt.GridBagConstraints();
         gridBagConstraints.gridx = 2;
         gridBagConstraints.gridy = 1;
         gridBagConstraints.gridwidth = 2;
         gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
         gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
         add(noDownButton, gridBagConstraints);

         gridBagConstraints = new java.awt.GridBagConstraints();
         gridBagConstraints.gridx = 4;
         gridBagConstraints.gridy = 1;
         gridBagConstraints.gridwidth = 2;
         gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
         gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
         add(defaultButton, gridBagConstraints);

         gridBagConstraints = new java.awt.GridBagConstraints();
         gridBagConstraints.gridx = 0;
         gridBagConstraints.gridy = 1;
         gridBagConstraints.gridwidth = 2;
         gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
         gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
         add(autoBox, gridBagConstraints);
      }
      validate();
   }

   /**
    * This method is called from within the constructor to initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is always
    * regenerated by the Form Editor.
    */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        xSpinner = new javax.swing.JSpinner();
        ySpinner = new javax.swing.JSpinner();
        zSpinner = new javax.swing.JSpinner();
        noDownButton = new javax.swing.JButton();
        defaultButton = new javax.swing.JButton();
        autoBox = new javax.swing.JToggleButton();

        setBorder(javax.swing.BorderFactory.createTitledBorder("downsize"));
        setMinimumSize(new java.awt.Dimension(150, 55));
        setName(""); // NOI18N
        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(200, 60));
        setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel1.setText("x");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 8);
        add(jLabel1, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel2.setText("y");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 8);
        add(jLabel2, gridBagConstraints);

        jLabel3.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel3.setText("z");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 8);
        add(jLabel3, gridBagConstraints);

        xSpinner.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        xSpinner.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(3), Integer.valueOf(1), null, Integer.valueOf(1)));
        xSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                xSpinnerStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(xSpinner, gridBagConstraints);

        ySpinner.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        ySpinner.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(3), Integer.valueOf(1), null, Integer.valueOf(1)));
        ySpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                ySpinnerStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(ySpinner, gridBagConstraints);

        zSpinner.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        zSpinner.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(3), Integer.valueOf(1), null, Integer.valueOf(1)));
        zSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                zSpinnerStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(zSpinner, gridBagConstraints);

        noDownButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        noDownButton.setText("no down");
        noDownButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
        noDownButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                noDownButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        add(noDownButton, gridBagConstraints);

        defaultButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        defaultButton.setText("default");
        defaultButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
        defaultButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                defaultButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        add(defaultButton, gridBagConstraints);

        autoBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        autoBox.setSelected(true);
        autoBox.setText("dynamic");
        autoBox.setMargin(new java.awt.Insets(2, 2, 2, 2));
        autoBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        add(autoBox, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

   private void xSpinnerStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_xSpinnerStateChanged
   {//GEN-HEADEREND:event_xSpinnerStateChanged
      if (autoBox.isSelected() && active)
         fireStateChanged();
   }//GEN-LAST:event_xSpinnerStateChanged

   private void ySpinnerStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_ySpinnerStateChanged
   {//GEN-HEADEREND:event_ySpinnerStateChanged
      if (autoBox.isSelected() && active)
         fireStateChanged();
   }//GEN-LAST:event_ySpinnerStateChanged

   private void zSpinnerStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_zSpinnerStateChanged
   {//GEN-HEADEREND:event_zSpinnerStateChanged
      if (autoBox.isSelected() && active)
         fireStateChanged();
   }//GEN-LAST:event_zSpinnerStateChanged

   private void noDownButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_noDownButtonActionPerformed
   {//GEN-HEADEREND:event_noDownButtonActionPerformed
      for (int i = 0; i < downsize.length; i++)
         downsize[i] = 1;
      updateSpinners();
   }//GEN-LAST:event_noDownButtonActionPerformed

   private void autoBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_autoBoxActionPerformed
   {//GEN-HEADEREND:event_autoBoxActionPerformed
      if (autoBox.isSelected() && active)
         fireStateChanged();
   }//GEN-LAST:event_autoBoxActionPerformed

   private void defaultButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_defaultButtonActionPerformed
   {//GEN-HEADEREND:event_defaultButtonActionPerformed
      System.arraycopy(defaultDownsize, 0, this.downsize, 0, downsize.length);
      updateSpinners();
   }//GEN-LAST:event_defaultButtonActionPerformed

   public int[] getDownsize()
   {
      return downsize;
   }

   private void updateSpinners()
   {
      SwingInstancer.swingRun(new Runnable()
      {
         public void run()
         {
            active = false;
            xSpinner.setValue(DownsizeUI.this.downsize[0]);
            if (DownsizeUI.this.downsize.length > 1)
            {
               ySpinner.setValue(DownsizeUI.this.downsize[1]);
               ySpinner.setVisible(true);
            } else
               ySpinner.setVisible(false);
            if (DownsizeUI.this.downsize.length > 2)
            {
               zSpinner.setValue(DownsizeUI.this.downsize[2]);
               zSpinner.setVisible(true);
            } else
               zSpinner.setVisible(false);
            active = true;
            fireStateChanged();
         }
      });
   }

   public void setDownsize(int[] downsize)
   {
      if (downsize == null || downsize.length <= 0 || downsize.length > 3)
         return;
      this.downsize = new int[downsize.length];
      defaultDownsize = new int[downsize.length];
      System.arraycopy(downsize, 0, this.downsize, 0, downsize.length);
      System.arraycopy(downsize, 0, defaultDownsize, 0, downsize.length);
      updateSpinners();
   }
   /**
    * Utility field holding list of ChangeListeners.
    */
   private transient ArrayList<ChangeListener> changeListenerList = new ArrayList<ChangeListener>();

   /**
    * Registers ChangeListener to receive events.
    *
    * @param listener The listener to register.
    */
   public synchronized void addChangeListener(ChangeListener listener)
   {
      changeListenerList.add(listener);
   }

   /**
    * Removes ChangeListener from the list of listeners.
    *
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
      if (!active)
         return;
      ChangeEvent e = new ChangeEvent(this);
      downsize[0] = (Integer) xSpinner.getValue();
      if (downsize.length > 1)
         downsize[1] = (Integer) ySpinner.getValue();
      if (downsize.length > 2)
         downsize[2] = (Integer) zSpinner.getValue();
      for (ChangeListener listener : changeListenerList)
         listener.stateChanged(e);
   }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton autoBox;
    private javax.swing.JButton defaultButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JButton noDownButton;
    private javax.swing.JSpinner xSpinner;
    private javax.swing.JSpinner ySpinner;
    private javax.swing.JSpinner zSpinner;
    // End of variables declaration//GEN-END:variables
}
