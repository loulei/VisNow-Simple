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

package pl.edu.icm.visnow.lib.basic.mappers.Graph;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.AbstractCellEditor;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.system.main.VisNow;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class GUI extends javax.swing.JPanel
{

   class ComponentTableModel extends DefaultTableModel
   {

      protected int columns = 6;
      protected int selectedRow = -1;
      protected int selectedColumn = -1;

      public ComponentTableModel()
      {
         super(new String[]
                 {
                    "component", "show", " ", "min", "avg", "max", "<html>\u03c3</html>"
                 }, 0);
      }

      @Override
      public Class getColumnClass(int c)
      {
         switch (c)
         {
         case 0:
            return String.class;
         case 1:
            return Boolean.class;
         case 2:
            return Color.class;
         default:
            return String.class;
         }
      }

      @Override
      public boolean isCellEditable(int row, int col)
      {
         return col == 2 || col == 1;
      }
   }

   class ColorIcon implements Icon
   {

      int w;
      int h;
      Color color;

      public ColorIcon(int w, int h, Color color)
      {
         this.w = w;
         this.h = h;
         this.color = color;
      }

      public void paintIcon(Component c, Graphics g, int x, int y)
      {
         g.setColor(color);
         g.fillRect(x, y, w, h);
      }

      public int getIconWidth()
      {
         return w;
      }

      public int getIconHeight()
      {
         return h;
      }
   }

   class ColorRenderer extends DefaultTableCellRenderer
   {

      @Override
      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
      {
         super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
         setHorizontalAlignment(CENTER);
         setVerticalAlignment(CENTER);
         setText(null);
         Color col = (Color) value;
         setIcon(new ColorIcon(table.getColumnModel().getColumn(column).getWidth() - 6, table.getRowHeight(row) - 6, col));
         setToolTipText("RGB value: " + col.getRed() + ", " + col.getGreen() + ", " + col.getBlue());
         return this;
      }
   }

   class ComponentRenderer extends DefaultTableCellRenderer
   {

      @Override
      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
      {
         super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
         setHorizontalAlignment(LEFT);
         setVerticalAlignment(CENTER);
         setForeground((Color) (table.getValueAt(row, column + 2)));
         setBackground(Color.DARK_GRAY);
         setText(value.toString());
         return this;
      }
   }

   class ColorEditor
           extends AbstractCellEditor
           implements TableCellEditor,
           ActionListener
   {

      private int r = -1;
      Color currentColor;
      JButton button;
      JColorChooser colorChooser;
      JDialog dialog;
      protected static final String EDIT = "edit";

      public ColorEditor()
      {
         //Set up the editor (from the table's point of view),
         //which is a button.
         //This button brings up the color chooser dialog,
         //which is the editor from the user's point of view.
         button = new JButton();
         button.setActionCommand(EDIT);
         button.addActionListener(this);
         button.setBorderPainted(false);

         //Set up the dialog that the button brings up.
         colorChooser = new JColorChooser(); 
         dialog = JColorChooser.createDialog(button, "Pick a Color", true,
                 colorChooser, this, null);
         dialog.getOwner().setIconImage(new ImageIcon(getClass().getResource( VisNow.getIconPath() )).getImage());
      }

      /**
       * Handles events from the editor button and from
       * the dialog's OK button.
       */
      public void actionPerformed(ActionEvent e)
      {
         if (EDIT.equals(e.getActionCommand()))
         {
            //The user has clicked the cell, so
            //bring up the dialog.
            button.setBackground(currentColor);
            colorChooser.setColor(currentColor);
            dialog.setVisible(true);
            fireEditingStopped();
         } else //User pressed dialog's "OK" button.
         {
            currentColor = colorChooser.getColor();
         }
      }

      //Implement the one CellEditor method that AbstractCellEditor doesn't.
      public Object getCellEditorValue()
      {
         return currentColor;
      }

      //Implement the one method defined by TableCellEditor.
      public Component getTableCellEditorComponent(JTable table,
              Object value,
              boolean isSelected,
              int row,
              int column)
      {
         r = row;
         currentColor = (Color) value;
         return button;
      }
   }
   
   protected ComponentTableModel componentTableModel = new ComponentTableModel();
   protected RegularField inField = null;
   protected Params params = null;
   protected int[] indices = null;
   protected Color[] defaultColors = new Color[]{
      new Color(1.f, 0.f, 0.f), new Color(0.f, 1.f, 0.f), new Color(0.f, 0.f, 1.f), 
      new Color(1.f, .5f, .5f), new Color(.5f, 1.f, .5f), new Color(.5f, .5f, 1.f),
      new Color(.5f, 0.f, 0.f), new Color(.5f, 1.f, 0.f), new Color(.5f, 0.f, 1.f), 
   };
   /** Creates new form GUI */
   public GUI()
   {
      initComponents();
      componentTable.setFont(new java.awt.Font("Dialog", 0, 10)); 
      componentTable.setAutoscrolls(false);
      componentTable.setGridColor(new java.awt.Color(200, 200, 200));
      TableColumnModel colModel = componentTable.getColumnModel();
      colModel.getColumn(0).setCellRenderer(new ComponentRenderer());
      colModel.getColumn(0).setPreferredWidth(100);
      colModel.getColumn(1).setPreferredWidth(10);
      colModel.getColumn(2).setPreferredWidth(10);
      colModel.getColumn(2).setCellRenderer(new ColorRenderer());
      colModel.getColumn(2).setCellEditor(new ColorEditor());
      colModel.getColumn(2).setPreferredWidth(10);
      colModel.getColumn(3).setPreferredWidth(35);
      colModel.getColumn(4).setPreferredWidth(35);
      colModel.getColumn(5).setPreferredWidth(35);
      colModel.getColumn(6).setPreferredWidth(35);
   }

   public void setInField(RegularField inField)
   {
      params.setActive(false);
      this.inField = inField;
      Vector<DataArray> inData = inField.getData();
      indices = new int[inData.size()];
      componentTableModel = new ComponentTableModel();
      int n = 0;
      for (int i = 0; i < inData.size(); i++)
      {
         DataArray dta = inData.get(i);
         if (!dta.isSimpleNumeric() || dta.getVeclen() != 1)
            continue;
         float avg = 0, s = 0;
         for (int j = 0; j < dta.getNData(); j++)
         {
            float x = dta.getData(j);
            avg += x;
            s += x * x;
         }
         avg /= dta.getNData();
         s = (float) (Math.sqrt(s / dta.getNData() - avg * avg));
         indices[n] = i;
         if (n == 0)
            componentTableModel.addRow(new Object[]
                    {dta.getName(), true, defaultColors[0], 
                     String.format("%5.1f", dta.getMinv()),
                     String.format("%5.1f", avg), 
                     String.format("%5.1f", dta.getMaxv()), 
                     String.format("%5.1f", s)});
         else
            componentTableModel.addRow(new Object[]
                    {dta.getName(), false, defaultColors[n%9], 
                     String.format("%5.1f", dta.getMinv()),
                     String.format("%5.1f", avg), 
                     String.format("%5.1f", dta.getMaxv()), 
                     String.format("%5.1f", s)});
         n += 1;
      }
      DisplayedData[] data = new DisplayedData[n];
      for (int i = 0; i < n; i++)
         data[i] = new DisplayedData(indices[i], i == 0, (Color)(componentTableModel.getValueAt(i, 2)));
      params.setDisplayedData(data);
      componentTableModel.addTableModelListener(new TableModelListener() 
      {
         public void tableChanged(TableModelEvent e)
         {
            fire();
         }
      });
      componentTable.setModel(componentTableModel);
      componentTable.setValueAt(true, 0, 1);
      TableColumnModel colModel = componentTable.getColumnModel();
      colModel.getColumn(0).setCellRenderer(new ComponentRenderer());
      colModel.getColumn(0).setPreferredWidth(100);
      colModel.getColumn(1).setPreferredWidth(10);
      colModel.getColumn(2).setPreferredWidth(10);
      colModel.getColumn(2).setCellRenderer(new ColorRenderer());
      colModel.getColumn(2).setCellEditor(new ColorEditor());
      colModel.getColumn(2).setPreferredWidth(10);
      colModel.getColumn(3).setPreferredWidth(35);
      colModel.getColumn(4).setPreferredWidth(35);
      colModel.getColumn(5).setPreferredWidth(35);
      colModel.getColumn(6).setPreferredWidth(35);   
      params.setActive(true);
   }


   /**
    * Set the value of params
    *
    * @param params new value of params
    */
   public void setParams(Params params)
   {
      this.params = params;
   }
   
   private void fire()
   {
      if (params == null)
         return;
      DisplayedData[] data = params.getDisplayedData();
      for (int i = 0; i < componentTable.getRowCount(); i++)
      {
         data[i].setColor((Color)componentTable.getValueAt(i, 2));
         data[i].setDisplayed((Boolean)componentTable.getValueAt(i, 1));
      }
      params.updateTable();
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

      jScrollPane1 = new javax.swing.JScrollPane();
      componentTable = new javax.swing.JTable();
      jPanel1 = new javax.swing.JPanel();
      xRangeSlider = new pl.edu.icm.visnow.gui.widgets.SubRangeSlider.SubRangeSlider();
      yRangeSlider = new pl.edu.icm.visnow.gui.widgets.SubRangeSlider.SubRangeSlider();
      jPanel2 = new javax.swing.JPanel();
      jPanel3 = new javax.swing.JPanel();
      colorEditor = new pl.edu.icm.visnow.gui.widgets.ColorEditor();
      fontSizeSlider = new javax.swing.JSlider();
      jLabel1 = new javax.swing.JLabel();
      xLabelField = new javax.swing.JTextField();
      jLabel2 = new javax.swing.JLabel();
      yLabelField = new javax.swing.JTextField();
      lineWidthSlider = new pl.edu.icm.visnow.gui.widgets.FloatSlider();
      titleField = new javax.swing.JTextField();
      titleBox = new javax.swing.JCheckBox();
      refreshButton = new javax.swing.JButton();
      legendBox = new javax.swing.JCheckBox();

      setMinimumSize(new java.awt.Dimension(180, 500));
      setPreferredSize(new java.awt.Dimension(230, 600));
      setLayout(new java.awt.GridBagLayout());

      jScrollPane1.setMinimumSize(new java.awt.Dimension(180, 100));
      jScrollPane1.setName("jScrollPane1"); // NOI18N
      jScrollPane1.setPreferredSize(new java.awt.Dimension(220, 150));

      componentTable.setModel(componentTableModel);
      componentTable.setName("componentTable"); // NOI18N
      jScrollPane1.setViewportView(componentTable);

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.weightx = 1.0;
      add(jScrollPane1, gridBagConstraints);

      jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
      jPanel1.setName("jPanel1"); // NOI18N
      jPanel1.setLayout(new java.awt.GridBagLayout());

      xRangeSlider.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "horizontal position and extent", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
      xRangeSlider.setBottomValue(30);
      xRangeSlider.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
      xRangeSlider.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
      xRangeSlider.setMinimumSize(new java.awt.Dimension(60, 55));
      xRangeSlider.setName("xRangeSlider"); // NOI18N
      xRangeSlider.setPaintLabels(true);
      xRangeSlider.setPaintTicks(true);
      xRangeSlider.setPreferredSize(new java.awt.Dimension(200, 60));
      xRangeSlider.setTopValue(90);
      xRangeSlider.addChangeListener(new javax.swing.event.ChangeListener() {
         public void stateChanged(javax.swing.event.ChangeEvent evt) {
            xRangeSliderStateChanged(evt);
         }
      });

      javax.swing.GroupLayout xRangeSliderLayout = new javax.swing.GroupLayout(xRangeSlider);
      xRangeSlider.setLayout(xRangeSliderLayout);
      xRangeSliderLayout.setHorizontalGroup(
         xRangeSliderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGap(0, 212, Short.MAX_VALUE)
      );
      xRangeSliderLayout.setVerticalGroup(
         xRangeSliderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGap(0, 49, Short.MAX_VALUE)
      );

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.weightx = 1.0;
      jPanel1.add(xRangeSlider, gridBagConstraints);

      yRangeSlider.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "vertical position and extent", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
      yRangeSlider.setBottomValue(75);
      yRangeSlider.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
      yRangeSlider.setMinimumSize(new java.awt.Dimension(60, 55));
      yRangeSlider.setName("yRangeSlider"); // NOI18N
      yRangeSlider.setPaintLabels(true);
      yRangeSlider.setPaintTicks(true);
      yRangeSlider.setPreferredSize(new java.awt.Dimension(200, 60));
      yRangeSlider.setTopValue(97);
      yRangeSlider.addChangeListener(new javax.swing.event.ChangeListener() {
         public void stateChanged(javax.swing.event.ChangeEvent evt) {
            yRangeSliderStateChanged(evt);
         }
      });

      javax.swing.GroupLayout yRangeSliderLayout = new javax.swing.GroupLayout(yRangeSlider);
      yRangeSlider.setLayout(yRangeSliderLayout);
      yRangeSliderLayout.setHorizontalGroup(
         yRangeSliderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGap(0, 212, Short.MAX_VALUE)
      );
      yRangeSliderLayout.setVerticalGroup(
         yRangeSliderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGap(0, 49, Short.MAX_VALUE)
      );

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      jPanel1.add(yRangeSlider, gridBagConstraints);

      jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
      jPanel2.setName("jPanel2"); // NOI18N

      javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
      jPanel2.setLayout(jPanel2Layout);
      jPanel2Layout.setHorizontalGroup(
         jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGap(0, 216, Short.MAX_VALUE)
      );
      jPanel2Layout.setVerticalGroup(
         jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGap(0, 116, Short.MAX_VALUE)
      );

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 10;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.weighty = 1.0;
      jPanel1.add(jPanel2, gridBagConstraints);

      jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "axes, ticks and legend color", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
      jPanel3.setMinimumSize(new java.awt.Dimension(100, 40));
      jPanel3.setName("jPanel3"); // NOI18N
      jPanel3.setLayout(new java.awt.BorderLayout());

      colorEditor.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
      colorEditor.setName("colorEditor"); // NOI18N
      colorEditor.addChangeListener(new javax.swing.event.ChangeListener() {
         public void stateChanged(javax.swing.event.ChangeEvent evt) {
            colorEditorStateChanged(evt);
         }
      });

      javax.swing.GroupLayout colorEditorLayout = new javax.swing.GroupLayout(colorEditor);
      colorEditor.setLayout(colorEditorLayout);
      colorEditorLayout.setHorizontalGroup(
         colorEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGap(0, 206, Short.MAX_VALUE)
      );
      colorEditorLayout.setVerticalGroup(
         colorEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGap(0, 15, Short.MAX_VALUE)
      );

      jPanel3.add(colorEditor, java.awt.BorderLayout.CENTER);

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      jPanel1.add(jPanel3, gridBagConstraints);

      fontSizeSlider.setFont(new java.awt.Font("Dialog", 0, 10));
      fontSizeSlider.setMajorTickSpacing(5);
      fontSizeSlider.setMaximum(40);
      fontSizeSlider.setMinimum(10);
      fontSizeSlider.setMinorTickSpacing(1);
      fontSizeSlider.setPaintLabels(true);
      fontSizeSlider.setPaintTicks(true);
      fontSizeSlider.setValue(15);
      fontSizeSlider.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "font size", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
      fontSizeSlider.setName("fontSizeSlider"); // NOI18N
      fontSizeSlider.addChangeListener(new javax.swing.event.ChangeListener() {
         public void stateChanged(javax.swing.event.ChangeEvent evt) {
            fontSizeSliderStateChanged(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 3;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      jPanel1.add(fontSizeSlider, gridBagConstraints);

      jLabel1.setFont(new java.awt.Font("Dialog", 0, 12));
      jLabel1.setText("x axis label");
      jLabel1.setName("jLabel1"); // NOI18N
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 6;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
      gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 6);
      jPanel1.add(jLabel1, gridBagConstraints);

      xLabelField.setText("x");
      xLabelField.setName("xLabelField"); // NOI18N
      xLabelField.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            xLabelFieldActionPerformed(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 6;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      jPanel1.add(xLabelField, gridBagConstraints);

      jLabel2.setFont(new java.awt.Font("Dialog", 0, 12));
      jLabel2.setText("y axis label");
      jLabel2.setName("jLabel2"); // NOI18N
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 7;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
      gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 6);
      jPanel1.add(jLabel2, gridBagConstraints);

      yLabelField.setText("y");
      yLabelField.setName("yLabelField"); // NOI18N
      yLabelField.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            yLabelFieldActionPerformed(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 7;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      jPanel1.add(yLabelField, gridBagConstraints);

      lineWidthSlider.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "line width", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
      lineWidthSlider.setMax(6.0F);
      lineWidthSlider.setMinimumSize(new java.awt.Dimension(90, 58));
      lineWidthSlider.setName("lineWidthSlider"); // NOI18N
      lineWidthSlider.setPreferredSize(new java.awt.Dimension(200, 61));
      lineWidthSlider.setShowingFields(false);
      lineWidthSlider.addChangeListener(new javax.swing.event.ChangeListener() {
         public void stateChanged(javax.swing.event.ChangeEvent evt) {
            lineWidthSliderStateChanged(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 4;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      jPanel1.add(lineWidthSlider, gridBagConstraints);

      titleField.setText("title");
      titleField.setName("titleField"); // NOI18N
      titleField.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            titleFieldActionPerformed(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 5;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      jPanel1.add(titleField, gridBagConstraints);

      titleBox.setFont(new java.awt.Font("Dialog", 0, 12));
      titleBox.setText("show title");
      titleBox.setName("titleBox"); // NOI18N
      titleBox.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            titleBoxActionPerformed(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 5;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
      jPanel1.add(titleBox, gridBagConstraints);

      refreshButton.setText("refresh");
      refreshButton.setName("refreshButton"); // NOI18N
      refreshButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            refreshButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 9;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      jPanel1.add(refreshButton, gridBagConstraints);

      legendBox.setFont(new java.awt.Font("Dialog", 0, 12));
      legendBox.setText("show color legend");
      legendBox.setName("legendBox"); // NOI18N
      legendBox.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            legendBoxActionPerformed(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 8;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
      jPanel1.add(legendBox, gridBagConstraints);

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.weighty = 1.0;
      add(jPanel1, gridBagConstraints);
   }// </editor-fold>//GEN-END:initComponents

   private void colorEditorStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_colorEditorStateChanged
   {//GEN-HEADEREND:event_colorEditorStateChanged
      if (params != null)
         params.setColor(colorEditor.getColor());
   }//GEN-LAST:event_colorEditorStateChanged

   private void xRangeSliderStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_xRangeSliderStateChanged
   {//GEN-HEADEREND:event_xRangeSliderStateChanged
      if (params != null && !xRangeSlider.isAdjusting())
         params.setHorizontalExtents(xRangeSlider.getBottomValue(), xRangeSlider.getTopValue());
   }//GEN-LAST:event_xRangeSliderStateChanged

   private void yRangeSliderStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_yRangeSliderStateChanged
   {//GEN-HEADEREND:event_yRangeSliderStateChanged
      if (params != null && !yRangeSlider.isAdjusting())
         params.setVerticalExtents(yRangeSlider.getBottomValue(), yRangeSlider.getTopValue());
   }//GEN-LAST:event_yRangeSliderStateChanged

   private void xLabelFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_xLabelFieldActionPerformed
   {//GEN-HEADEREND:event_xLabelFieldActionPerformed
      if (params == null) return;
      params.getAxesLabels()[0] = xLabelField.getText();
      params.fireStateChanged();
   }//GEN-LAST:event_xLabelFieldActionPerformed

   private void yLabelFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_yLabelFieldActionPerformed
   {//GEN-HEADEREND:event_yLabelFieldActionPerformed
       if (params == null) return;
       params.getAxesLabels()[1] = yLabelField.getText();
       params.fireStateChanged();
   }//GEN-LAST:event_yLabelFieldActionPerformed

   private void fontSizeSliderStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_fontSizeSliderStateChanged
   {//GEN-HEADEREND:event_fontSizeSliderStateChanged
       if (params != null && !fontSizeSlider.getValueIsAdjusting())
          params.setFontSize(fontSizeSlider.getValue());
   }//GEN-LAST:event_fontSizeSliderStateChanged

   private void lineWidthSliderStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_lineWidthSliderStateChanged
   {//GEN-HEADEREND:event_lineWidthSliderStateChanged
      if (params != null && !lineWidthSlider.isAdjusting())
          params.setLineWidth(lineWidthSlider.getVal());
   }//GEN-LAST:event_lineWidthSliderStateChanged

   private void titleFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_titleFieldActionPerformed
   {//GEN-HEADEREND:event_titleFieldActionPerformed
      if (params == null)
         return;
      if (titleBox.isSelected())
         params.setTitle(titleField.getText());
      else
         params.setTitle(null);
   }//GEN-LAST:event_titleFieldActionPerformed

   private void titleBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_titleBoxActionPerformed
   {//GEN-HEADEREND:event_titleBoxActionPerformed
      if (params == null)
         return;
      if (titleBox.isSelected())
         params.setTitle(titleField.getText());
      else
         params.setTitle(null);// TODO add your handling code here:
   }//GEN-LAST:event_titleBoxActionPerformed

   private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_refreshButtonActionPerformed
   {//GEN-HEADEREND:event_refreshButtonActionPerformed
      if (params != null) params.setRefresh(true);
   }//GEN-LAST:event_refreshButtonActionPerformed

   private void legendBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_legendBoxActionPerformed
   {//GEN-HEADEREND:event_legendBoxActionPerformed
      if (params != null) params.setColorLegend(legendBox.isSelected());
   }//GEN-LAST:event_legendBoxActionPerformed

   // Variables declaration - do not modify//GEN-BEGIN:variables
   private pl.edu.icm.visnow.gui.widgets.ColorEditor colorEditor;
   private javax.swing.JTable componentTable;
   private javax.swing.JSlider fontSizeSlider;
   private javax.swing.JLabel jLabel1;
   private javax.swing.JLabel jLabel2;
   private javax.swing.JPanel jPanel1;
   private javax.swing.JPanel jPanel2;
   private javax.swing.JPanel jPanel3;
   private javax.swing.JScrollPane jScrollPane1;
   private javax.swing.JCheckBox legendBox;
   private pl.edu.icm.visnow.gui.widgets.FloatSlider lineWidthSlider;
   private javax.swing.JButton refreshButton;
   private javax.swing.JCheckBox titleBox;
   private javax.swing.JTextField titleField;
   private javax.swing.JTextField xLabelField;
   private pl.edu.icm.visnow.gui.widgets.SubRangeSlider.SubRangeSlider xRangeSlider;
   private javax.swing.JTextField yLabelField;
   private pl.edu.icm.visnow.gui.widgets.SubRangeSlider.SubRangeSlider yRangeSlider;
   // End of variables declaration//GEN-END:variables
}
