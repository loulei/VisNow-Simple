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



package pl.edu.icm.visnow.gui.widgets.FloatSubRangeSlider;
/*
 * BasicFloatSubRangeSliderUI.java
 *
 * Created on April 14, 2004, 10:42 AM
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.plaf.*;
import pl.edu.icm.visnow.gui.icons.IconsContainer;
import pl.edu.icm.visnow.lib.utils.Range;
import pl.edu.icm.visnow.system.swing.VNSwingUtils;


/**
 *
 * @author Krzysztof S. Nowinski (know@icm.edu.pl)
 * Warsaw University
 * Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class BasicFloatSubRangeSliderUI extends FloatSubRangeSliderUI
implements MouseListener, MouseMotionListener
{
   protected int rWidth, rHeight;
   protected Insets d;
   protected int h = 8;
   protected int w = 6;
   protected int bottomPos;
   protected int topPos;
   protected boolean topAdj    = false;
   protected boolean bottomAdj = false;
   protected boolean rangeAdj  = false;
   protected Graphics2D g;
   protected JComponent c;
   protected float bottomValue;
   protected float topValue;
   protected float min;
   protected float max;
   protected int hPos;
   protected float startVal;
   protected float startBottomVal;
   protected float startTopVal;
   protected float scale;
   protected int decimalScale = 0;

   /** Creates a new instance of BasicFloatSubRangeSliderUI */
   public BasicFloatSubRangeSliderUI()
   {
   }

   public static ComponentUI createUI(JComponent c)
   {
      return new BasicFloatSubRangeSliderUI();
   }

   @Override
   public void installUI(JComponent c)
   {
      FloatSubRangeSlider mSlider = (FloatSubRangeSlider)c;
      mSlider.addMouseListener(this);
      mSlider.addMouseMotionListener(this);
   }

   @Override
   public void uninstallUI(JComponent c)
   {
      FloatSubRangeSlider mSlider = (FloatSubRangeSlider)c;
      mSlider.removeMouseListener(this);
      mSlider.removeMouseMotionListener(this);
   }

   @Override
   public void paint(Graphics graphic, JComponent c)
   {
      //  We don't want to paint inside the insets or borders.
      g = (Graphics2D)graphic;
      g.addRenderingHints(VNSwingUtils.getHints());
      this.c = c;
      d = c.getInsets();
      int width  = c.getWidth() - d.left - d.right;
      int height = c.getHeight() - d.top - d.bottom;
      int ht = height;
      g.setColor(c.getBackground());
      g.fillRect(0, 0, c.getWidth(), c.getHeight());
      g.translate(d.left, d.top);

      bottomValue = ((FloatSubRangeSlider)c).getBottomValue();
      topValue    = ((FloatSubRangeSlider)c).getTopValue();
      min         = ((FloatSubRangeSlider)c).getMinimum();
      max         = ((FloatSubRangeSlider)c).getMaximum();
      rWidth      = width-4*w;
      scale       = rWidth*1.f/(max-min);
      bottomPos   = (int)((bottomValue-min)*scale)+2*w;
      topPos      = (int)((topValue-min)*scale)+2*w;
      if (((FloatSubRangeSlider)c).isPaintTicks())
         ht-=7;
      if (((FloatSubRangeSlider)c).isPaintLabels())
         ht-=10;
      hPos = ht/2;
      decimalScale = ((FloatSubRangeSlider)c).getDecimalScale();
      int iScale = 1;
      for (int i=0; i<Math.abs(decimalScale); i++)
         iScale*=10;
      Range range = new Range((int)(1000.f / Math.max(width, 200)), min, max, false);

      if (((FloatSubRangeSlider)c).isPaintTicks())
      {
         int nSteps = range.getNsteps();
         float u = rWidth/(range.getRangeOrig()[1]-range.getRangeOrig()[0]);
         int xStart = (int)(u*(range.getRange()[0]-range.getRangeOrig()[0]));
         int xEnd = rWidth+(int)(u*(range.getRange()[1]-range.getRangeOrig()[1]));
         g.setColor(new Color(163,184,204));
         for (int i = 0; i<=10*nSteps; i++)
         {
            int x = xStart+(int)((float)(i*(xEnd-xStart))/(10.*nSteps));
            if (x < 0 || x > rWidth)
               continue;
            int l = 4;
            if (i%5 == 0)
               l = 6;
            g.drawLine(2*w+x, hPos+h+2,  2*w+x, hPos+h+l);
         }
         if (((FloatSubRangeSlider)c).isPaintLabels())
         {
            float xl = range.getLow();
            float step = range.getStep();
            int stepMag = (int)(Math.log10((double)step)+100)-100;
            String format = "%4.0f";
            if (stepMag>4)
               format = "%6.0e";
            if (stepMag<0)
            {
               if (stepMag>-5)
                  format = "%6."+(-stepMag)+"f";
               else
                  format = "6.5e";
            }
            g.setColor(Color.darkGray);
            for (int i = 0; i<=nSteps; i++)
            {
               int x = xStart+(int)((float)(i*(xEnd-xStart))/(float)nSteps);
               if (x<0 || x>0+rWidth)
                  continue;
               g.drawString(String.format(format, xl+i*step),2*w+x-15, hPos+h+15);

            }
         }
      }
      if (((FloatSubRangeSlider)c).isEnabled())
      {
         g.setColor(new Color(122,138,153));
         g.drawRect(2*w,   hPos-3 , rWidth, 5);
         g.setColor(new Color(163,184,204));
         g.drawLine(2*w+1, hPos-2 , rWidth+2*w-2, hPos-2);
         g.setColor(new Color(238,238,238));
         g.fillRect(2*w+1, hPos-1 , rWidth-2, 3);
         g.setColor(new Color(99,130,191));
         g.drawLine(bottomPos-6,hPos-3,topPos+1,hPos-3);
         g.drawLine(bottomPos-6,hPos+2,topPos+1,hPos+2);
         g.setColor(Color.WHITE);
         g.drawLine(bottomPos-6,hPos-2,topPos+1,hPos-2);
         g.setColor(new Color(210,226,239));
         g.drawLine(bottomPos-6,hPos-1,topPos+1,hPos-1);
         g.setColor(new Color(184,207,229));
         g.drawLine(bottomPos-6,hPos,topPos+1,hPos);
         g.setColor(new Color(163,184,204));
         g.drawLine(bottomPos-6,hPos+1,topPos+1,hPos+1);
         g.setColor(Color.lightGray);
         drawIndex(bottomPos - 1,  -1, ((FloatSubRangeSlider)c).isEnabled());
         drawIndex(topPos + 1, 1, ((FloatSubRangeSlider) c).isEnabled());
      }
      else
      {
         g.setColor(Color.LIGHT_GRAY);
         g.drawRect(2*w,   hPos-3 , rWidth, 6);
         g.drawLine(2*w+1, hPos-2 , rWidth+2*w-2, hPos-2);
         g.setColor(Color.white);
         g.drawLine(2*w+1, hPos+4 , rWidth+2*w-2, hPos+4);
         g.setColor(new Color(210,210,255));
         g.fillRect(bottomPos-6,hPos-2,topPos-bottomPos+12,3);
         g.setColor(new Color(222,222,255));
         g.drawLine(bottomPos-1,hPos+2,topPos+1,hPos+2);
         g.setColor(Color.lightGray);
         drawDisabledIndex(bottomPos - 1,  -1, ((FloatSubRangeSlider)c).isEnabled());
         drawDisabledIndex(topPos + 1, 1, ((FloatSubRangeSlider) c).isEnabled());
      }
   }

   private void drawIndex(int pos, int orientation, boolean enabled)
   {
      if (orientation == 1)
         g.drawImage(IconsContainer.getIndexR(), pos, hPos+h-16, null);
      else
         g.drawImage(IconsContainer.getIndexL(), pos - 8, hPos+h-16, null);
   }

   private void drawDisabledIndex(int pos, int orientation, boolean enabled)
   {
      if (orientation == 1)
         g.drawImage(IconsContainer.getIndexRDisabled(), pos, hPos+h-16, null);
      else
         g.drawImage(IconsContainer.getIndexLDisabled(), pos - 8, hPos+h-16, null);
   }

   public void mouseClicked(MouseEvent e)
   {
   }

   public void mouseDragged(MouseEvent e)
   {
      FloatSubRangeSlider theSlider = (FloatSubRangeSlider)e.getComponent();
      if (!theSlider.isEnabled())
         return;
      theSlider.setAdjusting(true);
      float val = ((e.getX()-w-d.left)/scale+min)-startVal;
      if (topAdj)
         theSlider.setTopValue(startTopVal+val,false);
      if (bottomAdj)
         theSlider.setBottomValue(startBottomVal+val,false);
      if (rangeAdj)
      {
         theSlider.setTopValue(startTopVal+val,false);
         theSlider.setBottomValue(startBottomVal+val,false);
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
      FloatSubRangeSlider theSlider = (FloatSubRangeSlider)e.getComponent();
      if (!theSlider.isEnabled())
         return;
      topAdj=bottomAdj=rangeAdj=false;
      int x = e.getX();
      int y = e.getY();
      if (y>hPos+h+5+d.top || y<hPos-h+d.top)
         return;
      if (e.getButton()==MouseEvent.BUTTON1)
      {
         if (x>=topPos+d.left-1 && x<=topPos+2+w+d.left)
            topAdj=true;
         if (x>=bottomPos-w+d.left-2 && x<=bottomPos+d.left+1)
            bottomAdj=true;
         if (x>=bottomPos-d.left+2 && x<=topPos+d.left-2 &&
             y>hPos-3 && y<hPos+3    )
            rangeAdj = true;
      }
      else
         if (x>=bottomPos-d.left && x<=topPos+d.left)
            rangeAdj = true;
      startVal = (x-w-d.left)/scale+min;
      startTopVal = theSlider.getTopValue();
      startBottomVal = theSlider.getBottomValue();
   }

   public void mouseReleased(MouseEvent e)
   {
      FloatSubRangeSlider theSlider = (FloatSubRangeSlider)e.getComponent();
      if (!theSlider.isEnabled())
         return;
      theSlider.setAdjusting(false);
      float val = (e.getX()-w-d.left)/scale+min-startVal;
      if (topAdj)
         theSlider.setTopValue(startTopVal+val,false, true);
      if (bottomAdj)
         theSlider.setBottomValue(startBottomVal+val,false, true);
      if (rangeAdj)
      {
         theSlider.setTopValue(startTopVal+val,false, true);
         theSlider.setBottomValue(startBottomVal+val,false, true);
      }
      topAdj=bottomAdj=rangeAdj=false;
   }
}
