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

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class HistogramGraphPanel extends javax.swing.JPanel
{

   private float[] transp = new float[256];
   private int bgrThreshold = 0;
   private float[] bgHist = new float[256];
   private float minval = 0;
   private float maxval = 1;
   private float scale = .5f;
   private int w = 200;
   private int h;
   private int lastx = 0;
   private float lasty = 0;
   private boolean logHist = true;
   private float lastShiftPressX = -1;
   private float lastShiftPressY = -1;
   protected float min = 0.f;
   protected float max = 1.f;
   protected float val = .5f;
   protected float rMin = 0.f;
   protected float rMax = 1.f;
   protected float delta = .001f;
   protected int l = 0;
   protected int u = 1000;
   protected int ld = 20;
   protected int dec = 4;
   protected int ddec = 3;
   protected int lScale;
   protected float sMin, sMax;
   protected String form = "%" + dec + "." + ddec + "f";
   protected String lform = "%" + (dec - 1) + "." + (ddec - 1) + "f";
   protected Font textFont = new java.awt.Font("Dialog", 0, 10);
   protected String compName = "value";
   protected FontMetrics fm = new JLabel().getFontMetrics(textFont);

   /** Creates new form HistogramGraphPanel */
   public HistogramGraphPanel()
   {
      initComponents();
      w = getWidth();
      h = getHeight();
      roundMinMax();
      for (int i = 0; i < 256; i++)
      {
         transp[i] = .0039f * i;
         bgHist[i] = (float)Math.exp(i * (255 - i)/1000.);
      }
      addMouseMotionListener(new java.awt.event.MouseMotionAdapter()
      {
         @Override
         public void mouseDragged(java.awt.event.MouseEvent evt)
         {
            // Add your handling code here:
            if ((evt.getModifiers() & MouseEvent.BUTTON1_MASK) == 0)
               return;
            int x = (int) ((255.0F * evt.getX()) / w);
            float y = 1.0F - evt.getY() / (float) h;
            if (x < 0)
               x = 0;
            if (x > 255)
               x = 255;
            if (y < 0)
               y = 0;
            if (y > 1)
               y = 1;
            if ((evt.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) == 0)
            {
               float s = (y - lasty) / (x - lastx);
               if (x > lastx)
                  for (int i = lastx; i <= x; i++)
                     transp[i] = lasty + (i - lastx) * s;
               else if (x < lastx)
                  for (int i = x; i <= lastx; i++)
                     transp[i] = lasty + (i - lastx) * s;
               lastx = x;
               lasty = y;
            }
            else if (lastShiftPressX >= 0)
            {
               float s = (y - lastShiftPressY) / (x - lastShiftPressX);
               if (x > lastShiftPressX)
                  for (int i = (int)lastShiftPressX; i <= x; i++)
                     transp[i] = lastShiftPressY + (i - lastShiftPressX) * s;
               else if (x < lastShiftPressX)
                  for (int i = x; i <= lastShiftPressX; i++)
                     transp[i] = lastShiftPressY + (i - lastShiftPressX) * s;
            }
            repaint();
            if (immmediateChangeListener != null)
               immmediateChangeListener.stateChanged(new ChangeEvent(this));
         }
      });

      addMouseListener(new java.awt.event.MouseAdapter()
      {
         @Override
         public void mousePressed(java.awt.event.MouseEvent evt)
         {
            if ((evt.getModifiers() & MouseEvent.BUTTON1_MASK) == 0)
               return;
            if ((evt.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) == 0)
            {
               lastx = (int) ((255.0F * evt.getX()) / w);
               if (lastx < 0)
                  lastx = 0;
               if (lastx > 255)
                  lastx = 255;
               lasty = 1.0F - evt.getY() / (float) h;
               if (lasty < 0)
                  lasty = 0.0F;
               if (lasty > 1)
                  lasty = 1.0F;
               lastShiftPressX = -1;
               lastShiftPressY = -1;
            }
            else
            {
               lastShiftPressX = (int) ((255.0F * evt.getX()) / w);
               if (lastShiftPressX < 0)
                  lastShiftPressX = 0;
               if (lastShiftPressX > 255)
                  lastShiftPressX = 255;
               lastShiftPressY = 1.0F - evt.getY() / (float) h;
               if (lastShiftPressY < 0)
                  lastShiftPressY = 0.0F;
               if (lastShiftPressY > 1)
                  lastShiftPressY = 1.0F;
            }
         }

         @Override
         public void mouseReleased(java.awt.event.MouseEvent evt)
         {
            if ((evt.getModifiers() & MouseEvent.BUTTON1_MASK) == 0)
               return;
            if (transparencyListener != null)
               transparencyListener.stateChanged(new ChangeEvent(this));
            if ((evt.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) != 0)
            {
               lastShiftPressX = (int) ((255.0F * evt.getX()) / w);
               if (lastShiftPressX < 0)
                  lastShiftPressX = 0;
               if (lastShiftPressX > 255)
                  lastShiftPressX = 255;
               lastShiftPressY = 1.0F - evt.getY() / (float) h;
               if (lastShiftPressY < 0)
                  lastShiftPressY = 0.0F;
               if (lastShiftPressY > 1)
                  lastShiftPressY = 1.0F;
            }
         }
         
         
         @Override
         public void mouseClicked(java.awt.event.MouseEvent evt)
         {
            if ((evt.getModifiers() & MouseEvent.BUTTON3_MASK) != 0)
                smoothTransparency();
            else if ((evt.getModifiers() & MouseEvent.BUTTON2_MASK) != 0)
               logHist = !logHist;
            else
               transp[(int) ((255.0F * evt.getX()) / w)] = 1.0F - evt.getY() / (float) h;
            repaint(); 
            if (immmediateChangeListener != null)
               immmediateChangeListener.stateChanged(new ChangeEvent(this));
            if (transparencyListener != null)
               transparencyListener.stateChanged(new ChangeEvent(this)); 
         }
      });
   }

   public void smoothTransparency()
   {
      int n = transp.length - 1;
      float[] t = new float[transp.length];
      for (int i = 2; i < transp.length - 2; i ++)
      {
         t[i] = (transp[i - 2] + 2 * transp[i - 1] + 4 * transp[i]  + 2 * transp[i + 1] + transp[i + 2]) / 10;
      }
      t[0]     = (4 * transp[0] + 2 * transp[1] +         transp[2]) / 7;
      t[1]     = (2 * transp[0] + 4 * transp[1] +     2 * transp[2] +     transp[3]) / 9;
      t[n - 1] = (2 * transp[n] + 4 * transp[n - 1] + 2 * transp[n - 2] + transp[n - 3]) / 9;
      t[n]     = (4 * transp[n] + 2 * transp[n - 1] +     transp[n - 2]) / 7;
      System.arraycopy(t, 0, transp, 0, transp.length);
   }
         
   public void setBgHist(float[] bgHist)
   {
      this.bgHist = bgHist;
      repaint();
   }

   public void setBgrThreshold(int bgrThreshold)
   {
      this.bgrThreshold = bgrThreshold;
      repaint();
   }

   public void setTransp(float[] transp)
   {
      this.transp = transp;
      repaint();
   }

   public void setScale(float scale)
   {
      this.scale = scale;
      repaint();
   }

   public void setCompName(String compName)
   {
      this.compName = compName;
   }
   
   public void setMinMax(float min, float max)
   {
      this.min = min;
      this.max = max;
      roundMinMax();
   }
   
   private void roundMinMax()
   {
      if (max <= min)
      {
         float t = max;
         max = min;
         min = t;
         if (max - min < .0001)
         {
            max += .00005;
            min -= .00005;
         }
      }
      double r = max - min;
      if (r <= 0)
         r = 1;
      double logr = Math.log10(r);
      int iLogr = (int) (logr + 100) - 100;
      double mr = r / Math.pow(10., 1. * iLogr);
      int space = w / 20;
      if (space < 5)
         space = 5;
      if (space > mr)
      {
         mr *= 10;
         iLogr -= 1;
      }
      mr /= space;
      if (mr < 2)
         mr = 2;
      else if (mr < 5)
         mr = 5;
      else
         mr = 10;
      float d = (float) mr;
      if (iLogr > 0)
         for (int i = 0; i < iLogr; i++)
            d *= 10;
      if (iLogr < 0)
         for (int i = 0; i > iLogr; i--)
            d /= 10;
      sMin = d * Math.round(min / d);
      if (sMin < min)
         sMin += d;
      sMax = d * Math.round(max / d);
      if (sMax > max)
         sMax -= d;
      delta = d / ld;
      rMin = delta * Math.round(min / delta);
      if (rMin > min)
         rMin -= delta;
      rMax = delta * Math.round(max / delta);
      if (rMax < max)
         rMax += delta;
      lScale = (int) ((sMin - rMin) / delta);
      u = (int) ((rMax - rMin) / delta);
      ddec = 101 - (int) (Math.log10(delta) + 100);
      logr = Math.log10(Math.max(Math.abs(sMin), Math.abs(sMax)));
      iLogr = (int) (logr + 100) - 100;
      if (ddec < 0)
         ddec = 0;
      if (iLogr > 0)
         dec = iLogr + ddec + 2;
      else
         dec = ddec + 2;
      form = "%" + dec + "." + ddec + "f";
      int k = ddec - 2;
      if (k < 0)
         k = 0;
      lform = "%" + (dec - 1) + "." + k + "f";
   }

   @Override
   public void paint(Graphics g)
   {
      w = getWidth();
      h = getHeight() - 10;
      double maxHist = 1;
      float dx = w / 255.f;
      Graphics2D gr = (Graphics2D) g;

      gr.setColor(Color.white);
      gr.fillRect(0, 0, getWidth(), getHeight());
      BasicStroke ba = new BasicStroke(1.0F);

      //background histogram
      if (bgHist != null)
      {
         float hv;
         gr.setColor(new Color(210, 210, 210));
         maxHist = 1;
         for (int i = 0; i < bgHist.length; i++)
            if (bgHist[i] > maxHist)
               maxHist = bgHist[i];
         if (logHist)
         {
            maxHist = Math.log1p(maxHist) * 1.1;
            for (int k = 0; k < bgHist.length; k++)
            {
               hv = (float) (Math.log1p((double) bgHist[k]) / maxHist);
               g.drawLine((int) (k * dx), h, (int) (k * dx), (int) (h - h * hv));
            }
         }
         else
            for (int k = 0; k < bgHist.length; k++)
            {
               hv = (float) (bgHist[k] / maxHist);
               g.drawLine((int) (k * dx), h, (int) (k * dx), (int) (h - h * hv));
            }
      }
      gr.setColor(Color.darkGray);
      gr.setFont(textFont);
      gr.drawString("opacity", 1, 10);
      gr.drawString(compName, w - fm.stringWidth(compName) - 3, h);
      float d = delta * w / (sMax - sMin);
      float off = (sMin - min) * w / (sMax - sMin);
      for (int i = 0; i + lScale <= u; i += ld)
      {
         gr.setColor(Color.darkGray);
         String s = String.format(lform, sMin + i * delta).trim();
         int l = fm.stringWidth(s);
         float x = i * d - l / 2 + off;
         if (x < 0)     x = 0;
         if (x > w - l) continue;
         gr.drawString(s, x, h + 9);
         gr.setColor(new Color(240, 240, 240));
         gr.drawLine((int) (i * d + off), 0, (int) (i * d + off), h);
      }
      gr.setColor(new Color(200, 0, 0));
      GeneralPath p = new GeneralPath();
      p.moveTo(0, h - h * transp[0]);
      for (int k = 1; k < transp.length; k++)
         p.lineTo(k * dx, h - h * transp[k]);
      gr.draw(p);
      gr.setColor(Color.GRAY);
      p = new GeneralPath();
      p.moveTo(0, h - h * transp[0]);
      for (int k = 1; k < transp.length; k++)
         if (k > bgrThreshold)
             p.lineTo(k * dx, h - h * scale * transp[k]);
         else
             p.lineTo(k * dx, h );
      gr.draw(p);
   }
   
   private ChangeListener transparencyListener;
   
   public synchronized void setChangeListener(ChangeListener listener)
   {
      transparencyListener = listener;
   }

   public synchronized void removeChangeListener(ChangeListener listener)
   {
      transparencyListener = null;
   }
   private ChangeListener immmediateChangeListener;
   
   public synchronized void setImmediateChangeListener(ChangeListener listener)
   {
      immmediateChangeListener = listener;
   }

   public synchronized void removeImmediateChangeListener(ChangeListener listener)
   {
      immmediateChangeListener = null;
   }

   /** This method is called from within the constructor to
    * initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is
    * always regenerated by the Form Editor.
    */
   @SuppressWarnings("unchecked")
   // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
   private void initComponents()
   {

      setMinimumSize(new java.awt.Dimension(160, 100));
      addComponentListener(new java.awt.event.ComponentAdapter()
      {
         public void componentResized(java.awt.event.ComponentEvent evt)
         {
            formComponentResized(evt);
         }
      });

      javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
      this.setLayout(layout);
      layout.setHorizontalGroup(
         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGap(0, 255, Short.MAX_VALUE)
      );
      layout.setVerticalGroup(
         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGap(0, 200, Short.MAX_VALUE)
      );
   }// </editor-fold>//GEN-END:initComponents

   private void formComponentResized(java.awt.event.ComponentEvent evt)//GEN-FIRST:event_formComponentResized
   {//GEN-HEADEREND:event_formComponentResized
      w = getWidth();
      h = getHeight();
      roundMinMax();
      repaint();
   }//GEN-LAST:event_formComponentResized

   // Variables declaration - do not modify//GEN-BEGIN:variables
   // End of variables declaration//GEN-END:variables
}
