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

package pl.edu.icm.visnow.lib.basic.mappers.Isosurface;

import pl.edu.icm.visnow.datasets.CellArray;
import pl.edu.icm.visnow.datasets.CellSet;
import pl.edu.icm.visnow.datasets.IrregularField;
import pl.edu.icm.visnow.datasets.cells.Cell;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.lib.utils.isosurface.CellCache;
import pl.edu.icm.visnow.lib.utils.isosurface.EdgesCut;

public class IrregularFieldIsosurface extends IsosurfaceEngine
{
   private final static int[][] cellEdg = new int[][] {{0, 1}, {0, 2}, {0, 3}, {1, 2}, {1, 3}, {2, 3}};
   private final static int[][] cellInd = new int[][] {{}, 
                                     {0, 1, 2},
                                     {0, 3, 4},
                                     {1, 2, 4, 3},
                                     {1, 3, 5},
                                     {0, 2, 5, 3},
                                     {0, 1, 5, 4},
                                     {2, 4, 5},
                                     {2, 4, 5},
                                     {0, 1, 5, 4},
                                     {0, 2, 5, 3},
                                     {1, 3, 5},
                                     {1, 2, 4, 3},
                                     {0, 3, 4},
                                     {0, 1, 2},
                                     {}};
   private int[][] buckets           = new int[CellCache.BUCKETS_NUMBER][];
   private float[] bucketsCeilingFun = new float[CellCache.BUCKETS_NUMBER];
   private float dataMin, dataMax;
   private int componentNumber;
   private IrregularField inField;
   private DataArray data;
   private float[] isoData;
   private IsosurfaceParams params;
   private long lastRecompute = -1;
   private CellCache cellCache;

   public IrregularFieldIsosurface(IrregularField inField)
   {
      if (inField == null)
      {
         throw new IllegalArgumentException("Incoming field cannot be null");
      }
      this.inField = inField;
   }

   private DataArray interpolateDataToSurface(int nData, int nIsoNodes, int[] newNodeInds, float[]newNodeRatios)
   {         
      DataArray cData = inField.getData(nData);
      DataArray outDa = null;
      int vlen = cData.getVeclen();
      switch (cData.getType())
      {
      case DataArray.FIELD_DATA_BYTE:
         byte[] bData = cData.getBData();
         byte[] outbData = new byte[vlen * nIsoNodes];
         for (int i = 0; i < nIsoNodes; i++)
         {
            int e0 = newNodeInds[2 * i];
            int e1 = newNodeInds[2 * i + 1];
            float r = newNodeRatios[i];
            for (int j = 0; j < vlen; j++)
               outbData[vlen * i + j] = (byte)(0xff & (int)(r * (0xff & bData[vlen * e1 + j]) + (1 - r) * (0xff & bData[vlen * e0 + j])));
         }
         outDa = DataArray.create(outbData, vlen, cData.getName());
         break;
      case DataArray.FIELD_DATA_SHORT:
         short[] sData = cData.getSData();
         short[] outsData = new short[vlen * nIsoNodes];
         for (int i = 0; i < nIsoNodes; i++)
         {
            int e0 = newNodeInds[2 * i];
            int e1 = newNodeInds[2 * i + 1];
            float r = newNodeRatios[i];
            for (int j = 0; j < vlen; j++)
               outsData[vlen * i + j] = (short)(r * sData[vlen * e1 + j] + (1 - r) * sData[vlen * e0 + j]);
         }
         outDa = DataArray.create(outsData, vlen, cData.getName());
         break;
      case DataArray.FIELD_DATA_INT:
         int[] iData = cData.getIData();
         int[] outiData = new int[vlen * nIsoNodes];
         for (int i = 0; i < nIsoNodes; i++)
         {
            int e0 = newNodeInds[2 * i];
            int e1 = newNodeInds[2 * i + 1];
            float r = newNodeRatios[i];
            for (int j = 0; j < vlen; j++)
               outiData[vlen * i + j] = (int)(r * iData[vlen * e1 + j] + (1 - r) * iData[vlen * e0 + j]);
         }
         outDa = DataArray.create(outiData, vlen, cData.getName());
         break;
      case DataArray.FIELD_DATA_FLOAT:
         float[] fData = cData.getFData();
         float[] outfData = new float[vlen * nIsoNodes];
         for (int i = 0; i < nIsoNodes; i++)
         {
            int e0 = newNodeInds[2 * i];
            int e1 = newNodeInds[2 * i + 1];
            float r = newNodeRatios[i];
            for (int j = 0; j < vlen; j++)
               outfData[vlen * i + j] =r * fData[vlen * e1 + j] + (1 - r) * fData[vlen * e0 + j];
         }
         outDa = DataArray.create(outfData, vlen, cData.getName());
         break;
      case DataArray.FIELD_DATA_DOUBLE:
         double[] dData = cData.getDData();
         double[] outdData = new double[vlen * nIsoNodes];
         for (int i = 0; i < nIsoNodes; i++)
         {
            int e0 = newNodeInds[2 * i];
            int e1 = newNodeInds[2 * i + 1];
            float r = newNodeRatios[i];
            for (int j = 0; j < vlen; j++)
               outdData[vlen * i + j] = r * dData[vlen * e1 + j] + (1 - r) * dData[vlen * e0 + j];
         }
         outDa = DataArray.create(outdData, vlen, cData.getName());
         break;
      }
      if (outDa != null)
      {
         outDa.setMinv(cData.getMinv());
         outDa.setMaxv(cData.getMaxv());
         outDa.setPhysMin(cData.getPhysMin());
         outDa.setPhysMax(cData.getPhysMax());
      }
      return outDa;
   }

