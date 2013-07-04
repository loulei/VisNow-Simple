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

package pl.edu.icm.visnow.lib.basic.mappers.SurfaceComponents;

import javax.media.j3d.PickInfo;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pl.edu.icm.visnow.datasets.IrregularField;
import pl.edu.icm.visnow.engine.core.InputEgg;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.gui.events.FloatValueModificationEvent;
import pl.edu.icm.visnow.gui.events.FloatValueModificationListener;
import pl.edu.icm.visnow.lib.templates.visualization.modules.IrregularOutFieldVisualizationModule;
import pl.edu.icm.visnow.lib.types.VNField;
import pl.edu.icm.visnow.lib.types.VNIrregularField;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;
import pl.edu.icm.visnow.lib.utils.field.ExtractCellSets;

/**
 * @author  Krzysztof S. Nowinski (know@icm.edu.pl)
 * Warsaw University, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class SurfaceComponents extends IrregularOutFieldVisualizationModule
{

   public static InputEgg[] inputEggs = null;
   public static OutputEgg[] outputEggs = null;
   private ProcessComponents processor = new ProcessComponents();
   private GUI computeUI = null;
   private Params params = new Params();
   private IrregularField inField = null;
   private IrregularField currentField = null;
   protected boolean fromGUI = false;

   public SurfaceComponents()
   {
      parameters = params = new Params();
      params.addChangeListener(new ChangeListener()
      {
         @Override
         public void stateChanged(ChangeEvent evt)
         {
            fromGUI = true;
            int mC = params.getModifiedComponent();
            if (mC >= 0)
               updateCellSetData(mC,  params.getModifiedName(), params.getModifiedSelection(), true);
            else
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
      computeUI.setParams(params);
      ui.addComputeGUI(computeUI);
      setPanel(ui);
      processor.addFloatValueModificationListener(
           new FloatValueModificationListener()
           {
              @Override
              public void floatValueChanged(FloatValueModificationEvent e)
              {
                 setProgress(e.getVal());
              }
           });
      processor.addChangeListener(computeUI.getNewCmpListener());
   }

   @Override
   public void showPickInfo(String pickedItemName, PickInfo info)
   {
      computeUI.updateComponentTable();
   }

   @Override
   public void onActive()
   {
      if (!fromGUI)
      {
         if (getInputFirstValue("inField") == null || ((VNField) getInputFirstValue("inField")).getField() == null)
            return;
         if (!(((VNField)getInputFirstValue("inField")).getField() instanceof IrregularField))
            return;
         currentField = (IrregularField)((VNField) getInputFirstValue("inField")).getField();
         if (inField == null || currentField != inField)
            inField = currentField;
         else
            return;
         if (processor.getInField() == null || inField != processor.getInField())
         {
            processor.setInField(inField, params);
         }
      }
      fromGUI = false;
      if (params.output())
      {
         params.setOutput(false);
         IrregularField resField = ExtractCellSets.extractCellSets(outField);
         if (resField == null)
            return;
         setOutputValue("outField", new VNIrregularField(resField));
      }
      else
      {
         outField = processor.buildOutput(params);
         computeUI.updateComponentTable(outField);
         ui.setInFieldDisplayData(outField, fieldDisplayParams);
         prepareOutputGeometry();
         show(true);
      }
   }
}
