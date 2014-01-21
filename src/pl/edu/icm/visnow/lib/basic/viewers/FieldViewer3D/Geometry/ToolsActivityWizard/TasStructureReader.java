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
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Node;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.CalculableParamsPool;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.CalculablePointsPool;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.PointDescriptor;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.GeometryTools.*;


/**
 *
 * @author Bartosz Borucki (babor@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class TasStructureReader {

    public static RootEntry readTas(String filePath) {
        return readTas(filePath, false, false);
    }

    public static RootEntry readTasStructure(String filePath) {
        return readTas(filePath, true, false);
    }


    public static RootEntry readTas(String filePath, boolean useCoords) {
        return readTas(filePath, false, useCoords);
    }

    public static RootEntry readTasStructure(String filePath, boolean useCoords) {
        return readTas(filePath, true, useCoords);
    }


    private static RootEntry readTas(String filePath, boolean structureOnly, boolean useCoords) {
        ArrayList<ToolEntry> tools = new ArrayList<ToolEntry>();
        ArrayList<BranchEntry> branches = new ArrayList<BranchEntry>();
        ArrayList<CpointEntry> cpoints = new ArrayList<CpointEntry>();

        try {
            if(filePath == null)
                return null;

            File file = new File(filePath);
            if(!file.exists())
                return null;

            Node node = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file).getDocumentElement();

            Node toolsNode = null;
            Node branchesNode = null;
            Node cpointsNode = null;
            Node toolNode, branchNode, cpointNode;

            RootEntry root = null;

            if(node.getNodeName().equals("root")) {
                //System.out.println("read root node:");
                String version = "v?";
                if(node.getAttributes().getNamedItem("version") != null) {
                    version = node.getAttributes().getNamedItem("version").getNodeValue();
                }

                root = new RootEntry(version, "Tools Activity Structure", "");

                for(int i = 0; i < node.getChildNodes().getLength(); i++) {
                    String nodeName = node.getChildNodes().item(i).getNodeName();

                    //----------------reading tool usages---------------------------
                    if(nodeName.equalsIgnoreCase("tools")) {
                        toolsNode = node.getChildNodes().item(i);
                        //System.out.println("read tools: ");
                        for (int j = 0; j < toolsNode.getChildNodes().getLength(); j++) {
                            toolNode = toolsNode.getChildNodes().item(j);
                            if(!toolNode.getNodeName().equals("tool"))
                                continue;

                            String toolId = "";
                            String toolType = "";
                            String toolName = "";
                            String toolDescription = "";
                            ArrayList<PointDescriptor> pds = new ArrayList<PointDescriptor>();
                            ToolEntry te = null;
                            Node indicesNode = null;
                            Node coordsNode = null;
                            if(toolNode.getAttributes().getNamedItem("id") != null)
                                toolId = toolNode.getAttributes().getNamedItem("id").getNodeValue();
                            if(toolNode.getAttributes().getNamedItem("type") != null)
                                toolType = toolNode.getAttributes().getNamedItem("type").getNodeValue();
                            if(toolNode.getAttributes().getNamedItem("name") != null)
                                toolName = toolNode.getAttributes().getNamedItem("name").getNodeValue();
                            if(toolNode.getAttributes().getNamedItem("description") != null)
                                toolDescription = toolNode.getAttributes().getNamedItem("description").getNodeValue();

                            if(!structureOnly) {
                                int count = 0;
                                for (int k = 0; k < toolNode.getChildNodes().getLength(); k++) {

                                    if(useCoords) {
                                        if(toolNode.getChildNodes().item(k).getNodeName().equals("coords")) {
                                            coordsNode = toolNode.getChildNodes().item(k);
                                        }
                                        if(coordsNode != null) {
                                            if(coordsNode.getAttributes().getNamedItem("x") != null &&
                                               coordsNode.getAttributes().getNamedItem("y") != null &&
                                               coordsNode.getAttributes().getNamedItem("z") != null ) {
                                                float[] coords = new float[3];
                                                int[] indices = {0,0,0};
                                                String tmp;
                                                try {
                                                    tmp = coordsNode.getAttributes().getNamedItem("x").getNodeValue();
                                                    coords[0] = Float.parseFloat(tmp);
                                                    tmp = coordsNode.getAttributes().getNamedItem("y").getNodeValue();
                                                    coords[1] = Float.parseFloat(tmp);
                                                    tmp = coordsNode.getAttributes().getNamedItem("z").getNodeValue();
                                                    coords[2] = Float.parseFloat(tmp);
                                                    pds.add(new PointDescriptor(toolId+"_"+(count++), indices, coords));
                                                } catch (NumberFormatException ex) {
                                                };
                                            }
                                        }
                                    } else {
                                        if(toolNode.getChildNodes().item(k).getNodeName().equals("indices")) {
                                            indicesNode = toolNode.getChildNodes().item(k);
                                        }
                                        if(indicesNode != null) {
                                            if(indicesNode.getAttributes().getNamedItem("i") != null &&
                                               indicesNode.getAttributes().getNamedItem("j") != null &&
                                               indicesNode.getAttributes().getNamedItem("k") != null ) {
                                                int[] indices = new int[3];
                                                String tmp;
                                                try {
                                                    tmp = indicesNode.getAttributes().getNamedItem("i").getNodeValue();
                                                    indices[0] = Integer.parseInt(tmp);
                                                    tmp = indicesNode.getAttributes().getNamedItem("j").getNodeValue();
                                                    indices[1] = Integer.parseInt(tmp);
                                                    tmp = indicesNode.getAttributes().getNamedItem("k").getNodeValue();
                                                    indices[2] = Integer.parseInt(tmp);
                                                    pds.add(new PointDescriptor(toolId, indices, null));
                                                } catch (NumberFormatException ex) {};
                                            }
                                        }

                                    }
                                    indicesNode = null;
                                    coordsNode = null;
                                }
                            }

                            te = new ToolEntry(toolId, toolName, toolDescription);
                            GeometryTool tool = null;
                            if(toolType.equals("TOOL_ANGLE")) {
                                tool = new AngleTool();
                            } else if(toolType.equals("TOOL_CENTERPOINT")) {
                                tool = new CenterPointTool();
                            } else if(toolType.equals("TOOL_DIAMETER")) {
                                tool = new DiameterTool();
                            } else if(toolType.equals("TOOL_LINE")) {
                                tool = new LineTool();
                            } else if(toolType.equals("TOOL_POINT")) {
                                tool = new PointTool();
                            } else if(toolType.equals("TOOL_POLYGON")) {
                                tool = new PolygonTool();
                            } else if(toolType.equals("TOOL_POLYLINE")) {
                                tool = new PolylineTool();
                            } else if(toolType.equals("TOOL_RADIUS")) {
                                tool = new RadiusTool();
                            }
                            te.setTool(tool);
                            if(pds != null)
                                te.setPointDescriptors(pds);

                            tools.add(te);
                        }
                    }

                    //----------------reading cpoints-------------------------------
                    if(nodeName.equalsIgnoreCase("cpoints")) {
                        cpointsNode = node.getChildNodes().item(i);
                        for (int j = 0; j < cpointsNode.getChildNodes().getLength(); j++) {
                            cpointNode = cpointsNode.getChildNodes().item(j);
                            if(!cpointNode.getNodeName().equals("cpoint"))
                                continue;

                            String cpointId = "";
                            String cpointType = "";
                            String cpointName = "";
                            String cpointDescription = "";
                            if(cpointNode.getAttributes().getNamedItem("id") != null)
                                cpointId = cpointNode.getAttributes().getNamedItem("id").getNodeValue();
                            if(cpointNode.getAttributes().getNamedItem("type") != null)
                                cpointType = cpointNode.getAttributes().getNamedItem("type").getNodeValue();
                            if(cpointNode.getAttributes().getNamedItem("name") != null)
                                cpointName = cpointNode.getAttributes().getNamedItem("name").getNodeValue();
                            if(cpointNode.getAttributes().getNamedItem("description") != null)
                                cpointDescription = cpointNode.getAttributes().getNamedItem("description").getNodeValue();

                            CpointEntry cpoint = new CpointEntry(cpointId, cpointName, cpointDescription);
                            cpoint.addCalculablePoint(CalculablePointsPool.getTypeByString(cpointType));
                            cpoints.add(cpoint);
                        }

                        for (int j = 0; j < cpointsNode.getChildNodes().getLength(); j++) {
                            cpointNode = cpointsNode.getChildNodes().item(j);
                            if(!cpointNode.getNodeName().equals("cpoint"))
                                continue;

                            String cpointId = "";
                            if(cpointNode.getAttributes().getNamedItem("id") != null) {
                                   cpointId = cpointNode.getAttributes().getNamedItem("id").getNodeValue();

                                   CpointEntry cpe = getCalculablePointById(cpoints, cpointId);
                                   String id;
                                   if(cpe != null) {
                                        for (int k = 0; k < cpointNode.getChildNodes().getLength(); k++) {
                                            if(cpointNode.getChildNodes().item(k).getNodeName().equals("tool")) {
                                                id = cpointNode.getChildNodes().item(k).getAttributes().getNamedItem("id").getNodeValue();
                                                ToolEntry t = getToolById(tools, id);
                                                if(t != null) {
                                                    cpe.addDependancy(t);
                                                }
                                            }
                                        }
                                    }
                            }
                        }

                    }


                    //----------------reading branches-------------------------------
                    if(nodeName.equalsIgnoreCase("branches")) {
                        branchesNode = node.getChildNodes().item(i);
                        //System.out.println("read branches: ");
                        for (int j = 0; j < branchesNode.getChildNodes().getLength(); j++) {
                            branchNode = branchesNode.getChildNodes().item(j);
                            if(!branchNode.getNodeName().equals("branch"))
                                continue;

                            String branchId = "";
                            String branchName = "";
                            String branchDescription = "";
                            String calculableType = null;
                            String calculableShortcut = "";
                            String calculableParameters = "";
                            if(branchNode.getAttributes().getNamedItem("id") != null)
                                branchId = branchNode.getAttributes().getNamedItem("id").getNodeValue();
                            if(branchNode.getAttributes().getNamedItem("name") != null)
                                branchName = branchNode.getAttributes().getNamedItem("name").getNodeValue();
                            if(branchNode.getAttributes().getNamedItem("description") != null)
                                branchDescription = branchNode.getAttributes().getNamedItem("description").getNodeValue();
                            if(branchNode.getAttributes().getNamedItem("calculable_type") != null)
                                calculableType = branchNode.getAttributes().getNamedItem("calculable_type").getNodeValue();
                            if(branchNode.getAttributes().getNamedItem("calculable_shortcut") != null)
                                calculableShortcut = branchNode.getAttributes().getNamedItem("calculable_shortcut").getNodeValue();
                            if(branchNode.getAttributes().getNamedItem("calculable_parameters") != null)
                                calculableParameters = branchNode.getAttributes().getNamedItem("calculable_parameters").getNodeValue();

                            float[] p = null;
                            if(calculableParameters != null && calculableParameters.length() > 0) {
                                String[] ps = calculableParameters.split(":");
                                if(ps != null && ps.length > 0) {
                                    p = new float[ps.length];
                                    try {
                                        for (int k = 0; k < p.length; k++) {
                                            p[k] = Float.parseFloat(ps[k]);
                                        }
                                    } catch(NumberFormatException ex) {
                                        p = null;
                                    }
                                } else {
                                    p = null;
                                }
                            }

                            BranchEntry b = new BranchEntry(branchId, branchName, branchDescription);
                            if(calculableType != null) {
                                b.addCalculable(CalculableParamsPool.getTypeByString(calculableType), calculableShortcut, p);
                            }
                            branches.add(b);
                            //System.out.println(" - branch id="+branchId+" name="+branchName+" description="+branchDescription);
                        }

                        //System.out.println("read dependancies: ");
                        for (int j = 0; j < branchesNode.getChildNodes().getLength(); j++) {
                            branchNode = branchesNode.getChildNodes().item(j);
                            if(!branchNode.getNodeName().equals("branch"))
                                continue;

                            String branchId = "";
                            if(branchNode.getAttributes().getNamedItem("id") != null) {
                                   branchId = branchNode.getAttributes().getNamedItem("id").getNodeValue();

                                   BranchEntry branch = getBranchById(branches, branchId);
                                   String id;
                                   if(branch != null) {
                                        //System.out.println("branch "+branchId+":");
                                        for (int k = 0; k < branchNode.getChildNodes().getLength(); k++) {
                                            if(branchNode.getChildNodes().item(k).getNodeName().equals("tool")) {
                                                id = branchNode.getChildNodes().item(k).getAttributes().getNamedItem("id").getNodeValue();
                                                //System.out.println("    - depends on point "+id);
                                                ToolEntry t = getToolById(tools, id);
                                                if(t != null) {
                                                    branch.addDependancy(t);
                                                }
                                            }

                                            if(branchNode.getChildNodes().item(k).getNodeName().equals("cpoint")) {
                                                id = branchNode.getChildNodes().item(k).getAttributes().getNamedItem("id").getNodeValue();
                                                //System.out.println("    - depends on point "+id);
                                                CpointEntry cpe = getCalculablePointById(cpoints, id);
                                                if(cpe != null) {
                                                    branch.addDependancy(cpe);
                                                }
                                            }

                                            if(branchNode.getChildNodes().item(k).getNodeName().equals("branch")) {
                                                id = branchNode.getChildNodes().item(k).getAttributes().getNamedItem("id").getNodeValue();
                                                //System.out.println("    - depends on branch "+id);
                                                BranchEntry pa = getBranchById(branches, id);
                                                if(pa != null)
                                                    branch.addDependancy(pa);
                                            }
                                        }
                                       ArrayList<ToolEntry> tes = branch.getAllToolEntries();
                                       ArrayList<PointDescriptor> pds = new ArrayList<PointDescriptor>();
                                       for (int k = 0; k < tes.size(); k++) {
                                           pds.addAll(tes.get(k).getPointDescriptors());
                                       }
                                       if(branch.getCalculable() != null) {
                                           branch.getCalculable().setPointDescriptors(pds);
                                       }
                                    }
                            }
                        }
                    }

                    //----------------reading comment-------------------------------
                    if(nodeName.equalsIgnoreCase("comment")) {
                        String comment = new String("");
                        if(node.getChildNodes().item(i).getAttributes().getNamedItem("value") != null) {
                            comment = node.getChildNodes().item(i).getAttributes().getNamedItem("value").getNodeValue();
                        }
                        root.setComment(comment);
                    }

                }

                for (int i = 0; i < branches.size(); i++) {
                    root.addBranch(branches.get(i));
                }

            }
            return root;
        } catch(Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }


    private static BranchEntry getBranchById(ArrayList<BranchEntry> branches, String id) {
        for (int i = 0; i < branches.size(); i++) {
            if(branches.get(i).getId().equals(id))
                return branches.get(i);
        }
        return null;
    }

    private static ToolEntry getToolById(ArrayList<ToolEntry> tools, String id) {
        for (int i = 0; i < tools.size(); i++) {
            if(tools.get(i).getId().equals(id))
                return tools.get(i);
        }
        return null;
    }

    private static CpointEntry getCalculablePointById(ArrayList<CpointEntry> cpoints, String id) {
        for (int i = 0; i < cpoints.size(); i++) {
            if(cpoints.get(i).getId().equals(id))
                return cpoints.get(i);
        }
        return null;
    }

   private TasStructureReader()
   {
   }

}
