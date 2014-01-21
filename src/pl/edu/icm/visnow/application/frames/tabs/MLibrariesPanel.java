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

package pl.edu.icm.visnow.application.frames.tabs;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;
import javax.imageio.ImageIO;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import pl.edu.icm.visnow.application.frames.Frames;
import pl.edu.icm.visnow.engine.library.LibraryCore;
import pl.edu.icm.visnow.engine.library.LibraryFolder;
import pl.edu.icm.visnow.engine.library.LibraryRoot;
import pl.edu.icm.visnow.system.main.VisNow;
import pl.edu.icm.visnow.system.swing.LibTreeCellRenderer;

/**
 *
 * @author gacek
 */
public class MLibrariesPanel extends javax.swing.JPanel implements DragGestureListener, DragSourceListener
{

   public final static DataFlavor moduleAdderFlavor = new DataFlavor("visnow/moduleAdder", "SomeVisNowCores");
   //private static final boolean debug = false;
   //private static final boolean profile = false;
   protected Frames frames;

   public Frames getFrames()
   {
      return frames;
   }
   protected DefaultMutableTreeNode mainNode;
   protected boolean dragging;
   protected Cursor customCursor;
   protected boolean isRectangleCursor;
   private DragSource dragSource;

   //<editor-fold defaultstate="collapsed" desc=" [CONSTRUCTOR] ">
   /** Creates new form MLibrariesPanel */
   public MLibrariesPanel(Frames frames)
   {
      initComponents();
      this.frames = frames;
      refresh();
      ToolTipManager.sharedInstance().registerComponent(libTree);
      libTree.setCellRenderer(new LibTreeCellRenderer());
      try
      {
         Toolkit toolkit = Toolkit.getDefaultToolkit();
         Image cursorImage = ImageIO.read(getClass().getResourceAsStream("/pl/edu/icm/visnow/gui/icons/rectangleContour.gif"));
         Point cursorHotSpot = new Point(0, 0);
         customCursor = toolkit.createCustomCursor(cursorImage, cursorHotSpot, "Cursor");
         isRectangleCursor = true;
      } catch (IOException e)
      {
         isRectangleCursor = false;
      }




      this.dragSource = new DragSource();//DragSource.getDefaultDragSource();
      dragSource.createDefaultDragGestureRecognizer(
              libTree,
              DnDConstants.ACTION_COPY_OR_MOVE,
              this);




   }

   public void refresh()
   {
      filterField.setText("");
      libTree.setModel(VisNow.get().getMainLibraries().getLibrariesTreeModel());      
      dragging = false;
      
      ArrayList<TreePath> expansionPaths = new ArrayList<TreePath>();
      DefaultMutableTreeNode rootNode = ((DefaultMutableTreeNode) libTree.getModel().getRoot());      
      for (int i = 0; i < rootNode.getChildCount(); i++) {
           TreeNode node = rootNode.getChildAt(i);
           expansionPaths.add(new TreePath(((DefaultMutableTreeNode)node).getPath()));
           resolveExpansionPaths(expansionPaths, node);           
      }
      
      for (int i = 0; i < expansionPaths.size(); i++) {
           libTree.expandPath(expansionPaths.get(i));                      
      }
   }
   
    private void resolveExpansionPaths(ArrayList<TreePath> paths, TreeNode node) {
        if(!(node instanceof DefaultMutableTreeNode))
            return;        
        Object obj = ((DefaultMutableTreeNode)node).getUserObject();
        if(!(obj instanceof LibraryFolder))
            return;        
        LibraryFolder folder = (LibraryFolder)obj;        
        Vector<LibraryFolder> subFolders = folder.getSubFolders();
        if(folder.isOpen()) {            
            paths.add(new TreePath(((DefaultMutableTreeNode)node).getPath()));
        }
        if(subFolders != null && !subFolders.isEmpty()) {
            for (int i = 0; i < node.getChildCount(); i++) {
                resolveExpansionPaths(paths, node.getChildAt(i));                
            }
        }
    
    }
   
   
   //</editor-fold>

