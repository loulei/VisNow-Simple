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
package pl.edu.icm.visnow.lib.basic.mappers.RegularFieldSlice;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.log4j.Logger;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.engine.core.InputEgg;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.geometries.objects.RegularField2DGeometry;
import pl.edu.icm.visnow.lib.templates.visualization.modules.RegularOutFieldVisualizationModule;
import pl.edu.icm.visnow.lib.types.VNRegularField;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;
import pl.edu.icm.visnow.lib.utils.field.SliceRegularField;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class RegularFieldSlice extends RegularOutFieldVisualizationModule {
    private static final Logger LOGGER = Logger.getLogger(RegularFieldSlice.class);

    protected RegularField inField = null;
    protected RegularFieldSliceGUI computeUI = null;
    protected boolean fromUI = false;
    protected boolean ignoreUI = false;
    protected RegularFieldSliceParams params;
    protected int lastAxis = -1;

    public RegularFieldSlice() {
        parameters = params = new RegularFieldSliceParams();
        params.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent evt) {
                fromUI = true;
                if (ignoreUI) {
                    ignoreUI = false;
                    return;
                }
                LOGGER.trace("RegularFieldGeometry null: ["+(regularFieldGeometry==null)+"]");
                LOGGER.trace("outField: "+ outField);                
                if (params.isAdjusting() && regularFieldGeometry != null &&
                        regularFieldGeometry instanceof RegularField2DGeometry &&
                        params.getAxis() == lastAxis) {
                    //hide geometry if slice is incorrect (out of range)
                    if (!SliceRegularField.sliceUpdate(inField, params.getAxis(), params.getSlice(), outField)) {
                        LOGGER.trace("clearing geometry");                            
                        outObj.clearAllGeometry();
                    } else {
                        //show geometry if slice appears
                        if (outObj.isEmpty()) {
                            LOGGER.trace("resetting geometry");                            
                            outObj.clearAllGeometry();
                            outGroup = regularFieldGeometry.getGeometry();
                            outObj.addNode(outGroup);
                        }
                        updateMinMax();
                        ((RegularField2DGeometry) regularFieldGeometry).updateData();
                    }
                } else
                    startAction();
            }
        });
        SwingInstancer.swingRun(new Runnable() {
            public void run() {
                computeUI = new RegularFieldSliceGUI();
            }
        });
        computeUI.setParams(params);
        ui.addComputeGUI(computeUI);
        setPanel(ui);
    }
    public static InputEgg[] inputEggs = null;
    public static OutputEgg[] outputEggs = null;

    private void update() {
        if (!SliceRegularField.sliceUpdate(inField, params.getAxis(), params.getSlice(), outField)) {
            //reset out field if slice is incorrect (out of range)
            setOutputValue("outField", new VNRegularField());
            outField = null;
        } else {
            setOutputValue("outField", new VNRegularField(outField));
            updateMinMax();
        }
    }

    @Override
    public void onActive() {
        if (getInputFirstValue("inField") == null)
            return;
        RegularField field = ((VNRegularField) getInputFirstValue("inField")).getField();
        if (field == null || field.getDims() == null || field.getDims().length != 3)
            return;
        if (inField == null || !inField.isStructureCompatibleWith(field) ||
                !inField.isDataCompatibleWith(field) || params.getAxis() != lastAxis) {
            inField = field;
            ignoreUI = true;
            SwingInstancer.swingRun(new Runnable() {
                public void run() {
                    computeUI.setDims(inField.getDims());
                }
            });

            ignoreUI = false;
            lastAxis = params.getAxis();
            outField = SliceRegularField.sliceField(inField, params.getAxis(), params.getSlice());
            updateMinMax();
            if (!prepareOutputGeometry())
                return;
            show();
            return;
        }
        if (outField == null) {
            outField = SliceRegularField.sliceField(inField, params.getAxis(), params.getSlice());
            if (!prepareOutputGeometry())
                return;            
        }
        
        if (getInputFirstValue("slice") != null) {
            int slice = (Integer) getInputFirstValue("slice");
            if (slice != params.getSlice()) {
                ignoreUI = true;
                params.setSlice(slice);
            }
        }
        inField = field;
        update();
        show();
        ignoreUI = false;
    }

    @Override
    protected void updateImSelected() {
        //System.out.println("huhu "+iAmSelected);
        params.setAdjusting(iAmSelected);

    }

    @Override
    public void processMouseDragged(double dx, double dy) {
        //double d = Math.sqrt(dx*dx + dy*dy);
        //System.out.println("drag "+d);
        double d = 0;
        if (Math.abs(dx) > Math.abs(dy))
            d = dx;
        else
            d = dy;

        int slice = params.getSlice();
        if (d > 0)
            params.setSlice(slice + 1);
        else
            params.setSlice(slice - 1);
    }

    private void updateMinMax() {
        if (params.isRecalculateMinMax() && outField != null) {
            for (int i = 0; i < outField.getNData(); i++) {
                outField.getData(i).recomputeMinMax();
            }
        }
        ui.getPresentationPanel().getDataMappingGUI().getColorMappedComponentPanel().getMap0Panel().updateComponent();
    }
}
