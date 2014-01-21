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

package pl.edu.icm.visnow.lib.basic.readers.ReadAVSField;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.nio.ByteOrder;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import javax.imageio.stream.FileImageInputStream;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.datasets.RegularFieldSchema;

class AVSFieldVariable
{

   private Map<String, String> parameters = null;

   public AVSFieldVariable(Map<String, String> params)
   {
      this.parameters = params;
   }

   // TODO getters & setters
   public Map<String, String> getParameters()
   {
      return parameters;
   }
}

/**
 *
 * @author Michal Lyczek (lyczek@icm.edu.pl)
 */
public class Core
{

   private static final int RECTILINEAR = 999;
   private static final int STRIDE_DEFAULT = 1;
   private static final int OFFSET_DEFAULT = 0;
   private static final int SKIP_DEFAULT = 0;
   private Map<String, String> parameters;
   private Map<String, AVSFieldVariable> variables;
   private Map<String, AVSFieldVariable> coords;
   private int ndim;
   private int[] dims;
   private int nspace = 3;
   private int dataType = DataArray.FIELD_DATA_BYTE;
   private ByteOrder endian = ByteOrder.BIG_ENDIAN;
   private int veclen;
   private int geometry;
   private String[] labels;
   private String[] units;
   private boolean variablesDefined = false;

   public void parseHeaderLine(String in)
   {

      if (in.isEmpty())
         return;
      String[] words = in.split("\\s+");
      if (words.length < 1)
         return;
      if (words[0].equals("variable"))
      {
         variablesDefined = true;
         // reading variable definition
         Map<String, String> variableParameters = new LinkedHashMap<String, String>();
         variableParameters.put("id", words[1]);
         for (int i = 2; i < words.length; i++)
         {
            String[] params = words[i].split("=");
            variableParameters.put(params[0].trim(), params[1].trim());
         }

         if (parameters.containsKey("label"))
         {
            labels = parameters.get("label").split("\\s+");
            int variableId = Integer.parseInt(variableParameters.get("id"));
            if (variableId <= labels.length)
               variableParameters.put("label", labels[variableId - 1]);
         }
         variables.put(words[1], new AVSFieldVariable(variableParameters));
      } else if (words[0].equals("coord"))
      {
         // reading variable definition
         Map<String, String> coordsParameters = new LinkedHashMap<String, String>();
         coordsParameters.put("type", "float");
         coordsParameters.put("id", words[1]);
         for (int i = 2; i < words.length; i++)
         {
            String[] params = words[i].split("=");
            coordsParameters.put(params[0].trim(), params[1].trim());
         }
         coords.put(words[1], new AVSFieldVariable(coordsParameters));
      } else
      {
         // reading parameter
         String[] input = in.split("=");
         parameters.put(input[0].trim(), input[1].trim());
      }
   }

