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

package pl.edu.icm.visnow.geometries.objects;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;
import javax.media.j3d.CapabilityNotSetException;
import javax.media.j3d.Group;
import javax.media.j3d.J3DGraphics2D;
import javax.media.j3d.MultipleParentException;
import javax.media.j3d.Node;
import javax.media.j3d.Texture2D;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransparencyAttributes;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.Point3f;
import pl.edu.icm.visnow.geometries.events.ColorEvent;
import pl.edu.icm.visnow.geometries.events.ColorListener;
import pl.edu.icm.visnow.geometries.events.TransformEvent;
import pl.edu.icm.visnow.geometries.events.TransformListener;
import pl.edu.icm.visnow.geometries.objects.generics.OpenAppearance;
import pl.edu.icm.visnow.geometries.objects.generics.OpenBranchGroup;
import pl.edu.icm.visnow.geometries.objects.generics.OpenShape3D;
import pl.edu.icm.visnow.geometries.objects.generics.OpenTransformGroup;
import pl.edu.icm.visnow.geometries.parameters.AbstractRenderingParams;
import pl.edu.icm.visnow.geometries.parameters.RenderingParams;
import pl.edu.icm.visnow.geometries.utils.transform.LocalToWindow;
import pl.edu.icm.visnow.geometries.viewer3d.Display3DPanel;
import pl.edu.icm.visnow.geometries.viewer3d.RenderingWindowInterface;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.pick.PickEvent;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.pick.PickListener;
import pl.edu.icm.visnow.lib.templates.visualization.modules.RenderWindowListeningModule;
import pl.edu.icm.visnow.lib.utils.geometry2D.GeometryObject2DStruct;

/**
 *
 * @author  Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class GeometryObject implements GeometryParent, Comparable
{
   protected static final int COLOR                   = 1;
   protected static final int AMBIENTCOLOR            = 2;
   protected static final int DIFFUSECOLOR            = 4;
   protected static final int SPECULARCOLOR           = 8;
   protected static final int TRANSPARENCY            = 16;
   protected static final int SHININESS               = 32;
   protected static final int LINEWIDTH               = 64;
   protected static final int LINESTYLE               = 128;
    /**
     * Previously: WINDOW_SETTING.
     * Informs that the GeometryObject is just being added to Java3D scene. It's not clear why it is
     * called in the middle of adding though.
     */
   protected static final int SCENE_TREE_BEING_ADDED = 1;
    /**
     * Informs that the GeometryObject has been just added to Java3D scene.
     */
   protected static final int SCENE_ADDED = 2;
    /**
     * Previously: WINDOW_REMOVING.
     * Informs that the GeometryObject is about to be removed from Java3D scene.
     */
   protected static final int SCENE_TREE_ABOUT_TO_REMOVE = -1;
