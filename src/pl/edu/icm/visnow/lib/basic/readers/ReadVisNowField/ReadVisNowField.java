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

package pl.edu.icm.visnow.lib.basic.readers.ReadVisNowField;

import java.io.File;
import java.io.IOException;
import java.util.Vector;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pl.edu.icm.visnow.datasets.IrregularField;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.gui.widgets.FileErrorFrame;
import pl.edu.icm.visnow.lib.gui.grid.GridFrame;
import pl.edu.icm.visnow.lib.types.VNRegularField;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;
import pl.edu.icm.visnow.lib.basic.readers.ReadVisNowField.utils.DataFileSchema;
import pl.edu.icm.visnow.lib.basic.readers.ReadVisNowField.utils.FieldIOSchema;
import pl.edu.icm.visnow.lib.basic.readers.ReadVisNowField.utils.RegularFieldIOSchema;
import pl.edu.icm.visnow.lib.templates.visualization.modules.OutFieldVisualizationModule;
import pl.edu.icm.visnow.lib.types.VNIrregularField;
import pl.edu.icm.visnow.system.main.VisNow;

/**
 *
 * @author Krzysztof Nowinski, University of Warsaw, ICM
 */
public class ReadVisNowField extends OutFieldVisualizationModule
{

   public static OutputEgg[] outputEggs = null;

   protected GUI computeUI = null;
   protected GridFrame gridFrame = new GridFrame();
   protected boolean fromGUI = false;
   protected Params params = null;
   protected Parser headerParser = null;
   protected FieldIOSchema schema = null;
   protected FileErrorFrame errorFrame = null;
   protected String fileName = null;
   protected int nCurrentThreads;
   protected boolean done = false;
   protected boolean currentTransferDone = false;
   protected int continuousColorAdjustingLimit = Integer.parseInt(VisNow.get().getMainConfig().getProperty("visnow.continuousColorAdjustingLimit"));

