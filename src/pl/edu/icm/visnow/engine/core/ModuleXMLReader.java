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

package pl.edu.icm.visnow.engine.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import pl.edu.icm.visnow.lib.types.VNDataAcceptor;
import pl.edu.icm.visnow.lib.types.VNDataSchema;
import pl.edu.icm.visnow.lib.types.VNDataSchemaComparator;

/**
 *
 * @author Bartosz Borucki (babor@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class ModuleXMLReader {
    
    public static InputEgg[] getInputEggsFromModuleXML(String packageName) throws URISyntaxException, ParserConfigurationException, SAXException, IOException, ClassNotFoundException {
        if (packageName == null) {
            return null;
        }

        InputStream is = ModuleXMLReader.class.getResourceAsStream("/" + packageName.replace(".", "/") + "/module.xml");
        if (is == null) {
            return null;
        }

        InputEgg[] out = getInputEggsFromStream(packageName, is);
        is.close();
        
        return out;
    }
    
    public static InputEgg[] getInputEggsFromStream(String packageName, InputStream is) throws URISyntaxException, ParserConfigurationException, SAXException, IOException, ClassNotFoundException {
        if (packageName == null || is == null) {
            return null;
        }

        Node node = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is).getDocumentElement();
        InputEgg[] inputEggs = null;
        Node inputsNode = null;

        if (node.getNodeName().equals("module")) {
            ArrayList<InputEgg> inputEggList = new ArrayList<InputEgg>();

            for (int i = 0; i < node.getChildNodes().getLength(); i++) {
                String nodeName = node.getChildNodes().item(i).getNodeName();

                if (nodeName.equalsIgnoreCase("inputs")) {
                    inputsNode = node.getChildNodes().item(i);
                    Node inputNode;
                    for (int j = 0; j < inputsNode.getChildNodes().getLength(); j++) {
                        inputNode = inputsNode.getChildNodes().item(j);
                        if (!inputNode.getNodeName().equals("input")) {
                            continue;
                        }

                        String inputName = null;
                        String inputType = null;
                        String inputModifiers = null;
                        String minConnections = null;
                        String maxConnections = null;
                        String inputDescription = null;

                        if (inputNode.getAttributes().getNamedItem("name") != null) {
                            inputName = inputNode.getAttributes().getNamedItem("name").getNodeValue();
                        }
                        if (inputNode.getAttributes().getNamedItem("type") != null) {
                            inputType = inputNode.getAttributes().getNamedItem("type").getNodeValue();
                        }
                        if (inputNode.getAttributes().getNamedItem("modifiers") != null) {
                            inputModifiers = inputNode.getAttributes().getNamedItem("modifiers").getNodeValue();
                        }
                        if (inputNode.getAttributes().getNamedItem("minConnections") != null) {
                            minConnections = inputNode.getAttributes().getNamedItem("minConnections").getNodeValue();
                        }
                        if (inputNode.getAttributes().getNamedItem("maxConnections") != null) {
                            maxConnections = inputNode.getAttributes().getNamedItem("maxConnections").getNodeValue();
                        }

                        if (inputName != null && inputType != null) {
                            Class typeClass = ModuleXMLReader.class.getClassLoader().loadClass(inputType);
                            int modifiers = InputEgg.NORMAL;
                            if(inputModifiers != null) {
                                String[] mods = inputModifiers.split(":");
                                for (int k = 0; k < mods.length; k++) {
                                    if (mods[k].equals("TRIGGERING")) {
                                        modifiers = modifiers | InputEgg.TRIGGERING;
                                        continue;
                                    }

                                    if (mods[k].equals("HIDDEN")) {
                                        modifiers = modifiers | InputEgg.HIDDEN;
                                        continue;
                                    }

                                    if (mods[k].equals("NECESSARY")) {
                                        modifiers = modifiers | InputEgg.NECESSARY;
                                        continue;
                                    }
                                }
                            }
                            
                            int minC = 0;
                            if (minConnections != null) {
                                try {
                                    minC = Integer.parseInt(minConnections);
                                } catch (NumberFormatException ex) {
                                    minC = 0;
                                }
                            }

                            int maxC = 1;
                            if (maxConnections != null) {
                                try {
                                    maxC = Integer.parseInt(maxConnections);
                                } catch (NumberFormatException ex) {
                                    maxC = 1;
                                }
                            }

                            //acceptors
                            VNDataAcceptor[] acceptors = null;
                            ArrayList<VNDataAcceptor> acceptorList = new ArrayList<VNDataAcceptor>();

                            //parse acceptors
                            Node acceptorNode;
                            for (int k = 0; k < inputNode.getChildNodes().getLength(); k++) {
                                acceptorNode = inputNode.getChildNodes().item(k);
                                if (!acceptorNode.getNodeName().equals("acceptor")) {
                                    continue;
                                }

                                Node paramNode;
                                ArrayList<String[]> params = new ArrayList<String[]>();
                                for (int l = 0; l < acceptorNode.getChildNodes().getLength(); l++) {
                                    paramNode = acceptorNode.getChildNodes().item(l);
                                    if (!paramNode.getNodeName().equals("param")) {
                                        continue;
                                    }

                                    String[] paramStr = new String[2];
                                    if (paramNode.getAttributes().getNamedItem("name") != null) {
                                        paramStr[0] = paramNode.getAttributes().getNamedItem("name").getNodeValue();
                                    }
                                    if (paramNode.getAttributes().getNamedItem("value") != null) {
                                        paramStr[1] = paramNode.getAttributes().getNamedItem("value").getNodeValue();
                                    }
                                    if (paramStr[0] == null || paramStr[0].length() == 0 || paramStr[1] == null || paramStr[1].length() == 0) {
                                        continue;
                                    }

                                    params.add(paramStr);
                                }


                                //create schema and comparator
                                VNDataSchema schm = new VNDataSchema(params);
                                long comp = VNDataSchemaComparator.createComparatorFromSchemaParams(params);
                                acceptorList.add(new VNDataAcceptor(schm, comp));
                            }

                            if (acceptorList.size() > 0) {
                                acceptors = new VNDataAcceptor[acceptorList.size()];
                                for (int k = 0; k < acceptors.length; k++) {
                                    acceptors[k] = acceptorList.get(k);
                                }
                            }


                            //parse description
                            Node descNode;
                            for (int k = 0; k < inputNode.getChildNodes().getLength(); k++) {
                                descNode = inputNode.getChildNodes().item(k);
                                if (!descNode.getNodeName().equals("description")) {
                                    continue;
                                }


                                if (descNode.getAttributes().getNamedItem("value") != null) {
                                    inputDescription = descNode.getAttributes().getNamedItem("value").getNodeValue();
                                }
                            }


                            inputEggList.add(new InputEgg(inputName, typeClass, modifiers, minC, maxC, inputDescription, acceptors));
                        } else {
                            System.err.println("illegal input entry in package: " + packageName);
                        }
                    }
                }
            }

            inputEggs = new InputEgg[inputEggList.size()];
            for (int i = 0; i < inputEggs.length; i++) {
                inputEggs[i] = inputEggList.get(i);
            }
        }
        return inputEggs;
    }

    public static OutputEgg[] getOutputEggsFromModuleXML(String packageName) throws URISyntaxException, ParserConfigurationException, SAXException, IOException, ClassNotFoundException {
        if (packageName == null) {
            return null;
        }

        InputStream is = ModuleXMLReader.class.getResourceAsStream("/" + packageName.replace(".", "/") + "/module.xml");
        if (is == null) {
            return null;
        }
        
        OutputEgg[] out = getOutputEggsFromStream(packageName, is);
        is.close();
        
        return out;
    }
        
    public static OutputEgg[] getOutputEggsFromStream(String packageName, InputStream is) throws URISyntaxException, ParserConfigurationException, SAXException, IOException, ClassNotFoundException {
        if (packageName == null || is == null) {
            return null;
        }
        
        Node node = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is).getDocumentElement();
        OutputEgg[] outputEggs = null;
        Node outputsNode = null;

        if (node.getNodeName().equals("module")) {
            String moduleClass = "";
            if (node.getAttributes().getNamedItem("class") != null) {
                moduleClass = node.getAttributes().getNamedItem("class").getNodeValue();
            }

            ArrayList<OutputEgg> outputEggList = new ArrayList<OutputEgg>();

            for (int i = 0; i < node.getChildNodes().getLength(); i++) {
                String nodeName = node.getChildNodes().item(i).getNodeName();

                if (nodeName.equalsIgnoreCase("outputs")) {
                    outputsNode = node.getChildNodes().item(i);
                    Node outputNode;
                    for (int j = 0; j < outputsNode.getChildNodes().getLength(); j++) {
                        outputNode = outputsNode.getChildNodes().item(j);

                        if (outputNode.getNodeName().equals("output")) {

                            String outputName = null;
                            String outputType = null;
                            String outputDescription = null;
                            String maxConnections = null;
                            
                            if (outputNode.getAttributes().getNamedItem("name") != null) {
                                outputName = outputNode.getAttributes().getNamedItem("name").getNodeValue();
                            }
                            if (outputNode.getAttributes().getNamedItem("type") != null) {
                                outputType = outputNode.getAttributes().getNamedItem("type").getNodeValue();
                            }
                            if (outputNode.getAttributes().getNamedItem("maxConnections") != null) {
                                maxConnections = outputNode.getAttributes().getNamedItem("maxConnections").getNodeValue();
                            }

                            int maxC = -1;
                            if (maxConnections != null) {
                                try {
                                    maxC = Integer.parseInt(maxConnections);
                                } catch (NumberFormatException ex) {
                                    maxC = -1;
                                }
                            }
                            
                            //output schemas
                            VNDataSchema[] schemas = null;
                            ArrayList<VNDataSchema> schemasList = new ArrayList<VNDataSchema>();

                            //parse schemas
                            Node schemaNode;
                            for (int k = 0; k < outputNode.getChildNodes().getLength(); k++) {
                                schemaNode = outputNode.getChildNodes().item(k);
                                if (!schemaNode.getNodeName().equals("schema")) {
                                    continue;
                                }

                                Node paramNode;
                                ArrayList<String[]> params = new ArrayList<String[]>();
                                for (int l = 0; l < schemaNode.getChildNodes().getLength(); l++) {
                                    paramNode = schemaNode.getChildNodes().item(l);
                                    if (!paramNode.getNodeName().equals("param")) {
                                        continue;
                                    }

                                    String[] paramStr = new String[2];
                                    if (paramNode.getAttributes().getNamedItem("name") != null) {
                                        paramStr[0] = paramNode.getAttributes().getNamedItem("name").getNodeValue();
                                    }
                                    if (paramNode.getAttributes().getNamedItem("value") != null) {
                                        paramStr[1] = paramNode.getAttributes().getNamedItem("value").getNodeValue();
                                    }
                                    if (paramStr[0] == null || paramStr[0].length() == 0 || paramStr[1] == null || paramStr[1].length() == 0) {
                                        continue;
                                    }

                                    params.add(paramStr);
                                }


                                //create schema
                                schemasList.add(new VNDataSchema(params));
                            }

                            if (schemasList.size() > 0) {
                                schemas = new VNDataSchema[schemasList.size()];
                                for (int k = 0; k < schemas.length; k++) {
                                    schemas[k] = schemasList.get(k);
                                }
                            }

                            //parse description
                            Node descNode;
                            for (int k = 0; k < outputNode.getChildNodes().getLength(); k++) {
                                descNode = outputNode.getChildNodes().item(k);
                                if (!descNode.getNodeName().equals("description")) {
                                    continue;
                                }


                                if (descNode.getAttributes().getNamedItem("value") != null) {
                                    outputDescription = descNode.getAttributes().getNamedItem("value").getNodeValue();
                                }
                            }



                            if (outputName != null && outputType != null) {
                                //Class typeClass = Class.forName(outputType).getClass();
                                Class typeClass = ModuleXMLReader.class.getClassLoader().loadClass(outputType);
                                outputEggList.add(new OutputEgg(outputName, typeClass, maxC, outputDescription, schemas));
                            } else {
                                System.err.println("illegal output entry in package: " + packageName);
                            }
                        } else if (outputNode.getNodeName().equals("geometryOutput")) {
                            //parse description
                            Node descNode;
                            String goDesc = null;
                            for (int k = 0; k < outputNode.getChildNodes().getLength(); k++) {
                                descNode = outputNode.getChildNodes().item(k);
                                if (!descNode.getNodeName().equals("description")) {
                                    continue;
                                }


                                if (descNode.getAttributes().getNamedItem("value") != null) {
                                    goDesc = descNode.getAttributes().getNamedItem("value").getNodeValue();
                                }
                            }

                            Class mc = null;
                            try {
                                mc = ModuleXMLReader.class.getClassLoader().loadClass(packageName + "." + moduleClass);
                                if (mc.getField("geometryOutput") != null) {
                                    OutputEgg goe = (OutputEgg) mc.getField("geometryOutput").get(null);
                                    if(goDesc != null) goe.setDescription(goDesc);
                                    if (goe != null) {
                                        outputEggList.add(goe);
                                    }
                                }
                            } catch (ClassNotFoundException ex) {
                            } catch (NoSuchFieldException ex) {
                            } catch (IllegalAccessException ex) {
                            } catch (IllegalArgumentException ex) {
                            } catch (SecurityException ex) {
                            }
                        }
                    }
                }
            }

            outputEggs = new OutputEgg[outputEggList.size()];
            for (int i = 0; i < outputEggs.length; i++) {
                outputEggs[i] = outputEggList.get(i);
            }
        }
        return outputEggs;
    }

    public static String[] getModuleInfo(String packageName) throws URISyntaxException, ParserConfigurationException, SAXException, IOException {
        if (packageName == null) {
            return null;
        }

        InputStream is = ModuleXMLReader.class.getResourceAsStream("/" + packageName.replace(".", "/") + "/module.xml");
        if (is == null) {
            return null;
        }
        
        String[] out = getModuleInfoFromStream(packageName, is);
        is.close();
        
        return out;
    }
    
    public static String[] getModuleInfoFromStream(String packageName, InputStream is) throws URISyntaxException, ParserConfigurationException, SAXException, IOException {
        if (packageName == null || is == null) {
            return null;
        }

        Node node = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is).getDocumentElement();
        Node descNode;
        Node readerNode;
        String moduleName = null;
        String moduleClass = null;
        String moduleDescription = null;
        String readerDataType = null;
        String testData = "false";

        if (node.getNodeName().equals("module")) {
            if (node.getAttributes().getNamedItem("name") != null) {
                moduleName = node.getAttributes().getNamedItem("name").getNodeValue();
            }

            if (node.getAttributes().getNamedItem("class") != null) {
                moduleClass = node.getAttributes().getNamedItem("class").getNodeValue();
                moduleClass = packageName + "." + moduleClass;
            }

            for (int i = 0; i < node.getChildNodes().getLength(); i++) {
                String nodeName = node.getChildNodes().item(i).getNodeName();
                if (nodeName.equalsIgnoreCase("description")) {
                    descNode = node.getChildNodes().item(i);
                    moduleDescription = descNode.getAttributes().getNamedItem("value").getNodeValue();
                }
                
                if (nodeName.equalsIgnoreCase("reader")) {
                    readerNode = node.getChildNodes().item(i);
                    String tmp = readerNode.getAttributes().getNamedItem("datatype").getNodeValue();
                    if(tmp != null && tmp.length() > 0)
                        readerDataType = tmp;
                }

                if (nodeName.equalsIgnoreCase("testdata")) {
                    testData = "true";
                }
            }

        }

        if (moduleName != null && moduleClass != null) {
            String[] out = new String[6];
            out[0] = moduleName;
            out[1] = moduleClass;
            out[2] = moduleDescription;
            out[3] = packageName;
            out[4] = readerDataType;
            out[5] = testData;
            return out;
        }
        return null;
    }

   private ModuleXMLReader()
   {
   }

}
