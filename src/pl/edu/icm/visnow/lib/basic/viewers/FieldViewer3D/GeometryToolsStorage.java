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

package pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D;

import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.GeometryTools.AngleTool;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.GeometryTools.CenterPointTool;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.GeometryTools.DiameterTool;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.GeometryTools.GeometryTool;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.GeometryTools.LineTool;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.GeometryTools.PointTool;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.GeometryTools.PolygonTool;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.GeometryTools.PolylineTool;
import pl.edu.icm.visnow.lib.basic.viewers.FieldViewer3D.GeometryTools.RadiusTool;

/**
 * @author  Bartosz Borucki (babor@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */

public class GeometryToolsStorage {

    public static final int GEOMETRY_TOOL_POINT = 0;
    public static final int GEOMETRY_TOOL_CENTER_POINT = 1;
    public static final int GEOMETRY_TOOL_LINE = 2;
    public static final int GEOMETRY_TOOL_RADIUS = 3;
    public static final int GEOMETRY_TOOL_DIAMETER = 4;
    public static final int GEOMETRY_TOOL_POLYLINE = 5;
    public static final int GEOMETRY_TOOL_POLYGON = 6;
    public static final int GEOMETRY_TOOL_ANGLE = 7;


    public GeometryToolsStorage() {
    }

    public static GeometryTool getGeometryTool(int n) {
        switch(n) {
            case GEOMETRY_TOOL_POINT:
                return new PointTool();
            case GEOMETRY_TOOL_CENTER_POINT:
                return new CenterPointTool();
            case GEOMETRY_TOOL_LINE:
                return new LineTool();
            case GEOMETRY_TOOL_RADIUS:
                return new RadiusTool();
            case GEOMETRY_TOOL_DIAMETER:
                return new DiameterTool();
            case GEOMETRY_TOOL_POLYLINE:
                return new PolylineTool();
            case GEOMETRY_TOOL_POLYGON:
                return new PolygonTool();
            case GEOMETRY_TOOL_ANGLE:
                return new AngleTool();
            default:
                return null;
        }
    }

    public static int getGeometryToolType(GeometryTool gt) {
        if(gt == null)
            return -1;

        if(gt instanceof PointTool) return GEOMETRY_TOOL_POINT;
        if(gt instanceof AngleTool) return GEOMETRY_TOOL_ANGLE;
        if(gt instanceof CenterPointTool) return GEOMETRY_TOOL_CENTER_POINT;
        if(gt instanceof DiameterTool) return GEOMETRY_TOOL_DIAMETER;
        if(gt instanceof LineTool) return GEOMETRY_TOOL_LINE;
        if(gt instanceof PolygonTool) return GEOMETRY_TOOL_POLYGON;
        if(gt instanceof PolylineTool) return GEOMETRY_TOOL_POLYLINE;
        if(gt instanceof RadiusTool) return GEOMETRY_TOOL_RADIUS;

        return -1;
    }
}
