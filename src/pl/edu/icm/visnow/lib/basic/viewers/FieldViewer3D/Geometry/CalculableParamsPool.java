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

public class CalculableParamsPool {

    public static enum CalculableType {

        ANGLE_FROM_3_POINTS {
            @Override
            public String toString() {
                return "Angle from 3 points";
            }

            public String getPreName() {
                return "angle_";
            }
        },

        ANGLE_FROM_2_LINES {
            @Override
            public String toString() {
                return "Angle from 2 lines (4 points)";
            }

            public String getPreName() {
                return "angle_";
            }
        },

        SIGNED_ANGLE_FROM_2_LINES {
            @Override
            public String toString() {
                return "Signed angle from 2 lines (4 points) and reference plane (3 points)";
            }

            public String getPreName() {
                return "angle_";
            }
        },

        MODIFIABLE_SIGNED_ANGLE_FROM_2_LINES {
            @Override
            public String toString() {
                return "Modifiable signed angle from 2 lines (4 points) and reference plane (3 points)";
            }

            public String getPreName() {
                return "angle_";
            }
        },

        LENGTH_FROM_POLYLINE {
            @Override
            public String toString() {
                return "Length of a polyline";
            }

            public String getPreName() {
                return "length_";
            }
        },

        DISTANCE {
            @Override
            public String toString() {
                return "Distance";
            }

            public String getPreName() {
                return "dist_";
            }
        },
        
        SIGNED_DISTANCE {
            @Override
            public String toString() {
                return "Signed distance";
            }

            public String getPreName() {
                return "sdist_";
            }
        },
        
        NORMALIZED_SIGNED_DISTANCE {
            @Override
            public String toString() {
                return "Normalized signed distance";
            }

            public String getPreName() {
                return "nsdist_";
            }
        };
        

        public abstract String getPreName();
    }

    public static CalculableParameter getCalculable(CalculableType type, String name) {
        return getCalculable(type, name, null);
    }

    public static CalculableParameter getCalculable(CalculableType type, String name, float[] parameters) {
        if(type == null)
            return null;
        
        switch(type) {
            case ANGLE_FROM_3_POINTS:
                return getAngleFrom3PointsCalculable(name, parameters);
            case ANGLE_FROM_2_LINES:
                return getAngleFrom2LinesCalculable(name, parameters);
            case SIGNED_ANGLE_FROM_2_LINES:
                return getSignedAngleFrom2LinesAndReferencePlaneCalculable(name);
            case MODIFIABLE_SIGNED_ANGLE_FROM_2_LINES:
                return getModifiableSignedAngleFrom2LinesAndReferencePlaneCalculable(name, parameters);
            case LENGTH_FROM_POLYLINE:
                return getLengthFromPolylinePointsCalculable(name);
            case NORMALIZED_SIGNED_DISTANCE:
                return getNormalizedSignedDistanceCalculable(name);
            case SIGNED_DISTANCE:
                return getSignedDistanceCalculable(name);
            case DISTANCE:
                return getDistanceCalculable(name);
            default:
                return null;
        }
    }

    public static CalculableParameter getAngleFrom3PointsCalculable(String name) {
        return getAngleFrom3PointsCalculable(name, null);
    }
    
