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

package pl.edu.icm.visnow.lib.basic.readers.VolumeReader;

import pl.edu.icm.visnow.engine.core.ParameterEgg;
import pl.edu.icm.visnow.engine.core.ParameterType;
import pl.edu.icm.visnow.engine.core.Parameter;
import pl.edu.icm.visnow.lib.basic.readers.ReaderParams;

/**
 *
 * @author  Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class Params extends ReaderParams
{

   public static final int FROM_FILE    = 0;
   public static final int FROM_INDICES = 1;
   public static final int NORMALIZED   = 2;
   public static final int USER_EXTENTS = 3;
   public static final int USER_AFFINE  = 4;

   private Parameter<float[]> scale;
   private Parameter<float[]> orig;
   private Parameter<float[]> min;
   private Parameter<float[]> max;
   private Parameter<Integer> type;

   @SuppressWarnings("unchecked")
   public Params()
   {
      super(getEggs());
      scale = getParameter("scale");
      scale.setValue(new float[] {1, 1, 1});
      orig = getParameter("orig");
      orig.setValue(new float[] {0, 0, 0});
      min = getParameter("min");
      min.setValue(new float[] {-1, -1, -1});
      max = getParameter("max");
      max.setValue(new float[] {1, 1, 1});
      type = getParameter("type");
      type.setValue(0);
   }
   private static ParameterEgg[] eggs = new ParameterEgg[]
   {
      new ParameterEgg<float[]>("scale", ParameterType.independent),
      new ParameterEgg<float[]>("orig", ParameterType.independent),
      new ParameterEgg<float[]>("min", ParameterType.independent),
      new ParameterEgg<float[]>("max", ParameterType.independent),
      new ParameterEgg<Integer>("type", ParameterType.independent)

   };

   private static ParameterEgg[] getEggs()
   {
      return eggs;
   }

   public float[] getScale()
   {
      return scale.getValue();
   }

   public void setScale(float[] scale)
   {
      this.scale.setValue(scale);
   }

   public float[] getOrig()
   {
      return orig.getValue();
   }

   public void setOrig(float[] orig)
   {
      this.orig.setValue(orig);
   }
   
   public float[] getMin()
   {
      return min.getValue();
   }

   public void setMin(float[] min)
   {
      this.min.setValue(min);
   }

   public float[] getMax()
   {
      return max.getValue();
   }

   public void setOMax(float[] max)
   {
      this.max.setValue(max);
   }

   public int getType()
   {
      return type.getValue();
   }

   public void setType(int type)
   {
      this.type.setValue(type);
   }
}
