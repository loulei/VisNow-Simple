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

package pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.DataProvider;

import java.awt.Color;
import java.util.ArrayList;
import pl.edu.icm.visnow.geometries.parameters.DataMappingParams;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.render.RenderEvent;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.render.RenderEventListener;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.ViewPanels.OrthosliceNumberChangedEvent;

/**
 * @author Bartosz Borucki (babor@icm.edu.pl) University of Warsaw, Interdisciplinary Centre for
 * Mathematical and Computational Modelling
 */
public class DataProviderParams
{

   private boolean silent = false;
   private int[] dims = null;
   private float[] point0 = null;
   private float[] upps = null;
   private float upp = 1.0f;
   private int[] orthosliceNumbers =
   {
      0, 0, 0
   };
   private int[] rgbComponents =
   {
      0, 0, 0
   };
   private int[] rgbComponentWeights =
   {
      100, 100, 100
   };
   private int singleComponent = 0;
   public static final int MAPPING_MODE_COLORMAPPED = 0;
   public static final int MAPPING_MODE_RGB = 1;
   public static final int MAPPING_MODE_FAST = 2;
   private int mappingMode = MAPPING_MODE_COLORMAPPED;
   private int sliceFillColor = (255 << 24);
   private float overlayOpacity = 0.5f;
   private boolean simpleOverlay = false;
   private float simpleOverlayLow = 0.0f;
   private float simpleOverlayUp = 255.0f;
   private int simpleOverlayComponent = 0;
   private boolean simpleOverlayMask = false;
   private boolean simpleOverlayInvert = false;
   private Color simpleOverlayColor = Color.RED;
   private float[] customPlanePoint =
   {
      0.0f, 0.0f, 0.0f
   };
   private float[] customPlaneVector =
   {
      1.0f, 0.0f, 0.0f
   };
   private float customPlaneUPPW = 1.0f;
   private float customPlaneUPPH = 1.0f;
   private boolean customPlaneInterpolation = true;
   private float[][] customPlaneExtents = null;
   private float[][] customPlaneBase = null;
   private float[] customOrthoPlanesPoint =
   {
      0.0f, 0.0f, 0.0f
   };
   private float[][] customOrthoPlanesVectors =
   {
      {
         1.0f, 0.0f, 0.0f
      }, 
      {
         0.0f, -1.0f, 0.0f
      }, 
      {
         0.0f, 0.0f, -1.0f
      }
   };
   private float[] customOrthoPlanesUPPWs =
   {
      1.0f, 1.0f, 1.0f
   };
   private float[] customOrthoPlanesUPPHs =
   {
      1.0f, 1.0f, 1.0f
   };
   private boolean customOrthoPlanesInterpolation = true;
   private float[][][] customOrthoPlanesExtents = new float[3][][];
   private float[][][] customOrthoPlanesBase = new float[3][][];
   private DataMappingParams dmparams = new DataMappingParams();
   private float isolineThreshold = 0.5f;
   private float[] isolineThresholds = null;

   public DataProviderParams()
   {
      orthosliceNumbers[0] = 0;
      orthosliceNumbers[1] = 0;
      orthosliceNumbers[2] = 0;
      upps = new float[3];
      upps[0] = 0.1f;
      upps[1] = 0.1f;
      upps[2] = 0.1f;
      point0 = new float[3];
      point0[0] = 0.0f;
      point0[1] = 0.0f;
      point0[2] = 0.0f;
      rgbComponents[0] = 0;
      rgbComponents[1] = 0;
      rgbComponents[2] = 0;
      rgbComponentWeights[0] = 100;
      rgbComponentWeights[1] = 100;
      rgbComponentWeights[2] = 100;
      dmparams.addRenderEventListener(new RenderEventListener()
      {
         @Override
         public void renderExtentChanged(RenderEvent e)
         {
            fireColormapChanged();
         }
      });
      //dmparams.getColorMap0Params().setMapType(ColorMapManager.COLORMAP1D_GRAY);
   }

   public int getOrthosliceNumber(int axis)
   {
      if (dims == null)
         return -1;

      if (axis < 0 || axis > 2)
         return -1;

      return orthosliceNumbers[axis];
   }

