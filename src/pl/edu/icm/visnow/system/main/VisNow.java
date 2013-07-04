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
exception statement from your version. */
//</editor-fold>

package pl.edu.icm.visnow.system.main;

import java.awt.*;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.*;
import java.util.List;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import pl.edu.icm.visnow.application.application.Application;
import pl.edu.icm.visnow.engine.error.Displayer;
import pl.edu.icm.visnow.engine.exception.VNOuterIOException;
import pl.edu.icm.visnow.engine.exception.VNSystemException;
import pl.edu.icm.visnow.engine.logging.VNLogger;
import pl.edu.icm.visnow.system.config.MainConfig;
import pl.edu.icm.visnow.system.framework.MainWindow;
import pl.edu.icm.visnow.system.framework.ScreenInfo;
import pl.edu.icm.visnow.system.libraries.AttachWizard;
import pl.edu.icm.visnow.system.libraries.MainLibraries;
import pl.edu.icm.visnow.system.libraries.MainTypes;

/**
 *
 * @author gacek
 */
public class VisNow {

    private static final Logger LOGGER = Logger.getLogger(VisNow.class);
    public static final int FULL_LIBRARY = 2;
    public static final int BASIC_LIBRARY = 1;
    public static final int SIMPLE_LIBRARY = 0;
    public static final int EXPERT_GUI = 0;
    public static final int SIMPLE_GUI = 1;
    public final static String MAIN_CLASS_NAME = "pl.edu.icm.visnow.system.main.VisNow";
    public final static String TITLE = "VisNow";
    private final static String VERSION_BASE = "1.0";
    public final static String CONFIG_DIR = ".visnow";
    public final static String CONFIG_VERSION = "0.67";
    public final static String PROJECT_NAME = "VNe4";
    public static final String LOG_OUTPUT_DIR = "log";  //subdir of CONFIG_DIR, place for *.log files (has to be equal as path defined in log4j_*.properties)
    private static final String LOG_CONFIG_TEMPLATES_DIR = "config_templates"; //directory of config templates which are copied to CONFIG_DIR
    private static final String LOG_CONFIG = "vnlog4j_default.properties"; //default configuration for logging in standard mode
    private static final String LOG_CONFIG_DEBUG = "vnlog4j_debug.properties";  //default configuration for logging in debug mode (turned on with command-line '-debug')
    public static final Locale LOCALE = Locale.ENGLISH;
    public static int screenWidth = 1366;
    public static int screenHeight = 738;
    public static int displayHeight = 600;
    public static int displayWidth = 600;
    public static java.io.File tmpDir = null;
    public static String tmpDirName = "";
    public static int guiLevel = SIMPLE_GUI;
    public static boolean allowGUISwitch = true;
    public static int libraryLevel = BASIC_LIBRARY;
    public static String VERSION = VERSION_BASE;

    public static File getTmpDir() {
        if (tmpDir == null) {
            try {
                tmpDir = java.io.File.createTempFile("visnow", "");
                tmpDir.delete();
                tmpDir.mkdir();
            } catch (Exception e) {
            }
        }
        return tmpDir;
    }

    public static String getTmpDirPath() {
        return getTmpDir().getAbsolutePath();
    }

    public void backup() {
        mainConfig.saveConfig();
        mainConfig.saveColorMaps();
        /*
         * TODO: write libraries
         */
    }
    //<editor-fold defaultstate="collapsed" desc=" PATH/DEVELOPMENT ">
    private String operatingFolder;

    public String getOperatingFolder() {
        return operatingFolder;
    }

    public boolean isDevelopment() {
        return jarPath.endsWith("build/classes/");
    }
    private String jarPath;
    // private String folderPath;

    public String getJarPath() {
        return jarPath;
    }
    // public String getFolderPath() {return folderPath;}

    public long getMemoryMax() {
        return memoryMax;
    }

    public long getMemoryAvailable() {
        Runtime r = Runtime.getRuntime();
        r.gc();
        long total = r.totalMemory();
        long free = r.freeMemory();
        long used = total - free;
        return (memoryMax - used);
    }

    private static String findOperatingFolder() {
        return findOperatingFolder(findJarPath());
    }

    private static String findOperatingFolder(String jarPath) {
        if (jarPath.endsWith("build/classes/")) //RUNNING FROM NETBEANS
            return new File(jarPath).getParentFile().getParent();
        else                              //RUNNING FROM JAR
            return new File(jarPath).getParent();
    }


