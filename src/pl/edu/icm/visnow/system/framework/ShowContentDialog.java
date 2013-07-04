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

package pl.edu.icm.visnow.system.framework;

import java.awt.Dimension;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import pl.edu.icm.visnow.system.main.VisNow;

/**
 *
 * @author babor
 */
public class ShowContentDialog extends javax.swing.JDialog {

    /** Creates new form ShowContentDialog */
    public ShowContentDialog(java.awt.Frame parent, String objectName, String objectInfo) {
        super(parent, true);

        if (parent == null) {
        }

        initComponents();

        this.setTitle("Show Content");
        this.setIconImage(new ImageIcon(getClass().getResource( VisNow.getIconPath() )).getImage());
        
        Icon infoIcon = UIManager.getIcon("OptionPane.informationIcon");
        this.textLabel.setIcon(infoIcon);
        this.textLabel.setIconTextGap(20);
        this.infoLabel.setText("Content of: " + objectName);
        this.textLabel.setText(objectInfo);
        centerOnScreen();
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

      jPanel1 = new javax.swing.JPanel();
      infoLabel = new javax.swing.JLabel();
      jPanel2 = new javax.swing.JPanel();
      jScrollPane1 = new javax.swing.JScrollPane();
      jPanel4 = new javax.swing.JPanel();
      textLabel = new javax.swing.JLabel();
      jPanel3 = new javax.swing.JPanel();
      okButton = new javax.swing.JButton();

      setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
      setTitle("Content");
      setMinimumSize(new java.awt.Dimension(600, 400));
      setResizable(false);
      getContentPane().setLayout(new java.awt.BorderLayout());

      jPanel1.setMinimumSize(new java.awt.Dimension(600, 30));
      jPanel1.setName("jPanel1"); // NOI18N
      jPanel1.setPreferredSize(new java.awt.Dimension(600, 30));
      jPanel1.setLayout(new java.awt.GridBagLayout());

      infoLabel.setText("Contents of");
      infoLabel.setName("infoLabel"); // NOI18N
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 1.0;
      gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
      jPanel1.add(infoLabel, gridBagConstraints);

      getContentPane().add(jPanel1, java.awt.BorderLayout.NORTH);

      jPanel2.setMinimumSize(new java.awt.Dimension(460, 300));
      jPanel2.setName("jPanel2"); // NOI18N
      jPanel2.setPreferredSize(new java.awt.Dimension(460, 400));
      jPanel2.setLayout(new java.awt.GridBagLayout());

      jScrollPane1.setBorder(null);
      jScrollPane1.setName("jScrollPane1"); // NOI18N

      jPanel4.setName("jPanel4"); // NOI18N
      jPanel4.setLayout(new java.awt.GridBagLayout());

      textLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
      textLabel.setText("<no data>");
      textLabel.setName("textLabel"); // NOI18N
      textLabel.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 1.0;
      gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
      jPanel4.add(textLabel, gridBagConstraints);

      jScrollPane1.setViewportView(jPanel4);

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 1.0;
      gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
      jPanel2.add(jScrollPane1, gridBagConstraints);

      getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

      jPanel3.setMinimumSize(new java.awt.Dimension(460, 35));
      jPanel3.setName("jPanel3"); // NOI18N
      jPanel3.setPreferredSize(new java.awt.Dimension(460, 35));
      jPanel3.setLayout(new java.awt.GridBagLayout());

      okButton.setText("OK");
      okButton.setName("okButton"); // NOI18N
      okButton.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            okButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 1.0;
      gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
      jPanel3.add(okButton, gridBagConstraints);

      getContentPane().add(jPanel3, java.awt.BorderLayout.SOUTH);

      pack();
   }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        this.dispose();
    }//GEN-LAST:event_okButtonActionPerformed

    public static void showContentDialog(java.awt.Frame parent, String objectName, Object object) {
        ShowContentDialog dlg;
        if (object == null) {
            dlg = new ShowContentDialog(parent, objectName, "<no data>");
        } else {
            dlg = new ShowContentDialog(parent, objectName, object.toString());
        }

        dlg.setVisible(true);
    }
   // Variables declaration - do not modify//GEN-BEGIN:variables
   private javax.swing.JLabel infoLabel;
   private javax.swing.JPanel jPanel1;
   private javax.swing.JPanel jPanel2;
   private javax.swing.JPanel jPanel3;
   private javax.swing.JPanel jPanel4;
   private javax.swing.JScrollPane jScrollPane1;
   private javax.swing.JButton okButton;
   private javax.swing.JLabel textLabel;
   // End of variables declaration//GEN-END:variables

    private void centerOnScreen() {
        int screenID = ScreenInfo.getScreenID(VisNow.get().getMainWindow());
        Dimension screenSize = ScreenInfo.getScreenDimension(screenID);
        int x = (screenSize.width - this.getWidth()) / 2;
        int y = (screenSize.height - this.getHeight()) / 2;
        this.setLocation(x, y);
    }
}