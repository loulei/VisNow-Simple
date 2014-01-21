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

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.media.j3d.Texture2D;
import javax.media.j3d.TransparencyAttributes;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.Color3f;
import pl.edu.icm.visnow.datasets.IrregularField;
import pl.edu.icm.visnow.geometries.objects.generics.OpenAppearance;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.render.RenderEvent;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.render.RenderEventListener;

/**
 *
 * @author Krzysztof S. Nowinski
 *   <p>   University of Warsaw, ICM
 */
public class IrregularFieldDisplayParams implements AbstractDataMappingParams, AbstractRenderingParams
{
   protected CellSetDisplayParams[] cellSetDisplayParameters;
   protected TransformParams globalTransformParams;
   protected DataMappingParams globalMappingParams = new DataMappingParams();
   protected ColormapLegendParameters colormapLegendParameters = globalMappingParams.getColormapLegendParameters();
   protected RenderingParams globalDisplayParams = new RenderingParams();
   protected TransparencyParams globalTransparencyParams = new TransparencyParams();

   protected boolean active = true;

   public IrregularFieldDisplayParams(IrregularField inField)
   {
      cellSetDisplayParameters = new CellSetDisplayParams[inField.getNCellSets()];
      for (int i = 0; i < inField.getNCellSets(); i++)
         cellSetDisplayParameters[i] = new CellSetDisplayParams(inField, inField.getCellSet(i));
      globalTransformParams = new TransformParams();
      globalMappingParams.addRenderEventListener(new RenderEventListener()
      {
         @Override
         public void renderExtentChanged(RenderEvent e)
         {
            for (int i = 0; i < cellSetDisplayParameters.length; i++)
               if (cellSetDisplayParameters[i].isInheriting())
                  cellSetDisplayParameters[i].getDataMappingParams().copy(globalMappingParams);
         }
      });
   }

   /**
    * Get the value of cellSetDisplayParameters
    *
    * @return the value of cellSetDisplayParameters
    */
   public CellSetDisplayParams[] getCellSetDisplayParameters()
   {
      return cellSetDisplayParameters;
   }

   /**
    * Set the value of cellSetDisplayParameters
    *
    * @param cellSetDisplayParameters new value of cellSetDisplayParameters
    */
   public void setCellSetDisplayParameters(CellSetDisplayParams[] cellSetDisplayParameters)
   {
      this.cellSetDisplayParameters = cellSetDisplayParameters;
   }

   /**
    * Get the value of cellSetDisplayParameters at specified index
    *
    * @param index
    * @return the value of cellSetDisplayParameters at specified index
    */
   public CellSetDisplayParams getCellSetDisplayParameters(int index)
   {
      if (cellSetDisplayParameters == null || 
          cellSetDisplayParameters.length == 0 ||
          index < 0 || index >= cellSetDisplayParameters.length)
         return null;
      return cellSetDisplayParameters[index];
   }

   /**
    * Set the value of cellSetDisplayParameters at specified index.
    *
    * @param index
    * @param newCellSetDisplayParameters new value of cellSetDisplayParameters at specified index
    */
   public void setCellSetDisplayParameters(int index, CellSetDisplayParams newCellSetDisplayParameters)
   {
      this.cellSetDisplayParameters[index] = newCellSetDisplayParameters;
   }

   public void spreadParams()
   {
      for (int i = 0; i < cellSetDisplayParameters.length; i++)
      {
         cellSetDisplayParameters[i].setActive(false);
         cellSetDisplayParameters[i].getDataMappingParams().copy(globalMappingParams);
         cellSetDisplayParameters[i].getRenderingParams().copy((RenderingParams)globalDisplayParams);
         cellSetDisplayParameters[i].getTransformParams().copy(globalTransformParams);
         cellSetDisplayParameters[i].setActive(true);
      }
   }

   public TransformParams getTransformParams()
   {
      return globalTransformParams;
   }

   public void setTransformParams(TransformParams transformParams)
   {
      this.globalTransformParams = transformParams;
   }

   public int getNCellSetDisplayParameters()
   {
      return cellSetDisplayParameters.length;
   }

   public void clearActiveParams()
   {
      for (int i = 0; i < cellSetDisplayParameters.length; i++)
         cellSetDisplayParameters[i].setActive(false);
   }

