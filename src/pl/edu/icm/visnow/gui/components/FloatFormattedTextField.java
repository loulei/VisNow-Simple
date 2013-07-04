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

import java.text.NumberFormat;
import java.text.ParseException;
import org.apache.log4j.Logger;
    
/**
 * Text field which tests input format against NumberFormat.getInstance().parse() method.
 * If maxPrecisionDigits is provided then decimal formatting is used.
 * Infinite and NaN are not valid values.
 * @author szpak
 */
public class FloatFormattedTextField extends FormattedTextField {
    private static final Logger LOGGER = Logger.getLogger(FloatFormattedTextField.class);

    private int maxPrecisionDigits = 0;
    private boolean preserveUserFormat = true;
     
    @Override
    protected boolean isValidValue(String text) {
        try {
            text = preformat(text);
            Float f = NumberFormat.getInstance().parse(text).floatValue();
            if (Float.isInfinite(f) || Float.isNaN(f)) return false;
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    @Override
    protected String getDefaultValue() {
        return NumberFormat.getInstance().format(1.0);
    }

    /**
     * Sets maximum number of digits to show. Set to 0 for no formatting (only trim).
     * 
     */
    public void setMaxPrecisionDigits(int maxPrecisionDigits) {
        this.maxPrecisionDigits = maxPrecisionDigits;
    }

    /**
     * This flag indicates that field is not formatted after user input.
     * @param preserveUserFormat 
     */
    public void setPreserveUserFormat(boolean preserveUserFormat) {
        this.preserveUserFormat = preserveUserFormat;
    }    
    
    /**
     * Formats text to be formatted on some particular base level - this formatting is applied even if "preserveUserFormat" is set.
     */
    private String preformat(String text) {
        //trim + lame hack to replace comma with dot (polish numeric keyboard)
        text = text.trim().replaceAll("\\,",".");
        //another lame hack to add "0" at the beginning if not present (numbers in format ".001")
        return (text.length() == 0 || text.charAt(0) != '.') ? text : "0"+text;        
    }
            
    @Override
    /**
     * Formats number to have only maxFormatDigits displayed.
     */
    protected String format(String text, boolean userModified) {
        //just trim spaces if user modified field
        // or if no formatting (just preformatting)
        if ((preserveUserFormat && userModified) || maxPrecisionDigits == 0) return preformat(text);
        //otherwise reformat using default formatter
        //by definition text is valid value - no need to catch exception
        else {
            float f = 0;
            try {
                text = preformat(text);
                f = NumberFormat.getInstance().parse(text.trim()).floatValue();
            } catch (ParseException e) {
                LOGGER.error("ParseException - This exception should not occur here. We assume that text is already in correct format!");
                throw new NumberFormatException(e.getMessage());
            }
                
            return String.format("%1$."+maxPrecisionDigits+"f",f);
        }
    }
}
