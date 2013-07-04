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

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import org.apache.log4j.Logger;
import pl.edu.icm.visnow.system.main.VisNow;

/**
 *
 * @author Krzysztof S. Nowinski
 * University of Warsaw, ICM
 */
public class VisNowCallTrace
{
   static Logger logger = Logger.getLogger(VisNowCallTrace.class);

   public static void trace()
   {
//      if (VisNow.isDebug())
         try
         {
            StringWriter sw = new StringWriter();
            PrintWriter w = new PrintWriter(sw);
            new Exception().printStackTrace(w);
            String s = sw.toString();
            BufferedReader r = new BufferedReader(new StringReader(s));
            String line = " ";
            do
            {
               line = r.readLine();
               if (line == null)
                  break;
               if (line.indexOf("VisNowCallTrace") >= 0)
                  continue;
               int k = line.indexOf("visnow");
               if (k >= 0)
                  System.out.println(line.substring(k));
            } while (line != null);
            System.out.println("");
         } catch (Exception e)
         {
         }
   }

   public static void traceLog(int level)
   {
      if (VisNow.isDebug())
         try
         {
            StringWriter sw = new StringWriter();
            PrintWriter w = new PrintWriter(sw);
            new Exception().printStackTrace(w);
            String s = sw.toString();
            BufferedReader r = new BufferedReader(new StringReader(s));
            StringBuilder out = new StringBuilder();
            String line = " ";
            do
            {
               line = r.readLine();
               if (line == null)
                  break;
               if (line.indexOf("VisNowCallTrace") >= 0)
                  continue;
               int k = line.indexOf("visnow");
               if (k >= 0)
                  out.append("\n");
                  out.append(line);
            } while (line != null);
            logger.debug(out.toString());
         } catch (Exception e)
         {
         }
   }

   private VisNowCallTrace()
   {
   }
}
