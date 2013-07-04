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

import java.util.Vector;
import javax.swing.table.DefaultTableModel;
import pl.edu.icm.visnow.datasets.dataarrays.DataArraySchema;
import pl.edu.icm.visnow.datasets.Field;

/**
 * @author  Bartosz Borucki (babor@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class OperationsTableModel extends DefaultTableModel {
    
    private Field field = null;
    private int[] components = null;
    private int[] componentsVeclen = null;
    private int[][] map = null;
    
    public OperationsTableModel(Field field) {
        super();
        this.field = field;
        
        Vector<DataArraySchema> tmp = field.getSchema().getComponentSchemas();
        components = new int[tmp.size()];
        componentsVeclen = new int[tmp.size()];
        for (int i = 0; i < tmp.size(); i++) {
            components[i] = i;
            componentsVeclen[i] = field.getData(i).getVeclen();            
        }
        
        map = new int[6][components.length];
        for (int i = 0; i < components.length; i++) {
            for (int j = 0; j < 6; j++) {
                map[j][i] = 0;                
            }
        }
    }

    public OperationsTableModel(Field field, int[][] map) {
        super();
        this.field = field;
        
        Vector<DataArraySchema> tmp = field.getSchema().getComponentSchemas();
        components = new int[tmp.size()];
        componentsVeclen = new int[tmp.size()];
        for (int i = 0; i < tmp.size(); i++) {
            components[i] = i;
            componentsVeclen[i] = field.getData(i).getVeclen();
        }
        
        this.map = map;
    }
    
    public int[][] getMap() {
        return map;
    }
    

    @Override
    public int getRowCount() {
        if(field == null) 
            return 0;
        
        return components.length;
    }

    @Override
    public int getColumnCount() {
        return 7;
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch(columnIndex) {
            case 0:
                return "Component";
            case 1:
                return "sum";
            case 2:
                return "min";
            case 3:
                return "max";
            case 4:
                return "avg";
            case 5:
                return "std";
            case 6:
                return "vstd";
        }
        return "";
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch(columnIndex) {
            case 0:
                return String.class;
            default:
                return Integer.class;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return !(columnIndex == 0) || (columnIndex == 6 && componentsVeclen[rowIndex] == 1);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if(field == null)
            return null;
        
        if(rowIndex < 0 || rowIndex >= components.length)
            return null;
        
        switch(columnIndex) {
            case 0:
                return field.getData(components[rowIndex]).getName();
            default:
                return map[columnIndex-1][rowIndex];
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if(field == null)
            return;
        
        switch(columnIndex) {
            case 0:
                return;
            default:
                map[columnIndex-1][rowIndex] = (Integer)aValue;                
                break;
        }
        fireTableCellUpdated(rowIndex, columnIndex);      
        fireTableDataChanged();
    }

    /**
     * @return the componentsVeclen
     */
    public int[] getComponentsVeclen() {
        return componentsVeclen;
    }

    
}
