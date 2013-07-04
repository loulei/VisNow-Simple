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

import pl.edu.icm.visnow.datasets.cells.Cell;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import java.util.Vector;
import pl.edu.icm.visnow.gui.events.FloatValueModificationEvent;
import pl.edu.icm.visnow.gui.events.FloatValueModificationListener;
import pl.edu.icm.visnow.lib.utils.RabinHashFunction;

/**
 *
 * @author Krzysztof S. Nowinski
 *   <p>   University of Warsaw, ICM
 */
public class CellSet implements DataContainer
{
   protected static final String DATA = "data";

   protected int nCells;
   protected long hash;
   protected long boundaryHash;
   protected CellSetSchema schema = new CellSetSchema();
   protected CellArray[] cellArrays = new CellArray[Cell.TYPES];
   protected CellArray[] boundaryCellArrays = new CellArray[Cell.TYPES2D];
   protected CellArray edgeArray;
   protected long[] cellArrayHashes = new long[Cell.TYPES];
   protected long[] boundaryCellArrayHashes = new long[Cell.TYPES2D];
   protected Vector<DataArray> data = new Vector<DataArray>();
   protected Vector<DataArray> nodeData = new Vector<DataArray>();
   protected int[] pointIndices = null;
   protected int nActiveNodes = -1;
   protected boolean selected = false;
   private float currentTime = 0;

   /**
    * Get the value of currentTime
    *
    * @return the value of currentTime
    */
   public float getCurrentTime()
   {
      return currentTime;
   }

   /**
    * Set the value of currentTime
    *
    * @param currentTime new value of currentTime
    */
   public void setCurrentTime(float currentTime)
   {
      this.currentTime = currentTime;
   }

   public CellSet(String name)
   {
      schema.setName(name);
      for (int i = 0; i<cellArrays.length; i++)
         cellArrays[i] = null;
      for (int i = 0; i < boundaryCellArrays.length; i++)
         boundaryCellArrays[i] = null;
   }

   public CellSet()
   {
      for (int i = 0; i < cellArrays.length; i++)
         cellArrays[i] = null;
      for (int i = 0; i < boundaryCellArrays.length; i++)
         boundaryCellArrays[i] = null;
   }

   @Override
   public String toString()
   {
      StringBuffer s = new StringBuffer();
      s.append(schema.getName()+":   ");
      for (int i = 0; i<Cell.TYPES; i++)
         if (cellArrays[i]!=null)
         {
            CellArray ca = cellArrays[i];
            s.append(""+ca.getNCells()+" "+Cell.UCDnames[i]+"s ");
         }
      s.append("\nboundary:   ");
      for (int i = 0; i<Cell.TYPES2D; i++)
         if (boundaryCellArrays[i]!=null)
         {
            CellArray ca = boundaryCellArrays[i];
            s.append(""+ca.getNCells()+" "+Cell.UCDnames[i]+"s ");
         }

      if (data.size()>0)
         for (DataArray dataArray : data)
            s.append(dataArray.toString()+"\n");
      else
            s.append("\n");
      return s.toString();
   }

   public String description()
   {
      StringBuffer s = new StringBuffer();
      s.append(schema.getName()+":   ");
      for (int i = 0; i<Cell.TYPES; i++)
         if (cellArrays[i]!=null)
         {
            CellArray ca = cellArrays[i];
            s.append(""+ca.getNCells()+" "+Cell.UCDnames[i]+"s ");
         }
      s.append("<br>boundary:   ");
      for (int i = 0; i<Cell.TYPES2D; i++)
         if (boundaryCellArrays[i]!=null)
         {
            CellArray ca = boundaryCellArrays[i];
            s.append(""+ca.getNCells()+" "+Cell.UCDnames[i]+"s ");
         }

      s.append("<br>Cell data components:");
      if (data.size()>0) {
          s.append("<table>");
         for (DataArray dataArray : data)
            s.append(dataArray.description()+"<br>");
         s.append("</table>");
      } else {
            s.append("<br>");
      }
      return s.toString();
   }
   
   public CellSet cloneDeep() 
   {
       //TODO
       throw new UnsupportedOperationException("Not yet implemented!");
   }

   public int getNCells()
   {
      return nCells;
   }

   public void setNCells(int nCells)
   {
      this.nCells = nCells;
   }

   @Override
   public int getNData()
   {
      return data.size();
   }

   /**
    * Get the value of schema
    *
    * @return the value of schema
    */
   public CellSetSchema getSchema()
   {
      return schema;
   }

   /**
    * Set the value of schema
    *
    * @param schema new value of schema
    */
   public void setSchema(CellSetSchema schema)
   {
      this.schema = schema;
   }
   
   @Override
   public DataSchema getDataSchema()
   {
      return schema;
   }

   public String getName()
   {
      return schema.getName();
   }

   public void setName(String name)
   {
      schema.setName(name);
   }

   /**
    * Get the value of cellArrays
    *
    * @return the value of cellArrays
    */
   public CellArray[] getCellArrays()
   {
      return cellArrays;
   }

