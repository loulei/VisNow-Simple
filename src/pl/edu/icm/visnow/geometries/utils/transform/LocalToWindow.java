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

package pl.edu.icm.visnow.geometries.utils.transform;
/*
 * LocalToWindow.java
 *
 * Created on May 15, 2004, 10:51 PM
 */
/*
 *      @(#)LocalToWindow.java 1.2 99/02/08 15:39:12
 *
 * Copyright (c) 1999 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */

/**
 * Utility class for doing local->window transformations for case where
 * Canvas3D is a simple display such as a monitor. This won't work for the
 * more complex cases (i.e. a multiple canvases, head tracking, etc).
 *
 * Usage:
 *    // after the canvas and node are created
 *    LocalToWindow locToWindow = LocalToWindow(node, canvas);
 *    ...
 *    // when we need to transform (canvas location and node transforms may have
 *    // changed)
 *    locToWindow.update(); // make sure transforms are up to date
 *
 *    Point3d[] localPts = <some local coords to transform >
 *    Point[] windowPts = <the area to put the tranformed pts >
 *    for (int i = 0; i < localPts.length; i++) {
 *       locToWindow.transformPt(localPts[i], windowPts[i]);
 *    }
 */
import java.awt.Dimension;
import java.awt.Point;
import javax.media.j3d.*;
import javax.vecmath.*;

public class LocalToWindow
{
   Canvas3D canvas = null;
   Node node       = null;

   // inquired/derived data
   Transform3D localToVworld       = new Transform3D();
   Transform3D vworldToImagePlate  = new Transform3D();
   Transform3D localToImagePlate   = new Transform3D();
   Transform3D imagePlateToLocal   = new Transform3D();
   Point3d eyePos                  = new Point3d();
   int 	    projType;
   Point     canvasScr;
   Dimension screenSize;
   double 	 metersPerPixelX;
   double 	 metersPerPixelY;

   // Temporaries
   Point3d	localPt      = new Point3d();
   Point3d	imagePlatePt = new Point3d();
   Vector3d	projVec      = new Vector3d();
   Point2d	screenPt     = new Point2d();
   Point2d	tempPt2d     = new Point2d();

   /**
    * Creates a LocalToWindow object with no associated node or canvas.
    * The node and canvas must be set before transforming points
    */

   /** Creates a new instance of LocalToWindow */
   public LocalToWindow()
   {
   }
   public LocalToWindow(Node node, Canvas3D canvas)
   {
      this.canvas = canvas;
      this.node = node;
      update();
   }

   /**
    * Either create LocalToWindow() just before transforming points or call
    * this method to ensure that the transforms are up to date.  Note: if
    * you are transforming several points, you only need to call this method
    * once.
    */
   public void update()
   {
      if ((this.canvas != null) && (this.node != null))
      {
         try
         {
            node.getLocalToVworld(localToVworld);
         }
         catch (RestrictedAccessException e)
         {
            return;
         }
         try
         {
            canvas.getVworldToImagePlate(vworldToImagePlate);
         }
         catch (RestrictedAccessException e)
         {
            System.out.println("canvas secret yet");
            return;
         }

         localToImagePlate.mul(vworldToImagePlate, localToVworld);
         imagePlateToLocal.invert(localToImagePlate);
         canvas.getCenterEyeInImagePlate(eyePos);
         projType = canvas.getView().getProjectionPolicy();
         canvasScr = canvas.getLocationOnScreen();
         screenSize = canvas.getScreen3D().getSize();
         double physicalScreenWidth =
         canvas.getScreen3D().getPhysicalScreenWidth();
         double physicalScreenHeight =
         canvas.getScreen3D().getPhysicalScreenHeight();
         metersPerPixelX = physicalScreenWidth / (double) screenSize.width;
         metersPerPixelY = physicalScreenHeight / (double) screenSize.height;
      }
   }

   /**
    * Set the node and canvas and call update()
    */
   public void update(Node node, Canvas3D canvas)
   {
      this.canvas = canvas;
      this.node = node;
      update();
   }

   public void update(Node node)
   {
      this.node = node;
      update();
   }

   /**
    * Transform the point from local coords to window coords
    */
   public float transformPt(Point3d localPt, Point2d windowPt)
   {
      localToImagePlate.transform(localPt, imagePlatePt);
      double zScale = 1.0; // default, used for PARALELL_PROJECTION
      if (projType == View.PERSPECTIVE_PROJECTION)
      {
         projVec.sub(imagePlatePt, eyePos);
         zScale = eyePos.z / (-projVec.z);
         screenPt.x = eyePos.x + projVec.x * zScale;
         screenPt.y = eyePos.y + projVec.y * zScale;
      } else
      {
         screenPt.x = imagePlatePt.x;
         screenPt.y = imagePlatePt.y;
      }
      windowPt.x = (screenPt.x / metersPerPixelX) - canvasScr.x;
      windowPt.y = screenSize.height - 1 -
                   (screenPt.y / metersPerPixelY) - canvasScr.y;
      return 1.f+(float)projVec.z;
   }

