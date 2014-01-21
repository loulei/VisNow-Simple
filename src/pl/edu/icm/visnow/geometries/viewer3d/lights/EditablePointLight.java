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

package pl.edu.icm.visnow.geometries.viewer3d.lights;

import javax.media.j3d.Bounds;
import javax.media.j3d.Light;
import javax.media.j3d.PointLight;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;

/**
 *
 * @author Krzysztof S. Nowinski
 * University of Warsaw, ICM
 */
public class EditablePointLight extends EditableLight
{
   protected Point3f position     = new Point3f(0,0,0);
   protected Point3f attenuation  = new Point3f(1,1,0);
   protected PointLight light     = new PointLight(false, lightColor, position, attenuation);

   public EditablePointLight(Color3f  lightCol, Bounds bounds, Point3f pos, Point3f att,
                             String name, boolean enbld)
   {
      type = POINT;
      lightName = name;
      if (pos!=null)
         position = pos;
      if (att!=null)
         attenuation = att;
      enabled = enbld;
      lightColor = lightCol;
      light = new PointLight(enabled, lightColor, position, attenuation);
      light.setCapability(Light.ALLOW_STATE_WRITE);
      light.setCapability(Light.ALLOW_COLOR_WRITE);
      light.setCapability(PointLight.ALLOW_ATTENUATION_WRITE);
      light.setCapability(PointLight.ALLOW_POSITION_WRITE);
      light.setInfluencingBounds(bounds);
   }

   public void setEnabled(boolean enabled)
   {
      this.enabled = enabled;
      light.setEnable(enabled);
   }

   public void setLightColor(Color3f lightCol)
   {
      lightColor = lightCol;
      light.setColor(lightColor);
   }

   public void setAttenuation(float constant, float linear, float quadratic)
   {
      attenuation = new Point3f(constant, linear, quadratic);
      light.setAttenuation(attenuation);
   }

   public float[] getAttenuation()
   {
      float[] a = new float[3];
      a[0] = attenuation.x;
      a[1] = attenuation.y;
      a[2] = attenuation.z;
      return a;
   }

   public PointLight getLight()
   {
      return light;
   }

}
