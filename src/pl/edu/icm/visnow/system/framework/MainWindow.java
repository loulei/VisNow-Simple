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
package pl.edu.icm.visnow.system.framework;

import java.awt.Component;
import java.awt.Insets;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import javax.swing.*;
import org.apache.log4j.Logger;
import pl.edu.icm.visnow.application.area.widgets.BgPanel;
import pl.edu.icm.visnow.engine.error.Displayer;
import pl.edu.icm.visnow.engine.exception.VNException;
import pl.edu.icm.visnow.engine.exception.VNSystemException;
import pl.edu.icm.visnow.gui.icons.UIIconLoader;
import pl.edu.icm.visnow.gui.icons.UIIconLoader.IconType;
import pl.edu.icm.visnow.system.config.PreferencesWindow;
import pl.edu.icm.visnow.system.main.VisNow;
import pl.edu.icm.visnow.system.swing.JComponentViewer;
import pl.edu.icm.visnow.system.swing.VNSwingUtils;
import pl.edu.icm.visnow.system.utils.log.LogWindow;
import pl.edu.icm.visnow.system.utils.usermessage.Level;
import pl.edu.icm.visnow.system.utils.usermessage.UserMessage;
import pl.edu.icm.visnow.system.utils.usermessage.UserMessageListener;

