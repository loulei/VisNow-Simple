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

import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.CalculableParameter;


/**
 *
 * @author vis
 */
public class PCCAddedOrthoPanelEvent extends ViewPanelEvent {
    private int[][] points = null;
    private int[][] connections = null;
    private CalculableParameter calculable = null;

    public PCCAddedOrthoPanelEvent(Object source, int[][] points, int[][] connections, CalculableParameter calculable) {
        super(source);
        if(points != null) {
            int n = points.length;
            this.points = new int[n][3];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < 3; j++) {
                    this.points[i][j] = points[i][j];
                }
            }
        }

        if(connections != null) {
            int n = connections.length;
            this.connections = new int[n][2];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < 2; j++) {
                    this.connections[i][j] = connections[i][j];
                }
            }
        }

        this.calculable = calculable;
    }

    /**
     * @return the points
     */
    public int[][] getPoints() {
        return points;
    }

    /**
     * @param points the points to set
     */
    public void setPoints(int[][] points) {
        this.points = points;
    }

    /**
     * @return the connections
     */
    public int[][] getConnections() {
        return connections;
    }

    /**
     * @param connections the connections to set
     */
    public void setConnections(int[][] connections) {
        this.connections = connections;
    }

    /**
     * @return the calculable
     */
    public CalculableParameter getCalculable() {
        return calculable;
    }

    /**
     * @param calculable the calculable to set
     */
    public void setCalculable(CalculableParameter calculable) {
        this.calculable = calculable;
    }


}
