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

package pl.edu.icm.visnow.lib.basic.readers.ReadVisNowField;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import pl.edu.icm.visnow.gui.widgets.FileErrorFrame;
import pl.edu.icm.visnow.lib.utils.io.FieldIOSchema;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class HeaderParser
{

   private LineNumberReader r;
   private String fileName;
   private File headerFile;
   private FileErrorFrame errorFrame = null;
   private String line;

   public HeaderParser(String fileName, boolean isURL, FileErrorFrame errorFrame) throws IOException
   {
      this.fileName = fileName;
      if (isURL)
      {
         URL url = new URL(fileName);
         URLConnection urlConnection = url.openConnection();
         r = new LineNumberReader(new InputStreamReader(urlConnection.getInputStream()));
      }
      else
      {
         headerFile = new File(fileName);
         r = new LineNumberReader(new FileReader(headerFile));
      }
      line = "";
      this.errorFrame = errorFrame;
   }

   public FieldIOSchema parseFieldHeader()
   {
      try
      {
         line = r.readLine().trim();
         if (line.startsWith("#VisNow regular field"))
            return new RegularFieldHeaderParser(r, headerFile, fileName, errorFrame).parseHeader();
         else if (line.startsWith("#VisNow irregular field"))
            return new IrregularFieldHeaderParser(r, headerFile, fileName, errorFrame).parseHeader();
//         else if (line.startsWith("#VisNow components"))
//            return new DataComponentsHeaderParser(r, headerFile, fileName, errorFrame).parseHeader();
      } catch (Exception e)
      {
      }
      ParsingUtils.outputError(errorFrame,"field description line should start with\"#VisNow (ir)regular field\"", fileName, r.getLineNumber(), null);
      return null;
   }


}
