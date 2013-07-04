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

package pl.edu.icm.visnow.engine.library.jar;

import java.util.HashMap;
import java.util.Vector;
import pl.edu.icm.visnow.engine.core.InputEgg;
import pl.edu.icm.visnow.engine.core.ModuleCore;
import pl.edu.icm.visnow.engine.exception.VNException;
import pl.edu.icm.visnow.engine.library.LibraryCore;
import pl.edu.icm.visnow.engine.library.LibraryFolder;
import pl.edu.icm.visnow.engine.library.LibraryRoot;
import pl.edu.icm.visnow.engine.library.TypesMap;
import pl.edu.icm.visnow.lib.types.VNDataAcceptor;

/**
 *
 * @author Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class JarLibraryRoot implements LibraryRoot {

    protected String filePath;
    protected String name;
    protected LibraryFolder rootFolder;

    protected ClassLoader loader;

    protected TypesMap typesMap;
    @Override
    public TypesMap getTypesMap() {return typesMap;}


    @Override
    public int getType() {return JAR;}
    @Override
    public String getFilePath() {return filePath;}
    @Override
    public String getName() {return name;}
    @Override
    public LibraryFolder getRootFolder() {return rootFolder;}

    public ClassLoader getLoader() {return loader;}
    protected void setRootFolder(LibraryFolder root) {this.rootFolder = root;}
    protected void setTypesMap(TypesMap typesMap) {this.typesMap = typesMap;}

    public JarLibraryRoot(String name, String filePath, ClassLoader loader) {
        this.name = name;
        this.loader = loader;
        this.typesMap = null;
        this.filePath = filePath;
    }

    @Override
    public ModuleCore loadCore(String classPath) throws VNException {
        try {
            Class coreClass = loader.loadClass(classPath);
            @SuppressWarnings("unchecked")
            ModuleCore ret = (ModuleCore)
                coreClass
                .getConstructor()
                .newInstance();

                //.getMethod("getInstance",(java.lang.Class[])null)
                //.invoke(null,(java.lang.Object[])null);
            /* TODO: set core name */
            //ret.setLibraryInfo(this, null, null);
            ret.setLibraryInfo(this.getName(), classPath);//, core.getName());
            return ret;
        } catch (Exception ex) {
            throw new VNException(
                    123456789L,
                    "Cannot load module core.",
                    ex,
                    this,
                    Thread.currentThread()
                    );
        }
    }

    @Override
    public HashMap<String, String> getInputTypes(String className) {
        HashMap<String, String> ret = new HashMap<String, String>();
        InputEgg[] e = ModuleCore.getInputEggs(className);
        if(e == null)
            return ret;
        for(InputEgg egg: e) {
            ret.put(egg.getName(), egg.getType().getName());
        }
        return ret;
    }

    @Override
    public HashMap<String, VNDataAcceptor[]> getInputVNDataAcceptors(String className) {
        HashMap<String, VNDataAcceptor[]> ret = new HashMap<String, VNDataAcceptor[]>();
        InputEgg[] e = ModuleCore.getInputEggs(className);
        if(e == null)
            return ret;

        for(InputEgg egg: e) {
            VNDataAcceptor[] vndas = egg.getVNDataAcceptors();
            if(vndas != null)
                ret.put(egg.getName(), vndas);
        }
        return ret;
    }

    @Override
    public Vector<LibraryCore> getAllCores() {
        if(this.rootFolder == null)
            return null;
        
        return rootFolder.getAllCores();
    }

}

