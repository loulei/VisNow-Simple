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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.edu.icm.visnow.datasets.CellArray;
import pl.edu.icm.visnow.datasets.CellSet;
import pl.edu.icm.visnow.datasets.IrregularField;
import pl.edu.icm.visnow.datasets.cells.Cell;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.gui.widgets.FileErrorFrame;
import pl.edu.icm.visnow.lib.basic.readers.ReadVisNowField.utils.BooleanArrayIOSchema;
import pl.edu.icm.visnow.lib.basic.readers.ReadVisNowField.utils.ComponentIOSchema;
import pl.edu.icm.visnow.lib.basic.readers.ReadVisNowField.utils.DataElementIOSchema;
import pl.edu.icm.visnow.lib.basic.readers.ReadVisNowField.utils.DataFileSchema;
import pl.edu.icm.visnow.lib.basic.readers.ReadVisNowField.utils.FilePartSchema;
import pl.edu.icm.visnow.lib.basic.readers.ReadVisNowField.utils.FileSectionSchema;
import pl.edu.icm.visnow.lib.basic.readers.ReadVisNowField.utils.IntArrayIOSchema;
import pl.edu.icm.visnow.lib.basic.readers.ReadVisNowField.utils.IrregularFieldIOSchema;

import static pl.edu.icm.visnow.lib.basic.readers.ReadVisNowField.SectionModel.typeLengths;

