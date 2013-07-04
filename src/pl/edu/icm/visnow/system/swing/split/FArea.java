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

package pl.edu.icm.visnow.system.swing.split;

import java.awt.BorderLayout;
import java.awt.Container;

/**
 *
 * @author gacek
 */
public abstract class FArea extends Container {


    public final static int nullD = -1;
    public final static int centerD = 0;
    public final static int topD = 1;
    public final static int bottomD = 2;
    public final static int leftD = 3;
    public final static int rightD = 4;
    public final static int lastD = rightD+1;

    
    protected abstract boolean isMajor();
    protected abstract boolean isSplit();
    protected abstract boolean isSingle();

    protected abstract FAreaMajor getMajor();
    protected abstract FPlace getSomePlace();
    protected abstract void replaceChild(FAreaSplittable oldChild, FAreaSplittable newChild);


    public void addBox(FBox box) {addBox(box, FAreaSplittable.centerD);}
    public abstract void addBox(FBox box, int direction);


    protected abstract void notifySplitListeners();
    //protected abstract void resize();



    protected FArea() {
        setLayout(new BorderLayout());
    }



}
