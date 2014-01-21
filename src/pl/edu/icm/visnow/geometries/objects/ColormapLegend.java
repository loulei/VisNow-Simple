//<editor-fold defaultstate="collapsed" desc=" COPYRIGHT AND LICENSE ">
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
//</editor-fold>



package pl.edu.icm.visnow.geometries.objects;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import javax.media.j3d.J3DGraphics2D;
import javax.swing.JLabel;
import pl.edu.icm.visnow.datamaps.ColorMapManager;
import pl.edu.icm.visnow.datamaps.colormap1d.DefaultColorMap1D;
import pl.edu.icm.visnow.geometries.parameters.ColormapLegendParameters;
import pl.edu.icm.visnow.geometries.utils.transform.LocalToWindow;
import pl.edu.icm.visnow.lib.utils.Range;

/**
 * @author Krzysztof S. Nowinski (know@icm.edu.pl) Warsaw University,
 * Interdisciplinary Centre for Mathematical and Computational Modelling
 */
public class ColormapLegend extends Geometry2D {

    public static final int NONE = 0;
    public static final int NORTH = 1;
    public static final int SOUTH = 2;
    public static final int EAST = 3;
    public static final int WEST = 4;
    protected float low = 0;
    protected float up = 1;
    protected float clow = 0;
    protected float cup = 1;
    protected DefaultColorMap1D colormap;
    protected ColormapLegendParameters params;
    protected Graphics2D gr;
    protected DecimalFormat intf;
    protected Range range;
    protected Font font;
    protected Font titleFont;
    /**
     * An array of explicitly set values for range values (used in custom
     * isolines values in VisNow Pro). If thrTable is not null, values from it
     * are used instead of values computed from range object (in such situation
     * range object is not used at all).
     */
    protected float[] thrTable;
    protected FontMetrics fm;

   public ColormapLegend()
   {
      super();
      name = "legend";
   }

    public void setParams(ColormapLegendParameters params) {
        this.params = params;
    }

    public void setThrTable(float[] thrTable) {
        this.thrTable = thrTable;
    }

    protected void drawHorizontalLegendRectangle(int x, int y, int l, int w) {
        int[] colorMapLookup;
        if (params != null && params.getColorMapLookup() != null)
            colorMapLookup = params.getColorMapLookup();
        else
            colorMapLookup = colormap.getRGBColorTable();
        if (thrTable != null) {
            gr.setStroke(new BasicStroke(2));
            for (int i = 0; i < thrTable.length; i++) {
                float v = thrTable[i];
                int k = (int) (255 * (v - clow) / (cup - clow));
                if (k < 0)
                    k = 0;
                if (k > 255)
                    k = 255;
                gr.setColor(new Color(colorMapLookup[k]));
                int j = x + (int) (l * (v - low) / (up - low));
                gr.drawLine(j, y, j, y + w);
            }
            gr.setColor(params.getColor());
            gr.setStroke(new BasicStroke(1));
        } else {
            GeneralPath ticks;
            BufferedImage img = new BufferedImage(256, 2, BufferedImage.TYPE_INT_ARGB);
            int[] pix = new int[512];
            for (int i = 0; i < 256; i++)
                pix[i] = pix[i + 256] = colorMapLookup[i];
            img.setRGB(0, 0, 256, 2, pix, 0, 256);
            gr.drawImage(img, x, y, l, w, null);
            ticks = new GeneralPath();
            ticks.moveTo(x, y);
            ticks.lineTo(x, y + w - 1);
            ticks.lineTo(x + l, y + w - 1);
            ticks.lineTo(x + l, y);
            ticks.lineTo(x, y);
            for (float t = range.getLow(); t <= up; t += range.getStep()) {   //here is: t <= up (and not range.getUp() - is that a mistake?
                int i = x + (int) (l * (t - low) / (up - low));
                ticks.moveTo(i, y);
                ticks.lineTo(i, y + w - 1);
            }
            gr.draw(ticks);
        }
    }

    protected void drawVerticalLegendRectangle(int x, int y, int l, int w) {
        int[] colorMapLookup;
        if (params != null && params.getColorMapLookup() != null)
            colorMapLookup = params.getColorMapLookup();
        else
            colorMapLookup = colormap.getRGBColorTable();
        if (thrTable != null) {
            gr.setStroke(new BasicStroke(2));
            for (int i = 0; i < thrTable.length; i++) {
                float v = thrTable[i];
                int k = (int) (255 * (v - clow) / (cup - clow));
                if (k < 0)
                    k = 0;
                if (k > 255)
                    k = 255;
                gr.setColor(new Color(colorMapLookup[k]));
                int j = y + l - (int) (l * (v - low) / (up - low));
                gr.drawLine(x, j, x + w, j);
            }
            gr.setColor(params.getColor());
            gr.setStroke(new BasicStroke(1));
        } else {
            GeneralPath ticks;
            BufferedImage img = new BufferedImage(2, 256, BufferedImage.TYPE_INT_ARGB);
            int[] pix = new int[512];
            for (int i = 0; i < 256; i++) {
                int k = pix.length - 2 - 2 * i;
                pix[k] = pix[k + 1] = colorMapLookup[i];
            }
            img.setRGB(0, 0, 2, 256, pix, 0, 2);
            gr.drawImage(img, x, y, w, l, null);
            ticks = new GeneralPath();
            ticks.moveTo(x, y);
            ticks.lineTo(x, y + l - 1);
            ticks.lineTo(x + w - 1, y + l - 1);
            ticks.lineTo(x + w - 1, y);
            ticks.lineTo(x, y);

            for (float t = range.getLow(); t <= up; t += range.getStep()) {   //here is: t <= up (and not range.getUp() - is that a mistake?
                int i = y + l - (int) (l * (t - low) / (up - low));
                ticks.moveTo(x, i);
                ticks.lineTo(x + w - 1, i);
            }
            gr.draw(ticks);
        }
    }

