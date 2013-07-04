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

package pl.edu.icm.visnow.lib.basic.mappers.VolumeRenderer;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.GraphicsConfigTemplate3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransparencyAttributes;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.log4j.Logger;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.engine.core.InputEgg;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.geometries.events.ColorListener;
import pl.edu.icm.visnow.geometries.events.ProjectionEvent;
import pl.edu.icm.visnow.geometries.events.ProjectionListener;
import pl.edu.icm.visnow.geometries.objects.ColormapLegend;
import pl.edu.icm.visnow.geometries.utils.transform.LocalToWindow;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.pick.Pick3DEvent;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.pick.Pick3DListener;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.render.FrameRenderedListener;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.render.RenderEvent;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.render.RenderEventListener;
import pl.edu.icm.visnow.gui.events.FloatValueModificationEvent;
import pl.edu.icm.visnow.gui.events.FloatValueModificationListener;
import pl.edu.icm.visnow.lib.templates.visualization.modules.VisualizationModule;
import pl.edu.icm.visnow.lib.types.VNRegularField;
import static pl.edu.icm.visnow.lib.utils.CropDown.*;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;

/**
 * @author Krzysztof S. Nowinski (know@icm.edu.pl) Warsaw University, Interdisciplinary Centre for
 * Mathematical and Computational Modelling
 */
public class VolumeRenderer extends VisualizationModule
{
    private static final Logger LOGGER = Logger.getLogger(VolumeRenderer.class);
   public static InputEgg[] inputEggs = null;
   public static OutputEgg[] outputEggs = null;
   public static String helpTopicID = null;
   protected TextureVolumeRenderer volRender;
   protected GUI ui = null;
   protected Params params = new Params();
   protected RegularField inField;
   protected int[] inDims = null;
   protected float[][] af = null;
   protected float[][] ia = null;
   protected float[][] phys_exts = null;
   protected float[] picked_world_coords = new float[3];
   protected int[] picked_index = null;
   protected float[] picked_physical_coords = new float[3];
   protected ColormapLegend colormapLegend = outObj.getColormapLegend();
   protected RegularField outField = null;
   protected boolean needPowerOf2Textures = true;
   protected LocalToWindow locToWin = null;
   private boolean ignoreUI = false;

   public VolumeRenderer()
   {
      this(true);
   }

   public VolumeRenderer(boolean lendUI)
   {
      parameters = params;
      SwingInstancer.swingRun(new Runnable()
      {
         @Override
         public void run()
         {
            ui = new GUI();
            ui.setGUIPresentation();
         }
      });
      params.setDataMappingParams(dataMappingParams);
      ui.setParams(params);
      ui.getCropUI().addChangeListener(new ChangeListener()
      {
         @Override
         public void stateChanged(ChangeEvent evt)
         {
            if (ignoreUI)
               return;
            outObj.detach();
            if (volRender != null)
            {
               volRender.setCrop(ui.getCropUI().getLow(), ui.getCropUI().getUp());
            }
            outObj.attach();
         }
      });

      dataMappingParams.addRenderEventListener(new RenderEventListener()
      {
         @Override
         public void renderExtentChanged(RenderEvent e)
         {
            if (ignoreUI)
               return;
            if (volRender == null)
            {
               return;
            }
            if ((e.getUpdateExtent() & RenderEvent.COLORS) != 0)
            {
               volRender.updateTextureColors();
            }
            if ((e.getUpdateExtent() & RenderEvent.TEXTURE) != 0)
            {
               volRender.updateTextureFromUV();
            }
            if ((e.getUpdateExtent() & RenderEvent.TRANSPARENCY) != 0)
            {
               volRender.updateTextureTransparency();
            }
         }
      });

      ui.addChangeListener(new ChangeListener()
      {
         @Override
         public void stateChanged(ChangeEvent evt)
         {
            if (params.isOutCroppedField())
            {
               startAction();
            }
            outObj.detach();
            if (volRender != null)
            {
               volRender.updateTexture();
            }
         }
      });
      ui.setDataMappingParams(dataMappingParams);
      if (lendUI)
      {
         setPanel(ui);
      }
      outObj.setName("VolRender");
      outObj.getRenderingParams().getAppearance().getTransparencyAttributes().setTransparencyMode(TransparencyAttributes.NICEST);
      outObj.setCreator(this);
      colormapLegend.setParams(dataMappingParams.getColormapLegendParameters());
      outObj.addGeometry2D(colormapLegend);
      GraphicsConfiguration gcfg =
              GraphicsEnvironment.getLocalGraphicsEnvironment().
              getDefaultScreenDevice().getBestConfiguration(new GraphicsConfigTemplate3D());
      Canvas3D canvas = new Canvas3D(gcfg);
      needPowerOf2Textures = !(Boolean) canvas.queryProperties().get("textureNonPowerOfTwoAvailable");
      canvas = null;
      gcfg = null;
   }

