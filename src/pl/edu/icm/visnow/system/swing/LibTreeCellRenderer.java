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

package pl.edu.icm.visnow.system.swing;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import pl.edu.icm.visnow.engine.library.LibraryCore;
import pl.edu.icm.visnow.engine.library.LibraryFolder;
import pl.edu.icm.visnow.engine.library.LibraryRoot;

/**
 *
 * @author  Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class LibTreeCellRenderer extends DefaultTreeCellRenderer
{

   private final static Color selectedBgColor = new Color(200, 200, 200);

   public LibTreeCellRenderer()
   {
   }

   @Override
   public Component getTreeCellRendererComponent(
           JTree tree,
           Object value,
           boolean selected,
           boolean expanded,
           boolean leaf,
           int row,
           boolean hasFocus)
   {
      DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) value;
      Object val = dmtn.getUserObject();
      if (val == null)
      {
         return new JLabel();
      }

      String name;
      JLabel label = new JLabel(val.toString());

      //////////////////////////////ROOT
      if (LibraryRoot.class.isAssignableFrom(val.getClass()))
      {
         name = ((LibraryRoot) val).getName();
         label.setText(name);
      }

      //////////////////////////////FOLDER
      if (LibraryFolder.class.isAssignableFrom(val.getClass()))
      {
         name = ((LibraryFolder) val).getName();
         label.setText(name);
         LibraryFolder lf = (LibraryFolder) val;
         if (lf.getRoot().getRootFolder() == lf)
         {
            if (lf.getRoot().getType() == LibraryRoot.INTERNAL)
            {
               label.setIcon(Icons.getLibraryRootIcon(LibraryRoot.INTERNAL, 1));
            } else
            {
               label.setIcon(Icons.getLibraryRootIcon(LibraryRoot.JAR, 1));
            }
         } else
         {
            label.setIcon(Icons.getLibraryFolderIcon(1));
         }
      }

      ///////////////////////////////CORE
      if (LibraryCore.class.isAssignableFrom(val.getClass()))
      {
         name = ((LibraryCore) val).getName();
         label.setText(name);
         label.setIcon(Icons.getLibraryCoreIcon(1));
         label.setToolTipText(((LibraryCore) val).getShortDescription());
      }

      if (selected)
      {
         label.setOpaque(true);
         label.setBackground(/*TODO: swing color*/selectedBgColor);
      }
      //if (val instanceof ModuleCore)
      //    setToolTipText(((ModuleCore)val).getShortDesc());

      if (dmtn.getAllowsChildren())
      {
         label.setForeground(Color.BLACK);
      } else
      {
         //temporary state for conditional accept
         label.setForeground(Color.ORANGE);
      }

      return label;
   }
}
