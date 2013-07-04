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

package pl.edu.icm.visnow.lib.utils.fft;

import edu.emory.mathcs.jtransforms.fft.FloatFFT_1D;
import edu.emory.mathcs.jtransforms.fft.FloatFFT_2D;
import edu.emory.mathcs.jtransforms.fft.FloatFFT_3D;

/**
 * @author  Bartosz Borucki (babor@icm.edu.pl)
 * Warsaw University, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class FftJ1Core extends FftCore{
    
    private FloatFFT_1D fft1;
    private FloatFFT_2D fft2;
    private FloatFFT_3D fft3;
    
    /**
     * Creates a new <code>FftJ1Core</code> object.
     */
    public FftJ1Core() {
    }
    
    
    @Override
    protected void fft_r2c(float[] real, float[] imag) {
        int length = real.length;
        float[] a = new float[2*length];
        System.arraycopy(real, 0, a, 0, length);
        fft1 = null;
        fft1 = new FloatFFT_1D(length);
        fft1.realForwardFull(a);
        for(int i = 0; i < length; i++) {
            real[i] = a[2*i];
            imag[i] = a[2*i+1];
        }
    }
    
    @Override
    protected void fft_c2c(float[] real, float[] imag) {
        int length = real.length;
        float[] a = new float[2*length];
        for(int i = 0; i < length; i++) {
            a[2*i] = real[i];
            a[2*i+1] = imag[i];
        }
        fft1 = null;
        fft1 = new FloatFFT_1D(length);
        fft1.complexForward(a);
        for(int i = 0; i < length; i++) {
            real[i] = a[2*i];
            imag[i] = a[2*i+1];
        }
    }    
    
    @Override
    protected void ifft(float[] real, float[] imag) {
        int length = real.length;
        float[] a = new float[2*length];
        for(int i = 0; i < length; i++) {
            a[2*i] = real[i];
            a[2*i+1] = imag[i];
        }
        fft1 = null;
        fft1 = new FloatFFT_1D(length);
        fft1.complexInverse(a, true);
        for(int i = 0; i < length; i++) {
            real[i] = a[2*i];
            imag[i] = a[2*i+1];
        }
    }    

    @Override
    protected void fft2_r2c(float[] real, float[] imag, int nx, int ny) {
        int length = nx*ny;
        float[] a = new float[2*length];
        System.arraycopy(real, 0, a, 0, length);
        fft2 = null;
        fft2 = new FloatFFT_2D(nx, ny);
        fft2.realForwardFull(a);
        for(int i = 0; i < length; i++) {
            real[i] = a[2*i];
            imag[i] = a[2*i+1];
        }

    }
    
    @Override
    protected void fft2_c2c(float[] real, float[] imag, int nx, int ny) {
        int length = nx*ny;
        float[] a = new float[2*length];
        for(int i = 0; i < length; i++) {
            a[2*i] = real[i];
            a[2*i+1] = imag[i];
        }
        fft2 = null;
        fft2 = new FloatFFT_2D(nx,ny);
        fft2.complexForward(a);
        for(int i = 0; i < length; i++) {
            real[i] = a[2*i];
            imag[i] = a[2*i+1];
        }
    }    
    
    @Override
    protected void ifft2(float[] real, float[] imag, int nx, int ny) {
        int length = nx*ny;
        float[] a = new float[2*length];
        for(int i = 0; i < length; i++) {
            a[2*i] = real[i];
            a[2*i+1] = imag[i];
        }
        fft2 = null;
        fft2 = new FloatFFT_2D(nx, ny);
        fft2.complexInverse(a, true);
        for(int i = 0; i < length; i++) {
            real[i] = a[2*i];
            imag[i] = a[2*i+1];
        }
    }       
  
    @Override
    protected void fft3_r2c(float[] real, float[] imag, int nx, int ny, int nz) {
        int length = nx*ny*nz;
        float[] a = new float[2*length];
        System.arraycopy(real, 0, a, 0, length);
        fft3 = null;
        fft3 = new FloatFFT_3D(nx, ny, nz);
        fft3.realForwardFull(a);
        for(int i = 0; i < length; i++) {
            real[i] = a[2*i];
            imag[i] = a[2*i+1];
        }
        
    }
      
    @Override
    protected void fft3_c2c(float[] real, float[] imag, int nx, int ny, int nz) {
        int length = nx*ny*nz;
        float[] a = new float[2*length];
        for(int i = 0; i < length; i++) {
            a[2*i] = real[i];
            a[2*i+1] = imag[i];
        }
        fft3 = null;
        fft3 = new FloatFFT_3D(nx,ny,nz);
        fft3.complexForward(a);
        for(int i = 0; i < length; i++) {
            real[i] = a[2*i];
            imag[i] = a[2*i+1];
        }
    }

    @Override
    protected void ifft3(float[] real, float[] imag, int nx, int ny, int nz) {
        int length = nx*ny*nz;
        float[] a = new float[2*length];
        for(int i = 0; i < length; i++) {
            a[2*i] = real[i];
            a[2*i+1] = imag[i];
        }
        fft3 = null;
        fft3 = new FloatFFT_3D(nx, ny, nz);
        fft3.complexInverse(a, true);
        for(int i = 0; i < length; i++) {
            real[i] = a[2*i];
            imag[i] = a[2*i+1];
        }
    }


    
    
}    
    
    
