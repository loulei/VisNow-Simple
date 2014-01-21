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
 exception statement from your version.
 */
//</editor-fold>

package pl.edu.icm.visnow.lib.basic.mappers.Streamlines;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pl.edu.icm.visnow.datasets.*;
import pl.edu.icm.visnow.datasets.cells.Cell;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.engine.core.Input;
import pl.edu.icm.visnow.engine.core.InputEgg;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.gui.events.FloatValueModificationEvent;
import pl.edu.icm.visnow.gui.events.FloatValueModificationListener;
import pl.edu.icm.visnow.lib.templates.visualization.modules.IrregularOutField1DVisualizationModule;
import pl.edu.icm.visnow.lib.types.VNField;
import pl.edu.icm.visnow.lib.types.VNIrregularField;
import pl.edu.icm.visnow.lib.types.VNRegularField;
import pl.edu.icm.visnow.lib.utils.CropDown;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;
import pl.edu.icm.visnow.lib.utils.geometry2D.GeometryObject2D;

/**
 * @author  Krzysztof S. Nowinski (know@icm.edu.pl)
 * Warsaw University, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class Streamlines extends IrregularOutField1DVisualizationModule
{

   public static InputEgg[] inputEggs = null;
   public static OutputEgg[] outputEggs = null;

   private ComputeStreamlines streamlines;
   protected GUI computeUI = null;
   private Params params;
   private Object lastIn = null;
   private Object lastPts = null;
   private Field inField = null;
   private RegularField inRegularField;
   private IrregularField inIrregularField;
   private Field pts;
   private Field downsizedPts;
   private int[] ptsDims = null;
   private int[] down = null;
   private Field flowField;
   private boolean fromParams = false;
   private boolean onInput = true;
   private FloatValueModificationListener progressListener =
       new FloatValueModificationListener()
       {
          public void floatValueChanged(FloatValueModificationEvent e)
          {
             setProgress(e.getVal());
          }
       };

   public Streamlines()
   {
      parameters = params = new Params();
      params.addChangeListener(new ChangeListener()
      {
         public void stateChanged(ChangeEvent evt)
         {
            if (onInput) return;
            fromParams = true;
            startAction();
         }
      });
      SwingInstancer.swingRunAndWait(new Runnable()
      {
         public void run()
         {
            computeUI = new GUI();
         }
      });
      computeUI.setParams(params);
      ui.addComputeGUI(computeUI);
      setPanel(ui);
      outObj.setName("Streamlines");
   }

   class Streamlines2D extends GeometryObject2D implements Cloneable
   {

      private int[] lines = null;
      private float[] coords = null;
      private float[] colors = null;

      public Streamlines2D(String name, int nvert, int nSpace, int[] lines, float[] c, float[] colors)
      {
         super(name);
         this.lines = lines;
         coords = new float[2 * nvert];
         for (int i = 0, k = 0, l = 0; i < nvert; i++, k += 1)
            for (int j = 0; j < 2; j++, k++, l++)
               coords[l] = c[k];
         this.colors = colors;
      }

      @Override
      public void drawLocal2D(Graphics2D g, AffineTransform at)
      {
         g.translate(at.getTranslateX(), at.getTranslateY());
         if (coords == null)
         {
            g.translate(-at.getTranslateX(), -at.getTranslateY());
            return;
         }
         for (int n = 0, k = 0; n < lines.length; n++, k += 1)
            for (int i = 0; i < lines[n] - 1; i++)
            {
               g.setColor(new Color(colors[3 * k], colors[3 * k + 1], colors[3 * k + 2]));
               g.drawLine((int) (coords[2 * k] * at.getScaleX()), (int) ((height - coords[2 * k + 1]) * at.getScaleY()),
                       (int) (coords[2 * k + 2] * at.getScaleX()), (int) ((height - coords[2 * k + 3]) * at.getScaleY()));
               k += 1;
            }
         g.translate(-at.getTranslateX(), -at.getTranslateY());
      }
   }
   
   @Override
   protected void prepareOutputGeometry()
   {
      if (outField == null)
         return;
      irregularFieldGeometry.setField(outField);
      irregularFieldGeometry.setIgnoreUpdate(true);
      if (irregularFieldGeometry.isNewParams())
      {
         fieldDisplayParams = irregularFieldGeometry.getFieldDisplayParams();
         defaultDisplayParams();
         fieldDisplayParams.addChangeListener(new ChangeListener()
            {
               public void stateChanged(ChangeEvent evt)
               {
                  show();
               }
            });
      }
      ui.getPresentation1DPanel().setInFieldDisplayData(outField, fieldDisplayParams);
      outObj.clearAllGeometry();
      outGroup = irregularFieldGeometry.getGeometry();
      irregularFieldGeometry.setIgnoreUpdate(false);
      outObj.addNode(outGroup);
   }
   
   private void checkAndDownsizeInPts()
   {
      down = params.getDown();
      int downsize = params.getDownsize();
      if (params.isDowsizeChanged())
      {
         params.setDowsizeChanged(false); 
         if (pts instanceof IrregularField)
         {
            IrregularField irregularDownsizedPoints;
            int dSize = params.getDownsize();
            if (dSize < 1)
                dSize = 1;
            if (dSize == 1)
            {
               downsizedPts = pts.clone();
               for (CellSet cs : ((IrregularField)downsizedPts).getCellSets())
                  cs.generateExternFaces();
            }
            else
            {
               downsizedPts = new IrregularField(pts.getNNodes() / dSize);
               downsizedPts.setNSpace(pts.getNSpace());
               int[] nodes = new int[pts.getNNodes() / dSize];
               for (int i = 0; i < nodes.length; i++)
                  nodes[i] = i;
               CellArray nodesArray = new CellArray(Cell.POINT, nodes, null, nodes);
               CellSet ptSet = new CellSet();
               ptSet.setCellArray(nodesArray);
               ptSet.setBoundaryCellArray(nodesArray);
               ((IrregularField)downsizedPts).addCellSet(ptSet);
               downsizedPts.addData(DataArray.create(nodes, 1, "dummy"));
               float[] ptsCoords = pts.getCoords();
               float[] downsizedCoords = new float[downsizedPts.getNSpace() * downsizedPts.getNNodes()];
               for (int i = 0, j = 0, k = 0; i < downsizedPts.getNNodes(); i++, k += downsize * downsizedPts.getNSpace())
                  for (int l = 0; l < downsizedPts.getNSpace(); l++, j++)
                     downsizedCoords[j] = ptsCoords[k + l];
               downsizedPts.setCoords(downsizedCoords);
            }
         }
         else
         {
            boolean downsized = false;
            for (int i = 0; i < ptsDims.length; i++)
               if (down[i] > 1)
               {
                  downsized = true;
                  break;
               }
            if (!downsized)
               downsizedPts = pts.clone();
            else
            {
               int[] downsizedDims = new int[ptsDims.length];
               for (int i = 0; i < downsizedDims.length; i++)
                  downsizedDims[i] = ptsDims[i] / down[i] + 1;
               downsizedPts = new RegularField(downsizedDims);
               downsizedPts.setNSpace(pts.getNSpace());
               int[] nodes = new int[downsizedPts.getNNodes()];
               for (int i = 0; i < nodes.length; i++)
                  nodes[i] = i;
               downsizedPts.addData(DataArray.create(nodes, 1, "dummy"));
               if (pts.getCoords() != null)
                  downsizedPts.setCoords(CropDown.downArray(pts.getCoords(), pts.getNSpace(), ptsDims, down));
               else
               {
                  float[][] ptsAffine = ((RegularField)pts).getAffine();
                  float[][] downsizedPtsAffine = new float[4][3];
                  for (int i = 0; i < 3; i++)
                  {
                     downsizedPtsAffine[3][i] = ptsAffine[3][i];
                     for (int j = 0; j < 3; j++)
                        downsizedPtsAffine[j][i] = down[j] * ptsAffine[j][i];
                  }
                  ((RegularField)downsizedPts).setAffine(downsizedPtsAffine);
               }
            }
         }
      }
   }

   @Override
   public void onActive()
   {
      boolean inChanged = false;
      boolean ptsChanged = false;
      if (!fromParams)
      {
         params.setActive(false);
         onInput = true;
         if ((getInputFirstValue("inField") == null || 
             ((VNField)getInputFirstValue("inField")).getField() == null ||
             ((VNField)getInputFirstValue("inField")).getField().getTrueDim() <2 ||
              getInputFirstValue("inField") == lastIn) && 
             (getInputFirstValue("startPoints") == null || 
              getInputFirstValue("startPoints") == lastPts) )
         {
            params.setActive(true);
            onInput = false;
            return;
         }
         if (getInputFirstValue("inField") != null)
         {
            inChanged = getInputFirstValue("inField") != lastIn ||
                        ((VNField)getInputFirstValue("inField")).getField() != inField;
            lastIn = getInputFirstValue("inField");
            inField = ((VNField)getInputFirstValue("inField")).getField();
            if (!inField.hasProperVectorComponent())
            {
                inField = null;
                outField = null;
               return;
            }
         }
         lastPts = getInputFirstValue("startPoints");
         if (getInputFirstValue("startPoints") == null || 
            ((VNField) getInputFirstValue("startPoints")).getField() == null)
         {
            ptsChanged = inChanged;
            pts = inField;
         }
         else
         {
            ptsChanged = true;
            pts = ((VNField)getInputFirstValue("startPoints")).getField();
         }
         if (inChanged)
         {
            if (streamlines != null)
               streamlines.clearFloatValueModificationListener();
            if (inField instanceof RegularField)
            {
               inIrregularField = null;
               inRegularField = (RegularField)inField;
               computeUI.setInField(inRegularField);
               streamlines = new ComputeRegularFieldStreamlines(inRegularField, params);
            }
            else
            {
               inRegularField = null;
               inIrregularField = (IrregularField)inField;
               computeUI.setInField(inIrregularField);
               streamlines = new ComputeIrregularFieldStreamlines(inIrregularField, params);
            }
            streamlines.addFloatValueModificationListener(progressListener);
            params.setDowsizeChanged(true);
         }
         if (ptsChanged)
         {
            if (pts instanceof RegularField)
            {
               RegularField regPts = (RegularField)pts;
               down = params.getDown();
               if (regPts.getDims() == null)
                  return;
               ptsDims = regPts.getDims();
               int n = (int) (Math.pow(3000., 1. / ptsDims.length));
               for (int i = 0; i < ptsDims.length; i++)
                  down[i] = (ptsDims[i] + n - 1) / n;
               params.setDownsize(1);
            }
            else
               params.setDownsize(pts.getNNodes() / 3000);
            computeUI.setInPts(pts);
         }
         onInput = false;
         params.setActive(true);
      }
      fromParams = false;
      if (inField == null)
         return;
      if (!inField.hasProperVectorComponent())
      {
//         JOptionPane.showMessageDialog(null,
//                     "Input field has no vector component",
//                     "warning",JOptionPane.WARNING_MESSAGE);
            inField = null;
            outField = null;
         return;
      }
      if (streamlines != null)
      {
         checkAndDownsizeInPts();
         streamlines.setStartPoints(downsizedPts);
         streamlines.updateStreamlines();
         outField = streamlines.getOutField();
         setOutputValue("streamlinesField", new VNIrregularField(outField));
         float[] outCoords = outField.getCoords();
         flowField = downsizedPts.clone();
         flowField.setCoords(outCoords);
         int[] validStepRange = new int[2 * flowField.getNNodes()];
         int[] tmpfrom = streamlines.getFromSteps();
         int[] tmpto   = streamlines.getToSteps();
         if (flowField instanceof RegularField)
         {
            RegularField rfField = (RegularField)flowField;
            int[] dims = rfField.getDims();
            switch (dims.length)
            {
               case 3:
                 for (int k = 0, m = k * dims[0] * dims[1]; k < dims[2]; k++)
                  {
                     int k0 = k - 1;
                     if (k0 < 0)       k0 = 0;
                     int k1 = k + 2;
                     if (k1 > dims[2]) k1 = dims[2];
                     for (int j = 0; j < dims[1]; j++)
                     {
                        int j0 = j - 1;
                        if (j0 < 0)       j0 = 0;
                        int j1 = j + 2;
                        if (j1 > dims[1]) j1 = dims[1];
                        for (int i = 0; i < dims[0]; i++, m++)
                        {
                           int t = Integer.MAX_VALUE;
                           int f = Integer.MIN_VALUE;
                           int i0 = i-1;
                           if (i0 < 0)       i0 = 0;
                           int i1 = i + 2;
                           if (i1 > dims[0]) i1 = dims[0];
                           for (int kk = k0, ks = k0 - k + 1; kk < k1; kk++, ks++)
                              for (int jj = j0, js = j0 - j + 1; jj < j1; jj++, js++)
                                 for (int ii = i0, is = i0 - i + 1, l = (kk * dims[1] + jj) * dims[0] + i0; ii < i1; ii++, is++, l++)
                                 {
                                    if (f < tmpfrom[l]) f = tmpfrom[l];
                                    if (t > tmpto[l])   t = tmpto[l];
                                 }
                           validStepRange[2 * m]     = f;
                           validStepRange[2 * m + 1] = t;
                        }
                     }
                  }
                  break;
               case 2:
                  for (int j = 0, m = j * dims[0]; j < dims[1]; j++)
                  {
                     int j0 = j - 1;
                     if (j0 < 0)       j0 = 0;
                     int j1 = j + 2;
                     if (j1 > dims[1]) j1 = dims[1];
                     for (int i = 0; i < dims[0]; i++, m++)
                     {
                        int t = Integer.MAX_VALUE;
                        int f = Integer.MIN_VALUE;
                        int i0 = i-1;
                        if (i0 < 0)       i0 = 0;
                        int i1 = i + 2;
                        if (i1 > dims[0]) i1 = dims[0];
                           for (int jj = j0, js = j0 - j + 1; jj < j1; jj++, js++)
                              for (int ii = i0, is = i0 - i + 1, l =jj * dims[0] + i0; ii < i1; ii++, is++, l++)
                              {
                                 if (f < tmpfrom[l]) f = tmpfrom[l];
                                 if (t > tmpto[l])   t = tmpto[l];
                              }
                        validStepRange[2 * m]     = f;
                        validStepRange[2 * m + 1] = t;
                     }
                  }
                  break;
               case 1:
                  for (int i = 0; i < dims[0]; i++)
                  {
                     int t = Integer.MAX_VALUE;
                     int f = Integer.MIN_VALUE;
                     int i0 = i - 1;
                     if (i0 < 0)
                        i0 = 0;
                     int i1 = i + 2;
                     if (i1 > dims[0])
                        i1 = dims[0];
                     for (int ii = i0, is = i0 - i + 1, l = i0; ii < i1; ii++, is++, l++)
                     {
                        if (f < tmpfrom[l])
                           f = tmpfrom[l];
                        if (t > tmpto[l])
                           t = tmpto[l];
                     }
                     validStepRange[2 * i] = f;
                     validStepRange[2 * i + 1] = t;
                  }
                  break;
            }
         }
         else
         {
            int n = flowField.getNNodes();
            IrregularField irfField = (IrregularField)flowField;
            for (int i = 0; i < n; i++) 
            {
               validStepRange[2 * i]     = Integer.MAX_VALUE;
               validStepRange[2 * i + 1] = Integer.MIN_VALUE;
            }
            for (CellSet cs : irfField.getCellSets()) 
               for (CellArray ca: cs.getCellArrays())
                  if (ca != null)
                  {
                     int k = ca.getCellNodes();
                     int[] nodes = ca.getNodes();
                     for (int i = 0; i < nodes.length; i += k) 
                     {
                        int t = Integer.MAX_VALUE;
                        int f = Integer.MIN_VALUE;
                        for (int j = 0; j < k; j++) 
                        {
                           int l = nodes[i + j];
                           if (f < tmpfrom[l]) f = tmpfrom[l];
                           if (t > tmpto[l])   t = tmpto[l];
                        }
                        for (int j = 0; j < k; j++) 
                        {
                           int l = nodes[i + j];
                           if (f > validStepRange[2 * l]) validStepRange[2 * l] = f;
                           if (t < validStepRange[2 * l + 1])   validStepRange[2 * l] = t;
                        }
                     }
                  }
            
         }
         DataArray da = DataArray.create(validStepRange, 2, "valid time range");
         da.setUserData(new String[]{"valid time range"});
         flowField.addData(da);
         
        if (flowField != null && flowField instanceof RegularField) {
            setOutputValue("regularFlowField", new VNRegularField((RegularField)flowField));
            setOutputValue("irregularFlowField", null);
        } else if(flowField != null && flowField instanceof IrregularField) {
            setOutputValue("regularFlowField", null);
            setOutputValue("irregularFlowField", new VNIrregularField((IrregularField)flowField));
        } else {
            setOutputValue("regularFlowField", null);
            setOutputValue("irregularFlowField", null);
        }
         
         prepareOutputGeometry();
         show();
      }
  }
}
