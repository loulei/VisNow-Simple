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

package pl.edu.icm.visnow.lib.basic.readers.ReadFluent;

/**
 * @author Bartosz Borucki (babor@icm.edu.pl) 
 * University of Warsaw
 * Interdisciplinary Centre for Mathematical and Computational Modelling * 
 */
public class FluentFace implements Cloneable {
    
    //Fluent face types
    public static final int FACE_TYPE_MIXED = 0;
    public static final int FACE_TYPE_LINEAR = 2;
    public static final int FACE_TYPE_TRIANGULAR = 3;
    public static final int FACE_TYPE_QUADRILATERAL = 4;
    public static final int FACE_TYPE_POLYGONAL = 5;
    
    //FLuent Boundary Condition types
    public static final int BC_TYPE_INTERIOR = 2;
    public static final int BC_TYPE_WALL = 3;
    public static final int BC_TYPE_PRESSURE_INLET = 4;
    public static final int BC_TYPE_PRESSURE_OUTLET = 5;
    public static final int BC_TYPE_SYMMETRY = 7;
    public static final int BC_TYPE_PERIODIC_SHADOW = 8;
    public static final int BC_TYPE_PRESSURE_FAR_FIELD = 9;
    public static final int BC_TYPE_VELOCITY_INLET = 10;
    public static final int BC_TYPE_PERIODIC = 12;
    public static final int BC_TYPE_FAN = 14;
    public static final int BC_TYPE_MASS_FLOW_INLET = 20;
    public static final int BC_TYPE_INTERFACE = 24;
    public static final int BC_TYPE_PARENT = 31;
    public static final int BC_TYPE_OUTFLOW = 36;
    public static final int BC_TYPE_AXIS = 37;
    
    

    private int type = FACE_TYPE_MIXED;
    private int zone;
    private int[] nodes = null;
    private int c0;
    private int c1;
    private int periodicShadow;
    private int parent;
    private int child;
    private int interfaceFaceParent;
    private int interfaceFaceChild;
    private int ncgParent;
    private int ncgChild;
    
    public FluentFace() {
        this.periodicShadow = 0;
        this.parent = 0;
        this.child = 0;
        this.interfaceFaceParent = 0;
        this.ncgParent = 0;
        this.ncgChild = 0;
        this.interfaceFaceChild = 0;                
    }
    
    public FluentFace(int type, int nNodes) {
        this();
        this.type = type;
        this.nodes = new int[nNodes];        
    }
    
    public FluentFace(int type, int[] nodes) {
        this();
        this.type = type;
        this.nodes = nodes;   
    }

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * @return the zone
     */
    public int getZone() {
        return zone;
    }

    /**
     * @param zone the zone to set
     */
    public void setZone(int zone) {
        this.zone = zone;
    }

    /**
     * @return the nodes
     */
    public int[] getNodes() {
        return nodes;
    }

    public int getNode(int i) {
        return nodes[i];
    }
    
    public void setNodes(int[] nodes) {
        this.nodes = nodes;
    }

    /**
     * @return the c0
     */
    public int getC0() {
        return c0;
    }

    /**
     * @param c0 the c0 to set
     */
    public void setC0(int c0) {
        this.c0 = c0;
    }

    /**
     * @return the c1
     */
    public int getC1() {
        return c1;
    }

    /**
     * @param c1 the c1 to set
     */
    public void setC1(int c1) {
        this.c1 = c1;
    }

    /**
     * @return the periodicShadow
     */
    public int getPeriodicShadow() {
        return periodicShadow;
    }

    /**
     * @param periodicShadow the periodicShadow to set
     */
    public void setPeriodicShadow(int periodicShadow) {
        this.periodicShadow = periodicShadow;
    }

    /**
     * @return the parent
     */
    public int getParent() {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(int parent) {
        this.parent = parent;
    }

    /**
     * @return the child
     */
    public int getChild() {
        return child;
    }

    /**
     * @param child the child to set
     */
    public void setChild(int child) {
        this.child = child;
    }

    /**
     * @return the interfaceFaceParent
     */
    public int getInterfaceFaceParent() {
        return interfaceFaceParent;
    }

    /**
     * @param interfaceFaceParent the interfaceFaceParent to set
     */
    public void setInterfaceFaceParent(int interfaceFaceParent) {
        this.interfaceFaceParent = interfaceFaceParent;
    }

    /**
     * @return the interfaceFaceChild
     */
    public int getInterfaceFaceChild() {
        return interfaceFaceChild;
    }

    /**
     * @param interfaceFaceChild the interfaceFaceChild to set
     */
    public void setInterfaceFaceChild(int interfaceFaceChild) {
        this.interfaceFaceChild = interfaceFaceChild;
    }

    /**
     * @return the ncgParent
     */
    public int getNcgParent() {
        return ncgParent;
    }

    /**
     * @param ncgParent the ncgParent to set
     */
    public void setNcgParent(int ncgParent) {
        this.ncgParent = ncgParent;
    }

    /**
     * @return the ncgChild
     */
    public int getNcgChild() {
        return ncgChild;
    }

    /**
     * @param ncgChild the ncgChild to set
     */
    public void setNcgChild(int ncgChild) {
        this.ncgChild = ncgChild;
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        FluentFace out = (FluentFace) super.clone();
        out.setType(type);
        out.setZone(zone);
        out.setNodes(nodes.clone());
        out.setC0(c0);
        out.setC1(c1);
        out.setPeriodicShadow(periodicShadow);
        out.setParent(parent);
        out.setChild(child);
        out.setInterfaceFaceParent(interfaceFaceParent);
        out.setInterfaceFaceChild(interfaceFaceChild);
        out.setNcgChild(ncgChild);
        out.setNcgParent(ncgParent);
        return out;        
    }
    
}
