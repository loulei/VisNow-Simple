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

package pl.edu.icm.visnow.geometries.parameters;

import pl.edu.icm.visnow.datasets.RegularField;

/**
 *
 * @author Krzysztof Nowinski
 */
public class RegularFieldDisplayParams 
{

    protected DataMappingParams mappingParams;
    protected RenderingParams renderingParams;
    protected TransformParams transformParams;
    protected RegularField3dParams content3DParams;

   public RegularFieldDisplayParams(RegularField field)
   {
      if (field == null)
         return;
      mappingParams = new DataMappingParams(field);
      renderingParams = new RenderingParams();
      mappingParams.getTransparencyParams().addListener(renderingParams.getTransparencyChangeListener());
      transformParams = new TransformParams();
      if (field.getDims() != null && field.getDims().length == 3)
         content3DParams = new RegularField3dParams();
   }

   public RegularFieldDisplayParams(RegularField3dParams content3DParams, DataMappingParams mappingParams, RenderingParams displayParams, TransformParams transformParams)
   {
      this.content3DParams = content3DParams;
      this.mappingParams   = mappingParams;
      this.renderingParams   = displayParams;
      this.transformParams = transformParams;
   }

    /**
     * Get the value of content3DParams
     *
     * @return the value of content3DParams
     */
    public RegularField3dParams getContent3DParams() 
    {
        return content3DParams;
    }

    /**
     * Set the value of content3DParams
     *
     * @param content3DParams new value of content3DParams
     */
    public void setContent3DParams(RegularField3dParams content3DParams) 
    {
        this.content3DParams = content3DParams;
    }

    /**
     * Get the value of transformParams
     *
     * @return the value of transformParams
     */
    public TransformParams getTransformParams() 
    {
        return transformParams;
    }

    /**
     * Set the value of transformParams
     *
     * @param transformParams new value of transformParams
     */
    public void setTransformParams(TransformParams transformParams) 
    {
        this.transformParams = transformParams;
    }

    /**
     * Get the value of renderingParams
     *
     * @return the value of renderingParams
     */
    public RenderingParams getDisplayParams() 
    {
        return renderingParams;
    }

    /**
     * Set the value of renderingParams
     *
     * @param renderingParams new value of renderingParams
     */
    public void setDisplayParams(RenderingParams displayParams) 
    {
        this.renderingParams = displayParams;
    }

    /**
     * Get the value of mappingParams
     *
     * @return the value of mappingParams
     */
    public DataMappingParams getMappingParams() 
    {
        return mappingParams;
    }

    /**
     * Set the value of mappingParams
     *
     * @param mappingParams new value of mappingParams
     */
    public void setMappingParams(DataMappingParams mappingParams) 
    {
        this.mappingParams = mappingParams;
    }
    
    public void setActive(boolean active)
    {
       mappingParams.setActive(active);
    }
}
