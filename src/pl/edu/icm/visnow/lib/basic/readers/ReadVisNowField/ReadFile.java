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

package pl.edu.icm.visnow.lib.basic.readers.ReadVisNowField;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteOrder;
import java.util.Scanner;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.gui.widgets.FileErrorFrame;
import pl.edu.icm.visnow.lib.utils.io.*;

/**
 *
 * @author know
 */
public class ReadFile implements Runnable
{
   protected RegularField outField = null;
   protected RegularFieldIOSchema schema = null;
   protected FileErrorFrame errorFrame = null;
   protected DataFileSchema fileSchema = null;
   protected LineNumberReader reader = null;
   protected ImageInputStream inStream = null;
   protected FileSectionReader sectionReader = null;
   protected Scanner scanner = null;
   protected Object input = null;
   protected String filePath = "";
   protected URLConnection urlConnection;
   protected URL url;

   public ReadFile(RegularField outField, RegularFieldIOSchema schema, int file, boolean isURL,
           FileErrorFrame errorFrame)
   {
      this.outField = outField;
      this.schema = schema;
      this.errorFrame = errorFrame;
      fileSchema = schema.getFileSchema(file);
      if (isURL)
      {
         filePath = fileSchema.getName();
         boolean relative = true;
         try
         {
            url = new URL(filePath);
            urlConnection = url.openConnection();
            relative = urlConnection == null;
         } catch (Exception e)
         {
         }
         if (relative)
            try
            {
               String hUrl = schema.getHeaderURL();
               int k = hUrl.lastIndexOf("/");
               url = new URL(hUrl.substring(0, k) + "/" + filePath);
               urlConnection = url.openConnection();
            } catch (Exception e)
            {
               System.out.println("could not open URL " + schema.getHeaderURL() + filePath);
            }
      } else if (new File(fileSchema.getName()).isAbsolute())
         filePath = fileSchema.getName();
      else
         filePath = schema.getHeaderFile().getParent() + File.separator + fileSchema.getName();
      try
      {
         switch (fileSchema.getType())
         {
            case DataFileSchema.BIG_ENDIAN:
            case DataFileSchema.LITTLE_ENDIAN:
               if (isURL)
                  inStream = new MemoryCacheImageInputStream(urlConnection.getInputStream());
               else
                  inStream = new FileImageInputStream(new File(filePath));
               if (fileSchema.getType() == DataFileSchema.BIG_ENDIAN)
                  inStream.setByteOrder(ByteOrder.BIG_ENDIAN);
               else
                  inStream.setByteOrder(ByteOrder.LITTLE_ENDIAN);
               input = inStream;
               break;
            case DataFileSchema.COLUMN:
            case DataFileSchema.FIXED_COLUMN:
               if (isURL)
                  reader = new LineNumberReader(new InputStreamReader(urlConnection.getInputStream()));
               else
                  reader = new LineNumberReader(new FileReader(filePath));
               input = reader;
               break;
            case DataFileSchema.ASCII:
               if (isURL)
                  scanner = new Scanner(new InputStreamReader(urlConnection.getInputStream()));
               else
                  scanner = new Scanner(new FileReader(filePath));
               input = scanner;
               break;
         }
         sectionReader = new FileSectionReader(outField, schema, reader, inStream, scanner, fileSchema.getType());
      } catch (FileNotFoundException e)
      {
         outputError("cannot open " + filePath,"", -1, null);
      }
      catch (IOException e)
      {
         if (inStream != null)
            outputError("error in " + filePath, "", 0, e);
         if (reader != null)
            outputError("error in data file " + filePath, "" , reader.getLineNumber(), e);
      }catch (Exception e)
      {
         outputError("cannot open " + filePath,"", -1, null);
      }
   }

   private void outputError(String text, String fname, int lineNumber, Exception e)
   {
      if (errorFrame != null)
         errorFrame.setErrorData(text, fname, lineNumber, e);
      else
         System.err.println("ERROR: " + text + "; in function " + "readFile" + " line " + lineNumber);
         //e.printStackTrace();   
   }
   
   public void run()
   {
      try
      {
         for (int part = 0; part < fileSchema.getNSections(); part++)
         {
            FilePartSchema partSchema = fileSchema.getPartSchema(part);
            if (partSchema instanceof SkipSchema)
               Skip.skip((SkipSchema)partSchema, input);
            if (partSchema instanceof FileSectionSchema)
            {  
               sectionReader.setSectionSchema((FileSectionSchema)partSchema, 0);
               if (sectionReader.readSection() > 1)
                  return;
               fileSchema.setLastRead(0);
            }
            else if (partSchema instanceof TimestepSchema)
            {
               TimestepSchema timestepSchema = (TimestepSchema)partSchema;
               int repeat = timestepSchema.getRepeat();
               float stime = timestepSchema.getTime();
               float dt = timestepSchema.getDt();
               if (repeat < 1) repeat = Integer.MAX_VALUE;
 time_loop:    for (int iStep = 0; iStep < repeat; iStep++) 
               {
                  for (int section = 0; section < timestepSchema.getNSections(); section++) 
                  {
                     FilePartSchema fps = timestepSchema.getSection(section);
                     if (fps instanceof SkipSchema)
                        Skip.skip((SkipSchema)fps, input);
                     if (fps instanceof FileSectionSchema)
                        sectionReader.setSectionSchema((FileSectionSchema)fps, stime + iStep * dt);
                     if (sectionReader.readSection() > 1)
                        break time_loop;
                  }
                  fileSchema.setLastRead(iStep);
               }
            }
         }
         if (inStream != null)
            inStream.close();
         if (reader != null)
            reader.close();
            
      } catch (FileNotFoundException e)
      {
         outputError("cannot open " + filePath,"", -1, null);
      }
      catch (IOException e)
      {
         if (reader != null)
            outputError("error in data file " + filePath, "" , reader.getLineNumber(), null);
         if (inStream != null)
            outputError("cerror in " + filePath,"", -1, null);
      }
   }
    
}
