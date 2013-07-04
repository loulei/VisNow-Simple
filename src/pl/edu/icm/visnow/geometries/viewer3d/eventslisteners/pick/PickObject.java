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

package pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.pick;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.IndexedLineStripArray;
import pl.edu.icm.visnow.geometries.objects.GeometryObject;
import pl.edu.icm.visnow.geometries.objects.GeometryParent;
import pl.edu.icm.visnow.geometries.objects.generics.OpenBranchGroup;
import pl.edu.icm.visnow.geometries.objects.generics.OpenShape3D;
import pl.edu.icm.visnow.geometries.geometryTemplates.Glyph;
import pl.edu.icm.visnow.geometries.geometryTemplates.Templates;
import pl.edu.icm.visnow.lib.utils.numeric.NumericalMethods;

/**
 *
 * @author Krzysztof S. Nowinski
 * University of Warsaw, ICM
 */
public class PickObject
{

   private boolean pickDone = false;

   public static class PickRayEvent extends EventObject
   {

      private float[] c = null;

      /** Creates a new instance of ProjectionModificationEvent */
      public PickRayEvent(Object source, float[] c)
      {
         super(source);
         this.c = c;
      }

      @Override
      public String toString()
      {
         return String.format("[%5.3f %5.3f %5.3f] -  [%5.3f %5.3f %5.3f]", c[0], c[1], c[2], c[3], c[4], c[5]);
      }

      public float[] getRayPoints()
      {
         return c;
      }
   }

   public static interface PickRayListener extends EventListener
   {

      void pickRayChanged(PickRayEvent e);
   }
   protected GeometryParent parent = null;
   protected String name = "3D pick";
   protected GeometryObject outObj = new GeometryObject(name);
   protected Glyph gt = new Templates.DiamondTemplate(0);
   protected float[] d = null;
   protected float scale = .1f;
   protected IndexedLineStripArray glyph = null;
   protected int nvertl, nvertp, nstripl, nstripp, nindl, nindp, ncol;
   protected int[] stripsl, stripsp, pIndexl, pIndexp, cIndexl, cIndexp;
   protected float[] vertsl, vertsp;
   protected float[] colors =
   {
      .3f, .6f, 1.f
   };
   protected float[] resultColors =
   {
      .1f, 1.f, .0f
   };
   
   protected float[] x = new float[3];

   public PickObject()
   {
      ncol = 1;
      nvertl = 2 * gt.getNverts() + 2;
      nstripl = 2 * gt.getNstrips() + 1;
      nindl = 2 * gt.getNinds() + 2;
      stripsl = new int[nstripl];
      for (int i = 0; i < gt.getNstrips(); i++)
         stripsl[i] = stripsl[i + gt.getNstrips()] = gt.getStrips()[i];
      stripsl[2 * gt.getNstrips()] = 2;
      pIndexl = new int[nindl];
      cIndexl = new int[nindl];
      for (int i = 0; i < gt.getNinds(); i++)
      {
         pIndexl[i] = gt.getPntsIndex()[i];
         pIndexl[i + gt.getNinds()] = gt.getPntsIndex()[i] + gt.getNverts();
      }
      pIndexl[2 * gt.getNinds()] = 2 * gt.getNverts();
      pIndexl[2 * gt.getNinds() + 1] = 2 * gt.getNverts() + 1;
      for (int i = 0; i < nindl; i++)
         cIndexl[i] = 0;
      vertsl = new float[3 * nvertl];

      nvertp = gt.getNverts();
      nstripp = gt.getNstrips();
      nindp = gt.getNinds();
      stripsp = new int[nstripp];
      System.arraycopy(gt.getStrips(), 0, stripsp, 0, gt.getNstrips());
      pIndexp = new int[nindp];
      cIndexp = new int[nindp];
      System.arraycopy(gt.getPntsIndex(), 0, pIndexp, 0, gt.getNinds());
      for (int i = 0; i < nindp; i++)
         cIndexp[i] = 0;
      vertsp = new float[3 * nvertp];
   }
   
