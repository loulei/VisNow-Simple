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

package pl.edu.icm.visnow.lib.basic.mappers.ObjectFlow;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.datasets.IrregularField;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.engine.core.InputEgg;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.render.FrameRenderedListener;
import pl.edu.icm.visnow.lib.templates.visualization.modules.OutFieldVisualizationModule;
import pl.edu.icm.visnow.lib.types.VNField;
import pl.edu.icm.visnow.lib.types.VNGeometryObject;
import pl.edu.icm.visnow.lib.types.VNIrregularField;
import pl.edu.icm.visnow.lib.types.VNRegularField;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;

/**
 * @author  Krzysztof S. Nowinski (know@icm.edu.pl)
 * Warsaw University, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class ObjectFlow extends OutFieldVisualizationModule
{

   protected Field inField;
   protected GUI computeUI = null;
   protected Params params;
   protected boolean fromGUI = false;
   protected RegularField inRegularField = null;
   protected IrregularField inIrregularField = null;
   protected boolean ignoreUI = false;
   protected boolean isTransparencyMask = false;
   protected boolean[] transparencyMask;
   protected int[] from;
   protected int[] to;

   public ObjectFlow()
   {
      parameters = params = new Params();
      outObj.setName("displacement");
      params.addChangeListener(new ChangeListener()
      {
         @Override
         public void stateChanged(ChangeEvent evt)
         {
            if(ignoreUI)
            {
               ignoreUI = false;
               return;
            }
            if (outField == null)
               return;
            outField.setCurrentTime(params.getTimeFrame());
            updateMask(transparencyMask);
            if(outField.isMask()) {
                show();
            } else {
                if (outField.getCoords() != null)
                    fieldGeometry.updateCoords(outField.getCoords());
                fieldGeometry.updateDataMap();
            }

            if(outObj.getCurrentViewer() != null)
                outObj.getCurrentViewer().setWaitForExternalTrigger(params.isContinuousUpdate());

            //if(outObj.getCurrentViewer() != null && !params.isContinuousUpdate()) {
            //    outObj.getCurrentViewer().setWaitForExternalTrigger(false);
            //}


            if (!params.isAdjusting() || params.isContinuousUpdate()) {
               fromGUI = true;
                //if(outObj.getCurrentViewer() != null && params.isContinuousUpdate()) {
                //    outObj.getCurrentViewer().setWaitForExternalTrigger(true);
                //}
               startAction();
            }
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


   @Override
   public void onInitFinishedLocal()
   {
      outObj.setCreator(this);
      setOutputValue("outObj", new VNGeometryObject(outObj));
   }

   private void updateMask(boolean[] outMask)
   {
      if (isTransparencyMask)
      {
         int frame = params.getFrame();
         boolean[] inMask = inField.getMask(frame);
         if (inMask != null)
            System.arraycopy(inMask, 0, outMask, 0, outMask.length);
         for (int i = 0; i < outField.getNNodes(); i++)
            transparencyMask[i] = (frame >= from[i] - 1) && (frame <= to[i] + 1);
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
         if (getInputFirstValue("frame") != null)
         {
            int frame = (Integer) getInputFirstValue("frame");
            if (frame != params.getFrame())
            {
               ignoreUI = true;
               params.setFrame(frame);
               ignoreUI = false;
            }
         }
         if (inField != input.getField())
         {
            inField = input.getField();
            ignoreUI = true;
            updateUI();
            ignoreUI = false;
            outField = inField;
            if (inField.getData("from") != null)
            {
               transparencyMask = new boolean[outField.getNNodes()];
               from = inField.getData("from").getIData();
               to = inField.getData("to").getIData();
               for (int i = 0; i < transparencyMask.length; i++)
                  transparencyMask[i] = true;
               isTransparencyMask = true;
               outField.setTransparencyMask(transparencyMask);
            }
            prepareOutputGeometry();
         }
      }
      outField.setCurrentTime(params.getTimeFrame());
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

   @Override
   public void onWaveFinalizing()
   {
//      if (params.isAdjusting())
//         computeUI.getFrameRenderedListener().frameRendered(null);
   }
}
