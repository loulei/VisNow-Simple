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
import java.util.Arrays;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import pl.edu.icm.visnow.datasets.CellArray;
import pl.edu.icm.visnow.datasets.CellSet;
import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.datasets.IrregularField;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.datasets.cells.Cell;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.lib.utils.field.MergeIrregularField;
import vtk.vtkCell;
import vtk.vtkCellData;
import vtk.vtkDataArray;
import vtk.vtkDataObject;
import vtk.vtkDoubleArray;
import vtk.vtkFloatArray;
import vtk.vtkGenericDataObjectReader;
import vtk.vtkIntArray;
import vtk.vtkMultiBlockDataSet;
import vtk.vtkNativeLibrary;
import vtk.vtkPointData;
import vtk.vtkPointSet;
import vtk.vtkPoints;
import vtk.vtkRectilinearGrid;
import vtk.vtkShortArray;
import vtk.vtkUnsignedCharArray;
import vtk.vtkUnsignedShortArray;
import vtk.vtkXMLGenericDataObjectReader;

/**
 *
 * @author Piotr Wendykier (piotrw@icm.edu.pl)
 * @author creed Interdisciplinary Centre for Mathematical and Computational
 * Modelling
 */
public class VTKNativeCore extends VTKCore {

    private static final Logger LOGGER = Logger.getLogger(VTKNativeCore.class);

    /**
     * Creates a new
     * <code>VTKNativeCore</code> object.
     */
    public VTKNativeCore() {
        vtkNativeLibrary.DisableOutputWindow(logFile);
    }

    @Override
    public Field readVTK(String filename, ByteOrder order) {
        Field res = loadVTK(filename);
        if (res == null) {
            VTKJavaCore javaCore = new VTKJavaCore();
            return javaCore.readVTK(filename, order);
        }
        return res;
    }

    private static Field loadVTK(String filename) {
        vtkGenericDataObjectReader readerLegacy = new vtkGenericDataObjectReader();
        readerLegacy.SetFileName(filename);
        readerLegacy.Update();
        if (readerLegacy.GetOutput() != null) {
            if (readerLegacy.IsFilePolyData() != 0) //        if( readerLegacy.OpenVTKFile() != 0 && readerLegacy.ReadHeader() !=0 )
            {
                LOGGER.info("using legacy reader & polydata");
                return vtkPointSet2vnField(readerLegacy.GetPolyDataOutput());
            } else if (readerLegacy.IsFileUnstructuredGrid() != 0) {
                LOGGER.info("using legacy reader & unstructured grid");
                return vtkPointSet2vnField(readerLegacy.GetUnstructuredGridOutput());
            } else if (readerLegacy.IsFileRectilinearGrid() != 0) {
                LOGGER.info("using legacy reader & rectilinear grid");
                return vtkRectilinearGrid2vnField(readerLegacy.GetRectilinearGridOutput());
            } else {
                LOGGER.error("legacy reader is OK, but neither polydata nor rectilinear grid nor unstuctured grid");
                return null;
            }
        }

        vtkXMLGenericDataObjectReader readerXML = new vtkXMLGenericDataObjectReader();
        readerXML.SetFileName(filename);
        readerXML.Update();
        if (readerXML.GetOutput() != null) {
            if (readerXML.GetPolyDataOutput() != null) {
                LOGGER.info("using XML reader & polydata");
                return vtkPointSet2vnField(readerXML.GetPolyDataOutput());
            } else if (readerXML.GetUnstructuredGridOutput() != null) {
                LOGGER.info("using XML reader & unstructured data");
                return vtkPointSet2vnField(readerXML.GetUnstructuredGridOutput());
            } else if (readerXML.GetMultiBlockDataSetOutput() != null) {
                LOGGER.info("using XML reader & multi block");
                return vtkMultiBlock2vnField(readerXML.GetMultiBlockDataSetOutput());
            } else {
                LOGGER.error("neither PolyData nor Unstructured Data nor Multi Block, giving up");
                return null;
            }
        } else {
            LOGGER.error("neither legacy nor XML file, giving up");
            return null;
        }
    }

    private static IrregularField vtkPointSet2vnField(vtkPointSet pointSet) {
        vtkPoints points = pointSet.GetPoints();

        IrregularField outField = new IrregularField(points.GetNumberOfPoints());

        if (!LoadCoords(outField, points)) {
            return null;
        }
        if (!LoadPointData(outField, pointSet.GetPointData())) {
            return null;
        }
        if (!LoadCells(outField, pointSet)) {
            return null;
        }
        if (!LoadCellData(outField.getCellSet(0), pointSet.GetCellData())) {
            return null;
        }
        AddNullDataIfNone(outField);

        return outField;
    }

