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

package pl.edu.icm.visnow.lib.gui;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pl.edu.icm.visnow.datasets.DataSchema;
import pl.edu.icm.visnow.datasets.dataarrays.DataArraySchema;

/**
 *
 * @author  Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class DataComponentSelector extends javax.swing.JPanel
{
   protected DataSchema dataSchema = null;
   protected boolean scalarComponentsOnly = false;
   protected boolean vectorComponentsOnly = false;
   protected boolean addNullComponent = false;
   protected boolean numericComponentsOnly = true;
   protected int nScalarComps = 0;
   protected int nVectorComps = 0;
   protected int nNumericComps = 0;
   protected int nItems = 0;
   protected int nComps = 0;
   protected Vector<String> compNames = new Vector<String>();
   protected int[] compIndices;
   protected boolean startNull = false;
   protected boolean active = true;
   protected String[] extraNames = null;
   protected int[] extraIndices;

   /** Creates new form DataComponentSelector */
   public DataComponentSelector()
   {
      initComponents();
      String title = ((TitledBorder) (cmpComboBox.getBorder())).getTitle();
      if(title == null || title.equals("")) {
          setMinimumSize(new Dimension(82, 32));
          setPreferredSize(new Dimension(182, 32));
      } else {
          setMinimumSize(new Dimension(82, 48));
          setPreferredSize(new Dimension(182, 48));
      }
   }

   /** This method is called from within the constructor to
    * initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is
    * always regenerated by the Form Editor.
    */
   // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
   private void initComponents()
   {

      cmpComboBox = new pl.edu.icm.visnow.gui.widgets.SteppedComboBox();

      setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
      setMaximumSize(new java.awt.Dimension(220, 36));
      setMinimumSize(new java.awt.Dimension(82, 36));
      setOpaque(false);
      setPreferredSize(new java.awt.Dimension(182, 36));
      setLayout(new java.awt.BorderLayout());

      cmpComboBox.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
      cmpComboBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
      cmpComboBox.setMaximumSize(new java.awt.Dimension(32767, 36));
      cmpComboBox.setMinimumSize(new java.awt.Dimension(80, 36));
      cmpComboBox.setPopupWidth(10);
      cmpComboBox.setPreferredSize(new java.awt.Dimension(180, 36));
      cmpComboBox.addItemListener(new java.awt.event.ItemListener()
      {
         public void itemStateChanged(java.awt.event.ItemEvent evt)
         {
            cmpComboBoxItemStateChanged(evt);
         }
      });
      add(cmpComboBox, java.awt.BorderLayout.CENTER);
   }// </editor-fold>//GEN-END:initComponents

   private void cmpComboBoxItemStateChanged(java.awt.event.ItemEvent evt)//GEN-FIRST:event_cmpComboBoxItemStateChanged
   {//GEN-HEADEREND:event_cmpComboBoxItemStateChanged
      if (active)
         fireStateChanged();
   }//GEN-LAST:event_cmpComboBoxItemStateChanged

   public void setTitle(String title)
   {
      if(title == null || title.equals("")) {
          setMinimumSize(new Dimension(82, 32));
          setPreferredSize(new Dimension(182, 32));
      } else {
          setMinimumSize(new Dimension(82, 48));
          setPreferredSize(new Dimension(182, 48));
      }
      ((TitledBorder) (cmpComboBox.getBorder())).setTitle(title);
   }

   protected void updateComponentNames(DataSchema schema)
   {
      active = false;
      if (schema == null)
      {
         nComps = 0;
         cmpComboBox.setModel(new javax.swing.DefaultComboBoxModel());
         return;
      }
      Vector<DataArraySchema> components = schema.getComponentSchemas();
      nComps = components.size();
      compNames.clear();
      nItems = 0;
      if (extraNames != null && extraIndices != null && extraIndices.length == extraNames.length)
         compIndices = new int[nComps + extraIndices.length + 1];
      else
         compIndices = new int[nComps + 1];
      if (scalarComponentsOnly)
      {
         nScalarComps = 0;
         for (int i = 0; i < nComps; i++)
            if (components.get(i).getVeclen() == 1 && (!numericComponentsOnly || (numericComponentsOnly && components.get(i).isSimpleNumeric())))
            {
               compNames.add(components.get(i).getName());
               compIndices[nScalarComps] = i;
               nScalarComps++;
               nItems++;
            }
      } else if (vectorComponentsOnly)
      {
         nVectorComps = 0;
         for (int i = 0; i < nComps; i++)
            if (components.get(i).getVeclen() > 1 && (!numericComponentsOnly || (numericComponentsOnly && components.get(i).isSimpleNumeric())))
            {
               compNames.add(components.get(i).getName());
               compIndices[nVectorComps] = i;
               nVectorComps++;
               nItems++;
            }
      } else if (numericComponentsOnly)
      {
         nNumericComps = 0;
         for (int i = 0; i < nComps; i++)
            if (components.get(i).isSimpleNumeric()) {
               compNames.add(components.get(i).getName());
               compIndices[nNumericComps] = i;
               nNumericComps++;
               nItems++;
            }
      } else
      {
         for (int i = 0; i < nComps; i++) {
                compNames.add(components.get(i).getName());
                compIndices[i] = i;
         }
         nItems = nComps;
      }
      if (extraNames != null && extraIndices != null && extraIndices.length == extraNames.length)
         for (int i = 0; i < extraIndices.length; i++, nItems++)
         {
            compNames.add(extraNames[i]);
            compIndices[nItems] = extraIndices[i];
         }
      if (addNullComponent)
      {
         compNames.add("null");
         compIndices[nItems] = -1;
      }
      cmpComboBox.setModel(new javax.swing.DefaultComboBoxModel(compNames));
//      System.out.println("startNull="+startNull);
      if (compNames.isEmpty() || startNull)
         cmpComboBox.setSelectedIndex(cmpComboBox.getItemCount() - 1);
      else
         cmpComboBox.setSelectedIndex(0);
      active = true;
      fireStateChanged();
   }

   public void setScalarComponentsOnly(boolean scalarComponentsOnly)
   {
      if (dataSchema != null && scalarComponentsOnly != this.scalarComponentsOnly)
         updateComponentNames(dataSchema);
      this.scalarComponentsOnly = scalarComponentsOnly;
   }

   public void setAddNullComponent(boolean addNullComponent)
   {
      if (dataSchema != null && addNullComponent != this.addNullComponent)
         updateComponentNames(dataSchema);
      this.addNullComponent = addNullComponent;
   }

   public void setVectorComponentsOnly(boolean vectorComponentsOnly)
   {
      if (dataSchema != null && vectorComponentsOnly != this.vectorComponentsOnly)
         updateComponentNames(dataSchema);
      this.vectorComponentsOnly = vectorComponentsOnly;
   }

   public void setDataSchema(DataSchema schema)
   {
      if (schema == null || schema.isDataCompatibleWith(dataSchema))
         return;
      updateComponentNames(schema);
      dataSchema = schema;
      if (startNull && addNullComponent)
         cmpComboBox.setSelectedIndex(cmpComboBox.getItemCount() - 1);
      else
      {
         if (cmpComboBox.getModel().getSize() > 0)
            cmpComboBox.setSelectedIndex(0);
         else
            cmpComboBox.setSelectedIndex(-1);
      }
   }

   public void setSelectedIndex(int n)
   {
      cmpComboBox.setSelectedIndex(n);
   }

   public boolean isNullSelected()
   {
      return (addNullComponent && cmpComboBox.getSelectedIndex() == compNames.size() - 1);
   }

    public boolean isNumericComponentsOnly() {
        return numericComponentsOnly;
    }

    public void setNumericComponentsOnly(boolean numericComponentsOnly) {
        this.numericComponentsOnly = numericComponentsOnly;
    }

   public int getComponent()
   {
      if (isNullSelected())
         return -1;
      int k = cmpComboBox.getSelectedIndex();
      if (k < 0 || k >= compIndices.length)
         return -1;
      return compIndices[k];
   }

   public void setComponent(int k)
   {
      if (k < 0 && addNullComponent)
      {
         cmpComboBox.setSelectedIndex(cmpComboBox.getItemCount() - 1);
         return;
      }

      if (k < 0 || k >= nComps)
         return;
      for (int i = 0; i < compIndices.length; i++)
      {
         if (compIndices[i] == k)
         {
            cmpComboBox.setSelectedIndex(i);
            break;
         }
      }
   }

   public int getnItems()
   {
      return nItems;
   }

   public int getnScalarComps()
   {
      return nScalarComps;
   }

   public int getnVectorComps()
   {
      return nVectorComps;
   }

   public int getnNumericComps()
   {
      return nNumericComps;
   }
   
   public void addExtraItems(String[] names, int[] ind)
   {
      extraNames = names;
      extraIndices = ind;
   }
   // Variables declaration - do not modify//GEN-BEGIN:variables
   private pl.edu.icm.visnow.gui.widgets.SteppedComboBox cmpComboBox;
   // End of variables declaration//GEN-END:variables
   /**
    * Utility field holding list of ChangeListeners.
    */
   protected transient ArrayList<ChangeListener> changeListenerList = new ArrayList<ChangeListener>();

   /**
    * Registers ChangeListener to receive events.
    * @param listener The listener to register.
    */
   public synchronized void addChangeListener(ChangeListener listener)
   {
      changeListenerList.add(listener);
   }

   /**
    * Removes ChangeListener from the list of listeners.
    * @param listener The listener to remove.
    */
   public synchronized void removeChangeListener(ChangeListener listener)
   {
      changeListenerList.remove(listener);
   }

   /**
    * Notifies all registered listeners about the event.
    *
    * @param object Parameter #1 of the <CODE>ChangeEvent<CODE> constructor.
    */
   protected void fireStateChanged()
   {
      ChangeEvent e = new ChangeEvent(this);
      for (ChangeListener listener : changeListenerList)
      {
         listener.stateChanged(e);
      }
   }

   public void setStartNullTransparencyComponent(boolean startNull)
   {
      this.startNull = startNull;
   }

   @Override
   public void setEnabled(boolean enabled)
   {
      cmpComboBox.setEnabled(enabled);
   }
}
