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

package pl.edu.icm.visnow.lib.basic.filters.CorrelationAnalysis;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Hashtable;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import pl.edu.icm.visnow.datamaps.ColorMap;
import pl.edu.icm.visnow.datamaps.ColorMapManager;
import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.geometries.gui.ComponentPrintable;
import pl.edu.icm.visnow.lib.utils.Range;
import pl.edu.icm.visnow.system.main.VisNow;
import pl.edu.icm.visnow.system.swing.filechooser.VNFileChooser;

/**
 * @author  Krzysztof S. Nowinski (know@icm.edu.pl)
 * Warsaw University, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */

public class CorrelationDisplayFrame extends javax.swing.JFrame
{
   private Field inField = null;
   private Params params = null;
   private Image corImage = null;
   private float stdScale = 10;
   private float scale = 10;
   private float mouseWheelSensitivity = 1.1f;
   private RegularField correlationField;
   private float[][] data;
   private int corrDim = 0;
   private int imageX = 0, imageY = 0;
   private float[] corrArray;
   private String[] corrVars;
   private float dMax = 1;
   private int lastX          = -1;
   private int lastY          = -1;
   private ColorMap colorMap  = ColorMapManager.getInstance().getColorMap1D(ColorMapManager.COLORMAP1D_BLUE_WHITE_RED);
   private ColorMap colorMap1  = ColorMapManager.getInstance().getColorMap1D(ColorMapManager.COLORMAP1D_BLUE_BLACK_RED);
   private int[] lut = colorMap.getARGBColorTable();
   private int[] lut1 = colorMap1.getARGBColorTable();
   private String lastPath = null;
   private String path = null;
   private Font font = new Font("DialogInput", 0, 10);
   private FontMetrics fm = new JLabel().getFontMetrics(font);
   private Color bgr = new Color(238,238,238);
   private int var0, var1;

   private double[][] tvals = new double[][]
   {
      {1.000, 1.376, 1.963, 3.078, 6.314, 12.71, 31.82, 63.66, 127.3, 318.3, 636.6},
      {0.816, 1.061, 1.386, 1.886, 2.920, 4.303, 6.965, 9.925, 14.09, 22.33, 31.60},
      {0.765, 0.978, 1.250, 1.638, 2.353, 3.182, 4.541, 5.841, 7.453, 10.21, 12.92},
      {0.741, 0.941, 1.190, 1.533, 2.132, 2.776, 3.747, 4.604, 5.598, 7.173, 8.610},
      {0.727, 0.920, 1.156, 1.476, 2.015, 2.571, 3.365, 4.032, 4.773, 5.893, 6.869},
      {0.718, 0.906, 1.134, 1.440, 1.943, 2.447, 3.143, 3.707, 4.317, 5.208, 5.959},
      {0.711, 0.896, 1.119, 1.415, 1.895, 2.365, 2.998, 3.499, 4.029, 4.785, 5.408},
      {0.706, 0.889, 1.108, 1.397, 1.860, 2.306, 2.896, 3.355, 3.833, 4.501, 5.041},
      {0.703, 0.883, 1.100, 1.383, 1.833, 2.262, 2.821, 3.250, 3.690, 4.297, 4.781},
      {0.700, 0.879, 1.093, 1.372, 1.812, 2.228, 2.764, 3.169, 3.581, 4.144, 4.587},
      {0.697, 0.876, 1.088, 1.363, 1.796, 2.201, 2.718, 3.106, 3.497, 4.025, 4.437},
      {0.695, 0.873, 1.083, 1.356, 1.782, 2.179, 2.681, 3.055, 3.428, 3.930, 4.318},
      {0.694, 0.870, 1.079, 1.350, 1.771, 2.160, 2.650, 3.012, 3.372, 3.852, 4.221},
      {0.692, 0.868, 1.076, 1.345, 1.761, 2.145, 2.624, 2.977, 3.326, 3.787, 4.140},
      {0.691, 0.866, 1.074, 1.341, 1.753, 2.131, 2.602, 2.947, 3.286, 3.733, 4.073},
      {0.690, 0.865, 1.071, 1.337, 1.746, 2.120, 2.583, 2.921, 3.252, 3.686, 4.015},
      {0.689, 0.863, 1.069, 1.333, 1.740, 2.110, 2.567, 2.898, 3.222, 3.646, 3.965},
      {0.688, 0.862, 1.067, 1.330, 1.734, 2.101, 2.552, 2.878, 3.197, 3.610, 3.922},
      {0.688, 0.861, 1.066, 1.328, 1.729, 2.093, 2.539, 2.861, 3.174, 3.579, 3.883},
      {0.687, 0.860, 1.064, 1.325, 1.725, 2.086, 2.528, 2.845, 3.153, 3.552, 3.850},
      {0.686, 0.859, 1.063, 1.323, 1.721, 2.080, 2.518, 2.831, 3.135, 3.527, 3.819},
      {0.686, 0.858, 1.061, 1.321, 1.717, 2.074, 2.508, 2.819, 3.119, 3.505, 3.792},
      {0.685, 0.858, 1.060, 1.319, 1.714, 2.069, 2.500, 2.807, 3.104, 3.485, 3.767},
      {0.685, 0.857, 1.059, 1.318, 1.711, 2.064, 2.492, 2.797, 3.091, 3.467, 3.745},
      {0.684, 0.856, 1.058, 1.316, 1.708, 2.060, 2.485, 2.787, 3.078, 3.450, 3.725},
      {0.684, 0.856, 1.058, 1.315, 1.706, 2.056, 2.479, 2.779, 3.067, 3.435, 3.707},
      {0.684, 0.855, 1.057, 1.314, 1.703, 2.052, 2.473, 2.771, 3.057, 3.421, 3.690},
      {0.683, 0.855, 1.056, 1.313, 1.701, 2.048, 2.467, 2.763, 3.047, 3.408, 3.674},
      {0.683, 0.854, 1.055, 1.311, 1.699, 2.045, 2.462, 2.756, 3.038, 3.396, 3.659},
      {0.683, 0.854, 1.055, 1.310, 1.697, 2.042, 2.457, 2.750, 3.030, 3.385, 3.646},
      {0.681, 0.851, 1.050, 1.303, 1.684, 2.021, 2.423, 2.704, 2.971, 3.307, 3.551},
      {0.679, 0.849, 1.047, 1.299, 1.676, 2.009, 2.403, 2.678, 2.937, 3.261, 3.496},
      {0.679, 0.848, 1.045, 1.296, 1.671, 2.000, 2.390, 2.660, 2.915, 3.232, 3.460},
      {0.678, 0.846, 1.043, 1.292, 1.664, 1.990, 2.374, 2.639, 2.887, 3.195, 3.416},
      {0.677, 0.845, 1.042, 1.290, 1.660, 1.984, 2.364, 2.626, 2.871, 3.174, 3.390},
      {0.677, 0.845, 1.041, 1.289, 1.658, 1.980, 2.358, 2.617, 2.860, 3.160, 3.373},
      {0.674, 0.842, 1.036, 1.282, 1.645, 1.960, 2.326, 2.576, 2.807, 3.090, 3.291}
   };
   private double[] oneSideTSig = new double[]
   {.25, .2, .15, .1, .05, .025, .01, .005, .0025, .001, .0005};
   private double[] twoSideTSig = new double[]
   {.5, .4, .3, .2, .1, .05, .025, .01, .005, .0025, .001};
   private int[] tDoF = new int[]
   {1, 2, 3, 4, 5, 6, 7, 8, 9, 10,11,12,13,14,15,16,17,18,19,20,
   21,22,23,24,25,26,27,28,29,30,40,50,60,80,100, 120};   
   private int column = 4;
   private int sampleSize = 100;
   private Hashtable<Integer, JLabel> labels = new Hashtable<Integer, JLabel>();
   private String[] ls = new String[]{"",".5", ".2", ".1", ".05", ".02", ".01"};
   

