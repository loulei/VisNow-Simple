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

package pl.edu.icm.visnow.lib.utils;

import static java.lang.Math.*;
/*
 * Range.java
 *
 * Created on November 19, 2003, 2:25 PM
 */

/**
 * A class for "nice" range segments. It splits a given range into "buckets"
 * (subranges) that begin and end in some "nice", round numbers, such as: <ul>
 * <li>1, 1.5, 2, 2.5, ... or </li>
 * <li>1, 2, 3, ... or </li>
 * <li>1, 2, 5, 10, 20, 50, ... etc. </li></ul>
 *
 * instead of values computed by a simple formula: 
 * <code>(upper_bound - lower_bound) / segment_count)</code>, 
 * which usually gives awful numbers with many digits after dot.
 *
 * This class enables to display such "nice" values in legend or use them for
 * displaying isolines.
 *
 * @author Krzysztof S. Nowinski (know@icm.edu.pl) 
 * Warsaw University
 * Interdisciplinary Centre for Mathematical and Computational Modelling
 */
public class Range {

    /**
     * Step between ticks. It could be set to (1, 2 or 5) * 10^N, N - integer
     */
    private float step;
    /**
     * Number of ticks.
     */
    private int nsteps;
    /**
     * Original range, in contrast to {@link #range range}.
     */
    private float[] rangeOrig;
    /**
     * A calculated range - with "nice" numbers.
     */
    private float[] range;
    /**
     * @deprecated It's used nowhere, but I don't delete that - just in case...
     */
    private float scale;
    private static final float[] std_range = {0.f, 1.f,};

    private Range(float low, float up) {
        range = new float[2];
        rangeOrig = new float[2];
        rangeOrig[0] = low;
        rangeOrig[1] = up;
    }

    /**
     * Creates a new instance of Range.
     */
    public Range(float low, float up, int width, boolean inner) {
        this(low, up);

        if (width < 200)
            width = 200;

        float tx;

        if (up == low)
            tx = 0;
        else {
            tx = (up - low) * 1000.f / width;

            int nx = (int) (Math.floor(Math.log10(tx)));
            tx /= Math.pow(10., 1. * nx);
            if (tx < 2.f)
                tx = 1.f;
            else {
                if (tx < 5.f)
                    tx = 2.f;
                else
                    tx = 5.f;
            }
            tx *= Math.pow(10., 1. * nx) / 10.f;
        }
        calculateRange(tx, low, up, inner);
    }

    /**
     *
     * @param nx desired number of buckets (usually the computed range will no
     * have EXACTLY this number of buckets, but only more or less)
     * @param low lower bound of the range
     * @param up upper bound of the range
     * @param inner should be bounds included in the calculated
     * {@link #range range}?
     */
    public Range(int nx, float low, float up, boolean inner) {
        this(low, up);

        //tx - range step
        float tx;
        if (up == low)
            tx = 0;
        else {
            tx = (up - low) / nx;

//        int exp = (int) (1000 + Math.log10(tx)) - 1000; // why in such way?? changing to a normal cast
            int exp = (int) Math.log10(tx);

            tx /= Math.pow(10., exp);
            // now tx is from range [1.0; 10.0)
            if (tx < 2.f)
                tx = 1.f;
            else if (tx < 5.f)
                tx = 2.f;
            else
                tx = 5.f;

            // restoring the proper number of zeroes (exponent)
            tx *= Math.pow(10., exp);
        }

        calculateRange(tx, low, up, inner);

    }

    public Range(float low, float up, int width) {
        this(low, up, width, true);
    }

    public Range(int nx, float low, float up) {
        this(nx, low, up, true);
    }

    public Range(float[] r, int width, boolean inner) {
        this(r[0], r[1], width, inner);
    }

    public Range(float[] r, int width) {
        this(r[0], r[1], width);
    }

    public Range() {
        this(std_range, 400);
    }

    /**
     * Sets {@link #step step} and calculates {@link #range range} (a computed
     * range with "nice" numbers)
     *
     * @param tx range step
     * @param low lower bound of the range
     * @param up upper bound of the range
     * @param inner should be bounds included in the computed
     * {@link #range range}?
     */
    private void calculateRange(float tx, float low, float up, boolean inner) {
        step = tx;

        if (up == low) {
            range[0] = range[1] = low;
            nsteps = 2;
        } else {
            range[0] = ((int) (low / tx)) * tx;
            if (range[0] < low && inner)
                range[0] += tx;
            if (range[0] > low && !inner)
                range[0] -= tx;

            range[1] = ((int) (up / tx)) * tx;
            if (range[1] < up && !inner)
                range[1] += tx;
            if (range[1] > up && inner)
                range[1] -= tx;

            nsteps = (int) ((range[1] - range[0]) / step);
        }
        if (nsteps < 2)
            nsteps = 2;
        scale = 1.f / (nsteps * step);

    }

    public int getNsteps() {
        return nsteps;
    }

    public float[] getRange() {
        return this.range;
    }

    public float[] getRangeOrig() {
        return this.rangeOrig;
    }

    public float getStep() {
        return step;
    }

    /**
     * @deprecated It's used nowhere, but I don't delete that - just in case...
     */
    public float getScale() {
        return scale;
    }

    public float getLow() {
        return this.range[0];
    }

    public float getUp() {
        return this.range[1];
    }

