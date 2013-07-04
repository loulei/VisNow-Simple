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

import pl.edu.icm.visnow.lib.types.VNDataSchema;

/**
 *
 * @author Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class OutputEgg {


    public static final int HIDDEN = 4;

    protected Class type;
    protected String name;
    protected VNDataSchema[] schemas = null;
    protected String description = null;
    protected int maxConnections = -1;

    public String getName() {return name;}
    public Class getType() {return type;}
    public String getDescription() {return description;}
    public void setDescription(String desc) {this.description = desc;}

    public OutputEgg(String name, Class type) {
        this.name = name;
        this.type = type;
    }

    public OutputEgg(String name, Class type, int maxConnections) {
        this.name = name;
        this.type = type;
        this.maxConnections = maxConnections;
    }

    public OutputEgg(String name, Class type, String description, VNDataSchema[] schemas) {
        this.name = name;
        this.type = type;
        this.schemas = schemas;
        this.description = description;
    }

    public OutputEgg(String name, Class type, int maxConnections, String description, VNDataSchema[] schemas) {
        this.name = name;
        this.type = type;
        this.schemas = schemas;
        this.description = description;
        this.maxConnections = maxConnections;
    }
    
    public VNDataSchema[] getVNDataSchemas() {
        return this.schemas;
    }
    
    public int getMaxConnections() {
        return this.maxConnections;
    }
}
