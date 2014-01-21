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

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.engine.core.InputEgg;
import pl.edu.icm.visnow.engine.core.ModuleCore;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.lib.types.VNField;
import pl.edu.icm.visnow.lib.types.VNRegularField;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;

/**
 * @author  Krzysztof S. Nowinski (know@icm.edu.pl)
 * Warsaw University, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class CorrelationAnalysis extends ModuleCore 
{

   private GUI ui = null;
   private CorrelationDisplayFrame correlationDisplayFrame = new CorrelationDisplayFrame();
   private RegularField outField = null;
   private Field inField = null;
   private Params params = null;
   public static InputEgg[] inputEggs = null;
   public static OutputEgg[] outputEggs = null;
   protected boolean ignoreUI = false;
   protected boolean fromUI = false;

   public CorrelationAnalysis()
   {
      parameters = params = new Params();
      params.addChangeListener(new ChangeListener()
      {
         public void stateChanged(ChangeEvent evt)
         {
            fromUI = true;
            correlationDisplayFrame.setVisible(true);
            if(ignoreUI)
               return;
            startAction();
         }
      });
      SwingInstancer.swingRunAndWait(new Runnable()
      {
         public void run()
         {
            ui = new GUI();
            ui.setParams(params);
            correlationDisplayFrame = new CorrelationDisplayFrame();
            correlationDisplayFrame.setParams(params);
            correlationDisplayFrame.setVisible(true);
         }
      });
      setPanel(ui);
   }


   public void update()
   {
      int sampleSize = 100;
      float[][] rawData = null;
      switch (params.getVariableType())
      {
         case Params.COMPONENTS_AS_VARIABLES:
         {
            int n = 0;
            sampleSize = inField.getNNodes();
            int[] comps = new int[inField.getNData()];
            for (int i = 0; i < inField.getNData(); i++)
               if (inField.getData(i).isSimpleNumeric() && inField.getData(i).getVeclen() == 1)
               {
                  comps[n] = i;
                  n += 1;
               }
            if (n <= 1)
               return;
            outField = new RegularField(new int[]{n, n});
            double[] avg   = new double[n];
            double[] sigma = new double[n];
            float[] matrix = new float[n * n];
            double dmax = -Double.MAX_VALUE;
            int p = inField.getNNodes();
            sampleSize = p;
            rawData = new float[n][sampleSize];
            for (int i = 0; i < n; i++)
            {
               float[] d0 = inField.getData(comps[i]).getFData();
               System.arraycopy(d0, 0, rawData[i], 0, sampleSize);
               avg[i] = sigma[i] = 0;
               for (int j = 0; j < p; j++)
                  avg[i] += d0[j];
               avg[i] /= p;
               double a0 = avg[i];
               for (int j = 0; j < p; j++)
                  sigma[i] += (d0[j] - a0) * (d0[j] - a0);
               sigma[i] = Math.sqrt(sigma[i] / p);
               if (sigma[i] == 0) sigma[i] = 1;
               if (sigma[i] > dmax)
                  dmax = sigma[i];
               for (int j = 0; j <= i; j++)
               {
                  double d = 0;
                  double a1 = avg[j];
                  float[] d1 = inField.getData(comps[j]).getFData();
                  for (int k = 0; k < p; k++)
                     d += (d0[k] - a0) * (d1[k] - a1);
                  if (params.isCorrelation())
                     matrix[i * n + j] = matrix[j * n + i] = (float)(d / (p * sigma[i] * sigma[j]));
                  else
                     matrix[i * n + j] = matrix[j * n + i] = (float)d / p;
               }
            }
            DataArray da = DataArray.create(matrix, 1, "");
            if (params.isCorrelation())
            {
               da.setName("correlations");
               da.setMaxv(1);
               da.setMinv(-1);
            }
            else
            {
               da.setName("covariance");
               da.setMaxv((float)(dmax * dmax));
               da.setMinv(-(float)(dmax * dmax));
            }
            String[] uData = new String[n];
            for (int i = 0; i < n; i++)
               uData[i] = inField.getData(comps[i]).getName();
            da.setUserData(uData);
            outField.addData(da);
            break;
         }
         case Params.ROWS_AS_VARIABLES:
         {
            if (!(inField instanceof RegularField))
               return;
            RegularField regularInField = (RegularField) inField;
            int[] dims = regularInField.getDims();
            float[] data = regularInField.getData(params.getComponent()).getFData();
            int m = dims[0];
            int n = dims[1];
            sampleSize = m;
            rawData = new float[n][sampleSize];
            outField = new RegularField(new int[]{n, n});
            double[] avg   = new double[n];
            double[] sigma = new double[n];
            float[] matrix = new float[n * n];
            double dmax = -Double.MAX_VALUE;
            for (int i = 0; i < n; i++)
            {
               System.arraycopy(data, sampleSize * i, rawData[i], 0, sampleSize);
               avg[i] = sigma[i] = 0;
               for (int j = 0, k = m * i; j < m; j++, k++)
                  avg[i] += data[k];
               avg[i] /= m;
               double a0 = avg[i];
               for (int j = 0, k = m * i; j < m; j++, k++)
                  sigma[i] += (data[k] - a0) * (data[k] - a0);
               sigma[i] = Math.sqrt(sigma[i] / m);
               if (sigma[i] == 0) sigma[i] = 1;
               if (sigma[i] > dmax)
                  dmax = sigma[i];
               for (int j = 0; j <= i; j++)
               {
                  double d = 0;
                  double a1 = avg[j];
                  for (int k = 0, k0 = i * m, k1 = j * m; k < m; k++, k1++, k0++)
                     d += (data[k0] - a0) * (data[k1] - a1);
                  if (params.isCorrelation())
                     matrix[i * n + j] = matrix[j * n + i] = (float)(d / (m * sigma[i] * sigma[j]));
                  else
                     matrix[i * n + j] = matrix[j * n + i] = (float)d / m;
               }
            }
            if (params.isCorrelation())
            {
               DataArray da = DataArray.create(matrix, 1, "correlations");
               da.setMaxv(1);
               da.setMinv(-1);
               outField.addData(da);
            }
            else
            {
               DataArray da = DataArray.create(matrix, 1, "covariance");
               da.setMaxv((float)(dmax * dmax));
               da.setMinv(-(float)(dmax * dmax));
               outField.addData(da);
            }
            break;
         }
         case Params.COLUMNS_AS_VARIABLES:
         {
            if (!(inField instanceof RegularField))
               return;
            RegularField regularInField = (RegularField) inField;
            int[] dims = regularInField.getDims();
            float[] data = regularInField.getData(params.getComponent()).getFData();
            int m = dims[0];
            int n = dims[1];
            sampleSize = n;
            rawData = new float[m][sampleSize];
            outField = new RegularField(new int[]{m, m});
            double[] avg   = new double[m];
            double[] sigma = new double[m];
            float[] matrix = new float[m * m];
            double dmax = -Double.MAX_VALUE;
            for (int i = 0; i < m; i++)
            {
               avg[i] = sigma[i] = 0;
               for (int j = 0, k = i; j < n; j++, k += m)
               {
                  avg[i] += data[k];
                  rawData[i][j] = data[k];
               }
               avg[i] /= n;
               double a0 = avg[i];
               for (int j = 0, k = i; j < n; j++, k += m)
                  sigma[i] += (data[k] - a0) * (data[k] - a0);
               sigma[i] = Math.sqrt(sigma[i] / n);
               if (sigma[i] == 0) sigma[i] = 1;
               if (sigma[i] > dmax)
                  dmax = sigma[i];
               for (int j = 0; j <= i; j++)
               {
                  double d = 0;
                  double a1 = avg[j];
                  for (int k = 0, k0 = i , k1 = j; k < n; k++, k1 += m, k0 += m)
                     d += (data[k0] - a0) * (data[k1] - a1);
                  if (params.isCorrelation())
                     matrix[i * m + j] = matrix[j * m + i] = (float)(d / (n * sigma[i] * sigma[j]));
                  else
                     matrix[i * m + j] = matrix[j * m + i] = (float)d / n;
               }
            }
            if (params.isCorrelation())
            {
               DataArray da = DataArray.create(matrix, 1, "correlations");
               da.setMaxv(1);
               da.setMinv(-1);
               outField.addData(da);
            }
            else
            {
               DataArray da = DataArray.create(matrix, 1, "covariance");
               da.setMaxv((float)(dmax * dmax));
               da.setMinv(-(float)(dmax * dmax));
               outField.addData(da);
            }
            break;
         }
      }
      correlationDisplayFrame.setCorrelationField(outField);
      correlationDisplayFrame.setSampleSize(sampleSize);
      correlationDisplayFrame.setData(rawData);
   }

   private void updateUI()
   {
      ignoreUI = true;
      correlationDisplayFrame.setInField(inField);
      ignoreUI = false;
   }

   @Override
   public void onActive()
   {
      if (fromUI)
      {
         update();
         setOutputValue("outField", new VNRegularField(outField));
         fromUI = false;
         return;
      }
      Field in;

      if (getInputFirstValue("inField") == null)
         return;
      in = ((VNField) getInputFirstValue("inField")).getField();
      if (in == null)
         return;
      inField = in;
      updateUI();
      update();
      setOutputValue("outField", new VNRegularField(outField));
      fromUI = false;
   }
   
   @Override
   public void onDelete() {
       correlationDisplayFrame.dispose();       
   }
}
