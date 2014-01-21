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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import pl.edu.icm.visnow.lib.utils.FloatFormats;
//import static pl.edu.icm.visnow.lib.utils.FloatFormats.eformat;

/**
 *
 * @author szpak
 */
public class LabelGenerator {
    private static final Logger LOGGER = Logger.getLogger(LabelGenerator.class);
    /**
     * Returns LabelTickSet array in order from best labeling (maximum number of labels) to worst one (no labels at all).
     *
     * Returns label maps (key: position -> value: label string) in priority order (most important labels first).
     * First label is
     * <code>10^powerMin</code> last is
     * <code>10^powerMax</code>.
     * All labels are properly formatted.
     * All returned labels should be placed on regular grid.
     *
     */
    public static LabelTickSet[] createPossibleLog10Labels(int powerMin, int powerMax) {

        //possible tick cases:
        //1 ticks (powerMin < powerMax but no space for 2 ticks)
        //1 tick (powerMin == powerMax) [show 1 tick even if no space for label]
        //2 ticks (powerMin < powerMax AND space for minimum 2 ticks but less then powerMax-powerMin+1)
        //powerMax-powerMin+1 ticks (powerMin < powerMax AND space for powerMax - powerMin + 1 but less then (powerMax - powerMin) * 10 + 1)
        //(powerMax - powerMin) * 10 + 1 ticks (powerMin < powerMax AND enough space for (powerMax - powerMin) * 10 + 1)

        //possible tick grid {0, 1, 2, powerMax - powerMin + 1, (powerMax - powerMin) * 10 + 1}


        //number of labels should be as large as possible!(?)
        //but in fact only these few cases are considered because it's hard to add other logarithmic labels than powers of 10 and its 2 and 5 multiples
        //so now, completely by accident, it looks like (for wide sliders) there was something like minimum distance between labels/ticks 
        //as a percent of whole slider range!
        //TODO: but anyway slider/labels don't need to start from 10^powerMin and end at 10^powerMax ! - exactly the same as it's done now in FloatSlider!


        //TODO: add case (when only powers of 10 are visible then add cases like - skip 1 label, skip 2 labels, skip 3 ... - but keep last label)
        //
        //
        //
        //possible label cases (from best to worst):
        // [implemented below]
        //
        //I. if powerMin == powerMax:
        //a) 1 label   + ticks {1}
        //b) NO labels + ticks {0}
        //
        //II. if powerMin + 1 == powerMax:
        //a) 4 labels          + ticks {0, ?1, 2 == powerMax - powerMin + 1, (powerMax - powerMin) * 10 + 1 = 11 [?major/minor]}
        //b) 2 labels (min/max)+ ticks {0, ?1, 2 == powerMax - powerMin + 1, (powerMax - powerMin) * 10 + 1 = 11 [?major/minor]}
        //c) 1 label (min)     + ticks {0, ?1, 2 == powerMax - powerMin + 1, (powerMax - powerMin) * 10 + 1 = 11 [?major/minor]}
        //d) NO labels         + ticks {0, 2 == powerMax - powerMin + 1, (powerMax - powerMin) * 10 + 1 = 11 [?major/minor]}
        //
        //III. if powerMin + 1 < powerMax:
        //a) (powerMax - powerMin + 1) * 3 + 1 labels  + ticks {0, ?1, 2, powerMax - powerMin + 1, (powerMax - powerMin) * 10 + 1 [?major/minor]}
        //a') (powerMax - powerMin + 1) labels         + ticks {0, ?1, 2, powerMax - powerMin + 1, (powerMax - powerMin) * 10 + 1 [?major/minor]}
        //b) 2 labels (min/max)                        + ticks {0, ?1, 2, powerMax - powerMin + 1, (powerMax - powerMin) * 10 + 1 [?major/minor]}
        //c) 1 label (min)                             + ticks {0, ?1, 2, powerMax - powerMin + 1, (powerMax - powerMin) * 10 + 1 [?major/minor]}
        //d) NO labels                                 + ticks {0, 2, powerMax - powerMin + 1, (powerMax - powerMin) * 10 + 1 [?major/minor]}
        //
        //

        //log10(2) = 0,30102999566398119802 ~ 0.3
        //log10(5) = 0,69897000433601885749 ~ 0.7
        //~0.3 & ~0.7 so we split each power into 10 parts
        //this will give error of size:
        //    for screen/slider width = 2000px
        //    for powerMax == powerMin + 1
        //    error = 2000 px * (log10(2) - 0.3) =~ 2 px
        //one could treat this as a bug, but it's probably the best solution to put log10(2) and log10(5) on the regular grid

        //TODO: add another case ???

        if (powerMin > powerMax) throw new IllegalArgumentException("min exponent cannot be larger than max exponent");
        else if (powerMin == powerMax) { //case I
            String format = FloatFormats.eformat(powerMin);
            return new LabelTickSet[]{
                new LabelTickSet(1, new int[]{0}, new String[]{String.format(format, Math.pow(10.0, powerMin))}, new int[]{1}, new int[]{}),
                new LabelTickSet(1, new int[]{}, new String[]{}, new int[]{}, new int[]{})
            };
//        } else if (powerMin + 1 == powerMax) { //case II
//            String formatMin = eformat(powerMin);
//            String formatMax = eformat(powerMax);
//
//            int totalGridPositions = 11; // == (powerMax - powerMin) * 10 + 1;
//            int[] fullTickGrids = new int[]{11, 2, 0};
//            int[] mainTickGrids = new int[]{2, 0};
////            int[] tickGrids = new int[]{11, 2, 0};
//
//            List<LabelTickSet> ltsList = new ArrayList<LabelTickSet>();
//
//            String[] labels = new String[]{String.format(formatMin, Math.pow(10.0, powerMin)),
//                String.format(formatMin, 2.0 * Math.pow(10.0, powerMin)),
//                String.format(formatMin, 5.0 * Math.pow(10.0, powerMin)),
//                String.format(formatMax, Math.pow(10.0, powerMax))};
//
//            ltsList.add(new LabelTickSet(totalGridPositions, new int[]{0, 3, 7, 10}, labels, mainTickGrids, fullTickGrids));
//            ltsList.add(new LabelTickSet(totalGridPositions, new int[]{0, 10}, new String[]{labels[0], labels[3]}, mainTickGrids, fullTickGrids));
//            ltsList.add(new LabelTickSet(totalGridPositions, new int[]{0}, new String[]{labels[0]}, mainTickGrids, fullTickGrids));
//            ltsList.add(new LabelTickSet(totalGridPositions, new int[]{}, new String[]{}, mainTickGrids, fullTickGrids));
//            return ltsList.toArray(new LabelTickSet[ltsList.size()]);
//
        } else { //case II and III 
            int totalGridPositions = (powerMax - powerMin) * 10 + 1;
            int[] fullTickGrids = new int[]{(powerMax - powerMin) * 10 + 1, powerMax - powerMin + 1, 2, 0};
            int[] mainTickGrids = new int[]{powerMax - powerMin + 1, 2, 0};

            List<LabelTickSet> ltsList = new ArrayList<LabelTickSet>();

            //labels of type 10^k and 2*10^k, 5*10^k
            String[] labelsFull = new String[(powerMax - powerMin) * 3 + 1];
            int[] labelPosFull = new int[(powerMax - powerMin) * 3 + 1];
            //labels of type 10^k
            String[] labelsMain = new String[powerMax - powerMin + 1];
            int[] labelPosMain = new int[powerMax - powerMin + 1];
            int labelFullNum = 0;
            int labelMainNum = 0;
            int posOnGrid = 0;

            for (int p = powerMin; p <= powerMax; p++) {
                String format = FloatFormats.eformat(p);
                //main labels (of type 10^k)
                String labelMain = String.format(format, Math.pow(10.0, p));
                labelsFull[labelFullNum] = labelMain;
                labelPosFull[labelFullNum++] = posOnGrid;
                labelsMain[labelMainNum] = labelMain;
                labelPosMain[labelMainNum++] = posOnGrid;
                //sub labels (of type 2*10^k, 5*10^k)
                if (p != powerMax) {
                    labelsFull[labelFullNum] = String.format(format, 2 * Math.pow(10.0, p));
                    labelPosFull[labelFullNum++] = posOnGrid + 3;
                    labelsFull[labelFullNum] = String.format(format, 5 * Math.pow(10.0, p));
                    labelPosFull[labelFullNum++] = posOnGrid + 7;
                }
                posOnGrid += 10;
            }

            ltsList.add(new LabelTickSet(totalGridPositions, labelPosFull, labelsFull, mainTickGrids, fullTickGrids));
            ltsList.add(new LabelTickSet(totalGridPositions, labelPosMain, labelsMain, mainTickGrids, fullTickGrids));
            if (powerMin + 1 < powerMax) //case III only
                ltsList.add(new LabelTickSet(totalGridPositions, new int[]{0, totalGridPositions - 1}, new String[]{labelsFull[0], labelsFull[(powerMax - powerMin) * 3]}, mainTickGrids, fullTickGrids));
            ltsList.add(new LabelTickSet(totalGridPositions, new int[]{0}, new String[]{labelsFull[0]}, mainTickGrids, fullTickGrids));
            ltsList.add(new LabelTickSet(totalGridPositions, new int[]{}, new String[]{}, mainTickGrids, fullTickGrids));
            return ltsList.toArray(new LabelTickSet[ltsList.size()]);
        }



        //NO labels + any of these {0, 1, 2, powerMax - powerMin + 1, (powerMax - powerMin) * 10 + 1}
        //1 label (only powerMin label) when powerMin=
        //1 label (only powerMin label - when there is space only for one label) OR (there is space for at least one label AND powerMin == powerMax)
        //2 labels (powerMin + 1 == powerMax AND there is enough space for 2 labels but not enough for )

    }