   /* following methods set rendering parameters for all cell sets -
    * these are convenience methods for simple, cell set independent GUIs
    */
   @Override
   public void setDisplayMode(int displayMode)
   {
      for (CellSetDisplayParams cellSetDisplayParams : cellSetDisplayParameters)
         cellSetDisplayParams.getRenderingParams().displayMode = displayMode;
      globalDisplayParams.setDisplayMode(displayMode);
      fireStateChanged();
   }

   @Override
   public void setCellDataMapped(boolean cellDataMapped)
   {
      for (CellSetDisplayParams cellSetDisplayParams : cellSetDisplayParameters)
         cellSetDisplayParams.getDataMappingParams().cellDataMapped = cellDataMapped;
      globalMappingParams.setCellDataMapped(cellDataMapped);
      fireStateChanged();
   }

   @Override
   public void setTexture(Texture2D texture)
   {
      for (CellSetDisplayParams cellSetDisplayParams : cellSetDisplayParameters)
         cellSetDisplayParams.getDataMappingParams().texture = texture;
      globalMappingParams.setTexture(texture);
      fireStateChanged();
   }

   public void setModeChanged(int modeChanged)
   {
      for (CellSetDisplayParams cellSetDisplayParams : cellSetDisplayParameters)
         cellSetDisplayParams.getDataMappingParams().modeChanged = modeChanged;
      globalMappingParams.setModeChanged(modeChanged);
      fireStateChanged();
   }

   public void setColorModeChanged(int colorModeChanged)
   {
      for (CellSetDisplayParams cellSetDisplayParams : cellSetDisplayParameters)
         cellSetDisplayParams.getDataMappingParams().colorModeChanged = colorModeChanged;
      globalMappingParams.setColorModeChanged(colorModeChanged);
      fireStateChanged();
   }

   public void setColormapLegendColor(Color colormapLegendColor)
   {
      for (CellSetDisplayParams cellSetDisplayParams : cellSetDisplayParameters)
         cellSetDisplayParams.getDataMappingParams().colormapLegendParameters.setColor(colormapLegendColor);
      fireStateChanged();
   }

   public void setColormapLegendH(float colormapLegendH)
   {
      for (CellSetDisplayParams cellSetDisplayParams : cellSetDisplayParameters)
         cellSetDisplayParams.getDataMappingParams().colormapLegendParameters.setL(colormapLegendH);
      fireStateChanged();
   }

   public void setColormapLegendPosition(int colormapLegendPosition)
   {
      for (CellSetDisplayParams cellSetDisplayParams : cellSetDisplayParameters)
         cellSetDisplayParams.getDataMappingParams().colormapLegendParameters.setPosition(colormapLegendPosition);
      fireStateChanged();
   }

   public void setColormapLegendW(float colormapLegendW)
   {
      for (CellSetDisplayParams cellSetDisplayParams : cellSetDisplayParameters)
         cellSetDisplayParams.getDataMappingParams().colormapLegendParameters.setW(colormapLegendW);
      fireStateChanged();
   }


   @Override
   public void setColor(Color color)
   {
      float[] fC = new float[3];
      fC = new float[3];
      color.getColorComponents(fC);
      for (CellSetDisplayParams cellSetDisplayParams : cellSetDisplayParameters)
         cellSetDisplayParams.getRenderingParams().appearance.getColoringAttributes().setColor(new Color3f(fC));
      setAmbientColor(new Color3f(fC));
      setDiffuseColor(new Color3f(fC));
      setSpecularColor(new Color3f(fC));
      globalDisplayParams.setColor(color);
      fireStateChanged();
   }

   /**
    * Setter for property ambientColor.
    * @param ambientColor New value of property fColor.
    */
   @Override
   public void setAmbientColor(Color3f ambientColor)
   {
      for (CellSetDisplayParams cellSetDisplayParams : cellSetDisplayParameters)
         if (cellSetDisplayParams.getRenderingParams().isInherited())
            cellSetDisplayParams.getRenderingParams().appearance.getMaterial().setAmbientColor(ambientColor);
      globalDisplayParams.setAmbientColor(ambientColor);
      fireStateChanged();
   }

