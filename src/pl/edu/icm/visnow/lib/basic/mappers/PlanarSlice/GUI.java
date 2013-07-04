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

package pl.edu.icm.visnow.lib.basic.mappers.PlanarSlice;


import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;
import pl.edu.icm.visnow.datasets.Field;

public class GUI extends javax.swing.JPanel
{

   protected Params params = new Params();
   protected Field inField;
   protected boolean active = true;
   protected int axis = 2;
   protected float[] coeffs = new float[3];
   protected float[] center;

   /** Creates new form EmptyVisnowModuleGUI */
   public GUI()
   {
      initComponents();
      xRotRoller.setLabel("x rotate");
      yRotRoller.setLabel("y rotate");
      zRotRoller.setLabel("z rotate");
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

      buttonGroup1 = new javax.swing.ButtonGroup();
      buttonGroup2 = new javax.swing.ButtonGroup();
      jPanel2 = new javax.swing.JPanel();
      xButton = new javax.swing.JRadioButton();
      yButton = new javax.swing.JRadioButton();
      zButton = new javax.swing.JRadioButton();
      transformResetButton = new javax.swing.JButton();
      xRotRoller = new pl.edu.icm.visnow.gui.widgets.UnboundedRoller.ExtendedUnboundedRoller();
      yRotRoller = new pl.edu.icm.visnow.gui.widgets.UnboundedRoller.ExtendedUnboundedRoller();
      zRotRoller = new pl.edu.icm.visnow.gui.widgets.UnboundedRoller.ExtendedUnboundedRoller();
      jPanel1 = new javax.swing.JPanel();
      originSlider = new pl.edu.icm.visnow.gui.widgets.FloatSlider();
      jPanel3 = new javax.swing.JPanel();
      belowButton = new javax.swing.JRadioButton();
      sliceButton = new javax.swing.JRadioButton();
      aboveButton = new javax.swing.JRadioButton();

      setLayout(new java.awt.GridBagLayout());

      jPanel2.setLayout(new java.awt.GridLayout(1, 0));

      buttonGroup1.add(xButton);
      java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pl/edu/icm/visnow/lib/basic/mappers/PlanarSlice/Bundle"); // NOI18N
      xButton.setText(bundle.getString("GUI.xButton.text")); // NOI18N
      xButton.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            xButtonActionPerformed(evt);
         }
      });
      jPanel2.add(xButton);

      buttonGroup1.add(yButton);
      yButton.setText(bundle.getString("GUI.yButton.text")); // NOI18N
      yButton.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            yButtonActionPerformed(evt);
         }
      });
      jPanel2.add(yButton);

      buttonGroup1.add(zButton);
      zButton.setSelected(true);
      zButton.setText(bundle.getString("GUI.zButton.text")); // NOI18N
      zButton.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            zButtonActionPerformed(evt);
         }
      });
      jPanel2.add(zButton);

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      add(jPanel2, gridBagConstraints);

      transformResetButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
      transformResetButton.setText("reset transform");
      transformResetButton.setMargin(new java.awt.Insets(2, 1, 2, 1));
      transformResetButton.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            transformResetButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 6;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
      add(transformResetButton, gridBagConstraints);

      xRotRoller.setName(""); // NOI18N
      xRotRoller.addChangeListener(new javax.swing.event.ChangeListener()
      {
         public void stateChanged(javax.swing.event.ChangeEvent evt)
         {
            xRotRollerStateChanged(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.weightx = 1.0;
      add(xRotRoller, gridBagConstraints);

      yRotRoller.addChangeListener(new javax.swing.event.ChangeListener()
      {
         public void stateChanged(javax.swing.event.ChangeEvent evt)
         {
            yRotRollerStateChanged(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 3;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      add(yRotRoller, gridBagConstraints);

      zRotRoller.addChangeListener(new javax.swing.event.ChangeListener()
      {
         public void stateChanged(javax.swing.event.ChangeEvent evt)
         {
            zRotRollerStateChanged(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 4;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      add(zRotRoller, gridBagConstraints);

      javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
      jPanel1.setLayout(jPanel1Layout);
      jPanel1Layout.setHorizontalGroup(
         jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGap(0, 217, Short.MAX_VALUE)
      );
      jPanel1Layout.setVerticalGroup(
         jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGap(0, 528, Short.MAX_VALUE)
      );

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 9;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.weighty = 1.0;
      add(jPanel1, gridBagConstraints);

      originSlider.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("GUI.originSlider.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
      originSlider.setMin(-1.0F);
      originSlider.setMinimumSize(new java.awt.Dimension(90, 58));
      originSlider.setPreferredSize(new java.awt.Dimension(200, 60));
      originSlider.setVal(0.0F);
      originSlider.addChangeListener(new javax.swing.event.ChangeListener()
      {
         public void stateChanged(javax.swing.event.ChangeEvent evt)
         {
            originSliderStateChanged(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 5;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      add(originSlider, gridBagConstraints);

      jPanel3.setLayout(new java.awt.GridLayout(1, 0));

      buttonGroup2.add(belowButton);
      belowButton.setText(bundle.getString("GUI.belowButton.text")); // NOI18N
      belowButton.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            belowButtonActionPerformed(evt);
         }
      });
      jPanel3.add(belowButton);

      buttonGroup2.add(sliceButton);
      sliceButton.setSelected(true);
      sliceButton.setText("slice\n");
      sliceButton.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            sliceButtonActionPerformed(evt);
         }
      });
      jPanel3.add(sliceButton);

      buttonGroup2.add(aboveButton);
      aboveButton.setText(bundle.getString("GUI.aboveButton.text")); // NOI18N
      aboveButton.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            aboveButtonActionPerformed(evt);
         }
      });
      jPanel3.add(aboveButton);

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      add(jPanel3, gridBagConstraints);
   }// </editor-fold>//GEN-END:initComponents

   /**
    * Set the value of params
    *
    * @param params new value of params
    */
   public void setParams(Params params)
   {
      this.params = params;
   }


   public void setInField(Field inField)
   {
      this.inField = inField;
      makeCoeffs();
      updateOrigSliderBounds();
   }

   private void setAxis()
   {
      if (xButton.isSelected())      axis = 0;
      else if (yButton.isSelected()) axis = 1;
      else if (zButton.isSelected()) axis = 2;
      params.setActive(false);
      originSlider.setVal(0);
      xRotRoller.setValue(0);
      yRotRoller.setValue(0);
      zRotRoller.setValue(0);
      params.setAxis(axis);
      params.setActive(true);
      makeCoeffs();
      updateOrigSliderBounds();
   }
   
    private void xButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_xButtonActionPerformed
    {//GEN-HEADEREND:event_xButtonActionPerformed
       setAxis();
    }//GEN-LAST:event_xButtonActionPerformed

    private void yButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_yButtonActionPerformed
    {//GEN-HEADEREND:event_yButtonActionPerformed
       setAxis();
    }//GEN-LAST:event_yButtonActionPerformed

    private void zButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_zButtonActionPerformed
    {//GEN-HEADEREND:event_zButtonActionPerformed
       setAxis();
    }//GEN-LAST:event_zButtonActionPerformed

   private void makeCoeffs()
   {
      params.setAdjusting(xRotRoller.isAdjusting() || 
                          yRotRoller.isAdjusting() || 
                          zRotRoller.isAdjusting() );
      Matrix3f rotMatrix = new Matrix3f(1, 0, 0,
                                        0, 1, 0,
                                        0, 0, 1);
      Matrix3f tmp = new Matrix3f();
      float[] cv = new float[] {0, 0, 0};
      cv[axis] = 1;
      Vector3f c = new Vector3f(cv);
      Vector3f cf = new Vector3f();
      tmp.rotX((float) (xRotRoller.getValue() * Math.PI / 180));
      rotMatrix.mul(tmp);
      tmp.rotY((float) (yRotRoller.getValue() * Math.PI / 180));
      rotMatrix.mul(tmp);
      tmp.rotZ((float) (zRotRoller.getValue() * Math.PI / 180));
      tmp.mul(rotMatrix);
      tmp.transform(c, cf);
      cf.get(coeffs);
      params.setCoeffs(coeffs);
      if (!params.isAdjusting())
         updateOrigSliderBounds();
   }
   
   private void updateOrigSliderBounds()
   {
      float[] crds = inField.getCoords();
      float[] c = params.getCoeffs();
      float rmin = Float.MAX_VALUE;
      float rmax = -Float.MAX_VALUE;
      for (int i = 0; i < inField.getNNodes(); i++)
      {
         float f = c[0] * crds[3 * i] + 
                   c[1] * crds[3 * i + 1] + 
                   c[2] * crds[3 * i + 2];
         if (f < rmin) rmin =f;
         if (f > rmax) rmax =f;
      }
      float cr = (rmax - rmin) / 10000;
      originSlider.setMinMax(rmin+cr, rmax-cr);
   }
   
   private void transformResetButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_transformResetButtonActionPerformed
   {//GEN-HEADEREND:event_transformResetButtonActionPerformed
       active = false;
       zRotRoller.reset();
       yRotRoller.reset();
       xRotRoller.reset();
       active = true;
       makeCoeffs();
   }//GEN-LAST:event_transformResetButtonActionPerformed

   private void xRotRollerStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_xRotRollerStateChanged
   {//GEN-HEADEREND:event_xRotRollerStateChanged
      makeCoeffs();
   }//GEN-LAST:event_xRotRollerStateChanged

   private void yRotRollerStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_yRotRollerStateChanged
   {//GEN-HEADEREND:event_yRotRollerStateChanged
      makeCoeffs();
   }//GEN-LAST:event_yRotRollerStateChanged

   private void zRotRollerStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_zRotRollerStateChanged
   {//GEN-HEADEREND:event_zRotRollerStateChanged
      makeCoeffs();
   }//GEN-LAST:event_zRotRollerStateChanged

   private void originSliderStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_originSliderStateChanged
   {//GEN-HEADEREND:event_originSliderStateChanged
      params.setAdjusting(originSlider.isAdjusting());
      params.setRightSide(originSlider.getVal());
   }//GEN-LAST:event_originSliderStateChanged

   private void aboveButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_aboveButtonActionPerformed
   {//GEN-HEADEREND:event_aboveButtonActionPerformed
      setPosition();
   }//GEN-LAST:event_aboveButtonActionPerformed

   private void sliceButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_sliceButtonActionPerformed
   {//GEN-HEADEREND:event_sliceButtonActionPerformed
      setPosition();
   }//GEN-LAST:event_sliceButtonActionPerformed

   private void belowButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_belowButtonActionPerformed
   {//GEN-HEADEREND:event_belowButtonActionPerformed
      setPosition();
   }//GEN-LAST:event_belowButtonActionPerformed

   private void setPosition()
   {
      if (sliceButton.isSelected())      params.setType(0);
      else if (belowButton.isSelected()) params.setType(-1);
      else if (aboveButton.isSelected()) params.setType(1);
   }
   
   // Variables declaration - do not modify//GEN-BEGIN:variables
   private javax.swing.JRadioButton aboveButton;
   private javax.swing.JRadioButton belowButton;
   private javax.swing.ButtonGroup buttonGroup1;
   private javax.swing.ButtonGroup buttonGroup2;
   private javax.swing.JPanel jPanel1;
   private javax.swing.JPanel jPanel2;
   private javax.swing.JPanel jPanel3;
   private pl.edu.icm.visnow.gui.widgets.FloatSlider originSlider;
   private javax.swing.JRadioButton sliceButton;
   private javax.swing.JButton transformResetButton;
   private javax.swing.JRadioButton xButton;
   private pl.edu.icm.visnow.gui.widgets.UnboundedRoller.ExtendedUnboundedRoller xRotRoller;
   private javax.swing.JRadioButton yButton;
   private pl.edu.icm.visnow.gui.widgets.UnboundedRoller.ExtendedUnboundedRoller yRotRoller;
   private javax.swing.JRadioButton zButton;
   private pl.edu.icm.visnow.gui.widgets.UnboundedRoller.ExtendedUnboundedRoller zRotRoller;
   // End of variables declaration//GEN-END:variables

}
