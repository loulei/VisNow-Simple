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

package pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 * @author  Bartosz Borucki (babor@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */

public class ConnectionDescriptorTableModel extends AbstractTableModel {

    private ArrayList<ConnectionDescriptor> cons = new ArrayList<ConnectionDescriptor>();
    private GeometryParams params = null;

    public ConnectionDescriptorTableModel(GeometryParams params) {
        this.params = params;
        this.params.addGeometryParamsListener(new GeometryParamsListener() {
            @Override
            public void onGeometryParamsChanged(GeometryParamsEvent e) {
                if(e.getType() == GeometryParamsEvent.TYPE_CONNECTION || e.getType() == GeometryParamsEvent.TYPE_ALL) {
                    fireTableDataChanged();
                }
            }
        });

        this.cons = params.getConnectionDescriptors();
    }

    @Override
    public int getRowCount() {
        if(cons == null)
            return 0;

        return cons.size();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if(cons == null)
            return null;

        switch(columnIndex) {
            case 0:
                return cons.get(rowIndex).getName();
            case 1:
                return cons.get(rowIndex).getP1().getName();
            case 2:
                return cons.get(rowIndex).getP2().getName();
            case 3:
                return (Float)cons.get(rowIndex).getLength();
        }
        return null;
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch(columnIndex) {
            case 0:
                return "name";
            case 1:
                return "p1";
            case 2:
                return "p2";
            case 3:
                return "dist.";
            default:
                return "";
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if(columnIndex < 3)
            return String.class;
        else
            return Float.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return (columnIndex==0);
    }

    @Override
    public void setValueAt(Object aValue, int row, int column) {
        if(column == 0) {
            if(!(aValue instanceof String))
                return;
            String tmp = (String)aValue;
            cons.get(row).setName(tmp);
        }
        fireTableCellUpdated(row, column);
    }


}
