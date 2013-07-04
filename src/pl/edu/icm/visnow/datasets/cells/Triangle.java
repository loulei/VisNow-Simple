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

import java.util.Arrays;
import pl.edu.icm.visnow.lib.utils.numeric.NumericalMethods;

/**
 *
 * @author  Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class Triangle extends Cell
{
   
   public Triangle()
   {
   }
   
   public Triangle(int nsp, int i0, int i1, int i2, boolean orientation)
   {
      type = Cell.TRIANGLE;
      nspace = nsp;
      vertices = new int[3];
      vertices[0] = i0;
      vertices[1] = i1;
      vertices[2] = i2;
      this.orientation = orientation;
      normalize();
   }
   
   public Triangle(int nsp, int i0, int i1, int i2)
   {
      this(nsp, i0, i1, i2, true);
   }
   
   public boolean isSimplex()
   {
      return true;
   }
   
// creed's undocumented constructor ;)
   public Triangle(int nsp, int i0, int i1, int i2, boolean orientation, boolean rearange)
   {
      type = Cell.TRIANGLE;
      nspace = nsp;
      vertices = new int[3];
      vertices[0] = i0;
      vertices[1] = i1;
      vertices[2] = i2;
      this.orientation = orientation;

	  if(rearange)
		normalize();
   }

   @Override
   public Cell[][] subcells()
   {
      Cell[][] tr = new Cell[3][];
      tr[2]    = triangulation();
      tr[1]    = faces();
      tr[0]    = new Point[vertices.length];
      for (int i = 0; i < vertices.length; i++)
         tr[0][i] = new Point(nspace, vertices[i]);
      return tr;
   }
   
   public Cell[] triangulation()
   {
      Triangle[] subdiv = {this};
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
   
    public Cell[] faces()
    {
       return faces(vertices, orientation);
    }

    public Cell[] faces(int[] nodes, boolean orientation)
    {
       Segment[] faces = new Segment[3];
       faces[0] = new Segment(nspace, nodes[1], nodes[2], orientation);
       faces[0].setStatus(Cell.ADDED_FACE);
       faces[1] = new Segment(nspace, nodes[2], nodes[0], orientation);
       faces[1].setStatus(Cell.ADDED_FACE);
       faces[2] = new Segment(nspace, nodes[0], nodes[1], orientation);
       faces[2].setStatus(Cell.ADDED_FACE);
       return faces;
    }

   @Override
   public boolean normalize(int[] nodes)
   {
      int i = 0;
      for (int j = 1; j < 3; j++)
         if (nodes[j]<nodes[i])
            i = j;
      if (i==1)
      {
         int t = nodes[0]; 
         nodes[0] = nodes[1];
         nodes[1] = nodes[2];
         nodes[2] = t;
      }
      else if (i==2)
      {
         int t = nodes[0]; 
         nodes[0] = nodes[2];
         nodes[2] = nodes[1];
         nodes[1] = t;
      }
      if (nodes[1]>nodes[2])
      {
         int t = nodes[2]; 
         nodes[2] = nodes[1];
         nodes[1] = t;
         return false;
      }
      return true;
   }

   public static boolean normalize(int[] nodes, int offset)
   {
      int i = 0;
      for (int j = 1; j < 3; j++)
         if (nodes[offset+j]<nodes[offset+i])
            i = j;
      if (i==1)
      {
         int t = nodes[offset];
         nodes[offset] = nodes[offset+1];
         nodes[offset+1] = nodes[offset+2];
         nodes[offset+2] = t;
      }
      else if (i==2)
      {
         int t = nodes[offset];
         nodes[offset] = nodes[offset+2];
         nodes[offset+2] = nodes[offset+1];
         nodes[offset+1] = t;
      }
      if (nodes[offset+1]>nodes[offset+2])
      {
         int t = nodes[offset+2];
         nodes[offset+2] = nodes[offset+1];
         nodes[offset+1] = t;
         return false;
      }
      return true;
   }

   @Override
   public byte compare(int[] v)
   {
      if (v.length != 3)
         return 0;
      return compare(new Triangle(this.nspace, v[0], v[1], v[2]));
   }

	@Override
	public boolean equals(Object obj)
	{
		Triangle that = ((Triangle)obj);

		return(Arrays.equals(this.vertices,that.vertices));
	}

   public SimplexPosition barycentricCoords(float[] p, float[] coords)
   {
      float[][] A  = new float[2][2];
      float[]   v0 = new float[2];
      float[]   b  = new float[2];
      int l = 2 * vertices[0];
      for (int i = 0; i < 2; i++)
         v0[i] = coords[l + i];
      for (int i = 0; i < 2; i++)
      {
         b[i] = p[i] - v0[i];
         for (int j = 0; j < 2; j++)
            A[i][j] = coords[2 * vertices[j + 1] + i] - v0[i];
      }
      float[] x = NumericalMethods.lsolve(A, b);
      if (x == null)
         return null;
      if (x[0] < 0 || x[1] < 0 || x[0] + x[1] > 1)
         return null;
      SimplexPosition result = new SimplexPosition();
      System.arraycopy(vertices, 0, result.verts, 0, 4);
      System.arraycopy(x, 0, result.coords, 1, 2);
      result.coords[0] = 1 - (x[0] + x[1]);
      result.verts = vertices;
      result.simplex = this;
      return result;
   }

}
