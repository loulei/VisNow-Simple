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

package pl.edu.icm.visnow.lib.utils.io;

/**
 *
 * @author  Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class ExtendedVolumeReaderParams
{
   private String dirPath                      = null;
   private String[] flist                      = null;
   private int nFiles                          = 0;
   private String base                         = null;
   private String ext                          = null;
   private int firstSlice                      = 0;
   private int lastSlice                       = 127;
   private int xResolution                     = 256;
   private int yResolution                     = 256;
   private int lowCrop                         = 0;
   private int highCrop                        = 1000;
   private float[] scale                       = {1,1,1};
   private boolean dicomDataByte               = false;
   private boolean forceDownsize               = false;
   private boolean byteSwap                    = true;
   private boolean skipStart                   = true;
   /** Creates a new instance of DicomReaderParams */
   public ExtendedVolumeReaderParams()
   {
   }

   public String[] getFlist()
   {
      return flist;
   }

   public void setFlist(String[] flist)
   {
      this.flist = flist;
   }

   public int getNFiles()
   {
      return nFiles;
   }

   public void setNFiles(int nFiles)
   {
      this.nFiles = nFiles;
   }

   public int getFirstSlice()
   {
      return firstSlice;
   }

   public void setFirstSlice(int firstSlice)
   {
      this.firstSlice = firstSlice;
   }

   public int getLastSlice()
   {
      return lastSlice;
   }

   public void setLastSlice(int lastSlice)
   {
      this.lastSlice = lastSlice;
   }

   public int getXResolution()
   {
      return xResolution;
   }

   public void setXResolution(int xResolution)
   {
      this.xResolution = xResolution;
   }

   public int getYResolution()
   {
      return yResolution;
   }

   public void setYResolution(int yResolution)
   {
      this.yResolution = yResolution;
   }

   public int getLowCrop()
   {
      return lowCrop;
   }

   public void setLowCrop(int lowCrop)
   {
      this.lowCrop = lowCrop;
   }

   public int getHighCrop()
   {
      return highCrop;
   }

   public void setHighCrop(int highCrop)
   {
      this.highCrop = highCrop;
   }

   public String getBase()
   {
      return base;
   }

   public void setBase(String base)
   {
      this.base = base;
   }

   public String getExt()
   {
      return ext;
   }

   public void setExt(String ext)
   {
      this.ext = ext;
   }

   public boolean isDicomDataByte()
   {
      return dicomDataByte;
   }

   public void setDicomDataByte(boolean dicomDataByte)
   {
      this.dicomDataByte = dicomDataByte;
   }

   public float[] getScale()
   {
      return scale;
   }

   public void setScale(float[] scale)
   {
      this.scale = scale;
   }

   public boolean isForceDownsize()
   {
      return forceDownsize;
   }

   public void setForceDownsize(boolean forceDownsize)
   {
      this.forceDownsize = forceDownsize;
   }

   public boolean isByteSwap()
   {
      return byteSwap;
   }

   public void setByteSwap(boolean byteSwap)
   {
      this.byteSwap = byteSwap;
   }

   public String getDirPath()
   {
      return dirPath;
   }

   public void setDirPath(String dirPath)
   {
      this.dirPath = dirPath;
   }

   public boolean isSkipStart()
   {
      return skipStart;
   }

   public void setSkipStart(boolean skipStart)
   {
      this.skipStart = skipStart;
   }

}
