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
 * @author  Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class Point extends Cell
{
   public Point(int nsp, int i, boolean orientation)
   {
      type = Cell.POINT;
      nspace = nsp;
      vertices = new int[1];
      vertices[0] = i;
      this.orientation = orientation;
   }
   
   public Point(int nsp, int i)
   {
      this(nsp, i, true);
   }
    
   @Override
   public boolean isSimplex()
   {
      return true;
   }
   
   @Override
   public Cell[] triangulation()
   {
      Point[] subdiv = {this};
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
      return null;
   }
    
   @Override
   public Cell[] faces(int[] nodes, boolean orientation)
   {
      return null;
   }

   @Override
   public Cell[][] subcells()
   {
      Point[][] tr = new Point[1][1];
      tr[0][0] = this;
      return tr;
   }

   @Override
   public int[] normalize()
   {
      return vertices;
   }
   
   @Override
   public boolean normalize(int[] nodes)
   {
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