   /**
    * Setter for property diffuseColor.
    * @param diffuseColor New value of property fColor.
    */
   @Override
   public void setDiffuseColor(Color3f diffuseColor)
   {
      for (CellSetDisplayParams cellSetDisplayParams : cellSetDisplayParameters)
         if (cellSetDisplayParams.getRenderingParams().isInherited())
            cellSetDisplayParams.getRenderingParams().appearance.getMaterial().setDiffuseColor(diffuseColor);
      globalDisplayParams.setDiffuseColor(diffuseColor);
      fireStateChanged();
   }

   /**
    * Setter for property specularColor.
    * @param specularColor New value of property fColor.
    */
   @Override
   public void setSpecularColor(Color3f specularColor)
   {
      for (CellSetDisplayParams cellSetDisplayParams : cellSetDisplayParameters)
         if (cellSetDisplayParams.getRenderingParams().isInherited())
            cellSetDisplayParams.getRenderingParams().appearance.getMaterial().setSpecularColor(specularColor);
      globalDisplayParams.setSpecularColor(specularColor);
      fireStateChanged();
   }

   @Override
   public Color getBackgroundColor()
   {
      return globalDisplayParams.getBackgroundColor();
   }

   @Override
   public void setBackgroundColor(Color backgroundColor)
   {
      for (CellSetDisplayParams cellSetDisplayParams : cellSetDisplayParameters)
         if (cellSetDisplayParams.getRenderingParams().isInherited())
            cellSetDisplayParams.getRenderingParams().setBackgroundColor(backgroundColor);
      globalDisplayParams.setBackgroundColor(backgroundColor);
      fireStateChanged();
   }

   /**
    * Setter for property transparency.
    * @param transparency New value of property transparency.
    */
   @Override
   public void setTransparency(float transparency)
   {
      for (CellSetDisplayParams cellSetDisplayParams : cellSetDisplayParameters)
         if (cellSetDisplayParams.getRenderingParams().isInherited())
            {
            if (transparency == 0)
               cellSetDisplayParams.getRenderingParams().appearance.getTransparencyAttributes().setTransparencyMode(TransparencyAttributes.NONE);
            else
               cellSetDisplayParams.getRenderingParams().appearance.getTransparencyAttributes().setTransparencyMode(TransparencyAttributes.NICEST);
            cellSetDisplayParams.getRenderingParams().appearance.getTransparencyAttributes().setTransparency(transparency);
            }
      globalDisplayParams.setTransparency(transparency);
      fireStateChanged();
   }

   /**
    * Setter for property shininess.
    * @param shininess New value of property shininess.
    */
   @Override
   public void setShininess(float shininess)
   {
      for (CellSetDisplayParams cellSetDisplayParams : cellSetDisplayParameters)
         if (cellSetDisplayParams.getRenderingParams().isInherited())
            cellSetDisplayParams.getRenderingParams().appearance.getMaterial().setShininess(shininess);
      globalDisplayParams.setShininess(shininess);
      fireStateChanged();
   }

   @Override
   public void setCullMode(int cullFace)
   {
      for (CellSetDisplayParams cellSetDisplayParams : cellSetDisplayParameters)
         if (cellSetDisplayParams.getRenderingParams().isInherited())
            cellSetDisplayParams.getRenderingParams().appearance.getPolygonAttributes().setCullFace(cullFace);
      fireStateChanged();
   }

   /**
    * Setter for property annoFont.
    * @param annoFont New value of property annoFont.
    */
   @Override
   public void setAnnoFont(Font annoFont)
   {
      for (CellSetDisplayParams cellSetDisplayParams : cellSetDisplayParameters)
         if (cellSetDisplayParams.getRenderingParams().isInherited())
            cellSetDisplayParams.getRenderingParams().annoFont = annoFont;
      fireStateChanged();
      globalDisplayParams.setAnnoFont(annoFont);
   }

   /**
    * Setter for property lineThickness.
    * @param lineThickness New value of property lineThickness.
    */
   @Override
   public void setLineThickness(float lineThickness)
   {
      for (CellSetDisplayParams cellSetDisplayParams : cellSetDisplayParameters)
         if (cellSetDisplayParams.getRenderingParams().isInherited())
            cellSetDisplayParams.getRenderingParams().setLineThickness(lineThickness);
   }
   
   @Override
   public boolean isLineLighting()
   {
      return globalDisplayParams.isLineLighting();
   }
   
