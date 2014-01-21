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

import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.datasets.Field;

/**
 * @author  Bartosz Borucki (babor@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class HistogramOperation {

    public static enum Operation {
        COUNT {
            @Override
            public String toString() {
                return "count";
            }
        },
        
        SUM {
            @Override
            public String toString() {
                return "sum";
            }
        },
        
        MIN {
            @Override
            public String toString() {
                return "min";
            }
        },
        
        MAX {
            @Override
            public String toString() {
                return "max";
            }
        },
        
        AVG {
            @Override
            public String toString() {
                return "average";
            }
        },
        
        STD {
            @Override
            public String toString() {
                return "stddev";
            }
        },
        
        VSTD {
            @Override
            public String toString() {
                return "vstddev";
            }
        },
        
    };
    
    
    private Operation operation = Operation.COUNT;
    private int componentIndex = -1;
    private Field field = null;
    private boolean log = false;
    private float logConst = 1.0f;
    private boolean dropBg = false;
    
    public HistogramOperation(Operation operation, int componentIndex, Field field, boolean log, boolean dropBackground) {        
        this(operation, componentIndex, field, log, 1.0f, dropBackground);        
    }
    
    public HistogramOperation(Operation operation, int componentIndex, Field field, boolean log, float logConst, boolean dropBackground) {        
        this.operation = operation;
        this.componentIndex = componentIndex;
        this.field = field;
        this.log = log;
        this.logConst = logConst;
        this.dropBg = dropBackground;
    }
    
    public HistogramOperation() {        
        this(Operation.COUNT, -1, null, false, 1.0f, false);        
    }

    public HistogramOperation(boolean log, float logConst, boolean dropBackground) {        
        this(Operation.COUNT, -1, null, log, logConst, dropBackground);        
    }
    
    public HistogramOperation(boolean log, boolean dropBackground) {        
        this(Operation.COUNT, -1, null, log, 1.0f, dropBackground);        
    }
    

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public int getComponentIndex() {
        return componentIndex;
    }

    public DataArray getComponent() {
        if(field == null)
            return null;
        
        if(componentIndex < 0 || componentIndex >= field.getNData())
            return null;
        
        return field.getData(componentIndex);
    }
    
    public float getComponentValue(int node) {
        return this.getComponent().getData(node);
    }

    public float[] getVectorComponentValue(int node) {        
        return this.getComponent().getVData(node);
    }
    
    public void setComponentIndex(int componentIndex) {
        this.componentIndex = componentIndex;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }
    
    public boolean isLog() {
        return log;
    }

    public void setLog(boolean log) {
        this.log = log;
    }

    public boolean isDropBackgound() {
        return dropBg;
    }

    public void setDropBackgound(boolean db) {
        this.dropBg = db;
    }
    
    @Override
    public String toString() {
        DataArray da = this.getComponent();
        String out = "histogram";
        if(da != null) {
            out += " "+da.getName()+" "+operation.toString();
        }
        return out;
    }
    
    public float getLogConst() {
        return logConst;
    }

    public void setLogConst(float logConst) {
        this.logConst = logConst;
    }

    
}
