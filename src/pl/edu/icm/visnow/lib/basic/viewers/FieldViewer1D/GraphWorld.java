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

package pl.edu.icm.visnow.lib.basic.viewers.FieldViewer1D;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.util.Vector;
import javax.swing.SwingUtilities;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.lib.utils.AxisLabelItem;
import pl.edu.icm.visnow.lib.utils.Range;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class GraphWorld extends javax.swing.JPanel
{
   protected int origX = 45, origY = 180;
   protected int endX = 45,  endY  = 10;
   protected int w = 500,  h = 150, lastw = -1, lasth = -1;
   protected int n0 = 0,  n1 = 10;
   protected float xmin = 0,  xmax = 10;
   protected float ymin = 0,  ymax = 1;
   protected float dx,  dy;
   protected float dn;
   protected Range xRange,  yRange;
   protected AxisLabelItem[] xLabels = null;
   protected AxisLabelItem[] yLabels = null;
   protected String[] axDesc = null;
   protected Vector<GraphData> data = null;
   protected boolean useSelectedData = false;
   protected boolean[] selData = null;
   protected Vector<int[]> selRanges = new Vector<int[]>() ;
   protected int[] subRange;
   protected int frame;
   protected Viewer1DFrame parentFrame = null;

   /** Creates new form GraphWorld */
   public GraphWorld()
   {
      initComponents();
   }

   public GraphWorld(int xmin, int xmax, Vector<GraphData> data, String[] axDesc)
   {
      initComponents();
      setData(n0, n1, data, xmin, xmax, axDesc);
   }

   protected void setData(int n0, int n1, Vector<GraphData> data, float xm, float xx, String[] axDesc)
   {
      this.n0 = n0;
      this.n1 = n1;
      if (xx > xm)
      {
         xmin = xm;
         xmax = xx;
      } else
      {
         xmin = n0;
         xmax = n1;
      }
      xRange = new Range(xmin, xmax, w - 70);
      dx = (w - 70) / (xmax - xmin);
      dn = (w - 70.f) / (n1 - n0);
      this.data = data;
      if (data == null || data.size() < 1)
      {
         ymin = 0;
         ymax = 1;
         return;
      }
      ymin = data.get(0).getData().getMinv();
      ymax = data.get(0).getData().getMaxv();
      for (int i = 1; i < data.size(); i++)
      {
         if (ymin > data.get(i).getData().getMaxv())
            ymin = data.get(i).getData().getMaxv();
         if (ymax < data.get(i).getData().getMaxv())
            ymax = data.get(i).getData().getMaxv();
      }
	  if( ymin == ymax )
		{ ymin -= 0.5; ymax += 0.5; }
      yRange = new Range(ymin, ymax, h);
      dy = (h - 23.f) / (ymax - ymin);
      this.axDesc = axDesc;
      repaint();
   }

   float[] toScr(float x, float y)
   {
      float[] p = new float[2];
      p[0] = origX + (x - xmin) * dx;
      p[1] = origY - (y - ymin) * dy;
//      System.out.println(""+x+" "+p[0]+"  "+y+" "+p[1]);
      return p;
   }

   float[] toWorld(float x, float y)
   {
      float[] p = new float[2];
      p[0] = (x - origX) / dx + xmin;
      p[1] = (origY - y) / dy + ymin;
      return p;
   }

   void drawDataArea(Graphics2D gr,
           boolean xaxis, int frame)
   {

      String xf,yf;
      float[] dash = {1.f, 3.f};
      int xk = (int)(Math.log10(xRange.getStep()));
      if (xk>=0)
         xf = "%"+(xk+2)+".0f";
      else
         xf = "%"+(2-xk)+"."+(-xk)+"f";
      int yk = (int)(Math.log10(yRange.getStep()));
      if (yk>0)
         yf = "%"+(yk+2)+".0f";
      else
         yf = "%"+(4-yk)+"."+(2-yk)+"f";
      BasicStroke ba = new BasicStroke(1.f, BasicStroke.CAP_BUTT,
              BasicStroke.JOIN_BEVEL, 2.0f,
              dash, 0.f);
      float[] xr = xRange.getRange();
      float[] yr = yRange.getRange();
      gr.setColor(Color.DARK_GRAY);
      gr.setFont(new java.awt.Font("Dialog", 0, 10));
      GeneralPath ticks = new GeneralPath();

      ticks.moveTo(origX, origY);
      ticks.lineTo(endX, origY);
      if (xLabels == null)
      {
         for (float xl  = xRange.getRange()[0];
                    xl <= xRange.getRange()[1];
                    xl += xRange.getStep())
            if (xl < xmax && xl >= xmin)
            {
               float xs = toScr(xl, yr[0])[0];
               ticks.moveTo(xs, origY);
               ticks.lineTo(xs, origY+3);
               gr.drawString(String.format(xf, xl), xs, origY + 18);
            }
      } else
         for (int i = 0; i < xLabels.length; i++)
         {
            float xs = toScr(xLabels[i].getPosition(), yr[1])[0];
            ticks.moveTo(xs, origY);
            ticks.lineTo(xs, origY + 3);
            if (xaxis)
               gr.drawString(xLabels[i].getLabel(), xs, origY + 18);
         }
      ticks.moveTo(origX, origY);
      ticks.lineTo(origX, endY);
      if (yLabels == null)
      {
         for (float yl =  yRange.getRange()[0];
                    yl <= yRange.getRange()[1] + 0.5 * yRange.getStep();
                    yl += yRange.getStep())
            if (yl < ymax && yl >= ymin)
            {
               float ys = toScr(xr[0], yl)[1];
               ticks.moveTo(origX, ys);
               ticks.lineTo(origX - 3, ys);
               gr.drawString(String.format(yf, yl), origX - 30, ys);
            }
         } else
         for (int i = 0; i < yLabels.length; i++)
         {
            float ys = toScr(xr[0], yLabels[i].getPosition())[1];
            ticks.moveTo(origX, ys);
            ticks.lineTo(origX - 2, ys);
            gr.drawString(yLabels[i].getLabel(), origX - 30, ys);
         }
      gr.draw(ticks);

      GeneralPath ticklines = new GeneralPath();
      gr.setStroke(ba);
      for (float xt = xRange.getRange()[0];
              xt <= xRange.getRange()[1];
              xt += xRange.getStep())
      {
         float xs = toScr(xt, yr[1])[0];
         ticklines.moveTo(xs, origY);
         ticklines.lineTo(xs, endY);
      }
      for (float yt =  yRange.getRange()[0];
                 yt <= yRange.getRange()[1];
                 yt += yRange.getStep())
      {
         float ys = toScr(xr[0], yt)[1];
         ticklines.moveTo(origX, ys);
         ticklines.lineTo(endX, ys);
      }
      gr.draw(ticklines);
      if (axDesc != null && axDesc.length >= 2)
      {
         gr.drawString(axDesc[0], endX - 5 * axDesc[0].length(), origY - 5);
         gr.drawString(axDesc[1], origX - 30, endY - 20);
      }
      if (frame < 0)
         return;
      ba = new BasicStroke(1.f);
      GeneralPath timeline = new GeneralPath();
      gr.setStroke(ba);
      gr.setColor(Color.GRAY);
      float xs = toScr(1.f * frame, 0.f)[0];
      timeline.moveTo(xs, origY);
      timeline.lineTo(xs, endY);
      gr.draw(timeline);
   }

   public void drawSubRange(Graphics2D gr, int low, int up)
   {
      gr.setColor(new Color(200, 200, 255));
         gr.fillRect((int)(origX + (low - xmin) * dx), 0, (int) (dx * (up - low + 1)), h);
   }

   void drawData(Graphics2D gr, GraphData graphData)
   {
      BasicStroke s = new BasicStroke(1.0f);
      DataArray dataArray = null;
      gr.setColor(graphData.getColor());
      gr.setStroke(s);
      dataArray = graphData.getData();
      float[] vals = dataArray.getFData();
      if (vals == null)
         return;
      GeneralPath graph = new GeneralPath();
      graph.moveTo(origX, origY - (vals[n0] - ymin) * dy);
      int nmax = Math.min(n1, vals.length);
      for (int k = n0 + 1; k < nmax; k++)
         graph.lineTo(origX + (k - n0) * dx, origY - (vals[k] - ymin) * dy);
      gr.draw(graph);
   }

   public void setParentFrame(Viewer1DFrame parentFrame)
   {
      this.parentFrame = parentFrame;
   }


   public void setXLabels(AxisLabelItem[] xLabels)
   {
      this.xLabels = xLabels;
   }

   void setYLabels(AxisLabelItem[] yLabels)
   {
      this.yLabels = yLabels;
   }

   void setAxDesc(String[] axDesc)
   {
      this.axDesc = axDesc;
   }

   /**
    * @param data the data to set
    */
   public void setData(Vector<GraphData> data)
   {
      this.data = data;
   }

   @Override
   public void paint(Graphics g)
   {
      Graphics2D gr = (Graphics2D) g;
      gr.setPaint(new Color(250, 250, 255));
      w = getWidth();
      h = getHeight();
      gr.fillRect(0, 0, w, h);
      origY = h - 20;
      endX  = w - 5;
      if (w < 100 || h < 100 || data == null || data.size() < 1)
         return;
      if (w != lastw || h != lasth)
         setData(n0, n1, data, xmin, xmax, axDesc);
      int nGraphs = data.size();
      gr.setPaint(new Color(230, 230, 242));
      if (selRanges != null && selRanges.size()>0)
      {
         for (int[] r : selRanges)
         {
            //System.out.print("   "+r[0]+"-"+r[1]);
            drawSubRange(gr, r[0], r[1]);
         }
         //System.out.println("");
      }
      drawDataArea(gr, true, n0);
      for (int iGraph = 0; iGraph < nGraphs; iGraph++)
         drawData(gr, data.get(iGraph));
      lastw = w;
      lasth = h;
   }

   /** This method is called from within the constructor to
    * initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is
    * always regenerated by the Form Editor.
    */
   @SuppressWarnings("unchecked")
   // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
   private void initComponents() {

      setBackground(new java.awt.Color(153, 153, 255));
      addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            formMouseClicked(evt);
         }
         public void mousePressed(java.awt.event.MouseEvent evt) {
            formMousePressed(evt);
         }
         public void mouseReleased(java.awt.event.MouseEvent evt) {
            formMouseReleased(evt);
         }
      });
      addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
         public void mouseDragged(java.awt.event.MouseEvent evt) {
            formMouseDragged(evt);
         }
      });

      javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
      this.setLayout(layout);
      layout.setHorizontalGroup(
         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGap(0, 400, Short.MAX_VALUE)
      );
      layout.setVerticalGroup(
         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGap(0, 300, Short.MAX_VALUE)
      );
   }// </editor-fold>//GEN-END:initComponents

    private void formMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_formMouseClicked
    {//GEN-HEADEREND:event_formMouseClicked
        if(SwingUtilities.isLeftMouseButton(evt)) {
            if (evt.getX() < origX || evt.getX() > endX)
                frame = (int) (toWorld(1.f * evt.getX(), 0.f)[0]);
        }
        else if(SwingUtilities.isRightMouseButton(evt)) {
            selRanges.clear();
            parentFrame.setSelRanges(selRanges);
            parentFrame.repaint();
        }
    }//GEN-LAST:event_formMouseClicked

    private void formMousePressed(java.awt.event.MouseEvent evt)//GEN-FIRST:event_formMousePressed
    {//GEN-HEADEREND:event_formMousePressed
        if(SwingUtilities.isLeftMouseButton(evt)) {
            subRange = new int[2];
            if (evt.getX() < origX || evt.getX() > endX)
               return;
            frame = (int) (toWorld(1.f * evt.getX(), 0.f)[0]);
            subRange[0] = subRange[1] = frame;
            selRanges.add(subRange);
            repaint();
        }
    }//GEN-LAST:event_formMousePressed

    private void formMouseReleased(java.awt.event.MouseEvent evt)//GEN-FIRST:event_formMouseReleased
    {//GEN-HEADEREND:event_formMouseReleased
        if(SwingUtilities.isLeftMouseButton(evt)) {
            repaint();
            if (parentFrame != null)
               parentFrame.setSelRanges(selRanges);
        }
    }//GEN-LAST:event_formMouseReleased

    private void formMouseDragged(java.awt.event.MouseEvent evt)//GEN-FIRST:event_formMouseDragged
    {//GEN-HEADEREND:event_formMouseDragged
        if(SwingUtilities.isLeftMouseButton(evt)) {
            if (evt.getX() < origX || evt.getX() > endX)
               return;
            frame = (int) (toWorld(1.f * evt.getX(), 0.f)[0]);
            if (frame < subRange[0])
               subRange[0] = frame;
            if (frame > subRange[1])
               subRange[1] = frame;
            repaint();
        }
    }//GEN-LAST:event_formMouseDragged
   // Variables declaration - do not modify//GEN-BEGIN:variables
   // End of variables declaration//GEN-END:variables
}
