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
package pl.edu.icm.visnow.lib.basic.viewers.Viewer2D;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;
import javax.imageio.ImageIO;
import javax.swing.ToolTipManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.commons.imaging.formats.tiff.constants.TiffConstants;
import org.apache.commons.io.FilenameUtils;
import pl.edu.icm.visnow.lib.basic.viewers.Viewer2D.Display2DControlsFrame.ImageFormat;
import pl.edu.icm.visnow.lib.utils.ImageUtilities;
import pl.edu.icm.visnow.lib.utils.YUVSaver;
import pl.edu.icm.visnow.lib.utils.geometry2D.GeometryObject2D;
import pl.edu.icm.visnow.lib.utils.geometry2D.TransformedGeometryObject2D;

/**
 * @author Bartosz Borucki (babor@icm.edu.pl) Warsaw University,
 * Interdisciplinary Centre for Mathematical and Computational Modelling
 */
public class Display2DPanel extends javax.swing.JPanel {

    protected Color bgColor = Color.BLACK;
    private Display2DFrame parentFrame = null;
    private TransformedGeometryObject2D root0 = new TransformedGeometryObject2D(new GeometryObject2D("root0"));
    protected TransformedGeometryObject2D root = new TransformedGeometryObject2D(new GeometryObject2D("display") {
        @Override
        public void drawLocal2D(Graphics2D g, AffineTransform tr) {
        }
    });
    protected Font titleFont = new Font("Helvetica", 0, 18);
    ;
    protected Color titleColor = Color.WHITE;
    protected String titleText = "";
    protected Display2DControlsFrame controlsFrame;
    protected boolean autoCenter = true;
    protected boolean autoNormalize = true;
    protected boolean firstRun = true;
    protected boolean move = false;
    protected int oldX, oldY;
    protected float mouseWheelSensitivity = 1.05f;
    protected boolean noObjects = true;
    private boolean storingFrames = false;
    private boolean storingJPEG = true;
    public static final int FORMAT_JPEG = 0;
    public static final int FORMAT_PNG = 1;
    public static final int FORMAT_YUV = 2;
    protected YUVSaver yuvSaver = null;
    private String currentObjectInfo = "";
    private String currentLocalPositionInfo = "";
    private String currentLocalValueInfo = "";

