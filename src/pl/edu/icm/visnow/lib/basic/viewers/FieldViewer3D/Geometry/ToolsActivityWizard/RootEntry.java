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
import javax.swing.tree.TreePath;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.PointDescriptor;

/**
 * @author  Bartosz Borucki (babor@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */

public class RootEntry extends Entry {
    private ArrayList<BranchEntry> branches = new ArrayList<BranchEntry>();

    private String comment = "";

    public RootEntry(String id, String name, String description) {
        super(Entry.ENTRY_TYPE_ROOT, id, name, description);
    }

    @Override
    public String toString() {
        return getName()+" "+getId();
    }

    @Override
    public boolean isReady() {
        for (int i = 0; i < branches.size(); i++) {
            if(!branches.get(i).isReady())
                return false;
        }
        return true;
    }

    public void addBranch(BranchEntry branch) {
        branch.addChangeListener(this);
        branches.add(branch);
        fireStateChanged();
    }

    public BranchEntry getBranch(int n) {
        return branches.get(n);
    }

    public int getBranchesSize() {
        return branches.size();
    }

    public int getBranchIndex(BranchEntry branch) {
        if(branch == null)
            return -1;

        for (int i = 0; i < branches.size(); i++) {
            if(branch == branches.get(i))
                return i;
        }
        return -1;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        fireStateChanged();
    }

    public ToolEntry getToolByPointDescriptor(PointDescriptor pd) {
        if(pd == null)
            return null;

        ToolEntry t;

        if(!cleanedup) {
            for (int i = 0; i < branches.size(); i++) {
                BranchEntry param = branches.get(i);
                for (int j = 0; j < param.getDependanciesSize(); j++) {
                    Entry dep = param.getDependancy(j);
                    if(dep instanceof ToolEntry) {
                         t = (ToolEntry)dep;
                         if(t.getPointDescriptors().contains(pd))
                             return t;
                    }
                }
            }
        } else {
            for (int i = 0; i < branches.size(); i++) {
                BranchEntry param = branches.get(i);
                t = param.getToolByPointDescriptor(pd);
                if(t != null)
                    return t;
            }
        }
        return null;
    }

    public int getNuberOfToolByPointDescriptor(PointDescriptor pd) {
        return getNuberOfTool(getToolByPointDescriptor(pd));
    }

    public ToolEntry getToolEntryByNumber(int n) {
        if(n < 0)
            return null;

        if(!cleanedup) {
            int c = 0;
            for (int i = 0; i < branches.size(); i++) {
                BranchEntry branch = branches.get(i);
                for (int j = 0; j < branch.getDependanciesSize(); j++) {
                    Entry dep = branch.getDependancy(j);
                    if(dep instanceof ToolEntry) {
                        if(c == n) {
                            return (ToolEntry)dep;
                        }
                        c++;
                    }

                }
            }
            return null;
        } else {
            ArrayList<ToolEntry> tmp = this.getAllToolEntries();
            if(n < 0 || n >= tmp.size())
                return null;
            
            return tmp.get(n);
        }        
    }

    public ToolEntry getToolById(String id) {
        if(id == null)
            return null;

        ArrayList<ToolEntry> tmp = this.getAllToolEntries();
        ToolEntry te;
        for (int i = 0; i < tmp.size(); i++) {
            te = tmp.get(i);
            if(te != null && te.getId().equals(id))
                return te;
        }
        return null;
    }

    public int getNuberOfBranch(BranchEntry be) {
        return branches.indexOf(be);
    }


    public int getNuberOfTool(ToolEntry te) {
        if(te == null)
            return -1;

        if(!cleanedup) {
            int c = 0;
            for (int i = 0; i < branches.size(); i++) {
                BranchEntry param = branches.get(i);
                for (int j = 0; j < param.getDependanciesSize(); j++) {
                    Entry dep = param.getDependancy(j);
                    if(dep instanceof ToolEntry) {
                        if(((ToolEntry)dep) == te)
                            return c;
                        c++;
                    }

                }
            }
            return -1;
        } else {
            ArrayList<ToolEntry> tmp = this.getAllToolEntries();
            return tmp.indexOf(te);
        }
        
    }

