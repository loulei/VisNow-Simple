//<editor-fold defaultstate="collapsed" desc=" License ">

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
//</editor-fold>
package pl.edu.icm.visnow.gui.icons;

import java.awt.Image;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.UIManager;

/**
 * Static loader for UIManager icons. This class provides functionality for loading (and caching) icons in original size and resized.
 *
 * @author szpak
 */
public class UIIconLoader {

    public enum IconType {

        INFO, WARNING, ERROR
    }
    //map with String as a key (IconType.name() + _ + width + _ + height)
    static Map<String, Icon> icons = new HashMap<String, Icon>();

    /**
     * Returns icon for passed
     * <code>iconType</code>. This method cache loaded icons.
     *
     * @return icon or null if such icon cannot be found
     */
    public static Icon getIcon(IconType iconType) {
        if (!icons.containsKey(key(iconType))) {
            String iconKey;
            switch (iconType) {
                case INFO:
                    iconKey = "OptionPane.informationIcon";
                    break;
                case WARNING:
                    iconKey = "OptionPane.warningIcon";
                    break;
                case ERROR:
                    iconKey = "OptionPane.errorIcon";
                    break;
                default:
                    throw new IllegalArgumentException("Icon " + iconType.name() + " is not supported");
            }

            Icon icon = UIManager.getIcon(iconKey);
            //return null if not found
            if (icon == null) return null;
            icons.put(key(iconType), icon);
        }
        return icons.get(key(iconType));
    }

    /**
     * Returns icon for passed
     * <code>iconType</code> and size. This method cache loaded icons.
     *
     * @return icon or null if such icon cannot be found
     */
    public static Icon getIcon(IconType iconType, int width, int height) {
        if (!icons.containsKey(key(iconType, width, height))) {
            Icon icon = getIcon(iconType);
            //return null if icon not found
            if (icon == null) return null;

            Image img = ((ImageIcon) icon).getImage();
            Image newimg = img.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
            icon = new ImageIcon(newimg);
            icons.put(key(iconType, width, height), icon);
        }
        return icons.get(key(iconType, width, height));
    }

    /**
     * Key to get/store icons with specified size.
     */
    private static String key(IconType iconType, int width, int height) {
        return iconType.name() + "_" + width + "_" + height;
    }

    /**
     * Key to get/store icons without specified size.
     */
    private static String key(IconType iconType) {
        return iconType.name();
    }
}
