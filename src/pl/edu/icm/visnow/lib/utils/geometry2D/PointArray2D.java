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

package pl.edu.icm.visnow.lib.utils.geometry2D;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import pl.edu.icm.visnow.datasets.RegularField;

/**
 * @author Bartosz Borucki (babor@icm.edu.pl) Warsaw University,
 * Interdisciplinary Centre for Mathematical and Computational Modelling
 */
public class PointArray2D extends Array2D {

    public PointArray2D() {
        this("point array 2D");
    }

    public PointArray2D(String name) {
        super(null, name);
    }

    public PointArray2D(RegularField field) {
        super(field, "point array 2D");
    }

    private int pw, ph, istep, jstep;
    double stepper = 10.0;
    private BufferedImage img;
    private WritableRaster raster;

    @Override
    public void drawLocal2D(Graphics2D g, AffineTransform tr) {
        int c = 0;        
        if (this.getPanel() != null) {
            double scx = tr.getScaleX();
            double scy = tr.getScaleY();
            pw = this.getPanel().getWidth();
            ph = this.getPanel().getHeight();

            img = new BufferedImage(pw, ph, BufferedImage.TYPE_INT_ARGB);
            raster = img.getRaster();
            Graphics2D img_g2d = (Graphics2D) img.getGraphics();
            for (int i = 0; i < pw; i++) {
                for (int j = 0; j < ph; j++) {
                    raster.setSample(i, j, 0, 0); //R
                    raster.setSample(i, j, 1, 0); //G
                    raster.setSample(i, j, 2, 0); //B
                    raster.setSample(i, j, 3, 0); //A
                }
            }

            img_g2d.setStroke(stroke);
            
            int k;
            Point2D pxy = null;
            Point2D pij = null;
            
            istep = (int)Math.floor(stepper/scx);
            if(istep < 1)
                istep = 1;

            jstep = (int)Math.floor(stepper/scy);
            if(jstep < 1)
                jstep = 1;

            int iend = width;
            int jend = height;
            try {
                pij = tr.inverseTransform(new Point2D.Double(pw,ph), pij);
                iend = (int)Math.ceil(pij.getX());
                if(iend > width) iend = width;
                jend = (int)Math.ceil(pij.getY());
                if(jend > height) jend = height;
            } catch(NoninvertibleTransformException ex) {                
            }
            
            for (int i = 0; i < iend; i+=istep) {
                for (int j = 0; j < jend; j+=jstep) {
                    k = (height - 1 - j) * width * 4 + i * 4;
                    if(colors == null || k+3>=colors.length || k<0)
                        continue;
                    img_g2d.setColor(new Color((int) (colors[k] & 0xff),
                            (int) (colors[k + 1] & 0xff),
                            (int) (colors[k + 2] & 0xff),
                            (int) (colors[k + 3] & 0xff)));
                    pij = new Point2D.Double(i+0.5, j+0.5);
                    pxy = tr.transform(pij, pxy);
                    img_g2d.draw(new Line2D.Double(pxy.getX(), pxy.getY(), pxy.getX(), pxy.getY()));
                    c++;
                }
            }
            
            if(g.getComposite() != null && g.getComposite() instanceof AlphaComposite && renderingParams.getTransparency() > 0) {
                AlphaComposite ac = (AlphaComposite) g.getComposite();
                float alpha = ac.getAlpha();
                alpha = alpha * (1.0f - renderingParams.getTransparency());
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));                    
            } else if (renderingParams.getTransparency() > 0) {
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f - renderingParams.getTransparency()));
            }
            g.drawImage(img, 0, 0, null);

        }
    }
}
