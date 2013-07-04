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

import pl.edu.icm.visnow.datasets.dataarrays.ComplexDataArray;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.system.main.VisNow;

/**
 * @author  Bartosz Borucki (babor@icm.edu.pl)
 * Warsaw University, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public abstract class FftCore {
    protected float[] outReal = null;
    protected float[] outImag = null;
    protected int[] dims;
    
    protected boolean working = false;
    protected boolean done = false;
    protected boolean complexInput = true;
    
    
    /**
     * Creates a new <code>FftCore</code> object.
     */
    public FftCore() {
    }
    
    protected abstract void fft_r2c(float[] real, float[] imag);    
    protected abstract void fft_c2c(float[] real, float[] imag);
    protected abstract void ifft(float[] real, float[] imag);
    
    protected abstract void fft2_r2c(float[] real, float[] imag, int nx, int ny); 
    protected abstract void fft2_c2c(float[] real, float[] imag, int nx, int ny);
    protected abstract void ifft2(float[] real, float[] imag, int nx, int ny);

    protected abstract void fft3_r2c(float[] real, float[] imag, int nx, int ny, int nz); 
    protected abstract void fft3_c2c(float[] real, float[] imag, int nx, int ny, int nz);
    protected abstract void ifft3(float[] real, float[] imag, int nx, int ny, int nz);
    

    /**
     * Runs Fast Fourier Transform calculations on the previously set input data, with the loaded core
     */
    public void calculateFFT(boolean center_origin) {
        if(outReal == null || outImag == null) 
            return;
        working = true;
        if(dims.length == 1) {
            if(complexInput) {
                fft_c2c(outReal, outImag);
            } else {
                fft_r2c(outReal, outImag);
//                fft_c2c(outReal, outImag);
            }
        } else if (dims.length == 2) {
            if(complexInput) {
                fft2_c2c(outReal, outImag, dims[0], dims[1]);
            } else {
                fft2_r2c(outReal, outImag, dims[0], dims[1]);
//                fft2_c2c(outReal, outImag, dims[0], dims[1]);
            }
            if(center_origin) {
                outReal = circShift_2D(outReal);
                outImag = circShift_2D(outImag);
            }
        } else if( dims.length == 3) {
            if(complexInput) {
                fft3_c2c(outReal, outImag, dims[0], dims[1], dims[2]);
            } else {
                fft3_r2c(outReal, outImag, dims[0], dims[1], dims[2]);
//                fft3_c2c(outReal, outImag, dims[0], dims[1], dims[2]);
            }
            if(center_origin) {
                outReal = circShift_3D(outReal);
                outImag = circShift_3D(outImag);
            }
        }
        working = false;
        done = true;
    }    

    /**
     * Runs Inverse Fast Fourier Transform calculations on the previously set input data, with the loaded core
     */
    public void calculateIFFT(boolean center_origin) {
        if(outReal == null || outImag == null) 
            return;
        
        working = true;
        if(dims.length == 1) {
                ifft(outReal, outImag);
        } else if (dims.length == 2) {
            if(center_origin) {
                outReal = inverseCircShift_2D(outReal);
                outImag = inverseCircShift_2D(outImag);
            }
            ifft2(outReal, outImag, dims[0], dims[1]);
        } else if( dims.length == 3) {
            if(center_origin) {
                outReal = inverseCircShift_3D(outReal);
                outImag = inverseCircShift_3D(outImag);
            }
            ifft3(outReal, outImag, dims[0], dims[1], dims[2]);
        }
        working = false;
        done = true;
    }    
    
    /**
     * Sets 1D input data for FFT calculations. Dimension is calculated as data length.
     *      
     * @param	inData	Input data array.
     */
    public void setInput(DataArray inData) {        
        if(inData == null || inData.getVeclen() > 1)
            return;
        
        int[] dms = new int[1];
        dms[0] = inData.getNData();
        this.setInput(inData, dms);
    }
    
    /**
     * Sets input data for FFT calculations with given data dimensions.
     * 
     * @param	inData	Input data array.
     * @param	dims	Data dimensions.
     */
    public void setInput(DataArray inData, int[] dims) {
        this.done = false;
        this.outReal = null;
        this.outImag = null;
        this.dims = new int[dims.length];
        if(dims.length == 1) {
            this.dims[0] = dims[0];
        }
        else if(dims.length == 2) {
            this.dims[0] = dims[1];
            this.dims[1] = dims[0];
        }
        else  {
            this.dims[0] = dims[2];
            this.dims[1] = dims[1];
            this.dims[2] = dims[0];
        }

        if(inData == null || inData.getVeclen() > 1)
            return;
        
        if(inData.getType() == DataArray.FIELD_DATA_COMPLEX) {
            this.outReal = ((ComplexDataArray)inData).getFRealData().clone();
            this.outImag = ((ComplexDataArray)inData).getFImagData().clone();
            this.complexInput = true;
        } else {
            this.outReal = inData.getFData().clone();
            this.outImag = new float[inData.getFData().length];
            this.complexInput = false;
        }
    }

    /**
     * Returns real part of calculated output transform as a float table.
     * 
     * @return	A <code>float[]</code> object.
     */
    public float[] getRealOutput() {
        if(done)
            return outReal;
        return null;
    }   
    
    /**
     * Returns imaginary part of calculated output transform as a float table.
     * 
     * @return	A <code>float[]</code> object.
     */
    public float[] getImagOutput() {
        if(done)
            return outImag;
        return null;
    }   

    /**
     * Returns true if calculations are in progress, false otherwise.
     * 
     * @return	<code>boolean</code> value.
     */
    public boolean isWorking() {
        return working;
    }


    /**
     * Static procedure that loads FFT core library.
     * It tries to load the native FFTW library for the current architecture and OS.
     * If fails it loads the default Java FFT core.
     * 
     * @return	<code>FftCore</code> object - instance of ready FFT calculation core
     */
    public static FftCore loadFftLibrary() {
       if (VisNow.isNativeLibraryLoaded("fftw")) {
          return new FftwCore(); 
       }
       else {
            return new FftJ1Core();
       }
    }
    
    
      private float[] circShift_2D(float[] in) {
        int rows = dims[0];
        int cols = dims[1];
        int cr = (int) Math.round(rows / 2.0);
        int cc = (int) Math.round(cols / 2.0);
        float[] out = new float[rows*cols];
        for(int r = 0; r < rows - cr; r++) {
            for(int c = 0; c < cols - cc; c++) {
                out[r*cols+c] = in[(r+cr)*cols+(c+cc)];
            }
        }
        for(int r = 0; r < rows - cr; r++) {
            for(int c = 0; c < cc; c++) {
                out[r*cols+(cols-cc+c)] = in[(cr+r)*cols+c];
            }
        }
        for(int r = 0; r < cr; r++) {
            for(int c = 0; c < cols - cc; c++) {
                out[(rows-cr+r)*cols+c] = in[r*cols+(cc+c)];
            }
        }
        for(int r = 0; r < cr; r++) {
            for(int c = 0; c < cc; c++) {
                out[(rows-cr+r)*cols+(cols-cc+c)] = in[r*cols+c];
            }
        }
        return out;
    }
    
    private float[] inverseCircShift_2D(float[] in) {
        int rows = dims[0];
        int cols = dims[1];
        int cr = (int) Math.round(rows / 2.0);
        int cc = (int) Math.round(cols / 2.0);
        float[] out = new float[rows*cols];
        for(int r = 0; r < rows - cr; r++) {
            for(int c = 0; c < cols - cc; c++) {
                out[(r+cr)*cols+(c+cc)] = in[r*cols+c];
            }
        }
        for(int r = 0; r < rows - cr; r++) {
            for(int c = 0; c < cc; c++) {
                out[(cr+r)*cols+c] = in[r*cols+(cols-cc+c)];
            }
        }
        for(int r = 0; r < cr; r++) {
            for(int c = 0; c < cols - cc; c++) {
                out[r*cols+(cc+c)] = in[(rows-cr+r)*cols+c];
            }
        }
        for(int r = 0; r < cr; r++) {
            for(int c = 0; c < cc; c++) {
                out[r*cols+c] = in[(rows-cr+r)*cols+(cols-cc+c)];
            }
        }
        return out;
    }  
      
    private float[] circShift_3D(float[] in) {
        int slices = dims[0];
        int rows = dims[1];
        int cols = dims[2];
        int cs = (int) Math.round(slices / 2.0);
        int cr = (int) Math.round(rows / 2.0);
        int cc = (int) Math.round(cols / 2.0);
        int sstride = rows*cols;
        float[] out = new float[slices*rows*cols];
        for(int s = 0; s < slices - cs; s++) {
            for(int r = 0; r < rows - cr; r++) {
                for(int c = 0; c < cols - cc; c++) {
                    out[s*sstride+r*cols+c] = in[(s+cs)*sstride+(r+cr)*cols+(c+cc)];
                }
            }
        }
        for(int s = 0; s < slices - cs; s++) {
            for(int r = 0; r < cr; r++) {
                for(int c = 0; c < cols - cc; c++) {
                    out[s*sstride+(rows-cr+r)*cols+c] = in[(s+cs)*sstride+r*cols+(c+cc)];
                }
            }
        }

       for(int s = 0; s < slices - cs; s++) {
            for(int r = 0; r < rows - cr; r++) {
                for(int c = 0; c < cc; c++) {
                    out[s*sstride+r*cols+(cols-cc+c)] = in[(s+cs)*sstride+(r+cr)*cols+c];
                }
            }
        }

       for(int s = 0; s < slices - cs; s++) {
            for(int r = 0; r < cr; r++) {
                for(int c = 0; c < cc; c++) {
                    out[s*sstride+(rows-cr+r)*cols+(cols-cc+c)] = in[(s+cs)*sstride+r*cols+c];
                }
            }
        }

       for(int s = 0; s < cs; s++) {
            for(int r = 0; r < rows - cr; r++) {
                for(int c = 0; c < cols - cc; c++) {
                    out[(slices-cs+s)*sstride+r*cols+c] = in[(s+cs)*sstride+(r+cr)*cols+c+cc];
                }
            }
        }

       for(int s = 0; s < cs; s++) {
            for(int r = 0; r < rows - cr; r++) {
                for(int c = 0; c < cc; c++) {
                    out[(slices-cs+s)*sstride+r*cols+(cols-cc+c)] = in[s*sstride+(r+cr)*cols+c];
                }
            }
        }

       for(int s = 0; s < cs; s++) {
            for(int r = 0; r < cr; r++) {
                for(int c = 0; c < cols - cc; c++) {
                    out[(slices-cs+s)*sstride+(rows-cr+r)*cols+c] = in[s*sstride+r*cols+c+cc];
                }
            }
        }

       for(int s = 0; s < cs; s++) {
            for(int r = 0; r < cr; r++) {
                for(int c = 0; c < cc; c++) {
                    out[(slices-cs+s)*sstride+(rows-cr+r)*cols+(cols-cc+c)] = in[s*sstride+r*cols+c];
                }
            }
        }

       return out;
    }
    
    private float[] inverseCircShift_3D(float[] in) {
        int slices = dims[0];
        int rows = dims[1];
        int cols = dims[2];
        int cs = (int) Math.round(slices / 2.0);
        int cr = (int) Math.round(rows / 2.0);
        int cc = (int) Math.round(cols / 2.0);
        int sstride = rows*cols;
        float[] out = new float[slices*rows*cols];
        for(int s = 0; s < slices - cs; s++) {
            for(int r = 0; r < rows - cr; r++) {
                for(int c = 0; c < cols - cc; c++) {
                    out[(s+cs)*sstride+(r+cr)*cols+(c+cc)] = in[s*sstride+r*cols+c];
                }
            }
        }
        for(int s = 0; s < slices - cs; s++) {
            for(int r = 0; r < cr; r++) {
                for(int c = 0; c < cols - cc; c++) {
                    out[(s+cs)*sstride+r*cols+(c+cc)] = in[s*sstride+(rows-cr+r)*cols+c];
                }
            }
        }

       for(int s = 0; s < slices - cs; s++) {
            for(int r = 0; r < rows - cr; r++) {
                for(int c = 0; c < cc; c++) {
                    out[(s+cs)*sstride+(r+cr)*cols+c] = in[s*sstride+r*cols+(cols-cc+c)];
                }
            }
        }

       for(int s = 0; s < slices - cs; s++) {
            for(int r = 0; r < cr; r++) {
                for(int c = 0; c < cc; c++) {
                    out[(s+cs)*sstride+r*cols+c] = in[s*sstride+(rows-cr+r)*cols+(cols-cc+c)];
                }
            }
        }

       for(int s = 0; s < cs; s++) {
            for(int r = 0; r < rows - cr; r++) {
                for(int c = 0; c < cols - cc; c++) {
                    out[(s+cs)*sstride+(r+cr)*cols+c+cc] = in[(slices-cs+s)*sstride+r*cols+c];
                }
            }
        }

       for(int s = 0; s < cs; s++) {
            for(int r = 0; r < rows - cr; r++) {
                for(int c = 0; c < cc; c++) {
                    out[s*sstride+(r+cr)*cols+c] = in[(slices-cs+s)*sstride+r*cols+(cols-cc+c)];
                }
            }
        }

       for(int s = 0; s < cs; s++) {
            for(int r = 0; r < cr; r++) {
                for(int c = 0; c < cols - cc; c++) {
                    out[s*sstride+r*cols+c+cc] = in[(slices-cs+s)*sstride+(rows-cr+r)*cols+c];
                }
            }
        }

       for(int s = 0; s < cs; s++) {
            for(int r = 0; r < cr; r++) {
                for(int c = 0; c < cc; c++) {
                    out[s*sstride+r*cols+c] = in[(slices-cs+s)*sstride+(rows-cr+r)*cols+(cols-cc+c)];
                }
            }
        }

       return out;
    }
}    
    
    
