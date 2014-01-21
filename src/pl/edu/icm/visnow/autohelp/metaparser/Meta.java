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

public abstract class Meta {
    public static final int SYMBOL_LENGTH = 2;
    public static final String BLOCK_OPEN = "<%";
    public static final String BLOCK_CLOSE = "%>";
    public static final String BLOCK_TERMINATE = "end";
    
    public static final String SYMBOL_OPEN = "<$";
    public static final String SYMBOL_CLOSE = "$>";

    
    protected String template = null;
    protected HashMap<String,String> data;
    
    
    public Meta() throws PreprocessException {
        this(null,null);
    }
    
    public Meta(String template, HashMap<String,String> data) throws PreprocessException {
        this.template = cleanComments(cleanBlockSeparators(template));
        this.data = data;
    }
    
    public String getTemplate() {
        return this.template;
    }
    
    public String getParsed() {
        if(template == null)
            return null;
        
        return parseTemplate();
    }
    
    @Override
    public String toString() {
        return template;
    }
    
    protected abstract void preprocess() throws PreprocessException;
    protected abstract String parseTemplate();
    protected abstract int getTemplateLegth();

    private String cleanBlockSeparators(String str) {
        if(!str.contains(BLOCK_CLOSE))
            return str;
        String out = "";
        String[] tmp = str.split(BLOCK_CLOSE);
        for (int i = 0; i < tmp.length; i++) {
            out += tmp[i].trim();
            if(i < tmp.length-1)
                out += " "+BLOCK_CLOSE;
        }
        if(str.trim().endsWith(BLOCK_CLOSE))
            out += " "+BLOCK_CLOSE;
        
        
        tmp = out.split(BLOCK_OPEN);
        out = "";        
        for (int i = 0; i < tmp.length; i++) {
            //System.out.println("tmp["+i+"]="+tmp[i]);
            out += trimLastTabsAndSpaces(tmp[i]);
            //System.out.println("trm["+i+"]="+trimFirstTabsAndSpaces(tmp[i]));
            if(i < tmp.length-1)
                out += BLOCK_OPEN;
        }
        
        
        
        
        //System.out.println(out);
        return out;
    }
    
    private String cleanComments(String str) {
        String COMMENT_OPEN = "<!--";
        String COMMENT_CLOSE = "-->";

        if(!str.contains(COMMENT_OPEN))
            return str;
        String out = "";
        
        for (int i = 0; i < str.length()-COMMENT_CLOSE.length(); i++) {
            if(str.substring(i, i+COMMENT_OPEN.length()).equals(COMMENT_OPEN) ) {
                //found COMMENT_OPEN
                int startContent = i; 

                //look for close
                int j = i+COMMENT_OPEN.length();
                boolean foundClose = false;
                while(j<str.length()-COMMENT_CLOSE.length()+1) {
                    if(str.substring(j, j+COMMENT_CLOSE.length()).equals(COMMENT_CLOSE) ) {
                        foundClose = true;
                        break;                        
                    }
                    j++;
                }                
                if(!foundClose) {
                    return str;
                }
                //found COMMENT_CLOSE at j
                int endContent = j+COMMENT_CLOSE.length();   
                
                out = str.substring(0, startContent) + str.substring(endContent);
            }
        }

        
        
        
        return out;
    }
    
    private String trimFirstTabsAndSpaces(String str) {
        String out = new String(str);
        while(out.startsWith("\t") || out.startsWith(" ")) {
            out = out.substring(1);
        }
        return out;
    }

    private String trimLastTabsAndSpaces(String str) {
        String out = new String(str);
        while(out.endsWith("\t") || out.endsWith(" ")) {
            out = out.substring(0,out.length()-1);
        }
        return out;
    }
    
}