    private static int vtkToVnCellType(int vtkCellType) {
        int[] vtkCellTypes = new int[]{1, 3, 5, 9, 10, 14, 13, 12};
        return ArrayUtils.indexOf(vtkCellTypes, vtkCellType);
    }

    private static boolean LoadCoords(IrregularField field, vtkPoints points) {
        int noDims = 3;
        int nPoints = points.GetNumberOfPoints();

// load geometry        
        LOGGER.info("[points]");
        LOGGER.info("number of points: " + nPoints);

        field.setNSpace(noDims);
        float[] coords = new float[nPoints * noDims];
        for (int n = 0; n < nPoints; n++) {
            double[] p = points.GetPoint(n);
            coords[ n * noDims] = (float) p[0];
            coords[ n * noDims + 1] = (float) p[1];
            coords[ n * noDims + 2] = (float) p[2];
        }
        field.setCoords(coords);
        return true;
    }

    private static boolean LoadPointData(Field field, vtkPointData pointData) {
        int nArrays = pointData.GetNumberOfArrays();
        int nComponentsTotal = pointData.GetNumberOfComponents();
        int nTuples = pointData.GetNumberOfTuples();
        LOGGER.info("[[point data]]");
        LOGGER.info("number of arrays: " + nArrays);
        LOGGER.info("number of components: " + nComponentsTotal);
        LOGGER.info("number of tuples: " + nTuples);

        for (int i = 0; i < nArrays; i++) {
            vtkDataArray array = pointData.GetArray(i);
            int nComponents = array.GetNumberOfComponents();
            assert (nTuples == array.GetNumberOfTuples());
            String name = array.GetName();
            LOGGER.info("[array " + i + ", name: " + name + "]");
            LOGGER.info("number of components: " + nComponents);
            LOGGER.info("number of tuples: " + nTuples);

            if (name.isEmpty()) {
                name = "points_data_" + Integer.toString(i);
            }
            field.addData(vtkDataArray2vnDataArray(array, name));
        }
        return true;
    }

    private static boolean LoadCells(IrregularField field, vtkPointSet polyData) {
        final int nCells = polyData.GetNumberOfCells();
        LOGGER.info("[cells]");
        LOGGER.info("cells: " + nCells);
        LOGGER.info("points: " + polyData.GetNumberOfPoints());
        if(nCells <= 0) {
            return false;
        }
// count number of cells of each type        
        int[] nIndices = new int[Cell.TYPES];
        for (int i = 0; i < polyData.GetNumberOfCells(); i++) {
            vtkCell cell = polyData.GetCell(i);
            final int vnCellType = vtkToVnCellType(cell.GetCellType());
            if (vnCellType == -1) {
                LOGGER.error("VTK cell type " + cell.GetCellType() + " not supported!");
                return false;
            } else {
                nIndices[ vnCellType]++;
            }
        }

// create index arrays        
        int[][] indices = new int[Cell.TYPES][];
        int[][] data_indices = new int[Cell.TYPES][];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = new int[nIndices[i] * Cell.nv[i]];
            data_indices[i] = new int[nIndices[i]];
        }

        int[] is = new int[Cell.TYPES];
        Arrays.fill(is, 0);

        for (int i = 0; i < polyData.GetNumberOfCells(); i++) {
            vtkCell cell = polyData.GetCell(i);
            int vnCellType = vtkToVnCellType(cell.GetCellType());
            if (vnCellType == -1) {
                continue;
            }
            assert (cell.GetNumberOfPoints() == Cell.nv[vnCellType]);
            data_indices[vnCellType][ is[vnCellType] / Cell.nv[vnCellType]] = i;
            for (int j = 0; j < Cell.nv[vnCellType]; j++) {
                indices[vnCellType][is[vnCellType]++] = cell.GetPointId(j);
            }
        }

        CellSet cellSet = new CellSet();
        for (int i = 0; i < indices.length; i++) {
            if (is[i] == 0) {
                continue;
            }
            CellArray cellArray = new CellArray(i, indices[i], null, data_indices[i]);
            cellSet.setCellArray(cellArray);
        }
        cellSet.generateDisplayData(field.getCoords());

