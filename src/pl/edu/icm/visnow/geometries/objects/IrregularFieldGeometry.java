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
package pl.edu.icm.visnow.geometries.objects;

import javax.media.j3d.MultipleParentException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.log4j.Logger;
import pl.edu.icm.visnow.datasets.CellSet;
import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.datasets.IrregularField;
import pl.edu.icm.visnow.geometries.objects.generics.OpenBranchGroup;
import pl.edu.icm.visnow.geometries.parameters.IrregularFieldDisplayParams;

/**
 *
 * @author Krzysztof S. Nowinski
 * <p>
 * University of Warsaw, ICM
 */
public class IrregularFieldGeometry extends FieldGeometry
{

   protected IrregularFieldDisplayParams fieldDisplayParams = null;
   protected CellSetGeometry[] cellSetGeometries = null;
   protected IrregularField field = null;
   static Logger logger = Logger.getLogger(IrregularFieldGeometry.class);
   protected ColormapLegend fldColormapLegend = colormapLegend;

   public IrregularFieldGeometry(String name)
   {
      super(name);
      transformedGeometries.addChild(geometries);
      geometry.addChild(transformedGeometries);
   }

   public IrregularFieldGeometry()
   {
      super();
      transformedGeometries.addChild(geometries);
      geometry.addChild(transformedGeometries);
   }

   public IrregularFieldGeometry(IrregularField inField, IrregularFieldDisplayParams fieldDisplayParams)
   {
      if (inField == null || inField.getNNodes() < 1 || inField.getNCellSets() != fieldDisplayParams.getNCellSetDisplayParameters())
         return;
      field = inField;
      this.fieldDisplayParams = fieldDisplayParams;
      name = inField.getName();
      transformedGeometries.addChild(geometries);
      geometry.addChild(transformedGeometries);
      clearAllGeometry();
      updateCellSetGeometries();
      transformParams = fieldDisplayParams.getTransformParams();
      fieldDisplayParams.getTransformParams().addChangeListener(new ChangeListener()
      {
         @Override
         public void stateChanged(ChangeEvent evt)
         {
            transformedGeometries.setTransform(transformParams.getTransform());
         }
      });
      transformedGeometries.setTransform(transformParams.getTransform());
   }

   private void updateCellSetGeometries()
   {
      boolean detach = geometry.postdetach();
      cellSetGeometries = new CellSetGeometry[field.getNCellSets()];
      for (int iSet = 0; iSet < field.getNCellSets(); iSet++)
      {
         CellSet cSet = field.getCellSet(iSet);
         cellSetGeometries[iSet] = new CellSetGeometry(cSet.getName());
         cellSetGeometries[iSet].setInData(field, cSet);
         fieldDisplayParams.getCellSetDisplayParameters(iSet).setActive(true);
         cellSetGeometries[iSet].setParams(fieldDisplayParams.getCellSetDisplayParameters(iSet));
         geometries.addChild(cellSetGeometries[iSet]);
         addBgrColorListener(cellSetGeometries[iSet].getBgrColorListener());
      }
      if (detach)
         geometry.postattach();
   }

   public boolean setField(IrregularField inField)
   {
      if (inField == null || inField.getNNodes() < 1)
         return false;
      structureChanged = (field == null || !inField.isStructureCompatibleWith(this.field));
      dataChanged = (field == null || !inField.isDataCompatibleWith(this.field));
      this.field = inField;
      name = inField.getName();
      newParams = false;
      if (structureChanged || dataChanged)
      {
         if (dataChanged)
         {
            fieldDisplayParams = new IrregularFieldDisplayParams(field);
            newParams = true;
         }
         if (cellSetGeometries != null)
            for (int iSet = 0; iSet < cellSetGeometries.length; iSet++)
               cellSetGeometries[iSet].getDataMappingParams().clearRenderEventListeners();
         clearAllGeometry();
         geometries.removeAllChildren();
         updateCellSetGeometries();
         transformParams = fieldDisplayParams.getTransformParams();
         colormapLegend.setParams(dataMappingParams.getColormapLegendParameters());
         fieldDisplayParams.getTransformParams().addChangeListener(new ChangeListener()
         {
            public void stateChanged(ChangeEvent evt)
            {
               transformedGeometries.setTransform(transformParams.getTransform());
            }
         });
         transformedGeometries.setTransform(transformParams.getTransform());
         if (field.getNCellSets() == 1)
            colormapLegend = cellSetGeometries[0].getColormapLegend();
         else
            colormapLegend = fldColormapLegend;
      } else
         for (int iSet = 0; iSet < field.getNCellSets(); iSet++)
            cellSetGeometries[iSet].setInData(field, field.getCellSet(iSet));

      return true;
   }

