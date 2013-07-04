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

package pl.edu.icm.visnow.lib.gui;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

/**
 * @author  Bartosz Borucki (babor@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class HistoArea extends JPanel {

    int[] histo = null;
    boolean logScale = false;

    @Override
    public void paint(Graphics g) {
        int w = getWidth();
        int h = getHeight();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, w, h);
        if (histo == null) {
            return;
        }
        double max = 0;
        if (logScale) {
            for (int i = 0; i < histo.length; i++) {
                if (max < Math.log1p((double) histo[i])) {
                    max = Math.log1p((double) histo[i]);
                }
            }
        } else {
            for (int i = 0; i < histo.length; i++) {
                if (max < histo[i]) {
                    max = histo[i];
                }
            }
        }
        max = (h - 4) / (max + .01);
        float d = (w - 4.f) / histo.length;
        g.setColor(Color.BLUE);
        if (logScale) {
            for (int i = 0; i < histo.length; i++) {
                int j = (int) (max * Math.log1p((double) histo[i]));
                if (j > 1) {
                    g.drawLine((int) (i * d + 2), h - 2, (int) (i * d + 2), h - 2 - j);
                }
            }
        } else {
            for (int i = 0; i < histo.length; i++) {
                g.drawLine((int) (i * d + 2), h - 2, (int) (i * d + 2), (int) (h - 2 - max * histo[i]));
            }
        }
    }

    public void setHisto(long[] histogram) {
        histo = new int[histogram.length];
        for (int i = 0; i < histogram.length; i++) {
            histo[i] = (int) histogram[i];
        }
        repaint();
    }

    public void setLogScale(boolean logScale) {
        this.logScale = logScale;
        repaint();
    }
}
