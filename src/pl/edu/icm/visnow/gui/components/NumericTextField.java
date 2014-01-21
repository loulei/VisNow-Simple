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
package pl.edu.icm.visnow.gui.components;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import pl.edu.icm.visnow.gui.swingwrappers.TextField;
import pl.edu.icm.visnow.gui.swingwrappers.UserActionListener;
import static pl.edu.icm.visnow.gui.components.NumericTextField.FieldType.*;

//TODO: add UNSIGNED_*

/**
 * Adds parsing input text to TextField, that means: tests numeric format of input value against X.parseX (X = Byte, Short, Integer, Long, Float or Double).
 *
 * Main assumption here is that this field always stores and shows correct number (for current selected type). So input is tested in user action (ValueChangedAction) in setText and in both setValue.
 * But not always getValue == parse(getText) - this is caused by formatting (but it's fine).
 *
 * Internal model here is type + double/long number + textual value.
 *
 * On user action (input text) value is reverted if value is out of range.
 * setText throws exception if value is out of range (this is feature of parseX methods).
 * setValue throws exception if value is out of range (to be consistent with setText).
 *
 * setType always casts to new type with rounding to range (according to new type) - in the same time this avoids casting large double to infinity float.
 *
 * No internal format is used. The only place for use custom format is setValue(value, format). After setType default java format is used (toString).
 *
 * This component starts in type == Double. If you use it as a JavaBean (in NetBeans GUI designer) then setType and setText/setValue can be called in "random" order and
 * rounding error may appear (for large Long numbers, larger then Long.MAX_VALUE/1023).
 * In such case it's better to use custom generation code (new NumericTextField(Long))
 *
 * Additionally valueChangeAction is performed only when numeric value has changed (not only text content - try "0 and "00")
 *
 * NaN and Infinity are not allowed here (even if they are properly parsed in parseFloat/Double).
 *
 * getText return different value then internal text (only while editing). In contrary getValue always return internally stored value.
 *
 * [Note: Because of the flow at the beginning this field cannot be easily extended to other types (wider then double, or completely different like Big*, complex, etc)]
 *
 * [Note: It might be better to have NumericField that does not extend TextField (so one cannot call setText but only setValue), and it can be done
 * like it's done in JSpinner (by JPanel-based editor) but in such case all TextField functionality (like setBackground, addFocusListener, etc)
 * would be lost and would need implementation.]
 *
 * @author szpak
 */
public class NumericTextField extends TextField {

    //model (only one from doubleValue and longValue handles correct value (depends on field type))
    //this could be changed to double/float/long/int/short/byteValue but that would be probably even more messy (in casting/parsing)
    //assume: never NaN, never Infinity
    private double doubleValue; //for float/double types
    private long longValue; // for byte/short/int/long types
    private String text; //assume: never null
    //
    //assume: type should be never null
    private FieldType type;

    public NumericTextField() {
        this(DOUBLE); //Double by default (wider class) (although may cause problems with large long (read javadoc))
    }

    public NumericTextField(FieldType formatType) {
        this(formatType, "0");
    }

    private NumericTextField(FieldType formatType, String defaultText) {
        if (formatType == null) throw new NullPointerException("Format type cannot be null");
        type = formatType;
        text = defaultText;
        super.setText(text);
        if (isRealType()) doubleValue = (Double) parseText(text);
        else longValue = (Long) parseText(text);

        super.addUserActionListener(new UserActionListener() {
            @Override
            public void valueChangedAction(EventObject event) {
                try { //fire value change if correct                    
                    if (isRealType()) { //real number type
                        String newText = NumericTextField.super.getText();
                        double newDoubleValue = (Double) parseText(newText);
                        //no exception -> update model
                        text = newText;
                        if (newDoubleValue != doubleValue) {
                            doubleValue = newDoubleValue; //update model
                            fireValueChanged(); //numeric value changed -> fire event
                        }
                    } else { //int number type
                        String newText = NumericTextField.super.getText();
                        long newLongValue = (Long) parseText(newText);
                        //no exception -> update model
                        text = newText;
                        if (newLongValue != longValue) {
                            longValue = newLongValue; //update model
                            fireValueChanged(); //numeric value changed -> fire event
                        }
                    }
                } catch (NumberFormatException e) { //revert text on failure
                    NumericTextField.super.setText(text);
                }
            }
        });
    }

    public enum FieldType {

        BYTE, SHORT, INT, LONG, FLOAT, DOUBLE;
    }

