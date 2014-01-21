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


package pl.edu.icm.visnow.geometries.parameters;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Texture2D;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.log4j.Logger;
import pl.edu.icm.visnow.datasets.CellSet;
import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.datasets.IrregularField;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.render.RenderEvent;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.render.RenderEventListener;
import pl.edu.icm.visnow.system.main.VisNow;

/**
 *
 * @author  Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class DataMappingParams implements Cloneable, AbstractDataMappingParams
{
	private static final Logger LOGGER = Logger.getLogger(DataMappingParams.class);
   public static final int NULL                   = -1;
   public static final int UNCOLORED              = 0;
   public static final int COLORMAPPED            = 1;
   public static final int RGB                    = 2;
   public static final int COLORMAPPED2D          = 4;
   public static final int COLORED                = 7;
   public static final int UVTEXTURED             = 8;
   public static final int COORDX                 = -10;
   public static final int COORDY                 = -11;
   public static final int COORDZ                 = -12;
   public static final int NORMALX                = -20;
   public static final int NORMALY                = -21;
   public static final int NORMALZ                = -22;
   public static final int INDEXI                 = -30;
   public static final int INDEXJ                 = -31;
   public static final int NO_MAP_MODIFICATION    = 0;
   public static final int SAT_MAP_MODIFICATION   = 1;
   public static final int VAL_MAP_MODIFICATION   = 2;
   public static final int BLEND_MAP_MODIFICATION = 3;

   public static final String[] colorTypes= new String[] {
      "UNCOLORED", "COLORMAPPED", "RGB", "HSV", "COLORMAPPED2D", "UVTEXTURED"
   };

   protected ColorComponentParams colorMap0Params = new ColorComponentParams();
   protected ColorComponentParams colorMap1Params = new ColorComponentParams();
   protected ColorComponentParams redParams       = new ColorComponentParams();
   protected ColorComponentParams greenParams     = new ColorComponentParams();
   protected ColorComponentParams blueParams      = new ColorComponentParams();
   protected ColorComponentParams satParams       = new ColorComponentParams();
   protected ColorComponentParams valParams       = new ColorComponentParams();
   protected ColorComponentParams uParams         = new ColorComponentParams();
   protected ColorComponentParams vParams         = new ColorComponentParams();
   protected TransparencyParams transparencyParams = new TransparencyParams();

   protected boolean active = true;
   protected boolean cellDataMapped = false;
   protected int colorMapModification = NO_MAP_MODIFICATION;
   protected int colorMode = COLORMAPPED;
   protected float blendRatio = 0;
   protected byte[] invalidColor = new byte[]{0, 0, 0};

   protected boolean inherited = true;
   
   protected boolean adjusting = false;
   protected int parentObjectSize = 256000;
   protected int continuousColorAdjustingLimit = Integer.parseInt(VisNow.get().getMainConfig().getProperty("visnow.continuousColorAdjustingLimit"));

   protected boolean useColormap2D = false; 
   protected BufferedImage textureImage;
   protected Texture2D texture = null;
   protected int colorMap2DIndex = 0;
   protected int modeChanged = 0;
   protected int colorModeChanged = 2;
   protected ColormapLegendParameters colormapLegendParameters = new ColormapLegendParameters();
   protected RenderEventListener colorCompChangeListener = new RenderEventListener()
   {
      @Override
      public void renderExtentChanged(RenderEvent e)
      {
         colormapLegendParameters.setEnabled(colorMode == COLORMAPPED &&
                                             colorMapModification == NO_MAP_MODIFICATION &&
                                             colorMap0Params.getDataComponent() >= 0);
         adjusting = e.isAdjusting();
         if (e.getSource() == uParams ||
             e.getSource() == vParams)
         {
            fireStateChanged(RenderEvent.TEXTURE);
         }
         else if (e.getSource() == transparencyParams)
         {
            fireStateChanged(RenderEvent.TRANSPARENCY);
         }
         else
         {
            fireStateChanged(RenderEvent.COLORS);
         }
      }
   };
   
   protected Field field = null;
   protected CellSet set = null;
   
   public DataMappingParams()
   {
      colorMap0Params.setListener(colorCompChangeListener);
      colorMap1Params.setListener(colorCompChangeListener);
      redParams.setListener(colorCompChangeListener);
      greenParams.setListener(colorCompChangeListener);
      blueParams.setListener(colorCompChangeListener);
      satParams.setListener(colorCompChangeListener);
      valParams.setListener(colorCompChangeListener);
      uParams.setListener(colorCompChangeListener);
      vParams.setListener(colorCompChangeListener);
      transparencyParams.addListener(colorCompChangeListener);
      colormapLegendParameters.addChangeListener(new ChangeListener()
      {
         @Override
         public void stateChanged(ChangeEvent e)
         {
            fireStateChanged(RenderEvent.COLORS);
         }
      });
   }

   public DataMappingParams(IrregularField field, CellSet set)
   {
      this();
      active = false;
      if (setInField(field))
      {
         this.set = set;
         return;
      }
      if (set != null)
         for (int i = 0; i < set.getNData(); i++)
            if (set.getData(i).isSimpleNumeric())
            {
               colorMap0Params.setDataComponent(i, set.getData(i).getMinv(), set.getData(i).getMaxv());
               colorMap1Params.setDataComponent(i, set.getData(i).getMinv(), set.getData(i).getMaxv());
               redParams.setDataComponent(i, set.getData(i).getMinv(), set.getData(i).getMaxv());
               greenParams.setDataComponent(i, set.getData(i).getMinv(), set.getData(i).getMaxv());
               blueParams.setDataComponent(i, set.getData(i).getMinv(), set.getData(i).getMaxv());
               satParams.setDataComponent(i, set.getData(i).getMinv(), set.getData(i).getMaxv());
               valParams.setDataComponent(i, set.getData(i).getMinv(), set.getData(i).getMaxv());
               colormapLegendParameters.setColormapLow(set.getData(i).getMinv());
               colormapLegendParameters.setColormapUp(set.getData(i).getMaxv());
               colormapLegendParameters.setColorMapLookup(colorMap0Params.getRGBColorTable());
               colormapLegendParameters.setEnabled(colorMode == COLORMAPPED &&
                                                   colorMapModification == NO_MAP_MODIFICATION &&
                                                   colorMap0Params.getDataComponent() >= 0);
               return;
            }
      colorMap0Params.setDataComponent(-1,0,0);
      colorMap1Params.setDataComponent(-1,0,0);
      redParams.setDataComponent(-1,0,0);
      greenParams.setDataComponent(-1,0,0);
      blueParams.setDataComponent(-1,0,0);
      satParams.setDataComponent(-1,0,0);
      valParams.setDataComponent(-1,0,0);
      colormapLegendParameters.setEnabled(false);
   }

   public DataMappingParams(Field field)
   {
      this();
      setInField(field);
   }

   public final boolean setInField(Field field)
   {
      if (field == null)
         return false;
      this.field = field;
      
      
      for (int i = 0; i < field.getNData(); i++)
         if (field.getData(i).isSimpleNumeric())
         {
            colorMap0Params.setDataComponent(i, field.getData(i).getMinv(), field.getData(i).getMaxv());            
            colorMap1Params.setDataComponent(i, field.getData(i).getMinv(), field.getData(i).getMaxv());
            redParams.setDataComponent(i, field.getData(i).getMinv(), field.getData(i).getMaxv());
            greenParams.setDataComponent(i, field.getData(i).getMinv(), field.getData(i).getMaxv());
            blueParams.setDataComponent(i, field.getData(i).getMinv(), field.getData(i).getMaxv());
            satParams.setDataComponent(i, field.getData(i).getMinv(), field.getData(i).getMaxv());
            valParams.setDataComponent(i, field.getData(i).getMinv(), field.getData(i).getMaxv());
            colormapLegendParameters.setColormapLow(field.getData(i).getMinv());
            colormapLegendParameters.setColormapUp(field.getData(i).getMaxv());
            colormapLegendParameters.setColorMapLookup(colorMap0Params.getRGBColorTable());
            colormapLegendParameters.setEnabled(colorMode == COLORMAPPED &&
                                                colorMapModification == NO_MAP_MODIFICATION &&
                                                colorMap0Params.getDataComponent() >= 0);
            return true;
         }
      colorMap0Params.setDataComponent(-1,0,0);
      colorMap1Params.setDataComponent(-1,0,0);
      redParams.setDataComponent(-1,0,0);
      greenParams.setDataComponent(-1,0,0);
      blueParams.setDataComponent(-1,0,0);
      satParams.setDataComponent(-1,0,0);
      valParams.setDataComponent(-1,0,0);
      colormapLegendParameters.setEnabled(false);
      return false;
   }


   public void copy(DataMappingParams src)
   {
      colorMap0Params.copy(src.colorMap0Params);
      colorMap1Params.copy(src.colorMap0Params);
      redParams.copy(src.redParams);
      greenParams.copy(src.greenParams);
      blueParams.copy(src.blueParams);
      satParams.copy(src.satParams);
      valParams.copy(src.valParams);
      uParams.copy(src.uParams);
      vParams.copy(src.vParams);
      cellDataMapped = src.cellDataMapped;
      colorMapModification = src.colorMapModification;
      colorMode = src.colorMode;
   }

   @Override
   public boolean isCellDataMapped()
   {
      return cellDataMapped;
   }

   @Override
   public void setCellDataMapped(boolean cellDataMapped)
   {
      if (this.cellDataMapped != cellDataMapped)
      {
         this.cellDataMapped = cellDataMapped;
         fireStateChanged(RenderEvent.GEOMETRY);
      }
   }

   @Override
   public ColorComponentParams getBlueParams()
   {
      return blueParams;
   }

   @Override
   public ColorComponentParams getColorMap0Params()
   {
      return colorMap0Params;
   }

   @Override
   public ColorComponentParams getColorMap1Params()
   {
      return colorMap1Params;
   }

   @Override
   public ColorComponentParams getGreenParams()
   {
      return greenParams;
   }

   @Override
   public ColorComponentParams getRedParams()
   {
      return redParams;
   }

   @Override
   public ColorComponentParams getValParams()
   {
      return valParams;
   }

   @Override
   public ColorComponentParams getSatParams()
   {
      return satParams;
   }

   @Override
   public ColorComponentParams getUParams()
   {
      return uParams;
   }

   @Override
   public ColorComponentParams getVParams()
   {
      return vParams;
   }

   public boolean isInherited()
   {
      return inherited;
   }

   public void setInherited(boolean inherited)
   {
      this.inherited = inherited;
   }

   @Override
   public int getColorMapModification()
   {
      return colorMapModification;
   }

   @Override
   public void setColorMapModification(int colorMapModification)
   {
      this.colorMapModification = colorMapModification;
      colormapLegendParameters.setEnabled(colorMode == COLORMAPPED &&
                                          colorMapModification == NO_MAP_MODIFICATION &&
                                          colorMap0Params.getDataComponent() >= 0);
      if (colorMode == COLORMAPPED)
         fireStateChanged(RenderEvent.COLORS);
   }
   
   @Override
   public int getColorMode()
   {
      return colorMode;
   }

   @Override
   public void setColorMode(int colorMode)
   {
      this.colorMode = colorMode;
      colormapLegendParameters.setEnabled(colorMode == COLORMAPPED &&
                                          colorMapModification == NO_MAP_MODIFICATION &&
                                          colorMap0Params.getDataComponent() >= 0);
      fireStateChanged(RenderEvent.COLORS);
   }

   @Override
   public float getBlendRatio()
   {
      return blendRatio;
   }

   @Override
   public void setBlendRatio(float blendRatio)
   {
      this.blendRatio = blendRatio;
      fireStateChanged(RenderEvent.COLORS);
   }

   @Override
   public Texture2D getTexture()
   {
      return texture;
   }

   @Override
   public void setTexture(Texture2D texture)
   {
      this.texture = texture;
      if (colorMode == UVTEXTURED)
      {
         LOGGER.info("renderExtentChanged texture");
         fireStateChanged(RenderEvent.TEXTURE);
      }
   }

   public void setTextureFileName(BufferedImage textureImage)
   {
      texture = new Texture2D(Texture2D.BASE_LEVEL, Texture2D.RGB,
              textureImage.getWidth(), textureImage.getHeight());
      texture.setImage(0, new ImageComponent2D(ImageComponent2D.FORMAT_RGB, textureImage));
      if (colorMode == UVTEXTURED)
         fireStateChanged(RenderEvent.TEXTURE);
   }

   public int getModeChanged()
   {
      return modeChanged;
   }

   public void setModeChanged(int modeChanged)
   {
      this.modeChanged = modeChanged;
   }

   public int getColorModeChanged()
   {
      return colorModeChanged;
   }

   public void setColorModeChanged(int colorModeChanged)
   {
      this.colorModeChanged = colorModeChanged;
   }

   public void setAdjusting(boolean adjusting)
   {
      this.adjusting = adjusting;
   }

   public void setParentObjectSize(int parentObjectSize)
   {
      this.parentObjectSize = parentObjectSize;
   }
   
   @Override
   public ColormapLegendParameters getColormapLegendParameters()
   {
      return colormapLegendParameters;
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

   @Override
   public void setActive(boolean active)
   {
      this.active = active;
      if (active)
         fireStateChanged(RenderEvent.COLORS);
   }

  /**
    * Utility field holding list of RenderEventListeners.
    */
   private transient List<RenderEventListener> renderEventListenerList =
           new CopyOnWriteArrayList<RenderEventListener>();

   /**
    * Registers RenderEventListener to receive events.
    * @param listener The listener to register.
    */
   @Override
   public synchronized void addRenderEventListener(RenderEventListener listener)
   {
      if(!renderEventListenerList.contains(listener)) 
            renderEventListenerList.add(listener);
   }

   /**
    * Removes RenderEventListener from the list of listeners.
    * @param listener The listener to remove.
    */
   @Override
   public synchronized void removeRenderEventListener(RenderEventListener listener)
   {
      renderEventListenerList.remove(listener);
   }

   public void clearRenderEventListeners()
   {
      renderEventListenerList.clear();
   }
   
   /**
    * Notifies all registered listeners about the event.               
    *
    * @param object Parameter #1 of the <CODE>ChangeEvent<CODE> constructor.
    */
   
   @Override
   public void fireStateChanged(int change)
   {
      if (active && (!(adjusting || 
                       colorMap0Params.isAdjusting() || colorMap1Params.isAdjusting() ||
                       satParams.isAdjusting() || valParams.isAdjusting() ||
                       redParams.isAdjusting() || greenParams.isAdjusting() || blueParams.isAdjusting() ||
                       transparencyParams.isAdjusting())
                 || parentObjectSize < continuousColorAdjustingLimit))
      {
         RenderEvent e = new RenderEvent(this, change);
         for (RenderEventListener listener : renderEventListenerList)
            listener.renderExtentChanged(e);
      }
   }


   /**
    * @param useColormap2D the useColormap2D to set
    */
   @Override
   public void setUseColormap2D(boolean useColormap2D)
   {
      this.useColormap2D = useColormap2D;
      if (colorMode == COLORMAPPED2D)
         fireStateChanged(RenderEvent.COLORS);
   }

   @Override
   public BufferedImage getTextureImage()
   {
      return textureImage;
   }

   @Override
   public void setTextureImage(BufferedImage image)
   {
      textureImage = image;
      if (colorMode == COLORMAPPED2D)
         fireStateChanged(RenderEvent.COLORS);
      if (colorMode == UVTEXTURED)
         fireStateChanged(RenderEvent.TEXTURE);
   }

   @Override
   public int getColorMap2DIndex()
   {
      return colorMap2DIndex;
   }

   @Override
   public void setColorMap2DIndex(int colorMap2DIndex)
   {
      this.colorMap2DIndex = colorMap2DIndex;
      fireStateChanged(RenderEvent.COLORS);
   }

   public boolean isUseColormap2D()
   {
      return useColormap2D;
   }

   public byte[] getInvalidColor()
   {
      return invalidColor;
   }

   public void setInvalidColor(byte[] invalidColor)
   {
      this.invalidColor = invalidColor;
   }

   @Override
   public DataMappingParams getMappingParams()
   {
      return this;
   }

   @Override
   public TransparencyParams getTransparencyParams()
   {
      return transparencyParams;
   }
   
}
