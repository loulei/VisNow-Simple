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

package pl.edu.icm.visnow.application.io;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import pl.edu.icm.visnow.application.application.Application;
import pl.edu.icm.visnow.engine.core.CoreName;
import pl.edu.icm.visnow.engine.commands.LibraryAddCommand;
import pl.edu.icm.visnow.engine.commands.LinkAddCommand;
import pl.edu.icm.visnow.engine.commands.ModuleAddCommand;
import pl.edu.icm.visnow.engine.core.LinkName;
import pl.edu.icm.visnow.engine.exception.VNOuterDataException;
import pl.edu.icm.visnow.engine.exception.VNOuterException;
import pl.edu.icm.visnow.engine.exception.VNOuterIOException;
import pl.edu.icm.visnow.engine.library.LibraryRoot;
import pl.edu.icm.visnow.system.Pair;

/**
 *
 * @author Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class XMLReader {
    
    private static boolean isDigit(char c) {
        return (c>='0')&&(c<='9');
    }

    private static int getLastInt(String s) {
        //System.out.println("getLastInt["+s+"]");
        String ch = "";
        boolean started = false;
        int i = s.length()-1;
        while(!isDigit(s.charAt(i))) {
        //    System.out.println("i="+i);
            --i; if(i<0) return 0;
        }
        while(isDigit(s.charAt(i))) {
            ch = ""+s.charAt(i)+ch;
            --i; if(i<0) break;
        }
        return Integer.parseInt(ch);
    }

    //<editor-fold defaultstate="collapsed" desc=" Read ">
    public static Application readXML(File file) throws VNOuterException {
        try {
            return tryReadXML(file);
        } catch (ParserConfigurationException ex) {
            throw new VNOuterException(
                    200903160142L,
                    "XML parser configuration exception, could not read application.",
                    ex,
                    null,
                    Thread.currentThread());
            //throw new VN1IOException(200903160142L, null, Thread.currentThread(), "ParserConfigurationException", ex);
        } catch (IOException ex) {
            throw new VNOuterIOException(
                    200903160143L,
                    "Could not read application.",
                    ex,
                    null,
                    Thread.currentThread());
        } catch (SAXException ex) {
            throw new VNOuterException(
                    200903160144L,
                    "XML (SAX) exception, could not read application.",
                    ex,
                    null,
                    Thread.currentThread());
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Read ">
    private static Application tryReadXML(File file)
            throws ParserConfigurationException, IOException, SAXException, VNOuterDataException {
        Node node = DocumentBuilderFactory.newInstance().
                newDocumentBuilder().
                parse(file).
                getDocumentElement();
        if (!node.getNodeName().equalsIgnoreCase("application")) {
            throw new VNOuterDataException(
                    200903160141L, "Wrong main node name", null, null, Thread.currentThread());
        }

        Vector<Pair<String, String>> parameters = new Vector<Pair<String, String>>();

        Node librariesNode = null;
        Node modulesNode = null;
        Node linksNode = null;

        String title;
        try {
            title = node.getAttributes().getNamedItem("name").getNodeValue();
        } catch (NullPointerException ex) {
            throw new VNOuterDataException(
                    200903160140L, "No title", ex, null, Thread.currentThread());
        }

        for (int i = 0; i < node.getChildNodes().getLength(); ++i) {
            String nodeName = node.getChildNodes().item(i).getNodeName();
            if (nodeName.equalsIgnoreCase("libraries")) {
                librariesNode = node.getChildNodes().item(i);
            }
            if (nodeName.equalsIgnoreCase("modules")) {
                modulesNode = node.getChildNodes().item(i);
            }
            if (nodeName.equalsIgnoreCase("links")) {
                linksNode = node.getChildNodes().item(i);
            }
        }

        if (librariesNode == null) {
            throw new VNOuterDataException(
                    200903160146L, "No libraries node", null, null, Thread.currentThread());
        }
        if (modulesNode == null) {
            throw new VNOuterDataException(
                    200903160147L, "No modules node", null, null, Thread.currentThread());
        }
        if (linksNode == null) {
            throw new VNOuterDataException(
                    200903160148L, "No links node", null, null, Thread.currentThread());
        }

        Application application = new Application(title, file);

        insertLibraries(application, librariesNode);
        insertModules(application, modulesNode, parameters);
        insertLinks(application, linksNode);
        insertParameters(application, parameters);

        application.clearHistory();

        return application;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Insert libraries ">
    private static void insertLibraries(Application application, Node node) {
        Vector<Node> nodes = new Vector<Node>();
        for (int i = 0; i < node.getChildNodes().getLength(); ++i) {
            if (node.getChildNodes().item(i).getNodeName().equalsIgnoreCase("library")) {
                nodes.add(node.getChildNodes().item(i));
            }
        }
        String tmpName;
        LibraryRoot library;
        for (Node tmp : nodes) {
            tmpName = tmp.getAttributes().getNamedItem("name").getNodeValue();
            application.getReceiver().receive(new LibraryAddCommand(tmpName));
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Insert modules ">
    private static void insertModules(Application application, Node node, Vector<Pair<String, String>> parameters) {
        Vector<Node> nodes = new Vector<Node>();
        for (int i = 0; i < node.getChildNodes().getLength(); ++i) {
            if (node.getChildNodes().item(i).getNodeName().equalsIgnoreCase("module")) {
                nodes.add(node.getChildNodes().item(i));
            }
        }
        String tmpName;
        String tmpPath;
        String tmpLib;
        int x;
        int y;
        for (Node tmp : nodes) {
            tmpName = tmp.getAttributes().getNamedItem("name").getNodeValue();
            tmpPath = tmp.getAttributes().getNamedItem("classname").getNodeValue();
            tmpLib = tmp.getAttributes().getNamedItem("library").getNodeValue();
            parameters.add(new Pair<String, String>(tmpName, tmp.getTextContent()));
            x = Integer.parseInt(tmp.getAttributes().getNamedItem("x").getNodeValue());
            y = Integer.parseInt(tmp.getAttributes().getNamedItem("y").getNodeValue());
            application.getReceiver().receive(
                    new ModuleAddCommand(
                    tmpName,
                    new CoreName(tmpLib, tmpPath),
                    new Point(x, y)));
            application.correctModuleCount(getLastInt(tmpName));
        }

    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Insert links ">
    private static void insertLinks(Application application, Node node) throws VNOuterDataException {
        Vector<Node> nodes = new Vector<Node>();
        for (int i = 0; i < node.getChildNodes().getLength(); ++i) {
            if (node.getChildNodes().item(i).getNodeName().equalsIgnoreCase("link")) {
                nodes.add(node.getChildNodes().item(i));
            }
        }

        NodeList tmpNodes;
        Node inputNode;
        Node outputNode;
        String from;
        String out;
        String to;
        String in;

        for (Node tmp : nodes) {

            tmpNodes = tmp.getChildNodes();

            inputNode = null;
            for (int j = 0; j < tmpNodes.getLength(); ++j) {
                if (tmpNodes.item(j).getNodeName().equalsIgnoreCase("input")) {
                    inputNode = tmpNodes.item(j);
                }
            }

            outputNode = null;
            for (int j = 0; j < tmpNodes.getLength(); ++j) {
                if (tmpNodes.item(j).getNodeName().equalsIgnoreCase("output")) {
                    outputNode = tmpNodes.item(j);
                }
            }
            if (inputNode == null) {
                throw new VNOuterDataException(200903260200L, "No input for a link.", null, null, Thread.currentThread());
            }
            if (outputNode == null) {
                throw new VNOuterDataException(200903260200L, "No output for a link.", null, null, Thread.currentThread());
            }

            from = outputNode.getAttributes().getNamedItem("module").getNodeValue();
            out = outputNode.getAttributes().getNamedItem("port").getNodeValue();
            to = inputNode.getAttributes().getNamedItem("module").getNodeValue();
            in = inputNode.getAttributes().getNamedItem("port").getNodeValue();
            //System.out.println("ADDING LINK [" + from + ":" + out + "]->[" + to + ":" + in + "]");
            application.getReceiver().receive(
                    new LinkAddCommand(
                    new LinkName(from, out, to, in),
                    false));
        }
    }
    //</editor-fold>

    private static void insertParameters(Application application, Vector<Pair<String, String>> parameters) {
        XStream xstream = new XStream(new DomDriver());

        for (Pair<String, String> entry : parameters) {
            application.getEngine().getModule(entry.getE()).getParameters().readXML(decode(entry.getF()));
//            for(int i = 0; i<entry.getValue().getLength(); ++i) {
//                Node node = entry.getValue().item(i);
//                if(!node.getNodeName().equals("parameter")) continue;
//                ////System.out.println(entry.getKey());
//                //System.out.println(node);
//                String parameter = node.getAttributes().getNamedItem("name").getNodeValue();
//                application.getEngine().getModule(entry.getKey()).getParameters()
//                        .setValue(parameter, xstream.fromXML(decode(node.getTextContent())));
//            }
        }
    }

    private static String decode(String in) {
        String ret = "";
        StringTokenizer tokenizer = new StringTokenizer(in, "[]|", true);
        while (tokenizer.hasMoreTokens()) {
            String next = tokenizer.nextToken();
            if (next.equals("[")) {
                ret += "<";
                continue;
            }
            if (next.equals("]")) {
                ret += ">";
                continue;
            }
            if (next.equals("|")) {
                next = tokenizer.nextToken();
                switch (next.charAt(0)) {
                    case '{':
                        ret += "[";
                        break;
                    case '}':
                        ret += "]";
                        break;
                    case '+':
                        ret += "|";
                        break;
                }
                ret += next.substring(1);
                continue;
            }
            ret += next;
        }


        return ret;
    }

   private XMLReader()
   {
   }
}
