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
package pl.edu.icm.visnow.lib.basic.readers.ReadVTK;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pl.edu.icm.visnow.datasets.IrregularField;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.lib.templates.visualization.modules.OutFieldVisualizationModule;
import pl.edu.icm.visnow.lib.types.VNField;
import pl.edu.icm.visnow.lib.types.VNIrregularField;
import pl.edu.icm.visnow.lib.types.VNRegularField;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;
import pl.edu.icm.visnow.lib.utils.vtk.VTKCore;

/**
 * @author creed Interdisciplinary Centre for Mathematical and Computational
 * Modelling
 */
public class ReadVTK extends OutFieldVisualizationModule {

    private GUI computeUI = null;
    private VTKCore core;

    public ReadVTK() {
        core = VTKCore.loadVTKLibrary();
        SwingInstancer.swingRunAndWait(new Runnable() {
            @Override
            public void run() {
                computeUI = new GUI();
            }
        });
        computeUI.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                startAction();
            }
        });
        ui.addComputeGUI(computeUI);
        setPanel(ui);
    }
    public static OutputEgg[] outputEggs = null;

    @Override
    public void onActive() {
        outField = core.readVTK(computeUI.getPath(), computeUI.getOrder());
        if (outField != null) {
            computeUI.setFieldDescription(outField.description());      
        }
        
        if (outField != null && outField instanceof RegularField) {
           outRegularField = (RegularField)outField;
           outIrregularField = null;
           setOutputValue("outRegularField", new VNRegularField(outRegularField));
           setOutputValue("outIrregularField", null);
        } else if(outField != null && outField instanceof IrregularField) {
           outRegularField = null;
           outIrregularField = (IrregularField)outField;
           setOutputValue("outRegularField", null);
           setOutputValue("outIrregularField", new VNIrregularField(outIrregularField));
        } else {
           outRegularField = null;
           outIrregularField = null;
           setOutputValue("outRegularField", null);
           setOutputValue("outIrregularField", null);
      }

      prepareOutputGeometry();
      show();
    }
}