/**
 *
 * @author Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class MainWindow extends javax.swing.JFrame {

    private static final Logger LOGGER = Logger.getLogger(MainWindow.class);
    protected ApplicationsPanel applicationsPanel;
    protected MainMenu menu;
    protected PreferencesWindow preferencesWindow;
    protected ColorMapEditorFrame colorMapEditorFrame;
    protected static InfoFrame infoFrame = new InfoFrame();

    //<editor-fold defaultstate="collapsed" desc=" Getters ">
    public ApplicationsPanel getApplicationsPanel() {
        return applicationsPanel;
    }

    public ColorMapEditorFrame getColorMapEditorFrame() {
        return colorMapEditorFrame;
    }

    public MainMenu getMainMenu() {
        return menu;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" [CONSTRUCTOR] inits ">
    private void initTemplates() {
        addTemplateFolder(templatesMenu, VisNow.get().getMainConfig().getTemplateRoot());
    }

    private void addTemplateFolder(JMenu menu, File file) {
        File[] tab = file.listFiles();
        Arrays.sort(tab, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });

        for (int i = 0; i < tab.length; ++i) {
            if (tab[i].isDirectory()) {
                JMenu tmp = new JMenu(tab[i].getName());
                addTemplateFolder(tmp, tab[i]);
                menu.add(tmp);
            }
        }
        for (int i = 0; i < tab.length; ++i) {
            if (tab[i].isDirectory())
                continue;
            JMenuItem item = new JMenuItem(tab[i].getName());
            item.addActionListener(new TemplateActionListener(tab[i], this.menu));
            menu.add(item);
        }
    }

    //<editor-fold defaultstate="collapsed" desc=" [CONSTRUCTOR]  ">
    /**
     * Creates new form MainWindow
     */
    public MainWindow() {
        initComponents();

        actionMenu.setVisible(false);
        jMenu1.setVisible(false);
        jSeparator1.setVisible(false);
        jSeparator3.setVisible(false);
        templatesMenu.setVisible(false);
        viewMenu.setVisible(false);
        undoItem.setVisible(false);
        redoItem.setVisible(false);
        historyItem.setVisible(false);
        setIconImage(new ImageIcon(getClass().getResource("/pl/edu/icm/visnow/gui/icons/big/visnow.png")).getImage());
        applicationsPanel = new ApplicationsPanel();
        preferencesWindow = new PreferencesWindow();
        menu = new MainMenu(this);
        ArrayList<Component> readers = menu.getReadersMenu(null);
        if (readers != null)
            for (int i = 0; i < readers.size(); i++) {
                openDataMenu.add(readers.get(i));
            }
        else
            openDataMenu.setVisible(false);

        ArrayList<Component> testdata = menu.getTestdataMenu(null);
        if (testdata != null)
            for (int i = 0; i < testdata.size(); i++) {
                openTestDataMenu.add(testdata.get(i));
            }
        else
            openTestDataMenu.setVisible(false);

        VNSwingUtils.setFillerComponent(mainPanel, applicationsPanel);
        setTitle(VisNow.TITLE + " v" + VisNow.VERSION);

        userMessagePanelToggle(false);
    }

    public UserMessageListener[] getUserMessageListeners() {
        UserMessageListener labelUML = new UserMessageListener() {
            @Override
            public void newMessage(UserMessage message) {
                Insets insets = statusPanelLabel.getInsets();
                int size = statusPanelLabel.getHeight() - insets.top - insets.bottom - 2; // height - border - margin (2px)

                IconType iconType = IconType.INFO;
                if (message.getLevel() == Level.ERROR) iconType = IconType.ERROR;
                if (message.getLevel() == Level.WARNING) iconType = IconType.WARNING;
                statusPanelLabelSet(message.toString(), UIIconLoader.getIcon(iconType, size, size));
            }
        };

        return new UserMessageListener[]{labelUML, userMessagePanel};
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        bigSplitPane = new javax.swing.JSplitPane();
        jPopupMenu1 = new javax.swing.JPopupMenu();
        jPanel1 = new javax.swing.JPanel();
        statusPanel = new javax.swing.JPanel();
        userMessagePanel = new pl.edu.icm.visnow.system.framework.UserMessagePanel();
        statusPanelLabel = new javax.swing.JLabel();
        statusPanelMemoryMonitor = new pl.edu.icm.visnow.gui.widgets.MemoryMonitor();
        mainPanel = new javax.swing.JPanel();
        jMenuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        newFileItem = new javax.swing.JMenuItem();
        openDataMenu = new javax.swing.JMenu();
        openTestDataMenu = new javax.swing.JMenu();
        jMenu1 = new javax.swing.JMenu();
        openFileItem = new javax.swing.JMenuItem();
        saveFileItem = new javax.swing.JMenuItem();
        saveAsFileItem = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        closeFileItem = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        templatesMenu = new javax.swing.JMenu();
        jSeparator1 = new javax.swing.JSeparator();
        exitFileItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        undoItem = new javax.swing.JMenuItem();
        redoItem = new javax.swing.JMenuItem();
        historyItem = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JSeparator();
        deleteItem = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JSeparator();
        colormapItem = new javax.swing.JMenuItem();
        preferencesEditMenuItem = new javax.swing.JMenuItem();
        actionMenu = new javax.swing.JMenu();
        interruptItem = new javax.swing.JMenuItem();
        clearStateItem = new javax.swing.JMenuItem();
        oopsItem = new javax.swing.JMenuItem();
        viewMenu = new javax.swing.JMenu();
        viewApplicationItem = new javax.swing.JCheckBoxMenuItem();
        viewWorkspaceItem = new javax.swing.JCheckBoxMenuItem();
        viewLibrariesItem = new javax.swing.JCheckBoxMenuItem();
        viewUIItem = new javax.swing.JCheckBoxMenuItem();
        viewPortsItem = new javax.swing.JCheckBoxMenuItem();
        viewRootsItem = new javax.swing.JCheckBoxMenuItem();
        helpMenu = new javax.swing.JMenu();
        helpMenuItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        aboutItem = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();

        bigSplitPane.setDividerLocation(0);
        bigSplitPane.setOneTouchExpandable(true);

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("VisNow");
        setBounds(new java.awt.Rectangle(600, 20, 850, 600));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setLayout(new java.awt.BorderLayout());

        statusPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        statusPanel.setLayout(new java.awt.GridBagLayout());

        userMessagePanel.addCloseListener(new pl.edu.icm.visnow.system.framework.UserMessagePanel.CloseListener() {
            public void closeButtonAction(java.util.EventObject evt) {
                userMessagePanelCloseButtonAction(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        statusPanel.add(userMessagePanel, gridBagConstraints);

        statusPanelLabel.setBackground(new java.awt.Color(221, 221, 221));
        statusPanelLabel.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        statusPanelLabel.setForeground(new java.awt.Color(85, 85, 85));
        statusPanelLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 10));
        statusPanelLabel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        statusPanelLabel.setOpaque(true);
        statusPanelLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                statusPanelLabelMouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        statusPanel.add(statusPanelLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        statusPanel.add(statusPanelMemoryMonitor, gridBagConstraints);

        jPanel1.add(statusPanel, java.awt.BorderLayout.SOUTH);

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 882, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 303, Short.MAX_VALUE)
        );

        jPanel1.add(mainPanel, java.awt.BorderLayout.CENTER);

        fileMenu.setMnemonic('f');
        fileMenu.setText("File"); // NOI18N

        newFileItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        newFileItem.setMnemonic('n');
        newFileItem.setText("New"); // NOI18N
        newFileItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newFileItemActionPerformed(evt);
            }
        });
        fileMenu.add(newFileItem);

        openDataMenu.setMnemonic('o');
        openDataMenu.setText("Open data file");
        fileMenu.add(openDataMenu);

        openTestDataMenu.setMnemonic('t');
        openTestDataMenu.setText("Open test data");
        fileMenu.add(openTestDataMenu);

        jMenu1.setText("XML IO");

        openFileItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        openFileItem.setMnemonic('o');
        openFileItem.setText("Open..."); // NOI18N
        openFileItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openFileItemActionPerformed(evt);
            }
        });
        jMenu1.add(openFileItem);

        saveFileItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        saveFileItem.setMnemonic('s');
        saveFileItem.setText("Save"); // NOI18N
        saveFileItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveFileItemActionPerformed(evt);
            }
        });
        jMenu1.add(saveFileItem);

        saveAsFileItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        saveAsFileItem.setMnemonic('a');
        saveAsFileItem.setText("Save as..."); // NOI18N
        saveAsFileItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsFileItemActionPerformed(evt);
            }
        });
        jMenu1.add(saveAsFileItem);

        fileMenu.add(jMenu1);

        jMenuItem3.setMnemonic('l');
        jMenuItem3.setText("Load network");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        fileMenu.add(jMenuItem3);

        jMenuItem2.setMnemonic('s');
        jMenuItem2.setText("Save network");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        fileMenu.add(jMenuItem2);

        closeFileItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        closeFileItem.setMnemonic('c');
        closeFileItem.setText("Close");
        closeFileItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeFileItemActionPerformed(evt);
            }
        });
        fileMenu.add(closeFileItem);
        fileMenu.add(jSeparator3);

        templatesMenu.setMnemonic('t');
        templatesMenu.setText("Templates");
        fileMenu.add(templatesMenu);
        fileMenu.add(jSeparator1);

        exitFileItem.setMnemonic('x');
        exitFileItem.setText("Exit"); // NOI18N
        exitFileItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitFileItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitFileItem);

        jMenuBar.add(fileMenu);

        editMenu.setMnemonic('e');
        editMenu.setText("Edit"); // NOI18N

        undoItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_MASK));
        undoItem.setMnemonic('u');
        undoItem.setText("Undo"); // NOI18N
        undoItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                undoItemActionPerformed(evt);
            }
        });
        editMenu.add(undoItem);

        redoItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, java.awt.event.InputEvent.CTRL_MASK));
        redoItem.setMnemonic('r');
        redoItem.setText("Redo"); // NOI18N
        redoItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                redoItemActionPerformed(evt);
            }
        });
        editMenu.add(redoItem);

        historyItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        historyItem.setMnemonic('h');
        historyItem.setText("History...");
        historyItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                historyItemActionPerformed(evt);
            }
        });
        editMenu.add(historyItem);
        editMenu.add(jSeparator4);

        deleteItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
        deleteItem.setMnemonic('D');
        deleteItem.setText("Delete");
        deleteItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteItemActionPerformed(evt);
            }
        });
        editMenu.add(deleteItem);
        editMenu.add(jSeparator5);

        colormapItem.setMnemonic('c');
        colormapItem.setText("Colormap Editor");
        colormapItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colormapItemActionPerformed(evt);
            }
        });
        editMenu.add(colormapItem);

        preferencesEditMenuItem.setMnemonic('p');
        preferencesEditMenuItem.setText("Preferences");
        preferencesEditMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                preferencesEditMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(preferencesEditMenuItem);

        jMenuBar.add(editMenu);

        actionMenu.setMnemonic('a');
        actionMenu.setText("Action"); // NOI18N

        interruptItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_SPACE, java.awt.event.InputEvent.CTRL_MASK));
        interruptItem.setMnemonic('i');
        interruptItem.setText("Interrupt"); // NOI18N
        interruptItem.setEnabled(false);
        interruptItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                interruptItemActionPerformed(evt);
            }
        });
        actionMenu.add(interruptItem);

        clearStateItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, java.awt.event.InputEvent.SHIFT_MASK));
        clearStateItem.setText("Clear state");
        clearStateItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearStateItemActionPerformed(evt);
            }
        });
        actionMenu.add(clearStateItem);

        oopsItem.setText("Ooops!");
        oopsItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                oopsItemActionPerformed(evt);
            }
        });
        actionMenu.add(oopsItem);

        jMenuBar.add(actionMenu);

        viewMenu.setMnemonic('v');
        viewMenu.setText("View");

        viewApplicationItem.setMnemonic('a');
        viewApplicationItem.setSelected(true);
        viewApplicationItem.setText("Application");
        viewMenu.add(viewApplicationItem);

        viewWorkspaceItem.setMnemonic('w');
        viewWorkspaceItem.setSelected(true);
        viewWorkspaceItem.setText("Workspace");
        viewWorkspaceItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewWorkspaceItemActionPerformed(evt);
            }
        });
        viewMenu.add(viewWorkspaceItem);

        viewLibrariesItem.setMnemonic('l');
        viewLibrariesItem.setSelected(true);
        viewLibrariesItem.setText("Libraries");
        viewLibrariesItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewLibrariesItemActionPerformed(evt);
            }
        });
        viewMenu.add(viewLibrariesItem);

        viewUIItem.setMnemonic('u');
        viewUIItem.setSelected(true);
        viewUIItem.setText("UI");
        viewUIItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewUIItemActionPerformed(evt);
            }
        });
        viewMenu.add(viewUIItem);

        viewPortsItem.setMnemonic('p');
        viewPortsItem.setSelected(true);
        viewPortsItem.setText("Ports");
        viewMenu.add(viewPortsItem);

        viewRootsItem.setMnemonic('r');
        viewRootsItem.setSelected(true);
        viewRootsItem.setText("Roots");
        viewMenu.add(viewRootsItem);

        jMenuBar.add(viewMenu);

        helpMenu.setMnemonic('h');
        helpMenu.setText("Help"); // NOI18N

        helpMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        helpMenuItem.setMnemonic('h');
        helpMenuItem.setText("Help contents");
        helpMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(helpMenuItem);
        helpMenu.add(jSeparator2);

        aboutItem.setMnemonic('a');
        aboutItem.setText("About"); // NOI18N
        aboutItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutItemActionPerformed(evt);
            }
        });
        helpMenu.add(aboutItem);

        jMenuItem1.setMnemonic('l');
        jMenuItem1.setText("Show log");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        helpMenu.add(jMenuItem1);

        jMenuBar.add(helpMenu);

        setJMenuBar(jMenuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        setBounds(600, 20, 892, 600);
    }// </editor-fold>//GEN-END:initComponents

    //<editor-fold defaultstate="collapsed" desc=" [Menu] file ">
