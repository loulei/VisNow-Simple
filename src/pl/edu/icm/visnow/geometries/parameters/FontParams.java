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
import java.util.ArrayList;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.Color3f;
import pl.edu.icm.visnow.geometries.utils.transform.LocalToWindow;

/**
 *
 * @author know
 */
public class FontParams
{
   private boolean threeDimensional = false;
   private float size = .02f;                    //size of text glyps relative to the window size (2D) or field diameter (3d)
   private float precision = 3;                  //precision of font model used for 3d fonts
   private float font3DSize = 1;
   private String fontName = "sans-serif";
   private int fontSize = 12;                     //font size in pixels for 2d labels
   private int fontType = Font.PLAIN;
   private Color color = Color.WHITE;
   private float colorCorrection = 1;
   
   
   public void createFontMetrics(LocalToWindow localToWindow, int w, int h)
   {
      fontSize = Math.max(5, (int)(h * size));
      float z = localToWindow.transformPt(new double[] {0,0,0}, new int[2]);
      float[] xl = localToWindow.reverseTransformPt(w / 2, (h - fontSize) / 2, z);
      float[] xu = localToWindow.reverseTransformPt(w / 2, (h + fontSize) / 2, z);
      font3DSize = (float)(Math.sqrt((xu[0] - xl[0]) * (xu[0] - xl[0]) + 
                                     (xu[1] - xl[1]) * (xu[1] - xl[1]) + 
                                     (xu[2] - xl[2]) * (xu[2] - xl[2])));
   }

   /**
    * Get the value of color
    *
    * @return the value of color
    */
   public Color getColor()
   {
      return color;
   }
   
   public Color3f getColor3f()
   {
      return new Color3f(color);
   }

   /**
    * Set the value of color
    *
    * @param color new value of color
    */
   public void setColor(Color color)
   {
      this.color = color;
      fireStateChanged();
   }

   /**
    * Set the value of fontType
    *
    * @param fontType new value of fontType
    */
   public void setFontType(int fontType)
   {
      this.fontType = fontType;
      fireStateChanged();
   }
   
   /**
    * Get the value of font2D
    *
    * @return the value of font2D
    */
   public Font getFont2D()
   {
      return new Font(fontName, fontType, fontSize);
   }

   public String getFontName()
   {
      return fontName;
   }

   public int getFontSize()
   {
      return (int)(threeDimensional ? precision * fontSize : fontSize);
   }

   public int getFontType()
   {
      return fontType;
   }

   public float getPrecision()
   {
      return precision;
   }

   public float getFont3DSize()
   {
      return font3DSize;
   }
   
   /**
    * Set the value of fontName
    *
    * @param fontName new value of fontName
    */
   public void setFontName(String fontName)
   {
      this.fontName = fontName;
      fireStateChanged();
   }

   /**
    * Set the value of precision
    *
    * @param precision new value of precision
    */
   public void setPrecision(float precision)
   {
      this.precision = precision;
      fireStateChanged();
   }

   public float getSize()
   {
      return size;
   }

   /**
    * Set the value of size
    *
    * @param size new value of size
    */
   public void setSize(float size)
   {
      this.size = size;
      fireStateChanged();
   }

   /**
    * Get the value of threeDimensional
    *
    * @return the value of threeDimensional
    */
   public boolean isThreeDimensional()
   {
      return threeDimensional;
   }

   /**
    * Set the value of threeDimensional
    *
    * @param threeDimensional new value of threeDimensional
    */
   public void setThreeDimensional(boolean threeDimensional)
   {
      this.threeDimensional = threeDimensional;
      fireStateChanged();
   }

   public void setBold(boolean bold)
   {
      if (bold) fontType |= Font.BOLD;
      else      fontType &= ~Font.BOLD;
      fireStateChanged();
   }
   
   public void setItalic(boolean italic)
   {
      if (italic) fontType |= Font.ITALIC;
      else        fontType &= ~Font.ITALIC;
      fireStateChanged();
   }

   public void setValues(Font font, boolean threeDimensional, float size, float precision)
   {
      this.threeDimensional = threeDimensional;
      this.size = size;
      this.precision = precision;
   }

   public float getColorCorrection()
   {
      return colorCorrection;
   }

   public void setColorCorrection(float colorCorrection)
   {
      this.colorCorrection = colorCorrection;
      fireStateChanged();
   }

    /**
    * Utility field holding list of ChangeListeners.
    */
   protected transient ArrayList<ChangeListener> changeListenerList = new ArrayList<ChangeListener>();

   /**
    * Registers ChangeListener to receive events.
    * @param listener The listener to register.
    */
   public synchronized void addChangeListener(ChangeListener listener)
   {
      changeListenerList.add(listener);
   }

   /**
    * Removes ChangeListener from the list of listeners.
    * @param listener The listener to remove.
    */
   public synchronized void removeChangeListener(ChangeListener listener)
   {
      changeListenerList.remove(listener);
   }

   /**
    * Notifies all registered listeners about the event.
    *
    * @param object Parameter #1 of the <CODE>ChangeEvent<CODE> constructor.
    */
   public void fireStateChanged()
   {
      ChangeEvent e = new ChangeEvent(this);
      for (int i = 0; i < changeListenerList.size(); i++) {
          changeListenerList.get(i).stateChanged(e);          
       }
   }

}
