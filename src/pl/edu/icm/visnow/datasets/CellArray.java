///<editor-fold defaultstate="collapsed" desc=" COPYRIGHT AND LICENSE ">
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

package pl.edu.icm.visnow.datasets;

import java.util.ArrayList;
import pl.edu.icm.visnow.datasets.cells.Cell;
import pl.edu.icm.visnow.datasets.cells.Hex;
import pl.edu.icm.visnow.datasets.cells.Pyramid;
import pl.edu.icm.visnow.datasets.cells.Quad;
import pl.edu.icm.visnow.datasets.cells.Tetra;
import pl.edu.icm.visnow.datasets.cells.Triangle;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.lib.utils.RabinHashFunction;

/**
 *
 * @author Krzysztof S. Nowinski
 *   <p>   University of Warsaw, ICM
 */
public class CellArray
{
   protected CellSet parent;
   protected int type;
   protected int nCells;
   protected int cellNodes;
   protected int[] nodes;
   protected boolean[] orientations;
   protected int[] dataIndices;
   protected int[] faceIndices;
   protected boolean cleanedDuplicates = false;
   protected float[] cellNormals;
   protected float[] cellDihedrals;
   protected float[] cellCenters;
   protected float[] cellRadii;
   protected float[] cellLow;
   protected float[] cellUp;
   protected long hash;

   public CellArray(int type)
   {
      this.type = type;
   }

   public CellArray(int type, int[] nodes, boolean[] orientations, int[] dataIndices)
   {
      if (nodes.length % Cell.nv[type] != 0)
         return;
      if (orientations == null)
      {
         orientations = new boolean[nodes.length / Cell.nv[type]];
         for (int i = 0; i < orientations.length; i++)
            orientations[i] = true;
      }
      if (nodes.length / Cell.nv[type] != orientations.length)
         return;
      this.type = type;
      this.cellNodes = Cell.nv[type];
      this.nCells = orientations.length;
      this.nodes = nodes;
      this.orientations = orientations;
      this.dataIndices = dataIndices;
      hash = RabinHashFunction.hash(nodes);
   }

   public CellArray(CellSet parent, int type, int[] nodes, boolean[] orientations, int[] indices)
   {
      this(type, nodes, orientations, indices);
      this.parent = parent;
      this.dataIndices = indices;
      hash = RabinHashFunction.hash(nodes);
   }

   public CellArray(CellArray old)
   {
      type = old.getType();
      nCells = old.getNCells();
      cellNodes = Cell.nv[type];
      nodes = old.getNodes().clone();
      orientations = old.getOrientations().clone();
      if (old.getDataIndices() != null)
         dataIndices = old.getDataIndices().clone();
      cleanedDuplicates = old.isCleanedDuplicates();
      hash = old.getHash();
   }
   
   public CellArray cloneArray()
   {
      return new CellArray(this);
   }

   /**
    * Get the value of parent
    *
    * @return the value of parent
    */
   public CellSet getParent()
   {
      return parent;
   }

   /**
    * Set the value of parent
    *
    * @param parent new value of parent
    */
   public void setParent(CellSet parent)
   {
      this.parent = parent;
   }

   /**
    * Get the value of type
    *
    * @return the value of type
    */
   public int getType()
   {
      return type;
   }

   /**
    * Set the value of type
    *
    * @param type new value of type
    */
   public void setType(int type)
   {
      this.type = type;
   }

   public int getDim()
   {
      return Cell.dim[type];
   }

   /**
    * Get the value of nCells
    *
    * @return the value of nCells
    */
   public int getNCells()
   {
      return nCells;
   }

   /**
    * Set the value of nCells
    *
    * @param nCells new value of nCells
    */
   public void setNCells(int nCells)
   {
      this.nCells = nCells;
   }

   public int getCellNodes()
   {
      return cellNodes;
   }

   /**
    * Get the value of nodes
    *
    * @return the value of nodes
    */
   public int[] getNodes()
   {
      return nodes;
   }

   /**
    * Set the value of nodes
    *
    * @param nodes new value of nodes
    */
   public void setNodes(int[] nodes)
   {
      this.nodes = nodes;
      hash = RabinHashFunction.hash(nodes);
   }

