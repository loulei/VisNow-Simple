//<editor-fold defaultstate="collapsed" desc=" COPYRIGHT AND LICENSE ">
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

package pl.edu.icm.visnow.geometries.utils;

import static java.lang.Math.max;
import static java.lang.Math.min;
import javax.vecmath.Color3f;
import pl.edu.icm.visnow.datamaps.ColorMapManager;
import pl.edu.icm.visnow.datasets.DataContainer;
import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.geometries.parameters.ColorComponentParams;
import pl.edu.icm.visnow.geometries.parameters.DataMappingParams;
import pl.edu.icm.visnow.geometries.parameters.TransparencyParams;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class ColorMapper
{

   private static void hsvtorgb(byte[] colors)
   {
      float hue, sat, val, f, p, q, t;
      int m;
      for (int i = 0; i < colors.length; i += 4)
      {
         hue = (0xff & colors[i]) / 42.6f;
         sat = (0xff & colors[i + 1]) / 255f;
         val = (0xff & colors[i + 2]) / 255f;
         m = (int) (Math.floor(hue));
         f = hue - m;
         p = val * (1 - sat);
         q = val * (1 - (sat * f));
         t = val * (1 - (sat * (1 - f)));
         switch (m)
         {
            case 0:
               colors[i] = (byte) (0xff & (int) (255 * val));
               colors[i + 1] = (byte) (0xff & (int) (255 * t));
               colors[i + 2] = (byte) (0xff & (int) (255 * p));
               break;
            case 1:
               colors[i] = (byte) (0xff & (int) (255 * q));
               colors[i + 1] = (byte) (0xff & (int) (255 * val));
               colors[i + 2] = (byte) (0xff & (int) (255 * p));
               break;
            case 2:
               colors[i] = (byte) (0xff & (int) (255 * p));
               colors[i + 1] = (byte) (0xff & (int) (255 * val));
               colors[i + 2] = (byte) (0xff & (int) (255 * t));
               break;
            case 3:
               colors[i] = (byte) (0xff & (int) (255 * p));
               colors[i + 1] = (byte) (0xff & (int) (255 * q));
               colors[i + 2] = (byte) (0xff & (int) (255 * val));
               break;
            case 4:
               colors[i] = (byte) (0xff & (int) (255 * t));
               colors[i + 1] = (byte) (0xff & (int) (255 * p));
               colors[i + 2] = (byte) (0xff & (int) (255 * val));
               break;
            case 5:
               colors[i] = (byte) (0xff & (int) (255 * val));
               colors[i + 1] = (byte) (0xff & (int) (255 * p));
               colors[i + 2] = (byte) (0xff & (int) (255 * q));
               break;
         }
      }
   }

   public static byte[] mapColorsIndexed(DataContainer data, DataMappingParams dataMappingParams,
           int[] indices, Color3f col, byte[] colors)
   {
      int nInd = indices.length;
      if (colors == null || colors.length != 4 * nInd)
         colors = new byte[4 * nInd];
      switch (dataMappingParams.getColorMode())
      {
         case DataMappingParams.COLORMAPPED:
            ColorComponentParams cMapParams = dataMappingParams.getColorMap0Params();
            ColorComponentParams modMapParams = null;
            if (dataMappingParams.getColorMapModification() == DataMappingParams.SAT_MAP_MODIFICATION)
               modMapParams = dataMappingParams.getSatParams();
            else if (dataMappingParams.getColorMapModification() == DataMappingParams.VAL_MAP_MODIFICATION)
               modMapParams = dataMappingParams.getValParams();
            boolean wrapCMap = cMapParams.isWrap();
            DataArray colData = data.getData(cMapParams.getDataComponent());
            if (colData == null || !colData.isSimpleNumeric())
            {
               byte[] bc = new byte[]
               {
                  (byte) (0xff & col.get().getRed()),
                  (byte) (0xff & col.get().getGreen()),
                  (byte) (0xff & col.get().getBlue()),
                  (byte) 100
               };
               for (int i = 0; i < colors.length;)
                  for (int j = 0; j < bc.length; j++, i++)
                     colors[i] = bc[j];
               return colors;
            }
            if (colData.getUserData(0).equalsIgnoreCase("colors")
                    && colData.getType() == DataArray.FIELD_DATA_BYTE
                    && colData.getVeclen() >= 3)
            {
               byte[] cData = colData.getBData();
               int vlen = colData.getVeclen();
               for (int i = 0; i < nInd; i++)
                  for (int j = 0, k = 4 * i, l = vlen * indices[i]; j < vlen; j++, k++, l++)
                     colors[k] = cData[l];
               if (vlen == 3)
                  for (int i = 3; i < colors.length; i += 4)
                     colors[i] = (byte) 255;
               return colors;
            }

            byte[] colorMapLUT = cMapParams.getRGBByteColorTable();
            int nColors = ColorMapManager.SAMPLING_TABLE - 1;
            float low = cMapParams.getDataMin();
            float d = nColors / (cMapParams.getDataMax() - low);
            int cIndex = 0;
            double v;
            int vl = colData.getVeclen();
            switch (colData.getType())
            {
               case DataArray.FIELD_DATA_BYTE:
                  byte[] bData = colData.getBData();
                  for (int i = 0; i < nInd; i++)
                  {
                     if (vl == 1)
                        v = 0xff & bData[indices[i]];
                     else
                     {
                        v = 0;
                        for (int j = 0, k = vl * indices[i]; j < vl; j++, k++)
                           v += (0xff & bData[k]) * (0xff & bData[k]);
                        v = Math.sqrt(v);
                     }
                     if (!wrapCMap)
                        cIndex = Math.max(0, Math.min(3 * nColors, 3 * (int) (d * (v - low))));
                     else
                        cIndex = 3 * (int) (d * (v - low));
                     for (int j = 0, l = 4 * i; j < 3; j++, l++)
                        colors[l] = colorMapLUT[cIndex + j];
                  }
                  break;
               case DataArray.FIELD_DATA_SHORT:
                  short[] sData = colData.getSData();
                  for (int i = 0; i < nInd; i++)
                  {
                     if (vl == 1)
                        v = sData[indices[i]];
                     else
                     {
                        v = 0;
                        for (int j = 0, k = vl * indices[i]; j < vl; j++, k++)
                           v += sData[k] * sData[k];
                        v = Math.sqrt(v);
                     }
                     if (!wrapCMap)
                        cIndex = Math.max(0, Math.min(3 * nColors, 3 * (int) (d * (v - low))));
                     else
                        cIndex = 3 * (((int) (d * (v - low) + 1000 * nColors)) % nColors);
                     for (int j = 0, l = 4 * i; j < 3; j++, l++)
                        colors[l] = colorMapLUT[cIndex + j];
                     for (int j = 0, l = 4 * i; j < 3; j++, l++)
                        colors[l] = colorMapLUT[cIndex + j];
                  }
                  break;
               case DataArray.FIELD_DATA_INT:
                  int[] iData = colData.getIData();
                  for (int i = 0; i < nInd; i++)
                  {
                     if (vl == 1)
                        v = iData[indices[i]];
                     else
                     {
                        v = 0;
                        for (int j = 0, k = vl * indices[i]; j < vl; j++, k++)
                           v += iData[k] * iData[k];
                        v = Math.sqrt(v);
                     }
                     if (!wrapCMap)
                        cIndex = Math.max(0, Math.min(3 * nColors, 3 * (int) (d * (v - low))));
                     else
                        cIndex = 3 * (((int) (d * (v - low) + 1000 * nColors)) % nColors);
                     for (int j = 0, l = 4 * i; j < 3; j++, l++)
                        colors[l] = colorMapLUT[cIndex + j];
                     for (int j = 0, l = 4 * i; j < 3; j++, l++)
                        colors[l] = colorMapLUT[cIndex + j];
                  }
                  break;
               case DataArray.FIELD_DATA_FLOAT:
                  float[] fData = colData.getFData();
                  for (int i = 0; i < nInd; i++)
                  {
                     if (vl == 1)
                        v = fData[indices[i]];
                     else
                     {
                        v = 0;
                        for (int j = 0, k = vl * indices[i]; j < vl; j++, k++)
                           v += fData[k] * fData[k];
                        v = Math.sqrt(v);
                     }
                     if (!wrapCMap)
                        cIndex = Math.max(0, Math.min(3 * nColors, 3 * (int) (d * (v - low))));
                     else
                        cIndex = 3 * (((int) (d * (v - low) + 1000 * nColors)) % nColors);
                     for (int j = 0, l = 4 * i; j < 3; j++, l++)
                        colors[l] = colorMapLUT[cIndex + j];
                     for (int j = 0, l = 4 * i; j < 3; j++, l++)
                        colors[l] = colorMapLUT[cIndex + j];
                  }
                  break;
               case DataArray.FIELD_DATA_DOUBLE:
                  double[] dData = colData.getDData();
                  for (int i = 0; i < nInd; i++)
                  {
                     if (vl == 1)
                        v = dData[indices[i]];
                     else
                     {
                        v = 0;
                        for (int j = 0, k = vl * indices[i]; j < vl; j++, k++)
                           v += dData[k] * dData[k];
                        v = Math.sqrt(v);
                     }
                     if (!wrapCMap)
                        cIndex = Math.max(0, Math.min(3 * nColors, 3 * (int) (d * (v - low))));
                     else
                        cIndex = 3 * (((int) (d * (v - low) + 1000 * nColors)) % nColors);
                     for (int j = 0, l = 4 * i; j < 3; j++, l++)
                        colors[l] = colorMapLUT[cIndex + j];
                     for (int j = 0, l = 4 * i; j < 3; j++, l++)
                        colors[l] = colorMapLUT[cIndex + j];
                  }
                  break;
            }
            for (int i = 3; i < colors.length; i += 4)
               colors[i] = (byte) 255;

            if (dataMappingParams.getColorMapModification() == DataMappingParams.SAT_MAP_MODIFICATION
                    || dataMappingParams.getColorMapModification() == DataMappingParams.VAL_MAP_MODIFICATION)
            {
               boolean modSat = (dataMappingParams.getColorMapModification() == DataMappingParams.SAT_MAP_MODIFICATION);
               colData = data.getData(modMapParams.getDataComponent());
               if (colData == null)
                  break;
               vl = colData.getVeclen();
               if (vl == 1)
                  low = modMapParams.getDataMin();
               else
                  low = 0;
               float mlow = modMapParams.getCmpMin();
               float mup  = modSat ? 1 : modMapParams.getCmpMax();
               d = (mup - mlow) / (modMapParams.getDataMax() - low);
               int r, g, b;
               double u;
               switch (colData.getType())
               {
                  case DataArray.FIELD_DATA_BYTE:
                     byte[] bData = colData.getBData();
                     for (int i = 0, l = 0; i < nInd; i++, l += 4)
                     {
                        if (vl == 1)
                           u = mlow + d * ((0xff & bData[indices[i]]) - low);
                        else
                        {
                           u = 0;
                           for (int j = 0, k = vl * indices[i]; j < vl; j++, k++)
                              u += (0xff & bData[k]) * (0xff & bData[k]);
                           u = mlow + d * Math.sqrt(u);
                        }
                        u = max(mlow, min(mup, u));
                        r = 0xff & colors[l];
                        g = 0xff & colors[l + 1];
                        b = 0xff & colors[l + 2];
                        for (int j = 0; j < 3; j++)
                           if (modSat)
                           {
                              int m = r;
                              if (g > m)
                                 m = g;
                              if (b > m)
                                 m = b;
                              colors[l]     = (byte) (0xff & (int) (m - u * (m - r)));
                              colors[l + 1] = (byte) (0xff & (int) (m - u * (m - g)));
                              colors[l + 2] = (byte) (0xff & (int) (m - u * (m - b)));
                           } else if (u <= 1)
                           {
                              colors[l]     = (byte) (0xff & (int) (u * r));
                              colors[l + 1] = (byte) (0xff & (int) (u * g));
                              colors[l + 2] = (byte) (0xff & (int) (u * b));
                           } else
                           {
                              double w = 2 - u;
                              colors[l]     = (byte) (0xff & (int) (255 - u * (255 - r)));
                              colors[l + 1] = (byte) (0xff & (int) (255 - u * (255 - g)));
                              colors[l + 2] = (byte) (0xff & (int) (255 - u * (255 - b)));
                           }
                     }
                     break;
                  case DataArray.FIELD_DATA_SHORT:
                     short[] sData = colData.getSData();
                     for (int i = 0, l = 0; i < nInd; i++, l += 4)
                     {
                        if (vl == 1)
                           u = mlow + d * (sData[indices[i]] - low);
                        else
                        {
                           u = 0;
                           for (int j = 0, k = vl * indices[i]; j < vl; j++, k++)
                              u += sData[k] * sData[k];
                           u = mlow + d * Math.sqrt(u);
                        }
                        u = max(mlow, min(mup, u));
                        r = 0xff & colors[l];
                        g = 0xff & colors[l + 1];
                        b = 0xff & colors[l + 2];
                        for (int j = 0; j < 3; j++)
                           if (modSat)
                           {
                              int m = r;
                              if (g > m)
                                 m = g;
                              if (b > m)
                                 m = b;
                              colors[l] = (byte) (0xff & (int) (m - u * (m - r)));
                              colors[l + 1] = (byte) (0xff & (int) (m - u * (m - g)));
                              colors[l + 2] = (byte) (0xff & (int) (m - u * (m - b)));
                           } else if (u <= 1)
                           {
                              colors[l]     = (byte) (0xff & (int) (u * r));
                              colors[l + 1] = (byte) (0xff & (int) (u * g));
                              colors[l + 2] = (byte) (0xff & (int) (u * b));
                           } else
                           {
                              double w = 2 - u;
                              colors[l]     = (byte) (0xff & (int) (255 - u * (255 - r)));
                              colors[l + 1] = (byte) (0xff & (int) (255 - u * (255 - g)));
                              colors[l + 2] = (byte) (0xff & (int) (255 - u * (255 - b)));
                           }
                     }
                     break;
                  case DataArray.FIELD_DATA_INT:
                     int[] iData = colData.getIData();
                     for (int i = 0, l = 0; i < nInd; i++, l += 4)
                     {
                        if (vl == 1)
                           u = mlow + d * (iData[indices[i]] - low);
                        else
                        {
                           u = 0;
                           for (int j = 0, k = vl * indices[i]; j < vl; j++, k++)
                              u += iData[k] * iData[k];
                           u = mlow + d * Math.sqrt(u);
                        }
                        u = max(mlow, min(mup, u));
                        r = 0xff & colors[l];
                        g = 0xff & colors[l + 1];
                        b = 0xff & colors[l + 2];
                        for (int j = 0; j < 3; j++)
                           if (modSat)
                           {
                              int m = r;
                              if (g > m)
                                 m = g;
                              if (b > m)
                                 m = b;
                              colors[l] = (byte) (0xff & (int) (m - u * (m - r)));
                              colors[l + 1] = (byte) (0xff & (int) (m - u * (m - g)));
                              colors[l + 2] = (byte) (0xff & (int) (m - u * (m - b)));
                           } else if (u <= 1)
                           {
                              colors[l]     = (byte) (0xff & (int) (u * r));
                              colors[l + 1] = (byte) (0xff & (int) (u * g));
                              colors[l + 2] = (byte) (0xff & (int) (u * b));
                           } else
                           {
                              double w = 2 - u;
                              colors[l]     = (byte) (0xff & (int) (255 - u * (255 - r)));
                              colors[l + 1] = (byte) (0xff & (int) (255 - u * (255 - g)));
                              colors[l + 2] = (byte) (0xff & (int) (255 - u * (255 - b)));
                           }
                     }
                     break;
                  case DataArray.FIELD_DATA_FLOAT:
                     float[] fData = colData.getFData();
                     for (int i = 0, l = 0; i < nInd; i++, l += 4)
                     {
                        if (vl == 1)
                           u = mlow + d * (fData[indices[i]] - low);
                        else
                        {
                           u = 0;
                           for (int j = 0, k = vl * indices[i]; j < vl; j++, k++)
                              u += fData[k] * fData[k];
                           u = mlow + d * Math.sqrt(u);
                        }
                        u = max(mlow, min(mup, u));
                        r = 0xff & colors[l];
                        g = 0xff & colors[l + 1];
                        b = 0xff & colors[l + 2];
                        for (int j = 0; j < 3; j++)
                           if (modSat)
                           {
                              int m = r;
                              if (g > m)
                                 m = g;
                              if (b > m)
                                 m = b;
                              colors[l] = (byte) (0xff & (int) (m - u * (m - r)));
                              colors[l + 1] = (byte) (0xff & (int) (m - u * (m - g)));
                              colors[l + 2] = (byte) (0xff & (int) (m - u * (m - b)));
                           } else if (u <= 1)
                           {
                              colors[l]     = (byte) (0xff & (int) (u * r));
                              colors[l + 1] = (byte) (0xff & (int) (u * g));
                              colors[l + 2] = (byte) (0xff & (int) (u * b));
                           } else
                           {
                              double w = 2 - u;
                              colors[l]     = (byte) (0xff & (int) (255 - u * (255 - r)));
                              colors[l + 1] = (byte) (0xff & (int) (255 - u * (255 - g)));
                              colors[l + 2] = (byte) (0xff & (int) (255 - u * (255 - b)));
                           }
                     }
                     break;
                  case DataArray.FIELD_DATA_DOUBLE:
                     double[] dData = colData.getDData();
                     for (int i = 0, l = 0; i < nInd; i++, l += 4)
                     {
                        if (vl == 1)
                           u = mlow + d * (dData[indices[i]] - low);
                        else
                        {
                           u = 0;
                           for (int j = 0, k = vl * indices[i]; j < vl; j++, k++)
                              u += dData[k] * dData[k];
                           u = mlow + d * Math.sqrt(u);
                        }
                        u = max(mlow, min(mup, u));
                        r = 0xff & colors[l];
                        g = 0xff & colors[l + 1];
                        b = 0xff & colors[l + 2];
                        for (int j = 0; j < 3; j++)
                           if (modSat)
                           {
                              int m = r;
                              if (g > m)
                                 m = g;
                              if (b > m)
                                 m = b;
                              colors[l] = (byte) (0xff & (int) (m - u * (m - r)));
                              colors[l + 1] = (byte) (0xff & (int) (m - u * (m - g)));
                              colors[l + 2] = (byte) (0xff & (int) (m - u * (m - b)));
                           } else if (u <= 1)
                           {
                              colors[l]     = (byte) (0xff & (int) (u * r));
                              colors[l + 1] = (byte) (0xff & (int) (u * g));
                              colors[l + 2] = (byte) (0xff & (int) (u * b));
                           } else
                           {
                              double w = 2 - u;
                              colors[l]     = (byte) (0xff & (int) (255 - u * (255 - r)));
                              colors[l + 1] = (byte) (0xff & (int) (255 - u * (255 - g)));
                              colors[l + 2] = (byte) (0xff & (int) (255 - u * (255 - b)));
                           }
                     }
                     break;
               }
            }
            for (int i = 3; i < colors.length; i += 4)
               colors[i] = (byte) 255;
            if (dataMappingParams.getColorMapModification() == DataMappingParams.BLEND_MAP_MODIFICATION)
            {
               float blendRatio = dataMappingParams.getBlendRatio();
               cMapParams = dataMappingParams.getColorMap1Params();
               colData = data.getData(cMapParams.getDataComponent());
               wrapCMap = cMapParams.isWrap();
               colorMapLUT = cMapParams.getRGBByteColorTable();
               nColors = ColorMapManager.SAMPLING_TABLE - 1;
               low = cMapParams.getDataMin();
               d = nColors / (cMapParams.getDataMax() - low);
               cIndex = 0;
               vl = colData.getVeclen();
               switch (colData.getType())
               {
                  case DataArray.FIELD_DATA_BYTE:
                     byte[] bData = colData.getBData();
                     for (int i = 0; i < nInd; i++)
                     {
                        if (vl == 1)
                           v = 0xff & bData[indices[i]];
                        else
                        {
                           v = 0;
                           for (int j = 0, k = vl * indices[i]; j < vl; j++, k++)
                              v += (0xff & bData[k]) * (0xff & bData[k]);
                           v = Math.sqrt(v);
                        }
                        if (!wrapCMap)
                           cIndex = Math.max(0, Math.min(3 * nColors, 3 * (int) (d * (v - low))));
                        else
                           cIndex = 3 * (((int) (d * (v - low) + 1000 * nColors)) % nColors);
                        for (int j = 0, l = 4 * i; j < 3; j++, l++)
                           colors[l] = (byte) (0xff & (int) ((1 - blendRatio) * (0xff & colors[l])
                                   + blendRatio * (0xff & colorMapLUT[cIndex + j])));
                     }
                     break;
                  case DataArray.FIELD_DATA_SHORT:
                     short[] sData = colData.getSData();
                     for (int i = 0; i < nInd; i++)
                     {
                        if (vl == 1)
                           v = sData[indices[i]];
                        else
                        {
                           v = 0;
                           for (int j = 0, k = vl * indices[i]; j < vl; j++, k++)
                              v += sData[k] * sData[k];
                           v = Math.sqrt(v);
                        }
                        if (!wrapCMap)
                        if (!wrapCMap)
                           cIndex = Math.max(0, Math.min(3 * nColors, 3 * (int) (d * (v - low))));
                        else
                           cIndex = 3 * (((int) (d * (v - low) + 1000 * nColors)) % nColors);
                        for (int j = 0, l = 4 * i; j < 3; j++, l++)
                           colors[l] = (byte) (0xff & (int) ((1 - blendRatio) * (0xff & colors[l]) + 
                                               blendRatio * (0xff & colorMapLUT[cIndex + j])));
                     }
                     break;
                  case DataArray.FIELD_DATA_INT:
                     int[] iData = colData.getIData();
                     for (int i = 0; i < nInd; i++)
                     {
                        if (vl == 1)
                           v = iData[indices[i]];
                        else
                        {
                           v = 0;
                           for (int j = 0, k = vl * indices[i]; j < vl; j++, k++)
                              v += iData[k] * iData[k];
                           v = Math.sqrt(v);
                        }
                        if (!wrapCMap)
                           cIndex = Math.max(0, Math.min(3 * nColors, 3 * (int) (d * (v - low))));
                        else
                           cIndex = 3 * (((int) (d * (v - low) + 1000 * nColors)) % nColors);
                        for (int j = 0, l = 4 * i; j < 3; j++, l++)
                           colors[l] = (byte) (0xff & (int) ((1 - blendRatio) * (0xff & colors[l])
                                   + blendRatio * (0xff & colorMapLUT[cIndex + j])));
                     }
                     break;
                  case DataArray.FIELD_DATA_FLOAT:
                     float[] fData = colData.getFData();
                     for (int i = 0; i < nInd; i++)
                     {
                        if (vl == 1)
                           v = fData[indices[i]];
                        else
                        {
                           v = 0;
                           for (int j = 0, k = vl * indices[i]; j < vl; j++, k++)
                              v += fData[k] * fData[k];
                           v = Math.sqrt(v);
                        }
                        if (!wrapCMap)
                           cIndex = Math.max(0, Math.min(3 * nColors, 3 * (int) (d * (v - low))));
                        else
                           cIndex = 3 * (((int) (d * (v - low) + 1000 * nColors)) % nColors);
                        for (int j = 0, l = 4 * i; j < 3; j++, l++)
                           colors[l] = (byte) (0xff & (int) ((1 - blendRatio) * (0xff & colors[l])
                                   + blendRatio * (0xff & colorMapLUT[cIndex + j])));
                     }
                     break;
                  case DataArray.FIELD_DATA_DOUBLE:
                     double[] dData = colData.getDData();
                     for (int i = 0; i < nInd; i++)
                     {
                        if (vl == 1)
                           v = dData[indices[i]];
                        else
                        {
                           v = 0;
                           for (int j = 0, k = vl * indices[i]; j < vl; j++, k++)
                              v += dData[k] * dData[k];
                           v = Math.sqrt(v);
                        }
                        if (!wrapCMap)
                           cIndex = Math.max(0, Math.min(3 * nColors, 3 * (int) (d * (v - low))));
                        else
                           cIndex = 3 * (((int) (d * (v - low) + 1000 * nColors)) % nColors);
                        for (int j = 0, l = 4 * i; j < 3; j++, l++)
                           colors[l] = (byte) (0xff & (int) ((1 - blendRatio) * (0xff & colors[l])
                                   + blendRatio * (0xff & colorMapLUT[cIndex + j])));
                     }
                     break;
               }
            }
            break;
         case DataMappingParams.RGB:
            float minData = 0,
            maxData = 0,
            minC = 0,
            scale = 1;
            ColorComponentParams[] rgbParams = new ColorComponentParams[]
            {
               dataMappingParams.getRedParams(),
               dataMappingParams.getGreenParams(),
               dataMappingParams.getBlueParams()
            };
            DataArray[] components = new DataArray[3];
            for (int i = 0; i < rgbParams.length; i++)
               components[i] = data.getData(rgbParams[i].getDataComponent());
            if ((components[0] == null || !components[0].isSimpleNumeric())
                    && (components[1] == null || !components[1].isSimpleNumeric())
                    && (components[1] == null || !components[1].isSimpleNumeric()))
            {
               byte[] bc = new byte[]
               {
                  (byte) (0xff & col.get().getRed()),
                  (byte) (0xff & col.get().getGreen()),
                  (byte) (0xff & col.get().getBlue())
               };
               for (int i = 0; i < colors.length;)
                  for (int j = 0; j < bc.length; j++, i++)
                     colors[i] = bc[j];
               for (int i = 3; i < colors.length; i += 4)
                  colors[i] = (byte) 255;
               return colors;
            }
            for (int ncomp = 0; ncomp < 3; ncomp++)
            {
               colData = components[ncomp];
               if (colData == null || !colData.isSimpleNumeric())
               {
                  byte c = (byte) (0xff & (int) (255 * rgbParams[ncomp].getCmpMin()));
                  for (int i = ncomp; i < colors.length; i += 3)
                     colors[i] = c;
                  continue;
               }

               minC = 255 * rgbParams[ncomp].getCmpMin();
               minData = rgbParams[ncomp].getDataMin();
               maxData = rgbParams[ncomp].getDataMax();
               scale = (255 - minC) / (maxData - minData);
               vl = colData.getVeclen();
               switch (colData.getType())
               {
                  case DataArray.FIELD_DATA_BYTE:
                     byte[] bData = colData.getBData();
                     for (int i = 0, l = ncomp; i < nInd; i++, l += 4)
                     {
                        if (vl == 1)
                           v = 0xff & bData[indices[i]];
                        else
                        {
                           v = 0;
                           for (int j = 0, k = vl * indices[i]; j < vl; j++, k++)
                              v += (0xff & bData[k]) * (0xff & bData[k]);
                           v = Math.sqrt(v);
                        }
                        if (v < minData)
                           v = minData;
                        if (v > maxData)
                           v = maxData;
                        colors[l] = (byte) (0xff & (int) (minC + scale * (v - minData)));
                     }
                     break;
                  case DataArray.FIELD_DATA_SHORT:
                     short[] sData = colData.getSData();
                     for (int i = 0, l = ncomp; i < nInd; i++, l += 4)
                     {
                        if (vl == 1)
                           v = sData[indices[i]];
                        else
                        {
                           v = 0;
                           for (int j = 0, k = vl * indices[i]; j < vl; j++, k++)
                              v += sData[k] * sData[k];
                           v = Math.sqrt(v);
                        }
                        if (v < minData)
                           v = minData;
                        if (v > maxData)
                           v = maxData;
                        colors[l] = (byte) (0xff & (int) (minC + scale * (v - minData)));
                     }
                     break;
                  case DataArray.FIELD_DATA_INT:
                     int[] iData = colData.getIData();
                     for (int i = 0, l = ncomp; i < nInd; i++, l += 4)
                     {
                        if (vl == 1)
                           v = iData[indices[i]];
                        else
                        {
                           v = 0;
                           for (int j = 0, k = vl * indices[i]; j < vl; j++, k++)
                              v += iData[k] * iData[k];
                           v = Math.sqrt(v);
                        }
                        if (v < minData)
                           v = minData;
                        if (v > maxData)
                           v = maxData;
                        colors[l] = (byte) (0xff & (int) (minC + scale * (v - minData)));
                     }
                     break;
                  case DataArray.FIELD_DATA_FLOAT:
                     float[] fData = colData.getFData();
                     for (int i = 0, l = ncomp; i < nInd; i++, l += 4)
                     {
                        if (vl == 1)
                           v = fData[indices[i]];
                        else
                        {
                           v = 0;
                           for (int j = 0, k = vl * indices[i]; j < vl; j++, k++)
                              v += fData[k] * fData[k];
                           v = Math.sqrt(v);
                        }
                        if (v < minData)
                           v = minData;
                        if (v > maxData)
                           v = maxData;
                        colors[l] = (byte) (0xff & (int) (minC + scale * (v - minData)));
                     }
                     break;
                  case DataArray.FIELD_DATA_DOUBLE:
                     double[] dData = colData.getDData();
                     for (int i = 0, l = ncomp; i < nInd; i++, l += 4)
                     {
                        if (vl == 1)
                           v = dData[indices[i]];
                        else
                        {
                           v = 0;
                           for (int j = 0, k = vl * indices[i]; j < vl; j++, k++)
                              v += dData[k] * dData[k];
                           v = Math.sqrt(v);
                        }
                        if (v < minData)
                           v = minData;
                        if (v > maxData)
                           v = maxData;
                        colors[l] = (byte) (0xff & (int) (minC + scale * (v - minData)));
                     }
                     break;
               }
            }
            break;
      }
      for (int i = 3; i < colors.length; i += 4)
         colors[i] = (byte) 255;
      if (data instanceof Field && ((Field) data).isMask())
      {
         boolean[] valid = ((Field) data).getMask();
         float[] c = new float[3];
         col.get(c);
         byte[] bc = new byte[4];
         for (int i = 0; i < c.length; i++)
            bc[i] = (byte) (0xff & (int) (255 * c[i]));
         for (int i = 0; i < nInd; i++)
            if (!valid[indices[i]])
            {
               colors[4 * i] = bc[0];
               colors[4 * i + 1] = bc[1];
               colors[4 * i + 2] = bc[2];
               colors[4 * i + 3] = (byte) 127;
            }
      }
      return colors;
   }

   public static byte[] map(DataContainer data, DataMappingParams dataMappingParams, int start, int end, int tStart, Color3f col, byte[] colors)
   {
      if (start < 0 || start >= end || end > data.getNNodes())
         return null;
      if (colors == null || colors.length <  4 * (tStart + end - start))
         colors = new byte[4 * (tStart + end - start)];
      switch (dataMappingParams.getColorMode())
      {
         case DataMappingParams.COLORMAPPED:
            ColorComponentParams cMapParams = dataMappingParams.getColorMap0Params();
            ColorComponentParams modMapParams = null;
            if (dataMappingParams.getColorMapModification() == DataMappingParams.SAT_MAP_MODIFICATION)
               modMapParams = dataMappingParams.getSatParams();
            else if (dataMappingParams.getColorMapModification() == DataMappingParams.VAL_MAP_MODIFICATION)
               modMapParams = dataMappingParams.getValParams();
            else if (dataMappingParams.getColorMapModification() == DataMappingParams.BLEND_MAP_MODIFICATION)
               modMapParams = dataMappingParams.getColorMap1Params();
            boolean wrapCMap = cMapParams.isWrap();
            DataArray colData = data.getData(cMapParams.getDataComponent());
            if (colData == null)
            {
               byte[] bc = new byte[]
               {
                  (byte) (0xff & col.get().getRed()),
                  (byte) (0xff & col.get().getGreen()),
                  (byte) (0xff & col.get().getBlue()),
                  (byte) 255
               };
               for (int i = 4 * tStart; i < 4 * (tStart + end - start);)
                  for (int j = 0; j < bc.length; j++, i++)
                     colors[i] = bc[j];
               return colors;
            }
            if (colData.getUserData(0).equalsIgnoreCase("colors")
                    && colData.getType() == DataArray.FIELD_DATA_BYTE
                    && colData.getVeclen() == 3)
            {
               byte[] cData = colData.getBData();
               for (int i = start, k = 4 * tStart, l = 3 * start; i < end; i++)
               {
                  for (int j = 0; j < 3; j++, k++, l++)
                     colors[k] = cData[l];
                  colors[k] = (byte) 255;
                  k += 1;
               }
               return colors;
            }
            if (colData.getUserData(0).equalsIgnoreCase("colors")
                    && colData.getType() == DataArray.FIELD_DATA_BYTE
                    && colData.getVeclen() == 4)
            {
               byte[] cData = colData.getBData();
               System.arraycopy(cData, 4 * start, colors, 4 * tStart, 4 * (end - start));
               return colors;
            }
            byte[] colorMapLUT = cMapParams.getRGBByteColorTable();
            int nColors = ColorMapManager.SAMPLING_TABLE - 1;
            float low = cMapParams.getDataMin();
            float d = nColors / (cMapParams.getDataMax() - low);
            int cIndex = 0;
            double v;
            int vl = colData.getVeclen();
            switch (colData.getType())
            {
               case DataArray.FIELD_DATA_BYTE:
                  byte[] bData = colData.getBData();
                  if (bData.length < end * vl)
                     break;
                  for (int i = start, it = tStart; i < end; i++, it++)
                  {
                     if (vl == 1)
                        v = 0xff & bData[i];
                     else
                     {
                        v = 0;
                        for (int j = 0, k = vl * i; j < vl; j++, k++)
                           v += (0xff & bData[k]) * (0xff & bData[k]);
                        v = Math.sqrt(v);
                     }
                     if (!wrapCMap)
                        cIndex = Math.max(0, Math.min(3 * nColors, 3 * (int) (d * (v - low))));
                     else
                        cIndex = 3 * (((int) (d * (v - low) + 1000 * nColors)) % nColors);
                     for (int j = 0, l = 4 * it; j < 3; j++, l++)
                        colors[l] = colorMapLUT[cIndex + j];
                  }
                  break;
               case DataArray.FIELD_DATA_SHORT:
                  short[] sData = colData.getSData();
                  if (sData.length < end * vl)
                     break;
                  for (int i = start, it = tStart; i < end; i++, it++)
                  {
                     if (vl == 1)
                        v = sData[i];
                     else
                     {
                        v = 0;
                        for (int j = 0, k = vl * i; j < vl; j++, k++)
                           v += sData[k] * sData[k];
                        v = Math.sqrt(v);
                     }
                     if (!wrapCMap)
                        cIndex = Math.max(0, Math.min(3 * nColors, 3 * (int) (d * (v - low))));
                     else
                        cIndex = 3 * (((int) (d * (v - low) + 1000 * nColors)) % nColors);
                     for (int j = 0, l = 4 * it; j < 3; j++, l++)
                        colors[l] = colorMapLUT[cIndex + j];
                  }
                  break;
               case DataArray.FIELD_DATA_INT:
                  int[] iData = colData.getIData();
                  if (iData.length < end * vl)
                     break;
                  for (int i = start, it = tStart; i < end; i++, it++)
                  {
                     if (vl == 1)
                        v = iData[i];
                     else
                     {
                        v = 0;
                        for (int j = 0, k = vl * i; j < vl; j++, k++)
                           v += iData[k] * iData[k];
                        v = Math.sqrt(v);
                     }
                     if (!wrapCMap)
                        cIndex = Math.max(0, Math.min(3 * nColors, 3 * (int) (d * (v - low))));
                     else
                        cIndex = 3 * (((int) (d * (v - low) + 1000 * nColors)) % nColors);
                     for (int j = 0, l = 4 * it; j < 3; j++, l++)
                        colors[l] = colorMapLUT[cIndex + j];
                  }
                  break;
               case DataArray.FIELD_DATA_FLOAT:
                  float[] fData = colData.getFData();
                  if (fData.length < end * vl)
                     break;
                  for (int i = start, it = tStart; i < end; i++, it++)
                  {
                     if (vl == 1)
                        v = fData[i];
                     else
                     {
                        v = 0;
                        for (int j = 0, k = vl * i; j < vl; j++, k++)
                           v += fData[k] * fData[k];
                        v = Math.sqrt(v);
                     }
                     if (!wrapCMap)
                        cIndex = Math.max(0, Math.min(3 * nColors, 3 * (int) (d * (v - low))));
                     else
                        cIndex = 3 * (((int) (d * (v - low) + 1000 * nColors)) % nColors);
                     for (int j = 0, l = 4 * it; j < 3; j++, l++)
                        colors[l] = colorMapLUT[cIndex + j];
                  }
                  break;
               case DataArray.FIELD_DATA_DOUBLE:
                  double[] dData = colData.getDData();
                  if (dData.length < end * vl)
                     break;
                  for (int i = start, it = tStart; i < end; i++, it++)
                  {
                     if (vl == 1)
                        v = dData[i];
                     else
                     {
                        v = 0;
                        for (int j = 0, k = vl * i; j < vl; j++, k++)
                           v += dData[k] * dData[k];
                        v = Math.sqrt(v);
                     }
                     if (!wrapCMap)
                        cIndex = Math.max(0, Math.min(3 * nColors, 3 * (int) (d * (v - low))));
                     else
                        cIndex = 3 * (((int) (d * (v - low) + 1000 * nColors)) % nColors);
                     for (int j = 0, l = 4 * it; j < 3; j++, l++)
                        colors[l] = colorMapLUT[cIndex + j];
                  }
                  break;

            }
            for (int i = 4 * tStart + 3; i < 4 * (tStart + end - start); i += 4)
               colors[i] = (byte) 255;

            if (dataMappingParams.getColorMapModification() == DataMappingParams.SAT_MAP_MODIFICATION
                    || dataMappingParams.getColorMapModification() == DataMappingParams.VAL_MAP_MODIFICATION)
            {
               boolean modSat = (dataMappingParams.getColorMapModification() == DataMappingParams.SAT_MAP_MODIFICATION);
               DataArray modData = data.getData(modMapParams.getDataComponent());
               if (modData == null)
                  break;
               vl = modData.getVeclen();
               if (vl == 1)
                  low = modMapParams.getDataMin();
               else
                  low = 0;
               float mlow = modMapParams.getCmpMin();
               d = (1 - mlow) / (modMapParams.getDataMax() - low);
               int r, g, b;
               double u;
               switch (modData.getType())
               {
                  case DataArray.FIELD_DATA_BYTE:
                     byte[] bData = modData.getBData();
                     for (int i = start, l = 4 * tStart; i < end; i++, l += 4)
                     {
                        if (vl == 1)
                           u = mlow + d * ((0xff & bData[i]) - low);
                        else
                        {
                           u = 0;
                           for (int j = 0, k = vl * i; j < vl; j++, k++)
                              u += (0xff & bData[k]) * (0xff & bData[k]);
                           u = mlow + d * Math.sqrt(u);
                        }
                        u = max(mlow, min(1, u));
                        r = 0xff & colors[l];
                        g = 0xff & colors[l + 1];
                        b = 0xff & colors[l + 2];
                        if (modSat)
                        {
                           int m = r;
                           if (g > m)
                              m = g;
                           if (b > m)
                              m = b;
                           colors[l] = (byte) (0xff & (int) (m - u * (m - r)));
                           colors[l + 1] = (byte) (0xff & (int) (m - u * (m - g)));
                           colors[l + 2] = (byte) (0xff & (int) (m - u * (m - b)));
                        } else
                        {
                           colors[l] = (byte) (0xff & (int) (u * r));
                           colors[l + 1] = (byte) (0xff & (int) (u * g));
                           colors[l + 2] = (byte) (0xff & (int) (u * b));
                        }
                     }
                     break;
                  case DataArray.FIELD_DATA_SHORT:
                     short[] sData = modData.getSData();
                     for (int i = start, l = 4 * tStart; i < end; i++, l += 4)
                     {
                        if (vl == 1)
                           u = mlow + d * (sData[i] - low);
                        else
                        {
                           u = 0;
                           for (int j = 0, k = vl * i; j < vl; j++, k++)
                              u += sData[k] * sData[k];
                           u = mlow + d * Math.sqrt(u);
                        }
                        u = max(mlow, min(1, u));
                        r = 0xff & colors[l];
                        g = 0xff & colors[l + 1];
                        b = 0xff & colors[l + 2];
                        if (modSat)
                        {
                           int m = r;
                           if (g > m)
                              m = g;
                           if (b > m)
                              m = b;
                           colors[l] = (byte) (0xff & (int) (m - u * (m - r)));
                           colors[l + 1] = (byte) (0xff & (int) (m - u * (m - g)));
                           colors[l + 2] = (byte) (0xff & (int) (m - u * (m - b)));
                        } else
                        {
                           colors[l] = (byte) (0xff & (int) (u * r));
                           colors[l + 1] = (byte) (0xff & (int) (u * g));
                           colors[l + 2] = (byte) (0xff & (int) (u * b));
                        }
                     }
                     break;
                  case DataArray.FIELD_DATA_INT:
                     int[] iData = modData.getIData();
                     for (int i = start, l = 4 * tStart; i < end; i++, l += 4)
                     {
                        if (vl == 1)
                           u = mlow + d * (iData[i] - low);
                        else
                        {
                           u = 0;
                           for (int j = 0, k = vl * i; j < vl; j++, k++)
                              u += iData[k] * iData[k];
                           u = mlow + d * Math.sqrt(u);
                        }
                        u = max(mlow, min(1, u));
                        r = 0xff & colors[l];
                        g = 0xff & colors[l + 1];
                        b = 0xff & colors[l + 2];
                        if (modSat)
                        {
                           int m = r;
                           if (g > m)
                              m = g;
                           if (b > m)
                              m = b;
                           colors[l] = (byte) (0xff & (int) (m - u * (m - r)));
                           colors[l + 1] = (byte) (0xff & (int) (m - u * (m - g)));
                           colors[l + 2] = (byte) (0xff & (int) (m - u * (m - b)));
                        } else
                        {
                           colors[l] = (byte) (0xff & (int) (u * r));
                           colors[l + 1] = (byte) (0xff & (int) (u * g));
                           colors[l + 2] = (byte) (0xff & (int) (u * b));
                        }
                     }
                     break;
                  case DataArray.FIELD_DATA_FLOAT:
                     float[] fData = modData.getFData();
                     for (int i = start, l = 4 * tStart; i < end; i++, l += 4)
                     {
                        if (vl == 1)
                           u = mlow + d * (fData[i] - low);
                        else
                        {
                           u = 0;
                           for (int j = 0, k = vl * i; j < vl; j++, k++)
                              u += fData[k] * fData[k];
                           u = mlow + d * Math.sqrt(u);
                        }
                        u = max(mlow, min(1, u));
                        r = 0xff & colors[l];
                        g = 0xff & colors[l + 1];
                        b = 0xff & colors[l + 2];
                        if (modSat)
                        {
                           int m = r;
                           if (g > m)
                              m = g;
                           if (b > m)
                              m = b;
                           colors[l] = (byte) (0xff & (int) (m - u * (m - r)));
                           colors[l + 1] = (byte) (0xff & (int) (m - u * (m - g)));
                           colors[l + 2] = (byte) (0xff & (int) (m - u * (m - b)));
                        } else
                        {
                           colors[l] = (byte) (0xff & (int) (u * r));
                           colors[l + 1] = (byte) (0xff & (int) (u * g));
                           colors[l + 2] = (byte) (0xff & (int) (u * b));
                        }
                     }
                     break;
                  case DataArray.FIELD_DATA_DOUBLE:
                     double[] dData = modData.getDData();
                     for (int i = start, l = 4 * tStart; i < end; i++, l += 4)
                     {
                        if (vl == 1)
                           u = mlow + d * (dData[i] - low);
                        else
                        {
                           u = 0;
                           for (int j = 0, k = vl * i; j < vl; j++, k++)
                              u += dData[k] * dData[k];
                           u = mlow + d * Math.sqrt(u);
                        }
                        u = max(mlow, min(1, u));
                        r = 0xff & colors[l];
                        g = 0xff & colors[l + 1];
                        b = 0xff & colors[l + 2];
                        if (modSat)
                        {
                           int m = r;
                           if (g > m)
                              m = g;
                           if (b > m)
                              m = b;
                           colors[l] = (byte) (0xff & (int) (m - u * (m - r)));
                           colors[l + 1] = (byte) (0xff & (int) (m - u * (m - g)));
                           colors[l + 2] = (byte) (0xff & (int) (m - u * (m - b)));
                        } else
                        {
                           colors[l] = (byte) (0xff & (int) (u * r));
                           colors[l + 1] = (byte) (0xff & (int) (u * g));
                           colors[l + 2] = (byte) (0xff & (int) (u * b));
                        }
                     }
                     break;
               }
            }
            if (dataMappingParams.getColorMapModification() == DataMappingParams.BLEND_MAP_MODIFICATION)
            {
               float blendRatio = dataMappingParams.getBlendRatio();
               cMapParams = dataMappingParams.getColorMap1Params();
               colData = data.getData(cMapParams.getDataComponent());
               wrapCMap = cMapParams.isWrap();
               colorMapLUT = cMapParams.getRGBByteColorTable();
               nColors = ColorMapManager.SAMPLING_TABLE - 1;
               low = cMapParams.getDataMin();
               d = nColors / (cMapParams.getDataMax() - low);
               cIndex = 0;
               vl = colData.getVeclen();
               switch (colData.getType())
               {
                  case DataArray.FIELD_DATA_BYTE:
                     byte[] bData = colData.getBData();
                     for (int i = start, l = 4 * tStart; i < end; i++, l++) //l is incrementad here to skip alpha entry
                     {
                        if (vl == 1)
                           v = 0xff & bData[i];
                        else
                        {
                           v = 0;
                           for (int j = 0, k = vl * i; j < vl; j++, k++)
                              v += (0xff & bData[k]) * (0xff & bData[k]);
                           v = Math.sqrt(v);
                        }
                        if (!wrapCMap)
                        {
                           cIndex = 3 * (int) (d * (v - low));
                           if (cIndex < 0)
                              cIndex = 0;
                           if (cIndex > 3 * nColors)
                              cIndex = 3 * nColors;
                        } else
                           cIndex = 3 * (((int) (d * (v - low) + 1000 * nColors)) % nColors);
                        for (int j = 0; j < 3; j++, l++)
                           colors[l] = (byte) (0xff & (int) ((1 - blendRatio) * (0xff & colors[l])
                                   + blendRatio * (0xff & colorMapLUT[cIndex + j])));
                     }
                     break;
                  case DataArray.FIELD_DATA_SHORT:
                     short[] sData = colData.getSData();
                     for (int i = start, l = 4 * tStart; i < end; i++, l++)
                     {
                        if (vl == 1)
                           v = sData[i];
                        else
                        {
                           v = 0;
                           for (int j = 0, k = vl * i; j < vl; j++, k++)
                              v += sData[k] * sData[k];
                           v = Math.sqrt(v);
                        }
                        if (!wrapCMap)
                        {
                           cIndex = 3 * (int) (d * (v - low));
                           if (cIndex < 0)
                              cIndex = 0;
                           if (cIndex > 3 * nColors)
                              cIndex = 3 * nColors;
                        } else
                           cIndex = 3 * (((int) (d * (v - low) + 1000 * nColors)) % nColors);
                        for (int j = 0; j < 3; j++, l++)
                           colors[l] = (byte) (0xff & (int) ((1 - blendRatio) * (0xff & colors[l])
                                   + blendRatio * (0xff & colorMapLUT[cIndex + j])));
                     }
                     break;
                  case DataArray.FIELD_DATA_INT:
                     int[] iData = colData.getIData();
                     for (int i = start, l = 4 * tStart; i < end; i++, l++)
                     {
                        if (vl == 1)
                           v = iData[i];
                        else
                        {
                           v = 0;
                           for (int j = 0, k = vl * i; j < vl; j++, k++)
                              v += iData[k] * iData[k];
                           v = Math.sqrt(v);
                        }
                        if (!wrapCMap)
                        {
                           cIndex = 3 * (int) (d * (v - low));
                           if (cIndex < 0)
                              cIndex = 0;
                           if (cIndex > 3 * nColors)
                              cIndex = 3 * nColors;
                        } else
                           cIndex = 3 * (((int) (d * (v - low) + 1000 * nColors)) % nColors);
                        for (int j = 0; j < 3; j++, l++)
                           colors[l] = (byte) (0xff & (int) ((1 - blendRatio) * (0xff & colors[l])
                                   + blendRatio * (0xff & colorMapLUT[cIndex + j])));
                     }
                     break;
                  case DataArray.FIELD_DATA_FLOAT:
                     float[] fData = colData.getFData();
                     for (int i = start, l = 4 * tStart; i < end; i++, l++)
                     {
                        if (vl == 1)
                           v = fData[i];
                        else
                        {
                           v = 0;
                           for (int j = 0, k = vl * i; j < vl; j++, k++)
                              v += fData[k] * fData[k];
                           v = Math.sqrt(v);
                        }
                        if (!wrapCMap)
                        {
                           cIndex = 3 * (int) (d * (v - low));
                           if (cIndex < 0)
                              cIndex = 0;
                           if (cIndex > 3 * nColors)
                              cIndex = 3 * nColors;
                        } else
                           cIndex = 3 * (((int) (d * (v - low) + 1000 * nColors)) % nColors);
                        for (int j = 0; j < 3; j++, l++)
                           colors[l] = (byte) (0xff & (int) ((1 - blendRatio) * (0xff & colors[l])
                                   + blendRatio * (0xff & colorMapLUT[cIndex + j])));
                     }
                     break;
                  case DataArray.FIELD_DATA_DOUBLE:
                     double[] dData = colData.getDData();
                     for (int i = start, l = 4 * tStart; i < end; i++, l++)
                     {
                        if (vl == 1)
                           v = dData[i];
                        else
                        {
                           v = 0;
                           for (int j = 0, k = vl * i; j < vl; j++, k++)
                              v += dData[k] * dData[k];
                           v = Math.sqrt(v);
                        }
                        if (!wrapCMap)
                        {
                           cIndex = 3 * (int) (d * (v - low));
                           if (cIndex < 0)
                              cIndex = 0;
                           if (cIndex > 3 * nColors)
                              cIndex = 3 * nColors;
                        } else
                           cIndex = 3 * (((int) (d * (v - low) + 1000 * nColors)) % nColors);
                        for (int j = 0; j < 3; j++, l++)
                           colors[l] = (byte) (0xff & (int) ((1 - blendRatio) * (0xff & colors[l])
                                   + blendRatio * (0xff & colorMapLUT[cIndex + j])));
                     }
                     break;
               }
            }
            break;
         case DataMappingParams.RGB:
            float minData = 0,
             maxData = 0,
             minC = 0,
             scale = 1;
            ColorComponentParams[] rgbParams = new ColorComponentParams[]
            {
               dataMappingParams.getRedParams(),
               dataMappingParams.getGreenParams(),
               dataMappingParams.getBlueParams()
            };
            DataArray[] components = new DataArray[3];
            for (int i = 0; i < rgbParams.length; i++)
               components[i] = data.getData(rgbParams[i].getDataComponent());
            if ((components[0] == null || !components[0].isSimpleNumeric())
                    && (components[1] == null || !components[1].isSimpleNumeric())
                    && (components[1] == null || !components[1].isSimpleNumeric()))
            {
               byte[] bc = new byte[]
               {
                  (byte) (0xff & col.get().getRed()),
                  (byte) (0xff & col.get().getGreen()),
                  (byte) (0xff & col.get().getBlue())
               };
               for (int i = 0; i < colors.length;)
                  for (int j = 0; j < bc.length; j++, i++)
                     colors[i] = bc[j];
               return colors;
            }
            for (int ncomp = 0; ncomp < 3; ncomp++)
            {
               colData = components[ncomp];
               if (colData == null || !colData.isSimpleNumeric())
               {
                  byte c = (byte) (0xff & (int) (255 * rgbParams[ncomp].getCmpMin()));
                  for (int i = ncomp; i < colors.length; i += 4)
                     colors[i] = c;
                  continue;
               }

               minC = 200 * rgbParams[ncomp].getCmpMin();
               minData = rgbParams[ncomp].getDataMin();
               maxData = rgbParams[ncomp].getDataMax();
               scale = (200 - minC) / (maxData - minData);
               vl = colData.getVeclen();
               switch (colData.getType())
               {
                  case DataArray.FIELD_DATA_BYTE:
                     byte[] bData = colData.getBData();
                     for (int i = start, l = 4 * tStart + ncomp; i < end; i++, l += 4)
                     {
                        if (vl == 1)
                           v = 0xff & bData[i];
                        else
                        {
                           v = 0;
                           for (int j = 0, k = vl * i; j < vl; j++, k++)
                              v += (0xff & bData[k]) * (0xff & bData[k]);
                           v = Math.sqrt(v);
                        }
                        if (v < minData)
                           v = minData;
                        if (v > maxData)
                           v = maxData;
                        colors[l] = (byte) (0xff & (int) (minC + scale * (v - minData)));
                     }
                     break;
                  case DataArray.FIELD_DATA_SHORT:
                     short[] sData = colData.getSData();
                     for (int i = start, l = 4 * tStart + ncomp; i < end; i++, l += 4)
                     {
                        if (vl == 1)
                           v = sData[i];
                        else
                        {
                           v = 0;
                           for (int j = 0, k = vl * i; j < vl; j++, k++)
                              v += sData[k] * sData[k];
                           v = Math.sqrt(v);
                        }
                        if (v < minData)
                           v = minData;
                        if (v > maxData)
                           v = maxData;
                        colors[l] = (byte) (0xff & (int) (minC + scale * (v - minData)));
                     }
                     break;
                  case DataArray.FIELD_DATA_INT:
                     int[] iData = colData.getIData();
                     for (int i = start, l = 4 * tStart + ncomp; i < end; i++, l += 4)
                     {
                        if (vl == 1)
                           v = iData[i];
                        else
                        {
                           v = 0;
                           for (int j = 0, k = vl * i; j < vl; j++, k++)
                              v += iData[k] * iData[k];
                           v = Math.sqrt(v);
                        }
                        if (v < minData)
                           v = minData;
                        if (v > maxData)
                           v = maxData;
                        colors[l] = (byte) (0xff & (int) (minC + scale * (v - minData)));
                     }
                     break;
                  case DataArray.FIELD_DATA_FLOAT:
                     float[] fData = colData.getFData();
                     for (int i = start, l = 4 * tStart + ncomp; i < end; i++, l += 4)
                     {
                        if (vl == 1)
                           v = fData[i];
                        else
                        {
                           v = 0;
                           for (int j = 0, k = vl * i; j < vl; j++, k++)
                              v += fData[k] * fData[k];
                           v = Math.sqrt(v);
                        }
                        if (v < minData)
                           v = minData;
                        if (v > maxData)
                           v = maxData;
                        colors[l] = (byte) (0xff & (int) (minC + scale * (v - minData)));
                     }
                     break;
                  case DataArray.FIELD_DATA_DOUBLE:
                     double[] dData = colData.getDData();
                     for (int i = start, l = 4 * tStart + ncomp; i < end; i++, l += 4)
                     {
                        if (vl == 1)
                           v = dData[i];
                        else
                        {
                           v = 0;
                           for (int j = 0, k = vl * i; j < vl; j++, k++)
                              v += dData[k] * dData[k];
                           v = Math.sqrt(v);
                        }
                        if (v < minData)
                           v = minData;
                        if (v > maxData)
                           v = maxData;
                        colors[l] = (byte) (0xff & (int) (minC + scale * (v - minData)));
                     }
                     break;
               }
            }
            break;
      }
      if (data instanceof Field && ((Field) data).isTransparencyMask())
      {
         boolean[] valid = ((Field) data).getTransparencyMask();
         for (int i = start; i < end; i++)
            if (!valid[i])
               colors[4 * (i - start) + 3] = 0;
      }
      if (data instanceof Field && ((Field) data).isMask())
      {
         boolean[] valid = ((Field) data).getMask();
         float[] c = new float[3];
         col.get(c);
         byte[] bc = new byte[4];
         for (int i = 0; i < c.length; i++)
            bc[i] = (byte) (0xff & (int) (255 * c[i]));
         for (int i = start, l = tStart; i < end; i++, l++)
            if (!valid[i])
            {
               colors[4 * l] = bc[0];
               colors[4 * l + 1] = bc[1];
               colors[4 * l + 2] = bc[2];
               colors[4 * l + 3] = (byte) 127;
            }
      }
      return colors;
   }

   public static int[] map(DataContainer data, DataMappingParams dataMappingParams, 
                           Color3f col, int[] colors, 
                           boolean transpose, int n0, int n1)
   {
      int n = data.getNNodes();
      if (colors == null || colors.length <  n)
         colors = new int[n];
      switch (dataMappingParams.getColorMode())
      {
         case DataMappingParams.COLORMAPPED:
            ColorComponentParams cMapParams = dataMappingParams.getColorMap0Params();
            ColorComponentParams modMapParams = null;
            if (dataMappingParams.getColorMapModification() == DataMappingParams.SAT_MAP_MODIFICATION)
               modMapParams = dataMappingParams.getSatParams();
            else if (dataMappingParams.getColorMapModification() == DataMappingParams.VAL_MAP_MODIFICATION)
               modMapParams = dataMappingParams.getValParams();
            else if (dataMappingParams.getColorMapModification() == DataMappingParams.BLEND_MAP_MODIFICATION)
               modMapParams = dataMappingParams.getColorMap1Params();
            boolean wrapCMap = cMapParams.isWrap();
            DataArray colData = data.getData(cMapParams.getDataComponent());
            if (colData == null)
            {
               for (int i = 0; i < n; i++)
                  colors[i] = 0xff << 24 | 
                              ((0xff & col.get().getRed()) << 16) |
                              ((0xff & col.get().getGreen()) << 8) |
                              (0xff & col.get().getBlue());
               return colors;
            }
            if (colData.getUserData(0).equalsIgnoreCase("colors")
                    && colData.getType() == DataArray.FIELD_DATA_BYTE
                    && colData.getVeclen() == 3)
            {
               byte[] cData = colData.getBData();
               for (int i = 0, l = 0; i < n; i++, l++)
                  colors[l] = 0xff << 24 | 
                              ((0xff & cData[3 * i + 2]) << 16) |
                              ((0xff & cData[3 * i + 1]) << 8) |
                              (0xff & cData[3 * i]);
               return colors;
            }
            if (colData.getUserData(0).equalsIgnoreCase("colors")
                    && colData.getType() == DataArray.FIELD_DATA_BYTE
                    && colData.getVeclen() == 4)
            {
               byte[] cData = colData.getBData();
               for (int i = 0, l = 0; i < n; i++, l++)
                  colors[l] =((0xff & cData[4 * i + 3]) << 24) | 
                             ((0xff & cData[4 * i + 2]) << 16) |
                             ((0xff & cData[4 * i + 1]) << 8) |
                             (0xff & cData[4 * i]);
               return colors;
            }
            byte[] colorMapLUT = cMapParams.getRGBByteColorTable();
            int nColors = ColorMapManager.SAMPLING_TABLE - 1;
            float low = cMapParams.getDataMin();
            float d = nColors / (cMapParams.getDataMax() - low);
            int cIndex = 0;
            double v;
            int vl = colData.getVeclen();
            switch (colData.getType())
            {
               case DataArray.FIELD_DATA_BYTE:
                  byte[] bData = colData.getBData();
                  if (bData.length < n * vl)
                     break;
                  for (int i = 0, it = 0; i < n; i++, it++)
                  {
                     if (vl == 1)
                        v = 0xff & bData[i];
                     else
                     {
                        v = 0;
                        for (int j = 0, k = vl * i; j < vl; j++, k++)
                           v += (0xff & bData[k]) * (0xff & bData[k]);
                        v = Math.sqrt(v);
                     }
                     if (!wrapCMap)
                        cIndex = Math.max(0, Math.min(3 * nColors, 3 * (int) (d * (v - low))));
                     else
                        cIndex = 3 * (((int) (d * (v - low) + 1000 * nColors)) % nColors);
                     colors[it] = (0xff & colorMapLUT[cIndex + 2]) |
                                  ((0xff & colorMapLUT[cIndex + 1]) << 8) |
                                  ((0xff & colorMapLUT[cIndex]) << 16);
                  }
                  break;
               case DataArray.FIELD_DATA_SHORT:
                  short[] sData = colData.getSData();
                  if (sData.length < n * vl)
                     break;
                  for (int i = 0, it = 0; i < n; i++, it++)
                  {
                     if (vl == 1)
                        v = sData[i];
                     else
                     {
                        v = 0;
                        for (int j = 0, k = vl * i; j < vl; j++, k++)
                           v += sData[k] * sData[k];
                        v = Math.sqrt(v);
                     }
                     if (!wrapCMap)
                        cIndex = Math.max(0, Math.min(3 * nColors, 3 * (int) (d * (v - low))));
                     else
                        cIndex = 3 * (((int) (d * (v - low) + 1000 * nColors)) % nColors);
                     colors[it] = (0xff & colorMapLUT[cIndex + 2]) |
                                  ((0xff & colorMapLUT[cIndex + 1]) << 8) |
                                  ((0xff & colorMapLUT[cIndex]) << 16);
                  }
                  break;
               case DataArray.FIELD_DATA_INT:
                  int[] iData = colData.getIData();
                  if (iData.length < n * vl)
                     break;
                  for (int i = 0, it = 0; i < n; i++, it++)
                  {
                     if (vl == 1)
                        v = iData[i];
                     else
                     {
                        v = 0;
                        for (int j = 0, k = vl * i; j < vl; j++, k++)
                           v += iData[k] * iData[k];
                        v = Math.sqrt(v);
                     }
                     if (!wrapCMap)
                        cIndex = Math.max(0, Math.min(3 * nColors, 3 * (int) (d * (v - low))));
                     else
                        cIndex = 3 * (((int) (d * (v - low) + 1000 * nColors)) % nColors);
                     colors[it] = (0xff & colorMapLUT[cIndex + 2]) |
                                  ((0xff & colorMapLUT[cIndex + 1]) << 8) |
                                  ((0xff & colorMapLUT[cIndex]) << 16);
                  }
                  break;
               case DataArray.FIELD_DATA_FLOAT:
                  float[] fData = colData.getFData();
                  if (fData.length < n * vl)
                     break;
                  for (int i = 0, it = 0; i < n; i++, it++)
                  {
                     if (vl == 1)
                        v = fData[i];
                     else
                     {
                        v = 0;
                        for (int j = 0, k = vl * i; j < vl; j++, k++)
                           v += fData[k] * fData[k];
                        v = Math.sqrt(v);
                     }
                     if (!wrapCMap)
                        cIndex = Math.max(0, Math.min(3 * nColors, 3 * (int) (d * (v - low))));
                     else
                        cIndex = 3 * (((int) (d * (v - low) + 1000 * nColors)) % nColors);
                     colors[it] = (0xff & colorMapLUT[cIndex + 2]) |
                                  ((0xff & colorMapLUT[cIndex + 1]) << 8) |
                                  ((0xff & colorMapLUT[cIndex]) << 16);
                  }
                  break;
               case DataArray.FIELD_DATA_DOUBLE:
                  double[] dData = colData.getDData();
                  if (dData.length < n * vl)
                     break;
                  for (int i = 0, it = 0; i < n; i++, it++)
                  {
                     if (vl == 1)
                        v = dData[i];
                     else
                     {
                        v = 0;
                        for (int j = 0, k = vl * i; j < vl; j++, k++)
                           v += dData[k] * dData[k];
                        v = Math.sqrt(v);
                     }
                     if (!wrapCMap)
                        cIndex = Math.max(0, Math.min(3 * nColors, 3 * (int) (d * (v - low))));
                     else
                        cIndex = 3 * (((int) (d * (v - low) + 1000 * nColors)) % nColors);
                     colors[it] = (0xff & colorMapLUT[cIndex + 2]) |
                                  ((0xff & colorMapLUT[cIndex + 1]) << 8) |
                                  ((0xff & colorMapLUT[cIndex]) << 16);
                  }
                  break;
            }
            for (int i = 0; i < n; i++)
               colors[i] |= 0xff << 24;

            if (dataMappingParams.getColorMapModification() == DataMappingParams.SAT_MAP_MODIFICATION
                    || dataMappingParams.getColorMapModification() == DataMappingParams.VAL_MAP_MODIFICATION)
            {
               boolean modSat = (dataMappingParams.getColorMapModification() == DataMappingParams.SAT_MAP_MODIFICATION);
               DataArray modData = data.getData(modMapParams.getDataComponent());
               if (modData == null)
                  break;
               vl = modData.getVeclen();
               if (vl == 1)
                  low = modMapParams.getDataMin();
               else
                  low = 0;
               float mlow = modMapParams.getCmpMin();
               d = (1 - mlow) / (modMapParams.getDataMax() - low);
               int r, g, b;
               double u;
               switch (modData.getType())
               {
                  case DataArray.FIELD_DATA_BYTE:
                     byte[] bData = modData.getBData();
                     for (int i = 0, l = 0; i < n; i++, l++)
                     {
                        if (vl == 1)
                           u = mlow + d * ((0xff & bData[i]) - low);
                        else
                        {
                           u = 0;
                           for (int j = 0, k = vl * i; j < vl; j++, k++)
                              u += (0xff & bData[k]) * (0xff & bData[k]);
                           u = mlow + d * Math.sqrt(u);
                        }
                        u = max(mlow, min(1, u));
                        r = 0xff & (colors[l] >> 16);
                        g = 0xff & (colors[l] >> 9);
                        b = 0xff & colors[l];
                        colors[l] &= 0xff << 24;
                        if (modSat)
                        {
                           int m = r;
                           if (g > m)
                              m = g;
                           if (b > m)
                              m = b;
                           colors[l] |= ((0xff & (int) (m - u * (m - r))) << 16) |
                                        ((0xff & (int) (m - u * (m - g))) << 8) |
                                         (0xff & (int) (m - u * (m - b)));
                        } else
                           colors[l] |= ((0xff & (int) (u * r)) << 16) |
                                        ((0xff & (int) (u * r)) << 8) |
                                         (0xff & (int) (u * r));
                     }
                     break;
                  case DataArray.FIELD_DATA_SHORT:
                     short[] sData = modData.getSData();
                     for (int i = 0, l = 0; i < n; i++, l++)
                     {
                        if (vl == 1)
                           u = mlow + d * (sData[i] - low);
                        else
                        {
                           u = 0;
                           for (int j = 0, k = vl * i; j < vl; j++, k++)
                              u += sData[k] * sData[k];
                           u = mlow + d * Math.sqrt(u);
                        }
                        u = max(mlow, min(1, u));
                        r = 0xff & (colors[l] >> 16);
                        g = 0xff & (colors[l] >> 9);
                        b = 0xff & colors[l];
                        colors[l] &= 0xff << 24;
                        if (modSat)
                        {
                           int m = r;
                           if (g > m)
                              m = g;
                           if (b > m)
                              m = b;
                           colors[l] |= ((0xff & (int) (m - u * (m - r))) << 16) |
                                        ((0xff & (int) (m - u * (m - g))) << 8) |
                                         (0xff & (int) (m - u * (m - b)));
                        } else
                           colors[l] |= ((0xff & (int) (u * r)) << 16) |
                                        ((0xff & (int) (u * r)) << 8) |
                                         (0xff & (int) (u * r));
                     }
                     break;
                  case DataArray.FIELD_DATA_INT:
                     int[] iData = modData.getIData();
                     for (int i = 0, l = 0; i < n; i++, l++)
                     {
                        if (vl == 1)
                           u = mlow + d * (iData[i] - low);
                        else
                        {
                           u = 0;
                           for (int j = 0, k = vl * i; j < vl; j++, k++)
                              u += iData[k] * iData[k];
                           u = mlow + d * Math.sqrt(u);
                        }
                        u = max(mlow, min(1, u));
                        r = 0xff & (colors[l] >> 16);
                        g = 0xff & (colors[l] >> 9);
                        b = 0xff & colors[l];
                        colors[l] &= 0xff << 24;
                        if (modSat)
                        {
                           int m = r;
                           if (g > m)
                              m = g;
                           if (b > m)
                              m = b;
                           colors[l] |= ((0xff & (int) (m - u * (m - r))) << 16) |
                                        ((0xff & (int) (m - u * (m - g))) << 8) |
                                         (0xff & (int) (m - u * (m - b)));
                        } else
                           colors[l] |= ((0xff & (int) (u * r)) << 16) |
                                        ((0xff & (int) (u * r)) << 8) |
                                         (0xff & (int) (u * r));
                     }
                     break;
                  case DataArray.FIELD_DATA_FLOAT:
                     float[] fData = modData.getFData();
                     for (int i = 0, l = 0; i < n; i++, l++)
                     {
                        if (vl == 1)
                           u = mlow + d * (fData[i] - low);
                        else
                        {
                           u = 0;
                           for (int j = 0, k = vl * i; j < vl; j++, k++)
                              u += fData[k] * fData[k];
                           u = mlow + d * Math.sqrt(u);
                        }
                        u = max(mlow, min(1, u));
                        r = 0xff & (colors[l] >> 16);
                        g = 0xff & (colors[l] >> 9);
                        b = 0xff & colors[l];
                        colors[l] &= 0xff << 24;
                        if (modSat)
                        {
                           int m = r;
                           if (g > m)
                              m = g;
                           if (b > m)
                              m = b;
                           colors[l] |= ((0xff & (int) (m - u * (m - r))) << 16) |
                                        ((0xff & (int) (m - u * (m - g))) << 8) |
                                         (0xff & (int) (m - u * (m - b)));
                        } else
                           colors[l] |= ((0xff & (int) (u * r)) << 16) |
                                        ((0xff & (int) (u * r)) << 8) |
                                         (0xff & (int) (u * r));
                     }
                     break;
                  case DataArray.FIELD_DATA_DOUBLE:
                     double[] dData = modData.getDData();
                     for (int i = 0, l = 0; i < n; i++, l++)
                     {
                        if (vl == 1)
                           u = mlow + d * (dData[i] - low);
                        else
                        {
                           u = 0;
                           for (int j = 0, k = vl * i; j < vl; j++, k++)
                              u += dData[k] * dData[k];
                           u = mlow + d * Math.sqrt(u);
                        }
                        u = max(mlow, min(1, u));
                        r = 0xff & (colors[l] >> 16);
                        g = 0xff & (colors[l] >> 9);
                        b = 0xff & colors[l];
                        colors[l] &= 0xff << 24;
                        if (modSat)
                        {
                           int m = r;
                           if (g > m)
                              m = g;
                           if (b > m)
                              m = b;
                           colors[l] |= ((0xff & (int) (m - u * (m - r))) << 16) |
                                        ((0xff & (int) (m - u * (m - g))) << 8) |
                                         (0xff & (int) (m - u * (m - b)));
                        } else
                           colors[l] |= ((0xff & (int) (u * r)) << 16) |
                                        ((0xff & (int) (u * r)) << 8) |
                                         (0xff & (int) (u * r));
                     }
                     break;
               }
            }
            if (dataMappingParams.getColorMapModification() == DataMappingParams.BLEND_MAP_MODIFICATION)
            {
               float blnRatio = dataMappingParams.getBlendRatio();
               cMapParams = dataMappingParams.getColorMap1Params();
               colData = data.getData(cMapParams.getDataComponent());
               if(colData == null)
                   break;
               wrapCMap = cMapParams.isWrap();
               colorMapLUT = cMapParams.getRGBByteColorTable();
               nColors = ColorMapManager.SAMPLING_TABLE - 1;
               low = cMapParams.getDataMin();
               d = nColors / (cMapParams.getDataMax() - low);
               cIndex = 0;
               vl = colData.getVeclen();
               switch (colData.getType())
               {
                  case DataArray.FIELD_DATA_BYTE:
                     byte[] bData = colData.getBData();
                     for (int i = 0, l = 0; i < n; i++, l++) //l is incrementad here to skip alpha entry
                     {
                        if (vl == 1)
                           v = 0xff & bData[i];
                        else
                        {
                           v = 0;
                           for (int j = 0, k = vl * i; j < vl; j++, k++)
                              v += (0xff & bData[k]) * (0xff & bData[k]);
                           v = Math.sqrt(v);
                        }
                        if (!wrapCMap)
                        {
                           cIndex = 3 * (int) (d * (v - low));
                           if (cIndex < 0)
                              cIndex = 0;
                           if (cIndex > 3 * nColors)
                              cIndex = 3 * nColors;
                        } else
                           cIndex = 3 * (((int) (d * (v - low) + 1000 * nColors)) % nColors);
                        int r = 0xff & (colors[l] >> 16);
                        int g = 0xff & (colors[l] >> 9);
                        int b = 0xff & colors[l];
                        colors[l] |= ((int)((1 - blnRatio) * r + blnRatio * (0xff & colorMapLUT[cIndex])) >> 16 )|
                                     ((int)((1 - blnRatio) * g + blnRatio * (0xff & colorMapLUT[cIndex + 1])) >> 8 )|
                                     (int)((1 - blnRatio) * b + blnRatio * (0xff & colorMapLUT[cIndex + 2]));
                     }
                     break;
                  case DataArray.FIELD_DATA_SHORT:
                     short[] sData = colData.getSData();
                     for (int i = 0, l = 0; i < n; i++, l++)
                     {
                        if (vl == 1)
                           v = sData[i];
                        else
                        {
                           v = 0;
                           for (int j = 0, k = vl * i; j < vl; j++, k++)
                              v += sData[k] * sData[k];
                           v = Math.sqrt(v);
                        }
                        if (!wrapCMap)
                        {
                           cIndex = 3 * (int) (d * (v - low));
                           if (cIndex < 0)
                              cIndex = 0;
                           if (cIndex > 3 * nColors)
                              cIndex = 3 * nColors;
                        } else
                           cIndex = 3 * (((int) (d * (v - low) + 1000 * nColors)) % nColors);
                        int r = 0xff & (colors[l] >> 16);
                        int g = 0xff & (colors[l] >> 9);
                        int b = 0xff & colors[l];
                        colors[l] |= ((int)((1 - blnRatio) * r + blnRatio * (0xff & colorMapLUT[cIndex])) >> 16 )|
                                     ((int)((1 - blnRatio) * g + blnRatio * (0xff & colorMapLUT[cIndex + 1])) >> 8 )|
                                     (int)((1 - blnRatio) *  b + blnRatio * (0xff & colorMapLUT[cIndex + 2]));
                     }
                     break;
                  case DataArray.FIELD_DATA_INT:
                     int[] iData = colData.getIData();
                     for (int i = 0, l = 0; i < n; i++, l++)
                     {
                        if (vl == 1)
                           v = iData[i];
                        else
                        {
                           v = 0;
                           for (int j = 0, k = vl * i; j < vl; j++, k++)
                              v += iData[k] * iData[k];
                           v = Math.sqrt(v);
                        }
                        if (!wrapCMap)
                        {
                           cIndex = 3 * (int) (d * (v - low));
                           if (cIndex < 0)
                              cIndex = 0;
                           if (cIndex > 3 * nColors)
                              cIndex = 3 * nColors;
                        } else
                           cIndex = 3 * (((int) (d * (v - low) + 1000 * nColors)) % nColors);
                        int r = 0xff & (colors[l] >> 16);
                        int g = 0xff & (colors[l] >> 9);
                        int b = 0xff & colors[l];
                        colors[l] |= ((int)((1 - blnRatio) * r + blnRatio * (0xff & colorMapLUT[cIndex])) >> 16 )|
                                     ((int)((1 - blnRatio) * g + blnRatio * (0xff & colorMapLUT[cIndex + 1])) >> 8 )|
                                     (int)((1 - blnRatio) *  b + blnRatio * (0xff & colorMapLUT[cIndex + 2]));
                     }
                     break;
                  case DataArray.FIELD_DATA_FLOAT:
                     float[] fData = colData.getFData();
                     for (int i = 0, l = 0; i < n; i++, l++)
                     {
                        if (vl == 1)
                           v = fData[i];
                        else
                        {
                           v = 0;
                           for (int j = 0, k = vl * i; j < vl; j++, k++)
                              v += fData[k] * fData[k];
                           v = Math.sqrt(v);
                        }
                        if (!wrapCMap)
                        {
                           cIndex = 3 * (int) (d * (v - low));
                           if (cIndex < 0)
                              cIndex = 0;
                           if (cIndex > 3 * nColors)
                              cIndex = 3 * nColors;
                        } else
                           cIndex = 3 * (((int) (d * (v - low) + 1000 * nColors)) % nColors);
                        int r = 0xff & (colors[l] >> 16);
                        int g = 0xff & (colors[l] >> 9);
                        int b = 0xff & colors[l];
                        colors[l] |= ((int)((1 - blnRatio) * r + blnRatio * (0xff & colorMapLUT[cIndex])) >> 16 )|
                                     ((int)((1 - blnRatio) * g + blnRatio * (0xff & colorMapLUT[cIndex + 1])) >> 8 )|
                                     (int)((1 - blnRatio) *  b + blnRatio * (0xff & colorMapLUT[cIndex + 2]));
                     }
                     break;
                  case DataArray.FIELD_DATA_DOUBLE:
                     double[] dData = colData.getDData();
                     for (int i = 0, l = 0; i < n; i++, l++)
                     {
                        if (vl == 1)
                           v = dData[i];
                        else
                        {
                           v = 0;
                           for (int j = 0, k = vl * i; j < vl; j++, k++)
                              v += dData[k] * dData[k];
                           v = Math.sqrt(v);
                        }
                        if (!wrapCMap)
                        {
                           cIndex = 3 * (int) (d * (v - low));
                           if (cIndex < 0)
                              cIndex = 0;
                           if (cIndex > 3 * nColors)
                              cIndex = 3 * nColors;
                        } else
                           cIndex = 3 * (((int) (d * (v - low) + 1000 * nColors)) % nColors);
                        int r = 0xff & (colors[l] >> 16);
                        int g = 0xff & (colors[l] >> 9);
                        int b = 0xff & colors[l];
                        colors[l] |= ((int)((1 - blnRatio) * r + blnRatio * (0xff & colorMapLUT[cIndex])) >> 16 )|
                                     ((int)((1 - blnRatio) * g + blnRatio * (0xff & colorMapLUT[cIndex + 1])) >> 8 )|
                                      (int)((1 - blnRatio) * b + blnRatio * (0xff & colorMapLUT[cIndex + 2]));
                     }
                     break;
               }
            }
            break;
         case DataMappingParams.RGB:
            float minData = 0,
             maxData = 0,
             minC = 0,
             scale = 1;
            ColorComponentParams[] rgbParams = new ColorComponentParams[]
            {
               dataMappingParams.getRedParams(),
               dataMappingParams.getGreenParams(),
               dataMappingParams.getBlueParams()
            };
            DataArray[] components = new DataArray[3];
            for (int i = 0; i < rgbParams.length; i++)
               components[i] = data.getData(rgbParams[i].getDataComponent());
            if ((components[0] == null || !components[0].isSimpleNumeric())
                    && (components[1] == null || !components[1].isSimpleNumeric())
                    && (components[1] == null || !components[1].isSimpleNumeric()))
            {
               byte[] bc = new byte[]
               {
                  (byte) (0xff & col.get().getRed()),
                  (byte) (0xff & col.get().getGreen()),
                  (byte) (0xff & col.get().getBlue())
               };
               for (int i = 0; i < colors.length;)
                  for (int j = 0; j < bc.length; j++, i++)
                     colors[i] = bc[j];
               return colors;
            }
            for (int ncomp = 0; ncomp < 3; ncomp++)
            {
               colData = components[ncomp];
               if (colData == null || !colData.isSimpleNumeric())
               {
                  byte c = (byte) (0xff & (int) (255 * rgbParams[ncomp].getCmpMin()));
                  for (int i = ncomp; i < colors.length; i += 4)
                     colors[i] = c;
                  continue;
               }

               minC = 200 * rgbParams[ncomp].getCmpMin();
               minData = rgbParams[ncomp].getDataMin();
               maxData = rgbParams[ncomp].getDataMax();
               scale = (200 - minC) / (maxData - minData);
               vl = colData.getVeclen();
               switch (colData.getType())
               {
                  case DataArray.FIELD_DATA_BYTE:
                     byte[] bData = colData.getBData();
                     for (int i = 0, l = 4 * 0 + ncomp; i < n; i++, l += 4)
                     {
                        if (vl == 1)
                           v = 0xff & bData[i];
                        else
                        {
                           v = 0;
                           for (int j = 0, k = vl * i; j < vl; j++, k++)
                              v += (0xff & bData[k]) * (0xff & bData[k]);
                           v = Math.sqrt(v);
                        }
                        if (v < minData)
                           v = minData;
                        if (v > maxData)
                           v = maxData;
                        colors[l] = (byte) (0xff & (int) (minC + scale * (v - minData)));
                     }
                     break;
                  case DataArray.FIELD_DATA_SHORT:
                     short[] sData = colData.getSData();
                     for (int i = 0, l = 4 * 0 + ncomp; i < n; i++, l += 4)
                     {
                        if (vl == 1)
                           v = sData[i];
                        else
                        {
                           v = 0;
                           for (int j = 0, k = vl * i; j < vl; j++, k++)
                              v += sData[k] * sData[k];
                           v = Math.sqrt(v);
                        }
                        if (v < minData)
                           v = minData;
                        if (v > maxData)
                           v = maxData;
                        colors[l] = (byte) (0xff & (int) (minC + scale * (v - minData)));
                     }
                     break;
                  case DataArray.FIELD_DATA_INT:
                     int[] iData = colData.getIData();
                     for (int i = 0, l = 4 * 0 + ncomp; i < n; i++, l += 4)
                     {
                        if (vl == 1)
                           v = iData[i];
                        else
                        {
                           v = 0;
                           for (int j = 0, k = vl * i; j < vl; j++, k++)
                              v += iData[k] * iData[k];
                           v = Math.sqrt(v);
                        }
                        if (v < minData)
                           v = minData;
                        if (v > maxData)
                           v = maxData;
                        colors[l] = (byte) (0xff & (int) (minC + scale * (v - minData)));
                     }
                     break;
                  case DataArray.FIELD_DATA_FLOAT:
                     float[] fData = colData.getFData();
                     for (int i = 0, l = 4 * 0 + ncomp; i < n; i++, l += 4)
                     {
                        if (vl == 1)
                           v = fData[i];
                        else
                        {
                           v = 0;
                           for (int j = 0, k = vl * i; j < vl; j++, k++)
                              v += fData[k] * fData[k];
                           v = Math.sqrt(v);
                        }
                        if (v < minData)
                           v = minData;
                        if (v > maxData)
                           v = maxData;
                        colors[l] = (byte) (0xff & (int) (minC + scale * (v - minData)));
                     }
                     break;
                  case DataArray.FIELD_DATA_DOUBLE:
                     double[] dData = colData.getDData();
                     for (int i = 0, l = 4 * 0 + ncomp; i < n; i++, l += 4)
                     {
                        if (vl == 1)
                           v = dData[i];
                        else
                        {
                           v = 0;
                           for (int j = 0, k = vl * i; j < vl; j++, k++)
                              v += dData[k] * dData[k];
                           v = Math.sqrt(v);
                        }
                        if (v < minData)
                           v = minData;
                        if (v > maxData)
                           v = maxData;
                        colors[l] = (byte) (0xff & (int) (minC + scale * (v - minData)));
                     }
                     break;
               }
            }
            break;
      }
      if (data instanceof Field && ((Field) data).isTransparencyMask())
      {
         boolean[] valid = ((Field) data).getTransparencyMask();
         for (int i = 0; i < n; i++)
            if (!valid[i])
               colors[4 * (i - 0) + 3] = 0;
      }
      if (data instanceof Field && ((Field) data).isMask())
      {
         boolean[] valid = ((Field) data).getMask();
         float[] c = new float[3];
         col.get(c);
         byte[] bc = new byte[4];
         for (int i = 0; i < c.length; i++)
            bc[i] = (byte) (0xff & (int) (255 * c[i]));
         for (int i = 0, l = 0; i < n; i++, l++)
            if (!valid[i])
            {
               colors[4 * l] = bc[0];
               colors[4 * l + 1] = bc[1];
               colors[4 * l + 2] = bc[2];
               colors[4 * l + 3] = (byte) 127;
            }
      }
      if (transpose && n0 * n1 == n)
      {
         int[] tcolors = new int[n];
         for (int i = 0; i < n1; i++)
            for (int j = 0; j < n0; j++)
               tcolors[j * n1 + i] = colors[i * n0 + j];
         System.arraycopy(tcolors, 0, colors, 0, n);
      }
      return colors;
   }

   public static byte[] mapTransparencyIndexed(DataContainer data, TransparencyParams params,
           int[] indices, byte[] colors)
   {
      DataArray trData = data.getData(params.getComponent());
      if (trData == null)
      {
         for (int i = 3; i < colors.length; i += 4)
            colors[i] = (byte)(0xff);
         return colors;
      }
      int nInd = indices.length;
      if (colors == null || colors.length != 4 * nInd)
         colors = new byte[4 * nInd];
      int[] map = params.getMap();
      byte[] tMap = new byte[map.length];
      for (int i = 0; i < tMap.length; i++)
         tMap[i] = (byte)(0xff & map[i]);
      float low = params.getDataLow();
      float up = params.getDataUp();
      float d = 255 / (up - low);
      int tIndex = 0;
      int vl = trData.getVeclen();
      double v;
      switch (trData.getType())
      {
      case DataArray.FIELD_DATA_BYTE:
         byte[] bData = trData.getBData();
         for (int i = 0; i < nInd; i++)
         {
            if (vl == 1)
               v = 0xff & bData[indices[i]];
            else
            {
               v = 0;
               for (int j = 0, k = vl * indices[i]; j < vl; j++, k++)
                  v += (0xff & bData[k]) * (0xff & bData[k]);
               v = Math.sqrt(v);
            }
            tIndex = (int) (d * (v - low));
            if (tIndex < 0)
               tIndex = 0;
            if (tIndex > 255)
               tIndex = 255;
            colors[4 * i + 3] =tMap[tIndex];
         }
         break;
      case DataArray.FIELD_DATA_SHORT:
         short[] sData = trData.getSData();
         for (int i = 0; i < nInd; i++)
         {
            if (vl == 1)
               v = sData[indices[i]];
            else
            {
               v = 0;
               for (int j = 0, k = vl * indices[i]; j < vl; j++, k++)
                  v += sData[k] * sData[k];
               v = Math.sqrt(v);
            }
            tIndex = (int) (d * (v - low));
            if (tIndex < 0)
               tIndex = 0;
            if (tIndex > 255)
               tIndex = 255;
            colors[4 * i + 3] =tMap[tIndex];
         }
         break;
      case DataArray.FIELD_DATA_INT:
         int[] iData = trData.getIData();
         for (int i = 0; i < nInd; i++)
         {
            if (vl == 1)
               v = iData[indices[i]];
            else
            {
               v = 0;
               for (int j = 0, k = vl * indices[i]; j < vl; j++, k++)
                  v += iData[k] * iData[k];
               v = Math.sqrt(v);
            }
            tIndex = (int) (d * (v - low));
            if (tIndex < 0)
               tIndex = 0;
            if (tIndex > 255)
               tIndex = 255;
            colors[4 * i + 3] =tMap[tIndex];
         }
         break;
      case DataArray.FIELD_DATA_FLOAT:
         float[] fData = trData.getFData();
         for (int i = 0; i < nInd; i++)
         {
            if (vl == 1)
               v = fData[indices[i]];
            else
            {
               v = 0;
               for (int j = 0, k = vl * indices[i]; j < vl; j++, k++)
                  v += fData[k] * fData[k];
               v = Math.sqrt(v);
            }
            tIndex = (int) (d * (v - low));
            if (tIndex < 0)
               tIndex = 0;
            if (tIndex > 255)
               tIndex = 255;
            colors[4 * i + 3] =tMap[tIndex];
         }
         break;
      case DataArray.FIELD_DATA_DOUBLE:
         double[] dData = trData.getDData();
         for (int i = 0; i < nInd; i++)
         {
            if (vl == 1)
               v = dData[indices[i]];
            else
            {
               v = 0;
               for (int j = 0, k = vl * indices[i]; j < vl; j++, k++)
                  v += dData[k] * dData[k];
               v = Math.sqrt(v);
            }
            tIndex = (int) (d * (v - low));
            if (tIndex < 0)
               tIndex = 0;
            if (tIndex > 255)
               tIndex = 255;
            colors[4 * i + 3] =tMap[tIndex];
         }
         break;
      }
      return colors;
   }

   public static byte[] mapTransparency(DataContainer data, TransparencyParams params, int start, int end, int tStart, byte[] colors)
   {
      if (start < 0 || start >= end || end > data.getNNodes() || colors == null || colors.length < 4 * end)
         return null;
      DataArray trData = data.getData(params.getComponent());
      if (trData == null)
      {
         for (int i = 4 * tStart + 3; i < 4 * (tStart + end - start); i += 4)
            colors[i] = (byte)(0xff);
         return colors;
      }
      int[] map = params.getMap();
      byte[] tMap = new byte[map.length];
      for (int i = 0; i < tMap.length; i++)
         tMap[i] = (byte) (0xff & map[i]);
      float low = params.getDataLow();
      float up = params.getDataUp();
      float d = 255 / (up - low);
      int tIndex = 0;
      double v;
      int vl = trData.getVeclen();
      switch (trData.getType())
      {
         case DataArray.FIELD_DATA_BYTE:
            byte[] bData = trData.getBData();
            if (bData.length < end * vl)
               break;
            for (int i = start, it = tStart; i < end; i++, it++)
            {
               if (vl == 1)
                  v = 0xff & bData[i];
               else
               {
                  v = 0;
                  for (int j = 0, k = vl * i; j < vl; j++, k++)
                     v += (0xff & bData[k]) * (0xff & bData[k]);
                  v = Math.sqrt(v);
               }
               tIndex = (int) (d * (v - low));
               if (tIndex < 0)
                  tIndex = 0;
               if (tIndex > 255)
                  tIndex = 255;
               colors[4 * it + 3] = tMap[tIndex];
            }
            break;
         case DataArray.FIELD_DATA_SHORT:
            short[] sData = trData.getSData();
            if (sData.length < end * vl)
               break;
            for (int i = start, it = tStart; i < end; i++, it++)
            {
               if (vl == 1)
                  v = sData[i];
               else
               {
                  v = 0;
                  for (int j = 0, k = vl * i; j < vl; j++, k++)
                     v += sData[k] * sData[k];
                  v = Math.sqrt(v);
               }
               tIndex = (int) (d * (v - low));
               if (tIndex < 0)
                  tIndex = 0;
               if (tIndex > 255)
                  tIndex = 255;
               colors[4 * it + 3] = tMap[tIndex];
            }
            break;
         case DataArray.FIELD_DATA_INT:
            int[] iData = trData.getIData();
            if (iData.length < end * vl)
               break;
            for (int i = start, it = tStart; i < end; i++, it++)
            {
               if (vl == 1)
                  v = iData[i];
               else
               {
                  v = 0;
                  for (int j = 0, k = vl * i; j < vl; j++, k++)
                     v += iData[k] * iData[k];
                  v = Math.sqrt(v);
               }
               tIndex = (int) (d * (v - low));
               if (tIndex < 0)
                  tIndex = 0;
               if (tIndex > 255)
                  tIndex = 255;
               colors[4 * it + 3] = tMap[tIndex];
            }
            break;
         case DataArray.FIELD_DATA_FLOAT:
            float[] fData = trData.getFData();
            if (fData.length < end * vl)
               break;
            for (int i = start, it = tStart; i < end; i++, it++)
            {
               if (vl == 1)
                  v = fData[i];
               else
               {
                  v = 0;
                  for (int j = 0, k = vl * i; j < vl; j++, k++)
                     v += fData[k] * fData[k];
                  v = Math.sqrt(v);
               }
               tIndex = (int) (d * (v - low));
               if (tIndex < 0)
                  tIndex = 0;
               if (tIndex > 255)
                  tIndex = 255;
               colors[4 * it + 3] = tMap[tIndex];
            }
            break;
         case DataArray.FIELD_DATA_DOUBLE:
            double[] dData = trData.getDData();
            if (dData.length < end * vl)
               break;
            for (int i = start, it = tStart; i < end; i++, it++)
            {
               if (vl == 1)
                  v = dData[i];
               else
               {
                  v = 0;
                  for (int j = 0, k = vl * i; j < vl; j++, k++)
                     v += dData[k] * dData[k];
                  v = Math.sqrt(v);
               }
               tIndex = (int) (d * (v - low));
               if (tIndex < 0)
                  tIndex = 0;
               if (tIndex > 255)
                  tIndex = 255;
               colors[4 * it + 3] = tMap[tIndex];
            }
            break;
      }
      return colors;
   }

   public static byte[] mapTimeValidityTransparency(int[] timeRange, float currentTime, int start, int end, int tStart, byte[] colors)
   {
      if (start < 0 || start >= end || end > timeRange.length / 2 || colors == null || colors.length < 4 * end)
         return null;
      for (int i = 2 * start, it = 4 * tStart + 3; i < 2 * end; i += 2, it += 4)
         if (currentTime < timeRange[i] || timeRange[i + 1] < currentTime)
            colors[it] = 0;
      return colors;
   }

   public static void setTransparencyMask(byte[] colors, boolean[] mask, int start, int end, int tStart)
   {
      if (colors == null || colors.length < 4 * end)
         return;
      for (int i = start, j = 4 * tStart + 3; i < end; i++, j+=4)
         if (!mask[i])
            colors[j] = 0;
   }

   public static void setTransparencyMask(byte[] colors, boolean[] mask, int start, int end)
   {
      if (colors == null || colors.length < 4 * end)
         return;
      setTransparencyMask(colors, mask, start, end, 0);
   }

   public static void setTransparencyMask(byte[] colors, boolean[] mask)
   {
      if (colors == null || mask == null || colors.length != 4 * mask.length)
         return;
      setTransparencyMask(colors, mask, 0, mask.length);
   }

   public static byte[] map(DataContainer data, DataMappingParams dataMappingParams, int start, int end, Color3f col, byte[] colors)
   {
      return map(data, dataMappingParams, start, end, start, col, colors);
   }

   public static byte[] map(DataContainer data, DataMappingParams dataMappingParams, int start, int end, byte[] colors)
   {
      return map(data, dataMappingParams, start, end, new Color3f(1, 1, 1), colors);
   }

   public static byte[] map(DataContainer data, DataMappingParams dataMappingParams, int nData, Color3f col, byte[] colors)
   {
      return map(data, dataMappingParams, 0, data.getNNodes(), col, colors);
   }

   public static byte[] map(DataContainer data, DataMappingParams dataMappingParams, Color3f col, byte[] colors)
   {
      return map(data, dataMappingParams, data.getNNodes(), col, colors);
   }

   public static byte[] map(DataContainer data, DataMappingParams dataMappingParams, byte[] colors)
   {
      return map(data, dataMappingParams, data.getNNodes(), new Color3f(1, 1, 1), colors);
   }

   public static byte[] mapTransparency(DataContainer data, TransparencyParams params, int start, int end, byte[] colors)
   {
      if (colors == null || colors.length < 4 * end)
         return null;
      return mapTransparency(data, params, start, end, start, colors);
   }

   public static byte[] mapTransparency(DataContainer data, TransparencyParams params, int nData, byte[] colors)
   {
      return mapTransparency(data, params, 0, data.getNNodes(), colors);
   }

   public static byte[] mapTransparency(DataContainer data, TransparencyParams params, byte[] colors)
   {
      return mapTransparency(data, params, data.getNNodes(), colors);
   }

   private ColorMapper()
   {
   }
}
