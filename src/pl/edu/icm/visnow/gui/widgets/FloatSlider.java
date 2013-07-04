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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Locale;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.log4j.Logger;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;
import pl.edu.icm.visnow.system.main.VisNow;

/**
 * This is extended version of standard Slider with following features:
 * - additional input fields for min, max and current value (switchable with checkbox or hidden)
 * - slider label/tick marks.
 * 
 * This slider tries to be "smart" and it tries to: 
 * - label the slider with multiplicity of numbers like 1,2,5,10,20,50, and so on
 * - create proper corresponding tick marks
 * - makes each slider step of the same length (even if slider range is difficult to handle e.g. 
 *      7..10000  - this will be mapped to 0..10000 with step like 50 or 100).
 * - trims min and max to reflect min and max input by user.
 *
 * Layout computation remark:
 *  This component's preferredSize is calculated using preferredSize of slider, checkbox and input text fields but
 *  inputs and slider have fixed preferredSize to avoid changing size of component and repacking it along with 
 *  all parent containers.
 * 
 * @author Krzysztof S. Nowinski, University of Warsaw, ICM
 * 
 */
public class FloatSlider extends javax.swing.JPanel implements Serializable, MouseListener {

    private static final Logger LOGGER = Logger.getLogger(FloatSlider.class);
    protected float min = 0f;
    protected float max = 1f;
    protected float val = 0.5f;
    protected float rMin = 0.f;
    protected float rMax = 1.f;
    protected float delta = .001f;
    protected int l = 0;
    protected int u = 1000;
    protected int ld = 100;
    protected int dec = 4;
    protected int ddec = 3;
    protected String form = "%" + dec + "." + ddec + "f";
    protected String lform = "%" + (dec - 1) + "." + (ddec - 1) + "f";
    protected Font textFont = new java.awt.Font("Dialog", 0, 10);
    protected boolean active = true;
    protected boolean adjusting = true;
    protected boolean showingFields = true;
    private Hashtable<Integer, JLabel> labels = new Hashtable<Integer, JLabel>();

    /**
     * Creates new form FloatSlider
     */
    public FloatSlider() {
        initComponents();
        //setText in initComponents doesn't initiate proper flow
        //so let's initiate it using setters which propagate the flow automaticaly
        setMinMax(min,max);
        setVal(val);
        
        for (int i = 0; i + l <= u; i += ld) {
            JLabel lbl = new JLabel(String.format(lform, rMin + i * delta));
            lbl.setFont(getFont());
            labels.put(new Integer(i + l), lbl);
        }
        slider.setLabelTable(labels);
        slider.addMouseListener(this);
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        slider = new javax.swing.JSlider();
        maxField = new pl.edu.icm.visnow.gui.components.FloatFormattedTextField();
        valField = new pl.edu.icm.visnow.gui.components.FloatFormattedTextField();
        minField = new pl.edu.icm.visnow.gui.components.FloatFormattedTextField();
        showFieldsBox = new javax.swing.JCheckBox();

        setFont(new java.awt.Font("Dialog", 0, 8)); // NOI18N
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });
        setLayout(new java.awt.GridBagLayout());

        slider.setFont(new java.awt.Font("Dialog", 0, 8)); // NOI18N
        slider.setMajorTickSpacing(100);
        slider.setMaximum(1000);
        slider.setMinorTickSpacing(20);
        slider.setPaintLabels(true);
        slider.setPaintTicks(true);
        slider.setMinimumSize(new java.awt.Dimension(40, 42));
        slider.setPreferredSize(new java.awt.Dimension(127, 42));
        slider.setRequestFocusEnabled(false);
        slider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        add(slider, gridBagConstraints);

        maxField.setBackground(new java.awt.Color(238, 238, 238));
        maxField.setBorder(null);
        maxField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        maxField.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        maxField.setMinimumSize(new java.awt.Dimension(60, 14));
        maxField.setPreferredSize(new java.awt.Dimension(60, 14));
        maxField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maxFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(maxField, gridBagConstraints);

        valField.setBorder(null);
        valField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        valField.setToolTipText("");
        valField.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        valField.setMinimumSize(new java.awt.Dimension(60, 14));
        valField.setPreferredSize(new java.awt.Dimension(60, 14));
        valField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                valFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(valField, gridBagConstraints);

        minField.setBackground(new java.awt.Color(238, 238, 238));
        minField.setBorder(null);
        minField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        minField.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        minField.setMinimumSize(new java.awt.Dimension(60, 14));
        minField.setPreferredSize(new java.awt.Dimension(60, 14));
        minField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                minFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(minField, gridBagConstraints);

        showFieldsBox.setSelected(true);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pl/edu/icm/visnow/gui/widgets/widgetstrings"); // NOI18N
        showFieldsBox.setToolTipText(bundle.getString("GUI.enhancedmodeCB.tooltip")); // NOI18N
        showFieldsBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        showFieldsBox.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        showFieldsBox.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        showFieldsBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
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
        add(showFieldsBox, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

private void showFieldsBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showFieldsBoxActionPerformed
    showingFields = showFieldsBox.isSelected();
    showFields();
}//GEN-LAST:event_showFieldsBoxActionPerformed

    /**
     * Sets valField for current value of val.
     */
    private void valFieldUpdate() {
        valField.setText(String.format(form, val));
    }
    
    /**
     * Gets current value in valField
     */
    private float valFieldGet() {
        return Float.parseFloat(valField.getText());        
    }

    /**
     * Sets maxField for current value of max.
     */
    private void maxFieldUpdate() {
        maxField.setText(String.format(form, max));
    }

    /**
     * Gets current value in maxField
     */
    private float maxFieldGet() {
        return Float.parseFloat(maxField.getText());        
    }

    /**
     * Sets minField for current value of min.
     */
    private void minFieldUpdate() {
        minField.setText(String.format(form, min));
    }

    /**
     * Gets current value in minField
     */
    private float minFieldGet() {
        return Float.parseFloat(minField.getText());
    }

