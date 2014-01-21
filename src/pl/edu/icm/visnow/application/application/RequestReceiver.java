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


import java.util.Vector;
import pl.edu.icm.visnow.engine.commands.BlockCommand;
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


/**
 *
 * @author Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class RequestReceiver {

    protected Application application;

    
    RequestReceiver(Application application) {
        this.application = application;
    }

    //<editor-fold defaultstate="collapsed" desc=" Receive (switch only) ">
    public void receive(Command command) {
        switch(command.getType()) {
            //case Command.BLOCK:
            //    beginGroup((BeginCommand) command);
            //    return;
            //case Command.END:
            //    endGroup((EndCommand) command);
            //    return;
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
                sceneSelectedModule((SelectedModuleCommand) command);
                return;
            case Command.UI_FRAME_SELECTED_MODULE:
                frameSelectedModule((SelectedModuleCommand) command);
                return;
            default:
                /* TODO: exception */
        }
    }
    //</editor-fold>

    protected void defaultCommand(Command command) {
        application.getExecutor().execute(command);
        application.getHistory().pushToUndo(command);
    }



    //<editor-fold defaultstate="collapsed" desc=" [Rec] Library ">
    protected void addLibrary(LibraryAddCommand command) {
        defaultCommand(command);
    }

    protected void renameLibrary(LibraryRenameCommand command) {
        throw new UnsupportedOperationException("Hubert");
    }

    protected void deleteLibrary(LibraryDeleteCommand command) {
        /* TODO: prompt dla uzytkownika */
        defaultCommand(command);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" [Rec] Module ">
    protected void addModule(ModuleAddCommand command) {
        defaultCommand(command);
    }

    protected void renameModule(ModuleRenameCommand command) {
        defaultCommand(command);
    }

    protected void deleteModule(ModuleDeleteCommand command) {
        BlockCommand block = new BlockCommand("Delete module ["+command.getName()+"]");
        Vector<Link> links = new Vector<Link>();
        for(Link link: application.getEngine().getModule(command.getName()))
            links.add(link);
        for(Link link: links) {
            LinkDeleteCommand ldc = new LinkDeleteCommand(link.getName());
            application.getExecutor().execute(ldc);
            block.addCommand(ldc);
        }
        application.getExecutor().execute(command);
        block.addCommand(command);
        application.getHistory().pushToUndo(block);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" [Rec] Link ">
    protected void addLink(LinkAddCommand command) {
        defaultCommand(command);
    }

    protected void deleteLink(LinkDeleteCommand command) {
        defaultCommand(command);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" [Rec] UI ">
    protected void moveModules(MoveModulesCommand command) {
        defaultCommand(command);
    }
    
    protected void showPort(ShowPortCommand command) {
        defaultCommand(command);
    }
    
    protected void hidePort(HidePortCommand command) {
        if(application
                .getEngine()
                .getModule(command.getModuleName())
                .getPort(command.isInput(), command.getPortName())
                .isLinked()
                )
        {
            return; /* TODO: zabezpieczenie przed ta sytuacja? monit w tym miejscu? */
        }
        defaultCommand(command);
    }

    protected void moveLinkBar(MoveLinkBarCommand command) {
        throw new UnsupportedOperationException("Hubert");
    }

    private void frameSelectedModule(SelectedModuleCommand selectedModuleCommand) {
        application.getExecutor().execute(selectedModuleCommand);
    }

    private void sceneSelectedModule(SelectedModuleCommand selectedModuleCommand) {
        application.getExecutor().execute(selectedModuleCommand);
    }

    //</editor-fold>


    private void splitLink(SplitLinkCommand command) {
        application.getExecutor().execute(command);
        application.getHistory().clear();//TODO: add to history / redo implementation
    }
}