    public static CalculableParameter getAngleFrom3PointsCalculable(String name, float[] parameters) {
        return new CalculableParameter(CalculableType.ANGLE_FROM_3_POINTS.getPreName()+name, parameters) {
            @Override
            public float getValue() {
                if(this.pointDescriptors.size() != 3)
                    return 0;

                double[] v1 = new double[3];
                double[] v2 = new double[3];
                double len;
                double angle;

                v1[0] = (double)(pointDescriptors.get(0).getWorldCoords()[0] - pointDescriptors.get(1).getWorldCoords()[0]);
                v1[1] = (double)(pointDescriptors.get(0).getWorldCoords()[1] - pointDescriptors.get(1).getWorldCoords()[1]);
                v1[2] = (double)(pointDescriptors.get(0).getWorldCoords()[2] - pointDescriptors.get(1).getWorldCoords()[2]);
                
                v2[0] = (double)(pointDescriptors.get(2).getWorldCoords()[0] - pointDescriptors.get(1).getWorldCoords()[0]);
                v2[1] = (double)(pointDescriptors.get(2).getWorldCoords()[1] - pointDescriptors.get(1).getWorldCoords()[1]);
                v2[2] = (double)(pointDescriptors.get(2).getWorldCoords()[2] - pointDescriptors.get(1).getWorldCoords()[2]);

                len = Math.sqrt(v1[0]*v1[0] + v1[1]*v1[1] + v1[2]*v1[2])*Math.sqrt(v2[0]*v2[0] + v2[1]*v2[1] + v2[2]*v2[2]);
                if(len == 0) {
                    angle = 0;
                } else {
                    angle = 180.0 *Math.acos((v1[0]*v2[0] + v1[1]*v2[1] + v1[2]*v2[2])/len)/Math.PI;
                }

                if(angle > 360 || angle < -360) {
                    angle = angle % 360;
                }

                float[] p = this.getParameters();
                if(p != null && p.length == 2) {
                    angle =  p[0] * angle + p[1];
                }

                return (float)angle;
            }

            @Override
            public int getLocationPointIndex() {
                return 1;
            }

            @Override
            public boolean isPointDescriptorsReady() {
                return (pointDescriptors.size() == 3);
            }

            @Override
            public CalculableType getType() {
                return CalculableType.ANGLE_FROM_3_POINTS;
            }

        };
    }

    public static CalculableParameter getAngleFrom2LinesCalculable(String name) {
        return getAngleFrom2LinesCalculable(name, null);
    }

    public static CalculableParameter getAngleFrom2LinesCalculable(String name, float[] parameters) {
        return new CalculableParameter(CalculableType.ANGLE_FROM_2_LINES.getPreName()+name, parameters) {
            @Override
            public float getValue() {
                if(this.pointDescriptors.size() != 4)
                    return 0;

                double[] v01 = new double[3];
                double[] v23 = new double[3];
                double len;
                double angle;

                v01[0] = (double)(pointDescriptors.get(1).getWorldCoords()[0] - pointDescriptors.get(0).getWorldCoords()[0]);
                v01[1] = (double)(pointDescriptors.get(1).getWorldCoords()[1] - pointDescriptors.get(0).getWorldCoords()[1]);
                v01[2] = (double)(pointDescriptors.get(1).getWorldCoords()[2] - pointDescriptors.get(0).getWorldCoords()[2]);

                v23[0] = (double)(pointDescriptors.get(3).getWorldCoords()[0] - pointDescriptors.get(2).getWorldCoords()[0]);
                v23[1] = (double)(pointDescriptors.get(3).getWorldCoords()[1] - pointDescriptors.get(2).getWorldCoords()[1]);
                v23[2] = (double)(pointDescriptors.get(3).getWorldCoords()[2] - pointDescriptors.get(2).getWorldCoords()[2]);

                len = Math.sqrt(v01[0]*v01[0] + v01[1]*v01[1] + v01[2]*v01[2])*Math.sqrt(v23[0]*v23[0] + v23[1]*v23[1] + v23[2]*v23[2]);
                if(len == 0) {
                    angle = 0;
                } else {
                    angle = 180.0 *Math.acos((v01[0]*v23[0] + v01[1]*v23[1] + v01[2]*v23[2])/len)/Math.PI;
                }

                if(angle > 360 || angle < -360) {
                    angle = angle % 360;
                }

                float[] p = this.getParameters();
                if(p != null && p.length == 2) {
                    angle =  p[0] * angle + p[1];
                }

                return (float)angle;
            }

            @Override
            public int getLocationPointIndex() {
                return 2;
            }

            @Override
            public boolean isPointDescriptorsReady() {
                return (pointDescriptors.size() == 4);
            }

            @Override
            public CalculableType getType() {
                return CalculableType.ANGLE_FROM_2_LINES;
            }

        };
    }

