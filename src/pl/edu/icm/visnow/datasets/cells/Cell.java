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

package pl.edu.icm.visnow.datasets.cells;

/**
 * 
 * @author Krzysztof S. Nowinski
 * University of Warsaw, ICM
 */
abstract public class Cell
{
   public static final int TYPES = 8;
   public static final int TYPES2D = 4;
   public static final int TYPES1D = 2;
   public static final int TYPES0D = 1;
   public static final int POINT = 0;
   public static final int SEGMENT = 1;
   public static final int TRIANGLE = 2;
   public static final int QUAD = 3;
   public static final int TETRA = 4;
   public static final int PYRAMID = 5;
   public static final int PRISM = 6;
   public static final int HEXAHEDRON = 7;
   public static final int ADDED_INTERN = 0;
   public static final int ADDED_FACE = 1;
   public static final int ADDED_EXTERN = 2;
   public static final int FACE = 4;
   public static final int FACE_EXTERN = 5;
   public static final int EXPLICIT = 8;
   public static final String[] UCDnames = {"pt", "line", "tri", "quad", "tet", "pyr", "prism", "hex"};
   public static final int[] nv = {1, 2, 3, 4, 4, 5, 6, 8};
   public static final int[] dim = {0, 1, 2, 2, 3, 3, 3, 3};
   public static final boolean[] isSimplexType = {true, true, true, false, true, false, false, false};
   public static final int[][] faceTypes =
     {{0,0,0,0,0,0,0,0},{2,0,0,0,0,0,0,0},{0,3,0,0,0,0,0,0},{0,4,0,0,0,0,0,0},
      {0,0,4,0,0,0,0,0},{0,0,4,1,0,0,0,0},{0,0,2,3,0,0,0,0},{0,0,0,6,0,0,0,0}};
   public static final int orientingVerts[][] = 
     {{0},{0},{0},{0},{0,1,2,3},{0,1,3,4},{0,1,2,3},{0,1,3,4}};
   public static final int[][] UCDnodeOrders =
   {{0}, {0, 1}, {0, 1, 2}, {0, 1, 2, 3}, {0, 1, 2, 3}, {4, 0, 3, 2, 1},
    {0, 1, 2, 3, 4, 5}, {0, 1, 2, 3, 4, 5, 6, 7}};
   
   protected int type;
   protected int status;
   protected int nspace;
   protected int[] vertices;
   protected boolean orientation;

   public static Cell createCell(int type, int nspace, int[] vertices, boolean orientation)
   {
      if (vertices.length != nv[type] || nspace < dim[type])
         return null;
      switch (type)
      {
         case POINT:
            return new Point(nspace, vertices[0], orientation);
         case SEGMENT:
            return new Segment(nspace, vertices[0], vertices[1], orientation);
         case TRIANGLE:
            return new Triangle(nspace, vertices[0], vertices[1], vertices[2], orientation);
         case QUAD:
            return new Quad(nspace, vertices[0], vertices[1], vertices[2], vertices[3], orientation);
         case TETRA:
            return new Tetra(nspace, vertices[0], vertices[1], vertices[2], vertices[3], orientation);
         case PYRAMID:
            return new Pyramid(nspace, vertices[0], vertices[1], vertices[2], vertices[3], vertices[4], orientation);
         case PRISM:
            return new Prism(nspace, vertices[0], vertices[1], vertices[2], vertices[3], vertices[4], vertices[5], orientation);
         case HEXAHEDRON:
            return new Hex(nspace, vertices[0], vertices[1], vertices[2], vertices[3], vertices[4], vertices[5], vertices[6], vertices[7], orientation);
         default:
            return null;
      }
   }

