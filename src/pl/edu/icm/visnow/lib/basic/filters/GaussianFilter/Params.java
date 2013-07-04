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

package pl.edu.icm.visnow.lib.basic.filters.GaussianFilter;

import pl.edu.icm.visnow.engine.core.ParameterEgg;
import pl.edu.icm.visnow.engine.core.ParameterType;
import pl.edu.icm.visnow.engine.core.Parameters;

/**
 *
 * @author Krzysztof S. Nowinski
 *         Warsaw University, ICM
 */
public class Params extends Parameters
{

   private void initParams()
   {
      setValue("sigma", new Float(10.f));
      setValue("sigma1", new Float(1.f));
      setValue("hiPass", new Boolean(false));
      setValue("bandPass", new Boolean(false));
      setValue("hiAbs", new Boolean(false));
      setValue("bandAbs", new Boolean(false));
      setValue("auto", new Boolean(false));
      setValue("nThreads", new Integer(1));
   }

   public Params()
   {
      super(eggs);
      initParams();
   }
   private static ParameterEgg[] eggs = new ParameterEgg[]
   {
      new ParameterEgg<Float>("sigma", ParameterType.dependent),
      new ParameterEgg<Float>("sigma1", ParameterType.dependent),
      new ParameterEgg<Boolean>("hiPass", ParameterType.dependent),
      new ParameterEgg<Boolean>("bandPass", ParameterType.dependent),
      new ParameterEgg<Boolean>("hiAbs", ParameterType.dependent),
      new ParameterEgg<Boolean>("bandAbs", ParameterType.dependent),
      new ParameterEgg<Boolean>("auto", ParameterType.dependent),
      new ParameterEgg<Integer>("nThreads", ParameterType.dependent)
   };

   public Float getSigma()
   {
      return (Float) getValue("sigma");
   }

   public void setSigma(float sigma)
   {
      setValue("sigma", sigma);
   }

   public Float getSigma1()
   {
      return (Float) getValue("sigma1");
   }

   public void setSigma1(float sigma1)
   {
      setValue("sigma1", sigma1);
   }

   public boolean isHiPass()
   {
      return (Boolean) getValue("hiPass");
   }

   public void setHiPass(boolean hiPass)
   {
      setValue("hiPass", hiPass);
   }

   public boolean isBandPass()
   {
      return (Boolean) getValue("bandPass");
   }

   public void setBandPass(boolean bandPass)
   {
      setValue("bandPass", bandPass);
   }

   public boolean isHiAbs()
   {
      return (Boolean) getValue("hiAbs");
   }

   public void setHiAbs(boolean hiAbs)
   {
      setValue("hiAbs", hiAbs);
   }

   public boolean isBandAbs()
   {
      return (Boolean) getValue("bandAbs");
   }

   public void setBandAbs(boolean bandAbs)
   {
      setValue("bandAbs", bandAbs);
   }

    public boolean isAuto()
    {
      return (Boolean) getValue("auto");
   }

   public void setAuto(boolean auto)
   {
      setValue("auto", auto);
   }

  public int getNThreads()
   {
      return (Integer) getValue("nThreads");
   }

   public void setNThreads(int nThreads)
   {
      setValue("nThreads", nThreads);
   }
}
