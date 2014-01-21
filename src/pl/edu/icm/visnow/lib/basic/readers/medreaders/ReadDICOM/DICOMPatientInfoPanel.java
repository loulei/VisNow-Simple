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

package pl.edu.icm.visnow.lib.basic.readers.medreaders.ReadDICOM;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTree;
import com.pixelmed.dicom.DicomDirectoryRecord;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.TagFromName;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 *
 * @author babor
 */
public class DICOMPatientInfoPanel extends javax.swing.JPanel {
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    private DicomDirectoryRecord ddr = null;
    private DicomInfoFrame infoFrame;

    /** Creates new form DICOMPatientInfoPanel */
    public DICOMPatientInfoPanel(DicomInfoFrame infoFrame) {
        this.infoFrame = infoFrame;
        initComponents();
    }

    public void setDicomDirectoryRecord(DicomDirectoryRecord ddr) {
        this.ddr = ddr;
        try {
            infoFrame.setTreeModel(new AttributeTree(ddr.getAttributeList()));
        } catch (DicomException ex) {
            infoFrame.setTreeModel(new DefaultTreeModel(new DefaultMutableTreeNode()));
        }

        AttributeList atl = ddr.getAttributeList();
        Attribute att;
        String patientId = "<n/a>";
        att = atl.get(TagFromName.PatientID);
        if(att != null) {
            patientId = att.getSingleStringValueOrDefault("<n/a>");
        }

        String patientName = "<n/a>";
        att = atl.get(TagFromName.PatientName);
        if(att != null) {
            patientName = att.getSingleStringValueOrDefault("<n/a>").replaceAll("^", " ").trim();
        }

        String patientSex = "<n/a>";
        att = atl.get(TagFromName.PatientSex);
        if(att != null) {
            patientSex = att.getSingleStringValueOrDefault("<n/a>");
        }

        Date patientBirthDate = null;
        att = atl.get(TagFromName.PatientBirthDate);
        if(att != null) {
            SimpleDateFormat parseDF = new SimpleDateFormat("yyyyMMdd");
            String tmp = att.getSingleStringValueOrEmptyString();
            if(tmp != null && tmp.length() > 0) {
                try {
                    patientBirthDate = parseDF.parse(tmp);
                } catch (ParseException ex) {
                    patientBirthDate = null;
                }
            }
        }

        String patientAge = "<n/a>";
        att = atl.get(TagFromName.PatientAge);
        if(att != null) {
            patientAge = att.getSingleStringValueOrDefault("<n/a>");
        }

        setPatientData(patientId, patientName, patientSex, patientBirthDate, patientAge, ddr.getChildCount());
    }

    private void setPatientData(String id, String name, String sex, Date birthDate, String age, int nStudies) {
        patientIdLabel.setText(id);
        patientNameLabel.setText(name);
        patientSexLabel.setText(sex);
        if(birthDate != null)
            patientBirthDateLabel.setText(df.format(birthDate));
        else
            patientBirthDateLabel.setText("<n/a>");
        patientAgeLabel.setText(age);
        studiesLabel.setText(""+nStudies);        
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

        jLabel1 = new javax.swing.JLabel();
        patientNameLabel = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        patientSexLabel = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        patientBirthDateLabel = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        patientAgeLabel = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        studiesLabel = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        patientIdLabel = new javax.swing.JLabel();
        infoButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("Entry type:  PATIENT");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(35, 10, 10, 10);
        add(jLabel1, gridBagConstraints);

        patientNameLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        patientNameLabel.setText("<n/a>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 5, 0);
        add(patientNameLabel, gridBagConstraints);

        jLabel3.setText("Patient name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 5, 0);
        add(jLabel3, gridBagConstraints);

        jLabel2.setText("Patient sex:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 5, 0);
        add(jLabel2, gridBagConstraints);

        patientSexLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        patientSexLabel.setText("<n/a>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 5, 0);
        add(patientSexLabel, gridBagConstraints);

        jLabel5.setText("Patient birth date:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 5, 0);
        add(jLabel5, gridBagConstraints);

        patientBirthDateLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        patientBirthDateLabel.setText("<n/a>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 5, 0);
        add(patientBirthDateLabel, gridBagConstraints);

        jLabel7.setText("Patient age:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 5, 0);
        add(jLabel7, gridBagConstraints);

        patientAgeLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        patientAgeLabel.setText("<n/a>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 5, 0);
        add(patientAgeLabel, gridBagConstraints);

        jLabel9.setText("No. of studies:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 20, 5, 0);
        add(jLabel9, gridBagConstraints);

        studiesLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        studiesLabel.setText("<n/a>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 20, 5, 0);
        add(studiesLabel, gridBagConstraints);

        jLabel4.setText("Patient ID:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 5, 0);
        add(jLabel4, gridBagConstraints);

        patientIdLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        patientIdLabel.setText("<n/a>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 5, 0);
        add(patientIdLabel, gridBagConstraints);

        infoButton.setText("Show full info");
        infoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                infoButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(35, 5, 5, 10);
        add(infoButton, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void infoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_infoButtonActionPerformed
        JFrame frame = (JFrame) SwingUtilities.getRoot(this);
        infoFrame.setLocation(frame.getLocation().x + frame.getWidth(), frame.getLocation().y);
        infoFrame.setVisible(true);
    }//GEN-LAST:event_infoButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton infoButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel patientAgeLabel;
    private javax.swing.JLabel patientBirthDateLabel;
    private javax.swing.JLabel patientIdLabel;
    private javax.swing.JLabel patientNameLabel;
    private javax.swing.JLabel patientSexLabel;
    private javax.swing.JLabel studiesLabel;
    // End of variables declaration//GEN-END:variables

}
