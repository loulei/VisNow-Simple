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

package pl.edu.icm.visnow.geometries.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.PolygonAttributes;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.vecmath.Color3f;
import pl.edu.icm.visnow.geometries.objects.FieldGeometry;
import pl.edu.icm.visnow.geometries.parameters.AbstractRenderingParams;
import pl.edu.icm.visnow.geometries.parameters.RenderingParams;
import pl.edu.icm.visnow.gui.icons.IconsContainer;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class DisplayPropertiesGUI extends javax.swing.JPanel
{
   protected static ImageIcon[] modeIcons = null;

   protected AbstractRenderingParams renderingParams = new RenderingParams();
   protected boolean is3D = true;
   protected boolean lastLinesButtonState = false;
   
   class IconString
   {
      ImageIcon icon; 
      String string;

      public IconString(ImageIcon icon, String string)
      {
         this.icon = icon;
         this.string = string;
      }

      public ImageIcon getIcon()
      {
         return icon;
      }

      public String getString()
      {
         return string;
      }
      
      public String toString()
      {
         return string;
      }
   }
   
   protected  IconString[] elements = {
        new IconString(new ImageIcon(IconsContainer.getGouraud()) , "smooth" ),
        new IconString(new ImageIcon(IconsContainer.getFlat()) , "flat" ),
        new IconString(new ImageIcon(IconsContainer.getUnshaded()) , "unshaded" ),
        new IconString(new ImageIcon(IconsContainer.getBackground()) , "background" )};
   
   class ComplexCellRenderer implements ListCellRenderer
   {
      protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

      public Component getListCellRendererComponent(JList list, Object value, int index,
              boolean isSelected, boolean cellHasFocus)
      {
         Icon theIcon = null;
         String theText = null;

         JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index,
                 isSelected, cellHasFocus);

         if (value instanceof IconString)
         {
            theIcon = ((IconString)value).getIcon();
            theText = ((IconString)value).getString();
         } else
            theText = "";
         if (theIcon != null)
            renderer.setIcon(theIcon);
         renderer.setText(theText);
         return renderer;
      }
   }
   /**
    * Creates new form DisplayPropertiesGUI
    */
   public DisplayPropertiesGUI()
   {
      initComponents();
      faceButton.setInit(new String[]
      {
         "all faces", "front faces", "back faces"
      }, null);
      specularColorEditor.setTitle("specular color");
      specularColorEditor.setBrightness(20);
      diffuseColorEditor.setTitle("diffuse color");
      featSlider.setShowingFields(false);
      Object popup = modeCombo.getUI().getAccessibleChild(modeCombo, 0);
      if (popup instanceof BasicComboPopup)
      {
         BasicComboPopup modesPopup = (BasicComboPopup) popup;
         JList modesList = modesPopup.getList();
         modesPopup.removeAll();
         modesList.setCellRenderer(new ComplexCellRenderer());
         modesList.setSize(200, 192);
         modesList.setMinimumSize(new Dimension(200, 192));
         modesList.setPreferredSize(new Dimension(200, 192));
         modesList.setMaximumSize(new Dimension(200, 192));
         modesList.setFixedCellHeight(48);
         modesPopup.setSize(200, 200);
         modesPopup.setMinimumSize(new Dimension(200, 200));
         modesPopup.setPreferredSize(new Dimension(200, 200));
         modesPopup.setMaximumSize(new Dimension(200, 200));
         modesPopup.add(modesList, BorderLayout.CENTER);
      }  
   }
   
   private void updateGUI() {
       if(this.renderingParams == null) {
           //TODO
           //inheritBox.setEnabled(false);
           //pointBox.setEnabled(false);
           //edgesBox.setEnabled(false);
           //surfaceBox.setEnabled(false);
           //imageBox.setEnabled(false);
           return;
       }
       
       imageBox.setVisible(!is3D);
       imageBox.setSelected((renderingParams.getDisplayMode() & RenderingParams.IMAGE) == RenderingParams.IMAGE);
       pointBox.setSelected((renderingParams.getDisplayMode() & RenderingParams.NODES) == RenderingParams.NODES);
       edgesBox.setSelected((renderingParams.getDisplayMode() & FieldGeometry.EDGES) == FieldGeometry.EDGES);
       surfaceBox.setSelected((renderingParams.getDisplayMode() & FieldGeometry.SURFACE) == FieldGeometry.SURFACE);
   }
   
    public void setIs3D(boolean is3D) {
        this.is3D = is3D;
        updateGUI();
    }   

   /**
    * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this
    * code. The content of this method is always regenerated by the Form Editor.
    */
   public void setRenderingParams(AbstractRenderingParams renderingParams)
   {
      this.renderingParams = renderingParams;
      updateGUI();
   }

   public void setShadingMode(boolean gouraud)
   {
      if (gouraud)
         SwingInstancer.swingRun(new Runnable()
         {
            @Override
            public void run()
            {
               modeCombo.setSelectedIndex(GOURAUD);
               renderingParams.setShadingMode(RenderingParams.GOURAUD_SHADED);
            }
         });
      else
         SwingInstancer.swingRun(new Runnable()
         {
            @Override
            public void run()
            {
               modeCombo.setSelectedIndex(FLAT);
               renderingParams.setShadingMode(RenderingParams.FLAT_SHADED);
            }
         });
   }

   public void setPresentation(boolean simple)
   {
      GridBagConstraints gridBagConstraints;
      Dimension simpleEdgesDim = new Dimension(200, 177);
      Dimension expertEdgesDim = new Dimension(200, 199);
      Dimension simpleSurfDim = new Dimension(200, 185);
      Dimension expertSurfDim = new Dimension(200, 235);
      Dimension simpleDim = new Dimension(200, 460);
      Dimension expertDim = new Dimension(200, 500);
      if (simple)
      {
         surfacePanel.remove(specularColorEditor);
         surfacePanel.remove(shininessSlider);
         edgesPanel.remove(lineLightingBox);
         edgesPanel.setMinimumSize(simpleEdgesDim);
         edgesPanel.setMaximumSize(simpleEdgesDim);
         edgesPanel.setPreferredSize(simpleEdgesDim);
         surfacePanel.setMinimumSize(simpleSurfDim);
         surfacePanel.setMaximumSize(simpleSurfDim);
         surfacePanel.setPreferredSize(simpleSurfDim);
         setMinimumSize(simpleDim);
         setMaximumSize(simpleDim);
         setPreferredSize(simpleDim);
      } else
      {   
         setMinimumSize(expertDim);
         setMaximumSize(expertDim);
         setPreferredSize(expertDim);    
         
         surfacePanel.setMinimumSize(expertSurfDim);
         surfacePanel.setMaximumSize(expertSurfDim);
         surfacePanel.setPreferredSize(expertSurfDim);
         
         gridBagConstraints = new java.awt.GridBagConstraints();
         gridBagConstraints.gridx = 0;
         gridBagConstraints.gridy = 4;
         gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
         gridBagConstraints.insets = new java.awt.Insets(0, 0, 3, 3);
         surfacePanel.add(specularColorEditor, gridBagConstraints);
         
         gridBagConstraints = new java.awt.GridBagConstraints();
         gridBagConstraints.gridx = 0;
         gridBagConstraints.gridy = 6;
         gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
         surfacePanel.add(shininessSlider, gridBagConstraints);
         edgesPanel.setMinimumSize(expertEdgesDim);
         edgesPanel.setMaximumSize(expertEdgesDim);
         edgesPanel.setPreferredSize(expertEdgesDim);
         
         gridBagConstraints = new java.awt.GridBagConstraints();
         gridBagConstraints.gridx = 0;
         gridBagConstraints.gridy = 1;
         gridBagConstraints.gridwidth = 2;
         gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
         edgesPanel.add(featSlider, gridBagConstraints);

         gridBagConstraints = new java.awt.GridBagConstraints();
         gridBagConstraints.gridx = 0;
         gridBagConstraints.gridy = 4;
         gridBagConstraints.gridwidth = 2;
         gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
         edgesPanel.add(lineLightingBox, gridBagConstraints);

      }
      validate();
   }

   @SuppressWarnings("unchecked")
   // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
   private void initComponents()
   {
      java.awt.GridBagConstraints gridBagConstraints;

      cullFaceGroup = new javax.swing.ButtonGroup();
      shadingGroup = new javax.swing.ButtonGroup();
      helpFrame = new javax.swing.JFrame();
      helpText = new javax.swing.JLabel();
      testFrame = new javax.swing.JFrame();
      jCheckBox1 = new javax.swing.JCheckBox();
      surfacePanel = new javax.swing.JPanel();
      transparencySlider = new javax.swing.JSlider();
      shininessSlider = new javax.swing.JSlider();
      jPanel5 = new javax.swing.JPanel();
      modeCombo = new javax.swing.JComboBox();
      flipToggle = new javax.swing.JToggleButton();
      faceButton = new pl.edu.icm.visnow.gui.widgets.MultistateButton();
      offsetBox = new javax.swing.JCheckBox();
      lightBackBox = new javax.swing.JCheckBox();
      specularColorEditor = new pl.edu.icm.visnow.gui.widgets.ColorEditor();
      diffuseColorEditor = new pl.edu.icm.visnow.gui.widgets.ColorEditor();
      edgesPanel = new javax.swing.JPanel();
      lineStyleCombo = new javax.swing.JComboBox();
      jLabel4 = new javax.swing.JLabel();
      featSlider = new pl.edu.icm.visnow.gui.widgets.LogarithmicSlider();
      lineLightingBox = new javax.swing.JCheckBox();
      lineWidthSlider = new pl.edu.icm.visnow.gui.widgets.LogarithmicSlider();
      jPanel1 = new javax.swing.JPanel();
      jPanel2 = new javax.swing.JPanel();
      surfaceBox = new javax.swing.JCheckBox();
      pointBox = new javax.swing.JCheckBox();
      edgesBox = new javax.swing.JCheckBox();
      imageBox = new javax.swing.JCheckBox();
      boxBox = new javax.swing.JCheckBox();

      helpFrame.setUndecorated(true);

      helpText.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
      helpText.setText("<html>This slider controls relative visibility of the object surface part<p>\nwith respect to other geometries<p>\nIf there are lines on the surface, increase slider value (drag right)<p>\nDrag left to see the surface through other object (\"object in the box\" effect)</html>"); // NOI18N
      helpFrame.getContentPane().add(helpText, java.awt.BorderLayout.CENTER);

      testFrame.setMinimumSize(new java.awt.Dimension(210, 570));

      jCheckBox1.setText("simple");
      jCheckBox1.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            jCheckBox1ActionPerformed(evt);
         }
      });
      testFrame.getContentPane().add(jCheckBox1, java.awt.BorderLayout.NORTH);

      setMinimumSize(new java.awt.Dimension(200, 480));
      setPreferredSize(new java.awt.Dimension(235, 480));
      setLayout(new java.awt.GridBagLayout());

      surfacePanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(153, 153, 153), java.awt.Color.white, new java.awt.Color(102, 102, 102), new java.awt.Color(204, 204, 204)), javax.swing.BorderFactory.createTitledBorder(null, "surfaces", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10)))); // NOI18N
      surfacePanel.setMinimumSize(new java.awt.Dimension(276, 225));
      surfacePanel.setPreferredSize(new java.awt.Dimension(330, 230));
      surfacePanel.setLayout(new java.awt.GridBagLayout());

      transparencySlider.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
      transparencySlider.setMajorTickSpacing(20);
      transparencySlider.setMinorTickSpacing(5);
      transparencySlider.setValue(0);
      transparencySlider.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "transparency", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
      transparencySlider.addChangeListener(new javax.swing.event.ChangeListener()
      {
         public void stateChanged(javax.swing.event.ChangeEvent evt)
         {
            transparencySliderStateChanged(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 5;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 1.0;
      surfacePanel.add(transparencySlider, gridBagConstraints);

      shininessSlider.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
      shininessSlider.setMajorTickSpacing(20);
      shininessSlider.setMinorTickSpacing(5);
      shininessSlider.setValue(15);
      shininessSlider.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "shininess", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
      shininessSlider.addChangeListener(new javax.swing.event.ChangeListener()
      {
         public void stateChanged(javax.swing.event.ChangeEvent evt)
         {
            shininessSliderStateChanged(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 6;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      surfacePanel.add(shininessSlider, gridBagConstraints);

      jPanel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
      jPanel5.setMinimumSize(new java.awt.Dimension(200, 80));
      jPanel5.setPreferredSize(new java.awt.Dimension(377, 80));
      jPanel5.setLayout(new java.awt.GridBagLayout());

      modeCombo.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
      modeCombo.setModel(new DefaultComboBoxModel(elements));
      modeCombo.addItemListener(new java.awt.event.ItemListener()
      {
         public void itemStateChanged(java.awt.event.ItemEvent evt)
         {
            modeComboItemStateChanged(evt);
         }
      });
      modeCombo.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            modeComboActionPerformed(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
      jPanel5.add(modeCombo, gridBagConstraints);

      flipToggle.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
      flipToggle.setText("flip sides");
      flipToggle.setMargin(new java.awt.Insets(2, 4, 2, 4));
      flipToggle.setMaximumSize(new java.awt.Dimension(89, 18));
      flipToggle.setMinimumSize(new java.awt.Dimension(89, 18));
      flipToggle.setName(""); // NOI18N
      flipToggle.setPreferredSize(new java.awt.Dimension(89, 18));
      flipToggle.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            flipToggleActionPerformed(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
      jPanel5.add(flipToggle, gridBagConstraints);

      faceButton.setText("multistateButton1");
      faceButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
      faceButton.setMaximumSize(new java.awt.Dimension(90, 18));
      faceButton.setMinimumSize(new java.awt.Dimension(90, 18));
      faceButton.setPreferredSize(new java.awt.Dimension(90, 18));
      faceButton.addChangeListener(new javax.swing.event.ChangeListener()
      {
         public void stateChanged(javax.swing.event.ChangeEvent evt)
         {
            faceButtonStateChanged(evt);
         }
      });
      faceButton.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            faceButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.weightx = 1.0;
      jPanel5.add(faceButton, gridBagConstraints);

      offsetBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
      offsetBox.setText("pull to front of the scene");
      offsetBox.setMaximumSize(new java.awt.Dimension(232, 18));
      offsetBox.setMinimumSize(new java.awt.Dimension(232, 18));
      offsetBox.setPreferredSize(new java.awt.Dimension(232, 18));
      offsetBox.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            offsetBoxActionPerformed(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
      jPanel5.add(offsetBox, gridBagConstraints);

      lightBackBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
      lightBackBox.setSelected(true);
      lightBackBox.setText("<html>lighted<p> backside</html>");
      lightBackBox.setMargin(new java.awt.Insets(0, 2, 2, 0));
      lightBackBox.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            lightBackBoxActionPerformed(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      jPanel5.add(lightBackBox, gridBagConstraints);

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      surfacePanel.add(jPanel5, gridBagConstraints);

      specularColorEditor.addChangeListener(new javax.swing.event.ChangeListener()
      {
         public void stateChanged(javax.swing.event.ChangeEvent evt)
         {
            specularColorEditorStateChanged(evt);
         }
      });
      specularColorEditor.setLayout(new java.awt.BorderLayout());
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 4;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.insets = new java.awt.Insets(0, 0, 3, 3);
      surfacePanel.add(specularColorEditor, gridBagConstraints);

      diffuseColorEditor.addChangeListener(new javax.swing.event.ChangeListener()
      {
         public void stateChanged(javax.swing.event.ChangeEvent evt)
         {
            diffuseColorEditorStateChanged(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 3;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 3);
      surfacePanel.add(diffuseColorEditor, gridBagConstraints);

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.weightx = 0.8;
      gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
      add(surfacePanel, gridBagConstraints);

      edgesPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(153, 153, 153), null, new java.awt.Color(102, 102, 102), new java.awt.Color(204, 204, 204)), javax.swing.BorderFactory.createTitledBorder(null, "points and lines", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10), java.awt.Color.black))); // NOI18N
      edgesPanel.setMinimumSize(new java.awt.Dimension(194, 199));
      edgesPanel.setPreferredSize(new java.awt.Dimension(194, 199));
      edgesPanel.setRequestFocusEnabled(false);
      edgesPanel.setLayout(new java.awt.GridBagLayout());

      lineStyleCombo.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
      lineStyleCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "solid", "dashed", "dotted", "dashdot" }));
      lineStyleCombo.addItemListener(new java.awt.event.ItemListener()
      {
         public void itemStateChanged(java.awt.event.ItemEvent evt)
         {
            lineStyleComboItemStateChanged(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 3;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
      edgesPanel.add(lineStyleCombo, gridBagConstraints);

      jLabel4.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
      jLabel4.setText("line style");
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 3;
      edgesPanel.add(jLabel4, gridBagConstraints);

      featSlider.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "feature angle", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
      featSlider.setMin(0.1F);
      featSlider.setMinimumSize(new java.awt.Dimension(90, 64));
      featSlider.setPreferredSize(new java.awt.Dimension(200, 64));
      featSlider.setVal(0.1F);
      featSlider.addChangeListener(new javax.swing.event.ChangeListener()
      {
         public void stateChanged(javax.swing.event.ChangeEvent evt)
         {
            featSliderStateChanged(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      edgesPanel.add(featSlider, gridBagConstraints);

      lineLightingBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
      lineLightingBox.setText("darken lines");
      lineLightingBox.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            lineLightingBoxActionPerformed(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 4;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      edgesPanel.add(lineLightingBox, gridBagConstraints);

      lineWidthSlider.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "line/point width", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
      lineWidthSlider.setMin(0.5F);
      lineWidthSlider.setMinimumSize(new java.awt.Dimension(90, 63));
      lineWidthSlider.setPreferredSize(new java.awt.Dimension(200, 63));
      lineWidthSlider.setVal(1.0F);
      lineWidthSlider.addChangeListener(new javax.swing.event.ChangeListener()
      {
         public void stateChanged(javax.swing.event.ChangeEvent evt)
         {
            lineWidthSliderStateChanged(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      edgesPanel.add(lineWidthSlider, gridBagConstraints);

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      add(edgesPanel, gridBagConstraints);

      jPanel1.setMinimumSize(new java.awt.Dimension(200, 100));
      jPanel1.setPreferredSize(new java.awt.Dimension(200, 100));
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 3;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 1.0;
      add(jPanel1, gridBagConstraints);

      jPanel2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(153, 153, 153), null, new java.awt.Color(102, 102, 102), new java.awt.Color(204, 204, 204)));
      jPanel2.setMaximumSize(new java.awt.Dimension(300, 55));
      jPanel2.setMinimumSize(new java.awt.Dimension(160, 45));
      jPanel2.setName(""); // NOI18N
      jPanel2.setPreferredSize(new java.awt.Dimension(200, 50));
      jPanel2.setLayout(new java.awt.GridBagLayout());

      surfaceBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
      surfaceBox.setSelected(true);
      surfaceBox.setText("surfaces"); // NOI18N
      surfaceBox.setMaximumSize(new java.awt.Dimension(88, 16));
      surfaceBox.setMinimumSize(new java.awt.Dimension(80, 16));
      surfaceBox.setPreferredSize(new java.awt.Dimension(80, 16));
      surfaceBox.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            surfaceBoxActionPerformed(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new java.awt.Insets(0, 5, 3, 0);
      jPanel2.add(surfaceBox, gridBagConstraints);

      pointBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
      pointBox.setText("points");
      pointBox.setMaximumSize(new java.awt.Dimension(64, 16));
      pointBox.setMinimumSize(new java.awt.Dimension(64, 16));
      pointBox.setPreferredSize(new java.awt.Dimension(64, 16));
      pointBox.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            pointBoxActionPerformed(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new java.awt.Insets(0, 0, 3, 0);
      jPanel2.add(pointBox, gridBagConstraints);

      edgesBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
      edgesBox.setText("lines");
      edgesBox.setMaximumSize(new java.awt.Dimension(54, 16));
      edgesBox.setMinimumSize(new java.awt.Dimension(54, 16));
      edgesBox.setPreferredSize(new java.awt.Dimension(54, 16));
      edgesBox.setRolloverEnabled(false);
      edgesBox.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            edgesBoxActionPerformed(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new java.awt.Insets(0, 0, 3, 0);
      jPanel2.add(edgesBox, gridBagConstraints);

      imageBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
      imageBox.setText("image");
      imageBox.setMaximumSize(new java.awt.Dimension(63, 16));
      imageBox.setMinimumSize(new java.awt.Dimension(63, 16));
      imageBox.setPreferredSize(new java.awt.Dimension(63, 16));
      imageBox.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            imageBoxActionPerformed(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
      jPanel2.add(imageBox, gridBagConstraints);

      boxBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
      boxBox.setText("box");
      boxBox.setMaximumSize(new java.awt.Dimension(47, 16));
      boxBox.setMinimumSize(new java.awt.Dimension(47, 16));
      boxBox.setPreferredSize(new java.awt.Dimension(47, 16));
      boxBox.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            boxBoxActionPerformed(evt);
         }
      });
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      jPanel2.add(boxBox, gridBagConstraints);

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      add(jPanel2, gridBagConstraints);
   }// </editor-fold>//GEN-END:initComponents

   private void setGeometryMode()
   {
      int mode = 0;
      if (surfaceBox.isSelected())
         mode |= FieldGeometry.SURFACE;
      if (edgesBox.isSelected())
         mode |= FieldGeometry.EDGES;
      if (pointBox.isSelected())
         mode |= RenderingParams.NODES;
      if (imageBox.isSelected())
         mode |= RenderingParams.IMAGE;
      if (boxBox.isSelected())
         mode |= RenderingParams.OUTLINE_BOX;
      renderingParams.setDisplayMode(mode);
   }
    private void edgesBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_edgesBoxActionPerformed
    {//GEN-HEADEREND:event_edgesBoxActionPerformed
       featSlider.setEnabled(edgesBox.isSelected());
       setGeometryMode();
}//GEN-LAST:event_edgesBoxActionPerformed

    private void lineStyleComboItemStateChanged(java.awt.event.ItemEvent evt)//GEN-FIRST:event_lineStyleComboItemStateChanged
    {//GEN-HEADEREND:event_lineStyleComboItemStateChanged
       switch (lineStyleCombo.getSelectedIndex())
       {
          case 0:
             renderingParams.setLineStyle(LineAttributes.PATTERN_SOLID);
             break;
          case 1:
             renderingParams.setLineStyle(LineAttributes.PATTERN_DASH);
             break;
          case 2:
             renderingParams.setLineStyle(LineAttributes.PATTERN_DOT);
             break;
          case 3:
             renderingParams.setLineStyle(LineAttributes.PATTERN_DASH_DOT);
             break;
       }
}//GEN-LAST:event_lineStyleComboItemStateChanged

    private void transparencySliderStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_transparencySliderStateChanged
    {//GEN-HEADEREND:event_transparencySliderStateChanged
       float transp = .01f * transparencySlider.getValue();
       if (renderingParams.getTransparency() == transp)
          return;

       renderingParams.setTransparency(transp);
}//GEN-LAST:event_transparencySliderStateChanged

    private void shininessSliderStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_shininessSliderStateChanged
    {//GEN-HEADEREND:event_shininessSliderStateChanged
       renderingParams.setShininess(3000.f / (1 + shininessSlider.getValue()));
}//GEN-LAST:event_shininessSliderStateChanged

    private void surfaceBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_surfaceBoxActionPerformed
    {//GEN-HEADEREND:event_surfaceBoxActionPerformed
       setGeometryMode();
}//GEN-LAST:event_surfaceBoxActionPerformed

    private void flipToggleActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_flipToggleActionPerformed
    {//GEN-HEADEREND:event_flipToggleActionPerformed
       renderingParams.setSurfaceOrientation(!flipToggle.isSelected());
    }//GEN-LAST:event_flipToggleActionPerformed

    private void pointBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_pointBoxActionPerformed
    {//GEN-HEADEREND:event_pointBoxActionPerformed
       setGeometryMode();
    }//GEN-LAST:event_pointBoxActionPerformed

    private void specularColorEditorStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_specularColorEditorStateChanged
    {//GEN-HEADEREND:event_specularColorEditorStateChanged
       renderingParams.setSpecularColor(new Color3f(specularColorEditor.getColorComponents()));
    }//GEN-LAST:event_specularColorEditorStateChanged

    private void diffuseColorEditorStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_diffuseColorEditorStateChanged
       renderingParams.setDiffuseColor(new Color3f(diffuseColorEditor.getColorComponents()));
    }//GEN-LAST:event_diffuseColorEditorStateChanged

    private void featSliderStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_featSliderStateChanged
    {//GEN-HEADEREND:event_featSliderStateChanged
       if (!featSlider.isAdjusting())
          renderingParams.setMinEdgeDihedral(featSlider.getVal() - .1f);
    }//GEN-LAST:event_featSliderStateChanged

   private void lineLightingBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_lineLightingBoxActionPerformed
   {//GEN-HEADEREND:event_lineLightingBoxActionPerformed
      renderingParams.setLineLighting(lineLightingBox.isSelected());
   }//GEN-LAST:event_lineLightingBoxActionPerformed

   private void lineWidthSliderStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_lineWidthSliderStateChanged
   {//GEN-HEADEREND:event_lineWidthSliderStateChanged
      float lineThickness = lineWidthSlider.getVal();
      if (renderingParams.getLineThickness() == lineThickness)
         return;
      renderingParams.setLineThickness(lineThickness);
   }//GEN-LAST:event_lineWidthSliderStateChanged

   private void offsetBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_offsetBoxActionPerformed
   {//GEN-HEADEREND:event_offsetBoxActionPerformed
      renderingParams.setSurfaceOffset(offsetBox.isSelected() ? -1000000 : 255);
   }//GEN-LAST:event_offsetBoxActionPerformed

   private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jCheckBox1ActionPerformed
   {//GEN-HEADEREND:event_jCheckBox1ActionPerformed
      this.setPresentation(jCheckBox1.isSelected());
   }//GEN-LAST:event_jCheckBox1ActionPerformed

    private void imageBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_imageBoxActionPerformed
       setGeometryMode();
    }//GEN-LAST:event_imageBoxActionPerformed

   private void boxBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_boxBoxActionPerformed
   {//GEN-HEADEREND:event_boxBoxActionPerformed
      setGeometryMode();
   }//GEN-LAST:event_boxBoxActionPerformed

   private static final int GOURAUD    = 0;
   private static final int FLAT       = 1;
   private static final int UNSHADED   = 2;
   private static final int BACKGROUND = 3;
   
   private void updateMode()
   {
      //System.out.println(""+lastLinesButtonState);
      switch (modeCombo.getSelectedIndex())
      {
      case GOURAUD:
         renderingParams.setShadingMode(RenderingParams.GOURAUD_SHADED);
         edgesBox.setSelected(lastLinesButtonState);
         break;
      case FLAT:
         renderingParams.setShadingMode(RenderingParams.FLAT_SHADED);
         edgesBox.setSelected(lastLinesButtonState);
         break;
      case UNSHADED:
         renderingParams.setShadingMode(RenderingParams.UNSHADED);
         edgesBox.setSelected(lastLinesButtonState);
         break;
      case BACKGROUND:
         lastLinesButtonState = edgesBox.isSelected();
         edgesBox.setSelected(true);
         renderingParams.setShadingMode(RenderingParams.BACKGROUND);
         renderingParams.setDisplayMode(renderingParams.getDisplayMode() | RenderingParams.EDGES);
         break;
      }      
   }
   
   private void modeComboItemStateChanged(java.awt.event.ItemEvent evt)//GEN-FIRST:event_modeComboItemStateChanged
   {//GEN-HEADEREND:event_modeComboItemStateChanged
      updateMode();
   }//GEN-LAST:event_modeComboItemStateChanged
   
   int[] cullChoices = {PolygonAttributes.CULL_NONE,
                        PolygonAttributes.CULL_BACK, 
                        PolygonAttributes.CULL_FRONT
                        };
   
   private void faceButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_faceButtonActionPerformed
   {//GEN-HEADEREND:event_faceButtonActionPerformed
      renderingParams.setCullMode(cullChoices[faceButton.getState()]);
   }//GEN-LAST:event_faceButtonActionPerformed

   private void faceButtonStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_faceButtonStateChanged
   {//GEN-HEADEREND:event_faceButtonStateChanged
      renderingParams.setCullMode(cullChoices[faceButton.getState()]);
   }//GEN-LAST:event_faceButtonStateChanged

   private void modeComboActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_modeComboActionPerformed
   {//GEN-HEADEREND:event_modeComboActionPerformed
      updateMode();
   }//GEN-LAST:event_modeComboActionPerformed

   private void lightBackBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_lightBackBoxActionPerformed
   {//GEN-HEADEREND:event_lightBackBoxActionPerformed
      renderingParams.setLightedBackside(lightBackBox.isSelected());
   }//GEN-LAST:event_lightBackBoxActionPerformed

   public static void main(String args[])
   {
      java.awt.EventQueue.invokeLater(new Runnable()
      {
         public void run()
         {
            DisplayPropertiesGUI gui = new DisplayPropertiesGUI();
            gui.testFrame.add(gui, BorderLayout.CENTER);
            gui.testFrame.setVisible(true);
         }
      });
   }
   // Variables declaration - do not modify//GEN-BEGIN:variables
   protected javax.swing.JCheckBox boxBox;
   protected javax.swing.ButtonGroup cullFaceGroup;
   protected pl.edu.icm.visnow.gui.widgets.ColorEditor diffuseColorEditor;
   protected javax.swing.JCheckBox edgesBox;
   protected javax.swing.JPanel edgesPanel;
   protected pl.edu.icm.visnow.gui.widgets.MultistateButton faceButton;
   protected pl.edu.icm.visnow.gui.widgets.LogarithmicSlider featSlider;
   protected javax.swing.JToggleButton flipToggle;
   protected javax.swing.JFrame helpFrame;
   protected javax.swing.JLabel helpText;
   protected javax.swing.JCheckBox imageBox;
   protected javax.swing.JCheckBox jCheckBox1;
   protected javax.swing.JLabel jLabel4;
   protected javax.swing.JPanel jPanel1;
   protected javax.swing.JPanel jPanel2;
   protected javax.swing.JPanel jPanel5;
   protected javax.swing.JCheckBox lightBackBox;
   protected javax.swing.JCheckBox lineLightingBox;
   protected javax.swing.JComboBox lineStyleCombo;
   protected pl.edu.icm.visnow.gui.widgets.LogarithmicSlider lineWidthSlider;
   protected javax.swing.JComboBox modeCombo;
   protected javax.swing.JCheckBox offsetBox;
   protected javax.swing.JCheckBox pointBox;
   protected javax.swing.ButtonGroup shadingGroup;
   protected javax.swing.JSlider shininessSlider;
   protected pl.edu.icm.visnow.gui.widgets.ColorEditor specularColorEditor;
   protected javax.swing.JCheckBox surfaceBox;
   protected javax.swing.JPanel surfacePanel;
   protected javax.swing.JFrame testFrame;
   protected javax.swing.JSlider transparencySlider;
   // End of variables declaration//GEN-END:variables

       
}
