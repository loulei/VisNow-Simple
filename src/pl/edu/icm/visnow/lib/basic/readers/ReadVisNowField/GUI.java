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

package pl.edu.icm.visnow.lib.basic.readers.ReadVisNowField;

import java.awt.BorderLayout;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import pl.edu.icm.visnow.lib.gui.Browser;
import pl.edu.icm.visnow.lib.gui.grid.GridFrame;
import pl.edu.icm.visnow.lib.templates.visualization.guis.VariablePresentation;
import pl.edu.icm.visnow.gui.events.BooleanChangeListener;
import pl.edu.icm.visnow.gui.events.BooleanEvent;
import pl.edu.icm.visnow.system.main.VisNow;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class GUI extends javax.swing.JPanel implements VariablePresentation
{
   private JFileChooser dataFileChooser = new JFileChooser();
   private String lastPath = null;
   private Params params;
   private Browser browser = new Browser(new String[] {".vnf", ".VNF"});
   private BooleanChangeListener presentationListener = new BooleanChangeListener()  
   {        
      @Override
      public void booleanChanged(BooleanEvent e)
      {
         setPresentation(e.getState());
      }
      @Override
      public void stateChanged(ChangeEvent e)
      {
      }
   };

   /** Creates new form GUI */
   public GUI()
   {
      initComponents();
      browser.setVisible(false);
      browser.addChangeListener(new ChangeListener()
      {
         public void stateChanged(ChangeEvent e)
         {
            params.setURL(true);
            params.setFileName(browser.getCurrentURL());
            fileNameField.setText(params.getFileName());
            fireStateChanged();
         }
      });
      dataFileChooser.setFileFilter(new FileNameExtensionFilter("Field reader", "Field file", "vnf", "VNF"));
      JScrollPane sp = new JScrollPane(fdPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
      JScrollBar sb = sp.getHorizontalScrollBar();
      sp.remove(sb);
      labelScrollPanel.add(sp, BorderLayout.CENTER);
      labelScrollPanel.add(sb, BorderLayout.NORTH);
   }

   public GUI(String title, String dataFileDesc, String ext0, String ext1)
   {
      initComponents();
      dataFileChooser.setFileFilter(new FileNameExtensionFilter("Field reader", "Field file", "vnf", "VNF"));
   }
   
   public void setPresentation(boolean simple)
   {
      jLabel1.setVisible(!simple);
      jSpinner1.setVisible(!simple);
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

        buttonGroup = new javax.swing.ButtonGroup();
        fdPanel = new javax.swing.JPanel();
        fieldDescription = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        selectButton = new javax.swing.JButton();
        fileNameField = new javax.swing.JTextField();
        rereadButton = new javax.swing.JButton();
        showBox = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        jSpinner1 = new javax.swing.JSpinner();
        labelScrollPanel = new javax.swing.JPanel();
        fileButton = new javax.swing.JRadioButton();
        urlButton1 = new javax.swing.JRadioButton();

        fdPanel.setMinimumSize(new java.awt.Dimension(300, 200));
        fdPanel.setPreferredSize(new java.awt.Dimension(400, 600));
        fdPanel.setLayout(new java.awt.GridBagLayout());

        fieldDescription.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        fieldDescription.setText("VisNow field");
        fieldDescription.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        fdPanel.add(fieldDescription, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        fdPanel.add(jPanel3, gridBagConstraints);

        setMinimumSize(new java.awt.Dimension(180, 900));
        setPreferredSize(new java.awt.Dimension(200, 920));
        setRequestFocusEnabled(false);
        setLayout(new java.awt.GridBagLayout());

        selectButton.setText("browse files");
        selectButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
        selectButton.setMaximumSize(new java.awt.Dimension(70, 20));
        selectButton.setMinimumSize(new java.awt.Dimension(70, 20));
        selectButton.setPreferredSize(new java.awt.Dimension(90, 20));
        selectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.1;
        add(selectButton, gridBagConstraints);

        fileNameField.setMinimumSize(new java.awt.Dimension(4, 20));
        fileNameField.setPreferredSize(new java.awt.Dimension(4, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        add(fileNameField, gridBagConstraints);

        rereadButton.setText("reread");
        rereadButton.setEnabled(false);
        rereadButton.setMaximumSize(new java.awt.Dimension(90, 20));
        rereadButton.setMinimumSize(new java.awt.Dimension(90, 20));
        rereadButton.setPreferredSize(new java.awt.Dimension(90, 20));
        rereadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rereadButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 0, 0);
        add(rereadButton, gridBagConstraints);

        showBox.setSelected(true);
        showBox.setText("show");
        showBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(showBox, gridBagConstraints);

        jLabel1.setText("threads");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(jLabel1, gridBagConstraints);

        jSpinner1.setModel(new javax.swing.SpinnerNumberModel(Runtime.getRuntime().availableProcessors(), 1, Runtime.getRuntime().availableProcessors(), 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(jSpinner1, gridBagConstraints);

        labelScrollPanel.setBackground(new java.awt.Color(153, 153, 153));
        labelScrollPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(labelScrollPanel, gridBagConstraints);

        buttonGroup.add(fileButton);
        fileButton.setSelected(true);
        fileButton.setText("file");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(fileButton, gridBagConstraints);

        buttonGroup.add(urlButton1);
        urlButton1.setText("URL");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(urlButton1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

   public void activateOpenDialog() {
       selectButtonActionPerformed(null);
   }
   
    private void selectButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_selectButtonActionPerformed
    {//GEN-HEADEREND:event_selectButtonActionPerformed
       if (fileButton.isSelected())
       {
          if (lastPath == null)
             dataFileChooser.setCurrentDirectory(new File(VisNow.get().getMainConfig().getUsableDataPath(ReadVisNowField.class)));
          else
             dataFileChooser.setCurrentDirectory(new File(lastPath));

          int returnVal = dataFileChooser.showOpenDialog(this);
          if (returnVal == JFileChooser.APPROVE_OPTION)
          {
             String fileName = dataFileChooser.getSelectedFile().getAbsolutePath();
             params.setFileName(fileName);
             lastPath = fileName.substring(0, fileName.lastIndexOf(File.separator));
             VisNow.get().getMainConfig().setLastDataPath(lastPath,ReadVisNowField.class);
             rereadButton.setEnabled(true);
          }
          fileNameField.setText(params.getFileName());
          fireStateChanged();
       } else if (urlButton1.isSelected())
          browser.setVisible(true);
//       else if (gridButton.isSelected())
//          gridFrame.setVisible(true);
}//GEN-LAST:event_selectButtonActionPerformed

    private void rereadButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_rereadButtonActionPerformed
    {//GEN-HEADEREND:event_rereadButtonActionPerformed
       fireStateChanged();
    }//GEN-LAST:event_rereadButtonActionPerformed

private void showBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showBoxActionPerformed
       fireStateChanged();
}//GEN-LAST:event_showBoxActionPerformed

   public void setFieldDescription(String s)
   {
      fieldDescription.setText(s);
   }
   /**
    * Utility field holding list of ChangeListeners.
    */
   private transient ArrayList<ChangeListener> changeListenerList =
           new ArrayList<ChangeListener>();

   /**
    * Registers ChangeListener to receive events.
    * @param listener The listener to register.
    */
   public synchronized void addChangeListener(ChangeListener listener)
   {
      changeListenerList.add(listener);
   }

   /**
    * Removes ChangeListener from the list of listeners.
    * @param listener The listener to remove.
    */
   public synchronized void removeChangeListener(ChangeListener listener)
   {
      changeListenerList.remove(listener);
   }
   
   public void unSetShow()
   {
      showBox.setSelected(false);
      params.setShow(false);
   }

   /**
    * Notifies all registered listeners about the event.
    *
    * @param object Parameter #1 of the <CODE>ChangeEvent<CODE> constructor.
    */
   private void fireStateChanged()
   {
      params.setShow(showBox.isSelected());
      params.fireStateChanged();
   }

   /**
    * @param params the params to set
    */
   public void setParams(Params params)
   {
      this.params = params;
   }

   public String getLastPath()
   {
      return lastPath;
   }
   
   @Override
   public BooleanChangeListener getPresentationListener()
   {
      return presentationListener;
   }
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.ButtonGroup buttonGroup;
    protected javax.swing.JPanel fdPanel;
    protected javax.swing.JLabel fieldDescription;
    protected javax.swing.JRadioButton fileButton;
    protected javax.swing.JTextField fileNameField;
    protected javax.swing.JLabel jLabel1;
    protected javax.swing.JPanel jPanel3;
    protected javax.swing.JSpinner jSpinner1;
    protected javax.swing.JPanel labelScrollPanel;
    protected javax.swing.JButton rereadButton;
    protected javax.swing.JButton selectButton;
    protected javax.swing.JCheckBox showBox;
    protected javax.swing.JRadioButton urlButton1;
    // End of variables declaration//GEN-END:variables
}
