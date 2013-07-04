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

package pl.edu.icm.visnow.lib.basic.filters.ComponentOperations;

import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.datasets.RegularField;

/**
 *
 * @author Bartosz Borucki (babor@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class CoordsFromDataCore {

    private Params params = null;
    private RegularField inField = null;
    private RegularField outField = null;

    public CoordsFromDataCore() {

    }
    
    public void setData(RegularField inField, Params p, RegularField outField) {
        this.params = p;
        this.inField = inField;
        this.outField = outField;
    }

    void update() {
        if(inField == null || params == null)
            return;

        int[] dims = inField.getDims();
        int nDims = dims.length;

        int nData = 1;
        for (int i = 0; i < nDims; i++) {
            nData *= dims[i];
        }

        if(params.isAddIndexComponent()) {
            int[] indexData = new int[nData];
            for (int i = 0; i < nData; i++) {
                indexData[i] = i;
            }
            outField.addData(DataArray.create(indexData, 1, "index"));
        }

        switch(nDims) {
            case 1:
                update1D();
                break;
            case 2:
                update2D();
                break;
            case 3:
                update3D();
                break;

        }

    }

    private void update1D() {
        int[] dims = inField.getDims();
        int nData = dims[0];
        float[] c,coords;

        int xComp, yComp, zComp;
        xComp = params.getXCoordComponent();
        yComp = params.getYCoordComponent();
        zComp = params.getZCoordComponent();
        int nFieldData = inField.getNData();
        if(xComp >= nFieldData)
            xComp = nFieldData-1;
        if(yComp >= nFieldData)
            yComp = nFieldData-1;
        if(zComp >= nFieldData)
            zComp = nFieldData-1;

        float xs,ys,zs;
        xs = params.getXCoordScaleVal();
        ys = params.getYCoordScaleVal();
        zs = params.getZCoordScaleVal();

        float[] xc,yc,zc;
        String[] axesNames = null;

        switch(params.getNDims()) {
            case 1:                
                coords = new float[nData];
                axesNames = new String[1];
                if(xComp >= 0) {
                    xc = inField.getData(xComp).getFData();
                    axesNames[0] = inField.getData(xComp).getName();
                    for (int i = 0; i < nData; i++) {
                        coords[i] = xs*xc[i];
                    }
                } else if(xComp == -1) {
                    axesNames[0] = "i";
                    for (int i = 0; i < nData; i++) {
                        coords[i] = xs*(float)i;
                    }
                } else if(xComp == -100) {
                    axesNames[0] = "";
                    for (int i = 0; i < nData; i++) {
                        coords[i] = 0.0f;
                    }
                }
                outField.setNSpace(1);
                outField.setCoords(coords);
                outField.setAxesNames(axesNames);
                break;
            case 2:
                coords = new float[2*nData];
                axesNames = new String[2];
                if(xComp >= 0) {
                    xc = inField.getData(xComp).getFData();
                    axesNames[0] =  inField.getData(xComp).getName();
                    for (int i = 0; i < nData; i++) {
                        coords[2*i] = xs*xc[i];
                    }
                } else if(xComp == -1) {
                    axesNames[0] = "i";
                    for (int i = 0; i < nData; i++) {
                        coords[2*i] = xs*(float)i;
                    }
                } else if(xComp == -100) {
                    axesNames[0] = "";
                    for (int i = 0; i < nData; i++) {
                        coords[2*i] = 0.0f;
                    }
                }

                if(yComp >= 0) {
                    yc = inField.getData(yComp).getFData();
                    axesNames[1] = inField.getData(yComp).getName();
                    for (int i = 0; i < nData; i++) {
                        coords[2*i+1] = ys*yc[i];
                    }
                } else if(yComp == -1) {
                    axesNames[1] = "i";
                    for (int i = 0; i < nData; i++) {
                        coords[2*i+1] = ys*(float)i;
                    }
                } else if(yComp == -100) {
                    axesNames[1] = "";
                    for (int i = 0; i < nData; i++) {
                        coords[2*i+1] = 0.0f;
                    }
                }
                outField.setNSpace(2);
                outField.setCoords(coords);                
                outField.setAxesNames(axesNames);
                break;
            case 3:
                coords = new float[3*nData];
                axesNames = new String[3];
                if(xComp >= 0) {
                    xc = inField.getData(xComp).getFData();
                    axesNames[0] = inField.getData(xComp).getName();
                    for (int i = 0; i < nData; i++) {
                        coords[3*i] = xs*xc[i];
                    }
                } else if(xComp == -1) {
                    axesNames[0] = "i";
                    for (int i = 0; i < nData; i++) {
                        coords[3*i] = xs*(float)i;
                    }
                } else if(xComp == -100) {
                    axesNames[0] = "";
                    for (int i = 0; i < nData; i++) {
                        coords[3*i] = 0.0f;
                    }
                }

                if(yComp >= 0) {
                    yc = inField.getData(yComp).getFData();
                    axesNames[1] = inField.getData(yComp).getName();
                    for (int i = 0; i < nData; i++) {
                        coords[3*i+1] = ys*yc[i];
                    }
                } else if(yComp == -1) {
                    axesNames[1] = "i";
                    for (int i = 0; i < nData; i++) {
                        coords[3*i+1] = ys*(float)i;
                    }
                } else if(yComp == -100) {
                    axesNames[1] = "";
                    for (int i = 0; i < nData; i++) {
                        coords[3*i+1] = 0.0f;
                    }
                }

                if(zComp >= 0) {
                    zc = inField.getData(zComp).getFData();
                    axesNames[2] = inField.getData(zComp).getName();
                    for (int i = 0; i < nData; i++) {
                        coords[3*i+2] = zs*zc[i];
                    }
                } else if(zComp == -1) {
                    axesNames[2] = "i";
                    for (int i = 0; i < nData; i++) {
                        coords[3*i+2] = zs*(float)i;
                    }
                } else if(zComp == -100) {
                    axesNames[2] = "";
                    for (int i = 0; i < nData; i++) {
                        coords[3*i+2] = 0.0f;
                    }
                }
                outField.setNSpace(3);
                outField.setCoords(coords);                                
                outField.setAxesNames(axesNames);
                break;
        }
    }

    private void update2D() {
        int[] dims = inField.getDims();
        int nData = dims[0]*dims[1];
        float[] c,coords;

        int xComp, yComp, zComp;
        xComp = params.getXCoordComponent();
        yComp = params.getYCoordComponent();
        zComp = params.getZCoordComponent();
        int nFieldData = inField.getNData();
        if(xComp >= nFieldData)
            xComp = nFieldData-1;
        if(yComp >= nFieldData)
            yComp = nFieldData-1;
        if(zComp >= nFieldData)
            zComp = nFieldData-1;

        float xs,ys,zs;
        String[] axesNames = null;
        xs = params.getXCoordScaleVal();
        ys = params.getYCoordScaleVal();
        zs = params.getZCoordScaleVal();

        float[] xc,yc,zc;

        if(params.getNDims() < 2) {
            outField = null;
            return;
        }

        switch(params.getNDims()) {
            case 2:
                coords = new float[2*nData];
                axesNames = new String[2];
                if(xComp >= 0) {
                    xc = inField.getData(xComp).getFData();
                    axesNames[0] = inField.getData(xComp).getName();
                    for (int i = 0; i < nData; i++) {
                        coords[2*i] = xs*xc[i];
                    }
                } else {
                    switch(xComp) {
                        case -1:
                            axesNames[0] = "i";
                            for (int j = 0, n = 0; j < dims[1]; j++) {
                                for (int i = 0; i < dims[0]; i++,n++) {
                                    coords[2*n] = xs*(float)i;
                                }
                            }
                            break;
                        case -2:
                            axesNames[0] = "j";
                            for (int j = 0, n = 0; j < dims[1]; j++) {
                                for (int i = 0; i < dims[0]; i++,n++) {
                                    coords[2*n] = xs*(float)j;
                                }
                            }
                            break;
                        case -100:
                            axesNames[0] = "";
                            for (int j = 0, n = 0; j < dims[1]; j++) {
                                for (int i = 0; i < dims[0]; i++,n++) {
                                    coords[2*n] = 0.0f;
                                }
                            }
                            break;
                    }
                }

                if(yComp >= 0) {
                    yc = inField.getData(yComp).getFData();
                    axesNames[1] = inField.getData(yComp).getName();
                    for (int i = 0; i < nData; i++) {
                        coords[2*i+1] = ys*yc[i];
                    }
                } else {
                    switch(yComp) {
                        case -1:
                            axesNames[1] = "i";
                            for (int j = 0, n = 0; j < dims[1]; j++) {
                                for (int i = 0; i < dims[0]; i++,n++) {
                                    coords[2*n+1] = ys*(float)i;
                                }
                            }
                            break;
                        case -2:
                            axesNames[1] = "j";
                            for (int j = 0, n = 0; j < dims[1]; j++) {
                                for (int i = 0; i < dims[0]; i++,n++) {
                                    coords[2*n+1] = ys*(float)j;
                                }
                            }
                            break;
                        case -100:
                            axesNames[1] = "";
                            for (int j = 0, n = 0; j < dims[1]; j++) {
                                for (int i = 0; i < dims[0]; i++,n++) {
                                    coords[2*n+1] = 0.0f;
                                }
                            }
                            break;
                    }
                }
                outField.setNSpace(2);
                outField.setCoords(coords);                
                outField.setAxesNames(axesNames);
                break;
            case 3:
                coords = new float[3*nData];
                axesNames = new String[3];
                if(xComp >= 0) {
                    xc = inField.getData(xComp).getFData();
                    axesNames[0] = inField.getData(xComp).getName();
                    for (int i = 0; i < nData; i++) {
                        coords[3*i] = xs*xc[i];
                    }
                } else {
                    switch(xComp) {
                        case -1:
                            axesNames[0] = "i";
                            for (int j = 0, n = 0; j < dims[1]; j++) {
                                for (int i = 0; i < dims[0]; i++,n++) {
                                    coords[3*n] = xs*(float)i;
                                }
                            }
                            break;
                        case -2:
                            axesNames[0] = "j";
                            for (int j = 0, n = 0; j < dims[1]; j++) {
                                for (int i = 0; i < dims[0]; i++,n++) {
                                    coords[3*n] = xs*(float)j;
                                }
                            }
                            break;
                        case -100:
                            axesNames[0] = "";
                            for (int j = 0, n = 0; j < dims[1]; j++) {
                                for (int i = 0; i < dims[0]; i++,n++) {
                                    coords[3*n] = 0.0f;
                                }
                            }
                            break;
                    }
                }

                if(yComp >= 0) {
                    yc = inField.getData(yComp).getFData();
                    axesNames[1] = inField.getData(yComp).getName();
                    for (int i = 0; i < nData; i++) {
                        coords[3*i+1] = ys*yc[i];
                    }
                } else {
                    switch(yComp) {
                        case -1:
                            axesNames[1] = "i";
                            for (int j = 0, n = 0; j < dims[1]; j++) {
                                for (int i = 0; i < dims[0]; i++,n++) {
                                    coords[3*n+1] = ys*(float)i;
                                }
                            }
                            break;
                        case -2:
                            axesNames[1] = "j";
                            for (int j = 0, n = 0; j < dims[1]; j++) {
                                for (int i = 0; i < dims[0]; i++,n++) {
                                    coords[3*n+1] = ys*(float)j;
                                }
                            }
                            break;
                        case -100:
                            axesNames[1] = "";
                            for (int j = 0, n = 0; j < dims[1]; j++) {
                                for (int i = 0; i < dims[0]; i++,n++) {
                                    coords[3*n+1] = 0.0f;
                                }
                            }
                            break;
                    }
                }

                if(zComp >= 0) {
                    zc = inField.getData(zComp).getFData();
                    axesNames[2] = inField.getData(zComp).getName();
                    for (int i = 0; i < nData; i++) {
                        coords[3*i+2] = zs*zc[i];
                    }
                } else {
                    switch(zComp) {
                        case -1:
                            axesNames[2] = "i";
                            for (int j = 0, n = 0; j < dims[1]; j++) {
                                for (int i = 0; i < dims[0]; i++,n++) {
                                    coords[3*n+2] = zs*(float)i;
                                }
                            }
                            break;
                        case -2:
                            axesNames[2] = "j";
                            for (int j = 0, n = 0; j < dims[1]; j++) {
                                for (int i = 0; i < dims[0]; i++,n++) {
                                    coords[3*n+2] = zs*(float)j;
                                }
                            }
                            break;
                        case -100:
                            axesNames[2] = "";
                            for (int j = 0, n = 0; j < dims[1]; j++) {
                                for (int i = 0; i < dims[0]; i++,n++) {
                                    coords[3*n+2] = 0.0f;
                                }
                            }
                            break;
                    }
                }
                outField.setNSpace(3);
                outField.setCoords(coords);
                outField.setAxesNames(axesNames);
                break;
        }
    }

    private void update3D() {
        int[] dims = inField.getDims();
        int nDims = 3;
        int nData = dims[0]*dims[1]*dims[2];
        float[] c,coords;

        int xComp, yComp, zComp;
        xComp = params.getXCoordComponent();
        yComp = params.getYCoordComponent();
        zComp = params.getZCoordComponent();
        int nFieldData = inField.getNData();
        if(xComp >= nFieldData)
            xComp = nFieldData-1;
        if(yComp >= nFieldData)
            yComp = nFieldData-1;
        if(zComp >= nFieldData)
            zComp = nFieldData-1;

        float xs,ys,zs;
        String[] axesNames = null;
        xs = params.getXCoordScaleVal();
        ys = params.getYCoordScaleVal();
        zs = params.getZCoordScaleVal();

        float[] xc,yc,zc;

        if(params.getNDims() < 3) {
            outField = null;
            return;
        }

        coords = new float[3*nData];
        axesNames = new String[3];

        if(xComp >= 0) {
            xc = inField.getData(xComp).getFData();
            axesNames[0] = inField.getData(xComp).getName();
            for (int i = 0; i < nData; i++) {
                coords[3*i] = xs*xc[i];
            }
        } else {
            switch(xComp) {
                case -1:
                    axesNames[0] = "i";
                    for (int k = 0, n = 0; k < dims[2]; k++) {
                        for (int j = 0; j < dims[1]; j++) {
                            for (int i = 0; i < dims[0]; i++,n++) {
                                coords[3*n] = xs*(float)i;
                            }
                        }
                    }
                    break;
                case -2:
                    axesNames[0] = "j";
                    for (int k = 0, n = 0; k < dims[2]; k++) {
                        for (int j = 0; j < dims[1]; j++) {
                            for (int i = 0; i < dims[0]; i++,n++) {
                                coords[3*n] = xs*(float)j;
                            }
                        }
                    }
                    break;
                case -3:
                    axesNames[0] = "k";
                    for (int k = 0, n = 0; k < dims[2]; k++) {
                        for (int j = 0; j < dims[1]; j++) {
                            for (int i = 0; i < dims[0]; i++,n++) {
                                coords[3*n] = xs*(float)k;
                            }
                        }
                    }
                    break;
                case -100:
                    axesNames[0] = "";
                    for (int k = 0, n = 0; k < dims[2]; k++) {
                        for (int j = 0; j < dims[1]; j++) {
                            for (int i = 0; i < dims[0]; i++,n++) {
                                coords[3*n] = 0.0f;
                            }
                        }
                    }
                    break;
            }
        }

        if(yComp >= 0) {
            yc = inField.getData(yComp).getFData();
            axesNames[1] = inField.getData(yComp).getName();
            for (int i = 0; i < nData; i++) {
                coords[3*i+1] = ys*yc[i];
            }
        } else {
            switch(yComp) {
                case -1:
                    axesNames[1] = "i";
                    for (int k = 0, n = 0; k < dims[2]; k++) {
                        for (int j = 0; j < dims[1]; j++) {
                            for (int i = 0; i < dims[0]; i++,n++) {
                                coords[3*n+1] = ys*(float)i;
                            }
                        }
                    }
                    break;
                case -2:
                    axesNames[1] = "j";
                    for (int k = 0, n = 0; k < dims[2]; k++) {
                        for (int j = 0; j < dims[1]; j++) {
                            for (int i = 0; i < dims[0]; i++,n++) {
                                coords[3*n+1] = ys*(float)j;
                            }
                        }
                    }
                    break;
                case -3:
                    axesNames[1] = "k";
                    for (int k = 0, n = 0; k < dims[2]; k++) {
                        for (int j = 0; j < dims[1]; j++) {
                            for (int i = 0; i < dims[0]; i++,n++) {
                                coords[3*n+1] = ys*(float)k;
                            }
                        }
                    }
                    break;
                case -100:
                    axesNames[1] = "";
                    for (int k = 0, n = 0; k < dims[2]; k++) {
                        for (int j = 0; j < dims[1]; j++) {
                            for (int i = 0; i < dims[0]; i++,n++) {
                                coords[3*n+1] = 0.0f;
                            }
                        }
                    }
                    break;
            }
        }

        if(zComp >= 0) {
            zc = inField.getData(zComp).getFData();
            axesNames[2] = inField.getData(zComp).getName();
            for (int i = 0; i < nData; i++) {
                coords[3*i+2] = zs*zc[i];
            }
        } else {
            switch(zComp) {
                case -1:
                    axesNames[2] = "i";
                    for (int k = 0, n = 0; k < dims[2]; k++) {
                        for (int j = 0; j < dims[1]; j++) {
                            for (int i = 0; i < dims[0]; i++,n++) {
                                coords[3*n+2] = zs*(float)i;
                            }
                        }
                    }
                    break;
                case -2:
                    axesNames[2] = "j";
                    for (int k = 0, n = 0; k < dims[2]; k++) {
                        for (int j = 0; j < dims[1]; j++) {
                            for (int i = 0; i < dims[0]; i++,n++) {
                                coords[3*n+2] = zs*(float)j;
                            }
                        }
                    }
                    break;
                case -3:
                    axesNames[2] = "k";
                    for (int k = 0, n = 0; k < dims[2]; k++) {
                        for (int j = 0; j < dims[1]; j++) {
                            for (int i = 0; i < dims[0]; i++,n++) {
                                coords[3*n+2] = zs*(float)k;
                            }
                        }
                    }
                    break;
                case -100:
                    axesNames[2] = "";
                    for (int k = 0, n = 0; k < dims[2]; k++) {
                        for (int j = 0; j < dims[1]; j++) {
                            for (int i = 0; i < dims[0]; i++,n++) {
                                coords[3*n+2] = 0.0f;
                            }
                        }
                    }
                    break;
            }
        }
        outField.setNSpace(3);
        outField.setCoords(coords);
        outField.setAxesNames(axesNames);
    }
    
}
