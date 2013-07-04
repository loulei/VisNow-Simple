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
public class TimestepSchema extends FilePartSchema
{
   protected Vector<FilePartSchema> sections = new Vector<FilePartSchema>();
   protected float time = 0;
   protected float dt = 1;
   protected int repeat = 1;

   public int getRepeat()
   {
      return repeat;
   }

   public void setRepeat(int repeat)
   {
      this.repeat = repeat;
   }

   public float getTime()
   {
      return time;
   }

   public void setTime(float time)
   {
      this.time = time;
   }
   
   public float getDt()
   {
      return dt;
   }

   public void setDt(float dt)
   {
      this.dt = dt;
   }
   
   public Vector<FilePartSchema> getSections()
   {
      return sections;
   }

   public void setSections(Vector<FilePartSchema> sections)
   {
      this.sections = sections;
   }
   
   public int getNSections()
   {
      return sections.size();
   }
   
   public FilePartSchema getSection(int i)
   {
      if (i < 0 || i >= sections.size())
         return null;
      return sections.get(i);
   }
   
   public void addSection(FilePartSchema s)
   {
      sections.add(s);
   }

   
   public String toString()
   {
      if (repeat < 0)
         return "indefinitely repeatable timestep";
      else if (repeat > 1)
         return "timestep " + repeat + " times";
      else 
         return "timestep " + time;
   }
   
   public String[] getDescription(boolean t)
   {
      String[] desc = new String[sections.size() + 1];
      desc[0] = toString();
      for (int i = 0; i < sections.size(); i++)
         desc[i + 1] = sections.get(i).toString(t);
      return desc;
   }

   @Override
   String toString(boolean t)
   {
      StringBuffer b = new StringBuffer();
      String[] desc = getDescription(t);
      for (int i = 0; i < desc.length; i++) 
         b.append(desc[i] + "  ");
      return b.toString();
   }
}
