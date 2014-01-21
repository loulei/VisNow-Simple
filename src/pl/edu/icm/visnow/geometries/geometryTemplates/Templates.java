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

/**
 *
 ** @author Krzysztof S. Nowinski, University of Warsaw ICM
 */
public class Templates 
{
   
//add new glyphs here
   
   public static class SegmentTemplate extends Glyph
   {
      public SegmentTemplate(Integer lod)
      {
         nstrips = 1;
         nverts  = 2;
         ninds  = 2;

         strips = new int[]{2};
         verts = new float[]{0, 0, 0, 0, 0, 1};
         normals   = new float[6];
         pntsIndex = new int[]{0,1};
         clrsIndex = new int[]{0,0};
      }

      public String getName()
      {
         return "segment";
      }

      public int getType()
      {
         return LINE_STRIPS;
      }

      public boolean isRefinable()
      {
         return false;
      }
   }
   
   public static class SymmetricSegmentTemplate extends Glyph
   {

      /** Creates a new instance of DiamondTemplate */
      public SymmetricSegmentTemplate(Integer lod)
      {
         nstrips = 1;
         nverts  = 2;
         ninds  = 2;

         strips = new int[]{2};
         verts = new float[]{0, 0, -1, 0, 0, 1};
         normals   = new float[6];
         pntsIndex = new int[]{0,1};
         clrsIndex = new int[]{0,0};
      }

      public String getName()
      {
         return "symmetric segment";
      }

      public int getType()
      {
         return LINE_STRIPS;
      }

      public boolean isRefinable()
      {
         return false;
      }
   }

   public static class SimpleArrowTemplate extends Glyph
   {

      /** Creates a new instance of DiamondTemplate */
      public SimpleArrowTemplate(Integer lod)
      {
         nstrips = 2;
         nverts  = 5;
         ninds  = 6;

         strips = new int[]{3, 3};
         verts = new float[]{0,     0,   0,     0,    0,   1,  .1f,  0, .8f,
                         -.05f, -.08f, .8f, -.05f, .08f, .8f};
         normals   = new float[15];
         pntsIndex = new int[]{0,1,2,3,1,4};
         clrsIndex = new int[]{0, 0, 0, 0, 0, 0};
      }

      public String getName()
      {
         return "simple arrow";
      }

      public int getType()
      {
         return LINE_STRIPS;
      }

      public boolean isRefinable()
      {
         return false;
      }
   }

   public static class ArrowTemplate extends Glyph
   {

      /** Creates a new instance of DiamondTemplate */
      public ArrowTemplate(Integer lod)
      {
         nstrips = 1;
         nverts  = 6;
         ninds  = 8;

         strips = new int[1];


         float[] v = {0,   0,   0,    0,   0, 1.f,
                      0, .1f, .8f,    0,-.1f,  .8f,
                     .1f,  0, .8f, -.1f,   0,  .8f};
         verts = v;
         normals   = new float[18];
         int[] pInd = {0,1,2,3,1,4,5,1};
         pntsIndex = pInd;
         clrsIndex = new int[ninds];
         strips[0]=ninds;
         for (int i = 0; i < clrsIndex.length; i++)
            clrsIndex[i] = 0;
      }

      public String getName()
      {
         return "arrow";
      }

      public int getType()
      {
         return LINE_STRIPS;
      }

      public boolean isRefinable()
      {
         return false;
      }

   }
   
   public static class CircleTemplate extends Glyph
   {

      /** Creates a new instance of DiamondTemplate */
      public CircleTemplate(Integer lod)
      {
         nstrips = 1;
         nverts  = 4*lod+1;
         ninds   = 4*lod+1;
         strips = new int[1];


         verts     = new float[3*nverts];
         pntsIndex = new int[nverts];
         clrsIndex = new int[nverts];
         normals   = new float[3*nverts];


         strips[0]=nverts;
         for (int i = 0; i < verts.length; i++)
            verts[i] = 0;
         for (int i=0;i<=4*lod;i++)
         {
            double phi = (Math.PI*i)/(2.*lod);
            verts[3*i]     = (float)(Math.cos(phi)/6.);
            verts[3*i+1]   = (float)(Math.sin(phi)/6.);
         }
         for (int i = 0; i < pntsIndex.length; i++)
         {
            pntsIndex[i] = i;
            clrsIndex[i] = 0;
         }
      }

