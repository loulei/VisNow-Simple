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

import java.util.ArrayList;
import pl.edu.icm.visnow.datasets.dataarrays.ComplexDataArray;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.datasets.dataarrays.FloatDataArray;
import pl.edu.icm.visnow.system.main.VisNow;

/**
 * @author Piotr Wendykier (piotrw@icm.edu.pl) 
 * @author Bartosz Borucki (babor@icm.edu.pl) Warsaw University,
 * Interdisciplinary Centre for Mathematical and Computational Modelling
 */
public abstract class FftCore {

    protected float[][] outReal = null;
    protected float[][] outImag = null;
    protected ArrayList<Float> timeSeries = null;
    protected int[] dims;
    protected boolean working = false;
    protected boolean done = false;
    protected boolean complexInput = true;

    /**
     * Creates a new
     * <code>FftCore</code> object.
     */
    public FftCore() {
    }

    public abstract void fft_r2c(float[] real, float[] imag);

    public abstract void fft_c2c(float[] real, float[] imag);

    public abstract void ifft(float[] real, float[] imag);

    public abstract void fft2_r2c(float[] real, float[] imag, int nx, int ny);

    public abstract void fft2_c2c(float[] real, float[] imag, int nx, int ny);

    public abstract void ifft2(float[] real, float[] imag, int nx, int ny);

    public abstract void fft3_r2c(float[] real, float[] imag, int nx, int ny, int nz);

    public abstract void fft3_c2c(float[] real, float[] imag, int nx, int ny, int nz);

    public abstract void ifft3(float[] real, float[] imag, int nx, int ny, int nz);

    /**
     * Runs Fast Fourier Transform calculations on the previously set input
     * data, with the loaded core
     */
    public void calculateFFT(boolean center_origin) {
        if (outReal == null || outImag == null) {
            return;
        }
        working = true;
        if (dims.length == 1) {
            if (complexInput) {
                for(int i = 0; i < outReal.length; i++) {
                    fft_c2c(outReal[i], outImag[i]);
                }
            } else {
                for(int i = 0; i < outReal.length; i++) {
                    fft_r2c(outReal[i], outImag[i]);
                }
            }
        } else if (dims.length == 2) {
            if (complexInput) {
                for(int i = 0; i < outReal.length; i++) {
                    fft2_c2c(outReal[i], outImag[i], dims[0], dims[1]);
                }
            } else {
                for(int i = 0; i < outReal.length; i++) {
                    fft2_r2c(outReal[i], outImag[i], dims[0], dims[1]);
                }
            }
            if (center_origin) {
                for(int i = 0; i < outReal.length; i++) {
                    outReal[i] = circShift_2D(outReal[i], dims[0], dims[1]);
                    outImag[i] = circShift_2D(outImag[i], dims[0], dims[1]);
                }
            }
        } else if (dims.length == 3) {
            if (complexInput) {
                for(int i = 0; i < outReal.length; i++) {
                    fft3_c2c(outReal[i], outImag[i], dims[0], dims[1], dims[2]);
                }
            } else {
                for(int i = 0; i < outReal.length; i++) {
                    fft3_r2c(outReal[i], outImag[i], dims[0], dims[1], dims[2]);
                }
            }
            if (center_origin) {
                for(int i = 0; i < outReal.length; i++) {
                    outReal[i] = circShift_3D(outReal[i], dims[0], dims[1], dims[2]);
                    outImag[i] = circShift_3D(outImag[i], dims[0], dims[1], dims[2]);
                }
            }
        }
        working = false;
        done = true;
    }

    /**
     * Runs Inverse Fast Fourier Transform calculations on the previously set
     * input data, with the loaded core
     */
    public void calculateIFFT(boolean center_origin) {
        if (outReal == null || outImag == null) {
            return;
        }

        working = true;
        if (dims.length == 1) {
            for(int i = 0; i < outReal.length; i++) {
                ifft(outReal[i], outImag[i]);
            }
        } else if (dims.length == 2) {
            if (center_origin) {
                for(int i = 0; i < outReal.length; i++) {
                    outReal[i] = inverseCircShift_2D(outReal[i], dims[0], dims[1]);
                    outImag[i] = inverseCircShift_2D(outImag[i], dims[0], dims[1]);
                }
            }
            for(int i = 0; i < outReal.length; i++) {
                ifft2(outReal[i], outImag[i], dims[0], dims[1]);
            }
        } else if (dims.length == 3) {
            if (center_origin) {
                for(int i = 0; i < outReal.length; i++) {
                    outReal[i] = inverseCircShift_3D(outReal[i], dims[0], dims[1], dims[2]);
                    outImag[i] = inverseCircShift_3D(outImag[i], dims[0], dims[1], dims[2]);
                }
            }
            for(int i = 0; i < outReal.length; i++) {
                ifft3(outReal[i], outImag[i], dims[0], dims[1], dims[2]);
            }
        }
        working = false;
        done = true;
    }

