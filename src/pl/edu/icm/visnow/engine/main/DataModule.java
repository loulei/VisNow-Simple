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

package pl.edu.icm.visnow.engine.main;

import pl.edu.icm.visnow.engine.core.Output;
import pl.edu.icm.visnow.engine.Engine;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.engine.core.VNData;

/**
 *
 * @author Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class DataModule extends ModuleBox {


    private Output output;
    //TODO: Output nie powinien być normalnym elementem.
    //Po pierwsze, tworzy niepotrzebny wątek i kolejkę.
    //Po drugie, musimy obchodzić cały system podłączający output do moduleBox i engine.
    //Może Output powinien być interfejsem?

    public DataModule(Engine engine, String name, VNData data) {
        super(engine, name, new DataCore());
//        this.data = data;
        this.output = new Output(new OutputEgg("data", data.getType()));
        output.setModuleBox(this);
        output.setValue(data.getValue());
        ((DataCore)this.getCore()).init(this);
    }

    public Output getOutput() {
        return output;
    }
}
