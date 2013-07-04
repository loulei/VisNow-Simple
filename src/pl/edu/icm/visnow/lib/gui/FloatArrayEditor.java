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

package pl.edu.icm.visnow.lib.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import static java.lang.Math.*;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JLabel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pl.edu.icm.visnow.lib.utils.Range;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class FloatArrayEditor extends javax.swing.JPanel
{

   private float vMin = 0, vMax = 255;
   private float physMin = 0, physMax = 255;
   private float[] thresholds = null;
   private boolean startSingle = true;
   private int decimals = 2;
   private boolean valuePreferred = true;

   /**
    * Creates new form FloatArrayEditor
    */
   public FloatArrayEditor()
   {
      initComponents();
      JLabel renderer = (JLabel) thresholdList.getCellRenderer();
      renderer.setFont(new Font(Font.DIALOG_INPUT, Font.PLAIN, 9));
      renderer.setHorizontalAlignment(SwingConstants.RIGHT);
   }

   public void setValuePreferred(boolean valuePreferred)
   {
      this.valuePreferred = valuePreferred;
   }
   
   public void setPresentation(boolean simple)
   {
      GridBagConstraints gridBagConstraints;
      Dimension simpleDim = new Dimension(200, 75);
      Dimension expertDim = new Dimension(200, 313);
      if (simple)
      {
         if (valuePreferred)
         {
            remove(linRangePanel);
            remove(logRangePanel);
            remove(arrayPanel);
            remove(fillPanel);
            singleValPanel.remove(setThrButton);
            singleValPanel.remove(addThrButton);
         }
         else
         {
            remove(linRangePanel);
            remove(logRangePanel);
            remove(arrayPanel);
            remove(fillPanel);
            remove(singleValPanel);
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.weightx = 1;
            add(nValsSlider, gridBagConstraints);
         }
         setMinimumSize(simpleDim);
         setPreferredSize(simpleDim);
         setMaximumSize(simpleDim);
      }
      else
      {
         setMinimumSize(expertDim);
         setPreferredSize(expertDim);
         setMaximumSize(expertDim);
         
         if (valuePreferred)
         {
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.weightx = 0.1;
            singleValPanel.add(setThrButton, gridBagConstraints);
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.weightx = 0.1;
            singleValPanel.add(addThrButton, gridBagConstraints);
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.weightx = 1.0;
            add(linRangePanel, gridBagConstraints);
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 2;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
            add(logRangePanel, gridBagConstraints);
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.gridheight = 3;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.weightx = 1.0;
            add(arrayPanel, gridBagConstraints);
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 3;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.weighty = 0.1;
            add(fillPanel, gridBagConstraints);
         }
         else
         {
            remove(nValsSlider);
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.gridwidth = 2;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.weightx = 1.0;
            add(singleValPanel, gridBagConstraints);
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.weightx = 1.0;
            add(linRangePanel, gridBagConstraints);
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 2;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
            add(logRangePanel, gridBagConstraints);
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.gridheight = 3;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.weightx = 1.0;
            add(arrayPanel, gridBagConstraints);
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 3;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.weighty = 0.1;
            add(fillPanel, gridBagConstraints);
         }
      }
   }

   /**
    * This method is called from within the constructor to initialize the form. WARNING: Do NOT
    * modify this code. The content of this method is always regenerated by the Form Editor.
    */
   @SuppressWarnings("unchecked")
   // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
   private void initComponents()
   {
      java.awt.GridBagConstraints gridBagConstraints;

      buttonGroup1 = new javax.swing.ButtonGroup();
      nValsSlider = new pl.edu.icm.visnow.gui.widgets.EnhancedIntSlider();
      linRangePanel = new javax.swing.JPanel();
      linCountSpinner = new javax.swing.JSpinner();
      jPanel9 = new javax.swing.JPanel();
      setLinRangeButton = new javax.swing.JButton();
      addLinRangeButton = new javax.swing.JButton();
      jLabel4 = new javax.swing.JLabel();
      jPanel2 = new javax.swing.JPanel();
      jLabel6 = new javax.swing.JLabel();
      minLinField = new javax.swing.JTextField();
      jLabel7 = new javax.swing.JLabel();
      maxLinField = new javax.swing.JTextField();
      logRangePanel = new javax.swing.JPanel();
      jLabel2 = new javax.swing.JLabel();
      minLogRangeField = new javax.swing.JTextField();
      jLabel3 = new javax.swing.JLabel();
      maxLogRangeField = new javax.swing.JTextField();
      jPanel8 = new javax.swing.JPanel();
      setLogRangeButton = new javax.swing.JButton();
      addLogRangeButton = new javax.swing.JButton();
      jLabel5 = new javax.swing.JLabel();
      eqSpacedLogRangeToggle = new javax.swing.JCheckBox();
      logValSlider = new javax.swing.JSlider();
      singleValPanel = new javax.swing.JPanel();
      thrSlider = new pl.edu.icm.visnow.gui.widgets.FloatSlider();
      setThrButton = new javax.swing.JRadioButton();
      addThrButton = new javax.swing.JRadioButton();
      arrayPanel = new javax.swing.JPanel();
      jScrollPane1 = new javax.swing.JScrollPane();
      thresholdList = new javax.swing.JList();
      clearThrButton = new javax.swing.JButton();
      deleteSelButton = new javax.swing.JButton();
      fillPanel = new javax.swing.JPanel();

      nValsSlider.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "number of isolines", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
      nValsSlider.setMax(200);
      nValsSlider.setMin(20);
      nValsSlider.setMinimumSize(new java.awt.Dimension(180, 63));
      nValsSlider.setName("nValsSlider"); // NOI18N
      nValsSlider.setPreferredSize(new java.awt.Dimension(220, 63));
      nValsSlider.setVal(50);
      nValsSlider.addChangeListener(new javax.swing.event.ChangeListener()
      {
         public void stateChanged(javax.swing.event.ChangeEvent evt)
         {
            nValsSliderStateChanged(evt);
         }
      });

      setMaximumSize(new java.awt.Dimension(300, 320));
      setMinimumSize(new java.awt.Dimension(180, 320));
      setPreferredSize(new java.awt.Dimension(210, 320));
      setRequestFocusEnabled(false);
      setLayout(new java.awt.GridBagLayout());

      linRangePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "linear range", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
      linRangePanel.setName("linRangePanel"); // NOI18N
      linRangePanel.setPreferredSize(new java.awt.Dimension(156, 123));
      linRangePanel.setLayout(new java.awt.GridBagLayout());

      linCountSpinner.setName("linCountSpinner"); // NOI18N
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.weightx = 0.1;
      linRangePanel.add(linCountSpinner, gridBagConstraints);

      jPanel9.setName("jPanel9"); // NOI18N
      jPanel9.setLayout(new java.awt.GridLayout(1, 0));

      setLinRangeButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
      setLinRangeButton.setText("set");
      setLinRangeButton.setMargin(new java.awt.Insets(0, 14, 0, 14));
      setLinRangeButton.setName("setLinRangeButton"); // NOI18N
      setLinRangeButton.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            setLinRangeButtonActionPerformed(evt);
         }
      });
      jPanel9.add(setLinRangeButton);

      addLinRangeButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
      addLinRangeButton.setText("add");
      addLinRangeButton.setMargin(new java.awt.Insets(0, 14, 0, 14));
      addLinRangeButton.setName("addLinRangeButton"); // NOI18N
      addLinRangeButton.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            addLinRangeButtonActionPerformed(evt);
         }
      });
      jPanel9.add(addLinRangeButton);

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
      linRangePanel.add(jPanel9, gridBagConstraints);

      jLabel4.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
      jLabel4.setText("thresholds");
      jLabel4.setName("jLabel4"); // NOI18N
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.weightx = 0.1;
      linRangePanel.add(jLabel4, gridBagConstraints);

      jPanel2.setName("jPanel2"); // NOI18N
      jPanel2.setLayout(new java.awt.GridLayout(2, 2));

      jLabel6.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
      jLabel6.setText("min");
      jLabel6.setName("jLabel6"); // NOI18N
      jPanel2.add(jLabel6);

      minLinField.setText("0");
      minLinField.setName("minLinField"); // NOI18N
      jPanel2.add(minLinField);

      jLabel7.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
      jLabel7.setText("max");
      jLabel7.setName("jLabel7"); // NOI18N
      jPanel2.add(jLabel7);

      maxLinField.setText("100");
      maxLinField.setName("maxLinField"); // NOI18N
      jPanel2.add(maxLinField);

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.insets = new java.awt.Insets(0, 6, 3, 0);
      linRangePanel.add(jPanel2, gridBagConstraints);

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.weightx = 1.0;
      add(linRangePanel, gridBagConstraints);

      logRangePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "logarithmic range", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
      logRangePanel.setInheritsPopupMenu(true);
      logRangePanel.setMinimumSize(new java.awt.Dimension(124, 128));
      logRangePanel.setName("logRangePanel"); // NOI18N
      logRangePanel.setPreferredSize(new java.awt.Dimension(155, 128));
      logRangePanel.setLayout(new java.awt.GridBagLayout());

      jLabel2.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
      jLabel2.setText("min");
      jLabel2.setName("jLabel2"); // NOI18N
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      logRangePanel.add(jLabel2, gridBagConstraints);

      minLogRangeField.setText(".01");
      minLogRangeField.setName("minLogRangeField"); // NOI18N
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
      logRangePanel.add(minLogRangeField, gridBagConstraints);

      jLabel3.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
      jLabel3.setText("max");
      jLabel3.setName("jLabel3"); // NOI18N
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.weightx = 0.1;
      logRangePanel.add(jLabel3, gridBagConstraints);

      maxLogRangeField.setText("100");
      maxLogRangeField.setName("maxLogRangeField"); // NOI18N
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.weightx = 0.1;
      gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
      logRangePanel.add(maxLogRangeField, gridBagConstraints);

      jPanel8.setName("jPanel8"); // NOI18N
      jPanel8.setLayout(new java.awt.GridLayout(1, 0));

      setLogRangeButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
      setLogRangeButton.setText("set");
      setLogRangeButton.setMargin(new java.awt.Insets(0, 14, 0, 14));
      setLogRangeButton.setName("setLogRangeButton"); // NOI18N
      setLogRangeButton.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            setLogRangeButtonActionPerformed(evt);
         }
      });
      jPanel8.add(setLogRangeButton);

      addLogRangeButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
      addLogRangeButton.setText("add");
      addLogRangeButton.setMargin(new java.awt.Insets(0, 14, 0, 14));
      addLogRangeButton.setName("addLogRangeButton"); // NOI18N
      addLogRangeButton.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            addLogRangeButtonActionPerformed(evt);
         }
      });
      jPanel8.add(addLogRangeButton);

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      logRangePanel.add(jPanel8, gridBagConstraints);

      jLabel5.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
      jLabel5.setText("<html>values /<p>decade</html>");
      jLabel5.setName("jLabel5"); // NOI18N
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 3;
      gridBagConstraints.gridheight = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.insets = new java.awt.Insets(5, 0, 1, 0);
      logRangePanel.add(jLabel5, gridBagConstraints);

      eqSpacedLogRangeToggle.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
      eqSpacedLogRangeToggle.setText("<html>equally<p> spaced</html>");
      eqSpacedLogRangeToggle.setName("eqSpacedLogRangeToggle"); // NOI18N
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 4;
      logRangePanel.add(eqSpacedLogRangeToggle, gridBagConstraints);

      logValSlider.setMaximum(9);
      logValSlider.setValue(2);
      logValSlider.setName("logValSlider"); // NOI18N
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 5;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      logRangePanel.add(logValSlider, gridBagConstraints);

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
      add(logRangePanel, gridBagConstraints);

      singleValPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "single threshold", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
      singleValPanel.setMaximumSize(new java.awt.Dimension(2147483647, 86));
      singleValPanel.setMinimumSize(new java.awt.Dimension(193, 85));
      singleValPanel.setName("singleValPanel"); // NOI18N
      singleValPanel.setPreferredSize(new java.awt.Dimension(220, 85));
      singleValPanel.setLayout(new java.awt.GridBagLayout());

      thrSlider.setName("thrSlider"); // NOI18N
      thrSlider.addChangeListener(new javax.swing.event.ChangeListener()
      {
         public void stateChanged(javax.swing.event.ChangeEvent evt)
         {
            thrSliderStateChanged(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.weightx = 1.0;
      singleValPanel.add(thrSlider, gridBagConstraints);

      buttonGroup1.add(setThrButton);
      setThrButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
      setThrButton.setSelected(true);
      setThrButton.setText("set");
      setThrButton.setMargin(new java.awt.Insets(0, 2, 0, 2));
      setThrButton.setName("setThrButton"); // NOI18N
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.weightx = 0.1;
      singleValPanel.add(setThrButton, gridBagConstraints);

      buttonGroup1.add(addThrButton);
      addThrButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
      addThrButton.setText("add");
      addThrButton.setMargin(new java.awt.Insets(2, 0, 2, 0));
      addThrButton.setMaximumSize(new java.awt.Dimension(44, 19));
      addThrButton.setMinimumSize(new java.awt.Dimension(44, 19));
      addThrButton.setName("addThrButton"); // NOI18N
      addThrButton.setPreferredSize(new java.awt.Dimension(44, 19));
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.weightx = 0.1;
      singleValPanel.add(addThrButton, gridBagConstraints);

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.weightx = 1.0;
      add(singleValPanel, gridBagConstraints);

      arrayPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "values", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
      arrayPanel.setMinimumSize(new java.awt.Dimension(72, 186));
      arrayPanel.setName("arrayPanel"); // NOI18N
      arrayPanel.setPreferredSize(new java.awt.Dimension(82, 206));
      arrayPanel.setLayout(new java.awt.GridBagLayout());

      jScrollPane1.setMinimumSize(new java.awt.Dimension(70, 100));
      jScrollPane1.setName("jScrollPane1"); // NOI18N
      jScrollPane1.setPreferredSize(new java.awt.Dimension(70, 120));

      thresholdList.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
      thresholdList.setModel(new javax.swing.AbstractListModel()
      {
         String[] strings = { "0", " " };
         public int getSize() { return strings.length; }
         public Object getElementAt(int i) { return strings[i]; }
      });
      thresholdList.setAlignmentX(0.0F);
      thresholdList.setName("thresholdList"); // NOI18N
      jScrollPane1.setViewportView(thresholdList);

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 1.0;
      arrayPanel.add(jScrollPane1, gridBagConstraints);

      clearThrButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
      clearThrButton.setText("clear list");
      clearThrButton.setMargin(new java.awt.Insets(4, 4, 4, 4));
      clearThrButton.setName("clearThrButton"); // NOI18N
      clearThrButton.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            clearThrButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 3;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.ipady = -6;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
      arrayPanel.add(clearThrButton, gridBagConstraints);

      deleteSelButton.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
      deleteSelButton.setText("<html>delete<p>selected</html>");
      deleteSelButton.setName("deleteSelButton"); // NOI18N
      deleteSelButton.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            deleteSelButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      arrayPanel.add(deleteSelButton, gridBagConstraints);

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.gridheight = 3;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.weightx = 1.0;
      add(arrayPanel, gridBagConstraints);

      fillPanel.setMinimumSize(new java.awt.Dimension(10, 1));
      fillPanel.setName("fillPanel"); // NOI18N
      fillPanel.setPreferredSize(new java.awt.Dimension(100, 1000));
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 3;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.weighty = 0.1;
      add(fillPanel, gridBagConstraints);
   }// </editor-fold>//GEN-END:initComponents

private void setLinRangeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setLinRangeButtonActionPerformed
   try
   {
      thresholds = Range.createLinearRange((Integer) linCountSpinner.getValue(), 
                                           Float.parseFloat(minLinField.getText()), 
                                           Float.parseFloat(maxLinField.getText()));
      setThresholds();
   } catch (Exception e)
   {
   }
}//GEN-LAST:event_setLinRangeButtonActionPerformed

private void addLinRangeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addLinRangeButtonActionPerformed
   try
   {
      mergeRange(Range.createLinearRange((Integer) linCountSpinner.getValue(), 
                                         Float.parseFloat(minLinField.getText()), 
                                         Float.parseFloat(maxLinField.getText())));
      setThresholds();
   } catch (Exception e)
   {
   }
}//GEN-LAST:event_addLinRangeButtonActionPerformed

private void clearThrButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearThrButtonActionPerformed
   float v = thrSlider.getVal();
   thresholds = new float[] {vMin + (v - physMin) * (vMax - vMin) / (physMax - physMin)};
   setThresholds();
}//GEN-LAST:event_clearThrButtonActionPerformed

