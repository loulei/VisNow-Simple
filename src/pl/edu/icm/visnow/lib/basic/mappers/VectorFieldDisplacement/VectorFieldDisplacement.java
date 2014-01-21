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

package pl.edu.icm.visnow.lib.basic.mappers.VectorFieldDisplacement;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.datasets.IrregularField;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.engine.core.InputEgg;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.render.FrameRenderedListener;
import pl.edu.icm.visnow.lib.templates.visualization.modules.OutFieldVisualizationModule;
import pl.edu.icm.visnow.lib.types.VNRegularField;
import pl.edu.icm.visnow.lib.types.VNField;
import pl.edu.icm.visnow.lib.types.VNIrregularField;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;

/**
 * @author  Krzysztof S. Nowinski (know@icm.edu.pl)
 * Warsaw University, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class VectorFieldDisplacement extends OutFieldVisualizationModule
{

   protected Field inField;
   protected GUI computeUI = null;
   protected Params params;
   protected boolean fromGUI = false;
   protected RegularField inRegularField = null;
   protected IrregularField inIrregularField = null;
   protected float[] inCoords;
   protected float[] coords;
   protected boolean ignoreUI = false;

   public VectorFieldDisplacement()
   {
      parameters = params = new Params();
      outObj.setName("displacement");
      params.addChangeListener(new ChangeListener()
      {
         @Override
         public void stateChanged(ChangeEvent evt)
         {
            if(ignoreUI || inField == null)
               return;
            updateCoords();
            fromGUI = true;
            fieldGeometry.updateCoords();
            if (!params.isAdjusting())
               startAction();
         }
      });
      SwingInstancer.swingRunAndWait(new Runnable()
      {
         @Override
         public void run()
         {
            computeUI = new GUI();
         }
      });
      computeUI.setParams(params);
      ui.addComputeGUI(computeUI);
      setPanel(ui);
   }

   public static InputEgg[] inputEggs = null;
   public static OutputEgg[] outputEggs = null;

   private void updateCoords()
   {
      float scale = params.getScale();
      DataArray da = inField.getData(params.getVectorComponent());
      float[] v = da.getFData();
      if (da.getVeclen() == 3)
         for (int i = 0; i < coords.length; i++)
            coords[i] = inCoords[i] + scale * v[i];
      else
      {
         int n = inField.getNNodes();
         int m = Math.min(3, da.getVeclen());
         for (int i = 0, k = 0, l = 0; i < n; i++, k += 3, l += da.getVeclen())
            for (int j = 0; j < m; j++)
                coords[k + j] = inCoords[k + j] + scale * v[l + j];
      }
   }

   private void updateUI()
   {
      ignoreUI = true;
      computeUI.setInData(inField);
      ignoreUI = false;
   }

   @Override
   public void onActive()
   {
      if (getInputFirstValue("inField") == null)
         return;
      if (!fromGUI)
      {
         VNField input = ((VNField) getInputFirstValue("inField"));
         Field newInField = input.getField();
         if (newInField != null && inField != newInField)
         {
            inField = newInField;
            int cmp = -1;
            for (int i = 0; i < inField.getNData(); i++)
               if (inField.getData(i).isSimpleNumeric() &&
                   inField.getData(i).getVeclen() > 1 &&
                   inField.getData(i).getVeclen() <= 3)
               {
                  cmp = i;
                  break;
               }
            if (cmp == -1)
               return;
            updateUI();
            outField = inField.clone();
            outField.setNSpace(3);
            if (inField instanceof RegularField)
            {
               inRegularField = (RegularField) inField;
               outRegularField = (RegularField) outField;
               updateUI();
               if (inRegularField.getCoords() != null)
                  inCoords = inRegularField.getCoords();
               else
                  inCoords = inRegularField.getCoordsFromAffine();
            } else
            {
               inIrregularField = (IrregularField) inField;
               outIrregularField = (IrregularField)outField;
               inCoords = inField.getCoords();
            }
            outField.clearCoords();
            coords = new float[3*outField.getNNodes()];
            System.arraycopy(inCoords, 0, coords, 0, inCoords.length);
            outField.setCoords(coords);
            prepareOutputGeometry();
         }
      }
      if (inField.getData(params.getVectorComponent()) != null &&
          inField.getData(params.getVectorComponent()).getVeclen() > 1 &&
          inField.getData(params.getVectorComponent()).getVeclen() <= 3)
         updateCoords();

      if (outField != null && outField instanceof RegularField) {
          setOutputValue("outRegularField", new VNRegularField((RegularField)outField));
          setOutputValue("outIrregularField", null);
      } else if(outField != null && outField instanceof IrregularField) {
          setOutputValue("outRegularField", null);
          setOutputValue("outIrregularField", new VNIrregularField((IrregularField)outField));
      } else {
          setOutputValue("outRegularField", null);
          setOutputValue("outIrregularField", null);
      }      
      show();
      fromGUI = false;
   }

   @Override
   public FrameRenderedListener getFrameRenderedListener()
   {
      return computeUI.getFrameRenderedListener();
   }

}