      public CircleTemplate()
      {
         this(5);
      }

      public String getName()
      {
         return "circle";
      }

      public int getType()
      {
         return LINE_STRIPS;
      }

      public boolean isRefinable()
      {
         return true;
      }
   }

   /**
    * A diamond glyph.
    */
   public static class DiamondTemplate extends Glyph
   {

      /** Creates a new instance of DiamondTemplate */
      public DiamondTemplate(Integer lod)
      {
         nstrips = 1;
         nverts  = 6;
         ninds  = 13;

         strips = new int[1];


         float[] v = {1, 0, 0,
                     -1, 0, 0,
                      0, 1, 0,
                      0,-1, 0,
                      0, 0, 1,
                      0, 0,-1};
         verts = v;
         normals   = new float[18];
         int[] pInd = {0,2,1,3,0,4,2,5,3,4,1,5,0};
         pntsIndex = pInd;
         clrsIndex = new int[ninds];
         strips[0]=ninds;
         for (int i = 0; i < clrsIndex.length; i++)
            clrsIndex[i] = 0;
      }

      public String getName()
      {
         return "diamond";
      }

      public int getType()
      {
         return LINE_STRIPS;
      }

      public boolean isRefinable()
      {
         return false;
      }
  }

   public static class TubeTemplate extends Glyph
   {
      public TubeTemplate(Integer lod)
      {
         int n = 4*lod+5;
         nstrips = 1;
         nverts  = 2*n+2;
         ninds   = 2*n+2;
         strips = new int[1];

         verts     = new float[3*nverts];
         normals   = new float[3*nverts];
         pntsIndex = new int[nverts];
         clrsIndex = new int[nverts];

         strips[0]=nverts;
         for (int i = 0; i < verts.length; i++)
            verts[i] = normals[i] = 0;
         for (int i=0;i<n+1;i++)
         {
            double phi = -2.*(Math.PI*i)/n;
            verts[6 * i    ] = verts[6 * i + 3] = (float) (Math.cos(phi));
            verts[6 * i + 1] = verts[6 * i + 4] = (float) (Math.sin(phi));
            verts[6 * i + 2] = 0;
            verts[6 * i + 5] = 1;
            normals[6 * i    ]  = normals[6 * i + 1]  = (float)(Math.cos(phi));
            normals[6 * i + 1]  = normals[6 * i + 4]  = (float)(Math.sin(phi));
            normals[6 * i + 2]  = normals[6 * i + 5]  = 0;
         }
         for (int i = 0; i < pntsIndex.length; i++)
         {
            pntsIndex[i] = i;
            clrsIndex[i] = 0;
         }
      }

      public TubeTemplate() {
         this(10);
      }

      public String getName()
      {
         return "tube";
      }

      public int getType()
      {
         return TRIANGLE_STRIPS;
      }

      public boolean isRefinable()
      {
         return true;
      }
   }

   public static class ConeTemplate extends Glyph
   {

      /** Creates a new instance of SphereTemplate */
      public ConeTemplate(Integer lod)
      {
         nstrips = 1;
         nverts  = 4*lod+2;
         ninds   = 4*lod+2;
         strips = new int[1];

         verts     = new float[3*nverts];
         normals   = new float[3*nverts];
         pntsIndex = new int[nverts];
         clrsIndex = new int[nverts];

         strips[0]=nverts;
         for (int i = 0; i < verts.length; i++)
            verts[i] = normals[i] = 0;
         verts[2] = 1;
         normals[2] = 1;
         double s = Math.sqrt(37./36.);
         for (int i=0;i<4*lod+1;i++)
         {
            double phi = (Math.PI*i)/(2.*lod);
            verts[3*(i+1)]     = (float)(Math.cos(phi)/6.);
            verts[3*(i+1)+1]   = (float)(Math.sin(phi)/6.);
            normals[3*(i+1)]   = (float)(Math.cos(phi)/6);
            normals[3*(i+1)+1] = (float)(Math.sin(phi)/6);
            normals[3*(i+1)+2] = 1/(float)(6*s);
         }
         for (int i = 0; i < pntsIndex.length; i++)
         {
            pntsIndex[i] = i;
            clrsIndex[i] = 0;
         }
      }

