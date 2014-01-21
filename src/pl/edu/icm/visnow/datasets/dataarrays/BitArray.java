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

package pl.edu.icm.visnow.datasets.dataarrays;

/**
 * @author  Bartosz Borucki (babor@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */

public class BitArray {
    private byte[] rawData;
    private int size;

    //----------------------
    // constructors
    //----------------------
    public BitArray(int size) {
        this(size, false);
    }

    public BitArray(boolean[] values) {
        this(values.length, false);
        for (int i = 0; i < values.length; i++) {
            this.setValueAtIndex(i, values[i]);            
        }
    }

    public BitArray(byte[] values) {
        this(values.length, false);
        for (int i = 0; i < values.length; i++) {
            this.setValueAtIndex(i, (int)(0xFF & values[i]) > 0);
        }
    }

    public BitArray(int[] values) {
        this(values.length, false);
        for (int i = 0; i < values.length; i++) {
            this.setValueAtIndex(i, values[i] > 0);
        }
    }

    public BitArray(int size, boolean initValue) {
        this.size = size;
        int tmp = (size-1)/8;
        int dataSize = tmp + 1;
        rawData = new byte[dataSize];
        for (int i = 0; i < rawData.length; i++) {
            rawData[i] = (byte)(initValue ? 1 : 0);
        }
    }

    private BitArray(int size, byte[] rawData) {
        this.size = size;
        this.rawData = rawData;
    }

    public BitArray(BitArray array) {
        this.size  = array.size();
        byte[] in = array.getRawData();
        this.rawData = new byte[in.length];
        for (int i = 0; i < in.length; i++) {
            this.rawData[i] = in[i];
        }
    }


    //----------------------
    // getters/setters
    //----------------------
    public int size() {
        return size;
    }

    private byte[] getRawData() {
        return rawData;
    }

    public boolean getValueAtIndex(int i) {
        if(i < 0 || i >= size)
            throw new IndexOutOfBoundsException();

        int index = i/8;
        byte v = rawData[index];
        int ii = i%8;
        int value = v>>(8-(ii+1)) & 0x0001;
        return value == 1;
    }

    public byte getByteValueAtIndex(int i) {
        if(i < 0 || i >= size)
            throw new IndexOutOfBoundsException();

        int index = i/8;
        byte v = rawData[index];
        int ii = i%8;
        int value = v>>(8-(ii+1)) & 0x0001;
        return (byte)value;
    }

    public int getIntValueAtIndex(int i) {
        if(i < 0 || i >= size)
            throw new IndexOutOfBoundsException();

        int index = i/8;
        byte v = rawData[index];
        int ii = i%8;
        return (v>>(8-(ii+1)) & 0x0001);
    }

    public void setValueAtIndex(int i, boolean value) {
        if(i < 0 || i >= size)
            throw new IndexOutOfBoundsException();

        int v= 0;
        if(value)
            v = 1;

        int index = i/8;
        int ii = i%8;
        byte oldV = rawData[index];
        oldV = (byte) (((0xFF7F>>ii) & oldV) & 0x00FF);
        byte newV = (byte) ((v<<(8-(ii+1))) | oldV);
        rawData[index] = newV;
    }

    public void setByteValueAtIndex(int i, byte value) {
        if(i < 0 || i >= size)
            throw new IndexOutOfBoundsException();

        if(value < 0 || value > 1)
            throw new IllegalArgumentException();

        int v = (int)(value & 0xFF);
        int index = i/8;
        int ii = i%8;
        byte oldV = rawData[index];
        oldV = (byte) (((0xFF7F>>ii) & oldV) & 0x00FF);
        byte newV = (byte) ((v<<(8-(ii+1))) | oldV);
        rawData[index] = newV;
    }

    public void setIntValueAtIndex(int i, int value) {
        if(i < 0 || i >= size)
            throw new IndexOutOfBoundsException();

        if(value < 0 || value > 1)
            throw new IllegalArgumentException();

        int index = i/8;
        int ii = i%8;
        byte oldV = rawData[index];
        oldV = (byte) (((0xFF7F>>ii) & oldV) & 0x00FF);
        byte newV = (byte) ((value<<(8-(ii+1))) | oldV);
        rawData[index] = newV;
    }



