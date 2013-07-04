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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;
import pl.edu.icm.visnow.datasets.CellArray;
import pl.edu.icm.visnow.datasets.CellSet;
import pl.edu.icm.visnow.datasets.IrregularField;
import pl.edu.icm.visnow.datasets.cells.Cell;
import pl.edu.icm.visnow.datasets.cells.Hex;
import pl.edu.icm.visnow.datasets.cells.Prism;
import pl.edu.icm.visnow.datasets.cells.Pyramid;
import pl.edu.icm.visnow.datasets.cells.Quad;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import static pl.edu.icm.visnow.lib.utils.numeric.SliceLookupTable.*;

/**
 *
 * @author know
 */ 
public class IrregularFieldSplitter 
{
   protected static final int CHUNK = 4096;
   
   class NewNode
   {
      public int p0, p1, index;
      public float ratio;

      public NewNode(int index, int p0, int p1, float ratio)
      {
         this.index = index;
         this.p0 = p0;
         this.p1 = p1;
         this.ratio = ratio;
      }
      
      public long getHash()
      {
         return (long)p1 << 32 | (long)p0;
      }
   }
   
   protected HashMap<Long, NewNode> newNodes = new HashMap<Long, NewNode>();
   
   protected IrregularField inField;
   protected int position = 0;
   
   protected boolean[] usedNodes;
   protected int nInNodes;
   protected int nOldNodes;
   protected int nNewNodes;
   protected int nOutNodes;
   protected int[] globalNewNodeIndices;
   
   protected int totalNewNodes = 0;
   
   protected int totalOutputCells = 0;
   
   protected Vector<Vector<int[]>[]> newCells = new Vector<Vector<int[]>[]>();
   protected Vector<Vector<int[]>[]> newCellDataIndices = new Vector<Vector<int[]>[]>();
   protected Vector<int[]> totalCellsInSets = new Vector<int[]>();
   
   protected int[] totalNewCells = new int[Cell.TYPES];
   protected int[] currentNewCells = new int[Cell.TYPES];
   protected Vector<int[]>[] newCellsInSet;
   protected Vector<int[]>[] newCellDataIndicesInSet;
   
   protected int cellType;
   protected int nCellNodes;
   protected int[] nodes;
   protected int[] dataIndices;
   
   protected NewNode[] newNodesArray;
   protected IrregularField outField;   
   
   public IrregularFieldSplitter(IrregularField inField, int position)
   {
      this.inField = inField;
      this.position = position;
      nInNodes = inField.getNNodes();
      usedNodes = new boolean[nInNodes];
      nOldNodes = nNewNodes = nOutNodes = 0;
      nNewNodes = 0;
      totalOutputCells = 0;
      totalCellsInSets.clear();
   }
   
   @SuppressWarnings("unchecked")
   public void initCellSetSplit(CellSet trCS)
   {
      Arrays.fill(totalNewCells, 0);
      Arrays.fill(currentNewCells, CHUNK);
      newCellsInSet = new Vector[Cell.TYPES];
      newCellDataIndicesInSet = new Vector[Cell.TYPES];
      for (int i = 0; i < Cell.TYPES; i++)
      {
         newCellsInSet[i] = new Vector<int[]>();
         newCellDataIndicesInSet[i] = new Vector<int[]>();
      }
      newCells.add(newCellsInSet);
      newCellDataIndices.add(newCellDataIndicesInSet);
      totalNewCells = new int[Cell.TYPES];
      totalNewCells = new int[Cell.TYPES];
      totalCellsInSets.add(totalNewCells);
   }
   
   public void initCellArraySplit(CellArray ca)
   {
      cellType = ca.getType();
      nCellNodes = Cell.nv[cellType];
      nodes = ca.getNodes();
      dataIndices = ca.getDataIndices();
   }
   
   public void processSimplex(int iCell, float[] vals, int cellDataIndex)
   {
      int[] slicedCell = new int[nCellNodes];
      System.arraycopy(nodes, iCell * nCellNodes, slicedCell, 0, nCellNodes);
      processSimplex(slicedCell,  vals, cellDataIndex);
   }
   
