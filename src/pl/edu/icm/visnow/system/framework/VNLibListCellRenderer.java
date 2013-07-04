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

package pl.edu.icm.visnow.system.framework;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import pl.edu.icm.visnow.engine.library.LibraryRoot;


/**
 *
 * @author  Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class VNLibListCellRenderer implements ListCellRenderer {
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JPanel panel = new JPanel();
        if(isSelected) panel.setBackground(new Color(128,128,128));
        else panel.setBackground(new Color(255,255,255));
        
        
        
        JLabel textLabel = new JLabel("  "+((LibraryRoot)value).getName()+"  ");
        JLabel iconLabel;
        if(textLabel.getText().equalsIgnoreCase("  internal  "))
            iconLabel = new JLabel(new ImageIcon(getClass().getResource("/pl/edu/icm/visnow/gui/icons/libraries/kalfa_intFolder48.png")));
        else 
            iconLabel = new JLabel(new ImageIcon(getClass().getResource("/pl/edu/icm/visnow/gui/icons/libraries/kalfa_jarFolder48.png")));

        panel.setLayout(new GridBagLayout());
        java.awt.GridBagConstraints iconConstraints = new GridBagConstraints();
        java.awt.GridBagConstraints textConstraints = new GridBagConstraints();
        textConstraints.gridx = 0;
        textConstraints.gridy = 1;
        panel.add(iconLabel, iconConstraints);
        panel.add(textLabel, textConstraints);
        
        return panel;
    }
}
