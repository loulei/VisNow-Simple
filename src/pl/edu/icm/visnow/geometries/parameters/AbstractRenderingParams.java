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

package pl.edu.icm.visnow.geometries.parameters;

import java.awt.Color;
import java.awt.Font;
import javax.vecmath.Color3f;
import pl.edu.icm.visnow.geometries.objects.generics.OpenAppearance;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.render.RenderEventListener;

/**
 *
 * @author Krzysztof S. Nowinski
 *   <p>   University of Warsaw, ICM
 */

public interface AbstractRenderingParams
{
   public static final int OUTLINE_BOX = 32;
   public static final int SURFACE = 1;
   public static final int EDGES = 2;
   public static final int EXTEDGES = 4;
   public static final int NODES = 8;
   public static final int IMAGE = 16;
   public static final int FLAT_SHADED = 1;
   public static final int GOURAUD_SHADED = 2;
   public static final int UNSHADED = 3;
   public static final int BACKGROUND = 4;
   public static final int INHERIT = -1;


   /**
    * Getter for property ambientColor.
    * @return Value of property ambientColor.
    */
   Color3f getAmbientColor();

   /**
    * Getter for property annoColor.
    * @return Value of property annoColor.
    */
   Color getAnnoColor();

   /**
    * Getter for property annoFont.
    * @return Value of property annoFont.
    */
   Font getAnnoFont();

   OpenAppearance getAppearance();
   OpenAppearance getLineAppearance();


   /**
    * Getter for property color.
    * @return Value of property color.
    */
   Color getColor();

   Color3f getColorf();

   /**
    * Getter for property diffuseColor.
    * @return Value of property diffuseColor.
    */
   Color3f getDiffuseColor();

   int getDisplayMode();

   int getLineStyle();

   /**
    * Getter for property lineThickness.
    * @return Value of property lineThickness.
    */
   float getLineThickness();

   /**
    * Getter for property shininess.
    * @return Value of property shininess.
    */
   float getShininess();

   /**
    * Getter for property specularColor.
    * @return Value of property specularColor.
    */
   Color3f getSpecularColor();

   /**
    * Getter for property transparency.
    * @return Value of property transparency.
    */
   float getTransparency();

   public float getMinEdgeDihedral();

   public void setMinEdgeDihedral(float minEdgeDihedral);
   
   public float getPointSize();
   
   public void setPointSize(float pointSize);

   /**
    * Setter for property ambientColor.
    * @param ambientColor New value of property fColor.
    */
   void setAmbientColor(Color3f ambientColor);

   /**
    * Setter for property annoColor.
    * @param annoColor New value of property annoColor.
    */
   void setAnnoColor(Color annoColor);

   /**
    * Setter for property annoFont.
    * @param annoFont New value of property annoFont.
    */
   void setAnnoFont(Font annoFont);

   void setAppearance(OpenAppearance appearance);

   void setLineAppearance(OpenAppearance appearance);

   /**
    * Setter for property color.
    * @param color New value of property color.
    */
   void setColor(Color color);

   void setColorf(Color3f colorf);

   /**
    * Setter for property diffuseColor.
    * @param diffuseColor New value of property fColor.
    */
   void setDiffuseColor(Color3f diffuseColor);

   void setDisplayMode(int displayMode);

   void setLineStyle(int lineStyle);
   
   boolean isLineLighting();
   
   void setLineLighting(boolean lineLighting);
   
   boolean ignoreMask();
   
   void setIgnoreMask(boolean ignoreMask);

   /**
    * Setter for property lineThickness.
    * @param lineThickness New value of property lineThickness.
    */
   void setLineThickness(float lineThickness);

   /**
    * Setter for property parentParams.
    * @param parentParams New value of property parentParams.
    */
   void setParentParams(AbstractRenderingParams parentParams);

   /**
    * Setter for property shininess.
    * @param shininess New value of property shininess.
    */
   void setShininess(float shininess);

   /**
    * Setter for property specularColor.
    * @param specularColor New value of property fColor.
    */
   void setSpecularColor(Color3f specularColor);

   Color getBackgroundColor();

   void setBackgroundColor(Color backgroundColor);

   /**
    * Setter for property transparency.
    * @param transparency New value of property transparency.
    */
   void setTransparency(float transparency);
   void setCullMode(int cullMode);

   public int getShadingMode();
   public void setShadingMode(int shadingMode);

   public boolean getSurfaceOrientation();

   public void setSurfaceOrientation(boolean orientation);

   public float getSurfaceOffset();

   public void setSurfaceOffset(float offset);
   
   public void setActive(boolean active);
   
   public void setLightedBackside(boolean lightedBg);
   
   public boolean isLightedBackside();
   
   public RenderEventListener getTransparencyChangeListener();

   public void addRenderEventListener(RenderEventListener listener);

   /**
    * Removes RenderEventListener from the list of listeners.
    * @param listener The listener to remove.
    */
   public void removeRenderEventListener(RenderEventListener listener);

   /**
    * Notifies all registered listeners about the event.
    *
    * @param object Parameter #1 of the <CODE>ChangeEvent<CODE> constructor.
    */
   public void fireStateChanged(int change);


}
