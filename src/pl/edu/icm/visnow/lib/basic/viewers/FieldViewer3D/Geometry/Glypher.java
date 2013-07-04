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

package pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.Geometry;

import java.util.ArrayList;
import javax.media.j3d.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pl.edu.icm.visnow.geometries.geometryTemplates.Glyph;
import pl.edu.icm.visnow.geometries.geometryTemplates.Templates.SphereTemplate;
import pl.edu.icm.visnow.geometries.geometryTemplates.Templates.TubeTemplate;
import pl.edu.icm.visnow.geometries.objects.GeometryObject;
import pl.edu.icm.visnow.geometries.objects.generics.*;

/**
 *
 * @author Bartosz Borucki (babor@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class Glypher {
    private GeometryObject glyphs = new GeometryObject("glyphs");
    private GeometryParams params;
    private ArrayList<PointDescriptor> points = new ArrayList<PointDescriptor>();


    private Glyph gt = new SphereTemplate(4);
    private Glyph ct = new TubeTemplate(16);

    private OpenAppearance app = new OpenAppearance();
    private OpenMaterial mat = new OpenMaterial();

    public Glypher() {
        prepare3DParams();
    }

    public void setParams(GeometryParams params) {
        this.params = params;
        this.points = params.getPointsDescriptors();
        this.params.addGeometryParamsListener(new GeometryParamsListener() {
            @Override
            public void onGeometryParamsChanged(GeometryParamsEvent e) {
                if(e.getType() == GeometryParamsEvent.TYPE_ALL ||
                        e.getType() == GeometryParamsEvent.TYPE_CONN3D ||
                        e.getType() == GeometryParamsEvent.TYPE_CONNECTION ||
                        e.getType() == GeometryParamsEvent.TYPE_GLYPHS ||
                        e.getType() == GeometryParamsEvent.TYPE_POINT_ADDED ||
                        e.getType() == GeometryParamsEvent.TYPE_POINT_MODIFIED ||
                        e.getType() == GeometryParamsEvent.TYPE_POINT_REMOVED ||
                        e.getType() == GeometryParamsEvent.TYPE_POINT_SELECTION )
                            update();
            }
        });
        update();
    }

    public void update() {
        glyphs.clearAllGeometry();
        if(params.getInField() == null || points == null || points.size() < 1)
            return;

        float[][] affine = params.getInField().getAffine();
        float[] p = new float[3];
        float d = 0, s;
        for (int i = 0; i < 3; i++) {
            p[i] = affine[0][i] + affine[1][i] + affine[2][i];
            d += p[i]*p[i];
        }
        float unitSize = (float)Math.sqrt(d);

        if(params.isShowGlyphs() && params.getPointsDescriptors() != null && params.getPointsDescriptors().size() > 0) {
            int nGlyphs = points.size();
            int nstrip = nGlyphs * gt.getNstrips();
            int nvert = nGlyphs * gt.getNverts();
            int nind = nGlyphs * gt.getNinds();
            int ncol = nGlyphs;
            int[] strips = new int[nstrip];
            float[] verts = new float[3 * nvert];
            float[] normals = new float[3 * nvert];
            int[] pIndex = new int[nind];
            int[] cIndex = new int[nind];
            float[] colors = new float[3 * ncol];

            makeIndices(nGlyphs, gt, strips, pIndex, cIndex);

            makeRegularData(unitSize*params.getGlyphScale(), gt, verts, normals, colors);

            IndexedGeometryStripArray surf = new IndexedTriangleStripArray(nvert, GeometryArray.COORDINATES|GeometryArray.NORMALS|GeometryArray.COLOR_3, nind, strips);
            OpenShape3D surfaces = new OpenShape3D();
            OpenBranchGroup gl = new OpenBranchGroup();
            surf.setCapability(IndexedLineStripArray.ALLOW_COUNT_READ);
            surf.setCapability(IndexedLineStripArray.ALLOW_FORMAT_READ);
            surf.setCapability(IndexedLineStripArray.ALLOW_COORDINATE_INDEX_READ);
            surf.setCapability(IndexedLineStripArray.ALLOW_COORDINATE_READ);
            surf.setCapability(IndexedLineStripArray.ALLOW_COORDINATE_WRITE);
            surf.setCapability(IndexedLineStripArray.ALLOW_COLOR_READ);
            surf.setCapability(IndexedLineStripArray.ALLOW_COLOR_WRITE);
            surf.setCoordinates(0, verts);
            surf.setCoordinateIndices(0, pIndex);
            surf.setColors(0, colors);
            surf.setColorIndices(0, cIndex);
            if (normals != null)
            {
                surf.setCapability(IndexedLineStripArray.ALLOW_NORMAL_READ);
                surf.setCapability(IndexedLineStripArray.ALLOW_NORMAL_WRITE);
                surf.setNormals(0, normals);
                surf.setNormalIndices(0, pIndex);
            }
            surfaces.addGeometry(surf);
            surfaces.setCapability(Shape3D.ENABLE_PICK_REPORTING);
            surfaces.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
            surfaces.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
            surfaces.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
            surfaces.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
            surfaces.setCapability(Geometry.ALLOW_INTERSECT);
            surfaces.setCapability(Node.ALLOW_LOCAL_TO_VWORLD_READ);
            surfaces.setUserData("surf");
            surfaces.setAppearance(app);


            gl.addChild(surfaces);
            glyphs.addNode(gl);
        }


        if(params.isShowConnections3D() && params.getConnectionDescriptors() != null && params.getConnectionDescriptors().size() > 0) {
           ArrayList<ConnectionDescriptor> conns = params.getConnectionDescriptors();
           int nConns = conns.size();
           int nstrip = nConns * ct.getNstrips();
           int nverts = nConns * ct.getNverts();
           int[] strips = new int[nstrip];
           for (int i = 0; i < strips.length; i++)
              strips[i] = ct.getStrips()[0];
           float[] verts = new float[3 * nverts];
           float[] normals = new float[3 * nverts];
           float[] colors = new float[3 * nverts];
           int ivert = 0;

           float r0 = unitSize/2 * params.getConnectionScale();
           float[] c0 = {255,0,0};
           for (int k = 0; k < nConns; k++) {
                float[] p0 = conns.get(k).getP1().getWorldCoords();
                float[] p1 = conns.get(k).getP2().getWorldCoords();
                addTubeSegment(p0, p1, r0, r0, c0, c0, ivert, verts, normals, colors);
                ivert += ct.getNverts();
           }

           TriangleStripArray surf = new TriangleStripArray(nverts,
                     GeometryArray.COORDINATES | GeometryArray.NORMALS | GeometryArray.COLOR_3,
                     strips);
           OpenBranchGroup gl = new OpenBranchGroup();
           OpenShape3D surfaces = new OpenShape3D();
           surf.setCapability(TriangleStripArray.ALLOW_COUNT_READ);
           surf.setCapability(TriangleStripArray.ALLOW_FORMAT_READ);
           surf.setCapability(TriangleStripArray.ALLOW_COORDINATE_READ);
           surf.setCapability(TriangleStripArray.ALLOW_COORDINATE_WRITE);
           surf.setCapability(TriangleStripArray.ALLOW_COLOR_READ);
           surf.setCapability(TriangleStripArray.ALLOW_COLOR_WRITE);
           surf.setCapability(TriangleStripArray.ALLOW_COORDINATE_READ);
           surf.setCapability(TriangleStripArray.ALLOW_COORDINATE_WRITE);
           surf.setCoordinates(0, verts);
           surf.setColors(0, colors);
           surf.setCapability(GeometryArray.ALLOW_NORMAL_READ);
           surf.setCapability(GeometryArray.ALLOW_NORMAL_WRITE);
           surf.setNormals(0, normals);
           surfaces.addGeometry(surf);
           surfaces.setCapability(Shape3D.ENABLE_PICK_REPORTING);
           surfaces.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
           surfaces.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
           surfaces.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
           surfaces.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
           surfaces.setCapability(Geometry.ALLOW_INTERSECT);
           surfaces.setCapability(Node.ALLOW_LOCAL_TO_VWORLD_READ);
           surfaces.setUserData("tubes");
           surfaces.setAppearance(app);
           gl.addChild(surfaces);
           glyphs.addNode(gl);
        }


        fireStateChanged();
    }

      private void addTubeSegment(float[] p0, float[] p1, float r0, float r1, float[] c0, float[] c1,
                                int ivert, float[] verts, float[] normals, float[] colors) {
        float[] u = {0,0,0};
        float[] v = {0,0,0};
        float[] w = new float[3];
        for (int i = 0; i < 3; i++)
           w[i] = p1[i]-p0[i];
        float s = (float) (Math.sqrt(w[0]*w[0] + w[1]*w[1] + w[2]*w[2]));
        int nverts = ct.getNverts();
        float[] tVerts = ct.getVerts();
        float[] tNorms = ct.getNormals();
        if (s > 0.f)
        {
            for (int i = 0; i < 3; i++)
               w[i]/=s;
            int k = 0;
            if (Math.abs(w[1])<Math.abs(w[0]))
               k = 1;
            if (Math.abs(w[2])<Math.abs(w[k]))
               k = 2;
            u[k] = 1;
            for (int i = 0; i < 3; i++)
               u[i] -= w[i]*w[k];
            s = (float) (Math.sqrt(u[0]*u[0] + u[1]*u[1] + u[2]*u[2]));
            for (int i = 0; i < 3; i++)
               u[i]/=s;
            v[0] = u[1]*w[2]-u[2]*w[1];
            v[1] = u[2]*w[0]-u[0]*w[2];
            v[2] = u[0]*w[1]-u[1]*w[0];
         }
        else
           for (int m = 0; m < 3; m++)
               u[m] = v[m] = 0;
        for (int l = 0; l < nverts; l+=2)
            for (int m = 0; m < 3; m++)
            {
                verts[3*(ivert+l) + m] = r0*(float)(tVerts[3*l] * u[m] + tVerts[3*l+1] * v[m]) + p0[m];
                normals[3*(ivert+l) + m] =  (float)(tNorms[3*l] * u[m] + tNorms[3*l+1] * v[m]);
                colors[3*(ivert+l) + m] = c0[m];
            }
        for (int l = 1; l < nverts; l+=2)
            for (int m = 0; m < 3; m++)
            {
                verts[3*(ivert+l) + m] = r1*(float)(tVerts[3*l] * u[m] + tVerts[3*l+1] * v[m]) + p1[m];
                normals[3*(ivert+l) + m] =  (float)(tNorms[3*l] * u[m] + tNorms[3*l+1] * v[m]);
                colors[3*(ivert+l) + m] = c1[m];
            }
    }


    public GeometryObject getGlyphsObject() {
        return glyphs;
    }

    private void makeIndices(int nGlyphs, Glyph gt, int[] strips, int[] pIndex, int[] cIndex)
    {
        int istrip = 0, iind = 0, ivert = 0, icol = 0;
        for (int n = 0; n < nGlyphs; n++)
        {
            for (int i = 0; i < gt.getNstrips(); i++)
            {
                strips[istrip] = gt.getStrips()[i];
                istrip += 1;
            }
            for (int i = 0; i < gt.getNinds(); i++)
            {
                pIndex[iind] = ivert + gt.getPntsIndex()[i];
                cIndex[iind] = icol;
                iind += 1;
            }
            ivert += gt.getNverts();
            icol += 1;
        }
    }


    private void makeRegularData(float scale, Glyph gt, float[] verts, float[] normals, float[] colors) {
        float[] tVerts = gt.getVerts();
        float[] tNorms = gt.getNormals();
        int ivert = 0, icol = 0;
            ivert = icol = 0;
            for (int ip = 0; ip < points.size(); ip++) {
                float[] p = points.get(ip).getWorldCoords();
                float[] lc = new float[3];
                lc[0] = 1.0f;
                lc[1] = 0.0f;
                lc[2] = 0.0f;
                if(params.getSelectedPoints() != null) {
                    for (int i = 0; i < params.getSelectedPoints().length; i++) {
                        if(params.getSelectedPoints()[i] == ip) {
                            lc[0] = 200.0f/255.0f;
                            lc[1] = 0.0f;
                            lc[2] = 1.0f;
                            break;
                        }
                    }
                }
                addGlyph(p, scale, ivert, icol, gt.getNverts(), tVerts, tNorms, verts, normals, colors, lc);
                ivert += gt.getNverts();
                icol += 3;
            }
   }


    private void addGlyph(float[] p, float scale, int ivert, int icol,
            int nverts, float[] tVerts, float[] tNorms,
            float[] verts, float[] normals, float[] colors, float[] currentColor)
    {        
        System.arraycopy(currentColor, 0, colors, icol, 3);
        for (int l = 0; l < nverts; l++)
        {
            for (int m = 0; m < 3; m++)
            {
                verts[3 * ivert + m] = scale * tVerts[3 * l + m] + p[m];
                normals[3 * ivert + m] = tNorms[3 * l + m];
            }
            ivert += 1;
        }
    }


   /**
    * Utility field holding list of ChangeListeners.
    */
   private transient ArrayList<ChangeListener> changeListenerList =
           new ArrayList<ChangeListener>();

   /**
    * Registers ChangeListener to receive events.
    * @param listener The listener to register.
    */
   public synchronized void addChangeListener(ChangeListener listener)
   {
      changeListenerList.add (listener);
   }

   /**
    * Removes ChangeListener from the list of listeners.
    * @param listener The listener to remove.
    */
   public synchronized void removeChangeListener(ChangeListener listener)
   {
      changeListenerList.remove (listener);
   }

   /**
    * Notifies all registered listeners about the event.
    *
    * @param object Parameter #1 of the <CODE>ChangeEvent<CODE> constructor.
    */
   private void fireStateChanged()
   {
      ChangeEvent e = new ChangeEvent (this);
      for (ChangeListener listener: changeListenerList)
         listener.stateChanged(e);
   }

    private void prepare3DParams() {
        mat.setShininess(15.f);
        mat.setColorTarget(OpenMaterial.AMBIENT_AND_DIFFUSE);
        app.setMaterial(mat);
        app.setColoringAttributes(new ColoringAttributes(1.f, 1.f, 1.f, 0));
        PolygonAttributes pattr = new PolygonAttributes(
                PolygonAttributes.POLYGON_FILL,
                PolygonAttributes.CULL_NONE, 0.f, true);
        app.setPolygonAttributes(pattr);
        OpenLineAttributes lattr = new OpenLineAttributes(1.f,
                OpenLineAttributes.PATTERN_SOLID, true);
        app.setLineAttributes(lattr);
    }

}
