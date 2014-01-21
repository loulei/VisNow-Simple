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

package pl.edu.icm.visnow.geometries.gui.TransparencyEditor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.MemoryImageSource;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pl.edu.icm.visnow.datamaps.ColorMapManager;
import pl.edu.icm.visnow.datamaps.colormap1d.DefaultColorMap1D;
import pl.edu.icm.visnow.geometries.events.ColorEvent;
import pl.edu.icm.visnow.geometries.events.ColorListener;
import pl.edu.icm.visnow.geometries.events.ColorMapChangeListener;
import pl.edu.icm.visnow.geometries.events.ColorMapEvent;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class TransparencyDisplayPanel extends javax.swing.JPanel
{
   private float[] transp = new float[256];
   private float scale = 1.f;
   private DefaultColorMap1D cMap = ColorMapManager.getInstance().getColorMap1D(0);
   private Color bgrColor = Color.BLACK;
   private boolean renderCheckerboard = true;
   private int bgrThreshold = 0;
   private Image transpImage = null;   
   private ColorListener bgrColorListener = new ColorListener()
   {
      public void colorChoosen(ColorEvent e)
      {
         setBgrColor(e.getSelectedColor());
         repaint();
      }
   };
   private ColorMapChangeListener colorMapChangeListener = new ColorMapChangeListener()
   {
      public void ColorMapChanged(ColorMapEvent e)
      {
         cMap = e.getCMap();
         repaint();
      }
   };
   private ChangeListener immediateRepaintListener = new ChangeListener()
   {
      public void stateChanged(ChangeEvent e) 
      {
         repaint();
      }
   };
   
   /** Creates new form TransparencyDisplayPanel */
   public TransparencyDisplayPanel()
   {
      initComponents();
      addMouseListener(new java.awt.event.MouseAdapter() 
      {
         @Override
         public void mouseClicked(java.awt.event.MouseEvent evt)
         {
            renderCheckerboard = !renderCheckerboard;
            repaint();
         }
      });

   }

   public void setBgrColor(Color bgrColor)
   {
      this.bgrColor = bgrColor;
      repaint();
   }

   public void setBgrThreshold(int bgrThreshold)
   {
      this.bgrThreshold = bgrThreshold;
      repaint();
   }

   public void setcMap(DefaultColorMap1D cMap)
   {
      this.cMap = cMap;
      repaint();
   }

   public void setScale(float scale)
   {
      this.scale = scale;
      repaint();
   }

   public void setTransp(float[] transp)
   {
      this.transp = transp;
      repaint();
   }
   
   private Image makeImage()
   {
      int h, w, i, j;
      int[] pix = null;
      int red, green, blue;
      float d;
      Image img = null;

      w = this.getWidth() - 10;
      h = 16;
      if (w < 0 || h < 0)
         return null;
      if (pix == null || w * h != pix.length)
         pix = new int[w * h];
      for (i = 0; i < w * h; i++)
         pix[i] = 0;
      int index = 0;

      byte[] colorMapLUT = cMap.getRGBByteColorTable();
      int nColors = ColorMapManager.SAMPLING_TABLE - 1;
      d = (float) nColors / w;
      for (int y = 0; y < h; y++)
      {
         i = (y % 16) / 8;
         for (int x = 0; x < w; x++)
         {
            j = (x % 16) / 8;
            if (i == j)
               j = 200;
            else
               j = 80;
            int[] cColor = new int[3];
            int kk = 3 * (int) (d * x);
            if (kk < 0)
               kk = 0;
            if (kk > 3 * nColors)
               kk = 3 * nColors;
            for (int m = 0; m < 3; m++, kk++)
               cColor[m] = 0xff & colorMapLUT[kk];
            float t = scale * transp[(int) (x * d)];
            if (x * d < bgrThreshold)
               t = 0;
            if (t > 1)
               t = 1;
            t = (float) (Math.sqrt((double) t));
            if (renderCheckerboard)
            {
               red = (int) (cColor[0] * t + j * (1 - t));
               green = (int) (cColor[1] * t + j * (1 - t));
               blue = (int) (cColor[2] * t + j * (1 - t));
            } else
            {
               red = (int) (cColor[0] * t + bgrColor.getRed() * (1 - t));
               green = (int) (cColor[1] * t + bgrColor.getGreen() * (1 - t));
               blue = (int) (cColor[2] * t + bgrColor.getBlue() * (1 - t));
            }
            pix[index++] = (255 << 24) | (red << 16) | (green << 8) | blue;
         }
      }
      img = createImage(new MemoryImageSource(w, h, pix, 0, w));
      return img;
   }

   @Override
   public void paint(Graphics g)
   {
      int w = getWidth();
      int h = getHeight();
      Graphics2D gr = (Graphics2D) g;

      gr.setColor(new Color(240, 240, 240));
      gr.fillRect(0, 0, w, h);
      transpImage = makeImage();
      if (transpImage != null)
         gr.drawImage(transpImage, 0, 4, w, h - 8, null);
   }

   public ColorMapChangeListener getColorMapChangeListener()
   {
      return colorMapChangeListener;
   }
   
   public ColorListener getColorListener()
   {
      return bgrColorListener;
   }

   public ChangeListener getImmediateRepaintListener() {
      return immediateRepaintListener;
   }
   
   /** This method is called from within the constructor to
    * initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is
    * always regenerated by the Form Editor.
    */
   @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setMinimumSize(new java.awt.Dimension(150, 24));
        setPreferredSize(new java.awt.Dimension(255, 24));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 241, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 24, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
