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
exception statement from your version.
*/
//</editor-fold>

package pl.edu.icm.visnow.lib.utils.numeric;

/**
 *
 * @author Krzysztof S. Nowinski Warsaw University, ICM
 */
public class NumericalMethods
{
   // Gaussian elimination with partial pivoting

   public static float[] lsolve(float[][] A, float[] b)
   {
      int N = b.length;

      for (int p = 0; p < N; p++)
      {
         // find pivot row and swap
         int max = p;
         for (int i = p + 1; i < N; i++)
            if (Math.abs(A[i][p]) > Math.abs(A[max][p]))
               max = i;
         float[] temp = A[p];
         A[p] = A[max];
         A[max] = temp;
         float t = b[p];
         b[p] = b[max];
         b[max] = t;

         // singular
         if (A[p][p] == 0.0f)
            return null;

         // pivot within A and b
         for (int i = p + 1; i < N; i++)
         {
            float alpha = A[i][p] / A[p][p];
            b[i] -= alpha * b[p];
            for (int j = p; j < N; j++)
               A[i][j] -= alpha * A[p][j];
         }
      }
      // back substitution
      float[] x = new float[N];
      for (int i = N - 1; i >= 0; i--)
      {
         float sum = 0.0f;
         for (int j = i + 1; j < N; j++)
            sum += A[i][j] * x[j];
         x[i] = (b[i] - sum) / A[i][i];
      }
      return x;
   }

   public static double[] dlsolve(double[][] A, double[] b)
   {
      int N = b.length;

      for (int p = 0; p < N; p++)
      {
         // find pivot row and swap
         int max = p;
         for (int i = p + 1; i < N; i++)
            if (Math.abs(A[i][p]) > Math.abs(A[max][p]))
               max = i;
         double[] temp = A[p];
         A[p] = A[max];
         A[max] = temp;
         double t = b[p];
         b[p] = b[max];
         b[max] = t;

         // singular
         if (A[p][p] == 0.0)
            return null;

         // pivot within A and b
         for (int i = p + 1; i < N; i++)
         {
            double alpha = A[i][p] / A[p][p];
            b[i] -= alpha * b[p];
            for (int j = p; j < N; j++)
               A[i][j] -= alpha * A[p][j];
         }
      }
      // back substitution
      double[] x = new double[N];
      for (int i = N - 1; i >= 0; i--)
      {
         double sum = 0.0f;
         for (int j = i + 1; j < N; j++)
            sum += A[i][j] * x[j];
         x[i] = (b[i] - sum) / A[i][i];
      }
      return x;
   }

   // Gaussian elimination with partial pivoting
   public static boolean invert(float[][] aa, float[][] invA)
   {
      if (aa == null || invA == null)
         return false;
      int n = aa.length;
      if (invA.length != n)
         return false;
      for (int i = 0; i < aa.length; i++)
         if (aa[i] == null || aa[i].length != n || invA[i] == null || invA[i].length != n)
            return false;
      float[][] a = new float[n][n];
      for (int i = 0; i < n; i++)
      {
         for (int j = 0; j < n; j++)
         {
            invA[i][j] = 0;
            a[i][j] = aa[i][j];
         }
         invA[i][i] = 1;
      }
      int[] pivot = new int[n];
      boolean[] row = new boolean[n];
      boolean[] col = new boolean[n];
      for (int i = 0; i < pivot.length; i++)
      {
         pivot[i] = -1;
         row[i] = col[i] = false;
      }
      for (int p = 0; p < n; p++)
      {
         // find pivot row
         int k = -1, l = -1;
         float v = 0;
         for (int i = 0; i < n; i++)
         {
            if (row[i])
               continue;
            for (int j = 0; j < n; j++)
               if (!col[j] && Math.abs(a[i][j]) > v)
               {
                  k = i;
                  l = j;
                  v = Math.abs(a[i][j]);
               }
         }
         if (k == -1)
            return false;
         pivot[k] = l;
         row[k] = col[l] = true;
         v = a[k][l];
         // normalize pivot row and subtract from other rows
         for (int j = 0; j < n; j++)
         {
            a[k][j] /= v;
            invA[k][j] /= v;
         }
         for (int i = 0; i < n; i++)
            if (i != k)
            {
               v = a[i][l];
               for (int j = 0; j < n; j++)
               {
                  a[i][j] -= a[k][j] * v;
                  invA[i][j] -= invA[k][j] * v;
               }
            }
      }
      for (int i = 0; i < n; i++)
         System.arraycopy(invA[i], 0, a[i], 0, n);
      for (int i = 0; i < n; i++)
      {
         int k = pivot[i];
         System.arraycopy(a[i], 0, invA[k], 0, n);
      }
      // back substitution
      return true;
   }

