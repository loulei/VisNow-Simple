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

package pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.ViewPanels;


/**
 *
 * @author vis
 */
public class CustomOrthoPlaneChangedViewPanelEvent extends ViewPanelEvent {
    private int axis = 0;
    private float[] point = new float[3];
    private  float[][] vectors = new float[3][3];

    public CustomOrthoPlaneChangedViewPanelEvent(Object source, int axis, float[] point, float[][] vectors) {
        super(source);
        this.axis = axis;
        for (int i = 0; i < 3; i++) {
            this.point[i] = point[i];
            for (int j = 0; j < 3; j++) {
                this.vectors[i][j] = vectors[i][j];
            }
        }
    }

    /**
     * @return the point
     */
    public float[] getPoint() {
        return point;
    }

    /**
     * @param point the point to set
     */
    public void setPoint(float[] point) {
        for (int i = 0; i < 3; i++) {
            this.point[i] = point[i];
        }
    }

    /**
     * @return the vector
     */
    public float[][] getVectors() {
        return vectors;
    }

    /**
     * @param vector the vector to set
     */
    public void setVectors(float[][] vectors) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.vectors[i][j] = vectors[i][j];
            }
        }
    }

    /**
     * @return the axis
     */
    public int getAxis() {
        return axis;
    }

    /**
     * @param axis the axis to set
     */
    public void setAxis(int axis) {
        this.axis = axis;
    }


}
