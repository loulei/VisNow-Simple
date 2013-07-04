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

package pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.TabbedUI;

import java.util.Vector;
import javax.swing.JPanel;

/**
 *
 * @author vis
 */
public class TabbedUI extends javax.swing.JPanel
{

   Vector<UITab> tabs = new Vector<UITab>();

   /**
    * Creates new form TabbedUI
    */
   public TabbedUI()
   {
      initComponents();
      updateGUI();
   }

   public void updateGUI()
   {
      tabbedPane.removeAll();
      for (int i = 0; i < tabs.size(); i++)
      {
         tabbedPane.addTab(tabs.get(i).getTitle(), tabs.get(i));
      }
   }

   public UITab addUITab(UITab tab)
   {
      for (int i = 0; i < tabs.size(); i++)
      {
         if (tabs.get(i).equals(tab))
            return tabs.get(i);
      }

      tabs.add(tab);
      updateGUI();
      return tab;
   }

   public void removeUITab(UITab tab)
   {
      tabs.remove(tab);
      updateGUI();
   }

   public void removeUITab(String title)
   {
      removeUITab(getUITab(title));
   }

   public UITab getUITab(String title)
   {
      for (int i = 0; i < tabs.size(); i++)
      {
         if (tabs.get(i).getTitle().equals(title))
         {
            return tabs.get(i);
         }
      }
      return addUITab(new UITab(title));
   }

   public void addUIToTab(JPanel ui, String title)
   {
      getUITab(title).addUI(ui);
   }

   public void addUIToTab(JPanel ui, String title, int position)
   {
      tabs.insertElementAt(new UITab(title), position);
      getUITab(title).addUI(ui);
      updateGUI();
   }

   @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabbedPane = new javax.swing.JTabbedPane();

        setBackground(java.awt.SystemColor.control);
        setMaximumSize(new java.awt.Dimension(230, 2147483647));
        setMinimumSize(new java.awt.Dimension(230, 7));
        setPreferredSize(new java.awt.Dimension(230, 650));
        setLayout(new java.awt.BorderLayout());

        tabbedPane.setBackground(java.awt.SystemColor.control);
        add(tabbedPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables
}