private void newFileItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newFileItemActionPerformed
    menu.newApplication();
}//GEN-LAST:event_newFileItemActionPerformed

private void openFileItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openFileItemActionPerformed
    //   new Thread(new Runnable(){public void run() {
    menu.openApplication();
    //   }}).start();
}//GEN-LAST:event_openFileItemActionPerformed

private void saveFileItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveFileItemActionPerformed
//    new Thread(new Runnable(){public void run() {
    menu.saveApplication();
    //   }}).start();
}//GEN-LAST:event_saveFileItemActionPerformed

private void saveAsFileItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsFileItemActionPerformed
//    new Thread(new Runnable(){public void run() {
    menu.saveAsApplication();
//    }}).start();
}//GEN-LAST:event_saveAsFileItemActionPerformed

private void closeFileItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeFileItemActionPerformed
    menu.closeApplication();
}//GEN-LAST:event_closeFileItemActionPerformed

private void exitFileItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitFileItemActionPerformed
    formWindowClosing(null);
}//GEN-LAST:event_exitFileItemActionPerformed
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" [Menu] edif ">
private void undoItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_undoItemActionPerformed
    menu.undo();
}//GEN-LAST:event_undoItemActionPerformed

private void redoItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_redoItemActionPerformed
    menu.redo();

}//GEN-LAST:event_redoItemActionPerformed

