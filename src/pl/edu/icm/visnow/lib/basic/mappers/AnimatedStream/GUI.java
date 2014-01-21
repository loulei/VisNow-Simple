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

/**
 * @author Krzysztof S. Nowinski (know@icm.edu.pl) Warsaw University,
 * Interdisciplinary Centre for Mathematical and Computational Modelling
 */
package pl.edu.icm.visnow.lib.basic.mappers.AnimatedStream;

import java.util.Hashtable;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import pl.edu.icm.visnow.lib.utils.Range;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;

public class GUI extends javax.swing.JPanel
{

   protected Params params                      = null;
   private int nFrames                          = 1;
   private int segmentLength                    = 1;
   private Hashtable<Integer,JLabel> timeLabels = new Hashtable<Integer,JLabel>();
   private int mode = Params.STOP;
   private ImageIcon fwdIcon     = new javax.swing.ImageIcon(getClass().getResource("/pl/edu/icm/visnow/gui/icons/fwd.png"));
   private ImageIcon stopActIcon = new javax.swing.ImageIcon(getClass().getResource("/pl/edu/icm/visnow/gui/icons/pauseButtonEnabled.png"));
   private ImageIcon bckIcon     = new javax.swing.ImageIcon(getClass().getResource("/pl/edu/icm/visnow/gui/icons/bck.png"));
   private ImageIcon selFwdIcon  = new javax.swing.ImageIcon(getClass().getResource("/pl/edu/icm/visnow/gui/icons/selfwd.png"));
   private ImageIcon selBckIcon  = new javax.swing.ImageIcon(getClass().getResource("/pl/edu/icm/visnow/gui/icons/selbck.png"));
   /**
    * Set the value of dims
    *
    * @param dims new value of dims
    */
   /** Creates new form EmptyVisnowModuleGUI */
   public GUI()
   {
      initComponents();
   }

