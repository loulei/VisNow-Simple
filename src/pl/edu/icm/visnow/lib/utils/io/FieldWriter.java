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

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.util.zip.GZIPOutputStream;
import pl.edu.icm.visnow.datasets.RegularField;

/**
 *
 * @author  Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class FieldWriter
{
   
   /**
    * Creates a new instance of FieldWriter
    */
   public FieldWriter()
   {
   }
   
   public static void writeVolume(String filePath, RegularField inField)
   {
	   if( inField == null )
		   return;
      DataOutputStream out = null;
      try
      {
         if (filePath.endsWith("_gz") || filePath.endsWith("_GZ"))
            out = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(filePath)));
         else
            out = new DataOutputStream(new FileOutputStream(filePath));
         int[] dims = inField.getDims();
         if (dims.length==3)
         {
            if (dims[0]<256 && dims[1]<256 && dims[2]<256)
            {
               out.writeByte(dims[0]);
               out.writeByte(dims[1]);
               out.writeByte(dims[2]);
            }
            else
            {
               out.writeByte(0);
               out.writeInt(dims[0]);
               out.writeInt(dims[1]);
               out.writeInt(dims[2]);
            }
         }
         else if (dims.length==2)
         {
            out.writeInt(dims[0]);
            out.writeInt(dims[1]);
         }
         else if (dims.length==1)
            out.writeInt(dims[0]);
         out.write(inField.getData(0).getBData());
         float[][] affine = inField.getAffine();
         for (int i = 0; i < affine.length; i++)
            for (int j = 0; j < affine[i].length; j++)
               out.writeFloat(affine[i][j]);
         out.writeUTF(inField.getData(0).getName());
         if (inField.getData(0).getUserData() != null)
            for (int i = 0; i < inField.getData(0).getUserData().length; i++)
               out.writeUTF(inField.getData(0).getUserData()[i]);
         out.close();
      }
      catch (Exception e)
      {
         System.out.println("could not write "+filePath);
         System.out.println(""+e);
         e.printStackTrace();
      }
      
   }
   
}
