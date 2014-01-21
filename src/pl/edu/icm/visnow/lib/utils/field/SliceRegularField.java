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

import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;

/**
 *
 * @author  Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class SliceRegularField
{

   private static final String[] coordNames =
   {
      "x", "y", "z"
   };

   /** Creates a new instance of CropRegularField */
   public SliceRegularField()
   {
   }

   public static DataArray sliceDataArray(RegularField inField, DataArray dataArr, int axis, int slice)
   {
      return dataArr.get2DSlice(inField.getDims(), axis, slice);
//      int[] dims = inField.getDims();
//      int m = dims[0] * dims[1];
//      int l = dims[0];
//      int vlen = dataArr.getVeclen();
//      switch (dataArr.getType())
//      {
//      case DataArray.FIELD_DATA_BYTE:
//         ByteDataArray bDataArr = (ByteDataArray) dataArr;
//         byte[] outbData = null;
//         switch (axis)
//         {
//         case 0:
//            outbData = bDataArr.get2DByteSlice(slice, dims[1], l, dims[2], m);
//            break;
//         case 1:
//            outbData = bDataArr.get2DByteSlice(slice * l, dims[0], 1, dims[2], m);
//            break;
//         case 2:
//            outbData = bDataArr.get2DByteSlice(slice * m, dims[0], 1, dims[1], l);
//            break;
//         }
//         return DataArray.create(outbData, vlen, dataArr.getName() + " " + coordNames[axis] + "=" + slice);
//      case DataArray.FIELD_DATA_SHORT:
//         ShortDataArray sDataArr = (ShortDataArray) dataArr;
//         short[] outsData = null;
//         switch (axis)
//         {
//         case 0:
//            outsData = sDataArr.get2DShortSlice(slice, dims[1], l, dims[2], m);
//            break;
//         case 1:
//            outsData = sDataArr.get2DShortSlice(slice * l, dims[0], 1, dims[2], m);
//            break;
//         case 2:
//            outsData = sDataArr.get2DShortSlice(slice * m, dims[0], 1, dims[1], l);
//            break;
//         }
//         return DataArray.create(outsData, vlen, dataArr.getName() + " " + coordNames[axis] + "=" + slice);
//      case DataArray.FIELD_DATA_INT:
//         IntDataArray iDataArr = (IntDataArray) dataArr;
//         int[] outiData = null;
//         switch (axis)
//         {
//         case 0:
//            outiData = iDataArr.get2DIntSlice(slice, dims[1], l, dims[2], m);
//            break;
//         case 1:
//            outiData = iDataArr.get2DIntSlice(slice * l, dims[0], 1, dims[2], m);
//            break;
//         case 2:
//            outiData = iDataArr.get2DIntSlice(slice * m, dims[0], 1, dims[1], l);
//            break;
//         }
//         return DataArray.create(outiData, vlen, dataArr.getName() + " " + coordNames[axis] + "=" + slice);
//      case DataArray.FIELD_DATA_FLOAT:
//         FloatDataArray fDataArr = (FloatDataArray) dataArr;
//         float[] outfData = null;
//         switch (axis)
//         {
//         case 0:
//            outfData = fDataArr.get2DFloatSlice(slice, dims[1], l, dims[2], m);
//            break;
//         case 1:
//            outfData = fDataArr.get2DFloatSlice(slice * l, dims[0], 1, dims[2], m);
//            break;
//         case 2:
//            outfData = fDataArr.get2DFloatSlice(slice * m, dims[0], 1, dims[1], l);
//            break;
//         }
//         return DataArray.create(outfData, vlen, dataArr.getName() + " " + coordNames[axis] + "=" + slice);
//      case DataArray.FIELD_DATA_DOUBLE:
//         DoubleDataArray dDataArr = (DoubleDataArray) dataArr;
//         double[] outdData = null;
//         switch (axis)
//         {
//         case 0:
//            outdData = dDataArr.get2DDoubleSlice(slice, dims[1], l, dims[2], m);
//            break;
//         case 1:
//            outdData = dDataArr.get2DDoubleSlice(slice * l, dims[0], 1, dims[2], m);
//            break;
//         case 2:
//            outdData = dDataArr.get2DDoubleSlice(slice * m, dims[0], 1, dims[1], l);
//            break;
//         }
//         return DataArray.create(outdData, vlen, dataArr.getName() + " " + coordNames[axis] + "=" + slice);
//      case DataArray.FIELD_DATA_LOGIC:
//         LogicDataArray lgDataArr = (LogicDataArray) dataArr;
//         outbData = null;
//         switch (axis)
//         {
//         case 0:
//            outbData = lgDataArr.get2DByteSlice(slice, dims[1], l, dims[2], m);
//            break;
//         case 1:
//            outbData = lgDataArr.get2DByteSlice(slice * l, dims[0], 1, dims[2], m);
//            break;
//         case 2:
//            outbData = lgDataArr.get2DByteSlice(slice * m, dims[0], 1, dims[1], l);
//            break;
//         }
//         return DataArray.create(outbData, vlen, dataArr.getName() + " " + coordNames[axis] + "=" + slice);
//      }
//      return null;
   }

   public static void sliceCoordsUpdate(RegularField inField, int axis, int slice, RegularField outField)
   {
      if (inField == null || inField.getDims() == null || inField.getDims().length != 3
              || axis < 0 || axis >= 3
              || slice < 0 || slice >= inField.getDims()[axis])
         return;
      int[] dims = inField.getDims();
      int[] outDims = outField.getDims();
      if (outDims == null || outDims.length != 2 || outDims[0] * outDims[1] * dims[axis] != inField.getNNodes())
         return;
      if (inField.getCoords() == null)
      {
         float[][] inAffine = inField.getAffine();
         float[][] outAffine = new float[4][3];
         switch (axis)
         {
         case 0:
            for (int i = 0; i < 3; i++)
            {
               outAffine[3][i] = inAffine[3][i] + slice * inAffine[0][i];
               outAffine[0][i] = inAffine[1][i];
               outAffine[1][i] = inAffine[2][i];
               outAffine[2][i] = 0;
            }
            break;
         case 1:
            for (int i = 0; i < 3; i++)
            {
               outAffine[3][i] = inAffine[3][i] + slice * inAffine[1][i];
               outAffine[0][i] = inAffine[0][i];
               outAffine[1][i] = inAffine[2][i];
               outAffine[2][i] = 0;
            }
            break;
         case 2:
            for (int i = 0; i < 3; i++)
            {
               outAffine[3][i] = inAffine[3][i] + slice * inAffine[2][i];
               outAffine[0][i] = inAffine[0][i];
               outAffine[1][i] = inAffine[1][i];
               outAffine[2][i] = 0;
            }
            break;
         }
         outField.setAffine(outAffine);
      } 
      else
      {
         outField.setCoords(inField.getAllCoords().get2DTimeDataSlice(inField.getDims(), axis, slice, inField.getNSpace()));
//         float[] inCoords = inField.getCoords();
//         float[] outCoords = new float[3 * outDims[0] * outDims[1]];
//         switch (axis)
//         {
//         case 0:
//            for (int i = 0, k = slice, l = 0; i < outDims[0] * outDims[1]; i++, k += dims[0])
//               for (int j = 0; j < 3; j++, l++)
//                  outCoords[l] = inCoords[3 * k + j];
//            break;
//         case 1:
//            for (int i = 0, k = 0; i < outDims[1]; i++)
//               for (int j = 0, l = 3 * ((i * dims[1] + slice) * dims[0]); j < 3 * outDims[0]; j++, k++, l++)
//                  outCoords[k] = inCoords[l];
//            break;
//         case 2:
//            for (int i = 0, j = 3 * slice * dims[0] * dims[1]; i < outCoords.length; i++, j++)
//               outCoords[i] = inCoords[j];
//            break;
//         }
//         outField.setNSpace(3);
//         outField.setCoords(outCoords);
      }
   }
   
   private static boolean[] get2DSlice(int start, int n0, int step0, int n1, int step1, boolean[] data)
   {
      boolean[] out = new boolean[n0*n1];
      for (int i1 = 0, k=0, l1=start ;i1<n1; i1++,l1+=step1)
         for (int i0=0, l0=l1; i0<n0; i0++, l0+=step0, k++)
               out[k]=data[l0];
      return out;
   }
   
   /**
    * @return false if slice is empty (empty outField should be set)
    */
   public static boolean sliceUpdate(RegularField inField, int axis, int slice, RegularField outField)
   {
      if (inField == null || inField.getDims() == null || inField.getDims().length != 3
              || axis < 0 || axis >= 3
              || slice < 0 || slice >= inField.getDims()[axis] || outField == null) {
         return false;
      }
      int[] dims = inField.getDims();
      int[] outDims = outField.getDims();
      if (outDims == null || outDims.length != 2 || outDims[0] * outDims[1] * dims[axis] != inField.getNNodes())
         return false;
      boolean[] validOut = null;
      if (inField.isMask())
      {
         int m = dims[0] * dims[1];
         int l = dims[0];
         switch (axis)
         {
         case 0:
            validOut = get2DSlice(slice, dims[1], l, dims[2], m, inField.getMask());
            break;
         case 1:
            validOut = get2DSlice(slice * l, dims[0], 1, dims[2], m, inField.getMask());
            break;
         case 2:
            validOut = get2DSlice(slice * m, dims[0], 1, dims[1], l, inField.getMask());
            break;
         }
      }
      outField.setMask(validOut);
      sliceCoordsUpdate(inField, axis, slice, outField);
      for (int n = 0; n < inField.getNData(); n++)
      {
         DataArray inDataArr = inField.getData(n);
         DataArray outDataArr = sliceDataArray(inField, inDataArr, axis, slice);
         outDataArr.setMinv(inDataArr.getMinv());
         outDataArr.setMaxv(inDataArr.getMaxv());
         outDataArr.setPhysMin(inDataArr.getPhysMin());
         outDataArr.setPhysMax(inDataArr.getPhysMax());
         outDataArr.setCurrentTime(inField.getCurrentTime());
         outField.setData(n, outDataArr);
      }

      return true;      
   }

   public static RegularField sliceField(RegularField inField, int axis, int slice)
   {
      if (inField == null || inField.getDims() == null || inField.getDims().length != 3
              || axis < 0 || axis >= 3
              || slice < 0 || slice >= inField.getDims()[axis])
         return null;
      int[] dims = inField.getDims();
      int[] outDims = new int[2];
      if (axis == 0)
      {
         outDims[0] = dims[1];
         outDims[1] = dims[2];
      } else if (axis == 1)
      {
         outDims[0] = dims[0];
         outDims[1] = dims[2];
      } else
      {
         outDims[0] = dims[0];
         outDims[1] = dims[1];
      }
      RegularField outField = new RegularField(outDims);
      outField.setNSpace(3);
      sliceUpdate(inField, axis, slice, outField);
      outField.forceCurrentTime(inField.getCurrentTime());
      return outField;
   }
}
