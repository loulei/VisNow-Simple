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

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import pl.edu.icm.visnow.datasets.RegularField;

/**
 * @author Bartosz Borucki (babor@icm.edu.pl) Warsaw University,
 * Interdisciplinary Centre for Mathematical and Computational Modelling
 */
public class BoxArray2D extends Array2D {

    public BoxArray2D() {
        this("box array 2D");
    }

    public BoxArray2D(String name) {
        super(null, name);
    }

    public BoxArray2D(RegularField field) {
        super(field, "box array 2D");
    }

    private int pw, ph;
    double stepper = 10.0;
    private BufferedImage img;
    private WritableRaster raster;
    
    @Override
    public void drawLocal2D(Graphics2D g, AffineTransform tr) {
        if (this.getPanel() != null) {
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
            Point2D pxy1 = null, pxy2 = null, pxy3 = null, pxy4 = null;
            Point2D pij1 = null, pij2 = null, pij3 = null, pij4 = null;
                        
            pij1 = new Point2D.Double(0, 0);
            pxy1 = tr.transform(pij1, pxy1);
            pij2 = new Point2D.Double(0, width);
            pxy2 = tr.transform(pij2, pxy2);                    
            pij3 = new Point2D.Double(height, width);
            pxy3 = tr.transform(pij3, pxy3);                    
            pij4 = new Point2D.Double(height, 0);
            pxy4 = tr.transform(pij4, pxy4);                    

            img_g2d.setColor(Color.white);
            img_g2d.draw(new Line2D.Double(pxy1.getX(), pxy1.getY(), pxy2.getX(), pxy2.getY()));
            img_g2d.draw(new Line2D.Double(pxy2.getX(), pxy2.getY(), pxy3.getX(), pxy3.getY()));
            img_g2d.draw(new Line2D.Double(pxy3.getX(), pxy3.getY(), pxy4.getX(), pxy4.getY()));
            img_g2d.draw(new Line2D.Double(pxy4.getX(), pxy4.getY(), pxy1.getX(), pxy1.getY()));

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
