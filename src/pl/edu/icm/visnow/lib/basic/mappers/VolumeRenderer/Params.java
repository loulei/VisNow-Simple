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



package pl.edu.icm.visnow.lib.basic.mappers.VolumeRenderer;

import pl.edu.icm.visnow.engine.core.ParameterEgg;
import pl.edu.icm.visnow.engine.core.ParameterType;
import pl.edu.icm.visnow.engine.core.Parameters;
import pl.edu.icm.visnow.geometries.parameters.DataMappingParams;

/**
 *
 * @author  Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class Params extends Parameters
{
   private boolean outCroppedField;
   private DataMappingParams dataMappingParams = null;

   @SuppressWarnings("unchecked")
   public Params()
   {
      super(eggs);
   }
   private static ParameterEgg[] eggs = new ParameterEgg[]
   {
      new ParameterEgg<Boolean>("cropExtents", ParameterType.dependent, true),
      new ParameterEgg<Integer>("shadow", ParameterType.dependent, -1),
      new ParameterEgg<Float>("shadow range", ParameterType.dependent, Float.MAX_VALUE),
   };

   public boolean isOutCroppedField()
   {
      return outCroppedField;
   }

   public void setOutCroppedField(boolean outCroppedField)
   {
      this.outCroppedField = outCroppedField;
   }

   public boolean isCropExtents()
   {
      return (Boolean)getValue("cropExtents");
   }

   public void setCropExtents(boolean cropExtents)
   {
      setValue("cropExtents", cropExtents);
   }

   public DataMappingParams getDataMappingParams()
   {
      return dataMappingParams;
   }

   public void setDataMappingParams(DataMappingParams dataMappingParams)
   {
      this.dataMappingParams = dataMappingParams;
   }

   public float getShadow()
   {
      return (Float) getValue("shadow range");
   }

   public void setShadow(float shadow)
   {
      setValue("shadow range", shadow);
      fireStateChanged();
   }

   public int getShadowRange()
   {
      return (Integer) getValue("shadow");
   }

   public void setShadowRange(int shadowRange)
   {
      setValue("shadow", shadowRange);
      fireStateChanged();
   }
}
