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

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.datasets.IrregularField;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.engine.core.InputEgg;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.lib.templates.visualization.modules.OutFieldVisualizationModule;
import pl.edu.icm.visnow.lib.types.VNField;
import pl.edu.icm.visnow.lib.types.VNRegularField;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class ComponentOperations extends OutFieldVisualizationModule
{

   public static InputEgg[] inputEggs = null;
   public static OutputEgg[] outputEggs = null;
   protected Field inField = null;
   protected Field lastInField = null;
   protected GUI computeUI = null;
   protected Params params;
   protected boolean fromUI = false;
   protected ComponentSelectorCore coreComponent = new ComponentSelectorCore();
   protected CoordsFromDataCore coreCoords = new CoordsFromDataCore();
   protected VectorOperationsCore coreVectors = new VectorOperationsCore();
   protected MaskCore coreMask = new MaskCore();
   protected ComplexCore coreComplex = new ComplexCore();
   protected boolean dataUnchanged = true;

   public ComponentOperations()
   {
      this.parameters = params = new Params();
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
            computeUI.setParams(params);
            ui.addComputeGUI(computeUI);
            setPanel(ui);            
         }
      });
      
      
      
   }
   
//   @Override
//   public void onOutputAttach(LinkFace link)
//   {
//      System.out.println(link.getOutput().getName());
//      if (outField != null && "outObj".equals(link.getOutput().getName()))
//      {
//         prepareOutputGeometry();
//         show();
//      }
//   }
//   
//   @Override
//   public void onInputDetach(LinkFace link)
//   {
//      System.out.println(link.getOutput().getName());
//      if ("outObj".equals(link.getOutput().getName()))
//         outObj.clearAllGeometry();
//   }

   

   @Override
   public void onActive()
   {
      if (!fromUI)
      {
         if (getInputFirstValue("inField") == null)
            return;
         inField = ((VNField)getInputFirstValue("inField")).getField();
         if (inField == null)
            return;
         if (lastInField == null || !inField.isDataCompatibleWith(lastInField))
         {
            dataUnchanged = false;
            lastInField = inField;
            resetParams();
            computeUI.setInField(inField);
            return;
         }
         lastInField = inField;
         if (!params.isAuto())
            return;
      }
      fromUI = false;      
      if (inField instanceof RegularField)
         outField = ((RegularField)inField).cloneBase();
      else
         outField = ((IrregularField)inField).cloneBase();
      if (params.isUseCoords() && inField instanceof RegularField)
      {
         coreCoords.setData((RegularField) inField, params, (RegularField)outField);
         coreCoords.update();
      } 
      coreComponent.setData(inField, outField, params);
      coreComponent.update();
      coreVectors.setData(inField, coreComponent.getOutField(), params);
      coreVectors.update();
      coreMask.setData(inField, coreComponent.getOutField(), params);
      coreMask.update();
      coreComplex.setData(inField, coreMask.getOutField(), params);
      coreComplex.update();
      
      if (inField instanceof RegularField)
         setOutputValue("regularOutField",new VNRegularField((RegularField)outField));
      setOutputValue("outField", new VNField(outField));
      if (getOutputs().getOutput("outObj").isLinked())
      {
         prepareOutputGeometry();
         show();
      }
   }

   private void resetParams()
   {
      int[] actions;
      boolean[] retain;
      if (inField == null)
      {
         actions = new int[] {Params.NOOP};
         retain = new boolean[]{true};
      }
      else
      {
         int nComps = inField.getNData();
         actions = new int[nComps];
         retain = new boolean[nComps];
         for (int i = 0; i < actions.length; i++)
         {
            actions[i] = Params.NOOP;
            retain[i] = true;
         }
      }
      params.setActions(actions);
      params.setRetain(retain);
   }
}
