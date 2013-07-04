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

package pl.edu.icm.visnow.lib.basic.mappers.Glyphs;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.util.Hashtable;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.geometries.geometryTemplates.ScalarGlyphTemplates;
import pl.edu.icm.visnow.geometries.geometryTemplates.VectorGlyphTemplates;
import pl.edu.icm.visnow.geometries.gui.DataMappingGUI;
import pl.edu.icm.visnow.geometries.parameters.AbstractDataMappingParams;
import pl.edu.icm.visnow.gui.widgets.MultistateButton;
import pl.edu.icm.visnow.gui.events.BooleanChangeListener;
import pl.edu.icm.visnow.gui.events.BooleanEvent;
import pl.edu.icm.visnow.system.main.VisNow;

/**
 *
 * @author  Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class GlyphsGUI extends javax.swing.JPanel
{
   private GlyphsParams params = new GlyphsParams();
   private Field inField = null;
   private Hashtable<Integer, JLabel> downLabels = new Hashtable<Integer, JLabel>();
   private int[] down =
   {
      1, 2, 5, 10, 20, 50, 100, 200, 500, 1000, 2000, 5000, 10000, 20000, 50000, 100000
   };
   private String[] downTexts =
   {
      "1", "", "", "10", "", "", "100", "", "", "1000", "", "", "1e4", "", "", "1e5"
   };
   private float smax = 1;
   private int[] downsize;
   private BooleanChangeListener presentationListener = new BooleanChangeListener()  
   {        
      @Override
      public void booleanChanged(BooleanEvent e)
      {
         setPresentation(e.getState());
      }
      @Override
      public void stateChanged(ChangeEvent e)
      {
      }
   };


   public void setPresentation(boolean simple)
   {
      GridBagConstraints gridBagConstraints;
      Dimension simpleDim = new Dimension(200, 300);
      Dimension expertDim = new Dimension(220, 530);
      dataMappingGUI.setPresentation(simple);
      if (simple)
      {
         jTabbedPane1.removeAll();
         remove(jTabbedPane1);
         glyphPanel.remove(extraGlyphControlsPanel);
         glyphPanel.remove(fillPanel);         
         glyphPanel.setMinimumSize(simpleDim);
         glyphPanel.setPreferredSize(simpleDim);
         glyphPanel.setMaximumSize(simpleDim);
         dataMappingGUI.setMinimumSize(simpleDim);
         dataMappingGUI.setPreferredSize(simpleDim);
         dataMappingGUI.setMaximumSize(simpleDim);
         
         gridBagConstraints = new java.awt.GridBagConstraints();
         gridBagConstraints.gridx = 0;
         gridBagConstraints.gridy = 1;
         gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
         gridBagConstraints.weightx = 1.0;
         add(glyphPanel, gridBagConstraints);
         gridBagConstraints = new java.awt.GridBagConstraints();
         gridBagConstraints.gridx = 0;
         gridBagConstraints.gridy = 2;
         gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
         gridBagConstraints.weightx = 1.0;
         add(dataMappingGUI, gridBagConstraints);
         gridBagConstraints = new java.awt.GridBagConstraints();
         gridBagConstraints.gridx = 0;
         gridBagConstraints.gridy = 3;
         gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
         gridBagConstraints.weighty = 1.0;
         add(fillPanel, gridBagConstraints);
      } else
      {   
         remove(glyphPanel);
         remove(dataMappingGUI);
         remove(fillPanel);
         glyphPanel.setMinimumSize(expertDim);
         glyphPanel.setPreferredSize(expertDim);
         glyphPanel.setMaximumSize(expertDim);
         gridBagConstraints = new java.awt.GridBagConstraints();
         gridBagConstraints.gridx = 0;
         gridBagConstraints.gridy = 6;
         gridBagConstraints.gridwidth = 2;
         gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
         gridBagConstraints.weightx = 1.0;
         glyphPanel.add(extraGlyphControlsPanel, gridBagConstraints);
         gridBagConstraints = new java.awt.GridBagConstraints();
         gridBagConstraints.gridx = 0;
         gridBagConstraints.gridy = 7;
         gridBagConstraints.gridwidth = 2;
         gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
         gridBagConstraints.weighty = 1.0;
         glyphPanel.add(fillPanel, gridBagConstraints);
         dataMappingGUI.setMinimumSize(expertDim);
         dataMappingGUI.setPreferredSize(expertDim);
         dataMappingGUI.setMaximumSize(expertDim);

        jTabbedPane1.addTab("glyphs", glyphPanel);
        jTabbedPane1.addTab("crop", jPanel1);
        jPanel6.add(dataMappingGUI, java.awt.BorderLayout.CENTER);
        jTabbedPane1.addTab("datamap", jPanel6);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jTabbedPane1, gridBagConstraints);
         
      }
      validate();
   }


   /** Creates new form VNgraph3DUI */
   public GlyphsGUI()
   {
      initComponents();
      guiPresentationButton.setState(VisNow.guiLevel);
      guiPresentationButton.setVisible(VisNow.allowGUISwitch);
      dataMappingGUI.setStartNullTransparencyComponent(true);
      for (int i = 0; i < downTexts.length; i++)
      {
         downLabels.put(i, new JLabel(downTexts[i]));
         downLabels.get(i).setFont(new java.awt.Font("Dialog", 0, 8));
      }
      downsizeSlider.setLabelTable(downLabels);
      mapComboBox.setAddNullComponent(true);
      mapComboBox.setTitle("glyph size component");
      thrComponentSelector.setAddNullComponent(true);
      thrComponentSelector.setScalarComponentsOnly(true);
      thrComponentSelector.setTitle("glyph threshold component");
      regularFieldDownsizeUI.addChangeListener(new ChangeListener()
      {
         @Override
         public void stateChanged(ChangeEvent e)
         {
            params.setActive(false);
            params.setDown(regularFieldDownsizeUI.getDownsize());
            params.setActive(true);
         }
      });
      glyphComboBox.setModel(new javax.swing.DefaultComboBoxModel(ScalarGlyphTemplates.getGlyphNames()));
   }

   /** This method is called from within the constructor to
    * initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is
    * always regenerated by the Form Editor.
    */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jTabbedPane1 = new javax.swing.JTabbedPane();
        glyphPanel = new javax.swing.JPanel();
        mapComboBox = new pl.edu.icm.visnow.lib.gui.DataComponentSelector();
        glyphComboBox = new javax.swing.JComboBox();
        lodSlider = new javax.swing.JSlider();
        absBox = new javax.swing.JCheckBox();
        sqrtBox = new javax.swing.JCheckBox();
        scaleSlider = new pl.edu.icm.visnow.gui.widgets.LogarithmicSlider();
        jPanel5 = new javax.swing.JPanel();
        downsizeSlider = new javax.swing.JSlider();
        regularFieldDownsizeUI = new pl.edu.icm.visnow.lib.gui.DownsizeUI();
        constantDiamBox = new javax.swing.JCheckBox();
        extraGlyphControlsPanel = new javax.swing.JPanel();
        glyphThicknessBox = new javax.swing.JCheckBox();
        glyphThicknessSlider = new pl.edu.icm.visnow.gui.widgets.FloatSlider();
        lineThicknessSlider = new javax.swing.JSlider();
        thrSlider = new pl.edu.icm.visnow.gui.widgets.FloatSlider();
        thrComponentSelector = new pl.edu.icm.visnow.lib.gui.DataComponentSelector();
        thrRelBox = new javax.swing.JCheckBox();
        fillPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        cropUI = new pl.edu.icm.visnow.lib.gui.CropUI();
        jPanel2 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        dataMappingGUI = new pl.edu.icm.visnow.geometries.gui.DataMappingGUI();
        guiPresentationButton = new MultistateButton(new String[]{"show simple GUI","show expert GUI"}, null);

        setMinimumSize(new java.awt.Dimension(200, 675));
        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(230, 810));
        setLayout(new java.awt.GridBagLayout());

        jTabbedPane1.setMinimumSize(new java.awt.Dimension(175, 550));
        jTabbedPane1.setPreferredSize(new java.awt.Dimension(200, 800));

        glyphPanel.setMinimumSize(new java.awt.Dimension(180, 600));
        glyphPanel.setPreferredSize(new java.awt.Dimension(200, 650));
        glyphPanel.setLayout(new java.awt.GridBagLayout());

        mapComboBox.setMinimumSize(new java.awt.Dimension(100, 30));
        mapComboBox.setPreferredSize(new java.awt.Dimension(200, 36));
        mapComboBox.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                mapComboBoxStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        glyphPanel.add(mapComboBox, gridBagConstraints);

        glyphComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "sphere", "cone", "diamond" }));
        glyphComboBox.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "glyph", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        glyphComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                glyphComboBoxItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        glyphPanel.add(glyphComboBox, gridBagConstraints);

        lodSlider.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        lodSlider.setMajorTickSpacing(5);
        lodSlider.setMaximum(20);
        lodSlider.setMinorTickSpacing(1);
        lodSlider.setPaintTicks(true);
        lodSlider.setSnapToTicks(true);
        lodSlider.setValue(1);
        lodSlider.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "glyph smoothness", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        lodSlider.setPreferredSize(new java.awt.Dimension(150, 50));
        lodSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                lodSliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        glyphPanel.add(lodSlider, gridBagConstraints);

        absBox.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        absBox.setSelected(true);
        absBox.setText("absolute value");
        absBox.setToolTipText("<html>if selected, glyphs scaled by |value|,<p>\nelse scaled by value - minvalue </html>");
        absBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        absBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                absBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 7, 5, 0);
        glyphPanel.add(absBox, gridBagConstraints);

        sqrtBox.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        sqrtBox.setText("square root scale");
        sqrtBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        sqrtBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sqrtBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 8, 5, 7);
        glyphPanel.add(sqrtBox, gridBagConstraints);

        scaleSlider.setAdjusting(true);
        scaleSlider.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "glyph scale", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        scaleSlider.setMinimumSize(new java.awt.Dimension(65, 65));
        scaleSlider.setPreferredSize(new java.awt.Dimension(200, 65));
        scaleSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                scaleSliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        glyphPanel.add(scaleSlider, gridBagConstraints);

        jPanel5.setMinimumSize(new java.awt.Dimension(10, 65));
        jPanel5.setOpaque(false);
        jPanel5.setLayout(new java.awt.CardLayout());

        downsizeSlider.setFont(new java.awt.Font("Dialog", 0, 8)); // NOI18N
        downsizeSlider.setMajorTickSpacing(1);
        downsizeSlider.setMaximum(15);
        downsizeSlider.setMinorTickSpacing(1);
        downsizeSlider.setPaintLabels(true);
        downsizeSlider.setPaintTicks(true);
        downsizeSlider.setSnapToTicks(true);
        downsizeSlider.setBorder(javax.swing.BorderFactory.createTitledBorder("downsize"));
        downsizeSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                downsizeSliderStateChanged(evt);
            }
        });
        jPanel5.add(downsizeSlider, "downsizeSlider");

        regularFieldDownsizeUI.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                regularFieldDownsizeUIStateChanged(evt);
            }
        });
        jPanel5.add(regularFieldDownsizeUI, "regularFieldDownsizeUI");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        glyphPanel.add(jPanel5, gridBagConstraints);

        constantDiamBox.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        constantDiamBox.setText("constant size glyphs");
        constantDiamBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                constantDiamBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        glyphPanel.add(constantDiamBox, gridBagConstraints);

        extraGlyphControlsPanel.setBackground(java.awt.SystemColor.control);
        extraGlyphControlsPanel.setLayout(new java.awt.GridBagLayout());

        glyphThicknessBox.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        glyphThicknessBox.setText("constant width arrows");
        glyphThicknessBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                glyphThicknessBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        extraGlyphControlsPanel.add(glyphThicknessBox, gridBagConstraints);

        glyphThicknessSlider.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "thickness of vector glyphs", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        glyphThicknessSlider.setMinimumSize(new java.awt.Dimension(90, 65));
        glyphThicknessSlider.setPreferredSize(new java.awt.Dimension(65, 65));
        glyphThicknessSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                glyphThicknessSliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        extraGlyphControlsPanel.add(glyphThicknessSlider, gridBagConstraints);

        lineThicknessSlider.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "line thickness", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        lineThicknessSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                lineThicknessSliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        extraGlyphControlsPanel.add(lineThicknessSlider, gridBagConstraints);

        thrSlider.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "threshold", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        thrSlider.setMinimumSize(new java.awt.Dimension(90, 65));
        thrSlider.setPreferredSize(new java.awt.Dimension(200, 65));
        thrSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                thrSliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        extraGlyphControlsPanel.add(thrSlider, gridBagConstraints);

        thrComponentSelector.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                thrComponentSelectorStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        extraGlyphControlsPanel.add(thrComponentSelector, gridBagConstraints);

        thrRelBox.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        thrRelBox.setText("<html>relative (draw if map component  &gt;  threshold * threshold component) </html>");
        thrRelBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                thrRelBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        extraGlyphControlsPanel.add(thrRelBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        glyphPanel.add(extraGlyphControlsPanel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        glyphPanel.add(fillPanel, gridBagConstraints);

        jTabbedPane1.addTab("glyphs", glyphPanel);

        jPanel1.setMinimumSize(new java.awt.Dimension(180, 200));
        jPanel1.setPreferredSize(new java.awt.Dimension(220, 200));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        cropUI.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        cropUI.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cropUIStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(cropUI, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(jPanel2, gridBagConstraints);

        jTabbedPane1.addTab("crop", jPanel1);

        jPanel6.setLayout(new java.awt.BorderLayout());

        dataMappingGUI.setPreferredSize(new java.awt.Dimension(235, 800));
        jPanel6.add(dataMappingGUI, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab("datamap", jPanel6);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jTabbedPane1, gridBagConstraints);

        guiPresentationButton.setBackground(new java.awt.Color(204, 204, 204));
        guiPresentationButton.setForeground(new java.awt.Color(0, 51, 153));
        guiPresentationButton.setText("multistateButton1");
        guiPresentationButton.setMaximumSize(new java.awt.Dimension(54, 24));
        guiPresentationButton.setMinimumSize(new java.awt.Dimension(54, 24));
        guiPresentationButton.setPreferredSize(new java.awt.Dimension(54, 24));
        guiPresentationButton.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                guiPresentationButtonStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(guiPresentationButton, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

private void downsizeSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_downsizeSliderStateChanged
   if (!downsizeSlider.getValueIsAdjusting())
      params.setDownsize(down[downsizeSlider.getValue()]);
}//GEN-LAST:event_downsizeSliderStateChanged

private void sqrtBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sqrtBoxActionPerformed
   params.setUseSqrt(sqrtBox.isSelected());
      setScaleMinMax();//GEN-LAST:event_sqrtBoxActionPerformed
   }

private void absBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_absBoxActionPerformed
   params.setUseAbs(absBox.isSelected());
      setScaleMinMax();//GEN-LAST:event_absBoxActionPerformed
   }

private void lodSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_lodSliderStateChanged
   if (lodSlider.getValueIsAdjusting())
      return;
   params.setLod(lodSlider.getValue());
}//GEN-LAST:event_lodSliderStateChanged

   private void glyphComboBoxItemStateChanged(java.awt.event.ItemEvent evt)//GEN-FIRST:event_glyphComboBoxItemStateChanged
   {//GEN-HEADEREND:event_glyphComboBoxItemStateChanged
      params.setType(glyphComboBox.getSelectedIndex());
}//GEN-LAST:event_glyphComboBoxItemStateChanged

