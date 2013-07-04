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

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.datasets.RegularField;

/**
 *
 * @author Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class RegularFieldReader
{

   private float progress = 0;

   /**
    * Creates a new instance of VolumeReader
    */
   public RegularFieldReader()
   {
   }

   public static RegularField readVolumefromURL(String fileName, int type, float[] origin, float[] scale)
   {
      URLConnection urlConnection;
      URL url;
      ImageInputStream in = null;
      try
      {
         url = new URL(fileName);
         urlConnection = url.openConnection();
         in = new MemoryCacheImageInputStream(urlConnection.getInputStream());
      } catch (Exception e)
      {
         System.out.println("could not open URL " + fileName);
      }
      RegularField outField = null;
      int n = 0;
      String[] udata = new String[1000];
      int[] dims = new int[3];
      float[][] pts = new float[2][3];
      try
      {
         String componentName = (new File(fileName)).getName();
         if (componentName == null || componentName.length() < 1)
            componentName = "data";
         dims[0] = in.readUnsignedByte();

         if (dims[0] != 0)
         {
            dims[1] = in.readUnsignedByte();
            dims[2] = in.readUnsignedByte();
         } else
         {
            dims[0] = in.readInt();
            dims[1] = in.readInt();
            dims[2] = in.readInt();
         }
         outField = new RegularField();
         outField.setDims(dims);


         pts[0][0] = pts[0][1] = pts[0][2] = 0.f;
         pts[1][0] = dims[0] - 1.f;
         pts[1][1] = dims[1] - 1.f;
         pts[1][2] = dims[2] - 1.f;


         byte[] data0 = new byte[dims[0] * dims[1] * dims[2]];
         in.read(data0);
         outField.addData(DataArray.create(data0, 1, componentName));
         try
         {
            float[][] affine = new float[4][3];
            for (int i = 0; i < affine.length; i++)
               for (int j = 0; j < affine[i].length; j++)
                  affine[i][j] = in.readFloat();
            if (type == pl.edu.icm.visnow.lib.basic.readers.VolumeReader.Params.FROM_FILE)
               outField.setAffine(affine);
         } catch (EOFException e)
         {
            outField.setPts(pts);
            if (scale == null || scale.length != 3)
            {
               float[] sc =
               {
                  1.f, 1.f, 1.f
               };
               outField.setScale(sc);
            } else
               outField.setScale(scale);
         }
         try
         {
            String name = in.readUTF();
            outField.getData(0).setName(name);
            for (n = 0; n < udata.length; n++)
               udata[n] = in.readUTF();
         } catch (EOFException e)
         {
            if (n > 0)
            {
               String[] uData = new String[n];
               System.arraycopy(udata, 0, uData, 0, n);
               outField.getData(0).setUserData(uData);
            }
         }
         in.close();
      } catch (Exception e)
      {
         System.out.println("could not read " + fileName);
         System.out.println("" + e);
         return null;
      }
      float[][] affine = new float[4][3];
      if (type == pl.edu.icm.visnow.lib.basic.readers.VolumeReader.Params.FROM_INDICES)
      {
         for (int i = 0; i < 3; i++)
         {
            for (int j = 0; j < 3; j++)
               affine[i][j] = 0;
            affine[i][i] = 1;
            affine[3][i] = 0;
         }
         outField.setAffine(affine);
      }
      if (type == pl.edu.icm.visnow.lib.basic.readers.VolumeReader.Params.NORMALIZED)
      {
         int dmax = 0;
         for (int i = 0; i < 3; i++)
            if (dims[i] > dmax)
               dmax = i;
         for (int i = 0; i < 3; i++)
         {
            pts[0][i] = -(dims[i] - 1.f) / (dmax - 1.f);
            pts[0][i] = (dims[i] - 1.f) / (dmax - 1.f);
         }
         outField.setPts(pts);
      }

      if (type == pl.edu.icm.visnow.lib.basic.readers.VolumeReader.Params.USER)
      {
         for (int i = 0; i < 3; i++)
         {
            for (int j = 0; j < 3; j++)
               affine[i][j] = 0;
            affine[i][i] = scale[i];
            affine[i][3] = origin[i];
         }
         outField.setAffine(affine);
      }

      return outField;
   }

   public static RegularField readVolume(String fileName, int type, float[] origin, float[] scale)
   {
      DataInputStream in = null;
      try
      {
         if (fileName.endsWith("gz") || fileName.endsWith("GZ"))
            in = new DataInputStream(new GZIPInputStream(new FileInputStream(fileName)));
         else
            in = new DataInputStream(new FileInputStream(fileName));
      } catch (Exception e)
      {
         System.out.println("could not read " + fileName);
         System.out.println("" + e);
         return null;
      }
      RegularField outField = null;
      int n = 0;
      String[] udata = new String[1000];
      int[] dims = new int[3];
      float[][] pts = new float[2][3];
      try
      {
         String componentName = (new File(fileName)).getName();
         if (componentName == null || componentName.length() < 1)
            componentName = "data";
         dims[0] = in.readUnsignedByte();

         if (dims[0] != 0)
         {
            dims[1] = in.readUnsignedByte();
            dims[2] = in.readUnsignedByte();
         } else
         {
            dims[0] = in.readInt();
            dims[1] = in.readInt();
            dims[2] = in.readInt();
         }
         outField = new RegularField();
         outField.setDims(dims);


         pts[0][0] = pts[0][1] = pts[0][2] = 0.f;
         pts[1][0] = dims[0] - 1.f;
         pts[1][1] = dims[1] - 1.f;
         pts[1][2] = dims[2] - 1.f;


         byte[] data0 = new byte[dims[0] * dims[1] * dims[2]];
         in.read(data0);
         outField.addData(DataArray.create(data0, 1, componentName));
         try
         {
            float[][] affine = new float[4][3];
            for (int i = 0; i < affine.length; i++)
               for (int j = 0; j < affine[i].length; j++)
                  affine[i][j] = in.readFloat();
            if (type == pl.edu.icm.visnow.lib.basic.readers.VolumeReader.Params.FROM_FILE)
               outField.setAffine(affine);
         } catch (EOFException e)
         {
            outField.setPts(pts);
            if (scale == null || scale.length != 3)
            {
               float[] sc =
               {
                  1.f, 1.f, 1.f
               };
               outField.setScale(sc);
            } else
               outField.setScale(scale);
         }
         try
         {
            String name = in.readUTF();
            outField.getData(0).setName(name);
            for (n = 0; n < udata.length; n++)
               udata[n] = in.readUTF();
         } catch (EOFException e)
         {
            if (n > 0)
            {
               String[] uData = new String[n];
               System.arraycopy(udata, 0, uData, 0, n);
               outField.getData(0).setUserData(uData);
            }
         }
         in.close();
      } catch (Exception e)
      {
         System.out.println("could not read " + fileName);
         System.out.println("" + e);
         return null;
      }
      float[][] affine = new float[4][3];
      if (type == pl.edu.icm.visnow.lib.basic.readers.VolumeReader.Params.FROM_INDICES)
      {
         for (int i = 0; i < 3; i++)
         {
            for (int j = 0; j < 3; j++)
               affine[i][j] = 0;
            affine[i][i] = 1;
            affine[3][i] = 0;
         }
         outField.setAffine(affine);
      }
      if (type == pl.edu.icm.visnow.lib.basic.readers.VolumeReader.Params.NORMALIZED)
      {
         int dmax = 0;
         for (int i = 0; i < 3; i++)
            if (dims[i] > dmax)
               dmax = i;
         for (int i = 0; i < 3; i++)
         {
            pts[0][i] = -(dims[i] - 1.f) / (dmax - 1.f);
            pts[0][i] = (dims[i] - 1.f) / (dmax - 1.f);
         }
         outField.setPts(pts);
      }

      if (type == pl.edu.icm.visnow.lib.basic.readers.VolumeReader.Params.USER)
      {
         for (int i = 0; i < 3; i++)
         {
            for (int j = 0; j < 3; j++)
               affine[i][j] = 0;
            affine[i][i] = scale[i];
            affine[3][i] = origin[i];
         }
         outField.setAffine(affine);
      }

      return outField;
   }

   public static RegularField readGaussianCube(String fileName)
   {
      String line = null;
      String[] tokens;
      RegularField outField = null;
      try
      {
         BufferedReader r = new BufferedReader(new FileReader(fileName));
         int[] dims = new int[3];
         float[][] pts = new float[2][3];
         float[][] affine = new float[4][3];
         line = r.readLine();
         line = r.readLine();  //ignoring header lines
         line = r.readLine().trim();
         tokens = line.split(" +");
         for (int i = 0; i < 3; i++)
            pts[0][i] = affine[3][i] = Float.parseFloat(tokens[i + 1]);
         for (int i = 0; i < dims.length; i++)
         {
            line = r.readLine().trim();
            tokens = line.split(" +");
            dims[i] = Integer.parseInt(tokens[0]);
            for (int j = 0; j < 3; j++)
               affine[i][j] = Float.parseFloat(tokens[j + 1]);
            pts[1][i] = pts[0][i] + (dims[i] - 1) * Float.parseFloat(tokens[i + 1]); // only rectangular components used
         }
         line = r.readLine();
         float[] data0 = new float[dims[0] * dims[1] * dims[2]];
         int k = 0, l = 0, m = 0, i = 0;
         while ((line = r.readLine()) != null && i < data0.length)
         {
            line = line.trim();
            if (line.length() == 0)
               continue;
            tokens = line.split(" +");
            for (int j = 0; j < tokens.length; j++)
            {
               data0[(k * dims[1] + l) * dims[0] + m] = Float.parseFloat(tokens[j]);
               i += 1;
               k += 1;
               if (k == dims[2])
               {
                  k = 0;
                  l += 1;
               }
               if (l == dims[1])
               {
                  l = 0;
                  m += 1;
               }
            }
         }

         String componentName = (new File(fileName)).getName();
         if (componentName == null || componentName.length() < 1)
            componentName = "data";
         outField = new RegularField();
         outField.setDims(dims);
         outField.setPts(pts);
         outField.setAffine(affine);
         outField.addData(DataArray.create(data0, 1, componentName));
      } catch (Exception e)
      {
         System.out.println("could not read " + fileName);
         System.out.println("" + e);
         e.printStackTrace();
      }
      return outField;
   }

   public static RegularField readRawBytes2D(String fileName)
   {
      int[] dims = new int[2];
      RegularField outField = null;
      try
      {
         DataInputStream in = new DataInputStream(new FileInputStream(fileName));
         dims[0] = in.readInt();
         dims[1] = in.readInt();
         byte[] data = new byte[dims[0] * dims[1]];
         in.read(data);
         outField = new RegularField();
         outField.setDims(dims);
         outField.addData(DataArray.create(data, 1, "var"));
      } catch (Exception e)
      {
         System.out.println("could not read " + fileName);
         System.out.println("" + e);
      }
      return outField;

   }

   public RegularField readSlices(ExtendedVolumeReaderParams params)
   {
      RegularField outField = null;
      String fileName = params.getBase() + "***" + params.getExt();
      try
      {

         String componentName = params.getBase().substring(params.getBase().lastIndexOf("/") + 1);
         if (componentName == null || componentName.length() < 1)
            componentName = "data";
         int[] dims = new int[3];
         dims[0] = params.getXResolution();
         dims[1] = params.getYResolution();
         dims[2] = params.getLastSlice() - params.getFirstSlice() + 1;
         float[][] pts = new float[2][3];
         pts[1][0] = dims[0] - 1.f;
         pts[1][1] = dims[1] - 1.f;
         pts[1][2] = dims[2] - 1.f;

         pts[0][0] = pts[0][1] = pts[0][2] = 0.f;
         DecimalFormat df = new DecimalFormat("000");

         byte[] data0 = new byte[dims[0] * dims[1] * dims[2]];
         byte[] tdata = new byte[dims[0] * dims[1]];
         for (int i = params.getFirstSlice(), j = 0; i <= params.getLastSlice(); i++, j++)
         {
            progress = (float) i / dims[2];
            fireStateChanged();
            fileName = params.getBase() + df.format(i) + params.getExt();
            DataInputStream in = new DataInputStream(new FileInputStream(fileName));
            if (!params.isSkipStart())
               in.read(data0);
            else
            {
               long fl = new File(fileName).length();
               long skip = fl - tdata.length;
               if (skip < 0)
                  continue;
               in.skip(skip);
               in.readFully(tdata, 0, tdata.length);
            }
            System.arraycopy(tdata, 0, data0, j * tdata.length, tdata.length);
            in.close();
         }
         outField = new RegularField();
         outField.setDims(dims);
         outField.setPts(pts);
         outField.setScale(params.getScale());
         outField.addData(DataArray.create(data0, 1, componentName));
      } catch (Exception e)
      {
         System.out.println("could not read " + fileName);
         System.out.println("" + e);
      }
      return outField;
   }

   public float getProgress()
   {
      return progress;
   }
   /**
    * Utility field holding list of ChangeListeners.
    */
   private transient ArrayList<ChangeListener> changeListenerList =
           new ArrayList<ChangeListener>();

   /**
    * Registers ChangeListener to receive events.
    *
    * @param listener The listener to register.
    */
   public synchronized void addChangeListener(ChangeListener listener)
   {
      changeListenerList.add(listener);
   }

   /**
    * Removes ChangeListener from the list of listeners.
    *
    * @param listener The listener to remove.
    */
   public synchronized void removeChangeListener(ChangeListener listener)
   {
      changeListenerList.remove(listener);
   }

   /**
    * Notifies all registered listeners about the event.
    *
    * @param object Parameter #1 of the
    * <CODE>ChangeEvent<CODE> constructor.
    */
   private void fireStateChanged()
   {
      ChangeEvent e = new ChangeEvent(this);
      for (ChangeListener listener : changeListenerList)
         listener.stateChanged(e);
   }
}