   // Gaussian elimination with partial pivoting
   public static boolean invert(double[][] aa, double[][] invA)
   {
      if (aa == null || invA == null)
         return false;
      int n = aa.length;
      if (invA.length != n)
         return false;
      for (int i = 0; i < aa.length; i++)
         if (aa[i] == null || aa[i].length != n || invA[i] == null || invA[i].length != n)
            return false;
      double[][] a = new double[n][n];
      for (int i = 0; i < n; i++)
      {
         for (int j = 0; j < n; j++)
         {
            invA[i][j] = 0;
            a[i][j] = aa[i][j];
         }
         invA[i][i] = 1;
      }
      int[] pivot = new int[n];
      boolean[] row = new boolean[n];
      boolean[] col = new boolean[n];
      for (int i = 0; i < pivot.length; i++)
      {
         pivot[i] = -1;
         row[i] = col[i] = false;
      }
      for (int p = 0; p < n; p++)
      {
         // find pivot row
         int k = -1, l = -1;
         double v = 0;
         for (int i = 0; i < n; i++)
         {
            if (row[i])
               continue;
            for (int j = 0; j < n; j++)
               if (!col[j] && Math.abs(a[i][j]) > v)
               {
                  k = i;
                  l = j;
                  v = Math.abs(a[i][j]);
               }
         }
         if (k == -1)
            return false;
         pivot[k] = l;
         row[k] = col[l] = true;
         v = a[k][l];
         // normalize pivot row and subtract from other rows
         for (int j = 0; j < n; j++)
         {
            a[k][j] /= v;
            invA[k][j] /= v;
         }
         for (int i = 0; i < n; i++)
            if (i != k)
            {
               v = a[i][l];
               for (int j = 0; j < n; j++)
               {
                  a[i][j] -= a[k][j] * v;
                  invA[i][j] -= invA[k][j] * v;
               }
            }
      }
      for (int i = 0; i < n; i++)
         System.arraycopy(invA[i], 0, a[i], 0, n);
      for (int i = 0; i < n; i++)
      {
         int k = pivot[i];
         System.arraycopy(a[i], 0, invA[k], 0, n);
      }
      // back substitution
      return true;
   }


   /* jacobiEigenproblemSolver (original matrix, results: eigenvalues,
    *                            results: eigenvectors, iterations) */
   public static int jacobiEigenproblemSolver(double a[][], double d[], double v[][], int ir)
   {
      if (a == null)
         return -1;
      int n = a.length;
      int i, j, p, q, kr;
      double dm, bm, diff, g, t, th, c, s, h;
      if (d == null || d.length != n || v == null || v.length != n)
         return -1;
      for (i = 0; i < n; i++)
         if (a[i] == null || a[i].length != n || v[i] == null || v[i].length != n)
            return -1;

      for (i = 0; i < n; i++)
      {
         for (j = 0; j < n; j++)
            v[i][j] = 0.;
         v[i][i] = 1.;
         d[i] = a[i][i];
      }
      if (n == 1)
         return 0;
      for (kr = 0; kr < ir; kr++)
      {
         dm = d[0];
         bm = 0.;
         p = q = 0;
         for (i = 1; i < n; i++)
         {
            for (j = 0; j < i; j++)
               if (Math.abs(a[j][i]) > bm)
               {
                  p = j;
                  q = i;
                  bm = Math.abs(a[j][i]);
               }
            if (Math.abs(d[i]) <= dm)
               dm = Math.abs(d[i]);
         }
         if (dm + bm == dm || bm == 0.)
            return 2;
         diff = d[q] - d[p];
         g = 100. * bm;
         if (g + Math.abs(diff) == Math.abs(diff))
            t = a[p][q] / diff;
         else
         {
            th = diff / (2 * a[p][q]);
            t = 1 / (Math.abs(th) + Math.sqrt(1. + th * th));
            if (th < 0.)
               t = -t;
         }
         c = 1 / Math.sqrt(1 + t * t);
         s = t * c;
         h = t * a[p][q];
         d[p] -= h;
         d[q] += h;
         a[p][q] = 0.;
         if (p > 0)
            for (i = 0; i < p; i++)
            {
               g = a[i][p];
               h = a[i][q];
               a[i][p] = c * g - s * h;
               a[i][q] = s * g + c * h;
            }
         if (p < q - 1)
            for (i = p + 1; i < q; i++)
            {
               g = a[p][i];
               h = a[i][q];
               a[p][i] = c * g - s * h;
               a[i][q] = s * g + c * h;
            }
         if (q < n - 1)
            for (i = q + 1; i < n; i++)
            {
               g = a[p][i];
               h = a[q][i];
               a[p][i] = c * g - s * h;
               a[q][i] = s * g + c * h;
            }
         for (i = 0; i < n; i++)
         {
            g = v[i][p];
            h = v[i][q];
            v[i][p] = c * g - s * h;
            v[i][q] = s * g + c * h;
         }
      }
      for (i = 0; i < n; i++)
         for (j = i + 1; j < n; j++)
         {
            a[i][j] = a[j][i];
            h = v[i][j];
            v[i][j] = v[j][i];
            v[j][i] = h;
         }

      return (1);
   }