   public float[] getOrthosliceRealPosition(int axis)
   {
      float[] out =
      {
         0.0f, 0.0f, 0.0f
      };
      if (dims == null)
         return out;
      if (axis < 0 || axis > 2)
         return out;
      System.arraycopy(point0, 0, out, 0, 3);
      out[axis] += upps[axis] * orthosliceNumbers[axis];
      return out;
   }

   public float[] getSingleRealPosition()
   {
      return point0;
   }

   public void setOrthosliceNumber(int axis, int slice)
   {
      if (dims == null)
         return;

      if (axis < 0 || axis > 2)
         return;

      if (slice < 0)
         slice = 0;

      if (slice >= dims[axis])
         slice = dims[axis] - 1;

      this.orthosliceNumbers[axis] = slice;
      fireOrthosliceNumberChanged(axis);
   }

   /**
    * @return the orthosliceNumbers
    */
   public int[] getOrthosliceNumbers()
   {
      return orthosliceNumbers;
   }

   /**
    * @param orthosliceNumbers the orthosliceNumbers to set
    */
   public void setOrthosliceNumbers(int[] orthosliceNumbers)
   {
      this.orthosliceNumbers = orthosliceNumbers;
      fireOrthosliceNumberChanged(-1);
   }

   public void setOrthosliceNumbers(int xSlice, int ySlice, int zSlice)
   {
      if (dims == null)
         return;

      if (xSlice < 0)
         xSlice = 0;
      if (ySlice < 0)
         ySlice = 0;
      if (zSlice < 0)
         zSlice = 0;

      if (xSlice >= dims[0])
         xSlice = dims[0] - 1;
      if (ySlice >= dims[1])
         ySlice = dims[1] - 1;
      if (zSlice >= dims[2])
         zSlice = dims[2] - 1;

      this.orthosliceNumbers[0] = xSlice;
      this.orthosliceNumbers[1] = ySlice;
      this.orthosliceNumbers[2] = zSlice;
      fireOrthosliceNumberChanged(-1);
   }

   public int getRgbComponent(int s)
   {
      if (s < 0 || s > 2)
         return -1;

      return rgbComponents[s];
   }

   public void setRgbComponent(int s, int component)
   {
      if (s < 0 || s > 2)
         return;

      this.rgbComponents[s] = component;
      fireRgbComponentChanged(s);
   }

   public void setAllRgbComponents(int component)
   {
      for (int i = 0; i < rgbComponents.length; i++)
      {
         this.rgbComponents[i] = component;
      }
      fireRgbComponentChanged(-1);
   }

   /**
    * @return the rgbComponents
    */
   public int[] getRgbComponents()
   {
      return rgbComponents;
   }

   /**
    * @param rgbComponents the rgbComponents to set
    */
   public void setRgbComponents(int[] rgbComponents)
   {
      this.rgbComponents = rgbComponents;
      fireRgbComponentChanged(-1);
   }

   public boolean isRgbComponentsEqual()
   {
      return (rgbComponents[0] == rgbComponents[1] && rgbComponents[0] == rgbComponents[2]);
   }

   public int getRgbComponentWeight(int s)
   {
      if (s < 0 || s > 2)
         return -1;

      return rgbComponentWeights[s];
   }

   public void setRgbComponentWeight(int s, int weight)
   {
      if (s < 0 || s > 2)
         return;

      this.rgbComponentWeights[s] = weight;
      fireRgbComponentWeightChanged(s);
   }

   public void setAllRgbComponentWeights(int weight)
   {
      for (int i = 0; i < rgbComponentWeights.length; i++)
      {
         this.rgbComponentWeights[i] = weight;
      }
      fireRgbComponentWeightChanged(-1);
   }

   /**
    * @return the rgbComponents
    */
   public int[] getRgbComponentWeights()
   {
      return rgbComponentWeights;
   }

   /**
    * @param rgbComponents the rgbComponents to set
    */
   public void setRgbComponentWeights(int[] rgbComponentWeights)
   {
      this.rgbComponentWeights = rgbComponentWeights;
      fireRgbComponentWeightChanged(-1);
   }
   private ArrayList<DataProviderParamsListener> listeners = new ArrayList<DataProviderParamsListener>();

