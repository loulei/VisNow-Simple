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
import java.util.Vector;
import javax.swing.JComponent;
import pl.edu.icm.visnow.application.area.Quad;
import pl.edu.icm.visnow.application.area.SelectableAreaItem;
import pl.edu.icm.visnow.engine.core.Link;
import pl.edu.icm.visnow.system.framework.ShowContentDialog;
import pl.edu.icm.visnow.system.swing.VNSwingUtils;

/**
 *
 * @author Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class LinkPanel extends JComponent implements SelectableAreaItem {

    //<editor-fold defaultstate="collapsed" desc=" [VAR] Link ">
    private Link link;
    public Link getLink() {return link;}
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" [VAR] Ports ">
    private PortPanel outputPanel;
    private PortPanel inputPanel;

    public PortPanel getInputPanel() {return inputPanel;}
    public PortPanel getOutputPanel() {return outputPanel;}
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" [VAR] Lines ">
    private Vector<LinkPanelRectangle> lines;
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" [VAR] Selection ">
    private boolean selected;

    public String getModuleForSelecting() {return null;}
    public boolean isSelected() {return selected;}
    public void setSelected(boolean b) {
        selected = b;
        repaint();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" [VAR] Menu ">
    private javax.swing.JMenuItem mbRemove;
    private javax.swing.JMenuItem mbShowContent;
    private javax.swing.JMenuItem mbSplit;
    private javax.swing.JPopupMenu popupMenu;
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Painting, Collision ">

    public boolean isRectangled(Quad q) {
        for(LinkPanelRectangle r: lines) {
            if(r.isRectangled(q)) {
                return true;
            }
        }
        return false;
    }

    public boolean isHit(Point p) {
        for(LinkPanelRectangle r: lines) {
            if(r.contains(p)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void paint(Graphics g) {
        //super.paint(g);
        Graphics2D gg = (Graphics2D) g;
        int fromX = outputPanel.getTotalX()+8;
        int fromY = outputPanel.getTotalY()+4;

        int toX = inputPanel.getTotalX()+8;
        int toY = inputPanel.getTotalY()+6;

        int secX = fromX;
        int secY = fromY+6;
        int lasX = toX;
        int lasY = (this.inputPanel!=null)?toY-6:toY-15;
        int[] x;
        int[] y;


        lines = new Vector<LinkPanelRectangle>();

        if(secY<lasY) {
            int midY = (secY+lasY)/2;

            x = new int[]{fromX,secX,secX,lasX,lasX,toX};
            y = new int[]{fromY,secY,midY,midY,lasY,toY};
            for(int i=0; i<5; ++i) {
                lines.add(new LinkPanelRectangle(x[i], y[i], x[i + 1], y[i + 1], 2));
            }


        } else {
            int midX = (secX+lasX)/2;
            x = new int[]{fromX,secX,midX,midX,lasX,toX};
            y = new int[]{fromY,secY,secY,lasY,lasY,toY};
            for(int i=0; i<5; ++i) {
                lines.add(new LinkPanelRectangle(x[i], y[i], x[i + 1], y[i + 1], 2));
            }
        }
        gg.addRenderingHints(VNSwingUtils.getHints());
        if(selected) {
            //gg.setColor(new java.awt.Color(255,255,204,153));
            //gg.setStroke(new BasicStroke(4,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
            //gg.drawPolyline(x,y,6);
            gg.setColor(new java.awt.Color(255,255,204,153));
            gg.setStroke(new BasicStroke(4,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND,1.0f,new float[]{8,8},8));
            gg.drawPolyline(x,y,6);
        }

        gg.setColor(VNSwingUtils.typeColor(getLink().getOutput().getType().getName()));
        //gg.setColor(Utils.anyColor);// TODO: Palette.getCurrent().getDataTypeStyle(getLink().getOutput().getType()).getColor());
        //if(this.getLink().getInput().getType() == Integer.class) gg.setColor(Utils.anyOtherColor);
        gg.setStroke(new BasicStroke(2,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
        gg.drawPolyline(x,y,6);
//        gg.drawLine(fromX,fromY,toX,toY);
        //gg.setColor(new java.awt.Color(255,0,0));
        //gg.setStroke(new BasicStroke(1,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
        //gg.drawLine(fromX,fromY,toX,toY);
        //gg.drawPolyline(x,y,6);
          gg.setColor(VNSwingUtils.typeColor(getLink().getOutput().getType().getName()));
//        gg.setColor(Utils.anyColor); //TODO: Palette.getCurrent().getDataTypeStyle(getLink().getOutput().getType()).getColor());
  //      if(this.getLink().getInput().getType() == Integer.class) gg.setColor(Utils.anyOtherColor);
//            gg.fillPolygon(new int[]{fromX-8,fromX+8,fromX,fromX-8},
//                           new int[]{fromY,  fromY,fromY+5,fromY},4);
//            gg.fillPolygon(new int[]{toX-8,toX+8,toX,toX-8},
//                           new int[]{toY,  toY,toY-15,toY},4);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" [Constructor] ">
    /**
     * Creates new instance of LinkConnectingPanel
     */
    public LinkPanel(Link link, PortPanel output, PortPanel input) {

            this.outputPanel = output;
            this.inputPanel = input;

        this.link = link;
        lines = new Vector<LinkPanelRectangle>();
        selected = false;

        popupMenu = new javax.swing.JPopupMenu();
        mbRemove = new javax.swing.JMenuItem();
        mbShowContent = new javax.swing.JMenuItem();

        mbRemove.setText("Remove");
        mbRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRemove();
            }
        });

        popupMenu.add(mbRemove);

        mbShowContent.setText("Show content");
        mbShowContent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuShowContent();
            }
        });

        popupMenu.add(mbShowContent);

//        mbSplit = new JMenuItem();
//        mbSplit.setText("Split");
//        mbSplit.addActionListener(new ActionListener() {
//
//            public void actionPerformed(ActionEvent e) {
//                menuSplit();
//            }
//        });
//        popupMenu.add(mbSplit);
//
    }
   //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Menu ">
    public void showMenu(Point p) {
        popupMenu.show(this,(int)p.getX(),(int)p.getY());
    }
    //</editor-fold>

    private void menuRemove() {
        getOutputPanel()
                        .getModulePanel()
                        .getAreaPanel()
                        .getArea()
                        .getOutput()
                        .deleteLink(getLink().getName());
                        //.getApplication()
                        //.getReceiver()
                        //.receive(new LinkDeleteCommand(getLink().getName()));
    }

    private void menuShowContent() {
        //javax.swing.JOptionPane.showMessageDialog(null,getLink().getOutput().getValue());
        ShowContentDialog.showContentDialog(
                null,
                getLink().getOutput().getModuleBox().getName() +" -> "+ getLink().getOutput().getName(),
                getLink().getOutput().getValue()
                );

    }

    private void menuSplit() {
    }
}
