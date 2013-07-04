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

package pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.PointsWizard;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.CustomSlicesDescriptor;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.PointDescriptor;


/**
 *
 * @author Bartosz Borucki (babor@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class NfdStructureWriter {

public static boolean writeNfd(NfdEntry nfd, String filePath, String comment) {
    ArrayList<PointEntry> points = new ArrayList<PointEntry>();
    ArrayList<ParameterEntry> parameters = new ArrayList<ParameterEntry>();

    try {
        if(filePath == null || nfd == null)
            return false;

        points = nfd.getAllPointEntries();
        parameters = nfd.getAllParameterEntries();
        
        File file = new File(filePath);
        FileWriter writer = new FileWriter(file);

        writeln(0, writer, "<?xml version=\'1.0\' encoding=\'utf-8\'?>");
        writeln(0, writer, "<nfd version=\""+nfd.getId()+"\">");

        writePoints(points, writer, true);
        writeParameters(parameters, writer);
        writeComment(comment, writer);

        writeln(0, writer, "</nfd>");
        writer.close();

        return true;
        
    } catch(Exception ex) {
        ex.printStackTrace();
        return false;
    }
}

public static boolean writeNfdStructure(NfdEntry nfd, String filePath) {
    ArrayList<PointEntry> points = new ArrayList<PointEntry>();
    ArrayList<ParameterEntry> parameters = new ArrayList<ParameterEntry>();

    try {
        if(filePath == null || nfd == null)
            return false;

        points = nfd.getAllPointEntries();
        parameters = nfd.getAllParameterEntries();

        File file = new File(filePath);
        FileWriter writer = new FileWriter(file);

        writeln(0, writer, "<?xml version=\'1.0\' encoding=\'utf-8\'?>");
        writeln(0, writer, "<nfd version=\""+nfd.getId()+"\">");

        writePoints(points, writer, false);
        writeParameters(parameters, writer);

        writeln(0, writer, "</nfd>");
        writer.close();

        return true;

    } catch(Exception ex) {
        ex.printStackTrace();
        return false;
    }
}


    private static void writeln(int n, FileWriter writer, String line) throws IOException {
        String ind = "";
        for(int i=0; i<n; ++i) {
            ind = ind + "    ";
        }
        writer.write(ind+line+"\n");
    }

    private static void writePoints(ArrayList<PointEntry> points, FileWriter writer, boolean withPointDescriptors) throws IOException {
        writeln(1, writer, "<points>");
        for (int i = 0; i < points.size(); i++) {
            writePoint(points.get(i), writer, withPointDescriptors);

        }
        writeln(1, writer, "</points>");
    }

    private static void writeParameters(ArrayList<ParameterEntry> parameters, FileWriter writer) throws IOException {
        writeln(1, writer, "<parameters>");
        for (int i = 0; i < parameters.size(); i++) {
            writeParameter(parameters.get(i), writer);

        }
        writeln(1, writer, "</parameters>");
    }

    private static void writePoint(PointEntry p, FileWriter writer, boolean withPointDescriptors) throws IOException {
        if(withPointDescriptors) {
            writeln(2, writer,
                    "<point " +
                    "id=\"" + p.getId() + "\" " +
                    "name=\"" + p.getName() + "\" " +
                    "description=\"" + p.getDescription() + "\"" +
                    ">");

            if(p.getPointDescriptor() != null)
                writePointDescriptor(p.getPointDescriptor(), writer);

            if(p.getSlicesDescriptor() != null)
                writeSlicesDescriptor(p.getSlicesDescriptor(), writer);
            
            writeln(2, writer,
                    "</point>");
        } else {
            writeln(2, writer,
                    "<point " +
                    "id=\"" + p.getId() + "\" " +
                    "name=\"" + p.getName() + "\" " +
                    "description=\"" + p.getDescription() + "\"" +
                    "></point>");
        }
    }

    private static void writeParameter(ParameterEntry p, FileWriter writer) throws IOException {
        writeln(2, writer,
                "<parameter " +
                "id=\"" + p.getId() + "\" " +
                "name=\"" + p.getName() + "\" " +
                "description=\"" + p.getDescription() + "\"" +
                ">");
        for (int i = 0; i < p.getDependanciesSize(); i++) {
                writeDependancy(p.getDependancy(i), writer);

        }
        writeln(2, writer, "</parameter>");

    }

    private static void writeDependancy(Entry p, FileWriter writer) throws IOException {
        if(p instanceof PointEntry) {
            writeln(3, writer,
                    "<point " +
                    "id=\"" + p.getId() + "\"" +
                    "/>");
        } else if (p instanceof ParameterEntry) {
            writeln(3, writer,
                    "<parameter " +
                    "id=\"" + p.getId() + "\"" +
                    "/>");
        }

    }

    private static void writePointDescriptor(PointDescriptor pd, FileWriter writer) throws IOException {
        int[] indices = pd.getIndices();
        float[] coords = pd.getWorldCoords();

        if(indices != null && indices.length == 3)
            writeln(3, writer,
                    "<indices " +
                    "i=\"" + indices[0] + "\" " +
                    "j=\"" + indices[1] + "\" " +
                    "k=\"" + indices[2] + "\"" +
                    "/>");
            writeln(3, writer,
                    "<coords " +
                    "x=\"" + String.format("%8e", coords[0]) + "\" " +
                    "y=\"" + String.format("%8e", coords[1]) + "\" " +
                    "z=\"" + String.format("%8e", coords[2]) + "\"" +
                    "/>");
    }

    private static void writeSlicesDescriptor(CustomSlicesDescriptor sd, FileWriter writer) throws IOException {
        float[] p0 = sd.getOriginPoint();
        float[][] vv = sd.getVectors();

        if(p0 != null && p0.length == 3 && vv != null && vv.length == 3 && vv[0] != null && vv[0].length == 3 && vv[1] != null && vv[1].length == 3 && vv[2] != null && vv[2].length == 3)
            writeln(3, writer,
                    "<slices_origin " +
                    "x=\"" + String.format("%8e", p0[0]) + "\" " +
                    "y=\"" + String.format("%8e", p0[1]) + "\" " +
                    "z=\"" + String.format("%8e", p0[2]) + "\"" +
                    "/>");
            writeln(3, writer,
                    "<slices_v0 " +
                    "x=\"" + String.format("%8e", vv[0][0]) + "\" " +
                    "y=\"" + String.format("%8e", vv[0][1]) + "\" " +
                    "z=\"" + String.format("%8e", vv[0][2]) + "\"" +
                    "/>");
            writeln(3, writer,
                    "<slices_v1 " +
                    "x=\"" + String.format("%8e", vv[1][0]) + "\" " +
                    "y=\"" + String.format("%8e", vv[1][1]) + "\" " +
                    "z=\"" + String.format("%8e", vv[1][2]) + "\"" +
                    "/>");
            writeln(3, writer,
                    "<slices_v2 " +
                    "x=\"" + String.format("%8e", vv[2][0]) + "\" " +
                    "y=\"" + String.format("%8e", vv[2][1]) + "\" " +
                    "z=\"" + String.format("%8e", vv[2][2]) + "\"" +
                    "/>");
    }


    private static void writeComment(String comment, FileWriter writer) throws IOException {
        if(comment == null)
            return;

        writeln(1, writer,
                    "<comment " +
                    "value=\"" + comment + "\"" +
                    "/>");

    }

   private NfdStructureWriter()
   {
   }



}
