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

import java.util.ArrayList;
import java.util.Vector;
import pl.edu.icm.visnow.engine.core.ParameterEgg;
import pl.edu.icm.visnow.engine.core.ParameterType;
import pl.edu.icm.visnow.engine.core.Parameters;
import pl.edu.icm.visnow.engine.core.Parameter;

/**
 *
 * @author  Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class Params extends Parameters {

    public static final int READ_AS_AUTO = 1;
    public static final int READ_AS_BYTES = 0;
    public static final int READ_AS_HISTOGRAM = 2;
    public static final int READ_AS_WINDOW = 3;

    public static final int VOXELSIZE_FROM_PIXELSIZE = 0;
    public static final int VOXELSIZE_FROM_SLICESDISTANCE = 1;
    public static final int VOXELSIZE_FROM_MANUALVALUE = 2;

    public Params() 
    {
        super(eggs);
        setValue("fileList", new ArrayList<String>());
        setValue("downsize", new int[]{1, 1, 1});
    }
    
    private static ParameterEgg[] eggs = new ParameterEgg[] {
                        new ParameterEgg<ArrayList>("fileList", ParameterType.filename,null),
                        new ParameterEgg<String>("dirPath", ParameterType.dependent, ""),
                        new ParameterEgg<String>("patientName", ParameterType.dependent, ""),
                        new ParameterEgg<Integer>("readAs", ParameterType.independent, READ_AS_AUTO),
                        new ParameterEgg<Integer>("low", ParameterType.independent, -1024),
                        new ParameterEgg<Integer>("high", ParameterType.independent, 1024),
                        new ParameterEgg<int[]>("downsize", ParameterType.independent, null),
                        new ParameterEgg<Boolean>("inpaintMissingSlices", ParameterType.independent, false),
                        new ParameterEgg<Boolean>("readAsVolume", ParameterType.independent, true),
                        new ParameterEgg<String>("infoString", ParameterType.dependent, ""),
                        new ParameterEgg<Boolean>("interpolateData", ParameterType.independent, false),
                        new ParameterEgg<Integer>("interpolateDataVoxelSizeFrom", ParameterType.independent, VOXELSIZE_FROM_PIXELSIZE),
                        new ParameterEgg<Float>("interpolateDataVoxelSizeManualValue", ParameterType.independent, 1.0f),
                        new ParameterEgg<Boolean>("scaleToMM", ParameterType.independent, false),
                        new ParameterEgg<Boolean>("framesAsTime", ParameterType.independent, false),
                        new ParameterEgg<Boolean>("framesRange", ParameterType.independent, null),
                        new ParameterEgg<Boolean>("ignoreOrientation", ParameterType.independent, false),
                        new ParameterEgg<Integer>("sliceDenoisingLevel", ParameterType.independent, 0),
            };

    
    public ArrayList<String> getFileList() {
        return (ArrayList<String>) this.getValue("fileList");
    }

    @Deprecated
    public void setFileList(Vector<String> fileList) {
        ArrayList<String> tmp = new ArrayList<String>();
        tmp.addAll(fileList);
        setFileList(tmp);
    }

    public void setFileList(ArrayList<String> fileList) {
        this.setValue("fileList", fileList);
        fireStateChanged();
    }

    public String getDirPath() {
        return (String) this.getValue("dirPath");
    }

    public void setDirPath(String dirPath) {
        this.setValue("dirPath", dirPath);
    }

    public String getPatientName() {
        return (String) this.getValue("patientName");
    }

    public void setPatientName(String patientName) {
        this.setValue("patientName",patientName);
    }

    public String getInfoString() {
        return (String) this.getValue("infoString");
    }

    public void setInfoString(String infoString) {
        this.setValue("infoString",infoString);
    }

    public int getReadAs() {
        return (Integer) this.getValue("readAs");
    }

    public void setReadAs(int readAs) {
        this.setValue("readAs",readAs);
    }

    public int getLow() {
        return (Integer) this.getValue("low");
    }

    public void setLow(int low) {
        this.setValue("low",low);
    }

    public int getHigh() {
        return (Integer) this.getValue("high");
    }

    public void setHigh(int high) {
        this.setValue("high",high);
    }

    public int[] getDownsize() {
        return (int[]) this.getValue("downsize");
    }

    public void setDownsize(int[] downsize) {
        this.setValue("downsize",downsize);
    }

    public boolean isInpaintMissingSlices() {
        return (Boolean)this.getValue("inpaintMissingSlices");
    }

    public void setInpaintMissingSlices(boolean inpaintMissingSlices) {
        this.setValue("inpaintMissingSlices",inpaintMissingSlices);
    }

    public boolean isReadAsVolume() {
        return (Boolean) this.getValue("readAsVolume");
    }

    public void setReadAsVolume(boolean readAsVolume) {
        this.setValue("readAsVolume",readAsVolume);
    }

    public boolean isInterpolateData() {
        return (Boolean)this.getValue("interpolateData");
    }

    public void setInterpolateData(boolean interpolate) {
        this.setValue("interpolateData",interpolate);
    }

    public int getInterpolateDataVoxelSizeFrom() {
        return (Integer) this.getValue("interpolateDataVoxelSizeFrom");
    }
    
    public int getSliceDenoisingLevel() {
        return (Integer) this.getValue("sliceDenoisingLevel");
    }

    public void setSliceDenoisingLevel(int sliceDenoisingLevel) {
        this.setValue("sliceDenoisingLevel",sliceDenoisingLevel);
    }

    public void setInterpolateDataVoxelSizeFrom(int type) {
        if(type != VOXELSIZE_FROM_MANUALVALUE && type != VOXELSIZE_FROM_PIXELSIZE && type != VOXELSIZE_FROM_SLICESDISTANCE)
            return;

        this.setValue("interpolateDataVoxelSizeFrom", type);
    }

    public float getInterpolateDataVoxelSizeManualValue() {
        return (Float)this.getValue("interpolateDataVoxelSizeManualValue");
    }

    public void setInterpolateDataVoxelSizeManualValue(float value) {
        this.setValue("interpolateDataVoxelSizeManualValue",value);
    }

    public boolean isScaleToMM() {
        return (Boolean) this.getValue("scaleToMM");
    }

    public void setScaleToMM(boolean mm) {
        this.setValue("scaleToMM",mm);
    }

    public boolean isFramesAsTime() {
        return (Boolean) this.getValue("framesAsTime");
    }

    public void setFramesAsTime(boolean value) {
        this.setValue("framesAsTime",value);
    }
    
    public boolean isIgnoreOrientation() {
        return (Boolean) this.getValue("ignoreOrientation");
    }

    public void setIgnoreOrientation(boolean value) {
        this.setValue("ignoreOrientation",value);
    }
    
    public int[] getFramesRange() {
        return (int[]) this.getValue("framesRange");
    }

    public void setFramesRange(int[] range) {
        this.setValue("framesRange", range);
    }
    
}
