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

package pl.edu.icm.visnow.lib.basic.filters.LocalOperations;


import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.engine.core.InputEgg;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.lib.templates.visualization.modules.RegularOutFieldVisualizationModule;
import pl.edu.icm.visnow.lib.types.VNRegularField;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;

/**
 * @author  Krzysztof S. Nowinski (know@icm.edu.pl)
 * Warsaw University, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */

public class LocalOperations extends RegularOutFieldVisualizationModule
{
   
   public static InputEgg[] inputEggs = null;
   public static OutputEgg[] outputEggs = null;
   protected GUI computeUI = null;
   protected RegularField inField = null;
   protected Params params;
   protected LocalOps ops = null;
   protected boolean fromUI = false;
   
   public LocalOperations()
   {
      parameters = params = new Params();
      params.addChangeListener(new ChangeListener()
      {
         public void stateChanged(ChangeEvent evt)
         {
             fromUI = true;
             startAction();
         }
      });
      SwingInstancer.swingRun(new Runnable()
      {
         public void run()
         {
            computeUI = new GUI();
         }
      });
      computeUI.setParams(params);
      ui.addComputeGUI(computeUI);
      setPanel(ui);
   }
   
   
   public void progress(int n)
   {
      setProgress((float)n/(inField.getDims()[2]-2*params.getRadius()));
   }
   
   public void update()
   {
      if (inField.getDims().length==3)
         ops = new LocalOps3D();
      else if (inField.getDims().length==2)
         ops = new LocalOps2D();
      else 
         return;
      outField = ops.compute(inField, params);
      if (!prepareOutputGeometry())
         return;
      show();
      setOutputValue("outField", new VNRegularField(outField));
   }
   
   @Override
   public void onActive()
   {
      if (!fromUI)
      {
         if (getInputFirstValue("inField") == null)
            return;
         inField = ((VNRegularField) getInputFirstValue("inField")).getField();
         if (params.isAuto())
            update();
      } else
      {
         update();
         fromUI = false;
      }
   }
}