private void historyItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_historyItemActionPerformed
    menu.showHistory();
}//GEN-LAST:event_historyItemActionPerformed
    //</editor-fold>

    public void showPreferences() {
        preferencesWindow.init();
    }

private void preferencesEditMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_preferencesEditMenuItemActionPerformed
    preferencesWindow.init();
}//GEN-LAST:event_preferencesEditMenuItemActionPerformed

    //<editor-fold defaultstate="collapsed" desc=" [Menu] action ">
private void interruptItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_interruptItemActionPerformed
    menu.interrupt();
}//GEN-LAST:event_interruptItemActionPerformed
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" [Menu] help ">
private void aboutItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutItemActionPerformed
    menu.about();
}//GEN-LAST:event_aboutItemActionPerformed
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Closing ">
private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    while (applicationsPanel.isApplicationOpened()) {
        if(!applicationsPanel.removeCurrentApplication()) {
            return;
        }
    }
    VisNow.get().backup();
    System.exit(0);
}//GEN-LAST:event_formWindowClosing
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" [Menu] delete ">
private void deleteItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteItemActionPerformed
    menu.delete();
}//GEN-LAST:event_deleteItemActionPerformed
    //</editor-fold>

private void colormapItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colormapItemActionPerformed
    if (colorMapEditorFrame == null) {
        colorMapEditorFrame = new ColorMapEditorFrame();
    }
    colorMapEditorFrame.setVisible(true);
}//GEN-LAST:event_colormapItemActionPerformed

