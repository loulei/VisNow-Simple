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

package pl.edu.icm.visnow.lib.basic.filters.ComponentOperations;

import java.awt.Color;
import java.util.Vector;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.datasets.dataarrays.DataArraySchema;
import pl.edu.icm.visnow.gui.widgets.SteppedComboBox;

/**
 *
 * @author  Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class GUI extends javax.swing.JPanel
{

   private Params params = new Params();
   private Field inField = null;
   private RegularField inRegularField = null;
   private int nComps = 0;
   private static final String[] actionTableHeader = new String[]
   {
      "component", "action", "retain", "min", "max"
   };
   private static final Class[] actionTableTypes = new Class[]
   {
      String.class, String.class, Boolean.class, Float.class, Float.class
   };
   private static final int[] actionColumnWidth = new int[]{130,50,50,50,50};
   
   
   private static final String[] complexSplitTableHeader = new String[]
   {
      "component", "Re", "Im", "Abs", "Arg"
   };
   private static final Class[] complexSplitTableTypes = new Class[]
   {
      String.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class
   };
   
   private static final String[] complexCombineTableHeader = new String[]
   {
      "name", "Re", "Im"
   };
   private int nScalarComps = 0;
   private int nVectorComps = 0;
   private int nComplexComps = 0;
   private int nNonComplexComps = 0;
   private Vector<String> compNames = new Vector<String>();
   private int[] compIndices;
   private Vector<String> vCompNames = new Vector<String>();
   private int[] vCompIndices;
   private static final String[] vCompTableHeader = new String[]
   {
      "vector component", "||", "/||", "split"
   };
   private static final Class[] vCompTableTypes = new Class[]
   {
      String.class, Boolean.class, Boolean.class, Boolean.class
   };
   private Vector<String> complexCompNames = new Vector<String>();
   private Vector<String> nonComplexCompNames = new Vector<String>();
   private int[] complexCompIndices;
   private int[] nonComplexCompIndices;

   private static final int[] createVectorsColumnWidth = new int[]{140,45,45,45,25};
   private static final int[] vectorsCompColumnWidth = new int[]{160,25,25,25};
   private static final int[] complexSplitPreferredColumnWidth = new int[]{150,40,40,40,40};
   
   /** Creates new form GUI */
   public GUI()
   {
      initComponents();
      actionTable.setRowHeight(22);
      for (int i = 1; i < actionColumnWidth.length; i++)
         actionTable.getColumnModel().getColumn(i).setPreferredWidth(actionColumnWidth[i]);
      actionTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
      actionTable.getTableHeader().setReorderingAllowed(false);
      for (int i = 1; i < createVectorsColumnWidth.length; i++)
         createVectorsTable.getColumnModel().getColumn(i).setPreferredWidth(actionColumnWidth[i]);
      createVectorsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
      for (int i = 1; i < vectorsCompColumnWidth.length; i++)
         vectorOperationsTable.getColumnModel().getColumn(i).setPreferredWidth(actionColumnWidth[i]);
      vectorOperationsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

      complexSplitTable.setRowHeight(22);
      complexSplitTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
      
      updateGUI();
      maskComponentSelector.setAddNullComponent(true);
      maskComponentSelector.setScalarComponentsOnly(true);
   }

   /** This method is called from within the constructor to
    * initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is
    * always regenerated by the Form Editor.
    */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel2 = new javax.swing.JPanel();
        mainPanel = new javax.swing.JPanel();
        runButton = new javax.swing.JButton();
        autoCheckBox = new javax.swing.JCheckBox();
        functionPane = new javax.swing.JTabbedPane();
        componentselectorPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        actionTable = new javax.swing.JTable();
        maskPanel = new javax.swing.JPanel();
        maskComponentSelector = new pl.edu.icm.visnow.lib.gui.DataComponentSelector();
        maskRangeSlider = new pl.edu.icm.visnow.gui.widgets.FloatSubRangeSlider.ExtendedFloatSubRangeSlider();
        jPanel1 = new javax.swing.JPanel();
        recomputeMinMaxBox = new javax.swing.JCheckBox();
        coordsPanel = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        indexComponentCB = new javax.swing.JCheckBox();
        xPanel = new javax.swing.JPanel();
        xComponentSelector = new pl.edu.icm.visnow.lib.gui.DataComponentSelector();
        xScaleSlider = new pl.edu.icm.visnow.gui.widgets.FloatSlider();
        yPanel = new javax.swing.JPanel();
        yComponentSelector = new pl.edu.icm.visnow.lib.gui.DataComponentSelector();
        yScaleSlider = new pl.edu.icm.visnow.gui.widgets.FloatSlider();
        zPanel = new javax.swing.JPanel();
        zComponentSelector = new pl.edu.icm.visnow.lib.gui.DataComponentSelector();
        zScaleSlider = new pl.edu.icm.visnow.gui.widgets.FloatSlider();
        topPanel = new javax.swing.JPanel();
        useCoordsCB = new javax.swing.JCheckBox();
        dim1RB = new javax.swing.JRadioButton();
        dim2RB = new javax.swing.JRadioButton();
        dim3RB = new javax.swing.JRadioButton();
        functionPane1 = new javax.swing.JTabbedPane();
        vectorPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        createVectorsTable = new javax.swing.JTable();
        force3DBox = new javax.swing.JCheckBox();
        jScrollPane3 = new javax.swing.JScrollPane();
        vectorOperationsTable = new javax.swing.JTable();
        complexPanel = new javax.swing.JPanel();
        complexCombinePanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        complexCombineTable = new javax.swing.JTable();
        complexSplitPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        complexSplitTable = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();

        setMaximumSize(new java.awt.Dimension(230, 800));
        setMinimumSize(new java.awt.Dimension(190, 450));
        setPreferredSize(new java.awt.Dimension(230, 600));
        setLayout(new java.awt.GridBagLayout());

        jPanel2.setMaximumSize(new java.awt.Dimension(32767, 600));
        jPanel2.setPreferredSize(new java.awt.Dimension(100, 500));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        add(jPanel2, gridBagConstraints);

        mainPanel.setLayout(new java.awt.GridBagLayout());

        runButton.setText("run");
        runButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        mainPanel.add(runButton, gridBagConstraints);

        autoCheckBox.setText("auto");
        autoCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        mainPanel.add(autoCheckBox, gridBagConstraints);

        functionPane.setToolTipText("<html>combine scalars as Re/Im part<p>split complex values into Re/Im<p>compute module and argument</html>");
        functionPane.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        functionPane.setMaximumSize(new java.awt.Dimension(32767, 400));
        functionPane.setMinimumSize(new java.awt.Dimension(170, 200));
        functionPane.setOpaque(true);
        functionPane.setPreferredSize(new java.awt.Dimension(220, 300));
        functionPane.setRequestFocusEnabled(false);

        componentselectorPanel.setToolTipText("<html>change component type, <p>drop or apply algebraic function</html> ");
        componentselectorPanel.setMaximumSize(new java.awt.Dimension(2147483647, 400));
        componentselectorPanel.setPreferredSize(new java.awt.Dimension(220, 350));
        componentselectorPanel.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setMaximumSize(new java.awt.Dimension(32767, 290));
        jScrollPane1.setMinimumSize(new java.awt.Dimension(180, 200));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(220, 250));

        actionTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null}
            },
            new String [] {
                "component", "action", "retain", "min", "max"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.Boolean.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        actionTable.setMaximumSize(new java.awt.Dimension(400, 1000));
        actionTable.setMinimumSize(new java.awt.Dimension(280, 240));
        actionTable.setPreferredSize(new java.awt.Dimension(300, 300));
        jScrollPane1.setViewportView(actionTable);
        actionTable.getColumnModel().getColumn(0).setPreferredWidth(130);
        actionTable.getColumnModel().getColumn(1).setResizable(false);
        actionTable.getColumnModel().getColumn(1).setPreferredWidth(50);
        actionTable.getColumnModel().getColumn(2).setResizable(false);
        actionTable.getColumnModel().getColumn(2).setPreferredWidth(50);
        actionTable.getColumnModel().getColumn(3).setResizable(false);
        actionTable.getColumnModel().getColumn(3).setPreferredWidth(50);
        actionTable.getColumnModel().getColumn(4).setResizable(false);
        actionTable.getColumnModel().getColumn(4).setPreferredWidth(50);

        componentselectorPanel.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        functionPane.addTab("basic", componentselectorPanel);

        maskPanel.setToolTipText("mask as invalid values outside specified range");
        maskPanel.setMaximumSize(new java.awt.Dimension(2147483647, 400));
        maskPanel.setLayout(new java.awt.GridBagLayout());

        maskComponentSelector.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                maskComponentSelectorStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        maskPanel.add(maskComponentSelector, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        maskPanel.add(maskRangeSlider, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        maskPanel.add(jPanel1, gridBagConstraints);

        recomputeMinMaxBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        recomputeMinMaxBox.setText("recompute min/max for data arrays");
        recomputeMinMaxBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                recomputeMinMaxBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        maskPanel.add(recomputeMinMaxBox, gridBagConstraints);

        functionPane.addTab("mask", maskPanel);

        coordsPanel.setMaximumSize(new java.awt.Dimension(2147483647, 400));
        coordsPanel.setMinimumSize(new java.awt.Dimension(200, 400));
        coordsPanel.setPreferredSize(new java.awt.Dimension(200, 400));
        coordsPanel.setLayout(new java.awt.BorderLayout());

        jScrollPane6.setMinimumSize(new java.awt.Dimension(175, 195));
        jScrollPane6.setPreferredSize(new java.awt.Dimension(215, 295));

        jPanel3.setMinimumSize(new java.awt.Dimension(170, 300));
        jPanel3.setPreferredSize(new java.awt.Dimension(210, 450));
        jPanel3.setLayout(new java.awt.GridBagLayout());

        indexComponentCB.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        indexComponentCB.setText("add index component");
        indexComponentCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                indexComponentCBActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel3.add(indexComponentCB, gridBagConstraints);

        xPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "x coordinate component", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        xPanel.setLayout(new java.awt.GridBagLayout());

        xComponentSelector.setAddNullComponent(false);
        xComponentSelector.setScalarComponentsOnly(true);
        xComponentSelector.setTitle("");
        xComponentSelector.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                xComponentSelectorStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        xPanel.add(xComponentSelector, gridBagConstraints);

        xScaleSlider.setMax(10.0F);
        xScaleSlider.setMinimumSize(new java.awt.Dimension(90, 40));
        xScaleSlider.setPreferredSize(new java.awt.Dimension(200, 40));
        xScaleSlider.setShowingFields(false);
        xScaleSlider.setToolTipText("x coordinate scale");
        xScaleSlider.setVal(1.0F);
        xScaleSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                xScaleSliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        xPanel.add(xScaleSlider, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPanel3.add(xPanel, gridBagConstraints);

        yPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "y coordinate component", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        yPanel.setLayout(new java.awt.GridBagLayout());

        yComponentSelector.setAddNullComponent(false);
        yComponentSelector.setScalarComponentsOnly(true);
        yComponentSelector.setTitle("");
        yComponentSelector.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                yComponentSelectorStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        yPanel.add(yComponentSelector, gridBagConstraints);

        yScaleSlider.setMax(10.0F);
        yScaleSlider.setMinimumSize(new java.awt.Dimension(90, 40));
        yScaleSlider.setPreferredSize(new java.awt.Dimension(200, 40));
        yScaleSlider.setShowingFields(false);
        yScaleSlider.setToolTipText("y coordinate scale");
        yScaleSlider.setVal(1.0F);
        yScaleSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                yScaleSliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        yPanel.add(yScaleSlider, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPanel3.add(yPanel, gridBagConstraints);

        zPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "z coordinate component", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        zPanel.setLayout(new java.awt.GridBagLayout());

        zComponentSelector.setAddNullComponent(false);
        zComponentSelector.setScalarComponentsOnly(true);
        zComponentSelector.setTitle("");
        zComponentSelector.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                zComponentSelectorStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        zPanel.add(zComponentSelector, gridBagConstraints);

        zScaleSlider.setMax(10.0F);
        zScaleSlider.setMinimumSize(new java.awt.Dimension(90, 40));
        zScaleSlider.setPreferredSize(new java.awt.Dimension(200, 40));
        zScaleSlider.setShowingFields(false);
        zScaleSlider.setToolTipText("z coordinate scale");
        zScaleSlider.setVal(1.0F);
        zScaleSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                zScaleSliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        zPanel.add(zScaleSlider, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPanel3.add(zPanel, gridBagConstraints);

        jScrollPane6.setViewportView(jPanel3);

        coordsPanel.add(jScrollPane6, java.awt.BorderLayout.CENTER);

        topPanel.setLayout(new java.awt.GridBagLayout());

        useCoordsCB.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        useCoordsCB.setText("set coordinates from data");
        useCoordsCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useCoordsCBActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 3, 2, 5);
        topPanel.add(useCoordsCB, gridBagConstraints);

        buttonGroup1.add(dim1RB);
        dim1RB.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        dim1RB.setText("1D");
        dim1RB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dim1RBActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        topPanel.add(dim1RB, gridBagConstraints);

        buttonGroup1.add(dim2RB);
        dim2RB.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        dim2RB.setText("2D");
        dim2RB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dim2RBActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        topPanel.add(dim2RB, gridBagConstraints);

        buttonGroup1.add(dim3RB);
        dim3RB.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        dim3RB.setText("3D");
        dim3RB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dim3RBActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        topPanel.add(dim3RB, gridBagConstraints);

        coordsPanel.add(topPanel, java.awt.BorderLayout.PAGE_START);

        functionPane.addTab("coords", coordsPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        mainPanel.add(functionPane, gridBagConstraints);

        functionPane1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        functionPane1.setMaximumSize(new java.awt.Dimension(32767, 400));
        functionPane1.setMinimumSize(new java.awt.Dimension(170, 250));
        functionPane1.setPreferredSize(new java.awt.Dimension(220, 300));

        vectorPanel.setToolTipText("<html>combine scalars into vector<p>compute norm or split vector in scalars</html>");
        vectorPanel.setMaximumSize(new java.awt.Dimension(2147483647, 300));
        vectorPanel.setMinimumSize(new java.awt.Dimension(180, 190));
        vectorPanel.setOpaque(false);
        vectorPanel.setPreferredSize(new java.awt.Dimension(223, 290));
        vectorPanel.setRequestFocusEnabled(false);
        vectorPanel.setLayout(new java.awt.GridBagLayout());

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane2.setMinimumSize(new java.awt.Dimension(180, 90));
        jScrollPane2.setOpaque(false);
        jScrollPane2.setPreferredSize(new java.awt.Dimension(220, 140));

        createVectorsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Name", "x", "y", "z", "||"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        createVectorsTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        createVectorsTable.setFocusable(false);
        createVectorsTable.setGridColor(new java.awt.Color(200, 200, 200));
        createVectorsTable.setInheritsPopupMenu(true);
        createVectorsTable.setMaximumSize(new java.awt.Dimension(240, 400));
        createVectorsTable.setMinimumSize(new java.awt.Dimension(180, 300));
        createVectorsTable.setPreferredSize(new java.awt.Dimension(220, 300));
        createVectorsTable.setRequestFocusEnabled(false);
        createVectorsTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(createVectorsTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        vectorPanel.add(jScrollPane2, gridBagConstraints);

        force3DBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        force3DBox.setText("force 3D vectors");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
        vectorPanel.add(force3DBox, gridBagConstraints);

        jScrollPane3.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane3.setMinimumSize(new java.awt.Dimension(180, 90));
        jScrollPane3.setPreferredSize(new java.awt.Dimension(453, 140));

        vectorOperationsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "component", "||", "/||", "split"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        vectorOperationsTable.setMaximumSize(new java.awt.Dimension(2147483647, 300));
        vectorOperationsTable.setMinimumSize(new java.awt.Dimension(180, 200));
        vectorOperationsTable.setPreferredSize(new java.awt.Dimension(225, 200));
        jScrollPane3.setViewportView(vectorOperationsTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.weighty = 1.0;
        vectorPanel.add(jScrollPane3, gridBagConstraints);

        functionPane1.addTab("vectors", vectorPanel);

        complexPanel.setLayout(new java.awt.GridBagLayout());

        complexCombinePanel.setMinimumSize(new java.awt.Dimension(200, 200));
        complexCombinePanel.setPreferredSize(new java.awt.Dimension(200, 200));
        complexCombinePanel.setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jLabel1.setText("Combine components to complex");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        complexCombinePanel.add(jLabel1, gridBagConstraints);

        complexCombineTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "component", "Re", "Im"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        complexCombineTable.setFocusable(false);
        complexCombineTable.setRequestFocusEnabled(false);
        complexCombineTable.setRowSelectionAllowed(false);
        jScrollPane4.setViewportView(complexCombineTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        complexCombinePanel.add(jScrollPane4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        complexPanel.add(complexCombinePanel, gridBagConstraints);

        complexSplitPanel.setMinimumSize(new java.awt.Dimension(200, 200));
        complexSplitPanel.setPreferredSize(new java.awt.Dimension(200, 200));
        complexSplitPanel.setLayout(new java.awt.GridBagLayout());

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jLabel2.setText("Split complex components");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        complexSplitPanel.add(jLabel2, gridBagConstraints);

        complexSplitTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null}
            },
            new String [] {
                "component", "Re", "Im", "Abs", "Arg"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        complexSplitTable.setFocusable(false);
        complexSplitTable.setRequestFocusEnabled(false);
        complexSplitTable.setRowSelectionAllowed(false);
        jScrollPane5.setViewportView(complexSplitTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        complexSplitPanel.add(jScrollPane5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        complexPanel.add(complexSplitPanel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        complexPanel.add(jPanel5, gridBagConstraints);

        functionPane1.addTab("complex", complexPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 0.1;
        mainPanel.add(functionPane1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(mainPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

   private void dim1RBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dim1RBActionPerformed
      if (dim1RB.isSelected())
      {
         params.setNDims(1);
         updateGUI();
      }
}//GEN-LAST:event_dim1RBActionPerformed

   private void dim2RBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dim2RBActionPerformed
      if (dim2RB.isSelected())
      {
         params.setNDims(2);
         updateGUI();
      }
   }//GEN-LAST:event_dim2RBActionPerformed

   private void dim3RBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dim3RBActionPerformed
      if (dim3RB.isSelected())
      {
         params.setNDims(3);
         updateGUI();
      }
   }//GEN-LAST:event_dim3RBActionPerformed

   private void indexComponentCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_indexComponentCBActionPerformed
      params.setAddIndexComponent(indexComponentCB.isSelected());
}//GEN-LAST:event_indexComponentCBActionPerformed

   private void xComponentSelectorStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_xComponentSelectorStateChanged
      params.setXCoordComponent(xComponentSelector.getComponent());
}//GEN-LAST:event_xComponentSelectorStateChanged

   private void xScaleSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_xScaleSliderStateChanged
      {
         float min = xScaleSlider.getMin();
         float max = xScaleSlider.getMax();
         float val = xScaleSlider.getVal();
         if (min != params.getXCoordScaleMin())
            params.setXCoordScaleMin(min);
         if (max != params.getXCoordScaleMax())
            params.setXCoordScaleMax(max);
         if (val != params.getXCoordScaleVal())
            params.setXCoordScaleVal(val);
      }
}//GEN-LAST:event_xScaleSliderStateChanged

   private void yComponentSelectorStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_yComponentSelectorStateChanged
      params.setYCoordComponent(yComponentSelector.getComponent());
}//GEN-LAST:event_yComponentSelectorStateChanged

   private void yScaleSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_yScaleSliderStateChanged
      {
         float min = yScaleSlider.getMin();
         float max = yScaleSlider.getMax();
         float val = yScaleSlider.getVal();
         if (min != params.getYCoordScaleMin())
            params.setYCoordScaleMin(min);
         if (max != params.getYCoordScaleMax())
            params.setYCoordScaleMax(max);
         if (val != params.getYCoordScaleVal())
            params.setYCoordScaleVal(val);
      }
}//GEN-LAST:event_yScaleSliderStateChanged

   private void zComponentSelectorStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_zComponentSelectorStateChanged
      params.setZCoordComponent(zComponentSelector.getComponent());
}//GEN-LAST:event_zComponentSelectorStateChanged

   private void zScaleSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_zScaleSliderStateChanged
      {
         float min = zScaleSlider.getMin();
         float max = zScaleSlider.getMax();
         float val = zScaleSlider.getVal();
         if (min != params.getZCoordScaleMin())
            params.setZCoordScaleMin(min);
         if (max != params.getZCoordScaleMax())
            params.setZCoordScaleMax(max);
         if (val != params.getZCoordScaleVal())
            params.setZCoordScaleVal(val);
      }
}//GEN-LAST:event_zScaleSliderStateChanged

   private void useCoordsCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useCoordsCBActionPerformed
      params.setUseCoords(useCoordsCB.isSelected());
      updateGUI();
   }//GEN-LAST:event_useCoordsCBActionPerformed

private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runButtonActionPerformed
    fire();
}//GEN-LAST:event_runButtonActionPerformed

   private void maskComponentSelectorStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_maskComponentSelectorStateChanged
   {//GEN-HEADEREND:event_maskComponentSelectorStateChanged
      params.setMaskComponent(maskComponentSelector.getComponent());
      if (maskComponentSelector.getComponent() < 0)
         return;
      DataArray da = inField.getData(maskComponentSelector.getComponent());
      maskRangeSlider.setMinMax(da.getMinv(), da.getMaxv());
   }//GEN-LAST:event_maskComponentSelectorStateChanged

   private void recomputeMinMaxBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_recomputeMinMaxBoxActionPerformed
   {//GEN-HEADEREND:event_recomputeMinMaxBoxActionPerformed
      params.setRecomputeMinMax(recomputeMinMaxBox.isSelected());
   }//GEN-LAST:event_recomputeMinMaxBoxActionPerformed

   private void autoCheckBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_autoCheckBoxActionPerformed
   {//GEN-HEADEREND:event_autoCheckBoxActionPerformed
      params.setAuto(autoCheckBox.isSelected());
   }//GEN-LAST:event_autoCheckBoxActionPerformed

   public void setUpComponentColumn(SteppedComboBox comboBox,
           TableColumn componentColumn)
   {
      componentColumn.setCellEditor(new DefaultCellEditor(comboBox));
      DefaultTableCellRenderer renderer =
              new DefaultTableCellRenderer();
      componentColumn.setCellRenderer(renderer);
   }

   public void setInField(Field inField)
   {
      this.inField = inField;
      if (inField instanceof RegularField)
         inRegularField = (RegularField) inField;
      maskComponentSelector.setDataSchema(inField.getSchema());
      maskComponentSelector.setSelectedIndex(maskComponentSelector.getnItems());
      updateGUI();
      Vector<DataArraySchema> components = inField.getSchema().getComponentSchemas();
      nComps = components.size();
      Object[][] actionTableContent = new Object[nComps][5];
      for (int i = 0; i < nComps; i++)
      {
         actionTableContent[i][0] = inField.getData(i).getName();
         actionTableContent[i][1] = "";
         actionTableContent[i][2] = true;
         actionTableContent[i][3] = inField.getData(i).getMinv() - .1f;
         actionTableContent[i][4] = inField.getData(i).getMaxv() + .1f;
      }
      DefaultTableModel tm = new javax.swing.table.DefaultTableModel(actionTableContent, actionTableHeader)
      {
         @Override
         public Class getColumnClass(int columnIndex)
         {
            return actionTableTypes[columnIndex];
         }
      };
      actionTable.setModel(tm);
      for (int i = 0; i < 5; i++)
         actionTable.getColumnModel().getColumn(i).setPreferredWidth(actionColumnWidth[i]);
      SteppedComboBox comboBox = new SteppedComboBox(Params.actionNames);
      comboBox.setFont(new java.awt.Font("Dialog", 0, 12));
      setUpComponentColumn(comboBox, actionTable.getColumnModel().getColumn(1));
      nComps = components.size();
      compNames.clear();
      vCompNames.clear();
      complexCompNames.clear();
      nonComplexCompNames.clear();
      compIndices = new int[nComps + 1];
      vCompIndices = new int[nComps + 1];
      complexCompIndices = new int[nComps + 1];
      nonComplexCompIndices = new int[nComps + 1];
      nScalarComps = nVectorComps = nComplexComps = nNonComplexComps = 0;
      for (int i = 0; i < nComps; i++) {
         if (components.get(i).getVeclen() == 1)
         {
            compNames.add(components.get(i).getName());
            compIndices[nScalarComps] = i;
            nScalarComps++;
         } else
         {
            vCompNames.add(components.get(i).getName());
            vCompIndices[nVectorComps] = i;
            nVectorComps++;
         }
         
         if(components.get(i).getType() == DataArray.FIELD_DATA_COMPLEX) {
            complexCompNames.add(components.get(i).getName());
            complexCompIndices[nComplexComps] = i;
            nComplexComps++;
         } else if(components.get(i).getVeclen() == 1) {
            nonComplexCompNames.add(components.get(i).getName());
            nonComplexCompIndices[nNonComplexComps] = i;
            nNonComplexComps++;
         }         
      }
      compIndices[nScalarComps] = -100;
      compNames.add("0");
      SteppedComboBox compComboBox = new SteppedComboBox(compNames);
      comboBox.addItem("Null");
      for (int i = 1; i < 4; i++)
         setUpComponentColumn(compComboBox, createVectorsTable.getColumnModel().getColumn(i));
      
      Object[][] vectorCompTableContent = new Object[nVectorComps][4];
      for (int i = 0; i < nVectorComps; i++)
      {
         vectorCompTableContent[i][0] = vCompNames.get(i);
         vectorCompTableContent[i][1] = false;
         vectorCompTableContent[i][2] = false;
         vectorCompTableContent[i][3] = false;
      }
      DefaultTableModel vtm = new javax.swing.table.DefaultTableModel(vectorCompTableContent, vCompTableHeader)
      {
         @Override
         public Class getColumnClass(int columnIndex)
         {
            return vCompTableTypes[columnIndex];
         }
      };
      vectorOperationsTable.setModel(vtm);

      
//      Object[][] complexSpltTableContent = new Object[nComplexComps][5];
//      for (int i = 0; i < nComplexComps; i++)
//      {
//         complexSpltTableContent[i][0] = inField.getData(complexCompIndices[i]).getName();
//         complexSpltTableContent[i][1] = false;
//         complexSpltTableContent[i][2] = false;
//         complexSpltTableContent[i][3] = false;
//         complexSpltTableContent[i][4] = false;
//      }
//      DefaultTableModel ctm = new javax.swing.table.DefaultTableModel(complexSpltTableContent, complexSplitTableHeader)
//      {
//         @Override
//         public Class getColumnClass(int columnIndex)
//         {
//            return complexSplitTableTypes[columnIndex];
//         }
//      };
//      complexSplitTable.setModel(ctm);
//      for (int i = 1; i < 5; i++)
//         complexSplitTable.getColumnModel().getColumn(i).setPreferredWidth(complexSplitPreferredColumnWidth[i]);
//      complexSplitPanel.setVisible(nComplexComps > 0);
      
//      SteppedComboBox nonComplexCompComboBox = new SteppedComboBox(nonComplexCompNames);
//      setUpComponentColumn(nonComplexCompComboBox, complexCombineTable.getColumnModel().getColumn(1));
//      setUpComponentColumn(nonComplexCompComboBox, complexCombineTable.getColumnModel().getColumn(2));
//      complexCombinePanel.setVisible(nNonComplexComps > 0);
      
      updateGUI();
   }

   private void updateGUI()
   {
      if (inRegularField == null)
      {
         useCoordsCB.setEnabled(false);
         coordsPanel.setVisible(false);
         dim1RB.setEnabled(false);
         dim2RB.setEnabled(false);
         dim3RB.setEnabled(false);
         xComponentSelector.setEnabled(false);
         yComponentSelector.setEnabled(false);
         zComponentSelector.setEnabled(false);
         xScaleSlider.setEnabled(false);
         yScaleSlider.setEnabled(false);
         zScaleSlider.setEnabled(false);
         indexComponentCB.setEnabled(false);
         complexCombinePanel.setVisible(false);
         complexSplitPanel.setVisible(false);         
         return;
      }
      coordsPanel.setVisible(true);
      useCoordsCB.setEnabled(true);
      useCoordsCB.setSelected(params.isUseCoords());

      if (params.isUseCoords())
      {
         int nDims = inRegularField.getDims().length;
         dim1RB.setEnabled(nDims == 1);
         dim2RB.setEnabled(nDims <= 2);
         dim3RB.setEnabled(nDims <= 3);
         dim1RB.setVisible(nDims == 1);
         dim2RB.setVisible(nDims <= 2);
         dim3RB.setVisible(nDims <= 3);
         switch (params.getNDims())
         {
            case 1:
               dim1RB.setSelected(true);
               xComponentSelector.setEnabled(true);
               yComponentSelector.setEnabled(false);
               zComponentSelector.setEnabled(false);
               xScaleSlider.setEnabled(true);
               yScaleSlider.setEnabled(false);
               zScaleSlider.setEnabled(false);
               xPanel.setVisible(true);
               yPanel.setVisible(false);
               zPanel.setVisible(false);
               break;
            case 2:
               dim2RB.setSelected(true);
               xComponentSelector.setEnabled(true);
               yComponentSelector.setEnabled(true);
               zComponentSelector.setEnabled(false);
               xScaleSlider.setEnabled(true);
               yScaleSlider.setEnabled(true);
               zScaleSlider.setEnabled(false);
               xPanel.setVisible(true);
               yPanel.setVisible(true);
               zPanel.setVisible(false);
               break;
            case 3:
               dim3RB.setSelected(true);
               xComponentSelector.setEnabled(true);
               yComponentSelector.setEnabled(true);
               zComponentSelector.setEnabled(true);
               xScaleSlider.setEnabled(true);
               yScaleSlider.setEnabled(true);
               zScaleSlider.setEnabled(true);
               xPanel.setVisible(true);
               yPanel.setVisible(true);
               zPanel.setVisible(true);
               break;
         }

         String[] extraNames;
         int[] extraIndices;
         switch (nDims)
         {
            case 1:
               extraNames = new String[2];
               extraNames[0] = "i";
               extraNames[1] = "0";
               extraIndices = new int[2];
               extraIndices[0] = -1;
               extraIndices[1] = -100;
               break;
            case 2:
               extraNames = new String[3];
               extraNames[0] = "i";
               extraNames[1] = "j";
               extraNames[2] = "0";
               extraIndices = new int[3];
               extraIndices[0] = -1;
               extraIndices[1] = -2;
               extraIndices[2] = -100;
               break;
            default:
               extraNames = new String[4];
               extraNames[0] = "i";
               extraNames[1] = "j";
               extraNames[2] = "k";
               extraNames[3] = "0";
               extraIndices = new int[4];
               extraIndices[0] = -1;
               extraIndices[1] = -2;
               extraIndices[2] = -3;
               extraIndices[2] = -100;
               break;
         }

         xComponentSelector.addExtraItems(extraNames, extraIndices);
         xComponentSelector.setDataSchema(inRegularField.getSchema());
         xComponentSelector.setComponent(params.getXCoordComponent());
         xScaleSlider.setMinMax(params.getXCoordScaleMin(), params.getXCoordScaleMax());
         xScaleSlider.setValue(params.getXCoordScaleVal(), true);

         yComponentSelector.addExtraItems(extraNames, extraIndices);
         yComponentSelector.setDataSchema(inRegularField.getSchema());
         yComponentSelector.setComponent(params.getYCoordComponent());
         yScaleSlider.setMinMax(params.getYCoordScaleMin(), params.getYCoordScaleMax());
         yScaleSlider.setValue(params.getYCoordScaleVal(), true);

         zComponentSelector.addExtraItems(extraNames, extraIndices);
         zComponentSelector.setDataSchema(inRegularField.getSchema());
         zComponentSelector.setComponent(params.getZCoordComponent());
         zScaleSlider.setMinMax(params.getZCoordScaleMin(), params.getZCoordScaleMax());
         zScaleSlider.setValue(params.getZCoordScaleVal(), true);

         indexComponentCB.setEnabled(true);
         indexComponentCB.setSelected(params.isAddIndexComponent());

      } else
      {
         dim1RB.setEnabled(false);
         dim2RB.setEnabled(false);
         dim3RB.setEnabled(false);
         dim1RB.setVisible(false);
         dim2RB.setVisible(false);
         dim3RB.setVisible(false);
         xComponentSelector.setEnabled(false);
         yComponentSelector.setEnabled(false);
         zComponentSelector.setEnabled(false);
         xScaleSlider.setEnabled(false);
         yScaleSlider.setEnabled(false);
         zScaleSlider.setEnabled(false);
         xPanel.setVisible(false);
         yPanel.setVisible(false);
         zPanel.setVisible(false);
         indexComponentCB.setEnabled(false);
      }
      
      
      Object[][] complexSpltTableContent = new Object[nComplexComps][5];
      boolean[] splitRe = params.getComplexSplitRe();
      if(splitRe == null || splitRe.length != nComplexComps) {
          splitRe = new boolean[nComplexComps];
          for (int i = 0; i < splitRe.length; i++) {
              splitRe[i] = false;              
          }
      }
      boolean[] splitIm = params.getComplexSplitRe();
      if(splitIm == null || splitIm.length != nComplexComps) {
          splitIm = new boolean[nComplexComps];
          for (int i = 0; i < splitIm.length; i++) {
              splitIm[i] = false;              
          }
      }
      boolean[] splitAbs = params.getComplexSplitRe();
      if(splitAbs == null || splitAbs.length != nComplexComps) {
          splitAbs = new boolean[nComplexComps];
          for (int i = 0; i < splitAbs.length; i++) {
              splitAbs[i] = false;              
          }
      }
      boolean[] splitArg = params.getComplexSplitRe();
      if(splitArg == null || splitArg.length != nComplexComps) {
          splitArg = new boolean[nComplexComps];
          for (int i = 0; i < splitArg.length; i++) {
              splitArg[i] = false;              
          }
      }
            
      for (int i = 0; i < nComplexComps; i++)
      {
         complexSpltTableContent[i][0] = inField.getData(complexCompIndices[i]).getName();
         complexSpltTableContent[i][1] = splitRe[i];
         complexSpltTableContent[i][2] = splitIm[i];
         complexSpltTableContent[i][3] = splitAbs[i];
         complexSpltTableContent[i][4] = splitArg[i];
      }
      DefaultTableModel ctm = new javax.swing.table.DefaultTableModel(complexSpltTableContent, complexSplitTableHeader)
      {
         @Override
         public Class getColumnClass(int columnIndex)
         {
            return complexSplitTableTypes[columnIndex];
         }
      };
      complexSplitTable.setModel(ctm);
      for (int i = 1; i < 5; i++)
         complexSplitTable.getColumnModel().getColumn(i).setPreferredWidth(complexSplitPreferredColumnWidth[i]);
      complexSplitPanel.setVisible(nComplexComps > 0);

      
      Vector<ComplexComponent> cc = params.getComplexCombineComponents();
      String[][] cccTableContent = new String[10][3];
      for (int i = 0; i < 10; i++) {
           for (int j = 0; j < 3; j++) {
               cccTableContent[i][j] = null;               
           }           
      }
       for (int i = 0; i < cc.size(); i++) {
           cccTableContent[i][0] = cc.get(i).getName();
           
           int tmp = cc.get(i).getRealComponent();
           int k = -1;
           for (int j = 0; j < nNonComplexComps; j++) {
               if(nonComplexCompIndices[j] == tmp) {
                   k = j;
                   break;
               }
           }           
           cccTableContent[i][1] = nonComplexCompNames.get(k);
           
           tmp = cc.get(i).getImagComponent();
           k = -1;
           for (int j = 0; j < nNonComplexComps; j++) {
               if(nonComplexCompIndices[j] == tmp) {
                   k = j;
                   break;
               }
           }           
           cccTableContent[i][2] = nonComplexCompNames.get(k);
       }
       DefaultTableModel ccctm = new javax.swing.table.DefaultTableModel(cccTableContent, complexCombineTableHeader);
       complexCombineTable.setModel(ccctm);
       
       SteppedComboBox nonComplexCompComboBox = new SteppedComboBox(nonComplexCompNames);
       setUpComponentColumn(nonComplexCompComboBox, complexCombineTable.getColumnModel().getColumn(1));
       setUpComponentColumn(nonComplexCompComboBox, complexCombineTable.getColumnModel().getColumn(2));
       complexCombinePanel.setVisible(nNonComplexComps > 0);
      
   }

   public void setParams(Params params)
   {
      this.params = params;
   }

   private void fire()
   {
      int[] actions = new int[nComps];
      boolean[] retain = new boolean[nComps];
      float[] clampMin = new float[nComps];
      float[] clampMax = new float[nComps];
      for (int i = 0; i < nComps; i++)
      {
         String s = (String) (actionTable.getValueAt(i, 1));
         for (int j = 0; j < Params.actionNames.length; j++)
            if (s.equalsIgnoreCase(Params.actionNames[j]))
            {
               actions[i] = Params.actionCodes[j];
               break;
            }
         retain[i] = (Boolean) (actionTable.getValueAt(i, 2));
         clampMin[i] = (Float) (actionTable.getValueAt(i, 3));
         clampMax[i] = (Float) (actionTable.getValueAt(i, 4));
      }
      params.setActions(actions);
      params.setRetain(retain);
      params.setMin(clampMin);
      params.setMax(clampMax);
      params.setMaskMin(maskRangeSlider.getLow());
      params.setMaskMax(maskRangeSlider.getUp());
      params.setRecomputeMinMax(recomputeMinMaxBox.isSelected());

      params.clearVectorComponents();
      for (int i = 0; i < createVectorsTable.getModel().getRowCount(); i++)
      {
         String name = (String) createVectorsTable.getValueAt(i, 0);
         if (name == null || name.length() < 1)
            continue;
         int[] components = new int[3];
         for (int j = 0; j < 3; j++)
         {
            int k = compNames.indexOf((String) createVectorsTable.getValueAt(i, j + 1));
            if (k < 0)
               components[j] = -1;
            else
               components[j] = compIndices[k];
         }
         boolean norm = false;
         if (createVectorsTable.getValueAt(i, 4) != null)
            norm = (Boolean) createVectorsTable.getValueAt(i, 4);
         params.addVectorComponent(name, components, norm);
      }
      if (nVectorComps == 0)
         params.setVCNorms(null);
      else
      {
         boolean[] vCN = new boolean[nVectorComps];
         boolean[] vCNormalize = new boolean[nVectorComps];
         boolean[] vCS = new boolean[nVectorComps];
         for (int i = 0; i < vCN.length; i++)
         {
            vCN[i] = (Boolean) vectorOperationsTable.getValueAt(i, 1);
            vCNormalize[i] = (Boolean) vectorOperationsTable.getValueAt(i, 2);
            vCS[i] = (Boolean) vectorOperationsTable.getValueAt(i, 3);
         }
         params.setVCNorms(vCN);
         params.setVCNormalize(vCNormalize);
         params.setVCSplit(vCS);
      }
      params.setFix3D(force3DBox.isSelected());
      
      params.clearComplexCombineComponents();
      if(nNonComplexComps > 0) {        
          for (int i = 0; i < complexCombineTable.getModel().getRowCount(); i++)
          {
             String name = (String) complexCombineTable.getValueAt(i, 0);
             if (name == null || name.length() < 1)
                continue;
             int[] components = new int[2];
             for (int j = 0; j < 2; j++)
             {
                int k = nonComplexCompNames.indexOf((String) complexCombineTable.getValueAt(i, j + 1));
                if (k < 0)
                   components[j] = -1;
                else
                   components[j] = nonComplexCompIndices[k];
             }
             params.addComplexCombineComponent(name, components[0], components[1]);
          }
      }
      
      if(nComplexComps > 0) {
          boolean[] splitRe = new boolean[nComplexComps];
          boolean[] splitIm = new boolean[nComplexComps];
          boolean[] splitAbs = new boolean[nComplexComps];
          boolean[] splitArg = new boolean[nComplexComps];
          
          for (int i = 0; i < nComplexComps; i++) {
             splitRe[i] = (Boolean) (complexSplitTable.getValueAt(i, 1));
             splitIm[i] = (Boolean) (complexSplitTable.getValueAt(i, 2));
             splitAbs[i] = (Boolean) (complexSplitTable.getValueAt(i, 3));
             splitArg[i] = (Boolean) (complexSplitTable.getValueAt(i, 4));
          }
          params.setComplexSplitRe(splitRe);
          params.setComplexSplitIm(splitIm);
          params.setComplexSplitAbs(splitAbs);
          params.setComplexSplitArg(splitArg);
      } else {
          params.setComplexSplitRe(new boolean[]{});
          params.setComplexSplitIm(new boolean[]{});
          params.setComplexSplitAbs(new boolean[]{});
          params.setComplexSplitArg(new boolean[]{});
      }
      
      params.fireStateChanged();
   }
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable actionTable;
    private javax.swing.JCheckBox autoCheckBox;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JPanel complexCombinePanel;
    private javax.swing.JTable complexCombineTable;
    private javax.swing.JPanel complexPanel;
    private javax.swing.JPanel complexSplitPanel;
    private javax.swing.JTable complexSplitTable;
    private javax.swing.JPanel componentselectorPanel;
    private javax.swing.JPanel coordsPanel;
    private javax.swing.JTable createVectorsTable;
    private javax.swing.JRadioButton dim1RB;
    private javax.swing.JRadioButton dim2RB;
    private javax.swing.JRadioButton dim3RB;
    private javax.swing.JCheckBox force3DBox;
    private javax.swing.JTabbedPane functionPane;
    private javax.swing.JTabbedPane functionPane1;
    private javax.swing.JCheckBox indexComponentCB;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JPanel mainPanel;
    private pl.edu.icm.visnow.lib.gui.DataComponentSelector maskComponentSelector;
    private javax.swing.JPanel maskPanel;
    private pl.edu.icm.visnow.gui.widgets.FloatSubRangeSlider.ExtendedFloatSubRangeSlider maskRangeSlider;
    private javax.swing.JCheckBox recomputeMinMaxBox;
    private javax.swing.JButton runButton;
    private javax.swing.JPanel topPanel;
    private javax.swing.JCheckBox useCoordsCB;
    private javax.swing.JTable vectorOperationsTable;
    private javax.swing.JPanel vectorPanel;
    private pl.edu.icm.visnow.lib.gui.DataComponentSelector xComponentSelector;
    private javax.swing.JPanel xPanel;
    private pl.edu.icm.visnow.gui.widgets.FloatSlider xScaleSlider;
    private pl.edu.icm.visnow.lib.gui.DataComponentSelector yComponentSelector;
    private javax.swing.JPanel yPanel;
    private pl.edu.icm.visnow.gui.widgets.FloatSlider yScaleSlider;
    private pl.edu.icm.visnow.lib.gui.DataComponentSelector zComponentSelector;
    private javax.swing.JPanel zPanel;
    private pl.edu.icm.visnow.gui.widgets.FloatSlider zScaleSlider;
    // End of variables declaration//GEN-END:variables
}
