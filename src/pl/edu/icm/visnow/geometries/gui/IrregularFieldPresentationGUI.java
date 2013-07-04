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

import java.awt.GridBagConstraints;
import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;
import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.geometries.objects.SignalingTransform3D;
import pl.edu.icm.visnow.geometries.parameters.IrregularFieldDisplayParams;
import pl.edu.icm.visnow.lib.gui.ChangeFiringGUI;
import pl.edu.icm.visnow.system.main.VisNow;

/**
 *
 * @author  Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class IrregularFieldPresentationGUI extends javax.swing.JPanel
{
   private boolean debug = VisNow.isDebug();
   protected Field inField = null;
   protected int nScalarComps = 0;
   protected int nScalarCellComps = 0;
   protected boolean active = true;
   protected IrregularFieldDisplayParams params = null;
   protected Matrix3f rotMatrix = new Matrix3f(1, 0, 0, 0, 1, 0, 0, 0, 1);
   protected Vector3f transVector = new Vector3f();
   protected float scale = 1.f;
   protected ChangeFiringGUI parentGUI = null;

   /** Creates new form ColoringGUI */
   public IrregularFieldPresentationGUI()
   {
      initComponents();
      dataMappingGUI.setStartNullTransparencyComponent(true);
   }
   
   
   public IrregularFieldPresentationGUI(Field inField, IrregularFieldDisplayParams params)
   {
      initComponents();
      active = false;
      if (params == null || inField == null)
         return;
      this.params = params;
      this.inField = inField;
      dataMappingGUI.setInData(inField, params);
      transformPanel.setTransformParams(params.getTransformParams());
      transformPanel.setTransSensitivity(inField.getDiameter()/500);
      displayPropertiesGUI.setRenderingParams(params);
      active = true;
   }

   public void setPresentation(boolean simple)
   {
      GridBagConstraints gridBagConstraints;
      if (simple)
      {
         remove(mainPane);
         mainPane.removeAll();
         dataMappingGUI.setPresentation(simple);
         displayPropertiesGUI.setPresentation(simple);
         gridBagConstraints = new java.awt.GridBagConstraints();
         gridBagConstraints.gridx = 0;
         gridBagConstraints.gridy = 1;
         gridBagConstraints.weightx = 1.0;
         gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
         add(dataMappingGUI, gridBagConstraints);
         if (dataMappingGUI.isTransparencyStartNull())
         {
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 2;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            add(displayPropertiesGUI, gridBagConstraints);
         }
      }
      else
      {
         remove(dataMappingGUI);
         if (dataMappingGUI.isTransparencyStartNull())
            remove(displayPropertiesGUI);
         dataMappingGUI.setPresentation(simple);
         displayPropertiesGUI.setPresentation(simple);
         mainPane.addTab("datamap", dataMappingGUI);
         mainPane.addTab("display", displayPropertiesGUI);
         mainPane.addTab("transform", transformPanel);
         gridBagConstraints = new java.awt.GridBagConstraints();
         gridBagConstraints.gridx = 0;
         gridBagConstraints.gridy = 1;
         gridBagConstraints.weightx = 1.0;
         gridBagConstraints.weighty = 1.0;
         gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
         add(mainPane, gridBagConstraints);
      }
      revalidate();
      repaint();
   }

   
   /** This method is called from within the constructor to
    * initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is
    * always regenerated by the Form Editor.
    */
   // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
   private void initComponents()
   {
      java.awt.GridBagConstraints gridBagConstraints;

      mainPane = new javax.swing.JTabbedPane();
      dataMappingGUI = new pl.edu.icm.visnow.geometries.gui.DataMappingGUI();
      displayPropertiesGUI = new pl.edu.icm.visnow.geometries.gui.DisplayPropertiesGUI();
      transformPanel = new pl.edu.icm.visnow.geometries.gui.TransformPanel();

      setMinimumSize(new java.awt.Dimension(200, 720));
      setName(""); // NOI18N
      setPreferredSize(new java.awt.Dimension(235, 760));
      setRequestFocusEnabled(false);
      setLayout(new java.awt.GridBagLayout());

      mainPane.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
      mainPane.setMinimumSize(new java.awt.Dimension(200, 565));
      mainPane.setPreferredSize(new java.awt.Dimension(235, 565));

      dataMappingGUI.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
      mainPane.addTab("datamap", dataMappingGUI);

      displayPropertiesGUI.setMinimumSize(new java.awt.Dimension(200, 590));
      displayPropertiesGUI.setPreferredSize(new java.awt.Dimension(235, 590));
      mainPane.addTab("display", displayPropertiesGUI);

      transformPanel.setMinimumSize(new java.awt.Dimension(200, 590));
      transformPanel.setPreferredSize(new java.awt.Dimension(235, 590));
      mainPane.addTab("transforms", transformPanel);

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 1.0;
      add(mainPane, gridBagConstraints);
   }// </editor-fold>//GEN-END:initComponents

   public void setSignalingTransform(SignalingTransform3D sigTrans)
   {
      transformPanel.setSigTrans(sigTrans);
   }

   public void fireStateChanged()
   {
      if (inField == null || params == null || !active)
         return;
      int mode = 0;
      params.setDisplayMode(mode);
      params.setActive(true);
   }

   public DisplayPropertiesGUI getDisplayPropertiesGUI()
   {
      return displayPropertiesGUI;
   }

   // Variables declaration - do not modify//GEN-BEGIN:variables
   private pl.edu.icm.visnow.geometries.gui.DataMappingGUI dataMappingGUI;
   private pl.edu.icm.visnow.geometries.gui.DisplayPropertiesGUI displayPropertiesGUI;
   private javax.swing.JTabbedPane mainPane;
   private pl.edu.icm.visnow.geometries.gui.TransformPanel transformPanel;
   // End of variables declaration//GEN-END:variables
}
