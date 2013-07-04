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
import java.awt.Cursor;
import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import pl.edu.icm.visnow.application.application.Application;
import pl.edu.icm.visnow.application.io.VNReader;
import pl.edu.icm.visnow.application.io.XMLReader;
import pl.edu.icm.visnow.engine.error.Displayer;
import pl.edu.icm.visnow.engine.exception.VNOuterException;
import pl.edu.icm.visnow.engine.library.LibraryCore;
import pl.edu.icm.visnow.engine.library.LibraryFolder;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;
import pl.edu.icm.visnow.system.main.VisNow;
import pl.edu.icm.visnow.system.swing.filechooser.VNFileChooser;

/**
 *
 * @author Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class MainMenu {

    //<editor-fold defaultstate="collapsed" desc=" [VAR] window, application ">
    protected MainWindow window;

    protected Application  getApplication() {
        return window.getApplicationsPanel().getApplication();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" [VAR] Untitled count ">
    private int untitledCount;

    public int getUntitledCount() {
        return untitledCount;
    }

    public void setUntitledCount(int untitledCount) {
        this.untitledCount = untitledCount;
    }

    public int nextUntitled() {
        this.untitledCount = untitledCount + 1;
        return untitledCount - 1;
    }
    //</editor-fold>

	protected MainMenu(MainWindow window) {
        this.window = window;
        this.untitledCount = 1;
    }

        //<editor-fold defaultstate="collapsed" desc=" FileFilters ">
    private final static FileNameExtensionFilter vnaFilter = new FileNameExtensionFilter("VisNow Application","vna", "VNA");

    private final static FileFilter vnfFilter = new FileFilter() {
        @Override
        public boolean accept(File f) {
            if(f.isDirectory()) return true;
            if(!f.isFile()) return false;
            return (f.getName().toLowerCase().endsWith(".vnf"));
        }
        @Override
        public String getDescription() {
            return "VisNow application data file ( .vnf)";
        }
    };

    private final static FileFilter vnsFilter = new FileFilter() {
        @Override
        public boolean accept(File f) {
            if(f.isDirectory()) return true;
            if(!f.isFile()) return false;
            return (f.getName().toLowerCase().endsWith(".vns"));
        }
        @Override
        public String getDescription() {
            return "VisNow application snapshot ( .vns)";
        }
    };

    private final static FileFilter vnFilter = new FileFilter() {
        @Override
        public boolean accept(File f) {
            if(f.isDirectory()) return true;
            if(!f.isFile()) return false;
            if (f.getName().toLowerCase().endsWith(".vna")) return true;
            if (f.getName().toLowerCase().endsWith(".vnf")) return true;
            if (f.getName().toLowerCase().endsWith(".vns")) return true;
            return false;
        }
        @Override
        public String getDescription() {
            return "All VisNow Applications ( .vna, .vnf, .vns)";
        }
    };
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" File ">
    protected void newApplication() {
        window.getApplicationsPanel().addApplication(new Application("Untitled("+nextUntitled()+")"));
    }




    protected void openApplication() {
//        try {
            VNFileChooser chooser = new VNFileChooser(); /* TODO: foxtrot file chooser */
            //chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.addFileFilter(vnFilter);
            chooser.addFileFilter(vnaFilter);
            chooser.addFileFilter(vnfFilter);
            chooser.addFileFilter(vnsFilter);
            chooser.setFileFilter(vnFilter);
