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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.gui.events.FloatValueModificationEvent;
import pl.edu.icm.visnow.gui.events.FloatValueModificationListener;

/**
 *
 * @author Bartosz Borucki (babor@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public abstract class DicomReaderCore {

    protected float progress = 0.0f;
    
    public DicomReaderCore() {
    }

    public abstract DataArray[] readDicomDataArray(ArrayList<DICOMSortingEntry> entries, int readAs, int lowCrop, int highCrop, int[] dims, int[] downsize, boolean interpolateMissingSlices, boolean withProgress, float progressModifier) throws IOException, DicomException;

    public abstract DataArray[] readDicomDataArrayFrames(DICOMSortingEntry entry, int readAs, int lowCrop, int highCrop, int[] dims, int[] downsize, boolean withProgress, float progressModifier, boolean framesAsDim, int framesRangeLow, int framesRangeUp) throws IOException, DicomException;

    //public abstract boolean readDicomArrayOffset(DICOMSortingEntry entry, int[] iDataRed, int[] iDataGreen, int[] iDataBlue, int offset, int[] dims, int[] downsize) throws DicomException;
    
    //public abstract boolean readDicomArrayFramesDim(DICOMSortingEntry entry, int[] iDataRed, int[] iDataGreen, int[] iDataBlue, int[] dims, int[] downsize) throws DicomException;

    @SuppressWarnings("unchecked")
    public static ArrayList<DICOMSortingEntry> sortDicomFiles(ArrayList<AttributeList> atls, ArrayList<String> listToSort, boolean localizeSlices, boolean ignoreOrientation) {
        if (atls == null || listToSort == null || atls.size() < 2 || listToSort.size() < 2 || listToSort.size() != atls.size()) {
            return null;
        }

        Attribute att = atls.get(0).get(TagFromName.ImageOrientationPatient);
        double[] imageOrientation = {1.0, 0.0, 0.0, 0.0, 1.0, 0.0};
        if(!ignoreOrientation && att != null) {
            try {
                imageOrientation = att.getDoubleValues();
            } catch (DicomException ex) {
                imageOrientation = null;
            }
        }

        att = atls.get(0).get(TagFromName.ImagePositionPatient);
        if (att == null) {
            return null;
        }
        double[] imagePosition = null;
        try {
            imagePosition = att.getDoubleValues();
        } catch (DicomException ex) {
            imagePosition = null;
        }
        if (imagePosition == null || imagePosition.length != 3) {
            return null;
        }


        //create slice normal vector
        double[] n = new double[3];
        n[0] = imageOrientation[1] * imageOrientation[5] - imageOrientation[2] * imageOrientation[4];
        n[1] = imageOrientation[2] * imageOrientation[3] - imageOrientation[0] * imageOrientation[5];
        n[2] = imageOrientation[0] * imageOrientation[4] - imageOrientation[1] * imageOrientation[3];

        //normalize vector
        double norm = 0;
        for (int i = 0; i < 3; i++) {
            norm += n[i] * n[i];
        }
        norm = Math.sqrt(norm);
        for (int i = 0; i < 3; i++) {
            n[i] = n[i] / norm;
        }

        //crate reference point p0
        double[] p0 = new double[3];
        p0[0] = imagePosition[0];
        p0[1] = imagePosition[1];
        p0[2] = imagePosition[2];

        ArrayList<DICOMSortingEntry> entries = new ArrayList<DICOMSortingEntry>();
        double[] imagePositionTmp;
        double d;
        double[] v = new double[3];
        for (int i = 0; i < listToSort.size(); i++) {
            att = atls.get(i).get(TagFromName.ImagePositionPatient);
            if (att == null) {
                return null;
            }
            imagePositionTmp = null;
            try {
                imagePositionTmp = att.getDoubleValues();
            } catch (DicomException ex) {
                imagePositionTmp = null;
            }
            if (imagePositionTmp == null || imagePositionTmp.length != 3) {
                return null;
            }

            d = 0;
            v[0] = imagePositionTmp[0] - p0[0];
            v[1] = imagePositionTmp[1] - p0[1];
            v[2] = imagePositionTmp[2] - p0[2];

            for (int j = 0; j < 3; j++) {
                d += v[j] * n[j];
            } //n is normalized so we don't divide by |n|

            entries.add(new DICOMSortingEntry(listToSort.get(i), atls.get(i), d));
        }

        if(entries instanceof List)
            Collections.sort(entries);

        if (localizeSlices && !ignoreOrientation && entries.size() > 1) {
            double dFirst = entries.get(0).getDistance();
            double minD = Double.POSITIVE_INFINITY;
            double tmpd;
            for (int i = 1; i < entries.size(); i++) {
                tmpd = entries.get(i).getDistance() - entries.get(i - 1).getDistance();
                if (tmpd < minD) {
                    minD = tmpd;
                }
            }

            ArrayList<DICOMSortingEntry> localizedEntries = new ArrayList<DICOMSortingEntry>();
            int c = 0, i = 0, tmpi;
            if(minD == 0)
                return localizedEntries;
            
            while (c < entries.size()) {
                tmpi = (int) Math.round((entries.get(c).getDistance() - dFirst) / minD);
                if (tmpi == i) {
                    localizedEntries.add(entries.get(c));
                    c++;
                } else {
                    localizedEntries.add(null);
                }
                i++;
            }
            return localizedEntries;
        } else {
            return entries;
        }
    }

    public static int[][] analyzeDicomTimeSteps(ArrayList<AttributeList> atls) {

        //check if data is time dependant
        Attribute att1, att2, att;
        for (int i = 0; i < atls.size(); i++) {
            att = atls.get(i).get(TagFromName.TriggerTime);
            if (att == null) {
                return null;
            }
            double tt = att.getSingleDoubleValueOrDefault(-1);
            if (tt == -1) {
                return null;
            }
        }

        //data seems time dependant
        //check type SIEMENS/PHILIPS/??

        int nFrames = 0;
        int nSlices = 0;


        //-------------------------check PHILIPS--------------------------------
        boolean philips = false;
        int v;
        for (int i = 0; i < atls.size(); i++) {
            try {
                att1 = atls.get(i).get(new AttributeTag("(0x2001,0x1008"));
                if (att1 != null) {
                    v = att1.getSingleIntegerValueOrDefault(-1);
                    if (v > nFrames) {
                        nFrames = v;
                    }
                }
            } catch (DicomException ex) {
                att1 = null;
            }
            try {
                att2 = atls.get(i).get(new AttributeTag("(0x2001,0x100a"));
                if (att2 != null) {
                    v = att2.getSingleIntegerValueOrDefault(-1);
                    if (v > nSlices) {
                        nSlices = v;
                    }
                }
            } catch (DicomException ex) {
                att2 = null;
            }

            philips = (att1 != null && att2 != null);
            if (!philips) {
                break;
            }
        }

        if (philips) {
            int[][] out = new int[nFrames][nSlices];
            int f = -1, s = -1;
            for (int i = 0; i < atls.size(); i++) {
                try {
                    att1 = atls.get(i).get(new AttributeTag("(0x2001,0x1008"));
                    f = att1.getSingleIntegerValueOrDefault(-1);
                } catch (DicomException ex) {
                    att1 = null;
                }
                try {
                    att2 = atls.get(i).get(new AttributeTag("(0x2001,0x100a"));
                    s = att2.getSingleIntegerValueOrDefault(-1);
                } catch (DicomException ex) {
                    att2 = null;
                }

                if (att1 == null || att2 == null || f == -1 || s == -1) {
                    return null;
                }

                out[f - 1][s - 1] = i;
            }
            return out;
        }


        //-----------------check SIEMENS--------------------------------
        boolean siemens = false;
        int[] tmp = new int[atls.size()];
        for (int i = 0; i < atls.size(); i++) {
            tmp[i] = 0;
        }

        for (int i = 0; i < atls.size(); i++) {
            att = atls.get(i).get(TagFromName.InstanceNumber);
            v = att.getSingleIntegerValueOrDefault(-1);
            if (v == -1) {
                siemens = false;
                break;
            }

            tmp[v - 1]++;
        }

        nFrames = 0;
        for (int i = 0; i < tmp.length; i++) {
            if (tmp[i] == 0) {
                nFrames = i;
                break;
            }
        }

        nSlices = 0;
        if (nFrames > 0) {
            if (atls.size() % nFrames == 0) {
                nSlices = atls.size() / nFrames;
                siemens = true;
                for (int i = 0; i < nFrames; i++) {
                    if (tmp[i] != nSlices) {
                        siemens = false;
                        break;
                    }
                }
            }
        }

        if (siemens) {
            int[][] out = new int[nFrames][nSlices];
            int[] tmp2 = new int[nFrames];
            for (int i = 0; i < tmp2.length; i++) {
                tmp2[i] = 0;
            }
            int f = -1;

            for (int i = 0; i < atls.size(); i++) {
                att = atls.get(i).get(TagFromName.InstanceNumber);
                f = att.getSingleIntegerValueOrDefault(-1);
                if (f == -1) {
                    return null;
                }

                out[f - 1][tmp2[f - 1]] = i;
                tmp2[f - 1]++;
            }
            return out;
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<DICOMSortingEntry>[] sortDicomFilesTimeSteps(ArrayList<AttributeList> atls, ArrayList<String> fileList, int[][] framesLists, boolean localizeSlices, boolean ignoreOrientation) {
        if (atls == null || fileList == null || framesLists == null || fileList.size() != atls.size()) {
            return null;
        }

        int nFrames = framesLists.length;
        int nSlices = framesLists[0].length;
        if (nFrames * nSlices != atls.size()) {
            return null;
        }

        ArrayList[] out = new ArrayList[nFrames];
        for (int i = 0; i < out.length; i++) {
            out[i] = new ArrayList<DICOMSortingEntry>();
        }

        for (int i = 0; i < nFrames; i++) {
            ArrayList<AttributeList> tmpAtls = new ArrayList<AttributeList>();
            ArrayList<String> tmpFileList = new ArrayList<String>();
            for (int j = 0; j < nSlices; j++) {
                tmpAtls.add(atls.get(framesLists[i][j]));
                tmpFileList.add(fileList.get(framesLists[i][j]));
            }
            out[i] = sortDicomFiles(tmpAtls, tmpFileList, localizeSlices, ignoreOrientation);
            if (out[i] == null) {
                return null;
            }
        }
        return out;
    }
    
    
    private transient FloatValueModificationListener statusListener = null;

    public void addFloatValueModificationListener(FloatValueModificationListener listener) {
        if (statusListener == null) {
            this.statusListener = listener;
        } else {
            System.out.println("" + this + ": only one status listener can be added");
        }
    }

    protected void fireProgressChanged() {
        FloatValueModificationEvent e = new FloatValueModificationEvent(this, progress, true);
        if (statusListener != null) {
            statusListener.floatValueChanged(e);
        }
    }
    
    public static class DICOMSortingEntry implements Comparable {

        private String filePath;
        private AttributeList header;
        private double distance;

        public DICOMSortingEntry(String filePath, AttributeList header, double distance) {
            this.filePath = filePath;
            this.distance = distance;
            this.header = header;
        }

        @Override
        public int compareTo(Object o) {
            if (!(o instanceof DICOMSortingEntry)) {
                return 1;
            }

            double v = ((DICOMSortingEntry) o).getDistance();

            if (distance == Double.NEGATIVE_INFINITY && v == Double.NEGATIVE_INFINITY) {
                return 0;
            } else if (distance == Double.NEGATIVE_INFINITY && v != Double.NEGATIVE_INFINITY) {
                return -1;
            } else if (distance != Double.NEGATIVE_INFINITY && v == Double.NEGATIVE_INFINITY) {
                return 1;
            } else if (distance > v) {
                return 1;
            } else if (distance < v) {
                return -1;
            } else {
                return 0;
            }
        }

        public String getFilePath() {
            return filePath;
        }

        public double getDistance() {
            return distance;
        }

        public AttributeList getHeader() {
            return header;
        }
        
        public void setDistance(double distance) {
            this.distance = distance;
        }
    }
    
}
