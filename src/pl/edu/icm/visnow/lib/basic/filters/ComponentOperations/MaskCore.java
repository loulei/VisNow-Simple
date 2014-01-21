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

import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;

/**
 *
 * @author Bartosz Borucki (babor@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class MaskCore
{

   private Params params = null;
   private Field inField = null;
   protected RegularField outRegularField = null;
   protected Field outField = null;

   public MaskCore()
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
      boolean[] mask;
      if (outField == null)
         return;
      if (params.getMaskComponent() < 0 )
      {
         outField.setMask(inField.getMask());
         return;
      }
      int n = outField.getNNodes();
      mask = new boolean[n];
      if (inField.getMask() == null)
         for (int j = 0; j < n; j++)
            mask[j] = true;
      else
      {
         boolean[] inMask = inField.getMask();
         System.arraycopy(inMask, 0, mask, 0, n);
      }
         
      int i = params.getMaskComponent();
      float low = params.getMaskMin();
      float up = params.getMaskMax();

      if(i < inField.getNData()) {
            switch (inField.getData(i).getType())
            {
            case DataArray.FIELD_DATA_BYTE:
               byte[] inB = inField.getData(i).getBData();
               for (int j = 0; j < n; j++)
                  if ((0xFF & inB[j]) <= low || (0xFF & inB[j]) >= up)
                     mask[j] = false;
               break;
            case DataArray.FIELD_DATA_SHORT:
               short[] inS = inField.getData(i).getSData();
               for (int j = 0; j < n; j++)
                  if (inS[j] <= low || inS[j] >= up)
                     mask[j] = false;
               break;
            case DataArray.FIELD_DATA_INT:
               int[] inI = inField.getData(i).getIData();
               for (int j = 0; j < n; j++)
                  if (inI[j] <= low || inI[j] >= up)
                     mask[j] = false;
               break;
            case DataArray.FIELD_DATA_FLOAT:
               float[] inF = inField.getData(i).getFData();
               for (int j = 0; j < n; j++)
                  if (inF[j] <= low || inF[j] >= up)
                     mask[j] = false;
               break;
            case DataArray.FIELD_DATA_DOUBLE:
               double[] inD = inField.getData(i).getDData();
               for (int j = 0; j < n; j++)
                  if (inD[j] <= low || inD[j] >= up)
                     mask[j] = false;
               break;
            }
      } else if(i >= inField.getNData()) {
          //mask by coords
          int dim = i - inField.getNData();
          if (inField instanceof RegularField && ((RegularField) inField).getCoords() == null) {
              //by affine
              int[] dims = ((RegularField) inField).getDims();
              float[] p;
              switch (dims.length) {
                  case 3:
                      for (int z = 0, j = 0; z < dims[2]; z++) {
                          for (int y = 0; y < dims[1]; y++) {
                              for (int x = 0; x < dims[0]; x++, j++) {
                                  p = ((RegularField) inField).getGridCoords(x, y, z);
                                  if (p[dim] <= low || p[dim] >= up) {
                                      mask[j] = false;
                                  }
                              }
                          }
                      }
                      break;
                  case 2:
                      for (int y = 0, j = 0; y < dims[1]; y++) {
                          for (int x = 0; x < dims[0]; x++, j++) {
                              p = ((RegularField) inField).getGridCoords(x, y);
                              if (p[dim] <= low || p[dim] >= up) {
                                  mask[j] = false;
                              }
                          }
                      }
                      break;
                  case 1:
                      for (int x = 0; x < dims[0]; x++) {
                          p = ((RegularField) inField).getGridCoords(x);
                          if (p[dim] <= low || p[dim] >= up) {
                              mask[x] = false;
                          }
                      }
                      break;
              }
          } else {
              float[] coords = inField.getCoords();
              int nSpace = inField.getNSpace();
              for (int j = 0; j < n; j++) {
                  if (coords[nSpace * j + dim] <= low || coords[nSpace * j + dim] >= up) {
                      mask[j] = false;
                  }
              }
          }
      }
      outField.setMask(mask);
      if (params.isRecomputeMinMax())
         for (DataArray da: outField.getData())
            da.recomputeMinMax(mask);
   }

   Field getOutField()
   {
      return outField;
   }
}
