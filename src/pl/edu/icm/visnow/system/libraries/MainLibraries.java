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

package pl.edu.icm.visnow.system.libraries;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Vector;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import pl.edu.icm.visnow.engine.library.LibraryCore;
import pl.edu.icm.visnow.engine.library.LibraryFolder;
import pl.edu.icm.visnow.engine.library.LibraryRoot;
import pl.edu.icm.visnow.engine.library.jar.JarLibReader;
import pl.edu.icm.visnow.lib.types.VNDataAcceptor;
import pl.edu.icm.visnow.lib.types.VNDataSchema;
import pl.edu.icm.visnow.lib.types.VNDataSchemaComparator;
import pl.edu.icm.visnow.lib.types.VNDataSchemaInterface;
import pl.edu.icm.visnow.system.main.VisNow;

/**
 *
 * @author Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class MainLibraries {

    protected Vector<LibraryRoot> libraries;
    protected LibraryRoot internalLibrary;

    public LibraryRoot getInternalLibrary() {return internalLibrary;}
    public Vector<LibraryRoot> getLibraries() {return libraries;}


    public boolean addLibrary(LibraryRoot library) {
        if(library==null) return false;
        libraries.add(library);
        return true;
    }

    public MainLibraries() {
        this.libraries = new Vector<LibraryRoot>();
        
        internalLibrary = JarLibReader.readFromJar(VisNow.get().getJarPath());
        libraries.add(internalLibrary);
    }

    public LibraryRoot getLibrary(String name) {
        for(LibraryRoot library: libraries)
            if(library.getName().equals(name))
                return library;
        return null;
    }

    public DefaultTreeModel getLibrariesTreeModel() {
        DefaultMutableTreeNode mainNode = new DefaultMutableTreeNode();
        for (LibraryRoot library : libraries) {
            mainNode.add(createNodeFromFolder(library.getRootFolder()));
        }

        return new DefaultTreeModel(mainNode);
    }

    public DefaultTreeModel getNameFilteredLibrariesTreeModel(String name) {
        DefaultMutableTreeNode mainNode = new DefaultMutableTreeNode();
        for (LibraryRoot library : libraries) {
            mainNode.add(createNameFilteredNodeFromFolder(library.getRootFolder(), name));
        }
        return new DefaultTreeModel(mainNode);
    }

    public DefaultTreeModel getTypeFilteredLibrariesTreeModel(String classname) {
        return getSchemaFilteredLibrariesTreeModel(classname, null);
    }

    public DefaultTreeModel getSchemaFilteredLibrariesTreeModel(String classname, VNDataSchemaInterface[] schemas) {
        DefaultMutableTreeNode mainNode = new DefaultMutableTreeNode();
        for (LibraryRoot library : libraries) {
            mainNode.add(createSchemaFilteredNodeFromFolder(library.getRootFolder(), classname, schemas));
        }
        return new DefaultTreeModel(mainNode);
    }


    private MutableTreeNode createNodeFromFolder(LibraryFolder library) {
        DefaultMutableTreeNode mtn = new DefaultMutableTreeNode(library);
        for (LibraryFolder lib : library.getSubFolders()) {
            mtn.add(createNodeFromFolder(lib));
        }
        for (LibraryCore core : library.getCores()) {
            mtn.add(createNodeFromCore(core));
        }
        return mtn;
    }

    private MutableTreeNode createNameFilteredNodeFromFolder(LibraryFolder library, String name) {
        DefaultMutableTreeNode mtn = new DefaultMutableTreeNode(library);
        for (LibraryFolder lib : library.getSubFolders()) {
            MutableTreeNode tmp = createNameFilteredNodeFromFolder(lib, name);
            if(tmp.getChildCount() > 0)
                mtn.add(tmp);
        }
        for (LibraryCore core : library.getCores()) {
            MutableTreeNode tmp = createNameFilteredNodeFromCore(core, name);
            if(tmp != null)
                mtn.add(tmp);
        }
        return mtn;
    }

    private MutableTreeNode createSchemaFilteredNodeFromFolder(LibraryFolder library, String classname, VNDataSchemaInterface[] schemas) {
        DefaultMutableTreeNode mtn = new DefaultMutableTreeNode(library);
        for (LibraryFolder lib : library.getSubFolders()) {
            MutableTreeNode tmp = createSchemaFilteredNodeFromFolder(lib, classname, schemas);
            if(tmp.getChildCount() > 0)
                mtn.add(tmp);
        }
        for (LibraryCore core : library.getCores()) {
            MutableTreeNode tmp = createSchemaFilteredNodeFromCore(core, classname, schemas);
            if(tmp != null)
                mtn.add(tmp);
        }
        return mtn;
    }

    private MutableTreeNode createNodeFromCore(LibraryCore core) {
        return new DefaultMutableTreeNode(core);
    }

    private MutableTreeNode createNameFilteredNodeFromCore(LibraryCore core, String name) {
        if(core.getName().toLowerCase().contains(name.toLowerCase()))
            return new DefaultMutableTreeNode(core);
        else
            return null;
    }

    private MutableTreeNode createSchemaFilteredNodeFromCore(LibraryCore core, String classname, VNDataSchemaInterface[] schemas) {
        if(core == null)
            return null;

        DefaultMutableTreeNode ret = new DefaultMutableTreeNode(core);
        boolean anything = false;
        HashMap<String, String> str = core.getInputTypes();
        HashMap<String, VNDataAcceptor[]> vndasList = core.getInputVNDataAcceptors();
        if(str == null) return null;
        for(Entry<String, String> e: str.entrySet()) {
            try {
                if(Class.forName(e.getValue()).isAssignableFrom(Class.forName(classname))) {

                    VNDataAcceptor[] vndas = vndasList.get(e.getKey());
                    boolean acceptableEntry = false;                    
                    boolean conditionalAccept = false; 
                    if(schemas == null || vndas == null || vndas.length == 0) {
                        
                        acceptableEntry = true;
                        conditionalAccept = true;
                        
                    } else {
here:                  for (int i = 0; i < vndas.length; i++) {
                            for (int j = 0; j < schemas.length; j++) {
                                
                                boolean tmp = VNDataSchemaComparator.isCompatible(schemas[j], vndas[i].getVNDataSchemaInterface(), vndas[i].getVNDataCompatibilityMask());
                                if(tmp) {
                                    acceptableEntry = true;
                                    break here;
                                }
                                
                                if(schemas[j] instanceof VNDataSchema) {
                                    long schemaMask = VNDataSchemaComparator.createComparatorFromSchemaParams(((VNDataSchema)schemas[j]).getParamsList());
                                    long acceptorMask = vndas[i].getVNDataCompatibilityMask();                    
                                    tmp = VNDataSchemaComparator.isConditionallyCompatible(schemas[j], schemaMask, vndas[i].getVNDataSchemaInterface(), acceptorMask);
                                    if(tmp) {
                                        acceptableEntry = true;
                                        conditionalAccept = true;
                                        break here;
                                    }
                                }
                            }
                        }
                    }

                    if(acceptableEntry) {
                        anything = true;
                        DefaultMutableTreeNode node = new DefaultMutableTreeNode(e.getKey(),!conditionalAccept);
                        ret.add(node);
                    }

                }
            } catch (ClassNotFoundException ex) {
                System.out.println("Class not found: ["+classname+"] or ["+e.getValue()+"] in core ["+core.getName()+"]");
                //Displayer.ddisplay(201002091100L, e, this, "ERROR IN LIBRARY? Class not found.");
            }
        }

        return (anything)?ret:null;
    }

}