    protected void drawHorizontalLegendLabels(int x, int y, int l) {
        for (float t = range.getLow(); t <= up; t += range.getStep()) {   //here is: t <= up (and not range.getUp() - is that a mistake?
            String label = intf.format(t);
            gr.drawString(label, x + (int) (l * (t - low) / (up - low)) - fm.stringWidth(label) / 3, y);
        }
    }

    protected void drawVerticalLegendLabels(int x, int y, int l) {
        for (float t = range.getLow(); t <= up; t += range.getStep())   //here is: t <= up (and not range.getUp() - is that a mistake?
            gr.drawString(intf.format(t), x, y + l - (int) (l * (t - low) / (up - low)));
    }

    @Override
    public void draw2D(J3DGraphics2D g, LocalToWindow ltw, int windowHeight, int windowWidth) {
        if (!params.isEnabled())
            return;
        int l = 100, w = 20;
        int x = 10, y = 20;
        int fontSize = 10;
        int titleFontSize = 10;

        String title = params.getName();
        if (params.getUnit() != null && !params.getUnit().isEmpty())
            title = params.getName() + " (" + params.getUnit() + ")";
        colormap = ColorMapManager.getInstance().getColorMap1D(params.getColormap());
        
//        System.out.println("drawing legend with params "+params+" "+params.getColormap());
        
        gr = (Graphics2D) g;
        if (colormap == null || params.getPosition() == NONE)
            return;
        clow = params.getColormapLow();
        cup = params.getColormapUp();
        if (thrTable == null) {
            low = clow;
            up = cup;
        } else {
            low = Float.MAX_VALUE;
            up = -clow;
            for (int i = 0; i < thrTable.length; i++) {
                if (up < thrTable[i])
                    up = thrTable[i];
                if (low > thrTable[i])
                    low = thrTable[i];
            }
        }
        if (low == up || clow == cup)
           return;
        switch (params.getPosition()) {
            case SOUTH:
            case NORTH:
                x = (int) (windowWidth * params.getX());
                y = (int) (windowHeight * params.getY());
                l = (int) (windowWidth * params.getL());
                w = (int) (windowHeight * params.getW());
                break;
            case WEST:
            case EAST:
                x = (int) (windowWidth * params.getY());
                y = (int) (windowHeight * params.getX());
                l = (int) (windowHeight * params.getL());
                w = (int) (windowWidth * params.getW());
                break;
        }
        range = new Range((int) (l / (float) params.getFontSize() / 3), low, up);
        int k = (int) (-Math.log10(range.getStep())) + 1; //TODO know: what "1. " is for?? and why not to use log10()??
        fontSize = params.getFontSize();
        titleFontSize = (int) (1.25 * fontSize);
        font = new java.awt.Font("Dialog", 0, fontSize);
        titleFont = new java.awt.Font("Dialog", 0, titleFontSize);
        fm = new JLabel().getFontMetrics(font);
        String fString = "####";
        if (k > 0) {
            fString = fString += ".";
            for (int i = 0; i < k; i++)
                fString = fString += "#";
        }

        if (params.getPosition() == NORTH || params.getPosition() == SOUTH)
            range = new Range(low, up, (int) (40 * l / (float) fm.stringWidth(fString)));

        intf = new DecimalFormat(fString);
        gr.setColor(params.getColor());
        gr.setFont(font);
        if (fm == null)
            return;

        // title
        fm = new JLabel().getFontMetrics(titleFont);
        switch (params.getPosition()) {
            case SOUTH:
                drawHorizontalLegendRectangle(x, windowHeight - y - w, l, w);
                drawHorizontalLegendLabels(x, windowHeight - y - w - 3, l);
                gr.setFont(titleFont);
                gr.drawString(title, x + l + (int) (.6 * fm.stringWidth(fString)), windowHeight - y - w / 2);
                break;
            case NORTH:
                drawHorizontalLegendRectangle(x, y, l, w);
                drawHorizontalLegendLabels(x, y + w + fontSize + 3, l);
                gr.setFont(titleFont);
                gr.drawString(title, x + l + (int) (.6 * fm.stringWidth(fString)), y + w / 2);
                break;
            case WEST:
                drawVerticalLegendRectangle(x, (int) (windowHeight * params.getX()), l, w);
                drawVerticalLegendLabels(x + w + 3, (int) (windowHeight * params.getX()), l);
                gr.setFont(titleFont);
                gr.drawString(title, x, y - (int) (1.3 * params.getFontSize()));
                break;
            case EAST: {
                // calculating maximum width of a legend's values string
                int maxLabelWidth = 0;
                for (float t = range.getLow(); t <= up; t += range.getStep()) {  //previously here was: t <= up - was that a mistake?
                    String s = (intf.format(t));
                    if (fm.stringWidth(s) > maxLabelWidth)
                        maxLabelWidth = fm.stringWidth(s);
                }

                drawVerticalLegendRectangle(windowWidth - x - w, y, l, w);
                drawVerticalLegendLabels(windowWidth - x - w - 3 - maxLabelWidth, y, l);
                gr.setFont(titleFont);
                gr.drawString(title, windowWidth - x - fm.stringWidth(title), y - (int) (1.3 * params.getFontSize()));
                break;
            }
        }
    }

   public ColormapLegendParameters getParams()
   {
      return params;
   }
}
