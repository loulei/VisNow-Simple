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
import com.pixelmed.dicom.DicomDirectory;
import com.pixelmed.dicom.DicomDirectoryRecord;
import com.pixelmed.dicom.DicomDirectoryRecordFactory;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.DicomFileUtilities;
import com.pixelmed.dicom.DicomInputStream;
import com.pixelmed.dicom.TagFromName;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeModelListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import pl.edu.icm.visnow.engine.core.ParameterChangeListener;
import pl.edu.icm.visnow.system.main.VisNow;

/**
 *
 * 
 * 
 */
public class GUI extends javax.swing.JPanel implements ParameterChangeListener {

    private JFileChooser dataFileChooser = new JFileChooser();
    private String lastPath = null;
    private Params params = null;
    private boolean silent = false;
    private DefaultListModel lm = new DefaultListModel();
    private DicomContentFrame frame = new DicomContentFrame();
    private String fileName = "";

    private FileFilter dicomdirFileFilter = new FileFilter() {
        @Override
        public boolean accept(File f) {
            if (f.isFile()) {
                String path = f.getAbsolutePath();
                String cmp = path.substring(path.lastIndexOf(File.separator) + 1);
                return cmp.equalsIgnoreCase("dicomdir");
            } else {
                return true;
            }
        }

        @Override
        public String getDescription() {
            String desc = "DICOMDIR file";
            return desc;
        }
    };

