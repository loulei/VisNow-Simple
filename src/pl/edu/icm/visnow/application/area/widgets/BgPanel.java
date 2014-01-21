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
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import pl.edu.icm.visnow.system.swing.JComponentViewer;

/**
 *
 * @author  Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class BgPanel extends JPanel {
    
    //<editor-fold defaultstate="collapsed" desc=" Back Images ">
    private static BufferedImage backImage = null;
    static BufferedImage getBackImage() {return backImage;}
    private static BufferedImage backLockImage = null;
    static BufferedImage getBackLockImage() {return backLockImage;}
    
    public static void initBackImages() {
        if(backImage == null) {
            int deepGrayInt = 20 * (1 + 256 + 256*256);
            int textureSize = 30;
            int textureSizeMinus = textureSize-1;
            backImage = new java.awt.image.BufferedImage(textureSize,textureSize,java.awt.image.BufferedImage.TYPE_INT_RGB);
            for(int i=0; i<textureSizeMinus; ++i) for(int j=0; j<textureSizeMinus; ++j)
                backImage.setRGB(i,j,0);
                
            for(int i=0; i<=textureSizeMinus; ++i) {
                backImage.setRGB(textureSizeMinus,i,deepGrayInt);
                backImage.setRGB(i,textureSizeMinus,deepGrayInt);
            }
        }
        if(backLockImage == null) {
            int bgBlue = 40*256*256; //oryginalnie: 30
            int deepBlue = 20 * (1 + 256 + 256*256) + bgBlue;
            int textureSize = 30;
            int textureSizeMinus = textureSize-1;
            backLockImage = new java.awt.image.BufferedImage(textureSize,textureSize,java.awt.image.BufferedImage.TYPE_INT_RGB);
            for(int i=0; i<textureSizeMinus; ++i) for(int j=0; j<textureSizeMinus; ++j)
                backLockImage.setRGB(i,j,bgBlue);
                
            for(int i=0; i<=textureSizeMinus; ++i) {
                backLockImage.setRGB(textureSizeMinus,i,deepBlue);
                backLockImage.setRGB(i,textureSizeMinus,deepBlue);
            }
        }
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc=" [VAR] - locked ">
    private boolean locked;
    public void setLocked(boolean b) {this.locked = b; repaint();}
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc=" [Constructor] ">
    public BgPanel() {        
        super();
        this.setBackground(new Color(0,0,0));
        BgPanel.initBackImages();
        locked = false;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc=" Paint ">
    @Override
    public void paint(Graphics g) {
            super.paint(g);
            Graphics2D gg = (Graphics2D)g;
            gg.setPaint(new TexturePaint(
                    (locked)?getBackLockImage():getBackImage(),
                    new java.awt.Rectangle(0,0,30,30))
                    );
            gg.fill(gg.getClip());
    }
    //</editor-fold>


    public static void main(String[] args) {
        JComponentViewer v = new JComponentViewer(new BgPanel(), 400, 400, true);
        v.setVisible(true);
    }

}
