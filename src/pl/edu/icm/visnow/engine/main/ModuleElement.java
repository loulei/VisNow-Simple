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

package pl.edu.icm.visnow.engine.main;

import java.util.ArrayList;
import pl.edu.icm.visnow.engine.core.Output;
import pl.edu.icm.visnow.engine.core.Input;
import javax.swing.JOptionPane;
import pl.edu.icm.visnow.engine.Engine;
import pl.edu.icm.visnow.engine.element.Element;
import pl.edu.icm.visnow.engine.element.ElementState;
import pl.edu.icm.visnow.engine.error.Displayer;
import pl.edu.icm.visnow.engine.exception.VNSystemEngineException;
import pl.edu.icm.visnow.engine.exception.VNSystemEngineStateException;
import pl.edu.icm.visnow.engine.messages.Message;


/**
 *
 * @author Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class ModuleElement extends Element {

    protected int ready_wait;
    protected int action_wait;
    protected int done_wait;
    protected ArrayList<Element> predecessors;

    //<editor-fold defaultstate="collapsed" desc=" [VAR] ModuleBox ">
    private ModuleBox moduleBox;
    public ModuleBox getModuleBox() {
        return moduleBox;
    }
    //</editor-fold>

    private ModuleSaturation saturation;
    private Input saturationReasonInput;
    public ModuleSaturation getSaturation() {return saturation;}
    public Input getSaturationReasonInput() {return saturationReasonInput;}
    public void setSaturation(ModuleSaturation saturation, Input saturationReasonInput) {
        //System.err.println("module "+this.getName()+" setting saturation to "+saturation);
        this.saturation = saturation;
        this.saturationReasonInput = saturationReasonInput;        
        moduleBox.getCore().onSaturationChange(saturation, saturationReasonInput);
        this.fireElementSaturationListeners();
    }

    private boolean checkSaturationType(InputSaturation in, ModuleSaturation mod) {
        for(Input input: this.getModuleBox().getInputs()) {
            if(input.getInputSaturation() == in) {
                this.setSaturation(mod, input);
                return true;
            }
        }
        return false;
    }

    public void checkSaturation() {
        //System.out.println("check sat");
        if(checkSaturationType(InputSaturation.wrongData, ModuleSaturation.wrongData)) return;
        //System.out.println("no WD");
        if(checkSaturationType(InputSaturation.notLinked, ModuleSaturation.notLinked)) return;
        //System.out.println("no NL");
        if(checkSaturationType(InputSaturation.noData, ModuleSaturation.noData)) return;
        //System.out.println("no ND");
        this.setSaturation(ModuleSaturation.ok, null);
    }

    public ModuleElement(ModuleBox moduleBox) {
        super(moduleBox.getName());//+".element");
        this.killer = moduleBox.getEngine();
        //System.out.println("new element");
        this.moduleBox = moduleBox;
        this.predecessors = new ArrayList<Element>();
        this.ready_wait = 0;
        this.done_wait = 0;
    }

    //<editor-fold defaultstate="collapsed" desc=" onNotify ">
    @Override
    protected void onNotifyMessage(Message message) throws VNSystemEngineStateException, VNSystemEngineException {
        //this.getModuleBox().getEngine().writeFlow(this+" : notify");
        if(getElementState()==ElementState.notifying) {
            predecessors.add(message.getSender());
            ++action_wait;
            return;
        }
        if(getElementState()==ElementState.ready) {
            predecessors.add(message.getSender());
            try {
                message.getSender().putToQueue(new Message(this, Message.READY));
            } catch (InterruptedException ex) {
                throw new VNSystemEngineException(
                200911000001L,
                "Notify message propagation interrupted",
                ex,
                this,
                Thread.currentThread()
                );
            }
            ++action_wait;
            return;
        }
        if(getElementState()!=ElementState.passive) {
            throw new VNSystemEngineStateException(
                    200903271420L,
                    "Wrong state of moduleElement: notify @ "+getElementState(),
                    null,
                    this,
                    Thread.currentThread()
                    );
        }
        setElementState(ElementState.notifying);
//        moduleBox.getEngine().getApplication().getScene().getScenePanel().getProgress().addModule(moduleBox.getName());
        predecessors.clear();
        anyoneActive = false;
        action_wait = 1; /* TODO: czy to potrzebne? */
        done_wait = 0;
        ready_wait = 0;
        predecessors.add(message.getSender());
        for(Output output: getModuleBox().getOutputs()) {
            ++ready_wait;
            ++done_wait;
            try {
                output.putToQueue(new Message(this, Message.NOTIFY));
            } catch (InterruptedException ex) {
                throw new VNSystemEngineException(
                        200910260001L,
                        "Notify message propagation interrupted",
                        ex,
                        this,
                        Thread.currentThread()
                        );
            }
        }

        if(ready_wait==0) {
            for(Element predecessor: predecessors) {
                try {
                    //System.out.println("ADDING");
                    predecessor.putToQueue(new Message(this, Message.READY));
                } catch (InterruptedException ex) {
                    throw new VNSystemEngineException(
                        200910260002L,
                        "Notify message propagation interrupted",
                        ex,
                        this,
                        Thread.currentThread()
                        );
                }
            }
            setElementState(ElementState.ready);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" onReady ">
    @Override
    protected void onReadyMessage(Message message) throws VNSystemEngineStateException, VNSystemEngineException {
//        this.getModuleBox().getEngine().writeFlow(this+" : ready");
        if(getElementState()!=ElementState.notifying)
            throw new VNSystemEngineStateException(
                    200903271421L,
                    "Wrong state of moduleElement: ready @ "+getElementState(),
                    null,
                    this,
                    Thread.currentThread()
                    );
        --ready_wait;
        if(ready_wait==0) {
            for(Element predecessor: predecessors) {
                try {
                    predecessor.putToQueue(new Message(this, Message.READY));
                } catch (InterruptedException ex) {
                    throw new VNSystemEngineException(
                        200910260003L,
                        "Ready message propagation interrupted",
                        ex,
                        this,
                        Thread.currentThread()
                        );
                }
            }
            setElementState(ElementState.ready);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" onActive ">
    protected boolean anyoneActive;

    @Override
    protected void onActionMessage(Message message) throws VNSystemEngineStateException, VNSystemEngineException {
        this.checkSaturation();
        //this.getModuleBox().getEngine()writeFlow(this+" : action");
        if(getElementState() == ElementState.propagating) {
            java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {JOptionPane.showMessageDialog(null, "Network error #1A ignored.");}
            });
            return;
        }
        if(getElementState() != ElementState.ready)
            throw new VNSystemEngineStateException(
                    200903271422L,
                    "Wrong state of moduleElement: action @ "+getElementState(),
                    null,
                    this,
                    Thread.currentThread()
                    );
        --action_wait;
        anyoneActive = true;
        if(action_wait == 0) {
            if(message.getSender() instanceof Engine) //TODO: załatanie w celu utworzenia onLocalActive();
                setActive(true);
            else
                setActive(false);
        }
            //for(Output output: getOutputs()) {
            //    output.getData().markValueOld();
            //}
    }


    @Override
    protected void onInactionMessage(Message message) throws VNSystemEngineStateException, VNSystemEngineException {
//        this.getModuleBox().getEngine().writeFlow(this+" : inaction");
        if(getElementState() != ElementState.ready)
            throw new VNSystemEngineStateException(
                    200903271423L,
                    "Wrong state of moduleElement: inaction @ "+getElementState(),
                    null,
                    this,
                    Thread.currentThread()
                    );
        --action_wait;
        if(action_wait == 0) {
            if(message.getSender() instanceof Engine) //TODO: załatanie w celu utworzenia onLocalActive();
                setActive(true);
            else
                setActive(false);
        }
    }

    protected void setActive(boolean first) throws VNSystemEngineException {
        setElementState(ElementState.active);
// MOVED to onDone();
//        for(Output output: getModuleBox().getOutputs()) {
//            output.getData().markValueOld();
//        }
        if(anyoneActive) {
            getModuleBox().getEngine().getPermission(this);
            if(first)
                try {
                    getModuleBox().getCore().onLocalActive(); //TODO: try-catch
                } catch(Exception e) {
                    killFromModule(e, "onLocalActive");
                    return;
                }
            else
                try {
                    getModuleBox().getCore().onActive(); 
                } catch(Exception e) {
                    killFromModule(e, "onActive");
                    return;
                }
            this.getModuleBox().setProgress(1.f);
            setElementState(ElementState.propagating);
            
            for(Output output: getModuleBox().getOutputs()) {
                try {
                    output.putToQueue(new Message(this, Message.ACTION));
                } catch (InterruptedException ex) {
                    throw new VNSystemEngineException(
                        200910260010L,
                        "Active message propagation interrupted",
                        ex,
                        this,
                        Thread.currentThread()
                        );
                }
            }

            if(done_wait == 0) {
                setElementState(ElementState.done);
                
                for(Element predecessor: predecessors)
                    try {
                    predecessor.putToQueue(new Message(this, Message.DONE));
                } catch (InterruptedException ex) {
                    throw new VNSystemEngineException(
                        200910260011L,
                        "Active message propagation interrupted",
                        ex,
                        this,
                        Thread.currentThread()
                        );
                }
                setElementState(ElementState.passive);
                
            }

        } else {
            try {
                getModuleBox().getCore().onInactive(); /////////////////////////////////////////////////////////////////////////////////
            } catch(Exception e) {
                killFromModule(e, "onInactive");
                return;
            }
            this.getModuleBox().setProgress(1.f);
            setElementState(ElementState.propagating);
            
            for(Output output: getModuleBox().getOutputs()) {
                try {
                    output.putToQueue(new Message(this, Message.INACTION));
                } catch (InterruptedException ex) {
                    throw new VNSystemEngineException(
                        200910260013L,
                        "Action message propagation interrupted",
                        ex,
                        this,
                        Thread.currentThread()
                        );
                }
            }
            if(done_wait == 0) {
                setElementState(ElementState.done);
                
                for(Element predecessor: predecessors)
                    try {
                        predecessor.putToQueue(new Message(this, Message.DONE));
                    } catch (InterruptedException ex) {
                        throw new VNSystemEngineException(
                            200910260014L,
                            "Action message propagation interrupted",
                            ex,
                            this,
                            Thread.currentThread()
                            );
                    }
                setElementState(ElementState.passive);
                
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" onDone ">
    @Override
    protected void onDoneMessage(Message message) throws VNSystemEngineStateException, VNSystemEngineException {
//        this.getModuleBox().getEngine().writeFlow(this+" : done");
        if(getElementState() != ElementState.propagating)
            throw new VNSystemEngineStateException(
                    200903271424L,
                    "Wrong state of moduleElement: done @ "+getElementState(),
                    null,
                    this,
                    Thread.currentThread()
                    );
        --done_wait;
        //System.out.println("DONE RECEIVED");
        //System.out.println("DONE-WAIT LEVEL: "+done_wait);
        //System.out.println("PREDECESSORS COUNT: "+predecessors.size());
        if(done_wait == 0) {
            setElementState(ElementState.done);
            
            for(Element predecessor: predecessors)
                try {
                    predecessor.putToQueue(new Message(this, Message.DONE));
                } catch (InterruptedException ex) {
                    throw new VNSystemEngineException(
                        200910260020L,
                        "Done message propagation interrupted",
                        ex,
                        this,
                        Thread.currentThread()
                        );
                }
            setElementState(ElementState.passive);
            for(Output output: getModuleBox().getOutputs()) {
                output.getData().markValueOld();
            }
        }
    }
    //</editor-fold>

    @Override
    protected void onKillMessage() {
        
    }

    private void killFromModule(Exception e, String functionName) {
        Displayer.ddisplay(42, e, this,
                "An error occured in function \""+functionName +"\" "+
                "of module \""+this.getName()+"\".\n"+
                "Please report this exception to the module core developer.\n"+
                "The application flow will be terminated."
                );
        this.getModuleBox().getEngine().kill();
        setElementState(ElementState.passive);        
        //this.getModuleBox().getEngine().getApplication().doTheMainReset();
    }

    @Override
    protected void setElementState(ElementState state) {
        super.setElementState(state);
        fireElementStateListeners();
    }

    void doTheMainResetKillAll() {
        setElementState(ElementState.passive);
        clearQueue();
    }
}