   //<editor-fold defaultstate="collapsed" desc=" Creating nodes ">
//    private MutableTreeNode createNodeFromFolder(LibraryFolder library) {
//        DefaultMutableTreeNode mtn = new DefaultMutableTreeNode(library);
//        for (LibraryFolder lib : library.getSubFolders()) {
//            mtn.add(createNodeFromFolder(lib));
//        }
//        for (LibraryCore core : library.getCores()) {
//            mtn.add(createNodeFromCore(core));
//        }
//        return mtn;
//    }
//
//    private MutableTreeNode createNodeFromCore(LibraryCore core) {
//        return new DefaultMutableTreeNode(core);
//    }
   //</editor-fold>
   //<editor-fold defaultstate="collapsed" desc=" Get selected cores ">
   private Vector<LibraryCore> getSelectedCores()
   {
      //if(debug2) System.out.println("DEBUG: getSelectedCores");
      //if(debug2) System.out.println("in Thread["+Thread.currentThread().getName()+"]");
      TreePath[] paths = libTree.getSelectionPaths();
      Vector<LibraryCore> ret = new Vector<LibraryCore>();
      if (paths == null)
      {
         return ret;
      }


      //int ret=0;
      //System.out.println("path - "+paths.length);
      for (int i = 0; i < paths.length; ++i)
      {
         if (((DefaultMutableTreeNode) paths[i].getLastPathComponent()).getUserObject() == null)
         {
            continue;
         }
         if (LibraryCore.class.isAssignableFrom(
                 ((DefaultMutableTreeNode) paths[i].getLastPathComponent()).getUserObject().getClass()))
         {
            ret.add((LibraryCore) ((DefaultMutableTreeNode) paths[i].getLastPathComponent()).getUserObject());
         }
      }
      //if (ret.size() == 0) {
      //    return null;
      // }
      return ret;
      //if(ret==0) return null;
//
//        LibraryCore[] tab = new LibraryCore[ret];
//        ret = 0;
//        for(int i=0; i<paths.length; ++i)
//            if(LibraryCore
//               .class
//               .isAssignableFrom(
//                    ((DefaultMutableTreeNode)paths[i].getLastPathComponent())
//                    .getUserObject()
//                    .getClass()
//            )) {
//                tab[ret] = (LibraryCore)((DefaultMutableTreeNode)paths[i].getLastPathComponent())
//                                .getUserObject();
//                ++ret;
//            }
//
//        return null;
   }

   //</editor-fold>
   @SuppressWarnings("unchecked")
   // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
   private void initComponents() {

      jPanel2 = new javax.swing.JPanel();
      jButton2 = new javax.swing.JButton();
      jPanel1 = new javax.swing.JPanel();
      jButton1 = new javax.swing.JButton();
      jPanel3 = new javax.swing.JPanel();
      filterField = new pl.edu.icm.visnow.gui.widgets.SearchField();
      jPanel4 = new javax.swing.JPanel();
      libScrollPane = new javax.swing.JScrollPane();
      libTree = new javax.swing.JTree();

      jPanel2.setBackground(new java.awt.Color(153, 153, 153));

      jButton2.setText("jButton2");

      javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
      jPanel2.setLayout(jPanel2Layout);
      jPanel2Layout.setHorizontalGroup(
         jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(jPanel2Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
            .addContainerGap())
      );
      jPanel2Layout.setVerticalGroup(
         jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      );

      jPanel1.setBackground(new java.awt.Color(153, 153, 153));

      jButton1.setText("jButton1");

      javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
      jPanel1.setLayout(jPanel1Layout);
      jPanel1Layout.setHorizontalGroup(
         jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(jPanel1Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jButton1)
            .addContainerGap(300, Short.MAX_VALUE))
      );
      jPanel1Layout.setVerticalGroup(
         jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addComponent(jButton1)
      );

      setLayout(new java.awt.BorderLayout());

      jPanel3.setMinimumSize(new java.awt.Dimension(200, 20));
      jPanel3.setPreferredSize(new java.awt.Dimension(200, 30));
      jPanel3.setLayout(new java.awt.BorderLayout());

      filterField.setEmptySearchFieldText("<filter modules>");
      filterField.addChangeListener(new javax.swing.event.ChangeListener() {
         public void stateChanged(javax.swing.event.ChangeEvent evt) {
            filterFieldStateChanged(evt);
         }
      });
      jPanel3.add(filterField, java.awt.BorderLayout.CENTER);

      add(jPanel3, java.awt.BorderLayout.NORTH);

      jPanel4.setLayout(new java.awt.BorderLayout());

