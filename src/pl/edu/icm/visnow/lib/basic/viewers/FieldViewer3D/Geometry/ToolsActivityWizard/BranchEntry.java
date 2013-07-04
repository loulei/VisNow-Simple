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
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.CalculableParameter;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.CalculableParamsPool;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.PointDescriptor;

/**
 * @author  Bartosz Borucki (babor@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */

public class BranchEntry extends Entry {
    private ArrayList<Entry> depends = new ArrayList<Entry>();
    private CalculableParameter calculable = null;

    public BranchEntry(String id, String name, String description) {
        super(Entry.ENTRY_TYPE_BRANCH, id, name, description);
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
            if((dep instanceof ToolEntry) && ((ToolEntry)dep == (ToolEntry)e))
                return true;

            if(dep instanceof CpointEntry) {
                if((e instanceof CpointEntry) && ((CpointEntry)dep == (CpointEntry)e))
                    return true;

                if(e instanceof ToolEntry) {
                    tmp = ((CpointEntry)dep).dependsOn((ToolEntry)e);
                    if(tmp) return true;
                }
            }

            if(dep instanceof BranchEntry) {
                if((e instanceof BranchEntry) && ((BranchEntry)dep == (BranchEntry)e))
                    return true;

                tmp = ((BranchEntry)dep).dependsOn(e);
                if(tmp) return true;
            }
        }
        return false;
    }

    public ArrayList<Entry> getPathToTool(ToolEntry t) {
        ArrayList<Entry> out = new ArrayList<Entry>();
        Entry dep;
        out.add(this);
        for (int i = 0; i < depends.size(); i++) {
            dep = depends.get(i);
            if(dep instanceof ToolEntry && ((ToolEntry)dep == t)) {
                out.add((ToolEntry)dep);
                return out;
            }

            if(dep instanceof CpointEntry && ((CpointEntry)dep).dependsOn(t)) {
                out.addAll(((CpointEntry)dep).getPathToTool(t));
                return out;
            }

            if(dep instanceof BranchEntry && ((BranchEntry)dep).dependsOn(t)) {
                out.addAll(((BranchEntry)dep).getPathToTool(t));
                return out;
            }
        }
        return out;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        fireStateChanged();
    }

    public boolean isDependancyIn(ArrayList<BranchEntry> parameters) {
        if(parameters == null)
            return false;

        BranchEntry param;
        ArrayList<Entry> deps;
        for (int i = 0; i < parameters.size(); i++) {
            param = parameters.get(i);
            if(param == this) continue;
            deps = param.getDependancies();
            for (int j = 0; j < deps.size(); j++) {
                if(deps.get(j) instanceof BranchEntry) {
                  if(((BranchEntry)deps.get(j)) == this)
                      return true;
                }
            }
        }
        return false;
    }


    public ToolEntry getToolByPointDescriptor(PointDescriptor pd) {
        if(pd == null)
            return null;

        ToolEntry t;
        for (int i = 0; i < depends.size(); i++) {
            Entry dep = depends.get(i);
            if(dep instanceof ToolEntry) {
                 t = (ToolEntry)dep;
                 if(t.getPointDescriptors().contains(pd))
                     return t;
            } else if(dep instanceof CpointEntry) {
                t = ((CpointEntry)dep).getToolByPointDescriptor(pd);
                if(t != null)
                    return t;
            } else if(dep instanceof BranchEntry) {
                t = ((BranchEntry)dep).getToolByPointDescriptor(pd);
                if(t != null)
                    return t;
            }
        }
        return null;
    }

    public ArrayList<ToolEntry> getAllToolEntries() {
        ArrayList<ToolEntry> tools = new ArrayList<ToolEntry>();
        Entry dep;
        ArrayList<ToolEntry> tmp;
        for (int i = 0; i < depends.size(); i++) {
            dep = depends.get(i);
            if(dep instanceof ToolEntry) {
                tools.add((ToolEntry)dep);
            } else if(dep instanceof CpointEntry) {
                tmp = ((CpointEntry)dep).getAllToolEntries();
                for (int j = 0; j < tmp.size(); j++) {
                    if(!tools.contains(tmp.get(j)))
                        tools.add(tmp.get(j));
                }
            } else if(dep instanceof BranchEntry) {
                tmp = ((BranchEntry)dep).getAllToolEntries();
                for (int j = 0; j < tmp.size(); j++) {
                    if(!tools.contains(tmp.get(j)))
                        tools.add(tmp.get(j));
                }
            }
        }
        return tools;
    }

    public ArrayList<CpointEntry> getAllCpointEntries() {
        ArrayList<CpointEntry> cpoints = new ArrayList<CpointEntry>();
        Entry dep;
        ArrayList<CpointEntry> tmp;
        for (int i = 0; i < depends.size(); i++) {
            dep = depends.get(i);
            if(dep instanceof CpointEntry) {
                cpoints.add((CpointEntry)dep);
            } else if(dep instanceof BranchEntry) {
                tmp = ((BranchEntry)dep).getAllCpointEntries();
                for (int j = 0; j < tmp.size(); j++) {
                    if(!cpoints.contains(tmp.get(j)))
                        cpoints.add(tmp.get(j));
                }
            }
        }
        return cpoints;
    }

    public ArrayList<BranchEntry> getAllBranchEntries() {
        ArrayList<BranchEntry> branches = new ArrayList<BranchEntry>();
        ArrayList<BranchEntry> tmp;
        for (int i = 0; i < depends.size(); i++) {
            Entry dep = depends.get(i);
            if(dep instanceof BranchEntry) {
                BranchEntry branch = (BranchEntry)dep;
                tmp = branch.getAllBranchEntries();
                for (int j = 0; j < tmp.size(); j++) {
                    if(!branches.contains(tmp.get(j)))
                        branches.add(tmp.get(j));
                }
                if(!branches.contains(branch))
                    branches.add(branch);
            }
        }
        return branches;
    }


    /**
     * @return the calculable
     */
    public CalculableParameter getCalculable() {
        return calculable;
    }

    public void addCalculable(CalculableParamsPool.CalculableType type, String calculableShortcut, float[] parameters) {
        this.calculable = CalculableParamsPool.getCalculable(type, this.name.replaceAll(" ", "_"), parameters);
        this.calculable.setShortcut(calculableShortcut);
    }

}
