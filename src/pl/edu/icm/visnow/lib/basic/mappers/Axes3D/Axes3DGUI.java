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

package pl.edu.icm.visnow.lib.basic.mappers.Axes3D;

import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.lib.utils.Utils;

/**
 *
 * @author  Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class Axes3DGUI extends javax.swing.JPanel
{
   private Field inField = null;
   private Axes3DParams params = new Axes3DParams();

   /** Creates new form Mapper3DUI */
   public Axes3DGUI()
   {
      initComponents();
      fontGUI.setInitBrightness(0);
   }

   /** This method is called from within the constructor to
    * initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is
    * always regenerated by the Form Editor.
    */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        xPanel = new javax.swing.JPanel();
        xLabelLabel = new javax.swing.JLabel();
        xLabelField = new javax.swing.JTextField();
        xFormatLabel = new javax.swing.JLabel();
        xFormatField = new javax.swing.JTextField();
        xGridBox = new javax.swing.JCheckBox();
        xGridLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        xyPosBox = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        xzPosBox = new javax.swing.JComboBox();
        yPanel = new javax.swing.JPanel();
        yLabelLabel = new javax.swing.JLabel();
        yLabelField = new javax.swing.JTextField();
        yFormatLabel = new javax.swing.JLabel();
        yFormatField = new javax.swing.JTextField();
        yGridLabel = new javax.swing.JLabel();
        yGridBox = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        yxPosBox = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        yzPosBox = new javax.swing.JComboBox();
        zPanel = new javax.swing.JPanel();
        zLabelLabel = new javax.swing.JLabel();
        zLabelField = new javax.swing.JTextField();
        zFormatLabel = new javax.swing.JLabel();
        zFormatField = new javax.swing.JTextField();
        zGridLabel = new javax.swing.JLabel();
        zGridBox = new javax.swing.JCheckBox();
        jLabel5 = new javax.swing.JLabel();
        zxPosBox = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        zyPosBox = new javax.swing.JComboBox();
        labelDensitySlider = new javax.swing.JSlider();
        jPanel2 = new javax.swing.JPanel();
        showBoxCB = new javax.swing.JCheckBox();
        showAxesCB = new javax.swing.JCheckBox();
        fontGUI = new pl.edu.icm.visnow.geometries.gui.FontGUI();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));

        setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        setLayout(new java.awt.GridBagLayout());

        xPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("x axis"));
        xPanel.setLayout(new java.awt.GridBagLayout());

        xLabelLabel.setText("label");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        xPanel.add(xLabelLabel, gridBagConstraints);

        xLabelField.setText("x");
        xLabelField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                xLabelFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        xPanel.add(xLabelField, gridBagConstraints);

        xFormatLabel.setText("format");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        xPanel.add(xFormatLabel, gridBagConstraints);

        xFormatField.setText("%4.1f");
        xFormatField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                xFormatFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        xPanel.add(xFormatField, gridBagConstraints);

        xGridBox.setSelected(true);
        xGridBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                xGridBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        xPanel.add(xGridBox, gridBagConstraints);

        xGridLabel.setText("grid lines");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 7);
        xPanel.add(xGridLabel, gridBagConstraints);

        jLabel1.setText("y position");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        xPanel.add(jLabel1, gridBagConstraints);

        xyPosBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "min", "0", "center", "max" }));
        xyPosBox.setMinimumSize(new java.awt.Dimension(62, 20));
        xyPosBox.setPreferredSize(new java.awt.Dimension(62, 21));
        xyPosBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                xyPosBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        xPanel.add(xyPosBox, gridBagConstraints);

        jLabel2.setText("z position");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        xPanel.add(jLabel2, gridBagConstraints);

        xzPosBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "min", "0", "center", "max" }));
        xzPosBox.setMinimumSize(new java.awt.Dimension(62, 20));
        xzPosBox.setPreferredSize(new java.awt.Dimension(62, 21));
        xzPosBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                xzPosBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        xPanel.add(xzPosBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        add(xPanel, gridBagConstraints);

        yPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("y axis"));
        yPanel.setLayout(new java.awt.GridBagLayout());

        yLabelLabel.setText("label");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        yPanel.add(yLabelLabel, gridBagConstraints);

        yLabelField.setText("x");
        yLabelField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                yLabelFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        yPanel.add(yLabelField, gridBagConstraints);

        yFormatLabel.setText("format");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        yPanel.add(yFormatLabel, gridBagConstraints);

        yFormatField.setText("%4.1f");
        yFormatField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                yFormatFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        yPanel.add(yFormatField, gridBagConstraints);

        yGridLabel.setText("grid lines");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 7);
        yPanel.add(yGridLabel, gridBagConstraints);

        yGridBox.setSelected(true);
        yGridBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                yGridBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        yPanel.add(yGridBox, gridBagConstraints);

        jLabel3.setText("x position");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        yPanel.add(jLabel3, gridBagConstraints);

        yxPosBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "min", "0", "center", "max" }));
        yxPosBox.setMinimumSize(new java.awt.Dimension(62, 20));
        yxPosBox.setPreferredSize(new java.awt.Dimension(62, 21));
        yxPosBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                yxPosBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        yPanel.add(yxPosBox, gridBagConstraints);

        jLabel4.setText("z position");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        yPanel.add(jLabel4, gridBagConstraints);

        yzPosBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "min", "0", "center", "max" }));
        yzPosBox.setMinimumSize(new java.awt.Dimension(62, 20));
        yzPosBox.setPreferredSize(new java.awt.Dimension(62, 21));
        yzPosBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                yzPosBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        yPanel.add(yzPosBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        add(yPanel, gridBagConstraints);

        zPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("z axis"));
        zPanel.setLayout(new java.awt.GridBagLayout());

        zLabelLabel.setText("label");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        zPanel.add(zLabelLabel, gridBagConstraints);

        zLabelField.setText("z");
        zLabelField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zLabelFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        zPanel.add(zLabelField, gridBagConstraints);

        zFormatLabel.setText("format");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        zPanel.add(zFormatLabel, gridBagConstraints);

        zFormatField.setText("%4.1f");
        zFormatField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zFormatFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        zPanel.add(zFormatField, gridBagConstraints);

        zGridLabel.setText("grid lines");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 7);
        zPanel.add(zGridLabel, gridBagConstraints);

        zGridBox.setSelected(true);
        zGridBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zGridBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        zPanel.add(zGridBox, gridBagConstraints);

        jLabel5.setText("x position");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        zPanel.add(jLabel5, gridBagConstraints);

        zxPosBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "min", "0", "center", "max" }));
        zxPosBox.setMinimumSize(new java.awt.Dimension(62, 20));
        zxPosBox.setPreferredSize(new java.awt.Dimension(62, 21));
        zxPosBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zxPosBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        zPanel.add(zxPosBox, gridBagConstraints);

        jLabel6.setText("y position");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        zPanel.add(jLabel6, gridBagConstraints);

        zyPosBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "min", "0", "center", "max" }));
        zyPosBox.setMinimumSize(new java.awt.Dimension(62, 20));
        zyPosBox.setPreferredSize(new java.awt.Dimension(62, 21));
        zyPosBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zyPosBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        zPanel.add(zyPosBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        add(zPanel, gridBagConstraints);

        labelDensitySlider.setMaximum(2000);
        labelDensitySlider.setMinimum(300);
        labelDensitySlider.setValue(600);
        labelDensitySlider.setBorder(javax.swing.BorderFactory.createTitledBorder("Label density"));
        labelDensitySlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                labelDensitySliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        add(labelDensitySlider, gridBagConstraints);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        showBoxCB.setSelected(true);
        showBoxCB.setText("show box");
        showBoxCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showBoxCBActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel2.add(showBoxCB, gridBagConstraints);

        showAxesCB.setSelected(true);
        showAxesCB.setText("show axes");
        showAxesCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showAxesCBActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel2.add(showAxesCB, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        add(jPanel2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        add(fontGUI, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.weighty = 1.0;
        add(filler1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

   private void xLabelFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_xLabelFieldActionPerformed
      params.setXLabel(xLabelField.getText());
}//GEN-LAST:event_xLabelFieldActionPerformed

   private void xFormatFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_xFormatFieldActionPerformed
      params.setXFormat(xFormatField.getText());
   }//GEN-LAST:event_xFormatFieldActionPerformed

private void yLabelFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_yLabelFieldActionPerformed
      params.setYLabel(yLabelField.getText());//GEN-LAST:event_yLabelFieldActionPerformed
      }

private void yFormatFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_yFormatFieldActionPerformed
      params.setYFormat(yFormatField.getText());
}//GEN-LAST:event_yFormatFieldActionPerformed

private void zLabelFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zLabelFieldActionPerformed
      params.setZLabel(zLabelField.getText());//GEN-LAST:event_zLabelFieldActionPerformed
      }