//            chooser.setCurrentDirectory(new File(VisNow.get().getMainConfig().getVN2UserFolder()));
            boolean chosen = chooser.showDialog();//window);
            if(chosen) {
                File file = chooser.getSelectedFile();
                Application application;
                try {
                    application = XMLReader.readXML(chooser.getSelectedFile());
                // TODO: Zamienic na wersje tablicowa - dodac obsluge tablicy wywolujaca odczytywanie pojedynczych plikow.
                /* TODO: load application */
                // add Application
                } catch (VNOuterException ex) {
                    Displayer.display(200907100601L, ex, this, "Could not read application from XML.");
                    return;
                }

                window.getApplicationsPanel().addApplication(application);
            }

    }


    public void openApplication(File file) {
        try {
            Application application = XMLReader.readXML(file);
            window.getApplicationsPanel().addApplication(application);
        } catch (VNOuterException ex) {
            Displayer.display(200907311400L, ex, this, "Could not read application from XML.");
        }
    }

    protected boolean saveApplication() {
//        if(window.getApplicationsPanel().getApplication().getFilePath() == null)
//            return saveAsApplication();
//        VisNow.get().getMainConfig().addRecentApplication(window.getApplicationsPanel().getApplication().getFilePath());
//        return window.getApplicationsPanel().getApplication().save();

        return betaSave();
    }

    protected boolean saveAsApplication() {
//        JFileChooser chooser = new JFileChooser(); /* TODO: foxtrot file chooser */
//        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
//        chooser.addChoosableFileFilter(vnFilter);
//        chooser.addChoosableFileFilter(vnaFilter);
//        chooser.addChoosableFileFilter(vnfFilter);
//        chooser.addChoosableFileFilter(vnsFilter);
//        chooser.setFileFilter(vnFilter);
//        chooser.setCurrentDirectory(new File(VisNow.get().getMainConfig().getVN2UserFolder()));
//        int fileInt = chooser.showSaveDialog(window);
//
        VNFileChooser chooser = new VNFileChooser(); /* TODO: foxtrot file chooser */
        //chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.addFileFilter(vnFilter);
        chooser.addFileFilter(vnaFilter);
        chooser.addFileFilter(vnfFilter);
        chooser.addFileFilter(vnsFilter);
        chooser.setFileFilter(vnFilter);
        //chooser.setCurrentDirectory(new File(VisNow.get().getMainConfig().getVN2UserFolder()));
        boolean chosen = chooser.showDialog();

        
        if(chosen) {
            return window.getApplicationsPanel().getApplication().saveAs(chooser.getSelectedFile());
        }
        return false;
    }

    protected void readTemplate() {

    }

    protected void closeApplication() {
        /* TODO: prompt o save */
        window.getApplicationsPanel().removeCurrentApplication();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Edit ">
    protected void undo() {

    }

    protected void redo() {

    }

    protected void showHistory() {

    }

    protected void delete() {
        getApplication().deleteSelected();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Action ">
    protected void interrupt() {

    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc=" Help ">
    protected void about() {
        JOptionPane.showMessageDialog(
            null,
            VisNow.TITLE+" v"+VisNow.VERSION+"\n\nCopyright 2012 ICM University of Warsaw",
            VisNow.TITLE+" v"+VisNow.VERSION,
            JOptionPane.INFORMATION_MESSAGE,
            new ImageIcon(getClass()
                .getResource("/pl/edu/icm/visnow/gui/icons/big/visnow.png"))
            );
    }

    protected void help() {
        VisNow.get().showHelp("top");
    }

    //</editor-fold>

    void clearState() {
        window.getApplicationsPanel().getApplication().doTheMainReset();
    }

    JFileChooser chooser = new JFileChooser( VisNow.get().getMainConfig().getUsableApplicationsPath() );

    boolean betaSave() {
//        VNFileChooser chooser = new VNFileChooser(); /* TODO: foxtrot file chooser */
//        //chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
//        chooser.addFileFilter(vnFilter);
//        chooser.addFileFilter(vnaFilter);
//        chooser.addFileFilter(vnfFilter);
//        chooser.addFileFilter(vnsFilter);
//        chooser.setFileFilter(vnFilter);
//        //chooser.setCurrentDirectory(new File(VisNow.get().getMainConfig().getVN2UserFolder()));
//        boolean chosen = chooser.showDialog();
//
//        if(chosen) {
//            return window.getApplicationsPanel().getApplication().betaSaveAs(chooser.getSelectedFile());
//        }
//        return false;

        chooser.addChoosableFileFilter(vnaFilter);
//        chooser.addChoosableFileFilter(vnfFilter);
//        chooser.addChoosableFileFilter(vnsFilter);
//        chooser.addChoosableFileFilter(vnFilter);
        int result = chooser.showSaveDialog(window);

        if(result == JFileChooser.APPROVE_OPTION) {
            File file = VNFileChooser.fileWithExtensionAddedIfNecessary( chooser.getSelectedFile(), 
					vnaFilter );
            VisNow.get().getMainConfig().setLastApplicationsPath(file.getAbsolutePath());
            return window.getApplicationsPanel().getApplication().betaSaveAs(file);
        }
        return false;
        
    }

    void betaOpen() {
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.addChoosableFileFilter(vnaFilter);
        int result = chooser.showOpenDialog(window);
        if(result == JFileChooser.APPROVE_OPTION) {
            final File file = chooser.getSelectedFile();
            betaOpenFile(file);            
        }
    }
    
    public void betaOpenFile(final File file) {
            new Thread(new Runnable() {
            @Override
                public void run() {
                    final Application application = VNReader.readApplication(file);                    
                    if(application != null) {
                        VisNow.get().getMainConfig().setLastApplicationsPath(file.getAbsolutePath());
                        java.awt.EventQueue.invokeLater(new Runnable() {
                    @Override
                            public void run() {
                                window.getApplicationsPanel().addApplication(application);
                            }
                        });
                    } else {
                        JOptionPane.showMessageDialog(window, "ERROR: cannot load VNA application\n"+file.getAbsolutePath(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            },
            "VN-READ-APPLICATION-B")
            .start();
    }
    
    
    public ArrayList<Component> getReadersMenu(Point p) {
        ArrayList<Component> readersList = new ArrayList<Component>();
        LibraryFolder rootFolder = VisNow.get().getMainLibraries().getInternalLibrary().getRootFolder();
        processLibraryFolderForReaders(rootFolder, readersList, p);
        
        if(readersList.isEmpty())
            return null;
        
        if(readersList.get(readersList.size()-1) instanceof JSeparator) {
            readersList.remove(readersList.size()-1);
        }
        return readersList;
    }

    private boolean processLibraryFolderForReaders(LibraryFolder folder, ArrayList<Component> readersList, final Point p) {
        Vector<LibraryFolder> folders = folder.getSubFolders();
        Vector<LibraryCore> cores = folder.getCores();
        for (int i = 0; i < folders.size(); i++) {
            boolean added = processLibraryFolderForReaders(folders.get(i), readersList, p);
            if(added)
                readersList.add(new JSeparator());
        }
        
        boolean added = false;
        for (int i = 0; i < cores.size(); i++) {
            final LibraryCore core = cores.get(i);
            if(core == null)
                return false;
            
            if(core.isReader()) {
                JMenuItem item = new JMenuItem(core.getReaderDataType()+"...");
                item.addActionListener(new java.awt.event.ActionListener() {
                    
                    @Override
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        VisNow.get().getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                        VisNow.get().getMainWindow().repaint();
                        SwingInstancer.swingRun(new Runnable() {
                            @Override
                            public void run() {
                                if(p == null)
                                    window.getApplicationsPanel().getApplication().addModuleByName(core.getName()+"[" + getApplication().getEngine().nextModuleNumber() + "]", core.getClassPath(), true);                        
                                else
                                    window.getApplicationsPanel().getApplication().addModuleByName(core.getName()+"[" + getApplication().getEngine().nextModuleNumber() + "]", core.getClassPath(), p, true);
                                VisNow.get().getMainWindow().setCursor(Cursor.getDefaultCursor());
                                VisNow.get().getMainWindow().repaint();
                            }
                        });                        
                    }
                });
                readersList.add(item);
                added = true;
            }
        }
        return added;
    }

    public ArrayList<Component> getTestdataMenu(Point p) {
        ArrayList<Component> out = new ArrayList<Component>();
        LibraryFolder rootFolder = VisNow.get().getMainLibraries().getInternalLibrary().getRootFolder();
        processLibraryFolderForTestData(rootFolder, out, p);
        
        if(out.isEmpty())
            return null;
        
        if(out.get(out.size()-1) instanceof JSeparator) {
            out.remove(out.size()-1);
        }
        return out;
        
    }

    private boolean processLibraryFolderForTestData(LibraryFolder folder, ArrayList<Component> list, final Point p) {
        Vector<LibraryFolder> folders = folder.getSubFolders();
        Vector<LibraryCore> cores = folder.getCores();
        for (int i = 0; i < folders.size(); i++) {
            boolean added = processLibraryFolderForTestData(folders.get(i), list, p);
            if(added)
                list.add(new JSeparator());
        }
        
        boolean added = false;
        for (int i = 0; i < cores.size(); i++) {
            final LibraryCore core = cores.get(i);
            if(core == null) {
                return false;
            }
            if(core.isTestData()) {
                JMenuItem item = new JMenuItem(core.getName());
                item.addActionListener(new java.awt.event.ActionListener() {
                    @Override
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        VisNow.get().getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                        VisNow.get().getMainWindow().repaint();
                        SwingInstancer.swingRun(new Runnable() {
                            @Override
                            public void run() {
                                if(p == null)
                                    window.getApplicationsPanel().getApplication().addModuleByName(core.getName()+"[" + getApplication().getEngine().nextModuleNumber() + "]", core.getClassPath(), false);
                                else
                                    window.getApplicationsPanel().getApplication().addModuleByName(core.getName()+"[" + getApplication().getEngine().nextModuleNumber() + "]", core.getClassPath(), p, false);                        
                                VisNow.get().getMainWindow().setCursor(Cursor.getDefaultCursor());
                                VisNow.get().getMainWindow().repaint();
                            }
                        });
                        
                    }
                });
                list.add(item);
                added = true;
            }
        }
        return added;
    }
    
}
