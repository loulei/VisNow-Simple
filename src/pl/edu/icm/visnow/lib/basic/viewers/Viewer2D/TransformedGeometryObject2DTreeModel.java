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

package pl.edu.icm.visnow.lib.basic.viewers.Viewer2D;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import pl.edu.icm.visnow.lib.utils.geometry2D.TransformedGeometryObject2D;

/**
* @author  Bartosz Borucki (babor@icm.edu.pl)
* Warsaw University, Interdisciplinary Centre
* for Mathematical and Computational Modelling
*/
public class TransformedGeometryObject2DTreeModel implements TreeModel {
    private TransformedGeometryObject2D rootObject = null;
    protected EventListenerList listenerList = new EventListenerList();

    
    public TransformedGeometryObject2DTreeModel(TransformedGeometryObject2D rootObject) {
        this.rootObject = rootObject;
    }    
    
    public Object getRoot() {
        return rootObject;
    }

    public Object getChild(Object parent, int index) {
        if(parent == null)
            return null;
        
        int size = ((TransformedGeometryObject2D)parent).getChildren().size();

        if(index < 0 || index >= size)
                return null;
        
        return ((TransformedGeometryObject2D)parent).getChildren().get(size-index-1);
    }

    public int getChildCount(Object parent) {
        if(parent == null)
            return 0;
        
        return ((TransformedGeometryObject2D)parent).getChildren().size();
    }

    public boolean isLeaf(Object node) {
        if(node == null)
            return true;
        
        if( ((TransformedGeometryObject2D)node).getChildren() == null || ((TransformedGeometryObject2D)node).getChildren().size() < 1 )
            return true;
        
        return false;
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
    }

    public int getIndexOfChild(Object parent, Object child) {
        if(parent == null || child == null)
            return 0;

        int size = ((TransformedGeometryObject2D)parent).getChildren().size();

        return (size-((TransformedGeometryObject2D)parent).getChildren().indexOf((TransformedGeometryObject2D)child)-1);
    }

    public void addTreeModelListener(TreeModelListener l) {
        listenerList.add(TreeModelListener.class, l);
    }

    public void removeTreeModelListener(TreeModelListener l) {
        listenerList.remove(TreeModelListener.class, l);
    }

    public TreeModelListener[] getTreeModelListeners() {
        return (TreeModelListener[])listenerList.getListeners(
                TreeModelListener.class);
    }
    
    protected void fireTreeNodesChanged(Object source, Object[] path, 
                                        int[] childIndices, 
                                        Object[] children) {
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==TreeModelListener.class) {
                // Lazily create the event:
                if (e == null)
                    e = new TreeModelEvent(source, path, 
                                           childIndices, children);
                ((TreeModelListener)listeners[i+1]).treeNodesChanged(e);
            }          
        }
    }

    protected void fireTreeNodesInserted(Object source, Object[] path, 
                                        int[] childIndices, 
                                        Object[] children) {
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==TreeModelListener.class) {
                // Lazily create the event:
                if (e == null)
                    e = new TreeModelEvent(source, path, 
                                           childIndices, children);
                ((TreeModelListener)listeners[i+1]).treeNodesInserted(e);
            }          
        }
    }

    protected void fireTreeNodesRemoved(Object source, Object[] path, 
                                        int[] childIndices, 
                                        Object[] children) {
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==TreeModelListener.class) {
                if (e == null)
                    e = new TreeModelEvent(source, path, 
                                           childIndices, children);
                ((TreeModelListener)listeners[i+1]).treeNodesRemoved(e);
            }          
        }
    }

    protected void fireTreeStructureChanged(Object source, Object[] path, 
                                        int[] childIndices, 
                                        Object[] children) {
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==TreeModelListener.class) {
                if (e == null)
                    e = new TreeModelEvent(source, path, 
                                           childIndices, children);
                ((TreeModelListener)listeners[i+1]).treeStructureChanged(e);
            }          
        }
    }

    private void fireTreeStructureChanged(Object source, TreePath path) {
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==TreeModelListener.class) {
                if (e == null)
                    e = new TreeModelEvent(source, path);
                ((TreeModelListener)listeners[i+1]).treeStructureChanged(e);
            }
        }
    }
    
}
