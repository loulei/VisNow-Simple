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

import java.util.Vector;
import pl.edu.icm.visnow.datasets.cells.Cell;
import pl.edu.icm.visnow.datasets.cells.Tetra;
import pl.edu.icm.visnow.datasets.cells.SimplexPosition;
import pl.edu.icm.visnow.datasets.cells.Triangle;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.lib.utils.VNFloatFormatter;

/**
 *
 * @author Krzysztof S. Nowinski
 *   <p>   University of Warsaw, ICM
 */
public class IrregularField extends Field
{
   private static final long serialVersionUID = 1728066965898700139L;
   protected Vector<CellSet> cellSets = new Vector<CellSet>();
   protected int[][][] cellGuide;
   protected int[][][] cellGuideTop;
   protected int[] cellGuideSizes = new int[4];
   protected float[] normals;
   

   public IrregularField()
   {
      nSpace = 3;
      timeCoords = new TimeData<float[]>();
   }
   
   public final int getType()
   {
      return Field.IRREGULAR;
   }

   @Override
   public String toString()
   {
      return "Irregular field " +
              nNodes +" nodes "+
              getNFrames() +" time frames " +
              data.size() + " data components";
   }
   
   public String shortDescription()
   {
      StringBuilder s = new StringBuilder();
      s.append("<html>").append(nNodes).append(" nodes");
      int n = 0;
      for (CellSet cs : cellSets)
         n += cs.getNCells(); 
      s.append("<p>").append(n);
      if (getNFrames() > 1)
         s.append("<p>").append(getNFrames()).append(" timesteps");
      s.append("").append(data.size()).append(" components</html>");
      return s.toString();
   }

    @Override
    public String description() {
        StringBuffer s = new StringBuffer();
        s.append(String.format("Irregular field %d-space, %d nodes", nSpace, nNodes));
        if (trueDim > 0) {
            s.append(", true " + trueDim + "-dim ");
        }
        if (getAllTimesteps().length > 1) {
            s.append(", ");
            s.append(getAllTimesteps().length);
            s.append(" timesteps<p>");
            s.append("time range ");
            s.append(VNFloatFormatter.defaultRangeFormat(getStartTime()));
            s.append(timeUnit);
            s.append(":");
            s.append(VNFloatFormatter.defaultRangeFormat(getEndTime()));
            s.append(timeUnit);
        }
        s.append("<p>geometric extents</p>");
        for (int i = 0; i < nSpace; i++) {
            s.append("<p>[");
            s.append(VNFloatFormatter.defaultRangeFormat(extents[0][i]));
            s.append(", ");
            s.append(VNFloatFormatter.defaultRangeFormat(extents[1][i]));
            s.append("]</p> ");
        }
        s.append("<p>physical extents</p>");
        for (int i = 0; i < nSpace; i++) {
            s.append("<p>[");
            s.append(VNFloatFormatter.defaultRangeFormat(physExts[0][i]));
            s.append(", ");
            s.append(VNFloatFormatter.defaultRangeFormat(physExts[1][i]));
            s.append("]</p> ");
        }
        
        if (data.size() > 0) {
            s.append("<p><BR>Node data components:");
            s.append("<font size=\"-2\"> <TABLE border=\"0\">"
                    + "<TR><TD>name</td><TD>veclen</td><td>type</td><td>ts</td><td>min</td><td>max</td><td>physMin</td><td>physMax</td></tr>");

            for (int i = 0; i < data.size(); i++) {
                s.append(getData(i).description());
            }
            s.append("</TABLE></font>");
        }
        s.append("<p><br>Cell sets:<p>");
        for (CellSet set : cellSets) {
            s.append(set.description());
        }
        return "<html>" + s + "</html>";
    }

   @Override
   public IrregularField clone()
   {
      IrregularField clone = new IrregularField();
      clone.setNNodes(nNodes);
      clone.setNSpace(nSpace);
      clone.setCoords(timeCoords);
      for (CellSet cellSet : cellSets)
      {
         clone.addCellSet(cellSet);
      }
      for (DataArray dataArray : data)
         clone.addData(dataArray);
      return clone;
   }


