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

package pl.edu.icm.visnow.lib.types;

import java.util.Vector;
import pl.edu.icm.visnow.datasets.*;
import pl.edu.icm.visnow.datasets.cells.Cell;

/**
 * @author Krzysztof S. Nowinski (know@icm.edu.pl) Warsaw University, Interdisciplinary Centre for
 * Mathematical and Computational Modelling
 */
public class VNField implements VNDataSchemaInterface
{

   protected Field field = null;
   private float[][] valueHistograms = null;
   private float[] minVal = null;
   private float[] avgVal = null;
   private float[] maxVal = null;
   private float[] stdDevVal = null;

   public VNField()
   {
   }

   public VNField(Field inField)
   {
      field = inField;
      field.checkPureDim();
   }

   public Field getField()
   {
      return field;
   }
   
   public int getTrueDim()
   {
      return field.getTrueDim();
   }

   @Override
   public String toString()
   {
      if (field == null)
      {
         return "empty field";
      }

      return field.description();
   }

   public float[] getAvgVal()
   {
      return avgVal;
   }

   public float[] getStdDevVal()
   {
      return stdDevVal;
   }

   public float[][] getValueHistograms()
   {
      return valueHistograms;
   }

   public float[] getMaxVal()
   {
      return maxVal;
   }

   public float[] getMinVal()
   {
      return minVal;
   }

   @Override
   public Class getVnDataType()
   {
      return VNField.class;
   }

   @Override
   public boolean isEmpty()
   {
      return (field == null);
   }

   @Override
   public boolean isField()
   {
      return true;
   }

   @Override
   public int getNSpace()
   {
      if (field == null)
      {
         return -1;
      }
      return field.getNSpace();
   }

   @Override
   public boolean isRegular()
   {
      return (field != null && field instanceof RegularField);
   }

   @Override
   public int getNDims()
   {
      return isRegular() ? ((RegularField)field).getDims().length : -1;
   }

   @Override
   public int[] getDims()
   {
      return isRegular() ? ((RegularField)field).getDims() : null;
   }

   @Override
   public boolean isAffine()
   {
      return isRegular() && field.getCoords() == null;
   }

   @Override
   public boolean isCoords()
   {
      if (this.isRegular())
      {
         return (((RegularField) field).getCoords() != null);
      } else
      {
         return false;
      }
   }

   @Override
   public int getNData()
   {
      if (field != null)
      {
         return field.getNData();
      } else
      {
         return 0;
      }
   }

   @Override
   public int[] getDataVeclens()
   {
      if (field != null)
      {
         int[] out = new int[field.getNData()];
         for (int i = 0; i < out.length; i++)
         {
            out[i] = field.getData(i).getVeclen();
         }
         return out;
      } else
      {
         return null;
      }
   }

   @Override
   public int[] getDataTypes()
   {
      if (field != null)
      {
         int[] out = new int[field.getNData()];
         for (int i = 0; i < out.length; i++)
         {
            out[i] = field.getData(i).getType();
         }
         return out;
      } else
      {
         return null;
      }
   }

   @Override
   public String[] getDataNames()
   {
      if (field != null)
      {
         String[] out = new String[field.getNData()];
         for (int i = 0; i < out.length; i++)
         {
            out[i] = field.getData(i).getName();
         }
         return out;
      } else
      {
         return null;
      }
   }

   @Override
   public boolean hasScalarComponent()
   {
      return hasVectorComponent(1);
   }

   @Override
   public boolean hasVectorComponent(int veclen)
   {
      int[] v = this.getDataVeclens();
      if (v == null)
      {
         return false;
      }
      for (int i = 0; i < v.length; i++)
      {
         if (v[i] == veclen)
         {
            return true;
         }
      }
      return false;
   }

   @Override
   public boolean isIrregular()
   {
      return field != null && field instanceof IrregularField;
   }

   @Override
   public boolean isTime()
   {
      
      return field != null && field.isTimeDependant();
   }

   @Override
   public boolean isCellSets()
   {
      if (field == null || !this.isIrregular())
      {
         return false;
      }
      return (((IrregularField) field).getNCellSets() > 0);
   }

   @Override
   public int getNCellSets()
   {
      if (field == null || !this.isIrregular())
      {
         return -1;
      }
      return ((IrregularField) field).getNCellSets();
   }

   @Override
   public int[] getNCellData()
   {
      if (field == null || !this.isIrregular())
      {
         return null;
      }

      Vector<CellSet> css = ((IrregularField) field).getCellSets();
      if (css == null || css.size() == 0)
         return null;

      int count = 0;
      for (int i = 0; i < css.size(); i++)
      {
         if (css.get(i).getNData() != 0)
         {
            count++;
         }
      }
      int[] out = new int[count];
      for (int i = 0, k = 0; i < css.size(); i++)
      {
         if (css.get(i).getNData() != 0)
         {
            out[k++] = css.get(i).getNData();
         }
      }
      return out;
   }

