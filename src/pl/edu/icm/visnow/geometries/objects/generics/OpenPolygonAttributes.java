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

package pl.edu.icm.visnow.geometries.objects.generics;

import javax.media.j3d.PolygonAttributes;

/**
 *
 * @author Krzysztof S. Nowinski
 *   <p>   University of Warsaw, ICM
 */
public class OpenPolygonAttributes extends PolygonAttributes
{
   public OpenPolygonAttributes()
   {
      setCapability(ALLOW_CULL_FACE_READ);
      setCapability(ALLOW_CULL_FACE_WRITE);
      setCapability(ALLOW_MODE_READ);
      setCapability(ALLOW_MODE_WRITE);
      setCapability(ALLOW_NORMAL_FLIP_READ);
      setCapability(ALLOW_NORMAL_FLIP_WRITE);
      setCapability(ALLOW_OFFSET_READ);
      setCapability(ALLOW_OFFSET_WRITE);
      setCullFace(CULL_NONE);
      setBackFaceNormalFlip(true);
   }
   
   public OpenPolygonAttributes(int mode, int cull, float offset,  boolean flip, float offsetFactor)
   {
      super(mode, cull, offset, flip, offsetFactor);
      setCapability(ALLOW_CULL_FACE_READ);
      setCapability(ALLOW_CULL_FACE_WRITE);
      setCapability(ALLOW_MODE_READ);
      setCapability(ALLOW_MODE_WRITE);
      setCapability(ALLOW_NORMAL_FLIP_READ);
      setCapability(ALLOW_NORMAL_FLIP_WRITE);
      setCapability(ALLOW_OFFSET_READ);
      setCapability(ALLOW_OFFSET_WRITE);
      setBackFaceNormalFlip(true);
   }
   
   public OpenPolygonAttributes(int mode, int cull, float offset,  boolean flip, float offsetFactor, boolean backFaceNormalFlip)
   {
      super(mode, cull, offset, flip, offsetFactor);
      setCapability(ALLOW_CULL_FACE_READ);
      setCapability(ALLOW_CULL_FACE_WRITE);
      setCapability(ALLOW_MODE_READ);
      setCapability(ALLOW_MODE_WRITE);
      setCapability(ALLOW_NORMAL_FLIP_READ);
      setCapability(ALLOW_NORMAL_FLIP_WRITE);
      setCapability(ALLOW_OFFSET_READ);
      setCapability(ALLOW_OFFSET_WRITE);
      setBackFaceNormalFlip(backFaceNormalFlip);
   }
   
   @Override
   public OpenPolygonAttributes cloneNodeComponent(boolean forceDuplicate)
   {
      OpenPolygonAttributes openPolygonAttributes = new OpenPolygonAttributes();
      openPolygonAttributes.duplicateNodeComponent(this, forceDuplicate);
      return openPolygonAttributes;
   }
   
}
