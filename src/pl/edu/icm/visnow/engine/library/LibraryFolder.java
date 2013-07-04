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

package pl.edu.icm.visnow.engine.library;

import java.util.Collections;
import java.util.Vector;

/**
 *
 * @author Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class LibraryFolder implements Comparable {

    protected String name;
    protected LibraryRoot root;
    protected Vector<LibraryFolder> folders;
    protected Vector<LibraryCore> cores;
    protected boolean sorted = false;
    protected boolean open = false;

    public String getName() {return name;}
    public LibraryRoot getRoot() {return root;}
    public Vector<LibraryFolder> getSubFolders() {
        if(sorted)
            Collections.sort(folders);
        return folders;
    }
    public Vector<LibraryCore> getCores() {
        if(sorted)
            Collections.sort(cores);
        return cores;
    }
    
    public Vector<LibraryCore> getAllCores() {
        Vector<LibraryCore> out = new Vector<LibraryCore>();
        if(folders != null) {
            for (int i = 0; i < folders.size(); i++) {
                out .addAll(folders.get(i).getAllCores());
            }
        }
        out.addAll(cores);        
        return out;
    }

    public LibraryFolder(LibraryRoot root, String name, Vector<LibraryFolder> subFolders, Vector<LibraryCore> subCores, boolean sorted, boolean open) {
        this.root = root;
        this.name = name;
        this.folders = subFolders;
        this.cores = subCores;        
        this.sorted = sorted;
        this.open = open;
    }

    @Override
    public int compareTo(Object o) {
        if(o == null || !(o instanceof LibraryFolder))
            return 1;
        
        if(o == this)
            return 0;
        
        return this.getName().compareTo(((LibraryFolder)o).getName());
    }
    
    public boolean isOpen() {
        return this.open;
    }
    
}
