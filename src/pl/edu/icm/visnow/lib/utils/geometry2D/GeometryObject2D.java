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

package pl.edu.icm.visnow.lib.utils.geometry2D;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pl.edu.icm.visnow.lib.basic.viewers.Viewer2D.Display2DPanel;

/**
 * @author  Bartosz Borucki (babor@icm.edu.pl)
 * Warsaw University, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class GeometryObject2D implements Cloneable {
    protected String name                              = null;
    protected AffineTransform internalTransform = new AffineTransform();
    protected int width = 0;
    protected int height = 0;
    private Display2DPanel panel = null;
    
    /** Creates a new instance of GeometryObject2D */
    public GeometryObject2D(){
        this("object");
    }
    
    public GeometryObject2D(String name){
      this.name = name;
    }
    
    public void drawLocal2D(Graphics2D g, AffineTransform tr) {        
    }
    
    @Override
    public String toString()
    {
      return name;
    }

   public String getName() {
      return name;
   }
   
   public void setName(String name)  {
      this.name = name;
      fireStateChanged();
   }

    public AffineTransform getInternalTransform() {
        return internalTransform;
    }

    public void setInternalTransform(AffineTransform internalTransform) {
        this.internalTransform = internalTransform;
        fireStateChanged();
    }
   
    @Override
    public Object clone() throws CloneNotSupportedException {
        try {
            GeometryObject2D out = (GeometryObject2D)super.clone();
            out.setInternalTransform((AffineTransform)internalTransform.clone());
            return out;
        } catch(CloneNotSupportedException ex) {
            return null;
        }
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
        fireStateChanged();
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
        fireStateChanged();
    }
    
    public float getLeftmostX() {
        Point2D.Float[] pts = new Point2D.Float[4];
        pts[0] = (Point2D.Float)internalTransform.transform(new Point2D.Float(0,0), null);
        pts[1] = (Point2D.Float)internalTransform.transform(new Point2D.Float(width,0), null);
        pts[2] = (Point2D.Float)internalTransform.transform(new Point2D.Float(width,height), null);
        pts[3] = (Point2D.Float)internalTransform.transform(new Point2D.Float(0,height), null);
        float minx = pts[0].x;
        for (int i = 1; i < pts.length; i++) {
            if( pts[i].x < minx ) minx = pts[i].x;
        }
        return minx;
    }

    public float getRightmostX() {
        Point2D.Float[] pts = new Point2D.Float[4];
        pts[0] = (Point2D.Float)internalTransform.transform(new Point2D.Float(0,0), null);
        pts[1] = (Point2D.Float)internalTransform.transform(new Point2D.Float(width,0), null);
        pts[2] = (Point2D.Float)internalTransform.transform(new Point2D.Float(width,height), null);
        pts[3] = (Point2D.Float)internalTransform.transform(new Point2D.Float(0,height), null);
        float maxx = pts[0].x;
        for (int i = 1; i < pts.length; i++) {
            if( pts[i].x > maxx ) maxx = pts[i].x;
        }
        return maxx;
    }
    
    public float getTopmostY() {
        Point2D.Float[] pts = new Point2D.Float[4];
        pts[0] = (Point2D.Float)internalTransform.transform(new Point2D.Float(0,0), null);
        pts[1] = (Point2D.Float)internalTransform.transform(new Point2D.Float(width,0), null);
        pts[2] = (Point2D.Float)internalTransform.transform(new Point2D.Float(width,height), null);
        pts[3] = (Point2D.Float)internalTransform.transform(new Point2D.Float(0,height), null);
        float miny = pts[0].y;
        for (int i = 1; i < pts.length; i++) {
            if( pts[i].y < miny ) miny = pts[i].y;
        }
        return miny;
    }
    
    public float getBottommostY() {
        Point2D.Float[] pts = new Point2D.Float[4];
        pts[0] = (Point2D.Float)internalTransform.transform(new Point2D.Float(0,0), null);
        pts[1] = (Point2D.Float)internalTransform.transform(new Point2D.Float(width,0), null);
        pts[2] = (Point2D.Float)internalTransform.transform(new Point2D.Float(width,height), null);
        pts[3] = (Point2D.Float)internalTransform.transform(new Point2D.Float(0,height), null);
        float maxy = pts[0].y;
        for (int i = 1; i < pts.length; i++) {
            if( pts[i].y > maxy ) maxy = pts[i].y;
        }
        return maxy;
    }

    public String getLocalInfoAt(float x, float y) {
        return "";
    }

    public String getDetailedLocalInfoAt(float x, float y) {
        return "";
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
    public synchronized void addChangeListener(ChangeListener listener) {
        changeListenerList.add(listener);
    }

    /**
     * Removes ChangeListener from the list of listeners.
     * @param listener The listener to remove.
     */
    public synchronized void removeChangeListener(ChangeListener listener) {
        changeListenerList.remove(listener);
    }

    /**
     * Notifies all registered listeners about the event.
     *
     * @param object Parameter #1 of the <CODE>ChangeEvent<CODE> constructor.
     */
    public void fireStateChanged() {
        if(!active)
            return;
        ChangeEvent e = new ChangeEvent(this);
        for (ChangeListener listener : changeListenerList) {
            listener.stateChanged(e);
        }
    }
    
    private boolean active = true;
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public boolean isActive() {
        return this.active;
    }

    /**
     * @return the panel
     */
    public Display2DPanel getPanel() {
        return panel;
    }

    /**
     * @param panel the panel to set
     */
    public void setPanel(Display2DPanel panel) {
        this.panel = panel;
    }
    
}