private void scaleSliderStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_scaleSliderStateChanged
{//GEN-HEADEREND:event_scaleSliderStateChanged
   params.setScale(scaleSlider.getVal());
}//GEN-LAST:event_scaleSliderStateChanged

private void mapComboBoxStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_mapComboBoxStateChanged
{//GEN-HEADEREND:event_mapComboBoxStateChanged
   params.setActive(false);
   int m = mapComboBox.getComponent();
   params.setComponent(m);
   if (inField.getData(m) == null || inField.getData(m).getVeclen() == 1)
      glyphComboBox.setModel(new javax.swing.DefaultComboBoxModel(ScalarGlyphTemplates.getGlyphNames()));
   else
      glyphComboBox.setModel(new javax.swing.DefaultComboBoxModel(VectorGlyphTemplates.getGlyphNames()));
   params.setType(glyphComboBox.getSelectedIndex());
   if (m >= 0)
   {
      thrSlider.setMin(inField.getData(m).getMinv());
      thrSlider.setMax(inField.getData(m).getMaxv());
      setScaleMinMax();
   }
   params.setActive(true);
}//GEN-LAST:event_mapComboBoxStateChanged

private void thrSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_thrSliderStateChanged
   if (!thrSlider.isAdjusting())
      params.setThr(thrSlider.getVal());
}//GEN-LAST:event_thrSliderStateChanged

