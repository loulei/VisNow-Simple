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

package pl.edu.icm.visnow.geometries.gui;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import javax.swing.JComponent;

/**
 *
  * A wrapper for a Swing component for hardcopy creation.
 * To print a component cmp, add a piece of code like this:
 * <p>
 * <CODE>      PrinterJob pj = PrinterJob.getPrinterJob(); <p>
 *      PageFormat mPageFormat = pj.defaultPage(); <p>
 *      mPageFormat = pj.pageDialog(mPageFormat); <p>
 *      ComponentPrintable cPr = new ComponentPrintable(cmp); <p>
 *      pj.setPrintable(cPr,mPageFormat); <p>
 *      if (pj.printDialog()) <p>
 *      { <p>
 *         try <p>
 *         { <p>
 *            pj.print(); <p>
 *         } <p>
 *         catch (PrinterException e) <p>
 *         { <p>
 *            JOptionPane.showMessageDialog(null,"cannot print window"); <p>
 *         } <p>
 *      } <p>
 * </CODE>
* @author  Krzysztof S. Nowinski (know@icm.edu.pl)
 * Warsaw University
 * Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class ComponentPrintable implements Printable {
   private int width, height;
   private Component mComponent;
   
   /**
    * Creates a new ComponentPrintable for a Component <arg>c</arg>
    */
   public ComponentPrintable(Component c) 
   {
      mComponent = c;
      height = c.getSize().height;
      width  = c.getSize().width;
   }
   
   /**
    * Prints the Component indicated in the constructor.
    * Do not call it explicitely - it is called by the corresponding Printer Job.
    * @return printed page status
    */
   public int print(Graphics g, PageFormat pageFormat, int pagelndex) 
   {
      double s;
      s = 9.*72./width;
      if (s > 6.5*72./height) 
         s = 6.5*72./height;
      if (pagelndex > 0) return NO_SUCH_PAGE;
      Graphics2D g2 = (Graphics2D)g;
      g2.translate(pageFormat.getImageableX(),
                   pageFormat.getImageableY());
      g2.scale(s,s);
      boolean wasBuffered = disableDoubleBuffering(mComponent);
      mComponent.paint(g2) ;
      restoreDoubleBuffering(mComponent, wasBuffered) ;
      return PAGE_EXISTS;
   }
   
   private boolean disableDoubleBuffering(Component c) 
   {
      if (c instanceof JComponent == false) return false;
      JComponent jc = (JComponent)c;
      boolean wasBuffered = jc.isDoubleBuffered() ;
      jc.setDoubleBuffered(false) ;
      return wasBuffered;
   }
   
   private void restoreDoubleBuffering(Component c, 
                                                  boolean wasBuffered) 
   {
      if (c instanceof JComponent)
      ((JComponent)c).setDoubleBuffered(wasBuffered) ;
   }
}
