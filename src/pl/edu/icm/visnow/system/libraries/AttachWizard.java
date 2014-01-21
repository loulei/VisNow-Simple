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

package pl.edu.icm.visnow.system.libraries;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import pl.edu.icm.visnow.application.application.Application;
import pl.edu.icm.visnow.application.area.widgets.PortPanel;
import pl.edu.icm.visnow.engine.commands.LinkAddCommand;
import pl.edu.icm.visnow.engine.commands.ModuleAddCommand;
import pl.edu.icm.visnow.engine.core.LinkName;
import pl.edu.icm.visnow.engine.core.Output;
import pl.edu.icm.visnow.engine.library.LibraryCore;
import pl.edu.icm.visnow.engine.library.LibraryFolder;
import pl.edu.icm.visnow.engine.library.LibraryRoot;
import pl.edu.icm.visnow.lib.types.VNDataAcceptor;
import pl.edu.icm.visnow.lib.types.VNDataSchemaComparator;
import pl.edu.icm.visnow.lib.types.VNDataSchemaInterface;
import pl.edu.icm.visnow.system.main.VisNow;

/**
 *
 * @author Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class AttachWizard {

    private HashMap<String, JMenu> menus = new HashMap<String, JMenu>();

    public AttachWizard() {
        Collection<String> col = VisNow.get().getMainTypes().getTypes();
        for (String s : col) {
            menus.put(s, getSimpleMenu(s));
        }
    }

    //<editor-fold defaultstate="collapsed" desc=" Init menus ">
    private JMenu getSimpleMenu(String classname) {
        JMenu ret = new JMenu("Attach");
        for (LibraryRoot root : VisNow.get().getMainLibraries().getLibraries()) {
            JMenu menu = getFolderMenu(classname, root.getRootFolder(), null);
            if (menu != null) {
                ret.add(menu);
            }
        }
        return ret;
    }

    public JMenu getFullMenu(String name, Point p) {
        JMenu ret = new JMenu(name);
        for (LibraryRoot root : VisNow.get().getMainLibraries().getLibraries()) {
            JMenu menu = getFolderMenu(null, root.getRootFolder(), p);
            if (menu != null) {
                ret.add(menu);
            }
        }
        return ret;
    }

    public JMenu getNewFullMenu(String name, Point p, JPopupMenu popup, JComponent parentComponent) {
        JMenu ret = new JMenu("New module");
        
        NewModuleTreePanel atp = new NewModuleTreePanel(
                VisNow.get().getMainLibraries().getLibrariesTreeModel(), 
                p, popup, parentComponent);
        ret.add(atp);
        return ret;
    }
    
    private JMenu getFolderMenu(String classname, LibraryFolder folder, final Point p) {
        JMenu ret = new JMenu(folder.getName());
        boolean anything = false;
        for (LibraryFolder fld : folder.getSubFolders()) {
            JMenu menu = getFolderMenu(classname, fld, p);
            if (menu != null) {
                ret.add(menu);
                anything = true;
            }
        }
        for (LibraryCore core : folder.getCores()) {
            if(classname != null) {
                JMenu menu = getCoreMenu(classname, core);
                if (menu != null) {
                    ret.add(menu);
                    anything = true;
                }
            } else {
                JMenuItem menuItem = new JMenuItem(core.getName());
                final LibraryCore cr = core;
                menuItem.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Application a = VisNow.get().getMainWindow().getApplicationsPanel().getCurrentApplication();
                        if(p == null)
                            a.addModuleByName(cr.getName(), cr.getClassPath(), false);
                        else
                            a.addModuleByName(cr.getName(), cr.getClassPath(), p, false);                        
                    }
                });
                ret.add(menuItem);
                anything = true;
            }

        }
        return (anything) ? ret : null;
    }

    private JMenu getCoreMenu(String classname, LibraryCore core) {
        if (core == null) {
            return null;
        }

        //System.out.println("\n\n");
        //System.out.println("getcoremenu: "+core.getName()+" (for: "+classname+")");
        JMenu ret = new JMenu(core.getName());
        boolean anything = false;
        HashMap<String, String> str = core.getInputTypes();
        if (str == null) {
            return null;
        }
        for (Entry<String, String> e : str.entrySet()) {

            try {
                if (Class.forName(e.getValue()).isAssignableFrom(Class.forName(classname))) {
                    anything = true;
                    JMenuItem item = new JMenuItem(e.getKey());
                    item.addActionListener(new PortChooser(core, e.getKey()));
                    ret.add(item);
                }
            } catch (ClassNotFoundException ex) {
                System.out.println("Class not found: [" + classname + "] or [" + e.getValue() + "] in core [" + core.getName() + "]");
                //Displayer.ddisplay(201002091100L, e, this, "ERROR IN LIBRARY? Class not found.");
            }
        }
        return (anything) ? ret : null;
    }
    //</editor-fold>
    private Output out;
    private int pointX;
    private int pointY;

    public JMenu getMenu(Output o, int x, int y) {
        out = o;
        pointX = x;
        pointY = y;
        return menus.get(o.getType().getName());
    }

    public JMenu getNewMenu(Output o, int x, int y, PortPanel panel) {
        out = o;
        pointX = x;
        pointY = y;

        JMenu ret = new JMenu("Attach");
        
        AttachTreePanel atp = new AttachTreePanel(VisNow.get().getMainLibraries().getLibrariesTreeModel(), panel);
        ret.add(atp);
        return ret;
    }

    public JMenu getFilteredMenu(Output o, int x, int y) {
        out = o;
        pointX = x;
        pointY = y;

        Object dataObj = o.getData().getValue();
        VNDataSchemaInterface[] schemas = null;
        if (dataObj != null && dataObj instanceof VNDataSchemaInterface) {
            schemas = new VNDataSchemaInterface[1];
            schemas[0] = (VNDataSchemaInterface) dataObj;
        } else {
            schemas = o.getVNDataSchemas();
        }

        if (schemas == null) {
            return menus.get(o.getType().getName());
        } else {
            return filterMenu(menus.get(o.getType().getName()), schemas);
        }
    }

    public JMenu getNewFilteredMenu(Output o, int x, int y, PortPanel panel) {
        out = o;
        pointX = x;
        pointY = y;

        Object dataObj = o.getData().getValue();
        VNDataSchemaInterface[] schemas = null;
        if (dataObj != null && dataObj instanceof VNDataSchemaInterface) {
            schemas = new VNDataSchemaInterface[1];
            schemas[0] = (VNDataSchemaInterface) dataObj;
        } else {
            schemas = o.getVNDataSchemas();
        }

        JMenu ret = new JMenu("Attach");
        ret.add(new AttachTreePanel(VisNow.get().getMainLibraries().getSchemaFilteredLibrariesTreeModel(o.getType().getName(), schemas), panel));
        return ret;
    }

    protected void portChosen(final LibraryCore core, final String port) {
        Thread t = new Thread("attachThread") {
            @Override
            public void run(){
                Application a = out.getModuleBox().getEngine().getApplication();
                String name = core.getName() + "[" + a.getEngine().nextModuleNumber() + "]";
                a.getReceiver().receive(new ModuleAddCommand(name, core.getCoreName(),
                        new Point(pointX - 10, pointY + 30), false));
                a.getReceiver().receive(new LinkAddCommand(new LinkName(out.getModuleBox().getName(),
                        out.getName(), name, port), true));
            }
        };
        t.start();
    }

    private JMenu filterMenu(JMenu inMenu, VNDataSchemaInterface[] schemas) {
        JMenu outMenu = new JMenu(inMenu.getText());
        for (int i = 0; i < inMenu.getItemCount(); i++) {
            JMenuItem mi = inMenu.getItem(i);
            if (mi instanceof JMenu) {
                JMenu child = filterMenu((JMenu) mi, schemas);
                if (child.getItemCount() > 0) {
                    outMenu.add(child);
                }
            } else {
                JMenuItem child = filterItem(mi, schemas);
                if (child != null) {
                    outMenu.add(child);
                }
            }
        }
        return outMenu;
    }

    private JMenuItem filterItem(JMenuItem mi, VNDataSchemaInterface[] schemas) {

        ActionListener[] als = mi.getActionListeners();
        PortChooser pc = null;
        for (int i = 0; i < als.length; i++) {
            if (als[i] instanceof PortChooser) {
                pc = (PortChooser) als[i];
                break;
            }
        }

        if (pc == null)
            return mi;

        LibraryCore core = pc.getLibraryCore();
        String port = pc.getPort();

        HashMap<String, VNDataAcceptor[]> vndasList = core.getInputVNDataAcceptors();
        VNDataAcceptor[] vndas = vndasList.get(port);
        if (vndas == null || vndas.length == 0) {
            return null;
        }
       for (int i = 0; i < vndas.length; i++)
          for (int j = 0; j < schemas.length; j++)
             if (VNDataSchemaComparator.isCompatible(schemas[j], vndas[i].getVNDataSchemaInterface(), vndas[i].getVNDataCompatibilityMask()))
             {
                JMenuItem outMi = new JMenuItem(mi.getText());
                outMi.addActionListener(pc);
                return outMi;
             }

        return null;
    }
}

class PortChooser implements ActionListener {

    private LibraryCore core;
    private String port;

    PortChooser(LibraryCore core, String port) {
        this.core = core;
        this.port = port;
    }

    public LibraryCore getLibraryCore() {
        return core;
    }

    public String getPort() {
        return port;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        VisNow.get().getAttachWizard().portChosen(core, port);
    }
}