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

import java.awt.Point;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;
import pl.edu.icm.visnow.application.application.Application;
import pl.edu.icm.visnow.engine.core.Link;
import pl.edu.icm.visnow.engine.error.Displayer;
import pl.edu.icm.visnow.engine.exception.VNApplicationNetLoopException;
import pl.edu.icm.visnow.engine.exception.VNOuterIOException;
import pl.edu.icm.visnow.engine.library.LibraryRoot;
import pl.edu.icm.visnow.engine.main.ModuleBox;

/**
 *
 * @author Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class XMLWriter {
    private static final boolean debug = false;

    //<editor-fold defaultstate="collapsed" desc=" Utils ">
    private static void writeln(int n, FileWriter writer, String line) throws IOException {
        String ind = "";
        for(int i=0; i<n; ++i) {
            ind = ind + "    ";
        }
        writer.write(ind+line+"\n");
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Write app ">
    public static boolean writeApplication(Application application, File file) {
        //System.out.println("write application");
        try {
                return tryWriteApplication(application, file);
        } catch (VNApplicationNetLoopException ex) {
            Displayer.display(200911201200L,
                    new VNOuterIOException(
                        200901010100L,
                        "Application network has a loop.",
                        ex,
                        null,
                        Thread.currentThread()
                    ),
                    null, "Could not write application.");
            return false;
        } catch (IOException ex) {
            Displayer.display(200901010101L,
                    new VNOuterIOException(
                        200901010100L,
                        "IO exception.",
                        ex,
                        null,
                        Thread.currentThread()
                    ),
                    null, "Could not write application.");
            return false;
        }
    }

    public static boolean tryWriteApplication(Application application, File file) throws IOException, VNApplicationNetLoopException {
        FileWriter writer = new FileWriter(file);
        writeln(0, writer, "<?xml version=\'1.0\' encoding=\'utf-8\'?>");
        writeAll(application, writer);
        writer.close();
        return true;
    }

    private static void writeAll(Application application, FileWriter writer) throws IOException, VNApplicationNetLoopException {
        writeln(0, writer, "<application name=\"" + application.getTitle() + "\">");
        writeLibraries(application, writer);
        writeNetwork(application, writer);
        writeGui(application, writer);
        writeln(0, writer, "</application>");
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Libraries ">
    private static void writeLibraries(Application application, FileWriter writer) throws IOException {
        writeln(1, writer, "<libraries>");
        for(LibraryRoot library: application.getLibraries()) {
            writeLibrary(library, writer);
        }
        writeln(1, writer, "</libraries>");
    }

    private static void writeLibrary(LibraryRoot root, FileWriter writer) throws IOException {
        writeln(2, writer,
                "<library " +
                "name=\"" + root.getName() + "\"" +
                "/>");
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Modules, Links ">
    private static void writeNetwork(Application application, FileWriter writer) throws IOException, VNApplicationNetLoopException {
        writeln(1, writer, "<modules>");
        for(ModuleBox module: application.getEngine().getTopologicalModules()) {
            Point p = application.getArea().getInput().getModulePosition(module.getName());
            writeModule(module,
                    p.x,
                    p.y,
                    writer);
        }
        writeln(1, writer, "</modules>");
        writeln(1, writer, "<links>");
        for(Link link: application.getEngine().getLinks().values()) {
            writeLink(link, writer);
        }
        writeln(1, writer, "</links>");
    }

    private static void writeModule(ModuleBox module, int x, int y, FileWriter writer) throws IOException {
        writeln(2, writer,
                "<module " +
                "name=\"" + module.getName() + "\" " +
                "classname=\"" + module.getCore().getCoreName().getClassName() + "\" " +
                "library=\"" + module.getCore().getCoreName().getLibraryName() + "\" " +
                "x=\"" + x + "\" " +
                "y=\"" + y + "\"" +
                ">");

        if(debug) System.out.println("WRITE MODULE : "+module.getName());
        String params = module.getParameters().writeXML();
        //System.out.println(module.getParameters());
        if(debug) System.out.println("PARAMS :"+params);
        if(params != null) {
            params = encode(params);
            params = params + "\n";
            StringTokenizer tokenizer = new StringTokenizer(params, "\n");
            while(tokenizer.hasMoreElements()) {
                writeln(3, writer, tokenizer.nextToken());
            }
        }
        writeln(2, writer, "</module>");
    }


    private static String encode(String in) {
        String ret = "";
        StringTokenizer tokenizer = new StringTokenizer(in, "<>[]|", true);
        while(tokenizer.hasMoreTokens()) {
            String next = tokenizer.nextToken();
            if(next.equals("|")) {ret += "|+"; continue;}
            if(next.equals("<")) {ret += "["; continue;}
            if(next.equals(">")) {ret += "]"; continue;}
            if(next.equals("[")) {ret += "|{"; continue;}
            if(next.equals("]")) {ret += "|}"; continue;}
            ret += next;
        }


        return ret;
    }


    private static void writeLink(Link link, FileWriter writer) throws IOException {
        writeln(2, writer, "<link>");
            writeln(3, writer,
                    "<output " + 
                    "module=\"" + link.getOutput().getModuleBox().getName() + "\" " +
                    "port=\"" + link.getOutput().getName() + "\"" +
                    "/>");
            writeln(3, writer,
                    "<input " + 
                    "module=\"" + link.getInput().getModuleBox().getName() + "\" " +
                    "port=\"" + link.getInput().getName() + "\"" +
                    "/>");
        writeln(2, writer, "</link>");
    }
    //</editor-fold>

    
    private static void writeGui(Application application, FileWriter writer) throws IOException {
        
    }

   private XMLWriter()
   {
   }



    

}
