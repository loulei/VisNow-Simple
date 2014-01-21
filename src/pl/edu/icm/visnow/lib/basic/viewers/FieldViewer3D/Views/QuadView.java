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

package pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Views;

import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.ViewPanels.ViewPanel;
import java.awt.Dimension;

/**
 * @author  Bartosz Borucki (babor@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */

public class QuadView extends javax.swing.JPanel {
    public static final int VIEWPORT_TOP_LEFT = 0;
    public static final int VIEWPORT_TOP_RIGHT = 1;
    public static final int VIEWPORT_BOTTOM_LEFT = 2;
    public static final int VIEWPORT_BOTTOM_RIGHT = 3;


    public static final int NO_CHANGE_DIVIDERS_ON_RESIZE = 0;
    public static final int CENTER_DIVIDERS_ON_RESIZE = 1;
    private int resizeDividerBehaviour = CENTER_DIVIDERS_ON_RESIZE;


    /** Creates new form QuadView */
    public QuadView() {
        initComponents();
    }

    public void reset() {
        setViewPanel(VIEWPORT_BOTTOM_LEFT, new ViewPanel());
        setViewPanel(VIEWPORT_BOTTOM_RIGHT, new ViewPanel());
        setViewPanel(VIEWPORT_TOP_LEFT, new ViewPanel());
        setViewPanel(VIEWPORT_TOP_RIGHT, new ViewPanel());
    }

    public ViewPanel getViewPanel(int n) {
        switch(n) {
            case VIEWPORT_TOP_RIGHT:
                return getTopRightViewPanel();
            case VIEWPORT_BOTTOM_RIGHT:
                return getBottomRightViewPanel();
            case VIEWPORT_BOTTOM_LEFT:
                return getBottomLeftViewPanel();
            case VIEWPORT_TOP_LEFT:
                return getTopLeftViewPanel();
            default:
                return null;
        }
    }

    public void setViewPanel(int n, ViewPanel panel) {
        int tmph, tmpv;
        tmph = horizontalSplitPane.getDividerLocation();
        tmpv = leftSplitPane.getDividerLocation();
        switch(n) {
            case VIEWPORT_TOP_RIGHT:
                topRightViewPanel.preRemove();
                rightSplitPane.remove(topRightViewPanel);
                topRightViewPanel.setInUse(false);
                topRightViewPanel = panel;
                rightSplitPane.setTopComponent(topRightViewPanel);
                topRightViewPanel.setInUse(true);
                topRightViewPanel.postAdd();
                break;
            case VIEWPORT_BOTTOM_RIGHT:
                bottomRightViewPanel.preRemove();
                rightSplitPane.remove(bottomRightViewPanel);
                bottomRightViewPanel.setInUse(false);
                bottomRightViewPanel = panel;
                rightSplitPane.setBottomComponent(bottomRightViewPanel);
                bottomRightViewPanel.setInUse(true);
                bottomRightViewPanel.postAdd();
                break;
            case VIEWPORT_BOTTOM_LEFT:
                bottomLeftViewPanel.preRemove();
                leftSplitPane.remove(bottomLeftViewPanel);
                bottomLeftViewPanel.setInUse(false);
                bottomLeftViewPanel = panel;
                leftSplitPane.setBottomComponent(bottomLeftViewPanel);
                bottomLeftViewPanel.setInUse(true);
                bottomLeftViewPanel.postAdd();
                break;
            case VIEWPORT_TOP_LEFT:
                topLeftViewPanel.preRemove();
                leftSplitPane.remove(topLeftViewPanel);
                topLeftViewPanel.setInUse(false);
                topLeftViewPanel = panel;
                leftSplitPane.setTopComponent(topLeftViewPanel);
                topLeftViewPanel.setInUse(true);
                topLeftViewPanel.postAdd();
                break;
        }
        horizontalSplitPane.setDividerLocation(tmph);
        leftSplitPane.setDividerLocation(tmpv);
        rightSplitPane.setDividerLocation(tmpv);
        repaint();
    }

    public Dimension getViewPanelSize(int n) {
        switch(n) {
            case VIEWPORT_TOP_RIGHT:
                return getTopRightViewPanelSize();
            case VIEWPORT_BOTTOM_RIGHT:
                return getBottomRightViewPanelSize();
            case VIEWPORT_BOTTOM_LEFT:
                return getBottomLeftViewPanelSize();
            case VIEWPORT_TOP_LEFT:
                return getTopLeftViewPanelSize();
            default:
                return null;
        }
    }

    public ViewPanel getTopLeftViewPanel() {
        return topLeftViewPanel;
    }

    public void setTopLeftViewPanel(ViewPanel panel) {
        setViewPanel(QuadView.VIEWPORT_TOP_LEFT, panel);
    }

    public Dimension getTopLeftViewPanelSize() {
        return topLeftViewPanel.getSize();
    }

    public ViewPanel getBottomLeftViewPanel() {
        return bottomLeftViewPanel;
    }

    public void setBottomLeftViewPanel(ViewPanel panel) {
        setViewPanel(QuadView.VIEWPORT_BOTTOM_LEFT, panel);
    }

    public Dimension getBottomLeftViewPanelSize() {
        return bottomLeftViewPanel.getSize();
    }

    public ViewPanel getTopRightViewPanel() {
        return topRightViewPanel;
    }

    public void setTopRightViewPanel(ViewPanel panel) {
        setViewPanel(QuadView.VIEWPORT_TOP_RIGHT, panel);
    }

    public Dimension getTopRightViewPanelSize() {
        return topRightViewPanel.getSize();
    }

    public ViewPanel getBottomRightViewPanel() {
        return bottomRightViewPanel;
    }

