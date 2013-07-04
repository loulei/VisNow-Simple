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

package pl.edu.icm.visnow.datasets;

import java.io.Serializable;
import pl.edu.icm.visnow.datasets.cells.Cell;
import pl.edu.icm.visnow.datasets.cells.RegularHex;
import pl.edu.icm.visnow.datasets.cells.Tetra;
import pl.edu.icm.visnow.datasets.cells.SimplexPosition;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.lib.utils.RabinHashFunction;
import pl.edu.icm.visnow.lib.utils.VNFloatFormatter;
import pl.edu.icm.visnow.lib.utils.field.RegularCellsTriangulation;
import pl.edu.icm.visnow.lib.utils.numeric.NumericalMethods;


/**
 *
 * @author  Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class RegularField extends Field implements Serializable
{
   public static final short[][] partNeighbDist = 
     {{}, {3, 3}, {4, 3, 4, 3, 3, 4, 3, 4}, 
      {   4,    4, 3, 4,    4,  
       4, 3, 4, 3,    3, 4, 3, 4,  
          4,    4, 3, 4,    4}};
   public static final short[][] fullNeighbDist = 
      {{}, {3, 3}, {4, 3, 4, 3, 3, 4, 3, 4}, 
      {5, 4, 5, 4, 3, 4, 5, 4, 5,
       4, 3, 4, 3,    3, 4, 3, 4,
       5, 4, 5, 4, 3, 4, 5, 4, 5}};
   
   private static final long serialVersionUID = -6060827781890957096L;
   private static final int MAXCELLDIM = 100;
   protected int[] dims               = null;
   protected float[][] affine         = new float[4][3];
   protected float[][] invAffine      = new float[3][3];
   protected float[][] rectilinarCoords = new float[3][];
   protected float[] coordsFromAffine = null;
   protected int[] cellExtentsDown    = new int[3];
   protected int[] cellExtentsDims    = new int[3];
   protected int[][] treeCells        = new int[3][];
   protected int[] cellNodeOffsets;
   protected int[] fullNeighbOffsets;
   protected int[] partNeighbOffsets;
   protected int[] strictNeighbOffsets;
   
   public RegularField()
   {
      schema = new RegularFieldSchema();
   }
   /**
    * Creates a new <code>RegularField</code> object.
    * 
    * @param	inData	The inData.
    */
   public RegularField(byte[] inData)
   {
      schema = new RegularFieldSchema();
      setDims(new int[] {0xff & inData[2], 0xff & inData[1], 0xff & inData[0]});

      byte[] data0 = new byte[nNodes];
      for (int i = 0, l = 3; i < nNodes; i++, l++)
         data0[i] = inData[l];
      data.add(DataArray.create(data0, 1, "vol0"));
   }
   
   /** Creates a new instance of ExampleField */
   public RegularField(int[] dims)
   {
      schema = new RegularFieldSchema();
      setDims(dims);
      nNodes = 1;
      for (int i = 0; i < dims.length; i++)
         nNodes*=dims[i];
      nSpace = dims.length;
      affineFromPoints();
      computeInvAffine();
   }
   
   public RegularField(int[] dims, boolean normalize)
   {
      schema = new RegularFieldSchema();
      setDims(dims);
      nNodes = 1;
      for (int i = 0; i < dims.length; i++)
         nNodes*=dims[i];
      nSpace = dims.length;
      if (!normalize)
      {
         extents[1][0] = dims[0] - 1.f;
         extents[1][1] = dims[1] - 1.f;
         extents[1][2] = dims[2] - 1.f;
         extents[0][0] = extents[0][1] = extents[0][2] = 0;
      }
      affineFromPoints();
      computeInvAffine();
   }

   public RegularField(int[] dims, float[][] pts)
   {
      schema = new RegularFieldSchema();
      setDims(dims);
      this.extents  = pts;
      nNodes = 1;
      for (int i = 0; i < dims.length; i++)
         nNodes*=dims[i];
      nSpace = pts[0].length;
      affineFromPoints();
      computeInvAffine();
   }
   
   @Override
   public String toString()
   {  
      StringBuilder s = new StringBuilder();
      s.append("Regular Field ").append(dims.length).append("D  ").
        append(nSpace).append("-space, ");
      if (getNFrames() > 1)
         s.append(getNFrames()).append(" time frames ");
      s.append("dimensions = {").append(dims[0]);
      if (dims.length > 1)
         s.append("x").append(dims[1]);
      if (dims.length > 2)
         s.append("x").append(dims[2]);
      return s.toString() + "}";
   }
   
   @Override
   public String shortDescription()
   {
      StringBuilder s = new StringBuilder();
      for (int i = 0; i < dims.length; i++)
         if (i > 0)
            s.append("x").append(dims[i]);
         else
            s.append("<html>").append(dims[i]);
      if (getNFrames() > 1)
         s.append("<p>").append(getNFrames()).append(" timesteps");
      s.append("").append(data.size()).append(" components</html>");
      return s.toString();
   }
   
   public String toMultilineString()
   {  
      StringBuilder s = new StringBuilder();
      s.append("<html>Regular Field ").append(dims.length).append("D  ");
      s.append(nSpace).append("-space,<br>");
      if (getNFrames() > 1)
         s.append(getNFrames()).append(" time frames <br>");
      s.append("dimensions = {").append(dims[0]);
      if (dims.length > 1)
         s.append("x").append(dims[1]);
      if (dims.length > 2)
         s.append("x").append(dims[2]);
      return s.toString() + "}</html>";
   }
   
    @Override
    public String description() {
        StringBuffer s = new StringBuffer();
        s.append("Regular Field ").append(dims.length).append("D  ");
        s.append(nSpace).append("-space, ");
        if (trueDim > 0) {
            s.append("true " + dims.length + "-dim ");
        }
        if (getNFrames() > 1) {
            s.append(getNFrames()).append(" timesteps<p>");
            s.append("time range ");
            s.append(VNFloatFormatter.defaultRangeFormat(getStartTime()));
            s.append(timeUnit);
            s.append(":");
            s.append(VNFloatFormatter.defaultRangeFormat(getEndTime()));
            s.append(timeUnit);
        }
        s.append("<p>dimensions = {").append(dims[0]);
        if (dims.length > 1) {
            s.append("x").append(dims[1]);
        }
        if (dims.length > 2) {
            s.append("x").append(dims[2]);
        }
        s.append("}");
        
        s.append("<p>geometric extents</p>");
        for (int i = 0; i < nSpace; i++) {
            s.append("<p>[");
            s.append(VNFloatFormatter.defaultRangeFormat(extents[0][i]));
            s.append(", ");
            s.append(VNFloatFormatter.defaultRangeFormat(extents[1][i]));
            s.append("]</p> ");
        }
        s.append("<p>physical extents</p>");
        for (int i = 0; i < nSpace; i++) {
            s.append("<p>[");
            s.append(VNFloatFormatter.defaultRangeFormat(physExts[0][i]));
            s.append(", ");
            s.append(VNFloatFormatter.defaultRangeFormat(physExts[1][i]));
            s.append("]</p> ");
        }
        if (timeCoords != null && !timeCoords.isEmpty()) {
            s.append("<p>with explicit coordinates</p>");
        } else {
            float maxv = 0.0f;
            float minv = 0.0f;
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 3; j++) {
                    if (affine[i][j] != 0.0f && affine[i][j] > maxv) {
                        maxv = affine[i][j];
                    }
                    if (affine[i][j] != 0.0f && affine[i][j] < minv) {
                        minv = affine[i][j];
                    }
                }
            }

            s.append("<p>origin at (");
            s.append(VNFloatFormatter.defaultRangeFormat(affine[3][0]));
            s.append(", ");
            s.append(VNFloatFormatter.defaultRangeFormat(affine[3][1]));
            s.append(", ");
            s.append(VNFloatFormatter.defaultRangeFormat(affine[3][2]));
            s.append(")<p>cell vectors:<p>");            
            for (int i = 0; i < dims.length; i++) {
                s.append("(");
                s.append(VNFloatFormatter.defaultRangeFormat(affine[i][0]));
                s.append(", ");
                s.append(VNFloatFormatter.defaultRangeFormat(affine[i][1]));
                s.append(", ");
                s.append(VNFloatFormatter.defaultRangeFormat(affine[i][2]));
                s.append(")</p><p>");
            }
        }
        if (timeMask != null && !timeMask.isEmpty()) {
            s.append("<p>with mask</p>");
        }
        s.append("<font size=\"-1\"> <TABLE border=\"0\">"
                + "<TR><TD>Component<TD>vlen<td>type<td>st.<td>min<td>max<td>physMin</td><td>physMax</td>");
        for (int i = 0; i < data.size(); i++) {
            s.append(getData(i).description());
        }
        s.append("</TABLE></font>");
        return "<html>" + s + "</html>";
    }

   @Override
   public final int getType()
   {
      return Field.REGULAR;
   }
    
   public RegularField cloneBase()
   {
      RegularField clone = new  RegularField(this.dims, this.extents);
      clone.setNSpace(this.nSpace);
      clone.setAffine(this.affine);
      if (this.timeCoords != null && !timeCoords.isEmpty())
         clone.setCoords(this.timeCoords);
      return clone;
   }

   @Override
   public RegularField clone()
   {
      RegularField clone = cloneBase();
      for (DataArray dataArray : data)
         clone.addData(dataArray);
      return clone;
   }

   @Override
   public RegularField cloneDeep()
   {
      RegularField clone = new  RegularField(this.dims.clone(), this.extents.clone());
      clone.setNSpace(this.nSpace);
      clone.setAffine(this.affine.clone());
      if (this.timeCoords != null && !timeCoords.isEmpty())
         clone.setCoords((TimeData<float[]>)this.timeCoords.clone());
      for (DataArray dataArray : data)
         clone.addData(dataArray.clone(dataArray.getName()));
      return clone;
   }


   @Override
   public RegularFieldSchema getSchema()
   {
      return (RegularFieldSchema)schema;
   }
   
   private void affineFromPoints()
   {
      for (int i = 0; i < extents[0].length; i++)
      {
         affine[3][i] = extents[0][i];
         for (int j = 0; j < 3; j++)
            affine[j][i] = 0;
         if (i<dims.length && dims[i]>1)
            affine[i][i] = (extents[1][i]-extents[0][i])/(dims[i]-1);
      }
      for (int i = extents[0].length; i < 3; i++)
      {
         for (int j = 0; j < 4; j++)
            affine[j][i] = 0;
         affine[i][i] = 1;
      }
      computeInvAffine();
   }

   public int[] getFullNeighbOffsets()
   {
      return fullNeighbOffsets;
   }

   public int[] getPartNeighbOffsets()
   {
      return partNeighbOffsets;
   }

   public int[] getStrictNeighbOffsets()
   {
      return strictNeighbOffsets;
   }

   public int[] getCellNodeOffsets()
   {
      return cellNodeOffsets;
   }
   
   @SuppressWarnings("ReturnOfCollectionOrArrayField")
   public float[][] getAffine()
   {
      return affine;
   }
   
   @SuppressWarnings("ReturnOfCollectionOrArrayField")
   public float[][] getInvAffine()
   {
      return invAffine;
   }

   @SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
   public void setAffine(float[][] affine)
   {
      if (affine == null || affine.length != 4
              || affine[0].length != 3 || affine[1].length != 3 || affine[2].length != 3 || affine[3].length != 3)
         return;
      this.affine = affine;

      float t;
      for (int i = 0; i < extents[0].length; i++)
      {
         extents[0][i] = extents[1][i] = affine[3][i];
         for (int x = 0; x < 2; x++)
            for (int y = 0; y < 2; y++)
               for (int z = 0; z < 2; z++)
               {
                  t = affine[3][i] + x * (dims[0] - 1) * affine[0][i];
                  if (dims.length > 1)
                     t += y * (dims[1] - 1) * affine[1][i];
                  if (dims.length > 2)
                     t += z * (dims[2] - 1) * affine[2][i];

                  if (t < extents[0][i])
                     extents[0][i] = t;
                  if (t > extents[1][i])
                     extents[1][i] = t;
               }
      }
      computeInvAffine();
   }
   
   private void computeInvAffine()
   {
      float[][] a =  new float[3][3];
      for (int i = 0; i < a.length; i++)
         System.arraycopy(affine[i], 0, a[i], 0, a[i].length);
      NumericalMethods.invert(a, invAffine);
      coordsTimestamp = System.currentTimeMillis();
      coordsHash = RabinHashFunction.hash(affine);
   }
   
   @SuppressWarnings("ReturnOfCollectionOrArrayField")
   public int[] getDims()
   {
      return this.dims;
   }
   
   //~--- set methods --------------------------------------------------------
   
   /**
    * Setter for property dims.
    * @param dims New value of property dims.
    */
   @SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
   public final void setDims(int[] dims)
   {
      this.dims = dims;
      nNodes = 1;
      for (int i = 0; i < dims.length; i++)
         nNodes*=dims[i];
      nSpace = dims.length;
       for (int i = 0; i < 2; i++) {
           for (int j = 0; j < 3; j++) {
               extents[i][j] = 0.0f;
           }
       }
       
       for (int i = 0; i < dims.length; i++) {
        extents[1][i] = dims[i] - 1.f;
       }
       for (int i = dims.length; i < 3; i++) {
           extents[1][i] = 0.0f;           
       }
      ((RegularFieldSchema)schema).setNDims(dims.length);
      affineFromPoints();
      switch (dims.length)
      {
         case 3:
            fullNeighbOffsets = new int[]
            {-dims[0]*dims[1] - dims[0] - 1, -dims[0]*dims[1] - dims[0], -dims[0]*dims[1] - dims[0] + 1,
             -dims[0]*dims[1]           - 1, -dims[0]*dims[1]          , -dims[0]*dims[1]           + 1,
             -dims[0]*dims[1] + dims[0] - 1, -dims[0]*dims[1] + dims[0], -dims[0]*dims[1] + dims[0] + 1,
                              - dims[0] - 1,                  - dims[0],                  - dims[0] + 1,
                                        - 1,                                                        + 1,
                                dims[0] - 1,                    dims[0],                    dims[0] + 1,
              dims[0]*dims[1] - dims[0] - 1,  dims[0]*dims[1] - dims[0],  dims[0]*dims[1] - dims[0] + 1,
              dims[0]*dims[1]           - 1,  dims[0]*dims[1]          ,  dims[0]*dims[1]           + 1,
              dims[0]*dims[1] + dims[0] - 1,  dims[0]*dims[1] + dims[0],  dims[0]*dims[1] + dims[0] + 1,
             };
            
            partNeighbOffsets = new int[]
            {                                -dims[0]*dims[1] - dims[0],
             -dims[0]*dims[1]           - 1, -dims[0]*dims[1]          , -dims[0]*dims[1]           + 1,
                                             -dims[0]*dims[1] + dims[0],
                              - dims[0] - 1,                  - dims[0],                  - dims[0] + 1,
                                        - 1,                                                        + 1,
                                dims[0] - 1,                    dims[0],                    dims[0] + 1,
                                              dims[0]*dims[1] - dims[0],
              dims[0]*dims[1]           - 1,  dims[0]*dims[1]          ,  dims[0]*dims[1]           + 1,
                                              dims[0]*dims[1] + dims[0],
             };
            strictNeighbOffsets = new int[]
            {   -dims[0]*dims[1],
                  - dims[0],
             - 1,              1,
                    dims[0],
                dims[0]*dims[1]
            };
            cellNodeOffsets = new int[]{                              0,                               1,
                                                            dims[0] + 1,                     dims[0], 
                                        dims[0] * dims[1],               dims[0] * dims[1]           + 1, 
                                        dims[0] * dims[1] + dims[0] + 1, dims[0] * dims[1] + dims[0]};
            return;
         case 2:
            fullNeighbOffsets = partNeighbOffsets = new int[]
            {-dims[0] - 1, -dims[0], -dims[0] + 1,
                      - 1,                      1,
              dims[0] - 1,  dims[0],  dims[0] + 1
            };
            strictNeighbOffsets = new int[]
            {    - dims[0],
             - 1,           1,
                   dims[0]
            };
            cellNodeOffsets = new int[]{          0,          1,
                                        dims[0] + 1,dims[0]};
            return;
         case 1:
            fullNeighbOffsets = partNeighbOffsets = strictNeighbOffsets = new int[] {-1, 1};
            cellNodeOffsets = new int[]{0, 1};
            return;
      }
   }
   
   /**
    * Setter for property extents.
    * @param extents New value of property extents.
    */
   public void setPts(float[][] pts)
   {
      this.extents = pts;
      for (int i = 0; i < 2; i++)
         System.arraycopy(pts[i], 0, physExts[i], 0, pts[0].length);
      affineFromPoints();
   }
   
   public void setScale(float[] scale)
   {
      if (dims.length == 2)
      {
         int nn = dims[0];
         if (dims[1] > nn)  nn = dims[1];
         extents[1][0] = scale[0] * (dims[0] - 1.f) / (2.f * nn);
         extents[1][1] = scale[1] * (dims[1] - 1.f) / (2.f * nn);
         extents[1][2] = .5f;
         extents[0][0] = -extents[1][0];
         extents[0][1] = -extents[1][1];
         extents[0][2] = -extents[1][2];
         
      }
      else if (dims.length == 3)
      {
         int nn = dims[0];
         if (dims[1] > nn)  nn = dims[1];
         if (dims[2] > nn)  nn = dims[2];
         extents[1][0] = scale[0] * (dims[0] - 1.f) / (2.f * nn);
         extents[1][1] = scale[1] * (dims[1] - 1.f) / (2.f * nn);
         extents[1][2] = scale[2] * (dims[2] - 1.f) / (2.f * nn);
         extents[0][0] = -extents[1][0];
         extents[0][1] = -extents[1][1];
         extents[0][2] = -extents[1][2];
      }
      affineFromPoints();
   }
   
   public void setRectilinearCoords(int coord, float[] c)
   {
      if (c == null || coord < 0 || coord >= 3)
         return;
      rectilinarCoords[coord] = c;
      extents[0][coord] = Float.MAX_VALUE;
      extents[1][coord] = -Float.MAX_VALUE;
      for (int i = 0; i < c.length; i++)
      {
         float f = c[i];
         if (extents[0][coord] > f) extents[0][coord] = f;
         if (extents[1][coord] < f) extents[1][coord] = f;
      }
      affineFromPoints();
   }
   
   public float[] getNodeCoords(int n)
   {
      if (timeCoords != null && !timeCoords.isEmpty())
      {
         float[] crds = new float[nSpace];
         for (int i = 0, j = n * nSpace; i < crds.length; i++, j++)
            crds[i] = timeCoords.get(currentFrame)[j];
      }
      int i, j, k;
      switch (dims.length)
      {
      case 3:
         i = n%dims[0];
         n /= dims[0];
         j = n%dims[1];
         k = n/dims[1];
         return getGridCoords(i, j, k);
      case 2:
         i = n%dims[0];
         j = n/dims[0];
         return getGridCoords(i, j);
      default:
         return getGridCoords(n);
      }
   }

   public byte[] getInterpolatedData(byte[] data, float u, float v, float w)
   {
       return RegularFieldInterpolator.getInterpolatedData(data, this.dims, u, v, w);
   }

   public short[] getInterpolatedData(short[] data, float u, float v, float w)
   {
       return RegularFieldInterpolator.getInterpolatedData(data, this.dims, u, v, w);
   }

   public int[] getInterpolatedData(int[] data, float u, float v, float w)
   {
       return RegularFieldInterpolator.getInterpolatedData(data, this.dims, u, v, w);
   }
   
   public float[] getInterpolatedData(float[] data, float u, float v, float w)
   {
       return RegularFieldInterpolator.getInterpolatedData(data, this.dims, u, v, w);
   }

   public double[] getInterpolatedData(double[] data, float u, float v, float w)
   {
       return RegularFieldInterpolator.getInterpolatedData(data, this.dims, u, v, w);
   }
   
   public float[] getGridCoords(int u)
   {
      if (dims.length != 1)
         return null;
      if (u<0) u=0; if (u>dims[0]-1) u=dims[0]-1;
      float[] c = new float[nSpace];
      if (timeCoords == null || timeCoords.isEmpty())
         for (int l = 0; l < nSpace; l++)
            c[l] = affine[3][l]+u*affine[0][l];
      else
         for (int i = 0, j = u*nSpace; i < c.length; i++, j++)
            c[i] = timeCoords.get(currentFrame)[j];
      return c;
   }
   
   public float[] getGridCoords(int u, int v)
   {
      if (dims.length != 2)
         return null;
      if (u<0) u=0; if (u>dims[0]-1) u=dims[0]-1;
      if (v<0) v=0; if (v>dims[1]-1) v=dims[1]-1;
      float[] c = new float[nSpace];
      if (timeCoords == null || timeCoords.isEmpty())
         for (int l = 0; l < nSpace; l++)
            c[l] = affine[3][l]+u*affine[0][l]+v*affine[1][l];
      else
         for (int i = 0, j = (v*dims[0] + u) * nSpace; i < c.length; i++, j++)
            c[i] = timeCoords.get(currentFrame)[j];
      return c;
   }
    
   public float[] getGridCoords(int u, int v, int w)
   {
      if (dims.length != 3)
         return null;
      if (u<0) u=0; if (u>dims[0]-1) u=dims[0]-1;
      if (v<0) v=0; if (v>dims[1]-1) v=dims[1]-1;
      if (w<0) w=0; if (w>dims[2]-1) w=dims[2]-1;
      float[] c = new float[nSpace];
      if (timeCoords == null  || timeCoords.isEmpty())
         for (int l = 0; l < 3; l++)
            c[l] = affine[3][l]+u*affine[0][l]+v*affine[1][l]+w*affine[2][l];
      else
         for (int i = 0, j = ((w*dims[1] + v)*dims[0] + u) * nSpace; i < c.length; i++, j++)
            c[i] = timeCoords.get(currentFrame)[j];
      return c;
   }

   public float[] getGridCoords(float u)
   {
      if (dims.length != 1)
         return null;
      if (u<0) u=0; if (u>dims[0]-1) u=dims[0]-1;
      if (timeCoords == null  || timeCoords.isEmpty())
      {
         float[] c = new float[nSpace];
         if (u<0) u=0; if (u>dims[0]-1) u=dims[0]-1;
         for (int l = 0; l < nSpace; l++)
            c[l] = affine[3][l]+u*affine[0][l];
         return c;
      }
      return getInterpolatedData(timeCoords.get(currentFrame), u, 0.f, 0.f);
   }
   
   public float[] getGridCoords(float u, float v)
   {
      if (dims.length != 2)
         return null;
      if (u<0) u=0; if (u>dims[0]-1) u=dims[0]-1;
      if (v<0) v=0; if (v>dims[1]-1) v=dims[1]-1;
      if (timeCoords == null || timeCoords.isEmpty())
      {
         float[] c = new float[nSpace];
         for (int l = 0; l < nSpace; l++)
            c[l] = affine[3][l]+u*affine[0][l]+v*affine[1][l];
         return c;
      }
      return getInterpolatedData(timeCoords.get(currentFrame), u, v, 0.f);
   }
   
   public float[] getGridCoords(float u, float v, float w)
   {
      if (dims.length != 3)
         return null;
      if (u<0) u=0; if (u>dims[0]-1) u=dims[0]-1;
      if (v<0) v=0; if (v>dims[1]-1) v=dims[1]-1;
      if (w<0) w=0; if (w>dims[2]-1) w=dims[2]-1;
      if (timeCoords == null || timeCoords.isEmpty())
      {
         float[] c = new float[nSpace];
         for (int l = 0; l < 3; l++)
            c[l] = affine[3][l]+u*affine[0][l]+v*affine[1][l]+w*affine[2][l];
         return c;
      }
      return getInterpolatedData(timeCoords.get(currentFrame), u, v, w);
   }

   public float[] get2DSliceData(int comp, int ind, int s)
   {
      if ((comp < 0) || (comp >= data.size()) || (dims.length < 2))
      {
         return null;
      }
      
      if (dims.length == 2)
      {
         return data.get(comp).get2DSlice(0, dims[0], 1,
                 dims[1], dims[0]);
      }
      else
      {
         switch (ind)
         {
            case 0 :
               return data.get(comp).get2DSlice(s, dims[1],
                       dims[0], dims[2], dims[0] * dims[1]);
               
            case 1 :
               return data.get(comp).get2DSlice(s * dims[0],
                       dims[0], 1, dims[2], dims[0] * dims[1]);
               
            case 2 :
               return data.get(comp).get2DSlice(s * dims[0]
                       * dims[1], dims[0], 1, dims[1], dims[0]);
         }
      }
      
      return null;
   }
      
   public float[] get2DNormSliceData(int comp, int ind, int s)
   {
      if ((comp < 0) || (comp >= data.size()) || (dims.length < 2))
      {
         return null;
      }
      
      if (dims.length == 2)
      {
         return data.get(comp).get2DNormSlice(0, dims[0], 1,
                 dims[1], dims[0]);
      }
      else
      {
         switch (ind)
         {
            case 0 :
               return data.get(comp).get2DNormSlice(s, dims[1],
                       dims[0], dims[2], dims[0] * dims[1]);
               
            case 1 :
               return data.get(comp).get2DNormSlice(s * dims[0],
                       dims[0], 1, dims[2], dims[0] * dims[1]);
               
            case 2 :
               return data.get(comp).get2DNormSlice(s * dims[0]
                       * dims[1], dims[0], 1, dims[1], dims[0]);
         }
      }
      
      return null;
   }
      
    public DataArray interpolateDataToIrregularMesh(float[] mesh, DataArray da) 
       {
       byte[]    outBData = null;
       short[]   outSData = null;
       int[]     outIData = null;
       float[]   outFData = null;
       double[]  outDData = null;
       DataArray outDA    = null;
       int vlen = da.getVeclen();
       if (nSpace!=3)
          return null;
       int nMeshNodes = mesh.length/nSpace;
       if (mesh.length != nSpace*nMeshNodes)
          return null;
       if (timeCoords != null && !timeCoords.isEmpty())
          throw new UnsupportedOperationException("Not supported yet.");
       switch (da.getType())
          {
             case DataArray.FIELD_DATA_BYTE:
                outBData = new byte[vlen*nMeshNodes];
                outDA = DataArray.create(outBData, vlen, da.getName());
                break;
             case DataArray.FIELD_DATA_SHORT:
                outSData = new short[vlen*nMeshNodes];
                outDA = DataArray.create(outSData, vlen, da.getName());
                break;
             case DataArray.FIELD_DATA_INT:
                outIData = new int[vlen*nMeshNodes];
                outDA = DataArray.create(outIData, vlen, da.getName());
                break;
             case DataArray.FIELD_DATA_FLOAT:
                outFData = new float[vlen*nMeshNodes];
                outDA = DataArray.create(outFData, vlen, da.getName());
                break;
             case DataArray.FIELD_DATA_DOUBLE:
                outDData = new double[vlen*nMeshNodes];
                outDA = DataArray.create(outDData, vlen, da.getName());
                break;
          }
      
          for (int i = 0, l=0; i < nMeshNodes; i++, l+=vlen)
          {
             float[] p = new float[3];
             float[] v = new float[3];
             for (int j = 0; j < 3; j++)
             {
                p[j] = mesh[3*i+j]-affine[3][j];
                v[j] = 0;
             }
             for (int j = 0; j < v.length; j++)
                for (int k = 0; k < v.length; k++)
                   v[j] += invAffine[j][k]*p[k];
             switch (da.getType())
             {
                case DataArray.FIELD_DATA_BYTE:
                   for (int j = 0; j < nMeshNodes; j++)
                   {
                      byte[] od = getInterpolatedData(da.getBData(), v[0], v[1], v[2]);
                      System.arraycopy(od, 0, outBData, l, vlen);
                   }
                break;
               case DataArray.FIELD_DATA_SHORT:
                   for (int j = 0; j < nMeshNodes; j++)
                   {
                      short[] od = getInterpolatedData(da.getSData(), v[0], v[1], v[2]);
                      System.arraycopy(od, 0, outSData, l, vlen);
                   }
                break;
               case DataArray.FIELD_DATA_INT:
                   for (int j = 0; j < nMeshNodes; j++)
                   {
                      int[] od = getInterpolatedData(da.getIData(), v[0], v[1], v[2]);
                      System.arraycopy(od, 0, outIData, l, vlen);
                   }
                break;
               case DataArray.FIELD_DATA_FLOAT:
                   for (int j = 0; j < nMeshNodes; j++)
                   {
                      float[] od = getInterpolatedData(da.getFData(), v[0], v[1], v[2]);
                      System.arraycopy(od, 0, outFData, l, vlen);
                   }
                break;
               case DataArray.FIELD_DATA_DOUBLE:
                   for (int j = 0; j < nMeshNodes; j++)
                   {
                      double[] od = getInterpolatedData(da.getDData(), v[0], v[1], v[2]);
                      System.arraycopy(od, 0, outDData, l, vlen);
                   }
                break;
             }
          }
       return outDA;
    }
   
    public DataArray interpolateDataToAffineMesh(int[] dims, float[][] meshAffine, DataArray da) 
       {
       byte[]    outBData = null;
       short[]   outSData = null;
       int[]     outIData = null;
       float[]   outFData = null;
       double[]  outDData = null;
       DataArray outDA    = null;
       int vlen = da.getVeclen();
       if (nSpace!=3)
          return null;
       int nMeshNodes = 1;
       for (int i = 0; i <dims.length; i++) 
           nMeshNodes *= dims[i];   
       if (timeCoords != null && !timeCoords.isEmpty())
          throw new UnsupportedOperationException("Not supported yet.");
       switch (da.getType())
          {
             case DataArray.FIELD_DATA_BYTE:
                outBData = new byte[vlen*nMeshNodes];
                outDA = DataArray.create(outBData, vlen, da.getName());
                break;
             case DataArray.FIELD_DATA_SHORT:
                outSData = new short[vlen*nMeshNodes];
                outDA = DataArray.create(outSData, vlen, da.getName());
                break;
             case DataArray.FIELD_DATA_INT:
                outIData = new int[vlen*nMeshNodes];
                outDA = DataArray.create(outIData, vlen, da.getName());
                break;
             case DataArray.FIELD_DATA_FLOAT:
                outFData = new float[vlen*nMeshNodes];
                outDA = DataArray.create(outFData, vlen, da.getName());
                break;
             case DataArray.FIELD_DATA_DOUBLE:
                outDData = new double[vlen*nMeshNodes];
                outDA = DataArray.create(outDData, vlen, da.getName());
                break;
          }
      
          for (int i = 0, l=0; i < nMeshNodes; i++, l+=vlen)
          {
             int i0,i1,i2;
             float[] p = new float[3];
             float[] v = new float[3];
             switch (dims.length)
             {
                case 1:
                   for (int j = 0; j < 3; j++)
                      p[j] = meshAffine[3][j]+i*meshAffine[0][j]-affine[3][j];
                   break;
                case 2:
                   i1 = i/dims[0];
                   i0 = i%dims[0];
                   for (int j = 0; j < 3; j++)
                      p[j] = meshAffine[3][j]+i0*meshAffine[0][j]+i1*meshAffine[1][j]-affine[3][j];
                   break;
                case 3:
                   i2 = i/(dims[1]*dims[0]);
                   i1 = (i/dims[0])%dims[1];
                   i0 = i%dims[0];
                   for (int j = 0; j < 3; j++)
                      p[j] = meshAffine[3][j]+i0*meshAffine[0][j]+i1*meshAffine[1][j]+i2*meshAffine[2][j]-affine[3][j];
                  break;
             }
             for (int j = 0; j < 3; j++)
                v[j] = 0;
             
             for (int j = 0; j < v.length; j++)
                for (int k = 0; k < v.length; k++)
                   v[j] += invAffine[j][k]*p[k];
             switch (da.getType())
             {
                case DataArray.FIELD_DATA_BYTE:
                   for (int j = 0; j < nMeshNodes; j++)
                   {
                      byte[] od = getInterpolatedData(da.getBData(), v[0], v[1], v[2]);
                      System.arraycopy(od, 0, outBData, l, vlen);
                   }
                break;
               case DataArray.FIELD_DATA_SHORT:
                   for (int j = 0; j < nMeshNodes; j++)
                   {
                      short[] od = getInterpolatedData(da.getSData(), v[0], v[1], v[2]);
                      System.arraycopy(od, 0, outSData, l, vlen);
                   }
                break;
               case DataArray.FIELD_DATA_INT:
                   for (int j = 0; j < nMeshNodes; j++)
                   {
                      int[] od = getInterpolatedData(da.getIData(), v[0], v[1], v[2]);
                      System.arraycopy(od, 0, outIData, l, vlen);
                   }
                break;
               case DataArray.FIELD_DATA_FLOAT:
                   for (int j = 0; j < nMeshNodes; j++)
                   {
                      float[] od = getInterpolatedData(da.getFData(), v[0], v[1], v[2]);
                      System.arraycopy(od, 0, outFData, l, vlen);
                   }
                break;
               case DataArray.FIELD_DATA_DOUBLE:
                   for (int j = 0; j < nMeshNodes; j++)
                   {
                      double[] od = getInterpolatedData(da.getDData(), v[0], v[1], v[2]);
                      System.arraycopy(od, 0, outDData, l, vlen);
                   }
                break;
             }
          }      
       outDA.recomputeMinMax();
       return outDA;
    }
   
    public DataArray interpolateDataToMesh(Field mesh, DataArray da)
    {
       if (mesh instanceof RegularField && ((RegularField)mesh).getCoords()==null)
       {
          RegularField fld = (RegularField)mesh;
          return interpolateDataToAffineMesh(fld.getDims(), fld.getAffine(), da);
       }
       else
          return interpolateDataToIrregularMesh(mesh.getCoords(), da);
    }

   @Override
   public float[] getInterpolatedData(float[] point, int index)
   {
      throw new UnsupportedOperationException("Not supported yet.");
   }
   
   public boolean isStructureCompatibleWith(Field f)
   {
      if (!(f instanceof RegularField)) return false;
      RegularField rf = (RegularField) f;
      if (rf.getDims().length != dims.length) return false;
      for (int i = 0; i < dims.length; i++)
         if (dims[i] != rf.getDims()[i]) return false;
      return true;
   }
   
   @Override
   public void checkPureDim()
   {
      trueDim = -1;
      switch (dims.length)
      {
         case 3:
            trueDim = 3;
            return;
         case 2:
            trueDim = 2;
            if (coords == null)
            {
               if (affine[1][2] != 0 || affine[0][2] != 0 || affine[3][2] != 0)
               trueDim = -1;
            }
            else
            {
               if (nSpace == 2)
                  return;
               for (int i = 0; i < nNodes; i++)
                  if (coords[3 * i + 2] != 0)
                  {
                     trueDim = -1;
                     return;
                  }
            }
            break;
         case 1:
            trueDim = 1;
            if (coords == null)
            {
               if (affine[0][1] != 0 || affine[0][2] != 0 || 
                   affine[3][1] != 0 || affine[3][2] != 0)
                  trueDim = -1;
            }
            else
            {
               switch (nSpace)
               {
               case 1:
                  return;
               case 2:
                  for (int i = 0; i < nNodes; i++)
                     if (coords[2 * i + 1] != 0)
                     {
                        trueDim = -1;
                        return;
                     }
                  break;
               case 3:
                  for (int i = 0; i < nNodes; i++)
                     if (coords[3 * i + 1] != 0 || coords[3 * i + 2] != 0)
                     {
                        trueDim = -1;
                        return;
                     }
                  break;
               }
            }
            break;
      }
   }

   @SuppressWarnings("ReturnOfCollectionOrArrayField")
   public float[] updateCoordsFromAffine()
   {
      float[] c = new float[3];
      float[] d = new float[3];
      if (coordsFromAffine == null)
      coordsFromAffine = new float[3 * nNodes];
      switch (dims.length)
      {
         case 3:
            for (int i = 0, l = 0; i < dims[2]; i++)
            {
               for (int j = 0; j < 3; j++)
                  c[j] = affine[3][j] + i * affine[2][j];
               for (int j = 0; j < dims[1]; j++)
               {
                  for (int k = 0; k < 3; k++)
                     d[k] = c[k] + j * affine[1][k];
                  for (int k = 0; k < dims[0]; k++)
                     for (int m = 0; m < 3; m++, l++)
                        coordsFromAffine[l] = d[m] + k * affine[0][m];
               }
            }
            break;
         case 2:
            for (int i = 0, l = 0; i < dims[1]; i++)
            {
               for (int j = 0; j < 3; j++)
                  c[j] = affine[3][j] + i * affine[1][j];
               for (int j = 0; j < dims[0]; j++)
                  for (int k = 0; k < 3; k++, l++)
                     coordsFromAffine[l] = c[k] + j * affine[0][k];
            }
            break;
         case 1:
            for (int i = 0, l = 0; i < dims[0]; i++)
               for (int k = 0; k < 3; k++, l++)
                   coordsFromAffine[l] = affine[3][k] + i * affine[0][k];
      }
      coordsHash = RabinHashFunction.hash(coordsFromAffine);
      return coordsFromAffine;
   }

   @SuppressWarnings("ReturnOfCollectionOrArrayField")
   public float[] getCoordsFromAffine()
   {
      if (coordsFromAffine != null)
         return coordsFromAffine;
      return updateCoordsFromAffine();
   }
