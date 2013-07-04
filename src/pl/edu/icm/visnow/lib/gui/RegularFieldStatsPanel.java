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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;
import pl.edu.icm.visnow.lib.types.VNRegularField;
import pl.edu.icm.visnow.datasets.RegularField;

/**
 *
 * @author  Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class RegularFieldStatsPanel extends javax.swing.JPanel
{
   class HistoArea extends JPanel
   {
      float[] histo = null;
      boolean logScale = false;
      public void paint(Graphics g)
      {
         int w = getWidth();
         int h = getHeight();
         g.setColor(Color.WHITE);
         g.fillRect(0,0,w,h);
         if (histo==null)
            return;
         double max = 0;
         if (logScale)
         {
            for (int i = 0; i < histo.length; i++)
               if (max<Math.log1p((double)histo[i])) max=Math.log1p((double)histo[i]);
         }
         else
            for (int i = 0; i < histo.length; i++)
               if (max<histo[i]) max=histo[i];
         max = (h-4)/(max+.01);
         float d = (w-4.f)/histo.length;
         g.setColor(Color.BLUE);
         if (logScale)
         {
            for (int i = 0; i < histo.length; i++)
            {
               int j = (int)(max*Math.log1p((double)histo[i]));
               g.drawLine((int)(i*d+2), h-2, (int)(i*d+2), h-2-j);
            }

         }
         else
         for (int i = 0; i < histo.length; i++)
            g.drawLine((int)(i*d+2), h-2, (int)(i*d+2), (int)(h-2-max*histo[i]));
      }
      public void setHisto(float[] histo, boolean logScale) 
      {
         this.histo = histo;
         this.logScale = logScale;
         repaint();
      }
   }

   class GraphArea extends JPanel
   {
      float[] histo = null;
      boolean logScale = false;
      public void paint(Graphics g)
      {
         int w = getWidth();
         int h = getHeight();
         g.setColor(Color.WHITE);
         g.fillRect(0,0,w,h);
         if (histo==null)
            return;
         double max = 0;
         if (logScale)
         {
            for (int i = 0; i < histo.length; i++)
               if (max<Math.log1p((double)histo[i])) max=Math.log1p((double)histo[i]);
         }
         else
            for (int i = 0; i < histo.length; i++)
               if (max<histo[i]) max=histo[i];
         max = (h-4)/(max+.01);
         float d = (w-4.f)/histo.length;
         g.setColor(Color.BLUE);
         int[] xPoints = new int[histo.length];
         int[] yPoints = new int[histo.length];
         if (logScale)
         {
            for (int i = 0; i < histo.length; i++)
            {
               xPoints[i] = (int)(i*d+2);
               yPoints[i] = h-2-(int)(max*Math.log1p((double)histo[i]));
            }         }
         else
            for (int i = 0; i < histo.length; i++)
            {
               xPoints[i] = (int)(i*d+2);
               yPoints[i] = h-2-(int)(max*histo[i]);
            }
         g.drawPolyline(xPoints, yPoints, histo.length);
      }

      public void setHisto(float[] histo, boolean logScale) 
      {
         this.histo = histo;
         this.logScale = logScale;
         repaint();
      }
   }
   
   HistoArea valArea = new HistoArea();
   HistoArea   dArea = new HistoArea();
   GraphArea thrArea = new GraphArea();
   
   private VNRegularField field = null;
   private RegularField inField = null;
   /** Creates new form RegularFieldStatsPanel */
   public RegularFieldStatsPanel()
   {
      initComponents();
      valHistoPanel.add(valArea, BorderLayout.CENTER);
      thrHistoPanel.add(thrArea, BorderLayout.CENTER);
      derivHistoPanel.add(dArea, BorderLayout.CENTER);
   }

    public void setField(VNRegularField field) 
    {
       if (field==null || field.getField()==null)
          return;
        this.field = field;
        inField = this.field.getField();
        fieldNameLabel.setText(inField.getName());
        StringBuffer dBuf = new StringBuffer(""+inField.getDims()[0]);
        for (int i = 1; i < inField.getDims().length; i++)
           dBuf.append("x"+inField.getDims()[i]);
        dimsLabel.setText(dBuf.toString());
        extLabel.setText(String.format("[%4.1f:%4.1f]x[%4.1f:%4.1f]x[%4.1f:%4.1f]",
                                       inField.getExtents()[0][0],inField.getExtents()[1][0],
                                       inField.getExtents()[0][1],inField.getExtents()[1][1],
                                       inField.getExtents()[0][2],inField.getExtents()[1][2]));
        physExtLabel.setText(String.format("[%4.1f:%4.1f]x[%4.1f:%4.1f]x[%4.1f:%4.1f]",
                                       inField.getPhysExts()[0][0],inField.getPhysExts()[1][0],
                                       inField.getPhysExts()[0][1],inField.getPhysExts()[1][1],
                                       inField.getPhysExts()[0][2],inField.getPhysExts()[1][2]));
        String[] inComponents = new String[inField.getNData()];
        for (int i=0; i<inField.getNData();i++)
           inComponents[i]=inField.getData(i).getName();
        componentsBox.setModel(new javax.swing.DefaultComboBoxModel(inComponents));
        show(0);
    }

    private void show(int n)
    {
      if (field==null || inField == null || n<0 || n>=inField.getNData())
         return;
      vMinLabel.setText(String.format("%7.3f",  field.getMinVal()[n]));
      vMaxLabel.setText(String.format("%7.3f",  field.getMaxVal()[n]));
      vMeanLabel.setText(String.format("%7.3f", field.getAvgVal()[n]));
      dMeanLabel.setText(String.format("%7.3f", field.getAvgGrad()[n]));
      vSDLabel.setText(String.format("%7.3f",   field.getStdDevVal()[n]));
      dSDLabel.setText(String.format("%7.3f",   field.getStdDevGrad()[n]));
      valArea.setHisto(field.getValueHistograms()[n],logBox.isSelected());
      thrArea.setHisto(field.getThrHistograms()[n],logBox.isSelected());
      dArea.setHisto(field.getDerivHistograms()[n],logBox.isSelected());
       
    }
   /** This method is called from within the constructor to
    * initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is
    * always regenerated by the Form Editor.
    */
   // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
   private void initComponents()
   {
      java.awt.GridBagConstraints gridBagConstraints;

      valLabel = new javax.swing.JLabel();
      valALabel = new javax.swing.JLabel();
      valSDLabel = new javax.swing.JLabel();
      gradLabel = new javax.swing.JLabel();
      gradALabel = new javax.swing.JLabel();
      gradSDLabel = new javax.swing.JLabel();
      valHistoPanel = new javax.swing.JPanel();
      jLabel7 = new javax.swing.JLabel();
      derivHistoPanel = new javax.swing.JPanel();
      jLabel8 = new javax.swing.JLabel();
      vMeanLabel = new javax.swing.JLabel();
      vSDLabel = new javax.swing.JLabel();
      dMeanLabel = new javax.swing.JLabel();
      dSDLabel = new javax.swing.JLabel();
      componentsBox = new javax.swing.JComboBox();
      logBox = new javax.swing.JCheckBox();
      valMinLabel = new javax.swing.JLabel();
      valMaxLabel = new javax.swing.JLabel();
      vMinLabel = new javax.swing.JLabel();
      vMaxLabel = new javax.swing.JLabel();
      thrHistoPanel = new javax.swing.JPanel();
      jLabel9 = new javax.swing.JLabel();
      jPanel1 = new javax.swing.JPanel();
      fieldNameLabel = new javax.swing.JLabel();
      jLabel2 = new javax.swing.JLabel();
      dimsLabel = new javax.swing.JLabel();
      jLabel4 = new javax.swing.JLabel();
      extLabel = new javax.swing.JLabel();
      jLabel6 = new javax.swing.JLabel();
      physExtLabel = new javax.swing.JLabel();

      setMinimumSize(new java.awt.Dimension(180, 550));
      setPreferredSize(new java.awt.Dimension(200, 600));
      setLayout(new java.awt.GridBagLayout());

      valLabel.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
      valLabel.setText("values");
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
      gridBagConstraints.weightx = 1.0;
      add(valLabel, gridBagConstraints);

      valALabel.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
      valALabel.setText("mean");
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 4;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.weightx = 1.0;
      add(valALabel, gridBagConstraints);

      valSDLabel.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
      valSDLabel.setText("std. dev.");
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 5;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      add(valSDLabel, gridBagConstraints);

      gradLabel.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
      gradLabel.setText("derivatives");
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 6;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
      add(gradLabel, gridBagConstraints);

      gradALabel.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
      gradALabel.setText("mean");
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 6;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
      add(gradALabel, gridBagConstraints);

      gradSDLabel.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
      gradSDLabel.setText("std. dev");
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 7;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      add(gradSDLabel, gridBagConstraints);

      valHistoPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
      valHistoPanel.setMinimumSize(new java.awt.Dimension(180, 200));
      valHistoPanel.setPreferredSize(new java.awt.Dimension(200, 200));
      valHistoPanel.setLayout(new java.awt.BorderLayout());
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 9;
      gridBagConstraints.gridwidth = 3;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 1.0;
      add(valHistoPanel, gridBagConstraints);

      jLabel7.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
      jLabel7.setText("value histogram");
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 8;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.insets = new java.awt.Insets(4, 0, 1, 0);
      add(jLabel7, gridBagConstraints);

      derivHistoPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
      derivHistoPanel.setMinimumSize(new java.awt.Dimension(180, 200));
      derivHistoPanel.setPreferredSize(new java.awt.Dimension(200, 200));
      derivHistoPanel.setLayout(new java.awt.BorderLayout());
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 11;
      gridBagConstraints.gridwidth = 3;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 1.0;
      add(derivHistoPanel, gridBagConstraints);

      jLabel8.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
      jLabel8.setText("derivatives histogram");
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 10;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.insets = new java.awt.Insets(5, 0, 1, 0);
      add(jLabel8, gridBagConstraints);

      vMeanLabel.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
      vMeanLabel.setText(" ");
      vMeanLabel.setMaximumSize(new java.awt.Dimension(100, 15));
      vMeanLabel.setMinimumSize(new java.awt.Dimension(50, 15));
      vMeanLabel.setPreferredSize(new java.awt.Dimension(80, 15));
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridy = 4;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.weightx = 1.0;
      add(vMeanLabel, gridBagConstraints);

      vSDLabel.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
      vSDLabel.setText(" ");
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridy = 5;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      add(vSDLabel, gridBagConstraints);

      dMeanLabel.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
      dMeanLabel.setText(" ");
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridy = 6;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
      add(dMeanLabel, gridBagConstraints);

      dSDLabel.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridy = 7;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      add(dSDLabel, gridBagConstraints);

      componentsBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
      componentsBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
      componentsBox.addItemListener(new java.awt.event.ItemListener()
      {
         public void itemStateChanged(java.awt.event.ItemEvent evt)
         {
            componentsBoxItemStateChanged(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
      gridBagConstraints.insets = new java.awt.Insets(0, 0, 7, 0);
      add(componentsBox, gridBagConstraints);

      logBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
      logBox.setText("log scale");
      logBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
      logBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
      logBox.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            logBoxActionPerformed(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.insets = new java.awt.Insets(0, 3, 8, 0);
      add(logBox, gridBagConstraints);

      valMinLabel.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
      valMinLabel.setText("min");
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.weightx = 1.0;
      add(valMinLabel, gridBagConstraints);

      valMaxLabel.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
      valMaxLabel.setText("max");
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 3;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.weightx = 1.0;
      add(valMaxLabel, gridBagConstraints);

      vMinLabel.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
      vMinLabel.setText(" ");
      vMinLabel.setMaximumSize(new java.awt.Dimension(100, 15));
      vMinLabel.setMinimumSize(new java.awt.Dimension(50, 15));
      vMinLabel.setPreferredSize(new java.awt.Dimension(80, 15));
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.weightx = 1.0;
      add(vMinLabel, gridBagConstraints);

      vMaxLabel.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
      vMaxLabel.setText(" ");
      vMaxLabel.setMaximumSize(new java.awt.Dimension(100, 15));
      vMaxLabel.setMinimumSize(new java.awt.Dimension(50, 15));
      vMaxLabel.setPreferredSize(new java.awt.Dimension(80, 15));
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridy = 3;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.weightx = 1.0;
      add(vMaxLabel, gridBagConstraints);

      thrHistoPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
      thrHistoPanel.setMinimumSize(new java.awt.Dimension(180, 200));
      thrHistoPanel.setPreferredSize(new java.awt.Dimension(200, 200));
      thrHistoPanel.setLayout(new java.awt.BorderLayout());
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 13;
      gridBagConstraints.gridwidth = 3;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 1.0;
      add(thrHistoPanel, gridBagConstraints);

      jLabel9.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
      jLabel9.setText("threshold preferences (small value is best)");
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 12;
      gridBagConstraints.gridwidth = 3;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
      gridBagConstraints.insets = new java.awt.Insets(5, 0, 1, 0);
      add(jLabel9, gridBagConstraints);

      jPanel1.setMinimumSize(new java.awt.Dimension(100, 60));
      jPanel1.setPreferredSize(new java.awt.Dimension(100, 75));
      jPanel1.setLayout(new java.awt.GridBagLayout());

      fieldNameLabel.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 1.0;
      gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
      jPanel1.add(fieldNameLabel, gridBagConstraints);

      jLabel2.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
      jLabel2.setText("dimensions");
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
      jPanel1.add(jLabel2, gridBagConstraints);

      dimsLabel.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.weightx = 1.0;
      jPanel1.add(dimsLabel, gridBagConstraints);

      jLabel4.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
      jLabel4.setText("extents");
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
      jPanel1.add(jLabel4, gridBagConstraints);

      extLabel.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.weightx = 1.0;
      jPanel1.add(extLabel, gridBagConstraints);

      jLabel6.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
      jLabel6.setText("physical extents");
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 3;
      gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
      jPanel1.add(jLabel6, gridBagConstraints);

      physExtLabel.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 3;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.weightx = 1.0;
      jPanel1.add(physExtLabel, gridBagConstraints);

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridwidth = 3;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
      add(jPanel1, gridBagConstraints);
   }// </editor-fold>//GEN-END:initComponents

private void logBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logBoxActionPerformed
   show(componentsBox.getSelectedIndex());
}//GEN-LAST:event_logBoxActionPerformed

   private void componentsBoxItemStateChanged(java.awt.event.ItemEvent evt)//GEN-FIRST:event_componentsBoxItemStateChanged
   {//GEN-HEADEREND:event_componentsBoxItemStateChanged
      show(componentsBox.getSelectedIndex());
}//GEN-LAST:event_componentsBoxItemStateChanged
   
   
   // Variables declaration - do not modify//GEN-BEGIN:variables
   private javax.swing.JComboBox componentsBox;
   private javax.swing.JLabel dMeanLabel;
   private javax.swing.JLabel dSDLabel;
   private javax.swing.JPanel derivHistoPanel;
   private javax.swing.JLabel dimsLabel;
   private javax.swing.JLabel extLabel;
   private javax.swing.JLabel fieldNameLabel;
   private javax.swing.JLabel gradALabel;
   private javax.swing.JLabel gradLabel;
   private javax.swing.JLabel gradSDLabel;
   private javax.swing.JLabel jLabel2;
   private javax.swing.JLabel jLabel4;
   private javax.swing.JLabel jLabel6;
   private javax.swing.JLabel jLabel7;
   private javax.swing.JLabel jLabel8;
   private javax.swing.JLabel jLabel9;
   private javax.swing.JPanel jPanel1;
   private javax.swing.JCheckBox logBox;
   private javax.swing.JLabel physExtLabel;
   private javax.swing.JPanel thrHistoPanel;
   private javax.swing.JLabel vMaxLabel;
   private javax.swing.JLabel vMeanLabel;
   private javax.swing.JLabel vMinLabel;
   private javax.swing.JLabel vSDLabel;
   private javax.swing.JLabel valALabel;
   private javax.swing.JPanel valHistoPanel;
   private javax.swing.JLabel valLabel;
   private javax.swing.JLabel valMaxLabel;
   private javax.swing.JLabel valMinLabel;
   private javax.swing.JLabel valSDLabel;
   // End of variables declaration//GEN-END:variables
   
}
