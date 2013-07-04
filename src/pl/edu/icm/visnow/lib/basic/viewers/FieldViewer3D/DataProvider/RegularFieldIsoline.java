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

package pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.DataProvider;
import java.util.ArrayList;
/**
 *
 * @author  Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public class RegularFieldIsoline
{
   private int[] dims;
   private float[] points;
   private float[] fData;
   private float threshold;
   
   private int nspace;
   private int npoint, midpoint;
   public ArrayList<float[][]> lines = new ArrayList<float[][]>();
   private float[][] t_coords;
   private boolean[][][] done;
   private int width;
   private int height;
   
   private float getthreshold(int i)
   {
      return fData[i];
   }
   /** Creates new Isoline */
   public RegularFieldIsoline(float val, int[] dims, float[] fData)
   {
      //System.out.println("New isoline...");
      
      width = dims[0];
      height= dims[1];
      this.fData = fData;
      threshold = val;
      float u,v0,v1,v2;
      int endln;
      int i,ii,j;
      i=ii=j=0;
      done = new boolean[height][width][2];
      for (int iy=0; iy<done.length; iy++)
         for (int ix=0; ix<done[0].length; ix++)
            done[iy][ix][0]=done[iy][ix][1]=false;
      
      t_coords = new float[width*height][2];
      
      for (int iy=0; iy<height-1; iy++)
      {
         for (int ix=0; ix<width-1; ix++)
         {
            if (done[iy][ix][0])
               continue;
            done[iy][ix][0]=true;
            v0 = getthreshold(iy*width+ix)-threshold;
            v1 = getthreshold(iy*width+ix+1)-threshold;
            v2 = getthreshold((iy+1)*width+ix)-threshold;
            if (v0==0.)  v0=1.e-10f;
            if (v1==0.)  v1=1.e-10f;
            if (v2==0.)  v2=1.e-10f;
            npoint = midpoint = 0;
            
            if (v0*v1<0)
            {
               u = 1.f/(v1-v0);
               t_coords[npoint][0] = ix-u*v0;
               t_coords[npoint][1] = iy;
               npoint += 1;
               Triangle tstart = new Triangle(ix,iy,0,2);
               endln=0;
               while (endln==0)
                  endln=tstart.traverse();
               midpoint=npoint;
               if (endln==2 && iy>0)
               {
                  tstart = new Triangle(ix,iy-1,1,2);
                  while (tstart.traverse()==0) ;
               }
            }
            
            else if (v0*v2<0)
            {
               u = 1.f/(v2-v0);
               t_coords[npoint][0] = ix;
               t_coords[npoint][1] = iy-u*v0;
               npoint += 1;
               Triangle tstart = new Triangle(ix,iy,0,1);
               endln=0;
               while (endln==0) 
                  endln=tstart.traverse();
               midpoint=npoint;
               if (endln==2 && ix>0)
               {
                  tstart = new Triangle(ix-1,iy,1,1);
                  while (tstart.traverse()==0) ;
               }
            }
            if (npoint>2)
               try
               {
                  float[][] iCoords=new float[npoint][2];
                  for(ii=midpoint-1,i=0; ii>=0; ii--,i++)
                     for (j=0;j<2;j++)
                        iCoords[i][j]=t_coords[ii][j];
                  for (ii=midpoint; ii<npoint; ii++,i++)
                     for (j=0;j<2;j++)
                        iCoords[i][j]=t_coords[ii][j];
                  lines.add(iCoords);
               }
               catch(Exception e)
               {
                  System.out.println("i="+i+" ii="+ii+" j="+j);
               }
         }
      }
      //now remove all unnecessary links:
      fData    = null;
      done     = null;
      t_coords = null;
   }
   
   public ArrayList<float[][]> getLines()
   {
      return(lines);
   }
   
   
   
   // TRIANGLE *****************************************
   private class Triangle
   {
      private float[] d;
      private int ix, iy;
      private int ixmax, iymax;
      private int is_up;
      private float[][] coords;
      private float[] vvals;
      private int edge_in;
      
      public Triangle(int inx, int iny, int  inIs_up, int inEdge_in)
      {
         
         if(inx < 0 || inx >= width || iny < 0 || iny >=height)
         {
            ix = iy =-999;
            throw new IllegalArgumentException("Triangle out of range.");
         }
         
         ix = inx;
         iy = iny;
         is_up = inIs_up;
         coords = new float[3][2];
         vvals   = new float[3];
         loadThresholds();
         edge_in=inEdge_in;
      }
      
      private void loadThresholds()
      {
         if(is_up==1)
         {
            coords[0][0]=ix+1;
            coords[0][1]=iy+1;
            vvals[0]=fData[(iy+1)*width+ix+1]-threshold;
            coords[1][0]=ix;
            coords[1][1]=iy+1;
            vvals[1]=fData[(iy+1)*width+ix]-threshold;
            coords[2][0]=ix+1;
            coords[2][1]=iy;
            vvals[2]=fData[iy*width+ix+1]-threshold;
         }
         else
         {
            coords[0][0]=ix;
            coords[0][1]=iy;
            vvals[0]=fData[iy*width+ix]-threshold;
            coords[1][0]=ix+1;
            coords[1][1]=iy;
            vvals[1]=fData[iy*width+ix+1]-threshold;
            coords[2][0]=ix;
            coords[2][1]=iy+1;
            vvals[2]=fData[(iy+1)*width+ix]-threshold;
         }
         for (int i=0; i<3;i++)
            if (vvals[i]==0.) vvals[i]=1.e-10f;
      }
      
      public int traverse()
      {
         int edge_out  = -1;
         int is_up_out = 1-is_up;
         int dirx,diry;
         done[iy][ix][is_up]=true;
         float u=.5f;
         dirx=diry=0;
         switch (edge_in)
         {
            case 0:
               if (vvals[0]*vvals[1]>=0.)
                  edge_out = 1;
               else
                  edge_out = 2;
               break;
            case 1:
               if (vvals[1]*vvals[2]>=0.)
                  edge_out = 2;
               else
                  edge_out = 0;
               break;
            case 2:
               if (vvals[2]*vvals[0]>=0.)
                  edge_out = 0;
               else
                  edge_out = 1;
               break;
         }
         switch (edge_out)
         {
            case 0:
               break;
            case 1:
               u = 1.f/(vvals[2]-vvals[0]);
               for (int i=0; i<2; i++)
                  t_coords[npoint][i] = u*(vvals[2]*coords[0][i]-vvals[0]*coords[2][i]);
               npoint ++;
               if (is_up==1)
                  dirx=1;
               else
                  dirx=-1;
               if (ix+dirx<0||ix+dirx>=width-1)
                  return 2;
               if (done[iy][ix+dirx][is_up_out])
                  return(1);
               break;
            case 2:
               u = 1.f/(vvals[0]-vvals[1]);
               for (int i=0; i<2; i++)
                  t_coords[npoint][i] =  u*(vvals[0]*coords[1][i]-vvals[1]*coords[0][i]);
               npoint++;
               if (is_up==1)
                  diry=1;
               else
                  diry=-1;
               if (iy+diry<0||iy+diry>=height-1)
                  return 2;
               if (done[iy+diry][ix][is_up_out])
                  return(1);
               break;
         }
         ix+=dirx;
         iy+=diry;
         is_up=is_up_out;
         edge_in = edge_out;
         loadThresholds();
         return 0;
      }
   }
}