   /**
    * Get the value of nodes of specified cell
    *
    * @param ind - cell index
    * @return the value of nodes of the cell
    */
   public int[] getNodes(int ind)
   {
      int n = Cell.nv[type];
      int[] selNodes = new int[n];
      for (int i = 0; i < n; i++)
         selNodes[i] = nodes[ind * n + i];
      return selNodes;
   }

   /**
    * Set the value of nodes of specified cell
    *
    * @param ind - cell index
    * @param newNodes new value of nodes of the cell
    */
   public void setNodes(int indices, int[] newNodes)
   {
      if (newNodes.length != Cell.nv[type])
         return;
      int n = Cell.nv[type];
      System.arraycopy(newNodes, 0, nodes, indices * n, n);
      hash = RabinHashFunction.hash(nodes);
      cleanedDuplicates = false;
   }

   /**
    * Get the value of orientations
    *
    * @return the value of orientations
    */
   public boolean[] getOrientations()
   {
      return orientations;
   }

   /**
    * Set the value of orientations
    *
    * @param orientations new value of orientations
    */
   public void setOrientations(boolean[] orientations)
   {
      this.orientations = orientations;
   }

   /**
    * Get the value of orientations at specified indices
    *
    * @param index
    * @return the value of orientations at specified index
    */
   public boolean isOrientations(int index)
   {
      return this.orientations[index];
   }

   /**
    * Set the value of orientations at specified indices.
    *
    * @param indices
    * @param newOrientations new value of orientations at specified indices
    */
   public void setOrientations(int indices, boolean newOrientations)
   {
      this.orientations[indices] = newOrientations;
   }

   public void setData(int[] nodes, boolean[] orientations)
   {
      if (nodes.length % Cell.nv[type] != 0 || nodes.length / Cell.nv[type] != orientations.length)
         return;
      this.nCells = orientations.length;
      this.nodes = nodes;
      this.orientations = orientations;
   }

   /**
    * Get the value of dataIndices
    *
    * @return the value of dataIndices
    */
   public int[] getDataIndices()
   {
      return dataIndices;
   }

   /**
    * Set the value of dataIndices
    *
    * @param dataIndices new value of dataIndices
    */
   public void setDataIndices(int[] dataIndices)
   {
      this.dataIndices = dataIndices;
   }

   /**
    * Get the value of dataIndices for cell ind
    * @param ind - cell number within array
    * @return the value of dataIndices
    */
   public int getDataIndices(int ind)
   {
      return dataIndices[ind];
   }

   /**
    * Set the value of dataIndices
    *
    * @param ind - cell number within array
    * @param dataIndices new value of dataIndices for cell ind
    */
   public void setDataIndices(int ind, int dataIndices)
   {
      this.dataIndices[ind] = dataIndices;
   }

   public float[] getCellNormals()
   {
      return cellNormals;
   }

   public void setCellNormals(float[] cellNormals)
   {
      this.cellNormals = cellNormals;
   }

   /**
    * 
    * @return true if cells are sorted (lexicographical order)
    */
   public boolean isCleanedDuplicates()
   {
      return cleanedDuplicates;
   }

   public void setCleanedDuplicate(boolean cleanedDuplicates)
   {
      this.cleanedDuplicates = cleanedDuplicates;
   }

   public void setHash()
   {
      hash = RabinHashFunction.hash(nodes);
   }

   public long getHash()
   {
      return hash;
   }

   public boolean isStructCompatible(CellArray ca)
   {
      if (ca == null || ca.getType() != type || hash != ca.getHash())
         return false;
      int[] caNodes = ca.getNodes();
      if (caNodes == null && nodes == null)
         return true;
      if (caNodes == null && nodes != null || caNodes != null && nodes == null
              || caNodes.length != nodes.length)
         return false;
      for (int i = 0; i < nodes.length; i++)
         if (nodes[i] != caNodes[i])
            return false;
      return true;
   }


   private int shortHash(int k)
   {
      short seed = 13131; // 31 131 1313 13131 131313 etc..
      short h = 0;

      for (int i = 0; i < cellNodes; i++)
         h = (short) (h * seed + nodes[cellNodes * k + i]);
      return (int) h - Short.MIN_VALUE;
   }

