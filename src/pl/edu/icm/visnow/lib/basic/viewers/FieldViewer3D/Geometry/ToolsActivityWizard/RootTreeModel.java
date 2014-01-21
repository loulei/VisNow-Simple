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

package pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.ToolsActivityWizard;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * @author  Bartosz Borucki (babor@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */

public class RootTreeModel implements TreeModel {
    protected EventListenerList listenerList = new EventListenerList();
    private RootEntry root;

    public RootTreeModel(RootEntry root) {
        this.root = root;
    }


    public Object getRoot() {
        return root;
    }

    public Object getChild(Object parent, int index) {
        if(!(parent instanceof Entry))
            return null;
        
        if(parent instanceof RootEntry) {
            return ((RootEntry)parent).getBranch(index);
        }

        if(parent instanceof BranchEntry) {
            return ((BranchEntry)parent).getDependancy(index);
        }

        if(parent instanceof CpointEntry) {
            return ((CpointEntry)parent).getDependancy(index);
        }

        return null;
    }

    public int getChildCount(Object parent) {
        if(!(parent instanceof Entry))
            return 0;

        if(parent instanceof RootEntry) {
            return ((RootEntry)parent).getBranchesSize();
        }

        if(parent instanceof BranchEntry) {
            return ((BranchEntry)parent).getDependanciesSize();
        }

        if(parent instanceof CpointEntry) {
            return ((CpointEntry)parent).getDependanciesSize();
        }

        return 0;
    }

    public boolean isLeaf(Object node) {
        if(node instanceof RootEntry) {
            return (((RootEntry)node).getBranchesSize() == 0);
        }

        if(node instanceof BranchEntry) {
            return (((BranchEntry)node).getDependanciesSize() == 0);
        }

        if(node instanceof CpointEntry) {
            return (((CpointEntry)node).getDependanciesSize() == 0);
        }

        if(node instanceof ToolEntry) {
            return true;
        }

        return true;
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
    }

    public int getIndexOfChild(Object parent, Object child) {
        if(!(parent instanceof Entry) || !(child instanceof Entry))
            return -1;

        if((parent instanceof RootEntry) && (child instanceof BranchEntry)) {
            return ((RootEntry)parent).getBranchIndex((BranchEntry)child);
        }

        if((parent instanceof BranchEntry) && (child instanceof Entry)) {
            return ((BranchEntry)parent).getDependancyIndex((Entry)child);
        }

        if((parent instanceof CpointEntry) && (child instanceof ToolEntry)) {
            return ((CpointEntry)parent).getDependancyIndex((ToolEntry)child);
        }

        return -1;
    }

    public void addTreeModelListener(TreeModelListener l) {
        listenerList.add(TreeModelListener.class, l);
    }

    public void removeTreeModelListener(TreeModelListener l) {
        listenerList.remove(TreeModelListener.class, l);
    }

}
