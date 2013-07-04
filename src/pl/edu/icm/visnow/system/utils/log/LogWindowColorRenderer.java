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
import java.util.HashMap;
import java.util.Map;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 * Simple color renderer; It renders JList Items in color according to Level value in LogLine (as an item in the list).
 * Additionally it contains levels color map. This map can be used to render buttons responsible
 * for turning on/off some particular level of logging.
 * 
 * @author szpak
 */
public class LogWindowColorRenderer extends DefaultListCellRenderer {
    public static final Color defaultColor = new Color(223,223,255);
    public static final Map<LogLine.Level,Color> colorMap;
    static {
        colorMap = new HashMap<LogLine.Level,Color>();
        colorMap.put(LogLine.Level.FATAL, Color.RED);
        colorMap.put(LogLine.Level.ERROR, Color.ORANGE);
        colorMap.put(LogLine.Level.WARN, Color.YELLOW);
        colorMap.put(LogLine.Level.INFO, new Color(223,255,223));
        colorMap.put(LogLine.Level.DEBUG, Color.WHITE);        
        colorMap.put(LogLine.Level.TRACE, new Color(223,223,255));        
        colorMap.put(LogLine.Level.UNKNOWN_LEVEL, new Color(223,223,255));
    }   
    
    public Component getListCellRendererComponent(
            JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        LogLine line = (LogLine) value;
        setText(line.getLine());
        Color c = colorMap.get(line.getLevel());
        setBackground(c);
        //set black to avoid default white color for selected items
        setForeground(Color.BLACK);
        return this;
    }
}
