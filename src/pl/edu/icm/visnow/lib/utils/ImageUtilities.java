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

package pl.edu.icm.visnow.lib.utils;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import pl.edu.icm.visnow.datamaps.colormap1d.DefaultColorMap1D;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;

public class ImageUtilities {

    private static final Component sComponent = new Component() {
    };
    private static final MediaTracker sTracker = new MediaTracker(sComponent);
    private static int sID = 0;

    public static boolean waitForImage(Image image) {
        int id;
        synchronized (sComponent) {
            id = sID++;
        }
        sTracker.addImage(image, id);
        try {
            sTracker.waitForID(id);
        } catch (InterruptedException ie) {
            return false;
        }
        if (sTracker.isErrorID(id)) {
            return false;
        }
        return true;
    }

    public static Image blockingLoad(String path) {
        Image image = Toolkit.getDefaultToolkit().getImage(path);
        if (waitForImage(image) == false) {
            return null;
        }
        return image;
    }

    public static Image blockingLoad(URL url) {
        Image image = Toolkit.getDefaultToolkit().getImage(url);
        if (waitForImage(image) == false) {
            return null;
        }
        return image;
    }

    public static BufferedImage makeBufferedImage(Image image) {
        BufferedImage bufferedImage = makeBufferedImage(image, BufferedImage.TYPE_INT_ARGB);
        if(bufferedImage == null)
            return null;
        WritableRaster raster = bufferedImage.getRaster();
        for (int j = 0; j < bufferedImage.getHeight(); j++) {
            for (int i = 0; i < bufferedImage.getWidth(); i++) {
                if (raster.getSample(i, j, 3) != 255) {
                    return bufferedImage;
                }
            }
        }

        bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
        if (waitForImage(image) == false) {
            return null;
        }
        Graphics2D g2 = bufferedImage.createGraphics();
        g2.drawImage(image, null, null);
        raster = bufferedImage.getRaster();
        for (int j = 0; j < bufferedImage.getHeight(); j++) {
            for (int i = 0; i < bufferedImage.getWidth(); i++) {
                if (raster.getSample(i, j, 0) != raster.getSample(i, j, 1) || raster.getSample(i, j, 0) != raster.getSample(i, j, 2)) {
                    return bufferedImage;
                }
            }
        }

        bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_BYTE_GRAY);
        if (waitForImage(image) == false) {
            return null;
        }
        g2 = bufferedImage.createGraphics();
        g2.drawImage(image, null, null);
        return bufferedImage;
    }

    public static BufferedImage makeBufferedImage(Image image, int imageType) {
        if (image == null) {
            return null;
        }

        if (waitForImage(image) == false) {
            return null;
        }
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), imageType);
        Graphics2D g2 = bufferedImage.createGraphics();
        g2.drawImage(image, null, null);
        return bufferedImage;
    }

    public static Frame getNonClearingFrame(String name, Component c) {
        final Frame f = new Frame(name) {

            @Override
            public void update(Graphics g) {
                paint(g);
            }
        };
        sizeContainerToComponent(f, c);
        centerFrame(f);
        f.setLayout(new BorderLayout());
        f.add(c, BorderLayout.CENTER);
        f.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                f.dispose();
            }
        });
        return f;
    }

    public static void sizeContainerToComponent(Container container, Component component) {
        if (container.isDisplayable() == false) {
            container.addNotify();
        }
        Insets insets = container.getInsets();
        Dimension size = component.getPreferredSize();
        int width = insets.left + insets.right + size.width;
        int height = insets.top + insets.bottom + size.height;
        container.setSize(width, height);
    }

    public static void centerComponent(Component component) {
        component.setLocation((component.getParent().getWidth() - component.getWidth()) / 2,
                (component.getParent().getHeight() - component.getHeight()) / 2);
    }

    public static void centerComponentToContainer(Component component, Component container) {
        if (container.isDisplayable() == false) {
            container.addNotify();
        }
        component.setLocation((container.getWidth() - component.getWidth()) / 2, (container.getHeight() - component.getHeight()) / 2);
    }

    public static void centerFrame(Frame f) {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension d = f.getSize();
        int x = (screen.width - d.width) / 2;
        int y = (screen.height - d.height) / 2;
        f.setLocation(x, y);
    }

    public static BufferedImage rgb2gray(BufferedImage img) {
        if (img.getType() == BufferedImage.TYPE_INT_ARGB || img.getType() == BufferedImage.TYPE_INT_RGB) {
            int w, h;
            w = img.getWidth();
            h = img.getHeight();
            BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);

            //----------------------------v3.0----------------------
            ColorModel cm = img.getColorModel();
            int pixel, gr;
            int r, g, b;
            WritableRaster raster = out.getRaster();
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    pixel = img.getRGB(x, y);
                    r = cm.getRed(pixel);
                    g = cm.getGreen(pixel);
                    b = cm.getBlue(pixel);

                    gr = (int) Math.round(((double) r + (double) g + (double) b) / 3.0);
                    raster.setSample(x, y, 0, gr);
                }
            }
            return out;
        }
        return img;
    }

    public static BufferedImage[] split2RGBA(BufferedImage img) {
        int w, h, a, r, g, b, pixel;

        if (img.getType() == BufferedImage.TYPE_INT_ARGB) {
            w = img.getWidth();
            h = img.getHeight();
            BufferedImage[] out = new BufferedImage[4];
            WritableRaster[] rasters = new WritableRaster[4];
            for (int i = 0; i < 4; i++) {
                out[i] = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
                rasters[i] = out[i].getRaster();
            }

            ColorModel cm = img.getColorModel();
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    pixel = img.getRGB(x, y);

                    a = cm.getAlpha(pixel);
                    r = cm.getRed(pixel);
                    g = cm.getGreen(pixel);
                    b = cm.getBlue(pixel);


                    rasters[3].setSample(x, y, 0, a);
                    rasters[0].setSample(x, y, 0, r);
                    rasters[1].setSample(x, y, 0, g);
                    rasters[2].setSample(x, y, 0, b);
                }
            }
            return out;
        } else {
            return null;
        }
    }

    public static BufferedImage[] split2RGB(BufferedImage img) {

        //--------------------v2.0--------------------------------------
        int w, h, r, g, b, pixel;

        if (img.getType() == BufferedImage.TYPE_INT_RGB) {
            w = img.getWidth();
            h = img.getHeight();
            BufferedImage[] out = new BufferedImage[3];
            WritableRaster[] rasters = new WritableRaster[3];
            for (int i = 0; i < 3; i++) {
                out[i] = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
                rasters[i] = out[i].getRaster();
            }

            ColorModel cm = img.getColorModel();
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    pixel = img.getRGB(x, y);

                    r = cm.getRed(pixel);
                    g = cm.getGreen(pixel);
                    b = cm.getBlue(pixel);


                    rasters[0].setSample(x, y, 0, r);
                    rasters[1].setSample(x, y, 0, g);
                    rasters[2].setSample(x, y, 0, b);
                }
            }
            return out;
        } else {
            return null;
        }
    }

    public static BufferedImage compensateLensDistortion(BufferedImage img, double k) {
        if (img == null) {
            return null;
        }

        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage out = new BufferedImage(w, h, img.getType());

        int x0 = (int) Math.floor(w / 2) + 1;
        int y0 = (int) Math.floor(h / 2) + 1;

        double ru, theta, ww, rd;
        int xd, yd;

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                ru = Math.sqrt((x - x0) * (x - x0) + (y - y0) * (y - y0));
                theta = Math.atan2(y - y0, x - x0);
                ww = Math.pow(ru / (2 * k) + Math.sqrt((ru * ru) / (4 * k * k) + 1 / (27 * k * k * k)), 1.0 / 3.0);
                rd = ww - 1 / (3 * k * ww);

                //nearest neighbour---------------------------------------
                xd = (int) Math.round(rd * Math.cos(theta)) + x0;
                yd = (int) Math.round(rd * Math.sin(theta)) + y0;
                if (xd >= 0 && yd >= 0 && xd < w && yd < h) {
                    //piksel nowy x,y = piksel stary xd,yd
                    out.setRGB(x, y, img.getRGB(xd, yd));
                }
                //---------------------------------------------------------
            }
        }
        return out;
    }

    public static BufferedImage compensateLensDistortion2(BufferedImage img, double k) {
        if (img == null) {
            return null;
        }

        int w = img.getWidth();
        int h = img.getHeight();
        //BufferedImage out = new BufferedImage(w, h, img.getType());

        int x0 = (int) Math.floor(w / 2) + 1;
        int y0 = (int) Math.floor(h / 2) + 1;

        double ru, theta, ww, rd;
        int xd, yd;

        double rdmax = Math.sqrt((w - x0) * (w - x0) + (h - y0) * (h - y0));
        double rumax = rdmax * (1 + k * rdmax * rdmax);
        //System.out.println("rdmax="+rdmax);
        //System.out.println("rumax="+rumax);
        double thetamax = Math.atan2(h - y0, w - x0);


        int xmax = (int) Math.round(rumax * Math.cos(thetamax)) * 2;
        int ymax = (int) Math.round(rumax * Math.sin(thetamax)) * 2;
        //System.out.println("xmax="+xmax);
        //System.out.println("ymax="+ymax);

        BufferedImage out = new BufferedImage(xmax, ymax, img.getType());

        int newx0 = (int) Math.floor(xmax / 2) + 1;
        int newy0 = (int) Math.floor(ymax / 2) + 1;

        int dx = (int) ((xmax - w) / 2) - 1;
        int dy = (int) ((ymax - h) / 2) - 1;

        for (int x = 0; x < xmax; x++) {
            for (int y = 0; y < ymax; y++) {
                ru = Math.sqrt((x - newx0) * (x - newx0) + (y - newy0) * (y - newy0));
                theta = Math.atan2(y - newy0, x - newx0);
                ww = Math.pow(ru / (2 * k) + Math.sqrt((ru * ru) / (4 * k * k) + 1 / (27 * k * k * k)), 1.0 / 3.0);
                rd = ww - 1 / (3 * k * ww);

                //nearest neighbour---------------------------------------
                xd = (int) Math.round(rd * Math.cos(theta)) + x0;
                yd = (int) Math.round(rd * Math.sin(theta)) + y0;


                if (xd >= 0 && yd >= 0 && xd < w && yd < h) {
                    //piksel nowy x,y = piksel stary xd,yd
                    out.setRGB(x, y, img.getRGB(xd, yd));
                }
                //---------------------------------------------------------
            }
        }
        return out;
    }

    public static BufferedImage cylindricalMapping(BufferedImage img, double f) {
        if (img == null) {
            return null;
        }

        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage out = new BufferedImage(w, h, img.getType());
        //System.out.println("w:"+w+", h:"+h);

        int x0 = (int) Math.floor(w / 2) + 1;
        int y0 = (int) Math.floor(h / 2) + 1;

        double tmax = Math.atan2((double) (w - x0), f);
        double tmin = Math.atan2(-((double) x0), f);
        double tstep = (tmax - tmin) / ((double) w);

        double vmax = ((double) (h - y0)) / f;
        double vmin = (-(double) y0) / f;
        double vstep = (vmax - vmin) / ((double) h);

        double theta, tan, cos;
        int x, y;

        for (int t = 0; t < w; t++) {
            theta = tmin + (double) t * tstep;
            tan = Math.tan(theta);
            cos = Math.cos(theta);
            x = (int) Math.round(f * tan) + x0;
            for (int v = 0; v < h; v++) {
                //nearest neighbour---------------------------------------
                //x = (int)Math.round(f*tan) + x0;
                y = (int) Math.round((vmin + (double) v * vstep) * f / cos) + y0;
                if (x >= 0 && y >= 0 && x < w && y < h) {
                    //piksel nowy x,y = piksel stary xd,yd
                    out.setRGB(t, v, img.getRGB(x, y));
                }
                //---------------------------------------------------------
            }
        }
        return out;
    }

    public static BufferedImage sphericalMapping(BufferedImage img, double f) {
        if (img == null) {
            return null;
        }

        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage out = new BufferedImage(w, h, img.getType());
        //System.out.println("w:"+w+", h:"+h);

        int x0 = (int) Math.floor(w / 2) + 1;
        int y0 = (int) Math.floor(h / 2) + 1;

        double tmax = Math.atan2((double) (w - x0), f);
        double tmin = Math.atan2(-((double) x0), f);
        double tstep = (tmax - tmin) / ((double) w);

        double fimax = Math.atan2((double) (h - y0), Math.sqrt(f * f));
        double fimin = Math.atan2(-(double) y0, Math.sqrt(f * f));
        double fistep = (fimax - fimin) / ((double) h);
        //System.out.println("fimax:"+fimax+", fimin:"+fimin);

        double theta, tantheta, costheta, tanfi, phi;
        int x, y;

        for (int t = 0; t < w; t++) {
            theta = tmin + (double) t * tstep;
            tantheta = Math.tan(theta);
            x = (int) Math.round(f * tantheta) + x0;
            for (int fi = 0; fi < h; fi++) {
                //nearest neighbour---------------------------------------
                phi = fimin + (double) fi * fistep;
                tanfi = Math.tan(phi);
                //x = (int)Math.round(f*tantheta) + x0;
                y = (int) Math.round(Math.sqrt((x - x0) * (x - x0) + f * f) * tanfi) + y0;
                if (x >= 0 && y >= 0 && x < w && y < h) {
                    //piksel nowy x,y = piksel stary xd,yd
                    out.setRGB(t, fi, img.getRGB(x, y));
                }
                //---------------------------------------------------------
            }
        }
        return out;
    }

    public static BufferedImage resizeImage(BufferedImage img, double xScale, double yScale, int type) {
        if (img == null) {
            return null;
        }

        if (xScale <= 0 || yScale <= 0) {
            return null;
        }

        int w = img.getWidth();
        int h = img.getHeight();

        int neww = (int) (((double) w) * xScale);
        int newh = (int) (((double) h) * yScale);

        BufferedImage out = new BufferedImage(neww, newh, img.getType());

        AffineTransform tr = new AffineTransform();
        tr.scale(xScale, yScale);
        BufferedImageOp op = new AffineTransformOp(tr, type);
        op.filter(img, out);
        return out;
    }

    public static BufferedImage rotateImage(BufferedImage img, double angle, int type) {
        return rotateImage(img, angle, type, Color.BLACK);
    }

    public static BufferedImage rotateImage(BufferedImage img, double angle, int type, Color fillBgColor) {
        if (img == null) {
            return null;
        }

        if (angle > 360.0 || angle < -360) {
            angle = angle % 360.0;
        }

        if (angle < 0) {
            angle = 360 + angle;
        }

        if (angle == 0.0 || angle == 360.0) {
            return img;
        }


        //System.out.println("angle="+angle);

        int w = img.getWidth();
        int h = img.getHeight();

        /*
        AffineTransform tr = new AffineTransform();
        tr.rotate(theta,w/2,h/2);
        BufferedImageOp op = new AffineTransformOp(tr, type);
        BufferedImage out = op.filter(img,null);
         */
        /*
        AffineTransform tr = new AffineTransform();
        tr.rotate(theta, w/2.0, h/2.0);
        AffineTransform translationTransform = findTranslation(tr, img);
        tr.preConcatenate(translationTransform);
        BufferedImageOp op = new AffineTransformOp(tr, type);
        
        BufferedImage out = op.filter(img,null);
         */
        BufferedImage out = null;
        if (angle == 90.0 || angle == 180.0 || angle == 270.0) {
            switch ((int) angle) {
                case 90:
                    out = new BufferedImage(h, w, img.getType());
                    for (int x = 0; x < w; x++) {
                        for (int y = 0; y < h; y++) {
                            out.setRGB(h - y - 1, x, img.getRGB(x, y));
                        }
                    }
                    break;
                case 180:
                    out = new BufferedImage(w, h, img.getType());
                    for (int x = 0; x < w; x++) {
                        for (int y = 0; y < h; y++) {
                            out.setRGB(w - x - 1, h - y - 1, img.getRGB(x, y));
                        }
                    }
                    break;
                case 270:
                    out = new BufferedImage(h, w, img.getType());
                    for (int x = 0; x < w; x++) {
                        for (int y = 0; y < h; y++) {
                            out.setRGB(y, w - x - 1, img.getRGB(x, y));
                        }
                    }
                    break;
            }
        } else {
            double theta = angle * Math.PI / 180.0;
            int neww = w, newh = h;
            double dx = 0.0, dy = 0.0;
            double s = Math.sin(theta);
            double c = Math.cos(theta);
            if (angle > 0.0 && angle < 90.0) {
                neww = (int) Math.round(((double) w) * c + ((double) h) * s);
                newh = (int) Math.round(((double) w) * s + ((double) h) * c);
                dx = ((double) h) * s;
                dy = 0.0;
            } else if (angle > 90.0 && angle < 180.0) {
                neww = (int) Math.round(-((double) w) * c + ((double) h) * s);
                newh = (int) Math.round(((double) w) * s - ((double) h) * c);
                dx = -((double) w) * c + ((double) h) * s;
                dy = -((double) h) * c;
            } else if (angle > 180.0 && angle < 270.0) {
                neww = (int) Math.round(-((double) w) * c - ((double) h) * s);
                newh = (int) Math.round(-((double) w) * s - ((double) h) * c);
                dx = -((double) w) * c;
                dy = -((double) w) * s - ((double) h) * c;
            } else if (angle > 270.0 && angle < 360.0) {
                neww = (int) Math.round(((double) w) * c - ((double) h) * s);
                newh = (int) Math.round(-((double) w) * s + ((double) h) * c);
                dx = 0.0;
                dy = -((double) w) * s;
            }

            AffineTransform tr = new AffineTransform();
            tr.translate(dx, dy);
            tr.rotate(theta);
            BufferedImageOp op = new AffineTransformOp(tr, type);
            out = new BufferedImage(neww, newh, img.getType());
            Graphics2D g2d = (Graphics2D) out.getGraphics();
            Rectangle clear = new Rectangle(0, 0, out.getWidth(), out.getHeight());
            g2d.setPaint(fillBgColor);
            g2d.fill(clear);
            op.filter(img, out);
        }
        return out;
    }

    public static BufferedImage stitchImages(BufferedImage[] images, int[] relX) {
        if (images == null || images.length < 2) {
            return null;
        }

        int[] xMax = max(relX);
        int x0 = relX[0];
        int xmax = xMax[0];
        int xmaxIndex = xMax[1];
        int W = xmax + images[xmaxIndex].getWidth();
        int H = images[0].getHeight();
        BufferedImage out = new BufferedImage(W, H, images[0].getType());
        int[] relRelX = new int[relX.length];
        relRelX[0] = 0;

        for (int i = 1; i < relX.length; i++) {
            relRelX[i] = relX[i] - relX[i - 1];
            //System.out.println("relRelX["+i+"]="+relRelX[i]);
        }

        for (int i = 0; i < images.length; i++) {
            if (i == 0) {
                //dla 0 obrazka
                copyRGBcolumns(images[0], 0, relX[1], out, 0);
                fadeImages(images[0], images[1], out, relX[1], relX[0]);
            } else if (i == images.length - 1) {
                //dla ostatniego obrazka
                int tmp1 = relX[i - 1] + images[i - 1].getWidth();
                copyRGBcolumns(images[i], tmp1 - relX[i], W - tmp1, out, tmp1);
            } else {
                //dla innych obrazkow
                int tmp1 = relX[i - 1] + images[i - 1].getWidth();
                copyRGBcolumns(images[i], tmp1 - relX[i], relX[i + 1] - tmp1, out, tmp1);
                fadeImages(images[i], images[i + 1], out, relRelX[i + 1], relX[i]);
            }
        }
        return out;
    }

    public static int[] max(int[] arr) {
        int[] out = new int[2];
        out[0] = arr[0];
        out[1] = 0;

        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > out[0]) {
                out[0] = arr[i];
                out[1] = i;
            }
        }
        return out;
    }

    private static double linearF(int x, int x0, int x1) {
        //funkca ma zwracac wartosci z przedzialu (0,1) na przedziale (x0,x1)
        return ((double) (x - x0) / (double) (x1 - x0));
    }

    public static void copyRGB(BufferedImage sourceImg, int sourceStartX, int sourceStartY, int sourceWidth, int sourceHeight, BufferedImage targetImg, int targetStartX, int targetStartY) {
        targetImg.setRGB(targetStartX, targetStartY, sourceWidth, sourceHeight, sourceImg.getRGB(sourceStartX, sourceStartY, sourceWidth, sourceHeight, null, 0, sourceWidth), 0, sourceWidth);
    }

    public static void copyRGBcolumns(BufferedImage sourceImg, int sourceStartX, int sourceWidth, BufferedImage targetImg, int targetStartX) {
        targetImg.setRGB(targetStartX, 0, sourceWidth, sourceImg.getHeight(), sourceImg.getRGB(sourceStartX, 0, sourceWidth, sourceImg.getHeight(), null, 0, sourceWidth), 0, sourceWidth);
    }

    public static void fadeImages(BufferedImage source1, BufferedImage source2, BufferedImage target, int relX, int targetX) {
        int pixel1, pixel2, newPixel;
        double f;
        int r1, g1, b1, r2, g2, b2;
        byte newR, newG, newB;
        ColorModel cm = source1.getColorModel();

        for (int x = relX; x < source1.getWidth(); x++) {
            f = linearF(x, relX, source1.getWidth());
            for (int y = 0; y < source1.getHeight(); y++) {
                pixel1 = source1.getRGB(x, y);
                pixel2 = source2.getRGB(x - relX, y);

                r1 = cm.getRed(pixel1);
                g1 = cm.getGreen(pixel1);
                b1 = cm.getBlue(pixel1);
                r2 = cm.getRed(pixel2);
                g2 = cm.getGreen(pixel2);
                b2 = cm.getBlue(pixel2);

                int tr = 10;

                if (r1 < tr && g1 < tr && b1 < tr) {
                    newPixel = pixel2;
                } else if (r2 < tr && g2 < tr && b2 < tr) {
                    newPixel = pixel1;
                } else {
                    newR = (byte) Math.round(((double) r1) * (1 - f) + ((double) r2) * f);
                    newG = (byte) Math.round(((double) g1) * (1 - f) + ((double) g2) * f);
                    newB = (byte) Math.round(((double) b1) * (1 - f) + ((double) b2) * f);
                    newPixel = (newR & 0xff) << 16 | (newG & 0xff) << 8 | (newB & 0xff) << 0;
                }
                target.setRGB(x + targetX, y, newPixel);
            }

        }
    }

    public static BufferedImage autoPanImage(BufferedImage img, Color bgcolor) {
        BufferedImage out = null;
        if (img == null) {
            return null;
        }

        int bgcolorInt = bgcolor.getRGB();
        int w = img.getWidth();
        int h = img.getHeight();
        int up = 0;
        int down = h - 1;
        int left = 0;
        int right = w - 1;
        int tmp;

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h / 2; y++) {
                tmp = img.getRGB(x, y);
                if (tmp == bgcolorInt) {
                    if (y > up) {
                        up = y;
                    }
                } else {
                    break;
                }
            }

            for (int y = h - 1; y > h / 2; y--) {
                tmp = img.getRGB(x, y);
                if (tmp == bgcolorInt) {
                    if (y < down) {
                        down = y;
                    }
                } else {
                    break;
                }
            }

        }


        for (int y = up; y < down; y++) {
            for (int x = 0; x < w / 2; x++) {
                tmp = img.getRGB(x, y);
                if (tmp == bgcolorInt) {
                    if (x > left) {
                        left = x;
                    }
                } else {
                    break;
                }
            }

            for (int x = w - 1; x > w / 2; x--) {
                tmp = img.getRGB(x, y);
                if (tmp == bgcolorInt) {
                    if (x < right) {
                        right = x;
                    }
                } else {
                    break;
                }
            }

        }


        System.out.println("up=" + up);
        System.out.println("down=" + down);
        System.out.println("left=" + left);
        System.out.println("right=" + right);


        out = img.getSubimage(left, up, right - left + 1, down - up + 1);
        return out;
    }

    private static AffineTransform findTranslation(AffineTransform at, BufferedImage bi) {
        Point2D p2din, p2dout;

        p2din = new Point2D.Double(0.0, 0.0);
        p2dout = at.transform(p2din, null);
        double ytrans = p2dout.getY();

        p2din = new Point2D.Double(0, bi.getHeight());
        p2dout = at.transform(p2din, null);
        double xtrans = p2dout.getX();

        AffineTransform tat = new AffineTransform();
        tat.translate(-xtrans, -ytrans);
        return tat;
    }

