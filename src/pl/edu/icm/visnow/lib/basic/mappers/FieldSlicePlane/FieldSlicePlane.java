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

package pl.edu.icm.visnow.lib.basic.mappers.FieldSlicePlane;


import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.datasets.cells.SimplexPosition;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.engine.core.InputEgg;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.geometries.objects.RegularField2DGeometry;
import pl.edu.icm.visnow.lib.templates.visualization.modules.RegularOutFieldVisualizationModule;
import pl.edu.icm.visnow.lib.types.VNField;
import pl.edu.icm.visnow.lib.types.VNRegularField;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class FieldSlicePlane extends RegularOutFieldVisualizationModule
{
   protected static final int TRANSFORM = 0;
   protected static final int AXIS      = 1;
   protected static final int DIMS      = 2;
   protected int change = TRANSFORM;
           
   public static InputEgg[] inputEggs = null;
   public static OutputEgg[] outputEggs = null;
   
   protected Field inField = null;
   protected GUI computeUI = null;
   protected boolean fromUI = false;
   protected boolean ignoreUI = false;
   protected Params params;      
   protected float[][] extents = new float[][] {{0,0,0},{1,1,1}};
   protected float[][] baseAffine = new float[4][3];
   protected float[][] affine = new float[4][3];
   protected float[][] inData;
   protected float[][] outData;
   protected int[] dims; 
   protected int[] lastDimensions = new int[]{100, 100}; 
   protected DataArray[] dArrs;
   protected int lastAxis = -1;
   protected int nThreads = 1;
   protected int[] inDims;
   protected float[][] inAffine = null;
   protected float[][] inInvAffine = null;
   protected boolean[] valid = null;
   
   
   public FieldSlicePlane()
   {
      parameters = params = new Params();
      params.addChangeListener(new ChangeListener()
      {
         public void stateChanged(ChangeEvent evt)
         {
            if (inField == null)
               return;
            fromUI = true;
            if(ignoreUI)
            {
               ignoreUI = false;
               return;
            }
            change = TRANSFORM;
            if (params.getAxis() != lastAxis)
               change = AXIS;
            for (int i = 0; i < 2; i++)
               if (params.getResolution()[i] != dims[i] && change != DIMS)
                  change = DIMS;
              startAction();
         }
      });
      params.getTransform().addChangeListener(new ChangeListener() {
         public void stateChanged(ChangeEvent e)
         {
            fromUI = true;
            if (inField == null || (inField.getCoords() != null && inField.getGeoTree() == null))
               return;
            transformSlice();
            if (!params.getTransform().isAdjusting())
               startAction();
         }
      });
      SwingInstancer.swingRun(new Runnable()
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
      float[][] trMatrix = params.getTransform().getMatrix();
      for (int i = 0; i < 4; i++)
      {
         for (int j = 0; j < 3; j++)
         {
            affine[i][j] = 0;
            for (int k = 0; k < 3; k++)
                affine[i][j] += trMatrix[j][k] * baseAffine[i][k];
            if(i==3)
                affine[i][j] += trMatrix[j][3];
         }
      }
      outField.setAffine(affine);
      updateSliceData();
   }

   class IrregularFieldInterpolator implements Runnable
   {
      int nThreads      = 1;
      int iThread       = 0;
      public IrregularFieldInterpolator(int nThr, int iThr)
      {
         this.nThreads       = nThr;
         this.iThread        = iThr;
      }

      public void run()
      {
         boolean[] inMask  = inField.getMask();
         float[] p = new float[3];
         for (int i = iThread; i < dims[1]; i += nThreads)
            for (int j = 0, l = i * dims[0]; j < dims[0]; j++, l++)
            {
               for (int k = 0; k < 3; k++)
                  p[k] = affine[3][k] + i * affine[1][k] + j * affine[0][k];
               SimplexPosition tCoords = inField.getFieldCoords(p);
               valid[l] = tCoords != null;
               if (tCoords != null)
               {
                  boolean isValid = true;
                  if (inMask != null)
                     for (int k = 0; k < 4; k++)
                        isValid = isValid && inMask[tCoords.verts[k]];
                  if (isValid)
                     for (int k = 0; k < inData.length; k++)
                     {
                        int vl = dArrs[k].getVeclen();
                        for (int m = 0; m < 4; m++)
                        {
                           int s = vl * tCoords.verts[m];
                           int t = vl * l;
                           for (int n = 0; n < vl; n++)
                              outData[k][t + n] += tCoords.coords[m] * inData[k][s + n];
                        }
                     }
                  else
                     valid[l] = false;
               }
         }
      }
   }

   class AffineFieldInterpolator implements Runnable
   {
      int nThreads      = 1;
      int iThread       = 0;

      public AffineFieldInterpolator(int nThr, int iThr)
      {
         this.nThreads       = nThr;
         this.iThread        = iThr;
      }

      public void run()
      {
         float[] p = new float[3];
         float[] q = new float[3];
         int[] ind = new int[3];
         int ii, ij, ik;
         float t, u, v;
         int off2 = inDims[0] * inDims[1];
         int off1 = inDims[0];
         boolean[] inMask  = inField.getMask();
         boolean isValid = true;
         for (int i = iThread; i < dims[1]; i += nThreads)
            for (int j = 0, l = i * dims[0]; j < dims[0]; j++, l++)
            {
               for (int k = 0; k < 3; k++)
                  p[k] = affine[3][k] + i * affine[1][k] + j * affine[0][k] - inAffine[3][k];
               for (int k = 0; k < 3; k++)
               {
                  q[k] = 0;
                  for (int m = 0; m < 3; m++)
                     q[k] += inInvAffine[k][m] * p[m];
                  ind[k] = (int)(q[k]+100000) - 100000;
                  q[k] -= ind[k];
               }
               ii = ind[2]; t = q[2];
               ij = ind[1]; u = q[1];
               ik = ind[0]; v = q[0];
               valid[l] = !(ii < 0 || ii >= inDims[2] - 1 || 
                            ij < 0 || ij >= inDims[1] - 1 || 
                            ik < 0 || ik >= inDims[0] - 1);
               if (inMask != null)
               {
                  int n = ik + off1 * ij + off2 * ii;
                  isValid = isValid && inMask[n]        && inMask[n + 1]        && inMask[n + off1]        && inMask[n + off1 + 1] && 
                                       inMask[n + off2] && inMask[n + off2 + 1] && inMask[n + off2 + off1] && inMask[n + off2 + off1 + 1];
               }
               if (isValid)
               for (int k = 0; k < inData.length; k++)
               {
                  int vl = dArrs[k].getVeclen();
                  int n = ik + off1 * ij + off2 * ii;
                  float[] data = inData[k];
                  if (!valid[l])
                     for (int m = 0; m < vl; m++)
                        outData[k][l * vl + m] = 0;
                  else
                     for (int m = 0; m < vl; m++)
                        outData[k][vl * l + m] = 
                           (1 - t) * ((1 - u) * ((1 - v) * data[vl *  n                + m] + v * data[vl * (n               + 1) + m]) +
                                           u  * ((1 - v) * data[vl * (n        + off1) + m] + v * data[vl * (n        + off1 + 1) + m])) +
                                t  * ((1 - u) * ((1 - v) * data[vl * (n + off2)        + m] + v * data[vl * (n + off2        + 1) + m]) +
                                           u  * ((1 - v) * data[vl * (n + off2 + off1) + m] + v * data[vl * (n + off2 + off1 + 1) + m]));
               }
               else
                  valid[l] = false; 
            }
      }
   }
   
   private void updateSliceData()
   {
      Thread[] workThreads = new Thread[nThreads];
      for (int i = 0; i < outData.length; i++)
         for (int j = 0; j < outData[i].length; j++)
            outData[i][j] = 0;
      if (inField instanceof RegularField && inField.getCoords() == null)
      {
         inDims = ((RegularField)inField).getDims();
         inAffine = ((RegularField)inField).getAffine();
         inInvAffine = ((RegularField)inField).getInvAffine();
      }
      for (int iThread = 0; iThread < nThreads; iThread++)
      {
         if (inField instanceof RegularField && inField.getCoords() == null)
            workThreads[iThread] = new Thread(new AffineFieldInterpolator(nThreads, iThread));
         else
            workThreads[iThread] = new Thread(new IrregularFieldInterpolator(nThreads, iThread));
         workThreads[iThread].start();
      }
      for (int i = 0; i < workThreads.length; i++)
         try {workThreads[i].join();}
         catch (Exception e){}
      if (regularFieldGeometry != null)
         ((RegularField2DGeometry)regularFieldGeometry).updateData();
   }
   
   private void updateOutField()
   {
      dims = params.getResolution();
      System.arraycopy(dims, 0, lastDimensions, 0, dims.length);
      outField = new RegularField(dims);
      outField.setNSpace(3);
      outField.setAffine(affine);
      valid = new boolean[outField.getNNodes()];
      outField.setMask(valid);
      int nNumDArrays = 0;
      for (int i = 0; i < inField.getNData(); i++)
         if (inField.getData(i).isSimpleNumeric()) nNumDArrays += 1;
      inData  = new float[nNumDArrays][];
      outData = new float[nNumDArrays][];
      dArrs = new DataArray[nNumDArrays];
      for (int i = 0, j = 0; i < inField.getNData(); i++)
         if (inField.getData(i).isSimpleNumeric())
         {
            DataArray da = inField.getData(i);
            inData[j] = da.getFData();
            dArrs[j] = inField.getData(j);
            outData[j] = new float[dArrs[j].getVeclen() * dims[0] * dims[1]];
            outField.addData(DataArray.create(outData[j], dArrs[j].getVeclen(), dArrs[j].getName()));
            outField.getData(j).setMaxv(dArrs[j].getMaxv());
            outField.getData(j).setMinv(dArrs[j].getMinv());
            outField.getData(j).setPhysMin(dArrs[j].getPhysMin());
            outField.getData(j).setPhysMax(dArrs[j].getPhysMax());
            j += 1;
         }

   }
   
   private void updateDims()
   {
      dims = params.getResolution();
      for (int i = 0; i < 2; i++)
      {
         for (int j = 0; j < 3; j++)
         {
            baseAffine[i][j] *= (float)lastDimensions[i]/(float)dims[i];
            affine[i][j] *= (float)lastDimensions[i]/(float)dims[i];
         }
         lastDimensions[i] = dims[i];
      }
      updateOutField();
   }
   
   private void updateInitSlicePosition()
   {

      for (int i = 0; i < 4; i++)
         for (int j = 0; j < 3; j++)
            affine[i][j] = baseAffine[i][j] = 0;
      switch (params.getAxis())
      {
      case 0:
         baseAffine[3][0] = .5f * (extents[0][0] + extents[1][0]);
         baseAffine[3][1] = extents[0][1];
         baseAffine[3][2] = extents[0][2];
         baseAffine[0][1] = (extents[1][1] - extents[0][1]) / (dims[0] - 1);
         baseAffine[1][2] = (extents[1][2] - extents[0][2]) / (dims[1] - 1);
         break;
      case 1:
         baseAffine[3][0] = extents[0][0];
         baseAffine[3][1] = .5f * (extents[0][1] + extents[1][1]);
         baseAffine[3][2] = extents[0][2];
         baseAffine[0][0] = (extents[1][0] - extents[0][0]) / (dims[0] - 1);
         baseAffine[1][2] = (extents[1][2] - extents[0][2]) / (dims[1] - 1);
         break;
      case 2:
         baseAffine[3][0] = extents[0][0];
         baseAffine[3][1] = extents[0][1];
         baseAffine[3][2] = .5f * (extents[0][2] + extents[1][2]);
         baseAffine[0][0] = (extents[1][0] - extents[0][0]) / (dims[0] - 1);
         baseAffine[1][1] = (extents[1][1] - extents[0][1]) / (dims[1] - 1);
         break;
      }
      for (int i = 0; i < 4; i++)
         System.arraycopy(baseAffine[i], 0, affine[i], 0, 3);
      lastAxis = params.getAxis();
      outField.setAffine(affine);
   }
   
   

   @Override
   public void onActive()
   {
      if (!fromUI)
      {
         if (getInputFirstValue("inField") == null)
            return;
         Field field = ((VNField) getInputFirstValue("inField")).getField();
         if (field == null)
            return;
         if (inField != field)
         {
            inField = field;
            extents = inField.getExtents();
            computeUI.setInField(inField);
            if (inField.getGeoTree() == null && inField.getCoords() != null)
            {
               SwingInstancer.swingRun(new Runnable()
               {
                  public void run()
                  {
                     computeUI.creatingTreeInfo(true);
                  }
               });
               inField.createGeoTree();
               if (inField.getGeoTree() == null)
                  return;
            }
            SwingInstancer.swingRun(new Runnable()
            {
               public void run()
               {
                  computeUI.creatingTreeInfo(false);
               }
            });
            updateOutField();
            prepareOutputGeometry();
            updateInitSlicePosition();
            lastAxis = -1;
         }
      }
      fromUI = false;
      switch (change)
      {
      case TRANSFORM:
         break;
      case AXIS:
         updateInitSlicePosition();
         break;
      case DIMS:
         updateDims();
         regularFieldGeometry.updateGeometry(outField);
      }
      change = TRANSFORM;
      updateSliceData();
      setOutputValue("outField", new VNRegularField(outField));
      show();
   }
}
