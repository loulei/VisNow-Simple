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


package pl.edu.icm.visnow.lib.basic.mappers.RibbonPlot;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pl.edu.icm.visnow.datasets.cells.Cell;
import pl.edu.icm.visnow.datasets.CellArray;
import pl.edu.icm.visnow.datasets.CellSet;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.datasets.IrregularField;
import pl.edu.icm.visnow.engine.core.InputEgg;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.gui.events.FloatValueModificationEvent;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.geometries.objects.GeometryObject;
import pl.edu.icm.visnow.geometries.parameters.AbstractRenderingParams;
import pl.edu.icm.visnow.gui.events.FloatValueModificationListener;
import pl.edu.icm.visnow.lib.basic.mappers.Axes3D.Axes3DObject;
import pl.edu.icm.visnow.lib.templates.visualization.modules.IrregularOutFieldVisualizationModule;
import pl.edu.icm.visnow.lib.types.VNRegularField;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;

/**
 * @author  Krzysztof S. Nowinski (know@icm.edu.pl)
 * Warsaw University, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class RibbonPlot extends IrregularOutFieldVisualizationModule
{
   public static InputEgg[] inputEggs = null;
   public static OutputEgg[] outputEggs = null;
   protected RegularField inField;
   protected GUI computeUI = null;
   protected boolean ignoreUI = false;
   protected boolean fromUI = false;
   protected Params params = null;
   protected int[] inDims = null;
   protected float[] coords = null;
   protected int axis = 0, lastAxis = 0;
   protected int[] cells = null;
   protected boolean[] orientations = null;
   protected int[] edges = null;
   protected boolean[] edgeOrientations = null;
   protected float[][] extents = new float[2][3];
   protected float[][] physExtents = new float[2][3];
   protected boolean lastRibbon = true;
   protected RegularField outBox = new RegularField(new int[] {2,2,2});
   protected pl.edu.icm.visnow.lib.basic.mappers.Axes3D.Axes3DObject axesObj = null;
   protected pl.edu.icm.visnow.lib.basic.mappers.Axes3D.Axes3DParams axesParams = 
           new pl.edu.icm.visnow.lib.basic.mappers.Axes3D.Axes3DParams();

   public RibbonPlot()
   {
      parameters = params = new Params();
      SwingInstancer.swingRunAndWait(new Runnable()
      {
         public void run()
         {
            computeUI = new GUI();
         }
      });
      computeUI.setParams(params);
      computeUI.getAxesGUI().setParams(axesParams);
      ui.addComputeGUI(computeUI);
      outObj.setName("ribbon plot");
      axesObj = new Axes3DObject();
      axesObj.setName("axes3D");
      outObj.addChild(axesObj);
      
      params.addChangeListener(new ChangeListener()
      {
         public void stateChanged(ChangeEvent evt)
         {
            fromUI = true;
            if (ignoreUI)
               return;
            if (computeUI.isAdjusting())
            {
               updateCoords();
               irregularFieldGeometry.updateCoords();
            } else
               startAction();
            lastRibbon = params.isRibbon();
         }
      });
      axesParams.addChangeListener(new ChangeListener()
      {
         public void stateChanged(ChangeEvent evt)
         {
            if (params.showAxes())
               axesObj.update(outBox, axesParams);
         }
      });
      setPanel(ui);
   }
   protected FloatValueModificationListener progressListener = new FloatValueModificationListener()
   {

      public void floatValueChanged(FloatValueModificationEvent e)
      {
         setProgress(e.getVal());
      }
   };

   void updateCells()
   {
      edges = new int[4 * inDims[1] * inDims[0]];
      edgeOrientations = new boolean[2 * inDims[1] * inDims[0]];
      for (int i = 0; i < edgeOrientations.length; i++)
         edgeOrientations[i] = true;
      if (axis == 0)
      {
         orientations = new boolean[inDims[1] * (inDims[0] - 1)];
         for (int i = 0; i < orientations.length; i++)
            orientations[i] = true;
         cells = new int[4 * inDims[1] * (inDims[0] - 1)];
         for (int i = 0, k = 0, l = 0; i < inDims[1]; i++, l += 2)
            for (int j = 0; j < inDims[0] - 1; j++, l += 2, k += 4)
            {
               cells[k] = l;
               cells[k + 1] = l + 1;
               cells[k + 2] = l + 3;
               cells[k + 3] = l + 2;
            }
         for (int i = 0, k = 0, l = 0; i < inDims[1]; i++, l += 2)
         {
            edges[k] = l;
            edges[k + 1] = l + 1;
            k += 2;
            for (int j = 0; j < inDims[0] - 1; j++, l += 2, k += 4)
            {
               edges[k] = l;
               edges[k + 1] = l + 2;
               edges[k + 2] = l + 1;
               edges[k + 3] = l + 3;
            }
            edges[k] = l;
            edges[k + 1] = l + 1;
            k += 2;
         }
      } else
      {
         orientations = new boolean[inDims[0] * (inDims[1] - 1)];
         for (int i = 0; i < orientations.length; i++)
            orientations[i] = true;
         cells = new int[4 * inDims[0] * (inDims[1] - 1)];
         for (int i = 0, k = 0, l = 0; i < inDims[0]; i++, l += 2)
            for (int j = 0; j < inDims[1] - 1; j++, l += 2, k += 4)
            {
               cells[k] = l;
               cells[k + 1] = l + 1;
               cells[k + 2] = l + 3;
               cells[k + 3] = l + 2;
            }
         for (int i = 0, k = 0, l = 0; i < inDims[0]; i++, l += 2)
         {
            edges[k] = l;
            edges[k + 1] = l + 1;
            k += 2;
            for (int j = 0; j < inDims[1] - 1; j++, l += 2, k += 4)
            {
               edges[k] = l;
               edges[k + 1] = l + 2;
               edges[k + 2] = l + 1;
               edges[k + 3] = l + 3;
            }
            edges[k] = l;
            edges[k + 1] = l + 1;
            k += 2;
         }
      }
   }

   void updateCoords()
   {
      float scale = params.getScale();
      float[] vals = inField.getData(params.getComponent()).getFData();
      float off0 = -.5f * inDims[0];
      float off1 = -.5f * inDims[1];
      float base = 0;
      boolean ribbon = params.isRibbon();
      if (!params.isZeroBased())
         base = scale * inField.getData(params.getComponent()).getMinv();
      extents[0][0] = off0;
      extents[0][1] = off1;
      extents[0][2] = inField.getData(params.getComponent()).getMinv() * scale;
      extents[1][0] = -off0;
      extents[1][1] = -off1;
      extents[1][2] = inField.getData(params.getComponent()).getMaxv() * scale;
      physExtents[0][0] = 0;
      physExtents[0][1] = 0;
      if (params.isZeroBased() && inField.getData(params.getComponent()).getMinv() > 0)
         physExtents[0][2] = 0;
      else
         physExtents[0][2] = inField.getData(params.getComponent()).getMinv();
      physExtents[1][0] = inDims[0];
      physExtents[1][1] = inDims[1];
      physExtents[1][2] = inField.getData(params.getComponent()).getMaxv();
      switch (axis)
      {
      case 0:
         for (int i = 0, k = 0, m = 0; i < inDims[1]; i++)
            for (int j = 0; j < inDims[0]; j++, m++, k += 6)
            {
               coords[k]     = coords[k + 3] = j + off0;
               coords[k + 1] = coords[k + 4] = i + off1;
               coords[k + 2] = coords[k + 5] = scale * vals[m];
               if (ribbon)
                  coords[k + 4] += 1;
               else
                  coords[k + 5] = base;
            }
         break;
      case 1:
         for (int i = 0, k = 0; i < inDims[0]; i++)
            for (int j = 0; j < inDims[1]; j++, k += 6)
            {
               coords[k]     = coords[k + 3] = i + off0;
               coords[k + 1] = coords[k + 4] = j + off1;
               coords[k + 2] = coords[k + 5] = scale * vals[inDims[0] * j + i];
               if (ribbon)
                  coords[k + 3] += 1;
               else
                  coords[k + 5] = base;
            }
      }
      outField.updateExtents();
      outField.setExtents(extents);
      outField.setPhysExts(physExtents);
      
   }
   
   protected void show()
   {
      if (outField.getNCellSets() > 1)
      {
         outObj.fireStopRendering();
      }
      outObj.clearGeometries2D();
      irregularFieldGeometry.updateGeometry();
      outObj.addGeometry2D(irregularFieldGeometry.getColormapLegend());
      for (int i = 0; i < outField.getNCellSets(); i++)
      {
         outObj.addGeometry2D(irregularFieldGeometry.getColormapLegend(i));
      }
      outObj.setExtents(outBox.getExtents());
      if (params.showAxes())
      {
         axesObj.update(outBox, axesParams);
         outObj.addChild(axesObj);
      }
      try
      {
         Thread.sleep(100);
      } catch (InterruptedException ex)
      {
      }
      if (outField.getNCellSets() > 1)
      {
         outObj.fireStartRendering();
      }
   }


   @Override
   public void onActive()
   {
      boolean newField = false;
      fromUI = false;
      if (!params.isActive())
         return;
      VNRegularField inFld = (VNRegularField) getInputFirstValue("inField");
      if (inFld == null)
         return;
      RegularField in = inFld.getField();
      if (in == null || in.getNData() < 1 || 
          in.getDims().length != 2 || in.getDims()[0] < 2 || in.getDims()[1] < 2)
         return;
      if (in != inField || lastAxis != params.getAxis())
      {
         if (!in.isDataCompatibleWith(inField))
         {
            ignoreUI = true;
            computeUI.setInField(in);
            ignoreUI = false;
            newField = true;
         }
         if (!in.isStructureCompatibleWith(inField) || lastAxis != params.getAxis())
            newField = true;
         inField = in;
         inDims = inField.getDims();
      }
//      if (newField)
//      {
         axis = lastAxis = params.getAxis();
         outField = new IrregularField(2 * inField.getNNodes());
         outField.setNSpace(3);
         coords = new float[3 * outField.getNNodes()];
         outField.setCoords(coords);
         updateCells();
         CellArray ribbons = new CellArray(Cell.QUAD, cells, orientations, null);
         CellArray ribbonEdges = new CellArray(Cell.SEGMENT, edges, edgeOrientations, null);
         CellSet cs = new CellSet();
         cs.setCellArray(ribbons);
         cs.setBoundaryCellArray(ribbons);
         cs.setCellArray(ribbonEdges);
         cs.setBoundaryCellArray(ribbonEdges);
         outField.addCellSet(cs);
         for (DataArray dta : inField.getDataArrays())
            if (dta.isSimpleNumeric() && dta.getVeclen() == 1)
               switch (axis)
               {
               case 0:
                  switch (dta.getType())
                  {
                  case DataArray.FIELD_DATA_BYTE:
                     byte[] inDB = dta.getBData();
                     byte[] outDB = new byte[2 * dta.getNData()];
                     for (int i = 0, j = 0; i < inDB.length; i++, j += 2)
                        outDB[j] = outDB[j + 1] = inDB[i];
                     outField.addData(DataArray.create(outDB, 1, dta.getName()));
                     break;
                  case DataArray.FIELD_DATA_SHORT:
                     short[] inDS = dta.getSData();
                     short[] outDS = new short[2 * dta.getNData()];
                     for (int i = 0, j = 0; i < inDS.length; i++, j += 2)
                        outDS[j] = outDS[j + 1] = inDS[i];
                     outField.addData(DataArray.create(outDS, 1, dta.getName()));
                     break;
                  case DataArray.FIELD_DATA_INT:
                     int[] inDI = dta.getIData();
                     int[] outDI = new int[2 * dta.getNData()];
                     for (int i = 0, j = 0; i < inDI.length; i++, j += 2)
                        outDI[j] = outDI[j + 1] = inDI[i];
                     outField.addData(DataArray.create(outDI, 1, dta.getName()));
                     break;
                  case DataArray.FIELD_DATA_FLOAT:
                     float[] inDF = dta.getFData();
                     float[] outDF = new float[2 * dta.getNData()];
                     for (int i = 0, j = 0; i < inDF.length; i++, j += 2)
                        outDF[j] = outDF[j + 1] = inDF[i];
                     outField.addData(DataArray.create(outDF, 1, dta.getName()));
                     break;
                  case DataArray.FIELD_DATA_DOUBLE:
                     double[] inDD = dta.getDData();
                     double[] outDD = new double[2 * dta.getNData()];
                     for (int i = 0, j = 0; i < inDD.length; i++, j += 2)
                        outDD[j] = outDD[j + 1] = inDD[i];
                     outField.addData(DataArray.create(outDD, 1, dta.getName()));
                     break;
                  }
                  break;
               case 1:
                  switch (dta.getType())
                  {
                  case DataArray.FIELD_DATA_BYTE:
                     byte[] inDB = dta.getBData();
                     byte[] outDB = new byte[2 * dta.getNData()];
                     for (int i = 0, k = 0; i < inDims[0]; i++)
                        for (int j = 0; j < inDims[1]; j++, k += 2)
                           outDB[k] = outDB[k + 1] = inDB[inDims[0] * j + i];
                     outField.addData(DataArray.create(outDB, 1, dta.getName()));
                     break;
                  case DataArray.FIELD_DATA_SHORT:
                     short[] inDS = dta.getSData();
                     short[] outDS = new short[2 * dta.getNData()];
                     for (int i = 0, k = 0; i < inDims[0]; i++)
                        for (int j = 0; j < inDims[1]; j++, k += 2)
                           outDS[k] = outDS[k + 1] = inDS[inDims[0] * j + i];
                     outField.addData(DataArray.create(outDS, 1, dta.getName()));
                     break;
                  case DataArray.FIELD_DATA_INT:
                     int[] inDI = dta.getIData();
                     int[] outDI = new int[2 * dta.getNData()];
                     for (int i = 0, k = 0; i < inDims[0]; i++)
                        for (int j = 0; j < inDims[1]; j++, k += 2)
                           outDI[k] = outDI[k + 1] = inDI[inDims[0] * j + i];
                     outField.addData(DataArray.create(outDI, 1, dta.getName()));
                     break;
                  case DataArray.FIELD_DATA_FLOAT:
                     float[] inDF = dta.getFData();
                     float[] outDF = new float[2 * dta.getNData()];
                     for (int i = 0, k = 0; i < inDims[0]; i++)
                        for (int j = 0; j < inDims[1]; j++, k += 2)
                           outDF[k] = outDF[k + 1] = inDF[inDims[0] * j + i];
                     outField.addData(DataArray.create(outDF, 1, dta.getName()));
                     break;
                  case DataArray.FIELD_DATA_DOUBLE:
                     double[] inDD = dta.getDData();
                     double[] outDD = new double[2 * dta.getNData()];
                     for (int i = 0, k = 0; i < inDims[0]; i++)
                        for (int j = 0; j < inDims[1]; j++, k += 2)
                           outDD[k] = outDD[k + 1] = inDD[inDims[0] * j + i];
                     outField.addData(DataArray.create(outDD, 1, dta.getName()));
                     break;
                  }
                  break;
               }
//      }
      updateCoords();
      outBox.setExtents(extents);
      float[][] phExts = new float[2][3];
      float[][] exts = outField.getExtents();
      for (int i = 0; i < 2; i++)
         System.arraycopy(exts[i], 0, phExts[i], 0, 2);
      phExts[0][2] = inField.getData(params.getComponent()).getPhysMin();
      phExts[1][2] = inField.getData(params.getComponent()).getPhysMax();
      outBox.setPhysExts(phExts);
      setOutputValue("outField", new VNRegularField(outBox));
      axesObj.update(outBox, axesParams);
      computeUI.getAxesGUI().setInfield(outBox);
      prepareOutputGeometry();
      
      if (params.isRibbon())
      {
         irregularFieldGeometry.getFieldDisplayParams().setShadingMode(AbstractRenderingParams.GOURAUD_SHADED);
         irregularFieldGeometry.getFieldDisplayParams().setDisplayMode(renderingParams.getDisplayMode() & ~AbstractRenderingParams.EDGES);
      }
      else
      {
         irregularFieldGeometry.getFieldDisplayParams().setShadingMode(AbstractRenderingParams.BACKGROUND);
         irregularFieldGeometry.getFieldDisplayParams().setDisplayMode(renderingParams.getDisplayMode() | AbstractRenderingParams.EDGES);
      }
      show();
   }

}
