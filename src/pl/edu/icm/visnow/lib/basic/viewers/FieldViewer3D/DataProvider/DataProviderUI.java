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

package pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.DataProvider;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pl.edu.icm.visnow.datamaps.ColorMapManager;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.ViewPanels.OrthosliceNumberChangedEvent;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;
import pl.edu.icm.visnow.system.main.VisNow;

/**
 *
 * @author Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class DataProviderUI extends javax.swing.JPanel implements DataProviderParamsListener {

    private RegularField field = null;
    private DataProvider dataProvider = null;
    private DataProviderParams dataProviderParams = null;
    private boolean silent = false;
    private boolean simple = (VisNow.guiLevel == VisNow.SIMPLE_GUI);

    /**
     * Creates new form DataProviderUI
     */
    public DataProviderUI() {
        initComponents();
        componentColormappingPanel1.setContinousUpdate(false);
        componentColormappingPanel1.setNull(false);
        //componentColormappingPanel1.setColorMap(ColorMapManager.COLORMAP1D_GRAY);
        
        redComponentSelector.setScalarComponentsOnly(true);
        redComponentSelector.setTitle("red");
        redComponentSelector.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent evt) {
                if (!silent) {
                    dataProviderParams.setRgbComponent(0, redComponentSelector.getComponent());
                }
            }
        });
        greenComponentSelector.setScalarComponentsOnly(true);
        greenComponentSelector.setTitle("green");
        greenComponentSelector.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent evt) {
                if (!silent) {
                    dataProviderParams.setRgbComponent(1, greenComponentSelector.getComponent());
                }
            }
        });
        blueComponentSelector.setScalarComponentsOnly(true);
        blueComponentSelector.setTitle("blue");
        blueComponentSelector.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent evt) {
                if (!silent) {
                    dataProviderParams.setRgbComponent(2, blueComponentSelector.getComponent());
                }
            }
        });
        
        fastSingleComponentSelector.setScalarComponentsOnly(true);
        fastSingleComponentSelector.setTitle("data component");
        fastSingleComponentSelector.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent evt) {
                if (!silent) {
                    if (field == null) {
                        return;
                    }
                    dataProviderParams.setSingleComponent(fastSingleComponentSelector.getComponent());
                }
            }
        });


        overlayComponentSelector.setScalarComponentsOnly(true);
        overlayComponentSelector.setTitle("overlay component");
        overlayComponentSelector.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent evt) {
                if (!silent) {
                    if (field == null) {
                        return;
                    }
                    silent = true;
                    int c = overlayComponentSelector.getComponent();
                    overlayRange.setData(field.getData(c));
                    overlayRange.setLowUp(field.getData(c).getMinv(), field.getData(c).getMaxv());
                    silent = false;
                    dataProviderParams.setSimpleOverlayLowUp(field.getData(c).getMinv(), field.getData(c).getMaxv());
                    dataProviderParams.setSimpleOverlayComponent(c);

                }
            }
        });


    }

    public void updateGUI() {
        SwingInstancer.swingRun(new Runnable() {

            @Override
            public void run() {
                silent = true;

                overlayPanel.setVisible(!simple);
                int tmp = dataPane.getSelectedIndex();
                dataPane.removeAll();
                dataPane.addTab("Colormapped", singlePanel);
                if(!simple) {
                    dataPane.addTab("RGB", rgbPanel);
                    dataPane.addTab("Fast", fastPanel); 
                    dataPane.setSelectedIndex(tmp);
                }
                dataPane.setSelectedIndex(0);

                if (dataProviderParams == null || field == null) {
                    componentColormappingPanel1.setEnabled(false);
                    componentColormappingPanel1.setData(null);
                    
                    fastSingleComponentSelector.setDataSchema(null);
                    fastSingleComponentSelector.setEnabled(false);
                    redComponentSelector.setEnabled(false);
                    redComponentSelector.setDataSchema(null);
                    rSlider.setEnabled(false);
                    greenComponentSelector.setEnabled(false);
                    greenComponentSelector.setDataSchema(null);
                    gSlider.setEnabled(false);
                    blueComponentSelector.setEnabled(false);
                    blueComponentSelector.setDataSchema(null);
                    bSlider.setEnabled(false);
                    overlaySlider.setEnabled(false);
                    overlayRange.setEmpty(true);
                    overlayRange.setEnabled(false);
                    overlayComponentSelector.setEnabled(false);
                    invertOverlayCB.setEnabled(false);
                    maskOverlayCB.setEnabled(false);
                    dataOverlayCB.setEnabled(false);
                    overlayColorEditor.setEnabled(false);
                    zSlider.setEnabled(false);
                    zField.setEnabled(false);
                    ySlider.setEnabled(false);
                    yField.setEnabled(false);
                    xSlider.setEnabled(false);
                    xField.setEnabled(false);
                    silent = false;
                    return;
                }

                componentColormappingPanel1.setEnabled(true);
                fastSingleComponentSelector.setEnabled(true);
                redComponentSelector.setEnabled(true);
                rSlider.setEnabled(true);
                gSlider.setEnabled(true);
                bSlider.setEnabled(true);
                greenComponentSelector.setEnabled(true);
                blueComponentSelector.setEnabled(true);
                overlaySlider.setEnabled(true);
                if (field.getDims().length == 3) {
                    zSlider.setEnabled(true);
                    zField.setEnabled(true);
                    ySlider.setEnabled(true);
                    yField.setEnabled(true);
                    xSlider.setEnabled(true);
                    xField.setEnabled(true);
                }

                overlayComponentSelector.setEnabled(dataProviderParams.isSimpleOverlay());
                overlayComponentSelector.setComponent(dataProviderParams.getSimpleOverlayComponent());
                overlayRange.setEnabled(dataProviderParams.isSimpleOverlay());
                dataOverlayCB.setEnabled(true);
                dataOverlayCB.setSelected(dataProviderParams.isSimpleOverlay());
                invertOverlayCB.setEnabled(dataProviderParams.isSimpleOverlay());
                invertOverlayCB.setSelected(dataProviderParams.isSimpleOverlayInvert());
                maskOverlayCB.setEnabled(dataProviderParams.isSimpleOverlay());
                maskOverlayCB.setSelected(dataProviderParams.isSimpleOverlayMask());
                overlayColorEditor.setEnabled(dataProviderParams.isSimpleOverlay());
                overlayColorEditor.setColor(dataProviderParams.getSimpleOverlayColor());

                fastSingleComponentSelector.setComponent(dataProviderParams.getSingleComponent());
                redComponentSelector.setComponent(dataProviderParams.getRgbComponent(0));
                greenComponentSelector.setComponent(dataProviderParams.getRgbComponent(1));
                blueComponentSelector.setComponent(dataProviderParams.getRgbComponent(2));

                rSlider.setValue(dataProviderParams.getRgbComponentWeight(0));
                gSlider.setValue(dataProviderParams.getRgbComponentWeight(1));
                bSlider.setValue(dataProviderParams.getRgbComponentWeight(2));

                if (field.getDims().length == 3) {
                    xSlider.setValue(dataProviderParams.getOrthosliceNumber(0));
                    ySlider.setValue(dataProviderParams.getOrthosliceNumber(1));
                    zSlider.setValue(dataProviderParams.getOrthosliceNumber(2));
                }

                switch (dataProviderParams.getMappingMode()) {
                    case DataProviderParams.MAPPING_MODE_COLORMAPPED:
                        dataPane.setSelectedIndex(0);
                        break;
                    case DataProviderParams.MAPPING_MODE_RGB:
                        dataPane.setSelectedIndex(1);
                        break;
                    case DataProviderParams.MAPPING_MODE_FAST:
                        dataPane.setSelectedIndex(2);
                        break;
                }

                overlayRange.setData(field.getData(dataProviderParams.getSimpleOverlayComponent()));
                overlayRange.setLowUp(dataProviderParams.getSimpleOverlayLow(), dataProviderParams.getSimpleOverlayUp());

                overlaySlider.setValue((int) (overlaySlider.getMaximum() * dataProviderParams.getOverlayOpacity()));
                silent = false;

            }
        });
    }

    public void setInfield(final RegularField inField) {
        SwingInstancer.swingRun(new Runnable() {

            @Override
            public void run() {
                silent = true;
                DataProviderUI.this.field = inField;
                dataProviderParams.getDataMappingParams().setInField(inField);
                
                if (inField == null) {
                    updateGUI();
                    return;
                }

                int[] dims = inField.getDims();
                if (dims == null || (dims.length != 3 && dims.length != 2)) {
                    DataProviderUI.this.field = null;
                    updateGUI();
                    return;
                }

                redComponentSelector.setDataSchema(inField.getSchema());
                greenComponentSelector.setDataSchema(inField.getSchema());
                blueComponentSelector.setDataSchema(inField.getSchema());
                componentColormappingPanel1.setData(inField.getSchema());
                fastSingleComponentSelector.setDataSchema(inField.getSchema());
                overlayComponentSelector.setDataSchema(inField.getSchema());

                if (dims.length == 3) {
                    int xMajorTick;
                    if (dims[0] < 20) {
                        xMajorTick = 5;
                    } else if (dims[0] < 50) {
                        xMajorTick = 10;
                    } else if (dims[0] < 100) {
                        xMajorTick = 20;
                    } else {
                        xMajorTick = 50;
                    }
                    xSlider.setMaximum(dims[0] - 1);
                    xSlider.setLabelTable(xSlider.createStandardLabels(xMajorTick));
                    xSlider.setValue(dims[0] / 2);

                    int yMajorTick;
                    if (dims[1] < 20) {
                        yMajorTick = 5;
                    } else if (dims[1] < 50) {
                        yMajorTick = 10;
                    } else if (dims[1] < 100) {
                        yMajorTick = 20;
                    } else {
                        yMajorTick = 50;
                    }
                    ySlider.setMaximum(dims[1] - 1);
                    ySlider.setLabelTable(ySlider.createStandardLabels(yMajorTick));
                    ySlider.setValue(dims[1] / 2);
                    ySlider.repaint();

                    int zMajorTick;
                    if (dims[2] < 20) {
                        zMajorTick = 5;
                    } else if (dims[2] < 50) {
                        zMajorTick = 10;
                    } else if (dims[2] < 100) {
                        zMajorTick = 20;
                    } else {
                        zMajorTick = 50;
                    }
                    zSlider.setMaximum(dims[2] - 1);
                    zSlider.setLabelTable(zSlider.createStandardLabels(zMajorTick));
                    zSlider.setValue(dims[2] / 2);
                    zSlider.repaint();
                }

                //lowUpMappingPanel.reset();
                overlayRange.reset();

                updateGUI();
                silent = false;

            }
        });
    }

    public void setDataProvider(DataProvider dp) {
        dataProvider = dp;
        dataProviderParams = dataProvider.getParams();
        componentColormappingPanel1.setParams(dataProviderParams.getDataMappingParams());     
        dataProviderParams.addDataProviderParamsListener(this);
        componentColormappingPanel1.setColorMap(ColorMapManager.COLORMAP1D_GRAY);
        updateGUI();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        topPanel = new javax.swing.JPanel();
        dataPane = new javax.swing.JTabbedPane();
        singlePanel = new javax.swing.JPanel();
        componentColormappingPanel1 = new pl.edu.icm.visnow.geometries.gui.ComponentColormappingPanel();
        rgbPanel = new javax.swing.JPanel();
        gPanel = new javax.swing.JPanel();
        greenComponentSelector = new pl.edu.icm.visnow.lib.gui.DataComponentSelector();
        gSlider = new javax.swing.JSlider();
        rPanel = new javax.swing.JPanel();
        redComponentSelector = new pl.edu.icm.visnow.lib.gui.DataComponentSelector();
        rSlider = new javax.swing.JSlider();
        bPanel = new javax.swing.JPanel();
        blueComponentSelector = new pl.edu.icm.visnow.lib.gui.DataComponentSelector();
        bSlider = new javax.swing.JSlider();
        fastPanel = new javax.swing.JPanel();
        fastSingleComponentSelector = new pl.edu.icm.visnow.lib.gui.DataComponentSelector();
        overlayPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        overlaySlider = new javax.swing.JSlider();
        jPanel2 = new javax.swing.JPanel();
        dataOverlayCB = new javax.swing.JCheckBox();
        overlayComponentSelector = new pl.edu.icm.visnow.lib.gui.DataComponentSelector();
        overlayRange = new pl.edu.icm.visnow.lib.gui.LowUpMappingUI();
        invertOverlayCB = new javax.swing.JCheckBox();
        maskOverlayCB = new javax.swing.JCheckBox();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        overlayColorEditor = new pl.edu.icm.visnow.gui.widgets.ColorEditor();
        xPanel = new javax.swing.JPanel();
        xSlider = new javax.swing.JSlider();
        xField = new javax.swing.JTextField();
        yPanel = new javax.swing.JPanel();
        ySlider = new javax.swing.JSlider();
        yField = new javax.swing.JTextField();
        zPanel = new javax.swing.JPanel();
        zSlider = new javax.swing.JSlider();
        zField = new javax.swing.JTextField();

        setMinimumSize(new java.awt.Dimension(200, 700));
        setPreferredSize(new java.awt.Dimension(200, 700));
        setLayout(new java.awt.GridBagLayout());

        topPanel.setLayout(new java.awt.BorderLayout());

        dataPane.setMinimumSize(new java.awt.Dimension(200, 260));
        dataPane.setPreferredSize(new java.awt.Dimension(200, 250));
        dataPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                dataPaneStateChanged(evt);
            }
        });

        singlePanel.setLayout(new java.awt.GridBagLayout());

        componentColormappingPanel1.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        singlePanel.add(componentColormappingPanel1, gridBagConstraints);

        dataPane.addTab("Colormapped", singlePanel);

        rgbPanel.setLayout(new java.awt.GridBagLayout());

        gPanel.setMinimumSize(new java.awt.Dimension(200, 40));
        gPanel.setLayout(new java.awt.GridLayout(1, 0));

        greenComponentSelector.setMinimumSize(new java.awt.Dimension(100, 35));
        greenComponentSelector.setPreferredSize(new java.awt.Dimension(200, 45));
        gPanel.add(greenComponentSelector);

        gSlider.setMinorTickSpacing(5);
        gSlider.setValue(100);
        gSlider.setMinimumSize(new java.awt.Dimension(60, 20));
        gSlider.setPreferredSize(new java.awt.Dimension(90, 20));
        gSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                gSliderStateChanged(evt);
            }
        });
        gPanel.add(gSlider);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        rgbPanel.add(gPanel, gridBagConstraints);

        rPanel.setMinimumSize(new java.awt.Dimension(200, 40));
        rPanel.setLayout(new java.awt.GridLayout(1, 0));

        redComponentSelector.setMinimumSize(new java.awt.Dimension(100, 35));
        redComponentSelector.setPreferredSize(new java.awt.Dimension(200, 45));
        redComponentSelector.setRequestFocusEnabled(false);
        rPanel.add(redComponentSelector);

        rSlider.setMinorTickSpacing(5);
        rSlider.setValue(100);
        rSlider.setMinimumSize(new java.awt.Dimension(60, 20));
        rSlider.setPreferredSize(new java.awt.Dimension(90, 20));
        rSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                rSliderStateChanged(evt);
            }
        });
        rPanel.add(rSlider);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        rgbPanel.add(rPanel, gridBagConstraints);

        bPanel.setMinimumSize(new java.awt.Dimension(200, 40));
        bPanel.setLayout(new java.awt.GridLayout(1, 0));

        blueComponentSelector.setMinimumSize(new java.awt.Dimension(100, 35));
        blueComponentSelector.setPreferredSize(new java.awt.Dimension(200, 45));
        bPanel.add(blueComponentSelector);

        bSlider.setMinorTickSpacing(5);
        bSlider.setValue(100);
        bSlider.setMinimumSize(new java.awt.Dimension(60, 20));
        bSlider.setPreferredSize(new java.awt.Dimension(90, 20));
        bSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                bSliderStateChanged(evt);
            }
        });
        bPanel.add(bSlider);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        rgbPanel.add(bPanel, gridBagConstraints);

        dataPane.addTab("RGB", rgbPanel);

        fastPanel.setLayout(new java.awt.GridBagLayout());

        fastSingleComponentSelector.setPreferredSize(new java.awt.Dimension(200, 45));
        fastSingleComponentSelector.setScalarComponentsOnly(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        fastPanel.add(fastSingleComponentSelector, gridBagConstraints);

        dataPane.addTab("Fast", fastPanel);

        topPanel.add(dataPane, java.awt.BorderLayout.CENTER);
        dataPane.getAccessibleContext().setAccessibleName("RGB");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        add(topPanel, gridBagConstraints);

        overlayPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        overlayPanel.setMinimumSize(new java.awt.Dimension(150, 270));
        overlayPanel.setPreferredSize(new java.awt.Dimension(200, 270));
        overlayPanel.setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jLabel1.setText("overlay opacity");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        overlayPanel.add(jLabel1, gridBagConstraints);

        overlaySlider.setMaximumSize(new java.awt.Dimension(32767, 20));
        overlaySlider.setMinimumSize(new java.awt.Dimension(100, 20));
        overlaySlider.setPreferredSize(new java.awt.Dimension(200, 20));
        overlaySlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                overlaySliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        overlayPanel.add(overlaySlider, gridBagConstraints);

        jPanel2.setMinimumSize(new java.awt.Dimension(200, 220));
        jPanel2.setPreferredSize(new java.awt.Dimension(200, 220));
        jPanel2.setLayout(new java.awt.GridBagLayout());

        dataOverlayCB.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        dataOverlayCB.setText("data threshold overlay");
        dataOverlayCB.setMinimumSize(new java.awt.Dimension(98, 24));
        dataOverlayCB.setPreferredSize(new java.awt.Dimension(98, 24));
        dataOverlayCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dataOverlayCBActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        jPanel2.add(dataOverlayCB, gridBagConstraints);

        overlayComponentSelector.setEnabled(false);
        overlayComponentSelector.setMinimumSize(new java.awt.Dimension(150, 45));
        overlayComponentSelector.setPreferredSize(new java.awt.Dimension(200, 45));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        jPanel2.add(overlayComponentSelector, gridBagConstraints);

        overlayRange.setEnabled(false);
        overlayRange.setTitle("threshold range");
        overlayRange.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                overlayRangeStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        jPanel2.add(overlayRange, gridBagConstraints);

        invertOverlayCB.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        invertOverlayCB.setText("invert");
        invertOverlayCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                invertOverlayCBActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        jPanel2.add(invertOverlayCB, gridBagConstraints);

        maskOverlayCB.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        maskOverlayCB.setText("mask data");
        maskOverlayCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maskOverlayCBActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel2.add(maskOverlayCB, gridBagConstraints);

        jPanel3.setLayout(new java.awt.GridBagLayout());

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jLabel2.setText("Overlay color:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        jPanel3.add(jLabel2, gridBagConstraints);

        overlayColorEditor.setEnabled(false);
        overlayColorEditor.setTitle("");
        overlayColorEditor.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                overlayColorEditorStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 2, 5);
        jPanel3.add(overlayColorEditor, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel2.add(jPanel3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        overlayPanel.add(jPanel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        add(overlayPanel, gridBagConstraints);

        xPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "i slice", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 8))); // NOI18N
        xPanel.setLayout(new java.awt.GridBagLayout());

        xSlider.setFont(new java.awt.Font("Dialog", 0, 8)); // NOI18N
        xSlider.setMajorTickSpacing(20);
        xSlider.setMinorTickSpacing(1);
        xSlider.setPaintLabels(true);
        xSlider.setPaintTicks(true);
        xSlider.setMinimumSize(new java.awt.Dimension(120, 40));
        xSlider.setPreferredSize(new java.awt.Dimension(200, 42));
        xSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                xSliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        xPanel.add(xSlider, gridBagConstraints);

        xField.setText("0");
        xField.setMinimumSize(new java.awt.Dimension(54, 19));
        xField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                xFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        xPanel.add(xField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        add(xPanel, gridBagConstraints);

        yPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "j slice", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 8))); // NOI18N
        yPanel.setLayout(new java.awt.GridBagLayout());

        ySlider.setFont(new java.awt.Font("Dialog", 0, 8)); // NOI18N
        ySlider.setMajorTickSpacing(20);
        ySlider.setMinorTickSpacing(1);
        ySlider.setPaintLabels(true);
        ySlider.setPaintTicks(true);
        ySlider.setMinimumSize(new java.awt.Dimension(120, 40));
        ySlider.setPreferredSize(new java.awt.Dimension(200, 42));
        ySlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                ySliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        yPanel.add(ySlider, gridBagConstraints);

        yField.setText("0");
        yField.setMinimumSize(new java.awt.Dimension(54, 19));
        yField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                yFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        yPanel.add(yField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        add(yPanel, gridBagConstraints);

        zPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "k slice", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 8))); // NOI18N
        zPanel.setLayout(new java.awt.GridBagLayout());

        zSlider.setFont(new java.awt.Font("Dialog", 0, 8)); // NOI18N
        zSlider.setMajorTickSpacing(20);
        zSlider.setMinorTickSpacing(1);
        zSlider.setPaintLabels(true);
        zSlider.setPaintTicks(true);
        zSlider.setMinimumSize(new java.awt.Dimension(120, 40));
        zSlider.setPreferredSize(new java.awt.Dimension(200, 42));
        zSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                zSliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        zPanel.add(zSlider, gridBagConstraints);

        zField.setText("0");
        zField.setMinimumSize(new java.awt.Dimension(54, 19));
        zField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        zPanel.add(zField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(zPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

   private void bSliderStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_bSliderStateChanged
   {//GEN-HEADEREND:event_bSliderStateChanged
       if (!silent) {
           dataProviderParams.setRgbComponentWeight(2, bSlider.getValue());
       }
   }//GEN-LAST:event_bSliderStateChanged

   private void gSliderStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_gSliderStateChanged
   {//GEN-HEADEREND:event_gSliderStateChanged
       if (!silent) {
           dataProviderParams.setRgbComponentWeight(1, gSlider.getValue());
       }
   }//GEN-LAST:event_gSliderStateChanged

   private void rSliderStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_rSliderStateChanged
   {//GEN-HEADEREND:event_rSliderStateChanged
       if (!silent) {
           dataProviderParams.setRgbComponentWeight(0, rSlider.getValue());
       }
   }//GEN-LAST:event_rSliderStateChanged

private void zSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_zSliderStateChanged
    zField.setText("" + zSlider.getValue());
    if (!silent) {
        dataProviderParams.setOrthosliceNumber(2, zSlider.getValue());
    }

}//GEN-LAST:event_zSliderStateChanged

private void zFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zFieldActionPerformed
    try {
        zSlider.setValue(Integer.parseInt(zField.getText()));
    } catch (Exception e) {
        zField.setText("" + zSlider.getValue());
    }
}//GEN-LAST:event_zFieldActionPerformed

