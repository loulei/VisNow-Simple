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
exception statement from your version.
*/
//</editor-fold>


package pl.edu.icm.visnow.lib.basic.filters.ComponentOperations;

import java.util.ArrayList;
import java.util.Vector;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.datasets.IrregularField;
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
   private IrregularField inIrregularField = null;
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
      maskComponentSelector.setStartNull(true);
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
        recomputeMinMaxBox = new javax.swing.JCheckBox();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        coordsPanel = new javax.swing.JPanel();
        useCoordsCB = new javax.swing.JCheckBox();
        jScrollPane6 = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        indexComponentCB = new javax.swing.JCheckBox();
        xPanel = new javax.swing.JPanel();
        xComponentSelector = new pl.edu.icm.visnow.lib.gui.DataComponentSelector();
        yPanel = new javax.swing.JPanel();
        yComponentSelector = new pl.edu.icm.visnow.lib.gui.DataComponentSelector();
        zPanel = new javax.swing.JPanel();
        zComponentSelector = new pl.edu.icm.visnow.lib.gui.DataComponentSelector();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        xScaleField = new pl.edu.icm.visnow.gui.components.FloatFormattedTextField();
        xShiftField = new pl.edu.icm.visnow.gui.components.FloatFormattedTextField();
        yScaleField = new pl.edu.icm.visnow.gui.components.FloatFormattedTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        yShiftField = new pl.edu.icm.visnow.gui.components.FloatFormattedTextField();
        zScaleField = new pl.edu.icm.visnow.gui.components.FloatFormattedTextField();
        zShiftField = new pl.edu.icm.visnow.gui.components.FloatFormattedTextField();
        jLabel9 = new javax.swing.JLabel();
        xVarShiftField = new pl.edu.icm.visnow.gui.components.FloatFormattedTextField();
        jLabel10 = new javax.swing.JLabel();
        yVarShiftField = new pl.edu.icm.visnow.gui.components.FloatFormattedTextField();
        jLabel11 = new javax.swing.JLabel();
        zVarShiftField = new pl.edu.icm.visnow.gui.components.FloatFormattedTextField();
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
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));

        setLayout(new java.awt.BorderLayout());

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
        autoCheckBox.setToolTipText("<html>auto run if new field appears<br/>\n (works only when new field is compatible with previous one)</html>");
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

        componentselectorPanel.setToolTipText("<html>change component type, <p>drop or apply algebraic function</html> ");
        componentselectorPanel.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setPreferredSize(new java.awt.Dimension(100, 200));

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
        jScrollPane1.setViewportView(actionTable);
        if (actionTable.getColumnModel().getColumnCount() > 0) {
            actionTable.getColumnModel().getColumn(0).setPreferredWidth(130);
            actionTable.getColumnModel().getColumn(1).setResizable(false);
            actionTable.getColumnModel().getColumn(1).setPreferredWidth(50);
            actionTable.getColumnModel().getColumn(2).setResizable(false);
            actionTable.getColumnModel().getColumn(2).setPreferredWidth(50);
            actionTable.getColumnModel().getColumn(3).setResizable(false);
            actionTable.getColumnModel().getColumn(3).setPreferredWidth(50);
            actionTable.getColumnModel().getColumn(4).setResizable(false);
            actionTable.getColumnModel().getColumn(4).setPreferredWidth(50);
        }

        componentselectorPanel.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        functionPane.addTab("basic", componentselectorPanel);

        maskPanel.setToolTipText("mask as invalid values outside specified range");
        maskPanel.setLayout(new java.awt.GridBagLayout());

        maskComponentSelector.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                maskComponentSelectorStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        maskPanel.add(maskComponentSelector, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        maskPanel.add(maskRangeSlider, gridBagConstraints);

        recomputeMinMaxBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        recomputeMinMaxBox.setText("recompute min/max for data arrays");
        recomputeMinMaxBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                recomputeMinMaxBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        maskPanel.add(recomputeMinMaxBox, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.weighty = 1.0;
        maskPanel.add(filler1, gridBagConstraints);

        functionPane.addTab("mask", maskPanel);

        coordsPanel.setLayout(new java.awt.BorderLayout());

        useCoordsCB.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        useCoordsCB.setText("set coordinates from data");
        useCoordsCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useCoordsCBActionPerformed(evt);
            }
        });
        coordsPanel.add(useCoordsCB, java.awt.BorderLayout.NORTH);

        jPanel3.setLayout(new java.awt.GridBagLayout());

        indexComponentCB.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        indexComponentCB.setText("add index component");
        indexComponentCB.setToolTipText("adds integer component (node index)");
        indexComponentCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                indexComponentCBActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 2;
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

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
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

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
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

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPanel3.add(zPanel, gridBagConstraints);

        jLabel3.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jLabel3.setText("y variable shift");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel3.add(jLabel3, gridBagConstraints);

        jLabel4.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jLabel4.setText("x scale");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel3.add(jLabel4, gridBagConstraints);

        jLabel5.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jLabel5.setText("x coord shift");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel3.add(jLabel5, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel3.add(xScaleField, gridBagConstraints);

        xShiftField.setText("0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel3.add(xShiftField, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel3.add(yScaleField, gridBagConstraints);

        jLabel6.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jLabel6.setText("y coord shift");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel3.add(jLabel6, gridBagConstraints);

        jLabel7.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jLabel7.setText("z variable shift");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel3.add(jLabel7, gridBagConstraints);

        jLabel8.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jLabel8.setText("z coord shift");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel3.add(jLabel8, gridBagConstraints);

        yShiftField.setText("0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel3.add(yShiftField, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel3.add(zScaleField, gridBagConstraints);

        zShiftField.setText("0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel3.add(zShiftField, gridBagConstraints);

        jLabel9.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jLabel9.setText("x variable shift");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel3.add(jLabel9, gridBagConstraints);

        xVarShiftField.setText("0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel3.add(xVarShiftField, gridBagConstraints);

        jLabel10.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jLabel10.setText("y scale");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel3.add(jLabel10, gridBagConstraints);

        yVarShiftField.setText("0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel3.add(yVarShiftField, gridBagConstraints);

        jLabel11.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jLabel11.setText("z scale");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel3.add(jLabel11, gridBagConstraints);

        zVarShiftField.setText("0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel3.add(zVarShiftField, gridBagConstraints);

        jScrollPane6.setViewportView(jPanel3);

        coordsPanel.add(jScrollPane6, java.awt.BorderLayout.CENTER);

        functionPane.addTab("coords", coordsPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        mainPanel.add(functionPane, gridBagConstraints);

        functionPane1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N

        vectorPanel.setToolTipText("<html>combine scalars into vector<p>compute norm or split vector in scalars</html>");
        vectorPanel.setLayout(new java.awt.GridBagLayout());

        jScrollPane2.setPreferredSize(new java.awt.Dimension(100, 100));

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
        createVectorsTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(createVectorsTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        vectorPanel.add(jScrollPane2, gridBagConstraints);

        force3DBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        force3DBox.setText("force 3D vectors");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        vectorPanel.add(force3DBox, gridBagConstraints);

        jScrollPane3.setPreferredSize(new java.awt.Dimension(100, 100));

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
        jScrollPane3.setViewportView(vectorOperationsTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        vectorPanel.add(jScrollPane3, gridBagConstraints);

        functionPane1.addTab("vectors", vectorPanel);

        complexPanel.setLayout(new java.awt.GridBagLayout());

        complexCombinePanel.setLayout(new java.awt.BorderLayout());

        jLabel1.setText("Combine components to complex");
        complexCombinePanel.add(jLabel1, java.awt.BorderLayout.NORTH);

        jScrollPane4.setPreferredSize(new java.awt.Dimension(100, 100));

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
        complexCombineTable.setRowSelectionAllowed(false);
        jScrollPane4.setViewportView(complexCombineTable);

        complexCombinePanel.add(jScrollPane4, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        complexPanel.add(complexCombinePanel, gridBagConstraints);

        complexSplitPanel.setLayout(new java.awt.BorderLayout());

        jLabel2.setText("Split complex components");
        complexSplitPanel.add(jLabel2, java.awt.BorderLayout.NORTH);

        jScrollPane5.setPreferredSize(new java.awt.Dimension(100, 100));

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
        complexSplitTable.setRowSelectionAllowed(false);
        jScrollPane5.setViewportView(complexSplitTable);

        complexSplitPanel.add(jScrollPane5, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        complexPanel.add(complexSplitPanel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.weighty = 1.0;
        complexPanel.add(filler2, gridBagConstraints);

        functionPane1.addTab("complex", complexPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        mainPanel.add(functionPane1, gridBagConstraints);

        add(mainPanel, java.awt.BorderLayout.NORTH);
    }// </editor-fold>//GEN-END:initComponents

   private void indexComponentCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_indexComponentCBActionPerformed
      params.setAddIndexComponent(indexComponentCB.isSelected());
}//GEN-LAST:event_indexComponentCBActionPerformed

   private void xComponentSelectorStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_xComponentSelectorStateChanged
      params.setXCoordComponent(xComponentSelector.getComponent());
}//GEN-LAST:event_xComponentSelectorStateChanged

   private void yComponentSelectorStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_yComponentSelectorStateChanged
      params.setYCoordComponent(yComponentSelector.getComponent());
}//GEN-LAST:event_yComponentSelectorStateChanged

   private void zComponentSelectorStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_zComponentSelectorStateChanged
      params.setZCoordComponent(zComponentSelector.getComponent());
}//GEN-LAST:event_zComponentSelectorStateChanged

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
      int i = maskComponentSelector.getComponent();
      if(i < inField.getNData()) {
          DataArray da = inField.getData(i);
          maskRangeSlider.setMinMax(da.getMinv(), da.getMaxv());
      } else if(i == inField.getNData()) { //x coordinate
          float[][] ext = inField.getExtents();
          maskRangeSlider.setMinMax(ext[0][0], ext[1][0]);          
      } else if(i == inField.getNData()+1) { //y coordinate
          float[][] ext = inField.getExtents();
          maskRangeSlider.setMinMax(ext[0][1], ext[1][1]);          
      } else if(i == inField.getNData()+2) { //z coordinate
          float[][] ext = inField.getExtents();
          maskRangeSlider.setMinMax(ext[0][2], ext[1][2]);          
      }
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
      else
         inIrregularField = (IrregularField)inField;
      maskComponentSelector.setDataSchema(inField.getSchema());
      maskComponentSelector.setNull();
      
      String[] coordItems = null;
      int[] coordIndices = null;
      switch(inField.getNSpace()) {
          case 3:
              coordItems = new String[]{"x coord","y coord","z coord"};
              coordIndices = new int[]{inField.getNData(),inField.getNData()+1,inField.getNData()+2};
              break;
          case 2:
              coordItems = new String[]{"x coord","y coord"};
              coordIndices = new int[]{inField.getNData(),inField.getNData()+1};
              break;
          case 1:
              coordItems = new String[]{"x coord"};
              coordIndices = new int[]{inField.getNData()};
              break;
      }      
      maskComponentSelector.addExtraItems(coordItems, coordIndices);
      
      updateGUI();
      ArrayList<DataArraySchema> components = inField.getSchema().getComponentSchemas();
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
      
      //TODO: create column name enum
      //remove non-convertible components from editing
      final boolean[] convertible = new boolean[nComps];      
      for (int i = 0; i< nComps; i++)
          convertible[i] = inField.getData(i).isNumericConvertible();          

      DefaultTableModel tm = new javax.swing.table.DefaultTableModel(actionTableContent, actionTableHeader)
      {
         @Override
         public Class getColumnClass(int columnIndex)
         {
            return actionTableTypes[columnIndex];
         }

          @Override
          public boolean isCellEditable(int row, int column) {
              if (column == 1) return convertible[row];
              else return super.isCellEditable(row, column);
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

      if (params != null)
         params.setMaskComponent(-1);
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
      
      coordsPanel.setVisible(true);
      useCoordsCB.setEnabled(true);
      useCoordsCB.setSelected(params.isUseCoords());

      if (params.isUseCoords())
      {
         String[] extraNames = null;
         int[] extraIndices = null;
         if (inIrregularField != null)
         {
            complexCombinePanel.setVisible(false);
            complexSplitPanel.setVisible(false); 
            extraNames = new String[] {"x", "y", "z", "0"};
            extraIndices = new int[] {-10, -11, -12, -100};
         }
         else if (inRegularField != null)
         {
            int nDims = inRegularField.getDims().length;
            switch (nDims)
            {
               case 1:
                  extraNames = new String[]{"x", "y", "z", "i", "0"};
                  extraIndices = new int[] {-10, -11, -12, -1, -100};
                  break;
               case 2:
                  extraNames = new String[]{"x", "y", "z", "i", "j", "0"};
                  extraIndices = new int[] {-10, -11, -12, -1, -2, -100};
                  break;
               default:
                  extraNames = new String[]{"x", "y", "z", "i", "j", "k", "0"};
                  extraIndices = new int[] {-10, -11, -12, -1, -2, -3, -100};
                  break;
            }

         }

         xComponentSelector.addExtraItems(extraNames, extraIndices);
         xComponentSelector.setDataSchema(inField.getSchema());
         xComponentSelector.setComponent(params.getXCoordComponent());

         yComponentSelector.addExtraItems(extraNames, extraIndices);
         yComponentSelector.setDataSchema(inField.getSchema());
         yComponentSelector.setComponent(params.getYCoordComponent());

         zComponentSelector.addExtraItems(extraNames, extraIndices);
         zComponentSelector.setDataSchema(inField.getSchema());
         zComponentSelector.setComponent(params.getZCoordComponent());

         indexComponentCB.setEnabled(true);
         indexComponentCB.setSelected(params.isAddIndexComponent());

      } else
      {
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
      
      params.setXCoordComponent(xComponentSelector.getComponent());
      params.setYCoordComponent(yComponentSelector.getComponent());
      params.setZCoordComponent(zComponentSelector.getComponent());
      
      params.setXVarShift(xVarShiftField.getValue());
      params.setYVarShift(yVarShiftField.getValue());
      params.setZVarShift(zVarShiftField.getValue());
      
      params.setXCoordScale(xScaleField.getValue());
      params.setYCoordScale(yScaleField.getValue());
      params.setZCoordScale(zScaleField.getValue());
      
      params.setXCoordShift(xShiftField.getValue());
      params.setYCoordShift(yShiftField.getValue());
      params.setZCoordShift(zShiftField.getValue());
      
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
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JCheckBox force3DBox;
    private javax.swing.JTabbedPane functionPane;
    private javax.swing.JTabbedPane functionPane1;
    private javax.swing.JCheckBox indexComponentCB;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel3;
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
    private javax.swing.JCheckBox useCoordsCB;
    private javax.swing.JTable vectorOperationsTable;
    private javax.swing.JPanel vectorPanel;
    private pl.edu.icm.visnow.lib.gui.DataComponentSelector xComponentSelector;
    private javax.swing.JPanel xPanel;
    private pl.edu.icm.visnow.gui.components.FloatFormattedTextField xScaleField;
    private pl.edu.icm.visnow.gui.components.FloatFormattedTextField xShiftField;
    private pl.edu.icm.visnow.gui.components.FloatFormattedTextField xVarShiftField;
    private pl.edu.icm.visnow.lib.gui.DataComponentSelector yComponentSelector;
    private javax.swing.JPanel yPanel;
    private pl.edu.icm.visnow.gui.components.FloatFormattedTextField yScaleField;
    private pl.edu.icm.visnow.gui.components.FloatFormattedTextField yShiftField;
    private pl.edu.icm.visnow.gui.components.FloatFormattedTextField yVarShiftField;
    private pl.edu.icm.visnow.lib.gui.DataComponentSelector zComponentSelector;
    private javax.swing.JPanel zPanel;
    private pl.edu.icm.visnow.gui.components.FloatFormattedTextField zScaleField;
    private pl.edu.icm.visnow.gui.components.FloatFormattedTextField zShiftField;
    private pl.edu.icm.visnow.gui.components.FloatFormattedTextField zVarShiftField;
    // End of variables declaration//GEN-END:variables
}
