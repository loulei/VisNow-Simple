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

/**
 *
 * @author Bartosz Borucki (babor@icm.edu.pl) University of Warsaw,
 * Interdisciplinary Centre for Mathematical and Computational Modelling
 */
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.media.j3d.*;
import pl.edu.icm.visnow.geometries.objects.GeometryObject;
import pl.edu.icm.visnow.geometries.utils.transform.LocalToWindow;

class CameraView
{
   protected String name = "camera";
   protected static final PhysicalBody physBody = new PhysicalBody();
   protected static final PhysicalEnvironment physEnv = new PhysicalEnvironment();
   protected BranchGroup rootBG = null;
   protected TransformGroup vpTG = null;
   protected ViewPlatform viewPlatform = null;
   protected ModelScene scene = null;
   protected View view = null;
   protected Canvas3D canvas = null;
   protected J3DGraphics2D vGraphics = null;
   protected LocalToWindow locToWin = null;
   protected boolean orthoView = false;
   protected boolean axesUpdated = false;
   protected GeometryObject rootObject = null;
   protected BranchGroup objMain = null;
   protected int lastX = 0, lastY = 0;
   protected float[][] reper = {{0, 0, 0}, {1, 0, 0}, {0, 1, 0}, {0, 0, 1}};
   protected float[][] reperIm = new float[4][2];
   protected int[][] reperScr = new int[3][2];
   protected int[][] lastAx = {{0,3,-2}, {-3,0,1}, {2, -1, 0}};
   protected int[][] axes = {{0,1}, {1,1}, {2, 1}};
   protected int[][] stdAxes = {{0,1}, {1,1}, {2, 1}};

   public CameraView()
   {

      GraphicsConfigTemplate3D gconfigTempl = new GraphicsConfigTemplate3D();
      GraphicsConfiguration gconfig =
              GraphicsEnvironment.getLocalGraphicsEnvironment().
              getDefaultScreenDevice().
              getBestConfiguration(gconfigTempl);

      canvas = new Canvas3D(gconfig)
      {
         @Override
         public void postRender()
         {
            vGraphics = super.getGraphics2D();
            if (objMain != null)
            {
               locToWin = new LocalToWindow(objMain, canvas);
               draw2D(vGraphics, locToWin);
            }
            vGraphics.flush(false);
         }
      };
      canvas.addKeyListener(new java.awt.event.KeyAdapter()
      {
         @Override
         public void keyReleased(KeyEvent evt)
         {
//            System.out.println(name);
            if (scene == null || !orthoView)
               return;
            Transform3D sForm = new Transform3D();
            switch (evt.getKeyCode())
            {
               case KeyEvent.VK_UP:
               case KeyEvent.VK_DOWN:
                  axesUpdated = false;
                  switch (axes[0][0])
                  {
                     case 0:
                        if ((evt.getKeyCode() == KeyEvent.VK_UP) ==  (axes[0][1] == 1))
                           sForm.rotX(Math.PI / 2.0);
                        else
                           sForm.rotX(-Math.PI / 2.0);
                        break;
                     case 1:
                        if ((evt.getKeyCode() == KeyEvent.VK_UP) ==  (axes[0][1] == 1))
                           sForm.rotY(Math.PI / 2.0);
                        else
                           sForm.rotY(-Math.PI / 2.0);
                        break;
                     case 2:
                        if ((evt.getKeyCode() == KeyEvent.VK_UP) ==  (axes[0][1] == 1))
                           sForm.rotZ(Math.PI / 2.0);
                        else
                           sForm.rotZ(-Math.PI / 2.0);
                        break;
                  }
                  break;
               case KeyEvent.VK_LEFT:
               case KeyEvent.VK_RIGHT:
                  axesUpdated = false;
                  switch (axes[1][0])
                  {
                     case 0:
                        if ((evt.getKeyCode() == KeyEvent.VK_RIGHT) ==  (axes[1][1] == 1))
                           sForm.rotX(Math.PI / 2.0);
                        else
                           sForm.rotX(-Math.PI / 2.0);
                        break;
                     case 1:
                        if ((evt.getKeyCode() == KeyEvent.VK_RIGHT) ==  (axes[1][1] == 1))
                           sForm.rotY(Math.PI / 2.0);
                        else
                           sForm.rotY(-Math.PI / 2.0);
                        break;
                     case 2:
                        if ((evt.getKeyCode() == KeyEvent.VK_RIGHT) ==  (axes[1][1] == 1))
                           sForm.rotZ(Math.PI / 2.0);
                        else
                           sForm.rotZ(-Math.PI / 2.0);
                        break;
                  }
                  break;
            }
            scene.multSwitchTransform(sForm);
            updateAxes();
         }
      });

      canvas.addMouseListener(new MouseAdapter()
      {
         @Override
         public void mousePressed(MouseEvent evt)
         {
            if (evt.getButton() == MouseEvent.BUTTON3)
            {
               lastX = evt.getX();
               lastY = evt.getY();
//               System.out.println("" + evt.getX() + " " + evt.getY());
            }
         }

         @Override
         public void mouseClicked(MouseEvent evt)
         {
            if (evt.getButton() == MouseEvent.BUTTON2)
            {
               scene.reset();
            }
         }
      });
      
      canvas.addMouseWheelListener(new java.awt.event.MouseWheelListener()
      {
         public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt)
         {
            scene.rescaleFromMouseWheel(evt);
         }
      });

      view = new View();
      viewPlatform = new ViewPlatform();

      view.setPhysicalBody(physBody);
      view.setPhysicalEnvironment(physEnv);
      view.attachViewPlatform(viewPlatform);
      view.addCanvas3D(canvas);

