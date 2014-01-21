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

package pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import pl.edu.icm.visnow.geometries.viewer3d.Display3DPanel;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.DataProvider.DataProvider;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.CalculableParams;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.GeometryParams;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.TabbedUI.UITab;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.ViewPanels.OrthosliceViewPanel;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.ViewPanels.ViewPanel;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Views.DoubleViewHorizontal;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Views.DoubleViewVertical;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Views.QuadView;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;
import pl.edu.icm.visnow.system.main.VisNow;

/**
 * @author  Bartosz Borucki (babor@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public final class FieldDisplay3DFrame extends javax.swing.JFrame
{

   public static final int MODE_3D = 0;
   public static final int MODE_2D = 1;
   private int mode = MODE_3D;
   public static final int VIEWPORT_SETUP_QUAD = 0;
   public static final int VIEWPORT_SETUP_DOUBLE_HORIZ = 1;
   public static final int VIEWPORT_SETUP_DOUBLE_VERT = 2;
   public static final int VIEWPORT_SETUP_SINGLE = 3;
   private int currentSetupType = 0;
   private QuadView quadViewPanel = new QuadView();
   private DoubleViewHorizontal doubleViewHorizPanel = new DoubleViewHorizontal();
   private DoubleViewVertical doubleViewVertPanel = new DoubleViewVertical();
   private ViewPanel singleViewPanel = new ViewPanel();
   private ViewportContentMenu[] quadVpcms = new ViewportContentMenu[4];
   private JMenuItem[] quadPredefinedMIs = new JMenuItem[3];
   private ViewportContentMenu[] doubleHorizVpcms = new ViewportContentMenu[2];
   private JMenuItem[] doubleHorizPredefinedMIs = new JMenuItem[2];
   private ViewportContentMenu[] doubleVertVpcms = new ViewportContentMenu[2];
   private JMenuItem[] doubleVertPredefinedMIs = new JMenuItem[2];
   private ViewportContentMenu singleVpcm;
   private ViewsStorage viewsStorage;
   private FieldViewer3DManager viewsDataManager = null;
   private GlobalParams params = new GlobalParams();
   private DataProvider dp = new DataProvider();
   private boolean silent = false;
   private boolean simpleGUI = (VisNow.guiLevel == VisNow.SIMPLE_GUI);

   /** Creates new form FieldDisplay3DFrame */
   public FieldDisplay3DFrame()
   {
      viewsStorage = new ViewsStorage(params);
      initMenus();
      initComponents();
      setDefaultCloseOperation(HIDE_ON_CLOSE);
      setDefaultViewportSetup();
      updateGUI();

   }

   private void centerFrame()
   {
      Toolkit tk = Toolkit.getDefaultToolkit();
      Dimension ss = tk.getScreenSize();
      Dimension fs = this.getSize();
      int x = (int) (ss.getWidth() - fs.width) / 2;
      int y = (int) (ss.getHeight() - fs.height) / 2;
      this.setLocation(x, y);
   }

   public void updateGUI()
   {
      SwingInstancer.swingRunAndWait(new Runnable()
      {

            @Override
         public void run()
         {
            silent = true;

            if (toolbarEnabled)
            {
               resetButton.setEnabled(true);
               resetAxesButton.setEnabled(true);
               scalingModeCB.setEnabled(true);
               paintViewInfoButton.setEnabled(true);
               show3DPlanesButton.setEnabled(true);
               autoLocateDividersButton.setEnabled(true);
               modeCustomOrthoSliceButton.setEnabled(true);
               modeCustomSliceButton.setEnabled(true);
               modeOrthosliceButton.setEnabled(true);
               interpolationButton.setEnabled(true);
               sliceLinesModeCB.setEnabled(true);


               scalingModeCB.setSelectedIndex(params.getScalingMode());
               paintViewInfoButton.setSelected(params.isPaintViewInfo());
               show3DPlanesButton.setSelected(params.isPlanes3DVisible());
               sliceLinesModeCB.setSelectedIndex(params.getSliceLinesMode());

               switch (params.getMode())
               {
               case GlobalParams.MODE_ORTHOSLICES:
                  modeOrthosliceButton.setSelected(true);
                  interpolationButton.setEnabled(false);
                  show3DPlanesButton.setEnabled(false);
                  break;
               case GlobalParams.MODE_CUSTOMSLICE:
                  modeCustomSliceButton.setSelected(true);
                  interpolationButton.setEnabled(true);
                  show3DPlanesButton.setEnabled(true);
                  interpolationButton.setSelected(dp.getParams().isCustomPlaneInterpolation());
                  break;
               case GlobalParams.MODE_CUSTOMORTHOSLICES:
                  modeCustomOrthoSliceButton.setSelected(true);
                  interpolationButton.setEnabled(true);
                  show3DPlanesButton.setEnabled(true);
                  interpolationButton.setSelected(dp.getParams().isCustomOrthoPlanesInterpolation());
               }
            } else
            {
               resetButton.setEnabled(false);
               resetAxesButton.setEnabled(false);
               scalingModeCB.setEnabled(false);
               paintViewInfoButton.setEnabled(false);
               show3DPlanesButton.setEnabled(false);
               autoLocateDividersButton.setEnabled(false);
               modeCustomOrthoSliceButton.setEnabled(false);
               modeCustomSliceButton.setEnabled(false);
               modeOrthosliceButton.setEnabled(false);
               interpolationButton.setEnabled(false);
               sliceLinesModeCB.setEnabled(false);
            }

            show3DPlanesButton.setVisible(!simpleGUI);
            autoLocateDividersButton.setVisible(!simpleGUI);
            modeOrthosliceButton.setVisible(!simpleGUI);
            modeCustomOrthoSliceButton.setVisible(!simpleGUI);
            modeCustomSliceButton.setVisible(!simpleGUI);
            interpolationButton.setVisible(!simpleGUI);            
            viewMenu.setVisible(!simpleGUI);
            
            pointToolButton.setSelected(true);
            silent = false;
         }
      });
   }

   public final void setDefaultViewportSetup()
   {
      switch(mode) {
          case MODE_3D:
              setViewportSetup(VIEWPORT_SETUP_QUAD);
              break;              
          case MODE_2D:
              setSingleSetup(ViewPanel.VIEW_2D);
              params.setScalingMode(OrthosliceViewPanel.SCALING_AUTO);
              break;              
      }
   }

   public synchronized void setViewportConents(int viewport, int contentType)
   {
      if (viewport < 0)
         return;

      if (contentType < 0 || contentType > 12)
      {
         return;
      }

      switch (currentSetupType)
      {
      case VIEWPORT_SETUP_QUAD:
         if (viewport > 3)
            return;
         if (quadViewPanel.getViewPanel(viewport).getType() == contentType)
            return;
         //wyrejestrowac ViewPanel z listenerow
         quadViewPanel.setViewPanel(viewport, viewsStorage.getView(contentType));
         quadVpcms[viewport].setSelected(contentType, true);

         //wyrejestrowac ViewPanel z listenerow
         break;
      case VIEWPORT_SETUP_DOUBLE_HORIZ:
         if (viewport > 1)
            return;
         if (doubleViewHorizPanel.getViewPanel(viewport).getType() == contentType)
            return;
         //wyrejestrowac ViewPanel z listenerow
         doubleViewHorizPanel.setViewPanel(viewport, viewsStorage.getView(contentType));
         doubleHorizVpcms[viewport].setSelected(contentType, true);
         //wyrejestrowac ViewPanel z listenerow
         break;
      case VIEWPORT_SETUP_DOUBLE_VERT:
         if (viewport > 1)
            return;
         if (doubleViewVertPanel.getViewPanel(viewport).getType() == contentType)
            return;
         //wyrejestrowac ViewPanel z listenerow
         doubleViewVertPanel.setViewPanel(viewport, viewsStorage.getView(contentType));
         doubleVertVpcms[viewport].setSelected(contentType, true);
         //wyrejestrowac ViewPanel z listenerow
         break;
      case VIEWPORT_SETUP_SINGLE:
         if (viewport > 0)
            return;
         if (singleViewPanel.getType() == contentType)
            return;
         //wyrejestrowac ViewPanel z listenerow
         singleViewPanel.preRemove();
         displayPanel.remove(singleViewPanel);
         singleViewPanel.setInUse(false);
         singleViewPanel = viewsStorage.getView(contentType);
         singleViewPanel.setInUse(true);
         displayPanel.add(singleViewPanel, BorderLayout.CENTER);
         singleViewPanel.postAdd();
         displayPanel.revalidate();
         displayPanel.repaint();
         singleVpcm.setSelected(contentType, true);
         //zarejstrowac nowy ViewPanel w listenerach
         break;
      }
   }

   public void setViewportSetup(int type)
   {
      switch (type)
      {
      case VIEWPORT_SETUP_QUAD:
         quadViewRBMI.setSelected(true);
         setDefaultQuadSetup();
         break;
      case VIEWPORT_SETUP_DOUBLE_HORIZ:
         doubleViewHorizRBMI.setSelected(true);
         setDefaultDoubleHorizSetup();
         break;
      case VIEWPORT_SETUP_DOUBLE_VERT:
         doubleViewVertRBMI.setSelected(true);
         setDefaultDoubleVertSetup();
         break;
      case VIEWPORT_SETUP_SINGLE:
         singleViewRBMI.setSelected(true);
         setDefaultSingleSetup();
         break;
      default:
         return;
      }
      repaint();
   }

   //public void setQuadSetup(int topRightView, int bottomRightView, int bottomLeftView, int topLeftView) {
   public void setQuadSetup(int topLeftView, int topRightView, int bottomLeftView, int bottomRightView)
   {
      switch (currentSetupType)
      {
      case VIEWPORT_SETUP_QUAD:
         quadViewPanel.reset();
         break;
      case VIEWPORT_SETUP_DOUBLE_HORIZ:
         doubleViewHorizPanel.reset();
         break;
      case VIEWPORT_SETUP_DOUBLE_VERT:
         doubleViewVertPanel.reset();
         break;
      case VIEWPORT_SETUP_SINGLE:
         singleViewPanel.preRemove();
         break;
      }

//        quadViewPanel.reset();
//        doubleViewHorizPanel.reset();
//        doubleViewVertPanel.reset();
//        singleViewPanel.preRemove();

      currentSetupType = VIEWPORT_SETUP_QUAD;
      displayPanel.removeAll();
      displayPanel.add(quadViewPanel, java.awt.BorderLayout.CENTER);
      displayPanel.revalidate();
      displayPanel.repaint();

      quadVpcms[0].setSelected(topLeftView, true);
      quadVpcms[1].setSelected(topRightView, true);
      quadVpcms[2].setSelected(bottomLeftView, true);
      quadVpcms[3].setSelected(bottomRightView, true);
      quadViewRBMI.setSelected(true);


      customSetupMenu.removeAll();
      for (int i = 0; i < 4; i++)
      {
         customSetupMenu.add(quadVpcms[i]);
      }

      predefinedSetupsMenu.removeAll();
      for (int i = 0; i < quadPredefinedMIs.length; i++)
      {
         predefinedSetupsMenu.add(quadPredefinedMIs[i]);
      }
      predefinedSetupsMenu.add(predefinedAllEmpty);

      setViewportConents(QuadView.VIEWPORT_TOP_LEFT, topLeftView);
      setViewportConents(QuadView.VIEWPORT_TOP_RIGHT, topRightView);
      setViewportConents(QuadView.VIEWPORT_BOTTOM_LEFT, bottomLeftView);
      setViewportConents(QuadView.VIEWPORT_BOTTOM_RIGHT, bottomRightView);
   }

   public void setDoubleHorizSetup(int leftView, int rightView)
   {
      switch (currentSetupType)
      {
      case VIEWPORT_SETUP_QUAD:
         quadViewPanel.reset();
         break;
      case VIEWPORT_SETUP_DOUBLE_HORIZ:
         doubleViewHorizPanel.reset();
         break;
      case VIEWPORT_SETUP_DOUBLE_VERT:
         doubleViewVertPanel.reset();
         break;
      case VIEWPORT_SETUP_SINGLE:
         singleViewPanel.preRemove();
         break;
      }

//        quadViewPanel.reset();
//        doubleViewHorizPanel.reset();
//        doubleViewVertPanel.reset();
//        singleViewPanel.preRemove();

      currentSetupType = VIEWPORT_SETUP_DOUBLE_HORIZ;
      displayPanel.removeAll();
      displayPanel.add(doubleViewHorizPanel, java.awt.BorderLayout.CENTER);
      displayPanel.revalidate();
      displayPanel.repaint();

      customSetupMenu.removeAll();
      doubleHorizVpcms[0].setSelected(leftView, true);
      doubleHorizVpcms[1].setSelected(rightView, true);
      for (int i = 0; i < 2; i++)
      {
         customSetupMenu.add(doubleHorizVpcms[i]);
      }
      doubleViewHorizRBMI.setSelected(true);

      predefinedSetupsMenu.removeAll();
      for (int i = 0; i < doubleHorizPredefinedMIs.length; i++)
      {
         predefinedSetupsMenu.add(doubleHorizPredefinedMIs[i]);
      }
      predefinedSetupsMenu.add(predefinedAllEmpty);


      setViewportConents(DoubleViewHorizontal.VIEWPORT_LEFT, leftView);
      setViewportConents(DoubleViewHorizontal.VIEWPORT_RIGHT, rightView);
   }

   public void setDoubleVertSetup(int topView, int bottomView)
   {
      switch (currentSetupType)
      {
      case VIEWPORT_SETUP_QUAD:
         quadViewPanel.reset();
         break;
      case VIEWPORT_SETUP_DOUBLE_HORIZ:
         doubleViewHorizPanel.reset();
         break;
      case VIEWPORT_SETUP_DOUBLE_VERT:
         doubleViewVertPanel.reset();
         break;
      case VIEWPORT_SETUP_SINGLE:
         singleViewPanel.preRemove();
         break;
      }

//        quadViewPanel.reset();
//        doubleViewHorizPanel.reset();
//        doubleViewVertPanel.reset();
//        singleViewPanel.preRemove();

      currentSetupType = VIEWPORT_SETUP_DOUBLE_VERT;
      displayPanel.removeAll();
      displayPanel.add(doubleViewVertPanel, java.awt.BorderLayout.CENTER);
      displayPanel.revalidate();
      displayPanel.repaint();

      customSetupMenu.removeAll();
      doubleVertVpcms[0].setSelected(topView, true);
      doubleVertVpcms[1].setSelected(bottomView, true);
      for (int i = 0; i < 2; i++)
      {
         customSetupMenu.add(doubleVertVpcms[i]);
      }
      doubleViewVertRBMI.setSelected(true);

      predefinedSetupsMenu.removeAll();
      for (int i = 0; i < doubleVertPredefinedMIs.length; i++)
      {
         predefinedSetupsMenu.add(doubleVertPredefinedMIs[i]);
      }
      predefinedSetupsMenu.add(predefinedAllEmpty);

      setViewportConents(DoubleViewVertical.VIEWPORT_TOP, topView);
      setViewportConents(DoubleViewVertical.VIEWPORT_BOTTOM, bottomView);
   }

   public void setSingleSetup(int view)
   {
      switch (currentSetupType)
      {
      case VIEWPORT_SETUP_QUAD:
         quadViewPanel.reset();
         break;
      case VIEWPORT_SETUP_DOUBLE_HORIZ:
         doubleViewHorizPanel.reset();
         break;
      case VIEWPORT_SETUP_DOUBLE_VERT:
         doubleViewVertPanel.reset();
         break;
      case VIEWPORT_SETUP_SINGLE:
         singleViewPanel.preRemove();
         break;
      }

//        quadViewPanel.reset();
//        doubleViewHorizPanel.reset();
//        doubleViewVertPanel.reset();

      currentSetupType = VIEWPORT_SETUP_SINGLE;
      displayPanel.removeAll();
      displayPanel.add(singleViewPanel, java.awt.BorderLayout.CENTER);
      displayPanel.revalidate();
      displayPanel.repaint();

      customSetupMenu.removeAll();
      singleVpcm.setSelected(view, true);
      customSetupMenu.add(singleVpcm);
      singleViewRBMI.setSelected(true);
      predefinedSetupsMenu.removeAll();
      predefinedSetupsMenu.add(predefinedAllEmpty);

      setViewportConents(0, view);
   }

   public void setDefaultQuadSetup()
   {
      setQuadSetup(ViewPanel.VIEW_SLICE_J, ViewPanel.VIEW_SLICE_I, ViewPanel.VIEW_SLICE_K, ViewPanel.VIEW_3D);
      quadViewPanel.autoDividersLocation();
      params.setMode(GlobalParams.MODE_ORTHOSLICES);
      params.setScalingMode(OrthosliceViewPanel.SCALING_EXTERNAL);
      interpolationButton.setEnabled(false);
      
   }

   public void setDefaultDoubleHorizSetup()
   {
      setDoubleHorizSetup(ViewPanel.VIEW_SLICE_J, ViewPanel.VIEW_SLICE_I);
      doubleViewHorizPanel.centerDivider();
   }

   public void setDefaultDoubleVertSetup()
   {
      setDoubleVertSetup(ViewPanel.VIEW_SLICE_J, ViewPanel.VIEW_SLICE_K);
      doubleViewVertPanel.centerDivider();
   }

   public void setDefaultSingleSetup()
   {
      setSingleSetup(ViewPanel.VIEW_SLICE_K);
   }

   private void initMenus()
   {
      quadVpcms[0] = new ViewportContentMenu(this, "Top Left Viewport", 0, 0);
      quadVpcms[1] = new ViewportContentMenu(this, "Top Right Viewport", 1, 0);
      quadVpcms[2] = new ViewportContentMenu(this, "Bottom Left Viewport", 2, 0);
      quadVpcms[3] = new ViewportContentMenu(this, "Bottom Right Viewport", 3, 0);

      doubleHorizVpcms[0] = new ViewportContentMenu(this, "Left Viewport", 0, 0);
      doubleHorizVpcms[1] = new ViewportContentMenu(this, "Right Viewport", 1, 0);

      doubleVertVpcms[0] = new ViewportContentMenu(this, "Top Viewport", 0, 0);
      doubleVertVpcms[1] = new ViewportContentMenu(this, "Bottom Viewport", 1, 0);

      singleVpcm = new ViewportContentMenu(this, "Main Viewport", 0, 0);

      quadPredefinedMIs[0] = new JMenuItem("XZ-YZ-XY-3D (default)");
      quadPredefinedMIs[0].addActionListener(new ActionListener()
      {

            @Override
         public void actionPerformed(ActionEvent e)
         {
            setQuadSetup(ViewPanel.VIEW_SLICE_J, ViewPanel.VIEW_SLICE_I, ViewPanel.VIEW_SLICE_K, ViewPanel.VIEW_3D);
         }
      });
      quadPredefinedMIs[1] = new JMenuItem("XY-ZY-XZ-3D");
      quadPredefinedMIs[1].addActionListener(new ActionListener()
      {

            @Override
         public void actionPerformed(ActionEvent e)
         {
            setQuadSetup(ViewPanel.VIEW_SLICE_K, ViewPanel.VIEW_SLICE_I_TRANS, ViewPanel.VIEW_SLICE_J, ViewPanel.VIEW_3D);
         }
      });
      quadPredefinedMIs[2] = new JMenuItem("XZ-YZ-XY-CS");
      quadPredefinedMIs[2].addActionListener(new ActionListener()
      {

            @Override
         public void actionPerformed(ActionEvent e)
         {
            setQuadSetup(ViewPanel.VIEW_SLICE_J, ViewPanel.VIEW_SLICE_I, ViewPanel.VIEW_SLICE_K, ViewPanel.VIEW_SLICE_CUSTOM);
         }
      });


      doubleHorizPredefinedMIs[0] = new JMenuItem("XZ-YZ (default)");
      doubleHorizPredefinedMIs[0].addActionListener(new ActionListener()
      {

            @Override
         public void actionPerformed(ActionEvent e)
         {
            setDoubleHorizSetup(ViewPanel.VIEW_SLICE_J, ViewPanel.VIEW_SLICE_I);
         }
      });
      doubleHorizPredefinedMIs[1] = new JMenuItem("XY-ZY");
      doubleHorizPredefinedMIs[1].addActionListener(new ActionListener()
      {

            @Override
         public void actionPerformed(ActionEvent e)
         {
            setDoubleHorizSetup(ViewPanel.VIEW_SLICE_K, ViewPanel.VIEW_SLICE_I_TRANS);
         }
      });

      doubleVertPredefinedMIs[0] = new JMenuItem("XZ-XY (default)");
      doubleVertPredefinedMIs[0].addActionListener(new ActionListener()
      {

            @Override
         public void actionPerformed(ActionEvent e)
         {
            setDoubleVertSetup(ViewPanel.VIEW_SLICE_J, ViewPanel.VIEW_SLICE_K);
         }
      });
      doubleVertPredefinedMIs[1] = new JMenuItem("XY-XZ");
      doubleVertPredefinedMIs[1].addActionListener(new ActionListener()
      {

            @Override
         public void actionPerformed(ActionEvent e)
         {
            setDoubleVertSetup(ViewPanel.VIEW_SLICE_K, ViewPanel.VIEW_SLICE_J);
         }
      });

   }

   /** This method is called from within the constructor to
    * initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is
    * always regenerated by the Form Editor.
    */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        viewSetupButtonGroup = new javax.swing.ButtonGroup();
        modeButtonGroup = new javax.swing.ButtonGroup();
        graphicToolsGroup = new javax.swing.ButtonGroup();
        tabbedUI = new pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.TabbedUI.TabbedUI();
        rightPanel = new javax.swing.JPanel();
        displayToolbar = new javax.swing.JToolBar();
        resetButton = new javax.swing.JButton();
        resetAxesButton = new javax.swing.JButton();
        scalingLabel = new javax.swing.JLabel();
        scalingModeCB = new javax.swing.JComboBox();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        paintViewInfoButton = new javax.swing.JToggleButton();
        show3DPlanesButton = new javax.swing.JToggleButton();
        autoLocateDividersButton = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        modeOrthosliceButton = new javax.swing.JToggleButton();
        modeCustomSliceButton = new javax.swing.JToggleButton();
        modeCustomOrthoSliceButton = new javax.swing.JToggleButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        interpolationButton = new javax.swing.JToggleButton();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        sliceLinesLabel = new javax.swing.JLabel();
        sliceLinesModeCB = new javax.swing.JComboBox();
        jSeparator7 = new javax.swing.JToolBar.Separator();
        displayPanel = new javax.swing.JPanel();
        graphicToolbar = new javax.swing.JToolBar();
        pointToolButton = new javax.swing.JToggleButton();
        lineToolButton = new javax.swing.JToggleButton();
        centerPointToolButton = new javax.swing.JToggleButton();
        radiusToolButton = new javax.swing.JToggleButton();
        diameterToolButton = new javax.swing.JToggleButton();
        polylineToolButton = new javax.swing.JToggleButton();
        polygonToolButton = new javax.swing.JToggleButton();
        angleToolButton = new javax.swing.JToggleButton();
        statusBar = new javax.swing.JPanel();
        leftStatusLabel = new javax.swing.JLabel();
        rightStatusLabel = new javax.swing.JLabel();
        operationPanel = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        closeItem = new javax.swing.JMenuItem();
        viewMenu = new javax.swing.JMenu();
        viewSetupMenu = new javax.swing.JMenu();
        quadViewRBMI = new javax.swing.JRadioButtonMenuItem();
        doubleViewHorizRBMI = new javax.swing.JRadioButtonMenuItem();
        doubleViewVertRBMI = new javax.swing.JRadioButtonMenuItem();
        singleViewRBMI = new javax.swing.JRadioButtonMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        viewportsContents = new javax.swing.JMenu();
        predefinedSetupsMenu = new javax.swing.JMenu();
        predefinedAllEmpty = new javax.swing.JMenuItem();
        customSetupMenu = new javax.swing.JMenu();
        jSeparator2 = new javax.swing.JSeparator();
        resetViewMI = new javax.swing.JMenuItem();

        setTitle("Field Viewer 3D");
        setIconImage(new ImageIcon(getClass().getResource("/pl/edu/icm/visnow/gui/icons/big/visnow.png")).getImage());
        setMinimumSize(new java.awt.Dimension(724, 550));
        getContentPane().setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 740;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        getContentPane().add(tabbedUI, gridBagConstraints);

        rightPanel.setLayout(new java.awt.GridBagLayout());

        displayToolbar.setFloatable(false);
        displayToolbar.setRollover(true);
        displayToolbar.setMargin(new java.awt.Insets(2, 2, 2, 2));
        displayToolbar.setMinimumSize(new java.awt.Dimension(18, 34));
        displayToolbar.setPreferredSize(new java.awt.Dimension(18, 34));

        resetButton.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        resetButton.setText("Reset view");
        resetButton.setToolTipText("Reset viewports to defaults");
        resetButton.setFocusable(false);
        resetButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        resetButton.setMaximumSize(new java.awt.Dimension(80, 28));
        resetButton.setMinimumSize(new java.awt.Dimension(80, 28));
        resetButton.setPreferredSize(new java.awt.Dimension(80, 28));
        resetButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });
        displayToolbar.add(resetButton);

        resetAxesButton.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        resetAxesButton.setText("Reset axes");
        resetAxesButton.setToolTipText("Reset viewports to defaults");
        resetAxesButton.setFocusable(false);
        resetAxesButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        resetAxesButton.setMaximumSize(new java.awt.Dimension(80, 28));
        resetAxesButton.setMinimumSize(new java.awt.Dimension(80, 28));
        resetAxesButton.setPreferredSize(new java.awt.Dimension(80, 28));
        resetAxesButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        resetAxesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetAxesButtonActionPerformed(evt);
            }
        });
        displayToolbar.add(resetAxesButton);

        scalingLabel.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        scalingLabel.setText("Scaling:");
        scalingLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 5, 1, 1));
        displayToolbar.add(scalingLabel);

        scalingModeCB.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        scalingModeCB.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Global", "Auto", "Manual" }));
        scalingModeCB.setMaximumSize(new java.awt.Dimension(70, 20));
        scalingModeCB.setMinimumSize(new java.awt.Dimension(70, 20));
        scalingModeCB.setPreferredSize(new java.awt.Dimension(70, 20));
        scalingModeCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scalingModeCBActionPerformed(evt);
            }
        });
        displayToolbar.add(scalingModeCB);
        displayToolbar.add(jSeparator5);

        paintViewInfoButton.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        paintViewInfoButton.setText("Views info");
        paintViewInfoButton.setToolTipText("Paint viewport info");
        paintViewInfoButton.setFocusable(false);
        paintViewInfoButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        paintViewInfoButton.setMaximumSize(new java.awt.Dimension(65, 28));
        paintViewInfoButton.setMinimumSize(new java.awt.Dimension(65, 28));
        paintViewInfoButton.setPreferredSize(new java.awt.Dimension(65, 28));
        paintViewInfoButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        paintViewInfoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paintViewInfoButtonActionPerformed(evt);
            }
        });
        displayToolbar.add(paintViewInfoButton);

        show3DPlanesButton.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        show3DPlanesButton.setText("3D planes");
        show3DPlanesButton.setToolTipText("Paint viewport info");
        show3DPlanesButton.setFocusable(false);
        show3DPlanesButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        show3DPlanesButton.setMaximumSize(new java.awt.Dimension(65, 28));
        show3DPlanesButton.setMinimumSize(new java.awt.Dimension(65, 28));
        show3DPlanesButton.setPreferredSize(new java.awt.Dimension(65, 28));
        show3DPlanesButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        show3DPlanesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                show3DPlanesButtonActionPerformed(evt);
            }
        });
        displayToolbar.add(show3DPlanesButton);

        autoLocateDividersButton.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        autoLocateDividersButton.setText("Divide");
        autoLocateDividersButton.setToolTipText("Auto localize dividers");
        autoLocateDividersButton.setFocusable(false);
        autoLocateDividersButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        autoLocateDividersButton.setMaximumSize(new java.awt.Dimension(45, 28));
        autoLocateDividersButton.setMinimumSize(new java.awt.Dimension(45, 28));
        autoLocateDividersButton.setPreferredSize(new java.awt.Dimension(45, 28));
        autoLocateDividersButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        autoLocateDividersButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoLocateDividersButtonActionPerformed(evt);
            }
        });
        displayToolbar.add(autoLocateDividersButton);

        jSeparator3.setSeparatorSize(new java.awt.Dimension(10, 28));
        displayToolbar.add(jSeparator3);

        modeButtonGroup.add(modeOrthosliceButton);
        modeOrthosliceButton.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        modeOrthosliceButton.setText("Ortho");
        modeOrthosliceButton.setToolTipText("Orthoslice mode");
        modeOrthosliceButton.setFocusable(false);
        modeOrthosliceButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        modeOrthosliceButton.setMaximumSize(new java.awt.Dimension(40, 28));
        modeOrthosliceButton.setMinimumSize(new java.awt.Dimension(40, 28));
        modeOrthosliceButton.setPreferredSize(new java.awt.Dimension(40, 28));
        modeOrthosliceButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        modeOrthosliceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modeOrthosliceButtonActionPerformed(evt);
            }
        });
        displayToolbar.add(modeOrthosliceButton);

        modeButtonGroup.add(modeCustomSliceButton);
        modeCustomSliceButton.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        modeCustomSliceButton.setText("Custom");
        modeCustomSliceButton.setToolTipText("Custom slice mode");
        modeCustomSliceButton.setFocusable(false);
        modeCustomSliceButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        modeCustomSliceButton.setMaximumSize(new java.awt.Dimension(50, 28));
        modeCustomSliceButton.setMinimumSize(new java.awt.Dimension(50, 28));
        modeCustomSliceButton.setPreferredSize(new java.awt.Dimension(50, 28));
        modeCustomSliceButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        modeCustomSliceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modeCustomSliceButtonActionPerformed(evt);
            }
        });
        displayToolbar.add(modeCustomSliceButton);

        modeButtonGroup.add(modeCustomOrthoSliceButton);
        modeCustomOrthoSliceButton.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        modeCustomOrthoSliceButton.setText("CustomOrtho");
        modeCustomOrthoSliceButton.setToolTipText("Custom slice mode");
        modeCustomOrthoSliceButton.setFocusable(false);
        modeCustomOrthoSliceButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        modeCustomOrthoSliceButton.setMaximumSize(new java.awt.Dimension(90, 28));
        modeCustomOrthoSliceButton.setMinimumSize(new java.awt.Dimension(90, 28));
        modeCustomOrthoSliceButton.setPreferredSize(new java.awt.Dimension(90, 28));
        modeCustomOrthoSliceButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        modeCustomOrthoSliceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modeCustomOrthoSliceButtonActionPerformed(evt);
            }
        });
        displayToolbar.add(modeCustomOrthoSliceButton);

        jSeparator4.setSeparatorSize(new java.awt.Dimension(10, 28));
        displayToolbar.add(jSeparator4);

        interpolationButton.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        interpolationButton.setSelected(true);
        interpolationButton.setText("Interpolation");
        interpolationButton.setToolTipText("Custom slice interpolation");
        interpolationButton.setFocusable(false);
        interpolationButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        interpolationButton.setMaximumSize(new java.awt.Dimension(75, 28));
        interpolationButton.setMinimumSize(new java.awt.Dimension(75, 28));
        interpolationButton.setPreferredSize(new java.awt.Dimension(75, 28));
        interpolationButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        interpolationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                interpolationButtonActionPerformed(evt);
            }
        });
        displayToolbar.add(interpolationButton);
        displayToolbar.add(jSeparator6);

        sliceLinesLabel.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        sliceLinesLabel.setText("Axes:");
        sliceLinesLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 5, 1, 1));
        displayToolbar.add(sliceLinesLabel);

        sliceLinesModeCB.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        sliceLinesModeCB.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Colored", "Blue", "White", "Black", "None" }));
        sliceLinesModeCB.setMaximumSize(new java.awt.Dimension(70, 20));
        sliceLinesModeCB.setMinimumSize(new java.awt.Dimension(70, 20));
        sliceLinesModeCB.setPreferredSize(new java.awt.Dimension(70, 20));
        sliceLinesModeCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sliceLinesModeCBActionPerformed(evt);
            }
        });
        displayToolbar.add(sliceLinesModeCB);
        displayToolbar.add(jSeparator7);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        rightPanel.add(displayToolbar, gridBagConstraints);

        displayPanel.setMinimumSize(new java.awt.Dimension(480, 480));
        displayPanel.setPreferredSize(new java.awt.Dimension(600, 600));
        displayPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        rightPanel.add(displayPanel, gridBagConstraints);

        graphicToolbar.setFloatable(false);
        graphicToolbar.setRollover(true);

        graphicToolsGroup.add(pointToolButton);
        pointToolButton.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        pointToolButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pl/edu/icm/visnow/gui/icons/tools/icon_tool_point_16.png"))); // NOI18N
        pointToolButton.setSelected(true);
        pointToolButton.setToolTipText("Point Tool");
        pointToolButton.setFocusable(false);
        pointToolButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pointToolButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        pointToolButton.setMaximumSize(new java.awt.Dimension(24, 24));
        pointToolButton.setMinimumSize(new java.awt.Dimension(24, 24));
        pointToolButton.setPreferredSize(new java.awt.Dimension(24, 24));
        pointToolButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pointToolButtonActionPerformed(evt);
            }
        });
        graphicToolbar.add(pointToolButton);

        graphicToolsGroup.add(lineToolButton);
        lineToolButton.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        lineToolButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pl/edu/icm/visnow/gui/icons/tools/icon_tool_line_16.png"))); // NOI18N
        lineToolButton.setToolTipText("Line Tool");
        lineToolButton.setFocusable(false);
        lineToolButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lineToolButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        lineToolButton.setMaximumSize(new java.awt.Dimension(24, 24));
        lineToolButton.setMinimumSize(new java.awt.Dimension(24, 24));
        lineToolButton.setPreferredSize(new java.awt.Dimension(24, 24));
        lineToolButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        lineToolButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lineToolButtonActionPerformed(evt);
            }
        });
        graphicToolbar.add(lineToolButton);

        graphicToolsGroup.add(centerPointToolButton);
        centerPointToolButton.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        centerPointToolButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pl/edu/icm/visnow/gui/icons/tools/icon_tool_center_16.png"))); // NOI18N
        centerPointToolButton.setToolTipText("Center Point Tool");
        centerPointToolButton.setFocusable(false);
        centerPointToolButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        centerPointToolButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        centerPointToolButton.setMaximumSize(new java.awt.Dimension(24, 24));
        centerPointToolButton.setMinimumSize(new java.awt.Dimension(24, 24));
        centerPointToolButton.setPreferredSize(new java.awt.Dimension(24, 24));
        centerPointToolButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                centerPointToolButtonActionPerformed(evt);
            }
        });
        graphicToolbar.add(centerPointToolButton);

        graphicToolsGroup.add(radiusToolButton);
        radiusToolButton.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        radiusToolButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pl/edu/icm/visnow/gui/icons/tools/icon_tool_radius_16.png"))); // NOI18N
        radiusToolButton.setToolTipText("Radius Tool");
        radiusToolButton.setFocusable(false);
        radiusToolButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        radiusToolButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        radiusToolButton.setMaximumSize(new java.awt.Dimension(24, 24));
        radiusToolButton.setMinimumSize(new java.awt.Dimension(24, 24));
        radiusToolButton.setPreferredSize(new java.awt.Dimension(24, 24));
        radiusToolButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        radiusToolButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radiusToolButtonActionPerformed(evt);
            }
        });
        graphicToolbar.add(radiusToolButton);

        graphicToolsGroup.add(diameterToolButton);
        diameterToolButton.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        diameterToolButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pl/edu/icm/visnow/gui/icons/tools/icon_tool_diameter_16.png"))); // NOI18N
        diameterToolButton.setToolTipText("Diameter Tool");
        diameterToolButton.setFocusable(false);
        diameterToolButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        diameterToolButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        diameterToolButton.setMaximumSize(new java.awt.Dimension(24, 24));
        diameterToolButton.setMinimumSize(new java.awt.Dimension(24, 24));
        diameterToolButton.setPreferredSize(new java.awt.Dimension(24, 24));
        diameterToolButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        diameterToolButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                diameterToolButtonActionPerformed(evt);
            }
        });
        graphicToolbar.add(diameterToolButton);

        graphicToolsGroup.add(polylineToolButton);
        polylineToolButton.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        polylineToolButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pl/edu/icm/visnow/gui/icons/tools/icon_tool_polyline_16.png"))); // NOI18N
        polylineToolButton.setToolTipText("Polyline Tool");
        polylineToolButton.setFocusable(false);
        polylineToolButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        polylineToolButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        polylineToolButton.setMaximumSize(new java.awt.Dimension(24, 24));
        polylineToolButton.setMinimumSize(new java.awt.Dimension(24, 24));
        polylineToolButton.setPreferredSize(new java.awt.Dimension(24, 24));
        polylineToolButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        polylineToolButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                polylineToolButtonActionPerformed(evt);
            }
        });
        graphicToolbar.add(polylineToolButton);

        graphicToolsGroup.add(polygonToolButton);
        polygonToolButton.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        polygonToolButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pl/edu/icm/visnow/gui/icons/tools/icon_tool_polygon_16.png"))); // NOI18N
        polygonToolButton.setToolTipText("Polygon Tool");
        polygonToolButton.setFocusable(false);
        polygonToolButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        polygonToolButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        polygonToolButton.setMaximumSize(new java.awt.Dimension(24, 24));
        polygonToolButton.setMinimumSize(new java.awt.Dimension(24, 24));
        polygonToolButton.setPreferredSize(new java.awt.Dimension(24, 24));
        polygonToolButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        polygonToolButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                polygonToolButtonActionPerformed(evt);
            }
        });
        graphicToolbar.add(polygonToolButton);

        graphicToolsGroup.add(angleToolButton);
        angleToolButton.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        angleToolButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pl/edu/icm/visnow/gui/icons/tools/icon_tool_angle_16.png"))); // NOI18N
        angleToolButton.setToolTipText("Angle Tool");
        angleToolButton.setFocusable(false);
        angleToolButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        angleToolButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        angleToolButton.setMaximumSize(new java.awt.Dimension(24, 24));
        angleToolButton.setMinimumSize(new java.awt.Dimension(24, 24));
        angleToolButton.setPreferredSize(new java.awt.Dimension(24, 24));
        angleToolButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        angleToolButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                angleToolButtonActionPerformed(evt);
            }
        });
        graphicToolbar.add(angleToolButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 1.0;
        rightPanel.add(graphicToolbar, gridBagConstraints);

        statusBar.setBackground(java.awt.SystemColor.control);
        statusBar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        statusBar.setMinimumSize(new java.awt.Dimension(400, 20));
        statusBar.setPreferredSize(new java.awt.Dimension(600, 20));
        statusBar.setLayout(new java.awt.GridBagLayout());

        leftStatusLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        statusBar.add(leftStatusLabel, gridBagConstraints);

        rightStatusLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        statusBar.add(rightStatusLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 1.0;
        rightPanel.add(statusBar, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 437;
        gridBagConstraints.ipady = 185;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(rightPanel, gridBagConstraints);

        operationPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        getContentPane().add(operationPanel, gridBagConstraints);

        fileMenu.setMnemonic('h');
        fileMenu.setText("File");

        closeItem.setText("close");
        closeItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeItemActionPerformed(evt);
            }
        });
        fileMenu.add(closeItem);

        jMenuBar1.add(fileMenu);

        viewMenu.setText("View");

        viewSetupMenu.setText("View setup");

        viewSetupButtonGroup.add(quadViewRBMI);
        quadViewRBMI.setSelected(true);
        quadViewRBMI.setText("Quad View");
        quadViewRBMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quadViewRBMIActionPerformed(evt);
            }
        });
        viewSetupMenu.add(quadViewRBMI);

        viewSetupButtonGroup.add(doubleViewHorizRBMI);
        doubleViewHorizRBMI.setText("Double View Horizontal");
        doubleViewHorizRBMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doubleViewHorizRBMIActionPerformed(evt);
            }
        });
        viewSetupMenu.add(doubleViewHorizRBMI);

        viewSetupButtonGroup.add(doubleViewVertRBMI);
        doubleViewVertRBMI.setText("Double View Vertical");
        doubleViewVertRBMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doubleViewVertRBMIActionPerformed(evt);
            }
        });
        viewSetupMenu.add(doubleViewVertRBMI);

        viewSetupButtonGroup.add(singleViewRBMI);
        singleViewRBMI.setText("Single View");
        singleViewRBMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                singleViewRBMIActionPerformed(evt);
            }
        });
        viewSetupMenu.add(singleViewRBMI);

        viewMenu.add(viewSetupMenu);
        viewMenu.add(jSeparator1);

        viewportsContents.setText("Viewports contents");

        predefinedSetupsMenu.setText("Predefined setups");

        predefinedAllEmpty.setText("All empty");
        predefinedAllEmpty.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                predefinedAllEmptyActionPerformed(evt);
            }
        });
        predefinedSetupsMenu.add(predefinedAllEmpty);

        viewportsContents.add(predefinedSetupsMenu);

        customSetupMenu.setText("Custom setup");
        customSetupMenu.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                customSetupMenuStateChanged(evt);
            }
        });
        viewportsContents.add(customSetupMenu);

        viewMenu.add(viewportsContents);
        viewMenu.add(jSeparator2);

        resetViewMI.setText("Reset to defaults");
        resetViewMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetViewMIActionPerformed(evt);
            }
        });
        viewMenu.add(resetViewMI);

        jMenuBar1.add(viewMenu);

        setJMenuBar(jMenuBar1);

        setSize(new java.awt.Dimension(1177, 798));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void closeItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_closeItemActionPerformed
    {//GEN-HEADEREND:event_closeItemActionPerformed
       this.dispose();
}//GEN-LAST:event_closeItemActionPerformed

    private void quadViewRBMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quadViewRBMIActionPerformed
       setDefaultQuadSetup();
    }//GEN-LAST:event_quadViewRBMIActionPerformed

    private void doubleViewHorizRBMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doubleViewHorizRBMIActionPerformed
       setDefaultDoubleHorizSetup();
    }//GEN-LAST:event_doubleViewHorizRBMIActionPerformed

    private void doubleViewVertRBMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doubleViewVertRBMIActionPerformed
       setDefaultDoubleVertSetup();
    }//GEN-LAST:event_doubleViewVertRBMIActionPerformed

    private void singleViewRBMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_singleViewRBMIActionPerformed
       setDefaultSingleSetup();
    }//GEN-LAST:event_singleViewRBMIActionPerformed

    private void resetViewMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetViewMIActionPerformed
       setDefaultViewportSetup();
    }//GEN-LAST:event_resetViewMIActionPerformed

    private void customSetupMenuStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_customSetupMenuStateChanged
       Component[] tmp = customSetupMenu.getMenuComponents();
       for (int i = 0; i < tmp.length; i++)
       {
          if (tmp[i] instanceof ViewportContentMenu)
          {
             ((ViewportContentMenu) tmp[i]).updateDisabled();
          }

       }
    }//GEN-LAST:event_customSetupMenuStateChanged

    private void predefinedAllEmptyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_predefinedAllEmptyActionPerformed
       switch (currentSetupType)
       {
       case VIEWPORT_SETUP_QUAD:
          for (int i = 0; i < 4; i++)
          {
             setViewportConents(i, ViewPanel.VIEW_NONE);
             quadVpcms[i].setSelected(ViewPanel.VIEW_NONE, true);
          }
          break;
       case VIEWPORT_SETUP_DOUBLE_HORIZ:
          for (int i = 0; i < 2; i++)
          {
             setViewportConents(i, ViewPanel.VIEW_NONE);
             doubleHorizVpcms[i].setSelected(ViewPanel.VIEW_NONE, true);
          }
          break;
       case VIEWPORT_SETUP_DOUBLE_VERT:
          for (int i = 0; i < 2; i++)
          {
             setViewportConents(i, ViewPanel.VIEW_NONE);
             doubleVertVpcms[i].setSelected(ViewPanel.VIEW_NONE, true);
          }
          break;
       case VIEWPORT_SETUP_SINGLE:
          setViewportConents(0, ViewPanel.VIEW_NONE);
          singleVpcm.setSelected(ViewPanel.VIEW_NONE, true);
          break;
       }



    }//GEN-LAST:event_predefinedAllEmptyActionPerformed

    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
       setDefaultViewportSetup();
}//GEN-LAST:event_resetButtonActionPerformed

    private void paintViewInfoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_paintViewInfoButtonActionPerformed
       params.setPaintViewInfo(paintViewInfoButton.isSelected());
}//GEN-LAST:event_paintViewInfoButtonActionPerformed

    private void autoLocateDividersButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoLocateDividersButtonActionPerformed
       switch (currentSetupType)
       {
       case VIEWPORT_SETUP_QUAD:
          quadViewPanel.autoDividersLocation();
          break;
       case VIEWPORT_SETUP_DOUBLE_HORIZ:
          break;
       case VIEWPORT_SETUP_DOUBLE_VERT:
          break;
       case VIEWPORT_SETUP_SINGLE:
          break;
       }

}//GEN-LAST:event_autoLocateDividersButtonActionPerformed

    private void modeOrthosliceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modeOrthosliceButtonActionPerformed
       if (params.getMode() == GlobalParams.MODE_CUSTOMORTHOSLICES)
       {
          setDefaultQuadSetup();
       }
       params.setMode(GlobalParams.MODE_ORTHOSLICES);
       interpolationButton.setEnabled(false);

    }//GEN-LAST:event_modeOrthosliceButtonActionPerformed

    private void modeCustomSliceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modeCustomSliceButtonActionPerformed
       if (params.getMode() == GlobalParams.MODE_CUSTOMORTHOSLICES)
       {
          //setDefaultQuadSetup();
          setViewportSetup(VIEWPORT_SETUP_QUAD);
       }
       params.setMode(GlobalParams.MODE_CUSTOMSLICE);
       if (currentSetupType == VIEWPORT_SETUP_QUAD)
       {
          try
          {
             Thread.sleep(50);
          } catch (InterruptedException ex)
          {
          }
          setViewportConents(QuadView.VIEWPORT_BOTTOM_RIGHT, ViewPanel.VIEW_SLICE_CUSTOM);
       }
       interpolationButton.setEnabled(true);
    }//GEN-LAST:event_modeCustomSliceButtonActionPerformed

    private void interpolationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_interpolationButtonActionPerformed
       if (params.getMode() == GlobalParams.MODE_CUSTOMSLICE)
          dp.getParams().setCustomPlaneInterpolation(interpolationButton.isSelected());
       else if (params.getMode() == GlobalParams.MODE_CUSTOMORTHOSLICES)
          dp.getParams().setCustomOrthoPlanesInterpolation(interpolationButton.isSelected());

}//GEN-LAST:event_interpolationButtonActionPerformed

    private void scalingModeCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scalingModeCBActionPerformed
       if (!silent)
          params.setScalingMode(scalingModeCB.getSelectedIndex());
    }//GEN-LAST:event_scalingModeCBActionPerformed

    private void modeCustomOrthoSliceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modeCustomOrthoSliceButtonActionPerformed
       params.setMode(GlobalParams.MODE_CUSTOMORTHOSLICES);
       try
       {
          Thread.sleep(50);
       } catch (InterruptedException ex)
       {
       }
       setQuadSetup(ViewPanel.VIEW_SLICE_CUSTOM_ORTHO_1, ViewPanel.VIEW_SLICE_CUSTOM_ORTHO_0, ViewPanel.VIEW_SLICE_CUSTOM_ORTHO_2, ViewPanel.VIEW_3D);
       interpolationButton.setEnabled(true);
    }//GEN-LAST:event_modeCustomOrthoSliceButtonActionPerformed

    private void show3DPlanesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_show3DPlanesButtonActionPerformed
       params.setPlanes3DVisible(show3DPlanesButton.isSelected());
    }//GEN-LAST:event_show3DPlanesButtonActionPerformed

    private void resetAxesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetAxesButtonActionPerformed
       dp.centerSlices();
       //params.setScalingMode(OrthosliceViewPanel.SCALING_EXTERNAL);
    }//GEN-LAST:event_resetAxesButtonActionPerformed

    private void pointToolButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pointToolButtonActionPerformed
       if (params.isToolSelectionBlocking())
          return;
       params.setSelectedGeometryTool(GeometryToolsStorage.GEOMETRY_TOOL_POINT);
    }//GEN-LAST:event_pointToolButtonActionPerformed

    private void centerPointToolButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_centerPointToolButtonActionPerformed
       if (params.isToolSelectionBlocking())
          return;
       params.setSelectedGeometryTool(GeometryToolsStorage.GEOMETRY_TOOL_CENTER_POINT);
    }//GEN-LAST:event_centerPointToolButtonActionPerformed

    private void lineToolButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lineToolButtonActionPerformed
       if (params.isToolSelectionBlocking())
          return;
       params.setSelectedGeometryTool(GeometryToolsStorage.GEOMETRY_TOOL_LINE);
    }//GEN-LAST:event_lineToolButtonActionPerformed

    private void radiusToolButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radiusToolButtonActionPerformed
       if (params.isToolSelectionBlocking())
          return;
       params.setSelectedGeometryTool(GeometryToolsStorage.GEOMETRY_TOOL_RADIUS);
    }//GEN-LAST:event_radiusToolButtonActionPerformed

    private void diameterToolButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_diameterToolButtonActionPerformed
       if (params.isToolSelectionBlocking())
          return;
       params.setSelectedGeometryTool(GeometryToolsStorage.GEOMETRY_TOOL_DIAMETER);
    }//GEN-LAST:event_diameterToolButtonActionPerformed

    private void polylineToolButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_polylineToolButtonActionPerformed
       if (params.isToolSelectionBlocking())
          return;
       params.setSelectedGeometryTool(GeometryToolsStorage.GEOMETRY_TOOL_POLYLINE);
    }//GEN-LAST:event_polylineToolButtonActionPerformed

    private void polygonToolButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_polygonToolButtonActionPerformed
       if (params.isToolSelectionBlocking())
          return;
       params.setSelectedGeometryTool(GeometryToolsStorage.GEOMETRY_TOOL_POLYGON);
    }//GEN-LAST:event_polygonToolButtonActionPerformed

    private void angleToolButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_angleToolButtonActionPerformed
       if (params.isToolSelectionBlocking())
          return;
       params.setSelectedGeometryTool(GeometryToolsStorage.GEOMETRY_TOOL_ANGLE);
    }//GEN-LAST:event_angleToolButtonActionPerformed

    private void sliceLinesModeCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sliceLinesModeCBActionPerformed
       if (!silent)
          params.setSliceLinesMode(sliceLinesModeCB.getSelectedIndex());
    }//GEN-LAST:event_sliceLinesModeCBActionPerformed

   public void setScalingModeUI(int n)
   {
      this.silent = true;
      scalingModeCB.setSelectedIndex(n);
      this.silent = false;
   }

   public void setDataProvider(DataProvider dp)
   {
      this.dp = dp;
      if (viewsDataManager == null)
      {
         viewsDataManager = new FieldViewer3DManager(this.viewsStorage, this.dp, this);
      } else
      {
         viewsDataManager.setDataProvider(dp);
      }
      //viewsDataManager.updateAllViews();
      viewsDataManager.updateAllViewsInUse();
   }

   public void setGeometryParams(GeometryParams gparams)
   {
      if (viewsDataManager == null)
         return;

      viewsDataManager.setGeometryParams(gparams);
   }

   public void setCalculableParams(CalculableParams cparams)
   {
      if (viewsDataManager == null)
         return;

      viewsDataManager.setCalculableParams(cparams);
   }

   public Display3DPanel getDisplay3DPanel()
   {
      return viewsStorage.getDisplay3DView().getDisplay3DPanel();
   }

   public void insertUI(final JPanel panel, final String title)
   {
      SwingInstancer.swingRunAndWait(new Runnable()
      {
         @Override
         public void run()
         {
            tabbedUI.addUIToTab(panel, title, 0);
         }
      });
   }
   
   public void addUI(final JPanel panel, final String position)
   {
      SwingInstancer.swingRunAndWait(new Runnable()
      {
         @Override
         public void run()
         {
            tabbedUI.addUIToTab(panel, position);
         }
      });
   }

   public void setUIVisible(final String position, final boolean vis)
   {
//      SwingInstancer.swingRunAndWait(new Runnable()
//      {
//         @Override
//         public void run()
//         {
            tabbedUI.setUITabActive(position, vis);
//         }
//      });
   }
   
   public void removeAllUIPages()
   {
      SwingInstancer.swingRunAndWait(new Runnable()
      {
         @Override
         public void run()
         {
            tabbedUI.removeAllUITabs();
         }
      });
   }
   
   public void addUIPage(final JPanel panel, final String title)
   {
      SwingInstancer.swingRunAndWait(new Runnable()
      {

            @Override
         public void run()
         {
            tabbedUI.addUITab(new UITab(title, panel));
         }
      });
   }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton angleToolButton;
    private javax.swing.JButton autoLocateDividersButton;
    private javax.swing.JToggleButton centerPointToolButton;
    private javax.swing.JMenuItem closeItem;
    private javax.swing.JMenu customSetupMenu;
    private javax.swing.JToggleButton diameterToolButton;
    private javax.swing.JPanel displayPanel;
    private javax.swing.JToolBar displayToolbar;
    private javax.swing.JRadioButtonMenuItem doubleViewHorizRBMI;
    private javax.swing.JRadioButtonMenuItem doubleViewVertRBMI;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JToolBar graphicToolbar;
    private javax.swing.ButtonGroup graphicToolsGroup;
    private javax.swing.JToggleButton interpolationButton;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JToolBar.Separator jSeparator7;
    private javax.swing.JLabel leftStatusLabel;
    private javax.swing.JToggleButton lineToolButton;
    private javax.swing.ButtonGroup modeButtonGroup;
    private javax.swing.JToggleButton modeCustomOrthoSliceButton;
    private javax.swing.JToggleButton modeCustomSliceButton;
    private javax.swing.JToggleButton modeOrthosliceButton;
    private javax.swing.JPanel operationPanel;
    private javax.swing.JToggleButton paintViewInfoButton;
    private javax.swing.JToggleButton pointToolButton;
    private javax.swing.JToggleButton polygonToolButton;
    private javax.swing.JToggleButton polylineToolButton;
    private javax.swing.JMenuItem predefinedAllEmpty;
    private javax.swing.JMenu predefinedSetupsMenu;
    private javax.swing.JRadioButtonMenuItem quadViewRBMI;
    private javax.swing.JToggleButton radiusToolButton;
    private javax.swing.JButton resetAxesButton;
    private javax.swing.JButton resetButton;
    private javax.swing.JMenuItem resetViewMI;
    private javax.swing.JPanel rightPanel;
    private javax.swing.JLabel rightStatusLabel;
    private javax.swing.JLabel scalingLabel;
    private javax.swing.JComboBox scalingModeCB;
    private javax.swing.JToggleButton show3DPlanesButton;
    private javax.swing.JRadioButtonMenuItem singleViewRBMI;
    private javax.swing.JLabel sliceLinesLabel;
    private javax.swing.JComboBox sliceLinesModeCB;
    private javax.swing.JPanel statusBar;
    private pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.TabbedUI.TabbedUI tabbedUI;
    private javax.swing.JMenu viewMenu;
    private javax.swing.ButtonGroup viewSetupButtonGroup;
    private javax.swing.JMenu viewSetupMenu;
    private javax.swing.JMenu viewportsContents;
    // End of variables declaration//GEN-END:variables

   /**
    * @return the viewsStorage
    */
   public ViewsStorage getViewsStorage()
   {
      return viewsStorage;
   }

   /**
    * @return the params
    */
   public GlobalParams getParams()
   {
      return params;
   }

   public FieldViewer3DManager getManager()
   {
      return viewsDataManager;
   }

   public void setLeftStatusText(String text)
   {
      setLeftStatusText(text, Color.BLACK, java.awt.SystemColor.control);
   }

   public void setLeftStatusText(String text, Color textColor, Color bgColor)
   {
      leftStatusLabel.setText(text);
      leftStatusLabel.setForeground(textColor);
      statusBar.setBackground(bgColor);
   }

   public void setRightStatusText(String text)
   {
      setRightStatusText(text, Color.BLACK);
   }

   public void setRightStatusText(String text, Color textColor)
   {
      rightStatusLabel.setText(text);
      rightStatusLabel.setForeground(textColor);
   }

   public void locateDividers()
   {
      this.autoLocateDividersButtonActionPerformed(null);
   }

   void setSelectedGeometryTool(int selectedGeometryTool)
   {
      switch (selectedGeometryTool)
      {
      case GeometryToolsStorage.GEOMETRY_TOOL_POINT:
         pointToolButton.setSelected(true);
         break;
      case GeometryToolsStorage.GEOMETRY_TOOL_CENTER_POINT:
         centerPointToolButton.setSelected(true);
         break;
      case GeometryToolsStorage.GEOMETRY_TOOL_LINE:
         lineToolButton.setSelected(true);
         break;
      case GeometryToolsStorage.GEOMETRY_TOOL_RADIUS:
         radiusToolButton.setSelected(true);
         break;
      case GeometryToolsStorage.GEOMETRY_TOOL_DIAMETER:
         diameterToolButton.setSelected(true);
         break;
      case GeometryToolsStorage.GEOMETRY_TOOL_POLYLINE:
         polylineToolButton.setSelected(true);
         break;
      case GeometryToolsStorage.GEOMETRY_TOOL_POLYGON:
         polygonToolButton.setSelected(true);
         break;
      case GeometryToolsStorage.GEOMETRY_TOOL_ANGLE:
         angleToolButton.setSelected(true);
         break;
      default:
         pointToolButton.setSelected(false);
         centerPointToolButton.setSelected(false);
         lineToolButton.setSelected(false);
         radiusToolButton.setSelected(false);
         diameterToolButton.setSelected(false);
         polylineToolButton.setSelected(false);
         polygonToolButton.setSelected(false);
         angleToolButton.setSelected(false);

         pointToolButton.setEnabled(false);
         centerPointToolButton.setEnabled(false);
         lineToolButton.setEnabled(false);
         radiusToolButton.setEnabled(false);
         diameterToolButton.setEnabled(false);
         polylineToolButton.setEnabled(false);
         polygonToolButton.setEnabled(false);
         angleToolButton.setEnabled(false);
         return;
      }

      pointToolButton.setEnabled(!params.isToolSelectionBlocking() || pointToolButton.isSelected());
      centerPointToolButton.setEnabled(!params.isToolSelectionBlocking() || centerPointToolButton.isSelected());
      lineToolButton.setEnabled(!params.isToolSelectionBlocking() || lineToolButton.isSelected());
      radiusToolButton.setEnabled(!params.isToolSelectionBlocking() || radiusToolButton.isSelected());
      diameterToolButton.setEnabled(!params.isToolSelectionBlocking() || diameterToolButton.isSelected());
      polylineToolButton.setEnabled(!params.isToolSelectionBlocking() || polylineToolButton.isSelected());
      polygonToolButton.setEnabled(!params.isToolSelectionBlocking() || polygonToolButton.isSelected());
      angleToolButton.setEnabled(!params.isToolSelectionBlocking() || angleToolButton.isSelected());
   }
   private boolean toolbarEnabled = true;

   public void setToolbarEnabled(boolean enabled)
   {
      this.toolbarEnabled = enabled;
      updateGUI();
   }

    /**
     * @param simpleGUI the simpleGUI to set
     */
    public void setSimpleGUI(boolean simpleGUI) {
        this.simpleGUI = simpleGUI;
        updateGUI();
    }

   public JPanel getOperationPanel()
   {
      return operationPanel;
   }

   public JToolBar getDisplayToolbar()
   {
      return displayToolbar;
   }

   public JToolBar getGraphicToolbar()
   {
      return graphicToolbar;
   }

   public JPanel getStatusBar()
   {
      return statusBar;
   }

    /**
     * @return the mode
     */
    public int getMode() {
        return mode;
    }

    /**
     * @param mode the mode to set
     */
    public void setMode(int mode) {
        this.mode = mode;
        updateMode();
    }

    private void updateMode() {
        setDefaultViewportSetup();
        switch(mode) {
            case MODE_3D:
                resetAxesButton.setVisible(true);
                show3DPlanesButton.setVisible(true);
                autoLocateDividersButton.setVisible(true);
                modeCustomOrthoSliceButton.setVisible(true);
                modeCustomSliceButton.setVisible(true);
                modeOrthosliceButton.setVisible(true);
                jSeparator3.setVisible(true);
                sliceLinesLabel.setVisible(true);
                sliceLinesModeCB.setVisible(true);
                jSeparator7.setVisible(true);
                interpolationButton.setVisible(true);
                
                viewMenu.setVisible(true);
                break;
            case MODE_2D:
                resetAxesButton.setVisible(false);
                show3DPlanesButton.setVisible(false);
                autoLocateDividersButton.setVisible(false);
                modeCustomOrthoSliceButton.setVisible(false);
                modeCustomSliceButton.setVisible(false);
                modeOrthosliceButton.setVisible(false);
                jSeparator3.setVisible(false);
                sliceLinesLabel.setVisible(false);
                sliceLinesModeCB.setVisible(false);
                jSeparator7.setVisible(false);
                interpolationButton.setVisible(false);
                
                viewMenu.setVisible(false);
                break;                
        }
    }
}
