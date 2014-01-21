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
 exception statement from your version.
 */
//</editor-fold>

package pl.edu.icm.visnow.lib.basic.mappers.Graph3D;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.engine.core.InputEgg;
import pl.edu.icm.visnow.engine.core.LinkFace;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.geometries.objects.RegularField2DGeometry;
import pl.edu.icm.visnow.lib.templates.visualization.modules.RegularOutFieldVisualizationModule;
import pl.edu.icm.visnow.lib.types.VNRegularField;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;

/**
 * @author  Krzysztof S. Nowinski (know@icm.edu.pl)
 * Warsaw University, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class Graph3D extends RegularOutFieldVisualizationModule
{

   private GUI computeUI = null;
   private RegularField inField = null;
   private Params params = null;
   public static InputEgg[] inputEggs = null;
   public static OutputEgg[] outputEggs = null;
   protected boolean ignoreUI = false;
   protected boolean fromUI = false;

   public Graph3D()
   {
      parameters = params = new Params();
      params.addChangeListener(new ChangeListener()
      {
         public void stateChanged(ChangeEvent evt)
         {
            fromUI = true;
            if(ignoreUI)
               return;
            if (params.isAdjusting() &&
                regularFieldGeometry != null)
            {
               update();
               if (regularFieldGeometry instanceof RegularField2DGeometry)
                  ((RegularField2DGeometry)regularFieldGeometry).updateCoords();
            }
            else
               startAction();
         }
      });
      SwingInstancer.swingRunAndWait(new Runnable()
      {
         public void run()
         {
            computeUI = new GUI();
         }
      });
      computeUI.setParams(params);
      outObj.setName("graph3D");
      ui.addComputeGUI(computeUI);
      setPanel(ui);
   }


   public void update()
   {
      boolean newCoords = false;
      float[] coords;
      if (inField == null || inField.getDims() == null || inField.getDims().length != 2)
         return;
      int mapComponent = params.getComponent();
      if (mapComponent <0 || mapComponent >= inField.getNData())
         return;
      if (inField.getData(mapComponent).getVeclen() != 1)
         return;
      int[] dims = inField.getDims();
      if (outField != null && outField.getCoords() != null)
         for (int i = 0; i < dims.length; i++)
            if (dims[i] != outField.getDims()[i])
            {
               newCoords = true;
               break;
            }
      if (outField == null || outField.getCoords() == null || newCoords)
      {
         outField = new RegularField(dims);
         outField.setNSpace(3);
         coords = new float[3 * dims[0] * dims[1]];
         outField.setCoords(coords);
      }
      else
         coords = outField.getCoords();
      for (int i = 0; i < inField.getNData(); i++)
         outField.setData(i, inField.getData(i));
      outField.setMask(inField.getMask());
      if (inField.getCoords() == null)
      {
         float[] h = new float[3];
         float[][] affine = inField.getAffine();
         h[0] = affine[0][1] * affine[1][2] - affine[0][2] * affine[1][1];
         h[1] = affine[0][2] * affine[1][0] - affine[0][0] * affine[1][2];
         h[2] = affine[0][0] * affine[1][1] - affine[0][1] * affine[1][0];
         float r = (float) (Math.sqrt(h[0] * h[0] + h[1] * h[1] + h[2] * h[2]));
         for (int i = 0; i < h.length; i++)
            h[i] *= params.getScale() / r;
         for (int i = 0, k = 0; i < dims[1]; i++)
         {
            float[] d = inField.getData(mapComponent).get1DSlice(i * dims[0], dims[0], 1);
            for (int j = 0; j < dims[0]; j++)
               for (int l = 0; l < 3; l++, k++)
                  coords[k] = affine[3][l] + i * affine[1][l] + j * affine[0][l] + h[l] * d[j];
         }
      }
      else
      {
         float[] c = inField.getCoords();
         float[] z = inField.getNormals();
         for (int i = 0, k = 0; i < dims[1]; i++)
         {
            float[] d = inField.getData(mapComponent).get1DSlice(i * dims[0], dims[0], 1);
            for (int j = 0; j < dims[0]; j++)
               for (int l = 0; l < 3; l++, k++)
                  coords[k] = c[k] + params.getScale() * z[k] * d[j];
         }
      }
      outField.updateExtents();
//      outField.computeNormals();
      if (params.isZFromData())
      {
         float[][] phExts = new float[2][3];
         float[][] exts = outField.getExtents();
         for (int i = 0; i < 2; i++)
            System.arraycopy(exts[i], 0, phExts[i], 0, 2);
         phExts[0][2] = inField.getData(mapComponent).getPhysMin();
         phExts[1][2] = inField.getData(mapComponent).getPhysMax();
         outField.setPhysExts(phExts);
      }
   }

   private void updateUI()
   {
      ignoreUI = true;
      computeUI.setInField(inField);
      ignoreUI = false;
   }

   @Override
   public void onActive()
   {
      if (fromUI)
      {
         update();
         if (regularFieldGeometry instanceof RegularField2DGeometry)
            ((RegularField2DGeometry)regularFieldGeometry).updateCoords();
         setOutputValue("graph3DField", new VNRegularField(outField));
         fromUI = false;
         return;
      }
      boolean newField = false;
      RegularField in;

      if (getInputFirstValue("inField") == null)
         return;
      in = ((VNRegularField) getInputFirstValue("inField")).getField();
      if (in == null)
         return;
      if (!in.isDataCompatibleWith(inField))
      {
         inField = in;
         newField = true;
         updateUI();
      }
      else if (!in.isStructureCompatibleWith(inField))
         newField = true;
      inField = in;
      update();
      setOutputValue("outField", new VNRegularField(outField));
      if (newField)
         if (!prepareOutputGeometry())
            return;
      newField = false;
      show();
      fromUI = false;
   }
   
   
   @Override
   public void onInputDetach(LinkFace link)
   {
      inField = null;
   }
}
