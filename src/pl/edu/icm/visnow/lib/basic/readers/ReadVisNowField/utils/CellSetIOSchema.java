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
 exception statement from your version. */
//</editor-fold>
package pl.edu.icm.visnow.lib.basic.readers.ReadVisNowField.utils;

import pl.edu.icm.visnow.datasets.CellArray;
import pl.edu.icm.visnow.datasets.CellSet;
import pl.edu.icm.visnow.datasets.cells.Cell;

/**
 *
 * @author Krzysztof S. Nowinski University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class CellSetIOSchema
{

   private CellSet cellSet;
   /**
    * Cell set name duplicated for faster access
    */
   protected String name;
   protected int[][] cellNodeArrays = new int[Cell.TYPES][];
   protected int[][] cellIndexArrays = new int[Cell.TYPES][];
   protected boolean[][] cellOrientations = new boolean[Cell.TYPES][];

   public CellSetIOSchema(CellSet cellSet)
   {
      this.cellSet = cellSet;
      name = cellSet.getName();
      for (int i = 0; i < Cell.TYPES; i++)
      {
         CellArray ca = cellSet.getCellArray(i);
         if (ca == null)
         {
            cellNodeArrays[i]   = null;
            cellIndexArrays[i]  = null;
            cellOrientations[i] = null;
         } else
         {
            cellNodeArrays[i]   = ca.getNodes();
            cellIndexArrays[i]  = ca.getDataIndices();
            cellOrientations[i] = ca.getOrientations();
         }
      }
   }

   /**
    * Get the value of cellSet
    *
    * @return the value of cellSet
    */
   public CellSet getCellSet()
   {
      return cellSet;
   }

   /**
    * Set the value of cellSet
    *
    * @param cellSet new value of cellSet
    */
   public void setCellSet(CellSet cellSet)
   {
      this.cellSet = cellSet;
      name = cellSet.getName();
   }

   /**
    * Get the value of name
    *
    * @return the value of name
    */
   public String getName()
   {
      return name;
   }

   public int[] getCellNodes(String cellName)
   {
      if (Cell.idMap.containsKey(cellName))
         return cellNodeArrays[Cell.idMap.get(cellName)];
      return null;
   }

   public int[] getCellIndices(String cellName)
   {
      if (Cell.idMap.containsKey(cellName))
         return cellIndexArrays[Cell.idMap.get(cellName)];
      return null;
   }

   public boolean[] getOrientations(String cellName)
   {
      if (Cell.idMap.containsKey(cellName))
         return cellOrientations[Cell.idMap.get(cellName)];
      return null;
   }

}