    private static String findJarPath(){
        URL url;
        try{
            url = Class.forName(MAIN_CLASS_NAME).getProtectionDomain().getCodeSource().getLocation();
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException("Main class not found. How is everything working?");
        }
        String jarPath = url.toString();

        try {
            jarPath = URLDecoder.decode(jarPath, Charset.defaultCharset().name());
        } catch (UnsupportedEncodingException ex) {
            jarPath = jarPath.replaceAll("%20", " ");
        }

        if (jarPath.startsWith("file:/")) {
            jarPath = jarPath.substring(5);
        }
        if (jarPath.startsWith(":")) {
            jarPath = jarPath.substring(1);
        }

        //String fileStr;
        if (jarPath.endsWith("build/classes/")) {                                //RUNNING FROM NETBEANS
            jarPath = jarPath.substring(0, jarPath.length() - 14);
            return jarPath + "dist/" + PROJECT_NAME + ".jar";
        } else {                                                             //RUNNING FROM JAR
            return jarPath;
        }
    }


    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc=" FRAME ">
    private MainWindow mainFrame;

    public MainWindow getMainWindow() {
        return mainFrame;
    }
//</editor-fold>
    //<editor-fold defaultstate="collapsed" desc=" CONFIG/LIBRARIES/TYPES/COLORMAPS ">
    private MainLibraries mainLibraries;

    public MainLibraries getMainLibraries() {
        return mainLibraries;
    }
    private MainConfig mainConfig;

    public MainConfig getMainConfig() {
        return mainConfig;
    }
    private MainTypes mainTypes;

    public MainTypes getMainTypes() {
        return mainTypes;
    }
    private AttachWizard attachWizard;

    public AttachWizard getAttachWizard() {
        return attachWizard;
    }
    private static boolean debug = false;

    public static boolean isDebug() {
        return debug;
    }

    private void initConfig() {
        try {
            mainConfig = new MainConfig(jarPath);
        } catch (VNOuterIOException ex) {
            Displayer.display(200903281600L, ex, this, "IO error while initiating configuration.");
        }
    }

    private void initTypes() {
        mainTypes = new MainTypes();
    }

    private void initLibraries() {
        mainLibraries = new MainLibraries();
    }

    private void initWizard() {
        attachWizard = new AttachWizard();
    }
    private HelpSet helpSet = null;

    public HelpSet getHelpSet() {
        return helpSet;
    }
    private HelpBroker helpBroker = null;

    public HelpBroker getHelpBroker() {
        return helpBroker;
    }

