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

public class RadiusTool extends GeometryTool {

    private final int pointRadius = 3;

    private Point startPoint = null;
    private Point currentPoint = null;
    private Point endPoint = null;

    @Override
    public void paint(Graphics g) {
        if(holding && startPoint != null && currentPoint != null) {
            int[][] pp = new int[2][2];
            pp[0][0] = startPoint.x;
            pp[0][1] = startPoint.y;
            pp[1][0] = currentPoint.x;
            pp[1][1] = currentPoint.y;
            int[] c = new int[2];
            c[0] = (startPoint.x+currentPoint.x)/2;
            c[1] = (startPoint.y+currentPoint.y)/2;
            float d = (float)(Math.sqrt( (pp[1][0]-pp[0][0])*(pp[1][0]-pp[0][0]) + (pp[1][1]-pp[0][1])*(pp[1][1]-pp[0][1]) ));
            int cx = (int)Math.round((float)c[0]-d/2.0f);
            int cy = (int)Math.round((float)c[1]-d/2.0f);
            int cd = (int)Math.round(d);
            
            g.setColor(Color.YELLOW);
            g.drawLine(pp[0][0], pp[0][1], c[0], c[1]);

            g.setColor(Color.RED);
            g.drawLine(c[0], c[1], pp[1][0], pp[1][1]);
            g.drawOval(cx, cy, cd, cd);
            g.fillRect(c[0]-pointRadius, c[1]-pointRadius, 2*pointRadius+1, 2*pointRadius+1);
            g.fillRect(pp[0][0]-pointRadius, pp[0][1]-pointRadius, 2*pointRadius+1, 2*pointRadius+1);
        }
    }

    public void mouseDragged(MouseEvent e) {
        if(holding) {
            currentPoint = e.getPoint();
            fireGeometryToolRepaintNeeded();
        }
    }

    public void mouseMoved(MouseEvent e) {

    }

    public void mouseClicked(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e) {
        holding = true;
        this.startPoint = e.getPoint();
        this.currentPoint = e.getPoint();
        fireGeometryToolRepaintNeeded();
    }

    public void mouseReleased(MouseEvent e) {
        holding = false;
        this.endPoint = e.getPoint();
        fireGeometryToolRepaintNeeded();
        metadata = null;
        fireGeometryToolStateChanged();
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
        return holding;
    }

    @Override
    public int[][] getPoints() {
        if(startPoint == null || endPoint == null)
            return null;

        int[][] out = new int[2][2];
        out[0][0] = startPoint.x;
        out[0][1] = startPoint.y;
        out[1][0] = (endPoint.x+startPoint.x)/2;
        out[1][1] = (endPoint.y+startPoint.y)/2;
        return out;
    }

    @Override
    public Metadata[] getPointMetadata() {
        return null;
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