   public IrregularField cloneBase()
   {
      IrregularField clone = new  IrregularField();
      clone.setNNodes(nNodes);
      clone.setNSpace(nSpace);
      clone.setCoords(timeCoords);
      for (CellSet cellSet : cellSets)
      {
         clone.addCellSet(cellSet);
      }
      return clone;
   }
   
   public IrregularField cloneDeep()
   {
      IrregularField clone = new  IrregularField();
      clone.setNSpace(this.nSpace);
      clone.setNNodes(nNodes);
      clone.setCoords((TimeData<float[]>)this.timeCoords.clone());
      for (DataArray dataArray : data)
         clone.addData(dataArray.cloneDeep(dataArray.getName()));
      for (CellSet cellSet : cellSets)
      {
         clone.addCellSet(cellSet);
      }
      return clone;
   }

   public IrregularField cloneNodesData()
   {
      IrregularField clone = new  IrregularField();
      clone.setNNodes(nNodes);
      clone.setNSpace(this.nSpace);
      clone.setNNodes(nNodes);
      clone.setCoords(this.timeCoords);
      for (DataArray dataArray : data)
         clone.addData(dataArray);
      return clone;
   }

   public IrregularField cloneCoords()
   {
      IrregularField clone = new  IrregularField();
      clone.setNSpace(this.nSpace);
      clone.setNNodes(nNodes);
      clone.setCoords(this.timeCoords);
      return clone;
   }

   public IrregularField cloneCoordsDeep()
   { // TODO: should be removed after cloneDeep is working!!!
      IrregularField clone = new  IrregularField();
      clone.setNSpace(this.nSpace);
      clone.setNNodes(nNodes);
      clone.setCoords((TimeData<float[]>)this.timeCoords.clone());
      for (CellSet cellSet : cellSets)
      {
         clone.addCellSet(cellSet);
      }
      for (DataArray dataArray : data)
         clone.addData(dataArray);
      return clone;
   }

   public IrregularField cloneStructure()
   {
      IrregularField clone = new  IrregularField();
      clone.setNSpace(this.nSpace);
      clone.setNNodes(nNodes);
      clone.setCoords(this.timeCoords);
      for (CellSet cellSet : cellSets)
      {
         clone.addCellSet(cellSet);
      }
      return clone;
   }

   @Override
   public float[][] getExtents()
   {
      return extents;
   }

   @Override
   public float[] getNormals()
   {
      return normals;
   }

   @Override
   public void setNormals(float[] normals)
   {
      this.normals = normals;
   }

   @Override
   public float[] getNodeCoords(int k)
   {
      float[] c = new float[nSpace];
      System.arraycopy( getCoords(), k*nSpace, c, 0, nSpace);
      return c;
   }

   public void setNodeCoords(int k, float[] c)
   {
      System.arraycopy(c, 0, getCoords(), k*nSpace, nSpace);
   }

