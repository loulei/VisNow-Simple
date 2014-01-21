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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.Timer;
import org.apache.log4j.Logger;
import pl.edu.icm.visnow.application.area.AreaPanelMouseEvents;
import pl.edu.icm.visnow.engine.core.Output;
import pl.edu.icm.visnow.engine.main.Port;
import pl.edu.icm.visnow.lib.types.VNField;
import pl.edu.icm.visnow.system.main.VisNow;

/**
 *
 * @author Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class PortPanelMouseEvents implements MouseListener, MouseMotionListener {
    private static final Logger LOGGER = Logger.getLogger(PortPanelMouseEvents.class);
    
    private PortPanel panel;
    private AreaPanelMouseEvents events;
    
    private boolean mouseOverPortPanel = false;
    private int mouseOverPortPanelTooltipDelay = 1000;
    private Timer mouseOverPortPanelTimer = new Timer(mouseOverPortPanelTooltipDelay, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            Port port = panel.getPort();
            String descStr = null;
            if(port instanceof Output) {
                Object obj = ((Output)port).getValue();
                if(obj instanceof VNField) { 
                    if(((VNField)obj).getField() != null)
                        descStr = ((VNField)obj).getField().shortDescription();
                    else 
                        descStr = "no data";
                    if(descStr.startsWith("<html>"))
                        descStr = descStr.substring(6);
                    if(descStr.endsWith("</html>"))
                        descStr = descStr.substring(0,descStr.length()-7);
                }
            }
            StringBuilder s = new StringBuilder();                    
            s.append("<html>").append(panel.getPort().getName());
            if(panel.getPort().getDescription() != null)
                s.append("<br>").append(panel.getPort().getDescription());
            if(descStr != null)
                s.append("<br>----<br>").append(descStr);                    
            s.append("</html>");

            JLabel lbl = new JLabel(s.toString());
            lbl.setForeground(Color.BLACK);
            lbl.setOpaque(true);
            lbl.setBackground(new Color(255,255,200));
            lbl.setBorder(BorderFactory.createLineBorder(Color.WHITE));
            if(!panel.isVisible()) {
                mouseOverPortPanelTimer.stop();
                return;
            }
            if(!panel.isVisible()) {
                mouseOverPortPanelTimer.stop();
                return;
            }
            Point p = new Point(panel.getLocationOnScreen().x + 25, 
                                panel.getLocationOnScreen().y + 15);
            int width = lbl.getPreferredSize().width;
            int height = lbl.getPreferredSize().height;
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            if (p.x + width > screenSize.getWidth())
               p.x = (int) screenSize.getWidth() - width;
            if (p.y + height > screenSize.getHeight())
               p.y = (int) screenSize.getHeight() - height;            
            lbl.setBounds(p.x, p.y, width, height);
            events.getAreaPanel().setFloatingComponent(lbl, p);
            mouseOverPortPanelTimer.stop();
        }
    });

    
    
    PortPanelMouseEvents(PortPanel panel) {
        this.panel = panel;
        if(panel != null && panel.getModulePanel() != null && panel.getModulePanel().getAreaPanel() != null)
            this.events = panel.getModulePanel().getAreaPanel().getMouseEvents();
    }

   @Override
   public void mouseClicked(MouseEvent e)
   {
      if (e.isMetaDown() || e.isAltDown() || e.isAltGraphDown())
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

    @Override
    public void mousePressed(MouseEvent e) {        
        if(mouseOverPortPanel) {
            //LOGGER.info("");
            mouseOverPortPanel = false;
            mouseOverPortPanelTimer.stop();
            events.getAreaPanel().removeFloatingComponent();
        }            
        
        events.portPanelSelfPressed(e, panel);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        events.mouseReleased(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        mouseOverPortPanel = true;
        mouseOverPortPanelTimer.restart();        
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if(mouseOverPortPanel) {
            mouseOverPortPanel = false;
            mouseOverPortPanelTimer.stop();
            events.getAreaPanel().removeFloatingComponent();
        }            
        
        panel.getModulePanel().getAreaPanel().repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        events.mousePortDragged(panel, e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        
    }

}
