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

package pl.edu.icm.visnow.help.whatnew;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Vector;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import pl.edu.icm.visnow.engine.error.Displayer;
import pl.edu.icm.visnow.system.main.VisNow;


/**
 *
 * @author  Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class WhatNewFrame extends javax.swing.JFrame {
    
    //<editor-fold defaultstate="collapsed" desc=" [VAR] File ">
    private File base;
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc=" [CONSTRUCTOR]  ">
    public WhatNewFrame() {
        initComponents();
        if(!VisNow.get().isDevelopment()) return;
        base = new File(VisNow.get().getOperatingFolder()+"/src/pl/edu/icm/visnow/help/whatnew");
        ///System.out.println(""+base.getPath());
        ///System.out.println(""+System.getProperty("user.name"));
        readFiles();
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc=" Refreshing ">
    private void readFiles() {
        dataList.clearSelection();
        Vector<File> years = new Vector<File>();
        Vector<File> months = new Vector<File>();
        Vector<File> days = new Vector<File>();
        Vector<WhatNewItem> items = new Vector<WhatNewItem>();
        for(int i=0; i<base.listFiles().length; ++i)
            if( (base.listFiles()[i]).getName().startsWith("_") )
                years.add(base.listFiles()[i]);
        for(File yfile: years)
            for(int i=0; i<yfile.listFiles().length; ++i)
                if( (yfile.listFiles()[i]).getName().startsWith("_") )
                    months.add(yfile.listFiles()[i]);
        for(File mfile: months)
            for(int i=0; i<mfile.listFiles().length; ++i)
                if( (mfile.listFiles()[i]).getName().startsWith("_") )
                    days.add(mfile.listFiles()[i]);
        DefaultListModel model = new DefaultListModel();
        for(File dfile: days)
            items.add(new WhatNewItem(dfile));
        WhatNewItem tmp;
        for(int i=0; i<items.size(); ++i)
        for(int j=0; j<items.size(); ++j) {
            if(items.elementAt(i).toInt() > items.elementAt(j).toInt()) {
                tmp = items.elementAt(j);
                items.setElementAt(items.elementAt(i), j);
                items.setElementAt(tmp, i);
            }
        }
        for(WhatNewItem item: items)
            model.addElement(item);
        dataList.setModel(model);
    }
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        browsePanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        textArea = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        authorField = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        newButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        dataList = new javax.swing.JList();

        setTitle("VisNow: Notes");

        textArea.setColumns(20);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setRows(5);
        jScrollPane2.setViewportView(textArea);

        jLabel1.setText("author:");

        authorField.setEditable(false);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        newButton.setText("New");
        newButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 3;
        gridBagConstraints.ipady = 3;
        jPanel1.add(newButton, gridBagConstraints);

        deleteButton.setText("Delete");
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 3;
        gridBagConstraints.ipady = 3;
        jPanel1.add(deleteButton, gridBagConstraints);

        saveButton.setText("Save");
        saveButton.setEnabled(false);
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 3;
        gridBagConstraints.ipady = 3;
        jPanel1.add(saveButton, gridBagConstraints);

        cancelButton.setText("Cancel");
        cancelButton.setEnabled(false);
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 3;
        gridBagConstraints.ipady = 3;
        jPanel1.add(cancelButton, gridBagConstraints);

        editButton.setText("Edit");
        editButton.setEnabled(false);
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 3;
        gridBagConstraints.ipady = 2;
        jPanel1.add(editButton, gridBagConstraints);

        dataList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        dataList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                dataListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(dataList);

        javax.swing.GroupLayout browsePanelLayout = new javax.swing.GroupLayout(browsePanel);
        browsePanel.setLayout(browsePanelLayout);
        browsePanelLayout.setHorizontalGroup(
            browsePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(browsePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(browsePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(browsePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, browsePanelLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(authorField, javax.swing.GroupLayout.DEFAULT_SIZE, 560, Short.MAX_VALUE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 616, Short.MAX_VALUE))
                .addContainerGap())
        );
        browsePanelLayout.setVerticalGroup(
            browsePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(browsePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(browsePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(browsePanelLayout.createSequentialGroup()
                        .addGroup(browsePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(authorField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, browsePanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(12, 12, 12))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(browsePanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(browsePanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    //<editor-fold defaultstate="collapsed" desc=" Editing ">
    private void setEditing(boolean b) {
        saveButton.setEnabled(b);
        cancelButton.setEnabled(b);
        //authorField.setEditable(b);
        textArea.setEditable(b);
        dataList.setEnabled(!b);
    }
    
    private void setEditable(boolean b) {
        editButton.setEnabled(b);
        deleteButton.setEnabled(b);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Buttons ">
    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        try {
            FileWriter writer = new FileWriter(((WhatNewItem) dataList.getSelectedValue()).getFile(),false);//GEN-LAST:event_saveButtonActionPerformed
            writer.write(textArea.getText());
            writer.close();
        } catch (IOException ex) {
            Displayer.ddisplay(2008092500001L, ex, "WhatNewFrame", "Error in saving data.");
        }
        ((WhatNewItem) dataList.getSelectedValue()).renewContent();
        setEditing(false);
}                                          

    private void newButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newButtonActionPerformed
        Calendar calendar = Calendar.getInstance();
        String m = "" + (calendar.get(Calendar.MONTH)+1);
        if(m.length()==1) m = "0"+m;
        String y = "" + calendar.get(Calendar.YEAR);
        y = y.substring(2);
        String d = "" + (calendar.get(Calendar.DAY_OF_MONTH));
        if(d.length()==1) d = "0"+d;
        try {
            File newFile = new File(base.getPath()+ "/_"+y);
            newFile.mkdir();
            newFile = new File(base.getPath()+ "/_"+y+"/_"+m);
            newFile.mkdir();
            newFile = new File(base.getPath()+ "/_"+y+"/_"+m+"/_"+d+"_"+System.getProperty("user.name"));
            newFile.createNewFile();
            //"/_"+m+"/_"+d+"_"+System.getProperty("user.name"));
            //newFile.createNewFile();
            readFiles();
        } catch (IOException ex) {
            Displayer.ddisplay(2008092500000L, ex, "WhatNewFrame", "It is not possible to create desired file.");
        }
        
    }//GEN-LAST:event_newButtonActionPerformed

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        setEditing(true);
}//GEN-LAST:event_editButtonActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        int i = JOptionPane.showConfirmDialog(this, "Are you really really sure?", "Delete item?", JOptionPane.YES_NO_OPTION);
        if(i == JOptionPane.YES_OPTION) {
            ((WhatNewItem)dataList.getSelectedValue()).getFile().delete();
            readFiles();
        }
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        setEditing(false);
        textArea.setText(
            ((WhatNewItem)(dataList.getSelectedValue())).getContent()
        );
    }//GEN-LAST:event_cancelButtonActionPerformed
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc=" List Event ">
    private void dataListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_dataListValueChanged
        if(!VisNow.get().isDevelopment()) return;
        if(dataList.isSelectionEmpty()) {
            setEditable(false);
            return;
        }
        textArea.setText(
                ((WhatNewItem)(dataList.getSelectedValue())).getContent()
                );
        authorField.setText(((WhatNewItem)(dataList.getSelectedValue())).getAuthor());
        
        
        setEditable(((WhatNewItem)dataList.getSelectedValue()).getAuthor().equals(System.getProperty("user.name"))  );
        
        
}//GEN-LAST:event_dataListValueChanged
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc=" Main ">
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new WhatNewFrame().setVisible(true);
            }
        });
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc=" [VAR] ">
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField authorField;
    private javax.swing.JPanel browsePanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JList dataList;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton editButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton newButton;
    private javax.swing.JButton saveButton;
    private javax.swing.JTextArea textArea;
    // End of variables declaration//GEN-END:variables
    //</editor-fold>
}
