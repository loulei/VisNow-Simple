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

package pl.edu.icm.visnow.lib.templates.visualization.modules;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.media.j3d.PickInfo;
import pl.edu.icm.visnow.engine.core.ModuleCore;
import pl.edu.icm.visnow.engine.core.Output;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.engine.main.ModuleSaturation;
import pl.edu.icm.visnow.geometries.events.ColorListener;
import pl.edu.icm.visnow.geometries.events.ProjectionListener;
import pl.edu.icm.visnow.geometries.objects.DataMappedGeometryObject;
import pl.edu.icm.visnow.geometries.objects.GeometryObject;
import pl.edu.icm.visnow.geometries.objects.GeometryParent;
import pl.edu.icm.visnow.geometries.parameters.AbstractRenderingParams;
import pl.edu.icm.visnow.geometries.parameters.DataMappingParams;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.pick.Pick3DListener;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.render.FrameRenderedListener;
import pl.edu.icm.visnow.lib.types.VNGeometryObject;
import pl.edu.icm.visnow.lib.utils.TimeStamper;
import pl.edu.icm.visnow.lib.utils.geometry2D.GeometryObject2DStruct;


/**
 *
 * @author  Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public abstract class VisualizationModule extends ModuleCore implements RenderWindowListeningModule
{
   public static final int VERTICAL   = 0;
   public static final int HORIZONTAL = 1;
   public static final int MICRO      = 2;
   protected GeometryObject2DStruct outObj2DStruct   = new GeometryObject2DStruct();
   protected DataMappedGeometryObject  outObj        = new DataMappedGeometryObject();
   protected GeometryParent parent                   = null;
   protected DataMappingParams dataMappingParams     = outObj.getDataMappingParams();
   protected AbstractRenderingParams renderingParams = outObj.getRenderingParams();
   protected BufferedImage textureImage              = null;
   protected int timestamp                           = 0;
   protected Color bgrColor                          = Color.BLACK ;
   protected boolean uiOnFrame                       = false;
   public static OutputEgg geometryOutput            = new OutputEgg("outObj", VNGeometryObject.class, 1);

   public class ModuleIdData
   {
      protected VisualizationModule module;

      public ModuleIdData(VisualizationModule module)
      {
         this.module = module;
      }

      public String getModuleId()
      {
         return this.module.getName();
      }

      public VisualizationModule getModule()
      {
         return module;
      }

   }
   /** Creates a new instance of VisualizationModule */
   public VisualizationModule()
   {
      outObj.setCreator(this);
      
      timestamp = TimeStamper.getTimestamp();
      outObj.setName("object"+timestamp);
      outObj2DStruct.setName("object"+timestamp);
   }

   public void show(DataMappingParams params)
   {
   }

   private void visInitFinished()
   {
      outObj.getGeometryObj().setUserData(new ModuleIdData(this));
      outObj2DStruct.setParentModulePort(this.getName() + ".out.outObj");
      setOutputValue("outObj", new VNGeometryObject(outObj, outObj2DStruct));
   }

   @Override
   public final void onInitFinished()
   {
       //UWAGA - nie zamieniać kolejności wywołania dwóch kolejnych linii.
       //Powoduje to zawieszanie VN przy uzyciu File -> Open data.
       visInitFinished();
       onInitFinishedLocal();
   }

   public void onInitFinishedLocal()
   {
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

   public Color getBgrColor()
   {
      return bgrColor;
   }

   public void setVisible(boolean visible)
   {
         if (visible)
            outObj.attach();
         else
            outObj.detach();
   }

   @Override
   public ProjectionListener getProjectionListener()
   {
      return null;
   }

   @Override
   public ColorListener getBackgroundColorListener()
   {
      return null;
   }

   @Override
   public FrameRenderedListener getFrameRenderedListener()
   {
      return null;
   }

   @Override
   public Pick3DListener getPick3DListener()
   {
      return parameters.getPick3DListener();
   }

   public GeometryObject getOutObject()
   {
      return outObj;
   }

   public void showPickInfo(String pickedItemName, PickInfo info)
   {
      System.out.println(pickedItemName + " " + info);
   }

   public void processPickInfo(String pickedItemName, PickInfo info)
   {

   }

    @Override
    public void onLocalSaturationChange(ModuleSaturation mSaturation) {
        if (mSaturation == ModuleSaturation.wrongData || mSaturation == ModuleSaturation.noData || mSaturation == ModuleSaturation.notLinked) {
            for (Output output : this.getOutputs()) {
                if (output.getType() == VNGeometryObject.class) {
                    continue;
                }
                output.setValue(null);
            }
            outObj.clearAllGeometry();
            //outObj2DStruct.setGeometryObject2D(null); ??
        }        
    }
}
