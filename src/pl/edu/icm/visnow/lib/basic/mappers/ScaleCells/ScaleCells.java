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

package pl.edu.icm.visnow.lib.basic.mappers.ScaleCells;


import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pl.edu.icm.visnow.datasets.dataarrays.BitArray;
import pl.edu.icm.visnow.datasets.cells.Cell;
import pl.edu.icm.visnow.datasets.CellArray;
import pl.edu.icm.visnow.datasets.CellSet;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.datasets.IrregularField;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.engine.core.InputEgg;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.geometries.parameters.AbstractRenderingParams;
import pl.edu.icm.visnow.lib.templates.visualization.modules.IrregularOutFieldVisualizationModule;
import pl.edu.icm.visnow.lib.types.VNField;
import pl.edu.icm.visnow.lib.types.VNIrregularField;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class ScaleCells extends IrregularOutFieldVisualizationModule
{

   public static InputEgg[] inputEggs = null;
   public static OutputEgg[] outputEggs = null;   
   private GUI computeUI = null;
   protected Field inField;
   protected RegularField inRegularField = null;
   protected IrregularField inIrregularField = null;
   protected Params params;
   protected float scale = .75f;
   protected boolean fromGUI = false;

   public ScaleCells()
   {
      parameters = params = new Params();
      params.addChangeListener(new ChangeListener()
      {
         public void stateChanged(ChangeEvent evt)
         {
            if (inIrregularField == null)
               return;
            fromGUI = true;
            scale = params.getScale();
            updateOutCoords(outField.getCoords(), outField.getNormals());
            irregularFieldGeometry.updateCoords();
            if (!params.isAdjusting())
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
      outObj.setName("scale cells");
   }


   private void updateOutCoords(float[] coords, float[] normals)
   {
      if (inIrregularField == null || coords == null)
         return;
      int n = 0;
      float[] c = new float[3];
      float[] inCoords = inIrregularField.getCoords();
      float[] inNormals = inIrregularField.getNormals();

      float[] u = new float[3];
      float[] v = new float[3];
      float[] w = new float[3];

      for (int i = 0; i < inIrregularField.getNCellSets(); i++)
      {
         CellSet inCS = inIrregularField.getCellSet(i);
         for (int j = 0; j < Cell.TYPES; j++)
            if (inCS.getCellArray(j) != null)
            {
               CellArray inCA = inCS.getCellArray(j);
               int nv = Cell.nv[j];
               int[] nodes = inCA.getNodes();
               for (int k = 0; k < inCA.getNCells(); k++)
               {
                  for (int l = 0; l < 3; l++)
                  {
                     c[l] = 0;
                     for (int m = k*nv; m < k*nv+nv; m++)
                        c[l] += inCoords[3*nodes[m]+l]/nv;
                  }
                  if (normals != null)
                  {
                  float wn = (float)(Math.sqrt(w[0]*w[0]+w[1]*w[1]+w[2]*w[2]));
                     for (int l = k*nv, nn = 3*k*nv; l < k*nv+nv; l++)
                        for (int m = 0; m < 3; m++, nn++)
                           normals[nn] = w[m]/wn;
                  }

                  for (int m = k*nv; m < k*nv+nv; m++)
                     for (int l = 0; l < 3; l++, n++)
                        coords[n] = scale * inCoords[3*nodes[m]+l] + (1-scale) * c[l];                  

               }
            }
      }
   }
   
   private void createOutField()
   {
      if (inIrregularField == null)
         return;
      int nNodes = 0;
      for (int i = 0; i < inIrregularField.getNCellSets(); i++)
      {
         CellSet cs = inIrregularField.getCellSet(i);
         for (int j = 0; j < Cell.TYPES; j++)
            if (cs.getCellArray(j) != null)
            nNodes += Cell.nv[j]*cs.getCellArray(j).getNCells();
      }
      outField = new IrregularField(nNodes);
      outField.setNSpace(3);
      float[] coords = new float[3*nNodes];
      outField.setCoords(coords);
      if (inIrregularField.getMask() != null)
      {
         int n = 0;
         boolean[] inMask = inIrregularField.getMask();
         boolean[] mask = new boolean[nNodes];
         for (int i = 0; i < inIrregularField.getNCellSets(); i++)
         {
            CellSet cs = inIrregularField.getCellSet(i);
            for (int j = 0; j < Cell.TYPES; j++)
               if (cs.getCellArray(j) != null)
               {
                  int[] nodes = cs.getCellArray(j).getNodes();
                  for (int k = 0; k < nodes.length; k++, n++)
                     mask[n] = inMask[nodes[k]];
               }
         }
         outField.setMask(mask);
      }
      int n = 0;
      for (int i = 0; i < inIrregularField.getNCellSets(); i++)
      {
         CellSet inCS = inIrregularField.getCellSet(i);
         CellSet outCS = new CellSet(inCS.getName());
         for (int j = 0; j < Cell.TYPES; j++)
            if (inCS.getCellArray(j) != null)
            {
               CellArray inCA = inCS.getCellArray(j);
               int[] nodes = new int[inCA.getNodes().length];
               for (int k = 0; k < nodes.length; k++, n++)
                  nodes[k] = n;
               CellArray outCA = new CellArray(j, nodes, inCA.getOrientations(), inCA.getDataIndices());
               outCS.setCellArray(outCA);
            }
         for (int j = 0; j < inCS.getNData(); j++)
            outCS.addData(inCS.getData(j));
         outCS.generateDisplayData(coords);
         outField.addCellSet(outCS);
      }
      for (int d = 0; d < inIrregularField.getNData(); d++)
      {
         DataArray inDA = inIrregularField.getData(d);
         if (!inDA.isSimpleNumeric())
            continue;
         DataArray outDA = null;
         n = 0;
         int vlen = inDA.getVeclen();
         switch (inDA.getType()) {
            case DataArray.FIELD_DATA_LOGIC:
               byte[] inbda = inDA.getBData();
               byte[] bda = new byte[nNodes*vlen];
               for (int i = 0; i < inIrregularField.getNCellSets(); i++)
               {
                  CellSet cs = inIrregularField.getCellSet(i);
                  for (int j = 0; j < Cell.TYPES; j++)
                     if (cs.getCellArray(j) != null)
                     {
                        int[] nodes = cs.getCellArray(j).getNodes();
                        for (int k = 0; k < nodes.length; k++)
                        {
                           int m = vlen*nodes[k];
                           for (int l = 0; l < vlen; l++, n++)
                              bda[n] = inbda[m+l];
                        }
                     }
               }
               outDA = DataArray.create(new BitArray(bda), inDA.getVeclen(), inDA.getName());
               break;
            
            case DataArray.FIELD_DATA_BYTE:
               byte[] inBD  = inDA.getBData();
               byte[] outBD = new byte[nNodes*vlen];
               for (int i = 0; i < inIrregularField.getNCellSets(); i++)
               {
                  CellSet cs = inIrregularField.getCellSet(i);
                  for (int j = 0; j < Cell.TYPES; j++)
                     if (cs.getCellArray(j) != null)
                     {
                        int[] nodes = cs.getCellArray(j).getNodes();
                        for (int k = 0; k < nodes.length; k++)
                        {
                           int m = vlen*nodes[k];
                           for (int l = 0; l < vlen; l++, n++)
                              outBD[n] = inBD[m+l];
                        }
                     }
               }
               outDA = DataArray.create(outBD, vlen, inDA.getName());
               break;
            case DataArray.FIELD_DATA_SHORT:
               short[] inSD = inDA.getSData();
               short[] outSD = new short[nNodes*vlen];
               for (int i = 0; i < inIrregularField.getNCellSets(); i++)
               {
                  CellSet cs = inIrregularField.getCellSet(i);
                  for (int j = 0; j < Cell.TYPES; j++)
                     if (cs.getCellArray(j) != null)
                     {
                        int[] nodes = cs.getCellArray(j).getNodes();
                        for (int k = 0; k < nodes.length; k++)
                        {
                           int m = vlen*nodes[k];
                           for (int l = 0; l < vlen; l++, n++)
                              outSD[n] = inSD[m+l];
                        }
                     }
               }
               outDA = DataArray.create(outSD, vlen, inDA.getName());
               break;
            case DataArray.FIELD_DATA_INT:
               int[] inID = inDA.getIData();
               int[] outID = new int[nNodes*vlen];
               for (int i = 0; i < inIrregularField.getNCellSets(); i++)
               {
                  CellSet cs = inIrregularField.getCellSet(i);
                  for (int j = 0; j < Cell.TYPES; j++)
                     if (cs.getCellArray(j) != null)
                     {
                        int[] nodes = cs.getCellArray(j).getNodes();
                        for (int k = 0; k < nodes.length; k++)
                        {
                           int m = vlen*nodes[k];
                           for (int l = 0; l < vlen; l++, n++)
                              outID[n] = inID[m+l];
                        }
                     }
               }
               outDA = DataArray.create(outID, vlen, inDA.getName());
               break;
            case DataArray.FIELD_DATA_FLOAT:
               float[] inFD = inDA.getFData();
               float[] outFD = new float[nNodes*vlen];
               for (int i = 0; i < inIrregularField.getNCellSets(); i++)
               {
                  CellSet cs = inIrregularField.getCellSet(i);
                  for (int j = 0; j < Cell.TYPES; j++)
                     if (cs.getCellArray(j) != null)
                     {
                        int[] nodes = cs.getCellArray(j).getNodes();
                        for (int k = 0; k < nodes.length; k++)
                        {
                           int m = vlen*nodes[k];
                           for (int l = 0; l < vlen; l++, n++)
                              outFD[n] = inFD[m+l];
                        }
                     }
               }
               outDA = DataArray.create(outFD, vlen, inDA.getName());
               break;
            case DataArray.FIELD_DATA_DOUBLE:
               double[] inDD = inDA.getDData();
               double[] outDD = new double[nNodes*vlen];
               for (int i = 0; i < inIrregularField.getNCellSets(); i++)
               {
                  CellSet cs = inIrregularField.getCellSet(i);
                  for (int j = 0; j < Cell.TYPES; j++)
                     if (cs.getCellArray(j) != null)
                     {
                        int[] nodes = cs.getCellArray(j).getNodes();
                        for (int k = 0; k < nodes.length; k++)
                        {
                           int m = vlen*nodes[k];
                           for (int l = 0; l < vlen; l++, n++)
                              outDD[n] = inDD[m+l];
                        }
                     }
               }
               outDA = DataArray.create(outDD, vlen, inDA.getName());
               break;
         }
         if (outDA != null) {             
            outDA.setMinv(inDA.getMinv());
            outDA.setMaxv(inDA.getMaxv());
            outDA.setPhysMin(inDA.getPhysMin());
            outDA.setPhysMax(inDA.getPhysMax());
            outDA.setUnit(inDA.getUnit());
            outDA.setUserData(inDA.getUserData());
            outField.addData(outDA);
         }
      }
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
            if (inField instanceof RegularField)
            {
               RegularField regularInField = (RegularField)inField;
               int[] dims = regularInField.getDims();
               int nNodes = regularInField.getNNodes();
               int nIndices = 0;
               int[] indices = null;
               float[] coords = inField.getCoords();
               float[][] aff = regularInField.getAffine();
               CellSet cs = new CellSet();
               switch (dims.length)
               {
               case 3:
                  int off2 = dims[0] * dims[1];
                  int off1 = dims[0];
                  nIndices = 8 * (dims[0] - 1) * (dims[1] - 1) * (dims[2] - 1);
                  indices = new int[nIndices];
                  for (int i = 0, m = 0; i < dims[2] - 1; i++)
                     for (int j = 0; j < dims[1] - 1; j++)
                        for (int k = 0, l = (i * dims[1] + j) * dims[0]; k < dims[0] - 1; k++, l++, m += 8)
                        {
                           indices[m    ] = l;
                           indices[m + 1] = l               + 1;
                           indices[m + 2] = l        + off1 + 1;
                           indices[m + 3] = l        + off1;
                           indices[m + 4] = l + off2;
                           indices[m + 5] = l + off2        + 1;
                           indices[m + 6] = l + off2 + off1 + 1;
                           indices[m + 7] = l + off2 + off1;
                        }

                  if (coords == null)
                  {
                     coords = new float[3 * nNodes];
                     for (int i = 0, m = 0; i < dims[2]; i++)
                        for (int j = 0; j < dims[1]; j++)
                           for (int k = 0; k < dims[0]; k++)
                              for (int n = 0; n < 3; n++, m++)
                                 coords[m] = aff[3][n] + i * aff[2][n] + j * aff[1][n] + k * aff[0][n];
                  }
                  boolean[] orientations = new boolean[(dims[0] - 1) * (dims[1] - 1) * (dims[2] - 1)];
                  for (int i = 0; i < orientations.length; i++)
                     orientations[i] = true;
                  CellArray ca = new CellArray(Cell.HEXAHEDRON, indices, orientations, indices);
                  cs.setCellArray(ca);
                  cs.generateExternFaces();
                  break;
               case 2:
                  off1 = dims[0];
                  nIndices = 4 * (dims[0] - 1) * (dims[1] - 1) ;
                  indices = new int[nIndices];
                     for (int j = 0, m = 0; j < dims[1] - 1; j++)
                        for (int k = 0, l = j * dims[0]; k < dims[0] - 1; k++, l++, m += 4)
                        {
                           indices[m    ] = l;
                           indices[m + 1] = l        + 1;
                           indices[m + 2] = l + off1 + 1;
                           indices[m + 3] = l + off1;
                        }

                  if (coords == null)
                  {
                     coords = new float[3 * nNodes];
                        for (int j = 0, m = 0; j < dims[1]; j++)
                           for (int k = 0; k < dims[0]; k++)
                              for (int n = 0; n < 3; n++, m++)
                                 coords[m] = aff[3][n] + j * aff[1][n] + k * aff[0][n];
                  }
                  orientations = new boolean[(dims[0] - 1) * (dims[1] - 1)];
                  for (int i = 0; i < orientations.length; i++)
                     orientations[i] = true;
                  ca = new CellArray(Cell.QUAD, indices, orientations, indices);
                  cs.setCellArray(ca);
                  cs.generateExternFaces();
                  break;
               case 1: 
                  nIndices = 2 * (dims[0] - 1);
                  indices = new int[nIndices];
                     break;
               }
               inIrregularField = new IrregularField(nNodes);
               inIrregularField.setCoords(coords);
               if (regularInField.getMask() != null)
                  inIrregularField.setMask(regularInField.getMask());
               inIrregularField.addCellSet(cs);
               for (DataArray da : inField.getData())
                  inIrregularField.addData(da);
            }
            else
            {
               inIrregularField = (IrregularField) inField;
            }
            outObj.clearAllGeometry();
            outGroup = null;
            inField = newInField;
            outObj.setName(inField.getName());
            createOutField();
            scale = params.getScale();
            updateOutCoords(outField.getCoords(), outField.getNormals());
            outField.setExtents(inField.getExtents());
            prepareOutputGeometry();
            irregularFieldGeometry.getFieldDisplayParams().setShadingMode(AbstractRenderingParams.FLAT_SHADED);
            show();
         }
      }
      setOutputValue("outField", new VNIrregularField(outField)); 
      fromGUI = false;
   }
}