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
exception statement from your version. */
//</editor-fold>

package pl.edu.icm.visnow.lib.basic.readers.ReadUCD;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pl.edu.icm.visnow.datasets.cells.Cell;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.geometries.parameters.AbstractRenderingParams;
import pl.edu.icm.visnow.gui.widgets.FileErrorFrame;
import pl.edu.icm.visnow.lib.templates.visualization.modules.IrregularOutFieldVisualizationModule;
import pl.edu.icm.visnow.lib.types.VNIrregularField;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;

/**
 * @author  Krzysztof S. Nowinski (know@icm.edu.pl)
 * Warsaw University, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class ReadUCD extends IrregularOutFieldVisualizationModule
{

   protected GUI computeUI = null;
   protected Params params;
   protected String lastFileName = " ";
   protected FileErrorFrame errorFrame = null;
   protected Cell[] stdCells = new Cell[Cell.TYPES];
   protected boolean ignoreUI = false;

   /**
    * Creates a new instance of CreateGrid
    */
   public ReadUCD()
   {
      for (int i = 0; i < stdCells.length; i++)
         stdCells[i] = Cell.createCell(i, 3, new int[Cell.nv[i]], true);
      parameters = params = new  Params();
      params.addChangeListener(new ChangeListener()
      {
            @Override
         public void stateChanged(ChangeEvent evt)
         {
            startAction();
         }
      });
      SwingInstancer.swingRunAndWait(new Runnable()
      {
            @Override
         public void run()
         {
            computeUI = new GUI();
            errorFrame = new FileErrorFrame();
         }
      });
      computeUI.setParams(params);
      ui.addComputeGUI(computeUI);
      setPanel(ui);
   }

   @Override
   public boolean isGenerator() {
      return true;
   }

   public static OutputEgg[] outputEggs = null;

   @Override
   public void onActive()
   {
      boolean binary = false;
      Reader reader;
      if (params.getFileName() != null && !params.getFileName().equals(lastFileName))
      {
         lastFileName = params.getFileName();
         try
         {
            InputStream test = new FileInputStream(params.getFileName());
            binary = test.read() == 7;
         } catch (Exception ex)
         {
            Logger.getLogger(ReadUCD.class.getName()).log(Level.SEVERE, null, ex);
         }

         if (binary)
            outField = new BinaryReader().readUCD(params, errorFrame);
         else
            outField = new ASCIIReader().readUCD(params, errorFrame);
         if (outField == null)
            return;
         computeUI.setFieldDescription(outField.description());
         if (params.isShow())
         {
            prepareOutputGeometry();
            irregularFieldGeometry.getFieldDisplayParams().setShadingMode(AbstractRenderingParams.FLAT_SHADED);
            show();
         }
         setOutputValue("UCD field", new VNIrregularField(outField));
      }
   }

   @Override
   public void onInitFinishedLocal() {
       if(isForceFlag())
           computeUI.activateOpenDialog();
   }

}
