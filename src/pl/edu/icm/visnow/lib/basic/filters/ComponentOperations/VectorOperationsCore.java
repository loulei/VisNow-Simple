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
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class VectorOperationsCore
{
   private Params params = null;
   private Field inField = null;
   protected Field outField = null;
   
   public void setData(Field inField, Field outField, Params p)
   {
      this.inField  = inField;
      this.outField = outField;
      this.params   = p;
   }
   
   void update()
   {
      if (inField == null || outField == null)
         return;
      int n = outField.getNNodes();
      Vector<VectorComponent> components = params.getVectorComponents();
      if (components != null)
      {
         if (params.isFix3D())
         {
            for (int i = 0; i < components.size(); i++)
            {
               float[] data = new float[3 * n];
               for (int j = 0; j < data.length; j++)
                  data[i] = 0;
               for (int j = 0; j < 3; j++)
               {
                  int comp = components.get(i).getScalarComponents()[j];
                  if (comp == -1)
                     continue;
                  
                  if(comp == -100) {
                      for (int k = 0, l = j; k < n; k++, l += 3)
                         data[l] = 0.0f;
                  } else {
                      float[] inData = inField.getData(comp).getFData();
                      for (int k = 0, l = j; k < inData.length; k++, l += 3)
                         data[l] = inData[k];
                  }
               }
               outField.addData(DataArray.create(data, 3, components.get(i).getName()));
               if (components.get(i).isComputeNorm())
               {
                  float[] norms = new float[n];
                  for (int j = 0; j < n; j++)
                     norms[j] = (float) (Math.sqrt(data[3 * j] * data[3 * j] + data[3 * j + 1] * data[3 * j + 1] + data[3 * j + 2] * data[3 * j + 2]));
                  outField.addData(DataArray.create(norms, 1, components.get(i).getName() + "_norm"));
               }
            }
         } else
         {
            for (int i = 0; i < components.size(); i++)
            {
               int vlen = 0;
               for (int j = 0; j < 3; j++)
                  if (components.get(i).getScalarComponents()[j] > -1 || components.get(i).getScalarComponents()[j] == -100)
                     vlen += 1;
               if (vlen == 0)
                  continue;
               float[] data = new float[vlen * n];
               for (int j = 0, m = 0; j < 3; j++)
               {
                  int comp = components.get(i).getScalarComponents()[j];
                  if (comp == -1)
                     continue;
                  
                  if(comp == -100) {
                      for (int k = 0, l = m; k < n; k++, l += vlen)
                        data[l] = 0.0f;
                  } else {
                    float[] inData = inField.getData(comp).getFData();
                    for (int k = 0, l = m; k < inData.length; k++, l += vlen)
                        data[l] = inData[k];
                  }
                  m += 1;
               }
               outField.addData(DataArray.create(data, vlen, components.get(i).getName()));
               if (components.get(i).isComputeNorm())
               {
                  float[] norms = new float[n];
                  for (int j = 0; j < n; j++)
                  {
                     norms[j] = 0;
                     for (int k = 0; k < vlen; k++)
                        norms[j] += data[vlen * j + k] * data[vlen * j + k];
                     norms[j] = (float) (Math.sqrt(norms[j]));
                  }
                  outField.addData(DataArray.create(norms, 1, components.get(i).getName() + "_norm"));
               }
            }
         }
      }
      boolean[] vCN = params.getVCNorms();
      if (vCN != null)
         for (int i = 0, l = 0; i < inField.getNData(); i++)
            if (inField.getData(i).isSimpleNumeric() && inField.getData(i).getVeclen() > 1)
            {
               if (vCN[l])
               {
                  float[] data = inField.getData(i).getFData();
                  int vlen = inField.getData(i).getVeclen();
                  float[] norms = new float[n];
                  for (int j = 0; j < n; j++)
                  {
                     norms[j] = 0;
                     for (int k = 0; k < vlen; k++)
                        norms[j] += data[vlen * j + k] * data[vlen * j + k];
                     norms[j] = (float) (Math.sqrt(norms[j]));
                  }
                  outField.addData(DataArray.create(norms, 1, inField.getData(i).getName() + "_norm"));
               }
               l += 1;
            }
      boolean[] vCS = params.getCSplit();
      if (vCS != null)
         for (int i = 0, iv = 0; i < inField.getNData(); i++)
            if (inField.getData(i).isSimpleNumeric() && inField.getData(i).getVeclen() > 1)
            {
               if (vCS[iv])
               {
                  float[] data = inField.getData(i).getFData();
                  int vlen = inField.getData(i).getVeclen();
                  for (int j = 0; j < vlen; j++)
                  {
                     float[] cmp = new float[inField.getNNodes()];
                     for (int k = 0, l = j; k < cmp.length; k++, l += vlen)
                        cmp[k] = data[l];
                     outField.addData(DataArray.create(cmp, 1, inField.getData(i).getName() + "_"+j));
                  }
               }
               iv += 1;
            }
   }
   
   Field getOutField()
   {
      return outField;
   }
   
}