   public GUI getVolRenderUI()
   {
      return ui;
   }

   public Params getParams()
   {
      return params;
   }

   public void setInField(RegularField in)
   {
      outObj.clearAllGeometry();
      volRender = null;
      if (in == null)
      {
         ui.setInField(null, params.getDataMappingParams());
         update();
         return;
      }
      int[] inD = in.getDims();
      if (inD.length != 3 || inD[0] < 2 || inD[1] < 2 || inD[2] < 2 || in.getNData() < 1)
      {
         return;
      }
      inField = in;
      inDims = inD;
      af = inField.getAffine();
      ia = inField.getInvAffine();
      phys_exts = inField.getPhysExts();
      params.getDataMappingParams().setInField(inField);
      ui.setInField(this.inField, params.getDataMappingParams());
      volRender = new TextureVolumeRenderer(inField, params, needPowerOf2Textures);
      outObj.addNode(volRender);
      outObj.setExtents(inField.getExtents());
      update();
   }

   @Override
   public void initParameters()
   {
      parameters = params;
   }

   public void update()
   {
       LOGGER.debug("");
      outObj.detach();
      if (volRender != null)
      {
         volRender.updateTexture();
         volRender.updateMesh();
      } else
      {
         outObj.clearAllGeometry();
      }

      if (inField != null)
      {
         outObj.setExtents(inField.getExtents());
         outObj.attach();
      }
   }