    public static CalculableParameter getSignedAngleFrom2LinesAndReferencePlaneCalculable(String name) {
        return getSignedAngleFrom2LinesAndReferencePlaneCalculable(name, null);
    }

    public static CalculableParameter getSignedAngleFrom2LinesAndReferencePlaneCalculable(String name, float[] parameters) {
        return new CalculableParameter(CalculableType.SIGNED_ANGLE_FROM_2_LINES.getPreName()+name, parameters) {
            @Override
            public float getValue() {
                if(this.pointDescriptors.size() != 7)
                    return 0;

                double[] v01 = new double[3];
                double[] v23 = new double[3];
                double[] n = new double[3];
                double[] tmp0 = new double[3];
                double angle, tmp1, tmp2;
                float[] p0,p1,p2;
                double[] n21 = new double[3], n20 = new double[3];

                v01[0] = (double)(pointDescriptors.get(1).getWorldCoords()[0] - pointDescriptors.get(0).getWorldCoords()[0]);
                v01[1] = (double)(pointDescriptors.get(1).getWorldCoords()[1] - pointDescriptors.get(0).getWorldCoords()[1]);
                v01[2] = (double)(pointDescriptors.get(1).getWorldCoords()[2] - pointDescriptors.get(0).getWorldCoords()[2]);

                v23[0] = (double)(pointDescriptors.get(3).getWorldCoords()[0] - pointDescriptors.get(2).getWorldCoords()[0]);
                v23[1] = (double)(pointDescriptors.get(3).getWorldCoords()[1] - pointDescriptors.get(2).getWorldCoords()[1]);
                v23[2] = (double)(pointDescriptors.get(3).getWorldCoords()[2] - pointDescriptors.get(2).getWorldCoords()[2]);

                p0 = pointDescriptors.get(4).getWorldCoords();
                p1 = pointDescriptors.get(5).getWorldCoords();
                p2 = pointDescriptors.get(6).getWorldCoords();
                for (int i = 0; i < 3; i++) {
                    n20[i] = (double)p0[i]-(double)p2[i];
                    n21[i] = (double)p1[i]-(double)p2[i];
                }
                n[0] = n20[1]*n21[2] - n20[2]*n21[1];
                n[1] = n20[2]*n21[0] - n20[0]*n21[2];
                n[2] = n20[0]*n21[1] - n20[1]*n21[0];


                double v01_norm = 0, v23_norm = 0, n_norm = 0;
                for (int i = 0; i < 3; i++) {
                    v01_norm += v01[i]*v01[i];
                    v23_norm += v23[i]*v23[i];
                    n_norm += n[i]*n[i];
                }
                v01_norm = Math.sqrt(v01_norm);
                v23_norm = Math.sqrt(v23_norm);
                n_norm = Math.sqrt(n_norm);
                if(n_norm == 0 || v01_norm == 0 || v23_norm == 0) {
                    return 0;
                }


                for (int i = 0; i < 3; i++) {
                    v01[i] = v01[i]/v01_norm;
                    v23[i] = v23[i]/v23_norm;
                    n[i] = n[i]/n_norm;
                }

                //signed_angle = atan2(  N * ( V1 x V2 ), V1 * V2  );
                // where * is dot product and x is cross product
                // N is the normal to the polygon
                // ALL vectors: N, V1, V2 must be normalized

                //tmp0 = v01 x v23
                tmp0[0] = v01[1]*v23[2] - v01[2]*v23[1];
                tmp0[1] = v01[2]*v23[0] - v01[0]*v23[2];
                tmp0[2] = v01[0]*v23[1] - v01[1]*v23[0];
                //tmp1 = n * tmp0
                tmp1 = n[0]*tmp0[0] + n[1]*tmp0[1] + n[2]*tmp0[2];
                //tmp2 = v01 * v23
                tmp2 = v01[0]*v23[0] + v01[1]*v23[1] + v01[2]*v23[2];
                angle = 180.0*Math.atan2(tmp1, tmp2)/Math.PI;

                if(angle > 360 || angle < -360) {
                    angle = angle % 360;
                }

                float[] p = this.getParameters();
                if(p != null && p.length == 2) {
                    angle =  p[0] * angle + p[1];
                }

                return (float)angle;
            }

            @Override
            public int getLocationPointIndex() {
                return 2;
            }

            @Override
            public boolean isPointDescriptorsReady() {
                return (pointDescriptors.size() == 7);
            }

            @Override
            public CalculableType getType() {
                return CalculableType.SIGNED_ANGLE_FROM_2_LINES;
            }

        };
    }

