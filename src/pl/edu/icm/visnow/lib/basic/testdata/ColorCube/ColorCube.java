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

package pl.edu.icm.visnow.lib.basic.testdata.ColorCube;

import javax.swing.JPanel;
import pl.edu.icm.visnow.datasets.CellArray;
import pl.edu.icm.visnow.datasets.CellSet;
import pl.edu.icm.visnow.datasets.IrregularField;
import pl.edu.icm.visnow.datasets.cells.Cell;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.geometries.objects.IrregularFieldGeometry;
import pl.edu.icm.visnow.lib.templates.visualization.modules.IrregularOutFieldVisualizationModule;
import pl.edu.icm.visnow.lib.types.VNIrregularField;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;

/**
 * @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class ColorCube extends IrregularOutFieldVisualizationModule
{

   protected boolean fromUI = false;
   public static OutputEgg[] outputEggs = null;

   /**
    * Creates a new instance of TestGeometryObject
    */
   public ColorCube()
   {
      SwingInstancer.swingRunAndWait(new Runnable()
      {
         @Override
         public void run()
         {
            setPanel(ui);
         }
      });
   }

   @Override
   public boolean isGenerator() {
      return true;
   }

   @Override
   public void onInitFinishedLocal()
   {
      irregularFieldGeometry = new IrregularFieldGeometry();
      outObj.addBgrColorListener(irregularFieldGeometry.getBackgroundColorListener());
      prepareOutputGeometry();
      onActive();
   }

   public void createTestField()
   {
      int nPoints = 8;
      outField = new IrregularField(nPoints);
      outField.setNSpace(3);
      float[] coords = new float[]
      {
         -1, -1, -1,
          1, -1, -1,
          1,  1, -1,
         -1,  1, -1,
         -1, -1,  1,
          1, -1,  1,
          1,  1,  1,
         -1,  1,  1
      };
      int[] colors = new int[]
      {
            0,    0,    0,
          255,    0,    0,
          255,  255,    0,
            0,  255,    0,
            0,    0,  255,
          255,    0,  255,
          255,  255,  255,
            0,  255,  255
      };
      outField.setCoords(coords);
      byte[] bColors = new byte[24];
      for (int i = 0; i < bColors.length; i++)
         bColors[i] = (byte)(0xff & colors[i]);
      
      CellSet cs = new CellSet();
      cs.addCells(new CellArray(Cell.HEXAHEDRON, new int[]{0, 1, 2, 3, 4, 5, 6, 7}, null, new int[]{0}));
      cs.generateDisplayData(coords);
      outField.addCellSet(cs);
      DataArray cls = DataArray.create(bColors, 3, "colors");
      cls.setUserData(new String[] {"colors"});
      outField.addData(cls);
      
   }


   @Override
   public void onActive()
   {
      createTestField();
      prepareOutputGeometry();
      show();
      setOutputValue("outField", new VNIrregularField(outField));
   }
}