private void thrRelBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_thrRelBoxActionPerformed
{//GEN-HEADEREND:event_thrRelBoxActionPerformed
   params.setThrRelative(thrRelBox.isSelected());
}//GEN-LAST:event_thrRelBoxActionPerformed

private void thrComponentSelectorStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_thrComponentSelectorStateChanged
{//GEN-HEADEREND:event_thrComponentSelectorStateChanged
   params.setThrComponent(thrComponentSelector.getComponent());
}//GEN-LAST:event_thrComponentSelectorStateChanged

private void constantDiamBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_constantDiamBoxActionPerformed
   setScaleMinMax();
   params.setConstantDiam(constantDiamBox.isSelected());
}//GEN-LAST:event_constantDiamBoxActionPerformed

private void glyphThicknessBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_glyphThicknessBoxActionPerformed
{//GEN-HEADEREND:event_glyphThicknessBoxActionPerformed
   params.setConstantThickness(glyphThicknessBox.isSelected());
   setThicknessMinMax();
}//GEN-LAST:event_glyphThicknessBoxActionPerformed

private void glyphThicknessSliderStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_glyphThicknessSliderStateChanged
{//GEN-HEADEREND:event_glyphThicknessSliderStateChanged
    params.setThickness(glyphThicknessSlider.getVal());
}//GEN-LAST:event_glyphThicknessSliderStateChanged

