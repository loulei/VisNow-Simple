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

package pl.edu.icm.visnow.lib.basic.readers.ReadFluent;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import javax.imageio.stream.FileImageInputStream;
import pl.edu.icm.visnow.datasets.CellArray;
import pl.edu.icm.visnow.datasets.CellSet;
import pl.edu.icm.visnow.datasets.IrregularField;
import pl.edu.icm.visnow.datasets.cells.Cell;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;

/**
 * @author Bartosz Borucki (babor@icm.edu.pl) 
 * University of Warsaw
 * Interdisciplinary Centre for Mathematical and Computational Modelling * 
 */
public class FluentReader {

    private String fileName = null;
    private float[] coords = null;
    private int nNodes = 0;
    private ArrayList<FluentCell> cells = new ArrayList<FluentCell>();
    private int nCells = 0;
    private ArrayList<FluentFace> faces = new ArrayList<FluentFace>();
    private int nFaces = 0;
    private String caseBuffer;
    private String dataBuffer;
    private HashMap<Integer, String> variableNames = null;
    private ArrayList<Integer> cellZones = new ArrayList<Integer>();
    private ArrayList<ScalarDataChunk> scalarDataChunks = new ArrayList<ScalarDataChunk>();
    private ArrayList<VectorDataChunk> vectorDataChunks = new ArrayList<VectorDataChunk>();
    private ArrayList<ArrayList<Integer>> subSectionZones = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> subSectionIds = new ArrayList<Integer>();
    private ArrayList<Integer> subSectionSize = new ArrayList<Integer>();
    private ArrayList<Integer> scalarSubSectionIds = new ArrayList<Integer>();
    private ArrayList<Integer> vectorSubSectionIds = new ArrayList<Integer>();
    private FileImageInputStream fluentCaseFile;
    private FileImageInputStream fluentDataFile;
    private ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;
    private ArrayList<String> cellDataArraySelection = new ArrayList<String>();
    private int nSpace;
    private int nScalarData = 0;
    private int nVectorData = 0;
    private int nData = 0;

    public FluentReader(String filename) {
        this.fileName = filename;
    }

    private boolean openCaseFile(String filename) {
        if (filename == null) {
            return false;
        }

        try {
            this.fluentCaseFile = new FileImageInputStream(new File(filename));
            this.fluentCaseFile.setByteOrder(byteOrder);
            return (this.fluentCaseFile != null);
        } catch (IOException ex) {
            this.fluentCaseFile = null;
            return false;
        }
    }

    private boolean openDataFile(String filename) {
        if (filename == null) {
            return false;
        }

        try {
            String dfilename = filename.substring(0, filename.length() - 3);
            dfilename = dfilename + "dat";
            this.fluentDataFile = new FileImageInputStream(new File(dfilename));
            this.fluentDataFile.setByteOrder(byteOrder);
            return (this.fluentDataFile != null);
        } catch (IOException ex) {
            this.fluentDataFile = null;
            return false;
        }
    }

    private void closeDataFile() {
        if (this.fluentDataFile == null) {
            return;
        }

        try {
            this.fluentDataFile.close();
        } catch (IOException ex) {
        }
    }

    private void closeCaseFile() {
        if (this.fluentCaseFile == null) {
            return;
        }

        try {
            this.fluentCaseFile.close();
        } catch (IOException ex) {
        }
    }

