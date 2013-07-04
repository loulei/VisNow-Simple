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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import pl.edu.icm.visnow.datasets.DataSchema;
import pl.edu.icm.visnow.geometries.parameters.AbstractDataMappingParams;
import pl.edu.icm.visnow.geometries.parameters.AbstractRenderingParams;
import pl.edu.icm.visnow.geometries.parameters.ColorComponentParams;
import pl.edu.icm.visnow.geometries.parameters.DataMappingParams;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.render.RenderEvent;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class ColorMappedComponentPanel extends javax.swing.JPanel
{

   private AbstractDataMappingParams params;
   private AbstractRenderingParams renderParams = null;
   private ColorComponentParams map0Params = null;
   private ColorComponentParams map1Params = null;
   private DataSchema schema = null;

   /** Creates new form ColorComponentPanel */
   public ColorMappedComponentPanel()
   {
      initComponents();
      map1Panel.setMap0(false);
   }

   public void setParams(AbstractDataMappingParams params)
   {
      this.params = params;
      this.map0Params = params.getColorMap0Params();
      this.map1Params = params.getColorMap1Params();
      map0Panel.setParams(params);
      map1Panel.setParams(params);
      colormapLegendGUI.setParams(params.getColormapLegendParameters());
   }

   public void setParams(AbstractRenderingParams params)
   {
      this.renderParams = params;
      map0Panel.setParams(params);
   }

   public void setData(DataSchema schema)
   {
      if (schema == null || params == null)
         return;
      map0Params = params.getColorMap0Params();
      map1Params = params.getColorMap1Params();
      this.schema = schema;
      map0Panel.setData(schema);
      map1Panel.setData(schema);
      if (params == null)
         return;
      colorModificationPanel.setData(schema);
      colorModificationPanel.setSampleColors(Color.GRAY, Color.RED);
   }
   

   public void setPresentation(boolean simple)
   {
      GridBagConstraints gridBagConstraints;
      Dimension simpleDim = new Dimension(210, 275);
      Dimension expertDim = new Dimension(210, 545);
      
      map0Panel.setPresentation(simple);
      colormapLegendGUI.setPresentation(simple);
      if (simple)
      {
         legendPanel.remove(colormapLegendGUI);
         remove(colorOptionPane);
         gridBagConstraints = new java.awt.GridBagConstraints();
         gridBagConstraints.gridx = 0;
         gridBagConstraints.gridy = 1;
         gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
         add(colormapLegendGUI, gridBagConstraints);
         setMinimumSize(simpleDim);
         setPreferredSize(simpleDim);
         setMaximumSize(simpleDim);
      } else
      {
         setMinimumSize(expertDim);
         setPreferredSize(expertDim);
         setMaximumSize(expertDim);
         remove(colormapLegendGUI);
         gridBagConstraints = new java.awt.GridBagConstraints();
         gridBagConstraints.gridx = 0;
         gridBagConstraints.gridy = 0;
         gridBagConstraints.weightx = 1.0;
         gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
         legendPanel.add(colormapLegendGUI, gridBagConstraints);
         gridBagConstraints.gridx = 0;
         gridBagConstraints.gridy = 1;
         gridBagConstraints.weightx = 1.0;
         gridBagConstraints.weighty = 1.0;
         gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
         gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
         colorOptionPane.setVisible(true);
         add(colorOptionPane, gridBagConstraints);
      }
      validate();
   }

   private void updateColorModification()
   {
      if (satButton.isSelected())
      {
         colorModificationPanel.setParams(params.getSatParams());
         colorModificationPanel.setSampleColors(Color.white, Color.red);
         params.setColorMapModification(DataMappingParams.SAT_MAP_MODIFICATION);
      } else
      {
         colorModificationPanel.setParams(params.getValParams());
         colorModificationPanel.setSampleColors(Color.black, Color.red);
         params.setColorMapModification(DataMappingParams.VAL_MAP_MODIFICATION);
      }
      params.fireStateChanged(RenderEvent.COLORS);
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
      map0Panel = new pl.edu.icm.visnow.geometries.gui.ComponentColormappingPanel();
      colorOptionPane = new javax.swing.JTabbedPane();
      legendPanel = new javax.swing.JPanel();
      colormapLegendGUI = new pl.edu.icm.visnow.geometries.gui.ColormapLegendGUI();
      fillPanel = new javax.swing.JPanel();
      blendPanel = new javax.swing.JPanel();
      blendSlider = new javax.swing.JSlider();
      jPanel4 = new javax.swing.JPanel();
      map1Panel = new pl.edu.icm.visnow.geometries.gui.ComponentColormappingPanel();
      modPanel = new javax.swing.JPanel();
      satButton = new javax.swing.JRadioButton();
      valButton = new javax.swing.JRadioButton();
      colorModificationPanel = new pl.edu.icm.visnow.geometries.gui.ColorComponentPanel();
      jPanel1 = new javax.swing.JPanel();
      jPanel2 = new javax.swing.JPanel();

      setMaximumSize(new java.awt.Dimension(2147483647, 545));
      setMinimumSize(new java.awt.Dimension(190, 545));
      setName(""); // NOI18N
      setPreferredSize(new java.awt.Dimension(230, 545));
      setVerifyInputWhenFocusTarget(false);
      setLayout(new java.awt.GridBagLayout());

      map0Panel.setName("map0Panel"); // NOI18N
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
      gridBagConstraints.weightx = 1.0;
      add(map0Panel, gridBagConstraints);

      colorOptionPane.setBorder(javax.swing.BorderFactory.createEtchedBorder());
      colorOptionPane.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
      colorOptionPane.setMaximumSize(new java.awt.Dimension(32767, 303));
      colorOptionPane.setMinimumSize(new java.awt.Dimension(190, 303));
      colorOptionPane.setName("colorOptionPane"); // NOI18N
      colorOptionPane.setPreferredSize(new java.awt.Dimension(230, 303));
      colorOptionPane.addChangeListener(new javax.swing.event.ChangeListener()
      {
         public void stateChanged(javax.swing.event.ChangeEvent evt)
         {
            colorOptionPaneStateChanged(evt);
         }
      });

      legendPanel.setBackground(new java.awt.Color(153, 204, 255));
      legendPanel.setMinimumSize(new java.awt.Dimension(185, 220));
      legendPanel.setName("legendPanel"); // NOI18N
      legendPanel.setPreferredSize(new java.awt.Dimension(225, 225));
      legendPanel.setLayout(new java.awt.GridBagLayout());

      colormapLegendGUI.setMaximumSize(new java.awt.Dimension(2147483647, 200));
      colormapLegendGUI.setMinimumSize(new java.awt.Dimension(187, 125));
      colormapLegendGUI.setName("colormapLegendGUI"); // NOI18N
      colormapLegendGUI.setPreferredSize(new java.awt.Dimension(220, 130));
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.weightx = 1.0;
      legendPanel.add(colormapLegendGUI, gridBagConstraints);

      fillPanel.setName("fillPanel"); // NOI18N

      javax.swing.GroupLayout fillPanelLayout = new javax.swing.GroupLayout(fillPanel);
      fillPanel.setLayout(fillPanelLayout);
      fillPanelLayout.setHorizontalGroup(
         fillPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGap(0, 215, Short.MAX_VALUE)
      );
      fillPanelLayout.setVerticalGroup(
         fillPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGap(0, 151, Short.MAX_VALUE)
      );

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 1.0;
      legendPanel.add(fillPanel, gridBagConstraints);

      colorOptionPane.addTab("simple", legendPanel);

      blendPanel.setMaximumSize(new java.awt.Dimension(2147483647, 285));
      blendPanel.setMinimumSize(new java.awt.Dimension(180, 285));
      blendPanel.setName("blendPanel"); // NOI18N
      blendPanel.setPreferredSize(new java.awt.Dimension(200, 285));
      blendPanel.setRequestFocusEnabled(false);
      blendPanel.setLayout(new java.awt.GridBagLayout());

      blendSlider.setFont(new java.awt.Font("Dialog", 0, 8)); // NOI18N
      blendSlider.setMajorTickSpacing(20);
      blendSlider.setMinorTickSpacing(2);
      blendSlider.setPaintLabels(true);
      blendSlider.setPaintTicks(true);
      blendSlider.setValue(0);
      blendSlider.setMinimumSize(new java.awt.Dimension(36, 42));
      blendSlider.setName("blendSlider"); // NOI18N
      blendSlider.setPreferredSize(new java.awt.Dimension(200, 45));
      blendSlider.addChangeListener(new javax.swing.event.ChangeListener()
      {
         public void stateChanged(javax.swing.event.ChangeEvent evt)
         {
            blendSliderStateChanged(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.weightx = 1.0;
      blendPanel.add(blendSlider, gridBagConstraints);

      jPanel4.setName("jPanel4"); // NOI18N

      javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
      jPanel4.setLayout(jPanel4Layout);
      jPanel4Layout.setHorizontalGroup(
         jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGap(0, 0, Short.MAX_VALUE)
      );
      jPanel4Layout.setVerticalGroup(
         jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGap(0, 0, Short.MAX_VALUE)
      );

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
      gridBagConstraints.weighty = 1.0;
      blendPanel.add(jPanel4, gridBagConstraints);

      map1Panel.setName("map1Panel"); // NOI18N
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      blendPanel.add(map1Panel, gridBagConstraints);

      colorOptionPane.addTab("blend", blendPanel);

      modPanel.setName("modPanel"); // NOI18N
      modPanel.setLayout(new java.awt.GridBagLayout());

      buttonGroup1.add(satButton);
      satButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
      satButton.setText("saturation");
      satButton.setName("satButton"); // NOI18N
      satButton.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            satButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
      modPanel.add(satButton, gridBagConstraints);

      buttonGroup1.add(valButton);
      valButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
      valButton.setSelected(true);
      valButton.setText("brightness");
      valButton.setName("valButton"); // NOI18N
      valButton.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            valButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      modPanel.add(valButton, gridBagConstraints);

      colorModificationPanel.setName("colorModificationPanel"); // NOI18N
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.weightx = 1.0;
      modPanel.add(colorModificationPanel, gridBagConstraints);

      jPanel1.setName("jPanel1"); // NOI18N

      javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
      jPanel1.setLayout(jPanel1Layout);
      jPanel1Layout.setHorizontalGroup(
         jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGap(0, 94, Short.MAX_VALUE)
      );
      jPanel1Layout.setVerticalGroup(
         jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGap(0, 152, Short.MAX_VALUE)
      );

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 3;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.weighty = 1.0;
      modPanel.add(jPanel1, gridBagConstraints);

      colorOptionPane.addTab("modify", modPanel);

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 1.0;
      gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
      add(colorOptionPane, gridBagConstraints);

      jPanel2.setName("jPanel2"); // NOI18N

      javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
      jPanel2.setLayout(jPanel2Layout);
      jPanel2Layout.setHorizontalGroup(
         jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGap(0, 224, Short.MAX_VALUE)
      );
      jPanel2Layout.setVerticalGroup(
         jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGap(0, 4, Short.MAX_VALUE)
      );

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.weighty = 1.0;
      add(jPanel2, gridBagConstraints);
   }// </editor-fold>//GEN-END:initComponents

    private void satButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_satButtonActionPerformed
    {//GEN-HEADEREND:event_satButtonActionPerformed
       updateColorModification();
    }//GEN-LAST:event_satButtonActionPerformed

    private void valButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_valButtonActionPerformed
    {//GEN-HEADEREND:event_valButtonActionPerformed
       updateColorModification();
    }//GEN-LAST:event_valButtonActionPerformed

    private void blendSliderStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_blendSliderStateChanged
    {//GEN-HEADEREND:event_blendSliderStateChanged
       params.setAdjusting(blendSlider.getValueIsAdjusting());
       params.setBlendRatio(blendSlider.getValue() / 100.f);
    }//GEN-LAST:event_blendSliderStateChanged

   private void colorOptionPaneStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_colorOptionPaneStateChanged
   {//GEN-HEADEREND:event_colorOptionPaneStateChanged
      if (params == null)
          return;
      switch (colorOptionPane.getSelectedIndex())
      {
      case 0:
         params.setColorMapModification(DataMappingParams.NO_MAP_MODIFICATION);
         break;
      case 1:
         params.setColorMapModification(DataMappingParams.BLEND_MAP_MODIFICATION);
         break;
      case 2:
         if (satButton.isSelected())
            params.setColorMapModification(DataMappingParams.SAT_MAP_MODIFICATION);
         else
            params.setColorMapModification(DataMappingParams.VAL_MAP_MODIFICATION);
         updateColorModification();
         break;
      }
   }//GEN-LAST:event_colorOptionPaneStateChanged

    
   private void updateParams()
   {
      map0Params.setActive(false);
      map1Params.setActive(false);
      params.setColorMapModification(DataMappingParams.NO_MAP_MODIFICATION);
      params.setColorMode(DataMappingParams.COLORED);
      map0Params.setActive(true);
      map1Params.setActive(true);
      params.fireStateChanged(WIDTH);
   }
   
   public boolean isAdjusting() {
       return map0Panel.isAdjusting() || map1Panel.isAdjusting();
   }
   
   // Variables declaration - do not modify//GEN-BEGIN:variables
   private javax.swing.JPanel blendPanel;
   private javax.swing.JSlider blendSlider;
   private javax.swing.ButtonGroup buttonGroup1;
   private pl.edu.icm.visnow.geometries.gui.ColorComponentPanel colorModificationPanel;
   private javax.swing.JTabbedPane colorOptionPane;
   private pl.edu.icm.visnow.geometries.gui.ColormapLegendGUI colormapLegendGUI;
   private javax.swing.JPanel fillPanel;
   private javax.swing.JPanel jPanel1;
   private javax.swing.JPanel jPanel2;
   private javax.swing.JPanel jPanel4;
   private javax.swing.JPanel legendPanel;
   private pl.edu.icm.visnow.geometries.gui.ComponentColormappingPanel map0Panel;
   private pl.edu.icm.visnow.geometries.gui.ComponentColormappingPanel map1Panel;
   private javax.swing.JPanel modPanel;
   private javax.swing.JRadioButton satButton;
   private javax.swing.JRadioButton valButton;
   // End of variables declaration//GEN-END:variables

    /**
     * @return the map0Panel
     */
    public pl.edu.icm.visnow.geometries.gui.ComponentColormappingPanel getMap0Panel() {
        return map0Panel;
    }
}
