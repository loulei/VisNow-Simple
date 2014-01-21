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
import pl.edu.icm.visnow.datasets.CellArray;
import pl.edu.icm.visnow.datasets.CellSet;
import pl.edu.icm.visnow.datasets.IrregularField;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.datasets.VNObject;
import pl.edu.icm.visnow.datasets.cells.Cell;
import pl.edu.icm.visnow.datasets.dataarrays.BitArray;
import pl.edu.icm.visnow.datasets.dataarrays.BooleanDataArray;
import pl.edu.icm.visnow.datasets.dataarrays.ComplexDataArray;
import pl.edu.icm.visnow.datasets.dataarrays.LogicDataArray;
import pl.edu.icm.visnow.datasets.dataarrays.ObjectDataArray;
import pl.edu.icm.visnow.datasets.dataarrays.StringDataArray;

/**
 *
 * @author Bartosz Borucki (babor@icm.edu.pl), University of Warsaw, ICM
 *
 */

public class GeometryParams {

    private ArrayList<PointDescriptor> pointsDescriptors = new ArrayList<PointDescriptor>();
    private ArrayList<CustomSlicesDescriptor> slicesDescriptors = new ArrayList<CustomSlicesDescriptor>();
    private ArrayList<ConnectionDescriptor> connectionDescriptors = new ArrayList<ConnectionDescriptor>();
    private int[] selectedPoints = null;
    private boolean showGlyphs = true;
    private float glyphScale = 0.5f;
    private boolean paintLabels = true;
    private boolean showConnections2D = true;
    private boolean showDistances2D = false;
    private boolean showConnections3D = true;
    private float connectionScale = 0.5f;
    private RegularField inField = null;
    private String infoString = null;
    private String patientString = "";
    private int[] intersectionPoint = null;
    private boolean wizard = false;
    //private boolean wizard = true;
    private boolean slicePositioning = false;
    private boolean selectionFollowSlices = true;

    private String nextPointName = null;
    private boolean blockAdding = false;
    
    private int currentClassId = -1;
    private boolean addFieldData = false;

    public GeometryParams() {
    }

    public ArrayList<int[]> getPoints() {
        ArrayList<int[]> points = new ArrayList<int[]>();
        for (PointDescriptor p : pointsDescriptors) {
            points.add(p.getIndices());
        }
        return points;
    }

    public IrregularField getPointsGeometryField(boolean addFieldDataComponents) {
        return GeometryFieldConverter.pac2field(pointsDescriptors, connectionDescriptors, currentClassId != -1, addFieldDataComponents, inField);
    }

    public int modifyPoint(int n, int[] p) {
        if (inField == null) {
            return -1;
        }

        if(n < 0 || n >= pointsDescriptors.size())
            return -1;

        if(pointsDescriptors.get(n) instanceof DependantPointDescriptor)
            return -1;

        float[] c = new float[3];
        if(inField.getDims().length == 3) {
            c = inField.getGridCoords((float) (p[0]), (float) (p[1]), (float) (p[2]));
        } else if(inField.getDims().length == 2) {
            float[] tmp = inField.getGridCoords((float) (p[0]), (float) (p[1]));
            c[0] = tmp[0];
            c[1] = tmp[1];
            c[2] = 0.0f;
        } else {
            return -1;
        }

        pointsDescriptors.get(n).setIndices(p);
        pointsDescriptors.get(n).setWorldCoords(c);

        int[] tmp = getSelectedPoints();
        fireGeometryParamsChanged(GeometryParamsEvent.TYPE_POINT_MODIFIED);
        setSelectedPoints(tmp);
        return n;
    }

    public int modifyPoint(int n, float[] c) {
        if (inField == null) {
            return -1;
        }

        if(n < 0 || n >= pointsDescriptors.size())
            return -1;

        if(pointsDescriptors.get(n) instanceof DependantPointDescriptor)
            return -1;

        int[] p = inField.getIndices(c[0], c[1], c[2]);
        if(p == null)
            return -1;
        float[] fp = inField.getGridCoords(p[0], p[1], p[2]);

        pointsDescriptors.get(n).setIndices(p);
        pointsDescriptors.get(n).setWorldCoords(fp);

        int[] tmp = getSelectedPoints();
        fireGeometryParamsChanged(GeometryParamsEvent.TYPE_POINT_MODIFIED);
        setSelectedPoints(tmp);
        return n;
    }


