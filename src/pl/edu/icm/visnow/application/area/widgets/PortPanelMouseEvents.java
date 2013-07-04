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

package pl.edu.icm.visnow.application.area.widgets;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JMenu;
import pl.edu.icm.visnow.application.area.AreaPanelMouseEvents;
import pl.edu.icm.visnow.engine.core.Output;
import pl.edu.icm.visnow.system.main.VisNow;

/**
 *
 * @author Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class PortPanelMouseEvents implements MouseListener, MouseMotionListener {

    private PortPanel panel;
    private AreaPanelMouseEvents events;

    PortPanelMouseEvents(PortPanel panel) {
        this.panel = panel;
        if(panel != null && panel.getModulePanel() != null && panel.getModulePanel().getAreaPanel() != null)
            this.events = panel.getModulePanel().getAreaPanel().getMouseEvents();
    }

   @Override
   public void mouseClicked(MouseEvent e)
   {
      if (e.isMetaDown())
      {
         if (!panel.getPort().isInput())
         {
            Output out = (Output) panel.getPort();
            JMenu attachMenu = VisNow.get().getAttachWizard().getNewFilteredMenu(
                    out,
                    panel.getTotalX(),
                    panel.getTotalY(),
                    panel);
            panel.setAttachMenu(attachMenu);
         }
         panel.showMenu(e.getPoint());
      }
   }

    public void mousePressed(MouseEvent e) {
        events.portPanelSelfPressed(e, panel);
    }

    public void mouseReleased(MouseEvent e) {
        events.mouseReleased(e);
    }

    public void mouseEntered(MouseEvent e) {
        
    }

    public void mouseExited(MouseEvent e) {
        panel.getModulePanel().getAreaPanel().repaint();
    }

    public void mouseDragged(MouseEvent e) {
        events.mousePortDragged(panel, e);
    }

    public void mouseMoved(MouseEvent e) {
        
    }

}
