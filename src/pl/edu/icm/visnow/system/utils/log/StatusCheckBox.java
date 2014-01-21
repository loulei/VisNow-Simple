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

package pl.edu.icm.visnow.system.utils.log;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import org.apache.log4j.Logger;

/**
 * Checkbox with color rectangle in place of standard "tick" mark.
 * 
 * @author szpak
 */
public class StatusCheckBox extends JCheckBox{
    private static final Logger LOGGER = Logger.getLogger(StatusCheckBox.class);
    
    public StatusCheckBox(String text, boolean selected, Color color) {
        super(text,selected);
        setIcon(new StatusCheckBoxIcon(color));
    }
    
    private class StatusCheckBoxIcon implements Icon {
        //default size (just to not return 0 on getIconHeight/Width)
        private int size = 16;
        private Color color;
        
        public StatusCheckBoxIcon(Color color) {
            this.color = color;
        }        
        public int getIconHeight() {
            return size;
        }
        public int getIconWidth() {
            return size;
        }
        public void paintIcon(Component c, Graphics g, int x, int y) {
            JCheckBox cb = (JCheckBox)c;           
            int margin = 2;
            this.size = cb.getHeight() - cb.getMargin().top-cb.getMargin().bottom - margin*2;
            if (cb.isSelected()) {
                g.setColor(color);
                g.fillRect(cb.getMargin().left,cb.getMargin().top + margin, size, size);
                g.setColor(Color.WHITE);
                g.drawRect(cb.getMargin().left,cb.getMargin().top + margin, size, size);
            } else {
                g.setColor(cb.getBackground().darker());
                g.fillRect(cb.getMargin().left,cb.getMargin().top + margin, size, size);
            }
        }        
    }
}