    /**
     * Sets 1D input data for FFT calculations. Dimension is calculated as data
     * length.
     *
     * @param	inData	Input data array.
     */
    public void setInput(DataArray inData) {
        if (inData == null || inData.getVeclen() > 1) {
            return;
        }

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
        if (dims.length == 1) {
            this.dims[0] = dims[0];
        } else if (dims.length == 2) {
            this.dims[0] = dims[1];
            this.dims[1] = dims[0];
        } else {
            this.dims[0] = dims[2];
            this.dims[1] = dims[1];
            this.dims[2] = dims[0];
        }

        if (inData == null || inData.getVeclen() > 1) {
            return;
        }

        int frames = inData.getNFrames();
        this.outReal = new float[frames][];
        this.outImag = new float[frames][];
        if(frames == 1) {
            if (inData.getType() == DataArray.FIELD_DATA_COMPLEX) {
                this.outReal[0] = ((ComplexDataArray) inData).getFRealData().clone();
                this.outImag[0] = ((ComplexDataArray) inData).getFImagData().clone();
                this.complexInput = true;
            } else {
                this.outReal[0] = inData.getFData().clone();
                this.outImag[0] = new float[inData.getFData().length];
                this.complexInput = false;
            }
        }
        else {
            timeSeries = inData.getTimeSeries();
            int i = 0;
            float currentTime = inData.getCurrentTime();
            for (Float t : timeSeries) {
                inData.setCurrentTime(t);
                if (inData.getType() == DataArray.FIELD_DATA_COMPLEX) {
                    this.outReal[i] = ((ComplexDataArray) inData).getFRealData().clone();
                    this.outImag[i] = ((ComplexDataArray) inData).getFImagData().clone();
                    this.complexInput = true;
                } else {
                    this.outReal[i] = inData.getFData().clone();
                    this.outImag[i] = new float[inData.getFData().length];
                    this.complexInput = false;
                }
                i++;
            }
            inData.setCurrentTime(currentTime);
        }
        
        
        
    }

    /**
     * Returns real part of calculated output transform as a DataArray.
     *
     * @return	A <code>DataArray</code> object.
     */
    public DataArray getRealOutput() {
        if (done) {
            if(outReal.length == 1) {
                return new FloatDataArray(outReal[0], 1, "");
            }
            else {
                FloatDataArray res = new FloatDataArray(outReal[0].length, 1);
                int i = 0;
                for (Float t : timeSeries) {
                     res.addData(outReal[i++], t);
                }
                res.setCurrentTime(timeSeries.get(0));
                return res;
            }
        }
        return null;
    }

    /**
     * Returns imaginary part of calculated output transform as a DataArray.
     *
     * @return	A <code>DataArray</code> object.
     */
    public DataArray getImagOutput() {
        if (done) {
            if(outImag.length == 1) {
                return new FloatDataArray(outImag[0], 1, "");
            }
            else {
                FloatDataArray res = new FloatDataArray(outImag[0].length, 1);
                int i = 0;
                for (Float t : timeSeries) {
                     res.addData(outImag[i++], t);
                }
                res.setCurrentTime(timeSeries.get(0));
                return res;
            }
        }
        return null;
    }

