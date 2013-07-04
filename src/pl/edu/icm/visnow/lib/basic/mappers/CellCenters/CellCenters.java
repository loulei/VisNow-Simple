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

package pl.edu.icm.visnow.lib.basic.mappers.CellCenters;


import pl.edu.icm.visnow.datasets.CellArray;
import pl.edu.icm.visnow.datasets.CellSet;
import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.datasets.IrregularField;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.datasets.cells.Cell;
import pl.edu.icm.visnow.engine.core.InputEgg;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.lib.templates.visualization.modules.IrregularOutFieldVisualizationModule;
import pl.edu.icm.visnow.lib.types.VNField;
import pl.edu.icm.visnow.lib.types.VNIrregularField;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class CellCenters extends IrregularOutFieldVisualizationModule
{

   public static InputEgg[] inputEggs = null;
   public static OutputEgg[] outputEggs = null;  
   protected RegularField inRegularField = null;
   protected IrregularField inField = null;

   public CellCenters()
   {
      setPanel(ui);
      outObj.setName("cell centers");
   }


   
   private void createOutField()
   {
      if (inField == null)
         return;
      int nNodes = 0;
      int[] nSetNodes = new int[inField.getNCellSets()];
      for (int i = 0; i < nSetNodes.length; i++)
         nSetNodes[i] = 0;
      for (int i = 0; i < inField.getNCellSets(); i++)
      {
         CellSet cs = inField.getCellSet(i);
         for (int j = 0; j < Cell.TYPES; j++)
            if (cs.getCellArray(j) != null)
            {
            nNodes += cs.getCellArray(j).getNCells();
            nSetNodes[i] += cs.getCellArray(j).getNCells();
            }
      }
      outField = new IrregularField();
      outField.setNNodes(nNodes);
      outField.setNSpace(3);
      int inSpace = inField.getNSpace();
      float[] inCoords = inField.getCoords();
      float[] coords = new float[3*nNodes];
      int n = 0;
      float[] u = new float[3];
      for (int i = 0, iout = 0; i < inField.getNCellSets(); i++)
      {
         CellSet inCS = inField.getCellSet(i);
         CellSet outCS = new CellSet(inCS.getName());
         int[] outIndices = new int[nSetNodes[i]];
         boolean[] orientations = new boolean[nSetNodes[i]];
         int[] outNodes = new int[nSetNodes[i]];
         for (int j = 0, iCAout = iout; j < Cell.TYPES; j++)
            if (inCS.getCellArray(j) != null)
            {
               CellArray inCA = inCS.getCellArray(j);
               int[] nodes = inCA.getNodes();
               int nv = Cell.nv[j];
               for (int k = 0, m = 0; k < inCA.getNCells(); k++, iout++, m ++)
               {
                  for (int l = 0; l < inSpace; l++)
                  {
                     u[l] = 0;
                     for (int p = 0; p < nv; p++)
                     {
                        int ip = nodes[k * nv + p];
                        u[l] += inCoords[inSpace * ip + l];
                     }
                     u[l] /= nv;
                  }
                  System.arraycopy(u, 0, coords, 3 * iout, inSpace);
                  outNodes[m] = iout;
               }
               if (inCA.getDataIndices() != null)
               {
                  int[] inIndices = inCA.getDataIndices();
                  for (int k = 0; k < inCA.getNCells(); k++, iCAout++)
                     outIndices[iCAout] = inIndices[k];
               }
            }
         for (int j = 0; j < orientations.length; j++)
         {
            outIndices[j] = j;
            orientations[j] = true;
         }
         outField.setCoords(coords);
         CellArray outCA = new CellArray(Cell.POINT, outNodes, orientations, outIndices);
         outCS.setCellArray(outCA);
         for (int j = 0; j < inCS.getNData(); j++)
            outCS.addData(inCS.getData(j));
         if (inField.getNCellSets() == 1)
            for (int j = 0; j < inCS.getNData(); j++)
               outField.addData(inCS.getData(j));
         outField.addCellSet(outCS);
      }
   }

   @Override
   public void onActive()
   {
      if (getInputFirstValue("inField") == null)
         return;
         VNField input = ((VNField) getInputFirstValue("inField"));
         Field newInField = input.getField();
         if (newInField != null && inField != newInField)
         {
            inField = (IrregularField) newInField;
            outObj.clearAllGeometry();
            outGroup = null;
            outObj.setName(inField.getName());
            createOutField();
            outField.setExtents(inField.getExtents());
            prepareOutputGeometry();
            show();
         }
      setOutputValue("outField", new VNIrregularField(outField)); 
   }
}