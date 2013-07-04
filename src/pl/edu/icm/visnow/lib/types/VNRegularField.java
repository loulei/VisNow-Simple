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


import pl.edu.icm.visnow.datasets.RegularField;

/**
 * @author  Krzysztof S. Nowinski (know@icm.edu.pl)
 * Warsaw University, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class VNRegularField extends VNField
{

    //private RegularField field = null;
    private boolean computed = false;
    private float[][] valueHistograms = null;
    private float[][] derivHistograms = null;
    private float[][] thrHistograms = null;
    private float[] minVal = null;
    private float[] avgVal = null;
    private float[] maxVal = null;
    private float[] avgGrad = null;
    private float[] stdDevVal = null;
    private float[] stdDevGrad = null;

    /**
     * Creates a new instance of VNRegularField
     */
    public VNRegularField()
    {
    }

    public VNRegularField(RegularField inField)
    {
       field = inField;
       if (field != null)
          field.checkPureDim();
    }

    @Override
    public RegularField getField()
    {
        return (RegularField)field;
    }

    /**
    *
    */
   @Override
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             public void createStats()
    {
        if (field == null || field.getNData() < 1 || computed)
            return;
        avgVal = new float[field.getNData()];
        minVal = new float[field.getNData()];
        maxVal = new float[field.getNData()];
        avgGrad = new float[field.getNData()];
        stdDevVal = new float[field.getNData()];
        stdDevGrad = new float[field.getNData()];
        valueHistograms = new float[field.getNData()][256];
        derivHistograms = new float[field.getNData()][256];
        thrHistograms = new float[field.getNData()][256];
        int[] count = new int[256];
        for (int n = 0; n < field.getNData(); n++)
        {
            float max = maxVal[n] = field.getData(n).getMaxv();
            float min = minVal[n] = field.getData(n).getMinv();
            float a = 0, a2 = 0;
            float da = 0, da2 = 0;
            float r = 1;
            int h, h0, h1, h2, m;

            for (int i = 0; i < 256; i++)
            {
                thrHistograms[n][i] = valueHistograms[n][i] = derivHistograms[n][i] = 0;
                count[i] = 1;
            }
            m = 1;
            if (min>=max-.001f)
            {
               float med = .5f*(min+max);
               min = med-.0005f;
               max = med+.0005f;
            }
            float d = 255 / (max - min);
            int[] dims = ((RegularField)field).getDims();
            if (dims.length == 3 && dims[0] > 1 && dims[1] > 1 && dims[2] > 1)
            {
                int s1 = dims[0], s2 = dims[0] * dims[1];
                for (int k = 0; k < dims[2] - 1; k++)
                {
                    for (int j = 0; j < dims[1] - 1; j++)
                    {
                        float[] v0 = field.getData(n).get1DSlice(k * s2 + j * s1, dims[0], 1);
                        float[] v1 = field.getData(n).get1DSlice(k * s2 + (j + 1) * s1, dims[0], 1);
                        float[] v2 = field.getData(n).get1DSlice((k + 1) * s2 + j * s1, dims[0], 1);
                        for (int i = 0; i < dims[0] - 1; i++)
                        {
                            a += v0[i];
                            a2 += v0[i] * v0[i];
                            h0 = (int) ((v0[i] - min) * d);
                            if (h0 < 0)    h0 = 0;
                            if (h0 > 255)  h0 = 255;
                            valueHistograms[n][h0] += 1;

                            float d0 = v0[i + 1] - v0[i];
                            if (d0 < 0)  d0 = -d0;
                            da += d0;
                            da2 += d0 * d0;
                            h = (int) (d0 * d);
                            if (h < 0)    h = 0;
                            if (h > 255)  h = 255;
                               derivHistograms[n][h] += 1;
                            h = (int) ((v0[i + 1] - min) * d);
                            if (h < 0)    h = 0;
                            if (h > 255)  h = 255;
                            if (h > h0)     { h1 = h0; h2 = h; } 
                            else            { h1 = h;  h2 = h0;}
                            if (d0 < .001f) r = 1000000.f;
                            else            r = 1 / (d0 * d0);
                            for (int l = h1; l < h2; l++)
                            {
                                thrHistograms[n][l] += r;
                                count[l] += 1;
                            }
                            
                            float d1 = v1[i] - v0[i];
                            if (d1 < 0)  d1 = -d1;
                            da += d1;
                            da2 += d1 * d1;
                            h = (int) (d1 * d);
                            if (h < 0)    h = 0;
                            if (h > 255)  h = 255;
                            derivHistograms[n][h] += 1;
                            h = (int) ((v1[i] - min) * d);
                            if (h < 0)    h = 0;
                            if (h > 255)  h = 255;
                            if (h > h0)     { h1 = h0; h2 = h; } 
                            else            { h1 = h;  h2 = h0;}
                            if (d1 < .001f) r = 1000000.f;
                            else            r = 1 / (d1 * d1);
                            for (int l = h1; l < h2; l++)
                            {
                                thrHistograms[n][l] += r;
                                count[l] += 1;
                            }
                            
                            float d2 = v2[i] - v0[i];
                            if (d2 < 0)  d2 = -d2;
                            da += d2;
                            da2 += d2 * d2;
                            h = (int) (d2 * d);
                            if (h < 0)    h = 0;
                            if (h > 255)  h = 255;
                            derivHistograms[n][h] += 1;
                            h = (int) ((v2[i] - min) * d);
                            if (h < 0)    h = 0;
                            if (h > 255)  h = 255;
                            if (h > h0)     { h1 = h0; h2 = h; } 
                            else            { h1 = h;  h2 = h0;}
                            if (d2 < .001f) r = 1000000.f;
                            else            r = 1 / (d2 * d2);
                            for (int l = h1; l < h2; l++)
                            {
                                thrHistograms[n][l] += r;
                                count[l] += 1;
                            }
                        }
                    }
                }
                m = (dims[2] - 1) * (dims[1] - 1) * (dims[0] - 1);
            } else if (dims.length == 2 && dims[0] > 1 && dims[1] > 1)
            {
                int s1 = dims[0];
                for (int j = 0; j < dims[1] - 1; j++)
                {
                    float[] v0 = field.getData(n).get1DSlice(j * s1, dims[0], 1);
                    float[] v1 = field.getData(n).get1DSlice((j + 1) * s1, dims[0], 1);
                    for (int i = 0; i < dims[0] - 1; i++)
                    {
                        a += v0[i];
                        a2 += v0[i] * v0[i];
                        h0 = (int) ((v0[i] - min) * d);
                        if (h0 <= 0 || h0 >= 255)
                            continue;
                        valueHistograms[n][h0] += 1;

                        float d0 = v0[i + 1] - v0[i];
                        if (d0 < 0)   d0 = -d0;
                        da += d0;
                        da2 += d0 * d0;
                        h = (int) (d0 * d);
                        if (h < 0)    h = 0;
                        if (h > 255)  h = 255;
                        derivHistograms[n][h] += 1;
                        h = (int) ((v0[i + 1] - min) * d);
                        if (h < 0)    h = 0;
                        if (h > 255)  h = 255;
                        if (h > h0)     { h1 = h0; h2 = h; } 
                        else            { h1 = h;  h2 = h0;}
                        if (d0 < .001f) r = 1000000.f;
                        else            r = 1 / (d0 * d0);
                        for (int l = h1; l < h2; l++)
                        {
                            thrHistograms[n][l] += r;
                            count[l] += 1;
                        }

                        float d1 = v1[i] - v0[i];
                        if (d1 < 0) d1 = -d1;
                        da += d1;
                        da2 += d1 * d1;
                        h = (int) (d1 * d);
                        if (h < 0)    h = 0;
                        if (h > 255)  h = 255;
                        derivHistograms[n][h] += 1;
                        h = (int) ((v1[i] - min) * d);
                        if (h < 0)    h = 0;
                        if (h > 255)  h = 255;
                        if (h > h0)     { h1 = h0; h2 = h; } 
                        else            { h1 = h;  h2 = h0;}
                        if (d1 < .001f) r = 1000000.f;
                        else            r = 1 / (d1 * d1);
                        for (int l = h1; l < h2; l++)
                        {
                            thrHistograms[n][l] += r;
                            count[l] += 1;
                        }
                    }
                }
                m = (dims[1] - 1) * (dims[0] - 1);
            } else if (dims.length == 1 && dims[0] > 1)
            {
                //float[] v0 = field.getData(n).get1DSlice(0, dims[0], 1);
                float[] v0 = field.getData(n).getFData();
                for (int i = 0; i < dims[0] - 1; i++)
                {
                    a += v0[i];
                    a2 += v0[i] * v0[i];
                    h0 = (int) ((v0[i] - min) * d);
                    if (h0 <= 0 || h0 >= 255)
                        continue;
                    valueHistograms[n][h0] += 1;

                    float d0 = v0[i + 1] - v0[i];
                    if (d0 < 0)   d0 = -d0;
                    da += d0;
                    da2 += d0 * d0;
                    h = (int) (d0 * d);
                    if (h < 0)    h = 0;
                    if (h > 255)  h = 255;
                    derivHistograms[n][h] += 1;
                    h = (int) ((v0[i + 1] - min) * d);
                    if (h < 0)    h = 0;
                    if (h > 255)  h = 255;
                    if (h > h0)     { h1 = h0; h2 = h; } 
                    else            { h1 = h;  h2 = h0;}
                    if (d0 < .001f) r = 1000000.f;
                    else            r = 1 / (d0 * d0);
                    for (int l = h1; l < h2; l++)
                    {
                        thrHistograms[n][l] += r;
                        count[l] += 1;
                    }

                }
                m = (dims[0] - 1);
            }
            
            
            thrHistograms[n][0] = thrHistograms[n][255] = 0;
            valueHistograms[n][0] = valueHistograms[n][255] = 0;
            for (int i = 1; i < 255; i++)
               thrHistograms[n][i] /= count[i];
//            for (int i = 1; i < 255; i++)
//                thrHistograms[n][i] = (float)Math.sqrt(Math.sqrt(thrHistograms[n][i]/count[i]));
            for (int i = 1; i < 255; i++)
               thrHistograms[n][i] = (thrHistograms[n][i-1]+2*thrHistograms[n][i]+thrHistograms[n][i+1])/4;
            avgVal[n] = a / m;
            stdDevVal[n] = (float) (Math.sqrt(a2 / m - avgVal[n] * avgVal[n]));
            m *= 2;
            avgGrad[n] = da / m;
            stdDevGrad[n] = (float) (Math.sqrt(da2 / m - avgGrad[n] * avgGrad[n]));
        }
        computed = true;
    }

    public float[] getAvgGrad()
    {
        if (!computed)
        {
            createStats();
        }
        return avgGrad;
    }

    @Override
    public float[] getAvgVal()
    {
        if (!computed)
        {
            createStats();
        }
        return avgVal;
    }

    public float[][] getDerivHistograms()
    {
        if (!computed)
        {
            createStats();
        }
        return derivHistograms;
    }

    public float[][] getThrHistograms()
    {
        if (!computed)
        {
            createStats();
        }
        return thrHistograms;
    }

    public float[] getStdDevGrad()
    {
        if (!computed)
        {
            createStats();
        }
        return stdDevGrad;
    }

    @Override
    public float[] getStdDevVal()
    {
        if (!computed)
        {
            createStats();
        }
        return stdDevVal;
    }

    @Override
    public float[][] getValueHistograms()
    {
        if (!computed)
        {
            createStats();
        }
        return valueHistograms;
    }

    @Override
    public float[] getMaxVal()
    {
        if (!computed)
        {
            createStats();
        }
        return maxVal;
    }

    @Override
    public float[] getMinVal()
    {
        if (!computed)
        {
            createStats();
        }
        return minVal;
    }
}
