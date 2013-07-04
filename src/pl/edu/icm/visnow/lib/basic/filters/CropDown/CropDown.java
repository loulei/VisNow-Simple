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

package pl.edu.icm.visnow.lib.basic.filters.CropDown;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.datasets.TimeData;
import pl.edu.icm.visnow.datasets.dataarrays.ComplexDataArray;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.engine.core.InputEgg;
import pl.edu.icm.visnow.engine.core.ModuleCore;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.lib.types.VNRegularField;
import static pl.edu.icm.visnow.lib.utils.CropDown.*;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;

/**
 *
 * @author  Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class CropDown extends ModuleCore
{

   public static InputEgg[] inputEggs = null;
   public static OutputEgg[] outputEggs = null;   
   protected GUI ui = null;
   protected RegularField inField = null;
   protected RegularField outField = null;
   protected Params params = new Params();
   protected int[] lastDims = {-1, -1, -1};
   protected boolean ignoreUI = false;

   public CropDown()
   {
      params.addChangeListener(new ChangeListener()
      {
         public void stateChanged(ChangeEvent evt)
         {
            if (ignoreUI)
               return;
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

   @Override
   public void onActive()
   {
      if (getInputFirstValue("inField") == null || 
        !(getInputFirstValue("inField") instanceof VNRegularField))
         return;
      inField = ((VNRegularField) getInputFirstValue("inField")).getField();
      if (inField == null)
         return;
      int[] dims = inField.getDims();
      for (int i = 0; i < dims.length; i++)
         if (lastDims == null || i >= lastDims.length || dims[i] != lastDims[i])
         {
            ignoreUI = true;
            ui.setInField(inField);
            ignoreUI = false;
         System.arraycopy(dims, 0, lastDims, 0, dims.length);
            break;
         }
      int[] outDims = new int[dims.length];
      int[] low = params.getLow();
      int[] up = params.getUp();
      int[] down = params.getDownsize();
      for (int i = 0; i < outDims.length; i++)
      {
         if (low[i] < 0 || up[i] > dims[i] || down[i] < 1 || up[i] < low[i] + 2 * down[i])
            return;
         outDims[i] = (up[i] - low[i] - 1) / down[i] + 1;
      }
      outField = new RegularField(outDims);
      outField.setNSpace(inField.getNSpace());
      if (inField.getCoords() != null)
      {
         TimeData<float[]> oldTimeCoords = inField.getAllCoords();
         for (int i = 0; i < oldTimeCoords.getNSteps(); i++)
            outField.setCoords(cropDownArray(oldTimeCoords.get(i), inField.getNSpace(), dims, low, up, down),
                                             oldTimeCoords.getTime(i));
         outField.setCoords(cropDownArray(inField.getCoords(), inField.getNSpace(), dims, low, up, down));
      }
      else
      {
         float[][] outAffine = new float[4][3];
         float[][] affine = inField.getAffine();
         System.arraycopy(affine[3], 0, outAffine[3], 0, 3);
         for (int i = 0; i < outDims.length; i++)
            for (int j = 0; j < 3; j++)
            {
               outAffine[3][j] += low[i] * affine[i][j];
               outAffine[i][j] = affine[i][j] * down[i];
            }
         outField.setAffine(outAffine);
      }
      if (inField.isMask())
         outField.setMask(cropDownArray(inField.getMask(), 1, dims, low, up, down));

      for (int i = 0; i < inField.getNData(); i++)
      {
         DataArray dta = inField.getData(i);
         if (!dta.isSimpleNumeric())
            continue;
         int outNData = outField.getNNodes();
         DataArray outDta = DataArray.create(dta.getType(), outNData, dta.getVeclen(), dta.getName(), dta.getUnit(), dta.getUserData());
         outDta.setTimeData(dta.getTimeData().cropDown(dta.getVeclen(), dims, low, up, down));
         outField.addData(outDta);
      }
      setOutputValue("outField", new VNRegularField(outField));
   }
}