   /**
    * Transform the point from local coords to window coords
    */
   public float transformPt(Point3d localPt, Point windowPt)
   {
      float depth = transformPt(localPt, tempPt2d);
      windowPt.x = (int)Math.round(tempPt2d.x);
      windowPt.y = (int)Math.round(tempPt2d.y);
      return depth;
   }
   
   public float transformPt(Point3d localPt, int[] wCoords)
   {
      float depth = transformPt(localPt, tempPt2d);
      wCoords[0] = (int)Math.round(tempPt2d.x);
      wCoords[1] = (int)Math.round(tempPt2d.y);
      return depth;
   }

   public float transformPt(double[] coords, int[] wCoords)
   {
      if (coords==null || coords.length<3)
         return 0;
      float depth = transformPt(new Point3d(coords), tempPt2d);
      wCoords[0] = (int)Math.round(tempPt2d.x);
      wCoords[1] = (int)Math.round(tempPt2d.y);
      return depth;
   }
   
   public float transformPt(double[] coords, double[] wCoords)
   {
      if (coords==null || coords.length<3)
         return 0;
      float depth = transformPt(new Point3d(coords), tempPt2d);
      wCoords[0] = tempPt2d.x;
      wCoords[1] = tempPt2d.y;
      return depth;
   }

    public float transformPt(double[] coords, float[] wCoords)
   {
      if (coords==null || coords.length<3)
         return 0;
      float depth = transformPt(new Point3d(coords), tempPt2d);
      wCoords[0] = (float)tempPt2d.x;
      wCoords[1] = (float)tempPt2d.y;
      return depth;
   }

  public float transformPt(float[] coords, int[] wCoords)
   {
      if (coords==null || coords.length<3)
         return 0;
      return transformPt(new double[]{coords[0],coords[1],coords[2]}, wCoords);
   }

   public float transformPt(float[] coords, float[] wCoords)
   {
      if (coords==null || coords.length<3)
         return 0;
      return transformPt(new double[]{coords[0],coords[1],coords[2]}, wCoords);
   }

  public void reverseTransformPt(Point2d windowPt, float z, Point3d localPt)
   {
      screenPt.x = (windowPt.x + canvasScr.x) * metersPerPixelX;
      screenPt.y = (screenSize.height - 1 - windowPt.y - canvasScr.y) * metersPerPixelY;
      double zScale = 1.0;
      if (projType == View.PERSPECTIVE_PROJECTION)
      {
         projVec.sub(imagePlatePt, eyePos);
         projVec.z = z;
         zScale = -eyePos.z / z;
         projVec.x = (screenPt.x - eyePos.x) / zScale;
         projVec.y = (screenPt.y - eyePos.y) / zScale;
         imagePlatePt.add(eyePos, projVec);
      } else
      {
         imagePlatePt.x = screenPt.x;
         imagePlatePt.y = screenPt.y;
         imagePlatePt.z = z;
      }
      imagePlateToLocal.transform(imagePlatePt, localPt);
   }

   public void  reverseTransformPt(int ix, int iy, float z, float[] coords)
   {
      if (coords == null || coords.length != 3)
         return;
      reverseTransformPt(new Point2d((double)ix, (double)iy), z, localPt);
      coords[0] = (float)localPt.x;
      coords[1] = (float)localPt.y;
      coords[2] = (float)localPt.z;
   }
   
   public float[] reverseTransformPt(int ix, int iy, float z)
   {
      float[] coords = new float[3];
      reverseTransformPt(ix, iy, z, coords);
      return coords;
   }

   public int getDir()
   {
      double[] v =new double[16];
      localToImagePlate.get(v);
      int d=3;
      float c=0;
      for (int i=0;i<3;i++)
         if (Math.abs(v[8+i])>c)
         {
            c = (float)Math.abs(v[8+i]);
            if   (v[8+i]>0) d= i+1;
            else            d=-i-1;
         }
      return d;
   }

   public int getDir(Transform3D externalTransform)
   {
       if(externalTransform == null)
           return getDir();
       
      double[] v =new double[16];
      Transform3D external2ImagePlate = new Transform3D();
      external2ImagePlate.set(localToImagePlate);
      external2ImagePlate.mul(externalTransform);
      external2ImagePlate.get(v);
      int d=3;
      float c=0;
      for (int i=0;i<3;i++)
         if (Math.abs(v[8+i])>c)
         {
            c = (float)Math.abs(v[8+i]);
            if   (v[8+i]>0) d= i+1;
            else            d=-i-1;
         }
      return d;
   }

}
