//<editor-fold defaultstate="collapsed" desc=" COPYRIGHT AND LICENSE ">
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
//</editor-fold>

package pl.edu.icm.visnow.geometries.objects;

import pl.edu.icm.visnow.datamaps.colormap1d.DefaultColorMap1D;
import pl.edu.icm.visnow.geometries.objects.generics.OpenBranchGroup;
import pl.edu.icm.visnow.geometries.parameters.DataMappingParams;

/**
 *
 * @author Krzysztof S. Nowinski, Warsaw University, ICM
 *
 */
public class DataMappedGeometryObject extends GeometryObject
{
   public static final int SURFACE  = 1;
   public static final int EDGES    = 2;
   public static final int EXTEDGES = 4;

   protected boolean structureChanged  = true;
   protected boolean dataChanged       = true;
   protected boolean rangeChanged       = true;
   protected boolean isTextureChanged  = true;
   protected boolean coordsChanged     = true;
   protected boolean colorsChanged     = true;
   protected boolean uvCoordsChanged   = true;
   protected boolean debug             = false;
   protected boolean newParams         = true;

   protected int nNodes           = 0;
   protected int nTriangleIndices = 0;
   protected int nLineIndices     = 0;
   protected int[] coordIndices   = null;
   protected int[] colorIndices   = null;
   protected float[] coords       = null;
   protected float[] normals      = null;
   protected float[] uvData       = null;
   protected byte[] colors        = null;
   protected DefaultColorMap1D colorMap    = null;
   protected DataMappingParams dataMappingParams = new DataMappingParams();
   protected ColormapLegend colormapLegend       = new ColormapLegend();

   /**
    * Transient place holder for all geometries created by the module - detached
    * and re-attached for all structural changes
    */
   protected OpenBranchGroup geometry = new OpenBranchGroup();

   /** Creates a new instance of DataMappedGeometryObject */
   public DataMappedGeometryObject()
   {
      colormapLegend.setParams(dataMappingParams.getColormapLegendParameters());
      dataMappingParams.getTransparencyParams().addListener(renderingParams.getTransparencyChangeListener());
      addGeometry2D(colormapLegend);
   }

   public DataMappedGeometryObject(String name)
   {
      super(name);
      colormapLegend.setParams(dataMappingParams.getColormapLegendParameters());
      dataMappingParams.getTransparencyParams().addListener(renderingParams.getTransparencyChangeListener());
      addGeometry2D(colormapLegend);
   }

   public DataMappedGeometryObject(String name, int timestamp)
   {
      super(name, timestamp);
      colormapLegend.setParams(dataMappingParams.getColormapLegendParameters());
      dataMappingParams.getTransparencyParams().addListener(renderingParams.getTransparencyChangeListener());
      addGeometry2D(colormapLegend);
   }

   public DataMappingParams getDataMappingParams()
   {
      return dataMappingParams;
   }

   public void setDataMappingParams(DataMappingParams dataMappingParams)
   {
      this.dataMappingParams = dataMappingParams;
      dataMappingParams.getTransparencyParams().addListener(renderingParams.getTransparencyChangeListener());
      colormapLegend.setParams(dataMappingParams.getColormapLegendParameters());
   }

   public ColormapLegend getColormapLegend()
   {
      return colormapLegend;
   }

    /**
     * @return the newParams
     */
    public boolean isNewParams() {
        return newParams;
    }

    public boolean isRangeChanged() {
        return rangeChanged;
    }

    public boolean isDataChanged() {
        return dataChanged;
    }

    public boolean isStructureChanged() {
        return structureChanged;
    }
    
}
