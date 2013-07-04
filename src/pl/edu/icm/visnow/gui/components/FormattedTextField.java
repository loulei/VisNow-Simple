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

package pl.edu.icm.visnow.gui.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Locale;
import javax.swing.JTextField;
import org.apache.log4j.Logger;

/**
 * This text field is for testing if user typed input string in proper format; You have to extend this class 
 * and override isValidValue() method; This text field assumes that it ALWAYS contains properly formatted value 
 * (excluding time when user is editing the field).
 * That's why default value is necessary to initialize field.
 * Additionally you can override format() method which reformats input string; By default identity method is used 
 * (no reformatting). 
 * Format is tested after text field has changed (user input:  press ENTER or setText is called).
 * If input is not properly formatted or field lost its focus than input is filled in with last valid value.
 * 
 * 1. This class avoids situation when visible text in input field is different then value of the field (getText) - 
 *    that is the case when there is no action on lost focus.
 * 2. It doesn't format input if format is not provided (like JFormattedTextField does)
 * 3. It tests format only on submit (opposite to DocumentListener)
 * 4. It doesn't force user to input proper value by locking focus (as InputVerifier does)
 * 
 * @author szpak
 */
public abstract class FormattedTextField extends JTextField {
    private static final Logger LOGGER = Logger.getLogger(FormattedTextField.class);

    private String lastValidValue;

    /**
     * @throws InvalidValueException if default value - that initiates field - is not properly formatted.
     */
    public FormattedTextField() {
        super();
        lastValidValue = format(getDefaultValue(),false);
        if (!isValidValue(lastValidValue))
            throw new InvalidValueException("Default value is not valid");

        super.setText(lastValidValue);

        this.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            }

            //field is submitted on lost focus
            @Override
            public void focusLost(FocusEvent e) {
                restoreText();
            }
        });

        //dummy listener to call fireActionPerformed even if there are no listeners
        this.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        });
    }

    /**
     * Passed parameter is formatted and set; this method assumes that parameter is a valid value, 
     * otherwise run time exception is thrown.
     * @throws InvalidValueException if parameter is not valid value
     */
    @Override
    public void setText(String t) throws InvalidValueException {
        if (isValidValue(t)) {
            lastValidValue = format(t,false);
            super.setText(lastValidValue);
        }
        else throw new InvalidValueException("Passed value is not valid: '"+t+ "' for locale: "+ Locale.getDefault());
    } 
    
    private void restoreText() {
        super.setText(lastValidValue);
    }
    
    /**
     * This method should test if text field is properly formatted.
     * For example, float field could be tested using Float.parseFloat(text).
     */
    protected abstract boolean isValidValue(String text);

    /**
     * This is necessary to properly initialize component.
     */
    protected abstract String getDefaultValue();

    /**
     * This method can be overrided to format text string (after every submit).
     * Basic case is to e.g, trim spaces.
     * 
     * @param userModified if true than reformatting is called after user action (fireActionPerformed)
     */
    protected String format(String text, boolean userModified) {
        return text;
    }


    @Override
    protected void fireActionPerformed() {
        String text = getText();
        if (!isValidValue(text))
            super.setText(lastValidValue);
        else {
            String oldValue = lastValidValue;
            lastValidValue = format(text,true); //format in user mode
            super.setText(lastValidValue);
            //If there is any change in input then fire ActionPerformed event.
            //There might be 2 different inputs with the same look after formatting eg. 1.000001 and 1.000000001
            if (!oldValue.equals(text)) { 
                super.fireActionPerformed();
            }
        }
    }

    /**
     * Exception that is thrown when invalid default value is passed from inherited class or invalid value is passed to setText.
     */
    public class InvalidValueException extends RuntimeException {

        public InvalidValueException(String message) {
            super(message);
        }
    }
}
