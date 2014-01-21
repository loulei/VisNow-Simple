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

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import pl.edu.icm.visnow.application.area.widgets.DataPanel;
import pl.edu.icm.visnow.application.area.widgets.LinkConnectingPanel;
import pl.edu.icm.visnow.application.area.widgets.ModulePanel;
import pl.edu.icm.visnow.application.area.widgets.PortPanel;
import pl.edu.icm.visnow.engine.core.Input;
import pl.edu.icm.visnow.engine.core.Output;
import pl.edu.icm.visnow.engine.main.Port;

/**
 *
 * @author Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class AreaPanelInternalManager {
    private static final Logger LOGGER = Logger.getLogger(AreaPanelInternalManager.class);
    
    private AreaPanel areaPanel;
    public AreaPanel getAreaPanel() {return areaPanel;}

    public AreaPanelInternalManager(AreaPanel areaPanel) {
        this.areaPanel = areaPanel;
    }

    void modulePanelPressed(DataPanel panel, int x, int y) {
        System.out.println("MPP/D");
        throw new UnsupportedOperationException("Not yet implemented");
    }



    private boolean probablyDragging = false;
    private boolean dragging = false;
    private int dragOriginX = 0;
    private int dragOriginY = 0;
    private ModulePanel dragPanel;


    void modulePanelPressed(ModulePanel panel, int x, int y) {
        probablyDragging = true;
        dragging = false;
        dragOriginX = panel.getLocation().x - x;
        dragOriginY = panel.getLocation().y - y;
        dragPanel = panel;
    }

    void modulePanelRightClicked(ModulePanel panel, int x, int y) {
        panel.showPopupMenu();        
    }
    
    void mouseDragged(int x, int y) {
        //if(! (dragging || probablyDragging)) return;
        if(probablyDragging) {
//            LOGGER.debug("probably dragging");
            probablyDragging = false;
            dragging = true;
            areaPanel.moveToDragLayer(dragPanel, true);
        }
        if (dragging) {
//            LOGGER.debug("dragging");
            Point p = dragPanel.getLocation();
            //dragging module within bounds (work area)
            dragPanel.setLocation(Math.min(areaPanel.getBgPanel().getWidth() - dragPanel.getWidth(), 
                                    Math.max(0, dragOriginX + x)), 
                                    Math.min(areaPanel.getBgPanel().getHeight() - dragPanel.getHeight(), 
                                    Math.max(0, dragOriginY + y)));
        }
        if(probablyConnecting) {
//            LOGGER.debug("probably connecting");
            probablyConnecting = false;
            connecting = true;
            connectingPanel = new LinkConnectingPanel(connectingPort, new Point(x,y));
            areaPanel.showConnectingPanel(connectingPanel, true);
        }
        if(connecting) {
//            LOGGER.debug("connecting");
            connectingPanel.setPoint(new Point(x,y));
            Component c = areaPanel.getAreaComponentAt(new Point(x,y));
            if(c instanceof PortPanel) {
                if(c != targetPort) {
                    areaPanel.removePortGlowPanelLarge();
                    targetPort = (PortPanel)c;
                    connectionEstablishing = false;
                    if(targetPort.getPort().isLinkPossible(connectingPort.getPort()) &&
                       targetPort.getPort().isLinkLoopPossible(connectingPort.getPort())
                       ) {
                        int state = targetPort.getPort().getLinkDataStatus(connectingPort.getPort());
                        if(state != Port.LINK_DATA_STATUS_ERROR) {
                            areaPanel.removeAllPortGlowPanelsSmall();
                            setConnectionWillStartAction(targetPort.getPort().willStartAction(connectingPort.getPort()));
                            areaPanel.showPortGlowPanelLarge(
                                    targetPort,
                                    true,
                                    state
                                    );

                            connectionEstablishing = true;
                        }
                    }
                }
            } else {
                connectionEstablishing = false;
                targetPort = null;
                areaPanel.removeAllPortGlowPanelsSmall();
                areaPanel.removePortGlowPanelLarge();
                
                ArrayList<PortPanel> pps;
                if(connectingPort.getPort() instanceof Output)                
                    pps = areaPanel.getAllAreaInputPortPanels();
                else if(connectingPort.getPort() instanceof Input)                
                    pps = areaPanel.getAllAreaOutputPortPanels();
                else
                    return;
                
                for (int i = 0; i < pps.size(); i++) {
                    PortPanel pp = pps.get(i);
                    if(pp.getPort().isLinkPossible(connectingPort.getPort()) &&
                       pp.getPort().isLinkLoopPossible(connectingPort.getPort()))
                            areaPanel.showPortGlowPanelSmall(
                                pp,
                                false,
                                pp.getPort().getLinkDataStatus(connectingPort.getPort())
                                );
                }
            }
        }
    }


    private boolean probablyConnecting = false;
    private boolean connectionWillStartAction = false;
    private void setConnectionWillStartAction(boolean connectionWillStartAction) {
        this.connectionWillStartAction = connectionWillStartAction;
    }
    private boolean connecting = false;
    private PortPanel connectingPort;
    private LinkConnectingPanel connectingPanel;
    private PortPanel targetPort;
    private boolean connectionEstablishing = false;

    void portPressed(PortPanel panel, Point point) {
        //Quick fix: testing if in connecting state (flow is not documented so this update may be possibly risky)
        //This is to avoid zombie arrows (refs #491)
        if (!connecting) {
            probablyConnecting = true;
            connectingPort = panel;
        }
    }

    void mouseReleased(boolean ctrlDown) {
        if(dragging) {
            dragging = false;
            areaPanel.moveToDragLayer(dragPanel, false);
        }
        if(probablyDragging) {
            this.areaPanel.getArea().getOutput().select(dragPanel, ctrlDown);
            probablyDragging = false;
        }
        if(probablyConnecting) {
            probablyConnecting = false;
        }
        if(connecting) {
            areaPanel.showConnectingPanel(connectingPanel, false);
            if(connectionEstablishing) {
                connectionEstablishing = false;
                areaPanel.getArea().getOutput().addLink(
                        connectingPort.getPort(),
                        targetPort.getPort(),
                        connectionWillStartAction);
            }
            connecting = false;
            areaPanel.removeAllPortGlowPanelsSmall();
            areaPanel.removePortGlowPanelLarge();
            targetPort = null;
        }
    }

 

//    void mouseConnectionMoved(Point point) {
//
//    }

    public boolean isConnecting() {
        return connecting;
    }
}
