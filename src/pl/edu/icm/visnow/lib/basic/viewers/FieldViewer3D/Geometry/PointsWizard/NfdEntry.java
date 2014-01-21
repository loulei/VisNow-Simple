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
import java.util.HashMap;
import javax.swing.event.ChangeEvent;
import javax.swing.tree.TreePath;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.PointDescriptor;

/**
 * @author  Bartosz Borucki (babor@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */

public class NfdEntry extends Entry {
    private ArrayList<ParameterEntry> parameters = new ArrayList<ParameterEntry>();

    private String comment = "";

    public NfdEntry(String id, String name, String description) {
        super(Entry.ENTRY_TYPE_NFD, id, name, description);
    }

    @Override
    public String toString() {
        return getName()+" "+getId();
        //return "nfd "+id;
    }

    @Override
    public boolean isReady() {
        for (int i = 0; i < parameters.size(); i++) {
            if(!parameters.get(i).isReady())
                return false;
        }
        return true;
    }

    public void addParameter(ParameterEntry param) {
        param.addChangeListener(this);
        parameters.add(param);
        fireStateChanged();
    }

    public ParameterEntry getParameter(int n) {
        return parameters.get(n);
    }

    public int getParametersSize() {
        return parameters.size();
    }

    public int getParameterIndex(ParameterEntry param) {
        if(param == null)
            return -1;

        for (int i = 0; i < parameters.size(); i++) {
            if(param == parameters.get(i))
                return i;
        }
        return -1;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        fireStateChanged();
    }

    public PointEntry getPointByPointDescriptor(PointDescriptor pd) {
        if(pd == null)
            return null;

        PointEntry p;

        if(!cleanedup) {
            for (int i = 0; i < parameters.size(); i++) {
                ParameterEntry param = parameters.get(i);
                for (int j = 0; j < param.getDependanciesSize(); j++) {
                    Entry dep = param.getDependancy(j);
                    if(dep instanceof PointEntry) {
                         p = (PointEntry)dep;
                         if(p.getPointDescriptor() == pd)
                             return p;
                    }
                }
            }
        } else {
            for (int i = 0; i < parameters.size(); i++) {
                ParameterEntry param = parameters.get(i);
                p = param.getPointByPointDescriptor(pd);
                if(p != null)
                    return p;
            }
        }

        return null;
    }

    public int getNuberOfPointByPointDescriptor(PointDescriptor pd) {
        return getNuberOfPoint(getPointByPointDescriptor(pd));
    }

    public PointEntry getPointByNumber(int n) {
        if(n < 0)
            return null;

        if(!cleanedup) {
            int c = 0;
            for (int i = 0; i < parameters.size(); i++) {
                ParameterEntry param = parameters.get(i);
                for (int j = 0; j < param.getDependanciesSize(); j++) {
                    Entry dep = param.getDependancy(j);
                    if(dep instanceof PointEntry) {
                        if(c == n) {
                            return (PointEntry)dep;
                        }
                        c++;
                    }

                }
            }
            return null;
        } else {
            ArrayList<PointEntry> tmp = this.getAllPointEntries();
            if(n < 0 || n >= tmp.size())
                return null;
            
            return tmp.get(n);
        }        
    }

    public PointEntry getPointById(String id) {
        if(id == null)
            return null;

        ArrayList<PointEntry> tmp = this.getAllPointEntries();
        PointEntry pe;
        for (int i = 0; i < tmp.size(); i++) {
            pe = tmp.get(i);
            if(pe != null && pe.getId().equals(id))
                return pe;
        }
        return null;
    }

    public int getNuberOfParam(ParameterEntry pe) {
        return parameters.indexOf(pe);
    }


    public int getNuberOfPoint(PointEntry p) {
        if(p == null)
            return -1;

        if(!cleanedup) {
            int c = 0;
            for (int i = 0; i < parameters.size(); i++) {
                ParameterEntry param = parameters.get(i);
                for (int j = 0; j < param.getDependanciesSize(); j++) {
                    Entry dep = param.getDependancy(j);
                    if(dep instanceof PointEntry) {
                        if(((PointEntry)dep) == p)
                            return c;
                        c++;
                    }

                }
            }
            return -1;
        } else {
            ArrayList<PointEntry> tmp = this.getAllPointEntries();
            return tmp.indexOf(p);
        }
        
    }

