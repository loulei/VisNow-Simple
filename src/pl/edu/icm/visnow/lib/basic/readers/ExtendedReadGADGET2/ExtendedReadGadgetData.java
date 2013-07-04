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

package pl.edu.icm.visnow.lib.basic.readers.ExtendedReadGADGET2;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.stream.FileImageInputStream;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pl.edu.icm.visnow.datasets.CellArray;
import pl.edu.icm.visnow.datasets.CellSet;
import pl.edu.icm.visnow.datasets.IrregularField;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.datasets.cells.Cell;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;

/**
 * @author Bartosz Borucki (babor@icm.edu.pl) University of Warsaw,
 * Interdisciplinary Centre for Mathematical and Computational Modelling
 */
public class ExtendedReadGadgetData {
    private static final int BUFFER_SIZE = 1024;
    private static final float LOG_CONSTANT = 1.0f;
    
    private float progress = 0.0f;
    private IrregularField field = null;
    private RegularField densityField = null;

    public ExtendedReadGadgetData() {
    }

    public float getProgress() {
        return progress;
    }
    /**
     * Utility field holding list of ChangeListeners.
     */
    protected transient ArrayList<ChangeListener> changeListenerList = new ArrayList<ChangeListener>();

    /**
     * Registers ChangeListener to receive events.
     *
     * @param listener The listener to register.
     */
    public synchronized void addChangeListener(ChangeListener listener) {
        changeListenerList.add(listener);
    }

    /**
     * Removes ChangeListener from the list of listeners.
     *
     * @param listener The listener to remove.
     */
    public synchronized void removeChangeListener(ChangeListener listener) {
        changeListenerList.remove(listener);
    }

    /**
     * Notifies all registered listeners about the event.
     *
     * @param object Parameter #1 of the
     * <CODE>ChangeEvent<CODE> constructor.
     */
    public void fireStateChanged() {
        ChangeEvent e = new ChangeEvent(this);
        for (int i = 0; i < changeListenerList.size(); i++) {
            changeListenerList.get(i).stateChanged(e);
        }
    }

    /**
     * @return the field
     */
    public IrregularField getField() {
        return field;
    }

    /**
     * @return the densityField
     */
    public RegularField getDensityField() {
        return densityField;
    }

    /**
     * Reads Gadget-2 data.
     *
     * @param filePaths - array with absolute paths to files to read. Each file
     * denotes single timestep.
     * @param readMask - 7 element boolean array with flags for reading
     * Velocity, ID, Type, Mass, Energy, Density and Temperature respectively
     * @param downsize - positive integer for forced downsize, 1 for no downsize
     * or -1 for auto downsize
     * @param densityFieldDims - 3 element int array for density field
     * dimensions
     * @param cropExtents - 2-by-3 array with x,y and z low and up spatial
     * limits for data read cropping
     * @param availableMemory - memory size in bytes available for usage (or -1
     * to ignore)
     * @return - true if read was sucessful or false if read was unsuccesful
     */
    public boolean read(String[] filePaths, boolean[] readMask, int downsize, int[] densityFieldDims, boolean logDensityField, float[][] cropExtents, long availableMemory) {
        field = null;
        densityField = null;

        if (filePaths == null || readMask == null || readMask.length != 7 || densityFieldDims == null || densityFieldDims.length != 3) {
            return false;
        }

        long mem = availableMemory;
        if (availableMemory == -1) {
            RuntimeMXBean RuntimemxBean = ManagementFactory.getRuntimeMXBean();
            List<String> aList = RuntimemxBean.getInputArguments();
            long memoryMax = Long.MAX_VALUE;
            for (int i = 0; i < aList.size(); i++) {
                String str = aList.get(i);
                if (str.startsWith("-Xmx")) {
                    String amount = str.substring(4, str.length() - 1);
                    String unit = str.substring(str.length() - 1);
                    try {
                        memoryMax = Long.parseLong(amount);
                        if (unit.equalsIgnoreCase("k")) {
                            memoryMax *= 1024L;
                        } else if (unit.equalsIgnoreCase("m")) {
                            memoryMax *= 1024L * 1024L;
                        } else if (unit.equalsIgnoreCase("g")) {
                            memoryMax *= 1024L * 1024L * 1024L;
                        }
                    } catch (NumberFormatException ex) {
                    }
                }
            }
            Runtime r = Runtime.getRuntime();
            long total = r.totalMemory();
            long free = r.freeMemory();
            long used = total - free;
            mem = (memoryMax - used);
        }


        if (cropExtents == null) {
            return readFull(filePaths, readMask, downsize, densityFieldDims, logDensityField, mem);
        } else {
            //read scheme with cropping
            if (cropExtents.length != 2 || cropExtents[0].length != 3) {
                return false;
            }
            return readCropped(filePaths, readMask, downsize, densityFieldDims, logDensityField, cropExtents, mem);
        }
    }

    private boolean readFull(String[] filePaths, boolean[] readMask, int downsize, int[] densityFieldDims, boolean logDensityField, long availableMemory) {
        for (int i = 0; i < filePaths.length; i++) {
            File f = new File(filePaths[i]);
            if (!f.exists() || !f.canRead()) {
                System.err.println("ERROR: cannot read file " + filePaths[i]);
                return false;
            }
        }

        progress = 0.0f;
        fireStateChanged();


        int nFrames = filePaths.length;


        GadgetFileHeader[] headers = new GadgetFileHeader[nFrames];
        int[] nParts = new int[nFrames];
        int[] tmp;
        int nPartsMax = 0;
        int nPartsMaxGas = 0;
        int iPartsMax = 0;

        for (int i = 0; i < nFrames; i++) {
            headers[i] = GadgetFileHeader.read(filePaths[i]);
            if (headers[i] == null) {
                System.err.println("ERROR: error reading file " + filePaths[i]);
                return false;
            }
            int[] nallhw = headers[i].getNallhw();
            for (int j = 0; j < nallhw.length; j++) {
                if(nallhw[j] != 0) {
                    System.err.println("ERROR: in file " + filePaths[i]+". Files with particle number >2^32 not supported!");
                    return false;
                }
            }
            
            nParts[i] = 0;
            tmp = headers[i].getNpart();
            for (int j = 0; j < tmp.length; j++) {
                nParts[i] += tmp[j];
            }
            if (nParts[i] > nPartsMax) {
                nPartsMax = nParts[i];
                nPartsMaxGas = tmp[0];
                iPartsMax = i;
            }
        }
        //TODO to nie uwzględnia odpowiednio umierania i rodzenia się cząsteczek, trzeba monitorować ID

        System.out.println("Maximum of " + nPartsMax + " particles found");


        int autoDownsize = 1;
        long mem = 0;
        if (downsize > 0) {
            System.out.println("estimating full data memory needs...");
            mem = estimateMemory(nFrames, nPartsMax, nPartsMaxGas, readMask, downsize, densityFieldDims);
            System.out.println("estimated size: " + (mem / (1024L * 1024L)) + " MB");
            System.out.println("available size: " + (availableMemory / (1024L * 1024L)) + " MB");
            if (mem > availableMemory) {
                System.err.println("Not enough memory to read Gadget-2 data with provided settings!");
                return false;
            }
            autoDownsize = downsize;
        } else if (downsize == -1) {

            autoDownsize = 1;
            mem = estimateMemory(nFrames, nPartsMax, nPartsMaxGas, readMask, 1, densityFieldDims);
            while (mem > availableMemory) {
                autoDownsize++;
                mem = estimateMemory(nFrames, nPartsMax, nPartsMaxGas, readMask, autoDownsize, densityFieldDims);
            }
            System.out.println("estimated downsize: " + autoDownsize);
            System.out.println("estimated downsized size: " + (mem / (1024L * 1024L)) + " MB");
            System.out.println("available size: " + (availableMemory / (1024L * 1024L)) + " MB");
        } else {
            System.err.println("Wrong downsize!");
            return false;
        }

        if (autoDownsize > 1) {
            nPartsMax = 0;
            for (int i = 0; i < nFrames; i++) {
                nParts[i] = 0;
                tmp = headers[i].getNpart();
                for (int j = 0; j < tmp.length; j++) {
                    nParts[i] += tmp[j] / autoDownsize;
                }
                if (nParts[i] > nPartsMax) {
                    nPartsMax = nParts[i];
                    nPartsMaxGas = tmp[0] / autoDownsize;
                }
            }
        }

        final ExtendedReadGadgetData core1 = new ExtendedReadGadgetData();
        if (nFrames == 1) {
            core1.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {
                    ExtendedReadGadgetData.this.progress = core1.getProgress();
                    ExtendedReadGadgetData.this.fireStateChanged();
                }
            });
        }
        if(downsize == 1)
            core1.readFullSingleFile(filePaths[iPartsMax], readMask, densityFieldDims, logDensityField, (nFrames != 1));
        else
            core1.readFullDownsizedSingleFile(filePaths[iPartsMax], readMask, autoDownsize, densityFieldDims, logDensityField, (nFrames != 1));
        IrregularField tmpField = core1.getField();
        RegularField tmpField2 = core1.getDensityField();
        if (nFrames == 1) {
            field = tmpField;
            densityField = tmpField2;
            return true;
        }

        float[] pos = null;
        float[] vel = null;
        int[] ids = null;
        int[] type = null;
        float[] mass = null;
        float[] u = null;
        //float[] rho = null;
        float[] temp = null;
        boolean[] valid = null;

        pos = new float[nFrames * nPartsMax * 3];
        for (int i = 0; i < pos.length; i++) {
            pos[i] = 0;
        }

        valid = new boolean[nFrames * nPartsMax];
        for (int i = 0; i < valid.length; i++) {
            valid[i] = false;
        }



        if (readMask[0]) {
            vel = tmpField.getData("velocity").getFData();
        }

        ids = tmpField.getData("ID").getIData();

        if (readMask[2]) {
            type = tmpField.getData("type").getIData();
        }
        if (readMask[3]) {
            mass = tmpField.getData("mass").getFData();
        }

        if (readMask[4]) {
            u = tmpField.getCellSet(0).getData(0).getFData(); //energy
        }
