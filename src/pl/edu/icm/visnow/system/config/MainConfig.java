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
package pl.edu.icm.visnow.system.config;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import pl.edu.icm.visnow.datamaps.ColorMapManager;
import pl.edu.icm.visnow.datamaps.colormap1d.RGBChannelColorMap1D;
import pl.edu.icm.visnow.datamaps.colormap1d.RGBChannelColorMap1D.ColorKnot;
import pl.edu.icm.visnow.engine.commands.LibraryAddCommand;
import pl.edu.icm.visnow.engine.commands.LibraryDeleteCommand;
import pl.edu.icm.visnow.engine.error.Displayer;
import pl.edu.icm.visnow.engine.exception.VNOuterIOException;
import pl.edu.icm.visnow.engine.library.LibraryRoot;
import pl.edu.icm.visnow.engine.library.jar.JarLibReader;
import pl.edu.icm.visnow.engine.library.jar.JarLibraryRoot;
import pl.edu.icm.visnow.system.FileLinesIterator;
import pl.edu.icm.visnow.system.main.VisNow;

/**
 *
 * @author Hubert Orlik-Grzesik, University of Warsaw, ICM
 *
 *
 */
public class MainConfig {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(MainConfig.class);
    
    protected static final String TEMPLATES = "templates";
    private static final String FAVORITE_FOLDERS = "favoriteFolders";
    private static final String RECENT_FOLDERS = "recentFolders";
    private static final String RECENT_APPLICATIONS = "recentApplications";
    private static final String WINDOW_XML = "window.xml";
    private static final String PROPERTIES = "visnow.properties";
    private static final String COLORMAPS_XML = "colormaps.xml";
    private static final String VISNOW_CONFIG_DIR = System.getProperty("user.home") + File.separator + ".visnow";
    private static final String PLUGINS_DIR_NAME = "plugins";
    private static final String PLUGINS_ACTIVE = "plugins";
    private static final String PLUGIN_FOLDERS = "plugin_folders";
    
    //<editor-fold defaultstate="collapsed" desc=" debug, todo ">
    /* TODO: sprawdzanie aktualnosci konfiguracji.
     * Jesli jest obecna konfiguracja, a nie jest obecna konfiguracja aktualna,
     * nalezy uruchomic jakiegos wizarda.
     */
    private boolean debug = true;
    //wyciete z konstruktora
    //TODO: wczytywanie nadrzednej konfiguracji z katalogu jara.
    // jak wlasciwie tworzyc ten katalog i jak nim zarzadzac?
//        File file = new File(new File(jarPath).getParent() + "/.visnow");
//
//        if(file.exists()) {
//            init(file);
//            return;
//        }
//
//
    //</editor-fold>
    private File configFolder;
    private ArrayList<File> pluginFolders = new ArrayList<File>();

    public File getTmpFolder() {
        return configFolder;
    }

    public File getConfigFolder() {
        return configFolder;
    }

    public ArrayList<File> getPluginFolders() {
        return pluginFolders;
    }

    public Node getWindowXML() {
        return windowXML;
    }

    //<editor-fold defaultstate="collapsed" desc=" [Constructor] - find config file ">
    //--------------------------------------------------------------------------
    public MainConfig(String jarPath) throws VNOuterIOException {
        File file = new File(VISNOW_CONFIG_DIR);
        if (!file.exists()) {
            file.mkdir();
        }
        file = new File(file.getPath() + File.separator + VisNow.CONFIG_VERSION);
        if (!file.exists()) {
            file.mkdir();
        }
        configFolder = file;

        readTemplates();
        readPluginFolders();
        readPlugins();
        readRecentApplications();
        readRecentFolders();
        readFavoriteFolders();
        readWindowXML();
        readProperties();
        readColorMaps();
    }
    //--------------------------------------------------------------------------
    //</editor-fold>
    File templatesFolder;
    private int applicationsCount;
    private Vector<String> applications;
    private int foldersCount;
    private Vector<String> folders;
    private Vector<FavoriteFolder> favoriteFolders;
    private Node windowXML;
    private String newWindowXML;
    private Properties props = new Properties();
    private ArrayList<VNPlugin> plugins = new ArrayList<VNPlugin>();

