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

package pl.edu.icm.visnow.datamaps;

import java.beans.PropertyChangeListener;

/**
 * 
 * A color map stores mapping between values and colors. Color map can be imagined either as a map of pairs:
 * <p>
 * value -> color
 * </p>
 * 
 * or as a set of pairs: (value (called <b>knot</b>), color), between which color changes gradually.
 * 
 * @author Michał Łyczek (lyczek@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public abstract interface ColorMap {

    /**
     * Returns true if colormap is a VisNow build-in colormap
     *
     * @return true if colormap is a VisNow build-in colormap
     */
    public boolean isBuildin();

    /**
     * Returns colormap name
     *
     * @return colormap name
     */
    public String getName();

    /**
     * Returns a list of ints (for example 256 values), where each int
     * represents one (R, G, B) triple.
     *
     * @return
     */
    public int[] getRGBColorTable();

    /**
     * Returns a list of ints (for example 256 values), where each int
     * represents one (A, R, G, B) triple.
     *
     * @return
     */
    public int[] getARGBColorTable();

    /**
     * Returns a list of bytes (for example 3*256 values), where each triple of bytes
     * represents one (R, G, B) triple.
     *
     * @return
     */
    public byte[] getRGBByteColorTable();

    /**
     * Returns a list of bytes (for example 4*256 values), where each quadruple of bytes
     * represents one (A, R, G, B) quadruple.
     *
     * @return
     */
    public byte[] getARGBByteColorTable();

    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener);

    public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener);
}
