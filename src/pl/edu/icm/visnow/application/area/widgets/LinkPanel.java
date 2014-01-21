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
exception statement from your version.
*/
//</editor-fold>

package pl.edu.icm.visnow.application.area.widgets;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.File;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.filechooser.FileNameExtensionFilter;
import pl.edu.icm.visnow.application.area.Quad;
import pl.edu.icm.visnow.application.area.SelectableAreaItem;
import pl.edu.icm.visnow.engine.core.Link;
import pl.edu.icm.visnow.lib.basic.writers.FieldWriter.FieldWriter;
import pl.edu.icm.visnow.lib.types.VNRegularField;
import pl.edu.icm.visnow.lib.utils.io.VisNowRegularFieldWriterCore;
import pl.edu.icm.visnow.system.framework.MainWindow;
import pl.edu.icm.visnow.system.main.VisNow;
import pl.edu.icm.visnow.system.swing.VNSwingUtils;
import pl.edu.icm.visnow.system.swing.filechooser.VNFileChooser;
import pl.edu.icm.visnow.system.utils.usermessage.Level;
import pl.edu.icm.visnow.system.utils.usermessage.UserMessage;

/**
 *
 * @author Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class LinkPanel extends JComponent implements SelectableAreaItem
{

   //<editor-fold defaultstate="collapsed" desc=" [VAR] Link ">
   private Link link;

   public Link getLink()
   {
      return link;
   }
    //</editor-fold>

   //<editor-fold defaultstate="collapsed" desc=" [VAR] Ports ">
   private PortPanel outputPanel;
   private PortPanel inputPanel;

   public PortPanel getInputPanel()
   {
      return inputPanel;
   }

   public PortPanel getOutputPanel()
   {
      return outputPanel;
   }
    //</editor-fold>

   //<editor-fold defaultstate="collapsed" desc=" [VAR] Lines ">
   private Vector<LinkPanelRectangle> lines;
    //</editor-fold>

   //<editor-fold defaultstate="collapsed" desc=" [VAR] Selection ">
   private boolean selected;
   private int centerX, centerY;

   public String getModuleForSelecting()
   {
      return null;
   }

   @Override
   public boolean isSelected()
   {
      return selected;
   }

   @Override
   public void setSelected(boolean b)
   {
      selected = b;
      repaint();
   }
    //</editor-fold>

   //<editor-fold defaultstate="collapsed" desc=" [VAR] Menu ">
   private javax.swing.JMenuItem mbRemove;
   private javax.swing.JMenuItem mbShowContent;
   private javax.swing.JPopupMenu popupMenu;
   private javax.swing.JMenuItem mbSaveData;
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Painting, Collision ">
   @Override
   public boolean isRectangled(Quad q)
   {
      for (LinkPanelRectangle r : lines)
      {
         if (r.isRectangled(q))
         {
            return true;
         }
      }
      return false;
   }

   public boolean isHit(Point p)
   {
      for (LinkPanelRectangle r : lines)
      {
         if (r.contains(p))
         {
            return true;
         }
      }
      return false;
   }

   @Override
   public void paint(Graphics g)
   {
      Graphics2D gg = (Graphics2D) g;
      int fromX = outputPanel.getTotalX() + 8;
      int fromY = outputPanel.getTotalY() + 4;

      int toX = inputPanel.getTotalX() + 8;
      int toY = inputPanel.getTotalY() + 6;
      
      centerX = (fromX + toX) / 2;
      centerY = (fromY + toY) / 2;

      int secX = fromX;
      int secY = fromY + 6;
      int lasX = toX;
      int lasY = (this.inputPanel != null) ? toY - 6 : toY - 15;
      int[] x;
      int[] y;

      lines = new Vector<LinkPanelRectangle>();

      if (secY < lasY)
      {
         int midY = (secY + lasY) / 2;

         x = new int[] {fromX, secX, secX, lasX, lasX, toX};
         y = new int[]
         {fromY, secY, midY, midY, lasY, toY};
         for (int i = 0; i < 5; ++i)
            lines.add(new LinkPanelRectangle(x[i], y[i], x[i + 1], y[i + 1], 2));

      } else
      {
         int midX = (secX + lasX) / 2;
         x = new int[] {fromX, secX, midX, midX, lasX, toX};
         y = new int[] {fromY, secY, secY, lasY, lasY, toY};
         for (int i = 0; i < 5; ++i)
            lines.add(new LinkPanelRectangle(x[i], y[i], x[i + 1], y[i + 1], 2));
      }
      gg.addRenderingHints(VNSwingUtils.getHints());
      if (selected)
      {
         gg.setColor(new java.awt.Color(255, 255, 204, 153));
         gg.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, 
                          new float[]{8, 8}, 8));
         gg.drawPolyline(x, y, 6);
      }

      gg.setColor(VNSwingUtils.typeColor(getLink().getOutput().getType().getName()));
      gg.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
      gg.drawPolyline(x, y, 6);
      gg.setColor(VNSwingUtils.typeColor(getLink().getOutput().getType().getName()));
   }
    //</editor-fold>

   //<editor-fold defaultstate="collapsed" desc=" [Constructor] ">
   /**
    * Creates new instance of LinkConnectingPanel
    */
   public LinkPanel(Link link, PortPanel output, PortPanel input)
   {
      this.outputPanel = output;
      this.inputPanel = input;
      this.link = link;
      lines = new Vector<LinkPanelRectangle>();
      selected = false;
      popupMenu = new javax.swing.JPopupMenu();
      
      mbRemove = new javax.swing.JMenuItem();
      mbRemove.setText("Remove");
      mbRemove.addActionListener(new java.awt.event.ActionListener()
      {
         @Override
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            menuRemove();
         }
      });
      popupMenu.add(mbRemove);

      popupMenu.add(new JPopupMenu.Separator());
      mbShowContent = new javax.swing.JMenuItem();
      mbShowContent.setText("Show content");
      mbShowContent.addActionListener(new java.awt.event.ActionListener()
      {
         @Override
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            menuShowContent();
         }
      });
      popupMenu.add(mbShowContent);

      popupMenu.add(new JPopupMenu.Separator());
      mbSaveData = new javax.swing.JMenuItem();
      mbSaveData.setText("Write field...");
      mbSaveData.addActionListener(new java.awt.event.ActionListener()
      {
         @Override
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            menuSaveData();
         }
      });
      popupMenu.add(mbSaveData);    
      
      this.add(new JLabel("INIT"));
   }
   //</editor-fold>

   //<editor-fold defaultstate="collapsed" desc=" Menu ">
   public void showMenu(Point p)
   {
       Object obj = getLink().getOutput().getValue();       
       mbSaveData.setEnabled(obj != null && obj instanceof VNRegularField);
       popupMenu.show(this, (int) p.getX(), (int) p.getY());
   }
   //</editor-fold>

   private void menuRemove()
   {
      getOutputPanel()
              .getModulePanel()
              .getAreaPanel()
              .getArea()
              .getOutput()
              .deleteLink(getLink().getName());
                        //.getApplication()
      //.getReceiver()
      //.receive(new LinkDeleteCommand(getLink().getName()));
   }

   private void menuShowContent()
   {
      MainWindow.getInfoFrame().showRefreshingContent(getLocationOnScreen().x + centerX, getLocationOnScreen().y + centerY, getLink().getOutput());
   }
   
    private void menuSaveData() {
        Object obj = getLink().getOutput().getValue();
        if (!(obj instanceof VNRegularField)) {
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(VisNow.get().getMainConfig().getUsableDataPath(FieldWriter.class)));
        FileNameExtensionFilter fieldFilter = new FileNameExtensionFilter("VisNow Field files", "vnf", "VNF");
        fileChooser.setFileFilter(fieldFilter);

        int returnVal = fileChooser.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String path = VNFileChooser.filenameWithExtenstionAddedIfNecessary(fileChooser.getSelectedFile(), new FileNameExtensionFilter("", "vnf", "VNF"));
            VisNow.get().getMainConfig().setLastDataPath(path, FieldWriter.class);
            boolean result = new VisNowRegularFieldWriterCore(((VNRegularField)obj).getField(), 
                                                              new pl.edu.icm.visnow.lib.basic.writers.FieldWriter.Params(path, false, false)).writeField();
            if (result)
                VisNow.get().userMessageSend(new UserMessage("VisNow", "", "Field successfully written", "", Level.INFO));
            else
                VisNow.get().userMessageSend(new UserMessage("VisNow", "", "Error writing field", "", Level.ERROR));            
        }
    }
}
