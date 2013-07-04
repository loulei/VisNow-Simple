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

package pl.edu.icm.visnow.application.application;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import pl.edu.icm.visnow.engine.core.ModuleCore;
import pl.edu.icm.visnow.engine.commands.Command;
import pl.edu.icm.visnow.engine.commands.HidePortCommand;
import pl.edu.icm.visnow.engine.commands.LibraryAddCommand;
import pl.edu.icm.visnow.engine.commands.LibraryDeleteCommand;
import pl.edu.icm.visnow.engine.commands.LibraryRenameCommand;
import pl.edu.icm.visnow.engine.commands.LinkAddCommand;
import pl.edu.icm.visnow.engine.commands.LinkDeleteCommand;
import pl.edu.icm.visnow.engine.commands.ModuleAddCommand;
import pl.edu.icm.visnow.engine.commands.ModuleDeleteCommand;
import pl.edu.icm.visnow.engine.commands.ModuleRenameCommand;
import pl.edu.icm.visnow.engine.commands.MoveLinkBarCommand;
import pl.edu.icm.visnow.engine.commands.MoveModulesCommand;
import pl.edu.icm.visnow.engine.commands.SelectedModuleCommand;
import pl.edu.icm.visnow.engine.commands.ShowPortCommand;
import pl.edu.icm.visnow.engine.commands.SplitLinkCommand;
import pl.edu.icm.visnow.engine.core.Link;
import pl.edu.icm.visnow.engine.core.LinkName;
import pl.edu.icm.visnow.engine.core.Output;
import pl.edu.icm.visnow.engine.error.Displayer;
import pl.edu.icm.visnow.engine.exception.VNException;
import pl.edu.icm.visnow.engine.main.ModuleBox;
import pl.edu.icm.visnow.lib.types.VNGeometryObject;
import pl.edu.icm.visnow.system.main.VisNow;