   public void processSimplex(int[] slicedSimplex, float[] vals, int cellDataIndex)
   {
      int subcellType;
      if (position != 0)
         subcellType = getSubcellType(cellType, vals, position > 0);
      else
         subcellType = getSliceType(cellType, vals);
      if (subcellType < 0)
         return;
      int[] subcellNodes;
      if (position != 0)
         subcellNodes = getSubcellNodes(cellType, vals, position > 0);
      else
         subcellNodes = getSliceNodes(cellType, vals);
      int currentCell = currentNewCells[subcellType];
      int[] currentCells;
      int[] currentDataIndices;
      if (currentCell >= CHUNK)
      {
         currentDataIndices = new int[CHUNK];
         currentCells = new int[Cell.nv[subcellType] * CHUNK];
         newCellsInSet[subcellType].add(currentCells);
         newCellDataIndicesInSet[subcellType].add(currentDataIndices);
         currentCell = currentNewCells[subcellType] = 0; 
      }
      else
      {
         currentCells = newCellsInSet[subcellType].get(newCellsInSet[subcellType].size() - 1);
         currentDataIndices = newCellDataIndicesInSet[subcellType].get(newCellsInSet[subcellType].size() - 1);
      }
      int subcellSize = subcellNodes.length;
      for (int l = 0; l < subcellNodes.length; l++)
      {
         if (subcellNodes[l] < nCellNodes)
         {
            int node = slicedSimplex[subcellNodes[l]];
            currentCells[subcellSize * currentCell + l] = node;
            usedNodes[node] = true;
         }
         else
         {
            int n0 = addNodes[nCellNodes][subcellNodes[l]][0];
            int n1 = addNodes[nCellNodes][subcellNodes[l]][1];
            int p0 = slicedSimplex[n0];
            int p1 = slicedSimplex[n1];
            long key = p0 < p1 ? (long)p1 << 32 | (long)p0 : (long)p0 << 32 | (long)p1;
            
            NewNode node = newNodes.get(key);
            if (node == null)
            {
               newNodes.put(key, new NewNode(nNewNodes, p0, p1, 
                                             vals[n1] / (vals[n1] -  vals[n0])));
               currentCells[subcellSize * currentCell + l] = nInNodes + nNewNodes;
               nNewNodes += 1;
            }
            else
            {
               currentCells[subcellSize * currentCell + l] = nInNodes + node.index;
            }
         }
      }
      currentDataIndices[currentCell] = cellDataIndex;
      totalNewCells[subcellType] += 1;
      currentNewCells[subcellType] += 1;
      totalOutputCells += 1;
   }
   
   public void processCell(int iCell, float[] vals)
   {
      int[] slicedCell = new int[nCellNodes];
      System.arraycopy(nodes, iCell * nCellNodes, slicedCell, 0, nCellNodes);
      if (dataIndices != null)
         processCell(slicedCell, vals, dataIndices[iCell]);
      else
         processCell(slicedCell, vals, -1);
   }
   
