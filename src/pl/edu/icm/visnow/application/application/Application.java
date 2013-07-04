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

import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.Semaphore;
import org.apache.log4j.Logger;
import pl.edu.icm.visnow.application.area.Area;
import pl.edu.icm.visnow.application.frames.ApplicationFrame2;
import pl.edu.icm.visnow.application.frames.Frames;
import pl.edu.icm.visnow.application.io.VNWriter;
import pl.edu.icm.visnow.application.io.XMLWriter;
import pl.edu.icm.visnow.application.libraries.Libraries;
import pl.edu.icm.visnow.engine.Engine;
import pl.edu.icm.visnow.engine.commands.ModuleAddCommand;
import pl.edu.icm.visnow.engine.commands.ModuleDeleteCommand;
import pl.edu.icm.visnow.engine.core.CoreName;
import pl.edu.icm.visnow.engine.logging.VNLogger;
import pl.edu.icm.visnow.engine.main.ModuleBox;

/**
 *
 * @author Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class Application
{

   private Libraries libraries;
   private Engine engine;
   private History history;
   private Frames frames;
   private Area area;
   private RequestReceiver receiver;
   private CommandExecutor executor;
   private Semaphore lockup;
   private String title;
   private String filePath;
   static Logger logger = Logger.getLogger(Application.class);

   protected void setFilePath(String filePath)
   {
      this.filePath = filePath;
   }

   public boolean tryGetAccess(String reason)
   {
      boolean ret = lockup.tryAcquire();
      if (ret)
         logger.debug("Acquired access @" + Thread.currentThread() + " for: [" + reason + "]");
      if (ret)
         getArea().getInput().showLock(true);//getScene().getScenePanel().getLockManager().setLocked(true);
      return ret;
   }

   public boolean getAccess(String reason) throws InterruptedException
   {
      VNLogger.debugAppLock("SLEEPING...");
      logger.debug("Getting access @" + Thread.currentThread() + " for: [" + reason + "]");
      lockup.acquire();
      logger.debug("acquired access @" + Thread.currentThread() + " for: [" + reason + "]");
      VNLogger.debugAppLock("ACQUIRED");
      getArea().getInput().showLock(true);//getScene().getScenePanel().getLockManager().setLocked(true);
      return true;
   }

   public void releaseAccess()
   {
      if (lockup.availablePermits() == 0)
         lockup.release();
      logger.debug("released access @" + Thread.currentThread());
      getArea().getInput().showLock(false);
      //getScene().getScenePanel().getLockManager().setLocked(false);
   }

   //<editor-fold defaultstate="collapsed" desc=" [VAR] GETTERS ">
   public Libraries getLibraries()
   {
      return libraries;
   }

   public Engine getEngine()
   {
      return engine;
   }

   protected History getHistory()
   {
      return history;
   }

   public Area getArea()
   {
      return area;
   }

   public CommandExecutor getExecutor()
   {
      return executor;
   }

   public Frames getFrames()
   {
      return frames;
   }
   //</editor-fold>

   public ApplicationFrame2 getApplicationFrame()
   {
      return frames.getApplicationFrame();
   }

   public RequestReceiver getReceiver()
   {
      return receiver;
   }

   public Semaphore getLockup()
   {
      return lockup;
   }

   public String getTitle()
   {
      return title;
   }

   public void setTitle(String title)
   {
      this.title = title;
   }

   public String getFilePath()
   {
      return filePath;
   }
   private Thread engineThread;

   public Application(String title)
   {
      this.libraries = new Libraries(this);
      this.engine = new Engine(this);
      this.history = new History(this);
      this.area = new Area(this);
      this.frames = new Frames(this);


      this.receiver = new RequestReceiver(this);
      this.executor = new CommandExecutor(this);


      this.lockup = new Semaphore(1, true);
      this.title = title;
      this.filePath = null;

      engineThread = new Thread(getEngine(), "VN-Engine");
      engineThread.start();
   }

   public Application(String title, File file)
   {
      this(title);
      this.filePath = file.getPath();
   }

   public boolean save()
   {
      return saveAs(new File(filePath));
   }
   private boolean changed = false;

   public void setChanged()
   {
      changed = true;
   }

   private void setNoChanged()
   {
      changed = false;
   }

   public boolean hasChanged()
   {
      return changed;
   }

   public boolean saveAs(File file)
   {
      try
      {
         this.getAccess("Saving application (Application-0)");
      } catch (InterruptedException e)
      {
         e.printStackTrace();
         return false;
      }
      boolean ret = XMLWriter.writeApplication(this, file);
      if (ret)
      {
         this.filePath = file.getPath();
         setNoChanged();
      }
      this.releaseAccess();
      return ret;
   }

   public void clearHistory()
   {
      history.clear();
   }

   public void deleteSelected()
   {
      area.getInput().requestedDeleteSelectedItems();
   }

    public void deleteAllModules() {
        HashMap<String, ModuleBox> tmp = this.getEngine().getModules();
        ArrayList<ModuleBox> modules = new ArrayList(tmp.values());
        ModuleBox module;
        for (int i = 0; i < modules.size(); i++) {
            module = modules.get(i);
            this.getReceiver().receive(new ModuleDeleteCommand(
                    module.getName(),
                    module.getCore().getCoreName(),
                    null));
        }
    }
   
   public void doTheMainReset()
   {
      engineThread = engine.doTheMainReset(engineThread);
      this.releaseAccess();
      engineThread.start();
   }

   public boolean betaSaveAs(File file)
   {
      try
      {
         this.getAccess("Saving application (Application-1)");
      } catch (InterruptedException e)
      {
         e.printStackTrace();
         return false;
      }
      boolean ret = VNWriter.writeApplication(this, file);
      if (ret)
      {
         this.filePath = file.getPath();
         setNoChanged();
      }
      this.releaseAccess();
      return ret;
   }

   public void correctModuleCount(int c)
   {
      this.getEngine().correctModuleCount(c);
   }
   private Point initPoint = new Point(200, 460);
   int initCounter = 0;

   public void addInitViewerByName(String name, String module)
   {
      ModuleAddCommand viewerAddCommand =
              new ModuleAddCommand(name + "[" + initCounter + "]",
              new CoreName("internal", null, module),
              initPoint);
      this.executor.execute(viewerAddCommand);
      initPoint = new Point(initPoint.x - 80, initPoint.y - 70);
      initCounter++;
   }

   public void addModuleByName(String name, String module, Point pt)
   {
      addModuleByName(name, module, pt, false);
   }

   public void addModuleByName(String name, String module, Point pt, boolean forceFlag)
   {
      ModuleAddCommand moduleAddCommand =
              new ModuleAddCommand(
              name,
              new CoreName("internal", null, module),
              pt,
              forceFlag);
      this.executor.execute(moduleAddCommand);
   }
   private Point newPoint = new Point(20, 20);
   int newCounter = 0;

   public void addModuleByName(String name, String module, boolean forceFlag)
   {
      addModuleByName(name, module, newPoint, forceFlag);
      newPoint = new Point(newPoint.x + 50, newPoint.y + 50);
      newCounter++;
      if (newCounter == 5)
      {
         newCounter = 0;
         newPoint = new Point(20, newPoint.y - 50);
      }
   }
}
