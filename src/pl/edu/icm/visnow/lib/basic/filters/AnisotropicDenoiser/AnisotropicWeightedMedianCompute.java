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

package pl.edu.icm.visnow.lib.basic.filters.AnisotropicDenoiser;

import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.gui.events.FloatValueModificationEvent;
import pl.edu.icm.visnow.gui.events.FloatValueModificationListener;
import pl.edu.icm.visnow.lib.utils.numeric.HeapSort;

/**
 *
 * @author  Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class AnisotropicWeightedMedianCompute extends AbstractAnisotropicWeightedMedianCompute
{

   private int progress = 0;
   private int[] dims;
   private int radius;
   private float slope, slope1;
   private int anisotropyVlen;
   private float[] anisotropy;
   /** Creates a new instance of InterpolateRegularField */
   public AnisotropicWeightedMedianCompute()
   {
   }
   
   public synchronized RegularField compute(RegularField inField, RegularField anisotropyField, Params params)
   {  
      if (inField == null)
         return null;
      radius    = params.getRadius(); 
      slope   = params.getSlope();   slope  *= slope;
      slope1  = params.getSlope1();  slope1 *= slope1;
      dims = inField.getDims();
      int[] outDims = new int[dims.length];
      float[][] inPts = inField.getExtents();
      float[][] outPts = new float[2][3];
      int outNdata = 1;
      int vlen;
      int nThreads = params.getNThreads();
      Thread[] workThreads = new Thread[nThreads]; 
      for (int i = 0; i < dims.length; i++)
      {
         outDims[i] = dims[i];
         outNdata *= outDims[i];
         outPts[0][i] = inPts[0][i];
         outPts[1][i] = inPts[1][i];
      }
      RegularField outField = new RegularField(outDims, outPts);
      for (int nComponent = 0; nComponent < params.getComponentsNumber(); nComponent++)
      {
         int component = params.getComponent(nComponent);
         int anisotropyComponent = params.getAnisotropyComponent(nComponent);
         DataArray dataArr = inField.getData(component);
         System.out.print("averaging component "+component+"("+dataArr.getName()+")");
         if (anisotropyComponent<0)
         {
            anisotropy = null;
            System.out.println("");
         }
         else
         {
            DataArray anisotropyArr = anisotropyField.getData(anisotropyComponent);
            anisotropyVlen = anisotropyArr.getVeclen();
            if (anisotropyVlen!=1 && anisotropyVlen!=dims.length)
               return null;
            anisotropy = anisotropyArr.getFData();
            System.out.println(" with anisotropy component "+anisotropyComponent+"("+
                               anisotropyArr.getName()+")");
         }
         vlen = dataArr.getVeclen();
         long t0 = System.currentTimeMillis();
         switch (dataArr.getType())
         {
            case DataArray.FIELD_DATA_BYTE:
               byte[] inBData = dataArr.getBData();
               byte[] outBData = new byte[outNdata * vlen];
               for (int i = 0; i < workThreads.length; i++)
               {
                  workThreads[i] = new Thread(new FilterArray(vlen, nThreads, i, params.isComputeBySlice(), 
                                                              inBData, outBData));
                  workThreads[i].start();
               }
               for (int i = 0; i < workThreads.length; i++)
                  try {workThreads[i].join();}
                  catch (Exception e){}
               outField.addData(DataArray.create(outBData, vlen, dataArr.getName()));
               break;
            case DataArray.FIELD_DATA_SHORT:
               short[] inSData = dataArr.getSData();
               short[] outSData = new short[outNdata * vlen];
               for (int i = 0; i < workThreads.length; i++)
               {
                  workThreads[i] = new Thread(new FilterArray(vlen, nThreads, i, params.isComputeBySlice(), 
                                                              inSData, outSData));
                  workThreads[i].start();
               }
               for (int i = 0; i < workThreads.length; i++)
                  try {workThreads[i].join();} 
                  catch (Exception e) {}
               outField.addData(DataArray.create(outSData, vlen, dataArr.getName()));
               break;
            case DataArray.FIELD_DATA_INT:
               int[] inIData = dataArr.getIData();
               int[] outIData = new int[outNdata*vlen];
               for (int i = 0; i < workThreads.length; i++)
               {
                  workThreads[i] = new Thread(new FilterArray(vlen, nThreads, i, params.isComputeBySlice(), 
                                                              inIData, outIData));
                  workThreads[i].start();
               }
               for (int i = 0; i < workThreads.length; i++)
                  try {workThreads[i].join();} 
                  catch (Exception e) {}
               outField.addData(DataArray.create(outIData, vlen, dataArr.getName()));
               break;
            case DataArray.FIELD_DATA_FLOAT:
               float[] inFData = dataArr.getFData();
               float[] outFData = new float[outNdata * vlen];
               for (int i = 0; i < workThreads.length; i++)
               {
                  workThreads[i] = new Thread(new FilterArray(vlen, nThreads, i, params.isComputeBySlice(), 
                                                              inFData, outFData));
                  workThreads[i].start();
               }
               for (int i = 0; i < workThreads.length; i++)
                  try {workThreads[i].join();} 
                  catch (Exception e) {}
               outField.addData(DataArray.create(outFData, vlen, dataArr.getName()));
               break;
            case DataArray.FIELD_DATA_DOUBLE:
               double[] inDData = dataArr.getDData();
               double[] outDData = new double[outNdata*vlen];
               for (int i = 0; i < workThreads.length; i++)
               {
                  workThreads[i] = new Thread(new FilterArray(vlen, nThreads, i, params.isComputeBySlice(), 
                                                              inDData, outDData));
                  workThreads[i].start();
               }
               for (int i = 0; i < workThreads.length; i++)
                  try {workThreads[i].join();} 
                  catch (Exception e) {}
               outField.addData(DataArray.create(outDData, vlen, dataArr.getName()));
               break;
         }
         System.out.println("elapsed time "+(System.currentTimeMillis()-t0)/(1000.f));
         System.out.println("average slice time using "+nThreads+" thread(s): "+((System.currentTimeMillis()-t0)/(1000.f*(dims[dims.length-1]-2*radius+1))));
      }
      if (outField.getNData() > 0)
         return outField;
      else
         return null;
   }
   
   class FilterArray implements Runnable
   {
      int type;
      int vlen; 
      int nThreads;
      int iThread;
      boolean bySlice;
      byte[]    inBData, outBData;
      short[]   inSData, outSData;
      int[]     inIData, outIData;
      float[]   inFData, outFData;
      double[]  inDData, outDData;
      
      
      public FilterArray(int vlen, int nThreads, int iThread, boolean bySlice, 
                         byte[] inData, byte[] outData)
      {
         type = DataArray.FIELD_DATA_BYTE;
         this.nThreads       = nThreads;
         this.iThread        = iThread;
         this.vlen           = vlen;
         this.bySlice        = bySlice;
         this.inBData        = inData;
         this.outBData       = outData;
      }

      public FilterArray(int vlen, int nThreads, int iThread, boolean bySlice, 
                         short[] inData, short[] outData)
      {
         type = DataArray.FIELD_DATA_SHORT;
         this.nThreads       = nThreads;
         this.iThread        = iThread;
         this.vlen           = vlen;
         this.bySlice        = bySlice;
         this.inSData        = inData;
         this.outSData       = outData;
     }

      public FilterArray(int vlen, int nThreads, int iThread, boolean bySlice, 
                         int[] inData, int[] outData)
      {
         type = DataArray.FIELD_DATA_INT;
         this.nThreads       = nThreads;
         this.iThread        = iThread;
         this.vlen           = vlen;
         this.bySlice        = bySlice;
         this.inIData        = inData;
         this.outIData       = outData;
    }

      public FilterArray(int vlen, int nThreads, int iThread, boolean bySlice, 
                         float[] inData, float[] outData)
      {
         type = DataArray.FIELD_DATA_FLOAT;
         this.nThreads       = nThreads;
         this.iThread        = iThread;
         this.vlen           = vlen;
         this.bySlice        = bySlice;
         this.inFData        = inData;
         this.outFData       = outData;
     }

      public FilterArray(int vlen, int nThreads, int iThread, boolean bySlice, 
                         double[] inData, double[] outData)
      {
         type = DataArray.FIELD_DATA_DOUBLE;
         this.nThreads       = nThreads;
         this.iThread        = iThread;
         this.vlen           = vlen;
         this.bySlice        = bySlice;
         this.inDData        = inData;
         this.outDData       = outData;
     }

     public void run()
     {
         int nSort = 1;
         for (int i = 0; i < dims.length; i++)
            nSort *= (2*radius+1);        
         float[] r = new float[2 * nSort];
         int rr, kl, ku, jl, ju, il, iu;
         float w = 0, sw = 0;
         for (int v = 0; v < vlen; v++)
            switch (dims.length)
            {
            case 3:
            if (bySlice)
               for (int k0 = iThread ; k0 < dims[2]; k0 += nThreads)
               {
                  if (iThread == 0)
                      fireStatusChanged((float)k0/dims[2]);
                  int m = k0*dims[0]*dims[1]*vlen+v;
                  kl = -radius; if (k0<radius) kl = -k0;
                  ku = radius;  if (k0>=dims[2]-radius) ku = dims[2]-k0-1;
                  for (int j0 = 0; j0 < dims[1]; j0++)
                  {
                     jl = -radius; if (j0<radius) jl = -j0;
                     ju = radius;  if (j0>=dims[1]-radius) ju = dims[1]-j0-1;
                     for (int i0 = 0, n0 = (dims[1]*k0+j0)*dims[0]; 
                              i0 < dims[0]; 
                              i0++, m+=vlen, n0++)
                     {
                        il = -radius; if (i0<radius) il = -i0;
                        iu = radius;  if (i0>=dims[0]-radius) iu = dims[0]-i0-1;
                        sw = 0.f; 
                        int l = 0;
                        switch (type)
                        {
                   case DataArray.FIELD_DATA_BYTE:
                              for (int j = jl; j <= ju; j++)
                                 for (int i = il, n = (k0*dims[1]+j+j0)*dims[0]+i0+il, p = j*j; 
                                          i <= iu; 
                                          i++, n++, l++)
                                 {
                                    w = 0;
                                    if (anisotropy!=null)
                                    {
                                       if (anisotropyVlen > 1)
                                          w = j*anisotropy[n0*anisotropyVlen+1]+
                                              i*anisotropy[n0*anisotropyVlen];
                                       else
                                          w = anisotropy[n0]-anisotropy[n];
                                    }
                                    w = (float)(Math.exp(-w*w/slope1-(p+i*i)/slope));
                                    r[2*l] = inBData[n*vlen+v];
                                    if (r[2*l] < 0)
                                       r[2*l] += 256;
                                    r[2*l+1] = w;
                                    sw += w;
                                 }
                           rr = (int)getMedian(r, l, sw);
                           if (rr>127)
                              outBData[m] = (byte)(rr-256);
                           else
                              outBData[m] = (byte)rr;
                           break;
                   case DataArray.FIELD_DATA_SHORT:
                              for (int j = jl; j <= ju; j++)
                                 for (int i = il, n = (k0*dims[1]+j+j0)*dims[0]+i0+il, p = j*j; 
                                          i <= iu; 
                                          i++, n++, l++)
                                 {
                                    w = 0;
                                    if (anisotropy!=null)
                                    {
                                       if (anisotropyVlen > 1)
                                          w = j*anisotropy[n0*anisotropyVlen+1]+
                                              i*anisotropy[n0*anisotropyVlen];
                                       else
                                          w = anisotropy[n0]-anisotropy[n];
                                    }
                                    w = (float)(Math.exp(-w*w/slope1-(p+i*i)/slope));
                                    r[2*l] = inSData[n*vlen+v];
                                    r[2*l+1] = w;
                                    sw += w;
                                 }
                           outSData[m] = (short)getMedian(r, l, sw);
                           break;
                   case DataArray.FIELD_DATA_INT:
                              for (int j = jl; j <= ju; j++)
                                 for (int i = il, n = (k0*dims[1]+j+j0)*dims[0]+i0+il, p = j*j; 
                                          i <= iu; 
                                          i++, n++, l++)
                                 {
                                    w = 0;
                                    if (anisotropy!=null)
                                    {
                                       if (anisotropyVlen > 1)
                                          w = j*anisotropy[n0*anisotropyVlen+1]+
                                              i*anisotropy[n0*anisotropyVlen];
                                       else
                                          w = anisotropy[n0]-anisotropy[n];
                                    }
                                    w = (float)(Math.exp(-w*w/slope1-(p+i*i)/slope));
                                    r[2*l] = inIData[n*vlen+v];
                                    r[2*l+1] = w;
                                    sw += w;
                                 }
                           outIData[m] = (int)getMedian(r, l, sw);
                           break;
                   case DataArray.FIELD_DATA_FLOAT:
                              for (int j = jl; j <= ju; j++)
                                 for (int i = il, n = (k0*dims[1]+j+j0)*dims[0]+i0+il, p = j*j; 
                                          i <= iu; 
                                          i++, n++, l++)
                                 {
                                    w = 0;
                                    if (anisotropy!=null)
                                    {
                                       if (anisotropyVlen > 1)
                                          w = j*anisotropy[n0*anisotropyVlen+1]+
                                              i*anisotropy[n0*anisotropyVlen];
                                       else
                                          w = anisotropy[n0]-anisotropy[n];
                                    }
                                    w = (float)(Math.exp(-w*w/slope1-(p+i*i)/slope));
                                    r[2*l] = inFData[n*vlen+v];
                                    r[2*l+1] = w;
                                    sw += w;
                                 }
                           outFData[m] = getMedian(r, l, sw);
                           break;
                   case DataArray.FIELD_DATA_DOUBLE:
                              for (int j = jl; j <= ju; j++)
                                 for (int i = il, n = (k0*dims[1]+j+j0)*dims[0]+i0+il, p = j*j; 
                                          i <= iu; 
                                          i++, n++, l++)
                                 {
                                    w = 0;
                                    if (anisotropy!=null)
                                    {
                                       if (anisotropyVlen > 1)
                                          w = j*anisotropy[n0*anisotropyVlen+1]+
                                              i*anisotropy[n0*anisotropyVlen];
                                       else
                                          w = anisotropy[n0]-anisotropy[n];
                                    }
                                    w = (float)(Math.exp(-w*w/slope1-(p+i*i)/slope));
                                    r[2*l] = (float)inDData[n*vlen+v];
                                    r[2*l+1] = w;
                                    sw += w;
                                 }
                           outDData[m] = getMedian(r, l, sw);
                           break;
                        }
                     }
                  }
               }
            else
               for (int k0 = iThread ; k0 < dims[2]; k0 += nThreads)
               {
                  if (iThread == 0)
                      fireStatusChanged((float)k0/dims[2]);
                  int m = k0*dims[0]*dims[1]*vlen+v;
                  kl = -radius; if (k0<radius) kl = -k0;
                  ku = radius;  if (k0>=dims[2]-radius) ku = dims[2]-k0-1;
                  for (int j0 = 0; j0 < dims[1]; j0++)
                  {
                     jl = -radius; if (j0<radius) jl = -j0;
                     ju = radius;  if (j0>=dims[1]-radius) ju = dims[1]-j0-1;
                     for (int i0 = 0, n0 = (dims[1]*k0+j0)*dims[0]; 
                              i0 < dims[0]; 
                              i0++, m+=vlen, n0++)
                     {
                        il = -radius; if (i0<radius) il = -i0;
                        iu = radius;  if (i0>=dims[0]-radius) iu = dims[0]-i0-1;
                        sw = 0.f; 
                        int l = 0;
                        switch (type)
                        {
                   case DataArray.FIELD_DATA_BYTE:
                           for (int k = kl; k <= ku; k++)
                              for (int j = jl; j <= ju; j++)
                                 for (int i = il, n = ((k0+k)*dims[1]+j+j0)*dims[0]+i0+il, p = k*k+j*j; 
                                          i <= iu; 
                                          i++, n++, l++)
                                 {
                                    w = 0;
                                    if (anisotropy!=null)
                                    {
                                       if (anisotropyVlen > 1)
                                          w = k*anisotropy[n0*anisotropyVlen+2]+
                                              j*anisotropy[n0*anisotropyVlen+1]+
                                              i*anisotropy[n0*anisotropyVlen];
                                       else
                                          w = anisotropy[n0]-anisotropy[n];
                                    }
                                    w = (float)(Math.exp(-w*w/slope1-(p+i*i)/slope));
                                    r[2*l] = inBData[n*vlen+v];
                                    if (r[2*l] < 0)
                                       r[2*l] += 256;
                                    r[2*l+1] = w;
                                    sw += w;
                                 }
                           rr = (int)getMedian(r, l, sw);
                           if (rr>127)
                              outBData[m] = (byte)(rr-256);
                           else
                              outBData[m] = (byte)rr;
                           break;
                   case DataArray.FIELD_DATA_SHORT:
                           for (int k = kl; k <= ku; k++)
                              for (int j = jl; j <= ju; j++)
                                 for (int i = il, n = ((k0+k)*dims[1]+j+j0)*dims[0]+i0+il, p = k*k+j*j; 
                                          i <= iu; 
                                          i++, n++, l++)
                                 {
                                    w = 0;
                                    if (anisotropy!=null)
                                    {
                                       if (anisotropyVlen > 1)
                                          w = k*anisotropy[n0*anisotropyVlen+2]+
                                              j*anisotropy[n0*anisotropyVlen+1]+
                                              i*anisotropy[n0*anisotropyVlen];
                                       else
                                          w = anisotropy[n0]-anisotropy[n];
                                    }
                                    w = (float)(Math.exp(-w*w/slope1-(p+i*i)/slope));
                                    r[2*l] = inSData[n*vlen+v];
                                    r[2*l+1] = w;
                                    sw += w;
                                 }
                           outSData[m] = (short)getMedian(r, l, sw);
                           break;
                   case DataArray.FIELD_DATA_INT:
                           for (int k = kl; k <= ku; k++)
                              for (int j = jl; j <= ju; j++)
                                 for (int i = il, n = ((k0+k)*dims[1]+j+j0)*dims[0]+i0+il, p = k*k+j*j; 
                                          i <= iu; 
                                          i++, n++, l++)
                                 {
                                    w = 0;
                                    if (anisotropy!=null)
                                    {
                                       if (anisotropyVlen > 1)
                                          w = k*anisotropy[n0*anisotropyVlen+2]+
                                              j*anisotropy[n0*anisotropyVlen+1]+
                                              i*anisotropy[n0*anisotropyVlen];
                                       else
                                          w = anisotropy[n0]-anisotropy[n];
                                    }
                                    w = (float)(Math.exp(-w*w/slope1-(p+i*i)/slope));
                                    r[2*l] = inIData[n*vlen+v];
                                    r[2*l+1] = w;
                                    sw += w;
                                 }
                           outIData[m] = (int)getMedian(r, l, sw);
                           break;
                   case DataArray.FIELD_DATA_FLOAT:
                            for (int k = kl; k <= ku; k++)
                              for (int j = jl; j <= ju; j++)
                                 for (int i = il, n = ((k0+k)*dims[1]+j+j0)*dims[0]+i0+il, p = k*k+j*j; 
                                          i <= iu; 
                                          i++, n++, l++)
                                 {
                                    w = 0;
                                    if (anisotropy!=null)
                                    {
                                       if (anisotropyVlen > 1)
                                          w = k*anisotropy[n0*anisotropyVlen+2]+
                                              j*anisotropy[n0*anisotropyVlen+1]+
                                              i*anisotropy[n0*anisotropyVlen];
                                       else
                                          w = anisotropy[n0]-anisotropy[n];
                                    }
                                    w = (float)(Math.exp(-w*w/slope1-(p+i*i)/slope));
                                    r[2*l] = inFData[n*vlen+v];
                                    r[2*l+1] = w;
                                    sw += w;
                                 }
                           outFData[m] = getMedian(r, l, sw);
                           break;
                   case DataArray.FIELD_DATA_DOUBLE:
                            for (int k = kl; k <= ku; k++)
                              for (int j = jl; j <= ju; j++)
                                 for (int i = il, n = ((k0+k)*dims[1]+j+j0)*dims[0]+i0+il, p = k*k+j*j; 
                                          i <= iu; 
                                          i++, n++, l++)
                                 {
                                    w = 0;
                                    if (anisotropy!=null)
                                    {
                                       if (anisotropyVlen > 1)
                                          w = k*anisotropy[n0*anisotropyVlen+2]+
                                              j*anisotropy[n0*anisotropyVlen+1]+
                                              i*anisotropy[n0*anisotropyVlen];
                                       else
                                          w = anisotropy[n0]-anisotropy[n];
                                    }
                                    w = (float)(Math.exp(-w*w/slope1-(p+i*i)/slope));
                                    r[2*l] = (float)inDData[n*vlen+v];
                                    r[2*l+1] = w;
                                    sw += w;
                                 }
                           outDData[m] = getMedian(r, l, sw);
                           break;
                        }
                     }
                  }
               }
               break;
            case 2:
               for (int j0 = iThread; j0 < dims[1]; j0+= nThreads)
               {
                  jl = -radius; if (j0<radius) jl = -j0;
                  ju = radius;  if (j0>=dims[1]-radius) ju = dims[1]-j0-1;
                  if (iThread == 0)
                     fireStatusChanged((float)j0/dims[1]);
                  for (int i0 = 0, n0 = j0*dims[0], m = j0*dims[0]*vlen+v; 
                           i0 < dims[0]; 
                           i0++, m+=vlen, n0++)
                  {
                     il = -radius; if (i0<radius) il = -i0;
                     iu = radius;  if (i0>=dims[0]-radius) iu = dims[0]-i0-1;
                     sw = 0.f; 
                     int l = 0;
                     switch (type)
                     {
                case DataArray.FIELD_DATA_BYTE:
                        for (int j = jl; j <= ju; j++)
                           for (int i = il, n = (j+j0)*dims[0]+i0+il, p = j*j; 
                                    i <= iu; 
                                    i++, n++,l++)
                           {
                              w = 0;
                              if (anisotropy!=null)
                              {
                                 if (anisotropyVlen > 1)
                                    w = j*anisotropy[n0*anisotropyVlen+1]+
                                        i*anisotropy[n0*anisotropyVlen];
                                 else
                                    w = anisotropy[n0]-anisotropy[n];
                              }
                              w = (float)(Math.exp(-w*w/slope1-(p+i*i)/slope));
                              r[2*l] = inBData[n*vlen+v];
                              if (r[2*l] < 0)
                                 r[2*l] += 256;
                              r[2*l+1] = w;
                              sw += w;
                           }
                        rr = (int)getMedian(r, l, sw);
                        if (rr>127)
                           outBData[m] = (byte)(rr-256);
                        else
                           outBData[m] = (byte)rr;
                        break;
                case DataArray.FIELD_DATA_SHORT:
                        for (int j = jl; j <= ju; j++)
                           for (int i = il, n = (j+j0)*dims[0]+i0+il, p = j*j; 
                                    i <= iu; 
                                    i++, n++, l++)
                           {
                              w = 0;
                              if (anisotropy!=null)
                              {
                                 if (anisotropyVlen > 1)
                                    w = j*anisotropy[n0*anisotropyVlen+1]+
                                        i*anisotropy[n0*anisotropyVlen];
                                 else
                                    w = anisotropy[n0]-anisotropy[n];
                              }
                              w = (float)(Math.exp(-w*w/slope1-(p+i*i)/slope));
                              r[2*l] = inSData[n*vlen+v];
                              r[2*l+1] = w;
                              sw += w;
                           }
                        outSData[m] = (short)getMedian(r, l, sw);
                        break;
               case DataArray.FIELD_DATA_INT:
                        for (int j = jl; j <= ju; j++)
                           for (int i = il, n = (j+j0)*dims[0]+i0+il, p = j*j; 
                                    i <= iu; 
                                    i++, n++, l++)
                           {
                              w = 0;
                              if (anisotropy!=null)
                              {
                                 if (anisotropyVlen > 1)
                                    w = j*anisotropy[n0*anisotropyVlen+1]+
                                        i*anisotropy[n0*anisotropyVlen];
                                 else
                                    w = anisotropy[n0]-anisotropy[n];
                              }
                              w = (float)(Math.exp(-w*w/slope1-(p+i*i)/slope));
                              r[2*l] = inIData[n*vlen+v];
                              r[2*l+1] = w;
                              sw += w;
                           }
                        outIData[m] = (int)getMedian(r, l, sw);
                        break;
               case DataArray.FIELD_DATA_FLOAT:
                        for (int j = jl; j <= ju; j++)
                           for (int i = il, n = (j+j0)*dims[0]+i0+il, p = j*j; 
                                    i <= iu; 
                                    i++, n++, l++)
                           {
                              w = 0;
                              if (anisotropy!=null)
                              {
                                 if (anisotropyVlen > 1)
                                    w = j*anisotropy[n0*anisotropyVlen+1]+
                                        i*anisotropy[n0*anisotropyVlen];
                                 else
                                    w = anisotropy[n0]-anisotropy[n];
                              }
                              w = (float)(Math.exp(-w*w/slope1-(p+i*i)/slope));
                              r[2*l] = inFData[n*vlen+v];
                              r[2*l+1] = w;
                              sw += w;
                           }
                        outFData[m] = getMedian(r, l, sw);
                        break;
               case DataArray.FIELD_DATA_DOUBLE:
                         for (int j = jl; j <= ju; j++)
                           for (int i = il, n = (j+j0)*dims[0]+i0+il, p = j*j; 
                                    i <= iu; 
                                    i++, n++, l++)
                           {
                              w = 0;
                              if (anisotropy!=null)
                              {
                                 if (anisotropyVlen > 1)
                                    w = j*anisotropy[n0*anisotropyVlen+1]+
                                        i*anisotropy[n0*anisotropyVlen];
                                 else
                                    w = anisotropy[n0]-anisotropy[n];
                              }
                              w = (float)(Math.exp(-w*w/slope1-(p+i*i)/slope));
                              r[2*l] = (float)inDData[n*vlen+v];
                              r[2*l+1] = w;
                              sw += w;
                           }
                        outDData[m] = getMedian(r, l, sw);
                        break;
                     }
                  }
            }
            break;
         case 1:
            for (int i0 = iThread, m = iThread; i0 < dims[0]; i0+= nThreads, m+= nThreads)
            {
               il = i0 - radius; if (il<0) il = 0;
               iu = i0 + radius;  if (i0>=dims[0]) iu = dims[0]-1;
               if (iThread == 0)
                  fireStatusChanged((float)i0/dims[0]);
               sw = 0.f;
               int l = 0;
               switch (type)
               {
          case DataArray.FIELD_DATA_BYTE:
                  for (int i = il; i <= iu; i++, l++)
                  {
                     w = 0;
                     if (anisotropy!=null)
                        w = anisotropy[i]-anisotropy[i0];
                     w = (float)(Math.exp(w*w/slope1-(i-i0)*(i-i0)/slope));
                     r[2*l] = inBData[i*vlen+v];
                     if (r[2*l] < 0)
                        r[2*l] += 256;
                     r[2*l+1] = w;
                     sw += w;
                  }
                  rr =  (int)getMedian(r, l, sw);
                  if (rr>127)
                     outBData[m] = (byte)(rr-256);
                  else
                     outBData[m] = (byte)rr;
                  break;
          case DataArray.FIELD_DATA_SHORT:
                  for (int i = il; i <= iu; i++)
                  {
                     w = 0;
                     if (anisotropy!=null)
                        w = anisotropy[i]-anisotropy[i0];
                     w = (float)(Math.exp(w*w/slope1-(i-i0)*(i-i0)/slope));
                     r[2*l] = inSData[i*vlen+v];
                     r[2*l+1] = w;
                     sw += w;
                  }
                  outSData[m] = (short)getMedian(r, l, sw);
                  break;
          case DataArray.FIELD_DATA_INT:
                  for (int i = il; i <= iu; i++)
                  {
                     w = 0;
                     if (anisotropy!=null)
                        w = anisotropy[i]-anisotropy[i0];
                     w = (float)(Math.exp(w*w/slope1-(i-i0)*(i-i0)/slope));
                     r[2*l] = inIData[i*vlen+v];
                     r[2*l+1] = w;
                     sw += w;
                  }
                  outIData[m] = (int)getMedian(r, l, sw);
                  break;
          case DataArray.FIELD_DATA_FLOAT:
                  for (int i = il; i <= iu; i++)
                  {
                     w = 0;
                     if (anisotropy!=null)
                        w = anisotropy[i]-anisotropy[i0];
                     w = (float)(Math.exp(w*w/slope1-(i-i0)*(i-i0)/slope));
                     r[2*l] = inFData[i*vlen+v];
                     r[2*l+1] = w;
                     sw += w;
                  }
                  outFData[m] = getMedian(r, l, sw);
                  break;
          case DataArray.FIELD_DATA_DOUBLE:
                  for (int i = il; i <= iu; i++)
                  {
                     w = 0;
                     if (anisotropy!=null)
                        w = anisotropy[i]-anisotropy[i0];
                     w = (float)(Math.exp(w*w/slope1-(i-i0)*(i-i0)/slope));
                     r[2*l] = (float)inDData[i*vlen+v];
                     r[2*l+1] = w;
                     sw += w;
                  }
                  outDData[m] = getMedian(r, l, sw);
                  break;
               }
            }
            break;
         }
      }
     
      private float getMedian(float[] r, int n, float sw)
      {
         if (n==0)
            return 0;
         HeapSort.sort(r,n,2);
         float w = 0, v = 0;
         for (int l = 1; l < 2*n; l+=2)
         {
            w += r[l];
            if (w > sw/2)
            {
               if (l>1)
               {
                  v = (sw/2-v)/r[l];
                  return v*r[l-1]+(1-v)*r[l-3];
               }
               else
                  return r[l-1];
            }
            v = w;
         }
         return r[2*n-2];
      }
     
   }

   private transient FloatValueModificationListener statusListener = null;

   public void addFloatValueModificationListener(FloatValueModificationListener listener)
   {
      if (statusListener == null)
         this.statusListener = listener;
      else
         System.out.println(""+this+": only one status listener can be added");
   }

   private void fireStatusChanged(float status)
   {
       FloatValueModificationEvent e = new FloatValueModificationEvent(this, status, true);
       if (statusListener != null)
          statusListener.floatValueChanged(e);
   }
}