private void minFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_minFieldActionPerformed
    LOGGER.trace("active: " + active);
    if (!active) return; //return if passive 

    //1. get value
    float minNew = minFieldGet();
    //2. call active setter (validate values, update controls, call listeners) 
    setMin(minNew, true, true);
    }//GEN-LAST:event_minFieldActionPerformed

private void maxFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maxFieldActionPerformed
    LOGGER.trace("active: " + active);
    if (!active) return; //return if passive 

    //1. get value      
    float maxNew = maxFieldGet();
    //2. call active setter (validate values, update controls, call listeners) 
    setMax(maxNew, true, true);
}//GEN-LAST:event_maxFieldActionPerformed

private void valFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_valFieldActionPerformed
    LOGGER.trace("active: " + active);
    if (!active) return; //return if passive 

    //1. get value
    float valNew = valFieldGet();
    //2. call active setter (validate values, update controls, call listeners) 
    setVal(valNew, true, true);
}//GEN-LAST:event_valFieldActionPerformed

private void sliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderStateChanged
    LOGGER.trace("active: " + active);
    if (!active) return; //return if passive 

    //1. get value
    float valNew = 0;
    if (slider.getValue() == slider.getMinimum()) valNew = min;
    else if (slider.getValue() == slider.getMaximum()) valNew = max;
    else valNew = rMin + slider.getValue() * delta;

    LOGGER.trace("slider min/max/new: " + slider.getMinimum() + " " + slider.getMaximum() + " " + slider.getValue() + " min/max/cur/new: " + min + " " + max + " " + val + " :" + valNew);

    //2. call active setter (validate values, update controls, call listeners) 
    setVal(valNew, true, false);
}//GEN-LAST:event_sliderStateChanged

