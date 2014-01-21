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

package pl.edu.icm.visnow.system.libraries;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.ToolTipManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import pl.edu.icm.visnow.application.application.Application;
import pl.edu.icm.visnow.engine.library.LibraryCore;
import pl.edu.icm.visnow.lib.basic.mappers.Isosurface.IsosurfaceGUI;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;
import pl.edu.icm.visnow.system.main.VisNow;
import pl.edu.icm.visnow.system.swing.LibTreeCellRenderer;

/**
 *
 * @author babor
 */
//public class NewModuleTreePanel extends javax.swing.JPanel {
public class NewModuleTreePanel extends javax.swing.JMenuItem
{

   private Point point = null;

   /** Creates new form TreePanel */
   public NewModuleTreePanel(DefaultTreeModel model, final Point point, final JPopupMenu popup, final JComponent parentComponent)
   {
      initComponents();
      this.point = point;
      ToolTipManager.sharedInstance().registerComponent(tree);
      tree.setCellRenderer(new LibTreeCellRenderer());
      tree.setModel(model);
      tree.expandRow(0);
      if (tree.getRowCount() > 0)
         tree.expandRow(1);
      tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
      tree.addMouseListener(new MouseListener()
      {
         @Override
         public void mouseClicked(MouseEvent e)
         {
            TreePath p = tree.getSelectionPath();
            if (p == null)
               return;
            final DefaultMutableTreeNode tn = (DefaultMutableTreeNode) p.getLastPathComponent();
            if (tn == null)
               return;
            if (e.getButton() == MouseEvent.BUTTON3 && tn.getUserObject() instanceof LibraryCore)
            {
               VisNow.get().showHelp(((LibraryCore)(tn.getUserObject())).getHelpTopicID());
               return;
            }
            if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2)
            {
               if (tn.isLeaf() && tn.getUserObject() instanceof LibraryCore)
               {
                    VisNow.get().getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    VisNow.get().getMainWindow().repaint();
                    popup.setVisible(false);
                    SwingInstancer.swingRunAndWait(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            LibraryCore cr = (LibraryCore)tn.getUserObject();
                            Application a = VisNow.get().getMainWindow().getApplicationsPanel().getCurrentApplication();
                            if(point == null)
                                    a.addModuleByName(cr.getName(), cr.getClassPath(), false);
                            else
                                    a.addModuleByName(cr.getName(), cr.getClassPath(), point, false);                        

                            VisNow.get().getMainWindow().setCursor(Cursor.getDefaultCursor());
                            VisNow.get().getMainWindow().repaint();                  
                        }
                    });
                  
               }
            }
         }

         @Override
         public void mousePressed(MouseEvent e) {}

         @Override
         public void mouseReleased(MouseEvent e) {}

         @Override
         public void mouseEntered(MouseEvent e) {}

         @Override
         public void mouseExited(MouseEvent e) {}
      });
   }

   /** This method is called from within the constructor to
    * initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is
    * always regenerated by the Form Editor.
    */
   @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tree = new javax.swing.JTree();

        setMinimumSize(new java.awt.Dimension(260, 400));
        setPreferredSize(new java.awt.Dimension(260, 400));
        setLayout(new java.awt.BorderLayout());

        jScrollPane1.setViewportView(tree);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTree tree;
    // End of variables declaration//GEN-END:variables
   /**
    * Utility field holding list of ChangeListeners.
    */
   protected transient ArrayList<ChangeListener> changeListenerList = new ArrayList<ChangeListener>();

   /**
    * Registers ChangeListener to receive events.
    * @param listener The listener to register.
    */
   @Override
   public synchronized void addChangeListener(ChangeListener listener)
   {
      changeListenerList.add(listener);
   }

   /**
    * Removes ChangeListener from the list of listeners.
    * @param listener The listener to remove.
    */
   @Override
   public synchronized void removeChangeListener(ChangeListener listener)
   {
      changeListenerList.remove(listener);
   }

   /**
    * Notifies all registered listeners about the event.
    *
    * @param object Parameter #1 of the <CODE>ChangeEvent<CODE> constructor.
    */
   @Override
   public void fireStateChanged()
   {
      ChangeEvent e = new ChangeEvent(this);
      for (int i = 0; i < changeListenerList.size(); i++)
      {
         changeListenerList.get(i).stateChanged(e);
      }
   }
}
