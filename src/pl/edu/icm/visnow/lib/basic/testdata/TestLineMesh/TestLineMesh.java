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

package pl.edu.icm.visnow.lib.basic.testdata.TestLineMesh;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pl.edu.icm.visnow.datasets.CellArray;
import pl.edu.icm.visnow.datasets.CellSet;
import pl.edu.icm.visnow.datasets.IrregularField;
import pl.edu.icm.visnow.datasets.cells.Cell;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.geometries.objects.IrregularFieldGeometry;
import pl.edu.icm.visnow.lib.templates.visualization.modules.IrregularOutFieldVisualizationModule;
import pl.edu.icm.visnow.lib.types.VNGeometryObject;
import pl.edu.icm.visnow.lib.types.VNIrregularField;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;

/**
 * @author Krzysztof Nowinski (know@icm.edu.pl) University of Warsaw,
 * Interdisciplinary Centre for Mathematical and Computational Modelling
 */
public class TestLineMesh extends IrregularOutFieldVisualizationModule
{

   protected GUI computeUI = null;
   protected Params params;
   protected boolean fromUI = false;
   public static OutputEgg[] outputEggs = null;

   /**
    * Creates a new instance of TestGeometryObject
    */
   public TestLineMesh()
   {
      params = new Params();
      params.addChangeListener(new ChangeListener()
      {
            @Override
         public void stateChanged(ChangeEvent evt)
         {
            startAction();
         }
      });
      SwingInstancer.swingRun(new Runnable()
      {

            @Override
         public void run()
         {
            computeUI = new GUI();
         }
      });
      ui.addComputeGUI(computeUI);
      setPanel(ui);
      computeUI.setParams(params);
   }

   public static boolean isGenerator()
   {
      return true;
   }

   @Override
   public void onInitFinished()
   {
      visInitFinished();
      irregularFieldGeometry = new IrregularFieldGeometry();
      outObj.addBgrColorListener(irregularFieldGeometry.getBackgroundColorListener());
      outObj.getGeometryObj().setUserData(getName());
      setOutputValue("outObj", new VNGeometryObject(outObj));   
      onActive();
   }

   public void createTestPoints()
   {
      int level = (int)(Math.sqrt(params.getNPoints()));
      if (level < 2) level = 2;
      int nPoints = 2 + 2 * (level - 1) * level;
      outField = new IrregularField();
      outField.setNNodes(nPoints);
      outField.setNSpace(3);
      float[] coords = new float[nPoints * 3];
      int nSegments = 2 * (2 * (level - 1) + 1) * level;
      float[] data = new float[nPoints];
      float[] data1 = new float[nPoints];
      int[] cells = new int[2 * nSegments];
      int[] cellDataIndices = new int[nSegments];
      float[] cellData = new float[nSegments];
      float[] cellData1 = new float[nSegments];
      double d = Math.PI / level;
      coords[0] = coords[1] = 0;
      coords[2] = 1;
      data[0] = 1;
      data1[0] = 0;
      int k = 3;
      int l = 0;
      int p = 1;
      for (int i = 1; i < level; i++)
      {
         float cosPsi = (float) Math.cos(i * d);
         float sinPsi = (float) Math.sin(i * d);
         for (int j = 0; j < 2 * level; j++, k += 3, p += 1, l += 4)
         {
            data[p] = cosPsi;
            data1[p] = sinPsi * (float)Math.cos(4 * j * d);
            coords[k]     = (float)(sinPsi * Math.cos(j * d));
            coords[k + 1] = (float)(sinPsi * Math.sin(j * d));
            coords[k + 2] = cosPsi;
         if (i == 1)
            cells[l] = 0;
         else
            cells[l] = p - 2 * level;
            cells[l + 1] = cells[l + 2] = p;
            if (j < 2 * level - 1)  
               cells[l + 3] = p + 1;
            else
               cells[l + 3] = p - (2 * level - 1);
         }
      }
      data[p] = -1;
      data1[p] = 0;
      coords[k] = coords[k + 1] = 0;
      coords[k + 2] = -1;
      p = 1 + 2 * (level - 2) * level;
      for (int j = 0;  j < 2 * level; j++, p += 1, l += 2)
      {
         cells[l] = p;
         cells[l + 1] = nPoints - 1;
      }
      for (int i = 0; i < cellData.length; i++)
      {
         cellData[i] = cellDataIndices[i] = i;
         cellData1[i] = coords[3* cells[2 * i]];
      }
      CellArray ca = new CellArray(Cell.SEGMENT, cells, null, cellDataIndices);
      CellSet cs = new CellSet();
      cs.setCellArray(ca);
      cs.addData(DataArray.create(cellData, 1, "c"));
      cs.addData(DataArray.create(cellData1, 1, "c1"));
      float[] v = new float[coords.length];
      for (int i = 0; i < v.length; i++) 
         v[i] = coords[i] / (2 + coords[i]);
      outField.addCellSet(cs);
      outField.setCoords(coords);
      outField.addData(DataArray.create(data, 1, "z"));
      outField.addData(DataArray.create(data1, 1, "z2"));
      outField.addData(DataArray.create(v, 3, "v"));
   }


   @Override
   public void onActive()
   {
      createTestPoints();
      prepareOutputGeometry();
      show();
      setOutputValue("outField", new VNIrregularField(outField));
   }
}
