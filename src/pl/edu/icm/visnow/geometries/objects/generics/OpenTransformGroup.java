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

package pl.edu.icm.visnow.geometries.objects.generics;

import javax.media.j3d.Node;
import javax.media.j3d.TransformGroup;
import pl.edu.icm.visnow.geometries.viewer3d.Display3DPanel;

/**
 *
 * @author  Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class OpenTransformGroup extends TransformGroup
{
   /** Creates a new instance of OpenTransformGroup */
   public OpenTransformGroup() 
   {
      setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
      setCapability(TransformGroup.ALLOW_CHILDREN_READ);
      setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
      setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
      setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
      setCapability(TransformGroup.ALLOW_LOCAL_TO_VWORLD_READ);
   }

   public OpenTransformGroup(String name) 
   {
      super.setName(name);
      setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
      setCapability(TransformGroup.ALLOW_CHILDREN_READ);
      setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
      setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
      setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
      setCapability(TransformGroup.ALLOW_LOCAL_TO_VWORLD_READ);
   }
   
   @Override
   public  void setUserData(Object userData)
   {
      super.setUserData(userData);
      for (int i = 0; i < numChildren(); i++)
         if (getChild(i) != null && userData != null)
            getChild(i).setUserData(getUserData());
   }

   public void printDebugInfo()
   {
      System.out.println(getName()+": "+this);
      int n = this.numChildren();
      System.out.println("group: "+n);
      for (int i=0;i<n;i++)
      {
         System.out.println("obj "+i);
         Node child = this.getChild(i);
         if (child instanceof OpenBranchGroup)
            ((OpenBranchGroup)child).printDebugInfo();
         if (child instanceof OpenTransformGroup)
            ((OpenTransformGroup)child).printDebugInfo();
      }
   }

   public Node cloneNode(boolean forceDuplicate)
   {
      OpenTransformGroup openTransformGroup = new OpenTransformGroup();
      openTransformGroup.duplicateNode(this, forceDuplicate);
      return openTransformGroup;
   }

   public void setCurrentViewer(Display3DPanel panel) {
       for (int i = 0; i < this.numChildren(); i++) {
           Node n = this.getChild(i);
           if(n instanceof OpenBranchGroup) {
               ((OpenBranchGroup)n).setCurrentViewer(panel);
           } else if(n instanceof OpenTransformGroup) {
               ((OpenTransformGroup)n).setCurrentViewer(panel);
           }
       }
   }
   
}
