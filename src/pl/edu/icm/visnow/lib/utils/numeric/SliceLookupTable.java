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

package pl.edu.icm.visnow.lib.utils.numeric;

import pl.edu.icm.visnow.datasets.cells.Cell;

/**
 *
 * @author know
 */
public class SliceLookupTable
{
   /*
    * encode distribution of values of slice equation function as 3-base number 
    * with i-th digit 0 for zero value at i-th node, 1 for negative and 2 for positive value
    * the code will be used to look in the table of sections
    */
   public static int simplexCode(float[] v)
   {
      int code = 0;
      for (int i = 0, k = 1; i < v.length; i++, k *= 3)
         if (v[i] < 0) 
            code += k;
         else if (v[i] > 0)
            code += 2 * k;
      return code;
   }
   /*
    * encode distribution of values of slice equation function as 3-base number 
    * if over,
    * i-th digit is 0 for zero value at i-th node, 1 for negative and 2 for positive value
    * else
    * i-th digit is 0 for zero value at i-th node, 1 for positive and 2 for negative value
    * the code will be used to look in the table of sections
    */
   public static int simplexCode(float[] v, boolean over)
   {
      int code = 0;
      if (over)
      {
         for (int i = 0, k = 1; i < v.length; i++, k *= 3)
            if (v[i] < 0) 
               code += k;
            else if (v[i] > 0)
               code += 2 * k;
      }
      else
      {
         for (int i = 0, k = 1; i < v.length; i++, k *= 3)
            if (v[i] > 0) 
               code += k;
            else if (v[i] < 0)
               code += 2 * k;
      }
      return code;
   }
   
   public static final int[][][] addNodes = 
   {
      {
      },  
      {
         {}
      },
      {
         {},{},{0, 1}
      },
      {
         {},{},{},{0, 1},{0, 2},{1, 2}
      },
      {
         {},{},{},{},{0, 1},{0, 2},{0, 3},{1, 2},{1, 3},{2, 3}
      },
   };
   
   public static final int[][][] slice = 
   {       
      {
         {0}, {}, {}
      },
      {                       // node codes (0:0, 1:-, 2:+
         {0, 1}, {1},    {1},                      //x0
         {0},    {},     {2},                      //x1
         {0},    {2},    {}                        //x2
      },
      {                             
         {0, 1, 2}, {1, 2}, {1, 2},                //x00
         {0, 2},    {},     {2, 3},                //x10
         {0, 2},    {2, 3}, {},                    //x20
         {0, 1},    {},     {1, 4},                //x01
         {},        {},     {3, 4},                //x11
         {0, 5},    {3, 5}, {4, 5},                //x21
         {0, 1},    {1, 4}, {},                    //x02
         {0, 5},    {4, 5}, {3, 5},                //x12
         {},        {3, 4}, {},                    //x22
      },
      {},
      {                                            
         {},           {1, 2, 3},    {1, 2, 3},    //x000
         {0, 2, 3},    {},           {2, 3, 4},    //x100
         {0, 2, 3},    {2, 3, 4},    {},           //x200
         
         {0, 1, 3},    {},           {1, 3, 5},    //x010
         {},           {},           {3, 4, 5},    //x110
         {0, 3, 7},    {3, 4, 7},    {3, 5, 7},    //x210
         
         {0, 1, 3},    {1, 3, 5},    {},           //x020
         {0, 3, 7},    {3, 5, 7},    {3, 4, 7},    //x120
         {},           {3, 4, 5},    {},           //x220
         
         
         {0, 1, 2},    {},           {1, 2, 6},    //x001
         {},           {},           {2, 4, 6},    //x101
         {0, 2, 8},    {2, 4, 8},    {2, 6, 8},    //x201
         
         {},           {},           {1, 5, 6},    //x011
         {},           {},           {4, 5, 6},    //x111
         {0, 7, 8},    {4, 7, 8},    {5, 6, 8, 7}, //x211
         
         {0, 1, 9},    {1, 5, 9},    {0, 6, 9},    //x021
         {0, 7, 9},    {5, 7, 9},    {4, 6, 9, 7}, //x121
         {0, 8, 9},    {4, 5, 9, 8}, {6, 8, 9},    //x221
         
         
         {0, 1, 2},    {1, 2, 6},    {},           //x002
         {0, 2, 8},    {2, 6, 8},    {2, 4, 8},    //x102
         {},           {2, 4, 6},    {},           //x202
         
         {0, 1, 9},    {1, 6, 9},    {1, 5, 9},    //x012
         {0, 8, 9},    {6, 8, 9},    {4, 5, 9, 8}, //x112
         {0, 7, 9},    {4, 6, 9, 7}, {5, 7, 9},    //x212
         
         {},           {1, 5, 6},    {},           //x022
         {0, 7, 8},    {5, 6, 8, 7}, {4, 7, 8},    //x122
         {},           {4, 5, 6},    {}            //x222
      }
   };
   
