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
package pl.edu.icm.visnow.lib.basic.readers.ReadImage;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Locale;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import pl.edu.icm.visnow.lib.gui.Browser;
import pl.edu.icm.visnow.lib.gui.grid.GridFrame;
import pl.edu.icm.visnow.lib.utils.io.InputSource;
import pl.edu.icm.visnow.system.main.VisNow;

/**
 * @author Bartosz Borucki (babor@icm.edu.pl) Warsaw University,
 * Interdisciplinary Centre for Mathematical and Computational Modelling
 */
public class GUI extends javax.swing.JPanel {

    private String lastPath = null;
    private Params params = new Params();
    private String[] extensions = new String[]{"jpg", "jpeg", "gif", "png", "JPG", "JPEG", "GIF", "PNG", "raw", "dat", "RAW", "DAT"};
    private Browser browser = new Browser(extensions);
    private GridFrame gridFrame = new GridFrame();
    private FileNameExtensionFilter allImagesFilter =
            new FileNameExtensionFilter("All image files", "bmp", "BMP", "gif", "GIF", "jpg", "jpeg", "JPG", "JPEG", "icns", "ICNS", "ico", "ICO", "cur", "CUR",
            "pcx", "PCX", "dcx", "DCX", "pnm", "PNM", "pgm", "PGM", "pbm", "PBM", "ppm", "PPM", "pam", "PAM", "png", "PNG", "psd", "PSD", "hdr", "HDR",
            "tif", "TIF", "tiff", "TIFF", "wbmp", "WBMP", "xbm", "XBM", "xpm", "XPM", "raw", "dat", "RAW", "DAT");
    private FileNameExtensionFilter bmpImagesFilter = new FileNameExtensionFilter("BMP images (*.bmp)", "bmp", "BMP");
    private FileNameExtensionFilter gifImagesFilter = new FileNameExtensionFilter("GIF images (*.gif)", "gif", "GIF");
    private FileNameExtensionFilter jpegImagesFilter = new FileNameExtensionFilter("JPEG images (*.jpg, *.jpeg)", "jpg", "jpeg", "JPG", "JPEG");
    private FileNameExtensionFilter icnsImagesFilter = new FileNameExtensionFilter("ICNS images (*.icns)", "icns", "ICNS");
    private FileNameExtensionFilter icoImagesFilter = new FileNameExtensionFilter("ICO images (*.ico, *.cur)", "ico", "ICO", "cur", "CUR");
    private FileNameExtensionFilter pcxImagesFilter = new FileNameExtensionFilter("PCX images (*.pcx, *.dcx)", "pcx", "PCX", "dcx", "DCX");
    private FileNameExtensionFilter pnmImagesFilter = new FileNameExtensionFilter("PNM images (*.pnm, *.pgm, *.pbm, *.ppm, *.pam)", "pnm", "PNM", "pgm", "PGM", "pbm", "PBM", "ppm", "PPM", "pam", "PAM");
    private FileNameExtensionFilter pngImagesFilter = new FileNameExtensionFilter("PNG images (*.png)", "png", "PNG");
    private FileNameExtensionFilter psdImagesFilter = new FileNameExtensionFilter("PSD images (*.psd)", "psd", "PSD");
    private FileNameExtensionFilter rgbeImagesFilter = new FileNameExtensionFilter("RGBE images (*.hdr)", "hdr", "HDR");
    private FileNameExtensionFilter tiffImagesFilter = new FileNameExtensionFilter("TIFF images (*.tif, *.tiff)", "tif", "TIF", "tiff", "TIFF");
    private FileNameExtensionFilter wbmpImagesFilter = new FileNameExtensionFilter("WBMP images (*.wbmp)", "wbmp", "WBMP");
    private FileNameExtensionFilter xbmImagesFilter = new FileNameExtensionFilter("XBM images (*.xbm)", "xbm", "XBM");
    private FileNameExtensionFilter xpmImagesFilter = new FileNameExtensionFilter("XPM  images (*.xpm)", "xpm", "XPM");
    private Image image = null;
    private JFileChooser fileChooser = new JFileChooser();