        field.addCellSet(cellSet);
        return true;
    }

    private static boolean LoadCellData(CellSet cellSet, vtkCellData cellData) {
        LOGGER.info("[[cell data]]");
        int nArrays = cellData.GetNumberOfArrays();
        LOGGER.info("number of arrays: " + nArrays);
        for (int i = 0; i < nArrays; i++) {
            vtkDataArray array = cellData.GetArray(i);
            int nComponents = array.GetNumberOfComponents();
            int nTuples = array.GetNumberOfTuples();
            final String name = array.GetName();

            LOGGER.info("[array " + i + ", name: " + name + "]");
            LOGGER.info("number of components: " + nComponents);
            LOGGER.info("number of tuples: " + nTuples);

            cellSet.addData(vtkDataArray2vnDataArray(array, name));
        }
        return true;
    }

    private static IrregularField vtkMultiBlock2vnField(vtkMultiBlockDataSet multiBlockDataSet) {
        int nBlocks = multiBlockDataSet.GetNumberOfBlocks();

        IrregularField outField = null;

        for (int i = 0; i < nBlocks; i++) {
            vtkDataObject block = multiBlockDataSet.GetBlock(i);
            final String blockType = block.GetClassName();
            LOGGER.info("block " + i + " class name: " + blockType);
            if ("vtkUnstructuredGrid".equals(blockType)) {
                vtkPointSet pointSet = (vtkPointSet) block;
                IrregularField field = vtkPointSet2vnField(pointSet);

                outField = MergeIrregularField.merge(outField, field, 0, true);
            } else {
                LOGGER.error("Sorry, this (" + blockType + ") type of block is not supported.");
            }
        }

        return outField;
    }

    private static float[] GetDataFromVtkDataArray(vtkDataArray array) {
        int nComponents = array.GetNumberOfComponents();
        int nTuples = array.GetNumberOfTuples();

        float[] data = new float[nTuples * nComponents];
        for (int j = 0; j < nComponents; j++) {
            for (int k = 0; k < nTuples; k++) {
                data[ k * nComponents + j] = (float) array.GetComponent(k, j);
            }
        }
        return data;
    }

    private static DataArray vtkDataArray2vnDataArray(vtkDataArray array, String name) {
        if (array instanceof vtkFloatArray) {
            return DataArray.create(((vtkFloatArray) array).GetJavaArray(), array.GetNumberOfComponents(), name);
        } else if (array instanceof vtkDoubleArray) {
            return DataArray.create(((vtkDoubleArray) array).GetJavaArray(), array.GetNumberOfComponents(), name);
        } else if (array instanceof vtkIntArray) {
            return DataArray.create(((vtkIntArray) array).GetJavaArray(), array.GetNumberOfComponents(), name);
        } else if (array instanceof vtkUnsignedShortArray) {
            return DataArray.create(((vtkUnsignedShortArray) array).GetJavaArray(), array.GetNumberOfComponents(), name);
        } else if (array instanceof vtkUnsignedCharArray) {
            return DataArray.create(((vtkUnsignedCharArray) array).GetJavaArray(), array.GetNumberOfComponents(), name);
        } else if (array instanceof vtkShortArray) {
            return DataArray.create(((vtkShortArray) array).GetJavaArray(), array.GetNumberOfComponents(), name);
        } //        else if( array instanceof vtkCharArray )
        //            return DataArray.create( ((vtkCharArray)array).GetJavaArray(), array.GetNumberOfComponents(), name );
        else // fallback to float
        {
            LOGGER.warn("data array " + array.GetClassName() + " not supported yet, converting to float");
            return DataArray.create(GetDataFromVtkDataArray(array), array.GetNumberOfComponents(), name);
        }
    }

    private static RegularField vtkRectilinearGrid2vnField(vtkRectilinearGrid grid) {
        int[] dims = grid.GetDimensions();
        LOGGER.info("[[rectlinear grid]]");
        LOGGER.info("dims: " + Arrays.toString(dims));

        RegularField field = new RegularField(dims);

        if (!LoadPointData(field, grid.GetPointData())) {
            return null;
        }

        return field;
    }

    private static void AddNullDataIfNone(Field field) {
        if (field.getNData() > 0) // there is node data 
        {
            return;
        }
        if (field instanceof IrregularField) {
            for (CellSet cs : ((IrregularField) field).getCellSets()) {
                if (cs.getNData() > 0) // there is cell data
                {
                    return;
                }
            }
        }

        float[] data = new float[field.getNNodes()];
        field.addData(DataArray.create(data, 1, "zeros"));
    }
}