   public synchronized void addDataProviderParamsListener(DataProviderParamsListener listener)
   {
      listeners.add(listener);
   }

   public synchronized void removeDataProviderParamsListener(DataProviderParamsListener listener)
   {
      listeners.remove(listener);
   }

   private void fireOrthosliceNumberChanged(int axis)
   {
      if (silent)
         return;
      OrthosliceNumberChangedEvent e = new OrthosliceNumberChangedEvent(this, axis);
      for (DataProviderParamsListener listener : listeners)
      {
         listener.onOrthosliceNumberChanged(e);
      }
   }

   private void fireRgbComponentChanged(int band)
   {
      if (silent)
         return;
      RgbComponentChangedEvent e = new RgbComponentChangedEvent(this, band);
      for (DataProviderParamsListener listener : listeners)
      {
         listener.onRgbComponentChanged(e);
      }
   }

   private void fireColormapChanged()
   {
      if (silent)
         return;
      ColormapChangedEvent e = new ColormapChangedEvent(this);
      for (DataProviderParamsListener listener : listeners)
      {
         listener.onColormapChanged(e);
      }
   }

   private void fireIsolineThresholdChanged()
   {
      if (silent)
         return;
      IsolineThresholdChangedEvent e = new IsolineThresholdChangedEvent(this);
      for (DataProviderParamsListener listener : listeners)
      {
         listener.onIsolineThresholdChanged(e);
      }
   }

   private void fireRgbComponentWeightChanged(int band)
   {
      if (silent)
         return;
      RgbComponentWeightChangedEvent e = new RgbComponentWeightChangedEvent(this, band);
      for (DataProviderParamsListener listener : listeners)
      {
         listener.onRgbComponentWeightChanged(e);
      }
   }

   private void fireCustomPlaneChanged()
   {
      if (silent)
         return;
      CustomPlaneChangedEvent e = new CustomPlaneChangedEvent(this);
      for (DataProviderParamsListener listener : listeners)
      {
         listener.onCustomPlaneChanged(e);
      }
   }

   private void fireCustomOrthoPlaneChanged(int axis, boolean[] changeMask)
   {
      if (silent)
         return;
      CustomOrthoPlaneChangedEvent e = new CustomOrthoPlaneChangedEvent(this, axis, changeMask);
      for (DataProviderParamsListener listener : listeners)
      {
         listener.onCustomOrthoPlaneChanged(e);
      }
   }

   /**
    * @return the silent
    */
   public boolean isSilent()
   {
      return silent;
   }

   /**
    * @param silent the silent to set
    */
   public void setSilent(boolean silent)
   {
      this.silent = silent;
   }

   /**
    * @param dims the dims to set
    */
   public void setDims(int[] dims)
   {
      this.dims = dims;
      if (dims.length == 3)
         for (int i = 0; i < 3; i++)
         {
            orthosliceNumbers[i] = dims[i] / 2;
         }
   }

   public void setPoint0(float[] p0)
   {
      if (p0 == null || p0.length != 3)
         return;

      this.point0 = new float[3];
      System.arraycopy(p0, 0, point0, 0, 3);
   }

   /**
    * @return the upp
    */
   public float getUpp()
   {
      return upp;
   }

   /**
    * @return the uppW
    */
   public float getOrthosliceUPPW(int axis, boolean trans)
   {
      if (axis < 0 || axis > 2)
         return 1.0f;

      if (upps == null)
         return 1.0f;

      if (trans)
      {
         switch (axis)
         {
            case 0:
               return upps[2];
            case 1:
               return upps[2];
            case 2:
               return upps[1];
            default:
               return 1.0f;
         }
      } else
      {
         switch (axis)
         {
            case 0:
               return upps[1];
            case 1:
               return upps[0];
            case 2:
               return upps[0];
            default:
               return 1.0f;
         }
      }
   }

   /**
    * @return the uppH
    */
   public float getOrthosliceUPPH(int axis, boolean trans)
   {
      if (axis < 0 || axis > 2)
         return 1.0f;

      if (upps == null)
         return 1.0f;

      if (trans)
      {
         switch (axis)
         {
            case 0:
               return upps[1];
            case 1:
               return upps[0];
            case 2:
               return upps[0];
            default:
               return 1.0f;
         }
      } else
      {
         switch (axis)
         {
            case 0:
               return upps[2];
            case 1:
               return upps[2];
            case 2:
               return upps[1];
            default:
               return 1.0f;
         }
      }
   }