   private DataArray readVariableFromASCII(AVSFieldVariable var, int size, int dataType)
   {

      String path = var.getParameters().get("file");
      int skip = var.getParameters().containsKey("skip") ? Integer.parseInt(var.getParameters().get("skip")) : SKIP_DEFAULT;
      int offset = var.getParameters().containsKey("offset") ? Integer.parseInt(var.getParameters().get("offset")) : OFFSET_DEFAULT;
      int stride = var.getParameters().containsKey("stride") ? Integer.parseInt(var.getParameters().get("stride")) : STRIDE_DEFAULT;
      String label = var.getParameters().containsKey("label") ? var.getParameters().get("label") : "variable " + var.getParameters().get("id");

      byte[] dataB = null;
      short[] dataS = null;
      int[] dataI = null;
      float[] dataF = null;
      double[] dataD = null;
      StreamTokenizer st;
      try
      {
         Reader in = new FileReader(path);
         st = new StreamTokenizer(in);
         st.resetSyntax();
         st.lowerCaseMode(true);
         st.eolIsSignificant(true);
         st.whitespaceChars(' ', ',');
         st.wordChars(33, ',' - 1);
         st.wordChars(',' + 1, 255);
         String s;
         int lines = 0;
         while (lines < skip && st.nextToken() != StreamTokenizer.TT_EOF)             //skipping initial lines (table header)
            if (st.ttype == StreamTokenizer.TT_EOL)
               lines += 1;
         st.eolIsSignificant(false);
         try
         {
            for (int i = 0; i < offset && st.nextToken() != StreamTokenizer.TT_EOF; i++)                 //skipping to column
               s = st.sval;
            switch (dataType)
            {
               case DataArray.FIELD_DATA_INT:
                  dataI = new int[size];
                  for (int i = 0; i < size && st.nextToken() != StreamTokenizer.TT_EOF; i++)
                  {
                     dataI[i] = Integer.parseInt(st.sval);
                     for (int j = 0; j < stride - 1 && st.nextToken() != StreamTokenizer.TT_EOF; j++)   //skip to next data item
                        s = st.sval;
                  }
                  break;
               case DataArray.FIELD_DATA_FLOAT:
                  dataF = new float[size];
                  for (int i = 0; i < size && st.nextToken() != StreamTokenizer.TT_EOF;)
                  {
                     if (st.sval == null)
                        continue;
                     dataF[i] = Float.parseFloat(st.sval);
                     for (int j = 0; j < stride - 1 && st.nextToken() != StreamTokenizer.TT_EOF;)
                     {//skip to next data item
                        s = st.sval;
                        if (s == null)
                           continue;
                        j += 1;
                     }
                     i += 1;
                  }
                  break;
               case DataArray.FIELD_DATA_DOUBLE:
                  dataD = new double[size];
                  for (int i = 0; i < size && st.nextToken() != StreamTokenizer.TT_EOF; i++)
                  {
                     dataD[i] = Double.parseDouble(st.sval);
                     for (int j = 0; j < stride - 1 && st.nextToken() != StreamTokenizer.TT_EOF; j++)   //skip to next data item
                        s = st.sval;
                  }
                  break;
               case DataArray.FIELD_DATA_BYTE:
                  dataB = new byte[size];
                  for (int i = 0; i < size && st.nextToken() != StreamTokenizer.TT_EOF; i++)
                  {
                     int k = Integer.parseInt(st.sval);
                     if (k > 127)
                        k -= 256;
                     dataB[i] = (byte) k;
                     for (int j = 0; j < stride - 1 && st.nextToken() != StreamTokenizer.TT_EOF; j++)   //skip to next data item
                        s = st.sval;
                  }
                  break;
               case DataArray.FIELD_DATA_SHORT:
                  dataS = new short[size];
                  for (int i = 0; i < size && st.nextToken() != StreamTokenizer.TT_EOF; i++)
                  {
                     dataS[i] = Short.parseShort(st.sval);
                     for (int j = 0; j < stride - 1 && st.nextToken() != StreamTokenizer.TT_EOF; j++)   //skip to next data item
                        s = st.sval;
                  }
                  break;
            }
         } catch (NumberFormatException ex)
         {
            System.out.println(st.sval + " is not a valid number (VisNow supports only blanks and commas as separators)");
         }
         in.close();
      } catch (FileNotFoundException ex)
      {
         ex.printStackTrace();
      } catch (IOException ex)
      {
         ex.printStackTrace();
      }
      switch (dataType)
      {
         case DataArray.FIELD_DATA_INT:
            return DataArray.create(dataI, 1, label);
         case DataArray.FIELD_DATA_FLOAT:
            return DataArray.create(dataF, 1, label);
         case DataArray.FIELD_DATA_DOUBLE:
            return DataArray.create(dataD, 1, label);
         case DataArray.FIELD_DATA_BYTE:
            return DataArray.create(dataB, 1, label);
         case DataArray.FIELD_DATA_SHORT:
            return DataArray.create(dataS, 1, label);
      }
      System.out.println("improper data type (only byte, int, float and double are supported)");
      return null;
   }

