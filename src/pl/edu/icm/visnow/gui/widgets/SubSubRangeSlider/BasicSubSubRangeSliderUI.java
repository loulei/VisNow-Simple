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

package pl.edu.icm.visnow.gui.widgets.SubSubRangeSlider;
/*
 * BasicSubSubRangeSliderUI.java
 *
 * Created on August 8, 2008, 10:42 AM
 */
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.plaf.*;


/**
 *
 * @author Bartosz Borucki (babor@icm.edu.pl)
 * Warsaw University
 * Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class BasicSubSubRangeSliderUI extends SubSubRangeSliderUI
implements MouseListener, MouseMotionListener
{
   int rWidth, rHeight;
   Insets d;
   int h = 6;
   int w = 6;
   int bottomPos;
   int topPos;
   int centerPos;
   boolean topAdj    = false;
   boolean bottomAdj = false;
   boolean rangeAdj  = false;
   boolean centerAdj = false;
   Graphics g;
   JComponent c;
   int bottomValue;
   int topValue;
   int centerValue;
   int min;
   int max;
   int hPos;
   int startVal;
   int startBottomVal;
   int startTopVal;
   int startCenterVal;
   float scale;
   int decimalScale = 0;
   
   /** Creates a new instance of BasicSubSubRangeSliderUI */
   public BasicSubSubRangeSliderUI()
   {
   }
   
   public static ComponentUI createUI(JComponent c)
   {
      return new BasicSubSubRangeSliderUI();
   }
   
   @Override
   public void installUI(JComponent c)
   {
      SubSubRangeSlider mSlider = (SubSubRangeSlider)c;
      mSlider.addMouseListener(this);
      mSlider.addMouseMotionListener(this);
   }
   
   @Override
   public void uninstallUI(JComponent c)
   {
      SubSubRangeSlider mSlider = (SubSubRangeSlider)c;
      mSlider.removeMouseListener(this);
      mSlider.removeMouseMotionListener(this);
   }
   
   @Override
   public void paint(Graphics g, JComponent c)
   {
      //  We don't want to paint inside the insets or borders.
      this.g = g;
      this.c = c;
      d = c.getInsets();
      int width  = c.getWidth() - d.left - d.right;
      int height = c.getHeight() - d.top - d.bottom;
      int ht = height;
      g.setColor(c.getBackground());
      g.fillRect(0, 0, c.getWidth(), c.getHeight());
      g.translate(d.left, d.top);
      
      bottomValue = ((SubSubRangeSlider)c).getBottomValue();
      topValue    = ((SubSubRangeSlider)c).getTopValue();
      centerValue = ((SubSubRangeSlider)c).getCenterValue();
      min         = ((SubSubRangeSlider)c).getMinimum();
      max         = ((SubSubRangeSlider)c).getMaximum();
      rWidth      = width-4*w;
      scale       = rWidth*1.f/(max-min);
      bottomPos   = (int)((float)(bottomValue-min)*scale)+2*w;
      topPos      = (int)((float)(topValue-min)*scale)+2*w;
      centerPos   = (int)((float)(centerValue-min)*scale)+2*w;
      
      if (((SubSubRangeSlider)c).isPaintTicks()) 
         ht-=7;
      if (((SubSubRangeSlider)c).isPaintLabels())
         ht-=12;
      hPos = ht/2;
      decimalScale = ((SubSubRangeSlider)c).getDecimalScale();
      int iScale = 1;
      for (int i=0; i<Math.abs(decimalScale); i++)
         iScale*=10;
      
      if (((SubSubRangeSlider)c).isPaintTicks())
      {
         double tx = (max-min)*1000./c.getWidth();
         double stx;
         int nx = (int)(Math.floor(Math.log(tx)/Math.log(10.))); 
         tx /= Math.pow(10.,1.*nx);
         if (tx<2.) 
         {
            tx = 1.;
            stx = .2;
         }
         else
         {
            if (tx<5.) 
            {
               tx = 2.;
               stx = .5;
            }
            else       
            {
               tx = 5.;
               stx = 1.;
            }
         }
         tx *= Math.pow(10.,1.*nx)/10.;
         stx *= Math.pow(10.,1.*nx)/10.;
         int step = (int) tx;
         if (step<1) step=1;         
         int sstep = (int) stx;
         if (sstep<1) sstep=1;         
         
         int xl = (min/step);
         xl *= step;
         if (xl<min) xl+=step;
         int xu = (max/step);
         xu *= step;
         if (xu>max) xu-=step;
         g.setColor(new Color(153,153,204));
         for (int x=xl; x<=xu; x+=step)
         {
            int xr = (int)((float)(x-min)*scale)+2*w;
            g.drawLine(xr, hPos+h+6,  xr, hPos+h+12);
         }
         if (((SubSubRangeSlider)c).isPaintLabels())
         {
            g.setColor(Color.darkGray);
            for (int x=xl; x<=xu; x+=step)
            {
               int ix = x;
               if (decimalScale<0) ix*=iScale;
               if (decimalScale>0) ix/=iScale;
               int xr = (int)((float)(x-min)*scale+2*w-2*Math.log(Math.abs(x)+1.));
               g.drawString(""+ix,xr, hPos+h+28);

            }
         }
         xl = (min/sstep);
         xl *= sstep;
         if (xl<min) xl+=sstep;
         xu = (max/sstep);
         xu *= sstep;
         if (xu>max) xu-=sstep;
         g.setColor(new Color(153,153,204));
         for (int x=xl; x<=xu; x+=sstep)
         {
            int xr = (int)((float)(x-min)*scale)+2*w;
            g.drawLine(xr, hPos+h+6,  xr, hPos+h+9);
         }        
      }
      if (((SubSubRangeSlider)c).isEnabled())
      {
         g.setColor(Color.gray);
         g.drawRect(2*w,   hPos-3 , rWidth, 6);
         g.drawLine(2*w+1, hPos-2 , rWidth+2*w-2, hPos-2);
         g.setColor(Color.white);
         g.drawLine(2*w+1, hPos+4 , rWidth+2*w-2, hPos+4);
         g.setColor(new Color(210,210,255));
         g.fillRect(bottomPos-6,hPos-2,topPos-bottomPos+12,3);      
         g.setColor(new Color(222,222,255));
         g.drawLine(bottomPos-1,hPos+2,topPos+1,hPos+2);
         g.setColor(Color.lightGray);
      }
      else
      {
         g.setColor(Color.lightGray);
         g.drawRect(2*w,   hPos-3 , rWidth, 6);
         g.drawLine(2*w+1, hPos-2 , rWidth+2*w-2, hPos-2);
         g.setColor(Color.white);
         g.drawLine(2*w+1, hPos+4 , rWidth+2*w-2, hPos+4);
         g.setColor(new Color(230,230,240));
         g.fillRect(bottomPos-6,hPos-2,topPos-bottomPos+12,4);      
         g.setColor(Color.lightGray);
         g.drawLine(bottomPos-1,hPos+2,topPos+1,hPos+2);
         g.setColor(Color.lightGray);
      }
//      g.drawLine(bottomPos-1, -hPos - 3, topPos + 1, hPos - 3);
      drawIndex(bottomPos - 1,  -1, ((SubSubRangeSlider)c).isEnabled());
      drawIndex(topPos + 1, 1, ((SubSubRangeSlider) c).isEnabled());   
      drawCenterIndex(centerPos, ((SubSubRangeSlider) c).isEnabled());   
   }
   
   private void drawIndex(int pos, int orientation, boolean enabled)
   {
      int [] x = new int[4];
      int [] y = new int[4];
      x[0]=x[1]=pos;
      x[2]=x[3]=pos+orientation*w;
      y[0]=hPos+h+5;
      y[1]=y[2]=hPos-h+2;
      y[3]=hPos+4;
      Polygon p = new Polygon(x,y,4);
      if (enabled)
         g.setColor(new Color(180,180,204));
      else
         g.setColor(new Color(230,230,240));
      g.fillPolygon(p);
      g.drawPolygon(p);
      if (orientation==1)
      {
         g.setColor(Color.white);      
         g.drawLine(x[0]-1,y[0]-1,x[1]-1,y[1]-1);
         g.drawLine(x[1]-1,y[1]-1,x[2]-1,y[2]-1);
         if (enabled)
            g.setColor(Color.darkGray);
         else
            g.setColor(Color.lightGray);
         g.drawLine(x[2]+1,y[2]+1,x[3]+1,y[3]+1);
         
      }
      else
      {
         g.setColor(Color.white);      
         g.drawLine(x[1]-1,y[1]-1,x[2]-1,y[2]-1);
         g.drawLine(x[2]-1,y[2]-1,x[3]-1,y[3]-1);
         if (enabled)
            g.setColor(Color.darkGray);
         else
            g.setColor(Color.lightGray);
         g.drawLine(x[0]+1,y[0]+1,x[1]+1,y[1]+1);
         
      }
//      g.drawLine(x[3] + 1, y[3] + 1, x[0] + 1, y[0] + 1);
   }

   private void drawCenterIndex(int pos, boolean enabled)
   {
      int [] x = new int[5];
      int [] y = new int[5];
      x[0]=x[1]=pos-w/2-1;
      x[3]=x[4]=pos+w/2+1;
      x[2]=pos;
      y[0]=y[4]=hPos-5-12;
      y[1]=y[3]=hPos-5-4;
      y[2]=hPos-5;
      
      Polygon p = new Polygon(x,y,5);
      if (enabled)
         g.setColor(new Color(180,180,204));
      else
         g.setColor(new Color(230,230,240));
      g.fillPolygon(p);
      g.drawPolygon(p);
      g.drawLine(x[2], y[2], x[2], hPos+5);

      g.setColor(Color.white);      
      g.drawLine(x[0]-1,y[0],x[1]-1,y[1]);
      g.drawLine(x[1]-1,y[1],x[2]-1,y[2]);
      if (enabled)
            g.setColor(Color.darkGray);
      else
            g.setColor(Color.lightGray);
      g.drawLine(x[2]+1,y[2],x[3]+1,y[3]);
      g.drawLine(x[3]+1,y[3],x[4]+1,y[4]);
   }
   
   
   public void mouseClicked(MouseEvent e)
   {
   }
   
   public void mouseDragged(MouseEvent e)
   {
      SubSubRangeSlider theSlider = (SubSubRangeSlider)e.getComponent();
      if (!theSlider.isEnabled())
         return;
      theSlider.setAdjusting(true);
      int val = (int)((e.getX()-w-d.left)/scale+min)-startVal;
      if (topAdj)
         theSlider.setTopValue(startTopVal+val,false);
      if (bottomAdj)
         theSlider.setBottomValue(startBottomVal+val,false);
      if(centerAdj)
         theSlider.setCenterValue(startCenterVal+val,false);
      if (rangeAdj)
      {
         theSlider.setTopValue(startTopVal+val,false);
         theSlider.setBottomValue(startBottomVal+val,false);
         theSlider.setCenterValue(startCenterVal+val,false);
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
      SubSubRangeSlider theSlider = (SubSubRangeSlider)e.getComponent();
      if (!theSlider.isEnabled())
         return;
      topAdj=bottomAdj=rangeAdj=centerAdj=false;
      int x = e.getX();
      int y = e.getY();
      if (y>hPos+h+5+d.top || y<hPos-5-12+d.top)
         return;
      if (e.getButton()==e.BUTTON1)
      {
         if (x>=topPos+d.left-1 && x<=topPos+2+w+d.left && y>hPos-5+d.top && y<hPos+h+5+d.top)
            topAdj=true;
         if (x>=bottomPos-w+d.left-2 && x<=bottomPos+d.left+1 && y>hPos-5+d.top && y<hPos+h+5+d.top)
            bottomAdj=true;
         if (x>=centerPos-w/2-1+d.left && x<=centerPos+w/2+1+d.left && y<hPos-5+d.top && y>hPos-5-11+d.top) {
            centerAdj=true;
         }
         if (x>=bottomPos-d.left+2 && x<=topPos+d.left-2 &&  y>hPos-3 && y<hPos+3)
            rangeAdj = true;
      }
      else
         if (x>=bottomPos-d.left && x<=topPos+d.left)
            rangeAdj = true;
      startVal = (int)((x-w-d.left)/scale+min);
      startTopVal = theSlider.getTopValue();
      startBottomVal = theSlider.getBottomValue();
      startCenterVal = theSlider.getCenterValue();
   }
   
   public void mouseReleased(MouseEvent e)
   {
      SubSubRangeSlider theSlider = (SubSubRangeSlider)e.getComponent();
      if (!theSlider.isEnabled())
         return;
      theSlider.setAdjusting(false);
      int val = (int)((e.getX()-w-d.left)/scale+min)-startVal;
      if (topAdj)
         theSlider.setTopValue(startTopVal+val,false, true);
      if (bottomAdj)
         theSlider.setBottomValue(startBottomVal+val,false, true);
      if (centerAdj)
         theSlider.setCenterValue(startCenterVal+val,false, true);
      if (rangeAdj)
      {
         theSlider.setTopValue(startTopVal+val,false, true);
         theSlider.setBottomValue(startBottomVal+val,false, true);
         theSlider.setCenterValue(startCenterVal+val,false, true);
      }
      topAdj=bottomAdj=rangeAdj=centerAdj=false;
   }
}
