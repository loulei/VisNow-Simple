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

package pl.edu.icm.visnow.geometries.parameters;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;
import pl.edu.icm.visnow.geometries.objects.GeometryObject;
import pl.edu.icm.visnow.geometries.objects.GeometryParent;
import pl.edu.icm.visnow.geometries.objects.RegularFieldGeometry;
import pl.edu.icm.visnow.geometries.objects.generics.OpenAppearance;
import pl.edu.icm.visnow.geometries.objects.generics.OpenBranchGroup;
import pl.edu.icm.visnow.geometries.objects.generics.OpenMaterial;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.render.RenderEvent;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.render.RenderEventListener;

/**
 *
 * @author  Krzysztof S. Nowinski, University of Warsaw, ICM
 * @author  Bartosz Borucki, University of Warsaw, ICM
 * 
 */
public class RenderingParams implements Cloneable, AbstractRenderingParams
{

   protected static final Color defColor = Color.WHITE;
   protected static final Color defAnnoColor = Color.WHITE;
   protected static final Color3f defAmbientColor = new Color3f(.5f, .5f, .5f);
   protected static final Color3f defDiffuseColor = new Color3f(.5f, .5f, .5f);
   protected static final Color3f defSpecularColor = new Color3f(.2f, .2f, .2f);
   protected static final Font defAnnoFont = new Font("Dialog", Font.PLAIN, 10);

   protected int displayMode = SURFACE | IMAGE;
   protected int shadingMode = GOURAUD_SHADED;
   protected float minEdgeDihedral = 0;
   protected boolean surfaceOrientation = true;
   protected float surfaceOffset = 500;
   protected OpenAppearance appearance = new OpenAppearance();
   protected OpenMaterial material = (OpenMaterial)appearance.getMaterial();
   protected OpenAppearance lineAppearance = new OpenAppearance();
   protected Font annoFont = null;
   protected boolean annoFontInherited = true;
   protected Color annoColor = null;
   protected boolean annoColorInherited = true;
   protected Color color = null;
   protected GeometryParent object = null;
   protected RenderingParams parentParams = null;
   protected Color bgrColor = Color.BLACK;
   protected Color3f bgrColor3f = new Color3f(bgrColor);
   protected boolean lineLighting = false;
   protected boolean ignoreMask = false;
   protected boolean inherited = true;
   protected boolean active = true;
   protected boolean lightedBackground = true;
   
   protected RenderEventListener transparencyChangeListener = new RenderEventListener()
   {
      @Override
      public void renderExtentChanged(RenderEvent e)
      {
         if (e.getSource() instanceof TransparencyParams)
         {
            TransparencyParams trSource = (TransparencyParams)e.getSource();
            if (trSource.getComponent() < 0 && appearance.getTransparencyAttributes().getTransparency() == 0)
               appearance.getTransparencyAttributes().setTransparencyMode(TransparencyAttributes.NONE);
            else
               appearance.getTransparencyAttributes().setTransparencyMode(TransparencyAttributes.NICEST);
            fireStateChanged(RenderEvent.TRANSPARENCY);
         }
      }
   };

   /** Creates a new instance of RenderingParams */
   public RenderingParams()
   {
      material.setColorTarget(Material.AMBIENT_AND_DIFFUSE);
      material.setAmbientColor(defAmbientColor);
      material.setDiffuseColor(defDiffuseColor);
      material.setSpecularColor(defSpecularColor);
      material.setLightingEnable(true);
      boolean detach = detachUserData(appearance.getUserData()); 
      if (appearance != null && appearance.getPolygonAttributes() != null)
      {
         appearance.getPolygonAttributes().setCullFace(PolygonAttributes.CULL_NONE);
         appearance.getPolygonAttributes().setPolygonOffset(surfaceOffset);
         appearance.getPolygonAttributes().setBackFaceNormalFlip(false);
         fireStateChanged(RenderEvent.GEOMETRY);
      }
      if(detach) attachUserData(appearance.getUserData());
   }

