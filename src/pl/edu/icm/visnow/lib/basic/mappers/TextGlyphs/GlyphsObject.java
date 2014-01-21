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

package pl.edu.icm.visnow.lib.basic.mappers.TextGlyphs;

import java.awt.Color;
import java.util.IllegalFormatException;
import javax.media.j3d.*;
import org.apache.log4j.Logger;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.datasets.dataarrays.StringDataArray;
import pl.edu.icm.visnow.geometries.objects.DataMappedGeometryObject;
import pl.edu.icm.visnow.geometries.objects.GeometryObject;
import pl.edu.icm.visnow.geometries.objects.generics.OpenBranchGroup;
import pl.edu.icm.visnow.geometries.parameters.FontParams;
import pl.edu.icm.visnow.geometries.utils.Texts2D;
import pl.edu.icm.visnow.geometries.utils.Texts3D;
import pl.edu.icm.visnow.geometries.utils.transform.LocalToWindow;
import pl.edu.icm.visnow.geometries.viewer3d.Display3DPanel;
import pl.edu.icm.visnow.lib.utils.geometry2D.CXYZString;
import pl.edu.icm.visnow.system.main.VisNow;
import pl.edu.icm.visnow.system.utils.usermessage.Level;
import pl.edu.icm.visnow.system.utils.usermessage.UserMessage;

/**
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class GlyphsObject extends DataMappedGeometryObject
{
    private static final Logger LOGGER = Logger.getLogger(GlyphsObject.class);
    
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
   
   //TODO: this should be accessible by sth like params.getFormat().getDefaultValue() or parameters.getDefaultValue("format");
   private String defaultTextFormat;
   
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
   public void drawLocal2D(J3DGraphics2D vGraphics, LocalToWindow ltw, int width, int height)
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
               glyphs[i].draw(vGraphics, width, height);
            }
         }
      }
   }
   
      
   private void updateCoords()
   {
      if (nGlyphs <= 0)
         return;
      if (baseCoords == null ||
          baseCoords.length != 3 * nGlyphs)
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
   }

   public void prepareGlyphs()
   {
      if (getCurrentViewer() == null || localToWindow == null)
         return;
      fontParams.createFontMetrics(localToWindow,
                                   getCurrentViewer().getWidth(),
                                   getCurrentViewer().getHeight());
      fontSize = fontParams.getFontSize();
      data = inField.getData(params.getComponent());
      if (data == null || data.getVeclen() != 1 || nGlyphs < 1)
         return;
      String format = params.getFormat();
      Color textColor2d = fontParams.getColor();
      if (outGroup != null)
         outGroup.detach();
      outGroup = null;
       String[] texts = null;
       if (data.getType() == DataArray.FIELD_DATA_STRING) {
           texts = new String[nGlyphs];
           String[] sData = ((StringDataArray) data).getStringData();
           for (int i = 0; i < texts.length; i++)
               texts[i] = sData[glyphIn[i]];
       } else {
           texts = new String[nGlyphs];
           try {
               for (int i = 0; i < texts.length; i++)
                   texts[i] = String.format(format, data.getData(glyphIn[i]));
           } catch (IllegalFormatException ex) { //if incorrect format then drop to default format
               //TODO: how to resolve problem with no access to current application? Maybe some VisNow.getCurrentApplication()
               VisNow.get().userMessageSend(new UserMessage("", "text glyphs", "Incorrect text format", "Specified format is incorrect: " + format + "<br>" + "Dropped to default one: " + defaultTextFormat, Level.WARNING));
               for (int i = 0; i < texts.length; i++)
                   texts[i] = String.format(defaultTextFormat, data.getData(glyphIn[i]));
           }
      }
      if (fontParams.isThreeDimensional())
      {
         glyphs = null;
         outGroup = new Texts3D(baseCoords, texts, fontParams);
         addNode(outGroup);
         setExtents(inField.getExtents());
      }
      else
      {
         Texts2D texts2D = new Texts2D(baseCoords, texts, fontParams);
         glyphs = texts2D.getGlyphs();
         geometryObj.getCurrentViewer().refresh();
      }
   }

   public void update(Field inField, Params params) 
  {
       //TODO: apparently this method is called twice after change in component selector (should be fixed)
       //additionally onActive is not called after such user interaction! 
      LOGGER.info("");
       
      this.inField = inField;
      this.params = params;
      if (defaultTextFormat == null) defaultTextFormat = params.getFormat();
      fontParams = params.getFontParams();
      if (params.getChange() == Params.COUNT_CHANGED)
         prepareGlyphCount();
      if (nGlyphs < 1)
         return;
      if (params.getChange() >= Params.GLYPHS_CHANGED)
      {
         updateCoords();
         prepareGlyphs();
      }
      params.setChange(0);
   }
   
   @Override
   public void setCurrentViewer(Display3DPanel panel) {
       this.myViewer = panel;
       outObj.setCurrentViewer(panel);

       for(GeometryObject child : geomChildren) {
           child.setCurrentViewer(panel);
       }
       prepareGlyphs();
   }
   
}
