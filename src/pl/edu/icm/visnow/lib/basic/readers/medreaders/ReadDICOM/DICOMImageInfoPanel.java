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

import com.pixelmed.dicom.*;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 *
 * @author babor
 */
public class DICOMImageInfoPanel extends javax.swing.JPanel {
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    private DicomDirectoryRecord ddr = null;
    private DicomInfoFrame infoFrame;

    /** Creates new form DICOMPatientInfoPanel */
    public DICOMImageInfoPanel(DicomInfoFrame infoFrame) {
        this.infoFrame = infoFrame;
        initComponents();
    }

    public void setDicomDirectoryRecord(DicomDirectoryRecord ddr, String parentDirPath) {
        this.ddr = ddr;
        Vector infoNameList = DicomDirectory.findAllContainedReferencedFileNames(ddr, parentDirPath);
        if(infoNameList != null && infoNameList.size() > 0) {
            File f = new File((String)infoNameList.get(0));
            if(!f.exists()) {
                String wrongPath = (String)infoNameList.get(0);
                if(!wrongPath.contains(parentDirPath)) {
                    return;
                }
                String relPath = wrongPath.replace(parentDirPath, "");
                f = new File(parentDirPath+relPath.toLowerCase());
                if(!f.exists())
                    return;
            }

            AttributeList atl = new AttributeList();
            try {
                //atl.read(f.getAbsolutePath(), null, true, true);
                atl.read(f, TagFromName.PixelData);
                infoFrame.setTreeModel(new AttributeTree(atl));
            } catch (Exception ex) {
                infoFrame.setTreeModel(new DefaultTreeModel(new DefaultMutableTreeNode()));
            }

            Attribute att;
            int cols = -1;
            att = atl.get(TagFromName.Columns);
            if(att != null) {
                cols = att.getSingleIntegerValueOrDefault(-1);
            }

            int rows = -1;
            att = atl.get(TagFromName.Rows);
            if(att != null) {
                rows = att.getSingleIntegerValueOrDefault(-1);
            }

            
            int[] size = {64,64};
            if(cols != rows) {
                if(cols > rows) {
                    size[0] = 64;
                    size[1] = (int)(64 * (double)rows/(double)cols);
                } else {
                    size[1] = 64;
                    size[0] = (int)(64 * (double)cols/(double)rows);
                }
            }

            BufferedImage img = DicomUtils.getDicomThumbnailFromAttributeList(atl, size);
            if(img == null) {
                img = new BufferedImage(64, 64, BufferedImage.TYPE_BYTE_GRAY);
                Graphics2D g2d = (Graphics2D)img.getGraphics();
                g2d.setColor(Color.BLACK);
                g2d.fillRect(0, 0, 64, 64);
                g2d.setColor(Color.WHITE);
                g2d.drawLine(8, 8, 56, 56);
                g2d.drawLine(8, 56, 56, 8);
            }
            imagePanel.setImage(img);

            Date acquisitionDate = null;
            att = atl.get(TagFromName.AcquisitionDate);
            if(att != null) {
                SimpleDateFormat parseDF = new SimpleDateFormat("yyyyMMdd");
                String tmp = att.getSingleStringValueOrEmptyString();
                if(tmp != null && tmp.length() > 0) {
                    try {
                        acquisitionDate = parseDF.parse(tmp);
                    } catch (ParseException ex) {
                        acquisitionDate = null;
                    }
                }
            }

            String photometricInterpretation = "<n/a>";
            att = atl.get(TagFromName.PhotometricInterpretation);
            if(att != null) {
                photometricInterpretation = att.getSingleStringValueOrDefault("<n/a>");
            }

            String imageType = "<n/a>";
            att = atl.get(TagFromName.ImageType);
            if(att != null) {
                imageType = att.getSingleStringValueOrDefault("<n/a>");
            }

            String modality = "<n/a>";
            att = atl.get(TagFromName.Modality);
            if(att != null) {
                modality = att.getSingleStringValueOrDefault("<n/a>");
            }

            String pixelSpacing = "<n/a>";
            att = atl.get(TagFromName.PixelSpacing);
            if(att != null) {
                pixelSpacing = att.getDelimitedStringValuesOrDefault("<n/a>");
            }

            String sliceLocation = "<n/a>";
            att = atl.get(TagFromName.SliceLocation);
            if(att != null) {
                double v = att.getSingleDoubleValueOrDefault(Double.NEGATIVE_INFINITY);
                if(v != Double.NEGATIVE_INFINITY)
                    sliceLocation = ""+v;
            }

            String sliceThickness = "<n/a>";
            att = atl.get(TagFromName.SliceThickness);
            if(att != null) {
                double v = att.getSingleDoubleValueOrDefault(Double.NEGATIVE_INFINITY);
                if(v != Double.NEGATIVE_INFINITY)
                    sliceThickness = ""+v;
            }
            
            int nFrames = 1;
            att = atl.get(TagFromName.NumberOfFrames);
            if(att != null) {
                nFrames = att.getSingleIntegerValueOrDefault(1);
            }            
            setImageData(cols, rows, acquisitionDate, photometricInterpretation, imageType, modality, pixelSpacing, sliceLocation, sliceThickness, nFrames);
        }
    }