    //----------------------
    // output
    //----------------------
    public String toBinaryString() {
        String out = "";
        String tmp;
        for (int i = 0; i < rawData.length; i++) {
            tmp =  Integer.toBinaryString((int)(rawData[i]&0xFF));
            while(tmp.length() < 8) {
                tmp = "0"+tmp;
            }
            out+=tmp;
        }
        return out.substring(0, size);
    }



    //----------------------
    // logic
    //----------------------
    public BitArray and(BitArray array) {
        if(array == null || array.size() != size)
            return null;

        byte[] in = array.getRawData();
        byte[] out = new byte[in.length];
        for (int i = 0; i < rawData.length; i++) {
            out[i] = (byte)(rawData[i] & in[i]);
        }
        return new BitArray(size, out);
    }

    public BitArray or(BitArray array) {
        if(array == null || array.size() != size)
            return null;

        byte[] in = array.getRawData();
        byte[] out = new byte[in.length];
        for (int i = 0; i < rawData.length; i++) {
            out[i] = (byte)(rawData[i] | in[i]);
        }
        return new BitArray(size, out);
    }

    public BitArray xor(BitArray array) {
        if(array == null || array.size() != size)
            return null;

        byte[] in = array.getRawData();
        byte[] out = new byte[in.length];
        for (int i = 0; i < rawData.length; i++) {
            out[i] = (byte)(rawData[i] ^ in[i]);
        }
        return new BitArray(size, out);
    }

    public BitArray not() {
        byte[] out = new byte[rawData.length];
        for (int i = 0; i < rawData.length; i++) {
            out[i] = (byte)(~rawData[i]);
        }
        return new BitArray(size, out);
    }

    public boolean isDataEqual(BitArray array) {
        if(array == null)
            return false;

        if(array.size() != size)
            return false;

        byte[] in = array.getRawData();
        for (int i = 0; i < rawData.length; i++) {
            if(rawData[i] != in[i])
                return false;
        }
        return true;
    }



    //----------------------
    // cast to basic type array methods
    //----------------------
    public byte[] getByteArray() {
        byte[] out = new byte[size];
        byte v;
        int ii;
        for (int i = 0; i < out.length; i++) {
            v = rawData[i/8];
            ii = i%8;
            out[i] = (byte)(v>>(8-(ii+1)) & 0x0001);
        }
        return out;
    }

    public byte[] getByteSubArray(int offset, int length) {
        if(offset < 0 || offset >= size || length < 1 || offset+length > size)
            return null;
        
        byte[] out = new byte[length];
        byte v;
        int ii;
        for (int i = offset, c = 0; i < offset+length; i++,c++) {
            v = rawData[i/8];
            ii = i%8;
            out[c] = (byte)(v>>(8-(ii+1)) & 0x0001);
        }
        return out;
    }

    public short[] getShortArray() {
        short[] out = new short[size];
        byte v;
        int ii;
        for (int i = 0; i < out.length; i++) {
            v = rawData[i/8];
            ii = i%8;
            out[i] = (short)(v>>(8-(ii+1)) & 0x0001);
        }
        return out;
    }

    public short[] getShortSubArray(int offset, int length) {
        if(offset < 0 || offset >= size || length < 1 || offset+length > size)
            return null;

        short[] out = new short[length];
        byte v;
        int ii;
        for (int i = offset, c = 0; i < offset+length; i++,c++) {
            v = rawData[i/8];
            ii = i%8;
            out[c] = (short)(v>>(8-(ii+1)) & 0x0001);
        }
        return out;
    }

    public int[] getIntArray() {
        int[] out = new int[size];
        byte v;
        int ii;
        for (int i = 0; i < out.length; i++) {
            v = rawData[i/8];
            ii = i%8;
            out[i] = v>>(8-(ii+1)) & 0x0001;
        }
        return out;
    }

    public int[] getIntSubArray(int offset, int length) {
        if(offset < 0 || offset >= size || length < 1 || offset+length > size)
            return null;

        int[] out = new int[length];
        byte v;
        int ii;
        for (int i = offset, c = 0; i < offset+length; i++,c++) {
            v = rawData[i/8];
            ii = i%8;
            out[c] = v>>(8-(ii+1)) & 0x0001;
        }
        return out;
    }

