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
package pl.edu.icm.visnow.system.config;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import pl.edu.icm.visnow.engine.exception.VNOuterDataException;
import pl.edu.icm.visnow.system.main.VisNow;
import static pl.edu.icm.visnow.system.main.VisNow.getOsType;

/**
 *
 * @author Bartosz Borucki (babor@icm.edu.pl), University of Warsaw, ICM
 *
 */
public class VNPlugin {

    private static final Logger LOGGER = Logger.getLogger(VNPlugin.class);
    private String name = "plugin";
    private String libraryName = null;
    private String jarPath = null;
    private String[] libs = null;
    private String[] nativeLibs = null;
    private URLClassLoader loader = null;
    private boolean active = false;

    public VNPlugin(String name, String jarPath, String[] libs, String[] nativeLibs) {
        this.name = name;
        this.jarPath = jarPath;
        this.libs = libs;
        this.nativeLibs = nativeLibs;
        createClassLoader();
        readLibraryName();
    }

    public VNPlugin(String name, String jarPath) {
        this(name, jarPath, null, null);
    }

    private void createClassLoader() {
        if (libs == null) {
            loader = null;
            return;
        }

        ArrayList<URL> urlList = new ArrayList<URL>();
        try {
            urlList.add((new File(jarPath)).toURI().toURL());
        } catch (MalformedURLException ex) {
            return;
        }

        File f;
        for (int i = 0; i < libs.length; i++) {
            f = new File(libs[i]);
            if (f.exists()) {
                try {
                    URL url = f.toURI().toURL();
                    urlList.add(url);
                } catch (MalformedURLException ex) {
                    LOGGER.warn("WARNING: malformed url for plugin " + name + ": " + libs[i]);
                }
            }
        }
        URL[] urls = new URL[urlList.size()];
        for (int i = 0; i < urls.length; i++) {
            urls[i] = urlList.get(i);
        }
        loader = new URLClassLoader(urls);
    }