private void zFormatFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zFormatFieldActionPerformed
      params.setZFormat(zFormatField.getText());
}//GEN-LAST:event_zFormatFieldActionPerformed

private void xGridBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_xGridBoxActionPerformed
      params.setXGridLines(xGridBox.isSelected());
}//GEN-LAST:event_xGridBoxActionPerformed

private void yGridBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_yGridBoxActionPerformed
      params.setYGridLines(yGridBox.isSelected());
}//GEN-LAST:event_yGridBoxActionPerformed

private void zGridBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zGridBoxActionPerformed
      params.setZGridLines(zGridBox.isSelected());
}//GEN-LAST:event_zGridBoxActionPerformed

private void xyPosBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_xyPosBoxActionPerformed
      params.getAxPos()[0][0] = xyPosBox.getSelectedIndex();
      params.fireStateChanged();
}//GEN-LAST:event_xyPosBoxActionPerformed

private void xzPosBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_xzPosBoxActionPerformed
      params.getAxPos()[0][1] = xzPosBox.getSelectedIndex();
      params.fireStateChanged();
}//GEN-LAST:event_xzPosBoxActionPerformed

private void yxPosBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_yxPosBoxActionPerformed
      params.getAxPos()[1][0] = yxPosBox.getSelectedIndex();
      params.fireStateChanged();
}//GEN-LAST:event_yxPosBoxActionPerformed