   public static final int[][] sliceType = 
   {       
      {
         Cell.POINT, -1, -1
      },
      {                       // node codes (0:0, 1:-, 2:+
         Cell.SEGMENT, Cell.POINT, Cell.POINT,    //x0
         Cell.POINT,   -1,         Cell.POINT,    //x1
         Cell.POINT,   Cell.POINT, -1             //x2
      },
      {                             
         Cell.TRIANGLE, Cell.SEGMENT, Cell.SEGMENT, //x00
         Cell.SEGMENT,  -1,           Cell.SEGMENT, //x10
         Cell.SEGMENT,  Cell.SEGMENT, -1,           //x20
         Cell.SEGMENT,  -1,           Cell.SEGMENT, //x01
         -1,            -1,           Cell.SEGMENT, //x11
         Cell.SEGMENT,  Cell.SEGMENT, Cell.SEGMENT, //x21
         Cell.SEGMENT,  Cell.SEGMENT, -1,           //x02
         Cell.SEGMENT,  Cell.SEGMENT, Cell.SEGMENT, //x12
         -1,            Cell.SEGMENT, -1,           //x22
      },
      {},
      {                                            
         -1,            Cell.TRIANGLE, Cell.TRIANGLE,//x000
         Cell.TRIANGLE, -1,            Cell.TRIANGLE,//x100
         Cell.TRIANGLE, Cell.TRIANGLE, -1,           //x200
         
         Cell.TRIANGLE, -1,            Cell.TRIANGLE,//x010
         -1,            -1,            Cell.TRIANGLE,//x110
         Cell.TRIANGLE, Cell.TRIANGLE, Cell.TRIANGLE,//x210
         
         Cell.TRIANGLE, Cell.TRIANGLE, -1,           //x020
         Cell.TRIANGLE, Cell.TRIANGLE, Cell.TRIANGLE,//x120
         -1,            Cell.TRIANGLE, -1,           //x220
         
         
         Cell.TRIANGLE, -1,            Cell.TRIANGLE, //x001
         -1,            -1,            Cell.TRIANGLE, //x101
         Cell.TRIANGLE, Cell.TRIANGLE, Cell.TRIANGLE, //x201
         
         -1,            -1,            Cell.TRIANGLE, //x011
         -1,            -1,            Cell.TRIANGLE, //x111
         Cell.TRIANGLE, Cell.TRIANGLE, Cell.QUAD,     //x211
         
         Cell.TRIANGLE, Cell.TRIANGLE, Cell.QUAD,     //x021
         Cell.TRIANGLE, Cell.TRIANGLE, Cell.QUAD,     //x121
         Cell.TRIANGLE, Cell.QUAD,     Cell.TRIANGLE, //x221
         
         
         Cell.TRIANGLE, Cell.TRIANGLE, -1,            //x002
         Cell.TRIANGLE, Cell.TRIANGLE, Cell.TRIANGLE, //x102
         -1,            Cell.TRIANGLE, -1,            //x202
         
         Cell.TRIANGLE, Cell.TRIANGLE, Cell.TRIANGLE, //x012
         Cell.TRIANGLE, Cell.TRIANGLE, Cell.QUAD,     //x112
         Cell.TRIANGLE, Cell.QUAD,     Cell.TRIANGLE, //x212
         
         -1,            Cell.TRIANGLE, -1,            //x022
         Cell.TRIANGLE, Cell.QUAD,     Cell.TRIANGLE, //x122
         -1,            Cell.TRIANGLE, -1             //x222
      }
   };
   
    
   public static final int[][][] subcell = 
   {       
      {
         {0}, {}, {0}
      },
      {                       // node codes (0:0, 1:-, 2:+
         {0, 1}, {},     {0, 1},                    //x0
         {},     {},     {0, 2},                    //x1
         {0, 1}, {2, 1}, {0, 1}                     //x2
      },
      {                             
         {0, 1, 2}, {},           {0, 1, 2},       //x00
         {},        {},           {0, 2, 3},       //x10
         {0, 1, 2}, {1, 2, 3},    {0, 1, 2},       //x20
         {},        {},           {0, 1, 4},       //x01
         {},        {},           {0, 3, 4},       //x11
         {0, 1, 5}, {1, 3, 5},    {0, 1, 5, 4},    //x21
         {0, 1, 2}, {1, 2, 4},    {0, 1, 2},       //x02
         {0, 2, 5}, {2, 4, 5},    {0, 3, 5, 2},    //x12
         {0, 1, 2}, {1, 3, 4, 2}, {0, 1, 2},       //x22
      },
      {},
      {                                            
         {0, 1, 2, 3},    {},                 {0, 1, 2, 3},       //x000
         {},              {},                 {0, 2, 3, 4},       //x100
         {0, 1, 2, 3},    {1, 2, 3, 4},       {0, 1, 2, 3},       //x200
         
         {},              {},                 {0, 1, 3, 5},       //x010
         {},              {},                 {0, 3, 4, 5},       //x110
         {0, 1, 3, 7},    {1, 3, 4, 7},       {0, 1, 7, 5, 3},    //x210
         
         {0, 1, 2, 3},    {1, 2, 3, 5},       {0, 1, 2, 3},       //x020
         {0, 2, 3, 7},    {2, 3, 5, 7},       {0, 2, 7, 4, 3},    //x120
         {0, 1, 2, 3},    {1, 2, 5, 4, 3},    {0, 1, 2, 3},       //x220
         
         {},              {},                 {0, 1, 2, 6},       //x001
         {},              {},                 {0, 2, 4, 6},       //x101
         {0, 1, 2, 8},    {1, 2, 4, 8},       {0, 1, 8, 6, 2},    //x201
         
         {},              {},                 {0, 1, 5, 6},       //x011
         {},              {},                 {0, 4, 5, 6},       //x111
         {0, 1, 7, 8},    {1, 4, 7, 8},       {0, 5, 6, 1, 7, 8}, //x211
         
         {0, 1, 2, 9},    {1, 2, 5, 9},       {0, 2, 9, 6, 1},    //x021
         {0, 2, 7, 9},    {2, 5, 7, 9},       {0, 4, 6, 2, 7, 9}, //x121
         {1, 2, 9, 8, 0}, {1, 4, 8, 2, 5, 9}, {0, 1, 2, 6, 8, 9}, //x221
         
         {0, 1, 2, 3},    {1, 2, 3, 6},       {0, 1, 2, 3},       //x002
         {0, 2, 3, 8},    {2, 3, 6, 8},       {0, 3, 8, 4, 2},    //x102
         {0, 1, 2, 3},    {1, 3, 6, 4, 2},    {0, 1, 2, 3},       //x202
         
         {0, 1, 3, 9},    {1, 3, 6, 9},       {0, 3, 9, 5, 1},    //x012
         {0, 3, 8, 9},    {3, 6, 8, 9},       {0, 4, 5, 3, 8, 9}, //x112
         {1, 3, 9, 7, 0}, {1, 4, 7, 3, 6, 9}, {0, 1, 3, 5, 7, 9}, //x212
         
         {0, 1, 2, 3},    {2, 3, 6, 5, 1},    {0, 1, 2, 3},       //x022
         {2, 3, 8, 7, 0}, {2, 5, 7, 3, 6, 8}, {0, 2, 3, 4, 7, 8}, //x122
         {0, 1, 2, 3},    {1, 2, 3, 4, 5, 6}, {0, 1, 2, 3}        //x222
      }
   };  
       
