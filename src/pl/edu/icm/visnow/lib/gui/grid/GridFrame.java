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

package pl.edu.icm.visnow.lib.gui.grid;

import eu.unicore.hila.Location;
import eu.unicore.hila.Resource;
import eu.unicore.hila.exceptions.HiLAException;
import eu.unicore.hila.grid.*;
import eu.unicore.hila.grid.unicore6.Unicore6ExportTask;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;
import pl.edu.icm.visnow.system.main.VisNow;

/**
 *
 * @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class GridFrame extends javax.swing.JFrame
{

   private static final int UNKNOWN = 0;
   private static final int WORKING = 1;
   private static final int DONE = 2;
   private static final int FAILED = 3;
   private static final int ABORTED = 4;
   private static Icon networkIcon = null;
   private static Icon siteIcon = null;
   private static Icon jobIcon = null;
   private static Icon closedDirIcon = null;
   private static Icon openDirIcon = null;
   private static Icon fileIcon = null;
   private DefaultMutableTreeNode selectedNode;
   private DefaultTreeModel model = null;
   private Resource selectedResource;
   private List<Job>  tList;
   private List<File> fList;
   private Location loc = null;
   private Resource res = null;
   private Site currentSite = null;
   private File currentFile = null;
   private String[] fileExtensions;
   private String[] requestedFileNames;
   private String[] transferredFileNames;
   private boolean aborted = false;
   private boolean singleFile = false;
   private Unicore6ExportTask[] transfers;
   private int[] transferStatus;
   private int[] currentTransferStatus;
   private int transferredFiles = 0;

   class GridCellRenderer extends DefaultTreeCellRenderer
   {
      public GridCellRenderer()
      {
         if (networkIcon == null)
            networkIcon = new ImageIcon(getClass().getResource("/pl/edu/icm/visnow/lib/gui/grid/network.png"));
         if (siteIcon == null)
            siteIcon = new ImageIcon(getClass().getResource("/pl/edu/icm/visnow/lib/gui/grid/server.png"));
         if (jobIcon == null)
            jobIcon = new ImageIcon(getClass().getResource("/pl/edu/icm/visnow/lib/gui/grid/project_open.png"));
         if (closedDirIcon == null)
            closedDirIcon = new ImageIcon(getClass().getResource("/pl/edu/icm/visnow/lib/gui/grid/folder.png"));
         if (openDirIcon == null)
            openDirIcon = new ImageIcon(getClass().getResource("/pl/edu/icm/visnow/lib/gui/grid/folder_open.png"));
         if (fileIcon == null)
            fileIcon = new ImageIcon(getClass().getResource("/pl/edu/icm/visnow/lib/gui/grid/file.png"));
      }

      private boolean hasAcceptableExtension(String name)
      {
         if (name == null || name.isEmpty() || fileExtensions == null)
            return false;
         for (int i = 0; i < fileExtensions.length; i++)
            if (name.endsWith(fileExtensions[i]))
               return true;
         return false;
      }

      private boolean isAcceptable(String name)
      {
         if (name == null || name.isEmpty() || requestedFileNames == null)
            return false;
         for (int i = 0; i < requestedFileNames.length; i++)
            if (name.equalsIgnoreCase(requestedFileNames[i]) && transferStatus[i] != DONE)
               return true;
         return false;
      }

      @Override
      public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                    boolean selected,  boolean expanded,
                                                    boolean leaf, int row, boolean hasFocus)
      {
         DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
         Object val = node.getUserObject();
         if (val == null)
            return new JLabel();

         String name = "";
         Icon icon = null;
         JLabel label = new JLabel(val.toString());
         try
         {
            if (node.isRoot())
            {
               name = "unicore6:/sites";
               icon = networkIcon;
            } else if (val instanceof Site)
            {
               name = ((Site) val).getName();
               icon = siteIcon;
            } else if (val instanceof Job)
            {
               name = ((Job) val).getName();
               icon = jobIcon;
            }
            if (val instanceof File)
            {
               File f = (File) val;
               name = f.getName();
               if (f.isDirectory())
               {
                  label.setForeground(Color.BLUE);
                  if (expanded)
                     icon = openDirIcon;
                  else
                     icon = closedDirIcon;
               } else 
               {
                  if (isAcceptable(name) || hasAcceptableExtension(name))
                     label.setForeground(new Color(0x00a000));
                  else
                     label.setForeground(new Color(0x050505));
                  icon = fileIcon;
               }
            }
            if (selected)
               label.setBackground(Color.LIGHT_GRAY);
            else
               label.setBackground(Color.YELLOW);
            label.setText(name);
            label.setIcon(icon);
         } catch (HiLAException hiLAException)
         {
         }
         label.setOpaque(selected);
         label.setBackground(Color.LIGHT_GRAY);
         return label;
      }
   }

   public GridFrame()
   {
      initComponents();
      model = (DefaultTreeModel)gridTree.getModel();
      gridTree.setCellRenderer(new GridCellRenderer());
   }

   public void setFileExtensions(String[] fileExtensions)
   {
      this.fileExtensions = fileExtensions;
   }

   public void setSingleFile(boolean singleFile)
   {
      this.singleFile = singleFile;
   }
   
   public void resetTransfer()
   {
      transferredFiles = 0;
   }
   
   public boolean isAborted()
   {
      return aborted;
   }

   public String[] getTransferredFileNames()
   {
      return transferredFileNames;
   }

   public void setRequestedFileNames(String[] requestedFileNames)
   {
      this.requestedFileNames = requestedFileNames;
      transferStatus = new int[requestedFileNames.length];
      for (int i = 0; i < requestedFileNames.length; i++)
         transferStatus[i] = UNKNOWN;
   }

   private class ListSites implements Runnable
   {
      @Override
      public void run()
      {
         try
         {
            loc = new Location("unicore6:/sites");
            progressBar.setIndeterminate(true);  
            res = loc.locate();
            progressBar.setIndeterminate(false);  
            SwingInstancer.swingRunAndWait(new Runnable()
            {
               @Override
               public void run()
               {
                  List<Resource> sitesList;
                  try
                  {
                     sitesList = res.getChildren();
                     int k = 0;
                     int n = selectedNode.getChildCount();
                     for (int i = n - 1; i >= 0; i--)
                        model.removeNodeFromParent((MutableTreeNode) selectedNode.getChildAt(i));
                     for (Resource resource : sitesList)
                     {
                        if (!(resource instanceof Site))
                           continue;
                        System.out.println(""+resource.getName());
                        model.insertNodeInto(new DefaultMutableTreeNode((Site) resource), selectedNode,k);
                        k += 1;
                     }
                  } catch (HiLAException ex)
                  {
                  }
               }
            });
         } catch (HiLAException hiLAException)
         {
         }
      }
   }

   private class ListJobs implements Runnable
   {

      @Override
      public void run()
      {
         try
         {
            currentSite = (Site) selectedResource;
            progressBar.setIndeterminate(true);  
            tList = currentSite.getTasks();
            progressBar.setIndeterminate(false);  
            SwingInstancer.swingRunAndWait(new Runnable()
            {
               @Override
               public void run()
               {
                  if (tList.isEmpty())
                     return;
                  int k = 0;
                  int n = selectedNode.getChildCount();
                  for (int i = n - 1; i >= 0; i--)
                     model.removeNodeFromParent((MutableTreeNode) selectedNode.getChildAt(i));
                  for (Job job : tList)
                  {
                     System.out.println("" + job.getName());
                     model.insertNodeInto(new DefaultMutableTreeNode(job), selectedNode,k);
                     k += 1;
                  }
                  progressBar.setIndeterminate(false);
               }
            });
         } catch (HiLAException hiLAException)
         {
         }
      }
   }

   private class ListJobDirectory implements Runnable
   {

      @Override
      public void run()
      {
         try
         {
            currentFile = ((Job) selectedResource).getWorkingDirectory();
            new ListDirectory().run();
         } catch (HiLAException hiLAException)
         {
         }
      }
   }
   
   private class ListDirectory implements Runnable
   {
      @Override
      public void run()
      {
         try
         {
            progressBar.setIndeterminate(true);    
            fList = currentFile.ls();
            progressBar.setIndeterminate(false);    
            int n = selectedNode.getChildCount();
            for (int i = n - 1; i >= 0; i--)
               model.removeNodeFromParent((MutableTreeNode)selectedNode.getChildAt(i));
            SwingInstancer.swingRunAndWait(new Runnable()
            {
               @Override
               public void run()
               {
                  int k = 0;
                  for (File file : fList)
                  {
                     System.out.println(""+file.getName());
                     model.insertNodeInto(new DefaultMutableTreeNode(file), selectedNode,k);
                     k += 1;
                  }
               }
            });
         } catch (HiLAException hiLAException)
         {
         }
      }
   }

   /**
    * This method is called from within the constructor to initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is always
    * regenerated by the Form Editor.
    */
   @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        gridTree = new javax.swing.JTree();
        readButton = new javax.swing.JButton();
        progressBar = new javax.swing.JProgressBar();
        closeButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setForeground(java.awt.Color.white);

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("unicore6:/sites");
        gridTree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        gridTree.setRowHeight(18);
        gridTree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                gridTreeMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(gridTree);

        readButton.setBackground(new java.awt.Color(204, 204, 204));
        readButton.setText("download and read selected file(s)");
        readButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                readButtonActionPerformed(evt);
            }
        });

        progressBar.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N

        closeButton.setText("close ");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(readButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 316, Short.MAX_VALUE)
                    .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(closeButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 419, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(readButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(closeButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

   private void readButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_readButtonActionPerformed
   {//GEN-HEADEREND:event_readButtonActionPerformed
      TreePath[] paths = gridTree.getSelectionPaths();
      if (paths == null || paths.length < 1)
         return;
      File[] selectedFiles = new File[paths.length];
      int nSelFiles = 0;
pathLoop:      
      for (int i = 0; i < paths.length; i++)
      {
         Object sel = paths[i].getLastPathComponent();
         if (!(sel instanceof DefaultMutableTreeNode))
            continue;
         selectedNode = (DefaultMutableTreeNode) sel;      
         if (selectedNode.isRoot() || 
             selectedNode.getUserObject() == null || 
           !(selectedNode.getUserObject() instanceof File))
            continue;
         File selFile = (File)selectedNode.getUserObject();
         String name = selFile.getName();
         if (name == null || name.isEmpty())
            continue;
         if (requestedFileNames != null && requestedFileNames.length > 0)
         {
            for (int j = 0; j < requestedFileNames.length; j++)
            if (name.equalsIgnoreCase(requestedFileNames[j]) && transferStatus[j] == UNKNOWN)
            {
               selectedFiles[nSelFiles] = selFile;
               nSelFiles += 1;
               continue pathLoop;
            }
         }
         if (fileExtensions == null || fileExtensions.length < 1)
         {
            selectedFiles[nSelFiles] = selFile;
            nSelFiles += 1;
            continue pathLoop;
         }
         for (int j = 0; j < fileExtensions.length; j++)
            if (name.endsWith(fileExtensions[j]))
            {
               selectedFiles[nSelFiles] = selFile;
               nSelFiles += 1;
               continue pathLoop;
            }
      }
      transfers = new Unicore6ExportTask[selectedFiles.length];
      transferredFileNames = new String[selectedFiles.length];
      currentTransferStatus = new int[selectedFiles.length];
      for (int i = 0; i < selectedFiles.length; i++)
         currentTransferStatus[i] = UNKNOWN;
      progressBar.setIndeterminate(true);    
      for (int i = 0; i < nSelFiles; i++)
      {
         try
         {
            transferredFileNames[i] = selectedFiles[i].getName();
            SimpleTransfer t = selectedFiles[i].exportToLocalFile(VisNow.getTmpDir());
            if (!(t instanceof Unicore6ExportTask))
               System.out.println("transfer is not Unicore6ExportTask");
            else
            {
               transfers[i] = (Unicore6ExportTask) t;
               currentTransferStatus[i] = WORKING;
            }
         } catch (Exception e)
         {
            e.printStackTrace();
         }
      }
      new Thread(new Runnable()
      {

         @Override
         //<editor-fold defaultstate="collapsed" desc="comment">
         public void run()
         {
            try
            {
               while (true)
               {
                  for (int i = 0; i < transfers.length; i++)
                  {
                     if (transfers[i] == null)
                     {
                        currentTransferStatus[i] = DONE;
                        System.out.println("vanished " + i);
                     } else
                     {
                        if (transfers[i].status() == TaskStatus.SUCCESSFUL)
                           currentTransferStatus[i] = DONE;
                        if (transfers[i].status() == TaskStatus.ABORTED ||
                            transfers[i].status() == TaskStatus.FAILED)
                           currentTransferStatus[i] = FAILED;
                     }
                  }
                  transferredFiles = 0;
                  for (int i = 0; i < currentTransferStatus.length; i++)
                     if (currentTransferStatus[i] == DONE)
                        transferredFiles += 1;
                  System.out.println(""+transferredFiles);
                  if (transferredFiles == currentTransferStatus.length)
                     break;
                  Thread.sleep(250);
               }
               if (requestedFileNames != null)
               {
                  for (int i = 0; i < requestedFileNames.length; i++)
                     for (int j = 0; j < transferredFileNames.length; j++)
                        if (requestedFileNames[i].equals(transferredFileNames[j])
                                && (transfers[j] != null) && transfers[j].status() == TaskStatus.SUCCESSFUL)
                        
                           transferStatus[i] = DONE;
               }
               if (singleFile && transferredFiles == 1 || 
                   requestedFileNames != null && transferredFiles == requestedFileNames.length)
               {
                  aborted = false;
                  fireStateChanged();
               }
               SwingInstancer.swingRunAndWait(new Runnable()
               {

                  @Override
                  public void run()
                  {
                     gridTree.repaint();
                  }
               });
            } catch (Exception e)
            {
               e.printStackTrace();
            }
         }
      }).start();
   }//GEN-LAST:event_readButtonActionPerformed

   private void closeButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_closeButtonActionPerformed
   {//GEN-HEADEREND:event_closeButtonActionPerformed
      aborted = true;
      setVisible(false);
      fireStateChanged();// TODO add your handling code here:
   }//GEN-LAST:event_closeButtonActionPerformed

   private void gridTreeMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_gridTreeMouseClicked
   {//GEN-HEADEREND:event_gridTreeMouseClicked
      if(evt.getClickCount() != 2)
         return;
      TreePath selectedPath = gridTree.getPathForLocation(evt.getX(), evt.getY());
      Object sel = selectedPath.getLastPathComponent();
      if (!(sel instanceof DefaultMutableTreeNode))
      {
         System.out.println("" + sel);
         return;
      }
      selectedNode = (DefaultMutableTreeNode) sel;
      if (evt.getClickCount() > 1)
      {     
         progressBar.setIndeterminate(true);    
         try
         {
            if (selectedNode.isRoot())
               new ListSites().run();
            else
            {
               selectedResource = (Resource) selectedNode.getUserObject();
               if (selectedResource instanceof Site)
                  new ListJobs().run();
               else if (selectedResource instanceof Job)
                  new ListJobDirectory().run();
               else if (selectedResource instanceof File)
               {
                  currentFile = (File) selectedResource;
                  if (currentFile.isDirectory())
                     new ListDirectory().run();
               }
            }            
            gridTree.expandPath(selectedPath);
         } catch (HiLAException hiLAException)
         {
         }
      }
      gridTree.expandPath(selectedPath);
   }//GEN-LAST:event_gridTreeMouseClicked
   /**
    * Utility field holding list of ChangeListeners.
    */
   private transient ArrayList<ChangeListener> changeListenerList =
           new ArrayList<ChangeListener>();

   /**
    * Registers ChangeListener to receive events.
    * <p/>
    * @param listener The listener to register.
    */
   public synchronized void addChangeListener(ChangeListener listener)
   {
      changeListenerList.add(listener);
   }

   /**
    * Removes ChangeListry {
    *
    * } catch (HiLAException hiLAException){} tener from the list of listeners.
    * <p/>
    * @param listener The listener to remove.
    */
   public synchronized void removeChangeListener(ChangeListener listener)
   {
      changeListenerList.remove(listener);
   }

   /**
    * Notifies all registered listeners about the event.
    * <p/>
    * @param object Parameter #1 of the
    * <CODE>ChangeEvent<CODE> constructor.
    */
   private void fireStateChanged()
   {
      ChangeEvent e = new ChangeEvent(this);
      for (ChangeListener listener : changeListenerList)
         listener.stateChanged(e);
   }

   /**
    * @param args the command line arguments
    */
   public static void main(String args[])
   {
      /*
       * Set the Nimbus look and feel
       */
      //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
       * If Nimbus (introduced in Java SE 6) is not available, stay with the
       * default look and feel. For details see
       * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
      
      try
      {
         for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels())
            if ("Nimbus".equals(info.getName()))
            {
               javax.swing.UIManager.setLookAndFeel(info.getClassName());
               break;
            }
      } catch (ClassNotFoundException ex)
      {
         java.util.logging.Logger.getLogger(GridFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      } catch (InstantiationException ex)
      {
         java.util.logging.Logger.getLogger(GridFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      } catch (IllegalAccessException ex)
      {
         java.util.logging.Logger.getLogger(GridFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      } catch (javax.swing.UnsupportedLookAndFeelException ex)
      {
         java.util.logging.Logger.getLogger(GridFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      } */
      //</editor-fold>

      /*
       * Create and display the form
       */
      System.out.println(""+java.io.File.pathSeparator+" "+java.io.File.separator);
      java.awt.EventQueue.invokeLater(new Runnable()
      {

         @Override
         public void run()
         {
            GridFrame f = new GridFrame();
            f.addChangeListener(new ChangeListener(){

               @Override
               public void stateChanged(ChangeEvent e)
               {
                  System.out.println("all done");
                  System.exit(0);
               }
               
            });
            f.setRequestedFileNames(new String[]{"pressure00", "temperature00", "pressure01", "temperature01"});
            f.setVisible(true);
         }
      });
   }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeButton;
    private javax.swing.JTree gridTree;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JButton readButton;
    // End of variables declaration//GEN-END:variables
}
