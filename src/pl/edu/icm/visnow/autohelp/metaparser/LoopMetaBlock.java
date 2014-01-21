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

public class LoopMetaBlock extends MetaBlock {
    private String[] commandElements;
    
    public LoopMetaBlock(String blockHeader, String blockFooter, String template, HashMap<String,String> data) throws PreprocessException {
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
        
        if(!commandElements[0].equals("for"))
            return "[ERROR]";

        if(commandElements.length < 4)
            return "[ERROR]";
        
        if(commandElements[2].equals("in") && commandElements.length != 4)
            return "[ERROR]";

        if(commandElements[3].equals("to") && commandElements.length != 5)
            return "[ERROR]";
        
        
        String variable = commandElements[1];
        
        if(commandElements[2].equals("in")) {
            try {
                String key = commandElements[3].trim();
                String poolStr = data.get(key);
                if(poolStr == null) {
                    //return blockHeader+template+blockFooter;
                    return "";
                }

                String[] pool = poolStr.split(":");
                String out = "";
                MainMetaBlock block;
                for (int i = 0; i < pool.length; i++) {
                    out += subparseTemplate2(variable, pool[i]);
                }
                return out;
            } catch(PreprocessException ex) {
                return "[ERROR]";
            }
        }
        
        
        if(commandElements[3].equals("to")) {
            try {
                int low = Integer.parseInt(commandElements[2]);
                int up = Integer.parseInt(commandElements[4]);
                String out = "";
                for (int i = low; i < up+1; i++) {
                    out += subparseTemplate1(variable, i);
                }
                return out;
            } catch(NumberFormatException ex) {
                return "[ERROR]";
            } catch(PreprocessException ex) {
                return "[ERROR]";
            }
        }
        
        return "[ERROR]";
    }

    protected String subparseTemplate1(String variable, int variableValue) throws PreprocessException {
        HashMap<String,String> subdata = new HashMap<String, String>();
        subdata.putAll(data);
        subdata.put(variable, ""+variableValue);
        MainMetaBlock block = new MainMetaBlock(template, subdata);        
        return block.parseTemplate();
    }

    protected String subparseTemplate2(String variable, String value) throws PreprocessException {
        //String subtemplate = template.replaceAll(variable, value);        

        String subtemplate = new String(template);
        for (int i = 0; i < subtemplate.length()-SYMBOL_LENGTH; i++) {
            if(subtemplate.substring(i, i+SYMBOL_LENGTH).equals(SYMBOL_OPEN) ) {
                //found BLOCK_OPEN
                int start = i;
                //look for close
                int j = i+SYMBOL_LENGTH;
                boolean foundClose = false;
                while(j<subtemplate.length()-SYMBOL_LENGTH+1) {
                    if(subtemplate.substring(j, j+SYMBOL_LENGTH).equals(SYMBOL_CLOSE) ) {
                        foundClose = true;
                        break;                        
                    }
                    j++;
                }                
                if(!foundClose) {
                    throw new PreprocessException(start);
                }
                //found BLOCK_CLOSE at j
                int end = j+SYMBOL_LENGTH;
                
                String foundVariable = subtemplate.substring(start+SYMBOL_LENGTH, end-SYMBOL_LENGTH).trim();                
                if(foundVariable.startsWith(variable))                
                    subtemplate = subtemplate.substring(0,start) + SYMBOL_OPEN + " " + foundVariable.replaceAll(variable, value) + " " + SYMBOL_CLOSE + subtemplate.substring(end);
            }
        }
        MainMetaBlock block = new MainMetaBlock(subtemplate, data);        
        return block.parseTemplate();
    }
    
}
