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

package pl.edu.icm.visnow.datamaps.colormap2d;

import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import pl.edu.icm.visnow.datamaps.ColorMapManager;
import pl.edu.icm.visnow.datamaps.DefaultColorMap;
import pl.edu.icm.visnow.datamaps.colormap1d.ColorMap1D;
import pl.edu.icm.visnow.datamaps.colormap1d.DefaultColorMap1D;
import pl.edu.icm.visnow.datamaps.colormap1d.RGBChannelColorMap1D;

/**
 * @author  Michał Łyczek (lyczek@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class ChannelColorMap2D extends DefaultColorMap implements ColorMap2D {

    private ColorMap1D yChannel;
    private ColorMap1D xChannel;
    private final PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {

        public void propertyChange(PropertyChangeEvent evt) {
            propertyChangeSupport.firePropertyChange(evt);
        }
    };

    public ChannelColorMap2D() {
        this.yChannel = new RGBChannelColorMap1D();
        this.yChannel.addPropertyChangeListener(propertyChangeListener);
        this.xChannel = new RGBChannelColorMap1D();
        this.xChannel.addPropertyChangeListener(propertyChangeListener);
    }

    public ChannelColorMap2D(String name) {
        this();
        this.name = name;
    }

    public ChannelColorMap2D(DefaultColorMap1D yChannel, DefaultColorMap1D xChannel) {
        this.yChannel = yChannel;
        this.yChannel.addPropertyChangeListener(propertyChangeListener);
        this.xChannel = xChannel;
        this.xChannel.addPropertyChangeListener(propertyChangeListener);
    }

    public ChannelColorMap2D(String name, DefaultColorMap1D yChannel, DefaultColorMap1D xChannel) {
        this(yChannel, xChannel);
        this.name = name;
    }

    public ColorMap1D getYChannel() {
        return yChannel;
    }

    public ColorMap1D getXChannel() {
        return xChannel;
    }

    public int[] getColorTable(int sampling) {
        int[] colorTable = new int[sampling * sampling];
        int[] xColorMap = yChannel.getRGBColorTable();
        int[] yColorMap = xChannel.getRGBColorTable();

        int jump = ColorMapManager.SAMPLING / sampling;
        for (int y = 0, m = 0; y < sampling; y += jump) {
            for (int x = 0; x < sampling; x += jump) {
                int r1 = (yColorMap[x] >> 16) & 0xFF;
                int g1 = (yColorMap[x] >> 8) & 0xFF;
                int b1 = (yColorMap[x] >> 0) & 0xFF;
                int r2 = (xColorMap[y] >> 16) & 0xFF;
                int g2 = (xColorMap[y] >> 8) & 0xFF;
                int b2 = (xColorMap[y] >> 0) & 0xFF;

                // TODO: mixing mode
                colorTable[m++] = (-1 << 24) + ((r1 + r2) / 2 << 16) + ((g1 + g2) / 2 << 8) + (b1 + b2) / 2;
            }
        }
        return colorTable;
    }

   public byte[] getRGBByteColorTable(int sampling)
   {
      byte[] colorTable = new byte[3 * sampling * sampling];
      int[] xColorMap = yChannel.getRGBColorTable();
      int[] yColorMap = xChannel.getRGBColorTable();

      int jump = ColorMapManager.SAMPLING / sampling;
      for (int y = 0, m = 0; y < sampling; y += jump)
      {
         for (int x = 0; x < sampling; x += jump, m++)
         {
            int r1 = (yColorMap[x] >> 16) & 0xFF;
            int g1 = (yColorMap[x] >> 8) & 0xFF;
            int b1 = (yColorMap[x] >> 0) & 0xFF;
            int r2 = (xColorMap[y] >> 16) & 0xFF;
            int g2 = (xColorMap[y] >> 8) & 0xFF;
            int b2 = (xColorMap[y] >> 0) & 0xFF;

            // TODO: mixing mode
            colorTable[3 * m] = (byte) (0xff & (r1 + r2) / 2);
            colorTable[3 * m + 1] = (byte) (0xff & (g1 + g2) / 2);
            colorTable[3 * m + 2] = (byte) (0xff & (b1 + b2) / 2);
         }
      }
      return colorTable;
   }

   public byte[] getARGBByteColorTable(int sampling)
   {
      byte[] colorTable = new byte[4 * sampling * sampling];
      int[] xColorMap = yChannel.getRGBColorTable();
      int[] yColorMap = xChannel.getRGBColorTable();

      int jump = ColorMapManager.SAMPLING / sampling;
      for (int y = 0, m = 0; y < sampling; y += jump)
      {
         for (int x = 0; x < sampling; x += jump, m++)
         {
            int r1 = (yColorMap[x] >> 16) & 0xFF;
            int g1 = (yColorMap[x] >> 8) & 0xFF;
            int b1 = (yColorMap[x] >> 0) & 0xFF;
            int r2 = (xColorMap[y] >> 16) & 0xFF;
            int g2 = (xColorMap[y] >> 8) & 0xFF;
            int b2 = (xColorMap[y] >> 0) & 0xFF;

            // TODO: mixing mode
            colorTable[4 * m + 1] = (byte) (0xff & (r1 + r2) / 2);
            colorTable[4 * m + 2] = (byte) (0xff & (g1 + g2) / 2);
            colorTable[4 * m + 3] = (byte) (0xff & (b1 + b2) / 2);
         }
      }
      return colorTable;
   }

    public int[] getColorTable() {
        return getColorTable(ColorMapManager.SAMPLING);
    }
    
    public BufferedImage createPreviewFromCache() {

        int[] colorTable = getColorTable(ColorMapManager.PREVIEW_SIZE);
        BufferedImage previewBI = new BufferedImage(ColorMapManager.PREVIEW_SIZE, ColorMapManager.PREVIEW_SIZE, BufferedImage.TYPE_INT_ARGB);
        previewBI.setRGB(0, 0, ColorMapManager.PREVIEW_SIZE, ColorMapManager.PREVIEW_SIZE, colorTable, 0, ColorMapManager.PREVIEW_SIZE);
        return previewBI;
    }

   @Override
   public int[] getRGBColorTable()
   {
      return getColorTable(ColorMapManager.SAMPLING);
   }

   @Override
   public int[] getARGBColorTable()
   {
      return getColorTable(ColorMapManager.SAMPLING);
   }

   @Override
   public byte[] getRGBByteColorTable()
   {
      return getRGBByteColorTable(ColorMapManager.SAMPLING);
   }

   @Override
   public byte[] getARGBByteColorTable()
   {
      return getARGBByteColorTable(ColorMapManager.SAMPLING);
   }

}
