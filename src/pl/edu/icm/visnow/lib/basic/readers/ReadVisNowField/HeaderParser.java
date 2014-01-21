//<editor-fold defaultstate="collapsed" desc=" COPYRIGHT AND LICENSE ">
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
exception statement from your version.
*/
//</editor-fold>

package pl.edu.icm.visnow.lib.basic.readers.ReadVisNowField;

import java.io.File;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Vector;
import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.datasets.dataarrays.ByteDataArray;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.datasets.dataarrays.DataArraySchema;
import pl.edu.icm.visnow.datasets.dataarrays.DoubleDataArray;
import pl.edu.icm.visnow.datasets.dataarrays.FloatDataArray;
import pl.edu.icm.visnow.datasets.dataarrays.IntDataArray;
import pl.edu.icm.visnow.datasets.dataarrays.ShortDataArray;
import pl.edu.icm.visnow.datasets.dataarrays.StringDataArray;
import pl.edu.icm.visnow.gui.widgets.FileErrorFrame;
import pl.edu.icm.visnow.lib.basic.readers.ReadVisNowField.utils.ComponentIOSchema;
import pl.edu.icm.visnow.lib.basic.readers.ReadVisNowField.utils.DataElementIOSchema;
import pl.edu.icm.visnow.lib.basic.readers.ReadVisNowField.utils.DataFileSchema;
import pl.edu.icm.visnow.lib.basic.readers.ReadVisNowField.utils.FilePartSchema;
import pl.edu.icm.visnow.lib.basic.readers.ReadVisNowField.utils.SkipSchema;
import pl.edu.icm.visnow.lib.basic.readers.ReadVisNowField.utils.TimestepSchema;


/**
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */

abstract public class HeaderParser
{
   public static final int EMPTY = -1;
   public static final int ACCEPTED = 0;
   public static final int BREAK = 1;
   public static final int UNKNOWN = 2;
   public static final int EOF = 3;
   public static final int ERROR = 4;
   protected String[] entries;
   protected String line;
   String[] stringsInLine;
   protected LineNumberReader r;
   protected Field field;
   protected String[] names;
   protected int[] types;
   protected int[] vlens;
   protected String fileName;
   protected File headerFile;
   protected FileErrorFrame errorFrame = null;
   protected boolean parsingTimestep = false;

   public HeaderParser(LineNumberReader r, File headerFile, String fileName, FileErrorFrame errorFrame)
   {
      this.r = r;
      this.headerFile = headerFile;
      this.fileName = fileName;
      this.errorFrame = errorFrame;
   }
   
   protected void outputError(String text, String fname, int lineNumber, Exception e) {
       if(errorFrame == null) {
           System.err.println("ERROR: "+text+"; in function "+fname+" line "+lineNumber);
       } else {
           errorFrame.setErrorData(text, fname, lineNumber, e);
       }
   }

   protected String nextLine()
   {
      String lineIn = "";
      try
      {
         while (lineIn != null && (lineIn.isEmpty() || lineIn.startsWith("#")))
            lineIn = r.readLine();
      } catch (IOException ex)
      {
      }
      return lineIn;
   }

   public int processLine(String lineIn, String[] acceptStrings, String[] endStrings, Vector<String[]> result)
   {
      if (result == null)
         return ERROR;
      if (lineIn == null)
         return EOF;
      if (lineIn.isEmpty() || lineIn.startsWith("#"))
         return EMPTY;
      for (String s : endStrings)
         if (lineIn.startsWith(s))
            return BREAK;
      Vector<String> substrings = new Vector<String>();
      lineIn = findSubstrings(lineIn.trim(), substrings).replaceFirst("#.*", "");
      String ll = lineIn.replaceFirst("\"", "");
      for (String s : acceptStrings)
         if (ll.toLowerCase().startsWith(s.toLowerCase()))
         {
            splitLine(ll, substrings, result);
            return ACCEPTED;
         }
      return UNKNOWN;
   }
   
