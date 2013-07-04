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

package pl.edu.icm.visnow.system.utils.log;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;
import pl.edu.icm.visnow.system.main.VisNow;

/**
 * Log Frame to:
 * - show log files - found in log directory (taken from VisNow configuration) e.g., user.home/.visnow/log;
 * - selecting different(old) log files (which are most likely in format vnlog.log_DATE.log
 * - color and filter different log levels.
 * 
 * It starts loop thread which test:
 * - if any new file appeared in log directory (than log list is refreshed + corresponding combo box)
 * - if current log file has been appended/removed/created (this is actually done by logReader), 
 *   then list with log lines is refreshed.
 * @author szpak
 */
public class LogWindow extends javax.swing.JFrame {
    DefaultListModel logLinesListModel;
    ArrayList<LogLine> logLines = new ArrayList<LogLine>();
    Map<LogLine.Level, Boolean> lineFilter = new HashMap<LogLine.Level, Boolean>();
    /** How often check if new lines appear in log. */
    private long updateTime = 500;
    //TODO: get from visnow configuration
    private String logPath = VisNow.getConfigDir(VisNow.LOG_OUTPUT_DIR,false).toString();
    //TODO: get from visnow configuration
    private String logFileNamePrefix = "vnlog.log";
    private String logFileNamePattern = logFileNamePrefix+".*";
    /** current log file used in loop to test if user selected different log file. */
    private String currentLogFile = "";
    private LogReader currentLogReader;    
    private DefaultComboBoxModel logFilesModel;
    /** number of files in log directory (to test if any new log file appeared. */
    private int lastFilesNum;
    
    /** set to null to stop the thread. */
    private Thread readerThread;

    /** autoscroll flags */
    boolean autoscroll = true;
    private int scrollPreviousMax = 0;
    
    private static LogWindow logWindow;
    
