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
import java.util.Vector;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Node;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.CalculableParamsPool.CalculableType;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry.PointDescriptor;


/**
 *
 * @author Bartosz Borucki (babor@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class NfdStructureReader {

    public static NfdEntry readNfd(String filePath) {
        return readNfd(filePath, false, false);
    }

    public static NfdEntry readNfdStructure(String filePath) {
        return readNfd(filePath, true, false);
    }


    public static NfdEntry readNfd(String filePath, boolean useCoords) {
        return readNfd(filePath, false, useCoords);
    }

    public static NfdEntry readNfdStructure(String filePath, boolean useCoords) {
        return readNfd(filePath, true, useCoords);
    }


    private static NfdEntry readNfd(String filePath, boolean structureOnly, boolean useCoords) {
        Vector<PointEntry> points = new Vector<PointEntry>();
        Vector<ParameterEntry> parameters = new Vector<ParameterEntry>();

        try {
            if(filePath == null)
                return null;

            File file = new File(filePath);
            if(!file.exists())
                return null;

            Node node = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file).getDocumentElement();

            Node pointsNode = null;
            Node parametersNode = null;
            Node pointNode, paramNode;

            NfdEntry nfd = null;

            if(node.getNodeName().equals("nfd")) {
                //System.out.println("read nfd node:");
                String version = "v?";
                if(node.getAttributes().getNamedItem("version") != null) {
                    version = node.getAttributes().getNamedItem("version").getNodeValue();
                }

                nfd = new NfdEntry(version, "Numerical Foot Description", "");

                for(int i = 0; i < node.getChildNodes().getLength(); i++) {
                    String nodeName = node.getChildNodes().item(i).getNodeName();

                    if(nodeName.equalsIgnoreCase("points")) {
                        pointsNode = node.getChildNodes().item(i);
                        //System.out.println("read points: ");
                        for (int j = 0; j < pointsNode.getChildNodes().getLength(); j++) {
                            pointNode = pointsNode.getChildNodes().item(j);
                            if(!pointNode.getNodeName().equals("point"))
                                continue;

                            String pointId = "";
                            String pointName = "";
                            String pointDescription = "";
                            PointDescriptor pd = null;
                            PointEntry pe = null;
                            Node indicesNode = null;
                            Node coordsNode = null;
                            if(pointNode.getAttributes().getNamedItem("id") != null)
                                pointId = pointNode.getAttributes().getNamedItem("id").getNodeValue();
                            if(pointNode.getAttributes().getNamedItem("name") != null)
                                pointName = pointNode.getAttributes().getNamedItem("name").getNodeValue();
                            if(pointNode.getAttributes().getNamedItem("description") != null)
                                pointDescription = pointNode.getAttributes().getNamedItem("description").getNodeValue();

                            if(!structureOnly) {
                                for (int k = 0; k < pointNode.getChildNodes().getLength(); k++) {
                                    if(pointNode.getChildNodes().item(k).getNodeName().equals("indices")) {
                                        indicesNode = pointNode.getChildNodes().item(k);
                                        break;
                                    }
                                }

                                if(useCoords) {
                                    for (int k = 0; k < pointNode.getChildNodes().getLength(); k++) {
                                        if(pointNode.getChildNodes().item(k).getNodeName().equals("coords")) {
                                            coordsNode = pointNode.getChildNodes().item(k);
                                            break;
                                        }
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

                                                pd = new PointDescriptor(pointId, indices, coords);

                                            } catch (NumberFormatException ex) {};

                                        }
                                    }
                                } else {
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

                                                //OSZUSTWO!!!
                                                //indices[2] = 281 - indices[2];

                                                pd = new PointDescriptor(pointId, indices, null);

                                            } catch (NumberFormatException ex) {};

                                        }
                                    }
                                }
                            }

                            pe = new PointEntry(pointId, pointName, pointDescription);
                            if(pd != null)
                                pe.setPointDescriptor(pd);

                            points.add(pe);
                            //System.out.println(" - point id="+pointId+" name="+pointName+" description="+pointDescription);
                        }
                    }

                    if(nodeName.equalsIgnoreCase("parameters")) {
                        parametersNode = node.getChildNodes().item(i);
                        //System.out.println("read parameters: ");
                        for (int j = 0; j < parametersNode.getChildNodes().getLength(); j++) {
                            paramNode = parametersNode.getChildNodes().item(j);
                            if(!paramNode.getNodeName().equals("parameter"))
                                continue;

                            String paramId = "";
                            String paramName = "";
                            String paramDescription = "";
                            String calculableType = null;
                            if(paramNode.getAttributes().getNamedItem("id") != null)
                                paramId = paramNode.getAttributes().getNamedItem("id").getNodeValue();
                            if(paramNode.getAttributes().getNamedItem("name") != null)
                                paramName = paramNode.getAttributes().getNamedItem("name").getNodeValue();
                            if(paramNode.getAttributes().getNamedItem("description") != null)
                                paramDescription = paramNode.getAttributes().getNamedItem("description").getNodeValue();
                            parameters.add(new ParameterEntry(paramId, paramName, paramDescription));
                            //System.out.println(" - parameter id="+paramId+" name="+paramName+" description="+paramDescription);
                        }

                        //System.out.println("read dependancies: ");
                        for (int j = 0; j < parametersNode.getChildNodes().getLength(); j++) {
                            paramNode = parametersNode.getChildNodes().item(j);
                            if(!paramNode.getNodeName().equals("parameter"))
                                continue;

                            String paramId = "";
                            if(paramNode.getAttributes().getNamedItem("id") != null) {
                                   paramId = paramNode.getAttributes().getNamedItem("id").getNodeValue();

                                   ParameterEntry parameter = getParamById(parameters, paramId);
                                   String id;
                                   if(parameter != null) {
                                        //System.out.println("parameter "+paramId+":");
                                        for (int k = 0; k < paramNode.getChildNodes().getLength(); k++) {
                                            if(paramNode.getChildNodes().item(k).getNodeName().equals("point")) {
                                                id = paramNode.getChildNodes().item(k).getAttributes().getNamedItem("id").getNodeValue();
                                                //System.out.println("    - depends on point "+id);
                                                PointEntry p = getPointById(points, id);
                                                if(p != null)
                                                    parameter.addDependancy(p);
                                            }

                                            if(paramNode.getChildNodes().item(k).getNodeName().equals("parameter")) {
                                                id = paramNode.getChildNodes().item(k).getAttributes().getNamedItem("id").getNodeValue();
                                                //System.out.println("    - depends on parameter "+id);
                                                ParameterEntry pa = getParamById(parameters, id);
                                                if(pa != null)
                                                    parameter.addDependancy(pa);
                                            }

                                            if(paramNode.getChildNodes().item(k).getNodeName().equals("calculable")) {
                                                String type = paramNode.getChildNodes().item(k).getAttributes().getNamedItem("type").getNodeValue();
                                                if(type != null) {
                                                    parameter.addCalculable(CalculableType.valueOf(type.toUpperCase()));
                                                }
                                            }

                                        }
                                    }
                            }
                        }
                    }

                    if(nodeName.equalsIgnoreCase("comment")) {
                        String comment = new String("");
                        if(node.getChildNodes().item(i).getAttributes().getNamedItem("value") != null) {
                            comment = node.getChildNodes().item(i).getAttributes().getNamedItem("value").getNodeValue();
                        }
                        nfd.setComment(comment);
                    }

                }

                for (int i = 0; i < parameters.size(); i++) {
                    nfd.addParameter(parameters.get(i));
                }

            }
            return nfd;
        } catch(Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }


    private static ParameterEntry getParamById(Vector<ParameterEntry> parameters, String id) {
        for (int i = 0; i < parameters.size(); i++) {
            if(parameters.get(i).getId().equals(id))
                return parameters.get(i);
        }
        return null;
    }

    private static PointEntry getPointById(Vector<PointEntry> points, String id) {
        for (int i = 0; i < points.size(); i++) {
            if(points.get(i).getId().equals(id))
                return points.get(i);
        }
        return null;
    }

   private NfdStructureReader()
   {
   }

}