//   
//   public void computeNormals()
//   {
//      boolean newNormals = false;
//      if (dims.length != 2)
//         return;
//      if (getData("normals") != null && getData("normals").getFData() != null && getData("normals").getFData().length == 3 * nNodes)
//         normals = getData("normals").getFData();
//      else
//      {
//         float[] normals = new float[3 * nNodes];
//         newNormals = true;
//      }
//      if (timeCoords == null || timeCoords.isEmpty())
//      {
//         float[] h = new float[3];
//         h[0] = affine[0][1] * affine[1][2] - affine[0][2] * affine[1][1];
//         h[1] = affine[0][2] * affine[1][0] - affine[0][0] * affine[1][2];
//         h[2] = affine[0][0] * affine[1][1] - affine[0][1] * affine[1][0];
//         float r = (float) (Math.sqrt(h[0] * h[0] + h[1] * h[1] + h[2] * h[2]));
//         for (int i = 0; i < h.length; i++)
//            h[i] /= r;
//         for (int i = 0, k = 0; i < nNodes; i++)
//            for (int j = 0; j < h.length; j++, k++)
//               normals[k] = h[j];
//      } else if (nSpace == 2)
//         for (int i = 0; i < nNodes; i++)
//         {
//            normals[3*i] = normals[3*i+1] = 0;
//            normals[3*i+2] = 1;
//         }
//      else
//      {
//         float[] c = timeCoords.get(currentFrame);
//         float[] u = new float[3];
//         float[] v = new float[3];
//         float[] h = new float[3];
//         int n = 3 * dims[0];
//         for (int i = 0, k = 0; i < dims[1]; i++)
//         {
//            for (int j = 0; j < dims[0]; j++, k += 3)
//            {
//               if (i == 0)
//                  for (int l = 0; l < 3; l++)
//                     v[l] = c[k + n + l] - c[k + l];
//               else if (i == dims[1] - 1)
//                  for (int l = 0; l < 3; l++)
//                     v[l] = c[k + l] - c[k - n + l];
//               else
//                  for (int l = 0; l < 3; l++)
//                     v[l] = c[k + n + l] - c[k - n + l];
//               if (j == 0)
//                  for (int l = 0; l < 3; l++)
//                     u[l] = c[k + 3 + l] - c[k + l];
//               else if (j == dims[0] - 1)
//                  for (int l = 0; l < 3; l++)
//                     u[l] = c[k + l] - c[k - 3 + l];
//               else
//                  for (int l = 0; l < 3; l++)
//                     u[l] = c[k + 3 + l] - c[k - 3 + l];
//               h[0] = u[1] * v[2] - u[2] * v[1];
//               h[1] = u[2] * v[0] - u[0] * v[2];
//               h[2] = u[0] * v[1] - u[1] * v[0];
//               float r = (float) (Math.sqrt(h[0] * h[0] + h[1] * h[1] + h[2] * h[2]));
//               for (int l = 0; l < 3; l++)
//                  normals[k + l] = h[l] / r;
//            }
//         }
//      }
//      if (newNormals)
//         addData(DataArray.create(normals, 3, "normals"));
//   }

   @Override
   @SuppressWarnings("ReturnOfCollectionOrArrayField")
   public float[] getNormals()
   {
      if (dims.length != 2)
         return null;
      float[] normals = new float[3 * nNodes];
      if (timeCoords == null || timeCoords.isEmpty())
      {
         float[] h = new float[3];
         h[0] = affine[0][1] * affine[1][2] - affine[0][2] * affine[1][1];
         h[1] = affine[0][2] * affine[1][0] - affine[0][0] * affine[1][2];
         h[2] = affine[0][0] * affine[1][1] - affine[0][1] * affine[1][0];
         float r = (float) (Math.sqrt(h[0] * h[0] + h[1] * h[1] + h[2] * h[2]));
         for (int i = 0; i < h.length; i++)
            h[i] /= r;
         for (int i = 0, k = 0; i < nNodes; i++)
            for (int j = 0; j < h.length; j++, k++)
               normals[k] = h[j];
      } else if (nSpace == 2)
         for (int i = 0; i < nNodes; i++)
         {
            normals[3*i] = normals[3*i+1] = 0;
            normals[3*i+2] = 1;
         }
      else
      {
         float[] c = timeCoords.get(currentFrame);
         float[] u = new float[3];
         float[] v = new float[3];
         float[] h = new float[3];
         int n = 3 * dims[0];
         for (int i = 0, k = 0; i < dims[1]; i++)
         {
            for (int j = 0; j < dims[0]; j++, k += 3)
            {
               if (i == 0)
                  for (int l = 0; l < 3; l++)
                     v[l] = c[k + n + l] - c[k + l];
               else if (i == dims[1] - 1)
                  for (int l = 0; l < 3; l++)
                     v[l] = c[k + l] - c[k - n + l];
               else
                  for (int l = 0; l < 3; l++)
                     v[l] = c[k + n + l] - c[k - n + l];
               if (j == 0)
                  for (int l = 0; l < 3; l++)
                     u[l] = c[k + 3 + l] - c[k + l];
               else if (j == dims[0] - 1)
                  for (int l = 0; l < 3; l++)
                     u[l] = c[k + l] - c[k - 3 + l];
               else
                  for (int l = 0; l < 3; l++)
                     u[l] = c[k + 3 + l] - c[k - 3 + l];
               h[0] = u[1] * v[2] - u[2] * v[1];
               h[1] = u[2] * v[0] - u[0] * v[2];
               h[2] = u[0] * v[1] - u[1] * v[0];
               float r = (float) (Math.sqrt(h[0] * h[0] + h[1] * h[1] + h[2] * h[2]));
               for (int l = 0; l < 3; l++)
                  normals[k + l] = h[l] / r;
            }
         }
      }
      return normals;
   }
   
   public void setNormals(float[] normals)
   {
      
   }

   public int[] getIndices(float x, float y, float z)
   {
      if (dims == null)
         return null;
      int[] ind = new int[3];
      if (dims.length == 3)
      {
         float[] p = getFloatIndices(x, y, z);
         for (int i = 0; i < 3; i++)
            ind[i] = Math.round(p[i]);
      } 
      else if ((timeCoords == null || timeCoords.isEmpty()) && dims.length == 2)
      {
         float[] v = {x, y};
         float[] p = new float[2];
         for (int i = 0; i < 2; i++)
            p[i] = v[i] - affine[3][i];
         float[] det = new float[3];
         det[2] = affine[0][0] * affine[1][1] - affine[1][0] * affine[0][1];
         det[0] = p[0] * affine[1][1] - affine[1][0] * p[1];
         det[1] = affine[0][0] * p[1]-  p[0] * affine[0][1];
         ind[0] = Math.round(det[0] / det[2]);
         ind[1] = Math.round(det[1] / det[2]);
         ind[2] = 0;
      }
      for (int i = 0; i < dims.length; i++)
      {
         if (ind[i] < 0)        ind[i] = 0;
         if (ind[i] >= dims[i]) ind[i] = dims[i] - 1;
      }
      return ind;
   }
   
   public int[] getIndices(float x, float y)
   {
      if (dims == null)
         return null;
      int[] ind = new int[3];
      if (dims.length == 3)
      {
         float[] p = getFloatIndices(x, y);
         for (int i = 0; i < 3; i++)
            ind[i] = Math.round(p[i]);
      } 
      else if ((timeCoords == null || timeCoords.isEmpty()) && dims.length == 2)
      {
         float[] v = {x, y};
         float[] p = new float[2];
         for (int i = 0; i < 2; i++)
            p[i] = v[i] - affine[3][i];
         float[] det = new float[3];
         det[2] = affine[0][0] * affine[1][1] - affine[1][0] * affine[0][1];
         det[0] = p[0] * affine[1][1] - affine[1][0] * p[1];
         det[1] = affine[0][0] * p[1]-  p[0] * affine[0][1];
         ind[0] = Math.round(det[0] / det[2]);
         ind[1] = Math.round(det[1] / det[2]);
         ind[2] = 0;
      }
      for (int i = 0; i < dims.length; i++)
      {
         if (ind[i] < 0)        ind[i] = 0;
         if (ind[i] >= dims[i]) ind[i] = dims[i] - 1;
      }
      return ind;
   }

   public float[] getFloatIndices(float x, float y, float z)
   {
      if (dims == null || dims.length != 3)
         return null;

      float[] ind = {0, 0, 0};
      if (timeCoords == null || timeCoords.isEmpty())
      {
         float[] p = new float[] {x - affine[3][0], y - affine[3][1], z - affine[3][2]} ;

         for (int i = 0; i < 3; i++)
         {
            ind[i] = 0;
            for (int j = 0; j < 3; j++)
               ind[i] += invAffine[j][i] * p[j];
         }
      } else
      {
         if (geoTree == null) 
            createGeoTree();
         SimplexPosition tCoords = getFieldCoords(new float[] {x,y,z});
         if (tCoords == null)
            return new float[]{-1,-1,-1};
         for (int m = 0; m < 4; m++)
         {
            int s = tCoords.verts[m];
            float t = tCoords.coords[m];
            int i0 = s % dims[0];
            int i1 = s / dims[0];
            int i2 = i1 / dims[1];
            i1 %= dims[1];
            ind[0] += t * i0;
            ind[1] += t * i1;
            ind[2] += t * i2;
         }
      }
      return ind;
   }

   public float[] getFloatIndices(float x, float y)
   {
      if (dims == null || dims.length != 2)
         return null;

      float[] ind = {0, 0};
      if (timeCoords == null || timeCoords.isEmpty())
      {
         float[] p = new float[] {x - affine[3][0], y - affine[3][1]};
         for (int i = 0; i < 2; i++)
            for (int j = 0; j < 2; j++)
               ind[i] += invAffine[i][j] * p[j];
      } else
      {
         if (geoTree == null) 
            createGeoTree();
         SimplexPosition tCoords = getFieldCoords(new float[] {x,y});
         if (tCoords == null)
            return new float[]{-1,-1,-1};
         for (int m = 0; m < 4; m++)
         {
            int s = tCoords.verts[m];
            float t = tCoords.coords[m];
            int i0 = s % dims[0];
            int i1 = s / dims[0];
            int i2 = i1 / dims[1];
            i1 %= dims[1];
            ind[0] += t * i0;
            ind[1] += t * i1;
            ind[2] += t * i2;
         }
      }
      return ind;
   }

   public void createGeoTree()
   {
      float[] c = timeCoords.get(currentFrame);
      
      if (dims.length != 3)
         return;
      float[] cellLow = new float[3];
      float[] cellUp  = new float[3];
      int nCells = 1;
      for (int i = 0; i < 3; i++)
      {
         cellExtentsDown[i] = (dims[i] + MAXCELLDIM - 1) / MAXCELLDIM;     
         cellExtentsDims[i] = (dims[i] + cellExtentsDown[i] - 2) /  cellExtentsDown[i]; // check
         nCells *= cellExtentsDims[i];
      }
      int[] cells = new int[nCells];
      cellExtents = new float[6][nCells];  
      for (int i = 0, l = 0; i < cellExtentsDims[2]; i++)
         for (int j = 0; j < cellExtentsDims[1]; j++)
            for (int k = 0; k < cellExtentsDims[0]; k++, l++)
            {
               cells[l] = l;
               int i0 = i  * cellExtentsDown[2];
               int i1 = i0 + cellExtentsDown[2] + 1; if (i1 > dims[2]) i1 = dims[2];
               int j0 = j  * cellExtentsDown[1];
               int j1 = j0 + cellExtentsDown[1] + 1; if (j1 > dims[1]) j1 = dims[1];
               int k0 = k  * cellExtentsDown[0];
               int k1 = k0 + cellExtentsDown[0] + 1; if (k1 > dims[0]) k1 = dims[0];
               for (int m = 0; m < 3; m++)
               {
                  cellLow[m] =  Float.MAX_VALUE;
                  cellUp[m]  = -Float.MAX_VALUE;
               }
               for (int ii = i0; ii < i1; ii++)
                  for (int jj = j0; jj < j1; jj++)  
                     for (int kk = k0; kk < k1; kk++)
                     {
                        int m = 3 * ((ii * dims[1] + jj) * dims[0] + kk);
                        for (int n = 0; n < 3; n++)
                        {
                           if (c[m + n] > cellUp[n])  cellUp[n]  = c[m + n];
                           if (c[m + n] < cellLow[n]) cellLow[n] = c[m + n];
                        }
                     }
               for (int m = 0; m < 3; m++)
               {
                  cellExtents[m][l]     = cellLow[m];
                  cellExtents[m + 3][l] = cellUp[m];
               }
            }
      geoTree =  new GeoTreeNode(cells, cellExtents);
      geoTree.splitFully();
   }

   private RegularHex[] getCells(int n)
   {
      int n0 = n  % cellExtentsDims[0];
      int k0 = n0 * cellExtentsDown[0];
      int k1 = k0 + cellExtentsDown[0]; if (k1 > dims[0]) k1 = dims[0];
      int n1 = (n / cellExtentsDims[0]) % cellExtentsDims[1];
      int j0 = n1 * cellExtentsDown[1];
      int j1 = j0 + cellExtentsDown[1]; if (j1 > dims[1]) j1 = dims[1];
      int n2 = n / (cellExtentsDims[0] * cellExtentsDims[1]);
      int i0 = n2 * cellExtentsDown[2];
      int i1 = i0 + cellExtentsDown[2]; if (i1 > dims[2]) i1 = dims[2];
      RegularHex[] cells = new RegularHex[(i1 - i0) * (j1 - j0) * (k1 - k0)];
      for (int ii = i0, i = 0; ii < i1; ii++)
         for (int jj = j0; jj < j1; jj++)  
            for (int kk = k0; kk < k1; kk++, i++)
            {
               int m = (ii * dims[1] + jj) * dims[0] + kk;
               cells[i] = new RegularHex(3, m,                      m + cellNodeOffsets[1], m + cellNodeOffsets[2], m + cellNodeOffsets[3], 
                                            m + cellNodeOffsets[4], m + cellNodeOffsets[5], m + cellNodeOffsets[6], m + cellNodeOffsets[7],
                                            true, ((ii + jj + kk) % 2) == 0);
            }
      return cells;
   }
   
   public SimplexPosition getFieldCoords(float[] p, int[] cells)
   {
      if (p == null || p.length != 3)
         return null;
      float[] c = timeCoords.get(currentFrame);
cellsLoop:
      for (int i = 0; i < cells.length; i++)
      {
         int cl = cells[i];
         for (int j = 0; j < 3; j++)
         {
            if (cellExtents[j][cl]     > p[j]) continue cellsLoop;
            if (cellExtents[j + 3][cl] < p[j]) continue cellsLoop;
         }
         RegularHex[] boxes = getCells(cl);
         float[] cellLow = new float[3];
         float[] cellUp  = new float[3];
boxesLoop:         
         for (int j = 0; j < boxes.length; j++)
         {
            for (int m = 0; m < 3; m++)
            {
               cellLow[m] =  Float.MAX_VALUE;
               cellUp[m]  = -Float.MAX_VALUE;
            }
            RegularHex cell = boxes[j];
            int[] verts = cell.getVertices();
            for (int k = 0; k < verts.length; k++)
            {
               int l = verts[k];
               for (int n = 0; n < 3; n++)
               {
                  if (c[3 * l + n] > cellUp[n])  cellUp[n]  = c[3 * l + n];
                  if (c[3 * l + n] < cellLow[n]) cellLow[n] = c[3 * l + n];
               }
            }
            for (int n = 0; n < 3; n++)
            {
               if (cellLow[n] > p[n]) continue boxesLoop;
               if (cellUp[n] < p[n])  continue boxesLoop;
            }
            Cell[] tets = cell.triangulation();
            for (int k = 0; k < tets.length; k++)
            {
                SimplexPosition result = ((Tetra)tets[k]).barycentricCoords(p, c);
                if (result != null)
                {
                   result.cell = cell;
                   result.cells = cells;
                   return result;
                }
            }
         }
      }
      return null;
   }  

   public SimplexPosition getFieldCoords(float[] p)
   {
      return getFieldCoords(p, geoTree.getCells(p));
   }

   public boolean getFieldCoords(float[] p, SimplexPosition result)
   {
      float[] c = timeCoords.get(currentFrame);
      Cell[] tets;
      float[] res;
      res = bCoords((Tetra)result.simplex, p);
      if (res != null)
      {
         result.verts = result.simplex.getVertices();
         result.coords = res;
         return true;
      }
      if (result.cell != null && result.cell.getType() != Cell.TETRA)
      {
         tets = ((RegularHex)(result.cell)).triangulation();
         for (int j = 0; j < tets.length; j++)
         {
            if (tets[j].getType() == Cell.TETRA)
            {
               res = bCoords((Tetra)tets[j], p);
               if (res != null)
               {
                  result.simplex = tets[j];
                  result.verts = result.simplex.getVertices();
                  result.coords = res;
                  return true;
               }
            }
         }
      }
      if (result.cells != null)
      {
cLoop:
         for (int i = 0; i < result.cells.length; i++)
         {
            int cl = result.cells[i];
            for (int j = 0; j < 3; j++)
            {
               if (cellExtents[j][cl]     > p[j]) continue cLoop;
               if (cellExtents[j + 3][cl] < p[j]) continue cLoop;
            }
            RegularHex[] boxes = getCells(cl);
            float[] cellLow = new float[3];
            float[] cellUp  = new float[3];
   boxesLoop:         
            for (int j = 0; j < boxes.length; j++)
            {
               for (int m = 0; m < 3; m++)
               {
                  cellLow[m] =  Float.MAX_VALUE;
                  cellUp[m]  = -Float.MAX_VALUE;
               }
               RegularHex cell = boxes[j];
               int[] verts = cell.getVertices();
               for (int k = 0; k < verts.length; k++)
               {
                  int l = verts[k];
                  for (int n = 0; n < 3; n++)
                  {
                     if (c[3 * l + n] > cellUp[n])  cellUp[n]  = c[3 * l + n];
                     if (c[3 * l + n] < cellLow[n]) cellLow[n] = c[3 * l + n];
                  }
               }
               for (int n = 0; n < 3; n++)
               {
                  if (cellLow[n] > p[n]) continue boxesLoop;
                  if (cellUp[n] < p[n])  continue boxesLoop;
               }
              tets = cell.triangulation();
               for (int k = 0; k < tets.length; k++)
               {
                   res = bCoords((Tetra)tets[k], p);
                   if (res != null)
                  {
                     result.simplex = tets[j];
                     result.verts = result.simplex.getVertices();
                     result.coords = res;
                     result.cell = cell;
                     return true;
                  }
               }
            }
         }
      }
      int[] cells = geoTree.getCells(p);
cLoop:
      for (int i = 0; i < result.cells.length; i++)
      {
         int cl = result.cells[i];
         for (int j = 0; j < 3; j++)
         {
            if (cellExtents[j][cl]     > p[j]) continue cLoop;
            if (cellExtents[j + 3][cl] < p[j]) continue cLoop;
         }
         RegularHex[] boxes = getCells(cl);
         float[] cellLow = new float[3];
         float[] cellUp  = new float[3];
boxesLoop:         
         for (int j = 0; j < boxes.length; j++)
         {
            for (int m = 0; m < 3; m++)
            {
               cellLow[m] =  Float.MAX_VALUE;
               cellUp[m]  = -Float.MAX_VALUE;
            }
            RegularHex cell = boxes[j];
            int[] verts = cell.getVertices();
            for (int k = 0; k < verts.length; k++)
            {
               int l = verts[k];
               for (int n = 0; n < 3; n++)
               {
                  if (c[3 * l + n] > cellUp[n])  cellUp[n]  = c[3 * l + n];
                  if (c[3 * l + n] < cellLow[n]) cellLow[n] = c[3 * l + n];
               }
            }
            for (int n = 0; n < 3; n++)
            {
               if (cellLow[n] > p[n]) continue boxesLoop;
               if (cellUp[n] < p[n])  continue boxesLoop;
            }
           tets = cell.triangulation();
            for (int k = 0; k < tets.length; k++)
            {
                res = bCoords((Tetra)tets[k], p);
                if (res != null)
               {
                  result.simplex = tets[j];
                  result.verts = result.simplex.getVertices();
                  result.coords = res;
                  result.cell = cell;
                  return true;
               }
            }
         }
      }
      return false;
   }
      
   public int[] getTetras(int i)
   {
       if (dims.length != 3 || i < 0 || i >= (dims[0] -1) * (dims[1] -1) * (dims[2] -1))
           return null;
       int off1 = dims[0] - 1;
       int off2 = (dims[0] - 1) * (dims[1] - 1);
       int i0 = i % off1;
       int i1 = (i / off1) % (dims[1] - 1);
       int i2 = i / off2;
       int l = ((i2 * dims[1]) + i1) * dims[0] + i0;
       off1 = dims[0];
       off2 = dims[0]* dims[1];
       return RegularCellsTriangulation.triangulateRegularHex(
                          l,        l        + 1, l        + off1 + 1, l        + off1, 
                          l + off2, l + off2 + 1, l + off2 + off1 + 1, l + off2 + off1, 
                          (i0 + i1 + i2) % 2 == 0);
   }
      
   public int[] getTriangles(int i)
   {
       if (dims.length != 2 || i < 0 || i >= (dims[0] -1) * (dims[1] -1))
           return null;
       int off = dims[0] - 1;
       int i0 = i % off;
       int i1 =  i / off;
       int l = i1 * dims[0] + i0;
       return RegularCellsTriangulation.triangulateRegularQuad(
                          l, l + 1, l + dims[0] + 1, l + dims[0]);
   }
      
   public IrregularField triangulate()
   {
      IrregularField outField = new IrregularField();
      outField.setNNodes(nNodes);
      if (timeCoords != null && !timeCoords.isEmpty())
         outField.setCoords(timeCoords);
      else
         outField.setCoords(getCoordsFromAffine());
      if (mask != null)
         outField.setMask(mask);
      CellArray ca = null;
      if (dims.length == 3)
      {
         int off1 = dims[0];
         int off2 = dims[0] * dims[1];
         int[] cellNodes = new int[20 * (dims[0] - 1) * (dims[1] - 1) * (dims[2] - 1)];
         for (int i = 0, m = 0; i < dims[2] - 1; i++)
            for (int j = 0; j < dims[1] - 1; j++)
               for (int k = 0, l = i * off2 + j * off1; k < dims[0] - 1; k++, l++, m += 20)
                  System.arraycopy(RegularCellsTriangulation.triangulateRegularHex(
                          l,        l        + 1, l        + off1 + 1, l        + off1, 
                          l + off2, l + off2 + 1, l + off2 + off1 + 1, l + off2 + off1, 
                          (i + j + k) % 2 == 0), 0, cellNodes, m, 20);
         boolean[] orient = new boolean[5 * (dims[0] - 1) * (dims[1] - 1) * (dims[2] - 1)];
         for (int i = 0; i < orient.length; i++)
            orient[i] = true;
         ca = new CellArray(Cell.TETRA, cellNodes, orient, null);
      }
      else if  (dims.length == 2)
      {
         int off1 = dims[0];
         int[] cellNodes = new int[6 * (dims[0] - 1) * (dims[1] - 1)];
            for (int j = 0, m = 0; j < dims[1] - 1; j++)
               for (int k = 0, l = j * off1; k < dims[0] - 1; k++, l++, m += 6)
                  System.arraycopy(RegularCellsTriangulation.triangulateRegularQuad(
                          l, l + 1, l + off1 + 1, l + off1), 0, cellNodes, m, 6);
         boolean[] orient = new boolean[2 * (dims[0] - 1) * (dims[1] - 1)];
         for (int i = 0; i < orient.length; i++)
            orient[i] = true;
         ca = new CellArray(Cell.TRIANGLE, cellNodes, orient, null);
      }
         CellSet cs = new CellSet();
         cs.setCellArray(ca);
         cs.generateDisplayData(outField.getCoords());
         outField.addCellSet(cs);
      for (DataArray dataArray : data)
         outField.addData(dataArray);
      outField.setExtents(extents);
      return outField;
   }
   
}


