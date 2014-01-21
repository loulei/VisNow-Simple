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

package pl.edu.icm.visnow.lib.basic.mappers.PlanarSlice;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.log4j.Logger;
import pl.edu.icm.visnow.datasets.CellArray;
import pl.edu.icm.visnow.datasets.CellSet;
import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.datasets.IrregularField;
import pl.edu.icm.visnow.engine.core.InputEgg;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.geometries.parameters.AbstractRenderingParams;
import pl.edu.icm.visnow.gui.events.FloatValueModificationEvent;
import pl.edu.icm.visnow.gui.events.FloatValueModificationListener;
import pl.edu.icm.visnow.lib.templates.visualization.modules.IrregularOutFieldVisualizationModule;
import pl.edu.icm.visnow.lib.types.VNField;
import pl.edu.icm.visnow.lib.types.VNIrregularField;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;
import pl.edu.icm.visnow.lib.utils.numeric.IrregularFieldSplitter;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class PlanarSlice extends IrregularOutFieldVisualizationModule
{

   private static final Logger LOGGER
           = Logger.getLogger(new Throwable().getStackTrace()[0].getClassName());
//    
   protected static final int TRANSFORM = 0;
   protected static final int AXIS = 1;
   protected static final int DIMS = 2;
   protected int change = TRANSFORM;
   public static InputEgg[] inputEggs = null;
   public static OutputEgg[] outputEggs = null;
   protected Field inField = null;
   protected IrregularField slicedField = null;
   protected GUI computeUI = null;
   protected boolean fromUI = false;
   protected boolean ignoreUI = false;
   protected Params params;
   protected int lastAxis = 2;
   protected int nThreads = 1;
   protected float[] coords;
   protected float[] center =
   {
      0, 0, 0
   }; // geometric center of the box surrounding the input field used as origin 
   // of the coordinate system for computing slices
   protected float[] coeffs =
   {
      0, 0, 1
   }; // normalized coefficients of the slice plane equation
   protected float fieldCenterRHS = 0;  // rhs for the equation of the slice plane passing through the center point
   protected float rhs = 0;
   protected long lastSnapTime;
   protected boolean fast = false;
   protected boolean busy = false;

   public PlanarSlice()
   {
      parameters = params = new Params();
      params.addChangeListener(new ChangeListener()
      {
         @Override
         public void stateChanged(ChangeEvent evt)
         {
            if (inField == null)
               return;
            if (params.isAdjusting())
               return;
            fromUI = true;
            if (ignoreUI)
            {
               ignoreUI = false;
               return;
            }
            change = TRANSFORM;
            if (params.getAxis() != lastAxis)
               change = AXIS;
            startAction();
         }
      });
      SwingInstancer.swingRunAndWait(new Runnable()
      {
         public void run()
         {
            computeUI = new GUI();
            computeUI.setParams(params);
            ui.addComputeGUI(computeUI);
            setPanel(ui);
         }
      });
   }

   private void transformSlice()
   {
      coeffs = params.getCoeffs();
      fieldCenterRHS = 0;
      for (int i = 0; i < 3; i++)
         fieldCenterRHS += coeffs[i] * center[i];
   }

   private void updateInitSlicePosition()
   {

      for (int i = 0; i < 3; i++)
         coeffs[i] = 0;
      coeffs[params.getAxis()] = 1;
      lastAxis = params.getAxis();
   }

   static public IrregularField sliceField(IrregularField field, int type, final float[] coeffs, final float rhs)
   {
      for (CellSet cs : field.getCellSets())
         for (CellArray ca : cs.getCellArrays())
            if (ca != null && ca.getCellRadii() == null)
               throw new RuntimeException("CellSet has not GeometryData. run addGeometryData() method");

      IrregularFieldSplitter splitter = new IrregularFieldSplitter(field, type);
      float[] coords = field.getCoords();

      for (int nSet = 0; nSet < field.getNCellSets(); nSet++)
      {
         CellSet trCS = field.getCellSet(nSet);
         splitter.initCellSetSplit(trCS);
         for (int iCellArray = 0; iCellArray < trCS.getCellArrays().length; iCellArray++)
         {
            if (trCS.getCellArray(iCellArray) == null)
               continue;

            CellArray ca = trCS.getCellArray(iCellArray);
            boolean isTriangulated = ca.isTriangulation();

            splitter.initCellArraySplit(ca);
            int nCellNodes = ca.getCellNodes();
            float[] vals = new float[nCellNodes];
            int[] indices = ca.getDataIndices();
            float[] cellCenters = ca.getCellCenters();
            float[] cellRadii = ca.getCellRadii();
            int[] nodes = ca.getNodes();
            int[] cellNodes = new int[ca.getCellNodes()];
            int m;
            for (int iCell = 0; iCell < ca.getNCells(); iCell++)
            {
               int index = -1;
               if (indices != null)
                  index = indices[iCell];

               float t = coeffs[0] * cellCenters[3 * iCell]
                       + coeffs[1] * cellCenters[3 * iCell + 1]
                       + coeffs[2] * cellCenters[3 * iCell + 2] - rhs;
               if (Math.abs(t) <= cellRadii[iCell])
               {
                  for (int l = 0; l < nCellNodes; l++)
                  {
                     cellNodes[l] = m = nodes[nCellNodes * iCell + l];
                     vals[l] = (coeffs[0] * coords[3 * m]
                             + coeffs[1] * coords[3 * m + 1]
                             + coeffs[2] * coords[3 * m + 2] - rhs);
                  }
                  if (isTriangulated)
                     splitter.processSimplex(cellNodes, vals, index);
                  else
                     splitter.processCell(cellNodes, vals, index);
               } else if (type == -1 && t < -cellRadii[iCell]
                       || type == 1 && t > cellRadii[iCell])
                  if (isTriangulated)
                     splitter.addSimplex(iCell);
                  else
                     splitter.addCellTriangulation(iCell);
            }

         }
      }
      return splitter.createOutField(coeffs);
   }
   FloatValueModificationListener progressListener
           = new FloatValueModificationListener()
           {
              @Override
              public void floatValueChanged(FloatValueModificationEvent e)
              {
                 setProgress(e.getVal());
              }
           };

   @Override
   public void onActive()
   {
      boolean newField = false;

      if (!fromUI)
      {
         if (getInputFirstValue("inField") == null)
            return;
         Field field = ((VNField) getInputFirstValue("inField")).getField();
         if (field == null)
            return;
         newField = true;
         if (inField != field)
         {
            inField = field;
            float[][] extents = inField.getExtents();
            for (int i = 0; i < 3; i++)
               center[i] = .5f * (extents[0][i] + extents[1][i]);
            computeUI.setInField(inField);
            if (inField instanceof IrregularField)
               slicedField = (IrregularField) inField;
            else
               slicedField = inField.triangulate();
            coords = slicedField.getCoords();
            lastAxis = 2;
            fast = false;
         }
      }
      if (newField)
      {
         for (CellSet cs : slicedField.getCellSets())
         {
            cs.addFloatValueModificationListener(progressListener);
            cs.addGeometryData(params.getThreads(), coords);
            cs.clearFloatValueModificationListener();
         }
         updateInitSlicePosition();
      }
      fromUI = false;
      if (change == AXIS)
         updateInitSlicePosition();
      change = TRANSFORM;
      lastSnapTime = System.currentTimeMillis();
      busy = true;

      transformSlice();

      rhs = params.getRightSide();
//      rhs = fieldCenterRHS + params.getRightSide();
      coeffs = params.getCoeffs();

      outField = sliceField(slicedField, params.getType(), coeffs, rhs);

      prepareOutputGeometry();
      irregularFieldGeometry.getFieldDisplayParams().setShadingMode(AbstractRenderingParams.FLAT_SHADED);
      if (outField != null)
         setOutputValue("outField", new VNIrregularField(outField));
      else
         setOutputValue("outField", null);
      busy = false;
      show();
   }
}