   public static Cell createCell(int type, int[] vertices, boolean orientation)
   {
      if (vertices.length != nv[type])
         return null;
      int nspace = dim[type];
      switch (type)
      {
         case POINT:
            return new Point(nspace, vertices[0], orientation);
         case SEGMENT:
            return new Segment(nspace, vertices[0], vertices[1], orientation);
         case TRIANGLE:
            return new Triangle(nspace, vertices[0], vertices[1], vertices[2], orientation);
         case QUAD:
            return new Quad(nspace, vertices[0], vertices[1], vertices[2], vertices[3], orientation);
         case TETRA:
            return new Tetra(nspace, vertices[0], vertices[1], vertices[2], vertices[3], orientation);
         case PYRAMID:
            return new Pyramid(nspace, vertices[0], vertices[1], vertices[2], vertices[3], vertices[4], orientation);
         case PRISM:
            return new Prism(nspace, vertices[0], vertices[1], vertices[2], vertices[3], vertices[4], vertices[5], orientation);
         case HEXAHEDRON:
            return new Hex(nspace, vertices[0], vertices[1], vertices[2], vertices[3], vertices[4], vertices[5], vertices[6], vertices[7], orientation);
         default:
            return null;
      }
   }

   public int getType()
   {
      return type;
   }
   
   abstract public boolean isSimplex();

   public int getDim()
   {
      return dim[type];
   }

   public int getStatus()
   {
      return status;
   }

   public void setStatus(int status)
   {
      this.status = status;
   }

   public int getNspace()
   {
      return nspace;
   }

   public int getNverts()
   {
      return nv[type];
   }

   public int[] getVertices()
   {
      return vertices;
   }

   public boolean isOrientation()
   {
      return orientation;
   }

   public void setOrientation(boolean orientation)
   {
      this.orientation = orientation;
   }

   @Override
   public String toString()
   {
      StringBuffer b = new StringBuffer();
      b.append("[");
      for (int i = 0; i < vertices.length; i++)
         b.append(String.format("%5d ", vertices[i]));
      b.append("]");
      if (orientation)
         b.append(" +");
      else
         b.append(" -");
      return new String(b);
   }

   /**
    * Creates complete list of subcells (faces, edges, vertices)
    * @return array of length (dim+1) of arrays of cells
    * (Cells[i] is array of subcells of dimension i)
    */
   abstract public Cell[][] subcells();

   /**
    * Creates array of cell faces
    * @return array faces
    */
   abstract public Cell[] faces();

   /**
    * Creates array of cell faces for a given set of node indices and orientation
    * @return array faces
    */
   abstract public Cell[] faces(int[] nodes, boolean orientation);

   /**
    * Divides into array of simplices
    * @return array of simplices of triangulation
    */
   abstract public Cell[] triangulation();

   
   /**
    * Divides into array of simplices
    * @return array of simplices of triangulation
    */
   abstract public int[][] triangulationVertices();

   /**
    * reorders vertices to increasing order (as long as possible) modifying orientation if necessary
    * @return
    * array of reordered vertices
    */
   public int[] normalize()
   {
      if (!normalize(vertices))
         orientation = !orientation;
      return vertices;
   }

   /**
    * A convenience function reordering nodes array to increasing order (as long as possible)
    * @return
    * true if orientation has been preserved, false otherwise
    */
   abstract public boolean normalize(int[] nodes);

   /**
    * returns a byte describing compatibility with x:
    * <p>
    * @param x compared cell
    * @return
    * <p> 0 if x is of different dimension
    * <p> 0 if x has no common face
    * <p> 1 if x has the same type, vertices and orientation
    * <p>-1 if x has the same type and vertices but opposite orientation
    * <p> 2 if x has common face and compatible orientation
    * <p>-2 if x has common face but opposite orientation
    * <p> 3 if x is a compatibly oriented face of this
    * <p>-3 if x is a oppositely oriented face of this
    * <p> 4 if this is a compatibly oriented face of x
    * <p>-4 if this is a oppositely oriented face of x
    */
   public byte compare(Cell x)
   {
      Cell[] f;
      Cell[] xf;
      if (x.getDim() == this.getDim())
      {
         if (x.getNverts() == this.getNverts())
         {
            int[] v = x.getVertices();
            boolean eq = true;
            for (int i = 0; i < v.length; i++)
               if (v[i] != vertices[i])
               {
                  eq = false;
                  break;
               }
            if (eq && (x.isOrientation() == orientation))
               return 1;
            else if (eq)
               return -1;
         }
         f = this.faces();
         xf = x.faces();
         if (f != null && xf != null)
            for (int i = 0; i < f.length; i++)
               for (int j = 0; j < xf.length; j++)
               {
                  if (f[i].compare(xf[j]) == 1)
                     return -2;
                  if (f[i].compare(xf[j]) == -1)
                     return 2;
               }
         return 0;
      }
      if (x.getDim() == this.getDim() - 1)
      {
         f = this.faces();
         if (f != null)
            for (int i = 0; i < f.length; i++)
            {
               if (f[i].compare(x) == 1)
                  return 3;
               if (f[i].compare(x) == -1)
                  return -3;
            }
         return 0;
      }
      if (x.getDim() == this.getDim() + 1)
      {
         xf = x.faces();
         if (xf != null)
            for (int i = 0; i < xf.length; i++)
            {
               if (xf[i].compare(this) == 1)
                  return 4;
               if (xf[i].compare(this) == -1)
                  return -4;
            }
         return 0;
      }
      return 0;
   }

