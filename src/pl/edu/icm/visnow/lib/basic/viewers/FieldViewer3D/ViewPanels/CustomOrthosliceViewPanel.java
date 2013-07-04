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

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.ConnectionDescriptor;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.PointDescriptor;

/**
 * @author Bartosz Borucki (babor@icm.edu.pl) University of Warsaw,
 * Interdisciplinary Centre for Mathematical and Computational Modelling
 */
public class CustomOrthosliceViewPanel extends ViewPanel implements ComponentListener, MouseListener, MouseMotionListener, MouseWheelListener {

    private BufferedImage dataImage = null;
    private BufferedImage overlayImage = null;
    private float overlayOpacity = 0.5f;
    private float imageScale = 1.0f;
    //private boolean autoScaling = true;
    private float imageSizeWidth = 1.0f;
    private float imageSizeHeight = 1.0f;
    private float uppW = 1.0f, uppH = 1.0f, upp = 1.0f;
    private int w = 0;
    private int h = 0;
    private float dw = 1.0f;
    private float dh = 1.0f;
    private boolean paintSliceInfo = false;
    private String sliceInfoString = "axis";
    private float[] customOrthoPlanesPoint = {0.0f, 0.0f, 0.0f};
    private float[][] customOrthoPlanesVectors = {{1.0f, 0.0f, 0.0f}, {0.0f, 1.0f, 0.0f}, {0.0f, 0.0f, 1.0f}};
    private int axis = 0;
    private float[][] extents = null;
    private float[][] base = null;
    private float[][] localBase = null;    
    private ArrayList<PointDescriptor> points = new ArrayList<PointDescriptor>();
    private ArrayList<ConnectionDescriptor> pointConnections = new ArrayList<ConnectionDescriptor>();
    private int[] selectedPoints = null;
    private boolean paintPointLabels = true;
    private boolean paintConnections = false;
    private boolean paintDistances = false;
    private int sliceLinesMode = OrthosliceViewPanel.SLICE_LINES_COLORED;
    private Color borderColor = Color.BLUE;
    private Color horizColor = Color.BLUE;
    private Color vertColor = Color.BLUE;
    float[] dash = {
        1.f, 2.f
    };
    private BasicStroke dashedLine = new BasicStroke(1.f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 2.0f, dash, 0.f);
    private BasicStroke solidLine = new BasicStroke(1.f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 2.0f);
    private int point0CircleRadius = 10;
    private GeneralPath[] customAxisPaths = new GeneralPath[2];
    private boolean rotatingAxes = false;
    private boolean holdingAxis0 = false;
    private boolean holdingAxis1 = false;
    private float[] startPoint = new float[3];
    private float[] endPoint = new float[3];
    private final int mouseOnmaskShift = MouseEvent.SHIFT_DOWN_MASK;
    private final int mouseOnmaskCtrl = MouseEvent.CTRL_DOWN_MASK;
    private final int wheelOnmaskShift = MouseWheelEvent.SHIFT_DOWN_MASK;
    private float[][] startingVectors = {{1.0f, 0.0f, 0.0f}, {0.0f, 1.0f, 0.0f}, {0.0f, 0.0f, 1.0f}};
    private float zoom = 1.0f;
    public static final int SCALING_AUTO = 1;
    public static final int SCALING_MANUAL = 2;
    private int scalingMode = SCALING_AUTO;
    private int imagePosX = 0;
    private int imagePosY = 0;
    private int translateStartPointX = 0;
    private int translateStartPointY = 0;
    private boolean translating = false;
    private boolean holdingPoint = false;
    private int holdingPointIndex = -1;
    private CursorProvider cp = new CursorProvider();

