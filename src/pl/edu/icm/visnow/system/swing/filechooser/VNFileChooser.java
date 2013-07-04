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

package pl.edu.icm.visnow.system.swing.filechooser;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import pl.edu.icm.visnow.system.config.FavoriteFolder;
import pl.edu.icm.visnow.system.main.VisNow;

/**
 *
 * @author Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class VNFileChooser extends javax.swing.JPanel {

	public static File fileWithExtensionAddedIfNecessary( File file, FileNameExtensionFilter filter )
	{
		String name = filenameWithExtenstionAddedIfNecessary(file, filter);
		return new File(name);
	}

	public static String filenameWithExtenstionAddedIfNecessary( File file, FileNameExtensionFilter filter )
	{
		if( filter.accept(file) )
			return file.getAbsolutePath();
		else
		{
			String absolutePath = file.getAbsolutePath();
			String ext = filter.getExtensions()[0]; // TODO is first one the best?
			return absolutePath+"."+ext;
		}
	}

	public static boolean checkForOverwrite(String path)
	{
		File file = new File(path);
		if(!file.exists())
			return true;
		int answer = JOptionPane.showConfirmDialog(null, "File "+path+" exists!\nAre you sure you want to overwrite it?", "File exists",
				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null);
		if( answer == JOptionPane.YES_OPTION )
			return true;
		else
			return false;
	}

    //<editor-fold defaultstate="collapsed" desc=" [FSVAR] Icons ">
    private final static Icon NEWFOLDER =
            javax.swing.plaf.metal.MetalIconFactory.getFileChooserNewFolderIcon();
            //new ImageIcon(VNFileChooser.class.getResource("/pl/edu/icm/visnow/gui/icons/chooser/new.png"));
    private final static Icon UP =
            javax.swing.plaf.metal.MetalIconFactory.getFileChooserUpFolderIcon();
            //new ImageIcon(VNFileChooser.class.getResource("/pl/edu/icm/visnow/gui/icons/chooser/up.png"));
    private final static Icon TREE =
            javax.swing.plaf.metal.MetalIconFactory.getFileChooserListViewIcon();
            //javax.swing.plaf.metal.MetalIconFactory.getTreeControlIcon(true);
            //new ImageIcon(VNFileChooser.class.getResource("/pl/edu/icm/visnow/gui/icons/chooser/jtreex.png"));
    private final static Icon FOLDERS =
            //javax.swing.plaf.metal.MetalIconFactory.
            //javax.swing.plaf.metal.MetalIconFactory.get
            new ImageIcon(VNFileChooser.class.getResource("/pl/edu/icm/visnow/gui/icons/chooser/folders.png"));
    private final static Icon ADDFOLDER =
            new ImageIcon(VNFileChooser.class.getResource("/pl/edu/icm/visnow/gui/icons/chooser/addfolder.png"));
    private final static Icon PREFERENCES =
            new ImageIcon(VNFileChooser.class.getResource("/pl/edu/icm/visnow/gui/icons/chooser/preferences.png"));
    private final static Icon HIDDEN =
            javax.swing.plaf.metal.MetalIconFactory.getFileChooserDetailViewIcon();
            //new ImageIcon(VNFileChooser.class.getResource("/pl/edu/icm/visnow/gui/icons/chooser/hidden.png"));
    private final static Icon NOHIDDEN =
            new ImageIcon(VNFileChooser.class.getResource("/pl/edu/icm/visnow/gui/icons/chooser/nohidden.png"));
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" [FSVAR] Comparator ">
    private final static Comparator<File> comparator = new Comparator<File>() {

        public int compare(File o1, File o2) {
            if (o1.isDirectory() && !o2.isDirectory()) {
                return -1;
            }
            if (!o1.isDirectory() && o2.isDirectory()) {
                return 1;
            }
            return o1.getName().compareTo(o2.getName());
        }
    };
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" [FSVAR] FileFilter ">
    public final static FileFilter acceptAllFileFilter = new FileFilter() {

        public boolean accept(File pathname) {
            return true;
        }

        @Override
        public String getDescription() {
            return "All files ( .*)";
        }
    };

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" [VAR] Master ">
    private VNFileChooserMaster master = new VNFileChooserMaster(this);

    public boolean showDialog(String title) {
        return master.showDialog(title);
    }

    public boolean showDialog() {
        return master.showDialog();
    }
    //</editor-fold>

    private File currentFolder;

    //<editor-fold defaultstate="collapsed" desc=" [DAT] MultiSelection ">
    private boolean multiSelection = true;

    private void setMultiSelection(boolean b) {
        multiSelection = b;
        if (b) {
            fileList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        } else {
            fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }
    }

    public boolean isMultiSelection() {
        return multiSelection;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" [DAT] ActionListeners ">
    public final static int ACTION_ACCEPT = 1;
    public final static int ACTION_CANCEL = 0;
    private Vector<ActionListener> actionListeners = new Vector<ActionListener>(3);

    public void addActionListener(ActionListener listener) {
        actionListeners.add(listener);
    }

    public void removeActionListener(ActionListener listener) {
        actionListeners.remove(listener);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" [DAT] FileFilters ">
    private Vector<FileFilter> fileFilters = new Vector<FileFilter>(5);

    public void addFileFilter(FileFilter filter) {
        fileFilters.add(filter);
        updateFileFilters();
    }

    public void removeFileFilter(FileFilter filter) {
        fileFilters.remove(filter);
    }

    public void setFileFilter(FileFilter filter) {
        fileFilterComboBox.setSelectedItem(filter);
    }

    private void initFileFilters() {
        fileFilters.add(acceptAllFileFilter);
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        for (FileFilter filter : fileFilters) {
            model.addElement(filter);
        }
        fileFilterComboBox.setModel(model);
        fileFilterComboBox.setRenderer(new ListCellRenderer() {

            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if(value instanceof FileFilter) {
                    return new JLabel(((FileFilter)value).getDescription());
                } else {
                    return new JLabel("? - incorrect");
                }
            }
        });
    }

    private void updateFileFilters() {
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        for (FileFilter filter : fileFilters) {
            model.addElement(filter);
        }
        fileFilterComboBox.setModel(model);
        selectFileFilter();
    }

    private void selectFileFilter() {
        this.refreshListElements();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" [DAT] ShowHiddenFiles ">
    private boolean showHiddenFiles = false;
    private boolean showHiddenIgnore = false;

    public void setShowHiddenFiles(boolean b) {
        showHiddenIgnore = true;
        showHiddenFiles = b;
        refreshListElements();
        hiddenFilesInListToggle.setSelected(b);
        showHiddenIgnore = false;
    }

    public boolean isShowHiddenFiles() {
        return showHiddenFiles;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" [VAR] Selected files - TODO ">
    //TODO: rozróżnianie obu sytuacji
    //TODO: zwracanie przed wybraniem pliku
    private File selectedFile;
    private File[] selectedFiles;

    public File getSelectedFile() {
        return selectedFile;
    }

    public File[] getSelectedFiles() {
        return selectedFiles;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" [CONSTRUCTOR] ">
    public VNFileChooser() {
        this(false);
    }

    public VNFileChooser(boolean preview) {
        initComponents();
        initComponents2();
        initFileFilters();
        if(VisNow.get().getMainConfig().getRecentFolders().size()>0)
            setFolder(new File(VisNow.get().getMainConfig().getRecentFolders().firstElement()));
        else
            setFolder(new File(VisNow.get().getOperatingFolder()));
        if(!preview) {
            this.remove(rightKPanel);
        }
    }

    //<editor-fold defaultstate="collapsed" desc=" Init components 2 ">
    private void initComponents2() {
        fileList.setCellRenderer(new FileNameRenderer());
        favoriteList.setCellRenderer(new ListCellRenderer() {
            public Component getListCellRendererComponent(
                    JList list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus)
            {
                JLabel lab;
                if(value instanceof FavoriteFolder) {
                    lab = new JLabel( ((FavoriteFolder)value).getName() );
                    lab.setIcon(((FavoriteFolder)value).getIcon());
                } else {
                    lab = new JLabel("?");
                }
                if(isSelected) {
                    lab.setOpaque(true);
                    lab.setBackground(Color.WHITE);
                }
                return lab;
            }
        });
    }
    //</editor-fold>

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Update recent/favorite list ">
 
    private void updateRecentList() {
        DefaultListModel dlm = new DefaultListModel();
        for (String s : VisNow.get().getMainConfig().getRecentFolders()) {
            dlm.addElement(s);
        }
        recentList.setModel(dlm);

    }

    protected void updateFavoriteList() {
        DefaultListModel dlm = new DefaultListModel();
        for (FavoriteFolder ff : VisNow.get().getMainConfig().getFavouriteFolders()) {
            dlm.addElement(ff);
        }
        favoriteList.setModel(dlm);

    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Generated code ">
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();
        jPanel6 = new javax.swing.JPanel();
        mainPanel = new javax.swing.JPanel();
        mainLowPanel = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        fileNameLabel = new javax.swing.JLabel();
        fileNameField = new javax.swing.JTextField();
        fileFilterLabel = new javax.swing.JLabel();
        fileFilterComboBox = new javax.swing.JComboBox();
        fileScrollPane = new javax.swing.JScrollPane();
        fileList = new javax.swing.JList();
        mainHighPanel = new javax.swing.JPanel();
        cornerPanel = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        upButton = new javax.swing.JButton();
        newButton = new javax.swing.JButton();
        hiddenFilesInListToggle = new javax.swing.JToggleButton();
        jPanel4 = new javax.swing.JPanel();
        pathList = new pl.edu.icm.visnow.system.swing.filechooser.ButtonList();
        rightKPanel = new javax.swing.JPanel();
        previewPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        autoGeneratePreviewCheckBox = new javax.swing.JCheckBox();
        generatePreviewButton = new javax.swing.JButton();
        disablePreviewToggle = new javax.swing.JToggleButton();
        jPanel1 = new javax.swing.JPanel();
        leftKPanel = new javax.swing.JPanel();
        foldersPanel = new javax.swing.JPanel();
        boxPanelF1 = new javax.swing.JPanel();
        boxPanelF2 = new javax.swing.JPanel();
        leftSplitPane = new javax.swing.JSplitPane();
        recentScrollPane = new javax.swing.JScrollPane();
        recentList = new javax.swing.JList();
        favoriteScrollPane = new javax.swing.JScrollPane();
        favoriteList = new javax.swing.JList();
        recentLabel = new javax.swing.JLabel();
        favoriteLabel = new javax.swing.JLabel();
        boxPanelF3 = new javax.swing.JPanel();
        optionsButton = new javax.swing.JButton();
        addToFavoritesButton = new javax.swing.JButton();
        treePanel = new javax.swing.JPanel();
        treeBrowser = new pl.edu.icm.visnow.system.swing.filechooser.TreeBrowser();
        treeHiddenFoldersToggleButton = new javax.swing.JToggleButton();
        jPanel3 = new javax.swing.JPanel();
        favoriteToggle = new javax.swing.JToggleButton();
        treeToggle = new javax.swing.JToggleButton();

        jScrollPane1.setViewportView(jTree1);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setLayout(new java.awt.BorderLayout());

        mainPanel.setLayout(new java.awt.BorderLayout());

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        fileNameLabel.setText("File name:");

        fileFilterLabel.setText("File types:");

        fileFilterComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        fileFilterComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileFilterComboBoxActionPerformed(evt);
            }
        });

        fileList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        fileList.setLayoutOrientation(javax.swing.JList.VERTICAL_WRAP);
        fileList.setVisibleRowCount(-1);
        fileList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fileListMouseClicked(evt);
            }
        });
        fileList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                fileListValueChanged(evt);
            }
        });
        fileList.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                fileListKeyPressed(evt);
            }
        });
        fileScrollPane.setViewportView(fileList);

        javax.swing.GroupLayout mainLowPanelLayout = new javax.swing.GroupLayout(mainLowPanel);
        mainLowPanel.setLayout(mainLowPanelLayout);
        mainLowPanelLayout.setHorizontalGroup(
            mainLowPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainLowPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainLowPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fileScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 464, Short.MAX_VALUE)
                    .addGroup(mainLowPanelLayout.createSequentialGroup()
                        .addGroup(mainLowPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(fileFilterLabel)
                            .addComponent(fileNameLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(mainLowPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainLowPanelLayout.createSequentialGroup()
                                .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cancelButton))
                            .addComponent(fileNameField, javax.swing.GroupLayout.DEFAULT_SIZE, 389, Short.MAX_VALUE)
                            .addComponent(fileFilterComboBox, 0, 389, Short.MAX_VALUE))))
                .addContainerGap())
        );
        mainLowPanelLayout.setVerticalGroup(
            mainLowPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainLowPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(fileScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainLowPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fileNameLabel)
                    .addComponent(fileNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainLowPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainLowPanelLayout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addGroup(mainLowPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cancelButton)
                            .addComponent(okButton)))
                    .addGroup(mainLowPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(fileFilterLabel)
                        .addComponent(fileFilterComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        mainPanel.add(mainLowPanel, java.awt.BorderLayout.CENTER);

        mainHighPanel.setMaximumSize(new java.awt.Dimension(32767, 100));
        mainHighPanel.setMinimumSize(new java.awt.Dimension(0, 100));
        mainHighPanel.setPreferredSize(new java.awt.Dimension(466, 100));

        cornerPanel.setLayout(new java.awt.BorderLayout());

        jPanel5.setLayout(new java.awt.GridLayout(1, 0, 10, 0));

        upButton.setIcon(UP);
        upButton.setToolTipText("Up one level");
        upButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        upButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                upButtonActionPerformed(evt);
            }
        });
        jPanel5.add(upButton);

        newButton.setIcon(NEWFOLDER);
        newButton.setToolTipText("New folder");
        newButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        newButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newButtonActionPerformed(evt);
            }
        });
        jPanel5.add(newButton);

        hiddenFilesInListToggle.setIcon(HIDDEN);
        hiddenFilesInListToggle.setToolTipText("Show hidden files");
        hiddenFilesInListToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hiddenFilesInListToggleActionPerformed(evt);
            }
        });
        jPanel5.add(hiddenFilesInListToggle);

        cornerPanel.add(jPanel5, java.awt.BorderLayout.WEST);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 342, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 36, Short.MAX_VALUE)
        );

        cornerPanel.add(jPanel4, java.awt.BorderLayout.CENTER);

        pathList.setBorder(null);

        javax.swing.GroupLayout mainHighPanelLayout = new javax.swing.GroupLayout(mainHighPanel);
        mainHighPanel.setLayout(mainHighPanelLayout);
        mainHighPanelLayout.setHorizontalGroup(
            mainHighPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainHighPanelLayout.createSequentialGroup()
                .addGroup(mainHighPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainHighPanelLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(pathList, javax.swing.GroupLayout.DEFAULT_SIZE, 464, Short.MAX_VALUE))
                    .addGroup(mainHighPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(cornerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 464, Short.MAX_VALUE)))
                .addContainerGap())
        );
        mainHighPanelLayout.setVerticalGroup(
            mainHighPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainHighPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cornerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pathList, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        mainPanel.add(mainHighPanel, java.awt.BorderLayout.NORTH);

        add(mainPanel, java.awt.BorderLayout.CENTER);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 160, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 356, Short.MAX_VALUE)
        );

        autoGeneratePreviewCheckBox.setText("auto");

        generatePreviewButton.setText("Generate");
        generatePreviewButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generatePreviewButtonActionPerformed(evt);
            }
        });

        disablePreviewToggle.setFont(new java.awt.Font("Dialog", 0, 12));
        disablePreviewToggle.setText("disable");

        javax.swing.GroupLayout previewPanelLayout = new javax.swing.GroupLayout(previewPanel);
        previewPanel.setLayout(previewPanelLayout);
        previewPanelLayout.setHorizontalGroup(
            previewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(previewPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(previewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, previewPanelLayout.createSequentialGroup()
                        .addComponent(autoGeneratePreviewCheckBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(generatePreviewButton, javax.swing.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE))
                    .addComponent(disablePreviewToggle, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        previewPanelLayout.setVerticalGroup(
            previewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, previewPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(disablePreviewToggle)
                .addGap(12, 12, 12)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(previewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(autoGeneratePreviewCheckBox)
                    .addComponent(generatePreviewButton))
                .addContainerGap())
        );

        javax.swing.GroupLayout rightKPanelLayout = new javax.swing.GroupLayout(rightKPanel);
        rightKPanel.setLayout(rightKPanelLayout);
        rightKPanelLayout.setHorizontalGroup(
            rightKPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(previewPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        rightKPanelLayout.setVerticalGroup(
            rightKPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(previewPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        add(rightKPanel, java.awt.BorderLayout.EAST);

        jPanel1.setLayout(new java.awt.BorderLayout());

        leftKPanel.setLayout(new java.awt.CardLayout());

        foldersPanel.setBackground(new java.awt.Color(204, 204, 204));

        boxPanelF1.setOpaque(false);
        boxPanelF1.setLayout(new java.awt.BorderLayout());

        boxPanelF2.setOpaque(false);
        boxPanelF2.setPreferredSize(new java.awt.Dimension(150, 397));
        boxPanelF2.setLayout(new java.awt.BorderLayout());

        leftSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        leftSplitPane.setOneTouchExpandable(true);

        recentList.setBackground(new java.awt.Color(242, 242, 242));
        recentList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        recentList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                recentListValueChanged(evt);
            }
        });
        recentScrollPane.setViewportView(recentList);

        leftSplitPane.setTopComponent(recentScrollPane);

        favoriteList.setBackground(new java.awt.Color(242, 242, 242));
        favoriteList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        favoriteList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                favoriteListValueChanged(evt);
            }
        });
        favoriteScrollPane.setViewportView(favoriteList);

        leftSplitPane.setRightComponent(favoriteScrollPane);

        boxPanelF2.add(leftSplitPane, java.awt.BorderLayout.CENTER);

        recentLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        recentLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        recentLabel.setText("Recent folders");
        boxPanelF2.add(recentLabel, java.awt.BorderLayout.NORTH);

        favoriteLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        favoriteLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        favoriteLabel.setText("Favorite folders");
        boxPanelF2.add(favoriteLabel, java.awt.BorderLayout.SOUTH);

        boxPanelF1.add(boxPanelF2, java.awt.BorderLayout.CENTER);

        boxPanelF3.setOpaque(false);

        optionsButton.setIcon(PREFERENCES);
        optionsButton.setToolTipText("Options...");
        optionsButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        optionsButton.setMaximumSize(new java.awt.Dimension(32, 32));
        optionsButton.setMinimumSize(new java.awt.Dimension(32, 32));
        optionsButton.setPreferredSize(new java.awt.Dimension(32, 32));
        optionsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optionsButtonActionPerformed(evt);
            }
        });

        addToFavoritesButton.setIcon(ADDFOLDER);
        addToFavoritesButton.setToolTipText("Add this folder...");
        addToFavoritesButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        addToFavoritesButton.setMaximumSize(new java.awt.Dimension(32, 32));
        addToFavoritesButton.setMinimumSize(new java.awt.Dimension(32, 32));
        addToFavoritesButton.setPreferredSize(new java.awt.Dimension(32, 32));
        addToFavoritesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addToFavoritesButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout boxPanelF3Layout = new javax.swing.GroupLayout(boxPanelF3);
        boxPanelF3.setLayout(boxPanelF3Layout);
        boxPanelF3Layout.setHorizontalGroup(
            boxPanelF3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(boxPanelF3Layout.createSequentialGroup()
                .addComponent(optionsButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addToFavoritesButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(89, Short.MAX_VALUE))
        );
        boxPanelF3Layout.setVerticalGroup(
            boxPanelF3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(boxPanelF3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(optionsButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(addToFavoritesButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout foldersPanelLayout = new javax.swing.GroupLayout(foldersPanel);
        foldersPanel.setLayout(foldersPanelLayout);
        foldersPanelLayout.setHorizontalGroup(
            foldersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(foldersPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(foldersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(boxPanelF1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)
                    .addComponent(boxPanelF3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        foldersPanelLayout.setVerticalGroup(
            foldersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, foldersPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(boxPanelF1, javax.swing.GroupLayout.DEFAULT_SIZE, 389, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(boxPanelF3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        leftKPanel.add(foldersPanel, "folders");

        treePanel.setBackground(new java.awt.Color(204, 204, 204));

        treeBrowser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                treeBrowserActionPerformed(evt);
            }
        });

        treeHiddenFoldersToggleButton.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        treeHiddenFoldersToggleButton.setSelected(true);
        treeHiddenFoldersToggleButton.setText("show hidden folders");
        treeHiddenFoldersToggleButton.setMargin(new java.awt.Insets(1, 1, 1, 1));
        treeHiddenFoldersToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                treeHiddenFoldersToggleButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout treePanelLayout = new javax.swing.GroupLayout(treePanel);
        treePanel.setLayout(treePanelLayout);
        treePanelLayout.setHorizontalGroup(
            treePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(treePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(treePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(treeBrowser, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)
                    .addComponent(treeHiddenFoldersToggleButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE))
                .addContainerGap())
        );
        treePanelLayout.setVerticalGroup(
            treePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, treePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(treeBrowser, javax.swing.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(treeHiddenFoldersToggleButton)
                .addContainerGap())
        );

        leftKPanel.add(treePanel, "tree");

        jPanel1.add(leftKPanel, java.awt.BorderLayout.WEST);

        jPanel3.setBackground(new java.awt.Color(153, 153, 153));
        jPanel3.setLayout(new java.awt.GridLayout());

        favoriteToggle.setIcon(FOLDERS);
        favoriteToggle.setSelected(true);
        favoriteToggle.setToolTipText("Recent and Favorites");
        favoriteToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                favoriteToggleActionPerformed(evt);
            }
        });
        jPanel3.add(favoriteToggle);

        treeToggle.setIcon(TREE);
        treeToggle.setToolTipText("Folder tree");
        treeToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                treeToggleActionPerformed(evt);
            }
        });
        jPanel3.add(treeToggle);

        jPanel1.add(jPanel3, java.awt.BorderLayout.NORTH);

        add(jPanel1, java.awt.BorderLayout.WEST);
    }// </editor-fold>//GEN-END:initComponents

    //</editor-fold>


    //<editor-fold defaultstate="collapsed" desc=" [A] Ok/cancel button ">
    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        accept();
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        cancel();
    }//GEN-LAST:event_cancelButtonActionPerformed
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Accept / cancel ">

    private void accept() {
        windowClosingIgnore = true;
        VisNow.get().getMainConfig().addRecentFolder(currentFolder);
        selectedFile =
                new File(currentFolder.getPath()+"/"+fileNameField.getText());

                //(File) fileList.getSelectedValue();
        //selectedFiles = new File[fileList.getSelectedValues().length];

        //for(int i=0; i<selectedFiles.length; ++i) {
        //    selectedFiles[i] = (File)fileList.getSelectedValues()[i];
        //}
        master.dialogClosed(true);
        for (ActionListener al : actionListeners) {
            al.actionPerformed(new ActionEvent(this, ACTION_ACCEPT, "Accept"));
        }
    }

    private boolean windowClosingIgnore = false;

    protected void windowCancel() {
        if(windowClosingIgnore) return;
        selectedFile = null;
        selectedFiles = null;
        for (ActionListener al : actionListeners) {
            al.actionPerformed(new ActionEvent(this, ACTION_CANCEL, "Cancel"));
        }
    }

    private void cancel() {
        windowClosingIgnore = true;
        selectedFile = null;
        selectedFiles = null;
        master.dialogClosed(false);
        for (ActionListener al : actionListeners) {
            al.actionPerformed(new ActionEvent(this, ACTION_CANCEL, "Cancel"));
        }

    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" [A] Folder management buttons ">
    private void upButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_upButtonActionPerformed
        if (currentFolder.getParentFile() != null) {
            setFolder(currentFolder.getParentFile());
        }
    }//GEN-LAST:event_upButtonActionPerformed

    private void newButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newButtonActionPerformed
        new File(currentFolder.getAbsolutePath() + "/nowyfolder").mkdir();
        refreshListElements();
    }//GEN-LAST:event_newButtonActionPerformed
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" [A] File list ">
    private void fileListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fileListMouseClicked
        if (evt.getClickCount() < 2) {
            return;
        }
        Object[] tab = fileList.getSelectedValues();
        if (tab.length == 1) {
            if (((File) tab[0]).isDirectory()) {
                setFolder((File) tab[0]);
            } else {
                accept();
            }
        }
    }//GEN-LAST:event_fileListMouseClicked

    private void fileListKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fileListKeyPressed
        if (evt.getKeyChar() != '\n') {
            return;
        }
        Object[] tab = fileList.getSelectedValues();
        if (tab.length == 1) {
            if (((File) tab[0]).isDirectory()) {
                setFolder((File) tab[0]);
            } else {
                accept();
            }
        }
    }//GEN-LAST:event_fileListKeyPressed
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" [A] Recent/favorites list ">
    private void recentListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_recentListValueChanged
        Object[] tab = recentList.getSelectedValues();
        if (tab.length == 1) {
            File file = new File((String) tab[0]);
            if (file.isDirectory()) {
                setFolder(file);
            }
        }


    }//GEN-LAST:event_recentListValueChanged

    private void favoriteListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_favoriteListValueChanged
        Object[] tab = favoriteList.getSelectedValues();
        if (tab.length == 1) {
            File file = new File( ( (FavoriteFolder) tab[0]).getPath() );
            if (file.isDirectory()) {
                setFolder(file);
            }
        }
    }//GEN-LAST:event_favoriteListValueChanged
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" [A] Options button ">
    private void optionsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optionsButtonActionPerformed
        VisNow.get().getMainWindow().showPreferences();
    }//GEN-LAST:event_optionsButtonActionPerformed
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" [A] Preview ">
    private void generatePreviewButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generatePreviewButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_generatePreviewButtonActionPerformed
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc=" [A] Left box toggles ">
    private boolean leftBorderIgnore = false;

    private void favoriteToggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_favoriteToggleActionPerformed
        if (leftBorderIgnore) {
            return;
        }
        leftBorderIgnore = true;
        treeToggle.setSelected(false);
        ((CardLayout) leftKPanel.getLayout()).show(leftKPanel, "folders");
//        if(treeToggle.isSelected()) {
//            this.remove(treePanel);
//            treeToggle.setSelected(false);
//        }
//        if(favoriteToggle.isSelected()) {
//            this.add(foldersPanel, BorderLayout.WEST);
//        } else {
//            this.remove(foldersPanel);
//        }
        leftBorderIgnore = false;
        repack();
    }//GEN-LAST:event_favoriteToggleActionPerformed

    private void treeToggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_treeToggleActionPerformed
        if (leftBorderIgnore) {
            return;
        }
        leftBorderIgnore = true;
        favoriteToggle.setSelected(false);
        ((CardLayout) leftKPanel.getLayout()).show(leftKPanel, "tree");
//        if(favoriteToggle.isSelected()) {
//            this.remove(foldersPanel);
//            favoriteToggle.setSelected(false);
//        }
//        if(treeToggle.isSelected()) {
//            this.add(treePanel, BorderLayout.WEST);
//        } else {
//            this.remove(treePanel);
//        }
        leftBorderIgnore = false;
        repack();
    }//GEN-LAST:event_treeToggleActionPerformed
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" [A] Tree browser ">
    private boolean treeIgnore = false;

    private void treeBrowserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_treeBrowserActionPerformed
        if (treeIgnore) {
            return;
        }
        treeIgnore = true;
        if (evt.getSource() instanceof File) {
            this.setFolder((File) evt.getSource());
        }
        treeIgnore = false;
    }//GEN-LAST:event_treeBrowserActionPerformed
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" [A] File filter combo box ">
    private void fileFilterComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileFilterComboBoxActionPerformed
        selectFileFilter();
    }//GEN-LAST:event_fileFilterComboBoxActionPerformed
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" [A] Hidden button ">
    private void hiddenFilesInListToggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hiddenFilesInListToggleActionPerformed
        if(showHiddenIgnore) return;
        setShowHiddenFiles(hiddenFilesInListToggle.isSelected());
    }//GEN-LAST:event_hiddenFilesInListToggleActionPerformed

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc=" [A] Add to favorites ">
    private void addToFavoritesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addToFavoritesButtonActionPerformed
        new AddFavoriteFolderPanel(this, currentFolder).showDialog();
    }//GEN-LAST:event_addToFavoritesButtonActionPerformed

    private void treeHiddenFoldersToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_treeHiddenFoldersToggleButtonActionPerformed
        treeBrowser.setHiddenFoldersVisible(treeHiddenFoldersToggleButton.isSelected());
    }//GEN-LAST:event_treeHiddenFoldersToggleButtonActionPerformed

    private void fileListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_fileListValueChanged
        if(fileList.getSelectedValue()==null) return;
        fileNameField.setText(
                ((File)fileList.getSelectedValue()).getName()
                );
    }//GEN-LAST:event_fileListValueChanged
    //</editor-fold>

    //TODO: useless?
    private void repack() {
        this.doLayout();
        //mainLowPanel.doLayout();
        //mainHighPanel.doLayout();
        //treePanel.doLayout();
        //foldersPanel.doLayout();

        repaint();
    }
    
    //<editor-fold defaultstate="collapsed" desc=" Generated variables ">
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addToFavoritesButton;
    private javax.swing.JCheckBox autoGeneratePreviewCheckBox;
    private javax.swing.JPanel boxPanelF1;
    private javax.swing.JPanel boxPanelF2;
    private javax.swing.JPanel boxPanelF3;
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel cornerPanel;
    private javax.swing.JToggleButton disablePreviewToggle;
    private javax.swing.JLabel favoriteLabel;
    private javax.swing.JList favoriteList;
    private javax.swing.JScrollPane favoriteScrollPane;
    private javax.swing.JToggleButton favoriteToggle;
    private javax.swing.JComboBox fileFilterComboBox;
    private javax.swing.JLabel fileFilterLabel;
    private javax.swing.JList fileList;
    private javax.swing.JTextField fileNameField;
    private javax.swing.JLabel fileNameLabel;
    private javax.swing.JScrollPane fileScrollPane;
    private javax.swing.JPanel foldersPanel;
    private javax.swing.JButton generatePreviewButton;
    private javax.swing.JToggleButton hiddenFilesInListToggle;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTree jTree1;
    private javax.swing.JPanel leftKPanel;
    private javax.swing.JSplitPane leftSplitPane;
    private javax.swing.JPanel mainHighPanel;
    private javax.swing.JPanel mainLowPanel;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JButton newButton;
    private javax.swing.JButton okButton;
    private javax.swing.JButton optionsButton;
    private pl.edu.icm.visnow.system.swing.filechooser.ButtonList pathList;
    private javax.swing.JPanel previewPanel;
    private javax.swing.JLabel recentLabel;
    private javax.swing.JList recentList;
    private javax.swing.JScrollPane recentScrollPane;
    private javax.swing.JPanel rightKPanel;
    private pl.edu.icm.visnow.system.swing.filechooser.TreeBrowser treeBrowser;
    private javax.swing.JToggleButton treeHiddenFoldersToggleButton;
    private javax.swing.JPanel treePanel;
    private javax.swing.JToggleButton treeToggle;
    private javax.swing.JButton upButton;
    // End of variables declaration//GEN-END:variables
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" set folder ">
    /**
     * Sets the current folder for the chooser panel.
     * If folder is null, resets to the file system root.
     * @param folder
     */
    public void setFolder(File folder) {
        //System.out.println("Set folder");
        if (!folder.isDirectory()) {
            return;
        }
        File ffolder = folder;
        if(folder == null) ffolder = File.listRoots()[0];
        currentFolder = ffolder;
        refreshListElements();
        refreshPathButtons();
        treeBrowser.setFolder(ffolder);
        repaint();
    }

    protected void setFolderFromPath(File folder) {
        //while(pathSmallPanel.getComponentCount() > pos)
        //    pathSmallPanel.remove(pos);
        currentFolder = folder;
        refreshListElements();
        refreshPathButtons();
        //pathSmallPanel.repaint();
    }

    private void refreshListElements() {
        File[] tab = currentFolder.listFiles();
        Arrays.sort(tab, comparator);
        DefaultListModel dlm = new DefaultListModel();
        FileFilter current = (FileFilter)fileFilterComboBox.getSelectedItem();
        if(showHiddenFiles) {
            for (File file : tab) {
                if(current.accept(file))
                    dlm.addElement(file);
            }
        } else {
            for (File file : tab) {
                if((!file.isHidden()) && current.accept(file))
                    dlm.addElement(file);
            }
        }
        fileList.setModel(dlm);
    }

    private void refreshPathButtons() {
        //System.out.println("refreshPathButtons");
        pathList.startAdding();
        addNewPathButton(currentFolder);
        pathList.stopAdding();

        //pathSmallPanel.removeAll();
        //addPathButton(currentFolder);
        //pathSmallPanel.doLayout();
        //pathBigPanel.doLayout();
    }

    private void addNewPathButton(File file) {
        if (file == null) {
            return;
        }
        addNewPathButton(file.getParentFile());

        JButton butt = new JButton(file.getName() + "/");
        butt.setBackground(new java.awt.Color(255, 255, 255));
        butt.setMargin(new java.awt.Insets(0, 0, 0, 0));
        butt.addActionListener(new FolderButtonActionListener(this, file));

        pathList.addButton(butt);
    }

    private int addPathButton(File file) {
//        System.out.println("Add button: "+((file==null)?null:file.getPath()));
//        if(file == null) return 0;
//        int ret = addPathButton(file.getParentFile());
//
//        GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
//        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
//
//        JButton butt = new JButton(file.getName()+"/");
//        butt.setBackground(new java.awt.Color(255, 255, 255));
//        butt.setMargin(new java.awt.Insets(0, 0, 0, 0));
//        butt.addActionListener(new FolderButtonActionListener(this, file, ret+1));
//        pathSmallPanel.add(butt, gridBagConstraints);
//        return ret+1;
        return 0;
    }

    void showing() {
        fileList.setSelectedIndices(new int[]{});
        //TODO: clear selection
        updateFavoriteList();
        updateRecentList();
    }


    //</editor-fold>

}



