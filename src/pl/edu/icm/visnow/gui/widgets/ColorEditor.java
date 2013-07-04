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

package pl.edu.icm.visnow.gui.widgets;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.Color3f;
import pl.edu.icm.visnow.gui.icons.IconsContainer;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class ColorEditor extends JPanel
{

   private static final long serialVersionUID = -4924595748344960000L;
   protected Color basicColor = Color.WHITE;
   protected BufferedImage img;
   protected int brightness = 50;
   protected int lastBrightness = 0;
   protected int lastHue = 300;
   protected int lastSat = 0;
   protected int lastButton = 0;
   protected int lastX = 0, lastY = 0;
   protected String title = "";
   protected Color color = new Color(.71f, .71f, .71f);
   protected Color3f color3f = new Color3f(.71f, .71f, .71f);
   protected float hue = 3;
   protected float sat = 0;
   protected boolean adjusting = false;
   protected JPanel colorSelector = new JPanel()
   {
      private static final long serialVersionUID = -8306101500602665597L;

      @Override
      public void paint(Graphics g)
      {
         g.drawImage(img, 0, 0, getWidth(), getHeight(), null);
      }
   };

   /**
    * Creates new form ColorEditor
    */
   public ColorEditor()
   {
      initComponents();
      img = IconsContainer.getLightColorTable();
      setBackground(color);
      colorSelector.addMouseListener(new java.awt.event.MouseAdapter()
      {
         @Override
         public void mouseClicked(java.awt.event.MouseEvent evt)
         {
            int x = (evt.getX() * img.getWidth()) / colorSelector.getWidth();
            if (x >= img.getWidth())
               x = img.getWidth() - 1;
            int y = (evt.getY() * img.getHeight()) / colorSelector.getHeight();
            if (y >= img.getHeight())
               y = img.getHeight() - 1;
            int c = img.getRGB(x, y);
            basicColor = new Color((c & 0x00ff0000) >> 16, (c & 0x0000ff00) >> 8, c & 0x000000ff);
            brightness = Math.max(Math.min(100, brightness), 0);
            float b = (float) Math.sqrt(brightness / 100.);
            color = new Color(basicColor.getRed() * b / 255,
                    basicColor.getGreen() * b / 255,
                    basicColor.getBlue() * b / 255);
            setBackground(color);
            color3f = new Color3f();
            fireStateChanged();
            colorDialog.setVisible(false);
         }
      });
      jPanel1.add(colorSelector, BorderLayout.CENTER);
   }

   public void setTitle(String s)
   {
      title = s;
      repaint();
   }

   public void setBasicColor(Color c)
   {
      basicColor = c;
      float b = (float)(brightness / 100.);
//      float b = (float) Math.sqrt(brightness / 100.);
      color = new Color(c.getRed() * b / 255,
              c.getGreen() * b / 255,
              c.getBlue() * b / 255);
      setBackground(color);
      repaint();
   }

   public void setBrightness(int br)
   {
      brightness = br;
      float b = (float)(brightness / 100.);
//      float b = (float) Math.sqrt(brightness / 100.);
      color = new Color(basicColor.getRed() * b / 255,
              basicColor.getGreen() * b / 255,
              basicColor.getBlue() * b / 255);
      setBackground(color);
      repaint();
   }

   public void setColor(Color c)
   {
      float r = c.getRed();
      float g = c.getGreen();
      float b = c.getBlue();
      float m = Math.max(r, Math.max(g, b));
      if (m == 0)
         basicColor = new Color(1, 1, 1);
      else
         basicColor = new Color(r / m, g / m, b / m);
      brightness = (int) (100 * m * m);
      color = c;
      repaint();
   }

   /**
    * This method is called from within the constructor to initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is always
    * regenerated by the Form Editor.
    */
   @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        colorDialog = new javax.swing.JFrame();
        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();

        colorDialog.setBackground(new java.awt.Color(134, 147, 156));
        colorDialog.setBounds(new java.awt.Rectangle(200, 200, 254, 154));
        colorDialog.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        colorDialog.setName("colorDialog"); // NOI18N
        colorDialog.setResizable(false);
        colorDialog.setUndecorated(true);

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new java.awt.GridBagLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
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

        colorDialog.getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        setToolTipText("<html>drag left MB to change brightness<p>drag right MB to change hue and saturation<p>click to open color chooser"); // NOI18N
        setMinimumSize(new java.awt.Dimension(60, 21));
        setPreferredSize(new java.awt.Dimension(100, 21));
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                formMouseReleased(evt);
            }
        });
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                formMouseDragged(evt);
            }
        });
        setLayout(null);
    }// </editor-fold>//GEN-END:initComponents

   @Override
   public void paint(Graphics g)
   {
      int w = getWidth();
      int h = getHeight();

      if (enabled)
      {
         g.setColor(color);
         g.fillRect(0, 0, w, h);
         g.setColor(Color.LIGHT_GRAY);
         g.fillRect(20, 2, w - 22, h - 4);
         w -= 24;
         h -= 6;
         g.setColor(basicColor);
         int r = (brightness * w) / 100;
         g.fillRect(21, 3, r, h);
         g.setColor(Color.GRAY);
         g.fillRect(r + 21, 3, w - r, h);
         g.setFont(new java.awt.Font("Dialog Bold", 0, 12));
         g.setColor(Color.BLACK);
         g.drawString(title, 22, h / 2 + 10);
      } else
      {
         g.setColor(Color.LIGHT_GRAY);
         g.fillRect(0, 0, w, h);
         g.setColor(new Color(238, 238, 238));
         g.fillRect(20, 2, w - 22, h - 4);
         w -= 24;
         h -= 6;
         g.setFont(new java.awt.Font("Dialog Bold", 0, 12));
         g.setColor(Color.LIGHT_GRAY);
         g.drawString(title, 22, h / 2 + 10);
      }
   }

   private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
      if (!enabled)
         return;
      colorDialog.setBounds(new java.awt.Rectangle(evt.getXOnScreen(), evt.getYOnScreen(), 254, 154));
      colorDialog.setVisible(true);
      java.awt.EventQueue.invokeLater(new Runnable()
      {
         @Override
         public void run()
         {
            colorDialog.toFront();
            colorDialog.repaint();
         }
      });

   }//GEN-LAST:event_formMouseClicked

   private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
      if (!enabled)
         return;
      adjusting = true;
      lastX = evt.getX();
      lastY = evt.getY();
      lastButton = evt.getButton();
      if (evt.getButton() == MouseEvent.BUTTON1)
         lastBrightness = brightness;
      else
      {
         lastHue = (int) (100 * hue);
         lastSat = (int) (100 * sat);
      }
   }//GEN-LAST:event_formMousePressed

   private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged
      if (!enabled)
         return;
      adjusting = true;
      if (lastButton == MouseEvent.BUTTON1)
      {
         brightness = lastBrightness + (int) (100. * (evt.getX() - lastX) / (getWidth() - 4.));
         if (brightness < 0)
            brightness = 0;
         else if (brightness > 100)
            brightness = 100;
      } else
      {
         hue = ((lastHue - evt.getX() + lastX + 2400) % 600) / 100.f;
         int s = lastSat + lastY - evt.getY();
         if (s < 0)
            s = 0;
         if (s > 100)
            s = 100;
         sat = s / 100.f;
         int i = (int) hue;
         float f = hue - i;
         float p = 1 - sat;
         float q = 1 - sat * f;
         float t = 1 - sat * (1 - f);
         if (i == 0)
            basicColor = new Color(1, t, p);
         else if (i == 1)
            basicColor = new Color(q, 1, p);
         else if (i == 2)
            basicColor = new Color(p, 1, t);
         else if (i == 3)
            basicColor = new Color(p, q, 1);
         else if (i == 4)
            basicColor = new Color(t, p, 1);
         else if (i == 5)
            basicColor = new Color(1, p, q);
      }
      float b = (float) (brightness / 100.);
