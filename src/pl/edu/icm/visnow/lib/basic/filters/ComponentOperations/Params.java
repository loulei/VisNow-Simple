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

package pl.edu.icm.visnow.lib.basic.filters.ComponentOperations;

import java.util.Vector;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.engine.core.ParameterEgg;
import pl.edu.icm.visnow.engine.core.ParameterType;
import pl.edu.icm.visnow.engine.core.Parameters;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class Params extends Parameters {
    public static final int NOOP = -1;
    public static final int BYTE = DataArray.FIELD_DATA_BYTE;
    public static final int BYTE_NORMALIZED = BYTE + DataArray.MAX_TYPE_VALUE;
    public static final int SHORT = DataArray.FIELD_DATA_SHORT;
    public static final int SHORT_NORMALIZED = SHORT + DataArray.MAX_TYPE_VALUE;
    public static final int INT = DataArray.FIELD_DATA_INT;
    public static final int FLOAT = DataArray.FIELD_DATA_FLOAT;
    public static final int DOUBLE = DataArray.FIELD_DATA_DOUBLE;
    public static final int LOG = DataArray.MAX_TYPE_VALUE + 4;
    public static final int ATAN = DataArray.MAX_TYPE_VALUE + 5;
    public static int[] actionCodes = new int[]{NOOP, BYTE, BYTE_NORMALIZED, SHORT, SHORT_NORMALIZED,
        INT, FLOAT, DOUBLE, LOG, ATAN};
    public static final String[] actionNames = new String[]{"noop",
        "to byte", "byte normalize", "to short", "short normalize",
        "to int", "to float", "to double", "log", "atan"
    };
    private static final int[] initActions = new int[]{0};
    private static ParameterEgg[] eggs = new ParameterEgg[]{
        new ParameterEgg<int[]>("actions", ParameterType.dependent, initActions),
        new ParameterEgg<boolean[]>("retain", ParameterType.dependent, new boolean[]{false}),
        
        new ParameterEgg<Boolean>("useCoords", ParameterType.independent, false),
        new ParameterEgg<Integer>("nDims", ParameterType.dependent, 3),
        
        new ParameterEgg<Integer>("xCoordComponent", ParameterType.dependent, 0),
        new ParameterEgg<Integer>("yCoordComponent", ParameterType.dependent, 0),
        new ParameterEgg<Integer>("zCoordComponent", ParameterType.dependent, 0),
        new ParameterEgg<Boolean>("addIndexComponent", ParameterType.independent, false),
        new ParameterEgg<Float>("xCoordScaleVal", ParameterType.independent, 1.0f),
        new ParameterEgg<Float>("xCoordScaleMin", ParameterType.independent, 0.0f),
        new ParameterEgg<Float>("xCoordScaleMax", ParameterType.independent, 5.0f),
        new ParameterEgg<Float>("yCoordScaleVal", ParameterType.independent, 1.0f),
        new ParameterEgg<Float>("yCoordScaleMin", ParameterType.independent, 0.0f),
        new ParameterEgg<Float>("yCoordScaleMax", ParameterType.independent, 5.0f),
        new ParameterEgg<Float>("zCoordScaleVal", ParameterType.independent, 1.0f),
        new ParameterEgg<Float>("zCoordScaleMin", ParameterType.independent, 0.0f),
        new ParameterEgg<Float>("zCoordScaleMax", ParameterType.independent, 5.0f),
        new ParameterEgg<float[]>("min", ParameterType.dependent, new float[]{0.f}),
        new ParameterEgg<float[]>("max", ParameterType.dependent, new float[]{1.f}),
        
        new ParameterEgg<Vector<VectorComponent>>("vectorComponents", ParameterType.dependent, null),
        new ParameterEgg<boolean[]>("vCNorms", ParameterType.dependent, null),
        new ParameterEgg<boolean[]>("vCNormalize", ParameterType.dependent, null),
        new ParameterEgg<boolean[]>("vCSplit", ParameterType.dependent, null),
        
        new ParameterEgg<Boolean>("fix3D", ParameterType.dependent, false),
        
        new ParameterEgg<Integer>("maskComponent", ParameterType.dependent, 0),
        new ParameterEgg<Boolean>("recomputeMinMax", ParameterType.independent, false),
        new ParameterEgg<Float>("maskMin", ParameterType.dependent, 0.f),
        new ParameterEgg<Float>("maskMax", ParameterType.dependent, 1.f),
        
        new ParameterEgg<Vector<ComplexComponent>>("complexCombineComponents", ParameterType.dependent, null),
        new ParameterEgg<boolean[]>("complexSplitRe", ParameterType.dependent, new boolean[]{}),
        new ParameterEgg<boolean[]>("complexSplitIm", ParameterType.dependent, new boolean[]{}),
        new ParameterEgg<boolean[]>("complexSplitAbs", ParameterType.dependent, new boolean[]{}),
        new ParameterEgg<boolean[]>("complexSplitArg", ParameterType.dependent, new boolean[]{}),
        
        new ParameterEgg<Boolean>("auto", ParameterType.independent, false),
    };

    public Params() {
        super(eggs);
        setValue("complexCombineComponents", new Vector<ComplexComponent>());
        setValue("vectorComponents", new Vector<VectorComponent>());
        setValue("actions",  initActions);
        setValue("retain",  new boolean[]{false});
        setValue("useCoords",  false);
        setValue("nDims",  3);
        setValue("xCoordComponent",  0);
        setValue("yCoordComponent",  0);
        setValue("zCoordComponent",  0);
        setValue("addIndexComponent",  false);
        setValue("xCoordScaleVal",  1.0f);
        setValue("xCoordScaleMin",  0.0f);
        setValue("xCoordScaleMax",  5.0f);
        setValue("yCoordScaleVal",  1.0f);
        setValue("yCoordScaleMin",  0.0f);
        setValue("yCoordScaleMax",  5.0f);
        setValue("zCoordScaleVal",  1.0f);
        setValue("zCoordScaleMin",  0.0f);
        setValue("zCoordScaleMax",  5.0f);
        setValue("min",  new float[]{0.f});
        setValue("max",  new float[]{1.f});
        setValue("vCNorms",  null);
        setValue("vCNormalize",  null);
        setValue("vCSplit",  null);
        setValue("fix3D",  false);
        setValue("maskComponent",  0);
        setValue("recomputeMinMax",  false);
        setValue("maskMin",  0.f);
        setValue("maskMax",  1.f);
        setValue("complexSplitRe",  new boolean[]{});
        setValue("complexSplitIm",  new boolean[]{});
        setValue("complexSplitAbs",  new boolean[]{});
        setValue("complexSplitArg",  new boolean[]{});
        setValue("auto",  false);
    }

    public int[] getActions() {
        return (int[]) getValue("actions");
    }

    public void setActions(int[] actions) {
        setValue("actions", actions);
    }
    
    public boolean[] getRetain() {
        return (boolean[]) getValue("retain");
    }

    public void setRetain(boolean[] retain) {
        setValue("retain", retain);
    }

    public int getNDims() {
        return (Integer) getValue("nDims");
    }

    public void setNDims(int nDims) {
        setValue("nDims", nDims);
    }

    public int getXCoordComponent() {
        return (Integer) getValue("xCoordComponent");
    }

    public void setXCoordComponent(int xCoord) {
        setValue("xCoordComponent", xCoord);
    }

   public int getYCoordComponent() {
        return (Integer) getValue("yCoordComponent");
    }

    public void setYCoordComponent(int yCoord) {
        setValue("yCoordComponent", yCoord);
    }

    public int getZCoordComponent() {
        return (Integer) getValue("zCoordComponent");
    }

    public void setZCoordComponent(int zCoord) {
        setValue("zCoordComponent", zCoord);
    }

    public boolean isUseCoords() {
        return (Boolean) getValue("useCoords");
    }

    public void setUseCoords(boolean use) {
        setValue("useCoords", use);
    }

    public boolean isAddIndexComponent() {
        return (Boolean) getValue("addIndexComponent");
    }

    public void setAddIndexComponent(boolean add) {
        setValue("addIndexComponent", add);
    }

    public float getXCoordScaleVal() {
        return (Float) getValue("xCoordScaleVal");
    }

    public void setXCoordScaleVal(float xCoordScaleVal) {
        setValue("xCoordScaleVal", xCoordScaleVal);
    }

    public float getYCoordScaleVal() {
        return (Float) getValue("yCoordScaleVal");
    }

    public void setYCoordScaleVal(float yCoordScaleVal) {
        setValue("yCoordScaleVal", yCoordScaleVal);
    }

    public float getZCoordScaleVal() {
        return (Float) getValue("zCoordScaleVal");
    }

    public void setZCoordScaleVal(float zCoordScaleVal) {
        setValue("zCoordScaleVal", zCoordScaleVal);
    }

    public float getXCoordScaleMin() {
        return (Float) getValue("xCoordScaleMin");
    }

    public void setXCoordScaleMin(float xCoordScaleMin) {
        setValue("xCoordScaleMin", xCoordScaleMin);
    }

    public float getYCoordScaleMin() {
        return (Float) getValue("yCoordScaleMin");
    }

    public void setYCoordScaleMin(float yCoordScaleMin) {
        setValue("yCoordScaleMin", yCoordScaleMin);
    }

    public float getZCoordScaleMin() {
        return (Float) getValue("zCoordScaleMin");
    }

    public void setZCoordScaleMin(float zCoordScaleMin) {
        setValue("zCoordScaleMin", zCoordScaleMin);
    }

    public float getXCoordScaleMax() {
        return (Float) getValue("xCoordScaleMax");
    }

    public void setXCoordScaleMax(float xCoordScaleMax) {
        setValue("xCoordScaleMax", xCoordScaleMax);
    }

    public float getYCoordScaleMax() {
        return (Float) getValue("yCoordScaleMax");
    }

    public void setYCoordScaleMax(float yCoordScaleMax) {
        setValue("yCoordScaleMax", yCoordScaleMax);
    }

    public float getZCoordScaleMax() {
        return (Float) getValue("zCoordScaleMax");
    }

    public void setZCoordScaleMax(float zCoordScaleMax) {
        setValue("zCoordScaleMax", zCoordScaleMax);
    }

    /**
     * Get the value of max
     *
     * @return the value of max
     */
    public float[] getMax() {
        return (float[]) getValue("max");
    }

    /**
     * Set the value of max
     *
     * @param max new value of max
     */
    public void setMax(float[] max) {
        setValue("max", max);
    }

    /**
     * Get the value of max at specified index
     *
     * @param index
     * @return the value of max at specified index
     */
    public float getMax(int index) {
        return ((float[]) getValue("max"))[index];
    }

    /**
     * Set the value of max at specified index.
     *
     * @param index
     * @param newMax new value of max at specified index
     */
    public void setMax(int index, float newMax) {
        ((float[]) getValue("max"))[index] = newMax;
    }

    /**
     * Get the value of min
     *
     * @return the value of min
     */
    public float[] getMin() {
        return (float[]) getValue("min");
    }

    /**
     * Set the value of min
     *
     * @param min new value of min
     */
    public void setMin(float[] min) {
        setValue("min", min);
    }

    /**
     * Get the value of min at specified index
     *
     * @param index
     * @return the value of min at specified index
     */
    public float getMin(int index) {
        return ((float[]) getValue("min"))[index];
    }

    /**
     * Set the value of min at specified index.
     *
     * @param index
     * @param newMin new value of min at specified index
     */
    public void setMin(int index, float newMin) {
        ((float[]) getValue("min"))[index] = newMin;
    }

    /**
     * Get the value of fix3D
     *
     * @return the value of fix3D
     */
    public boolean isFix3D() {
        return (Boolean) getValue("fix3D");
    }

    /**
     * Set the value of fix3D
     *
     * @param fix3D new value of fix3D
     */
    public void setFix3D(boolean fix3D) {
        setValue("fix3D", fix3D);
    }

    public boolean[] getVCNorms() {
        return (boolean[]) getValue("vCNorms");
    }
    
    public void setVCNorms(boolean[] vCNorms) {
        setValue("vCNorms", vCNorms);
    }
    
    public boolean[] getVCNormalize() {
        return (boolean[]) getValue("vCNormalize");
    }
    
    public void setVCNormalize(boolean[] vCNormalize) {
        setValue("vCNormalize", vCNormalize);
    }

    public boolean[] getCSplit() {
        return (boolean[]) getValue("vCSplit");
    }

    /**
     * Set the value of fix3D
     *
     * @param fix3D new value of fix3D
     */
    public void setVCSplit(boolean[] vCSplit) {
        setValue("vCSplit", vCSplit);
    }

    @SuppressWarnings({"unchecked"})
    public Vector<VectorComponent> getVectorComponents() {
        return (Vector<VectorComponent>) getValue("vectorComponents");
    }

    public void setVectorComponents(Vector<VectorComponent> vectorComponents) {
        setValue("vectorComponents", vectorComponents);
    }

    @SuppressWarnings({"unchecked"})
    public void clearVectorComponents() {
        ((Vector<VectorComponent>) getValue("vectorComponents")).clear();
    }

    @SuppressWarnings({"unchecked"})
    public void addVectorComponent(String name, int[] components, boolean computeNorm) {
        ((Vector<VectorComponent>) getValue("vectorComponents")).add(new VectorComponent(name, components, computeNorm));
    }

    public int getMaskComponent() {
        return (Integer) getValue("maskComponent");
    }

    public void setMaskComponent(int mask) {
        setValue("maskComponent", mask);
    }    

    public float getMaskMin() {
        return (Float) getValue("maskMin");
    }

    public void setMaskMin(float maskMin) {
        setValue("maskMin", maskMin);
    } 
    public float getMaskMax() {
        return (Float) getValue("maskMax");
    }

    public void setMaskMax(float maskMax) {
        setValue("maskMax", maskMax);
    }
    
    public boolean isRecomputeMinMax() {
        return (Boolean) getValue("recomputeMinMax");
    }

    public void setRecomputeMinMax(boolean recomputeMinMax) {
        setValue("recomputeMinMax", recomputeMinMax);
    }

    @SuppressWarnings({"unchecked"})
    public Vector<ComplexComponent> getComplexCombineComponents() {
        return (Vector<ComplexComponent>) getValue("complexCombineComponents");
    }

    public void setComplexCombineComponents(Vector<ComplexComponent> complexComponents) {
        setValue("complexCombineComponents", complexComponents);
    }

    @SuppressWarnings({"unchecked"})
    public void clearComplexCombineComponents() {
        ((Vector<ComplexComponent>) getValue("complexCombineComponents")).clear();
    }

    @SuppressWarnings({"unchecked"})
    public void addComplexCombineComponent(String name, int realComponent, int imagComponent) {
        ((Vector<ComplexComponent>) getValue("complexCombineComponents")).add(new ComplexComponent(name, realComponent, imagComponent));
    }
    
    public boolean[] getComplexSplitRe() {
        return (boolean[]) getValue("complexSplitRe");
    }

    public void setComplexSplitRe(boolean [] values) {
        setValue("complexSplitRe", values);
    }

    public boolean[] getComplexSplitIm() {
        return (boolean[]) getValue("complexSplitIm");
    }

    public void setComplexSplitIm(boolean [] values) {
        setValue("complexSplitIm", values);
    }
    
    public boolean[] getComplexSplitAbs() {
        return (boolean[]) getValue("complexSplitAbs");
    }

    public void setComplexSplitAbs(boolean [] values) {
        setValue("complexSplitAbs", values);
    }
    
    public boolean[] getComplexSplitArg() {
        return (boolean[]) getValue("complexSplitArg");
    }

    public void setComplexSplitArg(boolean [] values) {
        setValue("complexSplitArg", values);
    }
    
    public boolean isAuto() {
        return (Boolean) getValue("auto");
    }

    public void setAuto(boolean auto) {
        setValue("auto", auto);
    }

}
