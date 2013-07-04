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

package pl.edu.icm.visnow.lib.basic.mappers.Streamlines;


import java.awt.CardLayout;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class GUI extends javax.swing.JPanel
{

   private Params params = null;
   private Field inField;
   private float diam = 1, maxVn = 1;
   private Hashtable<Integer, JLabel> downLabels = new Hashtable<Integer, JLabel>();
   private int[] down =
   {
      1, 2, 5, 10, 20, 50, 100, 200, 500, 1000, 2000, 5000, 10000, 20000, 50000, 100000
   };
   private int downIndex = down.length - 1;
   private String[] downTexts =
   {
      "1", "", "", "10", "", "", "100", "", "", "1000", "", "", "1e4", "", "", "1e5"
   };
   private int[] downsize;
   private int downBy = 1;
   private Map<Integer, String> valLabels = new HashMap<Integer, String>();

   /** Creates new form GUI */
   public GUI()
   {
      initComponents();
      for (int i = 0; i < downTexts.length; i++)
      {
         downLabels.put(i, new JLabel(downTexts[i]));
         downLabels.get(i).setFont(new java.awt.Font("Dialog", 0, 8));
      }
      downsizeSlider.setLabelTable(downLabels);
      valLabels.put(-150, "-1000");
      valLabels.put(-100, "-100");
      valLabels.put(-50,  "-10");
      valLabels.put(0,    "0");
      valLabels.put(50,   "10");
      valLabels.put(100,  "100");
      valLabels.put(150,  "1000");
      rangeSlider.setValLabels(valLabels);
      vectorComponentSelector.setTitle("vector component");
      vectorComponentSelector.setVectorComponentsOnly(true);
      regularFieldDownsizeUI.addChangeListener(new ChangeListener()
      {
         @Override
         public void stateChanged(ChangeEvent e)
         {
            if (waitToggle.isSelected())
               return;
            params.setActive(false);
            params.setDown(regularFieldDownsizeUI.getDownsize());
            params.setActive(true);
         }
      });
      for (int i = 0; i < downTexts.length; i++)
      {
         downLabels.put(i, new JLabel(downTexts[i]));
         downLabels.get(i).setFont(new java.awt.Font("Dialog", 0, 8));
      }
      downsizeSlider.setLabelTable(downLabels);
   }

   private void startAction()
   {
      if (params==null || inField == null || waitToggle.isSelected())
         return;
      params.setActive(false);
      params.setStep(stepSlider.getVal());
      params.setVectorComponent(vectorComponentSelector.getComponent());
      params.setNForwardSteps((int)Math.pow(10., Math.max(rangeSlider.getTopValue(), 0) / 50.));
      params.setNBackwardSteps((int)Math.pow(10., Math.max(-rangeSlider.getBottomValue(), 0) / 50.));
      params.setActive(!waitToggle.isSelected());
   }
   
   public void setInField(Field inFld)
   {
      inField = inFld;
      diam = inField.getDiameter();
      SwingInstancer.swingRun(new Runnable()
      {
         public void run()
         {
            vectorComponentSelector.setDataSchema(inField.getSchema());
         }
     });
   }
   
   private void setScaleMinMax()
   {
      float max = 1;
      if (inField.getData(params.getVectorComponent()) != null)
         max = inField.getData(params.getVectorComponent()).getMaxv();
      float[][] ext = inField.getExtents();
      double diam = 0;
      for (int i = 0; i < 3; i++)
         diam += (ext[1][i] - ext[0][i]) * (ext[1][i] - ext[0][i]);
      if (max <= 0)
         max = .001f;
      float s0 = (float) (Math.sqrt(diam / 1000000) / max);
      stepSlider.setAll(s0 / 100, s0, s0 / 10);
      params.setStep(s0);
   }
   
   public void setInPts(Field pts)
   {
      if (pts instanceof RegularField)
      {
         downsize = params.getDown();
         SwingInstancer.swingRun(new Runnable()
         {
            public void run()
            {
               CardLayout cl = (CardLayout)(jPanel5.getLayout());
               downsize = params.getDown();
               regularFieldDownsizeUI.setDownsize(downsize);
               regularFieldDownsizeUI.setVisible(true);
               downsizeSlider.setVisible(false);
               cl.show(jPanel5, "regularFieldDownsizeUI");
           }
         });
      }
      else
      {
         downBy = down[down.length - 1];
          for (int i = 0; i < down.length; i++) 
             if (down[i] >= params.getDownsize())
             {downIndex = i;
                 downBy = down[i];
                 break;
             }
         SwingInstancer.swingRun(new Runnable()
         {
            public void run()
            {
               CardLayout cl = (CardLayout)(jPanel5.getLayout());
               downsizeSlider.setValue(downIndex);
               regularFieldDownsizeUI.setVisible(false);
               downsizeSlider.setVisible(true);
               params.setDownsize(downBy);
               cl.show(jPanel5, "downsizeSlider");
           }
         });
      }  
   }

   /**
    * Get the value of params
    *
    * @return the value of params
    */
   public Params getParams()
   {
      return params;
   }

   /**
    * Set the value of params
    *
    * @param params new value of params
    */
   public void setParams(Params params)
   {
      this.params = params;
   }


   /** This method is called from within the constructor to
    * initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is
    * always regenerated by the Form Editor.
    */
   @SuppressWarnings("unchecked")
   // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
   private void initComponents()
   {
      java.awt.GridBagConstraints gridBagConstraints;

      vectorComponentSelector = new pl.edu.icm.visnow.lib.gui.DataComponentSelector();
      stepSlider = new pl.edu.icm.visnow.gui.widgets.LogarithmicSlider();
      jPanel1 = new javax.swing.JPanel();
      jPanel5 = new javax.swing.JPanel();
      downsizeSlider = new javax.swing.JSlider();
      regularFieldDownsizeUI = new pl.edu.icm.visnow.lib.gui.DownsizeUI();
      rangeSlider = new pl.edu.icm.visnow.gui.widgets.SubRangeSlider.SubRangeSlider();
      waitToggle = new javax.swing.JToggleButton();

      setMinimumSize(new java.awt.Dimension(180, 412));
      setPreferredSize(new java.awt.Dimension(220, 416));
      setLayout(new java.awt.GridBagLayout());

      vectorComponentSelector.setMinimumSize(new java.awt.Dimension(100, 56));
      vectorComponentSelector.setPreferredSize(new java.awt.Dimension(150, 56));
      vectorComponentSelector.addChangeListener(new javax.swing.event.ChangeListener()
      {
         public void stateChanged(javax.swing.event.ChangeEvent evt)
         {
            vectorComponentSelectorStateChanged(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.weightx = 1.0;
      add(vectorComponentSelector, gridBagConstraints);

      stepSlider.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "integration step", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
      stepSlider.setMax(1.0F);
      stepSlider.setMin(1.0E-4F);
      stepSlider.setMinimumSize(new java.awt.Dimension(90, 62));
      stepSlider.setPreferredSize(new java.awt.Dimension(200, 65));
      stepSlider.setVal(0.002F);
      stepSlider.addChangeListener(new javax.swing.event.ChangeListener()
      {
         public void stateChanged(javax.swing.event.ChangeEvent evt)
         {
            stepSliderStateChanged(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 3;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      add(stepSlider, gridBagConstraints);
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 7;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.weighty = 1.0;
      add(jPanel1, gridBagConstraints);

      jPanel5.setMinimumSize(new java.awt.Dimension(10, 65));
      jPanel5.setOpaque(false);
      jPanel5.setLayout(new java.awt.CardLayout());

      downsizeSlider.setFont(new java.awt.Font("Dialog", 0, 8)); // NOI18N
      downsizeSlider.setMajorTickSpacing(1);
      downsizeSlider.setMaximum(15);
      downsizeSlider.setMinorTickSpacing(1);
      downsizeSlider.setPaintLabels(true);
      downsizeSlider.setPaintTicks(true);
      downsizeSlider.setSnapToTicks(true);
      downsizeSlider.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "downsize init points set", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
      downsizeSlider.addChangeListener(new javax.swing.event.ChangeListener()
      {
         public void stateChanged(javax.swing.event.ChangeEvent evt)
         {
            downsizeSliderStateChanged(evt);
         }
      });
      jPanel5.add(downsizeSlider, "downsizeSlider");

      regularFieldDownsizeUI.addChangeListener(new javax.swing.event.ChangeListener()
      {
         public void stateChanged(javax.swing.event.ChangeEvent evt)
         {
            regularFieldDownsizeUIStateChanged(evt);
         }
      });
      jPanel5.add(regularFieldDownsizeUI, "regularFieldDownsizeUI");

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      add(jPanel5, gridBagConstraints);

      rangeSlider.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "steps range (0 - init point)", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
      rangeSlider.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
      rangeSlider.setInsets(new java.awt.Insets(3, 18, 3, 18));
      rangeSlider.setMaximum(150);
      rangeSlider.setMinimum(-150);
      rangeSlider.setMinimumSize(new java.awt.Dimension(60, 60));
      rangeSlider.setPaintLabels(true);
      rangeSlider.setPaintTicks(true);
      rangeSlider.setPreferredSize(new java.awt.Dimension(200, 65));
      rangeSlider.addChangeListener(new javax.swing.event.ChangeListener()
      {
         public void stateChanged(javax.swing.event.ChangeEvent evt)
         {
            rangeSliderStateChanged(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 4;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      add(rangeSlider, gridBagConstraints);

      waitToggle.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
      waitToggle.setText("wait");
      waitToggle.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            waitToggleActionPerformed(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      add(waitToggle, gridBagConstraints);
   }// </editor-fold>//GEN-END:initComponents

    private void vectorComponentSelectorStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_vectorComponentSelectorStateChanged
    {//GEN-HEADEREND:event_vectorComponentSelectorStateChanged
      setScaleMinMax();
      startAction();
    }//GEN-LAST:event_vectorComponentSelectorStateChanged

    private void stepSliderStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_stepSliderStateChanged
    {//GEN-HEADEREND:event_stepSliderStateChanged
       if (!stepSlider.isAdjusting())
          startAction();
    }//GEN-LAST:event_stepSliderStateChanged

    private void downsizeSliderStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_downsizeSliderStateChanged
    {//GEN-HEADEREND:event_downsizeSliderStateChanged
       if (!downsizeSlider.getValueIsAdjusting())
       {
          params.setDownsize(down[downsizeSlider.getValue()]);
          startAction();
       }
}//GEN-LAST:event_downsizeSliderStateChanged

    private void regularFieldDownsizeUIStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_regularFieldDownsizeUIStateChanged
    {//GEN-HEADEREND:event_regularFieldDownsizeUIStateChanged
       params.setDowsizeChanged(true);
       startAction();
    }//GEN-LAST:event_regularFieldDownsizeUIStateChanged

   private void rangeSliderStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_rangeSliderStateChanged
   {//GEN-HEADEREND:event_rangeSliderStateChanged
      if (!rangeSlider.isAdjusting())
         startAction();
   }//GEN-LAST:event_rangeSliderStateChanged

   private void waitToggleActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_waitToggleActionPerformed
   {//GEN-HEADEREND:event_waitToggleActionPerformed
      params.setActive(!waitToggle.isSelected());
   }//GEN-LAST:event_waitToggleActionPerformed

   // Variables declaration - do not modify//GEN-BEGIN:variables
   private javax.swing.JSlider downsizeSlider;
   private javax.swing.JPanel jPanel1;
   private javax.swing.JPanel jPanel5;
   private pl.edu.icm.visnow.gui.widgets.SubRangeSlider.SubRangeSlider rangeSlider;
   private pl.edu.icm.visnow.lib.gui.DownsizeUI regularFieldDownsizeUI;
   private pl.edu.icm.visnow.gui.widgets.LogarithmicSlider stepSlider;
   private pl.edu.icm.visnow.lib.gui.DataComponentSelector vectorComponentSelector;
   private javax.swing.JToggleButton waitToggle;
   // End of variables declaration//GEN-END:variables
}
