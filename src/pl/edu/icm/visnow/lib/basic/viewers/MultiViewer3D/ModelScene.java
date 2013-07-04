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
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import java.awt.Color;
import java.util.SortedSet;
import java.util.Vector;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.LinearFog;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import pl.edu.icm.visnow.geometries.objects.GeometryObject;
import pl.edu.icm.visnow.geometries.objects.generics.OpenBranchGroup;
import pl.edu.icm.visnow.geometries.viewer3d.lights.EditableAmbientLight;
import pl.edu.icm.visnow.geometries.viewer3d.lights.EditableDirectionalLight;

/**
 *
 * @author Bartosz Borucki (babor@icm.edu.pl) University of Warsaw, Interdisciplinary Centre for
 * Mathematical and Computational Modelling
 */
class ModelScene
{
   protected GeometryObject rootObject                          = new GeometryObject("root_object");
   protected TransformGroup objScale                            = new TransformGroup();
   protected TransformGroup objRotate                           = new TransformGroup();
   protected TransformGroup objProjectionSwitch                 = new TransformGroup();
   protected TransformGroup objTranslate                        = new TransformGroup();
   protected BoundingSphere bounds                              = new BoundingSphere(new Point3d(0., 0., 0.), 1000.0);
   protected BranchGroup objRoot                                = new BranchGroup();
   protected BranchGroup objMain                                = new BranchGroup();
   protected EditableAmbientLight ambientLight                  = null;
   protected Vector<EditableDirectionalLight> directionalLights = new Vector<EditableDirectionalLight>();
   protected Vector<TransformGroup> directionalLightTransforms  = new Vector<TransformGroup>();
   protected float[][] rootExtents                              = new float[][] {{-1,-1,-1},{1,1,1}};
   protected double externScale                                 = 1;
   protected double mouseScale                                  = 1;
   protected double scale                                       = 1;
   protected double mouseWheelSensitivity                       = 1.02;
   protected Vector3d sceneCenter                               = new Vector3d(0., 0., 0.);
   protected Transform3D sceneNorm                              = new Transform3D();
   protected Background bg                                      = new Background();
   protected LinearFog myFog                                    = new LinearFog( );
   protected OpenBranchGroup myFogGroup                         = new OpenBranchGroup();
   protected Color3f bgColor                                    = new Color3f(0.f,0.f,0.f);
   protected Matrix3f axesSwitchingMatrix                       = new Matrix3f();
   
   public ModelScene()
   {
      objTranslate.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
      objTranslate.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
      objProjectionSwitch.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
      objProjectionSwitch.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
      objRotate.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
      objRotate.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
      objScale.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
      objScale.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
      objMain.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
      objMain.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
      objMain.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
      objTranslate.addChild(objMain);
      objScale.addChild(objTranslate);
      bg.setCapability(Background.ALLOW_COLOR_WRITE);
      bg.setApplicationBounds(bounds);
      myFog.setCapability(LinearFog.ALLOW_DISTANCE_WRITE);
      myFog.setCapability(LinearFog.ALLOW_COLOR_WRITE);
      myFog.setInfluencingBounds(bounds );
      myFog.setFrontDistance(1.);
      myFog.setBackDistance(4.);
      myFogGroup.addChild(myFog);
      objScale.addChild(bg);
      objProjectionSwitch.addChild(objScale);
      objRotate.addChild(objProjectionSwitch);
      objRoot.addChild(objRotate);

      ambientLight = new EditableAmbientLight(new Color3f(.25f,.25f,.25f), bounds, "ambient light", true);
      objRoot.addChild(ambientLight.getLight());
      directionalLights.add(new EditableDirectionalLight(new Color3f( 0.4f,  0.4f,  0.4f),
                                   new Vector3f( .1f,  .08f, -.5f), bounds, "light 1", true, true));
      directionalLights.add(new EditableDirectionalLight(new Color3f( 0.2f, 0.15f,  0.1f),
                                   new Vector3f(-1.f, -0.4f, -.5f), bounds, "light 2", true, false));
      directionalLights.add(new EditableDirectionalLight(new Color3f( 0.1f,  0.1f, 0.15f),
                                   new Vector3f( 0.f  ,0.6f, -1.f), bounds, "light 3", true, false));
      directionalLights.add(new EditableDirectionalLight(new Color3f( 0.1f,  0.1f, 0.15f),
                                   new Vector3f( 0.f  ,-0.6f, -1.f),
                                   bounds, "light 3", false, false));
      for (int i = 0; i<directionalLights.size(); i++)
      {
         TransformGroup lightTransform = new TransformGroup();
         lightTransform.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
         lightTransform.setName("light"+i+"Transform");
         directionalLightTransforms.add(lightTransform);
         OpenBranchGroup lightGroup = new OpenBranchGroup();
         EditableDirectionalLight light = directionalLights.get(i);
         lightGroup.addChild(light.getLight());
         lightGroup.addChild(light.getBackLight());
         lightTransform.addChild(lightGroup);
         objMain.addChild(lightTransform);
      }

      MouseRotate mouseRotate = new MouseRotate(objRotate);
      mouseRotate.setFactor(.01, .01);
      mouseRotate.setSchedulingBounds(bounds);
      objRoot.addChild(mouseRotate);
//      MouseTranslate mouseTranslate = new MouseTranslate(objTranslate);
//      mouseTranslate.setSchedulingBounds(bounds);
//      objRoot.addChild(mouseTranslate);
      objMain.addChild(rootObject.getGeometryObj()); 
   }
   
