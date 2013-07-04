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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Vector;
import pl.edu.icm.visnow.lib.basic.viewers.Viewer2D.Display2DPanel;

/**
 * @author  Bartosz Borucki (babor@icm.edu.pl)
 * Warsaw University, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class TransformedGeometryObject2D {
    protected GeometryObject2D me = null;
    protected GeometryObject2DStruct meStruct = null;
    protected AffineTransform externalTransform = new AffineTransform();    
    protected boolean visible = true;
    protected float opacity = 1.0f;
    protected int alphaCompositeType = AlphaComposite.SRC_OVER;
    protected String parentModulePort = "";
    protected TransformedGeometryObject2D parent = null;
    protected Vector<TransformedGeometryObject2D> children = new Vector<TransformedGeometryObject2D>();

    protected String name = "";

    protected Display2DPanel panel = null;
    
    public TransformedGeometryObject2D() {
    }
    
    public TransformedGeometryObject2D(GeometryObject2D obj) {
        this.me = obj;
        this.meStruct = null;
        if(this.me != null)
            this.name = new String(obj.getName());
    }

    public TransformedGeometryObject2D(GeometryObject2DStruct struct) {
        this.me = struct.getGeometryObject2D();
        this.meStruct = struct;
        if(this.me != null)
            this.name = new String(struct.getName());
        this.parentModulePort = struct.getParentModulePort();
        for (int i = 0; i < struct.getChildren().size(); i++) {
            this.addChild(new TransformedGeometryObject2D(struct.getChildren().get(i)));
        }
    }
      
    public void updateWithStruct(GeometryObject2DStruct struct) {
        this.me = (GeometryObject2D) struct.getGeometryObject2D();
        boolean tmp;
        int j,i;
        TransformedGeometryObject2D nt;
        for (i = 0; i < struct.getChildren().size(); i++) {
            tmp = false;
            for (j = 0; j < children.size(); j++) {
                if(struct.getChildren().get(i).getName().equals(children.get(j).getName())) {
                    tmp = true;
                    break;
                }
            }
            if(tmp) {
                children.get(j).updateWithStruct(struct.getChildren().get(i));
            } else {
                nt = new TransformedGeometryObject2D(struct.getChildren().get(i));
                nt.setOpacity(opacity);
                addChild(nt);
            }
        }

        for (j = 0; j < children.size(); j++) {
            tmp = false;
            for (i = 0; i < struct.getChildren().size(); i++) {
                if(struct.getChildren().get(i).getName().equals(children.get(j).getName())) {
                    tmp = true;
                    break;
                }
            }
            if(!tmp) {
                children.remove(j);
            }
        }
    }

    public void updateWithTransformedGeometryObject2D(TransformedGeometryObject2D trobj) {
        this.me = trobj.getGeometryObject2D();
        boolean tmp;
        int j,i;
        TransformedGeometryObject2D nt;
        for (i = 0; i < trobj.getChildren().size(); i++) {
            tmp = false;
            for (j = 0; j < children.size(); j++) {
                if(trobj.getChildren().get(i).getName().equals(children.get(j).getName())) {
                    tmp = true;
                    break;
                }
            }
            if(tmp) {
                children.get(j).updateWithTransformedGeometryObject2D(trobj.getChildren().get(i));
            } else {
                nt = trobj.getChildren().get(i);
                nt.setOpacity(opacity);
                addChild(nt);
            }
        }

        for (j = 0; j < children.size(); j++) {
            tmp = false;
            for (i = 0; i < trobj.getChildren().size(); i++) {
                if(trobj.getChildren().get(i).getName().equals(children.get(j).getName())) {
                    tmp = true;
                    break;
                }
            }
            if(!tmp) {
                children.remove(j);
            }
        }
    }
    
    public AffineTransform getExternalTransform() {
        return externalTransform;
    }

    public void setExternalTransform(AffineTransform externalTransform) {
        this.externalTransform = externalTransform;
    }
    
    public AffineTransform getInternalTransform() {
        return me.getInternalTransform();
    }
        
    public void draw2D(Graphics2D g, Display2DPanel panel) {
        this.setPanel(panel);
        Font f = g.getFont();
        Color c = g.getColor();        
        float parentAlpha = 1.0f;        
        Stroke stroke = g.getStroke();
        Composite comp = g.getComposite();
        if(comp instanceof AlphaComposite) {
            AlphaComposite ac = (AlphaComposite) g.getComposite();
            parentAlpha = ac.getAlpha();            
        }
        
        if(me != null && visible) {
            g.setComposite(AlphaComposite.getInstance(alphaCompositeType, parentAlpha * opacity));
            me.setPanel(panel);
            me.drawLocal2D(g, getAbsoluteTransform());
            for (int i = 0; i < children.size(); i++) {
                children.get(i).draw2D(g, panel);
            }
        }

        g.setStroke(stroke);
        g.setComposite(comp);
        g.setFont(f);
        g.setColor(c);

    }
    
    public AffineTransform getAbsoluteTransform() {
        AffineTransform mt = new AffineTransform();
        if(me == null)
            return mt;
        if(parent != null)
            mt.concatenate(parent.getAbsoluteTransform());
        mt.concatenate(externalTransform);
        mt.concatenate(me.getInternalTransform());
        return mt;
    }

    public AffineTransform getAbsoluteExternalTransform() {
        AffineTransform mt = new AffineTransform();
        if(parent != null)
            mt.concatenate(parent.getAbsoluteTransform());
        mt.concatenate(externalTransform);
        return mt;
    }

    public AffineTransform getExtIntTransform() {
        AffineTransform mt = new AffineTransform();
        mt.concatenate(externalTransform);
        mt.concatenate(me.getInternalTransform());
        return mt;
    }
    
    
    public GeometryObject2D getGeometryObject2D() {
        return me;
    }

    public GeometryObject2DStruct getGeometryObject2DStruct() {
        return meStruct;
    }
    
    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public float getOpacity() {
        return opacity;
    }

    public void setOpacity(float opacity) {
//        for(TransformedGeometryObject2D child : children) {
//            child.setOpacity(opacity);
//        }
        this.opacity = opacity;
    }
    
    public void setParent(TransformedGeometryObject2D parent) {
        this.parent = parent;
    }

    public void addChild(TransformedGeometryObject2D child) {
        addChild(child, children.size());
    }

    public void addChild(TransformedGeometryObject2D child, int layer) {
        boolean is = false;
        for (TransformedGeometryObject2D ch : children) {
            if (ch.getName().equals(child.getName())) {
                is = true;
            }
        }
        if (is) {
            removeChild(child);
        }

        if (layer >= children.size()) {
            children.add(child);
        } else {
            children.insertElementAt(child, layer);
        }
        child.setPanel(this.getPanel());
        child.setParent(this);
    }
    
    public void addChildCentered(TransformedGeometryObject2D child, int layer) {
        addChild(child, layer);
        child.centerToParent();
    }
    
    public void addChildCentered(TransformedGeometryObject2D child) {
        addChild(child);
        child.centerToParent();
    }

    @Override
    public String toString() {
        return name;
        //return me.getName();
    }

    public boolean removeChild(TransformedGeometryObject2D child) {
        child.setPanel(null);
        return children.remove(child);
    }

    public boolean removeChild(int i) {
        if( i < 0 || i >= children.size())
            return false;
        
        children.get(i).setPanel(null);
        return children.remove(children.get(i));
    }
    
    public Vector<TransformedGeometryObject2D> getChildren() {
        return children;
    }
    
    public void removeAllChildren() {
        for (int i = 0; i < children.size(); i++) {
            children.get(i).setPanel(null);            
        }
        children.clear();
    }

    public String getName() {
        return name;
        //return me.getName();
    }

    public void setName(String name) {
        //me.setName(name);
        this.name = name;
    }
    
    public boolean isMyChild(TransformedGeometryObject2D child) {
        return children.contains(child);
    }

    public void moveChildDown(TransformedGeometryObject2D child) {
        if (isMyChild(child)) {
            int index = children.indexOf(child);
            if (index > 0) {
                children.remove(index);
                children.insertElementAt(child, index - 1);
            }
        }
    }

    public void moveChildUp(TransformedGeometryObject2D child) {
        if (isMyChild(child)) {
            int index = children.indexOf(child);
            if (index >= 0 && index < children.size() - 1) {
                children.remove(index);
                children.insertElementAt(child, index + 1);
            }
        }
    }

    public TransformedGeometryObject2D getParent() {
        return parent;
    }
    
    public float getRelativePositionX() {
        if(parent == null) 
            return getAbsolutePositionX();
        
        return getAbsolutePositionX() - parent.getAbsolutePositionX();
    }

    public float getRelativePositionY() {
        if(parent == null) 
            return getAbsolutePositionY();
        
        return getAbsolutePositionY() - parent.getAbsolutePositionY();
    }
    
    public void setRelativePositionX(float xpos) {
        //externalTransform.translate((xpos-externalTransform.getTranslateX())/externalTransform.getScaleX(), 0);        
        if(parent == null) {
            setAbsolutePositionX(xpos);
        } else {
            setAbsolutePositionX(xpos+parent.getAbsolutePositionX());
        }
    }

    public void setRelativePositionY(float ypos) {
        if(parent == null) {
            setAbsolutePositionY(ypos);
        } else {
            setAbsolutePositionY(ypos+parent.getAbsolutePositionY());
        }
    }
 
    public float getAbsolutePositionX() {
        return (float)getAbsoluteTransform().getTranslateX();
    }

    public float getAbsolutePositionY() {
        return (float)getAbsoluteTransform().getTranslateY();
    }

    public void setAbsolutePositionX(float xpos) {
        externalTransform.translate((xpos-getAbsoluteX())/getAbsoluteTransform().getScaleX()*getInternalTransform().getScaleX(), 0);
    }

    public void setAbsolutePositionY(float ypos) {
        externalTransform.translate(0, (ypos-getAbsoluteY())/getAbsoluteTransform().getScaleY()*getInternalTransform().getScaleY());
    }
    
    public float getAbsoluteLeftmostX() {
        if(me == null)
            return 0;
        
        Point2D.Float[] pts = new Point2D.Float[4];
        pts[0] = (Point2D.Float)getAbsoluteTransform().transform(new Point2D.Float(0,0), null);
        pts[1] = (Point2D.Float)getAbsoluteTransform().transform(new Point2D.Float(me.getWidth(),0), null);
        pts[2] = (Point2D.Float)getAbsoluteTransform().transform(new Point2D.Float(me.getWidth(),me.getHeight()), null);
        pts[3] = (Point2D.Float)getAbsoluteTransform().transform(new Point2D.Float(0,me.getHeight()), null);
        float minx = pts[0].x;
        for (int i = 1; i < pts.length; i++) {
            if( pts[i].x < minx ) minx = pts[i].x;
        }
        
        float tmp;
        for (int i = 0; i < children.size(); i++) {
            tmp = children.get(i).getAbsoluteLeftmostX();
            if(tmp < minx) minx = tmp;
        }
        return minx;
    }
    
    public float getRelativeLeftmostX() {
        return getAbsoluteLeftmostX() - (float)getAbsoluteTransform().getTranslateX();
    }

    public float getAbsoluteRightmostX() {
        if(me == null)
            return 0;
        
        Point2D.Float[] pts = new Point2D.Float[4];
        pts[0] = (Point2D.Float)getAbsoluteTransform().transform(new Point2D.Float(0,0), null);
        pts[1] = (Point2D.Float)getAbsoluteTransform().transform(new Point2D.Float(me.getWidth(),0), null);
        pts[2] = (Point2D.Float)getAbsoluteTransform().transform(new Point2D.Float(me.getWidth(),me.getHeight()), null);
        pts[3] = (Point2D.Float)getAbsoluteTransform().transform(new Point2D.Float(0,me.getHeight()), null);
        float maxx = pts[0].x;
        for (int i = 1; i < pts.length; i++) {
            if( pts[i].x > maxx ) maxx = pts[i].x;
        }
        
        float tmp;
        for (int i = 0; i < children.size(); i++) {
            tmp = children.get(i).getAbsoluteRightmostX();
            if(tmp > maxx) maxx = tmp;
        }
        return maxx;
    }
    
    public float getRelativeRightmostX() {
        return getAbsoluteRightmostX() - (float)getAbsoluteTransform().getTranslateX();
    }
    
    public float getAbsoluteTopmostY() {
        if(me == null)
            return 0;
        
        Point2D.Float[] pts = new Point2D.Float[4];
        pts[0] = (Point2D.Float)getAbsoluteTransform().transform(new Point2D.Float(0,0), null);
        pts[1] = (Point2D.Float)getAbsoluteTransform().transform(new Point2D.Float(me.getWidth(),0), null);
        pts[2] = (Point2D.Float)getAbsoluteTransform().transform(new Point2D.Float(me.getWidth(),me.getHeight()), null);
        pts[3] = (Point2D.Float)getAbsoluteTransform().transform(new Point2D.Float(0,me.getHeight()), null);
        float miny = pts[0].y;
        for (int i = 1; i < pts.length; i++) {
            if( pts[i].y < miny ) miny = pts[i].y;
        }
        
        float tmp;
        for (int i = 0; i < children.size(); i++) {
            tmp = children.get(i).getAbsoluteTopmostY();
            if(tmp < miny) miny = tmp;
        }
        return miny;
    }
    
    public float getRelativeTopmostY() {
        return getAbsoluteTopmostY() - (float)getAbsoluteTransform().getTranslateY();
    }

    public float getAbsoluteBottommostY() {
        if(me == null)
            return 0;
        
        Point2D.Float[] pts = new Point2D.Float[4];
        pts[0] = (Point2D.Float)getAbsoluteTransform().transform(new Point2D.Float(0,0), null);
        pts[1] = (Point2D.Float)getAbsoluteTransform().transform(new Point2D.Float(me.getWidth(),0), null);
        pts[2] = (Point2D.Float)getAbsoluteTransform().transform(new Point2D.Float(me.getWidth(),me.getHeight()), null);
        pts[3] = (Point2D.Float)getAbsoluteTransform().transform(new Point2D.Float(0,me.getHeight()), null);
        float maxy = pts[0].y;
        for (int i = 1; i < pts.length; i++) {
            if( pts[i].y > maxy ) maxy = pts[i].y;
        }
        
        float tmp;
        for (int i = 0; i < children.size(); i++) {
            tmp = children.get(i).getAbsoluteBottommostY();
            if(tmp > maxy) maxy = tmp;
        }
        return maxy;
    }
    
    public float getRelativeBottommostY() {
        return getAbsoluteBottommostY() - (float)getAbsoluteTransform().getTranslateY();
    }

    public float getAbsoluteX() {
        Point2D.Float pt = (Point2D.Float)getAbsoluteTransform().transform(new Point2D.Float(0,0), null);
        return pt.x;
    }

    public float getAbsoluteY() {
        Point2D.Float pt = (Point2D.Float)getAbsoluteTransform().transform(new Point2D.Float(0,0), null);
        return pt.y;
    }
    
    public float getWidth() {
        return Math.abs(getAbsoluteRightmostX()-getAbsoluteLeftmostX());
    }
   
    public float getHeight() {
        return Math.abs(getAbsoluteBottommostY()-getAbsoluteTopmostY());
    }
    
    public float getSelfWidth() {
        if(me == null)
            return 0;
        
        Point2D.Float[] pts = new Point2D.Float[4];
        pts[0] = (Point2D.Float)getAbsoluteTransform().transform(new Point2D.Float(0,0), null);
        pts[1] = (Point2D.Float)getAbsoluteTransform().transform(new Point2D.Float(me.getWidth(),0), null);
        pts[2] = (Point2D.Float)getAbsoluteTransform().transform(new Point2D.Float(me.getWidth(),me.getHeight()), null);
        pts[3] = (Point2D.Float)getAbsoluteTransform().transform(new Point2D.Float(0,me.getHeight()), null);
        float maxx = pts[0].x;
        float minx = pts[0].x;
        for (int i = 1; i < pts.length; i++) {
            if( pts[i].x > maxx ) maxx = pts[i].x;
            if( pts[i].x < minx ) minx = pts[i].x;
        }
        return Math.abs(maxx-minx);
    }
    
    public float getSelfHeight() {
        if(me == null)
            return 0;
        
        Point2D.Float[] pts = new Point2D.Float[4];
        pts[0] = (Point2D.Float)getAbsoluteTransform().transform(new Point2D.Float(0,0), null);
        pts[1] = (Point2D.Float)getAbsoluteTransform().transform(new Point2D.Float(me.getWidth(),0), null);
        pts[2] = (Point2D.Float)getAbsoluteTransform().transform(new Point2D.Float(me.getWidth(),me.getHeight()), null);
        pts[3] = (Point2D.Float)getAbsoluteTransform().transform(new Point2D.Float(0,me.getHeight()), null);
        float maxy = pts[0].y;
        float miny = pts[0].y;
        for (int i = 1; i < pts.length; i++) {
            if( pts[i].y > maxy ) maxy = pts[i].y;
            if( pts[i].y < miny ) miny = pts[i].y;
        }
        return Math.abs(maxy-miny);
    }
    
    public void centerToParent() {
        if(parent == null)
            return;
        
        float pw = parent.getSelfWidth();
        float ph = parent.getSelfHeight();
        float px = (float)parent.getAbsoluteTransform().getTranslateX();
        float py = (float)parent.getAbsoluteTransform().getTranslateY();
        float mx2 = getAbsoluteRightmostX();
        float mx1 = getAbsoluteLeftmostX();
        float my2 = getAbsoluteBottommostY();
        float my1 = getAbsoluteTopmostY();
        float mx = getAbsoluteX();
        float my = getAbsoluteY();
    
        float tmpx = (pw - (mx2 - mx1))/2.0f + mx - mx1;
        float tmpy = (ph - (my2 - my1))/2.0f + my - my1;
        
        externalTransform.translate( -externalTransform.getTranslateX()/externalTransform.getScaleX(),  -externalTransform.getTranslateY()/externalTransform.getScaleY());
        externalTransform.translate(
                Math.floor(tmpx/getAbsoluteTransform().getScaleX() - getInternalTransform().getTranslateX()),
                Math.floor(tmpy/getAbsoluteTransform().getScaleY() - getInternalTransform().getTranslateY())
                );        
    }

    public void xCenterToParent() {
        if(parent == null)
            return;
        
        float pw = parent.getSelfWidth();
        float px = (float)parent.getAbsoluteTransform().getTranslateX();
        float mx2 = getAbsoluteRightmostX();
        float mx1 = getAbsoluteLeftmostX();
        float mx = getAbsoluteX();
        
        float tmpx = (pw - (mx2 - mx1))/2.0f + mx - mx1;
        
        externalTransform.translate( -externalTransform.getTranslateX()/externalTransform.getScaleX(),  0);
        externalTransform.translate(
                Math.floor(tmpx/getAbsoluteTransform().getScaleX() - getInternalTransform().getTranslateX()),
                0
                );        
    }
    
    public void yCenterToParent() {
        if(parent == null)
            return;
        
        float ph = parent.getSelfHeight();
        float py = (float)parent.getAbsoluteTransform().getTranslateY();
        float my2 = getAbsoluteBottommostY();
        float my1 = getAbsoluteTopmostY();
        float my = getAbsoluteY();
        
        float tmpy = (ph - (my2 - my1))/2.0f + my - my1;
        
        externalTransform.translate( 0,  -externalTransform.getTranslateY()/externalTransform.getScaleY());
        externalTransform.translate(
                0,
                Math.floor(tmpy/getAbsoluteTransform().getScaleY() - getInternalTransform().getTranslateY())
                );        
    }
    
    public void centerTo(TransformedGeometryObject2D root0) {
        if(root0 == null)
            return;
        
        float pw = root0.getSelfWidth();
        float ph = root0.getSelfHeight();
        float px = (float)root0.getAbsoluteTransform().getTranslateX();
        float py = (float)root0.getAbsoluteTransform().getTranslateY();
        float mx2 = getAbsoluteRightmostX();
        float mx1 = getAbsoluteLeftmostX();
        float my2 = getAbsoluteBottommostY();
        float my1 = getAbsoluteTopmostY();
        float mx = getAbsoluteX();
        float my = getAbsoluteY();

        float tmpx = (float)Math.floor((pw - (mx2 - mx1))/2.0f + mx - mx1);
        float tmpy = (float)Math.floor((ph - (my2 - my1))/2.0f + my - my1);

        setAbsolutePositionX(tmpx);
        setAbsolutePositionY(tmpy);
    }

    public void xCenterTo(TransformedGeometryObject2D root0) {
        if(root0 == null)
            return;
        
        float pw = root0.getSelfWidth();
        float px = (float)root0.getAbsoluteTransform().getTranslateX();
        float mx2 = getAbsoluteRightmostX();
        float mx1 = getAbsoluteLeftmostX();
        float mx = getAbsoluteX();

        float tmpx = (float)Math.floor((pw - (mx2 - mx1))/2.0f + mx - mx1);

        setAbsolutePositionX(tmpx);
    }

    public void yCenterTo(TransformedGeometryObject2D root0) {
        if(root0 == null)
            return;
        
        float ph = root0.getSelfHeight();
        float py = (float)root0.getAbsoluteTransform().getTranslateY();
        float my2 = getAbsoluteBottommostY();
        float my1 = getAbsoluteTopmostY();
        float my = getAbsoluteY();

        float tmpy = (float)Math.floor((ph - (my2 - my1))/2.0f + my - my1);

        setAbsolutePositionY(tmpy);
    }
    
    public void normalizeToParent() {
        if(parent == null)
            return;
        
        if(getWidth() < 2 || getHeight() < 2) 
            return;

        float pw,ph,ow,oh,ssx,ssy,ss;            
        pw = parent.getSelfWidth()*0.9f;
        ph = parent.getSelfHeight()*0.9f;
        
        ow = getWidth();
        oh = getHeight();

        ssx = pw/ow;
        ssy = ph/oh;
        if(ssx < ssy) {
            ss = ssx;
        } else {
            ss = ssy;
        }
        
        externalTransform.scale(ss, ss);        
    }

    public String getParentModulePort() {
        return parentModulePort;
    }

    public void setParentModulePort(String parentModulePort) {
        this.parentModulePort = parentModulePort;
    }
    
    public void internalize(){
        AffineTransform tmp = new AffineTransform();
        tmp.concatenate(externalTransform);
        tmp.concatenate(me.getInternalTransform());
        me.setInternalTransform((AffineTransform)tmp.clone());
        externalTransform = new AffineTransform();
        for (int i = 0; i < children.size(); i++) {
            tmp = new AffineTransform();
            tmp.concatenate(children.get(i).getExternalTransform());
            tmp.concatenate(children.get(i).getGeometryObject2D().getInternalTransform());
            children.get(i).getGeometryObject2D().setInternalTransform((AffineTransform)tmp.clone());
            children.get(i).setExternalTransform(new AffineTransform());
        }
    }

    public float getScaleX() {
        return (float)getAbsoluteTransform().getScaleX();        
    }
    
    public float getScaleY() {
        return (float)getAbsoluteTransform().getScaleY();        
    }

    
    public void setScaleX(float sx) {
        externalTransform.scale(sx/getAbsoluteTransform().getScaleX(), 1);
    }
    
    public void setScaleY(float sy) {
        externalTransform.scale(1, sy/getAbsoluteTransform().getScaleY());
    }
    
    public void setScale(float sx, float sy) {
        externalTransform.scale(sx/getAbsoluteTransform().getScaleX(), sy/getAbsoluteTransform().getScaleY());
    }

    public TransformedGeometryObject2D getChiltAt(float x, float y) {
        if(!isPointInsideBox(x, y))
            return null;

        for (int i = children.size()-1; i >= 0; i--) {
            if(children.get(i).isPointInsideBox(x, y)) {
                return children.get(i).getChiltAt(x, y);
            }
        }

        return this;
    }


    public boolean isPointInsideBox(float x, float y) {
        float left = getAbsoluteLeftmostX();
        float right = getAbsoluteRightmostX();
        float top = getAbsoluteTopmostY();
        float bottom = getAbsoluteBottommostY();


        return (x >= left && x <= right && y >= top && y <= bottom);
    }


    public int[] getLocalCoords(float x, float y) {
        if(!isPointInsideBox(x, y))
            return null;


        float lx = x - getAbsolutePositionX();
        float ly = this.getHeight() - (y - getAbsolutePositionY());
        
        float sx = (float)getAbsoluteTransform().getScaleX();
        float sy = (float)getAbsoluteTransform().getScaleY();
        
        int[] out = new int[2];
        out[0] = (int)Math.floor(lx/sx);        
        out[1] = (int)Math.floor(ly/sy);
        return out;
    }




    
//    public float getRelativeRotation() {        
//        double[] m = new double[6];
//        //externalTransform.getMatrix(m);
//        getExtIntTransform().getMatrix(m);
//        double rot = Math.atan2(-m[2], m[0]);
//        double rot2 = 360.0*rot/(2.0*Math.PI);
//        return (float)rot2;
//    }
//    
//    public void setRelativeRotation(float rot) {
//        double[] m = new double[6];
//        getInternalTransform().getMatrix(m);
//        double rot1 = Math.atan2(-m[2], m[0]);
//        float rot2 = (float)(360.0*rot1/(2.0*Math.PI));
//        
//        float rot3 = 2.0f*(float)Math.PI*(rot-getRelativeRotation())/360.0f;
//        externalTransform.rotate(rot3);
//    }
    

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
        me.setPanel(panel);
    }
        
}
