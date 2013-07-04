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

package pl.edu.icm.visnow.lib.basic.viewers.FieldViewer1D;

import java.awt.*;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.AbstractCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.system.main.VisNow;

/**
 *
 * @author  Krzysztof S. Nowinski (know@icm.edu.pl)
 * Warsaw University
 * Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class GraphArea extends javax.swing.JPanel
{
   private Viewer1DFrame parentFrame = null;

   class ComponentTableModel extends DefaultTableModel
   {
      protected int columns            = 6;
      protected int selectedRow        = -1;
      protected int selectedColumn     = -1;

      public ComponentTableModel()
      {
         super(new String[]    {"component", " ", "min", "avg", "max", "<html>\u03c3</html>"}, 0);
      }

      @Override
      public Class getColumnClass(int c)
      {
         switch(c)
         {
            case 0:
               return GraphData.class;
            case 1:
               return Color.class;
            default:
               return Float.class;
         }
      }
      @Override
      public boolean isCellEditable(int row, int col)
      {
         return col<3;
      }
   }

   protected int n0,  n1;
   protected int nMax = 0;
   protected int[] subRange = new int[2];
   protected Vector<int[]> selRanges = new Vector<int[]>();
   protected boolean useSelectedFrames = false;
   protected boolean[] selFrames = null;
   protected Vector<GraphData> data = new Vector<GraphData>();
   protected Vector<GraphData> graphedData = new Vector<GraphData>();
   protected float[] tdat = {1,2,3,4,5};
   protected float[] tdat1 = {1,2,3,4,5,6};
   DataArray testArray  = DataArray.create(tdat, 1, "test data 0");
   DataArray testArray1 = DataArray.create(tdat1, 1, "test data 1");
   ComponentTableModel componentTableModel = new ComponentTableModel();
   JTable componentTable = new JTable(componentTableModel);
   protected boolean ignoreChanges = false;
   /** Creates new form GraphArea */
   public GraphArea()
   {
      initComponents();
      componentTable.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
      componentTable.setAutoscrolls(false);
      componentTable.setGridColor(new java.awt.Color(200, 200, 200));
      TableColumnModel colModel = componentTable.getColumnModel();
      colModel.getColumn(0).setCellRenderer(new ComponentRenderer());
      colModel.getColumn(0).setPreferredWidth(150);
      colModel.getColumn(1).setCellRenderer(new ColorRenderer());
      colModel.getColumn(1).setCellEditor(new ColorEditor());
      colModel.getColumn(1).setPreferredWidth(10);
      colModel.getColumn(2).setPreferredWidth(35);
      colModel.getColumn(3).setPreferredWidth(35);
      colModel.getColumn(4).setPreferredWidth(35);
      colModel.getColumn(5).setPreferredWidth(35);
      setData(null, true);
      componentPane.getViewport().add(componentTable);
      componentTableModel.addTableModelListener(new TableModelListener() {
         public void tableChanged(TableModelEvent e)
         {
            if (ignoreChanges) return;
            ComponentTableModel tm = (ComponentTableModel)componentTable.getModel();
            int nRows = tm.getRowCount();
            int k = 0;
            for (int i = 0; i < nRows; i++)
               if (tm.getValueAt(i, 0) != null && tm.getValueAt(i, 0) instanceof GraphData)
                  k += 1;
            if (k == nRows)
            {
               ignoreChanges = true;
               tm.addRow(new Object[] {null,null,null,null,null,null});
               ignoreChanges = false;
            }
            recomputeData();
            graphWorld.repaint();
         }
      });
      recomputeData();
   }

   /** This method is called from within the constructor to
    * initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is
    * always regenerated by the Form Editor.
    */
   // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
   private void initComponents() {
      java.awt.GridBagConstraints gridBagConstraints;

      controlPanel = new javax.swing.JPanel();
      componentPane = new javax.swing.JScrollPane();
      jPanel2 = new javax.swing.JPanel();
      addButton = new javax.swing.JButton();
      graphWorld = new pl.edu.icm.visnow.lib.basic.viewers.FieldViewer1D.GraphWorld();

      setBorder(javax.swing.BorderFactory.createEtchedBorder());
      setToolTipText("<html>Click to select a frame for display<p>\nPress and drag to select frame range</html>");
      setMinimumSize(new java.awt.Dimension(400, 200));
      setPreferredSize(new java.awt.Dimension(500, 220));
      setLayout(new java.awt.GridBagLayout());

      controlPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
      controlPanel.setMinimumSize(new java.awt.Dimension(350, 220));
      controlPanel.setPreferredSize(new java.awt.Dimension(350, 220));
      controlPanel.setRequestFocusEnabled(false);
      controlPanel.setLayout(new java.awt.BorderLayout());
      controlPanel.add(componentPane, java.awt.BorderLayout.CENTER);

      jPanel2.setLayout(new java.awt.GridLayout(1, 0));

      addButton.setFont(new java.awt.Font("Dialog", 0, 12));
      addButton.setText("add");
      addButton.setMaximumSize(new java.awt.Dimension(120, 16));
      addButton.setMinimumSize(new java.awt.Dimension(70, 16));
      addButton.setPreferredSize(new java.awt.Dimension(70, 16));
      addButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            addButtonActionPerformed(evt);
         }
      });
      jPanel2.add(addButton);

      controlPanel.add(jPanel2, java.awt.BorderLayout.SOUTH);

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
      gridBagConstraints.weighty = 1.0;
      add(controlPanel, gridBagConstraints);

      org.jdesktop.layout.GroupLayout graphWorldLayout = new org.jdesktop.layout.GroupLayout(graphWorld);
      graphWorld.setLayout(graphWorldLayout);
      graphWorldLayout.setHorizontalGroup(
         graphWorldLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
         .add(0, 389, Short.MAX_VALUE)
      );
      graphWorldLayout.setVerticalGroup(
         graphWorldLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
         .add(0, 296, Short.MAX_VALUE)
      );

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.weightx = 1.0;
      add(graphWorld, gridBagConstraints);
   }// </editor-fold>//GEN-END:initComponents

   private void addButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_addButtonActionPerformed
   {//GEN-HEADEREND:event_addButtonActionPerformed
      ignoreChanges = true;
      componentTableModel.addRow(new Object[] {null, Color.DARK_GRAY, 0.f, 0.f,0.f,0.f});
      ignoreChanges = false;
}//GEN-LAST:event_addButtonActionPerformed

   class ColorIcon implements Icon {
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
         setIcon(new ColorIcon(table.getColumnModel().getColumn(column).getWidth()-6,table.getRowHeight(row)-6, col));
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
         setForeground((Color)(table.getValueAt(row, column+1)));
         if (value != null && value instanceof DataArray)
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
         dialog = JColorChooser.createDialog(button,"Pick a Color",true, 
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
            if (r >= 0 && r < componentTableModel.getRowCount())
              ((GraphData)componentTableModel.getValueAt(r, 0) ).setColor(currentColor);
            recomputeData();
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

   public void setData(Vector<DataArray> dataArrays, boolean newSchema)
   {
      if (newSchema)
      {
         data.clear();
         JComboBox componentComboBox = new JComboBox();
         if (dataArrays == null || dataArrays.isEmpty())
            return;
         for (int i = 0; i < dataArrays.size(); i++)
         {
            int k = (50+10*i)%255;
            GraphData d = new GraphData(i, dataArrays.get(i), new Color(k,k,k));
            data.add(d);
            componentComboBox.addItem(data.get(i));
         }
         componentComboBox.addItem("Null");
         componentTable.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(componentComboBox));
         GraphData d = data.get(0);
         componentTableModel.addRow(new Object[] {d, d.getColor(), 0,0,0,0});
         recomputeData();
      }
      else
      {
         for (int i = 0; i < data.size(); i++)
         {
            Color c = data.get(i).getColor();
            data.set(i, new GraphData(i, dataArrays.get(i), c));
         }
         for (int i = 0; i < componentTableModel.getRowCount(); i++)
         {
            GraphData gData = (GraphData)componentTableModel.getValueAt(i, 0);
            componentTableModel.setValueAt(data.get(gData.getItem()), i, 0);
            recomputeData();
         }
      }
   }

   public void updateData(Vector<DataArray> dataArrays)
   {
      recomputeData();
   }


   /**
    * Getter for n0 - lower limit of region of interest.
    * @return Value of n0.
    */
   public int getN0()
   {
      return n0;
   }

   /**
    * Setter for n0 - lower limit of region of interest.
    * @param n0 New value of n0.
    */
   public void setN0(int n0)
   {
      this.n0 = n0;
   }

   /**
    * Getter for n1 - upper limit of region of interest.
    * @return Value of property n1.
    */
   public int getN1()
   {
      return n1;
   }

   /**
    * Setter for n1 - upper limit of region of interest.
    * @param n1 New value of property n1.
    */
   public void setN1(int n1)
   {
      this.n1 = n1;
   }

   public int[] getSelRange()
   {
      return subRange;
   }

   public void setSelRanges(Vector<int[]> selRanges)
   {
      this.selRanges = selRanges;
      recomputeData();
   }

   public void setUseSelectedFrames(boolean useSelectedFrames)
   {
      this.useSelectedFrames = useSelectedFrames;
      recomputeData();
   }

   public void setSelFrames(boolean[] selFrames)
   {
      this.selFrames = selFrames;
   }

   public void setParentFrame(Viewer1DFrame parentFrame)
   {
      this.parentFrame = parentFrame;
      graphWorld.setParentFrame(parentFrame);
   }


   /**
    * @return the nMax
    */
   public int getNMax()
   {
      return nMax;
   }

   /**
    * @param nMax the nMax to set
    */
   public void setNMax(int nMax)
   {
      this.nMax = nMax;
   }

   public void recomputeData()
   {
      int length;
      graphedData.clear();
      ignoreChanges = true;
      for (int i = 0; i < componentTableModel.getRowCount(); i++)
         if (!(componentTableModel.getValueAt(i, 0) instanceof  GraphData))
            componentTableModel.removeRow(i);
      n1 = 0;
      for (int i = 0; i < componentTableModel.getRowCount(); i++)
      {
         GraphData gData = (GraphData)componentTableModel.getValueAt(i, 0);
         gData.setColor((Color)componentTableModel.getValueAt(i, 1));
         DataArray da = gData.getData();
         if (da == null || da.getVeclen() != 1)
            continue;
         float min = 1.e20f, max = -1.e20f, avg = 0, s = 0;
         int counter = 0;
         length = da.getNData();
         selFrames = new boolean[length];
         if (!useSelectedFrames || selRanges.isEmpty())
            for (int j = 0; j < length; j++)
               selFrames[j] = true;
         else
         {
            for (int j = 0; j < length; j++)
               selFrames[j] = false;
            for (int j = 0; j < selRanges.size(); j++)
            {
               subRange = selRanges.get(j);
               for (int k = Math.max(0, subRange[0]); k <= Math.min(subRange[1],length-1); k++)
                  selFrames[k] = true;
            }
         }
         for (int j = 0; j < length; j++)
         {
            if (!selFrames[j])
               continue;
            float x = da.getData(j);
            if (x<min) min = x;
            if (x>max) max = x;
            avg += x;
            s += x*x;
            counter++;
         }
         avg /= (double)counter;
         if(counter > 1) {
            s = (float)(Math.sqrt(s/(double)counter-avg*avg));
         }
         else {
             s = 0.0f;
         }
         componentTableModel.setValueAt(min, i, 2);
         componentTableModel.setValueAt(avg, i, 3);
         componentTableModel.setValueAt(max, i, 4);
         componentTableModel.setValueAt(s, i, 5);
         graphedData.add(gData);
         if (length > n1)
            n1 = length;
      }
      ignoreChanges = false;
      componentTable.repaint();
      graphWorld.setData(0, n1, graphedData, 0.f, (float)n1, new String[] {"x","y"});
   }


   // Variables declaration - do not modify//GEN-BEGIN:variables
   private javax.swing.JButton addButton;
   private javax.swing.JScrollPane componentPane;
   private javax.swing.JPanel controlPanel;
   private pl.edu.icm.visnow.lib.basic.viewers.FieldViewer1D.GraphWorld graphWorld;
   private javax.swing.JPanel jPanel2;
   // End of variables declaration//GEN-END:variables
 }
