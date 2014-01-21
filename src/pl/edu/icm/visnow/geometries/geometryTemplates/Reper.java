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

package pl.edu.icm.visnow.geometries.geometryTemplates;

import javax.media.j3d.GeometryArray;
import javax.media.j3d.IndexedGeometryStripArray;
import javax.media.j3d.IndexedTriangleStripArray;
import javax.media.j3d.PolygonAttributes;
import javax.vecmath.Color3f;
import pl.edu.icm.visnow.geometries.objects.GeometryObject;
import pl.edu.icm.visnow.geometries.objects.generics.OpenAppearance;
import pl.edu.icm.visnow.geometries.objects.generics.OpenMaterial;
import pl.edu.icm.visnow.geometries.objects.generics.OpenShape3D;

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class Reper extends GeometryObject
{
    protected int   nverts;
   /**
    * number of indices (vertices of rendered triangles/lines
    */
   protected int   ninds;
   /**
    * number of polytriangle strips/fans
    */
   protected int   nstrips;
   /**
    * numbers of vertices in each triangle strip/fan
    */
   protected int[] strips;
   /**
    * number of polylines
    */
   protected int   nlstrips;
   /**
    * numbers of vertices in each polyline
    */
   protected int[] lstrips;
   /**
    * vertex coordinates <p>
    * (recommended normalization to -1...1, (0,0,0) will be used as grid origin,
    * z - coordinate is will be used as vector direction
    *
    */
   protected float verts[];
   /**
    * normals coordinates
    */
   protected float normals[];
   /**
    * indices to points in all strips
    */
   protected int   pntsIndex[];
   /**
    * indices to colors (array of length ninds filled by zeros)
    */
   protected int   clrsIndex[];
  
   protected IndexedGeometryStripArray surf = null;
   protected OpenAppearance appearance = new OpenAppearance();
   
      /** Creates a new instance of SphereTemplate */
      public Reper(Integer lod)
      {
         int n = 4 * lod + 5;
         nstrips = 2;
         nverts = 4 * n + 4;
         ninds = 4 * n + 4;
         nstrips *= 3;
         nverts  *= 3;
         ninds   *= 3;
         strips = new int[]{2 * n + 2, 2 * n + 2, 2 * n + 2, 2 * n + 2, 2 * n + 2, 2 * n + 2};

         verts = new float[3 * nverts];
         normals = new float[3 * nverts];
         pntsIndex = new int[nverts];
         clrsIndex = new int[nverts];
         int m = 6 * n + 6;

         for (int i = 0; i < verts.length; i++) {
            verts[i] = 0;
         }
         for (int i = 0; i < n + 1; i++) {
            double phi = -2. * (Math.PI * i) / n;
            float c = (float)Math.cos(phi);
            float s = (float)Math.sin(phi);
            verts[6 * i    ] = verts[6 * i + 3] = .06f * c;
            verts[6 * i + 1] = verts[6 * i + 4] = .06f * s;
            verts[6 * i + 2] = 0;
            verts[6 * i + 5] = .8f;
            normals[6 * i    ] = normals[6 * i + 3] = c;
            normals[6 * i + 1] = normals[6 * i + 4] = s;
            normals[6 * i + 2] = normals[6 * i + 5] = 0;
            verts[m + 6 * i    ] = .15f * c;
            verts[m + 6 * i + 1] = .15f * s;
            verts[m + 6 * i + 2] = .6f;
            verts[m + 6 * i + 3] = verts[m + 6 * i + 4] = 0;
            verts[m + 6 * i + 5] = 1f;
            normals[m + 6 * i    ] = normals[m + 6 * i + 3] = c;
            normals[m + 6 * i + 1] = normals[m + 6 * i + 4] = s;
            normals[m + 6 * i + 2] = normals[m + 6 * i + 5] = 0;
            
            verts[2 * m + 6 * i + 1] = verts[2 * m + 6 * i + 4] = .06f * c;
            verts[2 * m + 6 * i + 2] = verts[2 * m + 6 * i + 5] = .06f * s;
            verts[2 * m + 6 * i    ] = 0;
            verts[2 * m + 6 * i + 3] = .8f;
            normals[2 * m + 6 * i + 1] = normals[2 * m + 6 * i + 4] = c;
            normals[2 * m + 6 * i + 2] = normals[2 * m + 6 * i + 5] = s;
            normals[2 * m + 6 * i    ] = normals[2 * m + 6 * i + 3] = 0;
            verts[3 * m + 6 * i + 1] = .15f * c;
            verts[3 * m + 6 * i + 2] = .15f * s;
            verts[3 * m + 6 * i    ] = .6f;
            verts[3 * m + 6 * i + 4] = verts[3 * m + 6 * i + 5] = 0;
            verts[3 * m + 6 * i + 3] = 1f;
            normals[3 * m + 6 * i + 1] = normals[3 * m + 6 * i + 4] = c;
            normals[3 * m + 6 * i + 2] = normals[3 * m + 6 * i + 5] = s;
            normals[3 * m + 6 * i    ] = normals[3 * m + 6 * i + 3] = 0;
            
            verts[4 * m + 6 * i + 2] = verts[4 * m + 6 * i + 5] = .06f * c;
            verts[4 * m + 6 * i    ] = verts[4 * m + 6 * i + 3] = .06f * s;
            verts[4 * m + 6 * i + 1] = 0;
            verts[4 * m + 6 * i + 4] = .8f;
            normals[4 * m + 6 * i + 2] = normals[4 * m + 6 * i + 5] = c;
            normals[4 * m + 6 * i    ] = normals[4 * m + 6 * i + 3] = s;
            normals[4 * m + 6 * i + 1] = normals[4 * m + 6 * i + 4] = 0;
            verts[5 * m + 6 * i + 2] = .15f * c;
            verts[5 * m + 6 * i    ] = .15f * s;
            verts[5 * m + 6 * i + 1] = .6f;
            verts[5 * m + 6 * i + 5] = verts[5 * m + 6 * i + 3] = 0;
            verts[5 * m + 6 * i + 4] = 1f;
            normals[5 * m + 6 * i + 2] = normals[5 * m + 6 * i + 5] = c;
            normals[5 * m + 6 * i    ] = normals[5 * m + 6 * i + 3] = s;
            normals[5 * m + 6 * i + 1] = normals[3 * m + 6 * i + 4] = 0;
         }
         for (int i = 0; i < pntsIndex.length; i++) 
         {
            pntsIndex[i] = i;
            clrsIndex[i] = i / (4 * n + 4);
         }
         Color3f[] colors = new Color3f[]{new Color3f(.1f, .1f, 1.f),new Color3f(1.f, .05f, .05f),new Color3f(0.f, 1.f, 0.f)};
         surf = new IndexedTriangleStripArray(nverts,
                 GeometryArray.COORDINATES | GeometryArray.COLOR_3 | GeometryArray.NORMALS ,
                 ninds, strips);
         surf.setColorIndices(0, clrsIndex);
         surf.setCoordinates(0, verts);
         surf.setCoordinateIndices(0, pntsIndex);
         surf.setNormals(0, normals);
         surf.setNormalIndices(0, pntsIndex);
         surf.setColors(0, colors);  
         OpenMaterial mat = new OpenMaterial();
         mat.setShininess(25.f);
         mat.setColorTarget(OpenMaterial.AMBIENT_AND_DIFFUSE);
         mat.setSpecularColor(.5f, .5f, .5f);
         appearance.setMaterial(mat);
         appearance.getPolygonAttributes().setCullFace(PolygonAttributes.CULL_NONE);
         appearance.getPolygonAttributes().setBackFaceNormalFlip(true);
         OpenShape3D surfaces = new OpenShape3D();
         surfaces.setAppearance(appearance);
         surfaces.addGeometry(surf);
         geometryObj.removeAllChildren();
         geometryObj.addChild(surfaces);
      }

      public Reper()
      {
         this(10);
      }
}
