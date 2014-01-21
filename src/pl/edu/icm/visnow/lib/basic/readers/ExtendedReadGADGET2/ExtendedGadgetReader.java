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

package pl.edu.icm.visnow.lib.basic.readers.ExtendedReadGADGET2;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.engine.core.InputEgg;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.engine.core.ParameterChangeListener;
import pl.edu.icm.visnow.lib.templates.visualization.modules.IrregularOutFieldVisualizationModule;
import pl.edu.icm.visnow.lib.types.VNIrregularField;
import pl.edu.icm.visnow.lib.types.VNRegularField;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;
import pl.edu.icm.visnow.system.main.VisNow;

/**
 * @author  Krzysztof S. Nowinski (know@icm.edu.pl)
 * Warsaw University, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class ExtendedGadgetReader extends IrregularOutFieldVisualizationModule {

    protected GUI computeUI = null;
    protected Params params;
    protected boolean fromGUI = false;
    protected RegularField outDensityField = null;
    private ExtendedReadGadgetData core;

    /**
     * Creates a new instance of CreateGrid
     */
    public ExtendedGadgetReader() {
        parameters = params = new Params();
        params.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent evt) {
                startAction();
            }
        });
        params.addParameterChangelistener(new ParameterChangeListener() {

            @Override
            public void parameterChanged(String name) {
                if("show".equals(name)) {
                    fromGUI = true;
                    startAction();
                }
            }
        });
        SwingInstancer.swingRunAndWait(new Runnable() {

            @Override
            public void run() {
                computeUI = new GUI();
                computeUI.setParams(params);
                ui.addComputeGUI(computeUI);
                setPanel(ui);
            }
        });

        core = new ExtendedReadGadgetData();
        core.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                setProgress(core.getProgress());
            }
        });
    }

    public static OutputEgg[] outputEggs = null;
    public static InputEgg[] inputEggs = null;

    @Override
    public void onActive() {
        if(!fromGUI) {
            if (params.getFilePaths() == null) {
                return;
            }

            Field inRangeField = null;
            if (getInputFirstValue("inRangeField") != null)
                inRangeField = ((VNRegularField) getInputFirstValue("inRangeField")).getField();
            float[][] cropExtents = null;            
            if(inRangeField != null) {
                cropExtents = inRangeField.getExtents();
            }
            
            core.read(
                    params.getFilePaths(), 
                    params.getReadMask(), 
                    params.getDownsize(), 
                    params.getDensityFieldDims(),
                    params.isDensityFieldLog(),
                    cropExtents, 
                    VisNow.get().getMemoryAvailable()/3
                    );
            
            outField = core.getField();
            if (outField == null) {
                return;
            }

            if(inRangeField == null)
                outDensityField = core.getDensityField();
            
            
            computeUI.setFieldDescription(outField.description());
            setOutputValue("outField", new VNIrregularField(outField));
            setOutputValue("outDensityField", new VNRegularField(outDensityField));
        }

        if (params.isShow()) {
            prepareOutputGeometry();
            show();
        }
        fromGUI = false;
    }
    
   @Override
   public void onInitFinishedLocal() {
       if(isForceFlag()) 
           computeUI.activateOpenDialog();
   }
 
   @Override
   public boolean isGenerator() {
      return true;
   }
   
}
