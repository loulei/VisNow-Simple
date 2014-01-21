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

package pl.edu.icm.visnow.application.area;

import java.util.Vector;
import pl.edu.icm.visnow.application.application.Application;
import pl.edu.icm.visnow.application.area.widgets.ModulePanel;
import pl.edu.icm.visnow.engine.core.LinkName;



/**
 *
 * @author Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class Area {

    
    private Application application;
    private AreaPanel areaPanel;
    private AreaInput areaInput;
    private AreaOutput areaOutput;

    protected Application getApplication() {return application;}
    public AreaPanel getPanel() {return areaPanel;}
    public AreaInput getInput() {return areaInput;}
    public AreaOutput getOutput() {return areaOutput;}

    public Area(Application application) {
        this.application = application;
        this.areaPanel = new AreaPanel(this);
        this.areaInput = new AreaInput(this);
        this.areaOutput = new AreaOutput(this);
    }

    private Vector<SelectableAreaItem> selection = new Vector<SelectableAreaItem>();

    public void select(SelectableAreaItem item) {
        selectNull();
        if(item != null) {
            selection.add(item);
            item.setSelected(true);
            if(item instanceof ModulePanel) {
                moduleSelectionChanged( ((ModulePanel)item).getModule().getName());
                areaPanel.repaint();
                return;
            }
        }
        moduleSelectionChanged(null);
        areaPanel.repaint();        
    }

    public void selectNull() {
        for(int i=0; i<selection.size();i++) {
            selection.get(i).setSelected(false);
        }
        selection.clear();
        areaPanel.repaint();
    }

    public void alterSelection(SelectableAreaItem item) {
        if(item.isSelected()) {
            item.setSelected(false);
            selection.remove(item);
        } else {
            item.setSelected(true);
            selection.add(item);
            if(item instanceof ModulePanel) {
                moduleSelectionChanged( ((ModulePanel)item).getModule().getName());
            }
        }
        areaPanel.repaint();
    }

    private void moduleSelectionChanged(String name) {
        getApplication().getFrames().getExecutor().selectModule(name);
    }

    /**
     * @return the selection
     */
    public Vector<SelectableAreaItem> getSelection() {
        return selection;
    }


}
