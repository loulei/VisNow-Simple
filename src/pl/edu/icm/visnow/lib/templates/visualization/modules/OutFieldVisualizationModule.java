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

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.datasets.FieldSchema;
import pl.edu.icm.visnow.datasets.IrregularField;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.datasets.cells.Cell;
import pl.edu.icm.visnow.geometries.objects.*;
import pl.edu.icm.visnow.geometries.objects.generics.OpenBranchGroup;
import pl.edu.icm.visnow.geometries.parameters.AbstractDataMappingParams;
import pl.edu.icm.visnow.geometries.parameters.IrregularFieldDisplayParams;
import pl.edu.icm.visnow.geometries.parameters.RegularFieldDisplayParams;
import pl.edu.icm.visnow.geometries.parameters.RenderingParams;
import pl.edu.icm.visnow.lib.templates.visualization.guis.FieldVisualizationGUI;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;
import pl.edu.icm.visnow.lib.utils.TimeStamper;

/**
 *
 * @author Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public abstract class OutFieldVisualizationModule extends VisualizationModule
{

   protected Field outField = null;
   protected FieldSchema lastOutFieldSchema = null;
   protected FieldGeometry fieldGeometry = null;
   protected RegularField outRegularField = null;
   protected RegularFieldGeometry regularFieldGeometry = null;
   protected IrregularField outIrregularField = null;
   protected IrregularFieldGeometry irregularFieldGeometry = null;
   protected IrregularFieldDisplayParams irregularParams = null;
   protected RegularFieldDisplayParams regularParams = null;
   protected FieldVisualizationGUI ui = null;
   protected OpenBranchGroup outGroup = null;

   /**
    * Creates a new instance of VisualizationModule
    */
   public OutFieldVisualizationModule()
   {
      SwingInstancer.swingRun(new Runnable()
      {
         public void run()
         {
            ui = new FieldVisualizationGUI();
         }
      });

      timestamp = TimeStamper.getTimestamp();
      outObj.setName("object" + timestamp);
   }

   protected void show()
   {
      if (fieldGeometry == null)
         return;
      outObj.clearGeometries2D();
      fieldGeometry.updateGeometry();
      outObj.addGeometry2D(fieldGeometry.getColormapLegend());
      outObj.setExtents(outField.getExtents());
   }

   protected void prepareOutputGeometry()
   {
      if (outField == null)
         return;
      boolean newParams = fieldGeometry == null || 
                          fieldGeometry.getField() == null || 
                          !outField.isDataCompatibleWith(fieldGeometry.getField());
      if (outField instanceof IrregularField)
      {
         outIrregularField = (IrregularField) outField;
         irregularFieldGeometry = new IrregularFieldGeometry();
         irregularFieldGeometry.setField(outIrregularField);
         irregularFieldGeometry.setIgnoreUpdate(true);
         if (irregularFieldGeometry.isNewParams())
         {
            this.irregularParams = irregularFieldGeometry.getFieldDisplayParams();
            defaultDisplayParams();             
            SwingInstancer.swingRun(new Runnable()
            {
               @Override
               public void run()
               {
                  ui.setInData(outIrregularField, irregularFieldGeometry.getFieldDisplayParams());
               }
            });

            irregularFieldGeometry.getFieldDisplayParams().addChangeListener(new ChangeListener()
            {
               @Override
               public void stateChanged(ChangeEvent evt)
               {
                  show();
               }
            });
         }
         irregularFieldGeometry.setIgnoreUpdate(false);
         fieldGeometry = irregularFieldGeometry;
      }
      if (outField instanceof RegularField)
      {
         outRegularField = (RegularField) outField;
         switch (outRegularField.getDims().length)
         {
         case 1:
            if (regularFieldGeometry instanceof RegularField1DGeometry)
               regularFieldGeometry.setField(outRegularField);
            else
               regularFieldGeometry = new RegularField1DGeometry(outRegularField.getName());
            break;
         case 2:
            if (regularFieldGeometry instanceof RegularField2DGeometry)
               regularFieldGeometry.setField(outRegularField);
            else
               regularFieldGeometry = new RegularField2DGeometry(outRegularField.getName());
            break;
         case 3:
            if (regularFieldGeometry instanceof RegularField3DGeometry)
               regularFieldGeometry.setField(outRegularField);
            else
               regularFieldGeometry = new RegularField3DGeometry(outRegularField.getName());
            break;
         }
         regularFieldGeometry.setField(outRegularField);
         
         if (newParams) {
            this.regularParams = regularFieldGeometry.getFieldDisplayParams();
            defaultDisplayParams();
            SwingInstancer.swingRun(new Runnable()
            {
               public void run()
               {
                  ui.setInData(outRegularField, regularFieldGeometry.getFieldDisplayParams());
               }
            });
         }
         fieldGeometry = regularFieldGeometry;
      }
      outObj.clearAllGeometry();
      outObj.addNode(fieldGeometry.getGeometry());
      
      outObj2DStruct.addChild(fieldGeometry.getGeometryObj2DStruct());
   }

   public AbstractDataMappingParams getMappingParams()
   {
      return fieldGeometry.getDataMappingParams();
   }
   
    protected void defaultDisplayParams() {
        if (outField == null) {
            return;
        }
        
        int mode = 0;
        if(outField instanceof IrregularField) {
            //set default display params
            boolean renderSurface = false;
            for (int i = 0; i < outIrregularField.getNCellSets(); i++) {
                if ((outIrregularField.getCellSet(i).getCellArray(Cell.TRIANGLE) != null && outIrregularField.getCellSet(i).getCellArray(Cell.TRIANGLE).getNCells() > 0)
                        || (outIrregularField.getCellSet(i).getCellArray(Cell.QUAD) != null && outIrregularField.getCellSet(i).getCellArray(Cell.QUAD).getNCells() > 0)
                        || (outIrregularField.getCellSet(i).getCellArray(Cell.TETRA) != null && outIrregularField.getCellSet(i).getCellArray(Cell.TETRA).getNCells() > 0)
                        || (outIrregularField.getCellSet(i).getCellArray(Cell.PYRAMID) != null && outIrregularField.getCellSet(i).getCellArray(Cell.PYRAMID).getNCells() > 0)
                        || (outIrregularField.getCellSet(i).getCellArray(Cell.PRISM) != null && outIrregularField.getCellSet(i).getCellArray(Cell.PRISM).getNCells() > 0)
                        || (outIrregularField.getCellSet(i).getCellArray(Cell.HEXAHEDRON) != null && outIrregularField.getCellSet(i).getCellArray(Cell.HEXAHEDRON).getNCells() > 0)) {
                    renderSurface = true;
                    break;
                }
            }
            if (renderSurface) {
                //int mode = irregularParams.getDisplayMode();
                irregularParams.setDisplayMode(mode | RenderingParams.SURFACE);
            }

            boolean renderEdges = false;
            if (!renderSurface) {
                for (int i = 0; i < outIrregularField.getNCellSets(); i++) {
                    if ((outIrregularField.getCellSet(i).getCellArray(Cell.SEGMENT) != null && outIrregularField.getCellSet(i).getCellArray(Cell.SEGMENT).getNCells() > 0)) {
                        renderEdges = true;
                        break;
                    }
                }
                if (renderEdges) {
                    //int mode = irregularParams.getDisplayMode();
                    irregularParams.setDisplayMode(mode | RenderingParams.EDGES);
                }
            }

            if (!renderSurface && !renderEdges) {
                boolean renderPoints = false;
                for (int i = 0; i < outIrregularField.getNCellSets(); i++) {
                    if (outIrregularField.getCellSet(i).getCellArray(Cell.POINT) != null
                            && outIrregularField.getCellSet(i).getCellArray(Cell.POINT).getNCells() > 0) {
                        renderPoints = true;
                        break;
                    }
                }
                if (renderPoints) {
                    //int mode = irregularParams.getDisplayMode();
                    irregularParams.setDisplayMode(mode | RenderingParams.NODES);
                }
            }
        } else if(outField instanceof RegularField) {
            int[] dims = outRegularField.getDims();
            //int mode = regularParams.getDisplayParams().getDisplayMode();
            switch (dims.length) {
                case 3:
                    regularParams.getDisplayParams().setDisplayMode(mode | RenderingParams.SURFACE);
                    break;
                case 2:
                    regularParams.getDisplayParams().setDisplayMode(mode | RenderingParams.SURFACE | RenderingParams.IMAGE);
                    break;
                case 1:
                    regularParams.getDisplayParams().setDisplayMode(mode | RenderingParams.EDGES);
                    break;
            }
        }
        

    }
   
   
}