   /**
    * finds all quote enclosed substrings in processedLine[0] and replaces each such 
    * substring by __n, where n is its number
    * @param processedLine - size 1 array containing processed string; on exit, 
    * processedLine[0] has all quota enclosed substring replaced by __0, __1,...
    * @param substrings vector of substrings found in the processed string
    * @return processedLine after substitution
    */
   public static String  findSubstrings(String processedLine, Vector<String>substrings)
   {
      String[] stringsInLine = processedLine.split("\"");
      substrings.clear();
      if (stringsInLine.length >= 2)
      {
         for (int i = 1; i < stringsInLine.length; i += 2)
         {
            substrings.add(stringsInLine[i]);
            processedLine = processedLine.replaceFirst("\"[^\"]*\"", "__" + (i / 2
                    ));
         }
      }
      return processedLine;
   }
   
   
   /**
    * splits line into comma separated sections and splits each section into space separated tokens
    * @param line processed line
    * @param substrings vector of substrings substituted by __n in the string line
    * @param result vector of line sections split into tokens
    */
   public static void splitLine(String line, Vector<String> substrings, Vector<String[]> result)
   {
      String[] entries = line.split("\\s*,\\s*");
      if (entries == null || entries.length < 1)
         return;
      result.clear();
      for (String entry : entries)
      {
         String[] res = entry.split("[=\\s]+");
         res[0] = res[0].toLowerCase();
         for (int i = 0; i < res.length; i++)
            if (res[i].startsWith("__"))
               try
               {
                  int k = Integer.parseInt(res[i].substring(2));
                  res[i] = substrings.get(k);
               }catch (NumberFormatException e)
               {
               }
         result.add(res);
      }
   }
   
   protected  DataArray parseComponentEntry(String line, int nNodes, String fileName, FileErrorFrame errorFrame, int lineNumber)
   {
      DataArray dataArr = null;
      Vector<String[]> res = new Vector<String[]>();
      int result = processLine(line, new String[]{"comp", "cmp"}, new String[]{"file","cell"}, res);
      if (result != ACCEPTED)
         return dataArr;
      String[][] typeNames = {{"bool", "log"},
                              {"byte", "char", "unsigned char"},
                              {"short"},
                              {"int"},
                              {"float", "real"},
                              {"double"},
                              {"complex"},
                              {"string"}};
      String name = res.get(0)[1];
      int type = DataArray.FIELD_DATA_UNKNOWN;
      if (res.get(0).length > 2)
      {
         parse_type:
         for (int j = 0; j < typeNames.length; j++)
            for (String item : typeNames[j])
               if (res.get(0)[2].startsWith(item))
               {
                  type = j;
                  break parse_type;
               }
      } else
      {
         outputError(errorFrame, "data type specification missing ", fileName, lineNumber, null);
         return null;
      }
      String unit = null;
      int veclen = 1;
      int[] dims = new int[] {1};
      boolean symmetric = false;
      boolean isMin = false, isMax = false, isPhysMin = false, isPhysMax = false;
      float   min = 0,       max = 1,       physMin = 0,       physMax = 1;
      String[] userData = null;
      for (int i = 1; i < res.size(); i++)
      {
         String[] strings = res.get(i);
         dims[0] = -1;
         symmetric = true;
         try
         {
            if (strings[0].startsWith("vec") || strings[0].startsWith("vlen") && strings.length > 1)
               veclen = Integer.parseInt(strings[1]);
            if (strings[0].startsWith("arr") && strings.length > 1)
            {
               int d = strings.length - 1;
               if (strings[1].startsWith("sym") && d >= 2)
               {

                  symmetric = true;
                  dims = new int[2];
                  dims[0] = dims[1] = Integer.parseInt(strings[2]);
                  veclen = (dims[0] * (dims[0] + 1)) / 2;
               }
               else
               {
                  int vl = 1;
                  dims = new int[d];
                  for (int j = 0; j < d; j++)
                  {
                     dims[j] = Integer.parseInt(strings[j + 1]);
                     vl *= dims[j];
                  }
                  veclen = vl;
               }
            }

            if (strings[0].startsWith("unit") && strings.length > 1)
               unit = strings[1].trim();
            if (strings[0].startsWith("type") && strings.length > 1)
            {
               String val = strings[1].toLowerCase();
               parse:
               for (int j = 0; j < typeNames.length; j++)
                  for (String item : typeNames[j])
                     if (val.startsWith(item))
                     {
                        type = j;
                        break parse;
                     }
            }
            if (strings[0].startsWith("user") && strings.length > 1)
            {
               userData = strings[1].trim().split(" *; *");
            }
            if (strings[0].startsWith("phys_min") && strings.length > 1)
            {
               isPhysMin = true;
               physMin = Float.parseFloat(strings[1]);
            }
            if (strings[0].startsWith("phys_max") && strings.length > 1)
            {
               isPhysMax = true;
               physMax = Float.parseFloat(strings[1]);
            }
            if (strings[0].startsWith("min") && strings.length > 1)
            {
               isMin = true;
               min = Float.parseFloat(strings[1]);
            }
            if (strings[0].startsWith("max") && strings.length > 1)
            {
               isMax = true;
               max = Float.parseFloat(strings[1]);
            }
         } catch (NumberFormatException e)
         {
            System.out.println("error in line: " + lineNumber);
         }
      }

      DataArraySchema schema = new DataArraySchema(name, unit, userData, type, veclen);

      switch (type)
      {
      case DataArray.FIELD_DATA_BYTE:
         dataArr = new ByteDataArray(schema, nNodes);
         break;
      case DataArray.FIELD_DATA_SHORT:
         dataArr = new ShortDataArray(schema, nNodes);
         break;
      case DataArray.FIELD_DATA_INT:
         dataArr = new IntDataArray(schema, nNodes);
         break;
      case DataArray.FIELD_DATA_FLOAT:
         dataArr = new FloatDataArray(schema, nNodes);
         break;
      case DataArray.FIELD_DATA_DOUBLE:
         dataArr = new DoubleDataArray(schema, nNodes);
         break;
      case DataArray.FIELD_DATA_STRING:
         dataArr = new StringDataArray(schema, nNodes);
         break;
      default:
         outputError(errorFrame, "data type not specified", fileName, lineNumber, null);
         return null;
      }
      if (dataArr == null)
         return null;
      if (isMin)
         dataArr.setMinv(min);
      if (isMax)
         dataArr.setMaxv(max);
      if (isPhysMin)
         dataArr.setPhysMin(physMin);
      if (isPhysMax)
         dataArr.setPhysMax(physMax);
      dataArr.setMatrixProperties(dims, symmetric);
      return dataArr;
   }

