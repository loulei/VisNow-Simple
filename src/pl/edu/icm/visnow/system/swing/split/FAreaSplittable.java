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

package pl.edu.icm.visnow.system.swing.split;

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
import java.util.HashMap;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import org.w3c.dom.Node;

/**
 *
 * @author gacek
 */
public abstract class FAreaSplittable extends FArea implements DropTargetListener {


    //<editor-fold defaultstate="collapsed" desc=" Directions ">
    

    private final static String[] direction = new String[] {
        BorderLayout.CENTER,
        BorderLayout.NORTH,
        BorderLayout.SOUTH,
        BorderLayout.WEST,
        BorderLayout.EAST
    };
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Layout ">
    private final static Integer bottomI = new Integer(1);
    private final static Integer topI = new Integer(10);

    private JLayeredPane layers = new JLayeredPane();
    private JPanel bottomLayer = new JPanel();
    private JPanel topLayer = new JPanel();
    protected JPanel getBottomLayer() {return bottomLayer;}

    private JPanel topPanel = new JPanel();
    private JPanel bottomPanel = new JPanel();
    private JPanel leftPanel = new JPanel();
    private JPanel rightPanel = new JPanel();
    private JPanel centerPanel = new JPanel();
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Active Panels ">
    private JPanel[] aPanels = new JPanel[]
        {centerPanel, topPanel, bottomPanel, leftPanel, rightPanel};

    private int activePanel = FAreaMajor.nullD;
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Structure ">
    protected FAreaMajor getMajor() {
        return parent.getMajor();
    }

    private FArea parent;
    public FArea getParentArea() {return parent;}
    public void setParentArea(FArea parent) {this.parent = parent;}
    //</editor-fold>


    public FAreaSplittable(FArea parent) {
        this.parent = parent;

        this.setLayout(new BorderLayout());
        this.add(layers, BorderLayout.CENTER);

        bottomLayer.setLayout(new BorderLayout());
        bottomLayer.setBounds(0,0,500,500);
        bottomLayer.setBackground(Color.yellow);
        layers.add(bottomLayer, bottomI);

        topLayer.setLayout(new BorderLayout());
        topLayer.setBounds(0,0,500,500);
        topLayer.setOpaque(false);
        layers.add(topLayer, topI);

        for(int i=FAreaMajor.centerD; i<FAreaMajor.lastD; ++i) {
            aPanels[i].setOpaque(false);
            topLayer.add(aPanels[i],direction[i]);
            if(i!=FAreaMajor.centerD) aPanels[i].setPreferredSize(FSplitUI.DD);
        }

        layers.addComponentListener(new ComponentListener(){
            public void componentResized(ComponentEvent e) {resize();}
            public void componentMoved(ComponentEvent e) {resize();}
            public void componentShown(ComponentEvent e) {resize();}
            public void componentHidden(ComponentEvent e) {}
        });

        DropTarget dt = new DropTarget(this,this);
    }



    //<editor-fold defaultstate="collapsed" desc=" Border Activation ">
    private void activatePanel(int dir) {
        if(activePanel != FAreaMajor.nullD && activePanel != dir) {
            aPanels[activePanel].setBorder(null);
        }
        activePanel = dir;
        aPanels[activePanel].setBorder(FSplitUI.border);
        repaint();
    }

    private void deactivatePanels() {
        activePanel = FAreaMajor.nullD;
        for(JPanel jp: aPanels)
            jp.setBorder(null);
        repaint();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Activate Border ">
    private void activateBorder(int x, int y) {
        if(y < FSplitUI.D) {
            activatePanel(topD);
        } else if (y > getHeight() - FSplitUI.D) {
            activatePanel(bottomD);
        } else if (x < FSplitUI.D) {
            activatePanel(leftD);
        } else if (x > getWidth() - FSplitUI.D) {
            activatePanel(rightD);
        } else {
            if(isSingle()) {
                activatePanel(centerD);
            } else {
                deactivatePanels();
            }
        }
    }
    //</editor-fold>




    public void dragEnter(DropTargetDragEvent dtde) {
        if(!dtde.isDataFlavorSupported(FSplitUI.BoxFlavor)) return;
    }
    public void dropActionChanged(DropTargetDragEvent dtde) {}

    public void dragOver(DropTargetDragEvent dtde) {
        if(!dtde.isDataFlavorSupported(FSplitUI.BoxFlavor)) return;
            Point p = dtde.getLocation();
            activateBorder(p.x,p.y);
    }

    public void dragExit(DropTargetEvent dte) {
        deactivatePanels();
    }

    public void drop(DropTargetDropEvent dtde) {
        if(!dtde.isDataFlavorSupported(FSplitUI.BoxFlavor)) return;
        int dir = activePanel;
        deactivatePanels();
        try {
            FBox box = (FBox)(dtde.getTransferable().getTransferData(FSplitUI.BoxFlavor));
            FAreaMajor boxMajor = box.getPlace().getParentArea().getMajor();
            //FPlace boxPlace = box.getPlace();
            box.detach();
            addBox(box,dir);
            //boxPlace.validate();
            boxMajor.checkRemoval();
            
        } catch (UnsupportedFlavorException ex) {
        } catch (IOException ex) {
        }
        validate();
        notifySplitListeners();
    }

    //protected abstract void doDrop(FBox box, int dir);



    protected void resize() {
        topLayer.setBounds(0,0,this.getWidth(), this.getHeight());
        bottomLayer.setBounds(0,0,this.getWidth(), this.getHeight());
        //topLayer.invalidate();
        //bottomLayer.invalidate();
        topLayer.validate();
        bottomLayer.validate();
        //TODO WHAT?
    }


    protected abstract String writeXML(int d);

    //protected abstract void initXML(Node node, HashMap<String, Component> map);
}