private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
    active = false;
    float v = val;
    sliderUpdateRange();
    val = v;
    slider.setValue((int) ((val - rMin) / delta));
    active = true;

    LOGGER.trace(this.getWidth() + "x" + this.getHeight() + " slider:" + this.slider.getWidth());
    LOGGER.trace("preferredSize: " + this.getPreferredSize() + " minimumSize: " + this.getMinimumSize());
}//GEN-LAST:event_formComponentResized

    /**
     * Sets slider for current value of val.
     */
    private void sliderUpdateThumb() {
        LOGGER.trace("rmin/rmax/delta/value " + rMin + " " + rMax + " " + delta + " " + val);
        int sliderPos;
        if (val == max) sliderPos = slider.getMaximum();
        else if (val == min) sliderPos = slider.getMinimum();
        else sliderPos = Math.round((val - rMin) / delta);
        slider.setValue(sliderPos);
    }

    /**
     * Updates slider range/ticks/labels; Tries in smart way to: 
     * - label the slider with multiplicity of numbers like 1,2,5,10,20,50, and so on
     * - create proper corresponding tick marks
     * - makes each slider step of the same length (even if slider range is difficult to handle e.g 
     *      7..10000  - this will be mapped to 0..10000 with step like 50 or 100).
     * 
     * This method updates slider which results in sliderStateChanged.
     */
    private void sliderUpdateRange() {
        SwingInstancer.swingRun(new Runnable() {
            public void run() {
                double r = max - min;
                if (r <= 0)
                    r = 1;
                double logr = Math.log10(r);
                int iLogr = (int) (logr + 100) - 100;
                double mr = r / Math.pow(10., 1. * iLogr);
                int space = (slider.getWidth() - 30) / 20;
                if (space < 5)
                    space = 5;
                if (space > mr) {
                    mr *= 10;
                    iLogr -= 1;
                }
                mr /= space;
                if (mr < 2)
                    mr = 2;
                else if (mr < 5)
                    mr = 5;
                else
                    mr = 10;
                float d = (float) mr;
                if (iLogr > 0)
                    for (int i = 0; i < iLogr; i++)
                        d *= 10;
                if (iLogr < 0)
                    for (int i = 0; i > iLogr; i--)
                        d /= 10;
                float sMin = d * Math.round(min / d);
                if (sMin < min)
                    sMin += d;
                float sMax = d * Math.round(max / d);
                if (sMax > max)
                    sMax -= d;
                delta = d / ld;
                rMin = delta * Math.round(min / delta);
                if (rMin > min)
                    rMin -= delta;
                rMax = delta * Math.round(max / delta);
                if (rMax < max)
                    rMax += delta;
                labels.clear();
                int lScale = (int) ((sMin - rMin) / delta);
                u = (int) Math.round((rMax - rMin) / delta);
                ddec = 101 - (int) (Math.log10(delta) + 100);
                logr = Math.log10(Math.max(Math.abs(sMin), Math.abs(sMax)));
                iLogr = (int) (logr + 100) - 100;
                if (ddec < 0)
                    ddec = 0;
                if (iLogr > 0)
                    dec = iLogr + ddec + 2;
                else
                    dec = ddec + 2;
                dec = Math.max(Math.min(dec, 10),1);
                form = "%" + dec + "." + ddec + "f";
                int k = ddec - 2;
                if (k < 0)
                    k = 0;
                lform = "%" + (dec - 1) + "." + k + "f";
                for (int i = 0; i + lScale <= u; i += ld) {
                    JLabel lbl = new JLabel(String.format(lform, sMin + i * delta));
                    lbl.setFont(FloatSlider.this.getFont());
                    labels.put(new Integer(i + (int) ((sMin - rMin) / delta)), lbl);
                }
                if (labels.size() == 0) {
                    JLabel lbl = new JLabel(String.format(lform, rMin));
                    lbl.setFont(FloatSlider.this.getFont());
                    labels.put(0,lbl);
                }
                slider.setMaximum(u);
                slider.setLabelTable(labels);
                slider.repaint();
            }
        });
    }

    /**
     * Updates val to be in range [min;max].
     */
    private void validateValToRange() {
        if (val < min)
            val = min;
        if (val > max)
            val = max;
    }

    /**
     * Updates max to be >= min.
     */
    private void validateMaxToRange() {
        if (max < min)
            max = min;
    }

    /**
     * Updates min to be &lt;= max.
     */
    private void validateMinToRange() {
        if (min > max)
            min = max;
    }

    public float getMin() {
        return min;
    }

    //TODO: active == inUserMode ?? active may be considered as inUserMode (which is event is generated by user - e.g. when
    //user types number into valField then it is active event (but then other controls like slider have to be updated
    // - and this is passive event).
    
    
    /**
     * Passive setter for min value; new min value has to be less or equal to max
     * 1. switch to passive
     * 2. validates new min value and val value
     * 3. updates controls
     * 4. call fireStateChanged if necessary
     * 
     * @param minNew new value for min
     * @param onChangeEvent if true then fireStateChange is called on change.
     */
    private void setMin(float minNew, boolean onChangeEvent, boolean userAction) {
        //1. switch to passive
        boolean prevActive = active;
        active = false; //passive update

        float valOld = val;
        float minOld = min;

        //2. validate and 3. update controls
        min = minNew;
        validateMinToRange();
        validateValToRange();

        sliderUpdateRange();
        sliderUpdateThumb();

        //TODO: add functionality keep user format (there is some kind of swing bug - input fields are not refreshed and text partially disappears)
        if (!userAction || min != minFieldGet())
            minFieldUpdate();
//        if (val != valOld)//valFieldGet())
            valFieldUpdate();

        active = prevActive;

        //4. call listeners    
        if (onChangeEvent && val != valOld)
            fireStateChanged();
    }

 
    /**
     * Passive setter - equivalent to setMin(valNew,false)
     */
    public void setMin(float minNew) {
        setMin(minNew, false, false);
    }

    public float getMax() {
        return max;
    }

    /**
     * Setter for max value; new max value has to be greater or equal to min
     * 1. switch to passive
     * 2. validates new max value and val value
     * 3. updates controls
     * 4. call fireStateChanged if necessary
     * 
     * @param maxNew new value for max
     * @param onChangeEvent if true then fireStateChange is called on change.
     */
    private void setMax(float maxNew, boolean onChangeEvent, boolean userAction) {
        //1. switch to passive
        boolean prevActive = active;
        active = false; //passive update

        float valOld = val;
        float maxOld = max;

        //2. validate and 3. update controls
        max = maxNew;
        validateMaxToRange();
        validateValToRange();

        sliderUpdateRange();
        sliderUpdateThumb();

        //TODO: add functionality keep user format (there is some kind of swing bug - input fields are not refreshed and text partially disappears)
        if (!userAction || max != maxFieldGet())
            maxFieldUpdate();
//        if (val != valOld)// valFieldGet())
            valFieldUpdate();

        active = prevActive;

        //4. call listeners    
        if (onChangeEvent && val != valOld)
            fireStateChanged();
    }
    
    /**
     * Passive setter - equivalent to setMax(valNew,false)
     */
    public void setMax(float maxNew) {
        setMax(maxNew, false, false);
    }

    /**
     * Passive setter for min and max values; min has to be less or equal to max
     * 1. switch to passive
     * 2. validates new min, max and val value
     * 3. updates controls
     * 
     * @param maxNew new value for max 
     * @param minNew new value for min 
     */
    private void setMinMax(float minNew, float maxNew, boolean onChangeEvent) {
        //1. switch to passive
        boolean prevActive = active;
        active = false; //passive update

        float minOld = min;
        float maxOld = max;
        float valOld = val;

        //2. validate and 3. update controls
        min = minNew;
        max = maxNew;
        validateMinToRange();
        validateMaxToRange();
        validateValToRange();

        sliderUpdateRange();
        sliderUpdateThumb();

        //TODO: add functionality keep user format (there is some kind of swing bug - input fields are not refreshed and text partially disappears)
//        if (min != minOld)//minFieldGet())
            minFieldUpdate();
//        if (max != maxOld)//maxFieldGet())
            maxFieldUpdate();
//        if (val != valOld)//valFieldGet())
            valFieldUpdate();

        active = prevActive;
        
        //4. call listeners    
        if (onChangeEvent && val != valOld)
            fireStateChanged();        
    }

    /**
     * Passive setter - equivalent to setMinMax(valNew,false)
     */
    public void setMinMax(float minNew, float maxNew) {
        setMinMax(minNew,maxNew, false);
    }

    
    
    public float getVal() {
        return val;
    }

    /**
     * Setter for val value; new val value has to be in [min..max] range
     * 1. switch to passive
     * 2. validates new val value
     * 3. updates controls
     * 4. call fireStateChanged if necessary
     * 
     * @param valNew new value for val
     * @param onChangeEvent if true then fireStateChange is called on change.
     */
    private void setVal(float valNew, boolean onChangeEvent, boolean userAction) {
        LOGGER.trace("valNew/onChange: " + valNew + " " + onChangeEvent + " isEventDispatchThread: " + SwingUtilities.isEventDispatchThread());
        //1. switch to passive
        boolean prevActive = active;
        active = false; //passive update

        float valOld = val;

        //2. validate and 3. update controls
        val = valNew;
        validateValToRange();

        sliderUpdateThumb();

        //TODO: fix this: always update (because it won't update if valOld==valNew (after validation) but input field (before validation) is different e.g. 10,10,20 -> 10,5,20 (5 validated to 10 but we still have 5 in input field)
        //TODO: add functionality keep user format (there is some kind of swing bug - input fields are not refreshed and text partially disappears)        
        if (!userAction || val!=valFieldGet()) 
            valFieldUpdate(); //always update (because it won't update if valOld==valNew (after validation) but input field (before validation) is different e.g. 10,10,20 -> 10,5,20 (5 validated to 10 but we still have 5 in input field)

        active = prevActive;

        //4. call listeners    
        //always call listeners (e.g. onMouseUp)
        if (onChangeEvent)// && val != valOld)
            fireStateChanged();
    }

    /**
     * Passive setter - equivalent to setVal(valNew,false)
     * @param valNew 
     */
    public void setVal(float valNew) {
        setVal(valNew, false, false);
    }
    
    /**     
     * @deprecated try to avoid this (all setters are passive - no listeners are called/no events produced with the setter). Use setVal instead.
     */
    @Deprecated
    public void setValue(float val, boolean silent) {
        setVal(val, !silent, false);
    }

    public int getDec() {
        return dec;
    }

    public void setDec(int dec) {
        this.dec = dec;
    }

    public int getDdec() {
        return ddec;
    }

    public void setDdec(int ddec) {
        this.ddec = ddec;
    }

    public void setTextFont(Font textFont) {
        this.textFont = textFont;
        maxField.setFont(textFont);
        valField.setFont(textFont);
        minField.setFont(textFont);
    }

    public boolean isAdjusting() {
        return slider.getValueIsAdjusting();
    }

    public void setAdjusting(boolean adjusting) {
        this.adjusting = adjusting;
        slider.setValueIsAdjusting(adjusting);
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
        minField.setVisible(showingFields);
        maxField.setVisible(showingFields);
        valField.setVisible(showingFields);
        this.validate();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private pl.edu.icm.visnow.gui.components.FloatFormattedTextField maxField;
    private pl.edu.icm.visnow.gui.components.FloatFormattedTextField minField;
    private javax.swing.JCheckBox showFieldsBox;
    private javax.swing.JSlider slider;
    private pl.edu.icm.visnow.gui.components.FloatFormattedTextField valField;
    // End of variables declaration//GEN-END:variables
    /**
     * Utility field holding list of ChangeListeners.
     */
    private transient ArrayList<ChangeListener> changeListenerList = new ArrayList<ChangeListener>();

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
        LOGGER.trace("Active: " + (!((slider.getValueIsAdjusting() && !adjusting) || !active)) + " value: " + val);
        if ((slider.getValueIsAdjusting() && !adjusting) || !active)
            return;
        java.util.ArrayList list;
        ChangeEvent e = new ChangeEvent(this);
        synchronized (this) {
            if (changeListenerList == null)
                return;
            list = new ArrayList<ChangeListener>(changeListenerList);
        }
        for (int i = 0; i < list.size(); i++) {
            ((ChangeListener) list.get(i)).stateChanged(e);
        }
    }
    /**
     * Utility field holding list of MouseListeners.
     */
    private transient ArrayList<MouseListener> mouseListenerList = new ArrayList<MouseListener>();

    /**
     * Registers MouseListener to receive events.
     *
     * @param listener The listener to register.
     */
    public synchronized void addMouseListener(MouseListener listener) {
        mouseListenerList.add(listener);
    }

    /**
     * Removes MouseListener from the list of listeners.
     *
     * @param listener The listener to remove.
     */
    public synchronized void removeMouseListener(MouseListener listener) {
        if (mouseListenerList != null) {
            mouseListenerList.remove(listener);
        }
    }

    public void mouseClicked(MouseEvent e) {
        for (MouseListener listener : mouseListenerList)
            listener.mouseClicked(e);
    }

    public void mousePressed(MouseEvent e) {
        for (MouseListener listener : mouseListenerList)
            listener.mousePressed(e);
    }

    public void mouseReleased(MouseEvent e) {
        for (MouseListener listener : mouseListenerList)
            listener.mouseReleased(e);
    }

    public void mouseEntered(MouseEvent e) {
        for (MouseListener listener : mouseListenerList)
            listener.mouseEntered(e);

    }

    public void mouseExited(MouseEvent e) {
        for (MouseListener listener : mouseListenerList)
            listener.mouseExited(e);
    }

    public static void main(String[] a) {
        VisNow.initLogging(true);
        Locale.setDefault(VisNow.LOCALE);
//       
//               /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(TestFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(TestFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(TestFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(TestFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//       
        final JFrame f = new JFrame();
        final FloatSlider fs = new FloatSlider();
        fs.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
            }
        });

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                f.add(fs);
                f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                f.setLocation(600, 300);
                f.pack();
                f.setVisible(true);
            }
        });
    }
}
