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

package pl.edu.icm.visnow.autohelp;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import javax.swing.JFrame;
import pl.edu.icm.visnow.application.area.widgets.ModulePanel;
import pl.edu.icm.visnow.engine.core.ModuleCore;
import pl.edu.icm.visnow.engine.core.ModuleXMLReader;
import pl.edu.icm.visnow.engine.main.ModuleBox;
import pl.edu.icm.visnow.lib.utils.ImageUtilities;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;
import pl.edu.icm.visnow.system.main.VisNow;

/**
 *
 * @author Bartosz Borucki (babor@icm.edu.pl) University of Warsaw, ICM
 * 
 */
public class ModuleImageGenerator {
    private static boolean buildPro = false;

    public static void main(final String[] args) {
        if(args != null && args.length == 1) {
            buildPro = (args[0].equals("-pro"));
        }
        
        SwingInstancer.swingRunAndWait(new Runnable() {
            @Override
            public void run() {
                String libraryXml = buildPro?AutoHelpGenerator.LIBRARY_XML_PRO:AutoHelpGenerator.LIBRARY_XML_SIMPLE;
                String[] vnargs;
                if(buildPro) {
                    vnargs = new String[] {"-full"};
                } else {
                    vnargs = new String[] {"-easy"};
                }                
                VisNow.mainBlocking(vnargs, false);

                String workDir = System.getProperty("user.dir");
                String srcDir = workDir + File.separator + "src";
                System.out.println("scanning source dir: " + srcDir + File.separator + libraryXml);

                ArrayList<String> modulePackageFilters = new ArrayList<String>();
                modulePackageFilters.add("pl.edu.icm.visnow.lib.basic");
                modulePackageFilters.add("pl.edu.icm.visnow.lib.chemistry");
                ArrayList<String> modules = AutoHelpGenerator.listModules(srcDir + File.separator + libraryXml, modulePackageFilters);
                System.out.println("done.");

                for (int i = 0; i < modules.size(); i++) {
                    String module = modules.get(i);
                    BufferedImage img = createModuleImage(srcDir, module);
                    if (img == null) {
                        continue;
                    }
                    try {
                        String location = srcDir + File.separator + 
                            AutoHelpGenerator.AUTOHELP_ROOT + File.separator + 
                            AutoHelpGenerator.AUTOHELP_MODULES_DIR + File.separator +
                            module + File.separator +
                            "resources" + File.separator;
                        String imageFileName = "module_image_" + module + ".png";
                        File dir = new File(location);
                        if(!dir.exists()) {
                            dir.mkdirs();
                        }
                        File imgFile = new File(location + File.separator + imageFileName);
                        ImageUtilities.writePng(img, imgFile);
                        System.out.println("   written image " + (i + 1) + " of " + modules.size() + ": " + imageFileName);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        continue;
                    }
                }
                System.out.println("done.");
                //System.exit(0);
            }
        });
        
    }

    private static BufferedImage createModuleImage(String srcDir, String module) {

        String moduleName = null;
        String moduleClass = null;
        ModuleCore mc = null;

        try {
            String moduleXmlPath = srcDir + File.separator + module.replace(".", File.separator) + File.separator + "module.xml";

            File moduleXmlFile = new File(moduleXmlPath);
            InputStream is = new FileInputStream(moduleXmlFile);
            if (is == null) {
                System.err.println("Error reading module.xml for module: " + module);
                return null;
            }
            String[] moduleInfo = ModuleXMLReader.getModuleInfoFromStream(module, is, null);
            is.close();
            moduleName = moduleInfo[0];
            moduleClass = moduleInfo[1];
            if (moduleName == null || moduleClass == null) {
                return null;
            }

            ClassLoader loader = ClassLoader.getSystemClassLoader();
            Class coreClass = loader.loadClass(moduleClass);
            mc = (ModuleCore) coreClass.getConstructor().newInstance();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        
        if(mc == null)
            return null;
        
        ModuleBox mb = new ModuleBox(null, moduleName+" [0]", mc);
        ModulePanel p = new ModulePanel(null, mb);        
        Rectangle bnds = p.getBounds();
     
        JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.add(p,BorderLayout.CENTER);
        frame.setBounds(0, 0, bnds.width+100, bnds.height+100);
        frame.setVisible(true);
        try {
            Thread.sleep(100);
        } catch(InterruptedException ex) {}
        
        BufferedImage img = new BufferedImage((int)bnds.getWidth(), (int)bnds.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.getGraphics();
        p.paint(g);
        
        frame.setVisible(false);
        return img;
    }
}
