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

package pl.edu.icm.visnow.lib.basic.filters.Boundaries;


import pl.edu.icm.visnow.datasets.CellArray;
import pl.edu.icm.visnow.datasets.CellSet;
import pl.edu.icm.visnow.datasets.IrregularField;
import pl.edu.icm.visnow.engine.core.InputEgg;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.lib.templates.visualization.modules.IrregularOutFieldVisualizationModule;
import pl.edu.icm.visnow.lib.types.VNField;
import pl.edu.icm.visnow.lib.types.VNIrregularField;


/**
 * @author  Krzysztof S. Nowinski (know@icm.edu.pl)
 * Warsaw University, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class Boundaries extends IrregularOutFieldVisualizationModule
{

   /**
    * Creates a new instance of SurfaceSmoother
    */
   public static InputEgg[] inputEggs = null;
   public static OutputEgg[] outputEggs = null;

   public Boundaries()
   {
      setPanel(ui);
   }

   @Override
   public void onActive()
   {
      if (getInputFirstValue("inField") == null || ((VNField) getInputFirstValue("inField")).getField() == null)
         return;
      IrregularField inField =  ((VNIrregularField) getInputFirstValue("inField")).getField();
      outField = inField.cloneNodesData();
      for (CellSet cellSet : inField.getCellSets())
      {
         CellSet outCellSet = new CellSet(cellSet.getName()+"_boundary");
         for (int i = 0; i < cellSet.getBoundaryCellArrays().length; i++)
         {
            CellArray ca = cellSet.getBoundaryCellArray(i);
            if (ca == null)
               continue;
            outCellSet.setCellArray(ca);
            outCellSet.setBoundaryCellArray(ca);
         }
         outField.addCellSet(outCellSet);
      }
      prepareOutputGeometry();
      show();
      setOutputValue("outField", new VNIrregularField(outField));
   }
}
