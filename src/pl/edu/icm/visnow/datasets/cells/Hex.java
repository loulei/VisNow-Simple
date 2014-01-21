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
 * @author Krzysztof S. Nowinski
 *   <p>   University of Warsaw, ICM
 */
public class Hex extends Cell
{
     int[] verts = {0,1,3,4};


/**
 * Constructor of a hex
 * @param nsp space dimension
 * @param i0 ... i7 - node indices
 *  <pre>
 *  i4----------i7
 *  |\          |\
 *  |  \           \
 *  |    \      |    \
 *  |     i5----------i6     
 *  |     |     |     |
 *  |     |           |
 *  i0- - | - - i3    |
 *   \    |      \    |
 *     \  |        \  |
 *       \|          \|
 *        i1----------i2
 * 
 *  </pre>
 * Indices are ordered so that
 *  <code>   i0<(i1,i2,i3,i4,i5,i6,i7), i1<i3<i4</code>
 * @param orientation significant if cell is of dim nspace or is a face of a cell of dim nspace,
 * true if vectors <code>i1-i0, i3-i0, i4-i0 </code>form positively oriented reper
 */
   public Hex(int nsp, 
              int i0, int i1, int i2, int i3, int i4, int i5, int i6, int i7,
              boolean orientation)
   {
      type = Cell.HEXAHEDRON;
      nspace = nsp;
      vertices = new int[8];
      vertices[0] = i0;
      vertices[1] = i1;
      vertices[2] = i2;
      vertices[3] = i3;
      vertices[4] = i4;
      vertices[5] = i5;
      vertices[6] = i6;
      vertices[7] = i7;
      this.orientation = orientation;
      normalize();
   }
   
   public Hex()
   {
   }
   
   public Hex(int nsp, int i0, int i1, int i2, int i3, int i4, int i5, int i6, int i7)
   {
      this(nsp, i0, i1, i2, i3, i4, i5, i6, i7, true);
   }
   
   public boolean isSimplex()
   {
      return false;
   }
   
   
   @Override
   public Cell[][] subcells()
   {
      Cell[][] tr = new Cell[4][];
      tr[3]    = new Hex[1];
      tr[3][0] = this;
      tr[2]    = faces();
      tr[1]    = new Segment[12];
      tr[1][0] = new Segment(nspace, vertices[0], vertices[1], orientation);
      tr[1][1] = new Segment(nspace, vertices[1], vertices[3], orientation);
      tr[1][2] = new Segment(nspace, vertices[3], vertices[2], orientation);
      tr[1][3] = new Segment(nspace, vertices[2], vertices[0], orientation);
      tr[1][4] = new Segment(nspace, vertices[4], vertices[5], orientation);
      tr[1][5] = new Segment(nspace, vertices[5], vertices[7], orientation);
      tr[1][6] = new Segment(nspace, vertices[7], vertices[6], orientation);
      tr[1][7] = new Segment(nspace, vertices[6], vertices[4], orientation);
      tr[1][8] = new Segment(nspace, vertices[0], vertices[4], orientation);
      tr[1][9] = new Segment(nspace, vertices[1], vertices[5], orientation);
      tr[1][10] = new Segment(nspace, vertices[2], vertices[6], orientation);
      tr[1][11] = new Segment(nspace, vertices[3], vertices[7], orientation);
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
      Cell[] faces = {new Quad(nspace, nodes[0], nodes[1], nodes[2], nodes[3], !orientation),
                      new Quad(nspace, nodes[4], nodes[5], nodes[6], nodes[7], orientation),
                      new Quad(nspace, nodes[0], nodes[1], nodes[5], nodes[4], orientation),
                      new Quad(nspace, nodes[1], nodes[2], nodes[6], nodes[5], orientation),
                      new Quad(nspace, nodes[2], nodes[3], nodes[7], nodes[6], orientation),
                      new Quad(nspace, nodes[0], nodes[3], nodes[7], nodes[4], !orientation)};
      return faces;
   }

   @Override
   public Cell[] triangulation()
   {
      Tetra[] subdiv = new Tetra[6];
      int[][] s;
      int[] f0 = {vertices[1], vertices[2], vertices[6], vertices[5]};
      s = diagonalSubdiv(f0);
      subdiv[0] = new Tetra(nspace, vertices[0], s[0][0], s[0][1], s[0][2], orientation);        
      subdiv[1] = new Tetra(nspace, vertices[0], s[1][0], s[1][1], s[1][2], orientation);        
      int[] f1 = {vertices[2], vertices[3], vertices[7], vertices[6]};
      s = diagonalSubdiv(f1);
      subdiv[2] = new Tetra(nspace, vertices[0], s[0][0], s[0][1], s[0][2], orientation);        
      subdiv[3] = new Tetra(nspace, vertices[0], s[1][0], s[1][1], s[1][2], orientation);        
      int[] f2 = {vertices[4], vertices[5], vertices[6], vertices[7]};
      s = diagonalSubdiv(f2);
      subdiv[4] = new Tetra(nspace, vertices[0], s[0][0], s[0][1], s[0][2], orientation);        
      subdiv[5] = new Tetra(nspace, vertices[0], s[1][0], s[1][1], s[1][2], orientation);        
      return subdiv;
   }
   
   @Override
   public int[][] triangulationVertices()
   {    
      return triangulationVertices(vertices);
   }
   
