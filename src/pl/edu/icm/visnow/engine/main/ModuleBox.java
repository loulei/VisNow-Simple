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



import pl.edu.icm.visnow.engine.core.Output;
import pl.edu.icm.visnow.engine.core.Link;
import pl.edu.icm.visnow.engine.core.Input;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.edu.icm.visnow.engine.core.Inputs;
import pl.edu.icm.visnow.engine.core.ModuleCore;
import pl.edu.icm.visnow.engine.core.Outputs;
import pl.edu.icm.visnow.engine.core.Parameters;
import pl.edu.icm.visnow.engine.Engine;
import pl.edu.icm.visnow.engine.core.ModuleBoxFace;
import pl.edu.icm.visnow.engine.messages.Message;


/**
 *
 * @author Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class ModuleBox implements Iterable<Link>, ModuleBoxFace {

    private static boolean debug = false;


//    private ModuleSaturation saturation;
//    public ModuleSaturation getSaturation() {
//        return saturation;
//    }

    //<editor-fold defaultstate="collapsed" desc=" [VAR] Name | Engine | internal blocks ">
    private String name;
    private Engine engine;
    private ModuleElement element;
    private ModuleCore core;

    //private Inputs inputs;
    //private Outputs outputs;
    //private Parameters parameters;


    @Override
    public String getName() {return name;}

    /**
     * @return the application
     */
    public Engine getEngine() {
        return engine;
    }

    /**
     * @return the inputs
     */
    public Inputs getInputs() {
        return core.getInputs();
    }

    /**
     * @return the outputs
     */
    public Outputs getOutputs() {
        return core.getOutputs();
    }

    /**
     * @return the parameters
     */
    public Parameters getParameters() {
        return core.getParameters();
    }

    /**
     * @return the element
     */
    public ModuleElement getElement() {
        return element;
    }

    /**
     * @return the core
     */
    public ModuleCore getCore() {
        return core;
    }



    public Input getInput(String name) {
        return core.getInputs().getInput(name);
    }

    public Output getOutput(String name) {
        return core.getOutputs().getOutput(name);
    }

    public Port getPort(boolean isInput, String name) {
        if(isInput)
            return getInput(name);
        return getOutput(name);
    }
    //</editor-fold>

    @SuppressWarnings("static-access")
    public ModuleBox(Engine engine, String name, ModuleCore core) {
        this.engine = engine;
        this.name = name;
        this.core = core;
        if(core != null) {
            this.element = new ModuleElement(this);
            core.setModuleBoxEgg(this);
            this.element.checkSaturation();
        }
    }

    Thread elementThread;

    public void doTheMainResetKillAll() {
        this.element.doTheMainResetKillAll();
        elementThread.interrupt();

        for(Input input: getInputs()) {
            input.doTheMainResetKillAll();
        }
        for(Output output: getOutputs()) {
            output.doTheMainResetKillAll();
        }

        for(Thread t: portThreads)
            t.interrupt();

        portThreads.clear();
    }

    public void doTheMainResetWakeUp() {
        run();
    }

    private Vector<Thread> portThreads = new Vector<Thread>();
    //public Vector<Thread> getThreads() {return threads;}

    public void run() {
        
        if(debug) System.out.println("run {"+this+"}");
        elementThread = new Thread(getElement(), "VNM-"+this.getName());
        elementThread.start();
        //threads.add(elementThread);
        for(Input input: getInputs()) {
            Thread t = new Thread(input, "VNI-"+input.getName());
            t.start();
            portThreads.add(t);
        }
        for(Output output: getOutputs()) {
            Thread t = new Thread(output, "VNO-"+output.getName());
            t.start();
            portThreads.add(t);
        }

    }

    public Iterator<Link> iterator() {
        return new ModuleLinkIterator(this);
    }

    public Iterator<Link> iterator(boolean in, boolean out) {
        return new ModuleLinkIterator(this, in, out);
    }

    public void startAction() {
        try {
            if(getEngine() == null)
                return;
            
            getEngine().putToQueue(new Message(this.getElement(), Message.START_ACTION));
            //this.getElement().getQueue().put(new Message(null, Message.START_ACTION));
        } catch (InterruptedException ex) {
            ex.printStackTrace();
            Logger.getLogger(ModuleBox.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setProgress(float progress) {
        getEngine()
                .getApplication()
                .getArea()
                .getInput()
                .setModuleProgress(this.getName(), progress);
    }

    public void updateName(String newName) {
        this.name = newName;
    }

    public boolean hasIndirectDownstreamConnectionTo(ModuleBox mod) {
        if(mod == null)
            return false;
        
        if(mod == this)
            return true;

        Iterator<Output> outs = getOutputs().iterator();
        Output out;
        Vector<Link> links;
        boolean tmp;
        while(outs.hasNext()) {
            out = outs.next();
            links = out.getLinks();
            for (int i = 0; i < links.size(); i++) {
                tmp = links.get(i).getInput().getModuleBox().hasIndirectDownstreamConnectionTo(mod);
                if(tmp)
                    return true;
            }
        }
        return false;
    }

    public boolean hasIndirectUpstreamConnectionTo(ModuleBox mod) {
        if(mod == null)
            return false;
        
        if(mod == this)
            return true;

        Iterator<Input> ins = getInputs().iterator();
        Input in;
        Vector<Link> links;
        boolean tmp;
        while(ins.hasNext()) {
            in = ins.next();
            links = in.getLinks();
            for (int i = 0; i < links.size(); i++) {
                tmp = links.get(i).getOutput().getModuleBox().hasIndirectUpstreamConnectionTo(mod);
                if(tmp)
                    return true;
            }
        }
        return false;
    }



    
    
}
