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

package pl.edu.icm.visnow.datasets;

import static java.lang.Math.*;

/**
 *
 * @author Krzysztof S. Nowinski
 *   <p>   University of Warsaw, ICM
 */
public class GeoTreeNode implements Runnable
{   
   public static final int MAX_CLASS = 4;
   private static final int nBins = 16385;
   protected int level = 0;
   protected int maxParallelLevel = -1;
   protected boolean fullySplit = false;
   protected int axis =  -1;
   protected float threshold = 0;
   protected int[] cells;
   protected GeoTreeNode nodeBelow = null;
   protected GeoTreeNode nodeAbove = null;
   private float[][] cellExtents;
   private int dim = 3;
   
   public GeoTreeNode(int dim, int[] cells, float[][] cellExtents)
   {
      this.dim = dim;
      this.cells = cells;
      this.cellExtents = cellExtents;
   }
   
   public GeoTreeNode(int dim, int[] cells, float[][] cellExtents, int level, int maxParallelLevel)
   {
      this.dim = dim;
      this.cells = cells;
      this.cellExtents = cellExtents;
      this.level = level;
      this.maxParallelLevel = maxParallelLevel;
   }
   
   public GeoTreeNode(int[] cells, float[][] cellExtents)
   {
      this(3, cells, cellExtents);
   }
   
   public GeoTreeNode(int[] cells, float[][] cellExtents, int level, int maxParallelLevel)
   {
      this(3, cells, cellExtents, level, maxParallelLevel);
   }
   
   private void split()
   {
      if (cells == null || cells.length < MAX_CLASS)
         return;      
      float[] cellsLow, cellsUp;
      int[] below = new int[nBins];
      int[] above = new int[nBins];
      int bestAxis = -1;
      int bestBalance = Integer.MAX_VALUE;
      int bestAbove = -1, bestBelow = -1;
      float bestThr = 0;
      for (int ax = 0; ax < dim; ax++)
      {
         cellsLow = cellExtents[ax];
         cellsUp  = cellExtents[ax + 3];
         float low = Float.MAX_VALUE;
         float up  = -Float.MAX_VALUE;
         for (int i = 0; i < cells.length; i++)
         {
            if (cellsLow[cells[i]] < low) low = cellsLow[cells[i]];
            if (cellsUp[cells[i]]  > up)  up  = cellsUp[cells[i]];
         }
         float delta = (nBins-1)/(up - low);
         for (int i = 0; i < above.length; i++)
            above[i] = below[i] = 0;
         for (int i = 0; i < cells.length; i++)
         {
            above[max(0, min(nBins-1,(int)((cellsUp[cells[i]] - low) * delta)))] += 1;
            below[max(0, min(nBins-1,(int)(ceil((cellsLow[cells[i]] - low) * delta))))] += 1;
         }
         for (int i = 1; i < below.length; i++)
            below[i] += below[i - 1];
         for (int i = above.length - 2; i >= 0; i--)
            above[i] += above[i + 1];
         int balance = 0;
         for (int i = 0; i < above.length - 1; i++)
            if (above[i] <= below[i])
            {
               balance = above[i] + below[i];
               if (balance < bestBalance)
               {
                  bestBalance = balance;
                  bestAxis    = ax;
                  bestThr     = low + i / delta;
                  bestAbove   = above[i];
                  bestBelow   = below[i];
               }
               break;
            }
      }
      if (bestAbove > .9 * cells.length || bestBelow > .9 * cells.length)
         return;
      axis = bestAxis;
      threshold = bestThr;

      int[] c = new int[cells.length];
      int j = 0;
      cellsLow = cellExtents[axis];
      cellsUp  = cellExtents[axis + 3];
      for (int i = 0; i < cells.length; i++)
         if (cellsLow[cells[i]] < threshold)
         {
            c[j] = cells[i];
            j += 1;
         }
      int[] cellsBelow = new int[j];
      System.arraycopy(c, 0, cellsBelow, 0, cellsBelow.length);
      j = 0;
      for (int i = 0; i < cells.length; i++)
         if (cellsUp[cells[i]] >= threshold)
         {
            c[j] = cells[i];
            j += 1;
         }
      int[] cellsAbove = new int[j];
      System.arraycopy(c, 0, cellsAbove, 0, cellsAbove.length);
      if (cellsBelow.length > .8 * cells.length || cellsAbove.length > .8 * cells.length)
         return;
      nodeBelow = new GeoTreeNode(cellsBelow, cellExtents, level + 1, maxParallelLevel);
      nodeAbove  = new GeoTreeNode(cellsAbove, cellExtents, level + 1, maxParallelLevel);
      cells = null;
   }
   
   @Override
   public void run()
   {
      if (level < maxParallelLevel)
         split();
      else
         splitFully();
   }
   
   public int[] getCells()
   {
      return cells;
   }

   public GeoTreeNode getNodeBelow()
   {
      return nodeBelow;
   }

   public GeoTreeNode getNodeAbove()
   {
      return nodeAbove;
   }

   public boolean isFullySplit()
   {
      return fullySplit;
   }
   
   public int[] getCells(float[] p)
   {
      if (cells != null)
         return cells;
      if (p[axis] < threshold)
         return nodeBelow.getCells(p);
      else
         return nodeAbove.getCells(p);
   }
   
   public void splitFully ()
   {
      split();
      if (cells == null)
      {
         nodeBelow.splitFully();
         nodeAbove.splitFully();
      }
      fullySplit = true;
   }
}