    private void initLaf() {
        try {
            if(getOsType() == OsType.OS_MAC)
                javax.swing.UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (Exception ex) {
            LOGGER.info("Failed to set look-and-feel");
        }
    }

    private void initHelp() {
        if (jarPath == null) {
            return;
        }

        String helpJarPath = jarPath.substring(0, jarPath.lastIndexOf("/")) + "/doc/VNhelp.jar";
        File helpJarFile = new File(helpJarPath);

        String helpHS = "vnhelp_HS.hs";
        try {
            URLClassLoader loader = new URLClassLoader(new URL[]{
                        helpJarFile.toURI().toURL()
                    });
            URL hsURL = HelpSet.findHelpSet(loader, helpHS);
            if (hsURL == null) {
                throw new Exception();
            }
            helpSet = new HelpSet(VisNow.class.getClassLoader(), hsURL);
        } catch (Exception ee) {
            helpSet = null;
            helpBroker = null;
            LOGGER.warn("Help system NOT initialized - HelpSet not found");
            return;
        }
        helpBroker = helpSet.createHelpBroker();
        helpBroker.setSize(new Dimension(1000, 600));

    }

    public void showHelp(String topicID) {
        if (helpBroker == null) {
            return;
        }

        if (topicID == null) {
            topicID = "visnow.nohelp";
        }
        try {
            VisNow.get().getHelpBroker().setCurrentID(topicID);
            VisNow.get().getHelpBroker().setDisplayed(true);
        } catch (Exception ee) {
            try {
                VisNow.get().getHelpBroker().setCurrentID("visnow.nohelp");
                VisNow.get().getHelpBroker().setDisplayed(true);
            } catch (Exception eee) {
                LOGGER.error("ERROR: help system not initialized or corrupted!");
            }
        }
    }
    //</editor-fold>
    private static ArrayList<String> loadedNativeLibraries = new ArrayList<String>();

    public static boolean isNativeLibraryLoaded(String library) {
        return loadedNativeLibraries.contains(library);
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc=" Native ">
    private void loadNativeLibraries() {
        LOGGER.info("Initializing native libraries...");
        String libDir = getOperatingFolder();

        LOGGER.info("    os.name: " + System.getProperty("os.name"));
        LOGGER.info("    os.arch: " + System.getProperty("os.arch"));

        boolean isLinux = (getOsType() == OsType.OS_LINUX);
        boolean isWindows = (getOsType() == OsType.OS_WINDOWS);
        boolean is64 = VisNow.isCpuArch64();
        boolean devel = isDevelopment();
        String sep = File.separator;

        if (!isLinux && !isWindows) {
            LOGGER.warn("    native libraries not found (you should use Linux or Windows)");
            return;
        }

        if (devel) {
            libDir += sep + "dist" + sep + "lib" + sep + "native";
        } else {
            libDir += sep + "lib" + sep + "native";
        }

        if (!(new File(libDir)).exists()) {
            LOGGER.warn("    native libraries directory not found");
            return;
        }

        if (isLinux && (new File(libDir + sep + "linux")).exists()) {
            libDir += sep + "linux";
            if (is64) {
                libDir += sep + "x86_64";
            } else {
                libDir += sep + "x86";
            }
        } else if (isWindows && (new File(libDir + sep + "windows")).exists()) {
            libDir += sep + "windows";
            if (is64) {
                libDir += sep + "win64";
            } else {
                libDir += sep + "win32";
            }
        }

        if (debug) {
            LOGGER.info("Native library directory: " + libDir);
        }

        File nativeLibDir = new File(libDir);
        if (!nativeLibDir.exists()) {
            LOGGER.warn("    native libraries directory not found");
            return;
        }

        String string = "    Loading native shared library for ";
        if (isLinux) {
            string += "Linux ";
        } else if (isWindows) {
            string += "Windows ";
        }

        if (is64) {
            string += "64-bit";
        } else if (isWindows) {
            string += "32-bit";
        }
        string += ".";
        LOGGER.info(string);

        String[] nativeLibraries = nativeLibDir.list();
        for (String lib : nativeLibraries) {
            File libF = new File(libDir + sep + lib);
            if (!libF.isDirectory()) {
                LOGGER.debug(lib + " is not a directory, ommiting!");
                continue;
            }

            String[] libraryFiles = libF.list(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return (name.toLowerCase().endsWith(".dll") || name.toLowerCase().endsWith(".so"));
                }
            });

            try {
                for (int i = 0; i < libraryFiles.length; i++) {
                    System.load(libDir + sep + lib + sep + libraryFiles[i]);
                }
                LOGGER.info("Loaded " + lib + " native library.");
                loadedNativeLibraries.add(lib);
            } catch (UnsatisfiedLinkError err) {
                LOGGER.warn("Loading " + lib + " failed:" + err.getMessage());
            }
        }
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc=" **MAIN** ">
    private static VisNow visnow;

    public static VisNow get() {
        return visnow;
    }

    private void profile() {
        JLabel jl = new JLabel("VisNow");
        jl.setFont(new java.awt.Font("Tahoma", 1, 9));
        jl.getFontMetrics(jl.getFont()).charsWidth(
                jl.getText().toCharArray(),
                0,
                jl.getText().length());
    }

    private VisNow() {
        profile();
    }

    private void init(String args[]) throws VNSystemException {
        init(args, true, false);
    }

    private void init(String args[], boolean frameVisible, boolean disableStartupViewers) throws VNSystemException {
        initMemory();
        jarPath = findJarPath();
        operatingFolder = findOperatingFolder(jarPath);

        renderSplashFrame(0.3f, "Loading native libraries...");
        loadNativeLibraries();
        renderSplashFrame(0.4f, "Initializing config...");
        initConfig();
        renderSplashFrame(0.5f, "Initializing module libraries...");
        initLibraries();
        initTypes();
        renderSplashFrame(0.6f, "Initializing module wizard...");
        initWizard();
        renderSplashFrame(0.7f, "Initializing help...");
        initHelp();
        initLaf();

        screenWidth = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
        screenHeight = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;
        int mainWindowWidth = 892;
        displayWidth = screenWidth - mainWindowWidth;
        if (displayWidth < 400) {
            displayWidth = 400;
            mainWindowWidth = screenWidth - displayWidth;
        }
        if (displayWidth > 1000) {
            displayWidth = 1000;
        }
        displayHeight = 600;
        if (screenHeight >= 1024) {
            displayHeight = 800;
        }
        if (screenHeight >= 1280) {
            displayHeight = 1000;
        }

        renderSplashFrame(0.8f, "Creating main window...");
        mainFrame = new MainWindow();
        mainFrame.setBounds(displayWidth, 20, mainWindowWidth, displayHeight);

        renderSplashFrame(0.9f, "Initializing application...");
        mainFrame.getApplicationsPanel().addApplication(new Application("Untitled(" + mainFrame.getMainMenu().nextUntitled() + ")"));

        if (!disableStartupViewers && mainFrame.getApplicationsPanel().getApplication() != null) {
            if (mainConfig.isStartupViewer3D()) {
                mainFrame.getApplicationsPanel().getApplication().addInitViewerByName("viewer 3D", "pl.edu.icm.visnow.lib.basic.viewers.Viewer3D.Viewer3D");
            }
            if (mainConfig.isStartupOrthoViewer3D()) {
                mainFrame.getApplicationsPanel().getApplication().addInitViewerByName("orthoviewer 3D", "pl.edu.icm.visnow.lib.basic.viewers.MultiViewer3D.Viewer3D");
            }
            if (mainConfig.isStartupViewer2D()) {
                mainFrame.getApplicationsPanel().getApplication().addInitViewerByName("viewer 2D", "pl.edu.icm.visnow.lib.basic.viewers.Viewer2D.Viewer2D");
            }
            if (mainConfig.isStartupFieldViewer3D()) {
                mainFrame.getApplicationsPanel().getApplication().addInitViewerByName("field viewer 3D", "pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.FieldViewer3D");
            }
        }

        if (splash != null) {
            try {
                splash.close();
            } catch (IllegalStateException ex) {
            }
        }
        mainFrame.setVisible(frameVisible);
        mainFrame.toFront();
    }

    public int getMainScreenID() {
        if (visnow == null) {
            return -1;
        }
        return ScreenInfo.getScreenID(visnow.getMainWindow());
    }

    public Dimension getMainScreenDimension() {
        if (visnow == null) {
            return new Dimension(0, 0);
        }
        return ScreenInfo.getScreenDimension(ScreenInfo.getScreenID(visnow.getMainWindow()));
    }

    public static void initLogging() {
        initLogging(false);
    }

    /**
     * Initializes logging which is: - creating log_output_dir (if not exists) -
     * copy log4j*.properties into configuration directory (if already not
     * there) - reset/init log4j configuration
     */
    public static void initLogging(boolean forceDebug) {
        try {
            //create log output dir (has to be the same as one specified in log4j*.properties file)
            makeConfigDir(LOG_OUTPUT_DIR, false);
            //location of log config templates
            File logConfigTemplatesDir = new File(findOperatingFolder()+File.separator+LOG_CONFIG_TEMPLATES_DIR);
            //get config dir (place for log config templates)
            File configDir = getConfigDir();

            String logConfigFilename = isDebug() || forceDebug ? LOG_CONFIG_DEBUG : LOG_CONFIG;
            //copy log templates
            if (!new File(configDir, logConfigFilename).exists()) {
                FileUtils.copyFile(new File(logConfigTemplatesDir.toString() + File.separator + logConfigFilename),
                                   new File(configDir.toString() + File.separator + logConfigFilename));
            }

            LogManager.resetConfiguration();
            PropertyConfigurator.configure(configDir.toString() + File.separator + logConfigFilename);
        } catch (IOException e) {
            LOGGER.error("Can't load logger configuration ", e);
        }
    }
    private final static SplashScreen splash = SplashScreen.getSplashScreen();

    public static void main(final String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {

                for (int i = 0; i < args.length; i++) {
                    if (args[i].equals("-debug")) {
                        debug = true;
                    }

                    if (args[i].equals("-easy")) {
                        libraryLevel = SIMPLE_LIBRARY;
                        VERSION = VERSION_BASE + "-Simple";
                        allowGUISwitch = false;
                    }

                }
                Locale.setDefault(Locale.US);

                renderSplashFrame(0.1f, "Initializing logging...");
                initLogging();
                startupInfo();
                renderSplashFrame(0.2f, "Initializing Java3D...");
                initJava3D();

                try {
                    visnow = new VisNow();

                    int argsCount = 0;
                    for (int i = 0; i < args.length; i++) {
                        if (!args[i].startsWith("-")) {
                            argsCount++;
                        }
                    }
                    String[] args2 = new String[argsCount];
                    int c = 0;
                    for (int i = 0; i < args.length; i++) {
                        if (!args[i].startsWith("-")) {
                            args2[c++] = args[i];
                        }
                    }
                    visnow.init(args2);
                } catch (VNSystemException ex) {
                    Displayer.display(1010101010, ex, null, "Initialization failed.");
                    return;
                }

            }
        });
    }

    public static void mainBlocking(final String[] args) {
        mainBlocking(args, true);
    }

    public static void mainBlocking(final String[] args, final boolean showMainFrame) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-debug")) {
                debug = true;
            }

            if (args[i].equals("-easy")) {
                libraryLevel = SIMPLE_LIBRARY;
                VERSION = VERSION_BASE + "-Simple";
                allowGUISwitch = false;
            }

        }
        VNLogger.init(true);
        Locale.setDefault(Locale.US);

        try {
            for (int i = 0; i < args.length; i++) {
                if (args[i].equalsIgnoreCase("debug")) {
                    debug = true;
                }
            }
            visnow = new VisNow();
            visnow.init(args, showMainFrame, true);

        } catch (VNSystemException ex) {
            Displayer.display(1010101010, ex, null, "Initialization failed.");
        }
    }

    private static void startupInfo() {
        LOGGER.info("");
        LOGGER.info("");
        LOGGER.info("-------- VisNow startup info --------");

        if (debug) {
            RuntimeMXBean RuntimemxBean = ManagementFactory.getRuntimeMXBean();
            List<String> aList = RuntimemxBean.getInputArguments();
            LOGGER.info(" * JVM startup flags:");
            for (int i = 0; i < aList.size(); i++) {
                LOGGER.info("    " + aList.get(i));
            }
            LOGGER.info("");
            Set<String> p = System.getProperties().stringPropertyNames();
            Iterator<String> ip = p.iterator();
            LOGGER.info(" * System properties:");
            String key;
            while (ip.hasNext()) {
                key = ip.next();
                LOGGER.info("    " + key + " = " + System.getProperty(key));
            }
            LOGGER.info("");

            Map<String, String> env = System.getenv();
            Set<String> envKeys = env.keySet();
            Iterator<String> envKeysI = envKeys.iterator();

            LOGGER.info(" * Environment variables:");
            while (envKeysI.hasNext()) {
                key = envKeysI.next();
                LOGGER.info("    " + key + " = " + env.get(key));
            }
            LOGGER.info("  ");
            LOGGER.info("------ Java Advanced Imaging info -------");
            String[] formats = ImageIO.getReaderFormatNames();
            String readerDescription, readerVendorName, readerVersion;
            ImageReader reader;
            ImageReaderSpi spi;
            for (int i = 0; i < formats.length; i++) {
                Iterator<ImageReader> tmpReaders = ImageIO.getImageReadersByFormatName(formats[i]);
                while (tmpReaders.hasNext()) {
                    reader = tmpReaders.next();
                    spi = reader.getOriginatingProvider();
                    readerDescription = spi.getDescription(Locale.US);
                    readerVendorName = spi.getVendorName();
                    readerVersion = spi.getVersion();
                    LOGGER.info("    " + formats[i] + ": " + readerDescription + " " + readerVendorName + " " + readerVersion);
                }
            }
            LOGGER.info("-----------------------------------------");

        } else {

            LOGGER.info(" * System properties:");
            LOGGER.info("    java.runtime.name = " + System.getProperty("java.runtime.name"));
            LOGGER.info("    java.vm.version = " + System.getProperty("java.vm.version"));
            LOGGER.info("    java.vm.vendor = " + System.getProperty("java.vm.vendor"));
            LOGGER.info("    java.vm.name = " + System.getProperty("java.vm.name"));
            LOGGER.info("    java.specification.version = " + System.getProperty("java.specification.version"));
            LOGGER.info("    java.runtime.version = " + System.getProperty("java.runtime.version"));
            LOGGER.info("    os.arch = " + System.getProperty("os.arch"));
            LOGGER.info("    os.name = " + System.getProperty("os.name"));
            LOGGER.info("    os.version = " + System.getProperty("os.version"));
            LOGGER.info("    java.library.path = " + System.getProperty("java.library.path"));
            LOGGER.info("    java.ext.dirs = " + System.getProperty("java.ext.dirs"));
            LOGGER.info("    java.class.path = " + System.getProperty("java.class.path"));
            LOGGER.info("");
            LOGGER.info(" * Environment variables:");
            LOGGER.info("    JAVA_HOME = " + System.getenv("JAVA_HOME"));
            LOGGER.info("    PATH = " + System.getenv("PATH"));
            LOGGER.info("    LD_LIBRARY_PATH = " + System.getenv("LD_LIBRARY_PATH"));
            LOGGER.info("    CLASSPATH = " + System.getenv("CLASSPATH"));
            LOGGER.info("-------------------------------------");

        }
        LOGGER.info("");
        LOGGER.info("");

    }
    private long memoryMax = Long.MAX_VALUE;

    private void initMemory() {
        RuntimeMXBean RuntimemxBean = ManagementFactory.getRuntimeMXBean();
        List<String> aList = RuntimemxBean.getInputArguments();

        for (int i = 0; i < aList.size(); i++) {
            String str = aList.get(i);
            if (debug) {
                LOGGER.info("JVM input argument #" + i + ": " + str);
            }
            if (str.startsWith("-Xmx")) {
                String amount = str.substring(4, str.length() - 1);
                String unit = str.substring(str.length() - 1);
                try {
                    memoryMax = Long.parseLong(amount);
                    if (unit.equalsIgnoreCase("k")) {
                        memoryMax *= 1024L;
                    } else if (unit.equalsIgnoreCase("m")) {
                        memoryMax *= 1024L * 1024L;
                    } else if (unit.equalsIgnoreCase("g")) {
                        memoryMax *= 1024L * 1024L * 1024L;
                    }
                } catch (NumberFormatException ex) {
                    memoryMax = Long.MAX_VALUE;
                    return;
                }
                if (debug) {
                    LOGGER.info("VisNow started with maximum memory: " + memoryMax + " bytes");
                }
            }
        }

    }

    private static void initJava3D() {
        LOGGER.info("Initializing Java3D... ");
        try {
            Class cl = Class.forName("javax.media.j3d.VirtualUniverse");
            VisNowJava3DInit.initJava3D(debug, LOGGER);
            persistJava3DState(true);
        } catch (HeadlessException ex) {
            LOGGER.fatal("");
            LOGGER.fatal("ERROR: cannot initialize display!");
            if (debug) {
                ex.printStackTrace();
            }
            System.exit(1);
        } catch (VisNowJava3DInit.Java3DVersionException ex) {
            boolean oldState = readJava3DPersistedState();
            LOGGER.fatal("");
            if (oldState) {
                LOGGER.fatal("ERROR: Java3D version " + ex.getVersion() + " is not supported. Minimum version is 1.5.2! Please refer to VisNow documentation.\nNOTE: Please note that VisNow was already running properly with Java3D on this machine.\nThis might mean that some external changes have been made to your Java3D installation.\nTry reinstalling Java3D or rerun VisNow installer.");
                try {
                    JOptionPane.showMessageDialog(null, "ERROR!\n\nJava3D version " + ex.getVersion() + " is not supported.\nMinimum version is 1.5.2!\n\nPlease refer to VisNow documentation.\n\n\n\nNOTE: Please note that VisNow was already running properly with Java3D on this machine.\nThis might mean that some external changes have been made to your Java3D installation.\nTry reinstalling Java3D or rerun VisNow installer.", "Java3D version error", JOptionPane.ERROR_MESSAGE);
                } catch (HeadlessException hex) {
                }
            } else {
                LOGGER.fatal("ERROR: Java3D version " + ex.getVersion() + " is not supported. Minimum version is 1.5.2! Please refer to VisNow documentation.");
                try {
                    JOptionPane.showMessageDialog(null, "ERROR!\n\nJava3D version " + ex.getVersion() + " is not supported.\nMinimum version is 1.5.2!\n\nPlease refer to VisNow documentation.\n\n", "Java3D version error", JOptionPane.ERROR_MESSAGE);
                } catch (HeadlessException hex) {
                }
            }
            System.exit(1);
        } catch (Exception ex) {
            boolean oldState = readJava3DPersistedState();
            LOGGER.fatal("");
            if (oldState) {
                LOGGER.fatal("ERROR: cannot initialize Java3D library!\nNOTE: Please note that VisNow was already running properly with Java3D on this machine.\nThis might mean that some external changes have been made to your Java3D installation.\nTry reinstalling Java3D or rerun VisNow installer.");
                try {
                    JOptionPane.showMessageDialog(null, "ERROR!\n\nCannot initialize Java3D library.\n\nPlease refer to VisNow documentation\nor contact your system adminstrator.\n\n\n\nNOTE: Please note that VisNow was already running properly with Java3D on this machine.\nThis might mean that some external changes have been made to your Java3D installation.\nTry reinstalling Java3D or rerun VisNow installer.", "Java3D error", JOptionPane.ERROR_MESSAGE);
                } catch (HeadlessException hex) {
                }
            } else {
                LOGGER.fatal("ERROR: cannot initialize Java3D library!");
                try {
                    JOptionPane.showMessageDialog(null, "ERROR!\n\nCannot initialize Java3D library.\n\nPlease refer to VisNow documentation\nor contact your system adminstrator.\n\n", "Java3D error", JOptionPane.ERROR_MESSAGE);
                } catch (HeadlessException hex) {
                }
            }
            if (debug) {
                ex.printStackTrace();
            }
            System.exit(1);
        }
        LOGGER.info("OK");
        LOGGER.info("");
    }

    /**
     * Returns config dir (or its subdir).
     *
     * @param subdir subdir of config dir or empty/null for no subdir
     */
    public static File getConfigDir(String subdir, boolean withVersionSuffix) {
        String configDirFullPath;
        if (withVersionSuffix) {
            configDirFullPath = System.getProperty("user.home") + File.separator + CONFIG_DIR + File.separator + CONFIG_VERSION;
        } else {
            configDirFullPath = System.getProperty("user.home") + File.separator + CONFIG_DIR;
        }

        if (subdir != null && !subdir.equals("")) {
            configDirFullPath = configDirFullPath + File.separator + subdir;
        }
        File file = new File(configDirFullPath);
        return file;
    }

    /**
     * Returns config dir (with version suffix).
     */
    public static File getConfigDir() {
        return getConfigDir(null, true);
    }

    /**
     * Creates (if does not exist) and returns config dir (with version suffix).
     */
    public static File makeConfigDir(String subdir, boolean withVersionSuffix) throws IOException {
        File file = getConfigDir(subdir, withVersionSuffix);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw new IOException("Can't create config directory [" + file.toString() + "]");
            }
        }
        return file;
    }

    private static void persistJava3DState(boolean state) {
        File file = new File(new File(System.getProperty("user.home")) + File.separator + ".visnow");
        if (!file.exists()) {
            file.mkdir();
        }
        file = new File(file.getPath() + File.separator + VisNow.CONFIG_VERSION);
        if (!file.exists()) {
            file.mkdir();
        }
        file = new File(file.getPath() + File.separator + "j3dinit");

        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            out.write("" + state + "\n");
            out.close();
        } catch (IOException ex) {
        }
    }

    private static boolean readJava3DPersistedState() {
        File file = new File(new File(System.getProperty("user.home")) + File.separator + ".visnow" + File.separator + VisNow.CONFIG_VERSION + File.separator + "j3dinit");
        if (!file.exists()) {
            return false;
        }

        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            String state = in.readLine();
            if ("true".equals(state)) {
                return true;
            }
            return false;
        } catch (FileNotFoundException ex) {
            return false;
        } catch (IOException ex) {
            return false;
        }
    }

    //</editor-fold>
    public static enum OsType {

        OS_WINDOWS, OS_LINUX, OS_MAC, OS_SOLARIS, OS_AIX, OS_UNKNOWN
    }

    private static OsType osType = null;

    public static OsType getOsType() {
        if(osType == null) {
            String osName = System.getProperty("os.name");
            if (osName.startsWith("Windows") || osName.startsWith("windows")) {
                osType = OsType.OS_WINDOWS;
                return osType;
            } else if (osName.equalsIgnoreCase("Linux") || osName.equalsIgnoreCase("FreeBSD") || osName.contains("Unix")) {
                osType = OsType.OS_LINUX;
            } else if (osName.startsWith("Mac")) {
                osType = OsType.OS_MAC;
            } else if (osName.equalsIgnoreCase("Solaris")) {
                osType = OsType.OS_SOLARIS;
            } else if (osName.equalsIgnoreCase("AIX")) {
                osType = OsType.OS_AIX;
            } else {
                osType = OsType.OS_UNKNOWN;
            }
        }
        return osType;
    }

    public static enum CpuArch {

        CPU_X86, CPU_X86_64, CPU_PPC, CPU_PPC_64, CPU_SPARC, CPU_SPARCV9, CPU_OTHER
    }

    private static CpuArch cpuArch = null;

    public static boolean isCpuArch64() {
        CpuArch arch = getCpuArch();
        return (arch == CpuArch.CPU_PPC_64 || arch == CpuArch.CPU_X86_64 || arch == CpuArch.CPU_SPARCV9);
    }

    public static CpuArch getCpuArch() {
        if(cpuArch == null) {
            String arch = System.getProperty("os.arch");

            if ((arch.contains("86") && arch.contains("64")) || arch.toLowerCase().contains("amd64")) {
                cpuArch = CpuArch.CPU_X86_64;
            } else if ((arch.contains("86") && !arch.contains("64")) || arch.contains("i386")) {
                cpuArch = CpuArch.CPU_X86;
            } else if ((arch.toLowerCase().contains("ppc") || arch.toLowerCase().contains("powerpc")) && arch.contains("64")) {
                cpuArch = CpuArch.CPU_PPC_64;
            } else if ((arch.toLowerCase().contains("ppc") || arch.toLowerCase().contains("powerpc")) && !arch.contains("64")) {
                cpuArch = CpuArch.CPU_PPC;
            } else if (arch.toLowerCase().contains("sparc") && arch.contains("9")) {
                cpuArch = CpuArch.CPU_SPARCV9;
            } else if (arch.toLowerCase().contains("sparc") && !arch.contains("9")) {
                cpuArch = CpuArch.CPU_SPARC;
            } else {
               cpuArch = CpuArch.CPU_OTHER;
            }
        }
        return cpuArch;
    }



    public static String getIconPath() {
        //return "/pl/edu/icm/visnow/gui/icons/vn.png";
        return "/pl/edu/icm/visnow/gui/icons/big/visnow.png";
    }
