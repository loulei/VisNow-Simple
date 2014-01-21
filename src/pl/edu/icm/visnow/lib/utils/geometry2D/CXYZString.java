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

package pl.edu.icm.visnow.lib.utils.geometry2D;
import javax.vecmath.*;
import java.awt.*;
import pl.edu.icm.visnow.geometries.parameters.FontParams;

/**
 *
 * @author  Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class CXYZString extends CXYString
{
   private float x = 0;
   private float y = 0;
   private float z = 0;

   /** Creates a new instance of CXYZString */
   public CXYZString(String s, Color c, float x, float y, float z, Font font, float relativeHeight)
   {
      super(s, c, font, relativeHeight);
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public CXYZString(String s, float x, float y, float z, FontParams params)
   {
      this(s, params.getColor(), x, y, z, params.getFont2D(), params.getSize());
   }

   public CXYZString(String s, Color c)
   {
      super(s,c);
   }

   public CXYZString(String s)
   {
      this(s,Color.WHITE);
   }

   public void setCoords(float x, float y, float z)
   {
      this.x = x;
      this.y = y;
      this.z = z;
   }


   public void update(pl.edu.icm.visnow.geometries.utils.transform.LocalToWindow ltw)
   {
      if (ltw==null) return;
      depth = ltw.transformPt(new Point3d((double)x,(double)y,(double)z),sCoords);
      if (depth>1.f) depth=1.f;
      if (depth<.01f) depth=.01f;
   }
}
