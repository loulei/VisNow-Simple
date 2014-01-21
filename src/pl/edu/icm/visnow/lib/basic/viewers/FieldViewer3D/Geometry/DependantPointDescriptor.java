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
import pl.edu.icm.visnow.datasets.RegularField;

/**
 * @author  Bartosz Borucki (babor@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */

public class DependantPointDescriptor extends PointDescriptor {
    private CalculablePoint cp = null;
    private RegularField field = null;

    public DependantPointDescriptor(String s, CalculablePoint cp, RegularField field) {
        super(s,null,null);
        this.cp = cp;
        this.field = field;
    }

    public DependantPointDescriptor(int n, CalculablePoint cp, RegularField field) {
        super(n,null,null);
        this.cp = cp;
        this.field = field;
    }


    @Override
    public int[] getIndices() {
        if(cp == null)
            return null;

        float[] tmp = cp.getValue();
        if(tmp != null)
            return field.getIndices(tmp[0], tmp[1], tmp[2]);
        
        return  null;
    }

    @Override
    public void setIndices(int[] indices) {
    }

    @Override
    public float[] getWorldCoords() {
        if(cp == null)
            return null;

        return cp.getValue();
    }

    @Override
    public void setWorldCoords(float[] coords) {
    }

    @Override
    public float[] getPhysicalCoords() {
        return null;
    }

    @Override
    public void setPhysicalCoords(float[] physicalCoords) {
    }

    @Override
    public boolean isDependant() {
        return true;
    }

    public boolean dependsOn(PointDescriptor pd) {
        if(cp == null)
            return false;

        ArrayList<PointDescriptor> pds = cp.getDependantPointDescriptors();
        for (int i = 0; i < pds.size(); i++) {
            if(pds.get(i) == pd)
                return true;
        }

        return false;
    }
}