   @Override
   public void onActive()
   {
      if (getInputFirstValue("inField") == null)
      {
         return;
      }
      if (params.isOutCroppedField())
      {
         params.setOutCroppedField(false);
         if (inField == null)
         {
            return;
         }
         int[] outDims = new int[inDims.length];
         int[] low = ui.getCropUI().getLow();
         int[] up = ui.getCropUI().getUp();
         int[] down = new int[]
         {
            1, 1, 1
         };
         for (int i = 0; i < outDims.length; i++)
         {
            if (low[i] < 0 || up[i] > inDims[i])
            {
               return;
            }
            outDims[i] = up[i] - low[i];
         }
         outField = new RegularField(outDims);
         outField.setNSpace(inField.getNSpace());
         if (inField.getCoords() != null)
         {
            outField.setCoords(cropDownArray(inField.getCoords(), inField.getNSpace(), inDims, low, up, down));
         } else
         {
            float[][] outAffine = new float[4][3];
            float[][] affine = inField.getAffine();
            System.arraycopy(affine[3], 0, outAffine[3], 0, 3);
            for (int i = 0; i < outDims.length; i++)
            {
               for (int j = 0; j < 3; j++)
               {
                  outAffine[3][j] += low[i] * affine[i][j];
                  outAffine[i][j] = affine[i][j];
               }
            }
            if (params.isCropExtents())
            {
               float[] l = new float[]
               {
                  Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE
               };
               float[] u = new float[]
               {
                  -Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE
               };
               for (int i = 0; i < 3; i++)
               {
                  for (int j = 0; j < 2; j++)
                  {
                     for (int k = 0; k < 2; k++)
                     {
                        for (int m = 0; m < 2; m++)
                        {
                           float f = affine[3][i] + j * outDims[2] * affine[2][j]
                                   + k * outDims[1] * affine[1][j]
                                   + m * outDims[0] * affine[0][j];
                           if (f < l[i])
                           {
                              l[i] = f;
                           }
                           if (f > u[i])
                           {
                              u[i] = f;
                           }
                        }
                     }
                  }
                  affine[3][i] -= .5f * (l[i] + u[i]);
               }
            }
            outField.setAffine(outAffine);
         }

         for (int i = 0; i < inField.getNData(); i++)
         {
            DataArray dta = inField.getData(i);
            switch (dta.getType())
            {
               case DataArray.FIELD_DATA_BYTE:
                  byte[] bData = cropDownArray(dta.getBData(), dta.getVeclen(), inDims, low, up, down);
                  outField.addData(DataArray.create(bData, dta.getVeclen(), dta.getName(), dta.getUnit(), dta.getUserData()));
                  break;
               case DataArray.FIELD_DATA_SHORT:
                  short[] sData = cropDownArray(dta.getSData(), dta.getVeclen(),
                          inDims, low, up, down);
                  outField.addData(DataArray.create(sData, dta.getVeclen(), dta.getName(), dta.getUnit(), dta.getUserData()));
                  break;
               case DataArray.FIELD_DATA_INT:
                  int[] iData = cropDownArray(dta.getIData(), dta.getVeclen(), inDims, low, up, down);
                  outField.addData(DataArray.create(iData,
                          dta.getVeclen(), dta.getName(), dta.getUnit(), dta.getUserData()));
                  break;
               case DataArray.FIELD_DATA_FLOAT:
                  float[] fData = cropDownArray(dta.getFData(), dta.getVeclen(), inDims, low, up, down);
                  outField.addData(DataArray.create(fData, dta.getVeclen(), dta.getName(), dta.getUnit(), dta.getUserData()));
                  break;
               case DataArray.FIELD_DATA_DOUBLE:
                  double[] dData = cropDownArray(dta.getDData(), dta.getVeclen(), inDims, low, up, down);
                  outField.addData(DataArray.create(dData, dta.getVeclen(), dta.getName(), dta.getUnit(), dta.getUserData()));
                  break;
            }
         }
         setOutputValue("croppedField", new VNRegularField(outField));
         return;
      }
      RegularField in = ((VNRegularField) getInputFirstValue("inField")).getField();
      if (in == null)
      {
         return;
      }
      if (in != inField)
      {
         ignoreUI = true;
         outObj.clearAllGeometry();
         volRender = null;
         int[] inD = in.getDims();
         if (inD.length != 3
                 || inD[0] < 2 || inD[1] < 2 || inD[2] < 2
                 || in.getNData() < 1)
            return;
         
         //outObj.clearAllGeometry();
         int cFr = in.getCurrentFrame();
         inField = in;
         inDims = inD;
         af = inField.getAffine();
         ia = inField.getInvAffine();
         phys_exts = inField.getPhysExts();
         inField.setCurrentFrame(cFr);
         params.getDataMappingParams().setParentObjectSize(inField.getNNodes());
         volRender = new TextureVolumeRenderer(inField, params, needPowerOf2Textures);
         volRender.addFloatValueModificationListener(
                 new FloatValueModificationListener()
         {
            @Override
            public void floatValueChanged(FloatValueModificationEvent e)
            {
               setProgress(e.getVal());
            }
         });
         ui.setInField(this.inField, params.getDataMappingParams());
         updateProjection();
         outObj.addNode(volRender);
         ignoreUI = false;
      }
      update();
   }

   @Override
   public FrameRenderedListener getFrameRenderedListener()
   {
      return null;
   }
   
