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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import javax.vecmath.Color3f;
import pl.edu.icm.visnow.datasets.DataSchema;
import pl.edu.icm.visnow.datasets.dataarrays.DataArraySchema;
import pl.edu.icm.visnow.geometries.parameters.AbstractDataMappingParams;
import pl.edu.icm.visnow.geometries.parameters.AbstractRenderingParams;
import pl.edu.icm.visnow.geometries.parameters.ColorComponentParams;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.render.RenderEvent;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.render.RenderEventListener;

/**
 *
 * @author know
 */
public class ComponentColormappingPanel extends javax.swing.JPanel implements RenderEventListener
{
   private AbstractDataMappingParams params;
   private AbstractRenderingParams renderParams = null;
   private ColorComponentParams mapParams = null;
   private DataSchema schema = null;
   private boolean fix0 = false;
   private boolean map0 = true;
   private boolean continousUpdate = true;
   private float min, max;
   private float physMin, physMax;
   private float d;
   
   /**
    * Creates new form ComponentColormappingPanel
    */
   public ComponentColormappingPanel()
   {
      initComponents();
      colorEditor.setVisible(false);
      colorEditor.setEnabled(false);
      colorCompSelector.setAddNullComponent(true);
      colorCompSelector.setTitle("color component");
      symRangeSlider.setShowingFields(false);
   }
   

   public void setMap0(boolean map0)
   {
      this.map0 = map0;
   }
   
   public void setPresentation(boolean simple)
   {
      GridBagConstraints gridBagConstraints;
      Dimension simpleDim = new Dimension(210, 207);
      Dimension expertDim = new Dimension(210, 230);
      Dimension mVPanelSimpleDim = new Dimension(208, 105);
      Dimension mVPanelExpertDim = new Dimension(223, 130);
      
      if (simple)
      {
         mapVariablePanel.remove(wrapBox);
         mapVariablePanel.remove(fix0CheckBox);
         wrapBox.setVisible(false);
         fix0CheckBox.setVisible(false);
         mapVariablePanel.setMinimumSize(mVPanelSimpleDim);
         mapVariablePanel.setPreferredSize(mVPanelSimpleDim);
         mapVariablePanel.setMaximumSize(mVPanelSimpleDim);
         setMinimumSize(simpleDim);
         setPreferredSize(simpleDim);
         setMaximumSize(simpleDim);
      } else
      {
         setMinimumSize(expertDim);
         setPreferredSize(expertDim);
         setMaximumSize(expertDim);
         mapVariablePanel.setMinimumSize(mVPanelExpertDim);
         mapVariablePanel.setPreferredSize(mVPanelExpertDim);
         mapVariablePanel.setMaximumSize(mVPanelExpertDim);
         gridBagConstraints = new java.awt.GridBagConstraints();
         gridBagConstraints.gridx = 0;
         gridBagConstraints.gridy = 2;
         gridBagConstraints.weightx = 1.0;
         gridBagConstraints.weighty = 0.0;
         mapVariablePanel.add(wrapBox, gridBagConstraints);
         gridBagConstraints = new java.awt.GridBagConstraints();
         gridBagConstraints.gridx = 1;
         gridBagConstraints.gridy = 2;
         gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
         gridBagConstraints.weightx = 1.0;
         gridBagConstraints.weighty = 0.0;
         gridBagConstraints.insets = new java.awt.Insets(0, 23, 0, 0);
         mapVariablePanel.add(fix0CheckBox, gridBagConstraints);
         wrapBox.setVisible(true);
         fix0CheckBox.setVisible(true);
      }
      validate();
   }
   
   public void setParams(AbstractDataMappingParams params)
   {
      if(this.params != null)
          this.params.removeRenderEventListener(this);
      this.params = params;
      this.params.addRenderEventListener(this);
      mapParams = map0 ? params.getColorMap0Params() : params.getColorMap1Params();
      colormapChooser.setParams(mapParams);
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
      dataRangeSlider.setParams(physMin, physMax, physMin, physMax);
      float smax = Math.max(Math.abs(physMin), Math.abs(physMax));
      symRangeSlider.setMinMax(0, smax);
      symRangeSlider.setVal(smax);
      mapParams.setDataMinMax(min + d * (dataRangeSlider.getLow() - physMin), min + d * (dataRangeSlider.getUp() - physMin));
   }