   private JPanel corelPanel = new JPanel()
   {
      @Override
      public void paint(Graphics g)
      {
         if (correlationField == null)
            return;
         Graphics2D gr = (Graphics2D) g;
         gr.setColor(bgr);
         gr.fillRect(0, 0, getWidth(), getHeight());
         if (corrDim < 1)
            return;
         if (corImage == null)
            return;
         gr.drawImage(corImage, imageX, imageY, (int)(scale * corrDim),
                 (int)(scale * corrDim), null);
      }
   };     
   
   private JPanel cmapPanel = new JPanel()
   {
      @Override
      public void paint(Graphics g)
      {
         Graphics2D gr = (Graphics2D) g;
         Range range;
         int w = getWidth();
         int h = getHeight();
         int x = 2;
         int y = 30;
         int l = Math.max(300,(int)(.6 * h));
         DecimalFormat intf;
         range = new Range(-1.f, 1.f, (int)(20 * l/12.f));
         GeneralPath ticks;
         BufferedImage img = new BufferedImage(2, 256, BufferedImage.TYPE_INT_ARGB);
         int[] pix = new int[512];
         for (int i = 0; i < 256; i++)
         {
            int k = pix.length - 2 - 2 * i;
            pix[k] = pix[k + 1] = lut[i];
         }
         img.setRGB(0, 0, 2, 256, pix, 0, 2);
         gr.drawImage(img, x, y, w-28, l, null);
         gr.setColor(Color.GRAY);
         gr.setFont(new Font("Dialog", 0, 12));
         ticks = new GeneralPath();
         ticks.moveTo(x, y);
         ticks.lineTo(x, y + l);
         ticks.lineTo(x + w, y + l);
         ticks.lineTo(x + w, y);
         ticks.lineTo(x, y);
         for (float t = range.getLow(); t <= 1; t += range.getStep())
         {
            int i = y + l - (int) (l * (t + 1.) / 2);
            ticks.moveTo(x, i);
            ticks.lineTo(x + w, i);
         }
         gr.draw(ticks);
         gr.setColor(Color.DARK_GRAY);
         for (float t = range.getLow(); t <= 1; t += range.getStep())
             gr.drawString(String.format("%.1f", t), w - 25, y + l - (int) (l * (t + 1) /2.) -2);
      }
   };     

