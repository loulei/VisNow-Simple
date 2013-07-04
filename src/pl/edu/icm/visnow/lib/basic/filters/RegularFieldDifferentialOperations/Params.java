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

package pl.edu.icm.visnow.lib.basic.filters.RegularFieldDifferentialOperations;

import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.datasets.RegularField;
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
   public static final int GRADIENT            = 0;
   public static final int GRADIENT_NORM       = 1;
   public static final int NORMALIZED_GRADIENT = 2;
   public static final int LAPLACIAN           = 3;
   public static final int HESSIAN             = 4;
   public static final int HESSIAN_EIG         = 5;
   public static final int DERIV               = 0;
   public static final int DIV                 = 1;
   public static final int ROT                 = 2;
   private static ParameterEgg[] eggs = new ParameterEgg[]
   {
      new ParameterEgg<int[]>("ScalarComponents", ParameterType.dependent, null),
      new ParameterEgg<int[]>("VectorComponents", ParameterType.dependent, null),
      new ParameterEgg<boolean[][]>("ScalarOperations", ParameterType.dependent, null),
      new ParameterEgg<boolean[][]>("VectorOperations", ParameterType.dependent, null),
      new ParameterEgg<Boolean>("autorun", ParameterType.dependent, true),
      new ParameterEgg<Integer>("threads", ParameterType.dependent,
      Runtime.getRuntime().availableProcessors())
   };

   public Params()
   {
      super(eggs);
   }
   
   public void setInfield(RegularField inField)
   {  
      int[] sCom = null, vCom = null;
      boolean[][] sOp = null, vOp = null;
      if (inField == null)
         return;
      int nScalarComponents = 0, nVectorComponents = 0;
      for (DataArray da: inField.getData())
      {
         if (da.isSimpleNumeric() && da.getVeclen() == 1)
            nScalarComponents += 1;
         if (da.isSimpleNumeric() && da.getVeclen() == inField.getDims().length)
            nVectorComponents += 1;
      }
      if (nScalarComponents > 0)
      {
         sCom = new int[nScalarComponents];
         sOp  = new boolean[nScalarComponents][6];
      }
      if (nVectorComponents > 0)
      {
         vCom = new int[nVectorComponents];
         vOp  = new boolean[nVectorComponents][3];
      }
      int is = 0, iv = 0;
      for (int i = 0; i < inField.getNData(); i++)
      {
         DataArray da = inField.getData(i);
         if (da.isSimpleNumeric() && da.getVeclen() == 1)
         {
            sCom[is] = i;
            is += 1;
         }
         if (da.isSimpleNumeric() && da.getVeclen() == inField.getDims().length)
         {
            vCom[iv] = i;
            iv += 1;
         }
      }
      setScalarComponents(sCom);
      setScalarOperations(sOp);
      setVectorComponents(vCom);
      setVectorOperations(vOp);
   }

   /**
    * Get the value of threads
    *
    * @return the value of threads
    */
   public final int getThreads()
   {
      return (Integer) getValue("threads");
   }

   /**
    * Set the value of threads
    *
    * @param threads new value of threads
    */
   public final void setThreads(int threads)
   {
      setValue("threads", threads);
   }

   public final boolean[][] getScalarOperations()
   {
      return (boolean[][]) getValue("ScalarOperations");
   }

   public final void setScalarOperations(boolean[][] operations)
   {
      setValue("ScalarOperations", operations);
   }
   
   public final boolean[][] getVectorOperations()
   {
      return (boolean[][]) getValue("VectorOperations");
   }

   public final void setVectorOperations(boolean[][] operations)
   {
      setValue("VectorOperations", operations);
   }


   public final int[] getScalarComponents()
   {
      return (int[]) getValue("ScalarComponents");
   }

   public final void setScalarComponents(int[] scalarComponents)
   {
      setValue("ScalarComponents", scalarComponents);
   }
   
   public final int[] getVectorComponents()
   {
      return (int[]) getValue("VectorComponents");
   }

   public final void setVectorComponents(int[] VectorComponents)
   {
      setValue("VectorComponents", VectorComponents);
   }
   
   public boolean isAutoRun()
   {
      return (Boolean)getValue("autorun");
   }
   
   public void setAutoRun(boolean autorun)
   {
      setValue("autorun", autorun);
   }
}
