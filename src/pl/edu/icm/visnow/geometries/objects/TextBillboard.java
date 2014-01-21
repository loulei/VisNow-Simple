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

import com.sun.j3d.utils.geometry.Text2D;
import javax.media.j3d.Billboard;
import javax.media.j3d.BoundingBox;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.Bounds;
import javax.media.j3d.Geometry;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.IndexedLineStripArray;
import javax.media.j3d.Node;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TriangleStripArray;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import pl.edu.icm.visnow.geometries.objects.generics.OpenAppearance;
import pl.edu.icm.visnow.geometries.objects.generics.OpenLineAttributes;
import pl.edu.icm.visnow.geometries.objects.generics.OpenShape3D;
import pl.edu.icm.visnow.geometries.parameters.FontParams;

/**
 *
 * @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class TextBillboard
{

   public static TransformGroup createBillboard(String text,
           FontParams fontParams,
           float[] coords, int nMode, BoundingSphere bounds)
   {
      return createBillboard(text, fontParams, fontParams.getColor3f(), 1.0f, coords, nMode, bounds);
   }

   public static TransformGroup createBillboard(String text,
           FontParams fontParams, Color3f color, float scale,
           float[] coords, int nMode, BoundingSphere bounds)
   {
      TransformGroup subTg = new TransformGroup();
      subTg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
      Text2D ti = new Text2D(text, color, fontParams.getFontName(), fontParams.getFontSize(), fontParams.getFontType());
      ti.setRectangleScaleFactor(scale * fontParams.getFont3DSize() / fontParams.getFontSize());
      float[] m = new float[16];
      for (int j = 0; j < m.length; j++)
         m[j] = 0;
      m[15] = 1;
      for (int j = 0; j < 3; j++)
      {
         m[5 * j] = 1;
         m[4 * j + 3] = coords[j];
      }
      TransformGroup tGr = new TransformGroup();
      tGr.setTransform(new Transform3D(m));
      tGr.addChild(ti);      
      subTg.addChild(tGr);
      Billboard billboard = new Billboard(subTg, Billboard.ROTATE_ABOUT_POINT, new Point3f(coords));
      billboard.setSchedulingBounds(bounds);
      subTg.addChild(billboard);
      return subTg;
   }

    public static TransformGroup createBillboard(String text,
            FontParams fontParams, Color3f color, float scale,
            float[] coords, int nMode, BoundingSphere bounds,
            boolean center, 
            boolean addOutline, 
            boolean vertical) {
        
        TransformGroup subTg = new TransformGroup();
        subTg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        Text2D ti = new Text2D(text, color, fontParams.getFontName(), fontParams.getFontSize(), fontParams.getFontType());
        ti.setRectangleScaleFactor(scale * fontParams.getFont3DSize() / fontParams.getFontSize());

        BoundingBox bb = (BoundingBox) ti.getBounds();
        Point3d p0 = new Point3d();
        bb.getLower(p0);
        Point3d p1 = new Point3d();
        bb.getUpper(p1);
        float w = (float) (p1.x - p0.x);
        float h = (float) (p1.y - p0.y);
        
        TransformGroup intTranslTg = new TransformGroup();
        Transform3D intTranslTr = new Transform3D();
        if(center)
            intTranslTr.setTranslation(new Vector3f(-w/2, -h/2, 0));
        intTranslTg.setTransform(intTranslTr);
        intTranslTg.addChild(ti);

        TransformGroup intRotTg = new TransformGroup();
        Transform3D intRotTr = new Transform3D();
        if(vertical)
            intRotTr.rotZ(Math.PI/2);
        intRotTg.setTransform(intRotTr);
        intRotTg.addChild(intTranslTg);
        

        float[] m = new float[16];
        for (int j = 0; j < m.length; j++) {
            m[j] = 0;
        }
        m[15] = 1;
        for (int j = 0; j < 3; j++) {
            m[5 * j] = 1;
            m[4 * j + 3] = coords[j];
        }                
        TransformGroup tGr = new TransformGroup();
        tGr.setTransform(new Transform3D(m));
        tGr.addChild(intRotTg);

        if (addOutline) {

            float[] outlineCoords = new float[]{
                (float) p0.x, (float) p0.y, (float) p0.z,
                (float) p0.x, (float) p0.y + h, (float) p0.z,
                (float) p0.x + w, (float) p0.y, (float) p0.z,
                (float) p0.x + w, (float) p0.y + h, (float) p0.z
            };

            IndexedLineStripArray outlineGeometry = new IndexedLineStripArray(4,
                    GeometryArray.COORDINATES | GeometryArray.BY_REFERENCE_INDICES
                    | GeometryArray.BY_REFERENCE | GeometryArray.USE_COORD_INDEX_ONLY
                    | GeometryArray.COLOR_3,
                    8, new int[]{8});
            int[] outlineIndices = new int[]{0, 1, 1, 3, 3, 2, 2, 0};
            outlineGeometry.setCoordIndicesRef(outlineIndices);
            outlineGeometry.setCapability(TriangleStripArray.ALLOW_COUNT_READ);
            outlineGeometry.setCapability(TriangleStripArray.ALLOW_FORMAT_READ);
            outlineGeometry.setCapability(TriangleStripArray.ALLOW_COORDINATE_READ);
            outlineGeometry.setCapability(TriangleStripArray.ALLOW_COORDINATE_WRITE);
            outlineGeometry.setCapability(TriangleStripArray.ALLOW_COLOR_READ);
            outlineGeometry.setCapability(TriangleStripArray.ALLOW_COLOR_WRITE);
            outlineGeometry.setCapability(TriangleStripArray.ALLOW_COORDINATE_READ);
            outlineGeometry.setCapability(TriangleStripArray.ALLOW_COORDINATE_WRITE);
            outlineGeometry.setCapability(GeometryArray.ALLOW_REF_DATA_READ);
            outlineGeometry.setCapability(GeometryArray.ALLOW_REF_DATA_WRITE);
            outlineGeometry.setCoordRefFloat(outlineCoords);

            OpenShape3D outline = new OpenShape3D();
            outline.addGeometry(outlineGeometry);
            outline.setCapability(Shape3D.ENABLE_PICK_REPORTING);
            outline.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
            outline.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
            outline.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
            outline.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
            outline.setCapability(Geometry.ALLOW_INTERSECT);
            outline.setCapability(Node.ALLOW_LOCAL_TO_VWORLD_READ);

            OpenAppearance lineApp = new OpenAppearance();
            OpenLineAttributes lineAttr = new OpenLineAttributes(1.f, OpenLineAttributes.PATTERN_SOLID, true);
            lineApp.setLineAttributes(lineAttr);
            outline.setAppearance(lineApp);

            float[] outlineColor = new float[]{1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f};
            outlineGeometry.setColorRefFloat(outlineColor);
            intTranslTg.addChild(outline);
        }

        subTg.addChild(tGr);

        Point3f rotationPoint = new Point3f(coords);
        Billboard billboard = new Billboard(subTg, Billboard.ROTATE_ABOUT_POINT, rotationPoint);
        billboard.setSchedulingBounds(bounds);
        subTg.addChild(billboard);
        return subTg;
    }
   
}