   public static int[][] triangulationVertices(int[] vertices)
   {
      int[][] subdiv = new int[6][];
      int[][] s;
      s = diagonalSubdiv(new int[] {vertices[1], vertices[2], vertices[6], vertices[5]});
      subdiv[0] = new int[] {vertices[0], s[0][0], s[0][1], s[0][2]};        
      subdiv[1] = new int[] {vertices[0], s[1][0], s[1][1], s[1][2]};        
      s = diagonalSubdiv(new int[] {vertices[2], vertices[3], vertices[7], vertices[6]});
      subdiv[2] = new int[] {vertices[0], s[0][0], s[0][1], s[0][2]};        
      subdiv[3] = new int[] {vertices[0], s[1][0], s[1][1], s[1][2]};   
      s = diagonalSubdiv(new int[] {vertices[4], vertices[5], vertices[6], vertices[7]});
      subdiv[4] = new int[] {vertices[0], s[0][0], s[0][1], s[0][2]};        
      subdiv[5] = new int[] {vertices[0], s[1][0], s[1][1], s[1][2]};     
      return subdiv;
   }

   private static final int[][] oppQuads = new int[][] {{1, 2, 6, 5}, {2, 3, 7, 6}, {4, 5, 6, 7}};
   
   public static int[][] triangulationIndices(int[] vertices)
   {
      int[][] subdiv = new int[6][];
      int[][] s;
      for (int i = 0; i < 3; i++)
      {
         int[] oppQuad = oppQuads[i];
         int[] oppQuadVerts = new int[4];
         for (int j = 0; j < oppQuadVerts.length; j++)
            oppQuadVerts[j] = vertices[oppQuad[j]];
         s = diagonalSubdivIndices(oppQuadVerts);
         subdiv[2 * i]     = new int[] {0, oppQuad[s[0][0]], oppQuad[s[0][1]], oppQuad[s[0][2]]};
         subdiv[2 * i + 1] = new int[] {0, oppQuad[s[1][0]], oppQuad[s[1][1]], oppQuad[s[1][2]]};
      }
      return subdiv;
   }

   @Override
   public boolean normalize(int[] nodes)
   {
      int i = 0, t=0;
      for (int j = 1; j < 8; j++)
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
            t = nodes[4]; 
            nodes[4] = nodes[5];
            nodes[5] = nodes[6];
            nodes[6] = nodes[7];
            nodes[7] = t;
            break;
         case 2:
            t = nodes[0]; 
            nodes[0] = nodes[2];
            nodes[2] = t;
            t = nodes[1]; 
            nodes[1] = nodes[3];
            nodes[3] = t;
            t = nodes[4]; 
            nodes[4] = nodes[6];
            nodes[6] = t;
            t = nodes[5]; 
            nodes[5] = nodes[7];
            nodes[7] = t;
            break;
         case 3:
            t = nodes[0]; 
            nodes[0] = nodes[3];
            nodes[3] = nodes[2];
            nodes[2] = nodes[1];
            nodes[1] = t;
            t = nodes[4]; 
            nodes[4] = nodes[7];
            nodes[7] = nodes[6];
            nodes[6] = nodes[5];
            nodes[5] = t;
            break;
         case 4:
            t = nodes[0]; 
            nodes[0] = nodes[4];
            nodes[4] = t;
            t = nodes[1]; 
            nodes[1] = nodes[7];
            nodes[7] = t;
            t = nodes[2]; 
            nodes[2] = nodes[6];
            nodes[6] = t;
            t = nodes[3]; 
            nodes[3] = nodes[5];
            nodes[5] = t;
            break;
         case 5:
            t = nodes[0]; 
            nodes[0] = nodes[5];
            nodes[5] = t;
            t = nodes[3]; 
            nodes[3] = nodes[6];
            nodes[6] = t;
            t = nodes[1]; 
            nodes[1] = nodes[4];
            nodes[4] = t;
            t = nodes[2]; 
            nodes[2] = nodes[7];
            nodes[7] = t;           
            break;
         case 6:
            t = nodes[0]; 
            nodes[0] = nodes[6];
            nodes[6] = t;
            t = nodes[3]; 
            nodes[3] = nodes[7];
            nodes[7] = t;
            t = nodes[1]; 
            nodes[1] = nodes[5];
            nodes[5] = t;
            t = nodes[2]; 
            nodes[2] = nodes[4];
            nodes[4] = t;           
            break;
         case 7:
            t = nodes[0]; 
            nodes[0] = nodes[7];
            nodes[7] = t;
            t = nodes[3]; 
            nodes[3] = nodes[4];
            nodes[4] = t;
            t = nodes[1]; 
            nodes[1] = nodes[6];
            nodes[6] = t;
            t = nodes[2]; 
            nodes[2] = nodes[5];
            nodes[5] = t;           
            break;
      }
      i = 1;
      for (int j = 2; j < 5; j++)
         if (nodes[j]<nodes[i] && j!=2)
            i = j;
      switch (i)
      {
      case 1:
         break;
      case 3:
         t = nodes[1]; 
         nodes[1] = nodes[3];
         nodes[3] = nodes[4];
         nodes[4] = t;
         t = nodes[2]; 
         nodes[2] = nodes[7];
         nodes[7] = nodes[5];
         nodes[5] = t;
         break;
      case 4:
         t = nodes[1]; 
         nodes[1] = nodes[4];
         nodes[4] = nodes[3];
         nodes[3] = t;
         t = nodes[2]; 
         nodes[2] = nodes[5];
         nodes[5] = nodes[7];
         nodes[7] = t;
         break;
      }
      if (nodes[3]>nodes[4])
      {
         t = nodes[3]; 
         nodes[3] = nodes[4];
         nodes[4] = t;
         t = nodes[2]; 
         nodes[2] = nodes[5];
         nodes[5] = t;
         return false;
      }
      return true;
   }

   @Override
   public byte compare(int[] v)
   {
      if (v.length!=8)
         return 0;
      return compare(new Hex(this.nspace, v[0], v[1], v[2], v[3], v[4], v[5], v[6], v[7]));
   }
   
}