    public TreePath getTreePathToPoint(PointEntry p) {
        if(!cleanedup) {
            Entry[] path = new Entry[3];
            path[0] = this;

            for (int i = 0; i < parameters.size(); i++) {
                if(parameters.get(i).dependsDirectlyOn(p)) {
                    path[1] = parameters.get(i);
                    path[2] = p;
                    return new TreePath(path);
                }
            }
            return null;
        } else {
            ArrayList<Entry> path = new ArrayList<Entry>();
            ArrayList<Entry> tmp;
            path.add(this);
            for (int i = 0; i < parameters.size(); i++) {
                if(parameters.get(i).dependsOn(p)) {
                    tmp = parameters.get(i).getPathToPoint(p);
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

    public TreePath getTreePathToParam(ParameterEntry pe) {
        ArrayList<Entry> path = new ArrayList<Entry>();
        ArrayList<Entry> tmp;
        path.add(this);
        for (int i = 0; i < parameters.size(); i++) {
            if(parameters.get(i).dependsOn(pe) || parameters.get(i) == pe) {
                tmp = parameters.get(i).getPathToParam(pe);
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


    public ArrayList<PointEntry> getAllPointEntries() {
        ArrayList<PointEntry> points = new ArrayList<PointEntry>();

        if(!cleanedup) {
            for (int i = 0; i < parameters.size(); i++) {
                ParameterEntry param = parameters.get(i);
                for (int j = 0; j < param.getDependanciesSize(); j++) {
                    Entry dep = param.getDependancy(j);
                    if(dep instanceof PointEntry) {
                        PointEntry p = (PointEntry)dep;
                        if(!points.contains(p))
                            points.add(p);
                    }
                }
            }
        } else {
            ArrayList<PointEntry> tmp;
            ParameterEntry param;
            for (int i = 0; i < parameters.size(); i++) {
                param = parameters.get(i);
                tmp = param.getAllPointEntries();
                for (int j = 0; j < tmp.size(); j++) {
                    if(!points.contains(tmp.get(j)))
                        points.add(tmp.get(j));
                }
            }            
        }
        return points;
    }

    public HashMap<String,PointEntry> getAllPointEntriesHashMap() {
        HashMap<String,PointEntry> out = new HashMap<String, PointEntry>();        
        ArrayList<PointEntry> points = getAllPointEntries();
        for (int i = 0; i < points.size(); i++) {
            PointEntry pe = points.get(i);
            out.put(pe.getId(), pe);
        }
        return out;
    }
    
    private boolean cleanedup = false;
    public void cleanup() {
        cleanedup = true;
        ParameterEntry param;
        for (int i = 0; i < parameters.size(); i++) {
            param = parameters.get(i);
            if(param.isDependancyIn(parameters)) {
                parameters.remove(param);
                i--;
            }
        }
    }


    public ArrayList<ParameterEntry> getAllParameterEntries() {
        if(!cleanedup) {
            return parameters;
        } else {
            ArrayList<ParameterEntry> params = new ArrayList<ParameterEntry>();
            ArrayList<ParameterEntry> tmp;
            for (int i = 0; i < parameters.size(); i++) {
                ParameterEntry param = parameters.get(i);
                tmp = param.getAllParameterEntries();
                for (int j = 0; j < tmp.size(); j++) {
                    if(!params.contains(tmp.get(j))) {
                        params.add(tmp.get(j));
                    }
                }
                if(!params.contains(param)) {
                    params.add(param);
                }
            }
            return params;
        }
    }

    public ArrayList<ParameterEntry> getAllPointEntryParents(PointEntry p, boolean directDependancy) {
        if(p == null)
            return null;

        ArrayList<ParameterEntry> params = getAllParameterEntries();
        ArrayList<ParameterEntry> out = new ArrayList<ParameterEntry>();

        for (int i = 0; i < params.size(); i++) {
            if(directDependancy) {
                if(params.get(i).dependsDirectlyOn(p))
                    out.add(params.get(i));
            } else {
                if(params.get(i).dependsOn(p))
                    out.add(params.get(i));
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
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

}