   public RenderingParams(GeometryParent object)
   {
      this();
      this.object = object;
   }
   
   @Override
   public String toString()
   {
      StringBuilder s = new StringBuilder();
      if ((displayMode & SURFACE) != 0)
         s.append(" surface ");
      if ((displayMode & EDGES) != 0)
         s.append(" edges ");
      return s.toString();
   }

   /**
    * Getter for property color.
    * @return Value of property color.
    */
   @Override
   public Color getColor()
   {
      if (color != null)
      {
         Color3f c = new Color3f();
         appearance.getColoringAttributes().getColor(c);
         return c.get();
      }
      if (parentParams != null)
         return parentParams.getColor();
      return defColor;
   }

   @Override
   public OpenAppearance getAppearance()
   {
      return appearance;
   }

   @Override
   public OpenAppearance getLineAppearance()
   {
      return lineAppearance;
   }

   @Override
   public int getDisplayMode()
   {
      return displayMode;
   }

   @Override
   public void setDisplayMode(int displayMode)
   {
      this.displayMode = displayMode;
      fireStateChanged(RenderEvent.GEOMETRY);
   }

   @Override
   public boolean getSurfaceOrientation()
   {
      return surfaceOrientation;
   }

   @Override
   public void setSurfaceOrientation(boolean orientation)
   {
      this.surfaceOrientation = orientation;
      fireStateChanged(RenderEvent.GEOMETRY);
   }

   /**
    * Setter for property color.
    * @param color New value of property color.
    */
   @Override
   public void setColor(Color color)
   {
      boolean detach = detachUserData(appearance.getUserData()); 
      float[] fC = new float[3];
      color.getColorComponents(fC);
      this.color = color;
      appearance.getColoringAttributes().setColor(new Color3f(fC));
      if (object != null)
         object.setColor();
      if(detach) attachUserData(appearance.getUserData());
      fireStateChanged(RenderEvent.COLORS);
   }

   public void resetColor()
   {

      this.color = null;
   }

   /**
    * Getter for property ambientColor.
    * @return Value of property ambientColor.
    */
   @Override
   public Color3f getAmbientColor()
   {
      Color3f c = new Color3f(0,0,0);
      material.getAmbientColor(c);
      return c;
   }

   /**
    * Setter for property ambientColor.
    * @param ambientColor New value of property fColor.
    */
   @Override
   public void setAmbientColor(Color3f ambientColor)
   {
      material.setAmbientColor(ambientColor);
      fireStateChanged(RenderEvent.COLORS);
   }

   /**
    * Getter for property diffuseColor.
    * @return Value of property diffuseColor.
    */
   @Override
   public Color3f getDiffuseColor()
   {
      Color3f c = new Color3f();
      material.getDiffuseColor(c);
      return c;
   }

   /**
    * Setter for property diffuseColor.
    * @param diffuseColor New value of property fColor.
    */
   @Override
   public void setDiffuseColor(Color3f diffuseColor)
   {       
      material.setDiffuseColor(diffuseColor);
      lineAppearance.getColoringAttributes().setColor(diffuseColor);
      fireStateChanged(RenderEvent.COLORS);
   }


   /**
    * Getter for property specularColor.
    * @return Value of property specularColor.
    */
   @Override
   public Color3f getSpecularColor()
   {
      Color3f c = new Color3f();
      material.getSpecularColor(c);
      return c;
   }

   /**
    * Setter for property specularColor.
    * @param specularColor New value of property fColor.
    */
   @Override
   public void setSpecularColor(Color3f specularColor)
   {
      material.setSpecularColor(specularColor);
      fireStateChanged(RenderEvent.COLORS);
   }

   /**
    * Getter for property transparency.
    * @return Value of property transparency.
    */
   @Override
   public float getTransparency()
   {
      return appearance.getTransparencyAttributes().getTransparency();
   }

