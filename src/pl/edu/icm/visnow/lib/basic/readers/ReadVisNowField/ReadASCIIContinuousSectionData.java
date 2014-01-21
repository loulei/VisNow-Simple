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

import java.util.Locale;
import java.util.Scanner;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.lib.basic.readers.ReadVisNowField.utils.FileSectionSchema;

/**
 *
 * @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class ReadASCIIContinuousSectionData {

    public static int readSectionData(SectionModel model, Scanner scanner, String filePath, String decimalSep) {
        int nComps = model.nItems;
        int nNodes = model.nData;
        int[] offsets = model.offsets;
        int[] types = model.types;
        int[] vlens = model.vlens;
        int[] ind = model.ind;
        boolean[][] boolArrs = model.boolArrs;
        byte[][] byteArrs = model.byteArrs;
        short[][] shortArrs = model.shortArrs;
        int[][] intArrs = model.intArrs;
        float[][] floatArrs = model.floatArrs;
        double[][] dblArrs = model.dblArrs;
        FileSectionSchema schema = model.sectionSchema;
        if(",".equals(decimalSep)) {
            scanner.useLocale(Locale.FRENCH);
        } else {
            scanner.useLocale(Locale.US);
        }            
        try {
            if (schema.getComponents().isEmpty()) {
                return 0;
            }
            String sep = schema.getSeparator();
            if (sep != null && !sep.isEmpty()) {
                scanner.useDelimiter("\\s*" + sep + "\\s*");
            }
            for (int k = 0; k < nNodes; k++) {
                int cPos = 0;
                for (int l = 0; l < nComps; l++) {
                    int ll = offsets[l] - cPos;
                    cPos = offsets[l] + 1;
                    for (int i = 0; i < ll; i++) {
                        scanner.next();
                    }
                    switch (types[l]) {
                        case DataArray.FIELD_DATA_BOOLEAN:
                            String s = scanner.next();
                            boolArrs[l][ind[l]] = s.startsWith("1") || s.startsWith("t");
                            ind[l] += vlens[l];
                            break;
                        case DataArray.FIELD_DATA_BYTE:
                            byteArrs[l][ind[l]] = (byte) (0xff & scanner.nextInt());
                            ind[l] += vlens[l];
                            break;
                        case DataArray.FIELD_DATA_SHORT:
                            shortArrs[l][ind[l]] = scanner.nextShort();
                            ind[l] += vlens[l];
                            break;
                        case DataArray.FIELD_DATA_INT:
                            intArrs[l][ind[l]] = scanner.nextInt();
                            ind[l] += vlens[l];
                            break;
                        case DataArray.FIELD_DATA_FLOAT:
                            floatArrs[l][ind[l]] = scanner.nextFloat();
                            ind[l] += vlens[l];
                            break;
                        case DataArray.FIELD_DATA_DOUBLE:
                            dblArrs[l][ind[l]] = scanner.nextDouble();
                            ind[l] += vlens[l];
                            break;
                    }
                }
            }
            return 0;
        } catch (Exception e) {
            outputError("error in data file " + filePath, filePath, 0, null);
            e.printStackTrace();
            return 2;
        }

    }

    static void outputError(String text, String fname, int lineNumber, Exception e) {
        System.err.println("ERROR: " + text + "; in function " + fname + " line " + lineNumber);
        //e.printStackTrace();
    }
}