   public void processCell(int[] cell, float[] vals, int cellDataIndex)
   {
      if (Cell.isSimplexType[cellType])
         processSimplex(cell, vals, cellDataIndex);
      else
      {
         for (int l = 0; l < cell.length; l++)
            usedNodes[cell[l]] = true;
         int newCellType;
         int[][] cellTriangulation;
         int[][] triangIndices;
         switch (cellType)
         {
         case Cell.QUAD:
            newCellType = Cell.TRIANGLE;
            cellTriangulation = Quad.triangulationVertices(cell);
            triangIndices     = Quad.triangulationIndices(cell);
            break;
         case Cell.PYRAMID:
            newCellType = Cell.TETRA;
            cellTriangulation = Pyramid.triangulationVertices(cell);
            triangIndices     = Pyramid.triangulationIndices(cell);
            break;
         case Cell.PRISM:
            newCellType = Cell.TETRA;
            cellTriangulation = Prism.triangulationVertices(cell);
            triangIndices     = Prism.triangulationIndices(cell);
            break;
         case Cell.HEXAHEDRON:
            newCellType = Cell.TETRA;
            cellTriangulation = Hex.triangulationVertices(cell);
            triangIndices     = Hex.triangulationIndices(cell);
            break;
         default:
            return;
         }
         int nSimplexNodes = Cell.nv[newCellType];
         for (int iSimplex = 0; iSimplex < cellTriangulation.length; iSimplex++)
         {
            int[] slicedSimplex = cellTriangulation[iSimplex];
            float[] slicedVals = new float[nSimplexNodes];
            for (int i = 0; i < nSimplexNodes; i++)
               slicedVals[i] = vals[triangIndices[iSimplex][i]];
            int subcellType;
            if (position != 0)
               subcellType = getSubcellType(newCellType, slicedVals, position > 0);
            else
               subcellType = getSliceType(newCellType, slicedVals);
            if (subcellType < 0)
               continue;
            int[] subcellNodes;
            if (position != 0)
               subcellNodes = getSubcellNodes(newCellType, slicedVals, position > 0);
            else
               subcellNodes = getSliceNodes(newCellType, slicedVals);
            int currentCell = currentNewCells[subcellType];
            int[] currentCells;
            int[] currentDataIndices;
            if (currentCell >= CHUNK)
            {
               currentDataIndices = new int[CHUNK];
               currentCells = new int[Cell.nv[subcellType] * CHUNK];
               newCellsInSet[subcellType].add(currentCells);
               newCellDataIndicesInSet[subcellType].add(currentDataIndices);
               currentCell = currentNewCells[subcellType] = 0; 
            }
            else
            {
               currentCells = newCellsInSet[subcellType].get(newCellsInSet[subcellType].size() - 1);
               currentDataIndices = newCellDataIndicesInSet[subcellType].get(newCellsInSet[subcellType].size() - 1);
            }
            int subcellSize = subcellNodes.length;
            for (int l = 0; l < subcellNodes.length; l++)
            {
               if (subcellNodes[l] < nSimplexNodes)
               {
                  int node = slicedSimplex[subcellNodes[l]];
                  currentCells[subcellSize * currentCell + l] = node;
                  usedNodes[node] = true;
               }
               else
               {
                  int n0 = addNodes[nSimplexNodes][subcellNodes[l]][0];
                  int n1 = addNodes[nSimplexNodes][subcellNodes[l]][1];
                  int p0 = slicedSimplex[n0];
                  int p1 = slicedSimplex[n1];
                  long key = p0 < p1 ? (long)p1 << 32 | (long)p0 : (long)p0 << 32 | (long)p1;

                  NewNode node = newNodes.get(key);
                  if (node == null)
                  {
                     newNodes.put(key, new NewNode(nNewNodes, p0, p1, 
                                                   slicedVals[n1] / (slicedVals[n1] -  slicedVals[n0])));
                     currentCells[subcellSize * currentCell + l] = nInNodes + nNewNodes;
                     nNewNodes += 1;
                  }
                  else
                  {
                     currentCells[subcellSize * currentCell + l] = nInNodes + node.index;
                  }
               }
            }
            currentDataIndices[currentCell] = cellDataIndex;
            totalNewCells[subcellType] += 1;
            currentNewCells[subcellType] += 1;
            totalOutputCells += 1;
         }
      }
   }
   
   public void addSimplex(int iCell)
   {
      int[] simplex = new int[nCellNodes];
      System.arraycopy(nodes, iCell * nCellNodes, simplex, 0, nCellNodes);
      if (dataIndices != null)
         addSimplex(simplex, dataIndices[iCell]);
      else
         addSimplex(simplex, -1);
   }
   
   public void addSimplex(int[] simplex,  int cellDataIndex)
   {
      int currentCell = currentNewCells[cellType];
      int[] currentCells;
      int[] currentDataIndices;
      if (currentCell >= CHUNK)
      {
         currentDataIndices = new int[CHUNK];
         currentCells = new int[Cell.nv[cellType] * CHUNK];
         newCellsInSet[cellType].add(currentCells);
         newCellDataIndicesInSet[cellType].add(currentDataIndices);
         currentCell = currentNewCells[cellType] = 0; 
      }
      else
      {
         currentCells = newCellsInSet[cellType].get(newCellsInSet[cellType].size() - 1);
         currentDataIndices = newCellDataIndicesInSet[cellType].get(newCellsInSet[cellType].size() - 1);
      }
      for (int l = 0; l < simplex.length; l++)
         usedNodes[simplex[l]] = true;
      currentDataIndices[currentCell] = cellDataIndex;
      System.arraycopy(simplex, 0, currentCells, simplex.length * currentCell, simplex.length);
      totalNewCells[cellType] += 1;
      currentNewCells[cellType] += 1;
      totalOutputCells += 1;
   }
   
   public void addCellTriangulation(int iCell)
   {
      int nCellNodes = Cell.nv[cellType];
      int[] cell = new int[nCellNodes];
      System.arraycopy(nodes, iCell * nCellNodes, cell, 0, nCellNodes);
      if (dataIndices != null)
         addCellTriangulation(cell, dataIndices[iCell]);
      else
         addCellTriangulation(cell, -1);
   }
   