   public float getSingleUPPW()
   {
      if (upps == null)
         return 1.0f;

      return upps[0];
   }

   public float getSingleUPPH()
   {
      if (upps == null)
         return 1.0f;

      return upps[1];
   }

   public void setUPPS(float xUPP, float yUPP, float zUPP)
   {
      this.upps = new float[3];
      upps[0] = xUPP;
      upps[1] = yUPP;
      upps[2] = zUPP;
      upp = Math.min(upps[0], Math.min(upps[1], upps[2]));
   }

   /**
    * @return the customPlanePoint
    */
   public float[] getCustomPlanePoint()
   {
      return customPlanePoint;
   }

   /**
    * @param customPlanePoint the customPlanePoint to set
    */
   public void setCustomPlanePoint(float[] customPlanePoint)
   {
      System.arraycopy(customPlanePoint, 0, this.customPlanePoint, 0, 3);
      fireCustomPlaneChanged();
   }

   /**
    * @return the customPlaneVector
    */
   public float[] getCustomPlaneVector()
   {
      return customPlaneVector;
   }

   /**
    * @param customPlaneVector the customPlaneVector to set
    */
   public void setCustomPlaneVector(float[] customPlaneVector)
   {
      System.arraycopy(customPlaneVector, 0, this.customPlaneVector, 0, 3);
      fireCustomPlaneChanged();
   }

   public void setCustomPlaneParams(float[] customPlanePoint, float[] customPlaneVector)
   {
      for (int i = 0; i < 3; i++)
      {
         this.customPlanePoint[i] = customPlanePoint[i];
         this.customPlaneVector[i] = customPlaneVector[i];
      }
      fireCustomPlaneChanged();
   }

   /**
    * @return the customPlaneUPPW
    */
   public float getCustomPlaneUPPW()
   {
      return customPlaneUPPW;
   }

   /**
    * @param customPlaneUPPW the customPlaneUPPW to set
    */
   public void setCustomPlaneUPPW(float customPlaneUPPW)
   {
      this.customPlaneUPPW = customPlaneUPPW;
   }

   /**
    * @return the customPlaneUPPH
    */
   public float getCustomPlaneUPPH()
   {
      return customPlaneUPPH;
   }

   /**
    * @param customPlaneUPPH the customPlaneUPPH to set
    */
   public void setCustomPlaneUPPH(float customPlaneUPPH)
   {
      this.customPlaneUPPH = customPlaneUPPH;
   }

   /**
    * @return the customPlaneInterpolationType
    */
   public boolean isCustomPlaneInterpolation()
   {
      return customPlaneInterpolation;
   }

   /**
    * @param customPlaneInterpolationType the customPlaneInterpolationType to set
    */
   public void setCustomPlaneInterpolation(boolean value)
   {
      this.customPlaneInterpolation = value;
      fireCustomPlaneChanged();
   }

   /**
    * @return the customPlaneExtents
    */
   public float[][] getCustomPlaneExtents()
   {
      return customPlaneExtents;
   }

   /**
    * @param customPlaneExtents the customPlaneExtents to set
    */
   public void setCustomPlaneExtents(float[][] customPlaneExtents)
   {
      this.customPlaneExtents = customPlaneExtents;
   }

   /**
    * @return the customPlaneBase
    */
   public float[][] getCustomPlaneBase()
   {
      return customPlaneBase;
   }

   /**
    * @param customPlaneBase the customPlaneBase to set
    */
   public void setCustomPlaneBase(float[][] customPlaneBase)
   {
      this.customPlaneBase = customPlaneBase;
   }

   /**
    * @return the singleComponent
    */
   public int getSingleComponent()
   {
      return singleComponent;
   }

   /**
    * @param singleComponent the singleComponent to set
    */
   public void setSingleComponent(int singleComponent)
   {
      this.singleComponent = singleComponent;
      fireRgbComponentChanged(-1);
   }

