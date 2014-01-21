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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JPopupMenu.Separator;
import javax.swing.filechooser.FileNameExtensionFilter;
import pl.edu.icm.visnow.engine.core.Input;
import pl.edu.icm.visnow.engine.core.Output;
import pl.edu.icm.visnow.engine.element.ElementSaturationListener;
import pl.edu.icm.visnow.engine.main.InputSaturation;
import pl.edu.icm.visnow.engine.main.OutputSaturation;
import pl.edu.icm.visnow.engine.main.Port;
import pl.edu.icm.visnow.lib.basic.writers.FieldWriter.FieldWriter;
import pl.edu.icm.visnow.lib.types.VNField;
import pl.edu.icm.visnow.lib.types.VNIrregularField;
import pl.edu.icm.visnow.lib.types.VNRegularField;
import pl.edu.icm.visnow.lib.utils.io.VisNowIrregularFieldWriterCore;
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
public class PortPanel extends javax.swing.JComponent
{

   //<editor-fold defaultstate="collapsed" desc=" PopupMenu ">
   private javax.swing.JMenuItem mbShowFullInfo;
   private javax.swing.JMenuItem mbShowContent;
   private javax.swing.JMenuItem mbSaveData;
   private javax.swing.JPopupMenu popupMenu;
   //</editor-fold>
   //<editor-fold defaultstate="collapsed" desc=" ModulePanel ">
   private ModulePanel modulePanel;

   public ModulePanel getModulePanel()
   {
      return modulePanel;
   }
   //</editor-fold>

   //<editor-fold defaultstate="collapsed" desc=" Coords ">
   public int getTotalX()
   {
      return getX() + getModulePanel().getX();
   }

   public int getTotalY()
   {
      return getY() + getModulePanel().getY();
   }

   public int getDX()
   {
      return 8;
   }

   public int getDY()
   {
      return (getPort().isInput()) ? 6 : 4;
   }
   //</editor-fold>
   //<editor-fold defaultstate="collapsed" desc=" Port ">
   private Port port;

   public Port getPort()
   {
      return port;
   }
   //</editor-fold>

   //<editor-fold defaultstate="collapsed" desc=" ShowMenu ">
   public void showMenu(Point p)
   {
      if(mbSaveData != null)
            mbSaveData.setEnabled(port != null && 
                                  port instanceof Output && 
                                  ((Output)port).getData() != null && 
                                  ((Output)port).getValue() instanceof VNField);
//                                  ((Output)port).getValue() instanceof VNRegularField);
      popupMenu.show(this, 10, 10);
   }
   //</editor-fold>

   //<editor-fold defaultstate="collapsed" desc=" [CONSTRUCTOR] ">
   /**
    * Creates new form PortPanel
    */
   public PortPanel(ModulePanel module, Port port)
   {

      this.modulePanel = module;
      this.port = port;

      popupMenu = new javax.swing.JPopupMenu();
      popupMenu.add(new Separator());
      mbShowFullInfo = new javax.swing.JMenuItem();
      mbShowFullInfo.setText("Show detailed info");
      mbShowFullInfo.addActionListener(new java.awt.event.ActionListener()
      {
         @Override
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
                MainWindow.getInfoFrame().showPortDetailedInfo(getLocationOnScreen().x, getLocationOnScreen().y, getPort());
         }
      });
      popupMenu.add(mbShowFullInfo);

