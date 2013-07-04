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

package pl.edu.icm.visnow.datamaps;

import java.awt.Color;
import java.beans.PropertyChangeSupport;
import java.util.Vector;
import pl.edu.icm.visnow.datamaps.colormap1d.DefaultColorMap1D;
import pl.edu.icm.visnow.datamaps.colormap1d.RGBChannelColorMap1D;
import pl.edu.icm.visnow.datamaps.colormap2d.ChannelColorMap2D;
import pl.edu.icm.visnow.datamaps.colormap2d.ColorMap2D;

/**
 * @author  Michał Łyczek (lyczek@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class ColorMapManager {

    public static final int COLORMAP1D_RAINBOW = 0;
    public static final int COLORMAP1D_GRAY = 1;
    public static final int COLORMAP1D_HOT = 2;
    public static final int COLORMAP1D_BICOLOR = 3;
    public static final int COLORMAP1D_TRICOLOR = 4;
    public static final int COLORMAP1D_HOT_1 = 5;
    public static final int COLORMAP1D_BLUE_WHITE_RED = 7;
    public static final int COLORMAP1D_BLUE_BLACK_RED = 6;
    public static final int COLORMAP1D_MEDICAL = 8;
    public static final int COLORMAP1D_GEOGRAPHICAL_LAND = 9;
    public static final int COLORMAP1D_GEOGRAPHICAL = 10;
    public static final int COLORMAP1D_BLUE_RED_YELLOW = 11;
    public static final int COLORMAP1D_FULLRAINBOW = 12;
    public static final int COLORMAP1D_RGB = 13;
    public static final int COLORMAP1D_BLACK_RED = 14;
    public static final int COLORMAP1D_BLACK_GREEN = 15;
    public static final int COLORMAP1D_BLACK_BLUE = 16;
    public final static int PREVIEW_SIZE = 32;
    public final static int SAMPLING_TABLE = 256;
    public final static int SAMPLING = SAMPLING_TABLE - 1;
    private static ColorMapManager singleton;
    private Vector<DefaultColorMap1D> colorMaps1D;
    private Vector<ColorMap2D> colorMaps2D;
    final public PropertyChangeSupport propertyChangeSupport;

    private void initStandardColormaps1D() {
        // rainbow
        {
            float[] pos = {
                .0f, .25f, .5f, .75f, 1.f
            };
            Color[] colors = {
                new Color(0.f, 0.f, 1.f),
                new Color(0.f, 1.f, 1.f),
                new Color(0.f, 1.f, 0.f),
                new Color(1.f, 1.f, 0.f),
                new Color(1.f, 0.f, 0.f),};
            RGBChannelColorMap1D cm = new RGBChannelColorMap1D("Rainbow", true, pos, colors);
            colorMaps1D.addElement(cm);
        }
        // gray
        {
            float[] pos = {
                .0f, 1.f
            };
            Color[] colors = {
                new Color(0.f, 0.f, 0.f),
                new Color(1.f, 1.f, 1.f),};
            RGBChannelColorMap1D cm = new RGBChannelColorMap1D("Gray", true, pos, colors);
            colorMaps1D.addElement(cm);
        }
        // hot
        {
            Color[] colors =                 
            {   new Color(0.42f, 0.00f, 0.00f),
                new Color(1.00f, 0.40f, 0.11f),
                new Color(0.98f, 0.92f, 0.50f),
                new Color(0.91f, 0.90f, 0.90f),
                new Color(0.61f, 0.63f, 1.00f)
            };
            float[] pos = {
                0.00f, 0.35f, 0.57f, 0.76f, 1.00f
            };
            RGBChannelColorMap1D cm = new RGBChannelColorMap1D("Hot", true, pos, colors);
            colorMaps1D.addElement(cm);
        }
        // gray
        {
            float[] pos = {
                .0f, 1.f
            };
            Color[] colors = {
                new Color(0.f, 0.f, 0.f),
                new Color(1.f, 1.f, 1.f),};
            RGBChannelColorMap1D cm = new RGBChannelColorMap1D("bicolor", true, pos, colors);
            colorMaps1D.addElement(cm);
        }
        {
            Color[] colors = {
                new Color(0.f, 0.f, 1.f),
                new Color(1.f, 1.f, 1.f),
                new Color(1.f, 0.f, 0.f)
            };
            float[] pos = {
                0.f,  .5f,  1.f
            };
            RGBChannelColorMap1D cm = new RGBChannelColorMap1D("tricolor", true, pos, colors);
            colorMaps1D.addElement(cm);
        }
        // hot 1
        {
            Color[] colors = {
                new Color(0.5f, 0.f, 0.f),
                new Color(1.f, 0.f, 0.f),
                new Color(1.f, 1.f, 0.f),
                new Color(1.f, 1.f, 1.f),
                new Color(.5f, .5f, 1.f)
            };
            float[] pos = {
                0.f, .2f, .4f, .7f, 1.f
            };
            RGBChannelColorMap1D cm = new RGBChannelColorMap1D("Hot 1", true, pos, colors);
            colorMaps1D.addElement(cm);
        }
        // blue - white - red:
        {
            Color[] colors = {
                new Color(0.f, 0.f, 1.f),
                new Color(0.f, 0.f, .5f),
                new Color(0.f, 0.f, 0.f),
                new Color(.5f, 0.f, 0.f),
                new Color(1.f, 0.f, 0.f)
            };
            float[] pos = {
                0.f, .4f, .5f, .6f, 1.f
            };
            RGBChannelColorMap1D cm = new RGBChannelColorMap1D("Blue - Black - Red", true, pos, colors);
            colorMaps1D.addElement(cm);
        }
        // blue - black - red
        {
            Color[] colors = {
                new Color(0.f, 0.f, 1.f),
                new Color(.5f, .5f, 1.f),
                new Color(1.f, 1.f, 1.f),
                new Color(1.f, .5f, .5f),
                new Color(1.f, 0.f, 0.f)
            };
            float[] pos = {
                0.f, .4f, .5f, .6f, 1.f
            };
            RGBChannelColorMap1D cm = new RGBChannelColorMap1D("Blue - White - Red", true, pos, colors);
            colorMaps1D.addElement(cm);
        }
        // medical
        {

            Color[] colors = {
                new Color(0.f, 0.f, 0.f),
                new Color(.48f, .125f, .125f),
                new Color(1.f, .7f, .3f),
                new Color(1.f, 1.f, 1.f)
            };
            float[] pos = {
                0.f, .33f, .66f, 1.f
            };
            RGBChannelColorMap1D cm = new RGBChannelColorMap1D("Medical", true, pos, colors);
            colorMaps1D.addElement(cm);
        }
        // geographical land
        {
            Color[] colors = {                new Color(0.33f, 0.55f, 0.26f),
                new Color(0.44f, 0.80f, 0.00f),
                new Color(0.71f, 1.00f, 0.36f),
                new Color(0.81f, 0.81f, 0.29f),
                new Color(0.90f, 0.78f, 0.20f),
                new Color(0.96f, 0.71f, 0.43f),
                new Color(0.59f, 0.59f, 0.59f),
                new Color(1.00f, 1.00f, 1.00f),

            };            
            float[] pos = {0.00f, 0.17f, 0.31f, 0.44f, 0.53f, 0.62f, 0.77f, 1.00f};

            RGBChannelColorMap1D cm = new RGBChannelColorMap1D("Geographical - land", true, pos, colors);
            colorMaps1D.addElement(cm);
        }
        // geographical full
        {
            Color[] colors = {
                new Color(50, 100, 200), new Color(50, 150, 255), new Color(50, 255, 255), new Color(255, 255, 255), 
                new Color(85, 140, 66), new Color(113, 205, 0), new Color(180,255,93),
                new Color(255, 255, 0), new Color(232, 198, 0), new Color(244,195,110),
                new Color(151, 151, 151), new Color(255, 255, 255),
            };
            float[] pos = {0.f, .1f, .25f, .45f,
                .5f, .548f, .592f, .657f, .74f, .828f,.918f, 1.f
            };
            RGBChannelColorMap1D cm = new RGBChannelColorMap1D("Geographical", true, pos, colors);
            colorMaps1D.addElement(cm);
        }
        // cmpl blue-red-yellow
        {
            float[] pos = {
                .0f, .33f, .67f, 1.f
            };
            Color[] colors = {
                new Color(0.f, 0.f, 1.f),
                new Color(1.f, 0.f, 1.f),
                new Color(1.f, 0.f, 0.f),
                new Color(1.f, 1.f, 0.f),};
            RGBChannelColorMap1D cm = new RGBChannelColorMap1D("blue-red-yellow", true, pos, colors);
            colorMaps1D.addElement(cm);
        }
        // full rainbow
        {
            float[] pos = {
                .0f, .167f, .333f, .5f, .667f, .833f, 1.f
            };
            Color[] colors = {
                new Color(1.f, 0.f, 1.f),
                new Color(0.f, 0.f, 1.f),
                new Color(0.f, 1.f, 1.f),
                new Color(0.f, 1.f, 0.f),
                new Color(1.f, 1.f, 0.f),
                new Color(1.f, 0.f, 0.f),
                new Color(1.f, 0.f, 1.f),};
            RGBChannelColorMap1D cm = new RGBChannelColorMap1D("Full rainbow", true, pos, colors);
            colorMaps1D.addElement(cm);
        }
        // red-green-blue
        {
            Color[] colors = {
                new Color(1.f, 0.f, 0.f),
                new Color(1.f, 0.f, 0.f),
                new Color(0.f, 1.f, 0.f),
                new Color(0.f, 1.f, 0.f),
                new Color(0.f, 0.f, 1.f),
                new Color(0.f, 0.f, 1.f)
            };
            float[] pos = {
                0.f, .33f, .34f, .66f, .67f, 1.f
            };
            RGBChannelColorMap1D cm = new RGBChannelColorMap1D("RGB", true, pos, colors);
            colorMaps1D.addElement(cm);
        }
        {
            Color[] colors = {
                new Color(0, 0, 0), new Color(255, 0, 0)
            };
            float[] pos = {
                0.f, 1.f
            };
            RGBChannelColorMap1D cm = new RGBChannelColorMap1D("Black-red", true, pos, colors);
            colorMaps1D.addElement(cm);
        }
        {
            Color[] colors = {
                new Color(0, 0, 0), new Color(0, 255, 0)
            };
            float[] pos = {
                0.f, 1.f
            };
            RGBChannelColorMap1D cm = new RGBChannelColorMap1D("Black-green", true, pos, colors);
            colorMaps1D.addElement(cm);
        }
        {
            Color[] colors = {
                new Color(0, 0, 0), new Color(0, 0, 255)
            };
            float[] pos = {
                0.f, 1.f
            };
            RGBChannelColorMap1D cm = new RGBChannelColorMap1D("Black-blue", true, pos, colors);
            colorMaps1D.addElement(cm);
        }
    }

    private void initStandardColormaps2D() {
        colorMaps2D.addElement(new ChannelColorMap2D("Black & White"));
    }

    protected ColorMapManager() {
        this.colorMaps1D = new Vector<DefaultColorMap1D>();
        this.colorMaps2D = new Vector<ColorMap2D>();
        this.propertyChangeSupport = new PropertyChangeSupport(this);

        initStandardColormaps1D();
        initStandardColormaps2D();
    }

    public static ColorMapManager getInstance() {
        if (singleton == null) {
            singleton = new ColorMapManager();
        }
        return singleton;
    }

    public void registerColorMap(ColorMap cm) {
        if (cm instanceof DefaultColorMap1D) {
            colorMaps1D.addElement((DefaultColorMap1D) cm);
        } else if (cm instanceof ColorMap2D) {
            colorMaps2D.addElement((ColorMap2D) cm);
        }
        propertyChangeSupport.firePropertyChange("ColorMaps", null, cm);
    }

    public void unregisterColorMap(ColorMap cm) {
        if (cm instanceof DefaultColorMap1D) {
            colorMaps1D.removeElement((DefaultColorMap1D) cm);
        } else if (cm instanceof ColorMap2D) {
            colorMaps2D.removeElement((ColorMap2D) cm);
        }
        propertyChangeSupport.firePropertyChange("ColorMaps", null, cm);
    }

    public ColorMapComboboxModel getColorMap2DListModel() {
        ColorMapComboboxModel colorMapComboboxModel = new ColorMapComboboxModel(2);
        return colorMapComboboxModel;
    }

    public ColorMapComboboxModel getColorMap1DListModel() {
        return new ColorMapComboboxModel(1);
    }

    public DefaultColorMap1D getColorMap1D(int index) {
        return colorMaps1D.get(index);
    }

    public ColorMap2D getColorMap2D(int index) {
        return colorMaps2D.get(index);
    }

    public int getColorMap1DIndex(DefaultColorMap1D colorMap1D) {
        return colorMaps1D.indexOf(colorMap1D);
    }

    public int getColorMap2DIndex(ColorMap2D colorMap2D) {
        return colorMaps2D.indexOf(colorMap2D);
    }

    public int getColorMap1DCount() {
        return colorMaps1D.size();
    }

    public int getColorMap2DCount() {
        return colorMaps2D.size();
    }
}
