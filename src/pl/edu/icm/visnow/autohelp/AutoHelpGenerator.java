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
package pl.edu.icm.visnow.autohelp;

import java.awt.Color;
import java.io.*;
import java.net.URISyntaxException;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.SAXException;
import pl.edu.icm.visnow.autohelp.metaparser.MainMetaBlock;
import pl.edu.icm.visnow.autohelp.metaparser.MetaBlock;
import pl.edu.icm.visnow.engine.core.InputEgg;
import pl.edu.icm.visnow.engine.core.ModuleXMLReader;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.engine.library.TypesMap;
import pl.edu.icm.visnow.engine.library.jar.JarLibReader;
import pl.edu.icm.visnow.lib.types.VNDataAcceptor;
import pl.edu.icm.visnow.lib.types.VNDataSchema;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;


public class AutoHelpGenerator {

    //paths realtive to project/src
    public static final String MODULE_HELP_TEMPLATE = "pl/edu/icm/visnow/autohelp/resources/module_help_template.html";
    public static final String MODULE_MAP_TEMPLATE_PRO = "pl/edu/icm/visnow/autohelp/resources/vnhelp_MAP_template_pro.xml";
    public static final String MODULE_TOC_TEMPLATE_PRO = "pl/edu/icm/visnow/autohelp/resources/vnhelp_TOC_template_pro.xml";
    public static final String MODULE_MAP_TEMPLATE_SIMPLE = "pl/edu/icm/visnow/autohelp/resources/vnhelp_MAP_template_simple.xml";
    public static final String MODULE_TOC_TEMPLATE_SIMPLE = "pl/edu/icm/visnow/autohelp/resources/vnhelp_TOC_template_simple.xml";
    public static final String AUTOHELP_ROOT = "doc/vnautohelp";
    //paths realtive to AUTOHELP_ROOT
    public static final String AUTOHELP_MODULES_DIR = "help/modules";
    public static final String AUTOHELP_MODULE_IMAGES_DIR = "help/modules/resources";
    public static final String LIBRARY_XML_PRO = "extended_library.xml";
    public static final String LIBRARY_XML_SIMPLE = "simple_library.xml";
    public static final String HELP_PACKAGE_PRO = "help_resources_pro";
    public static final String HELP_PACKAGE_SIMPLE = "help_resources";

    protected final ArrayList<String> modules;
    protected final ArrayList<String> moduleBases;
    protected final String srcDir;
    protected TypesMap typesMap;
    protected ArrayList<String> folders;
    
    //this variable is used for pro/simple selection when no external argument is given to main
    private static boolean buildPro = false;
    
    
    

    public AutoHelpGenerator(String srcDir, ArrayList<String> modules, ArrayList<String> moduleBases, TypesMap typesMap, ArrayList<String> folders) {
        this.srcDir = srcDir;
        this.modules = modules;
        this.moduleBases = moduleBases;
        this.typesMap = typesMap;
        this.folders = folders;
    }

