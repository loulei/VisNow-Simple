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

package pl.edu.icm.visnow.lib.basic.filters.SplineInterpolation;

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
import pl.edu.icm.visnow.lib.utils.numeric.splines.SplineUtilities;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class SplineInterpolation extends ModuleCore
{
   public static InputEgg[] inputEggs = null;
   public static OutputEgg[] outputEggs = null;
   private GUI ui = new GUI();
   protected Params params;
   protected RegularField inField = null;
   protected boolean fromGUI = false;
   private SplineUtilities spUtil = null;

   public SplineInterpolation()
   {
      parameters = params = new Params();
      params.addChangeListener(new ChangeListener()
      {
         public void stateChanged(ChangeEvent evt)
         {
            fromGUI = true;
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
   
   protected RegularField interpolatedField()
   {
      RegularField outField = null;
      int density = params.getDensity();
      int[] dims = inField.getDims();
      int[] outDims = null;
      int[] interpolatedComponents = params.getComponents();
      switch (params.getType())
      {
      case Params.DENSITY:
         outDims = new int[dims.length];
         for (int i = 0; i < dims.length; i++)
            outDims[i] = density*(dims[i]  - 1) +1;
         outField = new  RegularField(outDims);
         if (inField.getCoords() != null)
         {
            spUtil = new SplineUtilities(dims, inField.getNSpace(), density, inField.getCoords(), params.getNThreads());
            spUtil.addFloatValueModificationListener(
                    new FloatValueModificationListener()
                    {

                       public void floatValueChanged(FloatValueModificationEvent e)
                       {
                          setProgress(e.getVal());
                       }
                    });
            outField.setCoords(spUtil.splineInterpolate());
         }
         else
         {
            float[][] inAffine = inField.getAffine();
            float[][] outAffine = new float[4][3];
            for (int i = 0; i < 3; i++)
               for (int j = 0; j < 3; j++)
                  outAffine[i][j] = inAffine[i][j] / density;
            for (int i = 0; i < 3; i++)
               outAffine[3][i] = inAffine[3][i];
            outField.setAffine(outAffine);
         }      
         for (int n = 0; n < interpolatedComponents.length; n++)
         {
            DataArray da = inField.getData(interpolatedComponents[n]);
            if (!da.isSimpleNumeric())
               continue;
            spUtil = new SplineUtilities(dims, da.getVeclen(), density, da.getFData(), params.getNThreads());
            spUtil.addFloatValueModificationListener(
                    new FloatValueModificationListener()
                    {
                       public void floatValueChanged(FloatValueModificationEvent e)
                       {
                          setProgress(e.getVal());
                       }
                    });
            outField.addData(DataArray.create(spUtil.splineInterpolate(), da.getVeclen(), da.getName()));
         }
         break;
      case Params.NEWDIMS:
         outDims = params.getNewDims();
         outField = new  RegularField(outDims);
         if (inField.getCoords() != null)
         {
            spUtil = new SplineUtilities(dims, inField.getNSpace(), outDims, inField.getCoords(), params.getNThreads());
            spUtil.addFloatValueModificationListener(
                    new FloatValueModificationListener()
                    {

                       public void floatValueChanged(FloatValueModificationEvent e)
                       {
                          setProgress(e.getVal());
                       }
                    });
            outField.setCoords(spUtil.splineInterpolate());
         }
         else
         {
            float[][] inAffine = inField.getAffine();
            float[][] outAffine = new float[4][3];
            for (int i = 0; i < 3; i++)
            {
               float d = (dims[i] - 1.f)/(outDims[i] - 1.f);
               for (int j = 0; j < 3; j++)
                  outAffine[i][j] = inAffine[i][j] * d;
            }
            for (int i = 0; i < 3; i++)
               outAffine[3][i] = inAffine[3][i];
            outField.setAffine(outAffine);
         }      
         for (int n = 0; n < interpolatedComponents.length; n++)
         {
            DataArray da = inField.getData(interpolatedComponents[n]);
            if (!da.isSimpleNumeric())
               continue;
            spUtil = new SplineUtilities(dims, da.getVeclen(), outDims, da.getFData(), params.getNThreads());
            outField.addData(DataArray.create(spUtil.splineInterpolate(), da.getVeclen(), da.getName()));
         }
         break;
      case Params.CELLSIZE:
         outDims = new int[dims.length];
         float[][] inAffine = inField.getAffine();
         float[][] outAffine = new float[4][3];
         float d = inAffine[0][0] / params.getCellSize();
         outDims[0] = (int) ((dims[0] - 1) * d) + 1;
         outField = new RegularField(outDims);
         for (int j = 0; j < 3; j++)
            outAffine[0][j] = inAffine[0][j] / d;
         if (dims.length > 1)
         d = inAffine[1][1] / params.getCellSize();
         outDims[1] = (int) ((dims[1] - 1) * d) + 1;
         outField = new RegularField(outDims);
         for (int j = 0; j < 3; j++)
            outAffine[1][j] = inAffine[1][j] / d;
         if (dims.length > 2)
         d = inAffine[2][2] / params.getCellSize();
         outDims[2] = (int) ((dims[2] - 1) * d) + 1;
         outField = new RegularField(outDims);
         for (int j = 0; j < 3; j++)
            outAffine[2][j] = inAffine[2][j] / d;
         for (int i = 0; i < 3; i++)
            outAffine[3][i] = inAffine[3][i];
         outField.setAffine(outAffine);
         for (int n = 0; n < interpolatedComponents.length; n++)
         {
            DataArray da = inField.getData(interpolatedComponents[n]);
            if (!da.isSimpleNumeric())
               continue;
            spUtil = new SplineUtilities(dims, da.getVeclen(), inAffine, params.getCellSize(), da.getFData(), params.getNThreads());
            spUtil.addFloatValueModificationListener(
                    new FloatValueModificationListener()
                    {
                       public void floatValueChanged(FloatValueModificationEvent e)
                       {
                          setProgress(e.getVal());
                       }
                    });
            float[] outFData = spUtil.splineInterpolate();
            switch (da.getType())
            {
            case DataArray.FIELD_DATA_BYTE:
               byte[] outBData = new byte[outFData.length];
               for (int i = 0; i < outFData.length; i++)
                  outBData[i] = (byte)((int)(Math.max(0, Math.min(255, outFData[i]))) & 0xff);
               outField.addData(DataArray.create(outBData, da.getVeclen(), da.getName()));
               break;
            case DataArray.FIELD_DATA_SHORT:
               short[] outSData = new short[outFData.length];
               for (int i = 0; i < outFData.length; i++)
                  outSData[i] = (short)outFData[i];
               outField.addData(DataArray.create(outSData, da.getVeclen(), da.getName()));
               break;
            case DataArray.FIELD_DATA_INT:
               int[] outIData = new int[outFData.length];
               for (int i = 0; i < outFData.length; i++)
                  outIData[i] = (int)outFData[i];
               outField.addData(DataArray.create(outIData, da.getVeclen(), da.getName()));
               break;
            case DataArray.FIELD_DATA_FLOAT:
            case DataArray.FIELD_DATA_DOUBLE:
               outField.addData(DataArray.create(outFData, da.getVeclen(), da.getName()));
               break;
            }
         }
         break;
      }
      return outField;
   }
   
   @Override
   public void onActive()
   {
      if (getInputFirstValue("inField") == null
              || ((VNRegularField) getInputFirstValue("inField")).getField() == null)
         return;
      if (!fromGUI)
      {
         inField = ((VNRegularField) getInputFirstValue("inField")).getField();
         if (inField.getDims() == null || inField.getDims().length == 0)
            return;
         ui.setField(inField);
      }
      else
      {
         setOutputValue("outField", new VNRegularField(interpolatedField()));
         fromGUI = false;
      }
   }
}