   /**
    * removes cells occuring twice  (usuallly, internal cell faces)
    */
   public void cancelDuplicates()
   {
      if (cleanedDuplicates)
         return;
      int startBinSize = 2;
      int[][] indexBins = new int[65536][startBinSize];
      int[] indexN = new int[65536];
      int n = nCells;
      for (int i = 0; i < indexN.length; i++)
         indexN[i] = 0;
      
      for (int i = 0; i < nCells; i++)
      {
         int h = shortHash(i);
         int k = -1;
      s1:
         for (int j = 0; j < indexN[h]; j++)
         {
            int l = indexBins[h][j];
            for (int m = 0; m < cellNodes; m++)
               if (nodes[cellNodes * i + m] != nodes[cellNodes * l + m])
                  continue s1;        // cell i is not a duplicate of cell j in bin h
            k = j;  // all nodes match - duplicate found
            break;
         }
         if (k != -1)   // duplicate found - removing
         {
            for (int j = k + 1; j < indexN[h]; j++)
               indexBins[h][j - 1] = indexBins[h][j];
            indexN[h] -= 1;
            n -= 2;     // two cells removed
         } else
         {
            if (indexN[h] + 1 >= indexBins[h].length) // increase bin capacity
            {
               int[] t = new int[2 * indexBins[h].length];
               System.arraycopy(indexBins[h], 0, t, 0, indexBins[h].length);
               indexBins[h] = t;
            }
            indexBins[h][indexN[h]] = i;
            indexN[h] += 1;                         // cell index added to bin
         }
      }
      
      int maxBin = 0;
      for (int i = 0; i < indexN.length; i++)
         if (maxBin < indexN[i]) maxBin = indexN[i];
      int[] xNodes = new int[cellNodes * n];
      boolean[] xOrientations = new boolean[n];
      if (dataIndices != null)
      {
         int[] xDataIndices = new int[n];
         for (int i = 0, l = 0; i < 65536; i++)
            for (int j = 0; j < indexN[i]; j++, l++)
               xDataIndices[l] = dataIndices[indexBins[i][j]];
         dataIndices = xDataIndices;
      } 
      for (int i = 0, l = 0; i < 65536; i++)
         for (int j = 0; j < indexN[i]; j++, l++)
         {
            int m = indexBins[i][j];
            for (int k = 0; k < cellNodes; k++)
               xNodes[l * cellNodes + k] = nodes[m * cellNodes + k];
            xOrientations[l] = orientations[m];
         }
      setNCells(n);
      setNodes(xNodes);
      orientations = xOrientations;
      cleanedDuplicates = true;
   }

   public void cleanEdgeArray()
   {
      if (type != Cell.SEGMENT || cleanedDuplicates)
         return;
   }

   /**
    * generates a vector of arrays containg all faces of the cells 
    * @return list of cell arrays containing all faces of all cells of the array
    */
   public ArrayList<CellArray> generateFaces()
   {
      Cell[] stdCells = new Cell[Cell.TYPES];
      for (int i = 0; i < stdCells.length; i++)
         stdCells[i] = Cell.createCell(i, 3, new int[Cell.nv[i]], true);
      CellArray[] fcs = new CellArray[8];
      int[][] v = new int[8][];
      boolean[][] or = new boolean[8][];
      int[][] in = new int[8][];
      int[][] fcsOf = new int[8][];
      int[] faceTypes = Cell.faceTypes[type];
      for (int i = 0; i < faceTypes.length; i++)
         if (faceTypes[i] != 0)
         {
            int[] caNodes = new int[nCells * faceTypes[i] * Cell.nv[i]];
            boolean[] caOrientations = new boolean[nCells * faceTypes[i]];
            int[] caIndices = null;
            if (dataIndices != null)
            {
               caIndices = new int[nCells * faceTypes[i]];
               for (int j = 0; j < caIndices.length; j++)
                  caIndices[j] = dataIndices[j / faceTypes[i]];
            }
            int[] caFcsOf = new int[nCells * faceTypes[i]];
            fcs[i] = new CellArray(this.parent, i, caNodes, caOrientations, caIndices);
            v[i] = caNodes;
            or[i] = caOrientations;
            in[i] = caIndices;
            fcsOf[i] = caFcsOf;
         } else
            fcs[i] = null;
      int nv = Cell.nv[type];
      int[] cv = new int[nv];
      int[] ci = new int[8];
      for (int i = 0; i < 8; i++)
         ci[i] = 0;
      for (int i = 0; i < nCells; i++)
      {
         for (int j = 0; j < nv; j++)
            cv[j] = nodes[nv * i + j];
         Cell[] faces = stdCells[type].faces(cv, orientations[i]);
         for (int j = 0; j < faces.length; j++)
         {
            Cell cell = faces[j];
            int k = cell.getType();
            int l = ci[k];
            or[k][l] = cell.isOrientation();
            if (dataIndices != null)
               
               in[k][l] = dataIndices[i];
            fcsOf[k][l] = i;                     //parent cell
            int nfv = Cell.nv[k];
            System.arraycopy(cell.getVertices(), 0, v[k], l * nfv, nfv);
            ci[k] += 1;
         }
      }
      ArrayList<CellArray> faces = new ArrayList<CellArray>();
      for (int i = 0; i < fcs.length; i++)
         if (fcs[i] != null)
         {
            faces.add(fcs[i]);
            CellArray[] arFcsOf = new CellArray[fcsOf[i].length];
            for (int j = 0; j < arFcsOf.length; j++)
               arFcsOf[j] = this;
         }
      return faces;
   }

