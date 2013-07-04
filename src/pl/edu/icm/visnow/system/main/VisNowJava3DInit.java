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

package pl.edu.icm.visnow.system.main;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 * @author  Bartosz Borucki (babor@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class VisNowJava3DInit {
    private static final Logger LOGGER = Logger.getLogger(VisNowJava3DInit.class);
    
@SuppressWarnings({"unchecked"})
    public static void initJava3D(boolean debug, Logger logger) throws Java3DVersionException {

            //javax.media.j3d.VirtualUniverse vu = new javax.media.j3d.VirtualUniverse();
            Map<String, Object> vuMap = javax.media.j3d.VirtualUniverse.getProperties();
            Set<String> vuKeys = vuMap.keySet();
            Iterator<String> vuKeysI = vuKeys.iterator();

            String j3dVersion = (String) vuMap.get("j3d.version");
            j3dVersion = j3dVersion.substring(0, 5);
            int j3dVersion0 = 0;
            int j3dVersion1 = 0;
            int j3dVersion2 = 0;
            j3dVersion0 = Integer.parseInt(j3dVersion.substring(0, 1));
            j3dVersion1 = Integer.parseInt(j3dVersion.substring(2, 3));
            j3dVersion2 = Integer.parseInt(j3dVersion.substring(4, 5));

            logger.info("(v" + j3dVersion0 + "." + j3dVersion1 + "." + j3dVersion2 + ") ");
            if (j3dVersion0 < 1
                    || (j3dVersion0 == 1 && j3dVersion1 < 5)
                    || (j3dVersion0 == 1 && j3dVersion1 == 5 && j3dVersion2 < 1)) {
                
                throw new Java3DVersionException(j3dVersion);
                
            }


            logger.info("-------- Java3D startup info --------");
            logger.info(" * Virtual Universe properties:");

            if (debug) {
                String key;
                while (vuKeysI.hasNext()) {
                    key = vuKeysI.next();
                    logger.info("    " + key + " = " + vuMap.get(key));
                }
                logger.info("");
            } else {
                logger.info("    version = " + vuMap.get("j3d.version"));
                logger.info("    vendor = " + vuMap.get("j3d.vendor"));
                logger.info("    specification.version = " + vuMap.get("j3d.specification.version"));
                logger.info("    specification.vendor = " + vuMap.get("j3d.specification.vendor"));
                logger.info("    renderer = " + vuMap.get("j3d.renderer"));
            }
            logger.info("");

            javax.media.j3d.GraphicsConfigTemplate3D template = new javax.media.j3d.GraphicsConfigTemplate3D();
            template.setStereo(javax.media.j3d.GraphicsConfigTemplate3D.PREFERRED);
            template.setSceneAntialiasing(javax.media.j3d.GraphicsConfigTemplate3D.PREFERRED);

            GraphicsConfiguration gcfg = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getBestConfiguration(template);
            javax.media.j3d.Canvas3D c3d = new javax.media.j3d.Canvas3D(gcfg);
            Map c3dMap = c3d.queryProperties();
            Set<String> c3dMapKeys = c3dMap.keySet();
            Iterator<String> c3dMapKeysI = c3dMapKeys.iterator();


            logger.info(" * Canvas3D properties:");
            if (debug) {
                String key;
                while (c3dMapKeysI.hasNext()) {
                    key = c3dMapKeysI.next();
                    logger.info("    " + key + " = " + c3dMap.get(key));
                }
                logger.info("");
            } else {
                logger.info("    renderer version = " + c3dMap.get("native.version"));
                logger.info("    stereoAvailable = " + c3dMap.get("stereoAvailable"));
                logger.info("    sceneAntialiasingAvailable = " + c3dMap.get("sceneAntialiasingAvailable"));
            }
            logger.info("------------------------------------------");
            logger.info("");
    }
    
    public static class Java3DVersionException extends Exception {
        private String version = "";
        
        public Java3DVersionException(String version) {
            this.version = version;            
        }
        
        public String getVersion() {
            return version;
        }
        
        
    }

   private VisNowJava3DInit()
   {
   }
    
}