//
   protected static int timeStamp = 0;
   protected int id                                   = 0;
   protected OpenBranchGroup geometryObj              = new OpenBranchGroup();
   protected final OpenBranchGroup outObj             = new OpenBranchGroup();
   protected GeometryObject2DStruct outObj2DStruct    = new GeometryObject2DStruct();
   protected OpenTransformGroup transformObj          = new OpenTransformGroup();
   protected GeometryParent parent                    = null;
   protected String name                              = null;
   protected boolean needToKnowProjection             = false;
   protected SortedSet<GeometryObject> geomChildren   = Collections.synchronizedSortedSet(new TreeSet<GeometryObject>());
   protected AbstractRenderingParams renderingParams  = new RenderingParams(this);
   protected OpenAppearance appearance                = renderingParams.getAppearance();
   protected OpenAppearance lineAppearance            = renderingParams.getLineAppearance();
   protected RenderingWindowInterface renderingWindow = null;
   protected RenderWindowListeningModule creator      = null;
   protected float[][] ownExtents                     = {{Float.MAX_VALUE,Float.MAX_VALUE,Float.MAX_VALUE},{-Float.MAX_VALUE,-Float.MAX_VALUE,-Float.MAX_VALUE}};
   protected float[][] extents                        = {{Float.MAX_VALUE,Float.MAX_VALUE,Float.MAX_VALUE},{-Float.MAX_VALUE,-Float.MAX_VALUE,-Float.MAX_VALUE}};
   protected float[][] maxExtents                     = {{Float.MAX_VALUE,Float.MAX_VALUE,Float.MAX_VALUE},{-Float.MAX_VALUE,-Float.MAX_VALUE,-Float.MAX_VALUE}};//{{0,0,0},{0,0,0}};
   protected Texture2D texture                        = null;
   protected TextureAttributes ta                     = new TextureAttributes();
   protected Vector<Geometry2D> geometries2D          = new Vector<Geometry2D>();
   protected SignalingTransform3D currentTransform    = new SignalingTransform3D();
   protected LocalToWindow localToWindow              = null;
   protected TransformListener currentTransformListener;
   protected ColorListener backgroundColorListener;
   /**
    * Creates a new instance of GeometryObject
    */

   /**
    * Creates a new instance of GeometryObject
    * @param name object name 
    * @param timestamp timestamp used for rendering synchronization
    */
   public GeometryObject(String name, int timestamp)
   {
      id = timestamp;
      this.name = name+id;
      appearance.setUserData(this);
      lineAppearance.setUserData(this);
      backgroundColorListener = new ColorListener()
      {
         @Override
         public void colorChoosen(ColorEvent e)
         {
            renderingParams.setBackgroundColor(e.getSelectedColor());
            fireBgrChanged(e.getSelectedColor());
         }
      };
      outObj.setUserData(this);
      transformObj.addChild(geometryObj);
      outObj.addChild(transformObj);
      outObj.setName(name);
      outObj2DStruct.setName(name);


       currentTransformListener = new TransformListener() {
          @Override
          public void transformChanged(TransformEvent e) {
              updateExtents();
          }
       };
       currentTransform.addTransformListener(currentTransformListener);
   }

   public GeometryObject(String name)
   {
      this(name, timeStamp);
      timeStamp += 1;
   }

   public GeometryObject()
   {
      this("object");
   }

   public ArrayList<PickListener> getPickListenerList()
   {
      return PickListenerList;
   }

  /**
    * Utility field holding list of PickListeners.
    */
   protected transient ArrayList<PickListener> PickListenerList =
           new ArrayList<PickListener>();

   /**
    * Registers PickListener to receive events.
    * @param listener The listener to register.
    */
   public synchronized void addPickListener(PickListener listener)
   {
      PickListenerList.add(listener);
   }

   /**
    * Removes PickListener from the list of listeners.
    * @param listener The listener to remove.
    */
   public synchronized void removePickListener(PickListener listener)
   {
      PickListenerList.remove(listener);
   }

   /**
    * Notifies all registered listeners about the event.
    *
    */
   public void firePickChanged(MouseEvent evt, LocalToWindow localToWindow)
   {
      if(localToWindow == null)
           return;

      if (transformObj.numChildren()>0)
         localToWindow.update(transformObj.getChild(0));
      else
         localToWindow.update(transformObj);

      PickEvent e = new PickEvent(this, evt, localToWindow);
      for (GeometryObject geometryObject : geomChildren)
         geometryObject.firePickChanged(evt, localToWindow);
      for (PickListener listener: PickListenerList)
         listener.pickChanged(e);
   }

   @Override
   public int getAreaWidth()
   {
      if (parent==null)
         return 100;
      return parent.getAreaWidth();
   }

   @Override
   public int getAreaHeight()
   {
      if (parent==null)
         return 100;
      return parent.getAreaHeight();
   }

   public void drawLocal2D(J3DGraphics2D vGraphics, LocalToWindow ltw, int w, int h)
   {
   }

   public void setParentGeom(GeometryParent parent)
   {
      if (parent == null)
         this.parent = parent;
      else
         synchronized (parent)
         {
            this.parent = parent;
            getRenderingParams().setParentParams(parent.getRenderingParams());
         }
   }

   public void attach()
   {
      if (parent!=null)
         synchronized(parent)
         {
            parent.addChild(this);
         }
   }

   public boolean detach()
   {
      if (parent!=null)
      {
         synchronized(parent)
         {
            return parent.removeChild(this);
         }
      }
      return false;
   }

   @Override
   public synchronized void addChild(GeometryObject child)
   {
      boolean is =  false;
      for (GeometryObject x: geomChildren)
         if (x.getId()==child.getId())
            is = true;
      if (is) removeChild(child);
      child.setParentGeom(this);
      if (geomChildren.add(child))
      {
         child.consumeRenderingWindowInfo(renderingWindow, SCENE_TREE_BEING_ADDED);
         if (child.getGeometryObj().getParent() != null)
            child.getGeometryObj().detach();
         try
         {
            geometryObj.addChild(child.getGeometryObj());
         } catch (MultipleParentException e)
         {
            System.out.println("dangling geometric reference");
         }
         updateExtents();
         child.consumeRenderingWindowInfo(renderingWindow, SCENE_ADDED);
      }
   }

   @Override
   public OpenBranchGroup getGeometryObj()
   {
      return outObj;
   }

   public GeometryObject2DStruct getGeometryObj2DStruct()
   {
      return outObj2DStruct;
   }

   public SignalingTransform3D getCurrentTransform()
   {
      return currentTransform;
   }

   public void setCurrentTransform(SignalingTransform3D currentTransform)
   {
       if(this.currentTransform != null) {
           this.currentTransform.removeTransformListener(currentTransformListener);
       }
      this.currentTransform = currentTransform;
      this.currentTransform.addTransformListener(currentTransformListener);
   }

   
   @Override
   public synchronized void clearAllGeometry()
   {
      try
      {
         for (GeometryObject child : geomChildren)
         {
            child.consumeRenderingWindowInfo(renderingWindow, SCENE_TREE_ABOUT_TO_REMOVE);
         }
         geomChildren.clear();
         geometryObj.removeAllChildren();
         this.setExtents(maxExtents);
      } catch (Exception e) {}
   }

   /**
    * Tests if geometry is empty.
    * @return true if geometry object has no children
    */
   public boolean isEmpty() {
       return !geometryObj.getAllChildren().hasMoreElements();
   }

   private void updateAppearance(Node node)
   {
         if (node instanceof OpenShape3D && ((OpenShape3D)node).getAppearance() == null)
            ((OpenShape3D)node).setAppearance(appearance);
         else if (node instanceof Group)
      {
         Group g = (Group)node;
         if (!g.getCapability(Group.ALLOW_CHILDREN_READ) || g.getUserData() instanceof GeometryObject )
            return;
         int n = g.numChildren();
         for (int i=0;i<n;i++)
         {
            updateAppearance(g.getChild(i));
         }
      }

   }

   @Override
   public void addNode(Node node)
   {
      synchronized (geometryObj)
      {
         updateAppearance(node);
         geometryObj.addChild(node);
      }
   }

   public void removeNode(Node node)
   {
         geometryObj.removeChild(node);
   }


   @Override
   public synchronized boolean removeChild(GeometryObject child)
   {
      child.consumeRenderingWindowInfo(renderingWindow, SCENE_TREE_ABOUT_TO_REMOVE);
      boolean success = geomChildren.remove(child);
      geometryObj.removeChild(child.getGeometryObj());
      updateExtents();
      return success;
   }   
   
   public boolean isAncestor(GeometryObject obj)
   {
      if (geomChildren.contains(obj))
         return true;
      for (GeometryObject child : geomChildren)
         if (child.isAncestor(obj))
            return true;
      return false;
   }

   protected synchronized void refresh()
   {
      if (parent!=null)
         detach();
      geometryObj = new OpenBranchGroup();
      transformObj = new OpenTransformGroup();
      transformObj.addChild(geometryObj);
      if (parent!=null)
         parent.addChild(this);
   }

   @Override
   public void draw2D(J3DGraphics2D vGraphics, LocalToWindow ltw, int w, int h)
   {
      localToWindow = ltw;
      Font f = vGraphics.getFont();
      Color c = vGraphics.getColor();
      vGraphics.setFont(getRenderingParams().getAnnoFont());
      vGraphics.setColor(getRenderingParams().getColor());
      if (transformObj.numChildren()>0)
         localToWindow.update(transformObj.getChild(0));
      else
         localToWindow.update(transformObj);
      for (GeometryObject g: geomChildren)
         g.draw2D(vGraphics, localToWindow, w, h);
      drawLocal2D(vGraphics, localToWindow, w, h);
//      System.out.println(""+this+" "+geometries2D.size());
      for (Geometry2D geometry2D : geometries2D)
         if (geometry2D != null)
         {
//            System.out.println("drawing "+geometry2D.getName()+" "+this);
            geometry2D.draw2D(vGraphics, localToWindow, h, w);
         }
      vGraphics.setColor(c);
      vGraphics.setFont(f);
   }

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name + "_" + id;
      transformObj.setName(name + "transformObj");
      outObj.setName(name + "outObj");
      geometryObj.setName(name + "geomObj");
   }

   public int getId()
   {
      return id;
   }

   @Override
   public int compareTo(Object obj)
   {
      if (obj instanceof GeometryObject)
      {
         GeometryObject g = (GeometryObject)obj;
         if (id<g.getId())
            return -1;

         if (id > g.getId())
             return 1;return 0;
      }
      return 0;
   }

   @Override
   public void setScale(double s)
   {
      if (parent!=null)
         parent.setScale(s);
   }

   @Override
   public SortedSet<GeometryObject> getChildren()
   {
      return geomChildren;
   }

   @Override
   public void revalidate()
   {
      if (parent!=null)
         parent.revalidate();
   }

   @Override
   public String toString()
   {
      return name;
   }

   public float[][] getExtents()
   {
      return extents;
   }

   @Override
   public void updateExtents()
   {
      for (int i = 0; i < 3; i++)
      {
            extents[0][i] = ownExtents[0][i];
            extents[1][i] = ownExtents[1][i];
      }
      for (GeometryObject obj : geomChildren)
      {
         float[][] ext = obj.getExtents();
         for (int i = 0; i < 3; i++)
         {
            if (ext[0][i]<extents[0][i]) extents[0][i] = ext[0][i];
            if (ext[1][i]>extents[1][i]) extents[1][i] = ext[1][i];
         }
      }

      if(currentTransform != null) {
          Transform3D tr = currentTransform.getTransform();
          Point3f pt = new Point3f(extents[0]);
          tr.transform(pt);
          pt.get(extents[0]);

          pt = new Point3f(extents[1]);
          tr.transform(pt);
          pt.get(extents[1]);
      }
      if (parent!=null)
         parent.updateExtents();
   
   }

   @Override
   public void setExtents(float[][] ext)
   {
      if (ext==null || ext.length!=2 || ext[0].length!=3 || ext[1].length!=3)
         return;
      for (int i = 0; i < 3; i++)
         for (int j = 0; j < ext.length; j++)
            ownExtents[j][i] = ext[j][i];
      updateExtents();
   }

   private void printGeomDebugInfo(Node node)
   {
      System.out.println(""+this);
      if (node instanceof Group)
      {
         Group g = (Group)node;
         System.out.println(""+g);
         int n = g.numChildren();
         System.out.println("group: "+n);
         for (int i=0;i<n;i++)
         {
            System.out.println("obj "+i);
            printGeomDebugInfo(g.getChild(i));
         }
      }
      else
      {
         if (node instanceof OpenShape3D)
         {
            System.out.print("shape: ");
            OpenShape3D s = (OpenShape3D)node;
            OpenAppearance app = s.getAppearance();
            if (app!=null)
            {
               System.out.print("app: ");
               TransparencyAttributes tattr = app.getTransparencyAttributes();
               if (tattr!=null)
               {
                  System.out.print("transp: "+tattr.getTransparency());
               }
               else
                  System.out.println("no transp att");
            }
            System.out.println("");
         }
         else
            System.out.println("other");
      }
   }

   @Override
   public void printDebugInfo()
   {
      System.out.println(name);
      printGeomDebugInfo(geometryObj);
      for (GeometryObject next: geomChildren)
         next.printDebugInfo();
   }

   public void traverseGeometry(int function)
   {
      for (GeometryObject obj: geomChildren)
         obj.traverseGeometry(function);
      traverseGeometry(geometryObj, function);
   }

    /**
     * Notifies this object about being removed from the scene tree or being added to it.
     * <p/>
     * @param renderingWindow
     * @param function        event, possible values: SCENE_TREE_ABOUT_TO_REMOVE, SCENE_TREE_BEING_ADDED or
     *                        SCENE_ADDED
     */
   public void consumeRenderingWindowInfo(RenderingWindowInterface renderingWindow, int function)
   {
      for (GeometryObject obj: geomChildren)
         obj.consumeRenderingWindowInfo(renderingWindow, function);
      try
      {
         if (function == SCENE_TREE_ABOUT_TO_REMOVE && renderingWindow != null && creator != null)
         {
            renderingWindow.removeBgrColorListener(backgroundColorListener);
            renderingWindow.removeBgrColorListener(creator.getBackgroundColorListener());
            if (creator.getPick3DListener() != null)
                renderingWindow.removePick3DListener(creator.getPick3DListener());
            if (creator.getProjectionListener() != null)
                renderingWindow.removeProjectionListener(creator.getProjectionListener());
            if (creator.getFrameRenderedListener() != null)
                renderingWindow.removeFrameRenderedListener(creator.getFrameRenderedListener());
            this.renderingWindow = null;
         }
         if (renderingWindow != null)
            this.renderingWindow = renderingWindow;
         if (function == SCENE_TREE_BEING_ADDED && renderingWindow != null && creator != null)
         {
            renderingWindow.addBgrColorListener(backgroundColorListener);
            renderingWindow.addBgrColorListener(creator.getBackgroundColorListener());
            renderingWindow.addPick3DListener(creator.getPick3DListener());
            renderingWindow.addProjectionListener(creator.getProjectionListener());
            renderingWindow.addFrameRenderedListener(creator.getFrameRenderedListener());
         }
      } catch (Exception e)
      {
      }
   }

   public void traverseGeometry(Node node, int function)
   {
      if (node instanceof Group)
      {
         Group g = (Group)node;
         if (!g.getCapability(Group.ALLOW_CHILDREN_READ) || g.getUserData() instanceof GeometryObject )
            return;
         int n = g.numChildren();
         for (int i=0;i<n;i++)
         {
            traverseGeometry(g.getChild(i), function);
         }
      }
      else
      {
         if (node instanceof OpenShape3D)
         {
            try
            {
               OpenShape3D s = (OpenShape3D)node;
               s.setAppearance(appearance);
            }
            catch (CapabilityNotSetException e)
            {
            }
         }
      }
   }

   public Transform3D getTransform()
   {
      return currentTransform.getTransform();
   }

   @Override
   public void setTransform(Transform3D transform)
   {
      transformObj.setTransform(transform);
      currentTransform.setTransform(transform);
   }

   @Override
   public void setTransparency()
   {
      traverseGeometry(TRANSPARENCY);
   }

   @Override
   public void setShininess()
   {
      traverseGeometry(SHININESS);
   }

   @Override
   public void setLineThickness()
   {
      traverseGeometry(geometryObj,LINEWIDTH);
   }

   @Override
   public void setLineStyle()
   {
      traverseGeometry(geometryObj,LINESTYLE);
   }

   @Override
   public void setColor()
   {
      traverseGeometry(geometryObj,COLOR|AMBIENTCOLOR|DIFFUSECOLOR);
   }

   @Override
   public AbstractRenderingParams getRenderingParams()
   {
      return renderingParams;
   }

   public void setRenderingParams(AbstractRenderingParams params)
   {
      this.renderingParams = params;
   }

   public RenderingWindowInterface getRenderingWindow()
   {
      return renderingWindow;
   }

   public void setRenderingWindow(RenderingWindowInterface renderingWindow)
   {
      this.renderingWindow = renderingWindow;
   }

   public RenderWindowListeningModule getCreator()
   {
      return creator;
   }

   public final void setCreator(RenderWindowListeningModule creator)
   {
      this.creator = creator;
   }

   public void addGeometry2D(Geometry2D g)
   {
      geometries2D.add(g);
   }

   public void removeGeometry2D(Geometry2D g)
   {
      geometries2D.remove(g);
   }

   public void clearGeometries2D()
   {
      geometries2D.clear();
   }

   public Vector<Geometry2D> getGeometries2D()
   {
      return geometries2D;
   }

   public ColorListener getBackgroundColorListener()
   {
      return backgroundColorListener;
   }
   
   public OpenTransformGroup getTransformObj()
   {
      return transformObj;
   }

   public LocalToWindow getLocalToWindow()
   {
      return localToWindow;
   }

   /**
    * Utility field holding list of RenderEventColorListeners.
    */
   protected transient ArrayList<ColorListener> bgrColorListenerList =
           new ArrayList<ColorListener>();

   /**
    * Registers RenderEventColorListener to receive events.
   */
   public synchronized void addBgrColorListener(ColorListener colorListener)
   {
      if(!bgrColorListenerList.contains(colorListener))
        bgrColorListenerList.add(colorListener);
   }

   /**
    * Removes RenderEventColorListener from the list of ColorListeners.
    * @param colorListener The ColorListener to remove.
    */
   public synchronized void removeBgrColorListener(ColorListener colorListener)
   {
      bgrColorListenerList.remove(colorListener);
   }
   
   public synchronized void clearBgrColorListeners()
   {
      bgrColorListenerList.clear();
   }

   /**
    * Notifies all registered ColorListeners about the event.
    *
    */
   public void fireBgrChanged(Color color)
   {
      ColorEvent e = new ColorEvent(this, color);
      for (ColorListener colorListener : bgrColorListenerList)
         colorListener.colorChoosen(e);
   }

   protected ChangeListener renderingListener;

   public void fireStartRendering()
   {
      if (renderingListener != null)
         renderingListener.stateChanged(new ChangeEvent(true));
   }

   public void fireStopRendering()
   {
      if (renderingListener != null)
         renderingListener.stateChanged(new ChangeEvent(false));
   }

   public ChangeListener getRenderingListener()
   {
      return renderingListener;
   }

   public void setRenderingListener(ChangeListener renderingListener)
   {
      this.renderingListener = renderingListener;
   }

   protected Display3DPanel myViewer = null;

   public void setCurrentViewer(Display3DPanel panel) {
       this.myViewer = panel;
       outObj.setCurrentViewer(panel);

       for(GeometryObject child : geomChildren) {
           child.setCurrentViewer(panel);
       }
   }
   public Display3DPanel getCurrentViewer() {
       return myViewer;
   }
   
   @Override
   public Color getBackgroundColor()
   {
      if (parent != null)
         return parent.getBackgroundColor();
      return Color.BLACK;
   }
}
