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
exception statement from your version. */
//</editor-fold>


package pl.edu.icm.visnow.geometries.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
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

    public void setPresentation(boolean simple) {
        GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints(0, 1, 1, 1, 1, 1, java.awt.GridBagConstraints.NORTHWEST, java.awt.GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);

        map0Panel.setPresentation(simple);
        colormapLegendGUI.setPresentation(simple);
        if (simple) {
            legendPanel.remove(colormapLegendGUI);
            colorOptionPane.setVisible(false);
            add(colormapLegendGUI, gridBagConstraints);
        } else {
            remove(colormapLegendGUI);
            legendPanel.add(colormapLegendGUI, gridBagConstraints);
            colorOptionPane.setVisible(true);
        }
        revalidate();
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
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup1 = new javax.swing.ButtonGroup();
        map0Panel = new pl.edu.icm.visnow.geometries.gui.ComponentColormappingPanel();
        colorOptionPane = new javax.swing.JTabbedPane();
        legendPanel = new javax.swing.JPanel();
        colormapLegendGUI = new pl.edu.icm.visnow.geometries.gui.ColormapLegendGUI();
        blendPanel = new javax.swing.JPanel();
        blendSlider = new javax.swing.JSlider();
        map1Panel = new pl.edu.icm.visnow.geometries.gui.ComponentColormappingPanel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        modPanel = new javax.swing.JPanel();
        satButton = new javax.swing.JRadioButton();
        valButton = new javax.swing.JRadioButton();
        colorModificationPanel = new pl.edu.icm.visnow.geometries.gui.ColorComponentPanel();

        setName(""); // NOI18N
        setVerifyInputWhenFocusTarget(false);
        setLayout(new java.awt.GridBagLayout());

        map0Panel.setBorder(null);
        map0Panel.setName("map0Panel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        add(map0Panel, gridBagConstraints);

        colorOptionPane.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        colorOptionPane.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        colorOptionPane.setName("colorOptionPane"); // NOI18N
        colorOptionPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                colorOptionPaneStateChanged(evt);
            }
        });

        legendPanel.setName("legendPanel"); // NOI18N
        legendPanel.setLayout(new java.awt.GridBagLayout());

        colormapLegendGUI.setName("colormapLegendGUI"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        legendPanel.add(colormapLegendGUI, gridBagConstraints);

        colorOptionPane.addTab("simple", legendPanel);

        blendPanel.setName("blendPanel"); // NOI18N
        blendPanel.setRequestFocusEnabled(false);
        blendPanel.setLayout(new java.awt.GridBagLayout());

        blendSlider.setFont(new java.awt.Font("Dialog", 0, 8)); // NOI18N
        blendSlider.setMajorTickSpacing(20);
        blendSlider.setMinorTickSpacing(2);
        blendSlider.setPaintLabels(true);
        blendSlider.setPaintTicks(true);
        blendSlider.setValue(0);
        blendSlider.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "blend ratio", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        blendSlider.setName("blendSlider"); // NOI18N
        blendSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                blendSliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        blendPanel.add(blendSlider, gridBagConstraints);

        map1Panel.setBorder(null);
        map1Panel.setName("map1Panel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        blendPanel.add(map1Panel, gridBagConstraints);

        filler1.setName("filler1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.weighty = 1.0;
        blendPanel.add(filler1, gridBagConstraints);

        colorOptionPane.addTab("blend", blendPanel);

        modPanel.setName("modPanel"); // NOI18N
        modPanel.setLayout(new java.awt.GridBagLayout());

        buttonGroup1.add(satButton);
        satButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        satButton.setText("saturation");
        satButton.setName("satButton"); // NOI18N
        satButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
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
        valButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
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
        gridBagConstraints.weighty = 1.0;
        modPanel.add(colorModificationPanel, gridBagConstraints);

        colorOptionPane.addTab("modify", modPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
        add(colorOptionPane, gridBagConstraints);
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

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled); //To change body of generated methods, choose Tools | Templates.
        map0Panel.setEnabled(enabled);
        colorOptionPane.setEnabled(enabled);        
        colormapLegendGUI.setEnabled(enabled);        
        blendSlider.setEnabled(enabled);        
        map1Panel.setEnabled(enabled);        
        satButton.setEnabled(enabled);        
        valButton.setEnabled(enabled);        
        colorModificationPanel.setEnabled(enabled);        
    }
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel blendPanel;
    private javax.swing.JSlider blendSlider;
    private javax.swing.ButtonGroup buttonGroup1;
    private pl.edu.icm.visnow.geometries.gui.ColorComponentPanel colorModificationPanel;
    private javax.swing.JTabbedPane colorOptionPane;
    private pl.edu.icm.visnow.geometries.gui.ColormapLegendGUI colormapLegendGUI;
    private javax.swing.Box.Filler filler1;
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
    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        final ColorMappedComponentPanel cmcp = new ColorMappedComponentPanel();
//        f.setSize(300,800);
        f.add(cmcp);
        f.pack();
        f.addMouseListener(new MouseAdapter() {
            private boolean toggleSimple = true;
            @Override
            public void mouseClicked(MouseEvent e) {
                cmcp.setPresentation(toggleSimple);
                toggleSimple = !toggleSimple;
                cmcp.revalidate();
            }
            
        });
        f.setVisible(true);
    }    
}
