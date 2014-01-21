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

package pl.edu.icm.visnow.system.config;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 *
 * @author Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class FavoriteFolder {


    //<editor-fold defaultstate="collapsed" desc=" [FSVAR] Icons ">
    //public final static int MIN_PUBLIC_ICON = 2;
    public final static int HOME_ICON = 0;
    public final static int TEMPLATES_ICON = 1;
    
    public final static Icon[] ICONS = new Icon[]{
        new ImageIcon(FavoriteFolder.class.getResource("/pl/edu/icm/visnow/gui/icons/folders/home.png")),
        new ImageIcon(FavoriteFolder.class.getResource("/pl/edu/icm/visnow/gui/icons/folders/template.png")),
        new ImageIcon(FavoriteFolder.class.getResource("/pl/edu/icm/visnow/gui/icons/folders/gray.png")),
        new ImageIcon(FavoriteFolder.class.getResource("/pl/edu/icm/visnow/gui/icons/folders/silver.png")),
        new ImageIcon(FavoriteFolder.class.getResource("/pl/edu/icm/visnow/gui/icons/folders/yellow.png")),
        new ImageIcon(FavoriteFolder.class.getResource("/pl/edu/icm/visnow/gui/icons/folders/orange.png")),
        new ImageIcon(FavoriteFolder.class.getResource("/pl/edu/icm/visnow/gui/icons/folders/red.png")),
        new ImageIcon(FavoriteFolder.class.getResource("/pl/edu/icm/visnow/gui/icons/folders/maroon.png")),

    };
    //</editor-fold>




    private String path;
    private String name;
    private int iconId;


    public FavoriteFolder(String name, String path) {
        this(name, path, 0);
    }

    public FavoriteFolder(String name, String path, int icon) {
        this.name = name;
        this.path = path;
        this.iconId = icon;
    }


    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }


    public int getIconId() {
        return iconId;
    }

    /**
     * @return the icon
     */
    public Icon getIcon() {
        return ICONS[iconId];
    }

    /**
     * @param icon the icon to set
     */
    public void setIconId(int iconId) {
        this.iconId = iconId;
    }
    


}
