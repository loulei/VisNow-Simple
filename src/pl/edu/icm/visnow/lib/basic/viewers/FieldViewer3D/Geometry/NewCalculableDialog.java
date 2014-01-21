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

import java.util.ArrayList;
import javax.swing.AbstractListModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import pl.edu.icm.visnow.system.main.VisNow;

/**
 *
 * @author babor
 */
public class NewCalculableDialog extends javax.swing.JDialog {
    private NewCalculableParams params = new NewCalculableParams();
    private CalculableParameter cp = null;

    private GeometryParams gparams = null;


    public enum Result {ACCEPT, CANCEL}
    private Result result = Result.CANCEL;


    /** Creates new form NewCalculableDialog */
    public NewCalculableDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
//        setIconImage(new ImageIcon(getClass().getResource( VisNow.getIconPath() )).getImage());

        selectedPointsList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                updateGUI();
            }
        });

        allPointsList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                updateGUI();
            }
        });

        updateCalculableList();
        typeCBActionPerformed(null);

        params.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                selectedPointsList.setModel(new PointListModel(params.getPoints()));

                if(params.getType() != null)
                    preNameLabel.setText(params.getType().getPreName());

                if(params.getType() != null && params.getName() != null && params.getName().length() > 0 && params.getPoints().size() > 1) {
                    CalculableParameter tmpcp = CalculableParamsPool.getCalculable(params.getType(), params.getName());
                    tmpcp.setPointDescriptors(params.getPoints());
                    addButton.setEnabled(tmpcp.isPointDescriptorsReady());
                }
            }
        });

        updateGUI();
    }

    private void updateCalculableList() {
        typeCB.setModel(new DefaultComboBoxModel(CalculableParamsPool.CalculableType.values()));
    }

    private void updateGUI() {
        if(gparams == null) {
            addPointButton.setEnabled(false);
            allPointsList.setEnabled(false);
            selectedPointsList.setEnabled(false);
            preNameLabel.setText(" ");
            return;
        }

        allPointsList.setEnabled(true);
        selectedPointsList.setEnabled(true);

        //addPointButton.setEnabled(gparams.getPointsDescriptors().size() > 1);
        addPointButton.setEnabled(!allPointsList.getSelectionModel().isSelectionEmpty());
        removePointButton.setEnabled(!selectedPointsList.getSelectionModel().isSelectionEmpty());
        preNameLabel.setText(params.getType().getPreName());
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

        topPanel = new javax.swing.JPanel();
        infoLabel = new javax.swing.JLabel();
        mainPanel = new javax.swing.JPanel();
        mainTabbedPane = new javax.swing.JTabbedPane();
        typePanel = new javax.swing.JPanel();
        typeCB = new javax.swing.JComboBox();
        nameTF = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        preNameLabel = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        paramsStringTF = new javax.swing.JTextField();
        pointsPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        allPointsList = new javax.swing.JList();
        addPointButton = new javax.swing.JButton();
        removePointButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        selectedPointsList = new javax.swing.JList();
        bottomPanel = new javax.swing.JPanel();
        cancleButton = new javax.swing.JButton();
        addButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Add calculable");

        topPanel.setName("topPanel"); // NOI18N
        topPanel.setPreferredSize(new java.awt.Dimension(400, 30));
        topPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        infoLabel.setText("Fill all required parameters of a new calculable");
        infoLabel.setName("infoLabel"); // NOI18N
        topPanel.add(infoLabel);

        getContentPane().add(topPanel, java.awt.BorderLayout.NORTH);

        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setLayout(new java.awt.BorderLayout());

        mainTabbedPane.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        mainTabbedPane.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        mainTabbedPane.setName("mainTabbedPane"); // NOI18N

        typePanel.setName("typePanel"); // NOI18N
        typePanel.setLayout(new java.awt.GridBagLayout());

        typeCB.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        typeCB.setMaximumSize(new java.awt.Dimension(200, 24));
        typeCB.setMinimumSize(new java.awt.Dimension(200, 24));
        typeCB.setName("typeCB"); // NOI18N
        typeCB.setPreferredSize(new java.awt.Dimension(200, 24));
        typeCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                typeCBActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 20, 0, 20);
        typePanel.add(typeCB, gridBagConstraints);

        nameTF.setMaximumSize(new java.awt.Dimension(140, 24));
        nameTF.setMinimumSize(new java.awt.Dimension(140, 24));
        nameTF.setName("nameTF"); // NOI18N
        nameTF.setPreferredSize(new java.awt.Dimension(140, 24));
        nameTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nameTFActionPerformed(evt);
            }
        });
        nameTF.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                nameTFFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 65;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 15, 20);
        typePanel.add(nameTF, gridBagConstraints);

        jLabel1.setText("Select calculable type:");
        jLabel1.setName("jLabel1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        typePanel.add(jLabel1, gridBagConstraints);

        jLabel2.setText("Enter calculable name:");
        jLabel2.setName("jLabel2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(20, 10, 0, 0);
        typePanel.add(jLabel2, gridBagConstraints);

        preNameLabel.setText(" ");
        preNameLabel.setName("preNameLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 20, 0, 0);
        typePanel.add(preNameLabel, gridBagConstraints);

        jLabel3.setText("Enter parameters string:");
        jLabel3.setName("jLabel3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 10);
        typePanel.add(jLabel3, gridBagConstraints);

        paramsStringTF.setMaximumSize(new java.awt.Dimension(140, 24));
        paramsStringTF.setMinimumSize(new java.awt.Dimension(140, 24));
        paramsStringTF.setName("paramsStringTF"); // NOI18N
        paramsStringTF.setPreferredSize(new java.awt.Dimension(140, 24));
        paramsStringTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paramsStringTFActionPerformed(evt);
            }
        });
        paramsStringTF.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                paramsStringTFFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 20, 15, 20);
        typePanel.add(paramsStringTF, gridBagConstraints);

        mainTabbedPane.addTab("Type & Name", typePanel);

        pointsPanel.setName("pointsPanel"); // NOI18N
        pointsPanel.setLayout(new java.awt.GridBagLayout());

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        allPointsList.setName("allPointsList"); // NOI18N
        jScrollPane2.setViewportView(allPointsList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pointsPanel.add(jScrollPane2, gridBagConstraints);

        addPointButton.setText("Add");
        addPointButton.setMaximumSize(new java.awt.Dimension(90, 25));
        addPointButton.setMinimumSize(new java.awt.Dimension(90, 25));
        addPointButton.setName("addPointButton"); // NOI18N
        addPointButton.setPreferredSize(new java.awt.Dimension(90, 25));
        addPointButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addPointButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 5);
        pointsPanel.add(addPointButton, gridBagConstraints);

        removePointButton.setText("Remove");
        removePointButton.setEnabled(false);
        removePointButton.setMaximumSize(new java.awt.Dimension(90, 25));
        removePointButton.setMinimumSize(new java.awt.Dimension(90, 25));
        removePointButton.setName("removePointButton"); // NOI18N
        removePointButton.setPreferredSize(new java.awt.Dimension(90, 25));
        removePointButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removePointButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 5);
        pointsPanel.add(removePointButton, gridBagConstraints);

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        selectedPointsList.setName("selectedPointsList"); // NOI18N
        jScrollPane1.setViewportView(selectedPointsList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pointsPanel.add(jScrollPane1, gridBagConstraints);

        mainTabbedPane.addTab("Points", pointsPanel);

        mainPanel.add(mainTabbedPane, java.awt.BorderLayout.CENTER);

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

        bottomPanel.setName("bottomPanel"); // NOI18N
        bottomPanel.setPreferredSize(new java.awt.Dimension(400, 30));
        bottomPanel.setLayout(new java.awt.GridBagLayout());

        cancleButton.setText("Cancel");
        cancleButton.setName("cancleButton"); // NOI18N
        cancleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancleButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 5);
        bottomPanel.add(cancleButton, gridBagConstraints);

        addButton.setText("Add");
        addButton.setEnabled(false);
        addButton.setName("addButton"); // NOI18N
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 5);
        bottomPanel.add(addButton, gridBagConstraints);

        getContentPane().add(bottomPanel, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancleButtonActionPerformed
        result = Result.CANCEL;
        this.dispose();
    }//GEN-LAST:event_cancleButtonActionPerformed

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        result = Result.ACCEPT;

        if(params.getParametersString() != null && params.getParametersString().length() > 1) {
            String[] ps = params.getParametersString().split(":");
            float[] p = null;
            try {
                p = new float[ps.length];
                for (int i = 0; i < p.length; i++) {
                    p[i] = Float.parseFloat(ps[i]);
                }
            } catch (NumberFormatException ex) {
                p = null;
            }
            this.cp = CalculableParamsPool.getCalculable(params.getType(), params.getName(), p);
        } else {
            this.cp = CalculableParamsPool.getCalculable(params.getType(), params.getName());
        }
        this.cp.setPointDescriptors(params.getPoints());
        this.dispose();
    }//GEN-LAST:event_addButtonActionPerformed

    private void typeCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_typeCBActionPerformed
     
        params.setType((CalculableParamsPool.CalculableType)typeCB.getSelectedItem());

    }//GEN-LAST:event_typeCBActionPerformed

    private void nameTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nameTFActionPerformed
        params.setName(nameTF.getText());
    }//GEN-LAST:event_nameTFActionPerformed

    private void nameTFFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_nameTFFocusLost
        nameTFActionPerformed(null);
    }//GEN-LAST:event_nameTFFocusLost

    private void addPointButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addPointButtonActionPerformed
        int[] indices = allPointsList.getSelectedIndices();
        if(indices == null)
            return;

        params.addPoints(gparams.getPointsDescriptors(indices));
    }//GEN-LAST:event_addPointButtonActionPerformed

    private void removePointButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removePointButtonActionPerformed
        if(selectedPointsList.getSelectionModel().isSelectionEmpty())
            return;

        params.removePoint(selectedPointsList.getSelectedIndex());
    }//GEN-LAST:event_removePointButtonActionPerformed

    private void paramsStringTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_paramsStringTFActionPerformed
        params.setParametersString(paramsStringTF.getText());
    }//GEN-LAST:event_paramsStringTFActionPerformed

    private void paramsStringTFFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_paramsStringTFFocusLost
        paramsStringTFActionPerformed(null);
    }//GEN-LAST:event_paramsStringTFFocusLost


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JButton addPointButton;
    private javax.swing.JList allPointsList;
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JButton cancleButton;
    private javax.swing.JLabel infoLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JTabbedPane mainTabbedPane;
    private javax.swing.JTextField nameTF;
    private javax.swing.JTextField paramsStringTF;
    private javax.swing.JPanel pointsPanel;
    private javax.swing.JLabel preNameLabel;
    private javax.swing.JButton removePointButton;
    private javax.swing.JList selectedPointsList;
    private javax.swing.JPanel topPanel;
    private javax.swing.JComboBox typeCB;
    private javax.swing.JPanel typePanel;
    // End of variables declaration//GEN-END:variables


    /**
     * @return the result
     */
    public Result getResult() {
        return result;
    }

    /**
     * @return the cp
     */
    public CalculableParameter getNewCalculableParameter() {
        return cp;
    }



    /**
     * @param gparams the gparams to set
     */
    public void setGeometryParams(GeometryParams gparams) {
        this.gparams = gparams;
        if(gparams != null) {
            allPointsList.setModel(new PointListModel(gparams.getPointsDescriptors()));
        }
        updateGUI();
    }



    private class PointListModel extends AbstractListModel {
        ArrayList<PointDescriptor> points;

        public PointListModel(ArrayList<PointDescriptor> points) {
            this.points = points;
        }

        @Override
        public int getSize() {
            if(points == null)
                return 0;

            return points.size();
        }

        @Override
        public Object getElementAt(int index) {
            if(points == null)
                return null;

            return points.get(index);
        }

        
    }




}
