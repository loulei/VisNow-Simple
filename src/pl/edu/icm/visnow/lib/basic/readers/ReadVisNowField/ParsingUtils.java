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

import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.datasets.dataarrays.*;
import pl.edu.icm.visnow.gui.widgets.FileErrorFrame;

/**
 *
 * @author know
 */
public class ParsingUtils
{
   public static DataArray parseComponentEntry(String[][] res, String[] stringsInLine, 
                                               String[] entries, String fileName, 
                                               int nNodes, FileErrorFrame errorFrame, int lineNumber)
   {
      DataArray dataArr = null;
      String[][] typeNames = {{"bool", "log"},
                              {"byte", "char", "unsigned char"},
                              {"short"},
                              {"int"},
                              {"float", "real"},
                              {"double"},
                              {"complex"},
                              {"string"}};
      String name = "";
      String unit = null;
      int type = DataArray.FIELD_DATA_UNKNOWN;
      int veclen = 1;
      int[] dims = new int[] {1};
      boolean symmetric = false;
      float physMin = 0;
      float physMax = 1;
      String[] userData = null;
      name = res[0][1];
      if (name.startsWith("__"))
         try
         {
            int k = Integer.parseInt(name.substring(2));
            name = stringsInLine[k];
         } catch (Exception e)
         {
         }
      if (res[0].length > 2)
      {
         parse_type:
         for (int j = 0; j < typeNames.length; j++)
            for (int k = 0; k < typeNames[j].length; k++)
               if (res[0][2].startsWith(typeNames[j][k]))
               {
                  type = j;
                  break parse_type;
               }
      } else
      {
         outputError(errorFrame, "data type specification missing ", fileName, lineNumber, null);
         return null;
      }
      for (int i = 1; i < entries.length; i++)
      {
         String[] strings = res[i];
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
                  for (int k = 0; k < typeNames[j].length; k++)
                     if (val.startsWith(typeNames[j][k]))
                     {
                        type = j;
                        break parse;
                     }
            }
            if (strings[0].startsWith("user") && strings.length > 1)
            {
               userData = strings[1].trim().split(" *; *");
               for (int j = 0; j < userData.length; j++)
               {
                  if (userData[j].startsWith("__"))
                     try
                     {
                        int k = Integer.parseInt(userData[j].substring(2));
                        userData[j] = stringsInLine[k];
                     } catch (Exception e)
                     {
                     }
               }
            }
            if (strings[0].startsWith("min") && strings.length > 1)
               physMin = Float.parseFloat(strings[1]);
            if (strings[0].startsWith("max") && strings.length > 1)
               physMax = Float.parseFloat(strings[1]);
         } catch (Exception e)
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
      dataArr.setPhysMin(physMin);
      dataArr.setPhysMax(physMax);
      dataArr.setMatrixProperties(dims, symmetric);
      return dataArr;
   }
   
   public static DataArray parseComponentEntry(String[][] res, String[] stringsInLine, 
                                               String[] entries, String fileName, 
                                               Field field, FileErrorFrame errorFrame, int lineNumber)
   {
      return parseComponentEntry(res, stringsInLine, entries, fileName, field.getNNodes(), errorFrame, lineNumber);
   }
   public static void outputError(FileErrorFrame errorFrame, String text, String fname, int lineNumber, Exception e)
   {
      if (errorFrame == null)
         System.err.println("ERROR: " + text + "; in function " + fname + " line " + lineNumber);
      else
         errorFrame.setErrorData(text, fname, lineNumber, e);
   }
   
}