    //<editor-fold defaultstate="collapsed" desc=" Read ">
    private void readTemplates() {
        templatesFolder = new File(configFolder.getPath() + File.separator + TEMPLATES);
        if (!templatesFolder.exists()) {
            MainConfigInitializer.initTemplates(templatesFolder);
        }
    }

    private void readPluginFolders() {
        pluginFolders.clear();
        
        File homePluginDir = new File(VISNOW_CONFIG_DIR + File.separator + PLUGINS_DIR_NAME);
        if (!homePluginDir.exists()) {
            homePluginDir.mkdir();
        }
        pluginFolders.add(homePluginDir);
        
        File mainPluginDir = new File(VisNow.get().getOperatingFolder() + File.separator + PLUGINS_DIR_NAME);
        pluginFolders.add(mainPluginDir);
        
        
        File file = new File(configFolder.getPath() + File.separator + PLUGIN_FOLDERS);
        if (!file.exists()) {
            try {
                MainConfigInitializer.initPluginFolders(file);
            } catch(VNOuterIOException ex) {
                Displayer.ddisplay(201310041217L, ex, this, "Cannot create plugin folders file.");
            }
        }

        try {
            FileLinesIterator fli = new FileLinesIterator(file);
            while (fli.hasNext()) {
                String line = fli.next();
                File f = new File(line);
                if(f.exists() && f.isDirectory()) {
                    pluginFolders.add(f);
                }
                
            }
        } catch (FileNotFoundException ex) {
            Displayer.ddisplay(200907311201L, ex, this, "Cannot read plugins file.");
        }
    }

    public void rereadPlugins() {
        unloadPlugins();
        readPlugins();
    }
    
    private void readPlugins() {
        if (pluginFolders == null) {
            return;
        }
        plugins.clear();
        for (int i = 0; i < pluginFolders.size(); i++) {
            plugins.addAll(VNPlugin.pluginsFactory(pluginFolders.get(i)));
        }
                
        readPluginsActive();
        loadActivePlugins();
    }

