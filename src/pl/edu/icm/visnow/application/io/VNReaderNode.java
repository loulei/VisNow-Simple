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

package pl.edu.icm.visnow.application.io;

import java.util.HashMap;
import java.util.Vector;

/**
 *
 * @author Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class VNReaderNode {

//    public final static String application = "application";
//    public final static String library = "module";
//    public final static String module = "module";
//    public final static String params = "params";
//    public final static String end = "end";
//    public final static String link = "link";
//
//    public final static String[] tab = {};


    private String typeString;
    public VNReaderNodeType type;
    public HashMap<String, String> data = new HashMap<String, String>();

    public static VNReaderNode read(String s) {
        if(s.length()==0) return null;
        String t = s;
        while(t.charAt(0) != '@') {
            t = t.substring(1);
            if(t.length()==0) return null;
        }


        VNReaderNode node = new VNReaderNode();

        Vector<String> vec = new Vector<String>();
        String delim = "<>";
        String whitespace = " \t";

        t = t.substring(1)+"  ";
        String cur = "";

        while(t.length()>0) {
            char next = t.charAt(0);
            t = t.substring(1);
            if(whitespace.indexOf(next)==-1) {
                cur = cur+next;
            } else {
                node.typeString = cur;
                break;
            }
        }

        cur = "";
        boolean inside = false;
        
        while(t.length()>0) {
            char next = t.charAt(0);
            if(delim.indexOf(next)==-1) {
                if(inside || whitespace.indexOf(next)==-1)
                cur = cur+next;
            } else if(cur.length()>0) {
                vec.add(cur);
                inside = !inside;
                cur = "";
            }
            t = t.substring(1);
        }

        
        while(vec.size()>1) {
            node.data.put(vec.get(0), vec.get(1));
            vec.remove(0);
            vec.remove(0);
        }

        for(VNReaderNodeType type: VNReaderNodeType.values()) {
            if(node.typeString.equalsIgnoreCase(type.name())) {
                node.type = type;
                return node;
            }
        }

        return null;
    }
}