//    public static BufferedImage combineRGB(BufferedImage inRed, BufferedImage inGreen, BufferedImage inBlue) {
//        BufferedImage[] imgs = new BufferedImage[3];
//        imgs[0] = inRed;
//        imgs[1] = inGreen;
//        imgs[2] = inBlue;
//        return combineRGB(imgs);
//    }
//    
//    
//    public static BufferedImage combineRGB(BufferedImage[] inImgs) {
//        BufferedImage out = null;
//        int width = 0;
//        int height = 0;
//        if(inImgs == null)
//            return null;
//        
//        if(inImgs.length != 3) 
//            return null;
//        
//        for (int i = 0; i < inImgs.length; i++) {
//            if(inImgs[i] == null)
//                return null;
//        }
//        
//        //check if all images are the same size
//        width = inImgs[0].getWidth();
//        height = inImgs[0].getHeight();
//        for (int i = 1; i < inImgs.length; i++) {
//            if(inImgs[i].getWidth() != width || inImgs[i].getHeight() != height)
//                return null;
//        }
//        
//        //create output image
//        out = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
//        
//        //set pixels
//        WritableRaster outRaster = out.getRaster();
//        WritableRaster[] inRasters = new WritableRaster[3];
//        for (int i = 0; i < 3; i++) {
//            inRasters[i] = inImgs[i].getRaster();
//        }
//        
//        for (int x = 0; x < width; x++) {
//            for (int y = 0; y < height; y++) {
//                for (int i = 0; i < 3; i++) {
//                    outRaster.setSample(x,y,i,inRasters[i].getSample(x,y,0));
//                }
//            }
//        }
//        return out;
//    }
//    public static BufferedImage combineARGB(BufferedImage inRed, BufferedImage inGreen, BufferedImage inBlue, BufferedImage inAlpha) {
//        BufferedImage[] imgs = new BufferedImage[4];
//        imgs[0] = inRed;
//        imgs[1] = inGreen;
//        imgs[2] = inBlue;
//        imgs[3] = inAlpha;
//        return combineARGB(imgs);
//    }
    public static BufferedImage combineRGB(BufferedImage[] inImgs) {
        if (inImgs.length == 3) {
            return combineRGBA(inImgs);
        } else {
            BufferedImage[] tmp = new BufferedImage[3];
            tmp[0] = inImgs[0];
            tmp[1] = inImgs[1];
            tmp[2] = inImgs[2];
            return combineRGBA(tmp);
        }
    }

    public static BufferedImage combineRGBA(BufferedImage[] inImgs) {
        BufferedImage out = null;
        int numBands = 0;
        int width = 0;
        int height = 0;
        if (inImgs == null) {
            return null;
        }

        if (inImgs.length != 4 && inImgs.length != 3) {
            return null;
        }

        if (inImgs.length == 3) {
            numBands = 3;
        } else if (inImgs.length == 4) {
            if (inImgs[3] == null) {
                numBands = 3;
            } else {
                numBands = 4;
            }
        }

        for (int i = 0; i < numBands; i++) {
            if (inImgs[i] == null) {
                return null;
            }
        }

        //check if all images are the same size
        width = inImgs[0].getWidth();
        height = inImgs[0].getHeight();
        for (int i = 1; i < numBands; i++) {
            if (inImgs[i].getWidth() != width || inImgs[i].getHeight() != height) {
                return null;
            }
        }

        //create output image
        if (numBands == 3) {
            out = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        } else {
            out = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        }

        //set pixels
        WritableRaster outRaster = out.getRaster();


        WritableRaster[] inRasters = new WritableRaster[numBands];
        for (int i = 0; i < numBands; i++) {
            inRasters[i] = inImgs[i].getRaster();
        }

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int i = 0; i < numBands; i++) {
                    outRaster.setSample(x, y, i, inRasters[i].getSample(x, y, 0));
                }
            }
        }
        return out;
    }

    public static RegularField bufferedImage2RegularField(BufferedImage inImage, boolean vFlip) {
        if (inImage == null) {
            return null;
        }

        int[] dims = new int[2];
        dims[0] = inImage.getWidth();
        dims[1] = inImage.getHeight();

        RegularField field = new RegularField();
        field.setDims(dims);

        WritableRaster raster = inImage.getRaster();
        byte[][] samples = null;
        int i = 0;
        switch (inImage.getType()) {
            case BufferedImage.TYPE_BYTE_GRAY:
                samples = new byte[1][];
                samples[0] = new byte[dims[0] * dims[1]];
                if (vFlip) {
                    for (int y = 0; y < dims[1]; y++) {
                        for (int x = 0; x < dims[0]; x++) {
                            samples[0][i++] = (byte) raster.getSample(x, dims[1] - y - 1, 0);
                        }
                    }
                } else {
                    for (int y = 0; y < dims[1]; y++) {
                        for (int x = 0; x < dims[0]; x++) {
                            samples[0][i++] = (byte) raster.getSample(x, y, 0);
                        }
                    }
                }
                field.addData(DataArray.create(samples[0], 1, "grayscaleData"));
                break;
            case BufferedImage.TYPE_INT_RGB:
                samples = new byte[3][];
                samples[0] = new byte[dims[0] * dims[1]];
                samples[1] = new byte[dims[0] * dims[1]];
                samples[2] = new byte[dims[0] * dims[1]];
                if (vFlip) {
                    for (int y = 0; y < dims[1]; y++) {
                        for (int x = 0; x < dims[0]; x++) {
                            samples[0][i] = (byte) raster.getSample(x, dims[1] - y - 1, 0);
                            samples[1][i] = (byte) raster.getSample(x, dims[1] - y - 1, 1);
                            samples[2][i] = (byte) raster.getSample(x, dims[1] - y - 1, 2);
                            i++;
                        }
                    }
                } else {
                    for (int y = 0; y < dims[1]; y++) {
                        for (int x = 0; x < dims[0]; x++) {
                            samples[0][i] = (byte) raster.getSample(x, y, 0);
                            samples[1][i] = (byte) raster.getSample(x, y, 1);
                            samples[2][i] = (byte) raster.getSample(x, y, 2);
                            i++;
                        }
                    }
                }
                field.addData(DataArray.create(samples[0], 1, "redData"));
                field.addData(DataArray.create(samples[1], 1, "greenData"));
                field.addData(DataArray.create(samples[2], 1, "blueData"));
                break;
            case BufferedImage.TYPE_INT_ARGB:
                samples = new byte[4][];
                samples[0] = new byte[dims[0] * dims[1]];
                samples[1] = new byte[dims[0] * dims[1]];
                samples[2] = new byte[dims[0] * dims[1]];
                samples[3] = new byte[dims[0] * dims[1]];
                for (int y = 0; y < dims[1]; y++) {
                    for (int x = 0; x < dims[0]; x++) {
                        samples[0][i] = (byte) raster.getSample(x, dims[1] - y - 1, 0);
                        samples[1][i] = (byte) raster.getSample(x, dims[1] - y - 1, 1);
                        samples[2][i] = (byte) raster.getSample(x, dims[1] - y - 1, 2);
                        samples[3][i] = (byte) raster.getSample(x, dims[1] - y - 1, 3);
                        i++;
                    }
                }
                field.addData(DataArray.create(samples[0], 1, "redData"));
                field.addData(DataArray.create(samples[1], 1, "greenData"));
                field.addData(DataArray.create(samples[2], 1, "blueData"));
                field.addData(DataArray.create(samples[3], 1, "alphaData"));
                break;
        }
        
        float[][] affine = new float[4][3];
        for (int j = 0; j < 3; j++) {
            for (int k = 0; k < 3; k++) {
                affine[j][k] = 0.0f;                
                if(j == k)
                    affine[j][k] = 1.0f;
            }
        }
        
        affine[3][0] = -(float)dims[0]/2.0f;
        affine[3][1] = -(float)dims[1]/2.0f;
        affine[3][2] = 0.0f;
        field.setAffine(affine);
        return field;
    }

    public static BufferedImage invert(BufferedImage inImg) {
        if (inImg == null) {
            return null;
        }

        int width = inImg.getWidth();
        int height = inImg.getHeight();
        BufferedImage outImg = new BufferedImage(width, height, inImg.getType());
        WritableRaster outRaster = outImg.getRaster();
        WritableRaster inRaster = inImg.getRaster();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                for (int i = 0; i < outRaster.getNumBands(); i++) {
                    outRaster.setSample(x, y, i, 255 - inRaster.getSample(x, y, i));
                }
            }
        }

        return outImg;
    }

    public static BufferedImage addAlpha(BufferedImage src, BufferedImage alpha) {
        int w = src.getWidth();
        int h = src.getHeight();

        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        WritableRaster wr = bi.getWritableTile(0, 0);
        WritableRaster wr3 = wr.createWritableChild(0, 0, w, h, 0, 0, new int[]{0, 1, 2});
        WritableRaster wr1 = wr.createWritableChild(0, 0, w, h, 0, 0, new int[]{3});
        wr3.setRect(src.getData());
        wr1.setRect(alpha.getData());

        bi.releaseWritableTile(0, 0);

        return bi;
    }

    public static BufferedImage transparentColor(BufferedImage src, Color trColor) {
        int w = src.getWidth();
        int h = src.getHeight();
        BufferedImage dst = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        if (src.getRaster().getNumBands() < 3) {
            for (int i = 0; i < 3; i++) {
                dst.getRaster().setSamples(0, 0, w, h, i, src.getRaster().getSamples(0, 0, w, h, 0, (int[]) null));
            }
        } else if (src.getRaster().getNumBands() >= 3) {
            for (int i = 0; i < 3; i++) {
                dst.getRaster().setSamples(0, 0, w, h, i, src.getRaster().getSamples(0, 0, w, h, i, (int[]) null));
            }
        }

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                if (dst.getRaster().getSample(x, y, 0) == trColor.getRed()
                        && dst.getRaster().getSample(x, y, 1) == trColor.getGreen()
                        && dst.getRaster().getSample(x, y, 2) == trColor.getBlue()) {
                    dst.getRaster().setSample(x, y, 3, 0);
                } else {
                    dst.getRaster().setSample(x, y, 3, 255);
                }
            }
        }
        return dst;
    }

    public static void makeTransparent(BufferedImage img, Color trColor) {
        int w = img.getWidth();
        int h = img.getHeight();
        if (img.getType() != BufferedImage.TYPE_INT_ARGB) {
            return;
        }

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                if (img.getRaster().getSample(x, y, 0) == trColor.getRed()
                        && img.getRaster().getSample(x, y, 1) == trColor.getGreen()
                        && img.getRaster().getSample(x, y, 2) == trColor.getBlue()) {
                    img.getRaster().setSample(x, y, 3, 0);
                }
            }
        }
    }

    public static BufferedImage copyImage(BufferedImage in) {
        BufferedImage out = new BufferedImage(in.getWidth(), in.getHeight(), in.getType());
        for (int i = 0; i < in.getRaster().getNumBands(); i++) {
            out.getRaster().setSamples(0, 0, out.getWidth(), out.getHeight(), i, in.getRaster().getSamples(0, 0, in.getWidth(), in.getHeight(), i, (int[]) null));
        }
        return out;
    }

    public static BufferedImage createColorMappedBufferedImage(float[] data, DefaultColorMap1D cMap, float low, float up, int w, int h,
            int startX, int stopX, int startY, int stopY, boolean vFlip, boolean hFlip) {
        int myW = stopX - startX + 1;
        int myH = stopY - startY + 1;
        BufferedImage out = new BufferedImage(myW, myH, BufferedImage.TYPE_INT_RGB);
        int[] cmapLUT = cMap.getRGBColorTable();
        int cmapLUTSize = cmapLUT.length - 1;
        float cs = (float) cmapLUTSize / (up - low);
        int c;

        for (int j = 0; j < myH; j++) {
            for (int i = 0; i < myW; i++) {
                c = (int) ((data[(j + startY) * w + i + startX] - low) * cs);
                if (c < 0) {
                    c = 0;
                }
                if (c > cmapLUTSize) {
                    c = cmapLUTSize;
                }
                if (vFlip && hFlip) {
                    out.setRGB(myW - 1 - i, myH - 1 - j, cmapLUT[c]);
                } else if (vFlip && !hFlip) {
                    out.setRGB(i, myH - 1 - j, cmapLUT[c]);
                } else if (!vFlip && hFlip) {
                    out.setRGB(myW - 1 - i, j, cmapLUT[c]);
                } else {
                    out.setRGB(i, j, cmapLUT[c]);
                }
            }
        }
        return out;
    }

    public static BufferedImage switchAxes(BufferedImage img) {
        if (img == null) {
            return null;
        }

        return rotateImage(flipImageHorizontal(img), -90, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
    }

    public static BufferedImage flipImageHorizontal(BufferedImage img) {
        if (img == null) {
            return null;
        }

        if (img.getType() == 0) {
            img = convertToARGB(img);
        }
        BufferedImage out = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        for (int y = 0; y < out.getHeight(); y++) {
            for (int x = 0; x < out.getWidth(); x++) {
                out.setRGB(x, y, img.getRGB(img.getWidth() - x - 1, y));
            }
        }
        return out;
    }

    public static BufferedImage flipImageVertical(BufferedImage img) {
        if (img == null) {
            return null;
        }

        if (img.getType() == 0) {
            img = convertToARGB(img);
        }
        BufferedImage out = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        for (int y = 0; y < out.getHeight(); y++) {
            for (int x = 0; x < out.getWidth(); x++) {
                out.setRGB(x, y, img.getRGB(x, img.getHeight() - y - 1));
            }
        }
        return out;
    }

    public static BufferedImage convertToARGB(BufferedImage img) {
        if (img == null) {
            return null;
        }

        BufferedImage out = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        ColorConvertOp cco = new ColorConvertOp(img.getColorModel().getColorSpace(), out.getColorModel().getColorSpace(), null);
        cco.filter(img, out);
        return out;
    }

    private ImageUtilities() {
    }

    
    public static void writePng(BufferedImage img, File file) throws IOException {
        if (img == null) {
            return;
        }

        ImageIO.write(img, "png", file);
    }
    
    public static void writeJpeg(BufferedImage img, float quality, File file) throws FileNotFoundException, IOException {
        if (img == null) {
            return;
        }

        float q = quality;
        if (q < 0) {
            q = 0;
        }
        if (q > 1) {
            q = 1;
        }

        Iterator iter = ImageIO.getImageWritersByFormatName("jpeg");
        ImageWriter writer = (ImageWriter) iter.next();
        ImageWriteParam iwp = writer.getDefaultWriteParam();
        iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        iwp.setCompressionQuality(q);   // float between 0 and 1
                                        // 1 specifies minimum compression and maximum quality

        FileImageOutputStream output = new FileImageOutputStream(file);
        writer.setOutput(output);
        IIOImage image = new IIOImage(img, null, null);
        writer.write(null, image, iwp);
        writer.dispose();
    }
        
}