   public static final int[][] subcellType = 
   {    
      {
         Cell.POINT, -1, Cell.POINT
      },
      {                       // node codes (0:0, 1:-, 2:+
         Cell.SEGMENT, -1,           Cell.SEGMENT, //x0
         -1,           -1,           Cell.SEGMENT, //x1
         Cell.SEGMENT, Cell.SEGMENT, Cell.SEGMENT  //x2
      },
      {                             
         Cell.TRIANGLE, -1,            Cell.TRIANGLE, //x00
         -1,            -1,            Cell.TRIANGLE, //x10
         Cell.TRIANGLE, Cell.TRIANGLE, Cell.TRIANGLE, //x20
         -1,            -1,            Cell.TRIANGLE, //x01
         -1,            -1,            Cell.TRIANGLE, //x11
         Cell.TRIANGLE, Cell.TRIANGLE, Cell.QUAD,     //x21
         Cell.TRIANGLE, Cell.TRIANGLE, Cell.TRIANGLE, //x02
         Cell.TRIANGLE, Cell.TRIANGLE, Cell.QUAD,     //x12
         Cell.TRIANGLE, Cell.QUAD,     Cell.TRIANGLE, //x22
      },
      {},
      {                                            
         Cell.TETRA,   -1,           Cell.TETRA,  //x000
         -1,           -1,           Cell.TETRA,  //x100
         Cell.TETRA,   Cell.TETRA,   Cell.TETRA,  //x200
         
         -1,           -1,           Cell.TETRA,  //x010
         -1,           -1,           Cell.TETRA,  //x110
         Cell.TETRA,   Cell.TETRA,   Cell.PYRAMID,//x210
         
         Cell.TETRA,   Cell.TETRA,   Cell.TETRA,   //x020
         Cell.TETRA,   Cell.TETRA,   Cell.PYRAMID,//x120
         Cell.TETRA,   Cell.PYRAMID, Cell.TETRA,  //x220
         
         -1,           -1,           Cell.TETRA,  //x001
         -1,           -1,           Cell.TETRA,  //x101
         Cell.TETRA,   Cell.TETRA,   Cell.PYRAMID,//x201
         
         -1,           -1,           Cell.TETRA,  //x011
         -1,           -1,           Cell.TETRA,  //x111
         Cell.TETRA,   Cell.TETRA,   Cell.PRISM,  //x211
         
         Cell.TETRA,   Cell.TETRA,   Cell.PYRAMID,//x021
         Cell.TETRA,   Cell.TETRA,   Cell.PRISM,  //x121
         Cell.PYRAMID, Cell.PRISM,   Cell.PRISM,  //x221
         
         Cell.TETRA,   Cell.TETRA,   Cell.TETRA,  //x002
         Cell.TETRA,   Cell.TETRA,   Cell.PYRAMID,//x102
         Cell.TETRA,   Cell.PYRAMID, Cell.TETRA,  //x202
         
         Cell.TETRA,   Cell.TETRA,   Cell.PYRAMID,//x012
         Cell.TETRA,   Cell.TETRA,   Cell.PRISM,  //x112
         Cell.PYRAMID, Cell.PRISM,   Cell.PRISM,  //x212
         
         Cell.TETRA,   Cell.PYRAMID, Cell.TETRA,  //x022
         Cell.PYRAMID, Cell.PRISM,   Cell.PRISM,  //x122
         Cell.TETRA,   Cell.PRISM,   Cell.TETRA   //x222
      }
   };  
   