   private JPanel cldPanel = new JPanel()
   {
      @Override
      public void paint(Graphics g)
      {
         int fontSize = 12;
         if (correlationField == null)
            return;
         Graphics2D gr = (Graphics2D) g;
         gr.setColor(Color.white);
         gr.fillRect(0, 0, getWidth(), getHeight());
         if (corrDim < 1 || data == null ||
             var0 < 0 || var0 >= data.length ||
             var1 < 0 || var1 >= data.length)
            return;
         float[] v0 = data[var0];
         float[] v1 = data[var1];
         float min0 = Float.MAX_VALUE, max0 = -Float.MAX_VALUE;
         float min1 = Float.MAX_VALUE, max1 = -Float.MAX_VALUE;
         for (int i = 0; i < sampleSize; i++)
         {
            if (v0[i] < min0) min0 = v0[i];
            if (v0[i] > max0) max0 = v0[i];
            if (v1[i] < min1) min1 = v1[i];
            if (v1[i] > max1) max1 = v1[i];
         }
         int w = getWidth();
         int h = getHeight();
         float d0 = (w - 2 * fontSize - 4) / (max0 - min0);
         float d1 = (h - 2 * fontSize - 4) / (max1 - min1);
         gr.setColor(Color.DARK_GRAY);
         for (int i = 0; i < sampleSize; i++)
            gr.drawLine(13  + (int)((v0[i] - min0) * d0), h - 11 - (int)((v1[i] - min1) * d1), 
                        13  + (int)((v0[i] - min0) * d0), h - 11 - (int)((v1[i] - min1) * d1));
         Font font = new Font("DialogInput", 0, fontSize);
         FontMetrics fm = new JLabel().getFontMetrics(font);
         gr.setFont(font);
         gr.setColor(new Color(150,0,0));
         gr.drawString(corrVars[var1], 1, fontSize + 2);
         gr.drawString(corrVars[var0], w - fm.stringWidth(corrVars[var0]), h - 1);
         gr.drawString(String.format("%6.4f", corrArray[var0 * corrDim + var1]), w - fm.stringWidth("5555555"), fontSize + 2);
      }
   };     
   
   private JPanel hLegend = new JPanel()
   {
      @Override
      public void paint(Graphics g)
      {
         if (correlationField == null)
            return;
         Graphics2D gr = (Graphics2D) g;
         gr.setColor(bgr);
         gr.fillRect(0, 0, getWidth(), getHeight());
         if (corrDim < 1)
            return;
         gr.setColor(Color.DARK_GRAY);
         gr.setFont(font);
         int step = (int)(21/scale);
         float y = getHeight() - 6; 
         for (int i = 0; i < corrDim; i += step)
         {
            float x = imageX + scale * i + 9;
            gr.rotate(-Math.PI / 2, x, y);  
            gr.drawString(corrVars[i], x, y);
            gr.rotate(Math.PI / 2, x, y);  
         }
      }
   };

