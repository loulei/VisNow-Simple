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

import pl.edu.icm.visnow.engine.core.ParameterEgg;
import pl.edu.icm.visnow.engine.core.ParameterType;
import pl.edu.icm.visnow.engine.core.Parameters;

/**
 * @author  Bartosz Borucki (babor@icm.edu.pl)
 * Warsaw University, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class Params extends Parameters {

    private static ParameterEgg[] eggs = new ParameterEgg[]{
        new ParameterEgg<String[]>("filePaths", ParameterType.independent, null),
        new ParameterEgg<Boolean>("readVelocity", ParameterType.independent, true),
        new ParameterEgg<Boolean>("readId", ParameterType.independent, true),
        new ParameterEgg<Boolean>("readType", ParameterType.independent, false),
        new ParameterEgg<Boolean>("readMass", ParameterType.independent, false),
        new ParameterEgg<Boolean>("readEnergy", ParameterType.independent, false),
        new ParameterEgg<Boolean>("readDensity", ParameterType.independent, false),
        new ParameterEgg<Boolean>("readTemperature", ParameterType.independent, false),
        new ParameterEgg<Integer>("downsize", ParameterType.independent, 1),
        new ParameterEgg<Boolean>("show", ParameterType.independent, false),
        new ParameterEgg<int[]>("densityFieldDims", ParameterType.independent, null),
        new ParameterEgg<Boolean>("densityFieldLog", ParameterType.independent, true),
    };

    public Params() {
        super(eggs);
        setValue("densityFieldDims", new int[] {64,64,64});
    }

    public String[] getFilePaths() {
        return (String[]) getValue("filePaths");
    }

    public void setFilePaths(String[] filePaths) {
        setValue("filePaths", filePaths);
        fireStateChanged();
    }
    
    public boolean isReadVelocity() {
        return (Boolean) getValue("readVelocity");
    }

    public void setReadVelocity(boolean value) {
        setValue("readVelocity", value);
    }

    public boolean isReadId() {
        return (Boolean) getValue("readId");
    }

    public void setReadId(boolean value) {
        setValue("readId", value);
    }

    public boolean isReadType() {
        return (Boolean) getValue("readType");
    }

    public void setReadType(boolean value) {
        setValue("readType", value);
    }

    public boolean isReadMass() {
        return (Boolean) getValue("readMass");
    }

    public void setReadMass(boolean value) {
        setValue("readMass", value);
    }

    public boolean isReadEnergy() {
        return (Boolean) getValue("readEnergy");
    }

    public void setReadEnergy(boolean value) {
        setValue("readEnergy", value);
        if(value = false)
            setReadTemperature(false);
    }

//    public boolean isReadDensity() {
//        return (Boolean) getValue("readDensity");
//    }
//
//    public void setReadDensity(boolean value) {
//        setValue("readDensity", value);
//    }

    public boolean isReadTemperature() {
        return (Boolean) getValue("readTemperature");
    }

    public void setReadTemperature(boolean value) {
        setValue("readTemperature", value);
        if(value == true)
            setReadEnergy(true);
    }

    public int getDownsize() {
        return (Integer) getValue("downsize");
    }

    public void setDownsize(int value) {
        setValue("downsize", value);
    }

    public boolean isShow() {
        return (Boolean) getValue("show");
    }

    public void setShow(boolean value) {
        setValue("show", value);
    }

    public int[] getDensityFieldDims() {
        return (int[]) getValue("densityFieldDims");
    }

    public void setDensityFieldDims(int[] value) {
        setValue("densityFieldDims", value);
    }
    
    public boolean[] getReadMask() {
        boolean[] out = new boolean[7];
        out[0] = isReadVelocity();
        out[1] = isReadId();
        out[2] = isReadType();
        out[3] = isReadMass();
        out[4] = isReadEnergy();
        //out[5] = isReadDensity();
        out[5] = false;
        out[6] = isReadTemperature();
        return out;
    }
    
    public boolean isDensityFieldLog() {
        return (Boolean) getValue("densityFieldLog");
    }

    public void setDensityFieldLog(boolean value) {
        setValue("densityFieldLog", value);
    }
    
}
