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
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.ConnectionDescriptor;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.PointDescriptor;

/**
 * @author  Bartosz Borucki (babor@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */

public class CustomSliceViewPanel extends ViewPanel implements ComponentListener, MouseListener, MouseMotionListener, MouseWheelListener {
    private BufferedImage dataImage = null;

    private float imageScale = 1.0f;
    private boolean autoScaling = true;
    private float imageSizeWidth = 1.0f;
    private float imageSizeHeight = 1.0f;
    private float uppW = 1.0f, uppH = 1.0f, upp=1.0f;

    private int w = 0;
    private int h = 0;
    private float dw = 1.0f;
    private float dh = 1.0f;

    private boolean paintSliceInfo = false;
    private String sliceInfoString = "Custom Plane";

    private float[] customPlanePoint = {0.0f, 0.0f, 0.0f};
    private float[] customPlaneVector = {1.0f, 0.0f, 0.0f};

    private float[][] extents = null;
    private float[][] base = null;

    private ArrayList<PointDescriptor> points = new ArrayList<PointDescriptor>();
    private ArrayList<ConnectionDescriptor> pointConnections = new ArrayList<ConnectionDescriptor>();
    private int[] selectedPoints = null;
    private boolean paintPointLabels = true;
    private boolean paintConnections = false;
    private boolean paintDistances = false;