    public int addPoint(int[] p) {
        if (blockAdding || inField == null) {
            return -1;
        }

        float[] c = new float[3];
        if(inField.getDims().length == 3) {
            c = inField.getGridCoords((float) (p[0]), (float) (p[1]), (float) (p[2]));
        } else if(inField.getDims().length == 2) {
            float[] tmp = inField.getGridCoords((float) (p[0]), (float) (p[1]));
            c[0] = tmp[0];
            c[1] = tmp[1];
            c[2] = 0.0f;
        } else {
            return -1;
        }

        if(nextPointName != null) {
            pointsDescriptors.add(new PointDescriptor(new String(nextPointName), p, c, currentClassId));
            nextPointName = null;
        } else {
            pointsDescriptors.add(new PointDescriptor(p, c, currentClassId));
        }
//        int[] tmp = new int[pointsDescriptors.size()];
//        int[] dims = inField.getDims();
//        for (int i = 0; i < tmp.length; i++)
//       {
//          int[] idx = pointsDescriptors.get(i).getIndices();
//          tmp[i] = (idx[2] * dims[1] + idx[1]) * dims[0] + idx[0];
//       }
//        setSelectedPoints(tmp);
        int out = pointsDescriptors.size()-1;
        fireGeometryParamsChanged(GeometryParamsEvent.TYPE_POINT_ADDED);
        return out;
    }

    public int addPoint(String name, int[] p) {
        if (blockAdding || inField == null) {
            return -1;
        }
        float[] c = inField.getGridCoords((float) (p[0]), (float) (p[1]), (float) (p[2]));
        pointsDescriptors.add(new PointDescriptor(name, p, c, currentClassId));
        int out = pointsDescriptors.size()-1;
        fireGeometryParamsChanged(GeometryParamsEvent.TYPE_POINT_ADDED);
        return out;
    }
    
    public int addPoint(String name, int[] p, int classId) {
        if (blockAdding || inField == null) {
            return -1;
        }
        float[] c = inField.getGridCoords((float) (p[0]), (float) (p[1]), (float) (p[2]));
        pointsDescriptors.add(new PointDescriptor(name, p, c, classId));
        if(classId >= currentClassId)
            currentClassId = classId + 1;
        
        int out = pointsDescriptors.size()-1;
        fireGeometryParamsChanged(GeometryParamsEvent.TYPE_POINT_ADDED);
        return out;
    }

    public int addPoint(String name, float[] c) {
        if (blockAdding || inField == null || c == null) {
            return -1;
        }
        int[] p = inField.getIndices(c[0], c[1], c[2]);
        float[] fp = null;
        if(inField.getDims().length == 3) {
            fp = inField.getGridCoords(p[0], p[1], p[2]);
        } else if(inField.getDims().length == 2) {
            float[] tmp = inField.getGridCoords(p[0], p[1]);
            fp = new float[3];
            fp[0] = tmp[0];
            fp[1] = tmp[1];
            fp[2] = inField.getAffine()[3][2];
        }
        pointsDescriptors.add(new PointDescriptor(name, p, fp, currentClassId));
        int out = pointsDescriptors.size()-1;
        fireGeometryParamsChanged(GeometryParamsEvent.TYPE_POINT_ADDED);
        return out;
    }

    public int addPoint(String name, float[] c, int classId) {
        if (blockAdding || inField == null || c == null) {
            return -1;
        }
        int[] p = inField.getIndices(c[0], c[1], c[2]);
        float[] fp = null;
        if(inField.getDims().length == 3) {
            fp = inField.getGridCoords(p[0], p[1], p[2]);
        } else if(inField.getDims().length == 2) {
            float[] tmp = inField.getGridCoords(p[0], p[1]);
            fp = new float[3];
            fp[0] = tmp[0];
            fp[1] = tmp[1];
            fp[2] = inField.getAffine()[3][2];
        }
        pointsDescriptors.add(new PointDescriptor(name, p, fp, classId));
        if(classId >= currentClassId)
            currentClassId = classId + 1;
        
        int out = pointsDescriptors.size()-1;
        fireGeometryParamsChanged(GeometryParamsEvent.TYPE_POINT_ADDED);
        return out;
    }
    
