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

package pl.edu.icm.visnow.system.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;



/**
 *
 * @author Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class JExpandableList extends JComponent {


    private Component block = new JPanel();
    private Vector<Component> components = new Vector<Component>();
    private JExpandableList parentList = null;

    private JScrollPane parentPane = null;

    //private JScrollBar parentBar = null;

    //public void setParentScrollBar(JScrollBar bar) {
    //    parentBar = bar;
    //}

    public void setParentScrollPane(JScrollPane pane) {
        this.parentPane = pane;
    }

    public void setParentList(JExpandableList list) {
        this.parentList = list;
    }

    public JExpandableList getParentList() {
        return parentList;
    }



    public JExpandableList(Color bg) {
        block.setBackground(bg);
    }

    //<editor-fold defaultstate="collapsed" desc=" Add/remove/replace component ">
    @Override
    public void remove(Component comp) {
        components.remove(comp);
        repack();
    }

    //@Override
    public Component addEl(Component comp) {
        components.add(comp);
        repack();
        return comp;
    }

    @Override
    public Component add(Component comp, int place) {
        components.add(place, comp);
        repack();
        return comp;
    }

    public Component replace(int place, Component comp) {
        components.remove(place);
        components.add(place, comp);
        repack();
        return comp;
    }

    public Component replace(Component oldC, Component newC) {
        int place = components.indexOf(oldC);
        components.remove(place);
        components.add(place, newC);
        repack();
        return newC;
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Repack ">

    protected void repack2() {
        this.removeAll();
        if(components.isEmpty()) {
            VNSwingUtils.setFillerComponent(this, block);
            return;
        }
        GridBagLayout l = new GridBagLayout();

        this.setLayout(l);
        int i=0;
        for(; i<components.size(); ++i) {
            GridBagConstraints c = new GridBagConstraints(
                    0,i,
                    1,1,
                    1.,1.,
                    GridBagConstraints.NORTHWEST,
                    GridBagConstraints.BOTH,
                    new Insets(1,1,1,1),
                    10,5);
            this.add(components.elementAt(i),c);
        }
        this.add(block, new GridBagConstraints(
                    0,i,
                    1,1,
                    1.,1.,
                    GridBagConstraints.NORTHWEST,
                    GridBagConstraints.BOTH,
                    new Insets(1,1,1,1),
                    10,1));
        this.refresh();
    }

    protected void repack() {
        this.removeAll();
        if(components.isEmpty()) {
            VNSwingUtils.setFillerComponent(this, block);
            return;
        }

        JSplitPane split = new JSplitPane();
        prepareSplit(split);

        this.setLayout(new BorderLayout());
        super.add(split, BorderLayout.CENTER);
        //SwingUtils.setFillerComponent(this, split);
        split.setTopComponent(components.elementAt(0));
        JSplitPane last = split;
        
        for(int i=1; i<components.size(); ++i) {
            split = new JSplitPane();
            prepareSplit(split);
            
            split.setTopComponent(components.elementAt(i));
            last.setBottomComponent(split);

            last = split;
        }
        last.setBottomComponent(block);

        refresh();
    }


    private void prepareSplit(JSplitPane split) {
        split.setDividerSize(0);
        split.setOrientation(JSplitPane.VERTICAL_SPLIT);
        split.setBorder(null);
    }
    //</editor-fold>

    public void addPair(Component first, AbstractButton switchToSecond, Component second, AbstractButton switchToFirst) {
        addEl(first);
        switchToSecond.addActionListener(new SwitchActionListener(this, first, second));
        switchToFirst.addActionListener(new SwitchActionListener(this, second, first));
    }

    protected void refresh() {
        this.doLayout();
        repaint();

        if(parentList != null) {
            parentList.repack();
        }

        if(parentPane != null) {
            VNSwingUtils.setConstantHeight(this, 5000);//TODO: correctHeight
            this.validate();
            //parentBar.setEnabled(true);
            //parentBar.setMinimum(0);
            //parentBar.setMaximum(1000);
            this.getParent().validate();
        }

    }

}