    private void readLibraryName() {
        if(jarPath == null) {
            libraryName = null;
            return;
        }
        
        try {
            File file = new File(jarPath);
            if (!file.exists()) {
                libraryName = null;
                return;
            }
            JarFile jar = new JarFile(file);
            Enumeration<JarEntry> enumeration;
            JarEntry tmpEntry;
            JarEntry libraryEntry = null;
            enumeration = jar.entries();

            int ii = 2;
            libraryLoop:
            while (enumeration.hasMoreElements()) {
                tmpEntry = enumeration.nextElement();
                switch (VisNow.getLibraryLevel()) {
                    case VisNow.FULL_LIBRARY:
                        if (tmpEntry.getName().toLowerCase().equals("extended_library.xml")) {
                            libraryEntry = tmpEntry;
                            --ii;
                            if (ii == 0) {
                                break;
                            }
                        }
                        break;
                    case VisNow.BASIC_LIBRARY:
                        if (tmpEntry.getName().toLowerCase().equals("base_library.xml")) {
                            libraryEntry = tmpEntry;
                            --ii;
                            if (ii == 0) {
                                break libraryLoop;
                            }
                        }
                        break;
                    case VisNow.SIMPLE_LIBRARY:
                        if (tmpEntry.getName().toLowerCase().equals("simple_library.xml")) {
                            libraryEntry = tmpEntry;
                            --ii;
                            if (ii == 0) {
                                break libraryLoop;
                            }
                        }
                        break;
                }
            }

            if (libraryEntry == null) {
                libraryName = null;
                return;
            }
            InputStream is = jar.getInputStream(libraryEntry);
            Node main = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is).getDocumentElement();
            if (!main.getNodeName().equalsIgnoreCase("library")) {
                throw new VNOuterDataException(200903271350L, "Main node is not a library node.", null, null, Thread.currentThread());
            }
            String tmp = file.getPath();
            if (tmp.contains("\\")) {
                tmp = file.getPath().replace('\\', '/');
                tmp = "/" + tmp;
            }

            libraryName = main.getAttributes().getNamedItem("name").getNodeValue();
        } catch (Exception ex) {
            libraryName = null;
        }

    }

    public boolean loadNative() {
        if (nativeLibs == null) {
            return true;
        }
        
            ArrayList<String> libsToRead = new ArrayList<String>();
            for (int i = 0; i < nativeLibs.length; i++) {
                libsToRead.add(nativeLibs[i]);                
            }
            
            //try readining libraries with possible dependencies until success
            //WARNING: loop dependency is not supported
            int fails = 0;
            while(!libsToRead.isEmpty()) {
                String libToRead = libsToRead.get(0);
                libsToRead.remove(libToRead);
                try {
                    System.load(libToRead);
                    fails = 0;
                } catch(UnsatisfiedLinkError err) {
                    libsToRead.add(libToRead);
                    fails++;
                }           
                if(fails > 0 && fails == libsToRead.size())
                    break;
            }
            
            if(libsToRead.isEmpty()) {
                LOGGER.info("Plugin " + name + " loaded native libraries");
                return true;
            } else {
                LOGGER.warn("Plugin " + name + " failed to load native libraries");
                return false;
            }
        

//        try {
//            for (int i = 0; i < nativeLibs.length; i++) {
//                System.load(nativeLibs[i]);
//            }
//            LOGGER.info("Plugin " + name + " loaded native libraries");
//            return true;
//        } catch (UnsatisfiedLinkError err) {
//            LOGGER.warn("Plugin " + name + " failed to load native libraries");
//            return false;
//        }
    }

    public boolean unloadNative() {
        return true;
    }
    
    
    public boolean check() {
        return testJar(jarPath);
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the jarPath
     */
    public String getJarPath() {
        return jarPath;
    }

    /**
     * @param jarPath the jarPath to set
     */
    public void setJarPath(String jarPath) {
        this.jarPath = jarPath;
    }

    /**
     * @return the libs
     */
    public String[] getLibs() {
        return libs;
    }

    /**
     * @param libs the libs to set
     */
    public void setLibs(String[] libs) {
        this.libs = libs;
        createClassLoader();
    }

    /**
     * @return the nativeLibs
     */
    public String[] getNativeLibs() {
        return nativeLibs;
    }

    /**
     * @param nativeLibs the nativeLibs to set
     */
    public void setNativeLibs(String[] nativeLibs) {
        this.nativeLibs = nativeLibs;
    }

    /**
     * @return the loader
     */
    public URLClassLoader getLoader() {
        return loader;
    }

    public static ArrayList<VNPlugin> pluginsFactory(File pluginsDir) {
        ArrayList<VNPlugin> plugins = new ArrayList<VNPlugin>();
        if(pluginsDir == null || !pluginsDir.exists())
            return plugins;
        
        String[] ls = pluginsDir.list();
        for (int i = 0; i < ls.length; i++) {
            File f = new File(pluginsDir.getAbsolutePath() + File.separator + ls[i]);
            if (f.exists() && f.isDirectory()) {
                VNPlugin plugin = pluginFactory(f);
                if(plugin != null && plugin.check())
                    plugins.add(plugin);
            }
        }
        return plugins;
    }

    public static VNPlugin pluginFactory(File pluginDir) {
        if (pluginDir == null || !pluginDir.exists()) {
            return null;
        }
        String pluginPath = pluginDir.getAbsolutePath();

        //String pluginName = pluginPath.substring(pluginPath.lastIndexOf(File.separator) + 1);
                
        String pluginJarPath = null;
        String[] pluginLibs = null;
        String[] pluginNativeLibs = null;

        boolean firstJar = false;
        String[] ls = pluginDir.list();
        for (int i = 0; i < ls.length; i++) {
            if (ls[i].equals("lib")) {
                File libDir = new File(pluginPath + File.separator + ls[i]);
                String[] libLs = libDir.list(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return (name.toLowerCase().endsWith(".jar"));
                    }
                });
                if (libLs != null) {
                    pluginLibs = new String[libLs.length];
                    for (int j = 0; j < libLs.length; j++) {
                        pluginLibs[j] = pluginPath + File.separator + "lib" + File.separator + libLs[j];
                    }
                }

                String nativeLibDirPath = pluginPath + File.separator + "lib" + File.separator + "native";
                if(new File(nativeLibDirPath).exists()) {
                    //read native libraries list
                    boolean isLinux = (getOsType() == VisNow.OsType.OS_LINUX);
                    boolean isWindows = (getOsType() == VisNow.OsType.OS_WINDOWS);
                    boolean is64 = VisNow.isCpuArch64();
                    FilenameFilter nativeLibFilter = null;
                    if (isLinux && (new File(nativeLibDirPath + File.separator + "linux")).exists()) {
                        nativeLibDirPath += File.separator + "linux";
                        nativeLibFilter = new FilenameFilter() {
                            @Override
                            public boolean accept(File dir, String name) {
                                return (name.toLowerCase().endsWith(".so"));
                            }
                        };
                        if (is64) {
                            nativeLibDirPath += File.separator + "x86_64";
                        } else {
                            nativeLibDirPath += File.separator + "x86";
                        }
                    } else if (isWindows && (new File(nativeLibDirPath + File.separator + "windows")).exists()) {
                        nativeLibDirPath += File.separator + "windows";
                        nativeLibFilter = new FilenameFilter() {
                            @Override
                            public boolean accept(File dir, String name) {
                                return (name.toLowerCase().endsWith(".dll"));
                            }
                        };
                        if (is64) {
                            nativeLibDirPath += File.separator + "win64";
                        } else {
                            nativeLibDirPath += File.separator + "win32";
                        }
                    }
                    
                    File nativeLibDir = new File(nativeLibDirPath);
                    if(nativeLibDir.exists()) {
                        String[] nativeLibLs = nativeLibDir.list(nativeLibFilter);
                        pluginNativeLibs = new String[nativeLibLs.length];
                        for (int j = 0; j < nativeLibLs.length; j++) {
                            pluginNativeLibs[j] = nativeLibDirPath + File.separator + nativeLibLs[j];                        
                        }
                    }
                }


            } else if (ls[i].endsWith(".jar") || ls[i].endsWith(".JAR") && !firstJar) {
                pluginJarPath = pluginPath + File.separator + ls[i];
                firstJar = true;
            }
        }

        if (firstJar) {
            String pluginName = pluginJarPath.substring(pluginJarPath.lastIndexOf(File.separator) + 1);
            if(pluginName.endsWith(".jar"))
                pluginName = pluginName.substring(0,pluginName.length()-4);
            return new VNPlugin(pluginName, pluginJarPath, pluginLibs, pluginNativeLibs);
        } else {
            return null;
        }
    }

    public static boolean testJar(String jarPath) {
        try {
            File file = new File(jarPath);
            if (!file.exists()) {
                return false;
            }
            JarFile jar = new JarFile(file);
            Enumeration<JarEntry> enumeration;
            JarEntry tmpEntry;
            JarEntry libraryEntry = null;
            enumeration = jar.entries();

            int ii = 2;
            libraryLoop:
            while (enumeration.hasMoreElements()) {
                tmpEntry = enumeration.nextElement();
                switch (VisNow.getLibraryLevel()) {
                    case VisNow.FULL_LIBRARY:
                        if (tmpEntry.getName().toLowerCase().equals("extended_library.xml")) {
                            libraryEntry = tmpEntry;
                            --ii;
                            if (ii == 0) {
                                break;
                            }
                        }
                        break;
                    case VisNow.BASIC_LIBRARY:
                        if (tmpEntry.getName().toLowerCase().equals("base_library.xml")) {
                            libraryEntry = tmpEntry;
                            --ii;
                            if (ii == 0) {
                                break libraryLoop;
                            }
                        }
                        break;
                    case VisNow.SIMPLE_LIBRARY:
                        if (tmpEntry.getName().toLowerCase().equals("simple_library.xml")) {
                            libraryEntry = tmpEntry;
                            --ii;
                            if (ii == 0) {
                                break libraryLoop;
                            }
                        }
                        break;
                }
            }

            return (libraryEntry != null);
        } catch (IOException ex) {
            return false;
        }
    }

    /**
     * @return the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @param active the active to set
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    public void activate() {
        setActive(true);
    }

    public void deactivate() {
        setActive(false);
    }

    /**
     * @return the libraryName
     */
    public String getLibraryName() {
        return libraryName;
    }
}
