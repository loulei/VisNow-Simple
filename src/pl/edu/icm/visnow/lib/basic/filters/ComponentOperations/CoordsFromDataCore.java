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
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.datasets.RegularField;

/**
 *
 * @author Bartosz Borucki (babor@icm.edu.pl) University of Warsaw,
 * Interdisciplinary Centre for Mathematical and Computational Modelling
 */
public class CoordsFromDataCore
{

   private Params params = null;
   private Field inField = null;
   private Field outField = null;
   private int[] coordsComp = null;
   private float[] coordScale = null;
   private float[] coordShift = null;
   private float[] varShifts = null;
   private float[] coords = null;
   private float[] inCoords = null;
   private String[] axesNames = null;
   public CoordsFromDataCore()
   {

   }

   public void setData(Field inField, Params p, Field outField)
   {
      this.inField = inField;
      this.outField = outField;
      this.params = p;
   }

   void updateRegularField(RegularField inField, RegularField outField)
   {
      if (inField == null || params == null)
         return;
      int[] dims = inField.getDims();
      int nData = inField.getNNodes();

      for (int iCoord = 0; iCoord < 3; iCoord++)
      {
         int comp = coordsComp[iCoord];
         float scale = coordScale[iCoord];
         float shift = coordShift[iCoord];
         float varShift = varShifts[iCoord];
         switch (dims.length)
         {
         case 1:
            if (comp == -1)
            {
               axesNames[0] = "i";
               for (int i = 0; i < nData; i++)
                  coords[3 * i + iCoord] = scale * (i - varShift) + shift;
            } 
            break;
         case 2:
            switch (comp)
            {
               case -1:
                  axesNames[0] = "i";
                  for (int j = 0, n = 0; j < dims[1]; j++)
                     for (int i = 0; i < dims[0]; i++, n++)
                        coords[3 * n + iCoord] = scale * (i - varShift) + shift;
                  break;
               case -2:
                  axesNames[0] = "j";
                  for (int j = 0, n = 0; j < dims[1]; j++)
                     for (int i = 0; i < dims[0]; i++, n++)
                        coords[3 * n + iCoord] = scale * (j - varShift) + shift;
                  break;
            }
            break;
         case 3:
         switch (comp)
         {
            case -1:
               axesNames[2] = "i";
               for (int k = 0, n = 0; k < dims[2]; k++)
                  for (int j = 0; j < dims[1]; j++)
                     for (int i = 0; i < dims[0]; i++, n++)
                        coords[3 * n + iCoord] = scale * (i - varShift) + shift;
               break;
            case -2:
               axesNames[2] = "j";
               for (int k = 0, n = 0; k < dims[2]; k++)
                  for (int j = 0; j < dims[1]; j++)
                     for (int i = 0; i < dims[0]; i++, n++)
                        coords[3 * n + iCoord] = scale * (j - varShift) + shift;
               break;
            case -3:
               axesNames[2] = "k";
               for (int k = 0, n = 0; k < dims[2]; k++)
                  for (int j = 0; j < dims[1]; j++)
                     for (int i = 0; i < dims[0]; i++, n++)
                        coords[3 * n + iCoord] = scale * (k - varShift) + shift;
               break;
         }
            break;
         }
      }
   }
   
   void update()
   {
      if (inField==null)
         return;
      int nFieldData = inField.getNData();
      inCoords = inField.getCoords();
      int inNSpace = inField.getNSpace();
      if (inCoords == null && inField instanceof RegularField) 
         inCoords = ((RegularField)inField).getCoordsFromAffine();
      coords = new float[3 * inField.getNNodes()];
      coordsComp = new int[] {params.getXCoordComponent(), 
                              params.getYCoordComponent(),
                              params.getZCoordComponent()};
      for (int i = 0; i < coordsComp.length; i++)
         if (coordsComp[i] >= nFieldData)
            coordsComp[i] = nFieldData - 1;
      
      varShifts = new float[] {params.getXVarShift(),
                              params.getYVarShift(),
                              params.getZVarShift()};
      coordScale = new float[] {params.getXCoordScale(),
                                params.getYCoordScale(),
                                params.getZCoordScale()};
      coordShift = new float[] {params.getXCoordShift(),
                                params.getYCoordShift(),
                                params.getZCoordShift()};
      
      int nData = inField.getNNodes();

      if (params.isAddIndexComponent())
      {
         int[] indexData = new int[nData];
         for (int i = 0; i < nData; i++)
            indexData[i] = i;
         outField.addData(DataArray.create(indexData, 1, "index"));
      }
      
      axesNames = new String[3];
      for (int iCoord = 0; iCoord < 3; iCoord++)
      {
         int comp = coordsComp[iCoord];
         float scale = coordScale[iCoord];
         float varShift = varShifts[iCoord];
         float shift = coordShift[iCoord];
         float[] c = null;
         if (comp >= 0)
         {
            c = inField.getData(comp).getFData();
            axesNames[0] = inField.getData(comp).getName();
            for (int i = 0; i < nData; i++)
               coords[3 * i + iCoord] = scale * (c[i] - varShift) + shift;
         } else if (comp == -10)
         {
            axesNames[0] = "x";
            for (int i = 0; i < nData; i++)
               coords[3 * i + iCoord] = scale * (inCoords[inNSpace * i] - varShift) + shift;
         } else if (comp == -11 && inNSpace >= 2)
         {
            axesNames[0] = "y";
            for (int i = 0; i < nData; i++)
               coords[3 * i + iCoord] = scale * (inCoords[inNSpace * i + 1] - varShift) + shift;
         } else if (comp == -12 && inNSpace >= 3)
         {
            axesNames[0] = "z";
            for (int i = 0; i < nData; i++)
               coords[3 * i + iCoord] = scale * (inCoords[inNSpace * i + 2] - varShift) + shift;
         } else if (comp == -100)
         {
            axesNames[0] = "";
            for (int i = 0; i < nData; i++)
               coords[3 * i + iCoord] = shift;
         }
      }   
      if (inField instanceof RegularField)
         updateRegularField((RegularField)inField, (RegularField)outField);
      outField.setNSpace(3);
      outField.setCoords(coords);
      outField.setAxesNames(axesNames);
   }

}
