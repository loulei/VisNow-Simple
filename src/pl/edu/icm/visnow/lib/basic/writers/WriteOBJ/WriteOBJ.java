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

package pl.edu.icm.visnow.lib.basic.writers.WriteOBJ;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pl.edu.icm.visnow.engine.core.InputEgg;
import pl.edu.icm.visnow.engine.core.ModuleCore;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.geometries.objects.GeometryObject;
import pl.edu.icm.visnow.lib.types.VNGeometryObject;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;

/**
 *
 * @author Bartosz Borucki (babor@icm.edu.pl) University of Warsaw,
 * Interdisciplinary Centre for Mathematical and Computational Modelling
 */
public class WriteOBJ extends ModuleCore {

   public static InputEgg[] inputEggs = null;
   public static OutputEgg[] outputEggs = null;
    
    private GUI ui = null;
    protected Params params;

    public WriteOBJ() {
        parameters = params = new Params();
        params.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent evt) {
                startAction();
            }
        });

        SwingInstancer.swingRun(new Runnable() {

            public void run() {
                ui = new GUI();
                ui.setParams(params);
                setPanel(ui);
            }
        });

    }

    @Override
    public void onActive() {
        if (getInputFirstValue("inObject") == null) {
            return;
        }
        
        if(params.getFileName() == null || params.getFileName().length() < 1)
            return;
        
        GeometryObject inObject = ((VNGeometryObject) getInputFirstValue("inObject")).getGeometryObject();
        try {
            File f = new File(params.getFileName());
            System.out.println("writing OBJ file: " + f.getAbsolutePath());
            OBJWriter writer = new OBJWriter(f);
            writer.writeNode(inObject.getGeometryObj());
            writer.close();
            System.out.println("done");
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