   /**
    * returns a byte describing compatibility with x of a cell of the same type with vertices v:
    * <p>
    * @param v array of vertex numbers
    * @return
    * <p> 0 if length of v is different from number of vertices of this
    * <p> 0 if x has no common face
    * <p> 1 if x has the same type, vertices and orientation
    * <p>-1 if x has the same type and vertices but opposite orientation
    * <p>2 if x has common face and compatible orientation
    * <p>-2 if x has common face but opposite orientation
    */
   abstract public byte compare(int[] v);

   /**
    *
    * @param coords vertex coordinates
    * @param nodes list of nodes
    * @return orientation of the cell
    */
   public int geomOrientation(float[] coords, int[] nodes)
   {
      int[] orv = orientingVerts[type];
      if (orv.length < 4 || nodes.length < 4)
         return 0;
      int[] verts = new int[4];
      for (int i = 0; i < verts.length; i++)
         verts[i] = nodes[orv[i]];
      float[][] v = new float[nspace][nspace];
      float d;
      for (int i = 0; i < nspace; i++)
         for (int j = 0; j < nspace; j++)
            v[i][j] = coords[nspace * verts[i + 1] + j] - coords[nspace * verts[0] + j];
      if (nspace == 2)
         d = v[0][0] * v[1][1] - v[0][1] * v[1][0];
      else
         d = v[0][0] * v[1][1] * v[2][2] + v[0][1] * v[1][2] * v[2][0] + v[0][2] * v[1][0] * v[2][1]
           - v[0][1] * v[1][0] * v[2][2] - v[0][0] * v[1][2] * v[2][1] - v[0][2] * v[1][1] * v[2][0];
      return (int) (Math.signum(d));
   }
   
   /**
    *
    * @param coords vertex coordinates
    * @return orientation of the cell
    */
   public int geomOrientation(float[] coords)
   {
      int[] orv = orientingVerts[type];
      if (orv.length < 4 || vertices.length < 4)
         return 0;
      int[] verts = new int[4];
      for (int i = 0; i < verts.length; i++)
         verts[i] = vertices[orv[i]];
      float[][] v = new float[nspace][nspace];
      float d;
      for (int i = 0; i < nspace; i++)
         for (int j = 0; j < nspace; j++)
            v[i][j] = coords[nspace * verts[i + 1] + j] - coords[nspace * verts[0] + j];
      if (nspace == 2)
         d = v[0][0] * v[1][1] - v[0][1] * v[1][0];
      else
         d = v[0][0] * v[1][1] * v[2][2] + v[0][1] * v[1][2] * v[2][0] + v[0][2] * v[1][0] * v[2][1]
           - v[0][1] * v[1][0] * v[2][2] - v[0][0] * v[1][2] * v[2][1] - v[0][2] * v[1][1] * v[2][0];
      return (int) (Math.signum(d));
   }
   /**
    *
    * @param coords vertex coordinates
    * @return orientation of the cell
    */
   public static int geomOrientation(int type, int nspace, int geomspace, int[] vertices, float[] coords)
   {
      int[] orv = orientingVerts[type];
      if (orv.length < 4 || vertices.length < 4)
         return 0;
      int[] verts = new int[4];
      for (int i = 0; i < verts.length; i++)
         verts[i] = vertices[orv[i]];
      float[][] v = new float[geomspace][geomspace];
      float d;
      for (int i = 0; i < geomspace; i++)
         for (int j = 0; j < geomspace; j++)
            v[i][j] = coords[nspace * verts[i + 1] + j] - coords[nspace * verts[0] + j];
      if (geomspace == 2)
         d = v[0][0] * v[1][1] - v[0][1] * v[1][0];
      else
         d = v[0][0] * v[1][1] * v[2][2] + v[0][1] * v[1][2] * v[2][0] + v[0][2] * v[1][0] * v[2][1]
           - v[0][1] * v[1][0] * v[2][2] - v[0][0] * v[1][2] * v[2][1] - v[0][2] * v[1][1] * v[2][0];
      return (int) (Math.signum(d));
   }
   
