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

import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.datasets.RegularField;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class FieldSmoothDown
{  
   public static float[] smoothDownArray(float[] in, int[] inDims, int[] down, float sigma, int nThreads)
   {
      int dim = inDims.length;
      float[] cIn = null, cOut = null;
      cIn = in;
      int[] dims    = new int[dim];
      int[] outDims = new int[dim];
      System.arraycopy(inDims, 0, dims,    0, dim);
      System.arraycopy(inDims, 0, outDims, 0, dim);
      int len = 1;
      for (int i = 0; i < dim; i++) {
           len *= dims[i];           
      }
      int vlen = in.length/len;
      
      for (int direction = dim - 1; direction >= 0; direction--)
      {
         int d = down[direction];
         outDims[direction] = (dims[direction] + d - 1) / d;
         int nOut = 1;
         for (int i = 0; i < outDims.length; i++) 
            nOut *= outDims[i];
         cOut = new float[nOut*vlen];
         if (nThreads < 2)
         {
            DirectionalFieldSmoothing
                    smoother = new DirectionalFieldSmoothing(direction, dims, down[direction],
                                                             cIn, cOut, sigma, 1, 0);
            smoother.run();
         }
         else
         {
         Thread[] workThreads = new Thread[nThreads];
         DirectionalFieldSmoothing[] smoothers = new DirectionalFieldSmoothing[nThreads];
         for (int i = 0; i < workThreads.length; i++)
         {
            smoothers[i] = new DirectionalFieldSmoothing(direction, dims, down[direction],
                                                         cIn, cOut, sigma,  nThreads, i);
            workThreads[i] = new Thread(smoothers[i]);
            workThreads[i].start();
         }
         for (int i = 0; i < workThreads.length; i++)
            try
            {
               workThreads[i].join();
            } catch (Exception e)
            {
            }
         }
         dims[direction] = outDims[direction];
         cIn = cOut;
      }
      return cOut;
   }
   
   public static RegularField smoothDownToFloat(RegularField inField, int[] down, float sigma, int nThreads)
   {
      if (inField == null || inField.getDims() == null || inField.getDims().length <2)
         return null;
      int[] inDims = inField.getDims();
      int dim = inDims.length;
      int[] outDims = new int[dim];
      for (int i = 0; i < dim; i++)
         outDims[i] = (inDims[i] + down[i] - 1)/ down[i];
      RegularField outField = new RegularField(outDims);
      float[][] inAffine = inField.getAffine();
      float[][] outAffine = new float[4][3];
      for (int i = 0; i < 3; i++)
      {
         outAffine[3][i] = inAffine[3][i];
         for (int j = 0; j < 3; j++)
            outAffine[j][i] = inAffine[j][i] * down[i];
      }
      outField.setNSpace(inField.getNSpace());
      outField.setAffine(outAffine);
      if (inField.getCoords() != null)
         outField.setCoords(smoothDownArray(inField.getCoords(), inDims, down, sigma, nThreads));
      for (int component = 0; component < inField.getNData(); component++)
      {
         if (inField.getData(component).isSimpleNumeric())
            outField.addData(DataArray.create(smoothDownArray(inField.getData(component).getFData(), inDims, down, sigma, nThreads), 
                             inField.getData(component).getVeclen(), inField.getData(component).getName()));
      }
      return outField;
   }

   public static RegularField smoothDownToFloat(RegularField inField, int down, float sigma, int nThreads)
   {
      return smoothDownToFloat(inField, new int[] {down,down,down}, sigma, nThreads);
   }
           
   public static RegularField smoothDownToFloat(RegularField inField, int down, int nThreads)
   {
      return smoothDownToFloat(inField, new int[] {down,down,down}, (float)down, nThreads);
   }
           
   public static RegularField smoothDown(RegularField inField, int[] down, float sigma, int nThreads)
   {
      if (inField == null || inField.getDims() == null || inField.getDims().length < 2)
         return null;
      int[] inDims = inField.getDims();
      int dim = inDims.length;
      int[] outDims = new int[dim];
      for (int i = 0; i < dim; i++)
         outDims[i] = (inDims[i] + down[i] - 1)/ down[i];
      RegularField outField = new RegularField(outDims);
      float[][] inAffine = inField.getAffine();
      float[][] outAffine = new float[4][3];
      for (int i = 0; i < 3; i++)
      {
         outAffine[3][i] = inAffine[3][i];
         for (int j = 0; j < 3; j++)
            outAffine[j][i] = inAffine[j][i] * down[i];
      }
      outField.setNSpace(inField.getNSpace());
      outField.setAffine(outAffine);
      if (inField.getCoords() != null)
         outField.setCoords(smoothDownArray(inField.getCoords(), inDims, down, sigma, nThreads));
      for (int component = 0; component < inField.getNData(); component++)
      {
         float[] outData = smoothDownArray(inField.getData(component).getFData(), inDims, down, sigma, nThreads);
         if (inField.getData(component).isSimpleNumeric())
            switch (inField.getData(component).getType())
            {
            case DataArray.FIELD_DATA_BYTE:
               byte[] outBData = new byte[outData.length];
               for (int i = 0; i < outData.length; i++)
                  outBData[i] = (byte) (0xff & (int) outData[i]);
               outField.addData(DataArray.create(outBData, inField.getData(component).getVeclen(), "smoothed " + inField.getData(component).getName()));
               break;
            case DataArray.FIELD_DATA_SHORT:
               short[] outSData = new short[outData.length];
               for (int i = 0; i < outData.length; i++)
                  outSData[i] = (short) outData[i];
               outField.addData(DataArray.create(outSData, inField.getData(component).getVeclen(), "smoothed " + inField.getData(component).getName()));
               break;
            case DataArray.FIELD_DATA_INT:
               int[] outIData = new int[outData.length];
               for (int i = 0; i < outData.length; i++)
                  outIData[i] = (int) outData[i];
               outField.addData(DataArray.create(outIData, inField.getData(component).getVeclen(), "smoothed " + inField.getData(component).getName()));
               break;
             case DataArray.FIELD_DATA_FLOAT:
               outField.addData(DataArray.create(outData, inField.getData(component).getVeclen(), "smoothed " + inField.getData(component).getName()));
               break;
            case DataArray.FIELD_DATA_DOUBLE:
               outField.addData(DataArray.create(outData, inField.getData(component).getVeclen(), "smoothed " + inField.getData(component).getName()));
               break;
            }
      }
      return outField;
   }

   public static RegularField smoothDown(RegularField inField, int down, float sigma, int nThreads)
   {
      return smoothDown(inField, new int[] {down,down,down}, sigma, nThreads);
   }
           
   public static RegularField smoothDown(RegularField inField, int down, int nThreads)
   {
      return smoothDown(inField, new int[] {down,down,down}, (float)down, nThreads);
   }

   private FieldSmoothDown()
   {
   }
           
}