//      float b = (float) Math.sqrt(brightness / 100.);
      color = new Color(basicColor.getRed() * b / 255,
              basicColor.getGreen() * b / 255,
              basicColor.getBlue() * b / 255);
      setBackground(color);
      fireStateChanged();
      repaint();
   }//GEN-LAST:event_formMouseDragged

   private void formMouseReleased(java.awt.event.MouseEvent evt)//GEN-FIRST:event_formMouseReleased
   {//GEN-HEADEREND:event_formMouseReleased
      if (!enabled)
         return;
      adjusting = false;
      fireStateChanged();
   }//GEN-LAST:event_formMouseReleased
    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JFrame colorDialog;
    protected javax.swing.JPanel jPanel1;
    protected javax.swing.JPanel jPanel2;
    // End of variables declaration//GEN-END:variables

   public Color getColor()
   {
      return color;
   }

   public int getBrightness()
   {
      return brightness;
   }

   public float[] getColorComponents()
   {
      return new float[]
              {
                 basicColor.getRed() * brightness / 25500.f,
                 basicColor.getGreen() * brightness / 25500.f,
                 basicColor.getBlue() * brightness / 25500.f
              };
   }

   public boolean isAdjusting()
   {
      return adjusting;
   }
   /**
    * Utility field holding list of ChangeListeners.
    */
   private transient ArrayList<ChangeListener> listeners = new ArrayList<ChangeListener>();

   public void addChangeListener(ChangeListener l)
   {
      listeners.add(l);
   }

   public void removeChangeListener(ChangeListener l)
   {
      listeners.remove(l);
   }

   /**
    * Notifies all registered listeners about the event.
    *
    * @param object Parameter #1 of the <CODE>ChangeEvent<CODE> constructor.
    */
   private void fireStateChanged()
   {
      this.repaint();
      ChangeEvent e = new ChangeEvent(this);
      for (ChangeListener l : listeners)
         l.stateChanged(e);
   }
   private boolean enabled = true;

   @Override
   public void setEnabled(boolean enabled)
   {
      super.setEnabled(enabled);
      this.enabled = enabled;
   }

   @Override
   public boolean isEnabled()
   {
      return enabled;
   }
}
