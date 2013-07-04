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

import java.util.ArrayList;
import java.util.HashMap;
import pl.edu.icm.visnow.datasets.cells.Cell;

/**
 * @author Bartosz Borucki (babor@icm.edu.pl) 
 * University of Warsaw
 * Interdisciplinary Centre for Mathematical and Computational Modelling * 
 */
public class FluentCell implements Cloneable {
    //Fluent Element Types
    public static final int ELEMENT_TYPE_MIXED = 0;            // nodes/cell   faces/cell   visnow id
    public static final int ELEMENT_TYPE_TRIANGULAR = 1;       // 3            3            2
    public static final int ELEMENT_TYPE_TETRAHEDRAL = 2;      // 4            4            4
    public static final int ELEMENT_TYPE_QUADRILATERAL = 3;    // 4            4            3
    public static final int ELEMENT_TYPE_HEXAHEDRAL = 4;       // 8            6            7
    public static final int ELEMENT_TYPE_PYRAMID = 5;          // 5            5            5
    public static final int ELEMENT_TYPE_WEDGE = 6;            // 6            5            6
    public static final int ELEMENT_TYPE_POLYHEDRAL = 7;        // NN           NF          N/A
    
    public static final HashMap<Integer, Integer> mapTypeToVisNow= new HashMap<Integer, Integer>();
    static {
        mapTypeToVisNow.put(ELEMENT_TYPE_TRIANGULAR, Cell.TRIANGLE);
        mapTypeToVisNow.put(ELEMENT_TYPE_TETRAHEDRAL, Cell.TETRA);
        mapTypeToVisNow.put(ELEMENT_TYPE_QUADRILATERAL, Cell.QUAD);
        mapTypeToVisNow.put(ELEMENT_TYPE_HEXAHEDRAL, Cell.HEXAHEDRON);
        mapTypeToVisNow.put(ELEMENT_TYPE_PYRAMID, Cell.PYRAMID);
        mapTypeToVisNow.put(ELEMENT_TYPE_WEDGE, Cell.PRISM);
    }
    
    private int type;
    private int zone;
    private ArrayList<Integer> faces = new ArrayList<Integer>();
    private int parent;
    private int child;
    private int[] nodes;

    public FluentCell() {
        this.parent = 0;
        this.child = 0;
    }

    public FluentCell(int type, int zone, int parent, int child) {
        this.type = type;
        this.zone = zone;
        this.parent = parent;
        this.child = child;
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
     * @return the faces
     */
    public ArrayList<Integer> getFaces() {
        return faces;
    }

    public Integer getFace(int i) {
        return faces.get(i);
    }

    public FluentFace getFace(int i, ArrayList<FluentFace> facesList) {
        return facesList.get(faces.get(i));
    }
    
    public void setFaces(ArrayList<Integer> faces) {
        this.faces = faces;
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
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        FluentCell out = (FluentCell)super.clone();
        out.setType(type);
        out.setZone(zone);
        out.setFaces(faces);
        out.setParent(parent);
        out.setChild(child);
        out.setNodes(nodes);                
        return out;        
    }
    
}
