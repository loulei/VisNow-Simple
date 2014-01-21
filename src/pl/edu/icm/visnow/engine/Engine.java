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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.concurrent.Semaphore;
import pl.edu.icm.visnow.application.application.Application;
import pl.edu.icm.visnow.engine.core.LinkName;
import pl.edu.icm.visnow.engine.core.Link;
import pl.edu.icm.visnow.engine.element.Element;
import pl.edu.icm.visnow.engine.element.ElementKiller;
import pl.edu.icm.visnow.engine.element.ElementState;
import pl.edu.icm.visnow.engine.error.Displayer;
import pl.edu.icm.visnow.engine.exception.VNApplicationNetLoopException;
import pl.edu.icm.visnow.engine.exception.VNSystemEngineException;
import pl.edu.icm.visnow.engine.logging.VNLogger;
import pl.edu.icm.visnow.engine.core.Input;
import pl.edu.icm.visnow.engine.main.ModuleBox;
import pl.edu.icm.visnow.engine.main.ModuleElement;
import pl.edu.icm.visnow.engine.main.ModuleSaturation;
import pl.edu.icm.visnow.engine.messages.Message;



/**
 *
 * @author Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class Engine extends Element implements ElementKiller {

    private static boolean debug = true;

    private HashMap<String, ModuleBox> modules;
    private HashMap<LinkName, Link> links;


    private boolean killed = false;
    public void kill() {
        killed = true;
        //VNLogger.log("KILLED!", this, "engine");
        //getApplication().stopProgress(); //TODO - progressBar
        getApplication().releaseAccess();
    }

    public void startModules() {
        for(ModuleBox module: this.getModules().values()) {
            module.run();
        }
    }

    public boolean isKilled() {
        return killed;
    }

    public boolean getPermission(ModuleElement element) {
        return true;
    }

    private int moduleNumber;

    public int nextModuleNumber() {
        ++moduleNumber;
        return moduleNumber-1;
    }

    public HashMap<String, ModuleBox> getModules() {return modules;}
    public HashMap<LinkName, Link> getLinks() {return links;}

    public ModuleBox getModule(String name) {return modules.get(name);}
    public Link getLink(LinkName name) {return links.get(name);}

    private EngineCommandExecutor executor;
    private Application application;
     
    public EngineCommandExecutor getExecutor() {return executor;}
    public Application getApplication() {return application;}

    public Engine(Application application) {
        super("Engine");
        this.killer = this;
        this.modules = new HashMap<String, ModuleBox>();
        this.links = new HashMap<LinkName, Link>();
        this.executor = new EngineCommandExecutor(this);
        this.application = application;
        this.moduleNumber = 1;
    }

//    @Override
//    public LinkedBlockingQueue<Message> getQueue() {
//        VNLogger.log("MESSAGE PUT", this, "engine");
//        return super.getQueue();
//    }

    public boolean isNewActionPossible() {return true;}
    public boolean isModuleActionPossible() {return true;}

    @Override
    protected void onNotifyMessage(Message message) throws VNSystemEngineException {}

    @Override
    protected void onActionMessage(Message message) throws VNSystemEngineException {}

    @Override
    protected void onInactionMessage(Message message) throws VNSystemEngineException {}

    @Override
    protected void onKillMessage() throws VNSystemEngineException {}

    //private Message lastMessage;
    //protected Message getLastMessage() {
    //    return lastMessage;
    //}

    protected void gotException(VNSystemEngineException ex) {
        Displayer.display(200907100945L, ex, this, "Exception in engine queue.");
    }


    //private Semaphore nonsens=new Semaphore(1, true);

    @Override
    public boolean nextMessage() throws InterruptedException, VNSystemEngineException {
        //if(debug) System.out.println("................ on "+getQueue().toString());
        

      //  nonsens.acquire();
        final Message lastMessage = takeFromQueue();//getQueue().take();
       // nonsens.release();
        VNLogger.debugMessage(this, true, lastMessage);
        Runnable r = new Runnable() {
            public void run() {
                //System.out.println("ENGINE GOT MESSAGE: ["+lastMessage+"]");
                switch(lastMessage.getType()) {
                    case Message.KILL:
                        try {
                            onKillMessage();
                        } catch (VNSystemEngineException ex) {
                            gotException(ex);
                        }
                        break;
                    case Message.NOTIFY:
                        try {
                            onNotifyMessage(lastMessage);
                        } catch (VNSystemEngineException ex) {
                            gotException(ex);
                        }
                        break;
                    case Message.READY:
                        try {
                            onReadyMessage(lastMessage);
                        } catch (VNSystemEngineException ex) {
                            gotException(ex);
                        }
                        break;
                    case Message.ACTION:
                        try {
                            onActionMessage(lastMessage);
                        } catch (VNSystemEngineException ex) {
                            gotException(ex);
                        }
                        break;
                    case Message.INACTION:
                        try {
                            onInactionMessage(lastMessage);
                        } catch (VNSystemEngineException ex) {
                            gotException(ex);
                        }
                        break;
                    case Message.DONE:
                        try {
                            onDoneMessage(lastMessage);
                        } catch (VNSystemEngineException ex) {
                            gotException(ex);
                        }
                        break;
                    default:
                        try {
                            onOtherMessage(lastMessage);
                        } catch (VNSystemEngineException ex) {
                            gotException(ex);
                        }
                        break;
        }
            }
        };
        new Thread(r, "VN-Engine-message").start();
        return true;
    }

    @Override
    protected void onOtherMessage(Message message) throws VNSystemEngineException {
        switch(message.getType()) {
            case Message.START_ACTION:
                onStartActionMessage(message);
                return;
        }

    }

    private Semaphore lockup=new Semaphore(1, true);

    protected void onStartActionMessage(Message message) {
        try {
            VNLogger.debugEngineLock("SLEEPING...");
            lockup.acquire();
            VNLogger.debugEngineLock("ACQUIRED!");
        } catch(InterruptedException e) {
            return;
        }
        
        try {
            getApplication().getAccess("Start Action (Engine-0)");
            VNLogger.debugFlow(true, message.getSender());//.log("START! from module ["+message.getSender()+"]\n\n", this, "engine");
            killed =false;
//            getApplication().getScene().getScenePanel().getProgress().init(); //TODO - progressBar
            if(message.getSender() instanceof ModuleElement) {
                try {
                    ((ModuleElement)message.getSender()).getModuleBox().getCore().onWaveStarting();
                } catch(Exception e) {
                     Displayer.ddisplay(42, e, this,
                        "An error occured in function \"onWaveStarting\" "+
                        "of module \""+message.getSender().getName()+"\".\n"+
                        "Please report this exception to the module core developer.\n"+
                        "The application flow will be terminated."
                        );
                     killed = true;
                     getApplication().releaseAccess();
                     return;
                }
            }
            message.getSender().putToQueue(new Message(this, Message.NOTIFY));
            //if (debug) {
            //    System.out.println("sent message to " + message.getSender());
            //}
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onReadyMessage(Message message) throws VNSystemEngineException {
        try {
            //      getApplication().startProgress(); //TODO - progressBar
            message.getSender().putToQueue(new Message(this, Message.ACTION));
        } catch (InterruptedException ex) {
            throw new VNSystemEngineException(
                        200910260000L,
                        "Ready message propagation interrupted",
                        ex,
                        this,
                        Thread.currentThread()
                        );
        }
    }

    @Override
    protected void onDoneMessage(Message message) throws VNSystemEngineException {
        //getApplication().stopProgress(); //TODO - progressBar
        VNLogger.debugFlow(false, message.getSender());//.log("FINISH!\n\n", this, "engine");
        if(message.getSender() instanceof ModuleElement) {
            try {
                ((ModuleElement)message.getSender()).getModuleBox().getCore().onWaveFinalizing();
            } catch(Exception e) {
                 Displayer.ddisplay(42, e, this,
                    "An error occured in function \"onWaveFinalizing\" "+
                    "of module \""+message.getSender().getName()+"\".\n"+
                    "Please report this exception to the module core developer.\n"+
                    "The application flow will be terminated."
                    );
                 killed = true;
                 getApplication().releaseAccess();
                 return;
            }
        }
        getApplication().releaseAccess();
        lockup.release();
        VNLogger.debugEngineLock("RELEASE");
        // TODO: finish action
        
        engineSaturationCheck();
    }

    private void onFinishActionMessage(Message message) {

    }


    public void unkill() {
        killed = false;
    }




   public Collection<ModuleBox> getTopologicalModules() throws VNApplicationNetLoopException
   {
      //if(true) return this.getModules().values();

      Vector<ModuleBox> ret = new Vector<ModuleBox>();
      HashMap<ModuleBox, ModuleNode> nodes = new HashMap<ModuleBox, ModuleNode>();
      for (ModuleBox m : this.getModules().values())
         nodes.put(m, new ModuleNode(m));
      for (Map.Entry<ModuleBox, ModuleNode> en : nodes.entrySet())
      {
         Iterator<Link> i = en.getKey().iterator(false, true);
         while (i.hasNext())
            en.getValue().edges.add(nodes.get(i.next().getInput().getModuleBox()));
      }

      Vector<ModuleNode> ready = new Vector<ModuleNode>();
      for (ModuleNode mn : nodes.values())
         if (mn.inValue == 0)
            ready.add(mn);
      while (!ready.isEmpty())
      {
         ModuleNode next = ready.remove(ready.size() - 1);
         ret.add(next.node);
         for (ModuleNode mn : next.edges)
         {
            mn.inValue--;
            if (mn.inValue == 0)
               ready.add(mn);
         }
      }
      if (ret.size() < nodes.size())
         throw new VNApplicationNetLoopException();

      return ret;
   }

    void updateLinkName(LinkName old, LinkName ln) {
        Link l = links.get(old);
        links.remove(old);
        links.put(ln, l);
    }

    void updateModuleName(String name, String newName) {
        ModuleBox mb = modules.get(name);
        modules.remove(name);
        modules.put(newName, mb);
    }

    public Thread doTheMainReset(Thread oldThread) {

        this.setElementState(ElementState.passive);
        this.clearQueue();
        oldThread.interrupt();

        for(ModuleBox module: getModules().values()) {
            module.doTheMainResetKillAll();
        }

        killed = false;
        if(lockup.availablePermits()<=0)
            lockup.release();
        
        for(ModuleBox module: getModules().values()) {
            module.doTheMainResetWakeUp();
        }
        

        return new Thread(this, "VN-Engine-new");
    }

    public void correctModuleCount(int c) {
        if(moduleNumber<=c) moduleNumber = c+1;
    }

    public void engineSaturationCheck() {
        Iterator<Entry<String,ModuleBox>> it = modules.entrySet().iterator();
        boolean wrongData = false;
        boolean noData = false;
        while(it.hasNext()) {
            Entry<String,ModuleBox> entry = it.next();
            ModuleSaturation sat = entry.getValue().getElement().getSaturation();
            if(!wrongData && sat == ModuleSaturation.wrongData) {
                wrongData = true;
            }            
            if(!noData && sat == ModuleSaturation.noData) {
                noData = true;
            }            
        }
        
        if(!wrongData && !noData) {
            application.setStatus(Application.ApplicationStatus.OK);
        } else if(wrongData) {
            application.setStatus(Application.ApplicationStatus.ERROR);
        } else if(noData) {
            application.setStatus(Application.ApplicationStatus.WARNING);
        }
        
    }



}
class ModuleNode {
    protected ModuleBox node;
    protected int inValue;
    public ModuleNode(ModuleBox box) {
        this.node = box;
        int i = 0;
        for(Input inp: box.getInputs().getInputs().values()) {
            i+= inp.getLinks().size();
        }
        this.inValue = i;
        this.edges = new Vector<ModuleNode>();
    }
    protected Vector<ModuleNode> edges;
}