   private JPanel vLegend = new JPanel()
   {
      @Override
      public void paint(Graphics g)
      {
         Graphics2D gr = (Graphics2D) g;
         int w = getWidth();
         gr.setColor(bgr);
         gr.fillRect(0, 0, getWidth(), getHeight());
         if (corrDim < 1)
            return;
         gr.setColor(Color.DARK_GRAY);
         gr.setFont(font);
         int step = (int)(21/scale);
         for (int i = 0; i < corrDim; i += step)
         {
            float y = imageY + scale * i + 9;
            gr.drawString(corrVars[i], w - 3 - fm.stringWidth(corrVars[i]), y);
         }
      }
   };

   
   /**
    * Creates new form CorrelationDisplayFrame
    */
   public CorrelationDisplayFrame()
   {
      initComponents();
      for (int i = 0; i < ls.length; i++)
         labels.put(i, new JLabel(ls[i]));
      sigSlider.setLabelTable(labels);
      dataComponentSelector.setScalarComponentsOnly(true);
      correlationDisplayPanel.add(corelPanel, BorderLayout.CENTER);
      hMargin.add(hLegend, BorderLayout.CENTER);
      vMargin.add(vLegend, BorderLayout.CENTER);
      cloudPanel.add(cldPanel, BorderLayout.CENTER);
      colormapPanel.add(cmapPanel, BorderLayout.CENTER);
      corelPanel.addMouseWheelListener(new MouseWheelListener()
      {
         public void mouseWheelMoved(MouseWheelEvent evt)
         {
            int notches = evt.getWheelRotation();
            if (notches<0) scale/=mouseWheelSensitivity;
            else           scale*=mouseWheelSensitivity;
            scale = Math.max(1, Math.min(scale, 15));
            corelPanel.repaint();
            hLegend.repaint();
            vLegend.repaint();
         }
      });
      
      corelPanel.addMouseListener(new MouseListener()
      {
         public void mousePressed(MouseEvent e)
         {
            lastX = e.getX();
            lastY = e.getY();
         }
         
         public void mouseClicked(MouseEvent e)
         {
            if (e.getButton() == MouseEvent.BUTTON1)
            {
               var0 = (int)((e.getX() - imageX) / scale);
               var1 = (int)((e.getY() - imageY) / scale);
               cldPanel.repaint();
            }
            else
            {
               imageX = imageY = 0;
               scale = stdScale;
               hLegend.repaint();
               vLegend.repaint();
               repaint();
            }
         }
         public void mouseReleased(MouseEvent e){}
         public void mouseEntered(MouseEvent e){}
         public void mouseExited(MouseEvent e){}
      });
      
      corelPanel.addMouseMotionListener(new MouseMotionListener()
      {
         public void mouseDragged(MouseEvent evt)
         {
            if (lastX == -1)
            {
               lastX = evt.getX();
               lastY = evt.getY();
               return;
            }
            int ix = imageX + evt.getX() - lastX;
            if (ix<= 0 && ix + scale * corrDim >= getWidth())
               imageX = ix;
            lastX = evt.getX();
            int iy = imageY + evt.getY() - lastY;
            if (iy <= 0 && iy + scale * corrDim >= getHeight())
               imageY = iy;
            lastY = evt.getY();
            hLegend.repaint();
            vLegend.repaint();
            repaint();
         }
         
         public void mouseMoved(MouseEvent e)
         {
            int var0 = (int)((e.getX() - imageX) / scale);
            int var1 = (int)((e.getY() - imageY) / scale);
            if (corrDim < 1 || data == null ||
               var0 < 0 || var0 >= data.length ||
               var1 < 0 || var1 >= data.length)
               return;
            corelPanel.setToolTipText(
                    String.format("%s - %s : %6.4f", 
                                  corrVars[var0], corrVars[var1], 
                                  corrArray[var1 * corrDim + var0]));
            
         }
      });
      
      hLegend.addMouseListener(new MouseListener()
      {
         public void mousePressed(MouseEvent e)
         {
            lastX = e.getX();
         }

         public void mouseClicked(MouseEvent e)
         {
            imageX = 0;
            hLegend.repaint();
            vLegend.repaint();
            repaint();
         }
         public void mouseReleased(MouseEvent e){}
         public void mouseEntered(MouseEvent e){}
         public void mouseExited(MouseEvent e){}
      });
      
      hLegend.addMouseMotionListener(new MouseMotionListener()
      {
         
         public void mouseDragged(MouseEvent evt)
         {
            if (lastX == -1)
            {
               lastX = evt.getX();
               return;
            }
            int ix = imageX + evt.getX() - lastX;
            if (ix<= 0 && ix + scale * corrDim >= getWidth())
               imageX = ix;
            lastX = evt.getX();
            corelPanel.repaint();
            vLegend.repaint();
            repaint();
         }
         
         public void mouseMoved(MouseEvent e){}
      });
      
      vLegend.addMouseListener(new MouseListener()
      {
         public void mousePressed(MouseEvent e)
         {
            lastY = e.getY();
         }
         
         public void mouseClicked(MouseEvent e)
         {
            imageY = 0;
            hLegend.repaint();
            vLegend.repaint();
            repaint();
         }
         public void mouseReleased(MouseEvent e){}
         public void mouseEntered(MouseEvent e){}
         public void mouseExited(MouseEvent e){}
      });
      
      vLegend.addMouseMotionListener(new MouseMotionListener()
      {
         
         public void mouseDragged(MouseEvent evt)
         {
            if (lastX == -1)
            {
               lastY = evt.getY();
               return;
            }
            int iy = imageY + evt.getY() - lastY;
            if (iy <= 0 && iy + scale * corrDim >= getHeight())
               imageY = iy;
            lastY = evt.getY();
            corelPanel.repaint();
            hLegend.repaint();
            repaint();
         }
         
         public void mouseMoved(MouseEvent e){}
      });
   }
   
   

   public void setCorrelationField(RegularField correlationField)
   {
      if (correlationField == null || correlationField.getDims().length != 2 ||
          correlationField.getDims()[0] != correlationField.getDims()[1])
         return;
      if (correlationField.getData("correlations") == null && 
          correlationField.getData("covariance") == null)
         return;
      this.correlationField = correlationField;
      corrDim = correlationField.getDims()[0];
      String cmpName =  "";
      if (correlationField.getData("correlations") != null)
      {
         cmpName = "correlations";
         sigSlider.setEnabled(true);
      }
      else if (correlationField.getData("covariance") != null)
      {
         cmpName = "covariance";
         sigSlider.setEnabled(false);
         column = -1;
      }
      corrArray = correlationField.getData(cmpName).getFData();
      dMax = correlationField.getData(cmpName).getMaxv();
      if (correlationField.getData(cmpName).getUserData() != null &&
          correlationField.getData(cmpName).getUserData().length == corrDim)
         corrVars = correlationField.getData(cmpName).getUserData();
      else
      {
         corrVars = new String[corrDim];
         for (int i = 0; i < corrDim; i++)
            corrVars[i] = "" + i;
      }
      createImage();
      imageX = imageY = 0;
      repaint();
   }

