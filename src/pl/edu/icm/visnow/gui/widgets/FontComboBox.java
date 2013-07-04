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

package pl.edu.icm.visnow.gui.widgets;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

/**
 *
 * @author know
 */
public class FontComboBox extends JComboBox
{
   private int size = 12;
   private Color foreground = Color.BLACK;
   private Color background = Color.LIGHT_GRAY;
   private List<String> fontNames;

   private class FontComboBoxRenderer extends BasicComboBoxRenderer
   {

      @Override
      public Component getListCellRendererComponent(
              JList list,
              Object value,
              int index,
              boolean isSelected,
              boolean cellHasFocus)
      {
         JLabel renderer = new JLabel();
         renderer.setForeground(foreground);
         renderer.setBackground(background);
         if (value instanceof String)
         {
            renderer.setFont(new Font((String)value, Font.PLAIN, size));
            renderer.setText((String)value);
            renderer.setMaximumSize(new Dimension(150,size + 4));
            renderer.setMinimumSize(new Dimension(150,size + 4));
            renderer.setPreferredSize(new Dimension(150,size + 4));
         } else
            renderer.setText("");
         return renderer;
      }
   }

   public FontComboBox()
   {
      String stdChars = "0123456789!@#$%^&*()-_=+qwertyuiop[]QWERTYUIOP{}asdfghjkl;'ASDFGHJKL:|`zxcvbnm,./~ZXCVBNM<>?";
      GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
      Font[] tmpFonts = ge.getAllFonts();
      List<String> tmpFontNames = new ArrayList<String>();
      fontNames = new ArrayList<String>();
      for (int i = 0; i < tmpFonts.length; i++)
      {
         boolean proper = true;
         for (int j = 0; j < stdChars.length(); j++)
            if (!tmpFonts[i].canDisplay(stdChars.charAt(j)))
            {
               proper = false;
               break;
            }
         if (proper)
            tmpFontNames.add(tmpFonts[i].getName());
      }
      Set<String> set = new HashSet<String>(); 
      fontNames.add("Dialog");
      for (String fontName : tmpFontNames)
      {
         if (!fontName.endsWith("10") && set.add(fontName))
            fontNames.add(fontName);
      }
      for (String fontName : fontNames)
         addItem(fontName);
      setRenderer(new FontComboBoxRenderer());
   }
   
   @Override
   public void setFont(Font font)
   {
      super.setFont(font);
      size = (int)(1.6 * font.getSize());
   }
   
}
