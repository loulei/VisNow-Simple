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

package pl.edu.icm.visnow.lib.types;

import java.util.ArrayList;
import java.util.HashMap;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;

/**
 *
 * @author Bartosz Borucki (babor@icm.edu.pl) University of Warsaw, Interdisciplinary Centre for
 * Mathematical and Computational Modelling
 */
public class VNDataSchema implements VNDataSchemaInterface
{
   public static final int FIELD_DATA_SIMPLE_NUMERIC = 10;
   private boolean empty = false;
   private Class vnDataType = null;
   private boolean vnDataTypeSet = false;
   private boolean isField = false;
   private boolean isFieldSet = false;
   private int nSpace = -1;
   private boolean nSpaceSet = false;
   private boolean isRegular = false;
   private boolean isRegularSet = false;
   private int nDims = -1;
   private boolean nDimsSet = false;
   private int[] dims = null;
   private boolean dimsSet = false;
   private boolean isAffine = false;
   private boolean isAffineSet = false;
   private boolean isCoords = false;
   private boolean isCoordsSet = false;
   private int nData = 0;
   private boolean nDataSet = false;
   private int[] dataVeclens = null;
   private boolean dataVeclenSet = false;
   private boolean dataVeclensSet = false;
   private int[] dataTypes = null;
   private boolean dataTypeSet = false;
   private boolean dataTypesSet = false;
   private String[] dataNames = null;
   private boolean dataNameSet = false;
   private boolean dataNamesSet = false;
   private boolean isIrregular = false;
   private boolean isIrregularSet = false;
   private boolean isCellSets = false;
   private boolean isCellSetsSet = false;
   private int nCellSets = 0;
   private boolean nCellSetsSet = false;
   private int nCellData = 0;
   private boolean nCellDataSet = false;
   private int[] cellDataVeclens = null;
   private boolean cellDataVeclenSet = false;
   private boolean cellDataVeclensSet = false;
   private int[] cellDataTypes = null;
   private boolean cellDataTypeSet = false;
   private boolean cellDataTypesSet = false;
   private String[] cellDataNames = null;
   private boolean cellDataNameSet = false;
   private boolean cellDataNamesSet = false;
   private String[] cellSetNames = null;
   private boolean cellSetNameSet = false;
   private boolean cellSetNamesSet = false;
   private boolean isCellsPoint = false;
   private boolean isCellsPointSet = false;
   private boolean isCellsSegment = false;
   private boolean isCellsSegmentSet = false;
   private boolean isCellsTriangle = false;
   private boolean isCellsTriangleSet = false;
   private boolean isCellsQuad = false;
   private boolean isCellsQuadSet = false;
   private boolean isCellsTetra = false;
   private boolean isCellsTetraSet = false;
   private boolean isCellsPyramid = false;
   private boolean isCellsPyramidSet = false;
   private boolean isCellsPrism = false;
   private boolean isCellsPrismSet = false;
   private boolean isCellsHexa = false;
   private boolean isCellsHexaSet = false;
   private boolean isCells2D = false;
   private boolean isCells2DSet = false;
   private boolean isCells3D = false;
   private boolean isCells3DSet = false;
   private boolean isTime = false;
   private boolean isTimeSet = false;
   public String[] keys = new String[]
   {
      "VNDataType",
      "FIELD",
      "NSPACE",
      "REGULAR",
      "NDIMS",
      "DIMS",
      "AFFINE",
      "COORDS",
      "NDATA",
      "DATA_VECLEN",
      "DATA_VECLENS",
      "DATA_TYPE",
      "DATA_TYPES",
      "DATA_NAME",
      "DATA_NAMES",
      "IRREGULAR",
      "CELLSETS",
      "NCELLSETS",
      "NCELLDATA",
      "CELLDATA_VECLEN",
      "CELLDATA_VECLENS",
      "CELLDATA_TYPE",
      "CELLDATA_TYPES",
      "CELLDATA_NAME",
      "CELLDATA_NAMES",
      "CELLSET_NAME",
      "CELLSET_NAMES",
      "CELLS_POINT",
      "CELLS_SEGMENT",
      "CELLS_TRIANGLE",
      "CELLS_QUAD",
      "CELLS_TETRA",
      "CELLS_PYRAMID",
      "CELLS_PRISM",
      "CELLS_HEXAHEDRON",
      "CELLS_2D",
      "CELLS_3D",
      "TIME"
   };
   private String[] dataTypesString = new String[11];

   public VNDataSchema()
   {
      this.empty = true;
      dataTypesString[DataArray.FIELD_DATA_BOOLEAN] = "BooleanDataArray";
      dataTypesString[DataArray.FIELD_DATA_BYTE] = "ByteDataArray";
      dataTypesString[DataArray.FIELD_DATA_SHORT] = "ShortDataArray";
      dataTypesString[DataArray.FIELD_DATA_INT] = "IntDataArray";
      dataTypesString[DataArray.FIELD_DATA_FLOAT] = "FloatDataArray";
      dataTypesString[DataArray.FIELD_DATA_DOUBLE] = "DoubleDataArray";
      dataTypesString[DataArray.FIELD_DATA_COMPLEX] = "ComplexDataArray";
      dataTypesString[DataArray.FIELD_DATA_STRING] = "StringDataArray";
      dataTypesString[DataArray.FIELD_DATA_LOGIC] = "LogicDataArray";
      dataTypesString[DataArray.FIELD_DATA_OBJECT] = "ObjectDataArray";
      dataTypesString[FIELD_DATA_SIMPLE_NUMERIC] = "SimpleNumeric";
   }

   public VNDataSchema(String... args)
   {
      dataTypesString[DataArray.FIELD_DATA_BOOLEAN] = "BooleanDataArray";
      dataTypesString[DataArray.FIELD_DATA_BYTE] = "ByteDataArray";
      dataTypesString[DataArray.FIELD_DATA_SHORT] = "ShortDataArray";
      dataTypesString[DataArray.FIELD_DATA_INT] = "IntDataArray";
      dataTypesString[DataArray.FIELD_DATA_FLOAT] = "FloatDataArray";
      dataTypesString[DataArray.FIELD_DATA_DOUBLE] = "DoubleDataArray";
      dataTypesString[DataArray.FIELD_DATA_COMPLEX] = "ComplexDataArray";
      dataTypesString[DataArray.FIELD_DATA_STRING] = "StringDataArray";
      dataTypesString[DataArray.FIELD_DATA_LOGIC] = "LogicDataArray";
      dataTypesString[DataArray.FIELD_DATA_OBJECT] = "ObjectDataArray";
      dataTypesString[FIELD_DATA_SIMPLE_NUMERIC] = "SimpleNumeric";

      int nArgs = args.length / 2;
      HashMap<String, String> paramsMap = new HashMap<String, String>();
      for (int i = 0; i < nArgs; i++)
      {
         String param = args[2 * i];
         String value = args[2 * i + 1];
         if (isInKeys(param))
         {
            paramsMap.put(param, value);
         }
      }
      hashMap2Params(paramsMap);
   }