private void ySliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ySliderStateChanged
    yField.setText("" + ySlider.getValue());
    if (!silent) {
        dataProviderParams.setOrthosliceNumber(1, ySlider.getValue());
    }

}//GEN-LAST:event_ySliderStateChanged

private void yFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_yFieldActionPerformed
    try {
        ySlider.setValue(Integer.parseInt(yField.getText()));
    } catch (Exception e) {
        yField.setText("" + ySlider.getValue());
    }
}//GEN-LAST:event_yFieldActionPerformed

private void xSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_xSliderStateChanged
    xField.setText("" + xSlider.getValue());
    if (!silent) {
        dataProviderParams.setOrthosliceNumber(0, xSlider.getValue());
    }

}//GEN-LAST:event_xSliderStateChanged

private void xFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_xFieldActionPerformed
    try {
        xSlider.setValue(Integer.parseInt(xField.getText()));
    } catch (Exception e) {
        xField.setText("" + xSlider.getValue());
    }
}//GEN-LAST:event_xFieldActionPerformed

private void overlaySliderStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_overlaySliderStateChanged
{//GEN-HEADEREND:event_overlaySliderStateChanged
    if (dataProviderParams == null) {
        return;
    }

    dataProviderParams.setOverlayOpacity((float) overlaySlider.getValue() / (float) overlaySlider.getMaximum());
}//GEN-LAST:event_overlaySliderStateChanged

