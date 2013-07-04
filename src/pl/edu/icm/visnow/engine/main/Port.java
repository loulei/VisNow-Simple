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

import java.util.Vector;
import pl.edu.icm.visnow.engine.core.Input;
import pl.edu.icm.visnow.engine.core.Link;
import pl.edu.icm.visnow.engine.core.ModuleBoxFace;
import pl.edu.icm.visnow.engine.core.Output;
import pl.edu.icm.visnow.engine.element.Element;
import pl.edu.icm.visnow.engine.element.ElementState;
import pl.edu.icm.visnow.lib.types.VNDataAcceptor;
import pl.edu.icm.visnow.lib.types.VNDataSchema;
import pl.edu.icm.visnow.lib.types.VNDataSchemaComparator;

/**
 *
 * @author Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public abstract class Port extends Element {

    public abstract boolean isInput();
    public abstract Class getType();
    public abstract String getDescription();
    public abstract int getMaxConnections();

    protected ModuleBox module;

    public ModuleBox getModuleBox() {
        return module;
    }

    public void setModuleBox(ModuleBox moduleBox) {
        this.module = moduleBox;
        this.killer = moduleBox.getEngine();
    }
    
    public void setModuleBox(ModuleBoxFace moduleBox) {
        this.setModuleBox((ModuleBox)moduleBox);
    }

    protected Vector<Link> links;
    public Vector<Link> getLinks() {return links;}

    public Port(String name) {//, ModuleBox module) {
        super(name);
        //this.module = module;
        this.links = new Vector<Link>();
    }

    public boolean isLinked() {
        return !links.isEmpty();
    }

    @SuppressWarnings("unchecked")
    public boolean isLinkPossible(Port p) {
        if(p == null)
            return false;
        
        int maxc1 = this.getMaxConnections();
        int currc1 = this.getLinks().size();
        int maxc2 = p.getMaxConnections();
        int currc2 = p.getLinks().size();
        
        if(this.isInput()) {
            return ((maxc1 == -1 || currc1 < maxc1) && 
                    (maxc2 == -1 || currc2 < maxc2) && 
                    this.getType().isAssignableFrom(p.getType()));
        } else if(p.isInput()) {
            return ((maxc1 == -1 || currc1 < maxc1) && 
                    (maxc2 == -1 || currc2 < maxc2) && 
                    p.getType().isAssignableFrom(this.getType()));
        }
        return false;
    }

    public boolean isLinkSchemaPossible(Port p) {
        return (getLinkDataStatus(p) != LINK_DATA_STATUS_ERROR);
    }
    
    public boolean isLinkLoopPossible(Port p) {
        ModuleBox sModule = p.getModuleBox();
        ModuleBox eModule = this.getModuleBox();
        if(eModule == sModule)
            return false;
        
        if(this.isInput()) {
            return !eModule.hasIndirectDownstreamConnectionTo(sModule);
        } else if(p.isInput()) {
            return !eModule.hasIndirectUpstreamConnectionTo(sModule);
        }
        return false;
    }
    
    
    public static final int LINK_DATA_STATUS_OK = 0;
    public static final int LINK_DATA_STATUS_CONDITIONAL = 1;
    public static final int LINK_DATA_STATUS_ERROR = 2;
    
    private boolean debug = false;
    
    public int getLinkDataStatus(Port p) {        
        if(debug) System.out.println("");
        if(debug) System.out.println("testing is link data possible (in port "+this.getName()+") between:");
        Port in = null;
        Port out = null;
        if(isInput()) in = this;
        else out = this;
        if(p.isInput()) in = p;
        else out = p;

        if(in == null) return LINK_DATA_STATUS_ERROR;
        if(out == null) return LINK_DATA_STATUS_ERROR;

        if(debug) System.out.println(" - out = "+out.toString());
        if(debug) System.out.println(" - in = "+in.toString());

        Output output = (Output)out;
        Input input = (Input)in;

        VNDataSchema[] outSchemas = output.getVNDataSchemas();

        if(outSchemas == null && output.getData().getValue() == null) {
            if(debug) System.out.println("test result: OK @ no output data and no output schema");
            return LINK_DATA_STATUS_CONDITIONAL;
        }

        VNDataAcceptor[] acceptors = input.getVNDataAcceptors();
        if(acceptors == null || acceptors.length == 0) {
            if(debug) System.out.println("test result: OK @ no input acceptors");
            return LINK_DATA_STATUS_OK;
        }
        
        //check for FULL PASS within acceptors
        for (int i = 0; i < acceptors.length; i++) {
            boolean tmp = false;
            if(output.getData().getValue() != null) {
                tmp = VNDataSchemaComparator.isCompatible(output.getData().getValue(), acceptors[i].getVNDataSchemaInterface(), acceptors[i].getVNDataCompatibilityMask());
                if(tmp) {
                    if(debug) System.out.println("test result: OK @ output data FULLY PASSED acceptor #"+i);
                    return LINK_DATA_STATUS_OK;
                }
            }  else if (outSchemas != null) {
                for (int j = 0; j < outSchemas.length; j++) {
                    tmp = VNDataSchemaComparator.isCompatible(outSchemas[j], acceptors[i].getVNDataSchemaInterface(), acceptors[i].getVNDataCompatibilityMask());
                    if(tmp) {
                        if(debug) System.out.println("test result: OK @ output schema FULLY PASSED acceptor #"+i);
                        return LINK_DATA_STATUS_OK;
                    }
                }
            }
        }
        
        //check for CONDITIONAL PASS within acceptors
        for (int i = 0; i < acceptors.length; i++) {
            boolean tmp = false;
            if(output.getData().getValue() == null && outSchemas != null) {
                for (int j = 0; j < outSchemas.length; j++) {
                    long schemaMask = VNDataSchemaComparator.createComparatorFromSchemaParams(outSchemas[j].getParamsList());
                    long acceptorMask = acceptors[i].getVNDataCompatibilityMask();                    
                    tmp = VNDataSchemaComparator.isConditionallyCompatible(outSchemas[j], schemaMask, acceptors[i].getVNDataSchemaInterface(), acceptorMask);
                    if(tmp) {
                        if(debug) System.out.println("test result: CONDITIONAL @ output schema CONDITIONALLY PASSED acceptor '"+acceptors[i]+"' vs. schema '"+outSchemas[j]+"'");
                        return LINK_DATA_STATUS_CONDITIONAL;
                    }
                }
            }
        }
                
        if(debug) System.out.println("test result: FAILED @ all acceptors rejected");
        return LINK_DATA_STATUS_ERROR;
    }
    
    
    public boolean isVisible() {
        return true; /* TODO */
    }

    public boolean willStartAction3(Port port) {
        boolean ret = willStartAction(port);
        System.out.println("WillStartAction? "+ret+"!");
        return ret;
    }

    public boolean willStartAction(Port port) {

        //System.out.println("WILLSTA "+this + " : "+port);
        if(this.isInput()) {
            if( ((Output)port).getOutputSaturation() != OutputSaturation.ok) return false;
            if( !((Input)this).isTriggering()) return false;
            for(Input input: this.getModuleBox().getInputs()) {
                if(input != this && input.isNecessary() && !input.isLinked()) return false;
            }
            return true;
       //     System.out.println(((Output)port).getElementSaturation());
            //return ((Output)port).getData().isValueChanged();//.getElementSaturation() == ElementSaturation.saturated;
        } else {
            return port.willStartAction(this);
      //      System.out.println(((Output)this).getElementSaturation());
            //return ((Output)this).getData().isValueChanged();//.getElementSaturation() == ElementSaturation.saturated;
        }
    }

    public void doTheMainResetKillAll() {
        setElementState(ElementState.passive);
        clearQueue();
    }

    
}