   public void setParams(Params params)
   {
      this.params = params;
   }

   public void setInField(Field inField)
   {
      params.setActive(false);
      if (inField instanceof RegularField && ((RegularField) inField).getDims().length == 2)
      {
         rowButton.setEnabled(true);
         colButton.setEnabled(true);
         dataComponentSelector.setDataSchema(inField.getSchema());
      }
      else
      {
         rowButton.setEnabled(false);
         colButton.setEnabled(false);
         dataComponentSelector.setEnabled(false);
      }  
      this.inField = inField;
      cmpButton.setSelected(true);
      params.setVariableType(Params.COMPONENTS_AS_VARIABLES);
      params.setActive(true);
   }
   
   public void setData(float[][] data)
   {
      this.data = data;
   }
   
   public void setSampleSize(int sampleSize)
   {
      this.sampleSize = sampleSize;
   }
   
   private void createImage()
   {
      int[] pix = new int[corrDim * corrDim];
      if (sampleSize < 3)
         return;
      int nColors = ColorMapManager.SAMPLING_TABLE - 1;
      int dof = -1;
      for (int i = 0; i < tDoF.length; i++)
         if (sampleSize <= tDoF[i])
         {
            dof = i;
            break;
         }
      if (dof == -1)
         dof = tDoF.length - 1;
      double d = nColors / (2 * dMax);
      for (int i = 0; i < pix.length; i++)
      {
         double r = corrArray[i];
         int cIndex = (int) (d * (r + dMax));
         if (cIndex < 0)
            cIndex = 0;
         if (cIndex > nColors)
            cIndex = nColors;
         if (whiteRadioButton.isSelected())
            pix[i] = lut[cIndex];
         else
            pix[i] = lut1[cIndex];
         if (column != -1)
         {
            r = Math.sqrt(r * r * (sampleSize - 2) / (1 - r * r));
            if (r < tvals[dof][column])
               pix[i] = 0xff777777;
         }

      }
      corImage = createImage(new MemoryImageSource(corrDim, corrDim, pix, 0, corrDim));
   }

