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

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import pl.edu.icm.visnow.engine.core.CoreName;
import pl.edu.icm.visnow.engine.core.ModuleCore;
import pl.edu.icm.visnow.engine.core.ModuleXMLReader;
import pl.edu.icm.visnow.engine.exception.VNException;
import pl.edu.icm.visnow.engine.library.jar.JarLibraryRoot;
import pl.edu.icm.visnow.lib.types.VNDataAcceptor;

/**
 *
 * @author Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class LibraryCore implements Comparable {

    private boolean valid = false;
    protected LibraryRoot root;
    protected String className;
    protected String name;
    protected String shortDescription = null;
    protected String helpTopicID = null;
    protected CoreName coreName;
    protected boolean reader = false;
    protected String readerDataType = null;
    protected boolean testData = false;

    public String getName() {return name;}
    public CoreName getCoreName() {return coreName;}
    public LibraryRoot getRoot() {return root;}
    public String getClassPath() {return className;}
    public String getShortDescription() {return shortDescription;}
    public String getHelpTopicID() {return helpTopicID;}
    public boolean isReader() {return reader;}
    public boolean isTestData() {return testData;}
    public String getReaderDataType() {return readerDataType;}

    public ModuleCore generateCore() throws VNException {
        return root.loadCore(className);
    }

    public LibraryCore(LibraryRoot root, String name, String className) throws ClassNotFoundException {
        this.valid = true;
        this.root = root;
        this.name = name;
        this.className = className;
        this.coreName = new CoreName(root.getName(), className);

        if(root instanceof JarLibraryRoot) {
            Class coreClass = ((JarLibraryRoot)root).getLoader().loadClass(className);
        }
    }

    public LibraryCore(LibraryRoot root, String packageName) throws ClassNotFoundException {
        this.root = root;
        this.valid = false;

        String[] tmp = null;
        try {
            if(root instanceof JarLibraryRoot)
                tmp = ModuleXMLReader.getModuleInfo(packageName, ((JarLibraryRoot)root).getLoader());
            else            
                tmp = ModuleXMLReader.getModuleInfo(packageName, null);
        } catch(IOException ex) {
        } catch(ParserConfigurationException ex) {
        } catch(SAXException ex) {
        } catch(URISyntaxException ex) {
        }
        if(tmp == null || tmp.length != 6)
            return;

        this.valid = true;
        this.name = tmp[0];
        this.className = tmp[1];
        this.coreName = new CoreName(root.getName(), this.className);
        this.shortDescription = tmp[2];
        this.helpTopicID = tmp[3];
        if(tmp[4] != null) {
            this.reader = true;
            this.readerDataType = tmp[4];            
        }
        this.testData = (tmp[5] != null && tmp[5].equals("true"));

//        if(root instanceof JarLibraryRoot) {
//            Class coreClass = ((JarLibraryRoot)root).getLoader().loadClass(this.className);
//        }
    }

    public HashMap<String, String> getInputTypes() {
        return root.getInputTypes(className);
    }

    public HashMap<String, VNDataAcceptor[]> getInputVNDataAcceptors() {
        return root.getInputVNDataAcceptors(className);
    }

    @Override
    public int compareTo(Object o) {
        if(o == null || !(o instanceof LibraryCore))
            return 1;
        
        if(o == this)
            return 0;
        
        return this.getName().compareTo(((LibraryCore)o).getName());
    }

    /**
     * @return the valid
     */
    public boolean isValid() {
        return valid;
    }
}