    public CustomSliceViewPanel() {
        super();
        this.setName("custom slice");
        this.setType(VIEW_SLICE_CUSTOM);
        this.addComponentListener(this);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);
    }

    @Override
    public void update() {
        if(dataImage == null)
            return;

        if(autoScaling) {
            float newScale = 1.0f;
            Dimension myDim = this.getSize();
            float xs,ys;
            xs = (float)myDim.width/getPrefferedDataImageWidth();
            ys = (float)myDim.height/getPrefferedDataImageHeight();
            newScale = Math.min(xs, ys);

            if(newScale != imageScale) {
                imageScale = newScale;
                scaleImage();
            }
        }
        repaint();
    }

    public float getPrefferedDataImageWidth() {
        if(dataImage == null)
            return 0.0f;
        return imageSizeWidth/upp;
    }

    public float getPrefferedDataImageHeight() {
        if(dataImage == null)
            return 0.0f;
        return imageSizeHeight/upp;
    }

    private void scaleImage() {
        if(dataImage == null) {
            return;
        }
        this.w = (int)Math.round(imageScale*imageSizeWidth/upp);
        this.h = (int)Math.round(imageScale*imageSizeHeight/upp);
        if(upp == uppW)
            this.dw = imageScale;
        else
            this.dw = imageScale*uppW/upp;

        if(upp == uppH)
            this.dh = imageScale;
        else
            this.dh = imageScale*uppH/upp;
    }


    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D gr = (Graphics2D) g;
        Color oldColor = gr.getColor();

        //---------clear background----------------------
        gr.setColor(Color.GRAY);
        gr.fillRect(0, 0, this.getWidth(), this.getHeight());

        //---------paint data----------------------------
        gr.drawImage(dataImage, 0, 0, w, h, null);

        //--------paint points--------------------
        gr.setStroke(new BasicStroke());
        int jx,jy;
        for (int i = 0; i < points.size(); i++) {
            float[] p = points.get(i).getWorldCoords();
            float dist = 0, tmp = 0;
            for (int j = 0; j < 3; j++) {
                dist += customPlaneVector[j]*(p[j]-customPlanePoint[j]);
                tmp += customPlaneVector[j]*customPlaneVector[j];
            }
            dist = Math.abs(dist)/(float)Math.sqrt(tmp);

            int d = 8 - (int)Math.floor(dist/upp);
            GeneralPath path = new GeneralPath();

            if (d > 0) {
               if (d == 8) {
                   boolean selectedPoint = false;
                   if(selectedPoints != null)
                       for (int j = 0; j < selectedPoints.length; j++) {
                           if(i == selectedPoints[j]) {
                               selectedPoint = true;
                               break;
                           }
                       }
                   
                   if(selectedPoint) {
                       gr.setColor(new Color(200, 0, 255));
                   } else {
                       gr.setColor(new Color(255, 0, 0));
                   }
               } else {
                  gr.setColor(new Color(150, 0, 0));
               }

               int[] jj = realToPanelCoords(p);
               jx=jj[0];
               jy=jj[1];


               path.moveTo(jx - d, jy);
               path.lineTo(jx + d, jy);
               path.moveTo(jx, jy - d);
               path.lineTo(jx, jy + d);
               if (paintPointLabels && d == 8)
                  gr.drawString(points.get(i).getName(), jx + 3, jy - 3);
            }
            gr.draw(path);


        }

        //---------paint drag line-----------------------
        if(holdingNewPoint) {
            gr.setColor(Color.RED);

            gr.drawLine(dragLine[0][0], dragLine[0][1],dragLine[1][0], dragLine[1][1]);
            int[] c = new int[2];
            c[0] = (int)Math.round(dragLineCenter[0]);
            c[1] = (int)Math.round(dragLineCenter[1]);
            gr.drawLine(c[0]-3, c[1],
                        c[0]+3, c[1]);
            gr.drawLine(c[0], c[1]-3,
                        c[0], c[1]+3);
            float d = (float)(Math.sqrt( (dragLine[1][0]-dragLine[0][0])*(dragLine[1][0]-dragLine[0][0]) + (dragLine[1][1]-dragLine[0][1])*(dragLine[1][1]-dragLine[0][1]) ));
            int cx = (int)Math.round((float)c[0]-d/2.0f);
            int cy = (int)Math.round((float)c[1]-d/2.0f);
            int cd = (int)Math.round(d);
            gr.drawOval(cx, cy, cd, cd);
        }




        //---------paint slice info-----------------------
        if(paintSliceInfo) {
            gr.setColor(Color.YELLOW);
            gr.drawString(sliceInfoString, 5, 2+gr.getFontMetrics().getHeight());
        }

        //--------------------------------------------
        gr.setColor(oldColor);
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

        if(dataImage != null) {
            imageSizeWidth = uppW * (float)dataImage.getWidth();
            imageSizeHeight = uppH * (float)dataImage.getHeight();
        } else {
            imageSizeWidth = 1.0f;
            imageSizeHeight = 1.0f;
        }
        scaleImage();
        update();
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
        return p/dw;
    }

    private float panelToImageCoordsVertical(float p) {
        return p/dh;
    }

    private float[] panelToRealCoords(float[] p) {
        float[] out = new float[3];
        float x,y;
        x = panelToImageCoordsHorizontal(p[0])-0.5f;
        y = panelToImageCoordsVertical(p[1])-0.5f;

        for (int i = 0; i < 3; i++) {
            out[i] = extents[0][i] + x*base[0][i] + y*base[1][i];
        }
        return out;
    }

    private int[] realToPanelCoords(float[] p) {
        int[] out = new int[2];
        float[] tmp0 = new float[3];
        for (int j = 0; j < 3; j++) {
            tmp0[j] = Math.abs(base[0][j] + base[1][j]);
        }
        float min = tmp0[0];
        int mini = 0;
        if(tmp0[1] < min) {
            min = tmp0[1];
            mini = 1;
        }
        if(tmp0[2] < min) {
            min = tmp0[2];
            mini = 2;
        }

        int i0=0,i1=1;
        switch(mini) {
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
        float detA = base[0][i0]*base[1][i1] - base[1][i0]*base[0][i1];
        int ix,iy;

        ix = (int)Math.round(Math.abs((tmp0[i0]*base[1][i1] - base[1][i0]*tmp0[i1])/detA));
        iy = (int)Math.round(Math.abs((tmp0[i1]*base[0][i0] - base[0][i1]*tmp0[i0])/detA));
        out[0] = Math.round((float)ix*dw + dw/2.0f);
        out[1] = Math.round((float)iy*dh + dh/2.0f);
        return out;
    }

    private boolean holdingNewPoint = false;
    private int[][] dragLine = new int[2][2];
    private float[] dragLineCenter = new float[2];


    @Override
    public void mouseClicked(MouseEvent e) {
        float[] pxy = new float[2];
        pxy[0] = (float)e.getX();
        pxy[1] = (float)e.getY();
        float[] p = panelToRealCoords(pxy);
        if(e.getButton() == MouseEvent.BUTTON1) {
            if(e.getClickCount() > 1) {
                fireNewOrthoSlicesLocation(p);
            }
        } else if(e.getButton() == MouseEvent.BUTTON3) {
            //fireGeometryPointAdded(p);

        }

    }

    @Override
    public void mousePressed(MouseEvent e) {
        int x, y;
        x = e.getX();
        y = e.getY();
        if(x<0 || y<0 || x >= w || y >= h)
            return;

        if(e.getButton() == MouseEvent.BUTTON1) {

        } else if(e.getButton() == MouseEvent.BUTTON2) {

        } else if(e.getButton() == MouseEvent.BUTTON3) {
            holdingNewPoint = true;
            dragLine[0][0] = x;
            dragLine[0][1] = y;
            dragLineCenter[0] = x;
            dragLineCenter[1] = y;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON1) {

        } else if(e.getButton() == MouseEvent.BUTTON2) {

        } else if(e.getButton() == MouseEvent.BUTTON3) {
            holdingNewPoint = false;
            if(dragLineCenter[0] < 0)
                dragLineCenter[0] = 0;
            if(dragLineCenter[0] >= w)
                dragLineCenter[0] = w;
            if(dragLineCenter[1] < 0)
                dragLineCenter[1] = 0;
            if(dragLineCenter[1] >= h)
                dragLineCenter[1] = h;

            float[] p = panelToRealCoords(dragLineCenter);
            fireGeometryPointAdded(p);
        }

    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        int x, y;
        x = e.getX();
        y = e.getY();
        if(holdingNewPoint) {
            dragLine[1][0] = x;
            dragLine[1][1] = y;
            dragLineCenter[0] = (float)(dragLine[1][0]+dragLine[0][0])/2.0f;
            dragLineCenter[1] = (float)(dragLine[1][1]+dragLine[0][1])/2.0f;
            repaint();
        }

    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        //float scale = 0.01f;
        float[] p = new float[3];
        for (int i = 0; i < 3; i++) {
            p[i] = customPlanePoint[i] - customPlaneVector[i]*e.getWheelRotation()*upp;
        }
        setCustomPlanePoint(p);
        fireCustomPlaneChanged(p, customPlaneVector);
    }

    private void fireCustomPlaneChanged(float[] p, float[] v) {
        for (ViewPanelListener listener : viewPanelListeners) {
            listener.onViewPanelEvent(new CustomPlaneChangedViewPanelEvent(this, p, v));
        }
    }

    private void fireGeometryPointAdded(float[] p) {
        for (ViewPanelListener listener : viewPanelListeners) {
            listener.onViewPanelEvent(new GeometryPointAddedCustomPanelEvent(this, p));
        }
    }

    private void fireNewOrthoSlicesLocation(float[] p) {
        for (ViewPanelListener listener : viewPanelListeners) {
            listener.onViewPanelEvent(new NewOrthoSlicesLocationCustomPanelEvent(this, p));
        }
    }




    /**
     * @return the autoScaling
     */
    public boolean isAutoScaling() {
        return autoScaling;
    }

    /**
     * @param autoScaling the autoScaling to set
     */
    public void setAutoScaling(boolean autoScaling) {
        this.autoScaling = autoScaling;
        update();
    }

    /**
     * @return the imageScale
     */
    public float getImageScale() {
        return imageScale;
    }

    /**
     * @param imageScale the imageScale to set
     */
    public void setImageScale(float imageScale) {
        if(dataImage == null)
            return;

        autoScaling = false;
        this.imageScale = imageScale;
        scaleImage();
        update();
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
        if(dataImage == null)
            return this.getMinimumSize();
        return new Dimension(w, h);
    }

    /**
     * @param customPlanePoint the customPlanePoint to set
     */
    private void setCustomPlanePoint(float[] customPlanePoint) {
        this.customPlanePoint = customPlanePoint;
    }

    /**
     * @param customPlaneVector the customPlaneVector to set
     */
    private void setCustomPlaneVector(float[] customPlaneVector) {
        this.customPlaneVector = customPlaneVector;
    }

    public void setCustomPlaneParams(float[] customPlanePoint, float[] customPlaneVector, float[][] extents, float[][] base) {
        this.customPlaneVector = customPlaneVector;
        this.customPlanePoint = customPlanePoint;
        this.extents = extents;
        this.base = base;
    }

    public void setPoints(ArrayList<PointDescriptor> points) {
        this.points = points;
        repaint();
    }

    public void setSelectedPoints(int[] selectedPoints) {
        if(selectedPoints == null) {
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
}
