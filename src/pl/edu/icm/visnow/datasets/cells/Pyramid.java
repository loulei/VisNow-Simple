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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author  Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class Pyramid extends Cell
{
/**
 * Constructor of a pyramid
 * @param nsp space dimension
 * @param i0
 * @param i1
 * @param i2
 * @param i3
 * @param i4
 *  <pre>  
 *             i4
 *           // \\
 *         / /   \ \
 *       /  /     \  \
 *     i3- /- - - -\ - i2
 *     |  /         \  |
 *     | /           \ |
 *     |/             \|
 *     i0--------------i1
 * </pre> 
 *     i1<lt>(i2,i3,i4)
 * 
 * @param orientation significant if cell is of dim nspace or is a face of a cell of dim nspace 
 */
   public Pyramid(int nsp, int i0, int i1, int i2, int i3, int i4, boolean orientation)
   {
      type = Cell.PYRAMID;
      nspace = nsp;
      vertices = new int[5];
      vertices[0] = i0;
      vertices[1] = i1;
      vertices[2] = i2;
      vertices[3] = i3;
      vertices[4] = i4;
      this.orientation = orientation;
      normalize();
   }
   
   public Pyramid()
   {
   }
   
   public Pyramid(int nsp, int i0, int i1, int i2, int i3, int i4)
   {
      this(nsp, i0, i1, i2, i3, i4, true);
   }
   
   public boolean isSimplex()
   {
      return false;
   }
   
   @Override
   public Cell[][] subcells()
   {
      Cell[][] tr = new Cell[4][];
      tr[3]    = new Pyramid[1];
      tr[3][0] = this;
      tr[2]    = faces();
      tr[1]    = new Segment[8];
      tr[1][0] = new Segment(nspace, vertices[0], vertices[1], orientation);
      tr[1][1] = new Segment(nspace, vertices[1], vertices[3], orientation);
      tr[1][2] = new Segment(nspace, vertices[3], vertices[2], orientation);
      tr[1][3] = new Segment(nspace, vertices[2], vertices[0], orientation);
      tr[1][4] = new Segment(nspace, vertices[0], vertices[4], orientation);
      tr[1][5] = new Segment(nspace, vertices[1], vertices[4], orientation);
      tr[1][6] = new Segment(nspace, vertices[2], vertices[4], orientation);
      tr[1][7] = new Segment(nspace, vertices[3], vertices[4], orientation);
      tr[0]    = new Point[vertices.length];
      for (int i = 0; i < vertices.length; i++)
         tr[0][i] = new Point(nspace, vertices[i]);
      return tr;
   }

   @Override
   public Cell[] faces()
   {
      Cell[] faces = {new Quad(nspace, vertices[0], vertices[1], vertices[2], vertices[3], !orientation),
                      new Triangle(nspace, vertices[0], vertices[1], vertices[4], orientation),
                      new Triangle(nspace, vertices[1], vertices[2], vertices[4], orientation),
                      new Triangle(nspace, vertices[2], vertices[3], vertices[4], orientation),
                      new Triangle(nspace, vertices[0], vertices[3], vertices[4], !orientation)};
      return faces;
   }

   @Override
   public Cell[] faces(int[] nodes, boolean orientation)
   {
      Cell[] faces = {new Quad(nspace, nodes[0], nodes[1], nodes[2], nodes[3], !orientation),
                      new Triangle(nspace, nodes[0], nodes[1], nodes[4], orientation),
                      new Triangle(nspace, nodes[1], nodes[2], nodes[4], orientation),
                      new Triangle(nspace, nodes[2], nodes[3], nodes[4], orientation),
                      new Triangle(nspace, nodes[0], nodes[3], nodes[4], !orientation)};
      return faces;
   }

   @Override
   public Cell[] triangulation()
   {
      Tetra[] subdiv = {new Tetra(nspace, vertices[0], vertices[1], vertices[2], vertices[4], orientation),
                        new Tetra(nspace, vertices[0], vertices[2], vertices[3], vertices[4], orientation)};
      return subdiv;
   }
   
   /**
    *
    * @return
    */
   @Override
   public int[][] triangulationVertices()
   {
      return triangulationVertices(vertices);
   }
   
   /**
    *
    * @return
    */
   public static int[][] triangulationVertices(int[] vertices)
   {
      return new int[][] {{vertices[0], vertices[1], vertices[2], vertices[4]},
                          {vertices[0], vertices[2], vertices[3], vertices[4]}};
   }
   
   /**
    *
    * @return
    */
   public static int[][] triangulationIndices(int[] vertices)
   {
      return new int[][] {{0, 1, 2, 4},{0, 2, 3, 4}};
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
      if (v.length!=5)
         return 0;
      return compare( new Pyramid(this.nspace, v[0], v[1], v[2], v[3], v[4]));
   }


}
