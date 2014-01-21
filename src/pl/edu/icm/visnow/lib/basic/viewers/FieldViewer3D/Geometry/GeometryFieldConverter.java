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
package pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry;

import java.util.ArrayList;
import pl.edu.icm.visnow.datasets.CellArray;
import pl.edu.icm.visnow.datasets.CellSet;
import pl.edu.icm.visnow.datasets.IrregularField;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.datasets.VNObject;
import pl.edu.icm.visnow.datasets.cells.Cell;
import pl.edu.icm.visnow.datasets.dataarrays.BitArray;
import pl.edu.icm.visnow.datasets.dataarrays.BooleanDataArray;
import pl.edu.icm.visnow.datasets.dataarrays.ComplexDataArray;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.datasets.dataarrays.LogicDataArray;
import pl.edu.icm.visnow.datasets.dataarrays.ObjectDataArray;
import pl.edu.icm.visnow.datasets.dataarrays.StringDataArray;

/**
 * @author Bartosz Borucki (babor@icm.edu.pl) Warsaw University,
 * Interdisciplinary Centre for Mathematical and Computational Modelling
 * 19 July 2013
 */
public class GeometryFieldConverter {

    public static IrregularField pac2field(ArrayList<PointDescriptor> pointsDescriptors,
            ArrayList<ConnectionDescriptor> connectionDescriptors,
            boolean addClassData,
            boolean addFieldDataComponents,
            RegularField inField) {
        int nPoints = pointsDescriptors.size();
        if (nPoints < 1) {
            return null;
        }

        IrregularField field = new IrregularField(nPoints);
        field.setNSpace(3);

        int[] cells = new int[nPoints];
        float[] coords = new float[nPoints * 3];
        int[] data = new int[nPoints];
        String[] names = new String[nPoints];
        int[] classes = new int[nPoints];
        float[] p;
        for (int i = 0; i < data.length; i++) {
            p = pointsDescriptors.get(i).getWorldCoords();
            data[i] = i + 1;
            coords[3 * i] = p[0];
            coords[3 * i + 1] = p[1];
            coords[3 * i + 2] = p[2];
            cells[i] = i;
            names[i] = pointsDescriptors.get(i).getName();
            classes[i] = pointsDescriptors.get(i).getMembership();
        }

        field.setCoords(coords);
        field.addData(DataArray.create(data, 1, "points"));
        field.addData(DataArray.create(names, 1, "names"));
        if (addClassData) {
            field.addData(DataArray.create(classes, 1, "class"));
        }
        CellArray ca = new CellArray(Cell.POINT, cells, null, null);
        CellSet cs = new CellSet("points");
        cs.setCellArray(ca);
        cs.generateDisplayData(coords);
        field.addCellSet(cs);


        if (!connectionDescriptors.isEmpty()) {
            int nLines = connectionDescriptors.size();
            int[] ccells = new int[nLines * 2];
            String[] cnames = new String[nLines];
            for (int i = 0; i < nLines; i++) {
                ConnectionDescriptor cd = connectionDescriptors.get(i);
                ccells[2 * i] = pointsDescriptors.indexOf(cd.getP1());
                ccells[2 * i + 1] = pointsDescriptors.indexOf(cd.getP2());
                cnames[i] = cd.getName();
            }

            CellArray cca = new CellArray(Cell.SEGMENT, ccells, null, null);
            CellSet ccs = new CellSet("connections");
            ccs.setCellArray(cca);
            ccs.addData(DataArray.create(cnames, 1, "names"));
            ccs.generateDisplayData(coords);
            field.addCellSet(ccs);
        }

        if (addFieldDataComponents && inField != null) {
            int[] fDims = inField.getDims();
            int dimX = fDims[0];
            int dimY = fDims[1];

            int[] ind;
            int vlen;
            for (int i = 0; i < inField.getNData(); i++) {
                DataArray tmpDa = inField.getData(i);
                vlen = tmpDa.getVeclen();
                switch (tmpDa.getType()) {
                    case DataArray.FIELD_DATA_BYTE:
                        byte[] bData = new byte[nPoints * vlen];
                        byte[] inBData = tmpDa.getBData();
                        for (int j = 0; j < nPoints; j++) {
                            ind = pointsDescriptors.get(j).getIndices();
                            for (int l = 0; l < vlen; l++) {
                                bData[vlen * j + l] = inBData[vlen * (ind[2] * dimX * dimY + ind[1] * dimX + ind[0]) + l];
                            }
                        }
                        field.addData(DataArray.create(bData, vlen, tmpDa.getName()));
                        break;
                    case DataArray.FIELD_DATA_SHORT:
                        short[] sData = new short[nPoints * vlen];
                        short[] inSData = tmpDa.getSData();
                        for (int j = 0; j < nPoints; j++) {
                            ind = pointsDescriptors.get(j).getIndices();
                            for (int l = 0; l < vlen; l++) {
                                sData[vlen * j + l] = inSData[vlen * (ind[2] * dimX * dimY + ind[1] * dimX + ind[0]) + l];
                            }
                        }
                        field.addData(DataArray.create(sData, vlen, tmpDa.getName()));
                        break;
                    case DataArray.FIELD_DATA_INT:
                        int[] iData = new int[nPoints * vlen];
                        int[] inIData = tmpDa.getIData();
                        for (int j = 0; j < nPoints; j++) {
                            ind = pointsDescriptors.get(j).getIndices();
                            for (int l = 0; l < vlen; l++) {
                                iData[vlen * j + l] = inIData[vlen * (ind[2] * dimX * dimY + ind[1] * dimX + ind[0]) + l];
                            }
                        }
                        field.addData(DataArray.create(iData, vlen, tmpDa.getName()));
                        break;
                    case DataArray.FIELD_DATA_FLOAT:
                        float[] fData = new float[nPoints * vlen];
                        float[] inFData = tmpDa.getFData();
                        for (int j = 0; j < nPoints; j++) {
                            ind = pointsDescriptors.get(j).getIndices();
                            for (int l = 0; l < vlen; l++) {
                                fData[j * vlen + l] = inFData[vlen * (ind[2] * dimX * dimY + ind[1] * dimX + ind[0]) + l];
                            }
                        }
                        field.addData(DataArray.create(fData, vlen, tmpDa.getName()));
                        break;
                    case DataArray.FIELD_DATA_DOUBLE:
                        double[] dData = new double[nPoints * vlen];
                        double[] inDData = tmpDa.getDData();
                        for (int j = 0; j < nPoints; j++) {
                            ind = pointsDescriptors.get(j).getIndices();
                            for (int l = 0; l < vlen; l++) {
                                dData[j * vlen + l] = inDData[vlen * (ind[2] * dimX * dimY + ind[1] * dimX + ind[0]) + l];
                            }
                        }
                        field.addData(DataArray.create(dData, vlen, tmpDa.getName()));
                        break;
                    case DataArray.FIELD_DATA_BOOLEAN:
                        boolean[] booData = new boolean[nPoints * vlen];
                        boolean[] inBooData = ((BooleanDataArray) tmpDa).getBoolData();
                        for (int j = 0; j < nPoints; j++) {
                            ind = pointsDescriptors.get(j).getIndices();
                            for (int l = 0; l < vlen; l++) {
                                booData[j * vlen + l] = inBooData[vlen * (ind[2] * dimX * dimY + ind[1] * dimX + ind[0]) + l];
                            }
                        }
                        field.addData(new BooleanDataArray(booData, vlen, tmpDa.getName(), tmpDa.getUnit(), tmpDa.getUserData()));
                        break;
                    case DataArray.FIELD_DATA_COMPLEX:
                        float[] fDataRe = new float[nPoints * vlen];
                        float[] fDataIm = new float[nPoints * vlen];
                        float[] inFDataRe = ((ComplexDataArray) tmpDa).getFRealData();
                        float[] inFDataIm = ((ComplexDataArray) tmpDa).getFImagData();
                        int off;
                        for (int j = 0; j < nPoints; j++) {
                            ind = pointsDescriptors.get(j).getIndices();
                            for (int l = 0; l < vlen; l++) {
                                off = vlen * (ind[2] * dimX * dimY + ind[1] * dimX + ind[0]) + l;
                                fDataRe[j * vlen + l] = inFDataRe[off];
                                fDataIm[j * vlen + l] = inFDataIm[off];
                            }
                        }
                        field.addData(new ComplexDataArray(fDataRe, fDataIm, vlen, tmpDa.getName()));
                        break;
                    case DataArray.FIELD_DATA_LOGIC:
                        BitArray lData = new BitArray(nPoints * vlen);
                        BitArray inLData = ((LogicDataArray) tmpDa).getBitArray();
                        for (int j = 0; j < nPoints; j++) {
                            ind = pointsDescriptors.get(j).getIndices();
                            for (int l = 0; l < vlen; l++) {
                                lData.setValueAtIndex(j * vlen + l, inLData.getValueAtIndex(vlen * (ind[2] * dimX * dimY + ind[1] * dimX + ind[0]) + l));
                            }
                        }
                        field.addData(new LogicDataArray(lData, vlen, tmpDa.getName(), tmpDa.getUnit(), tmpDa.getUserData()));
                        break;
                    case DataArray.FIELD_DATA_OBJECT:
                        VNObject[] oData = new VNObject[nPoints * vlen];
                        VNObject[] inOData = ((ObjectDataArray) tmpDa).getObjData();
                        for (int j = 0; j < nPoints; j++) {
                            ind = pointsDescriptors.get(j).getIndices();
                            for (int l = 0; l < vlen; l++) {
                                oData[j * vlen + l] = inOData[vlen * (ind[2] * dimX * dimY + ind[1] * dimX + ind[0]) + l];
                            }
                        }
                        field.addData(DataArray.create(oData, vlen, tmpDa.getName()));
                        break;
                    case DataArray.FIELD_DATA_STRING:
                        String[] strData = new String[nPoints * vlen];
                        String[] inStrData = ((StringDataArray) tmpDa).getStringData();
                        for (int j = 0; j < nPoints; j++) {
                            ind = pointsDescriptors.get(j).getIndices();
                            for (int l = 0; l < vlen; l++) {
                                strData[j * vlen + l] = inStrData[vlen * (ind[2] * dimX * dimY + ind[1] * dimX + ind[0]) + l];
                            }
                        }
                        field.addData(DataArray.create(strData, vlen, tmpDa.getName()));
                        break;
                }
            }
        }
        return field;
    }

