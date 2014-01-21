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

import java.awt.image.BufferedImage;
import javax.media.j3d.Texture2D;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.render.RenderEventListener;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public interface AbstractDataMappingParams
{
   public void setActive(boolean val);
   public boolean isCellDataMapped();
   public void setCellDataMapped(boolean val);
   public int getColorMode();
   public void setColorMode(int val);
   
   public DataMappingParams getMappingParams();
   public ColorComponentParams getColorMap0Params();
   public ColorComponentParams getColorMap1Params();
   public ColorComponentParams getRedParams();
   public ColorComponentParams getGreenParams();
   public ColorComponentParams getBlueParams();
   public ColorComponentParams getSatParams();
   public ColorComponentParams getValParams();
   public ColorComponentParams getUParams();
   public ColorComponentParams getVParams();

   public int getColorMapModification();
   public void setColorMapModification(int c);
   public float getBlendRatio();
   public void setBlendRatio(float blendRatio);

   public Texture2D getTexture();
   public void setTexture(Texture2D texture);
   public BufferedImage getTextureImage();
   public void setTextureImage(BufferedImage image);
   public boolean isUseColormap2D();
   public void setUseColormap2D(boolean useColormap2D);
   public int getColorMap2DIndex();
   public void setColorMap2DIndex(int colorMap2DIndex);
   public ColormapLegendParameters getColormapLegendParameters();
   public void addRenderEventListener(RenderEventListener listener);
   public void removeRenderEventListener(RenderEventListener listener);
   public void fireStateChanged(int change);
   public TransparencyParams getTransparencyParams();
   public void setAdjusting(boolean adjusting);
}