   /** This method is called from within the constructor to
    * initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is
    * always regenerated by the Form Editor.
    */
   @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup1 = new javax.swing.ButtonGroup();
        segmentLengthSlider = new javax.swing.JSlider();
        nSegmentsSlider = new javax.swing.JSlider();
        delaySlider = new javax.swing.JSlider();
        backButton = new javax.swing.JButton();
        stopButton = new javax.swing.JButton();
        forwardButton = new javax.swing.JButton();
        badInLabel = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));

        setLayout(new java.awt.GridBagLayout());

        segmentLengthSlider.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        segmentLengthSlider.setMajorTickSpacing(5);
        segmentLengthSlider.setMaximum(50);
        segmentLengthSlider.setMinorTickSpacing(1);
        segmentLengthSlider.setPaintLabels(true);
        segmentLengthSlider.setPaintTicks(true);
        segmentLengthSlider.setValue(5);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pl/edu/icm/visnow/lib/basic/mappers/AnimatedStream/Bundle"); // NOI18N
        segmentLengthSlider.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("GUI.segmentLengthSlider.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        segmentLengthSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                segmentLengthSliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        add(segmentLengthSlider, gridBagConstraints);

        nSegmentsSlider.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        nSegmentsSlider.setMajorTickSpacing(50);
        nSegmentsSlider.setMinorTickSpacing(5);
        nSegmentsSlider.setPaintLabels(true);
        nSegmentsSlider.setPaintTicks(true);
        nSegmentsSlider.setValue(1);
        nSegmentsSlider.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("GUI.nSegmentsSlider.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        nSegmentsSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                nSegmentsSliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        add(nSegmentsSlider, gridBagConstraints);

        delaySlider.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        delaySlider.setMajorTickSpacing(10);
        delaySlider.setMaximum(50);
        delaySlider.setMinorTickSpacing(1);
        delaySlider.setPaintLabels(true);
        delaySlider.setPaintTicks(true);
        delaySlider.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("GUI.delaySlider.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        delaySlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                delaySliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        add(delaySlider, gridBagConstraints);

        backButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pl/edu/icm/visnow/gui/icons/bck.png"))); // NOI18N
        backButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.4;
        add(backButton, gridBagConstraints);

        stopButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pl/edu/icm/visnow/gui/icons/pauseButtonEnabled.png"))); // NOI18N
        stopButton.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/pl/edu/icm/visnow/gui/icons/pauseButtonDisabled.png"))); // NOI18N
        stopButton.setEnabled(false);
        stopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.2;
        add(stopButton, gridBagConstraints);

        forwardButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pl/edu/icm/visnow/gui/icons/fwd.png"))); // NOI18N
        forwardButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                forwardButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.4;
        add(forwardButton, gridBagConstraints);

        badInLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        badInLabel.setForeground(new java.awt.Color(153, 0, 51));
        badInLabel.setText(bundle.getString("GUI.badInLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        add(badInLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.weighty = 1.0;
        add(filler1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    /**
    * Set the value of params
    *
    * @param params new value of params
    */
   public void setParams(Params params)
   {
      this.params = params;
   }

   public void setNFrames(int nFr)
   {
      params.setActive(false);
      this.nFrames = nFr;
      SwingInstancer.swingRunAndWait(new Runnable()
      {
         public void run()
         {
            boolean hasFrames = nFrames > 1;
            backButton.setEnabled(hasFrames);
            badInLabel.setEnabled(hasFrames);
            delaySlider.setEnabled(hasFrames);
            forwardButton.setEnabled(hasFrames);
            nSegmentsSlider.setEnabled(hasFrames);
            segmentLengthSlider.setEnabled(hasFrames);
            stopButton.setEnabled(hasFrames);

            segmentLengthSlider.setValue(nFrames/200);
            updateNSegmentsSlider();
         }
      });
   }

   public void updateNSegmentsSlider()
   {
      int w = 200;
      int maxSegments = (nFrames + segmentLength) / (segmentLength + 1);
      if (maxSegments < 2)
         maxSegments = 2;
      int dt;
      Range tRange = new Range(0.f, (float) maxSegments, w);
      dt = (int) tRange.getStep();
      timeLabels.clear();
      for (int i = 0; i < (maxSegments / dt) + 1; i++)
         timeLabels.put(new Integer((i * dt)), new JLabel("" + i * dt));
      nSegmentsSlider.setMaximum(maxSegments);
      nSegmentsSlider.setMajorTickSpacing(5 * dt);
      nSegmentsSlider.setMinorTickSpacing(dt);
      nSegmentsSlider.setLabelTable(timeLabels);
      nSegmentsSlider.setPaintLabels(true);
      int  nSegments = Math.max(2, maxSegments/2);
      nSegmentsSlider.setValue(maxSegments - 1);
      params.setNSegments(nSegments);
      params.setActive(true);
   }

   public void setInText(String s)
   {
      badInLabel.setText(s);
   }

    private void segmentLengthSliderStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_segmentLengthSliderStateChanged
    {//GEN-HEADEREND:event_segmentLengthSliderStateChanged
       if (!segmentLengthSlider.getValueIsAdjusting() )
       {
           params.setActive(false);
           segmentLength = segmentLengthSlider.getValue();
           if (segmentLength == 0)
              segmentLength = 1;
           params.setSegmentLength(segmentLength);
           updateNSegmentsSlider();
           //params.setActive(true);
       }

    }//GEN-LAST:event_segmentLengthSliderStateChanged

    private void nSegmentsSliderStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_nSegmentsSliderStateChanged
    {//GEN-HEADEREND:event_nSegmentsSliderStateChanged
       if (!nSegmentsSlider.getValueIsAdjusting())
       {
           params.setActive(false);
           int n = nSegmentsSlider.getValue();
           if (n < 1)
              n = 1;
           params.setNSegments(n);
           params.setActive(true);
       }
    }//GEN-LAST:event_nSegmentsSliderStateChanged


    private void delaySliderStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_delaySliderStateChanged
    {//GEN-HEADEREND:event_delaySliderStateChanged
       params.setDelay(delaySlider.getValue());
    }//GEN-LAST:event_delaySliderStateChanged

   private void backButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_backButtonActionPerformed
   {//GEN-HEADEREND:event_backButtonActionPerformed
      if (mode != Params.STOP)
         return;
      backButton.setIcon(selBckIcon);
      forwardButton.setEnabled(false);
      stopButton.setEnabled(true);
      mode = Params.BACK;
      params.setAnimate(Params.BACK);
   }//GEN-LAST:event_backButtonActionPerformed

   private void forwardButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_forwardButtonActionPerformed
   {//GEN-HEADEREND:event_forwardButtonActionPerformed
      if (mode != Params.STOP)
         return;
      forwardButton.setIcon(selFwdIcon);
      backButton.setEnabled(false);
      stopButton.setEnabled(true);
      mode = Params.FORWARD;
      params.setAnimate(Params.FORWARD);
   }//GEN-LAST:event_forwardButtonActionPerformed

   private void stopButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_stopButtonActionPerformed
   {//GEN-HEADEREND:event_stopButtonActionPerformed
      mode = Params.STOP;
      stopButton.setEnabled(false);
      forwardButton.setIcon(fwdIcon);
      forwardButton.setEnabled(true);
      backButton.setIcon(bckIcon);
      backButton.setEnabled(true);
      params.setAnimate(Params.STOP);
   }//GEN-LAST:event_stopButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton backButton;
    private javax.swing.JLabel badInLabel;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JSlider delaySlider;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JButton forwardButton;
    private javax.swing.JSlider nSegmentsSlider;
    private javax.swing.JSlider segmentLengthSlider;
    private javax.swing.JButton stopButton;
    // End of variables declaration//GEN-END:variables
}