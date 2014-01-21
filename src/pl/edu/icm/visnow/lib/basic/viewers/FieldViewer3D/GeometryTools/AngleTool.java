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
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.Vector;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.CalculableParameter;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.CalculableParamsPool;

/**
 * @author  Bartosz Borucki (babor@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */

public class AngleTool extends GeometryTool {
    private static int timestamper = 0;

    private final int pointRadius = 2;

    private Vector<Point> points = new Vector<Point>();;
    private Vector<Metadata> pointMetadata = new Vector<Metadata>();
    private Point currentPoint = null;
    private Metadata currentPointMetadata = null;

    private int clickCount = 0;

    private double angle = 0.0f;

    private DecimalFormat df = new DecimalFormat("###.##");



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

            if(points.size() == 2) {
                ((Graphics2D)g).drawString(df.format(angle), points.get(1).x+10, points.get(1).y+10);
            }
        }
    }

    public void mouseDragged(MouseEvent e) {
    }

    double[] v1 = new double[3];
    double[] v2 = new double[3];
    double len = 0.0;

    public void mouseMoved(MouseEvent e) {
        if(holding) {
            currentPoint = e.getPoint();
            if(metadata != null)
                currentPointMetadata = metadata.clone();
            
            if(points.size() == 2) {
                v1[0] = (double)(points.get(0).x - points.get(1).x);
                v1[1] = (double)(points.get(0).y - points.get(1).y);
                v2[0] = (double)(currentPoint.x - points.get(1).x);
                v2[1] = (double)(currentPoint.y - points.get(1).y);
                if(pointMetadata.size() == points.size() && (metadata.getObject() instanceof Integer || metadata.getObject() instanceof Float)) {
                    if(metadata.getObject() instanceof Integer) {
                        v1[2] = (Integer)pointMetadata.get(0).getObject() - (Integer)pointMetadata.get(1).getObject();
                        v2[2] = (Integer)currentPointMetadata.getObject() - (Integer)pointMetadata.get(1).getObject();
                    } else {
                        v1[2] = (Float)pointMetadata.get(0).getObject() - (Float)pointMetadata.get(1).getObject();
                        v2[2] = (Float)currentPointMetadata.getObject() - (Float)pointMetadata.get(1).getObject();
                    }
                } else {
                    v1[2] = 0;
                    v2[2] = 0;
                }


                len = Math.sqrt(v1[0]*v1[0] + v1[1]*v1[1] + v1[2]*v1[2])*Math.sqrt(v2[0]*v2[0] + v2[1]*v2[1] + v2[2]*v2[2]);
                if(len == 0) {
                    angle = 0;
                } else {
                    angle = 180.0 *Math.acos((v1[0]*v2[0] + v1[1]*v2[1] + v1[2]*v2[2])/len)/Math.PI;
                }
            }

            fireGeometryToolRepaintNeeded();
        }
    }

    public void mouseClicked(MouseEvent e) {
        if(e.getClickCount() > 1)
            return;

        if(clickCount == 0) {
            points.clear();
            pointMetadata.clear();
        }
        clickCount++;

        points.add(e.getPoint());
        if(metadata != null)
            pointMetadata.add(metadata.clone());

        if(clickCount == 3) {
            currentPoint = null;
            holding = false;
            clickCount = 0;

            //metadata = new Metadata("angle", new Float(angle), new Point(points.get(1).x+10, points.get(1).y+15));
            fireGeometryToolStateChanged();
        } else {
            currentPoint = e.getPoint();
            holding = true;
            fireGeometryToolRepaintNeeded();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
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
        return CalculableParamsPool.getAngleFrom3PointsCalculable(""+(timestamper++));
    }

    @Override
    public Metadata getCalculableMetadata() {
        if(points.size() != 3 && pointMetadata.size() != 3)
            return null;

        return pointMetadata.get(1);
    }

    @Override
    public int getMinimumNPoints() {
        return 3;
    }
}