   @Override
   public int[][] getCellDataVeclens()
   {
      if (field == null || !this.isIrregular())
      {
         return null;
      }

      Vector<CellSet> css = ((IrregularField) field).getCellSets();
      if (css == null || css.size() == 0)
         return null;

      int count = 0;
      for (int i = 0; i < css.size(); i++)
      {
         if (css.get(i).getNData() != 0)
         {
            count++;
         }
      }
      int[][] out = new int[count][];
      for (int i = 0, k = 0; i < css.size(); i++)
      {
         if (css.get(i).getNData() != 0)
         {
            out[k] = new int[css.get(i).getNData()];
            for (int j = 0; j < css.get(i).getNData(); j++)
            {
               out[k][j] = css.get(i).getData(j).getVeclen();
            }
            k++;
         }
      }
      return out;
   }

   @Override
   public int[][] getCellDataTypes()
   {
      if (field == null || !this.isIrregular())
      {
         return null;
      }

      Vector<CellSet> css = ((IrregularField) field).getCellSets();
      if (css == null || css.size() == 0)
         return null;

      int count = 0;
      for (int i = 0; i < css.size(); i++)
      {
         if (css.get(i).getNData() != 0)
         {
            count++;
         }
      }
      int[][] out = new int[count][];
      for (int i = 0, k = 0; i < css.size(); i++)
      {
         if (css.get(i).getNData() != 0)
         {
            out[k] = new int[css.get(i).getNData()];
            for (int j = 0; j < css.get(i).getNData(); j++)
            {
               out[k][j] = css.get(i).getData(j).getType();
            }
            k++;
         }
      }
      return out;
   }

   @Override
   public String[][] getCellDataNames()
   {
      if (field == null || !this.isIrregular())
      {
         return null;
      }

      Vector<CellSet> css = ((IrregularField) field).getCellSets();
      if (css == null || css.size() == 0)
         return null;

      int count = 0;
      for (int i = 0; i < css.size(); i++)
      {
         if (css.get(i).getNData() != 0)
         {
            count++;
         }
      }
      String[][] out = new String[count][];
      for (int i = 0, k = 0; i < css.size(); i++)
      {
         if (css.get(i).getNData() != 0)
         {
            out[k] = new String[css.get(i).getNData()];
            for (int j = 0; j < css.get(i).getNData(); j++)
            {
               out[k][j] = css.get(i).getData(j).getName();
            }
            k++;
         }
      }
      return out;
   }

   @Override
   public boolean hasCellScalarComponent()
   {
      return hasCellVectorComponent(1);
   }

   @Override
   public boolean hasCellVectorComponent(int veclen)
   {
      int[][] v = this.getCellDataVeclens();
      if (v == null)
      {
         return false;
      }
      for (int i = 0; i < v.length; i++)
      {
         for (int j = 0; j < v[i].length; j++)
         {
            if (v[i][j] == veclen)
            {
               return true;
            }
         }
      }
      return false;
   }

   @Override
   public String[] getCellSetNames()
   {
      if (field == null || !isIrregular() || ((IrregularField) field).getNCellSets() == 0)
         return null;

      String[] out = new String[((IrregularField) field).getNCellSets()];
      for (int i = 0; i < out.length; i++)
      {
         out[i] = ((IrregularField) field).getCellSet(i).getName();
      }
      return out;
   }

   private boolean hasCellsType(int type)
   {
      if (field == null || !this.isIrregular())
         return false;

      if (type < 0 || type >= Cell.TYPES)
         return false;

      Vector<CellSet> css = ((IrregularField) field).getCellSets();
      if (css == null || css.isEmpty())
         return false;

      for (int i = 0; i < css.size(); i++)
      {
         if (css.get(i).getCellArray(type) != null)
            return true;

         CellArray[] bndrs = css.get(i).getBoundaryCellArrays();
         if (bndrs != null)
            for (int j = 0; j < bndrs.length; j++)
            {
               if (bndrs[j] != null && bndrs[i].getType() == type)
                  return true;
            }
      }
      return false;
   }

   @Override
   public boolean hasCellsPoint()
   {
      return hasCellsType(Cell.POINT);
   }

   @Override
   public boolean hasCellsSegment()
   {
      return hasCellsType(Cell.SEGMENT);
   }

   @Override
   public boolean hasCellsTriangle()
   {
      return hasCellsType(Cell.TRIANGLE);
   }

   @Override
   public boolean hasCellsQuad()
   {
      return hasCellsType(Cell.QUAD);
   }

   @Override
   public boolean hasCellsTetra()
   {
      return hasCellsType(Cell.TETRA);
   }

   @Override
   public boolean hasCellsPyramid()
   {
      return hasCellsType(Cell.PYRAMID);
   }

   @Override
   public boolean hasCellsPrism()
   {
      return hasCellsType(Cell.PRISM);
   }

   @Override
   public boolean hasCellsHexahedron()
   {
      return hasCellsType(Cell.HEXAHEDRON);
   }

   @Override
   public boolean hasCells2D()
   {
      if (field == null || !this.isIrregular())
         return false;

      return (hasCellsTriangle() || hasCellsQuad());
   }

   @Override
   public boolean hasCells3D()
   {
      if (field == null || !this.isIrregular())
         return false;
      return (hasCellsTetra() || hasCellsPyramid() || hasCellsPrism() || hasCellsHexahedron());
   }
   
   @Override
   public void createStats()
   {
   
   }
}
