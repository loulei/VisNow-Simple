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

package pl.edu.icm.visnow.engine.messages;

import pl.edu.icm.visnow.engine.element.Element;


/**
 *
 * @author Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class Message {

    public final static int KILL = 1;

    public final static int NOTIFY = 2;
    public final static int READY = 3;
    public final static int ACTION = 4;
    public final static int INACTION = 5;
    public final static int DONE = 6;

    public final static int LINK_ADD = 8;
    public final static int LINK_REMOVE = 9;

    public final static int START_ACTION = 10;

    private Element sender;
    public Element getSender() {return sender;}

    private int type;
    public int getType() {return type;}

    //private Wave wave;
    //public Wave getWave() {return wave;}

    public Message(Element sender, int type) {
        this.sender = sender;
        this.type = type;
    }

    protected static String[] types = {
        "null",
        "kill",
        "notify",
        "ready",
        "action",
        "inaction",
        "done",
        "WRONG",
        "link_add",
        "link_remove",
        "start_action",
        "WRONG"
    };

    @Override
    public String toString() {
        return "("+types[getType()]+") message from ["+getSender()+"]";
    }
}