private void oopsItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_oopsItemActionPerformed

    new JFileChooser().showOpenDialog(this);

    Object o = null;
    try {
        o.toString();
        float f = 1 / 0;
    } catch (Exception ex) {
        VNException e = new VNException(
                200907101000L,
                "Some exception",
                ex,
                this,
                Thread.currentThread());
        VNSystemException ee = new VNSystemException(
                200907101001L,
                "Do not divide by 0!",
                e,
                this,
                Thread.currentThread());
        Displayer.display(200907101002L, ee, this, "Errors catched!");
    }
}//GEN-LAST:event_oopsItemActionPerformed

private void clearStateItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearStateItemActionPerformed
    menu.clearState();
}//GEN-LAST:event_clearStateItemActionPerformed

private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
    menu.betaSave();
}//GEN-LAST:event_jMenuItem2ActionPerformed

private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
    menu.betaOpen();
}//GEN-LAST:event_jMenuItem3ActionPerformed

private void helpMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpMenuItemActionPerformed
    menu.help();
}//GEN-LAST:event_helpMenuItemActionPerformed

private void viewWorkspaceItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewWorkspaceItemActionPerformed
    //TODO
}//GEN-LAST:event_viewWorkspaceItemActionPerformed

private void viewLibrariesItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewLibrariesItemActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_viewLibrariesItemActionPerformed