    private void readPluginsActive() {
        File file = new File(configFolder.getPath() + File.separator + PLUGINS_ACTIVE + "_"+VisNow.getLibraryLevelAsString());
        if (!file.exists()) {
            try {
                MainConfigInitializer.initPluginsActive(file);
            } catch(VNOuterIOException ex) {
                Displayer.ddisplay(201310041217L, ex, this, "Cannot create plugins file.");
            }
        }

        try {
            FileLinesIterator fli = new FileLinesIterator(file);
            while (fli.hasNext()) {
                String line = fli.next();
                String[] entries = line.split("\t");
                if (entries == null || entries.length != 2) {
                    continue;
                }
                String path = entries[0];
                boolean active = "1".equals(entries[1]);
                if(active) {
                    for (int i = 0; i < plugins.size(); i++) {
                        VNPlugin pi = plugins.get(i);
                        if(pi.getJarPath().equals(path) && canActivatePlugin(pi)) {
                            pi.activate();
                        }
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            Displayer.ddisplay(200907311201L, ex, this, "Cannot read plugins file.");
        }
    }
    
    private boolean canActivatePlugin(VNPlugin plugin) {
        if(plugin == null || !plugins.contains(plugin))
            return false;
        
        for (int i = 0; i < plugins.size(); i++) {
            if(plugins.get(i).isActive() && plugins.get(i) != plugin && plugins.get(i).getLibraryName().equals(plugin.getLibraryName())) {
                return false;
            }
        }
        
        return true;
    }
    
    private void loadActivePlugins() {
        for (int i = 0; i < plugins.size(); i++) {
            if(plugins.get(i).isActive())            
                loadPlugin(plugins.get(i));
        }
    }

    private void unloadPlugins() {
        if(VisNow.get().getMainWindow() != null && VisNow.get().getMainWindow().getApplicationsPanel() != null && VisNow.get().getMainWindow().getApplicationsPanel().getCurrentApplication() != null) {
              Vector<LibraryRoot> libs = VisNow.get().getMainLibraries().getLibraries();
              while(libs.size() > 1) {
                  LibraryRoot lib = libs.get(libs.size()-1);
                  VisNow.get().getMainLibraries().deleteLibrary(lib);
                  VisNow.get().getMainWindow().getApplicationsPanel().getCurrentApplication().getReceiver().receive(new LibraryDeleteCommand(lib.getName()));                                
              }
          }        
    }
    
    public void reloadPlugins() {
        unloadPlugins();
        loadActivePlugins();
    }
    
    private void loadPlugin(VNPlugin plugin) {
        if (plugin == null || !plugin.isActive()) {
            return;
        }
        
        JarLibraryRoot jrl = JarLibReader.readFromPlugin(plugin);
        boolean nativeLoaded = plugin.loadNative();
        if (jrl != null && nativeLoaded) {            
            VisNow.get().getMainLibraries().addLibrary(jrl);
            if (VisNow.get().getMainWindow() != null && VisNow.get().getMainWindow().getApplicationsPanel() != null && VisNow.get().getMainWindow().getApplicationsPanel().getCurrentApplication() != null) {
                VisNow.get().getMainWindow().getApplicationsPanel().getCurrentApplication().getReceiver().receive(new LibraryAddCommand(jrl.getName()));
            }
            LOGGER.info("Plugin "+plugin.getName()+" loaded");
        } else {
            String msg = "";
            if(jrl == null)
                msg = "ERROR loading plugin:\n"+plugin.getName();
            else 
                msg = "ERROR loading native libraries for plugin:\n"+plugin.getName();
            LOGGER.error(msg);
            if(VisNow.get().getMainWindow() != null) {
                JOptionPane.showMessageDialog(VisNow.get().getMainWindow(), msg, "Error loading plugin", JOptionPane.ERROR_MESSAGE);
            }            
        }
    }

    private void readRecentFolders() throws VNOuterIOException {
        File file = new File(configFolder.getPath() + File.separator + RECENT_FOLDERS);
        if (!file.exists()) {
            MainConfigInitializer.initRecentFolders(file);
        }
        folders = new Vector<String>();
        try {
            FileLinesIterator fli = new FileLinesIterator(file);
            foldersCount = Integer.parseInt(fli.next());
            while (fli.hasNext()) {
                folders.add(fli.next());
            }
        } catch (FileNotFoundException ex) {
            Displayer.ddisplay(200907311202L, ex, this, "Cannot read recent folders.");
        }
    }

    private void readColorMaps() throws VNOuterIOException {
        File file = new File(configFolder.getPath() + File.separator + COLORMAPS_XML);
        if (!file.exists()) {
            MainConfigInitializer.initColorMaps(file);
        }
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);

            Element colormaps = doc.getDocumentElement();
            NodeList colormaps_nodes = colormaps.getElementsByTagName("colormap");
            for (int i = 0; i < colormaps_nodes.getLength(); i++) {
                Element colormap_el = (Element) colormaps_nodes.item(i);

                String name = colormap_el.getAttribute("name");
                String type = colormap_el.getAttribute("type");

                if (type.equals("rgbchannel")) {
                    NodeList colors_nodes = colormap_el.getElementsByTagName("color");
                    float[] pos = new float[colors_nodes.getLength()];
                    Color[] colors = new Color[colors_nodes.getLength()];
                    for (int j = 0; j < colors_nodes.getLength(); j++) {
                        Element color_el = (Element) colors_nodes.item(j);
                        pos[j] = Float.valueOf(color_el.getAttribute("position"));
                        colors[j] = new Color(Integer.valueOf(color_el.getAttribute("r")), Integer.valueOf(color_el.getAttribute("g")), Integer.valueOf(color_el.getAttribute("b")));
                    }
                    RGBChannelColorMap1D colormap = new RGBChannelColorMap1D(name, false, pos, colors);
                    ColorMapManager.getInstance().registerColorMap(colormap);
                }
            }
        } catch (Exception ex) {
            Displayer.ddisplay(200907311202L, ex, this, "Cannot read colormap file.");
        }
    }

    private void initWindowXML() {
        newWindowXML = ""
                + "<system>\n"
                + "  <major>\n"
                + "    <split dir=\"horizontal\" position=\"265\">\n"
                + "      <single>\n"
                + "        <box name=\"UI\"/>\n"
                + "      </single>\n"
                + "      <split dir=\"horizontal\" position=\"220\">\n"
                + "        <single>\n"
                + "          <box name=\"Library\"/>\n"
                + "        </single>\n"
                + "        <single>\n"
                + "          <box name=\"Workspace\"/>\n"
                + "        </single>\n"
                + "      </split>\n"
                + "    </split>\n"
                + "  </major>\n"
                + "</system>\n";
    }

    private void readWindowXML() {
        //TODO: to jest bez sensu! plik czytamy dwukrotnie!
        File file = new File(configFolder.getPath() + File.separator + WINDOW_XML);
        if (!file.exists()) {
            initWindowXML();
            writeWindowXML();
        }

        newWindowXML = "";
        try {
            FileLinesIterator fli = new FileLinesIterator(file);
            while (fli.hasNext()) {
                newWindowXML += (fli.next()) + "\n";
            }
        } catch (FileNotFoundException ex) {
            initWindowXML();
        }

        try {
            windowXML = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file).getDocumentElement();
        } catch (SAXException ex) {
            //Logger.getLogger(MainConfig.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            //Logger.getLogger(MainConfig.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            //Logger.getLogger(MainConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void readRecentApplications() throws VNOuterIOException {
        File file = new File(configFolder.getPath() + File.separator + RECENT_APPLICATIONS);
        if (!file.exists()) {
            MainConfigInitializer.initRecentApplications(file);
        }
        applications = new Vector<String>();
        try {
            FileLinesIterator fli = new FileLinesIterator(file);
            applicationsCount = Integer.parseInt(fli.next());
            while (fli.hasNext()) {
                applications.add(fli.next());
            }
        } catch (FileNotFoundException ex) {
            Displayer.ddisplay(200907311203L, ex, this, "Cannot read recent applications.");
        }
    }

    private void readFavoriteFolders() throws VNOuterIOException {
        File file = new File(configFolder.getPath() + File.separator + FAVORITE_FOLDERS);
        if (!file.exists()) {
            MainConfigInitializer.initFavoriteFolders(file);
        }
        favoriteFolders = new Vector<FavoriteFolder>();
        try {
            FileLinesIterator fli = new FileLinesIterator(file);
            boolean ignore = true;
            String s = null;
            int icon = 0;
            while (fli.hasNext()) {
                if (ignore) {
                    s = fli.next();
                    icon = Integer.parseInt(s.substring(0, s.indexOf(".")));
                    s = s.substring(s.indexOf(".") + 1);
                    ignore = false;
                } else {
                    favoriteFolders.add(new FavoriteFolder(s, fli.next(), icon));
                    ignore = true;
                }
            }
        } catch (Exception ex) {
            Displayer.ddisplay(200907311203L, ex, this, "Cannot read favorite applications.");
        }
    }

    private void readProperties() throws VNOuterIOException {
        File file = new File(configFolder.getPath() + File.separator + PROPERTIES);
        if (!file.exists()) {
            MainConfigInitializer.initProperties(file);
        }

        try {
            props.load(new FileInputStream(file));
        } catch (IOException ex) {
            Displayer.ddisplay(200907311203L, ex, this, "Cannot read properties file.");
        }
    }

    //</editor-fold>
//    public Vector<String> getLibraries() {
//        return libraries;
//    }
    public Vector<FavoriteFolder> getFavouriteFolders() {
        return favoriteFolders;
    }

    public Vector<String> getRecentFolders() {
        return folders;
    }

    public File getTemplateRoot() {
        return templatesFolder;
    }

    public Vector<String> getRecentApplications() {
        return applications;
    }

    public int getRecentApplicationCount() {
        return applicationsCount;
    }

    public int getRecentFoldersCount() {
        return foldersCount;
    }

    public String getVN2UserFolder() {
        return System.getProperty("user.home");
    }

    public String getDataWriterPath() {
        return getVN2UserFolder();
    }

    public String getWorkeffectPath() {
        return getVN2UserFolder();
    }

    public String getAdditionalConfigPath() {
        return getVN2UserFolder();
    }

    public String getMovieWriterPath() { // still unused
        return "/tmp";
    }

    public void setWinowXML(String xml) {
        //RELEASE-OFF
//        //TODO: to jest mocno bez sensu!
//        newWindowXML = xml;
//        writeWindowXML();
//        readWindowXML();
    }

    public void saveConfig() {
        writeRecentFolders();
        writeRecentApplications();
        writeFavoriteFolders();
        //RELEASE-OFF
        //writeWindowXML();
        writeProperties();
    }

    private void writeWindowXML() {
        File file = new File(configFolder.getPath() + File.separator + WINDOW_XML);
        FileWriter writer;
        try {
            writer = new FileWriter(file);
        } catch (IOException ex) {
            return;
        }
        try {
            writer.write(newWindowXML);
        } catch (IOException ex) {
            return;
        }
        try {
            writer.close();
        } catch (IOException ex) {
            return;
        }
    }

    private void writeRecentFolders() {
        File file = new File(configFolder.getPath() + File.separator + RECENT_FOLDERS);
        FileWriter writer;
        try {
            writer = new FileWriter(file);
        } catch (IOException ex) {
            return;
        }
        try {
            writer.write("" + foldersCount + "\n");
            int i = 0;
            for (String s : folders) {
                if (i >= foldersCount) {
                    break;
                }
                ++i;
                writer.write(s);
                writer.write("\n");
            }
        } catch (IOException ex) {
            return;
        }
        try {
            writer.close();
        } catch (IOException ex) {
            return;
        }
    }

    private void writeRecentApplications() {
        File file = new File(configFolder.getPath() + File.separator + RECENT_APPLICATIONS);
        FileWriter writer;
        try {
            writer = new FileWriter(file);
        } catch (IOException ex) {
            return;
        }
        try {
            writer.write("" + applicationsCount + "\n");
            for (String s : applications) {
                writer.write(s);
                writer.write("\n");
            }
        } catch (IOException ex) {
            return;
        }
        try {
            writer.close();
        } catch (IOException ex) {
            return;
        }
    }

    private void writeFavoriteFolders() {
        File file = new File(configFolder.getPath() + File.separator + FAVORITE_FOLDERS);
        FileWriter writer;
        try {
            writer = new FileWriter(file);
        } catch (IOException ex) {
            return;
        }
        try {
            for (FavoriteFolder ff : favoriteFolders) {
                writer.write("" + ff.getIconId() + "." + ff.getName() + "\n");
                writer.write(ff.getPath() + "\n");
            }
        } catch (IOException ex) {
            return;
        }
        try {
            writer.close();
        } catch (IOException ex) {
        }
    }
    
    public void savePluginsConfig() {
        writePluginFolders();
        writePluginsActive();
    }

    private void writePluginsActive() {
        File file = new File(configFolder.getPath() + File.separator + PLUGINS_ACTIVE + "_" + VisNow.getLibraryLevelAsString());
        FileWriter writer;
        try {
            writer = new FileWriter(file, false);
        } catch (IOException ex) {
            return;
        }
        try {
            for (int i = 0; i < plugins.size(); i++) {
                writer.write("" + plugins.get(i).getJarPath() + "\t" + (plugins.get(i).isActive() ? "1" : "0") + "\n");
            }
        } catch (IOException ex) {
            return;
        }
        try {
            writer.close();
        } catch (IOException ex) {
        }
    }

    private void writePluginFolders() {
        File file = new File(configFolder.getPath() + File.separator + PLUGIN_FOLDERS);
        FileWriter writer;
        try {
            writer = new FileWriter(file, false);
        } catch (IOException ex) {
            return;
        }
        try {
            for (int i = 2; i < pluginFolders.size(); i++) { //start from 2 because 0 and 1 are default
                writer.write("" + pluginFolders.get(i) + "\n");
            }
        } catch (IOException ex) {
            return;
        }
        try {
            writer.close();
        } catch (IOException ex) {
        }
    }
    
    private void writeProperties() {
        File file = new File(configFolder.getPath() + File.separator + PROPERTIES);
        try {
            props.store(new FileOutputStream(file), null);
        } catch (IOException ex) {
            return;
        }


    }

    public void addRecentFolder(File folder) {
        if (folders.contains(folder.getAbsolutePath())) {
            folders.remove(folder.getAbsolutePath());
            folders.add(0, folder.getAbsolutePath());
        } else {
            folders.add(0, folder.getAbsolutePath());
            while (folders.size() > foldersCount) {
                folders.remove(folders.size() - 1);
            }
        }

    }

    public void addRecentApplication(String filePath) {
        applications.add(filePath);
    }

    public void addFavoriteFolder(FavoriteFolder ff) {
        favoriteFolders.add(ff);
    }

    public boolean isStartupViewer3D() {
        return Boolean.parseBoolean(props.getProperty("visnow.startupViewer3D"));
    }

    public void setStartupViewer3D(boolean value) {
        props.setProperty("visnow.startupViewer3D", value ? "true" : "false");
    }

    public boolean isAutoconnectViewer() {
        return Boolean.parseBoolean(props.getProperty("visnow.autoconnectViewer"));
    }

    public void setAutoconnectViewer(boolean value) {
        props.setProperty("visnow.autoconnectViewer", value ? "true" : "false");
    }

    public boolean isStartupViewer2D() {
        return Boolean.parseBoolean(props.getProperty("visnow.startupViewer2D"));
    }

    public void setStartupViewer2D(boolean value) {
        props.setProperty("visnow.startupViewer2D", value ? "true" : "false");
    }

    public boolean isStartupFieldViewer3D() {
        return Boolean.parseBoolean(props.getProperty("visnow.startupFieldViewer3D"));
    }

    public void setStartupFieldViewer3D(boolean value) {
        props.setProperty("visnow.startupFieldViewer3D", value ? "true" : "false");
    }

    public boolean isStartupOrthoViewer3D() {
        return Boolean.parseBoolean(props.getProperty("visnow.startupOrthoViewer3D"));
    }

    public void setStartupOrthoViewer3D(boolean value) {
        props.setProperty("visnow.startupOrthoViewer3D", value ? "true" : "false");
    }

    public boolean isAutoconnectOrthoViewer3D() {
        return Boolean.parseBoolean(props.getProperty("visnow.autoconnectOrthoViewer3D"));
    }

    public void setAutoconnectOrthoViewer3D(boolean value) {
        props.setProperty("visnow.autoconnectOrthoViewer3D", value ? "true" : "false");
    }

    public String getUsableApplicationsPathType() {
        String str = props.getProperty("visnow.paths.applications.use");

        if ("last".equalsIgnoreCase(str)) {
            return "last";
        }

        if ("home".equalsIgnoreCase(str)) {
            return "home";
        }

        return "default";
    }

    public void setUsableApplicationsPathType(String type) {
        if ("last".equalsIgnoreCase(type)) {
            props.setProperty("visnow.paths.applications.use", "last");
        } else if ("home".equalsIgnoreCase(type)) {
            props.setProperty("visnow.paths.applications.use", "home");
        } else {
            props.setProperty("visnow.paths.applications.use", "default");
        }
    }

    public String getUsableApplicationsPath() {
        String str = props.getProperty("visnow.paths.applications.use");

        if ("last".equalsIgnoreCase(str)) {
            return getLastApplicationsPath();
        }

        if ("home".equalsIgnoreCase(str)) {
            return System.getProperty("user.home");
        }

        return getDefaultApplicationsPath();
    }

    public String getDefaultApplicationsPath() {
        String path = props.getProperty("visnow.paths.applications.default");
        if (path != null && (new File(path)).exists()) {
            return path;
        } else {
            return System.getProperty("user.home");
        }
    }

    public void setDefaultApplicationsPath(String path) {
        props.setProperty("visnow.paths.applications.default", new String(path));
    }

    public void setColorAdjustingLimit(int limit) {
        props.setProperty("visnow.continuousColorAdjustingLimit", String.format("%d", limit));
    }

    public String getLastApplicationsPath() {
        String path = props.getProperty("visnow.paths.applications.last");
        if (path != null && (new File(path)).exists()) {
            return path;
        } else {
            return getDefaultDataPath();
        }
    }

    public void setLastApplicationsPath(String path) {
        props.setProperty("visnow.paths.applications.last", new String(path));
        writeProperties();
    }

    public String getUsableDataPathType() {
        String str = props.getProperty("visnow.paths.data.use");

        if ("last".equalsIgnoreCase(str)) {
            return "last";
        }

        if ("home".equalsIgnoreCase(str)) {
            return "home";
        }

        return "default";
    }

    public String getUsableDataPath(Class moduleClass) {
        String str = props.getProperty("visnow.paths.data.use");

        if ("last".equalsIgnoreCase(str)) {
            return getLastDataPath(moduleClass);
        }

        if ("home".equalsIgnoreCase(str)) {
            return System.getProperty("user.home");
        }

        return getDefaultDataPath();
    }

    public void setUsableDataPathType(String type) {
        if ("last".equalsIgnoreCase(type)) {
            props.setProperty("visnow.paths.data.use", "last");
        } else if ("home".equalsIgnoreCase(type)) {
            props.setProperty("visnow.paths.data.use", "home");
        } else {
            props.setProperty("visnow.paths.data.use", "default");
        }
    }

    public String getDefaultDataPath() {
        String path = props.getProperty("visnow.paths.data.default");
        if (path != null && (new File(path)).exists()) {
            return path;
        } else {
            return System.getProperty("user.home");
        }
    }

    public void setDefaultDataPath(String path) {
        props.setProperty("visnow.paths.data.default", new String(path));
    }

    public String getLastDataPath(Class moduleClass) {
        String className = "";
        if (moduleClass != null) {
            className = "." + moduleClass.toString().substring(6);
        }

        String path = props.getProperty("visnow.paths.data.last" + className);
        if (path != null && (new File(path)).exists()) {
            return path;
        } else {
            path = props.getProperty("visnow.paths.data.last");
            if (path != null) {
                return path;
            }

            return getDefaultDataPath();
        }
    }

    public void setLastDataPath(String path, Class moduleClass) {
        props.setProperty("visnow.paths.data.last", path);
        if (moduleClass != null) {
            props.setProperty("visnow.paths.data.last." + moduleClass.toString().substring(6), path);
        }
        writeProperties();
    }

    public void saveColorMaps() {
        try {
            File file = new File(configFolder.getPath() + File.separator + COLORMAPS_XML);
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.write("<colormaps>\n");

            int count = ColorMapManager.getInstance().getColorMap1DCount();
            for (int i = 0; i < count; i++) {
                RGBChannelColorMap1D colormap = (RGBChannelColorMap1D) ColorMapManager.getInstance().getColorMap1D(i);
                if (!colormap.isBuildin()) {
                    writer.write(String.format("\t<colormap name='%s' type='rgbchannel'>\n", colormap.getName()));

                    for (ColorKnot ck : colormap.getColorKnots()) {
                        Color c = new Color(ck.getColor());
                        writer.write(String.format("\t\t<color position='%f' r='%d' g='%d' b='%d' />\n", ck.getPosition(), c.getRed(), c.getGreen(), c.getBlue()));
                    }
                    writer.write("\t</colormap>\n");
                }
            }

            writer.write("</colormaps>\n");
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(MainConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getProperty(String name) {
        return props.getProperty(name);
    }

    public void setProperty(String name, String value) {
        props.setProperty(name, value);
    }

    public ArrayList<VNPlugin> getPlugins() {
        return plugins;
    }

    /**
     * @return the nAvailableThreads
     */
    public int getNAvailableThreads() {        
        int n = Runtime.getRuntime().availableProcessors();
        String nTxt = getProperty("visnow.threads.limit");
        if(nTxt != null) {
            try {
                n = Integer.parseInt(nTxt);                
            } catch(NumberFormatException ex) {                
            }
        }
        return n;
    }
    

    /**
     * @param n the nAvailableThreads to set
     */
    public void setNAvailableThreads(int n) {
        if(n < 1 || n > Runtime.getRuntime().availableProcessors()) {
            return;
        }
        
        if(n == Runtime.getRuntime().availableProcessors()) {
            props.remove("visnow.threads.limit");
            return;
        }
        
        setProperty("visnow.threads.limit", ""+n);        
    }

   public int getNaNAction()
   {
      int n = 0;
      String nTxt = getProperty("visnow.numbers.NaNAction");
      if (nTxt != null)
         try
         {
            n = Integer.parseInt(nTxt);
         } catch (NumberFormatException ex)
         {
         }
      return n;
   }

   public void setNaNAction(int n)
   {
      setProperty("visnow.numbers.NaNAction", "" + n);
   }

   public int getInfAction()
   {
      int n = 0;
      String nTxt = getProperty("visnow.numbers.InfAction");
      if (nTxt != null)
         try
         {
            n = Integer.parseInt(nTxt);
         } catch (NumberFormatException ex)
         {
         }
      return n;
   }

   public void setInfAction(int n)
   {
      setProperty("visnow.numbers.InfAction", "" + n);
   }

}
