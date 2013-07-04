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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import pl.edu.icm.visnow.engine.error.Displayer;
import pl.edu.icm.visnow.engine.exception.VNOuterDataException;
import pl.edu.icm.visnow.engine.library.LibraryCore;
import pl.edu.icm.visnow.engine.library.LibraryFolder;
import pl.edu.icm.visnow.engine.library.TypesMap;
import pl.edu.icm.visnow.system.libraries.TypeStyle;
import pl.edu.icm.visnow.system.main.VisNow;
import pl.edu.icm.visnow.system.swing.VNSwingUtils;

/**
 *
 * @author Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class JarLibReader
{

   //<editor-fold defaultstate="collapsed" desc=" Interface ">
   public static JarLibraryRoot readFromJar(String path)
   {
      return readFromJar(new File(path));
   }

   public static JarLibraryRoot readFromJar(File file)
   {
      try
      {
         return tryReadFromJar(file);
      } catch (VNOuterDataException ex)
      {
         Displayer.display(200907100900L, ex, "STATIC: JarLibReader", "Could not read library from jar.");
      } catch (ParserConfigurationException ex)
      {
         Displayer.ddisplay(200907100901L, ex, "STATIC: JarLibReader", "Could not read library from jar.");
      } catch (SAXException ex)
      {
         Displayer.ddisplay(200907100902L, ex, "STATIC: JarLibReader", "Could not read library from jar.");
      } catch (IOException ex)
      {
         Displayer.ddisplay(200907100903L, ex, "STATIC: JarLibReader", "Could not read library from jar.");
      }
      return null;
   }
   //</editor-fold>

   protected static JarLibraryRoot tryReadFromJar(File file) throws IOException, ParserConfigurationException, SAXException, VNOuterDataException
   {
      JarFile jar = new JarFile(file);
      Enumeration<JarEntry> enumeration;
      JarEntry tmpEntry;
      JarEntry typesEntry = null;
      JarEntry libraryEntry = null;
      enumeration = jar.entries();

      int ii = 2;
      libraryLoop:
      while (enumeration.hasMoreElements())
      {
         tmpEntry = enumeration.nextElement();
         if (tmpEntry.getName().toLowerCase().equals("types.xml"))
         {
            typesEntry = tmpEntry;
            --ii;
            if (ii == 0)
               break libraryLoop;
         }
         switch (VisNow.libraryLevel)
         {
         case VisNow.FULL_LIBRARY:
            if (tmpEntry.getName().toLowerCase().equals("extended_library.xml"))
            {
               libraryEntry = tmpEntry;
               --ii;
               if (ii == 0)
                  break;
            }  
            break;
         case VisNow.BASIC_LIBRARY:  
            if (tmpEntry.getName().toLowerCase().equals("base_library.xml"))
            {
               libraryEntry = tmpEntry;
               --ii;
               if (ii == 0)
                  break libraryLoop;
            }
            break;
         case VisNow.SIMPLE_LIBRARY:  
            if (tmpEntry.getName().toLowerCase().equals("simple_library.xml"))
            {
               libraryEntry = tmpEntry;
               --ii;
               if (ii == 0)
                  break libraryLoop;
            }
            break;
         }
      }

      /*
       * TODO: types
       */

      if (libraryEntry == null)
         return null;
      ///////////////////LIBRARY FILE FOUND, GENERATING LIBRARY
      TypesMap typesMap = null;
      if (typesEntry != null)
         typesMap = readTypesMap(jar, typesEntry);
      ///////////////////GENERATE CLASS LOADER
      URLClassLoader loader =
              new URLClassLoader(new URL[]
              {
                 file.toURI().toURL()
              });
      ///////////////////PREPARE TO PARSE XML
      InputStream is = null;
      is = jar.getInputStream(libraryEntry);
      Node main = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is).getDocumentElement();
      if (!main.getNodeName().equalsIgnoreCase("library"))
         throw new VNOuterDataException(200903271350L, "Main node is not a library node.", null, null, Thread.currentThread());
      NodeList list = main.getChildNodes();

      JarLibraryRoot root;
      String tmp = file.getPath();
      if (tmp.contains("\\"))
      {
         tmp = file.getPath().replace('\\', '/');
         tmp = "/" + tmp;
      }
      //if(file.getPath().equals(VisNow.get().getJarPath()))
      if (tmp.equals(VisNow.get().getJarPath()))
         root = new InternalLibraryRoot(
                 main.getAttributes().getNamedItem("name").getNodeValue(),
                 file.getPath(),
                 loader);
      else
         root = new JarLibraryRoot(
                 main.getAttributes().getNamedItem("name").getNodeValue(),
                 file.getPath(),
                 loader);
      
      boolean sorted = false;
      if(main.getAttributes().getNamedItem("autosort") != null) {
            sorted = main.getAttributes().getNamedItem("autosort").getNodeValue().equalsIgnoreCase("yes");
      }
      
      boolean open = false;
      if(main.getAttributes().getNamedItem("open") != null) {
            open = main.getAttributes().getNamedItem("open").getNodeValue().equalsIgnoreCase("yes");
      }
      
      LibraryFolder rootFolder = new LibraryFolder(
              root,
              main.getAttributes().getNamedItem("name").getNodeValue(),
              new Vector<LibraryFolder>(),
              new Vector<LibraryCore>(),
              sorted,
              open);
      root.setRootFolder(rootFolder);
      root.setTypesMap(typesMap);

      for (int i = 0; i < list.getLength(); ++i)
         if (list.item(i).getNodeName().equalsIgnoreCase("folder")) {
            rootFolder.getSubFolders().add(readFolder(root, list.item(i)));
         } else if (list.item(i).getNodeName().equalsIgnoreCase("core")) {
             LibraryCore core = readCore(root, list.item(i));
             if(core != null) {
                rootFolder.getCores().add(core);
             } else {
                String str = "";
                if (list.item(i).getAttributes().getNamedItem("name") != null
                        && list.item(i).getAttributes().getNamedItem("class") != null) {
                    str = list.item(i).getAttributes().getNamedItem("class").getNodeValue();                    
                } else if (list.item(i).getAttributes().getNamedItem("package") != null) {
                    str = list.item(i).getAttributes().getNamedItem("package").getNodeValue();                    
                }                 
                System.err.println("ERROR: bad entry in library.xml file: "+str);                 
             }
         }
      return root;

   }

   protected static TypesMap readTypesMap(JarFile jar, JarEntry typesEntry) throws IOException, ParserConfigurationException, SAXException, VNOuterDataException
   {
      InputStream is = jar.getInputStream(typesEntry);

      TypesMap out = readTypesMap(is);
      is.close();

      return out;
   }

   public static TypesMap readTypesMap(InputStream is) throws IOException, ParserConfigurationException, SAXException, VNOuterDataException
   {
      if (is == null)
         return null;

      Node main = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is).getDocumentElement();
      if (!main.getNodeName().equalsIgnoreCase("types"))
         throw new VNOuterDataException(200903271351L, "Main node is nod a types node.", null, null, Thread.currentThread());
      NodeList list = main.getChildNodes();

      TypesMap typesMap = new TypesMap();

      for (int i = 0; i < list.getLength(); ++i)
         if (list.item(i).getNodeName().equalsIgnoreCase("type"))
            typesMap.getStyles().put(
                    list.item(i).getAttributes().getNamedItem("classname").getNodeValue(),
                    readTypeStyle(list.item(i)));

      return typesMap;
   }

   protected static TypeStyle readTypeStyle(Node node)
   {
      String value = node.getAttributes().getNamedItem("color").getNodeValue();
      return new TypeStyle(VNSwingUtils.color(value));
   }

   //<editor-fold defaultstate="collapsed" desc=" Read Folder ">
   protected static LibraryFolder readFolder(JarLibraryRoot root, Node node)
   {
      boolean sorted = false;
      if(node.getAttributes().getNamedItem("autosort") != null) {
            sorted = node.getAttributes().getNamedItem("autosort").getNodeValue().equalsIgnoreCase("yes");
      }

      boolean open = false;
      if(node.getAttributes().getNamedItem("open") != null) {
            open = node.getAttributes().getNamedItem("open").getNodeValue().equalsIgnoreCase("yes");
      }
      
       LibraryFolder ret = new LibraryFolder(
              root,
              node.getAttributes().getNamedItem("name").getNodeValue(),
              new Vector<LibraryFolder>(),
              new Vector<LibraryCore>(),
              sorted,
              open
              );
      NodeList list = node.getChildNodes();

      for (int i = 0; i < list.getLength(); ++i)
         if (list.item(i).getNodeName().equalsIgnoreCase("folder"))
            ret.getSubFolders().add(readFolder(root, list.item(i)));
         else if (list.item(i).getNodeName().equalsIgnoreCase("core"))
            ret.getCores().add(readCore(root, list.item(i)));

      return ret;
   }
   //</editor-fold>

   //<editor-fold defaultstate="collapsed" desc=" Read Core ">
   protected static LibraryCore readCore(JarLibraryRoot root, Node node)
   {
      try
      {
         LibraryCore core = null;
         if (node.getAttributes().getNamedItem("name") != null
                 && node.getAttributes().getNamedItem("class") != null)
            core = new LibraryCore(
                    root,
                    node.getAttributes().getNamedItem("name").getNodeValue(),
                    node.getAttributes().getNamedItem("class").getNodeValue());
         else if (node.getAttributes().getNamedItem("package") != null)
            core = new LibraryCore(
                    root,
                    node.getAttributes().getNamedItem("package").getNodeValue());
         
         if(core != null && core.isValid())
            return core;
         else
             return null;
      } catch (NullPointerException ex)
      {
         Displayer.display(
                 200907061840L,
                 new VNOuterDataException(
                 20090706141L,
                 "NullPointerException",
                 ex,
                 null,
                 Thread.currentThread()),
                 "STATIC: JarLibReader",
                 "Incorrect node: " + node.getAttributes().getNamedItem("name"));
         return null;
      } catch (ClassNotFoundException ex)
      {
         //TODO poprawic bledy
         System.err.println("ERROR in library.xml - wrong class in node: " + node.getAttributes().getNamedItem("name") + "");
         return null;
      }
   }

//    protected static LibraryCore readCore(JarLibraryRoot root, Node node) {
//        try {
//            LibraryCore core = new LibraryCore(
//                root,
//                node.getAttributes().getNamedItem("name").getNodeValue(),
//                node.getAttributes().getNamedItem("class").getNodeValue()
//                );
//            return core;
//       } catch (NullPointerException ex) {
//            Displayer.display(
//                    200907061840L,
//                    new VNOuterDataException(
//                        20090706141L,
//                        "NullPointerException",
//                        ex,
//                        null,
//                        Thread.currentThread()
//                    ),
//                    "STATIC: JarLibReader",
//                    "Incorrect node: "+node.getAttributes().getNamedItem("name"));
//            return null;
//        } catch (ClassNotFoundException ex) {
//            System.err.println("ERROR in library.xml - wrong class in node: "+node.getAttributes().getNamedItem("name")+"");
//            return null;
//        }
//    }
   //</editor-fold>
   private JarLibReader()
   {
   }
}
