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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import pl.edu.icm.visnow.datasets.CellSet;
import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.geometries.objects.SignalingTransform3D;
import pl.edu.icm.visnow.geometries.parameters.AbstractRenderingParams;
import pl.edu.icm.visnow.geometries.parameters.CellSetDisplayParams;
import pl.edu.icm.visnow.geometries.parameters.DataMappingParams;
import pl.edu.icm.visnow.system.main.VisNow;

/**
 *
 * @author  Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class CellSetPresentationGUI extends javax.swing.JPanel
{
   private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(CellSetPresentationGUI.class);
   private boolean debug = VisNow.isDebug();
   protected CellSet inCellSet = null;
   protected boolean active = true;
   protected CellSetDisplayParams params = null;
   protected DataMappingParams dataMappingParams = null;
   protected AbstractRenderingParams renderingParams = null;

   /** Creates new form ColoringGUI */
   public CellSetPresentationGUI()
   {
      initComponents();
   }

   public CellSetPresentationGUI(CellSet inCellSet, Field inField, CellSetDisplayParams params)
   {
      log.debug("init");
      initComponents();
      active = false;
      if (params == null || inCellSet == null || inField == null)
         return;
      this.params = params;
      this.inCellSet = inCellSet;
      log.debug("unpacking params");
      renderingParams = params.getRenderingParams();
      dataMappingParams = params.getDataMappingParams();
      displayPropertiesGUI.setRenderingParams(renderingParams);
      transformPanel.setTransformParams(params.getTransformParams());
      transformPanel.setTransSensitivity(inField.getDiameter()/500);
      dataMappingGUI.setInData(inCellSet, inField, dataMappingParams);
      dataMappingGUI.setStartNullTransparencyComponent(true);
      dataMappingGUI.setRenderingParams(renderingParams);
      if (inCellSet.getNData() > 0)
      {
         log.debug("adding selectingComponent panel");
         mainPane.insertTab("select",null,selectionPanel,"select data range for cells to be displayed",3);
         selectingComponentSelector.setScalarComponentsOnly(true);
         selectingComponentSelector.setAddNullComponent(true);
         selectingComponentSelector.setTitle("cells selected by component");
         selectingComponentSelector.setDataSchema(inCellSet.getSchema());
      }
      else
         mainPane.removeTabAt(3);
         log.debug("CellSetPresentationGUI done");
      active = true;
   }
   
   public String getCellSetName()
   {
      if (inCellSet == null)
         return null;
      return inCellSet.getName();
   }

   public DataMappingGUI getDataMappingGUI()
   {
      return dataMappingGUI;
   }

   public void setPresentation(boolean simple)
   {
      GridBagConstraints gridBagConstraints;
      Dimension simpleDim = new Dimension(200, 750);
      Dimension expertDim = new Dimension(200, 590);
      if (simple)
      {
         remove(mainPane);
         mainPane.removeAll();
         dataMappingGUI.setPresentation(simple);
         displayPropertiesGUI.setPresentation(simple);
         gridBagConstraints = new java.awt.GridBagConstraints();
         gridBagConstraints.gridx = 0;
         gridBagConstraints.gridy = 0;
         gridBagConstraints.weightx = 1.0;
         gridBagConstraints.weighty = 1.0;
         gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
         add(dataMappingGUI, gridBagConstraints);
         if (dataMappingGUI.isTransparencyStartNull())
         {
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.weighty = 1.0;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            add(displayPropertiesGUI, gridBagConstraints);
         }
         setMinimumSize(simpleDim);
         setMaximumSize(simpleDim);
         setPreferredSize(simpleDim);
      }
      else
      {
         remove(dataMappingGUI);
         if (dataMappingGUI.isTransparencyStartNull())
            remove(displayPropertiesGUI);
         setMinimumSize(expertDim);
         setMaximumSize(expertDim);
         setPreferredSize(expertDim);    
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
      validate();
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

      textureFileChooser = new javax.swing.JFileChooser();
      mainPane = new javax.swing.JTabbedPane();
      dataMappingGUI = new pl.edu.icm.visnow.geometries.gui.DataMappingGUI();
      displayPropertiesGUI = new pl.edu.icm.visnow.geometries.gui.DisplayPropertiesGUI();
      transformPanel = new pl.edu.icm.visnow.geometries.gui.TransformPanel();
      selectionPanel = new javax.swing.JPanel();
      selectCellsBox = new javax.swing.JCheckBox();
      selectedRangeSlider = new pl.edu.icm.visnow.gui.widgets.FloatSubRangeSlider.ExtendedFloatSubRangeSlider();
      jPanel1 = new javax.swing.JPanel();
      selectingComponentSelector = new pl.edu.icm.visnow.lib.gui.DataComponentSelector();

      setMinimumSize(new java.awt.Dimension(200, 620));
      setPreferredSize(new java.awt.Dimension(235, 620));
      setRequestFocusEnabled(false);
      setLayout(new java.awt.GridBagLayout());

      mainPane.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
      mainPane.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
      mainPane.setMinimumSize(new java.awt.Dimension(200, 620));
      mainPane.setPreferredSize(new java.awt.Dimension(235, 620));
      mainPane.setRequestFocusEnabled(false);

      dataMappingGUI.setMinimumSize(new java.awt.Dimension(180, 490));
      dataMappingGUI.setPreferredSize(new java.awt.Dimension(220, 495));
      mainPane.addTab("datamap", dataMappingGUI);

      displayPropertiesGUI.setBackground(new java.awt.Color(238, 238, 237));
      mainPane.addTab("display", displayPropertiesGUI);
      mainPane.addTab("transform", transformPanel);

      selectionPanel.setLayout(new java.awt.GridBagLayout());

      selectCellsBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
      selectCellsBox.setText("<html>show cells with data <p>values from range");
      selectCellsBox.setIconTextGap(8);
      selectCellsBox.setMinimumSize(new java.awt.Dimension(160, 38));
      selectCellsBox.setPreferredSize(new java.awt.Dimension(170, 38));
      selectCellsBox.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            selectCellsBoxActionPerformed(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
      selectionPanel.add(selectCellsBox, gridBagConstraints);

      selectedRangeSlider.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "cell data range", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 12))); // NOI18N
      selectedRangeSlider.addChangeListener(new javax.swing.event.ChangeListener()
      {
         public void stateChanged(javax.swing.event.ChangeEvent evt)
         {
            selectedRangeSliderStateChanged(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.ipadx = 120;
      gridBagConstraints.ipady = 2;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
      gridBagConstraints.weightx = 1.0;
      selectionPanel.add(selectedRangeSlider, gridBagConstraints);

      javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
      jPanel1.setLayout(jPanel1Layout);
      jPanel1Layout.setHorizontalGroup(
         jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGap(0, 255, Short.MAX_VALUE)
      );
      jPanel1Layout.setVerticalGroup(
         jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGap(0, 435, Short.MAX_VALUE)
      );

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 3;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.weighty = 1.0;
      selectionPanel.add(jPanel1, gridBagConstraints);

      selectingComponentSelector.addChangeListener(new javax.swing.event.ChangeListener()
      {
         public void stateChanged(javax.swing.event.ChangeEvent evt)
         {
            selectingComponentSelectorStateChanged(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      selectionPanel.add(selectingComponentSelector, gridBagConstraints);

      mainPane.addTab("select", selectionPanel);

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 1.0;
      add(mainPane, gridBagConstraints);
   }// </editor-fold>//GEN-END:initComponents


private void selectCellsBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_selectCellsBoxActionPerformed
{//GEN-HEADEREND:event_selectCellsBoxActionPerformed
   fireStateChanged();
}//GEN-LAST:event_selectCellsBoxActionPerformed

private void selectedRangeSliderStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_selectedRangeSliderStateChanged
{//GEN-HEADEREND:event_selectedRangeSliderStateChanged
    if (!selectedRangeSlider.isAdjusting() && selectCellsBox.isSelected())
      fireStateChanged();
}//GEN-LAST:event_selectedRangeSliderStateChanged

private void selectingComponentSelectorStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_selectingComponentSelectorStateChanged
{//GEN-HEADEREND:event_selectingComponentSelectorStateChanged
   DataArray selData = inCellSet.getData(selectingComponentSelector.getComponent());
   if (selData == null)
      return;
   selectedRangeSlider.setParams(selData.getMinv(), selData.getMaxv(), selData.getMinv(), selData.getMaxv());
   
}//GEN-LAST:event_selectingComponentSelectorStateChanged

   public void fireStateChanged()
   {
      if (inCellSet == null || dataMappingParams == null || !active)
         return;
      params.setActive(true);
      params.setSelectionActive(selectCellsBox.isSelected());
      params.setSelectByComponent(selectingComponentSelector.getComponent());
      params.setSelectOver(selectedRangeSlider.getLow());
      params.setSelectUnder(selectedRangeSlider.getUp());
   }

   public void setCellSetDisplayParams(CellSetDisplayParams params)
   {
      if (params == null)
         return;
      this.params = params;
      dataMappingParams = params.getDataMappingParams();
      renderingParams = params.getRenderingParams();
      displayPropertiesGUI.setRenderingParams(renderingParams);
      dataMappingGUI.setRenderingParams(renderingParams);
      transformPanel.setTransformParams(params.getTransformParams());
   }

   public void setSignalingTransform(SignalingTransform3D sigTrans)
   {
      transformPanel.setSigTrans(sigTrans);
   }

   // Variables declaration - do not modify//GEN-BEGIN:variables
   private pl.edu.icm.visnow.geometries.gui.DataMappingGUI dataMappingGUI;
   private pl.edu.icm.visnow.geometries.gui.DisplayPropertiesGUI displayPropertiesGUI;
   private javax.swing.JPanel jPanel1;
   private javax.swing.JTabbedPane mainPane;
   private javax.swing.JCheckBox selectCellsBox;
   private pl.edu.icm.visnow.gui.widgets.FloatSubRangeSlider.ExtendedFloatSubRangeSlider selectedRangeSlider;
   private pl.edu.icm.visnow.lib.gui.DataComponentSelector selectingComponentSelector;
   private javax.swing.JPanel selectionPanel;
   private javax.swing.JFileChooser textureFileChooser;
   private pl.edu.icm.visnow.geometries.gui.TransformPanel transformPanel;
   // End of variables declaration//GEN-END:variables
}