    public static void field2pac(IrregularField ptsField,
                                    RegularField inField,
                                    ArrayList<PointDescriptor> pointsDescriptors,
                                    ArrayList<ConnectionDescriptor> connectionDescriptors) {

        
        int nPoints = ptsField.getNNodes();

        String[] names = null;
        if (ptsField.getData("names") != null && ptsField.getData("names").getType() == DataArray.FIELD_DATA_STRING) {
            names = ((StringDataArray) ptsField.getData("names")).getStringData();
        } else if (ptsField.getData("name") != null && ptsField.getData("name").getType() == DataArray.FIELD_DATA_STRING) {
            names = ((StringDataArray) ptsField.getData("name")).getStringData();
        }

        int[] classes = null;
        if (ptsField.getData("class") != null && ptsField.getData("class").getType() == DataArray.FIELD_DATA_INT) {
            classes = ptsField.getData("class").getIData();
        } else if (ptsField.getData("classes") != null && ptsField.getData("classes").getType() == DataArray.FIELD_DATA_INT) {
            classes = ptsField.getData("classes").getIData();
        }
        
        int cId;
        float[] coords = ptsField.getCoords();
        //int[] data = ptsField.getData(0).getIData();        
        int c = 0;
        for (int i = 0; i < nPoints; i++) {
            String name;
            if (names != null) {
                name = names[i];
            } else {
                name = "ip" + c++;
            }

            if (classes != null) {
                cId = classes[i];
            } else {
                cId = -1;
            }

            if(inField == null) {
                float[] cp = new float[3];
                for (int j = 0; j < 3; j++) {
                    cp[j] = coords[3*i + j];                    
                }            
                pointsDescriptors.add(new PointDescriptor(name, null, cp, cId));
            } else {
                int[] p = inField.getIndices(coords[3 * i], coords[3 * i + 1], coords[3 * i + 2]);
                float[] cp = inField.getGridCoords((float) (p[0]), (float) (p[1]), (float) (p[2]));
                pointsDescriptors.add(new PointDescriptor(name, p, cp, cId));                
            }
        }
        
        for (int i = 0; i < ptsField.getNCellSets(); i++) {
            CellSet cs = ptsField.getCellSet(i);
            if (cs.getCellArray(Cell.SEGMENT) != null
                    && cs.getCellArray(Cell.SEGMENT).getNCells() > 0) {
                CellArray ca = cs.getCellArray(Cell.SEGMENT);
                int[] nodes = ca.getNodes();
                int nConns = ca.getNCells();
                String[] connNames = null;
                if (cs.getNData() > 0 && cs.getData("names") != null && cs.getData("names").getType() == DataArray.FIELD_DATA_STRING) {
                    connNames = ((StringDataArray) cs.getData("names")).getStringData();
                } else if (cs.getNData() > 0 && cs.getData("name") != null && cs.getData("name").getType() == DataArray.FIELD_DATA_STRING) {
                    connNames = ((StringDataArray) cs.getData("name")).getStringData();
                }

                int[] conn = new int[2];
                for (int j = 0; j < nConns; j++) {
                    conn[0] = nodes[2 * j];
                    conn[1] = nodes[2 * j + 1];
                    String connName;
                    if (connNames != null) {
                        connName = connNames[i];
                    } else {
                        connName = "" + names[conn[0]] + "->" + names[conn[1]];
                    }
                    connectionDescriptors.add(new ConnectionDescriptor(connName, pointsDescriptors.get(conn[0]), pointsDescriptors.get(conn[1])));
                }
            }
        }
    }
}
