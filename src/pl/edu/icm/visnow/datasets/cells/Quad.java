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

package pl.edu.icm.visnow.datasets.cells;

/**
 *
 * @author  Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class Quad extends Cell
{
/**
 * 
 * @param nsp space dimension
 * @param i0
 * @param i1
 * @param i2
 * @param i3
 * 
 *    i3------- i2
 *     |       / |
 *     |     /   |
 *     |   /     |
 *     | /       |
 *    i0--------i1
 * 
 * @param orientation significant if cell is of dim nspace or is a face of a cell of dim nspace 
 */
   public Quad(int nsp, int i0, int i1, int i2, int i3, boolean orientation)
   {
      type = Cell.QUAD;
      nspace = nsp;
      vertices = new int[4];
      vertices[0] = i0;
      vertices[1] = i1;
      vertices[2] = i2;
      vertices[3] = i3;
      this.orientation = orientation;
      normalize();
   }
   
   public Quad()
   {
   }
   
   public Quad(int nsp, int i0, int i1, int i2, int i3)
   {
      this(nsp, i0, i1, i2, i3, true);
   }
   
   @Override
   public Cell[][] subcells()
   {
      Cell[][] tr = new Cell[3][];
      tr[2]    = new Quad[1];
      tr[2][0] = this;
      tr[1]    = faces();
      tr[0]    = new Point[vertices.length];
      for (int i = 0; i < vertices.length; i++)
         tr[0][i] = new Point(nspace, vertices[i]);
      return tr;
   }
   
   @Override
   public boolean isSimplex()
   {
      return false;
   }

   @Override
   public Cell[] triangulation()
   {
      Triangle[] subdiv = {new Triangle(nspace, vertices[0], vertices[1], vertices[2], orientation),
                           new Triangle(nspace, vertices[0], vertices[2], vertices[3], orientation)};
      return subdiv;
   }
    
   @Override
   public int[][] triangulationVertices()
   {
      return new int[][]{{vertices[0], vertices[1], vertices[2]},
                         {vertices[0], vertices[2], vertices[3]}};
   }
   
   public static int[][] triangulationVertices(int[] vertices)
   {
      return new int[][]{{vertices[0], vertices[1], vertices[2]},
                         {vertices[0], vertices[2], vertices[3]}};
   }
   
   public static int[][] triangulationIndices(int[] vertices)
   {
      return new int[][]{{0, 1, 2},{0, 2, 3}};
   }
   
   @Override
    public Cell[] faces(int[] nodes, boolean orientation)
    {
       Segment[] faces = {new Segment(nspace, nodes[0], nodes[1], orientation),
                          new Segment(nspace, nodes[1], nodes[2], orientation),
                          new Segment(nspace, nodes[2], nodes[3], orientation),
                          new Segment(nspace, nodes[3], nodes[0], orientation)};
       faces[0].setStatus(Cell.ADDED_FACE);
       faces[1].setStatus(Cell.ADDED_FACE);
       faces[2].setStatus(Cell.ADDED_FACE);
       faces[3].setStatus(Cell.ADDED_FACE);
       return faces;
    }
   
   @Override
   public Cell[] faces()
   {
      return faces(vertices, orientation);
   }
   
   @Override
   public boolean normalize(int[] nodes)
   {
      int i = 0, t;
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
            nodes[2] = nodes[3];
            nodes[3] = t;
            break;
         case 2:
            t = nodes[0]; 
            nodes[0] = nodes[2];
            nodes[2] = t;
            t = nodes[1]; 
            nodes[1] = nodes[3];
            nodes[3] = t;
            break;
          case 3:
            t = nodes[0]; 
            nodes[0] = nodes[3];
            nodes[3] = nodes[2];
            nodes[2] = nodes[1];
            nodes[1] = t;
            break;
     }
      if (nodes[1]>nodes[3])
      {
         t = nodes[3]; 
         nodes[3] = nodes[1];
         nodes[1] = t;
         return false;
      }
      return true;
   }

   
   @Override
   public byte compare(int[] v)
   {
      if (v.length != 4)
         return 0;
      return compare(new Quad(this.nspace, v[0], v[1], v[2], v[3]));
   }

}
