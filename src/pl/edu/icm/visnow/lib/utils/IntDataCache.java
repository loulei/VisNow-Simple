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

package pl.edu.icm.visnow.lib.utils;

import java.util.Vector;

public class IntDataCache
{

   private final int dataCacheSize;
   private final int cacheSize;
   private final int veclen;
   private int nElements = 0;
   private Vector<int[]> elements = new Vector<int[]>();
   private int[] currentArr;
   private int currentIndex;

   public IntDataCache(int cacheSize, int veclen)
   {
      super();
      this.cacheSize = cacheSize;
      dataCacheSize = cacheSize * veclen;
      this.veclen = veclen;
      currentArr = new int[dataCacheSize];
      elements.add(currentArr);
      currentIndex = 0;
   }

   /**
    *
    * @param values
    * @return Index of inserted element
    */
   public int put(int... values)
   {
      assert values.length == veclen;
      return put(values, 0);
   }

   public int put(int[] data, int offset)
   {
      if (currentIndex == dataCacheSize)
      {
         currentArr = new int[dataCacheSize];
         elements.add(currentArr);
         currentIndex = 0;
      }
      System.arraycopy(data, offset, currentArr, currentIndex, veclen);
      currentIndex += veclen;
      return nElements++;
   }

   public int[] getContigous()
   {
      if (nElements > 0)
      {
         int[] outData = new int[veclen * nElements];
         int outIndex = 0;
         int lastEBlock = elements.size() - 1;

         //rewrite full blocks
         for (int i = 0; i < lastEBlock; ++i)
         {
            System.arraycopy(elements.get(i), 0, outData, outIndex, dataCacheSize);
            outIndex += dataCacheSize;
         }

         //rewrite last block
         int lastNElements = (nElements % cacheSize) * veclen;
         if (lastNElements == 0)
            lastNElements = dataCacheSize;
         System.arraycopy(elements.get(lastEBlock), 0, outData, outIndex, lastNElements);

         return outData;
      }
      return new int[0];
   }

   public int nElements()
   {
      return nElements;
   }
}
