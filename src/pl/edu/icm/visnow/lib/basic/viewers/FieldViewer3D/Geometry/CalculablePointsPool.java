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

/**
 * @author  Bartosz Borucki (babor@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */

public class CalculablePointsPool {

    public static enum CalculablePointType {

        CENTER_2_POINTS {
            @Override
            public String toString() {
                return "Central point between 2 points";
            }

            public String getPreName() {
                return "hwp_";
            }
        },

        CENTER_2_SEGMENTS {
            @Override
            public String toString() {
                return "Nearest 'crossing' of 2 segments";
            }

            public String getPreName() {
                return "xing_";
            }
        },

        PLANE_CAST {
            @Override
            public String toString() {
                return "Point to plane cast";
            }

            public String getPreName() {
                return "pcast_";
            }
        },

        WEIGHTED_CENTER {
            @Override
            public String toString() {
                return "Weighted central point";
            }

            public String getPreName() {
                return "wght_";
            }
        },
        
        AXIS_CAST {
            @Override
            public String toString() {
                return "Point to axis cast";
            }

            public String getPreName() {
                return "acast_";
            }
        };
        


        public abstract String getPreName();
    }

    public static CalculablePoint getCalculablePoint(CalculablePointType type, String name) {
        switch(type) {
            case CENTER_2_POINTS:
                return getCenter2PointsCalculablePoint(name);
            case CENTER_2_SEGMENTS:
                return getXing2SegmentsCalculablePoint(name);
            case PLANE_CAST:
                return getPlaneCastCalculablePoint(name);
            case WEIGHTED_CENTER:
                return getWghtCalculablePoint(name);
            case AXIS_CAST:
                return getAxisCastCalculablePoint(name);
            default:
                return null;
        }
    }

    
    public static CalculablePoint getCenter2PointsCalculablePoint(String name) {
        return new CalculablePoint(CalculablePointType.CENTER_2_POINTS.getPreName()+name) {
            @Override
            public float[] getValue() {
                if(this.pointDescriptors.size() != 2)
                    return null;

                float[] v1, v2;
                float[] out = new float[3];

                v1 = pointDescriptors.get(0).getWorldCoords();
                v2 = pointDescriptors.get(1).getWorldCoords();
                for (int i = 0; i < 3; i++) {
                    out[i] = (v1[i]+v2[i])/2.0f;
                }
                return out;
            }

            @Override
            public boolean isPointDescriptorsReady() {
                return (pointDescriptors.size() == 2);
            }

            @Override
            public CalculablePointType getType() {
                return CalculablePointType.CENTER_2_POINTS;
            }
        };
    }

    public static CalculablePoint getPlaneCastCalculablePoint(String name) {
        return new CalculablePoint(CalculablePointType.PLANE_CAST.getPreName()+name) {
            @Override
            public float[] getValue() {
                if(this.pointDescriptors.size() != 4)
                    return null;

                double[] base_n = new double[3];
                double[] base_p = new double[3];
                float[] p,p0,p1,p2;
                double[] v21 = new double[3], v20 = new double[3];
                p = pointDescriptors.get(0).getWorldCoords();
                p0 = pointDescriptors.get(1).getWorldCoords();
                p1 = pointDescriptors.get(2).getWorldCoords();
                p2 = pointDescriptors.get(3).getWorldCoords();
                for (int i = 0; i < 3; i++) {
                    base_p[i] = (double)p2[i];
                    v20[i] = (double)p0[i]-(double)p2[i];
                    v21[i] = (double)p1[i]-(double)p2[i];
                }
                base_n[0] = v21[1]*v20[2] - v21[2]*v20[1];
                base_n[1] = v21[2]*v20[0] - v21[0]*v20[2];
                base_n[2] = v21[0]*v20[1] - v21[1]*v20[0];
                double norm = 0;
                for (int i = 0; i < 3; i++) {
                    norm += base_n[i]*base_n[i];
                }
                norm = Math.sqrt(norm);
                for (int i = 0; i < 3; i++) {
                    base_n[i] = base_n[i]/norm;
                }

                return castPointToPlane(p, base_n, base_p);
            }

            private float[] castPointToPlane(float[] p, double[] n, double[] p0) {
                float[] out = new float[3];
                double t = 0, nn = 0;
                for (int i = 0; i < 3; i++) {
                    nn += n[i]*n[i];
                    t += n[i]*(p0[i]-(double)p[i]);
                }

                for (int i = 0; i < 3; i++) {
                    out[i] = (float)((double)p[i] + n[i]*t/nn);
                }
                return out;
            }

            @Override
            public boolean isPointDescriptorsReady() {
                return (pointDescriptors.size() == 4);
            }

            @Override
            public CalculablePointType getType() {
                return CalculablePointType.PLANE_CAST;
            }

        };
    }

