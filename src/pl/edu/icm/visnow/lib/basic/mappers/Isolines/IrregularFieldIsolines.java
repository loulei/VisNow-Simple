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

package pl.edu.icm.visnow.lib.basic.mappers.Isolines;

import pl.edu.icm.visnow.datasets.CellArray;
import pl.edu.icm.visnow.datasets.CellSet;
import pl.edu.icm.visnow.datasets.IrregularField;
import pl.edu.icm.visnow.datasets.cells.Cell;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;

/**
 *
 * @author  Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class IrregularFieldIsolines
{
   protected IsolinesParams params;
   protected IrregularField inField  = null;
   protected IrregularField outField = null;
   
   public IrregularFieldIsolines(IrregularField inField, IsolinesParams params)
   {
      boolean[] activeCellSets = params.getActiveCellSets();
      int component = params.getComponent();
      float[] thresholds = params.getThresholds();
      outField = null;
      if (inField == null ||
          inField.getData(component) == null || inField.getData(component).getVeclen() != 1 ||
          activeCellSets == null || activeCellSets.length<1 || 
          thresholds == null || thresholds.length <1)
         return;
      this.inField = inField;
      int nCellSets = inField.getNCellSets();
      int[] nSegments = new int[nCellSets];
      for (int i = 0; i < nSegments.length; i++)
         nSegments[i] = 0;
      float[][] tCoords = new float[nCellSets][];
      float[][] tData = new float[nCellSets][];
      int nCells = 0;
      for (int i = 0; i < nCellSets; i++)
      {
         if (activeCellSets[i])
         {
            CellSetIsolines csi = new CellSetIsolines(inField, inField.getCellSet(i), component, thresholds);
            nSegments[i] = csi.getnSegs();
            tCoords[i]   = csi.getCoords();
            tData[i]     = csi.getOutData();
            nCells += nSegments[i];
         }
      }
      if (nCells == 0)
         return;
      int nVerts = 2*nCells;
      float[] coords = new float[3*nVerts];
      float[] fData = new float[nVerts];
      int[] cells = new int[nVerts];
      for (int i = 0; i < cells.length; i++)
         cells[i] = i;
      boolean[] orientations = new boolean[nCells];
      for (int i = 0; i < orientations.length; i++)
         orientations[i] = true;
      for (int i = 0, j = 0, k = 0; i < nCellSets; i++)
      {
         if (nSegments[i] > 0)
         {
            for (int l = 0; l < tData[i].length; j++, l++)
               fData[j] = tData[i][l];
            for (int l = 0; l < tCoords[i].length; k++, l++)
               coords[k] = tCoords[i][l];
         }
      }
      outField = new IrregularField();
      outField.setNNodes(nVerts);
      outField.setNSpace(3);
      outField.setCoords(coords);
      CellSet cellSet = new CellSet(inField.getName()+"isolines");
      DataArray da = DataArray.create(fData, 1, inField.getData(component).getName());
      outField.addData(da);
      CellArray skeletonSegments = new CellArray(Cell.SEGMENT, cells, orientations, null);
      cellSet.setBoundaryCellArray(skeletonSegments);
      cellSet.setCellArray(skeletonSegments);
      outField.addCellSet(cellSet);
   }
   
   public IrregularField getOutField()
   {
      return outField;
   }

}
