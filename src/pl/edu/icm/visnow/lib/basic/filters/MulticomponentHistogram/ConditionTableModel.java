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
import java.util.Arrays;
import javax.swing.table.DefaultTableModel;
import pl.edu.icm.visnow.datasets.Field;

/**
 * @author  Bartosz Borucki (babor@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class ConditionTableModel extends DefaultTableModel {
    
    private ArrayList<Condition> conditions = new ArrayList<Condition>();
    private ArrayList<Condition.Logic> conditionsLogic = new ArrayList<Condition.Logic>();
    private Field field = null;
    
    public ConditionTableModel(Field field, Condition[] conditions, Condition.Logic[] conditionsLogic) {
        super();
        this.field = field;
        if(conditions != null)
            this.conditions.addAll(Arrays.asList(conditions));
        if(conditionsLogic != null)
            this.conditionsLogic.addAll(Arrays.asList(conditionsLogic));        
    }
    

    @Override
    public int getRowCount() {
        if(field == null) 
            return 0;
        
        return conditions.size()+1;
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch(columnIndex) {
            case 0:
                return "Logic";
            case 1:
                return "Component/Space";
            case 2:
                return "Operator";
            case 3:
                return "Value/Component";
        }
        return "";
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch(columnIndex) {
            case 0:
                return Condition.Logic.class;
            case 1:
                return Integer.class;
            case 2:
                return Condition.Operator.class;
            case 3:
                return Object.class;
        }
        return Object.class;        
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if(columnIndex == 0) {
            if(rowIndex == 0 || rowIndex >= conditions.size())
                return false;
        }
        
        return true;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if(field == null)
            return null;
        
        if(rowIndex < 0 || rowIndex >= conditions.size())
            return null;
        
        if(rowIndex == 0 && columnIndex == 0)
            return null;
        
        
        Condition cond = conditions.get(rowIndex);
        
        switch(columnIndex) {
            case 0:
                if((rowIndex - 1) >= conditionsLogic.size())
                    return null;
                else
                    return conditionsLogic.get(rowIndex-1);
            case 1:
                if(cond instanceof FieldDataCondition) {
                    FieldDataCondition fcond = (FieldDataCondition)cond;
                    return field.getData(fcond.getComponent1()).getName();
                } else if( cond instanceof FieldSpaceCondition) {
                    FieldSpaceCondition fcond = (FieldSpaceCondition)cond;
                    return fcond.getSpComponent();
                }
                break;                
            case 2:
                return cond.getOperator();
            case 3:
                if(cond instanceof FieldDataCondition) {
                    FieldDataCondition fcond = (FieldDataCondition)cond;
                    if(fcond.getComponent2() == null)
                        return fcond.getComponentValue();
                    else
                        return field.getData(fcond.getComponent2()).getName();                    
                } else if(cond instanceof FieldSpaceCondition) {
                    FieldSpaceCondition fcond = (FieldSpaceCondition)cond;
                    return fcond.getSpValue();                                        
                }
                break;
                
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if(field == null)
            return;
        
        if(rowIndex < 0)
            return;
        
        if(rowIndex == 0 && columnIndex == 0)
            return;
        
        if(rowIndex < conditions.size() && columnIndex == 1 && aValue == null) {
            //drop condition
            conditions.remove(rowIndex);
            if(rowIndex == 0 && conditionsLogic.size() > 0) {
                conditionsLogic.remove(0);
            } else if(rowIndex > 0 && conditionsLogic.size() > rowIndex-1) {
                conditionsLogic.remove(rowIndex-1);
            }
            fireTableCellUpdated(rowIndex, columnIndex);            
            fireTableDataChanged();
            return;
        }
        
        if(rowIndex >= conditions.size()) {
            if(columnIndex == 1) {
                //add condition
                if(aValue instanceof Integer) {
                    conditions.add(new FieldDataCondition(field, (Integer)aValue, Condition.Operator.GREATER, 0.0f));
                } else if(aValue instanceof FieldSpaceCondition.SpatialComponent) {
                    conditions.add(new FieldSpaceCondition(field, (FieldSpaceCondition.SpatialComponent)aValue, Condition.Operator.GREATER, 0.0f));
                }
                if(rowIndex > 0) {
                    conditionsLogic.add(Condition.Logic.AND);
                }                
                fireTableCellUpdated(rowIndex, columnIndex);
                fireTableDataChanged();
            }
            return;            
        }
        
        
        Condition cond;
        switch(columnIndex) {
            case 0:
                if((aValue instanceof Condition.Logic) && rowIndex > 0) {
                    conditionsLogic.set(rowIndex-1, (Condition.Logic)aValue);
                }
                break;                
            case 1:
                //zalezne od typu Data/Space i od zmiany typu
                cond = conditions.get(rowIndex);
                if(cond instanceof FieldDataCondition && aValue instanceof Integer) {
                    //zmienic wartosc
                    ((FieldDataCondition)cond).setComponent1((Integer)aValue);                    
                } else if(cond instanceof FieldDataCondition && aValue instanceof FieldSpaceCondition.SpatialComponent) {
                    //nowy condition spatial
                    FieldSpaceCondition nc = new FieldSpaceCondition(field, (FieldSpaceCondition.SpatialComponent)aValue, cond.getOperator(), 0.0f);
                    conditions.set(rowIndex, nc);                    
                } else if(cond instanceof FieldSpaceCondition && aValue instanceof FieldSpaceCondition.SpatialComponent) {
                    //zmienic wartosc
                    ((FieldSpaceCondition)cond).setSpComponent((FieldSpaceCondition.SpatialComponent)aValue);                    
                } else if(cond instanceof FieldSpaceCondition && aValue instanceof Integer) {
                    //nowy condition data
                    FieldDataCondition nc = new FieldDataCondition(field, (Integer)aValue, cond.getOperator(), 0.0f);
                    conditions.set(rowIndex, nc);                    
                }
                break;                
            case 2:
                if(aValue instanceof Condition.Operator) {
                    conditions.get(rowIndex).setOperator((Condition.Operator)aValue);
                }
                break;                
            case 3:
                //zalezne od typu Data/Space i od zmiany value/component
                cond = conditions.get(rowIndex);
                
                float v = 0;
                boolean isValue = false;
                if(aValue instanceof Float) {
                    v = (Float)aValue;
                    isValue = true;
                } else if(aValue instanceof String) {
                    try {
                        v = Float.parseFloat((String)aValue);
                        isValue = true;                        
                    } catch(NumberFormatException ex) {
                        isValue = false;
                    }                    
                }
                
                
                if(cond instanceof FieldDataCondition && aValue instanceof Integer) {
                    ((FieldDataCondition)cond).setComponent2((Integer)aValue);                    
                    ((FieldDataCondition)cond).setComponentValue(null);                    
                } else if(cond instanceof FieldDataCondition && isValue) {
                    ((FieldDataCondition)cond).setComponent2(null);                    
                    ((FieldDataCondition)cond).setComponentValue(v);                    
                } else if(cond instanceof FieldSpaceCondition && isValue) {
                    ((FieldSpaceCondition)cond).setSpValue(v);                    
                }
                break;
        }
        fireTableCellUpdated(rowIndex, columnIndex);      
        fireTableDataChanged();
    }

    public Condition[] getConditions() {
        if(conditions.isEmpty())
            return null;
        
        Condition[] out = new Condition[conditions.size()];
        for (int i = 0; i < out.length; i++) {
            out[i] = conditions.get(i);
        }
        return out;        
    }

    public Condition.Logic[] getConditionsLogic() {
        if(conditionsLogic.isEmpty())
            return null;
        
        Condition.Logic[] out = new Condition.Logic[conditionsLogic.size()];
        for (int i = 0; i < out.length; i++) {
            out[i] = conditionsLogic.get(i);
        }
        return out;        
    }

}
