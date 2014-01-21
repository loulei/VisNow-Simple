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

/**
 *
 * @author babor
 */
public abstract class Condition {

    public static enum Operator {
        
        EQUAL {
            @Override
            public String toString() {
                return "=";
            }
        },
        
        NOT_EQUAL {
            @Override
            public String toString() {
                return "!=";
            }
        },
        
        GREATER {
            @Override
            public String toString() {
                return ">";
            }
        },
        
        GREATER_OR_EQUAL {
            @Override
            public String toString() {
                return ">=";
            }
        },

        LESS {
            @Override
            public String toString() {
                return "<";
            }
        },
        
        LESS_OR_EQUAL {
            @Override
            public String toString() {
                return "<=";
            }
        }
        
    };

    
    public static enum Logic {
        
        AND {
            @Override
            public String toString() {
                return "AND";
            }
        },
        
        OR {
            @Override
            public String toString() {
                return "OR";
            }
        }
    };
    
    protected Operator operator = null;
    
    public abstract boolean check(int n);
    
    protected boolean decide(int value1, int value2) {
        switch(operator) {
            case EQUAL:
                return (value1 == value2);
            case NOT_EQUAL:
                return (value1 != value2);
            case GREATER:
                return (value1 > value2);
            case GREATER_OR_EQUAL:
                return (value1 >= value2);
            case LESS:
                return (value1 < value2);
            case LESS_OR_EQUAL:
                return (value1 <= value2);
        }
        return false;
    }

    protected boolean decide(float value1, float value2) {
        switch(operator) {
            case EQUAL:
                return (value1 == value2);
            case NOT_EQUAL:
                return (value1 != value2);
            case GREATER:
                return (value1 > value2);
            case GREATER_OR_EQUAL:
                return (value1 >= value2);
            case LESS:
                return (value1 < value2);
            case LESS_OR_EQUAL:
                return (value1 <= value2);
        }
        return false;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator op) {
        this.operator = op;
    }
    
}