      libTree.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            libTreeMouseClicked(evt);
         }
      });
      libScrollPane.setViewportView(libTree);

      jPanel4.add(libScrollPane, java.awt.BorderLayout.CENTER);

      add(jPanel4, java.awt.BorderLayout.CENTER);
   }// </editor-fold>//GEN-END:initComponents

    private void filterFieldStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_filterFieldStateChanged
       if (filterField.getSearchString() == null)
       {
          //libTree.setModel(VisNow.get().getMainLibraries().getLibrariesTreeModel());
          refresh();
       } else
       {
          libTree.setModel(VisNow.get().getMainLibraries().getNameFilteredLibrariesTreeModel(filterField.getSearchString()));
          dragging = false;
          expandAll(libTree, true);
       }

    }//GEN-LAST:event_filterFieldStateChanged

    private void libTreeMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_libTreeMouseClicked
    {//GEN-HEADEREND:event_libTreeMouseClicked
       TreePath p = libTree.getSelectionPath();
       if (p == null)
          return;
       DefaultMutableTreeNode tn = (DefaultMutableTreeNode) p.getLastPathComponent();
       if (tn == null)
          return;
       if (evt.getButton() == MouseEvent.BUTTON3 && tn.getUserObject() instanceof LibraryCore)
       {
          VisNow.get().showHelp(((LibraryCore) (tn.getUserObject())).getHelpTopicID());
          return;
       }

    }//GEN-LAST:event_libTreeMouseClicked

   public void dragGestureRecognized(DragGestureEvent dge)
   {
      //System.out.println("GESTURE RECOGNIZED!");
      Cursor c;
      if (isRectangleCursor)
      {
         c = customCursor;
      } else
      {
         c = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
      }

      if(getSelectedCores() != null && getSelectedCores().size() > 0)
         dge.startDrag(
              c,
              new ModuleAdder(getSelectedCores(), getFrames().getApplication()),
              this);
   }
   //<editor-fold defaultstate="collapsed" desc=" libTreeMousePressed ">    //</editor-fold>

   //<editor-fold defaultstate="collapsed" desc=" libTreeMouseReleased ">    //</editor-fold>
   //<editor-fold defaultstate="collapsed" desc=" addModules ">
   private void addModules(int sceneX, int sceneY)
   {
      print("addModules");

      new Thread(
              new ModuleAdder(getSelectedCores(), getFrames().getApplication())).start();

   }
   //</editor-fold>
   //<editor-fold defaultstate="collapsed" desc=" [VAR] ">
   // Variables declaration - do not modify//GEN-BEGIN:variables
   private pl.edu.icm.visnow.gui.widgets.SearchField filterField;
   private javax.swing.JButton jButton1;
   private javax.swing.JButton jButton2;
   private javax.swing.JPanel jPanel1;
   private javax.swing.JPanel jPanel2;
   private javax.swing.JPanel jPanel3;
   private javax.swing.JPanel jPanel4;
   private javax.swing.JScrollPane libScrollPane;
   private javax.swing.JTree libTree;
   // End of variables declaration//GEN-END:variables
   //</editor-fold>
   //<editor-fold defaultstate="collapsed" desc=" debug ">
   private boolean debug = false;

   private void print(String s)
   {
      if (!debug)
      {
         return;
      }
      System.out.println("MLibrariesPanel: \t" + s);
   }

   public void dragEnter(DragSourceDragEvent dsde)
   {
   }

   public void dragOver(DragSourceDragEvent dsde)
   {
   }

   public void dropActionChanged(DragSourceDragEvent dsde)
   {
   }

   public void dragExit(DragSourceEvent dse)
   {
   }

   public void dragDropEnd(DragSourceDropEvent dsde)
   {
   }

   //</editor-fold>
   public void expandAll(JTree tree, boolean expand)
   {
      TreeNode root = (TreeNode) tree.getModel().getRoot();

      // Traverse tree from root
      expandAll(tree, new TreePath(root), expand);
   }

   private void expandAll(JTree tree, TreePath parent, boolean expand)
   {
      // Traverse children
      TreeNode node = (TreeNode) parent.getLastPathComponent();
      if (node.getChildCount() >= 0)
      {
         for (Enumeration e = node.children(); e.hasMoreElements();)
         {
            TreeNode n = (TreeNode) e.nextElement();
            TreePath path = parent.pathByAddingChild(n);
            expandAll(tree, path, expand);
         }
      }

      // Expansion or collapse must be done bottom-up
      if (expand)
      {
         tree.expandPath(parent);
      } else
      {
         tree.collapsePath(parent);
      }
   }
}
