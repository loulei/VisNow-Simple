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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicButtonUI;
import pl.edu.icm.visnow.application.application.Application;
import pl.edu.icm.visnow.application.application.ApplicationStatusChangeEvent;
import pl.edu.icm.visnow.application.application.ApplicationStatusChangeListener;
import pl.edu.icm.visnow.application.frames.ApplicationFrame2;
import pl.edu.icm.visnow.gui.icons.UIIconLoader;
import pl.edu.icm.visnow.gui.icons.UIIconLoader.IconType;
import pl.edu.icm.visnow.system.main.VisNow;
import pl.edu.icm.visnow.system.swing.VNSwingUtils;

/**
 *
 * @author Hubert Orlik-Grzesik, University of Warsaw, ICM
 * @author Bartosz Borucki, University of Warsaw, ICM
 * 
 */
public class ApplicationsPanel extends javax.swing.JPanel implements ApplicationStatusChangeListener
{

    //private static final Color statusColorOK = new Color(221, 221, 221); //184,207,229
    private static final String statusTextOK = "";
    private static final Color statusColorWARNING = VNSwingUtils.SATURATION_NODATA;
    private static final String statusTextWARNING = "Application network WARNING.\nAt least one module has no data on a required input.";
    private static final Color statusColorERROR = VNSwingUtils.SATURATION_WRONGDATA;
    private static final String statusTextERROR = "Application network ERROR!\nAt least one module has wrong data on a required input.";
    
    
   //<editor-fold defaultstate="collapsed" desc=" Add/Remove/Get Application ">
   //protected Vector<Application> applications;
   public void addApplication(Application application)
   {
      ApplicationFrame2 frame = application.getApplicationFrame();
      jTabbedPane.add(frame);
      int i = jTabbedPane.indexOfComponent(frame);
      jTabbedPane.setTitleAt(i, frame.getApplication().getTitle());
      jTabbedPane.setTabComponentAt(i, new SimpleTabRenderer(frame.getApplication()) );
      jTabbedPane.setSelectedIndex(i);
      application.addStatusChangeListener(this);
   }

    private boolean safeRemoveApplication(Application app) {
        if (!app.hasChanged() || app.getEngine().getModules().isEmpty()) {
            return doRemoveAppplication(app);
        }
        
        int res = JOptionPane.showConfirmDialog(
                this,
                "Application " + app.getTitle()
                + " has changed. Do you want to save it?",
                "Exit VN",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE);
        
        if (res == JOptionPane.CANCEL_OPTION) {
            return false;
        }
        
        if (res == JOptionPane.YES_OPTION) {
            if (!VisNow.get().getMainWindow().getMainMenu().saveApplication()) {
                return false;
            }
        }        
        return doRemoveAppplication(app);
    }
       
    private boolean doRemoveAppplication(Application app) {
        if (app == null) {
            return true;
        }

        int j = -1;
        for (int i = 0; i < jTabbedPane.getTabCount(); i++) {
            if (jTabbedPane.getComponentAt(i) instanceof ApplicationFrame2 && ((ApplicationFrame2) jTabbedPane.getComponentAt(i)).getApplication() == app) {
                j = i;
                break;
            }
        }
        if (j == -1) {
            return false;
        }

        app.deleteAllModules();
        app.removeStatusChangeListener(this);
        jTabbedPane.remove(j);
        return true;
    }

   public boolean removeCurrentApplication()
   {
       return removeApplication(getCurrentApplication());
   }

   public void removeApplication(int i)
   {
       Component comp = jTabbedPane.getComponentAt(i);
       if(comp == null)
           return;
              
       if(!(comp instanceof ApplicationFrame2))
           return;

       Application app = ((ApplicationFrame2)comp).getApplication();
       removeApplication(app);
   }

   public boolean removeApplication(Application app) {
       return this.removeApplication(app, false);
   }
   
   public boolean removeApplication(Application app, boolean force)
   {
       if(app == null)
           return true;
       if(force)
           return doRemoveAppplication(app);
       else
           return safeRemoveApplication(app);
   }
   