    public CustomOrthosliceViewPanel(int axis) {
        super();
        this.setName("custom orthoslice " + axis);
        this.sliceInfoString = "axis" + axis;
        switch (axis) {
            case 0:
                this.setType(VIEW_SLICE_CUSTOM_ORTHO_0);
                break;
            case 1:
                this.setType(VIEW_SLICE_CUSTOM_ORTHO_1);
                break;
            case 2:
                this.setType(VIEW_SLICE_CUSTOM_ORTHO_2);
                break;
        }

        this.addComponentListener(this);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);
        this.axis = axis;
        updateSliceLinesColors();
    }

    @Override
    public void update() {
        if (dataImage == null) {
            return;
        }

        switch (scalingMode) {
            case SCALING_AUTO:
                zoom = 1.0f;
                imagePosX = 0;
                imagePosY = 0;
                float newScale = 1.0f;
                Dimension myDim = this.getSize();
                if (myDim.width == 0 || myDim.height == 0) {
                    repaint();
                    return;
                }
                float xs,
                 ys;
                xs = (float) myDim.width / getPrefferedDataImageWidth();
                ys = (float) myDim.height / getPrefferedDataImageHeight();
                newScale = Math.min(xs, ys);
                if (newScale != imageScale) {
                    imageScale = newScale;
                    scaleImage();
                }
                break;
            case SCALING_MANUAL:
                break;
        }

        repaint();
    }

    public float getPrefferedDataImageWidth() {
        if (dataImage == null) {
            return 0.0f;
        }
        return imageSizeWidth / upp;
    }

    public float getPrefferedDataImageHeight() {
        if (dataImage == null) {
            return 0.0f;
        }
        return imageSizeHeight / upp;
    }

    public void resetZoomAndPosition() {
        zoom = 1.0f;
        imagePosX = 0;
        imagePosY = 0;
        scaleImage();
        update();
        //fireZoomChanged();
    }

    private void scaleImage() {
        if (dataImage == null) {
            return;
        }

        this.w = (int) Math.round(zoom * imageScale * imageSizeWidth / upp);
        this.h = (int) Math.round(zoom * imageScale * imageSizeHeight / upp);

        if (upp == uppW) {
            this.dw = zoom * imageScale;
        } else {
            this.dw = zoom * imageScale * uppW / upp;
        }

        if (upp == uppH) {
            this.dh = zoom * imageScale;
        } else {
            this.dh = zoom * imageScale * uppH / upp;
        }

        updateCustomAxisPaths();
    }
    GeneralPath path;

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D gr = (Graphics2D) g;
        Color oldColor = gr.getColor();

        //---------clear background----------------------
        gr.setColor(Color.GRAY);
        gr.fillRect(0, 0, this.getWidth(), this.getHeight());

        gr.translate(imagePosX, imagePosY);
        //---------paint data----------------------------
        gr.drawImage(dataImage, 0, 0, w, h, null);

        //---------paint overlay-------------------------
        if (overlayImage != null) {
            AlphaComposite c = (AlphaComposite) gr.getComposite();
            gr.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, overlayOpacity));
            gr.drawImage(overlayImage, 0, 0, w, h, null);
            gr.setComposite(c);
        }

        //--------paint points--------------------
        gr.setStroke(new BasicStroke());
        float jx, jy;
        for (int i = 0; i < points.size(); i++) {
            float[] p = points.get(i).getWorldCoords();
            float dist = 0, tmp = 0;
            for (int j = 0; j < 3; j++) {
                dist += customOrthoPlanesVectors[axis][j] * (p[j] - customOrthoPlanesPoint[j]);
                tmp += customOrthoPlanesVectors[axis][j] * customOrthoPlanesVectors[axis][j];
            }
            dist = Math.abs(dist) / (float) Math.sqrt(tmp);

            float d = 8 - dist / upp;
            path = new GeneralPath();

            if (d > 0) {
                boolean selectedPoint = false;
                if (selectedPoints != null) {
                    for (int j = 0; j < selectedPoints.length; j++) {
                        if (i == selectedPoints[j]) {
                            selectedPoint = true;
                            break;
                        }
                    }
                }

                if (selectedPoint) {
                    gr.setColor(new Color(200, 0, 255));
                } else {
                    gr.setColor(new Color(255, 0, 0));
                }

                float[] jj = realToPanelCoords(p);
                jx = jj[0];
                jy = jj[1];


                path.moveTo(jx - d, jy);
                path.lineTo(jx + d, jy);
                path.moveTo(jx, jy - d);
                path.lineTo(jx, jy + d);
                if (paintPointLabels && d == 8) {
                    gr.drawString(points.get(i).getName(), jx + 3, jy - 3);
                }
            }
            gr.draw(path);


        }

        //---------paint 2D connections------------------------
        if (paintConnections && pointConnections.size() > 0) {
            gr.setColor(Color.YELLOW);
            gr.setStroke(new BasicStroke());
            path = new GeneralPath();
            for (int i = 0; i < pointConnections.size(); i++) {
                ConnectionDescriptor c = pointConnections.get(i);
                float[] fp1 = c.getP1().getWorldCoords();//c.getP1WorldCoordsDistanced(0.0f)
                float[] fp2 = c.getP2().getWorldCoords();//c.getP2WorldCoordsDistanced(0.0f);
                if (isInPlane(fp1) && isInPlane(fp2)) {
                    float[] jj1 = realToPanelCoords(fp1);
                    float[] jj2 = realToPanelCoords(fp2);
                    path.moveTo(jj1[0], jj1[1]);
                    path.lineTo(jj2[0], jj2[1]);
                }
            }
            gr.draw(path);
        }


        if (sliceLinesMode != OrthosliceViewPanel.SLICE_LINES_NONE) {
            //--------paint slice lines----------------------
            if (customAxisPaths[0] != null) {
                if (rotatingAxes || holdingAxis0) {
                    gr.setStroke(solidLine);
                } else {
                    gr.setStroke(dashedLine);
                }
                gr.setColor(horizColor);
                gr.draw(customAxisPaths[0]);
            }
            if (customAxisPaths[1] != null) {
                if (rotatingAxes || holdingAxis1) {
                    gr.setStroke(solidLine);
                } else {
                    gr.setStroke(dashedLine);
                }
                gr.setColor(vertColor);
                gr.draw(customAxisPaths[1]);
            }


            if (rotatingAxes) {
                gr.setStroke(solidLine);
                gr.setColor(Color.MAGENTA);
                point0CircleRadius = 10;
                float[] tmp = realToPanelCoords(customOrthoPlanesPoint);

                gr.drawOval((int) (tmp[0] + 0.5) - point0CircleRadius, (int) (tmp[1] + 0.5) - point0CircleRadius, 2 * point0CircleRadius, 2 * point0CircleRadius);
            }
        }

        //---------paint border----------------------
        if (sliceLinesMode == OrthosliceViewPanel.SLICE_LINES_COLORED) {
            gr.setStroke(solidLine);
            gr.setColor(borderColor);
            GeneralPath p = new GeneralPath();
            p.moveTo(0, 0);
            p.lineTo(w, 0);
            p.lineTo(w, h);
            p.lineTo(0, h);
            p.lineTo(0, 0);
            gr.draw(p);
        }



        //---------paint drag line-----------------------
        if (holdingNewPoint) {
            gr.setColor(Color.RED);

            gr.drawLine(dragLine[0][0], dragLine[0][1], dragLine[1][0], dragLine[1][1]);
            int[] c = new int[2];
            c[0] = (int) Math.round(dragLineCenter[0]);
            c[1] = (int) Math.round(dragLineCenter[1]);
            gr.drawLine(c[0] - 3, c[1],
                    c[0] + 3, c[1]);
            gr.drawLine(c[0], c[1] - 3,
                    c[0], c[1] + 3);
            float d = (float) (Math.sqrt((dragLine[1][0] - dragLine[0][0]) * (dragLine[1][0] - dragLine[0][0]) + (dragLine[1][1] - dragLine[0][1]) * (dragLine[1][1] - dragLine[0][1])));
            int cx = (int) Math.round((float) c[0] - d / 2.0f);
            int cy = (int) Math.round((float) c[1] - d / 2.0f);
            int cd = (int) Math.round(d);
            gr.drawOval(cx, cy, cd, cd);
        }




        //---------paint slice info-----------------------
        if (paintSliceInfo) {
            gr.setColor(Color.YELLOW);
            gr.drawString(sliceInfoString, 5, 2 + gr.getFontMetrics().getHeight());
        }

        //--------------------------------------------
        gr.setColor(oldColor);
    }

    private void updateCustomAxisPaths() {
//        if(localBase == null || extents == null)
//            return;
//
//        float[] p0 = realToPanelCoords(customOrthoPlanesPoint);        
//        float[] p1;
//        float[] tmp = new float[3];
//        
//        customAxisPaths[0] = new GeneralPath();
//        System.arraycopy(customOrthoPlanesPoint, 0, tmp, 0, tmp.length);
//        while(true) {
//            for (int i = 0; i < tmp.length; i++) {
//                tmp[i] += localBase[0][i];
//            }
//            p1 = realToPanelCoords(tmp);
//            if(p1[0] <= 0 || p1[0] >= w || p1[1] <= 0 || p1[1] >= h)
//                break;
//        }
//        customAxisPaths[0].moveTo(p0[0]+10*localPanelBase[0][0], p0[1]+10*localPanelBase[0][1]);        
//        customAxisPaths[0].lineTo(p1[0], p1[1]);
//
//        System.arraycopy(customOrthoPlanesPoint, 0, tmp, 0, tmp.length);
//        while(true) {
//            for (int i = 0; i < tmp.length; i++) {
//                tmp[i] -= localBase[0][i];
//            }
//            p1 = realToPanelCoords(tmp);
//            if(p1[0] <= 0 || p1[0] >= w || p1[1] <= 0 || p1[1] >= h)
//                break;
//        }
//        customAxisPaths[0].moveTo(p0[0]-10*localPanelBase[0][0], p0[1]-10*localPanelBase[0][1]);
//        customAxisPaths[0].lineTo(p1[0], p1[1]);
//
//
//
//        customAxisPaths[1] = new GeneralPath();
//        System.arraycopy(customOrthoPlanesPoint, 0, tmp, 0, tmp.length);
//        while(true) {
//            for (int i = 0; i < tmp.length; i++) {
//                tmp[i] += localBase[1][i];
//            }
//            p1 = realToPanelCoords(tmp);
//            if(p1[0] <= 0 || p1[0] >= w || p1[1] <= 0 || p1[1] >= h)
//                break;
//        }
//        customAxisPaths[1].moveTo(p0[0]+10*localPanelBase[1][0], p0[1]+10*localPanelBase[1][1]);
//        customAxisPaths[1].lineTo(p1[0], p1[1]);
//
//        System.arraycopy(customOrthoPlanesPoint, 0, tmp, 0, tmp.length);
//        while(true) {
//            for (int i = 0; i < tmp.length; i++) {
//                tmp[i] -= localBase[1][i];
//            }
//            p1 = realToPanelCoords(tmp);
//            if(p1[0] <= 0 || p1[0] >= w || p1[1] <= 0 || p1[1] >= h)
//                break;
//        }
//        customAxisPaths[1].moveTo(p0[0]-10*localPanelBase[1][0], p0[1]-10*localPanelBase[1][1]);
//        customAxisPaths[1].lineTo(p1[0], p1[1]);
//
    }

    /**
     * @return the dataImage
     */
    public BufferedImage getDataImage() {
        return dataImage;
    }

    /**
     * @param dataImage the dataImage to set
     */
    public void setDataImage(BufferedImage dataImage, float uppW, float uppH, float upp) {
        this.dataImage = dataImage;
        this.uppW = uppW;
        this.uppH = uppH;
        this.upp = upp;
        updateLocalBase();

        if (dataImage != null) {
            imageSizeWidth = uppW * (float) dataImage.getWidth();
            imageSizeHeight = uppH * (float) dataImage.getHeight();
        } else {
            imageSizeWidth = 1.0f;
            imageSizeHeight = 1.0f;
        }
        scaleImage();
        update();
    }

    /**
     * @return the overlayImage
     */
    public BufferedImage getOverlayImage() {
        return overlayImage;
    }

    /**
     * @param dataImage the dataImage to set
     */
    public void setOverlayImage(BufferedImage overlayImage, float overlayOpacity) {
        this.overlayImage = overlayImage;
        this.overlayOpacity = overlayOpacity;
        repaint();
    }

    @Override
    public void componentResized(ComponentEvent e) {
        update();
    }

    @Override
    public void componentMoved(ComponentEvent e) {
    }

    @Override
    public void componentShown(ComponentEvent e) {
    }

    @Override
    public void componentHidden(ComponentEvent e) {
    }

    private float panelToImageCoordsHorizontal(float p) {
        return p / dw;
    }

    private float panelToImageCoordsVertical(float p) {
        return p / dh;
    }

    private float imageCoordsToPanelHorizontal(float p) {
        return p * dw;
    }

    private float imageCoordsToPanelVertical(float p) {
        return p * dh;
    }

    private float[] panelToRealCoords(float x, float y) {
        float[] p = new float[2];
        p[0] = x;
        p[1] = y;
        return panelToRealCoords(p);
    }

    private float[] panelToRealCoords(float[] p) {
        float[] out = new float[3];
        float x, y;
        x = panelToImageCoordsHorizontal(p[0]) - 0.5f;
        y = panelToImageCoordsVertical(p[1]) - 0.5f;

        for (int i = 0; i < 3; i++) {
            out[i] = extents[0][i] + x * base[0][i] + y * base[1][i];
        }
        return out;
    }

    private float[] realToPanelCoords(float[] p) {
        float[] out = new float[2];
        if (base == null || extents == null) {
            out[0] = 0;
            out[1] = 0;
            return out;
        }

        float[] tmp0 = new float[3];
        for (int j = 0; j < 3; j++) {
            tmp0[j] = Math.abs(base[0][j] + base[1][j]);
        }
        float min = tmp0[0];
        int mini = 0;
        if (tmp0[1] < min) {
            min = tmp0[1];
            mini = 1;
        }
        if (tmp0[2] < min) {
            min = tmp0[2];
            mini = 2;
        }

        int i0 = 0, i1 = 1;
        switch (mini) {
            case 0:
                i0 = 1;
                i1 = 2;
                break;
            case 1:
                i0 = 0;
                i1 = 2;
                break;
            case 2:
                i0 = 0;
                i1 = 1;
                break;
        }

        for (int j = 0; j < 3; j++) {
            tmp0[j] = p[j] - extents[0][j];
        }
        float detA = base[0][i0] * base[1][i1] - base[1][i0] * base[0][i1];

        float x, y;
        x = (tmp0[i0] * base[1][i1] - base[1][i0] * tmp0[i1]) / detA;
        y = (tmp0[i1] * base[0][i0] - base[0][i1] * tmp0[i0]) / detA;

        out[0] = (int) Math.round((float) x * dw + dw / 2.0f);
        out[1] = (int) Math.round((float) y * dh + dh / 2.0f);
        return out;
    }
    private boolean holdingNewPoint = false;
    private int[][] dragLine = new int[2][2];
    private float[] dragLineCenter = new float[2];

    @Override
    public void mouseClicked(MouseEvent e) {
        int x, y;
        x = e.getX() - imagePosX;
        y = e.getY() - imagePosY;
        if (x < 0 || y < 0 || x >= w || y >= h) {
            return;
        }

        if (e.getButton() == MouseEvent.BUTTON1) {
            if (e.getClickCount() > 1) {
                float[] p = panelToRealCoords(x, y);
                setCustomOrthoPlanesPoint(p);
                fireCustomOrthoPlaneChanged(axis, customOrthoPlanesPoint, customOrthoPlanesVectors);
            }

        } else if (e.getButton() == MouseEvent.BUTTON3) {
        }

    }

    @Override
    public void mousePressed(MouseEvent e) {
        int x, y;
        x = e.getX() - imagePosX;
        y = e.getY() - imagePosY;

        if (e.getButton() == MouseEvent.BUTTON1) {
            if ((e.getModifiersEx() & mouseOnmaskShift) == mouseOnmaskShift) {
                fireZoomChanged();
                this.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                translateStartPointX = x + imagePosX;
                translateStartPointY = y + imagePosY;
                translating = true;
            } else if ((e.getModifiersEx() & mouseOnmaskCtrl) == mouseOnmaskCtrl) {
                float[] p;
                float[] pp;
                float dist, tol, tmp;
                float[] cp = new float[2];
                cp[0] = x;
                cp[1] = y;
                for (int i = 0; i < points.size(); i++) {
                    if (points.get(i).isDependant()) {
                        continue;
                    }
                    p = points.get(i).getWorldCoords();
                    pp = realToPanelCoords(p);
                    dist = 0;
                    for (int j = 0; j < 2; j++) {
                        tmp = pp[j] - cp[j];
                        dist += tmp * tmp;
                    }
                    dist = (float) Math.sqrt(dist);
                    //System.out.println("point "+points.get(i).getName()+" in-plane distance = "+dist);
                    if (dist < 3) {
                        dist = 0;
                        tol = 0;
                        for (int k = 0; k < 3; k++) {
                            dist += customOrthoPlanesVectors[axis][k] * (p[k] - customOrthoPlanesPoint[k]);
                            tol += customOrthoPlanesVectors[axis][k] * customOrthoPlanesVectors[axis][k];
                        }
                        dist = Math.abs(dist);
                        tol = (float) Math.sqrt(tol) * upp / 2;
                        //System.out.println("point "+points.get(i).getName()+" out-of-plane distance = "+dist+" (tolerance = "+tol+")");
                        if (dist <= tol) {
                            holdingPoint = true;
                            holdingPointIndex = i;
                            fireGeometryPointSelected(i, false);
                            break;
                        }
                    }
                }
            } else {
                if (x < 0 || y < 0 || x >= w || y >= h) {
                    return;
                }

                if (customAxisPaths[0] != null && Path2D.intersects(customAxisPaths[0].getPathIterator(null), x - 3, y - 3, 6, 6)) {
                    holdingAxis0 = true;
                }

                if (customAxisPaths[1] != null && Path2D.intersects(customAxisPaths[1].getPathIterator(null), x - 3, y - 3, 6, 6)) {
                    holdingAxis1 = true;
                }

                if (holdingAxis0 || holdingAxis1) {
                    startPoint = panelToRealCoords(x, y);
                    repaint();
                }

            }

        } else if (e.getButton() == MouseEvent.BUTTON2) {
            if (x < 0 || y < 0 || x >= w || y >= h) {
                return;
            }

            rotatingAxes = true;
            for (int i = 0; i < 3; i++) {
                System.arraycopy(customOrthoPlanesVectors[i], 0, startingVectors[i], 0, 3);
            }
            if (rotatingAxes) {
                startPoint = panelToRealCoords(x, y);
                repaint();
            }

        } else if (e.getButton() == MouseEvent.BUTTON3) {
            if (x < 0 || y < 0 || x >= w || y >= h) {
                return;
            }

            holdingNewPoint = true;
            dragLine[0][0] = x;
            dragLine[0][1] = y;
            dragLineCenter[0] = x;
            dragLineCenter[1] = y;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        this.setCursor(Cursor.getDefaultCursor());


        if (e.getButton() == MouseEvent.BUTTON1) {
            translating = false;
            if (holdingAxis0 || holdingAxis1) {
                holdingAxis0 = false;
                holdingAxis1 = false;
                fireCustomOrthoPlaneChanged(axis, customOrthoPlanesPoint, customOrthoPlanesVectors);
                repaint();
            }

            if (holdingPoint) {
                holdingPoint = false;
                holdingPointIndex = -1;
                fireGeometryPointSelected(-1, false);
            }

        } else if (e.getButton() == MouseEvent.BUTTON2) {
            if (rotatingAxes) {
                rotatingAxes = false;
                endPoint = panelToRealCoords(e.getX() - imagePosX, e.getY() - imagePosY);
                fireCustomOrthoPlaneChanged(axis, customOrthoPlanesPoint, calculateNewCustomOrthoPlaneVectors(startingVectors, customOrthoPlanesPoint, startPoint, endPoint));
                repaint();
            }
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            holdingNewPoint = false;
            if (dragLineCenter[0] < 0) {
                dragLineCenter[0] = 0;
            }
            if (dragLineCenter[0] >= w) {
                dragLineCenter[0] = w;
            }
            if (dragLineCenter[1] < 0) {
                dragLineCenter[1] = 0;
            }
            if (dragLineCenter[1] >= h) {
                dragLineCenter[1] = h;
            }

            float[] p = panelToRealCoords(dragLineCenter);
            fireGeometryPointAdded(p);
        }


    }

    @Override
    public void mouseEntered(MouseEvent e) {
        mouseOver = true;
    }

    @Override
    public void mouseExited(MouseEvent e) {
        mouseOver = false;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        int x, y;
        x = e.getX() - imagePosX;
        y = e.getY() - imagePosY;

        if (translating && ((e.getModifiersEx() & mouseOnmaskShift) == mouseOnmaskShift)) {
            fireZoomChanged();
            x += imagePosX;
            y += imagePosY;
            imagePosX += (x - translateStartPointX);
            imagePosY += (y - translateStartPointY);
            translateStartPointX = x;
            translateStartPointY = y;
            repaint();
        } else if (holdingPoint && ((e.getModifiersEx() & mouseOnmaskCtrl) == mouseOnmaskCtrl)) {
            float[] p = panelToRealCoords(x, y);
            fireSelectedPointMoved(holdingPointIndex, p);
        } else if (holdingAxis0 && holdingAxis1) {
            float[] p = new float[2];
            p[0] = x;
            p[1] = y;
            setCustomOrthoPlanesPoint(panelToRealCoords(p));
            fireCustomOrthoPlaneChanged(axis, customOrthoPlanesPoint, customOrthoPlanesVectors);
            repaint();
        } else if (holdingAxis0 && !holdingAxis1) {
            endPoint = panelToRealCoords((float) x, (float) y);
            float[] v = new float[3];
            for (int i = 0; i < 3; i++) {
                v[i] = endPoint[i] - startPoint[i];
                startPoint[i] = endPoint[i];
            }
            translateCustomOrthoPlanesPointInAxis(v, 1);
            fireCustomOrthoPlaneChanged(axis, customOrthoPlanesPoint, customOrthoPlanesVectors);
            repaint();
        } else if (!holdingAxis0 && holdingAxis1) {
            endPoint = panelToRealCoords(x, y);
            float[] v = new float[3];
            for (int i = 0; i < 3; i++) {
                v[i] = endPoint[i] - startPoint[i];
                startPoint[i] = endPoint[i];
            }
            translateCustomOrthoPlanesPointInAxis(v, 0);
            fireCustomOrthoPlaneChanged(axis, customOrthoPlanesPoint, customOrthoPlanesVectors);
            repaint();
        } else if (rotatingAxes) {
            endPoint = panelToRealCoords(x, y);
            setCustomOrthoPlanesVectors(calculateNewCustomOrthoPlaneVectors(startingVectors, customOrthoPlanesPoint, startPoint, endPoint));

            fireCustomOrthoPlaneChanged(axis, customOrthoPlanesPoint, calculateNewCustomOrthoPlaneVectors(startingVectors, customOrthoPlanesPoint, startPoint, endPoint));
            repaint();


        } else if (holdingNewPoint) {
            dragLine[1][0] = x;
            dragLine[1][1] = y;
            dragLineCenter[0] = (float) (dragLine[1][0] + dragLine[0][0]) / 2.0f;
            dragLineCenter[1] = (float) (dragLine[1][1] + dragLine[0][1]) / 2.0f;
            repaint();
        }
    }

    private boolean mouseOver = false;
    private float[] currentMousePosition = new float[2];
    
    @Override
    public void mouseMoved(MouseEvent e) {
        int x, y;
        x = e.getX() - imagePosX;
        y = e.getY() - imagePosY;
        if(x< 0 || y <0 || x >= w || y >= h) {
            this.setCursor(Cursor.getDefaultCursor());
            fireMouseLocationChanged(null);
            return;
        }

        if(mouseOver) {
            //currentMousePosition[0] = panelToRealImageCoordsHorizontal(x);
            //currentMousePosition[1] = panelToRealImageCoordsVertical(y);            
            //fireMouseLocationChanged(currentMousePosition);
        } else {
            fireMouseLocationChanged(null);
        }
        
        
        if (x < 0 || y < 0 || x >= w || y >= h) {
            return;
        }

        if(mouseOver && !((e.getModifiersEx() & mouseOnmaskCtrl) == mouseOnmaskCtrl) && !((e.getModifiersEx() & mouseOnmaskShift) == mouseOnmaskShift)) {
        
            if (customAxisPaths[0] != null && Path2D.intersects(customAxisPaths[0].getPathIterator(null), x - 3, y - 3, 6, 6)) {
                this.setCursor(cp.getCustomMoveCursor());
            } else if (customAxisPaths[1] != null && Path2D.intersects(customAxisPaths[1].getPathIterator(null), x - 3, y - 3, 6, 6)) {
                this.setCursor(cp.getCustomMoveCursor());
            } else {
                this.setCursor(Cursor.getDefaultCursor());
            }            
        } else {
            this.setCursor(Cursor.getDefaultCursor());
        }

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if ((e.getModifiersEx() & wheelOnmaskShift) == wheelOnmaskShift) {
            int x, y;
            float imgX, imgY;
            x = e.getX() - imagePosX;
            y = e.getY() - imagePosY;
            imgX = panelToImageCoordsHorizontal(x);
            imgY = panelToImageCoordsVertical(y);

            fireZoomChanged();
            if (e.getWheelRotation() < 0) {
                zoom = zoom * 1.1f;
            } else {
                zoom = zoom / 1.1f;
            }

            scaleImage();
            x -= imageCoordsToPanelHorizontal(imgX);
            y -= imageCoordsToPanelVertical(imgY);

            imagePosX += x;
            imagePosY += y;

            update();
        } else {
            float[] p = new float[3];
            for (int i = 0; i < 3; i++) {
                p[i] = customOrthoPlanesPoint[i] + customOrthoPlanesVectors[axis][i] * e.getWheelRotation() * upp;
            }
            fireCustomOrthoPlaneChanged(axis, p, customOrthoPlanesVectors);
        }
    }

    private void fireCustomOrthoPlaneChanged(int axis, float[] p, float[][] vv) {
        for (ViewPanelListener listener : viewPanelListeners) {
            listener.onViewPanelEvent(new CustomOrthoPlaneChangedViewPanelEvent(this, axis, p, vv));
        }
    }

    private void fireGeometryPointAdded(float[] p) {
        for (ViewPanelListener listener : viewPanelListeners) {
            listener.onViewPanelEvent(new GeometryPointAddedCustomOrthoPanelEvent(this, axis, p));
        }
    }

    private void fireGeometryPointSelected(int pIndex, boolean followSlices) {
        for (ViewPanelListener listener : viewPanelListeners) {
            listener.onViewPanelEvent(new GeometryPointSelectedCustomOrthoPanelEvent(this, pIndex, followSlices));
        }
    }

    private void fireSelectedPointMoved(int pIndex, float[] p) {
        for (ViewPanelListener listener : viewPanelListeners) {
            listener.onViewPanelEvent(new GeometryPointMovedCustomOrthoPanelEvent(this, pIndex, p));
        }
    }

    private void fireZoomChanged() {
        for (ViewPanelListener listener : viewPanelListeners) {
            listener.onViewPanelEvent(new ZoomChangedOrthoPanelEvent(this));
        }
    }

    public int getScalingMode() {
        return scalingMode;
    }

    public void setScalingMode(int scalingMode) {
        this.scalingMode = scalingMode;
        //update();
        resetZoomAndPosition();
    }

    /**
     * @return the imageScale
     */
    public float getImageScale() {
        return imageScale;
    }

    /**
     * @return the paintSliceInfo
     */
    public boolean isPaintSliceInfo() {
        return paintSliceInfo;
    }

    /**
     * @param paintSliceInfo the paintSliceInfo to set
     */
    public void setPaintSliceInfo(boolean paintSliceInfo) {
        this.paintSliceInfo = paintSliceInfo;
        repaint();
    }

    @Override
    public Dimension getPreferredViewSize() {
        if (dataImage == null) {
            return this.getMinimumSize();
        }
        return new Dimension(w, h);
    }

    private void translateCustomOrthoPlanesPointInAxis(float[] v, int axis) {
        if (localBase == null || v == null || v.length != 3) {
            return;
        }

        float[] newV = new float[3];
        float[] normBase = new float[3];
        float norm = 0;
        for (int i = 0; i < 3; i++) {
            norm += localBase[axis][i] * localBase[axis][i];
        }
        norm = (float) Math.sqrt(norm);
        for (int i = 0; i < 3; i++) {
            normBase[i] = localBase[axis][i] / norm;
        }

        float s = 0;
        for (int i = 0; i < 3; i++) {
            s += v[i] * normBase[i];

        }

        for (int i = 0; i < 3; i++) {
            newV[i] = s * normBase[i];
        }
        translateCustomOrthoPlanesPoint(newV);
    }

    private void translateCustomOrthoPlanesPoint(float[] v) {
        if (v == null || v.length != 3) {
            return;
        }

        float[] tmp = new float[3];
        for (int i = 0; i < 3; i++) {
            tmp[i] = customOrthoPlanesPoint[i] + v[i];
        }
        setCustomOrthoPlanesPoint(tmp);
    }

    private float[][] calculateNewCustomOrthoPlaneVectors(float[][] originalVectors, float[] p0, float[] p1, float[] p2) {
        float[] v1 = new float[3];
        float[] v2 = new float[3];
        float[] v3 = new float[3];
        float s = 0, v1norm = 0, v2norm = 0;

        for (int i = 0; i < 3; i++) {
            v1[i] = p1[i] - p0[i];
            v2[i] = p2[i] - p0[i];
            s += v1[i] * v2[i];
            v1norm += v1[i] * v1[i];
            v2norm += v2[i] * v2[i];
        }
        v1norm = (float) Math.sqrt(v1norm);
        v2norm = (float) Math.sqrt(v2norm);

        float angle = (float) Math.acos(s / (v1norm * v2norm));

        v3[0] = v1[1] * v2[2] - v1[2] * v2[1];
        v3[1] = v1[2] * v2[0] - v1[0] * v2[2];
        v3[2] = v1[0] * v2[1] - v1[1] * v2[0];
        s = 0;
        float[][] out = new float[3][3];

        switch (axis) {
            case 0:
                for (int i = 0; i < 3; i++) {
                    s += v3[i] * originalVectors[0][i];
                }
                if (s < 0) {
                    angle = -angle;
                }
                System.arraycopy(originalVectors[0], 0, out[0], 0, 3);
                out[1] = rotatePointAroundVector(originalVectors[1], originalVectors[0], angle);
                //rzut
                out[1] = castToPlane(out[1], originalVectors[0]);


                //out[2] = rotatePointAroundVector(originalVectors[2], originalVectors[0], angle);
                out[2][0] = out[0][1] * out[1][2] - out[0][2] * out[1][1];
                out[2][1] = out[0][2] * out[1][0] - out[0][0] * out[1][2];
                out[2][2] = out[0][0] * out[1][1] - out[0][1] * out[1][0];
                break;
            case 1:
                for (int i = 0; i < 3; i++) {
                    s += v3[i] * originalVectors[1][i];
                }
                if (s < 0) {
                    angle = -angle;
                }
                System.arraycopy(originalVectors[1], 0, out[1], 0, 3);
                out[0] = rotatePointAroundVector(originalVectors[0], originalVectors[1], angle);
                out[0] = castToPlane(out[0], originalVectors[1]);

                //out[2] = rotatePointAroundVector(originalVectors[2], originalVectors[1], angle);
                out[2][0] = out[0][1] * out[1][2] - out[0][2] * out[1][1];
                out[2][1] = out[0][2] * out[1][0] - out[0][0] * out[1][2];
                out[2][2] = out[0][0] * out[1][1] - out[0][1] * out[1][0];

                break;
            case 2:
                for (int i = 0; i < 3; i++) {
                    s += v3[i] * originalVectors[2][i];
                }
                if (s < 0) {
                    angle = -angle;
                }
                System.arraycopy(originalVectors[2], 0, out[2], 0, 3);
                out[0] = rotatePointAroundVector(originalVectors[0], originalVectors[2], angle);
                out[0] = castToPlane(out[0], originalVectors[2]);

                //out[1] = rotatePointAroundVector(originalVectors[1], originalVectors[2], angle);
                out[1][0] = out[2][1] * out[0][2] - out[2][2] * out[0][1];
                out[1][1] = out[2][2] * out[0][0] - out[2][0] * out[0][2];
                out[1][2] = out[2][0] * out[0][1] - out[2][1] * out[0][0];
                break;
        }

        //secure check
        double s01 = 0, s02 = 0, s12 = 0;
        double l0 = 0, l1 = 0, l2 = 0;
        for (int i = 0; i < 3; i++) {
            s01 += out[0][i] * out[1][i];
            s02 += out[0][i] * out[2][i];
            s12 += out[1][i] * out[2][i];
            l0 += out[0][i] * out[0][i];
            l1 += out[1][i] * out[1][i];
            l2 += out[2][i] * out[2][i];
        }

        l0 = Math.sqrt(l0);
        l1 = Math.sqrt(l1);
        l2 = Math.sqrt(l2);
        if (l0 == 0.0 || l1 == 0.0 || l2 == 0.0) {
            System.out.println("error estimating new base vectors - reverting to previous");
            return originalVectors;
        }

        s01 = Math.abs(s01) / (l0 * l1);
        s02 = Math.abs(s02) / (l0 * l2);
        s12 = Math.abs(s12) / (l1 * l2);

        double eps = 0.001f;
        if (s01 > eps || s02 > eps || s12 > eps) {
            System.out.println("error estimating new base vectors - reverting to previous");
            return originalVectors;
        }



        return out;
    }

    private float[] rotatePointAroundVector(float[] p, float[] v, float angle) {
        float[] out = new float[3];
        float ux, uy, uz, vx, vy, vz, wx, wy, wz, sa, ca;
        ux = v[0] * p[0];
        uy = v[0] * p[1];
        uz = v[0] * p[2];
        vx = v[1] * p[0];
        vy = v[1] * p[1];
        vz = v[1] * p[2];
        wx = v[2] * p[0];
        wy = v[2] * p[1];
        wz = v[2] * p[2];
        sa = (float) Math.sin(angle);
        ca = (float) Math.cos(angle);
        out[0] = v[0] * (ux + vy + wz) + (p[0] * (v[1] * v[1] + v[2] * v[2]) - v[0] * (vy + wz)) * ca + (-wy + vz) * sa;
        out[1] = v[1] * (ux + vy + wz) + (p[1] * (v[0] * v[0] + v[2] * v[2]) - v[1] * (ux + wz)) * ca + (wx - uz) * sa;
        out[2] = v[2] * (ux + vy + wz) + (p[2] * (v[0] * v[0] + v[1] * v[1]) - v[2] * (ux + vy)) * ca + (-vx + uy) * sa;
        return out;
    }

    private void setCustomOrthoPlanesPoint(float[] customOrthoPlanesPoint) {
        this.customOrthoPlanesPoint = customOrthoPlanesPoint;
        this.sliceInfoString = "axis " + axis + " " + customOrthoPlanesPoint[axis];
        updateCustomAxisPaths();
        repaint();
    }

    private void setCustomOrthoPlanesVectors(float[][] customOrthoPlanesVectors) {
        this.customOrthoPlanesVectors = customOrthoPlanesVectors;

        updateLocalBase();
        updateCustomAxisPaths();
        repaint();
    }

    private void updateLocalBase() {
        if (customOrthoPlanesVectors == null) {
            localBase = null;
            return;
        }

        localBase = new float[2][3];
        switch (axis) {
            case 0:
                for (int j = 0; j < 3; j++) {
                    localBase[0][j] = customOrthoPlanesVectors[1][j];
                    localBase[1][j] = customOrthoPlanesVectors[2][j];
                }
                break;
            case 1:
                for (int j = 0; j < 3; j++) {
                    localBase[0][j] = customOrthoPlanesVectors[0][j];
                    localBase[1][j] = customOrthoPlanesVectors[2][j];
                }
                break;
            case 2:
                for (int j = 0; j < 3; j++) {
                    localBase[0][j] = customOrthoPlanesVectors[0][j];
                    localBase[1][j] = customOrthoPlanesVectors[1][j];
                }
                break;
        }

        float[] localBaseNorm = {0, 0};
        for (int i = 0; i < 3; i++) {
            localBaseNorm[0] += localBase[0][i] * localBase[0][i];
            localBaseNorm[1] += localBase[1][i] * localBase[1][i];
        }
        localBaseNorm[0] = (float) Math.sqrt(localBaseNorm[0]);
        localBaseNorm[1] = (float) Math.sqrt(localBaseNorm[1]);
        if (localBaseNorm[0] == 0.0f || localBaseNorm[1] == 0.0f) {
            localBase = null;
            return;
        }

        for (int i = 0; i < 3; i++) {
            localBase[0][i] = upp * localBase[0][i] / localBaseNorm[0];
            localBase[1][i] = upp * localBase[1][i] / localBaseNorm[1];
        }
    }
    
//    private void updateLocalPanelBase() {
//        float[] p0 = realToPanelCoords(customOrthoPlanesPoint);        
//        float[] p1;
//        float[] tmp = new float[3];
//        
//        localPanelBase = new float[2][2];
//
//        System.arraycopy(customOrthoPlanesPoint, 0, tmp, 0, tmp.length);
//        for (int i = 0; i < tmp.length; i++) {
//            tmp[i] += localBase[0][i];
//        }
//        p1 = realToPanelCoords(tmp);        
//        float norm = 0;
//        for (int i = 0; i < 2; i++) {
//            localPanelBase[0][i] = p1[i]-p0[i];            
//            norm += localPanelBase[0][i]*localPanelBase[0][i];
//        }
//        norm = (float) Math.sqrt(norm);
//        for (int i = 0; i < 2; i++) {
//            localPanelBase[0][i] /= norm;            
//        }
//        
//               
//        System.arraycopy(customOrthoPlanesPoint, 0, tmp, 0, tmp.length);
//        for (int i = 0; i < tmp.length; i++) {
//            tmp[i] += localBase[1][i];
//        }
//        p1 = realToPanelCoords(tmp);        
//        norm = 0;
//        for (int i = 0; i < 2; i++) {
//            localPanelBase[1][i] = p1[i]-p0[i];            
//            norm += localPanelBase[1][i]*localPanelBase[1][i];
//        }
//        norm = (float) Math.sqrt(norm);
//        for (int i = 0; i < 2; i++) {
//            localPanelBase[1][i] /= norm;            
//        }
//    }

    public void setCustomOrthoPlanesParams(float[] customOrthoPlanesPoint, float[][] customOrthoPlanesVectors, float[][] extents, float[][] base) {
        this.customOrthoPlanesVectors = customOrthoPlanesVectors;
        this.customOrthoPlanesPoint = customOrthoPlanesPoint;
        this.sliceInfoString = "axis" + axis + " " + customOrthoPlanesPoint[axis];
        this.extents = extents;
        this.base = base;

        updateLocalBase();
        updateCustomAxisPaths();
        repaint();
    }

    public void setPoints(ArrayList<PointDescriptor> points) {
        this.points = points;
        repaint();
    }

    public void setSelectedPoints(int[] selectedPoints) {
        if (selectedPoints == null) {
            this.selectedPoints = null;
        } else {
            this.selectedPoints = new int[selectedPoints.length];
            System.arraycopy(selectedPoints, 0, this.selectedPoints, 0, selectedPoints.length);
        }
        repaint();
    }

    public void setConnections(ArrayList<ConnectionDescriptor> conns) {
        this.pointConnections = conns;
        repaint();
    }

    /**
     * @param paintPointLabels the paintPointLabels to set
     */
    public void setPaintPointLabels(boolean paintPointLabels) {
        this.paintPointLabels = paintPointLabels;
        repaint();
    }

    /**
     * @param paintConnections the paintConnections to set
     */
    public void setPaintConnections(boolean paintConnections) {
        this.paintConnections = paintConnections;
        repaint();
    }

    /**
     * @param paintDistances the paintConnections to set
     */
    public void setPaintDistances(boolean paintDistances) {
        this.paintDistances = paintDistances;
        repaint();
    }

    /**
     * @return the axis
     */
    public int getAxis() {
        return axis;
    }

    private boolean isInPlane(float[] p1) {
        if (customOrthoPlanesPoint == null || customOrthoPlanesVectors[axis] == null) {
            return false;
        }

        float eps = upp;//0.000001f;

        float[] p0 = customOrthoPlanesPoint;
        float[] v0 = customOrthoPlanesVectors[axis];
        float is = 0;

        for (int i = 0; i < 3; i++) {
            is += v0[i] * (p1[i] - p0[i]);
        }

        return (Math.abs(is) < eps);
    }

    private float[] castToPlane(float[] vector, float[] planeNormalVector) {
        float[] out = new float[3];
        float t = -(planeNormalVector[0] * vector[0] + planeNormalVector[1] * vector[1] + planeNormalVector[2] * vector[2]) / (planeNormalVector[0] * planeNormalVector[0] + planeNormalVector[1] * planeNormalVector[1] + planeNormalVector[2] * planeNormalVector[2]);
        out[0] = vector[0] + t * planeNormalVector[0];
        out[1] = vector[1] + t * planeNormalVector[1];
        out[2] = vector[2] + t * planeNormalVector[2];

        return out;
    }

    /**
     * @return the sliceLinesMode
     */
    public int getSliceLinesMode() {
        return sliceLinesMode;
    }

    /**
     * @param sliceLinesMode the sliceLinesMode to set
     */
    public void setSliceLinesMode(int sliceLinesMode) {
        this.sliceLinesMode = sliceLinesMode;
        updateSliceLinesColors();
    }

    private void updateSliceLinesColors() {
        switch (sliceLinesMode) {
            case OrthosliceViewPanel.SLICE_LINES_COLORED:
                switch (axis) {
                    case 0:
                        borderColor = Color.RED;
                        horizColor = Color.BLUE;
                        vertColor = Color.GREEN;
                        break;
                    case 1:
                        borderColor = Color.GREEN;
                        horizColor = Color.BLUE;
                        vertColor = Color.RED;
                        break;
                    case 2:
                        borderColor = Color.BLUE;
                        horizColor = Color.GREEN;
                        vertColor = Color.RED;
                        break;
                }
                break;
            case OrthosliceViewPanel.SLICE_LINES_BLUE:
                borderColor = Color.BLUE;
                horizColor = Color.BLUE;
                vertColor = Color.BLUE;
                break;
            case OrthosliceViewPanel.SLICE_LINES_WHITE:
                borderColor = Color.WHITE;
                horizColor = Color.WHITE;
                vertColor = Color.WHITE;
                break;
            case OrthosliceViewPanel.SLICE_LINES_BLACK:
                borderColor = Color.BLACK;
                horizColor = Color.BLACK;
                vertColor = Color.BLACK;
                break;
        }
        repaint();
    }
    
    private void fireMouseLocationChanged(float[] p) {
        for (ViewPanelListener listener : viewPanelListeners) {
            listener.onViewPanelEvent(new MouseLocationChangedOrthoPanelEvent(this,p));
        }
    }
    
}