    public int[] addPoint(CalculablePoint cp) {
        if(cp == null)
            return null;
        
        ArrayList<Integer> out = new ArrayList<Integer>();
        ArrayList<PointDescriptor> pds = cp.getDependantPointDescriptors();
        for (int i = 0; i < pds.size(); i++) {
            if(!pointsDescriptors.contains(pds.get(i))) {
                addPoint(pds.get(i).getName(), pds.get(i).getWorldCoords());
                out.add(new Integer(i));
            }
        }
        
        pointsDescriptors.add(new DependantPointDescriptor(cp.getName(), cp, inField));
        out.add(new Integer(pointsDescriptors.size()-1));

        int[] outa = new int[out.size()];
        for (int i = 0; i < out.size(); i++) {
            outa[i] = out.get(i);
        }

        fireGeometryParamsChanged(GeometryParamsEvent.TYPE_POINT_ADDED);
        return outa;
    }

    public int[] addPoints(String[] name, int[][] p) {
        return addPoints(name, p, null);
    }

    public int[] addPoints(String[] name, int[][] p, int[] classes) {
        if (blockAdding || inField == null || name == null || p == null || name.length != p.length) {
            return null;
        }
        int[] out = new int[p.length];
        int cId = -1;
        for (int i = 0; i < p.length; i++) {
            float[] c = inField.getGridCoords((float) (p[i][0]), (float) (p[i][1]), (float) (p[i][2]));
            if(classes != null) 
                cId = classes[i];
            pointsDescriptors.add(new PointDescriptor(name[i], p[i], c, cId));
            if(cId >= currentClassId)
                currentClassId = cId + 1;
            out[i] = pointsDescriptors.size()-1;
        }

        updatePointNumerator();

        fireGeometryParamsChanged(GeometryParamsEvent.TYPE_POINT_ADDED);
        return out;
    }

    public int[] addPoints(int[][] p) {
        if (blockAdding || inField == null || p == null) {
            return null;
        }

        int[] out = new int[p.length];
        for (int i = 0; i < p.length; i++) {
            out[i] = addPoint(p[i]);            
        }
        return out;
    }
    
    public int[] addPoints(float[][] p) {
        if (blockAdding || inField == null || p == null) {
            return null;
        }

        int[] out = new int[p.length];
        for (int i = 0; i < p.length; i++) {
            out[i] = addPoint(p[i]);            
        }
        return out;
    }
    

    public int addPoint(float[] c) {
        if (blockAdding || inField == null) {
            return -1;
        }
        int[] p = inField.getIndices(c[0], c[1], c[2]);
        float[] fp = inField.getGridCoords(p[0], p[1], p[2]);
        if(nextPointName != null) {
            pointsDescriptors.add(new PointDescriptor(new String(nextPointName), p, fp, currentClassId));
            nextPointName = null;
        } else {
            pointsDescriptors.add(new PointDescriptor(p, fp, currentClassId));
        }
        int out = pointsDescriptors.size()-1;
        fireGeometryParamsChanged(GeometryParamsEvent.TYPE_POINT_ADDED);
        return out;
    }

    public int addPointWithSlices(float[] c, float[] p0, float[][] vv) {
        if (blockAdding || inField == null) {
            return -1;
        }
        int[] p = inField.getIndices(c[0], c[1], c[2]);
        float[] fp = inField.getGridCoords(p[0], p[1], p[2]);
        if(nextPointName != null) {
            pointsDescriptors.add(new PointDescriptor(new String(nextPointName), p, fp, currentClassId));
            nextPointName = null;
        } else {
            pointsDescriptors.add(new PointDescriptor(p, fp, currentClassId));
        }
        if(p0 != null && vv != null)
            slicesDescriptors.add(new CustomSlicesDescriptor(p0.clone(), vv.clone()));
        else
            slicesDescriptors.add(new CustomSlicesDescriptor(null, null));

        int out = pointsDescriptors.size()-1;
        fireGeometryParamsChanged(GeometryParamsEvent.TYPE_POINT_ADDED);
        return out;
    }

    public void clearLastPoint() {
        if (inField == null) {
            return;
        }

        removePoint(pointsDescriptors.size() - 1);
    }

