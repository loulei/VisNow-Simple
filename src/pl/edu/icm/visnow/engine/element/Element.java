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

package pl.edu.icm.visnow.engine.element;

import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;
import pl.edu.icm.visnow.engine.error.Displayer;
import pl.edu.icm.visnow.engine.exception.VNSystemEngineException;
import pl.edu.icm.visnow.engine.logging.VNLogger;
import pl.edu.icm.visnow.engine.messages.Message;

/**
 *
 * @author  Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public abstract class Element implements Runnable {

    private static boolean debug = false;
    //<editor-fold defaultstate="collapsed" desc=" [VAR] ">
    private ElementState elementState;
    //private ElementSaturation elementSaturation;
    private LinkedBlockingQueue<Message> queue; //need to be fair, use SynchronousQueue(true) constructor
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Accessors ">
    /**
     * @return the elementState
     */
    public ElementState getElementState() {
        return elementState;
    }

    /**
     * @param elementState the elementState to set
     */
    protected void setElementState(ElementState elementState) {
        VNLogger.debugState(this, this.elementState, elementState);
        this.elementState = elementState;
    }

//    /**
//     * @return the elementSaturation
//     */
//    public ElementSaturation getElementSaturation() {
//        return elementSaturation;
//    }
//
//    /**
//     * @param elementSaturation the elementSaturation to set
//     */
//    protected void setElementSaturation(ElementSaturation elementSaturation) {
//        this.elementSaturation = elementSaturation;
//    }


    private final Object lock = new Object();

    public void putToQueue(Message message) throws InterruptedException {
        //synchronized(lock) {
            VNLogger.debugMessage(this, false, message);
            getQueue().put(message);
        //}
    }

    protected Message takeFromQueue() throws InterruptedException {
        //synchronized(lock) {
        //System.out.println("THREAD ["+Thread.currentThread()+"] is waiting on the queue.");
            return getQueue().take();
        //}
    }

    protected void clearQueue() {
        //synchronized(lock) {
            getQueue().clear();
        //}
    }

    private LinkedBlockingQueue<Message> getQueue() {
        return queue;
    }



    //</editor-fold>
    protected ElementKiller killer;

    private void kill() {
        clearQueue();
        setElementState(ElementState.passive);
        //TODO: log
    }
    protected String name;

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    public Element(String name) {
        this.name = name;
        //super(name);
        elementState = ElementState.passive;
//        elementSaturation = ElementSaturation.notSaturated;
        queue = new LinkedBlockingQueue<Message>();
    }

    @SuppressWarnings("empty-statement")
    @Override
    public void run() {
        //if(debug) System.out.println("I AM RUNNING! {"+this+"}");
        try {
            //System.out.println("waiting... "+this);
            while (nextMessage()) {
            }
        } catch (InterruptedException ex) {
            //Displayer.ddisplay(200907100810L, ex, this, "Interrupted.");
        } catch (VNSystemEngineException ex) {
            Displayer.display(200907100811L, ex, this, "Elementary exception (Ooops!).");
        } catch (OutOfMemoryError err) {
            Exception ex = new Exception("Out of memory", err);
            Displayer.ddisplay(201112051933L, ex, this, "Out of memory error.");
        }
        //if(debug) System.out.println("I AM NOT RUNNING :( {"+this+"}");
    }



    //<editor-fold defaultstate="collapsed" desc=" Messages ">
    public boolean nextMessage() throws InterruptedException, VNSystemEngineException {
        //if(debug) System.out.println("................ on "+getQueue().toString());
        Message message = takeFromQueue();


        ElementLogger.logMessage(this, message);
        if (killer.isKilled()) {
            kill();
            System.out.println("ENGINE KILLED");
            return true;
        }
        VNLogger.debugMessage(this, true, message);//.log("message received: " + message, this, "engine");
        //if(debug) System.out.println(message+" received by ["+this+"]");
        switch (message.getType()) {
            case Message.KILL:
                onKillMessage();
                return false;
            case Message.NOTIFY:
                onNotifyMessage(message);
                break;
            case Message.READY:
                onReadyMessage(message);
                break;
            case Message.ACTION:
                onActionMessage(message);
                break;
            case Message.INACTION:
                onInactionMessage(message);
                break;
            case Message.DONE:
                onDoneMessage(message);
                break;
            default:
                onOtherMessage(message);
                break;
        }
        return true;
    }

    protected abstract void onNotifyMessage(Message message) throws VNSystemEngineException;

    protected abstract void onReadyMessage(Message message) throws VNSystemEngineException;

    protected abstract void onActionMessage(Message message) throws VNSystemEngineException;

    protected abstract void onInactionMessage(Message message) throws VNSystemEngineException;

    protected abstract void onDoneMessage(Message message) throws VNSystemEngineException;

    protected abstract void onKillMessage() throws VNSystemEngineException;

    protected void onOtherMessage(Message message) throws VNSystemEngineException {
    }
    //</editor-fold>
    protected Vector<ElementSaturationListener> saturationListeners = new Vector<ElementSaturationListener>();

    public void addSaturationListener(ElementSaturationListener elementSaturationListener) {
        saturationListeners.add(elementSaturationListener);
    }

    protected void fireElementSaturationListeners() {
        for (ElementSaturationListener listener : saturationListeners) {
            listener.saturationChanged();
        }
    }
    private Vector<ElementStateListener> listeners = new Vector<ElementStateListener>();

    public void addElementStateListener(ElementStateListener listener) {
        listeners.add(listener);
    }

    protected void fireElementStateListeners() {
        for (ElementStateListener listener : listeners) {
            listener.elementStateSet(this.getElementState());
        }
    }

    //public void restart() {
    //    this.setElementState(ElementState.passive);
    //    clearQueue();
//
//    }
    
}