   protected PickRayListener pickRayListener = new PickRayListener()
   {

      public void pickRayChanged(PickRayEvent e)
      {
         float[] c = e.getRayPoints();
         if (d == null)
         {
            outObj.clearAllGeometry();

            glyph = new IndexedLineStripArray(nvertl,
                    GeometryArray.COORDINATES |
                    GeometryArray.COLOR_3,
                    nindl, stripsl);
            scale = .01f * (float) Math.sqrt((c[3] - c[0]) * (c[3] - c[0]) + (c[4] - c[1]) * (c[4] - c[1]) + (c[5] - c[2]) * (c[5] - c[2]));
            for (int j = 0; j < 3; j++)
            {
               for (int i = 0; i < gt.getNverts(); i++)
               {
                  vertsl[3 * i + j] = c[j] + scale * gt.getVerts()[3 * i + j];
                  vertsl[3 * (i + gt.getNverts()) + j] = c[3 + j] + scale * gt.getVerts()[3 * i + j];
               }
               vertsl[6 * gt.getNverts() + j] = c[j];
               vertsl[6 * gt.getNverts() + 3 + j] = c[3 + j];
            }
            glyph.setColors(0, colors);
            glyph.setCoordinates(0, vertsl);
            glyph.setCoordinateIndices(0, pIndexl);
            glyph.setColorIndices(0, cIndexl);
            OpenShape3D pickLine = new OpenShape3D();
            pickLine.addGeometry(glyph);
            OpenBranchGroup pLine = new OpenBranchGroup();
            pLine.addChild(pickLine);
            outObj.addNode(pLine);
            d = c;
            pickDone = false;
         } else
         {
            float[][] p = new float[2][3];
            float[][] v = new float[2][3];
            float[][] a = new float[2][2];
            float[] b = new float[2];
            a[0][0] = a[0][1] = b[0] = a[1][0] = a[1][1] = b[1] = 0;
            for (int i = 0; i < 3; i++)
            {
               p[0][i] = c[i];
               v[0][i] = c[i + 3] - c[i];
               p[1][i] = d[i];
               v[1][i] = d[i + 3] - d[i];
               a[0][0] += v[0][i] * v[0][i];
               a[0][1] -= v[0][i] * v[1][i];
               b[0] += v[0][i] * (p[1][i] - p[0][i]);
               a[1][0] -= v[0][i] * v[1][i];
               a[1][1] += v[1][i] * v[1][i];
               b[1] += v[1][i] * (p[0][i] - p[1][i]);
            }
            float[] t = NumericalMethods.lsolve(a, b);
            for (int i = 0; i < 3; i++)
               x[i] = (p[0][i] + t[0] * v[0][i] + p[1][i] + t[1] * v[1][i]) / 2; 
            outObj.clearAllGeometry();
            glyph = new IndexedLineStripArray(nvertp,
                    GeometryArray.COORDINATES |
                    GeometryArray.COLOR_3,
                    nindp, stripsp);
            scale = .01f * (float) Math.sqrt((c[3] - c[0]) * (c[3] - c[0]) + (c[4] - c[1]) * (c[4] - c[1]) + (c[5] - c[2]) * (c[5] - c[2]));
            for (int j = 0; j < 3; j++)
               for (int i = 0; i < gt.getNverts(); i++)
                  vertsp[3 * i + j] = x[j] + scale * gt.getVerts()[3 * i + j];
            glyph.setColors(0, resultColors);
            glyph.setCoordinates(0, vertsp);
            glyph.setCoordinateIndices(0, pIndexp);
            glyph.setColorIndices(0, cIndexp);
            OpenShape3D pickLine = new OpenShape3D();
            pickLine.addGeometry(glyph);
            OpenBranchGroup pLine = new OpenBranchGroup();
            pLine.addChild(pickLine);
            outObj.addNode(pLine);
            d = null;
            firePickChanged();
            pickDone = true;
         }
      }
   };

   public PickRayListener getPickRayListener()
   {
      return pickRayListener;
   }

   public boolean isPickDone()
   {
      return pickDone;
   }

   public GeometryObject getOutObject()
   {
      return outObj;
   }

   public float[] getPickedCoords()
   {
      return x;
   }

   public void setX(float[] x)
   {
      this.x = x;
      firePickChanged();
   }
   
   /**
    * Utility field holding list of PickListeners.
    */
   protected transient ArrayList<Pick3DListener> pick3DListenerList =
           new ArrayList<Pick3DListener>();

   /**
    * Registers PickListener to receive events.
    * @param listener The listener to register.
    */
   public synchronized void addPick3DListener(Pick3DListener listener)
   {
       if(listener == null)
           return;
       
      pick3DListenerList.add(listener);
   }

   /**
    * Removes PickListener from the list of listeners.
    * @param listener The listener to remove.
    */
   public synchronized void removePick3DListener(Pick3DListener listener)
   {
      pick3DListenerList.remove(listener);
   }

   /**
    * Notifies all registered listeners about the event.
    *
    * @param object Parameter #1 of the <CODE>ChangeEvent<CODE> constructor.
    */
   public void firePickChanged()
   {
      Pick3DEvent e = new Pick3DEvent(this, x);
      for (Pick3DListener listener : pick3DListenerList)
         listener.pick3DChanged(e);
   }
   
}