    public static void main(String[] args) throws IOException {
        if(args != null && args.length == 1) {
            buildPro = (args[0].equals("-pro"));
        }
        
        String workDir = System.getProperty("user.dir");
        String srcDir = workDir + File.separator + "src";
        String libraryXml;
        if(buildPro) {
            //settings for help for VisNow Pro
            libraryXml = LIBRARY_XML_PRO;
        } else {
            //settings for help for VisNow Simple
            libraryXml = LIBRARY_XML_SIMPLE;
        }        
        System.out.println("scanning source dir: " + srcDir + File.separator + libraryXml);
        ArrayList<String> moduleBases = new ArrayList<String>();
        moduleBases.add("pl.edu.icm.visnow.lib.basic");
        moduleBases.add("pl.edu.icm.visnow.lib.chemistry");
        ArrayList<String> modules = listModules(srcDir + File.separator + libraryXml, moduleBases);
        System.out.println("done.");


        TypesMap typesMap = null;
        try {
            String typesXmlPath = srcDir + File.separator + "types.xml";
            File typesXmlFile = new File(typesXmlPath);
            InputStream is = new FileInputStream(typesXmlFile);
            typesMap = JarLibReader.readTypesMap(is);
            is.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        System.out.println("cleaning help files in dir: " + srcDir + File.separator + AUTOHELP_ROOT);
        File modulesDir = new File(srcDir + File.separator + AUTOHELP_ROOT + File.separator + AUTOHELP_MODULES_DIR);
        if (!modulesDir.exists()) {
            modulesDir.mkdirs();
        } else {
            String[] modulesPaths = modulesDir.list();
            if (modulesPaths != null) {
                for (int i = 0; i < modulesPaths.length; i++) {
                    removeFiles(modulesDir + File.separator + modulesPaths[i], "module.html");
                }
            }
        }



        File mapFile = new File(srcDir + File.separator + AUTOHELP_ROOT + File.separator + "vnhelp_MAP.xml");
        mapFile.delete();
        ArrayList<String> folders = listFolders(srcDir + File.separator + libraryXml);

        File tocFile = new File(srcDir + File.separator + AUTOHELP_ROOT + File.separator + "vnhelp_TOC.xml");
        tocFile.delete();

        System.out.println("done.");


        System.out.println("storing help files in dir: " + srcDir + File.separator + AUTOHELP_ROOT);
        AutoHelpGenerator help = new AutoHelpGenerator(srcDir, modules, moduleBases, typesMap, folders);
        help.generateHelp(buildPro);
        System.out.println("done.");
    }

    public static String convertStreamToString(InputStream is)
            throws IOException {
        /*
         * To convert the InputStream to String we use the Reader.read(char[]
         * buffer) method. We iterate until the Reader return -1 which means
         * there's no more data to read. We use the StringWriter class to
         * produce the string.
         */
        if (is != null) {
            Writer writer = new StringWriter();

            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(
                        new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
            return writer.toString();
        } else {
            return "";
        }
    }

    private void generateHelpFile(String module, boolean pro) {
        boolean found = false;
        for (int i = 0; i < modules.size(); i++) {
            if (modules.get(i).equals(module)) {
                found = true;
                break;
            }
        }
        if (!found) {
            System.err.println("no such module: " + module);
            return;
        }


        HashMap<String, String> data = new HashMap<String, String>();
        //fill data from module
        try {
            String moduleXmlPath = srcDir + File.separator + module.replace(".", File.separator) + File.separator + "module.xml";

            File moduleXmlFile = new File(moduleXmlPath);
            InputStream is = new FileInputStream(moduleXmlFile);
            if (is == null) {
                System.err.println("Error reading module.xml for module: " + module);
                return;
            }
            String[] moduleInfo = ModuleXMLReader.getModuleInfoFromStream(module, is, null);
            is.close();
            data.put("module.name", toCapital(moduleInfo[0]));
            data.put("module.description", moduleInfo[2]);

            data.put("module.image", "resources/module_image_" + module + ".png");
            is = new FileInputStream(moduleXmlFile);
            InputEgg[] moduleInputs = ModuleXMLReader.getInputEggsFromStream(module, is, null);
            is.close();

            if (moduleInputs != null && moduleInputs.length > 0) {
                String inputs = "";
                String input;
                Color c;
                for (int i = 0; i < moduleInputs.length; i++) {
                    input = "input" + i;
                    inputs += input;
                    if (i < moduleInputs.length - 1) {
                        inputs += ":";
                    }
                    data.put(input + ".name", moduleInputs[i].getName());
                    data.put(input + ".type", moduleInputs[i].getType().getSimpleName());
                    data.put(input + ".description", moduleInputs[i].getDescription());
                    VNDataAcceptor[] acceptors = moduleInputs[i].getVNDataAcceptors();
                    if (acceptors != null) {
                        String acceptorsString = "";
                        for (int j = 0; j < acceptors.length; j++) {
                            acceptorsString += acceptors[j].toHtmlString() + "<br>";
                        }
                        data.put(input + ".acceptors", acceptorsString);
                    }

                    if (typesMap != null) {
                        c = typesMap.getStyle(moduleInputs[i].getType().getName()).getColor();
                        data.put(input + ".color", color2hex(c));
                    }
                }
                data.put("module.inputs", inputs);
            }

            is = new FileInputStream(moduleXmlFile);
            OutputEgg[] moduleOutputs = ModuleXMLReader.getOutputEggsFromStream(module, is, null);
            is.close();
            if (moduleOutputs != null && moduleOutputs.length > 0) {
                String outputs = "";
                String output;
                Color c;
                for (int i = 0; i < moduleOutputs.length; i++) {
                    output = "output" + i;
                    outputs += output;
                    if (i < moduleOutputs.length - 1) {
                        outputs += ":";
                    }
                    data.put(output + ".name", moduleOutputs[i].getName());
                    data.put(output + ".type", moduleOutputs[i].getType().getSimpleName());
                    data.put(output + ".description", moduleOutputs[i].getDescription());
                    VNDataSchema[] schemas = moduleOutputs[i].getVNDataSchemas();
                    if (schemas != null) {
                        String schemasString = "";
                        for (int j = 0; j < schemas.length; j++) {
                            schemasString += schemas[j].toHtmlString() + "<br>";
                        }
                        data.put(output + ".schemas", schemasString);
                    }

                    if (typesMap != null) {
                        c = typesMap.getStyle(moduleOutputs[i].getType().getName()).getColor();
                        data.put(output + ".color", color2hex(c));
                    }
                }
                data.put("module.outputs", outputs);
            }

            //put help_desc to data <$ module.helpfile $>
            String targetDir = srcDir + File.separator
                    + AUTOHELP_ROOT + File.separator
                    + AUTOHELP_MODULES_DIR + File.separator
                    + module + File.separator
                    + "resources";

            String hp = HELP_PACKAGE_SIMPLE;
            if(pro) {
                hp = HELP_PACKAGE_PRO;
                File helpFileResources = new File(srcDir + File.separator + module.replace('.', File.separatorChar) + File.separator + hp);
                if(!helpFileResources.exists()) {
                    hp = HELP_PACKAGE_SIMPLE;
                }
            }
            String helpFileResourcesSrc = srcDir + File.separator + module.replace('.', File.separatorChar) + File.separator + hp;
            String helpFileSrc = helpFileResourcesSrc + File.separator + "help_desc.html";            
            File helpFileResources = new File(helpFileResourcesSrc);
            File helpFile = new File(helpFileSrc);
            if (helpFileResources.exists() && helpFileResources.isDirectory() && helpFile.exists()) {
                File targetDirF = new File(targetDir);
                if (!targetDirF.exists()) {
                    targetDirF.mkdirs();
                }

                String[] ls = helpFileResources.list();
                for (int i = 0; i < ls.length; i++) {
                    if (ls[i].equals("help_desc.html") || ls[i].equals(".svn")) {
                        continue;
                    }

                    File lsf = new File(helpFileResources.getAbsolutePath() + File.separator + ls[i]);
                    if (lsf.isDirectory()) {
                        copydir(helpFileResources.getAbsolutePath() + File.separator + ls[i], targetDir);
                    } else {
                        copyfile(helpFileResources.getAbsolutePath() + File.separator + ls[i], targetDir + File.separator + ls[i]);
                    }
                }

                String helpfileContent = "";
                String line;
                BufferedReader in = new BufferedReader(new FileReader(helpFile));
                while ((line = in.readLine()) != null) {
                    helpfileContent += line + "\n";
                }
                in.close();

                for (int i = 0; i < ls.length; i++) {
                    if (ls[i].equals("help_desc.html") || ls[i].equals(".svn")) {
                        continue;
                    }

                    File lsf = new File(helpFileResources.getAbsolutePath() + File.separator + ls[i]);
                    //replace all jest niebezpieczne bo moze zamienic cos w tekstach
                    //helpfileContent = helpfileContent.replaceAll(ls[i], "resources/" + ls[i]);

                    if (lsf.isFile()) {
                        helpfileContent = helpfileContent.replaceAll(ls[i], "resources/" + ls[i]);
                    } else if (lsf.isDirectory()) {
                        helpfileContent = helpfileContent.replaceAll(ls[i] + "/", "resources/" + ls[i] + "/");
                    }



                }

                //parse helpfileContent
                if (helpfileContent.contains("<head>")) {
                    String helpfileContentHeader = helpfileContent.substring(
                            helpfileContent.indexOf("<head>"),
                            helpfileContent.indexOf("</head>"));
                    if (helpfileContentHeader.contains("<style")) {
                        String helpfileContentStyle = helpfileContentHeader.substring(
                                helpfileContentHeader.indexOf("<style type=\"text/css\">")+23,
                                helpfileContentHeader.indexOf("</style>"));
                        data.put("module.helpfile.style", helpfileContentStyle);
                    }
                }

                if (helpfileContent.contains("<body")) {
                    String helpfileContentBody = helpfileContent.substring(
                            helpfileContent.indexOf("<body"),
                            helpfileContent.indexOf("</body>"));
                    helpfileContentBody = helpfileContentBody.substring(
                            helpfileContentBody.indexOf(">") + 1);
                    data.put("module.helpfile.body", helpfileContentBody);
                } else {
                    data.put("module.helpfile.body", helpfileContent);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (data.isEmpty()) {
            return;
        }


        try {
            String template = convertStreamToString(new FileInputStream(srcDir + File.separator + MODULE_HELP_TEMPLATE));
            MetaBlock mainBlock = new MainMetaBlock(template, data);
            String out = mainBlock.getParsed();
            String location = srcDir + File.separator
                    + AUTOHELP_ROOT + File.separator
                    + AUTOHELP_MODULES_DIR + File.separator
                    + module + File.separator
                    + "module.html";
            writeOutput(out, location);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void generateHelpFiles(boolean pro) throws IOException {
        for (String module : modules) {
            generateHelpFile(module, pro);
        }
    }

    private void generateHelpMap(boolean pro) {
        HashMap<String, String> data = new HashMap<String, String>();
        String modulesString = "";
        String module;
        for (int i = 0; i < modules.size(); i++) {
            module = "module" + i;
            modulesString += module;
            data.put(module + ".class", modules.get(i));
            if (i < modules.size() - 1) {
                modulesString += ":";
            }
        }
        data.put("modules", modulesString);

        String foldersString = "";
        for (int i = 0; i < folders.size(); i++) {
            String folder = "folder" + i;
            foldersString += folder;
            String folderFile = folders.get(i).substring(0, folders.get(i).length() - 5);
            data.put(folder, folderFile);

            //copy folderFile to modules
            try {
                String inFile = srcDir + File.separator + folderFile.replace('.', File.separatorChar) + ".html";
                String outFile = srcDir + File.separator + AUTOHELP_ROOT + File.separator
                        + AUTOHELP_MODULES_DIR + File.separator
                        + folderFile + ".html";
                copyfile(inFile, outFile);


            } catch (Exception ex) {
                ex.printStackTrace();
            }


            if (i < folders.size() - 1) {
                foldersString += ":";
            }
        }
        data.put("folders", foldersString);

        try {
            String mm = pro?MODULE_MAP_TEMPLATE_PRO:MODULE_MAP_TEMPLATE_SIMPLE;
            String template = convertStreamToString(new FileInputStream(srcDir + File.separator + mm));
            MetaBlock mainBlock = new MainMetaBlock(template, data);
            String out = mainBlock.getParsed();
            //System.out.println(out);
            writeOutput(out, srcDir + File.separator + AUTOHELP_ROOT + File.separator + "vnhelp_MAP.xml");
        } catch (Exception ex) {
            ex.printStackTrace();
        }



    }

    private void generateHelpTOC(boolean pro) {
        HashMap<String, String> data = new HashMap<String, String>();
        String libraryXml = pro?LIBRARY_XML_PRO:LIBRARY_XML_SIMPLE;
        try {            
            InputStream is = new FileInputStream(srcDir + File.separator + libraryXml);
            Node main = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is).getDocumentElement();
            if (!main.getNodeName().equalsIgnoreCase("library")) {
                throw new Exception("Error in " + libraryXml);
            }


            data.put("modules", "root");
            data.put("root.name", "Modules");
            data.put("root.target", "");
            processNodeList(data, "root", main, false);
        } catch (Exception ex) {
            System.err.println("Error reading " + libraryXml);
            ex.printStackTrace();
        }

        try {
            String mt = pro?MODULE_TOC_TEMPLATE_PRO:MODULE_TOC_TEMPLATE_SIMPLE;
            String template = convertStreamToString(new FileInputStream(srcDir + File.separator + mt));
            MetaBlock mainBlock = new MainMetaBlock(template, data);
            String out = mainBlock.getParsed();
            //System.out.println(out);
            writeOutput(out, srcDir + File.separator + AUTOHELP_ROOT + File.separator + "vnhelp_TOC.xml");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void generateHelp(boolean pro) throws IOException {
        generateHelpFiles(pro);
        generateHelpMap(pro);
        generateHelpTOC(pro);
    }

    public static ArrayList<String> listModules(String libraryXmlPath, ArrayList<String> packageRootFilters) {
        ArrayList<String> modules = null;
        try {
            File library = new File(libraryXmlPath);
            BufferedReader in = new BufferedReader(new FileReader(library));
            modules = new ArrayList<String>();
            String line;
            String tLine;
            while ((line = in.readLine()) != null) {
                if (!line.contains("<core package=")) {
                    continue;
                }

                tLine = line.trim();
                String module = tLine.substring(15, tLine.lastIndexOf("\""));
                modules.add(module);
            }
        } catch (FileNotFoundException ex) {
            System.err.println("File " + libraryXmlPath + " not found!");
            return null;
        } catch (IOException ex) {
            System.err.println("Error while reading file " + libraryXmlPath);
            return null;
        }

        if (modules == null || modules.isEmpty()) {
            return null;
        }

        //filter
        ArrayList<String> outModules = new ArrayList<String>();
        for (int i = 0; i < modules.size(); i++) {
            boolean found = false;
            String module = modules.get(i);

            for (int j = 0; j < outModules.size(); j++) {
                if (module.equals(outModules.get(j))) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                if (packageRootFilters != null && !packageRootFilters.isEmpty()) {
                    boolean accepted = false;
                    for (int n = 0; n < packageRootFilters.size(); n++) {
                        if (module.startsWith(packageRootFilters.get(n))) {
                            accepted = true;
                            break;
                        }
                    }
                    if (accepted) {
                        outModules.add(module);
                    }
                } else {
                    outModules.add(module);
                }
            }
        }
        return outModules;
    }

    public static String toCapital(String str) {
        String[] words = str.split(" ");
        String out = "";
        for (int i = 0; i < words.length; i++) {
            char[] letters = words[i].toCharArray();
            char[] l0 = words[i].substring(0, 1).toUpperCase().toCharArray();
            letters[0] = l0[0];
            out += (new String(letters));
            if (i < words.length - 1) {
                out += " ";
            }
        }
        return out;
    }

    static void writeOutput(String str, String filePath) {
        try {
            File f = new File(filePath);
            File parentDir = f.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }

            FileOutputStream fos = new FileOutputStream(f);
            Writer out = new OutputStreamWriter(fos, "UTF8");
            out.write(str);
            out.close();
            //System.out.println("stored help file: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String color2hex(Color c) {
        String r = Integer.toHexString(c.getRed() & 0x00ffffff);
        if (r.length() == 0) {
            r = "00";
        } else if (r.length() == 1) {
            r = "0" + r;
        }

        String g = Integer.toHexString(c.getGreen() & 0x00ffffff);
        if (g.length() == 0) {
            g = "00";
        } else if (g.length() == 1) {
            g = "0" + g;
        }

        String b = Integer.toHexString(c.getBlue() & 0x00ffffff);
        if (b.length() == 0) {
            b = "00";
        } else if (b.length() == 1) {
            b = "0" + b;
        }

        return "#" + r + g + b;
    }

    private static void remove(String path) {
        if (path == null) {
            return;
        }

        if (path.equals(".") || path.equals("..")) {
            return;
        }

        File f = new File(path);
        if (!f.exists()) {
            return;
        }

        if (f.isFile()) {
            f.delete();
            return;
        }

        if (f.isDirectory()) {
            String[] ls = f.list();
            for (int i = 0; i < ls.length; i++) {
                remove(f.getAbsolutePath() + File.separator + ls[i]);
            }
            f.delete();
        }
    }

    private static void removeFiles(String path, String extension) {
        if (path == null) {
            return;
        }

        File f = new File(path);
        if (!f.exists()) {
            return;
        }

        if (f.isFile()) {
            if (extension == null || (extension != null && path.endsWith(extension))) {
                f.delete();
            }
            return;
        }

        if (f.isDirectory()) {
            String[] ls = f.list();
            for (int i = 0; i < ls.length; i++) {
                removeFiles(f.getAbsolutePath() + File.separator + ls[i], extension);
            }
        }
    }

    private static ArrayList<String> listFolders(String libraryXmlPath) {
        ArrayList<String> folders = null;
        try {
            File library = new File(libraryXmlPath);
            BufferedReader in = new BufferedReader(new FileReader(library));
            folders = new ArrayList<String>();
            String line;
            String tLine;
            while ((line = in.readLine()) != null) {
                if (!line.contains("<folder ") || !line.contains("autohelpfile=")) {
                    continue;
                }

                tLine = line.trim();
                String tmp = tLine.substring(tLine.indexOf("autohelpfile=") + 14);
                String folderHelpFile = tmp.substring(0, tmp.indexOf("\""));
                folders.add(folderHelpFile);
            }
        } catch (FileNotFoundException ex) {
            System.err.println("File " + libraryXmlPath + " not found!");
            return null;
        } catch (IOException ex) {
            System.err.println("Error while reading file " + libraryXmlPath);
            return null;
        }

        if (folders == null || folders.isEmpty()) {
            return null;
        }

        return folders;
    }
    private int count = 0;

    private void processNodeList(HashMap<String, String> data, String node, Node parentNode, boolean sorted) throws FileNotFoundException, URISyntaxException, IOException, ParserConfigurationException, SAXException {
        String nodeContent = "";

        if (sorted) {
            clipboard = srcDir;
            sortChildNodes(parentNode, false, 1, new ModuleNameComparator());
        }

        NodeList list = parentNode.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            if (list.item(i).getNodeName().equals("folder")) {
                String fldr = "folder" + (count++);
                NodeList nlist = list.item(i).getChildNodes();
                if (!checkList(nlist)) {
                    continue;
                }

                boolean sort = false;
                NamedNodeMap atts = list.item(i).getAttributes();
                if (atts != null) {
                    Node n = atts.getNamedItem("autosort");
                    if (n != null) {
                        sort = ("yes").equals(n.getNodeValue());
                    }
                }

                nodeContent += fldr + ":";
                data.put(fldr + ".name", list.item(i).getAttributes().getNamedItem("name").getNodeValue());
                if (list.item(i).getAttributes().getNamedItem("autohelpfile") != null) {
                    String tmp = list.item(i).getAttributes().getNamedItem("autohelpfile").getNodeValue();
                    data.put(fldr + ".target", tmp.substring(0, tmp.length() - 5));
                }
                processNodeList(data, fldr, list.item(i), sort);
            } else if (list.item(i).getNodeName().equals("core")) {
                if (list.item(i).getAttributes().getNamedItem("package") == null) {
                    continue;
                }

                String modPackage = list.item(i).getAttributes().getNamedItem("package").getNodeValue();
                boolean ok = false;
                for (int j = 0; j < modules.size(); j++) {
                    if (modules.get(j).equals(modPackage)) {
                        ok = true;
                        break;
                    }
                }
                if (!ok) {
                    continue;
                }

                String mod = "module" + (count++);
                nodeContent += mod + ":";
                String moduleXmlPath = srcDir + File.separator + modPackage.replace(".", File.separator) + File.separator + "module.xml";
                File moduleXmlFile = new File(moduleXmlPath);
                InputStream is = new FileInputStream(moduleXmlFile);
                if (is == null) {
                    System.err.println("Error reading module.xml for module: " + modPackage);
                    return;
                }
                String[] moduleInfo = ModuleXMLReader.getModuleInfoFromStream(modPackage, is, null);
                is.close();

                data.put(mod + ".name", toCapital(moduleInfo[0]));
                data.put(mod + ".target", modPackage);
            }
        }
        if (nodeContent.endsWith(":")) {
            nodeContent = nodeContent.substring(0, nodeContent.length() - 1);
        }
        nodeContent = sortContent(nodeContent);
        data.put(node + ".content", nodeContent);
    }

    private String sortContent(String str) {
        String out = "";
        String[] content = str.split(":");
        for (int i = 0; i < content.length; i++) {
            if (content[i].startsWith("folder")) {
                out += content[i] + ":";
            }
        }
        for (int i = 0; i < content.length; i++) {
            if (content[i].startsWith("module")) {
                out += content[i] + ":";
            }
        }
        if (out.endsWith(":")) {
            out = out.substring(0, out.length() - 1);
        }
        return out;
    }

    private static void copyfile(String srFile, String dtFile) {
        File f1 = new File(srFile);
        File f2 = new File(dtFile);
        copyfile(f1, f2);
    }

    private static void copydir(String srDir, String dtDir) {
        File d1 = new File(srDir);
        File d2 = new File(dtDir);
        copydir(d1, d2);
    }

    private static void copydir(File srcDir, File dstDir) {
        if (srcDir == null || dstDir == null) {
            return;
        }

        if (!srcDir.isDirectory() || !dstDir.isDirectory()) {
            return;
        }

        String srcDirName = srcDir.getAbsolutePath().substring(srcDir.getAbsolutePath().lastIndexOf(File.separator));
        String dstName = dstDir.getAbsolutePath() + File.separator + srcDirName;
        File dst = new File(dstName);
        if (!dst.exists()) {
            dst.mkdirs();
        }

        String[] ls = srcDir.list();
        for (int i = 0; i < ls.length; i++) {
            File tmp = new File(srcDir.getAbsolutePath() + File.separator + ls[i]);
            if (tmp.isDirectory()) {
                copydir(tmp, dst);
            } else {
                copyfile(tmp.getAbsolutePath(), dstName + File.separator + ls[i]);
            }
        }
    }

    private static void copyfile(File srcFile, File dstFile) {
        try {
            InputStream in = new FileInputStream(srcFile);
            OutputStream out = new FileOutputStream(dstFile);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage() + " in the specified directory.");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private boolean checkList(NodeList list) {
        //moduleBases
        //modules
        //list

        if (list == null) {
            return false;
        }

        for (int i = 0; i < list.getLength(); i++) {
            if (list.item(i).getNodeName().equals("core")) {
                if (list.item(i).getAttributes().getNamedItem("package") == null) {
                    continue;
                }

                String modPackage = list.item(i).getAttributes().getNamedItem("package").getNodeValue();

                //check module.xml
                File f = new File(srcDir + File.separator + modPackage.replace('.', File.separatorChar) + File.separator + "module.xml");
                if (!f.exists()) {
                    continue;
                }

                //check in modules list
                boolean ok = false;
                for (int j = 0; j < modules.size(); j++) {
                    if (modules.get(j).equals(modPackage)) {
                        ok = true;
                        break;
                    }
                }
                if (!ok) {
                    continue;
                }

                if (moduleBases != null && !moduleBases.isEmpty()) {
                    //check module bases
                    ok = false;
                    for (int j = 0; j < moduleBases.size(); j++) {
                        if (modPackage.startsWith(moduleBases.get(j))) {
                            ok = true;
                            continue;
                        }
                    }
                    if (!ok) {
                        continue;
                    }
                }
                return true;
            } else if (list.item(i).getNodeName().equals("folder")) {
                NodeList sublist = list.item(i).getChildNodes();
                boolean subcheck = checkList(sublist);
                if (subcheck) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void sortChildNodes(Node node, boolean descending, int depth, Comparator comparator) {

        List nodes = new ArrayList();
        NodeList childNodeList = node.getChildNodes();
        if (depth > 0 && childNodeList.getLength() > 0) {
            for (int i = 0; i < childNodeList.getLength(); i++) {
                Node tNode = childNodeList.item(i);
                sortChildNodes(tNode, descending, depth - 1,
                        comparator);
                // Remove empty text nodes
                if ((!(tNode instanceof Text))
                        || (tNode instanceof Text && ((Text) tNode)
                        .getTextContent().trim().length() > 1)) {
                    nodes.add(tNode);
                }
            }
            Comparator comp = (comparator != null) ? comparator : new DefaultNodeNameComparator();
            if (descending) {
                //if descending is true, get the reverse ordered comparator
                Collections.sort(nodes, Collections.reverseOrder(comp));
            } else {
                Collections.sort(nodes, comp);
            }

            for (Iterator iter = nodes.iterator(); iter.hasNext();) {
                Node element = (Node) iter.next();
                node.appendChild(element);
            }
        }

    }

    static class DefaultNodeNameComparator implements Comparator {

        public int compare(Object arg0, Object arg1) {
            return ((Node) arg0).getNodeName().compareTo(
                    ((Node) arg1).getNodeName());
        }
    }

    private static String clipboard = null;
    
    static class ModuleNameComparator implements Comparator {

        @Override
        public int compare(Object arg0, Object arg1) {
            if(!(arg0 instanceof Node))
                return 0;
            if(!(arg1 instanceof Node))
                return 0;
            
            Node node0 = (Node)arg0;
            Node node1 = (Node)arg1;            
            String node0Name = node0.getNodeName();
            String node1Name = node1.getNodeName();
            
            if(!node0Name.equals("core") || node0.getAttributes().getNamedItem("package") == null)
                return -1;
            if(!node1Name.equals("core") || node1.getAttributes().getNamedItem("package") == null)
                return 1;
            
            //read both module.xml and check names
            String module0Name = "";
            String module1Name = "";
            
            try {
                String modPackage = node0.getAttributes().getNamedItem("package").getNodeValue();
                File f = new File(clipboard + File.separator + modPackage.replace('.', File.separatorChar) + File.separator + "module.xml");
                InputStream is = new FileInputStream(f);
                String[] moduleInfo = ModuleXMLReader.getModuleInfoFromStream(modPackage, is, null);
                is.close();
                module0Name = toCapital(moduleInfo[0]);
            } catch(Exception ex) {
                return -1;
            }
            
            try {
                String modPackage = node1.getAttributes().getNamedItem("package").getNodeValue();
                File f = new File(clipboard + File.separator + modPackage.replace('.', File.separatorChar) + File.separator + "module.xml");
                InputStream is = new FileInputStream(f);
                String[] moduleInfo = ModuleXMLReader.getModuleInfoFromStream(modPackage, is, null);
                is.close();
                module1Name = toCapital(moduleInfo[0]);
            } catch(Exception ex) {
                return 1;
            }
            
            return module0Name.compareTo(module1Name);
        }
    }
    
}