   /**
    * @return the mappingMode
    */
   public int getMappingMode()
   {
      return mappingMode;
   }

   /**
    * @param mappingMode the mappingMode to set
    */
   public void setMappingMode(int mappingMode)
   {
      this.mappingMode = mappingMode;
      fireRgbComponentChanged(-1);
   }

   /**
    * @return the sliceFillColor
    */
   public int getSliceFillColor()
   {
      return sliceFillColor;
   }

   /**
    * @param sliceFillColor the sliceFillColor to set
    */
   public void setSliceFillColor(int sliceFillColor)
   {
      this.sliceFillColor = sliceFillColor;
      fireRgbComponentChanged(-1);
   }

   /**
    * @return the isolineTreshold
    */
   public float getIsolineThreshold()
   {
      return isolineThreshold;
   }

   /**
    * @param isolineTreshold the isolineTreshold to set
    */
   public void setIsolineThreshold(float isolineThreshold)
   {
      this.isolineThreshold = isolineThreshold;
      fireIsolineThresholdChanged();
   }

   public float[] getIsolineThresholds()
   {
      return isolineThresholds;
   }

   public void setIsolineThresholds(float[] isolineThresholds)
   {
      this.isolineThresholds = isolineThresholds;
      fireIsolineThresholdChanged();
   }

   /**
    * @return the overlayOpacity
    */
   public float getOverlayOpacity()
   {
      return overlayOpacity;
   }

   /**
    * @param overlayOpacity the overlayOpacity to set
    */
   public void setOverlayOpacity(float overlayOpacity)
   {
      this.overlayOpacity = overlayOpacity;
      fireOverlayOpacityChanged();
   }

   public float[] getCustomOrthoPlanesPoint()
   {
      return customOrthoPlanesPoint;
   }

   public void setCustomOrthoPlanesPoint(float[] point)
   {
      boolean[] changeMask =
      {
         false, false, false, false
      };
      for (int i = 0; i < 3; i++)
      {
         if (point[i] != this.customOrthoPlanesPoint[i])
         {
            this.customOrthoPlanesPoint[i] = point[i];
            changeMask[i] = true;
         }
      }

      if (changeMask[0] || changeMask[1] || changeMask[2])
         fireCustomOrthoPlaneChanged(-1, changeMask);
   }

   public float[][] getCustomOrthoPlanesVectors()
   {
      return customOrthoPlanesVectors;
   }

   public float[] getCustomOrthoPlanesVector(int axis)
   {
      return customOrthoPlanesVectors[axis];
   }

   public void setCustomOrthoPlanesVector(int axis, float[] vector)
   {
      boolean[] changeMask =
      {
         false, false, false, false
      };
      for (int i = 0; i < 3; i++)
      {
         if (this.customOrthoPlanesVectors[axis][i] != vector[i])
         {
            this.customOrthoPlanesVectors[axis][i] = vector[i];
            changeMask[3] = true;
         }
      }

//        changeMask[3] = true;
      if (changeMask[3])
         fireCustomOrthoPlaneChanged(axis, changeMask);
//        fireCustomOrthoPlaneChanged(axis, null);
   }

   public void setCustomOrthoPlanesParams(float[] point, float[][] vectors)
   {
      boolean[] changeMask =
      {
         false, false, false, false
      };
      for (int i = 0; i < 3; i++)
      {
         if (point[i] != this.customOrthoPlanesPoint[i])
         {
            this.customOrthoPlanesPoint[i] = point[i];
            changeMask[i] = true;
         }

         for (int j = 0; j < 3; j++)
         {
            if (this.customOrthoPlanesVectors[i][j] != vectors[i][j])
            {
               this.customOrthoPlanesVectors[i][j] = vectors[i][j];
               changeMask[3] = true;
            }
         }
      }
//        changeMask[3] = true;
      if (changeMask[0] || changeMask[1] || changeMask[2] || changeMask[3])
         fireCustomOrthoPlaneChanged(-1, changeMask);
   }

   public float getCustomOrthoPlanesUPPW(int axis)
   {
      return customOrthoPlanesUPPWs[axis];
   }

