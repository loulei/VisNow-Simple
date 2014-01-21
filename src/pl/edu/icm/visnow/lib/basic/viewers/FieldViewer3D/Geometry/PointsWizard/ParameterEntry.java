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

package pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.PointsWizard;

import java.util.ArrayList;
import javax.swing.event.ChangeEvent;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.CalculableParameter;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.CalculableParamsPool;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.PointDescriptor;

/**
 * @author  Bartosz Borucki (babor@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */

public class ParameterEntry extends Entry {
    private ArrayList<Entry> depends = new ArrayList<Entry>();

    private CalculableParameter calculable = null;

    public ParameterEntry(String id, String name, String description) {
        super(Entry.ENTRY_TYPE_PARAMETER, id, name, description);
    }


    @Override
    public String toString() {
        return getName();
        //return "parameter "+id;
    }

    @Override
    public boolean isReady() {
        for (int i = 0; i < depends.size(); i++) {
            if(!depends.get(i).isReady())
                return false;
        }
        return true;
    }

    public void addDependancy(Entry entry) {
        entry.addChangeListener(this);
        depends.add(entry);
        fireStateChanged();
    }

    public Entry getDependancy(int n) {
        return depends.get(n);
    }

    public int getDependanciesSize() {
        return depends.size();
    }

    public ArrayList<Entry> getDependancies() {
        return depends;
    }

    public int getDependancyIndex(Entry dep) {
        if(dep == null)
            return -1;

        for (int i = 0; i < depends.size(); i++) {
            if(dep == depends.get(i))
                return i;
        }

        return -1;
    }

    public boolean dependsDirectlyOn(Entry dep) {
        int n = getDependancyIndex(dep);
        return (n != -1);
    }

    public boolean dependsOn(Entry e) {
        Entry dep;
        boolean tmp;
        for (int i = 0; i < depends.size(); i++) {
            dep = depends.get(i);
            if((dep instanceof PointEntry) && (dep instanceof PointEntry) && ((PointEntry)dep == (PointEntry)e))
                return true;

            if(dep instanceof ParameterEntry) {
                if((e instanceof ParameterEntry) && ((ParameterEntry)dep == (ParameterEntry)e))
                    return true;

                tmp = ((ParameterEntry)dep).dependsOn(e);
                if(tmp) return true;
            }
        }
        return false;
    }

    public ArrayList<Entry> getPathToPoint(PointEntry p) {
        ArrayList<Entry> out = new ArrayList<Entry>();
        Entry dep;
        out.add(this);
        for (int i = 0; i < depends.size(); i++) {
            dep = depends.get(i);
            if(dep instanceof PointEntry && ((PointEntry)dep == p)) {
                out.add((PointEntry)dep);
                return out;
            }

            if(dep instanceof ParameterEntry && ((ParameterEntry)dep).dependsOn(p)) {
                out.addAll(((ParameterEntry)dep).getPathToPoint(p));
                return out;
            }
        }
        return out;
    }

    public ArrayList<Entry> getPathToParam(ParameterEntry pe) {
        ArrayList<Entry> out = new ArrayList<Entry>();
        Entry dep;
        out.add(this);
        for (int i = 0; i < depends.size(); i++) {
            dep = depends.get(i);
            if(dep instanceof ParameterEntry && ((ParameterEntry)dep == pe)) {
                out.add((ParameterEntry)dep);
                return out;
            }
        }
        return out;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        fireStateChanged();
    }


    public boolean isDependancyIn(ArrayList<ParameterEntry> parameters) {
        if(parameters == null)
            return false;

        ParameterEntry param;
        ArrayList<Entry> deps;
        for (int i = 0; i < parameters.size(); i++) {
            param = parameters.get(i);
            if(param == this) continue;
            deps = param.getDependancies();
            for (int j = 0; j < deps.size(); j++) {
                if(deps.get(j) instanceof ParameterEntry) {
                  if(((ParameterEntry)deps.get(j)) == this)
                      return true;
                }
            }
        }
        return false;
    }


    public PointEntry getPointByPointDescriptor(PointDescriptor pd) {
        if(pd == null)
            return null;

        PointEntry p;
        for (int i = 0; i < depends.size(); i++) {
            Entry dep = depends.get(i);
            if(dep instanceof PointEntry) {
                 p = (PointEntry)dep;
                 if(p.getPointDescriptor() == pd)
                     return p;
            } else if(dep instanceof ParameterEntry) {
                p = ((ParameterEntry)dep).getPointByPointDescriptor(pd);
                if(p != null)
                    return p;
            }
        }

        return null;
    }

    public ArrayList<PointEntry> getAllPointEntries() {
        ArrayList<PointEntry> points = new ArrayList<PointEntry>();
        Entry dep;
        ArrayList<PointEntry> tmp;
        for (int i = 0; i < depends.size(); i++) {
            dep = depends.get(i);
            if(dep instanceof PointEntry) {
                points.add((PointEntry)dep);
            } else if(dep instanceof ParameterEntry) {
                tmp = ((ParameterEntry)dep).getAllPointEntries();
                for (int j = 0; j < tmp.size(); j++) {
                    if(!points.contains(tmp.get(j)))
                        points.add(tmp.get(j));
                }
            }
        }
        return points;
    }

    public ArrayList<ParameterEntry> getAllParameterEntries() {
        ArrayList<ParameterEntry> params = new ArrayList<ParameterEntry>();
        ArrayList<ParameterEntry> tmp;
        for (int i = 0; i < depends.size(); i++) {
            Entry dep = depends.get(i);
            if(dep instanceof ParameterEntry) {
                ParameterEntry param = (ParameterEntry)dep;
                tmp = param.getAllParameterEntries();
                for (int j = 0; j < tmp.size(); j++) {
                    if(!params.contains(tmp.get(j)))
                        params.add(tmp.get(j));
                }
                params.add(param);
            }
        }
        return params;
    }

    /**
     * @return the calculable
     */
    public CalculableParameter getCalculable() {
        return calculable;
    }

    public void addCalculable(CalculableParamsPool.CalculableType type) {
        this.calculable = CalculableParamsPool.getCalculable(type, this.name.replaceAll(" ", "_"));
        System.out.println("parameter id="+this.id+" name="+this.name+" added calculable: "+calculable.getName());
    }

}