   protected int lastDir = 0;
   protected ProjectionListener projectionListener = new ProjectionListener()
   {
      @Override
      public void projectionChanged(ProjectionEvent e)
      {
         locToWin = e.getLocalToWindow();
         updateProjection();
      }
   };

   protected void updateProjection()
   {
      if (volRender == null || locToWin == null)
      {
         return;
      }

      int d = 0;

      if (volRender.getTg() != null)
      {
         Transform3D t3d = new Transform3D();
         volRender.getTg().getTransform(t3d);
         d = locToWin.getDir(t3d);
      } else
      {
         d = locToWin.getDir();
      }

      if (d != lastDir || (d>0 && volRender.getDir() != lastDir-1) || (d<=0 && volRender.getDir() != -lastDir-1))
      {
         if (d > 0)
         {
            volRender.setDir(d - 1, false);
         } else
         {
            volRender.setDir(-d - 1, true);
         }
         lastDir = d;
      }
   }
   
   protected Pick3DListener pick3DListener = new Pick3DListener()
   {
      @Override
      public void pick3DChanged(Pick3DEvent e)
      {
         if (inField == null)
            return;
         float[] x = e.getPoint();
         if (x == null)
            return;
         float[] y = new float[3];
         for (int i = 0; i < y.length; i++)
            y[i] = x[i] - af[3][i];
         for (int i = 0; i < 3; i++)
         {
            picked_world_coords[i] = 0;
            for (int j = 0; j < picked_world_coords.length; j++)
               picked_world_coords[j] += ia[i][j] * y[j];
         }
         picked_index = new int[]
         {
            (int) picked_world_coords[0], (int) picked_world_coords[1], (int) picked_world_coords[2]
         };
         if (picked_index[0] < 0 || picked_index[0] >= inDims[0]
                 || picked_index[1] < 0 || picked_index[1] >= inDims[1]
                 || picked_index[2] < 0 || picked_index[2] >= inDims[2])
            System.out.println("picked point outside of field area");
         else
         {
            for (int i = 0; i < 3; i++)
               picked_physical_coords[i] = phys_exts[0][i] + picked_world_coords[i] / (inDims[i] - 1) * (phys_exts[1][i] - phys_exts[0][i]);
            System.out.println("picked index = [" + picked_index[0] + "," + picked_index[1] + "," + picked_index[2] + "]");
            System.out.println("picked point = [" + picked_physical_coords[0] + "," + picked_physical_coords[1] + "," + picked_physical_coords[2] + "]");
            firePickChanged();
         }
      }
   };

   @Override
   public Pick3DListener getPick3DListener()
   {
      return pick3DListener;
   }

   @Override
   public ProjectionListener getProjectionListener()
   {
      return projectionListener;
   }

   @Override
   public ColorListener getBackgroundColorListener()
   {
      return ui.getBackgroundColorListener();
   }
   /**
    * Utility field holding list of PickListeners.
    */
   protected transient ArrayList<Pick3DListener> pick3DListenerList =
           new ArrayList<Pick3DListener>();

   /**
    * Registers PickListener to receive events.
    *
    * @param listener The listener to register.
    */
   public synchronized void addPick3DListener(Pick3DListener listener)
   {
      pick3DListenerList.add(listener);
   }

   /**
    * Removes PickListener from the list of listeners.
    *
    * @param listener The listener to remove.
    */
   public synchronized void removePick3DListener(Pick3DListener listener)
   {
      pick3DListenerList.remove(listener);
   }

   /**
    * Notifies all registered listeners about the event.
    *
    * @param object Parameter #1 of the     <CODE>ChangeEvent<CODE> constructor.
     */
   public void firePickChanged()
   {
      Pick3DEvent e = new Pick3DEvent(this, picked_world_coords, picked_index, picked_physical_coords);
      for (Pick3DListener listener : pick3DListenerList)
      {
         listener.pick3DChanged(e);
      }
   }
}