   public VNDataSchema(ArrayList<String[]> args)
   {
      int nArgs = args.size();
      HashMap<String, String> paramsMap = new HashMap<String, String>();
      for (int i = 0; i < nArgs; i++)
      {
         String param = args.get(i)[0];
         String value = args.get(i)[1];
         if (isInKeys(param))
         {
            paramsMap.put(param, value);
         }
      }
      hashMap2Params(paramsMap);
   }

   public ArrayList<String[]> getParamsList()
   {
      ArrayList<String[]> out = new ArrayList<String[]>();

      if (vnDataTypeSet)
      {
         out.add(new String[]
                 {
                    "VNDataType", vnDataType.toString()
                 });
      }
      if (isFieldSet)
      {
         out.add(new String[]
                 {
                    "FIELD", isField ? "true" : "false"
                 });
      }
      if (isTimeSet)
      {
         out.add(new String[]
                 {
                    "TIME", isTime ? "true" : "false"
                 });
      }
      if (nSpaceSet)
      {
         out.add(new String[]
                 {
                    "NSPACE", ("" + nSpace)
                 });
      }
      if (isRegularSet)
      {
         out.add(new String[]
                 {
                    "REGULAR", isRegular ? "true" : "false"
                 });
      }
      if (nDimsSet)
      {
         out.add(new String[]
                 {
                    "NDIMS", ("" + nDims)
                 });
      }
      if (dimsSet)
      {
         String tmp = "";
         for (int i = 0; i < dims.length - 1; i++)
         {
            tmp += "" + dims[i] + ",";
         }
         tmp += "" + dims[dims.length - 1];

         out.add(new String[]
                 {
                    "DIMS", tmp
                 });
      }
      if (isAffineSet)
      {
         out.add(new String[]
                 {
                    "AFFINE", isAffine ? "true" : "false"
                 });
      }
      if (isCoordsSet)
      {
         out.add(new String[]
                 {
                    "COORDS", isCoords ? "true" : "false"
                 });
      }
      if (nDataSet)
      {
         out.add(new String[]
                 {
                    "NDATA", ("" + nData)
                 });
      }
      if (dataVeclenSet)
      {
         out.add(new String[]
                 {
                    "DATA_VECLEN", ("" + dataVeclens[0])
                 });
      }
      if (dataVeclensSet)
      {
         String tmp = "";
         for (int i = 0; i < dataVeclens.length - 1; i++)
         {
            tmp += "" + dataVeclens[i] + ",";
         }
         tmp += "" + dataVeclens[dataVeclens.length - 1];
         out.add(new String[]
                 {
                    "DATA_VECLENS", tmp
                 });
      }

      if (dataTypeSet)
      {
         out.add(new String[]
                 {
                    "DATA_TYPE", ("" + dataTypesString[dataTypes[0]])
                 });
      }
      if (dataTypesSet)
      {
         String tmp = "";
         for (int i = 0; i < dataTypes.length - 1; i++)
         {
            tmp += "" + dataTypesString[dataTypes[i]] + ",";
         }
         tmp += "" + dataTypesString[dataTypes[dataTypes.length - 1]];
         out.add(new String[]
                 {
                    "DATA_TYPES", tmp
                 });
      }
      if (dataNameSet)
      {
         out.add(new String[]
                 {
                    "DATA_NAME", ("" + dataNames[0])
                 });
      }
      if (dataNamesSet)
      {
         String tmp = "";
         for (int i = 0; i < dataNames.length - 1; i++)
         {
            tmp += "" + dataNames[i] + ",";
         }
         tmp += "" + dataNames[dataNames.length - 1];
         out.add(new String[]
                 {
                    "DATA_NAMES", tmp
                 });
      }

      if (isIrregularSet)
      {
         out.add(new String[]
                 {
                    "IRREGULAR", isIrregular ? "true" : "false"
                 });
      }
      if (isCellSets)
      {
         out.add(new String[]
                 {
                    "CELLSETS", isCellSets ? "true" : "false"
                 });
      }
      if (nCellSetsSet)
      {
         out.add(new String[]
                 {
                    "NCELLSETS", ("" + nCellSets)
                 });
      }
      if (nCellDataSet)
      {
         out.add(new String[]
                 {
                    "NCELLDATA", ("" + nCellData)
                 });
      }
      if (cellDataVeclenSet)
      {
         out.add(new String[]
                 {
                    "CELLDATA_VECLEN", ("" + cellDataVeclens[0])
                 });
      }
      if (cellDataVeclensSet)
      {
         String tmp = "";
         for (int i = 0; i < cellDataVeclens.length - 1; i++)
         {
            tmp += "" + cellDataVeclens[i] + ",";
         }
         tmp += "" + cellDataVeclens[cellDataVeclens.length - 1];
         out.add(new String[]
                 {
                    "CELLDATA_VECLENS", tmp
                 });
      }
      if (cellDataTypeSet)
      {
         out.add(new String[]
                 {
                    "CELLDATA_TYPE", ("" + dataTypesString[cellDataTypes[0]])
                 });
      }
      if (cellDataTypesSet)
      {
         String tmp = "";
         for (int i = 0; i < cellDataTypes.length - 1; i++)
         {
            tmp += "" + dataTypesString[cellDataTypes[i]] + ",";
         }
         tmp += "" + dataTypesString[cellDataTypes[cellDataTypes.length - 1]];
         out.add(new String[]
                 {
                    "CELLDATA_TYPES", tmp
                 });
      }
      if (cellDataNameSet)
      {
         out.add(new String[]
                 {
                    "CELLDATA_NAME", ("" + cellDataNames[0])
                 });
      }
      if (cellDataNamesSet)
      {
         String tmp = "";
         for (int i = 0; i < cellDataNames.length - 1; i++)
         {
            tmp += "" + cellDataNames[i] + ",";
         }
         tmp += "" + cellDataNames[cellDataNames.length - 1];
         out.add(new String[]
                 {
                    "CELLDATA_NAMES", tmp
                 });
      }
      if (cellSetNameSet)
      {
         out.add(new String[]
                 {
                    "CELLSET_NAME", ("" + cellSetNames[0])
                 });
      }
      if (cellSetNamesSet)
      {
         String tmp = "";
         for (int i = 0; i < cellSetNames.length - 1; i++)
         {
            tmp += "" + cellSetNames[i] + ",";
         }
         tmp += "" + cellSetNames[cellSetNames.length - 1];
         out.add(new String[]
                 {
                    "CELLSET_NAMES", tmp
                 });
      }

      if (isCellsPointSet)
      {
         out.add(new String[]
                 {
                    "CELLS_POINT", isCellsPoint ? "true" : "false"
                 });
      }
      if (isCellsSegmentSet)
      {
         out.add(new String[]
                 {
                    "CELLS_SEGMENT", isCellsSegment ? "true" : "false"
                 });
      }
      if (isCells2DSet)
      {
         out.add(new String[]
                 {
                    "CELLS_2D", isCells2D ? "true" : "false"
                 });
      }
      if (isCells3DSet)
      {
         out.add(new String[]
                 {
                    "CELLS_3D", isCells3D ? "true" : "false"
                 });
      }
      if (isCellsTriangleSet)
      {
         out.add(new String[]
                 {
                    "CELLS_TRIANGLE", isCellsTriangle ? "true" : "false"
                 });
      }
      if (isCellsQuadSet)
      {
         out.add(new String[]
                 {
                    "CELLS_QUAD", isCellsQuad ? "true" : "false"
                 });
      }
      if (isCellsTetraSet)
      {
         out.add(new String[]
                 {
                    "CELLS_TETRA", isCellsTetra ? "true" : "false"
                 });
      }
      if (isCellsPyramidSet)
      {
         out.add(new String[]
                 {
                    "CELLS_PYRAMID", isCellsPyramid ? "true" : "false"
                 });
      }
      if (isCellsPrismSet)
      {
         out.add(new String[]
                 {
                    "CELLS_PRISM", isCellsPrism ? "true" : "false"
                 });
      }
      if (isCellsHexaSet)
      {
         out.add(new String[]
                 {
                    "CELLS_HEXAHEDRON", isCellsHexa ? "true" : "false"
                 });
      }

      return out;
   }

