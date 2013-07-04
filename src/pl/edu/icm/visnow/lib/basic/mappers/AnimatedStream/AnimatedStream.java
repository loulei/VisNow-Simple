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

package pl.edu.icm.visnow.lib.basic.mappers.AnimatedStream;


import java.util.Vector;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pl.edu.icm.visnow.datamaps.ColorMapManager;
import pl.edu.icm.visnow.datasets.cells.Cell;
import pl.edu.icm.visnow.datasets.CellArray;
import pl.edu.icm.visnow.datasets.CellSet;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.datasets.IrregularField;
import pl.edu.icm.visnow.engine.core.InputEgg;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.geometries.objects.IrregularFieldGeometry;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.render.FrameRenderedEvent;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.render.FrameRenderedListener;
import pl.edu.icm.visnow.lib.templates.visualization.modules.RenderWindowListeningModule;
import pl.edu.icm.visnow.lib.types.VNField;
import pl.edu.icm.visnow.lib.types.VNGeometryObject;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;
import pl.edu.icm.visnow.geometries.events.ColorListener;
import pl.edu.icm.visnow.lib.templates.visualization.modules.IrregularOutField1DVisualizationModule;

/**
 * @author  Krzysztof S. Nowinski (know@icm.edu.pl)
 * Warsaw University, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class AnimatedStream extends IrregularOutField1DVisualizationModule implements RenderWindowListeningModule
{

   protected Field inField;
   protected GUI computeGUI    = null;
   protected Params params;
   protected boolean fromGUI   = false;
   protected boolean ignoreUI  = false;
   protected int segmentLength = 2;
   protected int segmentStep   = 2;
   protected int frame         = 0;
   protected int nSegments     = 1;
   protected int segSkip       = 1;
   protected int dir           = 0;
   protected int nNodes;
   protected int outNNodes;
   protected Vector<float[]> inCoords;
   protected float[] outCoords;
   private boolean animating    = false;
   private boolean renderDone   = true;

   public AnimatedStream()
   {
      parameters = params = new Params();
      outObj.setName("animated streamlines");
      params.addChangeListener(new ChangeListener()
      {
         public void stateChanged(ChangeEvent evt)
         {
            if(ignoreUI)
               return;
            fromGUI = true;
            if (params.getAnimate() != Params.STOP)
            {
                dir = params.getAnimate();
                new Thread(new Animation()).start();
            }
            else
               updateOutField();
         }
      });
      SwingInstancer.swingRun(new Runnable()
      {
         public void run()
         {
            computeGUI    = new GUI();
         }
      });
      computeGUI.setParams(params);
      ui.addComputeGUI(computeGUI);
      setPanel(ui);
   }

   protected FrameRenderedListener frameRenderedListener = new FrameRenderedListener()
   {
      public void frameRendered(FrameRenderedEvent e)
      {
         renderDone = true;
      }
   };

   public ColorListener getBackgroundColorListener()
   {
      return null;
   }


   private class Animation implements Runnable 
   {
      public Animation()
      {
          
      }

      public synchronized void run()
      {
         if (animating)
           return;
         animating = true;
         while (params.getAnimate() != Params.STOP)
         {
            renderDone = false;
            frame = (frame + dir)%segSkip;
            updateCoords();
            Thread.yield();
            try
            {
               while (!renderDone)
               {
                  wait(10);
               }
               wait(params.getDelay()+1);
            }
            catch (InterruptedException c)
            {
               System.out.println("interrupt");
            }
         }
         animating = false;
      }
   }

   public static InputEgg[] inputEggs = null;
   public static OutputEgg[] outputEggs = null;


   @Override
   public void onInitFinished()
   {
      outObj.setCreator(this);
      irregularFieldGeometry = new IrregularFieldGeometry();
      outObj.getGeometryObj().setUserData(getName());
      setOutputValue("outObj", new VNGeometryObject(outObj));
   }
   
   private void updateCoordValues()
   {
      outCoords = outField.getCoords();
      for (int i = 0, l = 0, m = frame - segSkip; i < nSegments; i++, m += segSkip - segmentLength)
         for (int j = 0; j <= segmentLength; j++, m ++)
         {
            int r = Math.min(Math.max(m, 0), inField.getNFrames() - 1);
            float[] c = inCoords.get(r);
            System.arraycopy(c, 0, outCoords, l, c.length);
            l += c.length;
         }
   }

   private void updateCoords()
   {
      updateCoordValues();
      outField.updateExtents();
      irregularFieldGeometry.updateCoords();
   }

   
   private void updateOutField()
   {
      nSegments = params.getNSegments();
      segmentLength = params.getSegmentLength();
      segSkip = (inField.getNFrames()-segmentLength+1)/nSegments;
      nSegments += 2;
      nNodes = inField.getNNodes();
      inCoords = inField.getAllCoords().getAllData();
      outNNodes = nNodes*(segmentLength+1)*nSegments;
      frame = 0;
      
      outField = new IrregularField();
      outField.setNNodes(outNNodes);
      outField.setNSpace(3);
      outCoords = new float[outField.getNNodes() * outField.getNSpace()];
      outField.setCoords(outCoords);
      updateCoordValues();
      int[] lines = new int[2*nNodes*segmentLength*nSegments];
      boolean[] edgeOrientations = new boolean[nNodes*segmentLength*nSegments];
      for (int i = 0, l = 0, m = 0; i < nSegments; i++, m += nNodes)
         for (int j = 0; j < segmentLength; j++)
            for (int k = 0; k < nNodes; k++, l++, m++)
            {
               lines[2*l]   = m;
               lines[2*l+1] = m + nNodes;
               edgeOrientations[l] = true;
            }
      CellArray streamLines = new CellArray(Cell.SEGMENT, lines, edgeOrientations, null);
      CellSet cellSet = new CellSet(inField.getName());
      cellSet.setBoundaryCellArray(streamLines);
      cellSet.setCellArray(streamLines);
      outField.addCellSet(cellSet);
      
      int[] data = new int[outField.getNNodes()];
      for (int i = 0, l = 0; i < nSegments; i++)
         for (int j = 0; j < segmentLength+1; j++)
            for (int k = 0; k < nNodes; k++, l++)
               data[l] = j;
      DataArray d = DataArray.create(data, 1, "dummy");
      outField.addData(d);
      
      outField.setExtents(inField.getExtents());
      prepareOutputGeometry();      
      ui.getPresentation1DPanel().getColorPanel().setData(outField.getDataSchema());
//      ui.getPresentation1DPanel().getColorPanel().setColorMap(ColorMapManager.COLORMAP1D_GRAY);
      outObj.getDataMappingParams().getColorMap0Params().setMapType(ColorMapManager.COLORMAP1D_GRAY);
      show();   
   }

   @Override
   public void onActive()
   {
      if (!fromGUI)
      {
         if (getInputFirstValue("inField") == null)
            return;
         ignoreUI = true;
         VNField input = ((VNField) getInputFirstValue("inField"));
         if (inField != input.getField())
         {
            Field in = input.getField();
            if (in == null || in.getAllCoords() == null || in.getAllCoords().getNSteps() < 2)
            {
               computeGUI.setInText("<html>Bad input field:<p>must have time dependent coordinates</html>");
               computeGUI.setNFrames(0);
               return;
            }
            computeGUI.setInText("");
            inField = in;
            computeGUI.setNFrames(inField.getNFrames());
         }
         ignoreUI = false;
      }
      if (inField == null)
         return;
      updateOutField();
      fromGUI = false;
   }

   public FrameRenderedListener getFrameRenderedListener()
   {
      return frameRenderedListener;
   }
}
