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

package pl.edu.icm.visnow.geometries.gui;

import javax.media.j3d.Transform3D;
import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;
import pl.edu.icm.visnow.geometries.objects.SignalingTransform3D;
import pl.edu.icm.visnow.geometries.parameters.TransformParams;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class Transform2DPanel extends javax.swing.JPanel
{

   private TransformParams transformParams = new TransformParams();
   private SignalingTransform3D sigTrans   = null;
   private Transform3D trans   = null;
   private Matrix3f rotMatrix = new Matrix3f(1, 0, 0, 0, 1, 0, 0, 0, 1);
   private Vector3f transVector = new Vector3f();
   private float scale = 1.f;
   private float xScale = 1.f;
   private float yScale = 1.f;
   private float zScale = 1.f;
   private boolean active = true;

   /** Creates new form TransformPanel */
   public Transform2DPanel()
   {
      initComponents();
      rotationRoller.setLabel("rotation");
      xTransRoller.setLabel("x trans");
      yTransRoller.setLabel("y trans");
      scaleRoller.setLabel("scale");
      xScaleRoller.setLabel("x scale");
      yScaleRoller.setLabel("y scale");

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

      transformResetButton = new javax.swing.JButton();
      jPanel3 = new javax.swing.JPanel();
      scaleRoller = new pl.edu.icm.visnow.gui.widgets.UnboundedRoller.ExtendedUnboundedRoller();
      rotationRoller = new pl.edu.icm.visnow.gui.widgets.UnboundedRoller.ExtendedUnboundedRoller();
      xTransRoller = new pl.edu.icm.visnow.gui.widgets.UnboundedRoller.ExtendedUnboundedRoller();
      yTransRoller = new pl.edu.icm.visnow.gui.widgets.UnboundedRoller.ExtendedUnboundedRoller();
      exportFransformButton = new javax.swing.JButton();
      xScaleRoller = new pl.edu.icm.visnow.gui.widgets.UnboundedRoller.ExtendedUnboundedRoller();
      yScaleRoller = new pl.edu.icm.visnow.gui.widgets.UnboundedRoller.ExtendedUnboundedRoller();

      setPreferredSize(new java.awt.Dimension(206, 285));
      setLayout(new java.awt.GridBagLayout());

      transformResetButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
      transformResetButton.setText("reset/inherit");
      transformResetButton.setMargin(new java.awt.Insets(2, 1, 2, 1));
      transformResetButton.setName("transformResetButton"); // NOI18N
      transformResetButton.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            transformResetButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
      add(transformResetButton, gridBagConstraints);

      jPanel3.setName("jPanel3"); // NOI18N

      javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
      jPanel3.setLayout(jPanel3Layout);
      jPanel3Layout.setHorizontalGroup(
         jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGap(0, 206, Short.MAX_VALUE)
      );
      jPanel3Layout.setVerticalGroup(
         jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGap(0, 8, Short.MAX_VALUE)
      );

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 8;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.weighty = 1.0;
      add(jPanel3, gridBagConstraints);

      scaleRoller.setName("scaleRoller"); // NOI18N
      scaleRoller.addChangeListener(new javax.swing.event.ChangeListener()
      {
         public void stateChanged(javax.swing.event.ChangeEvent evt)
         {
            scaleRollerStateChanged(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 4;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.insets = new java.awt.Insets(6, 0, 3, 0);
      add(scaleRoller, gridBagConstraints);

      rotationRoller.setName("rotationRoller"); // NOI18N
      rotationRoller.addChangeListener(new javax.swing.event.ChangeListener()
      {
         public void stateChanged(javax.swing.event.ChangeEvent evt)
         {
            rotationRollerStateChanged(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.weightx = 1.0;
      add(rotationRoller, gridBagConstraints);

      xTransRoller.setName("xTransRoller"); // NOI18N
      xTransRoller.addChangeListener(new javax.swing.event.ChangeListener()
      {
         public void stateChanged(javax.swing.event.ChangeEvent evt)
         {
            xTransRollerStateChanged(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
      add(xTransRoller, gridBagConstraints);

      yTransRoller.setName("yTransRoller"); // NOI18N
      yTransRoller.addChangeListener(new javax.swing.event.ChangeListener()
      {
         public void stateChanged(javax.swing.event.ChangeEvent evt)
         {
            yTransRollerStateChanged(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 3;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      add(yTransRoller, gridBagConstraints);

      exportFransformButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
      exportFransformButton.setText("export transformation");
      exportFransformButton.setMargin(new java.awt.Insets(2, 1, 2, 1));
      exportFransformButton.setMinimumSize(new java.awt.Dimension(100, 25));
      exportFransformButton.setName("exportFransformButton"); // NOI18N
      exportFransformButton.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            exportFransformButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 7;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
      add(exportFransformButton, gridBagConstraints);

      xScaleRoller.setName("xScaleRoller"); // NOI18N
      xScaleRoller.addChangeListener(new javax.swing.event.ChangeListener()
      {
         public void stateChanged(javax.swing.event.ChangeEvent evt)
         {
            xScaleRollerStateChanged(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 5;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
      add(xScaleRoller, gridBagConstraints);

      yScaleRoller.setName("yScaleRoller"); // NOI18N
      yScaleRoller.addChangeListener(new javax.swing.event.ChangeListener()
      {
         public void stateChanged(javax.swing.event.ChangeEvent evt)
         {
            yScaleRollerStateChanged(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 6;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      add(yScaleRoller, gridBagConstraints);
   }// </editor-fold>//GEN-END:initComponents

    private void reset()
    {
       active = false;
//       System.out.println("resetting transform");
       rotationRoller.reset();
       yTransRoller.reset();
       xTransRoller.reset();
       scaleRoller.reset();
       xScaleRoller.reset();
       yScaleRoller.reset();
       rotMatrix = new Matrix3f(1, 0, 0, 0, 1, 0, 0, 0, 1);
       transVector = new Vector3f();
       scale = xScale = yScale = zScale = 1.f;
       transformParams.resetTransform();
       active = true;
    }
   
    private void transformResetButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_transformResetButtonActionPerformed
    {//GEN-HEADEREND:event_transformResetButtonActionPerformed
        reset();
}//GEN-LAST:event_transformResetButtonActionPerformed

    private void scaleRollerStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_scaleRollerStateChanged
    {//GEN-HEADEREND:event_scaleRollerStateChanged
       scale = (float) Math.pow(1.01, scaleRoller.getValue());
       makeRotMatrix();
    }//GEN-LAST:event_scaleRollerStateChanged

    private void rotationRollerStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_rotationRollerStateChanged
    {//GEN-HEADEREND:event_rotationRollerStateChanged
       makeRotMatrix();
    }//GEN-LAST:event_rotationRollerStateChanged

    private void xTransRollerStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_xTransRollerStateChanged
    {//GEN-HEADEREND:event_xTransRollerStateChanged
       transVector.x = xTransRoller.getValue();
       trans = new Transform3D(rotMatrix, transVector, scale);
      if (!active)
         return;
       transformParams.setAdjusting(xTransRoller.isAdjusting());
       transformParams.setTransform(trans);
       if (sigTrans != null)
          sigTrans.setTransform(trans);
    }//GEN-LAST:event_xTransRollerStateChanged

    private void yTransRollerStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_yTransRollerStateChanged
    {//GEN-HEADEREND:event_yTransRollerStateChanged
       transVector.y = yTransRoller.getValue();
       trans = new Transform3D(rotMatrix, transVector, scale);
      if (!active)
         return;
       transformParams.setAdjusting(yTransRoller.isAdjusting());
       transformParams.setTransform(trans);
       if (sigTrans != null)
          sigTrans.setTransform(trans);
    }//GEN-LAST:event_yTransRollerStateChanged

    private void exportFransformButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_exportFransformButtonActionPerformed
    {//GEN-HEADEREND:event_exportFransformButtonActionPerformed
       sigTrans.fireTransformChanged();
}//GEN-LAST:event_exportFransformButtonActionPerformed

    private void xScaleRollerStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_xScaleRollerStateChanged
    {//GEN-HEADEREND:event_xScaleRollerStateChanged
       xScale = (float) Math.pow(1.01, xScaleRoller.getValue());
       makeRotMatrix();
    }//GEN-LAST:event_xScaleRollerStateChanged

    private void yScaleRollerStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_yScaleRollerStateChanged
    {//GEN-HEADEREND:event_yScaleRollerStateChanged
       yScale = (float) Math.pow(1.01, yScaleRoller.getValue());
       makeRotMatrix();
    }//GEN-LAST:event_yScaleRollerStateChanged

   private void makeRotMatrix()
   {
      transformParams.setAdjusting(rotationRoller.isAdjusting() ||
      scaleRoller.isAdjusting() || xScaleRoller.isAdjusting() || yScaleRoller.isAdjusting());
      rotMatrix = new Matrix3f(xScale, 0, 0,
                               0, yScale, 0,
                               0, 0, zScale);
      Matrix3f tmp = new Matrix3f();
      tmp.rotZ((float) (rotationRoller.getValue() * Math.PI / 180));
      tmp.mul(rotMatrix);
      trans = new Transform3D(tmp, transVector, scale);
      if (!active)
         return;
      transformParams.setTransform(trans);
      if (sigTrans != null)
         sigTrans.setTransform(trans);
   }

   public void setTransformParams(TransformParams transformParams)
   {
      this.transformParams = transformParams;
   }

   public void setSigTrans(SignalingTransform3D sigTrans)
   {
      this.sigTrans = sigTrans;
   }
   
   public void resetTransform()
   {
      SwingInstancer.swingRunAndWait(new Runnable()
      {
          @Override
          public void run()
          {
             reset();
          }
      });
   }
   
   public void setTransSensitivity(float s)
   {
      xTransRoller.setSensitivity(s);
      yTransRoller.setSensitivity(s);
   }
   
   // Variables declaration - do not modify//GEN-BEGIN:variables
   protected javax.swing.JButton exportFransformButton;
   protected javax.swing.JPanel jPanel3;
   protected pl.edu.icm.visnow.gui.widgets.UnboundedRoller.ExtendedUnboundedRoller rotationRoller;
   protected pl.edu.icm.visnow.gui.widgets.UnboundedRoller.ExtendedUnboundedRoller scaleRoller;
   protected javax.swing.JButton transformResetButton;
   protected pl.edu.icm.visnow.gui.widgets.UnboundedRoller.ExtendedUnboundedRoller xScaleRoller;
   protected pl.edu.icm.visnow.gui.widgets.UnboundedRoller.ExtendedUnboundedRoller xTransRoller;
   protected pl.edu.icm.visnow.gui.widgets.UnboundedRoller.ExtendedUnboundedRoller yScaleRoller;
   protected pl.edu.icm.visnow.gui.widgets.UnboundedRoller.ExtendedUnboundedRoller yTransRoller;
   // End of variables declaration//GEN-END:variables
}
