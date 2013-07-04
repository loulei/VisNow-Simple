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

package pl.edu.icm.visnow.lib.basic.mappers.Isolines;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.datasets.IrregularField;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.geometries.parameters.AbstractDataMappingParams;
import pl.edu.icm.visnow.geometries.parameters.ColorComponentParams;
import pl.edu.icm.visnow.lib.templates.visualization.guis.VariablePresentation;
import pl.edu.icm.visnow.lib.types.VNField;
import pl.edu.icm.visnow.gui.events.BooleanChangeListener;
import pl.edu.icm.visnow.gui.events.BooleanEvent;

/**
 *
 * @author Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class IsolinesGUI extends JPanel implements VariablePresentation
{

   private Field inField = null;
   private IsolinesParams params = new IsolinesParams();
   private AbstractDataMappingParams dataMappingParams = null;
   private ColorComponentParams map0Params = null;
   private boolean[] aCS = null;
   private float vMin = 0, vMax = 255;
   private float physMin = 0, physMax = 255;
   private boolean syncing = false;
   private BooleanChangeListener presentationListener = new BooleanChangeListener()  
   {        
      @Override
      public void booleanChanged(BooleanEvent e)
      {
         setPresentation(e.getState());
      }
      @Override
      public void stateChanged(ChangeEvent e)
      {
      }
   };


   /**
    * Creates new form IsolinesUI
    */
   public IsolinesGUI()
   {
      initComponents();
      isoComponentSelector.setTitle("isoline component");
      thresholdsEditor.setMaxRangeCount(200);
      thresholdsEditor.setStartSingle(false);
      thresholdsEditor.setValuePreferred(false);
      thresholdsEditor.addChangeListener(new ChangeListener()
      {
         public void stateChanged(ChangeEvent e)
         {
            params.setThresholds(thresholdsEditor.getThresholds());
         }
      });
   }

   public void setPresentation(boolean simple)
   {
      GridBagConstraints gridBagConstraints;
      Dimension simpleDim = new Dimension(200, 128);
      Dimension expertDim = new Dimension(220, 668);
      if (simple)
      {
         jPanel2.remove(cellSetScrollPane);
         jPanel2.remove(dataComponentList);
         jPanel2.remove(jLabel1);
         thresholdsEditor.setPresentation(simple);
         jPanel2.setMinimumSize(simpleDim);
         jPanel2.setPreferredSize(simpleDim);
         jPanel2.setMaximumSize(simpleDim);
         gridBagConstraints = new java.awt.GridBagConstraints();
         gridBagConstraints.gridx = 0;
         gridBagConstraints.gridy = 0;
         gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
         gridBagConstraints.weightx = 1.0;
      } else
      {
         jPanel2.setMinimumSize(expertDim);
         jPanel2.setPreferredSize(expertDim);
         jPanel2.setMaximumSize(expertDim);
         thresholdsEditor.setPresentation(simple);

         gridBagConstraints = new java.awt.GridBagConstraints();
         gridBagConstraints.gridx = 0;
         gridBagConstraints.gridy = 1;
         gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
         gridBagConstraints.weightx = 1.0;
         jPanel2.add(cellSetScrollPane, gridBagConstraints);

         gridBagConstraints = new java.awt.GridBagConstraints();
         gridBagConstraints.gridx = 0;
         gridBagConstraints.gridy = 3;
         gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
         gridBagConstraints.insets = new java.awt.Insets(6, 0, 3, 0);
         jPanel2.add(jLabel1, gridBagConstraints);

         gridBagConstraints = new java.awt.GridBagConstraints();
         gridBagConstraints.gridx = 0;
         gridBagConstraints.gridy = 4;
         gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
         gridBagConstraints.insets = new java.awt.Insets(6, 0, 3, 0);
         jPanel2.add(dataComponentList, gridBagConstraints);
      }
      validate();
   }

   /**
    * This method is called from within the constructor to initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is always
    * regenerated by the Form Editor.
    */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel4 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        isoComponentSelector = new pl.edu.icm.visnow.lib.gui.DataComponentSelector();
        cellSetScrollPane = new javax.swing.JScrollPane();
        cellSetList = new javax.swing.JList();
        thresholdsEditor = new pl.edu.icm.visnow.lib.gui.FloatArrayEditor();
        dataComponentList = new pl.edu.icm.visnow.lib.gui.DataComponentList();
        jLabel1 = new javax.swing.JLabel();

        setMinimumSize(new java.awt.Dimension(180, 600));
        setPreferredSize(new java.awt.Dimension(230, 650));
        setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jPanel4, gridBagConstraints);

        jPanel2.setMinimumSize(new java.awt.Dimension(190, 635));
        jPanel2.setPreferredSize(new java.awt.Dimension(210, 636));
        jPanel2.setLayout(new java.awt.GridBagLayout());

        isoComponentSelector.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        isoComponentSelector.setMinimumSize(new java.awt.Dimension(100, 36));
        isoComponentSelector.setOpaque(true);
        isoComponentSelector.setPreferredSize(new java.awt.Dimension(200, 36));
        isoComponentSelector.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                isoComponentSelectorStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel2.add(isoComponentSelector, gridBagConstraints);

        cellSetScrollPane.setMinimumSize(new java.awt.Dimension(160, 60));
        cellSetScrollPane.setPreferredSize(new java.awt.Dimension(200, 100));
        cellSetScrollPane.setRequestFocusEnabled(false);

        cellSetList.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        cellSetList.setEnabled(false);
        cellSetList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                cellSetListValueChanged(evt);
            }
        });
        cellSetScrollPane.setViewportView(cellSetList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel2.add(cellSetScrollPane, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel2.add(thresholdsEditor, gridBagConstraints);

        dataComponentList.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        dataComponentList.setMinimumSize(new java.awt.Dimension(100, 150));
        dataComponentList.setPreferredSize(new java.awt.Dimension(200, 150));
        dataComponentList.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                dataComponentListStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel2.add(dataComponentList, gridBagConstraints);

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel1.setText("interpolate components to isolines");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 3, 0);
        jPanel2.add(jLabel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        add(jPanel2, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

   private void cellSetListValueChanged(javax.swing.event.ListSelectionEvent evt)//GEN-FIRST:event_cellSetListValueChanged
   {//GEN-HEADEREND:event_cellSetListValueChanged
      if (aCS != null)
         for (int i = 0; i < aCS.length; i++)
            aCS[i] = cellSetList.isSelectedIndex(i);
      params.setActiveCellSets(aCS);
   }//GEN-LAST:event_cellSetListValueChanged

private void isoComponentSelectorStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_isoComponentSelectorStateChanged
   if (syncing)
      return;
   syncing = true;
   boolean oldActive = params.isActive();
   params.setActive(false);
   int k = isoComponentSelector.getComponent();
   params.setComponent(k);
   if (k != -1)
      setDataComponent(k);
   dataComponentList.setComponent(k);
   params.setActive(oldActive);
   syncing = false;
}//GEN-LAST:event_isoComponentSelectorStateChanged

   private void dataComponentListStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_dataComponentListStateChanged
   {//GEN-HEADEREND:event_dataComponentListStateChanged
      params.setMappedComponents(dataComponentList.getComponentSelections());
   }//GEN-LAST:event_dataComponentListStateChanged

   private void setDataComponent(int k)
   {
      vMin = inField.getData(k).getMinv();
      vMax = inField.getData(k).getMaxv();
      physMin = inField.getData(k).getPhysMin();
      physMax = inField.getData(k).getPhysMax();
      thresholdsEditor.setMinMax(vMin, vMax, physMin, physMax);
   }

   /**
    * Setter for property inField.
    *
    * @param inField New value of property inField.
    */
   public void setInField(VNField in)
   {
      params.setActive(false);
      Field inFld = in.getField();
      if (inFld instanceof RegularField
              && (((RegularField) inFld).getDims() == null
              || ((RegularField) inFld).getDims().length != 2))
         return;

      if (inField == null || !inField.isDataCompatibleWith(inFld))
      {
         inField = inFld;
         isoComponentSelector.setDataSchema(inFld.getSchema());
         int k = -1;
         for (int i = 0; i < inField.getNData(); i++)
            if (inField.getData(i).isSimpleNumeric() && inField.getData(i).getVeclen() == 1)
            {
               k = i;
               break;
            }
         isoComponentSelector.setSelectedIndex(0);
      }
      if (inField instanceof IrregularField)
      {
         String[] cellSetNames = new String[((IrregularField) inField).getNCellSets()];
         for (int i = 0; i < cellSetNames.length; i++)
            cellSetNames[i] = ((IrregularField) inField).getCellSet(i).getName();
         cellSetList.setListData(cellSetNames);
         cellSetList.setEnabled(true);
         aCS = new boolean[((IrregularField) inField).getNCellSets()];
         for (int i = 0; i < aCS.length; i++)
            aCS[i] = true;
         params.setActiveCellSets(aCS);
      } else
         cellSetList.setEnabled(false);
      dataComponentList.setInField(inField);
      setDataComponent(isoComponentSelector.getComponent());
      params.setActive(true);
   }

   public void setParams(IsolinesParams params)
   {
      this.params = params;
   }

   public BooleanChangeListener getPresentationListener()
   {
      return presentationListener;
   }   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JList cellSetList;
    public javax.swing.JScrollPane cellSetScrollPane;
    public pl.edu.icm.visnow.lib.gui.DataComponentList dataComponentList;
    public pl.edu.icm.visnow.lib.gui.DataComponentSelector isoComponentSelector;
    public javax.swing.JLabel jLabel1;
    public javax.swing.JPanel jPanel2;
    public javax.swing.JPanel jPanel4;
    public pl.edu.icm.visnow.lib.gui.FloatArrayEditor thresholdsEditor;
    // End of variables declaration//GEN-END:variables
}
