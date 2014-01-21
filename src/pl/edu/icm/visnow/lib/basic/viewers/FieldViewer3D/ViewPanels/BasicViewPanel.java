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
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.event.ChangeEvent;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.CalculableParameter;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.ConnectionDescriptor;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.DependantPointDescriptor;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.PointDescriptor;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.GeometryTools.GeometryTool;

/**
 * @author  Bartosz Borucki (babor@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */

public class BasicViewPanel extends ViewPanel implements ComponentListener, MouseListener, MouseMotionListener, MouseWheelListener {
    float[] dash =
    {
        1.f, 2.f
    };
    private BasicStroke ba = new BasicStroke(1.f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 2.0f, dash, 0.f);
    
    private BufferedImage dataImage = null;
    private BufferedImage overlayImage = null;

    private float overlayImageOpacity = 0.5f;

    private float imageScale = 1.0f;
    private int imagePosX = 0;
    private int imagePosY = 0;
    private int translateStartPointX = 0;
    private int translateStartPointY = 0;
    private boolean translating = false;
    private float zoom = 1.0f;

    public static final int SCALING_EXTERNAL = 0;
    public static final int SCALING_AUTO = 1;
    public static final int SCALING_MANUAL = 2;
    private int scalingMode = SCALING_AUTO;


    private float imageSizeWidth = 1.0f;
    private float imageSizeHeight = 1.0f;
    private float uppW = 1.0f, uppH = 1.0f, upp=1.0f;

    private int w = 0;
    private int h = 0;
    private float dw = 1.0f;
    private float dh = 1.0f;

    private ArrayList<PointDescriptor> points = new ArrayList<PointDescriptor>();
    private ArrayList<ConnectionDescriptor> pointConnections = new ArrayList<ConnectionDescriptor>();
    private ArrayList<CalculableParameter> cps = new ArrayList<CalculableParameter>();
    private int[] selectedPoints = null;
    private boolean paintPointLabels = true;
    private boolean paintConnections = false;
    private boolean paintDistances = false;
    private boolean paintCalculableValues = false;
    private DecimalFormat df = new DecimalFormat("###.##");
    
    private final int mouseOnmaskShift = MouseEvent.SHIFT_DOWN_MASK;
    private final int mouseOnmaskCtrl = MouseEvent.CTRL_DOWN_MASK;
    private final int wheelOnmaskShift = MouseWheelEvent.SHIFT_DOWN_MASK;

    private float[] sliceRealPoint0 = {0.0f, 0.0f, 0.0f};

    private boolean holdingWindowRange = false;
    private float[] lastPoint = new float[3];

    private static Cursor customMoveCursor;
    private static boolean cursorsInitialized = false;
    private boolean holdingPoint = false;
    private int holdingPointIndex = -1;
    private boolean mouseOver = false;


    public BasicViewPanel() {
        super();
        if(!cursorsInitialized)
            initCursors();
        this.setName("2D");
        this.setType(VIEW_2D);
        this.addComponentListener(this);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);

