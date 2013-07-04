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

import com.sun.j3d.utils.image.TextureLoader;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.media.j3d.Texture2D;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import pl.edu.icm.visnow.datasets.CellSet;
import pl.edu.icm.visnow.datasets.DataSchema;
import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.geometries.events.ColorListener;
import pl.edu.icm.visnow.geometries.gui.TransparencyEditor.TransparencyEditor;
import pl.edu.icm.visnow.geometries.parameters.AbstractDataMappingParams;
import pl.edu.icm.visnow.geometries.parameters.AbstractRenderingParams;
import pl.edu.icm.visnow.geometries.parameters.DataMappingParams;
import pl.edu.icm.visnow.geometries.parameters.RenderingParams;
import pl.edu.icm.visnow.lib.utils.ImageUtilities;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;
import pl.edu.icm.visnow.system.main.VisNow;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class DataMappingGUI extends javax.swing.JPanel
{
   private boolean debug = VisNow.isDebug();
   protected Field inField = null;
   protected CellSet inCellSet = null;
   protected AbstractDataMappingParams params = null;
   protected int nComponents = 0;
   protected int nCellComps = 0;
   protected boolean active = true;
   protected AbstractRenderingParams renderingParams = new RenderingParams();
   protected float colorMin = 0, colorMax = 255, colorPhysMin = 0, colorPhysMax = 1, colorD = 255;
   protected ColorComponentPanel[] colorComponentPanels;
   protected DataSchema lastFieldDataSchema = null;
   protected DataSchema lastCellSetDataSchema = null;
   protected boolean transparencyActive = false;
   protected boolean transparencyStartNull = false;
   protected boolean simple = !VisNow.allowGUISwitch;
   protected BufferedImage image = null;
   
   protected boolean showNodeCellPanel = true;
   private static final String TEXTURE_FILE_PROPERTY = "visnow.paths.data.last.pl.edu.icm.visnow.geometries.gui.DataMappingGUI";

   private String lastTexturePath = null;
   /**
    * Creates new form DataMappingGUI
    */
   public DataMappingGUI()
   {
      initComponents();
      setPresentation(simple);
      colorComponentPanels = new ColorComponentPanel[]
      {
         redComponentPanel, greenComponentPanel, blueComponentPanel,
      };
      redComponentPanel.setSampleColors(Color.black, Color.RED);
      greenComponentPanel.setSampleColors(Color.black, Color.GREEN);
      blueComponentPanel.setSampleColors(Color.black, Color.BLUE);
      uComponentPanel.setSliderTitle("texture u component");
      vComponentPanel.setSliderTitle("texture v component");
      transparencyEditor.setStartNullTransparencyComponent(true);
      FileNameExtensionFilter allImagesFilter = new FileNameExtensionFilter("All image files", "jpg", "jpeg", "gif", "png", "JPG", "JPEG", "GIF", "PNG");
      FileNameExtensionFilter jpegImagesFilter = new FileNameExtensionFilter("JPEG images (*.jpg, *.jpeg)", "jpg", "jpeg", "JPG", "JPEG");
      FileNameExtensionFilter gifImagesFilter = new FileNameExtensionFilter("GIF images (*.gif)", "gif", "GIF");
      FileNameExtensionFilter pngImagesFilter = new FileNameExtensionFilter("PNG images (*.png)", "png", "PNG");
      textureFileChooser.addChoosableFileFilter(jpegImagesFilter);
      textureFileChooser.addChoosableFileFilter(gifImagesFilter);
      textureFileChooser.addChoosableFileFilter(pngImagesFilter);
      textureFileChooser.addChoosableFileFilter(allImagesFilter);  
   }
   
   public DataMappingGUI(boolean showNodeCellPanel)
   {
      this();
      this.showNodeCellPanel = showNodeCellPanel;
      nodeCellPanel.setVisible(showNodeCellPanel);
   }

   public DataMappingGUI(CellSet inCellSet, Field inField, AbstractDataMappingParams params)
   {
      this();
      setTextureDir();
      setInData(inCellSet, inField, params);
   }

   public DataMappingGUI(CellSet inCellSet, Field inField, AbstractDataMappingParams params, boolean showNodeCellPanel)
   {
      this();
      setTextureDir();
      this.showNodeCellPanel = showNodeCellPanel;
      nodeCellPanel.setVisible(showNodeCellPanel);
      setInData(inCellSet, inField, params);
   }

   public void setShowNodeCellPanel(boolean showNodeCellPanel)
   {
      this.showNodeCellPanel = showNodeCellPanel;
      nodeCellPanel.setVisible(showNodeCellPanel);
   }
   
   private void setTextureDir()
   {
      String fileName = VisNow.get().getMainConfig().getProperty(TEXTURE_FILE_PROPERTY);
      if (fileName != null)
         textureFileChooser.setSelectedFile(new File(fileName));
      else
         textureFileChooser.setCurrentDirectory(new File(VisNow.get().getMainConfig().getDefaultDataPath()));    
   }

   public void setParams(AbstractDataMappingParams params)
   {
      this.params = params;
      colorMappedComponentPanel.setParams(params);
      redComponentPanel.setParams(params.getRedParams());
      greenComponentPanel.setParams(params.getGreenParams());
      blueComponentPanel.setParams(params.getBlueParams());
      uComponentPanel.setParams(params.getUParams());
      vComponentPanel.setParams(params.getVParams());
      transparencyEditor.setParams(params.getTransparencyParams());
   }

   public final void setInData(CellSet inCellSet, Field inField, AbstractDataMappingParams params)
   {
      nodeCellPanel.setVisible(true);
      if (params == null || inCellSet == null || inField == null)
         return;
      this.inCellSet = inCellSet;
      this.inField = inField;
      setParams(params);
      if (lastFieldDataSchema != null && inField.getSchema().isDataCompatibleWith(lastFieldDataSchema, true)
              && (lastCellSetDataSchema == null || inCellSet.getSchema().isDataCompatibleWith(lastCellSetDataSchema, active)))
         return;
      lastFieldDataSchema = inField.getSchema();
      lastCellSetDataSchema = inCellSet.getSchema();
      nComponents = nCellComps = 0;
      for (int i = 0; i < inField.getNData(); i++)
         if (inField.getData(i).isSimpleNumeric())
            nComponents += 1;
      for (int i = 0; i < inCellSet.getNData(); i++)
         if (inCellSet.getData(i).isSimpleNumeric())
            nCellComps += 1;
      if (nComponents == 0 && nCellComps != 0)
      {
         this.params.setCellDataMapped(true);
         cellDataButton.setSelected(true);
         nodeDataButton.setEnabled(false);
         nodeDataButton.setVisible(false);
      }
      if (nComponents != 0)
      {
         this.params.setCellDataMapped(false);
         nodeDataButton.setSelected(true);
         nodeDataButton.setEnabled(true);
         if (nCellComps == 0)
         {
            cellDataButton.setEnabled(false);
            cellDataButton.setVisible(false);
         }
      }
      updateComponentSelectors();
   }

   public DataMappingGUI(Field inField, DataMappingParams params)
   {
      this();
      setInData(inField, params);
   }

   public final void setInData(Field inField, AbstractDataMappingParams params)   
   {
      if (params != null)
         setParams(params);
      if (params == null || inField == null)
         return;
      if (lastFieldDataSchema != null && inField.getSchema().isDataCompatibleWith(lastFieldDataSchema, true))
         return;
      lastFieldDataSchema = inField.getSchema();
      this.inField = inField;
      params.setCellDataMapped(false);
      updateComponentSelectors();
      
      SwingInstancer.swingRun(new Runnable()
      {
         @Override
         public void run()
         {
            cellDataButton.setEnabled(false);
            cellDataButton.setVisible(false);
            nodeCellPanel.setVisible(false);
            nodeDataButton.setSelected(true);
         }
      });
   }

   public ColorMappedComponentPanel getColorMappedComponentPanel()
   {
      return colorMappedComponentPanel;
   }

   public TransparencyEditor getTransparencyEditor()
   {
      return transparencyEditor;
   }

   public void setRenderingParams(AbstractRenderingParams renderingParams)
   {
      this.renderingParams = renderingParams;
      colorMappedComponentPanel.setParams(renderingParams);
   }
   private String[] irregularFieldAddTextureLabels = new String[]
   {
      "x", "y", "z",
      "x normal component", "y normal component", "z normal component"
   };
   private String[] regularFieldAddTextureLabels = new String[]
   {
      "x", "y", "z",
      "x normal component", "y normal component", "z normal component",
      "i", "j"
   };
   private String[] affineFieldAddTextureLabels = new String[]
   {
      "i", "j"
   };
   private int[] irregularFieldAddTextureComp = new int[]
   {
      DataMappingParams.COORDX, DataMappingParams.COORDY, DataMappingParams.COORDZ,
      DataMappingParams.NORMALX, DataMappingParams.NORMALY, DataMappingParams.NORMALZ
   };
   private int[] regularFieldAddTextureComp = new int[]
   {
      DataMappingParams.COORDX, DataMappingParams.COORDY, DataMappingParams.COORDZ,
      DataMappingParams.NORMALX, DataMappingParams.NORMALY, DataMappingParams.NORMALZ,
      DataMappingParams.INDEXI, DataMappingParams.INDEXJ
   };
   private int[] affineFieldAddTextureComp = new int[]
   {
      DataMappingParams.INDEXI, DataMappingParams.INDEXJ
   };

   private void updateComponentSelectors()
   {
      active = false;
      if (params.isCellDataMapped())
      {
         for (ColorComponentPanel colorComponentPanel : colorComponentPanels)
            colorComponentPanel.setData(inCellSet.getSchema());
         colorMappedComponentPanel.setData(inCellSet.getSchema());
         transparencyEditor.setDataContainer(inCellSet);
      } else
      {
         for (ColorComponentPanel colorComponentPanel : colorComponentPanels)
            colorComponentPanel.setData(inField.getSchema());
         colorMappedComponentPanel.setData(inField.getSchema());
         transparencyEditor.setDataContainer(inField);
      }
      if (inField instanceof RegularField && inField.getCoords() != null)
      {
         uComponentPanel.addExtraItems(regularFieldAddTextureLabels, regularFieldAddTextureComp);
         vComponentPanel.addExtraItems(regularFieldAddTextureLabels, regularFieldAddTextureComp);
      } else if (inField instanceof RegularField)
      {
         uComponentPanel.addExtraItems(affineFieldAddTextureLabels, affineFieldAddTextureComp);
         vComponentPanel.addExtraItems(affineFieldAddTextureLabels, affineFieldAddTextureComp);
      } else
      {
         uComponentPanel.addExtraItems(irregularFieldAddTextureLabels, irregularFieldAddTextureComp);
         vComponentPanel.addExtraItems(irregularFieldAddTextureLabels, irregularFieldAddTextureComp);

      }
      uComponentPanel.setData(inField.getSchema());
      vComponentPanel.setData(inField.getSchema());
      active = true;
   }
   
   public final void setPresentation(boolean simple)
   {
      this.simple = simple;
      Dimension simpleDim = new Dimension(200, 310);
      Dimension expertDim = new Dimension(200, 590);
      colorMappedComponentPanel.setPresentation(simple);
      
      if (simple)
      {
         mapPane.remove(texturePanel);
         mapPane.remove(rgbPanel);
         if (transparencyStartNull)
            mapPane.remove(transparencyPanel);
         setMinimumSize(simpleDim);
         setPreferredSize(simpleDim);
         setMaximumSize(simpleDim);
      } else
      {
         setMinimumSize(expertDim);
         setPreferredSize(expertDim);
         setMaximumSize(expertDim);
         if (!transparencyStartNull)
            mapPane.remove(transparencyPanel);
         mapPane.addTab("texture", texturePanel);
         mapPane.addTab("rgb", rgbPanel);
         mapPane.addTab("transp", transparencyPanel);
      }
      datamapPanelChanged();
      revalidate();
   }

   /**
    * This method is called from within the constructor to initialize the form. WARNING: Do NOT
    * modify this code. The content of this method is always regenerated by the Form Editor.
    */
   @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        textureFileChooser = new javax.swing.JFileChooser();
        nodeCellGroup = new javax.swing.ButtonGroup();
        colormapTextureGroup = new javax.swing.ButtonGroup();
        buttonGroup1 = new javax.swing.ButtonGroup();
        nodeCellPanel = new javax.swing.JPanel();
        nodeDataButton = new javax.swing.JRadioButton();
        cellDataButton = new javax.swing.JRadioButton();
        mapPane = new javax.swing.JTabbedPane();
        colorMappedComponentPanel = new pl.edu.icm.visnow.geometries.gui.ColorMappedComponentPanel();
        texturePanel = new javax.swing.JPanel();
        uComponentPanel = new pl.edu.icm.visnow.geometries.gui.TextureComponentPanel();
        vComponentPanel = new pl.edu.icm.visnow.geometries.gui.TextureComponentPanel();
        textureFilePanel = new javax.swing.JPanel();
        readTextureButton = new javax.swing.JButton();
        imagePathField = new javax.swing.JTextField();
        textureImageFlipXCB = new javax.swing.JCheckBox();
        textureImageFlipYCB = new javax.swing.JCheckBox();
        textureImagePanel = new javax.swing.JPanel();
        imagePanel = new pl.edu.icm.visnow.gui.widgets.ImagePanel();
        rgbPanel = new javax.swing.JPanel();
        redComponentPanel = new pl.edu.icm.visnow.geometries.gui.ColorComponentPanel();
        greenComponentPanel = new pl.edu.icm.visnow.geometries.gui.ColorComponentPanel();
        blueComponentPanel = new pl.edu.icm.visnow.geometries.gui.ColorComponentPanel();
        jPanel1 = new javax.swing.JPanel();
        transparencyPanel = new javax.swing.JPanel();
        transparencyEditor = new pl.edu.icm.visnow.geometries.gui.TransparencyEditor.TransparencyEditor();
        jPanel3 = new javax.swing.JPanel();

        textureFileChooser.setName("textureFileChooser"); // NOI18N

        setMinimumSize(new java.awt.Dimension(200, 590));
        setPreferredSize(new java.awt.Dimension(235, 590));
        setRequestFocusEnabled(false);
        setLayout(new java.awt.GridBagLayout());

        nodeCellPanel.setMinimumSize(new java.awt.Dimension(180, 20));
        nodeCellPanel.setName("nodeCellPanel"); // NOI18N
        nodeCellPanel.setPreferredSize(new java.awt.Dimension(220, 20));
        nodeCellPanel.setLayout(new java.awt.GridLayout(1, 0));

        nodeCellGroup.add(nodeDataButton);
        nodeDataButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        nodeDataButton.setSelected(true);
        nodeDataButton.setText("node data");
        nodeDataButton.setName("nodeDataButton"); // NOI18N
        nodeDataButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nodeDataButtonActionPerformed(evt);
            }
        });
        nodeCellPanel.add(nodeDataButton);

        nodeCellGroup.add(cellDataButton);
        cellDataButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        cellDataButton.setText("cell data");
        cellDataButton.setName("cellDataButton"); // NOI18N
        cellDataButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cellDataButtonActionPerformed(evt);
            }
        });
        nodeCellPanel.add(cellDataButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        add(nodeCellPanel, gridBagConstraints);

        mapPane.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        mapPane.setName("mapPane"); // NOI18N
        mapPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                mapPaneStateChanged(evt);
            }
        });

        colorMappedComponentPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        colorMappedComponentPanel.setName("colorMappedComponentPanel"); // NOI18N
        colorMappedComponentPanel.setPreferredSize(new java.awt.Dimension(220, 530));
        mapPane.addTab("cmap", colorMappedComponentPanel);

        texturePanel.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        texturePanel.setMinimumSize(new java.awt.Dimension(180, 530));
        texturePanel.setName("texturePanel"); // NOI18N
        texturePanel.setPreferredSize(new java.awt.Dimension(220, 530));
        texturePanel.setLayout(new java.awt.GridBagLayout());

        uComponentPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "u component", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        uComponentPanel.setMinimumSize(new java.awt.Dimension(200, 130));
        uComponentPanel.setName("uComponentPanel"); // NOI18N
        uComponentPanel.setPreferredSize(new java.awt.Dimension(230, 130));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        texturePanel.add(uComponentPanel, gridBagConstraints);

        vComponentPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "v component", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        vComponentPanel.setMinimumSize(new java.awt.Dimension(200, 130));
        vComponentPanel.setName("vComponentPanel"); // NOI18N
        vComponentPanel.setPreferredSize(new java.awt.Dimension(230, 130));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        texturePanel.add(vComponentPanel, gridBagConstraints);

        textureFilePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "texture image/colormap", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        textureFilePanel.setMinimumSize(new java.awt.Dimension(200, 60));
        textureFilePanel.setName("textureFilePanel"); // NOI18N
        textureFilePanel.setPreferredSize(new java.awt.Dimension(230, 60));
        textureFilePanel.setLayout(new java.awt.GridBagLayout());

        readTextureButton.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        readTextureButton.setText("read"); // NOI18N
        readTextureButton.setMaximumSize(new java.awt.Dimension(56, 19));
        readTextureButton.setMinimumSize(new java.awt.Dimension(56, 19));
        readTextureButton.setName("readTextureButton"); // NOI18N
        readTextureButton.setPreferredSize(new java.awt.Dimension(56, 19));
        readTextureButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                readTextureButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        textureFilePanel.add(readTextureButton, gridBagConstraints);

        imagePathField.setText(" "); // NOI18N
        imagePathField.setName("imagePathField"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        textureFilePanel.add(imagePathField, gridBagConstraints);

        textureImageFlipXCB.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        textureImageFlipXCB.setText("flip x");
        textureImageFlipXCB.setEnabled(false);
        textureImageFlipXCB.setName("textureImageFlipXCB"); // NOI18N
        textureImageFlipXCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textureImageFlipXCBActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        textureFilePanel.add(textureImageFlipXCB, gridBagConstraints);

        textureImageFlipYCB.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        textureImageFlipYCB.setText("flip y");
        textureImageFlipYCB.setEnabled(false);
        textureImageFlipYCB.setName("textureImageFlipYCB"); // NOI18N
        textureImageFlipYCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textureImageFlipYCBActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        textureFilePanel.add(textureImageFlipYCB, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        texturePanel.add(textureFilePanel, gridBagConstraints);

        textureImagePanel.setName("textureImagePanel"); // NOI18N
        textureImagePanel.setLayout(new java.awt.BorderLayout());

        imagePanel.setMinimumSize(new java.awt.Dimension(200, 200));
        imagePanel.setName("imagePanel"); // NOI18N
        imagePanel.setPreferredSize(new java.awt.Dimension(200, 200));

        javax.swing.GroupLayout imagePanelLayout = new javax.swing.GroupLayout(imagePanel);
        imagePanel.setLayout(imagePanelLayout);
        imagePanelLayout.setHorizontalGroup(
            imagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 238, Short.MAX_VALUE)
        );
        imagePanelLayout.setVerticalGroup(
            imagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 224, Short.MAX_VALUE)
        );

        textureImagePanel.add(imagePanel, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        texturePanel.add(textureImagePanel, gridBagConstraints);

        mapPane.addTab("texture", texturePanel);

        rgbPanel.setMinimumSize(new java.awt.Dimension(180, 530));
        rgbPanel.setName("rgbPanel"); // NOI18N
        rgbPanel.setPreferredSize(new java.awt.Dimension(220, 530));
        rgbPanel.setLayout(new java.awt.GridBagLayout());

        redComponentPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "red", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10), new java.awt.Color(204, 0, 51))); // NOI18N
        redComponentPanel.setMinimumSize(new java.awt.Dimension(200, 130));
        redComponentPanel.setName("redComponentPanel"); // NOI18N
        redComponentPanel.setPreferredSize(new java.awt.Dimension(230, 130));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        rgbPanel.add(redComponentPanel, gridBagConstraints);

        greenComponentPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "green", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10), new java.awt.Color(0, 155, 0))); // NOI18N
        greenComponentPanel.setMinimumSize(new java.awt.Dimension(200, 130));
        greenComponentPanel.setName("greenComponentPanel"); // NOI18N
        greenComponentPanel.setPreferredSize(new java.awt.Dimension(230, 130));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        rgbPanel.add(greenComponentPanel, gridBagConstraints);

        blueComponentPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "blue", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10), new java.awt.Color(0, 64, 255))); // NOI18N
        blueComponentPanel.setMinimumSize(new java.awt.Dimension(200, 130));
        blueComponentPanel.setName("blueComponentPanel"); // NOI18N
        blueComponentPanel.setPreferredSize(new java.awt.Dimension(230, 130));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        rgbPanel.add(blueComponentPanel, gridBagConstraints);

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(233, 50));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 238, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 154, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        rgbPanel.add(jPanel1, gridBagConstraints);

        mapPane.addTab("rgb", rgbPanel);

        transparencyPanel.setMinimumSize(new java.awt.Dimension(180, 530));
        transparencyPanel.setName("transparencyPanel"); // NOI18N
        transparencyPanel.setPreferredSize(new java.awt.Dimension(220, 530));
        transparencyPanel.setLayout(new java.awt.GridBagLayout());

        transparencyEditor.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "transparency component", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        transparencyEditor.setMinimumSize(new java.awt.Dimension(150, 400));
        transparencyEditor.setName("transparencyEditor"); // NOI18N
        transparencyEditor.setPreferredSize(new java.awt.Dimension(250, 400));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        transparencyPanel.add(transparencyEditor, gridBagConstraints);

        jPanel3.setMinimumSize(new java.awt.Dimension(100, 100));
        jPanel3.setName("jPanel3"); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 238, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 144, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        transparencyPanel.add(jPanel3, gridBagConstraints);

        mapPane.addTab("transp", transparencyPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.2;
        add(mapPane, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

   private void readTextureButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_readTextureButtonActionPerformed
   {//GEN-HEADEREND:event_readTextureButtonActionPerformed
      if(lastTexturePath != null)
           textureFileChooser.setCurrentDirectory(new File(lastTexturePath));
      else              
           textureFileChooser.setCurrentDirectory(new File(VisNow.get().getMainConfig().getUsableDataPath(DataMappingGUI.class)));
      
      if (textureFileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
      {
         String path = textureFileChooser.getSelectedFile().getAbsolutePath();
         lastTexturePath = path.substring(0, path.lastIndexOf(File.separator));
         VisNow.get().getMainConfig().setLastDataPath(lastTexturePath, DataMappingGUI.class);
         image = ImageUtilities.makeBufferedImage(ImageUtilities.blockingLoad(path));         
         imagePathField.setText(path);
         processTextureImage();
      }
}//GEN-LAST:event_readTextureButtonActionPerformed

    private void processTextureImage() {
        if(image == null) {
            if (active && params != null) {
                params.setTexture(null);
                params.setTextureImage(null);
            }
            textureImageFlipXCB.setEnabled(false);
            textureImageFlipYCB.setEnabled(false);
            return;
        }
        
        textureImageFlipXCB.setEnabled(true);        
        textureImageFlipYCB.setEnabled(true);        
        
        BufferedImage processedImage = image;            
        if (textureImageFlipXCB.isSelected() || textureImageFlipYCB.isSelected()) {
            if (textureImageFlipXCB.isSelected()) {
                processedImage = ImageUtilities.flipImageHorizontal(processedImage);
            }
            if (textureImageFlipYCB.isSelected()) {
                processedImage = ImageUtilities.flipImageVertical(processedImage);
            }
        }
        double scale = Math.min((double) textureImagePanel.getWidth() / (double) processedImage.getWidth(),
                (double) textureImagePanel.getHeight() / (double) processedImage.getHeight());
        imagePanel.setImage(processedImage, scale);
        TextureLoader tl = new TextureLoader(processedImage);
        if (active && params != null) {
            params.setTexture((Texture2D) (tl.getTexture()));
            params.setTextureImage(tl.getImage().getImage());
        }

    }
   
   private void cellDataButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cellDataButtonActionPerformed
   {//GEN-HEADEREND:event_cellDataButtonActionPerformed
      if (params == null)
         return;
      params.setCellDataMapped(cellDataButton.isSelected());
      updateComponentSelectors();
}//GEN-LAST:event_cellDataButtonActionPerformed

   private void nodeDataButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_nodeDataButtonActionPerformed
   {//GEN-HEADEREND:event_nodeDataButtonActionPerformed
      if (params == null)
         return;
      params.setCellDataMapped(cellDataButton.isSelected());
      updateComponentSelectors();
}//GEN-LAST:event_nodeDataButtonActionPerformed

   private void mapPaneStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_mapPaneStateChanged
   {//GEN-HEADEREND:event_mapPaneStateChanged
      datamapPanelChanged();
   }//GEN-LAST:event_mapPaneStateChanged

    private void textureImageFlipXCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textureImageFlipXCBActionPerformed
        processTextureImage();
    }//GEN-LAST:event_textureImageFlipXCBActionPerformed

    private void textureImageFlipYCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textureImageFlipYCBActionPerformed
        processTextureImage();
    }//GEN-LAST:event_textureImageFlipYCBActionPerformed

  private void datamapPanelChanged()                                         
   {                                             
      if (params == null)
         return;
      if (simple)
         params.setColorMode(DataMappingParams.COLORMAPPED);
      else
         switch (mapPane.getSelectedIndex())
         {
            case 0:
               params.setColorMode(DataMappingParams.COLORMAPPED);
               break;
            case 1:
               params.setColorMode(DataMappingParams.UVTEXTURED);
               break;
            case 2:
               params.setColorMode(DataMappingParams.RGB);
               break;
            default:
               break;
         }
   }             
    
   public ColorListener getBackgroundColorListener()
   {
      return transparencyEditor.getColorListener();
   }

   public boolean isTransparencyStartNull()
   {
      return transparencyStartNull;
   }

   public void setStartNullTransparencyComponent(boolean startNull)
   {
      transparencyStartNull = startNull;
      transparencyEditor.setStartNullTransparencyComponent(startNull);
   }

   public void updateWidgets()
   {
      if (params == null)
         return;
      active = false;
      active = true;
   }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected pl.edu.icm.visnow.geometries.gui.ColorComponentPanel blueComponentPanel;
    protected javax.swing.ButtonGroup buttonGroup1;
    protected javax.swing.JRadioButton cellDataButton;
    protected pl.edu.icm.visnow.geometries.gui.ColorMappedComponentPanel colorMappedComponentPanel;
    protected javax.swing.ButtonGroup colormapTextureGroup;
    protected pl.edu.icm.visnow.geometries.gui.ColorComponentPanel greenComponentPanel;
    protected pl.edu.icm.visnow.gui.widgets.ImagePanel imagePanel;
    protected javax.swing.JTextField imagePathField;
    protected javax.swing.JPanel jPanel1;
    protected javax.swing.JPanel jPanel3;
    protected javax.swing.JTabbedPane mapPane;
    protected javax.swing.ButtonGroup nodeCellGroup;
    protected javax.swing.JPanel nodeCellPanel;
    protected javax.swing.JRadioButton nodeDataButton;
    protected javax.swing.JButton readTextureButton;
    protected pl.edu.icm.visnow.geometries.gui.ColorComponentPanel redComponentPanel;
    protected javax.swing.JPanel rgbPanel;
    protected javax.swing.JFileChooser textureFileChooser;
    protected javax.swing.JPanel textureFilePanel;
    protected javax.swing.JCheckBox textureImageFlipXCB;
    protected javax.swing.JCheckBox textureImageFlipYCB;
    protected javax.swing.JPanel textureImagePanel;
    protected javax.swing.JPanel texturePanel;
    protected pl.edu.icm.visnow.geometries.gui.TransparencyEditor.TransparencyEditor transparencyEditor;
    protected javax.swing.JPanel transparencyPanel;
    protected pl.edu.icm.visnow.geometries.gui.TextureComponentPanel uComponentPanel;
    protected pl.edu.icm.visnow.geometries.gui.TextureComponentPanel vComponentPanel;
    // End of variables declaration//GEN-END:variables
}
