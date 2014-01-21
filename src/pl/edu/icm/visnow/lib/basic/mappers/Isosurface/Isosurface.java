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

package pl.edu.icm.visnow.lib.basic.mappers.Isosurface;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.datasets.IrregularField;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.datasets.cells.Cell;
import pl.edu.icm.visnow.engine.core.InputEgg;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.gui.events.FloatValueModificationEvent;
import pl.edu.icm.visnow.gui.events.FloatValueModificationListener;
import pl.edu.icm.visnow.lib.templates.visualization.modules.OutFieldVisualizationModule;
import pl.edu.icm.visnow.lib.types.VNField;
import pl.edu.icm.visnow.lib.types.VNIrregularField;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;
import pl.edu.icm.visnow.lib.utils.field.MergeIrregularField;
import pl.edu.icm.visnow.lib.utils.field.SmoothTriangulation;
import pl.edu.icm.visnow.system.main.VisNow;

/**
 * @author  Krzysztof S. Nowinski (know@icm.edu.pl)
 * Warsaw University, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class Isosurface extends OutFieldVisualizationModule
{

   /**
    *
    * inField - a 3D field to create isosurface;
    * at least one scalar data component must be present.
    * 
    * outField -  isosurface field will be created by update method -
    * can be void, can contain no node data (geometry only)
    *
    */
   public static InputEgg[] inputEggs = null;
   public static OutputEgg[] outputEggs = null;
   protected Field inField;
   protected IrregularField tmpField;
   protected IsosurfaceGUI computeUI = null;
   protected SmoothTriangulation smoother = new SmoothTriangulation();
   protected int[] interpolatedComponents = null;
   protected float lastThr = 127;
   protected float lastTime = -1;
   protected boolean fromGUI = false;
   protected IsosurfaceParams params;
   protected IsosurfaceEngine isosurfaceEngine;
   protected boolean debug;
   protected boolean ignoreUI = false;

   public Isosurface()
   {
      parameters = params = new IsosurfaceParams();
      SwingInstancer.swingRunAndWait(new Runnable()
      {

         @Override
         public void run()
         {
            computeUI = new IsosurfaceGUI();
            computeUI.setParams(params);
            ui.addComputeGUI(computeUI);
            setPanel(ui);
         }
      });
      outObj.setName("isosurface");
      params.addChangeListener(new ChangeListener()
      {
         @Override
         public void stateChanged(ChangeEvent evt)
         {
            if (ignoreUI)
            {
               ignoreUI = false;
               return;
            }
            fromGUI = true;
            startAction();
         }
      });
      smoother.addFloatValueModificationListener(
              new FloatValueModificationListener()
              {
                 @Override
                 public void floatValueChanged(FloatValueModificationEvent e)
                 {
                    setProgress(e.getVal() / 10 + .9f);
                 }
              });
      debug = VisNow.isDebug();
   }
   protected FloatValueModificationListener progressListener = new FloatValueModificationListener()
   {

      @Override
      public void floatValueChanged(FloatValueModificationEvent e)
      {
         setProgress(e.getVal());
      }
   };

   public void update()
   {
      if (isosurfaceEngine == null)
         return;      
      float dataCurrentTime = 0;
      if (inField.isTimeDependant() && params.getTime() != inField.getCurrentTime())
      {
         dataCurrentTime = inField.getCurrentTime();
         inField.setCurrentTime(params.getTime());
      }
      if (params.isRecompute() || tmpField == null)
      {
         float[] thresholds = params.getThresholds();
         tmpField = null;
         for (int i = 0; i < thresholds.length; i++)
         {
            IrregularField currentField = 
               isosurfaceEngine.makeIsosurface(params, params.getThresholds()[i]);
            if (currentField != null)
               tmpField = MergeIrregularField.merge(tmpField, currentField, i, params.isSeparate());
         }
      }
      if (tmpField == null) {
          outField = null;
          outIrregularField = null;
          setOutputValue("isosurfaceField", null);
          
          show();   
//      if (tmpField == null || tmpField.getNData() == 0)
         return;
      }
      if (params.isSmoothing() && params.getSmoothSteps() > 0)
      {
         smoother.setInField(tmpField);
         outField = tmpField.clone();
         outIrregularField = (IrregularField) outField;
         outIrregularField.setCoords(smoother.smoothCoords(params.getSmoothSteps(), .5f));
         if (tmpField.getNormals() != null)
            outIrregularField.setNormals(smoother.smoothNormals(params.getSmoothSteps(), .5f));
      } else {
         outField = tmpField;
         outIrregularField = (IrregularField) outField;
      }
      outIrregularField.setExtents(inField.getExtents());
      prepareOutputGeometry();
      show();
      setOutputValue("isosurfaceField", new VNIrregularField(outIrregularField));
      if (inField.getCurrentTime() != dataCurrentTime)
         inField.setCurrentTime(dataCurrentTime);
   }

   private void updateUI()
   {
      SwingInstancer.swingRunAndWait(new Runnable()
      {
         @Override
         public void run()
         {
            ignoreUI = true;
            computeUI.setInField(inField);
            ignoreUI = false;
         }
      });
   }

   @Override
   public void onActive()
   {
      if (!fromGUI)
      {
        VNField inFld = (VNField) getInputFirstValue("inField");
        if(inFld == null || inFld.getField() == null) {
             inField = null;
             outField = null;
             outIrregularField = null;
             return;
        }
        Field newInField = inFld.getField();
        if (inField == null || inField != newInField || newInField.getCurrentTime() != lastTime)
        {
           params.setRecompute(true);
           lastTime = newInField.getCurrentTime();
        }
        if (inField != newInField)
        {
           inField = newInField;

           if (inField.getNSpace() != 3 || inField.getNData() < 1) {
              inField = null;
              outField = null;
              outIrregularField = null;
              return;
           }
           if (inField instanceof RegularField)
           {
              int[] inDims = ((RegularField) inField).getDims();
              if (inDims.length != 3 || inDims[0] < 2 || inDims[1] < 2 || inDims[2] < 2) {
                 inField = null;
                 outField = null;
                 outIrregularField = null;
                 return;
              }
              isosurfaceEngine = new RegularFieldIsosurface((RegularField) inField);
              isosurfaceEngine.addFloatValueModificationListener(progressListener);
           } else {
              IrregularField irf = ((IrregularField)inField);
              if(!(irf.hasCellsType(Cell.TETRA) || irf.hasCellsType(Cell.PYRAMID) || irf.hasCellsType(Cell.PRISM) || irf.hasCellsType(Cell.HEXAHEDRON))) {
                  inField = null;
                  outField = null;
                  outIrregularField = null;
                  return;                  
              }
              isosurfaceEngine = new IrregularFieldIsosurface((IrregularField) inField);
              isosurfaceEngine.addFloatValueModificationListener(progressListener);
           }
           updateUI();
         }
         if (getInputFirstValue("threshold") != null)
         {
            float thr = (Float) getInputFirstValue("threshold");
            if (thr != lastThr)
            {
               lastThr = thr;
               ignoreUI = true;
               params.setThresholds(new float[]{thr});
               params.setRecompute(true);
            }
         }
      }

      fromGUI = false;
      if (!params.isRecompute() && !params.isActive())
         return;
      update();
   }
}
