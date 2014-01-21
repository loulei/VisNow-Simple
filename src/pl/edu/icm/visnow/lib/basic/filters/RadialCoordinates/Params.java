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

package pl.edu.icm.visnow.lib.basic.filters.RadialCoordinates;

import pl.edu.icm.visnow.engine.core.ParameterEgg;
import pl.edu.icm.visnow.engine.core.ParameterType;
import pl.edu.icm.visnow.engine.core.Parameters;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class Params extends Parameters
{
   public static final int SPHERICAL   = 0;
   public static final int CYLINDRICAL = 1;
   public static final int CONSTANT = -1;
   static final float s = (float)(Math.PI/180);


   private static ParameterEgg[] eggs = new ParameterEgg[]
   {
      new ParameterEgg<Integer>("mapType", ParameterType.dependent, SPHERICAL),
      new ParameterEgg<Integer>("rCoord", ParameterType.dependent, 0),
      new ParameterEgg<Float>("rMin", ParameterType.dependent, 0.f),
      new ParameterEgg<Float>("rMax", ParameterType.dependent, 1.f),
      new ParameterEgg<Float>("zMin", ParameterType.dependent, 0.f),
      new ParameterEgg<Float>("zMax", ParameterType.dependent, 1.f),
      new ParameterEgg<Integer>("phiCoord", ParameterType.dependent, 1),
      new ParameterEgg<Float>("phiMin", ParameterType.dependent, 0.f),
      new ParameterEgg<Float>("phiMax", ParameterType.dependent, 90.f),
      new ParameterEgg<Integer>("psiCoord", ParameterType.dependent, 2),
      new ParameterEgg<Float>("psiMin", ParameterType.dependent, 0.f),
      new ParameterEgg<Float>("psiMax", ParameterType.dependent, 180.f),
      new ParameterEgg<Boolean>("adjusting", ParameterType.dependent, false),
   };

   public Params()
   {
      super(eggs);
      setValue("mapType",SPHERICAL);
      setValue("rCoord",0);
      setValue("phiCoord",1);
      setValue("psiCoord",2);
      setValue("rMin",0.f);
      setValue("rMax",1.f);
      setValue("zMin",0.f);
      setValue("zMax",1.f);
      setValue("phiMin",0.f);
      setValue("phiMax",90.f);
      setValue("psiMin",0.f);
      setValue("psiMax",180.f);
   }

   public int getMapType()
   {
      return (Integer)getValue("mapType");
   }

   public void setMapType(int mapType)
   {
      setValue("mapType",mapType);
   }

   public int getRCoord()
   {
      return (Integer)getValue("rCoord");
   }

   public void setRCoord(int rCoord)
   {
      setValue("rCoord",rCoord);
   }

   public float getRMin()
   {
      return (Float)getValue("rMin");
   }
   public void setRMin(float rMin)
   {
      setValue("rMin",rMin);
   }

   public float getRMax()
   {
      return(Float) getValue("rMax");
   }

   public void setRMax(float rMax)
   {
      setValue("rMax",rMax);
   }

   public float getZMin()
   {
      return (Float)getValue("zMin");
   }

   public void setZMin(float zMin)
   {
      setValue("zMin",zMin);
   }

   public float getZMax()
   {
      return(Float) getValue("zMax");
   }

   public void setZMax(float zMax)
   {
      setValue("zMax",zMax);
   }

   public int getPhiCoord()
   {
      return (Integer)getValue("phiCoord");
   }

   public void setPhiCoord(int phiCoord)
   {
      setValue("phiCoord",phiCoord);
   }

   public float getPhiMin()
   {
      return s*(Float)getValue("phiMin");
   }

   public void setPhiMin(float phiMin)
   {
      setValue("phiMin",phiMin);
   }

   public float getPhiMax()
   {
      return s*(Float)getValue("phiMax");
   }

   public void setPhiMax(float phiMax)
   {
      setValue("phiMax",phiMax);
   }

   public int getPsiCoord()
   {
      return (Integer)getValue("psiCoord");
   }

   public void setPsiCoord(int psiCoord)
   {
      setValue("psiCoord",psiCoord);
   }

   public float getPsiMin()
   {
      return s*(Float)getValue("psiMin");
   }

   public void setPsiMin(float psiMin)
   {
      setValue("psiMin",psiMin);
   }

   public float getPsiMax()
   {
      return s*(Float)getValue("psiMax");
   }

   public void setPsiMax(float psiMax)
   {
      setValue("psiMax",psiMax);
   }

   public boolean isAdjusting()
   {
      return (Boolean)getValue("adjusting");
   }

   public void setAdjusting(boolean adjusting)
   {
      setValue("adjusting",adjusting);
   }
}
