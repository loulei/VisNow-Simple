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
package pl.edu.icm.visnow.gui.swingwrappers;

import java.util.EventListener;
import java.util.EventObject;

/**
 * The listener interface for receiving user events only.
 * (so there should be no firing such events on setters or any other change of internal state).
 *
 * @author szpak
 */
public interface UserActionListener extends EventListener {

    /**
     * Simple action that notifies about real value change
     * (so pressing Enter on TextField when typing the same text or click on already selected RadioButton should not fire such action).
     * 
     * This action should be the most simple and natural understanding of "value changed".
     *
     * On the other hand some real change may not be notified using this action - and this should be implementation specific. 
     * 
     * Examples:
     * <ul>
     * <li>typing into TextField and then losing focus without pressing ENTER can a) submit typed value b) revert previous value
     * <li>sliding/adjusting slider can a) fire event or b) event can be fired after adjusting is stopped
     * </ul>
     */
    public void valueChangedAction(EventObject event);
    
    /**
     * This action is fired when user performed some action 
     * (including actions that don't change value of component - like adjusting sliders, confirming text field with enter, clicking already clicked radiobutton)
     */
    //public void userAction(UserEvent event);
}
