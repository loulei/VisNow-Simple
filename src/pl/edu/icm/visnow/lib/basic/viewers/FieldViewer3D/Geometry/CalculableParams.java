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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author  Bartosz Borucki (babor@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */

public class CalculableParams implements GeometryParamsListener {
    private ArrayList<CalculableParameter> cps = new ArrayList<CalculableParameter>();
    private boolean paintCalculable2D = true;

    public CalculableParams() {
        
    }

    public int addCalculableParameter(CalculableParameter cp) {
        if(cp == null)
            return -1;
        
        if(cps.add(cp)) {
            fireStateChanged();
            return cps.size()-1;
        } else {
            return -1;
        }
    }

    public void removeCalculableParameter(int i) {
        if(cps.get(i) != null) {
            cps.remove(i);
            fireStateChanged();
        }
    }

    public void removeCalculableParameter(CalculableParameter cp) {
        if(cp == null)
            return;

        if(cps.contains(cp)) {
            cps.remove(cp);
            fireStateChanged();
        }
    }

    public void removeCalculableParameters(int[] indices) {
        if(indices == null)
            return;

        int tmpi;
        for (int i = 0; i < indices.length; i++) {
            tmpi = indices[i];
            cps.remove(indices[i]);
            for (int j = i+1; j < indices.length; j++) {
                if(indices[j] > tmpi)
                    indices[j]--;
            }
        }
        fireStateChanged();

    }

    public boolean isContainsCalculableByName(CalculableParameter cp) {
        if(cp == null)
            return false;

        for (int i = 0; i < cps.size(); i++) {
            if(cps.get(i).getName().equals(cp.getName()))
                return true;
        }
        return false;
    }

    public CalculableParameter getCalculableParameter(int i) {
        return cps.get(i);
    }

    public CalculableParameter getCalculableParameterByName(String name) {
        if(name == null)
            return null;

        CalculableParameter cp;
        for (int i = 0; i < cps.size(); i++) {
            cp = cps.get(i);
            if(cp != null && cp.getName().equals(name))
                return cp;
        }
        return null;
    }

    public CalculableParameter[] getCalculableParametersArray() {
        return (CalculableParameter[])cps.toArray();
    }

    public ArrayList<CalculableParameter> getCalculableParameters() {
        return cps;
    }

    public int getNCalculableParameters() {
        return cps.size();
    }

    /**
     * Utility field holding list of ChangeListeners.
     */
    private transient ArrayList<ChangeListener> changeListenerList = new ArrayList<ChangeListener>();

    /**
     * Registers ChangeListener to receive events.
     * @param listener The listener to register.
     */
    public synchronized void addChangeListener(ChangeListener listener) {
        changeListenerList.add(listener);
    }

    /**
     * Removes ChangeListener from the list of listeners.
     * @param listener The listener to remove.
     */
    public synchronized void removeChangeListener(ChangeListener listener) {
        changeListenerList.remove(listener);
    }

    /**
     * Notifies all registered listeners about the event.
     *
     * @param object Parameter #1 of the <CODE>ChangeEvent<CODE> constructor.
     */
    public void fireStateChanged() {
        ChangeEvent e = new ChangeEvent(this);
        for (ChangeListener listener : changeListenerList) {
            listener.stateChanged(e);
        }
    }

    @Override
    public void onGeometryParamsChanged(GeometryParamsEvent e) {
        if(e.getType() == GeometryParamsEvent.TYPE_POINT_ADDED || e.getType() == GeometryParamsEvent.TYPE_POINT_REMOVED || e.getType() == GeometryParamsEvent.TYPE_POINT_MODIFIED  || e.getType() == GeometryParamsEvent.TYPE_ALL) {
            //check all cps if dependant point still in gparams, if not remove cp
            ArrayList<PointDescriptor> tmp;

            GeometryParams gparams = (GeometryParams)e.getSource();
            for (int i = 0; i < cps.size(); i++) {
                tmp = cps.get(i).getDependantPointDescriptors();
                for (int j = 0; j < tmp.size(); j++) {
                    if(!gparams.isPointsContain(tmp.get(j))) {
                        cps.remove(i);
                        i--;
                        break;
                    }
                }
            }
            fireStateChanged();
        }
    }

    /**
     * @return the paintCalculable2D
     */
    public boolean isPaintCalculable2D() {
        return paintCalculable2D;
    }

    /**
     * @param paintCalculable2D the paintCalculable2D to set
     */
    public void setPaintCalculable2D(boolean paintCalculable2D) {
        if(paintCalculable2D != this.paintCalculable2D) {
            this.paintCalculable2D = paintCalculable2D;
            fireStateChanged();
        }
    }

    

}
