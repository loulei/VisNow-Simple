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


package pl.edu.icm.visnow.lib.basic.filters.RadialCoordinates;

import org.apache.log4j.Logger;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;

public class GUI extends javax.swing.JPanel
{
   private boolean rVar = true;
   private boolean phiVar = true;
   private boolean psiVar = true;
   private RegularField inField;

   protected Params params;
   static Logger logger = Logger.getLogger(GUI.class);

   /** Creates new form EmptyVisnowModuleGUI */
   public GUI()
   {
      initComponents();
   }

   public void setParams(Params params)
   {
      this.params = params;
   }

   public void setInField(RegularField inFld)
   {
      if (inFld==null || inFld.getDims()==null || inField != null && inField.getDims().length == inFld.getDims().length)
         return;
      inField = inFld;

      logger.debug("GUI setting infield");
      SwingInstancer.swingRunAndWait(new Runnable()
      {
         public void run()
         {
            logger.debug("setting widgets from field");
            switch (inField.getDims().length)
            {
            case 3:
               rPane.setSelectedComponent(varRPanel);
               phiPane.setSelectedComponent(varPhiPanel);
               psiPane.setSelectedComponent(varPsiPanel);
               xRButton.setEnabled(true);
               xRButton.setSelected(true);
               yRButton.setEnabled(true);
               zRButton.setEnabled(true);
               xPhiButton.setEnabled(true);
               yPhiButton.setEnabled(true);
               yPhiButton.setSelected(true);
               zPhiButton.setEnabled(true);
               xPsiButton.setEnabled(true);
               yPsiButton.setEnabled(true);
               zPsiButton.setEnabled(true);
               zPsiButton.setSelected(true);
               params.setRCoord(0);
               params.setPhiCoord(1);
               params.setPsiCoord(2);
               break;
            case 2:
               rPane.setSelectedComponent(constRPanel);
               phiPane.setSelectedComponent(varPhiPanel);
               psiPane.setSelectedComponent(varPsiPanel);
               xRButton.setEnabled(true);
               xRButton.setSelected(false);
               yRButton.setEnabled(true);
               zRButton.setEnabled(false);
               xPhiButton.setEnabled(true);
               xPhiButton.setSelected(true);
               yPhiButton.setEnabled(true);
               zPhiButton.setEnabled(false);
               xPsiButton.setEnabled(true);
               yPsiButton.setEnabled(true);
               yPsiButton.setSelected(true);
               zPsiButton.setEnabled(false);
               params.setRCoord(Params.CONSTANT);
               params.setPhiCoord(0);
               params.setPsiCoord(1);
               rPane.setSelectedIndex(1);
               break;
            case 1:
               rPane.setSelectedComponent(constRPanel);
               phiPane.setSelectedComponent(varPhiPanel);
               psiPane.setSelectedComponent(constPsiPanel);
               xRButton.setEnabled(true);
               yRButton.setEnabled(false);
               zRButton.setEnabled(false);
               xPhiButton.setEnabled(true);
               yPhiButton.setEnabled(false);
               zPhiButton.setEnabled(false);
               xPsiButton.setEnabled(true);
               xPsiButton.setSelected(true);
               yPsiButton.setEnabled(false);
               zPsiButton.setEnabled(false);
               rPane.setSelectedIndex(1);
               phiPane.setSelectedIndex(1);
               params.setRCoord(Params.CONSTANT);
               params.setPhiCoord(Params.CONSTANT);
               params.setPsiCoord(0);
               break;
            }
         }
      });
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        mapCombo = new javax.swing.JComboBox();
        rPane = new javax.swing.JTabbedPane();
        varRPanel = new javax.swing.JPanel();
        rRangeSlider = new pl.edu.icm.visnow.gui.widgets.FloatSubRangeSlider.ExtendedFloatSubRangeSlider();
        xRButton = new javax.swing.JRadioButton();
        yRButton = new javax.swing.JRadioButton();
        zRButton = new javax.swing.JRadioButton();
        constRPanel = new javax.swing.JPanel();
        rSlider = new pl.edu.icm.visnow.gui.widgets.FloatSlider();
        phiPane = new javax.swing.JTabbedPane();
        varPhiPanel = new javax.swing.JPanel();
        phiRangeSlider = new pl.edu.icm.visnow.gui.widgets.FloatSubRangeSlider.ExtendedFloatSubRangeSlider();
        xPhiButton = new javax.swing.JRadioButton();
        yPhiButton = new javax.swing.JRadioButton();
        zPhiButton = new javax.swing.JRadioButton();
        constPhiPanel = new javax.swing.JPanel();
        phiSlider = new pl.edu.icm.visnow.gui.widgets.FloatSlider();
        psiPane = new javax.swing.JTabbedPane();
        varPsiPanel = new javax.swing.JPanel();
        psiRangeSlider = new pl.edu.icm.visnow.gui.widgets.FloatSubRangeSlider.ExtendedFloatSubRangeSlider();
        xPsiButton = new javax.swing.JRadioButton();
        yPsiButton = new javax.swing.JRadioButton();
        zPsiButton = new javax.swing.JRadioButton();
        constPsiPanel = new javax.swing.JPanel();
        psiSlider = new pl.edu.icm.visnow.gui.widgets.FloatSlider();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));

        setLayout(new java.awt.GridBagLayout());

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pl/edu/icm/visnow/lib/basic/filters/RadialCoordinates/Bundle"); // NOI18N
        jLabel1.setText(bundle.getString("GUI.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(jLabel1, gridBagConstraints);

        mapCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "spherical", "cylindrical" }));
        mapCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mapComboActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        add(mapCombo, gridBagConstraints);

        varRPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        varRPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                varRPanelComponentShown(evt);
            }
        });
        varRPanel.setLayout(new java.awt.GridBagLayout());

        rRangeSlider.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        rRangeSlider.setMax(1.0F);
        rRangeSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                rRangeSliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        varRPanel.add(rRangeSlider, gridBagConstraints);

        buttonGroup1.add(xRButton);
        xRButton.setSelected(true);
        xRButton.setText(bundle.getString("GUI.xRButton.text_1")); // NOI18N
        xRButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                xRButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        varRPanel.add(xRButton, gridBagConstraints);

        buttonGroup1.add(yRButton);
        yRButton.setText(bundle.getString("GUI.yRButton.text_1")); // NOI18N
        yRButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                yRButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        varRPanel.add(yRButton, gridBagConstraints);

        buttonGroup1.add(zRButton);
        zRButton.setText(bundle.getString("GUI.zRButton.text_1")); // NOI18N
        zRButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zRButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        varRPanel.add(zRButton, gridBagConstraints);

        rPane.addTab(bundle.getString("GUI.varRPanel.TabConstraints.tabTitle"), varRPanel); // NOI18N

        constRPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                constRPanelComponentShown(evt);
            }
        });

        rSlider.setMax(10.0F);
        rSlider.setVal(1.0F);
        rSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                rSliderStateChanged(evt);
            }
        });

        javax.swing.GroupLayout constRPanelLayout = new javax.swing.GroupLayout(constRPanel);
        constRPanel.setLayout(constRPanelLayout);
        constRPanelLayout.setHorizontalGroup(
            constRPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 222, Short.MAX_VALUE)
            .addGroup(constRPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(rSlider, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE))
        );
        constRPanelLayout.setVerticalGroup(
            constRPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 86, Short.MAX_VALUE)
            .addGroup(constRPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(constRPanelLayout.createSequentialGroup()
                    .addComponent(rSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        rPane.addTab(bundle.getString("GUI.constRPanel.TabConstraints.tabTitle"), constRPanel); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        add(rPane, gridBagConstraints);

        varPhiPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        varPhiPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                varPhiPanelComponentShown(evt);
            }
        });
        varPhiPanel.setLayout(new java.awt.GridBagLayout());

        phiRangeSlider.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        phiRangeSlider.setLow(-90.0F);
        phiRangeSlider.setMax(90.0F);
        phiRangeSlider.setMin(-90.0F);
        phiRangeSlider.setOpaque(false);
        phiRangeSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                phiRangeSliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        varPhiPanel.add(phiRangeSlider, gridBagConstraints);

        buttonGroup2.add(xPhiButton);
        xPhiButton.setText(bundle.getString("GUI.xPhiButton.text")); // NOI18N
        xPhiButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                xPhiButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        varPhiPanel.add(xPhiButton, gridBagConstraints);

        buttonGroup2.add(yPhiButton);
        yPhiButton.setSelected(true);
        yPhiButton.setText(bundle.getString("GUI.yPhiButton.text")); // NOI18N
        yPhiButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                yPhiButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        varPhiPanel.add(yPhiButton, gridBagConstraints);

        buttonGroup2.add(zPhiButton);
        zPhiButton.setText(bundle.getString("GUI.zPhiButton.text")); // NOI18N
        zPhiButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zPhiButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        varPhiPanel.add(zPhiButton, gridBagConstraints);

        phiPane.addTab(bundle.getString("GUI.varPhiPanel.TabConstraints.tabTitle"), varPhiPanel); // NOI18N

        constPhiPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                constPhiPanelComponentShown(evt);
            }
        });

        phiSlider.setMax(90.0F);
        phiSlider.setMin(-90.0F);
        phiSlider.setVal(0.0F);
        phiSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                phiSliderStateChanged(evt);
            }
        });

        javax.swing.GroupLayout constPhiPanelLayout = new javax.swing.GroupLayout(constPhiPanel);
        constPhiPanel.setLayout(constPhiPanelLayout);
        constPhiPanelLayout.setHorizontalGroup(
            constPhiPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 222, Short.MAX_VALUE)
            .addGroup(constPhiPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(phiSlider, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE))
        );
        constPhiPanelLayout.setVerticalGroup(
            constPhiPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 104, Short.MAX_VALUE)
            .addGroup(constPhiPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, constPhiPanelLayout.createSequentialGroup()
                    .addComponent(phiSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        phiPane.addTab(bundle.getString("GUI.constPhiPanel.TabConstraints.tabTitle_1"), constPhiPanel); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        add(phiPane, gridBagConstraints);

        varPsiPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        varPsiPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                varPsiPanelComponentShown(evt);
            }
        });
        varPsiPanel.setLayout(new java.awt.GridBagLayout());

        psiRangeSlider.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        psiRangeSlider.setLow(-180.0F);
        psiRangeSlider.setMax(360.0F);
        psiRangeSlider.setUp(180.0F);
        psiRangeSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                psiRangeSliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        varPsiPanel.add(psiRangeSlider, gridBagConstraints);

        buttonGroup3.add(xPsiButton);
        xPsiButton.setText(bundle.getString("GUI.xPsiButton.text")); // NOI18N
        xPsiButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                xPsiButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        varPsiPanel.add(xPsiButton, gridBagConstraints);

        buttonGroup3.add(yPsiButton);
        yPsiButton.setText(bundle.getString("GUI.yPsiButton.text")); // NOI18N
        yPsiButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                yPsiButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        varPsiPanel.add(yPsiButton, gridBagConstraints);

        buttonGroup3.add(zPsiButton);
        zPsiButton.setSelected(true);
        zPsiButton.setText(bundle.getString("GUI.zPsiButton.text")); // NOI18N
        zPsiButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zPsiButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        varPsiPanel.add(zPsiButton, gridBagConstraints);

        psiPane.addTab(bundle.getString("GUI.varPsiPanel.TabConstraints.tabTitle"), varPsiPanel); // NOI18N

        constPsiPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                constPsiPanelComponentShown(evt);
            }
        });

        psiSlider.setMax(180.0F);
        psiSlider.setMin(-180.0F);
        psiSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                psiSliderStateChanged(evt);
            }
        });

        javax.swing.GroupLayout constPsiPanelLayout = new javax.swing.GroupLayout(constPsiPanel);
        constPsiPanel.setLayout(constPsiPanelLayout);
        constPsiPanelLayout.setHorizontalGroup(
            constPsiPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(psiSlider, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)
        );
        constPsiPanelLayout.setVerticalGroup(
            constPsiPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(constPsiPanelLayout.createSequentialGroup()
                .addComponent(psiSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        psiPane.addTab(bundle.getString("GUI.constPsiPanel.TabConstraints.tabTitle"), constPsiPanel); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        add(psiPane, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.weighty = 1.0;
        add(filler1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

   private void xRButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_xRButtonActionPerformed
   {//GEN-HEADEREND:event_xRButtonActionPerformed
      execute();
   }//GEN-LAST:event_xRButtonActionPerformed

   private void yRButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_yRButtonActionPerformed
   {//GEN-HEADEREND:event_yRButtonActionPerformed
      execute();
   }//GEN-LAST:event_yRButtonActionPerformed

   private void zRButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_zRButtonActionPerformed
   {//GEN-HEADEREND:event_zRButtonActionPerformed
      execute();
   }//GEN-LAST:event_zRButtonActionPerformed

   private void rRangeSliderStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_rRangeSliderStateChanged
   {//GEN-HEADEREND:event_rRangeSliderStateChanged
      execute();
   }//GEN-LAST:event_rRangeSliderStateChanged

   private void xPhiButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_xPhiButtonActionPerformed
   {//GEN-HEADEREND:event_xPhiButtonActionPerformed
      execute();
   }//GEN-LAST:event_xPhiButtonActionPerformed

   private void yPhiButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_yPhiButtonActionPerformed
   {//GEN-HEADEREND:event_yPhiButtonActionPerformed
      execute();
   }//GEN-LAST:event_yPhiButtonActionPerformed

   private void zPhiButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_zPhiButtonActionPerformed
   {//GEN-HEADEREND:event_zPhiButtonActionPerformed
      execute();
   }//GEN-LAST:event_zPhiButtonActionPerformed

   private void phiRangeSliderStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_phiRangeSliderStateChanged
   {//GEN-HEADEREND:event_phiRangeSliderStateChanged
      execute();
   }//GEN-LAST:event_phiRangeSliderStateChanged

   private void xPsiButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_xPsiButtonActionPerformed
   {//GEN-HEADEREND:event_xPsiButtonActionPerformed
      execute();
   }//GEN-LAST:event_xPsiButtonActionPerformed

   private void yPsiButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_yPsiButtonActionPerformed
   {//GEN-HEADEREND:event_yPsiButtonActionPerformed
      execute();
   }//GEN-LAST:event_yPsiButtonActionPerformed

   private void zPsiButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_zPsiButtonActionPerformed
   {//GEN-HEADEREND:event_zPsiButtonActionPerformed
      execute();
   }//GEN-LAST:event_zPsiButtonActionPerformed

   private void psiRangeSliderStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_psiRangeSliderStateChanged
   {//GEN-HEADEREND:event_psiRangeSliderStateChanged
      execute();
   }//GEN-LAST:event_psiRangeSliderStateChanged

   private void mapComboActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_mapComboActionPerformed
   {//GEN-HEADEREND:event_mapComboActionPerformed
      java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pl/edu/icm/visnow/lib/basic/filters/RadialCoordinates/Bundle");
      if (mapCombo.getSelectedIndex() == 1)
      {
         psiPane.removeTabAt(1);
         psiPane.removeTabAt(0);
         psiRangeSlider.setParams(-1,1,-1,1);
         psiPane.addTab(bundle.getString("GUI.varPsiPanel.TabConstraints.zTitle"), varPsiPanel); // NOI18N
         psiPane.addTab(bundle.getString("GUI.constPsiPanel.TabConstraints.zTitle"), constPsiPanel);
      }
      else
      {
         psiPane.removeTabAt(1);
         psiPane.removeTabAt(0);
         psiRangeSlider.setParams(0,360,0,180);
         psiPane.addTab(bundle.getString("GUI.varPsiPanel.TabConstraints.tabTitle"), varPsiPanel); // NOI18N
         psiPane.addTab(bundle.getString("GUI.constPsiPanel.TabConstraints.tabTitle"), constPsiPanel);
      }
      execute();
   }//GEN-LAST:event_mapComboActionPerformed

   private void varRPanelComponentShown(java.awt.event.ComponentEvent evt)//GEN-FIRST:event_varRPanelComponentShown
   {//GEN-HEADEREND:event_varRPanelComponentShown
      rVar = true;
      execute();
   }//GEN-LAST:event_varRPanelComponentShown

   private void constRPanelComponentShown(java.awt.event.ComponentEvent evt)//GEN-FIRST:event_constRPanelComponentShown
   {//GEN-HEADEREND:event_constRPanelComponentShown
      rVar = false;
      execute();
   }//GEN-LAST:event_constRPanelComponentShown

   private void varPhiPanelComponentShown(java.awt.event.ComponentEvent evt)//GEN-FIRST:event_varPhiPanelComponentShown
   {//GEN-HEADEREND:event_varPhiPanelComponentShown
      phiVar = true;
      execute();
   }//GEN-LAST:event_varPhiPanelComponentShown

   private void constPhiPanelComponentShown(java.awt.event.ComponentEvent evt)//GEN-FIRST:event_constPhiPanelComponentShown
   {//GEN-HEADEREND:event_constPhiPanelComponentShown
      phiVar = false;
      execute();
   }//GEN-LAST:event_constPhiPanelComponentShown

   private void varPsiPanelComponentShown(java.awt.event.ComponentEvent evt)//GEN-FIRST:event_varPsiPanelComponentShown
   {//GEN-HEADEREND:event_varPsiPanelComponentShown
      psiVar = true;
      execute();
   }//GEN-LAST:event_varPsiPanelComponentShown

   private void constPsiPanelComponentShown(java.awt.event.ComponentEvent evt)//GEN-FIRST:event_constPsiPanelComponentShown
   {//GEN-HEADEREND:event_constPsiPanelComponentShown
      psiVar = false;
      execute();
   }//GEN-LAST:event_constPsiPanelComponentShown

   private void phiSliderStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_phiSliderStateChanged
   {//GEN-HEADEREND:event_phiSliderStateChanged

      execute();
   }//GEN-LAST:event_phiSliderStateChanged

   private void rSliderStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_rSliderStateChanged
   {//GEN-HEADEREND:event_rSliderStateChanged

      execute();
   }//GEN-LAST:event_rSliderStateChanged

   private void psiSliderStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_psiSliderStateChanged
   {//GEN-HEADEREND:event_psiSliderStateChanged

      execute();
   }//GEN-LAST:event_psiSliderStateChanged

   private void execute()
   {
      if (params == null || inField == null)
         return;
      params.setAdjusting(rRangeSlider.isAdjusting() || phiRangeSlider.isAdjusting() || psiRangeSlider.isAdjusting() ||
                          rSlider.isAdjusting() || phiSlider.isAdjusting() || psiSlider.isAdjusting());
      params.setMapType(mapCombo.getSelectedIndex());

      if (rVar)
      {
         if (xRButton.isSelected()) params.setRCoord(0);
         if (yRButton.isSelected()) params.setRCoord(1);
         if (zRButton.isSelected()) params.setRCoord(2);
         params.setRMin(rRangeSlider.getLow());
         params.setRMax(rRangeSlider.getUp());
      }
      else
      {
         params.setRCoord(Params.CONSTANT);
         params.setRMin(rSlider.getVal());
      }

      if (phiVar)
      {
         if (xPhiButton.isSelected()) params.setPhiCoord(0);
         if (yPhiButton.isSelected()) params.setPhiCoord(1);
         if (zPhiButton.isSelected()) params.setPhiCoord(2);
         params.setPhiMin(phiRangeSlider.getLow());
         params.setPhiMax(phiRangeSlider.getUp());
      }
      else
      {
         params.setPhiCoord(Params.CONSTANT);
         params.setPhiMin(phiSlider.getVal());
      }

      if (psiVar)
      {

         if (xPsiButton.isSelected()) params.setPsiCoord(0);
         if (yPsiButton.isSelected()) params.setPsiCoord(1);
         if (zPsiButton.isSelected()) params.setPsiCoord(2);
         if (mapCombo.getSelectedIndex() == 0)
         {
             params.setPsiMin(psiRangeSlider.getLow());
             params.setPsiMax(psiRangeSlider.getUp());
         }
         else
         {
             params.setZMin(psiRangeSlider.getLow());
             params.setZMax(psiRangeSlider.getUp());
         }

      }
      else
      {
         params.setPsiCoord(Params.CONSTANT);
         params.setPsiMin(psiSlider.getVal());
      }

      params.setAdjusting(rRangeSlider.isAdjusting() || phiRangeSlider.isAdjusting() || psiRangeSlider.isAdjusting() ||
                          rSlider.isAdjusting() || phiSlider.isAdjusting() || psiSlider.isAdjusting());

      params.fireStateChanged();
   }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.JPanel constPhiPanel;
    private javax.swing.JPanel constPsiPanel;
    private javax.swing.JPanel constRPanel;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JComboBox mapCombo;
    private javax.swing.JTabbedPane phiPane;
    private pl.edu.icm.visnow.gui.widgets.FloatSubRangeSlider.ExtendedFloatSubRangeSlider phiRangeSlider;
    private pl.edu.icm.visnow.gui.widgets.FloatSlider phiSlider;
    private javax.swing.JTabbedPane psiPane;
    private pl.edu.icm.visnow.gui.widgets.FloatSubRangeSlider.ExtendedFloatSubRangeSlider psiRangeSlider;
    private pl.edu.icm.visnow.gui.widgets.FloatSlider psiSlider;
    private javax.swing.JTabbedPane rPane;
    private pl.edu.icm.visnow.gui.widgets.FloatSubRangeSlider.ExtendedFloatSubRangeSlider rRangeSlider;
    private pl.edu.icm.visnow.gui.widgets.FloatSlider rSlider;
    private javax.swing.JPanel varPhiPanel;
    private javax.swing.JPanel varPsiPanel;
    private javax.swing.JPanel varRPanel;
    private javax.swing.JRadioButton xPhiButton;
    private javax.swing.JRadioButton xPsiButton;
    private javax.swing.JRadioButton xRButton;
    private javax.swing.JRadioButton yPhiButton;
    private javax.swing.JRadioButton yPsiButton;
    private javax.swing.JRadioButton yRButton;
    private javax.swing.JRadioButton zPhiButton;
    private javax.swing.JRadioButton zPsiButton;
    private javax.swing.JRadioButton zRButton;
    // End of variables declaration//GEN-END:variables
}
