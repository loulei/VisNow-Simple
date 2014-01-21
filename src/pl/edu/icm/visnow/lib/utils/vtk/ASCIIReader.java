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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.ByteOrder;
import java.util.Scanner;
import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.datasets.IrregularField;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.gui.widgets.FileErrorFrame;

/**
 *
 * @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class ASCIIReader extends Reader {

    protected Scanner scanner = null;

    public ASCIIReader() {
    }

    public IrregularField readTimeVTK(String filename, FileErrorFrame errorFrame) {
        return null;
    }

    @Override
    protected int getInt() {
        return scanner.nextInt();
    }

    @Override
    protected void readArray(float[] a) {
        for (int i = 0; i < a.length; i++) {
            a[i] = scanner.nextFloat();
        }
    }

    @Override
    protected void readArray(int[] a) {
        for (int i = 0; i < a.length; i++) {
            a[i] = scanner.nextInt();
        }
    }

    @Override
    protected void readFloatArrayFrom(float[] a, int type) {
        readArray(a);
    }

    @Override
    protected String nextLine() {
        String line;
        try {
            do {
                line = scanner.nextLine().trim();
                //System.out.println(line);
            } while (line.isEmpty());
            return line;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String nextLine(String[] begin) {
        String[] lBegin = new String[begin.length];
        for (int i = 0; i < begin.length; i++) {
            lBegin[i] = begin[i].toLowerCase();
        }
        String line;
        try {
            do {
                line = scanner.nextLine().trim();
                //System.out.println(line);
                for (int i = 0; i < lBegin.length; i++) {
                    if (line.toLowerCase().startsWith(lBegin[i])) {
                        return line;
                    }
                }
            } while (line != null);
        } catch (Exception e) {
        }
        return null;
    }

    @Override
    protected DataArray readData(int type, int vlen, int nData, String name) {
        try {
            switch (type) {
                case DataArray.FIELD_DATA_BYTE:
                    byte[] bData = new byte[vlen * nData];
                    for (int i = 0; i < bData.length; i++) {
                        bData[i] = scanner.nextByte();
                    }
                    return (DataArray.create(bData, vlen, name));
                case DataArray.FIELD_DATA_SHORT:
                    short[] sData = new short[vlen * nData];
                    for (int i = 0; i < sData.length; i++) {
                        sData[i] = scanner.nextShort();
                    }
                    return (DataArray.create(sData, vlen, name));
                case DataArray.FIELD_DATA_INT:
                    int[] iData = new int[vlen * nData];
                    for (int i = 0; i < iData.length; i++) {
                        iData[i] = scanner.nextInt();
                    }
                    return (DataArray.create(iData, vlen, name));
                case DataArray.FIELD_DATA_FLOAT:
                    float[] fData = new float[vlen * nData];
                    for (int i = 0; i < fData.length; i++) {
                        fData[i] = scanner.nextFloat();
                    }
                    return (DataArray.create(fData, vlen, name));
                case DataArray.FIELD_DATA_DOUBLE:
                    double[] dData = new double[vlen * nData];
                    for (int i = 0; i < dData.length; i++) {
                        dData[i] = scanner.nextDouble();
                    }
                    return (DataArray.create(dData, vlen, name));
            }
        } catch (Exception e) {
        }
        return null;
    }

    @Override
    protected DataArray[] readDataArray(int nData, String l) {
        String name = "";
        int vlen = 1;
        int type = DataArray.FIELD_DATA_UNKNOWN;
        String line = l;
        String[] tokens = line.split("\\s+");
        if (tokens[0].equalsIgnoreCase("SCALARS") && tokens.length >= 3) {
            name = tokens[1];
            if (VTKDataTypeMap.containsKey(tokens[2].toLowerCase())) {
                type = VTKDataTypeMap.get(tokens[2].toLowerCase());
            } else {
                System.out.println("invalid vtk data type in " + line);
                return null;
            }
            if (scanner.hasNext("LOOKUP.*") || scanner.hasNext("lookup.*")) {
                scanner.nextLine();
            }
            return new DataArray[]{readData(type, vlen, nData, name)};
        }

        if (tokens[0].equalsIgnoreCase("COLOR_SCALARS") && tokens.length >= 3) {
            float[] a = new float[Integer.parseInt(tokens[2]) * nData];
            readArray(a);
            byte[] ba = new byte[a.length];
            for (int i = 0; i < ba.length; i++) {
                ba[i] = (byte) (0xff * (int) (255 * a[i]));
            }
            DataArray da = DataArray.create(ba, vlen, tokens[1]);
            da.setUserData(new String[]{"colors"});
            return new DataArray[]{da};
        }

        if (tokens[0].equalsIgnoreCase("VECTORS") || tokens[0].equalsIgnoreCase("NORMALS") && tokens.length >= 3) {
            name = tokens[1];
            vlen = 3;
            if (VTKDataTypeMap.containsKey(tokens[2].toLowerCase())) {
                type = VTKDataTypeMap.get(tokens[2].toLowerCase());
            } else {
                System.out.println("invalid vtk data type in " + line);
                return null;
            }
            return new DataArray[]{readData(type, vlen, nData, name)};
        }

        if (tokens[0].equalsIgnoreCase("TENSORS") && tokens.length >= 3) {
            name = tokens[1];
            vlen = 9;
            if (VTKDataTypeMap.containsKey(tokens[2].toLowerCase())) {
                type = VTKDataTypeMap.get(tokens[2].toLowerCase());
            } else {
                System.out.println("invalid vtk data type in " + line);
                return null;
            }
            DataArray da = readData(type, vlen, nData, name);
            da.setMatrixProperties(new int[]{3, 3}, false);
            return new DataArray[]{da};
        }

        if (tokens[0].equalsIgnoreCase("LOOKUP_TABLE") && tokens.length >= 3) {
            int n = Integer.parseInt(tokens[2]);
            for (int i = 0; i < 4 * n; i++) {
                scanner.nextFloat();
            }
            return null;
        }
        return null;
    }

    @Override
    public Field readVTK(String filename, ByteOrder order) {
        try {
            scanner = new Scanner(new FileReader(filename));
        } catch (FileNotFoundException ex) {
        }
        if (scanner == null) {
            return null;
        }
        String line = scanner.nextLine();
        if (!line.startsWith("# vtk DataFile")) {
            return null;
        }
        outFieldName = scanner.nextLine();
        line = nextLine(new String[]{"DATASET"});
        if (line == null) {
            return null;
        }
        String[] tokens = tokens = line.split("\\s+");
        if (tokens.length < 2 || !VTKFieldTypeMap.containsKey(tokens[1])) {
            return null;
        }
        VTKFieldType = VTKFieldTypeMap.get(tokens[1].toUpperCase());
        if (VTKFieldType <= RECTILINEAR_GRID) {
            RegularField outField = readRegularFile();
            scanner.close();
            return outField;
        } else if (VTKFieldType == UNSTRUCTURED_GRID) {
            IrregularField outField = readIrregularFileFromVTKGrid();
            scanner.close();
            return outField;
        } else if (VTKFieldType == POLYDATA) {
            IrregularField outField = readIrregularFileFromVTKPolys();
            scanner.close();
            return outField;
        }

        return null;
    }
}