    public void clearPoints() {
        clearConenctions();
        if (pointsDescriptors.isEmpty()) {
            return;
        }

        pointsDescriptors.clear();
        fireGeometryParamsChanged(GeometryParamsEvent.TYPE_POINT_REMOVED);
        PointDescriptor.resetPointCounter();
    }

    public void removePoints(int[] indices) {
        PointDescriptor pd;
        ConnectionDescriptor cd;
        for (int i = 0; i < indices.length; i++) {
            pd = pointsDescriptors.get(indices[i]);
            if(pd == null) continue;
            for (int j = 0; j < connectionDescriptors.size(); j++) {
                cd = connectionDescriptors.get(j);
                if(cd.getP1() == pd || cd.getP2() == pd) {
                    removeConnection(j);
                    j--;
                }
            }

            updateRemoveDependantPoints(pointsDescriptors.get(indices[i]));
            pointsDescriptors.remove(indices[i]);
            
            for (int j = i + 1; j < indices.length; j++) {
                if (indices[j] > indices[i]) {
                    indices[j]--;
                }
            }
        }
        fireGeometryParamsChanged(GeometryParamsEvent.TYPE_POINT_REMOVED);
    }

    public void removePoint(int n) {
        if( n < 0 || n >= pointsDescriptors.size())
            return;

        ConnectionDescriptor cd;
        PointDescriptor pd = pointsDescriptors.get(n);
        for (int i = 0; i < connectionDescriptors.size(); i++) {
            cd = connectionDescriptors.get(i);
            if(cd.getP1() == pd || cd.getP2() == pd) {
                removeConnection(i);
                i--;
            }
        }

        updateRemoveDependantPoints(pointsDescriptors.get(n));
        pointsDescriptors.remove(n);
        fireGeometryParamsChanged(GeometryParamsEvent.TYPE_POINT_REMOVED);
    }

    public void removePoint(PointDescriptor pd) {
        if(pd == null)
            return;
        
        ConnectionDescriptor cd;
        for (int i = 0; i < connectionDescriptors.size(); i++) {
            cd = connectionDescriptors.get(i);
            if(cd.getP1() == pd || cd.getP2() == pd) {
                removeConnection(i);
                i--;
            }
        }

        updateRemoveDependantPoints(pd);
        pointsDescriptors.remove(pd);
        fireGeometryParamsChanged(GeometryParamsEvent.TYPE_POINT_REMOVED);
    }

    public boolean isPointsContain(PointDescriptor pd) {
        if(pd == null)
            return false;

        return pointsDescriptors.contains(pd);
    }

    public ArrayList<PointDescriptor> getPointsDescriptors() {
        return pointsDescriptors;
    }

    public PointDescriptor getPointsDescriptor(int n) {
        if(n < 0 || n >= pointsDescriptors.size())
            return null;
        return pointsDescriptors.get(n);
    }

    public ArrayList<PointDescriptor> getPointsDescriptors(int[] indices) {
        ArrayList<PointDescriptor> out = new ArrayList<PointDescriptor>();
        for (int i = 0; i < indices.length; i++) {
            out.add(pointsDescriptors.get(indices[i]));
        }
        return out;
    }

    public PointDescriptor getPointsDescriptorByName(String name) {
        for (int i = 0; i < pointsDescriptors.size(); i++) {
            if(pointsDescriptors.get(i).getName().equals(name))
                return pointsDescriptors.get(i);
        }
        return null;
    }

    public ArrayList<CustomSlicesDescriptor> getSlicesDescriptors() {
        return slicesDescriptors;
    }

    public CustomSlicesDescriptor getSlicesDescriptor(int n) {
        if(n < 0 || n >= slicesDescriptors.size())
            return null;
        return slicesDescriptors.get(n);
    }
    
    public int getNumberOfPoints() {
        return pointsDescriptors.size();
    }

    public void setInfoString(String infoString) {
        this.infoString = infoString;
    }

    public void setPatientString(String patientString) {
        this.patientString = patientString;
    }

    public String getPatientString() {
        return patientString;
    }

    public void setPointsDescriptors(ArrayList<PointDescriptor> pointsDescriptors) {
        this.pointsDescriptors = pointsDescriptors;
    }

