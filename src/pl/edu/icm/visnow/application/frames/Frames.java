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

package pl.edu.icm.visnow.application.frames;

import pl.edu.icm.visnow.application.frames.tabs.MLibrariesPanel;
import pl.edu.icm.visnow.application.frames.tabs.ModulesGUIPanel;
import pl.edu.icm.visnow.application.frames.tabs.MPortsPanel;
import pl.edu.icm.visnow.application.frames.tabs.MTitlePanel;
import pl.edu.icm.visnow.application.application.Application;
//import pl.edu.icm.visnow.application.frames.tabs.MMasterLibrariesPanel;

/**
 *
 * @author Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class Frames {


    protected Application application;
    protected ApplicationFrame2 applicationFrame;
    protected ModulesGUIPanel guiPanel;
    protected MLibrariesPanel librariesPanel;
    protected MPortsPanel modulePanel;
    protected MTitlePanel titlePanel;
    //protected MRootsPanel rootsPanel;
//    protected MMasterLibrariesPanel masterLibrariesPanel;
//    protected ScenePanel scenePanel;

    protected FrameExecutor executor;

    public Frames(Application application) {
        this.application = application;
        this.guiPanel = new ModulesGUIPanel(this);
        this.librariesPanel = new MLibrariesPanel(this);
        this.modulePanel = new MPortsPanel(this);
        this.titlePanel = new MTitlePanel(this);
        this.executor = new FrameExecutor(this);
//        this.masterLibrariesPanel = new MMasterLibrariesPanel(this);
        this.applicationFrame = new ApplicationFrame2(this);
        //this.rootsPanel = new MRootsPanel();
        this.selectingEnabled = true;
    }

    //<editor-fold defaultstate="collapsed" desc=" Getters ">
    /**
     * @return the application
     */
    public Application getApplication() {
        return application;
    }

    /**
     * @return the applicationFrame
     */
    public ApplicationFrame2 getApplicationFrame() {
        return applicationFrame;
    }

    /**
     * @return the guiPanel
     */
    public ModulesGUIPanel getGuiPanel() {
        return guiPanel;
    }

    /**
     * @return the librariesPanel
     */
    public MLibrariesPanel getLibrariesPanel() {
        return librariesPanel;
    }

    /**
     * @return the modulePanel
     */
    public MPortsPanel getModulePanel() {
        return modulePanel;
    }

    /**
     * @return the titlePanel
     */
    public MTitlePanel getTitlePanel() {
        return titlePanel;
    }

    /**
     * @return the executor
     */
    public FrameExecutor getExecutor() {
        return executor;
    }

//    public MRootsPanel getRootsPanel() {
//        return rootsPanel;
//    }

    //</editor-fold>


    private boolean selectingEnabled;

    public boolean isSelectingEnabled() {
        return selectingEnabled;
    }

    public void setSelectingEnabled(boolean b) {
        selectingEnabled = b;
    }
}
