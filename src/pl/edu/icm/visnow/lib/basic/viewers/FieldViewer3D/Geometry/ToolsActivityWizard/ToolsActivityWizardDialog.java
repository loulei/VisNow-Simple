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

package pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.ToolsActivityWizard;

import java.io.File;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.FieldDisplay3DFrame;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.CalculableParameter;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.CalculableParams;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.CalculablePoint;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.DependantPointDescriptor;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.GeometryParams;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.GeometryParamsEvent;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.GeometryParamsListener;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.PointDescriptor;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.GeometryTools.AngleTool;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.GeometryTools.GeometryTool;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.GeometryTools.LineTool;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.GeometryToolsStorage;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.GlobalParams;
import pl.edu.icm.visnow.system.main.VisNow;

/**
 *
 * @author babor
 */
public class ToolsActivityWizardDialog extends javax.swing.JDialog implements GeometryParamsListener {

    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ToolsActivityWizardDialog.class);

    private RootEntry root = null;
    private GeometryParams gparams = null;
    private CalculableParams cparams = null;
    private FieldDisplay3DFrame frame = null;
    private GlobalParams globalParams = null;
    private int currentTool = 0;

    private boolean started = false;
    private boolean done = false;

    private boolean save = false;

    private String title = "Tools Activity Wizard";

    private boolean silent = false;

    private boolean limitedAccess = false;

    /** Creates new form PointsWizardDialog */
    public ToolsActivityWizardDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        if (parent instanceof FieldDisplay3DFrame) {
            frame = (FieldDisplay3DFrame) parent;
            globalParams = frame.getParams();
        }

        initComponents();
        structureTree.getSelectionModel().setSelectionMode(DefaultTreeSelectionModel.SINGLE_TREE_SELECTION);
        structureTree.addTreeSelectionListener(new TreeSelectionListener() {

            @Override
            public void valueChanged(TreeSelectionEvent e) {
                if(silent)
                    return;

                TreePath path = e.getPath();
                descriptionPane.setText("");
                onTreeSelectionValueChanged(path);
            }
        });

        setTitle(title);

        updateGUItoDefault();
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

        jPanel1 = new javax.swing.JPanel();
        loadButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        structureTree = new javax.swing.JTree();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        descriptionPane = new javax.swing.JTextPane();
        saveButton = new javax.swing.JButton();
        exitButton = new javax.swing.JButton();
        loadStructButton = new javax.swing.JButton();
        autosaveCB = new javax.swing.JCheckBox();
        writeHrfCB = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(800, 600));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        loadButton.setText("Load file");
        loadButton.setMaximumSize(new java.awt.Dimension(160, 25));
        loadButton.setMinimumSize(new java.awt.Dimension(160, 25));
        loadButton.setName("loadButton"); // NOI18N
        loadButton.setPreferredSize(new java.awt.Dimension(160, 25));
        loadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        jPanel1.add(loadButton, gridBagConstraints);

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("<no structure loaded>");
        structureTree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        structureTree.setCellRenderer(new EntryTreeCellRenderer());
        structureTree.setName("structureTree"); // NOI18N
        jScrollPane1.setViewportView(structureTree);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 5);
        jPanel1.add(jScrollPane1, gridBagConstraints);

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new java.awt.BorderLayout());

        jScrollPane2.setName("jScrollPane2"); // NOI18N
        jScrollPane2.setPreferredSize(new java.awt.Dimension(600, 200));

        descriptionPane.setEditable(false);
        descriptionPane.setName("descriptionPane"); // NOI18N
        jScrollPane2.setViewportView(descriptionPane);

        jPanel2.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jPanel2, gridBagConstraints);

        saveButton.setText("Save file as...");
        saveButton.setEnabled(false);
        saveButton.setMaximumSize(new java.awt.Dimension(160, 25));
        saveButton.setMinimumSize(new java.awt.Dimension(160, 25));
        saveButton.setName("saveButton"); // NOI18N
        saveButton.setPreferredSize(new java.awt.Dimension(160, 25));
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(saveButton, gridBagConstraints);

        exitButton.setText("End wizard");
        exitButton.setMaximumSize(new java.awt.Dimension(160, 25));
        exitButton.setMinimumSize(new java.awt.Dimension(160, 25));
        exitButton.setName("exitButton"); // NOI18N
        exitButton.setPreferredSize(new java.awt.Dimension(160, 25));
        exitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 5);
        jPanel1.add(exitButton, gridBagConstraints);

        loadStructButton.setText("Load structure");
        loadStructButton.setMaximumSize(new java.awt.Dimension(160, 25));
        loadStructButton.setMinimumSize(new java.awt.Dimension(160, 25));
        loadStructButton.setName("loadStructButton"); // NOI18N
        loadStructButton.setPreferredSize(new java.awt.Dimension(160, 25));
        loadStructButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadStructButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        jPanel1.add(loadStructButton, gridBagConstraints);

        autosaveCB.setText("autosave");
        autosaveCB.setEnabled(false);
        autosaveCB.setName("autosaveCB"); // NOI18N
        autosaveCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autosaveCBActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        jPanel1.add(autosaveCB, gridBagConstraints);

        writeHrfCB.setSelected(true);
        writeHrfCB.setText("write HRF");
        writeHrfCB.setEnabled(false);
        writeHrfCB.setName("writeHrfCB"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        jPanel1.add(writeHrfCB, gridBagConstraints);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private String lastPath = null;
    private String currentFilePath = null;
    private FileNameExtensionFilter tasFilter = new FileNameExtensionFilter("Tools Activity Structure file (*.tas)","tas","TAS");

    private void loadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadButtonActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setApproveButtonText("Load");
        if (lastPath == null) {
            fileChooser.setCurrentDirectory(new File( VisNow.get().getMainConfig().getAdditionalConfigPath() ));
        } else {
            fileChooser.setCurrentDirectory(new File(lastPath));
        }
        
        fileChooser.addChoosableFileFilter(tasFilter);

        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            lastPath = path.substring(0, path.lastIndexOf(File.separator));
            loadTas(path);
            next();
        }
    }//GEN-LAST:event_loadButtonActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setApproveButtonText("Save");
        if (lastPath == null) {
            fileChooser.setCurrentDirectory(new File( VisNow.get().getMainConfig().getAdditionalConfigPath() ));
        } else {
            fileChooser.setCurrentDirectory(new File(lastPath));
        }

        
        fileChooser.addChoosableFileFilter(tasFilter);

        int returnVal = fileChooser.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            if(!(path.endsWith(".tas") || path.endsWith(".TAS"))) {
                path += ".tas";
            }
            lastPath = new String(path);
            writeTas(path, gparams.getInfoString(), writeHrfCB.isSelected());
        }

    }//GEN-LAST:event_saveButtonActionPerformed

    private void exitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitButtonActionPerformed
        exitWizard();
    }//GEN-LAST:event_exitButtonActionPerformed

    private void loadStructButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadStructButtonActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setApproveButtonText("Load");
        if (lastPath == null) {
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        } else {
            fileChooser.setCurrentDirectory(new File(lastPath));
        }

        fileChooser.addChoosableFileFilter(tasFilter);

        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            lastPath = path.substring(0, path.lastIndexOf(File.separator));
            loadTas(path);
            next();
        }
    }//GEN-LAST:event_loadStructButtonActionPerformed

    private void autosaveCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autosaveCBActionPerformed
        if(!save) {
            saveButtonActionPerformed(evt);
        } else {
            this.setSave(false);
        }
    }//GEN-LAST:event_autosaveCBActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox autosaveCB;
    private javax.swing.JTextPane descriptionPane;
    private javax.swing.JButton exitButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton loadButton;
    private javax.swing.JButton loadStructButton;
    private javax.swing.JButton saveButton;
    private javax.swing.JTree structureTree;
    private javax.swing.JCheckBox writeHrfCB;
    // End of variables declaration//GEN-END:variables

    private boolean gparamsSilent = false;

    public void loadDefaultTasStructure() {
        loadTasStructure(gparams.getDefaultTasFile());
    }

    public boolean loadTas(String filePath) {
        return loadTas(filePath, false);
    }

    public boolean loadTas(String filePath, boolean forbidWarningDialog) {
        this.setSave(false);
        root = TasStructureReader.readTas(filePath,true);
        if (root == null) {
            structureTree.setModel(null);
            descriptionPane.setText("");
            if(!limitedAccess) {
                loadStructButton.setEnabled(true);
                saveButton.setEnabled(false);
                autosaveCB.setEnabled(false);
                writeHrfCB.setEnabled(false);
            }
            return false;
        }

        currentFilePath = filePath;
        this.setSave(true, forbidWarningDialog);
        if(!limitedAccess) {
            loadStructButton.setEnabled(false);
            saveButton.setEnabled(true);
            autosaveCB.setEnabled(true);
            writeHrfCB.setEnabled(true);
        }
        root.cleanup();

        root.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                structureTree.repaint();
            }
        });

        RootTreeModel rootModel = new RootTreeModel(root);
        structureTree.setModel(rootModel);
        descriptionPane.setText("");


        ArrayList<ToolEntry> tools = root.getAllToolEntries();
        //ArrayList<ToolEntry> tools;
        ToolEntry t;
        GeometryTool tool;
        CalculableParameter cp;
        gparamsSilent = true;
        gparams.clearPoints();

        for (int i = 0; i < tools.size(); i++) {
            t = tools.get(i);
            if(t.isReady()) {
                ArrayList<PointDescriptor> pds = t.getPointDescriptors();
                for (int j = 0; j < pds.size(); j++) {
                    gparams.addPoint(pds.get(j).getName(), pds.get(j).getWorldCoords());
                    pds.set(j, gparams.getPointsDescriptor(gparams.getNumberOfPoints()-1));
                }

                tool = t.getTool();
                if(tool != null) {
                    if(tool instanceof LineTool && pds.size() == 2) {
                        gparams.addConnection(pds.get(0), pds.get(1));
                    } else if(tool instanceof AngleTool && pds.size() == 3) {
                        gparams.addConnection(pds.get(0), pds.get(1));
                        gparams.addConnection(pds.get(1), pds.get(2));
                    }
                }
            }
        }

        ArrayList<CpointEntry> cpoints = root.getAllCpointEntries();
        CpointEntry c;
        for (int i = 0; i < cpoints.size(); i++) {
            c = cpoints.get(i);
            if(c.isReady() && c.getCalculablePoint() != null) {
                ArrayList<PointDescriptor> pds = new ArrayList<PointDescriptor>();
                ArrayList<ToolEntry> tes = c.getAllToolEntries();
                for (int j = 0; j < tes.size(); j++) {
                    pds.addAll(tes.get(j).getPointDescriptors());                                                            
                }
                c.getCalculablePoint().setPointDescriptors(pds);

                if(c.getCalculablePoint().isPointDescriptorsReady()) {
                    gparams.addPoint(c.getCalculablePoint());
                }
            }
        }


        ArrayList<BranchEntry> branches = root.getAllBranchEntries();
        BranchEntry b;
        for (int i = 0; i < branches.size(); i++) {
            b = branches.get(i);

            if(b.isReady()) {

                ArrayList<Entry> es = b.getDependancies();

                ArrayList<CpointEntry> bces = new ArrayList<CpointEntry>();
                for (int j = 0; j < es.size(); j++) {
                    if(es.get(j) instanceof CpointEntry) {
                        bces.add((CpointEntry)es.get(j));
                    }
                }

                //ArrayList<CpointEntry> bces = b.getAllCpointEntries();
                if(bces.size() > 1) {
                    for (int j = 1; j < bces.size(); j++) {
                        PointDescriptor p0 = gparams.getPointsDescriptorByName(bces.get(j-1).getCalculablePoint().getName());
                        PointDescriptor p1 = gparams.getPointsDescriptorByName(bces.get(j).getCalculablePoint().getName());
                        if(p0 != null && p1 != null && gparams.getConnectionByPointNames(p0.getName(), p1.getName()) == null) {
                            gparams.addConnection(p0,p1);
                        }
                    }
                }


                cp = b.getCalculable();
                if(cp != null) {
                    ArrayList<PointDescriptor> cp_pds = new ArrayList<PointDescriptor>();
                    for (int k = 0; k < es.size(); k++) {
                        Entry e = es.get(k);
                        if(e.isReady()) {
                            if(e instanceof ToolEntry) {
                                ToolEntry te = (ToolEntry)e;
                                ArrayList<PointDescriptor> pds = te.getPointDescriptors();
                                for (int j = 0; j < pds.size(); j++) {
                                    pds.set(j, gparams.getPointsDescriptorByName(pds.get(j).getName()));
                                    cp_pds.add(gparams.getPointsDescriptorByName(pds.get(j).getName()));
                                }
                            } else if(e instanceof CpointEntry) {
                                CpointEntry ce = (CpointEntry)e;
                                cp_pds.add(gparams.getPointsDescriptorByName(ce.getCalculablePoint().getName()));
                            } else if(e instanceof BranchEntry) {
                                BranchEntry be = (BranchEntry)e;
                                ArrayList<PointDescriptor> pds = processBranchEntry(be);
                                for (int j = 0; j < pds.size(); j++) {
                                    pds.set(j, gparams.getPointsDescriptorByName(pds.get(j).getName()));
                                    cp_pds.add(gparams.getPointsDescriptorByName(pds.get(j).getName()));
                                }
                            }
                        }
                    }
                    cp.setPointDescriptors(cp_pds);
                    cparams.addCalculableParameter(cp);
                }

            }
        }

        gparamsSilent = false;
        currentTool = 0;
        if(frame != null && globalParams != null) {
            globalParams.setSelectedGeometryTool(-1, true);
        }

        return true;
    }

    public boolean loadTasStructure(String filePath) {
        root = null;
        this.setSave(false);
        root = TasStructureReader.readTasStructure(filePath);
        if (root == null) {
            structureTree.setModel(null);
            descriptionPane.setText("");
            if(!limitedAccess) {
                loadStructButton.setEnabled(true);
                saveButton.setEnabled(false);
                autosaveCB.setEnabled(false);
                writeHrfCB.setEnabled(false);
            }
            return false;
        }

        if(!limitedAccess) {
            loadStructButton.setEnabled(false);
            saveButton.setEnabled(true);
            autosaveCB.setEnabled(true);
            writeHrfCB.setEnabled(true);
        }
        currentFilePath = null;
        this.setSave(false);
        root.cleanup();

        root.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                structureTree.repaint();
            }
        });

        RootTreeModel rootModel = new RootTreeModel(root);
        structureTree.setModel(rootModel);
        descriptionPane.setText("");

        currentTool = 0;
        return true;
    }


    int pCounter = 0;
    @Override
    public void onGeometryParamsChanged(GeometryParamsEvent e) {
        if(!this.isVisible())
            return;

        if (e.getType() == GeometryParamsEvent.TYPE_POINT_MODIFIED) {
            onTreeSelectionValueChanged(structureTree.getSelectionPath());
            currentTool = -1;
            if(frame != null && globalParams != null) {
                globalParams.setSelectedGeometryTool(-1, true);
            }

            if(save && (done || gparamsSilent || silent))
                writeTas(currentFilePath, gparams.getInfoString(), writeHrfCB.isSelected());
        }

        if (e.getType() == GeometryParamsEvent.TYPE_POINT_SELECTION) {
            if (gparams.getSelectedPoints() != null && gparams.getSelectedPoints().length == 1) {
                //int n = gparams.getSelectedPoints()[0];
                //goTo(root.getNuberOfToolByPointDescriptor(gparams.getPointsDescriptors().get(n)));

                //structureTree.clearSelection();

            } else if(gparams.getSelectedPoints() == null) {
                next();
            }
        }

        if(done || gparamsSilent || silent)
            return;

        if (e.getType() == GeometryParamsEvent.TYPE_POINT_ADDED) {
            if(currentTool != -1 && !(gparams.getPointsDescriptor(gparams.getNumberOfPoints()-1) instanceof DependantPointDescriptor) && root != null && root.getToolEntryByNumber(currentTool) != null && root.getToolEntryByNumber(currentTool).getTool() != null) {
                pCounter++;
                if (pCounter == root.getToolEntryByNumber(currentTool).getTool().getMinimumNPoints() ) {
                    ArrayList<PointDescriptor> pds = new ArrayList<PointDescriptor>();
                    for (int i = 0; i < pCounter; i++) {
                        pds.add(gparams.getPointsDescriptor(gparams.getNumberOfPoints()-(pCounter-i)));
                    }
                    root.getToolEntryByNumber(currentTool).setPointDescriptors(pds);
                    pCounter = 0;
                    next();
                }

            }
        }




        if(save)
            writeTas(currentFilePath, gparams.getInfoString(), writeHrfCB.isSelected());


    }

    /**
     * @param gparams the gparams to set
     */
    public void setGeometryParams(GeometryParams gparams) {
        gparams.addGeometryParamsListener(this);
        this.gparams = gparams;
    }

    public void setCalculableParams(CalculableParams cparams) {
        this.cparams = cparams;
    }

    public void next() {
        //VisNowCallTrace.trace();
        if (root == null) {
            return;
        }

        ToolEntry t = root.getToolEntryByNumber(currentTool);
        ArrayList<BranchEntry> bes = root.getAllBranchEntries();
        ArrayList<CpointEntry> ces = root.getAllCpointEntries();

        for (int i = 0; i < ces.size(); i++) {
            if(ces.get(i) != null && ces.get(i).isReady() && ces.get(i).dependsOn(t)) {
                CpointEntry cpe = (CpointEntry)ces.get(i);
                if(cpe.getCalculablePoint() != null) {
                    cpe.getCalculablePoint().setPointDescriptors(t.getPointDescriptors());
                    if(gparams.getPointsDescriptorByName(cpe.getCalculablePoint().getName()) == null)
                        gparams.addPoint(cpe.getCalculablePoint());
                }
            }
        }


        for (int i = 0; i < bes.size(); i++) {
            if(bes.get(i) != null && bes.get(i).isReady() && bes.get(i).dependsOn(t)) {

                ArrayList<Entry> deps = bes.get(i).getDependancies();
                ArrayList<CpointEntry> bces = new ArrayList<CpointEntry>();
                for (int j = 0; j < deps.size(); j++) {
                    if(deps.get(j) instanceof CpointEntry) {
                        CpointEntry cp = (CpointEntry)deps.get(j);
                        if(!bces.contains(cp))
                            bces.add(cp);
                    }

                }

                if(bces.size() > 1) {
                    for (int j = 1; j < bces.size(); j++) {
                        PointDescriptor p0 = gparams.getPointsDescriptorByName(bces.get(j-1).getCalculablePoint().getName());
                        PointDescriptor p1 = gparams.getPointsDescriptorByName(bces.get(j).getCalculablePoint().getName());
                        if(p0 != null && p1 != null && gparams.getConnectionByPointNames(p0.getName(), p1.getName()) == null) {
                            gparams.addConnection(p0,p1);
                        }
                    }
                }

                CalculableParameter cp = bes.get(i).getCalculable();
                if(cp == null)
                    continue;

                if(cparams.isContainsCalculableByName(cp))
                    continue;

                ArrayList<PointDescriptor> pds2 = processBranchEntry(bes.get(i));                
//                if(cp.getType().toString().startsWith("Signed")) {
//                    pds2.addAll(getReferencePlanePointDescriptors());
//                }
                cp.setPointDescriptors(pds2);
                cparams.addCalculableParameter(cp);
            }
        }

        if(root.isReady()) {
            this.done = true;
            currentTool = -1;
            if(frame != null && globalParams != null) {
                globalParams.setSelectedGeometryTool(-1, true);
            }
        }

        if(currentTool >= root.getAllToolEntries().size()-1 && root.isReady())
            finish();

        do {
            t = root.getToolEntryByNumber(currentTool);
            if (t == null) {
                if (!root.isReady()) {
                    currentTool = 0;
                } else {
                    if(frame != null && globalParams != null) {
                        globalParams.setSelectedGeometryTool(-1, true);
                    }
                    return;
                }
            } else {
                if (!t.isReady()) {
                    break;
                }

                currentTool++;
            }
        } while (true);

        TreePath path = root.getTreePathToTool(t);
        if (path != null) {
            structureTree.clearSelection();
            structureTree.setSelectionPath(path);
            structureTree.scrollPathToVisible(path);
        }
    }

    private void goTo(int n) {
        if (root == null) {
            return;
        }

        if (n < 0) {
            return;
        }

        ToolEntry t = root.getToolEntryByNumber(n);
        if (t == null) {
            return;
        }

        TreePath path = root.getTreePathToTool(t);
        if(n == currentTool) {
            silent = true;
            onTreeSelectionValueChanged(path);
            silent = false;
        } else {
            currentTool = n;
            if (path != null) {
                structureTree.setSelectionPath(path);
                structureTree.scrollPathToVisible(path);
            }
        }

        
    }

    public void start() {
        started = true;
        done = false;
        next();
    }

    public void writeTas(String path, String comment, boolean writeHRF) {
        writeTas(path, comment, false, writeHRF);
    }

    public void writeTas(String path, String comment, boolean forbidWarningDialog, boolean writeHRF) {
        if (root == null) {
            return;
        }

        log.debug("Saving TAS to file: "+path);
        if(TasStructureWriter.writeTas(root, path, comment)) {
            currentFilePath = new String(path);
            this.setSave(true, forbidWarningDialog);
        } else {
            this.setSave(false, forbidWarningDialog);
            JOptionPane.showMessageDialog(this, "Error saving data to file!", "ERROR", JOptionPane.ERROR_MESSAGE);
        }

        if(writeHRF) {            
            String hrfPath = path.substring(0, path.lastIndexOf(".tas"));
            hrfPath += ".txt";
            log.debug("Saving HRF to file: "+hrfPath);

            if(!TasStructureWriter.writeHrf(root, hrfPath)) {
                JOptionPane.showMessageDialog(this, "Error saving Human Readable Format data to text file!", "ERROR", JOptionPane.ERROR_MESSAGE);
            }
            
        }
    }

    /**
     * @return the started
     */
    public boolean wasStarted() {
        return started;
    }

    private void finish() {
        this.done = true;
        structureTree.getSelectionModel().clearSelection();

        gparams.setSelectedPoints(null);
        gparams.setNextPointName(null);

        if (frame != null) {
            //frame.setLeftStatusText("Numerical Foot Description complete!");
            //globalParams.setSelectedGeometryTool(globalParams.getSelectedGeometryTool(), false);
            currentTool = -1;
            globalParams.setSelectedGeometryTool(-1, true);
        }
    }

    public void setSave(boolean save) {
        setSave(save, false);
    }

    public void setSave(boolean save, boolean forbidWarningDialog) {
        if(!this.save && save && !forbidWarningDialog)
            displaySaveWarning(currentFilePath);
        
        this.save = save;
        autosaveCB.setSelected(save);
    }

    private void displaySaveWarning(String path) {
        JOptionPane.showMessageDialog(this, "WARNING!\n\nTAS file will be automatically saved,\noverwritting the loaded file:\n"+path+"\n\nUse 'autosave' option to switch this feature off.\n", "Autosave warning", JOptionPane.WARNING_MESSAGE);
    }

    private void onTreeSelectionValueChanged(TreePath path) {
        if(path == null || root == null)
            return;
        Object last = path.getLastPathComponent();

        descriptionPane.setText("");

        if(last instanceof ToolEntry) {
            ToolEntry te = (ToolEntry)last;
            currentTool = root.getNuberOfTool(te);

            descriptionPane.setText("id: "+te.getId()+"\n" +
                    "name: "+te.getName() + "\n" +
                    "description: "+te.getDescription()+"\n" +
                    "ready: "+(te.isReady()?"YES":"NO"));


            if(te.isReady()) {
                ArrayList<PointDescriptor> pds = te.getPointDescriptors();
                int[] pts = new int[pds.size()];
                for (int i = 0; i < pts.length; i++) {
                    pts[i] = gparams.getPointsDescriptors().indexOf(pds.get(i));
                }
                gparams.setSelectedPoints(pts);
                //gparams.setBlockAdding(true);

                if(frame != null && globalParams != null) {
                    globalParams.setSelectedGeometryTool(-1, true);
                }
            } else {
                gparams.setSelectedPoints(null);
                //gparams.setBlockAdding(false);

                if(frame != null && globalParams != null) {
                    globalParams.setSelectedGeometryTool(GeometryToolsStorage.getGeometryToolType(te.getTool()), true);
                }
            }

        } else {
            if(last instanceof BranchEntry) {
                BranchEntry be = (BranchEntry)last;
                descriptionPane.setText("id: "+be.getId()+"\n" +
                    "name: "+be.getName() + "\n" +
                    "description: "+be.getDescription()+"\n" +
                    "ready: "+(be.isReady()?"YES":"NO") +"\n" +
                    "value: "+(be.getCalculable()!=null?be.getCalculable().getValue():"<n/a>"));
            }

            currentTool = -1;
            if(frame != null && globalParams != null) {
                globalParams.setSelectedGeometryTool(-1, true);
            }

        }

    }

    public void exitWizard() {
        if(!save) {
            int res = JOptionPane.showConfirmDialog(this, "Do you really want to quit wizard without saving?", "Quit?", JOptionPane.YES_NO_OPTION);
            if(res == JOptionPane.NO_OPTION) {
                return;
            }
        }

        structureTree.getSelectionModel().clearSelection();

        gparams.setSelectedPoints(null);
        gparams.setNextPointName(null);

        if (frame != null) {
            frame.setLeftStatusText("");
            globalParams.setSelectedGeometryTool(GeometryToolsStorage.GEOMETRY_TOOL_POINT, false);
        }

        this.started = false;

        this.setVisible(false);

    }

    /**
     * @return the root
     */
    public RootEntry getRoot() {
        return root;
    }

    public void reset() {
        this.started = false;
        this.done = false;
    }

    /**
     * @param limitedAccess the limitedAccess to set
     */
    public void setLimitedAccess(boolean limitedAccess) {
        this.limitedAccess = limitedAccess;
        updateGUItoDefault();
    }

    private void updateGUItoDefault() {
        if(limitedAccess) {
            loadButton.setEnabled(false);
            loadStructButton.setEnabled(false);
            autosaveCB.setEnabled(false);
            writeHrfCB.setEnabled(false);
            saveButton.setEnabled(false);
            exitButton.setEnabled(false);
        } else {
            loadButton.setEnabled(true);
            loadStructButton.setEnabled(true);
            autosaveCB.setEnabled(false);
            writeHrfCB.setEnabled(false);
            saveButton.setEnabled(false);
            exitButton.setEnabled(true);
        }

        if(done) {
            currentTool = -1;
            if(frame != null && globalParams != null) {
                globalParams.setSelectedGeometryTool(-1, true);
            }
        }

    }


    private ArrayList<PointDescriptor> processBranchEntry(BranchEntry be) {
        ArrayList<Entry> deps = be.getDependancies();
        ArrayList<PointDescriptor> pds = new ArrayList<PointDescriptor>();
        Entry dep;
        CpointEntry cpe;
        CalculablePoint cpoint;
        for (int j = 0; j < deps.size(); j++) {
            dep = deps.get(j);
            if(dep instanceof ToolEntry) {
                pds.addAll(((ToolEntry)dep).getPointDescriptors());
            } else if(dep instanceof CpointEntry) {
                cpe = (CpointEntry)dep;
                cpoint = cpe.getCalculablePoint();
                if(cpoint != null) {
                    PointDescriptor pd = gparams.getPointsDescriptorByName(cpoint.getName());
                    if(pd != null)
                        pds.add(pd);
                }
            } else if(dep instanceof BranchEntry) {
                pds.addAll(processBranchEntry((BranchEntry)dep));
            }
        }

        return pds;
    }

    private ArrayList<PointDescriptor> getReferencePlanePointDescriptors() {
        if(gparams == null || gparams.getInField() == null || gparams.getInField().getDims().length != 2) {
            return null;
        }

        ArrayList<PointDescriptor> referencePlanePointDescriptors = new ArrayList<PointDescriptor>();

        float[] p0_coords = new float[3];
        int[] p0_indices = new int[3];
        p0_coords[0] = 0;
        p0_coords[1] = 0;
        p0_coords[2] = 0;
        p0_indices[0] = 0;
        p0_indices[1] = 0;
        p0_indices[2] = 0;
        PointDescriptor pd0 = new PointDescriptor(p0_indices, p0_coords);

        float[] p1_coords = new float[3];
        int[] p1_indices = new int[3];
        p1_coords[0] = 1;
        p1_coords[1] = 0;
        p1_coords[2] = 0;
        p1_indices[0] = 1;
        p1_indices[1] = 0;
        p1_indices[2] = 0;
        PointDescriptor pd1 = new PointDescriptor(p1_indices, p1_coords);

        float[] p2_coords = new float[3];
        int[] p2_indices = new int[3];
        p2_coords[0] = 0;
        p2_coords[1] = 1;
        p2_coords[2] = 0;
        p2_indices[0] = 0;
        p2_indices[1] = 1;
        p2_indices[2] = 0;
        PointDescriptor pd2 = new PointDescriptor(p2_indices, p2_coords);

        referencePlanePointDescriptors.add(pd0);
        referencePlanePointDescriptors.add(pd1);
        referencePlanePointDescriptors.add(pd2);

        return referencePlanePointDescriptors;
    }


}


