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


package pl.edu.icm.visnow.lib.basic.mappers.Axes3D;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pl.edu.icm.visnow.engine.core.ParameterEgg;
import pl.edu.icm.visnow.engine.core.ParameterType;
import pl.edu.icm.visnow.engine.core.Parameters;
import pl.edu.icm.visnow.geometries.parameters.FontParams;

/**
 *
 * @author Krzysztof S. Nowinski
 *         Warsaw University, ICM
 */
public class Axes3DParams extends Parameters
{

   public static final int MIN = 0;
   public static final int ZERO = 1;
   public static final int CENTER = 2;
   public static final int MAX = 3;
   protected static final String DESCS = "axDescs";
   protected static final String FORMATS = "axFormats";
   protected static final String GRID = "gridLines";
   protected static final String POSITION = "axPos";
   protected static final String BOX = "box";
   protected static final String AXES = "axes";
   protected static final String DENSITY = "labelDensity";
   private static ParameterEgg[] eggs = new ParameterEgg[]
   {
      new ParameterEgg(DESCS,     ParameterType.independent, null),
      new ParameterEgg(FORMATS,   ParameterType.independent, null),
      new ParameterEgg(GRID,      ParameterType.independent, null),
      new ParameterEgg(POSITION,  ParameterType.independent, null),
      new ParameterEgg(BOX,       ParameterType.independent, true),
      new ParameterEgg(AXES,       ParameterType.independent, true),
      new ParameterEgg(DENSITY,   ParameterType.independent, 6)
   };
   protected FontParams fontParams = new FontParams();

   public Axes3DParams()
   {
      super(eggs);
      setValue(DESCS,new String[]{"X", "Y", "Z"});
      setValue(FORMATS,new String[]{"%4.1f", "%4.1f", "%4.1f"});
      setValue(GRID,new boolean[]{true, true, true});
      setValue(POSITION,new int[][]{{MIN, MIN}, {MIN, MIN}, {MIN, MIN}});
      fontParams.addChangeListener(new ChangeListener()
      {
         @Override
         public void stateChanged(ChangeEvent e)
         {
            fireStateChanged();
         }
      });
   }

   public String[] getAxDescs()
   {
      return (String[]) getValue(DESCS);
   }

   public String[] getAxFormats()
   {
      return (String[]) getValue(FORMATS);
   }

   public boolean[] getGridLines()
   {
      return (boolean[]) getValue(GRID);
   }

   public void setXLabel(String s)
   {
      ((String[]) getValue(DESCS))[0] = s;
      fireStateChanged();
   }

   public void setYLabel(String s)
   {
      ((String[]) getValue(DESCS))[1] = s;
      fireStateChanged();
   }

   public void setZLabel(String s)
   {
      ((String[]) getValue(DESCS))[2] = s;
      fireStateChanged();
   }

   public void setXFormat(String s)
   {
      ((String[]) getValue(FORMATS))[0] = s;
      fireStateChanged();
   }

   public void setYFormat(String s)
   {
      ((String[]) getValue(FORMATS))[1] = s;
      fireStateChanged();
   }

   public void setZFormat(String s)
   {
      ((String[]) getValue(FORMATS))[2] = s;
      fireStateChanged();
   }

   public void setXGridLines(boolean show)
   {
      ((boolean[]) getValue(GRID))[0] = show;
      fireStateChanged();
   }

   public void setYGridLines(boolean show)
   {
      ((boolean[]) getValue(GRID))[1] = show;
      fireStateChanged();
   }

   public void setZGridLines(boolean show)
   {
      ((boolean[]) getValue(GRID))[2] = show;
      fireStateChanged();
   }

   public FontParams getFontParams()
   {
      return fontParams;
   }

   public int[][] getAxPos()
   {
      return (int[][]) getValue(POSITION);
   }

   public boolean isBox()
   {
      return (Boolean) getValue(BOX);
   }

   public void setBox(boolean box)
   {
      setValue(BOX, box);
      fireStateChanged();
   }

   public boolean isAxes()
   {
      return (Boolean) getValue(AXES);
   }

   public void setAxes(boolean axes)
   {
      setValue(AXES, axes);
      fireStateChanged();
   }
   
   public int getLabelDensity()
   {
      return (Integer) getValue(DENSITY);
   }

   public void setLabelDensity(int labelDensity)
   {
      setValue(DENSITY, labelDensity);
      fireStateChanged();
   }
}