    public float[] getFloatArray() {
        float[] out = new float[size];
        byte v;
        int ii;
        for (int i = 0; i < out.length; i++) {
            v = rawData[i/8];
            ii = i%8;
            out[i] = v>>(8-(ii+1)) & 0x0001;
        }
        return out;
    }

    public float[] getFloatSubArray(int offset, int length) {
        if(offset < 0 || offset >= size || length < 1 || offset+length > size)
            return null;

        float[] out = new float[length];
        byte v;
        int ii;
        for (int i = offset, c = 0; i < offset+length; i++,c++) {
            v = rawData[i/8];
            ii = i%8;
            out[c] = v>>(8-(ii+1)) & 0x0001;
        }
        return out;
    }

    public double[] getDoubleArray() {
        double[] out = new double[size];
        byte v;
        int ii;
        for (int i = 0; i < out.length; i++) {
            v = rawData[i/8];
            ii = i%8;
            out[i] = v>>(8-(ii+1)) & 0x0001;
        }
        return out;
    }

    public double[] getDoubleSubArray(int offset, int length) {
        if(offset < 0 || offset >= size || length < 1 || offset+length > size)
            return null;

        double[] out = new double[length];
        byte v;
        int ii;
        for (int i = offset, c = 0; i < offset+length; i++,c++) {
            v = rawData[i/8];
            ii = i%8;
            out[c] = v>>(8-(ii+1)) & 0x0001;
        }
        return out;
    }



    //-------------------------
    // set data from basic type array methods
    //-------------------------
    public boolean setValues(byte[] byteData) {
        if(byteData == null || byteData.length != size)
            return false;

        for (int i = 0; i < byteData.length; i++) {
            if(byteData[i] != 0 && byteData[i] != 1)
                return false;
        }

        int v;
        for (int i = 0; i < rawData.length; i++) {
            v = 0;
            for (int j = 0; j < 8; j++) {
                v = v | (byteData[8*i+j]<<(7-j));
            }
            rawData[i] = (byte)v;
        }
        return true;
    }

    public boolean setValues(short[] shortData) {
        if(shortData == null || shortData.length != size)
            return false;

        for (int i = 0; i < shortData.length; i++) {
            if(shortData[i] != 0 && shortData[i] != 1)
                return false;
        }

        int v;
        for (int i = 0; i < rawData.length; i++) {
            v = 0;
            for (int j = 0; j < 8; j++) {
                v = v | (shortData[8*i+j]<<(7-j));
            }
            rawData[i] = (byte)v;
        }
        return true;
    }

    public boolean setValues(int[] intData) {
        if(intData == null || intData.length != size)
            return false;

        for (int i = 0; i < intData.length; i++) {
            if(intData[i] != 0 && intData[i] != 1)
                return false;
        }

        int v;
        for (int i = 0; i < rawData.length; i++) {
            v = 0;
            for (int j = 0; j < 8; j++) {
                v = v | (intData[8*i+j]<<(7-j));
            }
            rawData[i] = (byte)v;
        }
        return true;
    }

    public boolean setValues(float[] floatData) {
        if(floatData == null || floatData.length != size)
            return false;

        for (int i = 0; i < floatData.length; i++) {
            if(floatData[i] != 0 && floatData[i] != 1)
                return false;
        }

        int v;
        for (int i = 0; i < rawData.length; i++) {
            v = 0;
            for (int j = 0; j < 8; j++) {
                v = v | (((int)floatData[8*i+j])<<(7-j));
            }
            rawData[i] = (byte)v;
        }
        return true;
    }

    public boolean setValues(double[] doubleData) {
        if(doubleData == null || doubleData.length != size)
            return false;

        for (int i = 0; i < doubleData.length; i++) {
            if(doubleData[i] != 0 && doubleData[i] != 1)
                return false;
        }

        int v;
        for (int i = 0; i < rawData.length; i++) {
            v = 0;
            for (int j = 0; j < 8; j++) {
                v = v | (((int)doubleData[8*i+j])<<(7-j));
            }
            rawData[i] = (byte)v;
        }
        return true;
    }



    //-------------------------
    // set data from basic type array with thresholding methods
    //-------------------------

