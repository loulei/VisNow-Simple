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
 * @author  Bartosz Borucki (babor@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */

public class ConnectionDescriptor {
    private PointDescriptor p1 = null;
    private PointDescriptor p2 = null;
    private String name = "";

    public ConnectionDescriptor(String name, PointDescriptor p1, PointDescriptor p2) {
        this.name = name;
        this.p1 = p1;
        this.p2 = p2;
    }

    public float getLength() {
        if(p1 == null || p2 == null)
            return 0;

        float out = 0;
        float[] c1 = p1.getWorldCoords();
        float[] c2 = p2.getWorldCoords();
        float[] dc = new float[3];
        for (int i = 0; i < 3; i++) {
            dc[i] = c1[i] - c2[i];
            out += dc[i]*dc[i];
        }
        out = (float)Math.sqrt(out);
        return out;
    }

    /**
     * @return the p1
     */
    public PointDescriptor getP1() {
        return p1;
    }

    public float[] getP1WorldCoordsDistanced(float scale) {
        float[] c1 = p1.getWorldCoords();
        float[] c2 = p2.getWorldCoords();
        float[] v = new float[3];
        for (int i = 0; i < 3; i++) {
            v[i] = scale*(c2[i] - c1[i]);
        }

        float[] out1 = new float[3];
        for (int i = 0; i < 3; i++) {
            out1[i] = c1[i] - v[i];
        }
        return out1;
    }

    public float[] getP2WorldCoordsDistanced(float scale) {
        float[] c1 = p1.getWorldCoords();
        float[] c2 = p2.getWorldCoords();
        float[] v = new float[3];
        for (int i = 0; i < 3; i++) {
            v[i] = scale*(c2[i] - c1[i]);
        }

        float[] out2 = new float[3];
        for (int i = 0; i < 3; i++) {
            out2[i] = c2[i] + v[i];
        }
        return out2;
    }

    /**
     * @param p1 the p1 to set
     */
    public void setP1(PointDescriptor p1) {
        this.p1 = p1;
    }

    /**
     * @return the p2
     */
    public PointDescriptor getP2() {
        return p2;
    }

    /**
     * @param p2 the p2 to set
     */
    public void setP2(PointDescriptor p2) {
        this.p2 = p2;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }



}