    /**
     * Sets/changes format type of this field to
     * <code>formatType</code> and do necessary casting.
     *
     * This property is called FieldType and not just Type to be alphabetically before "Text" in NetBeans GUI designer (sic!).
     * (anyway BeanInfo has to be added and FieldType marked as preferred)
     */
    public void setFieldType(FieldType newType) {
        if (newType == null) throw new NullPointerException("Format type cannot be null");
        if (type != newType) {
            if (isRealType()) { //cast from real type
                if (newType == DOUBLE) ; // do nothing (from double to double)
                else if (newType == FLOAT) doubleValue = Math.max(-Float.MAX_VALUE, Math.min(Float.MAX_VALUE, doubleValue)); //round to range + avoid casting to infinity!
                else if (newType == LONG) longValue = (long) doubleValue; //auto round to range
                else if (newType == INT) longValue = Math.max(Integer.MIN_VALUE, Math.min(Integer.MAX_VALUE, (long) doubleValue)); //round to range
                else if (newType == SHORT) longValue = Math.max(Short.MIN_VALUE, Math.min(Short.MAX_VALUE, (long) doubleValue)); //round to range
                else if (newType == BYTE) longValue = Math.max(Byte.MIN_VALUE, Math.min(Byte.MAX_VALUE, (long) doubleValue)); //round to range
                else
                    throw new RuntimeException("Incorrect format");
            } else { //cast from integer type
                if (newType == DOUBLE) doubleValue = (double) longValue; // double range larger than long
                else if (newType == FLOAT) doubleValue = (float) longValue; // float range larger than long
                else if (newType == LONG) ; // do nothing (from integer to integer)
                else if (newType == INT) longValue = Math.max(Integer.MIN_VALUE, Math.min(Integer.MAX_VALUE, longValue)); //round to range
                else if (newType == SHORT) longValue = Math.max(Short.MIN_VALUE, Math.min(Short.MAX_VALUE, longValue)); //round to range
                else if (newType == BYTE) longValue = Math.max(Byte.MIN_VALUE, Math.min(Byte.MAX_VALUE, longValue)); //round to range
                else
                    throw new RuntimeException("Incorrect format");
            }

            //update type
            type = newType;

            //update model (text) with default formatting
            if (isRealType()) text = Double.toString(doubleValue);
            else text = Long.toString(longValue);
            super.setText(text);
        }
    }

    public FieldType getFieldType() {
        return type;
    }

    /**
     * Tries to parse and updates model on success.
     *
     * @throws NumberFormatException if <code>t</code> cannot be parsed in current type.
     */
    @Override
    public void setText(String t) {
        if (isRealType())
            doubleValue = (Double) parseText(t);
        else
            longValue = (Long) parseText(t);

        text = t;
        super.setText(t);
    }

    /**
     *
     * @throws IllegalArgumentException if value is not of correct type (real number for integer field)
     */
    public void setValue(Object value) {
        if (!isCorrectType(value)) throw new IllegalArgumentException("Passed value is incorrect for this field type");

        //update model (value)
        setInternalValueFromObject(value);
        //update model (text)        
        if (isRealType()) {
            text = Double.toString(doubleValue);
            super.setText(text);
        } else {
            text = Long.toString(longValue);
            super.setText(text);
        }
    }

    /**
     * @throws IllegalArgumentException if value is not of correct type (real number for integer field)
     * @throws IllegalFormatException If format is incorrect
     * @throws NullPointerException if format is null
     */
    public void setValue(Object value, String format) {
        if (!isCorrectType(value)) throw new IllegalArgumentException("Passed value is incorrect for this field type");

        //update model (value)
        setInternalValueFromObject(value);
        //update model (text)        
        if (isRealType()) {
            text = String.format(format, doubleValue);
            super.setText(text);
        } else {
            text = String.format(format, longValue);
            super.setText(text);
        }
    }

