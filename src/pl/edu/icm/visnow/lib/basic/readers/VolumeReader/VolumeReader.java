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

package pl.edu.icm.visnow.lib.basic.readers.VolumeReader;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.lib.templates.visualization.modules.RegularOutFieldVisualizationModule;
import pl.edu.icm.visnow.lib.types.VNRegularField;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;
import pl.edu.icm.visnow.lib.utils.io.InputSource;

/**
 * @author Krzysztof S. Nowinski (know@icm.edu.pl) Warsaw University, Interdisciplinary Centre for
 * Mathematical and Computational Modelling
 */
public class VolumeReader extends RegularOutFieldVisualizationModule
{

   protected GUI computeUI = null;
   protected Params params;
   protected boolean fromGUI = false;
   protected boolean avsCompatible = true;

   /**
    * Creates a new instance of CreateGrid
    */
   public VolumeReader()
   {
      parameters = params = new Params();
      params.addChangeListener(new ChangeListener()
      {

            @Override
         public void stateChanged(ChangeEvent evt)
         {
            startAction();
         }
      });
      SwingInstancer.swingRunAndWait(new Runnable()
      {
            @Override
         public void run()
         {
            computeUI = new GUI();
            computeUI.setParams(params);
            ui.addComputeGUI(computeUI);
            setPanel(ui);
         }
      });
   }

   @Override
   public boolean isGenerator() {
      return true;
   }

   public static OutputEgg[] outputEggs = null;
   

   private RegularField readVolume(String fileName, int type, float[] origin, float[] scale)
   {
      DataInput in = null;
      URLConnection urlConnection;
      URL url;
      try
      {
         if (params.getSource() == InputSource.URL)
         {
            url = new URL(fileName);
            urlConnection = url.openConnection();
            in = new MemoryCacheImageInputStream(urlConnection.getInputStream());
         }
         else
         {
            if (fileName.endsWith("gz") || fileName.endsWith("GZ"))
               in = new DataInputStream(new GZIPInputStream(new FileInputStream(fileName)));
            else
               in = new DataInputStream(new FileInputStream(fileName));
         }
      } catch (IOException e)
      {
         System.out.println("could not open " + fileName);
         return null;
      }
      outField = null;
      int n = 0;
      String[] udata = new String[1000];
      int[] dims = new int[3];
      float[][] pts;
      try
      {
         String componentName = (new File(fileName)).getName();
         if (componentName == null || componentName.length() < 1)
            componentName = "data";
         dims[0] = in.readUnsignedByte();

         if (dims[0] != 0)
         {  avsCompatible = true;
            dims[1] = in.readUnsignedByte();
            dims[2] = in.readUnsignedByte();
         } else
         {  
            avsCompatible = true;
            dims[0] = in.readInt();
            dims[1] = in.readInt();
            dims[2] = in.readInt();
         }
         
         computeUI.setAVSCompatible(avsCompatible);
         outField = new RegularField(dims);


         pts = new float[][] {{0, 0, 0}, {dims[0] - 1, dims[1] - 1, dims[2] - 1}};
         float[][] affine = new float[][] {{0 , 0, 0}, {1, 0, 0}, {0 , 1, 0}, {0 , 0, 1}} ;
         
         byte[] data0 = new byte[dims[0] * dims[1] * dims[2]];
         in.readFully(data0);
         outField.addData(DataArray.create(data0, 1, componentName));
         try
         {
            for (int i = 0; i < affine.length; i++)
               for (int j = 0; j < affine[i].length; j++)
                  affine[i][j] = in.readFloat();
            if (type == pl.edu.icm.visnow.lib.basic.readers.VolumeReader.Params.FROM_FILE)
               outField.setAffine(affine);
         } catch (EOFException e)
         {
            outField.setExtents(pts);
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
         if (in instanceof InputStream)
            ((InputStream)in).close();
      } catch (IOException e)
      {
         System.out.println("could not read " + fileName);
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
               dmax = dims[i];
         for (int i = 0; i < 3; i++)
         {
            pts[0][i] = -(dims[i] - 1.f) / (dmax - 1.f);
            pts[1][i] = (dims[i] - 1.f) / (dmax - 1.f);
         }
         outField.setExtents(pts);
      }

      if (type == pl.edu.icm.visnow.lib.basic.readers.VolumeReader.Params.USER_AFFINE)
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
      
      if (type == pl.edu.icm.visnow.lib.basic.readers.VolumeReader.Params.USER_EXTENTS)
      {
         
         outField.setExtents(new float[][] {params.getMin(), params.getMax()});
      }

      return outField;
   }

    @Override
   public void onActive()
   {
      if (params.getFileName() == null)
         return;
      outField = readVolume(params.getFileName(), params.getType(), params.getOrig(), params.getScale());
      if (outField == null)
         return;
      computeUI.setFieldDescription(outField.description());
      setOutputValue("volume", new VNRegularField(outField));
      if (!params.isShow() || !prepareOutputGeometry())
         return;
      show();
   }
   
   @Override
   public void onInitFinishedLocal() {
       if(isForceFlag()) 
           computeUI.activateOpenDialog();
   }
   
}
