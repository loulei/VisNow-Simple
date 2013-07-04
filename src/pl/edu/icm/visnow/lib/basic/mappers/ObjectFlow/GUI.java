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

package pl.edu.icm.visnow.lib.basic.mappers.ObjectFlow;

import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.render.FrameModificationEvent;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.render.FrameModificationListener;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.render.FrameRenderedListener;

public class GUI extends javax.swing.JPanel
{
   protected Params params = null;
   protected Field field = null;

   /** Creates new form EmptyVisnowModuleGUI */
   public GUI()
   {
      initComponents();
      animationPanel.addListener(new FrameModificationListener()
      {
         public void frameChanged(FrameModificationEvent e)
         {
            params.setAdjusting(animationPanel.isAdjusting());
            params.setFrame(e.getFrame());
            params.setTimeFrame(e.getTimeFrame());
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
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        animationPanel = new pl.edu.icm.visnow.lib.gui.AnimationPanel();
        jPanel1 = new javax.swing.JPanel();
        continuousUpdateBox = new javax.swing.JCheckBox();

        setPreferredSize(new java.awt.Dimension(220, 300));
        setLayout(new java.awt.GridBagLayout());

        animationPanel.setMaximumSize(new java.awt.Dimension(2147483647, 300));
        animationPanel.setMinimumSize(new java.awt.Dimension(170, 280));
        animationPanel.setPreferredSize(new java.awt.Dimension(220, 285));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        add(animationPanel, gridBagConstraints);

        jPanel1.setMinimumSize(new java.awt.Dimension(170, 20));
        jPanel1.setPreferredSize(new java.awt.Dimension(200, 120));
        jPanel1.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        add(jPanel1, gridBagConstraints);

        continuousUpdateBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pl/edu/icm/visnow/lib/basic/mappers/ObjectFlow/Bundle"); // NOI18N
        continuousUpdateBox.setText(bundle.getString("GUI.continuousUpdateBox.text")); // NOI18N
        continuousUpdateBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                continuousUpdateBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(continuousUpdateBox, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

   private void continuousUpdateBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_continuousUpdateBoxActionPerformed
   {//GEN-HEADEREND:event_continuousUpdateBoxActionPerformed
      animationPanel.setFrameSliderEnabled(!continuousUpdateBox.isSelected());
       params.setContinuousUpdate(continuousUpdateBox.isSelected());
   }//GEN-LAST:event_continuousUpdateBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private pl.edu.icm.visnow.lib.gui.AnimationPanel animationPanel;
    private javax.swing.JCheckBox continuousUpdateBox;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables

   public void setParams(Params params)
   {
      this.params = params;
   }

   public void setInData(Field field)
   {
      this.field = field;
      animationPanel.setNFrames(field.getNFrames());
      animationPanel.setTimeRange(field.getStartTime(), field.getEndTime());
      animationPanel.setTimeUnit(field.getTimeUnit());
      animationPanel.setFrame(0);
   }

   public FrameRenderedListener getFrameRenderedListener()
   {
      return animationPanel.getFrameRenderedListener();
   }

}