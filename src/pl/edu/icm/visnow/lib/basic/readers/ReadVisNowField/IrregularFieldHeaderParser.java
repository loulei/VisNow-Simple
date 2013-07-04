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

import java.io.File;
import java.io.IOException;
import java.io.LineNumberReader;
import pl.edu.icm.visnow.datasets.IrregularField;
import pl.edu.icm.visnow.gui.widgets.FileErrorFrame;
import pl.edu.icm.visnow.lib.utils.io.IrregularFieldIOSchema;

/**
 *
 * @author know
 */
public class IrregularFieldHeaderParser
{
   
   public static final int EMPTY = -1;
   public static final int ACCEPTED = 0;
   public static final int BREAK = 1;
   public static final int UNKNOWN = 2;
   public static final int EOF = 3;
   public static final int ERROR = 4;
   private String[] entries;
   private String[][] res;
   private String line;
   private String lineIn;
   String[] stringsInLine;
   private LineNumberReader r;
   private IrregularField field;
   private String[] names;
   private int[] types;
   private int[] vlens;
   private String fileName;
   private File headerFile;
   private FileErrorFrame errorFrame = null;
   private int[][][] tileBds = null;
   private boolean extentsRead = false, affineRead = false, tiles = false, parsingTimestep = false;

   public IrregularFieldHeaderParser(LineNumberReader r, File headerFile, String fileName, FileErrorFrame errorFrame)
   {
      this.r = r;
      this.headerFile = headerFile;
      this.fileName = fileName;
      this.errorFrame = errorFrame;
   }
   
   
   private void outputError(String text, String fname, int lineNumber, Exception e) {
       if(this.errorFrame == null) {
           System.err.println("ERROR: "+text+"; in function "+fname+" line "+lineNumber);
       } else {
           errorFrame.setErrorData(text, fname, lineNumber, e);
       }
       
   }

   private void nextLine()
   {
      lineIn = "";
      try
      {
         while (lineIn != null && (lineIn.isEmpty() || lineIn.startsWith("#")))
            lineIn = r.readLine();
      } catch (IOException ex)
      {
      }
   }

   public int processLine(String[] acceptStrings, String[] endStrings)
   {
      if (lineIn == null)
         return EOF;
      if (lineIn.isEmpty() || lineIn.startsWith("#"))
         return EMPTY;
      line = lineIn.trim();
      stringsInLine = line.split("\"");
      if (stringsInLine.length >= 2)
      {
         for (int i = 1, j = 0; i < stringsInLine.length; i += 2, j++)
         {
            stringsInLine[j] = stringsInLine[i];
            line = line.replaceFirst("\"[^\"]*\"", "__" + j);
         }
      }
      line = line.replaceFirst("#.*", "");
      if (line.isEmpty())
         return EMPTY;
      for (String s : endStrings)
         if (line.startsWith(s))
            return BREAK;
      String ll = line.replaceFirst("\"", "");
      for (String s : acceptStrings)
         if (ll.toLowerCase().startsWith(s.toLowerCase()))
         {
            entries = line.split(" *, *");
            if (entries == null || entries.length < 1)
               return EMPTY;
            res = new String[entries.length][];
            for (int i = 0; i < entries.length; i++)
            {
               res[i] = entries[i].split("[= ]+");
               res[i][0] = res[i][0].toLowerCase();
            }
            return ACCEPTED;
         }
      return UNKNOWN;
   }

   public IrregularFieldIOSchema parseHeader()
   {
      IrregularFieldIOSchema schema = null;
      return schema;
   }
}
