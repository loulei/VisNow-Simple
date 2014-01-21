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
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import pl.edu.icm.visnow.system.swing.VNSwingUtils;



/**
 *
 * @author  Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
class ModuleGlowPanel extends JPanel {
    
    private int getMinus(int a) {
        if(a>0) return 0;
        else return -a;
    }
    private int d;
    
    /**
     * Creates a new instance of GlowPanel
     */
    public ModuleGlowPanel() {
        super();
        this.d = ModulePanel.GLOWBORDER;
    }
    
    @Override
    public void paint(Graphics g) {
        Graphics2D gg = (Graphics2D)g;
        gg.addRenderingHints(VNSwingUtils.getHints());
        gg.setColor(new Color(255,255,204,34));
        //Dalej jest zle: podswietlenie znika, jesli modul przesunac za krawedz przescrollowanej sceny.
        int w = (int)gg.getClipBounds().getWidth()-d-d +getMinus(this.getParent().getParent().getX());
        int h = (int)gg.getClipBounds().getHeight()-d-d +getMinus(this.getParent().getParent().getY());
        int x = d;
        int y = d;
        for(int i=1; i<=d; ++i)
            gg.fillRoundRect(x-i,y-i,2*i+w,2*i+h,d,d);
    }
    
}