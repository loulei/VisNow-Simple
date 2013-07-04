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

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import javax.swing.JOptionPane;
import pl.edu.icm.visnow.application.application.Application;
import pl.edu.icm.visnow.engine.commands.ModuleAddCommand;
import pl.edu.icm.visnow.engine.error.Displayer;
import pl.edu.icm.visnow.engine.library.LibraryCore;

/**
 *
 * @author gacek
 */
public class ModuleAdder extends InputStream implements Runnable, Transferable {

    private int sceneX;
    private int sceneY;
    private Vector<LibraryCore> selectedCores;
    private Application application;

    public ModuleAdder(Vector<LibraryCore> selectedCores, Application application) {
        this.selectedCores = selectedCores;
        this.application = application;
    }

    public void run() {
        print("run: tryGetAccess()");
        boolean acq = application.tryGetAccess("Add module from library (MLibP-1)");
        if (acq) {
            rerun();
        } else {
            java.awt.EventQueue.invokeLater(new Runnable() {

                public void run() {
                    int i = JOptionPane.showConfirmDialog(
                            null,
                            "Application is busy.\n"
                            + "Add the module anyway?\n"
                            + "(It's appearance may be delayed)",
                            "Network busy",
                            JOptionPane.YES_NO_OPTION);
                    if (i != JOptionPane.YES_OPTION) {
                        return;
                    } else {
                        new Thread(new Runnable() {

                            public void run() {
                                tryrun();
                            }
                        }).start();
                    }
                }
            });
        }
    }

    private void tryrun() {
        try {
            application.getAccess("Add module from library (MLibP-0)");
        } catch (InterruptedException ex) {
            Displayer.ddisplay(201001261216L, ex, this, "Interrupted");
        }
        rerun();
    }

    private void rerun() {
        int dx = 0;
        int dy = 0;
        application.releaseAccess();
        for (LibraryCore core : selectedCores) {
            application.getReceiver().receive(new ModuleAddCommand(
                    core.getName() + "[" + application.getEngine().nextModuleNumber() + "]",
                    core.getCoreName(),
                    new Point(sceneX - 15 + dx, sceneY - 18 + dy)));
            dx += 30;
            dy += 60;
        }

    }
    private boolean debug = false;

    private void print(String s) {
        if (!debug) {
            return;
        }
        System.out.println("MLibrariesPanelMA: \t" + s);
    }

    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{MLibrariesPanel.moduleAdderFlavor};
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return (flavor.equals(MLibrariesPanel.moduleAdderFlavor));
    }

    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        return this;
    }

    public void setLocation(Point location) {
        sceneX = location.x;
        sceneY = location.y;
    }

    @Override
    public int read() throws IOException {
        return -1;        
    }
}