    public static CalculableParameter getModifiableSignedAngleFrom2LinesAndReferencePlaneCalculable(String name, float[] parameters) {
        return new CalculableParameter(CalculableType.MODIFIABLE_SIGNED_ANGLE_FROM_2_LINES.getPreName()+name, parameters) {
            @Override
            public float getValue() {
                if(this.pointDescriptors.size() != 7)
                    return 0;

                double[] v01 = new double[3];
                double[] v23 = new double[3];
                double[] n = new double[3];
                double[] tmp0 = new double[3];
                double angle, tmp1, tmp2;
                float[] p0,p1,p2;
                double[] n21 = new double[3], n20 = new double[3];

                v01[0] = (double)(pointDescriptors.get(1).getWorldCoords()[0] - pointDescriptors.get(0).getWorldCoords()[0]);
                v01[1] = (double)(pointDescriptors.get(1).getWorldCoords()[1] - pointDescriptors.get(0).getWorldCoords()[1]);
                v01[2] = (double)(pointDescriptors.get(1).getWorldCoords()[2] - pointDescriptors.get(0).getWorldCoords()[2]);

                v23[0] = (double)(pointDescriptors.get(3).getWorldCoords()[0] - pointDescriptors.get(2).getWorldCoords()[0]);
                v23[1] = (double)(pointDescriptors.get(3).getWorldCoords()[1] - pointDescriptors.get(2).getWorldCoords()[1]);
                v23[2] = (double)(pointDescriptors.get(3).getWorldCoords()[2] - pointDescriptors.get(2).getWorldCoords()[2]);

                p0 = pointDescriptors.get(4).getWorldCoords();
                p1 = pointDescriptors.get(5).getWorldCoords();
                p2 = pointDescriptors.get(6).getWorldCoords();
                for (int i = 0; i < 3; i++) {
                    n20[i] = (double)p0[i]-(double)p2[i];
                    n21[i] = (double)p1[i]-(double)p2[i];
                }
                n[0] = n20[1]*n21[2] - n20[2]*n21[1];
                n[1] = n20[2]*n21[0] - n20[0]*n21[2];
                n[2] = n20[0]*n21[1] - n20[1]*n21[0];


                double v01_norm = 0, v23_norm = 0, n_norm = 0;
                for (int i = 0; i < 3; i++) {
                    v01_norm += v01[i]*v01[i];
                    v23_norm += v23[i]*v23[i];
                    n_norm += n[i]*n[i];
                }
                v01_norm = Math.sqrt(v01_norm);
                v23_norm = Math.sqrt(v23_norm);
                n_norm = Math.sqrt(n_norm);
                if(n_norm == 0 || v01_norm == 0 || v23_norm == 0) {
                    return 0;
                }


                for (int i = 0; i < 3; i++) {
                    v01[i] = v01[i]/v01_norm;
                    v23[i] = v23[i]/v23_norm;
                    n[i] = n[i]/n_norm;
                }

                //signed_angle = atan2(  N * ( V1 x V2 ), V1 * V2  );
                // where * is dot product and x is cross product
                // N is the normal to the polygon
                // ALL vectors: N, V1, V2 must be normalized

                //tmp0 = v01 x v23
                tmp0[0] = v01[1]*v23[2] - v01[2]*v23[1];
                tmp0[1] = v01[2]*v23[0] - v01[0]*v23[2];
                tmp0[2] = v01[0]*v23[1] - v01[1]*v23[0];
                //tmp1 = n * tmp0
                tmp1 = n[0]*tmp0[0] + n[1]*tmp0[1] + n[2]*tmp0[2];
                //tmp2 = v01 * v23
                tmp2 = v01[0]*v23[0] + v01[1]*v23[1] + v01[2]*v23[2];
                angle = 180.0*Math.atan2(tmp1, tmp2)/Math.PI;

                if(angle > 360 || angle < -360) {
                    angle = angle % 360;
                }

                float[] p = this.getParameters();
                if(p != null && p.length == 2) {
                    angle =  p[0] * angle + p[1];
                }

                return (float)angle;
                
            }

            @Override
            public int getLocationPointIndex() {
                return 2;
            }

            @Override
            public boolean isPointDescriptorsReady() {
                return (pointDescriptors.size() == 7);
            }

            @Override
            public CalculableType getType() {
                return CalculableType.MODIFIABLE_SIGNED_ANGLE_FROM_2_LINES;
            }

        };
    }

