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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;


/**
 *
 * @author Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class Inputs implements Iterable<Input> {

    protected HashMap<String, Input> inputs;
    protected Vector<Input> sorted;
    public HashMap<String, Input> getInputs() {return inputs;}
    public Vector<Input> getSortedInputs() {return sorted;}
    public Input getInput(String name) {return inputs.get(name);}

    //protected void addInput(String name, Input input) {
    //    inputs.put(name, input);
    //}

    public Inputs(InputEgg[] eggs){//, ModuleBox module) {
        inputs = new HashMap<String, Input>();
        sorted = new Vector<Input>();
        if(ModuleBoxFace.ACTIONPORTS) {
            //TODO!
            Input action = new Input(new InputEgg("actionInput", Void.class, InputEgg.TRIGGERING, 0, -1));//, module);
            //TODO!
            inputs.put("actionInput", action);
            //TODO!
            sorted.add(action);
        }
        if(eggs != null)
        for(InputEgg inputEgg: eggs) {
            Input input = new Input(inputEgg);//, module);
            inputs.put(inputEgg.getName(), input);
            sorted.add(input);
        }
    }

    public void setModuleBox(ModuleBoxFace module) {
        for(Input input: inputs.values())
            input.setModuleBox(module);
    }

    @Override
    public Iterator<Input> iterator() {
        return inputs.values().iterator();
    }

    
}
