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

import java.awt.Frame;
import java.awt.Point;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.JButton;

/**
 *
 * @author gacek
 */
public class FSplitButton extends JButton implements DragSourceListener, DragGestureListener {



    private DragSource dragSource;
    private FBox box;
    //private FBox getBox() {return box;}


    public FSplitButton(String text, FBox box) {
        super(text);
        this.setBackground(FSplitUI.normalColor);
        this.dragSource = DragSource.getDefaultDragSource();
        this.box = box;

        this.dragSource.createDefaultDragGestureRecognizer(
            this,
            DnDConstants.ACTION_COPY_OR_MOVE,
            this
            );

        //this.getTransferHandler();
    }

    public void setActive(boolean b) {
        this.setBackground((b)?FSplitUI.highColor:FSplitUI.normalColor);
    }



    public void dragEnter(DragSourceDragEvent dsde) {}

    public void dragOver(DragSourceDragEvent dsde) {}

    public void dropActionChanged(DragSourceDragEvent dsde) {}

    public void dragExit(DragSourceEvent dse) {}

    public void dragDropEnd(DragSourceDropEvent dsde) {
        this.box.getPlace().getParentArea().getMajor().getSplitSystem().setInternalTargetsActive(true);
        Point p = dsde.getLocation();
        Frame[] frames = Frame.getFrames();
        //System.out.println("OPUSZCZANIE, na punkt ("+p.x+"x"+p.y+"), przy "+frames.length+" ramkach.");
        for(Frame f: frames) {
            if(f.contains(p.x-f.getX(), p.y-f.getY())) {
        //        System.out.println("PUNKT ZAWARTY!");
                return;
            }
        }
        //System.out.println("PUNKT WOLNY!");
        box.detachToFrame(p);
    }

    public void dragGestureRecognized(DragGestureEvent dge) {
        //System.out.println("DGR!!");
        this.box.getPlace().getParentArea().getMajor().getSplitSystem().setInternalTargetsActive(false);
        dge.startDrag(FSplitUI.dragCursor, box, this);
        this.cancelListeners();
    }

    //<editor-fold defaultstate="collapsed" desc=" Listeners ">
    private Vector<ActionListener> listeners = new Vector<ActionListener>();

    private void cancelListeners() {
        while(this.getActionListeners().length > 0) {
            listeners.add(this.getActionListeners()[0]);
            this.removeActionListener(this.getActionListeners()[0]);
        }
        this.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                restoreListeners();
            }
        });
    }

    public void restoreListeners() {
        while(this.getActionListeners().length > 0) {
            this.removeActionListener(this.getActionListeners()[0]);
        }
        for(ActionListener al: listeners) {
            this.addActionListener(al);
        }
        listeners.clear();
    }
    //</editor-fold>

}