      public ConeTemplate()
      {
         this(10);
      }

      public String getName()
      {
         return "cone";
      }

      public int getType()
      {
         return TRIANGLE_FANS;
      }

      public boolean isRefinable()
      {
         return true;
      }

   }

   public static class Arrow3dTemplate extends Glyph
   {

      /** Creates a new instance of SphereTemplate */
      public Arrow3dTemplate(Integer lod)
      {
         int n = 4 * lod + 5;
         nstrips = 2;
         nverts = 4 * n + 4;
         ninds = 4 * n + 4;
         strips = new int[]{2 * n + 2, 2 * n + 2};

         verts = new float[3 * nverts];
         normals = new float[3 * nverts];
         pntsIndex = new int[nverts];
         clrsIndex = new int[nverts];
         int m = 6 * n + 6;

         for (int i = 0; i < verts.length; i++) {
            verts[i] = normals[i] = 0;
         }
         for (int i = 0; i < n + 1; i++) {
            double phi = -2. * (Math.PI * i) / n;
            verts[6 * i] = verts[6 * i + 3] = (float) (.06 * Math.cos(phi));
            verts[6 * i + 1] = verts[6 * i + 3 + 1] = (float) (.06 * Math.sin(phi));
            verts[6 * i + 2] = 0;
            verts[6 * i + 5] = .8f;
            normals[3 * i] = normals[3 * (i + 1)] = -(float) (Math.cos(phi));
            normals[3 * i + 1] = normals[3 * (i + 1) + 1] = -(float) (Math.sin(phi));
            normals[3 * i + 2] = normals[3 * (i + 1) + 2] = 0;
            verts[m + 6 * i]     = (float) (.15 * Math.cos(phi));
            verts[m + 6 * i + 1] = (float) (.15 * Math.sin(phi));
            verts[m + 6 * i + 2] = .6f;
            verts[m + 6 * i + 3] = verts[m + 6 * i + 4] = 0;
            verts[m + 6 * i + 5] = 1f;
            normals[m + 3 * i] = normals[m + 3 * (i + 1)] = -(float) (Math.cos(phi));
            normals[m + 3 * i + 1] = normals[m + 3 * (i + 1) + 1] = -(float) (Math.sin(phi));
            normals[m + 3 * i + 2] = normals[m + 3 * (i + 1) + 2] = 0;
         }
         for (int i = 0; i < pntsIndex.length; i++) {
            pntsIndex[i] = i;
            clrsIndex[i] = 0;
         }
      }

      public Arrow3dTemplate()
      {
         this(10);
      }

       public String getName()
      {
         return "arrow 3d";
      }

      public int getType()
      {
         return TRIANGLE_STRIPS;
      }

      public boolean isRefinable()
      {
         return true;
      }

  }


   public static class SphereTemplate extends Glyph
   {