    /**
     * Creates new form GUI
     */
    public GUI() {

        initComponents();
        messageLabel.setText("");
        gridFrame.setFileExtensions(extensions);
        gridFrame.setSingleFile(true);
        browser.setVisible(false);
        browser.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                params.setSource(InputSource.URL);
                params.setFiles(new String[]{browser.getCurrentURL()});
                imagePath.setText(params.getFiles()[0]);
                params.fireStateChanged();
            }
        });

        gridFrame.setVisible(false);
        gridFrame.setFileExtensions(extensions);
        gridFrame.setSingleFile(true);
        gridFrame.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                params.setSource(InputSource.GRID);
                String path = VisNow.getTmpDirPath() + "/" + gridFrame.getTransferredFileNames()[0];
                imagePath.setText(path);
                params.setFiles(new String[]{path});
            }
        });

        fileChooser.addChoosableFileFilter(allImagesFilter);
        fileChooser.addChoosableFileFilter(bmpImagesFilter);
        fileChooser.addChoosableFileFilter(gifImagesFilter);
        fileChooser.addChoosableFileFilter(jpegImagesFilter);
        fileChooser.addChoosableFileFilter(icnsImagesFilter);
        fileChooser.addChoosableFileFilter(icoImagesFilter);
        fileChooser.addChoosableFileFilter(pcxImagesFilter);
        fileChooser.addChoosableFileFilter(pnmImagesFilter);
        fileChooser.addChoosableFileFilter(pngImagesFilter);
        fileChooser.addChoosableFileFilter(psdImagesFilter);
        fileChooser.addChoosableFileFilter(rgbeImagesFilter);
        fileChooser.addChoosableFileFilter(tiffImagesFilter);
        fileChooser.addChoosableFileFilter(wbmpImagesFilter);
        fileChooser.addChoosableFileFilter(xbmImagesFilter);
        fileChooser.addChoosableFileFilter(xpmImagesFilter);

        fileChooser.setLocation(0, 0);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(allImagesFilter);

    }

    public void setParams(Params p) {
        this.params = p;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup = new javax.swing.ButtonGroup();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel3 = new javax.swing.JPanel();
        colorRB = new javax.swing.JRadioButton();
        grayscaleRB = new javax.swing.JRadioButton();
        rTF = new javax.swing.JTextField();
        gTF = new javax.swing.JTextField();
        bTF = new javax.swing.JTextField();
        rLabel = new javax.swing.JLabel();
        gLabel = new javax.swing.JLabel();
        bLabel = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        preset1Button = new javax.swing.JButton();
        preset2Button = new javax.swing.JButton();
        preset3Button = new javax.swing.JButton();
        showCB = new javax.swing.JCheckBox();
        singleMultiplePane = new javax.swing.JTabbedPane();
        singleImageTab = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        imagePath = new javax.swing.JTextField();
        browseButton = new javax.swing.JButton();
        messageLabel = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        imageDescription = new javax.swing.JLabel();
        imagePanel = new JPanel()
        {
            public void paint(Graphics g)
            {
                g.setColor(Color.LIGHT_GRAY);
                g.fillRect(0, 0, getWidth(), getHeight());
                if ((image == null)) return;
                int w = getWidth();
                int h = getHeight();
                float iw = image.getWidth(null);
                float ih = image.getHeight(null);
                if (iw <= w && ih <= h)
                {
                    w = (int) iw;
                    h = (int) ih;
                }
                else
                {
                    if (w / iw < h / ih)
                    h = (int)(w * ih / iw);
                    else
                    w = (int)(h * iw / ih);
                }
                g.drawImage(image, 0, 0, w, h, null);
            }
        };
        fileButton = new javax.swing.JRadioButton();
        urlButton = new javax.swing.JRadioButton();
        gridButton = new javax.swing.JRadioButton();
        multipleImagesTab = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        openMultipleButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel5 = new javax.swing.JPanel();
        imageList = new javax.swing.JTextPane();

        setRequestFocusEnabled(false);
        setLayout(new java.awt.GridBagLayout());

        jPanel3.setLayout(new java.awt.GridBagLayout());

        buttonGroup1.add(colorRB);
        colorRB.setSelected(true);
        colorRB.setText("color");
        colorRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorRBActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        jPanel3.add(colorRB, gridBagConstraints);

        buttonGroup1.add(grayscaleRB);
        grayscaleRB.setText("grayscale");
        grayscaleRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                grayscaleRBActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        jPanel3.add(grayscaleRB, gridBagConstraints);

        rTF.setText("0.30");
        rTF.setEnabled(false);
        rTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rTFActionPerformed(evt);
            }
        });
        rTF.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                rTFFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        jPanel3.add(rTF, gridBagConstraints);

        gTF.setText("0.59");
        gTF.setEnabled(false);
        gTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gTFActionPerformed(evt);
            }
        });
        gTF.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                gTFFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        jPanel3.add(gTF, gridBagConstraints);

        bTF.setText("0.11");
        bTF.setEnabled(false);
        bTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bTFActionPerformed(evt);
            }
        });
        bTF.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                bTFFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        jPanel3.add(bTF, gridBagConstraints);

        rLabel.setText("R:");
        rLabel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel3.add(rLabel, gridBagConstraints);

        gLabel.setText("G:");
        gLabel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel3.add(gLabel, gridBagConstraints);

        bLabel.setText("B:");
        bLabel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel3.add(bLabel, gridBagConstraints);

        jPanel4.setLayout(new java.awt.GridBagLayout());

        preset1Button.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        preset1Button.setText("preset #1");
        preset1Button.setEnabled(false);
        preset1Button.setMargin(new java.awt.Insets(2, 2, 2, 2));
        preset1Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                preset1ButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 3, 5, 2);
        jPanel4.add(preset1Button, gridBagConstraints);

        preset2Button.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        preset2Button.setText("preset #2");
        preset2Button.setEnabled(false);
        preset2Button.setMargin(new java.awt.Insets(2, 2, 2, 2));
        preset2Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                preset2ButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 3, 5, 2);
        jPanel4.add(preset2Button, gridBagConstraints);

        preset3Button.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        preset3Button.setText("preset #3");
        preset3Button.setEnabled(false);
        preset3Button.setMargin(new java.awt.Insets(2, 2, 2, 2));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 3, 5, 2);
        jPanel4.add(preset3Button, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        jPanel3.add(jPanel4, gridBagConstraints);

        showCB.setSelected(true);
        showCB.setText("show");
        showCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showCBActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel3.add(showCB, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        add(jPanel3, gridBagConstraints);

        singleMultiplePane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                singleMultiplePaneStateChanged(evt);
            }
        });

        singleImageTab.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("Read image");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 5, 0);
        singleImageTab.add(jLabel1, gridBagConstraints);

        imagePath.setMaximumSize(null);
        imagePath.setMinimumSize(new java.awt.Dimension(180, 20));
        imagePath.setPreferredSize(new java.awt.Dimension(180, 20));
        imagePath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                imagePathActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        singleImageTab.add(imagePath, gridBagConstraints);

        browseButton.setText("Browse...");
        browseButton.setMargin(new java.awt.Insets(2, 7, 2, 7));
        browseButton.setPreferredSize(new java.awt.Dimension(88, 20));
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 4, 5, 5);
        singleImageTab.add(browseButton, gridBagConstraints);

        messageLabel.setText(" ");
        messageLabel.setMaximumSize(new java.awt.Dimension(200, 17));
        messageLabel.setMinimumSize(new java.awt.Dimension(4, 17));
        messageLabel.setPreferredSize(new java.awt.Dimension(180, 17));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 10, 5);
        singleImageTab.add(messageLabel, gridBagConstraints);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new java.awt.GridBagLayout());

        imageDescription.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        imageDescription.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(imageDescription, gridBagConstraints);

        imagePanel.setMinimumSize(new java.awt.Dimension(10, 400));
        imagePanel.setPreferredSize(new java.awt.Dimension(10, 400));
        imagePanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(imagePanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        singleImageTab.add(jPanel1, gridBagConstraints);

        buttonGroup.add(fileButton);
        fileButton.setSelected(true);
        fileButton.setText("file");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        singleImageTab.add(fileButton, gridBagConstraints);

        buttonGroup.add(urlButton);
        urlButton.setText("URL");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        singleImageTab.add(urlButton, gridBagConstraints);

        buttonGroup.add(gridButton);
        gridButton.setText("grid");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        singleImageTab.add(gridButton, gridBagConstraints);

        singleMultiplePane.addTab("single", singleImageTab);

        multipleImagesTab.setLayout(new java.awt.GridBagLayout());

        jLabel2.setText("Open multiple images");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 5);
        multipleImagesTab.add(jLabel2, gridBagConstraints);

        openMultipleButton.setText("Browse...");
        openMultipleButton.setMargin(new java.awt.Insets(2, 6, 2, 6));
        openMultipleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openMultipleButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        multipleImagesTab.add(openMultipleButton, gridBagConstraints);

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel5.setLayout(new java.awt.BorderLayout());

        imageList.setEditable(false);
        imageList.setPreferredSize(new java.awt.Dimension(10, 10));
        jPanel5.add(imageList, java.awt.BorderLayout.CENTER);

        jScrollPane1.setViewportView(jPanel5);

        jPanel2.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        multipleImagesTab.add(jPanel2, gridBagConstraints);

        singleMultiplePane.addTab("sequence", multipleImagesTab);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(singleMultiplePane, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void imagePathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_imagePathActionPerformed
        String[] files = new String[1];
        files[0] = imagePath.getText();
        params.setSource(InputSource.FILE);
        params.setFiles(files);
    }//GEN-LAST:event_imagePathActionPerformed

    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
        if (fileButton.isSelected()) {
            if (lastPath == null) {
                fileChooser.setCurrentDirectory(new File(VisNow.get().getMainConfig().getUsableDataPath(ReadImage.class)));
            } else {
                fileChooser.setCurrentDirectory(new File(lastPath));
            }
            fileChooser.setMultiSelectionEnabled(false);

            int returnVal = fileChooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                String path = fileChooser.getSelectedFile().getAbsolutePath();
                lastPath = path.substring(0, path.lastIndexOf(File.separator));
                VisNow.get().getMainConfig().setLastDataPath(lastPath, ReadImage.class);
                imagePath.setText(path);
                String[] files = new String[1];
                files[0] = imagePath.getText();
                params.setSource(InputSource.FILE);
                params.setSequenceMode(false);
                params.setFiles(files);
            }
        } else if (urlButton.isSelected()) {
            browser.setVisible(true);
        } else if (gridButton.isSelected()) {
            gridFrame.setVisible(true);
        }
    }//GEN-LAST:event_browseButtonActionPerformed

    private void singleMultiplePaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_singleMultiplePaneStateChanged
        params.setFiles(null);
        if(singleMultiplePane.getSelectedIndex() == 1) {
            rLabel.setEnabled(false);
            gLabel.setEnabled(false);
            bLabel.setEnabled(false);
            rTF.setEnabled(false);
            gTF.setEnabled(false);
            bTF.setEnabled(false);
            preset1Button.setEnabled(false);
            preset2Button.setEnabled(false);
            preset3Button.setEnabled(false);
            colorRB.setEnabled(false);
            grayscaleRB.setEnabled(false);
            showCB.setEnabled(false);
        }
        else {
            colorRB.setEnabled(true);
            grayscaleRB.setEnabled(true);
            showCB.setEnabled(true);
            if(grayscaleRB.isSelected()) {
                rLabel.setEnabled(true);
                gLabel.setEnabled(true);
                bLabel.setEnabled(true);
                rTF.setEnabled(true);
                gTF.setEnabled(true);
                bTF.setEnabled(true);
                preset1Button.setEnabled(true);
                preset2Button.setEnabled(true);
                preset3Button.setEnabled(true);                
            }
            else {
                rLabel.setEnabled(false);
                gLabel.setEnabled(false);
                bLabel.setEnabled(false);
                rTF.setEnabled(false);
                gTF.setEnabled(false);
                bTF.setEnabled(false);
                preset1Button.setEnabled(false);
                preset2Button.setEnabled(false);
                preset3Button.setEnabled(false);
            }
        }
    }//GEN-LAST:event_singleMultiplePaneStateChanged

    private void openMultipleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openMultipleButtonActionPerformed
        if (lastPath == null) {
            fileChooser.setCurrentDirectory(new File(VisNow.get().getMainConfig().getUsableDataPath(ReadImage.class)));
        } else {
            fileChooser.setCurrentDirectory(new File(lastPath));
        }
        fileChooser.setMultiSelectionEnabled(true);

        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File[] files = fileChooser.getSelectedFiles();
            if (files == null) {
                return;
            }
            else if(files.length == 0) {
                files = new File[] {fileChooser.getSelectedFile()};
            }

            String[] paths = new String[files.length];
            for (int i = 0; i < paths.length; i++) {
                paths[i] = files[i].getAbsolutePath();
            }
            lastPath = paths[0].substring(0, paths[0].lastIndexOf(File.separator));
            VisNow.get().getMainConfig().setLastDataPath(lastPath, ReadImage.class);
            params.setSequenceMode(true);
            params.setFiles(paths);
        }
    }//GEN-LAST:event_openMultipleButtonActionPerformed
    private DecimalFormat format = new DecimalFormat("#.##");

    private void colorRBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorRBActionPerformed
        rLabel.setEnabled(false);
        gLabel.setEnabled(false);
        bLabel.setEnabled(false);
        rTF.setEnabled(false);
        gTF.setEnabled(false);
        bTF.setEnabled(false);
        preset1Button.setEnabled(false);
        preset2Button.setEnabled(false);
        preset3Button.setEnabled(false);
        params.setGrayscale(false);
    }//GEN-LAST:event_colorRBActionPerformed

    private void grayscaleRBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_grayscaleRBActionPerformed
        rLabel.setEnabled(true);
        gLabel.setEnabled(true);
        bLabel.setEnabled(true);
        rTF.setEnabled(true);
        gTF.setEnabled(true);
        bTF.setEnabled(true);
        preset1Button.setEnabled(true);
        preset2Button.setEnabled(true);
        preset3Button.setEnabled(true);
        float[] rgb = params.getRGBWeights();
        rTF.setText(format.format(rgb[0]));
        gTF.setText(format.format(rgb[1]));
        bTF.setText(format.format(rgb[2]));
        params.setGrayscale(true);
    }//GEN-LAST:event_grayscaleRBActionPerformed

    private void preset1ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_preset1ButtonActionPerformed
        float[] rgb = new float[]{0.30f, 0.59f, 0.11f};
        rTF.setText(format.format(rgb[0]));
        gTF.setText(format.format(rgb[1]));
        bTF.setText(format.format(rgb[2]));
        params.setRGBWeights(rgb);
    }//GEN-LAST:event_preset1ButtonActionPerformed

    private void rTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rTFActionPerformed
        float[] rgb = params.getRGBWeights();
        try {
            float v = Float.parseFloat(rTF.getText());
            if (v != rgb[0]) {
                rgb[0] = v;
                params.setRGBWeights(rgb);
            }
        } catch (NumberFormatException ex) {
            rTF.setText(format.format(rgb[0]));
        }
    }//GEN-LAST:event_rTFActionPerformed

    private void rTFFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_rTFFocusLost
        rTFActionPerformed(null);
    }//GEN-LAST:event_rTFFocusLost

    private void gTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gTFActionPerformed
        float[] rgb = params.getRGBWeights();
        try {
            float v = Float.parseFloat(gTF.getText());
            if (v != rgb[1]) {
                rgb[1] = v;
                params.setRGBWeights(rgb);
            }
        } catch (NumberFormatException ex) {
            gTF.setText(format.format(rgb[1]));
        }

    }//GEN-LAST:event_gTFActionPerformed

    private void gTFFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_gTFFocusLost
        gTFActionPerformed(null);
    }//GEN-LAST:event_gTFFocusLost

    private void bTFFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_bTFFocusLost
        bTFActionPerformed(null);
    }//GEN-LAST:event_bTFFocusLost

    private void bTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bTFActionPerformed
        float[] rgb = params.getRGBWeights();
        try {
            float v = Float.parseFloat(bTF.getText());
            if (v != rgb[2]) {
                rgb[2] = v;
                params.setRGBWeights(rgb);
            }
        } catch (NumberFormatException ex) {
            bTF.setText(format.format(rgb[2]));
        }
    }//GEN-LAST:event_bTFActionPerformed

    private void showCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showCBActionPerformed
        params.setShow(showCB.isSelected());
    }//GEN-LAST:event_showCBActionPerformed

    private void preset2ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_preset2ButtonActionPerformed
        float[] rgb = new float[]{1.0f / 3.0f, 1.0f / 3.0f, 1.0f / 3.0f};
        rTF.setText(format.format(rgb[0]));
        gTF.setText(format.format(rgb[1]));
        bTF.setText(format.format(rgb[2]));
        params.setRGBWeights(rgb);
    }//GEN-LAST:event_preset2ButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel bLabel;
    private javax.swing.JTextField bTF;
    private javax.swing.JButton browseButton;
    private javax.swing.ButtonGroup buttonGroup;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JRadioButton colorRB;
    private javax.swing.JRadioButton fileButton;
    private javax.swing.JLabel gLabel;
    private javax.swing.JTextField gTF;
    private javax.swing.JRadioButton grayscaleRB;
    private javax.swing.JRadioButton gridButton;
    private javax.swing.JLabel imageDescription;
    private javax.swing.JTextPane imageList;
    private javax.swing.JPanel imagePanel;
    private javax.swing.JTextField imagePath;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel messageLabel;
    private javax.swing.JPanel multipleImagesTab;
    private javax.swing.JButton openMultipleButton;
    private javax.swing.JButton preset1Button;
    private javax.swing.JButton preset2Button;
    private javax.swing.JButton preset3Button;
    private javax.swing.JLabel rLabel;
    private javax.swing.JTextField rTF;
    private javax.swing.JCheckBox showCB;
    private javax.swing.JPanel singleImageTab;
    private javax.swing.JTabbedPane singleMultiplePane;
    private javax.swing.JRadioButton urlButton;
    // End of variables declaration//GEN-END:variables

    public void setImageDescription(String s) {
        imageDescription.setText(s);
    }

    public void setInfo(String text, Color color) {
        messageLabel.setForeground(color);
        messageLabel.setText(text);
    }

    public void imagesListAddInfo(String text) {
        imageList.setText(text + "\n" + imageList.getText());
    }

    public void imagesListClear() {
        imageList.setText("");
    }

    public void setImage(Image image) {
        this.image = image;
        imagePanel.repaint();
    }

    public void activateOpenDialog() {
        browseButtonActionPerformed(null);
    }

    public static void main(String[] args) {
       VisNow.initLogging(true);       
       Locale.setDefault(VisNow.LOCALE);
       
       JFrame f = new JFrame();
       f.add(new GUI());
       f.pack();
       f.setLocation(400, 200);
       f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
       f.setVisible(true);        
    }

}