      if (!this.port.isInput()) {
           mbShowContent = new javax.swing.JMenuItem();
           mbShowContent.setText("Show content");
           mbShowContent.addActionListener(new java.awt.event.ActionListener() {
               @Override
               public void actionPerformed(java.awt.event.ActionEvent evt) {
                   if (getPort() instanceof Output) {
                       MainWindow.getInfoFrame().showRefreshingContent(getLocationOnScreen().x, getLocationOnScreen().y, (Output) getPort());
                   }
               }
           });
           popupMenu.add(mbShowContent);

         popupMenu.add(new Separator());
         mbSaveData = new javax.swing.JMenuItem();
         mbSaveData.setText("Write field...");
         mbSaveData.addActionListener(new java.awt.event.ActionListener()
         {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
               if (getPort() instanceof Output)
               {
                  Object obj = ((Output) getPort()).getValue();
//                  if (!(obj instanceof VNField))
//                  {
//                     return;
//                  }
                  if (!(obj instanceof VNField))
                  {
                     return;
                  }

                  JFileChooser fileChooser = new JFileChooser();
                  fileChooser.setCurrentDirectory(new File(VisNow.get().getMainConfig().getUsableDataPath(FieldWriter.class)));
                  FileNameExtensionFilter fieldFilter = new FileNameExtensionFilter("VisNow Field files", "vnf", "VNF");
                  fileChooser.setFileFilter(fieldFilter);

                  int returnVal = fileChooser.showSaveDialog(null);
                  if (returnVal == JFileChooser.APPROVE_OPTION)
                  {
                     String path = VNFileChooser.filenameWithExtenstionAddedIfNecessary(fileChooser.getSelectedFile(), new FileNameExtensionFilter("", "vnf", "VNF"));
                     VisNow.get().getMainConfig().setLastDataPath(path, FieldWriter.class);
                     boolean result = false;
                     if (obj instanceof VNRegularField)
                        result = new VisNowRegularFieldWriterCore(((VNRegularField) obj).getField(),
                                new pl.edu.icm.visnow.lib.basic.writers.FieldWriter.Params(path, false, false)).writeField();
                     if (obj instanceof VNIrregularField)
                        result = new VisNowIrregularFieldWriterCore(((VNIrregularField) obj).getField(),
                                new pl.edu.icm.visnow.lib.basic.writers.FieldWriter.Params(path, false, false)).writeField();
                     if (result)
                        VisNow.get().userMessageSend(new UserMessage("VisNow", "", "Field successfully written", "", Level.INFO));
                     else
                        VisNow.get().userMessageSend(new UserMessage("VisNow", "", "Error writing field", "", Level.ERROR));
                  }
               }
            }
         });
           popupMenu.add(mbSaveData);

       }

      if (!this.port.isInput())
      {
         // TODO: add Attach menu;
         //popupMenu.add(module.getScenePanel().getTypeMenu().getMenu(this));
      }

      this.addMouseListener(new PortPanelMouseEvents(this));
      this.addMouseMotionListener(new PortPanelMouseEvents(this));
      port.addSaturationListener(new ElementSaturationListener()
      {
         @Override
         public void saturationChanged()
         {
            java.awt.EventQueue.invokeLater(new Runnable()
            {
               @Override
               public void run()
               {
                  repaint();
               }
            });
         }
      });
   }
   //</editor-fold>

   //<editor-fold defaultstate="collapsed" desc=" Paint ">
   @Override
   public void paint(Graphics g)
   {
      super.paint(g);
      Graphics2D gg = (Graphics2D) g;
      gg.addRenderingHints(VNSwingUtils.getHints());
      if (getPort().isInput())
      {
         gg.setColor(VNSwingUtils.typeColor(getPort().getType().getName()));
         if (((Input) getPort()).isNecessary())
            gg.fillPolygon(new int[]{0, 8, 9, 17}, new int[]{2, 11, 11, 2}, 4);
         else
            gg.fillPolygon(new int[]{0, 8, 9, 17}, new int[]{5, 11, 11, 5}, 4);

         if (((Input) getPort()).isNecessary() && ((Input) getPort()).getInputSaturation() != InputSaturation.ok)
         {
            switch (((Input) getPort()).getInputSaturation())
            {
            case notLinked:
               gg.setColor(VNSwingUtils.SATURATION_NOTLINKED);
               break;
            case noData:
               gg.setColor(VNSwingUtils.SATURATION_NODATA);
               break;
            case wrongData:
               gg.setColor(VNSwingUtils.SATURATION_WRONGDATA);
               break;
            }
            gg.drawLine(1, 2, 16, 2);
         }
         
      
      } else
      {
         gg.setColor(VNSwingUtils.typeColor(getPort().getType().getName()));
         if (((Output) getPort()).getOutputSaturation() == OutputSaturation.noData)
            gg.fillRect(0, 0, 17, 4);
         else
            gg.fillPolygon(new int[]{0, 0, 8, 9, 17, 17}, new int[]{0, 3, 11, 11, 3, 0}, 6);

      }
   }
   
   private JMenu attachedMenu = null;

   public void setAttachMenu(JMenu menu)
   {

      if (attachedMenu != null)
         popupMenu.remove(attachedMenu);
      attachedMenu = menu;
      if (attachedMenu != null)
         popupMenu.add(menu, 0);

   }

   /**
    * @return the popupMenu
    */
   public javax.swing.JPopupMenu getPopupMenu()
   {
      return popupMenu;
   }
}