private void setLogRangeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setLogRangeButtonActionPerformed
   try
   {
       thresholds = Range.createLogRange(logValSlider.getValue(), 
                                         Float.parseFloat(minLogRangeField.getText()), 
                                         Float.parseFloat(maxLogRangeField.getText()),
                                         eqSpacedLogRangeToggle.isSelected());
        setThresholds();
   } catch (Exception e)
   {
   }
}//GEN-LAST:event_setLogRangeButtonActionPerformed

private void addLogRangeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addLogRangeButtonActionPerformed
   try
   {
   mergeRange(Range.createLogRange(logValSlider.getValue(), 
                                   Float.parseFloat(minLogRangeField.getText()), 
                                   Float.parseFloat(maxLogRangeField.getText()),
                                   eqSpacedLogRangeToggle.isSelected()));
   setThresholds();
   } catch (Exception e)
   {
   }
}//GEN-LAST:event_addLogRangeButtonActionPerformed

private void thrSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_thrSliderStateChanged
   if (thrSlider.isAdjusting())
      return;
   if (thresholds == null || setThrButton.isSelected())
      thresholds = new float[]{thrSlider.getVal()};
   else
   {
      float[] oldThr = thresholds;
      int nThr = oldThr.length + 1;
      thresholds = new float[oldThr.length + 1];
      System.arraycopy(oldThr, 0, thresholds, 0, oldThr.length);
      thresholds[oldThr.length] = thrSlider.getVal();
   }
   setThresholds();
}//GEN-LAST:event_thrSliderStateChanged

