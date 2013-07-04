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

package pl.edu.icm.visnow.lib.templates.visualization.modules;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.geometries.objects.RegularField1DGeometry;
import pl.edu.icm.visnow.geometries.objects.RegularField2DGeometry;
import pl.edu.icm.visnow.geometries.objects.RegularField3DGeometry;
import pl.edu.icm.visnow.geometries.objects.RegularFieldGeometry;
import pl.edu.icm.visnow.geometries.objects.generics.OpenBranchGroup;
import pl.edu.icm.visnow.geometries.parameters.RegularFieldDisplayParams;
import pl.edu.icm.visnow.geometries.parameters.RenderingParams;
import pl.edu.icm.visnow.lib.templates.visualization.guis.RegularFieldVisualizationGUI;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;
import pl.edu.icm.visnow.lib.utils.TimeStamper;


/**
 *
 * @author  Krzysztof S. Nowinski, University of Warsaw, ICM
 * @author  Bartosz Borucki, University of Warsaw, ICM
 * 
 */
public abstract class RegularOutFieldVisualizationModule extends VisualizationModule
{

   protected RegularField outField                          = null;
   protected RegularFieldGeometry regularFieldGeometry      = null;
   protected RegularFieldDisplayParams fieldDisplayParams   = null;
   protected RegularFieldVisualizationGUI ui                = null;
   protected OpenBranchGroup outGroup                       = null;
   protected int lastDims = -1;
   
   /** Creates a new instance of VisualizationModule */
   public RegularOutFieldVisualizationModule()
   {
      SwingInstancer.swingRun(new Runnable()
      {
         @Override
         public void run()
         {
            ui = new RegularFieldVisualizationGUI();
         }
      });
      timestamp = TimeStamper.getTimestamp();
      outObj.setName("object"+timestamp);
   }
   
   protected void show()
   {
      if (regularFieldGeometry == null)
         return;
      if (outField == null) {
          outObj.clearGeometries2D();
          outObj.clearAllGeometry();
          return;
      }
      outObj.clearGeometries2D();
      regularFieldGeometry.updateGeometry();
      outObj.addGeometry2D(regularFieldGeometry.getColormapLegend());
      outObj.setExtents(outField.getExtents());
   }

   protected boolean prepareOutputGeometry()
   {
      if (outField == null)
         return false;
      boolean newParams = regularFieldGeometry == null ||
                          regularFieldGeometry.getField() == null ||
                          !outField.isStructureCompatibleWith(regularFieldGeometry.getField()) ||
                          !outField.isDataCompatibleWith(regularFieldGeometry.getField());
      if (lastDims != outField.getDims().length || regularFieldGeometry == null)
      {
         switch (outField.getDims().length)
         {
         case 3:
            regularFieldGeometry = new RegularField3DGeometry(outField.getName());
            break;
         case 2:
            regularFieldGeometry = new RegularField2DGeometry(outField.getName());
            break;
         case 1:
            regularFieldGeometry = new RegularField1DGeometry(outField.getName());
            break;
         }
         lastDims = outField.getDims().length;
      }
      regularFieldGeometry.setField(outField);
      fieldDisplayParams = regularFieldGeometry.getFieldDisplayParams();
      
      if (newParams) {          
         defaultDisplayParams();          
         SwingInstancer.swingRun(new Runnable()
         {
         @Override
            public void run()
            {
               ui.setInFieldDisplayData(outField, fieldDisplayParams);
               ui.getMapperGUI().setSignalingTransform(outObj.getCurrentTransform());
            }
         });
      }
      
      outObj.clearAllGeometry();
      outGroup = regularFieldGeometry.getGeometry();
      outObj.addNode(outGroup);      
      
      outObj2DStruct.addChild(regularFieldGeometry.getGeometryObj2DStruct());
      
      return true;
    }

    protected void defaultDisplayParams() {
        if (outField == null || fieldDisplayParams == null) {
            return;
        }

        //set default display params
        int mode = 0;
        int[] dims = outField.getDims();
        //int mode = fieldDisplayParams.getDisplayParams().getDisplayMode();
        switch (dims.length) {
            case 3:
                fieldDisplayParams.getDisplayParams().setDisplayMode(mode | RenderingParams.SURFACE);
                break;
            case 2:
                fieldDisplayParams.getDisplayParams().setDisplayMode(mode | RenderingParams.SURFACE | RenderingParams.IMAGE);
                break;
            case 1:
                fieldDisplayParams.getDisplayParams().setDisplayMode(mode | RenderingParams.EDGES);
                break;
        }

    }
  
}