   public void addCellTriangulation(int[] cell,  int cellDataIndex)
   {
      if (Cell.isSimplexType[cellType])
         addSimplex(cell, cellDataIndex);
      else
      {
         for (int l = 0; l < cell.length; l++)
            usedNodes[cell[l]] = true;
         int newCellType;
         int[][] cellTriangulation;
         switch (cellType)
         {
         case Cell.QUAD:
            newCellType = Cell.TRIANGLE;
            cellTriangulation = Quad.triangulationVertices(cell);
            break;
         case Cell.PYRAMID:
            newCellType = Cell.TETRA;
            cellTriangulation = Pyramid.triangulationVertices(cell);
            break;
         case Cell.PRISM:
            newCellType = Cell.TETRA;
            cellTriangulation = Prism.triangulationVertices(cell);
            break;
         case Cell.HEXAHEDRON:
            newCellType = Cell.TETRA;
            cellTriangulation = Hex.triangulationVertices(cell);
            break;
         default:
            return;
         }
         for (int i = 0; i < cellTriangulation.length; i++)
         {
            int[] simplex = cellTriangulation[i];
            int currentCell = currentNewCells[newCellType];
            int[] currentCells;
            int[] currentDataIndices;
            if (currentCell >= CHUNK)
            {
               currentDataIndices = new int[CHUNK];
               currentCells = new int[Cell.nv[newCellType] * CHUNK];
               newCellsInSet[newCellType].add(currentCells);
               newCellDataIndicesInSet[newCellType].add(currentDataIndices);
               currentCell = currentNewCells[newCellType] = 0; 
            }
            else
            {
               currentCells = newCellsInSet[newCellType].get(newCellsInSet[newCellType].size() - 1);
               currentDataIndices = newCellDataIndicesInSet[newCellType].get(newCellsInSet[newCellType].size() - 1);
            }
            currentDataIndices[currentCell] = cellDataIndex;
            System.arraycopy(simplex, 0, currentCells, simplex.length * currentCell, simplex.length);
            totalNewCells[newCellType] += 1;
            currentNewCells[newCellType] += 1;
            totalOutputCells += 1;
         }
      }
   }
   
   public IrregularField createOutField(float[] normal)
   {
      if (totalOutputCells == 0)
         return null;
      nOldNodes = 0;
      for (int i = 0; i < usedNodes.length; i++)
         if (usedNodes[i])
            nOldNodes += 1;
      
      nOutNodes = nOldNodes + nNewNodes;
      if (nOutNodes == 0)
         return null;
      newNodesArray = new NewNode[nNewNodes];
      for (NewNode newNode : newNodes.values())
         newNodesArray[newNode.index] = newNode;
      
      globalNewNodeIndices = new int[nInNodes + nNewNodes];
      Arrays.fill(globalNewNodeIndices, -1);
      int cNode = 0;
      for (int i = 0; i < usedNodes.length; i++)
         if (usedNodes[i])
         {
            globalNewNodeIndices[i] = cNode;
            cNode += 1;
         }
      for (int i = 0; i < nNewNodes; i++, cNode++)
         globalNewNodeIndices[nInNodes + i] = cNode;
      outField = new IrregularField();
      outField.setNNodes(nOutNodes);
      
      float[] coords = inField.getCoords();
      float[] outCoords = new float[3 * nOutNodes];
      int k = 0;
      for (int i = 0; i < usedNodes.length; i++)
         if (usedNodes[i])
         {
            System.arraycopy(coords, 3 * i, outCoords, k, 3);
            k += 3;
         }   
      for (int i = 0; i < nNewNodes; i++)
      {
         int k0 = newNodesArray[i].p0;
         int k1 = newNodesArray[i].p1;
         float r = newNodesArray[i].ratio;
         for (int j = 0; j < 3; j++, k++)
            outCoords[k] = r * coords[3 * k0 + j] + (1 - r) * coords[3 * k1 + j];
      }
      outField.setCoords(outCoords);
      interpolateData();
      
      for (int iSet = 0; iSet < inField.getNCellSets(); iSet++)
      {
         int dataIndex = 0;
         CellSet trCS = inField.getCellSet(iSet);
         CellSet outCS = new CellSet(trCS.getName());
         
         totalNewCells = totalCellsInSets.get(iSet);
         newCellsInSet = newCells.get(iSet);
         newCellDataIndicesInSet = newCellDataIndices.get(iSet);
      
         for (int i = 0; i < trCS.getNData(); i++)
            outCS.addData(trCS.getData(i));
         
         for (int iArray = 0; iArray < Cell.TYPES; iArray++)
         {
            int nVertsInCell = Cell.nv[iArray];
            int nCellsInArray = totalNewCells[iArray];
            if (nCellsInArray < 1)
               continue;
            Vector<int[]> cellsInArray = newCellsInSet[iArray];
            Vector<int[]> cellDataIndicesInArray = newCellDataIndicesInSet[iArray];
            int[] cellNodes = new int[nVertsInCell * nCellsInArray];
            int[] cellDataIndices = new int[nCellsInArray];
            boolean[] orientations = new boolean[nCellsInArray];
            k = 0;
            for (int i = 0; i < cellsInArray.size(); i++)
            {
               int n = CHUNK;
               if (i == cellsInArray.size() - 1)
                  n = nCellsInArray % CHUNK;
               int[] nodesChunk = cellsInArray.get(i);
               int[] dataIndicesChunk = cellDataIndicesInArray.get(i);
               int[] cellVerts = new int[nVertsInCell];
               for (int j = 0; j < n; j++, k++)
               {
                  cellDataIndices[k] = dataIndicesChunk[j];
                  for (int l = 0; l < cellVerts.length; l++)
                     cellVerts[l] = globalNewNodeIndices[nodesChunk[j * nVertsInCell + l]];
                  int orientation = 0;
                  Cell cell = Cell.createCell(iArray, 3, cellVerts, true);
                  if (iArray <= Cell.QUAD)
                     orientation = cell.geomOrientation(outCoords, normal);
                  else
                     orientation = cell.geomOrientation(outCoords);
                  System.arraycopy(cell.getVertices(), 0, cellNodes, k * nVertsInCell, nVertsInCell);
                  orientations[k] = orientation > 0;
               }
            }
            outCS.addCells(new CellArray(iArray, cellNodes, orientations, cellDataIndices));
         }
         outCS.generateDisplayData(outCoords);
         outField.addCellSet(outCS);
      }
      return outField;
   }
   
