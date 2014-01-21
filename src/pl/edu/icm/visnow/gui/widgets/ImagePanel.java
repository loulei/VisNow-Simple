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

package pl.edu.icm.visnow.gui.widgets;

import pl.edu.icm.visnow.lib.utils.ImageUtilities;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 * @author Bartosz Borucki (babor@icm.edu.pl) Warsaw University,
 * Interdisciplinary Centre for Mathematical and Computational Modelling
 */
public class ImagePanel extends JPanel
{

   protected BufferedImage img;
   protected BufferedImage showImg;
   protected int width, height;
   private float transparency = 1.0f;
   private double scale = 1.0;
   private int alphaCompositeType = AlphaComposite.SRC_OVER;
   protected boolean showName = false;
   protected boolean framed = false;
   protected Color frameColor = Color.BLUE;
   protected int interpolationType = AffineTransformOp.TYPE_NEAREST_NEIGHBOR;
   protected boolean updated = false;

   public ImagePanel()
   {
      width = 10;
      height = 10;
      this.setOpaque(false);
   }

   private void init()
   {
      //setBackground(Color.white);
   }

   @Override
   public void paintComponent(Graphics g)
   {
      Graphics2D g2 = (Graphics2D) g;
      Rectangle clear = new Rectangle(0, 0, width, height);
      g2.setComposite(AlphaComposite.getInstance(alphaCompositeType, transparency));
      //g2.setPaint(getBackground());
      g2.setPaint(new Color(0, 0, 0, 0));
      g2.fill(clear);
      if (img != null)
      {
         g2.drawImage(showImg, 0, 0, null);
      }

      if (showName)
      {
         g2.setPaint(Color.BLACK);
         g2.drawString(this.getName(), 0, 10);
      }

      if (framed)
      {
         g2.setPaint(frameColor);
         g2.drawRect(0, 0, width - 1, height - 1);
         g2.drawRect(1, 1, width - 3, height - 3);
      }



   }

   @Override
   public Dimension getPreferredSize()
   {
      if (img != null)
      {
         width = Math.max(width, (int) (img.getWidth() * scale));
         height = Math.max(height, (int) (img.getHeight() * scale));
      }
      return new Dimension(width, height);
   }

   public void clear()
   {
      Graphics2D g2 = (Graphics2D) this.getGraphics();
      Rectangle clear = new Rectangle(0, 0, width, height);
      if (g2 != null)
      {
         g2.setPaint(getBackground());
         g2.fill(clear);
      }
      img = null;
   }

   public void setImage(BufferedImage bi)
   {
      scale = 1.0;
      img = bi;
      update();
   }

   public void setImage(BufferedImage bi, double scale)
   {
      this.scale = scale;
      this.img = bi;
      update();
   }

   public void setImage(String path)
   {
      this.setImage(ImageUtilities.loadImage(path));
   }

   public BufferedImage getImage()
   {
      return img;
   }

   public void setTransparency(int aCT, double tr)
   {
      this.alphaCompositeType = aCT;
      this.transparency = (float) tr;
      repaint();
   }

   public void setShowName(boolean show)
   {
      this.showName = show;
      repaint();
   }

   public boolean getShowName()
   {
      return this.showName;
   }

   public void setFramed(boolean framed)
   {
      this.framed = framed;
      repaint();
   }

   public boolean isFramed()
   {
      return framed;
   }

   public void setFrameColor(Color frameColor)
   {
      this.frameColor = frameColor;
      repaint();
   }

   public Color getFrameColor()
   {
      return frameColor;
   }

   public double getScale()
   {
      return scale;
   }

   public void setScale(double scale)
   {
      if(scale <= 0)
          return;
      
      this.scale = scale;
      update();
   }

   private void update()
   {
      if (scale != 1.0)
      {
         showImg = ImageUtilities.resizeImage(img, scale, scale, interpolationType);
      } else
      {
         showImg = img;
      }
      width = showImg.getWidth();
      height = showImg.getHeight();
      this.setSize(width, height);
      updated = true;
      repaint();
   }

   public boolean isUpdated()
   {
      return updated;
   }

   public void setUpdated(boolean upd)
   {
      this.updated = upd;
   }
}
