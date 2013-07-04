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

package pl.edu.icm.visnow.lib.basic.filters.GaussianFilter;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;

import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.engine.core.InputEgg;
import pl.edu.icm.visnow.engine.core.ModuleCore;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.gui.events.FloatValueModificationEvent;
import pl.edu.icm.visnow.gui.events.FloatValueModificationListener;
import pl.edu.icm.visnow.lib.basic.filters.LocalOperations.LocalOps;
import pl.edu.icm.visnow.lib.types.VNRegularField;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;
import pl.edu.icm.visnow.lib.utils.field.DirectionalFieldSmoothing;

/**
 * @author  Krzysztof S. Nowinski (know@icm.edu.pl)
 * Warsaw University, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class GaussianFilter extends ModuleCore
{

   /**
    * Creates a new instance of CreateGrid
    */
   public static InputEgg[] inputEggs = null;
   public static OutputEgg[] outputEggs = null;
   protected GUI ui = null;
   protected RegularField inField = null;
   protected RegularField outField = null;
   protected Params params;
   protected LocalOps ops = null;
   protected boolean fromUI = false;
   protected int radius, radius1;
   protected FloatValueModificationListener presmoothingProgressListener = new FloatValueModificationListener()
   {

      public void floatValueChanged(FloatValueModificationEvent e)
      {
         setProgress(e.getVal());
      }
   };

   public GaussianFilter()
   {
      parameters = params = new Params();
      params.addChangeListener(new ChangeListener()
      {

         public void stateChanged(ChangeEvent evt)
         {
            fromUI = true;
            startAction();
         }
      });
      SwingInstancer.swingRun(new Runnable()
      {

         public void run()
         {
            ui = new GUI();
         }
      });
      ui.setParams(params);
      setPanel(ui);
   }

   public void update()
   {
      radius = (int) (3 * params.getSigma());
      if (radius < 1)
         radius = 1;
      radius1 = (int) (3 * params.getSigma1());
      if (radius1 < 1)
         radius1 = 1;
      float[] kernel = null, kernel1 = null, kernelDist = null, kernelDist1 = null;
      int[] dims = inField.getDims();
      outField = new RegularField(dims);
      outField.setAffine(inField.getAffine());
      if (inField.getAllCoords() != null)
         outField.setCoords(inField.getAllCoords());
      float[] outData = null;
      float[] outData1 = null;
      for (int i = 0; i < dims.length; i++)
         if (radius >= dims[i] / 2)
            radius = dims[i] / 2 - 1;
      kernel = new float[2 * radius + 1];
      kernelDist = new float[2 * radius + 1];
      float s = 0;
      float r = 4.f / (params.getSigma() * params.getSigma());
      for (int i = 0; i <= radius; i++)
         kernel[radius + i] = kernel[radius - i] = (float) Math.exp(-i * i * r);
      for (int i = 0; i < kernel.length; i++)
         s += kernel[i];
      s = 1 / s;
      for (int i = 0; i < kernel.length; i++)
      {
         kernel[i] *= s;
         if (i == 0)
            kernelDist[i] = 0;
         else
            kernelDist[i] = kernelDist[i - 1] + kernel[i - 1];
      }
      if (params.isBandPass())
      {
         for (int i = 0; i < dims.length; i++)
            if (radius1 >= dims[i] / 2)
               radius1 = dims[i] / 2 - 1;
         kernel1 = new float[2 * radius1 + 1];
         kernelDist1 = new float[2 * radius1 + 1];
         s = 0;
         r = 4.f / (params.getSigma1() * params.getSigma1());
         for (int i = 0; i <= radius1; i++)
            kernel1[radius1 + i] = kernel1[radius1 - i] = (float) Math.exp(-i * i * r);
         for (int i = 0; i < kernel1.length; i++)
            s += kernel1[i];
         s = 1 / s;
         for (int i = 0; i < kernel1.length; i++)
         {
            kernel1[i] *= s;
            if (i == 0)
               kernelDist1[i] = 0;
            else
               kernelDist1[i] = kernelDist1[i - 1] + kernel1[i - 1];
         }
      }

      for (int component = 0; component < inField.getNData(); component++)
      {
         if (inField.getData(component).getVeclen() != 1)
            continue;
         if (inField.getData(component).getType() != DataArray.FIELD_DATA_FLOAT)
            outData = inField.getData(component).getFData();
         else
         {
            float[] inData = inField.getData(component).getFData();
            outData = new float[inField.getData(0).getNData()];
            System.arraycopy(inData, 0, outData, 0, inData.length);
         }
         for (int direction = 0; direction < dims.length; direction++)
         {
            int nThreads = params.getNThreads();
            Thread[] workThreads = new Thread[nThreads];
            DirectionalFieldSmoothing[] smoothers = new DirectionalFieldSmoothing[nThreads];
            for (int i = 0; i < workThreads.length; i++)
            {
               smoothers[i] = new DirectionalFieldSmoothing(direction, inField.getDims(), outData, kernel, kernelDist, radius, params.getNThreads(), i);
               if (i == 0)
                  smoothers[i].addFloatValueModificationListener(presmoothingProgressListener);
               workThreads[i] = new Thread(smoothers[i]);
               workThreads[i].start();
            }
            for (int i = 0; i < workThreads.length; i++)
               try
               {
                  workThreads[i].join();
               } catch (Exception e)
               {
               }
         }
         if (params.isBandPass())
         {
            if (inField.getData(component).getType() != DataArray.FIELD_DATA_FLOAT)
               outData1 = inField.getData(component).getFData();
            else
            {
               float[] inData = inField.getData(component).getFData();
               outData1 = new float[inField.getData(0).getNData()];
               System.arraycopy(inData, 0, outData1, 0, inData.length);
            }
            for (int direction = 0; direction < dims.length; direction++)
            {
               int nThreads = params.getNThreads();
               Thread[] workThreads = new Thread[nThreads];
               DirectionalFieldSmoothing[] smoothers = new DirectionalFieldSmoothing[nThreads];
               for (int i = 0; i < workThreads.length; i++)
               {
                  smoothers[i] = new DirectionalFieldSmoothing(direction, inField.getDims(), outData1, kernel1, kernelDist1, radius1, params.getNThreads(), i);
                  if (i == 0)
                     smoothers[i].addFloatValueModificationListener(presmoothingProgressListener);
                  workThreads[i] = new Thread(smoothers[i]);
                  workThreads[i].start();
               }
               for (int i = 0; i < workThreads.length; i++)
                  try
                  {
                     workThreads[i].join();
                  } catch (Exception e)
                  {
                  }
            }
         }

         switch (inField.getData(component).getType())
         {
            case DataArray.FIELD_DATA_BYTE:
               byte[] outBData = new byte[outData.length];
               for (int i = 0; i < outData.length; i++)
                  outBData[i] = (byte) (0xff & (int) outData[i]);
               outField.addData(DataArray.create(outBData, inField.getData(component).getVeclen(), "smoothed " + inField.getData(component).getName()));
               if (params.isHiPass())
               {
                  byte[] inBData = inField.getData(component).getBData();
                  short[] outSHData = new short[outData.length];
                  if (params.isHiAbs())
                     for (int i = 0; i < outData.length; i++)
                        outSHData[i] = (short) Math.abs((0xff & inBData[i]) - (0xff & outBData[i]));
                  else
                     for (int i = 0; i < outData.length; i++)
                        outSHData[i] = (short) ((0xff & inBData[i]) - (0xff & outBData[i]));
                  outField.addData(DataArray.create(outSHData, inField.getData(component).getVeclen(), "hi_pass " + inField.getData(component).getName()));
               }
               if (params.isBandPass())
               {
                  short[] outSBData = new short[outData.length];
                  if (params.isBandAbs())
                     for (int i = 0; i < outData.length; i++)
                        outSBData[i] = (short) Math.abs(outData1[i] - outData[i]);
                  else
                     for (int i = 0; i < outData.length; i++)
                        outSBData[i] = (short) (outData1[i] - outData[i]);
                  outField.addData(DataArray.create(outSBData, inField.getData(component).getVeclen(), "band_pass " + inField.getData(component).getName()));
               }
               break;
            case DataArray.FIELD_DATA_SHORT:
               short[] outSData = new short[outData.length];
               for (int i = 0; i < outData.length; i++)
                  outSData[i] = (short) outData[i];
               outField.addData(DataArray.create(outSData, inField.getData(component).getVeclen(), "smoothed " + inField.getData(component).getName()));
               if (params.isHiPass())
               {
                  short[] inSData = inField.getData(component).getSData();
                  short[] outSHData = new short[outData.length];
                  if (params.isHiAbs())
                     for (int i = 0; i < outData.length; i++)
                        outSHData[i] = (short) Math.abs(inSData[i] - outSData[i]);
                  else
                     for (int i = 0; i < outData.length; i++)
                        outSHData[i] = (short) (inSData[i] - outSData[i]);
                  outField.addData(DataArray.create(outSHData, inField.getData(component).getVeclen(), "hi_pass " + inField.getData(component).getName()));
               }
               if (params.isBandPass())
               {
                  short[] outSBData = new short[outData.length];
                  if (params.isBandAbs())
                     for (int i = 0; i < outData.length; i++)
                        outSBData[i] = (short) Math.abs(outData1[i] - outData[i]);
                  else
                     for (int i = 0; i < outData.length; i++)
                        outSBData[i] = (short) (outData1[i] - outData[i]);
                  outField.addData(DataArray.create(outSBData, inField.getData(component).getVeclen(), "band_pass " + inField.getData(component).getName()));
               }
               break;
            case DataArray.FIELD_DATA_INT:
               int[] outIData = new int[outData.length];
               for (int i = 0; i < outData.length; i++)
                  outIData[i] = (int) outData[i];
               outField.addData(DataArray.create(outIData, inField.getData(component).getVeclen(), "smoothed " + inField.getData(component).getName()));
               if (params.isHiPass())
               {
                  int[] inIData = inField.getData(component).getIData();
                  int[] outIHData = new int[outData.length];
                  if (params.isHiAbs())
                     for (int i = 0; i < outData.length; i++)
                        outIHData[i] = Math.abs(inIData[i] - outIData[i]);
                  else
                     for (int i = 0; i < outData.length; i++)
                        outIHData[i] = inIData[i] - outIData[i];
                  outField.addData(DataArray.create(outIHData, inField.getData(component).getVeclen(), "hi_pass " + inField.getData(component).getName()));
               }
               if (params.isBandPass())
               {
                  int[] outIBData = new int[outData.length];
                  if (params.isBandAbs())
                     for (int i = 0; i < outData.length; i++)
                        outIBData[i] = (int) Math.abs(outData1[i] - outData[i]);
                  else
                     for (int i = 0; i < outData.length; i++)
                        outIBData[i] = (int) (outData1[i] - outData[i]);
                  outField.addData(DataArray.create(outIBData, inField.getData(component).getVeclen(), "band_pass " + inField.getData(component).getName()));
               }
               break;
            case DataArray.FIELD_DATA_FLOAT:
               outField.addData(DataArray.create(outData, inField.getData(component).getVeclen(), "smoothed " + inField.getData(component).getName()));
               if (params.isHiPass())
               {
                  float[] inFData = inField.getData(component).getFData();
                  float[] outFHData = new float[outData.length];
                  if (params.isHiAbs())
                     for (int i = 0; i < outData.length; i++)
                        outFHData[i] = Math.abs(inFData[i] - outData[i]);
                  else
                     for (int i = 0; i < outData.length; i++)
                        outFHData[i] = inFData[i] - outData[i];
                  outField.addData(DataArray.create(outFHData, inField.getData(component).getVeclen(), "hi_pass " + inField.getData(component).getName()));
               }
               if (params.isBandPass())
               {
                  float[] outFBData = new float[outData.length];
                  if (params.isBandAbs())
                     for (int i = 0; i < outData.length; i++)
                        outFBData[i] = Math.abs(outData1[i] - outData[i]);
                  else
                     for (int i = 0; i < outData.length; i++)
                        outFBData[i] = outData1[i] - outData[i];
                  outField.addData(DataArray.create(outFBData, inField.getData(component).getVeclen(), "band_pass " + inField.getData(component).getName()));
               }
               break;
            case DataArray.FIELD_DATA_DOUBLE:
               outField.addData(DataArray.create(outData, inField.getData(component).getVeclen(), "smoothed " + inField.getData(component).getName()));
               if (params.isHiPass())
               {
                  double[] inDData = inField.getData(component).getDData();
                  float[] outFHData = new float[outData.length];
                  if (params.isHiAbs())
                     for (int i = 0; i < outData.length; i++)
                        outFHData[i] = (float) Math.abs(inDData[i] - outData[i]);
                  else
                     for (int i = 0; i < outData.length; i++)
                        outFHData[i] = (float) (inDData[i] - outData[i]);
                  outField.addData(DataArray.create(outFHData, inField.getData(component).getVeclen(), "hi_pass " + inField.getData(component).getName()));
               }
               if (params.isBandPass())
               {
                  float[] outFBData = new float[outData.length];
                  if (params.isBandAbs())
                     for (int i = 0; i < outData.length; i++)
                        outFBData[i] = Math.abs(outData1[i] - outData[i]);
                  else
                     for (int i = 0; i < outData.length; i++)
                        outFBData[i] = outData1[i] - outData[i];
                  outField.addData(DataArray.create(outFBData, inField.getData(component).getVeclen(), "hi_pass " + inField.getData(component).getName()));
               }
               break;
         }
      }
      setOutputValue("outField", new VNRegularField(outField));
   }

   @Override
   public void onActive()
   {
      if (!fromUI)
      {
         if (getInputFirstValue("inField") == null)
            return;
         inField = ((VNRegularField) getInputFirstValue("inField")).getField();
         if (params.isAuto())
            update();
      } else
      {
         fromUI = false;
         if (inField == null)
            return;
         update();
      }
   }
}
