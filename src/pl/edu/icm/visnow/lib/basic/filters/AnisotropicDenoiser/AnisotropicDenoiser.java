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

package pl.edu.icm.visnow.lib.basic.filters.AnisotropicDenoiser;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.engine.core.InputEgg;
import pl.edu.icm.visnow.engine.core.ModuleCore;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.gui.events.FloatValueModificationEvent;
import pl.edu.icm.visnow.gui.events.FloatValueModificationListener;
import pl.edu.icm.visnow.lib.types.VNRegularField;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;
import pl.edu.icm.visnow.lib.utils.field.FieldSmoothDown;

/**
 * @author  Krzysztof S. Nowinski (know@icm.edu.pl)
 * Warsaw University, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class AnisotropicDenoiser extends ModuleCore
{

   /**
    * Creates a new instance of AnisotropicDenoiser
    */
   public static InputEgg[] inputEggs = null;
   public static OutputEgg[] outputEggs = null;
   protected GUI ui = null;
   protected RegularField inField = null;
   protected RegularField anisotropyField = null;
   protected RegularField outField = null;
   protected Params params;
   protected AbstractAnisotropicWeightedMedianCompute medianCompute = new AnisotropicWeightedMedianCompute();
   protected AbstractAnisotropicWeightedAverageCompute averageCompute = new AnisotropicWeightedAverageCompute();
   protected FloatValueModificationListener presmoothingProgressListener = new FloatValueModificationListener()
              {
                 public void floatValueChanged(FloatValueModificationEvent e)
                 {
                    setProgress(e.getVal());
                 }
              };
   protected float[] aniso = null;

   public AnisotropicDenoiser()
   {
      parameters = params = new Params();
      SwingInstancer.swingRun(new Runnable()
      {
         public void run()
         {
            ui = new GUI();
         }
      });
      ui.setParams(params);
      setPanel(ui);
      params.addChangeListener(new ChangeListener()
      {
         public void stateChanged(ChangeEvent evt)
         {
            startAction();
         }
      });
      medianCompute.addFloatValueModificationListener(
              new FloatValueModificationListener()
              {
                 public void floatValueChanged(FloatValueModificationEvent e)
                 {
                    setProgress(e.getVal());
                 }
              });
      averageCompute.addFloatValueModificationListener(
              new FloatValueModificationListener()
              {
                 public void floatValueChanged(FloatValueModificationEvent e)
                 {
                    setProgress(e.getVal());
                 }
              });
   }

   public void runUpdate()
   {
      switch (params.getMethod())
      {
         case Params.AVERAGE:
            switch (params.getCore())
            {
               case CPU:
                  RegularField tmpField = inField;
                  for (int i = 0; i < params.getIterations(); i++)
                     tmpField = averageCompute.compute(tmpField, anisotropyField, params);
                  outField = tmpField;
                  break;
               case GPU:
//                           ComputeAveragesGPU gpuCore = new ComputeAveragesGPU();
//                           outField = gpuCore.compute(inField, anisotropyField, params);
                  break;
            }
            break;
         case Params.MEDIAN:
            outField = medianCompute.compute(inField, anisotropyField, params);
            break;
         default:
            outField = null;
      }
      if (outField == null)
         return;
      if (inField.getData(0).getUserData() != null)
         outField.getData(0).setUserData(inField.getData(0).getUserData());
      outField.setAffine(inField.getAffine());
      AnisotropicDenoiser.this.setOutputValue("outField", new VNRegularField(outField));
   }

   private void presmooth()
   {
      anisotropyField = FieldSmoothDown.smoothDownToFloat(inField, 1,
                       (float)params.getPresmoothRadius(), params.getNThreads());
      params.setPresmooth(false);
      ui.setFieldSchemas(inField.getSchema(), anisotropyField.getSchema());
   }

   private void computeWeights()
   {
      if (inField == null)
         return;
      int radius = params.getRadius();
      float slope = params.getSlope() * params.getSlope();
      float slope1 = params.getSlope1() * params.getSlope1();
      int[] dims = inField.getDims();

      float[][] inPts = inField.getExtents();

      for (int i = 0; i < dims.length; i++)
         if (dims[i] < 2 * radius + 2)
            return;
      RegularField weights = new RegularField(dims, inPts);
      for (int nComp = 0; nComp < params.getComponentsNumber(); nComp++)
      {
         int component = params.getComponent(nComp);
         int anisotropyComponent = params.getAnisotropyComponent(nComp);
         DataArray dataArr = inField.getData(component);
         int anisoVlen = 0;
         if (anisotropyComponent >= 0)
         {
            DataArray anisoArr = anisotropyField.getData(anisotropyComponent);
            anisoVlen = anisoArr.getVeclen();
            if (anisoVlen == 1 || anisoVlen == dims.length)
               aniso = anisoArr.getFData();
         }
         float[] data = new float[inField.getNNodes()];
         for (int i = 0; i < data.length; i++)
            data[i] = 0;
         switch (dims.length)
         {
            case 3:
               for (int k0 = radius; k0 < dims[2] - radius; k0 += 2 * radius + 3)
               {
                  setProgress((1.f * k0) / dims[2]);
                  for (int j0 = radius; j0 < dims[1] - radius; j0 += 2 * radius + 3)
                     for (int i0 = radius; i0 < dims[0] - radius; i0 += 2 * radius + 3)
                     {
                        int n0 = (k0 * dims[1] + j0) * dims[0] + i0;
                        if (aniso != null)
                           if (anisoVlen > 1)
                              for (int k = -radius; k <= radius; k++)
                                 for (int j = -radius; j <= radius; j++)
                                    for (int i = -radius, n = ((k + k0) * dims[1] + j + j0) * dims[0] + i0 - radius, p = k * k + j * j;
                                            i <= radius; i++, n++)
                                    {
                                       float s = k * aniso[n0 * anisoVlen + 2] +
                                               j * aniso[n0 * anisoVlen + 1] +
                                               i * aniso[n0 * anisoVlen];
                                       data[n] = (float) (Math.exp(-s * s / slope1 - (p + i * i) / slope));
                                    }
                           else
                              for (int k = -radius; k <= radius; k++)
                                 for (int j = -radius; j <= radius; j++)
                                    for (int i = -radius, n = ((k + k0) * dims[1] + j + j0) * dims[0] + i0 - radius, p = k * k + j * j;
                                            i <= radius; i++, n++)
                                    {
                                       float s = aniso[n0] - aniso[n];
                                       data[n] = (float) (Math.exp(-s * s / slope1 - (p + i * i) / slope));
                                    }
                        else
                           for (int k = -radius; k <= radius; k++)
                              for (int j = -radius; j <= radius; j++)
                                 for (int i = -radius, n = ((k + k0) * dims[1] + j + j0) * dims[0] + i0 - radius, p = k * k + j * j;
                                         i <= radius; i++, n++)
                                    data[n] = (float) (Math.exp(-(p + i * i) / slope));
                     }
               }
               break;
            case 2:
               for (int j0 = radius; j0 < dims[1] - radius; j0 += 2 * radius + 3)
                  for (int i0 = radius; i0 < dims[0] - radius; i0 += 2 * radius + 3)
                  {
                     int n0 = j0 * dims[0] + i0;
                     if (aniso != null)
                        if (anisoVlen > 1)
                           for (int j = -radius; j <= radius; j++)
                              for (int i = -radius, n = (j + j0) * dims[0] + i0 - radius, p = j * j; i <= radius; i++, n++)
                              {
                                 float s = j * aniso[n0 * anisoVlen + 1] +
                                         i * aniso[n0 * anisoVlen];
                                 data[n] = (float) (Math.exp(-s * s / slope1 - (p + i * i) / slope));
                              }
                        else
                           for (int j = -radius; j <= radius; j++)
                              for (int i = -radius, n = (j + j0) * dims[0] + i0 - radius, p = j * j; i <= radius; i++, n++)
                              {
                                 float s = aniso[n0] - aniso[n];
                                 data[n] = (float) (Math.exp(-s * s / slope1 - (p + i * i) / slope));
                              }
                     else
                        for (int j = -radius; j <= radius; j++)
                           for (int i = -radius, n = (j + j0) * dims[0] + i0 - radius, p = j * j; i <= radius; i++, n++)
                              data[n] = (float) (Math.exp(-(p + i * i) / slope));
                  }
               break;
         }
         weights.addData(DataArray.create(data, 1, dataArr.getName()));
      }
      setOutputValue("weights", new VNRegularField(weights));
   }

   @Override
   public void onActive()
   {
      if (params.isCompute())
      {
         runUpdate();
         params.setCompute(false);
      } else if (params.isPresmooth())
      {
         presmooth();
         params.setPresmooth(false);
      } else if (params.isComputeWeights())
      {
         computeWeights();
         params.setComputeWeights(false);
      } else
         try
         {
            if (getInputFirstValue("inField") == null)
               return;
            inField = ((VNRegularField) getInputFirstValue("inField")).getField();
            anisotropyField = null;
            if (getInputFirstValue("anisotropyField") != null)
            {
               anisotropyField = ((VNRegularField) getInputFirstValue("anisotropyField")).getField();
               if (anisotropyField == null || anisotropyField.getDims() == null ||
                       anisotropyField.getDims().length != inField.getDims().length)
                  for (int i = 0; i < anisotropyField.getDims().length; i++)
                     if (anisotropyField.getDims()[i] != inField.getDims()[i])
                     {
                        anisotropyField = null;
                        break;
                     }
            }
            if (anisotropyField == null)
               ui.setFieldSchemas(inField.getSchema(), null);
            else
               ui.setFieldSchemas(inField.getSchema(), anisotropyField.getSchema());
         } catch (Exception ex)
         {
//            pl.edu.icm.visnow.engine.errors.Displayer.display(toString(),"Error during action performing.",ex);
         }
   }
}