private void dataPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_dataPaneStateChanged
    if (dataProviderParams == null || silent) {
        return;
    }

    switch (dataPane.getSelectedIndex()) {
        case 0:
            dataProviderParams.setMappingMode(DataProviderParams.MAPPING_MODE_COLORMAPPED);
            break;
        case 1:
            dataProviderParams.setMappingMode(DataProviderParams.MAPPING_MODE_RGB);
            break;
        case 2:
            dataProviderParams.setMappingMode(DataProviderParams.MAPPING_MODE_FAST);
            break;
    }

}//GEN-LAST:event_dataPaneStateChanged

    private void dataOverlayCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dataOverlayCBActionPerformed
        dataProviderParams.setSimpleOverlay(dataOverlayCB.isSelected());
    }//GEN-LAST:event_dataOverlayCBActionPerformed

    private void overlayRangeStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_overlayRangeStateChanged
        if (!silent) {
            dataProviderParams.setSimpleOverlayLowUp(overlayRange.getLow(), overlayRange.getUp());
        }
    }//GEN-LAST:event_overlayRangeStateChanged

    private void invertOverlayCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_invertOverlayCBActionPerformed
        dataProviderParams.setSimpleOverlayInvert(invertOverlayCB.isSelected());
    }//GEN-LAST:event_invertOverlayCBActionPerformed

    private void maskOverlayCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maskOverlayCBActionPerformed
        dataProviderParams.setSimpleOverlayMask(maskOverlayCB.isSelected());
        if (maskOverlayCB.isSelected()) {
            overlaySlider.setValue(overlaySlider.getMaximum());
        }
    }//GEN-LAST:event_maskOverlayCBActionPerformed

    private void overlayColorEditorStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_overlayColorEditorStateChanged
        if (!silent) {
            dataProviderParams.setSimpleOverlayColor(overlayColorEditor.getColor());
        }
    }//GEN-LAST:event_overlayColorEditorStateChanged
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bPanel;
    private javax.swing.JSlider bSlider;
    private pl.edu.icm.visnow.lib.gui.DataComponentSelector blueComponentSelector;
    private pl.edu.icm.visnow.geometries.gui.ComponentColormappingPanel componentColormappingPanel1;
    private javax.swing.JCheckBox dataOverlayCB;
    private javax.swing.JTabbedPane dataPane;
    private javax.swing.JPanel fastPanel;
    private pl.edu.icm.visnow.lib.gui.DataComponentSelector fastSingleComponentSelector;
    private javax.swing.JPanel gPanel;
    private javax.swing.JSlider gSlider;
    private pl.edu.icm.visnow.lib.gui.DataComponentSelector greenComponentSelector;
    private javax.swing.JCheckBox invertOverlayCB;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JCheckBox maskOverlayCB;
    private pl.edu.icm.visnow.gui.widgets.ColorEditor overlayColorEditor;
    private pl.edu.icm.visnow.lib.gui.DataComponentSelector overlayComponentSelector;
    private javax.swing.JPanel overlayPanel;
    private pl.edu.icm.visnow.lib.gui.LowUpMappingUI overlayRange;
    private javax.swing.JSlider overlaySlider;
    private javax.swing.JPanel rPanel;
    private javax.swing.JSlider rSlider;
    private pl.edu.icm.visnow.lib.gui.DataComponentSelector redComponentSelector;
    private javax.swing.JPanel rgbPanel;
    private javax.swing.JPanel singlePanel;
    private javax.swing.JPanel topPanel;
    private javax.swing.JTextField xField;
    private javax.swing.JPanel xPanel;
    private javax.swing.JSlider xSlider;
    private javax.swing.JTextField yField;
    private javax.swing.JPanel yPanel;
    private javax.swing.JSlider ySlider;
    private javax.swing.JTextField zField;
    private javax.swing.JPanel zPanel;
    private javax.swing.JSlider zSlider;
    // End of variables declaration//GEN-END:variables

    @Override
    public void onOrthosliceNumberChanged(OrthosliceNumberChangedEvent evt) {
        updateGUI();
    }

    @Override
    public void onRgbComponentChanged(RgbComponentChangedEvent evt) {
        updateGUI();
    }

    @Override
    public void onColormapChanged(ColormapChangedEvent evt) {
        updateGUI();
    }

    @Override
    public void onRgbComponentWeightChanged(RgbComponentWeightChangedEvent evt) {
    }

    @Override
    public void onCustomPlaneChanged(CustomPlaneChangedEvent evt) {
    }

    @Override
    public void onIsolineThresholdChanged(IsolineThresholdChangedEvent evt) {
    }

    @Override
    public void onOverlayOpacityChanged(DataProviderParamsEvent evt) {
    }

    @Override
    public void onOverlayChanged(DataProviderParamsEvent evt) {
        updateGUI();
    }

    @Override
    public void onCustomOrthoPlaneChanged(CustomOrthoPlaneChangedEvent evt) {
    }

    public void setSimpleGUI(boolean simpleGUI) {
        this.simple = simpleGUI;
        updateGUI();
    }
}
