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

package pl.edu.icm.visnow.lib.utils.io;

import java.util.Vector;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class FileSectionSchema extends FilePartSchema
{
   static int[] bytes = new int[]
      {
         0, 1, 2, 4, 4, 8
      };

   protected int stride;
   protected Vector<ComponentIOSchema> components;
   protected boolean simpleComponent = false;
   protected int component = -999;
   protected String cmpName = null;
   protected String separator;
   protected int[][] tile;
   protected int tileSize = -1;

   public FileSectionSchema(int stride, Vector<ComponentIOSchema> components, int[] vlens, String separator)
   {
      this.stride = stride;{
      this.separator = separator;
      this.components = components;
      if (components != null && !components.isEmpty())
      
         simpleComponent = components.size() == 1 && components.get(0).getCoord() == -1
            && stride == vlens[components.get(0).getComponent()] * bytes[components.get(0).getType()];
         if (simpleComponent)
         {
            component = components.get(0).getComponent();
            cmpName   = components.get(0).getCmpName();
         }        
      }
   }

   @Override
   public String toString(boolean binary)
   {
      if (binary)
      {
         StringBuilder s = new StringBuilder(String.format("stride = %5d, ", stride));
         if (tile != null)
         {
            s.append("tile [");
            for (int i = 0; i < tile.length; i++)
               s.append(tile[i][0] + "-" + tile[i][1] + (i == tile.length - 1 ? "]   " : ","));
         }
         if (components == null || components.isEmpty())
            return s.toString();
         if (simpleComponent)
         {
            if (cmpName != null)
               s.append(cmpName);
            else
               s.append(component);
         } else
            for (int i = 0; i < components.size(); i++)
               s.append(components.get(i).toString());
         return s.toString();
      }
      StringBuilder s = new StringBuilder(String.format("stride = %5d, separator = \"%4s\",", stride, separator));
      if (tile != null)
      {
         s.append("tile [");
         for (int i = 0; i < tile.length; i++)
            s.append(tile[i][0] + "-" + tile[i][1] + (i == tile.length - 1 ? "]   " : ","));
      }
      if (components == null || components.isEmpty())
         return s.toString();
      if (simpleComponent)
      {
         if (cmpName != null)
            s.append(cmpName);
         else
            s.append(component);
      } else
         for (int i = 0; i < components.size(); i++)
            s.append(components.get(i).toString());
      return s.toString();
   }

   /**
    * Get the value of tile
    *
    * @return the value of tile
    */
   public int[][] getTile()
   {
      return tile;
   }

   /**
    * Set the value of tile
    *
    * @param tile new value of tile
    */
   public void setTile(int[][] tile)
   {
      this.tile = tile;
      if (tile == null)
         return;
      tileSize = 1;
      for (int n = 0; n < tile.length; n++)
         tileSize *= tile[n][1] - tile[n][0] + 1;
   }

   public int getTileSize()
   {
      return tileSize;
   }

   /**
    * Get the value of separator
    *
    * @return the value of separator
    */
   public String getSeparator()
   {
      return separator;
   }

   /**
    * Set the value of separator
    *
    * @param separator new value of separator
    */
   public void setSeparator(String separator)
   {
      this.separator = separator;
   }

   /**
    * Get the value of components
    *
    * @return the value of components
    */
   public Vector<ComponentIOSchema> getComponents()
   {
      return components;
   }

   /**
    * Set the value of components
    *
    * @param components new value of components
    */
   public void setComponents(Vector<ComponentIOSchema> components)
   {
      this.components = components;
   }

   public int getNComponents()
   {
      return components.size();
   }

   /**
    * Get the value of components at specified index
    *
    * @param index
    * @return the value of components at specified index
    */
   public ComponentIOSchema getComponent(int index)
   {
      return components.get(index);
   }

   /**
    * Set the value of components at specified index.
    *
    * @param index
    * @param newComponents new value of components at specified index
    */
   public void setComponent(int index, ComponentIOSchema newComponent)
   {
      components.setElementAt(newComponent, index);
   }

   public void addComponent(ComponentIOSchema newComponent)
   {
      components.add(newComponent);
   }

   public String getCmpName()
   {
      return cmpName;
   }

   public void setCmpName(String cmpName)
   {
      this.cmpName = cmpName;
   }

   /**
    * Get the value of stride
    *
    * @return the value of stride
    */
   public int getStride()
   {
      return stride;
   }

   /**
    * Set the value of stride
    *
    * @param stride new value of stride
    */
   public void setStride(int stride)
   {
      this.stride = stride;
   }
   
   public int getComponent()
   {
      return component;
   }

   public void setComponent(int component)
   {
      this.component = component;
   }
/**
 * 
 * @return true if the section contains a continuous image of a single component data array,
 * 
 */
   public boolean isSingleComponent()
   {
      return simpleComponent;
   }

}