private void deleteSelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteSelButtonActionPerformed
   if (thresholds == null)
      return;
   int[] toDel = thresholdList.getSelectedIndices();
   float[] tmpThr = new float[thresholds.length];
   int n = 0;
   for (int i = 0, k = 0; i < tmpThr.length; i++)
   {
      if (k < toDel.length && i == toDel[k])
         k += 1;
      else
      {
         tmpThr[n] = thresholds[i];
         n += 1;
      }
   }
   if (n == 0)
   {
      tmpThr[0] = thrSlider.getVal();
      n = 1;
   }
   thresholds = new float[n];
   System.arraycopy(tmpThr, 0, thresholds, 0, n);
   setThresholds();
}//GEN-LAST:event_deleteSelButtonActionPerformed

   private void nValsSliderStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_nValsSliderStateChanged
   {//GEN-HEADEREND:event_nValsSliderStateChanged
      if (nValsSlider.isAdjusting())
         return;
      thresholds = Range.createLinearRange(nValsSlider.getVal(), vMin, vMax);
      linCountSpinner.setValue(nValsSlider.getVal());
      setThresholds();
   }//GEN-LAST:event_nValsSliderStateChanged


   private void mergeRange(float[] range)
   {
      float[] oldThr = thresholds;
      int nThr = oldThr.length;
      if (range != null)
         nThr += range.length;
      thresholds = new float[nThr];
      System.arraycopy(oldThr, 0, thresholds, 0, oldThr.length);
      System.arraycopy(range, 0, thresholds, oldThr.length, range.length);
   }

   private void setThresholds()
   {
      float[] tmpThr = thresholds.clone();
      Arrays.sort(tmpThr);
      int nThr = 0;
      for (int i = 0; i < tmpThr.length; i++)
      {
         if (tmpThr[i] == tmpThr[nThr] && i > nThr || tmpThr[i] < physMin || tmpThr[i] > physMax)
            continue;
         tmpThr[nThr] = tmpThr[i];
         nThr += 1;
      }
      if (nThr == 0)
         return;
      thresholds = new float[nThr];
      System.arraycopy(tmpThr, 0, thresholds, 0, nThr);
      String format = "%." + (decimals + 2) + "f";
      String[] formattedThrs = new String[nThr];
      for (int i = 0; i < nThr; i++)
         formattedThrs[i] = String.format(format, thresholds[i]);
      thresholdList.setListData(formattedThrs);
      fireStateChanged();
   }

   public void setMinMax(float vMin, float vMax, float physMin, float physMax)
   {
      if (vMax < vMin + .0000001f)
         vMax = vMin + .0000001f;
      decimals = (int)(-Math.log10(vMax - vMin));
      if (decimals < 0) decimals = 0;
      decimals +=2;
      String format = "%."+decimals+"f";
      this.vMin = vMin;
      this.vMax = vMax;
      this.physMin = physMin;
      this.physMax = physMax;
      thrSlider.setMinMax(physMin, physMax);
      minLinField.setText(String.format(format, physMin));
      maxLinField.setText(String.format(format, physMax));
      float lMax = max(abs(physMin), abs(physMax));
      if (physMin > 0)
         minLogRangeField.setText(String.format(format, physMin));
      else
         minLogRangeField.setText(String.format(format, lMax / 1000));
      maxLogRangeField.setText(String.format(format, lMax));
      if (startSingle) {
         thresholds = new float[]
         {
            (physMin + physMax) / 2
         };      
         thrSlider.setVal(thresholds[0]);
      } else {
         try
         {
            int nDiv = (Integer) linCountSpinner.getValue();
            float rangeMin = Float.parseFloat(minLinField.getText());
            float rangeMax = Float.parseFloat(maxLinField.getText());
            thresholds = Range.createLinearRange(nDiv, rangeMin, rangeMax);
            
         } catch (Exception e)
         {
         }
      }
      setThresholds();
   }

   public void setMaxRangeCount(int count)
   {
      int step = count / 20;
      if (step < 1) step = 1;
      ((SpinnerNumberModel) (linCountSpinner.getModel())).setMaximum(count);
      ((SpinnerNumberModel) (linCountSpinner.getModel())).setStepSize(step);
      ((SpinnerNumberModel) (linCountSpinner.getModel())).setValue(count / 5);
   }

   public void setStartSingle(boolean startSingle)
   {
      this.startSingle = startSingle;
   }

   public float[] getThresholds()
   {
      if (thresholds == null)
         return null;
      float[] thr = new float[thresholds.length];
      for (int i = 0; i < thr.length; i++)
         thr[i] = vMin + (thresholds[i] - physMin) * (vMax - vMin) / (physMax - physMin);
      return thr;
   }
   /**
    * Utility field holding list of ChangeListeners.
    */
   private transient ArrayList<ChangeListener> changeListenerList =
           new ArrayList<ChangeListener>();

   /**
    * Registers ChangeListener to receive events.
    * @param listener The listener to register.
    */
   public synchronized void addChangeListener(ChangeListener listener)
   {
      changeListenerList.add(listener);
   }

   /**
    * Removes ChangeListener from the list of listeners.
    * @param listener The listener to remove.
    */
   public synchronized void removeChangeListener(ChangeListener listener)
   {
      changeListenerList.remove(listener);
   }

   /**
    * Notifies all registered listeners about the event.
    * 
    * @param object Parameter #1 of the <CODE>ChangeEvent<CODE> constructor.
    */
   private void fireStateChanged()
   {
      ChangeEvent e = new ChangeEvent(this);
      for (ChangeListener listener : changeListenerList)
         listener.stateChanged(e);
   }

   // Variables declaration - do not modify//GEN-BEGIN:variables
   private javax.swing.JButton addLinRangeButton;
   private javax.swing.JButton addLogRangeButton;
   private javax.swing.JRadioButton addThrButton;
   private javax.swing.JPanel arrayPanel;
   private javax.swing.ButtonGroup buttonGroup1;
   private javax.swing.JButton clearThrButton;
   private javax.swing.JButton deleteSelButton;
   private javax.swing.JCheckBox eqSpacedLogRangeToggle;
   private javax.swing.JPanel fillPanel;
   private javax.swing.JLabel jLabel2;
   private javax.swing.JLabel jLabel3;
   private javax.swing.JLabel jLabel4;
   private javax.swing.JLabel jLabel5;
   private javax.swing.JLabel jLabel6;
   private javax.swing.JLabel jLabel7;
   private javax.swing.JPanel jPanel2;
   private javax.swing.JPanel jPanel8;
   private javax.swing.JPanel jPanel9;
   private javax.swing.JScrollPane jScrollPane1;
   private javax.swing.JSpinner linCountSpinner;
   private javax.swing.JPanel linRangePanel;
   private javax.swing.JPanel logRangePanel;
   private javax.swing.JSlider logValSlider;
   private javax.swing.JTextField maxLinField;
   private javax.swing.JTextField maxLogRangeField;
   private javax.swing.JTextField minLinField;
   private javax.swing.JTextField minLogRangeField;
   private pl.edu.icm.visnow.gui.widgets.EnhancedIntSlider nValsSlider;
   private javax.swing.JButton setLinRangeButton;
   private javax.swing.JButton setLogRangeButton;
   private javax.swing.JRadioButton setThrButton;
   private javax.swing.JPanel singleValPanel;
   private pl.edu.icm.visnow.gui.widgets.FloatSlider thrSlider;
   private javax.swing.JList thresholdList;
   // End of variables declaration//GEN-END:variables
}