   public float[] getCellDihedrals()
   {
      return cellDihedrals;
   }

   public void setCellDihedrals(float[] cellDihedrals)
   {
      this.cellDihedrals = cellDihedrals;
   }


   public int[] getFaceIndices()
   {
      return faceIndices;
   }

   public void setFaceIndices(int[] faceIndices)
   {
      this.faceIndices = faceIndices;
   }

   public void clearFaceIndices()
   {
      this.faceIndices = null;
   }

   public CellArray generateEdges()
   {
      int nEdges = Cell.faceTypes[type][1];
      if (nEdges == 0)
         return null;
      int[] caNodes = new int[nCells * nEdges * Cell.nv[1]];
      boolean[] caOrientations = new boolean[nCells * nEdges];
      int[] caIndices = new int[nCells * nEdges];
      int[] fcsOf = new int[nCells * nEdges];
      CellArray edges = new CellArray(this.parent, Cell.SEGMENT, caNodes, caOrientations, caIndices);
      int nv = Cell.nv[type];
      int[] cv = new int[nv];
      for (int i = 0, l = 0; i < nCells; i++)
      {
         for (int j = 0; j < nv; j++)
            cv[j] = nodes[nv * i + j];
         Cell[] faces = Cell.createCell(type, 3, cv, orientations[i]).faces();
         for (int j = 0; j < faces.length; j++)
         {
            Cell cell = faces[j];
            if (cell.getType() != Cell.SEGMENT)
               continue;
            caOrientations[l] = cell.isOrientation();
            if (dataIndices != null)
               caIndices[l] = dataIndices[i];           //faces inherit data from original cell
            fcsOf[l] = i;
            int nfv = Cell.nv[1];
            System.arraycopy(cell.getVertices(), 0, caNodes, l * nfv, nfv);
            l += 1;
         }
      }
      edges.setFaceIndices(fcsOf);
      return edges;
   }
   
   public int[] getCellVerts(int i)
   {
      if (i < 0 || i >= nCells)
         return null;
      int[] cellVerts = new int[cellNodes];
      System.arraycopy(nodes, i * cellNodes, cellVerts, 0, cellNodes);
      return cellVerts;
   }
   
   public Cell getCell(int i)
   {
      if (i < 0 || i >= nCells)
         return null;
      int[] cellVerts = new int[cellNodes];
      System.arraycopy(nodes, i * cellNodes, cellVerts, 0, cellNodes);
      return Cell.createCell(type, Cell.dim[type], cellVerts, orientations[i]);
   }

