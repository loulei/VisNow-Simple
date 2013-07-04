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
import static pl.edu.icm.visnow.lib.utils.numeric.FiniteDifferences.Derivatives.*;
import pl.edu.icm.visnow.lib.utils.numeric.FiniteDifferences.InvertedJacobian;
import pl.edu.icm.visnow.lib.utils.numeric.PointwiseLinearAlgebra2D;
import pl.edu.icm.visnow.lib.utils.numeric.PointwiseLinearAlgebra3D;

/**
 *
 * @author Krzysztof S. Nowinski
 *   <p>   University of Warsaw, ICM
 */
public class RegularFieldDifferentialOps
{

   private static final int SCALAR      = 0;
   private static final int VECTOR      = 1;
   private static final int MATRIX      = 2;
   private static final int SYMMATRIX   = 3;
   private static final int FORM2       = 4;
   public static final String[] scalarSuffixes = new String[]
   {
      "_grad", "_gradnorm", "_normgrad", "_lapl", "_hessian"
   };
   private static final int[] scalarTypes = new int[]
   {
      VECTOR, SCALAR, VECTOR, SCALAR, SYMMATRIX
   };
   public static final String[] vectorSuffixes = new String[]
   {
      "_deriv", "_div", "_rot"
   };
   private static final int[] vectorTypes = new int[]
   {
      MATRIX, SCALAR, FORM2
   };
   private RegularField inField;
   private RegularField outField = null;
   private Params params;
   private int[] dims = null;
   private int dim = 0;
   private int nData = 0;
   private boolean affineField = true;
   private int nThreads = 1;
   
   private float[] invJacobian = null;
   private float[][][] computedScalarArrays = null;
   private float[][][] computedVectorArrays = null;

   public RegularFieldDifferentialOps(RegularField inField, Params params)
   {
      this.inField = inField;
      this.params = params;
   }

