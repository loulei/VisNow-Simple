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

package pl.edu.icm.visnow.datamaps.widgets;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import pl.edu.icm.visnow.datamaps.ColorMap;
import pl.edu.icm.visnow.datamaps.ColorMapManager;
import pl.edu.icm.visnow.datamaps.colormap1d.ColorMap1D;
import pl.edu.icm.visnow.datamaps.colormap2d.ColorMap2D;
import pl.edu.icm.visnow.datamaps.utils.Orientation;

/**
 * @author  Michał Łyczek (lyczek@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class ColorMapPreview extends JComponent {

    protected boolean editor;
    protected ColorMap colorMap;
    protected Orientation orientation = Orientation.VERTICAL;
    protected PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {

        public void propertyChange(PropertyChangeEvent evt) {
            repaint();
        }
    };

    public boolean isHorizontal() {
        return this.orientation == Orientation.HORIZONTAL;
    }

    public boolean isEditor() {
        return editor;
    }

    public void setEditor(boolean editor) {
        this.editor = editor;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    public ColorMapPreview() {
        this.orientation = Orientation.VERTICAL;
        this.editor = false;
        setBorder(new CompoundBorder(new LineBorder(Color.gray), new LineBorder(Color.white)));
    }

    public ColorMap getColorMap() {
        return colorMap;
    }

    public void setColorMap(ColorMap colorMap) {
        if (this.colorMap != null) {
            this.colorMap.removePropertyChangeListener(propertyChangeListener);
        }
        this.colorMap = colorMap;
        this.colorMap.addPropertyChangeListener(propertyChangeListener);
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Insets insets;
        if (getBorder() != null) {
            insets = getBorder().getBorderInsets(this);
        } else {
            insets = new Insets(0, 0, 0, 0);
        }
        Dimension dimension = new Dimension(getWidth() - insets.left - insets.right, getHeight() - insets.top - insets.bottom);

        g.setColor(getBackground());
        g.fillRect(insets.left, insets.top, dimension.width, dimension.height);
        if (colorMap != null) {
            int[] colorTable = colorMap.getRGBColorTable();

            if (colorMap instanceof ColorMap1D) {
                if (isHorizontal()) {
                    BufferedImage bi = new BufferedImage(ColorMapManager.SAMPLING, dimension.height, BufferedImage.TYPE_INT_ARGB);
                    for (int y = 0; y < dimension.height; y++) {
                        bi.setRGB(0, y, ColorMapManager.SAMPLING, 1, colorTable, 0, ColorMapManager.SAMPLING);
                    }
                    g.drawImage(bi, insets.left, insets.top, dimension.width, dimension.height, null);
                } else {
                    BufferedImage bi = new BufferedImage(dimension.width, ColorMapManager.SAMPLING, BufferedImage.TYPE_INT_ARGB);
                    for (int x = 0; x < dimension.width; x++) {
                        bi.setRGB(x, 0, 1, ColorMapManager.SAMPLING, colorTable, 0, 1);
                    }
                    g.drawImage(bi, insets.left, insets.top, dimension.width, dimension.height, null);
                }
            } else if (colorMap instanceof ColorMap2D) {
                BufferedImage bi = new BufferedImage(ColorMapManager.SAMPLING, ColorMapManager.SAMPLING, BufferedImage.TYPE_INT_ARGB);
                bi.setRGB(0, 0, ColorMapManager.SAMPLING, ColorMapManager.SAMPLING, colorTable, 0, ColorMapManager.SAMPLING);
                g.drawImage(bi, insets.left, insets.top, dimension.width, dimension.height, null);
            }

        }
    }
}
