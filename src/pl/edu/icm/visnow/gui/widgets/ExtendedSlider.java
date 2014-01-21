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
package pl.edu.icm.visnow.gui.widgets;

import java.awt.Font;
import java.awt.FontMetrics;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
//import org.apache.log4j.Logger;
import pl.edu.icm.visnow.gui.swingwrappers.UserActionListener;
import static pl.edu.icm.visnow.lib.utils.FloatFormats.*;
import pl.edu.icm.visnow.lib.utils.rangelabel.LabelGenerator;
import pl.edu.icm.visnow.lib.utils.rangelabel.LabelTickSet;
//import pl.edu.icm.visnow.system.main.VisNow;

//TODO: add "toggle enhanced controls" setter
//TODO: add globalMin/globaMax
// so in total we have
// allowedMin <= globaMin, sliderMin <= userMin <= userVal <= userMax <= sliderMax, globalMax <= allowedMax
// and completely independent  slider.getMinimum(), slider.getMaximum(
//TODO: (testit) make it respect minimum size (now when run as single file it can be resized to any width (but when dragging by window corner or bottom edge only))
/**
 * Main assumption is that slider model is always in correct state! (so setters also validate model!)
 * (but be carefull because NB designer can set min/max/val in random order (by accident alphabetical order max &le; min &le; val is good))
 *
 * Setters are inactive! (no fireStateChanged is called!).
 * //TODO: setters should be changed to set*Silent similar like in new VN Swing wrappers
 *
 * Main assumption is that min/max of JSlider are log of (sliderMax/sliderMin), + const, * const
 *
 * In total we have allowedMin &le; globaMin, sliderMin - e &le; userMin &le; userVal &le; userMax &le; sliderMax + e, globalMax &le; allowedMax
 * TODO: epsilon is necessary to avoid cases like 9.9999 - 1000.0001 and containing range 10^0 - 10^4.
 * And completely independent jSliderMin and jSliderMax.
 *
 * (TODO: int/float) Logarithmic slider; rounds slider min/max to the smallest range of type 10^m - 10^n, n > m that contains passed min, max values.
 *
 *
 * Difference between linear and logarithmic scale are in:
 * - allowedMin/Max (always positive for logarithmic scale)
 * - updateModel (depends on allowedMin/Max - so no change in code)
 * - recalcSliderIfNeeded (needs to be called each time scale change is done)
 * - recalcSlider (labels calculated differently)
 * - sliderStateChanged (different calculations for log/linear scale)
 * - updateSliderThumb (different calculations for log/linear scale)
 *
 * Slider should start as linear scale slider and then can be set to logarithmic to avoid NetBeans GUI designer bugs (linear range is wider).
 *
 * When changing scale type then following actions are performed:
 * 1. allowed Min/Max are updated
 * 2. model is updated
 * 3. slider is recalculated
 * Note: Changing scale type is kind of setter and is not considered as User Action! So no event is fired here... (even if value has changed!)
 *
 * ValueChangedAction (UserAction) is fired when:
 * 1. userVal is changed while user slides slider (if submitOnAdjusting == true)
 * 2. userVal is changed after user stopped adjusting (if submitOnAdjusting == false)
 * 3. userVal is changed after user entered new value in valNF
 * 4. userVal is changed after user entered new value in maxNF or minNF and then valNF was adjusted to range
 *
 * @author Krzysztof S. Nowinski, University of Warsaw, ICM
 * @reinventedBy szpak
 */
//TODO: test number of runs in recalculateSlider/ifNeeded while init slider
//TODO: change numeric fields formatting (from scientific one) - now for log range 999999....999999.1 no change can be seen in numeric field
public class ExtendedSlider extends javax.swing.JPanel {

//    private static final Logger LOGGER = Logger.getLogger(ExtendedSlider.class);
    //is logarithmic or linear scale (default)
    private ScaleType scaleType = ScaleType.LINEAR;
//    private ScaleType scaleType = ScaleType.LOGARITHMIC; //TODO: change it back - temporaly switched to logarithmic
    //bounds of the slider (depend on scale)
    private float allowedLogarithmicMin = (float) Math.pow(10, Math.floor(Math.log10(Float.MIN_VALUE * 100))); //100 as an error margin
    private float allowedLogarithmicMax = (float) Math.pow(10, Math.floor(Math.log10(Float.MAX_VALUE / 100))); //100 as an error margin
    private float allowedLinearMin = (float) -Float.MAX_VALUE / 100; //100 as an error margin
    private float allowedLinearMax = (float) Float.MAX_VALUE / 100; //100 as an error margin
    //allowed min/max values (to type into minField/maxField)
    //TODO: test arithmetic on numbers close to infinity/0
    //TODO: create int version
    private float allowedMin = allowedLinearMin;//(float) Math.pow(10, Math.floor(Math.log10(Float.MIN_VALUE * 100))); //100 as an error margin
    private float allowedMax = allowedLinearMax;//(float) Math.pow(10, Math.floor(Math.log10(Float.MAX_VALUE / 100))); //100 as an error margin
    //model: userMin, userMax, userVal
    //current min/max values (passed by user or in setters)
    private float userMin = 0.001f;
    private float userMax = 100;//100.f;
    //current value passed by user or in setter
    //in fact userVal and this slider val should be equal
    private float userVal = 2;
    //slider range which should in general include user range (one needs to consider epsilon case, though)
    //this is visible slider range to the user (for userRange 20 - 5000, slider range will be 10 - 10000)
    private float sliderMin = 0.001f;
    private float sliderMax = 100;
    //numeric field font
    private Font textFieldFont;
    //flag if slider should do any action in sliderStateChanged (this is to distinguish between user action and setters/update)
    private boolean jsliderActive = true;
    private boolean showingFields = true;
    private Hashtable<Integer, JLabel> labels = new Hashtable<Integer, JLabel>();
    //
    //previous (correct) text values of min/max/val fields - this is to remember user specific format (typed by hand)
//    private String minFieldCorrect = "";
//    private String maxFieldCorrect = "";
//    private String valFieldCorrect = "";