   private boolean isInKeys(String key)
   {
      if (key == null)
      {
         return false;
      }

      for (int i = 0; i < keys.length; i++)
      {
         if (key.equals(keys[i]))
         {
            return true;
         }
      }
      return false;
   }

   private void hashMap2Params(HashMap<String, String> paramsMap)
   {
      ArrayList<String> tmp = new ArrayList<String>(paramsMap.keySet());
      for (int i = 0; i < tmp.size(); i++)
      {
         String key = tmp.get(i);
         parseParam(key, paramsMap.get(key));
      }

   }

   private void parseParam(String key, String value)
   {
      if (key.equals("VNDataType"))
      {
         if (value != null)
         {
            if (value.equals("VNField"))
            {
               setVnDataType(VNField.class);
            } else if (value.equals("VNRegularField"))
            {
               setVnDataType(VNRegularField.class);
            } else if (value.equals("VNIrregularField"))
            {
               setVnDataType(VNIrregularField.class);
            }
         }
         return;
      }

      if (key.equals("FIELD"))
      {
         if (value != null)
         {
            if (value.equals("true"))
            {
               setIsField(true);
            } else
            {
               setIsField(false);
            }
         }
         return;
      }

      if (key.equals("NSPACE"))
      {
         if (value != null)
         {
            try
            {
               int n = Integer.parseInt(value);
               setNSpace(n);
               setIsField(true);
            } catch (NumberFormatException ex)
            {
               setNSpace(-1);
            }
         }
         return;
      }

      if (key.equals("REGULAR"))
      {
         if (value != null)
         {
            if (value.equals("true"))
            {
               setIsField(true);
               setIsRegular(true);
            } else
            {
               setIsRegular(false);
            }
         }
         return;
      }

      if (key.equals("NDIMS"))
      {
         if (value != null)
         {
            try
            {
               int n = Integer.parseInt(value);
               setNDims(n);
               setIsField(true);
               setIsRegular(true);
               setIsAffine(true);
            } catch (NumberFormatException ex)
            {
               setNDims(-1);
            }
         }
         return;
      }

      if (key.equals("DIMS"))
      {
         if (value != null)
         {
            try
            {
               String[] tmp = value.split(",");
               int[] tmpi = new int[tmp.length];
               try
               {
                  for (int i = 0; i < tmp.length; i++)
                  {
                     tmpi[i] = Integer.parseInt(tmp[i]);
                  }
                  setDims(tmpi);
                  setNDims(tmpi.length);
                  setIsField(true);
                  setIsRegular(true);
                  setIsAffine(true);
               } catch (NumberFormatException ex)
               {
                  setDims(null);
               }
            } catch (NumberFormatException ex)
            {
               setDims(null);
            }
         }
         return;
      }

      if (key.equals("AFFINE"))
      {
         if (value != null)
         {
            if (value.equals("true"))
            {
               setIsAffine(true);
               setIsField(true);
               setIsRegular(true);
            } else
            {
               setIsAffine(false);
            }
         }
         return;
      }

      if (key.equals("COORDS"))
      {
         if (value != null)
         {
            if (value.equals("true"))
            {
               setIsCoords(true);
               setIsField(true);
               setIsRegular(true);
            } else
            {
               setIsCoords(false);
            }
         }
         return;
      }

      if (key.equals("NDATA"))
      {
         if (value != null)
         {
            try
            {
               int n = Integer.parseInt(value);
               setNData(n);
               setIsField(true);
            } catch (NumberFormatException ex)
            {
               setNData(-1);
            }
         }
         return;
      }

      if (key.equals("DATA_VECLENS") || key.equals("DATA_VECLEN"))
      {
         if (value != null)
         {
            String[] tmp = value.split(",");
            int[] tmpi = new int[tmp.length];
            try
            {
               for (int i = 0; i < tmp.length; i++)
               {
                  tmpi[i] = Integer.parseInt(tmp[i]);
               }

               if (key.equals("DATA_VECLENS"))
               {
                  setDataVeclens(tmpi);
                  setNData(tmpi.length);
               } else
               {
                  this.dataVeclens = tmpi;
                  this.dataVeclenSet = true;
               }
               setIsField(true);
            } catch (NumberFormatException ex)
            {
               setDataVeclens(null);
            }
         }
         return;
      }

      if (key.equals("DATA_TYPES") || key.equals("DATA_TYPE"))
      {
         if (value != null)
         {
            String[] tmp = value.split(",");
            int[] tmpi = new int[tmp.length];
            for (int i = 0; i < tmp.length; i++)
            {
               if (tmp[i].equals("BooleanDataArray"))
               {
                  tmpi[i] = DataArray.FIELD_DATA_BOOLEAN;
                  continue;
               }
               if (tmp[i].equals("ByteDataArray"))
               {
                  tmpi[i] = DataArray.FIELD_DATA_BYTE;
                  continue;
               }
               if (tmp[i].equals("ShortDataArray"))
               {
                  tmpi[i] = DataArray.FIELD_DATA_SHORT;
                  continue;
               }
               if (tmp[i].equals("IntDataArray"))
               {
                  tmpi[i] = DataArray.FIELD_DATA_INT;
                  continue;
               }
               if (tmp[i].equals("FloatDataArray"))
               {
                  tmpi[i] = DataArray.FIELD_DATA_FLOAT;
                  continue;
               }
               if (tmp[i].equals("DoubleDataArray"))
               {
                  tmpi[i] = DataArray.FIELD_DATA_DOUBLE;
                  continue;
               }
               if (tmp[i].equals("ComplexDataArray"))
               {
                  tmpi[i] = DataArray.FIELD_DATA_COMPLEX;
                  continue;
               }
               if (tmp[i].equals("LogicDataArray"))
               {
                  tmpi[i] = DataArray.FIELD_DATA_LOGIC;
                  continue;
               }
               if (tmp[i].equals("StringDataArray"))
               {
                  tmpi[i] = DataArray.FIELD_DATA_STRING;
                  continue;
               }
               if (tmp[i].equals("ObjectDataArray"))
               {
                  tmpi[i] = DataArray.FIELD_DATA_OBJECT;
                  continue;
               }
               if (tmp[i].equals("SimpleNumeric"))
               {
                  tmpi[i] = FIELD_DATA_SIMPLE_NUMERIC;
                  continue;
               }
            }

            if (key.equals("DATA_TYPES"))
            {
               setDataTypes(tmpi);
               setNData(tmpi.length);
            } else
            {
               dataTypes = tmpi;
               this.dataTypeSet = true;
            }
            setIsField(true);
         }
         return;
      }

      if (key.equals("DATA_NAMES") || key.equals("DATA_NAME"))
      {
         if (value != null)
         {
            if (key.equals("DATA_NAMES"))
            {
               setDataNames(value.split(","));
               setNData(dataNames.length);
            } else
            {
               dataNames = value.split(",");
               this.dataNameSet = true;
            }
            setIsField(true);
         }
         return;
      }


      //------------------------------------------------------
      if (key.equals("IRREGULAR"))
      {
         if (value != null)
         {
            if (value.equals("true"))
            {
               setIsIrregular(true);
               setIsField(true);
            } else
            {
               setIsIrregular(false);
            }
         }
         return;
      }

      if (key.equals("CELLSETS"))
      {
         if (value != null)
         {
            if (value.equals("true"))
            {
               setIsCellSets(true);
               setIsIrregular(true);
               setIsField(true);
            } else
            {
               setIsCellSets(false);
            }
         }
         return;
      }

      if (key.equals("NCELLSETS"))
      {
         if (value != null)
         {
            try
            {
               int n = Integer.parseInt(value);
               setNCellSets(n);
               setIsIrregular(true);
               setIsField(true);
            } catch (NumberFormatException ex)
            {
               setNCellSets(-1);
            }
         }
         return;
      }

      if (key.equals("NCELLDATA"))
      {
         if (value != null)
         {
            try
            {
               int n = Integer.parseInt(value);
               setNCellData(n);
               setIsCellSets(true);
               setIsIrregular(true);
               setIsField(true);
            } catch (NumberFormatException ex)
            {
               setNCellData(-1);
            }
         }
         return;
      }

      if (key.equals("CELLDATA_VECLENS") || key.equals("CELLDATA_VECLEN"))
      {
         if (value != null)
         {
            String[] tmp = value.split(",");
            int[] tmpi = new int[tmp.length];
            try
            {
               for (int i = 0; i < tmp.length; i++)
               {
                  tmpi[i] = Integer.parseInt(tmp[i]);
               }

               if (key.equals("CELLDATA_VECLENS"))
               {
                  setCellDataVeclens(tmpi);
                  setNCellData(tmpi.length);
               } else
               {
                  this.cellDataVeclens = tmpi;
                  this.cellDataVeclenSet = true;
               }
               setIsCellSets(true);
               setIsIrregular(true);
               setIsField(true);
            } catch (NumberFormatException ex)
            {
               setCellDataVeclens(null);
            }
         }
         return;
      }

      if (key.equals("CELLDATA_TYPES") || key.equals("CELLDATA_TYPE"))
      {
         if (value != null)
         {
            String[] tmp = value.split(",");
            int[] tmpi = new int[tmp.length];
            for (int i = 0; i < tmp.length; i++)
            {
               if (tmp[i].equals("BooleanDataArray"))
               {
                  tmpi[i] = DataArray.FIELD_DATA_BOOLEAN;
                  continue;
               }
               if (tmp[i].equals("ByteDataArray"))
               {
                  tmpi[i] = DataArray.FIELD_DATA_BYTE;
                  continue;
               }
               if (tmp[i].equals("ShortDataArray"))
               {
                  tmpi[i] = DataArray.FIELD_DATA_SHORT;
                  continue;
               }
               if (tmp[i].equals("IntDataArray"))
               {
                  tmpi[i] = DataArray.FIELD_DATA_INT;
                  continue;
               }
               if (tmp[i].equals("FloatDataArray"))
               {
                  tmpi[i] = DataArray.FIELD_DATA_FLOAT;
                  continue;
               }
               if (tmp[i].equals("DoubleDataArray"))
               {
                  tmpi[i] = DataArray.FIELD_DATA_DOUBLE;
                  continue;
               }
               if (tmp[i].equals("ComplexDataArray"))
               {
                  tmpi[i] = DataArray.FIELD_DATA_COMPLEX;
                  continue;
               }
               if (tmp[i].equals("LogicDataArray"))
               {
                  tmpi[i] = DataArray.FIELD_DATA_LOGIC;
                  continue;
               }
               if (tmp[i].equals("StringDataArray"))
               {
                  tmpi[i] = DataArray.FIELD_DATA_STRING;
                  continue;
               }
               if (tmp[i].equals("ObjectDataArray"))
               {
                  tmpi[i] = DataArray.FIELD_DATA_OBJECT;
                  continue;
               }
               if (tmp[i].equals("SimpleNumeric"))
               {
                  tmpi[i] = FIELD_DATA_SIMPLE_NUMERIC;
                  continue;
               }
            }

            if (key.equals("CELLDATA_TYPES"))
            {
               setCellDataTypes(tmpi);
               setNCellData(tmpi.length);
            } else
            {
               this.cellDataTypes = tmpi;
               this.cellDataTypeSet = true;
            }
            setIsCellSets(true);
            setIsIrregular(true);
            setIsField(true);
         }
         return;
      }

      if (key.equals("CELLDATA_NAMES") || key.equals("CELLDATA_NAME"))
      {
         if (value != null)
         {
            if (key.equals("CELLDATA_NAMES"))
            {
               setCellDataNames(value.split(","));
               setNCellData(cellDataNames.length);
            } else
            {
               this.cellDataNames = value.split(",");
               this.cellDataNameSet = true;
            }
            setIsCellSets(true);
            setIsIrregular(true);
            setIsField(true);
         }
         return;
      }

      if (key.equals("CELLSET_NAMES") || key.equals("CELLSET_NAME"))
      {
         if (value != null)
         {
            if (key.equals("CELLSET_NAMES"))
            {
               setCellSetNames(value.split(","));
               setNCellSets(cellSetNames.length);
            } else
            {
               this.cellSetNames = value.split(",");
               this.cellSetNameSet = true;
            }
            setIsCellSets(true);
            setIsIrregular(true);
            setIsField(true);
         }
         return;
      }

      if (key.equals("CELLS_POINT"))
      {
         if (value != null)
         {
            if (value.equals("true"))
            {
               setCellsPoint(true);
               setIsCellSets(true);
               setIsIrregular(true);
               setIsField(true);
            } else
            {
               setCellsPoint(false);
            }
         }
         return;
      }

      if (key.equals("CELLS_SEGMENT"))
      {
         if (value != null)
         {
            if (value.equals("true"))
            {
               setCellsSegment(true);
               setIsCellSets(true);
               setIsIrregular(true);
               setIsField(true);
            } else
            {
               setCellsSegment(false);
            }
         }
         return;
      }

      if (key.equals("CELLS_2D"))
      {
         if (value != null)
         {
            if (value.equals("true"))
            {
               setIsCells2D(true);
               setIsCellSets(true);
               setIsIrregular(true);
               setIsField(true);
            } else
            {
               setIsCells2D(false);
            }
         }
         return;
      }

      if (key.equals("CELLS_TRIANGLE"))
      {
         if (value != null)
         {
            if (value.equals("true"))
            {
               setCellsTriangle(true);
               setIsCells2D(true);
               setIsCellSets(true);
               setIsIrregular(true);
               setIsField(true);
            } else
            {
               setCellsTriangle(false);
            }
         }
         return;
      }

      if (key.equals("CELLS_QUAD"))
      {
         if (value != null)
         {
            if (value.equals("true"))
            {
               setCellsQuad(true);
               setIsCells2D(true);
               setIsCellSets(true);
               setIsIrregular(true);
               setIsField(true);
            } else
            {
               setCellsQuad(false);
            }
         }
         return;
      }

      if (key.equals("CELLS_3D"))
      {
         if (value != null)
         {
            if (value.equals("true"))
            {
               setIsCells3D(true);
               setIsCellSets(true);
               setIsIrregular(true);
               setIsField(true);
            } else
            {
               setIsCells3D(false);
            }
         }
         return;
      }

      if (key.equals("CELLS_TETRA"))
      {
         if (value != null)
         {
            if (value.equals("true"))
            {
               setCellsTetra(true);
               setIsCells3D(true);
               setIsCellSets(true);
               setIsIrregular(true);
               setIsField(true);
            } else
            {
               setCellsTetra(false);
            }
         }
         return;
      }

      if (key.equals("CELLS_PYRAMID"))
      {
         if (value != null)
         {
            if (value.equals("true"))
            {
               setCellsPyramid(true);
               setIsCells3D(true);
               setIsCellSets(true);
               setIsIrregular(true);
               setIsField(true);
            } else
            {
               setCellsPyramid(false);
            }
         }
         return;
      }

      if (key.equals("CELLS_PRISM"))
      {
         if (value != null)
         {
            if (value.equals("true"))
            {
               setCellsPrism(true);
               setIsCells3D(true);
               setIsCellSets(true);
               setIsIrregular(true);
               setIsField(true);
            } else
            {
               setCellsPrism(false);
            }
         }
         return;
      }

      if (key.equals("CELLS_HEXAHEDRON"))
      {
         if (value != null)
         {
            if (value.equals("true"))
            {
               setCellsHexa(true);
               setIsCells3D(true);
               setIsCellSets(true);
               setIsIrregular(true);
               setIsField(true);
            } else
            {
               setCellsHexa(false);
            }
         }
         return;
      }

      if (key.equals("TIME"))
      {
         if (value != null)
         {
            if (value.equals("true"))
            {
               setTime(true);
            } else
            {
               setTime(false);
            }
            setIsField(true);
         }
         return;
      }

   }