    public static CalculablePoint getXing2SegmentsCalculablePoint(String name) {
        return new CalculablePoint(CalculablePointType.CENTER_2_SEGMENTS.getPreName()+name) {
            @Override
            public float[] getValue() {
                if(this.pointDescriptors.size() != 4)
                    return null;

                float[] a0,a1,b0,b1;
                a0 = pointDescriptors.get(0).getWorldCoords();
                a1 = pointDescriptors.get(1).getWorldCoords();
                b0 = pointDescriptors.get(2).getWorldCoords();
                b1 = pointDescriptors.get(3).getWorldCoords();

                float[] out = new float[3];
                double[] va = new double[3];
                double[] vb = new double[3];
                double[][] A = new double[2][2];
                double[] b = new double[2];
                double detA;
                double u, t;

                for (int i = 0; i < 3; i++) {
                    va[i] = (double)a1[i]-(double)a0[i];
                    vb[i] = (double)b1[i]-(double)b0[i];
                }

                A[0][0] = vb[0]*va[0] + vb[1]*va[1] + vb[2]*va[2];
                A[0][1] = -(va[0]*va[0] + va[1]*va[1] + va[2]*va[2]);
                A[1][0] = vb[0]*vb[0] + vb[1]*vb[1] + vb[2]*vb[2];
                A[1][1] = -(va[0]*vb[0] + va[1]*vb[1] + va[2]*vb[2]);
                b[0] = ((double)a0[0]-(double)b0[0])*va[0] + ((double)a0[1]-(double)b0[1])*va[1] + ((double)a0[2]-(double)b0[2])*va[2];
                b[1] = ((double)a0[0]-(double)b0[0])*vb[0] + ((double)a0[1]-(double)b0[1])*vb[1] + ((double)a0[2]-(double)b0[2])*vb[2];
                detA = A[0][0]*A[1][1] - A[0][1]*A[1][0];
                if(detA == 0.0)
                    return null;
                u = (b[0]*A[1][1] - A[0][1]*b[1])/detA;
                t = (A[0][0]*b[1] - b[0]*A[1][0])/detA;

                for (int i = 0; i < 3; i++) {
                    out[i] = (float)(((double)a0[i] + t*va[i] +(double) b0[i] + u*vb[i])/2.0);
                }

                //---check-----
//                double[] pa = new double[3];
//                double[] pb = new double[3];
//                double[] vab = new double[3];
//                double vavab = 0, vbvab = 0, normva = 0, normvb = 0, normvab = 0;
//                double dpa = 0, dpb = 0;
//                for (int i = 0; i < 3; i++) {
//                    pa[i] = (double)a0[i] + t*va[i];
//                    pb[i] = (double)b0[i] + u*vb[i];
//                    vab[i] = pb[i]-pa[i];
//                    vavab += va[i]*vab[i];
//                    vbvab += vb[i]*vab[i];
//                    normva += va[i]*va[i];
//                    normvb += vb[i]*vb[i];
//                    normvab += vab[i]*vab[i];
//                    dpa += (pa[i]-(double)out[i])*(pa[i]-(double)out[i]);
//                    dpb += (pb[i]-(double)out[i])*(pb[i]-(double)out[i]);
//                }
//                normva = (float)Math.sqrt(normva);
//                normvb = (float)Math.sqrt(normvb);
//                normvab = (float)Math.sqrt(normvab);
//                vavab = vavab/(normva*normvab);
//                vbvab = vbvab/(normvb*normvab);
//                dpa = (float)Math.sqrt(dpa);
//                dpb = (float)Math.sqrt(dpb);
//
//                System.out.println("pa: ["+pa[0]+","+pa[1]+","+pa[2]+"]");
//                System.out.println("pb: ["+pb[0]+","+pb[1]+","+pb[2]+"]");
//                System.out.println("va*vab = "+vavab);
//                System.out.println("vb*vab = "+vbvab);
//                System.out.println("dpa = "+dpa);
//                System.out.println("dpb = "+dpb);
                //------------

                return out;
            }

            private float dot(float[]a, float[] b) {
                return (a[0]*b[0] + a[1]*b[1] + a[2]*b[2]);
            }

            @Override
            public boolean isPointDescriptorsReady() {
                return (pointDescriptors.size() == 4);
            }

            @Override
            public CalculablePointType getType() {
                return CalculablePointType.CENTER_2_SEGMENTS;
            }

        };
    }