//<editor-fold defaultstate="collapsed" desc=" **SPLASH** ">
    static final int PROGRESS_BAR_X_MARGIN = 2;
    static final int PROGRESS_BAR_Y_MARGIN = 60;
    static final int PROGRESS_BAR_HEIGHT = 10;
    static final int PROGRESS_TEXT_X_POSITION = 10;
    static final int PROGRESS_TEXT_Y_MARGIN = PROGRESS_BAR_Y_MARGIN + 5;
    static final int BOTTOM_TEXT_X_MARGIN = 10;
    static final int BOTTOM_TEXT_Y_MARGIN = 10;
    static final Font lowerLineFont = new Font("Dialog", Font.PLAIN, 10);

    private static void renderSplashFrame(float progress, String loadText) {
        renderSplashFrame(progress, loadText, TITLE + " v" + VERSION, "Interdisciplinary Centre for Mathematical and Computational Modelling, University of Warsaw");

    }

    private static void renderSplashFrame(float progress, String loadText, String bottomTextUpperLine, String bottomTextLowerLine) {
        if (splash == null) {
            return;
        }

        try {
            Graphics2D g = splash.createGraphics();
            if (g == null) {
                return;
            }

            if(!splash.isVisible())
                return;

            Rectangle bounds = splash.getBounds();
            Font f = g.getFont();
            FontMetrics fm = g.getFontMetrics(f);
            java.awt.geom.Rectangle2D rect = fm.getStringBounds(loadText, g);
            int texth = (int) Math.ceil(rect.getHeight());
            g.setComposite(AlphaComposite.Clear);
            //g.setColor(Color.RED);
            g.fillRect(PROGRESS_TEXT_X_POSITION,
                    bounds.height - PROGRESS_TEXT_Y_MARGIN - texth - 5,
                    bounds.width - PROGRESS_TEXT_X_POSITION,
                    texth + 10);

            g.setFont(lowerLineFont);
            fm = g.getFontMetrics(g.getFont());
            rect = fm.getStringBounds(bottomTextLowerLine, g);
            int lowerLineTextHeight = (int) Math.ceil(rect.getHeight());
            g.fillRect(BOTTOM_TEXT_X_MARGIN,
                    bounds.height - BOTTOM_TEXT_Y_MARGIN - lowerLineTextHeight - 5,
                    bounds.width - BOTTOM_TEXT_X_MARGIN,
                    lowerLineTextHeight + 10);

            g.setFont(f);
            fm = g.getFontMetrics(g.getFont());
            rect = fm.getStringBounds(bottomTextUpperLine, g);
            texth = (int) Math.ceil(rect.getHeight());
            g.fillRect(BOTTOM_TEXT_X_MARGIN,
                    bounds.height - lowerLineTextHeight - BOTTOM_TEXT_Y_MARGIN - texth - 5,
                    bounds.width - BOTTOM_TEXT_X_MARGIN,
                    lowerLineTextHeight + 10);



            g.setPaintMode();
    //        g.setColor(Color.BLACK);
            g.setColor(new Color(0, 75, 50));
            g.drawString(loadText, PROGRESS_TEXT_X_POSITION, bounds.height - PROGRESS_TEXT_Y_MARGIN);
            g.drawString(bottomTextUpperLine, BOTTOM_TEXT_X_MARGIN, bounds.height - lowerLineTextHeight - BOTTOM_TEXT_Y_MARGIN);
            g.setFont(lowerLineFont);
            g.drawString(bottomTextLowerLine, BOTTOM_TEXT_X_MARGIN, bounds.height - BOTTOM_TEXT_Y_MARGIN);
            g.setFont(f);

    //        g.setColor(Color.BLACK);
            g.setColor(new Color(0, 150, 100));
            g.drawRect(PROGRESS_BAR_X_MARGIN,
                    bounds.height - PROGRESS_BAR_Y_MARGIN,
                    bounds.width - PROGRESS_BAR_X_MARGIN,
                    PROGRESS_BAR_HEIGHT);
            int progressWidth = bounds.width - 2 * PROGRESS_BAR_X_MARGIN;
            int done = (int) (progressWidth * progress);
            g.fillRect(PROGRESS_BAR_X_MARGIN,
                    bounds.height - PROGRESS_BAR_Y_MARGIN,
                    PROGRESS_BAR_X_MARGIN + done,
                    PROGRESS_BAR_HEIGHT);
            if (progress >= 1.0f) {
                g.fillRect(PROGRESS_BAR_X_MARGIN,
                        bounds.height - PROGRESS_BAR_Y_MARGIN,
                        bounds.width - PROGRESS_BAR_X_MARGIN,
                        PROGRESS_BAR_HEIGHT);
            }

            splash.update();
        } catch(IllegalStateException ex) {
        }
    }
    //</editor-fold>
}
