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

import java.awt.Color;
import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.geometries.events.ColorEvent;
import pl.edu.icm.visnow.geometries.events.ColorListener;
import pl.edu.icm.visnow.geometries.objects.generics.OpenBranchGroup;
import pl.edu.icm.visnow.geometries.objects.generics.OpenTransformGroup;
import pl.edu.icm.visnow.geometries.parameters.TransformParams;
import pl.edu.icm.visnow.system.main.VisNow;

/**
 *
 * @author Krzysztof S. Nowinski
 *   <p>   University of Warsaw, ICM
 */
abstract public class FieldGeometry extends DataMappedGeometryObject
{
   protected Field field;
   
   protected OpenBranchGroup geometries = new OpenBranchGroup();
   protected TransformParams transformParams = null;
   protected OpenTransformGroup transformedGeometries = new OpenTransformGroup();
   
   public FieldGeometry()
   {
      this("");
   }
   
   public FieldGeometry(String name)
   {
      this.name = name;
      debug = VisNow.isDebug();
      backgroundColorListener = new ColorListener()
      {
         @Override
         public void colorChoosen(ColorEvent e)
         {
            fireStateChanged(e.getSelectedColor());
         }
      };
   }

   public boolean isNewParams()
   {
      return newParams;
   }
   
   public void fireStateChanged(Color color)
   {
      ColorEvent e = new ColorEvent(this, color);
      for (ColorListener colorListener : bgrColorListenerList)
         colorListener.colorChoosen(e);
   }
 
   abstract public void createGeometry(Field inField);
   abstract public OpenBranchGroup getGeometry(Field inField);
   abstract public void updateGeometry(Field inField);
   abstract public void updateGeometry();
   abstract public void updateCoords(boolean force);
   abstract public void updateCoords(float[] coords);
   abstract public void updateCoords();
   abstract public void updateDataMap();
   abstract public OpenBranchGroup getGeometry();
   abstract public boolean setField(Field inField, boolean createParams);
   abstract public boolean setField(Field inField);
   abstract public Field getField();
}