   public void clearAllGeometry()
   {
       SortedSet<GeometryObject> children = rootObject.getChildren();
       for(GeometryObject child : children) {
           child.setCurrentViewer(null);
       }
       rootObject.clearAllGeometry();
   }
   
   public void multSwitchTransform(Transform3D sForm)
   {
      Transform3D switchTrans = new Transform3D();
      Transform3D currentTrans = new Transform3D();
      objProjectionSwitch.getTransform(currentTrans);
      switchTrans.mul(sForm, currentTrans);
      objProjectionSwitch.setTransform(switchTrans);
      currentTrans.invert(switchTrans);
      currentTrans.get(axesSwitchingMatrix);
   }
   public void setSwitchTransform(Transform3D sForm)
   {
      objProjectionSwitch.setTransform(sForm);
   }
   
   public void updateExtents()
   {
       float dim = 0;
       rootExtents = rootObject.getExtents();
       for (int i = 0; i < 3; i++)
          if (dim < rootExtents[1][i]-rootExtents[0][i]) dim = rootExtents[1][i]-rootExtents[0][i];
       if (dim==0)
          dim = 1;
       sceneCenter = new Vector3d(-.6*(rootExtents[1][0]+rootExtents[0][0])/dim,
                                  -.6*(rootExtents[1][1]+rootExtents[0][1])/dim,
                                  -.6*(rootExtents[1][2]+rootExtents[0][2])/dim);
       setScale(1.2/dim);
   }
   
   public void addGeomObject(GeometryObject obj)
   {
      rootObject.addChild(obj);
      updateExtents();             
   }
   
   protected void updateScale()
   {
      sceneNorm = new Transform3D(new Matrix3d
              (1.,0.,0.,
               0.,1.,0.,
               0.,0.,1.),
      sceneCenter, externScale*mouseScale);
      objScale.setTransform(sceneNorm);
   }
   
   public void setScale(double scale)
   {
      externScale = scale;
      updateScale();
   }

   public void rescaleFromMouseWheel(java.awt.event.MouseWheelEvent evt)
   {
      int notches = evt.getWheelRotation();
      if (notches < 0)
         mouseScale /= mouseWheelSensitivity;
      else
         mouseScale *= mouseWheelSensitivity;
      updateScale();
   }
   
   public void setTranslate(float[] d)
   {
      Transform3D tr = new Transform3D();
      tr.setTranslation(new Vector3f (d));
	   objTranslate.setTransform(tr); 
   }

   public void reset()
   {
      objRotate.setTransform(new Transform3D());
      objTranslate.setTransform(new Transform3D());
      mouseScale = 1.;
      updateScale();
   }
   
   public void setBackgroundColor(Color c)
   {
      bgColor = new Color3f(c.getColorComponents(null));
      bg.setColor(bgColor);
      myFog.setColor(bgColor);
   }

   public GeometryObject getRootObject()
   {
      return rootObject;
   }

   public BranchGroup getSceneGraph()
   {
      return objRoot;
   }
   
   public BranchGroup getObjMain()
   {
      return objMain;
   }

   public TransformGroup getSceneRotateGroup()
   {
      return objRotate;
   }
   
   public Matrix3f getAxesSwitchingTransform()
   {
      return axesSwitchingMatrix;
   }
}