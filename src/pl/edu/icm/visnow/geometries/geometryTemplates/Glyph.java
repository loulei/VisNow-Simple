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

package pl.edu.icm.visnow.geometries.geometryTemplates;

/**
 * @author  Krzysztof S. Nowinski (know@icm.edu.pl)
 * Warsaw University, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
abstract public class Glyph
/**
 *  Abstract class specifying templates for small geometric objects that can be used as glyphs.<p>
 *  Each subclass must have a constructor XxGlyph(Integer lod) providing data for building a glyph 
 *  with a specified level of detail lod. The glyph is a set of indexed geometry arrays of the type 
 * indicated by the getType() method
 * 
 */
{
   /**
    * glyph is formed by a series of line strips
    */
   public static final int LINE_STRIPS     = 0;
   /**
    * glyph is formed by a series of triangle strips
    */
   public static final int TRIANGLE_STRIPS = 1;
   /**
    * glyph is formed by a series of triangle fans
    */
   public static final int TRIANGLE_FANS   = 2;
   /**
    * number of vertices (coords triples)
    */
   protected int   nverts;
   /**
    * number of indices (vertices of rendered triangles/lines
    */
   protected int   ninds;
   /**
    * number of polytriangle strips/fans
    */
   protected int   nstrips;
   /**
    * numbers of vertices in each triangle strip/fan
    */
   protected int[] strips;
   /**
    * number of polylines
    */
   protected int   nlstrips;
   /**
    * numbers of vertices in each polyline
    */
   protected int[] lstrips;
   /**
    * vertex coordinates <p>
    * (recommended normalization to -1...1, (0,0,0) will be used as grid origin,
    * z - coordinate is will be used as vector direction
    *
    */
   protected float verts[];
   /**
    * normals coordinates
    */
   protected float normals[];
   /**
    * indices to points in all strips
    */
   protected int   pntsIndex[];
   /**
    * indices to colors (array of length ninds filled by zeros)
    */
   protected int   clrsIndex[];

   
   /**
    * 
    * @return glyph name for guis
    */
   abstract public String getName();
   /**
    * 
    * @return glyph type (one of LINE_STRIPS, TRIANGLE_STRIPS or TRIANGLE_FANS)
    */
   abstract public int getType();
   /**
    * 
    * @return true if various levels of detail (smoothness) are possible
    */
   abstract public boolean isRefinable();

   /**
    * 
    * @return  indices to colors
    */
   public int[] getClrsIndex()
   {
      return clrsIndex;
   }

   /**
    * 
    * @return numbers of vertices in each polyline
    */
   public int[] getLstrips()
   {
      return lstrips;
   }

   /**
    * 
    * @return number of indices
    */
   public int getNinds()
   {
      return ninds;
   }

   /**
    * 
    * @return number of polylines
    */
   public int getNlstrips()
   {
      return nlstrips;
   }

   /**
    * 
    * @return array of normals
    */
   public float[] getNormals()
   {
      return normals;
   }

   /**
    * 
    * @return number of triangle strips
    */
   public int getNstrips()
   {
      return nstrips;
   }

   /**
    * 
    * @return number of vertices
    */
   public int getNverts()
   {
      return nverts;
   }

   /**
    * 
    * @return
    */
   public int[] getPntsIndex()
   {
      return pntsIndex;
   }

   /**
    * 
    * @return
    */
   public int[] getStrips()
   {
      return strips;
   }

   /**
    * 
    * @return coordinates of vertices
    */
   public float[] getVerts()
   {
      return verts;
   }
   
   
}