    public void setInField(RegularField inField) {
        this.inField = inField;
        //clearConenctions();
        clearPoints();
    }
    /**
     * Utility field holding list of ChangeListeners.
     */
    private transient ArrayList<GeometryParamsListener> changeListenerList =
            new ArrayList<GeometryParamsListener>();

    /**
     * Registers ChangeListener to receive events.
     * @param listener The listener to register.
     */
    public synchronized void addGeometryParamsListener(GeometryParamsListener listener) {
        changeListenerList.add(listener);
    }

    /**
     * Removes ChangeListener from the list of listeners.
     * @param listener The listener to remove.
     */
    public synchronized void removeGeometryParamsListener(GeometryParamsListener listener) {
        changeListenerList.remove(listener);
    }

    /**
     * Notifies all registered listeners about the event.
     *
     * @param object Parameter #1 of the <CODE>ChangeEvent<CODE> constructor.
     */
    public void fireGeometryParamsChanged(int type) {
        GeometryParamsEvent e = new GeometryParamsEvent(this, type);
        for (GeometryParamsListener listener : changeListenerList) {
            listener.onGeometryParamsChanged(e);
        }
    }

    /**
     * @return the inField
     */
    public RegularField getInField() {
        return inField;
    }

    /**
     * @return the showGlyphs
     */
    public boolean isShowGlyphs() {
        return showGlyphs;
    }

    /**
     * @param showGlyphs the showGlyphs to set
     */
    public void setShowGlyphs(boolean showGlyphs) {
        this.showGlyphs = showGlyphs;
        fireGeometryParamsChanged(GeometryParamsEvent.TYPE_GLYPHS);
    }

    /**
     * @return the glyphScale
     */
    public float getGlyphScale() {
        return glyphScale;
    }

    /**
     * @param glyphScale the glyphScale to set
     */
    public void setGlyphScale(float glyphScale) {
        this.glyphScale = glyphScale;
        fireGeometryParamsChanged(GeometryParamsEvent.TYPE_GLYPHS);
    }

    /**
     * @return the paintLabels
     */
    public boolean isPaintLabels() {
        return paintLabels;
    }

    /**
     * @param paintLabels the paintLabels to set
     */
    public void setPaintLabels(boolean paintLabels) {
        this.paintLabels = paintLabels;
        fireGeometryParamsChanged(GeometryParamsEvent.TYPE_LABELS);
    }

    /**
     * @return the selectedPoint
     */
    public int[] getSelectedPoints() {
        return selectedPoints;
    }

    /**
     * @param selectedPoint the selectedPoint to set
     */
    public void setSelectedPoints(int[] selectedPoints) {
        if(selectedPoints == null && this.selectedPoints == null) {
            return;
        }

        if(selectedPoints != null && this.selectedPoints != null && selectedPoints.length == this.selectedPoints.length) {
            boolean same = true;
            for (int i = 0; i < selectedPoints.length; i++) {
                if(selectedPoints[i] != this.selectedPoints[i]) {
                    same = false;
                    break;
                }
            }

            if(same)
                return;
        }


        if(selectedPoints == null || selectedPoints.length == 0) {
            this.selectedPoints = null;
        } else {
            this.selectedPoints = selectedPoints.clone();
        }
        fireGeometryParamsChanged(GeometryParamsEvent.TYPE_POINT_SELECTION);
    }

    /**
     * @return the connectionDescriptors
     */
    public ArrayList<ConnectionDescriptor> getConnectionDescriptors() {
        return connectionDescriptors;
    }

    public void addConnection(PointDescriptor p1, PointDescriptor p2) {
        if (inField == null || blockAdding) {
            return;
        }

        if (p1 == p2) {
            return;
        }

        connectionDescriptors.add(new ConnectionDescriptor("" + p1.getName() + "->" + p2.getName(), p1, p2));
        fireGeometryParamsChanged(GeometryParamsEvent.TYPE_CONNECTION);
    }

    public void addConnection(PointDescriptor p1, PointDescriptor p2, String connectionName) {
        if (inField == null || blockAdding) {
            return;
        }

        if (p1 == p2) {
            return;
        }

        connectionDescriptors.add(new ConnectionDescriptor(connectionName, p1, p2));
        fireGeometryParamsChanged(GeometryParamsEvent.TYPE_CONNECTION);
    }