   protected SkipSchema parseSkipSection(String[] items)
   {
      try
      {
         int k = Integer.parseInt(items[1]);
         return new SkipSchema(k);
      } catch (NumberFormatException e)
      {
         if (items.length > 1)
            return new SkipSchema(items[1]);
      }
      return null;
   }
   
   protected static void outputError(FileErrorFrame errorFrame, String text, String fname, int lineNumber, Exception e)
   {
      if (errorFrame == null)
         System.err.println("ERROR: " + text + "; in function " + fname + " line " + lineNumber);
      else
         errorFrame.setErrorData(text, fname, lineNumber, e);
   }
   
   protected TimestepSchema parseTimestepEntry(int fileType)
   {
      TimestepSchema timestep = null;
      Vector<String[]> res = new Vector<String[]>();
      int result = processLine(line, new String[] {"timestep"}, new String[]{"file", "end"}, res);
      switch (result)
      {
      case ACCEPTED:
         timestep = new TimestepSchema();
         if (res.get(0).length > 1)
            try
            {
               timestep.setTime(Float.parseFloat(res.get(0)[1]));
            } catch (NumberFormatException e)
            {
               outputError("time value ", fileName, r.getLineNumber(), null);
            }
         if (res.get(0).length > 2)
            try
            {
               timestep.setDt(Float.parseFloat(res.get(0)[2]));
            } catch (NumberFormatException e)
            {
            }
         timestep_loop:
         while (true)
         {
            line = nextLine();
            Vector<String[]> tres = new Vector<String[]>();
            result = processLine(line, names, new String[]{"file", "end"}, tres);
            switch (result)
            {
            case ACCEPTED:
               if (tres.get(0)[0].equalsIgnoreCase("repeat"))
               {
                  try
                  {
                     timestep.setRepeat(Integer.parseInt(tres.get(0)[1]));
                  } catch (NumberFormatException e)
                  {
                  }
                  break timestep_loop;
               } else if (tres.get(0)[0].equalsIgnoreCase("skip"))
                  timestep.addSection(parseSkipSection(tres.get(0)));
               else
                  timestep.addSection(parseFileSectionEntry(fileType, tres));
               break;
            case EOF:
               break timestep_loop;
            case ERROR:
               break timestep_loop;
            case BREAK:
               if (res.get(0)[0].startsWith("end"))
                 line = nextLine(); 
               break timestep_loop;
            default:
               break;
            }
         }
      }
      return timestep;
   }

