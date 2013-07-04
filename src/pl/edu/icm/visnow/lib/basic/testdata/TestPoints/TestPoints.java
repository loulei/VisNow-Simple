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

package pl.edu.icm.visnow.lib.basic.testdata.TestPoints;

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
 * @author Bartosz Borucki (babor@icm.edu.pl) University of Warsaw,
 * Interdisciplinary Centre for Mathematical and Computational Modelling
 */
public class TestPoints extends IrregularOutFieldVisualizationModule
{

   protected GUI computeUI = null;
   protected Params params;
   protected boolean fromUI = false;
   public static OutputEgg[] outputEggs = null;

   /**
    * Creates a new instance of TestGeometryObject
    */
   public TestPoints()
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
      prepareOutputGeometry();
      onActive();
	   fieldDisplayParams.getCellSetDisplayParameters(0).getRenderingParams().setLineThickness(5.0f);
   }

   public void createTestPoints()
   {
      int nPoints = params.getNPoints();
      outField = new IrregularField();
      outField.setNNodes(nPoints);
      outField.setNSpace(3);
      float[] coords = new float[nPoints * 3];
      int[] data = new int[nPoints];
      float[] vData = new float[nPoints * 3];
      String[] texts = new String[nPoints];
      int[] cells = new int[nPoints];
      boolean[] orient = new boolean[nPoints];
      for (int i = 0; i < nPoints; i++)
      {
         data[i] = i + 1;
         texts[i] = "point " + i;
         cells[i] = i;
         orient[i] = true;
      }
      for (int i = 0; i < nPoints; i++)
      {
         for (int j = 0; j < 3; j++)
            coords[3 * i + j] = (float) Math.random() - .5f;
         vData[3 * i]      = -coords[3 * i + 1] + (float) Math.random() / 5 - .1f;
         vData[3 * i + 1]  = -coords[3 * i]     + (float) Math.random() / 5 - .1f;
         vData[3 * i + 2]  = -coords[3 * i + 2] + (float) Math.random() / 5 - .1f;
      }
      CellArray ca = new CellArray(Cell.POINT, cells, orient, null);
      CellSet cs = new CellSet();
      cs.setCellArray(ca);
      outField.addCellSet(cs);
      outField.setCoords(coords);
      outField.addData(DataArray.create(data, 1, "points"));
      outField.addData(DataArray.create(texts, 1, "texts"));
      outField.addData(DataArray.create(vData, 3, "vectors"));
      ui.getMapperGUI().setInFieldDisplayData(outField, fieldDisplayParams);
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