    public void addConnections(String[] names, int[][] conns) {
        if (blockAdding || inField == null || names == null || conns == null || names.length != conns.length) {
            return;
        }

        for (int i = 0; i < conns.length; i++) {
            connectionDescriptors.add(
                    new ConnectionDescriptor(
                    new String(names[i]),
                    pointsDescriptors.get(conns[i][0]),
                    pointsDescriptors.get(conns[i][1])));
        }
        fireGeometryParamsChanged(GeometryParamsEvent.TYPE_CONNECTION);
    }

    public void clearConenctions() {
        connectionDescriptors.clear();
        fireGeometryParamsChanged(GeometryParamsEvent.TYPE_CONNECTION);
    }

    public ConnectionDescriptor getConnectionByPointNames(String p0, String p1) {
        ConnectionDescriptor cd;
        for (int i = 0; i < connectionDescriptors.size(); i++) {
            cd = connectionDescriptors.get(i);
            if(cd.getP1().getName().equals(p0) && cd.getP2().getName().equals(p1))
                return cd;
        }
        return null;

    }

    public void removeConnections(int[] indices) {
        for (int i = 0; i < indices.length; i++) {
            removeConnection(indices[i]);
            for (int j = i + 1; j < indices.length; j++) {
                if (indices[j] > indices[i]) {
                    indices[j]--;
                }
            }
        }
        fireGeometryParamsChanged(GeometryParamsEvent.TYPE_CONNECTION);
    }

    public void removeConnection(int n) {
        connectionDescriptors.remove(n);
        fireGeometryParamsChanged(GeometryParamsEvent.TYPE_CONNECTION);
    }

    /**
     * @return the showConnections2D
     */
    public boolean isShowConnections2D() {
        return showConnections2D;
    }

    /**
     * @param showConnections2D the showConnections2D to set
     */
    public void setShowConnections2D(boolean showConnections2D) {
        this.showConnections2D = showConnections2D;
        if(!showConnections2D) {
            this.showDistances2D = false;
        }
        fireGeometryParamsChanged(GeometryParamsEvent.TYPE_CONN2D);
    }

    /**
     * @return the showDistances2D
     */
    public boolean isShowDistances2D() {
        return showDistances2D;
    }

    /**
     * @param showDistances2D the showConnections2D to set
     */
    public void setShowDistances2D(boolean showDistances2D) {
        this.showDistances2D = showDistances2D;
        fireGeometryParamsChanged(GeometryParamsEvent.TYPE_CONN2D);
    }

    /**
     * @return the showConnections3D
     */
    public boolean isShowConnections3D() {
        return showConnections3D;
    }

    /**
     * @param showConnections3D the showConnections3D to set
     */
    public void setShowConnections3D(boolean showConnections3D) {
        this.showConnections3D = showConnections3D;
        fireGeometryParamsChanged(GeometryParamsEvent.TYPE_CONN3D);
    }

    /**
     * @return the connectionScale
     */
    public float getConnectionScale() {
        return connectionScale;
    }

    /**
     * @param connectionScale the connectionScale to set
     */
    public void setConnectionScale(float connectionScale) {
        this.connectionScale = connectionScale;
        fireGeometryParamsChanged(GeometryParamsEvent.TYPE_CONN3D);
    }

    public String getInfoString() {
        return infoString;
    }

    /**
     * @return the intersectionPoint
     */
    public int[] getIntersectionPoint() {
        return intersectionPoint;
    }

    /**
     * @param intersectionPoint the intersectionPoint to set
     */
    public void setIntersectionPoint(int[] intersectionPoint) {
        this.intersectionPoint = intersectionPoint;
    }

    private void updatePointNumerator() {
        int maxp = 0;
        int tmpn;
        for (int i = 0; i < pointsDescriptors.size(); i++) {
            if (pointsDescriptors.get(i).getName().startsWith("p")) {
                String tmpName = pointsDescriptors.get(i).getName().substring(1);
                String tmpNumberString;
                if (isStartsWithNumber(tmpName)) {
                    try {
                        tmpNumberString = getStartingNumbers(tmpName);
                        if (tmpNumberString == null || tmpNumberString.length() < 1) {
                            continue;
                        }
                        tmpn = Integer.parseInt(tmpNumberString);
                    } catch (NumberFormatException ex) {
                        continue;
                    }
                    if (tmpn > maxp) {
                        maxp = tmpn;
                    }
                }
            }
        }
        PointDescriptor.setPointCounter(maxp + 1);
    }

