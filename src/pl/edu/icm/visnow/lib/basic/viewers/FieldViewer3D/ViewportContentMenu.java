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

package pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.ViewPanels.ViewPanel;

/**
 * @author  Bartosz Borucki (babor@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */

public class ViewportContentMenu extends JMenu implements ActionListener {
    private ButtonGroup buttonGroup = new ButtonGroup();
    private JRadioButtonMenuItem[] viewMenuRB = new JRadioButtonMenuItem[10];
    private int viewport = 0;
    private FieldDisplay3DFrame frame;

    public ViewportContentMenu(FieldDisplay3DFrame frame, String text, int viewport, int selected) {
        this.setText(text);
        this.viewport = viewport;
        this.frame = frame;
        initComponents();
        setSelected(selected, true);
    }

    private void initComponents() {
        viewMenuRB[0] = new JRadioButtonMenuItem("3D view");
        viewMenuRB[1] = new JRadioButtonMenuItem("x slice (YZ plane)");
        viewMenuRB[2] = new JRadioButtonMenuItem("y slice (XZ plane)");
        viewMenuRB[3] = new JRadioButtonMenuItem("z slice (XY plane)");
        viewMenuRB[4] = new JRadioButtonMenuItem("x slice transposed (ZY plane)");
        viewMenuRB[5] = new JRadioButtonMenuItem("y slice transposed (ZX plane)");
        viewMenuRB[6] = new JRadioButtonMenuItem("z slice transposed (YX plane)");
        viewMenuRB[7] = new JRadioButtonMenuItem("custom slice");
        viewMenuRB[8] = new JRadioButtonMenuItem("2D view");
        viewMenuRB[9] = new JRadioButtonMenuItem("none");
        for (int i = 0; i < viewMenuRB.length; i++) {
            buttonGroup.add(viewMenuRB[i]);
            this.add(viewMenuRB[i]);
            viewMenuRB[i].addActionListener(this);
        }
    }

    public void setSelected(int selectType, boolean silent) {
        if(selectType < 0 || selectType >= viewMenuRB.length) {
            for (int i = 0; i < viewMenuRB.length; i++) {
                viewMenuRB[i].setSelected(false);
                return;
            }
        }

        switch(selectType) {
            case ViewPanel.VIEW_3D:
                viewMenuRB[0].setSelected(true);
                break;
            case ViewPanel.VIEW_SLICE_I:
                viewMenuRB[1].setSelected(true);
                break;
            case ViewPanel.VIEW_SLICE_J:
                viewMenuRB[2].setSelected(true);
                break;
            case ViewPanel.VIEW_SLICE_K:
                viewMenuRB[3].setSelected(true);
                break;
            case ViewPanel.VIEW_SLICE_I_TRANS:
                viewMenuRB[4].setSelected(true);
                break;
            case ViewPanel.VIEW_SLICE_J_TRANS:
                viewMenuRB[5].setSelected(true);
                break;
            case ViewPanel.VIEW_SLICE_K_TRANS:
                viewMenuRB[6].setSelected(true);
                break;
            case ViewPanel.VIEW_SLICE_CUSTOM:
                viewMenuRB[7].setSelected(true);
                break;
            case ViewPanel.VIEW_2D:
                viewMenuRB[8].setSelected(true);
                break;
            case ViewPanel.VIEW_NONE:
                viewMenuRB[9].setSelected(true);
                break;
        }

        updateDisabled();
    }

    public void updateDisabled() {
        if(frame == null)
            return;

        if(!viewMenuRB[0].isSelected()) {
            viewMenuRB[0].setEnabled(!frame.getViewsStorage().getDisplay3DView().isInUse());
        } else {
            viewMenuRB[0].setEnabled(true);
        }

        if(!viewMenuRB[1].isSelected()) {
            viewMenuRB[1].setEnabled(!frame.getViewsStorage().getSliceXView().isInUse());
        } else {
            viewMenuRB[1].setEnabled(true);
        }

        if(!viewMenuRB[2].isSelected()) {
            viewMenuRB[2].setEnabled(!frame.getViewsStorage().getSliceYView().isInUse());
        } else {
            viewMenuRB[2].setEnabled(true);
        }

        if(!viewMenuRB[3].isSelected()) {
            viewMenuRB[3].setEnabled(!frame.getViewsStorage().getSliceZView().isInUse());
        } else {
            viewMenuRB[3].setEnabled(true);
        }

        if(!viewMenuRB[4].isSelected()) {
            viewMenuRB[4].setEnabled(!frame.getViewsStorage().getSliceXTransView().isInUse());
        } else {
            viewMenuRB[4].setEnabled(true);
        }

        if(!viewMenuRB[5].isSelected()) {
            viewMenuRB[5].setEnabled(!frame.getViewsStorage().getSliceYTransView().isInUse());
        } else {
            viewMenuRB[5].setEnabled(true);
        }

        if(!viewMenuRB[6].isSelected()) {
            viewMenuRB[6].setEnabled(!frame.getViewsStorage().getSliceZTransView().isInUse());
        } else {
            viewMenuRB[6].setEnabled(true);
        }

        if(!viewMenuRB[7].isSelected()) {
            viewMenuRB[7].setEnabled(!frame.getViewsStorage().getCustomSliceView().isInUse());
        } else {
            viewMenuRB[7].setEnabled(true);
        }

        if(!viewMenuRB[8].isSelected()) {
            viewMenuRB[8].setEnabled(!frame.getViewsStorage().getBasicView().isInUse());
        } else {
            viewMenuRB[8].setEnabled(true);
        }

    }

    public void actionPerformed(ActionEvent e) {
        if(this.frame == null)
            return;

        Object tmp = e.getSource();
        if(!(tmp instanceof JRadioButtonMenuItem))
            return;
        JRadioButtonMenuItem src = (JRadioButtonMenuItem)tmp;

        if(src == viewMenuRB[0]) {
            frame.setViewportConents(viewport, ViewPanel.VIEW_3D);
        } else if(src == viewMenuRB[1]) {
            frame.setViewportConents(viewport, ViewPanel.VIEW_SLICE_I);
        } else if(src == viewMenuRB[2]) {
            frame.setViewportConents(viewport, ViewPanel.VIEW_SLICE_J);
        } else if(src == viewMenuRB[3]) {
            frame.setViewportConents(viewport, ViewPanel.VIEW_SLICE_K);
        } else if(src == viewMenuRB[4]) {
            frame.setViewportConents(viewport, ViewPanel.VIEW_SLICE_I_TRANS);
        } else if(src == viewMenuRB[5]) {
            frame.setViewportConents(viewport, ViewPanel.VIEW_SLICE_J_TRANS);
        } else if(src == viewMenuRB[6]) {
            frame.setViewportConents(viewport, ViewPanel.VIEW_SLICE_K_TRANS);
        } else if(src == viewMenuRB[7]) {
            frame.setViewportConents(viewport, ViewPanel.VIEW_SLICE_CUSTOM);
        } else if(src == viewMenuRB[8]) {
            frame.setViewportConents(viewport, ViewPanel.VIEW_2D);
        } else if(src == viewMenuRB[9]) {
            frame.setViewportConents(viewport, ViewPanel.VIEW_NONE);
        }
        
    }



}
