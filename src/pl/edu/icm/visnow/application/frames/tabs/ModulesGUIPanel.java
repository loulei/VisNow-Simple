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
package pl.edu.icm.visnow.application.frames.tabs;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.util.Collection;
import javax.swing.DefaultComboBoxModel;
import org.apache.log4j.Logger;
import pl.edu.icm.visnow.application.frames.Frames;
import pl.edu.icm.visnow.engine.commands.Command;
import pl.edu.icm.visnow.engine.commands.SelectedModuleCommand;
import pl.edu.icm.visnow.engine.core.ModuleCore;
import pl.edu.icm.visnow.engine.main.ModuleBox;
import pl.edu.icm.visnow.gui.widgets.CoveringLayerPanel;
import pl.edu.icm.visnow.lib.basic.viewers.Viewer3D.Viewer3D;

/**
 *
 * @author gacek
 */
public class ModulesGUIPanel extends javax.swing.JPanel {

    private static final Logger LOGGER = Logger.getLogger(ModulesGUIPanel.class);

    protected ModuleCore lastCore = null, currentCore = null;
    protected Frames frames;

    public Frames getFrames() {
        return frames;
    }

    /**
     * Creates new form ModulesGUIPanel
     */
    public ModulesGUIPanel(Frames frames) {
        initComponents();
        this.frames = frames;
        refreshModules();
    }

    public boolean selectModule(String name) {
        if (name == null) {
            scrollPane.setViewportView(null);
            cardPanel.setVisible(false);
            if (comboBox.getItemCount() > 0) {
                comboBox.setSelectedIndex(comboBox.getItemCount() - 1);
            }
            currentCore = null;
        } else {
            if (getFrames().getApplication().getEngine().getModule(name) != null) {
                comboBox.setSelectedItem(name);
                currentCore = getFrames().getApplication().getEngine().getModule(name).getCore();
                if (lastCore != null && lastCore instanceof Viewer3D) {
                    ((Viewer3D) lastCore).getDisplayPanel().getControlsPanel().releaseLightEdit();
                }

                Component modulePanel = currentCore.getPanel();

                LOGGER.debug(modulePanel);

                if (modulePanel instanceof CoveringLayerPanel && ((CoveringLayerPanel)modulePanel).isFieldVisualisationGUI()) {
                    LOGGER.debug("NO SCROLL");
                    noScrollPanel.removeAll();
                    noScrollPanel.add(modulePanel, BorderLayout.CENTER);
                    noScrollPanel.repaint();
                    CardLayout cl = (CardLayout) (cardPanel.getLayout());
                    cl.show(cardPanel, "noScrollCard");
                } else {
                    LOGGER.debug("SCROLL");
                    scrollPane.setViewportView(modulePanel);
                    scrollPane.repaint();
                    CardLayout cl = (CardLayout) (cardPanel.getLayout());
                    cl.show(cardPanel, "scrollCard");
                }

                cardPanel.setVisible(true);
                
                lastCore = currentCore;
            }
        }
        revalidate();
        return true;
    }

    public ModuleCore getCurrentCore() {
        return currentCore;
    }

    public void refreshModules() {
        Collection<ModuleBox> modules = getFrames().getApplication().getEngine().getModules().values();
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        for (ModuleBox module : modules) {
            model.addElement(module.getName());
        }
        model.addElement("");
        comboBox.setModel(model);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        comboBox = new javax.swing.JComboBox();
        cardPanel = new javax.swing.JPanel();
        noScrollPanel = new javax.swing.JPanel();
        scrollPane = new javax.swing.JScrollPane();

        setLayout(new java.awt.BorderLayout());

        comboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxActionPerformed(evt);
            }
        });
        add(comboBox, java.awt.BorderLayout.NORTH);

        cardPanel.setLayout(new java.awt.CardLayout());

        noScrollPanel.setLayout(new java.awt.BorderLayout());
        cardPanel.add(noScrollPanel, "noScrollCard");

        scrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        cardPanel.add(scrollPane, "scrollCard");

        add(cardPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void comboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxActionPerformed
        if (getFrames().isSelectingEnabled()) {
            if (comboBox.getItemCount() > 0 && comboBox.getSelectedIndex() == comboBox.getItemCount() - 1) {
                getFrames().getApplication().getReceiver().receive(
                        new SelectedModuleCommand(Command.UI_FRAME_SELECTED_MODULE, null));
            } else {
                getFrames().getApplication().getReceiver().receive(
                        new SelectedModuleCommand(Command.UI_FRAME_SELECTED_MODULE, (String) comboBox.getSelectedItem()));
            }
            if (lastCore != null && lastCore instanceof Viewer3D) {
                ((Viewer3D) lastCore).getDisplayPanel().getControlsPanel().releaseLightEdit();
            }
        }
    }//GEN-LAST:event_comboBoxActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel cardPanel;
    private javax.swing.JComboBox comboBox;
    private javax.swing.JPanel noScrollPanel;
    private javax.swing.JScrollPane scrollPane;
    // End of variables declaration//GEN-END:variables
}
