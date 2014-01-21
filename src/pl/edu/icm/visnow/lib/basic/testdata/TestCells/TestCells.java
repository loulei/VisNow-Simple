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

package pl.edu.icm.visnow.lib.basic.testdata.TestCells;

import pl.edu.icm.visnow.datasets.CellArray;
import pl.edu.icm.visnow.datasets.CellSet;
import pl.edu.icm.visnow.datasets.IrregularField;
import pl.edu.icm.visnow.datasets.cells.Cell;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.geometries.objects.IrregularFieldGeometry;
import pl.edu.icm.visnow.lib.templates.visualization.modules.IrregularOutFieldVisualizationModule;
import pl.edu.icm.visnow.lib.types.VNIrregularField;
import pl.edu.icm.visnow.system.main.VisNow;

/**
 * @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class TestCells extends IrregularOutFieldVisualizationModule
{

   protected boolean fromUI = false;
   public static OutputEgg[] outputEggs = null;

   /**
    * Creates a new instance of TestGeometryObject
    */
   public TestCells()
   {
      ui.setPresentation(VisNow.guiLevel == VisNow.SIMPLE_GUI);
      setPanel(ui);
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
      onActive();
   }

   public void createTestField()
   {
      int nPoints = 15;
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
         -1,  1,  1,
          0,  2, -1,
          0,  2,  1,
          0,  1,  3,
         -1, -1,  3,
         -1,  1,  3,  
          1, -1,  3,
          1,  1,  3  
      };
      outField.setCoords(coords);
      
      
      CellSet cs = new CellSet();
      cs.addCells(new CellArray(Cell.HEXAHEDRON, new int[]{0, 1, 2, 3, 4, 5, 6, 7}, null, new int[]{0}));
      cs.addCells(new CellArray(Cell.PRISM,      new int[]{2, 8, 3, 6, 9, 7}, null, new int[]{1}));
      cs.addCells(new CellArray(Cell.PYRAMID,    new int[]{4, 5, 6, 7, 10}, null, new int[]{2}));
      cs.addCells(new CellArray(Cell.TETRA,      new int[]{6, 7, 10, 9}, null, new int[]{3}));
      cs.addCells(new CellArray(Cell.QUAD,       new int[]{4, 11, 12, 7}, null, new int[]{4}));
      cs.addCells(new CellArray(Cell.TRIANGLE,   new int[]{10, 12, 11}, null, new int[]{5}));
      cs.addCells(new CellArray(Cell.SEGMENT,    new int[]{11, 13}, null, new int[]{6}));
      cs.addCells(new CellArray(Cell.POINT,      new int[]{14}, null, new int[]{7}));
      cs.addData(DataArray.create(new float[]{Cell.HEXAHEDRON, Cell.PRISM, Cell.PYRAMID, Cell.TETRA, 
                                              Cell.QUAD, Cell.TRIANGLE,
                                              Cell.SEGMENT,Cell.POINT}, 1, "c"));
      cs.generateDisplayData(coords);
      outField.addCellSet(cs);
      outField.addData(DataArray.create(new float[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14}, 1, "nv"));
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