    private void setImageData(int cols, int rows, Date acquisitionDate, String photometricInterpretation, String imageType, String modality, String pixelSpacing, String sliceLocation, String sliceThickness, int nFrames) {
        imageSizeLabel.setText(""+cols+"x"+rows);
        if(acquisitionDate == null)
            acquisitionDateLabel.setText("<n/a>");
        else
            acquisitionDateLabel.setText(df.format(acquisitionDate));
        photometricInterpretationLabel.setText(photometricInterpretation);
        imageTypeLabel.setText(imageType);
        modalityLabel.setText(modality);
        pixelSpacingLabel.setText(pixelSpacing);
        sliceLocationLabel.setText(sliceLocation);
        sliceThicknessLabel.setText(sliceThickness);
        numberOfFramesLabel.setText(""+nFrames);
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
        jLabel4 = new javax.swing.JLabel();
        imageSizeLabel = new javax.swing.JLabel();
        infoButton = new javax.swing.JButton();
        imagePanel = new pl.edu.icm.visnow.gui.widgets.ImagePanel();
        jLabel2 = new javax.swing.JLabel();
        acquisitionDateLabel = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        photometricInterpretationLabel = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        imageTypeLabel = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        modalityLabel = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        pixelSpacingLabel = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        sliceLocationLabel = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        sliceThicknessLabel = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        numberOfFramesLabel = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("Entry type:  IMAGE");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(35, 10, 10, 10);
        add(jLabel1, gridBagConstraints);

        jLabel4.setText("Image size:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 5, 0);
        add(jLabel4, gridBagConstraints);

        imageSizeLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        imageSizeLabel.setText("<n/a>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 5, 0);
        add(imageSizeLabel, gridBagConstraints);

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

        imagePanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        imagePanel.setMaximumSize(new java.awt.Dimension(64, 64));
        imagePanel.setMinimumSize(new java.awt.Dimension(64, 64));
        imagePanel.setPreferredSize(new java.awt.Dimension(64, 64));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 25, 5, 5);
        add(imagePanel, gridBagConstraints);

        jLabel2.setText("Acquisition date:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 5, 0);
        add(jLabel2, gridBagConstraints);

        acquisitionDateLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        acquisitionDateLabel.setText("<n/a>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 5, 0);
        add(acquisitionDateLabel, gridBagConstraints);

        jLabel5.setText("Photometric interpretation:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 5, 0);
        add(jLabel5, gridBagConstraints);

        photometricInterpretationLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        photometricInterpretationLabel.setText("<n/a>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 5, 0);
        add(photometricInterpretationLabel, gridBagConstraints);

        jLabel7.setText("Image type:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 5, 0);
        add(jLabel7, gridBagConstraints);

        imageTypeLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        imageTypeLabel.setText("<n/a>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 5, 0);
        add(imageTypeLabel, gridBagConstraints);

        jLabel9.setText("Modality:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 5, 0);
        add(jLabel9, gridBagConstraints);

        modalityLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        modalityLabel.setText("<n/a>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 5, 0);
        add(modalityLabel, gridBagConstraints);

        jLabel11.setText("Pixel spacing:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 5, 0);
        add(jLabel11, gridBagConstraints);

        pixelSpacingLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        pixelSpacingLabel.setText("<n/a>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 5, 0);
        add(pixelSpacingLabel, gridBagConstraints);

        jLabel13.setText("Slice location:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 5, 0);
        add(jLabel13, gridBagConstraints);

        sliceLocationLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        sliceLocationLabel.setText("<n/a>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 5, 0);
        add(sliceLocationLabel, gridBagConstraints);

        jLabel15.setText("Slice thickness:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 5, 0);
        add(jLabel15, gridBagConstraints);

        sliceThicknessLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        sliceThicknessLabel.setText("<n/a>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 5, 0);
        add(sliceThicknessLabel, gridBagConstraints);

        jLabel6.setText("Number of frames:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 20, 5, 0);
        add(jLabel6, gridBagConstraints);

        numberOfFramesLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        numberOfFramesLabel.setText("<n/a>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 20, 5, 0);
        add(numberOfFramesLabel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void infoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_infoButtonActionPerformed
        JFrame frame = (JFrame) SwingUtilities.getRoot(this);
        infoFrame.setLocation(frame.getLocation().x + frame.getWidth(), frame.getLocation().y);
        infoFrame.setVisible(true);
    }//GEN-LAST:event_infoButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel acquisitionDateLabel;
    private pl.edu.icm.visnow.gui.widgets.ImagePanel imagePanel;
    private javax.swing.JLabel imageSizeLabel;
    private javax.swing.JLabel imageTypeLabel;
    private javax.swing.JButton infoButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel modalityLabel;
    private javax.swing.JLabel numberOfFramesLabel;
    private javax.swing.JLabel photometricInterpretationLabel;
    private javax.swing.JLabel pixelSpacingLabel;
    private javax.swing.JLabel sliceLocationLabel;
    private javax.swing.JLabel sliceThicknessLabel;
    // End of variables declaration//GEN-END:variables

}