   /**
    * Creates a new instance of CreateGrid
    */
   public ReadVisNowField()
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
      gridFrame.addChangeListener(new ChangeListener()
      {
         @Override
         public void stateChanged(ChangeEvent evt)
         {
            currentTransferDone = !gridFrame.isAborted();
         }
      });
      SwingInstancer.swingRunAndWait(new Runnable()
      {
         @Override
         public void run()
         {
            computeUI = new GUI();
            errorFrame = new FileErrorFrame();
         }
      });
      computeUI.setParams(params);
      ui.addComputeGUI(computeUI);
      setPanel(ui);
   }

   @Override
   public boolean isGenerator()
   {
      return true;
   }

   @Override
   public void onActive()
   {
      if (params.getFileName() != null)
      {
         try
         {
            fileName = params.getFileName();
            headerParser = new Parser(params.getFileName(), params.isURL(), errorFrame);
            schema = headerParser.parseFieldHeader();
            if (schema == null)
               return;
            outField = schema.getField();
            if (outField == null)
               return;
            StringBuilder iofb = new StringBuilder("<html>");
            String[] ioDesc = schema.getDescription();
            for (int i = 0; i < ioDesc.length; i++)
               iofb.append("<p>" + ioDesc[i]);
            iofb.append("</html>");
            computeUI.setFieldIODescription(iofb.toString());
         } catch (Exception e)
         {
            return;
         }
         Vector<DataFileSchema> fileSchemas = schema.getFileSchemas();
         int nFilesFromGrid = 0;
         for (DataFileSchema dataFileSchema : fileSchemas)
            if (dataFileSchema.getName().startsWith("grid:/"))
               nFilesFromGrid += 1;
         if (nFilesFromGrid > 0)
         {
            String[] filesFromGrid = new String[nFilesFromGrid];
            int i = 0;
            System.out.println("find these files on the grid:");
            for (DataFileSchema dataFileSchema : fileSchemas)
               if (dataFileSchema.getName().startsWith("grid:/"))
               {
                  String ffg = dataFileSchema.getName().substring(6);
                  System.out.println(ffg);
                  filesFromGrid[i] = ffg;
                  i += 1;
               }
            gridFrame.setRequestedFileNames(filesFromGrid);
            int nFilesWaiting = nFilesFromGrid;
            String tmpDirName = VisNow.getTmpDirPath();
            while (nFilesWaiting > 0)
            {
               currentTransferDone = false;
               gridFrame.setVisible(true);
               while (!currentTransferDone || gridFrame.isAborted())
                  try
                  {
                     Thread.sleep(50);
                  } catch (Exception e)
                  {
                  }
               if (gridFrame.isAborted())
                  return;
               String[] newlyTransferrefFiles = gridFrame.getTransferredFileNames();
               for (String newTFName : newlyTransferrefFiles)
                  for (DataFileSchema dataFileSchema : fileSchemas)
                  {
                     String name = dataFileSchema.getName();
                     if (name.startsWith("grid:/") && name.endsWith(newTFName))
                     {
                        dataFileSchema.setName(name.replaceFirst("grid:/", tmpDirName + File.separator));
                        nFilesWaiting -= 1;
                        break;
                     }
                  }
            }
            gridFrame.setVisible(false);
         }
         done = false;

         for (int i = 0; i < schema.getFileSchemas().size(); i++)
            new ReadFile(outField, schema, i, params.isURL(), errorFrame).run();
         outField.forceCurrentTime(0);
         if (outField.getCoords() != null)
            outField.updateExtents();

         for (int i = 0; i < outField.getNData(); i++)
            outField.getData(i).recomputeMinMax();
         computeUI.setFieldDescription(outField.description());
                  
         if (outField instanceof RegularField) {
           outRegularField = (RegularField)outField;
           outIrregularField = null;
           setOutputValue("regularOutField",new VNRegularField(outRegularField));
           setOutputValue("irregularOutField", null);         
         } else {
           outRegularField = null;
           outIrregularField = (IrregularField)outField;
           setOutputValue("regularOutField", null);
           setOutputValue("irregularOutField", new VNIrregularField((outIrregularField)));         
         }
        
         System.out.println(outField.getTimeUnit());
      }
      prepareOutputGeometry();
      if (params.isShow()|| 
          (outField instanceof RegularField && ((RegularField)outField).getDims().length != 2) || 
          outField.getNNodes() > continuousColorAdjustingLimit)  
         show();
   }

   @Override
   public void onInitFinishedLocal()
   {
      if (isForceFlag())
         computeUI.activateOpenDialog();
   }

   public static RegularField readVnf(String filePath)
   {
      return readVnf(VisNow.availableProcessors(), filePath);
   }

   public static RegularField readVnf(int nThreads, String filePath)
   {
      if (filePath == null)
         return null;

      File f = new File(filePath);
      if (!f.exists() || !f.canRead())
         return null;

      RegularField outField = null;
      Parser headerParser = null;
      RegularFieldIOSchema schema = null;
      int nCurrentThreads = nThreads;

      try
      {
         headerParser = new Parser(filePath, false, null);
         schema = (RegularFieldIOSchema) headerParser.parseFieldHeader();
         outField = schema.getField();
         if (outField == null)
            return null;
         schema.printDescription();
      } catch (IOException e)
      {
         return null;
      }
      int nReads = (schema.getFileSchemas().size() + nThreads - 1) / nThreads;

      if (schema.getFileSchemas().size() > 8)
         for (int i = 0; i < schema.getFileSchemas().size(); i++)
            new ReadFile(outField, schema, i, false, null).run();
      else
      {
         Thread[] workThreads = new Thread[nThreads];
         for (int readCycle = 0; readCycle < nReads; readCycle++)
         {
            nCurrentThreads = nThreads;
            if (readCycle == nReads - 1)
               nCurrentThreads = schema.getFileSchemas().size() % nThreads;
            if (nCurrentThreads == 0)
               nCurrentThreads = nThreads;
            for (int i = 0; i < nCurrentThreads; i++)
            {
               workThreads[i] = new Thread(new ReadFile(outField, schema, readCycle * nThreads + i, false, null));
               workThreads[i].start();
            }

            for (int iThr = 0; iThr < nCurrentThreads; iThr++)
               try
               {
                  workThreads[iThr].join();
               } catch (Exception e)
               {
               }
         }
      }
      if (outField.getCoords() != null)
         outField.updateExtents();
      outField.setCurrentTime(0);
      for (int i = 0; i < outField.getNData(); i++)
         outField.getData(i).recomputeMinMax();
      return outField;
   }

   public static void main(String[] args)
   {
      readVnf(1, "/home/know/Desktop/testVN/t2.vnf");
   }

}
