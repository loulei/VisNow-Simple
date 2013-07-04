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

package pl.edu.icm.visnow.lib.templates.visualization.guis;

import java.awt.CardLayout;
import java.util.ArrayList;
import javax.swing.JPanel;
import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.datasets.IrregularField;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.geometries.parameters.AbstractDataMappingParams;
import pl.edu.icm.visnow.geometries.parameters.IrregularFieldDisplayParams;
import pl.edu.icm.visnow.geometries.parameters.RegularFieldDisplayParams;
import pl.edu.icm.visnow.gui.widgets.MultistateButton;
import pl.edu.icm.visnow.gui.events.BooleanChangeListener;
import pl.edu.icm.visnow.gui.events.BooleanEvent;
import pl.edu.icm.visnow.system.main.VisNow;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class FieldVisualizationGUI extends javax.swing.JPanel
{

   protected Field inField;
   protected CardLayout cL;
   protected AbstractDataMappingParams mappingParams = null;
   protected boolean simpleGUI = true;

   /**
    * Creates new form FieldVisualizationGUI
    */
   public FieldVisualizationGUI()
   {
      initComponents();
      cL = (CardLayout) presentationPanel.getLayout();
      regularGUI.setPresentation(simpleGUI);
      irregularGUI.setPresentation(simpleGUI);
      field1DGUI.setPresentation(simpleGUI);
      guiPresentationButton.setState(VisNow.guiLevel);
      guiPresentationButton.setVisible(VisNow.allowGUISwitch);      
   }

   /**
    * This method is called from within the constructor to initialize the form. WARNING: Do NOT
    * modify this code. The content of this method is always regenerated by the Form Editor.
    */
   @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPane = new javax.swing.JTabbedPane();
        presentationPanel = new javax.swing.JPanel();
        regularGUI = new pl.edu.icm.visnow.geometries.gui.RegularFieldPresentationGUI();
        irregularGUI = new pl.edu.icm.visnow.geometries.gui.IrregularFieldMapperGUI();
        field1DGUI = new pl.edu.icm.visnow.geometries.gui.Presentation1DPanel();
        guiPresentationButton = new MultistateButton(new String[]{"show simple GUI","show expert GUI"}, null);

        setMinimumSize(new java.awt.Dimension(205, 900));
        setPreferredSize(new java.awt.Dimension(240, 900));
        setLayout(new java.awt.BorderLayout());

        mainPane.setName("mainPane"); // NOI18N
        mainPane.setPreferredSize(new java.awt.Dimension(240, 827));

        presentationPanel.setName("presentationPanel"); // NOI18N
        presentationPanel.setPreferredSize(new java.awt.Dimension(235, 800));
        presentationPanel.setLayout(new java.awt.CardLayout());

        regularGUI.setMinimumSize(new java.awt.Dimension(180, 596));
        regularGUI.setName("regularGUI"); // NOI18N
        regularGUI.setPreferredSize(new java.awt.Dimension(200, 600));
        presentationPanel.add(regularGUI, "regular");

        irregularGUI.setName("irregularGUI"); // NOI18N
        presentationPanel.add(irregularGUI, "irregular");

        field1DGUI.setName("field1DGUI"); // NOI18N
        presentationPanel.add(field1DGUI, "1d");

        mainPane.addTab("presentation", presentationPanel);

        add(mainPane, java.awt.BorderLayout.CENTER);

        guiPresentationButton.setBackground(new java.awt.Color(204, 204, 204));
        guiPresentationButton.setForeground(new java.awt.Color(0, 51, 153));
        guiPresentationButton.setText("multistateButton1");
        guiPresentationButton.setMaximumSize(new java.awt.Dimension(54, 24));
        guiPresentationButton.setMinimumSize(new java.awt.Dimension(54, 24));
        guiPresentationButton.setName("guiPresentationButton"); // NOI18N
        guiPresentationButton.setPreferredSize(new java.awt.Dimension(54, 24));
        guiPresentationButton.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                guiPresentationButtonStateChanged(evt);
            }
        });
        add(guiPresentationButton, java.awt.BorderLayout.NORTH);
    }// </editor-fold>//GEN-END:initComponents

   private void guiPresentationButtonStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_guiPresentationButtonStateChanged
   {//GEN-HEADEREND:event_guiPresentationButtonStateChanged
      simpleGUI = guiPresentationButton.getState() == VisNow.SIMPLE_GUI;
      regularGUI.setPresentation(simpleGUI);
      irregularGUI.setPresentation(simpleGUI);
      field1DGUI.setPresentation(simpleGUI);
      fireStateChanged();
   }//GEN-LAST:event_guiPresentationButtonStateChanged

   /**
    * Set regular inField and corresponding params
    *
    * @param inField new value of inField
    * @param RegularFieldDisplayParams to be controlled by regularGUI
    */
   public void setInData(RegularField inField, RegularFieldDisplayParams params)
   {
      this.inField = inField;
      mappingParams = params.getMappingParams();
      regularGUI.setInData(inField, params);
      cL.show(presentationPanel, "regular");
   }

   /**
    * Set regular inField and corresponding params
    *
    * @param inField new value of inField
    * @param RegularFieldDisplayParams to be controlled by regularGUI
    */
   public void setInData(IrregularField inField, IrregularFieldDisplayParams params)
   {
      this.inField = inField;
      mappingParams = params.getMappingParams();
      irregularGUI.setInFieldDisplayData(inField, params);
      cL.show(presentationPanel, "irregular");
   }

   public AbstractDataMappingParams getMappingParams()
   {
      return mappingParams;
   }

   public void addComputeGUI(JPanel gui)
   {
      mainPane.insertTab("computation", null, gui, "", 0);
      mainPane.setSelectedIndex(0);
      if (gui instanceof VariablePresentation)
         addChangeListener(((VariablePresentation) gui).getPresentationListener());
      guiPresentationButton.setState(VisNow.guiLevel);
      guiPresentationButton.setVisible(VisNow.allowGUISwitch);
   }
   /**
    * Utility field holding list of ChangeListeners.
    */
   private transient ArrayList<BooleanChangeListener> changeListenerList = new ArrayList<BooleanChangeListener>();

   /**
    * Registers ChangeListener to receive events.
    *
    * @param listener The listener to register.
    */
   public synchronized void addChangeListener(BooleanChangeListener listener)
   {
      changeListenerList.add(listener);
   }

   /**
    * Removes ChangeListener from the list of listeners.
    *
    * @param listener The listener to remove.
    */
   public synchronized void removeChangeListener(BooleanChangeListener listener)
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
      BooleanEvent e = new BooleanEvent(this, guiPresentationButton.getState() == 1);
      for (BooleanChangeListener listener : changeListenerList)
         listener.booleanChanged(e);
   }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected pl.edu.icm.visnow.geometries.gui.Presentation1DPanel field1DGUI;
    protected pl.edu.icm.visnow.gui.widgets.MultistateButton guiPresentationButton;
    protected pl.edu.icm.visnow.geometries.gui.IrregularFieldMapperGUI irregularGUI;
    protected javax.swing.JTabbedPane mainPane;
    protected javax.swing.JPanel presentationPanel;
    protected pl.edu.icm.visnow.geometries.gui.RegularFieldPresentationGUI regularGUI;
    // End of variables declaration//GEN-END:variables
}