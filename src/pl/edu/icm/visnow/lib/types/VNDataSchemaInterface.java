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

package pl.edu.icm.visnow.lib.types;

/**
 *
 * @author Bartosz Borucki (babor@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public interface VNDataSchemaInterface {

    public abstract Class getVnDataType();

    public abstract boolean isEmpty();

    public abstract boolean isField();
    public abstract int getNSpace();
    public abstract boolean isTime();
    public abstract int[] getDataVeclens();
    public abstract int[] getDataTypes();
    public abstract String[] getDataNames();
    public boolean hasScalarComponent();
    public boolean hasVectorComponent(int veclen);
    
    public abstract int getNData();

    public abstract boolean isRegular();
    public abstract int getNDims();
    public abstract int[] getDims();
    public abstract boolean isAffine();
    public abstract boolean isCoords();
    
    public abstract boolean isIrregular();
    public abstract boolean isCellSets();
    public abstract int getNCellSets();
    public abstract int[] getNCellData();
    public abstract int[][] getCellDataVeclens();
    public abstract int[][] getCellDataTypes();
    public abstract String[][] getCellDataNames();
    public boolean hasCellScalarComponent();
    public boolean hasCellVectorComponent(int veclen);
    public abstract String[] getCellSetNames();
    public boolean hasCellsPoint();
    public boolean hasCellsSegment();
    public boolean hasCellsTriangle();
    public boolean hasCellsQuad();
    public boolean hasCellsTetra();
    public boolean hasCellsPyramid();
    public boolean hasCellsPrism();
    public boolean hasCellsHexahedron();
    public boolean hasCells2D();
    public boolean hasCells3D();
    public void createStats();

}