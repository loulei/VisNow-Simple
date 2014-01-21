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

package pl.edu.icm.visnow.lib.utils.field;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class RegularCellsTriangulation
{
   public static int[] triangulateRegularHex(int i0, int i1, int i2, int i3, int i4, int i5, int i6, int i7, boolean even)
   {
      if (even)
         return new int[]
         {
            i0, i2, i7, i5, 
            i1, i2, i0, i5, 
            i3, i0, i2, i7,
            i6, i5, i7, i2,
            i5, i4, i7, i0
         };
      else
         return new int[]
         {
            i1, i3, i4, i6,
            i0, i1, i3, i4,
            i2, i3, i1, i6,  
            i4, i5, i1, i6,
            i7, i6, i4, i3
         };
   }
   
   public static int[] triangulateRegularQuad(int i0, int i1, int i2, int i3)
   {
         return new int[]
         {
            i0, i1, i2, 
            i0, i2, i3
         };
   }

   private RegularCellsTriangulation()
   {
   }
   

}
