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
/*
 * MemoryMonitor.java
 *
 * Created on 25 listopad 2003, 17:03
 */

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import pl.edu.icm.visnow.system.main.VisNow;

/**
 *
 * @author axnow
 */
public class MemoryMonitor extends JProgressBar implements Runnable
{

   /**
    * Creates a new instance of MemoryMonitor
    */
   private DecimalFormat format = new DecimalFormat("00.0");
   private long refresh = 500;
   private boolean modeStatic = true;

   public MemoryMonitor()
   {
      super();
      setStringPainted(true);
      setFont(new java.awt.Font("Dialog", 0, 10));
      updateValues();
      new Thread(this, "MemoryMonitor").start();
      this.addMouseListener(new MouseAdapter()
      {
         @Override
         public void mouseClicked(MouseEvent e)
         {
            if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1)
            {
               System.out.println("Garbage collector started...");
               Runtime.getRuntime().gc();
            } else if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON3)
            {
               MemoryMonitor.this.modeStatic = !MemoryMonitor.this.modeStatic;
            }
         }
      });
      format = (DecimalFormat) NumberFormat.getInstance(Locale.US);
      format.applyPattern("00.0%");
      this.setToolTipText("Right-click for maximum mode change static/dynamic. Double-left-click for garbage collection.");

   }

   private void updateValues()
   {
      Runtime r = Runtime.getRuntime();
      long total = r.totalMemory();
      long free = r.freeMemory();
      long used = total - free;
      long max = VisNow.get().getMemoryMax();
      if (modeStatic && max != Long.MAX_VALUE)
      {
         setMaximum(100);
         double ratio = (double) used / (double) max;
         setValue((int) Math.round(100 * ratio));
         String info = "";
         if (max / (1024L * 1024L) < 10)
         {
            info = (used / (1024L)) + "/" + (max / (1024L)) + " kB (" + format.format(getPercentComplete()) + ")";
         } else if (max / (1024L * 1024L * 1024L) < 10)
         {
            info = (used / (1024L * 1024L)) + "/" + (max / (1024L * 1024L)) + " MB (" + format.format(getPercentComplete()) + ")";
         } else
         {
            info = (used / (1024L * 1024L * 1024L)) + "/" + (max / (1024L * 1024L * 1024L)) + " GB (" + format.format(getPercentComplete()) + ")";
         }


         setString("Memory usage: "+info);
      } else
      {
         total = total / 1024L;
         used = used / 1024L;
         setMaximum((int) total);
         setValue((int) used);
         String info = "";
         if (total / 1024L < 10)
         {
            info = (used) + "/" + (total) + " kB (" + format.format(getPercentComplete()) + ")";
         } else if (total / (1024L * 1024L) < 10)
         {
            info = (used / 1024L) + "/" + (total / 1024L) + " MB (" + format.format(getPercentComplete()) + ")";
         } else
         {
            info = (used / (1024L * 1024L)) + "/" + (total / (1024L * 1024L)) + " GB (" + format.format(getPercentComplete()) + ")";
         }

         setString("Memory usage: "+info);
      }

   }

   public void run()
   {
      while (true)
      {
         SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               updateValues();
            }
         });

         try
         {
            Thread.currentThread().sleep(refresh);
         } catch (InterruptedException e)
         {
         }
      }
   }

   public long getRefresh()
   {
      return refresh;
   }

   /**
    * Setter for property refresh.
    *
    * @param refresh New value of property refresh.
    *
    */
   public synchronized void setRefresh(long refresh)
   {
      this.refresh = refresh;
   }
}
