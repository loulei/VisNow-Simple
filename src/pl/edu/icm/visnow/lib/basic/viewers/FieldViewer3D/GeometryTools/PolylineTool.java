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
import java.util.Vector;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.CalculableParameter;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.CalculableParamsPool;

/**
 * @author  Bartosz Borucki (babor@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */

public class PolylineTool extends GeometryTool {
    private static int timestamper = 0;
    
    private final int pointRadius = 2;

    private Vector<Point> points = new Vector<Point>();;
    private Vector<Metadata> pointMetadata = new Vector<Metadata>();
    private Point currentPoint = null;

    private boolean holding = false;
    private boolean firstClick = true;


    @Override
    public void paint(Graphics g) {
        if(holding && points.size() > 0) {
            g.setColor(Color.RED);
            for (int i = 0; i < points.size()-1; i++) {
                g.fillRect(points.get(i).x-pointRadius, points.get(i).y-pointRadius, 2*pointRadius+1, 2*pointRadius+1);
                g.drawLine(points.get(i).x, points.get(i).y, points.get(i+1).x, points.get(i+1).y);
            }
            g.fillRect(points.get(points.size()-1).x-pointRadius, points.get(points.size()-1).y-pointRadius, 2*pointRadius+1, 2*pointRadius+1);
            g.drawLine(points.get(points.size()-1).x, points.get(points.size()-1).y, currentPoint.x, currentPoint.y);
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
        if(firstClick) {
            points.clear();
            pointMetadata.clear();
            firstClick = false;
        }

        if(e.getClickCount() > 1) {
            //double click - end polyline
            currentPoint = null;
            holding = false;
            firstClick = true;
            metadata = null; //TODO mozna dodac dlugosc calkowita
            fireGeometryToolStateChanged();
        } else {
            //single click - next point
            points.add(e.getPoint());
            if(metadata != null)
                pointMetadata.add(metadata.clone());

            currentPoint = e.getPoint();
            holding = true;
        }
        fireGeometryToolRepaintNeeded();
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
        if(points.isEmpty())
            return null;

        int[][] out = new int[points.size()][2];
        for (int i = 0; i < points.size(); i++) {
            out[i][0] = points.get(i).x;
            out[i][1] = points.get(i).y;
        }
        return out;
    }

    @Override
    public Metadata[] getPointMetadata() {
        if(points.isEmpty() || pointMetadata.isEmpty())
            return null;

        Metadata[] out = new Metadata[pointMetadata.size()];
        for (int i = 0; i < pointMetadata.size(); i++) {
            out[i] = pointMetadata.get(i);
        }
        return out;
    }

    @Override
    public int[][] getConnections() {
        if(points.size() < 2)
            return null;

        int[][] out = new int[points.size()-1][2];
        for (int i = 0; i < points.size()-1; i++) {
            out[i][0] = i;
            out[i][1] = i+1;
        }
        return out;
    }

    @Override
    public CalculableParameter getCalculable() {
        return CalculableParamsPool.getLengthFromPolylinePointsCalculable("length"+(timestamper++));
//        return new CalculableParameter("length"+(timestamper++)) {
//            @Override
//            public float getValue() {
//                if(this.pointDescriptors.size() < 2)
//                    return 0;
//
//                double len = 0;
//                double tmp;
//                for (int i = 1; i < pointDescriptors.size(); i++) {
//                    tmp = 0;
//                    for (int j = 0; j < 3; j++) {
//                        tmp += pointDescriptors.get(i).getWorldCoords()[j]*pointDescriptors.get(i).getWorldCoords()[j];
//                    }
//                    len += Math.sqrt(tmp);
//                }
//                return (float)len;
//            }
//
//            @Override
//            public int getLocationPointIndex() {
//                return (pointDescriptors.size()-1);
//            }
//        };
    }

    @Override
    public Metadata getCalculableMetadata() {
        if(points.isEmpty() && pointMetadata.size() != points.size())
            return null;

        return pointMetadata.get(pointMetadata.size()-1);
    }

    @Override
    public int getMinimumNPoints() {
        return 2;
    }

}

