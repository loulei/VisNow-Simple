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

package pl.edu.icm.visnow.lib.basic.viewers.Viewer2D;

import java.util.Vector;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pl.edu.icm.visnow.engine.core.InputEgg;
import pl.edu.icm.visnow.engine.core.LinkFace;
import pl.edu.icm.visnow.engine.core.ModuleCore;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.lib.types.VNGeometryObject;
import pl.edu.icm.visnow.lib.utils.geometry2D.GeometryObject2DStruct;
import pl.edu.icm.visnow.lib.utils.geometry2D.TransformedGeometryObject2D;
import pl.edu.icm.visnow.system.main.VisNow;

/**
 * @author  Bartosz Borucki (babor@icm.edu.pl)
 * Warsaw University, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class Viewer2D extends ModuleCore {

    private Display2DFrame window;
    private GUI ui;
    private int stamper = 0;

    /**
     * Creates a new instance of Viewer2D
     */
    public Viewer2D() {
         ui = new GUI();
         ui.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                window.setVisible(true);
            }
        });
         window = new Display2DFrame();
         window.setBounds(0, 20, VisNow.displayWidth, VisNow.displayHeight);
         window.setTitle("Viewer 2D");
         window.setVisible(true);
         setPanel(ui);
    }

    public static InputEgg[] inputEggs = null;
    public static OutputEgg[] outputEggs = null;

    public static boolean isViewer() {
        return true;
    }

    @Override
    public void onDelete() {
        window.getDisplayPanel().clearAllGeometry();
        window.dispose();
    }

    @Override
    public void onActive() {
        window.setVisible(true);       
        String tmpName;
        Vector<Object> ins = getInputValues("inObject");
        GeometryObject2DStruct struct;
        Object obj;
        for (int i = 0; i < ins.size(); i++) {
            obj = ins.get(i);
            if(obj == null || !(obj instanceof VNGeometryObject) || ((VNGeometryObject)obj).getGeometryObject2DStruct() == null)
                continue;
            
            struct = ((VNGeometryObject) obj).getGeometryObject2DStruct();
            if (window.getDisplayPanel().getChildByParentModulePort(struct.getParentModulePort()) == null) {
                TransformedGeometryObject2D trobj = new TransformedGeometryObject2D(struct);
                String tmp = struct.getParentModulePort().substring(0, struct.getParentModulePort().indexOf("."));
                if (!trobj.getName().contains(tmp)) {
                    trobj.setName(trobj.getName() + " (" + tmp + ")");
                }

                trobj.setParentModulePort(struct.getParentModulePort());
                window.getDisplayPanel().addChild(trobj);
            } else {
                tmpName = window.getDisplayPanel().getChildByParentModulePort(struct.getParentModulePort()).getName();
                window.getDisplayPanel().getChildByParentModulePort(struct.getParentModulePort()).updateWithStruct(struct);
                String tmp2 = struct.getName() + " (" + struct.getParentModulePort().substring(0, struct.getParentModulePort().indexOf(".")) + ")";
                if (!tmpName.equals(tmp2)) {
                    tmpName = new String(tmp2);
                }
                window.getDisplayPanel().getChildByParentModulePort(struct.getParentModulePort()).setName(tmpName);
                window.getDisplayPanel().update();
            }
        }

    }

    @Override
    public void onInputDetach(LinkFace link) {
        TransformedGeometryObject2D tobj = window.getDisplayPanel().getChildByParentModulePort("" + link.getOutput());
        if(tobj != null) {
            window.getDisplayPanel().removeChild(tobj);
            tobj.getGeometryObject2DStruct().removeChangeListener(object2DChangedListener);
        }

    }

    @Override
    public void onInputAttach(LinkFace link) {
        Vector ins = link.getInput().getValues();
        if (ins == null) {
            return;
        }

        if (ins.get(ins.size() - 1) == null) {
            return;
        }

        TransformedGeometryObject2D obj = null;
        GeometryObject2DStruct struct = ((VNGeometryObject) ins.get(ins.size() - 1)).getGeometryObject2DStruct();
        if (struct == null) {
            return;
        }

        struct.addChangeListener(object2DChangedListener);
        
        obj = new TransformedGeometryObject2D(struct);

        if (!obj.getName().contains(link.getName().getOutputModule())) {
            obj.setName(obj.getName() + " (" + link.getName().getOutputModule() + ")");
        }

        if(obj != null)
            window.getDisplayPanel().addChild(obj);
        
        window.getDisplayPanel().reset();
    }

    private ChangeListener object2DChangedListener = new ChangeListener() {

        @Override
        public void stateChanged(ChangeEvent e) {
            onActive();
        }
    };

}

