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

import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map.Entry;
import pl.edu.icm.visnow.application.application.Application;
import pl.edu.icm.visnow.engine.core.CoreName;
import pl.edu.icm.visnow.engine.commands.LibraryAddCommand;
import pl.edu.icm.visnow.engine.commands.LinkAddCommand;
import pl.edu.icm.visnow.engine.commands.ModuleAddCommand;
import pl.edu.icm.visnow.engine.core.LinkName;

/**
 *
 * @author Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class VNReader {


    public static Application readApplication(File file) {
        try {
            return tryReadApplication(file);
        } catch (FileNotFoundException ex) {
            System.out.println("ERR");
            return null;
        } catch (IOException e) {
            System.out.println("ERR");
            return null;
        }
    }

    private static boolean isDigit(char c) {
        return (c>='0')&&(c<='9');
    }

private static int getLastInt(String s) {
        //System.out.println("getLastInt["+s+"]");
        String ch = "";
        boolean started = false;
        int i = s.length()-1;
        while(!isDigit(s.charAt(i))) {
        //    System.out.println("i="+i);
            --i; if(i<0) return 0;
        }
        while(isDigit(s.charAt(i))) {
            ch = ""+s.charAt(i)+ch;
            --i; if(i<0) break;
        }
        return Integer.parseInt(ch);
    }


    private static Application tryReadApplication(File file) throws FileNotFoundException, IOException {
        
        BufferedReader reader = new BufferedReader(new FileReader(file));

        Application ret = new Application("read_app", true);
        String module="";
        while(true) {
            String next = reader.readLine();
            if(next == null) break;
            VNReaderNode node = VNReaderNode.read(next);

            if(node != null) {
                System.out.println("***********");
                System.out.println(node.type);
                for(Entry<String, String> e: node.data.entrySet()) {
                    System.out.println("["+e.getKey()+"] => ["+e.getValue() +"]");
                }
                switch(node.type) {
                    case application:
                        ret.setTitle(node.data.get("name"));
                        break;
                    case library:
                        ret.getReceiver().receive(new LibraryAddCommand(node.data.get("name")));
                        break;
                    case module:
                        module = node.data.get("name");
                        ret.getReceiver().receive(new ModuleAddCommand(
                                node.data.get("name"),
                                new CoreName(node.data.get("library"), node.data.get("class")),
                                new Point(Integer.parseInt(node.data.get("x")), Integer.parseInt(node.data.get("y")))
                                ));
                        ret.correctModuleCount(getLastInt(module));
                        break;
                    case link:
                        ret.getReceiver().receive(new LinkAddCommand(new LinkName(
                                node.data.get("from"),
                                node.data.get("out"),
                                node.data.get("to"),
                                node.data.get("in")
                                ),
                                false));
                        break;
                    case params:
                        String dat = "";
                        String lin = "";
                        while(!lin.startsWith("@end")) {
                            dat += lin+"\n";
                            lin = reader.readLine();
                        }
                        ret.getEngine().getModule(module).getParameters().readXML(dat);
                        ret.getEngine().getModule(module).startAction();
                        break;
                    default:
                        break;

                }
            }

        }

        return ret;
    }

   private VNReader()
   {
   }
}