   /**
    * Setter for property transparency.
    * @param transparency New value of property transparency.
    */
   @Override
   public void setTransparency(float transparency)
   {
      boolean detach = detachUserData(appearance.getUserData()); 
      if (transparency == 0)
         appearance.getTransparencyAttributes().setTransparencyMode(TransparencyAttributes.NONE);
      else
         appearance.getTransparencyAttributes().setTransparencyMode(TransparencyAttributes.NICEST);
      appearance.getTransparencyAttributes().setTransparency(transparency);
      if (object != null)
         object.setTransparency();
      if(detach) attachUserData(appearance.getUserData());
      fireStateChanged(RenderEvent.TRANSPARENCY);
   }

   @Override
   public void setCullMode(int cullFace)
   {
      boolean detach = detachUserData(appearance.getUserData());  
      appearance.getPolygonAttributes().setCullFace(cullFace);
      if(detach) attachUserData(appearance.getUserData());
   }

   /**
    * Getter for property shininess.
    * @return Value of property shininess.
    */
   @Override
   public float getShininess()
   {
      return material.getShininess();
   }

   /**
    * Setter for property shininess.
    * @param shininess New value of property shininess.
    */
   @Override
   public void setShininess(float shininess)
   {
      material.setShininess(shininess);
      if (object != null)
         object.setShininess();
   }


   /**
    * Getter for property annoFont.
    * @return Value of property annoFont.
    */
   @Override
   public Font getAnnoFont()
   {
      if (annoFont != null)
         return this.annoFont;
      if (parentParams != null)
         return parentParams.getAnnoFont();
      return defAnnoFont;
   }

   /**
    * Setter for property annoFont.
    * @param annoFont New value of property annoFont.
    */
   @Override
   public void setAnnoFont(Font annoFont)
   {

      this.annoFont = annoFont;
   }

   /**
    * Holds value of property lineThickness.
    */
   /**
    * Getter for property lineThickness.
    * @return Value of property lineThickness.
    */
   @Override
   public float getLineThickness()
   {
      return lineAppearance.getLineAttributes().getLineWidth();
   }

   /**
    * Setter for property lineThickness.
    * @param lineThickness New value of property lineThickness.
    */
    @Override
   public void setLineThickness(float lineThickness)
   {
       
      boolean detach = detachUserData(lineAppearance.getUserData());     
      lineAppearance.getLineAttributes().setLineWidth(lineThickness);    
      lineAppearance.getPointAttributes().setPointSize(lineThickness);
      if (object != null)
         object.setLineThickness();
      if(detach) attachUserData(lineAppearance.getUserData());
      fireStateChanged(RenderEvent.APPEARANCE);
   }

    @Override
   public int getLineStyle()
   {
      return lineAppearance.getLineAttributes().getLinePattern();
   }

    @Override
   public void setLineStyle(int lineStyle)
   {
      boolean detach = detachUserData(lineAppearance.getUserData());         
      lineAppearance.getLineAttributes().setLinePattern(lineStyle);
      if (object != null)
         object.setLineStyle();
      if(detach) attachUserData(lineAppearance.getUserData());
      fireStateChanged(RenderEvent.APPEARANCE);
   }

    @Override
   public boolean isLineLighting()
   {
      return lineLighting;
   }

    @Override
   public void setLineLighting(boolean lineLighting)
   {
      this.lineLighting = lineLighting;
      fireStateChanged(RenderEvent.GEOMETRY);
   }
   

   /**
    * Getter for property parentParams.
    * @return Value of property parentParams.
    */
   public RenderingParams getParentParams()
   {
      return this.parentParams;
   }

   /**
    * Setter for property parentParams.
    * @param parentParams New value of property parentParams.
    */
   public void setParentParams(RenderingParams parentParams)
   {
      this.parentParams = parentParams;
   }