   public void compute(RegularField inField)
   {
      this.inField = inField;
      nThreads = params.getThreads();
      dims = inField.getDims();
      dim = dims.length;
      nData = 1;
      for (int i = 0; i < dims.length; i++)
         nData *= dims[i];
      if (inField.getCoords() != null)
         affineField = inField.getCoords() == null;
      invJacobian = InvertedJacobian.computeInvertedJacobian(nThreads, inField);
      if (params.getScalarComponents() != null && params.getScalarComponents().length > 0)
         computedScalarArrays = new float[params.getScalarComponents().length][5][];
      if (params.getVectorComponents() != null && params.getVectorComponents().length > 0)
         computedVectorArrays = new float[params.getVectorComponents().length][3][];
      Thread[] workThreads = new Thread[nThreads];
      outField = inField.clone();
      if (params.getScalarComponents() != null)
         for (int i = 0; i < params.getScalarComponents().length; i++)
         {
            DataArray da = inField.getData(params.getScalarComponents()[i]);
            float[] fda = da.getFData();
            boolean[] ops = params.getScalarOperations()[i];
            for (int j = 0; j < 6; j++)
            {
               if (!ops[j])
                  continue;
               if (j < 5 && computedScalarArrays[i][j] != null) //has been already computed
               {
                  if (scalarTypes[j] != SYMMATRIX)
                  {
                     int vlen = 1;
                     switch (scalarTypes[j])
                     {
                     case SCALAR:
                        vlen = 1;
                        break;
                     case VECTOR:
                        vlen = dim;
                        break;
                     case FORM2:
                        vlen = (dim == 3 ? 3 : 1);
                        break;
                     }
                     outField.addData(DataArray.create(computedScalarArrays[i][j], vlen, da.getName() + scalarSuffixes[j]));
                  } else
                  {
                     int d = (dim * (dim + 1)) / 2;
                     float[] mdta = computedScalarArrays[i][j];
                     DataArray outData = DataArray.create(mdta, d, da.getName() + scalarSuffixes[j]);
                     outData.setMatrixProperties(new int[] {dim,dim}, true);
                     outField.addData(outData);
                  }
               } else
               {
                  if (computedScalarArrays[i][0] == null) //first derivative must be computed and stored
                     computedScalarArrays[i][0] = computeDerivatives(nThreads, inField, fda, invJacobian);
                  float[] grad = computedScalarArrays[i][0];
                  if (j <= Params.NORMALIZED_GRADIENT)  //only first derivative is needed
                     switch (j)
                     {
                     case Params.GRADIENT:
                        outField.addData(DataArray.create(computedScalarArrays[i][0], dim, da.getName() + scalarSuffixes[j]));
                        break;
                     case Params.GRADIENT_NORM:
                        float[] gnorm = new float[nData];
                        for (int k = 0, l = 0; k < nData; k++)
                        {
                           double s = 0;
                           for (int m = 0; m < dim; m++, l++)
                              s += grad[l] * grad[l];
                           gnorm[k] = (float) Math.sqrt(s);
                        }
                        computedScalarArrays[i][1] = gnorm;
                        outField.addData(DataArray.create(computedScalarArrays[i][1], 1, da.getName() + scalarSuffixes[j]));
                        break;
                     case Params.NORMALIZED_GRADIENT:
                        float[] normg = new float[dim * nData];
                        for (int k = 0, l = 0, n = 0; k < nData; k++)
                        {
                           float s = 0;
                           for (int m = 0; m < dim; m++, l++)
                              s += grad[l] * grad[l];
                           if (s == 0)
                              for (int m = 0; m < dim; m++, n++)
                                 normg[n] = 0;
                           else
                           {
                              s = 1 / (float) Math.sqrt(s);
                              for (int m = 0; m < dim; m++, n++)
                                 normg[n] = grad[n] * s;
                           }
                        }
                        computedScalarArrays[i][2] = normg;
                        outField.addData(DataArray.create(computedScalarArrays[i][2], dim, da.getName() + scalarSuffixes[j]));
                        break;
                     }
                  else
                  {
                     float[] d2 = computeDerivatives(nThreads, inField, fda, invJacobian);
                     float[] h = symmetrize(dim, d2);
                     computedScalarArrays[i][4] = h;
                     switch (j)
                     {
                     case Params.LAPLACIAN:
                        computedScalarArrays[i][3] = new float[nData];
                        float[] lapl = computedScalarArrays[i][3];
                        switch (dim)
                        {
                           case 3:
                              for (int k = 0, l = 0; k < nData; k++, l += 6)
                                 lapl[k] = h[l] + h[l + 3] + h[l + 5];
                              break;
                           case 2:
                              for (int k = 0, l = 0; k < nData; k++, l += 3)
                                 lapl[k] = h[l] + h[l + 2];
                              break;
                        }
                        outField.addData(DataArray.create(lapl, 1, da.getName() + scalarSuffixes[j]));
                        break;
                     case Params.HESSIAN:
                        DataArray outData = DataArray.create(h, (dim * (dim + 1)) / 2, da.getName() + scalarSuffixes[j]);
                        outData.setMatrixProperties(new int[] {dim,dim}, true);
                        outField.addData(outData);
                        break;
                     case Params.HESSIAN_EIG:
                        
                        float[][] hEigV = new float[dim][nData];
                        float[][] hEigR = new float[dim][nData * dim];
                        if (dim == 3)
                           PointwiseLinearAlgebra3D.symEigen(nThreads, h, hEigV, hEigR);
                        else
                           PointwiseLinearAlgebra2D.symEigen(nThreads, h, hEigV, hEigR);
                        for (int k = 0; k < dim; k++)
                        {
                           outField.addData(DataArray.create(hEigV[k], 1, da.getName() + "H_eigval_" + k));
                           outField.addData(DataArray.create(hEigR[k], dim, da.getName() + "H_eigvec_" + k));
                        }
                        break;
                     }
                  }
               }
            }
         }
      if (params.getVectorComponents() != null)
         for (int i = 0; i < params.getVectorComponents().length; i++)
         {
            DataArray da = inField.getData(params.getVectorComponents()[i]);
            float[] fda = da.getFData();
            boolean[] ops =  params.getVectorOperations()[i];
            for (int j = 0; j < 3; j++)
            {
               if (!ops[j])
                  continue;
               if (computedVectorArrays[i][j] != null) //has been already computed
               {
                  if (vectorTypes[j] != MATRIX)
                  {
                     int vlen = 1;
                     switch (scalarTypes[j])
                     {
                     case SCALAR:
                        vlen = 1;
                        break;
                     case VECTOR:
                        vlen = dim;
                        break;
                     case FORM2:
                        vlen = (dim == 3 ? 3 : 1);
                        break;
                     }
                     outField.addData(DataArray.create(computedVectorArrays[i][j], vlen, da.getName() + vectorSuffixes[j]));
                  } else
                  {
                     float[] mdta = computedVectorArrays[i][j];
                     for (int k = 0; k < dim; k++)
                     {
                        float[] dta = new float[dim * nData];
                        for (int l = 0, m = 0; l < nData; l++)
                           for (int p = 0, n = dim * (dim * l + k); p < dim; p++, m++, n++)
                              dta[m] = mdta[n];
                        outField.addData(DataArray.create(dta, dim, da.getName() + vectorSuffixes[j] + "_" + k));
                     }
                  }
               } else if (computedVectorArrays[i][0] == null) 
                  computedVectorArrays[i][0] = computeDerivatives(nThreads, inField, fda, invJacobian);
               float[] h = computedVectorArrays[i][0];
               switch (j)
               {
               case Params.DERIV:
                  for (int k = 0; k < dim; k++)
                  {
                     float[] dta = new float[dim * nData];
                     for (int l = 0, m = 0; l < nData; l++)
                        for (int p = 0, n = dim * (dim * l + k); p < dim; p++, m++, n++)
                           dta[m] = h[n];
                     outField.addData(DataArray.create(dta, dim, da.getName() + vectorSuffixes[j] + "_" + k));
                  }
                  break;
               case Params.DIV:
                  float[] div = new float[nData];
                  computedVectorArrays[i][1] = div;
                  for (int k = 0, l = 0; k < nData; k++, l += dim * dim)
                  {
                     float s = 0;
                     for (int m = 0; m < dim; m++)
                        s += h[l + m * (dim + 1)];
                     div[k] = s;
                  }
                  outField.addData(DataArray.create(div, 1, da.getName() + vectorSuffixes[j]));
                  break;
               case Params.ROT:
                  if (dim == 3)
                  {
                     float[] rot = new float[dim * nData];
                     computedVectorArrays[i][2] = rot;
                     for (int k = 0, l = 0, n = 0; k < nData; k++, l += 3, n += 9)
                     {
                        rot[l]     = h[n + 7] - h[n + 5];
                        rot[l + 1] = h[n + 2] - h[n + 6];
                        rot[l + 2] = h[n + 3] - h[n + 1];
                     }
                     outField.addData(DataArray.create(rot, dim, da.getName() + vectorSuffixes[j]));
                  } else
                  {
                     float[] rot = new float[nData];
                     computedVectorArrays[i][2] = rot;
                     for (int k = 0, n = 0; k < nData; k++, n += 4)
                        rot[k] = h[n + 2] - h[n + 1];
                     outField.addData(DataArray.create(rot, 1, da.getName() + vectorSuffixes[j]));
                  }
                  break;
               }
            }
         }
   }

   public RegularField getOutField()
   {
      return outField;
   }
}
