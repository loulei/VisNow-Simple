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
exception statement from your version. */
//</editor-fold>


package pl.edu.icm.visnow.lib.basic.readers.ReadVisNowField;

import pl.edu.icm.visnow.lib.basic.readers.ReadVisNowField.utils.RegularFieldIOSchema;
import pl.edu.icm.visnow.lib.basic.readers.ReadVisNowField.utils.DataFileSchema;
import pl.edu.icm.visnow.lib.basic.readers.ReadVisNowField.utils.FileSectionSchema;
import pl.edu.icm.visnow.lib.basic.readers.ReadVisNowField.utils.ComponentIOSchema;
import pl.edu.icm.visnow.lib.basic.readers.ReadVisNowField.utils.FilePartSchema;
import java.io.File;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Vector;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.gui.widgets.FileErrorFrame;
import pl.edu.icm.visnow.lib.basic.readers.ReadVisNowField.utils.DataElementIOSchema;
import static pl.edu.icm.visnow.lib.basic.readers.ReadVisNowField.SectionModel.typeLengths;

/**
 *
 * @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class RegularFieldHeaderParser extends HeaderParser
{
   private RegularField regularField;
   private boolean extentsRead = false, affineRead = false;

   public RegularFieldHeaderParser(LineNumberReader r, File headerFile, String fileName, FileErrorFrame errorFrame)
   {
      super(r, headerFile, fileName, errorFrame);
   }

   public RegularFieldIOSchema parseHeader()
   {
      Vector<String[]> res = new Vector<String[]>();
      int result;
      RegularFieldIOSchema schema;
      String name = "";
      float[][] affine       = new float[][] {{1, 0, 0}, { 0, 1, 0}, {0, 0, 1}, {0, 0, 0}};
      float[][] extents      = new float[][] {{-1, -1, -1},  {1, 1, 1}};
      String[] axesNames          = new String[]  {"x", "y", "z"};
      String[] affineEntries = new String[]  {"v0", "v1", "v2", "orig"};
      String[] affineExtentEntries = new String[]  {"r0", "r1", "r2", "orig"};
      try
      {
         line = nextLine();
         result = processLine(line, new String[] {"dim", "field", "name"},
                                  new String[] {"c", "file", "x", "y", "z", "v", "o", "t"}, res);
         switch (result)
         {
         case ACCEPTED:
            for (int i = 0; i < res.size(); i++)
            {
               String[] strings = res.get(i);
               if ((strings[0].startsWith("name") || strings[0].startsWith("field")) && strings.length > 1)
                  name = strings[1];
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
                     } catch (NumberFormatException e)
                     {
                        outputError(strings[j + 1] + " is not integer ", fileName, r.getLineNumber(), null);
                        return null;
                     }
                  }
                  regularField = new RegularField(dims);
                  regularField.setName(name);
                  regularField.setNSpace(3);
               }
               if (strings[0].startsWith("valid") || strings[0].startsWith("mask"))
               {
                  boolean[] mask = new boolean[regularField.getNNodes()];
                  for (int j = 0; j < mask.length; j++)
                     mask[j] = true;
                  regularField.setMask(mask);
               }
               if (strings[0].startsWith("nspace"))
                  try
                  {
                     int k = Integer.parseInt(strings[1]);
                     regularField.setNSpace(k);
                  } catch (NumberFormatException e)
                  {
                     outputError(strings[1] + " is not integer ", fileName, r.getLineNumber(), null);
                  }
               if (strings[0].startsWith("coord"))
                  regularField.setCoords(new float[regularField.getNSpace() * regularField.getNNodes()]);
            }
            break;
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
         if (regularField == null)
         {
            outputError("no dimension specification in the description line ", fileName, r.getLineNumber(), null);
            return null;
         }
         schema = new RegularFieldIOSchema(regularField, headerFile, fileName);
         line = nextLine();
         if (processLine(line, new String[]{"time"}, new String[]{"c", "f", "v", "o", "x", "y", "z"}, res) == ACCEPTED)
         {
            if (res.get(0).length == 3 && res.get(0)[0].startsWith("time") && res.get(0)[1].startsWith("unit"))
               regularField.setTimeUnit(res.get(0)[2]);
            System.out.println("time unit " + res.get(0)[2]);
            line = nextLine();
         }
extent_loop:
         while (true)
         {
            result = processLine(line, axesNames, new String[]{ "c", "f", "v", "r", "o", "t"}, res);
            switch (result)
            {
            case ACCEPTED:
               try
               {
                  for (int i = 0; i < 3; i++)
                     if (res.get(0)[0].startsWith(axesNames[i]))
                     {
                        extents[0][i] = Float.parseFloat(res.get(0)[1]);
                        extents[1][i] = Float.parseFloat(res.get(0)[2]);
                        break;
                     }
                  extentsRead = true;
               } catch (NumberFormatException e)
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
            line = nextLine();
         }
affine_loop:
         while (true)
         {
            result = processLine(line, new String[]{"o", "v", "r"}, new String[]{"c", "f", "t"}, res);
            switch (result)
            {
            case ACCEPTED:
               try
               {
                  for (int i = 0; i < 4; i++)
                  {
                     if (res.get(0)[0].startsWith(affineEntries[i]))
                        for (int j = 1; j < Math.max(3,res.get(0).length); j++)
                           affine[i][j - 1] = Float.parseFloat(res.get(0)[j]);
                     else if (res.get(0)[0].startsWith(affineExtentEntries[i]))
                        for (int j = 1; j < Math.max(3,res.get(0).length); j++)
                           affine[i][j - 1] = Float.parseFloat(res.get(0)[j]) / (regularField.getDims()[i] - 1);
                  }
                  affineRead = true;
               } catch (NumberFormatException e)
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
            line = nextLine();
         }
         if (extentsRead)
            regularField.setExtents(extents);
         else if (affineRead)
            regularField.setAffine(affine);
         Vector<String> tNames = new Vector<String>();
         Vector<Integer> tTypes = new Vector<Integer>();
         Vector<Integer> tVlens = new Vector<Integer>();
         DataArray currentComponent;
         component_loop:
         while ((currentComponent = parseComponentEntry(line, regularField.getNNodes(), 
                                                        fileName, errorFrame, r.getLineNumber())) != null)
         {
            for (int i = 0; i < tNames.size(); i++)
               if (currentComponent.getName().equalsIgnoreCase(tNames.get(i)))
               {
                  outputError("duplicate component name " + currentComponent.getName() + 
                              ", only first one is valid", fileName, r.getLineNumber(), null);
                  line = nextLine();
                  continue component_loop;
               }
            tNames.add(currentComponent.getName());
            tTypes.add(currentComponent.getType());
            tVlens.add(currentComponent.getVeclen());
            regularField.addData(currentComponent);
            line = nextLine();
         }
         
         tNames.add("coord");
         tTypes.add(DataArray.FIELD_DATA_FLOAT);
         tVlens.add(regularField.getNSpace());
         
         tNames.add("mask");
         tTypes.add(DataArray.FIELD_DATA_BOOLEAN);
         tVlens.add(1);
         
         names = new String[tNames.size() + 6];
         String[] specialNames = new String[]{"skip", "stride", "sep", "tile", "timestep", "repeat"};
         for (int i = 0; i < tNames.size(); i++)
            names[i] = tNames.get(i);
         System.arraycopy(specialNames, 0, names, tNames.size(), 6);
         types = new int[tTypes.size()];
         for (int i = 0; i < types.length; i++)
            types[i] = tTypes.get(i);
         vlens = new int[tVlens.size()];
         for (int i = 0; i < vlens.length; i++)
            vlens[i] = tVlens.get(i);
         
         DataFileSchema dataFileSchema;
file_loop:
         while ((dataFileSchema = parseFileEntry()) != null)
            schema.addFileSchema(dataFileSchema);
         r.close();
      } catch (IOException e)
      {
         outputError("bad header file ", fileName, r.getLineNumber(), null);
         return null;
      }
      return schema;
   }
   
   @Override
   protected int parseCmpSchema(String[] strings, int fileType, Vector<DataElementIOSchema> compSchemas, int cOffset)
   {
      int crd = 0, cmp = -1;
      int offset, offset1 = -1;
      String[] keyData = strings[0].split("\\.");
      int nData = regularField.getNData();
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
         } catch (NumberFormatException e)
         {
            crd = -1;
         }
         String cmpName = keyData[keyData.length - 2];
         if (crd == -1)
            cmpName = keyData[keyData.length - 1];
         for (int j = 0; j < names.length; j++)
           if (cmpName.equalsIgnoreCase(names[j]))
           {
              cmp = j;
              break;
           }
      }
      if (cmp == -1)
      {
         System.out.println("warning: no proper component name in line: " );
         return cOffset;
      }
      int[] offsets = {-1, -1};
      cOffset = parseOffset(strings, fileType, cOffset, offsets);
      ComponentIOSchema cmpSchema = null;
      if (cmp < regularField.getNData()) 
      {
         DataArray da = regularField.getData(cmp);
         cmpSchema = new ComponentIOSchema(regularField, cmp, crd, da.getType(),  
                                           da.getVeclen(), regularField.getNNodes(), offsets[0], offsets[1]);
         cmpSchema.setCmpName(da.getName());
      }
      else if (cmp == regularField.getNData())   
      {
         cmpSchema = new ComponentIOSchema(regularField, cmp, crd, DataArray.FIELD_DATA_FLOAT,  
                                           regularField.getNSpace(), regularField.getNNodes(), offsets[0], offsets[1]);
         cmpSchema.setCmpName(names[cmp]);
      }
      else
      {
         cmpSchema = new ComponentIOSchema(regularField, cmp, crd, DataArray.FIELD_DATA_BOOLEAN,  
                                           1, regularField.getNNodes(), offsets[0], offsets[1]);
         cmpSchema.setCmpName(names[cmp + 1]);
      }
      compSchemas.add(cmpSchema);
      int offUnit = 1;
      if (fileType == DataFileSchema.LITTLE_ENDIAN || fileType == DataFileSchema.BIG_ENDIAN)
         offUnit = typeLengths[types[cmp]];
      if (crd == -1)
         return cOffset + offUnit * vlens[cmp];
      else
         return cOffset + offUnit;
   }

   @Override
   protected FilePartSchema parseFileSectionEntry(int fileType, Vector<String[]> tokens)
   {
      int stride = -1;
      int[][] tile = null;
      String separator = "";
      int cOffset = 0;
      Vector<DataElementIOSchema> compSchemas = new Vector<DataElementIOSchema>();

      for (int i = 0; i < tokens.size(); i++)
      {
         String[] strings = tokens.get(i);
         if (strings[0].startsWith("stride") && strings.length > 1)
            stride = Integer.parseInt(strings[1]);
         else if (strings[0].startsWith("separator") && strings.length > 1)
            separator = strings[1];
         else if (strings[0].startsWith("tile"))
         {
            try
            {
               tile = new int[regularField.getDims().length][2];
               if (strings.length == 2 * regularField.getDims().length + 1)
                  for (int j = 0; j < regularField.getDims().length; j++)
                  {
                     tile[j][0] = Integer.parseInt(strings[2 * j + 1]);
                     tile[j][1] = Integer.parseInt(strings[2 * j + 2]);
                  }
               else if (strings.length == regularField.getDims().length + 1)
               {
                  for (int j = 0; j < regularField.getDims().length; j++)
                  {
                     String[] tileBdrs = strings[j + 1].split(":");
                     tile[j][0] = Integer.parseInt(tileBdrs[0]);
                     tile[j][1] = Integer.parseInt(tileBdrs[1]);
                  }
               }
            } catch (NumberFormatException e)
            {
               System.out.println("warning: bad tile boundary in line: " );
               return null;
            } catch (ArrayIndexOutOfBoundsException e)
            {
               System.out.println("warning: bad tile boundaries in line: " );
               return null;
            }
         }
         else
            cOffset = parseCmpSchema(strings, fileType, compSchemas, cOffset);
      }
      if (stride == -1)
         stride = cOffset;
      FileSectionSchema secSchema;
      if (compSchemas.isEmpty())
         secSchema = new FileSectionSchema(stride, compSchemas, null, separator, (fileType & DataFileSchema.BINARY) != 0);
      else
         secSchema = new FileSectionSchema(stride, compSchemas, vlens, separator, (fileType & DataFileSchema.BINARY) != 0);
      if (tile != null)
         secSchema.setTile(tile);
      return secSchema;
   }

}