   @Override
   public Class getVnDataType()
   {
      return vnDataType;
   }

   @Override
   public boolean isEmpty()
   {
      return empty;
   }

   @Override
   public boolean isField()
   {
      return isField;
   }

   @Override
   public int getNSpace()
   {
      return nSpace;
   }

   @Override
   public boolean isRegular()
   {
      return isRegular;
   }

   @Override
   public int getNDims()
   {
      return nDims;
   }

   @Override
   public int[] getDims()
   {
      return dims;
   }

   @Override
   public boolean isAffine()
   {
      return isAffine;
   }

   @Override
   public boolean isCoords()
   {
      return isCoords;
   }

   @Override
   public int getNData()
   {
      return nData;
   }

   @Override
   public int[] getDataVeclens()
   {
      return dataVeclens;
   }

   @Override
   public int[] getDataTypes()
   {
      return dataTypes;
   }

   @Override
   public String[] getDataNames()
   {
      return dataNames;
   }

   @Override
   public boolean isIrregular()
   {
      return isIrregular;
   }

   @Override
   public boolean hasScalarComponent()
   {
      return hasVectorComponent(1);
   }

   @Override
   public boolean hasVectorComponent(int veclen)
   {
      int[] v = this.getDataVeclens();
      if (v == null)
      {
         return false;
      }
      for (int i = 0; i < v.length; i++)
      {
         if (v[i] == veclen)
         {
            return true;
         }
      }
      return false;
   }