   /**
    * Getter for property annoColor.
    * @return Value of property annoColor.
    */
   @Override
   public Color getAnnoColor()
   {
      if (annoColor != null)
         return this.annoColor;
      if (parentParams != null)
         return parentParams.getAnnoColor();
      if (color != null)
         return color;
      return defAnnoColor;
   }

   /**
    * Setter for property annoColor.
    * @param annoColor New value of property annoColor.
    */
   @Override
   public void setAnnoColor(Color annoColor)
   {

      this.annoColor = annoColor;
      fireStateChanged(RenderEvent.COLORS);
   }

   public boolean isAnnoFontInherited()
   {
      return annoFontInherited;
   }

   public boolean isAnnoColorInherited()
   {
      return annoColorInherited;
   }

   @Override
   public Color3f getColorf()
   {
      return new Color3f(color);
   }

   @Override
   public void setColorf(Color3f colorf)
   {
      setColor(colorf.get());
      fireStateChanged(RenderEvent.COLORS);
   }

   @Override
   public Color getBackgroundColor()
   {
      return bgrColor;
   }

   @Override
   public void setBackgroundColor(Color backgroundColor)
   {
      bgrColor3f = new Color3f(backgroundColor);
      setDiffuseColor(bgrColor3f);
      material.setDiffuseColor(bgrColor3f);
      material.setAmbientColor(bgrColor3f);
      material.setEmissiveColor(bgrColor3f);
   }

   @Override
   public int getShadingMode()
   {
      return shadingMode;
   }

   @Override
   public void setShadingMode(int shadingMode)
   {
      boolean detach = detachUserData(appearance.getUserData());
      this.shadingMode = shadingMode;
      if (shadingMode == UNSHADED)
         appearance.setMaterial(null);
      else
         appearance.setMaterial(material);
      if (shadingMode == BACKGROUND)
      {
         if (appearance != null && appearance.getColoringAttributes() != null)
            appearance.getColoringAttributes().setColor(bgrColor3f);
         setDiffuseColor(bgrColor3f);
         material.setDiffuseColor(bgrColor3f);
         material.setAmbientColor(bgrColor3f);
         material.setEmissiveColor(bgrColor3f);
      }
      fireStateChanged(RenderEvent.GEOMETRY);
      if(detach) attachUserData(appearance.getUserData());
   }

   @Override
   public void setAppearance(OpenAppearance appearance)
   {
      this.appearance = appearance;
   }

   @Override
   public void setLineAppearance(OpenAppearance appearance)
   {
      this.lineAppearance = appearance;
   }

   @Override
   public float getMinEdgeDihedral()
   {
      return minEdgeDihedral;
   }

   @Override
   public void setMinEdgeDihedral(float minEdgeDihedral)
   {
      this.minEdgeDihedral = minEdgeDihedral;
      fireStateChanged(RenderEvent.GEOMETRY);  
   }

   @Override
   public float getPointSize()
   {
      return lineAppearance.getPointAttributes().getPointSize();
   }

   @Override
   public void setPointSize(float pointSize)
   {
      boolean detach = detachUserData(lineAppearance.getUserData()); 
      lineAppearance.getPointAttributes().setPointSize(pointSize);
      if(detach) attachUserData(lineAppearance.getUserData());
   }

   public void copy(RenderingParams src)
   {
      displayMode = src.displayMode;
      shadingMode = src.shadingMode;
      minEdgeDihedral = src.minEdgeDihedral;
      surfaceOrientation = src.surfaceOrientation;
      appearance = src.appearance.cloneNodeComponent(true);
      lineAppearance = src.lineAppearance.cloneNodeComponent(true);
      annoFont = src.annoFont;
      annoFontInherited = true;
      annoColor = src.annoColor;
      annoColorInherited = true;
      color = src.color;
   }