    public static float[] createLinearRange(int nDiv, float rangeMin, float rangeMax) {
        double r = rangeMax - rangeMin;
        if (r <= 0)
            r = 1;
        double logr = log10(r);
        int iLogr = (int) (logr + 100) - 100;
        double mr = r / pow(10., 1. * iLogr);
        float d;
        float rMin;
        float rMax;
        float[] t;
        if (nDiv < 1)
            nDiv = 1;
        while (nDiv > mr) {
            mr *= 10;
            iLogr -= 1;
        }
        mr /= nDiv;
        if (mr < 2)
            mr = 2;
        else if (mr < 5)
            mr = 5;
        else
            mr = 10;
        d = (float) mr;
        if (iLogr > 0)
            for (int i = 0; i < iLogr; i++)
                d *= 10;
        if (iLogr < 0)
            for (int i = 0; i > iLogr; i--)
                d /= 10;
        rMin = d * ((int) (rangeMin / d));
        while (rMin < rangeMin)
            rMin += d;
        rMax = d * ((int) (rangeMax / d));
        while (rMax > rangeMax)
            rMax -= d;
        int l = (int) (rMin / d) - (int) (rangeMin / d);
        int u = (int) (rMax / d) - (int) (rangeMin / d);
        t = new float[u - l + 1];
        for (int i = 0; i + l <= u; i++)
            t[i] = rMin + i * d;
        return t;
    }

    public static float[] createLogRange(int nVals, float r0, float r1, boolean equallySpacedDecades) {
        float[][] decades = new float[][]{{1},
            {1, 3},
            {1, 2, 5},
            {1, 1.5f, 2.5f, 3, 5, 7},
            {1, 1.3f, 1.6f, 2, 2.5f, 3, 4, 5, 6, 8},
            {1, 1.2f, 1.4f, 1.7f, 2, 2.5f, 3f, 3.5f, 4, 5, 6, 7, 8},
            {1, 1.1f, 1.3f, 1.5f, 1.7f, 2, 2.3f, 2.6f, 3, 3.5f, 4, 4.5f, 5, 6, 7, 8, 9},
            {1, 1.1f, 1.2f, 1.3f, 1.4f, 1.5f, 1.6f, 1.8f, 2f, 2.2f, 2.4f, 2.6f, 2.8f, 3f, 3.2f, 3.4f, 3.6f, 3.8f, 4, 4.5f, 5, 5.5f, 6, 6.5f, 7, 7.5f, 8, 8.5f, 9},
            {1, 1.05f, 1.1f, 1.15f, 1.2f, 1.25f, 1.3f, 1.4f, 1.5f, 1.6f, 1.7f, 1.8f, 1.9f, 2, 2.1f, 2.2f, 2.3f, 2.4f, 2.5f, 2.6f, 2.8f, 3, 3.2f, 3.4f, 3.6f, 3.8f, 4, 4.2f, 4.4f, 4.6f, 4.8f, 5,
                5.3f, 5.6f, 6, 6.3f, 6.6f, 7, 7.5f, 8, 8.5f, 9, 9.5f}
        };
        float[][] eqDecades = new float[][]{{1},
            {1, 5},
            {1, 2, 4, 6, 8},
            {1, 2, 3, 4, 5, 6, 7, 8, 9},
            {1, 1.5f, 2, 2.5f, 3, 3.5f, 4, 4.5f, 5, 5.5f, 6, 6.5f, 7, 7.5f, 8, 8.5f, 9, 9.5f},};
        float[] logThrs = null;
        float[] decadeThrs = null;
        if (equallySpacedDecades)
            decadeThrs = eqDecades[min(nVals / 2, eqDecades.length - 1)];
        else
            decadeThrs = decades[min(nVals, decades.length - 1)];
        nVals = decadeThrs.length;
        try {
            if (r0 * r1 < 0) {
                r1 = max(abs(r0), abs(r1));
                r0 = r1 / 1000;
            } else {
                float u = min(abs(r0), abs(r1));
                r1 = max(abs(r0), abs(r1));
                if (u > 0)
                    r0 = u;
                else
                    r0 = r1 / 1000;
            }
            double logr0 = log10(r0);
            double logr1 = log10(r1);
            if (logr1 < logr0 + 1)
                logr1 = logr0 + 1;
            int iLogr0 = (int) (logr0 + 100.1) - 100;
            int iLogr1 = (int) (logr1 + 100.9) - 100;
            if (iLogr0 == iLogr1)
                iLogr1 = iLogr0 + 1;
            logThrs = new float[2 * (nVals * (iLogr1 - iLogr0) + 1) + 1];
            for (int i = iLogr0, k = 0; i < iLogr1; i++) {
                float v = (float) pow(10, i);
                for (int j = 0; j < nVals; j++, k++)
                    logThrs[k] = decadeThrs[j] * v;
            }
            logThrs[nVals * (iLogr1 - iLogr0)] = decadeThrs[0] * (float) pow(10, iLogr1);
            for (int i = 0; i < nVals * (iLogr1 - iLogr0) + 1; i++)
                logThrs[i + nVals * (iLogr1 - iLogr0) + 1] = -logThrs[i];
            logThrs[2 * (nVals * (iLogr1 - iLogr0) + 1)] = 0;
        } catch (Exception e) {
        }
        return logThrs;
    }
}
