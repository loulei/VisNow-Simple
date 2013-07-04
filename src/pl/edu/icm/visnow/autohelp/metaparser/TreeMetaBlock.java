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

package pl.edu.icm.visnow.autohelp.metaparser;

import java.util.HashMap;

/**
 * @author  Bartosz Borucki (babor@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */

public class TreeMetaBlock extends MetaBlock {
    private String[] commandElements;
    private String nodeOpenTemplate = null;
    private String nodeCloseTemplate = null;
    public static final String TREE_CONTENT = "treecontent";
    
    public TreeMetaBlock(String blockHeader, String blockFooter, String template, HashMap<String,String> data) throws PreprocessException {
        super(blockHeader, blockFooter, template, data);
        String headerCommand = blockHeader.substring(SYMBOL_LENGTH, blockHeader.length()-SYMBOL_LENGTH).trim();
        String footerCommand = blockFooter.substring(SYMBOL_LENGTH, blockFooter.length()-SYMBOL_LENGTH).trim();
        commandElements = headerCommand.split(" ");
    }
    
    @Override
    protected String parseTemplate() {
        if(contentMeta.isEmpty() || contentAddress.size() != contentMeta.size())
            return template;

        if(commandElements == null || commandElements[0] == null)
            return "[ERROR]";
        
        if(!commandElements[0].equals("tree"))
            return "[ERROR]";

        if(commandElements.length != 2)
            return "[ERROR]";
        
        
        try {

            for (int i = 0; i < template.length()-SYMBOL_LENGTH; i++) {
                if(template.substring(i, i+SYMBOL_LENGTH).equals(SYMBOL_OPEN) ) {
                    //found BLOCK_OPEN
                    String tmp = template.substring(i+SYMBOL_LENGTH).trim();
                    if(!tmp.startsWith(TREE_CONTENT))
                        continue;

                    int startContent = i; 

                    //look for close
                    int j = i+SYMBOL_LENGTH;
                    boolean foundClose = false;
                    while(j<template.length()-SYMBOL_LENGTH+1) {
                        if(template.substring(j, j+SYMBOL_LENGTH).equals(SYMBOL_CLOSE) ) {
                            foundClose = true;
                            break;                        
                        }
                        j++;
                    }                
                    if(!foundClose) {
                        throw new PreprocessException(startContent);
                    }
                    //found BLOCK_CLOSE at j
                    int endContent = j+SYMBOL_LENGTH;   
                    
                    nodeOpenTemplate = template.substring(0,startContent);
                    nodeCloseTemplate = template.substring(endContent);
                }
            }
            
            String out = "";
            String variable = commandElements[1];

            String branch = data.get(variable);

            out = parseNode(out, variable, branch);

            return out;
            
        } catch(PreprocessException ex) {
            return "[ERROR]";
        }
        
        
    }

    private String parseNode(String out, String variable, String node) throws PreprocessException {
        if(nodeOpenTemplate == null || nodeCloseTemplate == null)
            return out;
        
        String ncontent = data.get(node+".content");

        String nodeOpenString = null;
        if(nodeOpenTemplate.contains(Meta.SYMBOL_OPEN) && nodeOpenTemplate.contains(Meta.SYMBOL_CLOSE)) {
            MainMetaBlock openBlock = new MainMetaBlock(nodeOpenTemplate.replaceAll(variable, node), data);
            nodeOpenString = openBlock.parseTemplate();            
        } else {
            nodeOpenString = nodeOpenTemplate;            
        }
        
        String nodeCloseString = null;
        if(nodeCloseTemplate.contains(Meta.SYMBOL_OPEN) && nodeCloseTemplate.contains(Meta.SYMBOL_CLOSE)) {
            MainMetaBlock closeBlock = new MainMetaBlock(nodeCloseTemplate.replaceAll(variable, node), data);
            nodeCloseString = closeBlock.parseTemplate();            
        } else {
            nodeCloseString = nodeCloseTemplate;            
        }
                
        out += nodeOpenString;
        if(ncontent == null) {
            //node is a leaf
            
            
        } else {
            //node is a branch
            String[] content = ncontent.split(":");
            for (int i = 0; i < content.length; i++) {
                out = parseNode(out, variable, content[i]);                                    
            }
        }
        out += nodeCloseString;        
        
        return out;
    }

//    
//    public static void main(String[] args) {
//        HashMap<String,String> data = new HashMap<String, String>();
//        data.put("branch", "root");
//        data.put("root.name", "rootname");
//        data.put("root.target", "roottarget");
//        data.put("root.content", "b1:b2:b3");
//        data.put("b1.name", "b1name");
//        data.put("b1.target", "b1target");
//        data.put("b1.content", "b11:b12");
//        data.put("b2.name", "b2name");
//        data.put("b2.target", "b2target");
//        data.put("b3.name", "b3name");
//        data.put("b3.target", "b3target");
//        data.put("b11.name", "b11name");
//        data.put("b11.target", "b11target");
//        data.put("b12.name", "b12name");
//        data.put("b12.target", "b12target");
//        
//        
//        String template = "<tocitem text=\"<$ branch.name $>\" target=\"<$ branch.target $>\" expand=\"false\"><$ treecontent $></tocitem>";
//        try {
//            TreeMetaBlock block = new TreeMetaBlock("<% tree branch %>", "<% endtree %>", template, data);
//            String out = block.getParsed();
//            System.out.println(out);
//            
//        } catch(PreprocessException ex) {
//            ex.printStackTrace();
//        }
//        
//    }
//    
    
}
