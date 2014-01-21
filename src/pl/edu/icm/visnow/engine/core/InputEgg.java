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

import pl.edu.icm.visnow.lib.types.VNDataAcceptor;

/**
 * Lightweight class used to describe module's inputs.
 *
 * Contains the following information on the input:
 * <ul>
 * <li> name,
 * <li> class of the stored data,
 * <li> input type,
 * <li> default value,
 * <li> number of allowed connections.
 * </ul>
 *
 * There are four modifiers for the input type:
 * <ul>
 * <li> <b>TRIGGERING</b>
 * <li> <b>NECESSARY</b>
 * <li> <b>HIDDEN</b>
 * <li> <b>NORMAL</b>
 * </ul>
 *
 * @author Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class InputEgg {

    //TODO: kopiowanie defaultValue przy tworzeniu inputu

    /**
     * Default modifier.
     * Describes a non-triggering, non-necessary, visible input.
     */
    public static final int NORMAL = 0;
    /**
     * Modifier for triggering inputs.
     */
    public static final int TRIGGERING = 1;
    /**
     * Modifier for necessary inputs.
     */
    public static final int NECESSARY = 2;
    /**
     * Modifier for initially hidden inputs.
     */
    public static final int HIDDEN = 4;

    /**
     *
     * @param name
     * @param type
     * @param modifiers
     */
    public InputEgg(String name, Class type, int modifiers) {
        this(name, type, modifiers, 0, -1, null);
    }

    public InputEgg(String name, Class type, int modifiers, VNDataAcceptor[] acceptors) {
        this(name, type, modifiers, 0, -1, null, acceptors);
    }

    private VNDataAcceptor[] acceptors = null;

    public VNDataAcceptor[] getVNDataAcceptors() {
        return acceptors;
    }

    /**
     *
     * @param name
     * @param type
     * @param modifiers
     * @param defaultvalue
     */
    public InputEgg(String name, Class type, int modifiers, Object defaultvalue) {
        this(name, type, modifiers, 0, -1, defaultvalue);
    }

    /**
     *
     * @param name
     * @param type
     * @param modifiers
     * @param minConnections
     * @param maxConnections
     */
    public InputEgg(String name, Class type, int modifiers, int minConnections, int maxConnections) {
        this(name, type, modifiers, minConnections, maxConnections, null);
    }

    public InputEgg(String name, Class type, int modifiers, int minConnections, int maxConnections, String description, VNDataAcceptor[] acceptors) {
        this(name, type, modifiers, minConnections, maxConnections, null, description, acceptors);
    }

    /**
     *
     * @param name Name of the input.
     * @param type Class of the data maintained in the input.
     * @param modifiers Modifiers describing the input behavior.
     * The value should be either a byte alternative of some of the values
     * {@link InputEgg#TRIGGERING}, {@link InputEgg#NECESSARY} and {@link InputEgg#HIDDEN},
     * or the {@link InputEgg#NORMAL} value.
     * @param minConnections Minimum number of connections necessary for the input
     * work. It's not the same as the NECESSARY modifyer. If a port is not
     * necessary, the number of connections will affect only the port saturation,
     * not the work of the module (i.e. the module might be saturated
     * and respond to the work flow even if this port is not saturated).
     * However, if the port is necessary then the minimum number of connections
     * must be reached in order for the module to work.
     * @param maxConnections Maximum number of possible connections to this port.
     * -1 means infinity (precisely, Integer.MAX_VALUE).
     * @param defaultvalue Default object returned as a value of the port if
     * there is no connection to this port.
     *
     */
    public InputEgg(String name, Class type, int modifiers, int minConnections, int maxConnections, Object defaultvalue) {
        this.name = name;
        this.type = type;
        this.triggering = (modifiers & TRIGGERING)!=0;
        this.necessary = (modifiers & NECESSARY)!=0;
        this.hidden = (modifiers & HIDDEN)!=0;
        this.minConnections = minConnections;
        this.maxConnections = maxConnections;
    }

    public InputEgg(String name, Class type, int modifiers, int minConnections, int maxConnections, Object defaultvalue, String description, VNDataAcceptor[] acceptors) {
        this.name = name;
        this.type = type;
        this.triggering = (modifiers & TRIGGERING)!=0;
        this.necessary = (modifiers & NECESSARY)!=0;
        this.hidden = (modifiers & HIDDEN)!=0;
        this.minConnections = minConnections;
        this.maxConnections = maxConnections;
        this.acceptors = acceptors;
        this.description = description;
    }


    private String name;
    private Class type;
    private boolean triggering;
    private boolean necessary;
    private boolean hidden;
    private int minConnections;
    private int maxConnections;
    private Object defaultValue;
    private String description = null;

    /**
     * Gets the name of the input.
     * @return The name of the input.
     */
    public String getName() {return name;}
    /**
     * Gets the class of the data that can be stored in the input.
     * @return The class of the input data.
     */
    public Class getType() {return type;}
    /**
     * Checks whether the input is triggering.
     * @return True iff the input is triggering.
     */
    public boolean isTriggering() {return triggering;}
    /**
     * Checks whether the input is necessary.
     * @return True iff the input is necessary.
     */
    public boolean isNecessary() {return necessary;}
    /**
     * Checks whether the input is initially hidden.
     * @return True iff the input is initially hidden.
     */
    public boolean isHidden() {return hidden;}
    /**
     * Returns the minimal number of connections.
     * @return
     */
    public int getMinConnections() {return minConnections;}
    public int getMaxConnections() {return maxConnections;}
    public Object getDefaultValue() {return defaultValue;}
    /**
     * Gets the description of the input.
     * @return The description of the input.
     */
    public String getDescription() {return description;}


}
