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


package pl.edu.icm.visnow.lib.utils.field;

import pl.edu.icm.visnow.datasets.CellArray;
import pl.edu.icm.visnow.datasets.CellSet;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.datasets.IrregularField;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class MergeIrregularField
{

   public static IrregularField merge(IrregularField inField0, IrregularField inField1, int currentOp, boolean separate)
   {
      if ((inField0 == null || inField0.getNNodes() == 0) && (inField1 != null && inField1.getNNodes() != 0))
         return inField1.clone();
      if ((inField0 != null && inField0.getNNodes() != 0) && (inField1 == null || inField1.getNNodes() == 0))
         return inField0.clone();
      if ((inField0 == null || inField0.getNNodes() == 0) && (inField1 == null || inField1.getNNodes() == 0))
         return null;
      int nNodes1 = inField1.getNNodes();
      int nNodes0 = inField0.getNNodes();
      int nNodes = nNodes1 + nNodes0;
      float[] coords = new float[3 * nNodes];
      System.arraycopy(inField0.getCoords(), 0, coords, 0, 3 * nNodes0);
      System.arraycopy(inField1.getCoords(), 0, coords, 3 * nNodes0, 3 * nNodes1);
      IrregularField outField = new IrregularField(nNodes);
      outField.setNSpace(3);
      outField.setCoords(coords);
      for (int i = 0; i < inField0.getNData(); i++)
      {
         DataArray inDA = inField1.getData(i);
         if (!inDA.isSimpleNumeric())
            continue;
         DataArray outDA = inField0.getData(i);
         if (inDA.getType() != outDA.getType())
            continue;
         int vlen = inDA.getVeclen();
         switch (inDA.getType())
         {
         case DataArray.FIELD_DATA_BYTE:
            byte[] outB = new byte[vlen * nNodes];
            System.arraycopy(outDA.getBData(), 0, outB, 0, vlen * nNodes0);
            System.arraycopy(inDA.getBData(), 0, outB, vlen * nNodes0, vlen * nNodes1);
            outField.addData(DataArray.create(outB, vlen, outDA.getName(), outDA.getUnit(), outDA.getUserData()));
            break;
         case DataArray.FIELD_DATA_SHORT:
            short[] outS = new short[vlen * nNodes];
            System.arraycopy(outDA.getSData(), 0, outS, 0, vlen * nNodes0);
            System.arraycopy(inDA.getSData(), 0, outS, vlen * nNodes0, vlen * nNodes1);
            outField.addData(DataArray.create(outS, vlen, outDA.getName(), outDA.getUnit(), outDA.getUserData()));
            break;
         case DataArray.FIELD_DATA_INT:
            int[] outI = new int[vlen * nNodes];
            System.arraycopy(outDA.getIData(), 0, outI, 0, vlen * nNodes0);
            System.arraycopy(inDA.getIData(), 0, outI, vlen * nNodes0, vlen * nNodes1);
            outField.addData(DataArray.create(outI, vlen, outDA.getName(), outDA.getUnit(), outDA.getUserData()));
            break;
         case DataArray.FIELD_DATA_FLOAT:
            float[] outF = new float[vlen * nNodes];
            System.arraycopy(outDA.getFData(), 0, outF, 0, vlen * nNodes0);
            System.arraycopy(inDA.getFData(), 0, outF, vlen * nNodes0, vlen * nNodes1);
            outField.addData(DataArray.create(outF, vlen, outDA.getName(), outDA.getUnit(), outDA.getUserData()));
            break;
         case DataArray.FIELD_DATA_DOUBLE:
            double[] outD = new double[vlen * nNodes];
            System.arraycopy(outDA.getDData(), 0, outD, 0, vlen * nNodes0);
            System.arraycopy(inDA.getDData(), 0, outD, vlen * nNodes0, vlen * nNodes1);
            outField.addData(DataArray.create(outD, vlen, outDA.getName(), outDA.getUnit(), outDA.getUserData()));
            break;
         }
      }
      boolean sep = separate;
      if (inField0.getNCellSets() != inField1.getNCellSets())
         sep = true;
      if (sep)
      {
         for (CellSet cs : inField0.getCellSets())
            outField.addCellSet(cs);
         for (CellSet cs : inField1.getCellSets())
         {
            CellSet ncs = new CellSet();
            ncs.setName(cs.getName() + currentOp);
            ncs.setSelected(true);
            for (int i = 0; i < cs.getNData(); i++)
               ncs.addData(cs.getData(i));
            for (int i = 0; i < cs.getBoundaryCellArrays().length; i++)
            {
               if (cs.getBoundaryCellArray(i) == null)
                  continue;
               int[] inNds = cs.getBoundaryCellArray(i).getNodes();
               int[] outNds = new int[inNds.length];
               for (int j = 0; j < outNds.length; j++)
                  outNds[j] = inNds[j] + nNodes0;
               boolean[] inOr = cs.getBoundaryCellArray(i).getOrientations();
               boolean[] outOr = new boolean[inOr.length];
               System.arraycopy(inOr, 0, outOr, 0, inOr.length);
               int[] inIds = cs.getBoundaryCellArray(i).getDataIndices();
               int[] outIds = null;
               if( inIds != null ) {
                outIds = new int[inIds.length];
                System.arraycopy(inIds, 0, outIds, 0, inIds.length);
               }               
               ncs.setBoundaryCellArray(new CellArray(cs.getBoundaryCellArray(i).getType(), outNds, outOr, outIds));
            }
            for (int i = 0; i < cs.getCellArrays().length; i++)
            {
               if (cs.getCellArray(i) == null)
                  continue;
               int[] inNds = cs.getCellArray(i).getNodes();
               int[] outNds = new int[inNds.length];
               for (int j = 0; j < outNds.length; j++)
                  outNds[j] = inNds[j] + nNodes0;
               boolean[] inOr = cs.getCellArray(i).getOrientations();
               boolean[] outOr = new boolean[inOr.length];
               System.arraycopy(inOr, 0, outOr, 0, inOr.length);
               int[] inIds = cs.getCellArray(i).getDataIndices();
               int[] outIds = null;
               if( inIds != null ) {
                outIds = new int[inIds.length];
                System.arraycopy(inIds, 0, outIds, 0, inIds.length);
               }               
               ncs.setCellArray(new CellArray(cs.getCellArray(i).getType(), outNds, outOr, outIds));
            }
            outField.addCellSet(ncs);
         }
      }
      else
      {
         for (int i = 0; i < inField0.getNCellSets(); i++)
            outField.addCellSet(inField0.getCellSet(i).add(inField1.getCellSet(i), inField0.getNNodes()));
      }
      return outField;
   }

   private MergeIrregularField()
   {
   }
}