   public CellArray triangulate()
   {
      int nv = Cell.nv[type];
      int mult = 1;
      int simplex = Cell.POINT;
      switch (this.type)
      {
         case Cell.POINT:
            return this;
         case Cell.SEGMENT:
            return this;
         case Cell.TRIANGLE:
            return this;
         case Cell.TETRA:
            return this;
         case Cell.QUAD:
            simplex = Cell.TRIANGLE;
            mult = 2;
            break;
         case Cell.PYRAMID:
            simplex = Cell.TETRA;
            mult = 2;
            break;
         case Cell.PRISM:
            simplex = Cell.TETRA;
            mult = 3;
            break;
         case Cell.HEXAHEDRON:
            simplex = Cell.TETRA;
            mult = 6;
            break;
         default:
            simplex = Cell.TETRA;
            mult = 3;
            break;
      }
      int[] trNodes = new int[mult * Cell.nv[simplex] * nCells];
      boolean[] trOrientations = new boolean[mult * nCells];
      int[] cellVerts = new int[nv];
      int ntv = Cell.nv[simplex];
      for (int i = 0, l = 0, n = 0; i < nCells; i++)
      {
         for (int j = 0; j < nv; j++)
            cellVerts[j] = nodes[i * nv + j];
         Cell[] tr = Cell.createCell(type, Cell.dim[type], cellVerts, orientations[i]).triangulation();
         for (int j = 0; j < tr.length; j++, n++)
         {
            for (int k = 0; k < ntv; k++, l++)
               trNodes[l] = tr[j].getVertices()[k];
            trOrientations[n] = tr[j].isOrientation();
         }
      }
      if (dataIndices != null && dataIndices.length == nCells)
      {
         int[] trDataIndices = new int[mult * nCells];
         for (int i = 0; i < trDataIndices.length; i++)
            trDataIndices[i] = dataIndices[i / mult];
         return new CellArray(simplex, trNodes, trOrientations, trDataIndices);
      }
      return new CellArray(simplex, trNodes, trOrientations, null);
   }

   public void printContent()
   {
      System.out.println("" + nCells + " " + Cell.UCDnames[type]);
      int n = Cell.nv[type];
      for (int i = 0; i < nCells; i++)
      {
         for (int j = 0; j < n; j++)
            System.out.printf("%4d ", nodes[n * i + j]);
         System.out.println("" + orientations[i]);
      }
   }
   
   public float getMeasure(float[] coords)
   {
      float r = 0;
      for (int i = 0; i < nCells; i++)
      {
         int[] cellVerts = new int[cellNodes];
         System.arraycopy(nodes, i * cellNodes, cellVerts, 0, cellNodes);
         r += Cell.createCell(type, Cell.dim[type], cellVerts, orientations[i]).getMeasure(coords);
      }
      return r;
   }

   public int getnCells()
   {
      return nCells;
   }

   public float[] getCellCenters()
   {
      return cellCenters;
   }

   public float[] getCellRadii()
   {
      return cellRadii;
   }
   
   public boolean isTriangulation()
   {
      return (type == Cell.TETRA || type == Cell.TRIANGLE || type == Cell.SEGMENT || type == Cell.POINT);
   }

   class ComputeGeometryData implements Runnable
   {
      int nThreads      = 1;
      int iThread       = 0;
      float[] coords; 

      public ComputeGeometryData(int nThreads, int iThread, float[] coords)
      {
         this.nThreads = nThreads;
         this.iThread  = iThread;
         this.coords   = coords;
      }
      

      @Override
      public void run()
      {
         
         for (int k = iThread; k < nCells; k+=nThreads)
         {
            float[] c = new float[3];
            for (int l = 0; l < 3; l++)
               c[l] = 0;
            for (int l = 0; l < cellNodes; l++)
            {
               int cStart = 3 * nodes[k * cellNodes + l];
               for (int m = 0; m < 3; m++)
                  c[m] += coords[cStart + m];
            }
            for (int l = 0; l < c.length; l++)
               c[l] /= cellNodes;
            float r = 0;
            for (int l = 0; l < cellNodes; l++)
            {
               int cStart = 3 * nodes[k * cellNodes + l];
               for (int m = 0; m < 3; m++)
                  r += (c[m] - coords[cStart + m]) * (c[m] - coords[cStart + m]);
            }
            System.arraycopy(c, 0, cellCenters, 3 * k, 3);
            cellRadii[k] = (float) Math.sqrt(r);
         }
      }
   }      
   
   class CleanUp implements Runnable
   {
      int nThreads      = 1;
      int iThread       = 0;
      
      float[] coords; 

      public CleanUp(int nThreads, int iThread, float[] coords)
      {
         this.nThreads = nThreads;
         this.iThread  = iThread;
      }
      
