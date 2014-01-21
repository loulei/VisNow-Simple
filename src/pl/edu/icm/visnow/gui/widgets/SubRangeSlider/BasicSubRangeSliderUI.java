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


package pl.edu.icm.visnow.gui.widgets.SubRangeSlider;
/**
 * @author  Krzysztof S. Nowinski (know@icm.edu.pl)
 * Warsaw University, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */

import java.awt.*;
import java.awt.event.*;
import java.util.Map;
import javax.swing.*;
import javax.swing.plaf.*;
import pl.edu.icm.visnow.gui.icons.IconsContainer;
import pl.edu.icm.visnow.system.swing.VNSwingUtils;

/**
 *
 * @author Krzysztof S. Nowinski (know@icm.edu.pl)
 * Warsaw University
 * Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class BasicSubRangeSliderUI extends SubRangeSliderUI
        implements MouseListener, MouseMotionListener
{

   int rWidth, rHeight;
   Insets d;
   int h = 8;
   int w = 6;
   int bottomPos;
   int topPos;
   boolean topAdj = false;
   boolean bottomAdj = false;
   boolean rangeAdj = false;
   Graphics2D g;
   JComponent c;
   int bottomValue;
   int topValue;
   int min;
   int max;
   int hPos;
   int startVal;
   int startBottomVal;
   int startTopVal;
   float scale;
   int decimalScale = 0;
   Map<Integer,String> valLabels = null;

   /** Creates a new instance of BasicSubRangeSliderUI */
   public BasicSubRangeSliderUI()
   {
   }

   public static ComponentUI createUI(JComponent c)
   {
      return new BasicSubRangeSliderUI();
   }

   @Override
   public void installUI(JComponent c)
   {
      SubRangeSlider mSlider = (SubRangeSlider) c;
      mSlider.addMouseListener(this);
      mSlider.addMouseMotionListener(this);
   }

   @Override
   public void uninstallUI(JComponent c)
   {
      SubRangeSlider mSlider = (SubRangeSlider) c;
      mSlider.removeMouseListener(this);
      mSlider.removeMouseMotionListener(this);
   }

   @Override
   public void paint(Graphics graphic, JComponent c)
   {
      //  We don't want to paint inside the insets or borders.
      g = (Graphics2D) graphic;
      g.addRenderingHints(VNSwingUtils.getHints());
      this.c = c;
      d = c.getInsets();
      int width = c.getWidth() - d.left - d.right;
      int height = c.getHeight() - d.top - d.bottom;
      int ht = height;
      g.setColor(c.getBackground());
      g.fillRect(0, 0, c.getWidth(), c.getHeight());
      g.translate(d.left, d.top);
      valLabels = ((SubRangeSlider) c).getValLabels();
      bottomValue = ((SubRangeSlider) c).getBottomValue();
      topValue = ((SubRangeSlider) c).getTopValue();
      min = ((SubRangeSlider) c).getMinimum();
      max = ((SubRangeSlider) c).getMaximum();
      rWidth = width - 4 * w;
      scale = rWidth * 1.f / (max - min);
      bottomPos = (int) ((float) (bottomValue - min) * scale) + 2 * w;
      topPos = (int) ((float) (topValue - min) * scale) + 2 * w;
      if (((SubRangeSlider) c).isPaintTicks())   ht -= 7;
      if (((SubRangeSlider) c).isPaintLabels())  ht -= 12;
      hPos = ht / 2;
      decimalScale = ((SubRangeSlider) c).getDecimalScale();
      int iScale = 1;
      for (int i = 0; i < Math.abs(decimalScale); i++)
         iScale *= 10;

      if (((SubRangeSlider) c).isPaintTicks())
      {
         double tx = (max - min) * 1000. / c.getWidth();
         double stx;
         int nx = (int) (Math.floor(Math.log(tx) / Math.log(10.)));
         tx /= Math.pow(10., 1. * nx);
         if (tx < 2.)
         {
            tx = 1.;
            stx = .2;
         } else
         {
            if (tx < 5.)
            {
               tx = 2.;
               stx = .5;
            } else
            {
               tx = 5.;
               stx = 1.;
            }
         }
         tx *= Math.pow(10., 1. * nx) / 10.;
         stx *= Math.pow(10., 1. * nx) / 10.;
         int step = (int) tx;
         if (step < 1)
            step = 1;
         int sstep = (int) stx;
         if (sstep < 1)
            sstep = 1;

         int xl = (min / step);
         xl *= step;
         if (xl < min)  xl += step;
         int xu = (max / step);
         xu *= step;
         if (xu > max)  xu -= step;
         g.setColor(new Color(163, 184, 204));
         for (int x = xl; x <= xu; x += step)
         {
            int xr = (int) ((float) (x - min) * scale) + 2 * w;
            g.drawLine(xr, hPos + h + 2, xr, hPos + h + 5);
         }
         if (((SubRangeSlider) c).isPaintLabels())
         {
            g.setColor(Color.darkGray);
            if (valLabels == null)
               for (int x = xl; x <= xu; x += step)
               {
                  int ix = x;
                  if (decimalScale < 0)  ix *= iScale;
                  if (decimalScale > 0)  ix /= iScale;
                  int xr = (int) ((float) (x - min) * scale + 2 * w - 2 * Math.log(Math.abs(x) + 1.));
                  g.drawString("" + ix, xr, hPos + h + 15);
               }
            else
               for (Integer n : valLabels.keySet())
               {
                  String s = valLabels.get(n);
                  float xr =  ((float) (n - min) * scale + 2 * w) - 3 * s.length();
                  g.drawString(valLabels.get(n),  xr, hPos + h + 15);
               }
         }
         xl = (min / sstep);
         xl *= sstep;
         if (xl < min)
         {
            xl += sstep;
         }
         xu = (max / sstep);
         xu *= sstep;
         if (xu > max)
         {
            xu -= sstep;
         }
         g.setColor(new Color(153, 153, 204));
         for (int x = xl; x <= xu; x += sstep)
         {
            int xr = (int) ((float) (x - min) * scale) + 2 * w;
            g.drawLine(xr, hPos + h + 2, xr, hPos + h + 8);
         }
      }
      if (((SubRangeSlider) c).isEnabled())
      {
         g.setColor(new Color(122, 138, 153));
         g.drawRect(2 * w, hPos - 3, rWidth, 5);
         g.setColor(new Color(163, 184, 204));
         g.drawLine(2 * w + 1, hPos - 2, rWidth + 2 * w - 2, hPos - 2);
         g.setColor(new Color(238, 238, 238));
         g.fillRect(2 * w + 1, hPos - 1, rWidth - 2, 3);
         g.setColor(new Color(99, 130, 191));
         g.drawLine(bottomPos - 6, hPos - 3, topPos + 1, hPos - 3);
         g.drawLine(bottomPos - 6, hPos + 2, topPos + 1, hPos + 2);
         g.setColor(Color.WHITE);
         g.drawLine(bottomPos - 6, hPos - 2, topPos + 1, hPos - 2);
         g.setColor(new Color(210, 226, 239));
         g.drawLine(bottomPos - 6, hPos - 1, topPos + 1, hPos - 1);
         g.setColor(new Color(184, 207, 229));
         g.drawLine(bottomPos - 6, hPos, topPos + 1, hPos);
         g.setColor(new Color(163, 184, 204));
         g.drawLine(bottomPos - 6, hPos + 1, topPos + 1, hPos + 1);
         g.setColor(Color.lightGray);
         drawIndex(bottomPos - 1, -1, ((SubRangeSlider) c).isEnabled());
         drawIndex(topPos + 1, 1, ((SubRangeSlider) c).isEnabled());
      } else
      {
         g.setColor(Color.LIGHT_GRAY);
         g.drawRect(2 * w, hPos - 3, rWidth, 6);
         g.drawLine(2 * w + 1, hPos - 2, rWidth + 2 * w - 2, hPos - 2);
         g.setColor(Color.white);
         g.drawLine(2 * w + 1, hPos + 4, rWidth + 2 * w - 2, hPos + 4);
         g.setColor(new Color(210, 210, 255));
         g.fillRect(bottomPos - 6, hPos - 2, topPos - bottomPos + 12, 3);
         g.setColor(new Color(222, 222, 255));
         g.drawLine(bottomPos - 1, hPos + 2, topPos + 1, hPos + 2);
         g.setColor(Color.lightGray);
         drawDisabledIndex(bottomPos - 1, -1, ((SubRangeSlider) c).isEnabled());
         drawDisabledIndex(topPos + 1, 1, ((SubRangeSlider) c).isEnabled());
      }
   }

   private void drawIndex(int pos, int orientation, boolean enabled)
   {
      if (orientation == 1)
         g.drawImage(IconsContainer.getIndexR(), pos, hPos + h - 16, null);
      else
         g.drawImage(IconsContainer.getIndexL(), pos - 8, hPos + h - 16, null);

   }

   private void drawDisabledIndex(int pos, int orientation, boolean enabled)
   {
      if (orientation == 1)
         g.drawImage(IconsContainer.getIndexRDisabled(), pos, hPos + h - 16, null);
      else
         g.drawImage(IconsContainer.getIndexLDisabled(), pos - 8, hPos + h - 16, null);
   }

   public void mouseClicked(MouseEvent e)
   {
   }

   public void mouseDragged(MouseEvent e)
   {
      SubRangeSlider theSlider = (SubRangeSlider) e.getComponent();
      if (!theSlider.isEnabled())
      {
         return;
      }
      theSlider.setAdjusting(true);
      int val = (int) ((e.getX() - w - d.left) / scale + min) - startVal;
      if (topAdj)
      {
         theSlider.setTopValue(startTopVal + val, false);
      }
      if (bottomAdj)
      {
         theSlider.setBottomValue(startBottomVal + val, false);
      }
      if (rangeAdj)
      {
         theSlider.setTopValue(startTopVal + val, false);
         theSlider.setBottomValue(startBottomVal + val, false);
      }
   }

   public void mouseEntered(MouseEvent e)
   {
   }

   public void mouseExited(MouseEvent e)
   {
   }

   public void mouseMoved(MouseEvent e)
   {
   }

   public void mousePressed(MouseEvent e)
   {
      SubRangeSlider theSlider = (SubRangeSlider) e.getComponent();
      if (!theSlider.isEnabled())
      {
         return;
      }
      topAdj = bottomAdj = rangeAdj = false;
      int x = e.getX();
      int y = e.getY();
      if (y > hPos + h + 5 + d.top || y < hPos - h + d.top)
      {
         return;
      }
      if (e.getButton() == e.BUTTON1)
      {
         if (x >= topPos + d.left - 1 && x <= topPos + 2 + w + d.left)
         {
            topAdj = true;
         }
         else if (x >= bottomPos - w + d.left - 2 && x <= bottomPos + d.left + 4)
//         if (x >= bottomPos + d.left - 2 && x <= bottomPos + d.left + 4)
         {
            bottomAdj = true;
         }
         else if (x >= bottomPos - d.left + 2 && x <= topPos + d.left - 2
                 && y > hPos - 6 && y < hPos + 6)
         {
            rangeAdj = true;
         }
      } else if (x >= bottomPos - d.left && x <= topPos + d.left)
      {
         rangeAdj = true;
      }
      startVal = (int) ((x - w - d.left) / scale + min);
      startTopVal = theSlider.getTopValue();
      startBottomVal = theSlider.getBottomValue();
   }

   public void mouseReleased(MouseEvent e)
   {
      SubRangeSlider theSlider = (SubRangeSlider) e.getComponent();
      if (!theSlider.isEnabled())
      {
         return;
      }
      theSlider.setAdjusting(false);
      int val = (int) ((e.getX() - w - d.left) / scale + min) - startVal;
      if (topAdj)
      {
         theSlider.setTopValue(startTopVal + val, false, true);
      }
      if (bottomAdj)
      {
         theSlider.setBottomValue(startBottomVal + val, false, true);
      }
      if (rangeAdj)
      {
         theSlider.setTopValue(startTopVal + val, false, true);
         theSlider.setBottomValue(startBottomVal + val, false, true);
      }
      topAdj = bottomAdj = rangeAdj = false;
   }
}
