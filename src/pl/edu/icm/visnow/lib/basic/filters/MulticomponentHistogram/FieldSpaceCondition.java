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

import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.datasets.IrregularField;
import pl.edu.icm.visnow.datasets.RegularField;

/**
 * @author  Bartosz Borucki (babor@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class FieldSpaceCondition extends Condition {

    public static enum SpatialComponent {
        NODE {
            @Override
            public String toString() {
                return "n";
            }
        },
        
        INDEX0 {
            @Override
            public String toString() {
                return "i";
            }
        }, 
        
        INDEX1 {
            @Override
            public String toString() {
                return "j";
            }
        }, 
        
        INDEX2 {
            @Override
            public String toString() {
                return "k";
            }
        }, 
        
        SPACEX {
            @Override
            public String toString() {
                return "x";
            }
        }, 
        
        SPACEY {
            @Override
            public String toString() {
                return "y";
            }
        }, 
        
        SPACEZ {
            @Override
            public String toString() {
                return "z";
            }
        }
    };

    protected Field field;
    
    protected SpatialComponent spComponent = null;
    protected Float spValue = 0.0f;
    
    
    public FieldSpaceCondition(Field field, SpatialComponent spComponent, Operator spOperator, Float spValue) {
        this.field = field;
        this.spComponent = spComponent;
        this.operator = spOperator;
        this.spValue = spValue;        
    }

    public SpatialComponent getSpComponent() {
        return spComponent;
    }

    public void setSpComponent(SpatialComponent comp) {
        this.spComponent = comp;
    }
    
    public Float getSpValue() {
        return spValue;
    }

    public void setSpValue(Float value) {
        this.spValue = value;
    }
    
    public boolean check(int n) {
        if(field instanceof RegularField) {
            RegularField rField = (RegularField)field;
            int[] dims = rField.getDims();
            int nDims = dims.length;
            int i,j,k;
            float[] p = null;
            i = j = k = 0; 
            switch(nDims) {
                case 1:
                    i = n;
                    if(i < 0) i = 0;
                    if(i >= dims[0]) i = dims[0] - 1;
                    break;
                case 2:
                    j = n/dims[0];
                    i = n - j*dims[0];
                    if(i < 0) i = 0;
                    if(i >= dims[0]) i = dims[0] - 1;
                    if(j < 0) j = 0;
                    if(j >= dims[1]) j = dims[1] - 1;
                    break;
                case 3:
                    k = n/(dims[0]*dims[1]);
                    j = (n - k*(dims[0]*dims[1]))/dims[0];
                    i = n - k*(dims[0]*dims[1]) - j*dims[0];
                    if(i < 0) i = 0;
                    if(i >= dims[0]) i = dims[0] - 1;
                    if(j < 0) j = 0;
                    if(j >= dims[1]) j = dims[1] - 1;
                    if(k < 0) k = 0;
                    if(k >= dims[2]) k = dims[2] - 1;
                    break;
            }

            switch(spComponent) {
                case NODE:
                    int n0 = (int)(float)spValue;
                    return decide(n, n0);
                case INDEX0:
                    int i0 = (int)(float)spValue;
                    return decide(i, i0);
                case INDEX1:
                    if(nDims < 2) return false;
                    int j0 = (int)(float)spValue;
                    return decide(j, j0);
                case INDEX2:
                    if(nDims < 3) return false;
                    int k0 = (int)(float)spValue;
                    return decide(k, k0);
                case SPACEX:
                    if(nDims == 1) {
                        p = rField.getGridCoords(i);                    
                    } else if(nDims == 2) {
                        p = rField.getGridCoords(i,j);
                    } else if(nDims == 3) {
                        p = rField.getGridCoords(i,j,k);
                    }
                    return decide(p[0], spValue);
                case SPACEY:
                    if(nDims == 1) {
                        p = rField.getGridCoords(i);                    
                    } else if(nDims == 2) {
                        p = rField.getGridCoords(i,j);
                    } else if(nDims == 3) {
                        p = rField.getGridCoords(i,j,k);
                    }
                    if(p.length < 2)
                        return false;
                    return decide(p[1], spValue);
                case SPACEZ:
                    if(nDims == 1) {
                        p = rField.getGridCoords(i);                    
                    } else if(nDims == 2) {
                        p = rField.getGridCoords(i,j);
                    } else if(nDims == 3) {
                        p = rField.getGridCoords(i,j,k);
                    }
                    if(p.length < 3)
                        return false;
                    return decide(p[2], spValue);
            }
        } else if(field instanceof IrregularField) {
            float[] coords = field.getCoords();
            float[] p = {0,0,0};
            int nSpace = field.getNSpace();

            switch(nSpace) {
                case 1:
                    p[0] = coords[n];
                    break;
                case 2:
                    p[0] = coords[2*n];
                    p[1] = coords[2*n+1];                
                    break;
                case 3:
                    p[0] = coords[3*n];
                    p[1] = coords[3*n+1];
                    p[2] = coords[3*n+2];
                    break;
            }       

            switch(spComponent) {
                case NODE:
                    int n0 = (int)(float)spValue;
                    return decide(n, n0);
                case SPACEX:
                    return decide(p[0], spValue);
                case SPACEY:
                    if(nSpace < 2)
                        return false;
                    return decide(p[1], spValue);
                case SPACEZ:
                    if(nSpace < 3)
                        return false;
                    return decide(p[2], spValue);
            }
        }        
        return false;
    }
    
    public Field getField() {
        return field;
    }
    
}