    public boolean setValuesThresholded(byte[] byteData, byte threshold, boolean unsignedBytes) {
        if(byteData == null || byteData.length != size)
            return false;

        int v, off;
        byte data;

        if(unsignedBytes) {
            for (int i = 0; i < rawData.length; i++) {
                v = 0;
                for (int j = 0; j < 8; j++) {
                    off = 8*i+j;
                    if(off >= size)
                        break;
                    data = (byte)(((int)byteData[off]) >= (int)threshold ? 1 : 0);
                    v = v | (data<<(7-j));
                }
                rawData[i] = (byte)v;
            }
        } else {
            for (int i = 0; i < rawData.length; i++) {
                v = 0;
                for (int j = 0; j < 8; j++) {
                    off = 8*i+j;
                    if(off >= size)
                        break;
                    data = (byte)(byteData[off] >= threshold ? 1 : 0);
                    v = v | (data<<(7-j));
                }
                rawData[i] = (byte)v;
            }
        }
        return true;
    }

    public boolean setValuesThresholded(short[] shortData, short threshold, boolean unsignedShorts) {
        if(shortData == null || shortData.length != size)
            return false;

        int v, off;
        byte data;
        if(unsignedShorts) {
            for (int i = 0; i < rawData.length; i++) {
                v = 0;
                for (int j = 0; j < 8; j++) {
                    off = 8*i+j;
                    if(off >= size)
                        break;
                    data = (byte)(((int)shortData[off]) >= (int)threshold ? 1 : 0);
                    v = v | (data<<(7-j));
                }
                rawData[i] = (byte)v;
            }
        } else {
            for (int i = 0; i < rawData.length; i++) {
                v = 0;
                for (int j = 0; j < 8; j++) {
                    off = 8*i+j;
                    if(off >= size)
                        break;
                    data = (byte)(shortData[off] >= threshold ? 1 : 0);
                    v = v | (data<<(7-j));
                }
                rawData[i] = (byte)v;
            }
        }
        return true;
    }

    public boolean setValuesThresholded(int[] intData, int threshold) {
        if(intData == null || intData.length != size)
            return false;

        int v, off;
        byte data;
        for (int i = 0; i < rawData.length; i++) {
            v = 0;            
            for (int j = 0; j < 8; j++) {
                off = 8*i+j;
                if(off >= size)
                    break;
                data = (byte)(intData[off] >= threshold ? 1 : 0);
                v = v | (data<<(7-j));
            }
            rawData[i] = (byte)v;
        }
        return true;
    }

    public boolean setValuesThresholded(float[] floatData, float threshold) {
        if(floatData == null || floatData.length != size)
            return false;

        int v, off;
        byte data;
        for (int i = 0; i < rawData.length; i++) {
            v = 0;            
            for (int j = 0; j < 8; j++) {
                off = 8*i+j;
                if(off >= size)
                    break;
                data = (byte)(floatData[off] >= threshold ? 1 : 0);
                v = v | (data<<(7-j));
            }
            rawData[i] = (byte)v;
        }
        return true;
    }

    public boolean setValuesThresholded(double[] doubleData, double threshold) {
        if(doubleData == null || doubleData.length != size)
            return false;

        int v, off;
        byte data;
        for (int i = 0; i < rawData.length; i++) {
            v = 0;
            for (int j = 0; j < 8; j++) {
                off = 8*i+j;
                if(off >= size)
                    break;
                data = (byte)(doubleData[off] >= threshold ? 1 : 0);
                v = v | (data<<(7-j));
            }
            rawData[i] = (byte)v;
        }
        return true;
    }



    //----------------------
    // data masking
    //----------------------
    public byte[] maskData(byte[] data, byte maskOutValue, boolean invert) {
        if(data.length != size)
            return null;
        
        byte[] out = new byte[data.length];
        if(invert)
            for (int i = 0; i < out.length; i++) {
                out[i] = ( getValueAtIndex(i) ? maskOutValue: data[i]);
            }
        else
            for (int i = 0; i < out.length; i++) {
                out[i] = (getValueAtIndex(i) ? data[i] : maskOutValue);
            }
        return out;
    }