   /**
    * jacobiEigenproblemSolver (original matrix, results: eigenvalues,
    *                            results: eigenvectors, iterations) */
   public static int jacobiEigenproblemSolver(float a[][], float d[], float v[][], int ir)
   {
      if (a == null)
         return -1;
      int n = a.length;
      int i, j, p, q, kr;
      float dm, bm, diff, g, t, th, c, s, h;
      if (d == null || d.length != n || v == null || v.length != n)
         return -1;
      for (i = 0; i < n; i++)
         if (a[i] == null || a[i].length != n || v[i] == null || v[i].length != n)
            return -1;

      for (i = 0; i < n; i++)
      {
         for (j = 0; j < n; j++)
            v[i][j] = 0;
         v[i][i] = 1;
         d[i] = a[i][i];
      }
      if (n == 1)
         return 0;
      for (kr = 0; kr < ir; kr++)
      {
         dm = d[0];
         bm = 0;
         p = q = 0;
         for (i = 1; i < n; i++)
         {
            for (j = 0; j < i; j++)
               if (Math.abs(a[j][i]) > bm)
               {
                  p = j;
                  q = i;
                  bm = Math.abs(a[j][i]);
               }
            if (Math.abs(d[i]) <= dm)
               dm = Math.abs(d[i]);
         }
         if (dm + bm == dm || bm == 0.)
            return 2;
         diff = d[q] - d[p];
         g = 100 * bm;
         if (g + Math.abs(diff) == Math.abs(diff))
            t = a[p][q] / diff;
         else
         {
            th = diff / (2 * a[p][q]);
            t = 1 / (float) (Math.abs(th) + Math.sqrt(1. + th * th));
            if (th < 0.)
               t = -t;
         }
         c = 1 / (float) Math.sqrt(1 + t * t);
         s = t * c;
         h = t * a[p][q];
         d[p] -= h;
         d[q] += h;
         a[p][q] = 0;
         if (p > 0)
            for (i = 0; i < p; i++)
            {
               g = a[i][p];
               h = a[i][q];
               a[i][p] = c * g - s * h;
               a[i][q] = s * g + c * h;
            }
         if (p < q - 1)
            for (i = p + 1; i < q; i++)
            {
               g = a[p][i];
               h = a[i][q];
               a[p][i] = c * g - s * h;
               a[i][q] = s * g + c * h;
            }
         if (q < n - 1)
            for (i = q + 1; i < n; i++)
            {
               g = a[p][i];
               h = a[q][i];
               a[p][i] = c * g - s * h;
               a[q][i] = s * g + c * h;
            }
         for (i = 0; i < n; i++)
         {
            g = v[i][p];
            h = v[i][q];
            v[i][p] = c * g - s * h;
            v[i][q] = s * g + c * h;
         }
      }
      for (i = 0; i < n; i++)
         for (j = i + 1; j < n; j++)
         {
            a[i][j] = a[j][i];
            h = v[i][j];
            v[i][j] = v[j][i];
            v[j][i] = h;
         }
      return (1);
   }