   public void setCustomOrthoPlanesUPPW(int axis, float customOrthoPlanesUPPW)
   {
      this.customOrthoPlanesUPPWs[axis] = customOrthoPlanesUPPW;
   }

   public float getCustomOrthoPlanesUPPH(int axis)
   {
      return customOrthoPlanesUPPHs[axis];
   }

   public void setCustomOrthoPlanesUPPH(int axis, float customOrthoPlanesUPPH)
   {
      this.customOrthoPlanesUPPHs[axis] = customOrthoPlanesUPPH;
   }

   public boolean isCustomOrthoPlanesInterpolation()
   {
      return customOrthoPlanesInterpolation;
   }

   public void setCustomOrthoPlanesInterpolation(boolean value)
   {
      this.customOrthoPlanesInterpolation = value;
      fireCustomOrthoPlaneChanged(-1, null);
   }

   public float[][] getCustomOrthoPlanesExtents(int axis)
   {
      return customOrthoPlanesExtents[axis];
   }

   public float[][][] getCustomOrthoPlanesExtents()
   {
      return customOrthoPlanesExtents;
   }

   public void setCustomOrthoPlanesExtents(int axis, float[][] customOrthoPlanesExtents)
   {
      this.customOrthoPlanesExtents[axis] = customOrthoPlanesExtents;
   }

   public float[][] getCustomOrthoPlanesBase(int axis)
   {
      return customOrthoPlanesBase[axis];
   }

   public float[][][] getCustomOrthoPlanesBase()
   {
      return customOrthoPlanesBase;
   }

   public void setCustomOrthoPlanesBase(int axis, float[][] customOrthoPlanesBase)
   {
      this.customOrthoPlanesBase[axis] = customOrthoPlanesBase;
   }

   public boolean isSimpleOverlay()
   {
      return simpleOverlay;
   }

   public void setSimpleOverlay(boolean value)
   {
      this.simpleOverlay = value;
      fireOverlayChanged();
   }

   public float getSimpleOverlayLow()
   {
      return simpleOverlayLow;
   }

   public float getSimpleOverlayUp()
   {
      return simpleOverlayUp;
   }

   public void setSimpleOverlayLow(float low)
   {
      this.simpleOverlayLow = low;
      fireOverlayChanged();
   }

   public void setSimpleOverlayUp(float up)
   {
      this.simpleOverlayUp = up;
      fireOverlayChanged();
   }

   public void setSimpleOverlayLowUp(float low, float up)
   {
      this.simpleOverlayLow = low;
      this.simpleOverlayUp = up;
      fireOverlayChanged();
   }

   private void fireOverlayOpacityChanged()
   {
      if (silent)
         return;
      for (DataProviderParamsListener listener : listeners)
      {
         listener.onOverlayOpacityChanged(new DataProviderParamsEvent(this));
      }
   }

   private void fireOverlayChanged()
   {
      if (silent)
         return;
      for (DataProviderParamsListener listener : listeners)
      {
         listener.onOverlayChanged(new DataProviderParamsEvent(this));
      }
   }

   public void setSimpleOverlayComponent(int value)
   {
      this.simpleOverlayComponent = value;
      fireOverlayChanged();
   }

   public int getSimpleOverlayComponent()
   {
      return simpleOverlayComponent;
   }

   public void setSimpleOverlayColor(Color color)
   {
      this.simpleOverlayColor = color;
      fireOverlayChanged();
   }

   public Color getSimpleOverlayColor()
   {
      return simpleOverlayColor;
   }

   public void setSimpleOverlayMask(boolean value)
   {
      this.simpleOverlayMask = value;
      fireOverlayChanged();
   }

   public boolean isSimpleOverlayMask()
   {
      return simpleOverlayMask;
   }

   public void setSimpleOverlayInvert(boolean value)
   {
      this.simpleOverlayInvert = value;
      fireOverlayChanged();
   }

   public boolean isSimpleOverlayInvert()
   {
      return simpleOverlayInvert;
   }

   /**
    * @return the dmparams
    */
   public DataMappingParams getDataMappingParams()
   {
      return dmparams;
   }

   /**
    * @param dmparams the dmparams to set
    */
   public void setDataMappingParams(DataMappingParams dmparams)
   {
      this.dmparams = dmparams;
   }
}