   @Override
   public Object clone()
   {
      try
      {
         return super.clone();
      } catch (CloneNotSupportedException ex)
      {
         return null;
      }
   }

   public boolean isInherited()
   {
      return inherited;
   }

   public void setInherited(boolean inherited)
   {
      this.inherited = inherited;
   }

   @Override
   public boolean ignoreMask()
   {
      return ignoreMask;
   }

   @Override
   public void setIgnoreMask(boolean ignoreMask)
   {
      this.ignoreMask = ignoreMask;
      fireStateChanged(RenderEvent.GEOMETRY);
   }


   /**
    * Get the value of lightedBackground
    *
    * @return the value of lightedBackground
    */
   @Override
   public boolean isLightedBackside()
   {
      return lightedBackground;
   }

   /**
    * Set the value of lightedBackground
    *
    * @param lightedBackground new value of lightedBackground
    */
   @Override
   public void setLightedBackside(boolean lightedBackground)
   {
      this.lightedBackground = lightedBackground;
      fireStateChanged(RenderEvent.APPEARANCE);
   }

   /**
    * Utility field holding list of RenderEventListeners.
    */
   private transient ArrayList<RenderEventListener> renderEventListenerList =
           new ArrayList<RenderEventListener>();

   /**
    * Registers RenderEventListener to receive events.
    * @param listener The listener to register.
    */
   @Override
   public synchronized void addRenderEventListener(RenderEventListener listener)
   {
      if(!renderEventListenerList.contains(listener))
        renderEventListenerList.add(listener);
   }

   /**
    * Removes RenderEventListener from the list of listeners.
    * @param listener The listener to remove.
    */
   @Override
   public synchronized void removeRenderEventListener(RenderEventListener listener)
   {
      renderEventListenerList.remove(listener);
   }

   /**
    * Notifies all registered listeners about the event.
    *
    */
   @Override
   public void fireStateChanged(int change)
   {
      RenderEvent e = new RenderEvent(this, change);
       for (int i = 0; i < renderEventListenerList.size(); i++) {
         renderEventListenerList.get(i).renderExtentChanged(e);
       }
   }

   @Override
   public void setParentParams(AbstractRenderingParams parentParams)
   {
      //throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public float getSurfaceOffset()
   {
      return surfaceOffset;
   }

   @Override
   public void setSurfaceOffset(float offset)
   {
      boolean detach = detachUserData(appearance.getUserData()); 
      surfaceOffset = offset;
      if (appearance != null && appearance.getPolygonAttributes() != null)
      {
         appearance.getPolygonAttributes().setPolygonOffset(offset);
         fireStateChanged(RenderEvent.GEOMETRY);
      }
      if(detach) attachUserData(appearance.getUserData());
   }
   
   private boolean detachUserData(Object obj) {      
       if (obj != null) {          
          if(obj instanceof OpenBranchGroup) {
              return ((OpenBranchGroup)obj).postdetach();
          } else if(obj instanceof GeometryObject) {
              return ((GeometryObject)obj).getGeometryObj().postdetach();
          } else if(obj instanceof RegularFieldGeometry) {
              return ((RegularFieldGeometry)obj).getGeometryObject().postdetach();              
          }          
      }
      return false;
   }

   private void attachUserData(Object obj) {
      if (obj != null) {          
          if(obj instanceof OpenBranchGroup) {
              ((OpenBranchGroup)obj).postattach();
          } else if(obj instanceof GeometryObject) {
              ((GeometryObject)obj).getGeometryObj().postattach();
          } else if(obj instanceof RegularFieldGeometry) {
              ((RegularFieldGeometry)obj).getGeometryObject().postattach();
          }          
      }
   }

   public RenderEventListener getTransparencyChangeListener()
   {
      return transparencyChangeListener;
   }

   public void setActive(boolean active)
   {
      this.active = active;
//      if (active)
//         fireStateChanged(RenderEvent.GEOMETRY);
   }
   
}
