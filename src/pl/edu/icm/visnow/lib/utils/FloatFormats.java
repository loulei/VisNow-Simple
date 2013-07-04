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

/**
 *
 * @author know
 */
public class FloatFormats
{
   private FloatFormats(){}
   
   public static String eformat(int m)
   {
      String form;
      if (m >= 0 && m < 6)  form = "%" + (m + 1) + "." + 0 + "f";
      else if (m >= 0)      form = "%2.0e";
      else if (m > -5)      form = "%" + (-m + 1) + "." + (-m) + "f";
      else                  form = "%2.0e";
      return form;
   }
   
   public static String xeformat(int m)
   {
      String form;
      if (m >= 0 && m < 5)  form = "%7." + (5-m) + "f";
      else if (m >= 0)      form = "%5.3e";
      else if (m > -5)      form = "%7.5f";
      else                  form = "%5.3e";
      return form;
   }
   
   public static String format(float x)
   {
      return eformat((int) (Math.log10(x) + 100.1) - 100);
   }
   
   public static String xformat(float x)
   {
      return xeformat((int) (Math.log10(x) + 100.1) - 100);
   }
}
