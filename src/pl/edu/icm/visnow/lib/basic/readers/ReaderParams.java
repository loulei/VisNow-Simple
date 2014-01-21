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

package pl.edu.icm.visnow.lib.basic.readers;

import java.nio.ByteOrder;
import static java.nio.ByteOrder.*;
import java.util.Arrays;
import pl.edu.icm.visnow.engine.core.ParameterEgg;
import pl.edu.icm.visnow.engine.core.ParameterType;
import pl.edu.icm.visnow.engine.core.Parameters;
import pl.edu.icm.visnow.lib.utils.io.InputSource;

/**
 *
 * @author  Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class ReaderParams extends Parameters 
{

    @SuppressWarnings("unchecked")
    public ReaderParams() {
        super(eggs);
    }

    private static ParameterEgg[] concatenate(ParameterEgg[] additional) {
        ParameterEgg[] tab = Arrays.copyOf(eggs, ReaderParams.eggs.length + additional.length);
        for(int i=0; i<additional.length; ++i) {
            tab[ReaderParams.eggs.length+i] = additional[i];
        }
        return tab;
    }

    @SuppressWarnings("unchecked")
    public ReaderParams(ParameterEgg[] additional) {
        super(concatenate(additional));
    }
    
    private static ParameterEgg[] eggs = new ParameterEgg[]{
        new ParameterEgg<String>("fileName", ParameterType.filename, ""),
        new ParameterEgg<String[]>("fileNames", ParameterType.filename, null),
        new ParameterEgg<ByteOrder>("endian", ParameterType.independent, BIG_ENDIAN),
        new ParameterEgg<Boolean>("show", ParameterType.independent, true),
        new ParameterEgg<Integer>("input source", ParameterType.independent, InputSource.FILE)
    };

    public String getFileName() {
        return (String)getValue("fileName");
    }

    public void setFileName(String fileName) {
        setValue("fileName", fileName);
    }

    public String[] getFileNames() {
        return (String[])getValue("fileNames");
    }

    public void setFileNames(String[] fileNames) {
        setValue("fileNames", fileNames);
    }
    
    public boolean isShow()
    {
       return (Boolean)getValue("show");
    }

    public void setShow(boolean show)
    {
       setValue("show", show);
    }
    
    public int getSource()
    {
       return (Integer)getValue("input source");
    }

    public void setSource(int source)
    {
       setValue("input source", source);
    }
    public ByteOrder getEndian()
    {
       return (ByteOrder)getValue("endian");
    }

    public void setEndian(ByteOrder endian)
    {
       setValue("endian", endian);
    }
}
