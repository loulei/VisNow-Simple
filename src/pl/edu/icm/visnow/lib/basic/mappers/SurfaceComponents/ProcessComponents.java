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

package pl.edu.icm.visnow.lib.basic.mappers.SurfaceComponents;

import java.util.ArrayList;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.log4j.Logger;
import pl.edu.icm.visnow.datasets.cells.Cell;
import pl.edu.icm.visnow.datasets.CellArray;
import pl.edu.icm.visnow.datasets.CellSet;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.datasets.IrregularField;
import pl.edu.icm.visnow.gui.events.FloatValueModificationEvent;
import pl.edu.icm.visnow.gui.events.FloatValueModificationListener;
import pl.edu.icm.visnow.lib.utils.numeric.HeapSort;

/**
 *
 * @author  Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class ProcessComponents
{

   private IrregularField inField, outField;
   private int[][] cellSetComponents = null;
   private int[][] cellSetComponentSizes = null;
   private int[] cellSetNComponents = null;

   private static Logger logger = Logger.getLogger(ProcessComponents.class);

   public ProcessComponents()
   {
   }

   public void setInField(IrregularField inField, Params params)
   {
      this.inField = inField;
      cellSetComponents = new int[inField.getNCellSets()][inField.getNNodes()];   //holder for component indices: cellSetComponents[i][j] = k iff node j belongs to kth component of cell set i
      cellSetComponentSizes = new int[inField.getNCellSets()][];                  //holder for component sizes:   cellSetComponentSizes[i][2*j] is number of vertices in jth component of cell set i,
                                                                                  //for sorting purposes:         cellSetComponentSizes[i][2*j+1] is component number
      cellSetNComponents = new int[inField.getNCellSets()];                       //holder for component numbers: cellSetNComponents[i] is number of components of cell set i
      for (int i = 0; i < inField.getNCellSets(); i++)
         split(i);
   }

   private void addTriangleNeighbors(int[] pNeighb, int[] neighbInd, int i0, int i1, int i2)
   {
      for (int j = neighbInd[i0]; j < neighbInd[i0 + 1]; j++)
      {
         if (pNeighb[j] == i1)
            break;
         if (pNeighb[j] == -1)
         {
            pNeighb[j] = i1;
            break;
         }
      }
      for (int j = neighbInd[i0]; j < neighbInd[i0 + 1]; j++)
      {
         if (pNeighb[j] == i2)
            break;
         if (pNeighb[j] == -1)
         {
            pNeighb[j] = i2;
            break;
         }
      }
   }

   private void addQuadNeighbors(int[] pNeighb, int[] neighbInd, int i0, int i1, int i2, int i3)
   {
      for (int j = neighbInd[i0]; j < neighbInd[i0 + 1]; j++)
      {
         if (pNeighb[j] == i1)
            break;
         if (pNeighb[j] == -1)
         {
            pNeighb[j] = i1;
            break;
         }
      }
      for (int j = neighbInd[i0]; j < neighbInd[i0 + 1]; j++)
      {
         if (pNeighb[j] == i2)
            break;
         if (pNeighb[j] == -1)
         {
            pNeighb[j] = i2;
            break;
         }
      }
      for (int j = neighbInd[i0]; j < neighbInd[i0 + 1]; j++)
      {
         if (pNeighb[j] == i3)
            break;
         if (pNeighb[j] == -1)
         {
            pNeighb[j] = i3;
            break;
         }
      }
   }

   private void split(int nSet)
   {
      int[] neighb;
      int[] neighbInd;
      int n = 0, k0;
      int[] tris = null;
      int[] quads = null;
      int[] pNeighb = null;
      CellSet cs = inField.getCellSet(nSet);

      neighbInd = new int[inField.getNNodes() + 1];
      for (int i = 0; i < neighbInd.length; i++)
         neighbInd[i] = 0;

      CellArray triangleArray = cs.getBoundaryCellArray(Cell.TRIANGLE);
      if (triangleArray != null && triangleArray.getNCells() > 0)
      {
         tris = triangleArray.getNodes();
         for (int i = 0; i < tris.length; i++)
            neighbInd[tris[i]] += 2;
         n += tris.length;
      }
      CellArray quadArray = cs.getBoundaryCellArray(Cell.QUAD);
      if (quadArray != null && quadArray.getNCells() > 0)
      {
         quads = quadArray.getNodes();
         for (int i = 0; i < quads.length; i++)
            neighbInd[quads[i]] += 3;
         n += 2 * quads.length;
      }

      int k = 0;
      for (int i = 0; i < neighbInd.length; i++)
      {
         int j = k + neighbInd[i];
         neighbInd[i] = k;
         k = j;
      }

      pNeighb = new int[k];

      for (int i = 0; i < pNeighb.length; i++)
         pNeighb[i] = -1;
      if (tris != null)
         for (int i = 0; i < tris.length; i += 3)
         {
            fireStatusChanged(i / (2.f * n));
            addTriangleNeighbors(pNeighb, neighbInd, tris[i], tris[i + 1], tris[i + 2]);
            addTriangleNeighbors(pNeighb, neighbInd, tris[i + 2], tris[i], tris[i + 1]);
            addTriangleNeighbors(pNeighb, neighbInd, tris[i + 1], tris[i + 2], tris[i]);
         }
      if (quads != null)
         for (int i = 0; i < quads.length; i += 4)
         {
            fireStatusChanged((2 * i + tris.length / 3) / (2.f * n));
            addQuadNeighbors(pNeighb, neighbInd, quads[i], quads[i + 1], quads[i + 2], quads[i + 3]);
            addQuadNeighbors(pNeighb, neighbInd, quads[i + 3], quads[i], quads[i + 1], quads[i + 2]);
            addQuadNeighbors(pNeighb, neighbInd, quads[i + 2], quads[i + 3], quads[i], quads[i + 1]);
            addQuadNeighbors(pNeighb, neighbInd, quads[i + 1], quads[i + 2], quads[i + 3], quads[i]);
         }


      k = 0;
      for (int i = 0; i < pNeighb.length; i++)
         if (pNeighb[i] != -1)
            k += 1;
      neighb = new int[k];

      k = 0;
      for (int i = 0; i < neighbInd.length - 1; i++)
      {
         k0 = k;
         for (int j = neighbInd[i]; j < neighbInd[i + 1]; j++)
         {
            if (pNeighb[j] == -1)
               break;
            neighb[k] = pNeighb[j];
            k += 1;
         }
         neighbInd[i] = k0;
      }

      neighbInd[neighbInd.length - 1] = k;
      pNeighb = null;
      int nNodes = inField.getNNodes();
      int[] components = cellSetComponents[nSet];
      int[] stack = new int[nNodes];
      int stackSize = -1;
      for (int i = 0; i < nNodes; i++)
         components[i] = -1;
      int comp = 0;
      for (int seed = 0; seed < components.length; comp++)
      {
         components[seed] = comp;
         stackSize = 0;
         stack[stackSize] = seed;
         while (stackSize >= 0)
         {
            int current = stack[stackSize];
            stackSize -= 1;
            components[current] = comp;
            for (int j = neighbInd[current]; j < neighbInd[current + 1]; j++)
            {
               k = neighb[j];
               if (components[k] == -1)
               {
                  stackSize += 1;
                  stack[stackSize] = k;
                  components[k] = comp;
               }
            }
         }
         while (seed < nNodes && components[seed] != -1)
            seed += 1;  //looking for first node not yet assigned to any component
      }
      stack = null;
      cellSetNComponents[nSet] = comp;
      cellSetComponentSizes[nSet] = new int[2 * comp];
      for (int i = 0; i < comp; i++)
      {
         cellSetComponentSizes[nSet][2 * i]     = 0;
         cellSetComponentSizes[nSet][2 * i + 1] = i;
      }
      for (int i = 0; i < nNodes; i++)
         cellSetComponentSizes[nSet][2 * components[i]] += 1;
      HeapSort.sort(cellSetComponentSizes[nSet], 2, false);
      fireStateChanged();
   }


   public IrregularField buildOutput(Params params)
   {
      if (params == null || inField == null)
         return null;
      int nSeparateComponents = params.getSeparateComponents();
      outField = inField.cloneCoords();
      CellArray inCellArray = null;
      boolean[] inOrientations = null;
      int[] inCells = null;
      int[] maxComponentIndices = new int[cellSetComponentSizes.length];       //maxComponentIndices[i] is the index of largest component not yet assigned to output
      for (int i = 0; i < maxComponentIndices.length; i++)
         maxComponentIndices[i] = 0;
orderingLoop:
      for (int nSeparate = 0; nSeparate < nSeparateComponents; nSeparate++)
      {  
         fireStatusChanged(.5f + .5f*(nSeparate + 1.f)/nSeparateComponents);
         int maxComponentIndex = -1;
         int maxComponentSize = params.getMinComponentSize()-1;
         for (int nSet = 0; nSet < cellSetComponentSizes.length; nSet++)
            if (maxComponentIndices[nSet] < cellSetComponentSizes[nSet].length/2  &&
                cellSetComponentSizes[nSet][2 * maxComponentIndices[nSet]] > maxComponentSize)
            {
               maxComponentIndex = nSet;
               maxComponentSize = cellSetComponentSizes[nSet][maxComponentIndices[nSet]];
            }
         if (maxComponentIndex == -1)
            break orderingLoop;
         int maxComponentMark = cellSetComponentSizes[maxComponentIndex][2 * maxComponentIndices[maxComponentIndex] + 1];
         maxComponentIndices[maxComponentIndex] += 1;
         CellSet inCS  = inField.getCellSet(maxComponentIndex);
         CellSet outCS = new CellSet("component_" + nSeparate);
         if (inCS.getBoundaryCellArray(Cell.TRIANGLE) != null)
         {
            inCellArray = inCS.getBoundaryCellArray(Cell.TRIANGLE);
            inCells = inCellArray.getNodes();
            inOrientations = inCellArray.getOrientations();
            int nCells = 0;
            for (int i = 0; i < inCells.length; i += 3)
               if (cellSetComponents[maxComponentIndex][inCells[i]] == maxComponentMark)
                  nCells += 1;
            if (nCells == 0)
               continue;
            int[] outCells = new int[3 * nCells];
            boolean[] outOrientations = new boolean[nCells];
            for (int i = 0, k = 0; i < inCells.length / 3; i++)
               if (cellSetComponents[maxComponentIndex][inCells[3 * i]] == maxComponentMark)
               {
                  for (int j = 0; j < 3; j++)
                     outCells[3 * k + j] = inCells[3 * i + j];
                  outOrientations[k] = inOrientations[i];
                  k += 1;
               }
            CellArray outCellArray = new CellArray(Cell.TRIANGLE, outCells, outOrientations, null);
            outCS.setCellArray(outCellArray);
            outCS.setBoundaryCellArray(outCellArray);
         }
         if (inCS.getBoundaryCellArray(Cell.QUAD) != null)
         {
            inCellArray = inCS.getBoundaryCellArray(Cell.QUAD);
            inCells = inCellArray.getNodes();
            inOrientations = inCellArray.getOrientations();
            int nCells = 0;
            for (int i = 0; i < inCells.length; i += 3)
               if (cellSetComponents[maxComponentIndex][inCells[i]] == maxComponentMark)
                  nCells += 1;
            if (nCells == 0)
               continue;
            int[] outCells = new int[4 * nCells];
            boolean[] outOrientations = new boolean[nCells];
            for (int i = 0, k = 0; i < inCells.length / 4; i++)
               if (cellSetComponents[maxComponentIndex][inCells[4 * i]] == maxComponentMark)
               {
                  for (int j = 0; j < 4; j++)
                     outCells[4 * k + j] = inCells[4 * i + j];
                  outOrientations[k] = inOrientations[i];
                  k += 1;
               }
            CellArray outCellArray = new CellArray(Cell.QUAD, outCells, outOrientations, null);
            outCS.setCellArray(outCellArray);
            outCS.setBoundaryCellArray(outCellArray);
         }
         if (inCS.getBoundaryCellArray(Cell.QUAD) != null || inCS.getBoundaryCellArray(Cell.TRIANGLE) != null)
         {
            outCS.generateDisplayData(outField.getCoords());
            logger.debug("adding"+outCS);
            outField.addCellSet(outCS);
         }
      }

      int[] componentNumber = new int[inField.getNNodes()];
      for (int i = 0; i < componentNumber.length; i++)
         componentNumber[i] = 0;
      for (int i = 0; i < outField.getNCellSets(); i++)
      {
         CellSet cs = outField.getCellSet(i);
         for (int j = 0; j < cs.getBoundaryCellArrays().length; j++)
         {
            CellArray cArr = cs.getBoundaryCellArray(j);
            if (cArr != null && cArr.getNodes() != null)
            {
               int[] cN = cArr.getNodes();
               for (int k = 0; k < cN.length; k++)
                  componentNumber[cN[k]] = i;
            }
         }
         for (int j = 0; j < cs.getCellArrays().length; j++)
         {
            CellArray cArr = cs.getCellArray(j);
            if (cArr != null && cArr.getNodes() != null)
            {
               int[] cN = cArr.getNodes();
               for (int k = 0; k < cN.length; k++)
                  componentNumber[cN[k]] = i;
            }
         }
      }
      outField.addData(DataArray.create(componentNumber, 1, "component index"));
      for (int i = 0; i < inField.getNData(); i++)
         outField.addData(inField.getData(i));
 
      return outField;
   }

   public IrregularField getOutField()
   {
      return outField;
   }

   public IrregularField getInField()
   {
      return inField;
   }

   private transient FloatValueModificationListener statusListener = null;

   public void addFloatValueModificationListener(FloatValueModificationListener listener)
   {
      if (statusListener == null)
         this.statusListener = listener;
      else
         logger.error("" + this + ": only one status listener can be added");
   }

   private void fireStatusChanged(float status)
   {
      FloatValueModificationEvent e = new FloatValueModificationEvent(this, status, true);
      if (statusListener != null)
         statusListener.floatValueChanged(e);
   }
   /**
    * Utility field holding list of ChangeListeners.
    */
   private transient ArrayList<ChangeListener> changeListenerList =
           new ArrayList<ChangeListener>();

   /**
    * Registers ChangeListener to receive events.
    * @param listener The listener to register.
    */
   public synchronized void addChangeListener(ChangeListener listener)
   {
      changeListenerList.add(listener);
   }

   /**
    * Removes ChangeListener from the list of listeners.
    * @param listener The listener to remove.
    */
   public synchronized void removeChangeListener(ChangeListener listener)
   {
      changeListenerList.remove(listener);
   }

   /**
    * Notifies all registered listeners about the event.
    *
    * @param object Parameter #1 of the <CODE>ChangeEvent<CODE> constructor.
    */
   public void fireStateChanged()
   {
      ChangeEvent e = new ChangeEvent(this);
      for (ChangeListener listener : changeListenerList)
         listener.stateChanged(e);
   }

   public int[][] getCellSetComponentSizes()
   {
      return cellSetComponentSizes;
   }


}