    public static CalculableParameter getLengthFromPolylinePointsCalculable(String name) {
        return new CalculableParameter(CalculableType.LENGTH_FROM_POLYLINE.getPreName()+name) {
            @Override
            public float getValue() {
                if(this.pointDescriptors.size() < 2)
                    return 0;

                double len = 0;
                double tmp, tmp0;
                for (int i = 1; i < pointDescriptors.size(); i++) {
                    tmp = 0;
                    for (int j = 0; j < 3; j++) {
                        tmp0 = (pointDescriptors.get(i).getWorldCoords()[j] - pointDescriptors.get(i-1).getWorldCoords()[j]);
                        tmp += tmp0*tmp0;
                    }
                    len += Math.sqrt(tmp);
                }
                return (float)len;
            }

            @Override
            public int getLocationPointIndex() {
                return (pointDescriptors.size()-1);
            }

            @Override
            public boolean isPointDescriptorsReady() {
                return (pointDescriptors.size() >= 2);
            }

            @Override
            public CalculableType getType() {
                return CalculableType.LENGTH_FROM_POLYLINE;
            }
        };
    }
    
    public static CalculableParameter getNormalizedSignedDistanceCalculable(String name) {
        return new CalculableParameter(CalculableType.NORMALIZED_SIGNED_DISTANCE.getPreName()+name) {
            @Override
            public float getValue() {
                if(this.pointDescriptors.size() != 5)
                    return 0;
                
                //distance between p0 and p1, signed with vector directed from p0 to p2 and normalized by distance from n0 to n1
                
                float[] p0 = pointDescriptors.get(0).getWorldCoords(); 
                float[] p1 = pointDescriptors.get(1).getWorldCoords();                
                double d = 0;
                for (int i = 0; i < 3; i++) {
                    d += (p1[i]-p0[i])*(p1[i]-p0[i]);
                }
                d = Math.sqrt(d);
                
                float[] p2 = pointDescriptors.get(2).getWorldCoords();
                float sign = 0;
                for (int i = 0; i < 3; i++) {
                    sign += (p2[i]-p0[i])*(p1[i]-p0[i]);
                }
                if(sign >= 0)
                    sign = 1;
                else
                    sign = -1;
                
                float[] n0 = pointDescriptors.get(3).getWorldCoords();
                float[] n1 = pointDescriptors.get(4).getWorldCoords();
                double norm = 0;
                for (int i = 0; i < n1.length; i++) {
                    norm += (n1[i]-n0[i])*(n1[i]-n0[i]);                    
                }
                norm = Math.sqrt(norm);
                
                return (float) (sign*d/norm);              
            }

            @Override
            public int getLocationPointIndex() {
                return 1;
            }

            @Override
            public boolean isPointDescriptorsReady() {
                return (pointDescriptors.size() == 5);
            }

            @Override
            public CalculableType getType() {
                return CalculableType.NORMALIZED_SIGNED_DISTANCE;
            }

        };
    }

