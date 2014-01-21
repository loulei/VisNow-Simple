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

package pl.edu.icm.visnow.lib.basic.filters.SmoothDown;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.engine.core.InputEgg;
import pl.edu.icm.visnow.engine.core.ModuleCore;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.lib.types.VNRegularField;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;
import pl.edu.icm.visnow.lib.utils.field.FieldSmoothDown;
import pl.edu.icm.visnow.lib.utils.numeric.splines.SplineUtilities;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class SmoothDown extends ModuleCore
{

   private GUI ui = new GUI();
   protected Params params;
   protected RegularField inField = null;
   protected boolean fromGUI = false;
   private SplineUtilities spUtil = null;

   public SmoothDown()
   {
      parameters = params = new Params();
      params.addChangeListener(new ChangeListener()
      {
         public void stateChanged(ChangeEvent evt)
         {
            fromGUI = true;
            startAction();
         }
      });
      SwingInstancer.swingRunAndWait(new Runnable()
      {
         public void run()
         {
            ui = new GUI();
         }
      });
      ui.setParams(params);
      setPanel(ui);
   }
   private static InputEgg[] inputEggs = null;
   private static OutputEgg[] outputEggs = null;

   public static InputEgg[] getInputEggs()
   {
      if (inputEggs == null)
         inputEggs = new InputEgg[] {new InputEgg("inField", VNRegularField.class,
                                                  InputEgg.NECESSARY | InputEgg.TRIGGERING),};
      return inputEggs;
   }

   public static OutputEgg[] getOutputEggs()
   {
      if (outputEggs == null)
         outputEggs = new OutputEgg[] {new OutputEgg("outField", VNRegularField.class)};
      return outputEggs;
   }
   
   @Override
   public void onActive()
   {
      if (getInputFirstValue("inField") == null ||
          ((VNRegularField) getInputFirstValue("inField")).getField() == null)
         return;
      if (!fromGUI)
      {
         inField = ((VNRegularField) getInputFirstValue("inField")).getField();
         if (inField.getDims() == null || inField.getDims().length <2)
            return;
      }
      else
         setOutputValue("outField",
                        new VNRegularField(FieldSmoothDown.smoothDown(inField, params.getDownsize(),
                                                                             params.getSigma(), params.getNThreads())));
      fromGUI = false;
   }
}
