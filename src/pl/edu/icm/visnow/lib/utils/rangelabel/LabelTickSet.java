//<editor-fold defaultstate="collapsed" desc=" License ">

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
//</editor-fold>
package pl.edu.icm.visnow.lib.utils.rangelabel;

import java.awt.FontMetrics;
import java.util.Arrays;

//TODO: add subgrid flag (which is if ticks are subgrid of maingrid - that may be usefull for graphs)
//but before that - just remove major ticks 
//(this is to create range/labels like: 500, 1000, 2000 for log ranges like: 700-1500 (much better then range/labels 100 ... 10000)

//                    1. zrobic metode, która dla danej liczby pikseli bedzie zwracala optymalna liczbe wartosci(nie mniejsza niz liczbe pikseli) 
//                    i skok pojedynczego ticka (musi uwzgledniac minimalna odleglosc tikow(px) i optymalna (%))
//                       Przypadki: (szerokie / wąskie etykiety, duzo / malo etykiet(czyli powerMax >> powerMin)), 
//                       szerokosc bardzo duża / średnia / mała => widoczne etykiety potęg, widoczne etykiety 2 i 5, bez tików
//                    ?, tiki przy potęgach, tiki przy potęgach i 2, 5, tiki też w środku ???)
/**
 * Possible set of labels and ticks - completely independent from space where it will be (or not) rendered.
 * This class is a representation of labels and ticks to draw at regular grid (but neither all grid positions need to be occupied by labels nor by ticks!).
 *
 * Spec:
 * TotalGridPositions is always positive (at least 1 position - this is not really a grid - may be suitable for 1px wide slider/axis)
 * TotalGridBins == TotalGridPositions - 1
 * Label positions are within 0 .. TotalGridPositions - 1
 * Label set may be empty (TotalLabelNum == 0)
 * But labels cannot be empty (at least one character)
 * Tick grids may contain 0 grid (no ticks)
 *
 *
 * Tick grid is always a sub-grid of label grid (excluding 0 case: no ticks at all, and 1 case: just one tick at the beginning)!
 *
 * @author szpak
 */
public class LabelTickSet {

    private final int totalGridPositions; //number of grid positions for labels
    private final int[] labelPositions; //label positions on the grid (within 0 .. totalGridPositions - 1 range)
    private final String[] labels;
    //array element equals number of ticks in this sub-grid (if this number is > 1 then first and last ticks always lay at first/last position of a slider)
    private final int[] majorTickGrids; // possible regular tickgrids ordered from best ticking (maximum number of ticks) to worst one (possibly no ticks at all)
    private final int[] minorTickGrids; // possible regular tickgrids ordered from best ticking (maximum number of ticks) to worst one (possibly no ticks at all)
    //range is not necessary here but may be pretty useful if labels/ticks are calculated not for oryginal range but for extended one
    //Object to not lose precision for Long numbers
    private Object rangeStart;
    private Object rangeEnd;
    //real overhead (maximum from beginning and ending overhead)
    private float rangeOverheadPc;
    
    /**
     *
     * @param labelPositions on the grid; have to be in ascending order
     * @param labels labels at corresponding positions in labelPositions.
     */
    public LabelTickSet(int totalGridPositions, int[] labelPositions, String[] labels, int[] majorTickGrids, int[] minorTickGrids) {
        this.totalGridPositions = totalGridPositions;
        this.labelPositions = labelPositions;
        this.labels = labels;
        this.majorTickGrids = majorTickGrids;
        this.minorTickGrids = minorTickGrids;

        if (totalGridPositions <= 0) throw new IllegalArgumentException("Grid positions must be positive");
        for (int pos : labelPositions) if (pos < 0 || pos >= totalGridPositions) throw new IllegalArgumentException("Incorrect label position: " + pos);
        for (String label : labels) if (label.length() <= 0) throw new IllegalArgumentException("Empty labels are not supported");
        //test if tick grids are proper subgrids of main grid
        for (int subGrid : majorTickGrids) if (subGrid < 0 || (subGrid >= 2 && (totalGridPositions - 1) % (subGrid - 1) != 0)) throw new IllegalArgumentException("Incorrect major subgrid: " + subGrid + " Total: " + totalGridPositions);
        for (int subGrid : minorTickGrids) if (subGrid < 0 || (subGrid >= 2 && (totalGridPositions - 1) % (subGrid - 1) != 0)) throw new IllegalArgumentException("Incorrect minor subgrid: " + subGrid + " Total: " + totalGridPositions);
    }