   public Application getCurrentApplication()
   {
      if (jTabbedPane.getSelectedComponent() == null)
         return null;
      if (jTabbedPane.getSelectedComponent() instanceof ApplicationFrame2)
         return ((ApplicationFrame2) jTabbedPane.getSelectedComponent()).getApplication();
      return null;
   }

   //public Vector<Application> getApplications() {
   //    return applications;
   //}
   public boolean isApplicationOpened()
   {
      if (getCurrentApplication() == null)
         return false;
      return true;
   }

   //</editor-fold>
   /**
    * Creates new form ApplicationsPanel
    */
   public ApplicationsPanel()
   {
      initComponents();
      //applications = new Vector<Application>();
   }

   /**
    * This method is called from within the constructor to initialize the form. WARNING: Do NOT
    * modify this code. The content of this method is always regenerated by the Form Editor.
    */
   @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane = new javax.swing.JTabbedPane();

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane jTabbedPane;
    // End of variables declaration//GEN-END:variables

    @Override
    public void applicationStatusChanged(ApplicationStatusChangeEvent e) {
        ApplicationFrame2 frame = e.getApplication().getApplicationFrame();        
        int i = jTabbedPane.indexOfComponent(frame);
        
        switch (e.getStatus()) {
            case ERROR:
                ((SimpleTabRenderer)jTabbedPane.getTabComponentAt(i)).setIcon(UIIconLoader.getIcon(IconType.ERROR, 16, 16));
                jTabbedPane.setToolTipTextAt(i, statusTextERROR);
                break;
            case WARNING:
                ((SimpleTabRenderer)jTabbedPane.getTabComponentAt(i)).setIcon(UIIconLoader.getIcon(IconType.WARNING, 16, 16));
                jTabbedPane.setToolTipTextAt(i, statusTextWARNING);
                break;
            default:
                ((SimpleTabRenderer)jTabbedPane.getTabComponentAt(i)).setIcon(null);
                jTabbedPane.setToolTipTextAt(i, statusTextOK);
                break;
        }
    }
    
    /**
     * Refreshes
     * <code>application</code> title
     */
    public void updateApplicationTabTitle(Application application) {
        ApplicationFrame2 frame = application.getApplicationFrame();
        if (jTabbedPane.indexOfComponent(frame) >= 0)
            ((SimpleTabRenderer) jTabbedPane.getTabComponentAt(jTabbedPane.indexOfComponent(frame))).setLabel(application.getTitle());
    }

    private class SimpleTabRenderer extends JPanel {
        private Application application;
        private JLabel label;
        
        public SimpleTabRenderer(Application application) {
            super(new FlowLayout(FlowLayout.LEFT, 0, 0));
            this.application = application;
            setOpaque(false);
            label = new JLabel(application.getTitle());         
            add(label);            
            label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
            JButton button = new TabButton();
            add(button);
            setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));            
        }
        
        public void setTextColor(Color color) {
            this.label.setForeground(color);
        }

        public void setTextBackground(Color color) {
            this.label.setBackground(color);
        }
        
        public void setIcon(Icon icon) {
            label.setIcon(icon);
        }
        
        /**
         * Sets tab label 
         */
        public void setLabel(String text) {
            label.setText(text);
        }
        
        private class TabButton extends JButton implements ActionListener {
            public TabButton() {
                int size = 17;
                setPreferredSize(new Dimension(size, size));
                setToolTipText("Close application");
                setUI(new BasicButtonUI());
                setContentAreaFilled(false);
                setFocusable(false);
                setBorder(BorderFactory.createEtchedBorder());
                setBorderPainted(false);
                addMouseListener(buttonMouseListener);
                setRolloverEnabled(true);
                addActionListener(this);
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                ApplicationsPanel.this.removeApplication(application);
            }

            @Override
            public void updateUI() {
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                if (getModel().isPressed()) {
                    g2.translate(1, 1);
                }
                g2.setStroke(new BasicStroke(2));
                g2.setColor(Color.BLACK);
                if (getModel().isRollover()) {
                    g2.setColor(Color.MAGENTA);
                }
                int delta = 6;
                g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
                g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
                g2.dispose();
            }
        }
    }

    private final static MouseListener buttonMouseListener = new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(true);
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(false);
            }
        }
    };        
    
    
}