    private LogWindow() {
        initComponents();
        //add checkboxes for filtering levels
        for (LogLine.Level level: LogLine.Level.values()) {
            JCheckBox cb = new StatusCheckBox(level.name(),true,LogWindowColorRenderer.colorMap.get(level));
            cb.setMargin(new Insets(3,5,3,5));
            cb.setOpaque(true);
            cb.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JCheckBox cb = (JCheckBox)e.getSource();
                    lineFilter.put(LogLine.Level.valueOf(cb.getText()), cb.isSelected());
                    autoscroll = isScrollAtBottom(); //autoscroll on filter change
                    logLinesListModel.clear();
                    for (LogLine line:logLines)
                        if (lineFilter.get(line.getLevel()))
                            logLinesListModel.addElement(line);
                }
            });
            filterPanel.add(cb);
        }
        
        for (LogLine.Level level: LogLine.Level.values())
            lineFilter.put(level,true);
        logLinesListModel = new DefaultListModel();
        logLinesList.setModel(logLinesListModel);
        logLinesList.setCellRenderer(new LogWindowColorRenderer());
        
        //JScrollPane - autoscroll
        logLinesScrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {  
            public void adjustmentValueChanged(AdjustmentEvent e) {
                int scrollMax = e.getAdjustable().getMaximum()-e.getAdjustable().getVisibleAmount();
                //if autoscroll and something has been changed
                if (autoscroll && scrollMax != scrollPreviousMax)
                    e.getAdjustable().setValue(e.getAdjustable().getMaximum());
                scrollPreviousMax = scrollMax;
            }
        }); 
        
        logFilesModel = new DefaultComboBoxModel();
        logFilesComboBox.setModel(logFilesModel);

        //refresh log files list
        updateLogComboBox();
        //start loop thread
        runReaderThread();
    }

    /** Used in autoscroll functionality. */
    private boolean isScrollAtBottom() {
        JScrollBar sb = logLinesScrollPane.getVerticalScrollBar();
        return (sb.getValue() == sb.getMaximum()-sb.getVisibleAmount());        
    }

    /** Used in autoscroll functionality. */
    private void setScrollToBottom() {
        JScrollBar sb = logLinesScrollPane.getVerticalScrollBar();
        sb.setValue(sb.getMaximum());
    }
    
    /** List log directory to get valid log files and put them in combobox (on change). */
    private void updateLogComboBox() {
        final File[] files = new File(logPath).listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
              return name.matches(logFileNamePattern);
            }
        });
        if (files.length != lastFilesNum)
        {
            Arrays.sort( files, new Comparator<File>() {
                public int compare( File b, File a ) {
                    long lm = a.lastModified() - b.lastModified();                    
                    return lm>0?1:(lm<0?-1:0); //long to int
                }
            });
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    logFilesModel.removeAllElements();
                    for (File file: files)
                        logFilesModel.addElement(file.getPath().toString());
                }
            });
            
            lastFilesNum = files.length;
        }
    }
    
    /**
     * Starts main thread; in loop:
     * - gets next lines from log file (using logReader)
     * - or switches to another log file.
     * If file is new/switched than jList is cleared.
     */
    private void runReaderThread() {
        readerThread = new Thread(new Runnable() {
            public void run() {
                String threadedLogFile = null;
                while (readerThread != null) {
                    boolean switchLog = false;
                    if (!currentLogFile.equals(threadedLogFile)) {//switch of log (LogReader serves buffer from the beginning)
                        currentLogReader = LogReader.getLogReader(currentLogFile);
                        threadedLogFile = currentLogFile;
                        switchLog = true;
                    }    
                    currentLogReader.updateReader();
                    boolean isNew = currentLogReader.isNew();
                    final boolean clearList = switchLog || isNew;
                    final List<LogLine> lines = currentLogReader.getLines();
                    
                    if (clearList || lines.size()>0)
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                autoscroll = isScrollAtBottom(); //autoscroll on new lines
                                if (clearList) {
                                    logLinesListModel.clear();
                                    logLines.clear();
                                }
                                for (LogLine line:lines) {
                                    if (lineFilter.get(line.getLevel()))
                                        logLinesListModel.addElement(line);
                                    logLines.add(line);
                                }
                            }
                        });
                    
                    updateLogComboBox();
                    
                    try {
                        Thread.sleep(updateTime);
                    } catch (InterruptedException ex) {
                        readerThread = null;
                    }
                }
            }
        });
        readerThread.start();        
    }

    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        logLinesScrollPane = new javax.swing.JScrollPane();
        logLinesList = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        filterPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        logFilesComboBox = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("VisNow Log");
        setMinimumSize(new java.awt.Dimension(700, 500));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        logLinesScrollPane.setViewportView(logLinesList);

        jTextArea1.setColumns(20);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, logLinesList, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.line}"), jTextArea1, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane2.setViewportView(jTextArea1);

        filterPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));

        jLabel1.setText("Show levels:");
        filterPanel.add(jLabel1);

        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));

        jLabel2.setText("Log file:");
        jPanel1.add(jLabel2);

        logFilesComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logFilesComboBoxActionPerformed(evt);
            }
        });
        jPanel1.add(logFilesComboBox);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(logLinesScrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 962, Short.MAX_VALUE)
            .addComponent(jScrollPane2)
            .addComponent(filterPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(logLinesScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 371, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(filterPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        //reset log file
        currentLogFile = "";
        //stop the thread
        readerThread = null;
        //remove static logWindow
        logWindow = null;
    }//GEN-LAST:event_formWindowClosing

    /**
     * Performed if user wants to switch log file.
     */
    private void logFilesComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logFilesComboBoxActionPerformed
        Object s = logFilesComboBox.getSelectedItem();
        if (s != null)
            currentLogFile = s.toString();
    }//GEN-LAST:event_logFilesComboBoxActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(LogWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LogWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LogWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LogWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                //new LogWindow().setVisible(true);
                openLogWindow();
            }
        });
    }
    
    /**
     * Only one log window - returns singleton instance.
     */
    public static void openLogWindow() {
        if (logWindow == null)
            logWindow = new LogWindow();
        logWindow.setVisible(true);        
        //if no thread (possibly was interrupted) then restart
        if (logWindow.readerThread == null) logWindow.runReaderThread();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel filterPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JComboBox logFilesComboBox;
    private javax.swing.JList logLinesList;
    private javax.swing.JScrollPane logLinesScrollPane;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}