    public TreePath getTreePathToTool(ToolEntry te) {
        if(!cleanedup) {
            Entry[] path = new Entry[3];
            path[0] = this;

            for (int i = 0; i < branches.size(); i++) {
                if(branches.get(i).dependsDirectlyOn(te)) {
                    path[1] = branches.get(i);
                    path[2] = te;
                    return new TreePath(path);
                }
            }
            return null;
        } else {
            ArrayList<Entry> path = new ArrayList<Entry>();
            ArrayList<Entry> tmp;
            path.add(this);
            for (int i = 0; i < branches.size(); i++) {
                if(branches.get(i).dependsOn(te)) {
                    tmp = branches.get(i).getPathToTool(te);
                    for (int j = 0; j < tmp.size(); j++) {
                        path.add(tmp.get(j));
                    }
                    break;
                }
            }

            if(path.size() > 0) {
                Entry[] epath = new Entry[path.size()];
                for (int i = 0; i < epath.length; i++) {
                    epath[i] = path.get(i);
                }
                return new TreePath(epath);
            } else {
                return null;
            }


        }
    }

    public ArrayList<ToolEntry> getAllToolEntries() {
        ArrayList<ToolEntry> tools = new ArrayList<ToolEntry>();

        if(!cleanedup) {
            for (int i = 0; i < branches.size(); i++) {
                BranchEntry branch = branches.get(i);
                for (int j = 0; j < branch.getDependanciesSize(); j++) {
                    Entry dep = branch.getDependancy(j);
                    if(dep instanceof ToolEntry) {
                        ToolEntry t = (ToolEntry)dep;
                        if(!tools.contains(t))
                            tools.add(t);
                    }
                }
            }
        } else {
            ArrayList<ToolEntry> tmp;
            BranchEntry branch;
            for (int i = 0; i < branches.size(); i++) {
                branch = branches.get(i);
                tmp = branch.getAllToolEntries();
                for (int j = 0; j < tmp.size(); j++) {
                    if(!tools.contains(tmp.get(j)))
                        tools.add(tmp.get(j));
                }
            }            
        }
        return tools;
    }

    private boolean cleanedup = false;
    public void cleanup() {
        cleanedup = true;
        BranchEntry branch;
        for (int i = 0; i < branches.size(); i++) {
            branch = branches.get(i);
            if(branch.isDependancyIn(branches)) {
                branches.remove(branch);
                i--;
            }
        }
    }


    public ArrayList<BranchEntry> getAllBranchEntries() {
        if(!cleanedup) {
            return branches;
        } else {
            ArrayList<BranchEntry> bes = new ArrayList<BranchEntry>();
            ArrayList<BranchEntry> tmp;
            for (int i = 0; i < branches.size(); i++) {
                BranchEntry branch = branches.get(i);
                tmp = branch.getAllBranchEntries();
                for (int j = 0; j < tmp.size(); j++) {
                    if(!bes.contains(tmp.get(j))) {
                        bes.add(tmp.get(j));
                    }
                }
                if(!bes.contains(branch)) {
                    bes.add(branch);
                }
            }
            return bes;
        }
    }

    public ArrayList<CpointEntry> getAllCpointEntries() {
        ArrayList<CpointEntry> cpes = new ArrayList<CpointEntry>();
        ArrayList<BranchEntry> bes = getAllBranchEntries();
        for (int i = 0; i < bes.size(); i++) {
            ArrayList<CpointEntry> tmp = bes.get(i).getAllCpointEntries();
            for (int j = 0; j < tmp.size(); j++) {
                if(!cpes.contains(tmp.get(j)))
                    cpes.add(tmp.get(j));
            }
        }
        return cpes;
    }

    public ArrayList<BranchEntry> getAllToolEntryParents(ToolEntry p, boolean directDependancy) {
        if(p == null)
            return null;

        ArrayList<BranchEntry> bes = getAllBranchEntries();
        ArrayList<BranchEntry> out = new ArrayList<BranchEntry>();

        for (int i = 0; i < bes.size(); i++) {
            if(directDependancy) {
                if(bes.get(i).dependsDirectlyOn(p))
                    out.add(bes.get(i));
            } else {
                if(bes.get(i).dependsOn(p))
                    out.add(bes.get(i));
            }
        }
        
        return out;
    }

    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @bes comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

}