private void yzPosBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_yzPosBoxActionPerformed
      params.getAxPos()[1][1] = yzPosBox.getSelectedIndex();
      params.fireStateChanged();
}//GEN-LAST:event_yzPosBoxActionPerformed

private void zxPosBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zxPosBoxActionPerformed
      params.getAxPos()[2][0] = zxPosBox.getSelectedIndex();
      params.fireStateChanged();
}//GEN-LAST:event_zxPosBoxActionPerformed

private void zyPosBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zyPosBoxActionPerformed
      params.getAxPos()[2][1] = zyPosBox.getSelectedIndex();
      params.fireStateChanged();
}//GEN-LAST:event_zyPosBoxActionPerformed

private void labelDensitySliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_labelDensitySliderStateChanged
      if (labelDensitySlider.getValueIsAdjusting())
         return;
      params.setLabelDensity(labelDensitySlider.getValue() / 100 + 2);
}//GEN-LAST:event_labelDensitySliderStateChanged

    private void showBoxCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showBoxCBActionPerformed
        params.setBox(showBoxCB.isSelected());
    }//GEN-LAST:event_showBoxCBActionPerformed

    private void showAxesCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showAxesCBActionPerformed
        params.setAxes(showAxesCB.isSelected());
    }//GEN-LAST:event_showAxesCBActionPerformed

    public void setInfield(Field inField)
    {
        params.setActive(false);
        String[] axesNames = inField.getAxesNames();
        String[] oldAxesNames = params.getAxDescs();
        if (!Utils.isEqualStringArrays(axesNames, oldAxesNames)) {
            params.setActive(false);
            if (axesNames != null && axesNames.length > 0) {
                params.setXLabel(axesNames[0]);
                setXLabel(axesNames[0]);
                if (axesNames.length > 1) {
                    params.setYLabel(axesNames[1]);
                    setYLabel(axesNames[1]);
                    if (axesNames.length > 2) {
                        params.setZLabel(axesNames[2]);
                        setZLabel(axesNames[2]);
                    }
                }
            } else {
                params.setXLabel("x");
                setXLabel("x");
                params.setYLabel("y");
                setYLabel("y");
                params.setZLabel("z");
                setZLabel("z");
            }
        }
        if(check1D(inField)) {
            params.setBox(false);
            params.setAxes(false);
        }
        updateGUI();
        params.setActive(true);
    }
    
    private void updateGUI() {
        showAxesCB.setSelected(params.isAxes());
        showBoxCB.setSelected(params.isBox());
        //TODO update rest of GUI
    }

   public void setParams(Axes3DParams params)
   {
      this.params = params;
      fontGUI.setParams(params.getFontParams());

   }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.Box.Filler filler1;
    private pl.edu.icm.visnow.geometries.gui.FontGUI fontGUI;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSlider labelDensitySlider;
    private javax.swing.JCheckBox showAxesCB;
    private javax.swing.JCheckBox showBoxCB;
    private javax.swing.JTextField xFormatField;
    private javax.swing.JLabel xFormatLabel;
    private javax.swing.JCheckBox xGridBox;
    private javax.swing.JLabel xGridLabel;
    private javax.swing.JTextField xLabelField;
    private javax.swing.JLabel xLabelLabel;
    private javax.swing.JPanel xPanel;
    private javax.swing.JComboBox xyPosBox;
    private javax.swing.JComboBox xzPosBox;
    private javax.swing.JTextField yFormatField;
    private javax.swing.JLabel yFormatLabel;
    private javax.swing.JCheckBox yGridBox;
    private javax.swing.JLabel yGridLabel;
    private javax.swing.JTextField yLabelField;
    private javax.swing.JLabel yLabelLabel;
    private javax.swing.JPanel yPanel;
    private javax.swing.JComboBox yxPosBox;
    private javax.swing.JComboBox yzPosBox;
    private javax.swing.JTextField zFormatField;
    private javax.swing.JLabel zFormatLabel;
    private javax.swing.JCheckBox zGridBox;
    private javax.swing.JLabel zGridLabel;
    private javax.swing.JTextField zLabelField;
    private javax.swing.JLabel zLabelLabel;
    private javax.swing.JPanel zPanel;
    private javax.swing.JComboBox zxPosBox;
    private javax.swing.JComboBox zyPosBox;
    // End of variables declaration//GEN-END:variables


   public void setXLabel(String label) {
       this.xLabelField.setText(label);
   }

   public void setYLabel(String label) {
       this.yLabelField.setText(label);
   }

   public void setZLabel(String label) {
       this.zLabelField.setText(label);
   }

    private boolean check1D(Field field) {
        float[][] ext = field.getExtents();
        float[][] physExts = ext;
        if (field.getPhysExts() != null) {
            physExts = field.getPhysExts();
        }
        float r = 0;
        float x0 = ext[0][0];
        float x1 = ext[1][0];
        float y0 = ext[0][1];
        float y1 = ext[1][1];
        float z0 = ext[0][2];
        float z1 = ext[1][2];
        float sx = (physExts[1][0] - physExts[0][0]) / (x1 - x0);
        float sy = (physExts[1][1] - physExts[0][1]) / (y1 - y0);
        float sz = (physExts[1][2] - physExts[0][2]) / (z1 - z0);
        if (r < x1 - x0) {
            r = x1 - x0;
        }
        if (r < y1 - y0) {
            r = y1 - y0;
        }
        if (r < z1 - z0) {
            r = z1 - z0;
        }
        int drawn = 3;
        boolean drawX = x1 - x0 > .1f * r;
        boolean drawY = y1 - y0 > .1f * r;
        boolean drawZ = z1 - z0 > .1f * r;
        if (!drawX) {
            drawn -= 1;
        }
        if (!drawY) {
            drawn -= 1;
        }
        if (!drawZ) {
            drawn -= 1;
        }
        return (drawn <= 1);
    }
}