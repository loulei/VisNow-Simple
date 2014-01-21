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

import java.io.*;
import java.util.Enumeration;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import pl.edu.icm.visnow.engine.error.Displayer;
import pl.edu.icm.visnow.engine.exception.VNOuterIOException;
import pl.edu.icm.visnow.system.main.VisNow;

/**
 *
 * @author Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class MainConfigInitializer {


    //<editor-fold defaultstate="collapsed" desc=" Init templates ">
    public static void initTemplates(File file) {
        file.mkdir();
        try {
            JarFile jar = new JarFile(VisNow.get().getJarPath());
            Enumeration<JarEntry> enumeration;
            enumeration = jar.entries();

            while(enumeration.hasMoreElements()) {
                JarEntry e = enumeration.nextElement();
                if(e.getName().toLowerCase().startsWith(MainConfig.TEMPLATES) &&
                   e.getName().length() > 10) {
                    InputStream is = jar.getInputStream(e);

                    File out = new File(file.getPath()+File.separator+e.getName().substring(9));
                    out.createNewFile();
                    OutputStream os = new FileOutputStream(out);
                    //TODO: jak to przyspieszyć?
                    for(int i = is.read(); ; i = is.read()) {
                        if(i==-1) {
                            os.close();
                            break;
                        }
                        os.write(i);
                    }

                }
            }

        } catch (IOException ex) {
            Displayer.ddisplay(200907311200L, ex, "MainConfigInitializer.static", "Could not initialize templates.");
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Init libraries ">
    public static void initPluginsActive(File file) throws VNOuterIOException {
        try {
            file.createNewFile();
        } catch (IOException ex) {
            throw new VNOuterIOException(
                    200907100500L,
                    "Could not create plugins active file.",
                    ex,
                    "MainConfigInitializer.static",
                    Thread.currentThread());
        }
    }
    
    static void initPluginFolders(File file) throws VNOuterIOException {
        try {
            file.createNewFile();
        } catch (IOException ex) {
            throw new VNOuterIOException(
                    200907100500L,
                    "Could not create plugin folders file.",
                    ex,
                    "MainConfigInitializer.static",
                    Thread.currentThread());
        }
    }

    
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Init recent folders ">
    public static void initRecentFolders(File file) throws VNOuterIOException {
        try {
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.write("10\n");
            writer.close();
        } catch (IOException ex) {
            throw new VNOuterIOException(
                    200907100501L,
                    "Could not create file for recent folders configuration.",
                    ex,
                    "MainConfigInitializer.static",
                    Thread.currentThread());
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Init recent applications ">
    public static void initRecentApplications(File file) throws VNOuterIOException {
        try {
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.write("10\n");
            writer.close();
        } catch (IOException ex) {
            throw new VNOuterIOException(
                    200907100502L,
                    "Could not create file for recent applications configuration.",
                    ex,
                    "MainConfigInitializer.static",
                    Thread.currentThread());
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Init favorite folders ">
    public static void initFavoriteFolders(File file) throws VNOuterIOException {
        try {
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.write("0.Home\n");
            writer.write(System.getProperty("user.home")+"\n");
            writer.write("1.Templates\n");
            writer.write(file.getParent()+File.separator+MainConfig.TEMPLATES+"\n");
            writer.close();
        } catch (IOException ex) {
            throw new VNOuterIOException(
                    200907100503L,
                    "Could not create file for favorite folders configuration.",
                    ex,
                    "MainConfigInitializer.static",
                    Thread.currentThread());
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Init properties ">
    public static void initProperties(File file) throws VNOuterIOException {
        Properties props = new Properties();
        props.setProperty("visnow.startupViewer3D", "true");   
        props.setProperty("visnow.startupViewer2D", "false");
        props.setProperty("visnow.startupFieldViewer3D", "false");
        props.setProperty("visnow.autoconnectViewer", "true");
        
        props.setProperty("visnow.paths.applications.default", System.getProperty("user.home"));
        props.setProperty("visnow.paths.applications.last", System.getProperty("user.home"));
        props.setProperty("visnow.paths.applications.use", "last"); //last/default/home
        
        props.setProperty("visnow.paths.data.default", System.getProperty("user.home"));
        props.setProperty("visnow.paths.data.last", System.getProperty("user.home"));
        props.setProperty("visnow.paths.data.use", "last"); //last/default/home
        
        props.setProperty("visnow.continuousColorAdjustingLimit", "64000");

        try {
            props.store(new FileOutputStream(file), null);
        } catch (IOException ex) {
            throw new VNOuterIOException(
                    200907100503L,
                    "Could not create file for properties configuration.",
                    ex,
                    "MainConfigInitializer.static",
                    Thread.currentThread());
        }
    }
    //</editor-fold>

    public static void initColorMaps(File file) throws VNOuterIOException {
        try {
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.write("<colormaps></colormaps>");
            writer.close();
        } catch (IOException ex) {
            throw new VNOuterIOException(
                    200907100503L,
                    "Could not create file for favorite folders configuration.",
                    ex,
                    "MainConfigInitializer.static",
                    Thread.currentThread());
        }
    }

   private MainConfigInitializer()
   {
   }


}