    public short[] maskData(short[] data, short maskOutValue, boolean invert) {
        if(data.length != size)
            return null;

        short[] out = new short[data.length];
        if(invert)
            for (int i = 0; i < out.length; i++) {
                out[i] = ( getValueAtIndex(i) ? maskOutValue: data[i]);
            }
        else
            for (int i = 0; i < out.length; i++) {
                out[i] = (getValueAtIndex(i) ? data[i] : maskOutValue);
            }
        return out;
    }

    public int[] maskData(int[] data, int maskOutValue, boolean invert) {
        if(data.length != size)
            return null;

        int[] out = new int[data.length];
        if(invert)
            for (int i = 0; i < out.length; i++) {
                out[i] = ( getValueAtIndex(i) ? maskOutValue: data[i]);
            }
        else
            for (int i = 0; i < out.length; i++) {
                out[i] = (getValueAtIndex(i) ? data[i] : maskOutValue);
            }
        return out;
    }

    public float[] maskData(float[] data, float maskOutValue, boolean invert) {
        if(data.length != size)
            return null;

        float[] out = new float[data.length];
        if(invert)
            for (int i = 0; i < out.length; i++) {
                out[i] = ( getValueAtIndex(i) ? maskOutValue: data[i]);
            }
        else
            for (int i = 0; i < out.length; i++) {
                out[i] = (getValueAtIndex(i) ? data[i] : maskOutValue);
            }
        return out;
    }

    public double[] maskData(double[] data, double maskOutValue, boolean invert) {
        if(data.length != size)
            return null;

        double[] out = new double[data.length];
        if(invert)
            for (int i = 0; i < out.length; i++) {
                out[i] = ( getValueAtIndex(i) ? maskOutValue: data[i]);
            }
        else
            for (int i = 0; i < out.length; i++) {
                out[i] = (getValueAtIndex(i) ? data[i] : maskOutValue);
            }
        return out;
    }



    //------------------------
    // BitArray Factory
    //------------------------
    public static BitArray createBitArray(byte[] data, boolean unsignedBytes) {
        BitArray out = new BitArray(data.length);
        out.setValuesThresholded(data, (byte)1, unsignedBytes);
        return out;
    }

    public static BitArray createBitArray(short[] data, boolean unsignedShorts) {
        BitArray out = new BitArray(data.length);
        out.setValuesThresholded(data, (short)1, unsignedShorts);
        return out;
    }

    public static BitArray createBitArray(int[] data) {
        BitArray out = new BitArray(data.length);
        out.setValuesThresholded(data, 1);
        return out;
    }

    public static BitArray createBitArray(float[] data) {
        BitArray out = new BitArray(data.length);
        out.setValuesThresholded(data, 1.0f);
        return out;
    }

    public static BitArray createBitArray(double[] data) {
        BitArray out = new BitArray(data.length);
        out.setValuesThresholded(data, 1.0);
        return out;
    }

    public static BitArray createBitArray(byte[] data, byte threshold, boolean unsignedBytes) {
        BitArray out = new BitArray(data.length);
        out.setValuesThresholded(data, threshold, unsignedBytes);
        return out;
    }

    public static BitArray createBitArray(short[] data, short threshold, boolean unsignedShorts) {
        BitArray out = new BitArray(data.length);
        out.setValuesThresholded(data, threshold, unsignedShorts);
        return out;
    }

    public static BitArray createBitArray(int[] data, int threshold) {
        BitArray out = new BitArray(data.length);
        out.setValuesThresholded(data, threshold);
        return out;
    }

    public static BitArray createBitArray(float[] data, int threshold) {
        BitArray out = new BitArray(data.length);
        out.setValuesThresholded(data, threshold);
        return out;
    }

    public static BitArray createBitArray(double[] data, int threshold) {
        BitArray out = new BitArray(data.length);
        out.setValuesThresholded(data, threshold);
        return out;
    }

    public byte getMax() {
        for (int i = 0; i < rawData.length; i++) {
            if( (rawData[i]&0xFF) > 0)
                return 1;
        }
        return 0;
    }

    public byte getMin() {
        for (int i = 0; i < rawData.length; i++) {
            if((rawData[i]&0xFF) < 1)
                return 0;
        }
        return 1;
    }

    /**
     * @return the size
     */
    public int getSize() {
        return size;
    }

}

