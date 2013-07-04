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
import pl.edu.icm.visnow.engine.exception.VNRuntimeException;

/**
 *
 * @author Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class ModuleLinkIterator implements Iterator<Link> {

    private Iterator<Input> inputs;
    private Iterator<Output> outputs;
    private Iterator<Link> links;
    private boolean stillInputs;
    private Link myNext;

    public ModuleLinkIterator(ModuleBox module) {
        inputs = module.getInputs().iterator();
        outputs = module.getOutputs().iterator();
        stillInputs = true;
        findNext();
    }

    public ModuleLinkIterator(ModuleBox module, boolean in, boolean out) {
        if(in) inputs = module.getInputs().iterator();
        if(out) outputs = module.getOutputs().iterator();
        stillInputs = in;
        findNext();
    }


    public boolean hasNext() {
        return (myNext != null);
    }

    public Link next() {
        Link ret = myNext;
        findNext();
        return ret;
    }

    public void remove() {
        throw new VNRuntimeException(
                200903000000L,
                "Impossible operation: trying to remove link from moduleLinkIterator.",
                null,
                this,
                Thread.currentThread()
                );
    }

    protected void findNext() {
        while(links == null || !links.hasNext() )
            if( !findNextPort() ) {
                myNext = null;
                return;
            }
        myNext = links.next();
    }

    protected boolean findNextPort() {
        if(stillInputs && inputs.hasNext()) {
            links = inputs.next().getLinks().iterator();
            return true;
        }
        stillInputs = false;
        if(outputs.hasNext()) {
            links = outputs.next().getLinks().iterator();
            return true;
        }
        links = null;
        return false;

    }

}