   public static int getSliceType(int cellType, float[] vals)
   {
      return sliceType[cellType][simplexCode(vals)];
   }
   
   public static int[] getSliceNodes(int cellType, float[] vals)
   {
      return slice[cellType][simplexCode(vals)];
   }
   
   public static int getSubcellType(int cellType, float[] vals, boolean above)
   {
      return subcellType[cellType][simplexCode(vals, above)];
   }
   
   public static int[] getSubcellNodes(int cellType, float[] vals, boolean above)
   {
      return subcell[cellType][simplexCode(vals, above)];
   }
   
   static int[] v = {0, 1, -1};

   public static void main(String[] args)
   {
      for (int i = 0; i < 3; i++)
         for (int j = 0; j < 3; j++)
            for (int k = 0; k < 3; k++)
               for (int l = 0; l < 3; l++)
                  System.out.printf("%d%d%d%d, in%n",l,k,j,i);
      for (int node = 0; node < 10; node++)
      {
         System.out.printf("%2d %2d ", node + 1, node);
         for (int i = 0, n = 1; i < 3; i++)
            for (int j = 0; j < 3; j++)
               for (int k = 0; k < 3; k++)
                  for (int l = 0; l < 3; l++)
                     switch (node)
                     {
                        case 0:
                           System.out.printf("%2d ", v[l]);
                           break;
                        case 1:
                           System.out.printf("%2d ", v[k]);
                           break;
                        case 2:
                           System.out.printf("%2d ", v[j]);
                           break;
                        case 3:
                           System.out.printf("%2d ", v[i]);
                           break;
                        default:
                           System.out.printf("%2d ", 0);
                     }
         System.out.println("");
      }
   }
}