   @Override
   public float[] getInterpolatedData(float[] point, int index)
   {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public DataArray interpolateDataToMesh(Field mesh, DataArray da)
   {
      throw new UnsupportedOperationException("Not supported yet.");
   }
   
   public int getNCellSets()
   {
      return cellSets.size();
   }

   public Vector<CellSet> getCellSets()
   {
      return cellSets;
   }

   public CellSet getCellSet(int n)
   {
      if (n<0 || n>=cellSets.size())
         return null;
      return cellSets.get(n);
   }

   public void addCellSet(CellSet newSet)
   {
      cellSets.add(newSet);
   }

   public void replaceCellSet(int n, CellSet newSet)
   {
      if (n<0) return;
      if (n>=cellSets.size())
         cellSets.add(newSet);
      else
         cellSets.set(n, newSet);
   }
   
   public IrregularField flattenCellSets()
   {
      IrregularField out = this.clone();
      out.cellSets = new Vector<CellSet>();
      int nCellSets = cellSets.size();
      int[] status = new int[nCellSets];
      for (int i = 0; i < status.length; i++)
         status[i] = 0;
      for (int inSet = 0; inSet < status.length; inSet++)
      {
         if (status[inSet] != 0)
            continue;
         status[inSet] = 1;
         CellSet inCS = cellSets.get(inSet);
         CellSet outCS = new CellSet(inCS.getName());
         int[][] dataLengths = new int[inCS.getNData()][nCellSets + 1];
         for (int i = 0; i < dataLengths.length; i++)
            dataLengths[i][0] = 0;
         int[][] cellArraysLengths = new int[Cell.TYPES][nCellSets + 1];
         for (int i = 0; i < cellArraysLengths.length; i++)
            cellArraysLengths[i][0] = 0;
         int nFlattened = 0;
         for (int addedSet = inSet; addedSet < status.length; addedSet++)
         {
            if (status[addedSet] != 0)
               continue;
            CellSet addedCS = cellSets.get(addedSet);
            if (!inCS.isDataCompatible(addedCS, true))
               continue;
            nFlattened += 1;
            for (int i = 0; i < cellArraysLengths.length; i++)
               if (addedCS.getCellArray(i) != null)
                  cellArraysLengths[i][nFlattened] = cellArraysLengths[i][nFlattened - 1] + 
                                                     addedCS.getCellArray(i).getNCells();
            for (int i = 0; i < dataLengths.length; i++)
               dataLengths[i][nFlattened] = dataLengths[i][nFlattened - 1] + 
                                            addedCS.getData(i).getNData();
            status[addedSet] = 1;
         }
         int[][] newCellArrays = new int[Cell.TYPES][];
         for (int i = 0; i < newCellArrays.length; i++)
         {
            newCellArrays[i] = new int[Cell.nv[i] * cellArraysLengths[i][nFlattened]];
            
         }
      }
      return out;
   }

   @Override
   public boolean isStructureCompatibleWith(Field f)
   {
      if (!(f instanceof IrregularField) || f.getNNodes()!=nNodes) return false;
      IrregularField irf = (IrregularField)f;
      if (irf.getNCellSets() != cellSets.size()) return false;
      for (int i = 0; i < cellSets.size(); i++)
         if (!cellSets.get(i).isStructCompatible(irf.getCellSet(i))) return false;
      return true;
   }

   @Override
   public boolean isDataCompatibleWith(Field f)
   {
      if (f == null || !super.isDataCompatibleWith(f))
         return false;
      IrregularField irf = (IrregularField)f;
      if (irf.getNCellSets() != cellSets.size()) return false;
      for (int i = 0; i < cellSets.size(); i++)
         if (!cellSets.get(i).isDataCompatibleWith(irf.getCellSet(i))) return false;
      return true;
   }
   
   @Override
   public void checkPureDim()
   {
      trueDim = -1;
      int maxCellDim = 0;
      for (CellSet cs : cellSets)
      {
         if (cs.getCellArray(Cell.TETRA) != null      && cs.getCellArray(Cell.TETRA).getNCells() > 0 ||
             cs.getCellArray(Cell.PYRAMID) != null    && cs.getCellArray(Cell.PYRAMID).getNCells() > 0 ||
             cs.getCellArray(Cell.PRISM) != null      && cs.getCellArray(Cell.PRISM).getNCells() > 0 ||
             cs.getCellArray(Cell.HEXAHEDRON) != null && cs.getCellArray(Cell.HEXAHEDRON).getNCells() > 0)
         {
            trueDim = 3;
            return;
         }
         if (cs.getCellArray(Cell.TRIANGLE) != null && cs.getCellArray(Cell.TRIANGLE).getNCells() > 0 ||
             cs.getCellArray(Cell.QUAD) != null     && cs.getCellArray(Cell.QUAD).getNCells() > 0)
            maxCellDim = Math.max(maxCellDim, 2);
         if (cs.getCellArray(Cell.SEGMENT) != null && cs.getCellArray(Cell.SEGMENT).getNCells() > 0)
            maxCellDim = Math.max(maxCellDim, 1);
      }
      switch (maxCellDim)
      {
      case 0:
         return;
      case 1:
         trueDim = 1;
         switch (nSpace)
         {
         case 1:
            return;
         case 2:
            for (int i = 0; i < nNodes; i++)
               if (coords[2 * i + 1] != 0)
               {
                  trueDim = -1;
                  return;
               }
            break;
         case 3:
            for (int i = 0; i < nNodes; i++)
               if (coords[3 * i + 1] != 0 || coords[3 * i + 2] != 0)
               {
                  trueDim = -1;
                  return;
               }
            break;
         }
         break;
      case 2:
         trueDim = 2;
         switch (nSpace)
         {
            case 2:
            break;
         case 3:
            for (int i = 0; i < nNodes; i++)
               if (coords[3 * i + 2] != 0)
               {
                  trueDim = -1;
                  return;
               }
            break;
         }
         break;
      }
   }

   public int[][][] getCellGuide()
   {
      return cellGuide;
   }
   
   public void createGeoTree()
   {
      int[] cellArrayBegin = new int[] {0, 1, 2, 4};
      cellGuide = new int[4][][];
      cellGuideTop = new int[4][][];
      int n = cellSets.size();
      cellGuide[0] = new int[n][1];
      cellGuide[1] = new int[n][1];
      cellGuide[2] = new int[n][2];
      cellGuide[3] = new int[n][4];
      cellGuideTop[0] = new int[n][1];
      cellGuideTop[1] = new int[n][1];
      cellGuideTop[2] = new int[n][2];
      cellGuideTop[3] = new int[n][4];
      float[] c = timeCoords.get(currentFrame);
      for (int i = 0; i < cellGuide.length; i++)
      {
         int m = 0;
         for (int j = 0; j < n; j++)
         {
            CellSet cs = cellSets.get(j);
            if (cs != null)
               for (int k = 0; k < cellGuide[i][j].length; k++)
               {
                  cellGuide[i][j][k] = m;
                  CellArray ca = cs.getCellArray(cellArrayBegin[i] + k);
                  if (ca != null)
                      m+= ca.getNCells();
                  cellGuideTop[i][j][k] = m;
               }
         }
         cellGuideSizes[i] = m;
      }
      int[] cells = new int[cellGuideSizes[3]];
      for (int i = 0; i < cells.length; i++)
         cells[i] = i;
      cellExtents = new float[6][cellGuideSizes[3]];
      float[] cellLow = new float[3];
      float[] cellUp  = new float[3];
      for (int ics = 0, m = 0; ics < cellSets.size(); ics++)
      {
         CellSet cs = cellSets.get(ics);
         if (cs != null)
            for (int k = 0; k < 4; k++)
            {
               CellArray ca = cs.getCellArray(4 + k);
               if (ca == null)
                  continue;
               int cellNodes = Cell.nv[4 + k];
               int[] nodes = ca.getNodes();
               for (int i = 0; i < ca.getNCells(); i++, m++)
               {
                  for (int j = 0; j < 3; j++)
                  {
                     cellLow[j] = Float.MAX_VALUE;
                     cellUp[j]  = -Float.MAX_VALUE;
                  }
                  for (int node = 0; node < cellNodes; node++)
                  {
                     int l = 3 * nodes[i * cellNodes + node];
                     for (int j = 0; j < 3; j++)
                     {
                        float x = c[l + j];
                        if (x < cellLow[j]) cellLow[j] = x;
                        if (x > cellUp[j])  cellUp[j]  = x;
                     }
                  }
                  for (int j = 0; j < 3; j++)
                  {
                     cellExtents[j][m] = cellLow[j];
                     cellExtents[j+3][m] = cellUp[j];
                  }
               }
            }
      }
      
      int maxThr = Runtime.getRuntime().availableProcessors() - 1;
      if (maxThr < 1) maxThr = 1;
      int maxParallelLevel = 1 + (int)(Math.log(maxThr) / Math.log(2.));
      geoTree =  new GeoTreeNode(cells, cellExtents, 0, maxParallelLevel);
      pCreateGeoTree(geoTree, maxThr);
   }

   private Cell getCell(int n)
   {
      for (int j = cellSets.size() -  1; j >= 0; j--)
         for (int k = cellGuide[3][j].length - 1; k >= 0; k--)
            if (n >= cellGuide[3][j][k] && n < cellGuideTop[3][j][k])
            {
               CellArray c = cellSets.get(j).getCellArray(4 + k);
               int m = Cell.nv[c.getType()];
               int[] verts = new int[m];
               System.arraycopy(c.getNodes(), m * (n - cellGuide[3][j][k]), verts, 0, m);
               return Cell.createCell(c.getType(), verts, true);
            }
      return null;
   }

   public SimplexPosition getFieldCoords(float[] p)
   {
      if (coords == null)
         coords = timeCoords.get(currentFrame);
      int[] cells = geoTree.getCells(p);
cellsLoop:
      for (int i = 0; i < cells.length; i++)
      {
         int c = cells[i];
         for (int j = 0; j < trueDim; j++)
         {
            if (cellExtents[j][c]     > p[j]) continue cellsLoop;
            if (cellExtents[j + 3][c] < p[j]) continue cellsLoop;
         }
         Cell cell = getCell(c);
         Cell[] simplices = cell.triangulation();
         switch (trueDim)
         {
         case 3:
            for (int j = 0; j < simplices.length; j++)
            {
               if (simplices[j].getType() == Cell.TETRA)
               {
                   SimplexPosition result = ((Tetra)simplices[j]).barycentricCoords(p, coords);
                   if (result != null)
                   {
                      result.cell = cell;
                      result.cells = cells.clone();
                      return result;
                   }
               }
            }
            break;
         case 2:
            for (int j = 0; j < simplices.length; j++)
            {
               if (simplices[j].getType() == Cell.TRIANGLE)
               {
                   SimplexPosition result = ((Triangle)simplices[j]).barycentricCoords(p, coords);
                   if (result != null)
                   {
                      result.cell = cell;
                      result.cells = cells.clone();
                      return result;
                   }
               }
            }
         }
      }
      return null;
   }
   
   public boolean getFieldCoords(float[] p, SimplexPosition result)
   {
      Cell[] simplices;
      float[] res;
      int[] cells;
      switch (trueDim)
      {
      case 3:
         res = bCoords((Tetra)result.simplex, p);
         if (res != null)
         {
            result.verts = result.simplex.getVertices();
            result.coords = res;
            return true;
         }
         if (result.cell != null && result.cell.getType() != Cell.TETRA)
         {
            simplices = result.cell.triangulation();
            for (int j = 0; j < simplices.length; j++)
            {
               if (simplices[j].getType() == Cell.TETRA)
               {
                  res = bCoords((Tetra)simplices[j], p);
                  if (res != null)
                  {
                     result.simplex = simplices[j];
                     result.verts = result.simplex.getVertices();
                     result.coords = res;
                     return true;
                  }
               }
            }
         }
         if (result.cells != null)
         {
   cLoop:
            for (int i = 0; i < result.cells.length; i++)
            {
               int c = result.cells[i];
               if (getCell(c) == result.cell)
                  continue cLoop;
               for (int j = 0; j < 3; j++)
                  if (cellExtents[j][c] > p[j] || cellExtents[j + 3][c] < p[j])
                     continue cLoop;
               Cell cell = getCell(c);
               simplices = cell.triangulation();
               for (int j = 0; j < simplices.length; j++)
               {
                  if (simplices[j].getType() == Cell.TETRA)
                  {
                     res = bCoords((Tetra)simplices[j], p);
                     if (res != null)
                     {
                        result.simplex = simplices[j];
                        result.cell = cell;
                        result.verts = result.simplex.getVertices();
                        result.coords = res;
                        return true;
                     }
                  }
               }
            }
         }
         cells = geoTree.getCells(p);
   cellsLoop:
         for (int i = 0; i < cells.length; i++)
         {
            int c = cells[i];
            for (int j = 0; j < 3; j++)
            {
               if (cellExtents[j][c] > p[j])
                  continue cellsLoop;
               if (cellExtents[j + 3][c] < p[j])
                  continue cellsLoop;
            }
            simplices = getCell(c).triangulation();
            for (int j = 0; j < simplices.length; j++)
            {
               if (simplices[j].getType() == Cell.TETRA)
               {
                  res = bCoords((Tetra)simplices[j], p);
                  if (res != null)
                  {
                     result.simplex = simplices[j];
                     result.cell = getCell(c);
                     result.cells = cells;
                     result.verts = result.simplex.getVertices();
                     result.coords = res;
                     return true;
                  }
               }
            }
         }
         break;
      case 2:
         res = bCoords((Triangle)result.simplex, p);
         if (res != null)
         {
            result.verts = result.simplex.getVertices();
            result.coords = res;
            return true;
         }
         if (result.cell != null && result.cell.getType() != Cell.TETRA)
         {
            simplices = result.cell.triangulation();
            for (int j = 0; j < simplices.length; j++)
            {
               if (simplices[j].getType() == Cell.TETRA)
               {
                  res = bCoords((Triangle)simplices[j], p);
                  if (res != null)
                  {
                     result.simplex = simplices[j];
                     result.verts = result.simplex.getVertices();
                     result.coords = res;
                     return true;
                  }
               }
            }
         }
         if (result.cells != null)
         {
   cLoop:
            for (int i = 0; i < result.cells.length; i++)
            {
               int c = result.cells[i];
               if (getCell(c) == result.cell)
                  continue cLoop;
               for (int j = 0; j < 3; j++)
                  if (cellExtents[j][c] > p[j] || cellExtents[j + 3][c] < p[j])
                     continue cLoop;
               Cell cell = getCell(c);
               simplices = cell.triangulation();
               for (int j = 0; j < simplices.length; j++)
               {
                  if (simplices[j].getType() == Cell.TRIANGLE)
                  {
                     res = bCoords((Triangle)simplices[j], p);
                     if (res != null)
                     {
                        result.simplex = simplices[j];
                        result.cell = cell;
                        result.verts = result.simplex.getVertices();
                        result.coords = res;
                        return true;
                     }
                  }
               }
            }
         }
         cells = geoTree.getCells(p);
   cellsLoop:
         for (int i = 0; i < cells.length; i++)
         {
            int c = cells[i];
            for (int j = 0; j < 3; j++)
            {
               if (cellExtents[j][c] > p[j])
                  continue cellsLoop;
               if (cellExtents[j + 3][c] < p[j])
                  continue cellsLoop;
            }
            simplices = getCell(c).triangulation();
            for (int j = 0; j < simplices.length; j++)
            {
               if (simplices[j].getType() == Cell.TRIANGLE)
               {
                  res = bCoords((Triangle)simplices[j], p);
                  if (res != null)
                  {
                     result.simplex = simplices[j];
                     result.cell = getCell(c);
                     result.cells = cells;
                     result.verts = result.simplex.getVertices();
                     result.coords = res;
                     return true;
                  }
               }
            }
         }
         break;
      }
      return false;
   }

   @Override
   public IrregularField triangulate()
   {
      IrregularField outField = cloneNodesData();
      for (CellSet cs: cellSets)
      {
         CellSet trCs = cs.triangulate();
         trCs.generateDisplayData(timeCoords.get(currentFrame));
         outField.addCellSet(trCs);
      }
      outField.setExtents(extents);
      return outField;
   }
}
