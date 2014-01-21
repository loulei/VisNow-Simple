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

package pl.edu.icm.visnow.lib.basic.filters.ComponentOperations;

import java.util.Vector;
import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.datasets.dataarrays.ComplexDataArray;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;

/**
 *
 * @author Bartosz Borucki (babor@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class ComplexCore
{

   private Params params = null;
   private Field inField = null;
   protected RegularField outRegularField = null;
   protected Field outField = null;

   public ComplexCore()
   {
   }

   public void setData(Field inField, Field outField, Params p)
   {
      this.inField = inField;
      this.outField = outField;
      this.params = p;
   }


   void update()
   {
      if (inField == null || params == null)
      {
         outField = null;
         return;
      }

       int nComplexComps = 0;
       int nNonComplexComps = 0;
       for (int i = 0; i < inField.getNData(); i++) {
           if(inField.getData(i).getType() == DataArray.FIELD_DATA_COMPLEX) {
               nComplexComps++;
           } else if(inField.getData(i).getVeclen() == 1) {
               nNonComplexComps++;
           }           
       }
       
       if(nNonComplexComps > 0) {
           Vector<ComplexComponent> cc = params.getComplexCombineComponents();
           for (int i = 0; i < cc.size(); i++) {
               outField.addData(DataArray.create(inField.getData(cc.get(i).getRealComponent()).getFData(), inField.getData(cc.get(i).getImagComponent()).getFData(), 1, cc.get(i).getName(), null, null));
           }
       }
       
       if(nComplexComps > 0) {
           boolean[] splitRe =  params.getComplexSplitRe();
           if(splitRe == null || splitRe.length != nComplexComps) {
               splitRe = new boolean[nComplexComps];
               for (int i = 0; i < splitRe.length; i++) {
                   splitRe[i] = false;                   
               }
           }
           
           boolean[] splitIm =  params.getComplexSplitIm();
           if(splitIm == null || splitIm.length != nComplexComps) {
               splitIm = new boolean[nComplexComps];
               for (int i = 0; i < splitIm.length; i++) {
                   splitIm[i] = false;                   
               }
           }
                      
           boolean[] splitAbs =  params.getComplexSplitAbs();
           if(splitAbs == null || splitAbs.length != nComplexComps) {
               splitAbs = new boolean[nComplexComps];
               for (int i = 0; i < splitAbs.length; i++) {
                   splitAbs[i] = false;                   
               }
           }
           
           boolean[] splitArg =  params.getComplexSplitArg();
           if(splitArg == null || splitArg.length != nComplexComps) {
               splitArg = new boolean[nComplexComps];
               for (int i = 0; i < splitArg.length; i++) {
                   splitArg[i] = false;                   
               }
           }

           int c = 0;
           for (int i = 0; i < inField.getNData(); i++) {
               if(inField.getData(i).getType() != DataArray.FIELD_DATA_COMPLEX)
                   continue;
               
               if(splitRe[c])
                   outField.addData(DataArray.create(((ComplexDataArray)inField.getData(i)).getFRealData(), inField.getData(i).getVeclen(), "Re_"+inField.getData(i).getName()));
               if(splitIm[c])
                   outField.addData(DataArray.create(((ComplexDataArray)inField.getData(i)).getFImagData(), inField.getData(i).getVeclen(), "Im_"+inField.getData(i).getName()));
               if(splitAbs[c])
                   outField.addData(DataArray.create(((ComplexDataArray)inField.getData(i)).getFAbsData(), inField.getData(i).getVeclen(), "Abs_"+inField.getData(i).getName()));
               if(splitArg[c])
                   outField.addData(DataArray.create(((ComplexDataArray)inField.getData(i)).getFArgData(), inField.getData(i).getVeclen(), "Arg_"+inField.getData(i).getName()));
               
               c++;
           }
       }
   }
   
   Field getOutField()
   {
      return outField;
   }
}
