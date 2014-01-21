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

package pl.edu.icm.visnow.gui.widgets.SubSubRangeSlider;
import javax.swing.event.*;


/**
 * Defines the data model used by SubSubRangeSlider component.
 * Defines five interrelated integer properties: minimum, maximum, extent,
 * bottomValue and topValue.  These four integers define two nested ranges like this:
 * <pre>
 * minimum <= bottomValue <= bottomValue+extent <= topValue <= maximum
 * </pre>
 * The outer range is <code>minimum,maximum</code> and the inner
 * range is <code>bottomValue,topValue</code>.
 * <ul>
 * <li>
 *   The minimum and maximum set methods "correct" the other
 *   three properties to accommodate their new value argument.  For
 *   example setting the model's minimum may change its maximum, value,
 *   and extent properties (in that order), to maintain the constraints
 *   specified above.
 *
 * <li>
 *   The value and extent set methods "correct" their argument to
 *   fit within the limits defined by the other three properties.
 *   For example if <code>value == maximum</code>, <code>setExtent(10)</code>
 *   would change the extent (back) to zero.
 *
 * <li>
 *   The five BoundedRangeModel values are defined as Java Beans properties
 *   however Swing ChangeEvents are used to notify clients of changes rather
 *   than PropertyChangeEvents. This was done to keep the overhead of monitoring
 *   a BoundedRangeModel low. Changes are often reported at MouseDragged rates.
 * </ul>
 *
 * <p>
 *
 * For an example of specifying custom bounded range models used by sliders,
 * see <ahref="http://java.sun.com/docs/books/tutorial/uiswing/overview/anatomy.html">
 * The Anatomy of a Swing-Based Program</a>
 * in <em>The Java Tutorial.</em>
 *
 * @version 1.26 01/23/03
 * @author Hans Muller
 * @see DefaultBoundedRangeModel
 */
public interface SubSubRangeModel
{
   public static final int STOP=0;
   public static final int PUSH=1;

   public static final int RESET=0;
   public static final int IGNORE=1;
   public static final int STRETCH=2;
   public static final int PULL=3;
    /**
     * Returns the minimum acceptable value.
     *
     * @return the value of the minimum property
     * @see #setMinimum
     */
    int getMinimum();


    /**
    * Sets the model's minimum to <I>newMinimum</I>.   The
    * other three properties may be changed as well, to ensure
    * that:
    * <pre><CODE>
    * minimum <= value <= value+extent <= maximum
    * </CODE> </pre>
    * <p>
    * Notifies any listeners if the model changes.
    * @param newMinimum the model's new minimum
    * @see #getMinimum
    * @see #addChangeListener
    */
    void setMinimum(int newMinimum);


    /**
     * Returns the model's maximum.  Note that the upper
     * limit on the model's value is (maximum - extent).
     *
     * @return the value of the maximum property.
     * @see #setMaximum
     * @see #setExtent
     */
    int getMaximum();


    /**
    * Sets the model's maximum to <I>newMaximum</I>. The other
    * three properties may be changed as well, to ensure that
    * <pre><CODE>
    * minimum <= value <= value+extent <= maximum
    * </CODE></pre>
    * <p>
    * Notifies any listeners if the model changes.
    * @param newMaximum the model's new maximum
    * @see #getMaximum
    * @see #addChangeListener
    */
    void setMaximum(int newMaximum);


    /**
     * Returns the model's current bottomValue.  Note that the upper
     * limit on the model's value is <code>maximum - extent</code>
     * and the lower limit is <code>minimum</code>.
     *
     * @return  the model's value
     * @see     #setValue
     */
    int getBottomValue();


    /**
     * Sets the model's currentbottomVvalue to <code>newValue</code> if <code>newValue</code>
     * satisfies the model's constraints. Those constraints are:
     * <pre>
     * minimum <= value <= value+extent <= maximum
     * </pre>
     * Otherwise, if <code>newValue</code> is less than <code>minimum</code>
     * it's set to <code>minimum</code>, if its greater than
     * <code>maximum</code> then it's set to <code>maximum</code>, and
     * if it's greater than <code>value+extent</code> then it's set to
     * <code>value+extent</code>.
     * <p>
     * Notifies any listeners if the model changes.
     *
     * @param newValue the model's new value
     */
    void setBottomValue(int newValue);

    /**
     * Returns the model's current topValue.  Note that the upper
     * limit on the model's value is <code>maximum</code>
     * and the lower limit is <code>minimum</code> + extent</code> .
     *
     * @return  the model's topValue
     */
    int getTopValue();


    /**
     * Sets the model's current topValue to <code>newValue</code> if <code>newValue</code>
     * satisfies the model's constraints. Those constraints are:
     * <pre>
     * bottomValue+extent <= topValue <= maximum
     * </pre>
     * Notifies any listeners if the model changes.
     *
     * @param newValue the model's new value
     * @see #getValue
     */
    void setTopValue(int newValue);

    
    /**
     * Returns the model's current centerValue.  Note that the upper
     * limit on the model's center value is <code>topValue</code>
     * and the lower limit is <code>bottomValue</code> .
     *
     * @return  the model's centerValue
     * @see #setCenterValue
     */    
    int getCenterValue();
    
    
    /**
     * Sets the model's current centerValue to <code>newValue</code> if <code>newValue</code>
     * satisfies the model's constraints. Those constraints are:
     * <pre>
     * centerValue <= topValue <= maximum
     * centerValue >= bottomValue >= minimum
     * </pre>
     * Notifies any listeners if the model changes.
     *
     * @param newValue the model's new value
     * @see #getCenterValue
     */
    void setCenterValue(int newValue);
    
    
    