    /** Creates new form GUI */
    public GUI() {
        initComponents();
        dataFileChooser.setLocation(0,0);
        fileListList.setModel(lm);
        silent = true;
        updateGUI();
        silent = false;

        frame.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent evt) {
                if (frame.getCurrentNameList() != null) {
                    if(frame.getSelectedString() != null && frame.getSelectedString().length() > 1) {
                        if(fileName.length() < 1 && frame.getCurrentNameList().size() == 1) {
                            params.setInfoString(""+frame.getCurrentNameList().get(0)+""+frame.getSelectedString());
                        } else if(fileName.length() > 0) {
                            params.setInfoString(""+fileName+""+frame.getSelectedString());
                        } else {
                            params.setInfoString(""+params.getDirPath()+""+frame.getSelectedString());
                        }
                    }
                    params.setReadAsVolume(true);
                    params.setFramesAsTime(frame.isFramesAsTime());
                    if(frame.isFramesAsTime())
                        params.setFramesRange(frame.getFramesRange());
                    else
                        params.setFramesRange(null);
                    params.setFileList(frame.getCurrentNameList());                    
                }
            }
        });

        downsizeUI.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent evt) {
                if (!silent) {
                    params.setDownsize(downsizeUI.getDownsize());
                }
            }
        });
    }

    public void setParams(Params p) {
        this.params = p;
        this.params.addParameterChangelistener(this);
        silent = true;
        updateGUI();
        silent = false;
    }

    private void updateGUI() {
        lm.removeAllElements();
        lowCropTF.setEnabled(false);
        highCropTF.setEnabled(false);
        inpaintMissingSlicesCB.setEnabled(false);
        if (params == null) {
            bytesDataRB.setEnabled(false);
            autoDataRB.setEnabled(false);
            histoDataRB.setEnabled(false);
            windowDataRB.setEnabled(false);
            readAsStackCB.setEnabled(false);
            return;
        }

        bytesDataRB.setEnabled(true);
        autoDataRB.setEnabled(true);
        histoDataRB.setEnabled(true);
        windowDataRB.setEnabled(true);
        inpaintMissingSlicesCB.setEnabled(true);
        readAsStackCB.setEnabled(true);


        ArrayList<String> list = params.getFileList();
        if (list.size() > 0) {
            lm.addElement("Directory:");
            lm.addElement(params.getDirPath());
            lm.addElement(" ");
            lm.addElement("Files:");
            String tmp;
            for (int i = 0; i < list.size(); i++) {
                tmp = list.get(i);
                if(tmp != null && !tmp.isEmpty()) {
                    String tmp2 = tmp.substring(tmp.lastIndexOf(File.separator) + 1);
                    if(tmp2 != null && !tmp2.isEmpty())
                        lm.addElement(tmp2);                    
                }                
            }
        }

        switch (params.getReadAs()) {
            case Params.READ_AS_AUTO:
                autoDataRB.setSelected(true);
                break;
            case Params.READ_AS_BYTES:
                bytesDataRB.setSelected(true);
                lowCropTF.setEnabled(true);
                lowCropTF.setText("" + params.getLow());
                highCropTF.setEnabled(true);
                highCropTF.setText("" + params.getHigh());
                break;
            case Params.READ_AS_HISTOGRAM:
                histoDataRB.setSelected(true);
                lowCropTF.setEnabled(true);
                lowCropTF.setText("" + params.getLow());
                break;
            case Params.READ_AS_WINDOW:
                windowDataRB.setSelected(true);
                break;
        }

        interpolateDataCB.setSelected(params.isInterpolateData());
        interpolateManualTF.setEnabled(false);
        if(params.isInterpolateData()) {
            interpolateManualRB.setEnabled(true);
            interpolatePixelSizeRB.setEnabled(true);
            interpolateSliceDistRB.setEnabled(true);
            switch(params.getInterpolateDataVoxelSizeFrom()) {
                case Params.VOXELSIZE_FROM_MANUALVALUE:
                    interpolateManualRB.setSelected(true);
                    interpolateManualTF.setText(""+params.getInterpolateDataVoxelSizeManualValue());
                    interpolateManualTF.setEnabled(true);
                    break;
                case Params.VOXELSIZE_FROM_PIXELSIZE:
                    interpolatePixelSizeRB.setSelected(true);
                    break;
                case Params.VOXELSIZE_FROM_SLICESDISTANCE:
                    interpolateSliceDistRB.setSelected(true);
                    break;
            }
        } else {
            interpolateManualRB.setEnabled(false);
            interpolatePixelSizeRB.setEnabled(false);
            interpolateSliceDistRB.setEnabled(false);
            interpolateManualTF.setEnabled(false);
        }


        inpaintMissingSlicesCB.setSelected(params.isInpaintMissingSlices());
        downsizeUI.setDownsize(params.getDownsize());
        
        readAsStackCB.setSelected(params.isIgnoreOrientation());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
   // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
   private void initComponents()
   {
      java.awt.GridBagConstraints gridBagConstraints;

      buttonGroup1 = new javax.swing.ButtonGroup();
      buttonGroup2 = new javax.swing.ButtonGroup();
      openDICOMDIRButton = new javax.swing.JButton();
      openDICOMFilesButton = new javax.swing.JButton();
      infoPanel = new javax.swing.JPanel();
      jScrollPane1 = new javax.swing.JScrollPane();
      fileListList = new javax.swing.JList();
      readAsPanel = new javax.swing.JPanel();
      autoDataRB = new javax.swing.JRadioButton();
      bytesDataRB = new javax.swing.JRadioButton();
      histoDataRB = new javax.swing.JRadioButton();
      windowDataRB = new javax.swing.JRadioButton();
      jPanel1 = new javax.swing.JPanel();
      jLabel1 = new javax.swing.JLabel();
      jLabel2 = new javax.swing.JLabel();
      lowCropTF = new javax.swing.JTextField();
      highCropTF = new javax.swing.JTextField();
      interpolatePanel = new javax.swing.JPanel();
      interpolateDataCB = new javax.swing.JCheckBox();
      interpolatePixelSizeRB = new javax.swing.JRadioButton();
      interpolateSliceDistRB = new javax.swing.JRadioButton();
      interpolateManualRB = new javax.swing.JRadioButton();
      interpolateManualTF = new javax.swing.JTextField();
      jLabel3 = new javax.swing.JLabel();
      inpaintMissingSlicesCB = new javax.swing.JCheckBox();
      sliceDenisingLevelSlider = new javax.swing.JSlider();
      downsizeUI = new pl.edu.icm.visnow.lib.gui.DownsizeUI();
      histoPanel = new javax.swing.JPanel();
      histoArea = new pl.edu.icm.visnow.lib.gui.HistoArea();
      applyButton = new javax.swing.JButton();
      histoLogCB = new javax.swing.JCheckBox();
      showDicomWindowButton = new javax.swing.JButton();
      readAsStackCB = new javax.swing.JCheckBox();

      setMinimumSize(new java.awt.Dimension(180, 820));
      setOpaque(false);
      setPreferredSize(new java.awt.Dimension(200, 850));
      setRequestFocusEnabled(false);
      setLayout(new java.awt.GridBagLayout());

      openDICOMDIRButton.setText("Open DICOMDIR");
      openDICOMDIRButton.setMinimumSize(new java.awt.Dimension(94, 20));
      openDICOMDIRButton.setPreferredSize(new java.awt.Dimension(94, 22));
      openDICOMDIRButton.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            openDICOMDIRButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 6);
      add(openDICOMDIRButton, gridBagConstraints);

      openDICOMFilesButton.setText("Open DICOM file(s)");
      openDICOMFilesButton.setMinimumSize(new java.awt.Dimension(94, 20));
      openDICOMFilesButton.setPreferredSize(new java.awt.Dimension(94, 22));
      openDICOMFilesButton.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            openDICOMFilesButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new java.awt.Insets(5, 5, 7, 6);
      add(openDICOMFilesButton, gridBagConstraints);

      infoPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("File(s) info"));
      infoPanel.setMinimumSize(new java.awt.Dimension(200, 130));
      infoPanel.setPreferredSize(new java.awt.Dimension(200, 130));
      infoPanel.setLayout(new java.awt.BorderLayout());

      fileListList.setBackground(java.awt.SystemColor.window);
      fileListList.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
      fileListList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
      jScrollPane1.setViewportView(fileListList);

      infoPanel.add(jScrollPane1, java.awt.BorderLayout.CENTER);

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 3;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
      gridBagConstraints.weightx = 1.0;
      add(infoPanel, gridBagConstraints);

      readAsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Read data type:"));
      readAsPanel.setLayout(new java.awt.GridBagLayout());

      buttonGroup1.add(autoDataRB);
      autoDataRB.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
      autoDataRB.setSelected(true);
      autoDataRB.setText("auto");
      autoDataRB.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            autoDataRBActionPerformed(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
      gridBagConstraints.weightx = 1.0;
      readAsPanel.add(autoDataRB, gridBagConstraints);

      buttonGroup1.add(bytesDataRB);
      bytesDataRB.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
      bytesDataRB.setText("bytes");
      bytesDataRB.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            bytesDataRBActionPerformed(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
      gridBagConstraints.weightx = 1.0;
      readAsPanel.add(bytesDataRB, gridBagConstraints);

      buttonGroup1.add(histoDataRB);
      histoDataRB.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
      histoDataRB.setText("histogram equalization");
      histoDataRB.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            histoDataRBActionPerformed(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
      readAsPanel.add(histoDataRB, gridBagConstraints);

      buttonGroup1.add(windowDataRB);
      windowDataRB.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
      windowDataRB.setText("DICOM embeded window");
      windowDataRB.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            windowDataRBActionPerformed(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 3;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
      readAsPanel.add(windowDataRB, gridBagConstraints);

      jPanel1.setLayout(new java.awt.GridBagLayout());

      jLabel1.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
      jLabel1.setText("Low crop:");
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
      jPanel1.add(jLabel1, gridBagConstraints);

      jLabel2.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
      jLabel2.setText("High crop:");
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
      jPanel1.add(jLabel2, gridBagConstraints);

      lowCropTF.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
      lowCropTF.setEnabled(false);
      lowCropTF.setMinimumSize(new java.awt.Dimension(40, 19));
      lowCropTF.setPreferredSize(new java.awt.Dimension(40, 19));
      lowCropTF.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            lowCropTFActionPerformed(evt);
         }
      });
      lowCropTF.addFocusListener(new java.awt.event.FocusAdapter()
      {
         public void focusLost(java.awt.event.FocusEvent evt)
         {
            lowCropTFFocusLost(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
      jPanel1.add(lowCropTF, gridBagConstraints);

      highCropTF.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
      highCropTF.setEnabled(false);
      highCropTF.setMinimumSize(new java.awt.Dimension(40, 19));
      highCropTF.setPreferredSize(new java.awt.Dimension(40, 19));
      highCropTF.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            highCropTFActionPerformed(evt);
         }
      });
      highCropTF.addFocusListener(new java.awt.event.FocusAdapter()
      {
         public void focusLost(java.awt.event.FocusEvent evt)
         {
            highCropTFFocusLost(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 3;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
      jPanel1.add(highCropTF, gridBagConstraints);

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridheight = 4;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.weightx = 1.0;
      readAsPanel.add(jPanel1, gridBagConstraints);

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 4;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
      gridBagConstraints.weightx = 1.0;
      add(readAsPanel, gridBagConstraints);

      interpolatePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Data interpolation"));
      interpolatePanel.setLayout(new java.awt.GridBagLayout());

      interpolateDataCB.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
      interpolateDataCB.setText("interpolate data to regular mesh");
      interpolateDataCB.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            interpolateDataCBActionPerformed(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.gridwidth = 3;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
      gridBagConstraints.weightx = 1.0;
      interpolatePanel.add(interpolateDataCB, gridBagConstraints);

      buttonGroup2.add(interpolatePixelSizeRB);
      interpolatePixelSizeRB.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
      interpolatePixelSizeRB.setText("pixel size");
      interpolatePixelSizeRB.setMaximumSize(new java.awt.Dimension(68, 16));
      interpolatePixelSizeRB.setMinimumSize(new java.awt.Dimension(68, 16));
      interpolatePixelSizeRB.setPreferredSize(new java.awt.Dimension(68, 16));
      interpolatePixelSizeRB.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            interpolatePixelSizeRBActionPerformed(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.gridwidth = 3;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
      gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 0);
      interpolatePanel.add(interpolatePixelSizeRB, gridBagConstraints);

      buttonGroup2.add(interpolateSliceDistRB);
      interpolateSliceDistRB.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
      interpolateSliceDistRB.setText("slice distance");
      interpolateSliceDistRB.setMaximumSize(new java.awt.Dimension(88, 16));
      interpolateSliceDistRB.setMinimumSize(new java.awt.Dimension(88, 16));
      interpolateSliceDistRB.setName(""); // NOI18N
      interpolateSliceDistRB.setPreferredSize(new java.awt.Dimension(88, 16));
      interpolateSliceDistRB.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            interpolateSliceDistRBActionPerformed(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 3;
      gridBagConstraints.gridwidth = 3;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
      gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 0);
      interpolatePanel.add(interpolateSliceDistRB, gridBagConstraints);

      buttonGroup2.add(interpolateManualRB);
      interpolateManualRB.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
      interpolateManualRB.setText("manual");
      interpolateManualRB.setMaximumSize(new java.awt.Dimension(66, 16));
      interpolateManualRB.setMinimumSize(new java.awt.Dimension(61, 16));
      interpolateManualRB.setPreferredSize(new java.awt.Dimension(61, 16));
      interpolateManualRB.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            interpolateManualRBActionPerformed(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 4;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
      gridBagConstraints.weighty = 1.0;
      gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 0);
      interpolatePanel.add(interpolateManualRB, gridBagConstraints);

      interpolateManualTF.setMinimumSize(new java.awt.Dimension(4, 16));
      interpolateManualTF.setPreferredSize(new java.awt.Dimension(40, 16));
      interpolateManualTF.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            interpolateManualTFActionPerformed(evt);
         }
      });
      interpolateManualTF.addFocusListener(new java.awt.event.FocusAdapter()
      {
         public void focusLost(java.awt.event.FocusEvent evt)
         {
            interpolateManualTFFocusLost(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 4;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
      gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
      interpolatePanel.add(interpolateManualTF, gridBagConstraints);

      jLabel3.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
      jLabel3.setText("[mm]");
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridy = 4;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new java.awt.Insets(6, 2, 0, 5);
      interpolatePanel.add(jLabel3, gridBagConstraints);

      inpaintMissingSlicesCB.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
      inpaintMissingSlicesCB.setText("inpaint missing slices");
      inpaintMissingSlicesCB.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            inpaintMissingSlicesCBActionPerformed(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridwidth = 3;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
      interpolatePanel.add(inpaintMissingSlicesCB, gridBagConstraints);

      sliceDenisingLevelSlider.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
      sliceDenisingLevelSlider.setMajorTickSpacing(1);
      sliceDenisingLevelSlider.setMaximum(5);
      sliceDenisingLevelSlider.setMinorTickSpacing(1);
      sliceDenisingLevelSlider.setPaintLabels(true);
      sliceDenisingLevelSlider.setPaintTicks(true);
      sliceDenisingLevelSlider.setToolTipText("");
      sliceDenisingLevelSlider.setValue(0);
      sliceDenisingLevelSlider.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "slice denoising level", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
      sliceDenisingLevelSlider.addChangeListener(new javax.swing.event.ChangeListener()
      {
         public void stateChanged(javax.swing.event.ChangeEvent evt)
         {
            sliceDenisingLevelSliderStateChanged(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 5;
      gridBagConstraints.gridwidth = 3;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
      interpolatePanel.add(sliceDenisingLevelSlider, gridBagConstraints);

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 5;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
      gridBagConstraints.weightx = 1.0;
      add(interpolatePanel, gridBagConstraints);

      downsizeUI.setMinimumSize(new java.awt.Dimension(120, 70));
      downsizeUI.setPreferredSize(new java.awt.Dimension(200, 70));
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 6;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
      add(downsizeUI, gridBagConstraints);

      histoPanel.setLayout(new java.awt.BorderLayout());

      histoArea.setMinimumSize(new java.awt.Dimension(180, 120));
      histoArea.setPreferredSize(new java.awt.Dimension(200, 150));
      histoPanel.add(histoArea, java.awt.BorderLayout.CENTER);

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 9;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
      add(histoPanel, gridBagConstraints);

      applyButton.setText("Apply");
      applyButton.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            applyButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 8;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
      gridBagConstraints.insets = new java.awt.Insets(2, 3, 5, 3);
      add(applyButton, gridBagConstraints);

      histoLogCB.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
      histoLogCB.setText("log values histogram");
      histoLogCB.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            histoLogCBActionPerformed(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 10;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
      gridBagConstraints.weighty = 1.0;
      gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
      add(histoLogCB, gridBagConstraints);

      showDicomWindowButton.setText("Show DICOM window");
      showDicomWindowButton.setEnabled(false);
      showDicomWindowButton.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            showDicomWindowButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.insets = new java.awt.Insets(5, 5, 6, 5);
      add(showDicomWindowButton, gridBagConstraints);

      readAsStackCB.setText("read as stack (ignore orientation)");
      readAsStackCB.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            readAsStackCBActionPerformed(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 7;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
      gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
      add(readAsStackCB, gridBagConstraints);
   }// </editor-fold>//GEN-END:initComponents

   private void openDICOMDIRButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_openDICOMDIRButtonActionPerformed
   {//GEN-HEADEREND:event_openDICOMDIRButtonActionPerformed
       dataFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
       dataFileChooser.setMultiSelectionEnabled(false);
       dataFileChooser.setFileFilter(dicomdirFileFilter);

       if (lastPath == null) {
           dataFileChooser.setCurrentDirectory(new File( VisNow.get().getMainConfig().getUsableDataPath(ReadDICOM.class) ));
       } else {
           dataFileChooser.setCurrentDirectory(new File(lastPath));
       }

       int returnVal = dataFileChooser.showOpenDialog(this);
       if (returnVal == JFileChooser.APPROVE_OPTION) {
           fileName = dataFileChooser.getSelectedFile().getAbsolutePath();
           lastPath = fileName.substring(0, fileName.lastIndexOf(File.separator));
           VisNow.get().getMainConfig().setLastDataPath(lastPath, ReadDICOM.class);           
           if (fileName == null || fileName.length() < 1 || fileName.equals(" ")) {
               return;
           }

           File f = new File(fileName);
           if(!f.exists() || !f.getName().toLowerCase().equals("dicomdir")) {
               return;
           }

           if (!DicomFileUtilities.isDicomOrAcrNemaFile(fileName)) {
               System.out.println("INFO: Selected file is not a DICOM compatible file!");
               params.setFileList(new ArrayList<String>());
               return;
           }

           String dirPath = fileName.substring(0, fileName.lastIndexOf(File.separator));
           params.setDirPath(dirPath);

           AttributeList atl = new AttributeList();
           DicomDirectory dd;
           try {
               atl.read(fileName, null, true, true);
               dd = new DicomDirectory(atl);
               frame.setDicomTreeModel(dd, fileName.substring(0, fileName.lastIndexOf(File.separator)));
               showDicomWindowButton.setEnabled(true);
               frame.setVisible(true);
           } catch (IOException ex) {
               ex.printStackTrace();
           } catch (DicomException ex) {
               ex.printStackTrace();
           }
       } else {
           fileName = "";
           showDicomWindowButton.setEnabled(false);
       }
   }//GEN-LAST:event_openDICOMDIRButtonActionPerformed

   private void openDICOMFilesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openDICOMFilesButtonActionPerformed
       dataFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
       dataFileChooser.setMultiSelectionEnabled(true);
       dataFileChooser.resetChoosableFileFilters();
       if (lastPath == null) {
           dataFileChooser.setCurrentDirectory(new File( VisNow.get().getMainConfig().getUsableDataPath(ReadDICOM.class)));
       } else {
           dataFileChooser.setCurrentDirectory(new File(lastPath));
       }

       int returnVal = dataFileChooser.showOpenDialog(this);
       String dirPath = "";
       ArrayList<String> fileList = new ArrayList<String>();
       if (returnVal == JFileChooser.APPROVE_OPTION) {
           File[] files = dataFileChooser.getSelectedFiles();
           if (files == null || files.length < 1) {
               files = new File[1];
               files[0] = dataFileChooser.getSelectedFile();
           }
           lastPath = files[0].getAbsolutePath().substring(0, files[0].getAbsolutePath().lastIndexOf(File.separator));
           VisNow.get().getMainConfig().setLastDataPath(lastPath, ReadDICOM.class);
           dirPath = files[0].getAbsolutePath().substring(0, files[0].getAbsolutePath().lastIndexOf(File.separator));
           params.setDirPath(dirPath);

           if (files != null) {
               for (int i = 0; i < files.length; i++) {
                   if (!DicomFileUtilities.isDicomOrAcrNemaFile(files[i].getAbsolutePath())) {
                       System.out.println("INFO: File " + files[i].getAbsolutePath() + " is not a DICOM compatible file - skipping");
                       continue;
                   }
                  
                  if(!files[i].exists() || files[i].getName().toLowerCase().equals("dicomdir")) {
                        continue;
                  }

                   fileList.add(files[i].getAbsolutePath());
               }

               if(fileList.size() < 1)
                   return;
               
               try {
                   SimplifiedDicomDirectory sdd = null;
                   DicomDirectoryRecordFactory ddrf = new DicomDirectoryRecordFactory();
                   DicomDirectoryRecord[] images = new DicomDirectoryRecord[fileList.size()];
                   for (int i = 0; i < images.length; i++) {
                       File f = new File(fileList.get(i));
                       if(!f.exists()) {
                           images[i] = null;
                           continue;
                       }

                       AttributeList atl = new AttributeList();
                       DicomInputStream in = new DicomInputStream(f);
                       atl.read(in, TagFromName.PixelData);

                       Attribute att = atl.putNewAttribute(TagFromName.ReferencedFileID);
                       att.setValue(f.getName());

                       images[i] = ddrf.getNewImageDirectoryRecord(null, atl);
                   }

                   boolean one = false;
                   for (int i = 0; i < images.length; i++) {
                       if(images[i] != null) {
                           one = true;
                           break;
                       }
                   }
                   if(!one)
                       return;
                   
                   frame.setDicomTreeModel(new SimplifiedDicomDirectory(images, dirPath), dirPath);
                   showDicomWindowButton.setEnabled(true);
                   frame.setVisible(true);
               } catch (DicomException ex) {
                   ex.printStackTrace();
                   return;
               } catch (IOException ex) {
                   ex.printStackTrace();
                   return;
               }
           }
       } else {
           showDicomWindowButton.setEnabled(false);
       }
   }//GEN-LAST:event_openDICOMFilesButtonActionPerformed

   private void autoDataRBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoDataRBActionPerformed
       params.setReadAs(Params.READ_AS_AUTO);
   }//GEN-LAST:event_autoDataRBActionPerformed

   private void bytesDataRBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bytesDataRBActionPerformed
       params.setReadAs(Params.READ_AS_BYTES);
   }//GEN-LAST:event_bytesDataRBActionPerformed

   private void histoDataRBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_histoDataRBActionPerformed
       params.setReadAs(Params.READ_AS_HISTOGRAM);
   }//GEN-LAST:event_histoDataRBActionPerformed

   private void applyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyButtonActionPerformed
       params.fireStateChanged();
   }//GEN-LAST:event_applyButtonActionPerformed

   private void highCropTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_highCropTFActionPerformed
       try {
           int v = Integer.parseInt(highCropTF.getText());
           params.setHigh(v);
       } catch (Exception ex) {
           highCropTF.setText("" + params.getHigh());
       }
   }//GEN-LAST:event_highCropTFActionPerformed

   private void lowCropTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lowCropTFActionPerformed
       try {
           int v = Integer.parseInt(lowCropTF.getText());
           params.setLow(v);
       } catch (Exception ex) {
           lowCropTF.setText("" + params.getLow());
       }
   }//GEN-LAST:event_lowCropTFActionPerformed

   private void highCropTFFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_highCropTFFocusLost
       highCropTFActionPerformed(null);
   }//GEN-LAST:event_highCropTFFocusLost

   private void lowCropTFFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_lowCropTFFocusLost
       lowCropTFActionPerformed(null);
   }//GEN-LAST:event_lowCropTFFocusLost

   private void histoLogCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_histoLogCBActionPerformed
       histoArea.setLogScale(histoLogCB.isSelected());
   }//GEN-LAST:event_histoLogCBActionPerformed

   private void windowDataRBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_windowDataRBActionPerformed
       params.setReadAs(Params.READ_AS_WINDOW);
   }//GEN-LAST:event_windowDataRBActionPerformed

   private void showDicomWindowButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showDicomWindowButtonActionPerformed
        if(frame != null)
            frame.setVisible(true);
   }//GEN-LAST:event_showDicomWindowButtonActionPerformed

   private void interpolateDataCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_interpolateDataCBActionPerformed
        params.setInterpolateData(interpolateDataCB.isSelected());
   }//GEN-LAST:event_interpolateDataCBActionPerformed

   private void interpolatePixelSizeRBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_interpolatePixelSizeRBActionPerformed
        if(interpolatePixelSizeRB.isSelected())
            params.setInterpolateDataVoxelSizeFrom(Params.VOXELSIZE_FROM_PIXELSIZE);
   }//GEN-LAST:event_interpolatePixelSizeRBActionPerformed

   private void interpolateSliceDistRBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_interpolateSliceDistRBActionPerformed
        if(interpolateSliceDistRB.isSelected())
            params.setInterpolateDataVoxelSizeFrom(Params.VOXELSIZE_FROM_SLICESDISTANCE);
   }//GEN-LAST:event_interpolateSliceDistRBActionPerformed

   private void interpolateManualRBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_interpolateManualRBActionPerformed
        if(interpolateManualRB.isSelected())
            params.setInterpolateDataVoxelSizeFrom(Params.VOXELSIZE_FROM_MANUALVALUE);
   }//GEN-LAST:event_interpolateManualRBActionPerformed

   private void interpolateManualTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_interpolateManualTFActionPerformed
        try {
            float v = Float.parseFloat(interpolateManualTF.getText());
            params.setInterpolateDataVoxelSizeManualValue(v);
        } catch (NumberFormatException ex) {
            interpolateManualTF.setText(""+params.getInterpolateDataVoxelSizeManualValue());
        }
   }//GEN-LAST:event_interpolateManualTFActionPerformed

   private void interpolateManualTFFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_interpolateManualTFFocusLost
        interpolateManualTFActionPerformed(null);
   }//GEN-LAST:event_interpolateManualTFFocusLost

   private void inpaintMissingSlicesCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inpaintMissingSlicesCBActionPerformed
        params.setInpaintMissingSlices(inpaintMissingSlicesCB.isSelected());
   }//GEN-LAST:event_inpaintMissingSlicesCBActionPerformed

    private void readAsStackCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_readAsStackCBActionPerformed
        params.setIgnoreOrientation(readAsStackCB.isSelected());
    }//GEN-LAST:event_readAsStackCBActionPerformed

   private void sliceDenisingLevelSliderStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_sliceDenisingLevelSliderStateChanged
   {//GEN-HEADEREND:event_sliceDenisingLevelSliderStateChanged
      params.setSliceDenoisingLevel(sliceDenisingLevelSlider.getValue());
   }//GEN-LAST:event_sliceDenisingLevelSliderStateChanged

   // Variables declaration - do not modify//GEN-BEGIN:variables
   private javax.swing.JButton applyButton;
   private javax.swing.JRadioButton autoDataRB;
   private javax.swing.ButtonGroup buttonGroup1;
   private javax.swing.ButtonGroup buttonGroup2;
   private javax.swing.JRadioButton bytesDataRB;
   private pl.edu.icm.visnow.lib.gui.DownsizeUI downsizeUI;
   private javax.swing.JList fileListList;
   private javax.swing.JTextField highCropTF;
   private pl.edu.icm.visnow.lib.gui.HistoArea histoArea;
   private javax.swing.JRadioButton histoDataRB;
   private javax.swing.JCheckBox histoLogCB;
   private javax.swing.JPanel histoPanel;
   private javax.swing.JPanel infoPanel;
   private javax.swing.JCheckBox inpaintMissingSlicesCB;
   private javax.swing.JCheckBox interpolateDataCB;
   private javax.swing.JRadioButton interpolateManualRB;
   private javax.swing.JTextField interpolateManualTF;
   private javax.swing.JPanel interpolatePanel;
   private javax.swing.JRadioButton interpolatePixelSizeRB;
   private javax.swing.JRadioButton interpolateSliceDistRB;
   private javax.swing.JLabel jLabel1;
   private javax.swing.JLabel jLabel2;
   private javax.swing.JLabel jLabel3;
   private javax.swing.JPanel jPanel1;
   private javax.swing.JScrollPane jScrollPane1;
   private javax.swing.JTextField lowCropTF;
   private javax.swing.JButton openDICOMDIRButton;
   private javax.swing.JButton openDICOMFilesButton;
   private javax.swing.JPanel readAsPanel;
   private javax.swing.JCheckBox readAsStackCB;
   private javax.swing.JButton showDicomWindowButton;
   private javax.swing.JSlider sliceDenisingLevelSlider;
   private javax.swing.JRadioButton windowDataRB;
   // End of variables declaration//GEN-END:variables

    public void parameterChanged(String name) {
        silent = true;
        updateGUI();
        silent = false;
    }
