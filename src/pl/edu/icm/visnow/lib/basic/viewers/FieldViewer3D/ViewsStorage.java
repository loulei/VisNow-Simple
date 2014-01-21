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

import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.ViewPanels.CustomOrthosliceViewPanel;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.ViewPanels.CustomSliceViewPanel;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.ViewPanels.Display3DViewPanel;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.ViewPanels.OrthosliceViewPanel;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.ViewPanels.BasicViewPanel;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.ViewPanels.ViewPanel;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;


/**
 * @author  Bartosz Borucki (babor@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */

public class ViewsStorage {
    //private ViewPanel emptyView = null;
    private Display3DViewPanel display3DView = null;
    private OrthosliceViewPanel sliceXView = null;
    private OrthosliceViewPanel sliceYView = null;
    private OrthosliceViewPanel sliceZView = null;
    private OrthosliceViewPanel sliceXTransView = null;
    private OrthosliceViewPanel sliceYTransView = null;
    private OrthosliceViewPanel sliceZTransView = null;
    private CustomSliceViewPanel customSliceView = null;
    private CustomOrthosliceViewPanel[] customOrthosliceViews = new CustomOrthosliceViewPanel[3];
    private BasicViewPanel basicView = null;

    private GlobalParams params = null;

    public ViewsStorage(GlobalParams params) {
        this.params = params;

        SwingInstancer.swingRunAndWait(new Runnable() {
            public void run() {
                display3DView = new Display3DViewPanel();
                basicView = new BasicViewPanel();
                sliceXView = new OrthosliceViewPanel(0, false);
                sliceYView = new OrthosliceViewPanel(1, false);
                sliceZView = new OrthosliceViewPanel(2, false);
                sliceXTransView = new OrthosliceViewPanel(0, true);
                sliceYTransView = new OrthosliceViewPanel(1, true);
                sliceZTransView = new OrthosliceViewPanel(2, true);
                customSliceView = new CustomSliceViewPanel();
                customOrthosliceViews[0] = new CustomOrthosliceViewPanel(0);
                customOrthosliceViews[1] = new CustomOrthosliceViewPanel(1);
                customOrthosliceViews[2] = new CustomOrthosliceViewPanel(2);
            }
        });

    }

    public ViewPanel getView(int type) {
        switch(type) {
            case ViewPanel.VIEW_3D:
                return getDisplay3DView();
            case ViewPanel.VIEW_SLICE_I:
                return getSliceXView();
            case ViewPanel.VIEW_SLICE_J:
                return getSliceYView();
            case ViewPanel.VIEW_SLICE_K:
                return getSliceZView();
            case ViewPanel.VIEW_SLICE_I_TRANS:
                return getSliceXTransView();
            case ViewPanel.VIEW_SLICE_J_TRANS:
                return getSliceYTransView();
            case ViewPanel.VIEW_SLICE_K_TRANS:
                return getSliceZTransView();
            case ViewPanel.VIEW_SLICE_CUSTOM:
                return getCustomSliceView();
            case ViewPanel.VIEW_SLICE_CUSTOM_ORTHO_0:
                return getCustomOrthosliceView(0);
            case ViewPanel.VIEW_SLICE_CUSTOM_ORTHO_1:
                return getCustomOrthosliceView(1);
            case ViewPanel.VIEW_SLICE_CUSTOM_ORTHO_2:
                return getCustomOrthosliceView(2);
            case ViewPanel.VIEW_2D:
                return getBasicView();
            default:
                return getEmptyView();
        }
    }


    /**
     * @return the emptyView
     */
    public ViewPanel getEmptyView() {
        return new ViewPanel("",ViewPanel.VIEW_NONE);
    }

    /**
     * @return the display3DView
     */
    public Display3DViewPanel getDisplay3DView() {
        return display3DView;
    }

    /**
     * @return the basicView
     */
    public BasicViewPanel getBasicView() {
        return basicView;
    }

    /**
     * @return the sliceXView
     */
    public OrthosliceViewPanel getSliceXView() {
        return sliceXView;
    }

    /**
     * @return the sliceYView
     */
    public OrthosliceViewPanel getSliceYView() {
        return sliceYView;
    }

