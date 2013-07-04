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

import java.awt.image.BufferedImage;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pl.edu.icm.visnow.datasets.IrregularField;
import pl.edu.icm.visnow.datasets.cells.Cell;
import pl.edu.icm.visnow.engine.core.ModuleCore;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.geometries.events.ColorListener;
import pl.edu.icm.visnow.geometries.events.ProjectionListener;
import pl.edu.icm.visnow.geometries.objects.DataMappedGeometryObject;
import pl.edu.icm.visnow.geometries.objects.GeometryObject;
import pl.edu.icm.visnow.geometries.objects.GeometryParent;
import pl.edu.icm.visnow.geometries.objects.IrregularFieldGeometry;
import pl.edu.icm.visnow.geometries.objects.generics.OpenBranchGroup;
import pl.edu.icm.visnow.geometries.parameters.DataMappingParams;
import pl.edu.icm.visnow.geometries.parameters.IrregularFieldDisplayParams;
import pl.edu.icm.visnow.geometries.parameters.RenderingParams;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.pick.Pick3DListener;
import pl.edu.icm.visnow.lib.templates.visualization.guis.Field1dVisualizationGUI;
import pl.edu.icm.visnow.lib.types.VNGeometryObject;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;
import pl.edu.icm.visnow.lib.utils.TimeStamper;


