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
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.DicomFileUtilities;
import com.pixelmed.dicom.SequenceAttribute;
import com.pixelmed.dicom.TagFromName;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import pl.edu.icm.visnow.datamaps.ColorMapManager;
import pl.edu.icm.visnow.datamaps.colormap1d.DefaultColorMap1D;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.lib.utils.ImageUtilities;

/**
 * @author  Bartosz Borucki (babor@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class DicomUtils {
    public static boolean debug = false;

    public static RegularField readDicomFromFile(String fileName) {
        int[] downsize = {1,1};
        return readDicomFromFile(fileName, downsize);
    }

    public static RegularField readDicomFromFile(String fileName, int[] downsize) {
        try {
            if (fileName == null) {
                return null;
            }

            if (!DicomFileUtilities.isDicomOrAcrNemaFile(fileName)) {
                if(debug) System.err.println("ERROR: Selected file is not a DICOM compatible file!");
                return null;
            }


            AttributeList atl = new AttributeList();
            atl.read(fileName, null, true, true);            
            return readDicomFromAttributeList(atl, downsize);

        } catch (IOException ex) {
            if(debug) {
                System.err.println("ERROR: Cannot read DIOM file!");
                ex.printStackTrace();
            }
            return null;
        } catch (DicomException ex) {
            if(debug) { 
                System.err.println("ERROR: Cannot read DIOM file!");
                ex.printStackTrace();
            }
            return null;
        }
    }

    public static RegularField readDicomFromAttributeList(AttributeList atl) {
        int[] downsize = {1,1};
        return readDicomFromAttributeList(atl, downsize);
    }


    public static RegularField readDicomFromAttributeList(AttributeList atl, int[] downsize) {
        try {
            if (atl == null || !atl.isImage()) {
                return null;
            }

            Attribute att;
            att = atl.get(TagFromName.PixelData);
            if(att == null) {
                if(debug) System.err.println("ERROR: DICOM file does not contain image data!");
                return null;
            }

            att = atl.get(TagFromName.PhotometricInterpretation);
            String photometricInterpretation = att.getSingleStringValueOrDefault("MONOCHROME2");
            if (debug) {
                System.out.println("PhotometricInterpretation = " + photometricInterpretation);
            }

            if (photometricInterpretation.equals("MONOCHROME1")) {
                return readDicomMonochrome1(atl,downsize);
            } else if (photometricInterpretation.equals("MONOCHROME2")) {
                return readDicomMonochrome2(atl,downsize);
            } else if (photometricInterpretation.equals("PALETTE COLOR")) {
                return readDicomPaletteColor(atl,downsize);
            }

            return null;
        } catch (DicomException ex) {
            if(debug) {
                System.err.println("ERROR: Cannot read DIOM file!");
                ex.printStackTrace();
            }
            return null;
        }
    }


    private static RegularField readDicomMonochrome1(AttributeList atl, int[] downsize) throws DicomException {
        AttributeTag tag;
        Attribute att;

        if(downsize == null || downsize.length != 2)
            return null;

        att = atl.get(TagFromName.SamplesPerPixel);
        int samplesPerPixel = att.getSingleIntegerValueOrDefault(1);
        if(debug) System.out.println("SamplesPerPixel = "+samplesPerPixel);

        if(samplesPerPixel != 1) {
            if(debug) System.err.println("DICOM ERROR: Wrong SamplesPerPixel for MONOCHROME1!");
            return null;
        }

        // - read data
        att = atl.get(TagFromName.Columns);
        if(att == null) {
            if(debug) System.err.println("DICOM ERROR: Cannot read Columns value!");
            return null;
        }
        int width = att.getSingleIntegerValueOrDefault(0);
        if(width == 0) {
            if(debug) System.err.println("DICOM ERROR: Cannot read Columns value!");
            return null;
        }

        att = atl.get(TagFromName.Rows);
        if(att == null) {
            if(debug) System.err.println("DICOM ERROR: Cannot read Rows value!");
            return null;
        }
        int height = att.getSingleIntegerValueOrDefault(0);
        if(height == 0) {
            if(debug) System.err.println("DICOM ERROR: Cannot read Rows value!");
            return null;
        }


        int nData = width*height;


        att = atl.get(TagFromName.BitsAllocated);
        if(att == null) {
            if(debug) System.err.println("DICOM ERROR: Cannot read BitsAllocated!");
            return null;
        }
        int bitsAllocated = att.getSingleIntegerValueOrDefault(8);
        if(debug) System.out.println("BitsAllocated = "+bitsAllocated);

        att = atl.get(TagFromName.BitsStored);
        if(att == null) {
            if(debug) System.err.println("DICOM ERROR: Cannot read BitsStored!");
            return null;
        }
        int bitsStored = att.getSingleIntegerValueOrDefault(8);
        if(debug) System.out.println("BitsStored = "+bitsStored);


        att = atl.get(TagFromName.PixelData);
        if(att == null) {
            if(debug) System.err.println("DICOM ERROR: Cannot read PixelData!");
            return null;
        }

        int[] dims = new int[2];
        dims[0] = (int)Math.ceil((double)width/(double)downsize[0]);
        dims[1] = (int)Math.ceil((double)height/(double)downsize[1]);

        short[] sTmp = null;
        int[] iTmp = null;
        int maxv=0, minv=0;

        byte[] bTmp = null;
        byte[] bData = null;
        int v;

        switch(bitsAllocated) {
            case 8:
                bTmp = att.getByteValues();
                if(bTmp == null) {
                    if(debug) System.err.println("DICOM ERROR: Cannot read PixelData!");
                    return null;
                }
                if(bTmp.length != nData) {
                    System.err.println("Wrong PixelData size!");
                    return null;
                }
                bData = new byte[dims[0]*dims[1]];
                for (int y = 0, c = 0; y < height; y+=downsize[1]) {
                    for (int x = 0; x < width; x+=downsize[0], c++) {
                        v = 255 - (int)(bTmp[y*width + x]&0xFF);
                        if(v < 0)
                            v = 0;
                        if(v > 255)
                            v = 255;
                        bData[c] = (byte)v;
                    }
                }
                bTmp = null;
                break;
            case 16:
                sTmp = att.getShortValues();
                if(sTmp == null) {
                    if(debug) System.err.println("DICOM ERROR: Cannot read PixelData!");
                    return null;
                }
                if(sTmp.length != nData) {
                    System.err.println("Wrong PixelData size!");
                    return null;
                }

                iTmp = new int[dims[0]*dims[1]];
                maxv = (int)(sTmp[0]&0xFFFF);
                minv = (int)(sTmp[0]&0xFFFF);
                for (int y = 0, c = 0; y < height; y+=downsize[1]) {
                    for (int x = 0; x < width; x+=downsize[0], c++) {
                        v = (int)(sTmp[y*width + x]&0xFFFF);
                        if(v > maxv)
                            maxv = v;
                        if(v < minv)
                            minv = v;
                        iTmp[c] = v;
                    }
                }
                sTmp = null;
                System.gc();
                break;
            default:
                if(debug) System.err.println("DICOM ERROR: Unsupported bits allocated!");
                return null;
        }
        if(debug) System.out.println("PixelData OK "+width+"x"+height);


        RegularField outField = new RegularField(dims);

        switch(bitsAllocated) {
            case 8:
                outField.addData(DataArray.create(bData, 1, "DicomData"));
                break;
            case 16:
                int dv = maxv-minv;
                if(dv >= 32768) {
                    int[] iData = new int[dims[0]*dims[1]];
                    for (int i = 0; i < iData.length; i++) {
                        iData[i] = maxv - iTmp[i];
                    }
                    iTmp = null;
                    System.gc();
                    outField.addData(DataArray.create(iData, 1, "DicomData"));
                } else if(dv < 32768 && dv >= 256) {
                    short[] sData = new short[dims[0]*dims[1]];
                    for (int i = 0; i < sData.length; i++) {
                        sData[i] = (short)(maxv - iTmp[i]);
                    }
                    iTmp = null;
                    System.gc();
                    outField.addData(DataArray.create(sData, 1, "DicomData"));
                } else {
                    byte[] newBData = new byte[dims[0]*dims[1]];
                    for (int i = 0; i < newBData.length; i++) {
                        newBData[i] = (byte)(maxv - iTmp[i]);
                    }
                    iTmp = null;
                    System.gc();
                    outField.addData(DataArray.create(newBData, 1, "DicomData"));

                }
                break;
            default:
                return null;
        }
        return outField;
    }

    private static RegularField readDicomMonochrome2(AttributeList atl, int[] downsize) throws DicomException {
        AttributeTag tag;
        Attribute att;

        if(downsize == null || downsize.length != 2)
            return null;

        att = atl.get(TagFromName.SamplesPerPixel);
        int samplesPerPixel = att.getSingleIntegerValueOrDefault(1);
        if(debug) System.out.println("SamplesPerPixel = "+samplesPerPixel);

        if(samplesPerPixel != 1) {
            if(debug) System.err.println("DICOM ERROR: Wrong SamplesPerPixel for MONOCHROME2!");
            return null;
        }

        // - read data
        att = atl.get(TagFromName.Columns);
        if(att == null) {
            if(debug) System.err.println("DICOM ERROR: Cannot read Columns value!");
            return null;
        }
        int width = att.getSingleIntegerValueOrDefault(0);
        if(width == 0) {
            if(debug) System.err.println("DICOM ERROR: Cannot read Columns value!");
            return null;
        }

        att = atl.get(TagFromName.Rows);
        if(att == null) {
            if(debug) System.err.println("DICOM ERROR: Cannot read Rows value!");
            return null;
        }
        int height = att.getSingleIntegerValueOrDefault(0);
        if(height == 0) {
            if(debug) System.err.println("DICOM ERROR: Cannot read Rows value!");
            return null;
        }


        int nData = width*height;


        att = atl.get(TagFromName.BitsAllocated);
        if(att == null) {
            if(debug) System.err.println("DICOM ERROR: Cannot read BitsAllocated!");
            return null;
        }
        int bitsAllocated = att.getSingleIntegerValueOrDefault(8);
        if(debug) System.out.println("BitsAllocated = "+bitsAllocated);

        att = atl.get(TagFromName.BitsStored);
        if(att == null) {
            if(debug) System.err.println("DICOM ERROR: Cannot read BitsStored!");
            return null;
        }
        int bitsStored = att.getSingleIntegerValueOrDefault(8);
        if(debug) System.out.println("BitsStored = "+bitsStored);


        att = atl.get(TagFromName.PixelData);
        if(att == null) {
            if(debug) System.err.println("DICOM ERROR: Cannot read PixelData!");
            return null;
        }

        int[] dims = new int[2];
        dims[0] = (int)Math.ceil((double)width/(double)downsize[0]);
        dims[1] = (int)Math.ceil((double)height/(double)downsize[1]);

        int[] iTmp = null;
        short[] sTmp = null;
        byte[] bTmp = null;
        byte[] bData = null;
        int maxv = 0, minv = 0;
        int v;

        switch(bitsAllocated) {
            case 8:
                bTmp = att.getByteValues();
                if(bTmp == null) {
                    if(debug) System.err.println("DICOM ERROR: Cannot read PixelData!");
                    return null;
                }
                if(bTmp.length != nData) {
                    System.err.println("Wrong PixelData size!");
                    return null;
                }

                if(downsize[0] == 1 && downsize[1] == 1) {
                    bData = bTmp;
                } else {
                    bData = new byte[dims[0]*dims[1]];
                    for (int y = 0, c = 0; y < height; y+=downsize[1]) {
                        for (int x = 0; x < width; x+=downsize[0], c++) {
                            bData[c] = bTmp[y*width + x];
                        }
                    }
                    bTmp = null;
                }
                break;
            case 16:
                sTmp = att.getShortValues();
                if(sTmp == null) {
                    if(debug) System.err.println("DICOM ERROR: Cannot read PixelData!");
                    return null;
                }
                if(sTmp.length != nData) {
                    System.err.println("Wrong PixelData size!");
                    return null;
                }
                iTmp = new int[dims[0]*dims[1]];
                maxv = (int)(sTmp[0]&0xFFFF);
                minv = (int)(sTmp[0]&0xFFFF);
                for (int y = 0, c = 0; y < height; y+=downsize[1]) {
                    for (int x = 0; x < width; x+=downsize[0], c++) {
                        v = (int)(sTmp[y*width + x]&0xFFFF);
                        if(v > maxv)
                            maxv = v;
                        if(v < minv)
                            minv = v;
                        iTmp[c] = v;
                    }
                }
                sTmp = null;
                System.gc();
                break;
            default:
                if(debug) System.err.println("DICOM ERROR: Unsupported bits allocated!");
                return null;
        }
        if(debug) System.out.println("PixelData OK "+width+"x"+height);


        RegularField outField = new RegularField(dims);

        switch(bitsAllocated) {
            case 8:
                outField.addData(DataArray.create(bData, 1, "DicomData"));
                break;
            case 16:
                int dv = maxv-minv;
                if(dv >= 32768) {
                    outField.addData(DataArray.create(iTmp, 1, "DicomData"));
                } else if(dv < 32768 && dv >= 256) {
                    short[] sData = new short[dims[0]*dims[1]];
                    for (int i = 0; i < iTmp.length; i++) {
                        sData[i] = (short)iTmp[i];
                    }
                    iTmp = null;
                    System.gc();
                    outField.addData(DataArray.create(sData, 1, "DicomData"));
                } else {
                    byte[] newBData = new byte[dims[0]*dims[1]];
                    for (int i = 0; i < iTmp.length; i++) {
                        newBData[i] = (byte)iTmp[i];
                    }
                    iTmp = null;
                    System.gc();
                    outField.addData(DataArray.create(newBData, 1, "DicomData"));
                }
                break;
            default:
                return null;
        }
        return outField;
    }

    private static RegularField readDicomPaletteColor(AttributeList atl, int[] downsize) throws DicomException {
        AttributeTag tag;
        Attribute att;

        if(downsize == null || downsize.length != 2)
            return null;

        att = atl.get(TagFromName.SamplesPerPixel);
        int samplesPerPixel = att.getSingleIntegerValueOrDefault(1);
        if(debug) System.out.println("SamplesPerPixel = "+samplesPerPixel);

        if(samplesPerPixel != 1) {
            if(debug) System.err.println("DICOM ERROR: Wrong SamplesPerPixel for PALETTE COLOR!");
            return null;
        }

        // - read LUT descriptors
        int[] redLUTDescriptor = new int[3];
        int[] greenLUTDescriptor = new int[3];
        int[] blueLUTDescriptor = new int[3];
        att = atl.get(TagFromName.RedPaletteColorLookupTableDescriptor);
        if(att == null) {
            if(debug) System.err.println("DICOM ERROR: Cannot read RedPaletteColorLookupTableDescriptor!");
            return null;
        }
        redLUTDescriptor = att.getIntegerValues();
        if(redLUTDescriptor == null) {
            if(debug) System.err.println("DICOM ERROR: Cannot read RedPaletteColorLookupTableDescriptor!");
            return null;
        }

        att = atl.get(TagFromName.GreenPaletteColorLookupTableDescriptor);
        if(att == null) {
            if(debug) System.err.println("DICOM ERROR: Cannot read GreenPaletteColorLookupTableDescriptor!");
            return null;
        }
        greenLUTDescriptor = att.getIntegerValues();
        if(greenLUTDescriptor == null) {
            if(debug) System.err.println("DICOM ERROR: Cannot read GreenPaletteColorLookupTableDescriptor!");
            return null;
        }

        att = atl.get(TagFromName.BluePaletteColorLookupTableDescriptor);
        if(att == null) {
            if(debug) System.err.println("DICOM ERROR: Cannot read BluePaletteColorLookupTableDescriptor!");
            return null;
        }
        blueLUTDescriptor = att.getIntegerValues();
        if(blueLUTDescriptor == null) {
            if(debug) System.err.println("DICOM ERROR: Cannot read BluePaletteColorLookupTableDescriptor!");
            return null;
        }

        if(redLUTDescriptor.length != 3 || greenLUTDescriptor.length != 3 || blueLUTDescriptor.length != 3) {
            if(debug) System.err.println("DICOM ERROR: Wrong ColorLookupTableDescriptor!");
            return null;
        }

        if(!  (redLUTDescriptor[0] == blueLUTDescriptor[0] && blueLUTDescriptor[0] == greenLUTDescriptor[0])) {
            if(debug) System.err.println("DICOM ERROR: Wrong ColorLookupTableDescriptor number of entries!");
            return null;
        }
        if(!  (redLUTDescriptor[2] == blueLUTDescriptor[2] && blueLUTDescriptor[2] == greenLUTDescriptor[2])) {
            if(debug) System.err.println("DICOM ERROR: Wrong ColorLookupTableDescriptor bits for entry!");
            return null;
        }

        if(debug) System.out.println("RedPaletteColorLookupTableDescriptor = "+redLUTDescriptor[0]+"\\"+redLUTDescriptor[1]+"\\"+redLUTDescriptor[2]);
        if(debug) System.out.println("GreenPaletteColorLookupTableDescriptor = "+greenLUTDescriptor[0]+"\\"+greenLUTDescriptor[1]+"\\"+greenLUTDescriptor[2]);
        if(debug) System.out.println("BluePaletteColorLookupTableDescriptor = "+blueLUTDescriptor[0]+"\\"+blueLUTDescriptor[1]+"\\"+blueLUTDescriptor[2]);


        // - read LUTs
        int N = 0;
        int depth = redLUTDescriptor[2];
        int rOff = redLUTDescriptor[1];
        int gOff = greenLUTDescriptor[1];
        int bOff = blueLUTDescriptor[1];

        int[] iRedLUT = null;
        int[] iGreenLUT = null;
        int[] iBlueLUT = null;

        byte[] bRedLUT = null;
        byte[] bGreenLUT = null;
        byte[] bBlueLUT = null;

        short[] tmp;

        if(redLUTDescriptor[0] == 0)
            N = 1 << 16;
        else
            N = redLUTDescriptor[0];

        if(debug) System.out.println("NumberOfEntries = "+N);

        switch(depth) {
            case 8: // 8-bits per palette entry
                if(debug) System.out.println("BitsPerEntry = 8");

                bRedLUT = new byte[N];
                bGreenLUT = new byte[N];
                bBlueLUT = new byte[N];

                att = atl.get(TagFromName.RedPaletteColorLookupTableData);
                if(att == null) {
                    if(debug) System.err.println("DICOM ERROR: Cannot read RedPaletteColorLookupTableData!");
                    return null;
                }

                try {
                    bRedLUT = att.getByteValues();
                    if(bRedLUT == null || bRedLUT.length != N) {
                        if(debug) System.err.println("DICOM ERROR: Cannot read RedPaletteColorLookupTableData!");
                        return null;
                    }
                    if(debug) System.out.println("RedPaletteColorLookupTableData... OK");

                    att = atl.get(TagFromName.GreenPaletteColorLookupTableData);
                    if(att == null) {
                        if(debug) System.err.println("DICOM ERROR: Cannot read GreenPaletteColorLookupTableData!");
                        return null;
                    }
                    bGreenLUT = att.getByteValues();
                    if(bGreenLUT == null || bGreenLUT.length != N) {
                        if(debug) System.err.println("DICOM ERROR: Cannot read GreenPaletteColorLookupTableData!");
                        return null;
                    }
                    if(debug) System.out.println("GreenPaletteColorLookupTableData... OK");

                    att = atl.get(TagFromName.BluePaletteColorLookupTableData);
                    if(att == null) {
                        if(debug) System.err.println("DICOM ERROR: Cannot read BluePaletteColorLookupTableData!");
                        return null;
                    }
                    bBlueLUT = att.getByteValues();
                    if(bBlueLUT == null || bBlueLUT.length != N) {
                        if(debug) System.err.println("DICOM ERROR: Cannot read BluePaletteColorLookupTableData!");
                        return null;
                    }
                    if(debug) System.out.println("BluePaletteColorLookupTableData... OK");
                } catch(DicomException ex) {
                    att = atl.get(TagFromName.RedPaletteColorLookupTableData);
                    if(att == null) {
                        if(debug) System.err.println("DICOM ERROR: Cannot read RedPaletteColorLookupTableData!");
                        return null;
                    }
                    tmp = att.getShortValues();
                    if(tmp == null || (tmp.length != N && tmp.length != N/2)) {
                        if(debug) System.err.println("DICOM ERROR: Cannot read RedPaletteColorLookupTableData!");
                        return null;
                    }

                    if(tmp.length == N/2) {
                        for (int i = 0; i < tmp.length; i++) {
                            bRedLUT[2*i] = (byte) ((int)(tmp[i] & 0xFF00) >> 8);
                            bRedLUT[2*i+1] = (byte) (tmp[i] & 0x00FF);
                        }

                    } else {
                        depth = 16;
                        iRedLUT = new int[N];
                        for (int i = 0; i < N; i++) {
                            iRedLUT[i] = tmp[i]&0xFFFF;
                        }
                    }
                    if(debug) System.out.println("RedPaletteColorLookupTableData... OK");

                    att = atl.get(TagFromName.GreenPaletteColorLookupTableData);
                    if(att == null) {
                        if(debug) System.err.println("DICOM ERROR: Cannot read GreenPaletteColorLookupTableData!");
                        return null;
                    }
                    tmp = att.getShortValues();
                    if(tmp == null || (tmp.length != N && tmp.length != N/2)) {
                        if(debug) System.err.println("DICOM ERROR: Cannot read GreenPaletteColorLookupTableData!");
                        return null;
                    }

                    if(tmp.length == N/2) {
                        for (int i = 0; i < tmp.length; i++) {
                            bGreenLUT[2*i] = (byte) ((int)(tmp[i] & 0xFF00) >> 8);
                            bGreenLUT[2*i+1] = (byte) (tmp[i] & 0x00FF);
                        }

                    } else {
                        depth = 16;
                        iGreenLUT = new int[N];
                        for (int i = 0; i < N; i++) {
                            iGreenLUT[i] = tmp[i]&0xFFFF;
                        }
                    }
                    if(debug) System.out.println("GreenPaletteColorLookupTableData... OK");

                    att = atl.get(TagFromName.BluePaletteColorLookupTableData);
                    if(att == null) {
                        if(debug) System.err.println("DICOM ERROR: Cannot read BluePaletteColorLookupTableData!");
                        return null;
                    }
                    tmp = att.getShortValues();
                    if(tmp == null || (tmp.length != N && tmp.length != N/2)) {
                        if(debug) System.err.println("DICOM ERROR: Cannot read BluePaletteColorLookupTableData!");
                        return null;
                    }

                    if(tmp.length == N/2) {
                        for (int i = 0; i < tmp.length; i++) {
                            bBlueLUT[2*i] = (byte) ((int)(tmp[i] & 0xFF00) >> 8);
                            bBlueLUT[2*i+1] = (byte) (tmp[i] & 0x00FF);
                        }

                    } else {
                        depth = 16;
                        iBlueLUT = new int[N];
                        for (int i = 0; i < N; i++) {
                            iBlueLUT[i] = tmp[i]&0xFFFF;
                        }
                    }
                    if(debug) System.out.println("BluePaletteColorLookupTableData... OK");
                }
                break;
            case 16: // 16-bits per palette entry
                if(debug) System.out.println("BitsPerEntry = 16");                

                tmp = new short[N];
                iRedLUT = new int[N];
                iGreenLUT = new int[N];
                iBlueLUT = new int[N];

                att = atl.get(TagFromName.RedPaletteColorLookupTableData);
                if(att == null) {
                    if(debug) System.err.println("DICOM ERROR: Cannot read RedPaletteColorLookupTableData!");
                    return null;
                }
                tmp = att.getShortValues();
                if(tmp == null || tmp.length != N) {
                    if(debug) System.err.println("DICOM ERROR: Cannot read RedPaletteColorLookupTableData!");
                    return null;
                }
                if(debug) System.out.println("RedPaletteColorLookupTableData... OK");
                for (int i = 0; i < N; i++) {
                    iRedLUT[i] = tmp[i]&0xFFFF;
                }

                att = atl.get(TagFromName.GreenPaletteColorLookupTableData);
                if(att == null) {
                    if(debug) System.err.println("DICOM ERROR: Cannot read GreenPaletteColorLookupTableData!");
                    return null;
                }
                tmp = att.getShortValues();
                if(tmp == null || tmp.length != N) {
                    if(debug) System.err.println("DICOM ERROR: Cannot read GreenPaletteColorLookupTableData!");
                    return null;
                }
                if(debug) System.out.println("GreenPaletteColorLookupTableData... OK");
                for (int i = 0; i < N; i++) {
                    iGreenLUT[i] = tmp[i]&0xFFFF;
                }

                att = atl.get(TagFromName.BluePaletteColorLookupTableData);
                if(att == null) {
                    if(debug) System.err.println("DICOM ERROR: Cannot read BluePaletteColorLookupTableData!");
                    return null;
                }
                tmp = att.getShortValues();
                if(tmp == null || tmp.length != N) {
                    if(debug) System.err.println("DICOM ERROR: Cannot read BluePaletteColorLookupTableData!");
                    return null;
                }
                if(debug) System.out.println("BluePaletteColorLookupTableData... OK");
                for (int i = 0; i < N; i++) {
                    iBlueLUT[i] = tmp[i]&0xFFFF;
                }

                break;
            default:
                if(debug) System.err.println("DICOM ERROR: Wrong bits for entry!");
                return null;
        }

        // - read data
        att = atl.get(TagFromName.Columns);
        if(att == null) {
            if(debug) System.err.println("DICOM ERROR: Cannot read Columns value!");
            return null;
        }
        int width = att.getSingleIntegerValueOrDefault(0);
        if(width == 0) {
            if(debug) System.err.println("DICOM ERROR: Cannot read Columns value!");
            return null;
        }

        att = atl.get(TagFromName.Rows);
        if(att == null) {
            if(debug) System.err.println("DICOM ERROR: Cannot read Rows value!");
            return null;
        }
        int height = att.getSingleIntegerValueOrDefault(0);
        if(height == 0) {
            if(debug) System.err.println("DICOM ERROR: Cannot read Rows value!");
            return null;
        }


        int nData = width*height;
        int[] dims = new int[2];
        dims[0] = (int)Math.ceil((double)width/(double)downsize[0]);
        dims[1] = (int)Math.ceil((double)height/(double)downsize[1]);


        att = atl.get(TagFromName.BitsAllocated);
        if(att == null) {
            if(debug) System.err.println("DICOM ERROR: Cannot read BitsAllocated!");
            return null;
        }
        int bitsAllocated = att.getSingleIntegerValueOrDefault(8);


        att = atl.get(TagFromName.PixelData);
        if(att == null) {
            if(debug) System.err.println("DICOM ERROR: Cannot read PixelData!");
            return null;
        }

        short[] sTmp = null;
        byte[] bTmp = null;

        switch(bitsAllocated) {
            case 8:
                bTmp = att.getByteValues();
                if(bTmp == null) {
                    if(debug) System.err.println("DICOM ERROR: Cannot read PixelData!");
                    return null;
                }
                if(bTmp.length != nData) {
                    System.err.println("Wrong PixelData size!");
                    return null;
                }
                break;
            case 16:
                sTmp = att.getShortValues();
                if(sTmp == null) {
                    if(debug) System.err.println("DICOM ERROR: Cannot read PixelData!");
                    return null;
                }
                if(sTmp.length != nData) {
                    System.err.println("Wrong PixelData size!");
                    return null;
                }
                break;
            default:
                if(debug) System.err.println("DICOM ERROR: Unsupported bits allocated!");
                return null;
        }
        if(debug) System.out.println("PixelData OK "+width+"x"+height);


        RegularField outField = new RegularField(dims);

        int n;
        switch(depth) {
            case 8:
                if(bRedLUT == null || bGreenLUT == null || bBlueLUT == null)
                    return null;
                byte[] bRedData = new byte[dims[0]*dims[1]];
                byte[] bGreenData = new byte[dims[0]*dims[1]];
                byte[] bBlueData = new byte[dims[0]*dims[1]];
                
                switch(bitsAllocated) {
                    case 8:
                        if(bTmp == null)
                            return null;
                        for (int y = 0, c = 0; y < height; y+=downsize[1]) {
                            for (int x = 0; x < width; x+=downsize[0], c++) {
                                n =  (int)(bTmp[y*width + x]&0xFF);
                                bRedData[c] = bRedLUT[n-rOff];
                                bGreenData[c] = bGreenLUT[n-gOff];
                                bBlueData[c] = bBlueLUT[n-bOff];
                            }
                        }
                        break;
                    case 16:
                        if(sTmp == null)
                            return null;
                        for (int y = 0, c = 0; y < height; y+=downsize[1]) {
                            for (int x = 0; x < width; x+=downsize[0], c++) {
                                n =  (int)(sTmp[y*width + x]&0xFFFF);
                                bRedData[c] = bRedLUT[n-rOff];
                                bGreenData[c] = bGreenLUT[n-gOff];
                                bBlueData[c] = bBlueLUT[n-bOff];
                            }
                        }
                        break;
                    default:
                        return null;
                }

                boolean bequal = true;
                for (int i = 0; i < dims[0]*dims[1]; i++) {
                    if( bRedData[i] != bGreenData[i] || bGreenData[i] != bBlueData[i]) {
                        bequal = false;
                        break;
                    }
                }

                if(bequal) {
                    outField.addData(DataArray.create(bRedData, 1, "dicomData"));
                } else {
                    outField.addData(DataArray.create(bRedData, 1, "redData"));
                    outField.addData(DataArray.create(bGreenData, 1, "greenData"));
                    outField.addData(DataArray.create(bBlueData, 1, "blueData"));
                }
                break;
            case 16:
                if(iRedLUT == null || iGreenLUT == null || iBlueLUT == null)
                    return null;
                int[] iRedData = new int[dims[0]*dims[1]];
                int[] iGreenData = new int[dims[0]*dims[1]];
                int[] iBlueData = new int[dims[0]*dims[1]];
                switch(bitsAllocated) {
                    case 8:
                        if(bTmp == null)
                            return null;
                        for (int y = 0, c = 0; y < height; y+=downsize[1]) {
                            for (int x = 0; x < width; x+=downsize[0], c++) {
                                n =  (int)(bTmp[y*width + x]&0xFF);
                                iRedData[c] = iRedLUT[n-rOff];
                                iGreenData[c] = iGreenLUT[n-gOff];
                                iBlueData[c] = iBlueLUT[n-bOff];
                            }
                        }
                        break;
                    case 16:
                        if(sTmp == null)
                            return null;
                        for (int y = 0, c = 0; y < height; y+=downsize[1]) {
                            for (int x = 0; x < width; x+=downsize[0], c++) {
                                n =  (int)(sTmp[y*width + x]&0xFFFF);
                                iRedData[c] = iRedLUT[n-rOff];
                                iGreenData[c] = iGreenLUT[n-gOff];
                                iBlueData[c] = iBlueLUT[n-bOff];
                            }
                        }
                        break;
                    default:
                        return null;
                }

                boolean iequal = true;
                for (int i = 0; i < dims[0]*dims[1]; i++) {
                    if( iRedData[i] != iGreenData[i] || iGreenData[i] != iBlueData[i]) {
                        iequal = false;
                        break;
                    }
                }

                if(iequal) {
                    outField.addData(DataArray.create(iRedData, 1, "dicomData"));
                } else {
                    outField.addData(DataArray.create(iRedData, 1, "redData"));
                    outField.addData(DataArray.create(iGreenData, 1, "greenData"));
                    outField.addData(DataArray.create(iBlueData, 1, "blueData"));
                }
                break;
            default:
                return null;
        }
        return outField;
    }


    public static BufferedImage getImageFromAttributeList(AttributeList atl, int[] downsize) {
        if(atl == null)
            return null;

        if(!atl.isImage())
            return null;


        RegularField field = readDicomFromAttributeList(atl, downsize);

        if(field == null)
            return null;

        BufferedImage outImage = null;
        int[] dims = field.getDims();


        if(field.getNData() == 1) {
            outImage = new BufferedImage(dims[0], dims[1], BufferedImage.TYPE_INT_ARGB);
            DefaultColorMap1D cmap = ColorMapManager.getInstance().getColorMap1D(ColorMapManager.COLORMAP1D_GRAY);
            int[] cmapLUT = cmap.getRGBColorTable();
            int cmapLUTSize = cmapLUT.length-1;
            int c;

            float low = field.getData(0).getMinv();
            float up = field.getData(0).getMaxv();
            float cs = (float)cmapLUTSize/(up-low);
            int off = 0;
            if(field.getData(0).getType() == DataArray.FIELD_DATA_BYTE) {
                byte[] bdata = field.getData(0).getBData();
                for (int y = 0; y < dims[1]; y++) {
                    off = dims[0]*y;
                    for (int x = 0; x < dims[0]; x++) {
                        //outImage.setRGB(x, y, cmap.getARGB(bdata[off+x]&0xff, low, up));
                        c = (int)(((float)(bdata[off+x]&0xff) - low)*cs);
                        if(c < 0) c = 0;
                        if(c > cmapLUTSize) c = cmapLUTSize;
                        outImage.setRGB(x, y, cmapLUT[c]);
                    }
                }
            } else {
                float[] fdata = field.getData(0).getFData();
                for (int y = 0; y < dims[1]; y++) {
                    off = dims[0]*y;
                    for (int x = 0; x < dims[0]; x++) {
                        //outImage.setRGB(x, y, cmap.getARGB(fdata[off+x], low, up));
                        c = (int)((fdata[off+x] - low)*cs);
                        if(c < 0) c = 0;
                        if(c > cmapLUTSize) c = cmapLUTSize;
                        outImage.setRGB(x, y, cmapLUT[c]);
                    }
                }
            }
        } else if(field.getNData() == 3) {
            outImage = new BufferedImage(dims[0], dims[1], BufferedImage.TYPE_INT_RGB);
            float[][] data = new float[3][];
            data[0] = field.getData(0).getFData();
            data[1] = field.getData(1).getFData();
            data[2] = field.getData(2).getFData();

            WritableRaster raster = outImage.getRaster();

            int[] selectedComponents = {0,1,2};
            for (int d = 0; d < raster.getNumBands(); d++) {
                if (field.getData(selectedComponents[d]).getType() == DataArray.FIELD_DATA_BYTE) {
                    raster.setSamples(0, 0, dims[0], dims[1], d, field.getData(selectedComponents[d]).getIData());
                } else {
                    float[] tmp = field.getData(selectedComponents[d]).getFData();
                    float max = field.getData(selectedComponents[d]).getMaxv();
                    float min = field.getData(selectedComponents[d]).getMinv();
                    float s = max - min;
                    if(s < 0.0000001) {
                        if( min >= 0.0f && max <= 255.0f) {
                            min = 0.0f;
                            max = 255.0f;
                            s = max - min;
                        } else {
                            min = min - 0.1f;
                            max = max + 0.1f;
                            s = max - min;
                        }
                    }
                    for (int y = 0, i = 0; y < dims[1]; y++) {
                        for (int x = 0; x < dims[0]; x++,i++) {
                            raster.setSample(x, y, d, (int) ((tmp[i] - min) * 255 / s));
                        }
                    }
                }
            }
        }

        return outImage;
    }

    public static BufferedImage getDicomThumbnailFromFile(String fileName, int[] dims) {
        if(fileName == null || dims == null || dims.length != 2 || dims[0] == 0 || dims[1] == 0)
            return null;
        
        try {
            if (!DicomFileUtilities.isDicomOrAcrNemaFile(fileName)) {
                if(debug) System.err.println("ERROR: Selected file is not a DICOM compatible file!");
                return null;
            }

            AttributeList atl = new AttributeList();
            atl.read(fileName, null, true, true);            
            return getDicomThumbnailFromAttributeList(atl, dims);

        } catch (IOException ex) {
            if(debug) {
                System.err.println("ERROR: Cannot read DIOM file!");
                ex.printStackTrace();
            }
            return null;
        } catch (DicomException ex) {
            if(debug) { 
                System.err.println("ERROR: Cannot read DIOM file!");
                ex.printStackTrace();
            }
            return null;
        }
    }

    public static BufferedImage getDicomThumbnailFromAttributeList(AttributeList atl, int[] dims) {
        if(atl == null || dims == null || dims.length != 2 || dims[0] == 0 || dims[1] == 0)
            return null;

        Attribute att;
        AttributeList localAtl = atl;

        att = atl.get(TagFromName.IconImageSequence);
        if(att != null) {
            SequenceAttribute satt = (SequenceAttribute)att;
            if(satt.getNumberOfItems() > 0) {
                localAtl = satt.getItem(0).getAttributeList();
            }
        }

        att = localAtl.get(TagFromName.Columns);
        if(att == null) {
            if(debug) System.err.println("DICOM ERROR: Cannot read Columns value!");
            return null;
        }
        int width = att.getSingleIntegerValueOrDefault(0);
        if(width == 0) {
            if(debug) System.err.println("DICOM ERROR: Cannot read Columns value!");
            return null;
        }

        att = localAtl.get(TagFromName.Rows);
        if(att == null) {
            if(debug) System.err.println("DICOM ERROR: Cannot read Rows value!");
            return null;
        }
        int height = att.getSingleIntegerValueOrDefault(0);
        if(height == 0) {
            if(debug) System.err.println("DICOM ERROR: Cannot read Rows value!");
            return null;
        }

        if(width == 0 || height == 0)
            return null;

        int[] downsize = new int[2];
        if(width > dims[0] || height > dims[1]) {
            double sx,sy,s;
            sx = (double)dims[0]/(double)width;
            sy = (double)dims[1]/(double)height;
            s = Math.min(sx, sy);
            int ds = (int)Math.floor(1.0/s);
            downsize[0] = ds;
            downsize[1] = ds;
        } else {
            downsize[0] = 1;
            downsize[1] = 1;
        }
        //TODO poprawic wyliczanie downsize'u

        BufferedImage img = getImageFromAttributeList(localAtl, downsize);
        if(img == null)
            return null;
        
        if(img.getWidth() == dims[0] && img.getHeight() == dims[1])
            return img;
        else
            return ImageUtilities.resizeImage(img, (double)dims[0]/(double)img.getWidth(), (double)dims[1]/(double)img.getHeight(), AffineTransformOp.TYPE_BILINEAR);
    }

   private DicomUtils()
   {
   }



}
