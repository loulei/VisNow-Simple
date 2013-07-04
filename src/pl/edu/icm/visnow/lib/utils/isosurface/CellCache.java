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

package pl.edu.icm.visnow.lib.utils.isosurface;

import pl.edu.icm.visnow.datasets.CellArray;
import pl.edu.icm.visnow.datasets.CellSet;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.gui.events.FloatValueModificationEvent;
import pl.edu.icm.visnow.gui.events.FloatValueModificationListener;
import pl.edu.icm.visnow.lib.utils.IntDataCache;

/**
 *
 * @author know
 */
public class CellCache 
{
   public static final int BUCKETS_NUMBER = 32768;
   
   public static final long NULL_CEILING_ARRAY = -1;
   public static final long WRONG_CEILING_ARRAY = -2;
   public static final long NULL_BUCKETS_ARRAY = -3;
   public static final long WRONG_BUCKETS_ARRAY = -4;
   
   private DataArray data;
   private CellSet cellSet ;
   private float[] bucketsCeilingFun ;
   private int[][] buckets;
   private transient FloatValueModificationListener statusListener = null;
   
   public CellCache(DataArray data, CellSet cellSet, float[] bucketsCeilingFun, int[][] buckets, FloatValueModificationListener statusListener)
   {
      this.data = data;
      this.cellSet = cellSet;
      this.bucketsCeilingFun = bucketsCeilingFun;
      this.buckets = buckets;
      this.statusListener = statusListener;
   }
   
   public long computeCache()
   {
      if (bucketsCeilingFun == null)
         return NULL_CEILING_ARRAY;
      if (bucketsCeilingFun.length != BUCKETS_NUMBER)
         return WRONG_CEILING_ARRAY;
      if (buckets == null)
         return NULL_BUCKETS_ARRAY;
      if (buckets.length != BUCKETS_NUMBER)
         return WRONG_BUCKETS_ARRAY;
      float dataMin = data.getMinv();
      float dataMax = data.getMaxv();
      float[] isoData = data.getNormFData();
      float bucketFactor = BUCKETS_NUMBER / (dataMax - dataMin);

      //TODO który cellSet? Wybór w parametrach?
      //each cellArray in cellSet contains different types of cells)
      //we are interested only in 3D cell types(4-7 indexes, see class Cell)

      //count how many cells we are going to get
      int finalNCells = 0;
      byte[] cellsAfterTriangulation =
      {
         1, 2, 3, 6
      };
      for (int caInd = 4; caInd < 8; ++caInd)
      {
         CellArray ca = cellSet.getCellArray(caInd);
         if (ca != null)
         {
            finalNCells += ca.getNCells() * cellsAfterTriangulation[caInd - 4];
         }
      }

      /* This solution uses algorithm which optimizes access to cells by data value
       * (used data structure is built once, then used for all possible threshold values)
       *
       * Cells are mapped to a 2D plane (min data value of a cell VS max data value)
       * Plane is triangular (all cells are above diagonal).
       * Is is likely that for a single cell max and min data values will be close,
       * then all cells should be near to diagonal of the plane.
       *
       * Search by threshold value should only consider cells that have min<thr && max>thr,
       * which cuts out only small part of diagonal high-density region.
       *
       * 'Min value' axis is discretized into buckets, cells in buckets are not sorted
       */
      IntDataCache[] bucketsNodes = new IntDataCache[BUCKETS_NUMBER];
      int cellCacheSize = (int) Math.max((float) finalNCells / BUCKETS_NUMBER * 0.6f, 100.0f);
      for (int i = 0; i < BUCKETS_NUMBER; ++i)
      {
         bucketsNodes[i] = new IntDataCache(cellCacheSize, 4);
         bucketsCeilingFun[i] = Float.MIN_VALUE;
      }

      //process cells
      int totalCells = 0;
      int cellsProcessed = 0;
      for (int caInd = 4; caInd < 8; ++caInd)
      {
         CellArray ca = cellSet.getCellArray(caInd);
         if (ca != null)
         {
            CellArray triangulated = ca.triangulate(); //composed of Tetra cells
            int nCells = triangulated.getNCells();
            totalCells += nCells;
            int[] nodes = triangulated.getNodes(); //indexes of nodes
            assert nodes.length == nCells * 4;
            for (int cellInd = 0; cellInd < nCells; ++cellInd)
            { //for every tetra cell
               float min = isoData[nodes[cellInd * 4]];
               float max = min;
               for (int i = 1; i < 4; i++)
               {
                  float t =  isoData[nodes[cellInd * 4 +i]];
                  if (max < t) max = t;
                  if (min > t) min = t;
               }
               int bucketNum = (int) ((min - dataMin) * bucketFactor);
               if (bucketNum >= BUCKETS_NUMBER)
               {
                  bucketNum = BUCKETS_NUMBER - 1;
               }
               bucketsCeilingFun[bucketNum] = Math.max(bucketsCeilingFun[bucketNum], max);
               bucketsNodes[bucketNum].put(nodes, cellInd * 4);
               cellsProcessed++;
               if (cellsProcessed % 100 == 0)
               {
                  fireStatusChanged(0.9f * cellsProcessed / finalNCells);
               }
            }
         }
      }

      //sort data in buckets and get raw nodes data
      int maxBucketLength = 0;
      for (int i = 0; i < BUCKETS_NUMBER; ++i)
      {
         buckets[i] = bucketsNodes[i].getContigous();
         if (buckets[i].length > maxBucketLength)
            maxBucketLength = buckets[i].length;
         fireStatusChanged(0.9f + (0.1f * i / BUCKETS_NUMBER));
      }
      System.out.println("" + (float)maxBucketLength / totalCells);

      //compute ceiling function
      for (int i = 1; i < BUCKETS_NUMBER; ++i)
      {
         bucketsCeilingFun[i] = Math.max(bucketsCeilingFun[i - 1], bucketsCeilingFun[i]);
      }
      return System.currentTimeMillis();
   }

    protected void fireStatusChanged(float status) {
        if (statusListener != null) {
            FloatValueModificationEvent e = new FloatValueModificationEvent(this, status, true);
            statusListener.floatValueChanged(e);
        }
    }
}
