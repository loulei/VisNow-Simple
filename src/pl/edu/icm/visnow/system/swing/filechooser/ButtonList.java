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

package pl.edu.icm.visnow.system.swing.filechooser;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

/**
 *
 * @author Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class ButtonList extends JScrollPane {

    private JPanel bigPanel;
    private JPanel panel;
    private JPanel restPanel;


    public ButtonList() {
        setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        this.getHorizontalScrollBar().setPreferredSize(new Dimension(9,9));

        bigPanel = new JPanel();
        panel = new JPanel();
        restPanel = new JPanel();


        panel.setLayout(new GridBagLayout());
        bigPanel.setLayout(new BorderLayout());
        restPanel.setPreferredSize(new Dimension(1,1));

        bigPanel.add(panel, BorderLayout.WEST);
        bigPanel.add(restPanel, BorderLayout.CENTER);

        this.setViewportView(bigPanel);
    }

    public void startAdding() {
        panel.removeAll();
    }

    public void stopAdding() {
        panel.doLayout();
        bigPanel.doLayout();
        panel.doLayout();
        bigPanel.repaint();

        getViewport().doLayout();
        doLayout();
        getHorizontalScrollBar().setValue(getHorizontalScrollBar().getMaximum());
        repaint();
    }

    
    public void addButton(Component comp) {
        panel.add(comp);
    }

    public void quickAddButton(Component comp) {
        panel.add(comp);
        stopAdding();
    }
}
