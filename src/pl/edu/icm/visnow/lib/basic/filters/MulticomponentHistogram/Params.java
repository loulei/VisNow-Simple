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

package pl.edu.icm.visnow.lib.basic.filters.MulticomponentHistogram;

import java.util.ArrayList;
import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.engine.core.ParameterEgg;
import pl.edu.icm.visnow.engine.core.ParameterType;
import pl.edu.icm.visnow.engine.core.Parameters;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class Params extends Parameters {

    public static final int BINNING_BY_COMPONENTS = 0;
    public static final int BINNING_BY_COORDINATES = 1;

    private static ParameterEgg[] eggs = new ParameterEgg[]{
        new ParameterEgg<Integer>("nDims", ParameterType.independent, 3),
        new ParameterEgg<Integer>("binning", ParameterType.independent, BINNING_BY_COMPONENTS),
        new ParameterEgg<int[]>("dims", ParameterType.independent, null),
        new ParameterEgg<int[]>("selectedComponents", ParameterType.dependent, null),        
        new ParameterEgg<int[]>("selectedCoords", ParameterType.dependent, null),
        new ParameterEgg<Boolean>("countLogScale", ParameterType.independent, true),
        new ParameterEgg<Boolean>("countDropBackground", ParameterType.independent, false),
        new ParameterEgg<Condition[]>("filterConditions", ParameterType.dependent, null),
        new ParameterEgg<Condition.Logic[]>("filterConditionsLogic", ParameterType.dependent, null),
        new ParameterEgg<HistogramOperation[]>("histogramOperations", ParameterType.dependent, null),
        new ParameterEgg<Float>("logConstant", ParameterType.independent, 1.0f),
        new ParameterEgg<Boolean>("outGeometryToData", ParameterType.independent, false),
    };

    public Params() {
        super(eggs);

        int[] dims = {64,64,64};
        setValue("dims", dims);        
        
        int[] sc = {0,0,0};
        setValue("selectedComponents", sc);        

        int[] scoo = {0,1,2};
        setValue("selectedCoords", scoo);        
        
    }
    
    public void reset() {
        this.setActiveValue(false);
        
        this.setNDims(3);
        this.setBinning(BINNING_BY_COMPONENTS);
        int[] dims = {64,64,64};
        this.setDims(dims);
        int[] sc = {0,0,0};
        this.setSelectedComponents(sc);        
        int[] scoo = {0,1,2};
        this.setSelectedCoords(scoo);
        this.setCountLogScale(true);
        this.setCountDropBackground(false);
        this.setFilterConditions(null);
        this.setFilterConditionsLogic(null);
        this.setHistogramOperations(null);
        
        this.setActiveValue(true);
    }

    public void reset(Field field) {
        if(field == null) {
            reset();
            return;
        }
        
        this.setActiveValue(false);
        
        this.setNDims(3);
        this.setBinning(BINNING_BY_COMPONENTS);
        int[] dims = {64,64,64};
        this.setDims(dims);
        
        int firstScalarComponent = 0;
        for (int i = 0; i < field.getNData(); i++) {
            if(field.getData(i).getVeclen() == 1) {
                firstScalarComponent = i;
                break;
            }
        }        
        int[] sc = {firstScalarComponent,firstScalarComponent,firstScalarComponent};
        this.setSelectedComponents(sc);        
        
        int[] scoo = {0,0,0};
        if(field.getNSpace() > 1) {
            scoo[1] = 1;
            scoo[2] = 1;
        }
        if(field.getNSpace() > 2) {
            scoo[2] = 2;
        }
        this.setSelectedCoords(scoo);
        
        this.setCountLogScale(true);
        this.setCountDropBackground(false);
        this.setFilterConditions(null);
        this.setFilterConditionsLogic(null);
        this.setHistogramOperations(null);
        
        this.setActiveValue(true);
    }
    
    public void fireAction() {
        fireStateChanged();
    }

    public int getNDims() {
        return (Integer) getValue("nDims");
    }

    public void setNDims(int value) {
        setValue("nDims", value);
    }
    
    public int getBinning() {
        return (Integer) getValue("binning");
    }

    public void setBinning(int value) {
        setValue("binning", value);
    }

    public int[] getDims() {
        return (int[]) getValue("dims");
    }

    public void setDims(int[] value) {
        setValue("dims", value);
    }

    public int[] getSelectedComponents() {
        return (int[]) getValue("selectedComponents");
    }

    public void setSelectedComponents(int[] value) {
        setValue("selectedComponents", value);
    }

    public int[] getSelectedCoords() {
        return (int[]) getValue("selectedCoords");
    }

    public void setSelectedCoords(int[] value) {
        setValue("selectedCoords", value);
    }

    public boolean isCountLogScale() {
        return (Boolean) getValue("countLogScale");
    }

    public void setCountLogScale(boolean value) {
        setValue("countLogScale", value);
    }

    public boolean isCountDropBackground() {
        return (Boolean) getValue("countDropBackground");
    }

    public void setCountDropBackground(boolean value) {
        setValue("countDropBackground", value);
    }

    public Condition[] getFilterConditions() {
        return (Condition[]) getValue("filterConditions");
    }

    public void setFilterConditions(Condition[] value) {
        setValue("filterConditions", value);
    }

    public Condition.Logic[] getFilterConditionsLogic() {
        return (Condition.Logic[]) getValue("filterConditionsLogic");
    }

    public void setFilterConditionsLogic(Condition.Logic[] value) {
        setValue("filterConditionsLogic", value);
    }

    public HistogramOperation[] getHistogramOperations() {
        return (HistogramOperation[]) getValue("histogramOperations");
    }

    public void setHistogramOperations(HistogramOperation[] value) {
        setValue("histogramOperations", value);
    }

    public float getLogConstant() {
        return (Float) getValue("logConstant");
    }

    public void setLogConstant(float value) {
        setValue("logConstant", value);
        
        HistogramOperation[] ops = this.getHistogramOperations();
        if(ops != null) {
            for (int i = 0; i < ops.length; i++) {
                ops[i].setLogConst(value);                
            }
        }
        
    }

    public boolean isOutGeometryToData() {
        return (Boolean) getValue("outGeometryToData");
    }

    public void setOutGeometryToData(boolean value) {
        setValue("outGeometryToData", value);
    }
    
}
