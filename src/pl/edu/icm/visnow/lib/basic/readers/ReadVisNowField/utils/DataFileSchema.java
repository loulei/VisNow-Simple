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
exception statement from your version.
*/
//</editor-fold>

package pl.edu.icm.visnow.lib.basic.readers.ReadVisNowField.utils;

import java.util.Vector;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class DataFileSchema
{
   public static final int BINARY        = 3;
   public static final int BIG_ENDIAN    = 1;
   public static final int LITTLE_ENDIAN = 2;
   public static final int ASCII         = 4;
   public static final int COLUMN        = 5;
   public static final int FIXED_COLUMN  = 6;

   
   public static final String[] fileTypes = new String[] {"",
                                                          "binary big endian", 
                                                          "binary little endian", 
                                                          "binary", 
                                                          "ascii", 
                                                          "column ascii", 
                                                          "fixed column ascii"};
   protected String name;
   protected int type;
   private String decimalSep;
   protected Vector<FilePartSchema> sections = new Vector<FilePartSchema>();
   protected int lastRead = -1;   

   public DataFileSchema(String name, int type, String decimalSep)
   {
      this.name = name;
      this.type = type;
      this.decimalSep = decimalSep;
   }
   
   public int getNSections()
   {
      return sections.size();
   }
   
   public FilePartSchema getPartSchema(int i)
   {
      if (i < 0 || i >= sections.size())
         return null;
      return sections.get(i);
   }
   
   public FileSectionSchema getSection(int i)
   {
      if (i < 0 || i >= sections.size() || !(sections.get(i) instanceof FileSectionSchema))
         return null;
      return (FileSectionSchema)sections.get(i);
   }
   
   public void addSection(FilePartSchema s)
   {
      sections.add(s);
   }

   public int getLastRead()
   {
      return lastRead;
   }

   public void setLastRead(int lastRead)
   {
      this.lastRead = lastRead;
   }   
   
   @Override
   public String toString()
   {
      String typeName = fileTypes[type];
      if (type == BIG_ENDIAN || type == LITTLE_ENDIAN)
         return typeName + " file " + name;
      else
      {
         if (decimalSep != null)
            return typeName + " file " + name + " decimal separator = " + decimalSep;
         else
            return typeName + " file " + name;
      }
   }

   public String[] getDescription()
   {
      String[] desc = new String[10000];
      desc[0] = toString();
      int k = 1;
      for (int i = 0; i < sections.size(); i++)
         if (sections.get(i) instanceof FileSectionSchema)
         {
            desc[k] = sections.get(i).toString(type == BIG_ENDIAN || type == LITTLE_ENDIAN);
            k += 1;
         } else if (sections.get(i) instanceof SkipSchema)
         {
            desc[k] = sections.get(i).toString();
            k += 1;
         }
         else
         {
            String[] tStepDesc = ((TimestepSchema) sections.get(i)).getDescription(type == BIG_ENDIAN || type == LITTLE_ENDIAN);
            for (int j = 0; j < tStepDesc.length; j++, k++)
               desc[k] = tStepDesc[j];
         }
      String[] description = new String[k];
      System.arraycopy(desc, 0, description, 0, k);
      return description;
   }

   /**
    * Get the value of type
    *
    * @return the value of type
    */
   public int getType()
   {
      return type;
   }

   /**
    * Set the value of type
    *
    * @param type new value of type
    */
   public void setType(int type)
   {
      this.type = type;
   }

   /**
    * Get the value of name
    *
    * @return the value of name
    */
   public String getName()
   {
      return name;
   }

   /**
    * Set the value of name
    *
    * @param name new value of name
    */
   public void setName(String name)
   {
      this.name = name;
   }

    /**
     * @return the decimalSep
     */
    public String getDecimalSeparator() {
        return decimalSep;
    }

}
