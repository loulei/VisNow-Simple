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

package pl.edu.icm.visnow.lib.utils.geometry2D;

import java.util.ArrayList;
import java.util.Vector;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author  Bartosz Borucki (babor@icm.edu.pl)
 * Warsaw University, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class GeometryObject2DStruct implements Cloneable, ChangeListener {

    protected GeometryObject2D me;
    protected GeometryObject2DStruct parent = null;
    protected ArrayList<GeometryObject2DStruct> children = new ArrayList<GeometryObject2DStruct>();

    private String parentModulePort = "";
    
    /** Creates a new instance of GeometryObject2DStruct */
    public GeometryObject2DStruct() {
        me = new GeometryObject2D("object 2D");
        this.me.addChangeListener(this);
    }

    public GeometryObject2DStruct(GeometryObject2D obj) {
       if (obj == null)
          return;
        this.me = obj;
        this.me.addChangeListener(this);
    }
    
    public void setParent(GeometryObject2DStruct parent) {
        this.parent = parent;
        this.addChangeListener(parent);
    }

    public void addChild(GeometryObject2DStruct child) {
        addChild(child, children.size());
    }

    public void addChild(GeometryObject2DStruct child, int layer) {
        boolean is = false;
        for (GeometryObject2DStruct ch : children) {
            if (ch.getName().equals(child.getName())) {
                is = true;
            }
        }
        if (is) {
            removeChild(child);
        }

        if (layer >= children.size()) {
            children.add(child);
        } else {
            children.set(layer, child);
        }
        child.setParent(this);        
        fireStateChanged();
    }

    @Override
    public String toString() {
        return me.getName();
    }

    public boolean removeChild(GeometryObject2DStruct child) {
        child.removeChangeListener(this);
        return children.remove(child);
    }

    public ArrayList<GeometryObject2DStruct> getChildren() {
        return children;
    }

    public void removeAllChildren() {
        for(GeometryObject2DStruct child : children) {
            child.removeChangeListener(this);
        }
        children.clear();
    }

    public String getName() {
        if(me == null)
            return "N/A";
        return me.getName();
        
    }

    public void setName(String name) {
        if(me == null)
            return;
        
        me.setName(name);
        fireStateChanged();
    }

    public boolean isMyChild(GeometryObject2DStruct child) {
        return children.contains(child);
    }

    public void moveChildDown(GeometryObject2DStruct child) {
        if (isMyChild(child)) {
            int index = children.indexOf(child);
            if (index > 0) {
                children.remove(index);
                children.set(index - 1, child);
            }
        }
        fireStateChanged();
    }

    public void moveChildUp(GeometryObject2DStruct child) {
        if (isMyChild(child)) {
            int index = children.indexOf(child);
            if (index >= 0 && index < children.size() - 1) {
                children.remove(index);
                children.set(index + 1, child);
            }
        }
        fireStateChanged();
    }

    public GeometryObject2DStruct getParent() {
        return parent;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        try {
            GeometryObject2DStruct out = (GeometryObject2DStruct) super.clone();
            out.parent = null;
            out.children = new ArrayList<GeometryObject2DStruct>();
            for (int i = 0; i < children.size(); i++) {
                out.addChild((GeometryObject2DStruct) children.get(i).clone());
            }
            return out;
        } catch (CloneNotSupportedException ex) {
            return null;
        }
    }

    public GeometryObject2D getGeometryObject2D() {
        return me;
    }

    public void setGeometryObject2D(GeometryObject2D me) {
        if(this.me != null)
            this.me.removeChangeListener(this);
        
        this.me = me;
        
        if(this.me != null)
            this.me.addChangeListener(this);
        fireStateChanged();
    }

    public String getParentModulePort() {
        return parentModulePort;
    }

    public void setParentModulePort(String parentModulePort) {
        this.parentModulePort = parentModulePort;
    }

   /**
     * Utility field holding list of ChangeListeners.
     */
    private transient ArrayList<ChangeListener> changeListenerList =
            new ArrayList<ChangeListener>();

    /**
     * Registers ChangeListener to receive events.
     * @param listener The listener to register.
     */
    public synchronized void addChangeListener(ChangeListener listener) {
        changeListenerList.add(listener);
    }

    /**
     * Removes ChangeListener from the list of listeners.
     * @param listener The listener to remove.
     */
    public synchronized void removeChangeListener(ChangeListener listener) {
        changeListenerList.remove(listener);
    }

    /**
     * Notifies all registered listeners about the event.
     *
     * @param object Parameter #1 of the <CODE>ChangeEvent<CODE> constructor.
     */
    private void fireStateChanged() {
        ChangeEvent e = new ChangeEvent(this);
        for (int i = 0; i < changeListenerList.size(); i++) {
            changeListenerList.get(i).stateChanged(e);            
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        fireStateChanged();
    }
}