      /** Creates a new instance of SphereTemplate */
      public SphereTemplate(Integer lod)
      {
         nstrips = 4*(lod-1);
         nverts  = 4*lod*lod;
         ninds  = 8*(lod-1)*lod;
         strips = new int[4*(lod-1)];

         int i,j,k,k0,l;
         float t0,t1,t2,r;
         float[] p0 = new float[3];
         float[] p1 = new float[3];
         float[] p2 = new float[3];

         float[] q0 = new float[3];
         float[] q1 = new float[3];
         float[] q2 = new float[3];

         verts     = new float[12*lod*lod];
         normals   = new float[12*lod*lod];
         pntsIndex = new int[8*(lod-1)*lod];
         clrsIndex = new int[8*(lod-1)*lod];

         for (i=0;i<4*(lod-1);i++)
            strips[i]=2*lod;
         p0[0]=0.f;
         p0[1]=0.f;
         p0[2]=1.f;
         p1[0]=1.f;
         p1[1]=0.f;
         p1[2]=0.f;
         p2[0]=0.f;
         p2[1]=1.f;
         p2[2]=0.f;

         q0[0]=0.f;
         q0[1]=1.f;
         q0[2]=0.f;
         q1[0]=0.f;
         q1[1]=0.f;
         q1[2]=1.f;
         q2[0]=-1.f;
         q2[1]=0.f;
         q2[2]=0.f;

         k  = 0;
         k0 = k;
         for (i=0;i<lod;i++)
         {
            for (j=0; j<lod-i; j++)
            {
               t0 = (float)i/(lod-1);
               if (lod-i-1>0)
                  t2 = (1.f-t0)*(float)j/(lod-i-1);
               else
                  t2 = 0.f;
               t1 = 1.f -t0-t2;
               r = 0.f;
               for (l=0;l<3;l++)
               {
                  verts[3*k+l]=t0*p0[l]+t1*p1[l]+t2*p2[l];
                  r+=verts[3*k+l]*verts[3*k+l];
               }
               r = (float)Math.sqrt(r);
               for (l=0;l<3;l++)
                  verts[3*k+l]   /= r;
               k+=1;
            }
            for (j=0; j<i; j++)
            {
               t0 = (float)(lod-1-i)/(lod-1);
               t2 = (1.f-t0)*(float)(j+1)/i;
               t1 = 1.f -t0-t2;
               r = 0.f;
               for (l=0;l<3;l++)
               {
                  verts[3*k+l]=t0*q0[l]+t1*q1[l]+t2*q2[l];
                  r+=verts[3*k+l]*verts[3*k+l];
               }
               r = (float)Math.sqrt(r);
               for (l=0;l<3;l++)
                  verts[3*k+l]   /= r;
               k+=1;
            }
         }

         for (i=0;i<k;i++)
         {
            verts[3*(i+k)]     =  verts[3*i];
            verts[3*(i+k)+1]   = -verts[3*i+2];
            verts[3*(i+k)+2]   =  verts[3*i+1];
            verts[3*(i+2*k)]   =  verts[3*i];
            verts[3*(i+2*k)+1] = -verts[3*i+1];
            verts[3*(i+2*k)+2] = -verts[3*i+2];
            verts[3*(i+3*k)]   =  verts[3*i];
            verts[3*(i+3*k)+1] =  verts[3*i+2];
            verts[3*(i+3*k)+2] = -verts[3*i+1];
         }
         for (i=0;i<3*nverts;i++)
            normals[i]=verts[i];

         l = 0;
         k = lod*lod;
         k0 = 2*lod*(lod-1);
         for (i=0;i<lod-1;i++)
            for (j=0;j<lod;j++)
            {
               pntsIndex[     l]    =    j*lod+i;
               pntsIndex[     l+1]  =    j*lod+i+1;
               pntsIndex[  k0+l]    =  k+j*lod+i;
               pntsIndex[  k0+l+1]  =  k+j*lod+i+1;
               pntsIndex[2*k0+l]    =2*k+j*lod+i;
               pntsIndex[2*k0+l+1]  =2*k+j*lod+i+1;
               pntsIndex[3*k0+l]    =3*k+j*lod+i;
               pntsIndex[3*k0+l+1]  =3*k+j*lod+i+1;
               l+=2;
            }

         for (i=0;i<l;i++)
            clrsIndex[i]=0;
      }

      public SphereTemplate()
      {
         this(3);
      }

      public String getName()
      {
         return "sphere";
      }

      public int getType()
      {
         return TRIANGLE_STRIPS;
      }

      public boolean isRefinable()
      {
         return true;
      }

   }

}

