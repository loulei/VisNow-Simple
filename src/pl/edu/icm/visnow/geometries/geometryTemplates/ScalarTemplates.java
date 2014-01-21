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
public class ScalarTemplates 
{
   
//add new glyphs here
   
   public static class DiamondTemplate extends Glyph
   {

      /** Creates a new instance of DiamondTemplate */
      public DiamondTemplate(Integer lod)
      {
         nstrips = 1;
         nverts  = 6;
         ninds  = 13;

         strips = new int[]{13};


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

   public static class BoxTemplate extends Glyph
   {
      /** Creates a new instance of DiamondTemplate */
      public BoxTemplate(Integer lod)
      {
         nstrips = 12;
         nverts  = 8;
         ninds  = 24;

         strips = new int[] { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 };


         float[] v = {-1, -1, -1,
                       1, -1, -1,
                      -1,  1, -1,
                       1,  1, -1,
                      -1, -1,  1,
                       1, -1,  1,
                      -1,  1,  1,
                       1,  1,  1};
         verts = v;
         normals   = new float[24];
         int[] pInd = { 0, 1, 2, 3, 4, 5, 6, 7, 0, 2, 1, 3, 4, 6, 5, 7, 0, 4, 1, 5, 2, 6, 3, 7 };
         pntsIndex = pInd;
         clrsIndex = new int[ninds];
         for (int i = 0; i < clrsIndex.length; i++)
            clrsIndex[i] = 0;
      }

      public String getName()
      {
         return "box";
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

  public static class SnowFlakeTemplate extends Glyph
   {

      /** Creates a new instance of DiamondTemplate */
      public SnowFlakeTemplate(Integer lod)
      {
         nstrips = 3;
         nverts  = 6;
         ninds  = 6;

         strips = new int[]{2, 2, 2};


         float[] v = {1, 0, 0,
                     -1, 0, 0,
                      .5f, .81f, 0,
                      -.5f,-.81f, 0,
                      -.5f, .81f, 0,
                      .5f,-.81f, 0};
         verts = v;
         normals   = new float[18];
         int[] pInd = {0,1,2,3,4,5};
         pntsIndex = pInd;
         clrsIndex = new int[ninds];
         for (int i = 0; i < clrsIndex.length; i++)
            clrsIndex[i] = 0;
      }

      public String getName()
      {
         return "snowflake";
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

   public static class RaindropTemplate extends Glyph
   {

      /** Creates a new instance of SphereTemplate */
      public RaindropTemplate(Integer lod)
      {
         nstrips = 1;
         nverts  = 10;
         ninds  = 10;
         strips = new int[]{10};
         float s = (float)Math.sqrt(.75);
         verts     = new float[]{0, 1.4f, 0,  
                                 s,  .5f, 0,   -s,  .5f, 0, 
                                 1,    0, 0,   -1,    0, 0, 
                                 s, -.5f, 0,   -s, -.5f, 0, 
                               .5f,   -s, 0, -.5f,   -s, 0, 
                                 0,   -1, 0};
         normals   = new float[]{0,0,1, 0,0,1, 0,0,1, 0,0,1, 0,0,1, 
                                 0,0,1, 0,0,1, 0,0,1, 0,0,1, 0,0,1};
         pntsIndex = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
         clrsIndex = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
      }

      public String getName()
      {
         return "raindrop";
      }

      public int getType()
      {
         return TRIANGLE_STRIPS;
      }

      public boolean isRefinable()
      {
         return false;
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

         float[] p0 = new float[] {0,0,1};
         float[] p1 = new float[] {1,0,0};
         float[] p2 = new float[] {0,1,0};

         float[] q0 = new float[] {0,1,0};
         float[] q1 = new float[] {0,0,1};
         float[] q2 = new float[] {-1,0,0};
         verts     = new float[12*lod*lod];
         normals   = new float[12*lod*lod];
         pntsIndex = new int[8*(lod-1)*lod];
         clrsIndex = new int[8*(lod-1)*lod];

         for (i=0;i<4*(lod-1);i++)
            strips[i]=2*lod;

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

