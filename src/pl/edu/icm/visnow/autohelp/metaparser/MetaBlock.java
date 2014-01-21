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

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author  Bartosz Borucki (babor@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */

public abstract class MetaBlock extends Meta {
    //protected HashMap<Integer,Meta> content = new HashMap<Integer, Meta>();
    protected ArrayList<Integer> contentAddress = new ArrayList<Integer>();
    protected ArrayList<Meta> contentMeta = new ArrayList<Meta>();
    
    protected String blockHeader;
    protected String blockFooter;
    
    public MetaBlock(String blockHeader, String blockFooter, String template, HashMap<String,String> data) throws PreprocessException {
        super(template, data);   
        this.blockHeader = blockHeader;
        this.blockFooter = blockFooter;
        preprocess();
    }
    
    @Override
    protected void preprocess() throws PreprocessException {
        if(blockHeader != null && !(blockHeader.startsWith(BLOCK_OPEN) && blockHeader.endsWith(BLOCK_CLOSE)))
            throw new PreprocessException(-1);
        if(blockFooter != null && !(blockFooter.startsWith(BLOCK_OPEN) && blockFooter.endsWith(BLOCK_CLOSE)))
            throw new PreprocessException(-1);

        int N = template.length();
        for (int i = 0; i < N-SYMBOL_LENGTH; i++) {
            if(template.substring(i, i+SYMBOL_LENGTH).equals(BLOCK_OPEN) ) {
                //found BLOCK_OPEN
                int start = i;
                //look for close
                int j = i+SYMBOL_LENGTH;
                boolean foundClose = false;
                while(j<N-SYMBOL_LENGTH+1) {
                    if(template.substring(j, j+SYMBOL_LENGTH).equals(BLOCK_CLOSE) ) {
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
                
                
                int startTerm = -1;
                int endTerm = -1;
                
                /*
                //look for block termination
                for (int k = N-SYMBOL_LENGTH; k > end; k--) {
                    if(template.substring(k, k+SYMBOL_LENGTH).equals(BLOCK_CLOSE) ) {
                        endTerm = k+SYMBOL_LENGTH;
                        int m = k-SYMBOL_LENGTH;
                        boolean foundOpen = false;
                        while(m>start+SYMBOL_LENGTH) {
                            if(template.substring(m, m+SYMBOL_LENGTH).equals(BLOCK_OPEN) ) {
                                foundOpen = true;
                                break;                        
                            }
                            m--;
                        }                
                        if(!foundOpen) {
                            throw new PreprocessException(start);
                        }
                        //found termination of block
                        startTerm = m;
                        break;                        
                    }                                        
                }
                */
                int opencount = 0;
                for (int k = end; k < N-SYMBOL_LENGTH; k++) {
                    if(template.substring(k, k+SYMBOL_LENGTH).equals(BLOCK_OPEN) ) {
                        String tmp = template.substring(k+SYMBOL_LENGTH).trim();
                        if(tmp.startsWith(BLOCK_TERMINATE)) {
                            if(opencount == 0) {
                                //myclose
                                startTerm = k;
                                int m = k+SYMBOL_LENGTH;
                                foundClose = false;
                                while(m<N-SYMBOL_LENGTH+1) {
                                    if(template.substring(m, m+SYMBOL_LENGTH).equals(BLOCK_CLOSE) ) {
                                        foundClose = true;
                                        break;                        
                                    }
                                    m++;
                                }                
                                if(!foundClose) {
                                    throw new PreprocessException(startTerm);
                                }
                                //found BLOCK_CLOSE at j
                                endTerm = m+SYMBOL_LENGTH;
                                break;                        
                            } else {
                                opencount--;
                            }
                        } else {
                            opencount++;
                        }
                    }
                }
                
                if(startTerm == -1 || endTerm == -1) {
                    throw new PreprocessException(start);
                }
                
                String header = template.substring(start, end);
                String footer = template.substring(startTerm, endTerm);                
                i = endTerm-1;
                //MetaBlock block = new MetaBlock(header, footer, template.substring(end, startTerm), data);
                MetaBlock block = MetaBlock.factory(header, footer, template.substring(end, startTerm), data);
                //content.put(start, block);
                contentAddress.add(start);
                contentMeta.add(block);
            } else if(template.substring(i, i+SYMBOL_LENGTH).equals(SYMBOL_OPEN) ) {
                //found SYMBOL_OPEN at i
                int start = i;
                //look for close
                int j = i+SYMBOL_LENGTH;
                boolean foundClose = false;
                while(j<N) {
                    if(template.substring(j, j+SYMBOL_LENGTH).equals(SYMBOL_CLOSE) ) {
                        foundClose = true;
                        break;                        
                    }
                    j++;
                }                
                if(!foundClose) {
                    throw new PreprocessException(start);
                }
                //found SYMBOL_CLOSE at j
                int end = j+SYMBOL_LENGTH;
                i = end-1;
                MetaSymbol symbol = new MetaSymbol(template.substring(start,end), data);
                //content.put(start, symbol);               
                contentAddress.add(start);
                contentMeta.add(symbol);
            }
        }
        
        
    }

    @Override
    protected int getTemplateLegth() {
        int ret = 0;
        if(this.blockHeader != null)
            ret += blockHeader.length();
        ret += template.length();
        if(this.blockFooter != null)
            ret += blockFooter.length();
        return ret;        
    }
    
    
    public static MetaBlock factory(String blockHeader, String blockFooter, String template, HashMap<String,String> data) throws PreprocessException {
        if(blockHeader == null) {
            return new MainMetaBlock(template, data);
        }
        
        if(blockHeader != null && blockFooter == null)
            throw new PreprocessException(-1);
        
        
        String headerCommand = blockHeader.substring(SYMBOL_LENGTH, blockHeader.length()-SYMBOL_LENGTH).trim();
        String footerCommand = blockFooter.substring(SYMBOL_LENGTH, blockFooter.length()-SYMBOL_LENGTH).trim();
        String[] headerCommandElements = headerCommand.split(" ");
        if(headerCommandElements == null || headerCommandElements[0] == null || footerCommand == null)
            throw new PreprocessException(-1);
            
        if(headerCommandElements[0].equalsIgnoreCase("for") && footerCommand.equalsIgnoreCase("endfor")) {
            return new LoopMetaBlock(blockHeader, blockFooter, template, data);
        }

        if(headerCommandElements[0].equalsIgnoreCase("tree") && footerCommand.equalsIgnoreCase("endtree")) {
            return new TreeMetaBlock(blockHeader, blockFooter, template, data);
        }
        
        
        throw new PreprocessException(-2);
    }
    
    

}