/**
 *
 * @author  Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public abstract class IrregularOutField1DVisualizationModule extends ModuleCore
{
   protected GeometryParent parent                          = null;
   protected IrregularField outField                        = null;
   protected IrregularFieldGeometry irregularFieldGeometry  = null;
   protected IrregularFieldDisplayParams fieldDisplayParams = null;
   protected Field1dVisualizationGUI ui                     = null;
   protected OpenBranchGroup outGroup                       = null;
   protected BufferedImage textureImage                     = null;
   protected int timestamp;
   protected DataMappedGeometryObject  outObj               = new DataMappedGeometryObject();
   public static OutputEgg geometryOutput                   = new OutputEgg("outObj", VNGeometryObject.class, 1);
   
   /** Creates a new instance of VisualizationModule */
   public IrregularOutField1DVisualizationModule()
   {
      SwingInstancer.swingRun(new Runnable()
      {
         public void run()
         {
            ui = new Field1dVisualizationGUI();
         }
      });
      timestamp = TimeStamper.getTimestamp();
      outObj.setName("object"+timestamp);
   }
   
   public void show(DataMappingParams params)
   {
   }
   
   @Override
   public void onInitFinished()
   {
      outObj.getGeometryObj().setUserData(getName());
      irregularFieldGeometry = new IrregularFieldGeometry();
      irregularFieldGeometry.getGeometryObj2DStruct().setParentModulePort(getName() + ".out.outObj");
      setOutputValue("outObj", new VNGeometryObject(outObj, irregularFieldGeometry.getGeometryObj2DStruct()));
   }

   @Override
   public void initPorts()
   {
      initInputs();
      initOutputs();
   }

   @Override
   public void onDelete()
   {
      detach();
   }

   public void attach()
   {
      synchronized(parent)
      {        
         outObj.attach();
      }
   }
   
   public boolean detach()
   {
      return outObj.detach();
   }
   
   public GeometryParent getParent()
   {
      return parent;
   }

   public void setParent(GeometryParent parent)
   {
      synchronized(parent)
      {        
         this.parent = parent;
         outObj.setParentGeom(parent);
      }
   }

   public void setVisible(boolean visible)
   {
         if (visible)
            outObj.attach();
         else
            outObj.detach();
   }

   public ProjectionListener getProjectionListener()
   {
      return null;
   }
   
   public ColorListener getBackgroundColorListener()
   {
      return null;
   }

   public Pick3DListener getPick3DListener()
   {
      return null;
   }

   public GeometryObject getOutObject()
   {
      return outObj;
   }

   protected void show()
   {
      if (irregularFieldGeometry == null)
         return;
      outObj.clearGeometries2D();
      irregularFieldGeometry.updateGeometry();
      for (int i = 0; i < outField.getNCellSets(); i++)
         outObj.addGeometry2D(irregularFieldGeometry.getColormapLegend(i));
      outObj.setExtents(outField.getExtents());
   }

   protected void prepareOutputGeometry()
   {
      if (outField == null)
         return;
      boolean newParams = irregularFieldGeometry == null || 
                          irregularFieldGeometry.getField() == null || 
                          !outField.isDataCompatibleWith(irregularFieldGeometry.getField());
      irregularFieldGeometry.setField(outField);
      irregularFieldGeometry.setIgnoreUpdate(true);
      if (newParams)
      {
          fieldDisplayParams = irregularFieldGeometry.getFieldDisplayParams();
          
          //set default display params
          defaultDisplayParams();
                  
          SwingInstancer.swingRun(new Runnable()
          {
             public void run()
             {         
                ui.getPresentation1DPanel().setInFieldDisplayData(outField, fieldDisplayParams);
             }
          });
         fieldDisplayParams.addChangeListener(new ChangeListener()
            {
               public void stateChanged(ChangeEvent evt)
               {
                  show();
               }
            });
      }
      outObj.clearAllGeometry();
      outGroup = irregularFieldGeometry.getGeometry();
      irregularFieldGeometry.setIgnoreUpdate(false);
      outObj.addNode(outGroup);
   }
   
    protected void defaultDisplayParams() {
        if (outField == null || fieldDisplayParams == null) {
            return;
        }

        //set default display params
        int mode = 0;
        boolean renderSurface = false;
        for (int i = 0; i < outField.getNCellSets(); i++) {
            if ((outField.getCellSet(i).getCellArray(Cell.TRIANGLE) != null && outField.getCellSet(i).getCellArray(Cell.TRIANGLE).getNCells() > 0)
                    || (outField.getCellSet(i).getCellArray(Cell.QUAD) != null && outField.getCellSet(i).getCellArray(Cell.QUAD).getNCells() > 0)
                    || (outField.getCellSet(i).getCellArray(Cell.TETRA) != null && outField.getCellSet(i).getCellArray(Cell.TETRA).getNCells() > 0)
                    || (outField.getCellSet(i).getCellArray(Cell.PYRAMID) != null && outField.getCellSet(i).getCellArray(Cell.PYRAMID).getNCells() > 0)
                    || (outField.getCellSet(i).getCellArray(Cell.PRISM) != null && outField.getCellSet(i).getCellArray(Cell.PRISM).getNCells() > 0)
                    || (outField.getCellSet(i).getCellArray(Cell.HEXAHEDRON) != null && outField.getCellSet(i).getCellArray(Cell.HEXAHEDRON).getNCells() > 0)) {
                renderSurface = true;
                break;
            }
        }
        if (renderSurface) {
            //int mode = fieldDisplayParams.getDisplayMode();            
            fieldDisplayParams.setDisplayMode(mode | RenderingParams.SURFACE);
        }

        boolean renderEdges = false;
        if (!renderSurface) {
            for (int i = 0; i < outField.getNCellSets(); i++) {
                if ((outField.getCellSet(i).getCellArray(Cell.SEGMENT) != null && outField.getCellSet(i).getCellArray(Cell.SEGMENT).getNCells() > 0)) {
                    renderEdges = true;
                    break;
                }
            }
            if (renderEdges) {
                //int mode = fieldDisplayParams.getDisplayMode();
                fieldDisplayParams.setDisplayMode(mode | RenderingParams.EDGES);
            }
        }

        if (!renderSurface && !renderEdges) {
            boolean renderPoints = false;
            for (int i = 0; i < outField.getNCellSets(); i++) {
                if (outField.getCellSet(i).getCellArray(Cell.POINT) != null
                        && outField.getCellSet(i).getCellArray(Cell.POINT).getNCells() > 0) {
                    renderPoints = true;
                    break;
                }
            }
            if (renderPoints) {
                //int mode = fieldDisplayParams.getDisplayMode();
                fieldDisplayParams.setDisplayMode(mode | RenderingParams.NODES);
            }
        }
    }
    
}