   /* square root of a matrix */
   public static double[][] matSqrt(double[][] mat)
   {
      int n = mat.length;
      for (int i = 0; i < n; i++)
         if (mat[i].length != n)
            return null;
      double[][] m = new double[n][n];
      double[] d = new double[n];
      double[][] v = new double[n][n];
      double[][] res = new double[n][n];
      for (int i = 0; i < n; i++)
         System.arraycopy(mat[i], 0, m[i], 0, n);
      jacobiEigenproblemSolver(m, d, v, 1000);
      for (int i = 0; i < n; i++)
         d[i] = Math.sqrt(Math.abs(d[i]));
      for (int i = 0; i < n; i++)
         for (int j = 0; j < n; j++)
         {
            res[i][j] = 0;
            for (int k = 0; k < n; k++)
               res[i][j] += v[i][k] * d[k] * v[j][k];
         }
      return res;
   }

   /* matrix^(-1/2) */
   public static double[][] matInvSqrt(double[][] mat)
   {
      int n = mat.length;
      for (int i = 0; i < n; i++)
         if (mat[i].length != n)
            return null;
      double[][] m = new double[n][n];
      double[] d = new double[n];
      double[][] v = new double[n][n];
      double[][] res = new double[n][n];
      for (int i = 0; i < n; i++)
         System.arraycopy(mat[i], 0, m[i], 0, n);
      jacobiEigenproblemSolver(m, d, v, 1000);
      for (int i = 0; i < n; i++)
         d[i] = 1. / Math.sqrt(Math.abs(d[i]));
      for (int i = 0; i < n; i++)
         for (int j = 0; j < n; j++)
         {
            res[i][j] = 0;
            for (int k = 0; k < n; k++)
               res[i][j] += v[i][k] * d[k] * v[j][k];
         }
      return res;
   }
   private static double SAFETY = 0.9D; // safety scaling factor for Runge Kutta Fehlberg tolerance check

   // Fourth order Runge-Kutta for n (nequ) ordinary differential equations (ODE)
   public static float[] fourthOrder(DerivnFunction g, double x0, double[] y0, double xn, double h)
   {
      int nequ = y0.length;
      double[] k1 = new double[nequ];
      double[] k2 = new double[nequ];
      double[] k3 = new double[nequ];
      double[] k4 = new double[nequ];
      double[] y = new double[nequ];
      double[] yd = new double[nequ];
      double[] dydx = new double[nequ];
      double x = 0.0D;

      // Calculate nsteps
      double ns = (xn - x0) / h;
      ns = Math.rint(ns);
      int nsteps = (int) ns;
      h = (xn - x0) / ns;
      float[] trajectory = new float[nequ * (nsteps + 1)];

      // initialise
      for (int i = 0; i < nequ; i++)
      {
         y[i] = y0[i];
         trajectory[i] = (float) y0[i];
      }

      // iteration over allowed steps
      for (int j = 0, k = nequ; j < nsteps; j++)
      {
         x = x0 + j * h;
         dydx = g.derivn(x, y);
         for (int i = 0; i < nequ; i++)
            k1[i] = h * dydx[i];

         for (int i = 0; i < nequ; i++)
            yd[i] = y[i] + k1[i] / 2;
         dydx = g.derivn(x + h / 2, yd);
         for (int i = 0; i < nequ; i++)
            k2[i] = h * dydx[i];

         for (int i = 0; i < nequ; i++)
            yd[i] = y[i] + k2[i] / 2;
         dydx = g.derivn(x + h / 2, yd);
         for (int i = 0; i < nequ; i++)
            k3[i] = h * dydx[i];

         for (int i = 0; i < nequ; i++)
            yd[i] = y[i] + k3[i];
         dydx = g.derivn(x + h, yd);
         for (int i = 0; i < nequ; i++)
            k4[i] = h * dydx[i];

         for (int i = 0; i < nequ; i++, k++)
         {
            y[i] += k1[i] / 6 + k2[i] / 3 + k3[i] / 3 + k4[i] / 6;
            trajectory[k] = (float) y[i];
         }
      }
      return trajectory;
   }

