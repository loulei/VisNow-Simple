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

package pl.edu.icm.visnow.engine.commands;

/**
 *
 * @author Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class Command {

    //<editor-fold defaultstate="collapsed" desc=" FINAL types ">
    public static final int BLOCK = 0x1;
    public static final int BLOCK_REDO = 0x2;
    //public static final int BEGIN = 0x1;
    //public static final int END = 0x2;

    public static final int ADD_LIBRARY = 0x11;
    public static final int RENAME_LIBRARY = 0x12;
    public static final int DELETE_LIBRARY = 0x13;

    public static final int ADD_MODULE = 0x21;
    public static final int RENAME_MODULE = 0x22;
    public static final int DELETE_MODULE = 0x23;

    public static final int ADD_LINK = 0x31;
    public static final int DELETE_LINK = 0x33;
    public static final int SPLIT_LINK = 0x35;

    
    //public static final int UI_MOVE_MODULE = 0x101;
    public static final int UI_MOVE_MULTIPLE_MODULES = 0x102;
    public static final int UI_SHOW_PORT = 0x111;
    public static final int UI_HIDE_PORT = 0x112;
    public static final int UI_MOVE_LINK_BAR = 0x131;

    public static final int UI_SCENE_SELECTED_MODULE = 0x151;
    public static final int UI_FRAME_SELECTED_MODULE = 0x152;
    //</editor-fold>

    protected int type;
    public int getType() {return type;}

    protected Command reverse;
    public Command getReverseCommand() {return reverse;}


    protected Command(int type) {
        this.type = type;
    }

    
}
