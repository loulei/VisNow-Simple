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
import java.awt.Paint;
import javax.swing.JComponent;
import pl.edu.icm.visnow.application.area.AreaPanel;
import pl.edu.icm.visnow.system.swing.VNSwingUtils;

/**
 *
 * @author  Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public  class PortGlowPanel extends JComponent {
        
        public static final int w = 40;
        public static final int h = 40;
        public static final int r = 20;        
        
        //<editor-fold defaultstate="collapsed" desc=" Kolory ">
        
        private static final java.awt.Color[] clearColors = {
            new java.awt.Color(255,255,204,255),
            new java.awt.Color(255,255,204,187),
            new java.awt.Color(255,255,204,119),
            new java.awt.Color(255,255,204,85),
            new java.awt.Color(255,255,204,0)
        };
        private static final java.awt.Color[] okColors = {
            new java.awt.Color(55,255,4,255),
            new java.awt.Color(55,255,4,187),
            new java.awt.Color(55,255,4,119),
            new java.awt.Color(55,255,4,85),
            new java.awt.Color(55,255,4,0)
        };
        private static final java.awt.Color[] warningColors = {
            new java.awt.Color(255,220,4,255),
            new java.awt.Color(255,220,4,187),
            new java.awt.Color(255,220,4,119),
            new java.awt.Color(255,220,4,85),
            new java.awt.Color(255,220,4,0)
        };
        private static final java.awt.Color[] errorColors = {
            new java.awt.Color(255,55,4,255),
            new java.awt.Color(255,55,4,187),
            new java.awt.Color(255,55,4,119),
            new java.awt.Color(255,55,4,85),
            new java.awt.Color(255,55,4,0)
        };

        private static final Paint clearSmooth = 
                new java.awt.RadialGradientPaint(w/2,h/2,r,new float[]{0,1},
                    new java.awt.Color[]{clearColors[0],clearColors[4]}
                );
        private static final Paint okSmooth = 
                new java.awt.RadialGradientPaint(w/2,h/2,r,new float[]{0,1},
                    new java.awt.Color[]{okColors[0],okColors[4]}
                );
        private static final Paint warningSmooth = 
                new java.awt.RadialGradientPaint(w/2,h/2,r,new float[]{0,1},
                    new java.awt.Color[]{warningColors[0],warningColors[4]}
                );
        private static final Paint errorSmooth = 
                new java.awt.RadialGradientPaint(w/2,h/2,r,new float[]{0,1},
                    new java.awt.Color[]{errorColors[0],errorColors[4]}
                );

        private static final Paint clearSmoothSmall = 
                new java.awt.RadialGradientPaint(w/2,h/2,3*r/4,new float[]{0,1},
                    new java.awt.Color[]{clearColors[0],clearColors[4]}
                );
        private static final Paint okSmoothSmall = 
                new java.awt.RadialGradientPaint(w/2,h/2,3*r/4,new float[]{0,1},
                    new java.awt.Color[]{okColors[0],okColors[4]}
                );
        private static final Paint warningSmoothSmall = 
                new java.awt.RadialGradientPaint(w/2,h/2,3*r/4,new float[]{0,1},
                    new java.awt.Color[]{warningColors[0],warningColors[4]}
                );
        private static final Paint errorSmoothSmall = 
                new java.awt.RadialGradientPaint(w/2,h/2,3*r/4,new float[]{0,1},
                    new java.awt.Color[]{errorColors[0],errorColors[4]}
                );
        
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc=" Paint ">
        @Override
        public void paint(Graphics g) {
            Graphics2D gg = (Graphics2D) g;
            gg.addRenderingHints(VNSwingUtils.getHints());
            if(active) {
                switch(errorLevel) {
                    case ERRORLEVEL_OK:
                        gg.setPaint(okSmooth);
                        break;
                    case ERRORLEVEL_WARNING:
                        gg.setPaint(warningSmooth);
                        break;
                    case ERRORLEVEL_ERROR:
                        gg.setPaint(errorSmooth);
                        break;
                }
                gg.fillOval(0,0,w,h);
            } else {
                switch(errorLevel) {
                    case ERRORLEVEL_OK:
                        gg.setPaint(okSmoothSmall);
                        break;
                    case ERRORLEVEL_WARNING:
                        gg.setPaint(warningSmoothSmall);
                        break;
                    case ERRORLEVEL_ERROR:
                        gg.setPaint(errorSmoothSmall);
                        break;
                }
                gg.fillOval(0,0,w,h);                
            }

        }
        //</editor-fold>
        
        
        
       
    
    private boolean active;
    public void setActive(boolean b) {        
        active = b;
    }

    public static final int ERRORLEVEL_OK = 0;
    public static final int ERRORLEVEL_WARNING = 1;
    public static final int ERRORLEVEL_ERROR = 2;    
    private int errorLevel = ERRORLEVEL_OK;
    public void setErrorLevel(int errLevel) {
        this.errorLevel = errLevel;
    }
    
    
    private AreaPanel ap;

        //<editor-fold defaultstate="collapsed" desc=" Konstruktor ">
        public PortGlowPanel(AreaPanel ap) {
            super();
            this.ap = ap;
            //animation.start();
        }
        //</editor-fold>
        
        
    }
