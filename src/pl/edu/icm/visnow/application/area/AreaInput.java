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

package pl.edu.icm.visnow.application.area;

import java.awt.Component;
import java.awt.Point;
import java.util.Vector;
import pl.edu.icm.visnow.application.area.widgets.LinkPanel;
import pl.edu.icm.visnow.application.area.widgets.ModulePanel;
import pl.edu.icm.visnow.engine.commands.LinkDeleteCommand;
import pl.edu.icm.visnow.engine.commands.ModuleDeleteCommand;
import pl.edu.icm.visnow.engine.core.Link;
import pl.edu.icm.visnow.engine.core.LinkName;

/**
 *
 * @author Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class AreaInput
{

   private Area area;

   public Area getArea()
   {
      return area;
   }

   protected AreaInput(Area area)
   {
      this.area = area;
   }

   public void showLock(final boolean b)
   {
      java.awt.EventQueue.invokeLater(new Runnable()
      {
         public void run()
         {
            area.getPanel().setLocked(b);

         }
      });
   }

   public Point getModulePosition(String name)
   {
      return area.getPanel().getModulePanel(name).getLocation();
   }

   public void setModuleProgress(final String name, final float progress)
   {
      java.awt.EventQueue.invokeLater(new Runnable()
      {
         @Override
         public void run()
         {
            ModulePanel panel = area.getPanel().getModulePanel(name);
            if (panel != null)
               panel.setProgress(progress);
         }
      });
   }

   public Vector<SelectableAreaItem> getSelectedItems()
   {
      return this.getArea().getSelection();
   }

   public void addModuleWidget(final String name, final Point position)
   {
      java.awt.EventQueue.invokeLater(new Runnable()
      {
         @Override
         public void run()
         {
            ModulePanel p = new ModulePanel(area.getPanel(), area.getApplication().getEngine().getModule(name));
            area.getPanel().addModulePanel(p, position);
         }
      });
   }

   public void selectModule(final String name)
   {
      java.awt.EventQueue.invokeLater(new Runnable()
      {
         public void run()
         {
            area.select(area.getPanel().getModulePanel(name));
         }
      });
   }

   public void renameModuleWidget(final String name, final String newName)
   {
      java.awt.EventQueue.invokeLater(new Runnable()
      {
         public void run()
         {
//               
            area.getPanel().getModulePanel(name).updateName();
            area.getPanel().updateModuleName(name, newName);
         }
      });

   }

   public void deleteModuleWidget(final String name)
   {
      java.awt.EventQueue.invokeLater(new Runnable()
      {
         public void run()
         {
            area.getPanel().deleteModulePanel(name);
         }
      });

   }

   public void addLinkWidget(final LinkName name)
   {
      java.awt.EventQueue.invokeLater(new Runnable()
      {
         public void run()
         {
            //System.out.println("--------- swing link panel creation");
            LinkPanel lp = new LinkPanel(
                    area.getApplication().getEngine().getLink(name),
                    area.getPanel().getModulePanel(name.getOutputModule()).getOutputPanel(name.getOutputPort()),
                    area.getPanel().getModulePanel(name.getInputModule()).getInputPanel(name.getInputPort()));
            //System.out.println(name);
            //System.out.println("@"+Thread.currentThread());
            area.getPanel().addLinkPanel(lp);
         }
      });
   }

   public void deleteLinkWidget(final LinkName name)
   {
      java.awt.EventQueue.invokeLater(new Runnable()
      {
         public void run()
         {
            area.getPanel().deleteLinkPanel(name);
         }
      });
   }

   public void addDataWidget(String dataName, LinkName linkName, LinkName name)
   {
      throw new UnsupportedOperationException("Not yet implemented");
   }

   public Component getDropComponent()
   {
      //.getScenePanel().getViewPort();
      return this.getArea().getPanel().getViewPort();
   }

   public int getDropOffsetX()
   {
      //.getScene().getScenePanel().getScrollOffsetX();
      return this.getArea().getPanel().getScrollOffsetX();

   }

   public int getDropOffsetY()
   {
      return this.getArea().getPanel().getScrollOffsetY();
   }

   public void requestedDeleteSelectedItems()
   {
      Vector<SelectableAreaItem> vec = area.getInput().getSelectedItems();
      if (vec.isEmpty())
         return;

      Vector<Link> links = new Vector<Link>();
      Vector<ModulePanel> modules = new Vector<ModulePanel>();
      for (SelectableAreaItem i : vec)
      {
         if (i instanceof LinkPanel)
            links.add(((LinkPanel) i).getLink());
         if (i instanceof ModulePanel)
            modules.add((ModulePanel) i);
      }
      for (Link link : links)
      {
         this.getArea()
                 .getApplication()
                 .getReceiver().receive(new LinkDeleteCommand(link.getName()));
      }
      for (ModulePanel module : modules)
      {
         this.getArea()
                 .getApplication()
                 .getReceiver().receive(new ModuleDeleteCommand(
                 module.getModule().getName(),
                 module.getModule().getCore().getCoreName(),
                 module.getLocation()));
      }

      //throw new UnsupportedOperationException("Not yet implemented");
   }

   public void renameLink(LinkName oldLinkName, LinkName newLinkName)
   {
      this.getArea().getPanel().updateLinkName(oldLinkName, newLinkName);
   }
}
