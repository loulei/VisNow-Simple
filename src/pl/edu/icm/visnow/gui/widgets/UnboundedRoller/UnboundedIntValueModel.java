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

package pl.edu.icm.visnow.gui.widgets.UnboundedRoller;
import javax.swing.event.*;


/**
 * Defines the data model used by UnboundedRoller component. 
 * Defines two interrelated float properties: value and sensitivity.  
 *
 *
 * @version 1.26 01/23/03
 * @author Krzysztof S. Nowinski
 * @see DefaultBoundedRangeModel
 */
public interface UnboundedIntValueModel
{
    /**
     * Returns the current value.
     *
     * @return the value of the value property
     * @see #setValue
     */
    int getValue();


    /**
     * Sets the model's current value to <I>newValue</I>.
     * Notifies any listeners if the model changes.
     *
     * @param newMinimum the model's new minimum
     * @see #getValue
     * @see #addChangeListener
     */
    void setValue(int newValue);


    /**
     * Returns the model's sensitivity.  
     * @see #setSensitivity
     */
    int getSensitivity();


    /**
     * Sets the model's sensitivity to <I>newSensitivity</I>. 
     *
     * @param newSensitivity the model's new sensitivity
     * @see #getSensitivity
     * @see #addChangeListener
     */
    void setSensitivity(int newSensitivity);

    
    /**
     * Returns true if a series of changes of 
     * BottomValue or TopValue settings is in progress.
     * Returns false when both TopValue and BottomValue are finally set.
     */
    boolean isAdjusting();
    
    /**
     * Set true if a series of changes of 
     * Value settings is in progress.
     * Set false when both Value are finally set.
     */
    void setAdjusting(boolean newAdjusting);


    /**
     * Adds a ChangeListener to the model's listener list.
     *
     * @param x the ChangeListener to add
     * @see #removeChangeListener
     */
    void addChangeListener(ChangeListener x);


    /**
     * Removes a ChangeListener from the model's listener list.
     *
     * @param x the ChangeListener to remove
     * @see #addChangeListener
     */
    void removeChangeListener(ChangeListener x);

}
