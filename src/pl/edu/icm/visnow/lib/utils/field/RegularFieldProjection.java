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
import pl.edu.icm.visnow.geometries.parameters.RegularField3dParams;

/**
 *
 * @author  Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class RegularFieldProjection
{

   private static final String[] coordNames =
   {
      "x", "y", "z"
   };

   /** Creates a new instance of CropRegularField */
   public RegularFieldProjection()
   {
   }
   
  class ComputeProjection implements Runnable
   {

      int[] dims;
      int[] outDims;
      int type;
      int axis;
      int nThreads;
      int iThread;
      byte[]    inbData, outbData;
      short[]   insData, outsData;
      int[]     iniData, outiData;
      float[]   infData, outfData;
      double[]  indData, outdData;
      int operation;

      public ComputeProjection(int[] dims, int[] outDims, int axis, int nThreads, int iThread,
                               byte[] inData, byte[] outData, int operation)
      {
         type = DataArray.FIELD_DATA_BYTE;
         this.dims           = dims;
         this.outDims        = outDims;
         this.axis           = axis;
         this.nThreads       = nThreads;
         this.iThread        = iThread;
         this.inbData        = inData;
         this.outbData       = outData;
         this.operation      = operation;
      }

      public ComputeProjection(int[] dims, int[] outDims, int axis, int nThreads, int iThread,
                               short[] inData, short[] outData, int operation)
      {
         type = DataArray.FIELD_DATA_SHORT;
         this.dims           = dims;
         this.outDims        = outDims;
         this.axis           = axis;
         this.nThreads       = nThreads;
         this.iThread        = iThread;
         this.insData        = inData;
         this.outsData       = outData;
         this.operation      = operation;
     }

      public ComputeProjection(int[] dims, int[] outDims, int axis, int nThreads, int iThread,
                               int[] inData, int[] outData, int operation)
      {
         type = DataArray.FIELD_DATA_INT;
         this.dims           = dims;
         this.outDims        = outDims;
         this.axis           = axis;
         this.nThreads       = nThreads;
         this.iThread        = iThread;
         this.iniData        = inData;
         this.outiData       = outData;
         this.operation      = operation;
    }

      public ComputeProjection(int[] dims, int[] outDims, int axis, int nThreads, int iThread,
                               float[] inData, float[] outData, int operation)
      {
         type = DataArray.FIELD_DATA_FLOAT;
         this.dims           = dims;
         this.outDims        = outDims;
         this.axis           = axis;
         this.nThreads       = nThreads;
         this.iThread        = iThread;
         this.infData        = inData;
         this.outfData       = outData;
         this.operation      = operation;
     }

      public ComputeProjection(int[] dims, int[] outDims, int axis, int nThreads, int iThread,
                               double[] inData, double[] outData, int operation)
      {
         type = DataArray.FIELD_DATA_DOUBLE;
         this.dims           = dims;
         this.outDims        = outDims;
         this.axis           = axis;
         this.nThreads       = nThreads;
         this.iThread        = iThread;
         this.indData        = inData;
         this.outdData       = outData;
         this.operation      = operation;
     }

      public void run()
      {
            switch (operation)
            {
            case RegularField3dParams.AVG:
               switch (type)
               {
               case DataArray.FIELD_DATA_BYTE:
                  switch (axis)
                  {
                  case 0:
                     for (int i = iThread; i < dims[1] * dims[2]; i += nThreads)
                     {
                        int f = 0;
                        for (int j = 0, k = i * dims[0]; j < dims[0]; j++, k++)
                           f += inbData[k] & 0xff;
                        outbData[i] = (byte) (f / dims[0] & 0xff);
                     }
                     break;
                  case 1:
                     for (int i = iThread; i < dims[2]; i += nThreads)
                        for (int j = 0, ii = i * dims[0]; j < dims[0]; j++, ii++)
                        {
                           int f = 0;
                           for (int k = 0, kk = i * dims[0] * dims[1] + j; k < dims[1]; k++, kk += dims[0])
                              f += inbData[kk] & 0xff;
                           outbData[ii] = (byte) (f / dims[1] & 0xff);
                        }
                     break;
                  case 2:
                     for (int i = iThread; i < dims[0] * dims[1]; i += nThreads)
                     {
                        int f = 0;
                        for (int j = 0, k = i; j < dims[2]; j++, k += dims[0] * dims[1])
                           f += inbData[k] & 0xff;
                        outbData[i] = (byte) (f / dims[2] & 0xff);
                     }
                     break;
                  }
                  break;
               case DataArray.FIELD_DATA_SHORT:
                  switch (axis)
                  {
                  case 0:
                     for (int i = iThread; i < dims[1] * dims[2]; i += nThreads)
                     {
                        int f = 0;
                        for (int j = 0, k = i * dims[0]; j < dims[0]; j++, k++)
                           f += insData[k];
                        outsData[i] = (short) (f / dims[0]);
                     }
                     break;
                  case 1:
                     for (int i = iThread; i < dims[2]; i += nThreads)
                        for (int j = 0, ii = i * dims[0]; j < dims[0]; j++, ii++)
                        {
                           int f = 0;
                           for (int k = 0, kk = i * dims[0] * dims[1] + j; k < dims[1]; k++, kk += dims[0])
                              f += insData[kk];
                           outsData[ii] = (short) (f / dims[1]);
                        }
                     break;
                  case 2:
                     for (int i = iThread; i < dims[0] * dims[1]; i += nThreads)
                     {
                        int f = 0;
                        for (int j = 0, k = i; j < dims[2]; j++, k += dims[0] * dims[1])
                           f += insData[k];
                        outsData[i] = (short) (f / dims[2]);
                     }
                  }
                  break;
               case DataArray.FIELD_DATA_INT:
                  switch (axis)
                  {
                  case 0:
                     for (int i = iThread; i < dims[1] * dims[2]; i += nThreads)
                     {
                        int f = 0;
                        for (int j = 0, k = i * dims[0]; j < dims[0]; j++, k++)
                           f += iniData[k];
                        outiData[i] = f / dims[0];
                     }
                     break;
                  case 1:
                     for (int i = iThread; i < dims[2]; i += nThreads)
                        for (int j = 0, ii = i * dims[0]; j < dims[0]; j++, ii++)
                        {
                           int f = 0;
                           for (int k = 0, kk = i * dims[0] * dims[1] + j; k < dims[1]; k++, kk += dims[0])
                              f += iniData[kk];
                           outiData[ii] = f / dims[1];
                        }
                     break;
                  case 2:
                     for (int i = iThread; i < dims[0] * dims[1]; i += nThreads)
                     {
                        int f = 0;
                        for (int j = 0, k = i; j < dims[2]; j++, k += dims[0] * dims[1])
                           f += iniData[k];
                        outiData[i] = f / dims[2];
                     }
                     break;
                  }
                  break;
               case DataArray.FIELD_DATA_FLOAT:
                  switch (axis)
                  {
                  case 0:
                     for (int i = iThread; i < dims[1] * dims[2]; i += nThreads)
                     {
                        float f = 0;
                        for (int j = 0, k = i * dims[0]; j < dims[0]; j++, k++)
                           f += infData[k];
                        outfData[i] = f / dims[0];
                     }
                     break;
                  case 1:
                     for (int i = iThread; i < dims[2]; i += nThreads)
                        for (int j = 0, ii = i * dims[0]; j < dims[0]; j++, ii++)
                        {
                           float f = 0;
                           for (int k = 0, kk = i * dims[0] * dims[1] + j; k < dims[1]; k++, kk += dims[0])
                              f += infData[kk];
                           outfData[ii] = f / dims[1];
                        }
                     break;
                  case 2:
                     for (int i = iThread; i < dims[0] * dims[1]; i += nThreads)
                     {
                        float f = 0;
                        for (int j = 0, k = i; j < dims[2]; j++, k += dims[0] * dims[1])
                           f += infData[k];
                        outfData[i] = f / dims[2];
                     }
                     break;
                  }
                  break;
               case DataArray.FIELD_DATA_DOUBLE:
                  switch (axis)
                  {
                  case 0:
                     for (int i = iThread; i < dims[1] * dims[2]; i += nThreads)
                     {
                        double f = 0;
                        for (int j = 0, k = i * dims[0]; j < dims[0]; j++, k++)
                           f += indData[k];
                        outdData[i] = f / dims[0];
                     }
                     break;
                  case 1:
                     for (int i = iThread; i < dims[2]; i += nThreads)
                        for (int j = 0, ii = i * dims[0]; j < dims[0]; j++, ii++)
                        {
                           double f = 0;
                           for (int k = 0, kk = i * dims[0] * dims[1] + j; k < dims[1]; k++, kk += dims[0])
                              f += indData[kk];
                           outdData[ii] = f / dims[1];
                        }
                     break;
                  case 2:
                     for (int i = iThread; i < dims[0] * dims[1]; i += nThreads)
                     {
                        double f = 0;
                        for (int j = 0, k = i; j < dims[2]; j++, k += dims[0] * dims[1])
                           f += indData[k];
                        outdData[i] = f / dims[2];
                     }
                     break;
                  }
                  break;
               }
               break;
            case RegularField3dParams.MAX:
               switch (type)
               {
               case DataArray.FIELD_DATA_BYTE:
                  switch (axis)
                  {
                  case 0:
                     for (int i = iThread; i < dims[1] * dims[2]; i += nThreads)
                     {
                        int f = 0;
                        for (int j = 0, k = i * dims[0]; j < dims[0]; j++, k++)
                           if (f < (inbData[k] & 0xff))
                              f = inbData[k] & 0xff;
                        outbData[i] = (byte) (f & 0xff);
                     }
                     break;
                  case 1:
                     for (int i = iThread; i < dims[2]; i += nThreads)
                        for (int j = 0, ii = i * dims[0]; j < dims[0]; j++, ii++)
                        {
                           int f = 0;
                           for (int k = 0, kk = i * dims[0] * dims[1] + j; k < dims[1]; k++, kk += dims[0])
                              if (f < (inbData[kk] & 0xff))
                                 f = inbData[kk] & 0xff;
                           outbData[ii] = (byte) (f & 0xff);
                        }
                     break;
                  case 2:
                     for (int i = iThread; i < dims[0] * dims[1]; i += nThreads)
                     {
                        int f = 0;
                        for (int j = 0, k = i; j < dims[2]; j++, k += dims[0] * dims[1])
                           if (f < (inbData[k] & 0xff))
                              f = inbData[k] & 0xff;
                        outbData[i] = (byte) (f & 0xff);
                     }
                     break;
                  }
                  break;
               case DataArray.FIELD_DATA_SHORT:
                  switch (axis)
                  {
                  case 0:
                     for (int i = iThread; i < dims[1] * dims[2]; i += nThreads)
                     {
                        short f = -10000;
                        for (int j = 0, k = i * dims[0]; j < dims[0]; j++, k++)
                           if (f < insData[k])
                              f = insData[k];
                        outsData[i] = f;
                     }
                     break;
                  case 1:
                     for (int i = iThread; i < dims[2]; i += nThreads)
                        for (int j = 0, ii = i * dims[0]; j < dims[0]; j++, ii++)
                        {
                           short f = -10000;
                           for (int k = 0, kk = i * dims[0] * dims[1] + j; k < dims[1]; k++, kk += dims[0])
                              if (f < insData[kk])
                                 f = insData[kk];
                           outsData[ii] = f;
                        }
                     break;
                  case 2:
                     for (int i = iThread; i < dims[0] * dims[1]; i += nThreads)
                     {
                        short f = -10000;
                        for (int j = 0, k = i; j < dims[2]; j++, k += dims[0] * dims[1])
                           if (f < insData[k])
                              f = insData[k];
                        outsData[i] = f;
                     }
                     break;
                  }
                  break;
               case DataArray.FIELD_DATA_INT:
                  switch (axis)
                  {
                  case 0:
                     for (int i = iThread; i < dims[1] * dims[2]; i += nThreads)
                     {
                        int f = Integer.MIN_VALUE;
                        for (int j = 0, k = i * dims[0]; j < dims[0]; j++, k++)
                           if (f < iniData[k])
                              f = iniData[k];
                        outiData[i] = f;
                     }
                     break;
                  case 1:
                     for (int i = iThread; i < dims[2]; i += nThreads)
                        for (int j = 0, ii = i * dims[0]; j < dims[0]; j++, ii++)
                        {
                           int f = Integer.MIN_VALUE;
                           for (int k = 0, kk = i * dims[0] * dims[1] + j; k < dims[1]; k++, kk += dims[0])
                              if (f < iniData[kk])
                                 f = iniData[kk];
                           outiData[ii] = f;
                        }
                     break;
                  case 2:
                     for (int i = iThread; i < dims[0] * dims[1]; i += nThreads)
                     {
                        int f = Integer.MIN_VALUE;
                        for (int j = 0, k = i; j < dims[2]; j++, k += dims[0] * dims[1])
                           if (f < iniData[k])
                              f = iniData[k];
                        outiData[i] = f;
                     }
                     break;
                  }
                  break;
               case DataArray.FIELD_DATA_FLOAT:
                  switch (axis)
                  {
                  case 0:
                     for (int i = iThread; i < dims[1] * dims[2]; i += nThreads)
                     {
                        float f = Float.NEGATIVE_INFINITY;
                        for (int j = 0, k = i * dims[0]; j < dims[0]; j++, k++)
                           if (f < infData[k])
                              f = infData[k];
                        outfData[i] = f;
                     }
                     break;
                  case 1:
                     for (int i = iThread; i < dims[2]; i += nThreads)
                        for (int j = 0, ii = i * dims[0]; j < dims[0]; j++, ii++)
                        {
                           float f = Float.NEGATIVE_INFINITY;
                           for (int k = 0, kk = i * dims[0] * dims[1] + j; k < dims[1]; k++, kk += dims[0])
                              if (f < infData[kk])
                                 f = infData[kk];
                           outfData[ii] = f;
                        }
                     break;
                  case 2:
                     for (int i = iThread; i < dims[0] * dims[1]; i += nThreads)
                     {
                        float f = Float.NEGATIVE_INFINITY;
                        for (int j = 0, k = i; j < dims[2]; j++, k += dims[0] * dims[1])
                           if (f < infData[k])
                              f = infData[k];
                        outfData[i] = f;
                     }
                     break;
                  }
                  break;
               case DataArray.FIELD_DATA_DOUBLE:
                  switch (axis)
                  {
                  case 0:
                     for (int i = iThread; i < dims[1] * dims[2]; i += nThreads)
                     {
                        double f = Double.NEGATIVE_INFINITY;
                        for (int j = 0, k = i * dims[0]; j < dims[0]; j++, k++)
                           if (f < indData[k])
                              f = indData[k];
                        outdData[i] = f;
                     }
                     break;
                  case 1:
                     for (int i = iThread; i < dims[2]; i += nThreads)
                        for (int j = 0, ii = i * dims[0]; j < dims[0]; j++, ii++)
                        {
                           double f = 0;
                           for (int k = 0, kk = i * dims[0] * dims[1] + j; k < dims[1]; k++, kk += dims[0])
                              if (f < indData[kk])
                                 f = indData[kk];
                           outdData[ii] = f;
                        }
                     break;
                  case 2:
                     for (int i = iThread; i < dims[0] * dims[1]; i += nThreads)
                     {
                        double f = 0;
                        for (int j = 0, k = i; j < dims[2]; j++, k += dims[0] * dims[1])
                           if (f < indData[k])
                              f = indData[k];
                        outdData[i] = f;
                     }
                     break;
                  }
                  break;
               }
               break;
            case RegularField3dParams.STDDEV:
               switch (type)
               {
               case DataArray.FIELD_DATA_BYTE:
                  switch (axis)
                  {
                  case 0:
                     for (int i = iThread; i < dims[1] * dims[2]; i += nThreads)
                     {
                        int f = 0, s = 0;
                        for (int j = 0, k = i * dims[0]; j < dims[0]; j++, k++)
                        {
                           int v = inbData[k] & 0xff;
                           f += v;
                           s += v * v;
                        }
                        outbData[i] = (byte)  (0xff & (int)(Math.sqrt((float)s/dims[axis] - ((float)f/dims[axis])*((float)f/dims[axis]))));
                     }
                     break;
                  case 1:
                     for (int i = iThread; i < dims[2]; i += nThreads)
                        for (int j = 0, ii = i * dims[0]; j < dims[0]; j++, ii++)
                        {
                           int f = 0, s = 0;
                           for (int k = 0, kk = i * dims[0] * dims[1] + j; k < dims[1]; k++, kk += dims[0])
                           {
                              int v = inbData[kk] & 0xff;
                              f += v;
                              s += v * v;
                           }
                           outbData[ii] = (byte)  (0xff & (int)(Math.sqrt((float)s/dims[axis] - ((float)f/dims[axis])*((float)f/dims[axis]))));
                        }
                     break;
                  case 2:
                     for (int i = iThread; i < dims[0] * dims[1]; i += nThreads)
                     {
                        int f = 0, s = 0;
                        for (int j = 0, k = i; j < dims[2]; j++, k += dims[0] * dims[1])
                        {
                           int v = inbData[k] & 0xff;
                           f += v;
                           s += v * v;
                        }
                        outbData[i] = (byte) (0xff & (int)(Math.sqrt((float)s/dims[axis] - ((float)f/dims[axis])*((float)f/dims[axis]))));
                     }
                     break;
                  }
                  break;
               case DataArray.FIELD_DATA_SHORT:
                  switch (axis)
                  {
                  case 0:
                     for (int i = iThread; i < dims[1] * dims[2]; i += nThreads)
                     {
                        int f = 0, s = 0;
                        for (int j = 0, k = i * dims[0]; j < dims[0]; j++, k++)
                        {
                           int v = insData[k];
                           f += v;
                           s += v * v;
                        }
                        outsData[i] = (short)(Math.sqrt((float)s/dims[axis] - ((float)f/dims[axis])*((float)f/dims[axis])));
                     }
                     break;
                  case 1:
                     for (int i = iThread; i < dims[2]; i += nThreads)
                        for (int j = 0, ii = i * dims[0]; j < dims[0]; j++, ii++)
                        {
                           int f = 0, s = 0;
                           for (int k = 0, kk = i * dims[0] * dims[1] + j; k < dims[1]; k++, kk += dims[0])
                           {
                              int v = insData[kk];
                              f += v;
                              s += v * v;
                           }
                           outsData[ii] = (short)(Math.sqrt((float)s/dims[axis] - ((float)f/dims[axis])*((float)f/dims[axis])));
                        }
                     break;
                  case 2:
                     for (int i = iThread; i < dims[0] * dims[1]; i += nThreads)
                     {
                        int f = 0, s = 0;
                        for (int j = 0, k = i; j < dims[2]; j++, k += dims[0] * dims[1])
                        {
                           int v = insData[k];
                           f += v;
                           s += v * v;
                        }
                        outsData[i] = (short)(Math.sqrt((float)s/dims[axis] - ((float)f/dims[axis])*((float)f/dims[axis])));
                     }
                     break;
                  }
                  break;
               case DataArray.FIELD_DATA_INT:
                  switch (axis)
                  {
                  case 0:
                     for (int i = iThread; i < dims[1] * dims[2]; i += nThreads)
                     {
                        int f = 0, s = 0;
                        for (int j = 0, k = i * dims[0]; j < dims[0]; j++, k++)
                        {
                           int v = iniData[k];
                           f += v;
                           s += v * v;
                        }
                        outiData[i] = (int)(Math.sqrt((float)s/dims[axis] - ((float)f/dims[axis])*((float)f/dims[axis])));
                     }
                     break;
                  case 1:
                     for (int i = iThread; i < dims[2]; i += nThreads)
                        for (int j = 0, ii = i * dims[0]; j < dims[0]; j++, ii++)
                        {
                           int f = 0, s = 0;
                           for (int k = 0, kk = i * dims[0] * dims[1] + j; k < dims[1]; k++, kk += dims[0])
                        {
                           int v = iniData[kk];
                           f += v;
                           s += v * v;
                        }
                           outiData[ii] = (int)(Math.sqrt((float)s/dims[axis] - ((float)f/dims[axis])*((float)f/dims[axis])));
                        }
                     break;
                  case 2:
                     for (int i = iThread; i < dims[0] * dims[1]; i += nThreads)
                     {
                        int f = 0, s = 0;
                        for (int j = 0, k = i; j < dims[2]; j++, k += dims[0] * dims[1])
                        {
                           int v = iniData[k];
                           f += v;
                           s += v * v;
                        }
                        outiData[i] = (int)(Math.sqrt((float)s/dims[axis] - ((float)f/dims[axis])*((float)f/dims[axis])));
                     }
                     break;
                  }
                  break;
               case DataArray.FIELD_DATA_FLOAT:
                  switch (axis)
                  {
                  case 0:
                     for (int i = iThread; i < dims[1] * dims[2]; i += nThreads)
                     {
                        float f = 0, s = 0;
                        for (int j = 0, k = i * dims[0]; j < dims[0]; j++, k++)
                        {
                           float v = infData[k];
                           f += v;
                           s += v * v;
                        }
                        outfData[i] = (float)(Math.sqrt(s/dims[axis] - (f/dims[axis])*(f/dims[axis])));
                     }
                     break;
                  case 1:
                     for (int i = iThread; i < dims[2]; i += nThreads)
                        for (int j = 0, ii = i * dims[0]; j < dims[0]; j++, ii++)
                        {
                           float f = 0, s = 0;
                           for (int k = 0, kk = i * dims[0] * dims[1] + j; k < dims[1]; k++, kk += dims[0])
                           {
                              float v = infData[kk];
                              f += v;
                              s += v * v;
                           }
                        outfData[ii] = (float)(Math.sqrt(s/dims[axis] - (f/dims[axis])*(f/dims[axis])));
                        }
                     break;
                  case 2:
                     for (int i = iThread; i < dims[0] * dims[1]; i += nThreads)
                     {
                        float f = 0, s = 0;
                        for (int j = 0, k = i; j < dims[2]; j++, k += dims[0] * dims[1])
                        {
                           float v = infData[k];
                           f += v;
                           s += v * v;
                        }
                        outfData[i] = (float)(Math.sqrt(s/dims[axis] - (f/dims[axis])*(f/dims[axis])));
                     }
                     break;
                  }
                  break;
               case DataArray.FIELD_DATA_DOUBLE:
                  switch (axis)
                  {
                  case 0:
                     for (int i = iThread; i < dims[1] * dims[2]; i += nThreads)
                     {
                        double f = 0, s = 0;
                        for (int j = 0, k = i * dims[0]; j < dims[0]; j++, k++)
                        {
                           double v = indData[k];
                           f += v;
                           s += v * v;
                        }
                        outdData[i] = Math.sqrt(s/dims[axis] - (f/dims[axis])*(f/dims[axis]));
                     }
                     break;
                  case 1:
                     for (int i = iThread; i < dims[2]; i += nThreads)
                        for (int j = 0, ii = i * dims[0]; j < dims[0]; j++, ii++)
                        {
                           double f = 0, s = 0;
                           for (int k = 0, kk = i * dims[0] * dims[1] + j; k < dims[1]; k++, kk += dims[0])
                           {
                              double v = indData[kk];
                              f += v;
                              s += v * v;
                           }
                        outdData[ii] = Math.sqrt(s/dims[axis] - (f/dims[axis])*(f/dims[axis]));
                        }
                     break;
                  case 2:
                     outdData = new double[dims[0] * dims[1]];
                     for (int i = iThread; i < dims[0] * dims[1]; i += nThreads)
                     {
                        double f = 0, s = 0;
                        for (int j = 0, k = i; j < dims[2]; j++, k += dims[0] * dims[1])
                        {
                           double v = indData[k];
                           f += v;
                           s += v * v;
                        }
                        outdData[i] = Math.sqrt(s/dims[axis] - (f/dims[axis])*(f/dims[axis]));
                     }
                     break;
                  }
                  break;
               }
         }
      }
  }

   public final RegularField fieldProjection(RegularField inField, int axis, int function, int slice)
   {       
      if (inField == null || inField.getDims() == null || inField.getDims().length != 3 ||
          axis < 0 || axis >= 3 ||
          function < RegularField3dParams.AVG || function > RegularField3dParams.STDDEV)
         return null;
      int nThreads = pl.edu.icm.visnow.system.main.VisNow.availableProcessors();
      if (nThreads > 3) nThreads -= 1;
      int[] dims = inField.getDims();
      int nInData = dims[0] * dims[1] * dims[2];
      int[] outDims = new int[2];
      float[][] inAffine = inField.getAffine();
      float[][] outAffine = null;
      float[] inCoords = inField.getCoords();
      float[] outCoords = null;
      if (inField.getCoords() == null)
         outAffine = new float[4][3];

      switch (axis)
      {
      case 0:
         outDims[0] = dims[1];
         outDims[1] = dims[2];
         if (inField.getCoords() == null)
            for (int i = 0; i < 3; i++)
            {
               outAffine[3][i] = inAffine[3][i] + slice * inAffine[0][i];
               outAffine[0][i] = inAffine[1][i];
               outAffine[1][i] = inAffine[2][i];
               outAffine[2][i] = 0;
            }
         else
         {
            outCoords = new float[3 * outDims[0] * outDims[1]];
            for (int i = 0, k = slice, l = 0; i < outDims[0] * outDims[1]; i++, k += dims[0])
               for (int j = 0; j < 3; j++, l++)
                  outCoords[l] = inCoords[3 * k + j];
         }
         break;
      case 1:
         outDims[0] = dims[0];
         outDims[1] = dims[2];
         if (inField.getCoords() == null)
            for (int i = 0; i < 3; i++)
            {
               outAffine[3][i] = inAffine[3][i] + slice * inAffine[1][i];
               outAffine[0][i] = inAffine[0][i];
               outAffine[1][i] = inAffine[2][i];
               outAffine[2][i] = 0;
            }
         else
         {
            outCoords = new float[3 * outDims[0] * outDims[1]];
            for (int i = 0, k = 0; i < outDims[1]; i++)
               for (int j = 0, l = 3 * ((i * dims[1] + slice) * dims[0]); j < 3 * outDims[0]; j++, k++, l++)
                  outCoords[k] = inCoords[l];
         }
         break;
      case 2:
         outDims[0] = dims[0];
         outDims[1] = dims[1];
         if (inField.getCoords() == null)
            for (int i = 0; i < 3; i++)
            {
               outAffine[3][i] = inAffine[3][i] + slice * inAffine[2][i];
               outAffine[0][i] = inAffine[0][i];
               outAffine[1][i] = inAffine[1][i];
               outAffine[2][i] = 0;
            }
         else
         {
            outCoords = new float[3 * outDims[0] * outDims[1]];
            for (int i = 0, j = 3 * slice * dims[0] * dims[1]; i < outCoords.length; i++, j++)
               outCoords[i] = inCoords[j];
         }
         break;
      }
      RegularField outField = new RegularField(outDims);
      outField.setNSpace(3);
      if (inField.getCoords() == null)
         outField.setAffine(outAffine);
      else
         outField.setCoords(outCoords);

      for (int n = 0; n < inField.getNData(); n++)
      {
         DataArray dataArr = inField.getData(n);
         if (!dataArr.isSimpleNumeric())
            continue;
         DataArray outDA = null;
         int veclen = dataArr.getVeclen();
         Thread[] workThreads = new Thread[nThreads];
         if (veclen == 1)
         {
            switch (dataArr.getType())
            {
               case DataArray.FIELD_DATA_BYTE:
                  byte[] inbData = dataArr.getBData();
                  byte[] outbData = new byte[nInData / dims[axis]];
                  for (int i = 0; i < workThreads.length; i++)
                  {
                     workThreads[i] = new Thread(new ComputeProjection(dims, outDims, axis, nThreads, i, inbData, outbData, function));
                     workThreads[i].start();
                  }
                  for (int i = 0; i < workThreads.length; i++)
                     try
                     {
                        workThreads[i].join();
                     } catch (Exception e)
                     {
                     }
                  outDA = DataArray.create(outbData, 1, dataArr.getName() + " " + coordNames[axis] + "=" + slice);
                  break;
               case DataArray.FIELD_DATA_SHORT:
                  short[] insData = dataArr.getSData();
                  short[] outsData = new short[nInData / dims[axis]];
                  for (int i = 0; i < workThreads.length; i++)
                  {
                     workThreads[i] = new Thread(new ComputeProjection(dims, outDims, axis, nThreads, i, insData, outsData, function));
                     workThreads[i].start();
                  }
                  for (int i = 0; i < workThreads.length; i++)
                     try
                     {
                        workThreads[i].join();
                     } catch (Exception e)
                     {
                     }
                  outDA = DataArray.create(outsData, 1, dataArr.getName() + " " + coordNames[axis] + "=" + slice);
                  break;
               case DataArray.FIELD_DATA_INT:
                  int[] iniData = dataArr.getIData();
                  int[] outiData = new int[nInData / dims[axis]];
                  for (int i = 0; i < workThreads.length; i++)
                  {
                     workThreads[i] = new Thread(new ComputeProjection(dims, outDims, axis, nThreads, i, iniData, outiData, function));
                     workThreads[i].start();
                  }
                  for (int i = 0; i < workThreads.length; i++)
                     try
                     {
                        workThreads[i].join();
                     } catch (Exception e)
                     {
                     }
                  outDA = DataArray.create(outiData, 1, dataArr.getName() + " " + coordNames[axis] + "=" + slice);
                  break;
               case DataArray.FIELD_DATA_FLOAT:
                  float[] infData = dataArr.getFData();
                  float[] outfData = new float[nInData / dims[axis]];
                  for (int i = 0; i < workThreads.length; i++)
                  {
                     workThreads[i] = new Thread(new ComputeProjection(dims, outDims, axis, nThreads, i, infData, outfData, function));
                     workThreads[i].start();
                  }
                  for (int i = 0; i < workThreads.length; i++)
                     try
                     {
                        workThreads[i].join();
                     } catch (Exception e)
                     {
                     }
                  outDA = DataArray.create(outfData, 1, dataArr.getName() + " avg" + coordNames[axis]);
                  break;
               case DataArray.FIELD_DATA_DOUBLE:
                  double[] indData = dataArr.getDData();
                  double[] outdData = new double[nInData / dims[axis]];
                  for (int i = 0; i < workThreads.length; i++)
                  {
                     workThreads[i] = new Thread(new ComputeProjection(dims, outDims, axis, nThreads, i, indData, outdData, function));
                     workThreads[i].start();
                  }
                  for (int i = 0; i < workThreads.length; i++)
                     try
                     {
                        workThreads[i].join();
                     } catch (Exception e)
                     {
                     }
                  outDA = DataArray.create(outdData, 1, dataArr.getName() + " " + coordNames[axis] + "=" + slice);
                  break;
            }
            outDA.setMinv(dataArr.getMinv());
            outDA.setMaxv(dataArr.getMaxv());
            outField.addData(outDA);
         }
         else
         {
            float[] infData = dataArr.getVectorNorms();
            float[] outfData = new float[nInData / dims[axis]];
            for (int i = 0; i < workThreads.length; i++)
            {
               workThreads[i] = new Thread(new ComputeProjection(dims, outDims, axis, nThreads, i, infData, outfData, function));
               workThreads[i].start();
            }
            for (int i = 0; i < workThreads.length; i++)
               try
               {
                  workThreads[i].join();
               } catch (Exception e)
               {
               }
            outDA = DataArray.create(outfData, 1, dataArr.getName() + " avg" + coordNames[axis]);
            outField.addData(outDA);
            
         }
      }
      return outField;
   }

}
