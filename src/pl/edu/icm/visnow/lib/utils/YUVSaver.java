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

package pl.edu.icm.visnow.lib.utils;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;

/**
 *
 * @author Krzysztof S. Nowiński, Warsaw University ICM
 */
public class YUVSaver
{
   private int width;
   private int height;
   private String genericFileName;
   private byte[] Y = null;
   private byte[] U = null;
   private byte[] V = null;
  
   
   
   /** Creates a new instance of SaveYUV */
   public YUVSaver(int width, int height, String genericFileName)
   {
      if (width%16!=0 || height%16!=0)
      {
         System.out.println("bad frame dimensions");
         return;
      }
      this.width  = width;
      this.height = height;
      this.genericFileName = genericFileName;
      Y = new byte[width*height];
      U = new byte[width*height/4];
      V = new byte[width*height/4];
      System.out.println("h "+width+"  v "+height);
   }


   /**
    * Getter for property width.
    * @return Value of property width.
    */
   public int getWidth()
   {

      return this.width;
   }

   /**
    * Setter for property width.
    * @param width New value of property width.
    */
   public void setWidth(int width)
   {

      this.width = width;
   }


   /**
    * Getter for property height.
    * @return Value of property height.
    */
   public int getHeight()
   {
      return this.height;
   }

   /**
    * Setter for property height.
    * @param height New value of property height.
    */
   public void setHeight(int height)
   {
      this.height = height;
   }


   /**
    * Getter for property genericFileName.
    * @return Value of property genericFileName.
    */
   public String getGenericFileName()
   {

      return this.genericFileName;
   }

   /**
    * Setter for property genericFileName.
    * @param genericFileName New value of property genericFileName.
    */
   public void setGenericFileName(String genericFileName)
   {

      this.genericFileName = genericFileName;
   }

   
   public boolean saveEncoded(BufferedImage in, int current)
   {
      if (in.getHeight()!=height || in.getWidth()!=width || in.getType()!=in.TYPE_INT_RGB)
         return false;
      int[] data = in.getRGB(0, 0, width, height, null, 0, width);
      int x,y,i,j,r,g,b,t;
      double u,v;
      int mask = 255;
      for (y=i=0;y<height;y++)
         for (x=0;x<width;x++,i++)
         {
            b = data[i]&mask;
            g = (data[i]>>8)&mask;
            r = (data[i]>>16)&mask;
            t = (int)(0.2989*r+0.5866*g+0.1144*b);
            if (t<0)   t=0;
            if (t>255) t=255;
            Y[i] = (byte)t;
         }
      try
      {
         FileOutputStream out = new FileOutputStream(genericFileName+current+".Y");
         out.write(Y);
         out.close();
      }
      catch (Exception e)
      {
         System.out.println("could not write "+genericFileName+current+".Y");
         System.out.println(""+e);
         e.printStackTrace();
         return false;
      }
      for (y=i=j=0;y<height;y+=2,i+=width)
         for (x=0;x<width;x+=2,i+=2,j++)
         {
            u=v=0;
            b = data[i]&mask;
            g = (data[i]>>8)&mask;
            r = (data[i]>>16)&mask;
            u += (int)(0.493*(0.8856*b-0.5866*g- 0.2989*r));
            v += (int)(0.877*(0.7011*r-0.5866*g- 0.1144*b));
            b = data[i+1]&mask;
            g = (data[i+1]>>8)&mask;
            r = (data[i+1]>>16)&mask;
            u += (int)(0.493*(0.8856*b-0.5866*g- 0.2989*r));
            v += (int)(0.877*(0.7011*r-0.5866*g- 0.1144*b));
            b = data[i+width]&mask;
            g = (data[i+width]>>8)&mask;
            r = (data[i+width]>>16)&mask;
            u += (int)(0.493*(0.8856*b-0.5866*g- 0.2989*r));
            v += (int)(0.877*(0.7011*r-0.5866*g- 0.1144*b));
            b = data[i+width+1]&mask;
            g = (data[i+1]>>8)&mask;
            r = (data[i+width+1]>>16)&mask;
            u += (int)(0.493*(0.8856*b-0.5866*g- 0.2989*r));
            v += (int)(0.877*(0.7011*r-0.5866*g- 0.1144*b));
            t = 128+(int)u/4;
            if (t<0)   t=0;
            if (t>255) t=255;
            U[j] = (byte)t;
            t = 128+(int)v/4;
            if (t<0)   t=0;
            if (t>255) t=255;
            V[j] = (byte)t;
         }
      try
      {
         FileOutputStream out = new FileOutputStream(genericFileName+current+".U");
         out.write(U);
         out.close();
      }
      catch (Exception e)
      {
         System.out.println("could not write "+genericFileName+current+".U");
         System.out.println(""+e);
         e.printStackTrace();
         return false;
      }
      try
      {
         FileOutputStream out = new FileOutputStream(genericFileName+current+".V");
         out.write(V);
         out.close();
      }
      catch (Exception e)
      {
         System.out.println("could not write "+genericFileName+current+".V");
         System.out.println(""+e);
         e.printStackTrace();
         return false;
      }
      return true;
   }
}
