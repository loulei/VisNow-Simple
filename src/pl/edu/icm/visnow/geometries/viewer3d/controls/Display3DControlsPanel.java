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

package pl.edu.icm.visnow.geometries.viewer3d.controls;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.vecmath.Color3f;
import pl.edu.icm.visnow.geometries.viewer3d.Display3DPanel;
import pl.edu.icm.visnow.geometries.viewer3d.controls.light_editor.LightColorEditor;
import pl.edu.icm.visnow.geometries.viewer3d.lights.EditableAmbientLight;
import pl.edu.icm.visnow.system.main.VisNow;
import pl.edu.icm.visnow.system.swing.filechooser.VNFileChooser;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class Display3DControlsPanel extends javax.swing.JPanel
{

   private static final int BGR_COLOR    = 0;
   private static final int BGR_GRADIENT = 1;
   private static final int BGR_IMAGE    = 2;
   private static final long serialVersionUID = 4600073246148982408L;
   private Display3DPanel display3DPanel = null;
   private JFileChooser jpegChooser                            = new JFileChooser();
   private JFileChooser bgrImageFileChooser                    = new JFileChooser();
   private JFileChooser pngChooser                             = new JFileChooser();
//   private JFileChooser sceneChooser                           = new JFileChooser();
   private boolean active = true;
   private FileNameExtensionFilter jpegFilter = 
              new FileNameExtensionFilter("JPEG image file","jpg","JPG","jpeg","JPEG");
   private FileNameExtensionFilter pngFilter = 
              new FileNameExtensionFilter("PNG image file","png","PNG","jpeg","JPEG");
   private FileNameExtensionFilter scnFilter =
              new FileNameExtensionFilter("scene file","scn","SCN","scene","SCENE");
   private EditableAmbientLight ambientLight = null;
   LightColorEditor ambientLightColorEditor = new LightColorEditor();
   /** Creates new form Display3DControlsPanel */
   public Display3DControlsPanel()
   {
      initComponents();
   }

   public Display3DControlsPanel(Display3DPanel panel)
   {
      initComponents();
      this.display3DPanel = panel;
      titleEditor.setPanel(panel);
      ambientLightColorEditor.setName("ambientLightColorEditor"); // NOI18N
      GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.weightx = 1.0;
      jPanel3.add(ambientLightColorEditor, gridBagConstraints);
      ambientLight = panel.getAmbientLight();
      ambientLightColorEditor.setLight(ambientLight);
      ambientLightColorEditor.addChangeListener(new ChangeListener()
      {
         @Override
         public void stateChanged(ChangeEvent e)
         {
            ambientLight.setLightColor(new Color3f(ambientLightColorEditor.getColorComponents()));
         }
      });
      ambientToggle.setSelected(ambientLight.isEnabled());
      movieCreationPanel.setPanel(panel);
      directionalLightsEditorPanel.setPanel(panel);
      pointLightsEditorPanel.setPanel(panel);
      stereoToggle.setEnabled(panel.getStereoAvailable());
      stereoSeparationSlider.setVisible(panel.getStereoAvailable());
      dispToggle.setEnabled(true);
      stereoSeparationSlider.setEnabled(false);
      jpegChooser.setFileFilter(jpegFilter);
      jpegChooser.setCurrentDirectory(new File(VisNow.get().getMainConfig().getWorkeffectPath()));
      pngChooser.setFileFilter(pngFilter);
      pngChooser.setCurrentDirectory(new File(VisNow.get().getMainConfig().getWorkeffectPath()));
      clipPlanesUI.setModelClip(panel.getModelClip());
      moveCameraButton.setSelected(panel.isMoveCameraMode());
      backgroundColorEditor.setBrightness(0);    
      backgroundColorEditor1.setVisible(false);
      backgroundColorEditor2.setVisible(false);
      bgrImageReadButton.setVisible(false);
      FileNameExtensionFilter allImagesFilter = new FileNameExtensionFilter("All image files", "jpg", "jpeg", "gif", "png", "JPG", "JPEG", "GIF", "PNG");
      FileNameExtensionFilter jpegImagesFilter = new FileNameExtensionFilter("JPEG images (*.jpg, *.jpeg)", "jpg", "jpeg", "JPG", "JPEG");
      FileNameExtensionFilter gifImagesFilter = new FileNameExtensionFilter("GIF images (*.gif)", "gif", "GIF");
      FileNameExtensionFilter pngImagesFilter = new FileNameExtensionFilter("PNG images (*.png)", "png", "PNG");
      bgrImageFileChooser.addChoosableFileFilter(jpegImagesFilter);
      bgrImageFileChooser.addChoosableFileFilter(gifImagesFilter);
      bgrImageFileChooser.addChoosableFileFilter(pngImagesFilter);
      bgrImageFileChooser.addChoosableFileFilter(allImagesFilter);
   }
   /** This method is called from within the constructor to
    * initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is
    * always regenerated by the Form Editor.
    */
   @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup1 = new javax.swing.ButtonGroup();
        editorsPane = new javax.swing.JTabbedPane();
        appearanceControlPanel = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        mouseRotationSensitivitySlider = new javax.swing.JSlider();
        mouseTranslationSensitivitySlider = new javax.swing.JSlider();
        mouseWheelSensitivitySlider = new javax.swing.JSlider();
        stereoSeparationSlider = new javax.swing.JSlider();
        jPanel7 = new javax.swing.JPanel();
        moveCameraButton = new javax.swing.JToggleButton();
        jPanel14 = new javax.swing.JPanel();
        backgroundColorEditor = new pl.edu.icm.visnow.gui.widgets.ColorEditor();
        bgrColorButton = new javax.swing.JRadioButton();
        bgrGradButton = new javax.swing.JRadioButton();
        bgrImageButton = new javax.swing.JRadioButton();
        backgroundColorEditor1 = new pl.edu.icm.visnow.gui.widgets.ColorEditor();
        bgrImageReadButton = new javax.swing.JButton();
        backgroundColorEditor2 = new pl.edu.icm.visnow.gui.widgets.ColorEditor();
        resetButton = new javax.swing.JButton();
        dispToggle = new javax.swing.JToggleButton();
        stereoToggle = new javax.swing.JToggleButton();
        lockViewButton = new javax.swing.JToggleButton();
        jPanel1 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        fogRangeSlider = new pl.edu.icm.visnow.gui.widgets.FloatSubRangeSlider.FloatSubRangeSlider();
        depthCueBox = new javax.swing.JCheckBox();
        fovSlider = new javax.swing.JSlider();
        pick3DBox = new javax.swing.JCheckBox();
        axesSizeSlider = new javax.swing.JSlider();
        orientGlyphType = new javax.swing.JComboBox();
        jPanel11 = new javax.swing.JPanel();
        clipRangeSlider = new pl.edu.icm.visnow.gui.widgets.FloatSubRangeSlider.ExtendedFloatSubRangeSlider();
        clipBox = new javax.swing.JCheckBox();
        titleAnnoPanel = new javax.swing.JPanel();
        titleEditor = new pl.edu.icm.visnow.geometries.viewer3d.controls.TitleEditorPanel();
        lightEditorPanel = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        ambientToggle = new javax.swing.JCheckBox();
        jPanel13 = new javax.swing.JPanel();
        directionalLightsEditorPanel = new pl.edu.icm.visnow.geometries.viewer3d.controls.light_editor.DirectionalLightsEditorPanel();
        pointLightsEditorPanel = new pl.edu.icm.visnow.geometries.viewer3d.controls.light_editor.PointLightsEditorPanel();
        clipEditorPanel = new javax.swing.JPanel();
        clipPlanesUI = new pl.edu.icm.visnow.geometries.viewer3d.controls.clip_editor.ClipPlanesUI();
        animationPanel = new javax.swing.JPanel();
        animateToggle = new javax.swing.JToggleButton();
        allReset = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        xyRotSlider = new pl.edu.icm.visnow.gui.widgets.DoubleSlider();
        jPanel6 = new javax.swing.JPanel();
        xyTransSlider = new pl.edu.icm.visnow.gui.widgets.DoubleSlider();
        jPanel12 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        zRotSlider = new javax.swing.JSlider();
        zTransSlider = new javax.swing.JSlider();
        animScaleSlider = new javax.swing.JSlider();
        zRotReset = new javax.swing.JButton();
        zTransReset = new javax.swing.JButton();
        scaleReset = new javax.swing.JButton();
        resetRotationButton = new javax.swing.JButton();
        resetTranslationButton = new javax.swing.JButton();
        imageMoviePanel = new javax.swing.JPanel();
        jpegButton = new javax.swing.JButton();
        pngButton = new javax.swing.JButton();
        widthTF = new javax.swing.JTextField();
        heightTF = new javax.swing.JTextField();
        widthLabel = new javax.swing.JLabel();
        heightLabel = new javax.swing.JLabel();
        useWindowSizeCB = new javax.swing.JCheckBox();
        movieCreationPanel = new pl.edu.icm.visnow.geometries.viewer3d.controls.MovieCreationPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();

        setMinimumSize(new java.awt.Dimension(220, 500));
        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(280, 580));
        setLayout(new java.awt.BorderLayout());

        editorsPane.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        editorsPane.setMinimumSize(new java.awt.Dimension(220, 460));
        editorsPane.setName("editorsPane"); // NOI18N
        editorsPane.setPreferredSize(new java.awt.Dimension(240, 470));
        editorsPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                editorsPaneStateChanged(evt);
            }
        });

        appearanceControlPanel.setName("appearanceControlPanel"); // NOI18N
        appearanceControlPanel.setLayout(new java.awt.GridBagLayout());

        jPanel5.setName("jPanel5"); // NOI18N
        jPanel5.setLayout(new java.awt.GridLayout(4, 0));

        mouseRotationSensitivitySlider.setMajorTickSpacing(60);
        mouseRotationSensitivitySlider.setMinorTickSpacing(10);
        mouseRotationSensitivitySlider.setPaintTicks(true);
        mouseRotationSensitivitySlider.setValue(60);
        mouseRotationSensitivitySlider.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "mouse rotation sensitivity", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        mouseRotationSensitivitySlider.setMinimumSize(new java.awt.Dimension(40, 39));
        mouseRotationSensitivitySlider.setName("mouseRotationSensitivitySlider"); // NOI18N
        mouseRotationSensitivitySlider.setPreferredSize(new java.awt.Dimension(70, 55));
        mouseRotationSensitivitySlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                mouseRotationSensitivitySliderStateChanged(evt);
            }
        });
        jPanel5.add(mouseRotationSensitivitySlider);

        mouseTranslationSensitivitySlider.setMajorTickSpacing(60);
        mouseTranslationSensitivitySlider.setMinorTickSpacing(10);
        mouseTranslationSensitivitySlider.setPaintTicks(true);
        mouseTranslationSensitivitySlider.setValue(60);
        mouseTranslationSensitivitySlider.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "mouse translation sensitivity", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        mouseTranslationSensitivitySlider.setMinimumSize(new java.awt.Dimension(40, 39));
        mouseTranslationSensitivitySlider.setName("mouseTranslationSensitivitySlider"); // NOI18N
        mouseTranslationSensitivitySlider.setPreferredSize(new java.awt.Dimension(70, 55));
        mouseTranslationSensitivitySlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                mouseTranslationSensitivitySliderStateChanged(evt);
            }
        });
        jPanel5.add(mouseTranslationSensitivitySlider);

        mouseWheelSensitivitySlider.setMajorTickSpacing(30);
        mouseWheelSensitivitySlider.setPaintTicks(true);
        mouseWheelSensitivitySlider.setValue(30);
        mouseWheelSensitivitySlider.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "mouse wheel sensitivity", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        mouseWheelSensitivitySlider.setMinimumSize(new java.awt.Dimension(40, 39));
        mouseWheelSensitivitySlider.setName("mouseWheelSensitivitySlider"); // NOI18N
        mouseWheelSensitivitySlider.setPreferredSize(new java.awt.Dimension(70, 55));
        mouseWheelSensitivitySlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                mouseWheelSensitivitySliderStateChanged(evt);
            }
        });
        jPanel5.add(mouseWheelSensitivitySlider);

        stereoSeparationSlider.setMajorTickSpacing(100);
        stereoSeparationSlider.setMaximum(500);
        stereoSeparationSlider.setMinimum(-500);
        stereoSeparationSlider.setMinorTickSpacing(20);
        stereoSeparationSlider.setPaintTicks(true);
        stereoSeparationSlider.setSnapToTicks(true);
        stereoSeparationSlider.setValue(200);
        stereoSeparationSlider.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "stereo eye separation", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        stereoSeparationSlider.setName("stereoSeparationSlider"); // NOI18N
        stereoSeparationSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                stereoSeparationSliderStateChanged(evt);
            }
        });
        jPanel5.add(stereoSeparationSlider);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        appearanceControlPanel.add(jPanel5, gridBagConstraints);

        jPanel7.setName("jPanel7"); // NOI18N
        jPanel7.setLayout(new java.awt.GridBagLayout());

        moveCameraButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        moveCameraButton.setText("move camera");
        moveCameraButton.setName("moveCameraButton"); // NOI18N
        moveCameraButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveCameraButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel7.add(moveCameraButton, gridBagConstraints);

        jPanel14.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "background", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        jPanel14.setName("jPanel14"); // NOI18N
        jPanel14.setLayout(new java.awt.GridBagLayout());

        backgroundColorEditor.setBrightness(0);
        backgroundColorEditor.setMinimumSize(new java.awt.Dimension(60, 19));
        backgroundColorEditor.setName("backgroundColorEditor"); // NOI18N
        backgroundColorEditor.setPreferredSize(new java.awt.Dimension(100, 20));
        backgroundColorEditor.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                backgroundColorEditorStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 1, 0);
        jPanel14.add(backgroundColorEditor, gridBagConstraints);

        buttonGroup1.add(bgrColorButton);
        bgrColorButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        bgrColorButton.setSelected(true);
        bgrColorButton.setText("color");
        bgrColorButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        bgrColorButton.setName("bgrColorButton"); // NOI18N
        bgrColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bgrColorButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel14.add(bgrColorButton, gridBagConstraints);

        buttonGroup1.add(bgrGradButton);
        bgrGradButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        bgrGradButton.setText("grad");
        bgrGradButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        bgrGradButton.setName("bgrGradButton"); // NOI18N
        bgrGradButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bgrGradButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel14.add(bgrGradButton, gridBagConstraints);

        buttonGroup1.add(bgrImageButton);
        bgrImageButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        bgrImageButton.setText("image");
        bgrImageButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        bgrImageButton.setName("bgrImageButton"); // NOI18N
        bgrImageButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bgrImageButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel14.add(bgrImageButton, gridBagConstraints);

        backgroundColorEditor1.setBrightness(0);
        backgroundColorEditor1.setMinimumSize(new java.awt.Dimension(60, 19));
        backgroundColorEditor1.setName("backgroundColorEditor1"); // NOI18N
        backgroundColorEditor1.setPreferredSize(new java.awt.Dimension(100, 20));
        backgroundColorEditor1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                backgroundColorEditor1StateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 1, 0);
        jPanel14.add(backgroundColorEditor1, gridBagConstraints);

        bgrImageReadButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        bgrImageReadButton.setText("read");
        bgrImageReadButton.setMargin(new java.awt.Insets(2, 0, 2, 0));
        bgrImageReadButton.setMaximumSize(new java.awt.Dimension(35, 19));
        bgrImageReadButton.setMinimumSize(new java.awt.Dimension(35, 19));
        bgrImageReadButton.setName("bgrImageReadButton"); // NOI18N
        bgrImageReadButton.setPreferredSize(new java.awt.Dimension(35, 19));
        bgrImageReadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bgrImageReadButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel14.add(bgrImageReadButton, gridBagConstraints);

        backgroundColorEditor2.setBrightness(0);
        backgroundColorEditor2.setMinimumSize(new java.awt.Dimension(60, 19));
        backgroundColorEditor2.setName("backgroundColorEditor2"); // NOI18N
        backgroundColorEditor2.setPreferredSize(new java.awt.Dimension(100, 20));
        backgroundColorEditor2.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                backgroundColorEditor2StateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 1, 0);
        jPanel14.add(backgroundColorEditor2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel7.add(jPanel14, gridBagConstraints);

        resetButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        resetButton.setText("reset");
        resetButton.setToolTipText("resets all geometry position  and size to default ");
        resetButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        resetButton.setName("resetButton"); // NOI18N
        resetButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel7.add(resetButton, gridBagConstraints);

        dispToggle.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        dispToggle.setSelected(true);
        dispToggle.setText("perspective");
        dispToggle.setName("dispToggle"); // NOI18N
        dispToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dispToggleActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel7.add(dispToggle, gridBagConstraints);

        stereoToggle.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        stereoToggle.setText("stereo");
        stereoToggle.setName("stereoToggle"); // NOI18N
        stereoToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stereoToggleActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel7.add(stereoToggle, gridBagConstraints);

        lockViewButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        lockViewButton.setText("lock view");
        lockViewButton.setName("lockViewButton"); // NOI18N
        lockViewButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lockViewButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel7.add(lockViewButton, gridBagConstraints);

        jPanel1.setMinimumSize(new java.awt.Dimension(1, 10));
        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(1, 10));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel7.add(jPanel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        appearanceControlPanel.add(jPanel7, gridBagConstraints);

        jPanel9.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel9.setName("jPanel9"); // NOI18N
        jPanel9.setLayout(new javax.swing.BoxLayout(jPanel9, javax.swing.BoxLayout.LINE_AXIS));

        fogRangeSlider.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "depth cue range", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        fogRangeSlider.setMaximum(1.5F);
        fogRangeSlider.setMinimum(0.5F);
        fogRangeSlider.setName("fogRangeSlider"); // NOI18N
        fogRangeSlider.setPaintTicks(true);
        fogRangeSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                fogRangeSliderStateChanged(evt);
            }
        });
        jPanel9.add(fogRangeSlider);

        depthCueBox.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        depthCueBox.setMinimumSize(new java.awt.Dimension(80, 21));
        depthCueBox.setName("depthCueBox"); // NOI18N
        depthCueBox.setPreferredSize(new java.awt.Dimension(20, 21));
        depthCueBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                depthCueBoxActionPerformed(evt);
            }
        });
        jPanel9.add(depthCueBox);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        appearanceControlPanel.add(jPanel9, gridBagConstraints);

        fovSlider.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        fovSlider.setMajorTickSpacing(20);
        fovSlider.setMaximum(150);
        fovSlider.setMinimum(10);
        fovSlider.setMinorTickSpacing(1);
        fovSlider.setPaintLabels(true);
        fovSlider.setPaintTicks(true);
        fovSlider.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "field of view", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        fovSlider.setName("fovSlider"); // NOI18N
        fovSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                fovSliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        appearanceControlPanel.add(fovSlider, gridBagConstraints);

        pick3DBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        pick3DBox.setText("pick 3D active");
        pick3DBox.setName("pick3DBox"); // NOI18N
        pick3DBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pick3DBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        appearanceControlPanel.add(pick3DBox, gridBagConstraints);

        axesSizeSlider.setMajorTickSpacing(10);
        axesSizeSlider.setMaximum(160);
        axesSizeSlider.setMinimum(40);
        axesSizeSlider.setMinorTickSpacing(2);
        axesSizeSlider.setPaintTicks(true);
        axesSizeSlider.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "orientation glyph size", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        axesSizeSlider.setMinimumSize(new java.awt.Dimension(36, 39));
        axesSizeSlider.setName("axesSizeSlider"); // NOI18N
        axesSizeSlider.setPreferredSize(new java.awt.Dimension(200, 55));
        axesSizeSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                axesSizeSliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        appearanceControlPanel.add(axesSizeSlider, gridBagConstraints);

        orientGlyphType.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        orientGlyphType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Axes", "Medical" }));
        orientGlyphType.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "orientation glyph type", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        orientGlyphType.setMinimumSize(new java.awt.Dimension(68, 46));
        orientGlyphType.setName("orientGlyphType"); // NOI18N
        orientGlyphType.setPreferredSize(new java.awt.Dimension(68, 46));
        orientGlyphType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                orientGlyphTypeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        appearanceControlPanel.add(orientGlyphType, gridBagConstraints);

        jPanel11.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel11.setName("jPanel11"); // NOI18N
        jPanel11.setLayout(new javax.swing.BoxLayout(jPanel11, javax.swing.BoxLayout.LINE_AXIS));

        clipRangeSlider.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "clip range", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        clipRangeSlider.setMax(0.5F);
        clipRangeSlider.setMin(-0.5F);
        clipRangeSlider.setMinimumSize(new java.awt.Dimension(96, 68));
        clipRangeSlider.setName("clipRangeSlider"); // NOI18N
        clipRangeSlider.setPreferredSize(new java.awt.Dimension(216, 70));
        clipRangeSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                clipRangeSliderStateChanged(evt);
            }
        });
        jPanel11.add(clipRangeSlider);

        clipBox.setName("clipBox"); // NOI18N
        clipBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clipBoxActionPerformed(evt);
            }
        });
        jPanel11.add(clipBox);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        appearanceControlPanel.add(jPanel11, gridBagConstraints);

        editorsPane.addTab("general", appearanceControlPanel);

        titleAnnoPanel.setName("titleAnnoPanel"); // NOI18N
        titleAnnoPanel.setLayout(new java.awt.BorderLayout());

        titleEditor.setName("titleEditor"); // NOI18N
        titleAnnoPanel.add(titleEditor, java.awt.BorderLayout.CENTER);

        editorsPane.addTab("title", titleAnnoPanel);

        lightEditorPanel.setName("lightEditorPanel"); // NOI18N
        lightEditorPanel.setLayout(new java.awt.GridBagLayout());

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel1.setText("ambient light");
        jLabel1.setAutoscrolls(true);
        jLabel1.setName("jLabel1"); // NOI18N
        jLabel1.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 8);
        jPanel3.add(jLabel1, gridBagConstraints);

        ambientToggle.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        ambientToggle.setSelected(true);
        ambientToggle.setText("on");
        ambientToggle.setName("ambientToggle"); // NOI18N
        ambientToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ambientToggleActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        jPanel3.add(ambientToggle, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        lightEditorPanel.add(jPanel3, gridBagConstraints);

        jPanel13.setName("jPanel13"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        lightEditorPanel.add(jPanel13, gridBagConstraints);

        directionalLightsEditorPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "directional lights", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 12))); // NOI18N
        directionalLightsEditorPanel.setMinimumSize(new java.awt.Dimension(180, 125));
        directionalLightsEditorPanel.setName("directionalLightsEditorPanel"); // NOI18N
        directionalLightsEditorPanel.setPreferredSize(new java.awt.Dimension(220, 132));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        lightEditorPanel.add(directionalLightsEditorPanel, gridBagConstraints);

        pointLightsEditorPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "point lights", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 12))); // NOI18N
        pointLightsEditorPanel.setMinimumSize(new java.awt.Dimension(180, 100));
        pointLightsEditorPanel.setName("pointLightsEditorPanel"); // NOI18N
        pointLightsEditorPanel.setPreferredSize(new java.awt.Dimension(220, 105));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        lightEditorPanel.add(pointLightsEditorPanel, gridBagConstraints);

        editorsPane.addTab("lights", lightEditorPanel);

        clipEditorPanel.setName("clipEditorPanel"); // NOI18N
        clipEditorPanel.setLayout(new java.awt.BorderLayout());

        clipPlanesUI.setName("clipPlanesUI"); // NOI18N
        clipEditorPanel.add(clipPlanesUI, java.awt.BorderLayout.CENTER);

        editorsPane.addTab("clip", clipEditorPanel);

        animationPanel.setName("animationPanel"); // NOI18N
        animationPanel.setPreferredSize(new java.awt.Dimension(270, 318));
        animationPanel.setLayout(new java.awt.GridBagLayout());

        animateToggle.setText("animate");
        animateToggle.setMaximumSize(new java.awt.Dimension(84, 20));
        animateToggle.setMinimumSize(new java.awt.Dimension(84, 20));
        animateToggle.setName("animateToggle"); // NOI18N
        animateToggle.setPreferredSize(new java.awt.Dimension(84, 20));
        animateToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                animateToggleActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        animationPanel.add(animateToggle, gridBagConstraints);

        allReset.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        allReset.setText("reset all sliders");
        allReset.setMargin(new java.awt.Insets(2, 2, 2, 2));
        allReset.setMaximumSize(new java.awt.Dimension(20, 20));
        allReset.setMinimumSize(new java.awt.Dimension(20, 20));
        allReset.setName("allReset"); // NOI18N
        allReset.setPreferredSize(new java.awt.Dimension(20, 20));
        allReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                allResetActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        animationPanel.add(allReset, gridBagConstraints);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("xy rotation"));
        jPanel4.setName("jPanel4"); // NOI18N
        jPanel4.setPreferredSize(new java.awt.Dimension(140, 154));
        jPanel4.setLayout(new java.awt.BorderLayout());

        xyRotSlider.setMaxX(0.1);
        xyRotSlider.setMaxY(0.1);
        xyRotSlider.setMinX(-0.1);
        xyRotSlider.setMinY(-0.1);
        xyRotSlider.setMinimumSize(new java.awt.Dimension(90, 90));
        xyRotSlider.setName("xyRotSlider"); // NOI18N
        xyRotSlider.setPreferredSize(new java.awt.Dimension(140, 140));
        jPanel4.add(xyRotSlider, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 2.0;
        animationPanel.add(jPanel4, gridBagConstraints);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("xy translation"));
        jPanel6.setName("jPanel6"); // NOI18N
        jPanel6.setLayout(new java.awt.BorderLayout());

        xyTransSlider.setBackground(new java.awt.Color(235, 235, 250));
        xyTransSlider.setMinimumSize(new java.awt.Dimension(90, 90));
        xyTransSlider.setName("xyTransSlider"); // NOI18N
        xyTransSlider.setPreferredSize(new java.awt.Dimension(140, 140));
        jPanel6.add(xyTransSlider, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 2.0;
        animationPanel.add(jPanel6, gridBagConstraints);

        jPanel12.setName("jPanel12"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        animationPanel.add(jPanel12, gridBagConstraints);

        jPanel8.setName("jPanel8"); // NOI18N
        jPanel8.setLayout(new java.awt.GridBagLayout());

        zRotSlider.setMajorTickSpacing(50);
        zRotSlider.setMaximum(50);
        zRotSlider.setMinimum(-50);
        zRotSlider.setPaintTicks(true);
        zRotSlider.setValue(0);
        zRotSlider.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "z rotation", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 10))); // NOI18N
        zRotSlider.setName("zRotSlider"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel8.add(zRotSlider, gridBagConstraints);

        zTransSlider.setMajorTickSpacing(50);
        zTransSlider.setMaximum(50);
        zTransSlider.setMinimum(-50);
        zTransSlider.setPaintTicks(true);
        zTransSlider.setValue(0);
        zTransSlider.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "z translation", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 10))); // NOI18N
        zTransSlider.setName("zTransSlider"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel8.add(zTransSlider, gridBagConstraints);

        animScaleSlider.setMajorTickSpacing(50);
        animScaleSlider.setMaximum(50);
        animScaleSlider.setMinimum(-50);
        animScaleSlider.setPaintTicks(true);
        animScaleSlider.setValue(0);
        animScaleSlider.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "scale", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 10))); // NOI18N
        animScaleSlider.setName("animScaleSlider"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel8.add(animScaleSlider, gridBagConstraints);

        zRotReset.setText(" ");
        zRotReset.setMargin(new java.awt.Insets(2, 2, 2, 2));
        zRotReset.setMaximumSize(new java.awt.Dimension(20, 20));
        zRotReset.setMinimumSize(new java.awt.Dimension(20, 20));
        zRotReset.setName("zRotReset"); // NOI18N
        zRotReset.setPreferredSize(new java.awt.Dimension(20, 20));
        zRotReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zRotResetActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        jPanel8.add(zRotReset, gridBagConstraints);

        zTransReset.setText(" ");
        zTransReset.setMargin(new java.awt.Insets(2, 2, 2, 2));
        zTransReset.setMaximumSize(new java.awt.Dimension(20, 20));
        zTransReset.setMinimumSize(new java.awt.Dimension(20, 20));
        zTransReset.setName("zTransReset"); // NOI18N
        zTransReset.setPreferredSize(new java.awt.Dimension(20, 20));
        zTransReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zTransResetActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        jPanel8.add(zTransReset, gridBagConstraints);

        scaleReset.setText(" ");
        scaleReset.setMargin(new java.awt.Insets(2, 2, 2, 2));
        scaleReset.setMaximumSize(new java.awt.Dimension(20, 20));
        scaleReset.setMinimumSize(new java.awt.Dimension(20, 20));
        scaleReset.setName("scaleReset"); // NOI18N
        scaleReset.setPreferredSize(new java.awt.Dimension(20, 20));
        scaleReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scaleResetActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        jPanel8.add(scaleReset, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        animationPanel.add(jPanel8, gridBagConstraints);

        resetRotationButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        resetRotationButton.setText("reset");
        resetRotationButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        resetRotationButton.setName("resetRotationButton"); // NOI18N
        resetRotationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetRotationButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        animationPanel.add(resetRotationButton, gridBagConstraints);

        resetTranslationButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        resetTranslationButton.setText("reset");
        resetTranslationButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        resetTranslationButton.setName("resetTranslationButton"); // NOI18N
        resetTranslationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetTranslationButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        animationPanel.add(resetTranslationButton, gridBagConstraints);

        editorsPane.addTab("animate", animationPanel);

        imageMoviePanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        imageMoviePanel.setName("imageMoviePanel"); // NOI18N
        imageMoviePanel.setLayout(new java.awt.GridBagLayout());

        jpegButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jpegButton.setText("write jpeg");
        jpegButton.setName("jpegButton"); // NOI18N
        jpegButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jpegButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        imageMoviePanel.add(jpegButton, gridBagConstraints);

        pngButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        pngButton.setText("write png");
        pngButton.setName("pngButton"); // NOI18N
        pngButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pngButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        imageMoviePanel.add(pngButton, gridBagConstraints);

        widthTF.setEnabled(false);
        widthTF.setName("widthTF"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        imageMoviePanel.add(widthTF, gridBagConstraints);

        heightTF.setEnabled(false);
        heightTF.setName("heightTF"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        imageMoviePanel.add(heightTF, gridBagConstraints);

        widthLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        widthLabel.setText("width");
        widthLabel.setEnabled(false);
        widthLabel.setName("widthLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 0, 5);
        imageMoviePanel.add(widthLabel, gridBagConstraints);

        heightLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        heightLabel.setText("height");
        heightLabel.setEnabled(false);
        heightLabel.setName("heightLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 0, 5);
        imageMoviePanel.add(heightLabel, gridBagConstraints);

        useWindowSizeCB.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        useWindowSizeCB.setSelected(true);
        useWindowSizeCB.setText("use window size");
        useWindowSizeCB.setName("useWindowSizeCB"); // NOI18N
        useWindowSizeCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useWindowSizeCBActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        imageMoviePanel.add(useWindowSizeCB, gridBagConstraints);

        movieCreationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Movie creation controls"));
        movieCreationPanel.setMinimumSize(new java.awt.Dimension(373, 282));
        movieCreationPanel.setName("movieCreationPanel"); // NOI18N
        movieCreationPanel.setPreferredSize(new java.awt.Dimension(270, 282));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        imageMoviePanel.add(movieCreationPanel, gridBagConstraints);

        jLabel2.setText("Output image file");
        jLabel2.setName("jLabel2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        imageMoviePanel.add(jLabel2, gridBagConstraints);

        jPanel10.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jPanel10.setName("jPanel10"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        imageMoviePanel.add(jPanel10, gridBagConstraints);

        editorsPane.addTab("output", imageMoviePanel);

        add(editorsPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

   private void mouseRotationSensitivitySliderStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_mouseRotationSensitivitySliderStateChanged
   {//GEN-HEADEREND:event_mouseRotationSensitivitySliderStateChanged
      if (display3DPanel != null && !mouseRotationSensitivitySlider.getValueIsAdjusting())
      {
         double f = Math.pow(10., mouseRotationSensitivitySlider.getValue()/20.-3);
         display3DPanel.setMouseRotateSensitivity(f);
      }
}//GEN-LAST:event_mouseRotationSensitivitySliderStateChanged

   private void mouseTranslationSensitivitySliderStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_mouseTranslationSensitivitySliderStateChanged
   {//GEN-HEADEREND:event_mouseTranslationSensitivitySliderStateChanged
      if (display3DPanel != null && !mouseTranslationSensitivitySlider.getValueIsAdjusting())
      {
         double f = Math.pow(10., mouseTranslationSensitivitySlider.getValue()/20.-3);
         display3DPanel.setMouseTranslateSensitivity(f);
      }
}//GEN-LAST:event_mouseTranslationSensitivitySliderStateChanged

   private void mouseWheelSensitivitySliderStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_mouseWheelSensitivitySliderStateChanged
   {//GEN-HEADEREND:event_mouseWheelSensitivitySliderStateChanged
      if (display3DPanel!=null)
         display3DPanel.setMouseWheelSensitivity(Math.pow(1.05,mouseWheelSensitivitySlider.getValue()/30.));
}//GEN-LAST:event_mouseWheelSensitivitySliderStateChanged

   private void stereoSeparationSliderStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_stereoSeparationSliderStateChanged
   {//GEN-HEADEREND:event_stereoSeparationSliderStateChanged
      if (display3DPanel!=null)
         display3DPanel.setStereoSeparation(stereoSeparationSlider.getValue()/400.f);
}//GEN-LAST:event_stereoSeparationSliderStateChanged

   private void moveCameraButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_moveCameraButtonActionPerformed
   {//GEN-HEADEREND:event_moveCameraButtonActionPerformed
      if (display3DPanel!=null)
         display3DPanel.setMoveCameraMode(moveCameraButton.isSelected());
}//GEN-LAST:event_moveCameraButtonActionPerformed

   private void backgroundColorEditorStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_backgroundColorEditorStateChanged
   {//GEN-HEADEREND:event_backgroundColorEditorStateChanged
      updateBackground();
}//GEN-LAST:event_backgroundColorEditorStateChanged

   private void resetButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_resetButtonActionPerformed
   {//GEN-HEADEREND:event_resetButtonActionPerformed
      if (display3DPanel!=null)
         display3DPanel.reset();
}//GEN-LAST:event_resetButtonActionPerformed

   private void dispToggleActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_dispToggleActionPerformed
   {//GEN-HEADEREND:event_dispToggleActionPerformed
      if (display3DPanel!=null)
         display3DPanel.setDisplayMode(dispToggle.isSelected());
}//GEN-LAST:event_dispToggleActionPerformed

   private void stereoToggleActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_stereoToggleActionPerformed
   {//GEN-HEADEREND:event_stereoToggleActionPerformed
      if (display3DPanel!=null)
      {
         if (stereoToggle.isSelected())
         {
            dispToggle.setSelected(true);
            display3DPanel.setDisplayMode(true);
         }
         dispToggle.setEnabled(!stereoToggle.isSelected());
         stereoSeparationSlider.setEnabled(stereoToggle.isSelected());
         display3DPanel.setStereoEnable(stereoToggle.isSelected());
      }
}//GEN-LAST:event_stereoToggleActionPerformed

   private void fogRangeSliderStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_fogRangeSliderStateChanged
   {//GEN-HEADEREND:event_fogRangeSliderStateChanged
      display3DPanel.setFogRange(Math.exp(fogRangeSlider.getBottomValue()), Math.exp(fogRangeSlider.getTopValue()));
}//GEN-LAST:event_fogRangeSliderStateChanged

   private void depthCueBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_depthCueBoxActionPerformed
   {//GEN-HEADEREND:event_depthCueBoxActionPerformed
      if (display3DPanel != null)
         display3DPanel.setFogActive(depthCueBox.isSelected());
}//GEN-LAST:event_depthCueBoxActionPerformed

   private void fovSliderStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_fovSliderStateChanged
   {//GEN-HEADEREND:event_fovSliderStateChanged
      display3DPanel.setFoV(fovSlider.getValue());
}//GEN-LAST:event_fovSliderStateChanged

   private void pick3DBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_pick3DBoxActionPerformed
   {//GEN-HEADEREND:event_pick3DBoxActionPerformed
      if (display3DPanel != null)
         display3DPanel.setPick3DActive(pick3DBox.isSelected());
}//GEN-LAST:event_pick3DBoxActionPerformed

   private void axesSizeSliderStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_axesSizeSliderStateChanged
   {//GEN-HEADEREND:event_axesSizeSliderStateChanged
      if (display3DPanel != null)
         display3DPanel.setReperSize(axesSizeSlider.getValue());
}//GEN-LAST:event_axesSizeSliderStateChanged

   private void ambientToggleActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_ambientToggleActionPerformed
   {//GEN-HEADEREND:event_ambientToggleActionPerformed
      ambientLight.setEnabled(ambientToggle.isSelected());
}//GEN-LAST:event_ambientToggleActionPerformed

   private void animateToggleActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_animateToggleActionPerformed
   {//GEN-HEADEREND:event_animateToggleActionPerformed
      if (animateToggle.isSelected())
         display3DPanel.animate();
}//GEN-LAST:event_animateToggleActionPerformed

   private void allResetActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_allResetActionPerformed
   {//GEN-HEADEREND:event_allResetActionPerformed
      xyRotSlider.setValX(0.);
      xyRotSlider.setValY(0.);
      zRotSlider.setValue(0);
      xyTransSlider.setValX(0.);
      xyTransSlider.setValY(0.);
      zTransSlider.setValue(0);
      animScaleSlider.setValue(0);
}//GEN-LAST:event_allResetActionPerformed

   private void zRotResetActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_zRotResetActionPerformed
   {//GEN-HEADEREND:event_zRotResetActionPerformed
      zRotSlider.setValue(0);
}//GEN-LAST:event_zRotResetActionPerformed

   private void zTransResetActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_zTransResetActionPerformed
   {//GEN-HEADEREND:event_zTransResetActionPerformed
      zTransSlider.setValue(0);
}//GEN-LAST:event_zTransResetActionPerformed

   private void scaleResetActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_scaleResetActionPerformed
   {//GEN-HEADEREND:event_scaleResetActionPerformed
      animScaleSlider.setValue(0);
}//GEN-LAST:event_scaleResetActionPerformed

   private void jpegButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jpegButtonActionPerformed
   {//GEN-HEADEREND:event_jpegButtonActionPerformed
      boolean ok = false;
      int w = 0, h = 0;
      try
      {
         w = Integer.parseInt(widthTF.getText());
         h = Integer.parseInt(heightTF.getText());
         ok = true;
      }
      catch(NumberFormatException ex)
      {
         ok = false;
      }
      
      if(!useWindowSizeCB.isSelected() && ok && w > 16 && h > 16)
      {
         display3DPanel.newOffScreen(w,h);
      }
      else
      {
         display3DPanel.newOffScreen();
      }
      int returnVal = jpegChooser.showSaveDialog(null);
      if (returnVal == JFileChooser.APPROVE_OPTION)
         display3DPanel.writeJpeg( VNFileChooser.filenameWithExtenstionAddedIfNecessary( jpegChooser.getSelectedFile(), jpegFilter ) );
}//GEN-LAST:event_jpegButtonActionPerformed

   private void pngButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_pngButtonActionPerformed
   {//GEN-HEADEREND:event_pngButtonActionPerformed
      boolean ok = false;
      int w = 0, h = 0;
      try
      {
         w = Integer.parseInt(widthTF.getText());
         h = Integer.parseInt(heightTF.getText());
         ok = true;
      }
      catch(NumberFormatException ex)
      {
         ok = false;
      }
      
      Dimension d = display3DPanel.getOffScreenSize();
      if (!useWindowSizeCB.isSelected() && ok && w > 16 && h > 16) {
           if(d == null || (d != null && (w != d.width || h != d.height)))
                display3DPanel.newOffScreen(w, h);
      } else {
          if(d == null || (d != null && (windowWidth != d.width || windowHeight != d.height)))
                display3DPanel.newOffScreen();
      }
      int returnVal = pngChooser.showSaveDialog(null);
      if (returnVal == JFileChooser.APPROVE_OPTION)
         display3DPanel.writePNG( VNFileChooser.filenameWithExtenstionAddedIfNecessary(pngChooser.getSelectedFile(), pngFilter) );
}//GEN-LAST:event_pngButtonActionPerformed

   public MovieCreationPanel getMovieCreationPanel()
   {
      return movieCreationPanel;
   }
   
   public boolean animate()
   {
      return animateToggle.isSelected();
   }
   
   private double[] animationParams = new double[7];
   
   public double[] getAnimationParams()
   {
      double rX = xyRotSlider.getValX();
      double rY = xyRotSlider.getValY();
      double rRot = Math.sqrt(rX * rX + rY * rY);
      if (rRot > 0)
      {
         double f = Math.exp(8 * rRot) - 1;
         rX *= f / rRot;
         rY *= f / rRot;
      }
      animationParams[0] = -Math.PI / 180. * rY;
      animationParams[1] =  Math.PI / 180. * rX;
      animationParams[2] = Math.PI/90000.*zRotSlider.getValue();
      animationParams[3] = .0002*xyTransSlider.getValX();
      animationParams[4] = .0002*xyTransSlider.getValY();
      animationParams[5] = .0001*zTransSlider.getValue();
      animationParams[6] = Math.pow(2., animScaleSlider.getValue()/5000.);
      return animationParams;
   }

   
   public boolean isActive()
   {
      return active;
   }

   public void togglePerspective()
   {
      boolean p = dispToggle.isSelected();
      dispToggle.setSelected(!p);
   }

   public void toggleStereo()
   {
      boolean p = stereoToggle.isSelected();
      stereoToggle.setSelected(!p);
   }

   public void togglePick3D()
   {
      boolean p = pick3DBox.isSelected();
      pick3DBox.setSelected(!p);
   }

   public void setExtents(float[][] extents)
   {
      clipPlanesUI.setExtents(extents);
   }

   private int windowWidth = 0;
   private int windowHeight = 0;
   public void setWindowDimensions(int width, int height) {
       windowHeight = height;
       windowWidth = width;
   }
   
   
   private void useWindowSizeCBActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_useWindowSizeCBActionPerformed
   {//GEN-HEADEREND:event_useWindowSizeCBActionPerformed
      if(!useWindowSizeCB.isSelected())
      {
         widthTF.setText(""+windowWidth);
         heightTF.setText(""+windowHeight);
      }
      widthTF.setEnabled(!useWindowSizeCB.isSelected());
      widthLabel.setEnabled(!useWindowSizeCB.isSelected());
      heightTF.setEnabled(!useWindowSizeCB.isSelected());
      heightLabel.setEnabled(!useWindowSizeCB.isSelected());
}//GEN-LAST:event_useWindowSizeCBActionPerformed

private void clipRangeSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_clipRangeSliderStateChanged
      display3DPanel.setClipRange(clipBox.isSelected() ? Math.pow(10.,clipRangeSlider.getLow()) : 0.001, 
                         clipBox.isSelected() ? Math.pow(10.,clipRangeSlider.getUp()) : 100);
}//GEN-LAST:event_clipRangeSliderStateChanged

private void clipBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clipBoxActionPerformed
      display3DPanel.setClipRange(clipBox.isSelected() ? Math.pow(10.,clipRangeSlider.getLow()) : 0.001, 
                         clipBox.isSelected() ? Math.pow(10.,clipRangeSlider.getUp()) : 100);
}//GEN-LAST:event_clipBoxActionPerformed

    private void orientGlyphTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_orientGlyphTypeActionPerformed
      if (display3DPanel!=null)
      {
          
          switch(orientGlyphType.getSelectedIndex()) {
              case 0:
                  display3DPanel.setReperType(Display3DPanel.ReperType.AXES);
                  break;
              case 1:
                  display3DPanel.setReperType(Display3DPanel.ReperType.MEDICAL);
                  break;
          }
      }
    }//GEN-LAST:event_orientGlyphTypeActionPerformed

    private int bgrType = BGR_COLOR;
    private String imageFileName = null;
    
    private void setBgrType()
    {
       if (bgrColorButton.isSelected()) bgrType = BGR_COLOR;
       if (bgrGradButton.isSelected())  bgrType = BGR_GRADIENT;
       if (bgrImageButton.isSelected()) 
       {
          backgroundColorEditor.setBrightness(100);
          bgrType = BGR_IMAGE;
       }
       updateBackground();
    }
    
    private void updateBackground()
    {
       backgroundColorEditor1.setVisible(bgrType == BGR_GRADIENT);
       backgroundColorEditor2.setVisible(bgrType == BGR_GRADIENT);
       bgrImageReadButton.setVisible(bgrType  == BGR_IMAGE);
       if (display3DPanel == null)
          return;
       switch (bgrType)
       {
          case BGR_COLOR:
             display3DPanel.setBackgroundColor(backgroundColorEditor.getColor());
             titleEditor.setBgrColor(display3DPanel.getBackgroundColor());
             break;
          case BGR_GRADIENT:
             display3DPanel.setBackgroundGradient(backgroundColorEditor.getColor(), 
                                                  backgroundColorEditor1.getColor(), 
                                                  backgroundColorEditor2.getColor());
             titleEditor.setBgrColor(display3DPanel.getBackgroundColor());
             break;
          case BGR_IMAGE:
             display3DPanel.setBgrImageBrightness(backgroundColorEditor.getBrightness() / 100.f);
       }
    }
    
    public void releaseLightEdit()
    {
         pointLightsEditorPanel.releasePositionEdit();
         directionalLightsEditorPanel.releaseDirectionEdit();
    }
    
   private void backgroundColorEditor1StateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_backgroundColorEditor1StateChanged
   {//GEN-HEADEREND:event_backgroundColorEditor1StateChanged
      updateBackground();
   }//GEN-LAST:event_backgroundColorEditor1StateChanged

   private void bgrColorButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bgrColorButtonActionPerformed
   {//GEN-HEADEREND:event_bgrColorButtonActionPerformed
      setBgrType();
   }//GEN-LAST:event_bgrColorButtonActionPerformed

   private void bgrGradButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bgrGradButtonActionPerformed
   {//GEN-HEADEREND:event_bgrGradButtonActionPerformed
      setBgrType();
   }//GEN-LAST:event_bgrGradButtonActionPerformed

   private void bgrImageButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bgrImageButtonActionPerformed
   {//GEN-HEADEREND:event_bgrImageButtonActionPerformed
      setBgrType();
   }//GEN-LAST:event_bgrImageButtonActionPerformed

   private void bgrImageReadButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bgrImageReadButtonActionPerformed
   {//GEN-HEADEREND:event_bgrImageReadButtonActionPerformed
      
      if (bgrImageFileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
      {
         if (display3DPanel == null)
             return;
         String path = bgrImageFileChooser.getSelectedFile().getAbsolutePath();
         display3DPanel.setBackgroundImage(path);
      }
   }//GEN-LAST:event_bgrImageReadButtonActionPerformed

   private void backgroundColorEditor2StateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_backgroundColorEditor2StateChanged
   {//GEN-HEADEREND:event_backgroundColorEditor2StateChanged
      updateBackground(); 
   }//GEN-LAST:event_backgroundColorEditor2StateChanged

   private void editorsPaneStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_editorsPaneStateChanged
   {//GEN-HEADEREND:event_editorsPaneStateChanged
      if (!lightEditorPanel.isVisible())
         releaseLightEdit();
   }//GEN-LAST:event_editorsPaneStateChanged

   private void resetRotationButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_resetRotationButtonActionPerformed
   {//GEN-HEADEREND:event_resetRotationButtonActionPerformed
      xyRotSlider.setValX(0.);
      xyRotSlider.setValY(0.);
   }//GEN-LAST:event_resetRotationButtonActionPerformed

   private void resetTranslationButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_resetTranslationButtonActionPerformed
   {//GEN-HEADEREND:event_resetTranslationButtonActionPerformed
      
      xyTransSlider.setValX(0.);
      xyTransSlider.setValY(0.);
   }//GEN-LAST:event_resetTranslationButtonActionPerformed

    private void lockViewButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lockViewButtonActionPerformed
        if(display3DPanel != null)
            display3DPanel.setLockView(lockViewButton.isSelected());
    }//GEN-LAST:event_lockViewButtonActionPerformed

    public void setLockViewButtonState(boolean selected) {
        this.lockViewButton.setSelected(selected);
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton allReset;
    private javax.swing.JCheckBox ambientToggle;
    private javax.swing.JSlider animScaleSlider;
    private javax.swing.JToggleButton animateToggle;
    private javax.swing.JPanel animationPanel;
    private javax.swing.JPanel appearanceControlPanel;
    private javax.swing.JSlider axesSizeSlider;
    private pl.edu.icm.visnow.gui.widgets.ColorEditor backgroundColorEditor;
    private pl.edu.icm.visnow.gui.widgets.ColorEditor backgroundColorEditor1;
    private pl.edu.icm.visnow.gui.widgets.ColorEditor backgroundColorEditor2;
    private javax.swing.JRadioButton bgrColorButton;
    private javax.swing.JRadioButton bgrGradButton;
    private javax.swing.JRadioButton bgrImageButton;
    private javax.swing.JButton bgrImageReadButton;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox clipBox;
    private javax.swing.JPanel clipEditorPanel;
    private pl.edu.icm.visnow.geometries.viewer3d.controls.clip_editor.ClipPlanesUI clipPlanesUI;
    private pl.edu.icm.visnow.gui.widgets.FloatSubRangeSlider.ExtendedFloatSubRangeSlider clipRangeSlider;
    private javax.swing.JCheckBox depthCueBox;
    private pl.edu.icm.visnow.geometries.viewer3d.controls.light_editor.DirectionalLightsEditorPanel directionalLightsEditorPanel;
    private javax.swing.JToggleButton dispToggle;
    private javax.swing.JTabbedPane editorsPane;
    private pl.edu.icm.visnow.gui.widgets.FloatSubRangeSlider.FloatSubRangeSlider fogRangeSlider;
    private javax.swing.JSlider fovSlider;
    private javax.swing.JLabel heightLabel;
    private javax.swing.JTextField heightTF;
    private javax.swing.JPanel imageMoviePanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JButton jpegButton;
    private javax.swing.JPanel lightEditorPanel;
    private javax.swing.JToggleButton lockViewButton;
    private javax.swing.JSlider mouseRotationSensitivitySlider;
    private javax.swing.JSlider mouseTranslationSensitivitySlider;
    private javax.swing.JSlider mouseWheelSensitivitySlider;
    private javax.swing.JToggleButton moveCameraButton;
    private pl.edu.icm.visnow.geometries.viewer3d.controls.MovieCreationPanel movieCreationPanel;
    private javax.swing.JComboBox orientGlyphType;
    private javax.swing.JCheckBox pick3DBox;
    private javax.swing.JButton pngButton;
    private pl.edu.icm.visnow.geometries.viewer3d.controls.light_editor.PointLightsEditorPanel pointLightsEditorPanel;
    private javax.swing.JButton resetButton;
    private javax.swing.JButton resetRotationButton;
    private javax.swing.JButton resetTranslationButton;
    private javax.swing.JButton scaleReset;
    private javax.swing.JSlider stereoSeparationSlider;
    private javax.swing.JToggleButton stereoToggle;
    private javax.swing.JPanel titleAnnoPanel;
    private pl.edu.icm.visnow.geometries.viewer3d.controls.TitleEditorPanel titleEditor;
    private javax.swing.JCheckBox useWindowSizeCB;
    private javax.swing.JLabel widthLabel;
    private javax.swing.JTextField widthTF;
    private pl.edu.icm.visnow.gui.widgets.DoubleSlider xyRotSlider;
    private pl.edu.icm.visnow.gui.widgets.DoubleSlider xyTransSlider;
    private javax.swing.JButton zRotReset;
    private javax.swing.JSlider zRotSlider;
    private javax.swing.JButton zTransReset;
    private javax.swing.JSlider zTransSlider;
    // End of variables declaration//GEN-END:variables
}
