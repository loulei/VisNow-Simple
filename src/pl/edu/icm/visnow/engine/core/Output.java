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

package pl.edu.icm.visnow.engine.core;

import java.util.Vector;
import pl.edu.icm.visnow.engine.element.ElementState;
import pl.edu.icm.visnow.engine.error.Displayer;
import pl.edu.icm.visnow.engine.exception.VNSystemEngineException;
import pl.edu.icm.visnow.engine.exception.VNSystemEngineStateException;
import pl.edu.icm.visnow.engine.main.ModuleBox;
import pl.edu.icm.visnow.engine.main.OutputSaturation;
import pl.edu.icm.visnow.engine.main.Port;
import pl.edu.icm.visnow.engine.messages.Message;
import pl.edu.icm.visnow.lib.types.VNDataSchema;


/**
 *
 * @author Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class Output extends Port {

    protected OutputSaturation saturation;
    public OutputSaturation getOutputSaturation() {
        return saturation;
    }
    private void setOutputSaturation(OutputSaturation saturation) {
        this.saturation=saturation;
        this.fireElementSaturationListeners();
    }

    public void checkSaturation() {
        //System.out.println("out: check sat");
        if(data.getValue()==null)
            this.setOutputSaturation(OutputSaturation.noData);
        else
            this.setOutputSaturation(OutputSaturation.ok);
    }

    protected OutputEgg egg;
    public boolean isInput() {return false;}
    public Class getType() {return egg.getType();}
    public String getDescription() {return egg.getDescription();}   

    protected VNData data;

    public VNData getData() {return data;}
    public Object getValue() {return data.getValue();}

    public void addLink(Link link) {
        this.links.add(link);
        //if(this.getModuleBox().getCore()==null) return;
        try {
            this.getModuleBox().getCore().onOutputAttach(link);
        } catch(Exception e) {
            Displayer.ddisplay(200909302300L, e, this,
                    "ERROR IN MODULE FUNCTION:\n"+
                    "An error has occured in the function \"onOutputAttach\""+
                    "of module \""+name+"\".\n"+
                    "Please report this error to the module core developer."
                    );
        }
    }


    public boolean removeLink(Link link) {
        this.links.remove(link);
        try {
            this.getModuleBox().getCore().onOutputDetach(link);
        } catch(Exception e) {
            Displayer.ddisplay(200909302300L, e, this,
                    "ERROR IN MODULE FUNCTION:\n"+
                    "An error has occured in the function \"onOutputDetach\""+
                    "of module \""+name+"\".\n"+
                    "Please report this error to the module core developer."
                    );
        }
        return true;
    }
    

    public boolean setValue(Object o) {
        //System.out.println("OUT SET VAL");
        return this.getData().setValue(o);
    }

    public Output(OutputEgg egg) {//, ModuleBox module) {
        super(egg.getName());//, module);
        this.egg = egg;
        this.links = new Vector<Link>();
        this.saturation = OutputSaturation.noData;
        //this.data = new VNData(module, this, this.getType());
    }

    @Override
    public void setModuleBox(ModuleBox module) {
        super.setModuleBox(module);
        this.data = new VNData(module, this, this.getType());
        this.killer = module.getEngine();
        checkSaturation();
    }
    public void setModuleBox(ModuleBoxFace module) {
        this.setModuleBox((ModuleBox)module);
    }

    

    //public ModuleBox getModuleBoxEgg() {
    //    throw new Hubert();
    //}

    //<editor-fold defaultstate="collapsed" desc=" NRAD message ">
    private int waiting;

    @Override
    protected void onNotifyMessage(Message message) throws VNSystemEngineStateException, VNSystemEngineException {
//        this.getModuleBox().getEngine().writeFlow(this+" : notify");
        if(this.getElementState() != ElementState.passive)
            throw new VNSystemEngineStateException(
                    200903741414L,
                    "Wrong state of output: notify @ "+getElementState(),
                    null,
                    this,
                    Thread.currentThread()
                    );
        this.setElementState(ElementState.notifying);
        waiting = links.size();
        for(Link link: links) {
            try{
                link.getInput().putToQueue(new Message(this, Message.NOTIFY));
            } catch (InterruptedException ex) {
                throw new VNSystemEngineException(
                        200910261505L,
                        "Notify message propagation interrupted",
                        ex,
                        this,
                        Thread.currentThread()
                        );
            }
        }
        if(waiting == 0)
            setReady();
    }

    @Override
    protected void onReadyMessage(Message message) throws VNSystemEngineStateException, VNSystemEngineException {
//        this.getModuleBox().getEngine().writeFlow(this+" : ready");
        if(this.getElementState() != ElementState.notifying)
            throw new VNSystemEngineStateException(
                    200903741415L,
                    "Wrong state of output: ready @ "+getElementState(),
                    null,
                    this,
                    Thread.currentThread()
                    );
        --waiting;
        if(waiting == 0)
            setReady();
    }

    protected void setReady() throws VNSystemEngineException {
        this.setElementState(ElementState.ready);
        try {
            this.getModuleBox().getElement().putToQueue(new Message(this, Message.READY));
        } catch (InterruptedException ex) {
            throw new VNSystemEngineException(
                        200910261500L,
                        "Ready message propagation interrupted",
                        ex,
                        this,
                        Thread.currentThread()
                        );
        }
    }

    @Override
    protected void onActionMessage(Message message) throws VNSystemEngineStateException, VNSystemEngineException {
//        this.getModuleBox().getEngine().writeFlow(this+" : action");
        if(getElementState() != ElementState.ready)
            throw new VNSystemEngineStateException(
                    200903741416L,
                    "Wrong state of output: action @ "+getElementState(),
                    null,
                    this,
                    Thread.currentThread()
                    );
        this.setElementState(ElementState.propagating);
        waiting = links.size();
        for(Link link: links) {
            try {
                link.getInput().putToQueue(new Message(this, Message.ACTION));
            } catch (InterruptedException ex) {
                throw new VNSystemEngineException(
                        200910261506L,
                        "Action message propagation interrupted",
                        ex,
                        this,
                        Thread.currentThread()
                        );
            }
        }
        if(waiting == 0)
            setDone();
    }

    @Override
    protected void onInactionMessage(Message message) throws VNSystemEngineStateException, VNSystemEngineException {
        //this.getModuleBox().getEngine().writeFlow(this+" : inaction");
        if(getElementState() != ElementState.ready)
            throw new VNSystemEngineStateException(
                    200903741417L,
                    "Wrong state of output: inaction @ "+getElementState(),
                    null,
                    this,
                    Thread.currentThread()
                    );
        this.setElementState(ElementState.propagating);
        waiting = links.size();
        for(Link link: links) {
            try {
                link.getInput().putToQueue(new Message(this, Message.INACTION));
            } catch (InterruptedException ex) {
                throw new VNSystemEngineException(
                        200910261400L,
                        "Inaction message propagation interrupted",
                        ex,
                        this,
                        Thread.currentThread()
                        );
            }
        }
        if(waiting == 0)
            setDone();
    }

    @Override
    protected void onDoneMessage(Message message) throws VNSystemEngineStateException, VNSystemEngineException {
//        this.getModuleBox().getEngine().writeFlow(this+" : done");
        if(getElementState() != ElementState.propagating)
            throw new VNSystemEngineStateException(
                    200903741418L,
                    "Wrong state of output: done @ "+getElementState(),
                    null,
                    this,
                    Thread.currentThread()
                    );
        --waiting;
        if(waiting == 0)
            setDone();
    }

    protected void setDone() throws VNSystemEngineException {
        this.setElementState(ElementState.done);
        try {
            this.getModuleBox().getElement().putToQueue(new Message(this, Message.DONE));
        } catch (InterruptedException ex) {
            throw new VNSystemEngineException(
                        200910261401L,
                        "Done message propagation interrupted",
                        ex,
                        this,
                        Thread.currentThread()
                        );
        }
        this.setElementState(ElementState.passive);
    }
    //</editor-fold>

    @Override
    protected void onKillMessage() {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    @Override
    public String toString() {
        return getModuleBox().getName() + ".out."+getName();
    }

    
//    @Override
//    public void restart() {
//        super.restart();
//        this.getData().restart();
//    }

    public VNDataSchema[] getVNDataSchemas() {
        return egg.getVNDataSchemas();
    }

    @Override
    public int getMaxConnections() {
        return egg.getMaxConnections();
    }

}
