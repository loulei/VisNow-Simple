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

package pl.edu.icm.visnow.lib.basic.viewers.FieldViewer1D;

import java.util.Vector;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.datasets.FieldSchema;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.engine.core.InputEgg;
import pl.edu.icm.visnow.engine.core.LinkFace;
import pl.edu.icm.visnow.engine.core.ModuleCore;
import pl.edu.icm.visnow.lib.types.VNRegularField;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;

/**
 * @author  Krzysztof S. Nowinski (know@icm.edu.pl)
 * Warsaw University, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class FieldViewer1D extends ModuleCore
{
   protected Viewer1DFrame window;
   protected FieldSchema currentSchema = null;
   protected Vector<DataArray> dataArrays = new Vector<DataArray>();
   private GUI ui = null;

   /**
    * Creates a new instance of Viewer3D
    */
   public FieldViewer1D()
   {
      SwingInstancer.swingRunAndWait(new Runnable()
      {
         public void run()
         {
            ui = new GUI();
            window = new Viewer1DFrame();
            window.setTitle("Viewer1D");
            window.setVisible(true);
            ui.addChangeListener(new ChangeListener()
            {
               public void stateChanged(ChangeEvent evt)
               {
                  window.setVisible(true);
               }
            });
            setPanel(ui);
         }
      });
      //WTF-MUI:addModuleUI(ui);
   }

    public static InputEgg[] inputEggs = null;

//    public static InputEgg[] getInputEggs() {
//        if (inputEggs == null) {
//            inputEggs = new InputEgg[]{
//                        new InputEgg("inField", VNRegularField.class, InputEgg.NECESSARY | InputEgg.TRIGGERING |  InputEgg.NORMAL),
//                    };
//        }
//        return inputEggs;
//    }

   @Override
   public void onDelete()
   {
      window.dispose();
   }

   @Override
   public void onInputDetach(LinkFace link)
   {
      onActive();
   }

   @Override
   public void onActive()
   {
      if (getInputFirstValue("inField")==null)
              return;
      RegularField inFld = ((VNRegularField)getInputFirstValue("inField")).getField();
      if (inFld == null || inFld.getDims() == null || inFld.getDims().length != 1)
         return;
      dataArrays.clear();
      for (int i = 0; i < inFld.getNData(); i++)
         if (inFld.getData(i).getVeclen() == 1)
            dataArrays.add(inFld.getData(i));
      if (dataArrays.isEmpty())
         return;
      window.setDataArrays(dataArrays, currentSchema == null || !currentSchema.isDataCompatibleWith(inFld.getSchema()), inFld.getSchema());
      currentSchema = inFld.getSchema();
   }
   
    @Override
    public boolean isViewer() {
        return true;
    }
   
}

