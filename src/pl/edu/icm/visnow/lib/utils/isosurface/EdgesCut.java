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

package pl.edu.icm.visnow.lib.utils.isosurface;

import java.util.HashMap;
import java.util.Set;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class EdgesCut
{
   public static final int OFF = -1;
   public static final int OLD = 0;
   public static final int NEW = 1;
   protected int nEdges = 0;
   protected int nDuplicates = 0;
   protected float[] data;
   protected float threshold = 0;
   protected float[] coords;
   protected HashMap<Long, EdgeDesc> edgesCut = new HashMap<Long, EdgeDesc>();
   
   public class EdgeDesc
   {
      public int index;
      public float ratio;
      
      public EdgeDesc(int ind, float r)
      {
         index = ind;
         ratio = r;
      }
   }
   
   public EdgesCut(float[] data, float threshold, float[] coords)
   {
      this.data      = data;
      this.threshold = threshold;
      this.coords    = coords;
   }
   
   public int insertEdge(int e0, int e1)
   {  
      float v0 = data[e0]; 
      if (v0 == threshold) v0 += .000001f;
      float v1 = data[e1]; 
      if (v1 == threshold) v1 += .000001f;
      if (e0 == e1 ||
         (threshold - v0) *  (threshold - v1) >= 0)
         return OFF;
      long se;
      if (e0 < e1)
         se = (long)e1 << 32 | (long)e0;
      else
         se = (long)e0 << 32 | (long)e1;
      if (edgesCut.containsKey(se))
      {
         nDuplicates += 1;
         return OLD;
      }
      float t = (threshold - v0) / (v1 - v0);
      if (e0 > e1)
         t = 1 - t;
      edgesCut.put(se, new EdgeDesc(nEdges, t));
      nEdges += 1;
      return NEW;
   }

   public int getnEdges()
   {
      return nEdges;
   }
   
   public EdgeDesc getData(int e0, int e1)
   {
      if (e0 == e1)
         return null;
      long se;
      if (e0 < e1)
      {
         se = (long)e1 << 32 | (long)e0;
         return edgesCut.get(se);
      }
      se = (long)e0 << 32 | (long)e1;
      EdgeDesc ed = edgesCut.get(se);
      return new EdgeDesc(ed.index, 1 - ed.ratio);
   }
   
    public int getIndex(int e0, int e1)
   {
      if (e0 == e1)
         return -1;
      long se;
      if (e0 < e1)
         se = (long)e1 << 32 | (long)e0;
      else
         se = (long)e0 << 32 | (long)e1;
      EdgeDesc ed = edgesCut.get(se);
      if (ed == null)
         return -1;
      return ed.index;
   }

   public int getnDuplicates()
   {
      return nDuplicates;
   }
    
   public EdgeDesc getData(long se)
   {
      return edgesCut.get(se);
   }
   
   public Set<Long> getKeys()
   {
      return edgesCut.keySet();
   }
}
