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

/**
 *
 * @author Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class LinkName {
    private String outputModule;
    private String outputPort;
    private String inputModule;
    private String inputPort;

    public LinkName(String outputModule, String outputPort, String inputModule, String inputPort) {
        this.outputModule = outputModule;
        this.outputPort = outputPort;
        this.inputModule = inputModule;
        this.inputPort = inputPort;
    }


    /**
     * @return the outputModule
     */
    public String getOutputModule() {
        return outputModule;
    }

    /**
     * @return the outputPort
     */
    public String getOutputPort() {
        return outputPort;
    }

    /**
     * @return the inputModule
     */
    public String getInputModule() {
        return inputModule;
    }

    /**
     * @return the inputPort
     */
    public String getInputPort() {
        return inputPort;
    }


    @Override
    public String toString() {
        return "["+outputModule+":"+outputPort+"]->["+inputModule+":"+inputPort+"]";
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof LinkName) {
            LinkName name = (LinkName) o;
            if(!this.outputModule.equals(name.getOutputModule())) return false;
            if(!this.outputPort.equals(name.getOutputPort())) return false;
            if(!this.inputModule.equals(name.getInputModule())) return false;
            if(!this.inputPort.equals(name.getInputPort())) return false;
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.outputModule != null ? this.outputModule.hashCode() : 0);
        hash = 29 * hash + (this.outputPort != null ? this.outputPort.hashCode() : 0);
        hash = 29 * hash + (this.inputModule != null ? this.inputModule.hashCode() : 0);
        hash = 29 * hash + (this.inputPort != null ? this.inputPort.hashCode() : 0);
        return hash;
    }
}