   /**
    * @param vnDataType the vnDataType to set
    */
   private void setVnDataType(Class vnDataType)
   {
      this.vnDataType = vnDataType;
      this.vnDataTypeSet = true;
   }

   /**
    * @param isField the isField to set
    */
   private void setIsField(boolean isField)
   {
      this.isField = isField;
      this.isFieldSet = true;
   }

   /**
    * @param nSpace the nSpace to set
    */
   private void setNSpace(int nSpace)
   {
      this.nSpace = nSpace;
      this.nSpaceSet = true;
   }

   /**
    * @param isRegular the isRegular to set
    */
   private void setIsRegular(boolean isRegular)
   {
      this.isRegular = isRegular;
      if (isRegular == true)
      {
         this.isIrregular = false;
      }
      this.isRegularSet = true;
   }

   /**
    * @param nDims the nDims to set
    */
   private void setNDims(int nDims)
   {
      this.nDims = nDims;
      this.nDimsSet = true;
   }

   /**
    * @param dims the dims to set
    */
   private void setDims(int[] dims)
   {
      this.dims = dims;
      this.dimsSet = true;
   }

   /**
    * @param isAffine the isAffine to set
    */
   private void setIsAffine(boolean isAffine)
   {
      this.isAffine = isAffine;
      if (isAffine)
      {
         this.isCoords = false;
      }
      this.isAffineSet = true;
   }