   @Override
   public void setLineLighting(boolean lineLighting)
   {
      for (CellSetDisplayParams cellSetDisplayParams : cellSetDisplayParameters)
         if (cellSetDisplayParams.getRenderingParams().isInherited())
            cellSetDisplayParams.getRenderingParams().setLineLighting(lineLighting);
      globalDisplayParams.setLineLighting(lineLighting);
   }

   
   @Override
   public boolean ignoreMask()
   {
      return globalDisplayParams.ignoreMask();
   }
   
   @Override
   public void setIgnoreMask(boolean ignoreMask)
   {
      for (CellSetDisplayParams cellSetDisplayParams : cellSetDisplayParameters)
         if (cellSetDisplayParams.getRenderingParams().isInherited())
            cellSetDisplayParams.getRenderingParams().setIgnoreMask(ignoreMask);
      globalDisplayParams.setLineLighting(ignoreMask);
   }
   @Override
   public void setLineStyle(int lineStyle)
   {
      for (CellSetDisplayParams cellSetDisplayParams : cellSetDisplayParameters)
         cellSetDisplayParams.getRenderingParams().setLineStyle(lineStyle);
   }

   @Override
   public boolean getSurfaceOrientation()
   {
      return false;
   }

   @Override
   public void setSurfaceOrientation(boolean orientation)
   {
      for (CellSetDisplayParams cellSetDisplayParams : cellSetDisplayParameters)
         if (cellSetDisplayParams.getRenderingParams().isInherited())
            cellSetDisplayParams.getRenderingParams().setSurfaceOrientation(orientation);
   }


   /**
    * Setter for property annoColor.
    * @param annoColor New value of property annoColor.
    */
   @Override
   public void setAnnoColor(Color annoColor)
   {
      for (CellSetDisplayParams cellSetDisplayParams : cellSetDisplayParameters)
         cellSetDisplayParams.getRenderingParams().annoColor = annoColor;
      fireStateChanged();
      globalDisplayParams.setAnnoColor(annoColor);
   }

   @Override
   public void setColorf(Color3f colorf)
   {
      setColor(colorf.get());
      fireStateChanged();
   }

   @Override
   public Object clone()
   {
      try
      {
         return super.clone();
      } catch (CloneNotSupportedException ex)
      {
         return null;
      }
   }
   
   /**
    * Utility field holding list of ChangeListeners.
    */
   protected transient ArrayList<ChangeListener> changeListenerList = new ArrayList<ChangeListener>();

   /**
    * Registers ChangeListener to receive events.
    * @param listener The listener to register.
    */
   public synchronized void addChangeListener(ChangeListener listener)
   {
      if(!changeListenerList.contains(listener)) 
            changeListenerList.add(listener);
   }

   /**
    * Removes ChangeListener from the list of listeners.
    * @param listener The listener to remove.
    */
   public synchronized void removeChangeListener(ChangeListener listener)
   {
      changeListenerList.remove(listener);
   }

   public boolean isActive()
   {
      return active;
   }

   @Override
   public void setActive(boolean active)
   {
      this.active = active;
      for (CellSetDisplayParams cellSetDisplayParams : cellSetDisplayParameters)
         cellSetDisplayParams.setActive(active);
      fireStateChanged();
   }

   /**
    * Notifies all registered listeners about the event.
    *
    */
   public void fireStateChanged()
   {
      if (active)
      {
         ChangeEvent e = new ChangeEvent(this);
         for (ChangeListener listener : changeListenerList)
            listener.stateChanged(e);
      }
   }

   @Override
   public ColormapLegendParameters getColormapLegendParameters()
   {
      return colormapLegendParameters;
   }

   @Override
   public boolean isCellDataMapped()
   {
      return globalMappingParams.isCellDataMapped();
   }

   @Override
   public int getColorMode()
   {
      return globalMappingParams.getColorMode();
   }

   @Override
   public void addRenderEventListener(RenderEventListener listener)
   {
       globalMappingParams.addRenderEventListener(listener);
   }

   @Override
   public void removeRenderEventListener(RenderEventListener listener)
   {
      globalMappingParams.removeRenderEventListener(listener);
   }

   @Override
   public void fireStateChanged(int change)
   {
      
   }

   @Override
   public Color3f getAmbientColor()
   {
      return globalDisplayParams.getAmbientColor();
   }

   @Override
   public Color getAnnoColor()
   {
      return globalDisplayParams.getAnnoColor();
   }