   public void setData(DataSchema schema)
   {
      if (schema == null || params == null)
         return;
      if (!schema.isDataCompatibleWith(this.schema))
      {
         this.schema = schema;
         colorCompSelector.setDataSchema(schema);
      }
      if (params == null)
         return;
      if (colorCompSelector.getComponent() >= 0)
      { 
         updateDataRanges(schema.getSchema(colorCompSelector.getComponent()));
         colorEditor.setVisible(false);
         colormapChooser.setVisible(true);
         mapParams.setDataComponent(colorCompSelector.getComponent());
         mapParams.setDataMinMax(min, max);
         if (physMin >= physMax)
            physMax = physMin + .1f;
         params.getColormapLegendParameters().setColormapLow(physMin);
         params.getColormapLegendParameters().setColormapUp(physMax);
      }
   }
      
   public void updateDataRanges()
   {
      if (schema == null || colorCompSelector.getComponent() < 0 ||
                            colorCompSelector.getComponent() >= schema.getComponentSchemas().size())
         return;
      updateDataRanges(schema.getSchema(colorCompSelector.getComponent()));
   }

   public void setParams(AbstractRenderingParams params)
   {
      this.renderParams = params;
   }
   
   public void setNull(boolean addNull)
   {
      colorCompSelector.setAddNullComponent(addNull);
   }
           

