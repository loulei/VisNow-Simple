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

package pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.GeometryTools;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.CalculableParameter;

/**
 * @author  Bartosz Borucki (babor@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */

public class LineTool extends GeometryTool {

    private final int pointRadius = 2;

    private Point startPoint = null;
    private Point currentPoint = null;
    private Point endPoint = null;

    private Metadata startPointMetadata = null;
    private Metadata endPointMetadata = null;

    private boolean holding = false;


    @Override
    public void paint(Graphics g) {
        if(holding && startPoint != null && currentPoint != null) {
            g.setColor(Color.RED);
            g.fillRect(startPoint.x-pointRadius, startPoint.y-pointRadius, 2*pointRadius+1, 2*pointRadius+1);
            g.drawLine(startPoint.x, startPoint.y, currentPoint.x, currentPoint.y);
        }
    }

    public void mouseDragged(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
        if(holding) {
            currentPoint = e.getPoint();
            fireGeometryToolRepaintNeeded();
        }
    }

    public void mouseClicked(MouseEvent e) {
        if(holding) {
            holding = false;
            this.endPoint = e.getPoint();
            if(metadata != null)
                this.endPointMetadata = metadata.clone();
            fireGeometryToolRepaintNeeded();
            metadata = null;
            fireGeometryToolStateChanged();
        } else {
            holding = true;
            this.startPoint = e.getPoint();
            if(metadata != null)
                this.startPointMetadata = metadata.clone();
            this.currentPoint = e.getPoint();
            fireGeometryToolRepaintNeeded();
        }
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

    @Override
    public Cursor getCursor() {
        return Cursor.getDefaultCursor();
    }

    @Override
    public boolean isMouseWheelBlocking() {
        return false;
    }

    @Override
    public int[][] getPoints() {
        if(startPoint == null || endPoint == null)
            return null;

        int[][] out = new int[2][2];
        out[0][0] = startPoint.x;
        out[0][1] = startPoint.y;
        out[1][0] = endPoint.x;
        out[1][1] = endPoint.y;
        return out;
    }

    @Override
    public Metadata[] getPointMetadata() {
        if(startPoint == null || endPoint == null)
            return null;

        Metadata[] out = new Metadata[2];
        out[0] = startPointMetadata;
        out[1] = endPointMetadata;
        return out;
    }

    @Override
    public int[][] getConnections() {
        if(startPoint == null || endPoint == null)
            return null;

        int[][] out = new int[1][2];
        out[0][0] = 0;
        out[0][1] = 1;
        return out;
        
    }

    @Override
    public CalculableParameter getCalculable() {
        return null;
    }

    @Override
    public Metadata getCalculableMetadata() {
        return null;
    }

    @Override
    public int getMinimumNPoints() {
        return 2;
    }
    
}