   public static float[] lsolve3x3(float[][] A, float[] b)
   {
      float[] out = new float[3];
      float det, detx, dety, detz;
      det = A[0][0] * A[1][1] * A[2][2] - A[0][0] * A[1][2] * A[2][1] + A[0][1] * A[1][2] * A[2][0] - A[0][1] * A[1][0] * A[2][2] + A[0][2] * A[1][0] * A[2][1] - A[0][2] * A[1][1] * A[2][0];
      if (det == 0.0f)
         return null;

      detx = b[0] * A[1][1] * A[2][2] - b[0] * A[1][2] * A[2][1] + A[0][1] * A[1][2] * b[2] - A[0][1] * b[1] * A[2][2] + A[0][2] * b[1] * A[2][1] - A[0][2] * A[1][1] * b[2];
      dety = A[0][0] * b[1] * A[2][2] - A[0][0] * A[1][2] * b[2] + b[0] * A[1][2] * A[2][0] - b[0] * A[1][0] * A[2][2] + A[0][2] * A[1][0] * b[2] - A[0][2] * b[1] * A[2][0];
      detz = A[0][0] * A[1][1] * b[2] - A[0][0] * b[1] * A[2][1] + A[0][1] * b[1] * A[2][0] - A[0][1] * A[1][0] * b[2] + b[0] * A[1][0] * A[2][1] - b[0] * A[1][1] * A[2][0];
      out[0] = detx / det;
      out[1] = dety / det;
      out[2] = detz / det;
      return out;
   }

   public static float[] pullVectorField(int[] dims, float[] coords, float[] vec)
   {
      float[] pullVec = new float[vec.length];
      if (dims.length == 3)
      {
         float[][] jacobi = new float[3][3];
         int len = dims[0];
         int slc = dims[0] * dims[1];
         for (int i = 0, m = 0; i < dims[2]; i++)
            for (int j = 0; j < dims[1]; j++)
               for (int k = 0; k < dims[0]; k++, m++)
               {
                  for (int l = 0; l < 3; l++)
                  {
                     if (k == 0)
                        jacobi[l][0] = coords[3 * (m + 1) + l] - coords[3 * m + l];
                     else if (k == dims[0] - 1)
                        jacobi[l][0] = coords[3 * m + l] - coords[3 * (m - 1) + l];
                     else
                        jacobi[l][0] = .5f * (coords[3 * (m + 1) + l] - coords[3 * (m - 1) + l]);
                     if (j == 0)
                        jacobi[l][1] = coords[3 * (m + len) + l] - coords[3 * m + l];
                     else if (j == dims[1] - 1)
                        jacobi[l][1] = coords[3 * m + l] - coords[3 * (m - len) + l];
                     else
                        jacobi[l][1] = .5f * (coords[3 * (m + len) + l] - coords[3 * (m - len) + l]);
                     if (i == 0)
                        jacobi[l][2] = coords[3 * (m + slc) + l] - coords[3 * m + l];
                     else if (i == dims[2] - 1)
                        jacobi[l][2] = coords[3 * m + l] - coords[3 * (m - slc) + l];
                     else
                        jacobi[l][2] = .5f * (coords[3 * (m + slc) + l] - coords[3 * (m - slc) + l]);
                  }
                  float[] v = new float[3];
                  System.arraycopy(vec, 3 * m, v, 0, 3);
                  float[] w = lsolve(jacobi, v);
                  if (w != null)
                     System.arraycopy(w, 0, pullVec, 3 * m, 3);
               }
      } else
      {
         float[][] jacobi = new float[2][2];
         float[][] invJacobi = new float[2][2];
         float[] v = new float[2];
         int len = dims[0];
         for (int j = 0, m = 0; j < dims[1]; j++)
            for (int k = 0; k < dims[0]; k++, m++)
            {
               for (int l = 0; l < 2; l++)
               {
                  if (k == 0)
                     jacobi[l][0] = coords[2 * (m + 1) + l] - coords[2 * m + l];
                  else if (k == dims[0] - 1)
                     jacobi[l][0] = coords[2 * m + l] - coords[2 * (m - 1) + l];
                  else
                     jacobi[l][0] = .5f * (coords[2 * (m + 1) + l] - coords[2 * (m - 1) + l]);
                  if (j == 0)
                     jacobi[l][1] = coords[2 * (m + len) + l] - coords[2 * m + l];
                  else if (j == dims[1] - 1)
                     jacobi[l][1] = coords[2 * m + l] - coords[2 * (m - len) + l];
                  else
                     jacobi[1][l] = .5f * (coords[2 * (m + len) + l] - coords[2 * (m - len) + l]);
               }
               if (invert(jacobi, invJacobi))
               {
                  for (int l = 0; l < 2; l++)
                  {
                     v[l] = 0;
                     for (int n = 0; n < 2; n++)
                        v[l] += invJacobi[l][n] * vec[2 * m + n];
                  }
                  System.arraycopy(v, 0, pullVec, 2 * m, 2);
               }
            }
      }
      return pullVec;
   }

