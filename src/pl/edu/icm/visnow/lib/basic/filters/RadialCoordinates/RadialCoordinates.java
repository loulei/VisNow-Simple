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

package pl.edu.icm.visnow.lib.basic.filters.RadialCoordinates;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.log4j.Logger;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.engine.core.InputEgg;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.geometries.parameters.RegularField3dParams;
import pl.edu.icm.visnow.lib.templates.visualization.modules.RegularOutFieldVisualizationModule;
import pl.edu.icm.visnow.lib.types.VNRegularField;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class RadialCoordinates extends RegularOutFieldVisualizationModule
{

   private GUI computeUI = null;
   protected Params params;
   protected RegularField3dParams regularField3DmapParams = new RegularField3dParams();
   protected boolean fromUI = false;
   protected RegularField inField = null;
   protected RegularField lastInField = null;
   protected boolean fromIn = false;
   protected float[] coords = null;
   protected int[] dims = null;
   static Logger logger = Logger.getLogger(RadialCoordinates.class);

   public RadialCoordinates()
   {
      parameters = params = new Params();
      params.addChangeListener(new ChangeListener()
      {
         @Override
         public void stateChanged(ChangeEvent evt)
         {
            if (inField != null && !fromIn)
            {
               updateCoords();
               if (regularFieldGeometry != null)
                  regularFieldGeometry.updateCoords();
               if (!params.isAdjusting())
                  startAction();
            }
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
      ui.addComputeGUI(computeUI);
      setPanel(ui);
   }

   public static InputEgg[] inputEggs = null;
   public static OutputEgg[] outputEggs = null;

   private void updateCoords()
   {
      boolean sph = params.getMapType() == Params.SPHERICAL;
      int ir = params.getRCoord(), iphi = params.getPhiCoord(), ipsi = params.getPsiCoord();
      if (ir >= dims.length)    ir = Params.CONSTANT;
      if (iphi >= dims.length)  iphi = Params.CONSTANT;
      if (ipsi >= dims.length)  ipsi = Params.CONSTANT;
      float dr = 0, dz = 0;
      double dphi = 0, dpsi = 0;
      float r0 = params.getRMin();
      if (ir == Params.CONSTANT)
         dr = 0;
      else
         dr = (params.getRMax() - r0) / (dims[ir] - 1);
      double phi0 = params.getPhiMin();
      if (iphi == Params.CONSTANT)
         dphi = 0;
      else
         dphi = (params.getPhiMax() - phi0) / (dims[iphi] - 1);
      double psi0 = params.getPsiMin();
      float z0 = params.getZMin();
      if (ipsi == Params.CONSTANT)
         dpsi = dz = 0;
      else
      {
         dz =  (params.getZMax() - z0) / (dims[ipsi] - 1);
         dpsi = (params.getPsiMax() - psi0) / (dims[ipsi] - 1);
      }
      double phi = phi0;
      double psi = psi0;
      double cphi = Math.cos(phi);
      double sphi = Math.sin(phi);
      double cpsi = Math.cos(psi);
      double spsi = Math.sin(psi);
      float r = r0;
      float z = 0;
      switch (dims.length)
      {
         case 3:
            for (int i = 0, l = 0; i < dims[2]; i++)
            {
               if (ir == 2)
                  r = r0 + i * dr;
               if (iphi == 2)
               {
                  phi = phi0 + i * dphi;
                  if (sph)
                  {
                     cphi = Math.cos(phi);
                     sphi = Math.sin(phi);
                  }
               }
               if (ipsi == 2)
               {
                  if (sph)
                  {
                     psi = psi0 + i * dpsi;
                     cpsi = Math.cos(psi);
                     spsi = Math.sin(psi);
                  }
                  else
                     z = z0 + i * dz;
               }
               for (int j = 0; j < dims[1]; j++)
               {
                  if (ir == 1)
                     r = r0 + j * dr;
                  if (iphi == 1)
                  {
                     phi = phi0 + j * dphi;
                     cphi = Math.cos(phi);
                     sphi = Math.sin(phi);
                  }
                  if (ipsi == 1)
                  {
                     if (sph)
                     {
                        psi = psi0 + j * dpsi;
                        cpsi = Math.cos(psi);
                        spsi = Math.sin( psi);
                     }
                     else
                        z = z0 + j * dz;
                  }

                  for (int k = 0; k < dims[0]; k++)
                  {
                     if (ir == 0)
                        r = r0 + k * dr;
                     if (iphi == 0)
                     {
                        phi = phi0 + k * dphi;
                        cphi = Math.cos(phi);
                        sphi = Math.sin(phi);
                     }
                     if (ipsi == 0)
                     {
                        if (sph)
                        {
                           psi = psi0 + k * dpsi;
                           cpsi = Math.cos(psi);
                           spsi = Math.sin(psi);
                        }
                        else
                           z = z0 + k * dz;
                     }
                     if (sph)
                     {
                        coords[l] = (float) (r * cphi * cpsi);
                        coords[l + 1] = (float) (r * cphi * spsi);
                        coords[l + 2] = (float) (r * sphi);
                     } else
                     {
                        coords[l] = (float) (r * cphi);
                        coords[l + 1] = (float) (r * sphi);
                        coords[l + 2] = z;
                     }
                     l += 3;
                  }
               }
            }
            break;
         case 2:
            for (int j = 0, l = 0; j < dims[1]; j++)
            {
               if (ir == 1)
                  r = r0 + j * dr;
               if (iphi == 1)
               {
                  phi = phi0 + j * dphi;
                  cphi = Math.cos(phi);
                  sphi = Math.sin(phi);
               }
               if (ipsi == 1)
               {
                  if (sph)
                  {
                     psi = psi0 + j * dpsi;
                     cpsi = Math.cos(psi);
                     spsi = Math.sin( psi);
                  }
                  else
                     z = z0 + j * dz;
               }

               for (int k = 0; k < dims[0]; k++)
               {
                  if (ir == 0)
                     r = r0 + k * dr;
                  if (iphi == 0)
                  {
                     phi = phi0 + k * dphi;
                     cphi = Math.cos(phi);
                     sphi = Math.sin(phi);
                  }
                  if (ipsi == 0)
                  {
                     if (sph)
                     {
                        psi = psi0 + k * dpsi;
                        cpsi = Math.cos(psi);
                        spsi = Math.sin(psi);
                     }
                     else
                        z = z0 + k * dz;
                  }
                  if (sph)
                  {
                     coords[l] = (float) (r * cphi * cpsi);
                     coords[l + 1] = (float) (r * cphi * spsi);
                     coords[l + 2] = (float) (r * sphi);
                  } else
                  {
                     coords[l] = (float) (r * cphi);
                     coords[l + 1] = (float) (r * sphi);
                     coords[l + 2] = z;
                  }
                  l += 3;
               }
            }
            break;
         case 1:
            for (int k = 0, l = 0; k < dims[0]; k++)
            {
               if (ir == 0)
                  r = r0 + k * dr;
               if (iphi == 0)
               {
                  phi = phi0 + k * dphi;
                  cphi = Math.cos(phi);
                  sphi = Math.sin(phi);
               }
               if (ipsi == 0)
               {
                  if (sph)
                  {
                     psi = psi0 + k * dpsi;
                     cpsi = Math.cos(psi);
                     spsi = Math.sin(psi);
                  }
                  else
                     z = z0 + k * dz;
               }
               if (sph)
               {
                  coords[l] = (float) (r * cphi * cpsi);
                  coords[l + 1] = (float) (r * cphi * spsi);
                  coords[l + 2] = (float) (r * sphi);
               } else
               {
                  coords[l] = (float) (r * cphi);
                  coords[l + 1] = (float) (r * sphi);
                  coords[l + 2] = z;
               }
               l += 3;
            }
            break;
      }
      outField.setNSpace(3);
      outField.setCoords(coords);

   }

   public void updateField()
   {
      dims = inField.getDims();
      outField = new RegularField(dims);
      int nNodes = 1;
      for (int i = 0; i < dims.length; i++)
         nNodes *= dims[i];
      coords = new float[3 * nNodes];
      for (int i = 0; i < inField.getNData(); i++)
         outField.addData(inField.getData(i));
   }


   public void onActive()
   {
      if (getInputFirstValue("inField") != null &&
          ((VNRegularField) getInputFirstValue("inField")).getField() != null &&
          ((VNRegularField) getInputFirstValue("inField")).getField() != inField)
      {
         params.setActive(false);
         fromIn = true;
         logger.debug("setting infield");
         inField = ((VNRegularField) getInputFirstValue("inField")).getField();
         computeUI.setInField(inField);
         params.setActive(true);
         updateField();
         updateCoords();
         if (!prepareOutputGeometry())
            return;
         show();
         setOutputValue("outField", new VNRegularField(outField));
         fromIn = false;
         return;
      }
      if (!params.isAdjusting())
         setOutputValue("outField", new VNRegularField(outField));
   }
}
