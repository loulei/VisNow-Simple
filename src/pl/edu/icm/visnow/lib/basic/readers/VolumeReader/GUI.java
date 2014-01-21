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
 exception statement from your version. */
//</editor-fold>

package pl.edu.icm.visnow.lib.basic.readers.VolumeReader;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import pl.edu.icm.visnow.lib.gui.Browser;
import pl.edu.icm.visnow.lib.gui.grid.GridFrame;
import pl.edu.icm.visnow.lib.utils.io.InputSource;
import pl.edu.icm.visnow.system.main.VisNow;

/**
 *
 * @author  Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class GUI extends JPanel
{

   private JFileChooser dataFileChooser = new JFileChooser();
   private FileNameExtensionFilter dataFilter;
   protected Params params = new Params();
   private String lastPath = null;
   protected String fileName = null;
   protected String[] extensions = new String[]{ "dat", "DAT", "vol", "VOL"};
   private Browser browser = new Browser(extensions);
   private GridFrame gridFrame = new GridFrame();
   private int[] dims = null;
   private float[] orig = {0, 0, 0};
   private float[] scale = {1, 1, 1};

   /** Creates new form GUI */
   public GUI()
   {
      initComponents();
      dataFileChooser.setLocation(0,0);
      dataFilter = new FileNameExtensionFilter("volume data file", 
                                                extensions[0], extensions[1], extensions[2], extensions[3], 
                                                "dat_gz", "DAT_GZ","vol_gz","VOL_GZ");
      dataFileChooser.setFileFilter(dataFilter);
      
      browser.setVisible(false);
      browser.addChangeListener(new ChangeListener()
      {
         public void stateChanged(ChangeEvent e)
         {
            params.setSource(InputSource.URL);
            params.setFileName(browser.getCurrentURL());
            fileNameField.setText(params.getFileName());
            fireStateChanged();
         }
      });
      
      gridFrame.setVisible(false);
      gridFrame.setFileExtensions(extensions);
      gridFrame.setSingleFile(true);
      gridFrame.addChangeListener(new ChangeListener()
      {
         public void stateChanged(ChangeEvent e)
         {
            params.setSource(InputSource.GRID);
            params.setFileName(VisNow.getTmpDirPath() + File.separator + gridFrame.getTransferredFileNames()[0]);
            fileNameField.setText(params.getFileName());
            fireStateChanged();
         }
      });
   }

   public GUI(String title, String dataFileDesc, String ext0, String ext1)
   {
      initComponents();
      moduleLabel.setText(title);
      dataFilter = new FileNameExtensionFilter(dataFileDesc, ext0, ext1);
      dataFileChooser.setFileFilter(dataFilter);
      browser.setVisible(false);
      browser.addChangeListener(new ChangeListener()
      {
         public void stateChanged(ChangeEvent e)
         {
            params.setSource(InputSource.URL);
            params.setFileName(browser.getCurrentURL());
            fileNameField.setText(params.getFileName());
            fireStateChanged();
         }
      });
   }

   /** This method is called from within the constructor to
    * initialize the form.
    * WARNING: Do NOT
    * modify this code. The content of this method is always regenerated by the Form Editor.
    */
   // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
   private void initComponents()
   {
      java.awt.GridBagConstraints gridBagConstraints;

      sourceGroup = new javax.swing.ButtonGroup();
      geometryGroup = new javax.swing.ButtonGroup();
      moduleLabel = new javax.swing.JLabel();
      selectButton = new javax.swing.JButton();
      fileNameField = new javax.swing.JTextField();
      rereadButton = new javax.swing.JButton();
      jScrollPane1 = new javax.swing.JScrollPane();
      fieldDescription = new javax.swing.JLabel();
      extFromIndices = new javax.swing.JRadioButton();
      extNormalize = new javax.swing.JRadioButton();
      extUser = new javax.swing.JRadioButton();
      extFromFile = new javax.swing.JRadioButton();
      jLabel7 = new javax.swing.JLabel();
      fileButton = new javax.swing.JRadioButton();
      urlButton = new javax.swing.JRadioButton();
      gridButton = new javax.swing.JRadioButton();
      jTabbedPane1 = new javax.swing.JTabbedPane();
      additionalPanel1 = new javax.swing.JPanel();
      jLabel8 = new javax.swing.JLabel();
      jLabel9 = new javax.swing.JLabel();
      jLabel10 = new javax.swing.JLabel();
      jLabel11 = new javax.swing.JLabel();
      xMinField = new pl.edu.icm.visnow.gui.components.FloatFormattedTextField();
      xMaxField = new pl.edu.icm.visnow.gui.components.FloatFormattedTextField();
      jLabel12 = new javax.swing.JLabel();
      yMinField = new pl.edu.icm.visnow.gui.components.FloatFormattedTextField();
      yMaxField = new pl.edu.icm.visnow.gui.components.FloatFormattedTextField();
      jLabel13 = new javax.swing.JLabel();
      zMinField = new pl.edu.icm.visnow.gui.components.FloatFormattedTextField();
      zMaxField = new pl.edu.icm.visnow.gui.components.FloatFormattedTextField();
      additionalPanel = new javax.swing.JPanel();
      jLabel4 = new javax.swing.JLabel();
      jLabel5 = new javax.swing.JLabel();
      jLabel6 = new javax.swing.JLabel();
      jLabel2 = new javax.swing.JLabel();
      xOrigField = new javax.swing.JTextField();
      xScaleField = new javax.swing.JTextField();
      jLabel3 = new javax.swing.JLabel();
      yOrigField = new javax.swing.JTextField();
      yScaleField = new javax.swing.JTextField();
      jLabel1 = new javax.swing.JLabel();
      zOrigField = new javax.swing.JTextField();
      zScaleField = new javax.swing.JTextField();

      setBorder(javax.swing.BorderFactory.createTitledBorder(""));
      setMinimumSize(new java.awt.Dimension(180, 850));
      setOpaque(false);
      setPreferredSize(new java.awt.Dimension(200, 860));
      setRequestFocusEnabled(false);
      setLayout(new java.awt.GridBagLayout());

      moduleLabel.setText("module");
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.gridwidth = 3;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
      add(moduleLabel, gridBagConstraints);

      selectButton.setText("browse");
      selectButton.setMaximumSize(new java.awt.Dimension(90, 20));
      selectButton.setMinimumSize(new java.awt.Dimension(90, 20));
      selectButton.setPreferredSize(new java.awt.Dimension(90, 20));
      selectButton.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            selectButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
      gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 1);
      add(selectButton, gridBagConstraints);

      fileNameField.setMinimumSize(new java.awt.Dimension(4, 20));
      fileNameField.setPreferredSize(new java.awt.Dimension(4, 22));
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 3;
      gridBagConstraints.gridwidth = 3;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
      add(fileNameField, gridBagConstraints);

      rereadButton.setText("reread");
      rereadButton.setEnabled(false);
      rereadButton.setMaximumSize(new java.awt.Dimension(90, 20));
      rereadButton.setMinimumSize(new java.awt.Dimension(90, 20));
      rereadButton.setPreferredSize(new java.awt.Dimension(90, 20));
      rereadButton.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            rereadButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 10;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
      gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 1);
      add(rereadButton, gridBagConstraints);

      fieldDescription.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
      fieldDescription.setText(null);
      fieldDescription.setVerticalAlignment(javax.swing.SwingConstants.TOP);
      jScrollPane1.setViewportView(fieldDescription);

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 11;
      gridBagConstraints.gridwidth = 3;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 1.0;
      gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
      add(jScrollPane1, gridBagConstraints);

      geometryGroup.add(extFromIndices);
      extFromIndices.setText("from indices");
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 6;
      gridBagConstraints.gridwidth = 3;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
      add(extFromIndices, gridBagConstraints);

      geometryGroup.add(extNormalize);
      extNormalize.setText("normalize to fit in unit box");
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 7;
      gridBagConstraints.gridwidth = 3;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
      add(extNormalize, gridBagConstraints);

      geometryGroup.add(extUser);
      extUser.setText("user");
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 8;
      gridBagConstraints.gridwidth = 3;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
      add(extUser, gridBagConstraints);

      geometryGroup.add(extFromFile);
      extFromFile.setSelected(true);
      extFromFile.setText("from file");
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 5;
      gridBagConstraints.gridwidth = 3;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
      add(extFromFile, gridBagConstraints);

      jLabel7.setText("geometric dimensions");
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 4;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
      add(jLabel7, gridBagConstraints);

      sourceGroup.add(fileButton);
      fileButton.setSelected(true);
      fileButton.setText("file");
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
      add(fileButton, gridBagConstraints);

      sourceGroup.add(urlButton);
      urlButton.setText("URL");
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
      add(urlButton, gridBagConstraints);

      sourceGroup.add(gridButton);
      gridButton.setText("grid");
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
      add(gridButton, gridBagConstraints);

      jTabbedPane1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N

      additionalPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
      additionalPanel1.setMinimumSize(new java.awt.Dimension(180, 80));
      additionalPanel1.setPreferredSize(new java.awt.Dimension(200, 81));
      additionalPanel1.setLayout(new java.awt.GridLayout(4, 3));

      jLabel8.setText("axis");
      additionalPanel1.add(jLabel8);

      jLabel9.setText("min");
      additionalPanel1.add(jLabel9);

      jLabel10.setText("max");
      additionalPanel1.add(jLabel10);

      jLabel11.setText("x");
      additionalPanel1.add(jLabel11);

      xMinField.setText("-1");
      additionalPanel1.add(xMinField);
      additionalPanel1.add(xMaxField);

      jLabel12.setText("y");
      additionalPanel1.add(jLabel12);

      yMinField.setText("-1");
      additionalPanel1.add(yMinField);
      additionalPanel1.add(yMaxField);

      jLabel13.setText("z");
      additionalPanel1.add(jLabel13);

      zMinField.setText("-1");
      additionalPanel1.add(zMinField);
      additionalPanel1.add(zMaxField);

      jTabbedPane1.addTab("extents", additionalPanel1);

      additionalPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
      additionalPanel.setMinimumSize(new java.awt.Dimension(180, 80));
      additionalPanel.setPreferredSize(new java.awt.Dimension(200, 81));
      additionalPanel.setLayout(new java.awt.GridLayout(4, 3));

      jLabel4.setText("axis");
      additionalPanel.add(jLabel4);

      jLabel5.setText("origin");
      additionalPanel.add(jLabel5);

      jLabel6.setText("cell dim");
      additionalPanel.add(jLabel6);

      jLabel2.setText("x");
      additionalPanel.add(jLabel2);

      xOrigField.setText("0");
      additionalPanel.add(xOrigField);

      xScaleField.setText("1");
      additionalPanel.add(xScaleField);

      jLabel3.setText("y");
      additionalPanel.add(jLabel3);

      yOrigField.setText("0");
      additionalPanel.add(yOrigField);

      yScaleField.setText("1");
      additionalPanel.add(yScaleField);

      jLabel1.setText("z");
      additionalPanel.add(jLabel1);

      zOrigField.setText("0");
      additionalPanel.add(zOrigField);

      zScaleField.setText("1");
      additionalPanel.add(zScaleField);

      jTabbedPane1.addTab("origin/cell size", additionalPanel);

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 9;
      gridBagConstraints.gridwidth = 3;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      add(jTabbedPane1, gridBagConstraints);
   }// </editor-fold>//GEN-END:initComponents

   public void activateOpenDialog() {
       selectButtonActionPerformed(null);
   }
    
   private void selectButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_selectButtonActionPerformed
   {//GEN-HEADEREND:event_selectButtonActionPerformed
      if (fileButton.isSelected())
      {
         if (lastPath == null)
            dataFileChooser.setCurrentDirectory(new File(VisNow.get().getMainConfig().getUsableDataPath(VolumeReader.class)));
         else
            dataFileChooser.setCurrentDirectory(new File(lastPath));

         int returnVal = dataFileChooser.showOpenDialog(this);
         if (returnVal == JFileChooser.APPROVE_OPTION)
         {
            fileName = dataFileChooser.getSelectedFile().getAbsolutePath();
            params.setSource(InputSource.FILE);
            params.setFileName(fileName);
            lastPath = fileName.substring(0, fileName.lastIndexOf(File.separator));
            VisNow.get().getMainConfig().setLastDataPath(lastPath,VolumeReader.class);
            rereadButton.setEnabled(true);
         }
         fileNameField.setText(params.getFileName());
         fireStateChanged();
      } else if (urlButton.isSelected())
         browser.setVisible(true);
      else if (gridButton.isSelected())
         gridFrame.setVisible(true);
   }//GEN-LAST:event_selectButtonActionPerformed