   /**
    * @param isCoords the isCoords to set
    */
   private void setIsCoords(boolean isCoords)
   {
      this.isCoords = isCoords;
      if (isCoords)
      {
         this.isAffine = false;
      }
      this.isCoordsSet = true;
   }

   /**
    * @param nData the nData to set
    */
   private void setNData(int nData)
   {
      this.nData = nData;
      this.nDataSet = true;
   }

   /**
    * @param dataVeclens the dataVeclens to set
    */
   private void setDataVeclens(int[] dataVeclens)
   {
      this.dataVeclens = dataVeclens;
      this.dataVeclensSet = (dataVeclens != null);

   }

   /**
    * @param dataTypes the dataTypes to set
    */
   private void setDataTypes(int[] dataTypes)
   {
      this.dataTypes = dataTypes;
      this.dataTypesSet = (dataTypes != null);
   }

   /**
    * @param dataNames the dataNames to set
    */
   private void setDataNames(String[] dataNames)
   {
      this.dataNames = dataNames;
      this.dataNamesSet = (dataNames != null);
   }

   /**
    * @param isIrregular the isIrregular to set
    */
   private void setIsIrregular(boolean isIrregular)
   {
      this.isIrregular = isIrregular;
      if (isIrregular)
      {
         this.isRegular = false;
      }
      this.isIrregularSet = true;
   }

   private void setIsCellSets(boolean is)
   {
      this.isCellSets = is;
      this.isCellSetsSet = true;
   }

   @Override
   public boolean isCellSets()
   {
      return this.isCellSets;
   }

   @Override
   public String toString()
   {
      return getDescription("; ", "");
   }

   public String toHtmlString()
   {
      return getDescription("<br>","");
   }

