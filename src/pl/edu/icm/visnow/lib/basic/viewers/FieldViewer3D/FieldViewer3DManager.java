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

import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.DataProvider.DataProvider;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.DataProvider.DataProviderListener;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.DataProvider.DataProviderParams;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.*;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.ViewPanels.*;

/**
 * @author  Bartosz Borucki (babor@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class FieldViewer3DManager implements DataProviderListener, GlobalParamsListener, ComponentListener, ViewPanelListener, GeometryParamsListener, ChangeListener {
    private ViewsStorage viewsStorage = null;
    private DataProvider dataProvider = null;
    private DataProviderParams dataProviderParams = null;
    private GeometryParams geometryParams = null;
    private CalculableParams calculableParams = null;
    private FieldDisplay3DFrame frame = null;

    public FieldViewer3DManager(ViewsStorage viewsStorage, DataProvider dataProvider, FieldDisplay3DFrame frame) {
        this.viewsStorage = viewsStorage;
        this.dataProvider = dataProvider;
        this.frame = frame;
        this.dataProvider.addDataProviderListener(this);
        this.dataProviderParams = dataProvider.getParams();
        viewsStorage.getParams().addGlobalParamsListener(this);

        viewsStorage.getSliceXView().addComponentListener(this);
        viewsStorage.getSliceYView().addComponentListener(this);
        viewsStorage.getSliceZView().addComponentListener(this);
        viewsStorage.getSliceXTransView().addComponentListener(this);
        viewsStorage.getSliceYTransView().addComponentListener(this);
        viewsStorage.getSliceZTransView().addComponentListener(this);
        viewsStorage.getSliceXView().addViewPanelListener(this);
        viewsStorage.getSliceYView().addViewPanelListener(this);
        viewsStorage.getSliceZView().addViewPanelListener(this);
        viewsStorage.getSliceXTransView().addViewPanelListener(this);
        viewsStorage.getSliceYTransView().addViewPanelListener(this);
        viewsStorage.getSliceZTransView().addViewPanelListener(this);

        viewsStorage.getCustomSliceView().addViewPanelListener(this);

        viewsStorage.getCustomOrthosliceView(0).addViewPanelListener(this);
        viewsStorage.getCustomOrthosliceView(1).addViewPanelListener(this);
        viewsStorage.getCustomOrthosliceView(2).addViewPanelListener(this);

        viewsStorage.getBasicView().addViewPanelListener(this);

        updateViewsGeometryTool();
    }


    public void updateAllViewsInUse() {
        if(dataProvider == null || dataProviderParams == null)
            return;

        if(viewsStorage.getSliceXView().isInUse()) {
            updateViewSliceX();
        }

        if(viewsStorage.getSliceYView().isInUse()) {
            updateViewSliceY();
        }

        if(viewsStorage.getSliceZView().isInUse()) {
            updateViewSliceZ();
        }

        if(viewsStorage.getSliceXTransView().isInUse()) {
            updateViewSliceXTrans();
        }

        if(viewsStorage.getSliceYTransView().isInUse()) {
            updateViewSliceYTrans();
        }

        if(viewsStorage.getSliceZTransView().isInUse()) {
            updateViewSliceZTrans();
        }

        if(viewsStorage.getCustomSliceView().isInUse()) {
            updateViewCustomSlice();
        }

        for (int i = 0; i < 3; i++) {
            if(viewsStorage.getCustomOrthosliceView(i).isInUse())
                updateViewCustomOrthoSlice(i);
        }

        if(viewsStorage.getBasicView().isInUse()) {
            updateViewCustomSlice();
        }
        
        //updateViewsScaling();
    }

    public void updateAllViews() {
        if(dataProvider == null || dataProviderParams == null)
            return;

        updateViewSliceX();
        updateViewSliceY();
        updateViewSliceZ();
        updateViewSliceXTrans();
        updateViewSliceYTrans();
        updateViewSliceZTrans();
        updateViewCustomSlice();
        for (int i = 0; i < 3; i++) {
            updateViewCustomOrthoSlice(i);
        }
        updateViewSingle();
        
        updateViewsScaling();
    }


    public void updateViewsScaling() {
        if(viewsStorage.getParams().getScalingMode() == OrthosliceViewPanel.SCALING_EXTERNAL) {
            scaleViewsInUseEqually();
        } else if(viewsStorage.getParams().getScalingMode() == OrthosliceViewPanel.SCALING_AUTO) {
            setAllViewsAutoscale();
        } else if(viewsStorage.getParams().getScalingMode() == OrthosliceViewPanel.SCALING_MANUAL) {
            setAllViewsManualScaling();
        }
    }

    public void updateViewsType(int viewType) {
        if(dataProvider == null || dataProviderParams == null)
            return;

        switch(viewType) {
            case ViewPanel.VIEW_3D:
                break;
            case ViewPanel.VIEW_SLICE_I:
                updateViewSliceX();
                break;
            case ViewPanel.VIEW_SLICE_J:
                updateViewSliceY();
                break;
            case ViewPanel.VIEW_SLICE_K:
                updateViewSliceZ();
                break;
            case ViewPanel.VIEW_SLICE_I_TRANS:
                updateViewSliceXTrans();
                break;
            case ViewPanel.VIEW_SLICE_J_TRANS:
                updateViewSliceYTrans();
                break;
            case ViewPanel.VIEW_SLICE_K_TRANS:
                updateViewSliceZTrans();
                break;
            case ViewPanel.VIEW_SLICE_CUSTOM:
                updateViewCustomSlice();
                break;
            case ViewPanel.VIEW_SLICE_CUSTOM_ORTHO_0:
                updateViewCustomOrthoSlice(0);
                break;
            case ViewPanel.VIEW_SLICE_CUSTOM_ORTHO_1:
                updateViewCustomOrthoSlice(1);
                break;
            case ViewPanel.VIEW_SLICE_CUSTOM_ORTHO_2:
                updateViewCustomOrthoSlice(2);
                break;
            case ViewPanel.VIEW_2D:
                updateViewSingle();
                break;
        }
    }

   public void updateViewSliceX()
   {
      if (dataProvider == null || dataProviderParams == null)
         return;


      viewsStorage.getSliceXView().setDataImage(dataProvider.getOrthoSlice(0), dataProviderParams.getOrthosliceNumber(0), dataProviderParams.getOrthosliceRealPosition(0), dataProviderParams.getOrthosliceUPPW(0, false), dataProviderParams.getOrthosliceUPPH(0, false), dataProviderParams.getUpp());

      if (dataProvider.getIsolines(0) != null)
         viewsStorage.getSliceXView().setIsolines(dataProvider.getIsolines(0));
      
      if (dataProvider.getAuxField() != null)
         viewsStorage.getSliceXView().setIsoline(dataProvider.getIsoline(0));

      if (dataProvider.getOverlayField() != null || dataProviderParams.isSimpleOverlay())
         viewsStorage.getSliceXView().setOverlayImage(dataProvider.getOrthoSliceOverlay(0), dataProviderParams.getOverlayOpacity());
      else
         viewsStorage.getSliceXView().setOverlayImage(null, 1.0f);

   }

   public void updateViewSliceY()
   {
      if (dataProvider == null || dataProviderParams == null)
         return;

      viewsStorage.getSliceYView().setDataImage(dataProvider.getOrthoSlice(1), dataProviderParams.getOrthosliceNumber(1), dataProviderParams.getOrthosliceRealPosition(1), dataProviderParams.getOrthosliceUPPW(1, false), dataProviderParams.getOrthosliceUPPH(1, false), dataProviderParams.getUpp());

      if (dataProvider.getIsolines(1) != null)
         viewsStorage.getSliceYView().setIsolines(dataProvider.getIsolines(1));
         
      if (dataProvider.getAuxField() != null)
         viewsStorage.getSliceYView().setIsoline(dataProvider.getIsoline(1));

      if (dataProvider.getOverlayField() != null || dataProviderParams.isSimpleOverlay())
         viewsStorage.getSliceYView().setOverlayImage(dataProvider.getOrthoSliceOverlay(1), dataProviderParams.getOverlayOpacity());
      else
         viewsStorage.getSliceYView().setOverlayImage(null, 1.0f);
   }

   public void updateViewSliceZ()
   {
      if (dataProvider == null || dataProviderParams == null)
         return;

      viewsStorage.getSliceZView().setDataImage(dataProvider.getOrthoSlice(2), dataProviderParams.getOrthosliceNumber(2), dataProviderParams.getOrthosliceRealPosition(2), dataProviderParams.getOrthosliceUPPW(2, false), dataProviderParams.getOrthosliceUPPH(2, false), dataProviderParams.getUpp());

      if (dataProvider.getIsolines(2) != null)
         viewsStorage.getSliceZView().setIsolines(dataProvider.getIsolines(2));
      
      if (dataProvider.getAuxField() != null)
         viewsStorage.getSliceZView().setIsoline(dataProvider.getIsoline(2));
                 

      if (dataProvider.getOverlayField() != null || dataProviderParams.isSimpleOverlay())
         viewsStorage.getSliceZView().setOverlayImage(dataProvider.getOrthoSliceOverlay(2), dataProviderParams.getOverlayOpacity());
      else
         viewsStorage.getSliceZView().setOverlayImage(null, 1.0f);
   }

   public void updateViewSliceXTrans()
   {
      if (dataProvider == null || dataProviderParams == null)
         return;

      viewsStorage.getSliceXTransView().setDataImage(dataProvider.getOrthoSlice(0), dataProviderParams.getOrthosliceNumber(0), dataProviderParams.getOrthosliceRealPosition(0), dataProviderParams.getOrthosliceUPPW(0, true), dataProviderParams.getOrthosliceUPPH(0, true), dataProviderParams.getUpp());

      if (dataProvider.getIsolines(0) != null)
         viewsStorage.getSliceXTransView().setIsolines(dataProvider.getIsolines(0));
      
      if (dataProvider.getAuxField() != null)
         viewsStorage.getSliceXTransView().setIsoline(dataProvider.getIsoline(0));

      if (dataProvider.getOverlayField() != null || dataProviderParams.isSimpleOverlay())
         viewsStorage.getSliceXTransView().setOverlayImage(dataProvider.getOrthoSliceOverlay(0), dataProviderParams.getOverlayOpacity());
      else
         viewsStorage.getSliceXTransView().setOverlayImage(null, 1.0f);

   }

   public void updateViewSliceYTrans()
   {
      if (dataProvider == null || dataProviderParams == null)
         return;

      viewsStorage.getSliceYTransView().setDataImage(dataProvider.getOrthoSlice(1), dataProviderParams.getOrthosliceNumber(1), dataProviderParams.getOrthosliceRealPosition(1), dataProviderParams.getOrthosliceUPPW(1, true), dataProviderParams.getOrthosliceUPPH(1, true), dataProviderParams.getUpp());

      if (dataProvider.getIsolines(1) != null)
         viewsStorage.getSliceYTransView().setIsolines(dataProvider.getIsolines(1));
      
      if (dataProvider.getAuxField() != null)
         viewsStorage.getSliceYTransView().setIsoline(dataProvider.getIsoline(1));

      if (dataProvider.getOverlayField() != null || dataProviderParams.isSimpleOverlay())
         viewsStorage.getSliceYTransView().setOverlayImage(dataProvider.getOrthoSliceOverlay(1), dataProviderParams.getOverlayOpacity());
      else
         viewsStorage.getSliceYTransView().setOverlayImage(null, 1.0f);

   }

   public void updateViewSliceZTrans()
   {
      if (dataProvider == null || dataProviderParams == null)
         return;

      viewsStorage.getSliceZTransView().setDataImage(dataProvider.getOrthoSlice(2), dataProviderParams.getOrthosliceNumber(2), dataProviderParams.getOrthosliceRealPosition(2), dataProviderParams.getOrthosliceUPPW(2, true), dataProviderParams.getOrthosliceUPPH(2, true), dataProviderParams.getUpp());

      if (dataProvider.getIsolines(2) != null)
         viewsStorage.getSliceZTransView().setIsolines(dataProvider.getIsolines(2));
      
      if (dataProvider.getAuxField() != null)
         viewsStorage.getSliceZTransView().setIsoline(dataProvider.getIsoline(2));

      if (dataProvider.getOverlayField() != null || dataProviderParams.isSimpleOverlay())
         viewsStorage.getSliceZTransView().setOverlayImage(dataProvider.getOrthoSliceOverlay(2), dataProviderParams.getOverlayOpacity());
      else
         viewsStorage.getSliceZTransView().setOverlayImage(null, 1.0f);

   }

    private void switchToManualScaling() {
        if(viewsStorage.getParams().getScalingMode() == OrthosliceViewPanel.SCALING_MANUAL)
            return;

        viewsStorage.getParams().setScalingMode(OrthosliceViewPanel.SCALING_MANUAL);
    }

    private void updateViewCustomSlice() {
        if(dataProvider == null || dataProviderParams == null)
            return;

        viewsStorage.getCustomSliceView().setDataImage(dataProvider.getCustomSliceImage(), dataProviderParams.getCustomPlaneUPPW(), dataProviderParams.getCustomPlaneUPPH(), dataProviderParams.getUpp());

        if(dataProvider.getAuxField() != null) {
            //viewsStorage.getCustomSliceView().setIsoline(dataProvider.getIsoline(0));
        }

        if(dataProvider.getOverlayField() != null) {
            //viewsStorage.getCustomSliceView().setDataImage(dataProvider.getCustomSliceImage(), dataProviderParams.getCustomPlaneUPPW(), dataProviderParams.getCustomPlaneUPPH(), dataProviderParams.getUpp());
        }

    }

    public void updateViewSingle() {
        if(dataProvider == null || dataProviderParams == null)
            return;


        viewsStorage.getBasicView().setDataImage(dataProvider.getSingleImage(), dataProviderParams.getSingleRealPosition(), dataProviderParams.getSingleUPPW(), dataProviderParams.getSingleUPPH(), dataProviderParams.getUpp());

        if(dataProvider.getOverlayField() != null || dataProviderParams.isSimpleOverlay()) {
            viewsStorage.getBasicView().setOverlayImage(dataProvider.getOrthoSliceOverlay(0), dataProviderParams.getOverlayOpacity());
        }

    }

    public void scaleViewsInUse(float scale) {
        ViewPanel[] viewsInUse = viewsStorage.getViewsInUse();
        for (int i = 0; i < viewsInUse.length; i++) {
            if(viewsInUse[i] instanceof OrthosliceViewPanel) {
                ((OrthosliceViewPanel)viewsInUse[i]).setImageScale(scale);
            }
        }
    }

    public void scaleViewsInUseEqually() {
        ViewPanel[] viewsInUse = viewsStorage.getViewsInUse();
        float newScale;
        float scale = -1.0f;
        Dimension myDim;
        float imgWidth,imgHeight;
        float xs,ys;
        for (int i = 0; i < viewsInUse.length; i++) {
            if(!(viewsInUse[i] instanceof OrthosliceViewPanel))
                continue;

            if(((OrthosliceViewPanel)viewsInUse[i]).getDataImage() == null)
                continue;

            myDim = viewsInUse[i].getSize();
            imgWidth = ((OrthosliceViewPanel)viewsInUse[i]).getPrefferedDataImageWidth();
            imgHeight = ((OrthosliceViewPanel)viewsInUse[i]).getPrefferedDataImageHeight();
            xs = (float)myDim.width/imgWidth;
            ys = (float)myDim.height/imgHeight;
            newScale = Math.min(xs, ys);
            if(scale == -1.0f) {
                scale = newScale;
            } else if(newScale < scale)
                scale = newScale;
        }
        for (int i = 0; i < viewsInUse.length; i++) {
            if(viewsInUse[i] instanceof OrthosliceViewPanel) {
                ((OrthosliceViewPanel)viewsInUse[i]).setImageScale(scale);
            } else if(viewsInUse[i] instanceof CustomOrthosliceViewPanel) {
                ((CustomOrthosliceViewPanel)viewsInUse[i]).resetZoomAndPosition();
                ((CustomOrthosliceViewPanel)viewsInUse[i]).setScalingMode(CustomOrthosliceViewPanel.SCALING_AUTO);
            }


        }
    }

    public void setAllViewsAutoscale() {
        ViewPanel[] views = viewsStorage.getAllViews();
        for (int i = 0; i < views.length; i++) {
            if(views[i] instanceof OrthosliceViewPanel)
                ((OrthosliceViewPanel)views[i]).setScalingMode(OrthosliceViewPanel.SCALING_AUTO);
            if(views[i] instanceof CustomOrthosliceViewPanel) {
                ((CustomOrthosliceViewPanel)views[i]).resetZoomAndPosition();
                ((CustomOrthosliceViewPanel)views[i]).setScalingMode(CustomOrthosliceViewPanel.SCALING_AUTO);
            }
            if(views[i] instanceof BasicViewPanel)
                ((BasicViewPanel)views[i]).setScalingMode(BasicViewPanel.SCALING_AUTO);
}

    }

    public void setAllViewsManualScaling() {
        ViewPanel[] views = viewsStorage.getAllViews();
        for (int i = 0; i < views.length; i++) {
            if(views[i] instanceof OrthosliceViewPanel)
                ((OrthosliceViewPanel)views[i]).setScalingMode(OrthosliceViewPanel.SCALING_MANUAL);
            if(views[i] instanceof CustomOrthosliceViewPanel)
                ((CustomOrthosliceViewPanel)views[i]).setScalingMode(CustomOrthosliceViewPanel.SCALING_MANUAL);
            if(views[i] instanceof BasicViewPanel)
                ((BasicViewPanel)views[i]).setScalingMode(BasicViewPanel.SCALING_MANUAL);
        }

    }


    public void setAllViewsPaintInfo(boolean paint) {
        ViewPanel[] views = viewsStorage.getAllViews();
        for (int i = 0; i < views.length; i++) {
            if(views[i] instanceof OrthosliceViewPanel)
                ((OrthosliceViewPanel)views[i]).setPaintSliceInfo(paint);
            if(views[i] instanceof CustomSliceViewPanel)
                ((CustomSliceViewPanel)views[i]).setPaintSliceInfo(paint);
            if(views[i] instanceof CustomOrthosliceViewPanel)
                ((CustomOrthosliceViewPanel)views[i]).setPaintSliceInfo(paint);
        }
    }

    public void setAllViewsSliceLinesMode(int mode) {
        ViewPanel[] views = viewsStorage.getAllViews();
        for (int i = 0; i < views.length; i++) {
            if(views[i] instanceof OrthosliceViewPanel)
                ((OrthosliceViewPanel)views[i]).setSliceLinesMode(mode);
            //if(views[i] instanceof CustomSliceViewPanel)
                //((CustomSliceViewPanel)views[i]).setSliceLinesMode(mode);
            if(views[i] instanceof CustomOrthosliceViewPanel)
                ((CustomOrthosliceViewPanel)views[i]).setSliceLinesMode(mode);
        }
    }
    
    public void updateMode() {
        ViewPanel[] views = viewsStorage.getAllViews();
        for (int i = 0; i < views.length; i++) {
            if(views[i] instanceof OrthosliceViewPanel) {
                switch(viewsStorage.getParams().getMode()) {
                    case GlobalParams.MODE_ORTHOSLICES:
                        ((OrthosliceViewPanel)views[i]).setMode(OrthosliceViewPanel.MODE_ORTHOSLICES);
                        break;
                    case GlobalParams.MODE_CUSTOMSLICE:
                        ((OrthosliceViewPanel)views[i]).setMode(OrthosliceViewPanel.MODE_CUSTOMSLICE);
                        break;
                    case GlobalParams.MODE_CUSTOMORTHOSLICES:
                        break;
                }
            }
        }

        update3DViewContents();
    }


    public void update3DViewContents() {
        viewsStorage.getDisplay3DView().getDisplay3DPanel().removeChild(dataProvider.getCustomOrthoSlices3DPlanes());
        viewsStorage.getDisplay3DView().getDisplay3DPanel().removeChild(dataProvider.getCustomSlice3DPlane());
        if(viewsStorage.getParams().getMode() == GlobalParams.MODE_CUSTOMSLICE) {
            if(viewsStorage.getParams().isPlanes3DVisible())
                viewsStorage.getDisplay3DView().getDisplay3DPanel().addChild(dataProvider.getCustomSlice3DPlane());
        } else if(viewsStorage.getParams().getMode() == GlobalParams.MODE_CUSTOMORTHOSLICES) {
            if(viewsStorage.getParams().isPlanes3DVisible())
                viewsStorage.getDisplay3DView().getDisplay3DPanel().addChild(dataProvider.getCustomOrthoSlices3DPlanes());
        } else if(viewsStorage.getParams().getMode() == GlobalParams.MODE_ORTHOSLICES) {
            
        }
    }



    
    /**
     * @return the viewsStorage
     */
    public ViewsStorage getViewsStorage() {
        return viewsStorage;
    }

    /**
     * @param viewsStorage the viewsStorage to set
     */
    public void setViewsStorage(ViewsStorage viewsStorage) {
        this.viewsStorage = viewsStorage;
    }

    /**
     * @return the dataProvider
     */
    public DataProvider getDataProvider() {
        return dataProvider;
    }

    /**
     * @param dataProvider the dataProvider to set
     */
    public void setDataProvider(DataProvider dataProvider) {
        if(this.dataProvider != null) {
            this.dataProvider.removeDataProviderListener(this);
        }
        this.dataProvider = dataProvider;
        this.dataProvider.addDataProviderListener(this);
        this.dataProviderParams = dataProvider.getParams();
    }

    @Override
    public void onDataProviderOrthosliceUpdated(int axis) {
        switch(axis) {
            case -1:
                //updateAllViews();
                updateAllViewsInUse();
                viewsStorage.getSliceXView().setHorizontalSlicePosition(dataProviderParams.getOrthosliceNumber(2));
                viewsStorage.getSliceXView().setVerticalSlicePosition(dataProviderParams.getOrthosliceNumber(1));
                viewsStorage.getSliceXTransView().setHorizontalSlicePosition(dataProviderParams.getOrthosliceNumber(1));
                viewsStorage.getSliceXTransView().setVerticalSlicePosition(dataProviderParams.getOrthosliceNumber(2));
                viewsStorage.getSliceYView().setHorizontalSlicePosition(dataProviderParams.getOrthosliceNumber(2));
                viewsStorage.getSliceYView().setVerticalSlicePosition(dataProviderParams.getOrthosliceNumber(0));
                viewsStorage.getSliceYTransView().setHorizontalSlicePosition(dataProviderParams.getOrthosliceNumber(0));
                viewsStorage.getSliceYTransView().setVerticalSlicePosition(dataProviderParams.getOrthosliceNumber(2));
                viewsStorage.getSliceZView().setHorizontalSlicePosition(dataProviderParams.getOrthosliceNumber(1));
                viewsStorage.getSliceZView().setVerticalSlicePosition(dataProviderParams.getOrthosliceNumber(0));
                viewsStorage.getSliceZTransView().setHorizontalSlicePosition(dataProviderParams.getOrthosliceNumber(0));
                viewsStorage.getSliceZTransView().setVerticalSlicePosition(dataProviderParams.getOrthosliceNumber(1));
                break;
            case 0:
                if(viewsStorage.getSliceXView().isInUse())
                    updateViewSliceX();

                if(viewsStorage.getSliceXTransView().isInUse())
                    updateViewSliceXTrans();
                
                viewsStorage.getSliceYView().setVerticalSlicePosition(dataProviderParams.getOrthosliceNumber(0));
                viewsStorage.getSliceZView().setVerticalSlicePosition(dataProviderParams.getOrthosliceNumber(0));
                viewsStorage.getSliceYTransView().setHorizontalSlicePosition(dataProviderParams.getOrthosliceNumber(0));
                viewsStorage.getSliceZTransView().setHorizontalSlicePosition(dataProviderParams.getOrthosliceNumber(0));
                break;
            case 1:
                if(viewsStorage.getSliceYView().isInUse())
                    updateViewSliceY();

                if(viewsStorage.getSliceYTransView().isInUse())
                    updateViewSliceYTrans();

                viewsStorage.getSliceXView().setVerticalSlicePosition(dataProviderParams.getOrthosliceNumber(1));
                viewsStorage.getSliceZView().setHorizontalSlicePosition(dataProviderParams.getOrthosliceNumber(1));
                viewsStorage.getSliceXTransView().setHorizontalSlicePosition(dataProviderParams.getOrthosliceNumber(1));
                viewsStorage.getSliceZTransView().setVerticalSlicePosition(dataProviderParams.getOrthosliceNumber(1));
                break;
            case 2:
                if(viewsStorage.getSliceZView().isInUse())
                    updateViewSliceZ();

                if(viewsStorage.getSliceZTransView().isInUse())
                    updateViewSliceZTrans();

                viewsStorage.getSliceXView().setHorizontalSlicePosition(dataProviderParams.getOrthosliceNumber(2));
                viewsStorage.getSliceYView().setHorizontalSlicePosition(dataProviderParams.getOrthosliceNumber(2));
                viewsStorage.getSliceXTransView().setVerticalSlicePosition(dataProviderParams.getOrthosliceNumber(2));
                viewsStorage.getSliceYTransView().setVerticalSlicePosition(dataProviderParams.getOrthosliceNumber(2));
                break;
        }
    }

    @Override
    public void onDataProviderOverlayUpdated(int axis) {
        switch(axis) {
            case -1:
                //updateAllViews();
                updateAllViewsInUse();
                return;
            case 0:
                if(viewsStorage.getSliceXView().isInUse())
                    updateViewSliceX();
                if(viewsStorage.getSliceXTransView().isInUse())
                    updateViewSliceXTrans();
                break;
            case 1:
                if(viewsStorage.getSliceYView().isInUse())
                    updateViewSliceY();
                if(viewsStorage.getSliceYTransView().isInUse())
                    updateViewSliceYTrans();
                break;
            case 2:
                if(viewsStorage.getSliceZView().isInUse())
                    updateViewSliceZ();
                if(viewsStorage.getSliceZTransView().isInUse())
                    updateViewSliceZTrans();
                break;
        }
        for (int i = 0; i < 3; i++) {
            if(viewsStorage.getCustomOrthosliceView(i).isInUse())
                updateViewCustomOrthoSlice(i);
        }

        if(viewsStorage.getBasicView().isInUse()) {
            updateViewCustomSlice();
        }
        
    }


    @Override
    public void onDataProviderCustomSliceUpdated() {
        updateViewCustomSlice();
        ViewPanel[] views = viewsStorage.getAllViews();
        for (int i = 0; i < views.length; i++) {
            if(views[i] instanceof OrthosliceViewPanel)
                ((OrthosliceViewPanel)views[i]).setCustomPlaneParams(dataProviderParams.getCustomPlanePoint(), dataProviderParams.getCustomPlaneVector());
            if(views[i] instanceof CustomSliceViewPanel)
                ((CustomSliceViewPanel)views[i]).setCustomPlaneParams(
                        dataProviderParams.getCustomPlanePoint(),
                        dataProviderParams.getCustomPlaneVector(),
                        dataProviderParams.getCustomPlaneExtents(),
                        dataProviderParams.getCustomPlaneBase()
                        );

        }
    }

    @Override
    public void onDataProviderSingleDataUpdated() {
        updateViewSingle();
    }


    @Override
    public void onScalingModeChanged() {
        frame.setScalingModeUI(viewsStorage.getParams().getScalingMode());
        updateViewsScaling();
    }

    @Override
    public void onGeometryToolChanged() {
        frame.setSelectedGeometryTool(viewsStorage.getParams().getSelectedGeometryTool());
        updateViewsGeometryTool();
    }

    @Override
    public void onPaintViewInfoChanged() {
        setAllViewsPaintInfo(viewsStorage.getParams().isPaintViewInfo());
        setAllViewsSliceLinesMode(viewsStorage.getParams().getSliceLinesMode());
    }

    @Override
    public void onModeChanged() {
        frame.updateGUI();
        updateMode();
    }

    @Override
    public void onPlanes3DVisibleChanged() {
        update3DViewContents();
    }


    @Override
    public void componentResized(ComponentEvent e) {
        updateViewsScaling();

        //TODO co z tym zrobic zeby sie odpalalo tylko raz?
    }

    @Override
    public void componentMoved(ComponentEvent e) {
    }

    @Override
    public void componentShown(ComponentEvent e) {
    }

    @Override
    public void componentHidden(ComponentEvent e) {
    }

    @Override
    public void onViewPanelEvent(ViewPanelEvent e) {

        if(e instanceof OrthosliceNumberChangedViewPanelEvent) {

            int axis = ((OrthosliceNumberChangedViewPanelEvent)e).getAxis();
            int value = ((OrthosliceNumberChangedViewPanelEvent)e).getValue();
            dataProviderParams.setOrthosliceNumber(axis, value);
            geometryParams.setIntersectionPoint(dataProviderParams.getOrthosliceNumbers().clone());

        } else if(e instanceof NewOrthoSlicesLocationCustomPanelEvent) {

            if(dataProvider.getInField() == null)
                return;
            float[] p = ((NewOrthoSlicesLocationCustomPanelEvent)e).getPoint();
            int[] indices = dataProvider.getInField().getIndices(p[0], p[1], p[2]);
            dataProviderParams.setOrthosliceNumbers(indices.clone());
            geometryParams.setIntersectionPoint(dataProviderParams.getOrthosliceNumbers().clone());

        } else if(e instanceof CustomPlaneChangedViewPanelEvent) {

            float[] p = ((CustomPlaneChangedViewPanelEvent)e).getPoint();
            float[] v = ((CustomPlaneChangedViewPanelEvent)e).getVector();
            dataProviderParams.setCustomPlaneParams(p, v);

        } else if(e instanceof PointsConnectionsCalculablesAddedOrthoPanelEvent) {

            PointsConnectionsCalculablesAddedOrthoPanelEvent pac = (PointsConnectionsCalculablesAddedOrthoPanelEvent)e;
            int[] ptsIndices = geometryParams.addPoints(pac.getPoints());
            if(ptsIndices != null) {
                int[][] cns = pac.getConnections();
                if(cns != null)
                    for (int i = 0; i < cns.length; i++) {
                        geometryParams.addConnection(geometryParams.getPointsDescriptor(ptsIndices[cns[i][0]]), geometryParams.getPointsDescriptor(ptsIndices[cns[i][1]]));
                    }
            }
            CalculableParameter cp = pac.getCalculable();
            if(cp != null) {
                cp.setPointDescriptors(geometryParams.getPointsDescriptors(ptsIndices));
                calculableParams.addCalculableParameter(cp);
            }

        } else if(e instanceof GeometryPointSelectedOrthoPanelEvent) {

            int[] sel = new int[1];
            sel[0] = ((GeometryPointSelectedOrthoPanelEvent)e).getPointIndex();
            geometryParams.setSelectionFollowSlices(((GeometryPointSelectedOrthoPanelEvent)e).isFollowSlices());
            if(sel[0] == -1) {
                geometryParams.setSelectedPoints(null);
            } else {
                geometryParams.setSelectedPoints(sel);
            }
            geometryParams.setSelectionFollowSlices(true);

        } else if(e instanceof GeometryPointAddedCustomPanelEvent) {

            geometryParams.addPoint(((GeometryPointAddedCustomPanelEvent)e).getPoint());

        } else if(e instanceof MappingRangeChangedOrthoPanelEvent) {

            float newLow, newUp;
            if(dataProvider.getInField() == null)
                return;
            float d = (dataProvider.getInField().getData(dataProviderParams.getSingleComponent()).getMaxv() - dataProvider.getInField().getData(dataProviderParams.getSingleComponent()).getMinv())/500.0f;
            newLow = dataProviderParams.getDataMappingParams().getColorMap0Params().getDataMin() + d*((MappingRangeChangedOrthoPanelEvent)e).getDCenter() - d*((MappingRangeChangedOrthoPanelEvent)e).getDWidth()/2;
            newUp = dataProviderParams.getDataMappingParams().getColorMap0Params().getDataMax() + d*((MappingRangeChangedOrthoPanelEvent)e).getDCenter() + d*((MappingRangeChangedOrthoPanelEvent)e).getDWidth()/2;

            if(newUp > dataProvider.getInField().getData(dataProviderParams.getSingleComponent()).getMaxv())
                newUp = dataProvider.getInField().getData(dataProviderParams.getSingleComponent()).getMaxv();

            if(newLow < dataProvider.getInField().getData(dataProviderParams.getSingleComponent()).getMinv())
                newLow = dataProvider.getInField().getData(dataProviderParams.getSingleComponent()).getMinv();

            dataProviderParams.getDataMappingParams().getColorMap0Params().setDataMinMax(newLow, newUp);

        } else if(e instanceof ZoomChangedOrthoPanelEvent) {

            switchToManualScaling();

        } else if(e instanceof GeometryPointMovedOrthoPanelEvent) {

            int n = ((GeometryPointMovedOrthoPanelEvent)e).getPointNumber();
            int[] p = ((GeometryPointMovedOrthoPanelEvent)e).getPoint();

            geometryParams.setSelectionFollowSlices(false);
            geometryParams.modifyPoint(n, p);
            geometryParams.setSelectionFollowSlices(true);

        } else if(e instanceof UsedViewPanelEvent) {

            if(!(e.getSource() instanceof ViewPanel))
                return;

            ViewPanel panel = (ViewPanel)e.getSource();
            if(panel instanceof OrthosliceViewPanel) {
                OrthosliceViewPanel op = (OrthosliceViewPanel)panel;
                if(op == viewsStorage.getSliceXView())
                    updateViewSliceX();
                else if(op == viewsStorage.getSliceYView())
                    updateViewSliceY();
                else if(op == viewsStorage.getSliceZView())
                    updateViewSliceZ();
                else if(op == viewsStorage.getSliceXTransView())
                    updateViewSliceXTrans();
                else if(op == viewsStorage.getSliceYTransView())
                    updateViewSliceYTrans();
                else if(op == viewsStorage.getSliceZTransView())
                    updateViewSliceZTrans();
            } else if (panel instanceof CustomSliceViewPanel) {
                updateViewCustomSlice();
            } else if (panel instanceof CustomOrthosliceViewPanel) {
                updateViewCustomOrthoSlice(((CustomOrthosliceViewPanel)panel).getAxis());
            } else if (panel instanceof Display3DViewPanel) {

            }

        } else if(e instanceof CustomOrthoPlaneChangedViewPanelEvent) {

            float[] p = ((CustomOrthoPlaneChangedViewPanelEvent)e).getPoint();
            float[][] vv = ((CustomOrthoPlaneChangedViewPanelEvent)e).getVectors();
            dataProviderParams.setCustomOrthoPlanesParams(p, vv);
            if(geometryParams.getInField() != null) {
                int[] p0 = geometryParams.getInField().getIndices(p[0], p[1], p[2]);
                geometryParams.setIntersectionPoint(p0);                
            }

        } else if(e instanceof GeometryPointAddedCustomOrthoPanelEvent) {

            if(frame.getParams().getMode() == GlobalParams.MODE_CUSTOMORTHOSLICES) {
                geometryParams.addPointWithSlices(((GeometryPointAddedCustomOrthoPanelEvent)e).getPoint(), dataProviderParams.getCustomOrthoPlanesPoint(), dataProviderParams.getCustomOrthoPlanesVectors());
            } else {
                geometryParams.addPointWithSlices(((GeometryPointAddedCustomOrthoPanelEvent)e).getPoint(), null, null);
            }

        } else if(e instanceof GeometryPointSelectedCustomOrthoPanelEvent) {

            int[] sel = new int[1];
            sel[0] = ((GeometryPointSelectedCustomOrthoPanelEvent)e).getPointIndex();
            geometryParams.setSelectionFollowSlices(((GeometryPointSelectedCustomOrthoPanelEvent)e).isFollowSlices());
            if(sel[0] == -1) {
                geometryParams.setSelectedPoints(null);
            } else {
                geometryParams.setSelectedPoints(sel);
            }
            geometryParams.setSelectionFollowSlices(true);


        } else if(e instanceof GeometryPointMovedCustomOrthoPanelEvent) {

            int n = ((GeometryPointMovedCustomOrthoPanelEvent)e).getPointNumber();
            float[] p = ((GeometryPointMovedCustomOrthoPanelEvent)e).getPoint();

            geometryParams.setSelectionFollowSlices(false);
            geometryParams.modifyPoint(n, p);
            geometryParams.setSelectionFollowSlices(true);

        } else if(e instanceof MouseLocationChangedOrthoPanelEvent) {
            float[] p = ((MouseLocationChangedOrthoPanelEvent)e).getLocation();
            if(p == null || dataProvider.getInField() == null) {
                frame.setRightStatusText("");
            } else {
                ViewPanel panel = (ViewPanel)e.getSource();
                if(panel instanceof OrthosliceViewPanel) {
                    int axis = ((OrthosliceViewPanel)panel).getAxis();
                    int horizAxis = ((OrthosliceViewPanel)panel).getHorizAxis();
                    int vertAxis = ((OrthosliceViewPanel)panel).getVertAxis();
                    float[] p3d = new float[3];
                    p3d[axis] = dataProviderParams.getOrthosliceRealPosition(axis)[axis];
                    p3d[horizAxis] = p[0];
                    p3d[vertAxis] = p[1];
                    float minval = 0;
                    float maxval = 255;
                    if (dataProvider.getInField().getNData() == 1 && 
                        dataProvider.getInField().getData(0).getType() == DataArray.FIELD_DATA_BYTE)
                    {
                       minval = dataProvider.getInField().getData(0).getPhysMin();
                       maxval = dataProvider.getInField().getData(0).getPhysMax();
                    }
                    float dv = (maxval - minval) / 255;
                    int[] ind = dataProvider.getInField().getIndices(p3d[0], p3d[1], p3d[2]);
                    if(ind == null) {
                        frame.setRightStatusText("  ");                      
                        return;
                    }
                    int[] rgb = dataProvider.getOrthoSliceRGB(axis, ind[horizAxis], ind[vertAxis]);
                    Object vObj = dataProvider.getFieldValue(ind[0], ind[1], ind[2]);
                    String vStr = null;

                    //parse vObj to vStr
                    if(vObj != null) {
                        if(vObj instanceof Byte) {
                           int val = ((Byte)vObj)&0xFF;
                           float fVal = minval + val * dv;
                           vStr = String.format("%4.1f", fVal);
                        } else if(vObj instanceof byte[]) {
                            byte[] tmp = (byte[])vObj;
                            vStr = "["+(tmp[0]&0xFF);
                            for (int i = 1; i < tmp.length; i++) {
                                vStr += ","+(tmp[i]&0xFF);                                                                
                            }
                            vStr += "]";                            
                        } else if(vObj instanceof Short) {
                            vStr = ""+((Short)vObj);
                        } else if(vObj instanceof short[]) {
                            short[] tmp = (short[])vObj;
                            vStr = "["+tmp[0];
                            for (int i = 1; i < tmp.length; i++) {
                                vStr += ","+tmp[i];                                                                
                            }
                            vStr += "]";                            
                        } else if(vObj instanceof Integer) {
                            vStr = ""+((Integer)vObj);
                        } else if(vObj instanceof int[]) {
                            int[] tmp = (int[])vObj;
                            vStr = "["+tmp[0];
                            for (int i = 1; i < tmp.length; i++) {
                                vStr += ","+tmp[i];                                                                
                            }
                            vStr += "]";                            
                        } else if(vObj instanceof Float || vObj instanceof Double) {
                            vStr = ""+df.format(vObj);
                        } else if(vObj instanceof float[]) {
                            float[] tmp = (float[])vObj;
                            vStr = "["+df.format(tmp[0]);
                            for (int i = 1; i < tmp.length; i++) {
                                vStr += ","+df.format(tmp[i]);                                                                
                            }
                            vStr += "]";                            
                        } else if(vObj instanceof double[]) {
                            double[] tmp = (double[])vObj;
                            vStr = "["+df.format(tmp[0]);
                            for (int i = 1; i < tmp.length; i++) {
                                vStr += ","+df.format(tmp[i]);                                                                
                            }
                            vStr += "]";                            
                        } else if(vObj instanceof String) {
                            vStr = (String)vObj;
                        } else if(vObj instanceof String[]) {
                            String[] tmp = (String[])vObj;
                            vStr = "["+tmp[0];
                            for (int i = 1; i < tmp.length; i++) {
                                vStr += ","+tmp[i];                                                                
                            }
                            vStr += "]";                            
                        } else if(vObj instanceof Boolean) {
                            vStr = ((Boolean)vObj)?"true":"false";
                        } else if(vObj instanceof boolean[]) {
                            boolean[] tmp = (boolean[])vObj;
                            vStr = "["+(tmp[0]?"true":"false");
                            for (int i = 1; i < tmp.length; i++) {
                                vStr += ","+(tmp[i]?"true":"false");                                                                
                            }
                            vStr += "]";                            
                        } else if(vObj instanceof float[][]) {
                            float[][] tmp = (float[][])vObj;
                            int vlen = tmp.length;
                            if(vlen == 1) {
                                vStr = ""+df.format(tmp[0][0])+" + i*"+df.format(tmp[0][1]);
                            } else {
                                vStr = "["+df.format(tmp[0][0])+" + i*"+df.format(tmp[0][1]);
                                for (int i = 1; i < tmp.length; i++) {
                                    vStr += ","+df.format(tmp[i][0])+" + i*"+df.format(tmp[i][1]);
                                }
                                vStr += "]";                            
                            }
                        } 
                        
                    }
                    
                    if(vStr == null) {
                        frame.setRightStatusText("  ["+df.format(p3d[0])+","+df.format(p3d[1])+","+df.format(p3d[2])+"], ["+ind[0]+","+ind[1]+","+ind[2]+"]  ");
                    } else if(rgb == null) {frame.setRightStatusText("  ["+df.format(p3d[0])+","+df.format(p3d[1])+","+df.format(p3d[2])+"], ["+ind[0]+","+ind[1]+","+ind[2]+"], data="+vStr+"  ");
                    } else {
                        String rgbString = "";
                        if(rgb.length >= 3) {
                            if(rgb[0] == rgb[1] && rgb[1] == rgb[2]) {
                                rgbString = "V="+rgb[0];
                            } else {
                                rgbString = "R:G:B="+rgb[0]+":"+rgb[1]+":"+rgb[2];
                            }
                        } else {
                            rgbString = "V="+rgb[0];
                        }
                        frame.setRightStatusText("  ["+df.format(p3d[0])+","+df.format(p3d[1])+","+df.format(p3d[2])+"], ["+ind[0]+","+ind[1]+","+ind[2]+"], "+rgbString+", data="+vStr+"  ");
                    }
                } else {
                    frame.setRightStatusText("");
                }
            }

        }


    }

    private DecimalFormat df = new DecimalFormat("###.###");

    /**
     * @param geometryParams the geometryParams to set
     */
    public void setGeometryParams(GeometryParams geometryParams) {
        if(this.geometryParams != null)
            this.geometryParams.removeGeometryParamsListener(this);

        this.geometryParams = geometryParams;
        this.geometryParams.addGeometryParamsListener(this);
        if(calculableParams != null)
            this.geometryParams.addGeometryParamsListener(calculableParams);

        onGeometryParamsChanged(new GeometryParamsEvent(frame, GeometryParamsEvent.TYPE_ALL));
    }

    void setCalculableParams(CalculableParams cparams) {
        this.calculableParams = cparams;

        this.calculableParams.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                ViewPanel[] views = viewsStorage.getAllViews();
                ArrayList<CalculableParameter> cps = calculableParams.getCalculableParameters();
                for (int j = 0; j < views.length; j++) {
                    if(views[j] instanceof OrthosliceViewPanel) {
                        ((OrthosliceViewPanel)views[j]).setCalculableParameters(cps);
                        ((OrthosliceViewPanel)views[j]).setPaintCalculableValues(calculableParams.isPaintCalculable2D());
                    } else if(views[j] instanceof CustomSliceViewPanel) {
                        //((CustomSliceViewPanel)views[j]).setCalculableParameters(cps);
                        //((CustomSliceViewPanel)views[j]).setPaintCalculableValues(calculableParams.isPaintCalculable2D());
                    } else if(views[j] instanceof CustomOrthosliceViewPanel) {
                        //((CustomOrthosliceViewPanel)views[j]).setCalculableParameters(cps);
                        //((CustomOrthosliceViewPanel)views[j]).setPaintCalculableValues(calculableParams.isPaintCalculable2D());
                    } else if(views[j] instanceof BasicViewPanel) {
                        ((BasicViewPanel)views[j]).setCalculableParameters(cps);
                        ((BasicViewPanel)views[j]).setPaintCalculableValues(calculableParams.isPaintCalculable2D());
                    }
                }


            }
        });
        if(geometryParams != null)
            geometryParams.addGeometryParamsListener(this.calculableParams);
    }

    @Override
    public void onGeometryParamsChanged(GeometryParamsEvent e) {
        ArrayList<PointDescriptor> pts = geometryParams.getPointsDescriptors();
        ViewPanel[] views = viewsStorage.getAllViews();
        int[] selectedPoints = geometryParams.getSelectedPoints();

        if(e.getType() == GeometryParamsEvent.TYPE_POINT_ADDED || e.getType() == GeometryParamsEvent.TYPE_POINT_MODIFIED || e.getType() == GeometryParamsEvent.TYPE_POINT_REMOVED || e.getType() == GeometryParamsEvent.TYPE_ALL) {
            for (int j = 0; j < views.length; j++) {
                if(views[j] instanceof OrthosliceViewPanel) {
                    ((OrthosliceViewPanel)views[j]).setPoints(pts);
                } else if(views[j] instanceof CustomSliceViewPanel) {
                    ((CustomSliceViewPanel)views[j]).setPoints(pts);
                } else if(views[j] instanceof CustomOrthosliceViewPanel) {
                    ((CustomOrthosliceViewPanel)views[j]).setPoints(pts);
                } else if(views[j] instanceof BasicViewPanel) {
                    ((BasicViewPanel)views[j]).setPoints(pts);
                }
            }
        }

        if(e.getType() == GeometryParamsEvent.TYPE_POINT_SELECTION || e.getType() == GeometryParamsEvent.TYPE_ALL) {
            for (int j = 0; j < views.length; j++) {
                if(views[j] instanceof OrthosliceViewPanel) {
                    ((OrthosliceViewPanel)views[j]).setSelectedPoints(selectedPoints);
                } else if(views[j] instanceof CustomSliceViewPanel) {
                    ((CustomSliceViewPanel)views[j]).setSelectedPoints(selectedPoints);
                } else if(views[j] instanceof CustomOrthosliceViewPanel) {
                    ((CustomOrthosliceViewPanel)views[j]).setSelectedPoints(selectedPoints);
                } else if(views[j] instanceof BasicViewPanel) {
                    ((BasicViewPanel)views[j]).setSelectedPoints(selectedPoints);
                }
            }

//            if(selectedPoints != null && selectedPoints.length == 1) {
//                viewsStorage.getParams().setSelectedGeometryTool(GeometryToolsStorage.GEOMETRY_TOOL_POINT);
//            }

            if(selectedPoints != null && selectedPoints.length >= 1 && geometryParams.isSelectionFollowSlices()) {
                int[] p = geometryParams.getPoints().get(selectedPoints[0]);
                if(frame.getParams().getMode() == GlobalParams.MODE_ORTHOSLICES) {
                    dataProviderParams.setOrthosliceNumbers(p.clone());
                } else if(frame.getParams().getMode() == GlobalParams.MODE_CUSTOMORTHOSLICES) {
                    if(geometryParams.getInField() != null) {
                        if(geometryParams.isSlicePositioning()) {
                            CustomSlicesDescriptor sd = geometryParams.getSlicesDescriptor(selectedPoints[0]);
                            if(sd != null) {
                                float[] pp = sd.getOriginPoint();
                                float[][] vv = sd.getVectors();
                                if(pp != null && vv != null) {
                                    dataProviderParams.setCustomOrthoPlanesParams(pp, vv);
                                }
                            }
                        } else {
                            float[] p0 = geometryParams.getInField().getGridCoords((float)p[0], (float)p[1], (float)p[2]);
                            dataProviderParams.setCustomOrthoPlanesPoint(p0);
                        }
                    }
                }

            }
        }

        if(e.getType() == GeometryParamsEvent.TYPE_LABELS || e.getType() == GeometryParamsEvent.TYPE_ALL) {
            for (int j = 0; j < views.length; j++) {
                if(views[j] instanceof OrthosliceViewPanel) {
                    ((OrthosliceViewPanel)views[j]).setPaintPointLabels(geometryParams.isPaintLabels());
                } else if(views[j] instanceof CustomSliceViewPanel) {
                    ((CustomSliceViewPanel)views[j]).setPaintPointLabels(geometryParams.isPaintLabels());
                } else if(views[j] instanceof CustomOrthosliceViewPanel) {
                    ((CustomOrthosliceViewPanel)views[j]).setPaintPointLabels(geometryParams.isPaintLabels());
                } else if(views[j] instanceof BasicViewPanel) {
                    ((BasicViewPanel)views[j]).setPaintPointLabels(geometryParams.isPaintLabels());
                }
            }
        }

        if(e.getType() == GeometryParamsEvent.TYPE_GLYPHS || e.getType() == GeometryParamsEvent.TYPE_ALL) {

        }

        if(e.getType() == GeometryParamsEvent.TYPE_CONN2D || e.getType() == GeometryParamsEvent.TYPE_ALL) {
            for (int j = 0; j < views.length; j++) {
                if(views[j] instanceof OrthosliceViewPanel) {
                    ((OrthosliceViewPanel)views[j]).setPaintConnections(geometryParams.isShowConnections2D());
                    ((OrthosliceViewPanel)views[j]).setPaintDistances(geometryParams.isShowDistances2D());
                } else if(views[j] instanceof CustomSliceViewPanel) {
                    ((CustomSliceViewPanel)views[j]).setPaintConnections(geometryParams.isShowConnections2D());
                    ((CustomSliceViewPanel)views[j]).setPaintDistances(geometryParams.isShowDistances2D());
                } else if(views[j] instanceof CustomOrthosliceViewPanel) {
                    ((CustomOrthosliceViewPanel)views[j]).setPaintConnections(geometryParams.isShowConnections2D());
                    ((CustomOrthosliceViewPanel)views[j]).setPaintDistances(geometryParams.isShowDistances2D());
                } else if(views[j] instanceof BasicViewPanel) {
                    ((BasicViewPanel)views[j]).setPaintConnections(geometryParams.isShowConnections2D());
                    ((BasicViewPanel)views[j]).setPaintDistances(geometryParams.isShowDistances2D());
                }
            }
        }

        if(e.getType() == GeometryParamsEvent.TYPE_CONN3D || e.getType() == GeometryParamsEvent.TYPE_ALL) {

        }

        if(e.getType() == GeometryParamsEvent.TYPE_CONNECTION || e.getType() == GeometryParamsEvent.TYPE_ALL) {
            for (int j = 0; j < views.length; j++) {
                if(views[j] instanceof OrthosliceViewPanel) {
                    ((OrthosliceViewPanel)views[j]).setConnections(geometryParams.getConnectionDescriptors());
                } else if(views[j] instanceof CustomSliceViewPanel) {
                    ((CustomSliceViewPanel)views[j]).setConnections(geometryParams.getConnectionDescriptors());
                } else if(views[j] instanceof CustomOrthosliceViewPanel) {
                    ((CustomOrthosliceViewPanel)views[j]).setConnections(geometryParams.getConnectionDescriptors());
                } else if(views[j] instanceof BasicViewPanel) {
                    ((BasicViewPanel)views[j]).setConnections(geometryParams.getConnectionDescriptors());
                }
            }
        }
        
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if(e.getSource() instanceof GeometryUI) {
            float[] p = new float[3];
            float[] v = new float[3];

            int[] pn = geometryParams.getSelectedPoints();
            if(pn == null || pn.length < 1)
                return;
            
            ArrayList<PointDescriptor> points = new ArrayList<PointDescriptor>();
            ArrayList<PointDescriptor> allPoints = geometryParams.getPointsDescriptors();
            for (int i = 0; i < pn.length; i++) {
                points.add(allPoints.get(pn[i]));
            }
            allPoints = null;

            //Vector<PointDescriptor> points = geometryParams.getPointsDescriptors();
            if(points.size() != 3)
                return;

            float[] p0 = points.get(0).getWorldCoords();
            float[] p1 = points.get(1).getWorldCoords();
            float[] p2 = points.get(2).getWorldCoords();
            p[0] = p0[0];
            p[1] = p0[1];
            p[2] = p0[2];

            float[] v1 = new float[3];
            float[] v2 = new float[3];
            for (int i = 0; i < 3; i++) {
                v1[i] = p1[i] - p0[i];
                v2[i] = p2[i] - p0[i];
            }

            v[0] = v1[1]*v2[2] - v1[2]*v2[1];
            v[1] = v1[2]*v2[0] - v1[0]*v2[2];
            v[2] = v1[0]*v2[1] - v1[1]*v2[0];
            float len = 0;
            for (int i = 0; i < 3; i++) {
                len += v[i]*v[i];
            }
            len = (float)Math.sqrt(len);
            for (int i = 0; i < 3; i++) {
                v[i] = v[i]/len;
            }

            dataProviderParams.setCustomPlaneParams(p, v);
        }
    }

    @Override
    public void onDataProviderCustomOrthoSliceUpdated(int axis) {
        if(axis == -1) {
            updateViewCustomOrthoSlice(0);
            updateViewCustomOrthoSlice(1);
            updateViewCustomOrthoSlice(2);
        } else {
            updateViewCustomOrthoSlice(axis);
        }

        ViewPanel[] views = viewsStorage.getAllViews();
        for (int i = 0; i < views.length; i++) {
            if(views[i] instanceof CustomOrthosliceViewPanel) {
                int a = ((CustomOrthosliceViewPanel)views[i]).getAxis();
                ((CustomOrthosliceViewPanel)views[i]).setCustomOrthoPlanesParams(
                        dataProviderParams.getCustomOrthoPlanesPoint(),
                        dataProviderParams.getCustomOrthoPlanesVectors(),
                        dataProviderParams.getCustomOrthoPlanesExtents(a),
                        dataProviderParams.getCustomOrthoPlanesBase(a)
                        );
            }

        }
    }

    private void updateViewCustomOrthoSlice(int axis) {
        if(dataProvider == null || dataProviderParams == null)
            return;

        viewsStorage.getCustomOrthosliceView(axis).setDataImage(dataProvider.getCustomOrthoSliceImage(axis), dataProviderParams.getCustomOrthoPlanesUPPW(axis), dataProviderParams.getCustomOrthoPlanesUPPH(axis), dataProviderParams.getUpp());

        if(dataProvider.getAuxField() != null) {
            //viewsStorage.getCustomOrthoSliceView(axis).setIsoline(dataProvider.getCustomOrthoIsoline(axis));
        }

        if(dataProvider.getOverlayField() != null || dataProviderParams.isSimpleOverlay()) {
            viewsStorage.getCustomOrthosliceView(axis).setOverlayImage(dataProvider.getCustomOrthoSliceOverlay(axis), dataProviderParams.getOverlayOpacity());
        } else {
            viewsStorage.getCustomOrthosliceView(axis).setOverlayImage(null, 1.0f);
        }

    }

    private void updateViewsGeometryTool() {
        ViewPanel[] views = viewsStorage.getAllViews();
        for (int i = 0; i < views.length; i++) {
            views[i].setGeometryTool(GeometryToolsStorage.getGeometryTool(viewsStorage.getParams().getSelectedGeometryTool()));
        }
    }




}