//    /**
//     * Utility field holding list of ChangeListeners.
//     */
//    private transient ArrayList<ChangeListener> changeListenerList = new ArrayList<ChangeListener>();
//
//    /**
//     * Registers ChangeListener to receive events.
//     * @param listener The listener to register.
//     */
//    public synchronized void addChangeListener(ChangeListener listener) {
//        changeListenerList.add(listener);
//    }
//
//    /**
//     * Removes ChangeListener from the list of listeners.
//     * @param listener The listener to remove.
//     */
//    public synchronized void removeChangeListener(ChangeListener listener) {
//        changeListenerList.remove(listener);
//    }
//
//    /**
//     * Notifies all registered listeners about the event.
//     *
//     * @param object Parameter #1 of the <CODE>ChangeEvent<CODE> constructor.
//     */
//    public void fireStateChanged() {
//        if (!silent) {
//            ChangeEvent e = new ChangeEvent(this);
//            for (ChangeListener listener : changeListenerList) {
//                listener.stateChanged(e);
//            }
//        }
//    }
//
    /**
     * @return the histoArea
     */
    public pl.edu.icm.visnow.lib.gui.HistoArea getHistoArea() {
        return histoArea;
    }

    void activateOpenDialog() {
        openDICOMDIRButtonActionPerformed(null);
    }


