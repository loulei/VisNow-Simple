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

 package pl.edu.icm.visnow.lib.basic.mappers.Isosurface;

/*
 *Isosurface.java
 *
 *Created on August 14, 2004, 2:06 PM
 */
import java.util.ArrayList;
import pl.edu.icm.visnow.datasets.CellArray;
import pl.edu.icm.visnow.datasets.CellSet;
import pl.edu.icm.visnow.datasets.IrregularField;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.datasets.cells.Cell;
import pl.edu.icm.visnow.datasets.dataarrays.BitArray;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.lib.utils.isosurface.AddEdges;
import pl.edu.icm.visnow.lib.utils.isosurface.IsoEdges;
import pl.edu.icm.visnow.lib.utils.isosurface.IsoTriangles;

/**
 *
 *@author  Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class RegularFieldIsosurface extends IsosurfaceEngine
{

   private static final int[][][][] ie = IsoTriangles.ie;
   private static int[][][][] ig = IsoEdges.ig;
   private static int[][][][][] iaddg =
   {
      AddEdges.ig0, AddEdges.ig1, AddEdges.ig2
   };
   private static final int CHUNK_SIZE = 100000;
   private final RegularField in;

   /** Creates a new instance of Isosurface */
   public RegularFieldIsosurface(RegularField in)
   {
      this.in = in;
   }

   /**
    *Creates isosurface of a data component of a RegularField in at threshold value thr
    *and interpolates selected data componentson the surface/line mesh
    *Isosurface is generated from the input field cropped  and downsized according to the parameters
    *@param params   Parameters of isosurface
    *@return a TriangulatedField2D containing surface reprezentation and optimized line representation of the
    *isosurface with outData components interpolated
    */
   public IrregularField makeIsosurface(IsosurfaceParams params, float threshold)
   {
      int comp = params.getIsoComponent();
      int[] low = params.getLow();
      int[] up = params.getUp();
      int[] d = params.getDownsize();
      int[] outComp = null;
      if (in == null || 
          in.getDims().length != 3 ||
          comp < 0 ||
          comp >= in.getNData() ||
          threshold < in.getData(comp).getMinv() ||
          threshold > in.getData(comp).getMaxv() ||
          low[0] < 0 || low[1] < 0 || low[2] < 0 ||
          up[0] > in.getDims()[0] ||
          up[1] > in.getDims()[1] ||
          up[2] > in.getDims()[2] ||
          (up[0] - low[0]) / d[0] < 2 ||
          (up[1] - low[1]) / d[1] < 2 ||
          (up[2] - low[2]) / d[2] < 2) 
         return null;

      int[] inDims = in.getDims();
      DataArray data = in.getData(comp);

      
      boolean isValidity = in.isMask();
      boolean[] valid = in.getMask();

      ArrayList<float[]> ptVect = new ArrayList<float[]>();
      ArrayList<float[]> nvVect = new ArrayList<float[]>();
      ArrayList<int[]> trVect = new ArrayList<int[]>();
      ArrayList<int[]> edVect = new ArrayList<int[]>();
      ArrayList<int[]> orVect = new ArrayList<int[]>();


      float[] pt = null;
      float[] nv = null;
      int[] tr = null;
      int[] ed = null;
      int[] or = null;


      int ll, l, i, j, k, m, ip, it, jt, ix, iy, iz,
              sign, p = 0, nTriangles = 0, nEdges = 0; //l - layer
      float u, v, x, y, z;
      int k0, k1, k2;
      float[] a = new float[3];
      float[] b = new float[3];
      float[] c = new float[3];
      float[][] inDataOrig = new float[2][];
      int[] dims = new int[3];
      int[] dind = {1, 1, 1};
      for (i = 0; i < 3; i++)
      {
//         dims[i] = (up[i] - low[i] + d[i]) / d[i];
         dims[i] = (up[i] - low[i] + d[i] - 1) / d[i];
         dind[i] *= d[i];
      }
      ix = 1;
      iy = dims[0];
      iz = dims[0] * dims[1];

      int[][][] lPts = new int[dims[1] - 1][dims[0] - 1][12];
      outComp = new int[in.getNData()];
      int nMappedComponents = 1;
      outComp[0] = comp;
      for (i = 0; i < in.getNData(); i++)
         if (i != comp && in.getData(i).isSimpleNumeric())
         {
            outComp[nMappedComponents] = i;
            nMappedComponents += 1;
         }
      float thr = threshold;
      if (data.getType() == DataArray.FIELD_DATA_BYTE ||
          data.getType() == DataArray.FIELD_DATA_SHORT ||
          data.getType() == DataArray.FIELD_DATA_INT && thr - (int) thr < .001)
         thr = (int) thr + .5f;
      float[][] inData = new float[2][dims[1] * dims[0]];
      
      
      //zrównoleglić?
      for (ll = 0; ll < dims[2] - 1; ll++)
      {
         fireStatusChanged((.4f * ll) / (dims[2] - 1));
         l = low[2] + ll * d[2];
         if (ll == 0)
         {
            inDataOrig[0] = in.get2DNormSliceData(comp, 2, l);
            for (j = k = 0; j < dims[1]; j++)
               for (i = 0, m = (j * d[1] + low[1]) * inDims[0] + low[0];
                       i < dims[0];
                       i++, k++, m += d[0])
                  inData[0][k] = inDataOrig[0][m];
         } else
            for (i = 0; i < dims[1] * dims[0]; i++)
               inData[0][i] = inData[1][i];
         inDataOrig[1] = in.get2DNormSliceData(comp, 2, l + d[2]);
         for (j = k = 0; j < dims[1]; j++)
            for (i = 0, m = (j * d[1] + low[1]) * inDims[0] + low[0];
                    i < dims[0];
                    i++, k++, m += d[0])
               inData[1][k] = inDataOrig[1][m];
         // getting slices of original data, cropping and resizing them
         z = low[2] + ll * dind[2];
         if (ll == 0)
         {
            for (j = 0; j < dims[1]; j++)         //finding points in x lines
            {
               y = low[1] + j * dind[1];
               for (i = 0; i < dims[0] - 1; i++)
               {
                  ip = j * dims[0] + i;
                  x = low[0] + i * dind[0];
                  u = inData[0][ip] - thr;
                  if (u == 0.f)
                     u -= .01f;
                  v = inData[0][ip + 1] - thr;
                  if (v == 0.f)
                     v -= .01f;
                  if (u * v < 0)
                  {
                     k = p % CHUNK_SIZE;
                     if (k == 0)
                     {
                        pt = new float[3 * CHUNK_SIZE];
                        ptVect.add(pt);
                        nv = new float[3 * CHUNK_SIZE];
                        nvVect.add(nv);
                     }
                     pt[3 * k] = x + u / (u - v) * dind[0];
                     pt[3 * k + 1] = y;
                     pt[3 * k + 2] = z;
                     nv[3 * k] = (v - u) / dind[0];
                     if (j > 0 && j < dims[1] - 1)
                        nv[3 * k + 1] = .5f * (inData[0][ip + iy] - inData[0][ip - iy]) / dind[1];
                     else
                     {
                        if (j > 0)
                           nv[3 * k + 1] = (inData[0][ip] - inData[0][ip - iy]) / dind[1];
                        else
                           nv[3 * k + 1] = (inData[0][ip + iy] - inData[0][ip]) / dind[1];
                     }
                     nv[3 * k + 2] = (inData[1][ip] - inData[0][ip]) / dind[2];
                     if (j < dims[1] - 1)
                        lPts[j][i][0] = p;
                     if (j > 0)
                        lPts[j - 1][i][1] = p;
                     p += 1;
                  }
               }
            }
            for (j = 0; j < dims[1] - 1; j++)         //finding points in y lines
            {
               y = low[1] + j * dind[1];
               for (i = 0; i < dims[0]; i++)
               {
                  ip = j * dims[0] + i;
                  x = low[0] + i * dind[0];
                  u = inData[0][ip] - thr;
                  if (u == 0.f)
                     u -= 1.e-6f;
                  v = inData[0][ip + iy] - thr;
                  if (v == 0.f)
                     v -= 1.e-6f;
                  if (u * v < 0)
                  {
                     k = p % CHUNK_SIZE;
                     if (k == 0)
                     {
                        pt = new float[3 * CHUNK_SIZE];
                        ptVect.add(pt);
                        nv = new float[3 * CHUNK_SIZE];
                        nvVect.add(nv);
                     }
                     pt[3 * k] = x;
                     pt[3 * k + 1] = y + u / (u - v) * dind[1];
                     pt[3 * k + 2] = z;
                     if (i > 0 && i < dims[0] - 1)
                        nv[3 * k] = .5f * (inData[0][ip + ix] - inData[0][ip - ix]) / dind[0];
                     else
                     {
                        if (i > 0)
                           nv[3 * k] = (inData[0][ip] - inData[0][ip - ix]) / dind[0];
                        else
                           nv[3 * k] = (inData[0][ip + ix] - inData[0][ip]) / dind[0];
                     }
                     nv[3 * k + 1] = (v - u) / dind[1];
                     nv[3 * k + 2] = (inData[1][ip] - inData[0][ip]) / dind[2];
                     if (i < dims[0] - 1)
                        lPts[j][i][4] = p;
                     if (i > 0)
                        lPts[j][i - 1][5] = p;
                     p += 1;
                  }
               }
            }
         } else
         {
            for (j = 0; j < dims[1] - 1; j++)
               for (i = 0; i < dims[0] - 1; i++)
               {
                  lPts[j][i][0] = lPts[j][i][2];
                  lPts[j][i][1] = lPts[j][i][3];
                  lPts[j][i][4] = lPts[j][i][6];
                  lPts[j][i][5] = lPts[j][i][7];
               }
         }
         for (j = 0; j < dims[1]; j++)         //finding points in x lines
         {
            y = low[1] + j * dind[1];
            for (i = 0; i < dims[0] - 1; i++)
            {
               ip = j * dims[0] + i;
               x = low[0] + i * dind[0];
               u = inData[1][ip] - thr;
               if (u == 0.f)
                  u -= 1.e-6f;
               v = inData[1][ip + 1] - thr;
               if (v == 0.f)
                  v -= 1.e-6f;
               if (u * v < 0)
               {
                  k = p % CHUNK_SIZE;
                  if (k == 0)
                  {
                     pt = new float[3 * CHUNK_SIZE];
                     ptVect.add(pt);
                     nv = new float[3 * CHUNK_SIZE];
                     nvVect.add(nv);
                  }
                  pt[3 * k] = x + u / (u - v) * dind[0];
                  pt[3 * k + 1] = y;
                  pt[3 * k + 2] = z + dind[2];
                  nv[3 * k] = (v - u) / dind[0];
                  if (j > 0 && j < dims[1] - 1)
                     nv[3 * k + 1] = .5f * (inData[1][ip + iy] - inData[1][ip - iy]) / dind[1];
                  else
                  {
                     if (j > 0)
                        nv[3 * k + 1] = (inData[1][ip] - inData[1][ip - iy]) / dind[1];
                     else
                        nv[3 * k + 1] = (inData[1][ip + iy] - inData[1][ip]) / dind[1];
                  }
                  nv[3 * k + 2] = (inData[1][ip] - inData[0][ip]) / dind[2];
                  if (j < dims[1] - 1)
                     lPts[j][i][2] = p;
                  if (j > 0)
                     lPts[j - 1][i][3] = p;
                  p += 1;
               }
            }
         }
         for (j = 0; j < dims[1] - 1; j++)         //finding points in y lines
         {
            y = low[1] + j * dind[1];
            for (i = 0; i < dims[0]; i++)
            {
               ip = j * dims[0] + i;
               x = low[0] + i * dind[0];
               u = inData[1][ip] - thr;
               if (u == 0.f)
                  u -= 1.e-6f;
               v = inData[1][ip + iy] - thr;
               if (v == 0.f)
                  v -= 1.e-6f;
               if (u * v < 0)
               {
                  k = p % CHUNK_SIZE;
                  if (k == 0)
                  {
                     pt = new float[3 * CHUNK_SIZE];
                     ptVect.add(pt);
                     nv = new float[3 * CHUNK_SIZE];
                     nvVect.add(nv);
                  }
                  pt[3 * k] = x;
                  pt[3 * k + 1] = y + u / (u - v) * dind[1];
                  pt[3 * k + 2] = z + dind[2];
                  if (i > 0 && i < dims[0] - 1)
                     nv[3 * k] = .5f * (inData[1][ip + ix] - inData[1][ip - ix]) / dind[0];
                  else
                  {
                     if (i > 0)
                        nv[3 * k] = (inData[1][ip] - inData[1][ip - ix]) / dind[0];
                     else
                        nv[3 * k] = (inData[1][ip + ix] - inData[1][ip]) / dind[0];
                  }
                  nv[3 * k + 1] = (v - u) / dind[1];
                  nv[3 * k + 2] = (inData[1][ip] - inData[0][ip]) / dind[2];
                  if (i < dims[0] - 1)
                     lPts[j][i][6] = p;
                  if (i > 0)
                     lPts[j][i - 1][7] = p;
                  p += 1;
               }
            }
         }
         for (j = 0; j < dims[1]; j++)         //finding points in z lines
         {
            y = low[1] + j * dind[1];
            for (i = 0; i < dims[0]; i++)
            {
               ip = j * dims[0] + i;
               x = low[0] + i * dind[0];
               u = inData[0][ip] - thr;
               if (u == 0.f)
                  u -= 1.e-6f;
               v = inData[1][ip] - thr;
               if (v == 0.f)
                  v -= 1.e-6f;
               if (u * v < 0)
               {
                  k = p % CHUNK_SIZE;
                  if (k == 0)
                  {
                     pt = new float[3 * CHUNK_SIZE];
                     ptVect.add(pt);
                     nv = new float[3 * CHUNK_SIZE];
                     nvVect.add(nv);
                  }
                  pt[3 * k] = x;
                  pt[3 * k + 1] = y;
                  pt[3 * k + 2] = z + u / (u - v) * dind[2];
                  if (i > 0 && i < dims[0] - 1)
                     nv[3 * k] = .5f * (inData[0][ip + ix] - inData[0][ip - ix]) / dind[0];
                  else
                  {
                     if (i > 0)
                        nv[3 * k] = (inData[0][ip] - inData[0][ip - ix]) / dind[0];
                     else
                        nv[3 * k] = (inData[0][ip + ix] - inData[0][ip]) / dind[0];
                  }
                  if (j > 0 && j < dims[1] - 1)
                     nv[3 * k + 1] = .5f * (inData[0][ip + iy] - inData[0][ip - iy]) / dind[1];
                  else
                  {
                     if (j > 0)
                        nv[3 * k + 1] = (inData[0][ip] - inData[0][ip - iy]) / dind[1];
                     else
                        nv[3 * k + 1] = (inData[0][ip + iy] - inData[0][ip]) / dind[1];
                  }
                  nv[3 * k + 2] = (v - u) / dind[2];
                  if (j < dims[1] - 1 && i < dims[0] - 1)
                     lPts[j][i][8] = p;
                  if (j > 0 && i < dims[0] - 1)
                     lPts[j - 1][i][10] = p;
                  if (j < dims[1] - 1 && i > 0)
                     lPts[j][i - 1][9] = p;
                  if (j > 0 && i > 0)
                     lPts[j - 1][i - 1][11] = p;
                  p += 1;
               }
            }
         }
         for (j = 0; j < dims[1] - 1; j++)         //finding triangles
            for (i = 0; i < dims[0] - 1; i++)
            {
               int kk = ((ll * d[2] + low[2]) * inDims[1] + j * d[1] + low[1]) * inDims[0] + i * d[0] + low[0];
               int kx = d[0];
               int ky = d[1] * inDims[0];
               int kz = d[2] * inDims[1] * inDims[0];
               k = j * dims[0] + i;
               if (isValidity &&
                !(valid[kk]      && valid[kk + kx]      && valid[kk + ky]      && valid[kk + kx + ky] && 
                  valid[kk + kz] && valid[kk + kx + kz] && valid[kk + ky + kz] && valid[kk + kx + ky + kz]))
                  continue;
               if (Float.isNaN(inData[0][k]) || 
                   Float.isNaN(inData[0][k + ix]) ||
                   Float.isNaN(inData[0][k + iy]) ||
                   Float.isNaN(inData[0][k + iy + ix]) ||
                   Float.isNaN(inData[1][k]) ||
                   Float.isNaN(inData[1][k + ix]) ||
                   Float.isNaN(inData[1][k + iy]) ||
                   Float.isNaN(inData[1][k + iy + ix]))
                  continue;
               sign = 0;
               if (inData[0][k] > thr)
                  sign |= 1 << 0;
               if (inData[0][k + ix] > thr)
                  sign |= 1 << 1;
               if (inData[0][k + iy] > thr)
                  sign |= 1 << 2;
               if (inData[0][k + iy + ix] > thr)
                  sign |= 1 << 3;
               if (inData[1][k] > thr)
                  sign |= 1 << 4;
               if (inData[1][k + ix] > thr)
                  sign |= 1 << 5;
               if (inData[1][k + iy] > thr)
                  sign |= 1 << 6;
               if (inData[1][k + iy + ix] > thr)
                  sign |= 1 << 7;
               int[][] trIn = ie[(i + j + l) % 2][sign];
               for (it = 0; it < trIn.length; it++)
               {
                  if (trIn[it][0] < 0)
                     break;
                  m = nTriangles % CHUNK_SIZE;
                  if (m == 0)
                  {
                     tr = new int[3 * CHUNK_SIZE];
                     trVect.add(tr);
                     or = new int[CHUNK_SIZE];
                     orVect.add(or);
                  }
                  for (jt = 0; jt < 3; jt++)
                     tr[3 * m + jt] = lPts[j][i][trIn[it][jt]];
                  or[m] = (((i + j + l) % 2) << 16) | (sign << 8) | it;
                  nTriangles += 1;
               }
               int[][] edIn = ig[(i + j + l) % 2][sign];
               for (it = 0; it < edIn.length; it++)
               {
                  if (edIn[it][0] < 0)
                     break;
                  m = nEdges % CHUNK_SIZE;
                  if (m == 0)
                  {
                     ed = new int[2 * CHUNK_SIZE];
                     edVect.add(ed);
                  }
                  for (jt = 0; jt < 2; jt++)
                     ed[2 * m + jt] = lPts[j][i][edIn[it][jt]];
                  nEdges += 1;
               }
               if (i == dims[0] - 2)
               {
                  edIn = iaddg[0][(i + j + l) % 2][sign];
                  for (it = 0; it < edIn.length; it++)
                  {
                     if (edIn[it][0] < 0)
                        break;
                     m = nEdges % CHUNK_SIZE;
                     if (m == 0)
                     {
                        ed = new int[2 * CHUNK_SIZE];
                        edVect.add(ed);
                     }
                     for (jt = 0; jt < 2; jt++)
                        ed[2 * m + jt] = lPts[j][i][edIn[it][jt]];
                     nEdges += 1;
                  }
               }
               if (j == dims[1] - 2)
               {
                  edIn = iaddg[1][(i + j + l) % 2][sign];
                  for (it = 0; it < edIn.length; it++)
                  {
                     if (edIn[it][0] < 0)
                        break;
                     m = nEdges % CHUNK_SIZE;
                     if (m == 0)
                     {
                        ed = new int[2 * CHUNK_SIZE];
                        edVect.add(ed);
                     }
                     for (jt = 0; jt < 2; jt++)
                        ed[2 * m + jt] = lPts[j][i][edIn[it][jt]];
                     nEdges += 1;
                  }
               }
               if (ll == dims[2] - 2)
               {
                  edIn = iaddg[2][(i + j + l) % 2][sign];
                  for (it = 0; it < edIn.length; it++)
                  {
                     if (edIn[it][0] < 0)
                        break;
                     m = nEdges % CHUNK_SIZE;
                     if (m == 0)
                     {
                        ed = new int[2 * CHUNK_SIZE];
                        edVect.add(ed);
                     }
                     for (jt = 0; jt < 2; jt++)
                        ed[2 * m + jt] = lPts[j][i][edIn[it][jt]];
                     nEdges += 1;
                  }
               }
            }
      }
      if (p <= 0)
         return null;
      IrregularField out = new IrregularField(p);
      out.setNSpace(3);
      CellSet isosurfaceCellSet = new CellSet(in.getName() + String.format("_%5.2f", threshold));
      out.addCellSet(isosurfaceCellSet);
      if (p == 0 || nTriangles == 0)
         return out;

      int outNNodes = p;
      float[] coords = new float[3 * outNNodes];
      float[] normals = new float[3 * outNNodes];
      float[] uncert = null;
      boolean isUncert = params.isUncertainty();
      if (isUncert)
         uncert = new float[3 * outNNodes];
      float uu = 1;
      pointloop:
      for (i = l = 0; i < ptVect.size(); i++)
      {
         fireStatusChanged(.4f + (.15f * (i + 1)) / (ptVect.size()));
         pt = ptVect.get(i);
         nv = nvVect.get(i);
         for (j = 0; j < CHUNK_SIZE; j++)
         {
            uu = nv[3 * j] * nv[3 * j] + nv[3 * j + 1] * nv[3 * j + 1] + nv[3 * j + 2] * nv[3 * j + 2];
            u = (float) Math.sqrt(uu);
            if (uu == 0)
               uu = u = 1;
            for (ip = 0; ip < 3; ip++, l++)
            {
               coords[l] = pt[3 * j + ip];
               normals[l] = nv[3 * j + ip] / u;
               if (isUncert)
                  uncert[l] = nv[3 * j + ip] / uu;
            }
            if (l >= 3 * p)
               break pointloop;
         }
      }
      int[] cells = new int[3 * nTriangles];
      boolean[] orientations = new boolean[nTriangles];
      int[] dataIndices = new int[nTriangles];
      for (int n = 0; n < nTriangles; n++)
      {
         dataIndices[i] = i;
         orientations[i] = true;
      }
      triangleloop:
      for (i = l = 0; i < trVect.size(); i++)
      {
         fireStatusChanged(.55f + (.15f * (i + 1)) / (trVect.size()));
         tr = trVect.get(i);
         or = orVect.get(i);
         for (j = 0; j < tr.length; j += 3, l += 3)
         {
            if (l >= 3 * nTriangles)
               break triangleloop;
            k0 = cells[l] = tr[j];
            k1 = cells[l + 1] = tr[j + 1];
            k2 = cells[l + 2] = tr[j + 2];
            for (int ij = 0; ij < 3; ij++)
            {
               a[ij] = coords[3 * k1 + ij] - coords[3 * k0 + ij];
               b[ij] = coords[3 * k2 + ij] - coords[3 * k0 + ij];
            }
            if ((a[1] * b[2] - a[2] * b[1]) * normals[3 * k0] + (a[2] * b[0] - a[0] * b[2]) * normals[3 * k0 + 1] + (a[0] * b[1] - a[1] * b[0]) * normals[3 * k0 + 2] < 0)
            {
               cells[l + 1] = k2;
               cells[l + 2] = k1;
               int jj = or[j / 3];
               //              System.out.printf("%2d  %3d  %3d%n", (jj >> 16) & 0xff, (jj >> 8) & 0xff, jj & 0xff);
            }
         }
      }

      CellArray triangleArray = new CellArray(Cell.TRIANGLE, cells, orientations, dataIndices);
      isosurfaceCellSet.setCellArray(triangleArray);
      isosurfaceCellSet.setBoundaryCellArray(triangleArray);

      int[] edges = new int[2 * nEdges];
      boolean[] edgeOrientations = new boolean[nEdges];
      int[] edgeDataIndices = new int[nEdges];
      edgeloop:
      for (i = l = 0; i < edVect.size(); i++)
      {
         ed = edVect.get(i);
         for (j = 0; j < ed.length; j++, l++)
         {
            if (l >= 2 * nEdges)
            {
               break edgeloop;
            }
            edges[l] = ed[j];
         }
      }
      for (int n = 0; n < nEdges; n++)
         edgeOrientations[i] = true;
      CellArray edgeArray = new CellArray(Cell.SEGMENT, edges, edgeOrientations, edgeDataIndices);
      isosurfaceCellSet.setBoundaryCellArray(edgeArray);

      int[] points = new int[outNNodes];
      for (int n = 0; n < points.length; n++)
         points[n] = n;

      CellArray pointArray = new CellArray(Cell.POINT, points, null, null);
      isosurfaceCellSet.setBoundaryCellArray(pointArray);

      DataArray outDa = null;
      for (int n = 0; n < outComp.length; n++)
      {
         if (n == 0 && data.getVeclen() == 1)
         {
            float[] fda = new float[outNNodes];
            for (i = 0; i < p; i++)
            fda[i] = threshold;
            outDa = DataArray.create(fda, 1, data.getName() + "(thr)");
            outDa.setMinv(data.getMinv());
            outDa.setMaxv(data.getMaxv());
            outDa.setPhysMin(data.getPhysMin());
            outDa.setPhysMax(data.getPhysMax());
            out.addData(outDa);
         }
         fireStatusChanged(.8f + (.1f * (n + 1)) / outComp.length);
         k = outComp[n];
         DataArray da = in.getData(k);
         int vlen = da.getVeclen();
         switch (da.getType())
         {
            case DataArray.FIELD_DATA_LOGIC:
               byte[] inbda = da.getBData();
               byte[] bda = new byte[outNNodes * vlen];
               for (i = l = 0; i < out.getNNodes(); i++)
               {
                  byte[] bi = in.getInterpolatedData(inbda, coords[3 * i], coords[3 * i + 1], coords[3 * i + 2]);
                  for (j = 0; j < bi.length; j++, l++)
                     bda[l] = bi[j];
               }
               outDa = DataArray.create(new BitArray(bda), da.getVeclen(), da.getName(), da.getUnit(), da.getUserData());
               break;
            case DataArray.FIELD_DATA_BYTE:
               bda = new byte[outNNodes * vlen];
               for (i = l = 0; i < out.getNNodes(); i++)
               {
                  byte[] bi = in.getInterpolatedData(da.getBData(), coords[3 * i], coords[3 * i + 1], coords[3 * i + 2]);
                  for (j = 0; j < bi.length; j++, l++)
                     bda[l] = bi[j];
               }
               outDa = DataArray.create(bda, da.getVeclen(), da.getName(), da.getUnit(), da.getUserData());
               break;
            case DataArray.FIELD_DATA_SHORT:
               short[] sda = new short[outNNodes * vlen];
               for (i = l = 0; i < out.getNNodes(); i++)
               {
                  short[] si = in.getInterpolatedData(da.getSData(), coords[3 * i], coords[3 * i + 1], coords[3 * i + 2]);
                  for (j = 0; j < si.length; j++, l++)
                     sda[l] = si[j];
               }
               outDa = DataArray.create(sda, da.getVeclen(), da.getName(), da.getUnit(), da.getUserData());
               break;
            case DataArray.FIELD_DATA_INT:
               int[] ida = new int[outNNodes * vlen];
               for (i = l = 0; i < out.getNNodes(); i++)
               {
                  int[] ii = in.getInterpolatedData(da.getIData(), coords[3 * i], coords[3 * i + 1], coords[3 * i + 2]);
                  for (j = 0; j < ii.length; j++, l++)
                     ida[l] = ii[j];
               }
               outDa = DataArray.create(ida, da.getVeclen(), da.getName(), da.getUnit(), da.getUserData());
               break;
            case DataArray.FIELD_DATA_FLOAT:
               float[] fda = new float[outNNodes * vlen];
               for (i = l = 0; i < out.getNNodes(); i++)
               {
                  float[] fi = in.getInterpolatedData(da.getFData(), coords[3 * i], coords[3 * i + 1], coords[3 * i + 2]);
                  for (j = 0; j < fi.length; j++, l++)
                     fda[l] = fi[j];
               }
               outDa = DataArray.create(fda, da.getVeclen(), da.getName(), da.getUnit(), da.getUserData());
               break;
            case DataArray.FIELD_DATA_DOUBLE:
               double[] dda = new double[outNNodes * vlen];
               for (i = l = 0; i < out.getNNodes(); i++)
               {
                  double[] di = in.getInterpolatedData(da.getDData(), coords[3 * i], coords[3 * i + 1], coords[3 * i + 2]);
                  for (j = 0; j < di.length; j++, l++)
                     dda[l] = di[j];
               }
               outDa = DataArray.create(dda, da.getVeclen(), da.getName(), da.getUnit(), da.getUserData());
               break;
         }
         if (outDa != null)
         {
            outDa.setMinv(da.getMinv());
            outDa.setMaxv(da.getMaxv());
            outDa.setPhysMin(da.getPhysMin());
            outDa.setPhysMax(da.getPhysMax());
            out.addData(outDa);
         }
      }
      if (isUncert)
         out.addData(DataArray.create(uncert, 3, data.getName() + "(uncertainty)"));
      for (j = 0; j < outNNodes; j++)
      {
         float[] cc = in.getGridCoords(coords[3 * j], coords[3 * j + 1], coords[3 * j + 2]);
         for (l = 0; l < 3; l++)
            coords[3 * j + l] = cc[l];
      }
      out.setCoords(coords);
      out.setNormals(normals);
      return out;
   }
}
