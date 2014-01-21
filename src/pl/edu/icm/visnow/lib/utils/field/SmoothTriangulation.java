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

package pl.edu.icm.visnow.lib.utils.field;

import pl.edu.icm.visnow.datasets.IrregularField;
import pl.edu.icm.visnow.datasets.cells.Cell;
import pl.edu.icm.visnow.gui.events.FloatValueModificationEvent;
import pl.edu.icm.visnow.gui.events.FloatValueModificationListener;


/**
 *
 * @author  Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class SmoothTriangulation
{
   private float[] coords;
   private float[] normals;
   private IrregularField inIrregularField;
   private int[] neighb;
   private int[] neighbInd;
   
   public SmoothTriangulation()
   {
   }

   public void setInField(IrregularField inField)
   {
      inIrregularField = inField;
      if (inField.getCoords()==null)
         return;
      int nCellSets = inIrregularField.getNCellSets();
      int nTris = 0;
      for (int i = 0; i < nCellSets; i++)
         nTris += inIrregularField.getCellSet(i).getCellArray(Cell.TRIANGLE).getNCells();
      int[] tris = new int[3*nTris];
      for (int i = 0, k = 0; i < nCellSets; i++)
      {
         int[] tr = inIrregularField.getCellSet(i).getCellArray(Cell.TRIANGLE).getNodes();
         for (int j = 0; j < tr.length; j++, k++)
            tris[k] = tr[j];
      }
      setGeometry(inField.getCoords(), inField.getNormals(), tris);
   }

   public void setGeometry(float[] crds, float[] normls, int[] tris)
   {
      int ncells = tris.length / 3;
      int nnodes = crds.length / 3;
      coords = new float[crds.length];
      for (int l = 0; l < coords.length; l++)
         coords[l] = crds[l];
      if (normls != null && normls.length == coords.length)
      {
         normals = new float[normls.length];
         for (int i = 0; i < normls.length; i++)
            normals[i] = normls[i];
      }
      else
         normals = null;
      int[] pNeighb = new int[6*ncells];
      for (int i = 0; i < pNeighb.length; i++)
         pNeighb[i] = -1;
      neighbInd = new int[nnodes+1];
      for (int i = 0; i < neighbInd.length; i++)
         neighbInd[i]=0;
      for (int i = 0; i < tris.length; i++)
         neighbInd[tris[i]]+=2;
      int k=0;
      for (int i = 0; i < neighbInd.length; i++)
      {
         int j = k+neighbInd[i];
         neighbInd[i] = k;
         k = j;
      }

      for (int i = 0; i < tris.length/3; i++)
      {
         int i0 = tris[3*i];
         int i1 = tris[3*i+1];
         int i2 = tris[3*i+2];
         for (int j=neighbInd[i0]; j<neighbInd[i0+1]; j++)
         {
            if (pNeighb[j]==i1)
               break;
            if (pNeighb[j]==-1)
            {
               pNeighb[j]=i1;
               break;
            }
         }
         for (int j=neighbInd[i0]; j<neighbInd[i0+1]; j++)
         {
            if (pNeighb[j]==i2)
               break;
            if (pNeighb[j]==-1)
            {
               pNeighb[j]=i2;
               break;
            }
         }
         i1 = tris[3*i];
         i2 = tris[3*i+1];
         i0 = tris[3*i+2];
         for (int j=neighbInd[i0]; j<neighbInd[i0+1]; j++)
         {
            if (pNeighb[j]==i1)
               break;
            if (pNeighb[j]==-1)
            {
               pNeighb[j]=i1;
               break;
            }
         }
         for (int j=neighbInd[i0]; j<neighbInd[i0+1]; j++)
         {
            if (pNeighb[j]==i2)
               break;
            if (pNeighb[j]==-1)
            {
               pNeighb[j]=i2;
               break;
            }
         }
         i2 = tris[3*i];
         i0 = tris[3*i+1];
         i1 = tris[3*i+2];
         for (int j =neighbInd[i0]; j<neighbInd[i0+1]; j++)
         {
            if (pNeighb[j]==i1)
               break;
            if (pNeighb[j]==-1)
            {
               pNeighb[j]=i1;
               break;
            }
         }
         for (int j=neighbInd[i0]; j<neighbInd[i0+1]; j++)
         {
            if (pNeighb[j]==i2)
               break;
            if (pNeighb[j]==-1)
            {
               pNeighb[j]=i2;
               break;
            }
         }
      }
      k = 0;
      for (int i = 0; i < pNeighb.length; i++)
         if (pNeighb[i]!=-1)
            k+=1;
      neighb = new int[k];
      k = 0;
      for (int i = 0; i < neighbInd.length-1; i++)
      {
         int k0 = k;
         for (int j=neighbInd[i]; j<neighbInd[i+1]; j++)
         {
            if (pNeighb[j]==-1)
               break;
            neighb[k] = pNeighb[j];
            k+=1;
         }
         neighbInd[i] = k0;
      }
      neighbInd[neighbInd.length-1] = k;
   }

   public float[] smoothCoords(int nSteps, float smoothCoeff)
   {
      float[] tCoords = new float[coords.length];
      int n = coords.length/3;
      for (int step=0; step<nSteps; step++)
      {
         fireStatusChanged((step+1.f)/nSteps);
         for (int i=0; i<n; i++)
         {
            int m = neighbInd[i+1]-neighbInd[i];
            for (int l=0;l<3;l++)
            {
               tCoords[3*i+l] = 0.f;
               for (int j=neighbInd[i]; j<neighbInd[i+1]; j++)
               {
                  tCoords[3*i+l] += coords[3*neighb[j]+l];
               }
               tCoords[3*i+l] = smoothCoeff*coords[3*i+l]+(1.f-smoothCoeff)*tCoords[3*i+l]/m;
            }
         }
         for (int i=0; i<coords.length; i++)
            coords[i] = tCoords[i];
      }
      return coords;
   }

   public float[] smoothNormals(int nSteps, float smoothCoeff)
   {
      if (normals == null)
         return null;
      float[] tCoords = new float[normals.length];
      int n = normals.length/3;
      for (int step=0; step<nSteps; step++)
      {
         fireStatusChanged((step+1.f)/nSteps);
         for (int i=0; i<n; i++)
         {
            int m = neighbInd[i+1]-neighbInd[i];
            for (int l=0;l<3;l++)
            {
               tCoords[3*i+l] = 0.f;
               for (int j=neighbInd[i]; j<neighbInd[i+1]; j++)
               {
                  tCoords[3*i+l] += normals[3*neighb[j]+l];
               }
               tCoords[3*i+l] = smoothCoeff*normals[3*i+l]+(1.f-smoothCoeff)*tCoords[3*i+l]/m;
            }
         }
         for (int i=0; i<normals.length; i++)
            normals[i] = tCoords[i];
      }
      for (int i = 0; i < n; i++)
      {
         float d = (float)(Math.sqrt(normals[3*i]*normals[3*i] + normals[3*i+1]*normals[3*i+1] + normals[3*i+2]*normals[3*i+2]));
         for (int j = 0; j < 3; j++)
            normals[3*i+j] /= d;
      }
      return normals;
   }


   
   private transient FloatValueModificationListener statusListener = null;

   public void addFloatValueModificationListener(FloatValueModificationListener listener)
   {
      if (statusListener == null)
         this.statusListener = listener;
      else
         System.out.println(""+this+": only one status listener can be added");
   }

   private void fireStatusChanged(float status)
   {
       FloatValueModificationEvent e = new FloatValueModificationEvent(this, status, true);
       if (statusListener != null)
          statusListener.floatValueChanged(e);
   }

}