private void rereadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rereadButtonActionPerformed
   fireStateChanged();
}//GEN-LAST:event_rereadButtonActionPerformed

   public void setFieldDescription(String s)
   {
      fieldDescription.setText(s);
   }
   protected void fireStateChanged()
   {
      try
      {
         float x = Float.parseFloat(xScaleField.getText());
         params.getScale()[0] = x;
         float x0 = Float.parseFloat(xOrigField.getText());
         params.getOrig()[0] = x0;
      } catch (NumberFormatException e)
      {
         xScaleField.setText(String.format("%5f", params.getScale()[0]));
         xOrigField.setText(String.format("%5f", params.getOrig()[0]));
      }
      try
      {
         float y = Float.parseFloat(yScaleField.getText());
         params.getScale()[1] = y;
         float y0 = Float.parseFloat(yOrigField.getText());
         params.getOrig()[1] = y0;
      } catch (NumberFormatException e)
      {
         yScaleField.setText(String.format("%5f", params.getScale()[1]));
         yOrigField.setText(String.format("%5f", params.getOrig()[1]));
      }
      try
      {
         float z = Float.parseFloat(zScaleField.getText());
         params.getScale()[2] = z;
         float z0 = Float.parseFloat(zOrigField.getText());
         params.getOrig()[2] = z0;
      } catch (NumberFormatException e)
      {
         zScaleField.setText(String.format("%5f", params.getScale()[2]));
         zOrigField.setText(String.format("%5f", params.getOrig()[2]));
      }
      params.getMin()[0] = xMinField.getValue();
      params.getMax()[0] = xMaxField.getValue();
      params.getMin()[1] = yMinField.getValue();
      params.getMax()[1] = yMaxField.getValue();
      params.getMin()[2] = zMinField.getValue();
      params.getMax()[2] = zMaxField.getValue();
      if (extFromFile.isSelected())    params.setType(Params.FROM_FILE);
      if (extFromIndices.isSelected()) params.setType(Params.FROM_INDICES);
      if (extNormalize.isSelected())   params.setType(Params.NORMALIZED);
      if (extUser.isSelected() && jTabbedPane1.getSelectedIndex() == 0)        params.setType(Params.USER_EXTENTS);
      if (extUser.isSelected() && jTabbedPane1.getSelectedIndex() == 1)        params.setType(Params.USER_AFFINE);
      params.fireStateChanged();
   }

   /**
    * @param params the params to set
    */
   public void setParams(Params params)
   {
      this.params = params;
   }
   
   public void setAVSCompatible(boolean c)
   {
      extFromFile.setEnabled(!c);
      if (c)
         extNormalize.setSelected(true);
   }

   public void setDims(int[] dims)
   {
      if (dims == null || dims.length != 3)
         return;
      this.dims = dims;
   }

   // Variables declaration - do not modify//GEN-BEGIN:variables
   protected javax.swing.JPanel additionalPanel;
   protected javax.swing.JPanel additionalPanel1;
   private javax.swing.JRadioButton extFromFile;
   private javax.swing.JRadioButton extFromIndices;
   private javax.swing.JRadioButton extNormalize;
   private javax.swing.JRadioButton extUser;
   private javax.swing.JLabel fieldDescription;
   private javax.swing.JRadioButton fileButton;
   protected javax.swing.JTextField fileNameField;
   private javax.swing.ButtonGroup geometryGroup;
   private javax.swing.JRadioButton gridButton;
   private javax.swing.JLabel jLabel1;
   private javax.swing.JLabel jLabel10;
   private javax.swing.JLabel jLabel11;
   private javax.swing.JLabel jLabel12;
   private javax.swing.JLabel jLabel13;
   private javax.swing.JLabel jLabel2;
   private javax.swing.JLabel jLabel3;
   private javax.swing.JLabel jLabel4;
   private javax.swing.JLabel jLabel5;
   private javax.swing.JLabel jLabel6;
   private javax.swing.JLabel jLabel7;
   private javax.swing.JLabel jLabel8;
   private javax.swing.JLabel jLabel9;
   private javax.swing.JScrollPane jScrollPane1;
   private javax.swing.JTabbedPane jTabbedPane1;
   protected javax.swing.JLabel moduleLabel;
   protected javax.swing.JButton rereadButton;
   protected javax.swing.JButton selectButton;
   private javax.swing.ButtonGroup sourceGroup;
   private javax.swing.JRadioButton urlButton;
   private pl.edu.icm.visnow.gui.components.FloatFormattedTextField xMaxField;
   private pl.edu.icm.visnow.gui.components.FloatFormattedTextField xMinField;
   private javax.swing.JTextField xOrigField;
   private javax.swing.JTextField xScaleField;
   private pl.edu.icm.visnow.gui.components.FloatFormattedTextField yMaxField;
   private pl.edu.icm.visnow.gui.components.FloatFormattedTextField yMinField;
   private javax.swing.JTextField yOrigField;
   private javax.swing.JTextField yScaleField;
   private pl.edu.icm.visnow.gui.components.FloatFormattedTextField zMaxField;
   private pl.edu.icm.visnow.gui.components.FloatFormattedTextField zMinField;
   private javax.swing.JTextField zOrigField;
   private javax.swing.JTextField zScaleField;
   // End of variables declaration//GEN-END:variables
}
