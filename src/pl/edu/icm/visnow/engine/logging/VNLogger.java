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

package pl.edu.icm.visnow.engine.logging;

import java.util.HashMap;
import pl.edu.icm.visnow.engine.Engine;
import pl.edu.icm.visnow.engine.element.Element;
import pl.edu.icm.visnow.engine.element.ElementState;
import pl.edu.icm.visnow.engine.error.Displayer;
import pl.edu.icm.visnow.engine.main.ModuleElement;
import pl.edu.icm.visnow.engine.main.Port;
import pl.edu.icm.visnow.engine.messages.Message;

/**
 *
 * @author Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class VNLogger {

    private static boolean debug = true;
    private static LoggerType soutLogger = new LoggerType() {
        public void log(String message, Object obj, String type) {
            if (debug) {
                System.out.println(Displayer.timestamp() + " " + type.toUpperCase() + ": [" + obj.toString() + "]\t\t" + message);
            }
        }
    };
    
    public static HashMap<String, LoggerType> loggers =
            new HashMap<String, LoggerType>();

//    public static void log(String message, Object object, String type) {
//        LoggerType logger = loggers.get(type.toLowerCase());
//        if (logger == null || !debug) {
//            return;
//        }
//        logger.log(message, object, type);
//    }

    public static void init(boolean dbg) {
        loggers.put("engine", soutLogger);
        debug = dbg;
    }


//    private static boolean engineStateDebug     = true;
//    private static boolean moduleStateDebug     = true;
//    private static boolean portStateDebug       = false;
//    private static boolean engineMessageDebug   = true;
//    private static boolean moduleMessageDebug   = true;
//    private static boolean portMessageDebug     = false;
//    private static boolean flowDebug            = true;
//    private static boolean debugEngineLock      = true;
//    private static boolean debugAppLock         = true;


    private static boolean engineStateDebug     = false;
    private static boolean moduleStateDebug     = false;
    private static boolean portStateDebug       = false;
    private static boolean engineMessageDebug   = false;
    private static boolean moduleMessageDebug   = false;
    private static boolean portMessageDebug     = false;
    private static boolean flowDebug            = false;
    private static boolean debugEngineLock      = false;
    private static boolean debugAppLock         = false;




    public final static int STATE = 0;
    public final static int MESSAGE = 1;
    public final static int KILL = 2;


    private static final int[] tabPoints = {0,15,45,75,90,100,110,120};
    private static void putLine(String[] tab) {
        String ret = "";
        for(int i=0; i<tab.length; ++i) {
            ret += " ";
            while(ret.length() < tabPoints[i]) ret += " ";
            ret += tab[i];
        }
        System.out.println(ret);
    }

    public static void debugState(Element element, ElementState from, ElementState to) {
        if((element instanceof ModuleElement && moduleStateDebug) ||
           (element instanceof Engine && engineStateDebug ) ||
           (element instanceof Port && portStateDebug))

        putLine(new String[] {
                Displayer.timestamp(),
                Thread.currentThread().getName(),
                "["+element.getName()+"]",
                from+" -> "+to
        });
    }

    public static void debugMessage(Element element, boolean dealing, Message message) {
        if((element instanceof ModuleElement && moduleMessageDebug) ||
           (element instanceof Engine && engineMessageDebug ) ||
           (element instanceof Port && portMessageDebug))

        putLine(new String[] {
                Displayer.timestamp(),
                Thread.currentThread().getName(),
                "[" + element.getName()+"]",
                ((dealing)?"deal:\t":"received:\t"),
                message.toString()
        });
    }

    public static void debugFlow(boolean start, Element sender) {
        if(flowDebug) {
            if(start) {System.out.println(""); System.out.println("");}
            putLine(new String[] {
                    Displayer.timestamp(),
                    Thread.currentThread().getName(),
                    ((start)?"START":"FINISH")+" from [" +sender.getName()+"]"
            });
            if(!start) {System.out.println(""); System.out.println("");}
        }
    }

    public static void debugAppLock(String action) {
        if(debugAppLock)
            putLine(new String[]{
                Displayer.timestamp(),
                Thread.currentThread().getName(),
                "app "+action.toUpperCase(),
            });
    }

    public static void debugEngineLock(String action) {
        if(debugEngineLock)
            putLine(new String[]{
                Displayer.timestamp(),
                Thread.currentThread().getName(),
                "engine "+action.toUpperCase(),
            });
    }

   private VNLogger()
   {
   }
}