   /**
    * This method is called from within the constructor to initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is always
    * regenerated by the Form Editor.
    */
   @SuppressWarnings("unchecked")
   // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
   private void initComponents()
   {
      java.awt.GridBagConstraints gridBagConstraints;

      colormapChooser = new pl.edu.icm.visnow.datamaps.ColormapChooser();
      mapVariablePanel = new javax.swing.JPanel();
      colorCompSelector = new pl.edu.icm.visnow.lib.gui.DataComponentSelector();
      dataRangeSlider = new pl.edu.icm.visnow.gui.widgets.FloatSubRangeSlider.ExtendedFloatSubRangeSlider();
      wrapBox = new javax.swing.JCheckBox();
      fix0CheckBox = new javax.swing.JCheckBox();
      symRangeSlider = new pl.edu.icm.visnow.gui.widgets.FloatSlider();
      colorEditor = new pl.edu.icm.visnow.gui.widgets.ColorEditor();

      setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
      setMaximumSize(new java.awt.Dimension(2147483647, 236));
      setMinimumSize(new java.awt.Dimension(180, 236));
      setPreferredSize(new java.awt.Dimension(212, 236));
      setLayout(new java.awt.GridBagLayout());

      colormapChooser.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
      colormapChooser.setMinimumSize(new java.awt.Dimension(180, 102));
      colormapChooser.setPreferredSize(new java.awt.Dimension(200, 95));
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
      gridBagConstraints.weightx = 1.0;
      add(colormapChooser, gridBagConstraints);

      mapVariablePanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
      mapVariablePanel.setInheritsPopupMenu(true);
      mapVariablePanel.setMaximumSize(new java.awt.Dimension(2147483647, 130));
      mapVariablePanel.setMinimumSize(new java.awt.Dimension(180, 130));
      mapVariablePanel.setPreferredSize(new java.awt.Dimension(200, 130));
      mapVariablePanel.setLayout(new java.awt.GridBagLayout());

      colorCompSelector.setBorder(null);
      colorCompSelector.setMaximumSize(new java.awt.Dimension(250, 36));
      colorCompSelector.setMinimumSize(new java.awt.Dimension(180, 36));
      colorCompSelector.setPreferredSize(new java.awt.Dimension(200, 36));
      colorCompSelector.addChangeListener(new javax.swing.event.ChangeListener()
      {
         public void stateChanged(javax.swing.event.ChangeEvent evt)
         {
            colorCompSelectorStateChanged(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      mapVariablePanel.add(colorCompSelector, gridBagConstraints);

      dataRangeSlider.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "mapped range", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
      dataRangeSlider.setMaximumSize(new java.awt.Dimension(240, 61));
      dataRangeSlider.setMinimumSize(new java.awt.Dimension(96, 59));
      dataRangeSlider.setName(""); // NOI18N
      dataRangeSlider.setPreferredSize(new java.awt.Dimension(216, 65));
      dataRangeSlider.addChangeListener(new javax.swing.event.ChangeListener()
      {
         public void stateChanged(javax.swing.event.ChangeEvent evt)
         {
            dataRangeSliderStateChanged(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
      mapVariablePanel.add(dataRangeSlider, gridBagConstraints);

      wrapBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
      wrapBox.setText("wrap data");
      wrapBox.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            wrapBoxActionPerformed(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 3;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.weightx = 1.0;
      mapVariablePanel.add(wrapBox, gridBagConstraints);

      fix0CheckBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
      fix0CheckBox.setText("fix 0");
      fix0CheckBox.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            fix0CheckBoxActionPerformed(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 3;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new java.awt.Insets(0, 23, 0, 0);
      mapVariablePanel.add(fix0CheckBox, gridBagConstraints);

      symRangeSlider.setMaximumSize(new java.awt.Dimension(240, 65));
      symRangeSlider.setMinimumSize(new java.awt.Dimension(180, 64));
      symRangeSlider.setPreferredSize(new java.awt.Dimension(220, 65));
      symRangeSlider.setVisible(false);
      symRangeSlider.addChangeListener(new javax.swing.event.ChangeListener()
      {
         public void stateChanged(javax.swing.event.ChangeEvent evt)
         {
            symRangeSliderStateChanged(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      mapVariablePanel.add(symRangeSlider, gridBagConstraints);

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
      gridBagConstraints.weightx = 1.0;
      add(mapVariablePanel, gridBagConstraints);

      colorEditor.addChangeListener(new javax.swing.event.ChangeListener()
      {
         public void stateChanged(javax.swing.event.ChangeEvent evt)
         {
            colorEditorStateChanged(evt);
         }
      });

      javax.swing.GroupLayout colorEditorLayout = new javax.swing.GroupLayout(colorEditor);
      colorEditor.setLayout(colorEditorLayout);
      colorEditorLayout.setHorizontalGroup(
         colorEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGap(0, 245, Short.MAX_VALUE)
      );
      colorEditorLayout.setVerticalGroup(
         colorEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGap(0, 21, Short.MAX_VALUE)
      );

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
      add(colorEditor, gridBagConstraints);

      getAccessibleContext().setAccessibleName("");
   }// </editor-fold>//GEN-END:initComponents

   private void colorCompSelectorStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_colorCompSelectorStateChanged
   {//GEN-HEADEREND:event_colorCompSelectorStateChanged
      updateComponent();
   }//GEN-LAST:event_colorCompSelectorStateChanged

   private void dataRangeSliderStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_dataRangeSliderStateChanged
   {//GEN-HEADEREND:event_dataRangeSliderStateChanged
      if (mapParams == null)
        return;
      if(!continousUpdate && dataRangeSlider.isAdjusting())
          return;
      mapParams.setAdjusting(dataRangeSlider.isAdjusting());
      mapParams.setDataMinMax(min + d * (dataRangeSlider.getLow() - physMin), min + d * (dataRangeSlider.getUp() - physMin));
      params.getColormapLegendParameters().setColormapLow(dataRangeSlider.getLow());
      params.getColormapLegendParameters().setColormapUp(dataRangeSlider.getUp());
   }//GEN-LAST:event_dataRangeSliderStateChanged

   private void wrapBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_wrapBoxActionPerformed
   {//GEN-HEADEREND:event_wrapBoxActionPerformed
      mapParams.setWrap(wrapBox.isSelected());
   }//GEN-LAST:event_wrapBoxActionPerformed

   private void fix0CheckBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_fix0CheckBoxActionPerformed
   {//GEN-HEADEREND:event_fix0CheckBoxActionPerformed
      fix0 = fix0CheckBox.isSelected();
      symRangeSlider.setVisible(fix0);
      dataRangeSlider.setVisible(!fix0);
   }//GEN-LAST:event_fix0CheckBoxActionPerformed

   private void symRangeSliderStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_symRangeSliderStateChanged
   {//GEN-HEADEREND:event_symRangeSliderStateChanged
      if (mapParams == null || symRangeSlider.getVal() < 1e-5f)
      return;
      float v = d * symRangeSlider.getVal();
      mapParams.setDataMinMax(-v, v);
      params.getColormapLegendParameters().setColormapLow(-symRangeSlider.getVal());
      params.getColormapLegendParameters().setColormapUp(symRangeSlider.getVal());
   }//GEN-LAST:event_symRangeSliderStateChanged

   private void colorEditorStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_colorEditorStateChanged
   {//GEN-HEADEREND:event_colorEditorStateChanged
      if (renderParams == null)
         return;
      renderParams.setDiffuseColor(new Color3f(colorEditor.getColorComponents()));
      renderParams.setAmbientColor(new Color3f(colorEditor.getColorComponents()));
   }//GEN-LAST:event_colorEditorStateChanged

   public void updateComponent()
   {
      if (mapParams == null)
         return;
      mapParams.setActive(false);
      int selectedComponent = colorCompSelector.getComponent();
      mapParams.setDataComponent(selectedComponent);
      if (selectedComponent >= 0)
      {
         colorEditor.setVisible(false);
         colorEditor.setEnabled(false);
         DataArraySchema s = schema.getSchema(selectedComponent);
         updateDataRanges(s);
         colormapChooser.setVisible(true);
         params.getColormapLegendParameters().setTexts(s.getName(), s.getUnit());
      } else
      {
         colorEditor.setVisible(true);
         colorEditor.setEnabled(true);
         colormapChooser.setVisible(false);
      }
      mapParams.setActive(true);
      mapParams.fireStateChanged();
   }

    public void setColorMap(int map)
    {
       colormapChooser.setSelectedIndex(map);
       params.getColorMap0Params().setMapType(map);
    }
    
   private void updateParams()
   {
      mapParams.setActive(false);
      mapParams.setDataComponent(colorCompSelector.getComponent());
      mapParams.setDataMinMax(min + d * (dataRangeSlider.getLow() - physMin), min + d * (dataRangeSlider.getUp() - physMin));
      mapParams.setMapType(colormapChooser.getSelectedIndex());
      mapParams.setWrap(wrapBox.isSelected());
      mapParams.setActive(true);
      params.fireStateChanged(WIDTH);
   }
   
   public boolean isAdjusting() {
       return dataRangeSlider.isAdjusting() || colormapChooser.isAdjusting();
   }

    /**
     * @param continousUpdate the continousUpdate to set
     */
    public void setContinousUpdate(boolean continousUpdate) {
        this.continousUpdate = continousUpdate;
    }

    @Override
    public void renderExtentChanged(RenderEvent e) {
        if(e == null)
            return;
//        wyglada na zbedne - gdyby przywracac, trzeba przejsc przez przeskalowanie min/max->physMin/physMax
//        if(e.getUpdateExtent() == RenderEvent.COLORS) {
//            dataRangeSlider.setActive(false);            
//            dataRangeSlider.setLow(mapParams.getDataMin());
//            dataRangeSlider.setUp(mapParams.getDataMax());
//            dataRangeSlider.setActive(true, false);
//        }
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        colorCompSelector.setEnabled(enabled);
        dataRangeSlider.setEnabled(enabled);
        wrapBox.setEnabled(enabled);
        fix0CheckBox.setEnabled(enabled);
        colormapChooser.setEnabled(enabled);
        repaint();
    }
   
   // Variables declaration - do not modify//GEN-BEGIN:variables
   private pl.edu.icm.visnow.lib.gui.DataComponentSelector colorCompSelector;
   private pl.edu.icm.visnow.gui.widgets.ColorEditor colorEditor;
   private pl.edu.icm.visnow.datamaps.ColormapChooser colormapChooser;
   private pl.edu.icm.visnow.gui.widgets.FloatSubRangeSlider.ExtendedFloatSubRangeSlider dataRangeSlider;
   private javax.swing.JCheckBox fix0CheckBox;
   private javax.swing.JPanel mapVariablePanel;
   private pl.edu.icm.visnow.gui.widgets.FloatSlider symRangeSlider;
   private javax.swing.JCheckBox wrapBox;
   // End of variables declaration//GEN-END:variables

}
