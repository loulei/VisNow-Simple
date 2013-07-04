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

package pl.edu.icm.visnow.system.swing.filechooser;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 *
 * @author  gacek
 */
public class TreeBrowser extends javax.swing.JPanel {

    DefaultTreeModel model;


    private boolean hiddenVisible = true;

    private boolean isHiddenVisible() {return hiddenVisible;}

    public DefaultMutableTreeNode getBlank() {
        return new DefaultMutableTreeNode("");
    }
    
    
    public void generateChildren(DefaultMutableTreeNode node) {
        //node.removeAllChildren();
        while(node.getChildCount() > 0) {
            model.removeNodeFromParent((DefaultMutableTreeNode)node.getChildAt(0));
        }
    //    System.out.println("GENERATING CHILDREN: "+node.getUserObject());
        //System.out.println(""+((File)node.getUserObject()).getPath());
        File file = (File)node.getUserObject();
        File[] tab = file.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                if(!pathname.isDirectory()) return false;
                if(!pathname.isHidden()) return true;
                if(hiddenVisible) return true;
                return false;
            }
        });
        DefaultMutableTreeNode tmp;
        Arrays.sort(tab);
        for(int i=0; i<tab.length; ++i) {
            //if(tab[i].isDirectory()) {
                tmp = new DefaultMutableTreeNode(tab[i]);
                nodes.put(tab[i], tmp);
                model.insertNodeInto(getBlank(), tmp, tmp.getChildCount());
                //tmp.add(getBlank());
                //node.add(tmp);
                model.insertNodeInto(tmp, node, node.getChildCount());
            //}
        }
    }
    
    private HashMap<File,DefaultMutableTreeNode> nodes;

    //<editor-fold defaultstate="collapsed" desc=" [CONSTRUCTOR] ">
    public TreeBrowser() {
        
        initComponents();
        
        fileTree.setCellRenderer(new TreeCellRenderer() {

            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                JLabel label = null;
                String ic;
                
                ic="+";
                if(leaf) ic = "<";
                if(expanded) ic = "-";
                
                if(value instanceof DefaultMutableTreeNode)
                if(((DefaultMutableTreeNode)value).getUserObject() instanceof File)
                    label = new JLabel("["+ic+"] "+
                            ((File)((DefaultMutableTreeNode)value).getUserObject()).getName()
                            );
                if(label == null)
                    label = new JLabel("[["+ic+"]] "+value.toString());
                
                if(expanded)
                    label.setFont(new java.awt.Font("Dialog", 1, 10));
                else
                    label.setFont(new java.awt.Font("Dialog", 0, 10));
                
                if(selected)
                    label.setBackground(new java.awt.Color(200,200,200));
                else
                    label.setBackground(new java.awt.Color(255,255,255));
                
                label.setOpaque(true);
                return label;
            }
        });
        fileTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        
        nodes = new HashMap<File,DefaultMutableTreeNode>();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(new File("/"));
        nodes.put((File)root.getUserObject(), root);
        //root.add(getBlank());
        model = new DefaultTreeModel(root);
        generateChildren(root);
        fileTree.setModel(model);
    }
    //</editor-fold>

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new JScrollPane();
        fileTree = new JTree();

        setBackground(new Color(51, 51, 51));
        DefaultMutableTreeNode treeNode1 = new DefaultMutableTreeNode("root");
        fileTree.setModel(new DefaultTreeModel(treeNode1));
        fileTree.setEditable(true);
        fileTree.addTreeWillExpandListener(new TreeWillExpandListener() {
            public void treeWillCollapse(TreeExpansionEvent evt)throws ExpandVetoException {
                fileTreeTreeWillCollapse(evt);
            }
            public void treeWillExpand(TreeExpansionEvent evt)throws ExpandVetoException {
                fileTreeTreeWillExpand(evt);
            }
        });
        fileTree.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                fileTreeMouseClicked(evt);
            }
        });
        fileTree.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent evt) {
                fileTreeKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(fileTree);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 373, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    private boolean ignoreExpand = false;

private void fileTreeTreeWillExpand(javax.swing.event.TreeExpansionEvent evt)throws javax.swing.tree.ExpandVetoException {//GEN-FIRST:event_fileTreeTreeWillExpand

    if(ignoreExpand) return;
    generateChildren((DefaultMutableTreeNode)evt.getPath().getLastPathComponent());
            //nodes.get(evt.getPath().getLastPathComponent()));
}//GEN-LAST:event_fileTreeTreeWillExpand

private void fileTreeTreeWillCollapse(javax.swing.event.TreeExpansionEvent evt)throws javax.swing.tree.ExpandVetoException {//GEN-FIRST:event_fileTreeTreeWillCollapse
// TODO add your handling code here:
}//GEN-LAST:event_fileTreeTreeWillCollapse

    //<editor-fold defaultstate="collapsed" desc=" Action events ">
private void fileTreeKeyPressed(KeyEvent evt) {//GEN-FIRST:event_fileTreeKeyPressed
    if(evt.getKeyChar() == '\n')
        actionPerformed();
}//GEN-LAST:event_fileTreeKeyPressed

private void fileTreeMouseClicked(MouseEvent evt) {//GEN-FIRST:event_fileTreeMouseClicked
    if(evt.getClickCount()==2)
        actionPerformed();
}//GEN-LAST:event_fileTreeMouseClicked

//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" ActionListeners ">

private Vector<ActionListener> listeners = new Vector<ActionListener>(5,5);
public synchronized void addActionListener(ActionListener listener) {
    listeners.add(listener);
}
public synchronized void removeActionListener(ActionListener listener) {
    listeners.remove(listener);
}


private void actionPerformed() {
    ActionEvent evt = new ActionEvent(
            ((DefaultMutableTreeNode)fileTree.getSelectionPath().getLastPathComponent()).getUserObject()
            , 0, "select");
    for(ActionListener listener: listeners)
        listener.actionPerformed(evt);
}
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" [VAR] Generated variables ">
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JTree fileTree;
    private JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
//</editor-fold>




    void setFolder(File folder) {
        ignoreExpand = true;


        TreePath tp = openFolder(folder);
        //generateChildren((DefaultMutableTreeNode)tp.getLastPathComponent());
        fileTree.expandPath(tp);
        fileTree.scrollPathToVisible(tp);
        //for(int i=0; i<tp.getPathCount(); ++i) {
        //    System.out.println(
        //            ((DefaultMutableTreeNode)tp.getPathComponent(i)).getUserObject()
        //   );
        //}

        //fileTree.treeDidChange();
        //fileTree.repaint();
        //fileTree.setModel(fileTree.getModel());
        ignoreExpand = false;
    }

    private TreePath openFolder(File folder) {
        //System.out.println("OPEN FOLDER: "+folder);
        if(folder == null) return null;
        TreePath path = openFolder(folder.getParentFile());
        if(path == null) {
        //    generateChildren((DefaultMutableTreeNode)model.getRoot());
            return new TreePath(
                (DefaultMutableTreeNode)model.getRoot()
            );
        }

        //System.out.println("NOW OPENING FOLDER: "+folder);

        DefaultMutableTreeNode parent = (DefaultMutableTreeNode)path.getLastPathComponent();
        DefaultMutableTreeNode current = null;

        for(int i=0; i<parent.getChildCount(); ++i) {
            if(((DefaultMutableTreeNode)parent.getChildAt(i)).getUserObject().equals(folder)) {
                current = (DefaultMutableTreeNode)parent.getChildAt(i);
            }
        }
        if(current == null) {
            
            current = new DefaultMutableTreeNode(folder);
            model.insertNodeInto(current, parent, parent.getChildCount());
            nodes.put(folder, current);
        }

        //if(nodes.containsKey(folder)) {
        //    DefaultMutableTreeNode current = nodes.get(folder);
           // System.out.println("  current child count: "+current.getChildCount());
           // System.out.println("  equals: "+current.getParent().equals(parent));
            generateChildren(current);
            TreePath next = path.pathByAddingChild(current);
            return next;
        //} else {
        //    System.out.println("ERROR IN TREE BROWSER");
        //    return null;
        //}
    }

    void setHiddenFoldersVisible(boolean b) {
        if(b != hiddenVisible) {
            hiddenVisible = b;
            if(b)
                showNodes((DefaultMutableTreeNode)model.getRoot());
            else
                hideNodes((DefaultMutableTreeNode)model.getRoot());
            //model.reload();//fileTree.setModel(model);
        } else {
            hiddenVisible = b;
        }
    }

    private void showNodes(DefaultMutableTreeNode t) {
        //System.out.println("SHOW: "+t.getUserObject());
        for(int i=0; i<t.getChildCount(); ++i) {
            showNodes((DefaultMutableTreeNode)t.getChildAt(i));
        }

        if(!( t.getUserObject() instanceof File))
            return;
        File f = (File)t.getUserObject();

        File[] tab = f.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                if(!pathname.isDirectory()) return false;
                if(!pathname.isHidden()) return false;
                return true;
            }
        });

        if(tab==null) return;
        Arrays.sort(tab);

        DefaultMutableTreeNode tmp;

        for(int i=0; i<tab.length; ++i) {
            //if(tab[i].isDirectory()) {
                tmp = new DefaultMutableTreeNode(tab[i]);
                nodes.put(tab[i], tmp);
                tmp.add(getBlank());
                model.insertNodeInto(tmp, t, t.getChildCount());
                //t.add(tmp);
            //}
        }


    }

    private void hideNodes(DefaultMutableTreeNode t) {
        //System.out.println("HIDE: "+t.getUserObject());
        for(int i=0; i<t.getChildCount(); ++i) {
            Object o = ((DefaultMutableTreeNode)t.getChildAt(i)).getUserObject();
            if(!( o instanceof File))
                continue;
            if(((File)o).isHidden()) {
                //System.out.println("HIDDEN, REMOVE: "+o);
                model.removeNodeFromParent((DefaultMutableTreeNode)t.getChildAt(i));//t.remove(i);
                --i;
                continue;
            }
            hideNodes((DefaultMutableTreeNode)t.getChildAt(i));
        }
    }

}