    private boolean getCaseChunk() {
        try {
            this.caseBuffer = "";
            StringBuilder s = new StringBuilder();            
            int v;
            char c;

            // Find beginning of chunk
            while (true) {
                v = this.fluentCaseFile.read();
                if (v == -1) { //EOF                    
                    return false;
                }
                c = (char) v;
                if (c == '(') {
                    s.append(c);
                    break;
                }
            }

            // Gather index
            String index = "";
            while (true) {
                v = this.fluentCaseFile.read();
                if (v == -1) { //EOF
                    return false;
                }
                c = (char) v;
                s.append(c);
                if (c == ' ') {
                    break;
                }
                index += c;
            }

            // If the index is 3 digits or more, then binary, otherwise ascii.
            if (index.length() > 2) {  // Binary Chunk
                String end = "End of Binary Section   " + index + ")";
                int len = end.length();

                
                // Load the case buffer enough to start comparing to the end.
                while (s.length() < len) {
                    v = this.fluentCaseFile.read();
                    if (v == -1) { //EOF
                        return false;
                    }
                    c = (char) v;
                    s.append(c);
                }

                while (!end.equals(s.substring(s.length()-len))) {
                    v = this.fluentCaseFile.read();
                    if (v == -1) { //EOF
                        return false;
                    }
                    c = (char) v;
                    s.append(c);
                }
            } else {  // Ascii Chunk
                int level = 0;
                while (true) {
                    v = this.fluentCaseFile.read();
                    if (v == -1) { //EOF
                        return false;
                    }
                    c = (char) v;
                    s.append(c);
                    if (c == ')' && level == 0) {
                        break;
                    }
                    if (c == '(') {
                        level++;
                    }
                    if (c == ')') {
                        level--;
                    }
                }
            }
            this.caseBuffer = s.toString();
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    private boolean getDataChunk() {
        try {
            this.dataBuffer = "";
            StringBuilder s = new StringBuilder();            
            int v;
            char c;

            // Find beginning of chunk
            while (true) {
                v = this.fluentDataFile.read();
                if (v == -1) { //EOF
                    return false;
                }
                c = (char) v;
                if (c == '(') {
                    s.append(c);
                    break;
                }
            }

            // Gather index
            String index = "";
            while (true) {
                v = this.fluentDataFile.read();
                if (v == -1) { //EOF
                    return false;
                }
                c = (char) v;
                s.append(c);

                if (c == ' ') {
                    break;
                }
                index += c;
            }

            // If the index is 3 digits or more, then binary, otherwise ascii.
            if (index.length() > 2) {  // Binary Chunk
                String end = "End of Binary Section   " + index + ")";
                int len = end.length();

                // Load the data buffer enough to start comparing to the end std::string.
                while (s.length() < len) {
                    v = this.fluentDataFile.read();
                    if (v == -1) { //EOF
                        return false;
                    }
                    c = (char) v;
                    s.append(c);
                }

                while (!end.equals(s.substring(s.length()-len))) {
                    v = this.fluentDataFile.read();
                    if (v == -1) { //EOF
                        return false;
                    }
                    c = (char) v;
                    s.append(c);
                }
            } else {  // Ascii Chunk
                int level = 0;
                while (true) {
                    v = this.fluentDataFile.read();
                    if (v == -1) { //EOF
                        return false;
                    }
                    c = (char) v;
                    s.append(c);
                    if (c == ')' && level == 0) {
                        break;
                    }
                    if (c == '(') {
                        level++;
                    }
                    if (c == ')') {
                        level--;
                    }
                }
            }
            this.dataBuffer = s.toString();
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    private int getCaseIndex() {
        String sindex = "";
        int i = 1;
        while (this.caseBuffer.charAt(i) != ' ') {
            sindex += this.caseBuffer.charAt(i++);
        }
        return Integer.parseInt(sindex);
    }

    private int getDataIndex() {
        String sindex = "";
        int i = 1;
        while (this.dataBuffer.charAt(i) != ' ') {
            sindex += this.dataBuffer.charAt(i++);
        }
        return Integer.parseInt(sindex);
    }

    private int getNSpace() {
        int start = this.caseBuffer.indexOf("(", 1);
        String info = this.caseBuffer.substring(start + 4, start + 5);
        return Integer.parseInt(info);
    }

    private void getLittleEndianFlag() {
        int start = this.caseBuffer.indexOf('(', 1);
        int end = this.caseBuffer.indexOf(')', 1);
        String info = this.caseBuffer.substring(start + 1, end);
        String[] infos = info.split(" ");
        int flag = Integer.parseInt(infos[0]);
        if (flag == 60) {
            this.byteOrder = ByteOrder.LITTLE_ENDIAN;
        } else {
            this.byteOrder = ByteOrder.BIG_ENDIAN;
        }
    }

    private boolean isSwapBytes() {
        return (this.byteOrder == ByteOrder.LITTLE_ENDIAN);
    }

    private int getCaseBufferInt(int off) {
        byte[] b = new byte[4];
        for (int j = 0; j < 4; j++) {
            if (this.isSwapBytes()) {
                b[3 - j] = (byte) this.caseBuffer.charAt(off + j);
            } else {
                b[j] = (byte) this.caseBuffer.charAt(off + j);
            }
        }
        ByteBuffer buf = ByteBuffer.wrap(b);
        return buf.getInt();
    }

    private int getDataBufferInt(int off) {
        byte[] b = new byte[4];
        for (int j = 0; j < 4; j++) {
            if (this.isSwapBytes()) {
                b[3 - j] = (byte) this.dataBuffer.charAt(off + j);
            } else {
                b[j] = (byte) this.dataBuffer.charAt(off + j);
            }
        }
        ByteBuffer buf = ByteBuffer.wrap(b);
        return buf.getInt();
    }

    private float getCaseBufferFloat(int off) {
        byte[] b = new byte[4];
        for (int j = 0; j < 4; j++) {
            if (this.isSwapBytes()) {
                b[3 - j] = (byte) this.caseBuffer.charAt(off + j);
            } else {
                b[j] = (byte) this.caseBuffer.charAt(off + j);
            }
        }
        ByteBuffer buf = ByteBuffer.wrap(b);
        return buf.getFloat();
    }

    private float getDataBufferFloat(int off) {
        byte[] b = new byte[4];
        for (int j = 0; j < 4; j++) {
            if (this.isSwapBytes()) {
                b[3 - j] = (byte) this.dataBuffer.charAt(off + j);
            } else {
                b[j] = (byte) this.dataBuffer.charAt(off + j);
            }
        }
        ByteBuffer buf = ByteBuffer.wrap(b);
        return buf.getFloat();
    }

    private double getCaseBufferDouble(int off) {
        byte[] b = new byte[8];
        for (int j = 0; j < 8; j++) {
            if (this.isSwapBytes()) {
                b[7 - j] = (byte) this.caseBuffer.charAt(off + j);
            } else {
                b[j] = (byte) this.caseBuffer.charAt(off + j);
            }
        }
        ByteBuffer buf = ByteBuffer.wrap(b);
        return buf.getDouble();
    }

    private double getDataBufferDouble(int off) {
        byte[] b = new byte[8];
        for (int j = 0; j < 8; j++) {
            if (this.isSwapBytes()) {
                b[7 - j] = (byte) this.dataBuffer.charAt(off + j);
            } else {
                b[j] = (byte) this.dataBuffer.charAt(off + j);
            }
        }
        ByteBuffer buf = ByteBuffer.wrap(b);
        return buf.getDouble();
    }

    private void getNodesAscii() {
        int start = this.caseBuffer.indexOf('(', 1);
        int end = this.caseBuffer.indexOf(')', 1);
        String info = this.caseBuffer.substring(start + 1, end);
        int zoneId, firstIndex, lastIndex, type, nd;
        String[] infos = info.split(" ");
        zoneId = Integer.parseInt(infos[0], 16);
        firstIndex = Integer.parseInt(infos[1], 16);
        lastIndex = Integer.parseInt(infos[2], 16);
        type = Integer.parseInt(infos[3]);
        nd = Integer.parseInt(infos[4]);

        if (zoneId == 0) {
            this.nNodes = lastIndex;
            //this.nodes = new float[nNodes * nSpace];
            this.coords = new float[nNodes * 3];
        } else {
            int dstart = this.caseBuffer.indexOf('(', 5);
            int dend = this.caseBuffer.indexOf(')', dstart + 1);
            String pdata = this.caseBuffer.substring(dstart + 1, dend);
            StringTokenizer st = new StringTokenizer(pdata, " \n", false);
            double x, y, z;
            if (this.nSpace == 3) {
                for (int i = firstIndex - 1; i <= lastIndex - 1; i++) {
                    x = Double.parseDouble(st.nextToken());
                    y = Double.parseDouble(st.nextToken());
                    z = Double.parseDouble(st.nextToken());
                    this.coords[i * 3] = (float) x;
                    this.coords[i * 3 + 1] = (float) y;
                    this.coords[i * 3 + 2] = (float) z;
                }
            } else {
                for (int i = firstIndex - 1; i <= lastIndex - 1; i++) {
                    x = Double.parseDouble(st.nextToken());
                    y = Double.parseDouble(st.nextToken());
                    //force 3D field
                    z = 0.0;
                    this.coords[i * 3] = (float) x;
                    this.coords[i * 3 + 1] = (float) y;
                    this.coords[i * 3 + 2] = (float) z;
                }
            }
        }
    }

    private void getCellTreeAscii() {
        int start = this.caseBuffer.indexOf('(', 1);
        int end = this.caseBuffer.indexOf(')', 1);
        String info = this.caseBuffer.substring(start + 1, end);
        int cellId0, cellId1, parentZoneId, childZoneId;
        String[] infos = info.split(" ");
        cellId0 = Integer.parseInt(infos[0], 16);
        cellId1 = Integer.parseInt(infos[1], 16);
        parentZoneId = Integer.parseInt(infos[2], 16);
        childZoneId = Integer.parseInt(infos[3], 16);
        int dstart = this.caseBuffer.indexOf('(', 7);
        int dend = this.caseBuffer.indexOf(')', dstart + 1);
        String pdata = this.caseBuffer.substring(dstart + 1, dend);
        StringTokenizer st = new StringTokenizer(pdata, " \n", false);
        int numberOfKids, kid;
        for (int i = cellId0 - 1; i <= cellId1 - 1; i++) {
            this.cells.get(i).setParent(1);
            numberOfKids = Integer.parseInt(st.nextToken(), 32);
            for (int j = 0; j < numberOfKids; j++) {
                kid = Integer.parseInt(st.nextToken(), 32) - 1;
                this.cells.get(kid).setChild(1);
            }
        }
    }

    private void getCellTreeBinary() {

        int start = this.caseBuffer.indexOf('(', 1);
        int end = this.caseBuffer.indexOf(')', 1);
        String info = this.caseBuffer.substring(start + 1, end);
        int cellId0, cellId1, parentZoneId, childZoneId;
        String[] infos = info.split(" ");
        cellId0 = Integer.parseInt(infos[0], 16);
        cellId1 = Integer.parseInt(infos[1], 16);
        parentZoneId = Integer.parseInt(infos[2], 16);
        childZoneId = Integer.parseInt(infos[3], 16);
        int dstart = this.caseBuffer.indexOf('(', 7);
        int off = dstart + 1;
        int numberOfKids, kid;
        for (int i = cellId0 - 1; i <= cellId1 - 1; i++) {
            this.cells.get(i).setParent(1);
            numberOfKids = this.getCaseBufferInt(off);
            off = off + 4;
            for (int j = 0; j < numberOfKids; j++) {
                kid = this.getCaseBufferInt(off) - 1;
                off = off + 4;
                this.cells.get(kid).setChild(1);
            }
        }
    }

    private void getCellsAscii() {
        int start = this.caseBuffer.indexOf('(', 1);
        int end = this.caseBuffer.indexOf(')', 1);
        String info = this.caseBuffer.substring(start + 1, end);
        int zoneId, firstIndex, lastIndex, type, elementType;
        String[] infos = info.split(" ");
        zoneId = Integer.parseInt(infos[0], 16);
        firstIndex = Integer.parseInt(infos[1], 16);
        lastIndex = Integer.parseInt(infos[2], 16);
        type = Integer.parseInt(infos[3]);

        if (zoneId == 0) { // Cell Info
            this.nCells = lastIndex;
            cells.clear();
            for (int i = 0; i < nCells; i++) {
                cells.add(new FluentCell());
            }
        } else { // Cell Definitions
            elementType = Integer.parseInt(infos[4]);
            if (elementType == FluentCell.ELEMENT_TYPE_MIXED) {
                int dstart = this.caseBuffer.indexOf('(', 5);
                int dend = this.caseBuffer.indexOf(')', dstart + 1);
                String pdata = this.caseBuffer.substring(dstart + 1, dend);
                StringTokenizer st = new StringTokenizer(pdata, " \n", false);
                for (int i = firstIndex - 1; i <= lastIndex - 1; i++) {
                    FluentCell c = this.cells.get(i);
                    c.setType(Integer.parseInt(st.nextToken()));
                    c.setZone(zoneId);
                }
            } else {
                for (int i = firstIndex - 1; i <= lastIndex - 1; i++) {
                    FluentCell c = this.cells.get(i);
                    c.setType(elementType);
                    c.setZone(zoneId);
                }
            }
        }
    }

    private void getCellsBinary() {
        int start = this.caseBuffer.indexOf('(', 1);
        int end = this.caseBuffer.indexOf(')', 1);
        String info = this.caseBuffer.substring(start + 1, end);
        int zoneId, firstIndex, lastIndex, type, elementType;
        String[] infos = info.split(" ");
        zoneId = Integer.parseInt(infos[0], 16);
        firstIndex = Integer.parseInt(infos[1], 16);
        lastIndex = Integer.parseInt(infos[2], 16);
        type = Integer.parseInt(infos[3], 16);
        elementType = Integer.parseInt(infos[4], 16);
        if (elementType == FluentCell.ELEMENT_TYPE_MIXED) {
            int dstart = this.caseBuffer.indexOf('(', 7);
            int off = dstart + 1;
            for (int i = firstIndex - 1; i <= lastIndex - 1; i++) {
                FluentCell c = this.cells.get(i);
                c.setType(this.getCaseBufferInt(off));
                off = off + 4;
                c.setZone(zoneId);
            }
        } else {
            for (int i = firstIndex - 1; i <= lastIndex - 1; i++) {
                FluentCell c = this.cells.get(i);
                c.setType(elementType);
                c.setZone(zoneId);
            }
        }
    }

    private void getFacesAscii() {
        int start = this.caseBuffer.indexOf('(', 1);
        int end = this.caseBuffer.indexOf(')', 1);
        String info = this.caseBuffer.substring(start + 1, end);
        int zoneId, firstIndex, lastIndex, bcType, faceType;
        String[] infos = info.split(" ");
        zoneId = Integer.parseInt(infos[0], 16);
        firstIndex = Integer.parseInt(infos[1], 16);
        lastIndex = Integer.parseInt(infos[2], 16);
        bcType = Integer.parseInt(infos[3], 16);

        if (zoneId == 0) { // Face Info
            this.nFaces = lastIndex;
            faces.clear();
            for (int i = 0; i < nFaces; i++) {
                faces.add(new FluentFace());
            }
        } else { // Face Definitions
            faceType = Integer.parseInt(infos[4], 16);
            int dstart = this.caseBuffer.indexOf('(', 7);
            int dend = this.caseBuffer.indexOf(')', dstart + 1);
            String pdata = this.caseBuffer.substring(dstart + 1, dend);
            StringTokenizer st = new StringTokenizer(pdata, " \n", false);
            int numberOfNodesInFace;
            for (int i = firstIndex - 1; i <= lastIndex - 1; i++) {
                if (faceType == FluentFace.FACE_TYPE_MIXED || faceType == FluentFace.FACE_TYPE_POLYGONAL) {
                    numberOfNodesInFace = Integer.parseInt(st.nextToken());
                } else {
                    numberOfNodesInFace = faceType;
                }
                int[] fnodes = new int[numberOfNodesInFace];
                for (int j = 0; j < numberOfNodesInFace; j++) {
                    fnodes[j] = Integer.parseInt(st.nextToken(), 32) - 1;
                }
                FluentFace f = this.faces.get(i);
                f.setType(faceType);
                f.setNodes(fnodes);
                f.setC0(Integer.parseInt(st.nextToken(), 32) - 1);
                f.setC1(Integer.parseInt(st.nextToken(), 32) - 1);
                f.setZone(zoneId);
                if (f.getC0() >= 0) {
                    this.cells.get(f.getC0()).getFaces().add(i);
                }
                if (f.getC1() >= 0) {
                    this.cells.get(f.getC1()).getFaces().add(i);
                }
            }
        }
    }

    private void getFacesBinary() {
        int start = this.caseBuffer.indexOf('(', 1);
        int end = this.caseBuffer.indexOf(')', 1);
        String info = this.caseBuffer.substring(start + 1, end);
        int zoneId, firstIndex, lastIndex, bcType, faceType;
        String[] infos = info.split(" ");
        zoneId = Integer.parseInt(infos[0], 16);
        firstIndex = Integer.parseInt(infos[1], 16);
        lastIndex = Integer.parseInt(infos[2], 16);
        bcType = Integer.parseInt(infos[3], 16);
        faceType = Integer.parseInt(infos[4], 16);
        int dstart = this.caseBuffer.indexOf('(', 7);
        int numberOfNodesInFace = 0;
        int off = dstart + 1;
        for (int i = firstIndex - 1; i <= lastIndex - 1; i++) {
            if ((faceType == FluentFace.FACE_TYPE_MIXED) || (faceType == FluentFace.FACE_TYPE_POLYGONAL)) {
                numberOfNodesInFace = this.getCaseBufferInt(off);
                off = off + 4;
            } else {
                numberOfNodesInFace = faceType;
            }
            FluentFace f = this.faces.get(i);
            int[] fnodes = new int[numberOfNodesInFace];
            for (int k = 0; k < numberOfNodesInFace; k++) {
                fnodes[k] = this.getCaseBufferInt(off) - 1;
                off = off + 4;
            }
            f.setNodes(fnodes);
            f.setC0(this.getCaseBufferInt(off) - 1);
            off = off + 4;
            f.setC1(this.getCaseBufferInt(off) - 1);
            off = off + 4;
            f.setType(faceType);
            f.setZone(zoneId);
            if (f.getC0() >= 0) {
                this.cells.get(f.getC0()).getFaces().add(i);
            }
            if (f.getC1() >= 0) {
                this.cells.get(f.getC1()).getFaces().add(i);
            }
        }
    }

    private void getPeriodicShadowFacesAscii() {
        int start = this.caseBuffer.indexOf('(', 1);
        int end = this.caseBuffer.indexOf(')', 1);
        String info = this.caseBuffer.substring(start + 1, end);
        int firstIndex, lastIndex, periodicZone, shadowZone;
        String[] infos = info.split(" ");
        firstIndex = Integer.parseInt(infos[0], 16);
        lastIndex = Integer.parseInt(infos[1], 16);
        periodicZone = Integer.parseInt(infos[2], 16);
        shadowZone = Integer.parseInt(infos[3], 16);

        int dstart = this.caseBuffer.indexOf('(', 7);
        int dend = this.caseBuffer.indexOf(')', dstart + 1);
        String pdata = this.caseBuffer.substring(dstart + 1, dend);
        StringTokenizer st = new StringTokenizer(pdata, " \n", false);

        int faceIndex1, faceIndex2;
        for (int i = firstIndex - 1; i <= lastIndex - 1; i++) {
            faceIndex1 = Integer.parseInt(st.nextToken(), 32);
            faceIndex2 = Integer.parseInt(st.nextToken(), 32);
            this.faces.get(faceIndex1).setPeriodicShadow(1);
            ///a po co jest faceindex2 ???
        }
    }

    private void getSpeciesVariableNames() {
        //Locate the "(species (names" entry
        String variables = this.caseBuffer;
        int startPos = variables.indexOf("(species (names (");
        if (startPos != -1) {
            startPos += 17;
            variables = variables.substring(startPos);
            int endPos = variables.indexOf(")");
            variables = variables.substring(0, endPos);
            StringTokenizer st = new StringTokenizer(variables, " \n", false);
            int iterator = 0;
            while (st.hasMoreTokens()) {
                String temp = st.nextToken();

                this.variableNames.put(200 + iterator, temp);
                this.variableNames.put(250 + iterator, "M1_" + temp);
                this.variableNames.put(300 + iterator, "M2_" + temp);
                this.variableNames.put(450 + iterator, "DPMS_" + temp);
                this.variableNames.put(850 + iterator, "DPMS_DS_" + temp);
                this.variableNames.put(1000 + iterator, "MEAN_" + temp);
                this.variableNames.put(1050 + iterator, "RMS_" + temp);
                this.variableNames.put(1250 + iterator, "CREV_" + temp);
                iterator++;
            }
        }
    }

    private void getFaceTreeAscii() {
        int start = this.caseBuffer.indexOf('(', 1);
        int end = this.caseBuffer.indexOf(')', 1);
        String info = this.caseBuffer.substring(start + 1, end);
        int faceId0, faceId1, parentZoneId, childZoneId;
        String[] infos = info.split(" ");
        faceId0 = Integer.parseInt(infos[0], 16);
        faceId1 = Integer.parseInt(infos[1], 16);
        parentZoneId = Integer.parseInt(infos[2], 16);
        childZoneId = Integer.parseInt(infos[3], 16);
        int dstart = this.caseBuffer.indexOf('(', 7);
        int dend = this.caseBuffer.indexOf(')', dstart + 1);
        String pdata = this.caseBuffer.substring(dstart + 1, dend);
        StringTokenizer st = new StringTokenizer(pdata, " \n", false);
        int numberOfKids, kid;
        for (int i = faceId0 - 1; i <= faceId1 - 1; i++) {
            this.faces.get(i).setParent(1);
            numberOfKids = Integer.parseInt(st.nextToken(), 32);
            for (int j = 0; j < numberOfKids; j++) {
                kid = Integer.parseInt(st.nextToken(), 32) - 1;
                this.faces.get(kid).setChild(1);
            }
        }
    }

    private void getFaceTreeBinary() {
        int start = this.caseBuffer.indexOf('(', 1);
        int end = this.caseBuffer.indexOf(')', 1);
        String info = this.caseBuffer.substring(start + 1, end);
        int faceId0, faceId1, parentZoneId, childZoneId;
        String[] infos = info.split(" ");
        faceId0 = Integer.parseInt(infos[0], 16);
        faceId1 = Integer.parseInt(infos[1], 16);
        parentZoneId = Integer.parseInt(infos[2], 16);
        childZoneId = Integer.parseInt(infos[3], 16);
        int dstart = this.caseBuffer.indexOf('(', 7);
        int off = dstart + 1;
        int numberOfKids, kid;
        for (int i = faceId0 - 1; i <= faceId1 - 1; i++) {
            this.faces.get(i).setParent(1);
            numberOfKids = this.getCaseBufferInt(off);
            off = off + 4;
            for (int j = 0; j < numberOfKids; j++) {
                kid = this.getCaseBufferInt(off) - 1;
                off = off + 4;
                this.faces.get(kid).setChild(1);
            }
        }
    }

    private void getInterfaceFaceParentsAscii() {
        int start = this.caseBuffer.indexOf('(', 1);
        int end = this.caseBuffer.indexOf(')', 1);
        String info = this.caseBuffer.substring(start + 1, end);
        int faceId0, faceId1;
        String[] infos = info.split(" ");
        faceId0 = Integer.parseInt(infos[0], 16);
        faceId1 = Integer.parseInt(infos[1], 16);
        int dstart = this.caseBuffer.indexOf('(', 7);
        int dend = this.caseBuffer.indexOf(')', dstart + 1);
        String pdata = this.caseBuffer.substring(dstart + 1, dend);
        StringTokenizer st = new StringTokenizer(pdata, " \n", false);
        int parentId0, parentId1;
        for (int i = faceId0 - 1; i <= faceId1 - 1; i++) {
            parentId0 = Integer.parseInt(st.nextToken(), 32) - 1;
            parentId1 = Integer.parseInt(st.nextToken(), 32) - 1;
            this.faces.get(parentId0).setInterfaceFaceParent(1);
            this.faces.get(parentId1).setInterfaceFaceParent(1);
            this.faces.get(i).setInterfaceFaceChild(1);
        }
    }

    private void getInterfaceFaceParentsBinary() {
        int start = this.caseBuffer.indexOf('(', 1);
        int end = this.caseBuffer.indexOf(')', 1);
        String info = this.caseBuffer.substring(start + 1, end);
        int faceId0, faceId1;
        String[] infos = info.split(" ");
        faceId0 = Integer.parseInt(infos[0], 16);
        faceId1 = Integer.parseInt(infos[1], 16);
        int dstart = this.caseBuffer.indexOf('(', 7);
        int off = dstart + 1;
        int parentId0, parentId1;
        for (int i = faceId0 - 1; i <= faceId1 - 1; i++) {
            parentId0 = this.getCaseBufferInt(off) - 1;
            off = off + 4;
            parentId1 = this.getCaseBufferInt(off) - 1;
            off = off + 4;
            this.faces.get(parentId0).setInterfaceFaceParent(1);
            this.faces.get(parentId1).setInterfaceFaceParent(1);
            this.faces.get(i).setInterfaceFaceChild(1);
        }
    }

    private void getNonconformalGridInterfaceFaceInformationAscii() {
        int start = this.caseBuffer.indexOf('(', 1);
        int end = this.caseBuffer.indexOf(')', 1);
        String info = this.caseBuffer.substring(start + 1, end);
        int kidId, parentId, numberOfFaces;
        String[] infos = info.split(" ");
        kidId = Integer.parseInt(infos[0]);
        parentId = Integer.parseInt(infos[1]);
        numberOfFaces = Integer.parseInt(infos[2]);
        int dstart = this.caseBuffer.indexOf('(', 7);
        int dend = this.caseBuffer.indexOf(')', dstart + 1);
        String pdata = this.caseBuffer.substring(dstart + 1, dend);
        StringTokenizer st = new StringTokenizer(pdata, " \n", false);
        int child, parent;
        for (int i = 0; i < numberOfFaces; i++) {
            child = Integer.parseInt(st.nextToken(), 32) - 1;
            parent = Integer.parseInt(st.nextToken(), 32) - 1;
            this.faces.get(child).setNcgChild(1);
            this.faces.get(parent).setNcgParent(1);
        }
    }

    private void getNonconformalGridInterfaceFaceInformationBinary() {
        int start = this.caseBuffer.indexOf('(', 1);
        int end = this.caseBuffer.indexOf(')', 1);
        String info = this.caseBuffer.substring(start + 1, end);
        int kidId, parentId, numberOfFaces;
        String[] infos = info.split(" ");
        kidId = Integer.parseInt(infos[0]);
        parentId = Integer.parseInt(infos[1]);
        numberOfFaces = Integer.parseInt(infos[2]);
        int dstart = this.caseBuffer.indexOf('(', 7);
        int off = dstart + 1;
        int child, parent;
        for (int i = 0; i < numberOfFaces; i++) {
            child = this.getCaseBufferInt(off) - 1;
            off = off + 4;
            parent = this.getCaseBufferInt(off) - 1;
            off = off + 4;
            this.faces.get(child).setNcgChild(1);
            this.faces.get(parent).setNcgParent(1);
        }
    }

    private void getNodesSinglePrecision() {
        int start = this.caseBuffer.indexOf('(', 1);
        int end = this.caseBuffer.indexOf(')', 1);
        String info = this.caseBuffer.substring(start + 1, end);
        int zoneId, firstIndex, lastIndex, type;
        String[] infos = info.split(" ");
        zoneId = Integer.parseInt(infos[0], 16);
        firstIndex = Integer.parseInt(infos[1], 16);
        lastIndex = Integer.parseInt(infos[2], 16);
        type = Integer.parseInt(infos[3]);
        int dstart = this.caseBuffer.indexOf('(', 7);
        int off = dstart + 1;
        double x, y, z;
        if (this.nSpace == 3) {
            for (int i = firstIndex - 1; i <= lastIndex - 1; i++) {
                x = this.getCaseBufferFloat(off);
                off = off + 4;

                y = this.getCaseBufferFloat(off);
                off = off + 4;

                z = this.getCaseBufferFloat(off);
                off = off + 4;
                this.coords[3 * i] = (float) x;
                this.coords[3 * i + 1] = (float) y;
                this.coords[3 * i + 2] = (float) z;
            }
        } else {
            for (int i = firstIndex - 1; i <= lastIndex - 1; i++) {
                x = this.getCaseBufferFloat(off);
                off = off + 4;

                y = this.getCaseBufferFloat(off);
                off = off + 4;
                //forced 3D field
                z = 0.0;
                this.coords[3 * i] = (float) x;
                this.coords[3 * i + 1] = (float) y;
                this.coords[3 * i + 2] = (float) z;
            }
        }
    }

    private void getNodesDoublePrecision() {
        int start = this.caseBuffer.indexOf('(', 1);
        int end = this.caseBuffer.indexOf(')', 1);
        String info = this.caseBuffer.substring(start + 1, end);
        int zoneId, firstIndex, lastIndex, type;
        String[] infos = info.split(" ");
        zoneId = Integer.parseInt(infos[0], 16);
        firstIndex = Integer.parseInt(infos[1], 16);
        lastIndex = Integer.parseInt(infos[2], 16);
        type = Integer.parseInt(infos[3]);
        int dstart = this.caseBuffer.indexOf('(', 7);
        int off = dstart + 1;
        double x, y, z;
        if (this.nSpace == 3) {
            for (int i = firstIndex - 1; i <= lastIndex - 1; i++) {
                x = this.getCaseBufferDouble(off);
                off = off + 8;

                y = this.getCaseBufferDouble(off);
                off = off + 8;

                z = this.getCaseBufferDouble(off);
                off = off + 8;
                this.coords[3 * i] = (float) x;
                this.coords[3 * i + 1] = (float) y;
                this.coords[3 * i + 2] = (float) z;
            }
        } else {
            for (int i = firstIndex - 1; i <= lastIndex - 1; i++) {
                x = this.getCaseBufferDouble(off);
                off = off + 8;

                y = this.getCaseBufferDouble(off);
                off = off + 8;
                //forced 3D field
                z = 0.0;
                this.coords[3 * i] = (float) x;
                this.coords[3 * i + 1] = (float) y;
                this.coords[3 * i + 2] = (float) z;
            }
        }
    }

    private void getPeriodicShadowFacesBinary() {
        int start = this.caseBuffer.indexOf('(', 1);
        int end = this.caseBuffer.indexOf(')', 1);
        String info = this.caseBuffer.substring(start + 1, end);
        int firstIndex, lastIndex, periodicZone, shadowZone;
        String[] infos = info.split(" ");
        firstIndex = Integer.parseInt(infos[0], 16);
        lastIndex = Integer.parseInt(infos[1], 16);
        periodicZone = Integer.parseInt(infos[2], 16);
        shadowZone = Integer.parseInt(infos[3], 16);
        int dstart = this.caseBuffer.indexOf('(', 7);
        int off = dstart + 1;
        int faceIndex1, faceIndex2;
        for (int i = firstIndex - 1; i <= lastIndex - 1; i++) {
            faceIndex1 = this.getCaseBufferInt(off);
            off = off + 4;
            faceIndex2 = this.getCaseBufferInt(off);
            off = off + 4;
            this.faces.get(faceIndex1).setPeriodicShadow(1);
            ///a po co jest faceindex2 ???
        }
    }

    private void parseCaseFile() throws IOException {
        this.fluentCaseFile.reset();
        this.fluentCaseFile.seek(0);

        while (this.getCaseChunk()) {
            int index = this.getCaseIndex();
            switch (index) {
                case 0:
                    break;
                case 1:
                    break;
                case 2:
                    this.nSpace = this.getNSpace();
                    break;
                case 4:
                    this.getLittleEndianFlag();
                    break;
                case 10:
                    this.getNodesAscii();
                    break;
                case 12:
                    this.getCellsAscii();
                    break;
                case 13:
                    this.getFacesAscii();
                    break;
                case 18:
                    this.getPeriodicShadowFacesAscii();
                    break;
                case 37:
                    this.getSpeciesVariableNames();
                    break;
                case 38:
                    break;
                case 39:
                    break;
                case 40:
                    break;
                case 41:
                    break;
                case 45:
                    break;
                case 58:
                    this.getCellTreeAscii();
                    break;
                case 59:
                    this.getFaceTreeAscii();
                    break;
                case 61:
                    this.getInterfaceFaceParentsAscii();
                    break;
                case 62:
                    this.getNonconformalGridInterfaceFaceInformationAscii();
                    break;
                case 63:
                    break;
                case 64:
                    break;
                case 2010:
                    this.getNodesSinglePrecision();
                    break;
                case 3010:
                    this.getNodesDoublePrecision();
                    break;
                case 2012:
                    this.getCellsBinary();
                    break;
                case 3012:
                    this.getCellsBinary();
                    break;
                case 2013:
                    this.getFacesBinary();
                    break;
                case 3013:
                    this.getFacesBinary();
                    break;
                case 2018:
                    this.getPeriodicShadowFacesBinary();
                    break;
                case 3018:
                    this.getPeriodicShadowFacesBinary();
                    break;
                case 2040:
                    break;
                case 3040:
                    break;
                case 2041:
                    break;
                case 3041:
                    break;
                case 2058:
                    this.getCellTreeBinary();
                    break;
                case 3058:
                    this.getCellTreeBinary();
                    break;
                case 2059:
                    this.getFaceTreeBinary();
                    break;
                case 3059:
                    this.getFaceTreeBinary();
                    break;
                case 2061:
                    this.getInterfaceFaceParentsBinary();
                    break;
                case 3061:
                    this.getInterfaceFaceParentsBinary();
                    break;
                case 2062:
                    this.getNonconformalGridInterfaceFaceInformationBinary();
                    break;
                case 3062:
                    this.getNonconformalGridInterfaceFaceInformationBinary();
                    break;
                case 2063:
                    break;
                case 3063:
                    break;
                default:
                    break;
            }
        }
    }

    private void cleanCells() {
        ArrayList<Integer> t = new ArrayList<Integer>();
        for (int i = 0; i < cells.size(); i++) {
            if (((this.cells.get(i).getType() == FluentCell.ELEMENT_TYPE_TRIANGULAR) && (this.cells.get(i).getFaces().size() != 3))
                    || ((this.cells.get(i).getType() == FluentCell.ELEMENT_TYPE_TETRAHEDRAL) && (this.cells.get(i).getFaces().size() != 4))
                    || ((this.cells.get(i).getType() == FluentCell.ELEMENT_TYPE_QUADRILATERAL) && (this.cells.get(i).getFaces().size() != 4))
                    || ((this.cells.get(i).getType() == FluentCell.ELEMENT_TYPE_HEXAHEDRAL) && (this.cells.get(i).getFaces().size() != 6))
                    || ((this.cells.get(i).getType() == FluentCell.ELEMENT_TYPE_PYRAMID) && (this.cells.get(i).getFaces().size() != 5))
                    || ((this.cells.get(i).getType() == FluentCell.ELEMENT_TYPE_WEDGE) && (this.cells.get(i).getFaces().size() != 5))) {
                // Copy faces
                t.clear();
                for (int j = 0; j < (int) this.cells.get(i).getFaces().size(); j++) {
                    t.add(this.faces.indexOf(this.cells.get(i).getFaces().get(j)));
                }

                // Clear Faces
                this.cells.get(i).getFaces().clear();

                // Copy the faces that are not flagged back into the cell
                for (int j = 0; j < (int) t.size(); j++) {
                    if ((this.faces.get(t.get(j)).getChild() == 0)
                            && (this.faces.get(t.get(j)).getNcgChild() == 0)
                            && (this.faces.get(t.get(j)).getInterfaceFaceChild() == 0)) {
                        this.cells.get(i).getFaces().add(t.get(j));
                    }
                }
            }
        }
    }

    private void populateTriangleCell(int i) {
        FluentCell c = this.cells.get(i);
        int[] cnodes = new int[3];
        if (this.cells.get(i).getFace(0, faces).getC0() == i) {
            cnodes[0] = c.getFace(0, faces).getNode(0);
            cnodes[1] = c.getFace(0, faces).getNode(1);
        } else {
            cnodes[1] = c.getFace(0, faces).getNode(0);
            cnodes[0] = c.getFace(0, faces).getNode(1);
        }

        if (c.getFace(1, faces).getNode(0) != c.getNode(0) && c.getFace(1, faces).getNode(0) != c.getNode(1)) {
            cnodes[2] = c.getFace(1, faces).getNode(0);
        } else {
            cnodes[2] = c.getFace(1, faces).getNode(1);
        }
        c.setNodes(cnodes);
    }

    private void populateTetraCell(int i) {
        FluentCell c = this.cells.get(i);
        int[] cnodes = new int[4];

        if (c.getFace(0, faces).getC0() == i) {
            cnodes[0] = c.getFace(0, faces).getNode(0);
            cnodes[1] = c.getFace(0, faces).getNode(1);
            cnodes[2] = c.getFace(0, faces).getNode(2);
        } else {
            cnodes[2] = c.getFace(0, faces).getNode(0);
            cnodes[1] = c.getFace(0, faces).getNode(1);
            cnodes[0] = c.getFace(0, faces).getNode(2);
        }

        if (c.getFace(1, faces).getNode(0) != cnodes[0]
                && c.getFace(1, faces).getNode(0) != cnodes[1]
                && c.getFace(1, faces).getNode(0) != cnodes[2]) {
            cnodes[3] = c.getFace(1, faces).getNode(0);
        } else if (c.getFace(1, faces).getNode(1) != cnodes[0]
                && c.getFace(1, faces).getNode(1) != cnodes[1]
                && c.getFace(1, faces).getNode(1) != cnodes[2]) {
            cnodes[3] = c.getFace(1, faces).getNode(1);
        } else {
            cnodes[3] = c.getFace(1, faces).getNode(2);
        }
        c.setNodes(cnodes);
    }

    private void populateQuadCell(int i) {
        FluentCell c = this.cells.get(i);
        int[] cnodes = new int[4];

        if (c.getFace(0, faces).getC0() == i) {
            cnodes[0] = c.getFace(0, faces).getNode(0);
            cnodes[1] = c.getFace(0, faces).getNode(1);
        } else {
            cnodes[1] = c.getFace(0, faces).getNode(0);
            cnodes[0] = c.getFace(0, faces).getNode(1);
        }

        if ((c.getFace(1, faces).getNode(0) != cnodes[0]
                && c.getFace(1, faces).getNode(0) != cnodes[1])
                && (c.getFace(1, faces).getNode(1) != cnodes[0]
                && c.getFace(1, faces).getNode(1) != cnodes[1])) {
            if (c.getFace(1, faces).getC0() == i) {
                cnodes[2] = c.getFace(1, faces).getNode(0);
                cnodes[3] = c.getFace(1, faces).getNode(1);
            } else {
                cnodes[3] = c.getFace(1, faces).getNode(0);
                cnodes[2] = c.getFace(1, faces).getNode(1);
            }
        } else if ((c.getFace(2, faces).getNode(0) != cnodes[0]
                && c.getFace(2, faces).getNode(0) != cnodes[1])
                && (c.getFace(2, faces).getNode(1) != cnodes[0]
                && c.getFace(2, faces).getNode(1) != cnodes[1])) {
            if (c.getFace(2, faces).getC0() == i) {
                cnodes[2] = c.getFace(2, faces).getNode(0);
                cnodes[3] = c.getFace(2, faces).getNode(1);
            } else {
                cnodes[3] = c.getFace(2, faces).getNode(0);
                cnodes[2] = c.getFace(2, faces).getNode(1);
            }
        } else {
            if (c.getFace(3, faces).getC0() == i) {
                cnodes[2] = c.getFace(3, faces).getNode(0);
                cnodes[3] = c.getFace(3, faces).getNode(1);
            } else {
                cnodes[3] = c.getFace(3, faces).getNode(0);
                cnodes[2] = c.getFace(3, faces).getNode(1);
            }
        }
        c.setNodes(cnodes);
    }

    private void populateHexahedronCell(int i) {
        FluentCell c = this.cells.get(i);
        int[] cnodes = new int[8];

        if (c.getFace(0, faces).getC0() == i) {
            for (int j = 0; j < 4; j++) {
                cnodes[j] = c.getFace(0, faces).getNode(j);
            }
        } else {
            for (int j = 3; j >= 0; j--) {
                cnodes[3 - j] = c.getFace(0, faces).getNode(j);
            }
        }

        //  Look for opposite face of hexahedron
        for (int j = 1; j < 6; j++) {
            int flag = 0;
            for (int k = 0; k < 4; k++) {
                if ((cnodes[0] == c.getFace(j, faces).getNode(k))
                        || (cnodes[1] == c.getFace(j, faces).getNode(k))
                        || (cnodes[2] == c.getFace(j, faces).getNode(k))
                        || (cnodes[3] == c.getFace(j, faces).getNode(k))) {
                    flag = 1;
                }
            }
            if (flag == 0) {
                if (c.getFace(j, faces).getC1() == i) {
                    for (int k = 4; k < 8; k++) {
                        cnodes[k] = c.getFace(j, faces).getNode(k - 4);
                    }
                } else {
                    for (int k = 7; k >= 4; k--) {
                        cnodes[k] = c.getFace(j, faces).getNode(7 - k);
                    }
                }
            }
        }

        //  Find the face with points 0 and 1 in them.
        int[] f01 = new int[]{-1, -1, -1, -1};
        for (int j = 1; j < 6; j++) {
            int flag0 = 0;
            int flag1 = 0;
            for (int k = 0; k < 4; k++) {
                if (cnodes[0] == c.getFace(j, faces).getNode(k)) {
                    flag0 = 1;
                }
                if (cnodes[1] == c.getFace(j, faces).getNode(k)) {
                    flag1 = 1;
                }
            }
            if ((flag0 == 1) && (flag1 == 1)) {
                if (c.getFace(j, faces).getC0() == i) {
                    for (int k = 0; k < 4; k++) {
                        f01[k] = c.getFace(j, faces).getNode(k);
                    }
                } else {
                    for (int k = 3; k >= 0; k--) {
                        f01[k] = c.getFace(j, faces).getNode(k);
                    }
                }
            }
        }

        //  Find the face with points 0 and 3 in them.
        int[] f03 = new int[]{-1, -1, -1, -1};
        for (int j = 1; j < 6; j++) {
            int flag0 = 0;
            int flag1 = 0;
            for (int k = 0; k < 4; k++) {
                if (cnodes[0] == c.getFace(j, faces).getNode(k)) {
                    flag0 = 1;
                }
                if (cnodes[3] == c.getFace(j, faces).getNode(k)) {
                    flag1 = 1;
                }
            }

            if ((flag0 == 1) && (flag1 == 1)) {
                if (c.getFace(j, faces).getC0() == i) {
                    for (int k = 0; k < 4; k++) {
                        f03[k] = c.getFace(j, faces).getNode(k);
                    }
                } else {
                    for (int k = 3; k >= 0; k--) {
                        f03[k] = c.getFace(j, faces).getNode(k);
                    }
                }
            }
        }

        // What point is in f01 and f03 besides 0 ... this is point 4
        int p4 = 0;
        for (int k = 0; k < 4; k++) {
            if (f01[k] != cnodes[0]) {
                for (int n = 0; n < 4; n++) {
                    if (f01[k] == f03[n]) {
                        p4 = f01[k];
                    }
                }
            }
        }

        // Since we know point 4 now we check to see if points
        //  4, 5, 6, and 7 are in the correct positions.
        int[] t = new int[8];
        t[4] = cnodes[4];
        t[5] = cnodes[5];
        t[6] = cnodes[6];
        t[7] = cnodes[7];
        if (p4 == cnodes[5]) {
            cnodes[5] = t[6];
            cnodes[6] = t[7];
            cnodes[7] = t[4];
            cnodes[4] = t[5];
        } else if (p4 == c.getNode(6)) {
            cnodes[5] = t[7];
            cnodes[6] = t[4];
            cnodes[7] = t[5];
            cnodes[4] = t[6];
        } else if (p4 == c.getNode(7)) {
            cnodes[5] = t[4];
            cnodes[6] = t[5];
            cnodes[7] = t[6];
            cnodes[4] = t[7];
        }
        // else point 4 was lined up so everything was correct.
        c.setNodes(cnodes);
    }

    private void populatePyramidCell(int i) {
        FluentCell c = this.cells.get(i);
        int[] cnodes = new int[5];
        //  The quad face will be the base of the pyramid
        for (int j = 0; j < c.getFaces().size(); j++) {
            if (c.getFace(j, faces).getNodes().length == 4) {
                if (c.getFace(j, faces).getC0() == i) {
                    for (int k = 0; k < 4; k++) {
                        cnodes[k] = c.getFace(j, faces).getNode(k);
                    }
                } else {
                    for (int k = 0; k < 4; k++) {
                        cnodes[3 - k] = c.getFace(j, faces).getNode(k);
                    }
                }
            }
        }

        // Just need to find point 4
        for (int j = 0; j < c.getFaces().size(); j++) {
            if (c.getFace(j, faces).getNodes().length == 3) {
                for (int k = 0; k < 3; k++) {
                    if ((c.getFace(j, faces).getNode(k) != cnodes[0])
                            && (c.getFace(j, faces).getNode(k) != cnodes[1])
                            && (c.getFace(j, faces).getNode(k) != cnodes[2])
                            && (c.getFace(j, faces).getNode(k) != cnodes[3])) {
                        cnodes[4] = c.getFace(j, faces).getNode(k);
                    }
                }
            }
        }
        c.setNodes(cnodes);
    }

//----------------------------------------------------------------------------
    private void populateWedgeCell(int i) {
        FluentCell c = this.cells.get(i);
        int[] cnodes = new int[6];

        //  Find the first triangle face and make it the base.
        FluentFace base = faces.get(0);
        int first = 0;
        for (int j = 0; j < c.getFaces().size(); j++) {
            if ((c.getFace(j, faces).getType() == FluentFace.FACE_TYPE_TRIANGULAR) && (first == 0)) {
                base = c.getFace(j, faces);
                first = 1;
            }
        }

        //  Find the second triangle face and make it the top.
        FluentFace top = faces.get(0);
        int second = 0;
        for (int j = 0; j < c.getFaces().size(); j++) {
            if ((c.getFace(j, faces).getType() == FluentFace.FACE_TYPE_TRIANGULAR) && (second == 0) && (c.getFace(j, faces) != base)) {
                top = c.getFace(j, faces);
                second = 1;
            }
        }

        // Load Base nodes into the nodes std::vector
        if (base.getC0() == i) {
            for (int j = 0; j < 3; j++) {
                cnodes[j] = base.getNode(j);
            }
        } else {
            for (int j = 2; j >= 0; j--) {
                cnodes[2 - j] = base.getNode(j);
            }
        }
        // Load Top nodes into the nodes std::vector
        if (top.getC1() == i) {
            for (int j = 3; j < 6; j++) {
                cnodes[j] = top.getNode(j - 3);
            }
        } else {
            for (int j = 3; j < 6; j++) {
                cnodes[j] = top.getNode(5 - j);
            }
        }
        //  Find the quad face with points 0 and 1 in them.
        int[] w01 = new int[4];
        for (int j = 0; j < c.getFaces().size(); j++) {
            if (c.getFace(j, faces) != base && c.getFace(j, faces) != top) {
                int wf0 = 0;
                int wf1 = 0;
                for (int k = 0; k < 4; k++) {
                    if (cnodes[0] == c.getFace(j, faces).getNode(k)) {
                        wf0 = 1;
                    }
                    if (cnodes[1] == c.getFace(j, faces).getNode(k)) {
                        wf1 = 1;
                    }
                    if ((wf0 == 1) && (wf1 == 1)) {
                        for (int n = 0; n < 4; n++) {
                            w01[n] = c.getFace(j, faces).getNode(n);
                        }
                    }
                }
            }
        }
        //  Find the quad face with points 0 and 2 in them.
        int[] w02 = new int[]{-1, -1, -1, -1};
        for (int j = 0; j < c.getFaces().size(); j++) {
            if (c.getFace(j, faces) != base && c.getFace(j, faces) != top) {
                int wf0 = 0;
                int wf2 = 0;
                for (int k = 0; k < 4; k++) {
                    if (cnodes[0] == c.getFace(j, faces).getNode(k)) {
                        wf0 = 1;
                    }
                    if (cnodes[2] == c.getFace(j, faces).getNode(k)) {
                        wf2 = 1;
                    }
                    if ((wf0 == 1) && (wf2 == 1)) {
                        for (int n = 0; n < 4; n++) {
                            w02[n] = c.getFace(j, faces).getNode(n);
                        }
                    }
                }
            }
        }
        // Point 3 is the point that is in both w01 and w02
        // What point is in f01 and f02 besides 0 ... this is point 3
        int p3 = 0;
        for (int k = 0; k < 4; k++) {
            if (w01[k] != cnodes[0]) {
                for (int n = 0; n < 4; n++) {
                    if (w01[k] == w02[n]) {
                        p3 = w01[k];
                    }
                }
            }
        }
        // Since we know point 3 now we check to see if points
        //  3, 4, and 5 are in the correct positions.
        int[] t = new int[6];
        t[3] = cnodes[3];
        t[4] = cnodes[4];
        t[5] = cnodes[5];
        if (p3 == cnodes[4]) {
            cnodes[3] = t[4];
            cnodes[4] = t[5];
            cnodes[5] = t[3];
        } else if (p3 == cnodes[5]) {
            cnodes[3] = t[5];
            cnodes[4] = t[3];
            cnodes[5] = t[4];
        }
        // else point 3 was lined up so everything was correct.

        c.setNodes(cnodes);
    }

//    private void populatePolyhedronCell(int i) {
//        FluentCell c = this.cells.get(i);
//        ArrayList<Integer> cnodes = new ArrayList<>();
//        for (int j = 0; j < c.getFaces().size(); j++) {
//            int k;
//            for (k = 0; k < (int) c.getFace(j, faces).getNodes().length; k++) {
//                int flag;
//                flag = 0;
//                // Is the node already in the cell?
//                for (int n = 0; n < cnodes.size(); n++) {
//                    if (cnodes.get(n) == c.getFace(j, faces).getNode(k)) {
//                        flag = 1;
//                    }
//                }
//                if (flag == 0) {
//                    //No match - insert node into cell.
//                    cnodes.add(c.getFace(j, faces).getNode(k));
//                }
//            }
//        }
//
//        int[] cnodesArr = new int[cnodes.size()];
//        for (int j = 0; j < cnodesArr.length; j++) {
//            cnodesArr[j] = cnodes.get(j);
//        }
//        c.setNodes(cnodesArr);
//    }

    private void populateCellNodes() {
        for (int i = 0; i < this.cells.size(); i++) {
            switch (this.cells.get(i).getType()) {
                case FluentCell.ELEMENT_TYPE_TRIANGULAR:
                    this.populateTriangleCell(i);
                    break;
                case FluentCell.ELEMENT_TYPE_TETRAHEDRAL:
                    this.populateTetraCell(i);
                    break;
                case FluentCell.ELEMENT_TYPE_QUADRILATERAL:
                    this.populateQuadCell(i);
                    break;
                case FluentCell.ELEMENT_TYPE_HEXAHEDRAL:
                    this.populateHexahedronCell(i);
                    break;
                case FluentCell.ELEMENT_TYPE_PYRAMID:
                    this.populatePyramidCell(i);
                    break;
                case FluentCell.ELEMENT_TYPE_WEDGE:
                    this.populateWedgeCell(i);
                    break;
                case FluentCell.ELEMENT_TYPE_POLYHEDRAL:
                    //this.populatePolyhedronCell(i);
                    System.err.println("POLYHEDRAL cells not supported!");
                    break;
            }
        }
    }

    private void getNumberOfCellZones() {
        int match;
        for (int i = 0; i < this.cells.size(); i++) {
            if (this.cellZones.isEmpty()) {
                this.cellZones.add(this.cells.get(i).getZone());
            } else {
                match = 0;
                for (int j = 0; j < this.cellZones.size(); j++) {
                    if (this.cellZones.get(j) == this.cells.get(i).getZone()) {
                        match = 1;
                    }
                }
                if (match == 0) {
                    this.cellZones.add(this.cells.get(i).getZone());
                }
            }
        }
    }

    private void getData(int dataType) {
        int start = this.dataBuffer.indexOf('(', 1);
        int end = this.dataBuffer.indexOf(')', 1);
        String info = this.dataBuffer.substring(start + 1, end);
        int subSectionId, zoneId, size, nTimeLevels, nPhases, firstId, lastId;
        String[] infos = info.split(" ");
        subSectionId = Integer.parseInt(infos[0]);
        zoneId = Integer.parseInt(infos[1]);
        size = Integer.parseInt(infos[2]);
        nTimeLevels = Integer.parseInt(infos[3]);
        nPhases = Integer.parseInt(infos[4]);
        firstId = Integer.parseInt(infos[5]);
        lastId = Integer.parseInt(infos[6]);

        boolean zmatch = false;
        for (int i = 0; i < this.cellZones.size(); i++) {
            if (this.cellZones.get(i) == zoneId) {
                zmatch = true;
                break;
            }
        }

        if (zmatch) {
            int dstart = this.dataBuffer.indexOf('(', 7);
            int dend = this.dataBuffer.indexOf(')', dstart + 1);
            String pdata = this.dataBuffer.substring(dstart + 1, dend);
            StringTokenizer st = null;
            if(dataType == 1) {
                st = new StringTokenizer(pdata, " \n", false);
            }
            int off = dstart + 1;

            boolean match = false;
            for (int i = 0; i < this.subSectionIds.size(); i++) {
                if (subSectionId == this.subSectionIds.get(i)) {
                    match = true;
                    break;
                }
            }

            if (!match && (size < 4)) { 
                this.subSectionIds.add(subSectionId);
                this.subSectionSize.add(size);
                this.subSectionZones.add(new ArrayList<Integer>());
                this.subSectionZones.get(this.subSectionZones.size() - 1).add(zoneId);
            }

            if (size == 1) {
                this.nScalarData++;
                ScalarDataChunk sdc = new ScalarDataChunk();
                sdc.setSubsectionId(subSectionId);
                sdc.setZoneId(zoneId);
                double[] data = new double[lastId - firstId + 1];
                for (int i = 0; i < data.length; i++) {
                    if (dataType == 1) { //ASCII
                        data[i] = Double.parseDouble(st.nextToken());
                    } else if (dataType == 2) { //binary float
                        data[i] = this.getDataBufferFloat(off);
                        off = off + 4;
                    } else { //binary double
                        data[i] = this.getDataBufferDouble(off);
                        off = off + 8;
                    }
                }
                sdc.setScalarData(data);
                this.scalarDataChunks.add(sdc);
            } else {
                this.nVectorData++;
                VectorDataChunk vdc = new VectorDataChunk();
                vdc.setSubsectionId(subSectionId);
                vdc.setZoneId(zoneId);
                int N = (lastId - firstId + 1);
                double[] data = new double[size * N];
                for (int i = 0; i < N; i++) {
                    if (dataType == 1) { //ASCII
                        for (int v = 0; v < size; v++) {
                            data[size * i + v] = Double.parseDouble(st.nextToken());                            
                        }
                    } else if (dataType == 2) { //binary float
                        for (int v = 0; v < size; v++) {
                            data[size * i + v] = this.getDataBufferFloat(off);
                            off = off + 4;
                        }
                    } else { //binary double
                        for (int v = 0; v < size; v++) {
                            data[size * i + v] = this.getDataBufferDouble(off);
                            off = off + 8;
                        }
                    }
                }
                vdc.setVectorData(data);
                vdc.setVeclen(size);
                vectorDataChunks.add(vdc);
            }
        }
    }

    private void parseDataFile() throws IOException {
        this.fluentDataFile.reset();
        this.fluentDataFile.seek(0);
        while (this.getDataChunk()) {
            int index = this.getDataIndex();
            switch (index) {
                case 0:
                    break;
                case 4:
                    break;
                case 33:
                    break;
                case 37:
                    break;
                case 300:
                    getData(1);
                    break;
                case 301:
                    break;
                case 302:
                    break;
                case 2300:
                    getData(2);
                    break;
                case 2301:
                    break;
                case 2302:
                    break;
                case 3300:
                    getData(3);
                    break;
                case 3301:
                    break;
                case 3302:
                    break;
                default:
                    break;
            }
        }
    }

    public boolean requestInformation() {
        if (this.fileName == null) {
            System.err.println("FileName has to be specified!");
            return false;
        }

        boolean casFileOpened = this.openCaseFile(fileName);
        if (!casFileOpened) {
            System.err.println("Unable to open cas file.");
            return false;
        }

        boolean datFileOpened = this.openDataFile(this.fileName);
        if (!datFileOpened) {
            System.out.println("Unable to open dat file.");
        }

        this.variableNames = VariableNames.loadVariableNames();
        try {
            this.parseCaseFile();
            this.cleanCells(); 
            this.populateCellNodes();
            this.getNumberOfCellZones();
            this.nScalarData = 0;
            this.nVectorData = 0;
            if (datFileOpened) {
                this.parseDataFile();
                this.closeDataFile();
            }
            if (casFileOpened) {
                this.closeCaseFile();
            }
        } catch(IOException ex) {
            System.err.println("Error reading fluent files.");
            return false;
        }
        this.nData = this.nScalarData + this.nVectorData;

        int ssId, ssVeclen;
        for (int i = 0; i < this.subSectionIds.size(); i++) {
            ssId = this.subSectionIds.get(i);
            ssVeclen = this.subSectionSize.get(i);
            if (ssVeclen == 1) {
                this.cellDataArraySelection.add(this.variableNames.get(ssId));
                //this.scalarVariableNames.add(this.variableNames.get(ssId));
                this.scalarSubSectionIds.add(ssId);
            } else {
                this.cellDataArraySelection.add(this.variableNames.get(ssId));
                //this.vectorVariableNames.add(this.variableNames.get(ssId));
                this.vectorSubSectionIds.add(ssId);
            }
        }
        this.nCells = this.cells.size();
        return true;
    }

    public IrregularField requestData() {
        if (this.fileName == null) {
            System.err.println("FileName has to be specified!");
            return null;
        }

        IrregularField outField = new IrregularField(nNodes);
        outField.setNSpace(3);
        outField.setCoords(coords);
//        int[] ind = new int[nNodes];
//        for (int i = 0; i < ind.length; i++) {
//            ind[i] = i;
//        }
//        outField.addData(DataArray.create(ind, 1, "index"));


        CellSet[] cellSets = new CellSet[this.cellZones.size()];
        int zone = 0;
        for (int i = 0; i < this.cellZones.size(); i++) {
            zone = this.cellZones.get(i);
            cellSets[i] = new CellSet("zone_" + zone);
            int[][] csnodeIndices = new int[Cell.TYPES][];
            boolean[][] csorientations = new boolean[Cell.TYPES][];
            int[][] csdataIndices = new int[Cell.TYPES][];
            CellArray[] cellArrays = new CellArray[Cell.TYPES];
            int[] ncellsoftype = new int[Cell.TYPES];
            for (int j = 0; j < ncellsoftype.length; j++) {
                ncellsoftype[j] = 0;                
            }            
            FluentCell c;
            boolean error = false;
            
            //count cell types
            for (int j = 0; j < cells.size(); j++) {
                c = cells.get(j);
                if(c.getZone() == zone) {
                    if(c.getType() == FluentCell.ELEMENT_TYPE_POLYHEDRAL || c.getType() == FluentCell.ELEMENT_TYPE_MIXED) {
                        //TODO ??
                        error = true;
                        System.err.println("Cell zone contains not supported cells - skipping");
                        break;
                    }
                    ncellsoftype[FluentCell.mapTypeToVisNow.get(c.getType())]++;
                }
            }
            if(error) {
                cellSets[i] = null;
                continue;
            }
            
            int nCellsInSet = 0;
            for (int j = 0; j < ncellsoftype.length; j++) {
                nCellsInSet += ncellsoftype[j];                
            }
            cellSets[i].setNCells(nCellsInSet);

            //allocate arrays
            for (int j = 0; j < Cell.TYPES; j++) {
                if (ncellsoftype[j] > 0) {
                    csnodeIndices[j] = new int[Cell.nv[j] * ncellsoftype[j]];
                    csorientations[j] = new boolean[ncellsoftype[j]];
                    csdataIndices[j] = new int[ncellsoftype[j]];
                }
            }

            //fill arrays
            int cellType;
            int[] cellNodes;
            int[] count = new int[Cell.TYPES];
            int[] ccount = new int[Cell.TYPES];
            for (int j = 0; j < Cell.TYPES; j++) {
                count[j] = 0;
                ccount[j] = 0;                
            }            
            for (int j = 0; j < cells.size(); j++) {
                c = cells.get(j);
                if(c.getZone() == zone) {
                    cellType = FluentCell.mapTypeToVisNow.get(c.getType());
                    if(ncellsoftype[cellType] > 0) {
                        cellNodes = c.getNodes();
                        for (int k = 0; k < cellNodes.length; k++, ccount[cellType]++) {
                            csnodeIndices[cellType][ccount[cellType]] = cellNodes[k];
                            csorientations[cellType][count[cellType]] = true;
                            csdataIndices[cellType][count[cellType]] = count[cellType];
                        }
                        count[cellType]++;
                    }
                }
            }

            //create cellarrays
            for (int j = 0; j < Cell.TYPES; j++) {
                if (ncellsoftype[j] > 0) {
                    //cellArrays[j] = new CellArray(j, csnodeIndices[j], null, csdataIndices[j]);
                    cellArrays[j] = new CellArray(j, csnodeIndices[j], csorientations[j], csdataIndices[j]);
                    //cellArrays[j] = new CellArray(j, csnodeIndices[j], csorientations[j], null);
                    cellSets[i].addCells(cellArrays[j]);
                }
            }
            
            //add cell data
            int ssId;
            for (int j = 0; j < scalarDataChunks.size(); j++) {                
                if(scalarDataChunks.get(j).getZoneId() == zone) {
                    ssId = scalarDataChunks.get(j).getSubsectionId();
                    cellSets[i].addData(DataArray.create(scalarDataChunks.get(j).getScalarData(), 1, this.variableNames.get(ssId)));
                }
            }
            for (int j = 0; j < vectorDataChunks.size(); j++) {
                if(vectorDataChunks.get(j).getZoneId() == zone) {
                    ssId = vectorDataChunks.get(j).getSubsectionId();
                    cellSets[i].addData(DataArray.create(vectorDataChunks.get(j).getVectorData(), vectorDataChunks.get(j).getVeclen(), this.variableNames.get(ssId)));
                }
            }
            
            
            
        }

        for (int i = 0; i < cellSets.length; i++) {
            if(cellSets[i] != null) {
                //cellSets[i].generateExternFaces();
                cellSets[i].generateDisplayData(coords);
                outField.addCellSet(cellSets[i]);
            }            
        }
        return outField;
    }
}
