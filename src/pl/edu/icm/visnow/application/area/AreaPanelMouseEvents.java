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

package pl.edu.icm.visnow.application.area;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import pl.edu.icm.visnow.application.area.widgets.BgPanel;
import pl.edu.icm.visnow.application.area.widgets.DataPanel;
import pl.edu.icm.visnow.application.area.widgets.LinkPanel;
import pl.edu.icm.visnow.application.area.widgets.ModulePanel;
import pl.edu.icm.visnow.application.area.widgets.PortPanel;


/**
 *
 * @author Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class AreaPanelMouseEvents implements MouseListener, MouseMotionListener {

    //<editor-fold defaultstate="collapsed" desc=" AreaPanel ">
    private AreaPanel areaPanel;
    public AreaPanel getAreaPanel() {return areaPanel;}
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" [CONSTRUCTOR] ">
    public AreaPanelMouseEvents(AreaPanel areaPanel) {
        this.areaPanel = areaPanel;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" MouseClicked ">
    public void mouseClicked(MouseEvent e) {
        areaPanel.requestFocusInWindow();
        Component c = areaPanel.getAreaComponentAt(e.getPoint());
        if(c instanceof LinkPanel) {
            if(e.isMetaDown()) {
                ((LinkPanel) c).showMenu(e.getPoint());
            }
        }
        
        if(c instanceof ModulePanel) {
            modulePanelClicked(e, (ModulePanel)c);
            return;
        }

        if (c instanceof PortPanel)
        {
           //TODO? Powinno dzialac.
        }
        if(c == areaPanel.getBgPanel()) {

            if(e.isMetaDown())
                areaPanel.showMenu(e.getPoint());
        }
    }
    //</editor-fold>



    //<editor-fold defaultstate="collapsed" desc=" MousePressed ">
    public void mousePressed(MouseEvent e) {
        areaPanel.requestFocusInWindow();
        //areaPanel.getArea().getOutput().selectNull();
        Component c = areaPanel.getAreaComponentAt(e.getPoint());
        if(e.isMetaDown()) return;
        
        if(c instanceof ModulePanel) {
            modulePanelPressed(e, (ModulePanel)c);
            return;
        }
        if(c instanceof DataPanel) {
            dataPanelPressed(e, (DataPanel)c);
            return;
        }
        if(c instanceof LinkPanel) {
            linkPanelPressed(e, (LinkPanel)c);
            return;
        }
        if(c instanceof PortPanel) {
            portPanelPressed(e, (PortPanel)c);
            return;
        }
        if(c instanceof BgPanel) {
            bgPanelPressed(e, (BgPanel)c);
            return;
        }
    }

    public void dataPanelPressed(MouseEvent e, DataPanel panel) {
        areaPanel.getInternalManager().modulePanelPressed(panel, e.getX(), e.getY());
    }

    public void modulePanelPressed(MouseEvent e, ModulePanel panel) {
        //System.out.println("ModulePressed");
        //areaPanel.getArea().getOutput().select(panel, e.isControlDown());
        areaPanel.getInternalManager().modulePanelPressed(panel, e.getX(), e.getY());//.startDragging(e.getPoint());
        areaPanel.repaint();
    }

    public void modulePanelClicked(MouseEvent e, ModulePanel panel) {
        if(e.getButton() == MouseEvent.BUTTON3)
            areaPanel.getInternalManager().modulePanelRightClicked(panel, e.getX(), e.getY());//.startDragging(e.getPoint());
    }
    
    public void linkPanelPressed(MouseEvent e, LinkPanel panel) {
        areaPanel.getArea().getOutput().select(panel, e.isControlDown());
        areaPanel.repaint();
    }

    public void portPanelPressed(MouseEvent e, PortPanel panel) {
        areaPanel.getInternalManager().portPressed(panel, e.getPoint());
        areaPanel.repaint();
    }

    public void portPanelSelfPressed(MouseEvent e, PortPanel panel) {
        areaPanel.getInternalManager().portPressed(panel,
                new Point(
                e.getPoint().x+panel.getX()+panel.getModulePanel().getX(),
                e.getPoint().y+panel.getY()+panel.getModulePanel().getY()
                ));
        areaPanel.repaint();
    }

    public void bgPanelPressed(MouseEvent e, BgPanel panel) {        
        //areaPanel.getArea().getOutput().selectNull();        
        //areaPanel.repaint();
        
        //this.areaPanel.getArea().getOutput().select((ModulePanel)null, false);
    }
    //</editor-fold>

    public void mouseReleased(MouseEvent e) {
        areaPanel.requestFocusInWindow();
        areaPanel.getInternalManager().mouseReleased(e.isControlDown());
        areaPanel.repaint();
    }

    public void mouseEntered(MouseEvent e) {
        
    }

    public void mouseExited(MouseEvent e) {
        
    }

    public void mouseDragged(MouseEvent e) {
        areaPanel.requestFocusInWindow();
        areaPanel.getInternalManager().mouseDragged(e.getX(), e.getY());//.continueDragging(e.isControlDown());
        //areaPanel.getInternalManager().mouseConnectionMoved(e.getPoint());
        areaPanel.repaint();
    }

    public void mouseDragged(PortPanel p, MouseEvent e) {
        areaPanel.requestFocusInWindow();
        areaPanel.getInternalManager().mouseDragged(e.getX() + p.getTotalX(), e.getY()+p.getTotalY());//.continueDragging(e.isControlDown());
        //areaPanel.getInternalManager().mouseConnectionMoved(e.getPoint());
        areaPanel.repaint();
    }

    public void mousePortDragged(PortPanel p, MouseEvent e) {
        areaPanel.requestFocusInWindow();
        areaPanel.getInternalManager().mouseDragged(e.getX() + p.getTotalX(), e.getY()+p.getTotalY());//.continueDragging(e.isControlDown());
//        areaPanel.getInternalManager().mouseConnectionMoved(
//                new Point(
//                e.getPoint().x + p.getTotalX(),
//                e.getPoint().y + p.getTotalY()
//                ));
        areaPanel.repaint();
    }

    public void mouseMoved(MouseEvent e) {
        areaPanel.requestFocusInWindow();
    }



}