/**
 *
 * @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class IrregularFieldHeaderParser extends HeaderParser
{
   public static final String[][] prefixes = {{"point", "pt"}, {"line", "seg"}, {"tri"}, {"quad"}, {"tet"}, {"pyr"}, {"prism"}, {"hex"}};
   public static final String[] cellNames = {"point", "pt", "line", "seg", "tri", "quad", "tet", "pyr", "prism", "hex"};
   
//   private String line = "";
   protected IrregularField irregularField;

   public IrregularFieldHeaderParser(LineNumberReader r, File headerFile, String fileName, FileErrorFrame errorFrame)
   {
      super(r, headerFile, fileName, errorFrame);
   }
   
   protected CellSet parseCellSetEntry()
   {
      CellSet cellSet = null;
      Vector<String[]> res = new Vector<String[]>();
      int result = processLine(line, new String[] {"cell"}, new String[] {"file"}, res);
      if (result != ACCEPTED)
         return null;
      String name = res.get(0)[1];
      cellSet = new CellSet(name);
      if (res.size()> 1)
         try
         {
            String[] item = res.get(1);
            if (item[0].startsWith("n"))
               cellSet.setnDataValues(Integer.parseInt(item[1]));
            else if (item[1].startsWith("d") || item[1].startsWith("v"))
               cellSet.setnDataValues(Integer.parseInt(item[1]));
         } catch (NumberFormatException e)
         {
         }
cell_array_loop:
      while (true)
      {
         line = nextLine();
         result = processLine(line, cellNames,
                              new String[] {"comp", "cmp", "file"}, res);
         switch (result)
         {
         case ACCEPTED:
            String[] strings = res.get(0);
            int type = -1;
            int n = 0;
cell_type_loop:            
            for (int i = 0; i < prefixes.length; i++)
               for (String item : prefixes[i])
               {
                  if (strings[0].startsWith(item))
                  {
                     type = i;
                     try
                     {
                        n = Integer.parseInt(strings[1]);
                     } catch (NumberFormatException e)
                     {
                     }
                     break cell_type_loop;
                  }
                  if (strings[1].startsWith(item))
                  {
                     type = i;
                     try
                     {
                        n = Integer.parseInt(strings[0]);
                     } catch (NumberFormatException e)
                     {

                     }
                     break cell_type_loop;
                  }
               }
            if (type == -1)
               break;
            int[] nodes = new int[n * Cell.nv[type]];
            int[] dataIndices = new int[n];
            boolean[] orientations = new boolean[n];
            CellArray ca = new CellArray(type, nodes, orientations, dataIndices);
            cellSet.addCells(ca);
            break;
         case EOF:
            return cellSet;
         case ERROR:
            outputError("bad cell array entry ", fileName, r.getLineNumber(), null);
            return null;
         case BREAK:
            break cell_array_loop;
         default:
            break cell_array_loop;
         }
      }
      Vector<String> tNames = new Vector<String>();
      DataArray currentComponent = null;
component_loop:
      while ((currentComponent = parseComponentEntry(line, cellSet.getnDataValues(), 
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
         cellSet.addData(currentComponent);
         line = nextLine();
      }
      return cellSet;
   }

   public IrregularFieldIOSchema parseHeader()
   {
      IrregularFieldIOSchema schema = null;
      Vector<String[]> res = new Vector<String[]>();
      String name = "";
      try
      {
         line = nextLine();
         int result = processLine(line, new String[] {"field", "name"},
                                  new String[] {"c", "file"}, res);
         switch (result)
         {
         case ACCEPTED:
            for (int i = 0; i < res.size(); i++)
            {
               String[] strings = res.get(i);
               if ((strings[0].startsWith("name") || strings[0].startsWith("field")) && strings.length > 1)
                  name = strings[1];
               else if (strings[0].startsWith("nnodes"))
               {
                  if (strings.length < 2)
                  {
                     outputError("no nodes count specified after \"nnodes\"", fileName, r.getLineNumber(), null);
                     return null;
                  }
                  int nnodes = 0;
                  try
                  {
                     nnodes = Integer.parseInt(strings[1]);
                  } catch (NumberFormatException e)
                  {
                     outputError(strings[1] + " is not integer ", fileName, r.getLineNumber(), null);
                     return null;
                  }
                  irregularField = new IrregularField(nnodes);
                  irregularField.setName(name);
                  irregularField.setNSpace(3);
                  irregularField.setCoords(new float[irregularField.getNSpace() * irregularField.getNNodes()]);
               }
               else if (strings[0].startsWith("valid") || strings[0].startsWith("mask"))
               {
                  boolean[] mask = new boolean[irregularField.getNNodes()];
                  for (int j = 0; j < mask.length; j++)
                     mask[j] = true;
                  irregularField.setMask(mask);
               }
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
         if (irregularField.getNNodes() < 1)
         {
            outputError("no nodes count specified ", fileName, r.getLineNumber(), null);
            return null;
         }
         schema = new IrregularFieldIOSchema(irregularField, headerFile, fileName);
         line = nextLine();
         if (processLine(line, new String[]{"time"}, new String[]{"c"}, res) == ACCEPTED)
         {
            if (res.get(0).length == 3 && res.get(0)[0].startsWith("time") && res.get(0)[1].startsWith("unit"))
               irregularField.setTimeUnit(res.get(0)[2]);
            System.out.println("time unit " + res.get(0)[2]);
            nextLine();
         }
         Vector<String> tNames = new Vector<String>();
         Vector<Integer> tTypes = new Vector<Integer>();
         Vector<Integer> tVlens = new Vector<Integer>();
         DataArray currentComponent = null;
component_loop:
         while ((currentComponent = parseComponentEntry(line, irregularField.getNNodes(), 
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
            irregularField.addData(currentComponent);
            line = nextLine();
         }

         tNames.add("coord");
         tTypes.add(DataArray.FIELD_DATA_FLOAT);
         tVlens.add(irregularField.getNSpace());
         
         tNames.add("mask");
         tTypes.add(DataArray.FIELD_DATA_BOOLEAN);
         tVlens.add(1);

         CellSet newCellSet = null;
         while ((newCellSet = parseCellSetEntry()) != null)
            irregularField.addCellSet(newCellSet);
         
         for (CellSet cellSet : irregularField.getCellSets())
            tNames.add(cellSet.getName());
         
        
         String[] specialNames = new String[]{"skip", "stride", "sep", "tile", "timestep", "repeat"};
         names = new String[tNames.size() + specialNames.length];
         for (int i = 0; i < tNames.size(); i++)
            names[i] = tNames.get(i);
         System.arraycopy(specialNames, 0, names, tNames.size(), specialNames.length);
         types = new int[tTypes.size()];
         for (int i = 0; i < types.length; i++)
            types[i] = tTypes.get(i);
         vlens = new int[tVlens.size()];
         for (int i = 0; i < vlens.length; i++)
            vlens[i] = tVlens.get(i);
         DataFileSchema dataFileSchema;
//         for (int i = 0; i < names.length; i++)
//            System.out.println(names[i]);
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
   protected FilePartSchema parseFileSectionEntry(int fileType, Vector<String[]> tokens)
   {
      int stride = -1;
      int[][] tile = null;
      String separator = "";
      int cOffset = 0;
      Vector<DataElementIOSchema> compSchemas = new Vector<DataElementIOSchema>();

      for (int i = 0; i < tokens.size(); i++)
      {
         String[] strings = tokens.get(i);              // parsing i-th item with file part description line
         if (strings[0].startsWith("stride") && strings.length > 1)
            stride = Integer.parseInt(strings[1]);
         else if (strings[0].startsWith("separator") && strings.length > 1)
            separator = strings[1];
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

   @Override
   protected int parseCmpSchema(String[] strings, int fileType, Vector<DataElementIOSchema> compSchemas, int cOffset)
   {
      int crd = 0, cmp = -1;
      int[] offsets = {-1, -1};
      cOffset = parseOffset(strings, fileType, cOffset, offsets);
      String[] keyData = strings[0].split("\\.");
      if (keyData.length == 1)
         crd = -1;
      else
      {
         try
         {
            crd = Integer.parseInt(keyData[keyData.length - 1]);
         } catch (NumberFormatException e)
         {
            crd = -1;
         }
      }
      String elementName = keyData[0];
      for (int i = 0; i < irregularField.getNData(); i++)
         if (elementName.equalsIgnoreCase(irregularField.getData(i).getName()))
         {
            DataArray da = irregularField.getData(i);
            cmp = i; 
            ComponentIOSchema cmpSchema = new ComponentIOSchema(irregularField, cmp, crd, da.getType(), 
                                                                da.getVeclen(), irregularField.getNNodes(), offsets[0], offsets[1]);
            cmpSchema.setCmpName(da.getName());
            compSchemas.add(cmpSchema);
            int offUnit = 1;
            if (fileType == DataFileSchema.LITTLE_ENDIAN || fileType == DataFileSchema.BIG_ENDIAN)
               offUnit = typeLengths[types[cmp]];
            if (crd == -1)
               return cOffset + offUnit * vlens[cmp];
            else
               return cOffset + offUnit;
         }
      if (elementName.startsWith("coord"))
      {
         cmp = irregularField.getNData();
         ComponentIOSchema cmpSchema = new ComponentIOSchema(irregularField, cmp, crd, DataArray.FIELD_DATA_FLOAT, 
                                                             irregularField.getNSpace(), irregularField.getNNodes(), offsets[0], offsets[1]);
         cmpSchema.setCmpName("coords");
         compSchemas.add(cmpSchema);
         int offUnit = 1;
         if (fileType == DataFileSchema.LITTLE_ENDIAN || fileType == DataFileSchema.BIG_ENDIAN)
            offUnit = 4;
         if (crd == -1)
            return cOffset + 3 * offUnit;
         else
            return cOffset + offUnit;
      }
      if (irregularField.isMask() && elementName.startsWith("mask"))
      {
         cmp = irregularField.getNData();
         ComponentIOSchema cmpSchema = new ComponentIOSchema(irregularField, cmp, crd, DataArray.FIELD_DATA_BOOLEAN, 
                                                             1, irregularField.getNNodes(), offsets[0], offsets[1]);
         cmpSchema.setCmpName("mask");
         compSchemas.add(cmpSchema);
         return cOffset + 1;   
      }
      String[] dataID = elementName.split(":");
      for (int k = 0; k < irregularField.getNCellSets(); k++)
      {
         CellSet cs = irregularField.getCellSet(k);
         if (cs.getName().equalsIgnoreCase(dataID[0]))
         {
            if (dataID.length == 2)
            {
               for (int i = 0; i < cs.getNData(); i++)
                  if (cs.getData(i).getName().equalsIgnoreCase(dataID[1]))
                  {
                     DataArray da = cs.getData(i);
                     cmp = i;
                     ComponentIOSchema cmpSchema = new ComponentIOSchema(cs, cmp, crd, da.getType(), 
                                                       da.getVeclen(), cs.getnDataValues(), offsets[0], offsets[1]);
                     cmpSchema.setCmpName(da.getName());
                     compSchemas.add(cmpSchema);
                     int offUnit = 1;
                     if (fileType == DataFileSchema.LITTLE_ENDIAN || fileType == DataFileSchema.BIG_ENDIAN)
                        offUnit = typeLengths[types[cmp]];
                     if (crd == -1)
                        return cOffset + offUnit * vlens[cmp];
                     else
                        return cOffset + offUnit;
                  }
            }
            else
            {
               for (int i = 0; i < prefixes.length; i++)
                  for (String item : prefixes[i])
                     if (dataID[1].startsWith(item))
                     {
                        if (dataID[2].startsWith("ind"))
                        {
                           IntArrayIOSchema aSch = new IntArrayIOSchema(cs, cs.getCellArray(i).getDataIndices(), 1, 
                                                                        cs.getCellArray(i).getNCells(), -1, offsets[0], offsets[1]);
                           compSchemas.add(aSch);
                           if (fileType == DataFileSchema.LITTLE_ENDIAN || fileType == DataFileSchema.BIG_ENDIAN)
                              return cOffset + 4;
                           else
                              return cOffset + 1;
                        }
                        else if (dataID[2].startsWith("ori"))
                        {
                           BooleanArrayIOSchema aSch = new BooleanArrayIOSchema(cs, cs.getCellArray(i).getOrientations(), 1, 
                                                           cs.getCellArray(i).getNCells(), 0, offsets[0], offsets[1]);
                           compSchemas.add(aSch);
                           return cOffset + 1;
                        }
                        else
                        {
                           int vlen = Cell.nv[i];
                           IntArrayIOSchema aSch = new IntArrayIOSchema(cs, cs.getCellArray(i).getNodes(), vlen, 
                                                                        cs.getCellArray(i).getNCells(), -1, offsets[0], offsets[1]);
                           compSchemas.add(aSch);
                           if (fileType == DataFileSchema.LITTLE_ENDIAN || fileType == DataFileSchema.BIG_ENDIAN)
                              return cOffset + 4 * vlen;
                           else
                              return cOffset + vlen;
                        }
                     }
            }
         }
      }
      System.out.println("warning: no proper component name in line: ");
      return cOffset;   
   }
   
   public static void main(String[] args)
   {
      FileErrorFrame errorFrame = new FileErrorFrame();
      try
      {
         BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
         System.out.println("Enter path");
         String fName = br.readLine();
         Parser parser = new Parser(fName, false, errorFrame);
         IrregularFieldIOSchema schema = (IrregularFieldIOSchema) parser.parseFieldHeader();
         System.out.println("" + schema);
      } catch (IOException ex)
      {
         Logger.getLogger(IrregularFieldHeaderParser.class.getName()).log(Level.SEVERE, null, ex);
      }
   }
}