private void lineThicknessSliderStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_lineThicknessSliderStateChanged
{//GEN-HEADEREND:event_lineThicknessSliderStateChanged
    params.setLineThickness(lineThicknessSlider.getValue()/10.f);
}//GEN-LAST:event_lineThicknessSliderStateChanged

private void cropUIStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_cropUIStateChanged
{//GEN-HEADEREND:event_cropUIStateChanged
   params.setCrop(cropUI.getLow(), cropUI.getUp());
}//GEN-LAST:event_cropUIStateChanged

private void regularFieldDownsizeUIStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_regularFieldDownsizeUIStateChanged
{//GEN-HEADEREND:event_regularFieldDownsizeUIStateChanged
   params.setDown(regularFieldDownsizeUI.getDownsize());
}//GEN-LAST:event_regularFieldDownsizeUIStateChanged

   private void guiPresentationButtonStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_guiPresentationButtonStateChanged
   {//GEN-HEADEREND:event_guiPresentationButtonStateChanged
      setPresentation(guiPresentationButton.getState() == VisNow.SIMPLE_GUI);
   }//GEN-LAST:event_guiPresentationButtonStateChanged

private String[] lastInComponents = null;

   private boolean componentsChanged(String[] inComponents)
   {
      boolean changed = false;
      if (lastInComponents == null || lastInComponents.length != inComponents.length)
      {
         lastInComponents = inComponents;
         return true;
      }
      for (int i = 0; i < inComponents.length; i++)
         if (inComponents[i].compareTo(lastInComponents[i]) != 0)
            return true;
      return false;
   }

   public void setInData(Field inField,  AbstractDataMappingParams mapParams)
   {
      params.setActive(false);
      boolean dimsChanged = false;
      if (this.inField != null && this.inField instanceof RegularField && inField instanceof RegularField)
      {
         int[] d1 =((RegularField)this.inField).getDims();
         int[] d2 =((RegularField)inField).getDims();
         if (d1.length != d2.length)
            dimsChanged = true;
         else
            for (int i = 0; i < d2.length; i++)
               if (d1[i] != d2[i])
                  dimsChanged = true;
      }
      if (this.inField == null ||
         !this.inField.isStructureCompatibleWith(inField) ||
         !this.inField.isDataCompatibleWith(inField) ||
         this.inField.getNNodes() != inField.getNNodes() ||
         dimsChanged)
      {
         CardLayout cl = (CardLayout)(jPanel5.getLayout());
         if (inField instanceof RegularField && 
            (this.inField == null || !(this.inField instanceof RegularField)) || dimsChanged)
         {
            RegularField regularInField = (RegularField) inField;
            if (regularInField.getDims() == null)
               return;
            int[] dims = regularInField.getDims();
            cropUI.setNewExtents(dims);
            params.setCrop(new int[dims.length], new int[dims.length]);
            for (int i = 0; i < dims.length; i++)
            {
               params.getUpCrop()[i] = dims[i];
               params.getLowCrop()[i] = 0;
            }
            int n = (int) (Math.pow(10000., 1. / dims.length));
            downsize = new int[dims.length];
            downsize[0] = (dims[0] + n - 1) / n;
            if (dims.length > 1)
               downsize[1] = (dims[1] + n - 1) / n;
            if (dims.length > 2)
               downsize[2] = (dims[2] + n - 1) / n;
            regularFieldDownsizeUI.setDownsize(downsize);
            regularFieldDownsizeUI.setVisible(true);
            downsizeSlider.setVisible(false);
            cl.show(jPanel5, "regularFieldDownsizeUI");
            cropUI.setVisible(true);
         }
         else if (!(inField instanceof RegularField) && (this.inField == null || (this.inField instanceof RegularField)))
         {
            cropUI.setVisible(false);
            regularFieldDownsizeUI.setVisible(false);
            downsizeSlider.setVisible(true);
            cl.show(jPanel5,  "downsizeSlider");
            params.setDownsize(inField.getNNodes() / 1000);
            for (int i = 0; i < down.length; i++)
               if (down[i] > params.getDownsize())
               {
                  downsizeSlider.setValue(i);
                  break;
               }
         }
      }

      this.inField = inField;
      String[] inComponents = new String[inField.getNData()];
      for (int i = 0; i < inField.getNData(); i++)
         inComponents[i] = inField.getData(i).getName();
      if (componentsChanged(inComponents))
      {
         mapComboBox.setDataSchema(inField.getSchema());
         thrComponentSelector.setDataSchema(inField.getSchema());
         thrComponentSelector.setComponent(-1);
         params.setComponent(mapComboBox.getComponent());
         DataArray mapData = inField.getData(mapComboBox.getComponent());
         if (mapData == null || mapData.getVeclen() == 1)
            glyphComboBox.setModel(new javax.swing.DefaultComboBoxModel(ScalarGlyphTemplates.getGlyphNames()));
         else
            glyphComboBox.setModel(new javax.swing.DefaultComboBoxModel(VectorGlyphTemplates.getGlyphNames()));
         params.setType(glyphComboBox.getSelectedIndex());
      }
      setScaleMinMax();
      dataMappingGUI.setInData(inField, mapParams);
      params.setThrComponent(-1);
      params.setActive(true);
   }

   private void setScaleMinMax()
   {
      float max = 1;
      float min = 0;
      if (inField.getData(params.getComponent()) != null)
      {
         max = inField.getData(params.getComponent()).getMaxv();
         min = inField.getData(params.getComponent()).getMinv();
      }
      if (absBox.isSelected())
      {
         if (min < -max)
            max = -min;
      } else
         max -= min;
      float[][] ext = inField.getExtents();
      double diam = 0;
      for (int i = 0; i < 3; i++)
         diam += (ext[1][i] - ext[0][i]) * (ext[1][i] - ext[0][i]);
      if (max <= 0)
         max = .001f;
      smax = (float) (Math.sqrt(diam / 30) / max);
      if (params.isUseSqrt())
         smax /= (float) Math.sqrt((double) smax);
      scaleSlider.setMax(smax);
      scaleSlider.setMin(smax/100);
      scaleSlider.setVal(smax/10);
   }

   private void setThicknessMinMax()
   {
      float[][] ext = inField.getExtents();
      double diam = 0;
      for (int i = 0; i < 3; i++)
         diam += (ext[1][i] - ext[0][i]) * (ext[1][i] - ext[0][i]);
      float sm = (float) (Math.sqrt(diam / 30));
      glyphThicknessSlider.setMax(sm);
      glyphThicknessSlider.setMin(sm/100);
      glyphThicknessSlider.setVal(sm/10);
   }

   public void setParams(GlyphsParams params)
   {
      this.params = params;
   }

   public DataMappingGUI getDataMappingGUI()
   {
      return dataMappingGUI;
   }
   
   public BooleanChangeListener getPresentationListener()
   {
      return presentationListener;
   }

   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox absBox;
    private javax.swing.JCheckBox constantDiamBox;
    private pl.edu.icm.visnow.lib.gui.CropUI cropUI;
    private pl.edu.icm.visnow.geometries.gui.DataMappingGUI dataMappingGUI;
    private javax.swing.JSlider downsizeSlider;
    private javax.swing.JPanel extraGlyphControlsPanel;
    private javax.swing.JPanel fillPanel;
    private javax.swing.JComboBox glyphComboBox;
    private javax.swing.JPanel glyphPanel;
    private javax.swing.JCheckBox glyphThicknessBox;
    private pl.edu.icm.visnow.gui.widgets.FloatSlider glyphThicknessSlider;
    private pl.edu.icm.visnow.gui.widgets.MultistateButton guiPresentationButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JSlider lineThicknessSlider;
    private javax.swing.JSlider lodSlider;
    private pl.edu.icm.visnow.lib.gui.DataComponentSelector mapComboBox;
    private pl.edu.icm.visnow.lib.gui.DownsizeUI regularFieldDownsizeUI;
    private pl.edu.icm.visnow.gui.widgets.LogarithmicSlider scaleSlider;
    private javax.swing.JCheckBox sqrtBox;
    private pl.edu.icm.visnow.lib.gui.DataComponentSelector thrComponentSelector;
    private javax.swing.JCheckBox thrRelBox;
    private pl.edu.icm.visnow.gui.widgets.FloatSlider thrSlider;
    // End of variables declaration//GEN-END:variables
}