   /**
    * Set the value of cellArrays
    *
    * @param cellArrays new value of cellArrays
    */
   public void setCellArrays(CellArray[] cellArrays)
   {
      if (cellArrays.length!=Cell.TYPES)
         return;
      this.cellArrays = cellArrays;
      for (int i = 0; i<cellArrays.length; i++)
         cellArrayHashes[i] = cellArrays[i].getHash();
      nCells = 0;
      for (int i = 0; i < cellArrays.length; i++)
         if (cellArrays[i] != null)
            nCells += cellArrays[i].getNCells();
      hash = RabinHashFunction.hash(cellArrayHashes);
      for (int i = 0; i < Cell.TETRA; i++)
      {
         CellArray cellArray = cellArrays[i];
         if (cellArray != null && (boundaryCellArrays[i] == null || boundaryCellArrays[i].getNCells() == 0))
            boundaryCellArrays[i] = cellArray;
         
      }
   }

   /**
    * Get the value of cellArrays at specified index
    *
    * @param index
    * @return the value of cellArrays at specified index
    */
   public CellArray getCellArray(int index)
   {
      if (index<0||index>=Cell.TYPES)
         return null;
      return this.cellArrays[index];
   }

   /**
    * Set the value of cellArray 
    *
    * @param newCellArrays new value of cellArrays at specified index
    */
   public void setCellArray(CellArray newCellArray)
   {
      cellArrays[newCellArray.getType()] = newCellArray;
      if (newCellArray.getType() <= Cell.QUAD)
      {
         int i = newCellArray.getType();
         if (newCellArray != null && (boundaryCellArrays[i] == null || boundaryCellArrays[i].getNCells() == 0))
            boundaryCellArrays[i] = newCellArray;
      }
      cellArrayHashes[newCellArray.getType()] = newCellArray.getHash();
      nCells = 0;
      for (int i = 0; i < cellArrays.length; i++)
         if (cellArrays[i] != null)
            nCells += cellArrays[i].getNCells();
      hash = RabinHashFunction.hash(cellArrayHashes);
   }

   /**
    * Get the value of cellArrays
    *
    * @return the value of cellArrays
    */
   public CellArray[] getBoundaryCellArrays()
   {
      return boundaryCellArrays;
   }

   /**
    * Set the value of cellArrays
    *
    * @param cellArrays new value of cellArrays
    */
   public void setBoundaryCellArrays(CellArray[] cellArrays)
   {
      if (cellArrays.length!=Cell.TYPES2D)
         return;
      boundaryCellArrays = cellArrays;
      for (int i = 0; i<boundaryCellArrays.length; i++)
         boundaryCellArrayHashes[i] = boundaryCellArrays[i].getHash();
      hash = RabinHashFunction.hash(boundaryCellArrayHashes);
   }

   /**
    * Get the value of cellArrays at specified index
    *
    * @param index
    * @return the value of cellArrays at specified index
    */
   public CellArray getBoundaryCellArray(int index)
   {
      if (index<0||index>=Cell.TYPES2D)
         return null;
      return this.boundaryCellArrays[index];
   }

   /**
    * Set the value of cellArray
    *
    * @param newCellArrays new value of cellArrays at specified index
    */
   public void setBoundaryCellArray(CellArray newCellArray)
   {
      if (newCellArray.getType()<0 || newCellArray.getType()>=Cell.TYPES2D)
         return;
      boundaryCellArrays[newCellArray.getType()] = newCellArray;
      boundaryCellArrayHashes[newCellArray.getType()] = newCellArray.getHash();
      hash = RabinHashFunction.hash(boundaryCellArrayHashes);
   }

   public boolean isSelected()
   {
      return selected;
   }

   public void setSelected(boolean selected)
   {
      this.selected = selected;
   }

    public void invertSelected()
   {
      selected = !selected;
   }

  public long[] getCellArrayHashes()
   {
      return cellArrayHashes;
   }

   public long getHash()
   {
      return hash;
   }

   public Vector<DataArray> getData()
   {
      return data;
   }

   public DataArray getData(int i)
   {
      if (i<0||i>=data.size())
         return null;
      return data.get(i);
   }
   
   public DataArray getData(String s)
   {
      for (DataArray dataArray : data)
         if (dataArray.getName().equals(s))
            return dataArray;
      return null;
   }

   public void addData(DataArray dataArray)
   {
      data.add(dataArray);
      schema.addDataArraySchema(dataArray.getSchema());
   }

   public void setData(Vector<DataArray> data)
   {
      this.data = data;
   }

   public void setData(int i, DataArray dataArray)
   {
      if (i<0)
         return;
      if (i>=data.size())
         data.add(dataArray);
      else
         data.set(i, dataArray);
      schema.getSchemasFromData(data);
   }

   public void setData(int i, byte[] data0)
   {
      if (i>=data.size())
         setData(i, data0, DATA+data.size());
      else
         setData(i, data0, DATA+i);
      schema.getSchemasFromData(data);
   }

   public void setData(int i, byte[] data0, String name)
   {
      if (data0.length%nCells!=0||i<0)
         return;
      if (i>=data.size())
         data.add(DataArray.create(data0, data0.length/nCells, name));
      else
         data.set(i, DataArray.create(data0, data0.length/nCells, name));
      schema.getSchemasFromData(data);
   }

   public void setData(int i, short[] data0)
   {
      if (i>=data.size())
         setData(i, data0, DATA+data.size());
      else
         setData(i, data0, DATA+i);
      schema.getSchemasFromData(data);
   }

   public void setData(int i, short[] data0, String name)
   {
      if (data0.length%nCells!=0||i<0)
         return;
      if (i>=data.size())
         data.add(DataArray.create(data0, data0.length/nCells, name));
      else
         data.set(i, DataArray.create(data0, data0.length/nCells, name));
      schema.getSchemasFromData(data);
   }

