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

/**
 * @author  Bartosz Borucki (babor@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class FieldDataCondition extends Condition {

    private Field field;
    
    protected Integer component1 = null;
    protected Integer component2 = null;
    protected Float componentValue = 0.0f;
    
    
    public FieldDataCondition(Field field, Integer component1, Operator componentOperator, Integer component2) {
        this.field = field;
        this.component1 = component1;
        this.operator = componentOperator;
        this.component2 = component2;
        this.componentValue = null;
    }

    public FieldDataCondition(Field field, Integer component1, Operator componentOperator, Float componentValue) {
        this.field = field;
        this.component1 = component1;
        this.operator = componentOperator;
        this.component2 = null;
        this.componentValue = componentValue;
    }
    
    public boolean check(int n) {
        if(component1 == null || operator == null || (component2 == null && componentValue == null))
            return true;

        if(component2 != null)
            return chackDataByComponent(n);
        
        if(componentValue != null)
            return checkDataByValue(n);
        
        return false;
    }
    
    private boolean chackDataByComponent(int n) {
        return decide(field.getData(component1).getData(n), field.getData(component2).getData(n));
    }

    private boolean checkDataByValue(int n) {
        return decide(field.getData(component1).getData(n), componentValue);
    }

    public Integer getComponent1() {
        return component1;
    }

    public void setComponent1(Integer component) {
        this.component1 = component;
    }
    
    public Integer getComponent2() {
        return component2;
    }

    public void setComponent2(Integer component) {
        this.component2 = component;
    }
    
    public Float getComponentValue() {
        return componentValue;
    }

    public void setComponentValue(Float value) {
        this.componentValue = value;
    }

    public Field getField() {
        return field;
    }

    
}
