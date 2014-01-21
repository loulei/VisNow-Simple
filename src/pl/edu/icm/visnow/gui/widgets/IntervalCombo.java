//<editor-fold defaultstate="collapsed" desc=" COPYRIGHT AND LICENSE ">
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




package pl.edu.icm.visnow.gui.widgets;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import pl.edu.icm.visnow.gui.events.FloatValueModificationEvent;
import pl.edu.icm.visnow.gui.events.FloatValueModificationListener;


/**
 * @author Krzysztof S. Nowinski (know@icm.edu.pl) Warsaw University,
 * Interdisciplinary Centre for Mathematical and Computational Modelling
 */

public class IntervalCombo extends javax.swing.JPanel
{
   private float selectedInterval;

   /**
    * Creates new form IntervalCombo
    */
   public IntervalCombo()
   {
      initComponents();
   }

   /**
    * Prepares combo menu list as a series of nice values ranging from r/10^n to r
    * @param r increment limit
    * @param n range of increment values
    * @param initial initial selection  is set at ~r/10^(initial + 1)
   */
   public void setRangeLength(float r, int n, int initial)
   {
      initial = Math.min(n - 1, initial);
      String[] intervalStrings = new String[3 * n];
      int e = (int)(Math.log10(r) + 1000) - 1000;
      if (e < 0)
         for (int i = e; i < 0; i++)
            r *= 10;
      else if (e > 0)
         for (int i = 0; i < e; i++)
            r /= 10;
      int rounded;
      if (r <2)
         rounded = 2;
      else if (r < 5)
         rounded = 1;
      else
         rounded = 0;
      String[] tmpStrings = new String[3 * (n + 1)];
      for (int i = n; i >= 0; i--)
      {
         String pre = "", post = "";
         int exp = e - i;
         if (exp > 2 || exp < -3)
            post = "e"+exp;
         else
            switch (exp)
            {
            case 3:
               post = "000";
               break;
            case 2:
               post = "00";
               break;
            case 1:
               post = "0";
               break;
            case 0:
               break;
            case -1:
               pre = "0.";
               break;
            case -2:
               pre = "0.0";
               break;
            case -3:
               pre = "0.00";
               break;
            }
         tmpStrings[3 * i] = pre+"5"+post;
         tmpStrings[3 * i + 1] = pre+"2"+post;
         tmpStrings[3 * i + 2] = pre+"1"+post;
      }
      System.arraycopy(tmpStrings, rounded, intervalStrings, 0, 3 * n);
      intervalSelectionCombo.setModel(new DefaultComboBoxModel(intervalStrings));
      if (initial == 0)
         intervalSelectionCombo.setSelectedIndex(intervalStrings.length / 2);
      else
         intervalSelectionCombo.setSelectedIndex(initial);
   }

   /**
    * Convenience wrapper prepares combo menu list as a series of nice values ranging from r/10^n to r
    * @param r increment limit
    * @param n range of increment values
    * initial selection is set at ~r/10
   */
   public void setRangeLength(float r, int n)
   {
      setRangeLength(r, n, 0);
   }

   /**
    * Convenience wrapper prepares combo menu list as a series of nice values ranging from r/100 to r
    * @param r increment limit
    * @param n range of increment values
    * initial selection is set at ~r/10
   */
   public void setRangeLength(float r)
   {
      setRangeLength(r, 2);
   }

   /**
    * This method is called from within the constructor to initialize the form. WARNING: Do NOT
    * modify this code. The content of this method is always regenerated by the Form Editor.
    */
   @SuppressWarnings("unchecked")
   // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
   private void initComponents()
   {

      intervalSelectionCombo = new JComboBox();

      setMaximumSize(new Dimension(240, 24));
      setMinimumSize(new Dimension(100, 20));
      setOpaque(false);
      setPreferredSize(new Dimension(120, 22));
      setLayout(new BorderLayout());

      intervalSelectionCombo.setEditable(true);
      intervalSelectionCombo.setFont(new Font("Dialog", 0, 12)); // NOI18N
      intervalSelectionCombo.setModel(new DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
      intervalSelectionCombo.setInheritsPopupMenu(true);
      intervalSelectionCombo.setMaximumSize(new Dimension(200, 60));
      intervalSelectionCombo.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent evt)
         {
            intervalSelectionComboActionPerformed(evt);
         }
      });
      add(intervalSelectionCombo, BorderLayout.CENTER);

      getAccessibleContext().setAccessibleName("");
   }// </editor-fold>//GEN-END:initComponents

   private void intervalSelectionComboActionPerformed(ActionEvent evt)//GEN-FIRST:event_intervalSelectionComboActionPerformed
   {//GEN-HEADEREND:event_intervalSelectionComboActionPerformed
      String s = (String)intervalSelectionCombo.getSelectedItem();
      try
      {
         float v = Float.parseFloat(s);
         selectedInterval = v;
         fireStateChanged();
//         System.out.println(""+v);
      } catch (Exception e)
      {
      }
   }//GEN-LAST:event_intervalSelectionComboActionPerformed

   /**
    * Utility field holding list of ChangeListeners.
    */
   private transient ArrayList<FloatValueModificationListener> changeListenerList =
           new ArrayList<FloatValueModificationListener>();

   /**
    * Registers ChangeListener to receive events.
    * @param listener The listener to register.
    */
   public synchronized void addChangeListener(FloatValueModificationListener listener)
   {
      changeListenerList.add(listener);
   }

   /**
    * Removes ChangeListener from the list of listeners.
    * @param listener The listener to remove.
    */
   public synchronized void removeChangeListener(FloatValueModificationListener listener)
   {
      changeListenerList.remove(listener);
   }

   /**
    * Notifies all registered listeners about the event.
    *
    * @param object Parameter #1 of the <CODE>ChangeEvent<CODE> constructor.
    */
   private void fireStateChanged()
   {
      FloatValueModificationEvent e = new FloatValueModificationEvent(this, selectedInterval, false);
      for (FloatValueModificationListener listener : changeListenerList)
         listener.floatValueChanged(e);
   }

   public float getSelectedInterval()
   {
      return selectedInterval;
   }

   // Variables declaration - do not modify//GEN-BEGIN:variables
   private JComboBox intervalSelectionCombo;
   // End of variables declaration//GEN-END:variables
}
