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

import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.gui.events.FloatValueModificationEvent;
import pl.edu.icm.visnow.gui.events.FloatValueModificationListener;

/**
 *
 * @author  Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class AnisotropicWeightedAverageCompute extends AbstractAnisotropicWeightedAverageCompute
{

   private int progress = 0;
   private int[] dims;
   private int radius;
   private float slope, slope1;
   private int anisotropyVlen;
   private float[] anisotropy;
   private float[] sigma;
   /** Creates a new instance of InterpolateRegularField */
   public AnisotropicWeightedAverageCompute()
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
      int outNdata = 1;
      int vlen;
      int nThreads = params.getNThreads();
      Thread[] workThreads = new Thread[nThreads]; 
      for (int i = 0; i < dims.length; i++)
         outNdata *= dims[i];
      RegularField outField = new RegularField(dims);
      outField.setAffine(inField.getAffine());
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
         if (params.isComputeSigma())
            sigma = new float[outNdata * vlen];
         long t0 = System.currentTimeMillis();
         switch (dataArr.getType())
         {
            case DataArray.FIELD_DATA_BYTE:
               byte[] inBData = dataArr.getBData();
               byte[] outBData = new byte[outNdata * vlen];
               for (int i = 0; i < workThreads.length; i++)
               {
                  workThreads[i] = new Thread(new FilterArray(vlen, nThreads, i,
                                                              inBData, outBData, params.isComputeSigma()));
                  workThreads[i].start();
               }
               for (int i = 0; i < workThreads.length; i++)
                  try {workThreads[i].join();}
                  catch (Exception e){}
               outField.addData(DataArray.create(outBData, vlen, "smoothed " + dataArr.getName()));
               if (params.isComputeSigma())
               {
                  byte[] sgm = new byte[outNdata * vlen];
                  if (params.isNormalizeSigma())
                  {
                     float smax = 0;
                     for (int i = 0; i < sigma.length; i++)
                        if (smax < sigma[i]) smax = sigma[i];
                     smax = 255/smax;
                     for (int i = 0; i < sigma.length; i++)
                        sgm[i] = (byte)((int)(sigma[i]*smax) & 0xff);
                  }
                  else
                     for (int i = 0; i < outNdata * vlen; i++)
                        sgm[i] = (byte)((int)sigma[i] & 0xff);
                  outField.addData(DataArray.create(sgm, vlen, "std. dev " + dataArr.getName()));
               }
               break;
            case DataArray.FIELD_DATA_SHORT:
               short[] inSData = dataArr.getSData();
               short[] outSData = new short[outNdata * vlen];
               for (int i = 0; i < workThreads.length; i++)
               {
                  workThreads[i] = new Thread(new FilterArray(vlen, nThreads, i,
                                                              inSData, outSData, params.isComputeSigma()));
                  workThreads[i].start();
               }
               for (int i = 0; i < workThreads.length; i++)
                  try {workThreads[i].join();} 
                  catch (Exception e) {}
               outField.addData(DataArray.create(outSData, vlen, dataArr.getName()));
               if (params.isComputeSigma())
               {
                  if (params.isNormalizeSigma())
                  {
                     float smax = 0;
                     for (int i = 0; i < sigma.length; i++)
                        if (smax < sigma[i]) smax = sigma[i];
                     smax = 255/smax;
                     for (int i = 0; i < sigma.length; i++)
                        sigma[i] *= smax;
                  }
                  outField.addData(DataArray.create(sigma, vlen, "std. dev " + dataArr.getName()));
               }
               break;
            case DataArray.FIELD_DATA_INT:
               int[] inIData = dataArr.getIData();
               int[] outIData = new int[outNdata*vlen];
               for (int i = 0; i < workThreads.length; i++)
               {
                  workThreads[i] = new Thread(new FilterArray(vlen, nThreads, i,
                                                              inIData, outIData, params.isComputeSigma()));
                  workThreads[i].start();
               }
               for (int i = 0; i < workThreads.length; i++)
                  try {workThreads[i].join();} 
                  catch (Exception e) {}
               outField.addData(DataArray.create(outIData, vlen, dataArr.getName()));
               if (params.isComputeSigma())
               {
                  if (params.isNormalizeSigma())
                  {
                     float smax = 0;
                     for (int i = 0; i < sigma.length; i++)
                        if (smax < sigma[i]) smax = sigma[i];
                     smax = 255/smax;
                     for (int i = 0; i < sigma.length; i++)
                        sigma[i] *= smax;
                  }
                  outField.addData(DataArray.create(sigma, vlen, "std. dev " + dataArr.getName()));
               }
               break;
            case DataArray.FIELD_DATA_FLOAT:
               float[] inFData = dataArr.getFData();
               float[] outFData = new float[outNdata * vlen];
               for (int i = 0; i < workThreads.length; i++)
               {
                  workThreads[i] = new Thread(new FilterArray(vlen, nThreads, i,
                                                              inFData, outFData, params.isComputeSigma()));
                  workThreads[i].start();
               }
               for (int i = 0; i < workThreads.length; i++)
                  try {workThreads[i].join();} 
                  catch (Exception e) {}
               outField.addData(DataArray.create(outFData, vlen, dataArr.getName()));
               if (params.isComputeSigma())
               {
                  if (params.isNormalizeSigma())
                  {
                     float smax = 0;
                     for (int i = 0; i < sigma.length; i++)
                        if (smax < sigma[i]) smax = sigma[i];
                     smax = 255/smax;
                     for (int i = 0; i < sigma.length; i++)
                        sigma[i] *= smax;
                  }
                  outField.addData(DataArray.create(sigma, vlen, "std. dev " + dataArr.getName()));
               }
               break;
            case DataArray.FIELD_DATA_DOUBLE:
               double[] inDData = dataArr.getDData();
               double[] outDData = new double[outNdata*vlen];
               for (int i = 0; i < workThreads.length; i++)
               {
                  workThreads[i] = new Thread(new FilterArray(vlen, nThreads, i,
                                                              inDData, outDData, params.isComputeSigma()));
                  workThreads[i].start();
               }
               for (int i = 0; i < workThreads.length; i++)
                  try {workThreads[i].join();} 
                  catch (Exception e) {}
               outField.addData(DataArray.create(outDData, vlen, dataArr.getName()));
               if (params.isComputeSigma())
               {
                  if (params.isNormalizeSigma())
                  {
                     float smax = 0;
                     for (int i = 0; i < sigma.length; i++)
                        if (smax < sigma[i]) smax = sigma[i];
                     smax = 255/smax;
                     for (int i = 0; i < sigma.length; i++)
                        sigma[i] *= smax;
                  }
                  outField.addData(DataArray.create(sigma, vlen, "std. dev " + dataArr.getName()));
               }
               break;
         }
         System.out.println("elapsed time "+(System.currentTimeMillis()-t0)/(1000.f));
         System.out.println("average slice time "+((System.currentTimeMillis()-t0)/(1000.f*(dims[dims.length-1]-2*radius+1))));
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
      byte[]    inBData, outBData;
      short[]   inSData, outSData;
      int[]     inIData, outIData;
      float[]   inFData, outFData;
      double[]  inDData, outDData;
      boolean computeSigma = false;
      
      public FilterArray(int vlen, int nThreads, int iThread,
                         byte[] inData, byte[] outData, boolean computeSigma)
      {
         type = DataArray.FIELD_DATA_BYTE;
         this.nThreads       = nThreads;
         this.iThread        = iThread;
         this.vlen           = vlen;
         this.inBData        = inData;
         this.outBData       = outData;
         this.computeSigma   = computeSigma;
      }

      public FilterArray(int vlen, int nThreads, int iThread,
                         short[] inData, short[] outData, boolean computeSigma)
      {
         type = DataArray.FIELD_DATA_SHORT;
         this.nThreads       = nThreads;
         this.iThread        = iThread;
         this.vlen           = vlen;
         this.inSData        = inData;
         this.outSData       = outData;
         this.computeSigma   = computeSigma;
     }

      public FilterArray(int vlen, int nThreads, int iThread,
                         int[] inData, int[] outData, boolean computeSigma)
      {
         type = DataArray.FIELD_DATA_INT;
         this.nThreads       = nThreads;
         this.iThread        = iThread;
         this.vlen           = vlen;
         this.inIData        = inData;
         this.outIData       = outData;
         this.computeSigma   = computeSigma;
    }

      public FilterArray(int vlen, int nThreads, int iThread,
                         float[] inData, float[] outData, boolean computeSigma)
      {
         type = DataArray.FIELD_DATA_FLOAT;
         this.nThreads       = nThreads;
         this.iThread        = iThread;
         this.vlen           = vlen;
         this.inFData        = inData;
         this.outFData       = outData;
         this.computeSigma   = computeSigma;
     }

      public FilterArray(int vlen, int nThreads, int iThread,
                         double[] inData, double[] outData, boolean computeSigma)
      {
         type = DataArray.FIELD_DATA_DOUBLE;
         this.nThreads       = nThreads;
         this.iThread        = iThread;
         this.vlen           = vlen;
         this.inDData        = inData;
         this.outDData       = outData;
         this.computeSigma   = computeSigma;
     }

     public void run()
     {
         int rr, kl, ku, jl, ju, il, iu;
         float r, s = 0;
         float w = 0, sw = 0;
         int dk, kstart, kend;
         for (int v = 0; v < vlen; v++)
            switch (dims.length)
            {
            case 3:
               dk = dims[2]/nThreads;
               kstart = iThread       * dk + Math.min(iThread,     dims[2] % nThreads);
               kend   = (iThread + 1) * dk + Math.min(iThread + 1, dims[2] % nThreads);
               for (int k0 = kstart ; k0 < kend; k0++)
               {
                  if (iThread == 0)
                     fireStatusChanged(k0/(kend - 1.f));
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
                        r = sw = s = 0.f;
                        switch (type)
                        {
                   case DataArray.FIELD_DATA_BYTE:
                           for (int k = kl; k <= ku; k++)
                              for (int j = jl; j <= ju; j++)
                                 for (int i = il, n = ((k0+k)*dims[1]+j+j0)*dims[0]+i0+il, p = k*k+j*j; 
                                          i <= iu; 
                                          i++, n++)
                                 {
                                    rr = inBData[n*vlen+v];
                                    if (rr < 0) rr += 256;
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
                                    r += rr*w;
                                    s += rr*rr*w;
                                    sw += w;
                                 }
                           if (computeSigma)
                              sigma[m] = (float)Math.sqrt(s/sw - (r*r)/(sw*sw));
                           rr = (int)(r/sw);
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
                                          i++, n++)
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
                                    r += inSData[n*vlen+v]*w;
                                    s += inSData[n*vlen+v]*inSData[n*vlen+v]*w;
                                    sw += w;
                                 }
                           if (computeSigma)
                              sigma[m] = (float)Math.sqrt(s/sw - (r*r)/(sw*sw));
                           outSData[m] = (short)(r/sw);
                           break;
                   case DataArray.FIELD_DATA_INT:
                           for (int k = kl; k <= ku; k++)
                              for (int j = jl; j <= ju; j++)
                                 for (int i = il, n = ((k0+k)*dims[1]+j+j0)*dims[0]+i0+il, p = k*k+j*j; 
                                          i <= iu; 
                                          i++, n++)
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
                                    r += inIData[n*vlen+v]*w;
                                    s += inIData[n*vlen+v]*inIData[n*vlen+v]*w;
                                    sw += w;
                                 }
                           if (computeSigma)
                              sigma[m] = (float)Math.sqrt(s/sw - (r*r)/(sw*sw));
                           outIData[m] = (int)(r/sw);
                           break;
                   case DataArray.FIELD_DATA_FLOAT:
                          for (int k = kl; k <= ku; k++)
                              for (int j = jl; j <= ju; j++)
                                 for (int i = il, n = ((k0+k)*dims[1]+j+j0)*dims[0]+i0+il, p = k*k+j*j; 
                                          i <= iu; 
                                          i++, n++)
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
                                    r += inFData[n*vlen+v]*w;
                                    s += inFData[n*vlen+v]*inFData[n*vlen+v]*w;
                                    sw += w;
                                 }
                           if (computeSigma)
                              sigma[m] = (float)Math.sqrt(s/sw - (r*r)/(sw*sw));
                           outFData[m] = r/sw;
                           break;
                   case DataArray.FIELD_DATA_DOUBLE:
                          for (int k = kl; k <= ku; k++)
                              for (int j = jl; j <= ju; j++)
                                 for (int i = il, n = ((k0+k)*dims[1]+j+j0)*dims[0]+i0+il, p = k*k+j*j; 
                                          i <= iu; 
                                          i++, n++)
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
                                    r += inDData[n*vlen+v]*w;
                                    s += inDData[n*vlen+v]*inDData[n*vlen+v]*w;
                                    sw += w;
                                 }
                           if (computeSigma)
                              sigma[m] = (float)Math.sqrt(s/sw - (r*r)/(sw*sw));
                           outDData[m] = r/sw;
                           break;
                        }
                     }
                  }
               }
               break;
            case 2:
               dk = dims[1]/nThreads;
               kstart = iThread       * dk + Math.min(iThread,     dims[1] % nThreads);
               kend   = (iThread + 1) * dk + Math.min(iThread + 1, dims[1] % nThreads);
               for (int j0 = kstart; j0 < kend; j0++)
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
                     r = sw = s = 0.f;
                     switch (type)
                     {
                case DataArray.FIELD_DATA_BYTE:
                        for (int j = jl; j <= ju; j++)
                           for (int i = il, n = (j+j0)*dims[0]+i0+il, p = j*j; 
                                    i <= iu; 
                                    i++, n++)
                           {
                              rr = inBData[n*vlen+v];
                              if (rr < 0) rr += 256;
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
                              r += rr*w;
                              s += rr*rr*w;
                              sw += w;
                           }
                        if (computeSigma)
                              sigma[m] = (float)Math.sqrt(s/sw - (r*r)/(sw*sw));
                        rr = (int)(r/sw);
                        if (rr>127)
                           outBData[m] = (byte)(rr-256);
                        else
                           outBData[m] = (byte)rr;
                        break;
                case DataArray.FIELD_DATA_SHORT:
                        for (int j = jl; j <= ju; j++)
                           for (int i = il, n = (j+j0)*dims[0]+i0+il, p = j*j; 
                                    i <= iu; 
                                    i++, n++)
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
                              s += inSData[n*vlen+v]*inSData[n*vlen+v]*w;
                              r += inSData[n*vlen+v]*w;
                              sw += w;
                           }
                        if (computeSigma)
                              sigma[m] = (float)Math.sqrt(s/sw - (r*r)/(sw*sw));
                        outSData[m] = (short)(r/sw);
                        break;
               case DataArray.FIELD_DATA_INT:
                        for (int j = jl; j <= ju; j++)
                           for (int i = il, n = (j+j0)*dims[0]+i0+il, p = j*j; 
                                    i <= iu; 
                                    i++, n++)
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
                              s += inSData[n*vlen+v]*inSData[n*vlen+v]*w;
                              r += inSData[n*vlen+v]*w;
                              sw += w;
                           }
                        if (computeSigma)
                              sigma[m] = (float)Math.sqrt(s/sw - (r*r)/(sw*sw));
                        outIData[m] = (int)(r/sw);
                        break;
               case DataArray.FIELD_DATA_FLOAT:
                        for (int j = jl; j <= ju; j++)
                           for (int i = il, n = (j+j0)*dims[0]+i0+il, p = j*j; 
                                    i <= iu; 
                                    i++, n++)
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
                              s += inSData[n*vlen+v]*inSData[n*vlen+v]*w;
                              r += inSData[n*vlen+v]*w;
                              sw += w;
                           }
                        if (computeSigma)
                              sigma[m] = (float)Math.sqrt(s/sw - (r*r)/(sw*sw));
                        outFData[m] = r/sw;
                        break;
               case DataArray.FIELD_DATA_DOUBLE:
                        for (int j = jl; j <= ju; j++)
                           for (int i = il, n = (j+j0)*dims[0]+i0+il, p = j*j; 
                                    i <= iu; 
                                    i++, n++)
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
                              s += inSData[n*vlen+v]*inSData[n*vlen+v]*w;
                              r += inSData[n*vlen+v]*w;
                              sw += w;
                           }
                        if (computeSigma)
                              sigma[m] = (float)Math.sqrt(s/sw - (r*r)/(sw*sw));
                        outDData[m] = r/sw;
                        break;
                     }
                  }
            }
            break;
         case 1:
            dk = dims[1]/nThreads;
            kstart = iThread       * dk + Math.min(iThread,     dims[0] % nThreads);
            kend   = (iThread + 1) * dk + Math.min(iThread + 1, dims[0] % nThreads);
            for (int i0 = kstart; i0 < kend; i0++)
            {
               int m = i0;
               il = i0 - radius; if (il<0) il = 0;
               iu = i0 + radius;  if (i0>=dims[0]) iu = dims[0]-1;
               if (iThread == 0)
                  fireStatusChanged((float)i0/dims[0]);
               r = sw = s = 0.f;
               switch (type)
               {
          case DataArray.FIELD_DATA_BYTE:
                  for (int i = il; i <= iu; i++)
                  {
                     rr = inBData [i*vlen + v];
                     if (rr < 0) rr += 256;
                     w = 0;
                     if (anisotropy!=null)
                        w = anisotropy[i]-anisotropy[i0];
                     w = (float)(Math.exp(w*w/slope1-(i-i0)*(i-i0)/slope));
                     r += rr*w;
                     s += rr*rr*w;
                     sw += w;
                  }
                  if (computeSigma)
                              sigma[m] = (float)Math.sqrt(s/sw - (r*r)/(sw*sw));
                  rr = (int)(r/sw);
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
                     s += inSData[i*vlen+v]*inSData[i*vlen+v]*w;
                     r += inSData [i*vlen + v]*w;
                     sw += w;
                  }
                  if (computeSigma)
                              sigma[m] = (float)Math.sqrt(s/sw - (r*r)/(sw*sw));
                  outSData[m] = (short)(r/sw);
                  break;
          case DataArray.FIELD_DATA_INT:
                  for (int i = il; i <= iu; i++)
                  {
                     w = 0;
                     if (anisotropy!=null)
                        w = anisotropy[i]-anisotropy[i0];
                     w = (float)(Math.exp(w*w/slope1-(i-i0)*(i-i0)/slope));
                     s += inSData[i*vlen+v]*inSData[i*vlen+v]*w;
                     r += inSData [i*vlen + v]*w;
                     sw += w;
                  }
                  if (computeSigma)
                              sigma[m] = (float)Math.sqrt(s/sw - (r*r)/(sw*sw));
                  outIData[m] = (int)(r/sw);
                  break;
          case DataArray.FIELD_DATA_FLOAT:
                  for (int i = il; i <= iu; i++)
                  {
                     w = 0;
                     if (anisotropy!=null)
                        w = anisotropy[i]-anisotropy[i0];
                     w = (float)(Math.exp(w*w/slope1-(i-i0)*(i-i0)/slope));
                     s += inSData[i*vlen+v]*inSData[i*vlen+v]*w;
                     r += inSData [i*vlen + v]*w;
                     sw += w;
                  }
                  if (computeSigma)
                              sigma[m] = (float)Math.sqrt(s/sw - (r*r)/(sw*sw));
                  outFData[m] = r/sw;
                  break;
          case DataArray.FIELD_DATA_DOUBLE:
                  for (int i = il; i <= iu; i++)
                  {
                     w = 0;
                     if (anisotropy!=null)
                        w = anisotropy[i]-anisotropy[i0];
                     w = (float)(Math.exp(w*w/slope1-(i-i0)*(i-i0)/slope));
                     s += inSData[i*vlen+v]*inSData[i*vlen+v]*w;
                     r += inSData [i*vlen + v]*w;
                     sw += w;
                  }
                  if (computeSigma)
                              sigma[m] = (float)Math.sqrt(s/sw - (r*r)/(sw*sw));
                  outDData[m] = r/sw;
                  break;
               }
            }
            break;
         }
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
