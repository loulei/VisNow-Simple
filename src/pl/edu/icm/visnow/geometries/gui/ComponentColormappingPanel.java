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
package pl.edu.icm.visnow.geometries.gui;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import javax.vecmath.Color3f;
import org.apache.log4j.Logger;
import pl.edu.icm.visnow.datasets.DataSchema;
import pl.edu.icm.visnow.datasets.dataarrays.DataArraySchema;
import pl.edu.icm.visnow.geometries.parameters.AbstractDataMappingParams;
import pl.edu.icm.visnow.geometries.parameters.AbstractRenderingParams;
import pl.edu.icm.visnow.geometries.parameters.ColorComponentParams;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.render.RenderEvent;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.render.RenderEventListener;

/**
 *
 * @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class ComponentColormappingPanel extends javax.swing.JPanel implements RenderEventListener {
    private static final Logger LOGGER = Logger.getLogger(ComponentColormappingPanel.class);
    
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
    private boolean silent = false;

    /**
     * Creates new form ComponentColormappingPanel
     */
    public ComponentColormappingPanel() {
        initComponents();
        colorEditor.setVisible(false);
        colorEditor.setEnabled(false);
        colorCompSelector.setAddNullComponent(true);
        colorCompSelector.setTitle("color component");

        //fix to avoid "jumping" of the whole panel (by default symRangeSlider is smaller than dataRangeslider)
        symRangeSlider.setPreferredSize(dataRangeSlider.getSize());
    }

    public void setMap0(boolean map0) {
        this.map0 = map0;
    }

    public void setPresentation(boolean simple) {
        wrapBox.setVisible(!simple);
        fix0CheckBox.setVisible(!simple);
        if (simple) {
            //if simple then revert to full range slider
            if (fix0CheckBox.isSelected()) fix0CheckBox.setSelected(false);
            setRangeSliderType(false);
        }
        revalidate();
    }

    public void setParams(AbstractDataMappingParams params) {
        if (this.params != null)
            this.params.removeRenderEventListener(this);
        this.params = params;
        this.params.addRenderEventListener(this);
        mapParams = map0 ? params.getColorMap0Params() : params.getColorMap1Params();
        colormapChooser.setParams(mapParams);
    }

    private void updateDataRanges(DataArraySchema daSchema) {
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
        symRangeSlider.setAll(0, smax, smax);
        mapParams.setDataMinMax(min + d * (dataRangeSlider.getLow() - physMin), min + d * (dataRangeSlider.getUp() - physMin));
        params.getColormapLegendParameters().setColormapLow(dataRangeSlider.getLow());
        params.getColormapLegendParameters().setColormapUp(dataRangeSlider.getUp());
    }

    public void setData(DataSchema schema) {
        if (schema == null || params == null)
            return;
        if (!schema.isDataCompatibleWith(this.schema, true, true)) {
            this.schema = schema;
            colorCompSelector.setDataSchema(schema);
        }
        if (params == null)
            return;
        if (colorCompSelector.getComponent() >= 0) {
            updateDataRanges(schema.getSchema(colorCompSelector.getComponent()));
            colorEditor.setVisible(false);
            colormapChooser.setVisible(true);
            //mapParams.setDataComponent(colorCompSelector.getComponent());
            //mapParams.setDataMinMax(min, max);
            mapParams.setDataComponent(colorCompSelector.getComponent(), min, max);
            if (physMin >= physMax)
                physMax = physMin + .1f;
            params.getColormapLegendParameters().setColormapLow(physMin);
            params.getColormapLegendParameters().setColormapUp(physMax);
        }
    }

    public void updateDataRanges() {
        if (schema == null || colorCompSelector.getComponent() < 0
                || colorCompSelector.getComponent() >= schema.getComponentSchemas().size())
            return;
        updateDataRanges(schema.getSchema(colorCompSelector.getComponent()));
    }

    public void setParams(AbstractRenderingParams params) {
        this.renderParams = params;
    }

    public void setNull(boolean addNull) {
        colorCompSelector.setAddNullComponent(addNull);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        colormapChooser = new pl.edu.icm.visnow.datamaps.ColormapChooser();
        mapVariablePanel = new javax.swing.JPanel();
        colorCompSelector = new pl.edu.icm.visnow.lib.gui.DataComponentSelector();
        wrapBox = new javax.swing.JCheckBox();
        fix0CheckBox = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        dataRangeSlider = new pl.edu.icm.visnow.gui.widgets.FloatSubRangeSlider.ExtendedFloatSubRangeSlider();
        symRangeSlider = new pl.edu.icm.visnow.gui.widgets.ExtendedSlider();
        colorEditor = new pl.edu.icm.visnow.gui.widgets.ColorEditor();

        setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });
        setLayout(new java.awt.GridBagLayout());

        colormapChooser.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(colormapChooser, gridBagConstraints);

        mapVariablePanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        mapVariablePanel.setInheritsPopupMenu(true);
        mapVariablePanel.setLayout(new java.awt.GridBagLayout());

        colorCompSelector.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                colorCompSelectorStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        mapVariablePanel.add(colorCompSelector, gridBagConstraints);

        wrapBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        wrapBox.setText("wrap data");
        wrapBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wrapBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        mapVariablePanel.add(wrapBox, gridBagConstraints);

        fix0CheckBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        fix0CheckBox.setText("symmetrical");
        fix0CheckBox.setToolTipText("symmetrical range");
        fix0CheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fix0CheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 23, 0, 0);
        mapVariablePanel.add(fix0CheckBox, gridBagConstraints);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "mapped range", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        jPanel1.setLayout(new java.awt.GridBagLayout());

        dataRangeSlider.setName(""); // NOI18N
        dataRangeSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                dataRangeSliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(dataRangeSlider, gridBagConstraints);

        symRangeSlider.setShowingFields(false);
        symRangeSlider.setMax(1.0F);
        symRangeSlider.setMin(0.0F);
        symRangeSlider.setVal(0.5F);
        symRangeSlider.setVisible(false);
        symRangeSlider.addUserActionListener(new pl.edu.icm.visnow.gui.swingwrappers.UserActionListener() {
            public void valueChangedAction(java.util.EventObject evt) {
                symRangeSliderValueChangedAction(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(symRangeSlider, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        mapVariablePanel.add(jPanel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        add(mapVariablePanel, gridBagConstraints);

        colorEditor.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                colorEditorStateChanged(evt);
            }
        });

        javax.swing.GroupLayout colorEditorLayout = new javax.swing.GroupLayout(colorEditor);
        colorEditor.setLayout(colorEditorLayout);
        colorEditorLayout.setHorizontalGroup(
            colorEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 454, Short.MAX_VALUE)
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
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        add(colorEditor, gridBagConstraints);

        getAccessibleContext().setAccessibleName("");
    }// </editor-fold>//GEN-END:initComponents

   private void colorCompSelectorStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_colorCompSelectorStateChanged
   {//GEN-HEADEREND:event_colorCompSelectorStateChanged
       if(silent)
           return;
       
       updateComponent();
   }//GEN-LAST:event_colorCompSelectorStateChanged

   private void dataRangeSliderStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_dataRangeSliderStateChanged
   {//GEN-HEADEREND:event_dataRangeSliderStateChanged
       if(silent)
           return;
       if (mapParams == null)
           return;
       if (!continousUpdate && dataRangeSlider.isAdjusting())
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
       setRangeSliderType(fix0CheckBox.isSelected());
   }//GEN-LAST:event_fix0CheckBoxActionPerformed

   //indicates if current range slider is symmetric one
   //this flag is necessary to use setRangeSliderType from fix0CheckBox action and from setPresentation method
   private boolean symmetricalSliderVisible = false;
   
   /**
    * Switch slider between sub-range and symmetrical (0 - fixed) and updates its range.
    * 
    * @param symmetrical if true then 0-fixed slider is set, sub-range slider otherwise
     */
    private void setRangeSliderType(boolean symmetrical) {
        if (symmetricalSliderVisible != symmetrical) {
            symmetricalSliderVisible = symmetrical;
            dataRangeSlider.setVisible(!symmetrical);
            symRangeSlider.setVisible(symmetrical);

            if (symmetrical) {
                symRangeSlider.setAll(0,
                        Math.max(Math.abs(dataRangeSlider.getMax()), Math.abs(dataRangeSlider.getMin())),
                        Math.max(Math.abs(dataRangeSlider.getLow()), Math.abs(dataRangeSlider.getUp())));
                float v = symRangeSlider.getVal();
                mapParams.setDataMinMax(min + d * (-v - physMin), min + d * (v - physMin));
                params.getColormapLegendParameters().setColormapLow(-v);
                params.getColormapLegendParameters().setColormapUp(v);
            } else {
                dataRangeSlider.setMin(-symRangeSlider.getMax());
                dataRangeSlider.setMax(symRangeSlider.getMax());
                dataRangeSlider.setLow(-symRangeSlider.getVal());
                dataRangeSlider.setUp(symRangeSlider.getVal());
            }
        }
    }
   
   private void colorEditorStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_colorEditorStateChanged
   {//GEN-HEADEREND:event_colorEditorStateChanged
       if (renderParams == null)
           return;
       renderParams.setDiffuseColor(new Color3f(colorEditor.getColorComponents()));
       renderParams.setAmbientColor(new Color3f(colorEditor.getColorComponents()));
   }//GEN-LAST:event_colorEditorStateChanged

    private void symRangeSliderValueChangedAction(java.util.EventObject evt) {//GEN-FIRST:event_symRangeSliderValueChangedAction
        if (mapParams == null || symRangeSlider.getVal() < 1e-5f)
            return;
        float v = d * symRangeSlider.getVal();
        mapParams.setDataMinMax(-v, v);
        params.getColormapLegendParameters().setColormapLow(-symRangeSlider.getVal());
        params.getColormapLegendParameters().setColormapUp(symRangeSlider.getVal());

    }//GEN-LAST:event_symRangeSliderValueChangedAction

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        //fix to avoid "jumping" of the whole panel (by default symRangeSlider is smaller than dataRangeslider)
        symRangeSlider.setPreferredSize(dataRangeSlider.getSize());
    }//GEN-LAST:event_formComponentResized

    
    public void updateComponent() {
        if (mapParams == null)
            return;
        mapParams.setActive(false);
        int selectedComponent = colorCompSelector.getComponent();
        mapParams.setDataComponent(selectedComponent);
        if (selectedComponent >= 0) {
            colorEditor.setVisible(false);
            colorEditor.setEnabled(false);
            DataArraySchema s = schema.getSchema(selectedComponent);
            updateDataRanges(s);
            colormapChooser.setVisible(true);
            dataRangeSlider.setEnabled(true);
            wrapBox.setEnabled(true);
            fix0CheckBox.setEnabled(true);
            params.getColormapLegendParameters().setTexts(s.getName(), s.getUnit());
        } else {
            colorEditor.setVisible(true);
            colorEditor.setEnabled(true);
            colormapChooser.setVisible(false);
            dataRangeSlider.setEnabled(false);
            wrapBox.setEnabled(false);
            fix0CheckBox.setEnabled(false);
        }
        mapParams.setActive(true);
        mapParams.fireStateChanged();
    }

    public void setColorMap(int map) {
        colormapChooser.setSelectedIndex(map);
        params.getColorMap0Params().setMapType(map);
    }

    private void updateParams() {
        mapParams.setActive(false);
        //mapParams.setDataComponent(colorCompSelector.getComponent());
        //mapParams.setDataMinMax(min + d * (dataRangeSlider.getLow() - physMin), min + d * (dataRangeSlider.getUp() - physMin));
        mapParams.setDataComponent(colorCompSelector.getComponent(), min + d * (dataRangeSlider.getLow() - physMin), min + d * (dataRangeSlider.getUp() - physMin));
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
        if (e == null)
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
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel mapVariablePanel;
    private pl.edu.icm.visnow.gui.widgets.ExtendedSlider symRangeSlider;
    private javax.swing.JCheckBox wrapBox;
    // End of variables declaration//GEN-END:variables

    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        final ComponentColormappingPanel p = new ComponentColormappingPanel();
        f.add(p);
        f.pack();
        f.addComponentListener(new ComponentAdapter() {
            private boolean toggleSimple = true;

            @Override
            public void componentMoved(ComponentEvent e) {
                p.setPresentation(toggleSimple);
                toggleSimple = !toggleSimple;
            }
        });

        f.setVisible(true);
    }

    public void updateGUI() {
        silent = true;
        colorCompSelector.setComponent(params.getColorMap0Params().getDataComponent());
        dataRangeSlider.setLow(params.getColorMap0Params().getDataMin());
        dataRangeSlider.setUp(params.getColorMap0Params().getDataMax());
        silent = false;
    }
}
