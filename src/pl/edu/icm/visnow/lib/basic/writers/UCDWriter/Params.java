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

package pl.edu.icm.visnow.lib.basic.writers.UCDWriter;

import pl.edu.icm.visnow.engine.core.ParameterEgg;
import pl.edu.icm.visnow.engine.core.ParameterType;
import pl.edu.icm.visnow.engine.core.Parameters;
import pl.edu.icm.visnow.engine.core.Parameter;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class Params extends Parameters {

    private Parameter<String> fileName;
    private Parameter<Boolean> ascii;
    private Parameter<Boolean> AVS;

@SuppressWarnings("unchecked")
    private void initParameters() {
        fileName = getParameter("fileName");
        ascii = getParameter("ascii");
        AVS = getParameter("AVS");

        fileName.setValue("/tmp/test.inp");
        ascii.setValue(true);
        AVS.setValue(true);
    }

    private static ParameterEgg[] eggs = new ParameterEgg[]{
        new ParameterEgg<String>("fileName", ParameterType.dependent),
        new ParameterEgg<Boolean>("ascii", ParameterType.dependent),
        new ParameterEgg<Boolean>("AVS", ParameterType.dependent),
    };

    public Params() {
        super(eggs);
        initParameters();
    }

    public String getFileName() {
        return fileName.getValue();
    }

    public void setFileName(String fileName) {
        this.fileName.setValue(fileName);
    }
    public boolean isAscii() {
        return ascii.getValue();
    }

    public void setAscii(boolean ascii) {
        this.ascii.setValue(ascii);
    }
    public boolean isAVS() {
        return AVS.getValue();
    }

    public void setAVS(boolean AVS) {
        this.AVS.setValue(AVS);
    }

}
