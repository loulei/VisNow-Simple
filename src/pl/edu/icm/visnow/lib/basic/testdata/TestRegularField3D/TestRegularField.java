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

package pl.edu.icm.visnow.lib.basic.testdata.TestRegularField3D;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.lib.templates.visualization.modules.RegularOutFieldVisualizationModule;
import pl.edu.icm.visnow.lib.types.VNGeometryObject;
import pl.edu.icm.visnow.lib.types.VNRegularField;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;

/**
 * @author  Krzysztof S. Nowinski (know@icm.edu.pl)
 * Warsaw University, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class TestRegularField extends RegularOutFieldVisualizationModule
{
   public static OutputEgg[] outputEggs = null;
   private byte[] data0;
   private float[] data1, data2, data3, data5, data61, data62, data64, data68;

   /**
    * Creates a new instance of TestGeometryObject
    */
   protected GUI computeUI = null;

   public TestRegularField()
   {
      SwingInstancer.swingRun(new Runnable()
      {
         public void run()
         {
            computeUI = new GUI();
         }
      });
      computeUI.addChangeListener(new ChangeListener()
      {
         public void stateChanged(ChangeEvent evt)
         {
            startAction();
         }
      });
      ui.addComputeGUI(computeUI);
      setPanel(ui);
   }

   public static boolean isGenerator()
   {
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
         for (int k = iThread; k < n; k += nThreads)
         {
            if (iThread == 0)
               setProgress((float) k / n);
            int l = k * n * n;
            for (int j = 0; j < n; j++)
               for (int i = 0; i < n; i++, l++)
               {
                  double u = (2. * i - n) / n;
                  double v = (2. * j - n) / n;
                  double w = (2. * k - n) / n;
                  data0[l] = (byte)(0xff & (int)( 64 * (3 + (Math.exp(-1.6 * (2 * (u - .3) * (u - .3) + (v - .6) * (v - .6) + (w - .6) * (w - .6)))
                          - 2 * Math.exp(-.5 * ((u + .6) * (u + .6) + 2 * (v + .4) * (v + .4) + (w - .4) * (w - .6)))
                          - Math.exp(-.8 * ((u + .6) * (u + .6) + (v - .6) * (v - .6) + 2 * (w + .2) * (w + .2)))
                          + Math.exp(-1.4 * ((u - .3) * (u - .6) + (v + .6) * (v + .6) + (w + .6) * (w + .6)))))));
                  data1[l] = (float) (Math.cos(2 * u - 1) * Math.sin(4 * v * w) * Math.cos(3 * u * v * w) + (u * u + v * v + w) / 2);
                  double r = Math.sqrt(u * u + v * v);
                  double s = 1 / (.1 + (r - .5f) * (r - .5f) + w * w);
                  data5[3 * l] = (float) (-v * s - u / 2 - w / 4);
                  data5[3 * l + 1] = (float) (u * s - v / 2);
                  data5[3 * l + 2] = -(float) (w + u / 5 + .2 / (.05 + u * u + v * v));
                  data2[l] = 0;
                  if (u * u + v * v + (w + .5f) * (w + .5f) / 4 < .5 && w + .5 > 0)
                     data2[l] = (float) w + .5f;
                  double r0 = .1234567;
                  double r000 = Math.sqrt((u - r0) * (u - r0) + (v - r0) * (v - r0) + (w - r0) * (w - r0));
                  double r001 = Math.sqrt((u - r0) * (u - r0) + (v - r0) * (v - r0) + (w + r0) * (w + r0));
                  double r010 = Math.sqrt((u - r0) * (u - r0) + (v + r0) * (v + r0) + (w - r0) * (w - r0));
                  double r011 = Math.sqrt((u - r0) * (u - r0) + (v + r0) * (v + r0) + (w + r0) * (w + r0));
                  double r100 = Math.sqrt((u + r0) * (u + r0) + (v - r0) * (v - r0) + (w - r0) * (w - r0));
                  double r101 = Math.sqrt((u + r0) * (u + r0) + (v - r0) * (v - r0) + (w + r0) * (w + r0));
                  double r110 = Math.sqrt((u + r0) * (u + r0) + (v + r0) * (v + r0) + (w - r0) * (w - r0));
                  double r111 = Math.sqrt((u + r0) * (u + r0) + (v + r0) * (v + r0) + (w + r0) * (w + r0));
                  data61[l] = (float) (1 / (.5 + r000));
                  data62[l] = (float) (1 / (.5 + r000) - 1 / (.5 + r001));
                  data64[l] = (float) (1 / (.5 + r000) - 1 / (.5 + r001) - 1 / (.5 + r010) + 1 / (.5 + r011));
                  data68[l] = (float) (1 / (.5 + r000) - 1 / (.5 + r001) - 1 / (.5 + r010) + 1 / (.5 + r011) -
                                       1 / (.5 + r100) + 1 / (.5 + r101) + 1 / (.5 + r110) - 1 / (.5 + r111));
                  float x1 = 3 * (2.f * i - n) / n;
                  float x2 = 3 * (2.f * j - n) / n;
                  float x3 = 3 * (2.f * k - n) / n;
                  data3[3 * l] = -x2 - x1 * x3;
                  data3[3 * l + 1] = x1 - x2 * x3;
                  data3[3 * l + 2] = .5f * (x1 * x1 + x2 * x2 - x3 * x3 - 1);
               }
         }
      }
   }

   private void createTestRegularField(int n, int nThreads)
   {

      outField = new RegularField();
      int[] dims = new int[]
      {
         n, n, n
      };
      outField.setDims(dims);
      outField.setNSpace(3);
      float[][] points =
      {
         {
            -.5f, -.5f, -.5f
         },
         {
            .5f, .5f, .5f
         }
      };
      outField.setPts(points);
      data0 = new byte[n * n * n];
      data1 = new float[n * n * n];
      data2 = new float[n * n * n];
      data3 = new float[3 * n * n * n];
      data5 = new float[3 * n * n * n];
      data61 = new float[n * n * n];
      data62 = new float[n * n * n];
      data64 = new float[n * n * n];
      data68 = new float[n * n * n];

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
      outField.addData(DataArray.create(data1, 1, "trig function"));
      DataArray da = DataArray.create(data0, 1, "gaussians");
      da.setPhysMax(4);
      da.setPhysMin(-3);
      outField.addData(da);
      da = DataArray.create(data61, 1, "multipole");
      da.addData(data62, 1);
      da.addData(data64, 2);
      da.addData(data68, 3);
      outField.addData(da);
      outField.addData(DataArray.create(data2, 1, "semielipsoid"));
      outField.addData(DataArray.create(data5, 3, "vortex"));
      outField.addData(DataArray.create(data3, 3, "Hopf"));
      da = DataArray.create(data3, 3, "time vector field");
      da.addData(data5, 1);
      outField.addData(da);
      outField.setCurrentTime(0);
   }

   @Override
   public void onInitFinished()
   {
      outObj.getGeometryObj().setUserData(getName());
      setOutputValue("outObj", new VNGeometryObject(outObj));
      createTestRegularField(computeUI.getResolution(), computeUI.getNThreads());
      prepareOutputGeometry();
      show();
      setOutputValue("outField", new VNRegularField(outField));
   }

   @Override
   public void onActive()
   {
        createTestRegularField(computeUI.getResolution(), computeUI.getNThreads());
        prepareOutputGeometry();
        show();
        setOutputValue("outField", new VNRegularField(outField));
   }
}

