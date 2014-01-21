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

package pl.edu.icm.visnow.engine;

import org.apache.log4j.Logger;
import pl.edu.icm.visnow.engine.core.LinkName;
import pl.edu.icm.visnow.engine.core.ModuleCore;
import pl.edu.icm.visnow.engine.core.Link;
import pl.edu.icm.visnow.engine.error.Displayer;
import pl.edu.icm.visnow.engine.main.DataModule;
import pl.edu.icm.visnow.engine.main.ModuleBox;

/**
 *
 * @author Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class EngineCommandExecutor
{
   private static final Logger LOGGER = Logger.getLogger(EngineCommandExecutor.class);
    
   private static boolean debug = false;
   private Engine engine;

   public Engine getEngine()
   {
      return engine;
   }

   public boolean addLink(LinkName name, boolean active)
   {
      if(getEngine().getLink(name) != null) {
          return false;
      }
       
      ModuleBox receivingModule = getEngine().getModule(name.getInputModule());
      Link link = new Link(
              getEngine().getModule(name.getOutputModule()).getOutput(name.getOutputPort()),
                                    receivingModule.getInput(name.getInputPort()));      
      getEngine().getLinks().put(name, link);
      return true;
   }

   public void addModule(String name, ModuleCore core)
   {
      core.setApplication(this.getEngine().getApplication());
      ModuleBox mb = new ModuleBox(getEngine(), name, core);
      getEngine().getModules().put(name, mb);
      mb.run();
   }

   public boolean deleteLink(LinkName name)
   {
       LOGGER.debug("Link name: " + name);
      if (debug)
         System.out.println(name.toString());
      Link link = getEngine().getLink(name);
      link.getInput().removeLink(link);
      link.getOutput().removeLink(link);
      getEngine().getLinks().remove(name);
      return true;
   }

   public void deleteModule(String name)
   {
      try
      {
         getEngine().getModule(name).getCore().onDelete();
      } catch (Exception e)
      {
         Displayer.ddisplay(200909302300L, e, this,
                 "ERROR IN MODULE FUNCTION:\n"
                 + "An error has occured in the function \"onDelete\""
                 + "of module \"" + name + "\".\n"
                 + "Please report this error to the module core developer.");
      }
      getEngine().getModules().remove(name);
   }

   public EngineCommandExecutor(Engine engine)
   {
      this.engine = engine;
   }

   public void renameModule(String name, String newName)
   {
      ModuleBox mb = this.engine.getModule(name);
//        for(Link link: mb) {
//            LinkName old = link.getName();
//            String in = old.getInputModule();
//            String out = old.getOutputModule();
//            if(in.equals(name)) in = newName;
//            if(out.equals(name)) out = newName;
//            LinkName ln = new LinkName(
//                    out,
//                    old.getOutputPort(),
//                    in,
//                    old.getInputPort()
//                    );
//            link.updateName(ln);
//            engine.updateLinkName(old, ln);
//        }
      mb.updateName(newName);
      engine.updateModuleName(name, newName);
      // also rename links
   }

   public void splitLink(LinkName linkName, String name)
   {

      DataModule dm = new DataModule(
              getEngine(),
              name,
              getEngine().getLink(linkName).getOutput().getData());
      getEngine().getModules().put(name, dm);


      Link link = getEngine().getLink(linkName);
      link.splitToOutput(dm.getOutput());

      getEngine().getLinks().remove(linkName);
      //getEngine().getModule(linkName.getOutputModule()).splitRemoveLink(linkName);
      getEngine().getLinks().put(link.getName(), link);
      //nie tworzymy nowego watku!!

   }

   public void renameLink(LinkName oldLinkName, LinkName newLinkName)
   {
      Link link = getEngine().getLink(oldLinkName);
      link.updateName(newLinkName);
      engine.updateLinkName(oldLinkName, newLinkName);
   }
}
