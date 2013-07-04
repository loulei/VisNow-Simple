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
import org.apache.log4j.Logger;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.engine.core.ModuleCore;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.geometries.events.ColorListener;
import pl.edu.icm.visnow.geometries.events.ProjectionListener;
import pl.edu.icm.visnow.geometries.objects.GeometryObject;
import pl.edu.icm.visnow.geometries.objects.GeometryParent;
import pl.edu.icm.visnow.geometries.objects.RegularField1DGeometry;
import pl.edu.icm.visnow.geometries.objects.generics.OpenBranchGroup;
import pl.edu.icm.visnow.geometries.parameters.DataMappingParams;
import pl.edu.icm.visnow.geometries.parameters.RegularFieldDisplayParams;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.pick.Pick3DListener;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.render.RenderEvent;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.render.RenderEventListener;
import pl.edu.icm.visnow.lib.templates.visualization.guis.Field1dVisualizationGUI;
import pl.edu.icm.visnow.lib.types.VNGeometryObject;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;
import pl.edu.icm.visnow.lib.utils.TimeStamper;


/**
 *
 * @author  Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public abstract class RegularOutField1DVisualizationModule extends ModuleCore
{
    private static final Logger LOGGER = Logger.getLogger(RegularOutField1DVisualizationModule.class);
    
   protected GeometryParent parent                        = null;
   protected RegularField outField                        = null;
   protected RegularField1DGeometry outFieldGeometry      = new RegularField1DGeometry("line");
   protected RegularFieldDisplayParams fieldDisplayParams = null;
   protected Field1dVisualizationGUI ui                   = null;
   protected OpenBranchGroup outGroup                     = null;
   protected BufferedImage textureImage                   = null;
   protected int timestamp;
   public static OutputEgg geometryOutput                 = new OutputEgg("outObj", VNGeometryObject.class, 1);
   
   /** Creates a new instance of VisualizationModule */
   public RegularOutField1DVisualizationModule()
   {
      SwingInstancer.swingRun(new Runnable()
      {
         public void run()
         {
            ui = new Field1dVisualizationGUI();
         }
      });
      timestamp = TimeStamper.getTimestamp();
   }
   
   public void show(DataMappingParams params)
   {
   }
   
   @Override
   public void onInitFinished()
   {
      outFieldGeometry.getGeometryObj().setUserData(getName());
      setOutputValue("outObj", new VNGeometryObject(outFieldGeometry));
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
       LOGGER.trace("");
      synchronized(parent)
      {        
         outFieldGeometry.attach();
      }
   }
   
   public boolean detach()
   {
       LOGGER.trace("");
      return outFieldGeometry.detach();
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
         outFieldGeometry.setParentGeom(parent);
      }
   }

   public void setVisible(boolean visible)
   {
         if (visible)
            outFieldGeometry.attach();
         else
            outFieldGeometry.detach();
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
      return outFieldGeometry;
   }

   protected void show()
   {
      if (outFieldGeometry == null)
         return;
      outFieldGeometry.clearGeometries2D();
      outFieldGeometry.updateGeometry();
      outFieldGeometry.addGeometry2D(outFieldGeometry.getColormapLegend());
      outFieldGeometry.setExtents(outField.getExtents());
   }
   
   RenderEventListener renderEventListener = new RenderEventListener() 
         {
            public void renderExtentChanged(RenderEvent e)
            {
               outFieldGeometry.updateGeometry();
            }
         };
   
   protected void prepareOutputGeometry()
   {
      if (outField == null)
         return;
      boolean newParams = outFieldGeometry == null ||
                          outFieldGeometry.getField() == null || 
                          !outField.isStructureCompatibleWith(outFieldGeometry.getField()) ||
                          !outField.isDataCompatibleWith(outFieldGeometry.getField());
      outFieldGeometry.setField(outField);
      if (newParams)
      {
         fieldDisplayParams = outFieldGeometry.getFieldDisplayParams();
          SwingInstancer.swingRun(new Runnable()
          {
             public void run()
             {         
                ui.getPresentation1DPanel().setInFieldDisplayData(outField, fieldDisplayParams);
             }
          });
         
         fieldDisplayParams.getDisplayParams().addRenderEventListener(renderEventListener);
         fieldDisplayParams.getMappingParams().addRenderEventListener(renderEventListener);
      }
      outFieldGeometry.clearAllGeometry();
      outGroup = outFieldGeometry.getGeometry();
      outFieldGeometry.addNode(outGroup);
   }
}
