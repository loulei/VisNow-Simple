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

package pl.edu.icm.visnow.gui.widgets.VerticalUnboundedRoller;
/*
 * BasicUnboundedRollerUI.java
 *
 * Created on April 14, 2004, 10:42 AM
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.plaf.*;


/**
 *
 * @author  Krzysztof S. Nowinski, University of Warsaw, ICM
 */

public class BasicUnboundedRollerUI extends UnboundedRollerUI
implements MouseListener, MouseMotionListener, MouseWheelListener
{
   private Insets d;
   private JComponent c;
   private int width=12, height=40;
   
   private float lastVal = 0;
   private int lastY;
   private UnboundedRoller roller;
   private double scaleFactor = 2;
   private ImageIcon[] imgs = new ImageIcon[12];
   
   /** Creates a new instance of BasicSubRangeSliderUI */
   public BasicUnboundedRollerUI()
   {
      for (int i = 0; i < imgs.length; i++)
         imgs[i] = new javax.swing.ImageIcon(getClass().getResource("/pl/edu/icm/visnow/gui/icons/roller/"+i+"v.jpg"));
   }
   
   public static ComponentUI createUI(JComponent c)
   {
      return new BasicUnboundedRollerUI();
   }
   
   @Override
   public void installUI(JComponent c)
   {
      roller = (UnboundedRoller)c;
      roller.addMouseListener(this);
      roller.addMouseMotionListener(this);
   }
   
   @Override
   public void uninstallUI(JComponent c)
   {
      roller = (UnboundedRoller)c;
      roller.removeMouseListener(this);
      roller.removeMouseMotionListener(this);
   }
   
   @Override
   public void paint(Graphics g, JComponent c)
   {
      //  We don't want to paint inside the insets or borders.
      this.c = c;
      d = c.getInsets();
      width  = c.getWidth() - d.left - d.right ;
      height = c.getHeight() - d.top - d.bottom;
      g.setColor(c.getBackground());
      g.fillRect(0, 0, c.getWidth(), c.getHeight());
      g.translate(d.left, d.top);
      g.setColor(Color.DARK_GRAY);
      g.drawRect(0, 0, width, height);
      g.setColor(Color.GRAY);
      g.fillRect(2, 2 , width-4, height-3);
      g.setColor(Color.DARK_GRAY);
      g.setColor(Color.LIGHT_GRAY);
      g.clipRect(1,  1, width-2, height-1);
      int i = (int)(13*roller.getValue());
      int j = Math.abs(i)%12;
      i = i > 0 ? j : (12 - j) % 12;
      i = Math.max(0,i%12);
      g.drawImage(imgs[i].getImage(), 0, 0, width, height,0,0,31,319,null);
      
      if(!roller.isEnabled()) {
          AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.9f);
          ((Graphics2D)g).setComposite(ac);          
          ((Graphics2D)g).setColor(Color.LIGHT_GRAY);
          ((Graphics2D)g).fillRect(0, 0, width, height);
      }
      
   }
   
   @Override
   public void mouseClicked(MouseEvent e)
   {
      if (e.getButton() == MouseEvent.BUTTON1)
         roller.setSensitivity((float)(roller.getSensitivity()/scaleFactor));
      else
         roller.setSensitivity((float)(roller.getSensitivity()*scaleFactor));
   }
   
   @Override
   public void mouseDragged(MouseEvent e)
   {
      int y = e.getY();
      lastVal += (lastY - e.getY())*roller.getSensitivity();
      roller.setValue(lastVal);
      lastY = y;
      c.repaint();
   }
   
   @Override
   public void mouseEntered(MouseEvent e)
   {
   }
   
   @Override
   public void mouseExited(MouseEvent e)
   {
   }
   
   @Override
   public void mouseMoved(MouseEvent e)
   {
   }
   
   @Override
   public void mousePressed(MouseEvent e)
   {
      roller.setAdjusting(true);
      lastY = e.getY();
      lastVal = roller.getValue();
   }
   
   @Override
   public void mouseReleased(MouseEvent e)
   {
      roller.setAdjusting(false);
   }

   @Override
   public void mouseWheelMoved(MouseWheelEvent e)
   {
      lastVal += (e.getWheelRotation())*roller.getSensitivity();
      roller.setValue(lastVal);
      c.repaint();
   }
}
