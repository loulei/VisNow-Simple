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

import pl.edu.icm.visnow.lib.utils.numeric.NumericalMethods;

/**
 *
 * @author  Krzysztof S. Nowinski,
 * University of Warsaw, ICM
 */
public class Tetra extends Cell
{
   public Tetra(int nsp, int i0, int i1, int i2, int i3, boolean orientation)
   {
      type = Cell.TETRA;
      nspace = nsp;
      vertices = new int[4];
      vertices[0] = i0;
      vertices[1] = i1;
      vertices[2] = i2;
      vertices[3] = i3;
      this.orientation = orientation;
      normalize();
   }
   
   public Tetra()
   {
   }
   
   public Tetra(int nsp, int i0, int i1, int i2, int i3)
   {
      this(nsp, i0, i1, i2, i3, true);
   }

   public boolean isSimplex()
   {
      return true;
   }
   
   @Override
   public Cell[][] subcells()
   {
      Cell[][] tr = new Cell[4][];
      tr[3] = triangulation();
      tr[2] = faces();
      Segment[] s = {new Segment(nspace, vertices[0], vertices[1]),
                     new Segment(nspace, vertices[0], vertices[2]),
                     new Segment(nspace, vertices[0], vertices[3]),
                     new Segment(nspace, vertices[1], vertices[2]),
                     new Segment(nspace, vertices[1], vertices[3]),
                     new Segment(nspace, vertices[2], vertices[3])};
       tr[1]  = s;
       tr[0]    = new Point[vertices.length];
       for (int i = 0; i < vertices.length; i++)
          tr[0][i] = new Point(nspace, vertices[i]);
       return tr;
   }

   @Override
   public Cell[] faces()
   {
       return faces(vertices, orientation);
   }

   @Override
   public Cell[] faces(int[] nodes, boolean orientation)
   {
       Triangle[] faces = {new Triangle(nspace, nodes[1], nodes[2], nodes[3], orientation),
                           new Triangle(nspace, nodes[0], nodes[2], nodes[3], !orientation),
                           new Triangle(nspace, nodes[0], nodes[1], nodes[3], orientation),
                           new Triangle(nspace, nodes[0], nodes[1], nodes[2], !orientation)};
       faces[0].setStatus(Cell.ADDED_FACE);
       faces[1].setStatus(Cell.ADDED_FACE);
       faces[2].setStatus(Cell.ADDED_FACE);
       faces[3].setStatus(Cell.ADDED_FACE);
       return faces;
   }

   @Override
   public Cell[] triangulation()
   {
      Tetra[] subdiv = {this};
      return subdiv;
   }

   @Override
   public int[][] triangulationVertices()
   {
      return new int[][]{vertices};
   }
   
   public static int[][] triangulationVertices(int[] vertices)
   {
      return new int[][]{vertices};
   }
     
   public static boolean normalize(int[] nodes, int offset)
   {
      int i = 0, t=0;
      for (int j = 1; j < 4; j++)
         if (nodes[offset+j]<nodes[offset+i])
            i = j;
      switch (i)
      {
         case 0:
            break;
         case 1:
            t = nodes[offset+0];
            nodes[offset+0] = nodes[offset+1];
            nodes[offset+1] = nodes[offset+2];
            nodes[offset+2] = t;
            break;
         case 2:
            t = nodes[offset+0];
            nodes[offset+0] = nodes[offset+2];
            nodes[offset+2] = nodes[offset+3];
            nodes[offset+3] = t;
            break;
         case 3:
            t = nodes[offset+0];
            nodes[offset+0] = nodes[offset+3];
            nodes[offset+3] = nodes[offset+1];
            nodes[offset+1] = t;
            break;
      }
      i = 1;
      for (int j = 2; j < 4; j++)
         if (nodes[offset+j]<nodes[offset+i])
            i = j;
      switch (i)
      {
         case 1:
            break;
         case 2:
            t = nodes[offset+1];
            nodes[offset+1] = nodes[offset+2];
            nodes[offset+2] = nodes[offset+3];
            nodes[offset+3] = t;
            break;
         case 3:
            t = nodes[offset+1];
            nodes[offset+1] = nodes[offset+3];
            nodes[offset+3] = nodes[offset+2];
            nodes[offset+2] = t;
            break;
      }
      if (nodes[offset+3]<nodes[offset+2])
      {
         t = nodes[offset+2];
         nodes[offset+2] = nodes[offset+3];
         nodes[offset+3] = t;
         return false;
      }
      return true;
   }
   
   @Override
   public boolean normalize(int[] nodes)
   {
      int i = 0, t=0;
      for (int j = 1; j < 4; j++)
         if (nodes[j]<nodes[i])
            i = j;
      switch (i)
      {
         case 0:
            break;
         case 1:            
            t = nodes[0]; 
            nodes[0] = nodes[1];
            nodes[1] = nodes[2];
            nodes[2] = t;
            break;
         case 2:
            t = nodes[0]; 
            nodes[0] = nodes[2];
            nodes[2] = nodes[3];
            nodes[3] = t;
            break;
         case 3:
            t = nodes[0]; 
            nodes[0] = nodes[3];
            nodes[3] = nodes[1];
            nodes[1] = t;
            break;
      }
      i = 1;
      for (int j = 2; j < 4; j++)
         if (nodes[j]<nodes[i])
            i = j;
      switch (i)
      {
         case 1:
            break;
         case 2:
            t = nodes[1]; 
            nodes[1] = nodes[2];
            nodes[2] = nodes[3];
            nodes[3] = t;
            break;
         case 3:
            t = nodes[1]; 
            nodes[1] = nodes[3];
            nodes[3] = nodes[2];
            nodes[2] = t;
            break;
      }
      if (nodes[3]<nodes[2])
      {
         t = nodes[2];
         nodes[2] = nodes[3];
         nodes[3] = t;
         return false;
      }
      return true;
   }

   @Override
   public byte compare(int[] v)
   {
      if (v.length != 4)
         return 0;
      return compare(new Tetra(this.nspace, v[0], v[1], v[2], v[3]));
   }
   

   public SimplexPosition barycentricCoords(float[] p, float[] coords)
   {
      float[][] A  = new float[3][3];
      float[]   v0 = new float[3];
      float[]   b  = new float[3];
      int l = 3 * vertices[0];
      for (int i = 0; i < 3; i++)
         v0[i] = coords[l + i];
      for (int i = 0; i < 3; i++)
      {
         b[i] = p[i] - v0[i];
         for (int j = 0; j < 3; j++)
            A[i][j] = coords[3 * vertices[j + 1] + i] - v0[i];
      }
      float[] x = NumericalMethods.lsolve(A, b);
      if (x == null)
         return null;
      if (x[0] < 0 || x[1] < 0 || x[2] < 0 || x[0] + x[1] + x[2] > 1)
         return null;
      SimplexPosition result = new SimplexPosition();
      System.arraycopy(vertices, 0, result.verts, 0, 4);
      System.arraycopy(x, 0, result.coords, 1, 3);
      result.coords[0] = 1 - (x[0] + x[1] + x[2]);
      result.verts = vertices;
      result.simplex = this;
      return result;
   }
}