   public void setData(int i, int[] data0)
   {
      if (i>=data.size())
         setData(i, data0, DATA+data.size());
      else
         setData(i, data0, DATA+i);
      schema.getSchemasFromData(data);
   }

   public void setData(int i, int[] data0, String name)
   {
      if (data0.length%nCells!=0||i<0)
         return;
      if (i>=data.size())
         data.add(DataArray.create(data0, data0.length/nCells, name));
      else
         data.set(i, DataArray.create(data0, data0.length/nCells, name));
      schema.getSchemasFromData(data);
   }

   public void setData(int i, float[] data0)
   {
      if (i>=data.size())
         setData(i, data0, DATA+data.size());
      else
         setData(i, data0, DATA+i);
      schema.getSchemasFromData(data);
   }

   public void setData(int i, float[] data0, String name)
   {
      if (data0.length%nCells!=0||i<0)
         return;
      if (i>=data.size())
         data.add(DataArray.create(data0, data0.length/nCells, name));
      else
         data.set(i, DataArray.create(data0, data0.length/nCells, name));
      schema.getSchemasFromData(data);
   }

   public void setData(int i, double[] data0)
   {
      if (i>=data.size())
         setData(i, data0, DATA+data.size());
      else
         setData(i, data0, DATA+i);
      schema.getSchemasFromData(data);
   }

   public void setData(int i, double[] data0, String name)
   {
      if (data0.length%nCells!=0||i<0)
         return;
      if (i>=data.size())
         data.add(DataArray.create(data0, data0.length/nCells, name));
      else
         data.set(i, DataArray.create(data0, data0.length/nCells, name));
      schema.getSchemasFromData(data);
   }

   public boolean isDataCompatibleWith(CellSet f)
   {
      return this.schema.isDataCompatibleWith(f.getSchema());
   }

   public boolean isDataCompatibleWith(CellSetSchema s)
   {
      return this.schema.isDataCompatibleWith(s);
   }

   private int shortHash(int k, int cellNodes, int[] nodes)
   {
      short seed = 13131; // 31 131 1313 13131 131313 etc..
      short h = 0;

      for (int i = 0; i < cellNodes; i++)
         h = (short) (h * seed + nodes[cellNodes * k + i]);
      return (int) h - Short.MIN_VALUE;
   }

