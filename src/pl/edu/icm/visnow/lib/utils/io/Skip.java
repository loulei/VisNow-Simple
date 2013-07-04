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

import java.io.LineNumberReader;
import java.util.Scanner;
import javax.imageio.stream.ImageInputStream;

/**
 *
 * @author know
 */
public class Skip
{

   private Skip()
   {
   }

   public static void skip(SkipSchema schema, ImageInputStream in)
   {
      try
      {
         switch (schema.getType())
         {
         case SkipSchema.COUNT:
            in.skipBytes(schema.getCount());
            return;
         default:
            String line;
            do
            {
               line = in.readLine();
               if (line.matches(schema.getPattern()))
                  return;
            } while (line != null);
         }
      } catch (Exception e)
      {
      }
   }
   
   public static void skip(SkipSchema schema, LineNumberReader in)
   {
      String line;
      int lineNumber;
      try
      {
         switch (schema.getType())
         {
         case SkipSchema.COUNT:
            for (int i = 0; i < schema.getCount(); i++)
               in.readLine();
            return;
         case SkipSchema.PATTERN:
            do
            {
               lineNumber = in.getLineNumber();
               line = in.readLine();
               if (line.matches(schema.getPattern()))
               {
                  in.setLineNumber(lineNumber);
                  return;
               }
            } while (line != null);
         default:
            do
            {
               line = in.readLine();
               if (line.matches(schema.getPattern()))
                  return;
            } while (line != null);
         }
      } catch (Exception e)
      {
      }
   }
   
   public static void skip(SkipSchema schema, Scanner in)
   {
      String line;
      int lineNumber;
      try
      {
         switch (schema.getType())
         {
         case SkipSchema.COUNT:
            for (int i = 0; i < schema.getCount(); i++)
               in.nextLine();
            return;
         default:
            in.findInLine(schema.getPattern());
            in.nextLine();
         }
      } catch (Exception e)
      {
      }
   }
   
   public static void skip(SkipSchema schema, Object in)
   {
      if (in instanceof Scanner)
         skip (schema, (Scanner)in);
      else if (in instanceof LineNumberReader)
         skip (schema, (LineNumberReader)in);
      else if (in instanceof ImageInputStream)
         skip (schema, (ImageInputStream)in);
   }
}