   private String getDescription(String newline, String tab)
   {
      String str = "";

      if (isFieldSet && !isRegularSet && !isIrregularSet)
      {
         str += "Field ";
         if (nSpaceSet)
         {
            str += "" + nSpace + "-space ";
         }

         if (isTimeSet && isTime)
         {
            str += "with time frames ";
         }

         if (isTimeSet && !isTime)
         {
            str += "without time frames ";
         }
         
         return str;
      }


      if (isRegularSet && isRegular)
      {
         str += "Regular Field ";

         if (nDimsSet)
         {
            str += "" + nDims + "D ";
         }

         if (nSpaceSet)
         {
            str += "" + nSpace + "-space ";
         }

         if (dimsSet && dims != null)
         {
            str += "dimensions = {";
            for (int i = 0; i < dims.length; i++)
            {
               str += "" + dims[i];
               if (i < dims.length - 1)
               {
                  str += ",";
               }
            }
            str += "} ";
         }

         if (isAffineSet && isAffine)
         {
            str += "with affine geometry ";
         }

         if (isCoordsSet && isCoords)
         {
            str += "with explicit coordinates ";
         }

         if (isTimeSet && isTime)
         {
            str += "with time frames ";
         }

         if (isTimeSet && !isTime)
         {
            str += "without time frames ";
         }
         
         str += newline;

      }

      if (isIrregularSet && isIrregular)
      {
         str += "Irregular Field ";

         if (nSpaceSet)
         {
            str += "" + nSpace + "-space";
         }

         if (isTimeSet && isTime)
         {
            str += "with time frames ";
         }

         if (isTimeSet && !isTime)
         {
            str += "without time frames ";
         }
         
         str += newline;
      }


      if (isFieldSet && isField)
      {
         if (nDataSet)
         {
            str += tab + nData + " components" + newline;
         }

         if (dataVeclenSet && dataVeclens != null)
         {
            for (int i = 0; i < dataVeclens.length; i++)
            {
               //str += "at least one " + (dataVeclens[i] == 1 ? "scalar" : ("veclen=" + dataVeclens[i])) + " component" + newline;
                str += tab + (dataVeclens[i] == 1 ? "scalar" : ("veclen=" + dataVeclens[i])) + " component" + newline;
            }
         } else if (dataVeclensSet && dataVeclens != null)
         {
            str += tab + "with veclen=";
            for (int i = 0; i < dataVeclens.length; i++)
            {
               str += "" + dataVeclens[i];
               if (i < dataVeclens.length - 1)
               {
                  str += ",";
               }
            }
            str += newline;
         }

         if (dataTypeSet && dataTypes != null)
         {
            for (int i = 0; i < dataTypes.length; i++)
            {
               //str += "at least one component of type " + DataArray.getTypeName(dataTypes[i]) + newline;
                str += tab + "component of type " + DataArray.getTypeName(dataTypes[i]) + newline;
            }
         } else if (dataTypesSet && dataTypes != null)
         {
            str += tab + "with types=";
            for (int i = 0; i < dataTypes.length; i++)
            {
               str += "" + DataArray.getTypeName(dataTypes[i]);
               if (i < dataTypes.length - 1)
               {
                  str += ",";
               }
            }
            str += newline;
         }

         if (dataNameSet && dataNames != null)
         {
            for (int i = 0; i < dataNames.length; i++)
            {
               //str += "at least one component named '" + dataNames[i] + "'" + newline;
                str += tab + "component named '" + dataNames[i] + "'" + newline;
            }
         } else if (dataNamesSet && dataNames != null)
         {
            str += tab + "with names=";
            for (int i = 0; i < dataNames.length; i++)
            {
               str += "" + dataNames[i];
               if (i < dataNames.length - 1)
               {
                  str += ",";
               }
            }
            str += newline;
         }
      }

      if (isIrregularSet && isIrregular && isCellSetsSet && isCellSets)
      {
         str += tab + nCellSets + " cell sets" + newline;

         if (cellSetNameSet && cellSetNames != null)
         {
            for (int i = 0; i < cellSetNames.length; i++)
            {
               //str += "at least one cell set named '" + cellSetNames[i] + "'" + newline;
                str += tab + "cell set named '" + cellSetNames[i] + "'" + newline;
            }
         }

         if (cellSetNamesSet && cellSetNames != null)
         {
            str += tab + "with names=";
            for (int i = 0; i < cellSetNames.length; i++)
            {
               str += "" + cellSetNames[i];
               if (i < cellSetNames.length - 1)
               {
                  str += ",";
               }
            }
            str += newline;
         }

         if (nCellDataSet && nCellData != 0)
         {
            str += tab + nCellData + " cell components in cellsets" + newline;
         }

         if (cellDataVeclenSet && cellDataVeclens != null)
         {
            for (int i = 0; i < cellDataVeclens.length; i++)
            {
               //str += "at least one " + (cellDataVeclens[i] == 1 ? "scalar" : ("veclen=" + cellDataVeclens[i])) + " cell component" + newline;
                str += tab + (cellDataVeclens[i] == 1 ? "scalar" : ("veclen=" + cellDataVeclens[i])) + " cell component" + newline;
            }
         }

         if (cellDataVeclensSet && cellDataVeclens != null)
         {
            str += tab + "with cellset veclen=";
            for (int j = 0; j < cellDataVeclens.length; j++)
            {
               str += "" + cellDataVeclens[j];
               if (j < cellDataVeclens.length - 1)
               {
                  str += ",";
               }
            }
            str += newline;

         }

         if (cellDataTypeSet && cellDataTypes != null)
         {
            for (int i = 0; i < cellDataTypes.length; i++)
            {
               //str += "at least one cell component of type " + DataArray.getTypeName(cellDataTypes[i]) + newline;
                str += tab + "cell component of type " + DataArray.getTypeName(cellDataTypes[i]) + newline;
            }
         }

         if (cellDataTypesSet && cellDataTypes != null)
         {
            str += tab + "with types=";
            for (int j = 0; j < cellDataTypes.length; j++)
            {
               str += "" + DataArray.getTypeName(cellDataTypes[j]);
               if (j < cellDataTypes.length - 1)
               {
                  str += ",";
               }
            }
            str += newline;
         }

         if (cellDataNameSet && cellDataNames != null)
         {
            for (int i = 0; i < cellDataNames.length; i++)
            {
               //str += "at least one cell component named '" + cellDataNames[i] + "'" + newline;
                str += tab + "cell component named '" + cellDataNames[i] + "'" + newline;
            }
         }

         if (cellDataNamesSet && cellDataNames != null)
         {
            str += tab + "with names=";
            for (int j = 0; j < cellDataNames.length; j++)
            {
               str += "" + cellDataNames[j];
               if (j < cellDataNames.length - 1)
               {
                  str += ",";
               }
            }
            str += newline;
         }

         //str += newline;

         if (isCellsPointSet && isCellsPoint)
         {
            //str += "at least one with POINT cells" + newline;
             str += tab + "with POINT cells" + newline;
         }

         if (isCellsSegmentSet && isCellsSegment)
         {
            //str += "at least one with SEGMENT cells" + newline;
             str += tab + "with SEGMENT cells" + newline;
         }

         if (isCells2DSet && isCells2D)
         {
            //str += "at least one with 2D cells" + newline;
             str += tab + "with 2D cells" + newline;
         }

         if (isCells3DSet && isCells3D)
         {
            //str += "at least one with 3D cells" + newline;
             str += tab + "with 3D cells" + newline;
         }

         if (isCellsTriangleSet && isCellsTriangle)
         {
            //str += "at least one with TRIANGLE cells" + newline;
             str += tab + "with TRIANGLE cells" + newline;
         }

         if (isCellsQuadSet && isCellsQuad)
         {
            //str += "at least one with QUAD cells" + newline;
             str += tab + "with QUAD cells" + newline;
         }

         if (isCellsTetraSet && isCellsTetra)
         {
            //str += "at least one with TETRA cells" + newline;
             str += tab + "with TETRA cells" + newline;
         }

         if (isCellsPyramidSet && isCellsPyramid)
         {
            //str += "at least one with PYRAMID cells" + newline;
             str += tab + "with PYRAMID cells" + newline;
         }

         if (isCellsPrismSet && isCellsPrism)
         {
            //str += "at least one with PRISM cells" + newline;
             str += tab + "with PRISM cells" + newline;
         }

         if (isCellsHexaSet && isCellsHexa)
         {
            //str += "at least one with HEXAHEDRON cells" + newline;
             str += tab + "with HEXAHEDRON cells" + newline;
         }
      }

      if (str.endsWith(newline))
      {
         str = str.substring(0, str.lastIndexOf(newline));
      }

      return str;
   }

   /**
    * @return the nCellSets
    */
   @Override
   public int getNCellSets()
   {
      return nCellSets;
   }