    //TODO: take a look into JSpinner model .setValue
    //TODO: add min/max to model ?? (but this would be a tough thing)
    //TODO: it would be probably better to force developer to pass correct class (Byte - Double)
    // this could prevent future problems (while Infinity and NaN still can be incorrectly passed)
    //TODO: this might be changed to also allow passing real numbers for integer field type (??with rounding to int - but it's not very consistent
    // because if number is out of range than exception is thrown)
    /**
     * Sets internal doubleValue or longValue with respect to current field type and passed Object class.
     * Throws exception if value is out of range.
     */
    private void setInternalValueFromObject(Object value) {
        if (isRealType()) {
            //get value from Object
            Double newDoubleValue;
            if (value instanceof Double) newDoubleValue = (Double) value;
            else if (value instanceof Float) newDoubleValue = new Double((Float) value);
            else if (value instanceof Long) newDoubleValue = new Double((Long) value);
            else if (value instanceof Integer) newDoubleValue = new Double((Integer) value);
            else if (value instanceof Short) newDoubleValue = new Double((Short) value);
            else newDoubleValue = new Double((Byte) value);

            //test if correct
            if (newDoubleValue.isNaN() || newDoubleValue.isInfinite()) throw new NumberFormatException("NaN and Infinity are not supported: " + value);

            if (type == DOUBLE) doubleValue = newDoubleValue;
            else if (type == FLOAT) {
                if (newDoubleValue > Float.MAX_VALUE || newDoubleValue < -Float.MAX_VALUE) throw new IllegalArgumentException("Float value out of range " + value);
                doubleValue = new Float(newDoubleValue);
            } else
                throw new RuntimeException("Incorrect format");
        } else {
            //get value from Object
            Long newLongValue;
            if (value instanceof Long) newLongValue = (Long) value;
            else if (value instanceof Integer) newLongValue = new Long((Integer) value);
            else if (value instanceof Short) newLongValue = new Long((Short) value);
            else newLongValue = new Long((Byte) value);

            //test if correct
            if (type == LONG) longValue = newLongValue;
            else if (type == INT) {
                if (newLongValue > Integer.MAX_VALUE || newLongValue < Integer.MIN_VALUE) throw new IllegalArgumentException("Integer value out of range " + value);
                longValue = newLongValue;
            } else if (type == SHORT) {
                if (newLongValue > Short.MAX_VALUE || newLongValue < Short.MIN_VALUE) throw new IllegalArgumentException("Short value out of range " + value);
                longValue = newLongValue;
            } else if (type == BYTE) {
                if (newLongValue > Byte.MAX_VALUE || newLongValue < Byte.MIN_VALUE) throw new IllegalArgumentException("Byte value out of range " + value);
                longValue = newLongValue;
            } else
                throw new RuntimeException("Incorrect format");
        }
    }

    /**
     * All numeric types are correct if field is of real type; for integer type field only integer types are correct.
     */
    private boolean isCorrectType(Object o) {
        if (isRealType()) return o instanceof Double || o instanceof Float || o instanceof Long || o instanceof Integer || o instanceof Short || o instanceof Byte;
        else return o instanceof Long || o instanceof Integer || o instanceof Short || o instanceof Byte;
    }

    /**
     * Returns numeric value of this text field. Result object class reflects fieldType and is Double, Float, Long, Integer, Short or Byte.
     */
    public Object getValue() {
        if (type == DOUBLE) return new Double(doubleValue);
        else if (type == FLOAT) return new Float(doubleValue); //assume: cannot be infinite
        else if (type == LONG) return new Long(longValue);
        else if (type == INT) return new Integer((int) longValue);
        else if (type == SHORT) return new Short((short) longValue);
        else if (type == BYTE) return new Byte((byte) longValue);
        else
            throw new RuntimeException("Incorrect format");
    }

    /**
     * Parses
     * <code>t</code> and returns Double or Long (depends on
     * <code>type</code>).
     *
     * @throws NumberFormatException if cannot parse or floating point number is NaN or Infinity.
     */
    private Object parseText(String t) throws NumberFormatException {
        if (type == DOUBLE) {
            Double d = Double.parseDouble(t);
            if (d.isNaN() || d.isInfinite()) throw new NumberFormatException("NaN and Infinity are not supported");
            return d;
        } else if (type == FLOAT) {
            Float f = Float.parseFloat(t);
            if (f.isNaN() || f.isInfinite()) throw new NumberFormatException("NaN and Infinity are not supported");
            return new Double(f);
        } else if (type == LONG) return Long.parseLong(t);
        else if (type == INT) return new Long(Integer.parseInt(t));
        else if (type == SHORT) return new Long(Short.parseShort(t));
        else if (type == BYTE) return new Long(Byte.parseByte(t));

        throw new RuntimeException("Incorrect format");
    }

    /**
     * Is float or double.
     */
    private boolean isRealType() {
        return (type == DOUBLE || type == FLOAT);
    }

    private void fireValueChanged() {
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
}
