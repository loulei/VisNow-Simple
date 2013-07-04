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

package pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry;

import java.io.*;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.FieldDisplay3DFrame;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.PointsWizard.PointsWizardDialog;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.ToolsActivityWizard.ToolsActivityWizardDialog;
import pl.edu.icm.visnow.system.main.VisNow;

/**
 *
 * @author Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class GeometryUI extends javax.swing.JPanel implements ListSelectionListener {

    protected GeometryParams params = new GeometryParams();
    protected RegularField inField = null;
    protected PointDescriptorTableModel modelPts;
    protected ConnectionDescriptorTableModel modelConn;
    private JFileChooser fileChooser = new JFileChooser();
    private FileNameExtensionFilter geometryFileNameExtensionFilter = new FileNameExtensionFilter("Geometry (*.txt)", "txt", "TXT");
    private FieldDisplay3DFrame frame = null;
    private PointsWizardDialog wizard = null;
    private ToolsActivityWizardDialog wizard2 = null;
    private CalculableParams cparams = null;
    private CalculableParameterTableModel modelCps;
    private boolean simple = (VisNow.guiLevel == VisNow.SIMPLE_GUI);
    private boolean tableSilent = false;

    /**
     * Creates new form GeometryUI
     */
    public GeometryUI() {
        this(null);
    }

    public GeometryUI(FieldDisplay3DFrame frame) {
        initComponents();
        this.frame = frame;
        this.wizard = new PointsWizardDialog(frame, false);
        this.wizard2 = new ToolsActivityWizardDialog(frame, false);
        fileChooser.setCurrentDirectory(new File(VisNow.get().getMainConfig().getAdditionalConfigPath()));
    }

    public void setParams(GeometryParams par, CalculableParams cparams) {
        this.cparams = cparams;
        this.params = par;

        modelPts = new PointDescriptorTableModel(params);
        pointsTable.setModel(modelPts);
        pointsTable.getSelectionModel().addListSelectionListener(this);

        ArrayList<PointDescriptor> pds = params.getPointsDescriptors();
        PointDescriptor[] pdsArr = new PointDescriptor[pds.size()];
        for (int i = 0; i < pdsArr.length; i++) {
            pdsArr[i] = pds.get(i);            
        }
        connectPoint1CB.setModel(new DefaultComboBoxModel(pdsArr));
        connectPoint2CB.setModel(new DefaultComboBoxModel(pdsArr));

        modelConn = new ConnectionDescriptorTableModel(params);
        connectionsTable.setModel(modelConn);
        connectionsTable.getSelectionModel().addListSelectionListener(this);

        modelCps = new CalculableParameterTableModel(cparams);
        cpsTable.setModel(modelCps);
        cpsTable.getSelectionModel().addListSelectionListener(this);
        cpsTable.getColumnModel().getColumn(0).setMinWidth(120);
        cpsTable.getColumnModel().getColumn(0).setPreferredWidth(120);
        cpsTable.getColumnModel().getColumn(1).setMinWidth(80);
        cpsTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        cpsTable.getColumnModel().getColumn(2).setMinWidth(200);
        cpsTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        cpsTable.getColumnModel().getColumn(3).setMinWidth(240);
        cpsTable.getColumnModel().getColumn(3).setPreferredWidth(240);

        cparams.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                updateCalculableGUI();
            }
        });

        params.addGeometryParamsListener(new GeometryParamsListener() {

            @Override
            public void onGeometryParamsChanged(GeometryParamsEvent e) {
                if (e.getType() == GeometryParamsEvent.TYPE_POINT_SELECTION || e.getType() == GeometryParamsEvent.TYPE_ALL) {
                    createCustomPlaneButton.setEnabled(params.getSelectedPoints() != null && params.getSelectedPoints().length == 3);

                    if (params.getSelectedPoints() != null && params.getSelectedPoints().length == 1) {
                        int[] pts = params.getSelectedPoints();
                        if (pts != null && pts.length > 0) {
                            int min = pts[0];
                            int max = pts[0];
                            for (int i = 0; i < pts.length; i++) {
                                if (pts[i] > max) {
                                    max = pts[i];
                                }
                                if (pts[i] < min) {
                                    min = pts[i];
                                }
                            }

                            tableSilent = true;
                            pointsTable.getSelectionModel().setSelectionInterval(min, max);
                            tableSilent = false;
                        }


                    }

                    if (params.getSelectedPoints() == null) {
                        if (pointsTable.getSelectedRowCount() != 0) {
                            tableSilent = true;
                            pointsTable.getSelectionModel().removeSelectionInterval(0, pointsTable.getRowCount() - 1);
                            tableSilent = false;
                        }
                    }

                    slicePositioningCB.setSelected(params.isSlicePositioning());

                    //return;
                }

                updateGUI();
            }
        });

        if (params.isWizard()) {
            wizard.setGeometryParams(params);
            wizard.setCalculableParams(cparams);
            wizard2.setGeometryParams(params);
            wizard2.setCalculableParams(cparams);
        }
        updateGUI();
    }

    private void updateCalculableGUI() {
        if (this.cparams == null) {
            cpsTable.setEnabled(false);
            paintCalculableValues2DCB.setEnabled(false);
            removeCalculableButton.setEnabled(false);
            return;
        }

        cpsTable.setEnabled(true);
        paintCalculableValues2DCB.setEnabled(true);

        updateCalculableTable();
        paintCalculableValues2DCB.setSelected(cparams.isPaintCalculable2D());


    }

    private void updateCalculableTable() {
        cpsTable.tableChanged(new TableModelEvent(modelCps));
    }

    private void exportPointsAndConnections() {
        boolean done = false;
        while (!done) {
            fileChooser.setSelectedFile(new File(fileChooser.getCurrentDirectory() + File.separator + params.getPatientString().replaceAll(" ", "_") + ".txt"));
            fileChooser.setFileFilter(geometryFileNameExtensionFilter);
            int returnVal = fileChooser.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File f = fileChooser.getSelectedFile().getAbsoluteFile();
                if (f.exists()) {
                    int res = JOptionPane.showConfirmDialog(null, "File already exists!\n\nDo you want to replace it?");
                    if (res == JOptionPane.YES_OPTION) {
                        done = true;
                    } else if (res == JOptionPane.CANCEL_OPTION) {
                        return;
                    }
                } else {
                    done = true;
                }
                if (done) {
                    String path = fileChooser.getSelectedFile().getAbsolutePath();
                    exportPointsAndConnections(path);
                }
            } else {
                done = true;
            }
        }
    }

    private void exportPointsAndConnections(String filePath) {
        try {
            File f = new File(filePath);
            BufferedWriter output = new BufferedWriter(new FileWriter(f));

            output.write("points and connections data file");
            output.newLine();
            ArrayList<PointDescriptor> pts = params.getPointsDescriptors();
            ArrayList<ConnectionDescriptor> conn = params.getConnectionDescriptors();
            String tmp = "";
            int[] indices;
            output.append("points " + pts.size());
            output.newLine();
            for (int i = 0; i < pts.size(); i++) {
                indices = pts.get(i).getIndices();
                tmp = "" + pts.get(i).getName() + "\t" + indices[0] + "\t" + indices[1] + "\t" + indices[2];
                output.append(tmp);
                output.newLine();
            }
            output.newLine();

            output.append("connections " + conn.size());
            output.newLine();
            int i1, i2;
            for (int i = 0; i < conn.size(); i++) {
                i1 = pts.indexOf(conn.get(i).getP1());
                i2 = pts.indexOf(conn.get(i).getP2());
                tmp = "" + conn.get(i).getName() + "\t" + i1 + "\t" + i2;
                output.append(tmp);
                output.newLine();
            }
            output.newLine();

            if (params.getInfoString() != null) {
                output.append(params.getInfoString());
                output.newLine();
            }

            output.close();
        } catch (Exception ex) {
        }
    }

    private void importPointsAndConnections() {
        fileChooser.setFileFilter(geometryFileNameExtensionFilter);
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            importPointsAndConnections(path);
        }
    }

    private void importPointsAndConnections(String filePath) {
        try {
            File f = new File(filePath);
            BufferedReader input = new BufferedReader(new FileReader(f));

            String line;

            line = input.readLine();
            if (line == null || !line.equals("points and connections data file")) {
                input.close();
                return;
            }

            line = input.readLine();
            if (line == null || !line.startsWith("points")) {
                input.close();
                return;
            }
            String[] tmp = line.split(" ");
            int nPoints = Integer.parseInt(tmp[1]);

            String[] names = new String[nPoints];
            int[][] pts = new int[nPoints][3];

            for (int i = 0; i < nPoints; i++) {
                line = input.readLine();
                tmp = line.split("\t");
                names[i] = new String(tmp[0]);
                pts[i][0] = Integer.parseInt(tmp[1]);
                pts[i][1] = Integer.parseInt(tmp[2]);
                pts[i][2] = Integer.parseInt(tmp[3]);
            }

            line = input.readLine();
            line = input.readLine();
            if (line == null || !line.startsWith("connections")) {
                input.close();
                return;
            }
            tmp = line.split(" ");
            int nConns = Integer.parseInt(tmp[1]);
            String[] cnames = new String[nConns];
            int[][] conns = new int[nConns][2];
            for (int i = 0; i < nConns; i++) {
                line = input.readLine();
                if (line == null) {
                    input.close();
                    return;
                }
                tmp = line.split("\t");
                cnames[i] = new String(tmp[0]);
                conns[i][0] = Integer.parseInt(tmp[1]);
                conns[i][1] = Integer.parseInt(tmp[2]);
            }
            input.close();

            //params.clearConenctions();
            params.clearPoints();
            params.addPoints(names, pts);
            params.addConnections(cnames, conns);
        } catch (Exception ex) {
        }
    }

    private void updateGUI() {
        showGlyphsCB.setSelected(params.isShowGlyphs());
        paintLabelsCB.setSelected(params.isPaintLabels());
        showConnections2DCB.setSelected(params.isShowConnections2D());
        showDistances2DCB.setSelected(params.isShowDistances2D());
        showConnections3DCB.setSelected(params.isShowConnections3D());
        createCustomPlaneButton.setEnabled(params.getSelectedPoints() != null && params.getSelectedPoints().length == 3);
        undoButton.setEnabled(modelPts.getRowCount() > 0);
        wizardButton.setEnabled(params.isWizard() && params.getInField() != null);
        slicePositioningCB.setSelected(params.isSlicePositioning());

        ArrayList<PointDescriptor> pds = params.getPointsDescriptors();
        PointDescriptor[] pdsArr = new PointDescriptor[pds.size()];
        for (int i = 0; i < pdsArr.length; i++) {
            pdsArr[i] = pds.get(i);            
        }
        connectPoint1CB.setModel(new DefaultComboBoxModel(pdsArr));
        connectPoint2CB.setModel(new DefaultComboBoxModel(pdsArr));

        updateCalculableGUI();
        updateButtons();
        
        customSlicePanel.setVisible(!simple);
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

        mainTabbedPane = new javax.swing.JTabbedPane();
        pointsPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        clearPointsButton = new javax.swing.JButton();
        removePointButton = new javax.swing.JButton();
        addIntersectionButton = new javax.swing.JButton();
        undoButton = new javax.swing.JButton();
        addCalculablePointButton = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        showGlyphsCB = new javax.swing.JCheckBox();
        paintLabelsCB = new javax.swing.JCheckBox();
        glyphScaleSlider = new javax.swing.JSlider();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        pointsTable = new javax.swing.JTable();
        customSlicePanel = new javax.swing.JPanel();
        createCustomPlaneButton = new javax.swing.JButton();
        slicePositioningCB = new javax.swing.JCheckBox();
        connectionsPanel = new javax.swing.JPanel();
        connectPoint1CB = new javax.swing.JComboBox();
        connectPoint2CB = new javax.swing.JComboBox();
        connectButton = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        connectionsTable = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        removeConnectionButton = new javax.swing.JButton();
        showConnections2DCB = new javax.swing.JCheckBox();
        showConnections3DCB = new javax.swing.JCheckBox();
        showDistances2DCB = new javax.swing.JCheckBox();
        jLabel2 = new javax.swing.JLabel();
        tubesScaleSlider = new javax.swing.JSlider();
        calculablesPanel = new javax.swing.JPanel();
        topPanel = new javax.swing.JPanel();
        paintCalculableValues2DCB = new javax.swing.JCheckBox();
        addCalculableButon = new javax.swing.JButton();
        removeCalculableButton = new javax.swing.JButton();
        mainPanel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        cpsTable = new javax.swing.JTable();
        bottomPanel = new javax.swing.JPanel();
        importButton = new javax.swing.JButton();
        exportButton = new javax.swing.JButton();
        wizardButton = new javax.swing.JButton();
        outputPointsButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();

        setMinimumSize(new java.awt.Dimension(200, 1000));
        setPreferredSize(new java.awt.Dimension(200, 1000));
        setLayout(new java.awt.GridBagLayout());

        pointsPanel.setLayout(new java.awt.GridBagLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        clearPointsButton.setText("Clear all points");
        clearPointsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearPointsButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(clearPointsButton, gridBagConstraints);

        removePointButton.setText("Remove selected points");
        removePointButton.setEnabled(false);
        removePointButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removePointButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        jPanel1.add(removePointButton, gridBagConstraints);

        addIntersectionButton.setText("Add intersection point");
        addIntersectionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addIntersectionButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        jPanel1.add(addIntersectionButton, gridBagConstraints);

        undoButton.setText("Undo last point");
        undoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                undoButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        jPanel1.add(undoButton, gridBagConstraints);

        addCalculablePointButton.setText("Add calculated point");
        addCalculablePointButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addCalculablePointButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        jPanel1.add(addCalculablePointButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pointsPanel.add(jPanel1, gridBagConstraints);

        jPanel3.setLayout(new java.awt.GridBagLayout());

        showGlyphsCB.setText("Show glyphs in 3D panel");
        showGlyphsCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showGlyphsCBActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 5, 0, 0);
        jPanel3.add(showGlyphsCB, gridBagConstraints);

        paintLabelsCB.setText("Paint labels in 2D slices");
        paintLabelsCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paintLabelsCBActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        jPanel3.add(paintLabelsCB, gridBagConstraints);

        glyphScaleSlider.setMaximum(200);
        glyphScaleSlider.setValue(20);
        glyphScaleSlider.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                glyphScaleSliderMouseReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 1, 5, 5);
        jPanel3.add(glyphScaleSlider, gridBagConstraints);

        jLabel1.setText("Glyph scale:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 6, 5, 0);
        jPanel3.add(jLabel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        pointsPanel.add(jPanel3, gridBagConstraints);

        jScrollPane1.setMinimumSize(new java.awt.Dimension(180, 150));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(240, 150));

        pointsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Point label", "x", "y", "z"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        pointsTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(pointsTable);
        pointsTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        pointsTable.getColumnModel().getColumn(0).setMinWidth(70);
        pointsTable.getColumnModel().getColumn(0).setMaxWidth(90);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pointsPanel.add(jScrollPane1, gridBagConstraints);

        customSlicePanel.setLayout(new java.awt.GridBagLayout());

        createCustomPlaneButton.setText("Apply to custom plane");
        createCustomPlaneButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createCustomPlaneButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 5);
        customSlicePanel.add(createCustomPlaneButton, gridBagConstraints);

        slicePositioningCB.setText("Use custom slice positioning");
        slicePositioningCB.setEnabled(false);
        slicePositioningCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                slicePositioningCBActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        customSlicePanel.add(slicePositioningCB, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pointsPanel.add(customSlicePanel, gridBagConstraints);

        mainTabbedPane.addTab("points", pointsPanel);

        connectionsPanel.setLayout(new java.awt.GridBagLayout());

        connectPoint1CB.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        connectPoint1CB.setMinimumSize(new java.awt.Dimension(130, 24));
        connectPoint1CB.setPreferredSize(new java.awt.Dimension(130, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 0, 0);
        connectionsPanel.add(connectPoint1CB, gridBagConstraints);

        connectPoint2CB.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        connectPoint2CB.setMinimumSize(new java.awt.Dimension(130, 24));
        connectPoint2CB.setPreferredSize(new java.awt.Dimension(130, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 5, 0, 0);
        connectionsPanel.add(connectPoint2CB, gridBagConstraints);

        connectButton.setText("Connect");
        connectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(9, 5, 0, 5);
        connectionsPanel.add(connectButton, gridBagConstraints);

        jScrollPane2.setMinimumSize(new java.awt.Dimension(160, 160));
        jScrollPane2.setPreferredSize(new java.awt.Dimension(200, 160));

        connectionsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(connectionsTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        connectionsPanel.add(jScrollPane2, gridBagConstraints);

        jButton1.setText("Clear all connections");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        connectionsPanel.add(jButton1, gridBagConstraints);

        removeConnectionButton.setText("Remove selected connection");
        removeConnectionButton.setEnabled(false);
        removeConnectionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeConnectionButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 0, 5);
        connectionsPanel.add(removeConnectionButton, gridBagConstraints);

        showConnections2DCB.setText("Show connections in 2D slices");
        showConnections2DCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showConnections2DCBActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 0, 5);
        connectionsPanel.add(showConnections2DCB, gridBagConstraints);

        showConnections3DCB.setText("Show connections in 3D panel");
        showConnections3DCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showConnections3DCBActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        connectionsPanel.add(showConnections3DCB, gridBagConstraints);

        showDistances2DCB.setText("Show distances in 2D slices");
        showDistances2DCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showDistances2DCBActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        connectionsPanel.add(showDistances2DCB, gridBagConstraints);

        jLabel2.setText("Connections scale:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 9, 0, 0);
        connectionsPanel.add(jLabel2, gridBagConstraints);

        tubesScaleSlider.setMaximum(200);
        tubesScaleSlider.setValue(20);
        tubesScaleSlider.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tubesScaleSliderMouseReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        connectionsPanel.add(tubesScaleSlider, gridBagConstraints);

        mainTabbedPane.addTab("connections", connectionsPanel);

        calculablesPanel.setLayout(new java.awt.GridBagLayout());

        topPanel.setMinimumSize(new java.awt.Dimension(180, 100));
        topPanel.setPreferredSize(new java.awt.Dimension(200, 100));
        topPanel.setLayout(new java.awt.GridBagLayout());

        paintCalculableValues2DCB.setText("Show values in 2D slices");
        paintCalculableValues2DCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paintCalculableValues2DCBActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        topPanel.add(paintCalculableValues2DCB, gridBagConstraints);

        addCalculableButon.setText("Add calculable...");
        addCalculableButon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addCalculableButonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        topPanel.add(addCalculableButon, gridBagConstraints);

        removeCalculableButton.setText("Remove selected");
        removeCalculableButton.setEnabled(false);
        removeCalculableButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeCalculableButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        topPanel.add(removeCalculableButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        calculablesPanel.add(topPanel, gridBagConstraints);

        mainPanel.setLayout(new java.awt.BorderLayout());

        jScrollPane3.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        cpsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        cpsTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        cpsTable.setMaximumSize(new java.awt.Dimension(2147483647, 1000000));
        cpsTable.setMinimumSize(new java.awt.Dimension(640, 200));
        cpsTable.setPreferredSize(new java.awt.Dimension(640, 800));
        jScrollPane3.setViewportView(cpsTable);

        mainPanel.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        calculablesPanel.add(mainPanel, gridBagConstraints);

        mainTabbedPane.addTab("calculables", calculablesPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        add(mainTabbedPane, gridBagConstraints);

        bottomPanel.setLayout(new java.awt.GridBagLayout());

        importButton.setText("Load points & connections...");
        importButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        bottomPanel.add(importButton, gridBagConstraints);

        exportButton.setText("Save points & connections...");
        exportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 5);
        bottomPanel.add(exportButton, gridBagConstraints);

        wizardButton.setText("Start wizard");
        wizardButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wizardButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        bottomPanel.add(wizardButton, gridBagConstraints);

        outputPointsButton.setText("Output points");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        bottomPanel.add(outputPointsButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        add(bottomPanel, gridBagConstraints);

        jPanel2.setMinimumSize(new java.awt.Dimension(100, 10));
        jPanel2.setPreferredSize(new java.awt.Dimension(100, 10));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jPanel2, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

   private void clearPointsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearPointsButtonActionPerformed
//       wizz = null;
       //params.clearConenctions();
       params.clearPoints();
       fireStateChanged();
}//GEN-LAST:event_clearPointsButtonActionPerformed

   private void showGlyphsCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showGlyphsCBActionPerformed
       params.setShowGlyphs(showGlyphsCB.isSelected());
   }//GEN-LAST:event_showGlyphsCBActionPerformed

   private void paintLabelsCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_paintLabelsCBActionPerformed
       params.setPaintLabels(paintLabelsCB.isSelected());
   }//GEN-LAST:event_paintLabelsCBActionPerformed

   private void removePointButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removePointButtonActionPerformed
//       wizz = null;
       params.removePoints(pointsTable.getSelectedRows());
   }//GEN-LAST:event_removePointButtonActionPerformed

   private void glyphScaleSliderMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_glyphScaleSliderMouseReleased
       params.setGlyphScale((float) glyphScaleSlider.getValue() / 40.0f);
}//GEN-LAST:event_glyphScaleSliderMouseReleased

   private void connectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectButtonActionPerformed
       int i1, i2;
       i1 = connectPoint1CB.getSelectedIndex();
       i2 = connectPoint2CB.getSelectedIndex();
       if (i1 != i2) {
           params.addConnection(params.getPointsDescriptors().get(i1), params.getPointsDescriptors().get(i2));
       }
   }//GEN-LAST:event_connectButtonActionPerformed

   private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
       params.clearConenctions();
   }//GEN-LAST:event_jButton1ActionPerformed

   private void showConnections2DCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showConnections2DCBActionPerformed
       params.setShowConnections2D(showConnections2DCB.isSelected());
}//GEN-LAST:event_showConnections2DCBActionPerformed

   private void showConnections3DCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showConnections3DCBActionPerformed
       params.setShowConnections3D(showConnections3DCB.isSelected());
   }//GEN-LAST:event_showConnections3DCBActionPerformed

   private void tubesScaleSliderMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tubesScaleSliderMouseReleased
       params.setConnectionScale((float) tubesScaleSlider.getValue() / 40.0f);
   }//GEN-LAST:event_tubesScaleSliderMouseReleased

   private void exportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportButtonActionPerformed
       if (params.getPointsDescriptors().size() > 1) {
           exportPointsAndConnections();
       }
   }//GEN-LAST:event_exportButtonActionPerformed

   private void createCustomPlaneButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createCustomPlaneButtonActionPerformed
       fireStateChanged();
   }//GEN-LAST:event_createCustomPlaneButtonActionPerformed

   private void importButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importButtonActionPerformed
       importPointsAndConnections();
       fireStateChanged();
   }//GEN-LAST:event_importButtonActionPerformed

   private void removeConnectionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeConnectionButtonActionPerformed
       params.removeConnections(connectionsTable.getSelectedRows());
   }//GEN-LAST:event_removeConnectionButtonActionPerformed

   private void wizardButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wizardButtonActionPerformed

       startWizard();


   }//GEN-LAST:event_wizardButtonActionPerformed

   private void addIntersectionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addIntersectionButtonActionPerformed
       params.addPoint(params.getIntersectionPoint());
   }//GEN-LAST:event_addIntersectionButtonActionPerformed

   private void undoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_undoButtonActionPerformed
       if (params.getPoints().isEmpty()) {
           return;
       }
       int n = params.getPoints().size() - 1;
       params.removePoint(n);
   }//GEN-LAST:event_undoButtonActionPerformed

   private void slicePositioningCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_slicePositioningCBActionPerformed
       params.setSlicePositioning(slicePositioningCB.isSelected());
   }//GEN-LAST:event_slicePositioningCBActionPerformed

   private void showDistances2DCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showDistances2DCBActionPerformed
       params.setShowDistances2D(showDistances2DCB.isSelected());
   }//GEN-LAST:event_showDistances2DCBActionPerformed

   private void paintCalculableValues2DCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_paintCalculableValues2DCBActionPerformed
       cparams.setPaintCalculable2D(paintCalculableValues2DCB.isSelected());
}//GEN-LAST:event_paintCalculableValues2DCBActionPerformed

   private void addCalculableButonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addCalculableButonActionPerformed
       final NewCalculableDialog dlg = new NewCalculableDialog(this.frame, true);
       dlg.setGeometryParams(params);
       dlg.setVisible(true);
       if (dlg.getResult() == NewCalculableDialog.Result.ACCEPT) {
           cparams.addCalculableParameter(dlg.getNewCalculableParameter());
       }
   }//GEN-LAST:event_addCalculableButonActionPerformed

   private void removeCalculableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeCalculableButtonActionPerformed
       cparams.removeCalculableParameters(cpsTable.getSelectedRows());
}//GEN-LAST:event_removeCalculableButtonActionPerformed

   private void addCalculablePointButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addCalculablePointButtonActionPerformed
       NewCalculablePointDialog dlg = new NewCalculablePointDialog(null, true);
       dlg.setGeometryParams(params);
       dlg.setVisible(true);
       if (dlg.getResult() == NewCalculablePointDialog.Result.ACCEPT) {
           params.addPoint(dlg.getNewCalculablePoint());
       }

   }//GEN-LAST:event_addCalculablePointButtonActionPerformed
    /**
     * Utility field holding list of ChangeListeners.
     */
    private transient ArrayList<ChangeListener> changeListenerList =
            new ArrayList<ChangeListener>();

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
     * @param object Parameter #1 of the
     * <CODE>ChangeEvent<CODE> constructor.
     */
    private void fireStateChanged() {
        ChangeEvent e = new ChangeEvent(this);
        for (ChangeListener listener : changeListenerList) {
            listener.stateChanged(e);
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addCalculableButon;
    private javax.swing.JButton addCalculablePointButton;
    private javax.swing.JButton addIntersectionButton;
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JPanel calculablesPanel;
    private javax.swing.JButton clearPointsButton;
    private javax.swing.JButton connectButton;
    private javax.swing.JComboBox connectPoint1CB;
    private javax.swing.JComboBox connectPoint2CB;
    private javax.swing.JPanel connectionsPanel;
    private javax.swing.JTable connectionsTable;
    private javax.swing.JTable cpsTable;
    private javax.swing.JButton createCustomPlaneButton;
    private javax.swing.JPanel customSlicePanel;
    private javax.swing.JButton exportButton;
    private javax.swing.JSlider glyphScaleSlider;
    private javax.swing.JButton importButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JTabbedPane mainTabbedPane;
    private javax.swing.JButton outputPointsButton;
    private javax.swing.JCheckBox paintCalculableValues2DCB;
    private javax.swing.JCheckBox paintLabelsCB;
    private javax.swing.JPanel pointsPanel;
    private javax.swing.JTable pointsTable;
    private javax.swing.JButton removeCalculableButton;
    private javax.swing.JButton removeConnectionButton;
    private javax.swing.JButton removePointButton;
    private javax.swing.JCheckBox showConnections2DCB;
    private javax.swing.JCheckBox showConnections3DCB;
    private javax.swing.JCheckBox showDistances2DCB;
    private javax.swing.JCheckBox showGlyphsCB;
    private javax.swing.JCheckBox slicePositioningCB;
    private javax.swing.JPanel topPanel;
    private javax.swing.JSlider tubesScaleSlider;
    private javax.swing.JButton undoButton;
    private javax.swing.JButton wizardButton;
    // End of variables declaration//GEN-END:variables

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting() || tableSilent) {
            return;
        }

        if (e.getSource() == pointsTable.getSelectionModel() && pointsTable.getRowSelectionAllowed()) {
            //removePointButton.setEnabled(!pointsTable.getSelectionModel().isSelectionEmpty());
            params.setSelectedPoints(pointsTable.getSelectedRows());
        }

        if (e.getSource() == connectionsTable.getSelectionModel() && connectionsTable.getRowSelectionAllowed()) {
            //removeConnectionButton.setEnabled(!connectionsTable.getSelectionModel().isSelectionEmpty());
        }

        if (e.getSource() == cpsTable.getSelectionModel() && cpsTable.getRowSelectionAllowed()) {
            //removeCalculableButton.setEnabled(!cpsTable.getSelectionModel().isSelectionEmpty());
        }

        updateButtons();
    }

    private void updateButtons() {
        removePointButton.setEnabled(!pointsTable.getSelectionModel().isSelectionEmpty());
        removeConnectionButton.setEnabled(!connectionsTable.getSelectionModel().isSelectionEmpty());
        removeCalculableButton.setEnabled(!cpsTable.getSelectionModel().isSelectionEmpty());

        addCalculableButon.setVisible(!simple);
        removeCalculableButton.setVisible(!simple);
        bottomPanel.setVisible(!simple);
        addCalculablePointButton.setVisible(!simple);
        
    }
    

    public JButton getOutputPointsButton() {
        return outputPointsButton;
    }

    public void startWizard() {
        if (params.getDefaultNfdFile() != null) {
            if (!wizard.wasStarted()) {
                //params.clearConenctions();
                params.clearPoints();
                wizard.loadDefaultNfdStructure();
            }

            wizard.setVisible(true);
            wizard.start();
        }

        if (params.getDefaultTasFile() != null) {
            if (!wizard2.wasStarted()) {
                //params.clearConenctions();
                params.clearPoints();
                wizard2.loadDefaultTasStructure();
            }

            wizard2.setVisible(true);
            wizard2.start();
        }


    }

    /**
     * @return the wizard
     */
    public PointsWizardDialog getPointsWizard() {
        return wizard;
    }

    /**
     * @return the wizard2
     */
    public ToolsActivityWizardDialog getToolsWizard() {
        return wizard2;
    }

    public void setSimpleGUI(boolean simpleGUI) {
        this.simple = simpleGUI;
        updateGUI();
    }
}