   private void createAndAddTriangle(int v0, int v1, int v2, 
                                     int n0, int n1, float[] outCoords, float[] coords, 
                                     int[] nodes, boolean[] orientations, int[] l)
   {
      if (v0 < 0 || v1 < 0 || v2 < 0)
         return;
      int i;
      // sorting vertex numbers
      if (v0 > v1) {i = v1; v1 = v0; v0 = i;}
      if (v1 > v2) {i = v1; v1 = v2; v2 = i;}
      if (v0 > v1) {i = v1; v1 = v0; v0 = i;}
      //first edge vector
      float[] a = new float[] {outCoords[3 * v1]     - outCoords[3 * v0],
                               outCoords[3 * v1 + 1] - outCoords[3 * v0 + 1],
                               outCoords[3 * v1 + 2] - outCoords[3 * v0 + 2]};
      //second edge vector
      float[] b = new float[] {outCoords[3 * v2]     - outCoords[3 * v0],
                               outCoords[3 * v2 + 1] - outCoords[3 * v0 + 1],
                               outCoords[3 * v2 + 2] - outCoords[3 * v0 + 2]};
      //increasing isosurface component direction vector 
      float[] c = new float[] {coords[3 * n1]     - coords[3 * n0],
                               coords[3 * n1 + 1] - coords[3 * n0 + 1],
                               coords[3 * n1 + 2] - coords[3 * n0 + 2]};
      
      float det = a[0] * b[1] * c[2] + a[1] * b[2] * c[0] + a[2] * b[0] * c[1] - 
                 (a[0] * b[2] * c[1] + a[1] * b[0] * c[2] + a[2] * b[1] * c[0]);
      int k = l[0];
      nodes[3 * k] = v0;
      nodes[3 * k + 1] = v1;
      nodes[3 * k + 2] = v2;
      l[0] += 1;
      orientations[k] = det > 0;
   }