private void viewUIItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewUIItemActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_viewUIItemActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        LogWindow.openLogWindow();
    }//GEN-LAST:event_jMenuItem1ActionPerformed
    private boolean statusPanelExpanded = false;
    private void statusPanelLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statusPanelLabelMouseClicked
        userMessagePanelToggle(!statusPanelExpanded);
    }//GEN-LAST:event_statusPanelLabelMouseClicked

    private void userMessagePanelCloseButtonAction(java.util.EventObject evt) {//GEN-FIRST:event_userMessagePanelCloseButtonAction
        userMessagePanelToggle(false);
    }//GEN-LAST:event_userMessagePanelCloseButtonAction

    private void userMessagePanelToggle(boolean expanded) {
        statusPanelExpanded = expanded;
        statusPanelLabel.setVisible(!statusPanelExpanded);
        //TODO: check the flow
        //clear status label on collapse (in expanded mode is not visible anyway)
        if (!statusPanelExpanded) statusPanelLabelSet("", null);

        statusPanelMemoryMonitor.setVisible(!statusPanelExpanded);


        userMessagePanel.setVisible(statusPanelExpanded);

        statusPanel.revalidate();
    }

    /**
     * Sets status panel label text and icon.
     *
     * @param icon icon to set or null to remove icon
     */
    private void statusPanelLabelSet(String text, Icon icon) {
        statusPanelLabel.setText(text);
        statusPanelLabel.setIcon(icon);
    }

    public static InfoFrame getInfoFrame() {
        return infoFrame;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutItem;
    private javax.swing.JMenu actionMenu;
    private javax.swing.JSplitPane bigSplitPane;
    private javax.swing.JMenuItem clearStateItem;
    private javax.swing.JMenuItem closeFileItem;
    private javax.swing.JMenuItem colormapItem;
    private javax.swing.JMenuItem deleteItem;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuItem exitFileItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JMenuItem helpMenuItem;
    private javax.swing.JMenuItem historyItem;
    private javax.swing.JMenuItem interruptItem;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuItem newFileItem;
    private javax.swing.JMenuItem oopsItem;
    private javax.swing.JMenu openDataMenu;
    private javax.swing.JMenuItem openFileItem;
    private javax.swing.JMenu openTestDataMenu;
    private javax.swing.JMenuItem preferencesEditMenuItem;
    private javax.swing.JMenuItem redoItem;
    private javax.swing.JMenuItem saveAsFileItem;
    private javax.swing.JMenuItem saveFileItem;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JLabel statusPanelLabel;
    private pl.edu.icm.visnow.gui.widgets.MemoryMonitor statusPanelMemoryMonitor;
    private javax.swing.JMenu templatesMenu;
    private javax.swing.JMenuItem undoItem;
    private pl.edu.icm.visnow.system.framework.UserMessagePanel userMessagePanel;
    private javax.swing.JCheckBoxMenuItem viewApplicationItem;
    private javax.swing.JCheckBoxMenuItem viewLibrariesItem;
    private javax.swing.JMenu viewMenu;
    private javax.swing.JCheckBoxMenuItem viewPortsItem;
    private javax.swing.JCheckBoxMenuItem viewRootsItem;
    private javax.swing.JCheckBoxMenuItem viewUIItem;
    private javax.swing.JCheckBoxMenuItem viewWorkspaceItem;
    // End of variables declaration//GEN-END:variables
}
