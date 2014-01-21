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

package pl.edu.icm.visnow.lib.basic.mappers.Isolines;

import java.util.Arrays;
import java.util.Vector;
import pl.edu.icm.visnow.datasets.CellArray;
import pl.edu.icm.visnow.datasets.CellSet;
import pl.edu.icm.visnow.datasets.IrregularField;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.datasets.cells.Cell;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
/**
 *
 * @author  Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class RegularFieldIsolines
{
   private IsolinesParams params;
   private RegularField inField = null;
   private int inComponent;
   private int[] dims;
   private float[] fData;
   private float[] thresholds;
   private float threshold;
   private int npoint,  midpoint;
   private Vector<float[]>[] lines = null;
   private float[] t_coords;
   private boolean[] done;
   private boolean isMask = false;
   private boolean[] mask = null;
   private int width;
   private int height;
   private IrregularField outField = null;
   private int nNodes = 0;
   private int nPolys = 0;
   private float[] coords = null;
   private DataArray[] outData = null;

   @SuppressWarnings("unchecked")
   public RegularFieldIsolines(RegularField inField, IsolinesParams params)
   {
      inComponent = params.getComponent();
      thresholds = params.getThresholds();
      if (inField == null || inField.getDims() == null || inField.getDims().length != 2 ||
              thresholds == null || thresholds.length < 1)
         return;
      this.inField = inField;
      this.params  = params;
      isMask = inField.isMask();
      if (isMask)
         mask = inField.getMask();
      dims = inField.getDims();
      width = dims[0];
      height = dims[1];
      DataArray inDA = inField.getData(inComponent);
      if (inDA.getVeclen() == 1)
         fData = inField.getData(inComponent).getFData();
      else
         fData = inField.getData(inComponent).getVectorNorms();
      float u, v0, v1, v2;
      int endln;
      int i, ii, j;
      i = ii = j = 0;
      done = new boolean[height * width * 2];
      lines = (Vector<float[]>[]) new Vector[thresholds.length];
      for (int iv = 0; iv < thresholds.length; iv++)
      {
         threshold = thresholds[iv];
         lines[iv] = new Vector<float[]>();
         for (int iy = 0; iy < done.length; iy++)
            done[iy] = false;

         t_coords = new float[2 * width * height];

         for (int iy = 0; iy < height - 1; iy++)
         {
            for (int ix = 0, l = width * iy; ix < width - 1; ix++, l++)
            {
               if (done[2 * l])
                  continue;
               done[2 * l] = true;
               v0 = fData[iy * width + ix] - threshold;
               v1 = fData[iy * width + ix + 1] - threshold;
               v2 = fData[(iy + 1) * width + ix] - threshold;
               if (v0 == 0.)
                  v0 = 1.e-10f;
               if (v1 == 0.)
                  v1 = 1.e-10f;
               if (v2 == 0.)
                  v2 = 1.e-10f;
               npoint = midpoint = 0;

               if (v0 * v1 < 0)
               {
                  u = 1.f / (v1 - v0);
                  t_coords[2 * npoint] = ix - u * v0;
                  t_coords[2 * npoint + 1] = iy;
                  npoint += 1;
                  Triangle tstart = new Triangle(ix, iy, 0, 2);
                  if (tstart.isValid())
                  {
                     endln = 0;
                     while (endln == 0)
                        endln = tstart.traverse();
                     midpoint = npoint;
                     if (endln == 2 && iy > 0)
                     {
                        tstart = new Triangle(ix, iy - 1, 1, 2);
                        while (tstart.traverse() == 0)
                           continue;
                     }
                  }
               } else if (v0 * v2 < 0)
               {
                  u = 1.f / (v2 - v0);
                  t_coords[2 * npoint] = ix;
                  t_coords[2 * npoint + 1] = iy - u * v0;
                  npoint += 1;
                  Triangle tstart = new Triangle(ix, iy, 0, 1);
                  if (tstart.isValid())
                  {
                     endln = 0;
                     while (endln == 0)
                        endln = tstart.traverse();
                     midpoint = npoint;
                     if (endln == 2 && ix > 0)
                     {
                        tstart = new Triangle(ix - 1, iy, 1, 1);
                        while (tstart.traverse() == 0)
                           continue;
                     }
                  }
               }
               if (npoint > 2)
                  try
                  {
                     float[] iCoords = new float[2 * npoint];
                     for (ii = midpoint - 1  ,i=0; ii >= 0; ii--, i++)
                        for (j = 0; j < 2; j++)
                           iCoords[2 * i + j] = t_coords[2 * ii + j];
                     for (ii = midpoint; ii < npoint; ii++, i++)
                        for (j = 0; j < 2; j++)
                           iCoords[2 * i + j] = t_coords[2 * ii + j];
                     lines[iv].add(iCoords);
                  } catch (Exception e)
                  {
                     System.out.println("i=" + i + " ii=" + ii + " j=" + j);
                  }
            }
         }
      }
      //now remove all unnecessary links:
      fData = null;
      done = null;
      t_coords = null;
      createOutField();
   }

   public Vector<float[]>[] getLines()
   {
      return (lines);
   }


   // TRIANGLE *****************************************
   private class Triangle
   {
      private int ix,  iy;
      private int is_up;
      private float[][] coords;
      private float[] vals;
      private int edge_in;
      private boolean valid = true;

      public Triangle(int inx, int iny, int inIs_up, int inEdge_in)
      {
         if (inx < 0 || inx >= width || iny < 0 || iny >= height)
         {
            ix = iy = -999;
            throw new IllegalArgumentException("Triangle out of range.");
         }
         ix = inx;
         iy = iny;
         is_up = inIs_up;
         coords = new float[3][2];
         vals = new float[3];
         loadValues();
         edge_in = inEdge_in;
      }

      private void loadValues()
      {
         if (is_up == 1)
         {
            coords[0][0] = ix + 1;
            coords[0][1] = iy + 1;
            vals[0] = fData[(iy + 1) * width + ix + 1] - threshold;
            coords[1][0] = ix;
            coords[1][1] = iy + 1;
            vals[1] = fData[(iy + 1) * width + ix] - threshold;
            coords[2][0] = ix + 1;
            coords[2][1] = iy;
            vals[2] = fData[iy * width + ix + 1] - threshold;
            if (isMask)
               valid = mask[(iy + 1) * width + ix + 1] && 
                       mask[(iy + 1) * width + ix] && 
                       mask[iy * width + ix + 1];
         } else
         {
            coords[0][0] = ix;
            coords[0][1] = iy;
            vals[0] = fData[iy * width + ix] - threshold;
            coords[1][0] = ix + 1;
            coords[1][1] = iy;
            vals[1] = fData[iy * width + ix + 1] - threshold;
            coords[2][0] = ix;
            coords[2][1] = iy + 1;
            vals[2] = fData[(iy + 1) * width + ix] - threshold;
            if (isMask)
               valid = mask[iy * width + ix] && 
                       mask[iy * width + ix + 1] && 
                       mask[(iy + 1) * width + ix];
         }
         for (int i = 0; i < 3; i++)
            if (vals[i] == 0.)
               vals[i] = 1.e-10f;
      }

      public int traverse()
      {
         int edge_out = -1;
         int is_up_out = 1 - is_up;
         int dirx, diry;
         done[2 * (iy * width + ix) + is_up] = true;
         float u = .5f;
         dirx = diry = 0;
         switch (edge_in)
         {
            case 0:
               if (vals[0] * vals[1] >= 0.)
                  edge_out = 1;
               else
                  edge_out = 2;
               break;
            case 1:
               if (vals[1] * vals[2] >= 0.)
                  edge_out = 2;
               else
                  edge_out = 0;
               break;
            case 2:
               if (vals[2] * vals[0] >= 0.)
                  edge_out = 0;
               else
                  edge_out = 1;
               break;
         }
         switch (edge_out)
         {
            case 0:
               break;
            case 1:
               u = 1.f / (vals[2] - vals[0]);
               for (int i = 0; i < 2; i++)
                  t_coords[2 * npoint + i] = u * (vals[2] * coords[0][i] - vals[0] * coords[2][i]);
               npoint++;
               if (is_up == 1)
                  dirx = 1;
               else
                  dirx = -1;
               if (ix + dirx < 0 || ix + dirx >= width - 1)
                  return 2;
               if (done[2 * (iy * width + ix + dirx) + is_up_out])
                  return (1);
               break;
            case 2:
               u = 1.f / (vals[0] - vals[1]);
               for (int i = 0; i < 2; i++)
                  t_coords[2 * npoint + i] = u * (vals[0] * coords[1][i] - vals[1] * coords[0][i]);
               npoint++;
               if (is_up == 1)
                  diry = 1;
               else
                  diry = -1;
               if (iy + diry < 0 || iy + diry >= height - 1)
                  return 2;
               if (done[2 * ((iy + diry) * width + ix) + is_up_out])
                  return (1);
               break;
         }
         ix += dirx;
         iy += diry;
         is_up = is_up_out;
         edge_in = edge_out;
         loadValues();
         if (!valid)
            return 2;
         return 0;
      }

      public boolean isValid()
      {
         return valid;
      }
   }

   private void createOutField()
   {
      boolean[] mappedComponents = params.getMappedCompoents();
      for (int i = 0; i < lines.length; i++)
      {
         nPolys += lines[i].size();
         for (float[] line : lines[i])
            nNodes += line.length / 2;
      }
      if (nNodes < 2 || nPolys < 1)
         return;
      coords = new float[3 * nNodes];
      int[] polys = new int[nPolys];
      int nSegs = 0;
      int nOutData = 0;
      for (int i = 0; i < inField.getNData(); i++)
         if (mappedComponents[i] && i != inComponent)
            nOutData += 1;
      outData = new DataArray[nOutData];
      for (int i = 0, j = 0; i < inField.getNData(); i++)
         if (mappedComponents[i] && i != inComponent)
         {
            DataArray inDA = inField.getData(i);
            outData[j] = DataArray.create(inDA.getType(), nNodes, inDA.getVeclen(), inDA.getName(), inDA.getUnit(), inDA.getUserData());
            j += 1;
         }
      for (int n = 0, k = 0, l = 0; n < lines.length; n++)
      {
         for (float[] line : lines[n])
         {
            polys[k] = line.length / 2;
            nSegs += polys[k] - 1;
            k += 1;
            for (int i = 0; i < line.length; i += 2, l++)
            {
               float[] c = inField.getGridCoords(line[i], line[i + 1]);
               System.arraycopy(c, 0, coords, 3 * l, c.length);
               for (int j = c.length; j < 3; j++)
                  coords[3 * l + j] = 0;
               if (nOutData > 0)
                  for (int id = 0, iod = 0; id < inField.getNData(); id++)
                     if (mappedComponents[id] && id != inComponent)
                     {
                        DataArray da = inField.getData(id);
                        int vl = da.getVeclen();
                        switch (da.getType())
                        {
                        case DataArray.FIELD_DATA_BYTE:
                           System.arraycopy(inField.getInterpolatedData(da.getBData(), line[i], line[i + 1], 0.f), 0,
                                   outData[iod].produceBData(0), l * vl, vl);
                           break;
                        case DataArray.FIELD_DATA_SHORT:
                           System.arraycopy(inField.getInterpolatedData(da.getSData(), line[i], line[i + 1], 0.f), 0,
                                   outData[iod].produceSData(0), l * vl, vl);
                           break;
                        case DataArray.FIELD_DATA_INT:
                           System.arraycopy(inField.getInterpolatedData(da.getIData(), line[i], line[i + 1], 0.f), 0,
                                   outData[iod].produceIData(0), l * vl, vl);
                           break;
                        case DataArray.FIELD_DATA_FLOAT:
                           System.arraycopy(inField.getInterpolatedData(da.getFData(), line[i], line[i + 1], 0.f), 0,
                                   outData[iod].produceFData(0), l * vl, vl);
                           break;
                        case DataArray.FIELD_DATA_DOUBLE:
                           System.arraycopy(inField.getInterpolatedData(da.getDData(), line[i], line[i + 1], 0.f), 0,
                                   outData[iod].produceDData(0), l * vl, vl);
                           break;
                        }
                        iod += 1;
                     }
            }
         }
      }
      int[] edges = new int[2 * nSegs];
      for (int i = 0, k = 0, l = 0; i < polys.length; i++)
      {
         for (int j = 0; j < polys[i] - 1; j++)
         {
            edges[l] = k + j;
            edges[l + 1] = k + j + 1;
            l += 2;
         }
         k += polys[i];
      }

      outField = new IrregularField(nNodes);
      float[] outThr = new float[nNodes];
      for (int i = 0, k = 0; i < lines.length; i++)
      {
         int l = 0;
         for (float[] line : lines[i])
            l += line.length / 2;
         Arrays.fill(outThr, k, k + l, thresholds[i]);
         k += l;
      }
      outField.addData(DataArray.create(outThr, 1, inField.getData(inComponent).getName()));
      for (int i = 0; i < outData.length; i++)
      {
         outData[i].recomputeMinMax();
         outField.addData(outData[i]);
      }
      outField.setNSpace(3);
      outField.setCoords(coords);
      CellSet cellSet = new CellSet(inField.getName() + "isolines");
      boolean[] orientations = new boolean[edges.length / 2];
      for (int i = 0; i < orientations.length; i++)
         orientations[i] = true;
      CellArray skeletonSegments = new CellArray(Cell.SEGMENT, edges, orientations, null);
      cellSet.setBoundaryCellArray(skeletonSegments);
      cellSet.setCellArray(skeletonSegments);
      outField.addCellSet(cellSet);
   }

   public IrregularField getOutField()
   {
      return outField;
   }
}