    public int getTotalLabelNum() {
        return labels.length;
    }

    public int getTotalGridPositions() {
        return totalGridPositions;
    }

    /**
     * Return number of 'bins' between grid nodes; This is equal to totalGridPositions - 1 or 0 (if totalGridPositions &le; 1).
     */
    public int getTotalGridBins() {
        return totalGridPositions - 1;
    }

    public int[] getLabelPositions() {
        return labelPositions;
    }

    public String[] getLabels() {
        return labels;
    }

    //TODO: add limitLeftPx, limitRightPx
    // something like
    // (minimum/maximumLabelOverflow?  - which should work for both: thumb width (for slider) and space unlimited/ screen limited overflow for graphs (axes)
    //     * @param labelOverflowLeftPx number of pixels that can be used for labels outside widthPx (additionally to widthPx)
    // * @param labelOverflowRightPx number of pixels that can be used for labels outside widthPx (additionally to widthPx)

     
    //TODO: another flag should be added here ("bounded/limited" or something like that - that will be usefull for axes 3D - we've got no limitations on sides of the range - just in the middle)
    //TODO: slightly different computations for left/right/center alignment (not really applicable to JSliders, but may be usefull in graph axes)
    //TODO: vertical case (or better vertical/horizontal/both - see axes3D)
    //TODO: if only one label fits width and label width is much larger than sliderWidth (after padding) then mark as 'not enough space'
    //TODO: For JSlider it does not work perfectly because Thumb width should be considered as well (space is reserved for Thumb - no matter labels are visible or not) (but possible error is not very important - works slightly incorrect only for very narrow slider (less then one label width))
    /**
     * Tests if passed space (
     * (
     * <code>widthPx</code>) is large enough for this LabelTickSet.
     *
     * @param widthPx free space (in pixels)
     * @param minimumDistancePx minimum distance between labels (in pixels)
     *
     * [Note: Following calculations need to be done in real arithmetics to avoid blinking (while resize) - this is because after rounding to integer,
     * paradoxically, distance between labels can be larger for smaller width!
     * For the same reason minimum margin should be 1px (or maybe not .. 1px margin is an additional implemented feature)]
     */
    public boolean isEnoughSpace(int widthPx, FontMetrics fontMetrics, int minimumDistancePx) {//, int labelOverflowLeftPx, int labelOverflowRightPx) {
        //always enough space for no labels
        if (labels.length == 0) return true;

        //It's necessary to compute max label width because swing leaves left-right padding of size maxLabelWidth/2
        int maxLabelOddWidthPx = 0;
        int minLabelOddWidthPx = Integer.MAX_VALUE; //compute to eliminate obvious case (if number of labels times minWidth > width)
        int[] labelWidths = new int[labels.length];
        for (int i = 0; i < labels.length; i++) {
//                LOGGER.debug(labels[i] + " " + fontMetrics);
            int w = fontMetrics.stringWidth(labels[i]);
            //make it odd (one pixel in the center and remaining pixels symetrically)
            if (w % 2 == 0) w++;
            labelWidths[i] = w;
            maxLabelOddWidthPx = Math.max(w, maxLabelOddWidthPx);
            minLabelOddWidthPx = Math.min(w, minLabelOddWidthPx);
        }

        //eliminate obvious case (if number of labels * shortest label + minimum distance > width)
        //(this is not necessary but could speed up computations in many cases)
        if (labels.length * minLabelOddWidthPx + (labels.length - 1) * minimumDistancePx > widthPx) return false;

        //TODO: test label margins (space between labels added by swing/JSlider ?)
        //TODO: here max from maxLabelWidthPx and ThumbWidth can be taken
        //TODO: In VisNow Thumb has width 15px (7px each side). Good news is that slider.getWidth() gives correct answer (checked in Gimp)
        //width of slider with labels (after padding)

        int sliderWidthPx = widthPx - maxLabelOddWidthPx + 1; // in px
        int sliderStartPosPx = maxLabelOddWidthPx / 2; // in px (== 0 for no labels)

        //Real number arithmetics (read javadoc above)
        //free space left - decreases after adding another labels
        double freeSpaceStartPx = 0; //these start/end positions are in fact real position (where integer values like 0 and widthPx are BETWEEN physical pixels)
        double freeSpaceEndPx = widthPx;

        double tolerancePx = 0.1; //tolerance in px (necessary to avoid floating point problems; without that no labels at all)

        for (int i = 0; i < labels.length; i++) {
            double positionAtSlider = (double) labelPositions[i] * (sliderWidthPx - 1) / totalGridPositions + 0.5; // in px (0.5 to put it in the middle of the pixel)
            double labelHalfWidth = (double) labelWidths[i] / 2;
            double labelStartPos = sliderStartPosPx + positionAtSlider - labelHalfWidth;
            double labelEndPos = sliderStartPosPx + positionAtSlider + labelHalfWidth;

            //label does not fit free area (tolerance needed to avoid rounding problems (see javadoc for this method))
            if (labelStartPos < freeSpaceStartPx - tolerancePx || labelEndPos > freeSpaceEndPx + tolerancePx) return false;

            //update free space
            freeSpaceStartPx = labelEndPos;
            //add margin
            freeSpaceStartPx += minimumDistancePx;
        }


        return true;
    }

