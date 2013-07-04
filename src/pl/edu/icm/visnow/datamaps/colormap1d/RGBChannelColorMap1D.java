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

package pl.edu.icm.visnow.datamaps.colormap1d;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import pl.edu.icm.visnow.datamaps.ColorMapManager;

/**
 * @author  Michał Łyczek (lyczek@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class RGBChannelColorMap1D extends DefaultColorMap1D {

    public final static int CHANNEL_RED = 0;
    public final static int CHANNEL_GREEN = 1;
    public final static int CHANNEL_BLUE = 2;

    @Override
    public int[] getARGBColorTable() {
        return getRGBColorTable();
    }

    public static class Knot extends Point2D.Float implements Comparable<Knot> {

        protected ColorKnot colorKnot;

        public Knot(Point2D p) {
            super((float) p.getX(), (float) p.getY());
        }

        public ColorKnot getColorKnot() {
            return colorKnot;
        }

        public void setColorKnot(ColorKnot colorKnot) {
            this.colorKnot = colorKnot;
        }

        public Knot(float x, float y) {
            super(x, y);
        }

        public int compareTo(Knot o) {
            if (o.x < this.x) {
                return 1;
            } else if (o.x == this.x) {
                return 0;
            } else {
                return -1;
            }
        }
    }

    public static class ColorKnot implements Comparable<ColorKnot> {

        protected Knot[] knots;
        protected float position;
        protected int color;

        public ColorKnot(float position, int color) {
            this.knots = new Knot[3];
            this.position = position;
            this.color = color;
        }

        public void setKnot(int channel, Knot knot) {
            knots[channel] = knot;
        }

        public Knot getKnot(int channel) {
            return knots[channel];
        }

        public void removeKnot(Knot knot) {
            for (int c = 0; c < 3; c++) {
                if (knots[c] == knot) {
                    knots[c] = null;
                    return;
                }
            }
        }

        public float distance(ColorKnot ck) {
            return Math.abs(ck.position - this.position);
        }

        public float distance(float position) {
            return Math.abs(position - this.position);
        }

        public int getColor() {
            return color;
        }

        protected void setColor(int color) {
            this.color = color;
        }

        public float getPosition() {
            return position;
        }

        protected void setPosition(float position) {
            this.position = position;
        }

        public int compareTo(ColorKnot o) {
            if (o.position < this.position) {
                return 1;
            } else if (o.position == this.position) {
                return 0;
            } else {
                return -1;
            }
        }
    }
    protected List<Knot>[] channelKnots;
    protected List<ColorKnot> colorKnots;

    @SuppressWarnings("unchecked")
    public RGBChannelColorMap1D() {
        super();
        this.channelKnots = new List[3];
        this.channelKnots[0] = new LinkedList<Knot>();
        this.channelKnots[1] = new LinkedList<Knot>();
        this.channelKnots[2] = new LinkedList<Knot>();
        this.colorKnots = new LinkedList<ColorKnot>();

        addColorKnot(new ColorKnot(0, Color.white.getRGB()));
        addColorKnot(new ColorKnot(1, Color.black.getRGB()));
    }

    @SuppressWarnings({"unchecked"})
    public RGBChannelColorMap1D(RGBChannelColorMap1D old) {
        super();
        this.channelKnots = new List[3];
        this.channelKnots[0] = new LinkedList<Knot>();
        this.channelKnots[1] = new LinkedList<Knot>();
        this.channelKnots[2] = new LinkedList<Knot>();
        this.colorKnots = new LinkedList<ColorKnot>();

        for (ColorKnot ck : old.getColorKnots()) {
            addColorKnot(new ColorKnot(ck.position, ck.color));
        }
    }

    @SuppressWarnings("unchecked")
    public RGBChannelColorMap1D(String name, boolean buildin) {
        super(name, buildin);
        this.channelKnots = new List[3];
        this.channelKnots[0] = new LinkedList<Knot>();
        this.channelKnots[1] = new LinkedList<Knot>();
        this.channelKnots[2] = new LinkedList<Knot>();
        this.colorKnots = new LinkedList<ColorKnot>();

        addColorKnot(new ColorKnot(0, Color.white.getRGB()));
        addColorKnot(new ColorKnot(1, Color.black.getRGB()));
    }

    @SuppressWarnings({"unchecked"})
    public void set(RGBChannelColorMap1D old) {
        this.channelKnots = new List[3];
        this.channelKnots[0] = new LinkedList<Knot>();
        this.channelKnots[1] = new LinkedList<Knot>();
        this.channelKnots[2] = new LinkedList<Knot>();
        this.colorKnots = new LinkedList<ColorKnot>();

        for (ColorKnot ck : old.getColorKnots()) {
            addColorKnot(new ColorKnot(ck.position, ck.color));
        }
    }

    @SuppressWarnings("unchecked")
    public RGBChannelColorMap1D(String name, boolean buildin, float[] pos, Color[] colors) {
        super(name, buildin);
        this.channelKnots = new List[3];
        this.channelKnots[0] = new LinkedList<Knot>();
        this.channelKnots[1] = new LinkedList<Knot>();
        this.channelKnots[2] = new LinkedList<Knot>();
        this.colorKnots = new LinkedList<ColorKnot>();

        for (int i = 0; i < pos.length; i++) {
            addColorKnot(new ColorKnot(pos[i], colors[i].getRGB()));
        }
    }

    public int getKnotIndex(ColorKnot colorKnot) {
        return colorKnots.indexOf(colorKnot);
    }

    public Knot getClosestKnot(int channel, Point2D position) {
        float minDist = Float.MAX_VALUE;
        Knot closestKnot = null;
        for (Knot p : channelKnots[channel]) {
            if (p.distance(position) < minDist) {
                minDist = (float) p.distance(position);
                closestKnot = p;
            }
        }
        return closestKnot;
    }

    public Knot getKnot(int channel, float position) {
        for (Knot p : channelKnots[channel]) {
            if (p.x == position) {
                return p;
            }
        }
        return null;
    }

    public ColorKnot getClosestColorKnot(float position) {
        float minDist = Float.MAX_VALUE;
        ColorKnot closestKnot = null;
        for (ColorKnot p : colorKnots) {
            if (p.distance(position) < minDist) {
                minDist = p.distance(position);
                closestKnot = p;
            }
        }
        return closestKnot;
    }

    public ColorKnot getColorKnot(float position) {
        for (ColorKnot p : colorKnots) {
            if (p.position == position) {
                return p;
            }
        }
        return null;
    }

    protected boolean innerAddKnot(int channel, Knot knot) {
        int index = Collections.binarySearch(channelKnots[channel], knot);

        if (index < 0) {
            channelKnots[channel].add(-index - 1, knot);
            return true;
//        } else if (index == 0 || index == channelKnots[channel].size() - 1) {
//            return false;
        } else //            channelKnots[channel].add(index, knot);
        //            return true;
        {
            return false;
        }

    }

    public void addKnot(int channel, Knot knot) {
        if (innerAddKnot(channel, knot)) {
            ColorKnot colorKnot = new ColorKnot(knot.x, getColor(knot.x));
            colorKnot.setKnot(channel, knot);
            knot.setColorKnot(colorKnot);

            innerAddColorKnot(colorKnot);
            propertyChangeSupport.firePropertyChange("colorMap", null, this);
        }
    }

    protected boolean innerAddColorKnot(ColorKnot colorKnot) {
        int index = Collections.binarySearch(colorKnots, colorKnot);

        if (index < 0) {
            colorKnots.add(-index - 1, colorKnot);
            return true;
//        } else if (index == 0 || index == colorKnots.size() - 1) {
//            return false;
        } else //            colorKnots.add(index, colorKnot);
        //            return true;
        {
            return false;
        }
    }

    public final void addColorKnot(ColorKnot colorKnot) {
        if (innerAddColorKnot(colorKnot)) {
            float[] color = new Color(colorKnot.color).getColorComponents(null);
            for (int c = 0; c < 3; c++) {
                Knot knot = new Knot(colorKnot.position, color[c]);
                innerAddKnot(c, knot);
                colorKnot.setKnot(c, knot);
                knot.setColorKnot(colorKnot);
            }
            propertyChangeSupport.firePropertyChange("colorMap", null, this);
        }
    }

    protected final void innerMoveKnot(Knot knot, Point2D newPosition) {
        float x = (float) newPosition.getX(), y = (float) newPosition.getY();
        x = x < 0 ? 0 : x;
        x = x > 1 ? 1 : x;
        y = y < 0 ? 0 : y;
        y = y > 1 ? 1 : y;

        if (isBoundaryKnot(knot)) {
            knot.setLocation(new Point2D.Float(knot.x, y));
        } else {
            knot.setLocation(new Point2D.Float(x, y));
        }
        Collections.sort(channelKnots[0]);
        Collections.sort(channelKnots[1]);
        Collections.sort(channelKnots[2]);
    }

    public void moveKnot(Knot knot, Point2D newPosition) {
        innerMoveKnot(knot, newPosition);
        innerMoveColorKnot(knot.colorKnot, (float) newPosition.getX());
        innerSetColor(knot.colorKnot, getColor(knot.colorKnot.position));
        ColorKnot newColorKnot = null;
        for (int c = 0; c < 3; c++) {
            Knot knot0 = knot.colorKnot.getKnot(c);
            if (knot0 != knot && knot0 != null) {
                knot.colorKnot.setKnot(c, null);
                if (newColorKnot == null) {
                    newColorKnot = new ColorKnot(knot0.x, getColor(knot0.x));
                    newColorKnot.setKnot(c, knot0);
                    knot0.setColorKnot(newColorKnot);
                    innerAddColorKnot(newColorKnot);
                } else {
                    newColorKnot.setKnot(c, knot0);
                    knot0.setColorKnot(newColorKnot);
                }
            }
        }
        updateColorKnots();
        propertyChangeSupport.firePropertyChange("colorMap", null, this);
    }

    public boolean isBoundaryKnot(Knot knot) {
        for (int c = 0; c < 3; c++) {
            if (channelKnots[c].contains(knot)) {
                int index = channelKnots[c].indexOf(knot);
                if (index == 0 || index == channelKnots[c].size() - 1) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isBoundaryColorKnot(ColorKnot colorKnot) {
        if (colorKnots.contains(colorKnot)) {
            int index = colorKnots.indexOf(colorKnot);
            if (index == 0 || index == colorKnots.size() - 1) {
                return true;
            }
        }
        return false;
    }

    protected void innerSetColor(ColorKnot colorKnot, int color) {
        colorKnot.setColor(color);
    }

    public void setColor(ColorKnot colorKnot, int color) {
        innerSetColor(colorKnot, color);
        float[] colorComps = new Color(color).getColorComponents(null);
        for (int c = 0; c < 3; c++) {
            Knot knot = colorKnot.getKnot(c);
            if (knot != null) {
                innerMoveKnot(knot, new Point2D.Float(knot.x, colorComps[c]));
            } else {
                Knot newKnot = new Knot(colorKnot.position, colorComps[c]);
                innerAddKnot(c, newKnot);
                newKnot.setColorKnot(colorKnot);
                colorKnot.setKnot(c, newKnot);
            }
        }
        propertyChangeSupport.firePropertyChange("colorMap", null, this);
    }

    protected void innerMoveColorKnot(ColorKnot colorKnot, float newPosition) {
        float p = newPosition;
        p = p < 0 ? 0 : p;
        p = p > 1 ? 1 : p;

        if (!isBoundaryColorKnot(colorKnot)) {
            colorKnot.setPosition(p);
        }
        Collections.sort(colorKnots);
    }

    public void moveColorKnot(ColorKnot colorKnot, float newPosition) {
        float oldPosition = colorKnot.position;
        innerMoveColorKnot(colorKnot, newPosition);
        float[] colors = getComponents(oldPosition);
        for (int c = 0; c < 3; c++) {
            Knot knot = colorKnot.getKnot(c);
            if (knot == null) {
                knot = new Knot(oldPosition, colors[c]);
                knot.setColorKnot(colorKnot);
                colorKnot.setKnot(c, knot);
                innerAddKnot(c, knot);
            }
            innerMoveKnot(knot, new Point2D.Float(newPosition, knot.y));
        }
        updateColorKnots();
        propertyChangeSupport.firePropertyChange("colorMap", null, this);
    }

    protected void updateColorKnots() {
        for (ColorKnot colorKnot : colorKnots) {
            colorKnot.setColor(getColor(colorKnot.position));
        }
    }

    protected void innerRemoveKnot(Knot knot) {
        boolean removeColorKnot = true;
        for (int c = 0; c < 3; c++) {
            Knot knot0 = knot.colorKnot.getKnot(c);
            if (knot != knot0 && knot0 != null) {
                removeColorKnot = false;
            }
        }
        for (int c = 0; c < 3; c++) {
            if (channelKnots[c].contains(knot)) {
                channelKnots[c].remove(knot);
            }
        }
        if (removeColorKnot) {
            innerRemoveColorKnot(knot.colorKnot);
        } else {
            knot.colorKnot.removeKnot(knot);
            knot.colorKnot.setColor(getColor(knot.colorKnot.position));
        }

    }

    public void removeKnot(Knot knot) {
        if (!isBoundaryKnot(knot)) {
            innerRemoveKnot(knot);
            updateColorKnots();
            propertyChangeSupport.firePropertyChange("colorMap", null, this);
        }
    }

    public void innerRemoveColorKnot(ColorKnot colorKnot) {
        colorKnots.remove(colorKnot);
    }

    public void removeColorKnot(ColorKnot colorKnot) {
        if (!isBoundaryColorKnot(colorKnot)) {
            innerRemoveColorKnot(colorKnot);
            for (int c = 0; c < 3; c++) {
                Knot knot = colorKnot.getKnot(c);
                if (knot != null) {
                    innerRemoveKnot(knot);
                }
            }
        }
        propertyChangeSupport.firePropertyChange("colorMap", null, this);
    }

    public List<Knot>[] getChannelKnots() {
        return channelKnots;
    }

    public List<ColorKnot> getColorKnots() {
        return colorKnots;
    }

    public float[] getComponents(float v) {
        float[] colors = {0, 0, 0};
        for (int c = 0; c < 3; c++) {
            int index = Collections.binarySearch(channelKnots[c], new Knot(v, 0));

            if (index < 0) {
                if (-index - 1 == 0) {
                    colors[c] = channelKnots[c].get(0).y;
                } else {
                    Knot knot0 = channelKnots[c].get(-index - 2);
                    Knot knot1 = channelKnots[c].get(-index - 1);

                    double dist = knot1.x - knot0.x;
                    colors[c] = (float) (knot0.y * (1 - ((v - knot0.x)) / dist) + knot1.y * ((v - knot0.x) / dist));
                }
            } else {
                colors[c] = channelKnots[c].get(index).y;
            }
        }
        return colors;
    }

    public int getColor(float v) {
        float[] colors = getComponents(v);

        int r = (int) (255 * colors[0]);
        int g = (int) (255 * colors[1]);
        int b = (int) (255 * colors[2]);

        return (-1 << 24) + (r << 16) + (g << 8) + b;
    }

    public int[] getRGBColorTable() {
        int[] colorTable = new int[ColorMapManager.SAMPLING_TABLE];

        int[] indexes = {0, 0, 0};
        float[] colors = {0, 0, 0};

        for (int i = 0; i < ColorMapManager.SAMPLING_TABLE; i++) {
            //float v = (float) i / ColorMapManager.SAMPLING_TABLE;
            float v = (float) i / (float) (ColorMapManager.SAMPLING_TABLE - 1);

            for (int c = 0; c < 3; c++) {
                if (v > channelKnots[c].get(indexes[c]).x) {
                    indexes[c]++;
                }
                if (indexes[c] == 0) {
                    Knot knot0 = channelKnots[c].get(indexes[c]);
                    colors[c] = knot0.y;
                } else {
                    Knot knot0 = channelKnots[c].get(indexes[c] - 1);
                    Knot knot1 = channelKnots[c].get(indexes[c]);
                    if (v == knot1.x) {
                        colors[c] = knot1.y;
                    } else {
                        double dist = knot1.x - knot0.x;
                        colors[c] = (float) (knot0.y * (1 - ((v - knot0.x)) / dist) + knot1.y * ((v - knot0.x) / dist));
                    }
                }
            }

            int r = (int) (255 * colors[0]);
            int g = (int) (255 * colors[1]);
            int b = (int) (255 * colors[2]);

            colorTable[i] = (-1 << 24) + (r << 16) + (g << 8) + b;
        }
        return colorTable;
    }

    public byte[] getRGBByteColorTable() {
        byte[] colorTable = new byte[3 * ColorMapManager.SAMPLING_TABLE];

        int[] indexes = {0, 0, 0};
        float[] colors = {0, 0, 0};

        for (int i = 0; i < ColorMapManager.SAMPLING_TABLE; i++) {
            //float v = (float) i / ColorMapManager.SAMPLING_TABLE;
            float v = (float) i / (float) (ColorMapManager.SAMPLING_TABLE - 1);

            for (int c = 0; c < 3; c++) {
                if (v > channelKnots[c].get(indexes[c]).x) {
                    indexes[c]++;
                }
                if (indexes[c] == 0) {
                    Knot knot0 = channelKnots[c].get(indexes[c]);
                    colors[c] = knot0.y;
                } else {
                    Knot knot0 = channelKnots[c].get(indexes[c] - 1);
                    Knot knot1 = channelKnots[c].get(indexes[c]);
                    if (v == knot1.x) {
                        colors[c] = knot1.y;
                    } else {
                        double dist = knot1.x - knot0.x;
                        colors[c] = (float) (knot0.y * (1 - ((v - knot0.x)) / dist) + knot1.y * ((v - knot0.x) / dist));
                    }
                }
            }
            for (int j = 0; j < colors.length; j++) {
                colorTable[3 * i + j] = (byte) (0xff & (int) (255 * colors[j]));
            }
        }
        return colorTable;
    }

    public byte[] getARGBByteColorTable() {
        byte[] colorTable = new byte[4 * ColorMapManager.SAMPLING_TABLE];

        int[] indexes = {0, 0, 0};
        float[] colors = {0, 0, 0};

        for (int i = 0; i < ColorMapManager.SAMPLING_TABLE; i++) {
            //float v = (float) i / ColorMapManager.SAMPLING_TABLE;
            float v = (float) i / (float) (ColorMapManager.SAMPLING_TABLE - 1);

            for (int c = 0; c < 3; c++) {
                if (v > channelKnots[c].get(indexes[c]).x) {
                    indexes[c]++;
                }
                if (indexes[c] == 0) {
                    Knot knot0 = channelKnots[c].get(indexes[c]);
                    colors[c] = knot0.y;
                } else {
                    Knot knot0 = channelKnots[c].get(indexes[c] - 1);
                    Knot knot1 = channelKnots[c].get(indexes[c]);
                    if (v == knot1.x) {
                        colors[c] = knot1.y;
                    } else {
                        double dist = knot1.x - knot0.x;
                        colors[c] = (float) (knot0.y * (1 - ((v - knot0.x)) / dist) + knot1.y * ((v - knot0.x) / dist));
                    }
                }
            }
            for (int j = 0; j < colors.length; j++) {
                colorTable[4 * i + j + 1] = (byte) (0xff & (int) (255 * colors[j]));
            }
        }
        return colorTable;
    }

    public int[] getDims() {
        int[] dims = {
            ColorMapManager.SAMPLING_TABLE
        };
        return dims;
    }
    
    public int getNKnots()
    {
       return colorKnots.size();
    }
    
}