   /**
    * This method is called from within the constructor to initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is always
    * regenerated by the Form Editor.
    */
   @SuppressWarnings("unchecked")
   // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
   private void initComponents()
   {
      java.awt.GridBagConstraints gridBagConstraints;

      fileChooser = new javax.swing.JFileChooser();
      buttonGroup1 = new javax.swing.ButtonGroup();
      buttonGroup2 = new javax.swing.ButtonGroup();
      buttonGroup3 = new javax.swing.ButtonGroup();
      correlationDisplayPanel = new javax.swing.JPanel();
      jPanel2 = new javax.swing.JPanel();
      sigSlider = new javax.swing.JSlider();
      cmpButton = new javax.swing.JRadioButton();
      rowButton = new javax.swing.JRadioButton();
      colButton = new javax.swing.JRadioButton();
      dataComponentSelector = new pl.edu.icm.visnow.lib.gui.DataComponentSelector();
      jPanel1 = new javax.swing.JPanel();
      covarianceButton = new javax.swing.JRadioButton();
      correlationButton = new javax.swing.JRadioButton();
      whiteRadioButton = new javax.swing.JRadioButton();
      blackRadioButton = new javax.swing.JRadioButton();
      hMargin = new javax.swing.JPanel();
      vMargin = new javax.swing.JPanel();
      cloudPanel = new javax.swing.JPanel();
      colormapPanel = new javax.swing.JPanel();
      jMenuBar1 = new javax.swing.JMenuBar();
      jMenu1 = new javax.swing.JMenu();
      csvItem = new javax.swing.JMenuItem();
      textItem = new javax.swing.JMenuItem();
      printItem = new javax.swing.JMenuItem();

      getContentPane().setLayout(new java.awt.GridBagLayout());

      correlationDisplayPanel.setMinimumSize(new java.awt.Dimension(400, 400));
      correlationDisplayPanel.setOpaque(false);
      correlationDisplayPanel.setPreferredSize(new java.awt.Dimension(450, 450));
      correlationDisplayPanel.setLayout(new java.awt.BorderLayout());
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 1.0;
      getContentPane().add(correlationDisplayPanel, gridBagConstraints);

      jPanel2.setMinimumSize(new java.awt.Dimension(300, 78));
      jPanel2.setOpaque(false);
      jPanel2.setPreferredSize(new java.awt.Dimension(600, 78));
      jPanel2.setLayout(new java.awt.GridBagLayout());

      sigSlider.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
      sigSlider.setMajorTickSpacing(1);
      sigSlider.setMaximum(6);
      sigSlider.setPaintLabels(true);
      sigSlider.setSnapToTicks(true);
      sigSlider.setValue(4);
      sigSlider.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "significant at level", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
      sigSlider.setMaximumSize(new java.awt.Dimension(32767, 40));
      sigSlider.setMinimumSize(new java.awt.Dimension(150, 40));
      sigSlider.setPreferredSize(new java.awt.Dimension(160, 40));
      sigSlider.addChangeListener(new javax.swing.event.ChangeListener()
      {
         public void stateChanged(javax.swing.event.ChangeEvent evt)
         {
            sigSliderStateChanged(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 3;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.gridheight = 3;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      jPanel2.add(sigSlider, gridBagConstraints);

      buttonGroup1.add(cmpButton);
      cmpButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
      cmpButton.setSelected(true);
      cmpButton.setText("components as vars");
      cmpButton.setMaximumSize(new java.awt.Dimension(162, 16));
      cmpButton.setMinimumSize(new java.awt.Dimension(162, 16));
      cmpButton.setPreferredSize(new java.awt.Dimension(162, 16));
      cmpButton.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            cmpButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
      jPanel2.add(cmpButton, gridBagConstraints);

      buttonGroup1.add(rowButton);
      rowButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
      rowButton.setText("rows as vars");
      rowButton.setMaximumSize(new java.awt.Dimension(133, 16));
      rowButton.setMinimumSize(new java.awt.Dimension(133, 16));
      rowButton.setPreferredSize(new java.awt.Dimension(133, 16));
      rowButton.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            rowButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
      jPanel2.add(rowButton, gridBagConstraints);

      buttonGroup1.add(colButton);
      colButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
      colButton.setText("columns as vars");
      colButton.setMaximumSize(new java.awt.Dimension(156, 16));
      colButton.setMinimumSize(new java.awt.Dimension(156, 16));
      colButton.setPreferredSize(new java.awt.Dimension(153, 16));
      colButton.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            colButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
      jPanel2.add(colButton, gridBagConstraints);

      dataComponentSelector.setMaximumSize(new java.awt.Dimension(180, 40));
      dataComponentSelector.setMinimumSize(new java.awt.Dimension(100, 40));
      dataComponentSelector.setPreferredSize(new java.awt.Dimension(150, 40));
      dataComponentSelector.addChangeListener(new javax.swing.event.ChangeListener()
      {
         public void stateChanged(javax.swing.event.ChangeEvent evt)
         {
            dataComponentSelectorStateChanged(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.gridheight = 3;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.ipadx = 100;
      gridBagConstraints.ipady = 5;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
      gridBagConstraints.insets = new java.awt.Insets(7, 0, 7, 0);
      jPanel2.add(dataComponentSelector, gridBagConstraints);
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 4;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.weightx = 1.0;
      jPanel2.add(jPanel1, gridBagConstraints);

      buttonGroup2.add(covarianceButton);
      covarianceButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
      covarianceButton.setText("covariance ");
      covarianceButton.setMinimumSize(new java.awt.Dimension(97, 16));
      covarianceButton.setPreferredSize(new java.awt.Dimension(97, 16));
      covarianceButton.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            covarianceButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      jPanel2.add(covarianceButton, gridBagConstraints);

      buttonGroup2.add(correlationButton);
      correlationButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
      correlationButton.setSelected(true);
      correlationButton.setText("correlation");
      correlationButton.setMinimumSize(new java.awt.Dimension(93, 16));
      correlationButton.setPreferredSize(new java.awt.Dimension(93, 16));
      correlationButton.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            correlationButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      jPanel2.add(correlationButton, gridBagConstraints);

      buttonGroup3.add(whiteRadioButton);
      whiteRadioButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
      whiteRadioButton.setSelected(true);
      whiteRadioButton.setText("blue-white-red");
      whiteRadioButton.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            whiteRadioButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 3;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
      jPanel2.add(whiteRadioButton, gridBagConstraints);

      buttonGroup3.add(blackRadioButton);
      blackRadioButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
      blackRadioButton.setText("blue-black-red");
      blackRadioButton.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            blackRadioButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 3;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
      jPanel2.add(blackRadioButton, gridBagConstraints);

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      getContentPane().add(jPanel2, gridBagConstraints);

      hMargin.setMaximumSize(new java.awt.Dimension(32767, 72));
      hMargin.setMinimumSize(new java.awt.Dimension(400, 72));
      hMargin.setName(""); // NOI18N
      hMargin.setPreferredSize(new java.awt.Dimension(600, 72));
      hMargin.setRequestFocusEnabled(false);
      hMargin.setLayout(new java.awt.BorderLayout());
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      getContentPane().add(hMargin, gridBagConstraints);

      vMargin.setMaximumSize(new java.awt.Dimension(120, 32767));
      vMargin.setMinimumSize(new java.awt.Dimension(120, 400));
      vMargin.setName(""); // NOI18N
      vMargin.setPreferredSize(new java.awt.Dimension(120, 600));
      vMargin.setLayout(new java.awt.BorderLayout());
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      getContentPane().add(vMargin, gridBagConstraints);

      cloudPanel.setBackground(new java.awt.Color(255, 255, 255));
      cloudPanel.setMaximumSize(new java.awt.Dimension(150, 150));
      cloudPanel.setMinimumSize(new java.awt.Dimension(150, 150));
      cloudPanel.setPreferredSize(new java.awt.Dimension(150, 150));
      cloudPanel.setLayout(new java.awt.BorderLayout());
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.gridheight = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      getContentPane().add(cloudPanel, gridBagConstraints);

      colormapPanel.setMinimumSize(new java.awt.Dimension(50, 100));
      colormapPanel.setPreferredSize(new java.awt.Dimension(50, 100));
      colormapPanel.setRequestFocusEnabled(false);
      colormapPanel.setLayout(new java.awt.BorderLayout());
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      getContentPane().add(colormapPanel, gridBagConstraints);

      jMenu1.setText("File");

      csvItem.setText("output text");
      csvItem.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            csvItemActionPerformed(evt);
         }
      });
      jMenu1.add(csvItem);

      textItem.setText("output csv");
      textItem.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            textItemActionPerformed(evt);
         }
      });
      jMenu1.add(textItem);

      printItem.setText("print image");
      printItem.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            printItemActionPerformed(evt);
         }
      });
      jMenu1.add(printItem);

