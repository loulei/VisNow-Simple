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

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.geometries.parameters.AbstractRenderingParams;
import pl.edu.icm.visnow.geometries.parameters.RenderingParams;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.render.RenderEvent;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.render.RenderEventListener;

/**
 * @author Bartosz Borucki (babor@icm.edu.pl) Warsaw University,
 * Interdisciplinary Centre for Mathematical and Computational Modelling
 */
public abstract class Array2D extends GeometryObject2D implements Cloneable {

    protected RegularField field = null;
    protected byte[] colors = null;
    protected BasicStroke stroke = new BasicStroke(); 
    protected boolean antialiasing = true;
    protected AbstractRenderingParams renderingParams = new RenderingParams();
    
    protected RenderEventListener listener = new RenderEventListener() {

        @Override
        public void renderExtentChanged(RenderEvent e) {
            float lineWidth = renderingParams.getLineAppearance().getLineAttributes().getLineWidth();
            antialiasing = renderingParams.getLineAppearance().getLineAttributes().getLineAntialiasingEnable();

            stroke = new BasicStroke(lineWidth, 
                    BasicStroke.CAP_ROUND, 
                    BasicStroke.JOIN_ROUND);   
            fireStateChanged();                    
        }
    };

    public Array2D(RegularField field, String name) {
        this.name = name;
        updateFieldParams(field);
        this.renderingParams.addRenderEventListener(listener);                
    }
    
    public void setColors(byte[] colors) {
        if(colors != null && colors.length == 4*width*height) {
            this.colors = colors;
            fireStateChanged();
        }
    }
    
    public byte[] getColors() {
        return colors;
    }
    
    
    public void setRenderingParams(AbstractRenderingParams renderingParams) {
        if(renderingParams == this.renderingParams)
            return;
        
        if(this.renderingParams != null)
            this.renderingParams.removeRenderEventListener(listener);
        
        renderingParams.addRenderEventListener(listener);
        this.renderingParams = renderingParams;
    }
    

    /**
     * Clears render event listeners; this has to be done to remove all references to this object.
     */
    public void clearParamListeners() {
        if(this.renderingParams != null)
            this.renderingParams.removeRenderEventListener(listener);
    }    
    
    public void setField(RegularField field) {
        updateFieldParams(field);
        fireStateChanged();
    }

    public RegularField getField() {
        return field;
    }

    @Override
    public abstract void drawLocal2D(Graphics2D g, AffineTransform tr);

    @Override
    public Object clone() throws CloneNotSupportedException {
        Array2D out = (Array2D) super.clone();
        if (field != null) {
            out.setField(field);
        }
        return out;
    }

    private void updateFieldParams(RegularField field) {
        if (field == null || field.getDims().length != 2) {
            this.field = null;
            this.width = 0;
            this.height = 0;
            internalTransform = new AffineTransform();
        } else {
            this.field = field;
            this.width = field.getDims()[0];
            this.height = field.getDims()[1];
            if(field.getCoords() != null) {
                internalTransform = new AffineTransform();
                //TODO someday
            } else {
                float[][] affine = field.getAffine();
                double[] affineNorms = field.getAffineNorm();
                float[] flatmatrix = new float[] {1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f};
                float[] v = new float[2];
                if(affineNorms[0] == 0) {                    
                    flatmatrix[3] = (float) (affineNorms[2]/affineNorms[1]);
                } else if(affineNorms[1] == 0) {
                    flatmatrix[3] = (float) (affineNorms[2]/affineNorms[0]);
                } else if(affineNorms[2] == 0) {
                    flatmatrix[3] = (float) (affineNorms[1]/affineNorms[0]);
                }
                internalTransform = new AffineTransform(flatmatrix);
            }
            
            
            
            
        }        
        
        
    }

    private double vectorsAngle(float[] v1, float[] v2) {
        double len;
        double angle;
        len = Math.sqrt(v1[0]*v1[0] + v1[1]*v1[1] + v1[2]*v1[2])*Math.sqrt(v2[0]*v2[0] + v2[1]*v2[1] + v2[2]*v2[2]);
        if(len == 0) {
            angle = 0;
        } else {
            angle = Math.acos((v1[0]*v2[0] + v1[1]*v2[1] + v1[2]*v2[2])/len);
        }
        return angle;        
    }
    
}
