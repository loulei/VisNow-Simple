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

package pl.edu.icm.visnow.lib.basic.filters.InterpolationToRegularField;


import java.util.Arrays;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.IndexedLineStripArray;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pl.edu.icm.visnow.datasets.CellArray;
import pl.edu.icm.visnow.datasets.CellSet;
import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.datasets.IrregularField;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.datasets.cells.Cell;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.engine.core.InputEgg;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.geometries.objects.generics.OpenAppearance;
import pl.edu.icm.visnow.geometries.objects.generics.OpenBranchGroup;
import pl.edu.icm.visnow.geometries.objects.generics.OpenColoringAttributes;
import pl.edu.icm.visnow.geometries.objects.generics.OpenLineAttributes;
import pl.edu.icm.visnow.geometries.objects.generics.OpenShape3D;
import pl.edu.icm.visnow.lib.templates.visualization.modules.RegularOutFieldVisualizationModule;
import pl.edu.icm.visnow.lib.types.VNField;
import pl.edu.icm.visnow.lib.types.VNRegularField;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;
import static pl.edu.icm.visnow.lib.utils.numeric.SliceLookupTable.*;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class InterpolationToRegularField extends RegularOutFieldVisualizationModule
{
   protected static final int TRANSFORM = 0;
   protected static final int DIMS      = 1;
   protected static final int OUTPUT    = 2;
   protected int change = TRANSFORM;
           
   public static InputEgg[] inputEggs = null;
   public static OutputEgg[] outputEggs = null;
   
   protected GUI computeUI = null;
   protected boolean fromUI = false;
   protected boolean ignoreUI = false;
   
   protected Field inField = null;
   protected int trueDim;
   protected int nSpace;
   protected Params params;      
   protected float[][] extents = new float[][] {{0,0,0},{1,1,1}};
   protected float[][] boxExtents;
   protected float[][] baseAffine = new float[4][3];
   protected int lastAxis = -1;
   protected int nThreads = 1;
   
   protected int[] dims = new int[]{10,10,10}; 
   protected float[][] outBaseAffine = new float[4][3];
   protected float[][] outAffine = new float[4][3];
   protected float[][] outInvAffine;
   protected boolean[] valid = null;
   
   protected float[] inCoords = null;
   protected float[] recomputedCoords = null;
   
   protected boolean cellDataInterpolable = false;
   protected int nNumDArrays;
   protected int nNumNodeDataArrays;
   protected int[] indices;
   protected int[] vlens;
   protected float[][] inData;
   protected float[][] outData;

   protected float[] boxVerts = {-1, -1, -1,
                                  1, -1, -1,
                                 -1,  1, -1,
                                  1,  1, -1,
                                 -1, -1,  1,
                                  1, -1,  1,
                                 -1,  1,  1,
                                  1,  1,  1};
   
   protected OpenLineAttributes boxLineAttr = new OpenLineAttributes();
   protected OpenColoringAttributes boxColorAttr = new OpenColoringAttributes();
   protected OpenAppearance boxApp = new OpenAppearance();
   protected OpenShape3D boxShape = new OpenShape3D();
   protected IndexedLineStripArray box = null;
   protected OpenBranchGroup boxGroup = new OpenBranchGroup();
   
   public InterpolationToRegularField()
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
            for (int i = 0; i < trueDim; i++)
               if (params.getResolution()[i] != dims[i] && change != DIMS)
                  change = DIMS;
            if (params.isOutput())
               change = OUTPUT;
            startAction();
         }
      });
      params.getTransform().addChangeListener(new ChangeListener() 
      {
         public void stateChanged(ChangeEvent e)
         {
            fromUI = true;
            if (inField == null)
               return;
            transformBox();
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
      boxLineAttr.setLineAntialiasingEnable(true);
      boxLineAttr.setLineWidth(1.5f);
      boxApp.setLineAttributes(boxLineAttr);
      boxColorAttr.setColor(0, 1, 0);
      boxApp.setColoringAttributes(boxColorAttr);
      box = new IndexedLineStripArray(8, GeometryArray.COORDINATES, 24, new int[]
              { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 });
      box.setCoordinateIndices(0, new int[]
              { 0, 1, 2, 3, 4, 5, 6, 7, 0, 2, 1, 3, 4, 6, 5, 7, 0, 4, 1, 5, 2, 6, 3, 7 });
      box.setCapability(GeometryArray.ALLOW_COORDINATE_READ);
      box.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);
      box.setCoordinates(0, boxVerts);
      boxShape.setAppearance(boxApp);
      boxShape.addGeometry(box);
      boxGroup.addChild(boxShape);
      outObj.addNode(boxGroup);
   }

   private void updateBoxCoords()
   {
      if (trueDim == 3)
         for (int i = 0; i < 3; i++)
         {
            boxVerts[i]      = outBaseAffine[3][i];
            boxVerts[i + 3]  = outBaseAffine[3][i]                                             + outBaseAffine[0][i];
            boxVerts[i + 6]  = outBaseAffine[3][i]                       + outBaseAffine[1][i];
            boxVerts[i + 9]  = outBaseAffine[3][i]                       + outBaseAffine[1][i] + outBaseAffine[0][i];
            boxVerts[i + 12] = outBaseAffine[3][i] + outBaseAffine[2][i];
            boxVerts[i + 15] = outBaseAffine[3][i] + outBaseAffine[2][i]                       + outBaseAffine[0][i];
            boxVerts[i + 18] = outBaseAffine[3][i] + outBaseAffine[2][i] + outBaseAffine[1][i];
            boxVerts[i + 21] = outBaseAffine[3][i] + outBaseAffine[2][i] + outBaseAffine[1][i] + outBaseAffine[0][i];
         }
      else
         for (int i = 0; i < 3; i++)
         {
            boxVerts[i]      = boxVerts[i + 12] = outBaseAffine[3][i];
            boxVerts[i + 3]  = boxVerts[i + 15] = outBaseAffine[3][i]                       + outBaseAffine[0][i];
            boxVerts[i + 6]  = boxVerts[i + 18] = outBaseAffine[3][i] + outBaseAffine[1][i];
            boxVerts[i + 9]  = boxVerts[i + 21] = outBaseAffine[3][i] + outBaseAffine[1][i] + outBaseAffine[0][i];
         }
      boxExtents = new float[][]{{Float.MAX_VALUE,   Float.MAX_VALUE,  Float.MAX_VALUE},
                                 {-Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE}};
      for (int i = 0; i < 8; i++)
         for (int j = 0; j < 3; j++)
         {
            float fs = boxVerts[3 * i + j];
            if (boxExtents[0][j] > fs) boxExtents[0][j] = fs;
            if (boxExtents[1][j] < fs) boxExtents[1][j] = fs;
         }
      box.setCoordinates(0, boxVerts);
      outObj.setExtents(boxExtents);
   }

   private void initBaseAffine()
   {
      extents = inField.getExtents();
      for (int i = 0; i < 3; i++)
      {
         for (int j = 0; j < 3; j++)
            outBaseAffine[j][i] = baseAffine[j][i] = 0;
         outBaseAffine[i][i] = baseAffine[i][i] = extents[1][i] - extents[0][i];
         outBaseAffine[3][i] = baseAffine[3][i] = extents[0][i];
      }
      updateBoxCoords();
   }

   private void transformBox()
   {
      float[][] trMatrix = params.getTransform().getMatrix();
      for (int i = 0; i < 4; i++)
      {
         for (int j = 0; j < 3; j++)
         {
            outBaseAffine[i][j] = 0;
            for (int k = 0; k < 3; k++)
                outBaseAffine[i][j] += trMatrix[j][k] * baseAffine[i][k];
            if(i==3)
                outBaseAffine[i][j] += trMatrix[j][3];
         }
      }
      updateBoxCoords();
   }   
   
   
/**
 * The original field coordinates are recomputed to the index coordinates of the outfield 
 */   
   class CoordinateRecomputation implements Runnable
   {
      int nThreads      = 1;
      int iThread       = 0;
      int nInNodes;
      
      public CoordinateRecomputation(int nThr, int iThr)
      {
         this.nThreads       = nThr;
         this.iThread        = iThr;
         nInNodes = inField.getNNodes();
      }

      @Override
      public void run()
      {
         float[] v = new float[3];
         int dk = nInNodes/nThreads;
         int kstart = iThread       * dk + Math.min(iThread,     nInNodes % nThreads);
         int kend   = (iThread + 1) * dk + Math.min(iThread + 1, nInNodes % nThreads);
         for (int k = kstart ; k < kend; k++)
         {
            for (int i = 0; i < nSpace; i++)
               v[i] = inCoords[nSpace * k + i] - outAffine[3][i];
            for (int i = 0; i < trueDim; i++)
            {
               recomputedCoords[trueDim * k + i] = 0;
               for (int j = 0; j < trueDim; j++)
                  recomputedCoords[trueDim * k + i] += outInvAffine[j][i] * v[j];
            }
         }
      }
   }
   
   private void prepareInterpolation()
   {
      dims = params.getResolution();
      outField = new RegularField(dims);
      for (int i = 0; i < outAffine.length; i++)
         Arrays.fill(outAffine[i], 0.f);
      outField.setNSpace(3);
      for (int i = 0; i < trueDim; i++)
      {
         for (int j = 0; j < trueDim; j++)
            outAffine[j][i] = outBaseAffine[j][i] / (dims[j] - 1);
         outAffine[3][i] = outBaseAffine[3][i];
      }
      outField.setAffine(outAffine);
      outInvAffine = outField.getInvAffine();
      
      Thread[] workThreads = new Thread[nThreads];
      for (int iThread = 0; iThread < nThreads; iThread++)
         workThreads[iThread] = new Thread(new CoordinateRecomputation(nThreads, iThread));
      for (int iThread = 0; iThread < nThreads; iThread++)
         workThreads[iThread].start();
      for (int i = 0; i < workThreads.length; i++)
         try {workThreads[i].join();}
         catch (Exception e){}
      int nNodes = 1;
      for (int i = 0; i < trueDim; i++)
         nNodes *= dims[i];
      valid = new boolean[nNodes];
      Arrays.fill(valid, false);
   }
   
   private void processTetra(int[] nodes, int index)
   {
// tetraCoords - coordinates of tetra vertices (recomputed to the outField index coordinate system)
      float[][] tetraCoords = new float[4][3];
      
      float[][] sliceVertsBaryCoords = new float[4][4];
      float[][] sliceVertsPlaneCoords = new float[4][2];
      
      float[][] segmentVertsTriangleBaryCoords = new float[2][3];
      float[][] segmentVertsTetraBaryCoords = new float[2][4];
      float[]   segmentVertsLineCoords = new float[2];
      
      for (int i = 0; i < nodes.length; i++)
         System.arraycopy(recomputedCoords, 3 * nodes[i], tetraCoords[i], 0, 3);
      float zMin = dims[2] - .9999f, zMax = -.0001f;
      float yMin = dims[1] - .9999f, yMax = -.0001f;
      float xMin = dims[0] - .9999f, xMax = -.0001f;
      for (int i = 0; i < tetraCoords.length; i++)
      {
         if (tetraCoords[i][2] < zMin) zMin = tetraCoords[i][2];
         if (tetraCoords[i][2] > zMax) zMax = tetraCoords[i][2];
         if (tetraCoords[i][1] < yMin) yMin = tetraCoords[i][1];
         if (tetraCoords[i][1] > yMax) yMax = tetraCoords[i][1];
         if (tetraCoords[i][0] < xMin) xMin = tetraCoords[i][0];
         if (tetraCoords[i][0] > xMax) xMax = tetraCoords[i][0];
      }
// xMin:xMax, yMin:yMax, zMin:zMax - range of tetra
      if (zMin > dims[2] - 1 || zMax < 0 || yMin > dims[1] - 1 || yMax < 0 || xMin > dims[0] - 1 || xMax < 0)
         return;
      if ((int)zMin == (int)zMax && zMin != (int)zMin ||
          (int)yMin == (int)yMax && yMin != (int)yMin ||
          (int)xMin == (int)xMax && xMin != (int)xMin)
         return;
      int k0 = Math.max((int)zMin, 0);
      if (k0 < zMin) k0 += 1;
      zMax = Math.min(dims[2] - 1, zMax);
      float[] vals = new float[4];
// loop over possible z-indices in outfield (planes z = iz cutting tetra)
      for (int iz = k0; iz <= zMax; iz++)
      {
         for (int p = 0; p < 4; p++)
            vals[p] = tetraCoords[p][2] - iz;
         int[] sliceNodes = slice[4][simplexCode(vals)];
         if (sliceNodes == null || sliceNodes.length < 3)
            continue;
 // computing triangle (or triangles) forming slice of the tetra and z=iz plane
 // slice nodes are vertices of the triangle or quad, 
 // sliceVertsBaryCoords[i] is a vector of coefficients of the i-th node as linear combinations of original coords
 // sliceVertsPlaneCoords[i] contains xy coordinates of the i-th node
         int nTri = 1;
         if (sliceNodes.length == 4) nTri = 2;
         for (int i = 0; i < sliceNodes.length; i++)
         {
            int is = sliceNodes[i];
            for (int j = 0; j < sliceVertsBaryCoords[i].length; j++)
               sliceVertsBaryCoords[i][j] = 0;
            if (is < 4)
            {
               sliceVertsBaryCoords[i][is] = 1;
               sliceVertsPlaneCoords[i][0] = tetraCoords[is][0];
               sliceVertsPlaneCoords[i][1] = tetraCoords[is][1];
            }
            else
            {
               int p0 = addNodes[4][is][0];
               int p1 = addNodes[4][is][1];
               float u = vals[p1] / (vals[p1] - vals[p0]);
               sliceVertsBaryCoords[i][p0] = u;
               sliceVertsBaryCoords[i][p1] = 1 - u;
               sliceVertsPlaneCoords[i][0] = u * tetraCoords[p0][0] + (1 - u) * tetraCoords[p1][0];
               sliceVertsPlaneCoords[i][1] = u * tetraCoords[p0][1] + (1 - u) * tetraCoords[p1][1];
//               if (Math.abs(iz - (u * tetraCoords[p0][2] + (1 - u) * tetraCoords[p1][2])) > .0001)
//                  System.out.printf("%5d %9.4f %9.4f%n", iz, u * tetraCoords[p0][2] + (1 - u) * tetraCoords[p1][2], 
//                                                         iz - (u * tetraCoords[p0][2] + (1 - u) * tetraCoords[p1][2]));
            }
         }
         
         for (int iTri = 0; iTri < nTri; iTri++)
         {
            int[] triNodes;
            if (iTri == 0)
               triNodes = new int[]{0, 1, 2};
            else
               triNodes = new int[]{0, 2, 3};
            float[][] triXYCoords = new float[3][];
// slicing z-slice triangle along along y axis 
            for (int i = 0; i < 3; i++)
               triXYCoords[i] = sliceVertsPlaneCoords[triNodes[i]];
            
            yMin = dims[1] - .9999f; yMax = -.0001f;
            xMin = dims[0] - .9999f; xMax = -.0001f;
            for (int i = 0; i < 3; i++)
            {
               if (triXYCoords[i][1] < yMin) yMin = triXYCoords[i][1];
               if (triXYCoords[i][1] > yMax) yMax = triXYCoords[i][1];
               if (triXYCoords[i][0] < xMin) xMin = triXYCoords[i][0];
               if (triXYCoords[i][0] > xMax) xMax = triXYCoords[i][0];
            }
            if (yMin > dims[1] - 1 || yMax < 0 || xMin > dims[0] - 1 || xMax < 0)
               continue;
            if ((int)yMin == (int)yMax && yMin != (int)yMin ||
                (int)xMin == (int)xMax && xMin != (int)xMin)
               continue;
            int l0 = Math.max((int)yMin, 0);
            if (l0 < yMin) l0 += 1;
            yMax = Math.min(dims[1] - 1, yMax);
            float[] triVals = new float[3];
// loop over possible y-indices in outfield (segments y = iy cutting triangle)
            for (int iy = l0; iy <= yMax; iy++)
            {
               for (int p = 0; p < 3; p++)
                  triVals[p] = triXYCoords[p][1] - iy;
               int[] segmentNodes = slice[2][simplexCode(triVals)]; 
               if (segmentNodes == null || segmentNodes.length < 2)
                  continue;
               
               for (int i = 0; i < 2; i++)
               {
                  int is = segmentNodes[i];
                  for (int j = 0; j < segmentVertsTriangleBaryCoords[i].length; j++)
                     segmentVertsTriangleBaryCoords[i][j] = 0;
                  if (is < 3)
                  {
                     segmentVertsTriangleBaryCoords[i][is] = 1;
                     segmentVertsLineCoords[i] = triXYCoords[is][0];
                     System.arraycopy(sliceVertsBaryCoords[triNodes[is]], 0, segmentVertsTetraBaryCoords[i], 0, 4);
                  }
                  else
                  {
                     int p0 = addNodes[3][is][0];
                     int p1 = addNodes[3][is][1];
                     float u = triVals[p1] / (triVals[p1] - triVals[p0]);
                     segmentVertsTriangleBaryCoords[i][p0] = u;
                     segmentVertsTriangleBaryCoords[i][p1] = 1 - u;
                     segmentVertsLineCoords[i] = u * triXYCoords[p0][0] + (1-u) * triXYCoords[p1][0];
                     for (int j = 0; j < 4; j++)
                        segmentVertsTetraBaryCoords[i][j] = u * sliceVertsBaryCoords[triNodes[p0]][j] + 
                                                        (1-u) * sliceVertsBaryCoords[triNodes[p1]][j];
                  }
               }
               
               xMin = segmentVertsLineCoords[0]; xMax = segmentVertsLineCoords[1];
               if (segmentVertsLineCoords[1] < segmentVertsLineCoords[0])
               {
                  xMin = segmentVertsLineCoords[1];
                  xMax = segmentVertsLineCoords[0];
               }
               if (xMin > dims[0] - 1 || xMax < 0)
                  continue;
               int m0 = Math.max((int)xMin, 0);
               if (m0 < xMin) m0 += 1;
               int nData = dims[2] * dims[1] * dims[0];
               float d = 1 / (segmentVertsLineCoords[1] - segmentVertsLineCoords[0]);
               for (int ix = m0, iData = (dims[1] * iz + iy) * dims[0] + m0; ix <= Math.min(xMax, dims[0] - 1); ix++, iData++)
               {
                  if (valid[iData])
                     continue;
                  float t = d * (segmentVertsLineCoords[1] - ix);
                  float[] baryCoords = new float[4];
                  for (int i = 0; i < 4; i++)
                     baryCoords[i] = t *  segmentVertsTetraBaryCoords[0][i] +
                               (1 - t) *  segmentVertsTetraBaryCoords[1][i];
                  float[] err = new float[]{-ix, -iy, -iz};
                  for (int i = 0; i < 4; i++)
                     for (int j = 0; j < 3; j++)
                        err[j] += baryCoords[i] * tetraCoords[i][j];
                  if (err[0] * err[0] + err[1] * err[1] + err[2] * err[2] > .00001)
                     System.out.printf("%3d %3d %3d   %7.3f %7.3f %7.3f %n", ix, iy, iz, err[0], err[1], err[2]);
                  valid[iData] = true;
                  for (int i = 0; i < nNumNodeDataArrays; i++)
                  {
                     int vlen = vlens[i];
                     for (int j = 0; j < vlen; j++)
                     {
                        outData[i][vlen * iData + j] = 0;
                        for (int k = 0; k < 4; k++)
                           outData[i][vlen * iData + j] += baryCoords[k] * inData[i][vlen * nodes[k] + j];
                     }
                  }
                  for (int i = nNumNodeDataArrays; i < nNumDArrays; i++)
                  {
                     int vlen = vlens[i];
                     for (int j = 0; j < vlen; j++)
                     {
                        outData[i][vlen * iData + j] = 0;
                        for (int k = 0; k < 4; k++)
                           outData[i][vlen * iData + j] += baryCoords[k] * inData[i][vlen * index + j];
                     }
                  }
               }
            }
         }
      }
   }

   private void processTriangle(int[] nodes, int index)
   {
// triCoords - coordinates of triangle vertices (recomputed to the outField index coordinate system)
      float[][] triCoords = new float[3][2];
      for (int i = 0; i < 3; i++)
         System.arraycopy(recomputedCoords, 2 * nodes[i], triCoords[i], 0, 2);
      float yMin = dims[1] - .9999f, yMax = -.0001f;
      float xMin = dims[0] - .9999f, xMax = -.0001f;
      for (int i = 0; i < triCoords.length; i++)
      {
         if (triCoords[i][1] < yMin) yMin = triCoords[i][1];
         if (triCoords[i][1] > yMax) yMax = triCoords[i][1];
         if (triCoords[i][0] < xMin) xMin = triCoords[i][0];
         if (triCoords[i][0] > xMax) xMax = triCoords[i][0];
      }
      float[][] segmentVertsBaryCoords = new float[2][3];
      float[]   segmentVertsLineCoords = new float[2];
      
// xMin:xMax, yMin:yMax - range of triangle
      if (yMin > dims[1] - 1 || yMax < 0 || xMin > dims[0] - 1 || xMax < 0)
         return;
      if ((int)yMin == (int)yMax && yMin != (int)yMin ||
          (int)xMin == (int)xMax && xMin != (int)xMin)
         return;
      int k0 = Math.max((int)yMin, 0);
      if (k0 < yMin) k0 += 1;
      int l0 = Math.max((int)yMin, 0);
      if (l0 < yMin) l0 += 1;
      yMax = Math.min(dims[1] - 1, yMax);
      float[] triVals = new float[3];
// loop over possible y-indices in outfield (segments y = iy cutting triangle)
      for (int iy = l0; iy <= yMax; iy++)
      {
         for (int p = 0; p < 3; p++)
            triVals[p] = triCoords[p][1] - iy;
         int[] segmentNodes = slice[2][simplexCode(triVals)]; 
         if (segmentNodes == null || segmentNodes.length < 2)
            continue;

         for (int i = 0; i < 2; i++)
         {
            int is = segmentNodes[i];
            for (int j = 0; j < segmentVertsBaryCoords[i].length; j++)
               segmentVertsBaryCoords[i][j] = 0;
            if (is < 3)
            {
               segmentVertsBaryCoords[i][is] = 1;
               segmentVertsLineCoords[i] = triCoords[is][0];
            }
            else
            {
               int p0 = addNodes[3][is][0];
               int p1 = addNodes[3][is][1];
               float u = triVals[p1] / (triVals[p1] - triVals[p0]);
               segmentVertsBaryCoords[i][p0] = u;
               segmentVertsBaryCoords[i][p1] = 1 - u;
               segmentVertsLineCoords[i] = u * triCoords[p0][0] + (1-u) * triCoords[p1][0];
            }
         }

         xMin = segmentVertsLineCoords[0]; xMax = segmentVertsLineCoords[1];
         if (segmentVertsLineCoords[1] < segmentVertsLineCoords[0])
         {
            xMin = segmentVertsLineCoords[1];
            xMax = segmentVertsLineCoords[0];
         }
         if (xMin > dims[0] - 1 || xMax < 0)
            continue;
         int m0 = Math.max((int)xMin, 0);
         if (m0 < xMin) m0 += 1;
         int nData = dims[1] * dims[0];
         float d = 1 / (segmentVertsLineCoords[1] - segmentVertsLineCoords[0]);
         for (int ix = m0, iData = iy * dims[0] + m0; ix <= Math.min(xMax, dims[0] - 1); ix++, iData++)
         {
            if (valid[iData])
               continue;
            float t = d * (segmentVertsLineCoords[1] - ix);
            float[] baryCoords = new float[3];
            for (int i = 0; i < 3; i++)
               baryCoords[i] = t *  segmentVertsBaryCoords[0][i] +
                         (1 - t) *  segmentVertsBaryCoords[1][i];
            float[] err = new float[]{-ix, -iy};
            for (int i = 0; i < 3; i++)
               for (int j = 0; j < 2; j++)
                  err[j] += baryCoords[i] * triCoords[i][j];
            if (err[0] * err[0] + err[1] * err[1] > .00001)
               System.out.printf("%3d %3d   %7.3f %7.3f %n", ix, iy, err[0], err[1]);
            valid[iData] = true;
            for (int i = 0; i < nNumNodeDataArrays; i++)
            {
               int vlen = vlens[i];
               for (int j = 0; j < vlen; j++)
               {
                  outData[i][vlen * iData + j] = 0;
                  for (int k = 0; k < 3; k++)
                     outData[i][vlen * iData + j] += baryCoords[k] * inData[i][vlen * nodes[k] + j];
               }
            }
            for (int i = nNumNodeDataArrays; i < nNumDArrays; i++)
            {
               int vlen = vlens[i];
               for (int j = 0; j < vlen; j++)
               {
                  outData[i][vlen * iData + j] = 0;
                  for (int k = 0; k < 3; k++)
                     outData[i][vlen * iData + j] += baryCoords[k] * inData[i][vlen * index + j];
               }
            }
         }
      }
   }

   class UpdateRegularFieldPart implements Runnable
   {
      int from;
      int to;
      RegularField inField;
      
      public UpdateRegularFieldPart(int n, int nThreads, int iThread, RegularField inField)
      {
         this.inField = inField;
         from = n * iThread / nThreads;
         to =   n * (iThread + 1) / nThreads;
      }

      @Override
      public void run()
      {
         if (trueDim == 3)
            for (int i = from; i < to; i++)
            {
               int[] tetras = inField.getTetras(i);
               if (tetras == null)
                   continue;
               for (int tet = 0; tet < 5; tet++)
               {
                  int[] tetraVerts = new int[4];
                  System.arraycopy(tetras, 4 * tet, tetraVerts, 0, 4);
                  processTetra(tetraVerts, 0);
               }
            }
         else
            for (int i = from; i < to; i++)
            {
               int[] triangles = inField.getTriangles(i);
               if (triangles == null)
                   continue;
               for (int tri = 0; tri < 5; tri++)
               {
                  int[] triVerts = new int[3];
                  System.arraycopy(triangles, 3 * tri, triVerts, 0, 4);
                  processTriangle(triVerts, 0);
               }
            }
      }
   }
   
   private void updateOutField(RegularField inField)
   {
      int[] inDims = inField.getDims();
      nNumDArrays = 0;
      for (int i = 0; i < inField.getNData(); i++)
         if (inField.getData(i).isSimpleNumeric()) nNumDArrays += 1;
      nNumNodeDataArrays = nNumDArrays;
      inData  = new float[nNumDArrays][];
      outData = new float[nNumDArrays][];
      indices = new int[nNumDArrays];
      vlens = new int[nNumDArrays];
      for (int i = 0, j = 0; i < inField.getNData(); i++)
         if (inField.getData(i).isSimpleNumeric())
         {
            DataArray da = inField.getData(i);
            indices[j] = i;
            vlens[j] = da.getVeclen();
            inData[j] = da.getFData();
            outData[j] = new float[vlens[j] * dims[0] * dims[1] *dims[2]];
            j += 1;
         }
      
      int n = (inDims[0] -1) * (inDims[1] -1) * (inDims[2] -1);
      Thread[] workThreads = new Thread[nThreads];
      for (int iThread = 0; iThread < nThreads; iThread++)
         workThreads[iThread] = new Thread(new UpdateRegularFieldPart(n, nThreads, iThread, inField));
      for (int iThread = 0; iThread < nThreads; iThread++)
         workThreads[iThread].start();
      for (int iThread = 0; iThread < workThreads.length; iThread++)
         try {workThreads[iThread].join();}
         catch (Exception e){}
      
      for (int i = 0; i < nNumDArrays; i++)
      {
         DataArray da = inField.getData(indices[i]);
         DataArray outDA = DataArray.create(outData[i], da.getVeclen(), da.getName());
         outDA.setMaxv(da.getMaxv());
         outDA.setMinv(da.getMinv());
         outDA.setPhysMin(da.getPhysMin());
         outDA.setPhysMax(da.getPhysMax());
         outField.addData(outDA);
      }
      outField.setMask(valid);
      outField.setExtents(extents);
   }

   
   class UpdateIrregularFieldPart implements Runnable
   {
      int from;
      int to;
      int[] nodes;
      int[] indices;
      
      public UpdateIrregularFieldPart(int n, int nThreads, int iThread, int[] nodes, int[] indices)
      {
         this.nodes = nodes;
         this.indices = indices;
         from = n * iThread / nThreads;
         to =   n * (iThread + 1) / nThreads;
      }

      @Override
      public void run()
      {
         if (trueDim == 3)
            for (int tet = from; tet < to; tet++)
            {
               int[] tetraVerts = new int[4];
               System.arraycopy(nodes, 4 * tet, tetraVerts, 0, 4);
               if (indices != null)
                  processTetra(tetraVerts, indices[tet]);
               else
                  processTetra(tetraVerts, 0);
            }
         else
            for (int tri = from; tri < to; tri++)
            {
               int[] triVerts = new int[4];
               System.arraycopy(nodes, 3 * tri, triVerts, 0, 3);
               if (indices != null)
                  processTriangle(triVerts, indices[tri]);
               else
                  processTriangle(triVerts, 0);
            }
      }
   }
   
   private void updateOutField(IrregularField inField)
   {
      nNumDArrays = 0;
      for (int i = 0; i < inField.getNData(); i++)
         if (inField.getData(i).isSimpleNumeric()) nNumDArrays += 1;
      nNumNodeDataArrays = nNumDArrays;
      cellDataInterpolable = inField.getNCellSets() == 1 && inField.getCellSet(0).getNData() > 0;
      if (cellDataInterpolable)
      {
         CellSet cs = inField.getCellSet(0);
         for (int i = 0; i < cs.getNData(); i++)
            if (cs.getData(i).isSimpleNumeric()) nNumDArrays += 1;
      }
      inData  = new float[nNumDArrays][];
      outData = new float[nNumDArrays][];
      indices = new int[nNumDArrays];
      vlens = new int[nNumDArrays];
      int iOutData = 0;
      int nNodes = 1;
      for (int j = 0; j < trueDim; j++)
         nNodes *= dims[j];
      for (int i = 0; i < inField.getNData(); i++)
         if (inField.getData(i).isSimpleNumeric())
         {
            DataArray da = inField.getData(i);
            indices[iOutData] = i;
            vlens[iOutData] = da.getVeclen();
            inData[iOutData] = da.getFData();
            outData[iOutData] = new float[vlens[iOutData] * nNodes];
            iOutData += 1;
         }
      if (cellDataInterpolable)
      {
         CellSet cs = inField.getCellSet(0);
         for (int i = 0; i < cs.getNData(); i++)
            if (cs.getData(i).isSimpleNumeric())
            {
               DataArray da = cs.getData(i);
               indices[iOutData] = i;
               vlens[iOutData] = da.getVeclen();
               inData[iOutData] = da.getFData();
               outData[iOutData] = new float[vlens[iOutData] * dims[0] * dims[1] *dims[2]];
               iOutData += 1;
            }
      }
      for (CellSet inCellSet : inField.getCellSets())
      {
         if (trueDim == 3)
            for (int i = Cell.TETRA; i <= Cell.HEXAHEDRON; i++)
            {
               if (inCellSet.getCellArray(i) == null)
                  continue;
               CellArray inCellArray = inCellSet.getCellArray(i).triangulate();
               int n = inCellArray.getNCells();
               Thread[] workThreads = new Thread[nThreads];
               for (int iThread = 0; iThread < nThreads; iThread++)
                  workThreads[iThread] = new Thread(new UpdateIrregularFieldPart(n, nThreads, iThread, inCellArray.getNodes(), inCellArray.getDataIndices()));
               for (int iThread = 0; iThread < nThreads; iThread++)
                  workThreads[iThread].start();
               for (int iThread = 0; iThread < workThreads.length; iThread++)
                  try {workThreads[iThread].join();}
                  catch (Exception e){}
            }
         else if (trueDim == 2)
            for (int i = Cell.TRIANGLE; i <= Cell.QUAD; i++)
            {
               if (inCellSet.getCellArray(i) == null)
                  continue;
               CellArray inCellArray = inCellSet.getCellArray(i).triangulate();
               int n = inCellArray.getNCells();
               Thread[] workThreads = new Thread[nThreads];
               for (int iThread = 0; iThread < nThreads; iThread++)
                  workThreads[iThread] = new Thread(new UpdateIrregularFieldPart(n, nThreads, iThread, inCellArray.getNodes(), inCellArray.getDataIndices()));
               for (int iThread = 0; iThread < nThreads; iThread++)
                  workThreads[iThread].start();
               for (int iThread = 0; iThread < workThreads.length; iThread++)
                  try {workThreads[iThread].join();}
                  catch (Exception e){}
            }
      }
      for (int i = 0; i < nNumDArrays; i++)
      {
         DataArray da = null;
         if (i < nNumNodeDataArrays)
            da = inField.getData(indices[i]);
         else
            da = inField.getCellSet(0).getData(indices[i]);
         DataArray outDA = DataArray.create(outData[i], da.getVeclen(), da.getName());
         outDA.setMaxv(da.getMaxv());
         outDA.setMinv(da.getMinv());
         outDA.setPhysMin(da.getPhysMin());
         outDA.setPhysMax(da.getPhysMax());
         outField.addData(outDA);
      }
      outField.setMask(valid);
      outField.setExtents(boxExtents);
   }
   
   protected boolean prepareOutputGeometry()
   {
      if (!super.prepareOutputGeometry())
         return false;
      outObj.addNode(boxGroup);
      return true;
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
            trueDim  = inField.getTrueDim();
            if (trueDim < 2)
               return;
            extents = inField.getExtents();
            initBaseAffine();
            transformBox();
            inCoords = inField.getCoords();
            computeUI.setInField(inField);
            recomputedCoords = new float[trueDim * inField.getNNodes()];
            lastAxis = -1;
         }
      }
      fromUI = false;
      if (change == OUTPUT)
      {
         trueDim  = inField.getTrueDim();
         if (trueDim < 2)
            return;
         nSpace   = inField.getNSpace();
         prepareInterpolation();
         if (inField instanceof IrregularField)
            updateOutField((IrregularField)inField);
         else if (inField instanceof RegularField)
            updateOutField((RegularField)inField);
         prepareOutputGeometry();
         setOutputValue("outField", new VNRegularField(outField));
      }
      change = TRANSFORM;
      show();
   }
}
