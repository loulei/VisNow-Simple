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

package pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.ToolsActivityWizard;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.PointDescriptor;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.GeometryTools.*;


/**
 *
 * @author Bartosz Borucki (babor@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class TasStructureWriter {

public static boolean writeTas(RootEntry root, String filePath, String comment) {
    ArrayList<ToolEntry> tools = new ArrayList<ToolEntry>();
    ArrayList<CpointEntry> cpoints = new ArrayList<CpointEntry>();
    ArrayList<BranchEntry> branches = new ArrayList<BranchEntry>();

    try {
        if(filePath == null || root == null)
            return false;

        tools = root.getAllToolEntries();
        cpoints = root.getAllCpointEntries();
        branches = root.getAllBranchEntries();
        
        File file = new File(filePath);
        FileWriter writer = new FileWriter(file);

        writeln(0, writer, "<?xml version=\'1.0\' encoding=\'utf-8\'?>");
        writeln(0, writer, "<root version=\""+root.getId()+"\">");

        writeTools(tools, writer, true);
        writeCpoints(cpoints, writer);
        writeBranches(branches, writer);
        writeComment(comment, writer);

        writeln(0, writer, "</root>");
        writer.close();

        return true;
        
    } catch(Exception ex) {
        ex.printStackTrace();
        return false;
    }
}

public static boolean writeTasStructure(RootEntry root, String filePath) {
    ArrayList<ToolEntry> tools = new ArrayList<ToolEntry>();
    ArrayList<CpointEntry> cpoints = new ArrayList<CpointEntry>();
    ArrayList<BranchEntry> branches = new ArrayList<BranchEntry>();

    try {
        if(filePath == null || root == null)
            return false;

        tools = root.getAllToolEntries();
        cpoints = root.getAllCpointEntries();
        branches = root.getAllBranchEntries();

        File file = new File(filePath);
        FileWriter writer = new FileWriter(file);

        writeln(0, writer, "<?xml version=\'1.0\' encoding=\'utf-8\'?>");
        writeln(0, writer, "<tas version=\""+root.getId()+"\">");

        writeTools(tools, writer, false);
        writeCpoints(cpoints, writer);
        writeBranches(branches, writer);

        writeln(0, writer, "</tas>");
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

    private static void writeTools(ArrayList<ToolEntry> tools, FileWriter writer, boolean withPointDescriptors) throws IOException {
        writeln(1, writer, "<tools>");
        for (int i = 0; i < tools.size(); i++) {
            writeTool(tools.get(i), writer, withPointDescriptors);

        }
        writeln(1, writer, "</tools>");
    }

    private static void writeCpoints(ArrayList<CpointEntry> cpoints, FileWriter writer) throws IOException {
        writeln(1, writer, "<cpoints>");
        for (int i = 0; i < cpoints.size(); i++) {
            writeCpoint(cpoints.get(i), writer);

        }
        writeln(1, writer, "</cpoints>");
    }

    private static void writeBranches(ArrayList<BranchEntry> branches, FileWriter writer) throws IOException {
        writeln(1, writer, "<branches>");
        for (int i = 0; i < branches.size(); i++) {
            writeBranch(branches.get(i), writer);

        }
        writeln(1, writer, "</branches>");
    }

    private static void writeTool(ToolEntry t, FileWriter writer, boolean withPointDescriptors) throws IOException {
        String type = "";
        GeometryTool tool = t.getTool();
        if(tool instanceof AngleTool) {
            type = "TOOL_ANGLE";
        } else if(tool instanceof CenterPointTool) {
            type = "TOOL_CENTERPOINT";
        } else if(tool instanceof DiameterTool) {
            type = "TOOL_DIAMETER";
        } else if(tool instanceof LineTool) {
            type = "TOOL_LINE";
        } else if(tool instanceof PointTool) {
            type = "TOOL_POINT";
        } else if(tool instanceof PolygonTool) {
            type = "TOOL_POLYGON";
        } else if(tool instanceof PolylineTool) {
            type = "TOOL_POLYLINE";
        } else if(tool instanceof RadiusTool) {
            type = "TOOL_RADIUS";
        }

        if(withPointDescriptors) {
            writeln(2, writer,
                    "<tool " +
                    "type=\"" + type + "\" " +
                    "id=\"" + t.getId() + "\" " +
                    "name=\"" + t.getName() + "\" " +
                    "description=\"" + t.getDescription() + "\"" +
                    ">");

            ArrayList<PointDescriptor> pds = t.getPointDescriptors();
            for (int i = 0; i < pds.size(); i++) {
                writePointDescriptor(pds.get(i), writer);
            }
            
            writeln(2, writer,
                    "</tool>");
        } else {
            writeln(2, writer,
                    "<tool " +
                    "type=\"" + type + "\" " +
                    "id=\"" + t.getId() + "\" " +
                    "name=\"" + t.getName() + "\" " +
                    "description=\"" + t.getDescription() + "\"" +
                    "></tool>");
        }
    }

    private static void writeCpoint(CpointEntry c, FileWriter writer) throws IOException {
        writeln(2, writer,
                "<cpoint " +
                "type=\"" + c.getCalculablePoint().getType().toString() + "\" " +
                "id=\"" + c.getId() + "\" " +
                "name=\"" + c.getName() + "\" " +
                "description=\"" + c.getDescription() + "\"" +
                ">");
        for (int i = 0; i < c.getDependanciesSize(); i++) {
                writeDependancy(c.getDependancy(i), writer);
        }
        writeln(2, writer,
                "</cpoint>");
    }

    private static void writeBranch(BranchEntry b, FileWriter writer) throws IOException {
        if(b.getCalculable() == null) {
            writeln(2, writer,
                    "<branch " +
                    "id=\"" + b.getId() + "\" " +
                    "name=\"" + b.getName() + "\" " +
                    "description=\"" + b.getDescription() + "\"" +
                    ">");
        } else {
            if(b.getCalculable().getParameters() == null) {
                writeln(2, writer,
                        "<branch " +
                        "id=\"" + b.getId() + "\" " +
                        "name=\"" + b.getName() + "\" " +
                        "description=\"" + b.getDescription() + "\" " +
                        "calculable_type=\"" + b.getCalculable().getType().toString() + "\" " +
                        "calculable_shortcut=\"" + b.getCalculable().getShortcut() + "\" " +
                        "calculable_value=\"" + String.format("%8e", b.getCalculable().getValue()) + "\"" +
                        ">");
            } else {
                float[] p = b.getCalculable().getParameters();
                String ps = "";
                for (int i = 0; i < p.length; i++) {
                    ps += String.format("%8e", p[i]);
                    if(i < p.length-1)
                        ps += ":";
                }

                writeln(2, writer,
                        "<branch " +
                        "id=\"" + b.getId() + "\" " +
                        "name=\"" + b.getName() + "\" " +
                        "description=\"" + b.getDescription() + "\" " +
                        "calculable_type=\"" + b.getCalculable().getType().toString() + "\" " +
                        "calculable_shortcut=\"" + b.getCalculable().getShortcut() + "\" " +
                        "calculable_parameters=\"" + ps + "\" " +
                        "calculable_value=\"" + String.format("%8e", b.getCalculable().getValue()) + "\"" +
                        ">");

            }
        }

        for (int i = 0; i < b.getDependanciesSize(); i++) {
                writeDependancy(b.getDependancy(i), writer);
        }
        writeln(2, writer, "</branch>");

    }

    private static void writeDependancy(Entry p, FileWriter writer) throws IOException {
        if(p instanceof ToolEntry) {
            writeln(3, writer,
                    "<tool " +
                    "id=\"" + p.getId() + "\"" +
                    "/>");
        } else if (p instanceof CpointEntry) {
            writeln(3, writer,
                    "<cpoint " +
                    "id=\"" + p.getId() + "\"" +
                    "/>");
        } else if (p instanceof BranchEntry) {
            writeln(3, writer,
                    "<branch " +
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

    private static void writeComment(String comment, FileWriter writer) throws IOException {
        if(comment == null)
            return;

        writeln(1, writer,
                    "<comment " +
                    "value=\"" + comment + "\"" +
                    "/>");

    }

    public static boolean writeHrf(RootEntry root, String filePath) {
        ArrayList<BranchEntry> branches = new ArrayList<BranchEntry>();

        try {
            if(filePath == null || root == null)
                return false;

            branches = root.getAllBranchEntries();

            File file = new File(filePath);
            FileWriter writer = new FileWriter(file);
            writeHrfBranches(branches, writer);
            writer.close();

            return true;

        } catch(Exception ex) {
            ex.printStackTrace();
            return false;
        }

    }

    private static void writeHrfBranches(ArrayList<BranchEntry> branches, FileWriter writer) throws IOException {
        for (int i = 0; i < branches.size(); i++) {
            writeHrfBranch(branches.get(i), writer);
        }
    }

    private static void writeHrfBranch(BranchEntry b, FileWriter writer) throws IOException {
        if(b.getCalculable() != null) {
            writeln(0, writer,
                    b.getCalculable().getShortcut() +"\t" +
                    String.format("%f", b.getCalculable().getValue()) + "\t"
                    );
        }
    }

   private TasStructureWriter()
   {
   }


}