    /**
     * Returns the model's extent, the length of the inner range that
     * begins at the model's value.
     *
     * @return  the value of the model's extent property
     * @see     #setExtent
     * @see     #setValue
     */
    int getExtent();


    /**
     * Sets the model's extent.  The <I>newExtent</I> is forced to
     * be greater than or equal to zero and less than or equal to
     * maximum - value.
     * <p>
     * Notifies any listeners if the model changes.
     *
     * @param  newExtent the model's new extent
     * @see #getExtent
     * @see #setValue
     */
    void setExtent(int newExtent);

    /**
     * Returns the model's conflict resolving policy.
     * <p>
     * When policy==STOP updating bottomValue or topValue that would lead to
     * bottomValue+extent > topValue, change to currently modified value will be
     * constrained.
     * <p>
     * When policy==PUSH, the other value will be updated to preserve
     * bottomValue+extent <= topValue.
     * <p>
     * Notifies any listeners if the model changes.
     *
     * @param  newPolicy the model's new policy
     * @see #getExtent
     * @see #setBottomValue
     * @see #setTopValue
     */
    int getPolicy();

    /**
     * Sets the model's conflict resolving policy.
     * <p>
     * When policy==STOP updating bottomValue or topValue that would lead to
     * bottomValue+extent > topValue, change to currently modified value will be
     * constrained.
     * <p>
     * When policy==PUSH, the other value will be updated to preserve
     * bottomValue+extent <= topValue.
     * <p>
     * Notifies any listeners if the model changes.
     *
     * @param  newPolicy the model's new policy
     * @see #getExtent
     * @see #setBottomValue
     * @see #setTopValue
     */
    void setPolicy(int newPolicy);

    /**
    * <p><CODE>policy==RESET</CODE>:  updating<CODE> minimum</CODE> or <CODE>maximum</CODE> causes setting of
    * <CODE>bottomValue</CODE> to <CODE>minimum</CODE> and <CODE>topValue</CODE> to <CODE>maximum</CODE>
    * <p><CODE>policy==IGNORE</CODE>: updating <CODE>minimum or <CODE>maximum</CODE>modifies
    * <CODE>bottomValue</CODE> or <CODE>topValue</CODE> only  in the case of constraints violation
    * <p><CODE>policy==STRETCH</CODE>: if <CODE>topValue=maximum</CODE> and <CODE>maximum</CODE> is changed,
    * <CODE>topValue</CODE> is set to  <CODE>maximum</CODE>, <CODE>bottomValue</CODE> is modified accordingly
    * <p><CODE>policy==PULL</CODE>: if <CODE>topValue=maximum</CODE> and <CODE>maximum</CODE> is changed,
    * <CODE>topValue</CODE> is set to  <CODE>maximum</CODE> and <CODE>bottomValue</CODE> is modified to preserve extent;
    * in the case of <CODE>bottomValue=minimum</CODE> and <CODE>mainimum</CODE> is changed,
    * <CODE>bottomValue</CODE> follows  <CODE>minimum</CODE> and <CODE>topValue</CODE> is modified to preserve extent;
    * @return current value of <CODE>extensionPolicy</CODE>
    */
    int getExtensionPolicy();

    /**
    * Sets the model's reaction on minimum/maximum change.  
    * 
    * Notifies any listeners if the model changes.
    * @param newExtensionPolicy the model's new extension policy
    */
    void setExtensionPolicy(int newExtensionPolicy);

    /**
    * Returns true if a series of changes of
    * BottomValue or TopValue settings is in progress.
    * Returns false when both TopValue and BottomValue are finally set.
    * @return <CODE>true</CODE> if a series of changes of
    * <CODE>bottomValue</CODE> or <CODE>topValue</CODE> settings is in progress
    * <p><CODE>false</CODE> when both <CODE>TopValue</CODE> and <CODE>BottomValue</CODE> are finally set.
    */
    boolean isAdjusting();

    /**
     * Set true if a series of changes of
     * BottomValue or TopValue settings is in progress.
     * Set false when both TopValue and BottomValue are finally set.
     */
    void setAdjusting(boolean newAdjusting);

    /**
     * This method sets all of the model's data with a single method call.
     * The method results in a single change event being generated. This is
     * convenient when you need to adjust all the model data simultaneously and
     * do not want individual change events to occur.
     *
     * @param bottomValue 
     * @param topValue 
     * @param extent an int giving the amount by which the value can "jump"
     * @param policy 
     * @param extensionPolicy 
     * @param min    an int giving the minimum value
     * @param max    an int giving the maximum value
     * @param adjusting a boolean, true if a series of changes are in
     *                    progress
     *
     * @see #setValue
     * @see #setExtent
     * @see #setMinimum
     * @see #setMaximum
     * @see #setAdjusting
     */
    void setRangeProperties(int bottomValue, int topValue, int extent, int centerValue,
                            int policy, int extensionPolicy, int min, int max, boolean adjusting);


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

    boolean isBottomValueChanged();
    int getLastBottomValue();
    boolean isTopValueChanged();
    int getLastTopValue();
    boolean isCenterValueChanged();
    int getLastCenterValue();
}