    public void setBottomRightViewPanel(ViewPanel panel) {
        setViewPanel(QuadView.VIEWPORT_BOTTOM_RIGHT, panel);
    }

    public Dimension getBottomRightViewPanelSize() {
        return bottomRightViewPanel.getSize();
    }

    public int getHorizontalDividerLocation() {
        return horizontalSplitPane.getDividerLocation();
    }

    public void setHorizontalDividerLocation(int newLocation) {
        horizontalSplitPane.setDividerLocation(newLocation);
    }

    public void setHorizontalDividerLocation(double newPercentageLocation) {
        horizontalSplitPane.setDividerLocation(newPercentageLocation);
    }

    public int getVerticalDividerLocation() {
        return leftSplitPane.getDividerLocation();
    }

    public void setVerticalDividerLocation(int newLocation) {
        leftSplitPane.setDividerLocation(newLocation);
        rightSplitPane.setDividerLocation(newLocation);
    }

    public void setVerticalDividerLocation(double newPercentageLocation) {
        leftSplitPane.setDividerLocation(newPercentageLocation);
        rightSplitPane.setDividerLocation(newPercentageLocation);
    }

    public void centerDividers() {
        setHorizontalDividerLocation(0.5);
        setVerticalDividerLocation(0.5);
    }

    public void autoDividersLocation() {
        int lw, rw, tw, bw;

        lw = Math.max(topLeftViewPanel.getPreferredViewSize().width, bottomLeftViewPanel.getPreferredViewSize().width);
        rw = Math.max(topRightViewPanel.getPreferredViewSize().width, bottomRightViewPanel.getPreferredViewSize().width);
        tw = Math.max(topLeftViewPanel.getPreferredViewSize().height, topRightViewPanel.getPreferredViewSize().height);
        bw = Math.max(bottomLeftViewPanel.getPreferredViewSize().height, bottomRightViewPanel.getPreferredViewSize().height);

        setVerticalDividerLocation((float)tw/(float)(tw+bw));
        setHorizontalDividerLocation((float)lw/(float)(lw+rw));
    }

    public void setDividerSize(int newSize) {
        if(newSize < 3)
            newSize = 3;
        leftSplitPane.setDividerSize(newSize);
        rightSplitPane.setDividerSize(newSize);
        horizontalSplitPane.setDividerSize(newSize-2);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        horizontalSplitPane = new javax.swing.JSplitPane();
        leftSplitPane = new javax.swing.JSplitPane();
        topLeftViewPanel = new pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.ViewPanels.ViewPanel();
        bottomLeftViewPanel = new pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.ViewPanels.ViewPanel();
        rightSplitPane = new javax.swing.JSplitPane();
        topRightViewPanel = new pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.ViewPanels.ViewPanel();
        bottomRightViewPanel = new pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.ViewPanels.ViewPanel();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });
        setLayout(new java.awt.BorderLayout());

        horizontalSplitPane.setDividerLocation(400);
        horizontalSplitPane.setDividerSize(3);

        leftSplitPane.setDividerLocation(260);
        leftSplitPane.setDividerSize(5);
        leftSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        leftSplitPane.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                leftSplitPanePropertyChange(evt);
            }
        });

        topLeftViewPanel.setName("Top Left"); // NOI18N
        leftSplitPane.setLeftComponent(topLeftViewPanel);

        bottomLeftViewPanel.setName("Bottom Left"); // NOI18N
        leftSplitPane.setRightComponent(bottomLeftViewPanel);

        horizontalSplitPane.setLeftComponent(leftSplitPane);

        rightSplitPane.setDividerLocation(260);
        rightSplitPane.setDividerSize(5);
        rightSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        rightSplitPane.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                rightSplitPanePropertyChange(evt);
            }
        });

        topRightViewPanel.setName("Top Right"); // NOI18N
        rightSplitPane.setTopComponent(topRightViewPanel);

        bottomRightViewPanel.setName("Bottom Right"); // NOI18N
        rightSplitPane.setRightComponent(bottomRightViewPanel);

        horizontalSplitPane.setRightComponent(rightSplitPane);

        add(horizontalSplitPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void leftSplitPanePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_leftSplitPanePropertyChange
        if(evt.getPropertyName().equals("dividerLocation")) {
            rightSplitPane.setDividerLocation((Integer)evt.getNewValue());
        }
    }//GEN-LAST:event_leftSplitPanePropertyChange

    private void rightSplitPanePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_rightSplitPanePropertyChange
        if(evt.getPropertyName().equals("dividerLocation")) {
            leftSplitPane.setDividerLocation((Integer)evt.getNewValue());
        }
    }//GEN-LAST:event_rightSplitPanePropertyChange

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        if(resizeDividerBehaviour == CENTER_DIVIDERS_ON_RESIZE) {
            //centerDividers();
            autoDividersLocation();
        }

    }//GEN-LAST:event_formComponentResized


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.ViewPanels.ViewPanel bottomLeftViewPanel;
    private pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.ViewPanels.ViewPanel bottomRightViewPanel;
    private javax.swing.JSplitPane horizontalSplitPane;
    private javax.swing.JSplitPane leftSplitPane;
    private javax.swing.JSplitPane rightSplitPane;
    private pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.ViewPanels.ViewPanel topLeftViewPanel;
    private pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.ViewPanels.ViewPanel topRightViewPanel;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the resizeDividerBehaviour
     */
    public int getResizeDividerBehaviour() {
        return resizeDividerBehaviour;
    }

    /**
     * @param resizeDividerBehaviour the resizeDividerBehaviour to set
     */
    public void setResizeDividerBehaviour(int resizeDividerBehaviour) {
        this.resizeDividerBehaviour = resizeDividerBehaviour;
    }

}