      @Override
      public void run()
      {
         int[] cell = new int[cellNodes];
         for (int k = iThread; k < nCells; k+=nThreads)
         {
            System.arraycopy(nodes, k * cellNodes, cell, 0, cell.length);
            switch (type)
            {
            case Cell.TRIANGLE:
               new Triangle().normalize(nodes);
               break;
            case Cell.QUAD:
               new Quad().normalize(nodes);
               break;
            case Cell.TETRA:
               new Tetra().normalize(nodes);
               break;
            case Cell.PYRAMID:
               new Pyramid().normalize(nodes);
               break;
            case Cell.HEXAHEDRON:
               new Hex().normalize(nodes);
               break;
            }
            System.arraycopy(cell, 0, nodes, k * cellNodes, cell.length);
            int geomSpace = Cell.dim[type];
            int geomOrientation = Cell.geomOrientation(type, 3, geomSpace, cell, coords);
            if (orientations != null)
               orientations[k] = geomOrientation > 0;
         }
      }
   }   
   
   public void cleanUpOrientations(float[] coords)
   {
      int nThreads = pl.edu.icm.visnow.system.main.VisNow.availableProcessors();
      if (orientations == null)
         orientations = new boolean[nCells];
      Thread[] workThreads = new Thread[nThreads];
      for (int iThread = 0; iThread < nThreads; iThread++)
      {
         workThreads[iThread] = new Thread(new CleanUp(nThreads, iThread, coords));
         workThreads[iThread].start();
      }
      for (int iThread = 0; iThread < workThreads.length; iThread++)
         try { workThreads[iThread].join(); } catch (Exception e){}
   }
   
   class ComputeComponentData implements Runnable
   {
      int nThreads      = 1;
      int iThread       = 0;
      int veclen        = 1;
      float[] data; 

      public ComputeComponentData(int nThreads, int iThread, DataArray da)
      {
         this.nThreads = nThreads;
         this.iThread  = iThread;
         this.data     = da.getFData();
         this.veclen   = da.getVeclen();
      }
      
      @Override
      public void run()
      {
         for (int k = iThread; k < nCells; k+=nThreads)
         {
            float low = Float.MAX_VALUE;
            float up  = -Float.MAX_VALUE;
            for (int l = 0; l < cellNodes; l++)
               if (veclen == 1)
               {
                  float f = data[nodes[k * cellNodes + l]];
                  if (f > up)  up  = f;
                  if (f < low) low = f;
               }
               else
               {
                  int cStart = veclen * nodes[k * cellNodes + l];
                  float f = 0;
                  for (int m = 0; m < veclen; m++)
                     f += data[cStart + m] * data[cStart + m];
                  f = (float)Math.sqrt(f);
                  if (f > up)  up  = f;
                  if (f < low) low = f;
               }
            cellLow[k] = low;
            cellUp[k]  = up;
         }
      }
   }   
     /*
    * geometric center and diameter of each simplex is computed and stored in two addiutional cell data arrays
    */
   public void addGeometryData(int nThreads, float[] coords)
   {
      cellRadii = new float[nCells];
      cellCenters = new float[3 * nCells];
      Thread[] workThreads = new Thread[nThreads];
      for (int iThread = 0; iThread < nThreads; iThread++)
      {
         workThreads[iThread] = new Thread(new ComputeGeometryData(nThreads, iThread, coords));
         workThreads[iThread].start();
      }
      for (int iThread = 0; iThread < workThreads.length; iThread++)
         try { workThreads[iThread].join(); } catch (Exception e){}
   }
   
    /*
    * geometric center and diameter of each simplex is computed and stored in two addiutional cell data arrays
    */
   public void addComponentData(int nThreads, DataArray da)
   {
      cellLow = new float[nCells];
      cellUp = new float[nCells];
      Thread[] workThreads = new Thread[nThreads];
      for (int iThread = 0; iThread < nThreads; iThread++)
      {
         workThreads[iThread] = new Thread(new ComputeComponentData(nThreads, iThread, da));
         workThreads[iThread].start();
      }
      for (int iThread = 0; iThread < workThreads.length; iThread++)
         try { workThreads[iThread].join(); } catch (Exception e){}
   }

   public float[] getCellLow()
   {
      return cellLow;
   }

   public float[] getCellUp()
   {
      return cellUp;
   }
   
}
