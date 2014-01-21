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


package pl.edu.icm.visnow.lib.basic.mappers.Graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.geom.GeneralPath;
import java.util.Vector;
import javax.media.j3d.J3DGraphics2D;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.engine.core.InputEgg;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.geometries.objects.DataMappedGeometryObject;
import pl.edu.icm.visnow.geometries.utils.transform.LocalToWindow;
import pl.edu.icm.visnow.lib.templates.visualization.modules.VisualizationModule;
import pl.edu.icm.visnow.lib.types.VNGeometryObject;
import pl.edu.icm.visnow.lib.types.VNRegularField;
import pl.edu.icm.visnow.lib.utils.AxisLabelItem;
import pl.edu.icm.visnow.lib.utils.Range;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class GraphObject extends VisualizationModule

{
   protected GraphWorld graphWorld;
   public static InputEgg[] inputEggs   = null;
   public static OutputEgg[] outputEggs = null;
   private GUI ui                       = null;
   protected Params params;
   protected RegularField inField       = null;
   protected int dim;
   protected int nComps;
   protected boolean fromInput = true;
   protected boolean fromParams = false;
   protected Vector<DataArray> data = new Vector<DataArray>();
   protected Vector<Color> graphColors = new Vector<Color>();
   protected String[] axDesc = null;
   protected int fontHeight = 15;


   public GraphObject()
   {
      parameters = params = new Params();
      graphWorld = new GraphWorld(params);
      params.addChangeListener(new ChangeListener()
      {
         @Override
         public void stateChanged(ChangeEvent evt)
         {
            if (fromInput)
               return;
            fromParams = true;
            if (params.isRefresh())
               startAction();
            else
               update();
         }
      });
      SwingInstancer.swingRunAndWait(new Runnable()
      {
         @Override
         public void run()
         {
            ui = new GUI();
         }
      });
      ui.setParams(params);
      setPanel(ui);
   }

   public class GraphWorld extends DataMappedGeometryObject
   {
      protected int origX = 0,   endX = 200,  origY = 0, endY  = 200;
      protected int w,  h, lastw = -1, lasth = -1;
      protected int n0 = 0,  n1 = 10;
      protected float xmin = 0,  xmax = 10,  ymin = 0,  ymax = 1;
      protected int x0, y0;
      protected float dx,  dy;
      protected Range xRange,  yRange;
      protected AxisLabelItem[] xLabels = null;
      protected AxisLabelItem[] yLabels = null;
      protected int frame;
      protected Params params;

      public GraphWorld(Params params)
      {
         super();
         this.params = params;
         name = "graph world";
      }

      float[] toScr(float x, float y)
      {
         float[] p = new float[2];
         p[0] = x0 + (x - xmin) * dx;
         p[1] = y0 - (y - ymin) * dy;
         return p;
      }

     @Override
      public void drawLocal2D(J3DGraphics2D gr, LocalToWindow ltw, int width, int height)
      {
         if (renderingWindow == null ||
             params == null ||
             params.getDisplayedData() == null || params.getDisplayedData().length < 1)
            return;
         fontHeight = (int)(height * params.getFontSize());
         gr.setFont(new java.awt.Font("Dialog", 0, fontHeight));
         FontMetrics fm = gr.getFontMetrics();
         axDesc = params.getAxesLabels();
         String xf,yf;

         int yMargin = fontHeight + 2;
         if (data == null || data.size() < 1)
         {
            ymin = 0;
            ymax = 1;
            return;
         }
         ymin = data.get(0).getMinv();
         ymax = data.get(0).getMaxv();
         for (int i = 1; i < data.size(); i++)
         {
            if (ymin > data.get(i).getMinv())
               ymin = data.get(i).getMinv();
            if (ymax < data.get(i).getMaxv())
               ymax = data.get(i).getMaxv();
         }
         origY = (int)(height * params.getVerticalExtents()[0] / 100.f);
         endY  = (int)(height * params.getVerticalExtents()[1] / 100.f);
         h     = endY - origY;
         if (h < 2 * yMargin + 100) h = 2 * yMargin + 100;
         yRange = new Range((h - 2 * yMargin) / (5 * fontHeight), ymin, ymax,  false);
         ymax = yRange.getUp();
         ymin = yRange.getLow();
         dy = (h - 2 * yMargin) / (ymax - ymin);
         y0 = endY - yMargin;
         int yk = (int)(Math.log10(yRange.getStep()));
         if (yk>0)
            yf = "%"+(yk+2)+".0f";
         else
            yf = "%"+(4-yk)+"."+(2-yk)+"f";

         int xll = yk+2;
         if (yk < 0) xll = 4 - yk;
         xll = fm.stringWidth(("12345678901234567890").substring(0, xll));
         int xMargin = fm.stringWidth(axDesc[1]);
         if (xll > xMargin) xMargin = xll;
         int xrMargin = fm.stringWidth(axDesc[0]);
         xMargin += xrMargin;

         origX = (int)(width * params.getHorizontalExtents()[0] / 100.f);
         endX  = (int)(width * params.getHorizontalExtents()[1] / 100.f);
         w     = endX - origX;
         xRange = new Range(Math.max(w - xMargin, 200) / (3 * xll), 0.f, 1.f * dim, false);
         xmax = xRange.getUp();
         xmin = 0;
         dx = (w - xMargin) / xmax ;
         x0 = origX + xMargin - xrMargin;

         float[] urCorner = toScr(xmax, ymax);

         if (inField == null)
            return;
         int xk = (int)(Math.log10(xRange.getStep()));
         if (xk>=0)
            xf = "%"+(xk+2)+".0f";
         else
            xf = "%"+(2-xk)+"."+(-xk)+"f";

         GeneralPath axes = new GeneralPath();
         gr.setStroke(new BasicStroke(params.getLineWidth()));

         float[] xr = xRange.getRange();
         float[] yr = yRange.getRange();
         gr.setColor(params.getColor());
         axes.moveTo(x0, urCorner[1]);
         axes.lineTo(x0, y0);
         axes.lineTo(urCorner[0], y0);
         gr.draw(axes);

         GeneralPath ticklines = new GeneralPath();
         gr.setStroke(new BasicStroke(1.f, BasicStroke.CAP_ROUND,
                 BasicStroke.JOIN_ROUND, 1.0f, new float[]{1,3},0));
         for (float xt = xRange.getRange()[0];
                    xt <= xRange.getRange()[1];
                    xt += xRange.getStep())
         {
            float xs = toScr(xt, yr[1])[0];
            ticklines.moveTo(xs, y0);
            ticklines.lineTo(xs, urCorner[1]);
            gr.drawString(String.format(xf, xt), xs - 5, y0 + 3 + fontHeight);
         }
         for (float yt =  yRange.getRange()[0];
                    yt <= yRange.getRange()[1];
                    yt += yRange.getStep())
         {
            float ys = toScr(xr[0], yt)[1];
            ticklines.moveTo(x0, ys);
            ticklines.lineTo(urCorner[0], ys);
            String l = String.format(yf, yt);
            gr.drawString(l, x0 - fm.stringWidth(l) - 3, ys + fontHeight / 2.f);
         }
         gr.draw(ticklines);

         fontHeight = (int)(1.5 * height * params.getFontSize());

         gr.setFont(new java.awt.Font("Dialog", 0, fontHeight));
         if (axDesc != null && axDesc.length >= 2)
         {
            gr.drawString(axDesc[0],  urCorner[0] + 3, y0);
            gr.drawString(axDesc[1], x0 - 10, urCorner[1] - fontHeight);
         }

         fontHeight = (int)(2 * height * params.getFontSize());

         if (params.getTitle() != null && !params.getTitle().isEmpty())
         {
            gr.setFont(new java.awt.Font("Dialog", 0, fontHeight));
            fm = gr.getFontMetrics();
            int titleWidth = fm.stringWidth(params.getTitle());
            gr.drawString(params.getTitle(), (origX + endX - titleWidth) / 2, origY - fontHeight);
         }

         fontHeight = (int)( height * params.getFontSize());

         gr.setFont(new java.awt.Font("Dialog", 0, fontHeight));
         for (int i = 0; i < data.size(); i++)
         {
            gr.setColor(graphColors.get(i));
            GeneralPath graph =  new GeneralPath();
            gr.setStroke(new BasicStroke(params.getLineWidth()));
            float[] grData = data.get(i).getFData();
            float[] p = toScr(0, grData[0]);
            graph.moveTo(p[0], p[1]);
            for (int j = 0; j < grData.length; j++)
            {
               p = toScr(j, grData[j]);
               graph.lineTo(p[0], p[1]);
            }
            gr.draw(graph);
            if (!params.isColorLegend()) continue;
            gr.drawString(data.get(i).getName(), urCorner[0] + 2, origY + i * (3 + fontHeight));
         }
      }
   }

   protected void update()
   {
      dim = inField.getDims()[0];
      data.clear();
      graphColors.clear();
      DisplayedData[] disp = params.getDisplayedData();
      for (int i = 0; i < disp.length; i++)
      {
         DisplayedData displayedData = disp[i];
         if (displayedData.isDisplayed())
         {
            data.add(inField.getData(displayedData.getIndex()));
            graphColors.add(displayedData.getColor());
         }
      }
      outObj.clearGeometries2D();
      if (outObj.getRenderingWindow() != null)
          outObj.getRenderingWindow().refresh();
   }

   @Override
   public void onInitFinishedLocal()
   {
      outObj = graphWorld;
      setOutputValue("outObj", new VNGeometryObject(outObj));
   }



   @Override
   public void onActive()
   {
      if (!fromParams)
      {
         fromInput = true;
         boolean newField = false;
         RegularField in;
         if (getInputFirstValue("inField") == null)
            return;
         in = ((VNRegularField) getInputFirstValue("inField")).getField();
         if (in == null || in.getDims() == null || in.getDims().length != 1)
            return;
         boolean updateUI = inField == null || !in.isDataCompatibleWith(inField);
         inField = in;
         if (updateUI)
            ui.setInField(inField);
         fromInput = false;
      }
      fromParams = false;
      update();
   }
}
