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

package pl.edu.icm.visnow.lib.basic.mappers.TextGlyphs;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.engine.core.InputEgg;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.lib.templates.visualization.modules.VisualizationModule;
import pl.edu.icm.visnow.lib.types.VNField;
import pl.edu.icm.visnow.lib.types.VNGeometryObject;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;

/**
 * @author  Krzysztof S. Nowinski (know@icm.edu.pl)
 * Warsaw University, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class TextGlyphs extends VisualizationModule //implements RenderWindowListeningModule
{

   /**
    * Creates a new instance of CreateGrid
    */
   public static InputEgg[] inputEggs = null;
   public static OutputEgg[] outputEggs = null;
   private GUI ui = null;
   private Params params;
   private Field inField = null;
   private boolean fromIn = false;
   private GlyphsObject glyphObj = new GlyphsObject();

   public TextGlyphs()
   {
      parameters = params = new Params();
      params.addChangeListener(new ChangeListener()
      {
         @Override
         public void stateChanged(ChangeEvent evt)
         {
            if (inField != null && !fromIn)
               glyphObj.update(inField, params);
         }
      });

      SwingInstancer.swingRunAndWait(new Runnable()
      {
         @Override
         public void run()
         {
            ui = new GUI();
            ui.setParams(params);
            setPanel(ui);
         }
      });
   }

  @Override
   public void onInitFinishedLocal()
   {
       outObj = glyphObj;
       outObj.setCreator(this);
       setOutputValue("outObj", new VNGeometryObject(outObj));
   }

   @Override
   public void onActive()
   {
      if (getInputFirstValue("inField") != null &&
          ((VNField) getInputFirstValue("inField")).getField() != null)
      {
         params.setActive(false);
         fromIn = true;
         if (((VNField) getInputFirstValue("inField")).getField() != inField)
         {
            inField = ((VNField) getInputFirstValue("inField")).getField();
            ui.setInData(inField, dataMappingParams);
            params.setChange(Params.COUNT_CHANGED);
         }
         else
            params.setChange(Params.GLYPHS_CHANGED);
         params.setActive(true);
         fromIn = false;
      }
      if (params.getComponent() < 0)
         return;
      glyphObj.update(inField, params);
   }
}