      jMenuBar1.add(jMenu1);

      setJMenuBar(jMenuBar1);

      setSize(new java.awt.Dimension(836, 636));
      setLocationRelativeTo(null);
   }// </editor-fold>//GEN-END:initComponents

   
   private void printItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_printItemActionPerformed
   {//GEN-HEADEREND:event_printItemActionPerformed
      ComponentPrintable cPr;
      PrinterJob pj = PrinterJob.getPrinterJob();
      PageFormat mPageFormat = pj.defaultPage();
      mPageFormat = pj.pageDialog(mPageFormat);
      cPr = new ComponentPrintable(this);
      pj.setPrintable(cPr, mPageFormat);
      if (pj.printDialog())
      {
         try
         {
            pj.print();
         } catch (PrinterException e)
         {
            System.out.println("coannot print images");
         }
      }
   }//GEN-LAST:event_printItemActionPerformed

   private void csvItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_csvItemActionPerformed
   {//GEN-HEADEREND:event_csvItemActionPerformed
      if (corrArray == null || corrArray.length < 1)
         return;
      if (lastPath == null)
          fileChooser.setCurrentDirectory(new File( VisNow.get().getMainConfig().getWorkeffectPath() ));
       else
          fileChooser.setCurrentDirectory(new File(lastPath));
		FileNameExtensionFilter cvsExtensionFilter = new FileNameExtensionFilter("csv files", "csv", "CSV");
	   fileChooser.setFileFilter(cvsExtensionFilter);
       int returnVal = fileChooser.showSaveDialog(this);
       if (returnVal == JFileChooser.APPROVE_OPTION)
       {
          path = VNFileChooser.filenameWithExtenstionAddedIfNecessary( fileChooser.getSelectedFile(), cvsExtensionFilter );
          lastPath = path.substring(0, path.lastIndexOf(File.separator));
          try
          {
             PrintWriter writer = new PrintWriter(new File(path));
             writer.print("        ");
             for (int i = 0; i < corrDim; i++)
                writer.printf("%7s;", corrVars[i].substring(0, Math.min(7,corrVars[i].length())));
             writer.println();
             for (int i = 0; i < corrDim; i++)
             {
                writer.printf("%7s;", corrVars[i].substring(0, Math.min(7,corrVars[i].length())));
                for (int j = 0; j < corrDim; j++)
                {
                   writer.printf("%7.4f;", corrArray[i * corrDim + j]);
                }
                writer.println();
             }
             writer.close();
          } catch (Exception e)
          {
             System.out.println("could not print to " + path);
          }    
       }
   }//GEN-LAST:event_csvItemActionPerformed

   private void textItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_textItemActionPerformed
   {//GEN-HEADEREND:event_textItemActionPerformed
      if (corrArray == null || corrArray.length < 1)
         return;
      if (lastPath == null)
          fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
       else
          fileChooser.setCurrentDirectory(new File(lastPath));
		FileNameExtensionFilter txtNameExtensionFilter = new FileNameExtensionFilter("text files", "txt", "TXT");
       fileChooser.setFileFilter(txtNameExtensionFilter);
       int returnVal = fileChooser.showSaveDialog(this);
       if (returnVal == JFileChooser.APPROVE_OPTION)
       {
          path = VNFileChooser.filenameWithExtenstionAddedIfNecessary( fileChooser.getSelectedFile(), txtNameExtensionFilter );
          lastPath = path.substring(0, path.lastIndexOf(File.separator));
          try
          {
             PrintWriter writer = new PrintWriter(new File(path));
             for (int i = 0; i < corrDim; i++)
             {
                writer.printf("%20s ", corrVars[i]);
                int s = i/30 + 1;
                for (int j = 0; j < s; j++)
                {
                   int lmax = Math.min(30, i + 1 - 30 * j);
                   for (int l = 0; l < lmax; l++)
                      writer.printf("%7s ", corrVars[30 * j + l].substring(0, Math.min(7,corrVars[30 * j + l].length())));
                   writer.printf("%n                     ");
                   for (int l = 0; l < lmax; l++)
                      writer.printf("%7.4f ", corrArray[i * corrDim + 30 * j + l]);
                   writer.printf("%n                     ");
                }
                writer.println();
             }
             writer.close();
          } catch (Exception e)
          {
             System.out.println("could not print to " + path);
          }
       }
   }//GEN-LAST:event_textItemActionPerformed

   private void sigSliderStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_sigSliderStateChanged
   {//GEN-HEADEREND:event_sigSliderStateChanged
      if (!sigSlider.getValueIsAdjusting())
      {
         int[] cols = new int[]{-1, 0,2,3,4,5,6};
         column = cols[sigSlider.getValue()];
         createImage();
         corelPanel.repaint();
      }
   }//GEN-LAST:event_sigSliderStateChanged

   private void variableChanged()
   {
      if (cmpButton.isSelected())
      {
         dataComponentSelector.setEnabled(false);
         params.setVariableType(Params.COMPONENTS_AS_VARIABLES);
      }
      else if (inField instanceof RegularField && 
               ((RegularField) inField).getDims().length == 2 &&
              rowButton.isSelected())
      {
         dataComponentSelector.setEnabled(true);
         params.setVariableType(Params.ROWS_AS_VARIABLES);
      }
      else if (inField instanceof RegularField && 
               ((RegularField) inField).getDims().length == 2 &&
              colButton.isSelected())
      {
         dataComponentSelector.setEnabled(true);
         params.setVariableType(Params.COLUMNS_AS_VARIABLES);
      }
   }
   private void cmpButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cmpButtonActionPerformed
   {//GEN-HEADEREND:event_cmpButtonActionPerformed
      variableChanged();
   }//GEN-LAST:event_cmpButtonActionPerformed

   private void rowButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_rowButtonActionPerformed
   {//GEN-HEADEREND:event_rowButtonActionPerformed
      variableChanged();
   }//GEN-LAST:event_rowButtonActionPerformed

   private void colButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_colButtonActionPerformed
   {//GEN-HEADEREND:event_colButtonActionPerformed
      variableChanged();
   }//GEN-LAST:event_colButtonActionPerformed

   private void dataComponentSelectorStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_dataComponentSelectorStateChanged
   {//GEN-HEADEREND:event_dataComponentSelectorStateChanged
      params.setComponent(dataComponentSelector.getComponent());
   }//GEN-LAST:event_dataComponentSelectorStateChanged

   private void covarianceButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_covarianceButtonActionPerformed
   {//GEN-HEADEREND:event_covarianceButtonActionPerformed
      params.setCorrelations(correlationButton.isSelected());
   }//GEN-LAST:event_covarianceButtonActionPerformed

   private void correlationButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_correlationButtonActionPerformed
   {//GEN-HEADEREND:event_correlationButtonActionPerformed
      params.setCorrelations(correlationButton.isSelected());
   }//GEN-LAST:event_correlationButtonActionPerformed

   private void blackRadioButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_blackRadioButtonActionPerformed
   {//GEN-HEADEREND:event_blackRadioButtonActionPerformed
      createImage();
      corelPanel.repaint();
   }//GEN-LAST:event_blackRadioButtonActionPerformed

   private void whiteRadioButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_whiteRadioButtonActionPerformed
   {//GEN-HEADEREND:event_whiteRadioButtonActionPerformed
      createImage();
      corelPanel.repaint();
   }//GEN-LAST:event_whiteRadioButtonActionPerformed


   // Variables declaration - do not modify//GEN-BEGIN:variables
   private javax.swing.JRadioButton blackRadioButton;
   private javax.swing.ButtonGroup buttonGroup1;
   private javax.swing.ButtonGroup buttonGroup2;
   private javax.swing.ButtonGroup buttonGroup3;
   private javax.swing.JPanel cloudPanel;
   private javax.swing.JRadioButton cmpButton;
   private javax.swing.JRadioButton colButton;
   private javax.swing.JPanel colormapPanel;
   private javax.swing.JRadioButton correlationButton;
   private javax.swing.JPanel correlationDisplayPanel;
   private javax.swing.JRadioButton covarianceButton;
   private javax.swing.JMenuItem csvItem;
   private pl.edu.icm.visnow.lib.gui.DataComponentSelector dataComponentSelector;
   private javax.swing.JFileChooser fileChooser;
   private javax.swing.JPanel hMargin;
   private javax.swing.JMenu jMenu1;
   private javax.swing.JMenuBar jMenuBar1;
   private javax.swing.JPanel jPanel1;
   private javax.swing.JPanel jPanel2;
   private javax.swing.JMenuItem printItem;
   private javax.swing.JRadioButton rowButton;
   private javax.swing.JSlider sigSlider;
   private javax.swing.JMenuItem textItem;
   private javax.swing.JPanel vMargin;
   private javax.swing.JRadioButton whiteRadioButton;
   // End of variables declaration//GEN-END:variables
}
