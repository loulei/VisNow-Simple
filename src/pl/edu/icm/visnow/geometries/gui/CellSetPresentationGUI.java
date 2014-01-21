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

import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import pl.edu.icm.visnow.datasets.CellSet;
import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.geometries.objects.SignalingTransform3D;
import pl.edu.icm.visnow.geometries.parameters.AbstractRenderingParams;
import pl.edu.icm.visnow.geometries.parameters.CellSetDisplayParams;
import pl.edu.icm.visnow.geometries.parameters.DataMappingParams;
import pl.edu.icm.visnow.system.main.VisNow;
import static java.awt.GridBagConstraints.*;

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
      displayPropertiesGUI.setNDims(inCellSet.getNCellDims());      
      transformGUI.setTransformParams(params.getTransformParams());
      transformGUI.setTransSensitivity(inField.getDiameter()/500);
      dataMappingGUI.setInData(inCellSet, inField, dataMappingParams);
      dataMappingGUI.setStartNullTransparencyComponent(true);
      dataMappingGUI.setRenderingParams(renderingParams);
      
      extendedTabbedPane.removeTabAt(3);  //delete for release
      /*if (inCellSet.getNData() > 0)
      {
         log.debug("adding selectingComponent panel");
         mainPane.insertTab("select",null,selectionPanel,"select data range for cells to be displayed",3);
         selectingComponentSelector.setScalarComponentsOnly(true);
         selectingComponentSelector.setAddNullComponent(true);
         selectingComponentSelector.setTitle("cells selected by component");
         selectingComponentSelector.setDataSchema(inCellSet.getSchema());
      }
      else
         mainPane.removeTabAt(3);*/
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

    public void setPresentation(boolean simple) {
        Insets insets0 = new Insets(0, 0, 0, 0);
        dataMappingGUI.setPresentation(simple);
        displayPropertiesGUI.setPresentation(simple);

        if (simple) {
            simplePanel.add(dataMappingGUI, new GridBagConstraints(0, 1, 1, 1, 1, 0, NORTH, HORIZONTAL, insets0, 0, 0));
            if (dataMappingGUI.isTransparencyStartNull())
                simplePanel.add(displayPropertiesGUI, new GridBagConstraints(0, 2, 1, 1, 1, 0, NORTH, HORIZONTAL, insets0, 0, 0));
        } else {
            dataMappingPanel.add(dataMappingGUI, java.awt.BorderLayout.NORTH);
            displayPropertiesPanel.add(displayPropertiesGUI, java.awt.BorderLayout.NORTH);
        }
        ((CardLayout) getLayout()).show(this, simple ? "simpleUI" : "extendedUI");
        revalidate();

    }


   /** This method is called from within the constructor to
    * initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is
    * always regenerated by the Form Editor.
    */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        textureFileChooser = new javax.swing.JFileChooser();
        extendedTabbedPane = new javax.swing.JTabbedPane();
        dataMappingScrollPane = new javax.swing.JScrollPane();
        dataMappingPanel = new javax.swing.JPanel();
        dataMappingGUI = new pl.edu.icm.visnow.geometries.gui.DataMappingGUI();
        displayPropertiesScrollPane = new javax.swing.JScrollPane();
        displayPropertiesPanel = new javax.swing.JPanel();
        displayPropertiesGUI = new pl.edu.icm.visnow.geometries.gui.DisplayPropertiesGUI();
        transformScrollPane = new javax.swing.JScrollPane();
        transformPanel = new javax.swing.JPanel();
        transformGUI = new pl.edu.icm.visnow.geometries.gui.TransformPanel();
        selectScrollPane = new javax.swing.JScrollPane();
        selectPanel = new javax.swing.JPanel();
        selectionPanel = new javax.swing.JPanel();
        selectCellsBox = new javax.swing.JCheckBox();
        selectingComponentSelector = new pl.edu.icm.visnow.lib.gui.DataComponentSelector();
        selectedRangeSlider = new pl.edu.icm.visnow.gui.widgets.FloatSubRangeSlider.ExtendedFloatSubRangeSlider();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        simpleScrollPane = new javax.swing.JScrollPane();
        simplePanel = new javax.swing.JPanel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));

        setRequestFocusEnabled(false);
        setLayout(new java.awt.CardLayout());

        extendedTabbedPane.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        extendedTabbedPane.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        extendedTabbedPane.setRequestFocusEnabled(false);

        dataMappingScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        dataMappingPanel.setLayout(new java.awt.BorderLayout());
        dataMappingPanel.add(dataMappingGUI, java.awt.BorderLayout.NORTH);

        dataMappingScrollPane.setViewportView(dataMappingPanel);

        extendedTabbedPane.addTab("datamap", dataMappingScrollPane);

        displayPropertiesScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        displayPropertiesPanel.setLayout(new java.awt.BorderLayout());

        displayPropertiesGUI.setBackground(new java.awt.Color(238, 238, 237));
        displayPropertiesPanel.add(displayPropertiesGUI, java.awt.BorderLayout.NORTH);

        displayPropertiesScrollPane.setViewportView(displayPropertiesPanel);

        extendedTabbedPane.addTab("display", displayPropertiesScrollPane);

        transformScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        transformPanel.setLayout(new java.awt.BorderLayout());
        transformPanel.add(transformGUI, java.awt.BorderLayout.NORTH);

        transformScrollPane.setViewportView(transformPanel);

        extendedTabbedPane.addTab("transform", transformScrollPane);

        selectScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        selectPanel.setLayout(new java.awt.BorderLayout());

        selectionPanel.setLayout(new java.awt.GridBagLayout());

        selectCellsBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        selectCellsBox.setText("<html>show cells with data <p>values from range");
        selectCellsBox.setIconTextGap(8);
        selectCellsBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectCellsBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        selectionPanel.add(selectCellsBox, gridBagConstraints);

        selectingComponentSelector.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                selectingComponentSelectorStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        selectionPanel.add(selectingComponentSelector, gridBagConstraints);

        selectedRangeSlider.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "cell data range", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 12))); // NOI18N
        selectedRangeSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                selectedRangeSliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        selectionPanel.add(selectedRangeSlider, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.weighty = 1.0;
        selectionPanel.add(filler2, gridBagConstraints);

        selectPanel.add(selectionPanel, java.awt.BorderLayout.NORTH);

        selectScrollPane.setViewportView(selectPanel);

        extendedTabbedPane.addTab("select", selectScrollPane);

        add(extendedTabbedPane, "extendedUI");

        simpleScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        simplePanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 99;
        gridBagConstraints.weighty = 1.0;
        simplePanel.add(filler1, gridBagConstraints);

        simpleScrollPane.setViewportView(simplePanel);

        add(simpleScrollPane, "simpleUI");
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
      transformGUI.setTransformParams(params.getTransformParams());
   }

   public void setSignalingTransform(SignalingTransform3D sigTrans)
   {
      transformGUI.setSigTrans(sigTrans);
   }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private pl.edu.icm.visnow.geometries.gui.DataMappingGUI dataMappingGUI;
    private javax.swing.JPanel dataMappingPanel;
    private javax.swing.JScrollPane dataMappingScrollPane;
    private pl.edu.icm.visnow.geometries.gui.DisplayPropertiesGUI displayPropertiesGUI;
    private javax.swing.JPanel displayPropertiesPanel;
    private javax.swing.JScrollPane displayPropertiesScrollPane;
    private javax.swing.JTabbedPane extendedTabbedPane;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JCheckBox selectCellsBox;
    private javax.swing.JPanel selectPanel;
    private javax.swing.JScrollPane selectScrollPane;
    private pl.edu.icm.visnow.gui.widgets.FloatSubRangeSlider.ExtendedFloatSubRangeSlider selectedRangeSlider;
    private pl.edu.icm.visnow.lib.gui.DataComponentSelector selectingComponentSelector;
    private javax.swing.JPanel selectionPanel;
    private javax.swing.JPanel simplePanel;
    private javax.swing.JScrollPane simpleScrollPane;
    private javax.swing.JFileChooser textureFileChooser;
    private pl.edu.icm.visnow.geometries.gui.TransformPanel transformGUI;
    private javax.swing.JPanel transformPanel;
    private javax.swing.JScrollPane transformScrollPane;
    // End of variables declaration//GEN-END:variables

    public static void main(String[] args) {
        UIManager.getDefaults().put("TabbedPane.contentBorderInsets", new Insets(4,0,0,0));
        
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        final CellSetPresentationGUI p = new CellSetPresentationGUI();
        f.add(p);
        f.pack();
        f.addComponentListener(new ComponentAdapter() {
            private boolean toggleSimple = true;

            @Override
            public void componentMoved(ComponentEvent e) {
                p.setPresentation(toggleSimple);
                toggleSimple = !toggleSimple;
            }
        });
        f.setVisible(true);
    }

}
