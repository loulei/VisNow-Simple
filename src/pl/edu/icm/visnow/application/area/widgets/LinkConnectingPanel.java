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



import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import pl.edu.icm.visnow.system.swing.VNSwingUtils;


/**
 *
 * @author  Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class LinkConnectingPanel extends javax.swing.JComponent {
    
    //<editor-fold defaultstate="collapsed" desc=" [VAR] Scene ">
    private PortPanel output;
    private PortPanel input;
    public PortPanel getOutputPanel() {return output;}
    public PortPanel getInputPanel() {return input;}
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc=" [VAR] Controls ">
    public boolean isFromOutput() {return (input==null);}
    private Point point;
    public void setPoint(Point point) {
        this.point = point;
        this.repaint();
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc=" Paint ">
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D gg = (Graphics2D) g;
        int fromX = (this.output!=null)?output.getTotalX()+8:(int)point.getX();
        int fromY = (this.output!=null)?output.getTotalY()+4:(int)point.getY()+2;
        
        int toX = (this.input!=null)?input.getTotalX()+8:(int)point.getX();
        int toY = (this.input!=null)?input.getTotalY()+6:(int)point.getY()-2;
        
        int secX = fromX;
        int secY = fromY+6;
        int lasX = toX;
        int lasY = (this.input!=null)?toY-6:toY-15;
        int[] x;
        int[] y;
        
        if(secY<lasY) {
            int midY = (secY+lasY)/2;
            
            x = new int[]{fromX,secX,secX,lasX,lasX,toX};
            y = new int[]{fromY,secY,midY,midY,lasY,toY};
            
            
        } else {
            int midX = (secX+lasX)/2;
            x = new int[]{fromX,secX,midX,midX,lasX,toX};
            y = new int[]{fromY,secY,secY,lasY,lasY,toY};
        }
        
        
        gg.addRenderingHints(VNSwingUtils.getHints());
        if(output!= null) {
            gg.setColor(VNSwingUtils.typeColor(getOutputPanel().getPort().getType().getName()));
            //gg.setColor(Utils.anyColor);// TODO: Palette.getCurrent().getDataTypeStyle(getOutputPanel().getPort().getType()).getColor());
        } else {
            gg.setColor(VNSwingUtils.typeColor(getInputPanel().getPort().getType().getName()));
            //gg.setColor(Utils.anyColor); /// TODO: Palette.getCurrent().getDataTypeStyle(getInputPanel().getPort().getType()).getColor());
        }
        gg.setStroke(new BasicStroke(2,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
        gg.drawPolyline(x,y,6);
        
        if(this.output!=null) {
            gg.fillPolygon(new int[]{fromX-8,fromX+8,fromX,fromX-8},
                           new int[]{fromY,  fromY,fromY+5,fromY},4);
        } else {
            gg.fillPolygon(new int[]{fromX,fromX+4,fromX-4,fromX},
                           new int[]{fromY,fromY+8,fromY+8,fromY},4);
        }
        if(this.input!=null) {
            gg.fillPolygon(new int[]{toX-8,toX+8,toX,toX-8},
                           new int[]{toY,  toY,toY-5,toY},4);
        } else {
            gg.fillPolygon(new int[]{toX,toX+4,toX-4,toX},
                           new int[]{toY,toY-8,toY-8,toY},4);
        }
        
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc=" IsConnectionPossible ">
    public boolean isConnectionPossible(Object o) {
        if(!(o instanceof PortPanel)) {
            return false;
        }
        if(this.isFromOutput()) {
            if(getOutputPanel().getPort().isLinkPossible(((PortPanel)o).getPort())) {
                return true;
            }
        } else {
            if(getInputPanel().getPort().isLinkPossible(((PortPanel)o).getPort())) {
                return true;
            }
        }
        return false;

    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc=" [Constructor] ">
    /**
     * Creates new instance of LinkConnectingPanel
     */
    public LinkConnectingPanel(PortPanel panel,Point point) {
       
        if(panel.getPort().isInput()) {
            this.output = null;
            this.input = panel;
        } else {
            this.output = panel;
            this.input = null;
        }
        this.point = point;
        
    }
    //</editor-fold>
    
    
}