   protected DataFileSchema parseFileEntry()
   {
      String[] fileTypeNames = new String[] { "bin", "asc" };
      int[][] fileTypeInd = new int[][] {{1, 2}, {4, 5, 6}};
      String[][] fileSubTypes = new String[][] {{"b", "l"}, {"???", "col", "fix"}};
      DataFileSchema fileSchema;
      String name = "";
      int type = DataFileSchema.COLUMN;
      String decimalSeparator = null;
      try
      {
         Vector<String[]> res = new Vector<String[]>();
         int result = processLine(line, new String[] {"file"}, new String[] {"end"}, res);
         switch (result)
         {
            case ACCEPTED:
               if (res.get(0).length < 3)
               {
                  outputError("bad data file entry ", fileName, r.getLineNumber(), null);
                  return null;
               }
               String[] strings = res.get(0);
               name = strings[1];
               int baseType = -1;
               for (int j = 0; j < fileTypeNames.length; j++)
                  if (strings[2].toLowerCase().startsWith(fileTypeNames[j]))
                     baseType = j;
               type = fileTypeInd[baseType][0];
               if (strings.length > 3)
                  for (int j = 0; j < fileSubTypes[baseType].length; j++)
                     if (strings[3].toLowerCase().startsWith(fileSubTypes[baseType][j]))
                        type = fileTypeInd[baseType][j];
               if (baseType == 1) 
                  for (int i = 3; i < strings.length; i++)
                     if ((strings[i].startsWith("dec") || strings[i].startsWith("sep")) && strings.length > i+1)
                        decimalSeparator = strings[i+1];
               break;
            case EOF:
               return null;
            case ERROR:
               return null;
            case BREAK:
               return null;
            default:
               break;
         }
         fileSchema = new DataFileSchema(name, type, decimalSeparator);
fileSectionLoop:
         while (true)
         {
            line = nextLine();
            result = processLine(line, names, new String[] {"file", "end"}, res);
            switch (result)
            {
               case ACCEPTED:
                  if (res.get(0)[0].equalsIgnoreCase("skip"))
                  {
                     SkipSchema sSch = parseSkipSection(res.get(0));
                     if (sSch != null)
                        fileSchema.addSection(sSch);
                  }
                  else if (res.get(0)[0].equalsIgnoreCase("timestep"))
                     fileSchema.addSection(parseTimestepEntry(type));
                  else
                     fileSchema.addSection(parseFileSectionEntry(type, res));
                  break;
               case EOF:
                  return fileSchema;
               case ERROR:
                  return fileSchema;
               case BREAK:
                  return fileSchema;
               default:
                  break fileSectionLoop;
            }
         }
         return fileSchema;
      } catch (Exception e)
      {
         System.out.println("error in line: " + line);
         return null;
      }
      
   }
   
   protected int parseOffset(String[] strings, int fileType, int cOffset, int[] offsets)
   {
      if (fileType == DataFileSchema.FIXED_COLUMN)
      {
         String[] keyData = strings[1].split("-");
         try
         {
            if (keyData[0].startsWith("+"))
            {
               offsets[0] = cOffset + Integer.parseInt(keyData[0]);
               offsets[1] = offsets[0] + Integer.parseInt(keyData[1]);
            } else
            {
               offsets[0] = Integer.parseInt(keyData[0]);
               offsets[1] = Integer.parseInt(keyData[1]);
            }
            cOffset = offsets[1];
         } catch (NumberFormatException e)
         {
            outputError(" error in fixed char columns offset ", fileName, r.getLineNumber(), null);
            return cOffset;
         }
      } else
      {
         if (strings.length > 1)
            try
            {
               if (strings[1].startsWith("+"))
                  offsets[0] = cOffset + Integer.parseInt(strings[1]);
               else
               {
                  offsets[0] = Integer.parseInt(strings[1]);
                  if (offsets[0] < 0)
                  {
                     outputError(" error in columns offset ", fileName, r.getLineNumber(), null);
                     offsets[0] = cOffset;
                  }
               }
            } catch (NumberFormatException e)
            {
               outputError(" error in columns offset ", fileName, r.getLineNumber(), null);
               offsets[0] = cOffset;
            }
         else
            offsets[0] = cOffset;
      }
      return cOffset;
   }
   
   abstract protected FilePartSchema parseFileSectionEntry(int fileType, Vector<String[]> tres);
   abstract protected int parseCmpSchema(String[] strings, int fileType, Vector<DataElementIOSchema> compSchemas, int cOffset);
}
