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

package pl.edu.icm.visnow.lib.basic.mappers.Axes3D;
/*
 * Axes3DObject.java
 *
 * Created on November 4, 2003, 2:35 PM
 */

import pl.edu.icm.visnow.geometries.objects.generics.OpenLineAttributes;
import pl.edu.icm.visnow.geometries.objects.generics.OpenShape3D;
import pl.edu.icm.visnow.geometries.objects.DataMappedGeometryObject;
import pl.edu.icm.visnow.geometries.objects.generics.OpenAppearance;
import java.awt.Color;
import java.awt.Font;
import javax.media.j3d.Appearance;
import javax.media.j3d.Billboard;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.J3DGraphics2D;
import javax.media.j3d.LineArray;
import javax.media.j3d.LineAttributes;
import javax.vecmath.Color3f;
import pl.edu.icm.visnow.geometries.objects.generics.OpenBranchGroup;
import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.geometries.objects.TextBillboard;
import pl.edu.icm.visnow.geometries.parameters.FontParams;
import pl.edu.icm.visnow.geometries.utils.transform.LocalToWindow;
import pl.edu.icm.visnow.lib.utils.geometry2D.CXYZString;
import pl.edu.icm.visnow.lib.utils.Range;

/**
 *
 * @author  Krzysztof S. Nowinski (know@icm.edu.pl)
 * Warsaw University
 * Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class Axes3DObject extends DataMappedGeometryObject
{

   static String[] defAxDesc =
   {
      "X", "Y", "Z"
   };
   static String[] defAxFormats =
   {
      "%5.2f", "%5.2f", "%5.2f"
   };
   private FontParams fontParams;
   private String[] axDesc = defAxDesc;
   private String[] axFormats = defAxFormats;
   private boolean[] grid;
   private CXYZString[] axisLegends = null;
   private CXYZString[] axisNames = null;
   private float x0, x1, vx0, vx1;
   private float y0, y1, vy0, vy1;
   private float z0, z1, vz0, vz1;
   private float sx, sy, sz;
   private int nx, ny, nz;
   private Range rx, ry, rz;
   private int[][] axPos;
   private boolean box;
   private boolean drawX = true, drawY = true, drawZ = true;
   private int drawn = 3;
   private int nLabels = 0;

   public Axes3DObject()
   {
      name = "axes " + id;
   }

   /** Creates a new instance of Axes3DObject */
   public void update(Field field, Params params)
   {
//      new Exception().printStackTrace();
      if (params == null || field == null)
         return;
      fontParams = params.getFontParams();
      axDesc = params.getAxDescs();
      axFormats = params.getAxFormats();
      axPos = params.getAxPos();
      grid = params.getGridLines();
      extents = field.getExtents();
      box = params.isBox();
      float[][] physExts = extents;
      if (field.getPhysExts() != null)
         physExts = field.getPhysExts();
      float r = 0, s, c;
      x0 = extents[0][0];
      x1 = extents[1][0];
      y0 = extents[0][1];
      y1 = extents[1][1];
      z0 = extents[0][2];
      z1 = extents[1][2];
      sx = (physExts[1][0] - physExts[0][0]) / (x1 - x0);
      sy = (physExts[1][1] - physExts[0][1]) / (y1 - y0);
      sz = (physExts[1][2] - physExts[0][2]) / (z1 - z0);
      if (r < x1 - x0)
         r = x1 - x0;
      if (r < y1 - y0)
         r = y1 - y0;
      if (r < z1 - z0)
         r = z1 - z0;
      s = .05f * r;
      drawn = 3;
      drawX = x1 - x0 > .1f * r;
      drawY = y1 - y0 > .1f * r;
      drawZ = z1 - z0 > .1f * r;
      if (!drawX) drawn -= 1;
      if (!drawY) drawn -= 1;
      if (!drawZ) drawn -= 1;
      if (drawn < 2)
         return;
      if (drawX)
      {
         nx = (int) (params.getLabelDensity() * (x1 - x0) / r);
         if (nx < 200) nx = 200;
         vx0 = physExts[0][0] + sx * (x0 - extents[0][0]);
         vx1 = physExts[1][0] + sx * (x1 - extents[1][0]);
         rx = new Range(vx0, vx1, nx, false);
         vx0 = rx.getLow();
         vx1 = rx.getUp();
         x0 = extents[0][0] + (vx0 - physExts[0][0]) / sx;
         x1 = extents[1][0] + (vx1 - physExts[1][0]) / sx;
      }
      if (drawY)
      {
         ny = (int) (params.getLabelDensity() * (y1 - y0) / r);
         if (ny < 200) ny = 200;
         vy0 = physExts[0][1] + sy * (y0 - extents[0][1]);
         vy1 = physExts[1][1] + sy * (y1 - extents[1][1]);
         ry = new Range(vy0, vy1, ny, false);
         vy0 = ry.getLow();
         vy1 = ry.getUp();
         y0 = extents[0][1] + (vy0 - physExts[0][1]) / sy;
         y1 = extents[1][1] + (vy1 - physExts[1][1]) / sy;
      }
      if (drawZ)
      {
         nz = (int) (params.getLabelDensity() * (z1 - z0) / r);
         if (nz < 200) nz = 200;
         vz0 = physExts[0][2] + sz * (z0 - extents[0][2]);
         vz1 = physExts[1][2] + sz * (z1 - extents[1][2]);
         rz = new Range(vz0, vz1, nz, false);
         vz0 = rz.getLow();
         vz1 = rz.getUp();
         z0 = extents[0][2] + (vz0 - physExts[0][2]) / sz;
         z1 = extents[1][2] + (vz1 - physExts[1][2]) / sz;
      }
// x0-x1, y0-y1, z0--z1 are geom extends corrected for being not too short
// v.0-v.1 are range extents corrected for .0-.1 ranges
         
// v.0-v.1 are rounded range extents, .0-.1  are corresponding geometric extends      
      createAxes();
   }


   public void createAxes()
   {
      String[] ad;
      int i, j, k, m;
      float gx, gy, gz;
      float[] t = new float[18];
      fontParams.createFontMetrics(localToWindow, 
                                   getCurrentViewer().getWidth(), 
                                   getCurrentViewer().getHeight());
      if (drawX)
      {
         t[0] = x0;
         t[3] = x1;
         switch (axPos[0][0])
         {
         case Params.MIN:
            t[1] = t[4] = y0;
            break;
         case Params.ZERO:
            t[1] = t[4] = 0;
            break;
         case Params.CENTER:
            t[1] = t[4] = .5f * (y0 + y1);
            break;
         case Params.MAX:
            t[1] = t[4] = y1;
            break;
         }
         switch (axPos[0][1])
         {
         case Params.MIN:
            t[2] = t[5] = z0;
            break;
         case Params.ZERO:
            t[2] = t[5] = 0;
            break;
         case Params.CENTER:
            t[2] = t[5] = .5f * (z0 + z1);
            break;
         case Params.MAX:
            t[2] = t[5] = z1;
            break;
         }
      }
      if (drawY)
      {
         t[7] = y0;
         t[10] = y1;
         switch (axPos[1][0])
         {
         case Params.MIN:
            t[6] = t[9] = x0;
            break;
         case Params.ZERO:
            t[6] = t[9] = 0;
            break;
         case Params.CENTER:
            t[6] = t[9] = .5f * (x0 + x1);
            break;
         case Params.MAX:
            t[6] = t[9] = x1;
            break;
         }
         switch (axPos[1][1])
         {
         case Params.MIN:
            t[8] = t[11] = z0;
            break;
         case Params.ZERO:
            t[8] = t[11] = 0;
            break;
         case Params.CENTER:
            t[8] = t[11] = .5f * (z0 + z1);
            break;
         case Params.MAX:
            t[8] = t[11] = z1;
            break;
         }
      }
      if (drawZ)
      {
         t[14] = z0;
         t[17] = z1;
         switch (axPos[2][0])
         {
         case Params.MIN:
            t[12] = t[15] = x0;
            break;
         case Params.ZERO:
            t[12] = t[15] = 0;
            break;
         case Params.CENTER:
            t[12] = t[15] = .5f * (x0 + x1);
            break;
         case Params.MAX:
            t[12] = t[15] = x1;
            break;
         }
         switch (axPos[2][1])
         {
         case Params.MIN:
            t[13] = t[16] = y0;
            break;
         case Params.ZERO:
            t[13] = t[16] = 0;
            break;
         case Params.CENTER:
            t[13] = t[16] = .5f * (y0 + y1);
            break;
         case Params.MAX:
            t[13] = t[16] = y1;
            break;
         }
      }
      geometryObj.removeAllChildren();
      OpenBranchGroup axes = new OpenBranchGroup();
      if (axDesc.length == 3)
         ad = axDesc;
      else
         ad = defAxDesc;

//axes
      LineArray l = new LineArray(6, LineArray.COORDINATES
              | LineArray.COLOR_3);
      
      float colorCorrection = Math.min(2, Math.max(0, fontParams.getColorCorrection()));
      float val1  = Math.min(colorCorrection, 1);
      float val0 = Math.max(colorCorrection - 1, 0);
      float[] col =
      {
         val1, val0, val0, val1, val0, val0,
         val0, val1, val0, val0, val1, val0,
         val0, val0, val1, val0, val0, val1
      };

      if (colorCorrection < 1)
         for (int n = 0; n < col.length; n++)
            col[n] *= colorCorrection;
      else
         for (int n = 0; n < col.length; n++)
            if (col[n] == 0)
            col[n] = colorCorrection - 1;

      l.setCoordinates(0, t, 0, 6);
      l.setColors(0, col);
      l.setCapability(LineArray.ALLOW_COUNT_READ);
      l.setCapability(LineArray.ALLOW_FORMAT_READ);
      l.setCapability(LineArray.ALLOW_COORDINATE_READ);
      OpenLineAttributes la =
              new OpenLineAttributes(2.0f, LineAttributes.PATTERN_SOLID, true);
      OpenShape3D tr = new OpenShape3D();
      Appearance ap = new Appearance();
      ap.setLineAttributes(la);
      tr.setGeometry(l);
      tr.setAppearance(ap);
      axes.addChild(tr);

//box
      if (box)
      {

         float[] t1 =
         {
            x0, y0, z0, x1, y0, z0, x0, y0, z1, x1, y0, z1, x0, y1, z0, x1, y1, z0, x0, y1, z1, x1, y1, z1,
            x0, y0, z0, x0, y1, z0, x0, y0, z1, x0, y1, z1, x1, y0, z0, x1, y1, z0, x1, y0, z1, x1, y1, z1,
            x0, y0, z0, x0, y0, z1, x0, y1, z0, x0, y1, z1, x1, y0, z0, x1, y0, z1, x1, y1, z0, x1, y1, z1
         };
         float[] col1 =
         {
            val1, val0, val0, val1, val0, val0, val1, val0, val0, val1, val0, val0, 
            val1, val0, val0, val1, val0, val0, val1, val0, val0, val1, val0, val0,
            val0, val1, val0, val0, val1, val0, val0, val1, val0, val0, val1, val0, 
            val0, val1, val0, val0, val1, val0, val0, val1, val0, val0, val1, val0,
            val0, val0, val1, val0, val0, val1, val0, val0, val1, val0, val0, val1, 
            val0, val0, val1, val0, val0, val1, val0, val0, val1, val0, val0, val1
         };
         if (colorCorrection < 1)
            for (int n = 0; n < col1.length; n++)
               col1[n] *= colorCorrection;
         else
            for (int n = 0; n < col1.length; n++)
               if (col1[n] == 0)
               col1[n] = colorCorrection - 1;
         LineArray l1 =
                 new LineArray(24, LineArray.COORDINATES
                 | LineArray.COLOR_3);
         l1.setCoordinates(0, t1);
         l1.setColors(0, col1);
         l1.setCapability(LineArray.ALLOW_COUNT_READ);
         l1.setCapability(LineArray.ALLOW_FORMAT_READ);
         l1.setCapability(LineArray.ALLOW_COORDINATE_READ);
         OpenLineAttributes la1 =
                 new OpenLineAttributes(1.f, LineAttributes.PATTERN_SOLID, true);
         OpenShape3D tr1 = new OpenShape3D();
         OpenAppearance ap1 = new OpenAppearance();
         ap1.setLineAttributes(la1);
         tr1.setGeometry(l1);
         tr1.setAppearance(ap1);
         axes.addChild(tr1);
      }

//ticks
      nx = ny = nz = 0;
      gx = gy = gz = 0;
      if (drawX)
      {
         nx = 10 * rx.getNsteps();
         sx = rx.getStep();
         gx = (x1 - x0) / nx;
      }
      if (drawY)
      {
         ny = 10 * ry.getNsteps();
         sy = ry.getStep();
         gy = (y1 - y0) / ny;
      }
      if (drawZ)
      {
         nz = 10 * rz.getNsteps();
         sz = rz.getStep();
         gz = (z1 - z0) / nz;
      }

      int nGrid = nx + ny + nz;
      double[] t2 = new double[12 * nGrid];
      float[] col2 = new float[12 * nGrid];
      LineArray l2 =
              new LineArray(4 * nGrid,
              LineArray.COORDINATES
              | LineArray.COLOR_3);
      k = m = 0;
      if (drawX)
         for (i = 0; i < nx; i++, m++)
         {
            int s = 3;
            if (i % 5 == 0)
               s = 2;
            if (i % 10 == 0)
               s = 1;
            t2[3 * k] = t2[3 * k + 3] = t2[3 * k + 6] = t2[3 * k + 9] = x0 + i * gx;
            t2[3 * k + 1] = t[1] - gy / s;
            t2[3 * k + 4] = t[1] + gy / s;
            t2[3 * k + 7] = t2[3 * k + 10] = t[1];
            t2[3 * k + 2] = t2[3 * k + 5] = t[2];
            t2[3 * k + 8] = t[2] - gz / s;
            t2[3 * k + 11] = t[2] + gz / s;
            for (j = 0; j < 4; j++)
            {
               col2[3 * k + 3 * j]     = val1;
               col2[3 * k + 3 * j + 1] = val0;
               col2[3 * k + 3 * j + 2] = val0;
            }
            k += 4;
         }
      if (drawY)
         for (i = 0; i < ny; i++, m++)
         {
            int s = 3;
            if (i % 5 == 0)
               s = 2;
            if (i % 10 == 0)
               s = 1;
            t2[3 * k] = t2[3 * k + 3] = t[6];
            t2[3 * k + 6] = t[6] - gx / s;
            t2[3 * k + 9] = t[6] + gx / s;
            t2[3 * k + 1] = t2[3 * k + 4] = t2[3 * k + 7] = t2[3 * k + 10] = y0 + i * gy;
            t2[3 * k + 8] = t2[3 * k + 11] = t[8];
            t2[3 * k + 2] = t[8] - gz / s;
            t2[3 * k + 5] = t[8] + gz / s;
            for (j = 0; j < 4; j++)
            {
               col2[3 * k + 3 * j]     = val0;
               col2[3 * k + 3 * j + 1] = val1;
               col2[3 * k + 3 * j + 2] = val0;
            }
            k += 4;
         }
      if (drawZ)
         for (i = 0; i < nz; i++, m++)
         {
            int s = 3;
            if (i % 5 == 0)
               s = 2;
            if (i % 10 == 0)
               s = 1;
            t2[3 * k] = t2[3 * k + 3] = t[12];
            t2[3 * k + 6] = t[12] - gx / s;
            t2[3 * k + 9] = t[12] + gx / s;
            t2[3 * k + 1] = t[13] - gy / s;
            t2[3 * k + 4] = t[13] + gy / s;
            t2[3 * k + 7] = t2[3 * k + 10] = t[13];
            t2[3 * k + 2] = t2[3 * k + 5] = t2[3 * k + 8] = t2[3 * k + 11] = z0 + i * gz;
            for (j = 0; j < 4; j++)
            {
               col2[3 * k + 3 * j]     = val0;
               col2[3 * k + 3 * j + 1] = val0;
               col2[3 * k + 3 * j + 2] = val1;
            }
            k += 4;
         }
      
      l2.setCoordinates(0, t2);
      l2.setColors(0, col2);
      l2.setCapability(LineArray.ALLOW_COUNT_READ);
      l2.setCapability(LineArray.ALLOW_FORMAT_READ);
      l2.setCapability(LineArray.ALLOW_COORDINATE_READ);
      OpenShape3D tr2 = new OpenShape3D();
      OpenAppearance ap2 = new OpenAppearance();
      OpenLineAttributes la2 =
              new OpenLineAttributes(1.f, LineAttributes.PATTERN_SOLID, true);
      ap2.setLineAttributes(la2);
      tr2.setGeometry(l2);
      tr2.setAppearance(ap2);
      axes.addChild(tr2);
      
      nx = ny = nz = 0;
      gx = gy = gz = 0 ;
      nLabels = 0;
      if (drawX)
      {
         nx = rx.getNsteps();
         sx = rx.getStep();
         gx = (x1 - x0) / nx;
         nLabels += nx - 1;
      }
      if (drawY)
      {
         ny = ry.getNsteps();
         sy = ry.getStep();
         gy = (y1 - y0) / ny;
         nLabels += ny - 1;
      }
      if (drawZ)
      {
         nz = rz.getNsteps();
         sz = rz.getStep();
         gz = (z1 - z0) / nz;
         nLabels += nz - 1;
      }
      grid[0] = grid[0] && drawX;
      grid[1] = grid[1] && drawY;
      grid[2] = grid[2] && drawZ;
      if (grid[0] || grid[1] || grid[2])
      {
         LineArray l3 =
                 new LineArray(4 * nLabels, LineArray.COORDINATES | LineArray.COLOR_3);
         nGrid = 0;
         if (grid[0])
            nGrid += nx - 1;
         if (grid[1])
            nGrid += ny - 1;
         if (grid[2])
            nGrid += nz - 1;
         float[] t3 = new float[12 * nGrid];
         float[] col3 = new float[12 * nGrid];
         k = m = 0;
         if (grid[0])
         {
            for (i = 1; i < nx; i++, m++)
            {
               t3[3 * k] = t3[3 * k + 3] = t3[3 * k + 6] = t3[3 * k + 9] = x0 + i * gx;
               t3[3 * k + 1] = t3[3 * k + 4] = t3[3 * k + 7] = y0;
               t3[3 * k + 10] = y1;
               t3[3 * k + 2] = t3[3 * k + 8] = t3[3 * k + 11] = z0;
               t3[3 * k + 5] = z1;
               for (j = 0; j < 4; j++)
               {
                  col3[3 * k + 3 * j]     = val1;
                  col3[3 * k + 3 * j + 1] = val0;
                  col3[3 * k + 3 * j + 2] = val0;
               }
               k += 4;
            }
         }
         if (grid[1])
         {
            for (i = 1; i < ny; i++, m++)
            {
               t3[3 * k] = t3[3 * k + 3] = t3[3 * k + 6] = x0;
               t3[3 * k + 9] = x1;
               t3[3 * k + 1] = t3[3 * k + 4] = t3[3 * k + 7] = t3[3 * k + 10] = y0 + i * gy;
               t3[3 * k + 2] = t3[3 * k + 8] = t3[3 * k + 11] = z0;
               t3[3 * k + 5] = z1;
               for (j = 0; j < 4; j++)
               {
                  col3[3 * k + 3 * j]     = val0;
                  col3[3 * k + 3 * j + 1] = val1;
                  col3[3 * k + 3 * j + 2] = val0;
               }
               k += 4;
            }
         }
         if (grid[2])
         {
            for (i = 1; i < nz; i++, m++)
            {
               t3[3 * k] = t3[3 * k + 3] = t3[3 * k + 6] = x0;
               t3[3 * k + 9] = x1;
               t3[3 * k + 1] = t3[3 * k + 7] = t3[3 * k + 10] = y0;
               t3[3 * k + 4] = y1;
               t3[3 * k + 2] = t3[3 * k + 5] = t3[3 * k + 8] = t3[3 * k + 11] = z0 + i * gz;
               for (j = 0; j < 4; j++)
               {
                  col3[3 * k + 3 * j]     = val0;
                  col3[3 * k + 3 * j + 1] = val0;
                  col3[3 * k + 3 * j + 2] = val1;
               }
               k += 4;
            }
         }
         
         l3.setCoordinates(0, t3);
         l3.setColors(0, col3);
         l3.setCapability(LineArray.ALLOW_COUNT_READ);
         l3.setCapability(LineArray.ALLOW_FORMAT_READ);
         l3.setCapability(LineArray.ALLOW_COORDINATE_READ);
         OpenShape3D tr3 = new OpenShape3D();
         OpenAppearance ap3 = new OpenAppearance();
         OpenLineAttributes la3 = new OpenLineAttributes(1.f, LineAttributes.PATTERN_DOT, true);
         ap3.setLineAttributes(la3);
         tr3.setGeometry(l3);
         tr3.setAppearance(ap3);
         axes.addChild(tr3);
      }
      if (fontParams.isThreeDimensional())
      {
         BoundingSphere bSphere = new BoundingSphere();
         axisNames = null;
         Color3f paramColor = fontParams.getColor3f();
         Color3f red   = new Color3f(val1,  val0, val0);
         Color3f green = new Color3f(val0, val1,  val0);
         Color3f blue  = new Color3f(val0, val0, val1);
         axisLegends = null;
         if (drawX)
         {
            axes.addChild(TextBillboard.createBillboard(ad[0], fontParams, red, 1.4f,
                    new float[]{x1 + .3f * gx, t[1], t[2]}, Billboard.ROTATE_ABOUT_POINT, bSphere));
            for (i = 1, m = 0; i < nx; i++, m++)
               axes.addChild(TextBillboard.createBillboard(String.format(axFormats[0], rx.getLow() + i * sx), fontParams, red, 1,
                       new float[]{x0 + i * gx, t[1] - .3f * gy, t[2] - .3f * gz}, Billboard.ROTATE_ABOUT_POINT, bSphere));
         }
         if (drawY)
         {
            axes.addChild(TextBillboard.createBillboard(ad[1], fontParams, green, 1.4f,
                    new float[]{t[6], y1 + .3f * gy, t[8]}, Billboard.ROTATE_ABOUT_POINT, bSphere));
            for (i = 1; i < ny; i++, m++)
               axes.addChild(TextBillboard.createBillboard(String.format(axFormats[1], ry.getLow() + i * sy), fontParams, green, 1,
                       new float[]{t[6] - .3f * gx, y0 + i * gy, t[8] - .3f * gz}, Billboard.ROTATE_ABOUT_POINT, bSphere));
         }
         if (drawZ)
         {
            axes.addChild(TextBillboard.createBillboard(ad[2], fontParams, blue, 1.4f,
                    new float[]{t[12], t[13], z1 + .3f * gz}, Billboard.ROTATE_ABOUT_POINT, bSphere));
            for (i = 1; i < nz; i++, m++)
               axes.addChild(TextBillboard.createBillboard(String.format(axFormats[2], rz.getLow() + i * sz), fontParams, blue, 1,
                       new float[]{t[12] - .3f * gx, t[13] - .3f * gy, z0 + i * gz}, Billboard.ROTATE_ABOUT_POINT, bSphere));
         }
      }
      else
      {
         Font font = new Font(fontParams.getFontName(), fontParams.getFontType(), (int)(1.5 * fontParams.getFontSize()));
         axisNames = new CXYZString[drawn];
         int iAx = 0;
         if (drawX)
         {
            axisNames[iAx] = new CXYZString(ad[0], new Color(val1, val0, val0), x1 + .3f * gx, t[1], t[2], font);
            iAx += 1;
         }
         if (drawY)
         {
            axisNames[iAx] = new CXYZString(ad[1], new Color(val0, val1, val0), t[6], y1 + .3f * gy, t[8], font);
            iAx += 1;
         }
         if (drawZ)
         {
            axisNames[iAx] = new CXYZString(ad[2], new Color(val0, val0, val1), t[12], t[13], z1 + .3f * gz, font);
            iAx += 1;
         }
         
         
         font = new Font(fontParams.getFontName(), fontParams.getFontType(), fontParams.getFontSize());
         axisLegends = new CXYZString[nLabels];
         if (drawX)
            for (i = 1, m = 0; i < nx; i++, m++)
               axisLegends[m] = new CXYZString(String.format(axFormats[0], rx.getLow() + i * sx), 
                                               new Color(val1, val0, val0), 
                                               x0 + i * gx, t[1] - .3f * gy, t[2] - .3f * gz, font);
         if (drawY)
            for (i = 1; i < ny; i++, m++)
               axisLegends[m] = new CXYZString(String.format(axFormats[1], ry.getLow() + i * sy), 
                                               new Color(val0, val1, val0), 
                                               t[6] - .3f * gx, y0 + i * gy, t[8] - .3f * gz, font);
         if (drawZ)
            for (i = 1; i < nz; i++, m++)
               axisLegends[m] = new CXYZString(String.format(axFormats[2], rz.getLow() + i * sz), 
                                               new Color(val0, val0, val1), 
                                               t[12] - .3f * gx, t[13] - .3f * gy, z0 + i * gz, font);
      }
      geometryObj.addChild(axes);
      if (geometryObj.getCurrentViewer() != null)
         geometryObj.getCurrentViewer().refresh();
   }

   @Override
   public void drawLocal2D(J3DGraphics2D vGraphics, LocalToWindow ltw)
   {
      if (fontParams == null || fontParams.isThreeDimensional() || 
          ltw == null || vGraphics == null)
         return;
      if (axisLegends != null)
      {
         for (int i = 0; i < axisLegends.length; i++)
         {
            if (axisLegends[i] != null)
            {
               axisLegends[i].update(ltw);
               axisLegends[i].draw(vGraphics);
            }
         }
      }
      if (axisNames != null)
      {
         for (int i = 0; i < axisNames.length; i++)
            if (axisNames[i] != null)
            {
               axisNames[i].update(ltw);
               axisNames[i].draw(vGraphics);
            }
      }
   }
}