   public IrregularField makeIsosurface(IsosurfaceParams p, float threshold)
   {   
      IrregularField out;
      fireStatusChanged(0);
      if (inField.getData(p.getIsoComponent()).getType() == DataArray.FIELD_DATA_BYTE)
         threshold = (int)threshold + .5f;
      boolean newData = false;
      if (params == null || params.getIsoComponent() != componentNumber)
      {
         params = p;
         componentNumber = params.getIsoComponent();
         newData = true;
      }

      if (componentNumber > inField.getNData() || componentNumber < 0 || 
          !inField.getData(componentNumber).isSimpleNumeric() ||
          threshold < inField.getData(componentNumber).getMinv() ||
          threshold > inField.getData(componentNumber).getMaxv())
         return null;
      data = inField.getData(componentNumber);
      if (data.getVeclen() == 1)
         isoData = data.getFData();
      else
         isoData = data.getNormFData();
      dataMax = data.getMaxv();
      dataMin = data.getMinv();
      if (newData || inField.getData(componentNumber).changedSince(lastRecompute))
         lastRecompute = new CellCache(inField.getData(componentNumber), inField.getCellSet(0), bucketsCeilingFun, buckets, statusListener).computeCache();
      
      EdgesCut edgesCut = new EdgesCut(isoData, threshold, inField.getCoords());
      int nTriangles = 0;

//search through buckets
      float bucketFactor = CellCache.BUCKETS_NUMBER / (dataMax - dataMin);

      float[] coords = inField.getCoords();
      
      for (int bucketInd = Math.min((int) ((threshold - dataMin) * bucketFactor), CellCache.BUCKETS_NUMBER - 1); 
           bucketInd >= 0; --bucketInd)      
      {
         if (bucketsCeilingFun[bucketInd] < threshold)
            break;  //buckets with smaller indexes will certainly contain cells below threshold

         //search for cells in current bucket
         int[] nodes = buckets[bucketInd];
         for (int j = 0; j < nodes.length; j += 4)
         {
            int nOverT = 0;
            for (int i = 0; i < 4; i++)
               if (isoData[nodes[j + i]] >= threshold)
                  nOverT += 1;
            if (nOverT == 0 || nOverT == 4) 
               continue;  // all tetra vertices above or below threshold
            if (nOverT == 2)
               nTriangles += 2;
            else
               nTriangles += 1;
            for (int i = j; i < j + 3; i++)
               for (int ii = i; ii < j + 4; ii++)
                  edgesCut.insertEdge(nodes[i], nodes[ii]);
         }
      }
      
      int nIsoNodes = edgesCut.getnEdges();
      if (nIsoNodes < 3)
         return null;
      
      int[] newNodeInds = new int[2 * nIsoNodes];
      float[] newNodeRatios = new float[nIsoNodes];
      for (Long l: edgesCut.getKeys())
      {
         EdgesCut.EdgeDesc d = edgesCut.getData(l);
         newNodeInds[2 * d.index]     = (int)(l & 0xffffffff);
         newNodeInds[2 * d.index + 1] = (int)((l >> 32) & 0xffffffff);
         newNodeRatios[d.index]       = d.ratio;
      }
      
      out = new IrregularField(nIsoNodes);
      out.setNSpace(3);
      
      float[] outCoords = new float[3 * nIsoNodes];
      for (int i = 0; i < nIsoNodes; i++)
      { 
         int e0 = newNodeInds[2 * i];
         int e1 = newNodeInds[2 * i + 1];
         float r = newNodeRatios[i];
         for (int j = 0; j < 3; j++)
            outCoords[3 * i + j] = r * coords[3 * e1 + j] + (1 - r) * coords[3 * e0 + j];
      }
      out.setCoords(outCoords);
      if (data.getVeclen() == 1)
      {
         float[] thrData = new float[nIsoNodes];
         for (int i = 0; i < thrData.length; i++)
            thrData[i] = threshold;
         DataArray thrDataArr = DataArray.create(thrData, 1, data.getName()+"_thr");
         thrDataArr.setMinv(dataMin);
         thrDataArr.setMaxv(dataMax);
         out.addData(thrDataArr);
      }
      else
         out.addData(interpolateDataToSurface(componentNumber, nIsoNodes, newNodeInds, newNodeRatios));
      for (int nData = 0; nData < inField.getNData(); nData++)
         if (nData != componentNumber && inField.getData(nData).isSimpleNumeric())
            out.addData(interpolateDataToSurface(nData, nIsoNodes, newNodeInds, newNodeRatios));
      
      newNodeRatios = null; // no more used - freed 

      int[] nodes = new int[3 * nTriangles];
      boolean[] orientations = new boolean[nTriangles];
     
      int[] currentTri = new int[] {0}; // holds number of triangles created - incremented in createAndAddTriangle method
      
      for (int bucketInd = Math.min((int) ((threshold - dataMin) * bucketFactor), CellCache.BUCKETS_NUMBER - 1); 
           bucketInd >= 0; --bucketInd)
      {
         if (bucketsCeilingFun[bucketInd] < threshold)
            break;  
         //search for cells in current bucket
         int[] bNodes = buckets[bucketInd];
         int e0, e1;
         for (int j = 0; j < bNodes.length; j += 4)
         {
            int code = 0;
            for (int i = 0; i < 4; i++)
            {   
               float f = isoData[bNodes[j + i]];
               if ( f == threshold) 
                  f += .000001f;
               if (f > threshold)
                  code |= 1 << i;
            }
            if (cellInd[code].length == 0)
               continue;
            int[] edges = cellInd[code];
            int[] pts = new int[cellInd[code].length ];
            for (int i = 0; i < edges.length; i++)
               pts[i] = edgesCut.getIndex(bNodes[j + cellEdg[edges[i]][0]], 
                                          bNodes[j + cellEdg[edges[i]][1]]); 
            
            if (pts.length == 3 && pts[0] >= 0 && pts[1] >= 0 && pts[2] >= 0) // single triangle
            {
               e0 = newNodeInds[2 * pts[0]];
               e1 = newNodeInds[2 * pts[0] + 1];
               if (isoData[e1] < isoData[e0])
               {
                  int k = e0;
                  e0 = e1;
                  e1 = k;
               }
               createAndAddTriangle(pts[0], pts[1], pts[2], e0, e1, 
                                    outCoords, coords, 
                                    nodes, orientations, currentTri);
            }
            else  if (pts.length == 4 && pts[0] >= 0 && pts[1] >= 0 && pts[2] >= 0 && pts[3] >= 0) // quadrangle - cut along shorter diagonal
            {
               float d0 = 0, d1 = 0;
               for (int i = 0; i < 3; i++)
               {
                  float r = outCoords[3 * pts[0] + i] - outCoords[3 * pts[2] + i];
                  d0 += r * r;
                  r = outCoords[3 * pts[1] + i] - outCoords[3 * pts[3] + i];
                  d1 += r * r;
               }
               if (d0 > d1)
               {
                  e0 = newNodeInds[2 * pts[1]];
                  e1 = newNodeInds[2 * pts[1] + 1];
                  if (isoData[e1] < isoData[e0])
                  {
                     int k = e0;
                     e0 = e1;
                     e1 = k;
                  }
                  createAndAddTriangle(pts[0], pts[1], pts[3], e0, e1, 
                                       outCoords, coords, 
                                       nodes, orientations, currentTri);
                  createAndAddTriangle(pts[1], pts[2], pts[3],e0, e1, 
                                       outCoords, coords, 
                                       nodes, orientations, currentTri);
               }
               else
               {
                  e0 = newNodeInds[2 * pts[0]];
                  e1 = newNodeInds[2 * pts[0] + 1];
                  if (isoData[e1] < isoData[e0])
                  {
                     int k = e0;
                     e0 = e1;
                     e1 = k;
                  }
                  createAndAddTriangle(pts[0], pts[1], pts[2], e0, e1, 
                                       outCoords, coords, 
                                       nodes, orientations, currentTri);
                  createAndAddTriangle(pts[0], pts[2], pts[3], e0, e1, 
                                       outCoords, coords, 
                                       nodes, orientations, currentTri);
               }
            }
         }
      }
      for (int i = 3 * currentTri[0]; i < nodes.length; i++)
         nodes[i] = 0;
      
      newNodeInds   = null;    // no more used - freed    

      CellSet cs = new CellSet("iso");
      CellArray triArray = new CellArray(Cell.TRIANGLE, nodes, orientations, null);
      cs.addCells(triArray);
      cs.generateDisplayData(outCoords);
      out.addCellSet(cs);

      return out;
   }

}
