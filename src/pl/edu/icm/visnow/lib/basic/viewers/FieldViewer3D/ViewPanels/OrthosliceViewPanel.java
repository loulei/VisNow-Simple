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
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.event.ChangeEvent;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.CalculableParameter;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.ConnectionDescriptor;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.PointDescriptor;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.GeometryTools.GeometryTool;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.GeometryTools.Metadata;
import pl.edu.icm.visnow.lib.utils.ImageUtilities;

/**
 * @author  Bartosz Borucki (babor@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */

public class OrthosliceViewPanel extends ViewPanel implements ComponentListener, MouseListener, MouseMotionListener, MouseWheelListener {
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

    private int axis = -1;
    private int horizAxis = -1;
    private int vertAxis = -1;
    private boolean trans = false;

    private int horizontalSlicePosition = 10;
    private int verticalSlicePosition = 10;
    private int scaledHorizontalSlicePosition = 10;
    private int scaledVerticalSlicePosition = 10;

    private float[] customPlanePoint = {0.0f, 0.0f, 0.0f};
    private float[] customPlaneVector = {1.0f, 0.0f, 0.0f};
    private GeneralPath customSlicePath = null;

    private int w = 0;
    private int h = 0;
    private float dw = 1.0f;
    private float dh = 1.0f;


    public static final int MODE_ORTHOSLICES = 0;
    public static final int MODE_CUSTOMSLICE = 1;

    private int mode = MODE_ORTHOSLICES;

    public static final int SLICE_LINES_COLORED = 0;
    public static final int SLICE_LINES_BLUE = 1;
    public static final int SLICE_LINES_WHITE = 2;
    public static final int SLICE_LINES_BLACK = 3;
    public static final int SLICE_LINES_NONE = 4;
    private int sliceLinesMode = SLICE_LINES_COLORED;
    
    private boolean paintSliceInfo = false;
    private String sliceInfoString = "";
    private int sliceNumber = 0;
    private float[] sliceRealPoint0 = {0.0f, 0.0f, 0.0f};

    private ArrayList<PointDescriptor> points = new ArrayList<PointDescriptor>();
    private ArrayList<ConnectionDescriptor> pointConnections = new ArrayList<ConnectionDescriptor>();
    private ArrayList<CalculableParameter> cps = new ArrayList<CalculableParameter>();
    private int[] selectedPoints = null;
    private boolean paintPointLabels = true;
    private boolean paintConnections = false;
    private boolean paintDistances = false;
    private boolean paintCalculableValues = false;
    private DecimalFormat df = new DecimalFormat("###.##");
    private DecimalFormat df2 = new DecimalFormat("###.###");

    private ArrayList<float[][]> isoline = null;
    private ArrayList<float[][]>[] isolines = null;

    private final int mouseOnmaskShift = MouseEvent.SHIFT_DOWN_MASK;
    private final int mouseOnmaskCtrl = MouseEvent.CTRL_DOWN_MASK;
    private final int wheelOnmaskShift = MouseWheelEvent.SHIFT_DOWN_MASK;

    private boolean holdingPoint = false;
    private int holdingPointIndex = -1;

    private float[] currentMousePosition = new float[2];

    private Color borderColor = Color.BLUE;
    private Color horizColor = Color.BLUE;
    private Color vertColor = Color.BLUE;
    
    private CursorProvider cp = new CursorProvider();
    
    public OrthosliceViewPanel(int axis, boolean trans) {
        super();
        this.axis = axis;
        this.trans = trans;
        switch(axis) {
            case 0:
                if(trans) {
                    this.setName("slice i trans");
                    this.setType(VIEW_SLICE_I_TRANS);
                    sliceInfoString = "KJ";
                    horizAxis = 2;
                    vertAxis = 1;
                } else {
                    this.setName("slice i");
                    this.setType(VIEW_SLICE_I);
                    sliceInfoString = "JK";
                    horizAxis = 1;
                    vertAxis = 2;
                }
                break;
            case 1:
                if(trans) {
                    this.setName("slice j trans");
                    this.setType(VIEW_SLICE_J_TRANS);
                    sliceInfoString = "KI";
                    horizAxis = 2;
                    vertAxis = 0;
                } else {
                    this.setName("slice j");
                    this.setType(VIEW_SLICE_J);
                    sliceInfoString = "IK";
                    horizAxis = 0;
                    vertAxis = 2;
                }
                break;
            case 2:
                if(trans) {
                    this.setName("slice k trans");
                    this.setType(VIEW_SLICE_K_TRANS);
                    sliceInfoString = "JI";
                    horizAxis = 1;
                    vertAxis = 0;
                } else {
                    this.setName("slice k");
                    this.setType(VIEW_SLICE_K);
                    sliceInfoString = "IJ";
                    horizAxis = 0;
                    vertAxis = 1;
                }
                break;
        }
        updateSliceLinesColors();
        this.addComponentListener(this);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);

        if(geomTool != null) {
            geomTool.setMetadata(new Metadata(new Integer(sliceNumber)));
        }
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

    private void scaleImage() {
        if(dataImage == null) {
            return;
        }
        this.w = Math.round(zoom*imageScale*imageSizeWidth/upp);
        this.h = Math.round(zoom*imageScale*imageSizeHeight/upp);
        if(upp == uppW)
            this.dw = zoom*imageScale;
        else
            this.dw = zoom*imageScale*uppW/upp;

        if(upp == uppH)
            this.dh = zoom*imageScale;
        else
            this.dh = zoom*imageScale*uppH/upp;

        updateSlicePositions();
        updateCustomSlicePath();
    }

