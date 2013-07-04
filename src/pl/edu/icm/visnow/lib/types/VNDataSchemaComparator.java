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
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;

/**
 *
 * @author Bartosz Borucki (babor@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class VNDataSchemaComparator {

    public static final long FIELD = 1;
    public static final long NSPACE = 1 << 1;
    public static final long REGULAR = 1 << 2;
    public static final long NDIMS = 1 << 3;
    public static final long DIMS = 1 << 4;
    public static final long AFFINE = 1 << 5;
    public static final long COORDS = 1 << 6;
    public static final long NDATA = 1 << 7;
    public static final long DATA_VECLEN = 1 << 8;
    public static final long DATA_VECLENS = 1 << 9;
    public static final long DATA_TYPE = 1 << 10;
    public static final long DATA_TYPES = 1 << 11;
    public static final long DATA_NAME = 1 << 12;
    public static final long DATA_NAMES = 1 << 13;
    public static final long IRREGULAR = 1 << 14;    
    public static final long CELLSETS = 1 << 15;
    public static final long NCELLSETS = 1 << 16;
    public static final long NCELLDATA = 1 << 17;
    public static final long CELLDATA_VECLEN = 1 << 18;
    public static final long CELLDATA_VECLENS = 1 << 19;
    public static final long CELLDATA_TYPE = 1 << 20;
    public static final long CELLDATA_TYPES = 1 << 21;
    public static final long CELLDATA_NAME = 1 << 22;
    public static final long CELLDATA_NAMES = 1 << 23;
    public static final long CELLSET_NAME = 1 << 24;
    public static final long CELLSET_NAMES = 1 << 25;
    public static final long CELLS_POINT = 1 << 26;
    public static final long CELLS_SEGMENT = 1 << 27;
    public static final long CELLS_TRIANGLE = 1 << 28;
    public static final long CELLS_QUAD = 1 << 29;
    public static final long CELLS_TETRA = 1 << 30;
    public static final long CELLS_PYRAMID = 1 << 31;
    public static final long CELLS_PRISM = ((long)1) << 32;
    public static final long CELLS_HEXAHEDRON = ((long)1) << 33;
    public static final long TIME = ((long)1) << 34;
    public static final long CELLS_2D =((long)1) << 35;
    public static final long CELLS_3D = ((long)1) << 36;
    
    public static final long IRREGULAR_FIELD = IRREGULAR | FIELD;
    public static final long MASK_NULL = 0;
    

    public static long correctMask(long inMask) {
        long outMask = inMask;
        if ((outMask & REGULAR) == REGULAR) {
            outMask = outMask | FIELD;
        }

        if ((outMask & NDIMS) == NDIMS) {
            outMask = outMask | REGULAR | FIELD;
        }

        if ((outMask & NSPACE) == NSPACE) {
            outMask = outMask | FIELD;
        }

        if ((outMask & DIMS) == DIMS) {
            outMask = outMask | NDIMS | REGULAR | FIELD;
        }

        if ((outMask & AFFINE) == AFFINE) {
            outMask = outMask | REGULAR | FIELD;
        }

        if ((outMask & COORDS) == COORDS) {
            outMask = outMask | REGULAR | FIELD;
        }

        if ((outMask & NDATA) == NDATA) {
            outMask = outMask | FIELD;
        }

        if ((outMask & DATA_VECLEN) == DATA_VECLEN) {
            outMask = outMask | FIELD;
        }

        if ((outMask & DATA_VECLENS) == DATA_VECLENS) {
            outMask = outMask | NDATA | FIELD;
        }

        if ((outMask & DATA_TYPE) == DATA_TYPE) {
            outMask = outMask | FIELD;
        }

        if ((outMask & DATA_TYPES) == DATA_TYPES) {
            outMask = outMask | NDATA | FIELD;
        }

        if ((outMask & DATA_NAME) == DATA_NAME) {
            outMask = outMask | FIELD;
        }

        if ((outMask & DATA_NAMES) == DATA_NAMES) {
            outMask = outMask | NDATA | FIELD;
        }

        if ((outMask & IRREGULAR) == IRREGULAR) {
            outMask = outMask | FIELD;
        }

        if ((outMask & CELLSETS) == CELLSETS) {
            outMask = outMask | IRREGULAR | FIELD;
        }

        if ((outMask & NCELLSETS) == NCELLSETS) {
            outMask = outMask | CELLSETS | IRREGULAR | FIELD;
        }

        if ((outMask & NCELLDATA) == NCELLDATA) {
            outMask = outMask | CELLSETS | IRREGULAR | FIELD;
        }

        if ((outMask & CELLDATA_VECLEN) == CELLDATA_VECLEN) {
            outMask = outMask | CELLSETS | IRREGULAR | FIELD;
        }

        if ((outMask & CELLDATA_VECLENS) == CELLDATA_VECLENS) {
            outMask = outMask | NCELLDATA | CELLSETS | IRREGULAR | FIELD;
        }

        if ((outMask & CELLDATA_TYPE) == CELLDATA_TYPE) {
            outMask = outMask | CELLSETS | IRREGULAR | FIELD;
        }

        if ((outMask & CELLDATA_TYPES) == CELLDATA_TYPES) {
            outMask = outMask | NCELLDATA | CELLSETS | IRREGULAR | FIELD;
        }

        if ((outMask & CELLDATA_NAME) == CELLDATA_NAME) {
            outMask = outMask | CELLSETS | IRREGULAR | FIELD;
        }

        if ((outMask & CELLDATA_NAMES) == CELLDATA_NAMES) {
            outMask = outMask | NCELLDATA | CELLSETS | IRREGULAR | FIELD;
        }
        
        if ((outMask & CELLSET_NAME) == CELLSET_NAME) {
            outMask = outMask | CELLSETS | IRREGULAR | FIELD;
        }

        if ((outMask & CELLSET_NAMES) == CELLSET_NAMES) {
            outMask = outMask | NCELLSETS | CELLSETS | IRREGULAR | FIELD;
        }

        if ((outMask & CELLS_POINT) == CELLS_POINT) {
            outMask = outMask | CELLSETS | IRREGULAR | FIELD;
        }

        if ((outMask & CELLS_2D) == CELLS_2D) {
            outMask = outMask | CELLSETS | IRREGULAR | FIELD;
        }

        if ((outMask & CELLS_3D) == CELLS_3D) {
            outMask = outMask | CELLSETS | IRREGULAR | FIELD;
        }
        
        if ((outMask & CELLS_SEGMENT) == CELLS_SEGMENT) {
            outMask = outMask | CELLS_2D | CELLSETS | IRREGULAR | FIELD;
        }

        if ((outMask & CELLS_TRIANGLE) == CELLS_TRIANGLE) {
            outMask = outMask | CELLS_2D | CELLSETS | IRREGULAR | FIELD;
        }

        if ((outMask & CELLS_QUAD) == CELLS_QUAD) {
            outMask = outMask | CELLS_2D | CELLSETS | IRREGULAR | FIELD;
        }

        if ((outMask & CELLS_TETRA) == CELLS_TETRA) {
            outMask = outMask | CELLS_3D | CELLSETS | IRREGULAR | FIELD;
        }

        if ((outMask & CELLS_PYRAMID) == CELLS_PYRAMID) {
            outMask = outMask | CELLS_3D | CELLSETS | IRREGULAR | FIELD;
        }

        if ((outMask & CELLS_PRISM) == CELLS_PRISM) {
            outMask = outMask | CELLS_3D | CELLSETS | IRREGULAR | FIELD;
        }

        if ((outMask & CELLS_HEXAHEDRON) == CELLS_HEXAHEDRON) {
            outMask = outMask | CELLS_3D | CELLSETS | IRREGULAR | FIELD;
        }

        if ((outMask & TIME) == TIME) {
            outMask = outMask | FIELD;
        }
        return outMask;
    }

    public static boolean isCompatible(VNDataSchemaInterface data1, VNDataSchemaInterface data2, long compatibilityMask) {
        if (compatibilityMask == MASK_NULL) {
            return true;
        }

        long ccm = correctMask(compatibilityMask); //enforcing hierarhy of params

        try {
            if ((ccm & FIELD) == FIELD) {
                if (data1 == null && data2 != null) {
                    return false;
                }

                if (data1 != null && data2 == null) {
                    return false;
                }

                if (data1.isField() != data2.isField()) {
                    return false;
                }
            }

            if ((ccm & REGULAR) == REGULAR) {
                if (data1.isRegular() != data2.isRegular()) {
                    return false;
                }
            }

            if ((ccm & NDIMS) == NDIMS) {
                if (data1.getNDims() != data2.getNDims()) {
                    return false;
                }
            }

            if ((ccm & NSPACE) == NSPACE) {
                if (data1.getNSpace() != data2.getNSpace()) {
                    return false;
                }
            }

            if ((ccm & DIMS) == DIMS) {
                int[] dims1 = data1.getDims();
                int[] dims2 = data2.getDims();
                for (int i = 0; i < dims1.length; i++) {
                    if (dims1[i] != dims2[i]) {
                        return false;
                    }
                }
            }

            if ((ccm & AFFINE) == AFFINE) {
                if (data1.isAffine() != data2.isAffine()) {
                    return false;
                }
            }

            if ((ccm & COORDS) == COORDS) {
                //if( !data1.isCoords() || !data2.isCoords() )
                if (data1.isCoords() != data2.isCoords()) {
                    return false;
                }
            }

            if ((ccm & NDATA) == NDATA) {
                if (data1.getNData() != data2.getNData()) {
                    return false;
                }
            }

            if ((ccm & DATA_VECLEN) == DATA_VECLEN) {
                int[] veclens1 = data1.getDataVeclens();
                int[] veclens2 = data2.getDataVeclens();

                if (veclens1 == null || veclens2 == null) {
                    return false;
                }

                boolean result = false;
                for (int i = 0; i < veclens1.length; i++) {
                    for (int j = 0; j < veclens2.length; j++) {
                        if (veclens1[i] == veclens2[j]) {
                            result = true;
                            break;
                        }
                    }
                    if (result) {
                        break;
                    }
                }
                if (!result) {
                    return false;
                }
            }

            if ((ccm & DATA_VECLENS) == DATA_VECLENS) {
                int[] veclens1 = data1.getDataVeclens();
                int[] veclens2 = data2.getDataVeclens();
                if (veclens1 == null || veclens2 == null) {
                    return false;
                }

                if (veclens1.length != veclens2.length) {
                    return false;
                }

                for (int i = 0; i < veclens1.length; i++) {
                    if (veclens1[i] != veclens2[i]) {
                        return false;
                    }
                }
            }

            if ((ccm & DATA_TYPE) == DATA_TYPE) {
                int[] types1 = data1.getDataTypes();
                int[] types2 = data2.getDataTypes();
                if (types1 == null || types2 == null) {
                    return false;
                }

                boolean result = false;
                for (int i = 0; i < types1.length; i++) {
                    for (int j = 0; j < types2.length; j++) {
                        if (types1[i] == types2[j]) {
                            result = true;
                            break;
                        }
                    }
                    if (result) {
                        break;
                    }
                }
                if (!result) {
                    return false;
                }
            }

            if ((ccm & DATA_TYPES) == DATA_TYPES) {
                int[] types1 = data1.getDataTypes();
                int[] types2 = data2.getDataTypes();
                if (types1 == null || types2 == null) {
                    return false;
                }
                if (types1.length != types2.length) {
                    return false;
                }

                for (int i = 0; i < types1.length; i++) {
                    if (types1[i] != types2[i]) {
                        return false;
                    }
                }
            }

            if ((ccm & DATA_NAME) == DATA_NAME) {
                String[] names1 = data1.getDataNames();
                String[] names2 = data2.getDataNames();
                if (names1 == null || names2 == null) {
                    return false;
                }

                boolean result = false;
                for (int i = 0; i < names1.length; i++) {
                    for (int j = 0; j < names2.length; j++) {
                        if (names1[i].equals(names2[j])) {
                            result = true;
                            break;
                        }
                    }
                    if (result) {
                        break;
                    }
                }
                if (!result) {
                    return false;
                }
            }

            if ((ccm & DATA_NAMES) == DATA_NAMES) {
                String[] names1 = data1.getDataNames();
                String[] names2 = data2.getDataNames();
                if (names1 == null || names2 == null) {
                    return false;
                }
                if (names1.length != names2.length) {
                    return false;
                }

                for (int i = 0; i < names1.length; i++) {
                    if (!names1[i].equals(names2[i])) {
                        return false;
                    }
                }
            }

            if ((ccm & IRREGULAR) == IRREGULAR) {
                if (data1.isIrregular() != data2.isIrregular()) {
                    return false;
                }
            }

            if ((ccm & CELLSETS) == CELLSETS) {
                if (data1.isCellSets() != data2.isCellSets()) {
                    return false;
                }
            }
            
            if ((ccm & NCELLSETS) == NCELLSETS) {
                if (data1.getNCellSets() != data2.getNCellSets()) {
                    return false;
                }
            }
            
            if ((ccm & NCELLDATA) == NCELLDATA) {
                int[] ncelldata1 = data1.getNCellData();
                int[] ncelldata2 = data2.getNCellData();
                if (ncelldata1 == null || ncelldata2 == null) {
                    return false;
                }
                
                boolean result = false;
                for (int i = 0; i < ncelldata1.length; i++) {
                    for (int j = 0; j < ncelldata2.length; j++) {
                        if (ncelldata1[i] == ncelldata2[j]) {
                            result = true;
                            break;
                        }
                    }
                    if (result) {
                        break;
                    }
                }
                if (!result) {
                    return false;
                }
            }

            if ((ccm & CELLDATA_VECLEN) == CELLDATA_VECLEN) {
                int[][] veclens1 = data1.getCellDataVeclens();
                int[][] veclens2 = data2.getCellDataVeclens();

                if (veclens1 == null || veclens2 == null) {
                    return false;
                }

                boolean result = false;
                int[] tmp1, tmp2;
                for (int i = 0; i < veclens1.length; i++) {
                    for (int j = 0; j < veclens2.length; j++) {
                        tmp1 = veclens1[i];
                        tmp2 = veclens2[j];                        
                        if(tmp1.length == tmp2.length) {
                            for (int k = 0; k < tmp2.length; k++) {
                                if(tmp1[k] == tmp2[k]) {
                                    result = false;
                                    break;              
                                }
                            }
                        }
                        if(result)
                            break;
                    }
                    if (result) {
                        break;
                    }
                }
                if (!result) {
                    return false;
                }
            }

            if ((ccm & CELLDATA_VECLENS) == CELLDATA_VECLENS) {
                int[][] veclens1 = data1.getCellDataVeclens();
                int[][] veclens2 = data2.getCellDataVeclens();

                if (veclens1 == null || veclens2 == null) {
                    return false;
                }

                boolean result = false, tmpresult = false;
                int[] tmp1, tmp2;
                for (int i = 0; i < veclens1.length; i++) {
                    for (int j = 0; j < veclens2.length; j++) {
                        tmp1 = veclens1[i];
                        tmp2 = veclens2[j];                        
                        if(tmp1.length == tmp2.length) {
                            tmpresult = true;
                            for (int k = 0; k < tmp2.length; k++) {
                                if(tmp1[k] != tmp2[k]) {
                                    tmpresult = false;
                                    break;              
                                }
                            }
                        }
                        if(tmpresult) {
                            result = true;
                            break;
                        }
                    }
                    if (result) {
                        break;
                    }
                }
                if (!result) {
                    return false;
                }
            }

            if ((ccm & CELLDATA_TYPE) == CELLDATA_TYPE) {
                int[][] types1 = data1.getCellDataTypes();
                int[][] types2 = data2.getCellDataTypes();

                if (types1 == null || types2 == null) {
                    return false;
                }

                boolean result = false;
                int[] tmp1, tmp2;
                for (int i = 0; i < types1.length; i++) {
                    for (int j = 0; j < types2.length; j++) {
                        tmp1 = types1[i];
                        tmp2 = types2[j];                        
                        if(tmp1.length == tmp2.length) {
                            for (int k = 0; k < tmp2.length; k++) {
                                if(tmp1[k] == tmp2[k]) {
                                    result = false;
                                    break;              
                                }
                            }
                        }
                        if(result)
                            break;
                    }
                    if (result) {
                        break;
                    }
                }
                if (!result) {
                    return false;
                }
            }

            if ((ccm & CELLDATA_TYPES) == CELLDATA_TYPES) {
                int[][] types1 = data1.getCellDataTypes();
                int[][] types2 = data2.getCellDataTypes();

                if (types1 == null || types2 == null) {
                    return false;
                }

                boolean result = false, tmpresult = false;
                int[] tmp1, tmp2;
                for (int i = 0; i < types1.length; i++) {
                    for (int j = 0; j < types2.length; j++) {
                        tmp1 = types1[i];
                        tmp2 = types2[j];                        
                        if(tmp1.length == tmp2.length) {
                            tmpresult = true;
                            for (int k = 0; k < tmp2.length; k++) {
                                if(tmp1[k] != tmp2[k]) {
                                    tmpresult = false;
                                    break;              
                                }
                            }
                        }
                        if(tmpresult) {
                            result = true;
                            break;
                        }
                    }
                    if (result) {
                        break;
                    }
                }
                if (!result) {
                    return false;
                }
            }

            if ((ccm & CELLDATA_NAME) == CELLDATA_NAME) {
                String[][] names1 = data1.getCellDataNames();
                String[][] names2 = data2.getCellDataNames();

                if (names1 == null || names2 == null) {
                    return false;
                }

                boolean result = false;
                String[] tmp1, tmp2;
                for (int i = 0; i < names1.length; i++) {
                    for (int j = 0; j < names2.length; j++) {
                        tmp1 = names1[i];
                        tmp2 = names2[j];                        
                        if(tmp1.length == tmp2.length) {
                            for (int k = 0; k < tmp2.length; k++) {
                                if(tmp1[k].equals(tmp2[k])) {
                                    result = false;
                                    break;              
                                }
                            }
                        }
                        if(result)
                            break;
                    }
                    if (result) {
                        break;
                    }
                }
                if (!result) {
                    return false;
                }
            }

            if ((ccm & CELLDATA_NAMES) == CELLDATA_NAMES) {
                String[][] names1 = data1.getCellDataNames();
                String[][] names2 = data2.getCellDataNames();

                if (names1 == null || names2 == null) {
                    return false;
                }

                boolean result = false, tmpresult = false;
                String[] tmp1, tmp2;
                for (int i = 0; i < names1.length; i++) {
                    for (int j = 0; j < names2.length; j++) {
                        tmp1 = names1[i];
                        tmp2 = names2[j];                        
                        if(tmp1.length == tmp2.length) {
                            tmpresult = true;
                            for (int k = 0; k < tmp2.length; k++) {
                                if(!tmp1[k].equals(tmp2[k])) {
                                    tmpresult = false;
                                    break;              
                                }
                            }
                        }
                        if(tmpresult) {
                            result = true;
                            break;
                        }
                    }
                    if (result) {
                        break;
                    }
                }
                if (!result) {
                    return false;
                }
            }
            
            if ((ccm & CELLSET_NAME) == CELLSET_NAME) {
                String[] names1 = data1.getCellSetNames();
                String[] names2 = data2.getCellSetNames();
                if (names1 == null || names2 == null) {
                    return false;
                }

                boolean result = false;
                for (int i = 0; i < names1.length; i++) {
                    for (int j = 0; j < names2.length; j++) {
                        if (names1[i].equals(names2[j])) {
                            result = true;
                            break;
                        }
                    }
                    if (result) {
                        break;
                    }
                }
                if (!result) {
                    return false;
                }
            }

            if ((ccm & CELLSET_NAMES) == CELLSET_NAMES) {
                String[] names1 = data1.getCellSetNames();
                String[] names2 = data2.getCellSetNames();
                if (names1 == null || names2 == null) {
                    return false;
                }
                if (names1.length != names2.length) {
                    return false;
                }

                for (int i = 0; i < names1.length; i++) {
                    if (!names1[i].equals(names2[i])) {
                        return false;
                    }
                }
            }

            if ((ccm & CELLS_POINT) == CELLS_POINT) {
                if (data1.hasCellsPoint() != data2.hasCellsPoint()) {
                    return false;
                }
            }

            if ((ccm & CELLS_2D) == CELLS_2D) {
                if (data1.hasCells2D() != data2.hasCells2D()) {
                    return false;
                }
            }

            if ((ccm & CELLS_3D) == CELLS_3D) {
                if (data1.hasCells3D() != data2.hasCells3D()) {
                    return false;
                }
            }
            
            if ((ccm & CELLS_SEGMENT) == CELLS_SEGMENT) {
                if (data1.hasCellsSegment() != data2.hasCellsSegment()) {
                    return false;
                }
            }

            if ((ccm & CELLS_TRIANGLE) == CELLS_TRIANGLE) {
                if (data1.hasCellsTriangle() != data2.hasCellsTriangle()) {
                    return false;
                }
            }

            if ((ccm & CELLS_QUAD) == CELLS_QUAD) {
                if (data1.hasCellsQuad() != data2.hasCellsQuad()) {
                    return false;
                }
            }

            if ((ccm & CELLS_TETRA) == CELLS_TETRA) {
                if (data1.hasCellsTetra() != data2.hasCellsTetra()) {
                    return false;
                }
            }

            if ((ccm & CELLS_PYRAMID) == CELLS_PYRAMID) {
                if (data1.hasCellsPyramid() != data2.hasCellsPyramid()) {
                    return false;
                }
            }

            if ((ccm & CELLS_PRISM) == CELLS_PRISM) {
                if (data1.hasCellsPrism() != data2.hasCellsPrism()) {
                    return false;
                }
            }

            if ((ccm & CELLS_POINT) == CELLS_POINT) {
                if (data1.hasCellsHexahedron() != data2.hasCellsHexahedron()) {
                    return false;
                }
            }

            if ((ccm & TIME) == TIME) {
                if (data1.isTime() != data2.isTime()) {
                    return false;
                }
            }
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    public static boolean isCompatible(Object obj1, Object obj2, long compatibilityMask) {
        if (compatibilityMask == MASK_NULL) {
            return true;
        }

        if (obj1 == null && obj2 == null) {
            return true;
        }

        if ((obj1 == null && obj2 != null) || (obj1 != null && obj2 == null)) {
            return false;
        }

        if ((obj1 instanceof VNDataSchemaInterface) && (obj2 instanceof VNDataSchemaInterface)) {
            return isCompatible((VNDataSchemaInterface) obj1, (VNDataSchemaInterface) obj2, compatibilityMask);
        }
        return false;

    }

    public static boolean isConditionallyCompatible(VNDataSchemaInterface data1, long mask1, VNDataSchemaInterface data2, long mask2) {
        //check if data1 with mask1 conditionally matches into data2 with mask2

        if (mask1 == MASK_NULL || mask2 == MASK_NULL || (data1 == null && data2 == null)) {
            return true;
        }

        long ccm1 = correctMask(mask1);
        long ccm2 = correctMask(mask2);
        long commonMask = ccm1 & ccm2;

        if (!isCompatible(data1, data2, commonMask)) {
            return false;
        }

        try {

            if (data1 == null && data2 != null
                    || data1 != null && data2 == null) {
                return false;
            }

            if ((ccm2 & FIELD) == FIELD) {
                if (data1.isField() != data2.isField()) {
                    return false;
                }
            }

            if ((ccm2 & REGULAR) == REGULAR || (ccm2 & IRREGULAR) == IRREGULAR) {
                if ((data1.isRegular() && data2.isIrregular())
                        || (data2.isRegular() && data1.isIrregular())) {
                    return false;
                }
            }

            if ((ccm2 & AFFINE) == AFFINE || (ccm2 & COORDS) == COORDS) {
                if ((data1.isAffine() && data2.isCoords())
                        || (data2.isAffine() && data1.isCoords())) {
                    return false;
                }
            }

        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    public static long createComparatorFromSchemaParams(ArrayList<String[]> args) {
        long out = 0;
        for (int i = 0; i < args.size(); i++) {
            String str = args.get(i)[0];
            long p = parseString(str);
            if (p != -1) {
                out = out | p;
            }
        }
        return correctMask(out);
    }

    private static long parseString(String str) {
        if (str == null || str.length() == 0) {
            return -1;
        }

        if (str.equals("FIELD")) {
            return FIELD;
        }

        if (str.equals("NSPACE")) {
            return NSPACE;
        }

        if (str.equals("REGULAR")) {
            return REGULAR;
        }

        if (str.equals("NDIMS")) {
            return NDIMS;
        }

        if (str.equals("DIMS")) {
            return DIMS;
        }

        if (str.equals("AFFINE")) {
            return AFFINE;
        }

        if (str.equals("COORDS")) {
            return COORDS;
        }

        if (str.equals("NDATA")) {
            return NDATA;
        }

        if (str.equals("DATA_VECLEN")) {
            return DATA_VECLEN;
        }

        if (str.equals("DATA_VECLENS")) {
            return DATA_VECLENS;
        }

        if (str.equals("DATA_TYPE")) {
            return DATA_TYPE;
        }

        if (str.equals("DATA_TYPES")) {
            return DATA_TYPES;
        }

        if (str.equals("DATA_NAME")) {
            return DATA_NAME;
        }

        if (str.equals("DATA_NAMES")) {
            return DATA_NAMES;
        }

        if (str.equals("IRREGULAR")) {
            return IRREGULAR;
        }

        if (str.equals("CELLSETS")) {
            return CELLSETS;
        }

        if (str.equals("NCELLSETS")) {
            return NCELLSETS;
        }

        if (str.equals("NCELLDATA")) {
            return NCELLDATA;
        }

        if (str.equals("CELLDATA_VECLEN")) {
            return CELLDATA_VECLEN;
        }

        if (str.equals("CELLDATA_VECLENS")) {
            return CELLDATA_VECLENS;
        }

        if (str.equals("CELLDATA_TYPE")) {
            return CELLDATA_TYPE;
        }

        if (str.equals("CELLDATA_TYPES")) {
            return CELLDATA_TYPES;
        }

        if (str.equals("CELLDATA_NAME")) {
            return CELLDATA_NAME;
        }

        if (str.equals("CELLDATA_NAMES")) {
            return CELLDATA_NAMES;
        }
        
        if (str.equals("CELLSET_NAME")) {
            return CELLSET_NAME;
        }

        if (str.equals("CELLSET_NAMES")) {
            return CELLSET_NAMES;
        }
        
        if (str.equals("CELLS_POINT")) {
            return CELLS_POINT;
        }

        if (str.equals("CELLS_2D")) {
            return CELLS_2D;
        }

        if (str.equals("CELLS_3D")) {
            return CELLS_3D;
        }
        
        if (str.equals("CELLS_SEGMENT")) {
            return CELLS_SEGMENT;
        }
        
        if (str.equals("CELLS_TRIANGLE")) {
            return CELLS_TRIANGLE;
        }
        
        if (str.equals("CELLS_QUAD")) {
            return CELLS_QUAD;
        }
        
        if (str.equals("CELLS_TETRA")) {
            return CELLS_TETRA;
        }
        
        if (str.equals("CELLS_PYRAMID")) {
            return CELLS_PYRAMID;
        }
        
        if (str.equals("CELLS_PRISM")) {
            return CELLS_PRISM;
        }
        
        if (str.equals("CELLS_HEXAHEDRON")) {
            return CELLS_HEXAHEDRON;
        }
        
        if (str.equals("TIME")) {
            return TIME;
        }
        
        return -1;
    }

    public static String getDescription(VNDataSchemaInterface vndsi, long vndsc, String newline) {
        String str = "";
        if (vndsc == MASK_NULL) {
            return "everything";
        }

        long ccm = correctMask(vndsc); //enforcing hierarhy of params

        if (((ccm & FIELD) == FIELD) && ((ccm & REGULAR) != REGULAR) && ((ccm & IRREGULAR) != IRREGULAR)) {
            str += "Field ";
        }

        if ((ccm & REGULAR) == REGULAR) {
            str += "Regular Field ";

            if ((ccm & NDIMS) == NDIMS) {
                str += "" + vndsi.getNDims() + "D ";
            }

            if ((ccm & NSPACE) == NSPACE) {
                str += "" + vndsi.getNSpace() + "-space ";
            }

            if ((ccm & DIMS) == DIMS) {
                int[] dims = vndsi.getDims();
                str += "dimensions = {";
                for (int i = 0; i < dims.length; i++) {
                    str += "" + dims[i];
                    if (i < dims.length - 1) {
                        str += ",";
                    }
                }
                str += "} ";
            }

            if ((ccm & AFFINE) == AFFINE) {
                str += "with affine geometry ";
            }

            if ((ccm & COORDS) == COORDS) {
                str += "with explicit coordinates ";
            }

        }
        
        if ((ccm & IRREGULAR) == IRREGULAR) {
            str += "Irregular Field ";

            if ((ccm & NSPACE) == NSPACE) {
                str += "" + vndsi.getNSpace() + "-space ";
            }

        }
        
        if ((ccm & TIME) == TIME) {            
            str += ""+(vndsi.isTime()?"with":"without")+" time frames ";
        }
        str += newline;
        
        
        if ((ccm & FIELD) == FIELD) {
            if ((ccm & NDATA) == NDATA) {
                str += "" + vndsi.getNData() + " components" + newline;
            }

            if ((ccm & DATA_VECLEN) == DATA_VECLEN) {
                int[] v = vndsi.getDataVeclens();
                for (int i = 0; i < v.length; i++) {
                    str += "at least one " + (v[i] == 1 ? "scalar" : ("veclen=" + v[i])) + " component" + newline;
                }
            }

            if ((ccm & DATA_VECLENS) == DATA_VECLENS) {
                int[] v = vndsi.getDataVeclens();
                str += "with veclen=";
                for (int i = 0; i < v.length; i++) {
                    str += "" + v[i];
                    if (i < v.length - 1) {
                        str += ",";
                    }
                }
                str += newline;
            }

            if ((ccm & DATA_TYPE) == DATA_TYPE) {
                int[] v = vndsi.getDataTypes();
                for (int i = 0; i < v.length; i++) {
                    str += "at least one component of type " + DataArray.getTypeName(v[i]) + newline;
                }
            }

            if ((ccm & DATA_TYPES) == DATA_TYPES) {
                int[] v = vndsi.getDataVeclens();
                str += "with types=";
                for (int i = 0; i < v.length; i++) {
                    str += "" + DataArray.getTypeName(v[i]);
                    if (i < v.length - 1) {
                        str += ",";
                    }
                }
                str += newline;
            }

            if ((ccm & DATA_NAME) == DATA_NAME) {
                String[] v = vndsi.getDataNames();
                for (int i = 0; i < v.length; i++) {
                    str += "at least one component named '" + v[i] + "'" + newline;
                }
            }

            if ((ccm & DATA_NAMES) == DATA_NAMES) {
                String[] v = vndsi.getDataNames();
                str += "with names=";
                for (int i = 0; i < v.length; i++) {
                    str += "" + v[i];
                    if (i < v.length - 1) {
                        str += ",";
                    }
                }
                str += newline;
            }
        }
        
        if ((ccm & IRREGULAR) == IRREGULAR && (ccm & CELLSETS) == CELLSETS) {
            
            if((ccm & NCELLSETS) == NCELLSETS)
                str += "" + vndsi.getNCellSets() + " cell sets" + newline;
            
            if ((ccm & CELLSET_NAME) == CELLSET_NAME) {
                String[] v = vndsi.getCellSetNames();
                for (int i = 0; i < v.length; i++) {
                    str += "at least one cell set named '" + v[i] + "'" + newline;
                }
            }

            
            if ((ccm & CELLSET_NAMES) == CELLSET_NAMES) {
                String[] v = vndsi.getCellSetNames();
                str += "with names=";
                for (int i = 0; i < v.length; i++) {
                    str += "" + v[i];
                    if (i < v.length - 1) {
                        str += ",";
                    }
                }
                str += newline;
            }
         
            if ((ccm & NCELLDATA) == NCELLDATA) {
                //str += "" + vndsi.getNCellData() + " cell components" + newline;
                int[] v = vndsi.getNCellData();
                for (int i = 0; i < v.length; i++) {
                    str += "" + v[i];
                    if(i < v.length-1)
                        str += ",";
                }
                        
                str += " cell components in cellsets" + newline;
            }

            if ((ccm & CELLDATA_VECLEN) == CELLDATA_VECLEN) {
                int[][] v = vndsi.getCellDataVeclens();                
                for (int i = 0; i < v.length; i++) {
                    for (int j = 0; j < v[i].length; j++) {
                        str += "at least one " 
                                + (v[i][j] == 1 ? "scalar" : ("veclen=" + v[i][j])) + " cell component" + newline;
                    }
                }
            }

            if ((ccm & CELLDATA_VECLENS) == CELLDATA_VECLENS) {
                int[][] v = vndsi.getCellDataVeclens();                
                for (int i = 0; i < v.length; i++) {
                    str += "with cellset veclen=";
                    for (int j = 0; j < v[i].length; j++) {
                        str += "" + v[i][j];
                        if (j < v[i].length - 1) {
                            str += ",";
                        }
                    }
                    str += newline;
                }
                
            }

            if ((ccm & CELLDATA_TYPE) == CELLDATA_TYPE) {
                int[][] v = vndsi.getCellDataTypes();
                for (int i = 0; i < v.length; i++) {
                    for (int j = 0; j < v[i].length; j++) {
                        str += "at least one cell component of type " 
                                + DataArray.getTypeName(v[i][j]) + newline;
                    }
                }
            }

            if ((ccm & CELLDATA_TYPES) == CELLDATA_TYPES) {
                int[][] v = vndsi.getCellDataVeclens();                
                for (int i = 0; i < v.length; i++) {
                    str += "with types=";
                    for (int j = 0; j < v[i].length; j++) {
                        str += "" + DataArray.getTypeName(v[i][j]);
                        if (j < v[i].length - 1) {
                            str += ",";
                        }
                    }
                    str += newline;
                }
            }

            if ((ccm & CELLDATA_NAME) == CELLDATA_NAME) {
                String[][] v = vndsi.getCellDataNames();
                for (int i = 0; i < v.length; i++) {
                    for (int j = 0; j < v[i].length; j++) {
                        str += "at least one cell component named '" + v[i][j] + "'" + newline;
                    }
                }
            }

            if ((ccm & CELLDATA_NAMES) == CELLDATA_NAMES) {
                String[][] v = vndsi.getCellDataNames();
                
                for (int i = 0; i < v.length; i++) {
                    str += "with names=";
                    for (int j = 0; j < v[i].length; j++) {
                        str += "" + v[i][j];
                        if (j < v[i].length - 1) {
                            str += ",";
                        }
                    }                    
                    str += newline;
                }
                
            }
            
            if ((ccm & CELLS_POINT) == CELLS_POINT) {
                boolean v = vndsi.hasCellsPoint();
                if(v)                    
                    str += "at least one cellset containing POINT cells" + newline;
            }

            if ((ccm & CELLS_2D) == CELLS_2D) {
                boolean v = vndsi.hasCells2D();
                if(v)                    
                    str += "at least one cellset containing 2D cells" + newline;
            }

            if ((ccm & CELLS_3D) == CELLS_3D) {
                boolean v = vndsi.hasCells3D();
                if(v)                    
                    str += "at least one cellset containing 3D cells" + newline;
            }
            
            if ((ccm & CELLS_SEGMENT) == CELLS_SEGMENT) {
                boolean v = vndsi.hasCellsSegment();
                if(v)                    
                    str += "at least one cellset containing SEGMENT cells" + newline;
            }

            if ((ccm & CELLS_TRIANGLE) == CELLS_TRIANGLE) {
                boolean v = vndsi.hasCellsTriangle();
                if(v)                    
                    str += "at least one cellset containing TRIANGLE cells" + newline;
            }

            if ((ccm & CELLS_QUAD) == CELLS_QUAD) {
                boolean v = vndsi.hasCellsQuad();
                if(v)                    
                    str += "at least one cellset containing QUAD cells" + newline;
            }

            if ((ccm & CELLS_TETRA) == CELLS_TETRA) {
                boolean v = vndsi.hasCellsTetra();
                if(v)                    
                    str += "at least one cellset containing TETRA cells" + newline;
            }

            if ((ccm & CELLS_PYRAMID) == CELLS_PYRAMID) {
                boolean v = vndsi.hasCellsPyramid();
                if(v)                    
                    str += "at least one cellset containing PYRAMID cells" + newline;
            }

            if ((ccm & CELLS_PRISM) == CELLS_PRISM) {
                boolean v = vndsi.hasCellsPrism();
                if(v)                    
                    str += "at least one cellset containing PRISM cells" + newline;
            }

            if ((ccm & CELLS_HEXAHEDRON) == CELLS_HEXAHEDRON) {
                boolean v = vndsi.hasCellsHexahedron();
                if(v)                    
                    str += "at least one cellset containing HEXAHEDRON cells" + newline;
            }
            
        }

        if (str.endsWith(newline)) {
            str = str.substring(0, str.lastIndexOf(newline));
        }

        return str;
    }

   private VNDataSchemaComparator()
   {
   }
}