        //this.setGeometryTool(new LineTool());
        //this.setGeometryTool(new CenterPointTool());
        //this.setGeometryTool(new PointTool());
    }

    @Override
    public void update() {
        if(dataImage == null)
            return;

        switch(scalingMode) {
            case SCALING_AUTO:
                zoom = 1.0f;
                imagePosX = 0;
                imagePosY = 0;
                float newScale = 1.0f;
                Dimension myDim = this.getSize();
                float xs,ys;
                xs = (float)myDim.width/getPrefferedDataImageWidth();
                ys = (float)myDim.height/getPrefferedDataImageHeight();
                newScale = Math.min(xs, ys);
                imageScale = newScale;
                scaleImage();
                break;
            case SCALING_EXTERNAL:
                break;
            case SCALING_MANUAL:
                break;
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


    private void initCursors() {
        if(cursorsInitialized)
            return;
        
        try {
           Toolkit toolkit = Toolkit.getDefaultToolkit();

           Image cursorImage1 = ImageIO.read(getClass().getResourceAsStream("/pl/edu/icm/visnow/gui/icons/cursor_move.gif"));
           Point cursorHotSpot1 = new Point(0,0);
           cursorHotSpot1.x = 14;
           cursorHotSpot1.y = 14;
           customMoveCursor = toolkit.createCustomCursor(cursorImage1, cursorHotSpot1, "MoveCursor");

        } catch (IOException e) {
            customMoveCursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
        }
        cursorsInitialized = true;

    }

    private void scaleImage() {
        if(dataImage == null) {
            return;
        }
        this.w = (int)Math.round(zoom*imageScale*imageSizeWidth/upp);
        this.h = (int)Math.round(zoom*imageScale*imageSizeHeight/upp);
        if(upp == uppW)
            this.dw = zoom*imageScale;
        else
            this.dw = zoom*imageScale*uppW/upp;

        if(upp == uppH)
            this.dh = zoom*imageScale;
        else
            this.dh = zoom*imageScale*uppH/upp;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D gr = (Graphics2D) g;
        Color oldColor = gr.getColor();

        //---------clear background----------------------
        //gr.setColor(Color.DARK_GRAY);
        gr.setColor(Color.GRAY);
        gr.fillRect(0, 0, this.getWidth(), this.getHeight());


        gr.translate(imagePosX, imagePosY);
        //---------paint data----------------------------
        gr.drawImage(dataImage, 0, 0, w, h, null);


        //---------paint overlay-------------------------
        if(overlayImage != null) {
            AlphaComposite c = (AlphaComposite) gr.getComposite();
            gr.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, overlayImageOpacity));
            gr.drawImage(overlayImage, 0, 0, w, h, null);
            gr.setComposite(c);
        }


        GeneralPath p;
        int jx, jy;

        //---------paint 2D connections------------------------
        if (paintConnections && pointConnections.size() > 0)  {
            gr.setColor(Color.YELLOW);
            gr.setStroke(new BasicStroke());
            p = new GeneralPath();
            int[] c1, c2, c0 = new int[2];
            for (int i = 0; i < pointConnections.size(); i++) {
              ConnectionDescriptor c = pointConnections.get(i);
              c1 = c.getP1().getIndices();
              c2 = c.getP2().getIndices();
              jx = imageCoordsToPanelHorizontal(c1[0], true);
              jy = imageCoordsToPanelVertical(c1[1], true);
              p.moveTo(jx, jy);
              jx = imageCoordsToPanelHorizontal(c2[0], true);
              jy = imageCoordsToPanelVertical(c2[1], true);
              p.lineTo(jx, jy);
              if(paintDistances) {
                    c0[0] = (c1[0]+c2[0])/2;
                    c0[1] = (c1[1]+c2[1])/2;
                    jx = imageCoordsToPanelHorizontal(c0[0], true);
                    jy = imageCoordsToPanelVertical(c0[1], true);
                    gr.drawString(df.format(c.getLength()), jx+10, jy+15);
              }
            }
            gr.draw(p);

         }

        //---------paint points-------------------------
        gr.setStroke(new BasicStroke());
        for (int i = 0; i < points.size(); i++) {
           int[] point = points.get(i).getIndices();
           if(point == null)
               continue;
           int d = 8;
           p = new GeneralPath();
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
           } else if(points.get(i) instanceof DependantPointDescriptor) {
               gr.setColor(new Color(155, 0, 0));
           } else {
               gr.setColor(new Color(255, 0, 0));
           }

           jx = imageCoordsToPanelHorizontal(point[0], true);
           jy = imageCoordsToPanelVertical(point[1], true);
           p.moveTo(jx - d, jy);
           p.lineTo(jx + d, jy);
           p.moveTo(jx, jy - d);
           p.lineTo(jx, jy + d);
           if (paintPointLabels)
              gr.drawString(points.get(i).getName(), jx + 3, jy - 3);
            gr.draw(p);
        }


        //---------paint 2D calculable values------------------------
        if (paintCalculableValues && cps.size() > 0)  {
            gr.setColor(Color.BLUE);
            gr.setStroke(new BasicStroke());
            int[] c;
            CalculableParameter cp;
            for (int i = 0; i < cps.size(); i++) {
              cp = cps.get(i);
              c = cp.getLocationPoint().getIndices();
              jx = imageCoordsToPanelHorizontal(c[0], true);
              jy = imageCoordsToPanelVertical(c[1], true);
              gr.drawString(df.format(cp.getValue()), jx+10, jy+15);
            }
         }


        //------------paint geometry tool----------------------
        if(geomTool != null) {
            g.translate(-imagePosX, -imagePosY);
            geomTool.paint(g);
            g.translate(imagePosX, imagePosY);
        }


        //--------------------------------------------
        gr.setStroke(new BasicStroke());
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
    public void setDataImage(BufferedImage dataImage, float[] sliceRealPoint0, float uppW, float uppH, float upp) {
        this.dataImage = dataImage;
        this.uppW = uppW;
        this.uppH = uppH;
        this.upp = upp;
        this.sliceRealPoint0 = sliceRealPoint0;

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

    public void setIsoline(ArrayList<float[][]> isoline) {
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

    private int panelToImageCoordsHorizontal(int p) {
        return (int)Math.floor((float)p/dw);
    }

    private int panelToImageCoordsVertical(int p) {
        return (int)Math.floor((float)p/dh);
    }

    private float panelToRealImageCoordsHorizontal(int p) {
        return (float)p*upp/(zoom*imageScale) + sliceRealPoint0[0];
    }

    private float panelToRealImageCoordsVertical(int p) {
        return (float)p*upp/(zoom*imageScale) + sliceRealPoint0[1];
    }

    private int realImageCoordsToPanelHorizontal(float p) {
        return (int)Math.round((p-sliceRealPoint0[0])*(imageScale*zoom)/upp);
    }

    private int realImageCoordsToPanelVertical(float p) {
        return (int)Math.round((p-sliceRealPoint0[1])*(imageScale*zoom)/upp);
    }

    private int imageCoordsToPanelHorizontal(int p, boolean center) {
        if(center)
            return (int)Math.round(p*dw + dw/2);
        else
            return (int)Math.round(p*dw);
    }

    private int imageCoordsToPanelVertical(int p, boolean center) {
        if(center)
            return (int)Math.round(p*dh + dh/2);
        else
            return (int)Math.round(p*dh);
    }

    private float imageCoordsToPanelHorizontal(float p, boolean center) {
        if(center)
            return p*dw + dw/2;
        else
            return p*dw;
    }

    private float imageCoordsToPanelVertical(float p, boolean center) {
        if(center)
            return p*dh + dh/2;
        else
            return p*dh;
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        if(dataImage == null)
            return;

        int x, y;
        x = e.getX()-imagePosX;
        y = e.getY()-imagePosY;
        if(x < 0 || y < 0 || x >= w || y >= h)
            return;

        int imgX, imgY;
        imgX = panelToImageCoordsHorizontal(x);
        if(imgX < 0)
            imgX = 0;
        if(imgX >= dataImage.getWidth())
            imgX = dataImage.getWidth()-1;

        imgY = panelToImageCoordsVertical(y);
        if(imgY < 0)
            imgY = 0;
        if(imgY >= dataImage.getHeight())
            imgY = dataImage.getHeight()-1;

        if(e.getButton() == MouseEvent.BUTTON1) {


        } else if(e.getButton() == MouseEvent.BUTTON2) {


        } else if(e.getButton() == MouseEvent.BUTTON3) {
            if(geomTool != null) {
                geomTool.mouseClicked(e);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(dataImage == null)
            return;

        int x, y;
        x = e.getX()-imagePosX;
        y = e.getY()-imagePosY;
        if(x<0 || y<0 || x >= w || y >= h)
            return;

        if(e.getButton() == MouseEvent.BUTTON1) {
            if ((e.getModifiersEx() & mouseOnmaskShift) == mouseOnmaskShift) {
                fireZoomChanged();
                this.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                translateStartPointX = x+imagePosX;
                translateStartPointY = y+imagePosY;
                translating = true;
            } else if((e.getModifiersEx() & mouseOnmaskCtrl) == mouseOnmaskCtrl) {
                //point catching
                int jx, jy;
                for (int i = 0; i < points.size(); i++) {
                    if(points.get(i).isDependant()) continue;
                   int[] point = points.get(i).getIndices();
                   jx = imageCoordsToPanelHorizontal(point[0], true);
                   jy = imageCoordsToPanelVertical(point[1], true);
                   if(Math.abs(jx-x) < 3 && Math.abs(jy-y) < 3 ) {
                       holdingPoint = true;
                       holdingPointIndex = i;
                       fireGeometryPointSelected(i, false);
                       break;
                   }
                }
            } else {


            }
        } else if(e.getButton() == MouseEvent.BUTTON2) {
            holdingWindowRange = true;
            lastPoint[0] = x;
            lastPoint[1] = y;
        } else if(e.getButton() == MouseEvent.BUTTON3) {
            if(geomTool != null) {
                geomTool.mousePressed(e);
                return;
            }
            
        }
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(dataImage == null)
            return;

        if(e.getButton() == MouseEvent.BUTTON1) {
            translating = false;
            if(holdingPoint) {
                holdingPoint = false;
                holdingPointIndex = -1;
                fireGeometryPointSelected(-1, false);
            }
        } else if(e.getButton() == MouseEvent.BUTTON2) {
            holdingWindowRange = false;
        } else if(e.getButton() == MouseEvent.BUTTON3) {
            if(geomTool != null) {
                geomTool.mouseReleased(e);
                return;
            }
        }
        mouseMoved(e);
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if(dataImage == null)
            return;

        if(geomTool != null) {
            this.setCursor(geomTool.getCursor());
            geomTool.mouseEntered(e);
        }

        mouseOver = true;
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if(dataImage == null)
            return;

        if(geomTool != null) {
            this.setCursor(geomTool.getCursor());
            geomTool.mouseExited(e);
        }

        mouseOver = false;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if(dataImage == null)
            return;

        int x, y, imgX, imgY;
        x = e.getX()-imagePosX;
        y = e.getY()-imagePosY;

        imgX = panelToImageCoordsHorizontal(x);
        if(imgX < 0)
            imgX = 0;
        if(imgX >= dataImage.getWidth())
            imgX = dataImage.getWidth()-1;

        imgY = panelToImageCoordsVertical(y);
        if(imgY < 0)
            imgY = 0;
        if(imgY >= dataImage.getHeight())
            imgY = dataImage.getHeight()-1;

        if (translating && ((e.getModifiersEx() & mouseOnmaskShift) == mouseOnmaskShift)) {
            fireZoomChanged();
            x += imagePosX;
            y += imagePosY;
            imagePosX += (x-translateStartPointX);
            imagePosY += (y-translateStartPointY);
            translateStartPointX = x;
            translateStartPointY = y;
            repaint();
        } else if (holdingPoint && ((e.getModifiersEx() & mouseOnmaskCtrl) == mouseOnmaskCtrl)) {
            int[] clickedPoint = new int[3];
            clickedPoint[0] = imgX;
            clickedPoint[1] = imgY;
            clickedPoint[2] = 0;
            fireSelectedPointMoved(holdingPointIndex, clickedPoint);
        } else {
            if(holdingWindowRange) {
                float dx = (float)x-lastPoint[0];
                float dy = lastPoint[1]-(float)y;
                lastPoint[0] = x;
                lastPoint[1] = y;
                fireMappingRangeChanged(dx, dy);
            } else {
                if(geomTool != null) {
                    geomTool.mouseDragged(e);
                }
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if(dataImage == null)
            return;

        if(geomTool != null) {
            this.setCursor(geomTool.getCursor());
            geomTool.mouseMoved(e);
            return;
        }


        if(this.getCursor() != Cursor.getDefaultCursor()) {
            this.setCursor(Cursor.getDefaultCursor());
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if(dataImage == null)
            return;

        if ((e.getModifiersEx() & wheelOnmaskShift) == wheelOnmaskShift) {
            int x, y, imgX, imgY;
            x = e.getX()-imagePosX;
            y = e.getY()-imagePosY;
            imgX = panelToImageCoordsHorizontal(x);
            imgY = panelToImageCoordsVertical(y);

            fireZoomChanged();
            if(e.getWheelRotation() < 0)
                zoom = zoom*1.1f;
            else
                zoom = zoom / 1.1f;

            scaleImage();
            x -= imageCoordsToPanelHorizontal(imgX, true);
            y -= imageCoordsToPanelVertical(imgY, true);

            imagePosX += x;
            imagePosY += y;
            
            update();
        } else {
            
        }
    }

    public int getScalingMode() {
        return scalingMode;
    }

    public void setScalingMode(int scalingMode) {
        this.scalingMode = scalingMode;
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

        scalingMode = SCALING_EXTERNAL;
        this.imageScale = imageScale;
        zoom = 1.0f;
        imagePosX = 0;
        imagePosY = 0;
        scaleImage();
        update();
    }

    @Override
    public Dimension getPreferredViewSize() {
        if(dataImage == null)
            return this.getMinimumSize();
        return new Dimension(w, h);
    }

    private void fireSelectedPointMoved(int pIndex, int[] p) {
        for (ViewPanelListener listener : viewPanelListeners) {
            listener.onViewPanelEvent(new GeometryPointMovedOrthoPanelEvent(this, pIndex, p));
        }
    }

    private void fireGeometryPointSelected(int pIndex, boolean followSlices) {
        for (ViewPanelListener listener : viewPanelListeners) {
            listener.onViewPanelEvent(new GeometryPointSelectedOrthoPanelEvent(this, pIndex, followSlices));
        }
    }

    private void firePointsConnectionsCalculablesAdded(int[][] points, int[][] connections, CalculableParameter calculable) {
        for (ViewPanelListener listener : viewPanelListeners) {
            listener.onViewPanelEvent(new PCCAddedOrthoPanelEvent(this, points, connections, calculable));
        }
    }

    private void fireMappingRangeChanged(float dCenter, float dWidth) {
        for (ViewPanelListener listener : viewPanelListeners) {
            listener.onViewPanelEvent(new MappingRangeChangedOrthoPanelEvent(this, dCenter, dWidth));
        }
    }

    private void fireZoomChanged() {
        for (ViewPanelListener listener : viewPanelListeners) {
            listener.onViewPanelEvent(new ZoomChangedOrthoPanelEvent(this));
        }
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

    public void setCalculableParameters(ArrayList<CalculableParameter> cps) {
        this.cps = cps;
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
     * @param paintCalculableValues the paintCalculableValues to set
     */
    public void setPaintCalculableValues(boolean paintCalculableValues) {
        this.paintCalculableValues = paintCalculableValues;
        repaint();
    }

    /**
     * @param overlayImage the overlayImage to set
     */
    public void setOverlayImage(BufferedImage image, float opacity) {
        this.overlayImage = image;
        this.overlayImageOpacity = opacity;
        repaint();
    }

    public void setOverlayOpacity(float opacity) {
        this.overlayImageOpacity = opacity;
        repaint();
    }

    @Override
    public void onGeometryToolStateChanged(ChangeEvent e) {
        Object src = e.getSource();
        if(src instanceof GeometryTool) {
            GeometryTool gt = (GeometryTool)src;
            int[][] gtPoints = gt.getPoints();
            int[][] gtConnections = gt.getConnections();
            
            if(gtPoints == null)
                return;

            int nPts = gtPoints.length;
            int[][] outPoints = new int[nPts][3];
            for (int i = 0; i < nPts; i++) {
                outPoints[i][0] = panelToImageCoordsHorizontal(gtPoints[i][0]-imagePosX);
                outPoints[i][1] = panelToImageCoordsVertical(gtPoints[i][1]-imagePosY);
                outPoints[i][2] = 0;
            }

            firePointsConnectionsCalculablesAdded(outPoints, gtConnections, gt.getCalculable());
        }
    }

}