    private void updateSlicePositions() {
        scaledHorizontalSlicePosition = Math.round(dh/2.0f + (float)horizontalSlicePosition*dh);
        scaledVerticalSlicePosition = Math.round(dw/2.0f + (float)verticalSlicePosition*dw);
    }


    float[] dash =
    {
        1.f, 2.f
    };
    private BasicStroke dashedLine = new BasicStroke(1.f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 2.0f, dash, 0.f);
    private BasicStroke solidLine = new BasicStroke(1.f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 2.0f);

    private int customsliceCircleRadius = 10;

    private void updateSliceLinesColors() {
        switch(sliceLinesMode) {
            case SLICE_LINES_COLORED:
                switch(axis) {
                    case 0:
                        if(trans) {
                            borderColor = Color.RED;
                            horizColor = Color.GREEN;
                            vertColor = Color.BLUE;
                        } else {
                            borderColor = Color.RED;
                            horizColor = Color.BLUE;
                            vertColor = Color.GREEN;
                        }
                        break;
                    case 1:
                        if(trans) {
                            borderColor = Color.GREEN;
                            horizColor = Color.RED;
                            vertColor = Color.BLUE;
                        } else {
                            borderColor = Color.GREEN;
                            horizColor = Color.BLUE;
                            vertColor = Color.RED;
                        }
                        break;
                    case 2:
                        if(trans) {
                            borderColor = Color.BLUE;
                            horizColor = Color.RED;
                            vertColor = Color.GREEN;
                        } else {
                            borderColor = Color.BLUE;
                            horizColor = Color.GREEN;
                            vertColor = Color.RED;
                        }
                        break;
                }
                break;
            case SLICE_LINES_BLUE:
                borderColor = Color.BLUE;
                horizColor = Color.BLUE;
                vertColor = Color.BLUE;
                break;
            case SLICE_LINES_WHITE:
                borderColor = Color.WHITE;
                horizColor = Color.WHITE;
                vertColor = Color.WHITE;
                break;
            case SLICE_LINES_BLACK:
                borderColor = Color.BLACK;
                horizColor = Color.BLACK;
                vertColor = Color.BLACK;
                break;                        
        }        
        repaint();
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

       //--------paint isoline----------------------------
       if (isoline != null)
          paintIsoline(gr, isoline, Color.GREEN);
       //--------paint isoline----------------------------
       if (isolines != null && isolines.length == 2)
       {
          paintIsoline(gr, isolines[0], Color.BLUE);
          paintIsoline(gr, isolines[1], Color.RED);
       }

        GeneralPath p;
        if(sliceLinesMode != SLICE_LINES_NONE) {
            if(mode == MODE_ORTHOSLICES) {
                //---------paint orthoslice lines---------------------
                gr.setStroke(dashedLine);
                if(horizontalSlicePosition != -1) {
                    gr.setColor(horizColor);
                    p = new GeneralPath();
                    p.moveTo(0, scaledHorizontalSlicePosition);
                    p.lineTo(scaledVerticalSlicePosition-10, scaledHorizontalSlicePosition);
                    p.moveTo(scaledVerticalSlicePosition+10, scaledHorizontalSlicePosition);
                    p.lineTo(w, scaledHorizontalSlicePosition);
                    gr.draw(p);
                }
                if(verticalSlicePosition != -1) {
                    gr.setColor(vertColor);
                    p = new GeneralPath();
                    p.moveTo(scaledVerticalSlicePosition, 0);
                    p.lineTo(scaledVerticalSlicePosition, scaledHorizontalSlicePosition-10);
                    p.moveTo(scaledVerticalSlicePosition, scaledHorizontalSlicePosition+10);
                    p.lineTo(scaledVerticalSlicePosition, h);
                    gr.draw(p);
                }

            } else if(mode == MODE_CUSTOMSLICE) {
                //---------paint custom slice lines---------------------
                gr.setColor(Color.MAGENTA);
                gr.setStroke(dashedLine);

                if(rotatingCustomSlice)
                    gr.setStroke(new BasicStroke());
                else
                    gr.setStroke(dashedLine);


                customsliceCircleRadius = 10;
                int d = (int)Math.floor(Math.abs(customPlanePoint[axis]-sliceRealPoint0[axis])/upp);
                customsliceCircleRadius -= 2*d;
                if(customsliceCircleRadius < 3)
                    customsliceCircleRadius = 3;

                gr.drawOval(
                        realImageCoordsToPanelHorizontal(customPlanePoint[horizAxis])-customsliceCircleRadius,
                        realImageCoordsToPanelVertical(customPlanePoint[vertAxis])-customsliceCircleRadius,
                        2*customsliceCircleRadius,
                        2*customsliceCircleRadius
                        );
                gr.drawLine(
                        realImageCoordsToPanelHorizontal(customPlanePoint[horizAxis])-customsliceCircleRadius,
                        realImageCoordsToPanelVertical(customPlanePoint[vertAxis]),
                        realImageCoordsToPanelHorizontal(customPlanePoint[horizAxis])+customsliceCircleRadius,
                        realImageCoordsToPanelVertical(customPlanePoint[vertAxis])
                        );
                gr.drawLine(
                        realImageCoordsToPanelHorizontal(customPlanePoint[horizAxis]),
                        realImageCoordsToPanelVertical(customPlanePoint[vertAxis])-customsliceCircleRadius,
                        realImageCoordsToPanelHorizontal(customPlanePoint[horizAxis]),
                        realImageCoordsToPanelVertical(customPlanePoint[vertAxis])+customsliceCircleRadius
                        );

                if(customSlicePath != null) {
                    gr.draw(customSlicePath);
                }
            }
        }

        //---------paint border----------------------
        if(sliceLinesMode == OrthosliceViewPanel.SLICE_LINES_COLORED) {
            gr.setStroke(solidLine);        
            gr.setColor(borderColor);
            p = new GeneralPath();
            p.moveTo(0, 0);
            p.lineTo(w, 0);
            p.lineTo(w, h);
            p.lineTo(0, h);
            p.lineTo(0, 0);
            gr.draw(p);
        }


        //---------paint points-------------------------
        int jx, jy;
        gr.setStroke(new BasicStroke());
        for (int i = 0; i < points.size(); i++) {
            int[] point = points.get(i).getIndices();
            int d = 8 - Math.abs(point[axis] - sliceNumber);
            p = new GeneralPath();

            if (d > 0) {
               if (point[axis] == sliceNumber) {
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
               
               jx = imageCoordsToPanelHorizontal(point[horizAxis], true);
               jy = imageCoordsToPanelVertical(point[vertAxis], true);
               p.moveTo(jx - d, jy);
               p.lineTo(jx + d, jy);
               p.moveTo(jx, jy - d);
               p.lineTo(jx, jy + d);
               if (paintPointLabels && point[axis] == sliceNumber)
                  gr.drawString(points.get(i).getName(), jx + 3, jy - 3);
            }
            gr.draw(p);
        }

        //---------paint 2D connections------------------------
        if (paintConnections && pointConnections.size() > 0)  {
            gr.setColor(Color.YELLOW);
            gr.setStroke(new BasicStroke());
            p = new GeneralPath();
            for (int i = 0; i < pointConnections.size(); i++) {
               ConnectionDescriptor c = pointConnections.get(i);
               if (c.getP2().getIndices()[axis] == sliceNumber && c.getP1().getIndices()[axis] == c.getP2().getIndices()[axis]) {
                  int[] c1 = c.getP1().getIndices();
                  int[] c2 = c.getP2().getIndices();
                  int[] c0 = new int[3];
                  jx = imageCoordsToPanelHorizontal(c1[horizAxis], true);
                  jy = imageCoordsToPanelVertical(c1[vertAxis], true);
                  p.moveTo(jx, jy);
                  jx = imageCoordsToPanelHorizontal(c2[horizAxis], true);
                  jy = imageCoordsToPanelVertical(c2[vertAxis], true);
                  p.lineTo(jx, jy);

                  if(paintDistances) {
                        c0[horizAxis] = (c1[horizAxis]+c2[horizAxis])/2;
                        c0[vertAxis] = (c1[vertAxis]+c2[vertAxis])/2;
                        jx = imageCoordsToPanelHorizontal(c0[horizAxis], true);
                        jy = imageCoordsToPanelVertical(c0[vertAxis], true);
                        gr.drawString(df.format(c.getLength()), jx+10, jy+15);
                  }

               }
            }
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
              if(cp == null || cp.getLocationPoint() == null)
                  continue;
              c = cp.getLocationPoint().getIndices();
              if(c[axis] == sliceNumber) {
                  jx = imageCoordsToPanelHorizontal(c[horizAxis], true);
                  jy = imageCoordsToPanelVertical(c[vertAxis], true);
                  gr.drawString(df.format(cp.getValue()), jx+10, jy+15);
              }
            }
         }


        //---------paint slice info-----------------------
        if(paintSliceInfo) {
            gr.setColor(Color.YELLOW);
            gr.drawString(sliceInfoString+" "+sliceNumber+" "+df2.format(sliceRealPoint0[axis]), 5, 2+gr.getFontMetrics().getHeight());
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

   private void paintIsoline(Graphics2D gr, ArrayList<float[][]> isoline, Color col)
   {
      gr.setColor(col);
      gr.setStroke(new BasicStroke());
      GeneralPath p = new GeneralPath();
      if (trans)
      {
         for (int i = 0; i < isoline.size(); i++)
         {
            float[][] coords = (isoline.get(i));
            if (coords == null || coords.length < 2)
               continue;
            p.moveTo(imageCoordsToPanelHorizontal(coords[0][1], true), imageCoordsToPanelVertical(coords[0][0], true));
            for (int j = 1; j < coords.length; j++)
            {
               p.lineTo(imageCoordsToPanelHorizontal(coords[j][1], true), imageCoordsToPanelVertical(coords[j][0], true));
            }
         }
      } else
      {
         for (int i = 0; i < isoline.size(); i++)
         {
            float[][] coords = (isoline.get(i));
            if (coords == null || coords.length < 2)
               continue;
            p.moveTo(imageCoordsToPanelHorizontal(coords[0][0], true), imageCoordsToPanelVertical(coords[0][1], true));
            for (int j = 1; j < coords.length; j++)
            {
               p.lineTo(imageCoordsToPanelHorizontal(coords[j][0], true), imageCoordsToPanelVertical(coords[j][1], true));
            }
         }
      }
      gr.draw(p);
   }

    private void updateCustomSlicePath() {
        float[][] cornerPoints = new float[4][];
        
        cornerPoints[0] = new float[3];
        cornerPoints[0][horizAxis] = panelToRealImageCoordsHorizontal(0);
        cornerPoints[0][vertAxis] = panelToRealImageCoordsVertical(0);
        cornerPoints[0][axis] = sliceRealPoint0[axis];
        
        cornerPoints[1] = new float[3];
        cornerPoints[1][horizAxis] = panelToRealImageCoordsHorizontal(w-1);
        cornerPoints[1][vertAxis] = panelToRealImageCoordsVertical(0);
        cornerPoints[1][axis] = sliceRealPoint0[axis];
        
        cornerPoints[2] = new float[3];
        cornerPoints[2][horizAxis] = panelToRealImageCoordsHorizontal(w-1);
        cornerPoints[2][vertAxis] = panelToRealImageCoordsVertical(h-1);
        cornerPoints[2][axis] = sliceRealPoint0[axis];
        
        cornerPoints[3] = new float[3];
        cornerPoints[3][horizAxis] = panelToRealImageCoordsHorizontal(0);
        cornerPoints[3][vertAxis] = panelToRealImageCoordsVertical(h-1);
        cornerPoints[3][axis] = sliceRealPoint0[axis];
        
        int[] corners = new int[4];
        float value;
        for (int i = 0; i < 4; i++) {
            value = 0;
            for (int j = 0; j < 3; j++) {
                value += customPlaneVector[j]*(cornerPoints[i][j]-customPlanePoint[j]);
            }
            if(value < 0.0f)
                corners[i] = -1;
            else if(value >= 0)
                corners[i] = 1;
        }

        float xReal1,yReal1,xReal2,yReal2;

        if(corners[0] == corners[1] && corners[1] == corners[2] && corners[2] == corners[3]) {
            customSlicePath = null;
            return;
        }

        customSlicePath = new GeneralPath();
        if( (corners[0] == 1 && corners[1] == -1 && corners[2] == -1 && corners[3] == -1) || (corners[0] == -1 && corners[1] == 1 && corners[2] == 1 && corners[3] == 1)) {
            //case - corners[0] separated
            xReal1 = panelToRealImageCoordsHorizontal(0);
            yReal1 = getPlaneCoord2D(axis, sliceRealPoint0[axis], horizAxis, xReal1);
            yReal2 = panelToRealImageCoordsVertical(0);
            xReal2 = getPlaneCoord2D(axis, sliceRealPoint0[axis], vertAxis, yReal2);
        } else if( (corners[0] == -1 && corners[1] == 1 && corners[2] == -1 && corners[3] == -1) || (corners[0] == 1 && corners[1] == -1 && corners[2] == 1 && corners[3] == 1)) {
            //case - corners[1] separated
            xReal1 = panelToRealImageCoordsHorizontal(w-1);
            yReal1 = getPlaneCoord2D(axis, sliceRealPoint0[axis], horizAxis, xReal1);
            yReal2 = panelToRealImageCoordsVertical(0);
            xReal2 = getPlaneCoord2D(axis, sliceRealPoint0[axis], vertAxis, yReal2);
        } else if( (corners[0] == -1 && corners[1] == -1 && corners[2] == 1 && corners[3] == -1) || (corners[0] == 1 && corners[1] == 1 && corners[2] == -1 && corners[3] == 1)) {
            //case - corners[2] separated
            xReal1 = panelToRealImageCoordsHorizontal(w-1);
            yReal1 = getPlaneCoord2D(axis, sliceRealPoint0[axis], horizAxis, xReal1);
            yReal2 = panelToRealImageCoordsVertical(h-1);
            xReal2 = getPlaneCoord2D(axis, sliceRealPoint0[axis], vertAxis, yReal2);
        } else if( (corners[0] == -1 && corners[1] == -1 && corners[2] == -1 && corners[3] == 1) || (corners[0] == 1 && corners[1] == 1 && corners[2] == 1 && corners[3] == -1)) {
            //case - corners[3] separated
            xReal1 = panelToRealImageCoordsHorizontal(0);
            yReal1 = getPlaneCoord2D(axis, sliceRealPoint0[axis], horizAxis, xReal1);
            yReal2 = panelToRealImageCoordsVertical(h-1);
            xReal2 = getPlaneCoord2D(axis, sliceRealPoint0[axis], vertAxis, yReal2);
        } else if( (corners[0] == 1 && corners[1] == -1 && corners[2] == -1 && corners[3] == 1) || (corners[0] == -1 && corners[1] == 1 && corners[2] == 1 && corners[3] == -1)) {
            //case - left/right separation
            yReal1 = panelToRealImageCoordsVertical(0);
            xReal1 = getPlaneCoord2D(axis, sliceRealPoint0[axis], vertAxis, yReal1);
            yReal2 = panelToRealImageCoordsVertical(h-1);
            xReal2 = getPlaneCoord2D(axis, sliceRealPoint0[axis], vertAxis, yReal2);
        } else { //if( (corners[0] == 1 && corners[1] == 1 && corners[2] == -1 && corners[3] == -1) || (corners[0] == -1 && corners[1] == -1 && corners[2] == 1 && corners[3] == 1)) {
            //case - up/down separation
            xReal1 = panelToRealImageCoordsHorizontal(0);
            yReal1 = getPlaneCoord2D(axis, sliceRealPoint0[axis], horizAxis, xReal1);
            xReal2 = panelToRealImageCoordsHorizontal(w-1);
            yReal2 = getPlaneCoord2D(axis, sliceRealPoint0[axis], horizAxis, xReal2);

        }
        customSlicePath.moveTo(realImageCoordsToPanelHorizontal(xReal1), realImageCoordsToPanelVertical(yReal1));
        customSlicePath.lineTo(realImageCoordsToPanelHorizontal(xReal2), realImageCoordsToPanelVertical(yReal2));
    }

    private float getPlaneCoord2D(int axis0, float point0, int axis1, float point1) {
        float result = 0.0f;

        int myAxis;
        if(axis0 == 0 && axis1 == 1 || axis0 == 1 && axis1 == 0)
            myAxis = 2;
        else if(axis0 == 0 && axis1 == 2 || axis0 == 2 && axis1 == 0)
            myAxis = 1;
        else
            myAxis = 0;


        result -= (customPlaneVector[axis0]*(point0-customPlanePoint[axis0]) + customPlaneVector[axis1]*(point1-customPlanePoint[axis1]));
        result /= customPlaneVector[myAxis];
        result += customPlanePoint[myAxis];
        return result;
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
    public void setDataImage(BufferedImage dataImage, int sliceNumber, float[] sliceRealPoint0, float uppW, float uppH, float upp) {
        if(trans) {
            dataImage = ImageUtilities.switchAxes(dataImage);
        }
        this.dataImage = dataImage;
        this.uppW = uppW;
        this.uppH = uppH;
        this.upp = upp;
        this.sliceNumber = sliceNumber;
        if(geomTool != null) {
            geomTool.setMetadata(new Metadata(new Integer(sliceNumber)));
        }

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
         this.isoline = isoline;
         repaint();
    }

    public void setIsolines(ArrayList<float[][]>[] isolines) {
         this.isolines = isolines;
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

    /**
     * @param horizontalSlicePosition the horizontalSlicePosition to set
     */
    public void setHorizontalSlicePosition(int horizontalSlicePosition) {
        if(dataImage == null) {
            this.horizontalSlicePosition = horizontalSlicePosition;
            return;
        }

        if(horizontalSlicePosition < 0)
            horizontalSlicePosition = 0;
        
        if(horizontalSlicePosition >= dataImage.getHeight())
            horizontalSlicePosition = dataImage.getHeight()-1;

        this.horizontalSlicePosition = horizontalSlicePosition;
        updateSlicePositions();
        repaint();
    }

    /**
     * @param verticalSlicePosition the verticalSlicePosition to set
     */
    public void setVerticalSlicePosition(int verticalSlicePosition) {
        if(dataImage == null) {
            this.verticalSlicePosition = verticalSlicePosition;
            return;
        }

        if(verticalSlicePosition < 0)
            verticalSlicePosition = 0;

        if(verticalSlicePosition >= dataImage.getWidth())
            verticalSlicePosition = dataImage.getWidth()-1;

        this.verticalSlicePosition = verticalSlicePosition;
        updateSlicePositions();
        repaint();
    }

    private int panelToImageCoordsHorizontal(int p) {
        return (int)Math.floor((float)p/dw);
    }

    private int panelToImageCoordsVertical(int p) {
        return (int)Math.floor((float)p/dh);
    }

    private float panelToRealImageCoordsHorizontal(int p) {
        //return (float)p*upp/(zoom*imageScale) + sliceRealPoint0[horizAxis];
        return (float)p*upp/(zoom*imageScale) + sliceRealPoint0[horizAxis] - 0.5f*upp;
    }

    private float panelToRealImageCoordsVertical(int p) {
        return (float)p*upp/(zoom*imageScale) + sliceRealPoint0[vertAxis] - 0.5f*upp;
    }

    private int realImageCoordsToPanelHorizontal(float p) {
        return Math.round((p-sliceRealPoint0[horizAxis] + 0.5f*upp)*(imageScale*zoom)/upp);
    }

    private int realImageCoordsToPanelVertical(float p) {
        return Math.round((p-sliceRealPoint0[vertAxis] + 0.5f*upp)*(imageScale*zoom)/upp);
    }

    private int imageCoordsToPanelHorizontal(int p, boolean center) {
        if(center)
            return Math.round(p*dw + dw/2);
        else
            return Math.round(p*dw);
    }

    private int imageCoordsToPanelVertical(int p, boolean center) {
        if(center)
            return Math.round(p*dh + dh/2);
        else
            return Math.round(p*dh);
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


    private boolean holdingVerticalSlice = false;
    private boolean holdingHorizontalSlice = false;
    private boolean rotatingCustomSlice = false;
    private boolean translatingCustomSlice = false;
    private boolean holdingWindowRange = false;
    private boolean holdingNewPoint = false;

    private float[] rotationPoint = new float[3];
    private float[] lastPoint = new float[3];
    private float[] currentPoint = new float[3];
    
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

        switch(mode) {
            case MODE_ORTHOSLICES:
                if(e.getButton() == MouseEvent.BUTTON1) {
                    if(e.getClickCount() > 1) {
                        if(imgX != verticalSlicePosition ||  imgY != horizontalSlicePosition) {
                            setVerticalSlicePosition(imgX);
                            setHorizontalSlicePosition(imgY);
                            fireOrthosliceNumberChanged(horizAxis,imgX);
                            fireOrthosliceNumberChanged(vertAxis,imgY);                            
                        }
                    }
                } else if(e.getButton() == MouseEvent.BUTTON2) {


                } else if(e.getButton() == MouseEvent.BUTTON3) {
                    if(geomTool != null) {
                        geomTool.mouseClicked(e);
                    }
                }
                break;

            case MODE_CUSTOMSLICE:
                float imgRealX, imgRealY;
                imgRealX = panelToRealImageCoordsHorizontal(x);
                imgRealY = panelToRealImageCoordsVertical(y);
                if(e.getButton() == MouseEvent.BUTTON1) {
                    if(e.getClickCount() > 1) {
                        float[] p = new float[3];
                        p[horizAxis] = imgRealX;
                        p[vertAxis] = imgRealY;
                        p[axis] = sliceRealPoint0[axis];

                        setCustomPlanePoint(p);
                        fireCustomPlaneChanged(p, this.customPlaneVector);
                    }
                } else if(e.getButton() == MouseEvent.BUTTON2) {


                } else if(e.getButton() == MouseEvent.BUTTON3) {
                    if(geomTool != null) {
                        geomTool.mouseClicked(e);
                    }
                }
                break;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int x, y;
        x = e.getX()-imagePosX;
        y = e.getY()-imagePosY;
        if(x<0 || y<0 || x >= w || y >= h)
            return;

        switch(mode) {
            case MODE_ORTHOSLICES:
                if(e.getButton() == MouseEvent.BUTTON1) {                    
                    if ((e.getModifiersEx() & mouseOnmaskShift) == mouseOnmaskShift) {
                        fireZoomChanged();
                        this.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                        translateStartPointX = x+imagePosX;
                        translateStartPointY = y+imagePosY;
                        translating = true;
                    } else if((e.getModifiersEx() & mouseOnmaskCtrl) == mouseOnmaskCtrl) {
                        int jx, jy;
                        for (int i = 0; i < points.size(); i++) {
                           if(points.get(i).isDependant()) continue;
                           int[] point = points.get(i).getIndices();
                           jx = imageCoordsToPanelHorizontal(point[horizAxis], true);
                           jy = imageCoordsToPanelVertical(point[vertAxis], true);
                           if(point[axis] == sliceNumber && Math.abs(jx-x) < 3 && Math.abs(jy-y) < 3 ) {
                               holdingPoint = true;
                               holdingPointIndex = i;
                               fireGeometryPointSelected(i, false);
                               break;
                           }
                        }
                    } else {
                        if(Math.abs(x-scaledVerticalSlicePosition) <= 2) {
                            holdingVerticalSlice = true;
                        }

                        if(Math.abs(y-scaledHorizontalSlicePosition) <= 2) {
                            holdingHorizontalSlice = true;
                        }

                    }
                } else if(e.getButton() == MouseEvent.BUTTON2) {
                    holdingWindowRange = true;
                    lastPoint[0] = x;
                    lastPoint[1] = y;
                    this.setCursor(Cursor.getDefaultCursor());
                } else if(e.getButton() == MouseEvent.BUTTON3) {
                    if(geomTool != null) {
                        geomTool.mousePressed(e);
                    }
                }
                break;

            case MODE_CUSTOMSLICE:
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
                           int[] point = points.get(i).getIndices();
                           jx = imageCoordsToPanelHorizontal(point[horizAxis], true);
                           jy = imageCoordsToPanelVertical(point[vertAxis], true);
                           if(point[axis] == sliceNumber && Math.abs(jx-x) < 3 && Math.abs(jy-y) < 3 ) {
                               holdingPoint = true;
                               holdingPointIndex = i;
                               fireGeometryPointSelected(i, false);
                               break;
                           }
                        }
                    } else {
                        if(  Math.sqrt( Math.pow(x-realImageCoordsToPanelHorizontal(customPlanePoint[horizAxis]), 2) + Math.pow(y-realImageCoordsToPanelVertical(customPlanePoint[vertAxis]), 2) ) <= customsliceCircleRadius) {
                            translatingCustomSlice = true;
                        } else if( customSlicePath != null && Path2D.intersects(customSlicePath.getPathIterator(null), x-2, y-2, 4, 4) ) {
                            rotatingCustomSlice = true;
                            lastPoint[horizAxis] = panelToRealImageCoordsHorizontal(x);
                            lastPoint[vertAxis] = panelToRealImageCoordsVertical(y);
                            lastPoint[axis] = customPlanePoint[axis];

                            rotationPoint[horizAxis] = customPlanePoint[horizAxis];
                            rotationPoint[vertAxis] = customPlanePoint[vertAxis];
                            rotationPoint[axis] = sliceRealPoint0[axis];
                        }
                    }
                } else if(e.getButton() == MouseEvent.BUTTON2) {
                    holdingWindowRange = true;
                    lastPoint[0] = x;
                    lastPoint[1] = y;
                } else if(e.getButton() == MouseEvent.BUTTON3) {
                    if(geomTool != null) {
                        geomTool.mousePressed(e);
                    }
                }
                break;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(dataImage == null)
            return;

        switch(mode) {
            case MODE_ORTHOSLICES:
                if(e.getButton() == MouseEvent.BUTTON1) {
                    holdingHorizontalSlice = false;
                    holdingVerticalSlice = false;
                    translating = false;
                    if(holdingPoint) {
                        holdingPoint = false;
                        fireGeometryPointSelected(holdingPointIndex, false);
                        holdingPointIndex = -1;
                        //fireGeometryPointSelected(-1, false);
                    }
                } else if(e.getButton() == MouseEvent.BUTTON2) {
                    holdingWindowRange = false;
                } else if(e.getButton() == MouseEvent.BUTTON3) {
                    if(geomTool != null) {
                        geomTool.mouseReleased(e);
                        return;
                    }
                }
                break;

            case MODE_CUSTOMSLICE:
                if(e.getButton() == MouseEvent.BUTTON1) {
                    rotatingCustomSlice = false;
                    translatingCustomSlice = false;
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
                break;
        }
        mouseMoved(e);
        repaint();
    }

    private boolean mouseOver = false;

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
        fireMouseLocationChanged(null);
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

        switch(mode) {
            case MODE_ORTHOSLICES:
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
                    clickedPoint[horizAxis] = imgX;
                    clickedPoint[vertAxis] = imgY;
                    clickedPoint[axis] = sliceNumber;
                    fireSelectedPointMoved(holdingPointIndex, clickedPoint);
                } else {
                    if(holdingHorizontalSlice && !holdingVerticalSlice) {
                        if(x < 0 || x >= w || y < 0 || y >= h)
                            return;
                        if( imgY != horizontalSlicePosition) {
                            setHorizontalSlicePosition(imgY);
                            fireOrthosliceNumberChanged(vertAxis,imgY);
                        }
                    } else if(holdingVerticalSlice && !holdingHorizontalSlice) {
                        if(x < 0 || x >= w || y < 0 || y >= h)
                            return;
                        if( imgX != verticalSlicePosition) {
                            setVerticalSlicePosition(imgX);
                            fireOrthosliceNumberChanged(horizAxis,imgX);
                        }
                    } else if(holdingHorizontalSlice && holdingVerticalSlice) {
                        if(x < 0 || x >= w || y < 0 || y >= h)
                            return;
                        if( imgX != verticalSlicePosition) {
                            setVerticalSlicePosition(imgX);
                            fireOrthosliceNumberChanged(horizAxis,imgX);
                        }
                        if( imgY != horizontalSlicePosition) {
                            setHorizontalSlicePosition(imgY);
                            fireOrthosliceNumberChanged(vertAxis,imgY);
                        }
                    } else if(holdingWindowRange) {
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
                break;

            case MODE_CUSTOMSLICE:
                if (translating && ((e.getModifiersEx() & mouseOnmaskShift) == mouseOnmaskShift)) {
                    if(x < 0 || x >= w || y < 0 || y >= h)
                        return;
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
                    clickedPoint[horizAxis] = imgX;
                    clickedPoint[vertAxis] = imgY;
                    clickedPoint[axis] = sliceNumber;
                    fireSelectedPointMoved(holdingPointIndex, clickedPoint);
                } else if(rotatingCustomSlice) {
                    if(x < 0 || x >= w || y < 0 || y >= h)
                        return;
                    currentPoint[horizAxis] = panelToRealImageCoordsHorizontal(x);
                    currentPoint[vertAxis] = panelToRealImageCoordsVertical(y);
                    currentPoint[axis] = customPlanePoint[axis];
                    float rot = calculateAngle(rotationPoint, lastPoint, currentPoint, horizAxis, vertAxis);
                    System.arraycopy(currentPoint, 0, lastPoint, 0, 3);
                    setCustomPlaneVector(rotateVector(customPlaneVector, rot, horizAxis, vertAxis));
                    fireCustomPlaneChanged(this.customPlanePoint, this.customPlaneVector);
                } else if(translatingCustomSlice) {
                    if(x < 0 || x >= w || y < 0 || y >= h)
                        return;
                    currentPoint[horizAxis] = panelToRealImageCoordsHorizontal(x);
                    currentPoint[vertAxis] = panelToRealImageCoordsVertical(y);
                    currentPoint[axis] = customPlanePoint[axis];//sliceRealPoint0[axis];//customPlanePoint[axis];
                    setCustomPlanePoint(currentPoint);
                    fireCustomPlaneChanged(this.customPlanePoint, this.customPlaneVector);
                } else if(holdingWindowRange) {
                    float dx = lastPoint[0]-(float)x;
                    float dy = (float)y-lastPoint[1];
                    lastPoint[0] = x;
                    lastPoint[1] = y;
                    fireMappingRangeChanged(dx, dy);
                } else {
                    if(geomTool != null) {
                        geomTool.mouseDragged(e);
                    }
                }
                break;
        }
    }

    private float[] rotateVector(float[] vector, float angle, int axis0, int axis1) {
        float[] out = new float[3];
        System.arraycopy(vector, 0, out, 0, 3);

        double cfi = Math.cos(angle);
        double sfi = Math.sin(angle);
        out[axis0] = (float)(vector[axis0]*cfi - vector[axis1]*sfi);
        out[axis1] = (float)(vector[axis0]*sfi + vector[axis1]*cfi);
        return out;
    }

    private float calculateAngle(float[] p0, float[] p1, float[] p2, int axis0, int axis1) {
        if(p0 == null || p1 == null || p2 == null)
            return 0.0f;

        if(p0.length != 3 || p1.length != 3 ||p2.length != 3)
            return 0.0f;

        return -(float)(Math.atan2(p1[axis1]-p0[axis1], p1[axis0]-p0[axis0]) - Math.atan2(p2[axis1]-p0[axis1], p2[axis0]-p0[axis0]));
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if(dataImage == null)
            return;

        int x, y;
        x = e.getX()-imagePosX;
        y = e.getY()-imagePosY;
        if(x< 0 || y <0 || x >= w || y >= h) {
            this.setCursor(Cursor.getDefaultCursor());
            fireMouseLocationChanged(null);
            return;
        }

        if(mouseOver) {
            currentMousePosition[0] = panelToRealImageCoordsHorizontal(x);
            currentMousePosition[1] = panelToRealImageCoordsVertical(y);            
            fireMouseLocationChanged(currentMousePosition);
        } else {
            fireMouseLocationChanged(null);
        }

        if(mouseOver && !((e.getModifiersEx() & mouseOnmaskCtrl) == mouseOnmaskCtrl) && !((e.getModifiersEx() & mouseOnmaskShift) == mouseOnmaskShift)) {
            if(mode == MODE_ORTHOSLICES) {
                if((Math.abs(x-scaledVerticalSlicePosition) <= 2) &&  (Math.abs(y-scaledHorizontalSlicePosition) <= 2) ) {
                    this.setCursor(cp.getCustomMoveCursor());
                } else if((Math.abs(x-scaledVerticalSlicePosition) <= 2) &&  (Math.abs(y-scaledHorizontalSlicePosition) > 2) ) {
                    this.setCursor(cp.getCustomMoveHorizCursor());
                } else if((Math.abs(x-scaledVerticalSlicePosition) > 2) &&  (Math.abs(y-scaledHorizontalSlicePosition) <= 2) ) {
                    this.setCursor(cp.getCustomMoveVertCursor());
                    //this.setCursor(cp.getCustomMoveVertCursorRotated(30.0f));
                } else {
                    if(geomTool != null) {
                        this.setCursor(geomTool.getCursor());
                        geomTool.mouseMoved(e);
                    } else {
                        this.setCursor(Cursor.getDefaultCursor());
                    }
                }
            } else if(mode == MODE_CUSTOMSLICE) {
                if(  Math.sqrt( Math.pow(x-realImageCoordsToPanelHorizontal(customPlanePoint[horizAxis]), 2) + Math.pow(y-realImageCoordsToPanelVertical(customPlanePoint[vertAxis]), 2) ) <= customsliceCircleRadius) {
                    this.setCursor(cp.getCustomMoveCursor());
                } else if ( customSlicePath!= null && Path2D.intersects(customSlicePath.getPathIterator(null), x-2, y-2, 4, 4)  ) {
                    this.setCursor(cp.getCustomRotateCursor());
                } else {
                    if(geomTool != null) {
                        this.setCursor(geomTool.getCursor());
                        geomTool.mouseMoved(e);
                    } else {
                        this.setCursor(Cursor.getDefaultCursor());
                    }
                }
            }
        } else {
            if(geomTool != null) {
                this.setCursor(geomTool.getCursor());
                geomTool.mouseMoved(e);
            } else {
                this.setCursor(Cursor.getDefaultCursor());
            }
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if(geomTool != null && geomTool.isMouseWheelBlocking())
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
            fireOrthosliceNumberChanged(axis, sliceNumber-e.getWheelRotation());
        }
        mouseMoved(e);
    }

    /**
     * @return the mouseMode
     */
    public int getModeMode() {
        return mode;
    }

    /**
     * @param mouseMode the mouseMode to set
     */
    public void setMode(int mode) {
        this.mode = mode;
        repaint();
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


    private void fireOrthosliceNumberChanged(int axis, int value) {
        for (ViewPanelListener listener : viewPanelListeners) {
            listener.onViewPanelEvent(new OrthosliceNumberChangedViewPanelEvent(this, axis, value));
        }
    }

    private void fireCustomPlaneChanged(float[] p, float[] v) {
        for (ViewPanelListener listener : viewPanelListeners) {
            listener.onViewPanelEvent(new CustomPlaneChangedViewPanelEvent(this, p, v));
        }
    }

    private void fireSelectedPointMoved(int pIndex, int[] p) {
        for (ViewPanelListener listener : viewPanelListeners) {
            listener.onViewPanelEvent(new GeometryPointMovedOrthoPanelEvent(this, pIndex, p));
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

    private void fireGeometryPointSelected(int pIndex, boolean followSlices) {
        for (ViewPanelListener listener : viewPanelListeners) {
            listener.onViewPanelEvent(new GeometryPointSelectedOrthoPanelEvent(this, pIndex, followSlices));
        }
    }

    private void fireMouseLocationChanged(float[] p) {
        for (ViewPanelListener listener : viewPanelListeners) {
            listener.onViewPanelEvent(new MouseLocationChangedOrthoPanelEvent(this,p));
        }
    }

    /**
     * @param customPlanePoint the customPlanePoint to set
     */
    public void setCustomPlanePoint(float[] customPlanePoint) {
        this.customPlanePoint = customPlanePoint;
        updateCustomSlicePath();
        repaint();
    }

    /**
     * @param customPlaneVector the customPlaneVector to set
     */
    public void setCustomPlaneVector(float[] customPlaneVector) {
        this.customPlaneVector = customPlaneVector;
        updateCustomSlicePath();
        repaint();
    }

    public void setCustomPlaneParams(float[] customPlanePoint, float[] customPlaneVector) {
        this.customPlaneVector = customPlaneVector;
        this.customPlanePoint = customPlanePoint;
        updateCustomSlicePath();
        repaint();
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
    public void setGeometryTool(GeometryTool gt) {
        if(this.geomTool != null) {
            this.geomTool.removeChangeListener(this);
            this.geomTool = null;
        }

        this.geomTool = gt;

        if(this.geomTool != null) {
            this.geomTool.addChangeListener(this);
            this.geomTool.setMetadata(new Metadata(new Integer(sliceNumber)));
        }

        repaint();
    }


    @Override
    public void onGeometryToolStateChanged(ChangeEvent e) {
        Object src = e.getSource();
        if(src instanceof GeometryTool) {
            GeometryTool gt = (GeometryTool)src;
            int[][] gtPoints = gt.getPoints();
            Metadata[] gtMetadata = gt.getPointMetadata();
            int[][] gtConnections = gt.getConnections();

            if(gtPoints == null)
                return;

            int nPts = gtPoints.length;
            int[][] outPoints = new int[nPts][3];

            for (int i = 0; i < nPts; i++) {
                outPoints[i][horizAxis] = panelToImageCoordsHorizontal(gtPoints[i][0]-imagePosX);
                outPoints[i][vertAxis] = panelToImageCoordsVertical(gtPoints[i][1]-imagePosY);
                if(gtMetadata != null && gtMetadata.length == nPts && gtMetadata[i].getObject() instanceof Integer)
                    outPoints[i][axis] = (Integer)gtMetadata[i].getObject();
                else
                    outPoints[i][axis] = sliceNumber;
            }

            firePointsConnectionsCalculablesAdded(outPoints, gtConnections, gt.getCalculable());
        }
    }

    /**
     * @return the axis
     */
    public int getAxis() {
        return axis;
    }

    /**
     * @return the trans
     */
    public boolean isTrans() {
        return trans;
    }

    /**
     * @return the horizAxis
     */
    public int getHorizAxis() {
        return horizAxis;
    }

    /**
     * @return the vertAxis
     */
    public int getVertAxis() {
        return vertAxis;
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

}