    //TODO: minimum distance should be rather like 1px (add to spec)
    /**
     * Calculates best tick spacing and returns tick spacing in grid units (so if return 1 then every grid node should be painted as a tick)
     *
     * @param major if true then major ticks are calculated, minor ticks otherwise
     * @param width slider width in px
     * @param minimumDistance minimum distance in px between ticks (for example: if minimumDistance == 1 then there will be at least 1 px space between two ticks);
     * minimumDistance should be in general smaller for minor ticks and larger for major ticks (sometimes visible minimumDistance can be smaller than needed - and this
     * (apart from problems with calculating Thumb width) is caused by incorrect rounding tick position in JSlider (one can see distance 1 and 3 px in the same set!))
     * @return tick spacing in grid units or 0 if no ticks should be drawn
     */
    public int getBestTickSpacing(boolean major, int width, int minimumDistance) {
        if (major) {
            for (int tickGrid : majorTickGrids)
                if (tickGrid != 0 && (width - 1) / (tickGrid - 1) > minimumDistance) return (totalGridPositions - 1) / (tickGrid - 1);
        } else {
            for (int tickGrid : minorTickGrids)
                if (tickGrid != 0 && (width - 1) / (tickGrid - 1) > minimumDistance) return (totalGridPositions - 1) / (tickGrid - 1);
        }
        return 0;
    }

    /**
     * Returns maximum label width for passed
     * <code>fontMetrix</code>; Works with AWT toolkit
     *
     * @param fontMetrics
     * @return maximum label width or 0 if label set is empty
     */
    public int getMaximumLabelWidth(FontMetrics fontMetrics) {
        int maxLabelWidth = 0;
        for (String label : labels)
            maxLabelWidth = Math.max(fontMetrics.stringWidth(label), maxLabelWidth);
        return maxLabelWidth;
    }

    /**
     * Returns extended info about this set.
     */
    @Override
    public String toString() {
        return "Grid: " + totalGridPositions + ";  Labels: " + Arrays.toString(labels) + " at: " + Arrays.toString(labelPositions) + ";  Major tick grids: " + Arrays.toString(majorTickGrids) + ";  Minor tick grids: " + Arrays.toString(minorTickGrids);
    }
}