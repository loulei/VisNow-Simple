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

package pl.edu.icm.visnow.lib.basic.viewers.MultiViewer3D;

import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import javax.media.j3d.*;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

/**
 *
 * @author Bartosz Borucki (babor@icm.edu.pl) University of Warsaw, Interdisciplinary Centre for
 * Mathematical and Computational Modelling
 */
class UniverseBuilder
{

   protected Locale locale = null;
   protected CameraView rightCamera              = new CameraView();
   protected CameraView frontCamera              = new CameraView();
   protected CameraView topCamera                = new CameraView();
   protected CameraView mainCamera               = new CameraView();
   protected final ModelScene scene              = new ModelScene();
   protected BoundingSphere bounds               = new BoundingSphere(new Point3d(0., 0., 0.), 100.0);
   protected MouseRotate myMouseRotate           = new MouseRotate();
   protected double mouseRotateSensitivity       = .002;
   protected CameraView[] cameras                = new CameraView[]{rightCamera, frontCamera, topCamera, mainCamera};
   protected Vector3f rightPos = new Vector3f(-4.0f, 0.0f, 0.0f);
   protected Vector3f frontPos = new Vector3f(0.0f, -4.0f, 0.0f);
   protected Vector3f   topPos = new Vector3f(0.0f,  0.0f, 4.0f);
   protected Transform3D xformSide   = new Transform3D();
   protected Transform3D xformFront  = new Transform3D();
   protected Transform3D xformTop    = new Transform3D();


   public UniverseBuilder()
   {
      create();
   }

   private void create()
   {
      xformSide.rotX(-Math.PI / 2.0);
      xformSide.rotY(-Math.PI / 2.0);
      xformFront.rotX(Math.PI / 2.0);
      BranchGroup sceneBG = scene.getSceneGraph();
      Transform3D xform2;
      final VirtualUniverse universe = new VirtualUniverse();
      locale = new Locale(universe);
      
      rightCamera.setName("side");
      rightCamera.setOrthoView(true);
      Transform3D xform = new Transform3D();
      xform.set(rightPos);
      xform.mul(xformSide);
      rightCamera.getViewPlatformTransformGroup().setTransform(xform);

      frontCamera.setName("front/back");
      frontCamera.setOrthoView(true);
      frontCamera.setStdAxes(new int[][] {{0,1}, {2,1}, {1, -1}});
      xform = new Transform3D();
      xform.set(frontPos);
      xform.mul(xformFront);
      frontCamera.getViewPlatformTransformGroup().setTransform(xform);
      
      topCamera.setName("top/bottom");
      topCamera.setOrthoView(true);
      topCamera.setStdAxes(new int[][] {{0,1}, {1,1}, {2, 1}});
      xform = new Transform3D();
      xform.set(topPos);
      topCamera.getViewPlatformTransformGroup().setTransform(xform);
      
      mainCamera.setName("perspective view");
      mainCamera.setOrthoView(false);
      xform = new Transform3D();
      xform.rotX(Math.PI / 8.0);
      xform.rotY(Math.PI / 8.0);
      xform.setScale(.5);
      xform2 = new Transform3D();
      Vector3f vec = new Vector3f(0.0f, 0.0f, 4.0f);
      xform2.set(vec);
      xform.mul(xform2);
      mainCamera.getViewPlatformTransformGroup().setTransform(xform);
      
      for (int i = 0; i < cameras.length; i++)
      {
         cameras[i].setScene(scene);
         if (cameras[i].isOrthoView())
         {
            cameras[i].getView().setProjectionPolicy(View.PARALLEL_PROJECTION);
            scene.getSceneRotateGroup().addChild(cameras[i].getRootBG());
         }
         else
         {
            cameras[i].getView().setProjectionPolicy(View.PERSPECTIVE_PROJECTION);
            locale.addBranchGraph(cameras[i].getRootBG());
         }
      }

      sceneBG.compile();
      locale.addBranchGraph(sceneBG);
   }
   
   public CameraView getRightCamera()
   {
      return this.rightCamera;
   }

   public CameraView getMainCamera()
   {
      return this.mainCamera;
   }

   public CameraView getFrontCamera()
   {
      return this.frontCamera;
   }

   public CameraView getTopCamera()
   {
      return this.topCamera;
   }

   public ModelScene getScene()
   {
      return scene;
   }
   
   public void translateT(float d)
   {
      Transform3D xform = new Transform3D();
      topPos.x = d;
      xform.set(topPos);
      xform.mul(xformTop);
      topCamera.getViewPlatformTransformGroup().setTransform(xform);
      xform = new Transform3D();
      frontPos.x = d;
      xform.set(frontPos);
      xform.mul(xformFront);
      frontCamera.getViewPlatformTransformGroup().setTransform(xform);
   }
   
   public void translateU(float d)
   {
      Transform3D xform = new Transform3D();
      topPos.y = d;
      xform.set(topPos);
      xform.mul(xformTop);
      topCamera.getViewPlatformTransformGroup().setTransform(xform);
      xform = new Transform3D();
      rightPos.y = d;
      xform.set(rightPos);
      xform.mul(xformSide);
      rightCamera.getViewPlatformTransformGroup().setTransform(xform);
   }
    
   public void translateV(float d)
   {
      Transform3D xform = new Transform3D();
      rightPos.z = d;
      xform.set(rightPos);
      xform.mul(xformSide);
      rightCamera.getViewPlatformTransformGroup().setTransform(xform);
      xform = new Transform3D();
      frontPos.z = d;
      xform.set(frontPos);
      xform.mul(xformFront);
      frontCamera.getViewPlatformTransformGroup().setTransform(xform);
   }
  
}
