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
package pl.edu.icm.visnow.lib.utils.vtk;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pl.edu.icm.visnow.datasets.*;
import pl.edu.icm.visnow.datasets.cells.Cell;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;

/**
 *
 * @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
abstract public class Reader {

    class Polydata {

        public int type;
        public int nItems;
        public int[] verts;

        public Polydata(int type, int nItems, int[] verts) {
            this.type = type;
            this.nItems = nItems;
            this.verts = verts;
        }
    }
    protected static final int STRUCTURED_POINTS = 0;
    protected static final int STRUCTURED_GRID = 1;
    protected static final int RECTILINEAR_GRID = 2;
    protected static final int UNSTRUCTURED_GRID = 3;
    protected static final int POLYDATA = 4;
    protected static final int VERTICES = 0;
    protected static final int LINES = 1;
    protected static final int POLYGONS = 2;
    protected static final int TRIANGLE_STRIPS = 3;
    protected static boolean initialized = false;
    protected static Map<String, Integer> VTKFieldTypeMap = new HashMap<String, Integer>();
    protected static Map<String, Integer> VTKDataTypeMap = new HashMap<String, Integer>();
    protected static Map<String, Integer> VTKPolyTypeMap = new HashMap<String, Integer>();
    protected static final int[] VTKcellTypes = {
        -1,
        Cell.POINT, Cell.POINT,
        Cell.SEGMENT, Cell.SEGMENT,
        Cell.TRIANGLE, Cell.TRIANGLE, Cell.TRIANGLE, Cell.QUAD, Cell.QUAD,
        Cell.TETRA, Cell.HEXAHEDRON, Cell.HEXAHEDRON
    };
    protected static final int[][] VTKnodeOrders = {
        {0},
        {0},
        {0, 1},
        {0, 1},
        {0, 1, 2},
        {0, 1, 2},
        {0, 1, 2},
        {0, 1, 2, 3},
        {0, 1, 3, 2},
        {0, 1, 2, 3},
        {4, 0, 3, 2, 1},
        {0, 1, 3, 2, 4, 5, 7, 6},
        {0, 1, 2, 3, 4, 5, 6, 7}
    };
    protected static final int[] VTKcellSets = {
        -999, 1, 0, 1, -1, 1, -2, -1, 1, 1, 1, 1, 1
    };
    protected static Cell[] stdCells = new Cell[Cell.TYPES];
    protected int VTKFieldType = -1;
    protected int nNodes = 0;
    protected int nCellsItems = 0;
    protected int nCells = 0;
    protected int nCellData = 0;
    protected int nNodeData = 0;
    protected String outFieldName = "";

    public Reader() {
        if (!initialized) {
            initialized = true;
            for (int i = 0; i < stdCells.length; i++) {
                stdCells[i] = Cell.createCell(i, 3, new int[Cell.nv[i]], true);
            }
            VTKDataTypeMap.put("bit", DataArray.FIELD_DATA_BOOLEAN);
            VTKDataTypeMap.put("char", DataArray.FIELD_DATA_BYTE);
            VTKDataTypeMap.put("unsigned_char", DataArray.FIELD_DATA_BYTE);
            VTKDataTypeMap.put("short", DataArray.FIELD_DATA_SHORT);
            VTKDataTypeMap.put("int", DataArray.FIELD_DATA_INT);
            VTKDataTypeMap.put("integer", DataArray.FIELD_DATA_INT);
            VTKDataTypeMap.put("float", DataArray.FIELD_DATA_FLOAT);
            VTKDataTypeMap.put("double", DataArray.FIELD_DATA_DOUBLE);

            VTKFieldTypeMap.put("STRUCTURED_POINTS", STRUCTURED_POINTS);
            VTKFieldTypeMap.put("STRUCTURED_GRID", STRUCTURED_GRID);
            VTKFieldTypeMap.put("RECTILINEAR_GRID", RECTILINEAR_GRID);
            VTKFieldTypeMap.put("UNSTRUCTURED_GRID", UNSTRUCTURED_GRID);
            VTKFieldTypeMap.put("POLYDATA", POLYDATA);

            VTKPolyTypeMap.put("VERTICES", VERTICES);
            VTKPolyTypeMap.put("LINES", LINES);
            VTKPolyTypeMap.put("POLYGONS", POLYGONS);
            VTKPolyTypeMap.put("TRIANGLE_STRIPS", TRIANGLE_STRIPS);
        }
    }

    protected void addRectilinearCoords(RegularField outField) {
        int[] dims = outField.getDims();
        int type;
        String line = nextLine(new String[]{"X_COORDINATES"});
        String[] tokens = line.split("\\s+");
        int n = Integer.parseInt(tokens[1]);
        if (n != dims[0]) {
            return;
        }
        if (VTKDataTypeMap.containsKey(tokens[2].toLowerCase())) {
            type = VTKDataTypeMap.get(tokens[2].toLowerCase());
        } else {
            System.out.println("invalid vtk data type in " + line);
            return;
        }
        float[] cx = new float[n];
        readFloatArrayFrom(cx, type);
        outField.setRectilinearCoords(0, cx);
        line = nextLine(new String[]{"Y_COORDINATES"});
        tokens = line.split("\\s+");
        n = Integer.parseInt(tokens[1]);
        if (n != dims[1]) {
            return;
        }
        float[] cy = new float[n];
        readFloatArrayFrom(cy, type);
        outField.setRectilinearCoords(1, cy);

        if (dims.length > 2) {
            line = nextLine(new String[]{"Z_COORDINATES"});
            tokens = line.split("\\s+");
            n = Integer.parseInt(tokens[1]);
            if (n != dims[2]) {
                return;
            }
            float[] cz = new float[n];
            readFloatArrayFrom(cz, type);
            outField.setRectilinearCoords(2, cz);
        }
    }

    protected float[] readCoords(int n) {
        String line = nextLine(new String[]{"POINTS"});
        String[] tokens = line.split("\\s+");
        nNodes = Integer.parseInt(tokens[1]);
        int type;
        float[] coords = new float[3 * nNodes];
        if (n > 0 && nNodes != n) {
            return null;
        }
        if (VTKDataTypeMap.containsKey(tokens[2].toLowerCase())) {
            type = VTKDataTypeMap.get(tokens[2].toLowerCase());
        } else {
            System.out.println("invalid vtk data type in " + line);
            return null;
        }
        readFloatArrayFrom(coords, type);
        return coords;
    }

    protected void buildCells(CellSet cs, int nVerts, int nSegments, int nTriangles, int nQuads, List<Polydata> polys) {
        int[] ptNodes = new int[nVerts];
        int[] ptIndices = new int[nVerts];
        int[] segNodes = new int[2 * nSegments];
        int[] segIndices = new int[nSegments];
        int[] triNodes = new int[3 * nTriangles];
        int[] triIndices = new int[nTriangles];
        int[] quadNodes = new int[4 * nQuads];
        int[] quadIndices = new int[nQuads];

        int index = 0;
        int kvert = 0;
        int kline = 0;
        int ktri = 0;
        int kquad = 0;
        for (int i = 0; i < polys.size(); i++, index++) {
            int type = polys.get(i).type;
            int nItems = polys.get(i).nItems;
            int[] data = polys.get(i).verts;

            switch (type) {
                case VERTICES:
                    for (int j = 0; j < data.length;) {
                        int n = data[j];
                        j += 1;
                        for (int l = 0; l < n; l++, j++, kvert++) {
                            ptNodes[kvert] = data[j];
                            ptIndices[kvert] = index;
                        }
                    }
                    break;
                case LINES:
                    for (int j = 0; j < data.length; j++) {
                        int n = data[j];
                        j += 1;
                        for (int l = 0; l < n - 1; l++, j++, kline += 2) {
                            segNodes[kline] = data[j];
                            segNodes[kline + 1] = data[j + 1];
                            segIndices[kline / 2] = index;
                        }
                    }
                    break;
                case POLYGONS:
                    for (int j = 0; j < data.length;) {
                        int n = data[j];
                        j += 1;
                        switch (n) {
                            case 3:
                                for (int l = 0; l < 3; l++, j++, ktri++) {
                                    triNodes[ktri] = data[j];
                                    triIndices[ktri / 3] = index;
                                }
                                break;
                            case 4:
                                for (int l = 0; l < 4; l++, j++, kquad++) {
                                    quadNodes[kquad] = data[j];
                                    quadIndices[kquad / 4] = index;
                                }
                                break;
                            default:
                                int l0 = 0;
                                int l1 = n - 1;
                                int l2 = 1;
                                for (int l = 0; l < n - 2; l++, ktri += 3) {
                                    triNodes[ktri] = data[j + l0];
                                    triNodes[ktri + 1] = data[j + l1];
                                    triNodes[ktri + 2] = data[j + l2];
                                    triIndices[ktri / 3] = index;
                                    if (l % 2 == 0) {
                                        l0 = l2;
                                        l2 = l1 - 1;
                                    } else {
                                        l1 = l2;
                                        l2 = l0 + 1;
                                    }
                                }
                                j += n;
                        }
                        index += 1;
                    }
                    break;
                case TRIANGLE_STRIPS:
                    for (int j = 0; j < data.length - 3; j++) {
                        int n = data[j];
                        j += 1;
                        for (int l = 0; l < n - 2; l++, j++, ktri += 3) {
                            triNodes[ktri] = data[j];
                            triNodes[ktri + 1] = data[j + 1];
                            triNodes[ktri + 2] = data[j + 2];
                            triIndices[ktri / 3] = index;
                        }
                        j += 1;
                    }
            }
        }

        if (nVerts > 0) {
            boolean[] ptOrients = new boolean[nVerts];
            for (int i = 0; i < ptOrients.length; i++) {
                ptOrients[i] = true;
            }
            CellArray pointArray = new CellArray(Cell.POINT, ptNodes, ptOrients, ptIndices);
            cs.setCellArray(pointArray);
        }

        if (nSegments > 0) {
            boolean[] segOrients = new boolean[nSegments];
            for (int i = 0; i < segOrients.length; i++) {
                segOrients[i] = true;
            }
            CellArray segArray = new CellArray(Cell.SEGMENT, segNodes, segOrients, segIndices);
            cs.setCellArray(segArray);
        }

        if (nTriangles > 0) {
            boolean[] triOrients = new boolean[nTriangles];
            for (int i = 0; i < triOrients.length; i++) {
                triOrients[i] = true;
            }
            CellArray triArray = new CellArray(Cell.TRIANGLE, triNodes, triOrients, triIndices);
            cs.setCellArray(triArray);
        }

        if (nQuads > 0) {
            boolean[] quadOrients = new boolean[nQuads];
            for (int i = 0; i < quadOrients.length; i++) {
                quadOrients[i] = true;
            }
            CellArray quadArray = new CellArray(Cell.QUAD, quadNodes, quadOrients, quadIndices);
            cs.setCellArray(quadArray);
        }
    }

    protected void addVNFFieldData(DataContainer container, String l) {
        String name = "";
        int vlen = 1;
        int nData = 0;
        int type = DataArray.FIELD_DATA_UNKNOWN;
        String line = l;
        String[] tokens = line.split("\\s+");
        int nDataInField = Integer.parseInt(tokens[2]);
        for (int iData = 0; iData < nDataInField; iData++) {
            line = nextLine();
            tokens = line.split("\\s+");
            if (tokens.length < 4) {
                return;
            }
            name = tokens[0];
            vlen = Integer.parseInt(tokens[1]);
            nData = Integer.parseInt(tokens[2]);
            if (VTKDataTypeMap.containsKey(tokens[3].toLowerCase())) {
                type = VTKDataTypeMap.get(tokens[3].toLowerCase());
            } else {
                System.out.println("invalid vtk data type in " + line);
                return;
            }
            container.addData(readData(type, vlen, nData, name));
        }
    }

    protected RegularField readRegularFile() {
        RegularField outField = null;
        String line = nextLine();
        String[] tokens = line.split("\\s+");
        if (tokens.length < 4 || !tokens[0].equalsIgnoreCase("DIMENSIONS")) {
            return null;
        }
        try {
            int[] dims;
            int length = 1;
            if (Integer.parseInt(tokens[3]) <= 1) {
                dims = new int[2];
                for (int i = 0; i < dims.length; i++) {
                    dims[i] = Integer.parseInt(tokens[i + 1]);
                    length *= dims[i];
                }
                outField = new RegularField(dims);
                outField.setNSpace(3);
            } else {
                dims = new int[3];
                for (int i = 0; i < dims.length; i++) {
                    dims[i] = Integer.parseInt(tokens[i + 1]);
                    length *= dims[i];
                }
                outField = new RegularField(dims);
            }
            outField.setName(outFieldName);
            switch (VTKFieldType) {
                case STRUCTURED_POINTS:
                    float[][] affine = new float[4][3];
                    for (int k = 0; k < 2; k++) {
                        line = nextLine(new String[]{"ORIGIN", "SPACING", "ASPECT_RATIO"});
                        tokens = line.split("\\s+");
                        if (tokens[0].equalsIgnoreCase("ORIGIN")) {
                            for (int i = 0; i < affine[3].length; i++) {
                                affine[3][i] = Float.parseFloat(tokens[i + 1]);
                            }
                        } else {
                            for (int i = 0; i < affine[3].length; i++) {
                                affine[i][i] = Float.parseFloat(tokens[i + 1]);
                            }
                        }
                        outField.setAffine(affine);
                    }
                    break;
                case STRUCTURED_GRID:
                    float[] coords = readCoords(length);
                    outField.setCoords(coords);
//             float[][] affine = coordsToAffine(dims, nSpace, coords);
//             if(affine == null)
//                    outField.setCoords(coords);
//             else
//                    outField.setAffine(affine);
                    break;
                case RECTILINEAR_GRID:
                    addRectilinearCoords(outField);
                    break;
            }

            boolean nodeData = false;
            line = nextLine(new String[]{"POINT_DATA", "CELL_DATA"});
            for (int dataLoc = 0; dataLoc < 2; dataLoc++) {
                tokens = line.split("\\s+");
                if (tokens.length < 2) {
                    System.out.println("bad DATA line " + line);
                    return outField;
                }
                nodeData = tokens[0].equalsIgnoreCase("POINT_DATA");
                int nData = Integer.parseInt(tokens[1]);
                while ((line = nextLine(new String[]{"POINT_DATA", "CELL_DATA",
                    "FIELD",
                    "SCALARS", "COLOR_SCALARS", "LOOKUP_TABLE",
                    "VECTORS", "NORMALS", "TEXTURE_COORDINATES", "TENSORS"})) != null) {
                    if (line.toUpperCase().startsWith("POINT_DATA") || line.toUpperCase().startsWith("CELL_DATA")) {
                        break;
                    }
                    if (line.toUpperCase().startsWith("FIELD")) {
                        addVNFFieldData(outField, line);
                    } else {
                        DataArray[] da = readDataArray(nData, line);
                        if (da != null && nodeData) {
                            for (int i = 0; i < da.length; i++) {
                                outField.addData(da[i]);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
        return outField;
    }

    protected IrregularField readIrregularFileFromVTKGrid() {
        float[] coords = (readCoords(-1));
        IrregularField outField = new IrregularField(nNodes);
        outField.setNSpace(3);
        outField.setCoords(coords);
        String line = nextLine(new String[]{"CELL"});
        String[] tokens = line.split(" +");
        int[] tmpCells = null;
        int[][] nodes = null;
        int[] nCellsOfType = new int[Cell.TYPES];
        for (int i = 0; i < nCellsOfType.length; i++) {
            nCellsOfType[i] = 0;
        }

        if (tokens[0].equalsIgnoreCase("CELLS")) {
            try {
                nCellsItems = Integer.parseInt(tokens[2]);
            } catch (Exception e) {
                return null;
            }
            tmpCells = new int[nCellsItems];
            for (int i = 0; i < tmpCells.length; i++) {
                tmpCells[i] = getInt();
            }
            line = nextLine(new String[]{"CELL_TYPES"});
            tokens = line.split(" +");
            if (!tokens[0].equalsIgnoreCase("CELL_TYPES")) {
                return null;
            }
            try {
                nCells = Integer.parseInt(tokens[1]);
            } catch (Exception e) {
                return null;
            }
            int[] cellTypes = new int[nCells];
            int[] iCellOfType = new int[Cell.TYPES];
            for (int i = 0; i < nCellsOfType.length; i++) {
                nCellsOfType[i] = iCellOfType[i] = 0;
            }
            for (int i = 0, tmpCellNodesIndex = 0; i < cellTypes.length; i++, tmpCellNodesIndex += 1) {
                int vtkCellType = getInt();
                int vnCellType = VTKcellTypes[vtkCellType];
                int vnCellCount = VTKcellSets[vtkCellType];
                if (vnCellType == -1) {
                    System.out.println("" + vtkCellType);
                    return null;
                }
                cellTypes[i] = vtkCellType;
                if (vnCellCount == 1) {
                    nCellsOfType[vnCellType] += 1;
                } else {
                    int n = tmpCells[tmpCellNodesIndex];
                    nCellsOfType[vnCellType] += n + vnCellCount;
                    tmpCellNodesIndex += n;
                }
            }
            nodes = new int[Cell.TYPES][];
            for (int i = 0; i < Cell.TYPES; i++) {
                nodes[i] = new int[nCellsOfType[i] * Cell.nv[i]];
            }
            for (int i = 0, j = 0; i < nCells; i++) {
                int vtkCellType = cellTypes[i];
                int vnCellType = VTKcellTypes[vtkCellType];
                int k = Cell.nv[vnCellType];
                int k1 = tmpCells[j];
                j += 1;
                if (VTKcellSets[vtkCellType] == 1) {
                    for (int l = 0; l < k; l++, j++, iCellOfType[vnCellType] += 1) {
                        nodes[vnCellType][iCellOfType[vnCellType]] = tmpCells[j];
                    }
                } else {
                    for (int l = 0; l < k1 + VTKcellSets[vtkCellType]; l++) {
                        for (int m = 0; m < k; m++, iCellOfType[vnCellType] += 1) {
                            nodes[vnCellType][iCellOfType[vnCellType]] = tmpCells[j + l + k];
                        }
                    }
                    j += k1;
                }
            }
        }
        if (tokens[0].equalsIgnoreCase("CELL_TYPES")) {
            try {
                nCells = Integer.parseInt(tokens[1]);
                nodes = new int[Cell.TYPES][];
                nodes[Cell.POINT] = new int[nCells];
                for (int i = 0; i < nCells; i++) {
                    int k = getInt();
                    if (k == 1) {
                        nodes[Cell.POINT][i] = i;
                    }
                    nCellsOfType[Cell.POINT] = nCells;
                }
            } catch (Exception e) {
                return null;
            }
        }
        CellSet cs = new CellSet();
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] == null || nodes[i].length == 0) {
                continue;
            }
            boolean[] orient = new boolean[nCellsOfType[i]];
            int[] indices = new int[nCellsOfType[i]];
            for (int j = 0; j < orient.length; j++) {
                indices[i] = i;
                orient[j] = true;
            }
            cs.addCells(new CellArray(i, nodes[i], orient, indices));
        }
        cs.generateDisplayData(coords);
        outField.addCellSet(cs);
        line = nextLine(new String[]{"CELL_DATA", "POINT_DATA"});
        for (int iDataType = 0; iDataType < 2; iDataType++) {
            if (line == null) {
                break;
            }
            //System.out.println(line);
            tokens = line.split(" +");
            if (tokens.length < 2) {
                return outField;
            }
            DataContainer dataCont = outField;
            if (tokens[0].equalsIgnoreCase("CELL_DATA")) {
                dataCont = cs;
            }
            int nData = Integer.parseInt(tokens[1]);
            while ((line = nextLine(new String[]{"CELL_DATA", "POINT_DATA",
                "FIELD",
                "SCALARS", "COLOR_SCALARS", "LOOKUP_TABLE",
                "VECTORS", "NORMALS", "TEXTURE_COORDINATES", "TENSORS"})) != null) {
                if (line.toUpperCase().startsWith("CELL_DATA") || line.toUpperCase().startsWith("POINT_DATA")) {
                    break;
                }
                if (line.toUpperCase().startsWith("FIELD")) {
                    addVNFFieldData(dataCont, line);
                } else {
                    DataArray[] da = readDataArray(nData, line);
                    if (da != null) {
                        for (int i = 0; i < da.length; i++) {
                            dataCont.addData(da[i]);
                        }
                    }
                }
            }
        }
        return outField;
    }

    protected IrregularField readIrregularFileFromVTKPolys() {
        String line;
        String[] tokens;
        float[] coords = (readCoords(-1));
        IrregularField outField = new IrregularField(nNodes);
        outField.setNSpace(3);
        outField.setCoords(coords);
        CellSet cs = new CellSet();

        int nVerts = 0;
        int nSegments = 0;
        int nTriangles = 0;
        int nQuads = 0;
        List<Polydata> polys = new ArrayList<Polydata>();
        while ((line = nextLine(new String[]{"VERTICES", "LINES", "POLYGONS", "TRIANGLE_STRIPS",
            "CELL_DATA", "POINT_DATA"})) != null) {
            tokens = line.split(" +");
            if (tokens[0].equalsIgnoreCase("CELL_DATA") || tokens[0].equalsIgnoreCase("POINT_DATA")) {
                break;
            }
            if (tokens.length < 3) {
                System.out.println("error in line " + line);
                return outField;
            }
            int type = VTKPolyTypeMap.get(tokens[0].toUpperCase());
            int nItems = Integer.parseInt(tokens[1]);
            int nData = Integer.parseInt(tokens[2]);
            int[] data = new int[nData];
            readArray(data);
            polys.add(new Polydata(type, nItems, data));
            switch (type) {
                case VERTICES:
                    nVerts += nData - nItems;
                    break;
                case LINES:
                    nSegments += nData - 2 * nItems;
                    break;
                case POLYGONS:
                    for (int i = 0, j = 0; i < nItems; i++, j++) {
                        if (data[j] == 3) {
                            nTriangles += 1;
                        } else if (data[j] == 4) {
                            nQuads += 1;
                        } else {
                            nTriangles += (data[j] - 2);
                        }
                        j += data[j];
                    }
                    break;
                case TRIANGLE_STRIPS:
                    nTriangles += nData - 3 * nItems;
            }
        }
        buildCells(cs, nVerts, nSegments, nTriangles, nQuads, polys);
        cs.generateDisplayData(coords);
        outField.addCellSet(cs);
        for (int iDataType = 0; iDataType < 2; iDataType++) {
            if (line == null) {
                break;
            }
            //System.out.println(line);
            tokens = line.split(" +");
            if (tokens.length < 2) {
                return outField;
            }
            DataContainer dataCont = outField;
            if (tokens[0].equalsIgnoreCase("CELL_DATA")) {
                dataCont = cs;
            }
            int nData = Integer.parseInt(tokens[1]);
            while ((line = nextLine(new String[]{"CELL_DATA", "POINT_DATA",
                "FIELD",
                "SCALARS", "COLOR_SCALARS", "LOOKUP_TABLE",
                "VECTORS", "NORMALS", "TEXTURE_COORDINATES", "TENSORS"})) != null) {
                if (line.toUpperCase().startsWith("CELL_DATA") || line.toUpperCase().startsWith("POINT_DATA")) {
                    break;
                }
                if (line.toUpperCase().startsWith("FIELD")) {
                    addVNFFieldData(dataCont, line);
                } else {
                    DataArray[] da = readDataArray(nData, line);
                    if (da != null) {
                        for (int i = 0; i < da.length; i++) {
                            dataCont.addData(da[i]);
                        }
                    }
                }
            }
        }
        return outField;
    }

    abstract int getInt();

    abstract void readArray(float[] a);

    abstract void readArray(int[] a);

    abstract void readFloatArrayFrom(float[] a, int type);

    abstract DataArray[] readDataArray(int nData, String l);

    abstract String nextLine();

    abstract String nextLine(String[] begin);

    abstract DataArray readData(int type, int vlen, int nData, String name);

    abstract public Field readVTK(String filename, ByteOrder order);
}