    public ExtendedSlider() {
        initComponents();
        //set default font (here because in designer just setFont was used - this can cause problems in OOP when inheriting component)
        super.setFont(new java.awt.Font("Dialog", 0, 8));
        //update text field font to default
        setTextFieldFont(new java.awt.Font("Dialog", 0, 10));
        //TODO: try to solve it in better way (now this font update is not visible in NB Gui designer)
        slider.setFont(super.getFont());
        //TODO: set default: userVal = sliderMin, sliderMin = Float.MIN, sliderMax = Float.MAX to force module developer to set proper initialization value on this slider
        userVal = 2;
        userMin = sliderMin;
        userMax = sliderMax;
        //remember "previous" correct numbers
//                minCorrect = userMin;
//                maxCorrect = userMax;
//                valCorrect = userVal;

        validateModelAndUpdateUI(RangeChangeSource.NONE, false);
        updateTextFields(true, true, true);
        //recalculate and update thumb
        recalcSlider();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT
     * modify this code. The content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        slider = new javax.swing.JSlider();
        showFieldsBox = new javax.swing.JCheckBox();
        maxNF = new pl.edu.icm.visnow.gui.components.NumericTextField();
        valNF = new pl.edu.icm.visnow.gui.components.NumericTextField();
        minNF = new pl.edu.icm.visnow.gui.components.NumericTextField();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });
        addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                formAncestorAdded(evt);
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
        });
        setLayout(new java.awt.GridBagLayout());

        slider.setFont(new java.awt.Font("Dialog", 0, 8)); // NOI18N
        slider.setMajorTickSpacing(100);
        slider.setMaximum(1000);
        slider.setPaintLabels(true);
        slider.setPaintTicks(true);
        slider.setMinimumSize(new java.awt.Dimension(40, 42));
        slider.setPreferredSize(new java.awt.Dimension(60, 42));
        slider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderStateChanged(evt);
            }
        });
        slider.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                sliderComponentResized(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(slider, gridBagConstraints);

        showFieldsBox.setSelected(true);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pl/edu/icm/visnow/gui/widgets/widgetstrings"); // NOI18N
        showFieldsBox.setToolTipText(bundle.getString("GUI.enhancedmodeCB.tooltip")); // NOI18N
        showFieldsBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        showFieldsBox.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        showFieldsBox.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        showFieldsBox.setMargin(new java.awt.Insets(0, 5, 0, 0));
        showFieldsBox.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        showFieldsBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showFieldsBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(showFieldsBox, gridBagConstraints);

        maxNF.setFieldType(pl.edu.icm.visnow.gui.components.NumericTextField.FieldType.FLOAT);
        maxNF.setText("100");
        maxNF.setBackground(new java.awt.Color(238, 238, 238));
        maxNF.setBorder(null);
        maxNF.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        maxNF.setHorizontalAlignment(4);
        maxNF.setMinimumSize(new java.awt.Dimension(60, 14));
        maxNF.setPreferredSize(new java.awt.Dimension(60, 14));
        maxNF.addUserActionListener(new pl.edu.icm.visnow.gui.swingwrappers.UserActionListener() {
            public void valueChangedAction(java.util.EventObject evt) {
                maxNFValueChangedAction(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        add(maxNF, gridBagConstraints);

        valNF.setFieldType(pl.edu.icm.visnow.gui.components.NumericTextField.FieldType.FLOAT);
        valNF.setText("1");
        valNF.setBorder(null);
        valNF.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        valNF.setHorizontalAlignment(4);
        valNF.setMinimumSize(new java.awt.Dimension(60, 14));
        valNF.setPreferredSize(new java.awt.Dimension(60, 14));
        valNF.addUserActionListener(new pl.edu.icm.visnow.gui.swingwrappers.UserActionListener() {
            public void valueChangedAction(java.util.EventObject evt) {
                valNFValueChangedAction(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        add(valNF, gridBagConstraints);

        minNF.setFieldType(pl.edu.icm.visnow.gui.components.NumericTextField.FieldType.FLOAT);
        minNF.setText("0.001");
        minNF.setBackground(new java.awt.Color(238, 238, 238));
        minNF.setBorder(null);
        minNF.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        minNF.setHorizontalAlignment(4);
        minNF.setMinimumSize(new java.awt.Dimension(60, 14));
        minNF.setPreferredSize(new java.awt.Dimension(60, 14));
        minNF.addUserActionListener(new pl.edu.icm.visnow.gui.swingwrappers.UserActionListener() {
            public void valueChangedAction(java.util.EventObject evt) {
                minNFValueChangedAction(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        add(minNF, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

private void showFieldsBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showFieldsBoxActionPerformed
    showingFields = showFieldsBox.isSelected();
    showFields();
}//GEN-LAST:event_showFieldsBoxActionPerformed

    /**
     * Updates some of minField, maxField, valField to formatted values of userMin, userMax, userVal;
     * This method stores values as previous correct ones (used to undo).
     */
    private void updateTextFields(boolean updateMin, boolean updateMax, boolean updateVal) {
        if (scaleType == ScaleType.LOGARITHMIC) {
            if (updateMin) minNF.setValue(userMin, xformat(userMin));
            if (updateMax) maxNF.setValue(userMax, xformat(userMax));
            if (updateVal) valNF.setValue(userVal, xformat(userVal));
        } else if (scaleType == ScaleType.LINEAR) {
            String format = createTextFieldFormat();
            if (updateMin) minNF.setValue(userMin, format);
            if (updateMax) maxNF.setValue(userMax, format);
            if (updateVal) valNF.setValue(userVal, format);
        } else throw new RuntimeException("Incorrect scale type");
    }

    /**
     * Resets text fields to previous correct string value.
     * This method should use textual min/max/valFieldCorrect variables to restore previous value (but currently it uses user* which works pretty good - then only cons is to that user format will be lost on reverting field (val out of range))
     *
     * @param resetMin true if minField should be reverted to previous correct value
     * @param resetMax true if maxField should be reverted to previous correct value
     * @param resetVal true if valField should be reverted to previous correct value
     */
    private void resetTextFields(boolean resetMin, boolean resetMax, boolean resetVal) {
        //TODO: (important) such setting will change value of the field (so user value - fix it by remembering minCorrect/maxCorrect/valCorrect not string)
        //this can cause serious bug (setting text can set different value than current user value)
        //TODO: maybe this is not necessary (remembering minCorrect) - this is only reset of text field, so model should be correct at this time!
        //TODO: actually we need remembering text fields - this is needed to remember user text format! (probably needs fix in NumericTextField)
        //now fixed with setting real number, not texts

//        if (resetMin) minNF.setValue(minCorrect, xformat(minCorrect));
//                //Text(minFieldCorrect);
//        if (resetMax) maxNF.setValue(maxCorrect, xformat(maxCorrect));//minNF.setText(maxFieldCorrect);
//        if (resetVal) valNF.setValue(valCorrect, xformat(valCorrect));//minNF.setText(valFieldCorrect);

//        if (resetMin) minNF.setValue(userMin, xformat(userMin));
//        //Text(minFieldCorrect);
//        if (resetMax) maxNF.setValue(userMax, xformat(userMax));//minNF.setText(maxFieldCorrect);
//        if (resetVal) valNF.setValue(userVal, xformat(userVal));//minNF.setText(valFieldCorrect);


        if (scaleType == ScaleType.LOGARITHMIC) {
            if (resetMin) minNF.setValue(userMin, xformat(userMin));
            if (resetMax) maxNF.setValue(userMax, xformat(userMax));
            if (resetVal) valNF.setValue(userVal, xformat(userVal));
        } else if (scaleType == ScaleType.LINEAR) {
            String format = createTextFieldFormat();
            if (resetMin) minNF.setValue(userMin, format);
            if (resetMax) maxNF.setValue(userMax, format);
            if (resetVal) valNF.setValue(userVal, format);
        } else throw new RuntimeException("Incorrect scale type");
    }

    //TODO: find out best formatting (text field / labels)
    /**
     * Creates best text field format based on userMin / userMax.
     * //TODO: should be based on slider width (and scaleType) too.
     *
     * @return
     */
    private String createTextFieldFormat() {
        if (userMax == userMin) return "%4.4f";
        //TODO: format based on slider width        
        float formatDensity = (userMax - userMin) / 1000;
        if (formatDensity >= 1) return "%.0f";
        else return "%3." + (int) (1 - Math.log10(formatDensity)) + "f";
    }

private Float previousSlidedValue = null;
    
private void sliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderStateChanged
    if (jsliderActive && slider.getMaximum() != slider.getMinimum()) { //if user action
        float prevUserVal = userVal;

        double sliderPosNorm = (double) (slider.getValue() - slider.getMinimum()) / (slider.getMaximum() - slider.getMinimum());

        if (scaleType == ScaleType.LOGARITHMIC)
            userVal = (float) Math.pow(10, Math.log10(sliderMin) + sliderPosNorm * Math.log10(sliderMax / sliderMin));
        else if (scaleType == ScaleType.LINEAR)
            userVal = sliderMin + (float) sliderPosNorm * (sliderMax - sliderMin);
        else throw new RuntimeException("Incorrect scale type");


        //if slider at min/max position then userVal = userMin/userMax (this is necessary for sliders without overhead - like linear version without ticks)
        if (slider.getValue() == slider.getMinimum()) userVal = userMin;
        if (slider.getValue() == slider.getMaximum()) userVal = userMax;

        validateModelAndUpdateUI(RangeChangeSource.NONE, false);
        updateTextFields(false, false, true);

        
        
        //TODO: fix it ! this simple check does not work with adjusting (does not fire event when stop adjusting)
//        if (prevUserVal != userVal)
        fireStateChanged();
        
        //TODO: sliderStateChanged event has to be removed and UserActionListener (userAction) has to be added
        if ((!isAdjusting() || submitOnAdjusting) && !((Float)userVal).equals(previousSlidedValue)) {
            previousSlidedValue = userVal;
            fireValueChanged();
        }
                           
    }
}//GEN-LAST:event_sliderStateChanged

private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
    recalcSliderIfNeeded(RecalculateReason.RESIZE);
}//GEN-LAST:event_formComponentResized
    Map<RecalculateReason, Object> prevReason = new HashMap<RecalculateReason, Object>();

    /**
     * Recalculates slider only if reason-related data have changed. This is to avoid multiple recalculation (especially in resize-like events: componentResized, ancestorAdded).
     * That was first added because componentResized was no fired in VolumeSegmentation so other componentEvent listeners were added which led to duplication (call for recalculation for same slider width).
     */
    private void recalcSliderIfNeeded(RecalculateReason reason) {
//        LOGGER.debug(reason);
        switch (reason) {
            case FONT_CHANGED:
                if (!super.getFont().equals(prevReason.get(reason))) {
                    prevReason.put(reason, super.getFont());
                    recalcSlider();
                }
                break;
            case RESIZE:
//        LOGGER.info("" + slider.getWidth());
                if (!((Integer) slider.getWidth()).equals(prevReason.get(reason))) {
                    prevReason.put(reason, (Integer) slider.getWidth());
                    recalcSlider();
                }
                break;
            case MODEL_CHANGED:
                float[] minMax = (float[]) prevReason.get(reason);
                if (minMax == null || minMax[0] != userMin || minMax[1] != userMax) {
                    prevReason.put(reason, new float[]{userMin, userMax});
                    recalcSlider();
                }
                break;
            case SCALE_TYPE_CHANGED:
                if (!scaleType.equals(prevReason.get(reason))) {
                    prevReason.put(reason, scaleType);
                    recalcSlider();
                }
                break;
        }
    }

    /**
     * FontMetrics changed, model (userMin/userMax) changed, slider width changed, scale type (logarithmic/linear) changed;
     * This reason doesn't really mean that this particular property has changed. But possibly changed - and it's tested in
     * recalculateIfNeeded.
     */
    private enum RecalculateReason {

        FONT_CHANGED, MODEL_CHANGED, RESIZE, SCALE_TYPE_CHANGED;
    }

    public enum ScaleType {

        LINEAR, LOGARITHMIC;
    }

    private void formAncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_formAncestorAdded
        recalcSliderIfNeeded(RecalculateReason.RESIZE);
    }//GEN-LAST:event_formAncestorAdded

    private void sliderComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_sliderComponentResized
        recalcSliderIfNeeded(RecalculateReason.RESIZE);
    }//GEN-LAST:event_sliderComponentResized

    private void maxNFValueChangedAction(java.util.EventObject evt) {//GEN-FIRST:event_maxNFValueChangedAction
        float prevUserVal = userVal;
        userMax = (Float) maxNF.getValue();
        //TODO: user value can change when reverting from text

        //updates correct value (to undo) (it will be updated in validateModel if not in range)
//            maxFieldCorrect = maxNF.getText();

        validateModelAndUpdateUI(RangeChangeSource.MAX, true);
//        recalculateSlider();
        recalcSliderIfNeeded(RecalculateReason.MODEL_CHANGED);

        //fire state changed if userVal was changed (adjusted to range) (because it's fired by user action - type into max field)
        if (prevUserVal != userVal) {
            fireStateChanged(); //TODO: remove this, add getInternalSlider
            fireValueChanged(); //real value change -> notify listeners
        }
    }//GEN-LAST:event_maxNFValueChangedAction

    private void minNFValueChangedAction(java.util.EventObject evt) {//GEN-FIRST:event_minNFValueChangedAction
        float prevUserVal = userVal;
        userMin = (Float) minNF.getValue();
        //TODO: user value can change when reverting from text

        //updates correct value (to undo user text value) (it will be updated in validateModel if not in range)
//        minFieldCorrect = minNF.getText();

        validateModelAndUpdateUI(RangeChangeSource.MIN, true);
//        recalculateSlider();
        recalcSliderIfNeeded(RecalculateReason.MODEL_CHANGED);

        //fire state changed if userVal was changed (adjusted to range) (because it's fired by user action - type into max field)
        if (prevUserVal != userVal) {
            fireStateChanged(); //TODO: remove this, add getInternalSlider
            fireValueChanged(); //real value change -> notify listeners
        }
    }//GEN-LAST:event_minNFValueChangedAction

    private void valNFValueChangedAction(java.util.EventObject evt) {//GEN-FIRST:event_valNFValueChangedAction

        float prevUserVal = userVal;
        userVal = (Float) valNF.getValue();

        //might be split to validateModel and later updateTextFields + updateThumb
        validateModelAndUpdateUI(RangeChangeSource.NONE, true);

//        updates correct value (to undo) (it will be updated in validateModel if not in range)
//        valFieldCorrect = valNF.getText();

        //updates slider (it may be not updated in validateModel)
        updateSliderThumb();

        //fire state changed if userVal was changed (by user or adjusted to range)
        if (prevUserVal != userVal) {
            fireStateChanged(); //TODO: remove this, add getInternalSlider
            fireValueChanged(); //real value change -> notify listeners
        }

    }//GEN-LAST:event_valNFValueChangedAction

//TODO: add tool tip over min/max/val fields (especially val field - when set to sliderMax/Min -> userMax/userMin)
    /**
     * Recalculates slider visual range, slider step, slider ticks, slider labels and updates thumb position.
     *
     * Assumes that model is correct so allowedMin &le; userMin &le; userVal &le; userMax &le; allowedMax.
     * Particularly it can be userMin == userMax.
     *
     * This method does not change userMax/Min/Val values nor text fields. It sets slider range/labels/ticks & updates thumb (passive - no event fired).
     */
    private void recalcSlider() {
        //assume: model is always correct
        if (userMin > userMax || userVal < userMin || userVal > userMax) {
            throw new RuntimeException("There should be no such case!");
        }

//        LOGGER.debug("JSliderMin/Max: " + slider.getMinimum() + "/" + slider.getMaximum() + "SliderMin/Max: " + sliderMin + "/" + sliderMax + " UserMin/Val/Max: " + userMin + "/" + userVal + "/" + userMax);

        //turn off slider events (sliderStateChanged is fired in JSlider setters by default)
        jsliderActive = false;
        slider.setPaintLabels(false);
        slider.setPaintTicks(false);

        int sliderWidth = slider.getWidth();

        if (sliderWidth == 0) { //just in case as a separate case
            slider.setMinimum(0);
            slider.setMaximum(0);
        } else if (userMin == userMax) { //this can be true because it can be set like this in validateModel
            slider.setMinimum(0);
            slider.setMaximum(0);
            //TODO: create method for no 10-based labels (used when userMin == userMax)
            // and in following cases
            //TODO: if powerMin + 1 == powerMax AND range width is less then 30% of one power then non 10-based slider should be made with 0 - 2 labels?
            //TODO: consider cases like min/max = 10^k -/+ epsilon
        } else {
            LabelTickSet[] ltsList;

            if (scaleType == ScaleType.LOGARITHMIC) {
                //TODO: add relative accuracy (here log10(0.01) < -2 which gives floor == -3)
                //similar problem with 0.1 (ceil gives 1)
                //so round to nearest 10 power if difference is less then 1 px ??
                //(but be carefull because for 0.99 and 1.01 we don't want to show 0.1 - 10, but we don't want to show 1 - 1 neither! 
                // so probably this is also the case when 0.99 - 1.01 should be used or different labels creator should be used 
                // like log10 + delta (but this is problematic again )
                //TODO: if rounded to nearest 10 power then add behaviour that if slider at min/max position than value is userMin/Max
                int powerMin = (int) Math.floor(Math.log10(userMin));
                int powerMax = (int) Math.ceil(Math.log10(userMax));

                sliderMin = (float) Math.pow(10.0, powerMin);
                sliderMax = (float) Math.pow(10.0, powerMax);

                //TODO: if powerMin + 1 == powerMax AND range width is less then 30% of one power then non 10-based slider should be made with 0 - 2 labels?
                //TODO: consider cases like min/max = 10^k -/+ epsilon

//                    LOGGER.debug(" 10^" + powerMin + " <= " + userMin + " <= " + userVal + " <= " + userMax + " <= 10^" + powerMax);

                ltsList = LabelGenerator.createPossibleLog10Labels(powerMin, powerMax);

            } else if (scaleType == ScaleType.LINEAR) {
                sliderMin = userMin;
                sliderMax = userMax;

                ltsList = LabelGenerator.createPossibleLinearLabels(userMin, userMax); //labels with no overhead - no ticks.
            } else throw new RuntimeException("Incorrect scale type");
//                    LOGGER.debug(Arrays.toString(ltsList));

            FontMetrics fontMetrics = slider.getFontMetrics(super.getFont());

            //if proper label set was found (if empty label set was always in ltsList than this flag is not necessary - but there is no such assumption)
            boolean labelled = false;

//            LOGGER.debug(Math.round(0.01 * sliderWidth) + " " + Math.round(Math.min(20, Math.max(2, Math.round(0.01 * sliderWidth)))));

            //TODO: combine LabelTickSet with LabelGenerator (and move this code below into LabelGenerator?)
            //Traverse all possible label-tick sets and draw first for which is enough space to draw
            for (LabelTickSet labelTickSet : ltsList)
                //TODO: blinking lables now - needs to be solved in different way 
                //TODO: jumb by 2 may solve the problem - test it
                //TODO: No it doesn't - make visibility of one label set monotonic in relation to sliderWidth (so once enough space for one width then enough space for all higher width)
                // so the problem is that minimumDistance in percents grows too fast
                //possibly should be fixed by adding minimumDistancePc which will be calculated later taking into account both sliderWidth and totalLabelNum + jump by 2 ?
                if (labelTickSet.isEnoughSpace(sliderWidth, fontMetrics, (int) Math.round(Math.min(20, Math.max(2, Math.round(0.01 * sliderWidth)))))) {
                    labelled = true;

                    labels.clear();
                    int gridPositions = labelTickSet.getTotalGridPositions();

                    //precise density matters to have precision at least of 1 px size
                    //additionally it's better if it's not much larger then 1px to get proper behaviour when clicking on slider track
                    int gridDensity = 1;
                    if (labelTickSet.getTotalGridBins() > 0)
                        gridDensity = (int) Math.ceil((double) (sliderWidth - 1) / labelTickSet.getTotalGridBins());

                    //assign backed JSlider range
                    slider.setMinimum(0);
                    slider.setMaximum((gridPositions - 1) * gridDensity);

                    for (int i = 0; i < labelTickSet.getTotalLabelNum(); i++) {
                        JLabel lbl = new JLabel(labelTickSet.getLabels()[i]);
                        //TODO: test tool tips here
                        lbl.setFont(super.getFont()); //need to set same font as parent font
                        labels.put(gridDensity * labelTickSet.getLabelPositions()[i], lbl);
                    }

                    if (labelTickSet.getTotalLabelNum() > 0) {
                        slider.setLabelTable(labels);
                        slider.setPaintLabels(true);
                    }

//                    LOGGER.info(labelTickSet.getMaximumLabelWidth(fontMetrics));

                    //minimum distance 2px for minor ticks works just fine, but it could be done as well dependent on maximum label width 
                    //(lower for smaller labels - like for range 1-10)
                    //calculate slider width after drawing labels (left space for half max label width on the left and right of a slider)
                    int majorTickSpacing = labelTickSet.getBestTickSpacing(true, sliderWidth - labelTickSet.getMaximumLabelWidth(fontMetrics) / 2 * 2, 6);
                    int minorTickSpacing = labelTickSet.getBestTickSpacing(false, sliderWidth - labelTickSet.getMaximumLabelWidth(fontMetrics) / 2 * 2, 2);

//                            LOGGER.debug("major ticks: " + majorTickSpacing + "  minor ticks: " + minorTickSpacing);
                    //and draw ticks 
//                            if (majorTickSpacing > 0) 
                    slider.setMajorTickSpacing(majorTickSpacing * gridDensity);
//                            if (minorTickSpacing > 0) 
                    slider.setMinorTickSpacing(minorTickSpacing * gridDensity);
                    if (majorTickSpacing > 0 || minorTickSpacing > 0) slider.setPaintTicks(true);

                    break; // do not process next label sets
                }

            if (!labelled) { //no labels case (just in case - empty label set should be in ltsList)
                slider.setMinimum(0);
                slider.setMaximum(0);
            }

//                    LOGGER.debug(Arrays.toString(ltsList));



            //width in px between 10^k and 10^(k+1)
//                    int widthPerPower = slider.getWidth() / (powerMax - powerMin);

            //slider values between 10^k and 10^(k+1) (including 10^k)
            //should be multiplicity of 3 

            updateSliderThumb();
        }
        jsliderActive = true;
    }

    /**
     * Updates slider thumb; This method assumes that sliderMin/sliderMax and range of backed JSlider are correctly set.
     * This method is not active! so sliderStateChanged is set as inactive while updating slider.
     */
    private void updateSliderThumb() {
//        LOGGER.debug("JSliderMin/Max: " + slider.getMinimum() + "/" + slider.getMaximum() + "SliderMin/Max: " + sliderMin + "/" + sliderMax + " UserMin/Val/Max: " + userMin + "/" + userVal + "/" + userMax);

        if (sliderMax == sliderMin) return;

        double posNormalized;

        if (scaleType == ScaleType.LOGARITHMIC) {
            double size = Math.log(sliderMax / sliderMin);
            double userPos = Math.log(userVal / sliderMin);
            posNormalized = userPos / size; //position 0 ... 1
        } else if (scaleType == ScaleType.LINEAR)
            posNormalized = (userVal - sliderMin) / (sliderMax - sliderMin);
        else throw new RuntimeException("Incorrect scale type");


        jsliderActive = false;
        slider.setValue((int) Math.round(posNormalized * (slider.getMaximum() - slider.getMinimum()) + slider.getMinimum()));
        jsliderActive = true;
    }

    @Override
    public void setFont(Font font) {
        super.setFont(font); //To change body of generated methods, choose Tools | Templates.
        if (slider != null) {
            slider.setFont(font); //does not fire state changed (OK)
            recalcSliderIfNeeded(RecalculateReason.FONT_CHANGED);
        }
    }

    public float getMin() {
        return userMin;
    }

    /**
     * Sets userMin and validates full range: allowedMin &le; userMin &le; userVal &le; userMax &le; allowedMax.
     * Recalculates slider.
     */
    public void setMin(float min) {
        userMin = min;
        //no fire event in setters
        validateModelAndUpdateUI(RangeChangeSource.MIN, true);
        //update min by hand (max and val will be updated automatically in validateModel)
        updateTextFields(true, false, false);
        recalcSliderIfNeeded(RecalculateReason.MODEL_CHANGED);
    }

    public float getMax() {
        return userMax;
    }

    /**
     * Sets userMax and validates full range: allowedMin &le; userMin &le; userVal &le; userMax &le; allowedMax.
     * Recalculates slider.
     */
    public void setMax(float max) {
        userMax = max;
        //no fire event in setters
        validateModelAndUpdateUI(RangeChangeSource.MAX, true);
        //update max by hand (min and val will be updated automatically in validateModel)
        updateTextFields(false, true, false);
        recalcSliderIfNeeded(RecalculateReason.MODEL_CHANGED);
    }

    public float getVal() {
        return userVal;
    }

    /**
     * Sets userVal and validates full range: allowedMin &le; userMin &le; userVal &le; userMax &le; allowedMax.
     * Does not recalculate slider (no necessary here).
     */
    public void setVal(float val) {
        //LOGGER.info("" + val);
        userVal = val;
        //no fire event in setters
        validateModelAndUpdateUI(RangeChangeSource.NONE, true);
        //update val by hand (min and max will be updated automatically in validateModel)
        updateTextFields(false, false, true);
        updateSliderThumb();
    }

    /**
     * Sets userMin, userMax, userVal and validates full range: allowedMin &le; userMin &le; userVal &le; userMax &le; allowedMax.
     * Recalculates slider.
     */
    public void setAll(float min, float max, float val) {
        userMin = min;
        userMax = max;
        userVal = val;
        //no fire event in setters
        validateModelAndUpdateUI(RangeChangeSource.NONE, true);
        updateTextFields(true, true, true);
        recalcSliderIfNeeded(RecalculateReason.MODEL_CHANGED);
    }

    private enum RangeChangeSource {

        MIN, MAX, NONE;
    }

    /**
     * Validates userMin, userVal, userMax to meet: allowedMin &le; userMin &le; userVal &le; userMax &le; allowedMax;
     * Updates UI: text fields + thumb (does not recalculate slider).
     * Works in order:
     * <ol>
     * <li> sets userMin and userMax to meet allowedMin &le; userMin, userMax &le; allowedMax.
     * <li> sets userMin and userMax to meet userMin &le; userMax
     * <li> sets userVal to be between userMin and userMax
     * <li> update UI (calls updateUIValues)
     * </ol>
     * If changeSource == MIN || changeSource == NONE then userMax is updated to be &ge; userMin
     * If changeSource == MAX then userMin is updated to be &lt; userMax
     * On any change corresponding text field is updated (+ slider thumb if necessary). UI is updated ONLY ON CHANGE! so slider won't be updated
     * if value is changed but it's within range, so does not need to be adjusted to range.
     */
    private void validateModelAndUpdateUI(RangeChangeSource changeSource, boolean updateThumb) {
        boolean updateMin = false;
        boolean updateMax = false;
        boolean updateVal = false;
        if (userMin < allowedMin) {
            userMin = allowedMin;
            updateMin = true;
        }
        if (userMax > allowedMax) {
            userMax = allowedMax;
            updateMax = true;
        }

        if (userMin > userMax) {
            if (changeSource == RangeChangeSource.MAX) {
                userMin = userMax;
                updateMin = true;
            } else {
                userMax = userMin;
                updateMax = true;
            }
        }

        if (userVal > userMax) {
            userVal = userMax;
            updateVal = true;
        }

        if (userVal < userMin) {
            userVal = userMin;
            updateVal = true;
        }

        if (updateThumb && updateVal) updateSliderThumb();
        updateTextFields(updateMin, updateMax, updateVal);
    }

    public void setTextFont(Font textFont) {
        textFieldFont = textFont;
        setTextFieldFont(textFont);
    }

    private void setTextFieldFont(Font textFont) {
        maxNF.setFont(textFont);
        valNF.setFont(textFont);
        minNF.setFont(textFont);
    }

    public Font getTextFont() {
        return textFieldFont;
    }

    public boolean isAdjusting() {
        return slider.getValueIsAdjusting();
    }

    public boolean isShowingFields() {
        return showingFields;
    }

    public void setShowingFields(boolean showingFields) {
        this.showingFields = showingFields;
        showFieldsBox.setSelected(showingFields);
        showFields();
    }

    public void showFields() {
        minNF.setVisible(showingFields);
        maxNF.setVisible(showingFields);
        valNF.setVisible(showingFields);
        this.validate();
    }

    @Override
    public void setEnabled(boolean enabled) {
        slider.setEnabled(enabled); //does not fire state changed (OK)
        minNF.setEnabled(enabled);
        maxNF.setEnabled(enabled);
        valNF.setEnabled(enabled);
        showFieldsBox.setEnabled(enabled);
    }

    public ScaleType getScaleType() {
        return scaleType;
    }

    public void setScaleType(ScaleType scaleType) {
        boolean change = scaleType != this.scaleType;
        this.scaleType = scaleType;
        if (change) {
            if (scaleType == ScaleType.LINEAR) {
                allowedMax = allowedLinearMax;
                allowedMin = allowedLinearMin;
            } else if (scaleType == ScaleType.LOGARITHMIC) {
                allowedMax = allowedLogarithmicMax;
                allowedMin = allowedLogarithmicMin;
            } else throw new RuntimeException("Incorrect scale type");

            validateModelAndUpdateUI(RangeChangeSource.NONE, true);
        }
        //there is change testing in here anyway
        recalcSliderIfNeeded(RecalculateReason.SCALE_TYPE_CHANGED);
    }
//    /**
//     * Formats string to put into TextField (min/max/val). This should take into consideration also int/float version of this slider.
//     */
//    public String formatText(float value) {
//        return String.format(xformat(value), value);
//    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private pl.edu.icm.visnow.gui.components.NumericTextField maxNF;
    private pl.edu.icm.visnow.gui.components.NumericTextField minNF;
    private javax.swing.JCheckBox showFieldsBox;
    private javax.swing.JSlider slider;
    private pl.edu.icm.visnow.gui.components.NumericTextField valNF;
    // End of variables declaration//GEN-END:variables
    /**
     * Utility field holding list of ChangeListeners.
     */
    private transient ArrayList<ChangeListener> changeListenerList = new ArrayList<ChangeListener>();

    //TODO: remove this, add getInternalSlider
    /**
     * Registers ChangeListener to receive events.
     *
     * @param listener The listener to register.
     */
    public synchronized void addChangeListener(ChangeListener listener) {
        changeListenerList.add(listener);
    }

    /**
     * Removes ChangeListener from the list of listeners.
     *
     * @param listener The listener to remove.
     */
    public synchronized void removeChangeListener(ChangeListener listener) {
        if (changeListenerList != null) {
            changeListenerList.remove(listener);
        }
    }

    /**
     * Notifies all registered listeners about the event.
     *
     * @param object Parameter #1 of the <CODE>ChangeEvent<CODE> constructor.
     */
    private void fireStateChanged() {
//        LOGGER.debug(userVal);
        java.util.ArrayList list;
        ChangeEvent e = new ChangeEvent(this);
        synchronized (this) {
            if (changeListenerList == null) {
                return;
            }
            list = new ArrayList<ChangeListener>(changeListenerList);
//         list = (ArrayList<ChangeListener>)changeListenerList.clone ();
        }
        for (int i = 0; i < list.size(); i++) {
            ((ChangeListener) list.get(i)).stateChanged(e);
        }
    }
    
    //by default submit every single change of value
    private boolean submitOnAdjusting = true;

    /**
     * Indicates if value changed should be fired when user adjusts this slider. If not then valueChanged is fired after stop adjusting.
     */
    public boolean isSubmitOnAdjusting() {
        return submitOnAdjusting;
    }

    /**
     * Indicates if value changed should be fired when user adjusts this slider. If not then valueChanged is fired after stop adjusting.
     */
    public void setSubmitOnAdjusting(boolean submitOnAdjusting) {
        this.submitOnAdjusting = submitOnAdjusting;
    }

    /**
     * Notifies all UserActionListeners about valueChangedAction.
     */
    private void fireValueChanged() {
//        LOGGER.debug("New value: " + userVal);
        for (UserActionListener listener : userActionListeners)
            listener.valueChangedAction(new EventObject(this));
    }
    private List<UserActionListener> userActionListeners = new ArrayList<UserActionListener>();

    public void addUserActionListener(UserActionListener listener) {
        userActionListeners.add(listener);
    }

    public void removeUserActionListener(UserActionListener listener) {
        userActionListeners.remove(listener);
    }

//    public static void main(String[] a) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
////        try {
////            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
////                if ("Nimbus".equals(info.getName())) {
////                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
////                    break;
////                }
////            }
////        } catch (Exception ex) {
////            
////            //java.util.logging.Logger.getLogger(TestFrame2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
////        }
//        //</editor-fold>
//
//        VisNow.initLogging(true);
//        Locale.setDefault(VisNow.LOCALE);
//        JFrame f = new JFrame();
//        f.add(new ExtendedSlider());
//        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        f.setLocation(600, 300);
//        f.pack();
//        f.setVisible(true);
//    }
}
