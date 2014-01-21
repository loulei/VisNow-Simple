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

package pl.edu.icm.visnow.geometries.objects;
import java.awt.Color;
import pl.edu.icm.visnow.geometries.objects.generics.OpenBranchGroup;
import java.util.SortedSet;

import javax.media.j3d.J3DGraphics2D;
import javax.media.j3d.Node;
import javax.media.j3d.Transform3D;
import pl.edu.icm.visnow.geometries.parameters.AbstractRenderingParams;
import pl.edu.icm.visnow.geometries.utils.transform.LocalToWindow;

/**
 *
 * @author Krzysztof S. Nowiński, Warsaw University ICM
 */
public interface GeometryParent
{
   public void addChild(GeometryObject child);
   public OpenBranchGroup getGeometryObj();
   public void clearAllGeometry();
   public void addNode(Node node);
   public void draw2D(J3DGraphics2D vGraphics, LocalToWindow ltw, int h, int w);
   public int getAreaWidth();
   public int getAreaHeight();
   public Color getBackgroundColor();
   public boolean removeChild(GeometryObject child);
   public void setScale(double s);
   public AbstractRenderingParams getRenderingParams();
   @Override
   public String toString();   
   public SortedSet<GeometryObject> getChildren();
   public void revalidate();
   public void printDebugInfo();
   public void setTransparency();
   public void setShininess();
   public void setLineThickness();
   public void setLineStyle();   
   public void setColor();   
   public void setTransform(Transform3D transform);
   public void setExtents(float[][] ext);
   public void updateExtents();
}
