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
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.StringTokenizer;
import pl.edu.icm.visnow.application.application.Application;
import pl.edu.icm.visnow.engine.core.Link;
import pl.edu.icm.visnow.engine.library.LibraryRoot;
import pl.edu.icm.visnow.engine.core.Input;
import pl.edu.icm.visnow.engine.main.ModuleBox;
import pl.edu.icm.visnow.engine.core.Output;








/**
 *
 * @author Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class VNWriter {

    //<editor-fold defaultstate="collapsed" desc=" Utils ">
    private static void writeln(int n, FileWriter writer, String line) throws IOException {
        String ind = "";
        for(int i=0; i<n; ++i) {
            ind = ind + "    ";
        }
        writer.write(ind+line+"\n");
    }
    //</editor-fold>


    public static boolean writeApplication(Application application, File file) {
        System.out.println("WRITE");
        try {
            return tryWriteApplication(application, file);
        } catch (IOException ex) {
            System.out.println("EXCEPTION");
            ex.printStackTrace();
        }
        return false;
    }

    private static boolean tryWriteApplication(Application application, File file) throws IOException {
        
        FileWriter writer = new FileWriter(file);

        writeln(0, writer, "@application name<"+application.getTitle()+">");
        writeln(0, writer, "");
        for(LibraryRoot root: application.getLibraries()) {
            writeln(0, writer, "@library name<"+root.getName()+"> file<"+root.getFilePath()+">");
        }
        writeln(0, writer, "");

        //Map<ModuleBox, Integer> modules = new HashMap<ModuleBox, Integer>();
        //Map<Link, Integer> links = new HashMap<Link, Integer>();

        PriorityQueue<ModulePriority> pq = new PriorityQueue<ModulePriority>();
        HashMap<ModuleBox, ModulePriority> hm = new HashMap<ModuleBox, ModulePriority>();

        for(ModuleBox mb: application.getEngine().getModules().values()) {
            ModulePriority m = new ModulePriority(mb);
            pq.add(m);
            hm.put(mb, m);
        }

        for(Link link: application.getEngine().getLinks().values()) {
            ModulePriority m = hm.get(link.getInput().getModuleBox());
            pq.remove(m);
            m.priority++;
            pq.add(m);
        }

        while(!pq.isEmpty()) {
            ModuleBox mb = pq.poll().module;
            System.out.println("WRITING MODULE ["+mb.getName()+"]");
            writeModule(mb, application, writer);
            for(Output o: mb.getOutputs()) {
                for(Link link: o.getLinks()) {
                    ModulePriority m = hm.get(link.getInput().getModuleBox());
                    pq.remove(m);
                    m.priority--;
                    pq.add(m);
                }
            }
        }

        writer.close();
        return true;
    }


    private static void writeModule(ModuleBox module, Application application, FileWriter writer) throws IOException {
        Point p = application.getArea().getInput().getModulePosition(module.getName());
        writeln(0, writer, "@module"+
                " name<"+module.getName()+">"+
                " class<"+module.getCore().getCoreName().getClassName()+">"+
                " library<"+module.getCore().getCoreName().getLibraryName()+">"+
                " x<"+p.x+"> y<"+p.y+">");
        for(Input in: module.getInputs()) {
            for(Link link: in.getLinks()) {
                writeln(1, writer, "@link"+
                    " from<"+link.getOutput().getModuleBox().getName()+">"+
                    " out<"+link.getOutput().getName()+">"+
                    " to<"+link.getInput().getModuleBox().getName()+">"+
                    " in<"+link.getInput().getName()+">");
            }
        }
        writeln(0, writer, "@params");

//        String params = module.getParameters().writeXML();
//
//        if(params != null) {//TODO: trzeba uniknąć komend w parametrach
//            //params = encode(params);
//            params = params + "\n";
//            StringTokenizer tokenizer = new StringTokenizer(params, "\n");
//            while(tokenizer.hasMoreElements()) {
//                writeln(3, writer, tokenizer.nextToken());
//            }
//        }

        writeln(0, writer, "@end");

        writeln(0, writer, "");
    }

   private VNWriter()
   {
   }
    
}
class ModulePriority implements Comparable {

    public Integer priority;
    public ModuleBox module;

    public ModulePriority(ModuleBox mb) {
        module = mb;
        priority = 0;
    }

    public int compareTo(Object o) {
        if(o instanceof ModulePriority)
            return priority.compareTo(((ModulePriority)o).priority);
        return priority.compareTo(priority);
    }

}