   private void interpolateData()
   {
      for (int iData = 0; iData < inField.getNData(); iData++)
      {
         DataArray inDA = inField.getData(iData);
         if (!inDA.isSimpleNumeric())
            continue;
         int vlen = inDA.getVeclen();
         DataArray outDA;
         int k = 0;
         switch (inDA.getType())
         {
            case DataArray.FIELD_DATA_BYTE:
               break;
            case DataArray.FIELD_DATA_INT:
               int[] inID = inDA.getIData();
               int[] outID = new int[vlen * nOutNodes];
               for (int i = 0; i < usedNodes.length; i++)
                  if (usedNodes[i])
                  {
                     System.arraycopy(inID, vlen * i, outID, k, vlen);
                     k += vlen;
                  }
               for (int i = 0; i < nNewNodes; i++)
               {
                  int k0 = newNodesArray[i].p0;
                  int k1 = newNodesArray[i].p1;
                  float r = newNodesArray[i].ratio;
                  for (int j = 0; j < vlen; j++, k++)
                     outID[k] = (int)(r * inID[vlen * k0 + j] + (1 - r) * inID[vlen * k1 + j]);
               }
               
               outDA = DataArray.create(outID, vlen, inDA.getName());
               outDA.setMaxv(inDA.getMaxv());
               outDA.setMinv(inDA.getMinv());
               outField.addData(outDA);
               break;
            case DataArray.FIELD_DATA_FLOAT:
               float[] inFD = inDA.getFData();
               float[] outFD = new float[vlen * nOutNodes];
               for (int i = 0; i < nInNodes; i++)
                  if (usedNodes[i])
                  {
                     System.arraycopy(inFD, vlen * i, outFD, k, vlen);
                     k += vlen;
                  }
               for (int i = 0; i < nNewNodes; i++)
               {
                  int k0 = newNodesArray[i].p0;
                  int k1 = newNodesArray[i].p1;
                  float r = newNodesArray[i].ratio;
                  for (int j = 0; j < vlen; j++, k++)
                     outFD[k] = r * inFD[vlen * k0 + j] + (1 - r) * inFD[vlen * k1 + j];
               }
               outDA = DataArray.create(outFD, vlen, inDA.getName());
               outDA.setMaxv(inDA.getMaxv());
               outDA.setMinv(inDA.getMinv());
               outDA.setPhysMin(inDA.getPhysMin());
               outDA.setPhysMax(inDA.getPhysMax());
               outField.addData(outDA);
               break;
         }
      }
   }
   
}
