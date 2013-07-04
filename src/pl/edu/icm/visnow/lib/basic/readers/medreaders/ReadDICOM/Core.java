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
import java.util.ArrayList;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.gui.events.FloatValueModificationEvent;
import pl.edu.icm.visnow.gui.events.FloatValueModificationListener;
import pl.edu.icm.visnow.lib.basic.filters.AnisotropicDenoiser.AbstractAnisotropicWeightedAverageCompute;
import pl.edu.icm.visnow.lib.basic.filters.AnisotropicDenoiser.AnisotropicWeightedAverageCompute;
import pl.edu.icm.visnow.lib.basic.readers.medreaders.ReadDICOM.DicomReaderCore.DICOMSortingEntry;
import pl.edu.icm.visnow.lib.gui.HistoArea;
import pl.edu.icm.visnow.lib.utils.field.FieldSmoothDown;

/**
 * @author  Bartosz Borucki (babor@icm.edu.pl)
 * Warsaw University, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class Core {

    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Core.class);
    private RegularField outField = null;
    private Params params = null;
    private float progress = 0.0f;
    private HistoArea histoArea = null;

    public Core() {
    }

    public void setHistoArea(HistoArea histoArea) {
        this.histoArea = histoArea;
    }

    /**
     * @param params the params to set
     */
    public void setParams(Params params) {
        this.params = params;
    }

    public void update() throws ReadDICOMException {
        log.debug("running ReadDICOM core update");
        outField = readDicomFromFileList();

        if (params.getSliceDenoisingLevel() > 0)
        {
            pl.edu.icm.visnow.lib.basic.filters.AnisotropicDenoiser.Params denoiserParams =
                    new pl.edu.icm.visnow.lib.basic.filters.AnisotropicDenoiser.Params();
            denoiserParams.setComputeBySlice(true);
            int level = params.getSliceDenoisingLevel();
            denoiserParams.setPresmoothRadius(3 * level);
            denoiserParams.setRadius(level);
            denoiserParams.setSlope(level);
            denoiserParams.setSlope1(2 * level);
            RegularField anisotropyField = FieldSmoothDown.smoothDownToFloat(outField, 1,
                    (float) denoiserParams.getPresmoothRadius(), denoiserParams.getNThreads());
            denoiserParams.setPresmooth(false);
            AbstractAnisotropicWeightedAverageCompute averageCompute = new AnisotropicWeightedAverageCompute();
            averageCompute.addFloatValueModificationListener(
                    new FloatValueModificationListener()
            {
               @Override
               public void floatValueChanged(FloatValueModificationEvent e)
               {
                  progress = e.getVal();
                  fireStatusChanged(progress);
               }
            });
            RegularField tmpField = averageCompute.compute(outField, anisotropyField, denoiserParams);
            tmpField.getData(0).setPhysMin(outField.getData(0).getPhysMin());
            tmpField.getData(0).setPhysMax(outField.getData(0).getPhysMax());
            outField = tmpField;
        } 
        
        if (params.isInterpolateData() && outField != null) {
            log.debug("running ReadDICOM core data interpolation");
            progress = 0.5f;
            fireStatusChanged(progress);
            Regularizator r = new Regularizator();
            r.addFloatValueModificationListener(new FloatValueModificationListener() {

                public void floatValueChanged(FloatValueModificationEvent e) {
                    progress = 0.5f + (e.getVal()) / 2.0f;
                    if (progress >= 1.0f) {
                        progress = 0.999f;
                    }
                    fireStatusChanged(progress);
                }
            });

            float voxelSize = 1.0f;
            float[][] affine = outField.getAffine();
            switch (params.getInterpolateDataVoxelSizeFrom()) {
                case Params.VOXELSIZE_FROM_MANUALVALUE:
                    //voxelSize = params.getInterpolateDataVoxelSizeManualValue();
                    voxelSize = params.getInterpolateDataVoxelSizeManualValue() / 1000.0f;
                    break;
                case Params.VOXELSIZE_FROM_PIXELSIZE:
                    voxelSize = 0;
                    for (int i = 0; i < 3; i++) {
                        voxelSize += affine[0][i] * affine[0][i];
                    }
                    voxelSize = (float) Math.sqrt(voxelSize);
                    break;
                case Params.VOXELSIZE_FROM_SLICESDISTANCE:
                    voxelSize = 0;
                    for (int i = 0; i < 3; i++) {
                        voxelSize += affine[2][i] * affine[2][i];
                    }
                    voxelSize = (float) Math.sqrt(voxelSize);
                    break;
            }

            log.debug("voxel size =" + voxelSize);
            outField = r.regularize(outField, Runtime.getRuntime().availableProcessors(), voxelSize);
        }
        progress = 1.0f;
        fireStatusChanged(progress);
    }

    /**
     * @return the outField
     */
    public RegularField getOutField() {
        return outField;
    }

    public RegularField readDicomFromFileList() throws ReadDICOMException {
        RegularField field = null;
        ArrayList<String> fileList = params.getFileList();
        if (fileList == null || fileList.size() < 1) {
            throw new ReadDICOMException("ERROR: no files to read!");
        }

        progress = 0.0f;
        fireStatusChanged(progress);

        ArrayList<AttributeList> atls = new ArrayList<AttributeList>();
        ArrayList<String> fileListFiltered = new ArrayList<String>();

        log.debug("number of files in list: " + fileList.size());
        log.debug("Filter DICOM file list");
        File file;
        Attribute att;

        for (int i = 0; i < fileList.size(); i++) {
            progress = i * 0.25f / fileList.size();
            if (params.isInterpolateData()) {
                progress = progress / 2.0f;
            }
            fireStatusChanged(progress);

            String fileName = fileList.get(i);
            file = new File(fileName);
            AttributeList atl = new AttributeList();

            try {
                atl.read(file, TagFromName.PixelData);
            } catch (IOException ex) {
                System.out.println("WARNING! Cannot read file: " + fileName);
                continue;
            } catch (DicomException ex) {
                System.out.println("WARNING! File is not a DICOM file: " + fileName);
                continue;
            }

            //----------------check Rows TAG--------------------
            att = atl.get(TagFromName.Rows);
            if (att == null) {
                System.out.println("WARNING! File does not contain 'Rows' DICOM TAG: " + fileName);
                continue;
            }
            int rows = att.getSingleIntegerValueOrDefault(0);

            //----------------check Columns TAG--------------------
            att = atl.get(TagFromName.Columns);
            if (att == null) {
                System.out.println("WARNING! File does not contain 'Columns' DICOM TAG: " + fileName);
                continue;
            }
            int cols = att.getSingleIntegerValueOrDefault(0);

            //----------------check rows/cols size--------------------
            if (rows == 0 || cols == 0) {
                System.out.println("WARNING! File image data of zero size: " + fileName);
                continue;
            }

            //----------------check PhotometricInterpretation TAG--------------------
            att = atl.get(TagFromName.PhotometricInterpretation);
            if (att == null) {
                System.out.println("WARNING! File does not contain 'PhotometricInterpretation' DICOM TAG: " + fileName);
                continue;
            }
            String photometricInterpretation = att.getSingleStringValueOrDefault("");

            if (!(
                    photometricInterpretation.equals("MONOCHROME1") || 
                    photometricInterpretation.equals("MONOCHROME2") || 
                    photometricInterpretation.equals("PALETTE COLOR") ||
                    photometricInterpretation.equals("RGB")
               )) {
                System.out.println("WARNING! Unknown PhotometricInterpretation '" + photometricInterpretation + "' in file: " + fileName);
                continue;
            }

            if(fileList.size() == 1) {
                atls.add(atl);
                fileListFiltered.add(fileName);
                break;
            }

            //----------------check PixelSpacing TAG--------------------
            att = atl.get(TagFromName.PixelSpacing);
            if (att == null) {
                System.out.println("WARNING! File does not contain 'PixelSpacing' DICOM TAG: " + fileName);
                continue;
            }

            //----------------check SeriesNumber TAG--------------------
            att = atl.get(TagFromName.SeriesNumber);
            if (att == null) {
                System.out.println("WARNING! File does not contain 'SeriesNumber' DICOM TAG: " + fileName);
                continue;
            }

            //----------------check ImagePositionPatient TAG--------------------
            att = atl.get(TagFromName.ImagePositionPatient);
            if (att == null) {
                System.out.println("WARNING! File does not contain 'ImagePositionPatient' DICOM TAG: " + fileName);
                continue;
            }

            //----------------check ImageOrientationPatient TAG--------------------
            att = atl.get(TagFromName.ImageOrientationPatient);
            if (att == null) {
                System.out.println("WARNING! File does not contain 'ImageOrientationPatient' DICOM TAG: " + fileName);
                continue;
            }

            atls.add(atl);
            fileListFiltered.add(fileName);

        }

        int numberOfFrames = 1;
        int N = atls.size();
        if (N == 0) {
            throw new ReadDICOMException("ERROR: no files to read after DICOM tagging check!");
        }
        log.debug("number of files after filtering tags: " + N);

        //check coherency
        log.debug("Check coherency of DICOM file list");
        try {
            int cols = 0, rows = 0, tmpcols, tmprows, seriesNo = -1, tmpseriesNo;

            double[] pixelSpacingTmp, pixelSpacing = {1.0, 1.0};

            double[] imageOrientation = {1.0, 0.0, 0.0, 0.0, 1.0, 0.0};
            double[] imageOrientationTmp;
            
            

            String photometricInterpretation = "", tmpphotometricInterpretation;


            att = atls.get(0).get(TagFromName.Columns);
            if(att != null) cols = att.getSingleIntegerValueOrDefault(0);
            att = atls.get(0).get(TagFromName.Rows);
            if(att != null) rows = att.getSingleIntegerValueOrDefault(0);
            att = atls.get(0).get(TagFromName.SeriesNumber);
            if(att != null) seriesNo = att.getSingleIntegerValueOrDefault(-1);
            att = atls.get(0).get(TagFromName.PhotometricInterpretation);
            if(att != null) photometricInterpretation = att.getSingleStringValueOrDefault("");

            att = atls.get(0).get(TagFromName.NumberOfFrames);
            if(att != null)
                numberOfFrames = att.getSingleIntegerValueOrDefault(1);
            
            att = atls.get(0).get(TagFromName.ImageOrientationPatient);
            if(!params.isIgnoreOrientation() && att != null)
                imageOrientation = att.getDoubleValues();

            att = atls.get(0).get(TagFromName.PixelSpacing);
            if(att != null)
                pixelSpacing = att.getDoubleValues();

            for (int i = 1; i < atls.size(); i++) {
                att = atls.get(i).get(TagFromName.Columns);
                tmpcols = att.getSingleIntegerValueOrDefault(0);
                if (!(tmpcols == cols && tmpcols > 0)) {
                    System.out.println("WARNING! File incoherent in DICOM TAG 'Columns': " + fileListFiltered.get(i));
                    atls.remove(i);
                    fileListFiltered.remove(i);
                    i--;
                    continue;
                }

                att = atls.get(i).get(TagFromName.Rows);
                tmprows = att.getSingleIntegerValueOrDefault(0);
                if (!(tmprows == rows && tmprows > 0)) {
                    System.out.println("WARNING! File incoherent in DICOM TAG 'Rows': " + fileListFiltered.get(i));
                    atls.remove(i);
                    fileListFiltered.remove(i);
                    i--;
                    continue;
                }

                att = atls.get(i).get(TagFromName.SeriesNumber);
                tmpseriesNo = att.getSingleIntegerValueOrDefault(-1);
                //if (!(tmpseriesNo == seriesNo && seriesNo >= 0)) {
                if (tmpseriesNo != seriesNo) {
                    System.out.println("WARNING! File incoherent in DICOM TAG 'SeriesNumber': " + fileListFiltered.get(i));
                    atls.remove(i);
                    fileListFiltered.remove(i);
                    i--;
                    continue;
                }

                att = atls.get(i).get(TagFromName.PhotometricInterpretation);
                tmpphotometricInterpretation = att.getSingleStringValueOrDefault("");
                if (!(tmpphotometricInterpretation.equals(photometricInterpretation))) {
                    System.out.println("WARNING! File incoherent in DICOM TAG 'PhotometricInterpretation': " + fileListFiltered.get(i));
                    atls.remove(i);
                    fileListFiltered.remove(i);
                    i--;
                    continue;
                }


                double orientationEps = 0.000001;
                att = atls.get(i).get(TagFromName.ImageOrientationPatient);
                if(att != null) {
                    imageOrientationTmp = att.getDoubleValues();
                } else {
                    imageOrientationTmp = imageOrientation;
                }
                if (!params.isIgnoreOrientation() && !(imageOrientationTmp != null
                        && imageOrientationTmp.length == 6
                        && Math.abs(imageOrientationTmp[0] - imageOrientation[0]) < orientationEps
                        && Math.abs(imageOrientationTmp[1] - imageOrientation[1]) < orientationEps
                        && Math.abs(imageOrientationTmp[2] - imageOrientation[2]) < orientationEps
                        && Math.abs(imageOrientationTmp[3] - imageOrientation[3]) < orientationEps
                        && Math.abs(imageOrientationTmp[4] - imageOrientation[4]) < orientationEps
                        && Math.abs(imageOrientationTmp[5] - imageOrientation[5]) < orientationEps)) {
                    System.out.println("WARNING! File incoherent in DICOM TAG 'ImageOrientationPatient': " + fileListFiltered.get(i));
                    atls.remove(i);
                    fileListFiltered.remove(i);
                    i--;
                    continue;
                }

                att = atls.get(i).get(TagFromName.PixelSpacing);
                pixelSpacingTmp = att.getDoubleValues();
                if (!(pixelSpacingTmp != null && pixelSpacingTmp.length == 2 && pixelSpacingTmp[0] == pixelSpacing[0] && pixelSpacingTmp[1] == pixelSpacing[1])) {
                    System.out.println("WARNING! File incoherent in DICOM TAG 'PixelSpacing': " + fileListFiltered.get(i));
                    atls.remove(i);
                    fileListFiltered.remove(i);
                    i--;
                    continue;
                }
                
                att = atls.get(i).get(TagFromName.NumberOfFrames);
                if(att != null) {
                    int tmpNumberOfFrames = att.getSingleIntegerValueOrDefault(1);
                    if(tmpNumberOfFrames != numberOfFrames) {
                        System.out.println("WARNING! File incoherent in DICOM TAG 'NumberOfFrames': " + fileListFiltered.get(i));
                        atls.remove(i);
                        fileListFiltered.remove(i);
                        i--;
                        continue;
                    }
                }
            }

            N = atls.size();
            if (N == 0) {
                throw new ReadDICOMException("ERROR: no files to read after coherency check!");
            }
            log.debug("number of files after coherency filtering: " + N);

            //list is DICOM checked and coherent for reading
            log.debug("DICOM file list filtered and coherency-checked");

            String patientName = "patient";
            att = atls.get(0).get(TagFromName.PatientName);
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
            params.setPatientName(patientName);
            
            
            //filelist ready
            if (N == 1 && numberOfFrames == 1) {
                field = readSingleDicomFile(atls.get(0), fileListFiltered.get(0));
            } else if (N == 1 && numberOfFrames > 1) {
                field = readSingleDicomFramesFile(atls.get(0), fileListFiltered.get(0));                                
            } else {
                field = readMultipleDicomFiles(atls, fileListFiltered);
            }
        } catch (DicomException ex) {
            throw new ReadDICOMException("ERROR: DICOM exception occured: " + ex.getMessage());
        } catch (IOException ex) {
            throw new ReadDICOMException("ERROR: IO exception occured: " + ex.getMessage());
        }

        atls = null;
        System.gc();
        return field;
    }

    public RegularField readSingleDicomFile(AttributeList atl, String filePath) throws DicomException, IOException {
        ArrayList<DICOMSortingEntry> entries = new ArrayList<DICOMSortingEntry>();
        entries.add(new DICOMSortingEntry(filePath, atl, 0));

        Attribute att;
        int cols, rows;
        att = atl.get(TagFromName.Columns);
        if (att == null) {
            return null;
        }
        cols = att.getSingleIntegerValueOrDefault(0);
        att = atl.get(TagFromName.Rows);
        if (att == null) {
            return null;
        }
        rows = att.getSingleIntegerValueOrDefault(0);

        if (rows == 0 || cols == 0) {
            return null;
        }

        att = atl.get(TagFromName.PhotometricInterpretation);
        if (att == null) {
            return null;
        }
        String photometricInterpretation = att.getSingleStringValueOrNull();
        if (photometricInterpretation == null) {
            return null;
        }

        if (!(
                photometricInterpretation.equals("MONOCHROME1") || 
                photometricInterpretation.equals("MONOCHROME2") || 
                photometricInterpretation.equals("PALETTE COLOR") ||
                photometricInterpretation.equals("RGB")
            )) {
            System.err.println("ERROR: DICOM photometric interpretation '" + photometricInterpretation + "' not supported!");
            return null;
        }

        int[] downsize = params.getDownsize();
        int[] dims = new int[2];
        dims[0] = (int) Math.ceil((double) cols / (double) downsize[0]);
        dims[1] = (int) Math.ceil((double) rows / (double) downsize[1]);
        RegularField field = new RegularField(dims);
        DataArray[] outDataArray = null;

        DicomReaderCore drc = null;
        
        if (photometricInterpretation.equals("MONOCHROME1") || photometricInterpretation.equals("MONOCHROME2")) {
            drc = new DicomReaderCoreMonochrome(histoArea);
        } else if (photometricInterpretation.equals("PALETTE COLOR")) {
            if (params.getReadAs() != Params.READ_AS_AUTO) {
                System.out.println("WARNING! Color DICOM data detected. Switching read type to AUTO!");
                params.setReadAs(Params.READ_AS_AUTO);
            }
            drc = new DicomReaderCorePaletteColor();
        } else if (photometricInterpretation.equals("RGB")) {
            if (params.getReadAs() != Params.READ_AS_AUTO) {
                System.out.println("WARNING! Color DICOM data detected. Switching read type to AUTO!");
                params.setReadAs(Params.READ_AS_AUTO);
            }
            drc = new DicomReaderCoreRGB();
        }
        if(drc == null)
            return null;
        
        drc.addFloatValueModificationListener(new FloatValueModificationListener() {
            @Override
            public void floatValueChanged(FloatValueModificationEvent e) {
                Core.this.progress = e.getVal();
                fireStatusChanged(progress);
            }
        });
        outDataArray = drc.readDicomDataArray(entries, params.getReadAs(), params.getLow(), params.getHigh(), dims, downsize, false, false, 1.0f);

       if (outDataArray != null)
       {
          for (int j = 0; j < outDataArray.length; j++)
          {
             if (outDataArray[j] != null)
             {
                if (params.getReadAs() == Params.READ_AS_BYTES)
                {
                   outDataArray[j].setPhysMin(params.getLow());
                   outDataArray[j].setPhysMax(params.getHigh());
                }
                field.addData(outDataArray[j]);
             }
          }
       }

        int N = entries.size();
        double w = cols, h = rows;

        double[] pixelSpacing = {1.0, 1.0};
        att = atl.get(TagFromName.PixelSpacing);
        if (att != null) {
            pixelSpacing = att.getDoubleValues();
        }

        double[] imagePositionFirst = null;
        att = atl.get(TagFromName.ImagePositionPatient);
        if (att != null) {
            imagePositionFirst = att.getDoubleValues();
        } else {
            imagePositionFirst = new double[3];
            imagePositionFirst[0] = 0;
            imagePositionFirst[1] = 0;
            imagePositionFirst[2] = 0;
        }

        double[] imageOrientation = {1.0, 0.0, 0.0, 0.0, 1.0, 0.0};
        att = atl.get(TagFromName.ImageOrientationPatient);
        if (!params.isIgnoreOrientation() && att != null) {
            imageOrientation = att.getDoubleValues();
        }

        double[][] daffine = new double[4][3];
        daffine[3][0] = imagePositionFirst[0];
        daffine[3][1] = imagePositionFirst[1];
        daffine[3][2] = imagePositionFirst[2];

        daffine[0][0] = imageOrientation[0] * (double) pixelSpacing[0];
        daffine[0][1] = imageOrientation[1] * (double) pixelSpacing[0];
        daffine[0][2] = imageOrientation[2] * (double) pixelSpacing[0];
        daffine[1][0] = imageOrientation[3] * (double) pixelSpacing[1];
        daffine[1][1] = imageOrientation[4] * (double) pixelSpacing[1];
        daffine[1][2] = imageOrientation[5] * (double) pixelSpacing[1];

        daffine[2][0] = daffine[0][1] * daffine[1][2] - daffine[0][2] * daffine[1][1];
        daffine[2][1] = daffine[0][2] * daffine[1][0] - daffine[0][0] * daffine[1][2];
        daffine[2][2] = daffine[0][0] * daffine[1][1] - daffine[0][1] * daffine[1][0];

        //normalize affine to m
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                daffine[i][j] /= 1000.0;
            }
        }

        //center affine
        double[][] extents = new double[2][3];
        extents[0][0] = daffine[3][0];
        extents[0][1] = daffine[3][1];
        extents[0][2] = daffine[3][2];
        for (int i = 0; i < 3; i++) {
            extents[1][i] = extents[0][i] + daffine[0][i] * (w - 1) + daffine[1][i] * (h - 1) + daffine[2][i] * (N - 1);
        }
        for (int i = 0; i < 3; i++) {
            daffine[3][i] -= (extents[1][i] + extents[0][i]) / 2.0;
        }


        //set affine to field
        float[][] affine = new float[4][3];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                affine[i][j] = (float) daffine[i][j];
            }
        }

        if(params.isScaleToMM()) {
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 3; j++) {
                    affine[i][j] = affine[i][j]*1000;
                }
            }
        }

        field.setAffine(affine);

        float[][] physExtents = new float[2][3];
        for (int i = 0; i < 3; i++) {
            physExtents[0][i] = (float) (extents[0][i]);
            physExtents[1][i] = (float) (extents[1][i]);
        }

        if(params.isScaleToMM()) {
            for (int i = 0; i < 3; i++) {
                physExtents[0][i] = physExtents[0][i]*1000;
                physExtents[1][i] = physExtents[0][i]*1000;
            }
        }

        field.setPhysExts(physExtents);

        return field;
    }

    public RegularField readSingleDicomFramesFile(AttributeList atl, String filePath) throws DicomException, IOException {
        ArrayList<DICOMSortingEntry> entries = new ArrayList<DICOMSortingEntry>();
        entries.add(new DICOMSortingEntry(filePath, atl, 0));

        Attribute att;
        int cols, rows;
        att = atl.get(TagFromName.Columns);
        if (att == null) {
            return null;
        }
        cols = att.getSingleIntegerValueOrDefault(0);
        att = atl.get(TagFromName.Rows);
        if (att == null) {
            return null;
        }
        rows = att.getSingleIntegerValueOrDefault(0);

        if (rows == 0 || cols == 0) {
            return null;
        }
        
        int numberOfFrames = 1;
        att = atl.get(TagFromName.NumberOfFrames);
        if(att != null)
            numberOfFrames = att.getSingleIntegerValueOrDefault(1);
        
        if(numberOfFrames == 1)
            return readSingleDicomFile(atl, filePath);
        else {
            if(params.isFramesAsTime()) {
                return readSingleDicomFramesFileAsTime(atl, filePath);
            } else
                return readSingleDicomFramesFileAsDim(atl, filePath);
        }
    }
    
    public RegularField readSingleDicomFramesFileAsDim(AttributeList atl, String filePath) throws DicomException, IOException {
        DICOMSortingEntry entry = new DICOMSortingEntry(filePath, atl, 0);

        Attribute att;
        int cols, rows;
        att = atl.get(TagFromName.Columns);
        if (att == null) {
            return null;
        }
        cols = att.getSingleIntegerValueOrDefault(0);
        att = atl.get(TagFromName.Rows);
        if (att == null) {
            return null;
        }
        rows = att.getSingleIntegerValueOrDefault(0);

        if (rows == 0 || cols == 0) {
            return null;
        }

        int numberOfFrames = 1;
        att = atl.get(TagFromName.NumberOfFrames);
        if(att != null)
            numberOfFrames = att.getSingleIntegerValueOrDefault(1);
        
        if(numberOfFrames == 1)
            return readSingleDicomFile(atl, filePath);
        
        att = atl.get(TagFromName.PhotometricInterpretation);
        if (att == null) {
            return null;
        }
        String photometricInterpretation = att.getSingleStringValueOrNull();
        if (photometricInterpretation == null) {
            return null;
        }

        if (!(photometricInterpretation.equals("MONOCHROME1") || 
              photometricInterpretation.equals("MONOCHROME2") || 
              photometricInterpretation.equals("PALETTE COLOR"))) {
            System.err.println("ERROR: DICOM photometric interpretation '" + 
                    photometricInterpretation + "' not supported!");
            return null;
        }

        int[] downsize = params.getDownsize();
        int[] dims = new int[3];
        dims[0] = (int) Math.ceil((double) cols / (double) downsize[0]);
        dims[1] = (int) Math.ceil((double) rows / (double) downsize[1]);
        dims[2] = (int) Math.ceil((double) numberOfFrames / (double) downsize[2]);
        RegularField field = new RegularField(dims);
        DataArray[] outDataArray = null;
        DicomReaderCore drc = null;

        if (photometricInterpretation.equals("MONOCHROME1") || 
            photometricInterpretation.equals("MONOCHROME2")) {
            drc = new DicomReaderCoreMonochrome(histoArea);
        } else if (photometricInterpretation.equals("PALETTE COLOR")) {
            if (params.getReadAs() != Params.READ_AS_AUTO) {
                System.out.println("WARNING! Color DICOM data detected. Switching read type to AUTO!");
                params.setReadAs(Params.READ_AS_AUTO);
            }
            drc = new DicomReaderCorePaletteColor();
        } else if (photometricInterpretation.equals("RGB")) {
            if (params.getReadAs() != Params.READ_AS_AUTO) {
                System.out.println("WARNING! Color DICOM data detected. Switching read type to AUTO!");
                params.setReadAs(Params.READ_AS_AUTO);
            }
            drc = new DicomReaderCoreRGB();
        }
        if(drc == null)
            return null;
        
        drc.addFloatValueModificationListener(new FloatValueModificationListener() {
            @Override
            public void floatValueChanged(FloatValueModificationEvent e) {
                Core.this.progress = e.getVal();
                fireStatusChanged(progress);
            }
        });
        outDataArray = drc.readDicomDataArrayFrames(entry, params.getReadAs(), 
                                                       params.getLow(), params.getHigh(), 
                                                       dims, downsize, false, 1.0f, true, 0, 0);

       if (outDataArray != null)
       {
          for (int j = 0; j < outDataArray.length; j++)
          {
             if (outDataArray[j] != null)
             {
                if (params.getReadAs() == Params.READ_AS_BYTES)
                {
                   outDataArray[j].setPhysMin(params.getLow());
                   outDataArray[j].setPhysMax(params.getHigh());
                }
                field.addData(outDataArray[j]);
             }
          }
       }

        double w = cols, h = rows, d = numberOfFrames;

        Attribute attPixelSpacing, attImagePositionPatient, attImageOrientationPatient, attSliceThickness;
        att = null;
        
        
        double[] pixelSpacing = {1.0, 1.0};
        double sliceThickness = 1.0;
        double[][] daffine = new double[4][3];
        for (int i = 0; i < 3; i++) {
            daffine[i][i] = 1.0;            
        }
        
        attPixelSpacing = atl.get(TagFromName.PixelSpacing);
        attImagePositionPatient = atl.get(TagFromName.ImagePositionPatient);
        attImageOrientationPatient = atl.get(TagFromName.ImageOrientationPatient);
        att = atl.get(TagFromName.SharedFunctionalGroupsSequence);            
        AttributeList seqAtl = null;
        if(att != null && ((SequenceAttribute)att).getItem(0) != null) {
            seqAtl = ((SequenceAttribute)att).getItem(0).getAttributeList();
        }
        
        if(attPixelSpacing != null && attImageOrientationPatient != null && attImagePositionPatient != null) {
            pixelSpacing = attPixelSpacing.getDoubleValues();
            double[] imagePositionFirst = attImagePositionPatient.getDoubleValues();
            double[] imageOrientation = {1.0, 0.0, 0.0, 0.0, 1.0, 0.0};
            if(!params.isIgnoreOrientation() && attImageOrientationPatient != null) {
                imageOrientation = attImageOrientationPatient.getDoubleValues();
            }
            if(pixelSpacing != null || imagePositionFirst != null || imageOrientation != null) {
                daffine[3][0] = imagePositionFirst[0];
                daffine[3][1] = imagePositionFirst[1];
                daffine[3][2] = imagePositionFirst[2];

                daffine[0][0] = imageOrientation[0] * pixelSpacing[0];
                daffine[0][1] = imageOrientation[1] * pixelSpacing[0];
                daffine[0][2] = imageOrientation[2] * pixelSpacing[0];
                daffine[1][0] = imageOrientation[3] * pixelSpacing[1];
                daffine[1][1] = imageOrientation[4] * pixelSpacing[1];
                daffine[1][2] = imageOrientation[5] * pixelSpacing[1];

                daffine[2][0] = daffine[0][1] * daffine[1][2] - daffine[0][2] * daffine[1][1];
                daffine[2][1] = daffine[0][2] * daffine[1][0] - daffine[0][0] * daffine[1][2];
                daffine[2][2] = daffine[0][0] * daffine[1][1] - daffine[0][1] * daffine[1][0];
                
                
                //TODO downsize
            }
        } else if(attPixelSpacing != null) {
            pixelSpacing = attPixelSpacing.getDoubleValues();
            att = atl.get(TagFromName.SliceThickness);            
            if(att != null) {                
                sliceThickness = att.getSingleDoubleValueOrDefault(0);
                if(sliceThickness > 0) {
                    daffine[0][0] = pixelSpacing[0];
                    daffine[1][1] = pixelSpacing[1];
                    daffine[2][2] = sliceThickness;
                    
                    daffine[3][0] = -(dims[0]*pixelSpacing[0])/2.0;
                    daffine[3][1] = -(dims[1]*pixelSpacing[1])/2.0;
                    daffine[3][2] = -(dims[2]*sliceThickness)/2.0;
                }
            }
        } else if(seqAtl != null) {
            Attribute mps = seqAtl.get(TagFromName.PixelMeasuresSequence);
            AttributeList mpsAtl = ((SequenceAttribute)mps).getItem(0).getAttributeList();
            attPixelSpacing = mpsAtl.get(TagFromName.PixelSpacing);
            att = mpsAtl.get(TagFromName.SliceThickness);            
            if(attPixelSpacing != null && att != null) {
                pixelSpacing = attPixelSpacing.getDoubleValues();
                sliceThickness = att.getSingleDoubleValueOrDefault(0);
                if(pixelSpacing != null && sliceThickness > 0) {
                    daffine[0][0] = pixelSpacing[0] * downsize[0];
                    daffine[1][1] = pixelSpacing[1] * downsize[1];
                    daffine[2][2] = sliceThickness * downsize[2];

                    daffine[3][0] = -(w*pixelSpacing[0])/2.0;
                    daffine[3][1] = -(h*pixelSpacing[1])/2.0;
                    daffine[3][2] = -(d*sliceThickness)/2.0;
                }
            }
        } else {
            daffine[0][0] = pixelSpacing[0] * downsize[0];
            daffine[1][1] = pixelSpacing[1] * downsize[1];
            daffine[2][2] = sliceThickness * downsize[2];

            daffine[3][0] = -(w*pixelSpacing[0])/2.0;
            daffine[3][1] = -(h*pixelSpacing[1])/2.0;
            daffine[3][2] = -(d*sliceThickness)/2.0;
        }
        
        //normalize affine to m
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                daffine[i][j] /= 1000.0;
            }
        }

        //center affine
        double[][] extents = new double[2][3];
        extents[0][0] = daffine[3][0];
        extents[0][1] = daffine[3][1];
        extents[0][2] = daffine[3][2];
        for (int i = 0; i < 3; i++) {
            extents[1][i] = extents[0][i] + daffine[0][i] * (w - 1) + daffine[1][i] * (h - 1) + daffine[2][i] * (d - 1);
        }
        for (int i = 0; i < 3; i++) {
            daffine[3][i] -= (extents[1][i] + extents[0][i]) / 2.0;
        }


        //set affine to field
        float[][] affine = new float[4][3];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                affine[i][j] = (float) daffine[i][j];
            }
        }

        if(params.isScaleToMM()) {
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 3; j++) {
                    affine[i][j] = affine[i][j]*1000;
                }
            }
        }

        field.setAffine(affine);

        float[][] physExtents = new float[2][3];
        for (int i = 0; i < 3; i++) {
            physExtents[0][i] = (float) (extents[0][i]);
            physExtents[1][i] = (float) (extents[1][i]);
        }

        if(params.isScaleToMM()) {
            for (int i = 0; i < 3; i++) {
                physExtents[0][i] = physExtents[0][i]*1000;
                physExtents[1][i] = physExtents[0][i]*1000;
            }
        }

        field.setPhysExts(physExtents);
        
        return field;
    }

    public RegularField readSingleDicomFramesFileAsTime(AttributeList atl, String filePath) throws DicomException, IOException {
        DICOMSortingEntry entry = new DICOMSortingEntry(filePath, atl, 0);

        Attribute att;
        int cols, rows;
        att = atl.get(TagFromName.Columns);
        if (att == null) {
            return null;
        }
        cols = att.getSingleIntegerValueOrDefault(0);
        att = atl.get(TagFromName.Rows);
        if (att == null) {
            return null;
        }
        rows = att.getSingleIntegerValueOrDefault(0);

        if (rows == 0 || cols == 0) {
            return null;
        }

        int numberOfFrames = 1;
        att = atl.get(TagFromName.NumberOfFrames);
        if(att != null)
            numberOfFrames = att.getSingleIntegerValueOrDefault(1);
        
        if(numberOfFrames == 1)
            return readSingleDicomFile(atl, filePath);
        
        att = atl.get(TagFromName.PhotometricInterpretation);
        if (att == null) {
            return null;
        }
        String photometricInterpretation = att.getSingleStringValueOrNull();
        if (photometricInterpretation == null) {
            return null;
        }

        if (!(photometricInterpretation.equals("MONOCHROME1") || 
              photometricInterpretation.equals("MONOCHROME2") || 
              photometricInterpretation.equals("PALETTE COLOR"))) {
            System.err.println("ERROR: DICOM photometric interpretation '" + 
                    photometricInterpretation + "' not supported!");
            return null;
        }

        int[] downsize = params.getDownsize();
        int[] dims = new int[2];
        dims[0] = (int) Math.ceil((double) cols / (double) downsize[0]);
        dims[1] = (int) Math.ceil((double) rows / (double) downsize[1]);
        RegularField field = new RegularField(dims);
        DataArray[] outDataArray = null;
        DicomReaderCore drc = null;
        
        int framesRangeLow = 0, framesRangeUp = 0;
        
        if(params.getFramesRange() != null) {
            framesRangeLow = params.getFramesRange()[0];
            if(framesRangeLow < 0) framesRangeLow = 0;
            if(framesRangeLow >= numberOfFrames) framesRangeLow = numberOfFrames-1;
            framesRangeUp = params.getFramesRange()[1];
            if(framesRangeUp < 0) framesRangeUp = 0;
            if(framesRangeUp >= numberOfFrames) framesRangeUp = numberOfFrames-1;
            if(framesRangeUp < framesRangeLow)
                framesRangeUp = framesRangeLow;
        }
        

        if (photometricInterpretation.equals("MONOCHROME1") || 
            photometricInterpretation.equals("MONOCHROME2")) {
            drc = new DicomReaderCoreMonochrome(histoArea);
        } else if (photometricInterpretation.equals("PALETTE COLOR")) {
            if (params.getReadAs() != Params.READ_AS_AUTO) {
                System.out.println("WARNING! Color DICOM data detected. Switching read type to AUTO!");
                params.setReadAs(Params.READ_AS_AUTO);
            }
            drc = new DicomReaderCorePaletteColor();
        } else if (photometricInterpretation.equals("RGB")) {
            if (params.getReadAs() != Params.READ_AS_AUTO) {
                System.out.println("WARNING! Color DICOM data detected. Switching read type to AUTO!");
                params.setReadAs(Params.READ_AS_AUTO);
            }
            drc = new DicomReaderCoreRGB();
        }
        if(drc == null)
            return null;
        
        drc.addFloatValueModificationListener(new FloatValueModificationListener() {
            @Override
            public void floatValueChanged(FloatValueModificationEvent e) {
                Core.this.progress = e.getVal();
                fireStatusChanged(progress);
            }
        });
        outDataArray = drc.readDicomDataArrayFrames(entry, params.getReadAs(), 
                                                       params.getLow(), params.getHigh(), 
                                                       dims, downsize, false, 1.0f, false, framesRangeLow, framesRangeUp);

       if (outDataArray != null)
       {
          for (int j = 0; j < outDataArray.length; j++)
          {
             if (outDataArray[j] != null)
             {
                if (params.getReadAs() == Params.READ_AS_BYTES)
                {
                   outDataArray[j].setPhysMin(params.getLow());
                   outDataArray[j].setPhysMax(params.getHigh());
                }
                field.addData(outDataArray[j]);
             }
          }
       }

        double w = cols, h = rows;

        Attribute attPixelSpacing, attImagePositionPatient, attImageOrientationPatient;
        att = null;
        
        
        double[] pixelSpacing = {1.0, 1.0};
        double[][] daffine = new double[4][3];
        for (int i = 0; i < 3; i++) {
            daffine[i][i] = 1.0;            
        }
        
        attPixelSpacing = atl.get(TagFromName.PixelSpacing);
        attImagePositionPatient = atl.get(TagFromName.ImagePositionPatient);
        attImageOrientationPatient = atl.get(TagFromName.ImageOrientationPatient);
        att = atl.get(TagFromName.SharedFunctionalGroupsSequence);            
        AttributeList seqAtl = null;
        if(att != null && ((SequenceAttribute)att).getItem(0) != null) {
            seqAtl = ((SequenceAttribute)att).getItem(0).getAttributeList();
        }
        
        if(attPixelSpacing != null && attImageOrientationPatient != null && attImagePositionPatient != null) {
            pixelSpacing = attPixelSpacing.getDoubleValues();
            double[] imagePositionFirst = attImagePositionPatient.getDoubleValues();
            double[] imageOrientation = {1.0, 0.0, 0.0, 0.0, 1.0, 0.0};
            if(!params.isIgnoreOrientation()) {
                imageOrientation = attImageOrientationPatient.getDoubleValues();
            }
            if(pixelSpacing != null || imagePositionFirst != null || imageOrientation != null) {
                daffine[3][0] = imagePositionFirst[0];
                daffine[3][1] = imagePositionFirst[1];
                daffine[3][2] = imagePositionFirst[2];

                daffine[0][0] = imageOrientation[0] * pixelSpacing[0];
                daffine[0][1] = imageOrientation[1] * pixelSpacing[0];
                daffine[0][2] = imageOrientation[2] * pixelSpacing[0];
                daffine[1][0] = imageOrientation[3] * pixelSpacing[1];
                daffine[1][1] = imageOrientation[4] * pixelSpacing[1];
                daffine[1][2] = imageOrientation[5] * pixelSpacing[1];

                //TODO downsize
            }
        } else if(attPixelSpacing != null) {
            pixelSpacing = attPixelSpacing.getDoubleValues();
            daffine[0][0] = pixelSpacing[0];
            daffine[1][1] = pixelSpacing[1];

            daffine[3][0] = -(dims[0]*pixelSpacing[0])/2.0;
            daffine[3][1] = -(dims[1]*pixelSpacing[1])/2.0;
            daffine[3][2] = 0.0f;
        } else if(seqAtl != null) {
            Attribute mps = seqAtl.get(TagFromName.PixelMeasuresSequence);
            AttributeList mpsAtl = ((SequenceAttribute)mps).getItem(0).getAttributeList();
            attPixelSpacing = mpsAtl.get(TagFromName.PixelSpacing);
            if(attPixelSpacing != null) {
                pixelSpacing = attPixelSpacing.getDoubleValues();
                if(pixelSpacing != null) {
                    daffine[0][0] = pixelSpacing[0] * downsize[0];
                    daffine[1][1] = pixelSpacing[1] * downsize[1];

                    daffine[3][0] = -(w*pixelSpacing[0])/2.0;
                    daffine[3][1] = -(h*pixelSpacing[1])/2.0;
                    daffine[3][2] = 0.0f;
                }
            }
        } else {
            daffine[0][0] = pixelSpacing[0] * downsize[0];
            daffine[1][1] = pixelSpacing[1] * downsize[1];

            daffine[3][0] = -(w*pixelSpacing[0])/2.0;
            daffine[3][1] = -(h*pixelSpacing[1])/2.0;
            daffine[3][2] = 0.0f;
        }
        
        //normalize affine to m
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                daffine[i][j] /= 1000.0;
            }
        }

        //center affine
        double[][] extents = new double[2][3];
        extents[0][0] = daffine[3][0];
        extents[0][1] = daffine[3][1];
        extents[0][2] = daffine[3][2];
        for (int i = 0; i < 3; i++) {
            extents[1][i] = extents[0][i] + daffine[0][i] * (w - 1) + daffine[1][i] * (h - 1);
        }
        for (int i = 0; i < 3; i++) {
            daffine[3][i] -= (extents[1][i] + extents[0][i]) / 2.0;
        }


        //set affine to field
        float[][] affine = new float[4][3];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                affine[i][j] = (float) daffine[i][j];
            }
        }

        if(params.isScaleToMM()) {
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 3; j++) {
                    affine[i][j] = affine[i][j]*1000;
                }
            }
        }

        field.setAffine(affine);

        float[][] physExtents = new float[2][3];
        for (int i = 0; i < 3; i++) {
            physExtents[0][i] = (float) (extents[0][i]);
            physExtents[1][i] = (float) (extents[1][i]);
        }

        if(params.isScaleToMM()) {
            for (int i = 0; i < 3; i++) {
                physExtents[0][i] = physExtents[0][i]*1000;
                physExtents[1][i] = physExtents[0][i]*1000;
            }
        }

        field.setPhysExts(physExtents);
        
        return field;
    }
    
    private RegularField readMultipleDicomFiles(ArrayList<AttributeList> atls, ArrayList<String> fileListFiltered) throws IOException, ReadDICOMException, DicomException {
        //analyze time frames in list
        log.debug("Analyze DICOM file list for time steps");
        int[][] framesLists = DicomReaderCore.analyzeDicomTimeSteps(atls);

        if (framesLists == null) {
            log.debug("DICOM file list does not contain time steps");
            ArrayList<DICOMSortingEntry> sortedEntries = DicomReaderCore.sortDicomFiles(atls, fileListFiltered, true, params.isIgnoreOrientation());
            if (sortedEntries == null) {
                throw new ReadDICOMException("ERROR: cannot reconstruct volume due to sorting error!");
            }
            return readDicomAsVolume(sortedEntries);
        } else {
            log.debug("DICOM file list contains " + framesLists.length + " time steps");

            //generate separate sorted lists
            ArrayList<DICOMSortingEntry>[] sortedEntriesTimeSteps = DicomReaderCore.sortDicomFilesTimeSteps(atls, fileListFiltered, framesLists, true, params.isIgnoreOrientation());
            if (sortedEntriesTimeSteps == null) {
                throw new ReadDICOMException("ERROR: cannot reconstruct volume due to sorting error!");
            }
            return readDicomAsVolumeTimeSteps(sortedEntriesTimeSteps);
        }
    }

    private RegularField readDicomAsVolume(ArrayList<DICOMSortingEntry> sortedEntries) throws IOException, DicomException {
        if (sortedEntries == null || sortedEntries.size() < 2) {
            return null;
        }

        AttributeList atl = sortedEntries.get(0).getHeader();
        AttributeList atlLast = sortedEntries.get(sortedEntries.size() - 1).getHeader();

        Attribute att;
        int cols, rows, slices;
        att = atl.get(TagFromName.Columns);
        cols = att.getSingleIntegerValueOrDefault(0);
        att = atl.get(TagFromName.Rows);
        rows = att.getSingleIntegerValueOrDefault(0);
        slices = sortedEntries.size();

        att = atl.get(TagFromName.PhotometricInterpretation);
        String photometricInterpretation = att.getSingleStringValueOrNull();
        if (photometricInterpretation == null) {
            return null;
        }

        if (!(photometricInterpretation.equals("MONOCHROME1") || photometricInterpretation.equals("MONOCHROME2") || photometricInterpretation.equals("PALETTE COLOR"))) {
            System.err.println("ERROR: DICOM photometric interpretation '" + photometricInterpretation + "' not supported!");
            return null;
        }

        if (rows == 0 || cols == 0 || slices == 0) {
            return null;
        }

        int[] dims;
        int[] downsize = params.getDownsize();
        if (slices == 1) {
            dims = new int[2];
            dims[0] = (int) Math.ceil((double) cols / (double) downsize[0]);
            dims[1] = (int) Math.ceil((double) rows / (double) downsize[1]);
        } else {
            dims = new int[3];
            dims[0] = (int) Math.ceil((double) cols / (double) downsize[0]);
            dims[1] = (int) Math.ceil((double) rows / (double) downsize[1]);
            dims[2] = (int) Math.ceil((double) slices / (double) downsize[2]);
        }

        RegularField field = new RegularField(dims);
        DataArray[] outDataArrays = null;
        DicomReaderCore drc = null;

        float progressModifier = 1.0f;
        if (params.isInterpolateData()) {
            progressModifier = 0.5f;
        }

        if (photometricInterpretation.equals("MONOCHROME1") || photometricInterpretation.equals("MONOCHROME2")) {
            drc = new DicomReaderCoreMonochrome(histoArea);
        } else if (photometricInterpretation.equals("PALETTE COLOR")) {
            if (params.getReadAs() != Params.READ_AS_AUTO) {
                System.out.println("WARNING! Color DICOM data detected. Switching read type to AUTO!");
                params.setReadAs(Params.READ_AS_AUTO);
            }
            drc = new DicomReaderCorePaletteColor();
        } else if (photometricInterpretation.equals("RGB")) {
            if (params.getReadAs() != Params.READ_AS_AUTO) {
                System.out.println("WARNING! Color DICOM data detected. Switching read type to AUTO!");
                params.setReadAs(Params.READ_AS_AUTO);
            }
            drc = new DicomReaderCoreRGB();
        }
        drc.addFloatValueModificationListener(new FloatValueModificationListener() {
            @Override
            public void floatValueChanged(FloatValueModificationEvent e) {
                Core.this.progress = e.getVal();
                fireStatusChanged(progress);
            }
        });
        outDataArrays = drc.readDicomDataArray(sortedEntries, params.getReadAs(), 
                                               params.getLow(), params.getHigh(), 
                       dims, downsize, params.isInpaintMissingSlices(),
               true, progressModifier);
       if (outDataArrays == null)
       {
          return null;
       }

       for (int i = 0; i < outDataArrays.length; i++)
       {
          if (params.getReadAs() == Params.READ_AS_BYTES)
          {
             outDataArrays[i].setPhysMin(params.getLow());
             outDataArrays[i].setPhysMax(params.getHigh());
          }
          field.addData(outDataArrays[i]);
       }


        double w = dims[0], h = dims[1], d = dims[2];

        double[] pixelSpacing = {1.0, 1.0};
        att = atl.get(TagFromName.PixelSpacing);
        if (att != null) {
            pixelSpacing = att.getDoubleValues();
        }

        pixelSpacing[0] = pixelSpacing[0] * downsize[0];
        pixelSpacing[1] = pixelSpacing[1] * downsize[1];

        double[] imagePositionFirst = null;
        att = atl.get(TagFromName.ImagePositionPatient);
        if (att != null) {
            imagePositionFirst = att.getDoubleValues();
        } else {
            imagePositionFirst = new double[3];
            imagePositionFirst[0] = 0;
            imagePositionFirst[1] = 0;
            imagePositionFirst[2] = 0;
        }

        double[] imagePositionLast = null;
        att = atlLast.get(TagFromName.ImagePositionPatient);
        if (att != null) {
            imagePositionLast = att.getDoubleValues();
        } else {
            imagePositionLast = new double[3];
            imagePositionLast[0] = 0;
            imagePositionLast[1] = 0;
            imagePositionLast[2] = slices - 1;
        }

        double[] imageOrientation = {1.0, 0.0, 0.0, 0.0, 1.0, 0.0};
        att = atl.get(TagFromName.ImageOrientationPatient);
        if (!params.isIgnoreOrientation() && att != null) {
            imageOrientation = att.getDoubleValues();
        }

        double[][] daffine = new double[4][3];
        daffine[3][0] = imagePositionFirst[0];
        daffine[3][1] = imagePositionFirst[1];
        daffine[3][2] = imagePositionFirst[2];

        daffine[0][0] = imageOrientation[0] * (double) pixelSpacing[0];
        daffine[0][1] = imageOrientation[1] * (double) pixelSpacing[0];
        daffine[0][2] = imageOrientation[2] * (double) pixelSpacing[0];
        daffine[1][0] = imageOrientation[3] * (double) pixelSpacing[1];
        daffine[1][1] = imageOrientation[4] * (double) pixelSpacing[1];
        daffine[1][2] = imageOrientation[5] * (double) pixelSpacing[1];

        daffine[2][0] = (imagePositionLast[0] - imagePositionFirst[0]) / (double) (dims[2] - 1);
        daffine[2][1] = (imagePositionLast[1] - imagePositionFirst[1]) / (double) (dims[2] - 1);
        daffine[2][2] = (imagePositionLast[2] - imagePositionFirst[2]) / (double) (dims[2] - 1);


        //normalize affine to m
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                daffine[i][j] /= 1000.0;
            }
        }

        //center affine
        double[][] extents = new double[2][3];
        extents[0][0] = daffine[3][0];
        extents[0][1] = daffine[3][1];
        extents[0][2] = daffine[3][2];
        for (int i = 0; i < 3; i++) {
            extents[1][i] = extents[0][i] + daffine[0][i] * (w - 1) + daffine[1][i] * (h - 1) + daffine[2][i] * (d - 1);
        }
        for (int i = 0; i < 3; i++) {
            daffine[3][i] -= (extents[1][i] + extents[0][i]) / 2.0;
        }


        //set affine to field
        float[][] affine = new float[4][3];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                affine[i][j] = (float) daffine[i][j];
            }
        }

        if(params.isScaleToMM()) {
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 3; j++) {
                    affine[i][j] = affine[i][j]*1000;
                }
            }
        }

        field.setAffine(affine);

        float[][] physExtents = new float[2][3];
        for (int i = 0; i < 3; i++) {
            physExtents[0][i] = (float) (extents[0][i]);
            physExtents[1][i] = (float) (extents[1][i]);
        }

        if(params.isScaleToMM()) {
            for (int i = 0; i < 3; i++) {
                physExtents[0][i] = physExtents[0][i]*1000;
                physExtents[1][i] = physExtents[0][i]*1000;
            }
        }

        field.setPhysExts(physExtents);

        return field;
    }

    private RegularField readDicomAsVolumeTimeSteps(ArrayList<DICOMSortingEntry>[] entriesFrames) throws IOException, DicomException {
        if (entriesFrames == null || entriesFrames.length < 1) {
            return null;
        }

        ArrayList<DICOMSortingEntry> entries = entriesFrames[0];

        DicomInputStream dis = new DicomInputStream(new File(entries.get(0).getFilePath()));
        AttributeList atl = new AttributeList();
        atl.read(dis, TagFromName.PixelData);
        dis.close();

        dis = new DicomInputStream(new File(entries.get(entries.size() - 1).getFilePath()));
        AttributeList atlLast = new AttributeList();
        atlLast.read(dis, TagFromName.PixelData);
        dis.close();


        Attribute att;
        int cols, rows, slices;
        att = atl.get(TagFromName.Columns);
        cols = att.getSingleIntegerValueOrDefault(0);
        att = atl.get(TagFromName.Rows);
        rows = att.getSingleIntegerValueOrDefault(0);
        slices = entries.size();

        att = atl.get(TagFromName.PhotometricInterpretation);
        String photometricInterpretation = att.getSingleStringValueOrNull();
        if (photometricInterpretation == null) {
            return null;
        }

        if (!(photometricInterpretation.equals("MONOCHROME1") || photometricInterpretation.equals("MONOCHROME2") || photometricInterpretation.equals("PALETTE COLOR"))) {
            System.err.println("ERROR: DICOM photometric interpretation '" + photometricInterpretation + "' not supported!");
            return null;
        }

        if (rows == 0 || cols == 0 || slices == 0) {
            return null;
        }

        int[] dims;
        int[] downsize = params.getDownsize();
        if (slices == 1) {
            dims = new int[2];
            dims[0] = (int) Math.ceil((double) cols / (double) downsize[0]);
            dims[1] = (int) Math.ceil((double) rows / (double) downsize[1]);
        } else {
            dims = new int[3];
            dims[0] = (int) Math.ceil((double) cols / (double) downsize[0]);
            dims[1] = (int) Math.ceil((double) rows / (double) downsize[1]);
            dims[2] = (int) Math.ceil((double) slices / (double) downsize[2]);
        }
        RegularField field = new RegularField(dims);
        DicomReaderCore drc = null;

        float progressModifier = 1.0f;
        if (params.isInterpolateData()) {
            progressModifier = 0.5f;
        }

        for (int i = 0; i < entriesFrames.length; i++) {
            DataArray[] outDataArray = null;
            if (photometricInterpretation.equals("MONOCHROME1") || photometricInterpretation.equals("MONOCHROME2")) {
                drc = new DicomReaderCoreMonochrome(histoArea);
            } else if (photometricInterpretation.equals("PALETTE COLOR")) {
                if (params.getReadAs() != Params.READ_AS_AUTO) {
                    System.out.println("WARNING! Color DICOM data detected. Switching read type to AUTO!");
                    params.setReadAs(Params.READ_AS_AUTO);
                }
                drc = new DicomReaderCorePaletteColor();
            }
            
            if(drc == null)
                return null;

            drc.addFloatValueModificationListener(new FloatValueModificationListener() {
                @Override
                public void floatValueChanged(FloatValueModificationEvent e) {
                    Core.this.progress = e.getVal();
                    fireStatusChanged(progress);
                }
            });
            outDataArray = drc.readDicomDataArray(entriesFrames[i], params.getReadAs(), params.getLow(), params.getHigh(), dims, downsize, params.isInpaintMissingSlices(), true, progressModifier);

            if (outDataArray != null) {
                for (int j = 0; j < outDataArray.length; j++) {
                    if (outDataArray[j] != null) {
                        outDataArray[j].setName("frame" + i + "_" + outDataArray[j].getName());
                        if (params.getReadAs() == Params.READ_AS_BYTES) {
                            outDataArray[j].setPhysMin(params.getLow());
                            outDataArray[j].setPhysMax(params.getHigh());
                        }
                        field.addData(outDataArray[j]);
                    }
                }
            }
        }

        int N = entries.size();
        double w = cols, h = rows;

        double[] pixelSpacing = {1.0, 1.0};
        att = atl.get(TagFromName.PixelSpacing);
        if (att != null) {
            pixelSpacing = att.getDoubleValues();
        }

        double[] imagePositionFirst = null;
        att = atl.get(TagFromName.ImagePositionPatient);
        if (att != null) {
            imagePositionFirst = att.getDoubleValues();
        } else {
            imagePositionFirst = new double[3];
            imagePositionFirst[0] = 0;
            imagePositionFirst[1] = 0;
            imagePositionFirst[2] = 0;
        }

        double[] imagePositionLast = null;
        att = atlLast.get(TagFromName.ImagePositionPatient);
        if (att != null) {
            imagePositionLast = att.getDoubleValues();
        } else {
            imagePositionLast = new double[3];
            imagePositionLast[0] = 0;
            imagePositionLast[1] = 0;
            imagePositionLast[2] = N - 1;
        }

        double[] imageOrientation = {1.0, 0.0, 0.0, 0.0, 1.0, 0.0};
        att = atl.get(TagFromName.ImageOrientationPatient);
        if (!params.isIgnoreOrientation() && att != null) {
            imageOrientation = att.getDoubleValues();
        }

        double[][] daffine = new double[4][3];
        daffine[3][0] = imagePositionFirst[0];
        daffine[3][1] = imagePositionFirst[1];
        daffine[3][2] = imagePositionFirst[2];

        daffine[0][0] = imageOrientation[0] * (double) pixelSpacing[0];
        daffine[0][1] = imageOrientation[1] * (double) pixelSpacing[0];
        daffine[0][2] = imageOrientation[2] * (double) pixelSpacing[0];
        daffine[1][0] = imageOrientation[3] * (double) pixelSpacing[1];
        daffine[1][1] = imageOrientation[4] * (double) pixelSpacing[1];
        daffine[1][2] = imageOrientation[5] * (double) pixelSpacing[1];

        daffine[2][0] = (imagePositionLast[0] - imagePositionFirst[0]) / (double) (N - 1);
        daffine[2][1] = (imagePositionLast[1] - imagePositionFirst[1]) / (double) (N - 1);
        daffine[2][2] = (imagePositionLast[2] - imagePositionFirst[2]) / (double) (N - 1);


        //normalize affine to m
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                daffine[i][j] /= 1000.0;
            }
        }

        //center affine
        double[][] extents = new double[2][3];
        extents[0][0] = daffine[3][0];
        extents[0][1] = daffine[3][1];
        extents[0][2] = daffine[3][2];
        for (int i = 0; i < 3; i++) {
            extents[1][i] = extents[0][i] + daffine[0][i] * (w - 1) + daffine[1][i] * (h - 1) + daffine[2][i] * (N - 1);
        }
        for (int i = 0; i < 3; i++) {
            daffine[3][i] -= (extents[1][i] + extents[0][i]) / 2.0;
        }


        //set affine to field
        float[][] affine = new float[4][3];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                affine[i][j] = (float) daffine[i][j];
            }
        }

        if(params.isScaleToMM()) {
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 3; j++) {
                    affine[i][j] = affine[i][j]*1000;
                }
            }
        }

        field.setAffine(affine);

        float[][] physExtents = new float[2][3];
        for (int i = 0; i < 3; i++) {
            physExtents[0][i] = (float) (extents[0][i]);
            physExtents[1][i] = (float) (extents[1][i]);
        }

        if(params.isScaleToMM()) {
            for (int i = 0; i < 3; i++) {
                physExtents[0][i] = physExtents[0][i]*1000;
                physExtents[1][i] = physExtents[0][i]*1000;
            }
        }

        field.setPhysExts(physExtents);

        return field;
    }
       
    private transient FloatValueModificationListener statusListener = null;

    public void addFloatValueModificationListener(FloatValueModificationListener listener) {
        if (statusListener == null) {
            this.statusListener = listener;
        } else {
            System.out.println("" + this + ": only one status listener can be added");
        }
    }

    private void fireStatusChanged(float status) {
        FloatValueModificationEvent e = new FloatValueModificationEvent(this, status, true);
        if (statusListener != null) {
            statusListener.floatValueChanged(e);
        }
    }
}
