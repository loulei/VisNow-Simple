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
import pl.edu.icm.visnow.engine.element.Element;
import pl.edu.icm.visnow.engine.element.ElementState;
import pl.edu.icm.visnow.engine.error.Displayer;
import pl.edu.icm.visnow.engine.exception.VNSystemEngineException;
import pl.edu.icm.visnow.engine.exception.VNSystemEngineStateException;
import pl.edu.icm.visnow.engine.main.InputSaturation;
import pl.edu.icm.visnow.engine.main.OutputSaturation;
import pl.edu.icm.visnow.engine.main.Port;
import static pl.edu.icm.visnow.engine.main.Port.LINK_DATA_STATUS_ERROR;
import pl.edu.icm.visnow.engine.messages.Message;
import pl.edu.icm.visnow.lib.types.VNDataAcceptor;

/**
 *
 * @author Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class Input extends Port {
    private static boolean debug = false;

    protected InputSaturation saturation;
    protected Link saturationReasonLink = null;
    public InputSaturation getInputSaturation() {
        return saturation;
    }
    public Link getInputSaturationReasonLink() {
        return saturationReasonLink;
    }
    private void setInputSaturation(InputSaturation saturation, Link saturationReasonLink) {
        this.saturation = saturation;
        this.saturationReasonLink = saturationReasonLink;
        this.fireElementSaturationListeners();
    }

    protected InputEgg egg;
    @Override
    public boolean isInput() {return true;}
    @Override
    public Class getType() {return egg.getType();}
    @Override
    public String getDescription() {return egg.getDescription();}
    public boolean isTriggering() {return egg.isTriggering();}
    public boolean isNecessary() {return egg.isNecessary();}
    public int getMinConnections() {return egg.getMinConnections();}
    @Override
    public int getMaxConnections() {
        if(egg.getMaxConnections()==-1) return Integer.MAX_VALUE;
        return egg.getMaxConnections();
    }
    public Object getDefaultValue() {return egg.getDefaultValue();}

    protected Vector<VNData> datas;
    protected Vector<Object> values;


    public final void checkSaturation() {
        if(this.isNecessary()) {
            if(this.isLinked()) {
                for(Link link: this.getLinks()) {
                    if(link.getOutput().getOutputSaturation() == OutputSaturation.noData) {
                        setInputSaturation(InputSaturation.noData, link);
                        return;
                    } else {                        
                        int status = this.getLinkDataStatus(link.getOutput());
                        if(status == LINK_DATA_STATUS_ERROR) {
                            setInputSaturation(InputSaturation.wrongData, link);
                            return;
                        }
                    }
                }
                setInputSaturation(InputSaturation.ok, null);
            } else {
                this.setInputSaturation(InputSaturation.notLinked, null);
            }
        } else {
            if(this.isLinked()) {
//                for(VNData data: datas) {
//                    if(data.getValue()==null) {
//                        setInputSaturation(InputSaturation.noData);
//                        return;
//                    }
//                }
                for(Link link: this.getLinks()) {
                    if(link.getOutput().getOutputSaturation() == OutputSaturation.noData) {
                        setInputSaturation(InputSaturation.noData, link);
                        return;
                    } else {                        
                        int status = this.getLinkDataStatus(link.getOutput());
                        if(status == LINK_DATA_STATUS_ERROR) {
                            setInputSaturation(InputSaturation.wrongData, link);
                            return;
                        }
                    }
                }
                setInputSaturation(InputSaturation.ok, null);
            } else {
                this.setInputSaturation(InputSaturation.ok, null);
            }
        }
    }

    public boolean addLink(Link link) {
        if(debug) System.out.println("add link to input");
        if(links.size() >= getMaxConnections())
            return false;
        links.add(link);
        if(debug) System.out.println("added link to input");
        try {
            this.getModuleBox().getCore().onInputAttach(link);
        } catch(Exception e) {
            Displayer.ddisplay(200909302300L, e, this,
                    "ERROR IN MODULE FUNCTION:\n"+
                    "An error has occured in the function \"onInputAttach\""+
                    "of module \""+name+"\".\n"+
                    "Please report this error to the module core developer."
                    );
        }
        checkSaturation();
        this.getModuleBox().getElement().checkSaturation();
        this.getModuleBox().getEngine().engineSaturationCheck();
        return true;
    }

    public boolean removeLink(Link link) {
        this.links.remove(link);
        try {
            this.getModuleBox().getCore().onInputDetach(link);
        } catch(Exception e) {
            Displayer.ddisplay(200909302300L, e, this,
                    "ERROR IN MODULE FUNCTION:\n"+
                    "An error has occured in the function \"onInputDetach\""+
                    "of module \""+name+"\".\n"+
                    "Please report this error to the module core developer."
                    );
        }
        checkSaturation();
        this.getModuleBox().getElement().checkSaturation();
        this.getModuleBox().getEngine().engineSaturationCheck();
        return true;
    }

    public Input(InputEgg egg) {//, ModuleBox module) {
        super(egg.getName());//, module);
        this.egg = egg;
        this.predecessors = new Vector<Element>();
        this.action_wait = 0;
        this.links = new Vector<Link>();
        this.datas = new Vector<VNData>();
        this.values = new Vector<Object>();
        checkSaturation();
    }
    
    //private ModuleBox module;
    //public ModuleBox getModuleBoxEgg() {return module;}

    //<editor-fold defaultstate="collapsed" desc=" NRAD Message Propagating ">

    protected Vector<Element> predecessors;
    protected int action_wait;
    protected boolean anyoneActive;

    @Override
    protected void onNotifyMessage(Message message) throws VNSystemEngineException {
        //VNLogger.debugMessage(this, true, message);
        //this.getModuleBox().getEngine().writeFlow(this+" : notify");
        if(getElementState() == ElementState.passive) {
            setElementState(ElementState.notifying);
            predecessors.clear();
            action_wait = 0;
            anyoneActive = false;
            try {
                getModuleBox().getElement().putToQueue(new Message(this, Message.NOTIFY));
            } catch (InterruptedException ex) {
                throw new VNSystemEngineException(
                        200812101750L,
                        "Notify message propagation interrupted",
                        ex,
                        this,
                        Thread.currentThread()
                        );

            }
            predecessors.add(message.getSender());
            ++action_wait;
            return;
        }
        if(getElementState() == ElementState.notifying) {
            predecessors.add(message.getSender());
            ++action_wait;
            return;
        }
        if(getElementState() == ElementState.ready) {
            predecessors.add(message.getSender());
            ++action_wait;
            try {
                message.getSender().putToQueue(new Message(this, Message.READY));
            } catch(InterruptedException ex) {
                throw new VNSystemEngineException(
                        200910261503L,
                        "Notify message answer interrupted",
                        ex,
                        this,
                        Thread.currentThread()
                        );
            }
            return;
        }

        throw new VNSystemEngineStateException(
                200812101745L,
                "Wrong state of input: notifying @ "+getElementState(),
                null,
                this,
                Thread.currentThread()
                );
        
    }

    @Override
    protected void onReadyMessage(Message message) throws VNSystemEngineException {
        //this.getModuleBox().getEngine().writeFlow(this+" : ready");
        if(getElementState() != ElementState.notifying)
             throw new VNSystemEngineStateException(
                    200812101752L,
                    "Wrong state of input: ready @ "+getElementState(),
                    null,
                    this,
                    Thread.currentThread()
                    );
        setElementState(ElementState.ready);
        try {
            for(Element element: predecessors) {
                element.putToQueue(new Message(this, Message.READY));
            }
        } catch (InterruptedException ex) {
            throw new VNSystemEngineException(
                    200812101750L,
                    "Ready message propagation interrupted",
                    ex,
                    this,
                    Thread.currentThread()
                    );
        }

    }

    @Override
    protected void onActionMessage(Message message) throws VNSystemEngineException {
        //this.getModuleBox().getEngine().writeFlow(this+" : action");
        if(getElementState() != ElementState.ready)
            throw new VNSystemEngineStateException(
                    200812101755L,
                    "Wrong state of input: action @ "+getElementState(),
                    null,
                    this,
                    Thread.currentThread()
                    );
        -- action_wait;
        anyoneActive = true;
        if(action_wait!=0) return;
        setActive();
    }

    @Override
    protected void onInactionMessage(Message message) throws VNSystemEngineException {
        //this.getModuleBox().getEngine().writeFlow(this+" : inaction");
        if(getElementState() != ElementState.ready)
            throw new VNSystemEngineStateException(
                    200812101755L,
                    "Wrong state of input: action @ "+getElementState(),
                    null,
                    this,
                    Thread.currentThread()
                    );
        -- action_wait;
        if(action_wait!=0) return;
        setActive();
    }

    protected void setActive() throws VNSystemEngineException {
        setElementState(ElementState.propagating);
        try {
            getModuleBox().getElement().putToQueue(new Message(this,(anyoneActive)?Message.ACTION:Message.INACTION));
        } catch (InterruptedException ex) {
            throw new VNSystemEngineException(
                    200812101758L,
                    "Action message propagation interrupted",
                    null,
                    this,
                    Thread.currentThread()
                    );

        }
    }

    @Override
    protected void onDoneMessage(Message message) throws VNSystemEngineException {
//        this.getModuleBox().getEngine().writeFlow(this+" : done");
        if(getElementState() != ElementState.propagating)
            throw new VNSystemEngineStateException(
                    200812101759L,
                    "Wrong state of input: ready @ "+getElementState(),
                    null,
                    this,
                    Thread.currentThread()
                    );
        setElementState(ElementState.passive);
        try {
            for(Element element: predecessors) {
                element.putToQueue(new Message(this, Message.DONE));
            }
        } catch (InterruptedException ex) {
            throw new VNSystemEngineException(
                    200812101760L,
                    "Done message propagation interrupted",
                    null,
                    this,
                    Thread.currentThread()
                    );
        }
        predecessors.clear();
        action_wait = 0;
    }
    //</editor-fold>

    @Override
    protected void onKillMessage() {
        
    }


    public VNData getFirstData() {
        if(links.isEmpty())
            return null;
        return links.elementAt(0).getOutput().getData();
    }

    public Vector<VNData> getDatas() {
        datas.clear();
        for(Link link: links)
            datas.add(link.getOutput().getData());
        return datas;
    }

    //TODO: wersje pobierajace DV i niepobierajace DV
    //TOOD: dobry opis w javadoc
    public Object getFirstValue() {
        if(links.isEmpty())
            return getDefaultValue();
        return links.elementAt(0).getOutput().getValue();
    }

    public Vector<Object> getValues() {
        values.clear();
        for(Link link: links)
            values.add(link.getOutput().getValue());
        return values;
    }


    @Override
    public String toString() {
        return getModuleBox().getName() + ".in."+getName();
    }

    public VNDataAcceptor[] getVNDataAcceptors() {
        return egg.getVNDataAcceptors();
    }
}