    /**
     * Creates new form Display2DPanel
     */
    public Display2DPanel() {
        initComponents();
        this.setMinimumSize(new Dimension(300, 200));
        this.setPreferredSize(new Dimension(600, 800));
        controlsFrame = new Display2DControlsFrame(this);
        root0.getGeometryObject2D().setWidth(this.getWidth());
        root0.getGeometryObject2D().setHeight(this.getHeight());
        root.getGeometryObject2D().setWidth(0);
        root.getGeometryObject2D().setHeight(0);
        root0.addChild(root);
        controlsFrame.setTreeRoot(root);
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setBorder(javax.swing.BorderFactory.createEtchedBorder());
        setToolTipText(null);
        setMinimumSize(new java.awt.Dimension(500, 350));
        setPreferredSize(new java.awt.Dimension(1000, 600));
        addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                formMouseWheelMoved(evt);
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                formMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                formMouseReleased(evt);
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                formMouseDragged(evt);
            }
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                formMouseMoved(evt);
            }
        });
        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        root0.getGeometryObject2D().setWidth(this.getWidth());
        root0.getGeometryObject2D().setHeight(this.getHeight());
        if (firstRun) {
            root.centerToParent();
            firstRun = false;
        }
        update();
        controlsFrame.update();
    }//GEN-LAST:event_formComponentResized
    private boolean tootltipActive = false;

   private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
       if (evt.getButton() == MouseEvent.BUTTON1) {
           tootltipActive = !tootltipActive;
           formMouseMoved(evt);
       } else if (evt.getButton() == MouseEvent.BUTTON2) {
           this.reset();
       } else if (evt.getButton() == MouseEvent.BUTTON3) {
           showControlsFrame();
       }

   }//GEN-LAST:event_formMouseClicked

   private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged
       if (move && !noObjects) {
           autoCenter = false;
           autoNormalize = false;
           double sx = root.getExtIntTransform().getScaleX();
           double sy = root.getExtIntTransform().getScaleY();
           root.getExternalTransform().translate((double) (evt.getX() - oldX) / sx, (double) (evt.getY() - oldY) / sy);
           oldX = evt.getX();
           oldY = evt.getY();
           update();
           controlsFrame.update();
       }
   }//GEN-LAST:event_formMouseDragged

   private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
       if (evt.getButton() == MouseEvent.BUTTON1) {
           oldX = evt.getX();
           oldY = evt.getY();
           if (!noObjects) {
               move = true;
               this.setCursor(new Cursor(Cursor.MOVE_CURSOR));
           }
       }
   }//GEN-LAST:event_formMousePressed

   private void formMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseReleased
       if (evt.getButton() == MouseEvent.BUTTON1) {
           move = false;
           this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
       }
   }//GEN-LAST:event_formMouseReleased

   private void formMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_formMouseWheelMoved
       if (!autoNormalize && !noObjects) {
           float scale = 1.0f;
           if (evt.getWheelRotation() > 0) {
               scale = 1.0f / mouseWheelSensitivity;
           } else {
               scale = 1.0f * mouseWheelSensitivity;
           }

           //autoCenter = false;
           root.getExternalTransform().scale(scale, scale);
           update();
           controlsFrame.update();
       }
   }//GEN-LAST:event_formMouseWheelMoved
    private TransformedGeometryObject2D currentUnderCursorObj = null;

   private void formMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseMoved
       int x = evt.getX();
       int y = evt.getY();
       currentUnderCursorObj = root.getChiltAt(x, y);
       currentObjectInfo = "";
       currentLocalValueInfo = "";
       currentLocalPositionInfo = "";

       if (currentUnderCursorObj == null || currentUnderCursorObj == root) {
           fireStateChanged();

           if (parentFrame != null) {
               parentFrame.setStatus("");

           }
           tootltipActive = false;
           this.setToolTipText(null);
           return;
       }

       //System.out.println("clicekd - "+((obj==null)?"<empty>":obj.getName()));
       currentObjectInfo = "" + currentUnderCursorObj.getName();

       int[] p = currentUnderCursorObj.getLocalCoords(x, y);
       //System.out.println("["+p[0]+","+p[1]+"]");
       currentLocalPositionInfo = "[" + p[0] + "," + p[1] + "]";

       //System.out.println("local info: " + obj.getGeometryObject2D().getLocalInfoAt(p[0], p[1]));
       currentLocalValueInfo = currentUnderCursorObj.getGeometryObject2D().getLocalInfoAt(p[0], p[1]);

       fireStateChanged();

       if (parentFrame != null) {
           parentFrame.setStatus("" + currentObjectInfo + " | " + currentLocalPositionInfo + " | " + currentLocalValueInfo);

       }


       if (tootltipActive) {
           this.setToolTipText(currentUnderCursorObj.getGeometryObject2D().getDetailedLocalInfoAt(p[0], p[1]));
           ToolTipManager.sharedInstance().setInitialDelay(0);
           ToolTipManager.sharedInstance().setDismissDelay(100000);
           ToolTipManager.sharedInstance().mouseMoved(
                   new MouseEvent(this, 0, 0, 0,
                   x, y, // X-Y of the mouse for the tool tip
                   0, false));
       } else {
           this.setToolTipText(null);
       }


   }//GEN-LAST:event_formMouseMoved

   private void formMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseExited
       currentObjectInfo = "";
       currentLocalValueInfo = "";
       currentLocalPositionInfo = "";
       currentUnderCursorObj = null;

       fireStateChanged();

       if (parentFrame != null) {
           parentFrame.setStatus("");

       }
       tootltipActive = false;
       this.setToolTipText(null);



   }//GEN-LAST:event_formMouseExited

    public void setBackgroundColor(Color c) {
        bgColor = c;
        repaint();
    }

    public Color getBackgroundColor() {
        return bgColor;
    }

    public String getTitle() {
        return titleText;
    }

    public void setTitle(String title) {
        this.titleText = title;
        repaint();
    }

    public Color getTitleColor() {
        return titleColor;
    }

    public void setTitleColor(Color color) {
        this.titleColor = color;
        repaint();
    }

    public Font getTitleFont() {
        return titleFont;
    }

    public void setTitleFont(Font font) {
        this.titleFont = font;
        repaint();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    public synchronized void draw2D(Graphics2D g) {
        root.draw2D(g, this);
    }
    private boolean dontWrite = false;

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Font f = g2d.getFont();
        Color c = g2d.getColor();
        Rectangle clear = new Rectangle(0, 0, getWidth(), getHeight());
        g2d.setPaint(bgColor);
        g2d.fill(clear);

        draw2D(g2d);

        g2d.setFont(titleFont);
        g2d.setColor(titleColor);
        g2d.drawString(titleText, 50, 10 + titleFont.getSize());
        g2d.setFont(f);
        g2d.setColor(c);
        if (storingFrames && !dontWrite) {
            if (storingJPEG) {
                writeImage(controlsFrame.getMovieCreationPanel().getCurrentFrameFileName(), FORMAT_JPEG);
            } else {
                writeImage(controlsFrame.getMovieCreationPanel().getGenericFrameFileName(), FORMAT_PNG);
            }
        }
    }

    public void addChild(TransformedGeometryObject2D child, int layer) {
        child.setPanel(this);
        root.addChild(child, layer);
        root.centerToParent();
        update();
    }

    public void addChild(TransformedGeometryObject2D child) {
        this.addChild(child, root.getChildren().size());
    }

    public boolean removeChild(TransformedGeometryObject2D child) {
        boolean success = root.removeChild(child);
        child.setPanel(null);
        update();
        return success;
    }

    public boolean removeChildByParentModulePort(String pmp) {
        if (pmp == null) {
            return false;
        }

        int i;
        for (i = 0; i < root.getChildren().size(); i++) {
            if (root.getChildren().get(i).getParentModulePort().equals(pmp)) {
                break;
            }
        }
        root.getChildren().get(i).setPanel(null);
        boolean success = root.removeChild(i);
        if (controlsFrame != null) {
            controlsFrame.resetTree();
        }
        update();
        return success;
    }

    public TransformedGeometryObject2D getChildByParentModulePort(String pmp) {
        if (pmp == null) {
            return null;
        }

        int n = -1;
        for (int i = 0; i < root.getChildren().size(); i++) {
            if (root.getChildren().get(i).getParentModulePort().equals(pmp)) {
                n = i;
                break;
            }
        }
        if (n < 0) {
            return null;
        } else {
            return root.getChildren().get(n);
        }
    }

    public void clearAllGeometry() {
        this.removeAllChildren();
    }

    public ArrayList<TransformedGeometryObject2D> getChildren() {
        return root.getChildren();
    }

    public void removeAllChildren() {
        for (int i = 0; i < root.getChildren().size(); i++) {
            root.getChildren().get(i).setPanel(null);
        }
        root.removeAllChildren();
        update();
    }

    public void moveChild(TransformedGeometryObject2D child, int layer) {
        if (layer < 0) {
            return;
        }
        removeChild(child);
        addChild(child, layer);
    }

    public void updateTree() {
        if (controlsFrame != null) {
            controlsFrame.updateTree();
        }
        //controlsFrame.setObjects(root);
    }

    public boolean isAutoCenter() {
        return autoCenter;
    }

    public void setAutoCenter(boolean autoCenter) {
        this.autoCenter = autoCenter;
        update();
    }

    public boolean isAutoNormalize() {
        return autoNormalize;
    }

    public void setAutoNormalize(boolean autoNormalize) {
        this.autoNormalize = autoNormalize;
        //update();
        setAutoCenter(autoNormalize);
    }

    public void normalize() {
        reset();
    }

    public void center() {
        root.centerToParent();
        root.centerToParent();
        //fix double centering
        repaint();
        updateTree();
    }

    public void reset() {
        root.centerToParent();
        root.normalizeToParent();
        root.centerToParent();
        root.normalizeToParent();
        //fix double normalisation

        repaint();
        updateTree();
    }

    public void update() {
        if (autoNormalize) {
            root.normalizeToParent();
        }

        if (autoCenter) {
            root.centerToParent();
        }

        noObjects = !(root.getChildren().size() > 0);

        repaint();
        updateTree();
    }

    public Display2DFrame getParentFrame() {
        return parentFrame;
    }

    public void setParentFrame(Display2DFrame parentFrame) {
        this.parentFrame = parentFrame;
    }

    public TransformedGeometryObject2D getRootObject() {
        return root0;
    }

    public void writeImage(File file) {
        BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        dontWrite = true;
        paintComponent(img.getGraphics());
        dontWrite = false;

        String ext = FilenameUtils.getExtension(file.getAbsolutePath()).toLowerCase();

        try {
            if (ext.equals("jpg") || ext.equals("jpeg")) {
                ImageUtilities.writeJpeg(img, 1.0f, file);
            } else if (ext.equals("png")) {
                ImageUtilities.writePng(img, file);
            } else if (ext.equals("gif")) {
                ImageUtilities.writeGif(img, file);
            } else if (ext.equals("tif") || ext.equals("tiff")) {
                ImageUtilities.writeTiff(img, TiffConstants.TIFF_COMPRESSION_UNCOMPRESSED, file);
            } else if (ext.equals("bmp")) {
                ImageUtilities.writeBmp(img, file);
            } else if (ext.equals("pcx")) {
                ImageUtilities.writePcx(img, file);
            } else {
                throw new IllegalArgumentException("Invalid file extension " + ext);
            }
        } catch (IOException e) {
            System.out.println("I/O exception for " + file.getAbsolutePath());
        }

    }

    public void writeImage(String fileName, int format) {
        //System.out.println("writing image: "+fileName);
        BufferedImage img = null;
        try {
            img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
            dontWrite = true;
            paintComponent(img.getGraphics());
            dontWrite = false;
            File file = new File(fileName);
            switch (format) {
                case FORMAT_PNG:
                    ImageUtilities.writePng(img, file);
                    break;
                case FORMAT_YUV:
                    int[] content = null;
                    content = img.getData().getPixels(0, 0, img.getWidth(), img.getHeight(), content);
                    if (yuvSaver == null || yuvSaver.getHeight() != img.getHeight() || yuvSaver.getWidth() != img.getWidth()) {
                        yuvSaver = new YUVSaver(img.getWidth(), img.getHeight(), fileName);
                    }
                    yuvSaver.saveEncoded(img, controlsFrame.getMovieCreationPanel().getCurrentFrameNumber());
                    break;
                default:
                    ImageIO.write(img, ImageFormat.JPEG_FORMAT.getExtension(), file);
                    break;
            }



        } catch (FileNotFoundException ex) {
//         pl.edu.icm.visnow.egg.error.Displayer.display(2008052915280L, ex, toString(), "File not found: " + fileName);
        } catch (IOException ex) {
//         pl.edu.icm.visnow.egg.error.Displayer.display(2008052915290L, ex, toString(), "IO Exception: " + fileName);
        }
    }

    /**
     * @return the storingFrames
     */
    public boolean isStoringFrames() {
        return storingFrames;
    }

    /**
     * @param storingFrames the storingFrames to set
     */
    public void setStoringFrames(boolean storingFrames) {
        this.storingFrames = storingFrames;
        if (storingFrames) {
            repaint();
        }
    }

    /**
     * @return the storingJPEG
     */
    public boolean isStoringJPEG() {
        return storingJPEG;
    }

    /**
     * @param storingJPEG the storingJPEG to set
     */
    public void setStoringJPEG(boolean storingJPEG) {
        this.storingJPEG = storingJPEG;
    }
    /**
     * Utility field holding list of ChangeListeners.
     */
    private transient ArrayList<ChangeListener> changeListenerList =
            new ArrayList<ChangeListener>();

    /**
     * Registers ChangeListener to receive events.
     *
     * @param listener The listener to register.
     */
    public synchronized void addChangeListener(ChangeListener listener) {
        changeListenerList.add(listener);
    }

    /**
     * Removes ChangeListener from the list of listeners.
     *
     * @param listener The listener to remove.
     */
    public synchronized void removeChangeListener(ChangeListener listener) {
        changeListenerList.remove(listener);
    }

    /**
     * Notifies all registered listeners about the event.
     *
     * @param object Parameter #1 of the <CODE>ChangeEvent<CODE> constructor.
     */
    private void fireStateChanged() {
        ChangeEvent e = new ChangeEvent(this);
        for (ChangeListener listener : changeListenerList) {
            listener.stateChanged(e);
        }
    }

    private void showControlsFrame() {
        if (!controlsFrame.isVisible()) {
            controlsFrame.setLocation(parentFrame.getLocation().x + 30, parentFrame.getLocation().y + 30);
            controlsFrame.setTitle(parentFrame.getTitle() + " Controls");
        }


        if ((controlsFrame.getExtendedState() | Frame.ICONIFIED) == Frame.ICONIFIED) {
            controlsFrame.setExtendedState(Frame.NORMAL);
        }

        controlsFrame.setVisible(true);
    }
}