      vpTG = new TransformGroup();
      vpTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
      vpTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
      vpTG.addChild(viewPlatform);

      rootBG = new BranchGroup();
      rootBG.setCapability(BranchGroup.ALLOW_DETACH);
      rootBG.addChild(vpTG);
   }
   
   private void updateAxes()
   {
      int h = canvas.getHeight();
      for (int i = 0; i < reper.length; i++)
         locToWin.transformPt(reper[i], reperIm[i]);
      float maxv = 0;
      for (int i = 0; i < 2; i++)
      {
         for (int j = 1; j < 4; j++)
         {
            reperIm[j][i] -= reperIm[0][i];
            if (Math.abs(reperIm[j][i]) > maxv)
               maxv = Math.abs(reperIm[j][i]);
         }
      }
      float d = .06f * h / maxv;
      for (int i = 0; i < 2; i++)
         for (int j = 1; j < 4; j++)
            reperScr[j - 1][i] = (int) (d * reperIm[j][i]);
      for (int i = 0; i < 3; i++)
         for (int j = 0; j < 2; j++)
            if (reperScr[i][j] != 0)
            {
               axes[j][0] = i;
               axes[j][1] = (int) Math.signum(reperScr[i][j]);
            }
      axes[1][1] = -axes[1][1];
      axes[2][0] = Math.abs(lastAx[axes[0][0]][axes[1][0]]) - 1;
      axes[2][1] = axes[0][1] * axes[1][1] * (int) Math.signum(lastAx[axes[0][0]][axes[1][0]]);
//      System.out.printf("%s%n%3d %3d%n%3d %3d%n%3d %3d%n", name,axes[0][0],axes[0][1],axes[1][0],axes[1][1],axes[2][0],axes[2][1]);
      axesUpdated = true;
   }
   
   public void drawLocal2D(J3DGraphics2D vGraphics, LocalToWindow ltw)
   {
      if (orthoView)
      {
         int h = canvas.getHeight();
         if (!axesUpdated)
            updateAxes();
         Color[] axesColors = {Color.RED, Color.GREEN, Color.BLUE};
         int l = (int)(.07 * h);
         int x0 = axes[0][1] > 0 ? 10 : l + 10;
         int y0 = axes[1][1] > 0 ? h - 10 : h - l - 10;
         int lx = axes[0][1] * l;
         int ly = -axes[1][1] * l;
         int r = l/4;
         if (axes[2][1] < 0)
         {
            vGraphics.setColor(axesColors[axes[2][0]]);
            vGraphics.fillOval(x0 - r, y0 - r, 2 * r, 2 * r);
         }
         vGraphics.setStroke(new BasicStroke(2));
         vGraphics.setColor(axesColors[axes[0][0]]);
         vGraphics.drawLine(x0, y0, x0 + lx, y0);
         vGraphics.fillPolygon(new int[] {x0 + lx, x0 + (3 * lx) / 4, x0 + (3 * lx) / 4}, new int[] {y0, y0 + l / 5, y0 - l / 5}, 3);
         vGraphics.setColor(axesColors[axes[1][0]]);
         vGraphics.drawLine(x0, y0, x0, y0 + ly);
         vGraphics.fillPolygon(new int[] {x0, x0 + l / 5, x0 - l / 5}, new int[] {y0 + ly, y0 + (3 * ly) / 4, y0 + (3 * ly) / 4}, 3);
         if (axes[2][1] > 0)
         {
            vGraphics.setColor(axesColors[axes[2][0]]);
            vGraphics.fillOval(x0 - r, y0 - r, 2 * r, 2 * r);
         }
      }
      Font f = vGraphics.getFont();
      Color c = vGraphics.getColor();
      vGraphics.setFont(new Font("Dialog", Font.PLAIN, 14));
      vGraphics.setColor(Color.BLUE);
      vGraphics.drawString(name, 5, 18);
      vGraphics.setFont(f);
      vGraphics.setColor(c);
   }

   public void draw2D(J3DGraphics2D vGraphics, LocalToWindow ltw)
   {
      try
      {
         drawLocal2D(vGraphics, ltw);
         rootObject.draw2D(vGraphics, ltw);
      } catch (java.util.ConcurrentModificationException e)
      {
      }
   }

   public void setName(String name)
   {
      this.name = name;
   }
   
   public TransformGroup getViewPlatformTransformGroup()
   {
      return this.vpTG;
   }

   public BranchGroup getRootBG()
   {
      return this.rootBG;
   }

   public View getView()
   {
      return this.view;
   }

   public Canvas3D getCanvas3D()
   {
      return this.canvas;
   }

   public boolean isOrthoView()
   {
      return orthoView;
   }

   public void setOrthoView(boolean orthoView)
   {
      this.orthoView = orthoView;
   }
   
   public void setScene(ModelScene scene)
   {
      this.scene = scene;
      rootObject = scene.getRootObject();
      objMain = scene.getObjMain();
      locToWin = new LocalToWindow(objMain, canvas);
   }

   public void setStdAxes(int[][] stdAxes)
   {
      this.stdAxes = stdAxes;
   }
   
   public void translateHorizontal(int pixels)
   {
      float[] coords = new float[3];
      locToWin.reverseTransformPt(pixels, 0, 0, coords);
      scene.setTranslate(coords);
   }
   
   public void translateVertical(int pixels)
   {
      float[] coords = new float[3];
      locToWin.reverseTransformPt(0, pixels, 0, coords);
      scene.setTranslate(coords);
      
   }
}