   @Override
   public Font getAnnoFont()
   {
      return globalDisplayParams.getAnnoFont();
   }

   @Override
   public OpenAppearance getAppearance()
   {
      return globalDisplayParams.getAppearance();
   }

   @Override
   public OpenAppearance getLineAppearance()
   {
      return globalDisplayParams.getLineAppearance();
   }

   @Override
   public Color getColor()
   {
      return globalDisplayParams.getColor();
   }

   @Override
   public Color3f getColorf()
   {
      return globalDisplayParams.getColorf();
   }

   @Override
   public Color3f getDiffuseColor()
   {
      return globalDisplayParams.getDiffuseColor();
   }

   @Override
   public int getDisplayMode()
   {
      return globalDisplayParams.getDisplayMode();
   }

   @Override
   public int getLineStyle()
   {
      return globalDisplayParams.getLineStyle();
   }

   @Override
   public float getLineThickness()
   {
      return globalDisplayParams.getLineThickness();
   }

   @Override
   public float getShininess()
   {
      return globalDisplayParams.getShininess();
   }

   @Override
   public Color3f getSpecularColor()
   {
      return globalDisplayParams.getSpecularColor();
   }

   @Override
   public float getTransparency()
   {
      return globalDisplayParams.getTransparency();
   }

   @Override
   public void setAppearance(OpenAppearance appearance)
   {
      globalDisplayParams.setAppearance(appearance);
   }

   @Override
   public void setLineAppearance(OpenAppearance appearance)
   {
      globalDisplayParams.setLineAppearance(appearance);
   }