//        if (readMask[5]) {
//            rho = tmpField.getCellSet(0).getData(1).getFData(); //density
//        }
        if (readMask[6]) {
            temp = tmpField.getCellSet(0).getData(2).getFData(); //tmperature
        }

        float[] posTmp;
        posTmp = tmpField.getCoords();
        System.arraycopy(posTmp, 0, pos, iPartsMax * nPartsMax * 3, posTmp.length);
        for (int i = 0; i < nPartsMax; i++) {
            valid[iPartsMax * nPartsMax + i] = true;
        }
        tmpField = null;

        if (readMask[6] && readMask[4]) {
            double BOLTZMANN = 1.3806e-16;
            double PROTONMASS = 1.6726e-24;
            double UnitLength_in_cm = 3.085678e21;
            double UnitMass_in_g = 1.989e43;
            double UnitVelocity_in_cm_per_s = 1.0e5;
            double UnitTime_in_s = UnitLength_in_cm / UnitVelocity_in_cm_per_s;
            double UnitEnergy_in_cgs = UnitMass_in_g * Math.pow(UnitLength_in_cm, 2) / Math.pow(UnitTime_in_s, 2);
            double Xh = 0.76;
            double gamma = 5.0 / 3;
            double MeanWeight, uu;
            for (int i = 0; i < nPartsMaxGas; i++) {
                MeanWeight = 4.0 / (3 * Xh + 1 + 4 * Xh) * PROTONMASS;
                uu = u[i] * UnitEnergy_in_cgs / UnitMass_in_g;
                temp[i] = (float) (MeanWeight / BOLTZMANN * (gamma - 1) * uu);
            }
        }
        //done data

        progress = 0.9f / (float) nFrames;
        fireStateChanged();


        //read position timesequence       
        int nThreads = Runtime.getRuntime().availableProcessors();
        FrameReaderFullThread[] threads = new FrameReaderFullThread[nThreads];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new FrameReaderFullThread(i, nThreads, nFrames, iPartsMax, nPartsMax, nParts, autoDownsize, headers, ids, valid, pos);
            threads[i].start();
        }

        try {
            for (int i = 0; i < threads.length; i++) {
                threads[i].join();
            }
        } catch (InterruptedException ex) {
            return false;
        }


        for (int n = 1, l = nPartsMax; n < nFrames; n++) {
            for (int i = 0; i < nPartsMax; i++, l++) {
                if (valid[n * nPartsMax + i] && !valid[(n - 1) * nPartsMax + i]) {
                    //validated celll - copy position to previous invalid
                    for (int j = n - 1; j >= 0; j--) {
                        if (valid[j * nPartsMax + i]) {
                            break;
                        }
                        for (int m = 0; m < 3; m++) {
                            pos[3 * j * nPartsMax + 3 * i + m] = pos[3 * n * nPartsMax + 3 * i + m];
                        }
                    }
                }

                if (!valid[n * nPartsMax + i] && valid[(n - 1) * nPartsMax + i]) {
                    //invalidated cell - copy last position to following invalid
                    for (int j = n; j < nFrames; j++) {
                        if (valid[j * nPartsMax + i]) {
                            break;
                        }
                        for (int m = 0; m < 3; m++) {
                            pos[3 * j * nPartsMax + 3 * i + m] = pos[3 * (n - 1) * nPartsMax + 3 * i + m];
                        }
                    }
                }

            }
        }



        //build out field and cellsets
        System.out.print("creating field...");
        IrregularField outField = new IrregularField();

        outField.setNSpace(3);
        outField.setNNodes(nPartsMax);
        outField.setCoords(pos);
        outField.setMask(valid);
        if (readMask[0]) {
            outField.addData(DataArray.create(vel, 3, "velocity"));
        }
        outField.addData(DataArray.create(ids, 1, "ID"));
        if (readMask[2]) {
            outField.addData(DataArray.create(type, 1, "type"));
        }
        if (readMask[3]) {
            outField.addData(DataArray.create(mass, 1, "mass"));
        }

        int[] header_npart = headers[iPartsMax].getNpart();
        for (int i = 0; i < header_npart.length; i++) {
            header_npart[i] = header_npart[i] / downsize;
        }
        for (int k = 0; k < 6; k++) {
            if (header_npart[k] == 0) {
                continue;
            }

            int[] cells = new int[header_npart[k]];
            boolean[] orient = new boolean[header_npart[k]];
            for (int i = 0, pc_new = 0; i < cells.length; i++, pc_new++) {
                cells[i] = pc_new;
                orient[i] = true;
            }
            CellArray ca = new CellArray(Cell.POINT, cells, orient, null);
            String csName = "cs_";
            switch (k) {
                case 0:
                    csName += "Gas";
                    break;
                case 1:
                    csName += "Halo";
                    break;
                case 2:
                    csName += "Disk";
                    break;
                case 3:
                    csName += "Bulge";
                    break;
                case 4:
                    csName += "Stars";
                    break;
                case 5:
                    csName += "Bndry";
                    break;
            }
            CellSet cs = new CellSet(csName);
            cs.setCellArray(ca);
            if (k == 0 && nPartsMaxGas > 0) {
                if (readMask[4]) {
                    cs.addData(DataArray.create(u, 1, "energy"));
                }
//                if (readMask[5]) {
//                    cs.addData(DataArray.create(rho, 1, "density"));
//                }
                if (readMask[6]) {
                    cs.addData(DataArray.create(temp, 1, "temperature"));
                }
            }
            outField.addCellSet(cs);
        }

        progress = 1.0f;
        fireStateChanged();

        System.out.print("done.");
        field = outField;
        return true;
    }

    public boolean readFullSingleFile(String filePath, boolean[] readMask, int[] densityFieldDims, boolean logDensityField, boolean silent) {
        field = null;
        densityField = null;

        if (filePath == null || filePath.length() < 1) {
            return false;
        }

        if (!silent) {
            progress = 0.0f;
            fireStateChanged();
        }

        File f = new File(filePath);
        if (!f.exists()) {
            return false;
        }

        int[] header_npart = new int[6];
        double[] header_mass = new double[6];
        double header_time;
        double header_redshift;
        int header_flag_sfr;
        int header_flag_feedback;
        int[] header_npartTotal = new int[6];
        int header_flag_cooling;
        int header_num_files;
        double header_BoxSize = -1;
        double header_Omega0;
        double header_OmegaLambda;
        double header_HubbleParam;
        int header_flagAge;
        int header_flagMetals;
        int[] header_nallhw = new int[6];
        byte[] header_fill = new byte[256 - 6 * 4 - 6 * 8 - 2 * 8 - 2 * 4 - 6 * 4 - 2 * 4 - 4 * 8 - 2 * 4 - 6 * 4];  // fills to 256 Bytes

        IrregularField outField = null;
        RegularField outDensityField = null;
        FileImageInputStream in;
        try {
            in = new FileImageInputStream(f);
            in.setByteOrder(ByteOrder.BIG_ENDIAN);
            int test0 = in.readInt();
            if (test0 != 256) {
                in.setByteOrder(ByteOrder.LITTLE_ENDIAN);
                in.seek(0);
                test0 = in.readInt();
                if (test0 != 256) {
                    System.err.println("ERROR: Cannot establish file endianness.");
                    return false;
                }
            }

            //start read header
            in.readFully(header_npart, 0, 6);
            in.readFully(header_mass, 0, 6);
            header_time = in.readDouble();
            header_redshift = in.readDouble();
            header_flag_sfr = in.readInt();
            header_flag_feedback = in.readInt();
            in.readFully(header_npartTotal, 0, 6);
            header_flag_cooling = in.readInt();
            header_num_files = in.readInt();
            header_BoxSize = in.readDouble();
            header_Omega0 = in.readDouble();
            header_OmegaLambda = in.readDouble();
            header_HubbleParam = in.readDouble();
            header_flagAge = in.readInt();
            header_flagMetals = in.readInt();
            in.readFully(header_nallhw, 0, 6);
            in.readFully(header_fill, 0, header_fill.length);
            skipInt(in, 1);
            //end read header
            
            for (int i = 0; i < 6; i++) {
                if(header_nallhw[i] != 0) {
                    System.err.println("ERROR: file with particle number >2^32 not supported!");
                    return false;
                }
            }

            int NumPart = 0, ntot_withmasses = 0, Ngas = 0;
            int typeCount = 0;
            for (int k = 0; k < 6; k++) {
                NumPart += header_npart[k];
                if (header_npart[k] > 0) {
                    typeCount++;
                }
            }
            Ngas = header_npart[0];
            for (int k = 0; k < 6; k++) { 
                if (header_mass[k] == 0) {
                    ntot_withmasses += header_npart[k];
                }
            }

            if (!silent) {
                System.out.println("Header represents " + NumPart + " particles");
            }

            float[] pos = null;
            float[] vel = null;
            int[] id = null;
            int[] type = null;
            float[] mass = null;
            float[] u = null;
            //float[] rho = null;
            float[] temp = null;

            pos = new float[NumPart * 3];
            if (readMask[0]) {
                //System.out.println("particle velocity");
                vel = new float[NumPart * 3];
            }
            if (readMask[1]) {
                //System.out.println("particle ID");
                id = new int[NumPart];
            }
            if (readMask[2]) {
                //System.out.println("particle type");
                type = new int[NumPart];
            }
            if (readMask[3]) {
                //System.out.println("particle mass");
                mass = new float[NumPart];
            }

            if (readMask[4]) {
                //System.out.println("internal energy");
                u = new float[Ngas];
            }
//            if (readMask[5]) {
//                //System.out.println("density");
//                rho = new float[dn_Ngas];
//            }
            if (readMask[6]) {
                //System.out.println("temperature");
                temp = new float[Ngas];
            }

            //allocate densityField            
            float[] densityData = new float[densityFieldDims[0]*densityFieldDims[1]*densityFieldDims[2]];
            
            if (!silent) {
                System.out.println(" done.");
                progress = 0.1f;
                fireStateChanged();
                System.out.print("reading data...");
            }
            
            skipInt(in, 1);
            in.readFully(pos, 0, 3 * NumPart);
            skipInt(in, 1);

            if (!silent) {
                progress = 0.1f + 0.8f * (float) in.getStreamPosition() / (float) in.length();
                fireStateChanged();
            }

            skipInt(in, 1);
            if (readMask[0]) { //velocity
                in.readFully(vel, 0, 3 * NumPart);
            } else {
                in.skipBytes(4 * 3 * NumPart);
            }
            skipInt(in, 1);

            if (!silent) {
                progress = 0.1f + 0.8f * (float) in.getStreamPosition() / (float) in.length();
                fireStateChanged();
            }

            skipInt(in, 1);
            if (readMask[1]) { //ID
                in.readFully(id, 0, NumPart);
            } else {
                in.skipBytes(4 * NumPart);
            }
            skipInt(in, 1);

            if (!silent) {
                progress = 0.1f + 0.8f * (float) in.getStreamPosition() / (float) in.length();
                fireStateChanged();
            }

            if (ntot_withmasses > 0) {
                skipInt(in, 1);
            }

            for (int k = 0, pc = 0; k < 6; k++) {
                if(header_npart[k] == 0)
                    continue;
                
                if (readMask[2]) { //type
                    for (int n = 0; n < header_npart[k]; n++) {
                        type[pc + n] = k;
                    }
                }
                if (header_mass[k] == 0) {
                    if (readMask[3]) { //mass
                        in.readFully(mass, pc, header_npart[k]);
                    } else {
                        in.skipBytes(4 * header_npart[k]);
                    }
                } else {
                    if (readMask[3]) { //mass
                        for (int n = 0; n < header_npart[k]; n++) {
                            mass[pc + n] = (float) header_mass[k];
                        }
                    }
                }
                pc += header_npart[k];
                
                if (!silent) {
                    progress = 0.1f + 0.8f * (float) in.getStreamPosition() / (float) in.length();
                    fireStateChanged();
                }
            }
            
            if (ntot_withmasses > 0) {
                skipInt(in, 1);
            }


            if (header_npart[0] > 0) {
                skipInt(in, 1);
                if (readMask[4]) { //energy
                    in.readFully(u, 0, Ngas);
                } else {
                    in.skipBytes(Ngas * 4);
                }
                skipInt(in, 1);

                if (!silent) {
                    progress = 0.1f + 0.8f * (float) in.getStreamPosition() / (float) in.length();
                    fireStateChanged();
                }

//                skipInt(in, 1);
//                if (readMask[5]) { //density
//                    in.readFully(rho, 0, Ngas);    
//                } else {
//                    in.skipBytes(Ngas * 4);
//                }
                //skipInt(in,1);
            }
            in.close();
            //end read file

            if (!silent) {
                progress = 0.9f;
                fireStateChanged();
            }

            //calculate temperature
            if (readMask[6] && readMask[4]) {
                double BOLTZMANN = 1.3806e-16;
                double PROTONMASS = 1.6726e-24;
                double UnitLength_in_cm = 3.085678e21;
                double UnitMass_in_g = 1.989e43;
                double UnitVelocity_in_cm_per_s = 1.0e5;
                double UnitTime_in_s = UnitLength_in_cm / UnitVelocity_in_cm_per_s;
                double UnitEnergy_in_cgs = UnitMass_in_g * Math.pow(UnitLength_in_cm, 2) / Math.pow(UnitTime_in_s, 2);
                double Xh = 0.76;
                double gamma = 5.0 / 3;
                double MeanWeight, uu;
                for (int i = 0; i < Ngas; i++) {
                    MeanWeight = 4.0 / (3 * Xh + 1 + 4 * Xh) * PROTONMASS;
                    uu = u[i] * UnitEnergy_in_cgs / UnitMass_in_g;
                    temp[i] = (float) (MeanWeight / BOLTZMANN * (gamma - 1) * uu);
                }
            }

            if (!silent) {
                System.out.println("done");
                progress = 0.95f;
                fireStateChanged();
                System.out.print("creating field...");
            }

            outField = new IrregularField();

            outField.setNSpace(3);
            outField.setNNodes(NumPart);
            outField.setCoords(pos);
            if (readMask[0]) {
                outField.addData(DataArray.create(vel, 3, "velocity"));
            }
            if (readMask[1]) {
                outField.addData(DataArray.create(id, 1, "ID"));
            }
            if (readMask[2]) {
                outField.addData(DataArray.create(type, 1, "type"));
            }
            if (readMask[3]) {
                outField.addData(DataArray.create(mass, 1, "mass"));
            }

            for (int k = 0, pc_new = 0; k < 6; k++) {
                if (header_npart[k] == 0) {
                    continue;
                }
                int[] cells = new int[header_npart[k]];
                boolean[] orient = new boolean[header_npart[k]];
                for (int i = 0; i < cells.length; i++, pc_new++) {
                    cells[i] = pc_new;
                    orient[i] = true;
                }
                CellArray ca = new CellArray(Cell.POINT, cells, orient, null);
                String csName = "cs_";
                switch (k) {
                    case 0:
                        csName += "Gas";
                        break;
                    case 1:
                        csName += "Halo";
                        break;
                    case 2:
                        csName += "Disk";
                        break;
                    case 3:
                        csName += "Bulge";
                        break;
                    case 4:
                        csName += "Stars";
                        break;
                    case 5:
                        csName += "Bndry";
                        break;
                }
                CellSet cs = new CellSet(csName);
                cs.setCellArray(ca);
                if (k == 0 && Ngas > 0) {
                    if (readMask[4]) {
                        cs.addData(DataArray.create(u, 1, "energy"));
                    }
//                    if (readMask[5]) {
//                        cs.addData(DataArray.create(rho, 1, "density"));
//                    }
                    if (readMask[6]) {
                        cs.addData(DataArray.create(temp, 1, "temperature"));
                    }
                }
                outField.addCellSet(cs);
            }
            

            //density field
            outDensityField = new RegularField(densityFieldDims);
            float[][] affine = new float[4][3];
            float[] binSize = new float[3];
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 3; j++) {
                    affine[i][j] = 0.0f;
                }
            }            
            
            if(header_BoxSize > 0) {
                for (int j = 0; j < 3; j++) {
                    binSize[j] = (float)header_BoxSize/(float)(densityFieldDims[j]-1);
                    affine[j][j] = binSize[j];
                }                
            } else {
                float[][] ext = outField.getExtents();
                for (int j = 0; j < 3; j++) {
                    binSize[j] = (float)(ext[1][j]-ext[0][j])/(float)(densityFieldDims[j]-1);
                    affine[j][j] = binSize[j];
                    affine[3][j] = ext[0][j];
                }
                
                
            }
            float binVol = binSize[0]*binSize[1]*binSize[2];
            
            
            //density not using masses
            int[] v = new int[3];
            for (int i = 0; i < NumPart; i++) {
                for (int j = 0; j < 3; j++) {
                    v[j] = (int)Math.floor((pos[3*i + j]-affine[3][j])/binSize[j]);
                    if(v[j] < 0) v[j] = 0;
                    if(v[j] >= densityFieldDims[j]) v[j] = densityFieldDims[j]-1;
                }
                densityData[v[2]*densityFieldDims[1]*densityFieldDims[0] + v[1]*densityFieldDims[0] + v[0]]++;
            }
            if(logDensityField) 
                for (int i = 0; i < densityData.length; i++) {                    
                    densityData[i] = (float)Math.log(LOG_CONSTANT + densityData[i]);
                    //densityData[i] = (float)Math.log(LOG_CONSTANT + densityData[i]/binVol);
                }
            
            
            
            outDensityField.setAffine(affine);
            outDensityField.addData(DataArray.create(densityData, 1, "density"));

            if (!silent) {
                progress = 1.0f;
                fireStateChanged();
                System.out.println("done");
            }
        } catch (Exception ex) {
            System.err.println("ERROR!");
            ex.printStackTrace();
            return false;
        } catch (OutOfMemoryError err) {
            System.err.println("ERROR!");
            System.err.println("Out of memory!");
            return false;
        }

        field = outField;
        densityField = outDensityField;
        return true;
    }

    public boolean readFullDownsizedSingleFile(String filePath, boolean[] readMask, int downsize, int[] densityFieldDims, boolean logDensityField, boolean silent) {
        if(downsize == 1)
            return readFullSingleFile(filePath, readMask, densityFieldDims, logDensityField, silent);
        
        field = null;
        densityField = null;

        if (filePath == null || filePath.length() < 1) {
            return false;
        }

        if (!silent) {
            progress = 0.0f;
            fireStateChanged();
        }

        File f = new File(filePath);
        if (!f.exists()) {
            return false;
        }

        int[] header_npart = new int[6];
        double[] header_mass = new double[6];
        double header_time;
        double header_redshift;
        int header_flag_sfr;
        int header_flag_feedback;
        int[] header_npartTotal = new int[6];
        int header_flag_cooling;
        int header_num_files;
        double header_BoxSize;
        double header_Omega0;
        double header_OmegaLambda;
        double header_HubbleParam;
        int header_flagAge;
        int header_flagMetals;
        int[] header_nallhw = new int[6];
        byte[] header_fill = new byte[256 - 6 * 4 - 6 * 8 - 2 * 8 - 2 * 4 - 6 * 4 - 2 * 4 - 4 * 8 - 2 * 4 - 6 * 4];  // fills to 256 Bytes

        IrregularField outField = null;
        RegularField outDensityField = null;
        FileImageInputStream in;
        try {
            in = new FileImageInputStream(f);
            in.setByteOrder(ByteOrder.BIG_ENDIAN);

            if (!silent) {
                System.out.println("Testing data endianness...");
            }
            int test0 = in.readInt();
            if (test0 != 256) {
                if (!silent) {
                    System.out.println("BIG ENDIAN: error");
                }
                in.setByteOrder(ByteOrder.LITTLE_ENDIAN);
                in.seek(0);
                test0 = in.readInt();
                if (test0 != 256) {
                    System.err.println("ERROR: Cannot establish file endianness.");
                    return false;
                } else {
                    if (!silent) {
                        System.out.println("LITTLE ENDIAN: OK");
                    }
                }
            } else {
                if (!silent) {
                    System.out.println("BIG ENDIAN: OK");
                }
            }

            //start read header
            in.readFully(header_npart, 0, 6);
            in.readFully(header_mass, 0, 6);
            header_time = in.readDouble();
            header_redshift = in.readDouble();
            header_flag_sfr = in.readInt();
            header_flag_feedback = in.readInt();
            in.readFully(header_npartTotal, 0, 6);
            header_flag_cooling = in.readInt();
            header_num_files = in.readInt();
            header_BoxSize = in.readDouble();
            header_Omega0 = in.readDouble();
            header_OmegaLambda = in.readDouble();
            header_HubbleParam = in.readDouble();
            header_flagAge = in.readInt();
            header_flagMetals = in.readInt();
            in.readFully(header_nallhw, 0, 6);
            in.readFully(header_fill, 0, header_fill.length);
            skipInt(in, 1);
            //end read header
            
            for (int i = 0; i < 6; i++) {
                if(header_nallhw[i] != 0) {
                    System.err.println("ERROR: file with particle number >2^32 not supported!");
                    return false;
                }
            }
            

            int NumPart = 0, ntot_withmasses = 0, Ngas = 0;
            int dn_NumPart = 0, dn_ntot_withmasses = 0, dn_Ngas = 0;
            int typeCount = 0;
            for (int k = 0; k < 6; k++) { //dlaczego 5 a nie 6 ???
                NumPart += header_npart[k];
                dn_NumPart += header_npart[k] / downsize;
                if (header_npart[k] > 0) {
                    typeCount++;
                }
            }
            Ngas = header_npart[0];
            dn_Ngas = header_npart[0] / downsize;
            for (int k = 0; k < 6; k++) { 
                if (header_mass[k] == 0) {
                    ntot_withmasses += header_npart[k];
                    dn_ntot_withmasses += header_npart[k] / downsize;
                }
            }

            if (dn_NumPart < 1) {
                return false;
            }

            if (!silent) {
                System.out.println("Header represents " + NumPart + " particles, reading with downsize: " + dn_NumPart + " particles");
            }

            float[] pos = null;
            float[] vel = null;
            int[] id = null;
            int[] type = null;
            float[] mass = null;
            float[] u = null;
            //float[] rho = null;
            float[] temp = null;

            pos = new float[dn_NumPart * 3];
            if (readMask[0]) {
                //System.out.println("particle velocity");
                vel = new float[dn_NumPart * 3];
            }
            if (readMask[1]) {
                //System.out.println("particle ID");
                id = new int[dn_NumPart];
            }
            if (readMask[2]) {
                //System.out.println("particle type");
                type = new int[dn_NumPart];
            }
            if (readMask[3]) {
                //System.out.println("particle mass");
                mass = new float[dn_NumPart];
            }

            if (readMask[4]) {
                //System.out.println("internal energy");
                u = new float[dn_Ngas];
            }
//            if (readMask[5]) {
//                //System.out.println("density");
//                rho = new float[dn_Ngas];
//            }
            if (readMask[6]) {
                //System.out.println("temperature");
                temp = new float[dn_Ngas];
            }

            //allocate densityField
            float[] densityData = new float[densityFieldDims[0]*densityFieldDims[1]*densityFieldDims[2]];

            if (!silent) {
                System.out.println(" done.");
                progress = 0.1f;
                fireStateChanged();
                System.out.print("reading data...");
            }
            
            //estimate buffer size
            int bufSize;
            if(downsize < BUFFER_SIZE) {
                bufSize = downsize*(BUFFER_SIZE/downsize);
            } else {
                bufSize = downsize;
            }                
            
            skipInt(in, 1);
            float[] buf = new float[3*bufSize];                
            for (int k = 0, pc = 0; k < 6; k++) {
                int rest = header_npart[k] % bufSize;
                for (int n = 0; n < header_npart[k]; n += bufSize) {
                    if (n + bufSize > header_npart[k]) {                            
                        in.readFully(buf, 0, 3*rest);
                        for (int m = 0; m < rest; m += downsize) {
                            if(m+downsize <= rest) {
                                System.arraycopy(buf, 3*m, pos, 3 * pc, 3);
                                pc++;
                            }
                        }
                    } else {
                        in.readFully(buf, 0, 3*bufSize);
                        for (int m = 0; m < bufSize; m += downsize) {
                            System.arraycopy(buf, 3*m, pos, 3 * pc, 3);
                            pc++;
                        }
                    }
                }
            }
            buf = null;            
            skipInt(in, 1);
            
            if(logDensityField) {
                for (int i = 0; i < densityData.length; i++) {                    
                    densityData[i] = (float)Math.log(LOG_CONSTANT + densityData[i]);
                }
            }

            if (!silent) {
                progress = 0.1f + 0.8f * (float) in.getStreamPosition() / (float) in.length();
                fireStateChanged();
            }

            skipInt(in, 1);
            if (readMask[0]) { //velocity
                buf = new float[3*bufSize];                
                for (int k = 0, pc = 0; k < 6; k++) {
                    int rest = header_npart[k] % bufSize;
                    for (int n = 0; n < header_npart[k]; n += bufSize) {
                        if (n + bufSize > header_npart[k]) {                            
                            in.readFully(buf, 0, 3*rest);
                            for (int m = 0; m < rest; m += downsize) {
                                if(m+downsize <= rest) {
                                    System.arraycopy(buf, 3*m, vel, 3 * pc, 3);
                                    pc++;
                                }
                            }
                        } else {
                            in.readFully(buf, 0, 3*bufSize);
                            for (int m = 0; m < bufSize; m += downsize) {
                                System.arraycopy(buf, 3*m, vel, 3 * pc, 3);
                                pc++;
                            }
                        }
                    }
                }
                buf = null;
            } else {
                in.skipBytes(4 * 3 * NumPart);
            }
            skipInt(in, 1);

            if (!silent) {
                progress = 0.1f + 0.8f * (float) in.getStreamPosition() / (float) in.length();
                fireStateChanged();
            }

            skipInt(in, 1);
            if (readMask[1]) { //ID
                int[] ibuf = new int[bufSize];                
                for (int k = 0, pc = 0; k < 6; k++) {
                    int rest = header_npart[k] % bufSize;
                    for (int n = 0; n < header_npart[k]; n += bufSize) {
                        if (n + bufSize > header_npart[k]) {                            
                            in.readFully(ibuf, 0, rest);
                            for (int m = 0; m < rest; m += downsize) {
                                if(m+downsize <= rest) {
                                    id[pc] = ibuf[m];
                                    pc++;
                                }
                            }
                        } else {
                            in.readFully(ibuf, 0, bufSize);
                            for (int m = 0; m < bufSize; m += downsize) {
                                id[pc] = ibuf[m];
                                pc++;
                            }
                        }
                    }
                }
                buf = null;
            } else {
                in.skipBytes(4 * NumPart);
            }
            skipInt(in, 1);

            if (!silent) {
                progress = 0.1f + 0.8f * (float) in.getStreamPosition() / (float) in.length();
                fireStateChanged();
            }

            if (ntot_withmasses > 0) {
                skipInt(in, 1);
            }

            for (int k = 0, pc = 0; k < 6; k++) {
                if(header_npart[k] == 0)
                    continue;

                if (readMask[2]) { //type
                    for (int n = 0; n < (header_npart[k]/downsize); n++) {
                        type[pc + n] = k;
                    }
                }

                if(header_mass[k] == 0) {
                    int[] ibuf = null;
                    if (readMask[3])
                            ibuf = new int[bufSize];                
                    int rest = header_npart[k] % bufSize;
                    for (int n = 0; n < header_npart[k]; n += bufSize) {
                        if (readMask[3]) { //mass
                            if (n + bufSize > header_npart[k]) {                            
                                in.readFully(ibuf, 0, rest);
                                for (int m = 0; m < rest; m += downsize) {
                                    if(m+downsize <= rest) {
                                        mass[pc] = ibuf[m];
                                        pc++;
                                    }
                                }
                            } else {
                                in.readFully(ibuf, 0, bufSize);
                                for (int m = 0; m < bufSize; m += downsize) {
                                    mass[pc] = ibuf[m];
                                    pc++;
                                }
                            }
                        } else {
                            if (n + bufSize > header_npart[k]) {                            
                                in.skipBytes(4*rest);
                            } else {
                                in.skipBytes(4*bufSize);
                            }
                        }
                    }
                    ibuf = null;
                } else {
                    if (readMask[3]) { //mass
                        for (int n = 0; n < header_npart[k]/downsize; n++) {
                            mass[pc + n] = (float) header_mass[k];
                        }                            
                    }
                }

                if(!(readMask[3] && header_mass[k] == 0)) {
                    pc += header_npart[k]/downsize;
                }
                
                if (!silent) {
                    progress = 0.1f + 0.8f * (float) in.getStreamPosition() / (float) in.length();
                    fireStateChanged();
                }
            }
            
            if (ntot_withmasses > 0) {
                skipInt(in, 1);
            }

            if (header_npart[0] > 0) {
                skipInt(in, 1);
                if (readMask[4]) { //energy
                    buf = new float[bufSize];                
                    int rest = header_npart[0] % bufSize;
                    for (int n = 0, pc = 0; n < header_npart[0]; n += bufSize) {
                        if (n + bufSize > header_npart[0]) {                            
                            in.readFully(buf, 0, rest);
                            for (int m = 0; m < rest; m += downsize) {
                                if(m+downsize <= rest) {
                                    u[pc] = buf[m];
                                    pc++;
                                }
                            }
                        } else {
                            in.readFully(buf, 0, bufSize);
                            for (int m = 0; m < bufSize; m += downsize) {
                                u[pc] = buf[m];
                                pc++;
                            }
                        }
                    }
                    buf = null;
                } else {
                    in.skipBytes(Ngas * 4);
                }
                skipInt(in, 1);

                if (!silent) {
                    progress = 0.1f + 0.8f * (float) in.getStreamPosition() / (float) in.length();
                    fireStateChanged();
                }

//                skipInt(in, 1);
//                if (readMask[5]) { //density
//                        float[] tmpF = new float[downsize];
//                        for (int n = 0, pc = 0; n < header_npart[0]; n+=downsize) {
//                            if(n+downsize > header_npart[0]) {
//                                in.skipBytes(4 * (header_npart[0] % downsize));
//                            } else {
//                                in.readFully(tmpF, 0, downsize);
//                                System.arraycopy(tmpF, 0, rho, pc, 1);
//                                pc++;
//                            }                            
//                        }
//                } else {
//                    in.skipBytes(Ngas * 4);
//                }
                //skipInt(in,1);
            }
            in.close();
            //end read file

            if (!silent) {
                progress = 0.9f;
                fireStateChanged();
            }

            //calculate temperature
            if (readMask[6] && readMask[4]) {
                double BOLTZMANN = 1.3806e-16;
                double PROTONMASS = 1.6726e-24;
                double UnitLength_in_cm = 3.085678e21;
                double UnitMass_in_g = 1.989e43;
                double UnitVelocity_in_cm_per_s = 1.0e5;
                double UnitTime_in_s = UnitLength_in_cm / UnitVelocity_in_cm_per_s;
                double UnitEnergy_in_cgs = UnitMass_in_g * Math.pow(UnitLength_in_cm, 2) / Math.pow(UnitTime_in_s, 2);
                double Xh = 0.76;
                double gamma = 5.0 / 3;
                double MeanWeight, uu;
                for (int i = 0; i < dn_Ngas; i++) {
                    MeanWeight = 4.0 / (3 * Xh + 1 + 4 * Xh) * PROTONMASS;
                    uu = u[i] * UnitEnergy_in_cgs / UnitMass_in_g;
                    temp[i] = (float) (MeanWeight / BOLTZMANN * (gamma - 1) * uu);
                }
            }

            if (!silent) {
                System.out.println("done");
                progress = 0.95f;
                fireStateChanged();
                System.out.print("creating field...");
            }

            outField = new IrregularField();

            outField.setNSpace(3);
            outField.setNNodes(dn_NumPart);
            outField.setCoords(pos);
            if (readMask[0]) {
                outField.addData(DataArray.create(vel, 3, "velocity"));
            }
            if (readMask[1]) {
                outField.addData(DataArray.create(id, 1, "ID"));
            }
            if (readMask[2]) {
                outField.addData(DataArray.create(type, 1, "type"));
            }
            if (readMask[3]) {
                outField.addData(DataArray.create(mass, 1, "mass"));
            }

            for (int k = 0, pc_new = 0; k < 6; k++) {
                if (header_npart[k] == 0) {
                    continue;
                }
                int[] cells = new int[header_npart[k] / downsize];
                boolean[] orient = new boolean[header_npart[k] / downsize];
                for (int i = 0; i < cells.length; i++, pc_new++) {
                    cells[i] = pc_new;
                    orient[i] = true;
                }
                CellArray ca = new CellArray(Cell.POINT, cells, orient, null);
                String csName = "cs_";
                switch (k) {
                    case 0:
                        csName += "Gas";
                        break;
                    case 1:
                        csName += "Halo";
                        break;
                    case 2:
                        csName += "Disk";
                        break;
                    case 3:
                        csName += "Bulge";
                        break;
                    case 4:
                        csName += "Stars";
                        break;
                    case 5:
                        csName += "Bndry";
                        break;
                }
                CellSet cs = new CellSet(csName);
                cs.setCellArray(ca);
                if (k == 0 && dn_Ngas > 0) {
                    if (readMask[4]) {
                        cs.addData(DataArray.create(u, 1, "energy"));
                    }
//                    if (readMask[5]) {
//                        cs.addData(DataArray.create(rho, 1, "density"));
//                    }
                    if (readMask[6]) {
                        cs.addData(DataArray.create(temp, 1, "temperature"));
                    }
                }
                outField.addCellSet(cs);
            }
            
            //density field
            outDensityField = new RegularField(densityFieldDims);
            float[][] affine = new float[4][3];
            float[] binSize = new float[3];
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 3; j++) {
                    affine[i][j] = 0.0f;
                }
            }
            
            if(header_BoxSize > 0) {
                for (int j = 0; j < 3; j++) {
                    binSize[j] = (float)header_BoxSize/(float)(densityFieldDims[j]-1);
                    affine[j][j] = binSize[j];
                }                
            } else {
                float[][] ext = outField.getExtents();
                for (int j = 0; j < 3; j++) {
                    binSize[j] = (float)(ext[1][j]-ext[0][j])/(float)(densityFieldDims[j]-1);
                    affine[j][j] = binSize[j];
                    affine[3][j] = ext[0][j];
                }                
            }
            float binVol = binSize[0]*binSize[1]*binSize[2];
            
            //density not using masses
            int[] v = new int[3];
            for (int i = 0; i < dn_NumPart; i++) {
                for (int j = 0; j < 3; j++) {
                    v[j] = (int)Math.floor((pos[3*i + j]-affine[3][j])/binSize[j]);
                    if(v[j] < 0) v[j] = 0;
                    if(v[j] >= densityFieldDims[j]) v[j] = densityFieldDims[j]-1;
                }
                densityData[v[2]*densityFieldDims[1]*densityFieldDims[0] + v[1]*densityFieldDims[0] + v[0]]++;
            }
            if(logDensityField) 
                for (int i = 0; i < densityData.length; i++) {                    
                    densityData[i] = (float)Math.log(LOG_CONSTANT + densityData[i]);
                    //densityData[i] = (float)Math.log(LOG_CONSTANT + densityData[i]/binVol);
                }
            outDensityField.setAffine(affine);
            outDensityField.addData(DataArray.create(densityData, 1, "density"));

            if (!silent) {
                progress = 1.0f;
                fireStateChanged();
                System.out.println("done");
            }
        } catch (Exception ex) {
            System.err.println("ERROR!");
            ex.printStackTrace();
            return false;
        } catch (OutOfMemoryError err) {
            System.err.println("ERROR!");
            System.err.println("Out of memory!");
            return false;
        }

        field = outField;
        densityField = outDensityField;
        return true;
    }
    
    private boolean readCropped(String[] filePaths, boolean[] readMask, int downsize, int[] densityFieldDims, boolean logDensityField, float[][] cropExtents, long availableMemory) {
        if(cropExtents == null)
            return readFull(filePaths, readMask, downsize, densityFieldDims, logDensityField, availableMemory);
        
        for (int i = 0; i < filePaths.length; i++) {
            File f = new File(filePaths[i]);
            if (!f.exists() || !f.canRead()) {
                System.err.println("ERROR: cannot read file " + filePaths[i]);
                return false;
            }
        }

        progress = 0.0f;
        fireStateChanged();


        int nFrames = filePaths.length;

        GadgetFileHeader[] headers = new GadgetFileHeader[nFrames];
        int[] nParts = new int[nFrames];
        int[] nPartsCropped = new int[nFrames];
        int[][] nPartsCroppedTmp = new int[nFrames][6];
        int[] tmp;
        int nPartsMax = 0;
        int nPartsMaxCropped = 0;
        int nPartsMaxGas = 0;
        int nPartsMaxGasCropped = 0;
        int iPartsMax = 0;
        int iPartsMaxCropped = 0;

        for (int i = 0; i < nFrames; i++) {
            headers[i] = GadgetFileHeader.read(filePaths[i]);
            if (headers[i] == null) {
                System.err.println("ERROR: error reading file " + filePaths[i]);
                return false;
            }
            int[] nallhw = headers[i].getNallhw();
            for (int j = 0; j < nallhw.length; j++) {
                if(nallhw[j] != 0) {
                    System.err.println("ERROR: in file " + filePaths[i]+". Files with particle number >2^32 not supported!");
                    return false;
                }
            }
            
            nParts[i] = 0;
            tmp = headers[i].getNpart();
            for (int j = 0; j < tmp.length; j++) {
                nParts[i] += tmp[j];
            }
            if (nParts[i] > nPartsMax) {
                nPartsMax = nParts[i];
                nPartsMaxGas = tmp[0];
                iPartsMax = i;
            }
            
            
            //analyze count in cropRange
            FileImageInputStream in;
            try {
                in = new FileImageInputStream(new File(headers[i].getFilePath()));
                in.setByteOrder(headers[i].getEndian());

                skipInt(in, 1);
                in.skipBytes(256); //skip header
                skipInt(in, 1);

                //estimate buffer size
                int bufSize = BUFFER_SIZE;

                skipInt(in, 1);
                int[] header_npart = headers[i].getNpart();
                float[] buf = new float[3*bufSize];                
                float p;
                boolean ok = false;
                for (int k = 0; k < 6; k++) {
                    nPartsCroppedTmp[i][k] = 0;
                    int rest = header_npart[k] % bufSize;
                    for (int n = 0; n < header_npart[k]; n += bufSize) {
                        int readLen = bufSize;
                        if (n + bufSize > header_npart[k])
                            readLen = rest;
                        
                        in.readFully(buf, 0, 3*readLen);
                        for (int j = 0; j < readLen; j++) {                            
                            ok = true;
                            for (int l = 0; l < 3; l++) {
                                p = buf[3*j + l];
                                if(p < cropExtents[0][l] || p > cropExtents[1][l]) {
                                    ok = false;
                                    break;
                                }
                            }
                            if(ok) {
                                nPartsCroppedTmp[i][k]++;
                            }                                                        
                        }
                    }                    
                }
                buf = null;            
                in.close();
            } catch(IOException ex) {
                System.err.println("ERROR reading file "+headers[i].getFilePath());
                return false; 
            }
            
            nPartsCropped[i] = 0;
            for (int j = 0; j < 6; j++) {
                nPartsCropped[i] += nPartsCroppedTmp[i][j];
            }
            if (nPartsCropped[i] > nPartsMaxCropped) {
                nPartsMaxCropped = nPartsCropped[i];
                nPartsMaxGasCropped = nPartsCroppedTmp[i][0];
            }
        }
        //TODO to nie uwzględnia odpowiednio umierania i rodzenia się cząsteczek, trzeba monitorować ID
        System.out.println("Maximum of " + nPartsMaxCropped + " particles found in cropped region");


        int autoDownsize = 1;
        long mem = 0;
        if (downsize > 0) {
            System.out.println("estimating full data memory needs...");
            mem = estimateMemory(nFrames, nPartsMaxCropped, nPartsMaxGasCropped, readMask, downsize, densityFieldDims);
            System.out.println("estimated size: " + (mem / (1024L * 1024L)) + " MB");
            System.out.println("available size: " + (availableMemory / (1024L * 1024L)) + " MB");
            if (mem > availableMemory) {
                System.err.println("Not enough memory to read Gadget-2 data with provided settings!");
                return false;
            }
            autoDownsize = downsize;
        } else if (downsize == -1) {

            autoDownsize = 1;
            mem = estimateMemory(nFrames, nPartsMaxCropped, nPartsMaxGasCropped, readMask, 1, densityFieldDims);
            while (mem > availableMemory) {
                autoDownsize++;
                mem = estimateMemory(nFrames, nPartsMaxCropped, nPartsMaxGasCropped, readMask, autoDownsize, densityFieldDims);
            }
            System.out.println("estimated downsize: " + autoDownsize);
            System.out.println("estimated downsized size: " + (mem / (1024L * 1024L)) + " MB");
            System.out.println("available size: " + (availableMemory / (1024L * 1024L)) + " MB");
        } else {
            System.err.println("Wrong downsize!");
            return false;
        }

        if (autoDownsize > 1) {
            nPartsMaxCropped = 0;
            for (int i = 0; i < nFrames; i++) {
                nPartsCropped[i] = 0;
                tmp = nPartsCroppedTmp[i];
                for (int j = 0; j < tmp.length; j++) {
                    nPartsCropped[i] += tmp[j] / autoDownsize;
                }
                if (nPartsCropped[i] > nPartsMaxCropped) {
                    nPartsMaxCropped = nPartsCropped[i];
                    nPartsMaxGasCropped = tmp[0] / autoDownsize;
                }
            }
        }
        

        final ExtendedReadGadgetData core1 = new ExtendedReadGadgetData();
        if (nFrames == 1) {
            core1.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {
                    ExtendedReadGadgetData.this.progress = core1.getProgress();
                    ExtendedReadGadgetData.this.fireStateChanged();
                }
            });
        }
        core1.readCroppedDownsizedSingleFile(filePaths[iPartsMaxCropped], readMask, autoDownsize, densityFieldDims, logDensityField, cropExtents, nPartsCroppedTmp[iPartsMaxCropped], (nFrames != 1));
        
        IrregularField tmpField = core1.getField();
        RegularField tmpField2 = core1.getDensityField();
        if (nFrames == 1) {
            field = tmpField;
            densityField = tmpField2;
            return true;
        }

        //TODO        
        return false;        
    }

    public boolean readCroppedDownsizedSingleFile(String filePath, boolean[] readMask, int downsize, int[] densityFieldDims, boolean logDensityField, float[][] cropExtents, int[] header_npartC, boolean silent) {
        field = null;
        densityField = null;

        if (filePath == null || filePath.length() < 1) {
            return false;
        }

        if (!silent) {
            progress = 0.0f;
            fireStateChanged();
        }

        File f = new File(filePath);
        if (!f.exists()) {
            return false;
        }

        int[] header_npart = new int[6];
        double[] header_mass = new double[6];
        double header_time;
        double header_redshift;
        int header_flag_sfr;
        int header_flag_feedback;
        int[] header_npartTotal = new int[6];
        int header_flag_cooling;
        int header_num_files;
        double header_BoxSize;
        double header_Omega0;
        double header_OmegaLambda;
        double header_HubbleParam;
        int header_flagAge;
        int header_flagMetals;
        int[] header_nallhw = new int[6];
        byte[] header_fill = new byte[256 - 6 * 4 - 6 * 8 - 2 * 8 - 2 * 4 - 6 * 4 - 2 * 4 - 4 * 8 - 2 * 4 - 6 * 4];  // fills to 256 Bytes

        IrregularField outField = null;
        RegularField outDensityField = null;
        FileImageInputStream in;
        try {
            in = new FileImageInputStream(f);
            in.setByteOrder(ByteOrder.BIG_ENDIAN);

            if (!silent) {
                System.out.println("Testing data endianness...");
            }
            int test0 = in.readInt();
            if (test0 != 256) {
                if (!silent) {
                    System.out.println("BIG ENDIAN: error");
                }
                in.setByteOrder(ByteOrder.LITTLE_ENDIAN);
                in.seek(0);
                test0 = in.readInt();
                if (test0 != 256) {
                    System.err.println("ERROR: Cannot establish file endianness.");
                    return false;
                } else {
                    if (!silent) {
                        System.out.println("LITTLE ENDIAN: OK");
                    }
                }
            } else {
                if (!silent) {
                    System.out.println("BIG ENDIAN: OK");
                }
            }

            //start read header
            in.readFully(header_npart, 0, 6);
            in.readFully(header_mass, 0, 6);
            header_time = in.readDouble();
            header_redshift = in.readDouble();
            header_flag_sfr = in.readInt();
            header_flag_feedback = in.readInt();
            in.readFully(header_npartTotal, 0, 6);
            header_flag_cooling = in.readInt();
            header_num_files = in.readInt();
            header_BoxSize = in.readDouble();
            header_Omega0 = in.readDouble();
            header_OmegaLambda = in.readDouble();
            header_HubbleParam = in.readDouble();
            header_flagAge = in.readInt();
            header_flagMetals = in.readInt();
            in.readFully(header_nallhw, 0, 6);
            in.readFully(header_fill, 0, header_fill.length);
            skipInt(in, 1);
            //end read header
            
            for (int i = 0; i < 6; i++) {
                if(header_nallhw[i] != 0) {
                    System.err.println("ERROR: file with particle number >2^32 not supported!");
                    return false;
                }
            }
            

            int NumPart = 0, ntot_withmasses = 0, Ngas = 0;
            //int dn_NumPart = 0, dn_ntot_withmasses = 0, dn_Ngas = 0;
            int NumPartC = 0, ntot_withmassesC = 0, NgasC = 0;
            int dn_NumPartC = 0, dn_ntot_withmassesC = 0, dn_NgasC = 0;
            int typeCount = 0;
            int typeCountC = 0;
            
            for (int k = 0; k < 6; k++) {
                NumPart += header_npart[k];                
                //dn_NumPart += header_npart[k] / downsize;
                if (header_npart[k] > 0) {
                    typeCount++;
                }
                NumPartC += header_npartC[k];                
                dn_NumPartC += header_npartC[k] / downsize;
                if (header_npartC[k] > 0) {
                    typeCountC++;
                }
            }
            Ngas = header_npart[0];
            //dn_Ngas = header_npart[0] / downsize;
            NgasC = header_npartC[0];
            dn_NgasC = header_npartC[0] / downsize;
            for (int k = 0; k < 6; k++) { 
                if (header_mass[k] == 0) {
                    ntot_withmasses += header_npart[k];
                    //dn_ntot_withmasses += header_npart[k] / downsize;
                    ntot_withmassesC += header_npartC[k];
                    dn_ntot_withmassesC += header_npartC[k] / downsize;
                }
            }
            
            if (dn_NumPartC < 1) {
                return false;
            }

            if (!silent) {
                System.out.println("Header represents " + NumPart + " particles, "+NumPartC+" in cropped region, reading with downsize: " + dn_NumPartC + " particles");
            }

            float[] pos = null;
            int[] offs = null;
            float[] vel = null;
            int[] id = null;
            int[] type = null;
            float[] mass = null;
            float[] u = null;
            //float[] rho = null;
            float[] temp = null;

            pos = new float[dn_NumPartC * 3];
            if(readMask[0] || readMask[1] || readMask[2] || 
                    readMask[3] || readMask[4] || readMask[6]) {
                offs = new int[dn_NumPartC];
            }            
            
            if (readMask[0]) {
                //System.out.println("particle velocity");
                vel = new float[dn_NumPartC * 3];
            }
            if (readMask[1]) {
                //System.out.println("particle ID");
                id = new int[dn_NumPartC];
            }
            if (readMask[2]) {
                //System.out.println("particle type");
                type = new int[dn_NumPartC];
            }
            if (readMask[3]) {
                //System.out.println("particle mass");
                mass = new float[dn_NumPartC];
            }

            if (readMask[4]) {
                //System.out.println("internal energy");
                u = new float[dn_NgasC];
            }
//            if (readMask[5]) {
//                //System.out.println("density");
//                rho = new float[dn_NgasC];
//            }
            if (readMask[6]) {
                //System.out.println("temperature");
                temp = new float[dn_NgasC];
            }

            //allocate densityField            
            float[] densityData = new float[densityFieldDims[0]*densityFieldDims[1]*densityFieldDims[2]];
            
            if (!silent) {
                System.out.println(" done.");
                progress = 0.1f;
                fireStateChanged();
                System.out.print("reading data...");
            }
            
            //estimate buffer size
            int bufSize;
            if(downsize < BUFFER_SIZE) {
                bufSize = downsize*(BUFFER_SIZE/downsize);
            } else {
                bufSize = downsize;
            }                
            
            skipInt(in, 1);
            float[] buf = new float[3*bufSize];                
            float p;
            boolean ok = true;
            for (int k = 0, off = 0, pc = 0; k < 6; k++) {
                for (int i = 0; i < k; i++) {
                    off += header_npart[i];                    
                }                
                int rest = header_npart[k] % bufSize;
                for (int n = 0; n < header_npart[k]; n += bufSize) {                    
                    int readLen = bufSize;
                    if (n + bufSize > header_npart[k]) {                            
                        readLen = rest;
                    }                                       
                    in.readFully(buf, 0, 3*readLen);
                    for (int j = 0; j < readLen; j+=downsize) {                            
                        ok = true;
                        for (int l = 0; l < 3; l++) {
                            p = buf[3*j + l];
                            if(p < cropExtents[0][l] || p > cropExtents[1][l]) {
                                ok = false;
                                break;
                            }
                        }
                        if(ok && pc < dn_NumPartC) {
                            offs[pc] = off+j;
                            System.arraycopy(buf, 3*j, pos, 3*pc, 3);
                            pc++;
                        }                                                        
                    }
                    off += bufSize;
                }
            }
            buf = null;            
            skipInt(in, 1);
            
            if (!silent) {
                progress = 0.1f + 0.8f * (float) in.getStreamPosition() / (float) in.length();
                fireStateChanged();
            }

            skipInt(in, 1);
            if (readMask[0]) { //velocity
                buf = new float[3*bufSize];                
                for (int k = 0, off = 0, pc = 0; k < 6; k++) {
                    for (int i = 0; i < k; i++) {
                        off += header_npart[i];                    
                    }                
                    int rest = header_npart[k] % bufSize;
                    for (int n = 0; n < header_npart[k]; n += bufSize) {
                        int readLen = bufSize;
                        if (n + bufSize > header_npart[k]) {                            
                            readLen = rest;
                        }                                       
                        in.readFully(buf, 0, 3*readLen);
                        for (int j = 0; j < readLen; j+=downsize) {                            
                            if(pc < offs.length && offs[pc] == (off + j)) {
                                System.arraycopy(buf, 3*j, vel, 3*pc, 3);
                                pc++;
                            }
                       }
                       off += bufSize;
                    }                    
                }
                buf = null;
            } else {
                in.skipBytes(4 * 3 * NumPart);
            }
            skipInt(in, 1);

            if (!silent) {
                progress = 0.1f + 0.8f * (float) in.getStreamPosition() / (float) in.length();
                fireStateChanged();
            }

            skipInt(in, 1);
            if (readMask[1]) { //ID
                int[] ibuf = new int[bufSize];                
                for (int k = 0, off = 0, pc = 0; k < 6; k++) {
                    for (int i = 0; i < k; i++) {
                        off += header_npart[i];                    
                    }                
                    int rest = header_npart[k] % bufSize;
                    for (int n = 0; n < header_npart[k]; n += bufSize) {
                        int readLen = bufSize;
                        if (n + bufSize > header_npart[k]) {                            
                            readLen = rest;
                        }                                       
                        in.readFully(ibuf, 0, readLen);
                        for (int m = 0; m < bufSize; m+=downsize, off+=downsize) {
                            if(pc < offs.length && offs[pc] == (off + m)) {
                                id[pc] = ibuf[m];
                                pc++;
                            }
                        }
                        off += bufSize;
                    }
                }
                buf = null;
            } else {
                in.skipBytes(4 * NumPart);
            }
            skipInt(in, 1);

            if (!silent) {
                progress = 0.1f + 0.8f * (float) in.getStreamPosition() / (float) in.length();
                fireStateChanged();
            }

            if (ntot_withmasses > 0) {
                skipInt(in, 1);
            }

            for (int k = 0, off = 0, pc = 0; k < 6; k++) {
                if(header_npart[k] == 0) 
                    continue;

                if (readMask[2]) { //type
                    for (int n = 0; n < (header_npartC[k]/downsize); n++) {
                        type[pc + n] = k;
                    }
                }

                if(header_mass[k] == 0) {
                    int[] ibuf = null;
                    if (readMask[3])
                            ibuf = new int[bufSize];                
                    int rest = header_npart[k] % bufSize;
                    for (int n = 0; n < header_npart[k]; n += bufSize) {
                        int readLen = bufSize;
                        if (n + bufSize > header_npart[k]) {                            
                            readLen = rest;
                        }                                       
                        if (readMask[3]) { //mass
                            in.readFully(ibuf, 0, readLen);
                            for (int m = 0; m < bufSize; m += downsize, off += downsize) {
                                if(offs[pc] == off) {
                                    mass[pc] = ibuf[m];
                                    pc++;
                                }
                            }
                        } else {
                            in.skipBytes(4*readLen);
                        }
                    }
                    ibuf = null;
                } else {
                    if (readMask[3]) { //mass
                        for (int n = 0; n < header_npartC[k]/downsize; n++) {
                            mass[pc + n] = (float) header_mass[k];
                        }                            
                    }
                }

                if(!(readMask[3] && header_mass[k] == 0)) {
                    pc += header_npartC[k]/downsize;
                }
                
                if (!silent) {
                    progress = 0.1f + 0.8f * (float) in.getStreamPosition() / (float) in.length();
                    fireStateChanged();
                }
            }
            
            if (ntot_withmasses > 0) {
                skipInt(in, 1);
            }

            if (header_npartC[0] > 0) {
                skipInt(in, 1);
                if (readMask[4]) { //energy
                    buf = new float[bufSize];                
                    int rest = header_npart[0] % bufSize;
                    for (int n = 0, off = 0, pc = 0; n < header_npart[0]; n += bufSize) {
                        int readLen = bufSize;
                        if (n + bufSize > header_npart[0]) {                            
                            readLen = rest;
                        }                                       
                        in.readFully(buf, 0, readLen);
                        for (int m = 0; m < bufSize; m += downsize, off += downsize) {
                            if(offs[pc] == off) {
                                u[pc] = buf[m];
                                pc++;
                            }                            
                        }
                    }
                    buf = null;
                } else {
                    in.skipBytes(Ngas * 4);
                }
                skipInt(in, 1);

                if (!silent) {
                    progress = 0.1f + 0.8f * (float) in.getStreamPosition() / (float) in.length();
                    fireStateChanged();
                }
            }
            in.close();
            //end read file

            if (!silent) {
                progress = 0.9f;
                fireStateChanged();
            }

            //calculate temperature
            if (readMask[6] && readMask[4]) {
                double BOLTZMANN = 1.3806e-16;
                double PROTONMASS = 1.6726e-24;
                double UnitLength_in_cm = 3.085678e21;
                double UnitMass_in_g = 1.989e43;
                double UnitVelocity_in_cm_per_s = 1.0e5;
                double UnitTime_in_s = UnitLength_in_cm / UnitVelocity_in_cm_per_s;
                double UnitEnergy_in_cgs = UnitMass_in_g * Math.pow(UnitLength_in_cm, 2) / Math.pow(UnitTime_in_s, 2);
                double Xh = 0.76;
                double gamma = 5.0 / 3;
                double MeanWeight, uu;
                for (int i = 0; i < dn_NgasC; i++) {
                    MeanWeight = 4.0 / (3 * Xh + 1 + 4 * Xh) * PROTONMASS;
                    uu = u[i] * UnitEnergy_in_cgs / UnitMass_in_g;
                    temp[i] = (float) (MeanWeight / BOLTZMANN * (gamma - 1) * uu);
                }
            }

            if (!silent) {
                System.out.println("done");
                progress = 0.95f;
                fireStateChanged();
                System.out.print("creating field...");
            }

            outField = new IrregularField();

            outField.setNSpace(3);
            outField.setNNodes(dn_NumPartC);
            outField.setCoords(pos);
            if (readMask[0]) {
                outField.addData(DataArray.create(vel, 3, "velocity"));
            }
            if (readMask[1]) {
                outField.addData(DataArray.create(id, 1, "ID"));
            }
            if (readMask[2]) {
                outField.addData(DataArray.create(type, 1, "type"));
            }
            if (readMask[3]) {
                outField.addData(DataArray.create(mass, 1, "mass"));
            }

            for (int k = 0, pc = 0; k < 6; k++) {
                if (header_npartC[k] == 0) {
                    continue;
                }
                int[] cells = new int[header_npartC[k] / downsize];
                boolean[] orient = new boolean[header_npartC[k] / downsize];
                for (int i = 0; i < cells.length; i++, pc++) {
                    cells[i] = pc;
                    orient[i] = true;
                }
                CellArray ca = new CellArray(Cell.POINT, cells, orient, null);
                String csName = "cs_";
                switch (k) {
                    case 0:
                        csName += "Gas";
                        break;
                    case 1:
                        csName += "Halo";
                        break;
                    case 2:
                        csName += "Disk";
                        break;
                    case 3:
                        csName += "Bulge";
                        break;
                    case 4:
                        csName += "Stars";
                        break;
                    case 5:
                        csName += "Bndry";
                        break;
                }
                CellSet cs = new CellSet(csName);
                cs.setCellArray(ca);
                if (k == 0 && dn_NgasC > 0) {
                    if (readMask[4]) {
                        cs.addData(DataArray.create(u, 1, "energy"));
                    }
//                    if (readMask[5]) {
//                        cs.addData(DataArray.create(rho, 1, "density"));
//                    }
                    if (readMask[6]) {
                        cs.addData(DataArray.create(temp, 1, "temperature"));
                    }
                }
                outField.addCellSet(cs);
            }
            
            //density field
            outDensityField = new RegularField(densityFieldDims);
            float[][] affine = new float[4][3];
            float[] binSize = new float[3];
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 3; j++) {
                    affine[i][j] = 0.0f;
                }
            }            
            
            if(header_BoxSize > 0) {
                for (int j = 0; j < 3; j++) {
                    binSize[j] = (float)header_BoxSize/(float)(densityFieldDims[j]-1);
                    affine[j][j] = binSize[j];
                }                
            } else {
                float[][] ext = outField.getExtents();
                for (int j = 0; j < 3; j++) {
                    binSize[j] = (float)(ext[1][j]-ext[0][j])/(float)(densityFieldDims[j]-1);
                    affine[j][j] = binSize[j];
                    affine[3][j] = ext[0][j];
                }                
            }
            float binVol = binSize[0]*binSize[1]*binSize[2];
            
            
            //density not using masses
            int[] v = new int[3];
            for (int i = 0; i < dn_NumPartC; i++) {
                for (int j = 0; j < 3; j++) {
                    v[j] = (int)Math.floor((pos[3*i + j]-affine[3][j])/binSize[j]);
                    if(v[j] < 0) v[j] = 0;
                    if(v[j] >= densityFieldDims[j]) v[j] = densityFieldDims[j]-1;
                }
                densityData[v[2]*densityFieldDims[1]*densityFieldDims[0] + v[1]*densityFieldDims[0] + v[0]]++;
            }
            if(logDensityField) 
                for (int i = 0; i < densityData.length; i++) {                    
                    densityData[i] = (float)Math.log(LOG_CONSTANT + densityData[i]);
                    //densityData[i] = (float)Math.log(LOG_CONSTANT + densityData[i]/binVol);
                }
            outDensityField.setAffine(affine);
            outDensityField.addData(DataArray.create(densityData, 1, "density"));
            
            
            if (!silent) {
                progress = 1.0f;
                fireStateChanged();
                System.out.println("done");
            }
        } catch (Exception ex) {
            System.err.println("ERROR!");
            ex.printStackTrace();
            return false;
        } catch (OutOfMemoryError err) {
            System.err.println("ERROR!");
            System.err.println("Out of memory!");
            return false;
        }

        field = outField;
        densityField = outDensityField;
        return true;
    }
    
    
    private long estimateMemory(int nFrames, int nPartsMax, int nPartsMaxGas, boolean[] readMask, int downsize, int[] densityFieldDims) {
        //positions
        int localNPartsMax = nPartsMax / downsize;
        int localNPartsMaxGas = nPartsMaxGas / downsize;

        long mem = 0;        
        if (nFrames > 1) {

            mem = nFrames * localNPartsMax * 3 * 4;
            mem += localNPartsMax * 3 * 4; //for tmp

            if (readMask[0]) { //velocity
                mem += localNPartsMax * 3 * 4;
            }

            //ID
            mem += 3 * localNPartsMax * 4; //for store, tmp and addressing

            if (readMask[2]) { //type
                mem += localNPartsMax * 4;
            }

            if (readMask[3]) { //mass
                mem += localNPartsMax * 4;
            }

            if (readMask[4]) { //energy
                mem += localNPartsMaxGas * 4;
            }
//            if (readMask[5]) { //density
//                mem += localNPartsMaxGas * 4;
//            }
            if (readMask[6]) { //tmperature
                mem += localNPartsMaxGas * 4;
            }

            //memory for cellsets
            mem += localNPartsMax * 4;
            mem += localNPartsMax;

            //memory fo validity mask
            mem += nFrames * localNPartsMax;

            mem += densityFieldDims[0] * densityFieldDims[1] * densityFieldDims[2] * 4;
        } else {
            //nFrames == 1
            mem = localNPartsMax * 3 * 4; //pos
            if (readMask[0]) { //velocity
                mem += localNPartsMax * 3 * 4;
            }
            if (readMask[1]) { //id
                mem += localNPartsMax * 4;
            }
            if (readMask[2]) { //type
                mem += localNPartsMax * 4;
            }
            if (readMask[3]) { //mass
                mem += localNPartsMax * 4;
            }

            if (readMask[4]) { //energy
                mem += localNPartsMaxGas * 4;
            }
            if (readMask[5]) { //density
                mem += localNPartsMaxGas * 4;
            }
            if (readMask[6]) { //temperature
                mem += localNPartsMaxGas * 4;
            }

            //memory for cellsets
            mem += localNPartsMax * 4;
            mem += localNPartsMax;

            mem += densityFieldDims[0] * densityFieldDims[1] * densityFieldDims[2] * 4;
        }
        
        mem += 4*3*BUFFER_SIZE;

        return mem;
    }

    private static void skipInt(FileImageInputStream in, int n) throws IOException {
        in.skipBytes(n * 4);
    }

    private class FrameReaderFullThread extends Thread {

        int iThread;
        int nThreads;
        int nFrames;
        int iPartsMax;
        int nPartsMax;
        int[] nParts;
        GadgetFileHeader[] headers;
        int[] ids;
        boolean[] valid;
        float[] pos;
        int downsize;

        public FrameReaderFullThread(int iThread, int nThreads, int nFrames, int iPartsMax, int nPartsMax, int[] nParts, int downsize, GadgetFileHeader[] headers, int[] ids, boolean[] valid, float[] pos) {
            this.iThread = iThread;
            this.nThreads = nThreads;
            this.nFrames = nFrames;
            this.iPartsMax = iPartsMax;
            this.nPartsMax = nPartsMax;
            this.nParts = nParts;
            this.headers = headers;
            this.ids = ids;
            this.valid = valid;
            this.pos = pos;
            this.downsize = downsize;
        }

        @Override
        public void run() {
            int[] idTmp;
            float[] posTmp;
            for (int n = iThread, c = 0; n < nFrames; n += nThreads) {
                c++;
                if (n == iPartsMax) {
                    continue;
                }

                if (downsize == 1) {
                    posTmp = new float[3 * nParts[n]];
                    idTmp = new int[nParts[n]];
                    if (!readGadgetFullPositionsIds(headers[n], posTmp, idTmp)) {
                        System.err.println("ERROR: error reading data from file " + headers[n].getFilePath());
                        return;
                    }

                    boolean copy = false;
                    if (idTmp.length == ids.length) {
                        copy = checkId(ids, idTmp);
                    }

                    if (copy) {
                        System.out.println("frame " + n + " copy geometry");
                        System.arraycopy(posTmp, 0, pos, n * nPartsMax * 3, posTmp.length);
                        for (int i = 0; i < idTmp.length; i++) {
                            valid[n * nPartsMax + i] = true;
                        }
                    } else {
                        //different particle configuration readdress
                        for (int i = 0; i < idTmp.length; i++) {
                            int id = -1;
                            for (int k = 0; k < ids.length; k++) {
                                if (ids[k] == idTmp[i]) {
                                    id = k;
                                    break;
                                }
                            }
                            if (id == -1) {
                                System.out.println("WARNING id from file " + headers[i].getFilePath() + " not found in file " + headers[iPartsMax].getFilePath());
                                continue;
                            }
                            for (int m = 0; m < 3; m++) {
                                pos[n * nPartsMax * 3 + 3 * id + m] = posTmp[3 * i + m];
                            }
                            valid[n * nPartsMax + id] = true;
                        }
                    }

                } else {
                    //with downsize
                    posTmp = new float[3 * nParts[n]];
                    idTmp = new int[nParts[n]];
                    if (!readGadgetFullDownsizedPositionsIds(headers[n], posTmp, idTmp, downsize, ids)) {
                        System.err.println("ERROR: error reading data from file " + headers[n].getFilePath());
                        return;
                    }

                    System.out.println("frame " + n + " copy geometry");
                    System.arraycopy(posTmp, 0, pos, n * nPartsMax * 3, posTmp.length);
                    for (int i = 0; i < idTmp.length; i++) {
                        valid[n * nPartsMax + i] = (idTmp[i] != -1);
                    }
                }

                if (iThread == 0) {
                    progress = (c + 1) * 0.9f / (float) (nFrames / nThreads);
                    fireStateChanged();
                }
            }

        }

        private boolean checkId(int[] id1, int[] id2) {
            if (id1 == null || id2 == null) {
                return false;
            }
            if (id1.length != id2.length) {
                return false;
            }

            for (int i = 0; i < id2.length; i++) {
                if (id1[i] != id2[i]) {
                    return false;
                }
            }
            return true;
        }

        private boolean readGadgetFullPositionsIds(GadgetFileHeader header, float[] pos, int[] id) {
            if (pos == null || id == null || header == null) {
                return false;
            }

            File f = new File(header.getFilePath());

            FileImageInputStream in;
            try {
                int[] header_npart = header.getNpart();
                int NumPart = 0;
                for (int k = 0; k < 6; k++) {
                    NumPart += header_npart[k];
                }

                in = new FileImageInputStream(f);
                in.setByteOrder(header.getEndian());

                skipInt(in, 1);
                in.skipBytes(256); //skip header
                skipInt(in, 1);

                skipInt(in, 1);
                in.readFully(pos, 0, 3 * NumPart); //read positions
                skipInt(in, 1);

                skipInt(in, 1);
                in.skipBytes(4 * 3 * NumPart); //skip velocities
                skipInt(in, 1);

                skipInt(in, 1);
                in.readFully(id, 0, NumPart);  //read ID
                skipInt(in, 1);
                in.close();
            } catch (IOException ex) {
                return false;
            }
            return true;
        }

        private boolean readGadgetFullDownsizedPositionsIds(GadgetFileHeader header, float[] pos, int[] id, int downsize, int[] refIds) {
            if (pos == null || id == null || header == null || refIds == null) {
                return false;
            }

            File f = new File(header.getFilePath());

            FileImageInputStream inIds;
            FileImageInputStream inPos;
            try {
                int[] header_npart = header.getNpart();
                int NumPart = 0;
                for (int k = 0; k < 6; k++) {
                    NumPart += header_npart[k];
                }

                if (refIds.length != NumPart / downsize) {
                    return false;
                }

                for (int i = 0; i < id.length; i++) {
                    id[i] = -1;
                    pos[3 * i] = 0;
                    pos[3 * i + 1] = 0;
                    pos[3 * i + 2] = 0;
                }

                inIds = new FileImageInputStream(f);
                inIds.setByteOrder(header.getEndian());
                inPos = new FileImageInputStream(f);
                inPos.setByteOrder(header.getEndian());


                //setup inIds
                skipInt(inIds, 1);
                inIds.skipBytes(256); //skip header
                skipInt(inIds, 1);
                skipInt(inIds, 1);
                inIds.skipBytes(4 * 3 * NumPart); //skip positions
                skipInt(inIds, 1);
                skipInt(inIds, 1);
                inIds.skipBytes(4 * 3 * NumPart); //skip velocities
                skipInt(inIds, 1);
                skipInt(inIds, 1);

                //setup inPos
                skipInt(inPos, 1);
                inPos.skipBytes(256); //skip header
                skipInt(inPos, 1);
                skipInt(inPos, 1);


                int c = 0;
                for (int i = 0; i < NumPart; i++) {
                    int v = inIds.readInt();
                    boolean found = false;
                    for (int j = 0; j < refIds.length; j++) {
                        if (refIds[j] == v) {
                            found = true;
                            break;
                        }
                    }

                    if (found) {
                        id[c] = v;
                        inPos.readFully(pos, 3 * c, 3);
                        c++;
                        if (c == refIds.length) {
                            break;
                        }
                    } else {
                        inPos.skipBytes(4 * 3);
                    }

                }

                inIds.close();
                inPos.close();
            } catch (IOException ex) {
                return false;
            }
            return true;
        }
    }
}