    //TODO: add maximum total number of labels (it's not so good to have too many labels on the screen - hmm.. .I don't know...)
    //linear scale cases:
    //3 possibilities (minor ticks + possible label positions):
    //- multiples of 1*10^n0
    //- multiples of 2*10^n0
    //- multiples of 5*10^n0
    //So the smallest possible n0 and smallest possible 1/2/5 should be taken to have possiblity of showing minor ticks? (at least 1 px distance)
    // ???????\
    //
    //
    //For sure labels of type k*[1|2|5]*10^(n+1) should have higher priority then these of type k*[1|2|5]*10^n ?? (this is subset anyway ?? !! - almost: 2<>5)
    // 
    //Maybe this should be the first question: Should array of LabelTickSet be monotnic/linearly ordered ?? (one LabelTickSet is always subset of another LabelTickSet?)
    //If yes then:
    //if sliderRange always starts/ends at userMin/Max
    //than we have just simple order:
    //- no labels
    //- first label like k*10^n - for highest possible n (within k0*10^n within userMin...userMax range) 
    //- first/last label like k*10^n 
    //- some labels like k*10^n 
    //- labels like k*10^n 
    //- labels like k*10^n and like k*10^n + [2|5]*10^(n-1)
    //- labels like k*10^(n-1) ...
    //- labels like k*10^(n-1) and like k*10^(n-1) + [2|5]*10^(n-2) ...
    //- ... and so on
    //
    //now, what about the range (and ticks (minor/ ?major?))
    //First assumption should be that there is no label without corresponding tick! 
    //So if only large (few) labels are shown on a slider then every label has it's tick (at least in tick set - they can be hidden if minimumDistancePx will be too large)
    //[Or no ticks at all! ?]
    //Maybe there should be also assumption that last level (most detailed one) is shown using minor ticks and last but one is shown using major ticks
    //
    //Another approach that makes things easier is to show major ticks only when higher level can be shown this way (so think about major<>minor ticks as the least priority problem)
    //
    //Good solution whould be maximumOverhead (in percents/pixels - max = min(10%, 50px) should be all right). That would give enough space for extension to put ticks at proper positions.
    //maximumBeginingOverhead - is not very good because it's better to always have grid/ticks at beginning/end of a set/slider/axis!
    //or maximumInactiveOverhead - to describe that it will be inactive part of a slider? Or just maximumOverhead (better name for labeling charts)
    //But we need to extend LabelGenerator
    //This should be fixed in createLogScale - here beginning/end overhead is needeed (or just one maximumOverhead - used only on the beginning in linear scale, and on the beginning and end in logarithmic scale)
    //TODO: do not put labels outside range (from extended range) into set (this is to avoid confusing user about range to select)
    //TODO: replace begining/end label with range min/max. Example: user range: -1.7...10.1, slider range: -2...11 (or -2...10.5) labels at ends: -1.7, 10.1
    /**
     * Creates possible linear labeling; Overhead is added to range if it's necessary to create proper grid/ticks.
     * Provided overhead value should be reasonably large (something like 5-10%) to allow to create (major) ticks at possibly high level.
     *
     * @param valueMin range min value
     * @param valueMax range max value
     * @param overheadPc overhead to the range (in percent)
     */
    public static LabelTickSet[] createPossibleLinearLabels(float valueMin, float valueMax, float overheadPc) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Creates possible linear labeling. Note that resulting ticks may not really reflect label grid (which should not be the case).
     *
     * This method is to provide back compatibility with previous slider (FloatSlider).
     */
    public static LabelTickSet[] createPossibleLinearLabels(float valueMin, float valueMax) {
        //So just a quick fix.
        //0. grid range: valueMin...valueMax
        //1. no ticks at all     
        //2. labels at correct positions
        //3. [optional] first and last label with valueMin/valueMax (with or without previous first/last labels (3 cases in total))

        float valueRange = valueMax - valueMin;

        if (valueRange < 0) throw new IllegalArgumentException("min value cannot be larger than max value");
        else if (valueRange == 0) { //precise 0
//            String format = FloatFormats.format(valueMin);
            return new LabelTickSet[]{
                //                new LabelTickSet(1, new int[]{0}, new String[]{String.format(format, valueMin)}, new int[]{1}, new int[]{}),
                //no ticks (quick fix)
//                new LabelTickSet(1, new int[]{0}, new String[]{String.format(format, valueMin)}, new int[]{}, new int[]{}),
                //incorrect format - find good one
                new LabelTickSet(1, new int[]{0}, new String[]{valueMin + ""}, new int[]{}, new int[]{}),
                new LabelTickSet(1, new int[]{}, new String[]{}, new int[]{}, new int[]{})
            };
        } else {
            //0. grid range: valueMin...valueMax
            //1. no ticks at all     
            //2. labels at correct positions
            //3. [optional] first and last label with valueMin/valueMax (with or without previous first/last labels (3 cases in total))

            //create LabelTickSet lis
            List<LabelTickSet> ltsList = new ArrayList<LabelTickSet>();


            //less then 10 but at least 1 number (within range) of type k * baseStep10
            float baseStep10 = (float) Math.pow(10, Math.floor(Math.log10(valueRange)));


            //TODO: this is temporal solution - fix it.. (now some large precision for each step is needed to create dense enough grid.
            int gridPrecision = 100;
            
            //TODO: optimize it anyway! (even 1000 labels can be generated now! - and component width calculated, which may be time consuming - test it!)
            //3 levels should be enough(k*10^2 = k*100 < 1000 labels)
            //3 sublevels at each level
            float[] steps = new float[]{baseStep10 / 100, 2 * baseStep10 / 100, 5 * baseStep10 / 100, baseStep10 / 10, 2 * baseStep10 / 10, 5 * baseStep10 / 10, baseStep10, 2 * baseStep10, 5 * baseStep10};

            for (float step : steps) {
                String format;
                if (step >= 1) format = "%.0f";
                else format = "%3." + (int)(1 - Math.log10(step)) + "f";
                
                LOGGER.debug(format);
                
                int totalGridPositions = Math.round(valueRange / step * gridPrecision) + 1;                
                int firstLabelPosition = (int)Math.round((Math.ceil(valueMin / step) - valueMin / step) * gridPrecision);
                
                LOGGER.debug(totalGridPositions + " " + firstLabelPosition);
                
                int numLabels = (int) Math.floor(valueMax / step) - (int) Math.ceil(valueMin / step) + 1;
                //positive value needs to be tested only for highest level (2 * baseStep10 and 5 * baseStep10) 
                //additionally should be at least 2 values to avoid one label for non min/max values (like only "20" label when range is 1..21)
                if (numLabels >= 2) {
                    float firstLabel = (float) (step * Math.ceil(valueMin / step));
                    String[] labels = new String[numLabels];
                    int[] positions = new int[numLabels];
                    for (int i = 0; i < numLabels; i++) {
//                        labels[i] = (float) (step * (i + Math.ceil(valueMin / step))) + ""; //better precision this way? (not much)
//                        labels[i] = firstLabel + i * step + "";//String.format(format, firstLabel + i * step); (doesn't work properly for range 1..21)
                        labels[i] = String.format(format, firstLabel + i * step); //(doesn't work properly for range 1..21)
                        //TODO: test it and calculate precisely!
                        positions[i] = Math.min(firstLabelPosition + i * gridPrecision, totalGridPositions - 1);//i;
                    }

//                    ok.. fix this... maybe add fake ticks and that's it for a moment
                    //TODO: temporaly test - set proper totalgrid and label positions here
                    ltsList.add(new LabelTickSet(totalGridPositions, positions, labels, new int[]{}, new int[]{}));
                }
            }

            //just first and last label
            //TODO: default formatting now - test it
            ltsList.add(new LabelTickSet(2, new int[]{0, 1}, new String[]{valueMin + "", valueMax + ""}, new int[]{}, new int[]{}));
            ltsList.add(new LabelTickSet(2, new int[]{0}, new String[]{valueMin + ""}, new int[]{}, new int[]{}));
//            ltsList.add(new LabelTickSet(2, new int[]{0, 1}, new String[]{String.format(format, valueMin), String.format(format, valueMax)}, new int[]{}, new int[]{}));
//            ltsList.add(new LabelTickSet(2, new int[]{0}, new String[]{String.format(format, valueMin)}, new int[]{}, new int[]{}));
            ltsList.add(new LabelTickSet(2, new int[]{}, new String[]{}, new int[]{}, new int[]{}));

            return ltsList.toArray(new LabelTickSet[ltsList.size()]);

//            //revert and return list
//            LabelTickSet[] lts = new LabelTickSet[ltsList.size()];
//            for (int l = ltsList.size() - 1, i = 0; l >= 0; l++)
//                lts[i++] = ltsList.get(l);
//            return lts;

        }
    }
}
