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

import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeListener;
import org.apache.log4j.Logger;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.geometries.objects.SignalingTransform3D;
import pl.edu.icm.visnow.geometries.parameters.AbstractRenderingParams;
import pl.edu.icm.visnow.geometries.parameters.DataMappingParams;
import pl.edu.icm.visnow.geometries.parameters.RegularFieldDisplayParams;
import pl.edu.icm.visnow.geometries.parameters.RenderingParams;
import pl.edu.icm.visnow.geometries.parameters.TransformParams;
import static java.awt.GridBagConstraints.*;

/**
 *
 * @author Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class RegularFieldPresentationGUI extends javax.swing.JPanel {
    private static final Logger LOGGER = Logger.getLogger(RegularFieldPresentationGUI.class);
    
    RegularField inField = null;
    private int nScalarComps = 0;
    private Vector<String> xtScalarCompNames = new Vector<String>();
    private int[] scalarCompIndices;
    private int nDims = -1;
    private boolean simple = false;
    private RegularFieldDisplayParams params = null;

    /**
     * Creates new form ColoringGUI
     */
    public RegularFieldPresentationGUI() {
        initComponents();
        dataMappingGUI.setStartNullTransparencyComponent(true);
    }

    public void setInField(RegularField inField) {
        if (inField == null) {
            return;
        }
        this.inField = inField;
        nScalarComps = 0;
        xtScalarCompNames.clear();
        scalarCompIndices = new int[inField.getNData()];
        for (int i = 0; i < inField.getNData(); i++) {
            xtScalarCompNames.add(inField.getData(i).getName());
            scalarCompIndices[nScalarComps] = i;
            nScalarComps++;
        }
        xtScalarCompNames.add("x");
        xtScalarCompNames.add("y");
        xtScalarCompNames.add("z");
        xtScalarCompNames.add("normal x");
        xtScalarCompNames.add("normal y");
        xtScalarCompNames.add("normal z");
        if (inField instanceof RegularField) {
            xtScalarCompNames.add("i");
            xtScalarCompNames.add("j");
        }
        if (nScalarComps < 1) {
            return;
        }
        //active = true;        
        if (!fixFlag && params != null) {
            dataMappingGUI.setInData(inField, params.getMappingParams());
            dataMappingGUI.setRenderingParams(params.getDisplayParams());
            displayPropertiesGUI.setRenderingParams(params.getDisplayParams());
            transformGUI.setTransformParams(params.getTransformParams());
            if (inField.getDims().length == 3) {
                displayContentGUI.setParams(params.getContent3DParams());
            }
        }
        
        if (nDims == -1 || nDims != inField.getDims().length) {
            nDims = inField.getDims().length;
            switch (nDims) {
                case 3:
                    if (simple) {
                        GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints(0,0,1,1,1,0,java.awt.GridBagConstraints.FIRST_LINE_START,java.awt.GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0);
                        add(displayContentGUI, gridBagConstraints);
                    } else {
                        displayContentPanel.add(displayContentGUI, java.awt.BorderLayout.NORTH);
                        extendedTabbedPane.insertTab("content", null, displayContentScrollPane, "3d field display content", 0);
                    }
                    displayContentGUI.setVisible(true);
                    break;
                case 2:
                    if (simple) {
                        remove(displayContentGUI);
                    } else {
                        extendedTabbedPane.remove(displayContentScrollPane);
                    }
                    displayContentGUI.setVisible(false);
                    break;
                case 1:
                    if (simple) {
                        remove(displayContentGUI);
                    } else {
                        extendedTabbedPane.remove(displayContentScrollPane);
                    }
                    displayContentGUI.setVisible(false);
                    break;
            }
            displayPropertiesGUI.setNDims(nDims);
        }
    }

    public void setPresentation(boolean simple) {
        Insets insets0 = new Insets(0, 0, 0, 0);
        dataMappingGUI.setPresentation(simple);
        displayPropertiesGUI.setPresentation(simple);
        if (simple) {
            if (nDims == 3) simplePanel.add(displayContentGUI, new GridBagConstraints(0, 0, 1, 1, 1, 0, NORTH, HORIZONTAL, insets0, 0, 0));
            simplePanel.add(dataMappingGUI, new GridBagConstraints(0, 1, 1, 1, 1, 0, NORTH, HORIZONTAL, insets0, 0, 0));
            simplePanel.add(displayPropertiesGUI, new GridBagConstraints(0, 2, 1, 1, 1, 0, NORTH, HORIZONTAL, insets0, 0, 0));

        } else {
            displayContentPanel.add(displayContentGUI, java.awt.BorderLayout.NORTH);
            dataMappingPanel.add(dataMappingGUI, java.awt.BorderLayout.NORTH);
            displayPropertiesPanel.add(displayPropertiesGUI, java.awt.BorderLayout.NORTH);
        }
        ((CardLayout) getLayout()).show(this, simple ? "simpleUI" : "extendedUI");
        revalidate();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        extendedTabbedPane = new javax.swing.JTabbedPane();
        displayContentScrollPane = new javax.swing.JScrollPane();
        displayContentPanel = new javax.swing.JPanel();
        displayContentGUI = new pl.edu.icm.visnow.geometries.gui.RegularField3DMapPanel();
        dataMappingScrollPane = new javax.swing.JScrollPane();
        dataMappingPanel = new javax.swing.JPanel();
        dataMappingGUI = new pl.edu.icm.visnow.geometries.gui.DataMappingGUI();
        displayPropertiesScrollPane = new javax.swing.JScrollPane();
        displayPropertiesPanel = new javax.swing.JPanel();
        displayPropertiesGUI = new pl.edu.icm.visnow.geometries.gui.DisplayPropertiesGUI();
        transformScrollPane = new javax.swing.JScrollPane();
        transformPanel = new javax.swing.JPanel();
        transformGUI = new pl.edu.icm.visnow.geometries.gui.TransformPanel();
        simpleScrollPane = new javax.swing.JScrollPane();
        simplePanel = new javax.swing.JPanel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));

        setRequestFocusEnabled(false);
        setLayout(new java.awt.CardLayout());

        extendedTabbedPane.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        extendedTabbedPane.setToolTipText("");
        extendedTabbedPane.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N

        displayContentScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        displayContentPanel.setLayout(new java.awt.BorderLayout());
        displayContentPanel.add(displayContentGUI, java.awt.BorderLayout.NORTH);

        displayContentScrollPane.setViewportView(displayContentPanel);

        extendedTabbedPane.addTab("content", displayContentScrollPane);

        dataMappingScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        dataMappingPanel.setLayout(new java.awt.BorderLayout());
        dataMappingPanel.add(dataMappingGUI, java.awt.BorderLayout.NORTH);

        dataMappingScrollPane.setViewportView(dataMappingPanel);

        extendedTabbedPane.addTab("datamap", dataMappingScrollPane);

        displayPropertiesScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        displayPropertiesPanel.setLayout(new java.awt.BorderLayout());
        displayPropertiesPanel.add(displayPropertiesGUI, java.awt.BorderLayout.NORTH);

        displayPropertiesScrollPane.setViewportView(displayPropertiesPanel);

        extendedTabbedPane.addTab("display", displayPropertiesScrollPane);

        transformScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        transformPanel.setLayout(new java.awt.BorderLayout());
        transformPanel.add(transformGUI, java.awt.BorderLayout.NORTH);

        transformScrollPane.setViewportView(transformPanel);

        extendedTabbedPane.addTab("transform", transformScrollPane);

        add(extendedTabbedPane, "extendedUI");

        simpleScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        simplePanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 99;
        gridBagConstraints.weighty = 1.0;
        simplePanel.add(filler1, gridBagConstraints);

        simpleScrollPane.setViewportView(simplePanel);

        add(simpleScrollPane, "simpleUI");
    }// </editor-fold>//GEN-END:initComponents

    public RegularField3DMapPanel getRegularField3DMapPanel() {
        return displayContentGUI;
    }
    /**
     * Utility field holding list of ChangeListeners.
     */
    private transient ArrayList<ChangeListener> changeListenerList = new ArrayList<ChangeListener>();

    /**
     * Registers ChangeListener to receive events.
     *
     * @param listener The listener to register.
     */
    public synchronized void addChangeListener(ChangeListener listener) {
        changeListenerList.add(listener);
    }

    /**
     * Removes ChangeListener from the list of listeners.
     *
     * @param listener The listener to remove.
     */
    public synchronized void removeChangeListener(ChangeListener listener) {
        changeListenerList.remove(listener);
    }

    /**
     * Notifies all registered listeners about the event.
     *
     * @param object Parameter #1 of the <CODE>ChangeEvent<CODE> constructor.
     */
    public void setDisplayParams(AbstractRenderingParams displayParams) {
        displayPropertiesGUI.setRenderingParams(displayParams);
    }

    public void setTransforParams(TransformParams params) {
        transformGUI.setTransformParams(params);
    }

    public void setParams(RegularFieldDisplayParams params) {
        this.params = params;
        if (inField != null && params != null) {
            dataMappingGUI.setInData(inField, params.getMappingParams());
            dataMappingGUI.setRenderingParams(params.getDisplayParams());
            displayPropertiesGUI.setRenderingParams(params.getDisplayParams());
            transformGUI.setTransformParams(params.getTransformParams());
            if (inField.getDims().length == 3) {
                displayContentGUI.setParams(params.getContent3DParams());
            }
        }
    }

    private boolean fixFlag = false;
    
    public void setInData(RegularField inField, RegularFieldDisplayParams params) {
        if (inField == null || params == null) {
            return;
        }
    
        //fixFlag = true;
        setInField(inField);
        setParams(params);
        //fixFlag = true;
    }

    public void setSignalingTransform(SignalingTransform3D sigTrans) {
        transformGUI.setSigTrans(sigTrans);
    }

    public void setTransSensitivity(float s) {
        transformGUI.setTransSensitivity(s);
    }
    
    public RenderingParams getRenderingParams()
    {
        return params.getDisplayParams();
    }

    public DataMappingParams getMappingParams() 
    {
        return params.getMappingParams();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private pl.edu.icm.visnow.geometries.gui.DataMappingGUI dataMappingGUI;
    private javax.swing.JPanel dataMappingPanel;
    private javax.swing.JScrollPane dataMappingScrollPane;
    private pl.edu.icm.visnow.geometries.gui.RegularField3DMapPanel displayContentGUI;
    private javax.swing.JPanel displayContentPanel;
    private javax.swing.JScrollPane displayContentScrollPane;
    private pl.edu.icm.visnow.geometries.gui.DisplayPropertiesGUI displayPropertiesGUI;
    private javax.swing.JPanel displayPropertiesPanel;
    private javax.swing.JScrollPane displayPropertiesScrollPane;
    private javax.swing.JTabbedPane extendedTabbedPane;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JPanel simplePanel;
    private javax.swing.JScrollPane simpleScrollPane;
    private pl.edu.icm.visnow.geometries.gui.TransformPanel transformGUI;
    private javax.swing.JPanel transformPanel;
    private javax.swing.JScrollPane transformScrollPane;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the dataMappingGUI
     */
    public pl.edu.icm.visnow.geometries.gui.DataMappingGUI getDataMappingGUI() {
        return dataMappingGUI;
    }
    
    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        final RegularFieldPresentationGUI p = new  RegularFieldPresentationGUI();
        p.nDims = 3;
        f.add(p);
        f.pack();
        f.addComponentListener(new ComponentAdapter() {
            private boolean toggleSimple = true;
            @Override
            public void componentMoved(ComponentEvent e) {
                p.setPresentation(toggleSimple);
                toggleSimple = !toggleSimple;
                LOGGER.debug("");
            }
            
        });
        f.setVisible(true);
    }    

}