    /**
     * Returns calculated output transform as a DataArray.
     *
     * @return	A <code>DataArray</code> object.
     */
    public DataArray getOutput() {
        if (done) {
            if(outImag.length == 1) {
                return new ComplexDataArray(outReal[0], outImag[0], 1, "");
            }
            else {
                ComplexDataArray res = new ComplexDataArray(outImag[0].length, 1);
                int i = 0;
                float[][] tmp = new float[2][];
                for (Float t : timeSeries) {
                    tmp[0] =  outReal[i];
                    tmp[1] =  outImag[i];
                    res.addData(tmp, t);
                    i++;
                }
                res.setCurrentTime(timeSeries.get(0));
                return res;
            }
        }
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
     * Static procedure that loads FFT core library. It tries to load the native
     * FFTW library for the current architecture and OS. If fails it loads the
     * default Java FFT core.
     *
     * @return	<code>FftCore</code> object - instance of ready FFT calculation
     * core
     */
    public static FftCore loadFftLibrary() {
        if (VisNow.isNativeLibraryLoaded("fftw")) {
            return new FftwCore();
        } else {
            return new FftJ1Core();
        }
    }

    public static float[] circShift_1D(float[] in) {
        int cc = (int) Math.floor(in.length / 2.0);
        float[] out = new float[in.length];
        for (int c = 0; c < in.length - cc; c++) {
            out[c] = in[c + cc];
        }
        for (int c = 0; c < cc; c++) {
            out[in.length - cc + c] = in[c];
        }
        return out;
    }

    public static float[] inverseCircShift_1D(float[] in) {
        int cc = (int) Math.floor(in.length / 2.0);
        float[] out = new float[in.length];
        for (int c = 0; c < in.length - cc; c++) {
            out[c + cc] = in[c];
        }
        for (int c = 0; c < cc; c++) {
            out[c] = in[in.length - cc + c];
        }
        return out;
    }

    public static float[] circShift_2D(float[] in, int rows, int cols) {
        int cr = (int) Math.floor(rows / 2.0);
        int cc = (int) Math.floor(cols / 2.0);
        float[] out = new float[rows * cols];
        for (int r = 0; r < rows - cr; r++) {
            for (int c = 0; c < cols - cc; c++) {
                out[r * cols + c] = in[(r + cr) * cols + (c + cc)];
            }
        }
        for (int r = 0; r < rows - cr; r++) {
            for (int c = 0; c < cc; c++) {
                out[r * cols + (cols - cc + c)] = in[(cr + r) * cols + c];
            }
        }
        for (int r = 0; r < cr; r++) {
            for (int c = 0; c < cols - cc; c++) {
                out[(rows - cr + r) * cols + c] = in[r * cols + (cc + c)];
            }
        }
        for (int r = 0; r < cr; r++) {
            for (int c = 0; c < cc; c++) {
                out[(rows - cr + r) * cols + (cols - cc + c)] = in[r * cols + c];
            }
        }
        return out;
    }

    public static float[] inverseCircShift_2D(float[] in, int rows, int cols) {
        int cr = (int) Math.floor(rows / 2.0);
        int cc = (int) Math.floor(cols / 2.0);
        float[] out = new float[rows * cols];
        for (int r = 0; r < rows - cr; r++) {
            for (int c = 0; c < cols - cc; c++) {
                out[(r + cr) * cols + (c + cc)] = in[r * cols + c];
            }
        }
        for (int r = 0; r < rows - cr; r++) {
            for (int c = 0; c < cc; c++) {
                out[(cr + r) * cols + c] = in[r * cols + (cols - cc + c)];
            }
        }
        for (int r = 0; r < cr; r++) {
            for (int c = 0; c < cols - cc; c++) {
                out[r * cols + (cc + c)] = in[(rows - cr + r) * cols + c];
            }
        }
        for (int r = 0; r < cr; r++) {
            for (int c = 0; c < cc; c++) {
                out[r * cols + c] = in[(rows - cr + r) * cols + (cols - cc + c)];
            }
        }
        return out;
    }

    public static float[] circShift_3D(float[] in, int slices, int rows, int cols) {
        int cs = (int) Math.floor(slices / 2.0);
        int cr = (int) Math.floor(rows / 2.0);
        int cc = (int) Math.floor(cols / 2.0);
        int sstride = rows * cols;
        float[] out = new float[slices * rows * cols];
        for (int s = 0; s < slices - cs; s++) {
            for (int r = 0; r < rows - cr; r++) {
                for (int c = 0; c < cols - cc; c++) {
                    out[s * sstride + r * cols + c] = in[(s + cs) * sstride + (r + cr) * cols + (c + cc)];
                }
            }
        }
        for (int s = 0; s < slices - cs; s++) {
            for (int r = 0; r < cr; r++) {
                for (int c = 0; c < cols - cc; c++) {
                    out[s * sstride + (rows - cr + r) * cols + c] = in[(s + cs) * sstride + r * cols + (c + cc)];
                }
            }
        }

        for (int s = 0; s < slices - cs; s++) {
            for (int r = 0; r < rows - cr; r++) {
                for (int c = 0; c < cc; c++) {
                    out[s * sstride + r * cols + (cols - cc + c)] = in[(s + cs) * sstride + (r + cr) * cols + c];
                }
            }
        }

        for (int s = 0; s < slices - cs; s++) {
            for (int r = 0; r < cr; r++) {
                for (int c = 0; c < cc; c++) {
                    out[s * sstride + (rows - cr + r) * cols + (cols - cc + c)] = in[(s + cs) * sstride + r * cols + c];
                }
            }
        }

        for (int s = 0; s < cs; s++) {
            for (int r = 0; r < rows - cr; r++) {
                for (int c = 0; c < cols - cc; c++) {
                    out[(slices - cs + s) * sstride + r * cols + c] = in[s * sstride + (r + cr) * cols + c + cc];
                }
            }
        }

        for (int s = 0; s < cs; s++) {
            for (int r = 0; r < rows - cr; r++) {
                for (int c = 0; c < cc; c++) {
                    out[(slices - cs + s) * sstride + r * cols + (cols - cc + c)] = in[s * sstride + (r + cr) * cols + c];
                }
            }
        }

        for (int s = 0; s < cs; s++) {
            for (int r = 0; r < cr; r++) {
                for (int c = 0; c < cols - cc; c++) {
                    out[(slices - cs + s) * sstride + (rows - cr + r) * cols + c] = in[s * sstride + r * cols + c + cc];
                }
            }
        }

        for (int s = 0; s < cs; s++) {
            for (int r = 0; r < cr; r++) {
                for (int c = 0; c < cc; c++) {
                    out[(slices - cs + s) * sstride + (rows - cr + r) * cols + (cols - cc + c)] = in[s * sstride + r * cols + c];
                }
            }
        }

        return out;
    }

    public static float[] inverseCircShift_3D(float[] in, int slices, int rows, int cols) {
        int cs = (int) Math.floor(slices / 2.0);
        int cr = (int) Math.floor(rows / 2.0);
        int cc = (int) Math.floor(cols / 2.0);
        int sstride = rows * cols;
        float[] out = new float[slices * rows * cols];
        for (int s = 0; s < slices - cs; s++) {
            for (int r = 0; r < rows - cr; r++) {
                for (int c = 0; c < cols - cc; c++) {
                    out[(s + cs) * sstride + (r + cr) * cols + (c + cc)] = in[s * sstride + r * cols + c];
                }
            }
        }
        for (int s = 0; s < slices - cs; s++) {
            for (int r = 0; r < cr; r++) {
                for (int c = 0; c < cols - cc; c++) {
                    out[(s + cs) * sstride + r * cols + (c + cc)] = in[s * sstride + (rows - cr + r) * cols + c];
                }
            }
        }

        for (int s = 0; s < slices - cs; s++) {
            for (int r = 0; r < rows - cr; r++) {
                for (int c = 0; c < cc; c++) {
                    out[(s + cs) * sstride + (r + cr) * cols + c] = in[s * sstride + r * cols + (cols - cc + c)];
                }
            }
        }

        for (int s = 0; s < slices - cs; s++) {
            for (int r = 0; r < cr; r++) {
                for (int c = 0; c < cc; c++) {
                    out[(s + cs) * sstride + r * cols + c] = in[s * sstride + (rows - cr + r) * cols + (cols - cc + c)];
                }
            }
        }

        for (int s = 0; s < cs; s++) {
            for (int r = 0; r < rows - cr; r++) {
                for (int c = 0; c < cols - cc; c++) {
                    out[s * sstride + (r + cr) * cols + c + cc] = in[(slices - cs + s) * sstride + r * cols + c];
                }
            }
        }

        for (int s = 0; s < cs; s++) {
            for (int r = 0; r < rows - cr; r++) {
                for (int c = 0; c < cc; c++) {
                    out[s * sstride + (r + cr) * cols + c] = in[(slices - cs + s) * sstride + r * cols + (cols - cc + c)];
                }
            }
        }

        for (int s = 0; s < cs; s++) {
            for (int r = 0; r < cr; r++) {
                for (int c = 0; c < cols - cc; c++) {
                    out[s * sstride + r * cols + c + cc] = in[(slices - cs + s) * sstride + (rows - cr + r) * cols + c];
                }
            }
        }

        for (int s = 0; s < cs; s++) {
            for (int r = 0; r < cr; r++) {
                for (int c = 0; c < cc; c++) {
                    out[s * sstride + r * cols + c] = in[(slices - cs + s) * sstride + (rows - cr + r) * cols + (cols - cc + c)];
                }
            }
        }

        return out;
    }
}
