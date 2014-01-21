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
import java.awt.*;
import javax.media.j3d.*;
import pl.edu.icm.visnow.geometries.parameters.FontParams;

/**
 *
 * @author  Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class CXYString
{
   protected String s;
   protected Color c = null;
   protected int fontSize = 12;
   protected Font font = new Font("Dialog",Font.PLAIN, fontSize);
   protected float[] ccomps = new float[3];
   protected int[] sCoords = new int[2];
   protected float depth=1.f;
   protected float relativeHeight = .01f;

   /** Creates a new instance of CXYString */

   public CXYString(String s, Color c, Font font, float relativeHeight)
   {
      this.font = font;
      this.relativeHeight = relativeHeight;
      this.s = s;
      this.c = c;
      ccomps = c.getRGBColorComponents(ccomps);
   }

   public CXYString(String s, FontParams params)
   {
      this.s = s;
      c = params.getColor();
      font = params.getFont2D();
      relativeHeight = params.getSize();
      ccomps = c.getRGBColorComponents(ccomps);
   }

   public CXYString(String s, Color c)
   {
      this.s = s;
      this.c = c;
      ccomps = c.getRGBColorComponents(ccomps);
   }


   @Override
   public String toString()
   {
      return s+" at("+sCoords[0]+","+sCoords[1]+")";
   }

   public String getString()
   {
      return s;
   }


   public void setColor(Color c)
   {
      if (c==null)
         return;
      this.c = c;
      ccomps = c.getRGBColorComponents(ccomps);
   }

   /**
    *
    * @param vGraphics
    */
   public void draw(J3DGraphics2D vGraphics, int w, int h)
   {
      if (s==null || s.length()<1)
         return;
      Font f = vGraphics.getFont();
      Font ft = new Font(font.getFontName(), font.getStyle(), (int)(h * relativeHeight));
      vGraphics.setFont(ft);
      vGraphics.setColor(new Color(depth*ccomps[0],depth*ccomps[1],depth*ccomps[2]));
      vGraphics.drawString(s,sCoords[0],sCoords[1]);
      vGraphics.setFont(f);
   }

   public void draw(J3DGraphics2D vGraphics, int xpos, int ypos, int w, int h)
   {
      if (s==null || s.length()<1)
         return;
      Font f = vGraphics.getFont();
      Font ft = new Font(font.getFontName(), font.getStyle(), (int)(h * relativeHeight));
      vGraphics.setFont(ft);
      vGraphics.setColor(new Color(depth*ccomps[0],depth*ccomps[1],depth*ccomps[2]));
      vGraphics.drawString(s,xpos,ypos);
      vGraphics.setFont(f);
   }
}
