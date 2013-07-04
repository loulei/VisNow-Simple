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

package pl.edu.icm.visnow.help.whatnew;

import java.io.File;
import java.io.FileReader;

/**
 *
 * @author  Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class WhatNewItem {

    private File file;
    public File getFile() {return file;}
    private String id;
    private String content;
    public String getAuthor() {
        return file.getName().substring(4);
    }
    public String getContent() {
        if(content == null) {
            try {
                FileReader reader = new FileReader(file);
                String s = "";
                for(int r = reader.read(); r != -1; r = reader.read()) {
                    s = s+(char)r;
                }
                content = s;
            } catch (Exception e) {}
        }
        return content;
    }
    public void renewContent() {
        content = null;
    }
    public WhatNewItem(File file) {
        this.file = file;
        String day = file.getName().substring(0, 3);
        String month = file.getParentFile().getName();
        String year = file.getParentFile().getParentFile().getName().substring(1);
    
        id = "20" + year + "/"+ month.substring(1) + "/" + day.substring(1);
        content = null;
    }
    public int toInt() {
        int d = Integer.parseInt(file.getName().substring(1, 2));
        int m = Integer.parseInt(file.getParentFile().getName().substring(1));
        int y = Integer.parseInt(file.getParentFile().getParentFile().getName().substring(1));
        return 32*32*y+32*m+d;
    }
    @Override
    public String toString() {
        return id;
    }
}
