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
public class Segment extends Cell
{
   
   public Segment(int nsp, int i0, int i1, boolean orientation)
   {
      type = Cell.SEGMENT;
      nspace = nsp;
      vertices = new int[2];
      vertices[0] = i0;
      vertices[1] = i1;
      this.orientation = orientation;
      normalize();
   }
   
   public Segment()
   {
   }
   
   public Segment(int nsp, int i0, int i1)
   {
      this(nsp, i0, i1, true);
   }
   
  @Override
   public boolean isSimplex()
   {
      return true;
   }
   
   @Override
   public Cell[][] subcells()
   {
      Cell[][] tr = new Cell[2][];
      tr[1]    = new Segment[1];
      tr[1][0] = this;
      tr[0]    = new Point[vertices.length];
      for (int i = 0; i < vertices.length; i++)
         tr[0][i] = new Point(nspace, vertices[i]);
      return tr;
   }
   
   @Override
   public Cell[] triangulation()
   {
      Segment[] subdiv = {this};
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
    
   @Override
    public Cell[] faces()
    {
       return faces(vertices, orientation);
    }
   
   @Override
    public Cell[] faces(int[] nodes, boolean orientation)
    {
       Point[] faces = new Point[2];
       faces[0] = new Point(nspace, nodes[0],false);
       faces[0].setStatus(Cell.ADDED_FACE);
       faces[1] = new Point(nspace, nodes[1],true);
       faces[1].setStatus(Cell.ADDED_FACE);
       return faces;
    }
   
   @Override
   public boolean normalize(int[] nodes)
   {
      if (nodes[0]>nodes[1])
      {
         int t = nodes[0]; 
         nodes[0] = nodes[1];
         nodes[1] = t;
         return false;
      }
      return true;
   }


  @Override
  public byte compare(int[] v)
  {
    if (v.length == 1)
       return 0;
    else if (v[0] != vertices[0])
       return 0;
    else
       return 1;       
  }
  
}
