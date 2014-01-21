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

import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;

/**
 *
 * @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class BinaryReader extends Reader {

    protected ImageInputStream in;

    public BinaryReader() {
    }

    @Override
    protected int getInt() {
        try {
            return in.readInt();
        } catch (IOException ex) {
            Logger.getLogger(BinaryReader.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }

    @Override
    protected void readArray(float[] a) {
        try {
            in.readFully(a, 0, a.length);
        } catch (IOException ex) {
            Logger.getLogger(BinaryReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void readArray(int[] a) {
        try {
            in.readFully(a, 0, a.length);
        } catch (IOException ex) {
            Logger.getLogger(BinaryReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void readFloatArrayFrom(float[] a, int type) {
        try {
            switch (type) {
                case DataArray.FIELD_DATA_BYTE:
                    byte[] bData = new byte[a.length];
                    in.readFully(bData);
                    for (int i = 0; i < a.length; i++) {
                        a[i] = bData[i] & 0xff;
                    }
                    break;
                case DataArray.FIELD_DATA_SHORT:
                    short[] sData = new short[a.length];
                    in.readFully(sData, 0, sData.length);
                    for (int i = 0; i < a.length; i++) {
                        a[i] = sData[i];
                    }
                    break;
                case DataArray.FIELD_DATA_INT:
                    int[] iData = new int[a.length];
                    in.readFully(iData, 0, iData.length);
                    for (int i = 0; i < a.length; i++) {
                        a[i] = iData[i];
                    }
                    break;
                case DataArray.FIELD_DATA_FLOAT:
                    in.readFully(a, 0, a.length);
                    break;
                case DataArray.FIELD_DATA_DOUBLE:
                    double[] dData = new double[a.length];
                    in.readFully(dData, 0, dData.length);
                    for (int i = 0; i < a.length; i++) {
                        a[i] = (float) dData[i];
                    }
                    break;
            }
        } catch (Exception e) {
        }
    }

    @Override
    protected String nextLine() {
        String line;
        try {
            do {
                line = in.readLine().trim();
                //System.out.println(line);
            } while (line.isEmpty());
            return line;
        } catch (Exception e) {
            return null;
        }
    }

    protected String nextLine(String[] begin) {
        String[] lBegin = new String[begin.length];
        for (int i = 0; i < begin.length; i++) {
            lBegin[i] = begin[i].toLowerCase();
        }
        String line;
        try {
            do {
                line = in.readLine().trim();
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
                    in.readFully(bData);
                    return (DataArray.create(bData, vlen, name));
                case DataArray.FIELD_DATA_SHORT:
                    short[] sData = new short[vlen * nData];
                    in.readFully(sData, 0, sData.length);
                    return (DataArray.create(sData, vlen, name));
                case DataArray.FIELD_DATA_INT:
                    int[] iData = new int[vlen * nData];
                    in.readFully(iData, 0, iData.length);
                    return (DataArray.create(iData, vlen, name));
                case DataArray.FIELD_DATA_FLOAT:
                    float[] fData = new float[vlen * nData];
                    in.readFully(fData, 0, fData.length);
                    return (DataArray.create(fData, vlen, name));
                case DataArray.FIELD_DATA_DOUBLE:
                    double[] dData = new double[vlen * nData];
                    in.readFully(dData, 0, dData.length);
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
            tokens = nextLine().split("\\s+");
            boolean defaultLookupTable = tokens[1].equalsIgnoreCase("default");
            DataArray da = readData(type, vlen, nData, name);

            return new DataArray[]{da};
        }

        if (tokens[0].equalsIgnoreCase("COLOR_SCALARS") && tokens.length >= 3) {

            vlen = Integer.parseInt(tokens[2]);
            DataArray da = readData(DataArray.FIELD_DATA_BYTE, vlen, nData, tokens[1]);
            da.setUserData(new String[]{"colors"});
            if (vlen > 1) {
                byte[] bdata = da.getBData();
                DataArray[] res = new DataArray[vlen];
                byte[][] samples = new byte[vlen][nData];
                for (int i = 0; i < vlen; i++) {
                    for (int j = 0; j < nData; j++) {
                        samples[i][j] = bdata[vlen * j + i];
                    }
                    res[i] = DataArray.create(samples[i], 1, "grayscale" + (i + 1));
                }
                if (vlen == 3) {
                    res[0].setName("redData");
                    res[1].setName("greenData");
                    res[2].setName("blueData");
                }
                return res;
            } else {
                return new DataArray[]{da};
            }
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
            try {
                int n = Integer.parseInt(in.readLine().split("\\s+")[2]);
                for (int i = 0; i < 4 * n; i++) {
                    in.readFloat();
                }
            } catch (IOException iOException) {
            } catch (NumberFormatException numberFormatException) {
            }
            return null;
        }
        return null;
    }

    @Override
    public Field readVTK(String filename, ByteOrder order) {
        try {
            in = new FileImageInputStream(new File(filename));
            if (in == null) {
                return null;
            }
            in.setByteOrder(order);
            String line = in.readLine();
            if (!line.startsWith("# vtk DataFile")) {
                return null;
            }
            outFieldName = in.readLine();
            line = nextLine(new String[]{"DATASET"});
            if (line == null) {
                return null;
            }
            String[] tokens = line.split("\\s+");
            if (tokens.length < 2 || !VTKFieldTypeMap.containsKey(tokens[1])) {
                return null;
            }
            VTKFieldType = VTKFieldTypeMap.get(tokens[1].toUpperCase());
            if (VTKFieldType <= RECTILINEAR_GRID) {
                return readRegularFile();
            } else if (VTKFieldType == UNSTRUCTURED_GRID) {
                return readIrregularFileFromVTKGrid();
            } else if (VTKFieldType == POLYDATA) {
                return readIrregularFileFromVTKPolys();
            }
            return null;
        } catch (Exception e) {
        }
        return null;
    }
}