   public static float[] pushVectorField(int[] dims, float[] coords, float[] vec)
   {
      float[] pushVec = new float[vec.length];
      if (dims.length == 3)
      {
         float[][] jacobi = new float[3][3];
         float[] v = new float[3];
         int len = dims[0];
         int slc = dims[0] * dims[1];
         for (int i = 0, m = 0; i < dims[2]; i++)
            for (int j = 0; j < dims[1]; j++)
               for (int k = 0; k < dims[0]; k++, m++)
               {
                  for (int l = 0; l < 3; l++)
                  {
                     if (k == 0)
                        jacobi[l][0] = coords[3 * (m + 1) + l] - coords[3 * m + l];
                     else if (k == dims[0] - 1)
                        jacobi[l][0] = coords[3 * m + l] - coords[3 * (m - 1) + l];
                     else
                        jacobi[l][0] = .5f * (coords[3 * (m + 1) + l] - coords[3 * (m - 1) + l]);
                     if (j == 0)
                        jacobi[l][1] = coords[3 * (m + len) + l] - coords[3 * m + l];
                     else if (j == dims[1] - 1)
                        jacobi[l][1] = coords[3 * m + l] - coords[3 * (m - len) + l];
                     else
                        jacobi[1][l] = .5f * (coords[3 * (m + len) + l] - coords[3 * (m - len) + l]);
                     if (i == 0)
                        jacobi[l][2] = coords[3 * (m + slc) + l] - coords[3 * m + l];
                     else if (i == dims[2] - 1)
                        jacobi[l][2] = coords[3 * m + l] - coords[3 * (m - slc) + l];
                     else
                        jacobi[l][2] = .5f * (coords[3 * (m + slc) + l] - coords[3 * (m - slc) + l]);
                  }
                  for (int l = 0; l < 3; l++)
                  {
                     v[l] = 0;
                     for (int n = 0; n < 3; n++)
                        v[l] += jacobi[l][n] * vec[3 * m + n];
                  }
                  System.arraycopy(v, 0, pushVec, 3 * m, 3);
               }
      } else
      {
         float[][] jacobi = new float[2][2];
         float[] v = new float[2];
         int len = dims[0];
         int slc = dims[0] * dims[1];
         for (int j = 0, m = 0; j < dims[1]; j++)
            for (int k = 0; k < dims[0]; k++, m++)
            {
               for (int l = 0; l < 2; l++)
               {
                  if (k == 0)
                     jacobi[l][0] = coords[2 * (m + 1) + l] - coords[2 * m + l];
                  else if (k == dims[0] - 1)
                     jacobi[l][0] = coords[2 * m + l] - coords[2 * (m - 1) + l];
                  else
                     jacobi[l][0] = .5f * (coords[2 * (m + 1) + l] - coords[2 * (m - 1) + l]);
                  if (j == 0)
                     jacobi[l][1] = coords[2 * (m + len) + l] - coords[2 * m + l];
                  else if (j == dims[1] - 1)
                     jacobi[l][1] = coords[2 * m + l] - coords[2 * (m - len) + l];
                  else
                     jacobi[1][l] = .5f * (coords[2 * (m + len) + l] - coords[2 * (m - len) + l]);
               }
               for (int l = 0; l < 2; l++)
               {
                  v[l] = 0;
                  for (int n = 0; n < 2; n++)
                     v[l] += jacobi[l][n] * vec[2 * m + n];
               }
               System.arraycopy(v, 0, pushVec, 2 * m, 2); 
            }
      }
      return pushVec;
   }

   private NumericalMethods()
   {
   }
   
   public static void main(String[] args)
   {
      float[][] test = new float[10][10];
      float[][] invtest = new float[10][10];
      for (int i = 0; i < test.length; i++)
         for (int j = 0; j < test[i].length; j++)
            test[i][j] = (float)Math.random();
      invert(test,invtest);
      for (int i = 0; i < invtest.length; i++)
      {
         for (int j = 0; j < test.length; j++)
            System.out.printf("%8.3f ", test[i][j]);
         System.out.print("   ");
         for (int j = 0; j < test.length; j++)
            System.out.printf("%8.3f ", invtest[i][j]);
         System.out.println("");
      }
   }
}
