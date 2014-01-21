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
public class RegularHex extends Hex
{
/**
 * Constructor of a regular hex
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
 * @param even is parity of i0 + i1 + i2 (indices of the cell in mesh)
 */
   private boolean even;

   public RegularHex(int nsp,
              int i0, int i1, int i2, int i3, int i4, int i5, int i6, int i7,
              boolean orientation, boolean even)
   {
      super(nsp, i0, i1, i2, i3, i4, i5, i6, i7, orientation);
      this.even = even;
   }

   public RegularHex(int nsp, int i0, int i1, int i2, int i3, int i4, int i5, int i6, int i7, boolean even)
   {
      this(nsp, i0, i1, i2, i3, i4, i5, i6, i7, true, even);
   }

   
   public boolean isSimplex()
   {
      return false;
   }
   
   @Override
   public Cell[] triangulation()
   {
      if (even)
          return new Tetra[]
          {
             new Tetra(nspace, vertices[0], vertices[2], vertices[7], vertices[5], orientation),
             new Tetra(nspace, vertices[1], vertices[2], vertices[0], vertices[5], orientation),
             new Tetra(nspace, vertices[3], vertices[0], vertices[2], vertices[7], orientation),
             new Tetra(nspace, vertices[6], vertices[5], vertices[7], vertices[2], orientation),
             new Tetra(nspace, vertices[5], vertices[4], vertices[7], vertices[0], orientation)
          };
      else
         return new Tetra[]
          {
             new Tetra(nspace, vertices[1], vertices[3], vertices[4], vertices[6], orientation),
             new Tetra(nspace, vertices[0], vertices[1], vertices[3], vertices[4], orientation),
             new Tetra(nspace, vertices[2], vertices[3], vertices[1], vertices[6], orientation),
             new Tetra(nspace, vertices[5], vertices[4], vertices[1], vertices[6], orientation),
             new Tetra(nspace, vertices[7], vertices[6], vertices[4], vertices[3], orientation)
          };
   }
   
   @Override
   public  int[][] triangulationVertices()
   {
      if (even) return new int[][]
          {{vertices[0], vertices[2], vertices[7], vertices[5]},
           {vertices[1], vertices[2], vertices[0], vertices[5]},
           {vertices[3], vertices[0], vertices[2], vertices[7]},
           {vertices[6], vertices[5], vertices[7], vertices[2]},
           {vertices[5], vertices[4], vertices[7], vertices[0]}};
      else
         return new int[][]
          {{vertices[1], vertices[3], vertices[4], vertices[6]},
           {vertices[0], vertices[1], vertices[3], vertices[4]},
           {vertices[2], vertices[3], vertices[1], vertices[6]},
           {vertices[5], vertices[4], vertices[1], vertices[6]},
           {vertices[7], vertices[6], vertices[4], vertices[3]}};
   }   
   

   public static int[][] triangulationVertices(int[] vertices, boolean even)
   {
      if (even) return new int[][]
          {{vertices[0], vertices[2], vertices[7], vertices[5]},
           {vertices[1], vertices[2], vertices[0], vertices[5]},
           {vertices[3], vertices[0], vertices[2], vertices[7]},
           {vertices[6], vertices[5], vertices[7], vertices[2]},
           {vertices[5], vertices[4], vertices[7], vertices[0]}};
      else
         return new int[][]
          {{vertices[1], vertices[3], vertices[4], vertices[6]},
           {vertices[0], vertices[1], vertices[3], vertices[4]},
           {vertices[2], vertices[3], vertices[1], vertices[6]},
           {vertices[5], vertices[4], vertices[1], vertices[6]},
           {vertices[7], vertices[6], vertices[4], vertices[3]}};
   }
   
   public int[] getIndicesChunk()
   {
      if (even)
         return new int[]
         {
            vertices[0], vertices[2], vertices[7], vertices[5], 
            vertices[1], vertices[2], vertices[0], vertices[5], 
            vertices[3], vertices[0], vertices[2], vertices[7],
            vertices[6], vertices[5], vertices[7], vertices[2],
            vertices[5], vertices[4], vertices[7], vertices[0]
         };
      else
         return new int[]
         {
            vertices[1], vertices[3], vertices[4], vertices[6],
            vertices[0], vertices[1], vertices[3], vertices[4],
            vertices[2], vertices[3], vertices[1], vertices[6],
            vertices[5], vertices[4], vertices[1], vertices[6],
            vertices[7], vertices[6], vertices[4], vertices[3]
         };
   }

   public static int[][] triangulationIndices(boolean even)
   {
      if (even) return new int[][]
          {{0, 2, 7, 5},
           {1, 2, 0, 5},
           {3, 0, 2, 7},
           {6, 5, 7, 2},
           {5, 4, 7, 0}};
      else
         return new int[][]
          {{1, 3, 4, 6},
           {0, 1, 3, 4},
           {2, 3, 1, 6},
           {5, 4, 1, 6},
           {7, 6, 4, 3}};
   }   

}
