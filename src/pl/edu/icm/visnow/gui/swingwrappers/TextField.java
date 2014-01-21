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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import javax.swing.JTextField;

/**
 * JTextField wrapper: adds ValueChanged Action to this component (with two alternative behaviors: submit on lost focus (default) or revert on lost focus)
 * 
 * ValueChange is fired only on user action (no firing event on setter).
 *
 * @author szpak
 */
public class TextField extends JTextField {
    private String previousText = "";
    private boolean submitOnLostFocus = true;

    public TextField() {
        initEvents();
    }

    private void initEvents() {        

        this.addFocusListener(new FocusAdapter() {
            //submit or revert value on lost focus
            @Override
            public void focusLost(FocusEvent e) {
                if (submitOnLostFocus && !TextField.super.getText().equals(previousText)) {
                    rememberValues(TextField.super.getText());
                    fireValueChanged();
                }
                else revertText();
            }
        });
        
        //fire event if value changed
        this.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!TextField.super.getText().equals(previousText)) {
                    rememberValues(TextField.super.getText());
                    fireValueChanged();
                }
            }
        }); 
    }

    @Override
    public void setText(String t) {
        super.setText(t);         
        previousText = super.getText();
    }
 
    private void revertText() {
        super.setText(previousText);
    }
    
    //TODO: maybe this is not necessary (compare with ComboBox)
    /**
     * Added just for compatibility with other wrappers (setText doesn't fire action performed, which is fine)
     */
//    public void setValueSilent(String t) {
//        setText(t);
//    }
    
    /**
     * In general it should be called before fireValueChange - because text/value are good.
     */
    private void rememberValues(String newText) {
        previousText = newText;
    }    
    
    private void fireValueChanged() {
        for (UserActionListener listener: userActionListeners)
            listener.valueChangedAction(new EventObject(this));
    }
    
    private List<UserActionListener> userActionListeners = new ArrayList<UserActionListener>();

    public void addUserActionListener(UserActionListener listener) {
        userActionListeners.add(listener);
    }

    public void removeUserActionListener(UserActionListener listener) {
        userActionListeners.remove(listener);
    }

    public void setSubmitOnLostFocus(boolean submitOnLostFocus) {
        this.submitOnLostFocus = submitOnLostFocus;
    }

    public boolean isSubmitOnLostFocus() {
        return submitOnLostFocus;
    }
}
