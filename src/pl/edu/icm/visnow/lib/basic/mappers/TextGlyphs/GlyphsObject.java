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

package pl.edu.icm.visnow.lib.basic.mappers.TextGlyphs;

import java.awt.Color;
import java.awt.Font;
import javax.media.j3d.*;
import javax.vecmath.Color3f;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.datasets.dataarrays.StringDataArray;
import pl.edu.icm.visnow.geometries.objects.DataMappedGeometryObject;
import pl.edu.icm.visnow.geometries.objects.TextBillboard;
import pl.edu.icm.visnow.geometries.objects.generics.OpenBranchGroup;
import pl.edu.icm.visnow.geometries.parameters.FontParams;
import pl.edu.icm.visnow.geometries.utils.transform.LocalToWindow;
import pl.edu.icm.visnow.lib.utils.geometry2D.CXYZString;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class GlyphsObject extends DataMappedGeometryObject
{
   private CXYZString[] glyphs = null;
   private Params params;
   private FontParams fontParams;
   private Field inField = null;
   private OpenBranchGroup outGroup = null;
   private DataArray data,thrData;
   private float thr = -Float.MAX_VALUE;
   private float[] baseCoords = null;
   private int[] glyphIn = null;
   private int nGlyphs = 0;
   private int fontSize = 12;

   public GlyphsObject()
   {
      name = "text glyphs";
   }

   private boolean isValid(int i)
   {
      double d = 0, t = 0;
      if (thrData == null)
         return true;
      if (thrData.getVeclen() == 1)
         t = thrData.getData(i);
      else
      {
         float[] v = thrData.getVData(i);
         t = 0;
         for (int j = 0; j < v.length; j++)
            t += v[j] * v[j];
         t = Math.sqrt(t);
      }
      return t > thr;
   }

   @Override
   public void drawLocal2D(J3DGraphics2D vGraphics, LocalToWindow ltw)
   {
      if (ltw == null || params == null)
         return;
      if (glyphs != null)
      {
         for (int i = 0; i < glyphs.length; i++)
         {
            if (glyphs[i] != null)
            {
               glyphs[i].update(ltw);
               glyphs[i].draw(vGraphics);
            }
         }
      }
   }

   private void prepareGlyphCount()
   {
      nGlyphs = 0;
      data = inField.getData(params.getComponent());
      if (data == null || (data.getVeclen() != 3 && data.getVeclen() != 1))
         return;
      thr = params.getThr();
      thrData = null;
      if (params.getThrComponent() >= 0)
         thrData = inField.getData(params.getThrComponent());
      int[] gl = new int[inField.getNNodes()];
      for (int i = 0; i < gl.length; i++)
         gl[i] = -1;
      nGlyphs = 0;
      if (inField instanceof RegularField)
      {
         RegularField inRegularField = (RegularField) inField;
         if (inRegularField.getDims() == null)
            return;
         int[] dims = inRegularField.getDims();
         int[] down = params.getDown();
         switch (dims.length)
         {
         case 3:
            for (int i = 0; i < dims[2]; i += down[2])
               for (int j = 0; j < dims[1]; j += down[1])
                  for (int k = 0, l = (dims[1] * i + j) * dims[0]; k < dims[0]; k += down[0], l += down[0])
                     if (isValid(l))
                     {
                        gl[nGlyphs] = l;
                        nGlyphs += 1;
                     }
            break;
         case 2:
            for (int j = 0; j < dims[1]; j += down[1])
               for (int k = 0, l = j * dims[0]; k < dims[0]; k += down[0], l += down[0])
                  if (isValid(l))
                  {
                     gl[nGlyphs] = l;
                     nGlyphs += 1;
                  }
            break;
         case 1:
            for (int l = 0; l < dims[0]; l += down[0])
               if (isValid(l))
               {
                  gl[nGlyphs] = l;
                  nGlyphs += 1;
               }
         }
      } else
      {
         int downsize = params.getDownsize();
         for (int i = 0; i < inField.getNNodes(); i += downsize)
            if (isValid(i))
            {
               gl[nGlyphs] = i;
               nGlyphs += 1;
            }
      }
      glyphIn = new int[nGlyphs];
      System.arraycopy(gl, 0, glyphIn, 0, nGlyphs);
      baseCoords = new float[3 * nGlyphs];
      for (int i = 0; i < baseCoords.length; i++)
         baseCoords[i] = 0;
      if (inField.getCoords() != null)
      {
         int nSp = inField.getNSpace();
         float[] fldCoords = inField.getCoords();
         for (int i = 0; i < nGlyphs; i++)
            for (int j = 0; j < nSp; j++)
               baseCoords[3 * i + j] = fldCoords[nSp * glyphIn[i] + j];
      } else if (inField instanceof RegularField)
      {
         float[][] inAff = ((RegularField) inField).getAffine();
         int[] dims = ((RegularField) inField).getDims();
         int i0 = 0, i1 = 0, i2 = 0;
         for (int i = 0; i < nGlyphs; i++)
         {
            int j = glyphIn[i];
            i0 = j % dims[0];
            if (dims.length > 1)
            {
               j /= dims[0];
               i1 = j % dims[1];
               if (dims.length > 2)
                  i2 = j / dims[1];
            }
            for (int k = 0; k < 3; k++)
               baseCoords[3 * i + k] = inAff[3][k] + i0 * inAff[0][k] + i1 * inAff[1][k] + i2 * inAff[2][k];
         }
      }
   }

   public void prepareGlyphs()
   {
      if (getCurrentViewer() == null || localToWindow == null)
         return;
      fontParams.createFontMetrics(localToWindow, 
                                   getCurrentViewer().getWidth(), 
                                   getCurrentViewer().getHeight());
      String fontName = fontParams.getFontName();
      fontSize = fontParams.getFontSize();
      data = inField.getData(params.getComponent());
      if (data == null || data.getVeclen() != 1 || nGlyphs < 1)
         return;
      String format = params.getFormat();
      BoundingSphere bSphere = new BoundingSphere();
      float[] c = new float[3];
      Color3f textColor = new Color3f(fontParams.getColor());
      Color textColor2d = fontParams.getColor();
      if (outGroup != null)
         outGroup.detach();
      outGroup = null;
      if (fontParams.isThreeDimensional())
      {
         glyphs = null;
         outGroup = new OpenBranchGroup();
         if (data.getType() == DataArray.FIELD_DATA_STRING)
         {
            String[] texts = ((StringDataArray) data).getStringData();
            for (int i = 0; i < nGlyphs; i++)
            {
               for (int j = 0; j < c.length; j++)
                  c[j] = baseCoords[3 * i + j];
               outGroup.addChild(TextBillboard.createBillboard(texts[glyphIn[i]],
                                 fontParams, c, Billboard.ROTATE_ABOUT_POINT, bSphere));
            }
         } else
         {
            for (int i = 0; i < nGlyphs; i++)
            {
               for (int j = 0; j < c.length; j++)
                  c[j] = baseCoords[3 * i + j];
               try
               {
                  outGroup.addChild(TextBillboard.createBillboard(String.format(format, data.getData(glyphIn[i])),
                                    fontParams, c, Billboard.ROTATE_ABOUT_POINT, bSphere));
               } catch (Exception e)
               {
               }
            }
         }
         addNode(outGroup);
         setExtents(inField.getExtents());
      }
      else
      {
         Font font = fontParams.getFont2D();
         glyphs = new CXYZString[nGlyphs];
         if (data.getType() == DataArray.FIELD_DATA_STRING)
         {
            String[] texts = ((StringDataArray) data).getStringData();
            for (int i = 0; i < nGlyphs; i++)
               glyphs[i] = new CXYZString(texts[glyphIn[i]], textColor2d,
                       baseCoords[3 * i], baseCoords[3 * i + 1], baseCoords[3 * i + 2], font);
         } else
         {
            for (int i = 0; i < nGlyphs; i++)
               glyphs[i] = new CXYZString(String.format(format, data.getData(glyphIn[i])), textColor2d,
                       baseCoords[3 * i], baseCoords[3 * i + 1], baseCoords[3 * i + 2], font);
         }
         geometryObj.getCurrentViewer().refresh();
      }
   }

   public void update(Field inField, Params params)
   {
      this.inField = inField;
      this.params = params;
      fontParams = params.getFontParams();
      if (params.getChange() == Params.COUNT_CHANGED)
         prepareGlyphCount();
      if (nGlyphs < 1)
         return;
      if (params.getChange() >= Params.GLYPHS_CHANGED)
         prepareGlyphs();
      params.setChange(0);
   }
}