//    private class SimplifiedDicomDirectory implements TreeModel{
//        private DicomDirectoryRecord top;
//
//        public SimplifiedDicomDirectory(DicomDirectoryRecord top) {
//            this.top = top;
//        }
//
//        public Object getRoot() {
//            return top;
//        }
//
//        public Object getChild(Object parent, int index) {
//            if(parent instanceof DicomDirectoryRecord)
//                return ((DicomDirectoryRecord)parent).getChildAt(index);
//            else
//                return null;
//        }
//
//        public int getChildCount(Object parent) {
//            if(parent instanceof DicomDirectoryRecord)
//                return ((DicomDirectoryRecord)parent).getChildCount();
//            else
//                return 0;
//        }
//
//        public boolean isLeaf(Object node) {
//            if(node instanceof DicomDirectoryRecord)
//                return ((DicomDirectoryRecord)node).isLeaf();
//            else
//                return true;
//        }
//
//        public void valueForPathChanged(TreePath path, Object newValue) {
//        }
//
//        public int getIndexOfChild(Object parent, Object child) {
//            if(parent instanceof DicomDirectoryRecord && child instanceof DicomDirectoryRecord)
//                return ((DicomDirectoryRecord)parent).getIndex((DicomDirectoryRecord)child);
//            else
//                return -1;
//        }
//
//        private Vector listeners;
//
//        public void addTreeModelListener(TreeModelListener tml) {
//            if (listeners == null) listeners = new Vector();
//            listeners.addElement(tml);
//        }
//
//        public void removeTreeModelListener(TreeModelListener tml) {
//            if (listeners != null) listeners.removeElement(tml);
//        }
//
//    }


    public class SimplifiedDicomDirectory implements TreeModel{
        private ArrayList<DicomDirectoryRecord> images;
        private String top = "All";
        private String parentPath = "";

        public SimplifiedDicomDirectory(DicomDirectoryRecord[] images, String parentPath) {
            this.images = new ArrayList<DicomDirectoryRecord>();
            for (int i = 0; i < images.length; i++) {
                this.images.add(images[i]);
            }
            this.top = "All ("+this.images.size()+")";
            this.parentPath = parentPath;
        }

        public Object getRoot() {
            return top;
        }

        public Object getChild(Object parent, int index) {
            if(parent instanceof DicomDirectoryRecord)
                return ((DicomDirectoryRecord)parent).getChildAt(index);
            else if(parent == top)
                return images.get(index);
            else
                return null;
        }

        public int getChildCount(Object parent) {
            if(parent instanceof DicomDirectoryRecord)
                return ((DicomDirectoryRecord)parent).getChildCount();
            else if(parent == top)
                return images.size();
            else
                return 0;
        }

        public boolean isLeaf(Object node) {
            if(node instanceof DicomDirectoryRecord)
                return ((DicomDirectoryRecord)node).isLeaf();
            else if(node == top)
                return (images.isEmpty());
            else
                return true;
        }

        public void valueForPathChanged(TreePath path, Object newValue) {
        }

        public int getIndexOfChild(Object parent, Object child) {
            if(parent instanceof DicomDirectoryRecord && child instanceof DicomDirectoryRecord)
                return ((DicomDirectoryRecord)parent).getIndex((DicomDirectoryRecord)child);
            else if(parent == top && child instanceof DicomDirectoryRecord)
                return images.indexOf(child);
            else
                return -1;
        }

        private ArrayList<TreeModelListener> listeners;

        public void addTreeModelListener(TreeModelListener tml) {
            if (listeners == null) listeners = new ArrayList<TreeModelListener>();
            listeners.add(tml);
        }

        public void removeTreeModelListener(TreeModelListener tml) {
            if (listeners != null) listeners.remove(tml);
        }

    @SuppressWarnings("unchecked")
        public ArrayList<String> getReferencedNameList() {
            ArrayList<String> out = new ArrayList<String>();
            for (int i = 0; i < images.size(); i++) {
                Vector<String> tmp = DicomDirectory.findAllContainedReferencedFileNames(images.get(i), parentPath);
                out.addAll(tmp);
            }
            return out;
        }

    }
    
    public void openDICOMDir()
    {
       openDICOMDIRButtonActionPerformed(null);
    }
}
