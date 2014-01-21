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

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.DicomInputStream;
import com.pixelmed.dicom.TagFromName;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;

/**
 *
 * @author Bartosz Borucki (babor@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class DicomReaderCoreRGB extends DicomReaderCore {

    public DicomReaderCoreRGB() {
    }
    
    @Override
    public DataArray[] readDicomDataArray(ArrayList<DICOMSortingEntry> entries, int readAs, int lowCrop, int highCrop, int[] dims, int[] downsize, boolean interpolateMissingSlices, boolean withProgress, float progressModifier) throws IOException, DicomException {
        if (entries == null || entries.size() < 1 || dims == null) {
            return null;
        }

        DataArray[] outDataArrays = new DataArray[3];

        String patientName = "dicom_data";

        AttributeList atl;
        Attribute att;
        int samplesPerPixel, width, height;
        String photometricInterpretation = null;

        atl = entries.get(0).getHeader();

        att = atl.get(TagFromName.PhotometricInterpretation);
        if (att == null) {
            return null;
        }
        photometricInterpretation = att.getSingleStringValueOrNull();
        if (photometricInterpretation == null || !photometricInterpretation.equals("RGB")) {
            return null;
        }

        att = atl.get(TagFromName.SamplesPerPixel);
        samplesPerPixel = att.getSingleIntegerValueOrDefault(1);

        if (samplesPerPixel != 3) {
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

        if ((int) Math.ceil((double)width / (double)downsize[0]) != dims[0] || (int) Math.ceil((double)height / (double)downsize[1]) != dims[1]) {
            System.err.println("ERROR: DICOM file not compatible with selected set!");
            return null;
        }

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

        int nSliceData = dims[0] * dims[1];
        int nSlices = 1;
        if (dims.length == 3) {
            nSlices = dims[2];
        }
        int nData = nSliceData * nSlices;
        int offset;
        byte bgValueGlobalR = 0;
        byte bgValueGlobalG = 0;
        byte bgValueGlobalB = 0;

        System.out.println("DICOM reader: reading " + nSlices + " slice(s) with auto data detection");
        byte[] bDataRed = new byte[nData];
        byte[] bDataGreen = new byte[nData];
        byte[] bDataBlue = new byte[nData];
        
        for (int i = 0, i2 = 0; i < entries.size(); i += downsize[2], i2++) {
            if (entries.get(i) == null) {
                continue;
            }
            offset = i2 * nSliceData;

            if(!readDicomArrayOffset(entries.get(i), bDataRed, bDataGreen, bDataBlue, offset, dims, downsize)) {
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
                        bDataRed[offset + j] = bgValueGlobalR;
                        bDataGreen[offset + j] = bgValueGlobalG;
                        bDataBlue[offset + j] = bgValueGlobalB;
                    }
                    continue;
                }

                if (next == i + 1 && prev == i - 1) {
                    for (int j = 0; j < nSliceData; j++) {
                        bDataRed[offset + j]   = (byte) Math.round(((float) (bDataRed[offset - nSliceData + j] & 0xFF)   + (float) (bDataRed[offset + nSliceData + j] & 0xFF)) / 2.0f);
                        bDataGreen[offset + j] = (byte) Math.round(((float) (bDataGreen[offset - nSliceData + j] & 0xFF) + (float) (bDataGreen[offset + nSliceData + j] & 0xFF)) / 2.0f);
                        bDataBlue[offset + j]  = (byte) Math.round(((float) (bDataBlue[offset - nSliceData + j] & 0xFF)  + (float) (bDataBlue[offset + nSliceData + j] & 0xFF)) / 2.0f);
                    }
                } else {
                    float prevW, nextW;
                    int prevStep = (i - prev) / downsize[2];
                    int nextStep = (next - i) / downsize[2];
                    prevW = 1.0f / (float) prevStep;
                    nextW = 1.0f / (float) nextStep;
                    for (int j = 0; j < nSliceData; j++) {
                        bDataRed[offset + j] = (byte)   Math.round(((float) (bDataRed[offset - prevStep * nSliceData + j] & 0xFF) * prevW + (float)   (bDataRed[offset + nextStep * nSliceData + j] & 0xFF) * nextW) / (nextW + prevW));
                        bDataGreen[offset + j] = (byte) Math.round(((float) (bDataGreen[offset - prevStep * nSliceData + j] & 0xFF) * prevW + (float) (bDataGreen[offset + nextStep * nSliceData + j] & 0xFF) * nextW) / (nextW + prevW));
                        bDataBlue[offset + j] = (byte)  Math.round(((float) (bDataBlue[offset - prevStep * nSliceData + j] & 0xFF) * prevW + (float)  (bDataBlue[offset + nextStep * nSliceData + j] & 0xFF) * nextW) / (nextW + prevW));
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
                    bDataRed[offset + j] = bgValueGlobalR;
                    bDataGreen[offset + j] = bgValueGlobalG;
                    bDataBlue[offset + j] = bgValueGlobalB;
                }
            }
        }

        outDataArrays[0] = DataArray.create(bDataRed, 1, patientName+"_red");
        outDataArrays[1] = DataArray.create(bDataGreen, 1, patientName+"_green");
        outDataArrays[2] = DataArray.create(bDataBlue, 1, patientName+"_blue");
        return outDataArrays;
    }

    @Override
    public DataArray[] readDicomDataArrayFrames(DICOMSortingEntry entry, int readAs, int lowCrop, int highCrop, int[] dims, int[] downsize, boolean withProgress, float progressModifier, boolean framesAsDim, int framesRangeLow, int framesRangeUp) throws IOException, DicomException {
        if(!framesAsDim) {
            System.err.println("ERROR: reading frames as timesteps in RGB not supported!");
            return null;
        }
        
        if (entry == null || dims == null) {
            return null;
        }

        DataArray[] outDataArrays = new DataArray[3];

        String patientName = "dicom_data";

        AttributeList atl;
        Attribute att;
        int samplesPerPixel, width, height;
        String photometricInterpretation = null;

        atl = entry.getHeader();

        att = atl.get(TagFromName.PhotometricInterpretation);
        if (att == null) {
            return null;
        }
        photometricInterpretation = att.getSingleStringValueOrNull();
        if (photometricInterpretation == null || !photometricInterpretation.equals("RGB")) {
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

        att = atl.get(TagFromName.NumberOfFrames);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read NumberOfFrames value!");
            return null;
        }
        int depth = att.getSingleIntegerValueOrDefault(0);
        if (depth == 0) {
            System.err.println("DICOM ERROR: Cannot read NumberOfFrames value!");
            return null;
        }        
        
        if ((int) Math.ceil((double)width / (double)downsize[0]) != dims[0] || (int) Math.ceil((double)height / (double)downsize[1]) != dims[1] || (depth > 1 && (int) Math.ceil((double)depth / (double)downsize[2]) != dims[2])) {
            System.err.println("ERROR: DICOM file not compatible with selected set!");
            return null;
        }

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

        int nData = dims[0] * dims[1] * dims[2];
        System.out.println("DICOM reader: reading volume with auto data detection");
        int[] iDataRed = new int[nData];
        int[] iDataGreen = new int[nData];
        int[] iDataBlue = new int[nData];


        if(!readDicomArrayFrames(entry, iDataRed, iDataGreen, iDataBlue, dims, downsize, framesAsDim)) {
            return null;
        }

        if (withProgress) {
            progress = progressModifier;
            fireProgressChanged();
        }

        outDataArrays[0] = DataArray.create(iDataRed, 1, patientName+"_red");
        outDataArrays[1] = DataArray.create(iDataGreen, 1, patientName+"_green");
        outDataArrays[2] = DataArray.create(iDataBlue, 1, patientName+"_blue");
        return outDataArrays;
    }
    
    private boolean readDicomArrayOffset(DICOMSortingEntry entry, byte[] bDataR, byte[] bDataG, byte[] bDataB, int offset, int[] dims, int[] downsize) throws DicomException {
        if (entry == null || bDataR == null || bDataG == null || bDataB == null || dims == null || downsize == null) {
            return false;
        }

        if (offset >= bDataR.length || bDataR.length != bDataG.length || bDataR.length != bDataB.length) {
            return false;
        }

        AttributeList atl = null;
        try {
            DicomInputStream dis = new DicomInputStream(new File(entry.getFilePath()));
            atl = new AttributeList();
            atl.read(dis);
            dis.close();
        } catch (Exception ex) {
            System.err.println("ERROR: " + ex.getMessage());
            return false;
        }

        Attribute att = atl.get(TagFromName.PhotometricInterpretation);
        if (att == null) {
            System.err.println("DICOM ERROR: Wrong PhotometricInterpretation!");
            return false;
        }
        String photometricInterpretation = att.getSingleStringValueOrNull();
        if (photometricInterpretation == null || !photometricInterpretation.equals("RGB")) {
            System.err.println("DICOM ERROR: Wrong PhotometricInterpretation!");
            return false;
        }

        att = atl.get(TagFromName.SamplesPerPixel);
        int samplesPerPixel = att.getSingleIntegerValueOrDefault(1);

        if (samplesPerPixel != 3) {
            System.err.println("DICOM ERROR: Wrong SamplesPerPixel for "+photometricInterpretation+"!");
            return false;
        }

        int planarConfiguration = 1;
        att = atl.get(TagFromName.PlanarConfiguration);
        if(att != null)
            planarConfiguration = att.getSingleIntegerValueOrDefault(1);

        boolean usgColorDataPresent = true;
        att = atl.get(TagFromName.UltrasoundColorDataPresent);
        if(att != null)
            usgColorDataPresent = (att.getSingleIntegerValueOrDefault(0) ==  1);                   
        
        // - read data
        att = atl.get(TagFromName.Columns);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read Columns value!");
            return false;
        }
        int width = att.getSingleIntegerValueOrDefault(0);
        if (width == 0) {
            System.err.println("DICOM ERROR: Cannot read Columns value!");
            return false;
        }

        att = atl.get(TagFromName.Rows);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read Rows value!");
            return false;
        }
        int height = att.getSingleIntegerValueOrDefault(0);
        if (height == 0) {
            System.err.println("DICOM ERROR: Cannot read Rows value!");
            return false;
        }

        if ((int) Math.ceil((double)width / (double)downsize[0]) != dims[0] || (int) Math.ceil((double)height / (double)downsize[1]) != dims[1]) {
            System.err.println("ERROR: DICOM file not compatible with selected set!");
            return false;
        }

        int nSliceData = width * height;

        att = atl.get(TagFromName.BitsAllocated);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read BitsAllocated!");
            return false;
        }
        int bitsAllocated = att.getSingleIntegerValueOrDefault(8);
        if (bitsAllocated != 8) {
            System.err.println("DICOM ERROR: Wrong BitsAllocated value!");
            return false;
        }
        

        att = atl.get(TagFromName.PixelData);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read PixelData!");
            return false;
        }

        byte[] bSliceData = null;
        try {
            bSliceData = att.getByteValues();
        } catch (DicomException dex) {
            System.err.println("ERROR: " + dex.getMessage());
            return false;
        }
        if (bSliceData == null) {
            System.err.println("DICOM ERROR: Cannot read PixelData!");
            return false;
        }                
        
        if(!usgColorDataPresent) {
            //TODO read as mono...
            if (bSliceData.length != nSliceData) {
                System.err.println("Wrong PixelData size!");
                return false;
            }
            
            for (int y = 0, c = 0; y < height; y += downsize[1]) {
                for (int x = 0; x < width; x += downsize[0], c++) {
                    bDataR[offset + c] = bSliceData[y*width + x];
                    bDataG[offset + c] = bDataR[offset + c];
                    bDataB[offset + c] = bDataR[offset + c];
                }
            }                
        } else { 
            //read as RGB...
            if (bSliceData.length < 3*nSliceData) {
                System.err.println("Wrong PixelData size!");
                return false;
            }
            
            if(planarConfiguration == 0) {
                //color-by-pixel
                for (int y = 0, c = 0; y < height; y += downsize[1]) {
                    for (int x = 0; x < width; x += downsize[0], c++) {
                        bDataR[offset + c] = bSliceData[y*width*3 + x*3 + 0];
                        bDataG[offset + c] = bSliceData[y*width*3 + x*3 + 1];
                        bDataB[offset + c] = bSliceData[y*width*3 + x*3 + 2];
                    }
                }                
            } else {
                //color-by-plane
                int ccoff = 0;
                for (int y = 0, c = 0; y < height; y += downsize[1]) {
                    for (int x = 0; x < width; x += downsize[0], c++) {
                        bDataR[offset + c] = bSliceData[ccoff + y*width + x];
                    }
                }             
                ccoff += nSliceData;
                
                for (int y = 0, c = 0; y < height; y += downsize[1]) {
                    for (int x = 0; x < width; x += downsize[0], c++) {
                        bDataG[offset + c] = bSliceData[ccoff + y*width + x];
                    }
                }             
                ccoff += nSliceData;
                
                for (int y = 0, c = 0; y < height; y += downsize[1]) {
                    for (int x = 0; x < width; x += downsize[0], c++) {
                        bDataB[offset + c] = bSliceData[ccoff + y*width + x];
                    }
                }             
            }
        }        
        return true;
    }

    private boolean readDicomArrayFrames(DICOMSortingEntry entry, int[] iDataRed, int[] iDataGreen, int[] iDataBlue, int[] dims, int[] downsize, boolean framesAsDim) throws DicomException {
        if (entry == null || iDataRed == null || iDataGreen == null || iDataBlue == null || dims == null || downsize == null) {
            return false;
        }

        AttributeList atl = null;
        try {
            DicomInputStream dis = new DicomInputStream(new File(entry.getFilePath()));
            atl = new AttributeList();
            atl.read(dis);
            dis.close();
        } catch (Exception ex) {
            System.err.println("ERROR: " + ex.getMessage());
            return false;
        }

        Attribute att = atl.get(TagFromName.PhotometricInterpretation);
        if (att == null) {
            System.err.println("DICOM ERROR: Wrong PhotometricInterpretation!");
            return false;
        }
        String photometricInterpretation = att.getSingleStringValueOrNull();
        if (photometricInterpretation == null || !photometricInterpretation.equals("RGB")) {
            System.err.println("DICOM ERROR: Wrong PhotometricInterpretation!");
            return false;
        }

        att = atl.get(TagFromName.SamplesPerPixel);
        int samplesPerPixel = att.getSingleIntegerValueOrDefault(1);

        if (samplesPerPixel != 1) {
            System.err.println("DICOM ERROR: Wrong SamplesPerPixel for "+photometricInterpretation+"!");
            return false;
        }

        // - read data
        att = atl.get(TagFromName.Columns);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read Columns value!");
            return false;
        }
        int width = att.getSingleIntegerValueOrDefault(0);
        if (width == 0) {
            System.err.println("DICOM ERROR: Cannot read Columns value!");
            return false;
        }

        att = atl.get(TagFromName.Rows);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read Rows value!");
            return false;
        }
        int height = att.getSingleIntegerValueOrDefault(0);
        if (height == 0) {
            System.err.println("DICOM ERROR: Cannot read Rows value!");
            return false;
        }

        att = atl.get(TagFromName.NumberOfFrames);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read NumberOfFrames value!");
            return false;
        }
        int zdepth = att.getSingleIntegerValueOrDefault(0);
        if (zdepth == 0) {
            System.err.println("DICOM ERROR: Cannot read NumberOfFrames value!");
            return false;
        }        
        
        if ((int) Math.ceil((double)width / (double)downsize[0]) != dims[0] || (int) Math.ceil((double)height / (double)downsize[1]) != dims[1] || (zdepth > 1 && (int) Math.ceil((double)zdepth / (double)downsize[2]) != dims[2])) {
            System.err.println("ERROR: DICOM file not compatible with selected set!");
            return false;
        }

        int nData = width * height * zdepth;

        att = atl.get(TagFromName.BitsAllocated);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read BitsAllocated!");
            return false;
        }
        int bitsAllocated = att.getSingleIntegerValueOrDefault(8);

        att = atl.get(TagFromName.PixelData);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read PixelData!");
            return false;
        }

        int[] redLUTDescriptor = new int[3];
        int[] greenLUTDescriptor = new int[3];
        int[] blueLUTDescriptor = new int[3];
        int depth, rOff, gOff, bOff;
        int N = 0;

        byte[] bRedLUT = null;
        byte[] bGreenLUT = null;
        byte[] bBlueLUT = null;

        short[] tmp = null;
        int[] iRedLUT = null;
        int[] iGreenLUT = null;
        int[] iBlueLUT = null;

        att = atl.get(TagFromName.RedPaletteColorLookupTableDescriptor);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read RedPaletteColorLookupTableDescriptor!");
            return false;
        }
        redLUTDescriptor = att.getIntegerValues();
        if (redLUTDescriptor == null) {
            System.err.println("DICOM ERROR: Cannot read RedPaletteColorLookupTableDescriptor!");
            return false;
        }

        att = atl.get(TagFromName.GreenPaletteColorLookupTableDescriptor);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read GreenPaletteColorLookupTableDescriptor!");
            return false;
        }
        greenLUTDescriptor = att.getIntegerValues();
        if (greenLUTDescriptor == null) {
            System.err.println("DICOM ERROR: Cannot read GreenPaletteColorLookupTableDescriptor!");
            return false;
        }

        att = atl.get(TagFromName.BluePaletteColorLookupTableDescriptor);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read BluePaletteColorLookupTableDescriptor!");
            return false;
        }
        blueLUTDescriptor = att.getIntegerValues();
        if (blueLUTDescriptor == null) {
            System.err.println("DICOM ERROR: Cannot read BluePaletteColorLookupTableDescriptor!");
            return false;
        }

        if (redLUTDescriptor.length != 3 || greenLUTDescriptor.length != 3 || blueLUTDescriptor.length != 3) {
            System.err.println("DICOM ERROR: Wrong ColorLookupTableDescriptor!");
            return false;
        }

        if (!(redLUTDescriptor[0] == blueLUTDescriptor[0] && blueLUTDescriptor[0] == greenLUTDescriptor[0])) {
            System.err.println("DICOM ERROR: Wrong ColorLookupTableDescriptor number of entries!");
            return false;
        }
        if (!(redLUTDescriptor[2] == blueLUTDescriptor[2] && blueLUTDescriptor[2] == greenLUTDescriptor[2])) {
            System.err.println("DICOM ERROR: Wrong ColorLookupTableDescriptor bits for entry!");
            return false;
        }

        depth = redLUTDescriptor[2];
        rOff = redLUTDescriptor[1];
        gOff = greenLUTDescriptor[1];
        bOff = blueLUTDescriptor[1];

        if(depth != 16 && depth != 8) {
            System.err.println("DICOM ERROR: Wrong color depth for entry!");
            return false;
        }

        if (redLUTDescriptor[0] == 0) {
            N = 1 << 16;
        } else {
            N = redLUTDescriptor[0];
        }

        switch (depth) {
            case 8: // 8-bits per palette entry
                bRedLUT = new byte[N];
                bGreenLUT = new byte[N];
                bBlueLUT = new byte[N];

                att = atl.get(TagFromName.RedPaletteColorLookupTableData);
                if (att == null) {
                    System.err.println("DICOM ERROR: Cannot read RedPaletteColorLookupTableData!");
                    return false;
                }

                try {
                    bRedLUT = att.getByteValues();
                    if (bRedLUT == null || bRedLUT.length != N) {
                        System.err.println("DICOM ERROR: Cannot read RedPaletteColorLookupTableData!");
                        return false;
                    }

                    att = atl.get(TagFromName.GreenPaletteColorLookupTableData);
                    if (att == null) {
                        System.err.println("DICOM ERROR: Cannot read GreenPaletteColorLookupTableData!");
                        return false;
                    }
                    bGreenLUT = att.getByteValues();
                    if (bGreenLUT == null || bGreenLUT.length != N) {
                        System.err.println("DICOM ERROR: Cannot read GreenPaletteColorLookupTableData!");
                        return false;
                    }

                    att = atl.get(TagFromName.BluePaletteColorLookupTableData);
                    if (att == null) {
                        System.err.println("DICOM ERROR: Cannot read BluePaletteColorLookupTableData!");
                        return false;
                    }
                    bBlueLUT = att.getByteValues();
                    if (bBlueLUT == null || bBlueLUT.length != N) {
                        System.err.println("DICOM ERROR: Cannot read BluePaletteColorLookupTableData!");
                        return false;
                    }
                } catch (DicomException ex) {
                    att = atl.get(TagFromName.RedPaletteColorLookupTableData);
                    if (att == null) {
                        System.err.println("DICOM ERROR: Cannot read RedPaletteColorLookupTableData!");
                        return false;
                    }
                    tmp = att.getShortValues();
                    if (tmp == null || (tmp.length != N && tmp.length != N / 2)) {
                        System.err.println("DICOM ERROR: Cannot read RedPaletteColorLookupTableData!");
                        return false;
                    }

                    if (tmp.length == N / 2) {
                        for (int i = 0; i < tmp.length; i++) {
                            bRedLUT[2 * i] = (byte) ((int) (tmp[i] & 0xFF00) >> 8);
                            bRedLUT[2 * i + 1] = (byte) (tmp[i] & 0x00FF);
                        }
                    } else {
                        depth = 16;
                        iRedLUT = new int[N];
                        for (int i = 0; i < N; i++) {
                            iRedLUT[i] = tmp[i] & 0xFFFF;
                        }
                    }

                    att = atl.get(TagFromName.GreenPaletteColorLookupTableData);
                    if (att == null) {
                        System.err.println("DICOM ERROR: Cannot read GreenPaletteColorLookupTableData!");
                        return false;
                    }
                    tmp = att.getShortValues();
                    if (tmp == null || (tmp.length != N && tmp.length != N / 2)) {
                        System.err.println("DICOM ERROR: Cannot read GreenPaletteColorLookupTableData!");
                        return false;
                    }

                    if (tmp.length == N / 2) {
                        for (int i = 0; i < tmp.length; i++) {
                            bGreenLUT[2 * i] = (byte) ((int) (tmp[i] & 0xFF00) >> 8);
                            bGreenLUT[2 * i + 1] = (byte) (tmp[i] & 0x00FF);
                        }
                    } else {
                        depth = 16;
                        iGreenLUT = new int[N];
                        for (int i = 0; i < N; i++) {
                            iGreenLUT[i] = tmp[i] & 0xFFFF;
                        }
                    }

                    att = atl.get(TagFromName.BluePaletteColorLookupTableData);
                    if (att == null) {
                        System.err.println("DICOM ERROR: Cannot read BluePaletteColorLookupTableData!");
                        return false;
                    }
                    tmp = att.getShortValues();
                    if (tmp == null || (tmp.length != N && tmp.length != N / 2)) {
                        System.err.println("DICOM ERROR: Cannot read BluePaletteColorLookupTableData!");
                        return false;
                    }

                    if (tmp.length == N / 2) {
                        for (int i = 0; i < tmp.length; i++) {
                            bBlueLUT[2 * i] = (byte) ((int) (tmp[i] & 0xFF00) >> 8);
                            bBlueLUT[2 * i + 1] = (byte) (tmp[i] & 0x00FF);
                        }
                    } else {
                        depth = 16;
                        iBlueLUT = new int[N];
                        for (int i = 0; i < N; i++) {
                            iBlueLUT[i] = tmp[i] & 0xFFFF;
                        }
                    }
                }
                break;
            case 16: // 16-bits per palette entry
                tmp = new short[N];
                iRedLUT = new int[N];
                iGreenLUT = new int[N];
                iBlueLUT = new int[N];

                att = atl.get(TagFromName.RedPaletteColorLookupTableData);
                if (att == null) {
                    System.err.println("DICOM ERROR: Cannot read RedPaletteColorLookupTableData!");
                    return false;
                }
                tmp = att.getShortValues();
                if (tmp == null || tmp.length != N) {
                    System.err.println("DICOM ERROR: Cannot read RedPaletteColorLookupTableData!");
                    return false;
                }
                for (int i = 0; i < N; i++) {
                    iRedLUT[i] = tmp[i] & 0xFFFF;
                }

                att = atl.get(TagFromName.GreenPaletteColorLookupTableData);
                if (att == null) {
                    System.err.println("DICOM ERROR: Cannot read GreenPaletteColorLookupTableData!");
                    return false;
                }
                tmp = att.getShortValues();
                if (tmp == null || tmp.length != N) {
                    System.err.println("DICOM ERROR: Cannot read GreenPaletteColorLookupTableData!");
                    return false;
                }
                for (int i = 0; i < N; i++) {
                    iGreenLUT[i] = tmp[i] & 0xFFFF;
                }

                att = atl.get(TagFromName.BluePaletteColorLookupTableData);
                if (att == null) {
                    System.err.println("DICOM ERROR: Cannot read BluePaletteColorLookupTableData!");
                    return false;
                }
                tmp = att.getShortValues();
                if (tmp == null || tmp.length != N) {
                    System.err.println("DICOM ERROR: Cannot read BluePaletteColorLookupTableData!");
                    return false;
                }
                for (int i = 0; i < N; i++) {
                    iBlueLUT[i] = tmp[i] & 0xFFFF;
                }

                break;
        }

        att = atl.get(TagFromName.PixelData);
        if (att == null) {
            System.err.println("DICOM ERROR: Cannot read PixelData!");
            return false;
        }

        byte[] bVolumeData = null;
        short[] sVolumeData = null;
        int[] iVolumeData = null;
        int v;

        switch (bitsAllocated) {
            case 8:
                try {
                    bVolumeData = att.getByteValues();
                } catch (DicomException dex) {
                    System.err.println("ERROR: " + dex.getMessage());
                    return false;
                }

                if (bVolumeData == null) {
                    System.err.println("DICOM ERROR: Cannot read PixelData!");
                    return false;
                }
                if (bVolumeData.length != nData) {
                    System.err.println("Wrong PixelData size!");
                    return false;
                }
                break;
            case 16:
                try {
                    sVolumeData = att.getShortValues();
                } catch (DicomException dex) {
                    System.err.println("ERROR: " + dex.getMessage());
                    return false;
                }

                if (sVolumeData == null) {
                    System.err.println("DICOM ERROR: Cannot read PixelData!");
                    return false;
                }
                if (sVolumeData.length != nData) {
                    System.err.println("Wrong PixelData size!");
                    return false;
                }
                break;
            case 32:
                try {
                    iVolumeData = att.getIntegerValues();
                } catch (DicomException dex) {
                    System.err.println("ERROR: " + dex.getMessage());
                    return false;
                }

                if (iVolumeData == null) {
                    System.err.println("DICOM ERROR: Cannot read PixelData!");
                    return false;
                }
                if (iVolumeData.length != nData) {
                    System.err.println("Wrong PixelData size!");
                    return false;
                }
                break;
            default:
                System.err.println("DICOM ERROR: Unsupported bits allocated!");
                return false;
        }


        switch (depth) {
            case 8:
                switch (bitsAllocated) {
                    case 8:
                        for (int z = 0, c = 0; z < zdepth; z += downsize[2]) {
                            for (int y = 0; y < height; y += downsize[1]) {
                                for (int x = 0; x < width; x += downsize[0], c++) {
                                    v = (int) (bVolumeData[z*width*height + y*width + x] & 0xFF);
                                    iDataRed[c] = (int)(bRedLUT[v - rOff]&0xff);
                                    iDataGreen[c] = (int)(bGreenLUT[v - gOff]&0xff);
                                    iDataBlue[c] = (int)(bBlueLUT[v - bOff]&0xff);
                                }
                            }
                        }
                        break;
                    case 16:
                        for (int z = 0, c = 0; z < zdepth; z += downsize[2]) {
                            for (int y = 0; y < height; y += downsize[1]) {
                                for (int x = 0; x < width; x += downsize[0], c++) {
                                    v = (int) (sVolumeData[z*width*height + y*width + x] & 0xFFFF);
                                    iDataRed[c] = (int)(bRedLUT[v - rOff]&0xff);
                                    iDataGreen[c] = (int)(bGreenLUT[v - gOff]&0xff);
                                    iDataBlue[c] = (int)(bBlueLUT[v - bOff]&0xff);
                                }
                            }
                        }
                        break;
                    case 32:
                        for (int z = 0, c = 0; z < zdepth; z += downsize[2]) {
                            for (int y = 0; y < height; y += downsize[1]) {
                                for (int x = 0; x < width; x += downsize[0], c++) {
                                    v = iVolumeData[z*width*height + y*width + x];
                                    iDataRed[c] = (int)(bRedLUT[v - rOff]&0xff);
                                    iDataGreen[c] = (int)(bGreenLUT[v - gOff]&0xff);
                                    iDataBlue[c] = (int)(bBlueLUT[v - bOff]&0xff);
                                }
                            }
                        }
                        break;
                    default:
                        return false;
                }
                break;
            case 16:
                switch (bitsAllocated) {
                    case 8:
                        for (int z = 0, c = 0; z < zdepth; z += downsize[2]) {
                            for (int y = 0; y < height; y += downsize[1]) {
                                for (int x = 0; x < width; x += downsize[0], c++) {
                                    v = (int) (bVolumeData[z*width*height + y*width + x] & 0xFF);
                                    iDataRed[c] = iRedLUT[v - rOff];
                                    iDataGreen[c] = iGreenLUT[v - gOff];
                                    iDataBlue[c] = iBlueLUT[v - bOff];
                                }
                            }
                        }
                        break;
                    case 16:
                        for (int z = 0, c = 0; z < zdepth; z += downsize[2]) {
                            for (int y = 0; y < height; y += downsize[1]) {
                                for (int x = 0; x < width; x += downsize[0], c++) {
                                    v = (int) (sVolumeData[z*width*height + y*width + x] & 0xFFFF);
                                    iDataRed[c] = iRedLUT[v - rOff];
                                    iDataGreen[c] = iGreenLUT[v - gOff];
                                    iDataBlue[c] = iBlueLUT[v - bOff];
                                }
                            }
                        }
                        break;
                    case 32:
                        for (int z = 0, c = 0; z < zdepth; z += downsize[2]) {
                            for (int y = 0; y < height; y += downsize[1]) {
                                for (int x = 0; x < width; x += downsize[0], c++) {
                                    v = iVolumeData[z*width*height + y*width + x];
                                    iDataRed[c] = iRedLUT[v - rOff];
                                    iDataGreen[c] = iGreenLUT[v - gOff];
                                    iDataBlue[c] = iBlueLUT[v - bOff];
                                }
                            }
                        }
                        break;
                    default:
                        return false;
                }
                break;
            default:
                return false;
        }
        return true;
    }
        
}
