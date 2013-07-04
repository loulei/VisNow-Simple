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
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.gui.widgets.FileErrorFrame;
import pl.edu.icm.visnow.lib.utils.io.*;

import static pl.edu.icm.visnow.lib.basic.readers.ReadVisNowField.SectionModel.typeLengths;

/**
 *
 * @author know
 */
public class RegularFieldHeaderParser
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
   private RegularField field;
   private String[] names;
   private int[] types;
   private int[] vlens;
   private String fileName;
   private File headerFile;
   private FileErrorFrame errorFrame = null;
   private int[][][] tileBds = null;
   private boolean extentsRead = false, affineRead = false, tiles = false, parsingTimestep = false;

   public RegularFieldHeaderParser(LineNumberReader r, File headerFile, String fileName, FileErrorFrame errorFrame)
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
               res[i] = entries[i].split("[=: ]+");
               res[i][0] = res[i][0].toLowerCase();
            }
            return ACCEPTED;
         }
      return UNKNOWN;
   }

   public RegularFieldIOSchema parseHeader()
   {
      RegularFieldIOSchema schema = null;
      String name = "";
      float[][] affine       = new float[][] {{1, 0, 0}, { 0, 1, 0}, {0, 0, 1}, {0, 0, 0}};
      float[][] extents      = new float[][] {{-1, -1, -1},  {1, 1, 1}};
      String[]      axes          = new String[]  {"x", "y", "z"};
      String[] affineEntries = new String[]  {"v0", "v1", "v2", "orig"};
      String[] affineExtentEntries = new String[]  {"r0", "r1", "r2", "orig"};
      try
      {
         header_loop:
         while (true)
         {
            nextLine();
            int result = processLine(new String[] {"dim", "field", "name"},
                                     new String[] {"c", "file", "x", "y", "z", "v", "o", "t"});
            switch (result)
            {
            case ACCEPTED:
               for (int i = 0; i < entries.length; i++)
               {
                  String[] strings = res[i];
                  if ((strings[0].startsWith("name") || strings[0].startsWith("field")) && strings.length > 1)
                     name = strings[1];
                  if (name.startsWith("__"))
                     try
                     {
                        int k = Integer.parseInt(name.substring(2));
                        name = stringsInLine[k];
                     } catch (Exception e)
                     {
                     }
                  if (strings[0].startsWith("dim"))
                  {
                     if (strings.length < 2)
                     {
                        outputError("no dimensions specified after \"dim\"", fileName, r.getLineNumber(), null);
                        return null;
                     }
                     int nDims = strings.length - 1;
                     int[] dims = new int[nDims];
                     for (int j = 0; j < nDims; j++)
                     {
                        try
                        {
                           dims[j] = Integer.parseInt(strings[j + 1]);
                        } catch (Exception e)
                        {
                           outputError(strings[j + 1] + " is not integer ", fileName, r.getLineNumber(), null);
                           return null;
                        }
                     }
                     field = new RegularField(dims);
                     field.setName(name);
                     field.setNSpace(3);
                  }
                  if (strings[0].startsWith("valid") || strings[0].startsWith("mask"))
                  {
                     boolean[] mask = new boolean[field.getNNodes()];
                     for (int j = 0; j < mask.length; j++)
                        mask[j] = true;
                     field.setMask(mask);
                  }
                  if (strings[0].startsWith("nspace"))
                     try
                     {
                        int k = Integer.parseInt(strings[1]);
                        field.setNSpace(k);
                     } catch (Exception e)
                     {
                        outputError(strings[1] + " is not integer ", fileName, r.getLineNumber(), null);
                     }
                  if (strings[0].startsWith("coord"))
                     field.setCoords(new float[field.getNSpace() * field.getNNodes()]);
               }
               break header_loop;
            case EOF:
               outputError("no field description line ", fileName, r.getLineNumber(), null);
               return null;
            case ERROR:
               outputError("bad field file ", fileName, r.getLineNumber(), null);
               return null;
            case BREAK:
               outputError("no field description line ", fileName, r.getLineNumber(), null);
               return null;
            default:
               break;
            }
         }
         if (field == null)
         {
            outputError("no dimension specification in the description line ", fileName, r.getLineNumber(), null);
            return null;
         }
         schema = new RegularFieldIOSchema(field, headerFile, fileName);
         nextLine();
         if (processLine(new String[]{"time"}, new String[]{"c", "f", "v", "o", "x", "y", "z"}) == ACCEPTED)
         {
            if (res[0].length == 3 && res[0][0].startsWith("time") && res[0][1].startsWith("unit"))
            field.setTimeUnit(res[0][2]);
            System.out.println("time unit " + res[0][2]);
            nextLine();
         }
         extent_loop:
         while (true)
         {
            int result = processLine(axes, new String[]{ "c", "f", "v", "r", "o", "t"});
            switch (result)
            {
            case ACCEPTED:
               try
               {
                  for (int i = 0; i < 3; i++)
                     if (res[0][0].startsWith(axes[i]))
                     {
                        extents[0][i] = Float.parseFloat(res[0][1]);
                        extents[1][i] = Float.parseFloat(res[0][2]);
                        break;
                     }
                  extentsRead = true;
               } catch (Exception e)
               {
                  outputError("invalid extents line ", fileName, r.getLineNumber(), null);
               }
               break;
            case EOF:
               outputError("no data section ", fileName, r.getLineNumber(), null);
               return null;
            case ERROR:
               break extent_loop;
            case BREAK:
               break extent_loop;
            default:
               break;
            }
            nextLine();
         }
         affine_loop:
         while (true)
         {
            int result = processLine(new String[]
                    {
                       "o", "v", "r"
                    }, new String[]
                    {
                       "c", "f", "t"
                    });
            switch (result)
            {
            case ACCEPTED:
               try
               {
                  for (int i = 0; i < 4; i++)
                  {
                     if (res[0][0].startsWith(affineEntries[i]))
                        for (int j = 1; j < Math.max(3,res[0].length); j++)
                           affine[i][j - 1] = Float.parseFloat(res[0][j]);
                     else if (res[0][0].startsWith(affineExtentEntries[i]))
                        for (int j = 1; j < Math.max(3,res[0].length); j++)
                           affine[i][j - 1] = Float.parseFloat(res[0][j]) / (field.getDims()[i] - 1);
                  }
                  affineRead = true;
               } catch (Exception e)
               {
                  outputError("invalid affine table entry line", fileName, r.getLineNumber(), null);
               }
               break;
            case EOF:
               outputError("no data section ", fileName, r.getLineNumber(), null);
               return null;
            case ERROR:
               break affine_loop;
            case BREAK:
               break affine_loop;
            default:
               break;
            }
            nextLine();
         }
         if (extentsRead)
            field.setPts(extents);
         else if (affineRead)
            field.setAffine(affine);
         Vector<String> tNames = new Vector<String>();
         Vector<Integer> tTypes = new Vector<Integer>();
         Vector<Integer> tVlens = new Vector<Integer>();
         component_loop:
         while (true)
         {
            int result = processLine(new String[]{"comp", "cmp"}, new String[]{"f"});
            component_switch:
            switch (result)
            {
            case ACCEPTED:
               DataArray a = ParsingUtils.parseComponentEntry(res, stringsInLine, entries, fileName, field, errorFrame, r.getLineNumber());
               if (a != null)
               {
                  for (int i = 0; i < tNames.size(); i++)
                     if (a.getName().equalsIgnoreCase(tNames.get(i)))
                     {
                        outputError("duplicate component name " + a.getName() + ", only first one is valid", fileName, r.getLineNumber(), null);
                        break component_switch;
                     }
                  tNames.add(a.getName());
                  tTypes.add(a.getType());
                  tVlens.add(a.getVeclen());
                  field.addData(a);
               }
               break;
            case EOF:
               break component_loop;
            case ERROR:
               break component_loop;
            case BREAK:
               break component_loop;
            default:
               break;
            }
            nextLine();
         }

         tNames.add("coord");
         tTypes.add(DataArray.FIELD_DATA_FLOAT);
         tVlens.add(field.getNSpace());

         tNames.add("mask");
         tTypes.add(DataArray.FIELD_DATA_BOOLEAN);
         tVlens.add(1);

         names = new String[tNames.size() + 6];
         for (int i = 0; i < tNames.size(); i++)
            names[i] = tNames.get(i);
         names[tNames.size()] = "skip";
         names[tNames.size() + 1] = "stride";
         names[tNames.size() + 2] = "sep";
         names[tNames.size() + 3] = "tile";
         names[tNames.size() + 4] = "timestep";
         names[tNames.size() + 5] = "repeat";
         types = new int[tTypes.size()];
         for (int i = 0; i < types.length; i++)
            types[i] = tTypes.get(i);
         vlens = new int[tVlens.size()];
         for (int i = 0; i < vlens.length; i++)
            vlens[i] = tVlens.get(i);

         file_loop:
         while (true)
         {
            int result = processLine(new String[] {"file"}, new String[] {"end" });
            switch (result)
            {
            case ACCEPTED:
               DataFileSchema s = parseFileEntry();
               if (s != null)
                  schema.addFileSchema(s);
               else
                   return null;
               break;
            case EOF:
               break file_loop;
            case ERROR:
               break file_loop;
            case BREAK:
               break file_loop;
            default:
               break;
            }
         }
         r.close();
      } catch (Exception e)
      {
         outputError("bad header file ", fileName, r.getLineNumber(), null);
         return null;
      }
      return schema;
   }
   
   public DataFileSchema parseFileEntry()
   {
      String[] fileTypeNames = new String[] {"bin", "asc"};
      int[][] fileTypeInd      = new int[][] {{1, 2},{4,5,6}};
      String[][] fileSubTypes = new String[][] {{"b", "l"},
                                                {"???", "col", "fix"}};
      DataFileSchema s = null;
      String name = "";
      int type = DataFileSchema.COLUMN;
      String decimalSeparator = null;
      try
      {
         file_loop:
         while (true)
         {
            int result = processLine(new String[]{"file"}, new String[]{"end" });
            switch (result)
            {
            case ACCEPTED:
               if (res[0].length < 3)
               {
                  outputError("bad data file entry ", fileName, r.getLineNumber(), null);
                  line = "";
                  return null;
               }
               name = res[0][1];
               if (name.startsWith("__"))
                  try
                  {
                     int k = Integer.parseInt(name.substring(2));
                     name = stringsInLine[k];
                  } catch (Exception e)
                  {
                  }
               String[] strings = res[0];
               int baseType = -1;
               for (int j = 0; j < fileTypeNames.length; j++)
                  if (strings[2].toLowerCase().startsWith(fileTypeNames[j]))
                     baseType = j;    
               type = fileTypeInd[baseType][0];
               if (strings.length > 3)
                  for (int j = 0; j < fileSubTypes[baseType].length; j++)
                     if (strings[3].toLowerCase().startsWith(fileSubTypes[baseType][j]))
                        type = fileTypeInd[baseType][j];
               for (int i = 1; i < res.length; i++)
               {
                  strings = res[i];
                  if (strings[0].startsWith("dec") && strings.length > 1)
                     decimalSeparator = strings[1];
               }
               break;
            case EOF:
               break file_loop;
            case ERROR:
               break file_loop;
            case BREAK:
               break file_loop;
            default:
               break;
            }
            s = new DataFileSchema(name, type, decimalSeparator);
            TimestepSchema timestep = null;
            while (true)
            {
               nextLine();
               result = processLine(names, new String[]
                       {
                          "file", "end"
                       });
               switch (result)
               {
               case ACCEPTED:
                  int k = 0;
                  for (int i = 0; i < res.length; i++)
                     if (res[i][0].equalsIgnoreCase("skip"))
                     {
                        SkipSchema sSch = parseSkipSection(res[i]);
                        if (sSch != null)
                           s.addSection(sSch);
                        k = i + 1;
                     }
                  if (res.length > k)
                  {
                     String[][] tr = new String[res.length - k][];
                     System.arraycopy(res, k, tr, 0, res.length - k);
                     res = tr;
                  }
                  else
                     break;
                  if (res[0][0].equalsIgnoreCase("timestep"))
                  {
                     parsingTimestep = true;
                     timestep = new TimestepSchema();
                     if (res[0].length > 1)
                     {
                        try
                        {
                           timestep.setTime(Float.parseFloat(res[0][1]));
                        } catch (Exception e)
                        {
                           outputError("time value ", fileName, r.getLineNumber(), null);
                        }
                     }
                     if (res[0].length > 2)
                     {
                        try
                        {
                           timestep.setDt(Float.parseFloat(res[0][2]));
                        } catch (Exception e)
                        {
                        }
                     }
            timestep_loop:             
                     while (true)
                     {
                        nextLine();
                        result = processLine(names, new String[]
                                {
                                   "file", "end"
                                });
                        switch (result)
                        {
                        case ACCEPTED:
                           if (res[0][0].equalsIgnoreCase("repeat"))
                           {
                              if (parsingTimestep)
                              {
                                 try 
                                 {
                                    timestep.setRepeat(Integer.parseInt(res[0][1]));
                                 } catch (Exception e) {}
                                 parsingTimestep = false;
                              }
                              break timestep_loop;
                           }
                           else if (res[0][0].equalsIgnoreCase("skip"))
                              timestep.addSection(parseSkipSection(res[0]));
                           else
                           {
                              List<FilePartSchema> parsedSections = parseFileSectionEntry(type);
                              for (int i = 0; i < parsedSections.size(); i++)
                                 timestep.addSection(parsedSections.get(i));
                           }
                           break;
                        case EOF:
                           break file_loop;
                        case ERROR:
                           break file_loop;
                        case BREAK:
                           break timestep_loop;
                        default:
                           break;
                        }
                     }
                     s.addSection(timestep);
                  }
                  else
                  {
                     List<FilePartSchema> parsedSections = parseFileSectionEntry(type);
                     for (int i = 0; i < parsedSections.size(); i++)
                        s.addSection(parsedSections.get(i));
                  }
                  break;
               case EOF:
                  break file_loop;
               case ERROR:
                  break file_loop;
               case BREAK:
                  break file_loop;
               default:
                  break;
               }
            }
         }
      } catch (Exception e)
      {
         System.out.println("error in line: " + line);
         return null;
      }
      return s;
   }
   
   private SkipSchema parseSkipSection(String[] items)
   {
      try
      {
         int k = Integer.parseInt(items[1]);
         return new SkipSchema(k);
      } catch (Exception e)
      {
         if (items.length > 1)
            return new SkipSchema(items[1]);
      }
      return null;
   }
   
   private List<FilePartSchema> parseFileSectionEntry(int fileType)
   {
      List<FilePartSchema> sectionsList = new LinkedList<FilePartSchema>();
      int stride = -1;
      int[][] tile = new int[field.getDims().length][2];
      int crd = 0;
      int cmp = -1;
      String separator = "";
      int offset = 0;
      int offset1 = -1;
      int cOffset = 0;
      Vector<ComponentIOSchema> compSchemas = new Vector<ComponentIOSchema>();
      boolean tiledSection = tiles;

      for (int i = 0; i < res.length; i++)
      {
         String[] strings = res[i];
         if (strings[0].startsWith("skip"))
         {
            SkipSchema sSch = parseSkipSection(strings);
            if (sSch != null)
               sectionsList.add(sSch);
            continue;
         }
         if (strings[0].startsWith("stride") && strings.length > 1)
         {
            stride = Integer.parseInt(strings[1]);
            continue;
         }
         if (strings[0].startsWith("separator") && strings.length > 1)
         {
            separator = strings[1];
            continue;
         }
         if (strings[0].startsWith("tile"))
         {
            strings = entries[i].split("[ :]+");
            if (strings.length == field.getDims().length + 1)
               for (int j = 0; j < field.getDims().length; j++)
               {
                  int k = Integer.parseInt(strings[j + 1]);
                  System.arraycopy(tileBds[j][k], 0, tile[j], 0, 2);
               }
            else if (strings.length == 2 * field.getDims().length + 1)
               for (int j = 0; j < field.getDims().length; j++)
               {
                  tile[j][0] = Integer.parseInt(strings[2 * j + 1]);
                  tile[j][1] = Integer.parseInt(strings[2 * j + 2]);
               }
            tiledSection = true;
            continue;
         }
         String[] keyData = strings[0].split("\\.");
         for (int j = 0; j < keyData.length; j++)
            if (keyData[j].startsWith("__"))
               try
               {
                  int k = Integer.parseInt(keyData[j].substring(2));
                  keyData[j] = stringsInLine[k];
               } catch (Exception e)
               {
               }
         cmp =-1;
         int nData = field.getNData();
         if (keyData.length == 1)
         {
            for (int j = 0; j < names.length; j++)
              if (keyData[0].equalsIgnoreCase(names[j]) || j >= nData && keyData[0].startsWith(names[j]))
              {
                 cmp = j;
                 crd = -1;
                 break;
              }
         } else
         {
            try
            {
               crd = Integer.parseInt(keyData[keyData.length - 1]);
            } catch (Exception e)
            {
               crd = -1;
            }
            String cmpName = keyData[keyData.length - 2];
            if (crd == -1)
               cmpName = keyData[keyData.length - 1];
            for (int j = 0; j < names.length; j++)
              if (cmpName.equalsIgnoreCase(names[j]) || j >= nData && cmpName.startsWith(names[j]))
              {
                 cmp = j;
                 break;
              }
         }
         if (cmp == -1)
         {
            System.out.println("warning: no proper component name in line: " );
            continue;
         }
         if (fileType == DataFileSchema.FIXED_COLUMN)
         {
            keyData = strings[1].split("-");
            try
            {
               if (keyData[0].startsWith("+"))
               {
                  offset = cOffset + Integer.parseInt(keyData[0]);
                  offset1 = offset + Integer.parseInt(keyData[1]);
               } else
               {
                  offset = Integer.parseInt(keyData[0]);
                  offset1 = Integer.parseInt(keyData[1]);
               }
               cOffset = offset1;
            } catch (Exception e)
            {
               outputError(" error in fixed char columns offset ", fileName, r.getLineNumber(), null);
               return null;
            }
         } else
         {
            if (strings.length > 1)
               try
               {
                  if (strings[1].startsWith("+"))
                     offset = cOffset + Integer.parseInt(strings[1]);
                  else
                  {
                     offset = Integer.parseInt(strings[1]);
                     if (offset < 0)
                     {
                        outputError(" error in columns offset ", fileName, r.getLineNumber(), null);
                        offset = cOffset;
                     }
                  }
               } catch (Exception e)
               {
                  outputError(" error in columns offset ", fileName, r.getLineNumber(), null);
                  offset = cOffset;
               }
            else
               offset = cOffset;
            int offUnit = 1;
            if (fileType == DataFileSchema.LITTLE_ENDIAN || fileType == DataFileSchema.BIG_ENDIAN)
               offUnit = typeLengths[types[cmp]];
            if (crd == -1)
               cOffset += offUnit * vlens[cmp];
            else
               cOffset += offUnit;
         }
         ComponentIOSchema cmpSchema = new ComponentIOSchema(-1, cmp, crd, types[cmp], offset, offset1);
         if (cmp < field.getNData())       cmpSchema.setCmpName(field.getData(cmp).getName());
         else                              cmpSchema.setCmpName(names[cmp]);
         compSchemas.add(cmpSchema);
      }
      if (stride == -1)
         stride = cOffset;
      FileSectionSchema secSchema = null;
      if (compSchemas.isEmpty())
         secSchema = new FileSectionSchema(stride, compSchemas, null, separator);
      else
         secSchema = new FileSectionSchema(stride, compSchemas, vlens, separator);
      if (tiledSection)
         secSchema.setTile(tile);
      sectionsList.add(secSchema);
      return sectionsList;
   }


}
