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

package pl.edu.icm.visnow.geometries.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import pl.edu.icm.visnow.datasets.DataSchema;
import pl.edu.icm.visnow.datasets.dataarrays.DataArraySchema;
import pl.edu.icm.visnow.geometries.parameters.ColorComponentParams;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class ColorComponentPanel extends javax.swing.JPanel
{
   private ColorComponentParams params = null;
   private DataSchema schema = null;
   private Color c2 = getBackground();
   private Color c1 = getBackground();
   private BufferedImage sample = new BufferedImage(100,10,BufferedImage.TYPE_INT_RGB);
   private int lastX = 0, lastY = 0;
   private float lastMin = .1f, cMin = .1f;

   private float min, max;
   private float physMin, physMax;
   private float d;
   
   /** Creates new form ColorComponentPanel */
   public ColorComponentPanel()
   {
      initComponents();
      compSelector.setAddNullComponent(true);
      updateSample();
   }

   public void setParams(ColorComponentParams params)
   {
      this.params = params;
      cMin = params.getCmpMin();
      params.setActive(false);
      compSelector.setComponent(params.getDataComponent());
      if (schema != null)
      {
         DataArraySchema s = schema.getSchema(compSelector.getComponent());
         if (s != null)
         {
            dataRangeSlider.setMinimum(s.getMinv());
            dataRangeSlider.setMaximum(s.getMaxv());
         }
      }
      dataRangeSlider.setBottomValue(params.getDataMin());
      dataRangeSlider.setTopValue(params.getDataMax());
      params.setActive(true);
   }

   public void setData(DataSchema schema)
   {
      this.schema = schema;
      compSelector.setDataSchema(schema);
      updateComponent();
   }
   
   private void updateSample()
   {
      int w = sample.getWidth();
      int h = sample.getHeight();
      float s = 1.f / (w - 1);
      int[] b1 = new int[] {c1.getRed(), c1.getGreen(), c1.getBlue()};
      int[] b2 = new int[] {c2.getRed(), c2.getGreen(), c2.getBlue()};
      int[] b = new int[3];
      int[] sampleData = sample.getRGB(0, 0, w, h, null, 0, w);
      for (int i = 0; i < w; i++)
      {
         float t = i * s;
         for (int k = 0; k < 3; k++)
            b[k] = (int)(t * b2[k] + (1-t) * b1[k]);
         for (int j = 0; j < h; j++)
            sampleData[j * w + i] = (b[0] << 16) | (b[1] << 8) | b[2];
         sample.setRGB(0, 0, w, h, sampleData, 0, w);
      }
      this.repaint();
   }
   
   public void setSampleColors(Color col1, Color col2)
   {
      c1 = col1;
      c2 = col2;
      updateSample();
   }
   
   private void updateDataRanges(DataArraySchema daSchema)
   {
      min = daSchema.getMinv();
      max = daSchema.getMaxv();
      physMin = daSchema.getPhysMin();
      physMax = daSchema.getPhysMax();
      if (physMin == physMax)
         d = 1;
      else
         d = (max - min) / (physMax - physMin);
      dataRangeSlider.setValues(physMin, physMin, physMax, physMax);
   }
   
   private void updateComponent()
   {
      if (params == null)
         return;
      DataArraySchema s = schema.getSchema(compSelector.getComponent());
      if (s != null)
         updateDataRanges(s);
      else
         dataRangeSlider.setValues(0, 0, 100, 100);
      params.setDataComponent(compSelector.getComponent());
      params.setDataMinMax(dataRangeSlider.getBottomValue(), dataRangeSlider.getTopValue());
      params.setActive(true);
      params.fireStateChanged();
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

      compSelector = new pl.edu.icm.visnow.lib.gui.DataComponentSelector();
      dataRangeSlider = new pl.edu.icm.visnow.gui.widgets.FloatSubRangeSlider.FloatSubRangeSlider();
      samplePanel = new JPanel()
      {
         public void paint(Graphics g)
         {
            Graphics2D gr = (Graphics2D)g;
            gr.drawImage(sample,0,0,getWidth(), getHeight(), null);
            gr.setColor(Color.DARK_GRAY);
            int p = (int)(cMin * getWidth());
            gr.fillRect(0,0,p,getHeight());
         }
      };

      setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
      setPreferredSize(new java.awt.Dimension(202, 101));
      setLayout(new java.awt.GridBagLayout());

      compSelector.setBorder(null);
      compSelector.setMinimumSize(new java.awt.Dimension(150, 35));
      compSelector.setName("compSelector"); // NOI18N
      compSelector.setPreferredSize(new java.awt.Dimension(200, 40));
      compSelector.addChangeListener(new javax.swing.event.ChangeListener() {
         public void stateChanged(javax.swing.event.ChangeEvent evt) {
            compSelectorStateChanged(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      add(compSelector, gridBagConstraints);

      dataRangeSlider.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "data range", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
      dataRangeSlider.setFont(new java.awt.Font("Dialog", 0, 8)); // NOI18N
      dataRangeSlider.setMinimumSize(new java.awt.Dimension(60, 52));
      dataRangeSlider.setName("dataRangeSlider"); // NOI18N
      dataRangeSlider.setPaintLabels(true);
      dataRangeSlider.setPaintTicks(true);
      dataRangeSlider.setPreferredSize(new java.awt.Dimension(200, 57));
      dataRangeSlider.addChangeListener(new javax.swing.event.ChangeListener() {
         public void stateChanged(javax.swing.event.ChangeEvent evt) {
            dataRangeSliderStateChanged(evt);
         }
      });

      javax.swing.GroupLayout dataRangeSliderLayout = new javax.swing.GroupLayout(dataRangeSlider);
      dataRangeSlider.setLayout(dataRangeSliderLayout);
      dataRangeSliderLayout.setHorizontalGroup(
         dataRangeSliderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGap(0, 256, Short.MAX_VALUE)
      );
      dataRangeSliderLayout.setVerticalGroup(
         dataRangeSliderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGap(0, 51, Short.MAX_VALUE)
      );

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 0.3;
      add(dataRangeSlider, gridBagConstraints);

      samplePanel.setMaximumSize(new java.awt.Dimension(32767, 15));
      samplePanel.setMinimumSize(new java.awt.Dimension(100, 15));
      samplePanel.setName("samplePanel"); // NOI18N
      samplePanel.setPreferredSize(new java.awt.Dimension(180, 20));
      samplePanel.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mousePressed(java.awt.event.MouseEvent evt) {
            samplePanelMousePressed(evt);
         }
         public void mouseReleased(java.awt.event.MouseEvent evt) {
            samplePanelMouseReleased(evt);
         }
      });
      samplePanel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
         public void mouseDragged(java.awt.event.MouseEvent evt) {
            samplePanelMouseDragged(evt);
         }
      });

      javax.swing.GroupLayout samplePanelLayout = new javax.swing.GroupLayout(samplePanel);
      samplePanel.setLayout(samplePanelLayout);
      samplePanelLayout.setHorizontalGroup(
         samplePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGap(0, 256, Short.MAX_VALUE)
      );
      samplePanelLayout.setVerticalGroup(
         samplePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGap(0, 20, Short.MAX_VALUE)
      );

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.insets = new java.awt.Insets(1, 3, 1, 3);
      add(samplePanel, gridBagConstraints);
   }// </editor-fold>//GEN-END:initComponents

    private void compSelectorStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_compSelectorStateChanged
    {//GEN-HEADEREND:event_compSelectorStateChanged
        updateComponent();
}//GEN-LAST:event_compSelectorStateChanged

    private void dataRangeSliderStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_dataRangeSliderStateChanged
    {//GEN-HEADEREND:event_dataRangeSliderStateChanged
       if (params != null) 
       {
          params.setAdjusting(dataRangeSlider.isAdjusting());
          params.setDataMinMax(min + d * (dataRangeSlider.getBottomValue() - physMin), min + d * (dataRangeSlider.getTopValue() - physMin));
       }
}//GEN-LAST:event_dataRangeSliderStateChanged

    private void samplePanelMousePressed(java.awt.event.MouseEvent evt)//GEN-FIRST:event_samplePanelMousePressed
    {//GEN-HEADEREND:event_samplePanelMousePressed
       lastX = evt.getX();
       lastMin = cMin;
    }//GEN-LAST:event_samplePanelMousePressed

    private void samplePanelMouseDragged(java.awt.event.MouseEvent evt)//GEN-FIRST:event_samplePanelMouseDragged
    {//GEN-HEADEREND:event_samplePanelMouseDragged
       cMin = lastMin + (float)(evt.getX() - lastX) / samplePanel.getWidth();
       if (cMin < 0) cMin = 0;
       if (cMin > 1) cMin = 1;
       samplePanel.repaint();
       if (params != null)
       {
          params.setAdjusting(true);
          params.setCmpMin(cMin);
       }
    }//GEN-LAST:event_samplePanelMouseDragged

   private void samplePanelMouseReleased(java.awt.event.MouseEvent evt)//GEN-FIRST:event_samplePanelMouseReleased
   {//GEN-HEADEREND:event_samplePanelMouseReleased
       if (params != null)
          params.setAdjusting(true);
   }//GEN-LAST:event_samplePanelMouseReleased

   // Variables declaration - do not modify//GEN-BEGIN:variables
   private pl.edu.icm.visnow.lib.gui.DataComponentSelector compSelector;
   private pl.edu.icm.visnow.gui.widgets.FloatSubRangeSlider.FloatSubRangeSlider dataRangeSlider;
   private javax.swing.JPanel samplePanel;
   // End of variables declaration//GEN-END:variables
}