    /**
     * @return the sliceZView
     */
    public OrthosliceViewPanel getSliceZView() {
        return sliceZView;
    }

    /**
     * @return the sliceXTransView
     */
    public OrthosliceViewPanel getSliceXTransView() {
        return sliceXTransView;
    }

    /**
     * @return the sliceYTransView
     */
    public OrthosliceViewPanel getSliceYTransView() {
        return sliceYTransView;
    }

    /**
     * @return the sliceZTransView
     */
    public OrthosliceViewPanel getSliceZTransView() {
        return sliceZTransView;
    }

    /**
     * @return the customSliceView
     */
    public CustomSliceViewPanel getCustomSliceView() {
        return customSliceView;
    }

    public CustomOrthosliceViewPanel getCustomOrthosliceView(int axis) {
        if(axis < 0 || axis > 2)
            return null;
        
        return customOrthosliceViews[axis];
    }

    public CustomOrthosliceViewPanel getCustomOrthosliceView0() {
        return getCustomOrthosliceView(0);
    }

    public CustomOrthosliceViewPanel getCustomOrthosliceView1() {
        return getCustomOrthosliceView(1);
    }

    public CustomOrthosliceViewPanel getCustomOrthosliceView2() {
        return getCustomOrthosliceView(2);
    }

    /**
     * @return the params
     */
    public GlobalParams getParams() {
        return params;
    }

    /**
     * @param params the params to set
     */
    public void setParams(GlobalParams params) {
        this.params = params;
    }

    public ViewPanel[] getViewsInUse() {
        int n = 0, i = 0;
        if(getDisplay3DView().isInUse()) n++;
        if(getSliceXView().isInUse()) n++;
        if(getSliceYView().isInUse()) n++;
        if(getSliceZView().isInUse()) n++;
        if(getSliceXTransView().isInUse()) n++;
        if(getSliceYTransView().isInUse()) n++;
        if(getSliceZTransView().isInUse()) n++;
        if(getCustomSliceView().isInUse()) n++;
        if(getCustomOrthosliceView0().isInUse()) n++;
        if(getCustomOrthosliceView1().isInUse()) n++;
        if(getCustomOrthosliceView2().isInUse()) n++;
        if(getBasicView().isInUse()) n++;

        ViewPanel[] out = new ViewPanel[n];
        if(display3DView.isInUse()) {
            out[i++] = display3DView;
        }
        if(sliceXView.isInUse()) {
            out[i++] = sliceXView;
        }
        if(sliceYView.isInUse()) {
            out[i++] = sliceYView;
        }
        if(sliceZView.isInUse()) {
            out[i++] = sliceZView;
        }
        if(sliceXTransView.isInUse()) {
            out[i++] = sliceXTransView;
        }
        if(sliceYTransView.isInUse()) {
            out[i++] = sliceYTransView;
        }
        if(sliceZTransView.isInUse()) {
            out[i++] = sliceZTransView;
        }
        if(customSliceView.isInUse()) {
            out[i++] = customSliceView;
        }
        if(customOrthosliceViews[0].isInUse()) {
            out[i++] = customOrthosliceViews[0];
        }
        if(customOrthosliceViews[1].isInUse()) {
            out[i++] = customOrthosliceViews[1];
        }
        if(customOrthosliceViews[2].isInUse()) {
            out[i++] = customOrthosliceViews[2];
        }
        if(basicView.isInUse()) {
            out[i++] = basicView;
        }
        return out;
    }

    public ViewPanel[] getAllViews() {
        ViewPanel[] out = new ViewPanel[12];
        out[0] = getDisplay3DView();
        out[1] = getSliceXView();
        out[2] = getSliceYView();
        out[3] = getSliceZView();
        out[4] = getSliceXTransView();
        out[5] = getSliceYTransView();
        out[6] = getSliceZTransView();
        out[7] = getCustomSliceView();
        out[8] = getCustomOrthosliceView0();
        out[9] = getCustomOrthosliceView1();
        out[10] = getCustomOrthosliceView2();
        out[11] = getBasicView();
        return out;
    }

}