/**
 *
 * @author Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class CommandExecutor
{

   private static final boolean profile = false;
   private Application application;

   public Application getApplication()
   {
      return application;
   }

   CommandExecutor(Application application)
   {
      this.application = application;
   }

   //<editor-fold defaultstate="collapsed" desc=" Receive (switch only) ">
   public void execute(Command command)
   {
      application.setChanged();
      switch (command.getType())
      {
      case Command.ADD_LIBRARY:
         addLibrary((LibraryAddCommand) command);
         return;
      case Command.RENAME_LIBRARY:
         renameLibrary((LibraryRenameCommand) command);
         return;
      case Command.DELETE_LIBRARY:
         deleteLibrary((LibraryDeleteCommand) command);
         return;
      case Command.ADD_MODULE:
         addModule((ModuleAddCommand) command);
         return;
      case Command.RENAME_MODULE:
         renameModule((ModuleRenameCommand) command);
         return;
      case Command.DELETE_MODULE:
         selectedModule(new SelectedModuleCommand(Command.UI_FRAME_SELECTED_MODULE, null));
         deleteModule((ModuleDeleteCommand) command);
         return;
      case Command.ADD_LINK:
         addLink((LinkAddCommand) command);
         return;
      case Command.DELETE_LINK:
         deleteLink((LinkDeleteCommand) command);
         return;
      case Command.SPLIT_LINK:
         splitLink((SplitLinkCommand) command);
         return;
      case Command.UI_MOVE_MULTIPLE_MODULES:
         moveModules((MoveModulesCommand) command);
         return;
      case Command.UI_MOVE_LINK_BAR:
         moveLinkBar((MoveLinkBarCommand) command);
         return;
      case Command.UI_SHOW_PORT:
         showPort((ShowPortCommand) command);
         return;
      case Command.UI_HIDE_PORT:
         hidePort((HidePortCommand) command);
         return;
      case Command.UI_SCENE_SELECTED_MODULE:
         throw new UnsupportedOperationException("Deprecated");
      case Command.UI_FRAME_SELECTED_MODULE:
         selectedModule((SelectedModuleCommand) command);
         return;
      }
   }
   //</editor-fold>

   //<editor-fold defaultstate="collapsed" desc=" [Rec] Library ">
   protected void addLibrary(LibraryAddCommand command)
   {
      try
      {
         application.getAccess("Add Library (ACE-0)");
      } catch (InterruptedException ex)
      {
         return;
      }
      application.getLibraries().addLibrary(command.getName(), VisNow.get().getMainLibraries().getLibrary(command.getName()));
      application.getFrames().getExecutor().refreshLibraries();
      application.releaseAccess();
   }

   protected void renameLibrary(LibraryRenameCommand command)
   {
      throw new UnsupportedOperationException("Hubert");
   }

   protected void deleteLibrary(LibraryDeleteCommand command)
   {
      throw new UnsupportedOperationException("Hubert");
   }
   //</editor-fold>

   private void addModuleProfile(String string)
   {
      if (profile)
         System.out.println(
                 Displayer.timestamp() + " "
                 + this.getClass().getName() + ": "
                 + "addModule"
                 + "\n\t"
                 + string);
   }

   //<editor-fold defaultstate="collapsed" desc=" [Rec] Module ">
   protected void addModule(ModuleAddCommand command)
   {
      try
      {
         application.getAccess("Add module (ACE-4)");
      } catch (InterruptedException ex)
      {
         return;
      }
      ModuleCore core;
      try
      {
         core = application.getLibraries().generateCore(command.getCoreName());
      } catch (VNException ex)
      {
         Displayer.display(200907100600L, ex, this, "Cannot add module, unknown exception.");
         return; /* TODO: do something with this exception */
      }

      application.getEngine().getExecutor().addModule(command.getName(), core);
      application.getArea().getInput().addModuleWidget(command.getName(), command.getPosition());
      application.getFrames().getExecutor().refreshModules();
      application.getArea().getInput().selectModule(command.getName());

      LinkName newLinkName = null;
      if(VisNow.get().getMainConfig().isAutoconnectViewer())
          for (Output output : core.getOutputs().getSortedOutputs())
          {
             if (output.getType() == VNGeometryObject.class)
             try
             {
                 HashMap<String, ModuleBox> modules = application.getEngine().getModules();
                 Collection<ModuleBox> mbxs = modules.values();
                 Iterator<ModuleBox> it = mbxs.iterator();
                 ModuleBox mb;
                 int viewerMaxNo = -1;
                 String viewerName = null;
                 while(it.hasNext()) {
                     mb = it.next();
                     if(mb.getCore().getCoreName().getClassName().equals("pl.edu.icm.visnow.lib.basic.viewers.Viewer3D.Viewer3D")
                             || mb.getCore().getCoreName().getClassName().equals("pl.edu.icm.visnow.lib.basic.viewers.Viewer2D.Viewer2D")
                             
                             ) {
                         String name = mb.getName();
                         String no = name.substring(name.lastIndexOf("[")+1, name.lastIndexOf("]"));
                         int n = Integer.parseInt(no);
                         if(n > viewerMaxNo) {
                             viewerMaxNo = n;
                             viewerName = name;
                         }
                     }
                 }
                 
                 if(viewerName != null) {
                     newLinkName = new LinkName(core.getName(), output.getName(), viewerName,"inObject");
                 }
             } catch (Exception e)
             {
             }
          }
      if(VisNow.get().getMainConfig().isAutoconnectOrthoViewer3D())
          for (Output output : core.getOutputs().getSortedOutputs())
          {
             if (output.getType() == VNGeometryObject.class)
             try
             {
                 HashMap<String, ModuleBox> modules = application.getEngine().getModules();
                 Collection<ModuleBox> mbxs = modules.values();
                 Iterator<ModuleBox> it = mbxs.iterator();
                 ModuleBox mb;
                 int viewerMaxNo = -1;
                 String viewerName = null;
                 while(it.hasNext()) {
                     mb = it.next();
                     if(mb.getCore().getCoreName().getClassName().equals("pl.edu.icm.visnow.lib.basic.viewers.MultiViewer3D.Viewer3D")) {
                         String name = mb.getName();
                         String no = name.substring(name.lastIndexOf("[")+1, name.lastIndexOf("]"));
                         int n = Integer.parseInt(no);
                         if(n > viewerMaxNo) {
                             viewerMaxNo = n;
                             viewerName = name;
                         }
                     }
                 }
                 
                 if(viewerName != null) {
                     newLinkName = new LinkName(core.getName(), output.getName(), viewerName,"inObject");
                 }
             } catch (Exception e)
             {
             }
          }
      //application.getFrames().getExecutor().selectModule(command.getName());

      try
      {
         application.getEngine().getModule(command.getName()).getCore().setForceFlag(command.getForceFlag());
         application.getEngine().getModule(command.getName()).getCore().onInitFinished();
      } catch (Exception e)
      {
         Displayer.ddisplay(200909302301L, e, this,
                 "ERROR IN MODULE FUNCTION:\n"
                 + "An error has occured in the function \"onInitFinished\""
                 + "of module \"" + command.getName() + "\".\n"
                 + "Please report this error to the module core developer.");
      }
      application.releaseAccess();
      if (newLinkName != null)
         execute(new LinkAddCommand(newLinkName, true));
   }

   protected void renameModule(ModuleRenameCommand command)
   {
      try
      {
         application.getAccess("Rename module (ACE-5)");
      } catch (InterruptedException ex)
      {
         return;
      }

      ModuleBox mb = application.getEngine().getModule(command.getName());
      for (Link link : mb)
      {
         LinkName oldLinkName = link.getName();
         String in = oldLinkName.getInputModule();
         String out = oldLinkName.getOutputModule();
         if (in.equals(command.getName()))
            in = command.getNewName();
         if (out.equals(command.getName()))
            out = command.getNewName();
         LinkName newLinkName = new LinkName(
                 out,
                 oldLinkName.getOutputPort(),
                 in,
                 oldLinkName.getInputPort());


         application.getEngine().getExecutor().renameLink(oldLinkName, newLinkName);
         application.getArea().getInput().renameLink(oldLinkName, newLinkName);
      }

      application.getEngine().getExecutor().renameModule(command.getName(), command.getNewName());
      application.getFrames().getExecutor().refreshModules();
      application.getArea().getInput().renameModuleWidget(command.getName(), command.getNewName());
      application.getArea().getInput().selectModule(command.getNewName());
      //application.getFrames().getExecutor().selectModule(command.getNewName());
      application.releaseAccess();
   }

   protected void deleteModule(ModuleDeleteCommand command)
   {
      try
      {
         application.getAccess("Delete module (ACE-6)");
      } catch (InterruptedException ex)
      {
         return;
      }
      application.getEngine().getExecutor().deleteModule(command.getName());
      application.getFrames().getExecutor().refreshModules();
      application.getFrames().getExecutor().selectModule(null);
      application.getArea().getInput().deleteModuleWidget(command.getName());
      application.releaseAccess();
   }
   //</editor-fold>

   //<editor-fold defaultstate="collapsed" desc=" [Rec] Link ">
   protected void addLink(LinkAddCommand command)
   {
      //System.out.println("ADDING LINK!! Command is active: "+command.isActive());
      try
      {
         application.getAccess("Add Link (ACE-1)");
      } catch (InterruptedException ex)
      {
         return;
      }
      //System.out.println("ADDING LINK TO ENGINE");
      application.getEngine().getExecutor().addLink(command.getName(), command.isActive());
      //System.out.println("ADDING LINK WIDGET");
      application.getArea().getInput().addLinkWidget(command.getName());
      if (command.isActive())
      {
         application.getEngine().getModule(command.getName().getInputModule()).startAction();
      }
      //System.out.println("ADDED!!\n\n");
      application.releaseAccess();
   }

   protected void deleteLink(LinkDeleteCommand command)
   {
      try
      {
         application.getAccess("Delete Link (ACE-2)");
      } catch (InterruptedException ex)
      {
         return;
      }
      application.getEngine().getExecutor().deleteLink(command.getName());
      application.getArea().getInput().deleteLinkWidget(command.getName());
      application.releaseAccess();
   }
   //</editor-fold>

   //<editor-fold defaultstate="collapsed" desc=" [Rec] UI ">
   protected void moveModules(MoveModulesCommand command)
   {
   }

   protected void showPort(ShowPortCommand command)
   {
   }

   protected void hidePort(HidePortCommand command)
   {
   }

   protected void moveLinkBar(MoveLinkBarCommand command)
   {
   }

   private void selectedModule(SelectedModuleCommand command)
   {
      application.getArea().getInput().selectModule(command.getName());
   }

   //</editor-fold>
   private void splitLink(SplitLinkCommand command)
   {
      try
      {
         application.getAccess("Split Link (ACE-3)");
      } catch (InterruptedException ex)
      {
         return;
      }

      Link old = application.getEngine().getLink(command.getLinkName());

      String dataName = ""
              + old.getOutput().getType().getSimpleName()
              + "[" + application.getEngine().nextModuleNumber() + "]";

      application.getEngine().getExecutor().splitLink(command.getLinkName(), dataName);
      application.getArea().getInput().addDataWidget(dataName, command.getLinkName(), old.getName());

      application.getFrames().getExecutor().refreshModules();
      application.getArea().getInput().selectModule(dataName);
      application.getFrames().getExecutor().selectModule(dataName);

      application.releaseAccess();
   }
}
