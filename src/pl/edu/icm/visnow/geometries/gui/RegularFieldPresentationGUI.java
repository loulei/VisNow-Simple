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

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.geometries.objects.SignalingTransform3D;
import pl.edu.icm.visnow.geometries.parameters.AbstractRenderingParams;
import pl.edu.icm.visnow.geometries.parameters.DataMappingParams;
import pl.edu.icm.visnow.geometries.parameters.RegularFieldDisplayParams;
import pl.edu.icm.visnow.geometries.parameters.TransformParams;

/**
 *
 * @author Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class RegularFieldPresentationGUI extends javax.swing.JPanel
{

   RegularField inField = null;
   private int nScalarComps = 0;
   private Vector<String> xtScalarCompNames = new Vector<String>();
   private int[] scalarCompIndices;
   private boolean is3D = true;
   private boolean simple = false;
   private RegularFieldDisplayParams params = null;
   private JPanel fillPanel = new JPanel();

   /**
    * Creates new form ColoringGUI
    */
   public RegularFieldPresentationGUI()
   {
      initComponents();
      dataMappingGUI.setStartNullTransparencyComponent(true);
   }

   public void setInField(RegularField inField)
   {
      if (inField == null) 
         return;
      this.inField = inField;
      nScalarComps = 0;
      xtScalarCompNames.clear();
      scalarCompIndices = new int[inField.getNData()];
      for (int i = 0; i < inField.getNData(); i++)
      {
         xtScalarCompNames.add(inField.getData(i).getName());
         scalarCompIndices[nScalarComps] = i;
         nScalarComps++;
      }
      xtScalarCompNames.add("x");
      xtScalarCompNames.add("y");
      xtScalarCompNames.add("z");
      xtScalarCompNames.add("normal x");
      xtScalarCompNames.add("normal y");
      xtScalarCompNames.add("normal z");
      if (inField instanceof RegularField)
      {
         xtScalarCompNames.add("i");
         xtScalarCompNames.add("j");
      }
      if (nScalarComps < 1)
         return;
      //active = true;
      if (params != null)
      {
         dataMappingGUI.setInData(inField, params.getMappingParams());
         displayPropertiesGUI.setRenderingParams(params.getDisplayParams());
         transformPanel.setTransformParams(params.getTransformParams());
         transformPanel.setTransSensitivity(inField.getDiameter() / 500);
         if (inField.getDims().length == 3)
            regularField3DMapPanel.setParams(params.getContent3DParams());
      }
      if (is3D != (inField.getDims().length == 3))
      {
         GridBagConstraints gridBagConstraints;
         is3D = inField.getDims().length == 3;         
         if (is3D)
         {
            if (simple)
            {
               gridBagConstraints = new java.awt.GridBagConstraints();
               gridBagConstraints.gridx = 0;
               gridBagConstraints.gridy = 0;
               gridBagConstraints.weightx = 1.0;
               gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
               gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
               add(regularField3DMapPanel, gridBagConstraints);
            } else
            {
               gridBagConstraints = new java.awt.GridBagConstraints();
               gridBagConstraints.gridx = 0;
               gridBagConstraints.gridy = 0;
               gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
               gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
               gridBagConstraints.weightx = 1.0;
               displayContentPanel.add(regularField3DMapPanel, gridBagConstraints);
               mainPane.insertTab("content", null, displayContentPanel, "3d field display content", 0);
            }
         } else
         {
            if (simple)
               remove(regularField3DMapPanel);
            else
               mainPane.remove(displayContentPanel);
         }
         regularField3DMapPanel.setVisible(is3D);
         displayPropertiesGUI.setIs3D(is3D);
      }
   }

   public void setPresentation(boolean simple)
   {
      GridBagConstraints gridBagConstraints;
      dataMappingGUI.setPresentation(simple);
      displayPropertiesGUI.setPresentation(simple);
      if (simple)
      {
         remove(mainPane);
         displayContentPanel.remove(regularField3DMapPanel);
         mainPane.removeAll();
         setLayout(new java.awt.GridBagLayout());

         if (is3D)
         {
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            add(regularField3DMapPanel, gridBagConstraints);
         }

         gridBagConstraints = new java.awt.GridBagConstraints();
         gridBagConstraints.gridx = 0;
         gridBagConstraints.gridy = 1;
         gridBagConstraints.weightx = 1.0;
         gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
         gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
         add(dataMappingGUI, gridBagConstraints);

         gridBagConstraints = new java.awt.GridBagConstraints();
         gridBagConstraints.gridx = 0;
         gridBagConstraints.gridy = 2;
         gridBagConstraints.weightx = 1.0;
         gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
         gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
         add(displayPropertiesGUI, gridBagConstraints);

         gridBagConstraints = new java.awt.GridBagConstraints();
         gridBagConstraints.gridx = 0;
         gridBagConstraints.gridy = 3;
         gridBagConstraints.weightx = 1.0;
         gridBagConstraints.weighty = 1.0;
         gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
         gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
         add(displayPropertiesGUI, gridBagConstraints);
         
      } else
      {
         remove(dataMappingGUI);
         remove(regularField3DMapPanel);
         remove(displayPropertiesGUI);
         remove(fillPanel);
         setLayout(new java.awt.BorderLayout());
         if (is3D)
         {
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
            gridBagConstraints.weightx = 1.0;
            displayContentPanel.add(regularField3DMapPanel, gridBagConstraints);
            mainPane.addTab("content", displayContentPanel);
         }
         mainPane.addTab("datamap", dataMappingGUI);
         mainPane.addTab("display", displayPropertiesGUI);
         mainPane.addTab("transform", transformPanel);

         add(mainPane, BorderLayout.CENTER);
      }
      revalidate();
   }

   /**
    * This method is called from within the constructor to initialize the form. WARNING: Do NOT
    * modify this code. The content of this method is always regenerated by the Form Editor.
    */
   // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
   private void initComponents()
   {
      java.awt.GridBagConstraints gridBagConstraints;

      mainPane = new javax.swing.JTabbedPane();
      displayContentPanel = new javax.swing.JPanel();
      regularField3DMapPanel = new pl.edu.icm.visnow.geometries.gui.RegularField3DMapPanel();
      jPanel15 = new javax.swing.JPanel();
      dataMappingGUI = new pl.edu.icm.visnow.geometries.gui.DataMappingGUI();
      displayPropertiesGUI = new pl.edu.icm.visnow.geometries.gui.DisplayPropertiesGUI();
      transformPanel = new pl.edu.icm.visnow.geometries.gui.TransformPanel();

      setMinimumSize(new java.awt.Dimension(200, 730));
      setPreferredSize(new java.awt.Dimension(235, 730));
      setRequestFocusEnabled(false);
      setLayout(new java.awt.BorderLayout());

      mainPane.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
      mainPane.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
      mainPane.setMinimumSize(new java.awt.Dimension(200, 620));
      mainPane.setPreferredSize(new java.awt.Dimension(235, 620));

      displayContentPanel.setLayout(new java.awt.GridBagLayout());

      regularField3DMapPanel.setMinimumSize(new java.awt.Dimension(200, 140));
      regularField3DMapPanel.setPreferredSize(new java.awt.Dimension(235, 140));
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
      gridBagConstraints.weightx = 1.0;
      displayContentPanel.add(regularField3DMapPanel, gridBagConstraints);

      javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
      jPanel15.setLayout(jPanel15Layout);
      jPanel15Layout.setHorizontalGroup(
         jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGap(0, 253, Short.MAX_VALUE)
      );
      jPanel15Layout.setVerticalGroup(
         jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGap(0, 1565, Short.MAX_VALUE)
      );

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 1.0;
      displayContentPanel.add(jPanel15, gridBagConstraints);

      mainPane.addTab("content", displayContentPanel);
      mainPane.addTab("datamap", dataMappingGUI);
      mainPane.addTab("display", displayPropertiesGUI);
      mainPane.addTab("transform", transformPanel);

      add(mainPane, java.awt.BorderLayout.CENTER);
   }// </editor-fold>//GEN-END:initComponents

   public RegularField3DMapPanel getRegularField3DMapPanel()
   {
      return regularField3DMapPanel;
   }
   /**
    * Utility field holding list of ChangeListeners.
    */
   private transient ArrayList<ChangeListener> changeListenerList = new ArrayList<ChangeListener>();

   /**
    * Registers ChangeListener to receive events.
    *
    * @param listener The listener to register.
    */
   public synchronized void addChangeListener(ChangeListener listener)
   {
      changeListenerList.add(listener);
   }

   /**
    * Removes ChangeListener from the list of listeners.
    *
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
   public void setDisplayParams(AbstractRenderingParams displayParams)
   {
      displayPropertiesGUI.setRenderingParams(displayParams);
   }

   public void setTransforParams(TransformParams params)
   {
      transformPanel.setTransformParams(params);
   }

   public void setParams(RegularFieldDisplayParams params)
   {
      this.params = params;
      if (inField != null && params != null)
      {
         dataMappingGUI.setInData(inField, params.getMappingParams());
         dataMappingGUI.setRenderingParams(params.getDisplayParams());
         displayPropertiesGUI.setRenderingParams(params.getDisplayParams());
         transformPanel.setTransformParams(params.getTransformParams());
         if (inField.getDims().length == 3)
            regularField3DMapPanel.setParams(params.getContent3DParams());
      }
   }

   public void setInData(RegularField inField, RegularFieldDisplayParams params)
   {
      if (inField == null || params == null)
         return;
      setInField(inField);
      setParams(params);
   }

   public void setSignalingTransform(SignalingTransform3D sigTrans)
   {
      transformPanel.setSigTrans(sigTrans);
   }

   public void setTransSensitivity(float s)
   {
      transformPanel.setTransSensitivity(s);
   }

   public DataMappingParams getMappingParams()
   {
      return params.getMappingParams();
   }
   // Variables declaration - do not modify//GEN-BEGIN:variables
   private pl.edu.icm.visnow.geometries.gui.DataMappingGUI dataMappingGUI;
   private javax.swing.JPanel displayContentPanel;
   private pl.edu.icm.visnow.geometries.gui.DisplayPropertiesGUI displayPropertiesGUI;
   private javax.swing.JPanel jPanel15;
   private javax.swing.JTabbedPane mainPane;
   private pl.edu.icm.visnow.geometries.gui.RegularField3DMapPanel regularField3DMapPanel;
   private pl.edu.icm.visnow.geometries.gui.TransformPanel transformPanel;
   // End of variables declaration//GEN-END:variables

    /**
     * @return the dataMappingGUI
     */
    public pl.edu.icm.visnow.geometries.gui.DataMappingGUI getDataMappingGUI() {
        return dataMappingGUI;
    }
}
