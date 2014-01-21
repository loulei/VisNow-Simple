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

package pl.edu.icm.visnow.lib.basic.filters.FFT;

import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.lib.utils.fft.FftCore;

/**
 * @author  Bartosz Borucki (babor@icm.edu.pl)
 * Warsaw University, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class Core {

    private Params params;
    private RegularField inField = null;
    private RegularField outField = null;
    private FftCore fourier;

    public Core() {
        fourier = FftCore.loadFftLibrary();
    }

    public void setParams(Params params) {
        this.params = params;
    }

    public void update() {
        
        if (inField == null) {
            outField = null;
            return;
        }

        outField = new RegularField(inField.getDims());
        if (inField.getCoords() == null) {
            outField.setAffine(inField.getAffine());
        } else {
            outField.setCoords(inField.getCoords());
        }

        for (int i = 0; i < inField.getNData(); i++) {
            if (inField.getData(i).getVeclen() > 1) {
                continue;
            }

            fourier.setInput(inField.getData(i), inField.getDims());
            
            if(params.getDirection() == Params.DIRECTION_FORWARD) {
                fourier.calculateFFT(params.getOrigin() == Params.ORIGIN_CENTER);
                DataArray fft = fourier.getOutput();
                fft.setName("FT_" + inField.getData(i).getName());
                outField.addData(fft);
            }
            else {
                fourier.calculateIFFT(params.getOrigin() == Params.ORIGIN_CENTER);
                DataArray ifft = fourier.getOutput();
                ifft.setName("IFT_" + inField.getData(i).getName());
                outField.addData(ifft);               
            }
            
        }
    }

    public void setInField(RegularField field) {
        this.inField = field;
    }
    
    /**
     * @return the outField
     */
    public RegularField getOutField() {
        return outField;
    }
}
