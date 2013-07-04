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

package pl.edu.icm.visnow.lib.basic.readers.medreaders.ReadDICOM;

import com.pixelmed.dicom.*;
import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import javax.imageio.stream.FileImageInputStream;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.lib.gui.HistoArea;

/**
 *
 * @author Bartosz Borucki (babor@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class DicomReaderCoreMonochrome extends DicomReaderCore {
    private HistoArea histoArea = null;
    
    public DicomReaderCoreMonochrome(HistoArea histoArea) {
        this.histoArea = histoArea;
    }

    @Override
    public DataArray[] readDicomDataArray(ArrayList<DICOMSortingEntry> entries, int readAs, int lowCrop, int highCrop, int[] dims, int[] downsize, boolean interpolateMissingSlices, boolean withProgress, float progressModifier) throws IOException, DicomException {
        if (entries == null || entries.size() < 1 || dims == null) {
            return null;
        }

        String patientName = "dicom_data";

        AttributeList atl;
        Attribute att;
        int samplesPerPixel, width, height, depth, bitsAllocated;
        String photometricInterpretation = null;

        atl = entries.get(0).getHeader();

        att = atl.get(TagFromName.PhotometricInterpretation);
        if (att == null) {
            return null;
        }
        photometricInterpretation = att.getSingleStringValueOrNull();
        if (photometricInterpretation == null || (!photometricInterpretation.equals("MONOCHROME1") && !photometricInterpretation.equals("MONOCHROME2"))) {
            return null;
        }


        att = atl.get(TagFromName.SamplesPerPixel);
        samplesPerPixel = att.getSingleIntegerValueOrDefault(1);

        if (samplesPerPixel != 1) {
            System.err.println("DICOM ERROR: Wrong SamplesPerPixel for " + photometricInterpretation + "!");
            return null;
        }

        att = atl.get(TagFromName.Columns);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read Columns value!");
            return null;
        }
        width = att.getSingleIntegerValueOrDefault(0);
        if (width == 0) {
            System.err.println("DICOM ERROR: Cannot read Columns value!");
            return null;
        }

        att = atl.get(TagFromName.Rows);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read Rows value!");
            return null;
        }
        height = att.getSingleIntegerValueOrDefault(0);
        if (height == 0) {
            System.err.println("DICOM ERROR: Cannot read Rows value!");
            return null;
        }

        depth = 1;
        att = atl.get(TagFromName.NumberOfFrames);
        if(att != null)
            depth = att.getSingleIntegerValueOrDefault(1);
        
        if ((int) Math.ceil((double)width / (double)downsize[0]) != dims[0] || (int) Math.ceil((double)height / (double)downsize[1]) != dims[1] || (depth > 1 && (int) Math.ceil((double)depth / (double)downsize[2]) != dims[2])) {
            System.err.println("ERROR: DICOM file not compatible with selected set!");
            return null;
        }


        att = atl.get(TagFromName.BitsAllocated);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read BitsAllocated!");
            return null;
        }
        bitsAllocated = att.getSingleIntegerValueOrDefault(8);

        att = atl.get(TagFromName.PatientName);
        if (att != null) {
            patientName = att.getSingleStringValueOrDefault("dicom_data");
        }
        patientName = patientName.replaceAll("^", "");
        if (patientName.startsWith("0")
                || patientName.startsWith("1")
                || patientName.startsWith("2")
                || patientName.startsWith("3")
                || patientName.startsWith("4")
                || patientName.startsWith("5")
                || patientName.startsWith("6")
                || patientName.startsWith("7")
                || patientName.startsWith("8")
                || patientName.startsWith("9")) {
            patientName = "_" + patientName;
        }

        int windowLowCrop = 0;
        int windowHighCrop = 1024;
        boolean windowFound = false;
        int windowCenter = 512, windowWidth = 1024;
        if (readAs == Params.READ_AS_WINDOW) {
            att = atl.get(TagFromName.WindowCenter);
            if (att != null) {
                windowCenter = att.getSingleIntegerValueOrDefault(512);
                att = atl.get(TagFromName.WindowWidth);
                if (att != null) {
                    windowWidth = att.getSingleIntegerValueOrDefault(1024);
                    windowFound = true;
                }
                windowLowCrop = windowCenter - (windowWidth / 2);
                windowHighCrop = windowCenter + (windowWidth / 2);
            }

            lowCrop = windowLowCrop;
            highCrop = windowHighCrop;
        }

        int nSliceData = dims[0] * dims[1];
        int nSlices = 1;
        if (dims.length == 3) {
            nSlices = dims[2];
        }
        int nData = nSliceData * nSlices;
        int offset;

        switch (readAs) {
            case Params.READ_AS_AUTO:
            case Params.READ_AS_HISTOGRAM:
                if (readAs == Params.READ_AS_HISTOGRAM) {
                    System.out.println("DICOM reader: reading " + nSlices + " slice(s) equalizing histogram to bytes discarding vals under " + lowCrop);
                } else {
                    System.out.println("DICOM reader: reading " + nSlices + " slice(s) with auto data detection");
                }
                if (bitsAllocated == 8) {
                    byte[] bData = new byte[nData];
                    int bgValueGlobal = 0;

                    for (int i = 0, i2 = 0; i < entries.size(); i += downsize[2], i2++) {
                        if (entries.get(i) == null) {
                            continue;
                        }
                        offset = i2 * nSliceData;

                        int bgValue = readDicomArrayOffsetMonochrome_8bit(entries.get(i), bData, offset, dims, downsize);
                        if (bgValue < bgValueGlobal) {
                            bgValueGlobal = bgValue;
                        }

                        if (bgValue == Integer.MAX_VALUE) {
                            entries.set(i, null);
                        }

                        if (withProgress) {
                            progress = (0.25f + (i + 1) * 0.75f / entries.size()) * progressModifier;
                            fireProgressChanged();
                        }
                    }

                    if (interpolateMissingSlices) {
                        for (int i = 0, i2 = 0; i < entries.size(); i += downsize[2], i2++) {
                            if (entries.get(i) != null) {
                                continue;
                            }

                            offset = i2 * nSliceData;

                            int prev = i;
                            while (prev > 0) {
                                prev -= downsize[2];
                                if (entries.get(prev) != null) {
                                    break;
                                }
                            }

                            int next = i;
                            while (next < entries.size() - 1) {
                                next += downsize[2];
                                if (entries.get(next) != null) {
                                    break;
                                }
                            }

                            if (prev < 0 || next >= entries.size()) {
                                for (int j = 0; j < nSliceData; j++) {
                                    bData[offset + j] = (byte) bgValueGlobal;
                                }
                                continue;
                            }

                            if (next == i + 1 && prev == i - 1) {
                                for (int j = 0; j < nSliceData; j++) {
                                    bData[offset + j] = (byte) Math.round(((float) (bData[offset - nSliceData + j] & 0xff) + (float) (bData[offset + nSliceData + j] & 0xff)) / 2.0f);
                                }
                            } else {
                                float prevW, nextW;
                                int prevStep = (i - prev) / downsize[2];
                                int nextStep = (next - i) / downsize[2];
                                prevW = 1.0f / (float) prevStep;
                                nextW = 1.0f / (float) nextStep;
                                for (int j = 0; j < nSliceData; j++) {
                                    bData[offset + j] = (byte) Math.round(((float) (bData[offset - prevStep * nSliceData + j] & 0xff) * prevW + (float) (bData[offset + nextStep * nSliceData + j] & 0xff) * nextW) / (nextW + prevW));
                                }
                            }
                        }
                    } else {
                        for (int i = 0, i2 = 0; i < entries.size(); i += downsize[2], i2++) {
                            if (entries.get(i) != null) {
                                continue;
                            }

                            offset = i2 * nSliceData;
                            for (int j = 0; j < nSliceData; j++) {
                                bData[offset + j] = (byte) bgValueGlobal;
                            }
                        }
                    }


                    if (readAs == Params.READ_AS_HISTOGRAM) {
                        int low = lowCrop - bgValueGlobal;
                        long[] histogram = new long[256];
                        for (int i = 0; i < histogram.length; i++) {
                            histogram[i] = 0;
                        }
                        for (int i = 0; i < bData.length; i++) {
                            int v = (int) (bData[i] & 0xff) - bgValueGlobal;
                            if (v >= low && v >= 0 && v < histogram.length) {
                                histogram[v] += 1;
                            }
                        }
                        double s = 0, d = 0;
                        int k = 1;
                        d = 0;
                        for (int i = 1; i < histogram.length; i++) {
                            histogram[i] = (long) Math.sqrt(histogram[i]);
                            d += histogram[i];
                        }
                        if (histoArea != null) {
                            histoArea.setHisto(histogram);
                        }
                        s = 0;
                        k = 0;
                        for (int i = 1; i < histogram.length; i++) {
                            s += histogram[i];
                            if (s > k * d / 256) {
                                k += 1;
                            }
                            histogram[i] = k - 1;
                        }

                        for (int i = 0; i < bData.length; i++) {
                            int v = (int) (bData[i] & 0xff) - bgValueGlobal;
                            if (v <= low) {
                                bData[i] = 0;
                            } else {
                                if (v >= histogram.length) {
                                    v = histogram.length - 1;
                                }
                                int b = (int) histogram[v];
                                if (b > 255) {
                                    b = 255;
                                }
                                bData[i] = (byte) (0xff & b);
                            }
                        }
                    }

                    DataArray[] out = new DataArray[1];
                    out[0] = DataArray.create(bData, 1, patientName);
                    return out;
                } else if (bitsAllocated == 16) {
                    short[] sData = new short[nData];
                    int bgValueGlobal = 0;

                    for (int i = 0, i2 = 0; i < entries.size(); i += downsize[2], i2++) {
                        if (entries.get(i) == null) {
                            continue;
                        }
                        offset = i2 * nSliceData;

                        int bgValue = readDicomArrayOffsetMonochrome_16bit(entries.get(i), sData, offset, dims, downsize);
                        if (bgValue < bgValueGlobal) {
                            bgValueGlobal = bgValue;
                        }

                        if (bgValue == Integer.MAX_VALUE) {
                            entries.set(i, null);
                        }

                        if (withProgress) {
                            progress = (0.25f + (i + 1) * 0.75f / entries.size()) * progressModifier;
                            fireProgressChanged();
                        }
                    }

                    if (interpolateMissingSlices) {
                        for (int i = 0, i2 = 0; i < entries.size(); i += downsize[2], i2++) {
                            if (entries.get(i) != null) {
                                continue;
                            }

                            offset = i2 * nSliceData;

                            int prev = i;
                            while (prev > 0) {
                                prev -= downsize[2];
                                if (entries.get(prev) != null) {
                                    break;
                                }
                            }

                            int next = i;
                            while (next < entries.size() - 1) {
                                next += downsize[2];
                                if (entries.get(next) != null) {
                                    break;
                                }
                            }

                            if (prev < 0 || next >= entries.size()) {
                                for (int j = 0; j < nSliceData; j++) {
                                    sData[offset + j] = (short) bgValueGlobal;
                                }
                                continue;
                            }

                            if (next == i + 1 && prev == i - 1) {
                                for (int j = 0; j < nSliceData; j++) {
                                    sData[offset + j] = (short) Math.round(((float) sData[offset - nSliceData + j] + (float) sData[offset + nSliceData + j]) / 2.0f);
                                }
                            } else {
                                float prevW, nextW;
                                int prevStep = (i - prev) / downsize[2];
                                int nextStep = (next - i) / downsize[2];
                                prevW = 1.0f / (float) prevStep;
                                nextW = 1.0f / (float) nextStep;
                                for (int j = 0; j < nSliceData; j++) {
                                    sData[offset + j] = (short) Math.round(((float) sData[offset - prevStep * nSliceData + j] * prevW + (float) sData[offset + nextStep * nSliceData + j] * nextW) / (nextW + prevW));
                                }
                            }
                        }
                    } else {
                        for (int i = 0, i2 = 0; i < entries.size(); i += downsize[2], i2++) {
                            if (entries.get(i) != null) {
                                continue;
                            }

                            offset = i2 * nSliceData;
                            for (int j = 0; j < nSliceData; j++) {
                                sData[offset + j] = (short) bgValueGlobal;
                            }
                        }
                    }

                    if (readAs == Params.READ_AS_HISTOGRAM) {
                        byte[] bData = new byte[sData.length];
                        int low = lowCrop - bgValueGlobal;
                        long[] histogram = new long[4096];
                        for (int i = 0; i < histogram.length; i++) {
                            histogram[i] = 0;
                        }
                        for (int i = 0; i < sData.length; i++) {
                            int v = sData[i] - bgValueGlobal;
                            if (v >= low && v >= 0 && v < histogram.length) {
                                histogram[v] += 1;
                            }
                        }
                        double s = 0, d = 0;
                        int k = 1;
                        d = 0;
                        for (int i = 1; i < histogram.length; i++) {
                            histogram[i] = (long) Math.sqrt(histogram[i]);
                            d += histogram[i];
                        }
                        if (histoArea != null) {
                            histoArea.setHisto(histogram);
                        }
                        s = 0;
                        k = 0;
                        for (int i = 1; i < histogram.length; i++) {
                            s += histogram[i];
                            if (s > k * d / 256) {
                                k += 1;
                            }
                            histogram[i] = k - 1;
                        }

                        for (int i = 0; i < sData.length; i++) {
                            int v = sData[i] - bgValueGlobal;
                            if (v <= low) {
                                bData[i] = 0;
                            } else {
                                if (v >= histogram.length) {
                                    v = histogram.length - 1;
                                }
                                int b = (int) histogram[v];
                                if (b > 255) {
                                    b = 255;
                                }
                                bData[i] = (byte) (0xff & b);
                            }
                        }
                        sData = null;
                        System.gc();
                        DataArray[] out = new DataArray[1];
                        out[0] = DataArray.create(bData, 1, patientName);
                        return out;
                    }

                    DataArray[] out = new DataArray[1];
                    out[0] = DataArray.create(sData, 1, patientName);
                    return out;
                } else if (bitsAllocated == 32) {
                    int[] iData = new int[nData];
                    int bgValueGlobal = 0;

                    for (int i = 0, i2 = 0; i < entries.size(); i += downsize[2], i2++) {
                        if (entries.get(i) == null) {
                            continue;
                        }
                        offset = i2 * nSliceData;

                        int bgValue = readDicomArrayOffsetMonochrome_32bit(entries.get(i), iData, offset, dims, downsize);
                        if (bgValue < bgValueGlobal) {
                            bgValueGlobal = bgValue;
                        }

                        if (bgValue == Integer.MAX_VALUE) {
                            entries.set(i, null);
                        }

                        if (withProgress) {
                            progress = (0.25f + (i + 1) * 0.75f / entries.size()) * progressModifier;
                            fireProgressChanged();
                        }
                    }

                    if (interpolateMissingSlices) {
                        for (int i = 0, i2 = 0; i < entries.size(); i += downsize[2], i2++) {
                            if (entries.get(i) != null) {
                                continue;
                            }

                            offset = i2 * nSliceData;

                            int prev = i;
                            while (prev > 0) {
                                prev -= downsize[2];
                                if (entries.get(prev) != null) {
                                    break;
                                }
                            }

                            int next = i;
                            while (next < entries.size() - 1) {
                                next += downsize[2];
                                if (entries.get(next) != null) {
                                    break;
                                }
                            }

                            if (prev < 0 || next >= entries.size()) {
                                for (int j = 0; j < nSliceData; j++) {
                                    iData[offset + j] = bgValueGlobal;
                                }
                                continue;
                            }

                            if (next == i + 1 && prev == i - 1) {
                                for (int j = 0; j < nSliceData; j++) {
                                    iData[offset + j] = (int) Math.round(((float) iData[offset - nSliceData + j] + (float) iData[offset + nSliceData + j]) / 2.0f);
                                }
                            } else {
                                float prevW, nextW;
                                int prevStep = (i - prev) / downsize[2];
                                int nextStep = (next - i) / downsize[2];
                                prevW = 1.0f / (float) prevStep;
                                nextW = 1.0f / (float) nextStep;
                                for (int j = 0; j < nSliceData; j++) {
                                    iData[offset + j] = (int) Math.round(((float) iData[offset - prevStep * nSliceData + j] * prevW + (float) iData[offset + nextStep * nSliceData + j] * nextW) / (nextW + prevW));
                                }
                            }
                        }
                    } else {
                        for (int i = 0, i2 = 0; i < entries.size(); i += downsize[2], i2++) {
                            if (entries.get(i) != null) {
                                continue;
                            }

                            offset = i2 * nSliceData;
                            for (int j = 0; j < nSliceData; j++) {
                                iData[offset + j] = bgValueGlobal;
                            }
                        }
                    }

                    if (readAs == Params.READ_AS_HISTOGRAM) {
                        byte[] bData = new byte[iData.length];
                        int low = lowCrop - bgValueGlobal;
                        long[] histogram = new long[2048];
                        for (int i = 0; i < histogram.length; i++) {
                            histogram[i] = 0;
                        }
                        for (int i = 0; i < iData.length; i++) {
                            int v = iData[i] - bgValueGlobal;
                            if (v >= low && v >= 0 && v < histogram.length) {
                                histogram[v] += 1;
                            }
                        }
                        double s = 0, d = 0;
                        int k = 1;
                        d = 0;
                        for (int i = 1; i < histogram.length; i++) {
                            histogram[i] = (long) Math.sqrt(histogram[i]);
                            d += histogram[i];
                        }
                        if (histoArea != null) {
                            histoArea.setHisto(histogram);
                        }
                        s = 0;
                        k = 0;
                        for (int i = 1; i < histogram.length; i++) {
                            s += histogram[i];
                            if (s > k * d / 256) {
                                k += 1;
                            }
                            histogram[i] = k - 1;
                        }

                        for (int i = 0; i < iData.length; i++) {
                            int v = iData[i] - bgValueGlobal;
                            if (v <= low) {
                                bData[i] = 0;
                            } else {
                                if (v >= histogram.length) {
                                    v = histogram.length - 1;
                                }
                                int b = (int) histogram[v];
                                if (b > 255) {
                                    b = 255;
                                }
                                bData[i] = (byte) (0xff & b);
                            }
                        }
                        iData = null;
                        System.gc();
                        DataArray[] out = new DataArray[1];
                        out[0] = DataArray.create(bData, 1, patientName);
                        return out;
                    }

                    DataArray[] out = new DataArray[1];
                    out[0] = DataArray.create(iData, 1, patientName);
                    return out;
                }
            case Params.READ_AS_BYTES:
            case Params.READ_AS_WINDOW:
                if (readAs == Params.READ_AS_WINDOW) {
                    if (windowFound) {
                        System.out.println("DICOM reader: reading " + nSlices + " slice(s) with DICOM embeded window " + windowCenter + "/" + windowWidth + " normalized to bytes");
                    } else {
                        System.out.println("DICOM reader: no DICOM embeded window found - reading " + nSlices + " slice(s) with byte data normalized to <" + lowCrop + " " + highCrop + ">");
                    }
                } else {
                    System.out.println("DICOM reader: reading " + nSlices + " slice(s) with byte data normalized to <" + lowCrop + " " + highCrop + ">");
                }


                byte[] bData = new byte[nData];
                int bgValueGlobal = 0;

                for (int i = 0, i2 = 0; i < entries.size(); i += downsize[2], i2++) {
                    if (entries.get(i) == null) {
                        continue;
                    }
                    offset = i2 * nSliceData;
                    int bgValue = readDicomArrayOffsetMonochrome_Cropped(entries.get(i), bData, offset, dims, downsize, lowCrop, highCrop, false);
                    if (bgValue < bgValueGlobal) {
                        bgValueGlobal = bgValue;
                    }

                    if (bgValue == Integer.MAX_VALUE) {
                        entries.set(i, null);
                    }

                    if (withProgress) {
                        progress = (0.25f + (i + 1) * 0.75f / entries.size()) * progressModifier;
                        fireProgressChanged();
                    }
                }

                if (interpolateMissingSlices) {
                    for (int i = 0, i2 = 0; i < entries.size(); i += downsize[2], i2++) {
                        if (entries.get(i) != null) {
                            continue;
                        }

                        offset = i2 * nSliceData;

                        int prev = i;
                        while (prev > 0) {
                            prev -= downsize[2];
                            if (entries.get(prev) != null) {
                                break;
                            }
                        }

                        int next = i;
                        while (next < entries.size() - 1) {
                            next += downsize[2];
                            if (entries.get(next) != null) {
                                break;
                            }
                        }

                        if (prev < 0 || next >= entries.size()) {
                            for (int j = 0; j < nSliceData; j++) {
                                bData[offset + j] = (byte) bgValueGlobal;
                            }
                            continue;
                        }

                        if (next == i + 1 && prev == i - 1) {
                            for (int j = 0; j < nSliceData; j++) {
                                bData[offset + j] = (byte) Math.round(((float) (bData[offset - nSliceData + j] & 0xff) + (float) (bData[offset + nSliceData + j] & 0xff)) / 2.0f);
                            }
                        } else {
                            float prevW, nextW;
                            int prevStep = (i - prev) / downsize[2];
                            int nextStep = (next - i) / downsize[2];
                            prevW = 1.0f / (float) prevStep;
                            nextW = 1.0f / (float) nextStep;
                            for (int j = 0; j < nSliceData; j++) {
                                bData[offset + j] = (byte) Math.round(((float) (bData[offset - prevStep * nSliceData + j] & 0xff) * prevW + (float) (bData[offset + nextStep * nSliceData + j] & 0xff) * nextW) / (nextW + prevW));
                            }
                        }
                    }
                } else {
                    for (int i = 0, i2 = 0; i < entries.size(); i += downsize[2], i2++) {
                        if (entries.get(i) != null) {
                            continue;
                        }

                        offset = i2 * nSliceData;
                        for (int j = 0; j < nSliceData; j++) {
                            bData[offset + j] = (byte) bgValueGlobal;
                        }
                    }
                }
                DataArray[] out = new DataArray[1];
                out[0] = DataArray.create(bData, 1, patientName);
                return out;
        }
        return null;
    }

    @Override    
    public DataArray[] readDicomDataArrayFrames(DICOMSortingEntry entry, int readAs, int lowCrop, int highCrop, int[] dims, int[] downsize, boolean withProgress, float progressModifier, boolean framesAsDim, int framesRangeLow, int framesRangeUp) throws IOException, DicomException {
        if (entry == null || dims == null) {
            return null;
        }

        String patientName = "dicom_data";

        AttributeList atl;
        Attribute att;
        int samplesPerPixel, width, height, nFrames, bitsAllocated;
        String photometricInterpretation = null;

        atl = entry.getHeader();

        att = atl.get(TagFromName.PhotometricInterpretation);
        if (att == null) {
            return null;
        }
        photometricInterpretation = att.getSingleStringValueOrNull();
        if (photometricInterpretation == null || (!photometricInterpretation.equals("MONOCHROME1") && !photometricInterpretation.equals("MONOCHROME2"))) {
            return null;
        }


        att = atl.get(TagFromName.SamplesPerPixel);
        samplesPerPixel = att.getSingleIntegerValueOrDefault(1);

        if (samplesPerPixel != 1) {
            System.err.println("DICOM ERROR: Wrong SamplesPerPixel for " + photometricInterpretation + "!");
            return null;
        }

        att = atl.get(TagFromName.Columns);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read Columns value!");
            return null;
        }
        width = att.getSingleIntegerValueOrDefault(0);
        if (width == 0) {
            System.err.println("DICOM ERROR: Cannot read Columns value!");
            return null;
        }

        att = atl.get(TagFromName.Rows);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read Rows value!");
            return null;
        }
        height = att.getSingleIntegerValueOrDefault(0);
        if (height == 0) {
            System.err.println("DICOM ERROR: Cannot read Rows value!");
            return null;
        }

        nFrames = 1;
        att = atl.get(TagFromName.NumberOfFrames);
        if(att != null)
            nFrames = att.getSingleIntegerValueOrDefault(1);
        
        if(framesAsDim && ((int) Math.ceil((double)width / (double)downsize[0]) != dims[0] || (int) Math.ceil((double)height / (double)downsize[1]) != dims[1] || (nFrames > 1 && (int) Math.ceil((double)nFrames / (double)downsize[2]) != dims[2]))) {
            System.err.println("ERROR: DICOM file not compatible with selected set!");
            return null;
        }
        
        if(!framesAsDim && (framesRangeLow < 0 || framesRangeUp < 0 || framesRangeLow >= nFrames || framesRangeUp >= nFrames || framesRangeLow > framesRangeUp)) {
            System.err.println("ERROR: wrong frames range!");
            return null;            
        }
        
        if(!framesAsDim && ((int) Math.ceil((double)width / (double)downsize[0]) != dims[0] || (int) Math.ceil((double)height / (double)downsize[1]) != dims[1])) {
            System.err.println("ERROR: DICOM file not compatible with selected set!");
            return null;
        }
        
        att = atl.get(TagFromName.BitsAllocated);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read BitsAllocated!");
            return null;
        }
        bitsAllocated = att.getSingleIntegerValueOrDefault(8);

        att = atl.get(TagFromName.PatientName);
        if (att != null) {
            patientName = att.getSingleStringValueOrDefault("dicom_data");
        }
        patientName = patientName.replaceAll("^", "");
        if (patientName.startsWith("0")
                || patientName.startsWith("1")
                || patientName.startsWith("2")
                || patientName.startsWith("3")
                || patientName.startsWith("4")
                || patientName.startsWith("5")
                || patientName.startsWith("6")
                || patientName.startsWith("7")
                || patientName.startsWith("8")
                || patientName.startsWith("9")) {
            patientName = "_" + patientName;
        }

        int windowLowCrop = 0;
        int windowHighCrop = 1024;
        boolean windowFound = false;
        int windowCenter = 512, windowWidth = 1024;
        if (readAs == Params.READ_AS_WINDOW) {
            att = atl.get(TagFromName.WindowCenter);
            if (att != null) {
                windowCenter = att.getSingleIntegerValueOrDefault(512);
                att = atl.get(TagFromName.WindowWidth);
                if (att != null) {
                    windowWidth = att.getSingleIntegerValueOrDefault(1024);
                    windowFound = true;
                }
                windowLowCrop = windowCenter - (windowWidth / 2);
                windowHighCrop = windowCenter + (windowWidth / 2);
            }

            lowCrop = windowLowCrop;
            highCrop = windowHighCrop;
        }

        int nData;
        if(framesAsDim) {
            nData = dims[0] * dims[1] * dims[2];
            switch (readAs) {
                case Params.READ_AS_AUTO:
                case Params.READ_AS_HISTOGRAM:
                    if (readAs == Params.READ_AS_HISTOGRAM) {
                        System.out.println("DICOM reader: reading volume equalizing histogram to bytes discarding vals under " + lowCrop);
                    } else {
                        System.out.println("DICOM reader: reading volume with auto data detection");
                    }
                    if (bitsAllocated == 8) {
                        byte[] bData = new byte[nData];
                        int bgValueGlobal = 0;

                        int bgValue = readDicomArrayMonochromeFrames_8bit(entry, bData, dims, downsize);
                        if (bgValue < bgValueGlobal) {
                            bgValueGlobal = bgValue;
                        }

                        if (withProgress) {
                            progress = progressModifier;
                            fireProgressChanged();
                        }


                        if (readAs == Params.READ_AS_HISTOGRAM) {
                            int low = lowCrop - bgValueGlobal;
                            long[] histogram = new long[256];
                            for (int i = 0; i < histogram.length; i++) {
                                histogram[i] = 0;
                            }
                            for (int i = 0; i < bData.length; i++) {
                                int v = (int) (bData[i] & 0xff) - bgValueGlobal;
                                if (v >= low && v >= 0 && v < histogram.length) {
                                    histogram[v] += 1;
                                }
                            }
                            double s = 0, d = 0;
                            int k = 1;
                            d = 0;
                            for (int i = 1; i < histogram.length; i++) {
                                histogram[i] = (long) Math.sqrt(histogram[i]);
                                d += histogram[i];
                            }
                            if (histoArea != null) {
                                histoArea.setHisto(histogram);
                            }
                            s = 0;
                            k = 0;
                            for (int i = 1; i < histogram.length; i++) {
                                s += histogram[i];
                                if (s > k * d / 256) {
                                    k += 1;
                                }
                                histogram[i] = k - 1;
                            }

                            for (int i = 0; i < bData.length; i++) {
                                int v = (int) (bData[i] & 0xff) - bgValueGlobal;
                                if (v <= low) {
                                    bData[i] = 0;
                                } else {
                                    if (v >= histogram.length) {
                                        v = histogram.length - 1;
                                    }
                                    int b = (int) histogram[v];
                                    if (b > 255) {
                                        b = 255;
                                    }
                                    bData[i] = (byte) (0xff & b);
                                }
                            }
                        }

                        DataArray[] out = new DataArray[1];
                        out[0] = DataArray.create(bData, 1, patientName);
                        return out;
                    } else if (bitsAllocated == 16) {
                        short[] sData = new short[nData];
                        int bgValueGlobal = 0;

                        int bgValue = readDicomArrayMonochromeFrames_16bit(entry, sData, dims, downsize);
                        if (bgValue < bgValueGlobal) {
                            bgValueGlobal = bgValue;
                        }

                        if (withProgress) {
                            progress = progressModifier;
                            fireProgressChanged();
                        }

                        if (readAs == Params.READ_AS_HISTOGRAM) {
                            byte[] bData = new byte[sData.length];
                            int low = lowCrop - bgValueGlobal;
                            long[] histogram = new long[4096];
                            for (int i = 0; i < histogram.length; i++) {
                                histogram[i] = 0;
                            }
                            for (int i = 0; i < sData.length; i++) {
                                int v = sData[i] - bgValueGlobal;
                                if (v >= low && v >= 0 && v < histogram.length) {
                                    histogram[v] += 1;
                                }
                            }
                            double s = 0, d = 0;
                            int k = 1;
                            d = 0;
                            for (int i = 1; i < histogram.length; i++) {
                                histogram[i] = (long) Math.sqrt(histogram[i]);
                                d += histogram[i];
                            }
                            if (histoArea != null) {
                                histoArea.setHisto(histogram);
                            }
                            s = 0;
                            k = 0;
                            for (int i = 1; i < histogram.length; i++) {
                                s += histogram[i];
                                if (s > k * d / 256) {
                                    k += 1;
                                }
                                histogram[i] = k - 1;
                            }

                            for (int i = 0; i < sData.length; i++) {
                                int v = sData[i] - bgValueGlobal;
                                if (v <= low) {
                                    bData[i] = 0;
                                } else {
                                    if (v >= histogram.length) {
                                        v = histogram.length - 1;
                                    }
                                    int b = (int) histogram[v];
                                    if (b > 255) {
                                        b = 255;
                                    }
                                    bData[i] = (byte) (0xff & b);
                                }
                            }
                            sData = null;
                            System.gc();
                            DataArray[] out = new DataArray[1];
                            out[0] = DataArray.create(bData, 1, patientName);
                            return out;
                        }

                        DataArray[] out = new DataArray[1];
                        out[0] = DataArray.create(sData, 1, patientName);
                        return out;
                    } else if (bitsAllocated == 32) {
                        int[] iData = new int[nData];
                        int bgValueGlobal = 0;

                        int bgValue = readDicomArrayMonochromeFrames_32bit(entry, iData, dims, downsize);
                        if (bgValue < bgValueGlobal) {
                            bgValueGlobal = bgValue;
                        }

                        if (withProgress) {
                            progress = progressModifier;
                            fireProgressChanged();
                        }

                        if (readAs == Params.READ_AS_HISTOGRAM) {
                            byte[] bData = new byte[iData.length];
                            int low = lowCrop - bgValueGlobal;
                            long[] histogram = new long[2048];
                            for (int i = 0; i < histogram.length; i++) {
                                histogram[i] = 0;
                            }
                            for (int i = 0; i < iData.length; i++) {
                                int v = iData[i] - bgValueGlobal;
                                if (v >= low && v >= 0 && v < histogram.length) {
                                    histogram[v] += 1;
                                }
                            }
                            double s = 0, d = 0;
                            int k = 1;
                            d = 0;
                            for (int i = 1; i < histogram.length; i++) {
                                histogram[i] = (long) Math.sqrt(histogram[i]);
                                d += histogram[i];
                            }
                            if (histoArea != null) {
                                histoArea.setHisto(histogram);
                            }
                            s = 0;
                            k = 0;
                            for (int i = 1; i < histogram.length; i++) {
                                s += histogram[i];
                                if (s > k * d / 256) {
                                    k += 1;
                                }
                                histogram[i] = k - 1;
                            }

                            for (int i = 0; i < iData.length; i++) {
                                int v = iData[i] - bgValueGlobal;
                                if (v <= low) {
                                    bData[i] = 0;
                                } else {
                                    if (v >= histogram.length) {
                                        v = histogram.length - 1;
                                    }
                                    int b = (int) histogram[v];
                                    if (b > 255) {
                                        b = 255;
                                    }
                                    bData[i] = (byte) (0xff & b);
                                }
                            }
                            iData = null;
                            System.gc();
                            DataArray[] out = new DataArray[1];
                            out[0] = DataArray.create(bData, 1, patientName);
                            return out;
                        }

                        DataArray[] out = new DataArray[1];
                        out[0] = DataArray.create(iData, 1, patientName);
                        return out;
                    }
                case Params.READ_AS_BYTES:
                case Params.READ_AS_WINDOW:
                    if (readAs == Params.READ_AS_WINDOW) {
                        if (windowFound) {
                            System.out.println("DICOM reader: reading volume with DICOM embeded window " + windowCenter + "/" + windowWidth + " normalized to bytes");
                        } else {
                            System.out.println("DICOM reader: no DICOM embeded window found - reading volume with byte data normalized to <" + lowCrop + " " + highCrop + ">");
                        }
                    } else {
                        System.out.println("DICOM reader: reading volume with byte data normalized to <" + lowCrop + " " + highCrop + ">");
                    }


                    byte[] bData = new byte[nData];
                    int bgValue = readDicomArrayMonochromeFrames_Cropped(entry, bData, dims, downsize, lowCrop, highCrop, false);

                    if (withProgress) {
                        progress = progressModifier;
                        fireProgressChanged();
                    }
                    DataArray[] out = new DataArray[1];
                    out[0] = DataArray.create(bData, 1, patientName);
                    return out;
            }
        } else {
            //read frames as time
            nData = dims[0] * dims[1];
            nFrames = framesRangeUp - framesRangeLow + 1;
            switch (readAs) {
                case Params.READ_AS_AUTO:
                case Params.READ_AS_HISTOGRAM:
                    if (readAs == Params.READ_AS_HISTOGRAM) {
                        System.out.println("DICOM reader: reading frames equalizing histogram to bytes discarding vals under " + lowCrop);
                    } else {
                        System.out.println("DICOM reader: reading frames with auto data detection");
                    }
                    if (bitsAllocated == 8) {
                        byte[][] bData = new byte[nFrames][nData];
                        int bgValueGlobal = 0;

                        int bgValue = readDicomArrayMonochromeFrames_8bit(entry, bData, dims, downsize, framesRangeLow, framesRangeUp);
                        if (bgValue < bgValueGlobal) {
                            bgValueGlobal = bgValue;
                        }

                        if (withProgress) {
                            progress = progressModifier;
                            fireProgressChanged();
                        }


                        if (readAs == Params.READ_AS_HISTOGRAM) {
                            int low = lowCrop - bgValueGlobal;
                            long[] histogram = new long[256];
                            for (int i = 0; i < histogram.length; i++) {
                                histogram[i] = 0;
                            }
                            for (int n = 0; n < nFrames; n++) {
                                for (int i = 0; i < bData[n].length; i++) {
                                    int v = (int) (bData[n][i] & 0xff) - bgValueGlobal;
                                    if (v >= low && v >= 0 && v < histogram.length) {
                                        histogram[v] += 1;
                                    }
                                }
                            }
                            double s = 0, d = 0;
                            int k = 1;
                            d = 0;
                            for (int i = 1; i < histogram.length; i++) {
                                histogram[i] = (long) Math.sqrt(histogram[i]);
                                d += histogram[i];
                            }
                            if (histoArea != null) {
                                histoArea.setHisto(histogram);
                            }
                            s = 0;
                            k = 0;
                            for (int i = 1; i < histogram.length; i++) {
                                s += histogram[i];
                                if (s > k * d / 256) {
                                    k += 1;
                                }
                                histogram[i] = k - 1;
                            }

                            for (int n = 0; n < nFrames; n++) {
                                for (int i = 0; i < bData[n].length; i++) {
                                    int v = (int) (bData[n][i] & 0xff) - bgValueGlobal;
                                    if (v <= low) {
                                        bData[n][i] = 0;
                                    } else {
                                        if (v >= histogram.length) {
                                            v = histogram.length - 1;
                                        }
                                        int b = (int) histogram[v];
                                        if (b > 255) {
                                            b = 255;
                                        }
                                        bData[n][i] = (byte) (0xff & b);
                                    }
                                }
                            }
                        }

                        DataArray[] out = new DataArray[1];
                        out[0] = DataArray.create(bData[0], 1, patientName);
                        out[0].getTimeData().clear();
                        for (int i = 0; i < nFrames; i++) {
                            out[0].addData(bData[i], i+framesRangeLow);                            
                        }                        
                        return out;
                        
                    } else if (bitsAllocated == 16) {
                        short[][] sData = new short[nFrames][nData];
                        int bgValueGlobal = 0;

                        int bgValue = readDicomArrayMonochromeFrames_16bit(entry, sData, dims, downsize, framesRangeLow, framesRangeUp);
                        if (bgValue < bgValueGlobal) {
                            bgValueGlobal = bgValue;
                        }

                        if (withProgress) {
                            progress = progressModifier;
                            fireProgressChanged();
                        }

                        if (readAs == Params.READ_AS_HISTOGRAM) {
                            byte[][] bData = new byte[nFrames][sData[0].length];
                            int low = lowCrop - bgValueGlobal;
                            long[] histogram = new long[4096];
                            for (int i = 0; i < histogram.length; i++) {
                                histogram[i] = 0;
                            }
                            
                            for (int n = 0; n < nFrames; n++) {
                                for (int i = 0; i < sData[n].length; i++) {
                                    int v = sData[n][i] - bgValueGlobal;
                                    if (v >= low && v >= 0 && v < histogram.length) {
                                        histogram[v] += 1;
                                    }
                                }
                            }
                            double s = 0, d = 0;
                            int k = 1;
                            d = 0;
                            for (int i = 1; i < histogram.length; i++) {
                                histogram[i] = (long) Math.sqrt(histogram[i]);
                                d += histogram[i];
                            }
                            if (histoArea != null) {
                                histoArea.setHisto(histogram);
                            }
                            s = 0;
                            k = 0;
                            for (int i = 1; i < histogram.length; i++) {
                                s += histogram[i];
                                if (s > k * d / 256) {
                                    k += 1;
                                }
                                histogram[i] = k - 1;
                            }

                            for (int n = 0; n < nFrames; n++) {
                                for (int i = 0; i < sData[n].length; i++) {
                                    int v = sData[n][i] - bgValueGlobal;
                                    if (v <= low) {
                                        bData[n][i] = 0;
                                    } else {
                                        if (v >= histogram.length) {
                                            v = histogram.length - 1;
                                        }
                                        int b = (int) histogram[v];
                                        if (b > 255) {
                                            b = 255;
                                        }
                                        bData[n][i] = (byte) (0xff & b);
                                    }
                                }
                            }
                            sData = null;
                            System.gc();
                            DataArray[] out = new DataArray[1];
                            out[0] = DataArray.create(bData[0], 1, patientName);
                            out[0].getTimeData().clear();
                            for (int i = 0; i < nFrames; i++) {
                                out[0].addData(bData[i], i+framesRangeLow);                            
                            }                        
                            return out;
                        }

                        DataArray[] out = new DataArray[1];
                        out[0] = DataArray.create(sData[0], 1, patientName);
                        out[0].getTimeData().clear();
                        for (int i = 0; i < nFrames; i++) {
                            out[0].addData(sData[i], i+framesRangeLow);                            
                        }                        
                        return out;
                    } else if (bitsAllocated == 32) {
                        int[][] iData = new int[nFrames][nData];
                        int bgValueGlobal = 0;

                        int bgValue = readDicomArrayMonochromeFrames_32bit(entry, iData, dims, downsize, framesRangeLow, framesRangeUp);
                        if (bgValue < bgValueGlobal) {
                            bgValueGlobal = bgValue;
                        }

                        if (withProgress) {
                            progress = progressModifier;
                            fireProgressChanged();
                        }

                        if (readAs == Params.READ_AS_HISTOGRAM) {
                            byte[][] bData = new byte[nFrames][iData[0].length];
                            int low = lowCrop - bgValueGlobal;
                            long[] histogram = new long[2048];
                            for (int i = 0; i < histogram.length; i++) {
                                histogram[i] = 0;
                            }
                            
                            for (int n = 0; n < nFrames; n++) {
                                for (int i = 0; i < iData[n].length; i++) {
                                    int v = iData[n][i] - bgValueGlobal;
                                    if (v >= low && v >= 0 && v < histogram.length) {
                                        histogram[v] += 1;
                                    }
                                }
                            }
                            double s = 0, d = 0;
                            int k = 1;
                            d = 0;
                            for (int i = 1; i < histogram.length; i++) {
                                histogram[i] = (long) Math.sqrt(histogram[i]);
                                d += histogram[i];
                            }
                            if (histoArea != null) {
                                histoArea.setHisto(histogram);
                            }
                            s = 0;
                            k = 0;
                            for (int i = 1; i < histogram.length; i++) {
                                s += histogram[i];
                                if (s > k * d / 256) {
                                    k += 1;
                                }
                                histogram[i] = k - 1;
                            }

                            for (int n = 0; n < nFrames; n++) {
                                for (int i = 0; i < iData[n].length; i++) {
                                    int v = iData[n][i] - bgValueGlobal;
                                    if (v <= low) {
                                        bData[n][i] = 0;
                                    } else {
                                        if (v >= histogram.length) {
                                            v = histogram.length - 1;
                                        }
                                        int b = (int) histogram[v];
                                        if (b > 255) {
                                            b = 255;
                                        }
                                        bData[n][i] = (byte) (0xff & b);
                                    }
                                }
                            }
                            iData = null;
                            System.gc();
                            DataArray[] out = new DataArray[1];
                            out[0] = DataArray.create(bData[0], 1, patientName);
                            out[0].getTimeData().clear();
                            for (int i = 0; i < nFrames; i++) {
                                out[0].addData(bData[i], i+framesRangeLow);                            
                            }                        
                            return out;
                        }

                        DataArray[] out = new DataArray[1];
                        out[0] = DataArray.create(iData[0], 1, patientName);
                        out[0].getTimeData().clear();
                        for (int i = 0; i < nFrames; i++) {
                            out[0].addData(iData[i], i+framesRangeLow);                            
                        }                        
                        return out;
                    }
                case Params.READ_AS_BYTES:
                case Params.READ_AS_WINDOW:
                    if (readAs == Params.READ_AS_WINDOW) {
                        if (windowFound) {
                            System.out.println("DICOM reader: reading frames with DICOM embeded window " + windowCenter + "/" + windowWidth + " normalized to bytes");
                        } else {
                            System.out.println("DICOM reader: no DICOM embeded window found - reading frames with byte data normalized to <" + lowCrop + " " + highCrop + ">");
                        }
                    } else {
                        System.out.println("DICOM reader: reading frames with byte data normalized to <" + lowCrop + " " + highCrop + ">");
                    }


                    byte[][] bData = new byte[nFrames][nData];
                    int bgValue = readDicomArrayMonochromeFrames_Cropped(entry, bData, dims, downsize, lowCrop, highCrop, false, framesRangeLow, framesRangeUp);

                    if (withProgress) {
                        progress = progressModifier;
                        fireProgressChanged();
                    }
                    DataArray[] out = new DataArray[1];
                    out[0] = DataArray.create(bData[0], 1, patientName);
                    out[0].getTimeData().clear();
                    for (int i = 0; i < nFrames; i++) {
                        out[0].addData(bData[i], i+framesRangeLow);                            
                    }                        
                    return out;
            }            
        }
        return null;
    }  
    
    private int readDicomArrayOffsetMonochrome_8bit(DICOMSortingEntry entry, byte[] bData, int offset, int[] dims, int[] downsize) {
        if (entry == null || bData == null || dims == null || downsize == null) {
            return Integer.MAX_VALUE;
        }

        if (offset >= bData.length) {
            return Integer.MAX_VALUE;
        }

        AttributeList atl = null;
        try {
            DicomInputStream dis = new DicomInputStream(new File(entry.getFilePath()));
            atl = new AttributeList();
            atl.read(dis);
            dis.close();
        } catch (Exception ex) {
            System.err.println("ERROR: " + ex.getMessage());
            return Integer.MAX_VALUE;
        }

        Attribute att = atl.get(TagFromName.PhotometricInterpretation);
        if (att == null) {
            System.err.println("DICOM ERROR: Wrong PhotometricInterpretation!");
            return Integer.MAX_VALUE;
        }
        String photometricInterpretation = att.getSingleStringValueOrNull();
        if (photometricInterpretation == null || (!photometricInterpretation.equals("MONOCHROME1") && !photometricInterpretation.equals("MONOCHROME2"))) {
            System.err.println("DICOM ERROR: Wrong PhotometricInterpretation!");
            return Integer.MAX_VALUE;
        }

        att = atl.get(TagFromName.SamplesPerPixel);
        int samplesPerPixel = att.getSingleIntegerValueOrDefault(1);

        if (samplesPerPixel != 1) {
            System.err.println("DICOM ERROR: Wrong SamplesPerPixel for "+photometricInterpretation+"!");
            return Integer.MAX_VALUE;
        }

        // - read data
        att = atl.get(TagFromName.Columns);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read Columns value!");
            return Integer.MAX_VALUE;
        }
        int width = att.getSingleIntegerValueOrDefault(0);
        if (width == 0) {
            System.err.println("DICOM ERROR: Cannot read Columns value!");
            return Integer.MAX_VALUE;
        }

        att = atl.get(TagFromName.Rows);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read Rows value!");
            return Integer.MAX_VALUE;
        }
        int height = att.getSingleIntegerValueOrDefault(0);
        if (height == 0) {
            System.err.println("DICOM ERROR: Cannot read Rows value!");
            return Integer.MAX_VALUE;
        }

        if ((int) Math.ceil((double)width / (double)downsize[0]) != dims[0] || (int) Math.ceil((double)height / (double)downsize[1]) != dims[1]) {
            System.err.println("ERROR: DICOM file not compatible with selected set!");
            return Integer.MAX_VALUE;
        }

        int nSliceData = width * height;

        att = atl.get(TagFromName.BitsAllocated);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read BitsAllocated!");
            return Integer.MAX_VALUE;
        }
        int bitsAllocated = att.getSingleIntegerValueOrDefault(8);
        if (bitsAllocated != 8) {
            System.err.println("DICOM ERROR: Wrong BitsAllocated!");
            return Integer.MAX_VALUE;
        }

        int rescaleIntercept = 0;
        boolean useRescaleIntercept = false;
        att = atl.get(TagFromName.RescaleIntercept);
        if (att != null) {
            rescaleIntercept = att.getSingleIntegerValueOrDefault(0);
            useRescaleIntercept = true;
        }

        double rescaleSlope = 1.0;
        boolean useRescaleSlope = false;
        att = atl.get(TagFromName.RescaleSlope);
        if (att != null) {
            rescaleSlope = att.getSingleDoubleValueOrDefault(1.0);
            useRescaleSlope = true;
        }

        int pixelPaddingValue = rescaleIntercept;
        boolean usePixelPaddingValue = false;
        att = atl.get(TagFromName.PixelPaddingValue);
        if (att != null) {
            pixelPaddingValue = att.getSingleIntegerValueOrDefault(rescaleIntercept);
            usePixelPaddingValue = true;
        }

        int pixelPaddingRangeLimit = pixelPaddingValue;
        att = atl.get(TagFromName.PixelPaddingRangeLimit);
        if (att != null) {
            pixelPaddingRangeLimit = att.getSingleIntegerValueOrDefault(pixelPaddingValue);
        }

        att = atl.get(TagFromName.PixelData);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read PixelData!");
            return Integer.MAX_VALUE;
        }

        byte[] bSliceData = null;
        try {
            bSliceData = att.getByteValues();
        } catch (DicomException dex) {
            System.err.println("ERROR: " + dex.getMessage());
            return Integer.MAX_VALUE;
        }

        if (bSliceData == null) {
            System.err.println("DICOM ERROR: Cannot read PixelData!");
            return Integer.MAX_VALUE;
        }
        if (bSliceData.length != nSliceData) {
            System.err.println("Wrong PixelData size!");
            return Integer.MAX_VALUE;
        }

        int bgValue = Integer.MAX_VALUE;
        int v;
        for (int i = 0; i < bSliceData.length; i++) {
            v = (int) (bSliceData[i] & 0xff);
            if (usePixelPaddingValue) {
                if ((v < pixelPaddingValue || v > pixelPaddingRangeLimit) && v < bgValue) {
                    bgValue = v;
                }
            } else {
                if (v < bgValue) {
                    bgValue = v;
                }
            }
        }

        if (usePixelPaddingValue) {
            for (int i = 0; i < bSliceData.length; i++) {
                if ((int) (bSliceData[i] & 0xff) >= pixelPaddingValue && (int) (bSliceData[i] & 0xff) <= pixelPaddingRangeLimit) {
                    bSliceData[i] = (byte) bgValue;
                }
            }
        }

        if (useRescaleSlope) {
            for (int i = 0; i < bSliceData.length; i++) {
                bSliceData[i] = (byte) Math.round((double) (bSliceData[i] & 0xff) * rescaleSlope + (double) rescaleIntercept);
            }
            bgValue = (int) Math.round((double) bgValue * rescaleSlope + (double) rescaleIntercept);
        } else if (useRescaleIntercept && !useRescaleSlope) {
            for (int i = 0; i < bSliceData.length; i++) {
                bSliceData[i] = (byte) ((bSliceData[i] & 0xff) + rescaleIntercept);
            }
            bgValue = bgValue + rescaleIntercept;
        }

        int maxValue = Integer.MIN_VALUE;
        if(photometricInterpretation.equals("MONOCHROME1")) {
            for (int i = 0; i < bSliceData.length; i++) {
                if((int)(bSliceData[i]&0xff) > maxValue) maxValue = (int)(bSliceData[i]&0xff);
            }

            for (int i = 0; i < bSliceData.length; i++) {
                bSliceData[i] = (byte)(maxValue - (int)(bSliceData[i]&0xff));
            }
            bgValue = maxValue - bgValue;
        }

        if (downsize[0] == 1 && downsize[1] == 1) {
            System.arraycopy(bSliceData, 0, bData, offset, bSliceData.length);
        } else {
            for (int y = 0, i = 0; y < height; y += downsize[1]) {
                for (int x = 0; x < width; x += downsize[0], i++) {
                    bData[offset + i] = bSliceData[y * width + x];
                }
            }
        }
        return bgValue;
    }

    private int readDicomArrayMonochromeFrames_8bit(DICOMSortingEntry entry, byte[] bData, int[] dims, int[] downsize) {
        if (entry == null || bData == null || dims == null || downsize == null) {
            return Integer.MAX_VALUE;
        }

        AttributeList atl = null;
        try {
            DicomInputStream dis = new DicomInputStream(new File(entry.getFilePath()));
            atl = new AttributeList();
            atl.read(dis);
            dis.close();
        } catch (Exception ex) {
            System.err.println("ERROR: " + ex.getMessage());
            return Integer.MAX_VALUE;
        }

        Attribute att = atl.get(TagFromName.PhotometricInterpretation);
        if (att == null) {
            System.err.println("DICOM ERROR: Wrong PhotometricInterpretation!");
            return Integer.MAX_VALUE;
        }
        String photometricInterpretation = att.getSingleStringValueOrNull();
        if (photometricInterpretation == null || (!photometricInterpretation.equals("MONOCHROME1") && !photometricInterpretation.equals("MONOCHROME2"))) {
            System.err.println("DICOM ERROR: Wrong PhotometricInterpretation!");
            return Integer.MAX_VALUE;
        }

        att = atl.get(TagFromName.SamplesPerPixel);
        int samplesPerPixel = att.getSingleIntegerValueOrDefault(1);

        if (samplesPerPixel != 1) {
            System.err.println("DICOM ERROR: Wrong SamplesPerPixel for "+photometricInterpretation+"!");
            return Integer.MAX_VALUE;
        }

        // - read data
        att = atl.get(TagFromName.Columns);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read Columns value!");
            return Integer.MAX_VALUE;
        }
        int width = att.getSingleIntegerValueOrDefault(0);
        if (width == 0) {
            System.err.println("DICOM ERROR: Cannot read Columns value!");
            return Integer.MAX_VALUE;
        }

        att = atl.get(TagFromName.Rows);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read Rows value!");
            return Integer.MAX_VALUE;
        }
        int height = att.getSingleIntegerValueOrDefault(0);
        if (height == 0) {
            System.err.println("DICOM ERROR: Cannot read Rows value!");
            return Integer.MAX_VALUE;
        }

        att = atl.get(TagFromName.NumberOfFrames);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read NumberOfFrames value!");
            return Integer.MAX_VALUE;
        }
        int depth = att.getSingleIntegerValueOrDefault(0);
        if (depth == 0) {
            System.err.println("DICOM ERROR: Cannot read NumberOfFrames value!");
            return Integer.MAX_VALUE;
        }
        
        
        if ((int) Math.ceil((double)width / (double)downsize[0]) != dims[0] || (int) Math.ceil((double)height / (double)downsize[1]) != dims[1] || (depth > 1 &&  (int) Math.ceil((double)depth / (double)downsize[2]) != dims[2])) {
            System.err.println("ERROR: DICOM file not compatible with selected set!");
            return Integer.MAX_VALUE;
        }

        int nData = width * height * depth;

        att = atl.get(TagFromName.BitsAllocated);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read BitsAllocated!");
            return Integer.MAX_VALUE;
        }
        int bitsAllocated = att.getSingleIntegerValueOrDefault(8);
        if (bitsAllocated != 8) {
            System.err.println("DICOM ERROR: Wrong BitsAllocated!");
            return Integer.MAX_VALUE;
        }

        int rescaleIntercept = 0;
        boolean useRescaleIntercept = false;
        att = atl.get(TagFromName.RescaleIntercept);
        if (att != null) {
            rescaleIntercept = att.getSingleIntegerValueOrDefault(0);
            useRescaleIntercept = true;
        }

        double rescaleSlope = 1.0;
        boolean useRescaleSlope = false;
        att = atl.get(TagFromName.RescaleSlope);
        if (att != null) {
            rescaleSlope = att.getSingleDoubleValueOrDefault(1.0);
            useRescaleSlope = true;
        }

        int pixelPaddingValue = rescaleIntercept;
        boolean usePixelPaddingValue = false;
        att = atl.get(TagFromName.PixelPaddingValue);
        if (att != null) {
            pixelPaddingValue = att.getSingleIntegerValueOrDefault(rescaleIntercept);
            usePixelPaddingValue = true;
        }

        int pixelPaddingRangeLimit = pixelPaddingValue;
        att = atl.get(TagFromName.PixelPaddingRangeLimit);
        if (att != null) {
            pixelPaddingRangeLimit = att.getSingleIntegerValueOrDefault(pixelPaddingValue);
        }

        att = atl.get(TagFromName.PixelData);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read PixelData!");
            return Integer.MAX_VALUE;
        }

        byte[] bVolumeData = null;
        try {
            if(att instanceof OtherWordAttributeOnDisk) {
                long off = ((OtherWordAttributeOnDisk)att).getByteOffset();
                boolean bigEndian = ((OtherWordAttributeOnDisk)att).isBigEndian();
                ((OtherWordAttributeOnDisk)att).removeValues();
                File f = ((OtherWordAttributeOnDisk)att).getFile();                
                if(f == null) {
                    f = new File(entry.getFilePath());
                }
                FileImageInputStream in = new FileImageInputStream(f);
                if(bigEndian)
                    in.setByteOrder(ByteOrder.BIG_ENDIAN);
                else
                    in.setByteOrder(ByteOrder.LITTLE_ENDIAN);
                bVolumeData = new byte[nData];
                in.seek(off);
                in.readFully(bVolumeData, 0, nData);
            } else {
                bVolumeData = att.getByteValues();                
            }
        } catch (Exception dex) {
            System.err.println("ERROR: " + dex.getMessage());
            return Integer.MAX_VALUE;
        }

        if (bVolumeData == null) {
            System.err.println("DICOM ERROR: Cannot read PixelData!");
            return Integer.MAX_VALUE;
        }
        if (bVolumeData.length != nData) {
            System.err.println("Wrong PixelData size!");
            return Integer.MAX_VALUE;
        }

        int bgValue = Integer.MAX_VALUE;
        int v;
        for (int i = 0; i < bVolumeData.length; i++) {
            v = (int) (bVolumeData[i] & 0xff);
            if (usePixelPaddingValue) {
                if ((v < pixelPaddingValue || v > pixelPaddingRangeLimit) && v < bgValue) {
                    bgValue = v;
                }
            } else {
                if (v < bgValue) {
                    bgValue = v;
                }
            }
        }

        if (usePixelPaddingValue) {
            for (int i = 0; i < bVolumeData.length; i++) {
                if ((int) (bVolumeData[i] & 0xff) >= pixelPaddingValue && (int) (bVolumeData[i] & 0xff) <= pixelPaddingRangeLimit) {
                    bVolumeData[i] = (byte) bgValue;
                }
            }
        }

        if (useRescaleSlope) {
            for (int i = 0; i < bVolumeData.length; i++) {
                bVolumeData[i] = (byte) Math.round((double) (bVolumeData[i] & 0xff) * rescaleSlope + (double) rescaleIntercept);
            }
            bgValue = (int) Math.round((double) bgValue * rescaleSlope + (double) rescaleIntercept);
        } else if (useRescaleIntercept && !useRescaleSlope) {
            for (int i = 0; i < bVolumeData.length; i++) {
                bVolumeData[i] = (byte) ((bVolumeData[i] & 0xff) + rescaleIntercept);
            }
            bgValue = bgValue + rescaleIntercept;
        }

        int maxValue = Integer.MIN_VALUE;
        if(photometricInterpretation.equals("MONOCHROME1")) {
            for (int i = 0; i < bVolumeData.length; i++) {
                if((int)(bVolumeData[i]&0xff) > maxValue) maxValue = (int)(bVolumeData[i]&0xff);
            }

            for (int i = 0; i < bVolumeData.length; i++) {
                bVolumeData[i] = (byte)(maxValue - (int)(bVolumeData[i]&0xff));
            }
            bgValue = maxValue - bgValue;
        }

        if (downsize[0] == 1 && downsize[1] == 1 && downsize[2] == 1) {
            System.arraycopy(bVolumeData, 0, bData, 0, bVolumeData.length);
        } else {
            for (int z = 0, i = 0; z < depth; z += downsize[2]) {
                for (int y = 0; y < height; y += downsize[1]) {
                    for (int x = 0; x < width; x += downsize[0], i++) {
                        bData[i] = bVolumeData[z*width*height + y*width + x];
                    }
                }
            }
        }
        return bgValue;
    }

    private int readDicomArrayMonochromeFrames_8bit(DICOMSortingEntry entry, byte[][] bData, int[] dims, int[] downsize, int framesRangeLow, int framesRangeUp) {
        if (entry == null || bData == null || dims == null || downsize == null) {
            return Integer.MAX_VALUE;
        }

        AttributeList atl = null;
        try {
            DicomInputStream dis = new DicomInputStream(new File(entry.getFilePath()));
            atl = new AttributeList();
            atl.read(dis);
            dis.close();
        } catch (Exception ex) {
            System.err.println("ERROR: " + ex.getMessage());
            return Integer.MAX_VALUE;
        }

        Attribute att = atl.get(TagFromName.PhotometricInterpretation);
        if (att == null) {
            System.err.println("DICOM ERROR: Wrong PhotometricInterpretation!");
            return Integer.MAX_VALUE;
        }
        String photometricInterpretation = att.getSingleStringValueOrNull();
        if (photometricInterpretation == null || (!photometricInterpretation.equals("MONOCHROME1") && !photometricInterpretation.equals("MONOCHROME2"))) {
            System.err.println("DICOM ERROR: Wrong PhotometricInterpretation!");
            return Integer.MAX_VALUE;
        }

        att = atl.get(TagFromName.SamplesPerPixel);
        int samplesPerPixel = att.getSingleIntegerValueOrDefault(1);

        if (samplesPerPixel != 1) {
            System.err.println("DICOM ERROR: Wrong SamplesPerPixel for "+photometricInterpretation+"!");
            return Integer.MAX_VALUE;
        }

        // - read data
        att = atl.get(TagFromName.Columns);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read Columns value!");
            return Integer.MAX_VALUE;
        }
        int width = att.getSingleIntegerValueOrDefault(0);
        if (width == 0) {
            System.err.println("DICOM ERROR: Cannot read Columns value!");
            return Integer.MAX_VALUE;
        }

        att = atl.get(TagFromName.Rows);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read Rows value!");
            return Integer.MAX_VALUE;
        }
        int height = att.getSingleIntegerValueOrDefault(0);
        if (height == 0) {
            System.err.println("DICOM ERROR: Cannot read Rows value!");
            return Integer.MAX_VALUE;
        }

        att = atl.get(TagFromName.NumberOfFrames);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read NumberOfFrames value!");
            return Integer.MAX_VALUE;
        }
        int nFrames = att.getSingleIntegerValueOrDefault(0);
        if (nFrames == 0) {
            System.err.println("DICOM ERROR: Cannot read NumberOfFrames value!");
            return Integer.MAX_VALUE;
        }
        int nFramesToRead = framesRangeUp - framesRangeLow + 1;
        
        
        if ((int) Math.ceil((double)width / (double)downsize[0]) != dims[0] || (int) Math.ceil((double)height / (double)downsize[1]) != dims[1]) {
            System.err.println("ERROR: DICOM file not compatible with selected set!");
            return Integer.MAX_VALUE;
        }

        int nData = width * height;

        att = atl.get(TagFromName.BitsAllocated);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read BitsAllocated!");
            return Integer.MAX_VALUE;
        }
        int bitsAllocated = att.getSingleIntegerValueOrDefault(8);
        if (bitsAllocated != 8) {
            System.err.println("DICOM ERROR: Wrong BitsAllocated!");
            return Integer.MAX_VALUE;
        }

        int rescaleIntercept = 0;
        boolean useRescaleIntercept = false;
        att = atl.get(TagFromName.RescaleIntercept);
        if (att != null) {
            rescaleIntercept = att.getSingleIntegerValueOrDefault(0);
            useRescaleIntercept = true;
        }

        double rescaleSlope = 1.0;
        boolean useRescaleSlope = false;
        att = atl.get(TagFromName.RescaleSlope);
        if (att != null) {
            rescaleSlope = att.getSingleDoubleValueOrDefault(1.0);
            useRescaleSlope = true;
        }

        int pixelPaddingValue = rescaleIntercept;
        boolean usePixelPaddingValue = false;
        att = atl.get(TagFromName.PixelPaddingValue);
        if (att != null) {
            pixelPaddingValue = att.getSingleIntegerValueOrDefault(rescaleIntercept);
            usePixelPaddingValue = true;
        }

        int pixelPaddingRangeLimit = pixelPaddingValue;
        att = atl.get(TagFromName.PixelPaddingRangeLimit);
        if (att != null) {
            pixelPaddingRangeLimit = att.getSingleIntegerValueOrDefault(pixelPaddingValue);
        }

        att = atl.get(TagFromName.PixelData);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read PixelData!");
            return Integer.MAX_VALUE;
        }

        byte[] bVolumeData = null;
        try {
            if(att instanceof OtherWordAttributeOnDisk) {
                long off = ((OtherWordAttributeOnDisk)att).getByteOffset();
                boolean bigEndian = ((OtherWordAttributeOnDisk)att).isBigEndian();
                ((OtherWordAttributeOnDisk)att).removeValues();
                File f = ((OtherWordAttributeOnDisk)att).getFile();                
                if(f == null) {
                    f = new File(entry.getFilePath());
                }
                FileImageInputStream in = new FileImageInputStream(f);
                if(bigEndian)
                    in.setByteOrder(ByteOrder.BIG_ENDIAN);
                else
                    in.setByteOrder(ByteOrder.LITTLE_ENDIAN);
                
                bVolumeData = new byte[nData*nFrames];
                in.seek(off);
                in.readFully(bVolumeData, 0, nData*nFrames);
            } else {
                bVolumeData = att.getByteValues();                
            }
        } catch (Exception dex) {
            System.err.println("ERROR: " + dex.getMessage());
            return Integer.MAX_VALUE;
        }

        if (bVolumeData == null) {
            System.err.println("DICOM ERROR: Cannot read PixelData!");
            return Integer.MAX_VALUE;
        }
        if (bVolumeData.length != nData*nFrames) {
            System.err.println("Wrong PixelData size!");
            return Integer.MAX_VALUE;
        }

        int bgValue = Integer.MAX_VALUE;
        int v;
        for (int i = 0; i < bVolumeData.length; i++) {
            v = (int) (bVolumeData[i] & 0xff);
            if (usePixelPaddingValue) {
                if ((v < pixelPaddingValue || v > pixelPaddingRangeLimit) && v < bgValue) {
                    bgValue = v;
                }
            } else {
                if (v < bgValue) {
                    bgValue = v;
                }
            }
        }

        if (usePixelPaddingValue) {
            for (int i = 0; i < bVolumeData.length; i++) {
                if ((int) (bVolumeData[i] & 0xff) >= pixelPaddingValue && (int) (bVolumeData[i] & 0xff) <= pixelPaddingRangeLimit) {
                    bVolumeData[i] = (byte) bgValue;
                }
            }
        }

        if (useRescaleSlope) {
            for (int i = 0; i < bVolumeData.length; i++) {
                bVolumeData[i] = (byte) Math.round((double) (bVolumeData[i] & 0xff) * rescaleSlope + (double) rescaleIntercept);
            }
            bgValue = (int) Math.round((double) bgValue * rescaleSlope + (double) rescaleIntercept);
        } else if (useRescaleIntercept && !useRescaleSlope) {
            for (int i = 0; i < bVolumeData.length; i++) {
                bVolumeData[i] = (byte) ((bVolumeData[i] & 0xff) + rescaleIntercept);
            }
            bgValue = bgValue + rescaleIntercept;
        }

        int maxValue = Integer.MIN_VALUE;
        if(photometricInterpretation.equals("MONOCHROME1")) {
            for (int i = 0; i < bVolumeData.length; i++) {
                if((int)(bVolumeData[i]&0xff) > maxValue) maxValue = (int)(bVolumeData[i]&0xff);
            }

            for (int i = 0; i < bVolumeData.length; i++) {
                bVolumeData[i] = (byte)(maxValue - (int)(bVolumeData[i]&0xff));
            }
            bgValue = maxValue - bgValue;
        }

        if (downsize[0] == 1 && downsize[1] == 1) {
            for (int n = 0; n < nFramesToRead; n++) {
                System.arraycopy(bVolumeData, (n+framesRangeLow)*nData, bData[n], 0, nData);                
            }            
        } else {
            for (int n = 0; n < nFramesToRead; n++) {
                for (int y = 0, i = 0; y < height; y += downsize[1]) {
                    for (int x = 0; x < width; x += downsize[0], i++) {
                        bData[n][i] = bVolumeData[(n+framesRangeLow)*nData + y*width + x];
                    }
                }
            }
        }
        return bgValue;
    }
    
    private int readDicomArrayOffsetMonochrome_16bit(DICOMSortingEntry entry, short[] sData, int offset, int[] dims, int[] downsize) {
        if (entry == null || sData == null || dims == null || downsize == null) {
            return Integer.MAX_VALUE;
        }

        if (offset >= sData.length) {
            return Integer.MAX_VALUE;
        }

        AttributeList atl = null;
        try {
            DicomInputStream dis = new DicomInputStream(new File(entry.getFilePath()));
            atl = new AttributeList();
            atl.read(dis);
            dis.close();
        } catch (DicomException ex) {
            System.err.println("ERROR: " + ex.getMessage());
            return Integer.MAX_VALUE;
        } catch(IOException ex) {
            System.err.println("ERROR: " + ex.getMessage());
            return Integer.MAX_VALUE;
        }

        Attribute att = atl.get(TagFromName.PhotometricInterpretation);
        if (att == null) {
            System.err.println("DICOM ERROR: Wrong PhotometricInterpretation!");
            return Integer.MAX_VALUE;
        }
        String photometricInterpretation = att.getSingleStringValueOrNull();
        if (photometricInterpretation == null || (!photometricInterpretation.equals("MONOCHROME1") && !photometricInterpretation.equals("MONOCHROME2"))) {
            System.err.println("DICOM ERROR: Wrong PhotometricInterpretation!");
            return Integer.MAX_VALUE;
        }
        
        att = atl.get(TagFromName.SamplesPerPixel);
        int samplesPerPixel = att.getSingleIntegerValueOrDefault(1);

        if (samplesPerPixel != 1) {
            System.err.println("DICOM ERROR: Wrong SamplesPerPixel for "+photometricInterpretation+"!");
            return Integer.MAX_VALUE;
        }

        // - read data
        att = atl.get(TagFromName.Columns);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read Columns value!");
            return Integer.MAX_VALUE;
        }
        int width = att.getSingleIntegerValueOrDefault(0);
        if (width == 0) {
            System.err.println("DICOM ERROR: Cannot read Columns value!");
            return Integer.MAX_VALUE;
        }

        att = atl.get(TagFromName.Rows);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read Rows value!");
            return Integer.MAX_VALUE;
        }
        int height = att.getSingleIntegerValueOrDefault(0);
        if (height == 0) {
            System.err.println("DICOM ERROR: Cannot read Rows value!");
            return Integer.MAX_VALUE;
        }

        if ((int) Math.ceil((double)width / (double)downsize[0]) != dims[0] || (int) Math.ceil((double)height / (double)downsize[1]) != dims[1]) {
            System.err.println("ERROR: DICOM file not compatible with selected set!");
            return Integer.MAX_VALUE;
        }

        int nSliceData = width * height;

        att = atl.get(TagFromName.BitsAllocated);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read BitsAllocated!");
            return Integer.MAX_VALUE;
        }
        int bitsAllocated = att.getSingleIntegerValueOrDefault(16);
        if (bitsAllocated != 16) {
            System.err.println("DICOM ERROR: Wrong BitsAllocated!");
            return Integer.MAX_VALUE;
        }

        int rescaleIntercept = 0;
        boolean useRescaleIntercept = false;
        att = atl.get(TagFromName.RescaleIntercept);
        if (att != null) {
            rescaleIntercept = att.getSingleIntegerValueOrDefault(0);
            useRescaleIntercept = true;
        }

        double rescaleSlope = 1.0;
        boolean useRescaleSlope = false;
        att = atl.get(TagFromName.RescaleSlope);
        if (att != null) {
            rescaleSlope = att.getSingleDoubleValueOrDefault(1.0);
            useRescaleSlope = true;
        }

        int pixelPaddingValue = rescaleIntercept;
        boolean usePixelPaddingValue = false;
        att = atl.get(TagFromName.PixelPaddingValue);
        if (att != null) {
            pixelPaddingValue = att.getSingleIntegerValueOrDefault(rescaleIntercept);
            usePixelPaddingValue = true;
        }

        int pixelPaddingRangeLimit = pixelPaddingValue;
        att = atl.get(TagFromName.PixelPaddingRangeLimit);
        if (att != null) {
            pixelPaddingRangeLimit = att.getSingleIntegerValueOrDefault(pixelPaddingValue);
        }

        att = atl.get(TagFromName.PixelData);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read PixelData!");
            return Integer.MAX_VALUE;
        }

        short[] sSliceData = null;
        try {
            sSliceData = att.getShortValues();
        } catch (DicomException dex) {
            System.err.println("ERROR: " + dex.getMessage());
            return Integer.MAX_VALUE;
        }

        if (sSliceData == null) {
            System.err.println("DICOM ERROR: Cannot read PixelData!");
            return Integer.MAX_VALUE;
        }
        if (sSliceData.length != nSliceData) {
            System.err.println("Wrong PixelData size!");
            return Integer.MAX_VALUE;
        }

        int bgValue = Integer.MAX_VALUE;
        short v;
        for (int i = 0; i < sSliceData.length; i++) {
            v = sSliceData[i];
            if (usePixelPaddingValue) {
                if (((int) v < pixelPaddingValue || (int) v > pixelPaddingRangeLimit) && (int) v < bgValue) {
                    bgValue = (int) v;
                }
            } else {
                if ((int) v < bgValue) {
                    bgValue = (int) v;
                }
            }
        }

        if (usePixelPaddingValue) {
            for (int i = 0; i < sSliceData.length; i++) {
                if ((int) sSliceData[i] >= pixelPaddingValue && (int) sSliceData[i] <= pixelPaddingRangeLimit) {
                    sSliceData[i] = (short) bgValue;
                }
            }
        }

        if (useRescaleSlope) {
            for (int i = 0; i < sSliceData.length; i++) {
                sSliceData[i] = (short) Math.round((double) sSliceData[i] * rescaleSlope + (double) rescaleIntercept);
            }
            bgValue = (int) Math.round((double) bgValue * rescaleSlope + (double) rescaleIntercept);
        } else if (useRescaleIntercept && !useRescaleSlope) {
            for (int i = 0; i < sSliceData.length; i++) {
                sSliceData[i] = (short) (sSliceData[i] + rescaleIntercept);
            }
            bgValue = bgValue + rescaleIntercept;
        }

        if(photometricInterpretation.equals("MONOCHROME1")) {
            short maxValue = Short.MIN_VALUE;
            for (int i = 0; i < sSliceData.length; i++) {
                if(sSliceData[i] > maxValue) maxValue = sSliceData[i];
            }

            for (int i = 0; i < sSliceData.length; i++) {
                sSliceData[i] = (short)(maxValue - sSliceData[i]);
            }
            bgValue = maxValue - bgValue;
        }

        if (downsize[0] == 1 && downsize[1] == 1) {
            System.arraycopy(sSliceData, 0, sData, offset, sSliceData.length);
        } else {
            for (int y = 0, i = 0; y < height; y += downsize[1]) {
                for (int x = 0; x < width; x += downsize[0], i++) {
                    sData[offset + i] = sSliceData[y * width + x];
                }
            }
        }
        return bgValue;
    }
    
    private int readDicomArrayMonochromeFrames_16bit(DICOMSortingEntry entry, short[] sData, int[] dims, int[] downsize) {
        if (entry == null || sData == null || dims == null || downsize == null) {
            return Integer.MAX_VALUE;
        }

        AttributeList atl = null;
        try {
            DicomInputStream dis = new DicomInputStream(new File(entry.getFilePath()));
            atl = new AttributeList();
            atl.read(dis);
            dis.close();
        } catch (DicomException ex) {
            System.err.println("ERROR: " + ex.getMessage());
            return Integer.MAX_VALUE;
        } catch(IOException ex) {
            System.err.println("ERROR: " + ex.getMessage());
            return Integer.MAX_VALUE;
        }

        Attribute att = atl.get(TagFromName.PhotometricInterpretation);
        if (att == null) {
            System.err.println("DICOM ERROR: Wrong PhotometricInterpretation!");
            return Integer.MAX_VALUE;
        }
        String photometricInterpretation = att.getSingleStringValueOrNull();
        if (photometricInterpretation == null || (!photometricInterpretation.equals("MONOCHROME1") && !photometricInterpretation.equals("MONOCHROME2"))) {
            System.err.println("DICOM ERROR: Wrong PhotometricInterpretation!");
            return Integer.MAX_VALUE;
        }
        
        att = atl.get(TagFromName.SamplesPerPixel);
        int samplesPerPixel = att.getSingleIntegerValueOrDefault(1);

        if (samplesPerPixel != 1) {
            System.err.println("DICOM ERROR: Wrong SamplesPerPixel for "+photometricInterpretation+"!");
            return Integer.MAX_VALUE;
        }

        // - read data
        att = atl.get(TagFromName.Columns);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read Columns value!");
            return Integer.MAX_VALUE;
        }
        int width = att.getSingleIntegerValueOrDefault(0);
        if (width == 0) {
            System.err.println("DICOM ERROR: Cannot read Columns value!");
            return Integer.MAX_VALUE;
        }

        att = atl.get(TagFromName.Rows);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read Rows value!");
            return Integer.MAX_VALUE;
        }
        int height = att.getSingleIntegerValueOrDefault(0);
        if (height == 0) {
            System.err.println("DICOM ERROR: Cannot read Rows value!");
            return Integer.MAX_VALUE;
        }

        att = atl.get(TagFromName.NumberOfFrames);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read NumberOfFrames value!");
            return Integer.MAX_VALUE;
        }
        int depth = att.getSingleIntegerValueOrDefault(0);
        if (depth == 0) {
            System.err.println("DICOM ERROR: Cannot read NumberOfFrames value!");
            return Integer.MAX_VALUE;
        }        
        
        if ((int) Math.ceil((double)width / (double)downsize[0]) != dims[0] || (int) Math.ceil((double)height / (double)downsize[1]) != dims[1] || (depth > 1 && (int) Math.ceil((double)depth / (double)downsize[2]) != dims[2])) {
            System.err.println("ERROR: DICOM file not compatible with selected set!");
            return Integer.MAX_VALUE;
        }

        int nData = width * height * depth;

        att = atl.get(TagFromName.BitsAllocated);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read BitsAllocated!");
            return Integer.MAX_VALUE;
        }
        int bitsAllocated = att.getSingleIntegerValueOrDefault(16);
        if (bitsAllocated != 16) {
            System.err.println("DICOM ERROR: Wrong BitsAllocated!");
            return Integer.MAX_VALUE;
        }

        int rescaleIntercept = 0;
        boolean useRescaleIntercept = false;
        att = atl.get(TagFromName.RescaleIntercept);
        if (att != null) {
            rescaleIntercept = att.getSingleIntegerValueOrDefault(0);
            useRescaleIntercept = true;
        }

        double rescaleSlope = 1.0;
        boolean useRescaleSlope = false;
        att = atl.get(TagFromName.RescaleSlope);
        if (att != null) {
            rescaleSlope = att.getSingleDoubleValueOrDefault(1.0);
            useRescaleSlope = true;
        }

        int pixelPaddingValue = rescaleIntercept;
        boolean usePixelPaddingValue = false;
        att = atl.get(TagFromName.PixelPaddingValue);
        if (att != null) {
            pixelPaddingValue = att.getSingleIntegerValueOrDefault(rescaleIntercept);
            usePixelPaddingValue = true;
        }

        int pixelPaddingRangeLimit = pixelPaddingValue;
        att = atl.get(TagFromName.PixelPaddingRangeLimit);
        if (att != null) {
            pixelPaddingRangeLimit = att.getSingleIntegerValueOrDefault(pixelPaddingValue);
        }

        att = atl.get(TagFromName.PixelData);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read PixelData!");
            return Integer.MAX_VALUE;
        }

        short[] sVolumeData = null;
        try {
            if(att instanceof OtherWordAttributeOnDisk) {
                long off = ((OtherWordAttributeOnDisk)att).getByteOffset();
                boolean bigEndian = ((OtherWordAttributeOnDisk)att).isBigEndian();
                ((OtherWordAttributeOnDisk)att).removeValues();
                File f = ((OtherWordAttributeOnDisk)att).getFile();                
                if(f == null) {
                    f = new File(entry.getFilePath());
                }
                FileImageInputStream in = new FileImageInputStream(f);
                if(bigEndian)
                    in.setByteOrder(ByteOrder.BIG_ENDIAN);
                else
                    in.setByteOrder(ByteOrder.LITTLE_ENDIAN);
                sVolumeData = new short[nData];
                in.seek(off);
                in.readFully(sVolumeData, 0, nData);
            } else {
                sVolumeData = att.getShortValues();                
            }
        } catch (Exception ex) {
            System.err.println("ERROR: " + ex.getMessage());
            return Integer.MAX_VALUE;
        }

        if (sVolumeData == null) {
            System.err.println("DICOM ERROR: Cannot read PixelData!");
            return Integer.MAX_VALUE;
        }
        if (sVolumeData.length != nData) {
            System.err.println("Wrong PixelData size!");
            return Integer.MAX_VALUE;
        }

        int bgValue = Integer.MAX_VALUE;
        short v;
        for (int i = 0; i < sVolumeData.length; i++) {
            v = sVolumeData[i];
            if (usePixelPaddingValue) {
                if (((int) v < pixelPaddingValue || (int) v > pixelPaddingRangeLimit) && (int) v < bgValue) {
                    bgValue = (int) v;
                }
            } else {
                if ((int) v < bgValue) {
                    bgValue = (int) v;
                }
            }
        }

        if (usePixelPaddingValue) {
            for (int i = 0; i < sVolumeData.length; i++) {
                if ((int) sVolumeData[i] >= pixelPaddingValue && (int) sVolumeData[i] <= pixelPaddingRangeLimit) {
                    sVolumeData[i] = (short) bgValue;
                }
            }
        }

        if (useRescaleSlope) {
            for (int i = 0; i < sVolumeData.length; i++) {
                sVolumeData[i] = (short) Math.round((double) sVolumeData[i] * rescaleSlope + (double) rescaleIntercept);
            }
            bgValue = (int) Math.round((double) bgValue * rescaleSlope + (double) rescaleIntercept);
        } else if (useRescaleIntercept && !useRescaleSlope) {
            for (int i = 0; i < sVolumeData.length; i++) {
                sVolumeData[i] = (short) (sVolumeData[i] + rescaleIntercept);
            }
            bgValue = bgValue + rescaleIntercept;
        }

        if(photometricInterpretation.equals("MONOCHROME1")) {
            short maxValue = Short.MIN_VALUE;
            for (int i = 0; i < sVolumeData.length; i++) {
                if(sVolumeData[i] > maxValue) maxValue = sVolumeData[i];
            }

            for (int i = 0; i < sVolumeData.length; i++) {
                sVolumeData[i] = (short)(maxValue - sVolumeData[i]);
            }
            bgValue = maxValue - bgValue;
        }

        if (downsize[0] == 1 && downsize[1] == 1 && downsize[2] == 1) {
            System.arraycopy(sVolumeData, 0, sData, 0, sVolumeData.length);
        } else {
            for (int z = 0, i = 0; z < depth; z += downsize[2]) {
                for (int y = 0; y < height; y += downsize[1]) {
                    for (int x = 0; x < width; x += downsize[0], i++) {
                        sData[i] = sVolumeData[z*width*height + y*width + x];
                    }
                }
            }
        }
        return bgValue;
    }

    private int readDicomArrayMonochromeFrames_16bit(DICOMSortingEntry entry, short[][] sData, int[] dims, int[] downsize, int framesRangeLow, int framesRangeUp) {
        if (entry == null || sData == null || dims == null || downsize == null) {
            return Integer.MAX_VALUE;
        }

        AttributeList atl = null;
        try {
            DicomInputStream dis = new DicomInputStream(new File(entry.getFilePath()));
            atl = new AttributeList();
            atl.read(dis);
            dis.close();
        } catch (DicomException ex) {
            System.err.println("ERROR: " + ex.getMessage());
            return Integer.MAX_VALUE;
        } catch(IOException ex) {
            System.err.println("ERROR: " + ex.getMessage());
            return Integer.MAX_VALUE;
        }

        Attribute att = atl.get(TagFromName.PhotometricInterpretation);
        if (att == null) {
            System.err.println("DICOM ERROR: Wrong PhotometricInterpretation!");
            return Integer.MAX_VALUE;
        }
        String photometricInterpretation = att.getSingleStringValueOrNull();
        if (photometricInterpretation == null || (!photometricInterpretation.equals("MONOCHROME1") && !photometricInterpretation.equals("MONOCHROME2"))) {
            System.err.println("DICOM ERROR: Wrong PhotometricInterpretation!");
            return Integer.MAX_VALUE;
        }
        
        att = atl.get(TagFromName.SamplesPerPixel);
        int samplesPerPixel = att.getSingleIntegerValueOrDefault(1);

        if (samplesPerPixel != 1) {
            System.err.println("DICOM ERROR: Wrong SamplesPerPixel for "+photometricInterpretation+"!");
            return Integer.MAX_VALUE;
        }

        // - read data
        att = atl.get(TagFromName.Columns);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read Columns value!");
            return Integer.MAX_VALUE;
        }
        int width = att.getSingleIntegerValueOrDefault(0);
        if (width == 0) {
            System.err.println("DICOM ERROR: Cannot read Columns value!");
            return Integer.MAX_VALUE;
        }

        att = atl.get(TagFromName.Rows);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read Rows value!");
            return Integer.MAX_VALUE;
        }
        int height = att.getSingleIntegerValueOrDefault(0);
        if (height == 0) {
            System.err.println("DICOM ERROR: Cannot read Rows value!");
            return Integer.MAX_VALUE;
        }

        att = atl.get(TagFromName.NumberOfFrames);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read NumberOfFrames value!");
            return Integer.MAX_VALUE;
        }
        int nFrames = att.getSingleIntegerValueOrDefault(0);
        if (nFrames == 0) {
            System.err.println("DICOM ERROR: Cannot read NumberOfFrames value!");
            return Integer.MAX_VALUE;
        }   
        int nFramesToRead = framesRangeUp - framesRangeLow + 1;
        
        if ((int) Math.ceil((double)width / (double)downsize[0]) != dims[0] || (int) Math.ceil((double)height / (double)downsize[1]) != dims[1] || (nFrames > 1 && (int) Math.ceil((double)nFrames / (double)downsize[2]) != dims[2])) {
            System.err.println("ERROR: DICOM file not compatible with selected set!");
            return Integer.MAX_VALUE;
        }

        int nData = width * height;

        att = atl.get(TagFromName.BitsAllocated);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read BitsAllocated!");
            return Integer.MAX_VALUE;
        }
        int bitsAllocated = att.getSingleIntegerValueOrDefault(16);
        if (bitsAllocated != 16) {
            System.err.println("DICOM ERROR: Wrong BitsAllocated!");
            return Integer.MAX_VALUE;
        }

        int rescaleIntercept = 0;
        boolean useRescaleIntercept = false;
        att = atl.get(TagFromName.RescaleIntercept);
        if (att != null) {
            rescaleIntercept = att.getSingleIntegerValueOrDefault(0);
            useRescaleIntercept = true;
        }

        double rescaleSlope = 1.0;
        boolean useRescaleSlope = false;
        att = atl.get(TagFromName.RescaleSlope);
        if (att != null) {
            rescaleSlope = att.getSingleDoubleValueOrDefault(1.0);
            useRescaleSlope = true;
        }

        int pixelPaddingValue = rescaleIntercept;
        boolean usePixelPaddingValue = false;
        att = atl.get(TagFromName.PixelPaddingValue);
        if (att != null) {
            pixelPaddingValue = att.getSingleIntegerValueOrDefault(rescaleIntercept);
            usePixelPaddingValue = true;
        }

        int pixelPaddingRangeLimit = pixelPaddingValue;
        att = atl.get(TagFromName.PixelPaddingRangeLimit);
        if (att != null) {
            pixelPaddingRangeLimit = att.getSingleIntegerValueOrDefault(pixelPaddingValue);
        }

        att = atl.get(TagFromName.PixelData);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read PixelData!");
            return Integer.MAX_VALUE;
        }

        short[] sVolumeData = null;
        try {
            if(att instanceof OtherWordAttributeOnDisk) {
                long off = ((OtherWordAttributeOnDisk)att).getByteOffset();
                boolean bigEndian = ((OtherWordAttributeOnDisk)att).isBigEndian();
                ((OtherWordAttributeOnDisk)att).removeValues();
                File f = ((OtherWordAttributeOnDisk)att).getFile();                
                if(f == null) {
                    f = new File(entry.getFilePath());
                }
                FileImageInputStream in = new FileImageInputStream(f);
                if(bigEndian)
                    in.setByteOrder(ByteOrder.BIG_ENDIAN);
                else
                    in.setByteOrder(ByteOrder.LITTLE_ENDIAN);
                sVolumeData = new short[nData*nFrames];
                in.seek(off);
                in.readFully(sVolumeData, 0, nData*nFrames);
            } else {
                sVolumeData = att.getShortValues();                
            }
        } catch (Exception ex) {
            System.err.println("ERROR: " + ex.getMessage());
            return Integer.MAX_VALUE;
        }

        if (sVolumeData == null) {
            System.err.println("DICOM ERROR: Cannot read PixelData!");
            return Integer.MAX_VALUE;
        }
        if (sVolumeData.length != nData*nFrames) {
            System.err.println("Wrong PixelData size!");
            return Integer.MAX_VALUE;
        }

        int bgValue = Integer.MAX_VALUE;
        short v;
        for (int i = 0; i < sVolumeData.length; i++) {
            v = sVolumeData[i];
            if (usePixelPaddingValue) {
                if (((int) v < pixelPaddingValue || (int) v > pixelPaddingRangeLimit) && (int) v < bgValue) {
                    bgValue = (int) v;
                }
            } else {
                if ((int) v < bgValue) {
                    bgValue = (int) v;
                }
            }
        }

        if (usePixelPaddingValue) {
            for (int i = 0; i < sVolumeData.length; i++) {
                if ((int) sVolumeData[i] >= pixelPaddingValue && (int) sVolumeData[i] <= pixelPaddingRangeLimit) {
                    sVolumeData[i] = (short) bgValue;
                }
            }
        }

        if (useRescaleSlope) {
            for (int i = 0; i < sVolumeData.length; i++) {
                sVolumeData[i] = (short) Math.round((double) sVolumeData[i] * rescaleSlope + (double) rescaleIntercept);
            }
            bgValue = (int) Math.round((double) bgValue * rescaleSlope + (double) rescaleIntercept);
        } else if (useRescaleIntercept && !useRescaleSlope) {
            for (int i = 0; i < sVolumeData.length; i++) {
                sVolumeData[i] = (short) (sVolumeData[i] + rescaleIntercept);
            }
            bgValue = bgValue + rescaleIntercept;
        }

        if(photometricInterpretation.equals("MONOCHROME1")) {
            short maxValue = Short.MIN_VALUE;
            for (int i = 0; i < sVolumeData.length; i++) {
                if(sVolumeData[i] > maxValue) maxValue = sVolumeData[i];
            }

            for (int i = 0; i < sVolumeData.length; i++) {
                sVolumeData[i] = (short)(maxValue - sVolumeData[i]);
            }
            bgValue = maxValue - bgValue;
        }

        if (downsize[0] == 1 && downsize[1] == 1) {
            for (int n = 0; n < nFramesToRead; n++) {
                System.arraycopy(sVolumeData, (n+framesRangeLow)*nData, sData[n], 0, nData);                
            }            
        } else {
            for (int n = 0; n < nFramesToRead; n++) {
                for (int y = 0, i = 0; y < height; y += downsize[1]) {
                    for (int x = 0; x < width; x += downsize[0], i++) {
                        sData[n][i] = sVolumeData[(n+framesRangeLow)*nData + y*width + x];
                    }
                }
            }
        }
        return bgValue;
    }
    
    private int readDicomArrayOffsetMonochrome_32bit(DICOMSortingEntry entry, int[] iData, int offset, int[] dims, int[] downsize) {
        if (entry == null || iData == null || dims == null || downsize == null) {
            return Integer.MAX_VALUE;
        }

        if (offset >= iData.length) {
            return Integer.MAX_VALUE;
        }

        AttributeList atl = null;
        try {
            DicomInputStream dis = new DicomInputStream(new File(entry.getFilePath()));
            atl = new AttributeList();
            atl.read(dis);
            dis.close();
        } catch (Exception ex) {
            System.err.println("ERROR: " + ex.getMessage());
            return Integer.MAX_VALUE;
        }

        Attribute att = atl.get(TagFromName.PhotometricInterpretation);
        if (att == null) {
            System.err.println("DICOM ERROR: Wrong PhotometricInterpretation!");
            return Integer.MAX_VALUE;
        }
        String photometricInterpretation = att.getSingleStringValueOrNull();
        if (photometricInterpretation == null || (!photometricInterpretation.equals("MONOCHROME1") && !photometricInterpretation.equals("MONOCHROME2"))) {
            System.err.println("DICOM ERROR: Wrong PhotometricInterpretation!");
            return Integer.MAX_VALUE;
        }

        att = atl.get(TagFromName.SamplesPerPixel);
        int samplesPerPixel = att.getSingleIntegerValueOrDefault(1);

        if (samplesPerPixel != 1) {
            System.err.println("DICOM ERROR: Wrong SamplesPerPixel for "+photometricInterpretation+"!");
            return Integer.MAX_VALUE;
        }

        // - read data
        att = atl.get(TagFromName.Columns);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read Columns value!");
            return Integer.MAX_VALUE;
        }
        int width = att.getSingleIntegerValueOrDefault(0);
        if (width == 0) {
            System.err.println("DICOM ERROR: Cannot read Columns value!");
            return Integer.MAX_VALUE;
        }

        att = atl.get(TagFromName.Rows);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read Rows value!");
            return Integer.MAX_VALUE;
        }
        int height = att.getSingleIntegerValueOrDefault(0);
        if (height == 0) {
            System.err.println("DICOM ERROR: Cannot read Rows value!");
            return Integer.MAX_VALUE;
        }

        if ((int) Math.ceil((double)width / (double)downsize[0]) != dims[0] || (int) Math.ceil((double)height / (double)downsize[1]) != dims[1]) {
            System.err.println("ERROR: DICOM file not compatible with selected set!");
            return Integer.MAX_VALUE;
        }

        int nSliceData = width * height;

        att = atl.get(TagFromName.BitsAllocated);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read BitsAllocated!");
            return Integer.MAX_VALUE;
        }
        int bitsAllocated = att.getSingleIntegerValueOrDefault(32);
        if (bitsAllocated != 32) {
            System.err.println("DICOM ERROR: Wrong BitsAllocated!");
            return Integer.MAX_VALUE;
        }

        int rescaleIntercept = 0;
        boolean useRescaleIntercept = false;
        att = atl.get(TagFromName.RescaleIntercept);
        if (att != null) {
            rescaleIntercept = att.getSingleIntegerValueOrDefault(0);
            useRescaleIntercept = true;
        }

        double rescaleSlope = 1.0;
        boolean useRescaleSlope = false;
        att = atl.get(TagFromName.RescaleSlope);
        if (att != null) {
            rescaleSlope = att.getSingleDoubleValueOrDefault(1.0);
            useRescaleSlope = true;
        }

        int pixelPaddingValue = rescaleIntercept;
        boolean usePixelPaddingValue = false;
        att = atl.get(TagFromName.PixelPaddingValue);
        if (att != null) {
            pixelPaddingValue = att.getSingleIntegerValueOrDefault(rescaleIntercept);
            usePixelPaddingValue = true;
        }

        int pixelPaddingRangeLimit = pixelPaddingValue;
        att = atl.get(TagFromName.PixelPaddingRangeLimit);
        if (att != null) {
            pixelPaddingRangeLimit = att.getSingleIntegerValueOrDefault(pixelPaddingValue);
        }

        att = atl.get(TagFromName.PixelData);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read PixelData!");
            return Integer.MAX_VALUE;
        }

        int[] iSliceData = null;
        try {
            iSliceData = att.getIntegerValues();
        } catch (DicomException dex) {
            System.err.println("ERROR: " + dex.getMessage());
            return Integer.MAX_VALUE;
        }

        if (iSliceData == null) {
            System.err.println("DICOM ERROR: Cannot read PixelData!");
            return Integer.MAX_VALUE;
        }
        if (iSliceData.length != nSliceData) {
            System.err.println("Wrong PixelData size!");
            return Integer.MAX_VALUE;
        }

        int bgValue = Integer.MAX_VALUE;
        int v;
        for (int i = 0; i < iSliceData.length; i++) {
            v = iSliceData[i];
            if (usePixelPaddingValue) {
                if ((v < pixelPaddingValue || v > pixelPaddingRangeLimit) && v < bgValue) {
                    bgValue = v;
                }
            } else {
                if (v < bgValue) {
                    bgValue = v;
                }
            }
        }

        if (usePixelPaddingValue) {
            for (int i = 0; i < iSliceData.length; i++) {
                if (iSliceData[i] >= pixelPaddingValue && iSliceData[i] <= pixelPaddingRangeLimit) {
                    iSliceData[i] = bgValue;
                }
            }
        }

        if (useRescaleSlope) {
            for (int i = 0; i < iSliceData.length; i++) {
                iSliceData[i] = (int) Math.round((double) iSliceData[i] * rescaleSlope + (double) rescaleIntercept);
            }
            bgValue = (int) Math.round((double) bgValue * rescaleSlope + (double) rescaleIntercept);
        } else if (useRescaleIntercept && !useRescaleSlope) {
            for (int i = 0; i < iSliceData.length; i++) {
                iSliceData[i] = iSliceData[i] + rescaleIntercept;
            }
            bgValue = bgValue + rescaleIntercept;
        }

        if(photometricInterpretation.equals("MONOCHROME1")) {
            int maxValue = Integer.MIN_VALUE;
            for (int i = 0; i < iSliceData.length; i++) {
                if(iSliceData[i] > maxValue) maxValue = iSliceData[i];
            }

            for (int i = 0; i < iSliceData.length; i++) {
                iSliceData[i] = maxValue - iSliceData[i];
            }
            bgValue = maxValue - bgValue;
        }

        if (downsize[0] == 1 && downsize[1] == 1) {
            System.arraycopy(iSliceData, 0, iData, offset, iSliceData.length);
        } else {
            for (int y = 0, i = 0; y < height; y += downsize[1]) {
                for (int x = 0; x < width; x += downsize[0], i++) {
                    iData[offset + i] = iSliceData[y * width + x];
                }
            }
        }
        return bgValue;
    }

    private int readDicomArrayMonochromeFrames_32bit(DICOMSortingEntry entry, int[] iData, int[] dims, int[] downsize) {
        if (entry == null || iData == null || dims == null || downsize == null) {
            return Integer.MAX_VALUE;
        }

        AttributeList atl = null;
        try {
            DicomInputStream dis = new DicomInputStream(new File(entry.getFilePath()));
            atl = new AttributeList();
            atl.read(dis);
            dis.close();
        } catch (Exception ex) {
            System.err.println("ERROR: " + ex.getMessage());
            return Integer.MAX_VALUE;
        }

        Attribute att = atl.get(TagFromName.PhotometricInterpretation);
        if (att == null) {
            System.err.println("DICOM ERROR: Wrong PhotometricInterpretation!");
            return Integer.MAX_VALUE;
        }
        String photometricInterpretation = att.getSingleStringValueOrNull();
        if (photometricInterpretation == null || (!photometricInterpretation.equals("MONOCHROME1") && !photometricInterpretation.equals("MONOCHROME2"))) {
            System.err.println("DICOM ERROR: Wrong PhotometricInterpretation!");
            return Integer.MAX_VALUE;
        }

        att = atl.get(TagFromName.SamplesPerPixel);
        int samplesPerPixel = att.getSingleIntegerValueOrDefault(1);

        if (samplesPerPixel != 1) {
            System.err.println("DICOM ERROR: Wrong SamplesPerPixel for "+photometricInterpretation+"!");
            return Integer.MAX_VALUE;
        }

        // - read data
        att = atl.get(TagFromName.Columns);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read Columns value!");
            return Integer.MAX_VALUE;
        }
        int width = att.getSingleIntegerValueOrDefault(0);
        if (width == 0) {
            System.err.println("DICOM ERROR: Cannot read Columns value!");
            return Integer.MAX_VALUE;
        }

        att = atl.get(TagFromName.Rows);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read Rows value!");
            return Integer.MAX_VALUE;
        }
        int height = att.getSingleIntegerValueOrDefault(0);
        if (height == 0) {
            System.err.println("DICOM ERROR: Cannot read Rows value!");
            return Integer.MAX_VALUE;
        }

        att = atl.get(TagFromName.NumberOfFrames);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read NumberOfFrames value!");
            return Integer.MAX_VALUE;
        }

        int depth = att.getSingleIntegerValueOrDefault(0);
        if (depth == 0) {
            System.err.println("DICOM ERROR: Cannot read NumberOfFrames value!");
            return Integer.MAX_VALUE;
        }        
        
        if ((int) Math.ceil((double)width / (double)downsize[0]) != dims[0] || (int) Math.ceil((double)height / (double)downsize[1]) != dims[1] || (depth > 1 && (int) Math.ceil((double)depth / (double)downsize[2]) != dims[2])) {
            System.err.println("ERROR: DICOM file not compatible with selected set!");
            return Integer.MAX_VALUE;
        }

        int nData = width * height * depth;

        att = atl.get(TagFromName.BitsAllocated);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read BitsAllocated!");
            return Integer.MAX_VALUE;
        }
        int bitsAllocated = att.getSingleIntegerValueOrDefault(32);
        if (bitsAllocated != 32) {
            System.err.println("DICOM ERROR: Wrong BitsAllocated!");
            return Integer.MAX_VALUE;
        }

        int rescaleIntercept = 0;
        boolean useRescaleIntercept = false;
        att = atl.get(TagFromName.RescaleIntercept);
        if (att != null) {
            rescaleIntercept = att.getSingleIntegerValueOrDefault(0);
            useRescaleIntercept = true;
        }

        double rescaleSlope = 1.0;
        boolean useRescaleSlope = false;
        att = atl.get(TagFromName.RescaleSlope);
        if (att != null) {
            rescaleSlope = att.getSingleDoubleValueOrDefault(1.0);
            useRescaleSlope = true;
        }

        int pixelPaddingValue = rescaleIntercept;
        boolean usePixelPaddingValue = false;
        att = atl.get(TagFromName.PixelPaddingValue);
        if (att != null) {
            pixelPaddingValue = att.getSingleIntegerValueOrDefault(rescaleIntercept);
            usePixelPaddingValue = true;
        }

        int pixelPaddingRangeLimit = pixelPaddingValue;
        att = atl.get(TagFromName.PixelPaddingRangeLimit);
        if (att != null) {
            pixelPaddingRangeLimit = att.getSingleIntegerValueOrDefault(pixelPaddingValue);
        }

        att = atl.get(TagFromName.PixelData);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read PixelData!");
            return Integer.MAX_VALUE;
        }

        int[] iVolumeData = null;
        try {
            if(att instanceof OtherWordAttributeOnDisk) {
                long off = ((OtherWordAttributeOnDisk)att).getByteOffset();
                boolean bigEndian = ((OtherWordAttributeOnDisk)att).isBigEndian();
                ((OtherWordAttributeOnDisk)att).removeValues();
                File f = ((OtherWordAttributeOnDisk)att).getFile();                
                if(f == null) {
                    f = new File(entry.getFilePath());
                }
                FileImageInputStream in = new FileImageInputStream(f);
                if(bigEndian)
                    in.setByteOrder(ByteOrder.BIG_ENDIAN);
                else
                    in.setByteOrder(ByteOrder.LITTLE_ENDIAN);
                iVolumeData = new int[nData];
                in.seek(off);
                in.readFully(iVolumeData, 0, nData);
            } else {
                iVolumeData = att.getIntegerValues();                
            }
        } catch (Exception dex) {
            System.err.println("ERROR: " + dex.getMessage());
            return Integer.MAX_VALUE;
        }

        if (iVolumeData == null) {
            System.err.println("DICOM ERROR: Cannot read PixelData!");
            return Integer.MAX_VALUE;
        }
        if (iVolumeData.length != nData) {
            System.err.println("Wrong PixelData size!");
            return Integer.MAX_VALUE;
        }

        int bgValue = Integer.MAX_VALUE;
        int v;
        for (int i = 0; i < iVolumeData.length; i++) {
            v = iVolumeData[i];
            if (usePixelPaddingValue) {
                if ((v < pixelPaddingValue || v > pixelPaddingRangeLimit) && v < bgValue) {
                    bgValue = v;
                }
            } else {
                if (v < bgValue) {
                    bgValue = v;
                }
            }
        }

        if (usePixelPaddingValue) {
            for (int i = 0; i < iVolumeData.length; i++) {
                if (iVolumeData[i] >= pixelPaddingValue && iVolumeData[i] <= pixelPaddingRangeLimit) {
                    iVolumeData[i] = bgValue;
                }
            }
        }

        if (useRescaleSlope) {
            for (int i = 0; i < iVolumeData.length; i++) {
                iVolumeData[i] = (int) Math.round((double) iVolumeData[i] * rescaleSlope + (double) rescaleIntercept);
            }
            bgValue = (int) Math.round((double) bgValue * rescaleSlope + (double) rescaleIntercept);
        } else if (useRescaleIntercept && !useRescaleSlope) {
            for (int i = 0; i < iVolumeData.length; i++) {
                iVolumeData[i] = iVolumeData[i] + rescaleIntercept;
            }
            bgValue = bgValue + rescaleIntercept;
        }

        if(photometricInterpretation.equals("MONOCHROME1")) {
            int maxValue = Integer.MIN_VALUE;
            for (int i = 0; i < iVolumeData.length; i++) {
                if(iVolumeData[i] > maxValue) maxValue = iVolumeData[i];
            }

            for (int i = 0; i < iVolumeData.length; i++) {
                iVolumeData[i] = maxValue - iVolumeData[i];
            }
            bgValue = maxValue - bgValue;
        }

        if (downsize[0] == 1 && downsize[1] == 1) {
            System.arraycopy(iVolumeData, 0, iData, 0, iVolumeData.length);
        } else {
            for (int z = 0, i = 0; z < depth; z += downsize[2]) {
                for (int y = 0; y < height; y += downsize[1]) {
                    for (int x = 0; x < width; x += downsize[0], i++) {
                        iData[i] = iVolumeData[z*height*width + y*width + x];
                    }
                }
            }
        }
        return bgValue;
    }

    private int readDicomArrayMonochromeFrames_32bit(DICOMSortingEntry entry, int[][] iData, int[] dims, int[] downsize, int framesRangeLow, int framesRangeUp) {
        if (entry == null || iData == null || dims == null || downsize == null) {
            return Integer.MAX_VALUE;
        }

        AttributeList atl = null;
        try {
            DicomInputStream dis = new DicomInputStream(new File(entry.getFilePath()));
            atl = new AttributeList();
            atl.read(dis);
            dis.close();
        } catch (Exception ex) {
            System.err.println("ERROR: " + ex.getMessage());
            return Integer.MAX_VALUE;
        }

        Attribute att = atl.get(TagFromName.PhotometricInterpretation);
        if (att == null) {
            System.err.println("DICOM ERROR: Wrong PhotometricInterpretation!");
            return Integer.MAX_VALUE;
        }
        String photometricInterpretation = att.getSingleStringValueOrNull();
        if (photometricInterpretation == null || (!photometricInterpretation.equals("MONOCHROME1") && !photometricInterpretation.equals("MONOCHROME2"))) {
            System.err.println("DICOM ERROR: Wrong PhotometricInterpretation!");
            return Integer.MAX_VALUE;
        }

        att = atl.get(TagFromName.SamplesPerPixel);
        int samplesPerPixel = att.getSingleIntegerValueOrDefault(1);

        if (samplesPerPixel != 1) {
            System.err.println("DICOM ERROR: Wrong SamplesPerPixel for "+photometricInterpretation+"!");
            return Integer.MAX_VALUE;
        }

        // - read data
        att = atl.get(TagFromName.Columns);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read Columns value!");
            return Integer.MAX_VALUE;
        }
        int width = att.getSingleIntegerValueOrDefault(0);
        if (width == 0) {
            System.err.println("DICOM ERROR: Cannot read Columns value!");
            return Integer.MAX_VALUE;
        }

        att = atl.get(TagFromName.Rows);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read Rows value!");
            return Integer.MAX_VALUE;
        }
        int height = att.getSingleIntegerValueOrDefault(0);
        if (height == 0) {
            System.err.println("DICOM ERROR: Cannot read Rows value!");
            return Integer.MAX_VALUE;
        }

        att = atl.get(TagFromName.NumberOfFrames);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read NumberOfFrames value!");
            return Integer.MAX_VALUE;
        }

        int nFrames = att.getSingleIntegerValueOrDefault(0);
        if (nFrames == 0) {
            System.err.println("DICOM ERROR: Cannot read NumberOfFrames value!");
            return Integer.MAX_VALUE;
        }        
        int nFramesToRead = framesRangeUp - framesRangeLow + 1;
        
        if ((int) Math.ceil((double)width / (double)downsize[0]) != dims[0] || (int) Math.ceil((double)height / (double)downsize[1]) != dims[1]) {
            System.err.println("ERROR: DICOM file not compatible with selected set!");
            return Integer.MAX_VALUE;
        }

        int nData = width * height;

        att = atl.get(TagFromName.BitsAllocated);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read BitsAllocated!");
            return Integer.MAX_VALUE;
        }
        int bitsAllocated = att.getSingleIntegerValueOrDefault(32);
        if (bitsAllocated != 32) {
            System.err.println("DICOM ERROR: Wrong BitsAllocated!");
            return Integer.MAX_VALUE;
        }

        int rescaleIntercept = 0;
        boolean useRescaleIntercept = false;
        att = atl.get(TagFromName.RescaleIntercept);
        if (att != null) {
            rescaleIntercept = att.getSingleIntegerValueOrDefault(0);
            useRescaleIntercept = true;
        }

        double rescaleSlope = 1.0;
        boolean useRescaleSlope = false;
        att = atl.get(TagFromName.RescaleSlope);
        if (att != null) {
            rescaleSlope = att.getSingleDoubleValueOrDefault(1.0);
            useRescaleSlope = true;
        }

        int pixelPaddingValue = rescaleIntercept;
        boolean usePixelPaddingValue = false;
        att = atl.get(TagFromName.PixelPaddingValue);
        if (att != null) {
            pixelPaddingValue = att.getSingleIntegerValueOrDefault(rescaleIntercept);
            usePixelPaddingValue = true;
        }

        int pixelPaddingRangeLimit = pixelPaddingValue;
        att = atl.get(TagFromName.PixelPaddingRangeLimit);
        if (att != null) {
            pixelPaddingRangeLimit = att.getSingleIntegerValueOrDefault(pixelPaddingValue);
        }

        att = atl.get(TagFromName.PixelData);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read PixelData!");
            return Integer.MAX_VALUE;
        }

        int[] iVolumeData = null;
        try {
            if(att instanceof OtherWordAttributeOnDisk) {
                long off = ((OtherWordAttributeOnDisk)att).getByteOffset();
                boolean bigEndian = ((OtherWordAttributeOnDisk)att).isBigEndian();
                ((OtherWordAttributeOnDisk)att).removeValues();
                File f = ((OtherWordAttributeOnDisk)att).getFile();                
                if(f == null) {
                    f = new File(entry.getFilePath());
                }
                FileImageInputStream in = new FileImageInputStream(f);
                if(bigEndian)
                    in.setByteOrder(ByteOrder.BIG_ENDIAN);
                else
                    in.setByteOrder(ByteOrder.LITTLE_ENDIAN);
                iVolumeData = new int[nData*nFrames];
                in.seek(off);
                in.readFully(iVolumeData, 0, nData*nFrames);
            } else {
                iVolumeData = att.getIntegerValues();                
            }
        } catch (Exception dex) {
            System.err.println("ERROR: " + dex.getMessage());
            return Integer.MAX_VALUE;
        }

        if (iVolumeData == null) {
            System.err.println("DICOM ERROR: Cannot read PixelData!");
            return Integer.MAX_VALUE;
        }
        if (iVolumeData.length != nData*nFrames) {
            System.err.println("Wrong PixelData size!");
            return Integer.MAX_VALUE;
        }

        int bgValue = Integer.MAX_VALUE;
        int v;
        for (int i = 0; i < iVolumeData.length; i++) {
            v = iVolumeData[i];
            if (usePixelPaddingValue) {
                if ((v < pixelPaddingValue || v > pixelPaddingRangeLimit) && v < bgValue) {
                    bgValue = v;
                }
            } else {
                if (v < bgValue) {
                    bgValue = v;
                }
            }
        }

        if (usePixelPaddingValue) {
            for (int i = 0; i < iVolumeData.length; i++) {
                if (iVolumeData[i] >= pixelPaddingValue && iVolumeData[i] <= pixelPaddingRangeLimit) {
                    iVolumeData[i] = bgValue;
                }
            }
        }

        if (useRescaleSlope) {
            for (int i = 0; i < iVolumeData.length; i++) {
                iVolumeData[i] = (int) Math.round((double) iVolumeData[i] * rescaleSlope + (double) rescaleIntercept);
            }
            bgValue = (int) Math.round((double) bgValue * rescaleSlope + (double) rescaleIntercept);
        } else if (useRescaleIntercept && !useRescaleSlope) {
            for (int i = 0; i < iVolumeData.length; i++) {
                iVolumeData[i] = iVolumeData[i] + rescaleIntercept;
            }
            bgValue = bgValue + rescaleIntercept;
        }

        if(photometricInterpretation.equals("MONOCHROME1")) {
            int maxValue = Integer.MIN_VALUE;
            for (int i = 0; i < iVolumeData.length; i++) {
                if(iVolumeData[i] > maxValue) maxValue = iVolumeData[i];
            }

            for (int i = 0; i < iVolumeData.length; i++) {
                iVolumeData[i] = maxValue - iVolumeData[i];
            }
            bgValue = maxValue - bgValue;
        }

        if (downsize[0] == 1 && downsize[1] == 1) {
            for (int n = 0; n < nFramesToRead; n++) {
                System.arraycopy(iVolumeData, (n+framesRangeLow)*nData, iData[n], 0, nData);
            }
        } else {
            for (int n = 0; n < nFramesToRead; n++) {
                for (int y = 0, i = 0; y < height; y += downsize[1]) {
                    for (int x = 0; x < width; x += downsize[0], i++) {
                        iData[n][i] = iVolumeData[(n+framesRangeLow)*nData + y*width + x];
                    }
                }
            }
        }
        return bgValue;
    }
    
    private int readDicomArrayOffsetMonochrome_Cropped(DICOMSortingEntry entry, byte[] bData, int offset, int[] dims, int[] downsize, int lowCrop, int highCrop, boolean nativeCrop) {
        if (entry == null || bData == null || dims == null || downsize == null) {
            return Integer.MAX_VALUE;
        }

        if (offset >= bData.length) {
            return Integer.MAX_VALUE;
        }

        AttributeList atl = null;
        try {
            DicomInputStream dis = new DicomInputStream(new File(entry.getFilePath()));
            atl = new AttributeList();
            atl.read(dis);
            dis.close();
        } catch (Exception ex) {
            System.err.println("ERROR: " + ex.getMessage());
            return Integer.MAX_VALUE;
        }

        Attribute att = atl.get(TagFromName.PhotometricInterpretation);
        if (att == null) {
            System.err.println("DICOM ERROR: Wrong PhotometricInterpretation!");
            return Integer.MAX_VALUE;
        }
        String photometricInterpretation = att.getSingleStringValueOrNull();
        if (photometricInterpretation == null || (!photometricInterpretation.equals("MONOCHROME1") && !photometricInterpretation.equals("MONOCHROME2"))) {
            System.err.println("DICOM ERROR: Wrong PhotometricInterpretation!");
            return Integer.MAX_VALUE;
        }

        att = atl.get(TagFromName.SamplesPerPixel);
        int samplesPerPixel = att.getSingleIntegerValueOrDefault(1);

        if (samplesPerPixel != 1) {
            System.err.println("DICOM ERROR: Wrong SamplesPerPixel for "+photometricInterpretation+"!");
            return Integer.MAX_VALUE;
        }

        // - read data
        att = atl.get(TagFromName.Columns);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read Columns value!");
            return Integer.MAX_VALUE;
        }
        int width = att.getSingleIntegerValueOrDefault(0);
        if (width == 0) {
            System.err.println("DICOM ERROR: Cannot read Columns value!");
            return Integer.MAX_VALUE;
        }

        att = atl.get(TagFromName.Rows);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read Rows value!");
            return Integer.MAX_VALUE;
        }
        int height = att.getSingleIntegerValueOrDefault(0);
        if (height == 0) {
            System.err.println("DICOM ERROR: Cannot read Rows value!");
            return Integer.MAX_VALUE;
        }

        if ((int) Math.ceil((double)width / (double)downsize[0]) != dims[0] || (int) Math.ceil((double)height / (double)downsize[1]) != dims[1]) {
            System.err.println("ERROR: DICOM file not compatible with selected set!");
            return Integer.MAX_VALUE;
        }

        int nSliceData = width * height;

        att = atl.get(TagFromName.BitsAllocated);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read BitsAllocated!");
            return Integer.MAX_VALUE;
        }
        int bitsAllocated = att.getSingleIntegerValueOrDefault(16);

        int rescaleIntercept = 0;
        boolean useRescaleIntercept = false;
        att = atl.get(TagFromName.RescaleIntercept);
        if (att != null) {
            rescaleIntercept = att.getSingleIntegerValueOrDefault(0);
            useRescaleIntercept = true;
        }

        double rescaleSlope = 1.0;
        boolean useRescaleSlope = false;
        att = atl.get(TagFromName.RescaleSlope);
        if (att != null) {
            rescaleSlope = att.getSingleDoubleValueOrDefault(1.0);
            useRescaleSlope = true;
        }

        int pixelPaddingValue = rescaleIntercept;
        boolean usePixelPaddingValue = false;
        att = atl.get(TagFromName.PixelPaddingValue);
        if (att != null) {
            pixelPaddingValue = att.getSingleIntegerValueOrDefault(rescaleIntercept);
            usePixelPaddingValue = true;
        }

        int pixelPaddingRangeLimit = pixelPaddingValue;
        att = atl.get(TagFromName.PixelPaddingRangeLimit);
        if (att != null) {
            pixelPaddingRangeLimit = att.getSingleIntegerValueOrDefault(pixelPaddingValue);
        }

        att = atl.get(TagFromName.PixelData);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read PixelData!");
            return Integer.MAX_VALUE;
        }

        int bgValue = Integer.MAX_VALUE;
        int v;
        float s = 255.0f / (float) (highCrop - lowCrop);

        switch (bitsAllocated) {
            case 8:
                byte[] bSliceData = null;
                try {
                    bSliceData = att.getByteValues();
                } catch (DicomException dex) {
                    System.err.println("ERROR: " + dex.getMessage());
                    return Integer.MAX_VALUE;
                }

                if (bSliceData == null) {
                    System.err.println("DICOM ERROR: Cannot read PixelData!");
                    return Integer.MAX_VALUE;
                }
                if (bSliceData.length != nSliceData) {
                    System.err.println("Wrong PixelData size!");
                    return Integer.MAX_VALUE;
                }

                for (int i = 0; i < bSliceData.length; i++) {
                    v = (int) (bSliceData[i] & 0xff);
                    if (usePixelPaddingValue) {
                        if ((v < pixelPaddingValue || v > pixelPaddingRangeLimit) && v < bgValue) {
                            bgValue = v;
                        }
                    } else {
                        if (v < bgValue) {
                            bgValue = v;
                        }
                    }
                }

                if (usePixelPaddingValue) {
                    for (int i = 0; i < bSliceData.length; i++) {
                        if ((int) (bSliceData[i] & 0xff) >= pixelPaddingValue && (int) (bSliceData[i] & 0xff) <= pixelPaddingRangeLimit) {
                            bSliceData[i] = (byte) bgValue;
                        }
                    }
                }

                if (!nativeCrop) {
                    if (useRescaleSlope) {
                        for (int i = 0; i < bSliceData.length; i++) {
                            bSliceData[i] = (byte) Math.round((double) (bSliceData[i] & 0xff) * rescaleSlope + (double) rescaleIntercept);
                        }
                        bgValue = (int) Math.round((double) bgValue * rescaleSlope + (double) rescaleIntercept);
                    } else if (useRescaleIntercept && !useRescaleSlope) {
                        for (int i = 0; i < bSliceData.length; i++) {
                            bSliceData[i] = (byte) ((bSliceData[i] & 0xff) + rescaleIntercept);
                        }
                        bgValue = bgValue + rescaleIntercept;
                    }
                }

                if(photometricInterpretation.equals("MONOCHROME1")) {
                    int maxValue = Integer.MIN_VALUE;
                    for (int i = 0; i < bSliceData.length; i++) {
                        if((int)(bSliceData[i]&0xff) > maxValue) maxValue = (int)(bSliceData[i]&0xff);
                    }

                    for (int i = 0; i < bSliceData.length; i++) {
                        bSliceData[i] = (byte)(maxValue - (int)(bSliceData[i]&0xff));
                    }
                    bgValue = maxValue - bgValue;
                }

                for (int y = 0, i = 0; y < height; y += downsize[1]) {
                    for (int x = 0; x < width; x += downsize[0], i++) {
                        v = (int) (bSliceData[y * width + x] & 0xff);
                        v = (int) ((v - lowCrop) * s);
                        if (v < 0) {
                            v = 0;
                        }
                        if (v > 255) {
                            v = 255;
                        }
                        bData[offset + i] = (byte) v;
                        //bData[offset + i] = (byte)Math.round(((float)(bSliceData[y*width + x]&0xff) - (float)lowCrop)*s);
                    }
                }
                break;
            case 32:
                int[] iSliceData = null;
                try {
                    iSliceData = att.getIntegerValues();
                } catch (DicomException dex) {
                    System.err.println("ERROR: " + dex.getMessage());
                    return Integer.MAX_VALUE;
                }

                if (iSliceData == null) {
                    System.err.println("DICOM ERROR: Cannot read PixelData!");
                    return Integer.MAX_VALUE;
                }
                if (iSliceData.length != nSliceData) {
                    System.err.println("Wrong PixelData size!");
                    return Integer.MAX_VALUE;
                }

                for (int i = 0; i < iSliceData.length; i++) {
                    v = iSliceData[i];
                    if (usePixelPaddingValue) {
                        if ((v < pixelPaddingValue || v > pixelPaddingRangeLimit) && v < bgValue) {
                            bgValue = v;
                        }
                    } else {
                        if (v < bgValue) {
                            bgValue = v;
                        }
                    }
                }

                if (usePixelPaddingValue) {
                    for (int i = 0; i < iSliceData.length; i++) {
                        if (iSliceData[i] >= pixelPaddingValue && iSliceData[i] <= pixelPaddingRangeLimit) {
                            iSliceData[i] = bgValue;
                        }
                    }
                }

                if (!nativeCrop) {
                    if (useRescaleSlope) {
                        for (int i = 0; i < iSliceData.length; i++) {
                            iSliceData[i] = (int) Math.round((double) iSliceData[i] * rescaleSlope + (double) rescaleIntercept);
                        }
                        bgValue = (int) Math.round((double) bgValue * rescaleSlope + (double) rescaleIntercept);
                    } else if (useRescaleIntercept && !useRescaleSlope) {
                        for (int i = 0; i < iSliceData.length; i++) {
                            iSliceData[i] = iSliceData[i] + rescaleIntercept;
                        }
                        bgValue = bgValue + rescaleIntercept;
                    }
                }

                if(photometricInterpretation.equals("MONOCHROME1")) {
                    int maxValue = Integer.MIN_VALUE;
                    for (int i = 0; i < iSliceData.length; i++) {
                        if(iSliceData[i] > maxValue) maxValue = iSliceData[i];
                    }

                    for (int i = 0; i < iSliceData.length; i++) {
                        iSliceData[i] = maxValue - iSliceData[i];
                    }
                    bgValue = maxValue - bgValue;
                }

                for (int y = 0, i = 0; y < height; y += downsize[1]) {
                    for (int x = 0; x < width; x += downsize[0], i++) {
                        v = iSliceData[y * width + x];
                        v = (int) ((v - lowCrop) * s);
                        if (v < 0) {
                            v = 0;
                        }
                        if (v > 255) {
                            v = 255;
                        }
                        bData[offset + i] = (byte) v;
                        //bData[offset + i] = (byte)Math.round((float)(iSliceData[y*width + x] - lowCrop)*s);
                    }
                }
                break;
            default:
                short[] sSliceData = null;
                try {
                    sSliceData = att.getShortValues();
                } catch (DicomException dex) {
                    System.err.println("ERROR: " + dex.getMessage());
                    return Integer.MAX_VALUE;
                }

                if (sSliceData == null) {
                    System.err.println("DICOM ERROR: Cannot read PixelData!");
                    return Integer.MAX_VALUE;
                }
                if (sSliceData.length != nSliceData) {
                    System.err.println("Wrong PixelData size!");
                    return Integer.MAX_VALUE;
                }

                for (int i = 0; i < sSliceData.length; i++) {
                    v = (int) sSliceData[i];
                    if (usePixelPaddingValue) {
                        if ((v < pixelPaddingValue || v > pixelPaddingRangeLimit) && v < bgValue) {
                            bgValue = v;
                        }
                    } else {
                        if (v < bgValue) {
                            bgValue = v;
                        }
                    }
                }

                if (usePixelPaddingValue) {
                    for (int i = 0; i < sSliceData.length; i++) {
                        if ((int) sSliceData[i] >= pixelPaddingValue && (int) sSliceData[i] <= pixelPaddingRangeLimit) {
                            sSliceData[i] = (short) bgValue;
                        }
                    }
                }

                if (!nativeCrop) {
                    if (useRescaleSlope) {
                        for (int i = 0; i < sSliceData.length; i++) {
                            sSliceData[i] = (short) Math.round((double) sSliceData[i] * rescaleSlope + (double) rescaleIntercept);
                        }
                        bgValue = (int) Math.round((double) bgValue * rescaleSlope + (double) rescaleIntercept);
                    } else if (useRescaleIntercept && !useRescaleSlope) {
                        for (int i = 0; i < sSliceData.length; i++) {
                            sSliceData[i] = (short) (sSliceData[i] + rescaleIntercept);
                        }
                        bgValue = bgValue + rescaleIntercept;
                    }
                }

                if(photometricInterpretation.equals("MONOCHROME1")) {
                    short maxValue = Short.MIN_VALUE;
                    for (int i = 0; i < sSliceData.length; i++) {
                        if(sSliceData[i] > maxValue) maxValue = sSliceData[i];
                    }

                    for (int i = 0; i < sSliceData.length; i++) {
                        sSliceData[i] = (short)(maxValue - sSliceData[i]);
                    }
                    bgValue = maxValue - bgValue;
                }

                for (int y = 0, i = 0; y < height; y += downsize[1]) {
                    for (int x = 0; x < width; x += downsize[0], i++) {
                        v = (int) sSliceData[y * width + x];
                        v = (int) ((v - lowCrop) * s);
                        if (v < 0) {
                            v = 0;
                        }
                        if (v > 255) {
                            v = 255;
                        }
                        bData[offset + i] = (byte) v;
                        //bData[offset + i] = (byte)Math.round((float)(sSliceData[y*width + x] - lowCrop)*s);
                    }
                }
                break;
        }
        return bgValue;
    }

    private int readDicomArrayMonochromeFrames_Cropped(DICOMSortingEntry entry, byte[] bData, int[] dims, int[] downsize, int lowCrop, int highCrop, boolean nativeCrop) {
        if (entry == null || bData == null || dims == null || downsize == null) {
            return Integer.MAX_VALUE;
        }

        AttributeList atl = null;
        try {
            DicomInputStream dis = new DicomInputStream(new File(entry.getFilePath()));
            atl = new AttributeList();
            atl.read(dis);
            dis.close();
        } catch (Exception ex) {
            System.err.println("ERROR: " + ex.getMessage());
            return Integer.MAX_VALUE;
        }

        Attribute att = atl.get(TagFromName.PhotometricInterpretation);
        if (att == null) {
            System.err.println("DICOM ERROR: Wrong PhotometricInterpretation!");
            return Integer.MAX_VALUE;
        }
        String photometricInterpretation = att.getSingleStringValueOrNull();
        if (photometricInterpretation == null || (!photometricInterpretation.equals("MONOCHROME1") && !photometricInterpretation.equals("MONOCHROME2"))) {
            System.err.println("DICOM ERROR: Wrong PhotometricInterpretation!");
            return Integer.MAX_VALUE;
        }

        att = atl.get(TagFromName.SamplesPerPixel);
        int samplesPerPixel = att.getSingleIntegerValueOrDefault(1);

        if (samplesPerPixel != 1) {
            System.err.println("DICOM ERROR: Wrong SamplesPerPixel for "+photometricInterpretation+"!");
            return Integer.MAX_VALUE;
        }

        // - read data
        att = atl.get(TagFromName.Columns);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read Columns value!");
            return Integer.MAX_VALUE;
        }
        int width = att.getSingleIntegerValueOrDefault(0);
        if (width == 0) {
            System.err.println("DICOM ERROR: Cannot read Columns value!");
            return Integer.MAX_VALUE;
        }

        att = atl.get(TagFromName.Rows);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read Rows value!");
            return Integer.MAX_VALUE;
        }
        int height = att.getSingleIntegerValueOrDefault(0);
        if (height == 0) {
            System.err.println("DICOM ERROR: Cannot read Rows value!");
            return Integer.MAX_VALUE;
        }

        att = atl.get(TagFromName.NumberOfFrames);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read NumberOfFrames value!");
            return Integer.MAX_VALUE;
        }

        int depth = att.getSingleIntegerValueOrDefault(0);
        if (depth == 0) {
            System.err.println("DICOM ERROR: Cannot read NumberOfFrames value!");
            return Integer.MAX_VALUE;
        }        
        
        if ((int) Math.ceil((double)width / (double)downsize[0]) != dims[0] || (int) Math.ceil((double)height / (double)downsize[1]) != dims[1] || (depth > 1 && (int) Math.ceil((double)depth / (double)downsize[2]) != dims[2])) {
            System.err.println("ERROR: DICOM file not compatible with selected set!");
            return Integer.MAX_VALUE;
        }

        int nData = width * height * depth;

        att = atl.get(TagFromName.BitsAllocated);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read BitsAllocated!");
            return Integer.MAX_VALUE;
        }
        int bitsAllocated = att.getSingleIntegerValueOrDefault(16);

        int rescaleIntercept = 0;
        boolean useRescaleIntercept = false;
        att = atl.get(TagFromName.RescaleIntercept);
        if (att != null) {
            rescaleIntercept = att.getSingleIntegerValueOrDefault(0);
            useRescaleIntercept = true;
        }

        double rescaleSlope = 1.0;
        boolean useRescaleSlope = false;
        att = atl.get(TagFromName.RescaleSlope);
        if (att != null) {
            rescaleSlope = att.getSingleDoubleValueOrDefault(1.0);
            useRescaleSlope = true;
        }

        int pixelPaddingValue = rescaleIntercept;
        boolean usePixelPaddingValue = false;
        att = atl.get(TagFromName.PixelPaddingValue);
        if (att != null) {
            pixelPaddingValue = att.getSingleIntegerValueOrDefault(rescaleIntercept);
            usePixelPaddingValue = true;
        }

        int pixelPaddingRangeLimit = pixelPaddingValue;
        att = atl.get(TagFromName.PixelPaddingRangeLimit);
        if (att != null) {
            pixelPaddingRangeLimit = att.getSingleIntegerValueOrDefault(pixelPaddingValue);
        }

        att = atl.get(TagFromName.PixelData);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read PixelData!");
            return Integer.MAX_VALUE;
        }

        int bgValue = Integer.MAX_VALUE;
        int v;
        float s = 255.0f / (float) (highCrop - lowCrop);

        switch (bitsAllocated) {
            case 8:
                byte[] bVolumeData = null;
                try {
                    if(att instanceof OtherWordAttributeOnDisk) {
                        long off = ((OtherWordAttributeOnDisk)att).getByteOffset();
                        boolean bigEndian = ((OtherWordAttributeOnDisk)att).isBigEndian();
                        ((OtherWordAttributeOnDisk)att).removeValues();
                        File f = ((OtherWordAttributeOnDisk)att).getFile();                
                        if(f == null) {
                            f = new File(entry.getFilePath());
                        }
                        FileImageInputStream in = new FileImageInputStream(f);
                        if(bigEndian)
                            in.setByteOrder(ByteOrder.BIG_ENDIAN);
                        else
                            in.setByteOrder(ByteOrder.LITTLE_ENDIAN);
                        bVolumeData = new byte[nData];
                        in.seek(off);
                        in.readFully(bVolumeData, 0, nData);
                    } else {
                        bVolumeData = att.getByteValues();                
                    }
                } catch (Exception ex) {
                    System.err.println("ERROR: " + ex.getMessage());
                    return Integer.MAX_VALUE;
                }

                if (bVolumeData == null) {
                    System.err.println("DICOM ERROR: Cannot read PixelData!");
                    return Integer.MAX_VALUE;
                }
                if (bVolumeData.length != nData) {
                    System.err.println("Wrong PixelData size!");
                    return Integer.MAX_VALUE;
                }

                for (int i = 0; i < bVolumeData.length; i++) {
                    v = (int) (bVolumeData[i] & 0xff);
                    if (usePixelPaddingValue) {
                        if ((v < pixelPaddingValue || v > pixelPaddingRangeLimit) && v < bgValue) {
                            bgValue = v;
                        }
                    } else {
                        if (v < bgValue) {
                            bgValue = v;
                        }
                    }
                }

                if (usePixelPaddingValue) {
                    for (int i = 0; i < bVolumeData.length; i++) {
                        if ((int) (bVolumeData[i] & 0xff) >= pixelPaddingValue && (int) (bVolumeData[i] & 0xff) <= pixelPaddingRangeLimit) {
                            bVolumeData[i] = (byte) bgValue;
                        }
                    }
                }

                if (!nativeCrop) {
                    if (useRescaleSlope) {
                        for (int i = 0; i < bVolumeData.length; i++) {
                            bVolumeData[i] = (byte) Math.round((double) (bVolumeData[i] & 0xff) * rescaleSlope + (double) rescaleIntercept);
                        }
                        bgValue = (int) Math.round((double) bgValue * rescaleSlope + (double) rescaleIntercept);
                    } else if (useRescaleIntercept && !useRescaleSlope) {
                        for (int i = 0; i < bVolumeData.length; i++) {
                            bVolumeData[i] = (byte) ((bVolumeData[i] & 0xff) + rescaleIntercept);
                        }
                        bgValue = bgValue + rescaleIntercept;
                    }
                }

                if(photometricInterpretation.equals("MONOCHROME1")) {
                    int maxValue = Integer.MIN_VALUE;
                    for (int i = 0; i < bVolumeData.length; i++) {
                        if((int)(bVolumeData[i]&0xff) > maxValue) maxValue = (int)(bVolumeData[i]&0xff);
                    }

                    for (int i = 0; i < bVolumeData.length; i++) {
                        bVolumeData[i] = (byte)(maxValue - (int)(bVolumeData[i]&0xff));
                    }
                    bgValue = maxValue - bgValue;
                }

                for (int z = 0, i = 0; z < depth; z += downsize[2]) {
                    for (int y = 0; y < height; y += downsize[1]) {
                        for (int x = 0; x < width; x += downsize[0], i++) {
                            v = (int) (bVolumeData[z*width*height + y*width + x] & 0xff);
                            v = (int) ((v - lowCrop) * s);
                            if (v < 0) {
                                v = 0;
                            }
                            if (v > 255) {
                                v = 255;
                            }
                            bData[i] = (byte) v;
                            //bData[offset + i] = (byte)Math.round(((float)(bSliceData[y*width + x]&0xff) - (float)lowCrop)*s);
                        }
                    }
                }
                break;
            case 32:
                int[] iVolumeData = null;
                try {
                    if(att instanceof OtherWordAttributeOnDisk) {
                        long off = ((OtherWordAttributeOnDisk)att).getByteOffset();
                        boolean bigEndian = ((OtherWordAttributeOnDisk)att).isBigEndian();
                        ((OtherWordAttributeOnDisk)att).removeValues();
                        File f = ((OtherWordAttributeOnDisk)att).getFile();                
                        if(f == null) {
                            f = new File(entry.getFilePath());
                        }
                        FileImageInputStream in = new FileImageInputStream(f);
                        if(bigEndian)
                            in.setByteOrder(ByteOrder.BIG_ENDIAN);
                        else
                            in.setByteOrder(ByteOrder.LITTLE_ENDIAN);
                        iVolumeData = new int[nData];
                        in.seek(off);
                        in.readFully(iVolumeData, 0, nData);
                    } else {
                        iVolumeData = att.getIntegerValues();                
                    }
                } catch (Exception ex) {
                    System.err.println("ERROR: " + ex.getMessage());
                    return Integer.MAX_VALUE;
                }

                if (iVolumeData == null) {
                    System.err.println("DICOM ERROR: Cannot read PixelData!");
                    return Integer.MAX_VALUE;
                }
                if (iVolumeData.length != nData) {
                    System.err.println("Wrong PixelData size!");
                    return Integer.MAX_VALUE;
                }

                for (int i = 0; i < iVolumeData.length; i++) {
                    v = iVolumeData[i];
                    if (usePixelPaddingValue) {
                        if ((v < pixelPaddingValue || v > pixelPaddingRangeLimit) && v < bgValue) {
                            bgValue = v;
                        }
                    } else {
                        if (v < bgValue) {
                            bgValue = v;
                        }
                    }
                }

                if (usePixelPaddingValue) {
                    for (int i = 0; i < iVolumeData.length; i++) {
                        if (iVolumeData[i] >= pixelPaddingValue && iVolumeData[i] <= pixelPaddingRangeLimit) {
                            iVolumeData[i] = bgValue;
                        }
                    }
                }

                if (!nativeCrop) {
                    if (useRescaleSlope) {
                        for (int i = 0; i < iVolumeData.length; i++) {
                            iVolumeData[i] = (int) Math.round((double) iVolumeData[i] * rescaleSlope + (double) rescaleIntercept);
                        }
                        bgValue = (int) Math.round((double) bgValue * rescaleSlope + (double) rescaleIntercept);
                    } else if (useRescaleIntercept && !useRescaleSlope) {
                        for (int i = 0; i < iVolumeData.length; i++) {
                            iVolumeData[i] = iVolumeData[i] + rescaleIntercept;
                        }
                        bgValue = bgValue + rescaleIntercept;
                    }
                }

                if(photometricInterpretation.equals("MONOCHROME1")) {
                    int maxValue = Integer.MIN_VALUE;
                    for (int i = 0; i < iVolumeData.length; i++) {
                        if(iVolumeData[i] > maxValue) maxValue = iVolumeData[i];
                    }

                    for (int i = 0; i < iVolumeData.length; i++) {
                        iVolumeData[i] = maxValue - iVolumeData[i];
                    }
                    bgValue = maxValue - bgValue;
                }

                for (int z = 0, i = 0; z < depth; z += downsize[2]) {
                    for (int y = 0; y < height; y += downsize[1]) {
                        for (int x = 0; x < width; x += downsize[0], i++) {
                            v = iVolumeData[z*width*height + y*width + x];
                            v = (int) ((v - lowCrop) * s);
                            if (v < 0) {
                                v = 0;
                            }
                            if (v > 255) {
                                v = 255;
                            }
                            bData[i] = (byte) v;
                            //bData[offset + i] = (byte)Math.round((float)(iSliceData[y*width + x] - lowCrop)*s);
                        }
                    }
                }
                break;
            default:
                short[] sVolumeData = null;
                try {
                    if(att instanceof OtherWordAttributeOnDisk) {
                        long off = ((OtherWordAttributeOnDisk)att).getByteOffset();
                        boolean bigEndian = ((OtherWordAttributeOnDisk)att).isBigEndian();
                        ((OtherWordAttributeOnDisk)att).removeValues();
                        File f = ((OtherWordAttributeOnDisk)att).getFile();                
                        if(f == null) {
                            f = new File(entry.getFilePath());
                        }
                        FileImageInputStream in = new FileImageInputStream(f);
                        if(bigEndian)
                            in.setByteOrder(ByteOrder.BIG_ENDIAN);
                        else
                            in.setByteOrder(ByteOrder.LITTLE_ENDIAN);
                        sVolumeData = new short[nData];
                        in.seek(off);
                        in.readFully(sVolumeData, 0, nData);
                    } else {
                        sVolumeData = att.getShortValues();                
                    }
                } catch (Exception ex) {
                    System.err.println("ERROR: " + ex.getMessage());
                    return Integer.MAX_VALUE;
                }

                if (sVolumeData == null) {
                    System.err.println("DICOM ERROR: Cannot read PixelData!");
                    return Integer.MAX_VALUE;
                }
                if (sVolumeData.length != nData) {
                    System.err.println("Wrong PixelData size!");
                    return Integer.MAX_VALUE;
                }

                for (int i = 0; i < sVolumeData.length; i++) {
                    v = (int) sVolumeData[i];
                    if (usePixelPaddingValue) {
                        if ((v < pixelPaddingValue || v > pixelPaddingRangeLimit) && v < bgValue) {
                            bgValue = v;
                        }
                    } else {
                        if (v < bgValue) {
                            bgValue = v;
                        }
                    }
                }

                if (usePixelPaddingValue) {
                    for (int i = 0; i < sVolumeData.length; i++) {
                        if ((int) sVolumeData[i] >= pixelPaddingValue && (int) sVolumeData[i] <= pixelPaddingRangeLimit) {
                            sVolumeData[i] = (short) bgValue;
                        }
                    }
                }

                if (!nativeCrop) {
                    if (useRescaleSlope) {
                        for (int i = 0; i < sVolumeData.length; i++) {
                            sVolumeData[i] = (short) Math.round((double) sVolumeData[i] * rescaleSlope + (double) rescaleIntercept);
                        }
                        bgValue = (int) Math.round((double) bgValue * rescaleSlope + (double) rescaleIntercept);
                    } else if (useRescaleIntercept && !useRescaleSlope) {
                        for (int i = 0; i < sVolumeData.length; i++) {
                            sVolumeData[i] = (short) (sVolumeData[i] + rescaleIntercept);
                        }
                        bgValue = bgValue + rescaleIntercept;
                    }
                }

                if(photometricInterpretation.equals("MONOCHROME1")) {
                    short maxValue = Short.MIN_VALUE;
                    for (int i = 0; i < sVolumeData.length; i++) {
                        if(sVolumeData[i] > maxValue) maxValue = sVolumeData[i];
                    }

                    for (int i = 0; i < sVolumeData.length; i++) {
                        sVolumeData[i] = (short)(maxValue - sVolumeData[i]);
                    }
                    bgValue = maxValue - bgValue;
                }

                for (int z = 0, i = 0; z < depth; z += downsize[2]) {
                    for (int y = 0; y < height; y += downsize[1]) {
                        for (int x = 0; x < width; x += downsize[0], i++) {
                            v = (int) sVolumeData[z*width*height + y*width + x];
                            v = (int) ((v - lowCrop) * s);
                            if (v < 0) {
                                v = 0;
                            }
                            if (v > 255) {
                                v = 255;
                            }
                            bData[i] = (byte) v;
                            //bData[offset + i] = (byte)Math.round((float)(sSliceData[y*width + x] - lowCrop)*s);
                        }
                    }
                }
                break;
        }
        return bgValue;
    }

    private int readDicomArrayMonochromeFrames_Cropped(DICOMSortingEntry entry, byte[][] bData, int[] dims, int[] downsize, int lowCrop, int highCrop, boolean nativeCrop, int framesRangeLow, int framesRangeUp) {
        if (entry == null || bData == null || dims == null || downsize == null) {
            return Integer.MAX_VALUE;
        }

        AttributeList atl = null;
        try {
            DicomInputStream dis = new DicomInputStream(new File(entry.getFilePath()));
            atl = new AttributeList();
            atl.read(dis);
            dis.close();
        } catch (Exception ex) {
            System.err.println("ERROR: " + ex.getMessage());
            return Integer.MAX_VALUE;
        }

        Attribute att = atl.get(TagFromName.PhotometricInterpretation);
        if (att == null) {
            System.err.println("DICOM ERROR: Wrong PhotometricInterpretation!");
            return Integer.MAX_VALUE;
        }
        String photometricInterpretation = att.getSingleStringValueOrNull();
        if (photometricInterpretation == null || (!photometricInterpretation.equals("MONOCHROME1") && !photometricInterpretation.equals("MONOCHROME2"))) {
            System.err.println("DICOM ERROR: Wrong PhotometricInterpretation!");
            return Integer.MAX_VALUE;
        }

        att = atl.get(TagFromName.SamplesPerPixel);
        int samplesPerPixel = att.getSingleIntegerValueOrDefault(1);

        if (samplesPerPixel != 1) {
            System.err.println("DICOM ERROR: Wrong SamplesPerPixel for "+photometricInterpretation+"!");
            return Integer.MAX_VALUE;
        }

        // - read data
        att = atl.get(TagFromName.Columns);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read Columns value!");
            return Integer.MAX_VALUE;
        }
        int width = att.getSingleIntegerValueOrDefault(0);
        if (width == 0) {
            System.err.println("DICOM ERROR: Cannot read Columns value!");
            return Integer.MAX_VALUE;
        }

        att = atl.get(TagFromName.Rows);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read Rows value!");
            return Integer.MAX_VALUE;
        }
        int height = att.getSingleIntegerValueOrDefault(0);
        if (height == 0) {
            System.err.println("DICOM ERROR: Cannot read Rows value!");
            return Integer.MAX_VALUE;
        }

        att = atl.get(TagFromName.NumberOfFrames);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read NumberOfFrames value!");
            return Integer.MAX_VALUE;
        }

        int nFrames = att.getSingleIntegerValueOrDefault(0);
        if (nFrames == 0) {
            System.err.println("DICOM ERROR: Cannot read NumberOfFrames value!");
            return Integer.MAX_VALUE;
        }     
        int nFramesToRead = framesRangeUp - framesRangeLow + 1;
        
        if ((int) Math.ceil((double)width / (double)downsize[0]) != dims[0] || (int) Math.ceil((double)height / (double)downsize[1]) != dims[1]) {
            System.err.println("ERROR: DICOM file not compatible with selected set!");
            return Integer.MAX_VALUE;
        }

        int nData = width * height;

        att = atl.get(TagFromName.BitsAllocated);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read BitsAllocated!");
            return Integer.MAX_VALUE;
        }
        int bitsAllocated = att.getSingleIntegerValueOrDefault(16);

        int rescaleIntercept = 0;
        boolean useRescaleIntercept = false;
        att = atl.get(TagFromName.RescaleIntercept);
        if (att != null) {
            rescaleIntercept = att.getSingleIntegerValueOrDefault(0);
            useRescaleIntercept = true;
        }

        double rescaleSlope = 1.0;
        boolean useRescaleSlope = false;
        att = atl.get(TagFromName.RescaleSlope);
        if (att != null) {
            rescaleSlope = att.getSingleDoubleValueOrDefault(1.0);
            useRescaleSlope = true;
        }

        int pixelPaddingValue = rescaleIntercept;
        boolean usePixelPaddingValue = false;
        att = atl.get(TagFromName.PixelPaddingValue);
        if (att != null) {
            pixelPaddingValue = att.getSingleIntegerValueOrDefault(rescaleIntercept);
            usePixelPaddingValue = true;
        }

        int pixelPaddingRangeLimit = pixelPaddingValue;
        att = atl.get(TagFromName.PixelPaddingRangeLimit);
        if (att != null) {
            pixelPaddingRangeLimit = att.getSingleIntegerValueOrDefault(pixelPaddingValue);
        }

        att = atl.get(TagFromName.PixelData);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read PixelData!");
            return Integer.MAX_VALUE;
        }

        int bgValue = Integer.MAX_VALUE;
        int v;
        float s = 255.0f / (float) (highCrop - lowCrop);

        switch (bitsAllocated) {
            case 8:
                byte[] bVolumeData = null;
                try {
                    if(att instanceof OtherWordAttributeOnDisk) {
                        long off = ((OtherWordAttributeOnDisk)att).getByteOffset();
                        boolean bigEndian = ((OtherWordAttributeOnDisk)att).isBigEndian();
                        ((OtherWordAttributeOnDisk)att).removeValues();
                        File f = ((OtherWordAttributeOnDisk)att).getFile();                
                        if(f == null) {
                            f = new File(entry.getFilePath());
                        }
                        FileImageInputStream in = new FileImageInputStream(f);
                        if(bigEndian)
                            in.setByteOrder(ByteOrder.BIG_ENDIAN);
                        else
                            in.setByteOrder(ByteOrder.LITTLE_ENDIAN);
                        bVolumeData = new byte[nData*nFrames];
                        in.seek(off);
                        in.readFully(bVolumeData, 0, nData*nFrames);
                    } else {
                        bVolumeData = att.getByteValues();                
                    }
                } catch (Exception ex) {
                    System.err.println("ERROR: " + ex.getMessage());
                    return Integer.MAX_VALUE;
                }

                if (bVolumeData == null) {
                    System.err.println("DICOM ERROR: Cannot read PixelData!");
                    return Integer.MAX_VALUE;
                }
                if (bVolumeData.length != nData*nFrames) {
                    System.err.println("Wrong PixelData size!");
                    return Integer.MAX_VALUE;
                }

                for (int i = 0; i < bVolumeData.length; i++) {
                    v = (int) (bVolumeData[i] & 0xff);
                    if (usePixelPaddingValue) {
                        if ((v < pixelPaddingValue || v > pixelPaddingRangeLimit) && v < bgValue) {
                            bgValue = v;
                        }
                    } else {
                        if (v < bgValue) {
                            bgValue = v;
                        }
                    }
                }

                if (usePixelPaddingValue) {
                    for (int i = 0; i < bVolumeData.length; i++) {
                        if ((int) (bVolumeData[i] & 0xff) >= pixelPaddingValue && (int) (bVolumeData[i] & 0xff) <= pixelPaddingRangeLimit) {
                            bVolumeData[i] = (byte) bgValue;
                        }
                    }
                }

                if (!nativeCrop) {
                    if (useRescaleSlope) {
                        for (int i = 0; i < bVolumeData.length; i++) {
                            bVolumeData[i] = (byte) Math.round((double) (bVolumeData[i] & 0xff) * rescaleSlope + (double) rescaleIntercept);
                        }
                        bgValue = (int) Math.round((double) bgValue * rescaleSlope + (double) rescaleIntercept);
                    } else if (useRescaleIntercept && !useRescaleSlope) {
                        for (int i = 0; i < bVolumeData.length; i++) {
                            bVolumeData[i] = (byte) ((bVolumeData[i] & 0xff) + rescaleIntercept);
                        }
                        bgValue = bgValue + rescaleIntercept;
                    }
                }

                if(photometricInterpretation.equals("MONOCHROME1")) {
                    int maxValue = Integer.MIN_VALUE;
                    for (int i = 0; i < bVolumeData.length; i++) {
                        if((int)(bVolumeData[i]&0xff) > maxValue) maxValue = (int)(bVolumeData[i]&0xff);
                    }

                    for (int i = 0; i < bVolumeData.length; i++) {
                        bVolumeData[i] = (byte)(maxValue - (int)(bVolumeData[i]&0xff));
                    }
                    bgValue = maxValue - bgValue;
                }

                for (int n = 0, i = 0; n < nFramesToRead; n++) {
                    for (int y = 0; y < height; y += downsize[1]) {
                        for (int x = 0; x < width; x += downsize[0], i++) {
                            v = (int) (bVolumeData[(n+framesRangeLow)*nData + y*width + x] & 0xff);
                            v = (int) ((v - lowCrop) * s);
                            if (v < 0) {
                                v = 0;
                            }
                            if (v > 255) {
                                v = 255;
                            }
                            bData[n][i] = (byte) v;
                        }
                    }
                }
                break;
            case 32:
                int[] iVolumeData = null;
                try {
                    if(att instanceof OtherWordAttributeOnDisk) {
                        long off = ((OtherWordAttributeOnDisk)att).getByteOffset();
                        boolean bigEndian = ((OtherWordAttributeOnDisk)att).isBigEndian();
                        ((OtherWordAttributeOnDisk)att).removeValues();
                        File f = ((OtherWordAttributeOnDisk)att).getFile();                
                        if(f == null) {
                            f = new File(entry.getFilePath());
                        }
                        FileImageInputStream in = new FileImageInputStream(f);
                        if(bigEndian)
                            in.setByteOrder(ByteOrder.BIG_ENDIAN);
                        else
                            in.setByteOrder(ByteOrder.LITTLE_ENDIAN);
                        iVolumeData = new int[nData*nFrames];
                        in.seek(off);
                        in.readFully(iVolumeData, 0, nData*nFrames);
                    } else {
                        iVolumeData = att.getIntegerValues();                
                    }
                } catch (Exception ex) {
                    System.err.println("ERROR: " + ex.getMessage());
                    return Integer.MAX_VALUE;
                }

                if (iVolumeData == null) {
                    System.err.println("DICOM ERROR: Cannot read PixelData!");
                    return Integer.MAX_VALUE;
                }
                if (iVolumeData.length != nData*nFrames) {
                    System.err.println("Wrong PixelData size!");
                    return Integer.MAX_VALUE;
                }

                for (int i = 0; i < iVolumeData.length; i++) {
                    v = iVolumeData[i];
                    if (usePixelPaddingValue) {
                        if ((v < pixelPaddingValue || v > pixelPaddingRangeLimit) && v < bgValue) {
                            bgValue = v;
                        }
                    } else {
                        if (v < bgValue) {
                            bgValue = v;
                        }
                    }
                }

                if (usePixelPaddingValue) {
                    for (int i = 0; i < iVolumeData.length; i++) {
                        if (iVolumeData[i] >= pixelPaddingValue && iVolumeData[i] <= pixelPaddingRangeLimit) {
                            iVolumeData[i] = bgValue;
                        }
                    }
                }

                if (!nativeCrop) {
                    if (useRescaleSlope) {
                        for (int i = 0; i < iVolumeData.length; i++) {
                            iVolumeData[i] = (int) Math.round((double) iVolumeData[i] * rescaleSlope + (double) rescaleIntercept);
                        }
                        bgValue = (int) Math.round((double) bgValue * rescaleSlope + (double) rescaleIntercept);
                    } else if (useRescaleIntercept && !useRescaleSlope) {
                        for (int i = 0; i < iVolumeData.length; i++) {
                            iVolumeData[i] = iVolumeData[i] + rescaleIntercept;
                        }
                        bgValue = bgValue + rescaleIntercept;
                    }
                }

                if(photometricInterpretation.equals("MONOCHROME1")) {
                    int maxValue = Integer.MIN_VALUE;
                    for (int i = 0; i < iVolumeData.length; i++) {
                        if(iVolumeData[i] > maxValue) maxValue = iVolumeData[i];
                    }

                    for (int i = 0; i < iVolumeData.length; i++) {
                        iVolumeData[i] = maxValue - iVolumeData[i];
                    }
                    bgValue = maxValue - bgValue;
                }

                for (int n = 0, i = 0; n < nFramesToRead; n++) {
                    for (int y = 0; y < height; y += downsize[1]) {
                        for (int x = 0; x < width; x += downsize[0], i++) {
                            v = iVolumeData[(n+framesRangeLow)*nData + y*width + x];
                            v = (int) ((v - lowCrop) * s);
                            if (v < 0) {
                                v = 0;
                            }
                            if (v > 255) {
                                v = 255;
                            }
                            bData[n][i] = (byte) v;
                        }
                    }
                }
                break;
            default:
                short[] sVolumeData = null;
                try {
                    if(att instanceof OtherWordAttributeOnDisk) {
                        long off = ((OtherWordAttributeOnDisk)att).getByteOffset();
                        boolean bigEndian = ((OtherWordAttributeOnDisk)att).isBigEndian();
                        ((OtherWordAttributeOnDisk)att).removeValues();
                        File f = ((OtherWordAttributeOnDisk)att).getFile();                
                        if(f == null) {
                            f = new File(entry.getFilePath());
                        }
                        FileImageInputStream in = new FileImageInputStream(f);
                        if(bigEndian)
                            in.setByteOrder(ByteOrder.BIG_ENDIAN);
                        else
                            in.setByteOrder(ByteOrder.LITTLE_ENDIAN);
                        sVolumeData = new short[nData*nFrames];
                        in.seek(off);
                        in.readFully(sVolumeData, 0, nData*nFrames);
                    } else {
                        sVolumeData = att.getShortValues();                
                    }
                } catch (Exception ex) {
                    System.err.println("ERROR: " + ex.getMessage());
                    return Integer.MAX_VALUE;
                }

                if (sVolumeData == null) {
                    System.err.println("DICOM ERROR: Cannot read PixelData!");
                    return Integer.MAX_VALUE;
                }
                if (sVolumeData.length != nData*nFrames) {
                    System.err.println("Wrong PixelData size!");
                    return Integer.MAX_VALUE;
                }

                for (int i = 0; i < sVolumeData.length; i++) {
                    v = (int) sVolumeData[i];
                    if (usePixelPaddingValue) {
                        if ((v < pixelPaddingValue || v > pixelPaddingRangeLimit) && v < bgValue) {
                            bgValue = v;
                        }
                    } else {
                        if (v < bgValue) {
                            bgValue = v;
                        }
                    }
                }

                if (usePixelPaddingValue) {
                    for (int i = 0; i < sVolumeData.length; i++) {
                        if ((int) sVolumeData[i] >= pixelPaddingValue && (int) sVolumeData[i] <= pixelPaddingRangeLimit) {
                            sVolumeData[i] = (short) bgValue;
                        }
                    }
                }

                if (!nativeCrop) {
                    if (useRescaleSlope) {
                        for (int i = 0; i < sVolumeData.length; i++) {
                            sVolumeData[i] = (short) Math.round((double) sVolumeData[i] * rescaleSlope + (double) rescaleIntercept);
                        }
                        bgValue = (int) Math.round((double) bgValue * rescaleSlope + (double) rescaleIntercept);
                    } else if (useRescaleIntercept && !useRescaleSlope) {
                        for (int i = 0; i < sVolumeData.length; i++) {
                            sVolumeData[i] = (short) (sVolumeData[i] + rescaleIntercept);
                        }
                        bgValue = bgValue + rescaleIntercept;
                    }
                }

                if(photometricInterpretation.equals("MONOCHROME1")) {
                    short maxValue = Short.MIN_VALUE;
                    for (int i = 0; i < sVolumeData.length; i++) {
                        if(sVolumeData[i] > maxValue) maxValue = sVolumeData[i];
                    }

                    for (int i = 0; i < sVolumeData.length; i++) {
                        sVolumeData[i] = (short)(maxValue - sVolumeData[i]);
                    }
                    bgValue = maxValue - bgValue;
                }

                for (int n = 0, i = 0; n < nFramesToRead; n++) {
                    for (int y = 0; y < height; y += downsize[1]) {
                        for (int x = 0; x < width; x += downsize[0], i++) {
                            v = (int) sVolumeData[(n+framesRangeLow)*nData + y*width + x];
                            v = (int) ((v - lowCrop) * s);
                            if (v < 0) {
                                v = 0;
                            }
                            if (v > 255) {
                                v = 255;
                            }
                            bData[n][i] = (byte) v;
                        }
                    }
                }
                break;
        }
        return bgValue;
    }
    

}
