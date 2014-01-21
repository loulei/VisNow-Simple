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

import java.awt.Component;
import java.awt.dnd.DropTarget;
import java.util.HashMap;
import java.util.Vector;
import org.w3c.dom.Node;

/**
 *
 * @author gacek
 */
public class FSplitSystem {


    private Vector<FSplitListener> listeners = new Vector<FSplitListener>();
    protected void addSplitListener(FSplitListener listener) {listeners.add(listener);}
    protected void removeSplitListener(FSplitListener listener) {listeners.remove(listener);}

    private Vector<DropTarget> internalTargets = new Vector<DropTarget>();

    protected void addInternalDropTarget(DropTarget target) {
        internalTargets.add(target);
    }

    protected void removeInternalDropTarget(DropTarget target) {
        internalTargets.remove(target);
    }

    protected void setInternalTargetsActive(boolean active) {
        for(DropTarget t: internalTargets)
            t.setActive(active);
    }

    private Vector<FAreaMajor> majors = new Vector<FAreaMajor>();

    protected void addFAreaMajor(FAreaMajor major) {
        majors.add(major);
    }

    protected void removeFAreaMajor(FAreaMajor major) {
        majors.remove(major);
    }

    protected String writeXML() {
        String ret = "<system>\n";
        for(FAreaMajor fm: majors) {
            ret += fm.writeXML();
        }
        ret += "</system>\n";
        return ret;
    }

    void fromXML(Node xml, HashMap<String, Component> map) {
        //TODO PILNIE!
        //System.out.println("*\nSystem.fromXML");
        //System.out.println(xml);
    }

    void notifySplitListeners() {
        for(FSplitListener listener: listeners) {
            listener.splitAction();
        }
    }
}
