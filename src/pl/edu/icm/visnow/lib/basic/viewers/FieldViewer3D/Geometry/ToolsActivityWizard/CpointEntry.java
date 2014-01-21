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

package pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.ToolsActivityWizard;

import java.util.ArrayList;
import javax.swing.event.ChangeEvent;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.CalculablePoint;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.CalculablePointsPool;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.PointDescriptor;

/**
 * @author  Bartosz Borucki (babor@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */

public class CpointEntry extends Entry {
    private ArrayList<ToolEntry> depends = new ArrayList<ToolEntry>();
    private CalculablePoint calculablePoint = null;

    public CpointEntry(String id, String name, String description) {
        super(Entry.ENTRY_TYPE_CPOINT, id, name, description);
    }


    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean isReady() {
        for (int i = 0; i < depends.size(); i++) {
            if(!depends.get(i).isReady())
                return false;
        }
        return true;
    }

    public void addDependancy(ToolEntry te) {
        te.addChangeListener(this);
        depends.add(te);
        updateCP();
        fireStateChanged();
    }

    public ToolEntry getDependancy(int n) {
        return depends.get(n);
    }

    public int getDependanciesSize() {
        return depends.size();
    }

    public ArrayList<ToolEntry> getDependancies() {
        return depends;
    }

    public int getDependancyIndex(ToolEntry dep) {
        if(dep == null)
            return -1;

        for (int i = 0; i < depends.size(); i++) {
            if(dep == depends.get(i))
                return i;
        }

        return -1;
    }

    public boolean dependsDirectlyOn(ToolEntry dep) {
        int n = getDependancyIndex(dep);
        return (n != -1);
    }

    public boolean dependsOn(ToolEntry e) {
        ToolEntry dep;
        boolean tmp;
        for (int i = 0; i < depends.size(); i++) {
            dep = depends.get(i);
            if((ToolEntry)dep == (ToolEntry)e)
                return true;
        }
        return false;
    }

    public ArrayList<Entry> getPathToTool(ToolEntry t) {
        ArrayList<Entry> out = new ArrayList<Entry>();
        ToolEntry dep;
        out.add(this);
        for (int i = 0; i < depends.size(); i++) {
            dep = depends.get(i);
            if(dep == t) {
                out.add(dep);
                return out;
            }
        }
        return out;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        fireStateChanged();
    }

    public ToolEntry getToolByPointDescriptor(PointDescriptor pd) {
        if(pd == null)
            return null;

        ToolEntry t;
        for (int i = 0; i < depends.size(); i++) {
            t = depends.get(i);
            if(t.getPointDescriptors().contains(pd))
                 return t;
        }
        return null;
    }

    public ArrayList<ToolEntry> getAllToolEntries() {
        ArrayList<ToolEntry> tools = new ArrayList<ToolEntry>();
        ToolEntry dep;
        for (int i = 0; i < depends.size(); i++) {
            dep = depends.get(i);
            tools.add(dep);
        }
        return tools;
    }

    public CalculablePoint getCalculablePoint() {
        return calculablePoint;
    }

    public void addCalculablePoint(CalculablePointsPool.CalculablePointType type) {
        this.calculablePoint = CalculablePointsPool.getCalculablePoint(type, this.id.replaceAll(" ", "_"));
    }

    private void updateCP() {
        if(calculablePoint != null) {
            ArrayList<PointDescriptor> pds = new ArrayList<PointDescriptor>();
            for (int i = 0; i < depends.size(); i++) {
                pds.addAll(depends.get(i).getPointDescriptors());
            }
            calculablePoint.setPointDescriptors(pds);
        }

    }

}