   /**
    *
    * @param coords vertex coordinates
    * @param normal vector normal to the surface
    * @return orientation of the cell
    */
   public int geomOrientation(float[] coords, float[] normal)
   {
      if (vertices.length < 3)
         return 0;
      float[][] v = new float[nspace][nspace];
      float d;
      for (int i = 0; i < nspace -1; i++)
         for (int j = 0; j < nspace; j++)
            v[i][j] = coords[nspace * vertices[i + 1] + j] - coords[nspace * vertices[0] + j];
      if (normal != null)
         System.arraycopy(normal, 0, v[nspace -1], 0, nspace);
      if (nspace == 2)
         d = v[0][0] * v[1][1] - v[0][1] * v[1][0];
      else
         d = v[0][0] * v[1][1] * v[2][2] + v[0][1] * v[1][2] * v[2][0] + v[0][2] * v[1][0] * v[2][1]
           - v[0][1] * v[1][0] * v[2][2] - v[0][0] * v[1][2] * v[2][1] - v[0][2] * v[1][1] * v[2][0];
      return (int) (Math.signum(d));
   }
   
   /**
    * Utility subdividing a quad according to the convention of lowest vertex at the diagonal
    * @param quad numbers of four quad vertices (ordered as in Quad class)
    * @return array describing two triangles of quad triangulation
    */
   protected static int[][] diagonalSubdiv(int[] quad)
   {
      if (quad == null || quad.length != 4)
         return null;
      if ((quad[0] < quad[1] && quad[0] < quad[3])
              || (quad[2] < quad[1] && quad[2] < quad[3]))
         return new int[][] {{quad[0], quad[1], quad[2]}, {quad[0], quad[2], quad[3]}};
      else
         return new int[][] {{quad[1], quad[2], quad[3]}, {quad[1], quad[3], quad[0]}};
   }
   
   /**
    * Utility subdividing a quad according to the convention of lowest vertex at the diagonal
    * @param quad numbers of four quad vertices (ordered as in Quad class)
    * @return array describing two triangles of quad triangulation
    */
   protected static int[][] diagonalSubdivIndices(int[] quad)
   {
      if (quad == null || quad.length != 4)
         return null;
      if ((quad[0] < quad[1] && quad[0] < quad[3])
              || (quad[2] < quad[1] && quad[2] < quad[3]))
         return new int[][] {{0, 1, 2}, {0, 2, 3}};
      else
         return new int[][] {{1, 2, 3}, {1, 3, 0}};
   }
   
