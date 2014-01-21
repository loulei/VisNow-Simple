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

package pl.edu.icm.visnow.lib.basic.readers.ReadImage;

import pl.edu.icm.visnow.engine.core.ParameterEgg;
import pl.edu.icm.visnow.engine.core.ParameterType;
import pl.edu.icm.visnow.engine.core.Parameters;
import pl.edu.icm.visnow.lib.utils.io.InputSource;

/**
 *
 * @author  Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class Params extends Parameters 
{

    @SuppressWarnings("unchecked")
    public Params() {
        super(eggs);
        float[] rgb = new float[] {0.30f, 0.59f, 0.11f};
        setValue("rgb", rgb);
    }


    
    private static ParameterEgg[] eggs = new ParameterEgg[]{
        new ParameterEgg<String[]>("fileNames", ParameterType.filename, null),
        new ParameterEgg<Boolean>("show", ParameterType.independent, true),
        new ParameterEgg<Integer>("type", ParameterType.independent, InputSource.FILE),
        new ParameterEgg<Boolean>("URL", ParameterType.independent, false),
        new ParameterEgg<Boolean>("grayscale", ParameterType.independent, false),
        new ParameterEgg<float[]>("rgb", ParameterType.filename, null),
        new ParameterEgg<Boolean>("sequenceMode", ParameterType.independent, false)        
    };

    public String[] getFiles() {
        return (String[])getValue("fileNames");
    }

    public void setFiles(String[] fileName) {
        setValue("fileNames", fileName);
        fireStateChanged();
    }

    public boolean isShow()
    {
       return (Boolean)getValue("show");
    }

    public void setShow(boolean show)
    {
       setValue("show", show);
    }
    
    public boolean isURL()
    {
       return (Boolean)getValue("URL");
    }

    public void setURL(boolean url)
    {
       setValue("URL", url);
    }

    public int getSource()
    {
       return (Integer)getValue("type");
    }

    public void setSource(int type)
    {
       setValue("type", type);
    }

    public boolean isGrayscale()
    {
       return (Boolean)getValue("grayscale");
    }

    public void setGrayscale(boolean grayscale)
    {
       setValue("grayscale", grayscale);
       fireStateChanged();
    }
    
    public boolean isSequenceMode()
    {
       return (Boolean)getValue("sequenceMode");
    }

    public void setSequenceMode(boolean sequenceMode)
    {
       setValue("sequenceMode", sequenceMode);
    }
    
    public float[] getRGBWeights() {
        return (float[])getValue("rgb");
    }

    public void setRGBWeights(float[] weights) {
        setValue("rgb", weights);
        fireStateChanged();
    }
    
}