   private DataArray readVariableFromBinary(AVSFieldVariable var, int size, int dataType)
   {

      String path = var.getParameters().get("file");
      int skip = var.getParameters().containsKey("skip") ? Integer.parseInt(var.getParameters().get("skip")) : SKIP_DEFAULT;
      int stride = var.getParameters().containsKey("stride") ? Integer.parseInt(var.getParameters().get("stride")) : STRIDE_DEFAULT;
      String label = var.getParameters().containsKey("label") ? var.getParameters().get("label") : "variable " + var.getParameters().get("id");

      byte[] dataB = null;
      short[] dataS = null;
      int[] dataI = null;
      float[] dataF = null;
      double[] dataD = null;
      int bStride = 1;
      try
      {
         FileImageInputStream inStream = new FileImageInputStream(new File(path));
         DataArray da = null;
         for (int i = 0; i < skip; i++)
            inStream.read();
         inStream.setByteOrder(endian);
         switch (dataType)
         {
            case DataArray.FIELD_DATA_BYTE:
               dataB = new byte[size];
               bStride = stride - 1;
               if (bStride == 0)
               {
                  inStream.readFully(dataB, 0, size);
               } else
               {
                  for (int i = 0; i < size; i++)
                  {
                     dataB[i] = inStream.readByte();
                     if (i < size - 1)   //skip to next data item
                        inStream.skipBytes(bStride);
                  }
               }
               da = DataArray.create(dataB, 1, label);
               break;
            case DataArray.FIELD_DATA_SHORT:
               dataS = new short[size];
               bStride = 2 * (stride - 1);
               if (bStride == 0)
               {
                  inStream.readFully(dataS, 0, size);
               } else
               {
                  for (int i = 0; i < size; i++)
                  {
                     dataS[i] = inStream.readShort();
                     if (i < size - 1)   //skip to next data item
                        inStream.skipBytes(bStride);
                  }
               }
               da = DataArray.create(dataS, 1, label);
               break;
            case DataArray.FIELD_DATA_INT:
               dataI = new int[size];
               bStride = 4 * (stride - 1);
               if (bStride == 0)
               {
                  inStream.readFully(dataI, 0, size);
               } else
               {
                  for (int i = 0; i < size; i++)
                  {
                     dataI[i] = inStream.readInt();
                     if (i < size - 1)   //skip to next data item
                        inStream.skipBytes(bStride);
                  }
               }
               da = DataArray.create(dataI, 1, label);
               break;
            case DataArray.FIELD_DATA_FLOAT:
               dataF = new float[size];
               bStride = 4 * (stride - 1);
               if (bStride == 0)
               {
                  inStream.readFully(dataF, 0, size);
               } else
               {
//                for (int i = 0; i < size; i++)
//                {
//                   dataF[i] = inStream.readFloat();
//                   if (i < size - 1)   //skip to next data item
//                      inStream.skipBytes(bStride);
//                }
                  float[] tmp = new float[stride * (size - 1) + 1];
                  inStream.readFully(tmp, 0, tmp.length);
                  for (int i = 0; i < size; i++)
                  {
                     dataF[i] = tmp[i * stride];
                  }
                  tmp = null;
               }
               da = DataArray.create(dataF, 1, label);
               break;
            case DataArray.FIELD_DATA_DOUBLE:
               dataD = new double[size];
               bStride = 8 * (stride - 1);
               if (bStride == 0)
               {
                  inStream.readFully(dataD, 0, size);
               } else
               {
                  for (int i = 0; i < size; i++)
                  {
                     dataD[i] = inStream.readDouble();
                     if (i < size - 1)   //skip to next data item
                        inStream.skipBytes(bStride);
                  }
               }
               da = DataArray.create(dataD, 1, label);
               break;
         }
         inStream.close();
         return da;
      } catch (FileNotFoundException ex)
      {
         ex.printStackTrace();
      } catch (IOException ex)
      {
         ex.printStackTrace();
      }
      return null;
   }

   private DataArray readVariable(AVSFieldVariable var, int size, int dataType)
   {
      if ("ascii".equalsIgnoreCase(var.getParameters().get("filetype")))
         return readVariableFromASCII(var, size, dataType);
      else if ("binary".equalsIgnoreCase(var.getParameters().get("filetype")))
         return readVariableFromBinary(var, size, dataType);
      else
         System.out.println("only ascii and binary data are supported");
      return null;
   }

   private float[] readCoord(AVSFieldVariable var, int size)
   {
      if ("ascii".equalsIgnoreCase(var.getParameters().get("filetype")))
         return readVariableFromASCII(var, size, DataArray.FIELD_DATA_FLOAT).getFData();
      else if ("binary".equalsIgnoreCase(var.getParameters().get("filetype")))
         return readVariableFromBinary(var, size, DataArray.FIELD_DATA_FLOAT).getFData();
      else
         System.out.println("only ascii and binary data are supported");
      return null;
   }

