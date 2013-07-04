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

package pl.edu.icm.visnow.gui.widgets.UnboundedRoller;
/*
 * BasicUnboundedIntRollerUI.java
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

public class BasicUnboundedIntRollerUI extends UnboundedIntRollerUI 
        implements MouseListener, MouseMotionListener, MouseWheelListener
{
   int rHeight;
   Insets d;
   boolean valueAdj  = false;
   Graphics g;
   JComponent c;
   int width=40, height=12;
   
   int startPos;
   int startVal = 0;
   int lastVal = 0;
   int lastX, lastY;
   float scale;
   private UnboundedIntRoller roller;
   private ImageIcon[] imgs = new ImageIcon[12];
   
   /** Creates a new instance of BasicSubRangeSliderUI */
   public BasicUnboundedIntRollerUI()
   {
      for (int i = 0; i < imgs.length; i++)
         imgs[i] = new javax.swing.ImageIcon(getClass().getResource("/pl/edu/icm/visnow/gui/icons/roller/"+i+".jpg"));
   }
   
   public static ComponentUI createUI(JComponent c)
   {
      return new BasicUnboundedIntRollerUI();
   }
   
   @Override
   public void installUI(JComponent c)
   {
      roller = (UnboundedIntRoller)c;
      roller.addMouseListener(this);
      roller.addMouseMotionListener(this);
   }
   
   @Override
   public void uninstallUI(JComponent c)
   {
      roller = (UnboundedIntRoller)c;
      roller.removeMouseListener(this);
      roller.removeMouseMotionListener(this);
   }
   
   @Override
   public void paint(Graphics g, JComponent c)
   {
      //  We don't want to paint inside the insets or borders.
      this.g = g;
      this.c = c;
      d = c.getInsets();
      width  = c.getWidth() - d.left - d.right - 12;
      height = c.getHeight() - d.top - d.bottom;
      g.setColor(c.getBackground());
      g.fillRect(0, 0, c.getWidth(), c.getHeight());
      g.translate(d.left, d.top);
      g.setColor(Color.DARK_GRAY);
      g.drawRect(0, 0, width, height);
      g.setColor(Color.GRAY);
      g.fillRect(2, 2 , width-4, height-3);
      g.setColor(Color.LIGHT_GRAY);
      g.clipRect(1,  1, width-2, height-1);
      g.setColor(Color.LIGHT_GRAY);
      g.clipRect(1,  1, width-2, height-1);
      int i = (13 * roller.getValue());
      i = Math.max(0,i%12);
      g.drawImage(imgs[i].getImage(), 0, 0, width, height,0,0,319,31,null);
   }
   
   public void mouseClicked(MouseEvent e)
   {
      if (e.getButton() == MouseEvent.BUTTON1)
         roller.setSensitivity(Math.max(1,roller.getSensitivity()-1));
      else 
         roller.setSensitivity(roller.getSensitivity()+1);
   }
   
   public void mouseDragged(MouseEvent e)
   {
      int x = e.getX();
      lastVal += (e.getX()-lastX)*roller.getSensitivity();
      roller.setValue(lastVal);
      lastX = x;
      c.repaint();
   }
   
   public void mouseEntered(MouseEvent e)
   {
   }
   
   public void mouseExited(MouseEvent e)
   {
   }
   
   public void mouseMoved(MouseEvent e)
   {
   }
   
   public void mousePressed(MouseEvent e)
   {
      roller.setAdjusting(true);
      lastX = startPos = e.getX();
      lastY = e.getY();
      lastVal = startVal = roller.getValue();

   }
   
   public void mouseReleased(MouseEvent e)
   {
      roller.setAdjusting(false);
   }

   public void mouseWheelMoved(MouseWheelEvent e)
   {
      lastVal += (e.getWheelRotation())*roller.getSensitivity();
      roller.setValue(lastVal);
   }}