   public boolean setField(IrregularField inField, boolean createParams)
   {
      if (createParams)
         return setField(inField);
      if (inField == null || inField.getNNodes() < 1)
         return false;
      this.field = inField;
      name = inField.getName();
      newParams = false;
      return true;
   }

   @Override
   public void setIgnoreUpdate(boolean ignoreUpdate)
   {
      this.ignoreUpdate = ignoreUpdate;
      for (CellSetGeometry cellSetGeometrie : cellSetGeometries)
         cellSetGeometrie.setIgnoreUpdate(ignoreUpdate);
   }

   public CellSetGeometry getCellSetGeometry(int i)
   {
      if (cellSetGeometries != null && i >= 0 && i < cellSetGeometries.length)
         return cellSetGeometries[i];
      return null;
   }

   @Override
   public void updateCoords(float[] coords)
   {
      boolean detach = geometry.postdetach();
      this.coords = coords;
      if (field != null)
         for (int i = 0; i < field.getNCellSets(); i++)
            cellSetGeometries[i].updateCoords(coords);
      if (detach)
         geometry.postattach();
   }

   @Override
   public void updateCoords(boolean force)
   {
      if (field == null || !force)
         return;
      boolean detach = geometry.postdetach();
      coords = field.getCoords();
      for (int i = 0; i < field.getNCellSets(); i++)
         cellSetGeometries[i].updateCoords(force);
      if (detach)
         geometry.postattach();
   }

   @Override
   public void updateCoords()
   {
      if (field == null || ignoreUpdate)
         return;
      boolean detach = geometry.postdetach();
      coords = field.getCoords();
      for (int i = 0; i < field.getNCellSets(); i++)
         cellSetGeometries[i].updateCoords();
      if (detach)
         geometry.postattach();
   }

   public void updateTextureCoords()
   {
      if (field != null)
      {
         boolean detach = geometry.postdetach();
         for (int i = 0; i < field.getNCellSets(); i++)
            cellSetGeometries[i].updateTextureCoords();
         if (detach)
            geometry.postattach();
      }
   }

   public void updateDataMap()
   {
      if (field != null)
      {
         boolean detach = geometry.postdetach();
         for (int i = 0; i < field.getNCellSets(); i++)
            cellSetGeometries[i].updataDataMap();
         if (detach)
            geometry.postattach();
      }
   }

   public void updateColors()
   {
      if (field != null)
      {
         boolean detach = geometry.postdetach();
         for (int i = 0; i < field.getNCellSets(); i++)
            cellSetGeometries[i].updateColors();
         if (detach)
            geometry.postattach();
      }
   }

   @Override
   public void createGeometry(Field inField)
   {
      updateGeometry();
   }

   @Override
   public OpenBranchGroup getGeometry()
   {
      updateGeometry();
      return geometry;
   }

   @Override
   public void updateGeometry()
   {
      if (ignoreUpdate || field == null)
         return;

      boolean detach = geometry.postdetach();
      try
      {
         transformedGeometries.removeAllChildren();
      } catch (Exception e)
      {
      }
      if (cellSetGeometries != null)
         for (int i = 0; i < field.getNCellSets(); i++)
            if (cellSetGeometries[i] != null)
               cellSetGeometries[i].updateGeometry();
      if (geometries.getParent() != null)
         geometries.detach();
      try
      {
         transformedGeometries.addChild(geometries);
      } catch (MultipleParentException e)
      {
         logger.error("multiple parent in " + this + " updateGeometry");
      }
      logger.debug("updating - re-attached");
      if (detach)
         geometry.postattach();
   }

   @Override
   public OpenBranchGroup getGeometry(Field inField)
   {
      return null;
   }

   @Override
   public void updateGeometry(Field inField)
   {
   }

   public IrregularFieldDisplayParams getFieldDisplayParams()
   {
      newParams = false;
      return fieldDisplayParams;
   }

   public ColormapLegend getColormapLegend(int i)
   {
      if (cellSetGeometries == null || i < 0 || i >= cellSetGeometries.length || cellSetGeometries[i] == null)
         return null;
      return cellSetGeometries[i].getColormapLegend();
   }

   @Override
   public boolean setField(Field inField, boolean createParams)
   {
      if (inField == null || !(inField instanceof IrregularField))
         return false;
      return setField((IrregularField) inField, createParams);
   }

   @Override
   public boolean setField(Field inField)
   {
      if (inField == null || !(inField instanceof IrregularField))
         return false;
      return setField((IrregularField) inField);
   }

   public void updateCellSetData(int n)
   {
      if (n >= 0 && n < field.getNCellSets())
      {
         cellSetGeometries[n].setName(field.getCellSet(n).getName());
         cellSetGeometries[n].setPicked(field.getCellSet(n).isSelected());
      }
   }

   @Override
   public Field getField()
   {
      return field;
   }

}