   @Override
   public void setParentParams(AbstractRenderingParams parentParams)
   {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public int getShadingMode()
   {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public void setShadingMode(int shadingMode)
   {
      for (CellSetDisplayParams cellSetDisplayParams : cellSetDisplayParameters)
         if (cellSetDisplayParams.isInheriting())
            cellSetDisplayParams.getRenderingParams().shadingMode = shadingMode;
      globalDisplayParams.setDisplayMode(shadingMode);
      fireStateChanged();
   }

   @Override
   public float getMinEdgeDihedral()
   {
      return 0;
   }

   @Override
   public void setMinEdgeDihedral(float minEdgeDihedral)
   {
      for (CellSetDisplayParams cellSetDisplayParams : cellSetDisplayParameters)
         if (cellSetDisplayParams.isInheriting())
            cellSetDisplayParams.getRenderingParams().setMinEdgeDihedral(minEdgeDihedral);
      fireStateChanged();
   }

   @Override
   public float getPointSize()
   {
      return globalDisplayParams.getPointSize();
   }

   @Override
   public void setPointSize(float pointSize)
   {
      globalDisplayParams.setPointSize(pointSize);
      for (CellSetDisplayParams cellSetDisplayParams : cellSetDisplayParameters)
         if (cellSetDisplayParams.isInheriting())
            cellSetDisplayParams.getRenderingParams().setPointSize(pointSize);
      fireStateChanged();
   }

   public void setPickIndicator(int pickIndicator)
   {
      for (CellSetDisplayParams cellSetDisplayParams : cellSetDisplayParameters)
         cellSetDisplayParams.setPickIndicator(pickIndicator);
   }

   @Override
   public int getColorMap2DIndex()
   {
      return globalMappingParams.getColorMap2DIndex();
   }

   @Override
   public void setColorMap2DIndex(int colorMap2DIndex)
   {
      globalMappingParams.setColorMap2DIndex(colorMap2DIndex);
      for (CellSetDisplayParams cellSetDisplayParams : cellSetDisplayParameters)
         if (cellSetDisplayParams.isInheriting())
            cellSetDisplayParams.getDataMappingParams().setColorMap2DIndex(colorMap2DIndex);
      fireStateChanged();
   }


   @Override
   public boolean isUseColormap2D()
   {
      return globalMappingParams.isUseColormap2D();
   }

   @Override
   public void setUseColormap2D(boolean useColormap2D)
   {
     globalMappingParams.setUseColormap2D(useColormap2D);
     for (CellSetDisplayParams cellSetDisplayParams : cellSetDisplayParameters)
        if (cellSetDisplayParams.isInheriting())
           cellSetDisplayParams.getDataMappingParams().setUseColormap2D(useColormap2D);
      fireStateChanged();
   }

   @Override
   public Texture2D getTexture()
   {
      return globalMappingParams.getTexture();
   }

   @Override
   public BufferedImage getTextureImage()
   {
      return globalMappingParams.getTextureImage();
   }

   @Override
   public void setTextureImage(BufferedImage image)
   {
      globalMappingParams.setTextureImage(image);
   }

   @Override
   public ColorComponentParams getColorMap0Params()
   {
      return globalMappingParams.getColorMap0Params();
   }

   @Override
   public ColorComponentParams getColorMap1Params()
   {
      return globalMappingParams.getColorMap1Params();
   }

   @Override
   public ColorComponentParams getRedParams()
   {
      return globalMappingParams.getRedParams();
   }

   @Override
   public ColorComponentParams getGreenParams()
   {
      return globalMappingParams.getGreenParams();
   }

   @Override
   public ColorComponentParams getBlueParams()
   {
      return globalMappingParams.getBlueParams();
   }

   @Override
   public ColorComponentParams getSatParams()
   {
      return globalMappingParams.getSatParams();
   }

   @Override
   public ColorComponentParams getValParams()
   {
      return globalMappingParams. getValParams();
   }

   @Override
   public ColorComponentParams getUParams()
   {
      return globalMappingParams.getUParams();
   }

   @Override
   public ColorComponentParams getVParams()
   {
      return globalMappingParams.getVParams();
   }

   @Override
   public int getColorMapModification()
   {
      return globalMappingParams.getColorMapModification();
   }

   @Override
   public void setColorMapModification(int colorMapModification)
   {
      globalMappingParams.setColorMapModification(colorMapModification);
      for (CellSetDisplayParams cellSetDisplayParams : cellSetDisplayParameters)
         if (cellSetDisplayParams.isInheriting())
            cellSetDisplayParams.getDataMappingParams().setColorMapModification(colorMapModification);
      fireStateChanged();
   }

   public DataMappingParams getGlobalMappingParams()
   {
      return globalMappingParams;
   }

   @Override
   public void setColorMode(int colorMode)
   {
      globalMappingParams.setColorMode(colorMode);
      for (CellSetDisplayParams cellSetDisplayParams : cellSetDisplayParameters)
         if (cellSetDisplayParams.isInheriting())
            cellSetDisplayParams.getDataMappingParams().setColorMode(colorMode);
      fireStateChanged();
   }

   @Override
   public float getBlendRatio()
   {
      return globalMappingParams.getBlendRatio();
   }

   @Override
   public void setBlendRatio(float blendRatio)
   {
      globalMappingParams.setBlendRatio(blendRatio);
      for (CellSetDisplayParams cellSetDisplayParams : cellSetDisplayParameters)
         if (cellSetDisplayParams.isInheriting())
            cellSetDisplayParams.getDataMappingParams().setBlendRatio(blendRatio);
      fireStateChanged();
   }

   @Override
   public float getSurfaceOffset()
   {
      return globalDisplayParams.getSurfaceOffset();
   }

   @Override
   public void setSurfaceOffset(float offset)
   {
      globalDisplayParams.setSurfaceOffset(offset);
      for (CellSetDisplayParams cellSetDisplayParams : cellSetDisplayParameters)
         if (cellSetDisplayParams.isInheriting())
            cellSetDisplayParams.getRenderingParams().setSurfaceOffset(offset);
      fireStateChanged();
   }
   
   @Override
   public void setLightedBackside(boolean lightedBg)
   {
      globalDisplayParams.setLightedBackside(lightedBg);
      for (CellSetDisplayParams cellSetDisplayParams : cellSetDisplayParameters)
         if (cellSetDisplayParams.isInheriting())
            cellSetDisplayParams.getRenderingParams().setLightedBackside(lightedBg);
      fireStateChanged();
   }
   
   @Override
   public boolean isLightedBackside()
   {
      return globalDisplayParams.isLightedBackside();
   }
   
   @Override
   public DataMappingParams getMappingParams()
   {
      return globalMappingParams;
   }

   @Override
   public TransparencyParams getTransparencyParams()
   {
      return globalMappingParams.getTransparencyParams();
   }

   @Override
   public RenderEventListener getTransparencyChangeListener()
   {
      return globalDisplayParams.getTransparencyChangeListener();
   }

   @Override
   public void setAdjusting(boolean adjusting)
   {
      
   }
   
}
