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

package pl.edu.icm.visnow.geometries.utils;

import java.awt.image.BufferedImage;
import pl.edu.icm.visnow.datasets.DataContainer;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.geometries.parameters.ColorComponentParams;
import pl.edu.icm.visnow.geometries.parameters.DataMappingParams;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class TextureMapper
{

   public static float[] map(DataArray data, ColorComponentParams params, int start, int end, float[] uv, int iuv, float delta)
   {
      double v;
      float t;
      if (start < 0 || start >= end || end > data.getNData())
         return null;
      if (uv == null || uv.length != 2 * (end - start))
      {
         uv = new float[2 * (end - start)];
         for (int i = 0; i < uv.length; i++)
            uv[i] = 0;
      }
      if (params.getDataComponent() == DataMappingParams.NULL)
         return uv;
      boolean wrap = params.isWrap();
      float low = params.getDataMin();
      float d = 1 / (params.getDataMax() - low);
      if (data != null)
      {
         int vl = data.getVeclen();
         switch (data.getType())
         {
         case DataArray.FIELD_DATA_BYTE:
            byte[] bData = data.getBData();
            for (int i = start, l = iuv; i < end; i++, l += 2)
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
               t = (float) (d * (v - low));
               if (wrap)
                  t = (t + 1000) - (int) (t + 1000);
               else
               {
                  if (t < delta) t = delta;
                  if (t > 1 - delta) t = 1 - delta;
               }
               uv[l] = t;
            }
            break;
         case DataArray.FIELD_DATA_SHORT:
            short[] sData = data.getSData();
            for (int i = start, l = iuv; i < end; i++, l += 2)
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
               t = (float) (d * (v - low));
               if (wrap)
                  t = (t + 1000) - (int) (t + 1000);
               else
               {
                  if (t < delta) t = delta;
                  if (t > 1 - delta) t = 1 - delta;
               }
               uv[l] = t;
            }
            break;
         case DataArray.FIELD_DATA_INT:
            int[] iData = data.getIData();
            for (int i = start, l = iuv; i < end; i++, l += 2)
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
               t = (float) (d * (v - low));
               if (wrap)
                  t = (t + 1000) - (int) (t + 1000);
               else
               {
                  if (t < delta) t = delta;
                  if (t > 1 - delta) t = 1 - delta;
               }
               uv[l] = t;
            }
            break;
         case DataArray.FIELD_DATA_FLOAT:
            float[] fData = data.getFData();
            for (int i = start, l = iuv; i < end; i++, l += 2)
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
               t = (float) (d * (v - low));
               if (wrap)
                  t = (t + 1000) - (int) (t + 1000);
               else
               {
                  if (t < delta) t = delta;
                  if (t > 1 - delta) t = 1 - delta;
               }
               uv[l] = t;
            }
            break;
         case DataArray.FIELD_DATA_DOUBLE:
            double[] dData = data.getDData();
            for (int i = start, l = iuv; i < end; i++, l += 2)
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
               t = (float) (d * (v - low));
               if (wrap)
                  t = (t + 1000) - (int) (t + 1000);
               else
               {
                  if (t < delta) t = delta;
                  if (t > 1 - delta) t = 1 - delta;
               }
               uv[l] = t;
            }
            break;
         }
      }
      return uv;
   }

   public static float[] map(DataArray data, ColorComponentParams params,  float[] uv, int iuv)
   {
      return map(data, params, 0, data.getNData(), uv, iuv, .005f);
   }
   
   public static float[] map(int[] dims, ColorComponentParams params, float[] uv, int iuv, float delta)
   {
      if (dims.length != 2)
         return uv;
      int k = params.getDataComponent();
      int d0 = 2 * dims[0];
      if (k == DataMappingParams.INDEXI)
         k = 0;
      else if (k == DataMappingParams.INDEXJ)
         k = 1;
      else
         return uv;
      float t;
      boolean wrap = params.isWrap();
      float low = params.getDataMin() * (dims[k] - 1) / 100f;
      float d   = 100.f / ((dims[k] - 1) * (params.getDataMax() - params.getDataMin()));
      if (uv == null || uv.length != 2 * dims[0] * dims[1])
         uv = new float[2 * dims[0] * dims[1]];
      switch (k)
      {
      case 0:
         for (int i = 0; i < dims[0]; i++)
         {
            t = (i - low) * d;
            if (wrap)
               t = (t + 1000) - (int) (t + 1000);
            else
            {
                  if (t < delta) t = delta;
                  if (t > 1 - delta) t = 1 - delta;
            }
            for (int j = 0, l = 2 * i + iuv; j < dims[1]; j++, l += d0)
               uv[l] = t;
         }
         break;
      case 1:
         for (int i = 0, l = iuv; i < dims[1]; i++)
         {
            t = (i - low) * d;
            if (wrap)
               t = (t + 1000) - (int) (t + 1000);
            else
            {
                  if (t < delta) t = delta;
                  if (t > 1 - delta) t = 1 - delta;
            }
            for (int j = 0; j < dims[0]; j++, l += 2)
               uv[l] = t;
         }
          break;
      }
      return uv;
   }
   
   public static float[] map(float[] coords, int nSpace, float[][] extents, ColorComponentParams params, float[] uv, int iuv, float delta)
   {
      if (coords.length / nSpace != uv.length / 2)
         return uv;
      int k = params.getDataComponent();
      switch (k)
      {
         case DataMappingParams.COORDX:
         case DataMappingParams.NORMALX:
            k = 0;
            break;
         case DataMappingParams.COORDY:
         case DataMappingParams.NORMALY:
            k = 1;
            break;
         case DataMappingParams.COORDZ:
         case DataMappingParams.NORMALZ:
            k = 2;
            break;
         default:
            k = 0;
      }
      float pmin = params.getDataMin() / 100;
      float pmax = params.getDataMax() / 100;
      float min = extents[0][k] + pmin *  (extents[1][k] - extents[0][k]);
      float max = extents[0][k] + pmax *  (extents[1][k] - extents[0][k]);
      float scale = 1 / (max - min);
      float t;
      if (params.isWrap())
         for (int i = k, l = iuv; i < coords.length; i+= nSpace, l += 2)
         {
            t = (coords[i] - min) * scale;
            t = (t + 1000) - (int) (t + 1000);
            uv[l] = t;
         }
      else
         for (int i = k, l = iuv; i < coords.length; i+= nSpace, l += 2)
         {
            t = (coords[i] - min) * scale;
            if (t < delta) t = delta;
            if (t > 1 - delta) t = 1 - delta;
            uv[l] = t;
         }
      return uv;
   }
   
   public static int[] map(DataContainer inField, DataMappingParams params, int[] textureImageData, int start, int end, int[] texture)
   {  
      if (texture == null || texture.length != end - start)
         texture = new int[end - start];
      for (int i = 0; i < texture.length; i++)
         texture[i] = 0;
      DataArray uData = inField.getData(params.getUParams().getDataComponent());
      DataArray vData = inField.getData(params.getVParams().getDataComponent());
      BufferedImage textureImage = params.getTextureImage();
      if (uData == null || vData == null || textureImage == null)
         return texture;
      double v;
      int t;
      int dim = textureImage.getWidth() - 1;
      float low = params.getUParams().getDataMin();
      float d = dim / (params.getUParams().getDataMax() - low);
      int vl = uData.getVeclen();
      switch (uData.getType())
      {
      case DataArray.FIELD_DATA_BYTE:
         byte[] bData = uData.getBData();
         for (int i = start, l = 0; i < end; i++, l++)
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
            t = (int) (d * (v - low));
            if (t < 0) t = 0;
            if (t >= dim) t = dim - 1;
            texture[l] = t;
         }
         break;
      case DataArray.FIELD_DATA_SHORT:
         short[] sData = uData.getSData();
         for (int i = start, l = 0; i < end; i++, l++)
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
            t = (int) (d * (v - low));
            if (t < 0) t = 0;
            if (t >= dim) t = dim - 1;
            texture[l] = t;
         }
         break;
      case DataArray.FIELD_DATA_INT:
         int[] iData = uData.getIData();
         for (int i = start, l = 0; i < end; i++, l++)
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
            t = (int) (d * (v - low));
            if (t < 0) t = 0;
            if (t >= dim) t = dim - 1;
            texture[l] = t;
         }
         break;
      case DataArray.FIELD_DATA_FLOAT:
         float[] fData = uData.getFData();
         for (int i = start, l = 0; i < end; i++, l++)
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
            t = (int) (d * (v - low));
            if (t < 0) t = 0;
            if (t >= dim) t = dim - 1;
            texture[l] = t;
         }
         break;
      case DataArray.FIELD_DATA_DOUBLE:
         double[] dData = uData.getDData();
         for (int i = start, l = 0; i < end; i++, l++)
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
            t = (int) (d * (v - low));
            if (t < 0) t = 0;
            if (t >= dim) t = dim - 1;
            texture[l] = t;
         }
         break;
      }
      int width  = params.getTextureImage().getWidth();
      dim = params.getTextureImage().getHeight() - 1;
      low = params.getVParams().getDataMin();
      d = dim / (params.getVParams().getDataMax() - low);
      vl = vData.getVeclen();
      switch (vData.getType())
      {
      case DataArray.FIELD_DATA_BYTE:
         byte[] bData = vData.getBData();
         for (int i = start, l = 0; i < end; i++, l++)
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
            t = (int) (d * (v - low));
            if (t < 0) t = 0;
            if (t >= dim) t = dim - 1;
            texture[l] = textureImageData[t * width + texture[l]];
         }
         break;
      case DataArray.FIELD_DATA_SHORT:
         short[] sData = vData.getSData();
         for (int i = start, l = 0; i < end; i++, l++)
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
            t = (int) (d * (v - low));
            if (t < 0) t = 0;
            if (t >= dim) t = dim - 1;
            texture[l] = textureImageData[t * width + texture[l]];
         }
         break;
      case DataArray.FIELD_DATA_INT:
         int[] iData = vData.getIData();
         for (int i = start, l = 0; i < end; i++, l++)
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
            t = (int) (d * (v - low));
            if (t < 0) t = 0;
            if (t >= dim) t = dim - 1;
            texture[l] = textureImageData[t * width + texture[l]];
         }
         break;
      case DataArray.FIELD_DATA_FLOAT:
         float[] fData = vData.getFData();
         for (int i = start, l = 0; i < end; i++, l++)
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
            t = (int) (d * (v - low));
            if (t < 0) t = 0;
            if (t >= dim) t = dim - 1;
            texture[l] = textureImageData[t * width + texture[l]];
         }
         break;
      case DataArray.FIELD_DATA_DOUBLE:
         double[] dData = vData.getDData();
         for (int i = start, l = 0; i < end; i++, l++)
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
            t = (int) (d * (v - low));
            if (t < 0) t = 0;
            if (t >= dim) t = dim - 1;
            texture[l] = textureImageData[t * width + texture[l]];
         }
         break;
      }
      return texture;
   }

   private TextureMapper()
   {
   }

  
   
}
