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

package pl.edu.icm.visnow.system.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;
import pl.edu.icm.visnow.engine.library.TypesMap;
import pl.edu.icm.visnow.engine.library.jar.JarLibReader;
import pl.edu.icm.visnow.system.main.VisNow;

/**
 *
 * @author Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class VNSwingUtils {

    public static void setComponentSize(Component component, int width, int height) {
        Dimension dim = new Dimension(width, height);
        component.setMinimumSize(dim);
        component.setPreferredSize(dim);
        component.setMaximumSize(dim);
    }

    public static void setFillerComponent(Container outer, Component inner) {
        outer.removeAll();
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(outer);
        outer.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(inner, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(inner, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        outer.doLayout();
        outer.repaint();
    }

    public static void setConstantHeight(Component c, int h) {
        int w1 = c.getMinimumSize().width;
        int w2 = c.getPreferredSize().width;
        int w3 = c.getMaximumSize().width;
        c.setMinimumSize(new Dimension(w1, h));
        c.setPreferredSize(new Dimension(w2, h));
        c.setMaximumSize(new Dimension(w3, h));
    }

     public static void setConstantWidth(Component c, int w) {
        int h1 = c.getMinimumSize().height;
        int h2 = c.getPreferredSize().height;
        int h3 = c.getMaximumSize().height;
        c.setMinimumSize(new Dimension(w, h1));
        c.setPreferredSize(new Dimension(w, h2));
        c.setMaximumSize(new Dimension(w, h3));
    }

    private static Random rand = null;

    public static Color randomBrown() {
        if(rand == null) rand = new Random();
        return new Color(rand.nextInt(200), rand.nextInt(120), rand.nextInt(20));
    }


    //<editor-fold defaultstate="collapsed" desc=" Colors ">
    private static HashMap<Integer,Color> colors = new HashMap<Integer,Color>();
    /**
     *
     * @param r From 0 (black) to 15 (white).
     * @param g From 0 (black) to 15 (white).
     * @param b From 0 (black) to 15 (white).
     * @return
     */
    public static Color color(int r, int g, int b) {
        int hash = r*17*17+g*17+b;
        if(!colors.containsKey(hash))
            colors.put(hash,new Color(r*17,g*17,b*17) );
        return colors.get(hash);
    }
    //wartosci kanalow: 0..15
    //a: 0 - przezroczysty, 15 - nieprzezroczysty
    public static Color color(int r, int g, int b, int a) {
        int hash = r*17*17+g*17+b+17*17*17*a;
        if(!colors.containsKey(hash))
            colors.put(hash,new Color(r*17,g*17,b*17,a*17) );
        return colors.get(hash);
    }
    public static Color errorColor = color(15,3,0);


    public static Color anyColor = color(2,10,0);
    //public static Color anyOtherColor = color(10,6,0);

    public static Color color(String code) {
        char rc = code.charAt(1);
        char gc = code.charAt(2);
        char bc = code.charAt(3);
        int r,g,b;
        if(rc >= 'a') r = 10 + (rc - 'a');
        else r = rc-'0';
        if(gc >= 'a') g = 10 + (gc - 'a');
        else g = gc-'0';
        if(bc >= 'a') b = 10 + (bc - 'a');
        else b = bc-'0';
        return color(r,g,b);
    }

    private static TypesMap localTypesMap = null;
    
    public static Color typeColor(String className)
    {
       try
       {
           if(VisNow.get() == null) {
               if(localTypesMap != null)
                   return localTypesMap.getStyle(className).getColor();
               
                String typesXmlPath = "src" + File.separator + "types.xml";
                File typesXmlFile = new File(typesXmlPath);
                InputStream is = new FileInputStream(typesXmlFile);
                localTypesMap = JarLibReader.readTypesMap(is);
                is.close();
                return localTypesMap.getStyle(className).getColor();
           } else           
                return VisNow.get().getMainTypes().getTypeStyle(className).getColor();
       }
       catch (Exception e)
       {
          System.err.println("Could not find color for a port. Add port type to the types.xml file.");
          return color(3,3,3);
       }
    }
    //</editor-fold>

    public final static Color STATE_PASSIVE     = color(15,15,15);
    public final static Color STATE_NOTIFYING   = color(6,6,6);
    public final static Color STATE_READY       = color(8,12,15);
    public final static Color STATE_ACTIVE      = color(15,15,0);
    public final static Color STATE_PROGRESS    = color(8,12,0);
    public final static Color STATE_PROPAGATING = color(0,0,12);

    public final static Color SATURATION_NOTLINKED = color(0,0,13);
    public final static Color SATURATION_NODATA    = color(13,11,0);
    public final static Color SATURATION_WRONGDATA = color(13,0,0);
    public final static Color SATURATION_OK        = color(15,15,15);




    //<editor-fold defaultstate="collapsed" desc=" Rendering hints ">
    private static HashMap<Key,Object> hints = null;

    public static HashMap getHints() {
        if(hints == null) {
            hints = new HashMap<Key,Object>();
            hints.put(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        }
        return hints;
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Data ">
    public static String currentTime() {
        Calendar calendar = Calendar.getInstance();
        String ret = "";
        ret += calendar.get(Calendar.HOUR_OF_DAY);
        ret += ":";
        ret += calendar.get(Calendar.MINUTE);
        ret += ":";
        ret += calendar.get(Calendar.SECOND);
        return ret;
    }
    //</editor-fold>

   private VNSwingUtils()
   {
   }

}
