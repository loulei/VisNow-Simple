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

package pl.edu.icm.visnow.lib.utils.field;

import pl.edu.icm.visnow.datasets.CellArray;
import pl.edu.icm.visnow.datasets.CellSet;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.datasets.IrregularField;

/**
 *
 * @author Krzysztof S. Nowinski
 * University of Warsaw, ICM
 */
public class ExtractCellSets 
{
   public static IrregularField extractCellSets(IrregularField in)
   {
      boolean[] vpts = new boolean[in.getNNodes()];
      int[] vptinds  = new int[in.getNNodes()];
      for (int i = 0; i < vptinds.length; i++)
      {
         vptinds[i] = -1;
         vpts[i] = false;
      }
      for (CellSet s : in.getCellSets())
         if (s.isSelected())
         {
            for (CellArray a : s.getCellArrays())
               if (a != null)
               {
                  int[] nodes = a.getNodes();
                  for (int i = 0; i < nodes.length; i++)
                     vpts[nodes[i]] = true;
               }
            for (CellArray a : s.getBoundaryCellArrays())
               if (a != null)
               {
                  int[] nodes = a.getNodes();
                  for (int i = 0; i < nodes.length; i++)
                     vpts[nodes[i]] = true;
               }
         }
      int  nOut = 0;
      for (int i = 0; i < vptinds.length; i++)
         if (vpts[i])
         {
            vptinds[i] = nOut;
            nOut += 1;
         }
      if (nOut == 0)
         return null;
      IrregularField out = new IrregularField(nOut);
      out.setNSpace(in.getNSpace());
      out.setCoords(floatArrayCompact(in.getCoords(), in.getNSpace(), nOut, vpts) );
      for (DataArray da: in.getData())
         if (da.isSimpleNumeric())
            switch (da.getType())
            {
            case DataArray.FIELD_DATA_BYTE:
               out.addData(DataArray.create(byteArrayCompact(da.getBData(), da.getVeclen(), nOut, vpts),
                                            da.getVeclen(), da.getName(), da.getUnit(), da.getUserData()));
               break;
            case DataArray.FIELD_DATA_SHORT:
               out.addData(DataArray.create(shortArrayCompact(da.getSData(), da.getVeclen(), nOut, vpts),
                                            da.getVeclen(), da.getName(), da.getUnit(), da.getUserData()));
               break;
            case DataArray.FIELD_DATA_INT:
               out.addData(DataArray.create(intArrayCompact(da.getIData(), da.getVeclen(), nOut, vpts),
                                            da.getVeclen(), da.getName(), da.getUnit(), da.getUserData()));
               break;
            case DataArray.FIELD_DATA_FLOAT:
               out.addData(DataArray.create(floatArrayCompact(da.getFData(), da.getVeclen(), nOut, vpts),
                                            da.getVeclen(), da.getName(), da.getUnit(), da.getUserData()));
               break;
            case DataArray.FIELD_DATA_DOUBLE:
               out.addData(DataArray.create(doubleArrayCompact(da.getDData(), da.getVeclen(), nOut, vpts),
                                            da.getVeclen(), da.getName(), da.getUnit(), da.getUserData()));
               break;
            }
      for (CellSet s : in.getCellSets())
         if (s.isSelected())
         {
            CellSet outS = new CellSet(s.getName());
            for (CellArray a : s.getCellArrays())
               if (a != null)
               {
                  int[] nodes = a.getNodes();
                  int[] outNodes = new int[nodes.length];
                  for (int i = 0; i < nodes.length; i++)
                     outNodes[i] = vptinds[nodes[i]];
                  CellArray outA = new CellArray(outS, a.getType(), outNodes, a.getOrientations(), a.getDataIndices());
                  outS.setCellArray(outA);
               }
            for (CellArray a : s.getBoundaryCellArrays())
               if (a != null)
               {
                  int[] nodes = a.getNodes();
                  int[] outNodes = new int[nodes.length];
                  for (int i = 0; i < nodes.length; i++)
                     outNodes[i] = vptinds[nodes[i]];
                  CellArray outA = new CellArray(outS, a.getType(), outNodes, a.getOrientations(), a.getDataIndices());
                  outS.setBoundaryCellArray(outA);
               }
            out.addCellSet(outS);
         }
      return out;
   }
   
   public static byte[] byteArrayCompact(byte[] data, int vlen, int outLen, boolean[] v)
   {
      int n = data.length/vlen;
      if (n != v.length)
         return null;
      byte[] out = new byte[outLen * vlen];
      for (int i = 0, j = 0; i < n; i++)
         if (v[i])
         {
            System.arraycopy(data, vlen * i, out, j, vlen);
            j += vlen;
         }
      return out;
   }

   public static short[] shortArrayCompact(short[] data, int vlen, int outLen, boolean[] v)
   {
      int n = data.length/vlen;
      if (n != v.length)
         return null;
      short[] out = new short[outLen * vlen];
      for (int i = 0, j = 0; i < n; i++)
         if (v[i])
         {
            System.arraycopy(data, vlen * i, out, j, vlen);
            j += vlen;
         }
      return out;
   }

   public static int[] intArrayCompact(int[] data, int vlen, int outLen, boolean[] v)
   {
      int n = data.length/vlen;
      if (n != v.length)
         return null;
      int[] out = new int[outLen * vlen];
      for (int i = 0, j = 0; i < n; i++)
         if (v[i])
         {
            System.arraycopy(data, vlen * i, out, j, vlen);
            j += vlen;
         }
      return out;
   }

   public static float[] floatArrayCompact(float[] data, int vlen, int outLen, boolean[] v)
   {
      int n = data.length/vlen;
      if (n != v.length)
         return null;
      float[] out = new float[outLen * vlen];
      for (int i = 0, j = 0; i < n; i++)
         if (v[i])
         {
            System.arraycopy(data, vlen * i, out, j, vlen);
            j += vlen;
         }
      return out;
   }

   public static double[] doubleArrayCompact(double[] data, int vlen, int outLen, boolean[] v)
   {
      int n = data.length/vlen;
      if (n != v.length)
         return null;
      double[] out = new double[outLen * vlen];
      for (int i = 0, j = 0; i < n; i++)
         if (v[i])
         {
            System.arraycopy(data, vlen * i, out, j, vlen);
            j += vlen;
         }
      return out;
   }

   private ExtractCellSets()
   {
   }

}
