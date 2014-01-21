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

package pl.edu.icm.visnow.engine.error;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Calendar;
import javax.swing.JPanel;
import pl.edu.icm.visnow.engine.exception.VNException;
import pl.edu.icm.visnow.engine.exception.VNRuntimeException;
import pl.edu.icm.visnow.engine.main.ModuleElement;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;
import pl.edu.icm.visnow.system.main.VisNow;
import pl.edu.icm.visnow.system.swing.JComponentViewer;


/**
 *
 * @author  Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class Displayer
{

   private static void developmentDisplay(long catchercode, Exception ex, Object catcher, String msg)
   {
      System.out.println("*******************");
      System.out.println("*****  ERROR  *****");
      System.out.println("*******************");
      System.out.println("CATCHER:");
      System.out.println("ID     : " + catchercode);
      System.out.println("OBJECT : " + catcher);
      System.out.println("MESSAGE:");
      System.out.println(msg);
      System.out.println("*******************");
      if (ex instanceof VNException)
      {
         VNException ve = (VNException) ex;
         System.out.println("THROWER:");
         System.out.println("ID     : " + ve.getCode());
         System.out.println("OBJECT : " + ve.getThrower());
         System.out.println("THREAD : " + ve.getThread());
         System.out.println("MESSAGE:");
         System.out.println(ve.getMessage());
         System.out.println("*******************");
      }
      System.out.println("STACK:");
      StackTraceElement[] trace = ex.getStackTrace();
      for (StackTraceElement element : trace)
         System.out.println(element);
      System.out.println("*******************");
      if (ex instanceof VNException)
      {
         VNException ve = (VNException) ex;
         if (ve.getDetails() instanceof String)
         {
            System.out.println("DETAILS:");
            System.out.println(ve.getDetails());
         }
         if (ve.getDetails() instanceof Exception)
         {
            System.out.println("PACKED EXCEPTION:");
            System.out.println(ve.getDetails());
            System.out.println("PACKED STACK:");
            ((Exception) ve.getDetails()).printStackTrace();
         }
      }
   }

   private static class OpenErrorFrame implements Runnable
   {

      long catchercode;
      Exception ex;
      Object catcher;
      String msg;      

      public OpenErrorFrame(long catchercode, Exception ex, Object catcher, String msg)
      {
         this.catcher = catcher;
         this.catchercode = catchercode;
         this.ex = ex;
         this.msg = msg;
      }

      public void run()
      {

         ErrorDisplayPanel dp = new ErrorDisplayPanel();
         dp.getTabs().add("catcher", new CatcherPanel(catchercode, catcher, msg));
         dp.getTabs().add("exception", getPanel(ex));
         Throwable h = ex.getCause();
         int i = 1;
         while (h != null)
         {
            String c = "cause" + ((i > 1) ? "^" + i : "");
            dp.getTabs().add(c, getPanel(h));
            h = h.getCause();
            ++i;
         }
         JComponentViewer frame = new JComponentViewer(dp, "Exception", 450, 450, true, false);
         CloseFrameActionListener al = new CloseFrameActionListener(frame, catcher);
         dp.addActionListener(al);
         frame.addWindowListener(al);
         frame.setVisible(true);
      }
   }

   private static void normalDisplay(long catchercode, Exception ex, Object catcher, String msg)
   {
      OpenErrorFrame openErFr = new OpenErrorFrame(catchercode, ex, catcher, msg);
      
      SwingInstancer.swingRunAndWait(openErFr);
   }

   private static JPanel getPanel(Throwable ex)
   {
      if (VNException.class.isAssignableFrom(ex.getClass()))
         return new VNErrorPanel((VNException) ex);
      if (VNRuntimeException.class.isAssignableFrom(ex.getClass()))
         return new VNErrorPanel((VNRuntimeException) ex);
      return new ErrorPanel(ex);
   }

   public static void display(long catcherCode, VNException ex, Object catcher, String msg)
   {
      ddisplay(catcherCode, ex, catcher, msg);
   }

   public static void display(long catcherCode, VNRuntimeException ex, Object catcher, String msg)
   {
      ddisplay(catcherCode, ex, catcher, msg);
   }

   public static void ddisplay(long catcherCode, Exception ex, Object catcher, String msg)
   {
      //if(VisNow.get().isDevelopment())
      //    developmentDisplay(catcherCode, ex, catcher, msg);
      if (VisNow.get().isDevelopment())
         System.out.println("EXCEPTION");
      normalDisplay(catcherCode, ex, catcher, msg);
      //if(VisNow.get().isDevelopment()) {
      //    ex.printStackTrace();
      //    if(ex.getCause() != null)
      //        ex.getCause().printStackTrace();
      //}
   }

   private static String to2String(int i)
   {
      if (i < 10)
         return "0" + i;
      return "" + i;
   }

   private static String to3String(int i)
   {
      if (i < 10)
         return "00" + i;
      if (i < 100)
         return "0" + i;
      return "" + i;
   }

   public static String timestamp()
   {
      String ret = "";
      Calendar cal = Calendar.getInstance();
      ret += to2String(cal.get(Calendar.MINUTE)) + ":";
      ret += to2String(cal.get(Calendar.SECOND)) + ":";
      ret += to3String(cal.get(Calendar.MILLISECOND));
      return ret;
   }

   private Displayer()
   {
   }
}

class CloseFrameActionListener implements ActionListener, WindowListener
{

   private Frame frame;
   private Object catcher;

   public CloseFrameActionListener(Frame frame, Object catcher)
   {
      this.frame = frame;
      this.catcher = catcher;
   }
   
   private void action() {
      frame.setVisible(false);
      if(catcher instanceof ModuleElement) {
          ((ModuleElement)catcher).getModuleBox().getEngine().getApplication().doTheMainReset();
      }       
   }

    @Override
   public void actionPerformed(ActionEvent e)
   {
      action();
   }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
    }

    @Override
    public void windowClosed(WindowEvent e) {
        action();
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }
}