    public static CalculablePoint getWghtCalculablePoint(String name) {
        return new CalculablePoint(CalculablePointType.WEIGHTED_CENTER.getPreName()+name) {
            @Override
            public float[] getValue() {
                if(this.pointDescriptors.size() < 2)
                    return null;

                float[] out = {0.0f, 0.0f, 0.0f};
                float[] tmp;
                int N = this.pointDescriptors.size();
                for (int i = 0; i < N; i++) {
                    tmp = this.pointDescriptors.get(i).getWorldCoords();
                    for (int j = 0; j < 3; j++) {
                        out[j] += tmp[j];
                    }
                }
                for (int j = 0; j < 3; j++) {
                    out[j] /= (float)N;
                }
                return out;
            }

            @Override
            public boolean isPointDescriptorsReady() {
                return (pointDescriptors.size() >= 2);
            }

            @Override
            public CalculablePointType getType() {
                return CalculablePointType.WEIGHTED_CENTER;
            }

        };
    }

    public static CalculablePoint getAxisCastCalculablePoint(String name) {
        return new CalculablePoint(CalculablePointType.AXIS_CAST.getPreName()+name) {
            @Override
            public float[] getValue() {
                if(this.pointDescriptors.size() != 3)
                    return null;
                
                float[] p = pointDescriptors.get(0).getWorldCoords();
                float[] ax0 = pointDescriptors.get(1).getWorldCoords();
                float[] ax1 = pointDescriptors.get(2).getWorldCoords();
                
                if(p == null || ax0 == null || ax1 == null)
                    return null;
                
                float[] v = new float[3];
                float[] v1 = new float[3];
                for (int i = 0; i < 3; i++) {
                    v[i] = ax1[i]-ax0[i];                    
                    v1[i] = p[i]-ax0[i];                    
                }
                
                double t = ((double)(v[0]*v1[0] + v[1]*v1[1] + v[2]*v1[2]))/((double)(v[0]*v[0] + v[1]*v[1] + v[2]*v[2]));
                
                float[] out = new float[3];
                for (int i = 0; i < 3; i++) {
                    out[i] = (float)(ax0[i] + t*v[i]);                    
                }
                
                return out;
            }

            @Override
            public boolean isPointDescriptorsReady() {
                return (pointDescriptors.size() == 3);
            }

            @Override
            public CalculablePointType getType() {
                return CalculablePointType.AXIS_CAST;
            }

        };
    }
    

    public static CalculablePointType getTypeByString(String str) {
        if(CalculablePointType.CENTER_2_POINTS.toString().equals(str))
            return CalculablePointType.CENTER_2_POINTS;
        if(CalculablePointType.CENTER_2_SEGMENTS.toString().equals(str))
            return CalculablePointType.CENTER_2_SEGMENTS;
        if(CalculablePointType.PLANE_CAST.toString().equals(str))
            return CalculablePointType.PLANE_CAST;
        if(CalculablePointType.WEIGHTED_CENTER.toString().equals(str))
            return CalculablePointType.WEIGHTED_CENTER;
        if(CalculablePointType.AXIS_CAST.toString().equals(str))
            return CalculablePointType.AXIS_CAST;

        return null;
    }

   private CalculablePointsPool()
   {
   }

}
