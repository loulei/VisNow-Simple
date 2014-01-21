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

package pl.edu.icm.visnow.lib.basic.testdata.TestRegularField2D;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.datasets.dataarrays.BitArray;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.lib.templates.visualization.modules.OutFieldVisualizationModule;
import pl.edu.icm.visnow.lib.types.VNRegularField;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;
import pl.edu.icm.visnow.system.main.VisNow;

/**
 * @author  Krzysztof S. Nowinski (know@icm.edu.pl)
 * Warsaw University, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class TestRegularField2D extends OutFieldVisualizationModule
{

   public static OutputEgg[] outputEggs = null;
   private int lastD = 50;
   private double[][] d = new double[256][5];
   private float[] data0, data1, data2, grad, tdata, datax;
   private byte[] bData;
   private short[] sData;
   private int[] iData;
   private float[] fReData, fImData;
   private String[] strData;
   private BitArray bitData;
           
   
  /**
    * Creates a new instance of TestGeometryObject
    */
   protected GUI computeUI = null;
   public TestRegularField2D()
   {
      SwingInstancer.swingRunAndWait(new Runnable()
      {
         @Override
         public void run()
         {
            computeUI = new GUI();
         }
      });
      ui.addComputeGUI(computeUI);
      setPanel(ui);
      computeUI.addChangeListener(new ChangeListener()
      {
         @Override
         public void stateChanged(ChangeEvent evt)
         {
            startAction();
         }
      });
   }

   @Override
   public boolean isGenerator() {
      return true;
   }


   class Compute implements Runnable
   {
      int nThreads;
      int n;
      int iThread;

      public Compute(int nThreads, int size, int iThread)
      {

         this.nThreads = nThreads;
         this.iThread = iThread;
         this.n = size;
      }

      public void run()
      {
         for (int j = iThread; j < n; j += nThreads)
         {
            if (iThread == 0)
               setProgress((float) j / (n - 1));
            for (int i = 0, l = n * j; i < n; i++, l++)
            {
               double u = (2. * i - n) / n;
               double v = (2. * j - n) / n;
               double s = 0, t = 0, w = 0, sg[] = new double[]{0, 0};
               for (int k = 0; k < d.length; k++)
               {
                  double r = d[k][4] * Math.exp(-(u - d[k][0]) * (u - d[k][0]) / d[k][2]
                                                -(v - d[k][1]) * (v - d[k][1]) / d[k][3]);
                  s += r;
                  sg[0] += -2 * r * (u-d[k][0]) / d[k][2];
                  sg[1] += -2 * r * (v-d[k][1]) / d[k][3];
                  if (k > 1)
                     t += r;
                  if (k > 3)
                     w += r;
               }
               data0[l] = (float) s;
               data1[l] = (float) t;
               data2[l] = (float) w;
               grad[2 * l]     = (float)sg[0];
               grad[2 * l + 1] = (float)sg[1];
               datax[l] = 5 * (float)(Math.sin(5 * u) + Math.cos(5 * v) + 2);
               sData[l] = (short) s;
               iData[l] = (int) s;
               fReData[l] = (float) s;
               fImData[l] = (float) t;
               strData[l] = "v="+(float)s;
               bitData.setValueAtIndex(l, s > 10.0);
            }
         }
      }
   }

   private void createTestRegularField(int n, int nThreads)
   {
      if (n < 20)
         n = 20;
      for (int i = 0; i < d.length; i++)
      {
         d[i][0] = 2 * Math.random() - 1;
         d[i][1] = 2 * Math.random() - 1;
         if (i == 0)
         {
            d[i][2] = Math.random()+.5;
            d[i][3] = Math.random()+.5;
            d[i][4] = 10;
         }
         else if (i < 8)
         {
            d[i][2] = .2*(Math.random()+.4);
            d[i][3] = .2*(Math.random()+.4);
            d[i][4] = Math.random()*3;
         }
         else
         {
            d[i][2] = .05*(Math.random()+.1);
            d[i][3] = .05*(Math.random()+.1);
            d[i][4] = Math.random();
         }
      }
      int[] dims = new int[]{n,n};
      outField = new RegularField(dims);
      outRegularField = (RegularField)outField;
      float[][] points =
      {{-.5f, -.5f, 0}, {.5f, .5f, 0}};
      outRegularField.setExtents(points);
      data0 = new float[n * n];
      data1 = new float[n * n];
      data2 = new float[n * n];
      datax = new float[n * n];
      grad  = new float[2 * n * n];
      bData = new byte[n * n];
      sData = new short[n * n];
      iData = new int[n * n];
      fReData = new float[n * n];
      fImData = new float[n * n];
      strData = new String[n * n];
      bitData = new BitArray(n * n);
      Thread[] workThreads = new Thread[nThreads];
      for (int i = 0; i < workThreads.length; i++)
      {
         workThreads[i] = new Thread(new Compute(nThreads, n, i));
         workThreads[i].start();
      }
      for (int i = 0; i < workThreads.length; i++)
         try
         {
            workThreads[i].join();
         } catch (Exception e)
         {
         }
      setProgress(1);
      DataArray bdta = DataArray.create(data0, 1, "gaussians");
      outRegularField.addData(bdta);
      outRegularField.addData(DataArray.create(data1, 1, "gaussians1"));
      outRegularField.addData(DataArray.create(data2, 1, "gaussians2"));
      outRegularField.addData(DataArray.create(grad,  2, "gaussians_gradient"));
      
      float min = bdta.getMinv();
      float max = bdta.getMaxv();
      float d = 255 / (max - min);
      for (int i = 0; i < data0.length; i++)
         bData[i] = (byte)(0xff & (int)(0.5 * d * (data0[i] - min) + 20));
      bdta = DataArray.create(bData,  1, "byte_gaussians");
      bdta.setPhysMin(0.3f);
      bdta.setPhysMax(0.7f);
      outRegularField.addData(bdta);
      
      outRegularField.addData(DataArray.create(sData,  1, "short_gaussians"));
      outRegularField.addData(DataArray.create(iData,  1, "int_gaussians"));
      outRegularField.addData(DataArray.create(fReData, fImData, 1, "complex_gaussians"));
      outRegularField.addData(DataArray.create(strData,  1, "string_gaussians"));
      outRegularField.addData(DataArray.create(bitData,  1, "logic_gaussians"));
      bdta.addData(datax, 1);
      outRegularField.setCurrentTime(outRegularField.getStartTime());
   }

   @Override
   public void onInitFinishedLocal()
   {
      createTestRegularField(computeUI.getResolution(), VisNow.availableProcessors());
      lastD = computeUI.getResolution();
      setOutputValue("outField", new VNRegularField(outRegularField));
      prepareOutputGeometry();
      show();
   }


   @Override
   public void onActive()
   {
      if (lastD == computeUI.getResolution())
         return;
      lastD = computeUI.getResolution();
      createTestRegularField(computeUI.getResolution(), VisNow.availableProcessors());
      setOutputValue("outField", new VNRegularField(outRegularField));
      prepareOutputGeometry();
      show();
   }
}