   public float getMeasure(float[] coords)
   {
      int i0, i1, i2, i3;
      float[] v1 = new float[3];
      float[] v2 = new float[3];
      float[] v3 = new float[3];
      float r;
      switch (type)
      {
         case POINT:
            return 1;
         case SEGMENT:
            r = 0;
            i1 = vertices[1];
            i0 = vertices[0];
            for (int i = 0; i < 3; i++)
               r += (coords[3 * i1 + i] - coords[3 * i0 + i]) *  (coords[3 * i1 + i] - coords[3 * i0 + i]);
            return (float)(Math.sqrt(r));
         case TRIANGLE:
            r = 0;
            i2 = vertices[2];
            i1 = vertices[1];
            i0 = vertices[0];
            for (int i = 0; i < 3; i++)
            {
               v1[i] = coords[3 * i1 + i] - coords[3 * i0 + i];
               v2[i] = coords[3 * i2 + i] - coords[3 * i0 + i];
            }
            v3[0] = v1[1] * v2[2] - v2[1] * v1[2];
            v3[1] = v1[2] * v2[0] - v2[2] * v1[0];
            v3[2] = v1[0] * v2[1] - v2[0] * v1[1];
            for (int i = 0; i < 3; i++)
               r += v3[i] * v3[i];
            return (float)(Math.sqrt(r)) / 2;
         case TETRA:
            r = 0;
            i3 = vertices[3];
            i2 = vertices[2];
            i1 = vertices[1];
            i0 = vertices[0];
            for (int i = 0; i < 3; i++)
            {
               v1[i] = coords[3 * i1 + i] - coords[3 * i0 + i];
               v2[i] = coords[3 * i2 + i] - coords[3 * i0 + i];
               v3[i] = coords[3 * i3 + i] - coords[3 * i0 + i];
            }
            r = v1[0] * v2[1] * v3[2] + v1[1] * v2[2] * v3[0] + v1[2] * v2[0] * v3[1] -
                (v1[2] * v2[1] * v3[0] + v1[0] * v2[2] * v3[1] + v1[1] * v2[0] * v3[2]); 
            return Math.abs(r) / 6;
         default:
            Cell[] triang = triangulation();
            r = 0;
            for (int i = 0; i < triang.length; i++)
               r += triang[i].getMeasure(coords);
            return r;
      }
   }
   
   public float getSignedMeasure(float[] coords)
   {
      int i0, i1, i2, i3;
      float[] v1 = new float[3];
      float[] v2 = new float[3];
      float[] v3 = new float[3];
      float r;
      switch (type)
      {
         case POINT:
            return 1;
         case SEGMENT:
            r = 0;
            i1 = vertices[1];
            i0 = vertices[0];
            for (int i = 0; i < 3; i++)
               r += (coords[3 * i1 + i] - coords[3 * i0 + i]) *  (coords[3 * i1 + i] - coords[3 * i0 + i]);
            return (float)(Math.sqrt(r));
         case TRIANGLE:
            r = 0;
            i2 = vertices[2];
            i1 = vertices[1];
            i0 = vertices[0];
            for (int i = 0; i < 3; i++)
            {
               v1[i] = coords[3 * i1 + i] - coords[3 * i0 + i];
               v2[i] = coords[3 * i2 + i] - coords[3 * i0 + i];
            }
            v3[0] = v1[1] * v2[2] - v2[1] * v1[2];
            v3[1] = v1[2] * v2[0] - v2[2] * v1[0];
            v3[2] = v1[0] * v2[1] - v2[0] * v1[1];
            for (int i = 0; i < 3; i++)
               r += v3[i] * v3[i];
            return (float)(Math.sqrt(r)) / 2;
         case TETRA:
            r = 0;
            i3 = vertices[3];
            i2 = vertices[2];
            i1 = vertices[1];
            i0 = vertices[0];
            for (int i = 0; i < 3; i++)
            {
               v1[i] = coords[3 * i1 + i] - coords[3 * i0 + i];
               v2[i] = coords[3 * i2 + i] - coords[3 * i0 + i];
               v3[i] = coords[3 * i3 + i] - coords[3 * i0 + i];
            }
            r = v1[0] * v2[1] * v3[2] + v1[1] * v2[2] * v3[0] + v1[2] * v2[0] * v3[1] -
                (v1[2] * v2[1] * v3[0] + v1[0] * v2[2] * v3[1] + v1[1] * v2[0] * v3[2]); 
            float sign = this.orientation ? 1 : -1;
			return sign * r / 6;
         default:
            Cell[] triang = triangulation();
            r = 0;
            for (int i = 0; i < triang.length; i++)
               r += triang[i].getSignedMeasure(coords);
            return r;
      }
   }
   
}
