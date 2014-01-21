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
import java.awt.dnd.DropTarget;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.JFrame;
import org.w3c.dom.Node;

/**
 *
 * @author gacek
 */
public class FAreaMajor extends FArea {


    
    //
    protected boolean isMajor() {return true;}
    protected boolean isSplit() {return false;}
    protected boolean isSingle() {return false;}

    private boolean persistent;

    public FAreaMajor(boolean persistent) {
        this(persistent, new FSplitSystem());
    }

    //
    public FAreaMajor(boolean persistent, FSplitSystem system) {
        this.persistent = persistent;
        this.system = system;
        system.addFAreaMajor(this);
        this.add(child, BorderLayout.CENTER);
        this.setBackground(Color.BLACK);
    }

    //
    protected FAreaMajor getMajor() {return this;}
    private FAreaSplittable child = new FAreaSingle(this);
    public FAreaSplittable getChild() {return child;}
    protected void replaceChild(FAreaSplittable oldChild, FAreaSplittable newChild) {
        child = newChild;
        this.removeAll();
        this.add(child, BorderLayout.CENTER);
    }

    public FPlace getSomePlace() {return child.getSomePlace();}

    public void addBox(FBox box, int direction) {
        child.addBox(box,direction);
        notifySplitListeners();
    }

    //
    private FAreaSingle removePlace = null;
    void markPlaceForRemoval(FAreaSingle remove) {removePlace = remove;}

    void checkRemoval() {
        //System.out.println("Check removal, "+(removePlace!=null));
        if(removePlace==null) return;
        if(child.equals(removePlace)) {
            removePlace.validate();
            removePlace.repaint();
            removePlace = null;
            if(!persistent) die();
            return;
        }
        removePlace.performRemoval();
        removePlace = null;
    }

    private void die() {
        if(frame!=null) {
            frame.setVisible(false);
            frame.dispose();
        }
    }

    
    private JFrame frame = null;
    void setFrame(JFrame frame) {
        this.frame = frame;
    }

    @Override
    public void addBox(FBox box) {
        addBox(box, centerD);
        notifySplitListeners();
    }

    public void addBox(String name, Component component) {
        addBox(new FBox(name, component));
        notifySplitListeners();
    }


    private FSplitSystem system;
    public FSplitSystem getSplitSystem() {return system;}


    public void addInternalDropTarget(DropTarget target) {
        system.addInternalDropTarget(target);
    }

    public void removeInternalDropTarget(DropTarget target) {
        system.removeInternalDropTarget(target);
    }

    public void setInternalTargetsActive(boolean active) {
        system.setInternalTargetsActive(active);
    }
   // private void sResize(ComponentEvent e) {
    //    System.out.println(e.paramString()+" ["+this.getWidth()+"x"+this.getHeight()+"]");
   //     java.awt.EventQueue.invokeLater(new Runnable() {public void run() {
   //         resize();
   //     }});
   // }

    //
    //protected void resize() {
    //    child.resize();
        //TODO WHAT?
    //}


    protected String writeXML() {
        String ret = "  <major>\n";
        ret += this.getChild().writeXML(2);
        ret += "  </major>\n";
        return ret;
    }

    public String getXML() {
        return system.writeXML();
    }

    public void useXML(Node xml, HashMap<String, Component> map) {
//        System.out.println("******\nUSE XML\n");
//        System.out.println(xml);
        if(!xml.getNodeName().equalsIgnoreCase("system")) return;

        Vector<Node> majors = new Vector<Node>();
        for(int i=0; i<xml.getChildNodes().getLength(); ++i) {
            if(xml.getChildNodes().item(i).getNodeName().equalsIgnoreCase("major"))
                majors.add(xml.getChildNodes().item(i));
        }

        initXML(majors.firstElement(), map);
        for(int i=1; i<majors.size(); ++i) {
            system.fromXML(majors.elementAt(i), map);
        }

//        System.out.println("******\n\n");
    }

    private void initXML(Node xml, HashMap<String, Component> map) {
//        System.out.println("*\nMajor.initXML");
//        System.out.println(xml);
        Node node = null;
        for(int i=0; i<xml.getChildNodes().getLength(); ++i) {
            Node tmp = xml.getChildNodes().item(i);
            if(tmp.getNodeType() == tmp.ELEMENT_NODE) {
                node = tmp; break;
            }
        }
        if(node == null) return;

        if(node.getNodeName().equalsIgnoreCase("split")) {
            this.replaceChild(child, new FAreaSplit(this, node, map));
        } else if(node.getNodeName().equalsIgnoreCase("single")) {
            this.replaceChild(child, new FAreaSingle(this, node, map));
        }
        //this.validate();
    }


    public void addSplitListener(FSplitListener listener) {system.addSplitListener(listener);}
    public void removeSplitListener(FSplitListener listener) {system.removeSplitListener(listener);}

    protected void notifySplitListeners() {system.notifySplitListeners();}
}
