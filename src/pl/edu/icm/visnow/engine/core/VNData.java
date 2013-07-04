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

//import pl.edu.icm.visnow.engine.main.ModuleBox;


/**
 *
 * @author  Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class VNData
{

    private static boolean debug = false;

    protected Object value;
    protected Class type;
    protected ModuleBoxFace sourceModule;
    protected Output sourceOutput;

    protected boolean valueChanged;

    public Object getValue() {
        return value;
    }

    private void checkSaturation() {
        sourceOutput.checkSaturation();
        for(LinkFace link: sourceOutput.getLinks()) {
            link.getInput().checkSaturation();
        }
    }

    public boolean setValue(Object value) {
        if(debug) System.out.println("set value");
        valueChanged = true;
        if(value==null) {this.value = null; checkSaturation(); return true;}
        if(type.isInstance(value)) {this.value = value; checkSaturation(); return true;}
        //System.out.println("vns | type = "+type);
        //System.out.println("vns | object = "+value);
        if(debug) System.out.println("value not set;");
        checkSaturation();
        return false;
    }
    
//    public VNData() {
//        this(null, null, null, null);
//    }
//
//    public VNData(ModuleBox module, Output output) {
//        this(module, output, output.getType());
//    }
//
    public VNData(ModuleBoxFace module, Output output, Class type) {
        this(module, output, type, null);
    }

    public VNData(ModuleBoxFace module, Output output, Class type, Object value) {
        this.sourceModule = module;
        this.sourceOutput = output;
        this.type = type;
        if(value==null || !(type.isInstance(value)))
            this.value = null;
        else
            this.value = value;
        this.valueChanged = true;
    }



    //<editor-fold defaultstate="collapsed" desc=" Getters/Setters ">
    public boolean isValueChanged() {
        return valueChanged;
    }

    public void markValueOld() {
        valueChanged = false;
    }

    public Class getType() {
        return type;
    }

    //public void setType(Class type) {
    //    this.type = type;
    //}

    public ModuleBoxFace getSourceModule() {
        return sourceModule;
    }

    //public void setSourceModule(ModuleBox sourceModule) {
    //    this.sourceModule = sourceModule;
    //}

    public Output getSourceOutput() {
        return sourceOutput;
    }

    public void restart() {
        this.valueChanged = true;
    }

    //public void setSourceOutput(Output sourceOutput) {
    //    this.sourceOutput = sourceOutput;
    //}
    //</editor-fold>
}
