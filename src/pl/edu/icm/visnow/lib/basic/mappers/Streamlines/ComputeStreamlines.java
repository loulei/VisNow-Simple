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

package pl.edu.icm.visnow.lib.basic.mappers.Streamlines;

import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.datasets.IrregularField;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.gui.events.FloatValueModificationEvent;
import pl.edu.icm.visnow.gui.events.FloatValueModificationListener;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
abstract public class ComputeStreamlines
{
   protected IrregularField outField = null;
   protected int trueDim = 3;
   protected int nSpace = 3;
   protected float[] startCoords = null;
   protected float[] coords = null;
   protected int[] fromSteps = null;
   protected int[] toSteps = null;
   protected float[] vectors = null;
   protected int nvert = 0;
   protected int[] lines = null;
   protected Params params = null;
   protected int nForward = 0;
   protected int nBackward = 0;
   protected int nSrc;
   protected float[] vects = null;
   protected int vlen = 3;
   protected int[] threadProgress = null;
   protected float[][] downCoords = null;
   protected float[][] upCoords = null;
   protected float[][] downVectors = null;
   protected float[][] upVectors = null;
   protected float eps0 = .1f, eps1 = .1f;
   protected int nThreads;
   protected int[] indices = null;

   public ComputeStreamlines(Field inField, Params params)
   {
      this.params  = params;
      nForward     = params.getNForwardSteps();
      nBackward    = params.getNBackwardSteps();
      trueDim      = inField.getTrueDim();
      vects        = inField.getData(params.getVectorComponent()).getFData();
      vlen         = inField.getData(params.getVectorComponent()).getVeclen();
   }


   abstract void updateStreamlines();
   
   public void setStartPoints(Field startPoints)
   {
      nSrc = startPoints.getNNodes();
      if (startPoints.getCoords() != null)
         startCoords = startPoints.getCoords();
      else if (startPoints instanceof RegularField)
         startCoords = ((RegularField)startPoints).getCoordsFromAffine();
      fromSteps = new int[nSrc];
      toSteps   = new int[nSrc];
   }
   
   public float[] getvNorms()
   {
      return vectors;
   } 

   public IrregularField getOutField()
   {
      return outField;
   }
   
   public float[] getCoords()
   {
      return coords;
   }

   public int[] getFromSteps()
   {
      return fromSteps;
   }

   public int[] getToSteps()
   {
      return toSteps;
   }
   
   public int[] getLines()
   {
      return lines;
   }

   public int getNvert()
   {
      return nvert;
   }  
   protected transient FloatValueModificationListener statusListener = null;

   public void addFloatValueModificationListener(FloatValueModificationListener listener)
   {
      if (statusListener == null)
         this.statusListener = listener;
      else
         System.out.println(""+this+": only one status listener can be added");
   }
   
   public void clearFloatValueModificationListener()
   {
      statusListener = null;
   }

    protected void fireStatusChanged(float status)
   {
       FloatValueModificationEvent e = new FloatValueModificationEvent(this, status, true);
       if (statusListener != null)
          statusListener.floatValueChanged(e);
   }
   
}