   public RegularField readAVSField(String path)
   {
      String currentPath = path;
      boolean char12present = false;
      String line = " ";
      FileImageInputStream inStream = null;
      variablesDefined = false;
      parameters = new LinkedHashMap<String, String>();
      variables = new LinkedHashMap<String, AVSFieldVariable>();
      coords = new LinkedHashMap<String, AVSFieldVariable>();

      try
      {
// parsing field file         
         LineNumberReader in = new LineNumberReader(new FileReader(path));
         line = in.readLine();
         if (!(line.startsWith("# AVS")))
         {
            System.out.println("file " + path + " does not start with # AVS and can not be recognized as an AVS field file");
            in.close();
            return null;
         }
         do
         {
            line = in.readLine();
            if (line == null)
               break;
            if (line.matches("\f\f.*"))
            {
               char12present = true;
               break;
            }
            if (line.startsWith("#"))
               continue;
            String s = line.replaceAll(" *= *", "=").replaceFirst("#.*", "").trim();
            parseHeaderLine(s);
         } while (true);
         in.close();
      } catch (Exception e)
      {
         System.out.println("bad header file " + path);
         return null;
      }
// check for required parameters
      try
      {
         ndim = Integer.parseInt(parameters.get("ndim"));
      } catch (Exception e)
      {
         System.out.println("no ndim=... line or bad ndim value in AVS field file " + currentPath);
         return null;
      }

      int size = 1;
      dims = new int[ndim];
      for (int i = 0; i < ndim; i++)
      {
         try
         {
            dims[i] = Integer.parseInt(parameters.get("dim" + (i + 1)));
            size *= dims[i];
         } catch (Exception e)
         {
            System.out.println("no dim" + (i + 1) + "=... line or bad dim" + (i + 1) + "=... line in AVS field file " + currentPath);
            return null;
         }
      }

      try
      {
         nspace = Integer.parseInt(parameters.get("ndim"));
      } catch (Exception e)
      {
         System.out.println("no nspace=... line or bad nspace value in AVS field file " + currentPath);
         return null;
      }

      String dT;
      if (endian == ByteOrder.BIG_ENDIAN)
      {
         String dType = parameters.get("data");
         if (dType == null)
         {
            System.out.println("no data type specified in AVS field file " + currentPath);
            return null;
         }
         if (dType.startsWith("INTEL") || dType.startsWith("intel") || dType.startsWith("x86") || dType.startsWith("X86"))
            endian = ByteOrder.LITTLE_ENDIAN;
         else if (dType.startsWith("XDR") || dType.startsWith("xdr") || !dType.matches(".*_.*"))
            endian = ByteOrder.BIG_ENDIAN;
         if (dType.matches(".*=.*"))
            dT = dType.substring(dType.indexOf('=') + 1);
         else
            dT = dType;
         if (dT.equalsIgnoreCase("byte"))
            dataType = DataArray.FIELD_DATA_BYTE;
         else if (dT.equalsIgnoreCase("short"))
            dataType = DataArray.FIELD_DATA_SHORT;
         else if (dT.equalsIgnoreCase("int") || dT.equalsIgnoreCase("integer") || dT.equalsIgnoreCase("xdr_integer"))
            dataType = DataArray.FIELD_DATA_INT;
         else if (dT.equalsIgnoreCase("float") || dT.equalsIgnoreCase("xdr_float"))
            dataType = DataArray.FIELD_DATA_FLOAT;
         else if (dT.equalsIgnoreCase("double"))
            dataType = DataArray.FIELD_DATA_DOUBLE;
         else
         {
            System.out.println(dType + " is not a valid data type");
            return null;
         }
      }

      dT = parameters.get("field");
      if (dT == null)
      {
         System.out.println("no grid type specified in AVS field file " + currentPath
                 + " assuming uniform geometry");
         geometry = RegularFieldSchema.UNIFORM;
      }
      if (dT.equalsIgnoreCase("uniform"))
         geometry = RegularFieldSchema.UNIFORM;
      else if (dT.equalsIgnoreCase("irregular"))
         geometry = RegularFieldSchema.FREEFORM;
      else if (dT.equalsIgnoreCase("rectilinear"))
      {
         System.out.println("Rectilinear fields are not supported in VisNow. Creating irregular geometry.");
         geometry = RECTILINEAR;
      } else
      {
         System.out.println(dT + " is not a valid field type");
         return null;
      }

      try
      {
         veclen = Integer.parseInt(parameters.get("veclen"));
      } catch (Exception e)
      {
         System.out.println("no veclen=... line or bad veclen value in AVS field file " + currentPath);
         return null;
      }

      line = parameters.get("label");
      if (line != null)
      {
         labels = line.split(" +");
         if (labels.length > veclen)
            System.out.println("too many label items - only first " + veclen + " will be used");
         if (labels.length < veclen)
         {
            System.out.println("some label items missing - variable_n will be used for missing components");
            String[] l = new String[labels.length];
            System.arraycopy(labels, 0, l, 0, l.length);
            labels = new String[veclen];
            System.arraycopy(l, 0, labels, 0, l.length);
            for (int i = l.length; i < veclen; i++)
               labels[i] = "variable" + i;
         }
      } else
      {
         System.out.println("no variable labels specified in AVS field file " + currentPath);
         labels = new String[veclen];
         for (int i = 0; i < veclen; i++)
            labels[i] = "variable" + i;
      }

      line = parameters.get("unit");
      if (line != null)
      {
         units = line.split(" +");
         if (units.length > veclen)
            System.out.println("too many unit items - only first " + veclen + " will be used");
         if (units.length < veclen)
         {
            System.out.println("some unit items missing - variable_n will be used for missing components");
            String[] l = new String[units.length];
            System.arraycopy(units, 0, l, 0, l.length);
            units = new String[veclen];
            System.arraycopy(l, 0, units, 0, l.length);
            for (int i = l.length; i < veclen; i++)
               units[i] = "variable" + i;
         }
      } else
      {
         System.out.println("no variable units specified in AVS field file " + currentPath);
         units = new String[veclen];
         for (int i = 0; i < veclen; i++)
            units[i] = "variable" + i;
      }

      RegularField outField = new RegularField(dims);
      float[][] pts = new float[2][3];
      for (int i = 0; i < ndim; i++)
      {
         pts[0][i] = 0.f;
         pts[1][i] = dims[i] - 1.f;
      }
      try
      {
         if (parameters.get("min_ext") != null)
         {
            String[] min_ext = parameters.get("min_ext").trim().split(" +");
            for (int i = 0; i < min_ext.length; i++)
               pts[0][i] = Float.parseFloat(min_ext[i]);
         }
         if (parameters.get("max_ext") != null)
         {
            String[] max_ext = parameters.get("max_ext").trim().split(" +");
            for (int i = 0; i < max_ext.length; i++)
               pts[1][i] = Float.parseFloat(max_ext[i]);
         }
      } catch (Exception e)
      {
         System.out.println("bad min/max_ext line");
      }

      outField.setExtents(pts);
      if (char12present && !variablesDefined)
      {
         int b;
         try
         {
            inStream = new FileImageInputStream(new File(path));
            do
               b = inStream.read();
            while (b != 12);
            b = inStream.read();
            inStream.setByteOrder(endian);
            switch (dataType)
            {
               case DataArray.FIELD_DATA_BYTE:
                  byte[][] bData = new byte[veclen][size];
                  for (int i = 0; i < size; i++)
                     for (int j = 0; j < veclen; j++)
                        bData[j][i] = inStream.readByte();
                  for (int i = 0; i < veclen; i++)
                     outField.addData(DataArray.create(bData[i], 1, labels[i]));
                  break;
               case DataArray.FIELD_DATA_SHORT:
                  short[][] sData = new short[veclen][size];
                  for (int i = 0; i < size; i++)
                     for (int j = 0; j < veclen; j++)
                        sData[j][i] = inStream.readShort();
                  for (int i = 0; i < veclen; i++)
                     outField.addData(DataArray.create(sData[i], 1, labels[i]));
                  break;
               case DataArray.FIELD_DATA_INT:
                  int[][] iData = new int[veclen][size];
                  for (int i = 0; i < size; i++)
                     for (int j = 0; j < veclen; j++)
                        iData[j][i] = inStream.readInt();
                  for (int i = 0; i < veclen; i++)
                     outField.addData(DataArray.create(iData[i], 1, labels[i]));
                  break;
               case DataArray.FIELD_DATA_FLOAT:
                  float[][] fData = new float[veclen][size];
                  for (int i = 0; i < size; i++)
                     for (int j = 0; j < veclen; j++)
                        fData[j][i] = inStream.readFloat();
                  for (int i = 0; i < veclen; i++)
                     outField.addData(DataArray.create(fData[i], 1, labels[i]));
                  break;
               case DataArray.FIELD_DATA_DOUBLE:
                  double[][] dData = new double[veclen][size];
                  for (int i = 0; i < size; i++)
                     for (int j = 0; j < veclen; j++)
                        dData[j][i] = inStream.readDouble();
                  for (int i = 0; i < veclen; i++)
                     outField.addData(DataArray.create(dData[i], 1, labels[i]));
                  break;
            }

            if (geometry == RegularFieldSchema.UNIFORM)
               try
               {
                  for (int i = 0; i < nspace; i++)
                     for (int j = 0; j < 2; j++)
                        pts[j][i] = inStream.readFloat();
                  outField.setExtents(pts);
                  return outField;
               } catch (EOFException ex)
               {
                  System.out.println("no field extents data in  binary section of " + path
                          + " values from header will be used");
               }
            else
            {
               float[] coord = new float[nspace * size];
               if (geometry == RegularFieldSchema.FREEFORM)
               {
                  for (int i = 0; i < nspace; i++)
                     for (int j = 0; j < size; j++)
                        coord[i + nspace * j] = inStream.readFloat();
                  outField.setCoords(coord);
               } else if (geometry == RECTILINEAR)
               {
                  float[] x;
                  float[] y;
                  float[] z;
                  switch (ndim)
                  {
                     case 1:
                        inStream.readFully(coord, 0, size);
                        break;
                     case 2:
                        x = new float[dims[0]];
                        y = new float[dims[1]];
                        inStream.readFully(x, 0, x.length);
                        inStream.readFully(y, 0, y.length);
                        for (int i = 0, k = 0; i < dims[1]; i++)
                           for (int j = 0; j < dims[0]; j++, k += 2)
                           {
                              coord[k] = x[j];
                              coord[k + 1] = y[i];
                           }
                        break;
                     case 3:
                        x = new float[dims[0]];
                        y = new float[dims[1]];
                        z = new float[dims[2]];
                        inStream.readFully(x, 0, x.length);
                        inStream.readFully(y, 0, y.length);
                        inStream.readFully(z, 0, z.length);
                        for (int i = 0, l = 0; i < dims[2]; i++)
                           for (int j = 0; j < dims[1]; j++)
                              for (int k = 0; k < z.length; k++, l += 3)
                              {
                                 coord[l] = x[k];
                                 coord[l + 1] = y[j];
                                 coord[l + 2] = z[j];
                              }
                        break;
                  }
                  outField.setCoords(coord);
               }
            }
            inStream.close();
            return outField;
         } catch (Exception e)
         {
            System.out.println("error when reading binary data from " + currentPath);
            System.out.println("" + e);
            e.printStackTrace();
         }
         return outField;
      } else
      {
         for (Entry<String, AVSFieldVariable> e : variables.entrySet())
         {
            String pathtmp = e.getValue().getParameters().get("file");
            if ((new File((new File(path)).getParent() + File.separator + pathtmp)).exists())
            {
               File f = new File(path);
               e.getValue().getParameters().put("file", f.getParent() + File.separator + pathtmp);
            }
            outField.addData(readVariable(e.getValue(), size, dataType));
         }
         if (geometry == RegularFieldSchema.FREEFORM)
         {
            float[] coord = new float[nspace * size];
            for (Entry<String, AVSFieldVariable> e : coords.entrySet())
            {
               int coordinate = -1;
               String pathtmp = e.getValue().getParameters().get("file");
               try
               {
                  coordinate = Integer.parseInt(e.getValue().getParameters().get("id")) - 1;
               } catch (Exception ex)
               {
               }
               if (coordinate < 0 || coordinate >= nspace)
               {
                  System.out.println("problems with finding coordinate items in header file");
                  continue;
               }
               if ((new File((new File(path)).getParent() + File.separator + pathtmp)).exists())
               {
                  File f = new File(path);
                  e.getValue().getParameters().put("file", f.getParent() + File.separator + pathtmp);
               }
               float[] c = readCoord(e.getValue(), size);
               for (int i = 0; i < c.length; i++)
                  coord[nspace * i + coordinate] = c[i];
            }
            outField.setCoords(coord);
         }
      }
      return outField;
   }

   public RegularField readField(String path, ByteOrder endian)
   {
      try
      {
         this.endian = endian;
         LineNumberReader in = new LineNumberReader(new FileReader(path));
         String line = " ";
         while (line != null && !line.matches("\f\f.*"))
         {
            line = in.readLine();
            if (line.matches(" *# *AVS.*"))
            {
               in.close();
               return readAVSField(path);
            }
         }
      } catch (Exception e)
      {
         return null;
      }
      return null;
   }

   /**
    * Creates a new instance of ReaderAVSCore
    */
   public Core()
   {
   }
}