    public static CalculableParameter getSignedDistanceCalculable(String name) {
        return new CalculableParameter(CalculableType.SIGNED_DISTANCE.getPreName()+name) {
            @Override
            public float getValue() {
                if(this.pointDescriptors.size() != 3)
                    return 0;
                
                //distance between p0 and p1, signed with vector directed from p0 to p2
                
                float[] p0 = pointDescriptors.get(0).getWorldCoords(); 
                float[] p1 = pointDescriptors.get(1).getWorldCoords();                
                double d = 0;
                for (int i = 0; i < 3; i++) {
                    d += (p1[i]-p0[i])*(p1[i]-p0[i]);
                }
                d = Math.sqrt(d);
                
                float[] p2 = pointDescriptors.get(2).getWorldCoords();
                float sign = 0;
                for (int i = 0; i < 3; i++) {
                    sign += (p2[i]-p0[i])*(p1[i]-p0[i]);
                }
                if(sign >= 0)
                    sign = 1;
                else
                    sign = -1;
                
                return (float) (sign*d);              
            }

            @Override
            public int getLocationPointIndex() {
                return 1;
            }

            @Override
            public boolean isPointDescriptorsReady() {
                return (pointDescriptors.size() == 3);
            }

            @Override
            public CalculableType getType() {
                return CalculableType.SIGNED_DISTANCE;
            }

        };
    }
    
    public static CalculableParameter getDistanceCalculable(String name) {
        return new CalculableParameter(CalculableType.DISTANCE.getPreName()+name) {
            @Override
            public float getValue() {
                if(this.pointDescriptors.size() != 2)
                    return 0;
                
                //distance between p0 and p1
                
                float[] p0 = pointDescriptors.get(0).getWorldCoords(); 
                float[] p1 = pointDescriptors.get(1).getWorldCoords();                
                double d = 0;
                for (int i = 0; i < 3; i++) {
                    d += (p1[i]-p0[i])*(p1[i]-p0[i]);
                }
                d = Math.sqrt(d);
                
                return (float) d;              
            }

            @Override
            public int getLocationPointIndex() {
                return 1;
            }

            @Override
            public boolean isPointDescriptorsReady() {
                return (pointDescriptors.size() == 2);
            }

            @Override
            public CalculableType getType() {
                return CalculableType.DISTANCE;
            }

        };
    }

    public static CalculableType getTypeByString(String str) {
        if(CalculableType.ANGLE_FROM_2_LINES.toString().equals(str))
            return CalculableType.ANGLE_FROM_2_LINES;
        if(CalculableType.ANGLE_FROM_3_POINTS.toString().equals(str))
            return CalculableType.ANGLE_FROM_3_POINTS;
        if(CalculableType.LENGTH_FROM_POLYLINE.toString().equals(str))
            return CalculableType.LENGTH_FROM_POLYLINE;
        if(CalculableType.SIGNED_ANGLE_FROM_2_LINES.toString().equals(str))
            return CalculableType.SIGNED_ANGLE_FROM_2_LINES;
        if(CalculableType.MODIFIABLE_SIGNED_ANGLE_FROM_2_LINES.toString().equals(str))
            return CalculableType.MODIFIABLE_SIGNED_ANGLE_FROM_2_LINES;
        if(CalculableType.NORMALIZED_SIGNED_DISTANCE.toString().equals(str))
            return CalculableType.NORMALIZED_SIGNED_DISTANCE;
        if(CalculableType.SIGNED_DISTANCE.toString().equals(str))
            return CalculableType.SIGNED_DISTANCE;
        if(CalculableType.DISTANCE.toString().equals(str))
            return CalculableType.DISTANCE;

        return null;
    }

   private CalculableParamsPool()
   {
   }

}
