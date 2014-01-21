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
package pl.edu.icm.visnow.system.config;

import java.io.File;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import pl.edu.icm.visnow.system.main.VisNow;

/**
 *
 * @author Bartosz Borucki (babor@icm.edu.pl), University of Warsaw, ICM
 */
public class PreferencesPluginsPanel extends javax.swing.JPanel {

    private ArrayList<VNPlugin> plugins = new ArrayList<VNPlugin>();
    private ArrayList<File> folders = new ArrayList<File>();
    private JFileChooser fileChooser = new JFileChooser();

    /**
     * Creates new form PreferencesPluginsPanel
     */
    public PreferencesPluginsPanel() {
        initComponents();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setCurrentDirectory(new File(VisNow.get().getMainConfig().getUsableDataPath(PreferencesPluginsPanel.class)));
        pluginFoldersTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int rowCount = pluginFoldersTable.getSelectedRowCount();
                int[] rows = pluginFoldersTable.getSelectedRows();
                boolean extra = false;
                for (int i = 0; i < rows.length; i++) {
                    if (rows[i] != 0 && rows[i] != 1) {
                        extra = true;
                        break;
                    }
                }
                removeFolderButton.setEnabled(rowCount == 1 && extra);
            }
        });
    }

    public void init() {
        ArrayList<VNPlugin> plugs = VisNow.get().getMainConfig().getPlugins();
        plugins.clear();
        plugins.addAll(plugs);

        ArrayList<File> dirs = VisNow.get().getMainConfig().getPluginFolders();
        folders.clear();
        folders.addAll(dirs);

        updateGUI();
    }

    private void updateGUI() {
        AbstractTableModel pluginTableModel = new AbstractTableModel() {
            @Override
            public int getRowCount() {
                return plugins.size();
            }

            @Override
            public int getColumnCount() {
                return 3;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                switch (columnIndex) {
                    case 0:
                        return plugins.get(rowIndex).getName();
                    case 1:
                        return plugins.get(rowIndex).getJarPath();
                    case 2:
                        return plugins.get(rowIndex).isActive();
                }
                return null;
            }

            @Override
            public String getColumnName(int columnIndex) {
                switch (columnIndex) {
                    case 0:
                        return "Plugin name";
                    case 1:
                        return "Library JAR path";
                    case 2:
                        return "Active";
                }
                return "";
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0:
                        return String.class;
                    case 1:
                        return String.class;
                    case 2:
                        return Boolean.class;
                }
                return null;
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return (columnIndex == 2);
            }

            @Override
            public void setValueAt(Object aValue, int row, int column) {
                if (column == 2) {
                    if (!(aValue instanceof Boolean)) {
                        return;
                    }
                    boolean val = (Boolean) aValue;
                    if (val && !canActivatePlugin(plugins.get(row))) {
                        return;
                    }
                    plugins.get(row).setActive(val);
                }
                fireTableCellUpdated(row, column);
            }
        };
        pluginsTable.setModel(pluginTableModel);
        pluginsTable.getColumnModel().getColumn(0).setPreferredWidth(140);
        pluginsTable.getColumnModel().getColumn(0).setMinWidth(140);
        pluginsTable.getColumnModel().getColumn(0).setMaxWidth(140);
        pluginsTable.getColumnModel().getColumn(2).setPreferredWidth(40);
        pluginsTable.getColumnModel().getColumn(2).setMinWidth(40);
        pluginsTable.getColumnModel().getColumn(2).setMaxWidth(40);


        AbstractTableModel foldersTableModel = new AbstractTableModel() {
            @Override
            public int getRowCount() {
                return folders.size();
            }

            @Override
            public int getColumnCount() {
                return 1;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                return folders.get(rowIndex).getAbsolutePath();
            }

            @Override
            public String getColumnName(int columnIndex) {
                return "Plugin directory";
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };
        pluginFoldersTable.setModel(foldersTableModel);


    }

    public void apply() {
        ArrayList<VNPlugin> plugs = VisNow.get().getMainConfig().getPlugins();
        plugs.clear();
        plugs.addAll(plugins);

        ArrayList<File> dirs = VisNow.get().getMainConfig().getPluginFolders();
        dirs.clear();
        dirs.addAll(folders);        
        
        VisNow.get().getMainConfig().savePluginsConfig();
        VisNow.get().getMainConfig().reloadPlugins();
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

        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        pluginFoldersTable = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        addFolderButton = new javax.swing.JButton();
        removeFolderButton = new javax.swing.JButton();
        refreshButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        pluginsTable = new javax.swing.JTable();

        setLayout(new java.awt.GridBagLayout());

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Plugin directories"));
        jPanel2.setMinimumSize(new java.awt.Dimension(600, 100));
        jPanel2.setPreferredSize(new java.awt.Dimension(600, 200));
        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel3.setLayout(new java.awt.BorderLayout());

        pluginFoldersTable.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(pluginFoldersTable);

        jPanel3.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jPanel2.add(jPanel3, java.awt.BorderLayout.CENTER);

        jPanel4.setMinimumSize(new java.awt.Dimension(100, 100));
        jPanel4.setPreferredSize(new java.awt.Dimension(100, 200));
        jPanel4.setLayout(new java.awt.GridBagLayout());

        addFolderButton.setText("Add...");
        addFolderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addFolderButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        jPanel4.add(addFolderButton, gridBagConstraints);

        removeFolderButton.setText("Remove");
        removeFolderButton.setEnabled(false);
        removeFolderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeFolderButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        jPanel4.add(removeFolderButton, gridBagConstraints);

        refreshButton.setText("Refresh");
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel4.add(refreshButton, gridBagConstraints);

        jPanel2.add(jPanel4, java.awt.BorderLayout.EAST);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jPanel2, gridBagConstraints);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Plugins"));
        jPanel1.setMinimumSize(new java.awt.Dimension(600, 100));
        jPanel1.setPreferredSize(new java.awt.Dimension(600, 200));
        jPanel1.setLayout(new java.awt.BorderLayout());

        pluginsTable.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(pluginsTable);

        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jPanel1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void addFolderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addFolderButtonActionPerformed
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            VisNow.get().getMainConfig().setLastDataPath(path, PreferencesPluginsPanel.class);
            folders.add(new File(path));
            refreshPluginList();
        }
    }//GEN-LAST:event_addFolderButtonActionPerformed

    private void removeFolderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeFolderButtonActionPerformed
        int rowCount = pluginFoldersTable.getSelectedRowCount();
        int[] rows = pluginFoldersTable.getSelectedRows();
        if(rowCount < 1 || rows == null)
            return;
        
        for (int i = 0; i < rows.length; i++) {
            if(rows[i] != 0 && rows[i] != 1) {
                File f = folders.get(rows[i]);
                folders.remove(f);                
            }            
        }
        refreshPluginList();
        
    }//GEN-LAST:event_removeFolderButtonActionPerformed

    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshButtonActionPerformed
        refreshPluginList();
    }//GEN-LAST:event_refreshButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addFolderButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable pluginFoldersTable;
    private javax.swing.JTable pluginsTable;
    private javax.swing.JButton refreshButton;
    private javax.swing.JButton removeFolderButton;
    // End of variables declaration//GEN-END:variables

    private boolean canActivatePlugin(VNPlugin plugin) {
        if (plugin == null || !plugins.contains(plugin)) {
            return false;
        }

        for (int i = 0; i < plugins.size(); i++) {
            if (plugins.get(i).isActive() && plugins.get(i) != plugin && plugins.get(i).getLibraryName().equals(plugin.getLibraryName())) {
                return false;
            }
        }

        return true;
    }

    private void refreshPluginList() {
        ArrayList<File> dirs = VisNow.get().getMainConfig().getPluginFolders();
        dirs.clear();
        dirs.addAll(folders);        
        VisNow.get().getMainConfig().rereadPlugins();
        init();
    }
}