   /**
    * merges cell array with own cell array of corresponding type removing duplicates
    * @param ca merged array
    * @param mca array of cell arrays - ca is merged to alement of mca of corresponding type
    */
   public void mergeCellArray(CellArray ca, CellArray[] mca)
   {
      if (ca==null)
         return;
      int tp = ca.getType();
      int nv = Cell.nv[tp];
      if (mca[tp]==null)
      {
         ca.cancelDuplicates();
         mca[tp] = ca;
         return;
      }
      CellArray ca0 = mca[tp];
      int nc0 = ca0.getNCells();
      int nc = ca.getNCells();
      int[] nds0 = ca0.getNodes();
      int[] nds = ca.getNodes();
      boolean[] or0 = ca0.orientations;
      boolean[] or = ca.orientations;
      int startBinSize = 2;
      int[][] indexBins = new int[65536][startBinSize];
      int[] indexN = new int[65536];
      int n = nc+nc0;
      for (int i = 0; i < indexN.length; i++)
         indexN[i] = 0;
      for (int i = 0; i < nc0; i++)
      {
         int h = shortHash(i, nv, nds0);
         int k = -1;
      s1:
         for (int j = 0; j < indexN[h]; j++)
         {
            int l = indexBins[h][j];
            for (int m = 0; m < nv; m++)
               if (nds0[nv * i + m] != nds0[nv * l + m])
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
      
      for (int i = 0; i < nc; i++)
      {
         int h = shortHash(i, nv, nds);
         int k = -1;
      s2:
         for (int j = 0; j < indexN[h]; j++)
         {
            int l = indexBins[h][j];
            if (l < nc0)
            {
               for (int m = 0; m < nv; m++)
                  if (nds[nv * i + m] != nds0[nv * l + m])
                     continue s2;        // cell i is not a duplicate of cell j in bin h
            }
            else
            {
               l -= nc0;
               for (int m = 0; m < nv; m++)
                  if (nds[nv * i + m] != nds[nv * l + m])
                     continue s2;        // cell i is not a duplicate of cell j in bin h
            }
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
            indexBins[h][indexN[h]] = i+nc0;
            indexN[h] += 1;                         // cell index added to bin
         }
      }
      
      int maxBin = 0;
      for (int i = 0; i < indexN.length; i++)
         if (maxBin < indexN[i]) maxBin = indexN[i];
      int[] xNodes = new int[nv * n];
      boolean[] xOrientations = new boolean[n];
      if (ca0.getDataIndices() != null && ca.getDataIndices() != null)
      {
         int[] di0 = ca0.getDataIndices();
         int[] di  = ca.getDataIndices();
         int[] xDataIndices = new int[n];
         for (int i = 0, l = 0; i < 65536; i++)
            for (int j = 0; j < indexN[i]; j++, l++)
            {
               int m = indexBins[i][j];
               if (m<nc0)
               {
                  for (int k = 0; k < nv; k++)
                     xNodes[l * nv + k] = nds0[m * nv + k];
                  xOrientations[l] = or0[m];
                  xDataIndices[l] = di0[m];
               }
               else
               {
                  m -= nc0;
                  for (int k = 0; k < nv; k++)
                     xNodes[l * nv + k] = nds[m * nv + k];
                  xOrientations[l] = or[m];
                  xDataIndices[l] = di[m];
               }
            }
         mca[tp] = new CellArray(this, tp, xNodes, xOrientations, xDataIndices);
      } else
      {
         for (int i = 0, l = 0; i < 65536; i++)
            for (int j = 0; j < indexN[i]; j++, l++)
            {
               int m = indexBins[i][j];
               if (m<nc0)
               {
                  for (int k = 0; k < nv; k++)
                     xNodes[l * nv + k] = nds0[m * nv + k];
                  xOrientations[l] = or0[m];
               }
               else
               {
                  m -= nc0;
                  for (int k = 0; k < nv; k++)
                     xNodes[l * nv + k] = nds[m * nv + k];
                  xOrientations[l] = or[m];
               }
            }
         mca[tp] = new CellArray(this, tp, xNodes, xOrientations, null);
      }
      
      mca[tp].setCleanedDuplicate(true);
      hash = RabinHashFunction.hash(cellArrayHashes);
   }
   
   public CellSet merge(CellSet cs)
   {
      CellSet out = new CellSet();
      out.setName(getName());
      for (int i = 0; i < cellArrays.length; i++)
      {
         CellArray outCa = cellArrays[i].cloneArray();
         mergeCellArray(outCa, new CellArray[]{cs.getCellArray(i)});
         out.setCellArray(outCa);
      }
      for (int i = 0; i < boundaryCellArrays.length; i++)
      {
         CellArray outCa = boundaryCellArrays[i].cloneArray();
         mergeCellArray(outCa, new CellArray[]{cs.getBoundaryCellArray(i)});
         out.setBoundaryCellArray(outCa);
      }
      return out;
   }
   
   public CellSet add(CellSet cs, int nNodes)
   {
      int[] nodes = null;
      boolean[] or = null;
      CellSet out = new CellSet();
      out.setName(getName());
      for (int i = 0; i < cellArrays.length; i++)
      {
         CellArray ca0 = cellArrays[i];
         CellArray ca1 = cs.getCellArray(i);
         if (ca0 == null && ca1 == null)
            continue;
         if (ca1 == null)
         {
             out.setCellArray(ca0.cloneArray());
             continue;
         }
         if (ca0 == null)
         {   
            int[] nodes1 = ca1.getNodes();
            nodes = nodes1.clone();
            for (int j = 0; j < nodes1.length; j++)
               nodes[j] += nNodes;
            or = ca1.getOrientations().clone();
            out.setCellArray(new CellArray(ca1.getType(), nodes, or, null));
             continue;
         }
         int[] nodes0 = ca0.getNodes();
         int[] nodes1 = ca1.getNodes();
         if (nodes0 == null)
            nodes = nodes1;
         else if (nodes1 == null)
            nodes = nodes0;
         else
         {
            nodes = new int[nodes0.length + nodes1.length];
            System.arraycopy(nodes0, 0, nodes, 0, nodes0.length);
            for (int j = 0, k = nodes0.length; j < nodes1.length; j++, k++)
               nodes[k] = nodes1[j] + nNodes;
         }
         boolean[] or0 = ca0.getOrientations();
         boolean[] or1 = ca1.getOrientations();
         if (or0 == null)
            or = or1;
         else  if (or1 == null)
            or = or0;
         else
         {
            or = new boolean[or0.length + or1.length];
            System.arraycopy(or0, 0, or, 0, or0.length);
            System.arraycopy(or1, 0, or, or0.length, or1.length);
         }
         out.setCellArray(new CellArray(ca0.getType(), nodes, or, null));
      }
      for (int i = 0; i < boundaryCellArrays.length; i++)
      {
         CellArray ca0 = boundaryCellArrays[i];
         CellArray ca1 = cs.getBoundaryCellArray(i);
         if (ca0 == null && ca1 == null)
            continue;
         if (ca1 == null)
         {
             out.setCellArray(ca0.cloneArray());
             continue;
         }
         if (ca0 == null)
         {   
            int[] nodes1 = ca1.getNodes();
            nodes = nodes1.clone();
            for (int j = 0; j < nodes1.length; j++)
               nodes[j] += nNodes;
            or = ca1.getOrientations().clone();
            out.setCellArray(new CellArray(ca1.getType(), nodes, or, null));
             continue;
         }
         int[] nodes0 = ca0.getNodes();
         int[] nodes1 = ca1.getNodes();
         if (nodes0 == null)
            nodes = nodes1;
         else if (nodes1 == null)
            nodes = nodes0;
         else
         {
            nodes = new int[nodes0.length + nodes1.length];
            System.arraycopy(nodes0, 0, nodes, 0, nodes0.length);
            for (int j = 0, k = nodes0.length; j < nodes1.length; j++, k++)
               nodes[k] = nodes1[j] + nNodes;
         }
         boolean[] or0 = ca0.getOrientations();
         boolean[] or1 = ca1.getOrientations();
         if (or0 == null)
            or = or1;
         else  if (or1 == null)
            or = or0;
         else
         {
            or = new boolean[or0.length + or1.length];
            System.arraycopy(or0, 0, or, 0, or0.length);
            System.arraycopy(or1, 0, or, or0.length, or1.length);
         }
         out.setBoundaryCellArray(new CellArray(ca0.getType(), nodes, or, null));
      }
      return out;
   }
   
   public void addCells(CellArray ca)
   {
      if (cellArrays[ca.getType()] == null)
         cellArrays[ca.getType()] = ca.cloneArray();
      else
      {
         CellArray oldCa = cellArrays[ca.getType()];
         int[] nodes = new int[oldCa.getNodes().length + ca.getNodes().length];
         System.arraycopy(oldCa.getNodes(), 0, nodes,0, oldCa.getNodes().length);
         System.arraycopy(ca.getNodes(), 0, nodes, oldCa.getNodes().length, ca.getNodes().length);
         boolean[] orientations = new boolean[oldCa.getNCells() + ca.getNCells()];
         System.arraycopy(oldCa.getOrientations(), 0, orientations, 0, oldCa.getNCells());
         System.arraycopy(ca.getOrientations(), 0, orientations, oldCa.getNCells(), ca.getNCells());
         if (oldCa.getDataIndices() != null && ca.getDataIndices() != null)
         {
            int[] dataIndices = new int[oldCa.getDataIndices().length + ca.getDataIndices().length];
            System.arraycopy(oldCa.getDataIndices(), 0, dataIndices, 0, oldCa.getDataIndices().length);
            System.arraycopy(ca.getDataIndices(), 0, dataIndices, oldCa.getDataIndices().length, ca.getDataIndices().length);
            cellArrays[ca.getType()] = new CellArray(ca.getType(), nodes, orientations, dataIndices);
         }
         else
            cellArrays[ca.getType()] = new CellArray(ca.getType(), nodes, orientations, null);
         cellArrays[ca.getType()].generateEdges();
      }
   }
   
   public CellSet triangulate()
   {
      CellSet triangulated = new CellSet(getName()+"_triangulated");
      for (CellArray cellArray : cellArrays)
         if (cellArray != null)
            triangulated.addCells(cellArray.triangulate());
      for (int i = 0; i < data.size(); i++)
         triangulated.addData(data.get(i));
      return triangulated;
   }


   public void generateExternFaces()
   {
// cleanup first       
       for (int i = 0; i < Cell.TYPES2D; i++) {
           boundaryCellArrays[i] = null;
       }
 
// then work       
      if (cellArrays[Cell.TETRA] == null && cellArrays[Cell.PYRAMID] == null &&
          cellArrays[Cell.PRISM] == null && cellArrays[Cell.HEXAHEDRON] == null)
      {
         boundaryCellArrays[Cell.TRIANGLE] = cellArrays[Cell.TRIANGLE];
         boundaryCellArrays[Cell.QUAD] = cellArrays[Cell.QUAD];
         boundaryHash = RabinHashFunction.hash(boundaryCellArrayHashes);
         return;
      }
      if (cellArrays[Cell.TRIANGLE] != null)
         boundaryCellArrays[Cell.TRIANGLE] = new CellArray(cellArrays[Cell.TRIANGLE]);
      if (cellArrays[Cell.QUAD] != null)
         boundaryCellArrays[Cell.QUAD]     = new CellArray(cellArrays[Cell.QUAD]);
      for (int i = 0; i<cellArrays.length; i++)
         if (cellArrays[i]!=null && Cell.dim[i]==3)
         {
            Vector<CellArray> faces = cellArrays[i].generateFaces();
            for (CellArray cellArray : faces)
               mergeCellArray(cellArray, boundaryCellArrays);
         }
      updateActiveNodes();
   }

   protected void createBoundaryCellNormals(float[] coords)
   {
      if (boundaryCellArrays == null || coords == null)
         return;
      for (int m = 0; m < Cell.TYPES2D; m++)
      {
         CellArray ar = boundaryCellArrays[m];
         if (ar == null || ar.getDim() != 2 || ar.getNCells() < 1)
             continue;
         float[] cellNormals = new float[3 * ar.getNCells()];
         if (ar == null || (ar.getType() != Cell.TRIANGLE && ar.getType() != Cell.QUAD))
             continue;
         int n = Cell.nv[ar.getType()];
         float[] v0 = new float[3];
         float[] v1 = new float[3];
         int[] nodes = ar.getNodes();
         boolean[] orientation = ar.getOrientations();
         int i;
         for (i = 0; i < ar.getNCells(); i++)
         {
            for (int j = 0; j < 3; j++)
            {
               try
               {
                  v0[j] = coords[3*nodes[i*n+1]+j] - coords[3*nodes[i*n]+j];
                  v1[j] = coords[3*nodes[i*n+2]+j] - coords[3*nodes[i*n]+j];
               } catch (Exception e)
               {
                  System.out.println(""+i);
               }
               
            }
            cellNormals[3*i]   = v0[1]*v1[2] - v0[2]*v1[1];
            cellNormals[3*i+1] = v0[2]*v1[0] - v0[0]*v1[2];
            cellNormals[3*i+2] = v0[0]*v1[1] - v0[1]*v1[0];
            float r = cellNormals[3*i]*cellNormals[3*i] +
                       cellNormals[3*i+1]*cellNormals[3*i+1] +
                       cellNormals[3*i+2]*cellNormals[3*i+2];
            if (r == 0) 
               continue;
            r = (float)(Math.sqrt(r));
            if (orientation[i])
               for (int j = 0; j < 3; j++)
                  cellNormals[3*i+j] /= r;
            else
               for (int j = 0; j < 3; j++)
                  cellNormals[3*i+j] /= -r;
         }
         ar.setCellNormals(cellNormals);
      }
   }

   public void generateDisplayData(float[] coords)
   {
      generateExternFaces();
      CellArray triEdges = null, quadEdges = null, seg = cellArrays[Cell.SEGMENT];
      createBoundaryCellNormals(coords);
      
      int nTriangleEdges = 0;
      int[] triEdgeNodes = null;
      int[] triEdgeFaces = null;
      float[] triNormals = null;
      boolean[] triOrientations = null;
      int[] triDataIndices = null;
      
      int nQuadEdges = 0;
      int[] quadEdgeNodes = null;
      int[] quadEdgeFaces = null;
      float[] quadNormals = null;
      boolean[] quadOrientations  = null;
      int[] quadDataIndices  = null;

      if (cellArrays[Cell.POINT] != null)
         boundaryCellArrays[Cell.POINT] = cellArrays[Cell.POINT];
      
      if (boundaryCellArrays[Cell.TRIANGLE] != null)
      {
         triEdges = boundaryCellArrays[Cell.TRIANGLE].generateEdges();
         nTriangleEdges = triEdges.getNCells();
         triEdgeNodes = triEdges.getNodes();
         triEdgeFaces = triEdges.getFaceIndices();
         triNormals = boundaryCellArrays[Cell.TRIANGLE].getCellNormals();
         triOrientations   = triEdges.getOrientations();
         triDataIndices   = triEdges.getDataIndices();
      }
      
      if (boundaryCellArrays[Cell.QUAD] != null)
      {
         quadEdges = boundaryCellArrays[Cell.QUAD].generateEdges();
         nQuadEdges = quadEdges.getNCells();
         quadEdgeNodes = quadEdges.getNodes();
         quadEdgeFaces = quadEdges.getFaceIndices();
         quadNormals = boundaryCellArrays[Cell.QUAD].getCellNormals();
         quadOrientations  = quadEdges.getOrientations();
         quadDataIndices  = quadEdges.getDataIndices();
      }
      
      int startBinSize = 12;
      int[][] indexBins = new int[65536][startBinSize];
      int[] indexN = new int[65536];
      int nFaceEdges = 0;
      for (int i = 0; i < indexN.length; i++)
         indexN[i] = 0;
      for (int i = 0; i < nTriangleEdges; i++)
      {
         int h = shortHash(i, 2, triEdgeNodes);
         int k = -1;
         int l = 0;
         for (int j = 0; j < indexN[h]; j+=2)
         {
            l = indexBins[h][j];
            if (triEdgeNodes[2 * i] != triEdgeNodes[2 * l] || triEdgeNodes[2 * i + 1] != triEdgeNodes[2 * l + 1])
               continue;                          // cell i is not a duplicate of cell j in bin h
            k = j;                                   // all nodes match - duplicate found
            break;
         }
         if (k != -1)                                // duplicate found - removing
         {
            if (indexBins[h][k] < 0)
               indexBins[h][k + 1] = -18001;
            else
            {
               int i0 = triEdgeFaces[i];
               int l0 = triEdgeFaces[l];
               double s = 0;
               for (int j = 0; j < 3; j++)
                  s += triNormals[3 * i0 + j] * triNormals[3 * l0 + j];
               indexBins[h][k + 1] = -(int)(18000*Math.acos(s)/Math.PI)-1;
            }
         } else
         {
            if (indexN[h] + 1 >= indexBins[h].length) // increase bin capacity
            {
               int[] t = new int[2 * indexBins[h].length];
               System.arraycopy(indexBins[h], 0, t, 0, indexBins[h].length);
               indexBins[h] = t;
            }
            indexBins[h][indexN[h]] = i;
            indexBins[h][indexN[h]+1] = triEdgeFaces[i];
            indexN[h] += 2;                           // cell index added to bin
            nFaceEdges += 1;
         }
      }
      
      for (int i = 0; i < nQuadEdges; i++)
      {
         int h = shortHash(i, 2, quadEdgeNodes);
         int k = -1;
         int l = 0;
         boolean triangleFound = false;
         for (int j = 0; j < indexN[h]; j+=2)
         {
            l = indexBins[h][j];
            if (l < nTriangleEdges)
            {
               triangleFound = true;
               if (quadEdgeNodes[2 * i] != triEdgeNodes[2 * l] || quadEdgeNodes[2 * i + 1] != triEdgeNodes[2 * l + 1])
                  continue;                           // cell i is not a duplicate of cell j in bin h
            }
            else
            {
               triangleFound = false;
               l -= nTriangleEdges;
               if (quadEdgeNodes[2 * i] != quadEdgeNodes[2 * l] || quadEdgeNodes[2 * i + 1] != quadEdgeNodes[2 * l + 1])
                  continue;                           // cell i is not a duplicate of cell j in bin h
            }
            k = j;                                    // all nodes match - duplicate found
            break;
         }
         if (k != -1)                                 // duplicate found - removing
         {
            if (indexBins[h][k + 1] < 0)
               indexBins[h][k + 1] = - 18001;
            else
            {
               int i0 = quadEdgeFaces[i];
               if (triangleFound)
               {
                  int l0 = triEdgeFaces[l];
                  double s = 0;
                  for (int j = 0; j < 3; j++)
                     s += quadNormals[3 * i0 + j] * triNormals[3 * l0 + j];
                  indexBins[h][k + 1] = -(int)(18000*Math.acos(s)/Math.PI)-1;
               }
               else
               {
                  int l0 = quadEdgeFaces[l];
                  double s = 0;
                  for (int j = 0; j < 3; j++)
                     s += quadNormals[3 * i0 + j] * quadNormals[3 * l0 + j];
                  indexBins[h][k + 1] = -(int)(18000*Math.acos(s)/Math.PI)-1;
               }
            }
         } else
         {
            if (indexN[h] + 1 >= indexBins[h].length) // increase bin capacity
            {
               int[] t = new int[2 * indexBins[h].length];
               System.arraycopy(indexBins[h], 0, t, 0, indexBins[h].length);
               indexBins[h] = t;
            }
            indexBins[h][indexN[h]] = i+nTriangleEdges;
            indexBins[h][indexN[h]+1] = quadEdgeFaces[i];
            indexN[h] += 2;                         // cell index added to bin
            nFaceEdges += 1;
         }
      }

      int maxBin = 0;
      for (int i = 0; i < indexN.length; i++)
         if (maxBin < indexN[i]) maxBin = indexN[i];
//      System.out.println("hashing "+(nQuadEdges+nTriangleEdges)+" cells into 65536 bins, max bin = "+maxBin);
      int nEdges = nFaceEdges;
      if (seg != null)
         nEdges += seg.getNCells();
      int[] xNodes = new int[2 * nEdges];
      boolean[] xOrientations = new boolean[nEdges];
      float[] dihedrals = new float[nEdges];
      int l = 0;
      
      if ((triEdges == null || triDataIndices != null) && 
          (quadEdges == null || quadDataIndices != null) && 
          (cellArrays[Cell.SEGMENT] == null || cellArrays[Cell.SEGMENT].getDataIndices() != null))
      {
         int[] xDataIndices = new int[nEdges];
         for (int i = 0; i < 65536; i++)
            for (int j = 0; j < indexN[i]; j+=2, l++)
            {
               int m = indexBins[i][j];
               if (m<nTriangleEdges)
               {
                  for (int k = 0; k < 2; k++)
                     xNodes[l * 2 + k] = triEdgeNodes[m * 2 + k];
                  xOrientations[l] = true;
                  xDataIndices[l] = triDataIndices[m];
               }
               else
               {
                  m -= nTriangleEdges;
                  for (int k = 0; k < 2; k++)
                     xNodes[l * 2 + k] = quadEdgeNodes[m * 2 + k];
                  xOrientations[l] = quadOrientations[m];
                  xDataIndices[l] = quadDataIndices[m];
               }
               if (indexBins[i][j+1] < 0)
                  dihedrals[l] = (-indexBins[i][j+1]-1)/100.f;
               else
                  dihedrals[l] = 181.f;
            }
         if (seg != null)
         {
            int nSeg = seg.getNCells();
            int[] segNodes = seg.getNodes();
            boolean[] segOrientations  = seg.getOrientations();
            int[] segDataIndices  = seg.getDataIndices();
            for (int i = 0; i < nSeg; i++, l++)
            {
               for (int k = 0; k < 2; k++)
                   xNodes[l * 2 + k] = segNodes[2 * i + k];
               xOrientations[l] = segOrientations[i];
               xDataIndices[l] = segDataIndices[i];
               dihedrals[l] = 181.f;
            }
         }
         boundaryCellArrays[Cell.SEGMENT] = new CellArray(this, Cell.SEGMENT, xNodes, xOrientations, xDataIndices);
         boundaryCellArrays[Cell.SEGMENT].setCellDihedrals(dihedrals);
      } else
      {
         for (int i = 0; i < 65536; i++)
            for (int j = 0; j < indexN[i]; j+=2, l++)
            {
               int m = indexBins[i][j];
               if (m<nTriangleEdges)
               {
                  for (int k = 0; k < 2; k++)
                     xNodes[l * 2 + k] = triEdgeNodes[m * 2 + k];
                  xOrientations[l] = triOrientations[m];
               }
               else
               {
                  m -= nTriangleEdges;
                  for (int k = 0; k < 2; k++)
                     xNodes[l * 2 + k] = quadEdgeNodes[m * 2 + k];
                  xOrientations[l] = quadOrientations[m];
               }
               dihedrals[l] = (-indexBins[i][j+1]-1)/100.f;
            }
         if (seg != null)
         {
            int nSeg = seg.getNCells();
            int[] segNodes = seg.getNodes();
            boolean[] segOrientations  = seg.getOrientations();
            for (int i = 0; i < nSeg; i++, l++)
            {
               for (int k = 0; k < 2; k++)
                   xNodes[l * 2 + k] = segNodes[2 * i + k];
               xOrientations[l] = segOrientations[i];
               dihedrals[l] = 180.f;
            }
         }
         boundaryCellArrays[Cell.SEGMENT] = new CellArray(this, Cell.SEGMENT, xNodes, xOrientations, null);
         boundaryCellArrays[Cell.SEGMENT].setCellDihedrals(dihedrals);
      }

      hash = RabinHashFunction.hash(cellArrayHashes);
   }

   public boolean isStructCompatible(CellSet s)
   {
      if (s.getHash()!=hash)
         return false;
      for (int i = 0; i<cellArrays.length; i++)
      {
         if (cellArrays[i] == null)
         {
            if (s.getCellArray(i) != null)
               return false;
            continue;
         }
         if (s.getCellArray(i) == null ||
             !cellArrays[i].isStructCompatible(s.getCellArray(i)))
            return false;
      }
      return true;
   }
   
   private void updateActiveNodes()
   {
      int maxNode = -1;
      for (int i = 0; i < cellArrays.length; i++)
      {
         CellArray cellArray = cellArrays[i];
         if (cellArray != null && cellArray.getNodes() != null)
         {
            int[] nodes = cellArray.getNodes();
            for (int j = 0; j < nodes.length; j++)
               if (nodes[j] > maxNode)
                  maxNode = nodes[j];
         }
      }
      for (int i = 0; i < boundaryCellArrays.length; i++)
      {
         CellArray cellArray = boundaryCellArrays[i];
         if (cellArray != null && cellArray.getNodes() != null)
         {
            int[] nodes = cellArray.getNodes();
            for (int j = 0; j < nodes.length; j++)
               if (nodes[j] > maxNode)
                  maxNode = nodes[j];
         }
      }
      boolean[] activeNodes = new boolean[maxNode + 1];
      for (int i = 0; i < activeNodes.length; i++)
         activeNodes[i] = false;
      nActiveNodes = 0;
      for (int i = 0; i < cellArrays.length; i++)
      {
         CellArray cellArray = cellArrays[i];
         if (cellArray != null && cellArray.getNodes() != null)
         {
            int[] nodes = cellArray.getNodes();
            for (int j = 0; j < nodes.length; j++)
               if (!activeNodes[nodes[j]])
               {
                  activeNodes[nodes[j]] = true;
                  nActiveNodes += 1;
               }
         }
      }
      for (int i = 0; i < boundaryCellArrays.length; i++)
      {
         CellArray cellArray = boundaryCellArrays[i];
         if (cellArray != null && cellArray.getNodes() != null)
         {
            int[] nodes = cellArray.getNodes();
            for (int j = 0; j < nodes.length; j++)
               if (!activeNodes[nodes[j]])
               {
                  activeNodes[nodes[j]] = true;
                  nActiveNodes += 1;
               }
         }
      }
      int[] nodes = new int[nActiveNodes];
      boolean[] orientations = new boolean[nActiveNodes];
      for (int i = 0, j = 0; i < activeNodes.length; i++)
         if (activeNodes[i])
         {
            nodes[j] = i;
            j += 1;
         }
      CellArray bPCA = new CellArray(Cell.POINT, nodes, orientations, null);
      boundaryCellArrays[Cell.POINT] = bPCA;
   }

   public int getnActiveNodes()
   {
      if (nActiveNodes < 0)
         updateActiveNodes();
      return nActiveNodes;
   }

   public void setnActiveNodes(int nActiveNodes)
   {
      this.nActiveNodes = nActiveNodes;
   }

    public int getNNodes()
    {
        if (data == null || data.isEmpty())
           return 0;
        return data.get(0).getNData();
    }
    
    public void addGeometryData(int nThreads, float[] coords)
    {
       for (int i = 0; i < cellArrays.length; i++)
       {
          CellArray ca = cellArrays[i];
          fireStatusChanged((float)i / cellArrays.length);
          if (ca != null)
             ca.addGeometryData(nThreads, coords);
       }
    }
 
   public float getMeasure(int dim, float[] coords)
   {
      float r = 0;
      switch(dim)
      {
         case 0:
            if (cellArrays[Cell.POINT] != null)
               r += (float)(cellArrays[Cell.POINT].getNCells());
         case 1:
            if (cellArrays[Cell.SEGMENT] != null)
               r += cellArrays[Cell.SEGMENT].getMeasure(coords);
         case 2:
            if (cellArrays[Cell.TRIANGLE] != null)
               r += cellArrays[Cell.TRIANGLE].getMeasure(coords);
            if (cellArrays[Cell.QUAD] != null)
               r += cellArrays[Cell.QUAD].getMeasure(coords);
         case 3:
             if (cellArrays[Cell.TETRA] != null)
                r += cellArrays[Cell.TETRA].getMeasure(coords);
             if (cellArrays[Cell.PYRAMID] != null)
                r += cellArrays[Cell.PYRAMID].getMeasure(coords); 
             if (cellArrays[Cell.PRISM] != null)
                r += cellArrays[Cell.PRISM].getMeasure(coords);    
             if (cellArrays[Cell.HEXAHEDRON] != null)
                r += cellArrays[Cell.HEXAHEDRON].getMeasure(coords);
      }
      return r;
   }  
   
   public boolean isDataCompatible(CellSet s, boolean checkComponentNames)
   {
      if (getNData() != s.getNData())
         return false;
      for (int i = 0; i < getNData(); i++)
         if (!getData(i).fullyCompatibleWith(s.getData(i), checkComponentNames))
            return false;
      return true;
   }
   
   protected transient FloatValueModificationListener statusListener = null;

   public void addFloatValueModificationListener(FloatValueModificationListener listener)
   {
      if (statusListener == null)
         this.statusListener = listener;
      else
         System.out.println(""+this+": only one status listener can be added");
   }
   
   public void clearFloatValueModificationListener()
   {
      statusListener = null;
   }

   protected void fireStatusChanged(float status)
   {
       FloatValueModificationEvent e = new FloatValueModificationEvent(this, status, true);
       if (statusListener != null)
          statusListener.floatValueChanged(e);
   }
   
}
