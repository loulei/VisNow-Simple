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

package pl.edu.icm.visnow.geometries.objects;

import com.sun.j3d.utils.geometry.Text2D;
import javax.media.j3d.Billboard;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import pl.edu.icm.visnow.geometries.parameters.FontParams;

/**
 *
 * @author know
 */
public class TextBillboard
{

   public static TransformGroup createBillboard(String text,
           FontParams fontParams,
           float[] coords, int nMode, BoundingSphere bounds)
   {
      return createBillboard(text, fontParams, fontParams.getColor3f(), 1.0f, coords, nMode, bounds);
   }

   public static TransformGroup createBillboard(String text,
           FontParams fontParams, Color3f color, float scale,
           float[] coords, int nMode, BoundingSphere bounds)
   {
      TransformGroup subTg = new TransformGroup();
      subTg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
      Text2D ti = new Text2D(text, color, fontParams.getFontName(), fontParams.getFontSize(), fontParams.getFontType());
      ti.setRectangleScaleFactor(scale * fontParams.getFont3DSize() / fontParams.getFontSize());
      float[] m = new float[16];
      for (int j = 0; j < m.length; j++)
         m[j] = 0;
      m[15] = 1;
      for (int j = 0; j < 3; j++)
      {
         m[5 * j] = 1;
         m[4 * j + 3] = coords[j];
      }
      TransformGroup tGr = new TransformGroup();
      tGr.setTransform(new Transform3D(m));
      tGr.addChild(ti);
      subTg.addChild(tGr);
      Billboard billboard = new Billboard(subTg, nMode, new Point3f(coords));
      billboard.setSchedulingBounds(bounds);
      subTg.addChild(billboard);
      return subTg;
   }
   
}
