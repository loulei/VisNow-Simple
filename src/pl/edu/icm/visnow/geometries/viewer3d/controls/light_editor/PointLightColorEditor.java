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

package pl.edu.icm.visnow.geometries.viewer3d.controls.light_editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.vecmath.Color3f;
import pl.edu.icm.visnow.geometries.viewer3d.lights.EditablePointLight;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class PointLightColorEditor extends JPanel
{

   private static final long serialVersionUID = -4924595748344960000L;
   protected EditablePointLight light;
   protected BufferedImage img;
   protected Color basicColor = Color.WHITE;
   protected float constant = 1, linear = 1, quadratic = 1;
   protected int lastX = 0;
   protected JPanel panel = new JPanel()
   {
      private static final long serialVersionUID = -8306101500602665597L;
      @Override
      public void paint(Graphics g)
      {
         g.drawImage(img, 0, 0, getWidth(), getHeight(), null);
      }
   };

   protected JPanel ePanel = new JPanel()
   {
      private static final long serialVersionUID = -8306101500602665597L;
      @Override
      public void paint(Graphics gr)
      {
         int w = getWidth();
         int h = getHeight();
         for (int i = w + h; i >= 0; i--)
         {
            float a = 1/(constant + (i * linear)/(2 * w) + (i * i * quadratic)/(4 * w * w));
            int r = (int)(a*basicColor.getRed());   if (r>255) r = 255;
            int g = (int)(a*basicColor.getGreen()); if (g>255) g = 255;
            int b = (int)(a*basicColor.getBlue());  if (b>255) b = 255;
            gr.setColor(new Color(r,g,b));
            gr.fillArc(w / 2 - i/2, h/2 -i/2, i, i, 0, 360);
            gr.setColor(new Color(51,51,51));
            gr.drawString("close", w - 45, h - 5);
         }
      }
   };

   /** Creates new form LightColorEditor */
   public PointLightColorEditor()
   {
      initComponents();
      try
      {
         img = ImageIO.read(getClass().getResource("/pl/edu/icm/visnow/gui/icons/lightChooser.png"));
      } catch (Exception e)
      {
      }
      panel.addMouseListener(new java.awt.event.MouseAdapter()
      {
         @Override
         public void mouseClicked(java.awt.event.MouseEvent evt)
         {
            int x = (evt.getX() * img.getWidth())/panel.getWidth();
            if (x >= img.getWidth())  x = img.getWidth() -1;
            int y = (evt.getY() * img.getHeight())/panel.getHeight();
            if (y >= img.getHeight()) y = img.getHeight() -1;
            int c = img.getRGB(x, y);
            basicColor = new Color((c & 0x00ff0000) >> 16, (c & 0x0000ff00) >> 8, c & 0x000000ff);
            light.setLightColor(new Color3f(((c & 0x00ff0000) >> 16) / 255.f, ((c & 0x0000ff00) >> 8) / 255.f, (c & 0x000000ff) / 255.f ));
            repaint();
            ePanel.repaint();
         }
      });
      ePanel.addMouseListener(new java.awt.event.MouseAdapter()
      {
         @Override
         public void mouseClicked(java.awt.event.MouseEvent evt)
         {
            colorDialog.setVisible(false);
         }
      });
      jPanel3.add(ePanel,  BorderLayout.CENTER);
      jPanel1.add(panel, BorderLayout.CENTER);
   }

   @Override
   public void paint(Graphics gr)
   {
      int w = getWidth();
      int h = getHeight();
      for (int i = w; i >= 0; i--)
      {
         float a = 1/(constant + (i * linear)/(2 * w) + (i * i * quadratic)/(4 * w * w));
         int r = (int)(a*basicColor.getRed());   if (r>255) r = 255;
         int g = (int)(a*basicColor.getGreen()); if (g>255) g = 255;
         int b = (int)(a*basicColor.getBlue());  if (b>255) b = 255;
         gr.setColor(new Color(r,g,b));
         gr.fillArc(w / 2 - i/2, h/2 -i/2, i, i, 0, 360);
      }
   }

   public void setLight(EditablePointLight light)
   {
      this.light = light;
      float[] t = new float[3];
      light.getLightColor().get(t);
      basicColor = new Color(t[0], t[1], t[2]);
      constant = light.getAttenuation()[0];
      constSlider.setValue((int)(10 * constant));
      linear = light.getAttenuation()[1];
      linSlider.setValue((int)(10 * linear));
      quadratic = light.getAttenuation()[2];
      quadSlider.setValue((int)(10 * quadratic));
      repaint();
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

      colorDialog = new javax.swing.JFrame();
      jPanel2 = new javax.swing.JPanel();
      jPanel1 = new javax.swing.JPanel();
      constSlider = new javax.swing.JSlider();
      linSlider = new javax.swing.JSlider();
      quadSlider = new javax.swing.JSlider();
      jPanel3 = new javax.swing.JPanel();
      jLabel1 = new javax.swing.JLabel();
      jLabel2 = new javax.swing.JLabel();
      jLabel3 = new javax.swing.JLabel();

      colorDialog.setBackground(new java.awt.Color(134, 147, 156));
      colorDialog.setBounds(new java.awt.Rectangle(200, 200, 254, 154));
      colorDialog.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
      colorDialog.setName("colorDialog"); // NOI18N
      colorDialog.setUndecorated(true);
      colorDialog.setResizable(false);

      jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
      jPanel2.setMinimumSize(new java.awt.Dimension(250, 200));
      jPanel2.setName("jPanel2"); // NOI18N
      jPanel2.setPreferredSize(new java.awt.Dimension(270, 220));
      jPanel2.setLayout(new java.awt.GridBagLayout());

      jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
      jPanel1.setMaximumSize(new java.awt.Dimension(252, 152));
      jPanel1.setMinimumSize(new java.awt.Dimension(252, 152));
      jPanel1.setName("jPanel1"); // NOI18N
      jPanel1.setPreferredSize(new java.awt.Dimension(252, 152));
      jPanel1.setLayout(new java.awt.BorderLayout());
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 1.0;
      gridBagConstraints.insets = new java.awt.Insets(0, 0, 3, 0);
      jPanel2.add(jPanel1, gridBagConstraints);

      constSlider.setMajorTickSpacing(20);
      constSlider.setMinorTickSpacing(2);
      constSlider.setPaintTicks(true);
      constSlider.setName("constSlider"); // NOI18N
      constSlider.addChangeListener(new javax.swing.event.ChangeListener()
      {
         public void stateChanged(javax.swing.event.ChangeEvent evt)
         {
            constSliderStateChanged(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
      jPanel2.add(constSlider, gridBagConstraints);

      linSlider.setMajorTickSpacing(20);
      linSlider.setMinorTickSpacing(2);
      linSlider.setPaintTicks(true);
      linSlider.setName("linSlider"); // NOI18N
      linSlider.addChangeListener(new javax.swing.event.ChangeListener()
      {
         public void stateChanged(javax.swing.event.ChangeEvent evt)
         {
            linSliderStateChanged(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
      jPanel2.add(linSlider, gridBagConstraints);

      quadSlider.setMajorTickSpacing(20);
      quadSlider.setMinorTickSpacing(2);
      quadSlider.setPaintTicks(true);
      quadSlider.setName("quadSlider"); // NOI18N
      quadSlider.addChangeListener(new javax.swing.event.ChangeListener()
      {
         public void stateChanged(javax.swing.event.ChangeEvent evt)
         {
            quadSliderStateChanged(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 3;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
      jPanel2.add(quadSlider, gridBagConstraints);

      jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
      jPanel3.setMinimumSize(new java.awt.Dimension(200, 60));
      jPanel3.setName("jPanel3"); // NOI18N
      jPanel3.setPreferredSize(new java.awt.Dimension(200, 70));
      jPanel3.setLayout(new java.awt.BorderLayout());
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 4;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.weighty = 1.0;
      jPanel2.add(jPanel3, gridBagConstraints);

      jLabel1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
      jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
      jLabel1.setText("constant");
      jLabel1.setName("jLabel1"); // NOI18N
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      jPanel2.add(jLabel1, gridBagConstraints);

      jLabel2.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
      jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
      jLabel2.setText("linear");
      jLabel2.setName("jLabel2"); // NOI18N
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      jPanel2.add(jLabel2, gridBagConstraints);

      jLabel3.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
      jLabel3.setText("quadratic");
      jLabel3.setName("jLabel3"); // NOI18N
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 3;
      jPanel2.add(jLabel3, gridBagConstraints);

      colorDialog.getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

      setMinimumSize(new java.awt.Dimension(60, 21));
      setPreferredSize(new java.awt.Dimension(80, 21));
      addMouseListener(new java.awt.event.MouseAdapter()
      {
         public void mouseClicked(java.awt.event.MouseEvent evt)
         {
            formMouseClicked(evt);
         }
      });
      setLayout(new java.awt.BorderLayout());
   }// </editor-fold>//GEN-END:initComponents

   private void formMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_formMouseClicked
   {//GEN-HEADEREND:event_formMouseClicked
      colorDialog.setBounds(new java.awt.Rectangle(evt.getXOnScreen(), evt.getYOnScreen(), 254, 325));
      colorDialog.setVisible(true);
   }//GEN-LAST:event_formMouseClicked

   private void constSliderStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_constSliderStateChanged
   {//GEN-HEADEREND:event_constSliderStateChanged
     constant = constSlider.getValue() / 20.f;
     light.setAttenuation(constant, linear, quadratic);
     repaint();
     ePanel.repaint();
      repaint();
   }//GEN-LAST:event_constSliderStateChanged

   private void linSliderStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_linSliderStateChanged
   {//GEN-HEADEREND:event_linSliderStateChanged
      linear = linSlider.getValue() / 20.f;
      light.setAttenuation(constant, linear, quadratic);
      repaint();
      ePanel.repaint();
      repaint();
   }//GEN-LAST:event_linSliderStateChanged

   private void quadSliderStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_quadSliderStateChanged
   {//GEN-HEADEREND:event_quadSliderStateChanged
      quadratic = quadSlider.getValue() / 20.f;
      light.setAttenuation(constant, linear, quadratic);
      repaint();
      ePanel.repaint();
      repaint();
   }//GEN-LAST:event_quadSliderStateChanged

   // Variables declaration - do not modify//GEN-BEGIN:variables
   protected javax.swing.JFrame colorDialog;
   protected javax.swing.JSlider constSlider;
   protected javax.swing.JLabel jLabel1;
   protected javax.swing.JLabel jLabel2;
   protected javax.swing.JLabel jLabel3;
   protected javax.swing.JPanel jPanel1;
   protected javax.swing.JPanel jPanel2;
   protected javax.swing.JPanel jPanel3;
   protected javax.swing.JSlider linSlider;
   protected javax.swing.JSlider quadSlider;
   // End of variables declaration//GEN-END:variables

}
