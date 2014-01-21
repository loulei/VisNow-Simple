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

package pl.edu.icm.visnow.application.frames;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.HashMap;
import javax.swing.JPanel;
import org.w3c.dom.Node;
import pl.edu.icm.visnow.application.application.Application;
import pl.edu.icm.visnow.system.main.VisNow;
import pl.edu.icm.visnow.system.swing.split.FAreaMajor;
import pl.edu.icm.visnow.system.swing.split.FSplitListener;

/**
 *
 * @author gacek
 */
public class ApplicationFrame2 extends JPanel
{

   protected Frames frames;

   public Frames getFrames()
   {
      return frames;
   }

   public Application getApplication()
   {
      return frames.getApplication();
   }
   private FAreaMajor major;

   public String writeWindowXML()
   {
      return major.getXML();
   }

   public ApplicationFrame2(Frames frames)
   {
      this.frames = frames;
      this.setLayout(new BorderLayout());

      major = new FAreaMajor(true);
      this.add(major, BorderLayout.CENTER);

      Node xml = VisNow.get().getMainConfig().getWindowXML();
      HashMap<String, Component> map = new HashMap<String, Component>();
      map.put("Application", frames.getTitlePanel());
      map.put("Workspace", frames.getApplication().getArea().getPanel());
      map.put("Ports", frames.getModulePanel());
      map.put("UI", frames.getGuiPanel());
      map.put("Library", frames.getLibrariesPanel());


      //Vector<Node> majors = new Vector<Node>();
      //for(int i=0; i<xml.getChildNodes().getLength(); ++i) {
//            if(xml.getChildNodes().item(i).getNodeName().equalsIgnoreCase("major"))
//                majors.add(xml.getChildNodes().item(i));
      //      }
//        xml = majors.firstElement();
//        System.out.println(xml.getNodeName());

//        major.addBox("Application", frames.getTitlePanel());
//        major.addBox("Workspace",frames.getApplication().getArea().getPanel());

//        major.addBox(new FBox("Ports",frames.getModulePanel()),FAreaMajor.leftD);
//        ((FAreaSplit)major.getChild()).getSon().addBox(new FBox("UI",frames.getGuiPanel()));
//        ((FAreaSplit)major.getChild()).getSon().addBox(new FBox("Library",frames.getLibrariesPanel()),FAreaMajor.bottomD);


//        ((FAreaSplit)major.getChild()).setDividerLocation(250);
//        ((FAreaSplit)((FAreaSplit)major.getChild()).getSon()).setDividerLocation(400);
      major.useXML(xml, map);
      major.addInternalDropTarget(frames.getApplication().getArea().getPanel().getDropTarget());

      major.addSplitListener(new FSplitListener()
      {
         public void splitAction()
         {
            //System.out.println("SPLIT ACTION");
            VisNow.get().getMainConfig().setWinowXML(major.getXML());
         }
      });
   }

   public Component getAreaLocator()
   {
      return major;
   }
   /*


    protected Frames frames;
    public Frames getFrames() {return frames;}

    public Application getApplication() {return frames.getApplication();}

    public ApplicationFrame2(Frames frames) {
    this.frames = frames;
    initComponents();
    this.leftTabs.add(frames.getGuiPanel(), "UI");
    this.leftTabs.add(frames.getModulePanel(), "Ports");
    this.rightTabs.add(frames.getLibrariesPanel(), "Library");
    this.rightTabs.add(frames.getTitlePanel(), "Application");

    bigSplitPane.setRightComponent(frames.getApplication().getArea().getPanel());
    //mainPanel.add(frames.getScenePanel(), BorderLayout.CENTER);
    //        this.add(frames.getScenePanel().getProgress(), BorderLayout.SOUTH);
    //        SwingUtils.setConstantHeight(frames.getScenePanel().getProgress(), 15);
    }

    public JSplitPane getAreaLocator() {
    return bigSplitPane;
    }

    //public JPanel getMainPanel() {
    //    return mainPanel;
    //}

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

    mainPanel = new javax.swing.JPanel();
    bigSplitPane = new javax.swing.JSplitPane();
    jPanel1 = new javax.swing.JPanel();
    jSplitPane1 = new javax.swing.JSplitPane();
    leftTabs = new javax.swing.JTabbedPane();
    rightTabs = new javax.swing.JTabbedPane();

    mainPanel.setLayout(new java.awt.BorderLayout());

    setMinimumSize(new java.awt.Dimension(800, 500));
    setPreferredSize(new java.awt.Dimension(850, 610));
    setLayout(new java.awt.BorderLayout());

    bigSplitPane.setDividerLocation(520);
    bigSplitPane.setDividerSize(8);
    bigSplitPane.setMinimumSize(new java.awt.Dimension(900, 650));
    bigSplitPane.setOneTouchExpandable(true);
    bigSplitPane.setPreferredSize(new java.awt.Dimension(950, 650));

    jPanel1.setMinimumSize(new java.awt.Dimension(500, 490));
    jPanel1.setPreferredSize(new java.awt.Dimension(500, 610));

    jSplitPane1.setDividerLocation(250);
    jSplitPane1.setDividerSize(8);
    jSplitPane1.setOneTouchExpandable(true);

    leftTabs.setMinimumSize(new java.awt.Dimension(200, 480));
    leftTabs.setOpaque(true);
    leftTabs.setPreferredSize(new java.awt.Dimension(220, 600));
    jSplitPane1.setLeftComponent(leftTabs);

    rightTabs.setMinimumSize(new java.awt.Dimension(200, 480));
    rightTabs.setPreferredSize(new java.awt.Dimension(200, 600));
    jSplitPane1.setRightComponent(rightTabs);

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
    jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
    .addComponent(jSplitPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 519, Short.MAX_VALUE)
    );
    jPanel1Layout.setVerticalGroup(
    jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
    .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 702, Short.MAX_VALUE)
    );

    bigSplitPane.setLeftComponent(jPanel1);

    add(bigSplitPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>


    // Variables declaration - do not modify
    private javax.swing.JSplitPane bigSplitPane;
    private javax.swing.JPanel jPanel1;
    private JSplitPane jSplitPane1;
    private javax.swing.JTabbedPane leftTabs;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JTabbedPane rightTabs;
    // End of variables declaration

    */
}