    private boolean isStartsWithNumber(String str) {
        return str.startsWith("0") ||
                str.startsWith("1") ||
                str.startsWith("2") ||
                str.startsWith("3") ||
                str.startsWith("4") ||
                str.startsWith("5") ||
                str.startsWith("6") ||
                str.startsWith("7") ||
                str.startsWith("8") ||
                str.startsWith("9");
    }

    private String getStartingNumbers(String str) {
        if (str == null) {
            return "";
        }

        if (!isStartsWithNumber(str)) {
            return "";
        }

        int n = 1;
        String tmp = str.substring(n);
        while (isStartsWithNumber(tmp)) {
            n++;
            tmp = str.substring(n);
        }
        return str.substring(0, n);
    }

    /**
     * @return the wizard
     */
    public boolean isWizard() {
        return wizard;
    }

    /**
     * @param wizard the wizard to set
     */
    public void setWizard(boolean wizard) {
        this.wizard = wizard;
        fireGeometryParamsChanged(GeometryParamsEvent.TYPE_ALL);
    }

    /**
     * @param nextPointName the nextPointName to set
     */
    public void setNextPointName(String nextPointName) {
        this.nextPointName = nextPointName;
    }

    /**
     * @return the slicePositioning
     */
    public boolean isSlicePositioning() {
        return slicePositioning;
    }

    /**
     * @param slicePositioning the slicePositioning to set
     */
    public void setSlicePositioning(boolean slicePositioning) {
        this.slicePositioning = slicePositioning;
        fireGeometryParamsChanged(GeometryParamsEvent.TYPE_POINT_SELECTION);
    }

    /**
     * @return the selectionFollowSlices
     */
    public boolean isSelectionFollowSlices() {
        return selectionFollowSlices;
    }

    /**
     * @param selectionFollowSlices the selectionFollowSlices to set
     */
    public void setSelectionFollowSlices(boolean selectionFollowSlices) {
        this.selectionFollowSlices = selectionFollowSlices;
    }

    private String defaultNfdFile = null;
    //private String defaultNfdFile = "cmc3d.nfd";
    public String getDefaultNfdFile() {
        return defaultNfdFile;
    }

    public void setDefaultNfdFile(String filePath) {
        this.defaultNfdFile = filePath;
    }

    private String defaultTasFile = null;
    public String getDefaultTasFile() {
        return defaultTasFile;
    }

    public void setDefaultTasFile(String filePath) {
        this.defaultTasFile = filePath;
    }

    public void setBlockAdding(boolean b) {
        this.blockAdding = b;
    }

    /**
     * @return the blockAdding
     */
    public boolean isBlockAdding() {
        return blockAdding;
    }

    private void updateRemoveDependantPoints(PointDescriptor pd) {
        if(pd == null)
            return;

        for (int i = 0; i < pointsDescriptors.size(); i++) {
            if(pointsDescriptors.get(i).isDependant()) {
                if(((DependantPointDescriptor)pointsDescriptors.get(i)).dependsOn(pd)) {
                    removePoint(pointsDescriptors.get(i));
                    i--;
                }
            }
        }
    }

    /**
     * @return the currentClassId
     */
    public int getCurrentClassId() {
        return currentClassId;
    }

    /**
     * @param currentClassId the currentClassId to set
     */
    public void setCurrentClassId(int currentClassId) {
        if(currentClassId == this.currentClassId)
            return;
        
        boolean fire = (currentClassId == -1 || this.currentClassId == -1);
        this.currentClassId = currentClassId;
        if(fire)
            fireGeometryParamsChanged(GeometryParamsEvent.TYPE_POINT_CLASS);
    }

    /**
     * @return the addFieldData
     */
    public boolean isAddFieldData() {
        return addFieldData;
    }

    /**
     * @param addFieldData the addFieldData to set
     */
    public void setAddFieldData(boolean addFieldData) {
        this.addFieldData = addFieldData;
    }
}
