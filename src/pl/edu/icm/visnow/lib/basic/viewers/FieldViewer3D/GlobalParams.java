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

import java.util.Vector;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.ViewPanels.OrthosliceViewPanel;

/**
 *
 * @author vis
 */
public class GlobalParams {
    //private boolean keepGlobalScale = true;
    private int scalingMode = OrthosliceViewPanel.SCALING_EXTERNAL;
    
    private int sliceLinesMode = OrthosliceViewPanel.SLICE_LINES_COLORED;

    private boolean paintViewInfo = false;
    private boolean silent = false;

    public static final int MODE_ORTHOSLICES = 0;
    public static final int MODE_CUSTOMSLICE = 1;
    public static final int MODE_CUSTOMORTHOSLICES = 2;

    private int mode = MODE_ORTHOSLICES;

    private boolean planes3DVisible = true;

    private int selectedGeometryTool = GeometryToolsStorage.GEOMETRY_TOOL_POINT;
    private boolean toolSelectionBlocking = false;

    public GlobalParams() {

    }

    public int getScalingMode() {
        return scalingMode;
    }

    public void setScalingMode(int scalingMode) {
        this.scalingMode = scalingMode;
        fireScalingModeChanged();
    }


   private Vector<GlobalParamsListener> listeners = new Vector<GlobalParamsListener>();

   public synchronized void addGlobalParamsListener(GlobalParamsListener listener) {
      listeners.add(listener);
   }

   public synchronized void removeGlobalParamsListener(GlobalParamsListener listener) {
      listeners.remove(listener);
   }

//   private void fireKeepGlobalScaleChanged() {
//       if(silent)
//           return;
//      for (GlobalParamsListener listener : listeners) {
//         listener.onKeepGlobalScaleChanged();
//      }
//   }

   private void fireScalingModeChanged() {
       if(silent)
           return;
      for (GlobalParamsListener listener : listeners) {
         listener.onScalingModeChanged();
      }
   }


   private void firePaintViewInfoChanged() {
       if(silent)
           return;
      for (GlobalParamsListener listener : listeners) {
         listener.onPaintViewInfoChanged();
      }
   }

   private void fireModeChanged() {
       if(silent)
           return;
      for (GlobalParamsListener listener : listeners) {
         listener.onModeChanged();
      }
   }

    private void firePlanes3DVisibleChanged() {
       if(silent)
           return;
      for (GlobalParamsListener listener : listeners) {
         listener.onPlanes3DVisibleChanged();
      }
    }

   private void fireGeometryToolChanged() {
       if(silent)
           return;
      for (GlobalParamsListener listener : listeners) {
         listener.onGeometryToolChanged();
      }
   }

    /**
     * @return the silent
     */
    public boolean isSilent() {
        return silent;
    }

    /**
     * @param silent the silent to set
     */
    public void setSilent(boolean silent) {
        this.silent = silent;
    }

    /**
     * @return the paintViewInfo
     */
    public boolean isPaintViewInfo() {
        return paintViewInfo;
    }

    /**
     * @param paintViewInfo the paintSliceInfo to set
     */
    public void setPaintViewInfo(boolean paintViewInfo) {
        this.paintViewInfo = paintViewInfo;
        firePaintViewInfoChanged();
    }

    /**
     * @return the mode
     */
    public int getMode() {
        return mode;
    }

    /**
     * @param mode the mode to set
     */
    public void setMode(int mode) {
        this.mode = mode;
        fireModeChanged();
    }

    /**
     * @return the planes3DVisible
     */
    public boolean isPlanes3DVisible() {
        return planes3DVisible;
    }

    /**
     * @param planes3DVisible the planes3DVisible to set
     */
    public void setPlanes3DVisible(boolean planes3DVisible) {
        this.planes3DVisible = planes3DVisible;
        firePlanes3DVisibleChanged();
    }

    /**
     * @return the selectedGeometryTool
     */
    public int getSelectedGeometryTool() {
        return selectedGeometryTool;
    }

    public boolean isToolSelectionBlocking() {
        return toolSelectionBlocking;
    }
    
    /**
     * @param selectedGeometryTool the selectedGeometryTool to set
     */
    public void setSelectedGeometryTool(int selectedGeometryTool, boolean blocking) {
        this.selectedGeometryTool = selectedGeometryTool;
        this.toolSelectionBlocking = blocking;
        fireGeometryToolChanged();
    }

    public void setSelectedGeometryTool(int selectedGeometryTool) {
        setSelectedGeometryTool(selectedGeometryTool, false);
    }

    public int getSliceLinesMode() {
        return sliceLinesMode;
    }

    public void setSliceLinesMode(int sliceLinesMode) {
        this.sliceLinesMode = sliceLinesMode;
        firePaintViewInfoChanged();
    }



}