   /**
    * @param nCellSets the nCellSets to set
    */
   private void setNCellSets(int nCellSets)
   {
      this.nCellSets = nCellSets;
      this.nCellSetsSet = true;
   }

   /**
    * @return the nCellData
    */
   @Override
   public int[] getNCellData()
   {
      return new int[]
              {
                 nCellData
              };
   }

   /**
    * @param nCellData the nCellData to set
    */
   private void setNCellData(int nCellData)
   {
      this.nCellData = nCellData;
      this.nCellDataSet = true;
   }

   /**
    * @return the cellDataVeclens
    */
   @Override
   public int[][] getCellDataVeclens()
   {
      return new int[][]
              {
                 cellDataVeclens
              };
   }

   /**
    * @param cellDataVeclens the cellDataVeclens to set
    */
   private void setCellDataVeclens(int[] cellDataVeclens)
   {
      this.cellDataVeclens = cellDataVeclens;
      this.cellDataVeclenSet = (cellDataVeclens != null);
   }

   /**
    * @return the cellDataTypes
    */
   @Override
   public int[][] getCellDataTypes()
   {
      return new int[][]
              {
                 cellDataTypes
              };
   }

   /**
    * @param cellDataTypes the cellDataTypes to set
    */
   private void setCellDataTypes(int[] cellDataTypes)
   {
      this.cellDataTypes = cellDataTypes;
      this.cellDataTypeSet = (cellDataTypes != null);
   }

   /**
    * @return the cellDataNames
    */
   @Override
   public String[][] getCellDataNames()
   {
      return new String[][]
              {
                 cellDataNames
              };
   }

   /**
    * @param cellDataNames the cellDataNames to set
    */
   private void setCellDataNames(String[] cellDataNames)
   {
      this.cellDataNames = cellDataNames;
      this.cellDataNameSet = (cellDataNames != null);
   }

   /**
    * @return the cellSetNames
    */
   @Override
   public String[] getCellSetNames()
   {
      return cellSetNames;
   }

   /**
    * @param cellSetNames the cellSetNames to set
    */
   private void setCellSetNames(String[] cellSetNames)
   {
      this.cellSetNames = cellSetNames;
      this.cellSetNameSet = (cellSetNames != null);
   }

   /**
    * @param isCellsPoint the isCellsPoint to set
    */
   private void setCellsPoint(boolean isCellsPoint)
   {
      this.isCellsPoint = isCellsPoint;
      this.isCellsPointSet = true;
   }

   /**
    * @param isCellsSegment the isCellsSegment to set
    */
   private void setCellsSegment(boolean isCellsSegment)
   {
      this.isCellsSegment = isCellsSegment;
      this.isCellsSegmentSet = true;
   }

   /**
    * @param isCellsTriangle the isCellsTriangle to set
    */
   private void setCellsTriangle(boolean isCellsTriangle)
   {
      this.isCellsTriangle = isCellsTriangle;
      this.isCellsTriangleSet = true;
   }

   /**
    * @param isCellsQuad the isCellsQuad to set
    */
   private void setCellsQuad(boolean isCellsQuad)
   {
      this.isCellsQuad = isCellsQuad;
      this.isCellsQuadSet = true;
   }

   /**
    * @param isCellsTetra the isCellsTetra to set
    */
   private void setCellsTetra(boolean isCellsTetra)
   {
      this.isCellsTetra = isCellsTetra;
      this.isCellsTetraSet = true;
   }

   /**
    * @param isCellsPyramid the isCellsPyramid to set
    */
   private void setCellsPyramid(boolean isCellsPyramid)
   {
      this.isCellsPyramid = isCellsPyramid;
      this.isCellsPyramidSet = true;
   }

   /**
    * @param isCellsPrism the isCellsPrism to set
    */
   private void setCellsPrism(boolean isCellsPrism)
   {
      this.isCellsPrism = isCellsPrism;
      this.isCellsPrismSet = true;
   }

   /**
    * @param isCellsHexa the isCellsHexa to set
    */
   private void setCellsHexa(boolean isCellsHexa)
   {
      this.isCellsHexa = isCellsHexa;
      this.isCellsHexaSet = true;
   }

   @Override
   public boolean hasCellScalarComponent()
   {
      return hasCellVectorComponent(1);
   }

   @Override
   public boolean hasCellVectorComponent(int veclen)
   {
      int[][] v = this.getCellDataVeclens();
      if (v == null || v[0] == null)
      {
         return false;
      }
      for (int i = 0; i < v.length; i++)
      {
         for (int j = 0; j < v[i].length; j++)
         {
            if (v[i][j] == veclen)
            {
               return true;
            }
         }
      }
      return false;
   }

   @Override
   public boolean hasCellsPoint()
   {
      return isCellsPoint;
   }

   @Override
   public boolean hasCellsSegment()
   {
      return isCellsSegment;
   }

   @Override
   public boolean hasCellsTriangle()
   {
      return isCellsTriangle;
   }

   @Override
   public boolean hasCellsQuad()
   {
      return isCellsQuad;
   }

   @Override
   public boolean hasCellsTetra()
   {
      return isCellsTetra;
   }

   @Override
   public boolean hasCellsPyramid()
   {
      return isCellsPyramid;
   }

   @Override
   public boolean hasCellsPrism()
   {
      return isCellsPrism;
   }

   @Override
   public boolean hasCellsHexahedron()
   {
      return isCellsHexa;
   }

   /**
    * @return the isTime
    */
   @Override
   public boolean isTime()
   {
      return isTime;
   }

   /**
    * @param isTime the isTime to set
    */
   private void setTime(boolean isTime)
   {
      this.isTime = isTime;
      this.isTimeSet = true;
   }

   /**
    * @return the isCells2D
    */
   @Override
   public boolean hasCells2D()
   {
      return isCells2D;
   }

   /**
    * @param isCells2D the isCells2D to set
    */
   public void setIsCells2D(boolean isCells2D)
   {
      this.isCells2D = isCells2D;
   }

   /**
    * @return the isCells3D
    */
   @Override
   public boolean hasCells3D()
   {
      return isCells3D;
   }

   /**
    * @param isCells3D the isCells3D to set
    */
   public void setIsCells3D(boolean isCells3D)
   {
      this.isCells3D = isCells3D;
   }

   @Override
   public void createStats()
   {
   }   
}
