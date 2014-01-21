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

package pl.edu.icm.visnow.lib.basic.viewers.Viewer2D;

import java.text.DecimalFormat;
import pl.edu.icm.visnow.gui.events.FloatValueModificationEvent;
import pl.edu.icm.visnow.gui.events.FloatValueModificationListener;
import pl.edu.icm.visnow.lib.utils.geometry2D.TransformedGeometryObject2D;

/**
 * @author  Bartosz Borucki (babor@icm.edu.pl)
 * Warsaw University, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class Display2DObjectEditor extends javax.swing.JPanel {
    private TransformedGeometryObject2D obj = null;
    private Display2DPanel panel = null;
    private boolean relative = true;
    private DecimalFormat df = new DecimalFormat("###.##");
    private boolean scaleChained = true;
    
    /** Creates new form Display2DObjectEditor */
    public Display2DObjectEditor() {
        initComponents();
        relativeRB.setSelected(true);
        relative = relativeRB.isSelected();
        setEnabled(false);
        scaleChainButton.setSelected(scaleChained);
        xPositionRoller.addFloatValueModificationListener(
                new FloatValueModificationListener()
                {
                   public void floatValueChanged(FloatValueModificationEvent e)
                   {
                       xPositionTF.setText(replaceComa(df.format(e.getVal())));
                       updatePositionX();
                   }           
                 });
        yPositionRoller.addFloatValueModificationListener(
                new FloatValueModificationListener()
                {
                   public void floatValueChanged(FloatValueModificationEvent e)
                   {
                       yPositionTF.setText(replaceComa(df.format(e.getVal())));
                       updatePositionY();
                   }           
                 });
        xScaleRoller.addFloatValueModificationListener(
                new FloatValueModificationListener()
                {
                   public void floatValueChanged(FloatValueModificationEvent e)
                   {
                      if(scaleChained) 
                      {
                         xScaleTF.setText(replaceComa(df.format(Math.pow(10.0, e.getVal()))));
                         yScaleTF.setText(replaceComa(df.format(Math.pow(10.0, e.getVal()))));
                         updateScales();            
                      } 
                      else 
                      {
                         xScaleTF.setText(replaceComa(df.format(Math.pow(10.0, e.getVal()))));
                          updateScales();            
                      } 
                    }           
                 });  
        yScaleRoller.addFloatValueModificationListener(
                new FloatValueModificationListener()
                {
                   public void floatValueChanged(FloatValueModificationEvent e)
                   {
                      if(scaleChained) 
                      {
                         xScaleTF.setText(replaceComa(df.format(Math.pow(10.0, e.getVal()))));
                         yScaleTF.setText(replaceComa(df.format(Math.pow(10.0, e.getVal()))));
                         updateScales();            
                      } 
                      else 
                      {
                          yScaleTF.setText(replaceComa(df.format(Math.pow(10.0, e.getVal()))));
                          updateScales();            
                      } 
                    }           
                 });  
                 
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        positionBG = new javax.swing.ButtonGroup();
        visibilityPanel = new javax.swing.JPanel();
        visibleTB = new javax.swing.JToggleButton();
        transparencySlider = new javax.swing.JSlider();
        positionPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        absoluteRB = new javax.swing.JRadioButton();
        relativeRB = new javax.swing.JRadioButton();
        centerButton = new javax.swing.JButton();
        xPositionLabel = new javax.swing.JLabel();
        yPositionLabel = new javax.swing.JLabel();
        xPosResetButton = new javax.swing.JButton();
        yPosResetButton = new javax.swing.JButton();
        xPositionTF = new javax.swing.JTextField();
        yPositionTF = new javax.swing.JTextField();
        yPositionRoller = new pl.edu.icm.visnow.gui.widgets.UnboundedRoller.UnboundedRoller();
        xPositionRoller = new pl.edu.icm.visnow.gui.widgets.UnboundedRoller.UnboundedRoller();
        scalePanel = new javax.swing.JPanel();
        xScaleLabel = new javax.swing.JLabel();
        yScaleLabel = new javax.swing.JLabel();
        xScaleResetButton = new javax.swing.JButton();
        yScaleResetButton = new javax.swing.JButton();
        xScaleTF = new javax.swing.JTextField();
        yScaleTF = new javax.swing.JTextField();
        xScaleRoller = new pl.edu.icm.visnow.gui.widgets.UnboundedRoller.UnboundedRoller();
        yScaleRoller = new pl.edu.icm.visnow.gui.widgets.UnboundedRoller.UnboundedRoller();
        scaleChainButton = new pl.edu.icm.visnow.gui.widgets.ChainTogglePanel();

        setMinimumSize(new java.awt.Dimension(240, 280));
        setPreferredSize(new java.awt.Dimension(240, 280));
        setLayout(new java.awt.GridBagLayout());

        visibilityPanel.setLayout(new java.awt.GridBagLayout());

        visibleTB.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        visibleTB.setSelected(true);
        visibleTB.setText("Visible");
        visibleTB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                visibleTBActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        visibilityPanel.add(visibleTB, gridBagConstraints);

        transparencySlider.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        transparencySlider.setValue(0);
        transparencySlider.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "transparency", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 11))); // NOI18N
        transparencySlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                transparencySliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        visibilityPanel.add(transparencySlider, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        add(visibilityPanel, gridBagConstraints);

        positionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("position"));
        positionPanel.setLayout(new java.awt.GridBagLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        positionBG.add(absoluteRB);
        absoluteRB.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        absoluteRB.setText("Absolute");
        absoluteRB.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        absoluteRB.setMargin(new java.awt.Insets(0, 0, 0, 0));
        absoluteRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                absoluteRBActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 0, 0);
        jPanel1.add(absoluteRB, gridBagConstraints);

        positionBG.add(relativeRB);
        relativeRB.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        relativeRB.setSelected(true);
        relativeRB.setText("Relative");
        relativeRB.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        relativeRB.setMargin(new java.awt.Insets(0, 0, 0, 0));
        relativeRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                relativeRBActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 0, 0);
        jPanel1.add(relativeRB, gridBagConstraints);

        centerButton.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        centerButton.setText("Center");
        centerButton.setMargin(new java.awt.Insets(2, 10, 2, 10));
        centerButton.setMaximumSize(new java.awt.Dimension(70, 24));
        centerButton.setMinimumSize(new java.awt.Dimension(70, 24));
        centerButton.setPreferredSize(new java.awt.Dimension(70, 24));
        centerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                centerButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel1.add(centerButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        positionPanel.add(jPanel1, gridBagConstraints);

        xPositionLabel.setText("x");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        positionPanel.add(xPositionLabel, gridBagConstraints);

        yPositionLabel.setText("y");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        positionPanel.add(yPositionLabel, gridBagConstraints);

        xPosResetButton.setText("*");
        xPosResetButton.setMargin(new java.awt.Insets(0, 2, 0, 1));
        xPosResetButton.setPreferredSize(new java.awt.Dimension(15, 20));
        xPosResetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                xPosResetButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 3, 0, 5);
        positionPanel.add(xPosResetButton, gridBagConstraints);

        yPosResetButton.setText("*");
        yPosResetButton.setMargin(new java.awt.Insets(0, 2, 0, 1));
        yPosResetButton.setPreferredSize(new java.awt.Dimension(15, 20));
        yPosResetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                yPosResetButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 3, 0, 5);
        positionPanel.add(yPosResetButton, gridBagConstraints);

        xPositionTF.setText("0.0");
        xPositionTF.setMaximumSize(new java.awt.Dimension(60, 20));
        xPositionTF.setMinimumSize(new java.awt.Dimension(60, 20));
        xPositionTF.setPreferredSize(new java.awt.Dimension(60, 20));
        xPositionTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                xPositionTFActionPerformed(evt);
            }
        });
        xPositionTF.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                xPositionTFFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 3);
        positionPanel.add(xPositionTF, gridBagConstraints);

        yPositionTF.setText("0.0");
        yPositionTF.setMaximumSize(new java.awt.Dimension(60, 20));
        yPositionTF.setMinimumSize(new java.awt.Dimension(60, 20));
        yPositionTF.setPreferredSize(new java.awt.Dimension(60, 20));
        yPositionTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                yPositionTFActionPerformed(evt);
            }
        });
        yPositionTF.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                yPositionTFFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 2);
        positionPanel.add(yPositionTF, gridBagConstraints);

        yPositionRoller.setMaximumSize(new java.awt.Dimension(60, 24));
        yPositionRoller.setMinimumSize(new java.awt.Dimension(60, 24));
        yPositionRoller.setOutValue(0.0F);
        yPositionRoller.setPreferredSize(new java.awt.Dimension(60, 24));
        yPositionRoller.setSensitivity(1.0F);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        positionPanel.add(yPositionRoller, gridBagConstraints);

        xPositionRoller.setMaximumSize(new java.awt.Dimension(60, 24));
        xPositionRoller.setMinimumSize(new java.awt.Dimension(60, 24));
        xPositionRoller.setOutValue(0.0F);
        xPositionRoller.setPreferredSize(new java.awt.Dimension(60, 24));
        xPositionRoller.setSensitivity(1.0F);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        positionPanel.add(xPositionRoller, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        add(positionPanel, gridBagConstraints);

        scalePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("scale"));
        scalePanel.setLayout(new java.awt.GridBagLayout());

        xScaleLabel.setText("x");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 14, 0);
        scalePanel.add(xScaleLabel, gridBagConstraints);

        yScaleLabel.setText("y");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        scalePanel.add(yScaleLabel, gridBagConstraints);

        xScaleResetButton.setText("*");
        xScaleResetButton.setMargin(new java.awt.Insets(0, 2, 0, 1));
        xScaleResetButton.setPreferredSize(new java.awt.Dimension(15, 20));
        xScaleResetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                xScaleResetButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 15, 5);
        scalePanel.add(xScaleResetButton, gridBagConstraints);

        yScaleResetButton.setText("*");
        yScaleResetButton.setMargin(new java.awt.Insets(0, 2, 0, 1));
        yScaleResetButton.setPreferredSize(new java.awt.Dimension(15, 20));
        yScaleResetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                yScaleResetButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        scalePanel.add(yScaleResetButton, gridBagConstraints);

        xScaleTF.setText("1.0");
        xScaleTF.setMaximumSize(new java.awt.Dimension(60, 20));
        xScaleTF.setMinimumSize(new java.awt.Dimension(60, 20));
        xScaleTF.setPreferredSize(new java.awt.Dimension(60, 20));
        xScaleTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                xScaleTFActionPerformed(evt);
            }
        });
        xScaleTF.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                xScaleTFFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 10, 0);
        scalePanel.add(xScaleTF, gridBagConstraints);

        yScaleTF.setText("1.0");
        yScaleTF.setMaximumSize(new java.awt.Dimension(60, 20));
        yScaleTF.setMinimumSize(new java.awt.Dimension(60, 20));
        yScaleTF.setPreferredSize(new java.awt.Dimension(60, 20));
        yScaleTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                yScaleTFActionPerformed(evt);
            }
        });
        yScaleTF.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                yScaleTFFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        scalePanel.add(yScaleTF, gridBagConstraints);

        xScaleRoller.setMaximumSize(new java.awt.Dimension(60, 24));
        xScaleRoller.setMinimumSize(new java.awt.Dimension(60, 24));
        xScaleRoller.setOutValue(0.0F);
        xScaleRoller.setPreferredSize(new java.awt.Dimension(60, 24));
        xScaleRoller.setSensitivity(0.01F);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 10, 5);
        scalePanel.add(xScaleRoller, gridBagConstraints);

        yScaleRoller.setMaximumSize(new java.awt.Dimension(60, 24));
        yScaleRoller.setMinimumSize(new java.awt.Dimension(60, 24));
        yScaleRoller.setOutValue(0.0F);
        yScaleRoller.setPreferredSize(new java.awt.Dimension(60, 24));
        yScaleRoller.setSensitivity(0.01F);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        scalePanel.add(yScaleRoller, gridBagConstraints);

        scaleChainButton.setSelected(true);
        scaleChainButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scaleChainButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 3);
        scalePanel.add(scaleChainButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        add(scalePanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void yScaleResetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_yScaleResetButtonActionPerformed
        if(scaleChained) {
            xScaleTF.setText("1");
            yScaleTF.setText("1");
            updateScales();
        } else {
            yScaleTF.setText("1");
            updateScaleY();    
        }
    }//GEN-LAST:event_yScaleResetButtonActionPerformed

    private void xScaleResetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_xScaleResetButtonActionPerformed
        if(scaleChained) {
            xScaleTF.setText("1");
            yScaleTF.setText("1");
            updateScales();
        } else {
            xScaleTF.setText("1");
            updateScaleX();
        }
    }//GEN-LAST:event_xScaleResetButtonActionPerformed

    private void yPositionTFFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_yPositionTFFocusLost
        yPositionTFActionPerformed(null);
    }//GEN-LAST:event_yPositionTFFocusLost

    private void yPositionTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_yPositionTFActionPerformed
        if(obj == null) {
            yPositionTF.setText("0");
            return;
        }
        updatePositionY();
    }//GEN-LAST:event_yPositionTFActionPerformed

    private void xPositionTFFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_xPositionTFFocusLost
        xPositionTFActionPerformed(null);
    }//GEN-LAST:event_xPositionTFFocusLost

    private void xPositionTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_xPositionTFActionPerformed
        if(obj == null) {
            xPositionTF.setText("0");
            return;
        }
        updatePositionX();
    }//GEN-LAST:event_xPositionTFActionPerformed

    private void xScaleTFFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_xScaleTFFocusLost
        xScaleTFActionPerformed(null);
    }//GEN-LAST:event_xScaleTFFocusLost

    private void yScaleTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_yScaleTFActionPerformed
        if(scaleChained) {
            if(obj == null) {
                xScaleTF.setText("0.0");
                yScaleTF.setText("0.0");
                return;
            }
            xScaleTF.setText(yScaleTF.getText());
            updateScales();
        } else {
            if(obj == null) {
                yScaleTF.setText("0.0");
                return;
            }
            updateScaleY();
        }
    }//GEN-LAST:event_yScaleTFActionPerformed

    private void xScaleTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_xScaleTFActionPerformed
        if(scaleChained) {
            if(obj == null) {
                xScaleTF.setText("0.0");
                yScaleTF.setText("0.0");
                return;
            }
            
            yScaleTF.setText(xScaleTF.getText());
            updateScales();
        } else {
            if(obj == null) {
                xScaleTF.setText("0.0");
                return;
            }
            updateScaleX();
        }
    }//GEN-LAST:event_xScaleTFActionPerformed

    private void visibleTBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_visibleTBActionPerformed
        if(obj == null) 
            return;
        obj.setVisible(visibleTB.isSelected());
        if(panel!=null)
            panel.update();
    }//GEN-LAST:event_visibleTBActionPerformed

    private void relativeRBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_relativeRBActionPerformed
        relative = relativeRB.isSelected();
        updateWidgets();
    }//GEN-LAST:event_relativeRBActionPerformed

    private void absoluteRBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_absoluteRBActionPerformed
        relativeRBActionPerformed(evt);
    }//GEN-LAST:event_absoluteRBActionPerformed

    private void centerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_centerButtonActionPerformed
        if(obj == null) 
            return;
        
        if(relative) {
            obj.centerToParent();
        } else {
            obj.centerTo(panel.getRootObject());
        }
        
        if(panel!=null)
            panel.update();
        
        updateWidgets();
    }//GEN-LAST:event_centerButtonActionPerformed

    private void yScaleTFFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_yScaleTFFocusLost
        yScaleTFActionPerformed(null);
        
    }//GEN-LAST:event_yScaleTFFocusLost

    private void xPosResetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_xPosResetButtonActionPerformed
        if(obj == null) 
            return;
        
        if(relative) {
            obj.xCenterToParent();
        } else {
            obj.xCenterTo(panel.getRootObject());
        }
        
        if(panel!=null)
            panel.update();
        
        updateWidgets();        
        
    }//GEN-LAST:event_xPosResetButtonActionPerformed

    private void yPosResetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_yPosResetButtonActionPerformed
        if(obj == null) 
            return;
        
        if(relative) {
            obj.yCenterToParent();
        } else {
            obj.yCenterTo(panel.getRootObject());
        }
        
        if(panel!=null)
            panel.update();
        
        updateWidgets();        
        
    }//GEN-LAST:event_yPosResetButtonActionPerformed

    private void scaleChainButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scaleChainButtonActionPerformed
        scaleChained = scaleChainButton.isSelected();
        if(scaleChained && !xScaleTF.getText().equals(yScaleTF.getText())) {
            yScaleTF.setText(xScaleTF.getText());
            updateScaleY();
        }
    }//GEN-LAST:event_scaleChainButtonActionPerformed

    private void transparencySliderStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_transparencySliderStateChanged
    {//GEN-HEADEREND:event_transparencySliderStateChanged
       if(obj == null || silent)
          return;
       obj.setOpacity(1.0f - transparencySlider.getValue()/100.0f);
       if(panel!=null)
          panel.update();
    }//GEN-LAST:event_transparencySliderStateChanged
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton absoluteRB;
    private javax.swing.JButton centerButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.ButtonGroup positionBG;
    private javax.swing.JPanel positionPanel;
    private javax.swing.JRadioButton relativeRB;
    private pl.edu.icm.visnow.gui.widgets.ChainTogglePanel scaleChainButton;
    private javax.swing.JPanel scalePanel;
    private javax.swing.JSlider transparencySlider;
    private javax.swing.JPanel visibilityPanel;
    private javax.swing.JToggleButton visibleTB;
    private javax.swing.JButton xPosResetButton;
    private javax.swing.JLabel xPositionLabel;
    private pl.edu.icm.visnow.gui.widgets.UnboundedRoller.UnboundedRoller xPositionRoller;
    private javax.swing.JTextField xPositionTF;
    private javax.swing.JLabel xScaleLabel;
    private javax.swing.JButton xScaleResetButton;
    private pl.edu.icm.visnow.gui.widgets.UnboundedRoller.UnboundedRoller xScaleRoller;
    private javax.swing.JTextField xScaleTF;
    private javax.swing.JButton yPosResetButton;
    private javax.swing.JLabel yPositionLabel;
    private pl.edu.icm.visnow.gui.widgets.UnboundedRoller.UnboundedRoller yPositionRoller;
    private javax.swing.JTextField yPositionTF;
    private javax.swing.JLabel yScaleLabel;
    private javax.swing.JButton yScaleResetButton;
    private pl.edu.icm.visnow.gui.widgets.UnboundedRoller.UnboundedRoller yScaleRoller;
    private javax.swing.JTextField yScaleTF;
    // End of variables declaration//GEN-END:variables
    
    public void setObject(TransformedGeometryObject2D obj) {
        if(obj == null || obj.getParent() == null) {
            visibleTB.setSelected(false);
            transparencySlider.setValue(transparencySlider.getMinimum());
            relativeRB.setSelected(true);
            xPositionTF.setText("0");
            yPositionTF.setText("0");
            xPositionRoller.setOutValue(0);
            yPositionRoller.setOutValue(0);
            xScaleTF.setText("1.0");
            yScaleTF.setText("1.0");
            xScaleRoller.setOutValue(1.0f);
            yScaleRoller.setOutValue(1.0f);
//            angleTF.setText("0.0");
//            angleRoller.setOutValue(0.0f);
            setEnabled(false);
            return;
        }
        
        setEnabled(true);
        this.obj = obj;
        updateWidgets();
    }
    
    public TransformedGeometryObject2D getObject() {
        return obj;
    }
    
    public void setPanel(Display2DPanel panel) {
        this.panel = panel;
    }
    
    public Display2DPanel getPanel() {
        return panel;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        visibleTB.setEnabled(enabled);
        transparencySlider.setEnabled(enabled);

        absoluteRB.setEnabled(enabled);
        relativeRB.setEnabled(enabled);
        xPositionLabel.setEnabled(enabled);
        yPositionLabel.setEnabled(enabled);
        xPositionTF.setEnabled(enabled);
        yPositionTF.setEnabled(enabled);
        centerButton.setEnabled(enabled);

        xScaleLabel.setEnabled(enabled);
        yScaleLabel.setEnabled(enabled);
        xScaleTF.setEnabled(enabled);
        yScaleTF.setEnabled(enabled);
    
//        angleTF.setEnabled(enabled);
    }

    private void updatePositionX() {
        float xpos;
        
        if(obj == null)
            return;
        
        try {
            xpos = Float.parseFloat(xPositionTF.getText());
        } catch (NumberFormatException ex) {
            System.out.println("unable to parse xPosition");
            if(relative) {
                 xPositionTF.setText(replaceComa(df.format(obj.getRelativePositionX())));
            } else {
                xPositionTF.setText(replaceComa(df.format(obj.getAbsolutePositionX())));
            }
            return;
        }
        
        if(relative) {
            obj.setRelativePositionX(xpos);
        } else {
            obj.setAbsolutePositionX(xpos);
        }
        updateWidgets();
        if(panel!=null)
            panel.update();
    }
    

    private void updatePositionY() {
        float ypos;

        if(obj == null)
            return;
        
        try {
            ypos = Float.parseFloat(yPositionTF.getText());
        } catch (NumberFormatException ex) {
            System.out.println("unable to parse yPosition");
            if(relative) {
                yPositionTF.setText(replaceComa(df.format(obj.getRelativePositionY())));
            } else {
                yPositionTF.setText(replaceComa(df.format(obj.getAbsolutePositionY())));
            }
            return;
        }
        
        if(relative) {
            obj.setRelativePositionY(ypos);
        } else {
            obj.setAbsolutePositionY(ypos);
        }
        updateWidgets();
        if(panel!=null)
            panel.update();
    }
    
    private boolean silent = false;
    
    public void updateWidgets() {
        if(obj == null)
            return;
    
        silent = true;
        visibleTB.setSelected(obj.isVisible());
        transparencySlider.setValue(Math.round(transparencySlider.getMaximum()*(1.0f-obj.getOpacity())));

        float x = 0;
        float y = 0;
        if(relative) {
            x = obj.getRelativePositionX();
            y = obj.getRelativePositionY();
        } else {
            x = obj.getAbsolutePositionX();
            y = obj.getAbsolutePositionY();
        }
        xPositionTF.setText(replaceComa(df.format(x)));
        yPositionTF.setText(replaceComa(df.format(y)));
        xPositionRoller.setOutValue(x);
        yPositionRoller.setOutValue(y);

        float sx = obj.getScaleX();
        float sy = obj.getScaleY();
        
        xScaleTF.setText(replaceComa(df.format(sx)));
        yScaleTF.setText(replaceComa(df.format(sy)));
        xScaleRoller.setOutValue((float)Math.log10(sx));
        yScaleRoller.setOutValue((float)Math.log10(sy));
        silent = false;
    }

    
    private void updateScaleY() {
        Float sy;
        if(obj == null)
            return;
        try {
            sy = Float.parseFloat(yScaleTF.getText());
        } catch (NumberFormatException ex) {
            yScaleTF.setText(replaceComa(df.format(obj.getAbsoluteTransform().getScaleY())));
            return;
        }
        obj.setScaleY(sy);
        updateWidgets();

        if(panel!=null)
            panel.update();
    }

    private void updateScaleX() {
        Float sx;
        if(obj == null)
            return;
        try {
            sx = Float.parseFloat(xScaleTF.getText());
        } catch (NumberFormatException ex) {
            xScaleTF.setText(replaceComa(df.format(obj.getAbsoluteTransform().getScaleX())));
            return;
        }
        obj.setScaleX(sx);
        updateWidgets();
        if(panel!=null)
            panel.update();
    }

    private void updateScales() {
        Float sx;
        Float sy;
        if(obj == null)
            return;
        try {
            sx = Float.parseFloat(xScaleTF.getText());
        } catch (NumberFormatException ex) {
            xScaleTF.setText(replaceComa(df.format(obj.getAbsoluteTransform().getScaleX())));
            return;
        }
        try {
            sy = Float.parseFloat(yScaleTF.getText());
        } catch (NumberFormatException ex) {
            yScaleTF.setText(replaceComa(df.format(obj.getAbsoluteTransform().getScaleY())));
            return;
        }
        obj.setScale(sx, sy);
        updateWidgets();
        if(panel!=null)
            panel.update();
    }
    
    
    private String replaceComa(String str) {
        return str.replace(",",".");
    }
    
    
    
}

