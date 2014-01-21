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
exception statement from your version.
*/
//</editor-fold>

package pl.edu.icm.visnow.application.area;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import javax.swing.*;
import pl.edu.icm.visnow.application.area.widgets.BgPanel;
import pl.edu.icm.visnow.application.area.widgets.LinkConnectingPanel;
import pl.edu.icm.visnow.application.area.widgets.LinkPanel;
import pl.edu.icm.visnow.application.area.widgets.ModulePanel;
import pl.edu.icm.visnow.application.area.widgets.PortGlowPanel;
import pl.edu.icm.visnow.application.area.widgets.PortPanel;
import pl.edu.icm.visnow.application.frames.tabs.MLibrariesPanel;
import pl.edu.icm.visnow.application.frames.tabs.ModuleAdder;
import pl.edu.icm.visnow.engine.core.LinkName;
import pl.edu.icm.visnow.engine.main.Port;
import pl.edu.icm.visnow.system.main.VisNow;

/**
 *
 * @author Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class AreaPanel extends JPanel implements DropTargetListener {


    // <editor-fold defaultstate="collapsed" desc=" [VAR] Layers ">
    public static Integer backI        = new Integer(10);
    public static Integer glowI        = new Integer(11);
    public static Integer linkI        = new Integer(20);
    public static Integer linkDragI    = new Integer(30);
    public static Integer moduleI      = new Integer(50);
    public static Integer smokeI       = new Integer(55);
    public static Integer linkSmokeI   = new Integer(60);
    public static Integer moduleSmokeI = new Integer(65);
    public static Integer moduleDragI  = new Integer(70);
    public static Integer rectangleSelectionI = new Integer(90);
    public static Integer progressI    = new Integer(99);    
    //</editor-fold>
    


    private AreaPanelMouseEvents mouseEvents;
    public AreaPanelMouseEvents getMouseEvents() {return mouseEvents;}

    private AreaPanelInternalManager internalManager;
    public AreaPanelInternalManager getInternalManager() {return internalManager;}

    private Area area;
    public Area getArea() {return area;}


    private JLayeredPane layeredPane = new JLayeredPane();
    private JScrollPane scrollPane = new JScrollPane();
    private BgPanel bgPanel = new BgPanel();
       
    protected AreaPanel(Area area) {
        this.area = area;
        layeredPane.setPreferredSize(new java.awt.Dimension(800,600));
        layeredPane.setMinimumSize(new java.awt.Dimension(500,500));

        scrollPane.setBorder(null);
        scrollPane.setAutoscrolls(true);
        scrollPane.setViewportView(layeredPane);

        this.setLayout(new BorderLayout());
        this.add(scrollPane, BorderLayout.CENTER);


        //bgPanel.setBounds(0,0,layeredPane.getWidth(),layeredPane.getHeight());
        bgPanel.setBounds(0,0,2000,2000);
        layeredPane.add(bgPanel,backI);
        
        layeredPane.setBackground(new Color(20,20,23));

        mouseEvents = new AreaPanelMouseEvents(this);
        layeredPane.addMouseListener(mouseEvents);
        layeredPane.addMouseMotionListener(mouseEvents);

        internalManager = new AreaPanelInternalManager(this);

        this.setFocusable(true);
        this.onResize(null);
        this.setBorder(null);

        this.menu = new JPopupMenu();

        JMenu openDataFile = new JMenu("Open data file");
        
        ArrayList<Component> readers = VisNow.get().getMainWindow().getMainMenu().getReadersMenu(popupPoint);
        if(readers != null) {
            for (int i = 0; i < readers.size(); i++) {
                openDataFile.add(readers.get(i));
            }
            menu.add(openDataFile);
        }
        
        JMenu openTestData = new JMenu("Open test data");
        ArrayList<Component> tests = VisNow.get().getMainWindow().getMainMenu().getTestdataMenu(popupPoint);
        if(tests != null) {
            for (int i = 0; i < tests.size(); i++) {
                openTestData.add(tests.get(i));
            }
            menu.add(openTestData);
        }
        

        JMenu newModuleMenu = VisNow.get().getAttachWizard().getNewFullMenu("New module", popupPoint, menu, layeredPane);
        menu.add(newModuleMenu);
        
        this.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {onResize(e);}
            @Override
            public void componentMoved(ComponentEvent e) {}
            @Override
            public void componentShown(ComponentEvent e) {onResize(e);}
            @Override
            public void componentHidden(ComponentEvent e) {}
        });
        layeredPane.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {onLayeredPaneResize(e);}
            @Override
            public void componentMoved(ComponentEvent e) {}
            @Override
            public void componentShown(ComponentEvent e) {onLayeredPaneResize(e);}
            @Override
            public void componentHidden(ComponentEvent e) {}
        });

        DropTarget dt = new DropTarget(this,this);
        //this.getDropTarget().
    }

    void onResize(ComponentEvent evt) {
        int ww = (this.getWidth()>layeredPane.getWidth())?this.getWidth():layeredPane.getWidth();
        int hh = (this.getHeight()>layeredPane.getHeight())?this.getHeight():layeredPane.getHeight();
        layeredPane.setPreferredSize(new java.awt.Dimension(ww,hh));
        this.repaint();
    }

    void onLayeredPaneResize(ComponentEvent evt) {
        bgPanel.setBounds(0,0,layeredPane.getWidth(),layeredPane.getHeight());
        for(LinkPanel lp: linkPanels.values()) {
            lp.setBounds(0,0,layeredPane.getWidth(),layeredPane.getHeight());
        }
        layeredPane.repaint();
    }

    private boolean locked = false;

    protected void setLocked(boolean b) {
        locked = b;
        bgPanel.setLocked(b);

    }

    public boolean isLocked() {
        return locked;
    }

    protected ArrayList<PortPanel> getAllAreaInputPortPanels() {
        ArrayList<PortPanel> out = new ArrayList<PortPanel>();
        Component[] tmp = layeredPane.getComponents();
        for (int i = 0; i < tmp.length; i++) {
            if(tmp[i] instanceof ModulePanel) {
                PortPanel[] tmp2 = ((ModulePanel)tmp[i]).getAllInputPanels();
                out.addAll(Arrays.asList(tmp2));
            }            
        }
        return out;        
    }

    protected ArrayList<PortPanel> getAllAreaOutputPortPanels() {
        ArrayList<PortPanel> out = new ArrayList<PortPanel>();
        Component[] tmp = layeredPane.getComponents();
        for (int i = 0; i < tmp.length; i++) {
            if(tmp[i] instanceof ModulePanel) {
                PortPanel[] tmp2 = ((ModulePanel)tmp[i]).getAllOutputPanels();
                out.addAll(Arrays.asList(tmp2));
            }            
        }
        return out;        
    }
    
    protected Component getAreaComponentAt(Point point) {
        Component c = layeredPane.findComponentAt(point);
        while(true) {
            if(c == null) return null;
            if(c instanceof PortPanel) return c;
            if(c instanceof ModulePanel) return c;
            if(c instanceof LinkPanel) {
                for(LinkPanel lp: linkPanels.values()) {
                    if(lp.isHit(point)) return lp;
                }
                //return null;
            }
            if(c == bgPanel) return c;
            c = c.getParent();
            //if(c == layeredPane) return null;
            if(c == layeredPane) return bgPanel;
        }

//        if(c instanceof PortPanel) return c;
//        if(c instanceof ModulePanel) return c;
//        if(c instanceof LinkPanel) {
//            for(LinkPanel lp: linkPanels.values()) {
//                if(lp.isHit(point)) return lp;
//            }
//        }
//        if(c == bgPanel) return c;
//        
//        return bgPanel;        
    }

    BgPanel getBgPanel() {
        return bgPanel;
    }

    private JPopupMenu menu;
    private Point popupPoint = new Point(50, 50);
    
    void showMenu(Point point) {
        this.popupPoint.setLocation((int)point.getX(), (int)point.getY());
        menu.show(this, (int)point.getX(), (int)point.getY());
    }

    int getScrollOffsetX() {
        return scrollPane.getViewport().getViewPosition().x;
    }

    int getScrollOffsetY() {
        return scrollPane.getViewport().getViewPosition().y;
    }

    Component getViewPort() {
        return scrollPane.getViewport();
    }

    void addModulePanel(ModulePanel p, Point position) {
        modulePanels.put(p.getModule().getName(), p);
        layeredPane.add(p, moduleI);
        p.setLocation(position);
        layeredPane.moveToFront(p);
        layeredPane.repaint();
    }

    void addLinkPanel(LinkPanel lp) {
        linkPanels.put(lp.getLink().getName(), lp);
        layeredPane.add(lp, linkI);
        lp.setBounds(0, 0, layeredPane.getWidth(), layeredPane.getHeight());
        layeredPane.moveToFront(lp);
        layeredPane.repaint();
    }

    private HashMap<String, ModulePanel> modulePanels = new HashMap<String, ModulePanel>();
    private HashMap<LinkName, LinkPanel> linkPanels = new HashMap<LinkName, LinkPanel>();

    public ModulePanel getModulePanel(String name) {
        if(name == null)
            return null;
        
        return modulePanels.get(name);
    }

    void moveToDragLayer(ModulePanel dragPanel, boolean drag) {
        layeredPane.setLayer(dragPanel, (drag)?moduleDragI:moduleI);
    }

    void showConnectingPanel(LinkConnectingPanel connectingPanel, boolean show) {
        if(show) {
            layeredPane.add(connectingPanel, linkDragI);
            connectingPanel.setBounds(0, 0, layeredPane.getWidth(), layeredPane.getHeight());
        }
        else layeredPane.remove(connectingPanel);
        layeredPane.repaint();
    }


    private boolean pgpPresent = false;
    private PortGlowPanel pgp = new PortGlowPanel(this);
    
    void showPortGlowPanelLarge(PortPanel c, boolean active, int linkDataStatus) {
        if(pgpPresent) return;
        pgp.setActive(active);
        switch(linkDataStatus) {
            case Port.LINK_DATA_STATUS_OK:
                pgp.setErrorLevel(PortGlowPanel.ERRORLEVEL_OK);
                break;
            case Port.LINK_DATA_STATUS_CONDITIONAL:
                pgp.setErrorLevel(PortGlowPanel.ERRORLEVEL_WARNING);
                break;
            case Port.LINK_DATA_STATUS_ERROR:
                pgp.setErrorLevel(PortGlowPanel.ERRORLEVEL_ERROR);
                break;
        }        
        //pgp.setErrorLevel(dataPossible?PortGlowPanel.ERRORLEVEL_OK:PortGlowPanel.ERRORLEVEL_ERROR);
        layeredPane.add(pgp, glowI);
        pgp.setBounds(c.getTotalX() - 12, c.getTotalY()-15, 40,40);
        pgpPresent = true;
        pgp.repaint();
    }
    
    
    void removePortGlowPanelLarge() {
        if(pgpPresent) {
            pgpPresent = false;
            pgp.setActive(false);
            layeredPane.remove(pgp);
        }
    }


    private ArrayList<PortGlowPanel> ppgs = new ArrayList<PortGlowPanel>();
    
    void showPortGlowPanelSmall(PortPanel c, boolean active, int linkDataStatus) {
        PortGlowPanel tmp = new PortGlowPanel(this);
        switch(linkDataStatus) {
            case Port.LINK_DATA_STATUS_OK:
                tmp.setErrorLevel(PortGlowPanel.ERRORLEVEL_OK);
                break;
            case Port.LINK_DATA_STATUS_CONDITIONAL:
                tmp.setErrorLevel(PortGlowPanel.ERRORLEVEL_WARNING);
                break;
            case Port.LINK_DATA_STATUS_ERROR:
                tmp.setErrorLevel(PortGlowPanel.ERRORLEVEL_ERROR);
                break;
        }        
        ppgs.add(tmp);        
        layeredPane.add(tmp, glowI);
        tmp.setBounds(c.getTotalX() - 12, c.getTotalY()-15, 40,40);
        tmp.repaint();
    }
    
    void removeAllPortGlowPanelsSmall() {
        if(ppgs.isEmpty())
            return;
        
        for (int i = 0; i < ppgs.size(); i++) {
            layeredPane.remove(ppgs.get(i));
        }
        ppgs.clear();
    }
    
    void deleteModulePanel(String name) {
        layeredPane.remove(getModulePanel(name));
        modulePanels.remove(name);
        repaint();
    }

    void deleteLinkPanel(LinkName name) {
        layeredPane.remove(linkPanels.get(name));
        linkPanels.remove(name);
        repaint();
    }

    void updateModuleName(String name, String newName) {
        ModulePanel mp = modulePanels.get(name);
        modulePanels.remove(name);
        modulePanels.put(newName, mp);
        layeredPane.moveToFront(mp);
    }

    void updateLinkName(LinkName oldLinkName, LinkName newLinkName) {
        LinkPanel lp = linkPanels.get(oldLinkName);
        linkPanels.remove(oldLinkName);
        linkPanels.put(newLinkName, lp);
    }

    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
        
    }

    @Override
    public void dragOver(DropTargetDragEvent dtde) {
        
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {

    }

    @Override
    public void dragExit(DropTargetEvent dte) {

    }

    @Override
    public void drop(DropTargetDropEvent dtde) {
        try {
            ModuleAdder ma = (ModuleAdder) dtde.getTransferable().getTransferData(MLibrariesPanel.moduleAdderFlavor);
            ma.setLocation(dtde.getLocation());
            new Thread(ma).start();
        } catch (UnsupportedFlavorException ex) {            
        } catch (IOException ex) {
        }
    }

    private JWindow floatingWindow = null;
    
    public void setFloatingComponent(JComponent comp, Point positionOnScreen) {
        if(floatingWindow != null) {
            floatingWindow.dispose();
            floatingWindow = null;
        }
        
        floatingWindow = new JWindow();
        floatingWindow.add(comp);
        floatingWindow.setLocation(positionOnScreen);
        floatingWindow.pack();
        floatingWindow.setVisible(true);        
    }

    public void setFloatingComponentPosition(Point positionOnScreen) {
        if(floatingWindow != null) {
            floatingWindow.setLocation(positionOnScreen);
            floatingWindow.pack();
            floatingWindow.repaint();
        }
    }
    
    public void removeFloatingComponent() {
        if(floatingWindow != null) {
            floatingWindow.dispose();
            floatingWindow = null;
        }
    }


    
}
