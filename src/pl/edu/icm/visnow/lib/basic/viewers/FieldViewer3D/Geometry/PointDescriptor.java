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

package pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry;

/**
 *
 * @author Krzysztof S. Nowinski
 *   <p>   University of Warsaw, ICM
 */
public class PointDescriptor {

    private static int id = 0;
    protected String name = "";
    protected int[] indices;
    protected float[] worldCoords;
    protected float[] physicalCoords;

    public PointDescriptor(String s, int[] p, float[] c) {
        name = s;
        indices = p;
        worldCoords = c;
    }

    public PointDescriptor(int[] p, float[] c) {
        name = "p" + nextPoint();
        indices = p;
        worldCoords = c;
    }

    public PointDescriptor(int n, int[] p, float[] c) {
        name = "p" + n;
        indices = p;
        worldCoords = c;
    }

    public int[] getIndices() {
        return indices;
    }

    public void setIndices(int[] indices) {
        this.indices = indices;
    }

    public float[] getWorldCoords() {
        return worldCoords;
    }

    public void setWorldCoords(float[] coords) {
        this.physicalCoords = this.worldCoords = coords;
    }

    public float[] getPhysicalCoords() {
        return physicalCoords;
    }

    public void setPhysicalCoords(float[] physicalCoords) {
        this.physicalCoords = physicalCoords;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static int nextPoint() {
        return id++;
    }

    public static void resetPointCounter() {
        id = 0;
    }

    public static void setPointCounter(int n) {
        id = n;
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean isDependant() {
        return false;
    }
}
