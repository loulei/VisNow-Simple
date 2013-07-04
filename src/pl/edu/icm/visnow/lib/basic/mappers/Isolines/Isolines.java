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

package pl.edu.icm.visnow.lib.basic.mappers.Isolines;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.Vector;
import javax.media.j3d.LineAttributes;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.Color3f;
import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.datasets.IrregularField;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.engine.core.InputEgg;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.geometries.parameters.DataMappingParams;
import pl.edu.icm.visnow.geometries.utils.ColorMapper;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.render.RenderEvent;
import pl.edu.icm.visnow.geometries.viewer3d.eventslisteners.render.RenderEventListener;
import pl.edu.icm.visnow.lib.templates.visualization.modules.IrregularOutField1DVisualizationModule;
import pl.edu.icm.visnow.lib.types.VNField;
import pl.edu.icm.visnow.lib.types.VNIrregularField;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;
import pl.edu.icm.visnow.lib.utils.geometry2D.GeometryObject2D;
import pl.edu.icm.visnow.lib.utils.geometry2D.GeometryObject2DStruct;

/**
 * @author Krzysztof S. Nowinski (know@icm.edu.pl) Warsaw University,
 * Interdisciplinary Centre for Mathematical and Computational Modelling
 */
public class Isolines extends IrregularOutField1DVisualizationModule {

    /**
     * Creates a new instance of CreateGrid
     */
    public static InputEgg[] inputEggs = null;
    public static OutputEgg[] outputEggs = null;
    protected VNField input = null;
    protected RegularFieldIsolines regularFieldIsolines = null;
    protected IrregularFieldIsolines irregularFieldIsolines = null;
    protected IsolinesGUI computeUI = null;
    protected Field inField = null;
    protected Isolines2D out2D;
    protected int component = -1;
    protected boolean fromUI = false;
    protected boolean ignoreUI = false;
    protected IsolinesParams params;
    protected int[]  size;

    public Isolines() {
        parameters = params = new IsolinesParams();
        params.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent evt) {
                if (ignoreUI) {
                    return;
                }
                fromUI = true;
                startAction();
            }
        });
        SwingInstancer.swingRun(new Runnable() {
            public void run() {
                computeUI = new IsolinesGUI();
                computeUI.setParams(params);
                ui.addComputeGUI(computeUI);
                setPanel(ui);
            }
        });
        outObj.setName("isolines");
    }

    @Override
    public void onDelete() {
        detach();
        ((Container) getPanel()).removeAll();
        ui = null;
        regularFieldIsolines = null;
        out2D = null;
    }

    public class Isolines2D extends GeometryObject2D implements Cloneable {

        private Vector<float[]>[] data = null;
        private float[][] coords = null;
        private Color[] colors = null;
        private float lineWidth = 1;
        private int lineStyle = LineAttributes.PATTERN_SOLID;

        public Isolines2D(String name) {
            super(name);
        }

        public Isolines2D(String name, Vector<float[]>[] data, int[] dims) {
            super(name);
            if (regularFieldIsolines != null) {
                setData(data, dims);
            }
        }

        public Isolines2D(String name, float[][] coords, int nSpace) {
            super(name);
            if (regularFieldIsolines == null || nSpace != 2) {
                return;
            }
            this.coords = coords;
        }

        public void setColors(Color[] colors) {
            this.colors = colors;
        }

        public void setLineWidth(float lineWidth) {
            this.lineWidth = lineWidth;
        }
        
        private void setLineStyle(int lineStyle) {
            this.lineStyle = lineStyle;
        }

        public final void setData(Vector<float[]>[] data, int[] dims) {
            this.data = data;
            this.width = dims[0];
            this.height = dims[1];
        }

        public int getDataSize() {
            if(data != null)
                return this.data.length;
            return 0;
        }
        
        @Override
        public void drawLocal2D(Graphics2D g, AffineTransform at) {
            if (regularFieldIsolines == null || colors == null) {
                return;
            }
            
            switch(lineStyle) {
                case LineAttributes.PATTERN_DASH:
                    g.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 2.0f, new float[]{10.0f, 5.0f}, 0.0f));
                    break;
                case LineAttributes.PATTERN_DASH_DOT:
                    g.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 2.0f, new float[]{10.0f, 5.0f, lineWidth, 5.0f}, 0.0f));
                    break;
                case LineAttributes.PATTERN_DOT:
                    g.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 2.0f, new float[]{lineWidth, 5.0f}, 0.0f));
                    break;
                default:
                    g.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 2.0f));
                    break;                    
            }
            
            g.translate(at.getTranslateX(), at.getTranslateY());
            if (data == null) {
                if (coords == null) {
                    g.translate(-at.getTranslateX(), -at.getTranslateY());
                    return;
                }
                for (int i = 0; i < coords.length; i++) {
                    g.setColor(colors[i]);
                    for (int j = 0; j < coords[i].length; j += 4) {
                        g.drawLine((int) ((coords[i][j]+0.5) * at.getScaleX()), (int) ((height - coords[i][j + 1] - 0.5) * at.getScaleY()),
                                (int) ((coords[i][j + 2] + 0.5) * at.getScaleX()), (int) ((height - coords[i][j + 3] - 0.5) * at.getScaleY()));
                    }
                }
                g.translate(-at.getTranslateX(), -at.getTranslateY());
                return;
            }

            for (int n = 0; n < data.length; n++) {
                g.setColor(colors[n]);
                Vector<float[]> vdata = data[n];
                for (int j = 0; j < vdata.size(); j++) {
                    float[] line = vdata.get(j);
                    int m = line.length / 2;
                    int[] ix = new int[m];
                    int[] iy = new int[m];
                    for (int i = 0; i < m; i++) {
                        ix[i] = (int) ((line[2 * i] + 0.5) * at.getScaleX());
                        iy[i] = (int) ((height - line[2 * i + 1] - 0.5) * at.getScaleY());
                    }
                    g.drawPolyline(ix, iy, m);
                }
            }
            g.translate(-at.getTranslateX(), -at.getTranslateY());
        }

    }

    public void updateDisplay2DParams() {
        if (regularFieldIsolines == null || regularFieldIsolines.getLines() == null || regularFieldIsolines.getLines().length < 1 || irregularFieldGeometry == null) {
            return;
        }
        
        Color[] colors = new Color[regularFieldIsolines.getLines().length];        
        if(irregularFieldGeometry.getCellSetGeometry(0).getDataMappingParams().getColorMap0Params().getDataComponent() < 0) {
            Color3f c = irregularFieldGeometry.getCellSetGeometry(0).getParams().getRenderingParams().getAmbientColor();
            for (int i = 0; i < colors.length; i++) {
                colors[i] = new Color(c.getX(), c.getY(), c.getZ());
            }            
        } else {        
            int[] colormapLUT = irregularFieldGeometry.getCellSetGeometry(0).getDataMappingParams().getColorMap0Params().getMap().getRGBColorTable();
            float low = irregularFieldGeometry.getCellSetGeometry(0).getDataMappingParams().getColorMap0Params().getDataMin();
            float up = irregularFieldGeometry.getCellSetGeometry(0).getDataMappingParams().getColorMap0Params().getDataMax();        
            float[] thresholds = params.getThresholds();
            float s = (float) (colormapLUT.length-1) / (up - low);        
            for (int i = 0; i < colors.length; i++) {
                colors[i] = new Color(colormapLUT[Math.max(0, Math.min((int) ((thresholds[i] - low) * s), colormapLUT.length-1))]);
            }
        }
        out2D.setColors(colors);

        out2D.setLineWidth(irregularFieldGeometry.getCellSetGeometry(0).getParams().getRenderingParams().getLineThickness());
        out2D.setLineStyle(irregularFieldGeometry.getCellSetGeometry(0).getParams().getRenderingParams().getLineStyle());        
        out2D.setName("Isolines");
    }

    public void update(RegularField inField) {
        regularFieldIsolines = new RegularFieldIsolines(inField, params);
        this.size = ((RegularField)inField).getDims();
        irregularFieldIsolines = null;
        outField = regularFieldIsolines.getOutField();        
        out2D = null;
        if (regularFieldIsolines != null) {
            out2D = new Isolines2D("isolines");
            out2D.setData(regularFieldIsolines.getLines(), size);
        }
    }

    public void update(IrregularField inField) {
        irregularFieldIsolines = new IrregularFieldIsolines(inField, params);
        regularFieldIsolines = null;
        outField = irregularFieldIsolines.getOutField();
        out2D = null;
    }

    public void update() {
        DataArray da = inField.getData(params.getComponent());
        if (da.getMaxv() < da.getMinv() + 1e-6f) {
            return;
        }
        if (inField instanceof RegularField) {
            update((RegularField) inField);
        } else if (inField instanceof IrregularField) {
            update((IrregularField) inField);
        } else {
            return;
        }
        if (outField == null || outField.getNNodes() < 2) {
            outObj.clearAllGeometry();
            return;
        }
        outGroup = null;
        outField.setExtents(inField.getExtents());
        prepareOutputGeometry();
        setOutputValue("outField", new VNIrregularField(outField));
        irregularFieldGeometry.getDataMappingParams().getTransparencyParams().setComponent(-1);
        irregularFieldGeometry.getColormapLegend(0).setThrTable(params.getThresholds());
        show();
    }

    @Override
    protected void show() {
        if (irregularFieldGeometry == null) {
            return;
        }
        outObj.clearGeometries2D();
        irregularFieldGeometry.updateGeometry();
        for (int i = 0; i < outField.getNCellSets(); i++) {
            outObj.addGeometry2D(irregularFieldGeometry.getColormapLegend(i));
        }
        outObj.setExtents(outField.getExtents());
    }

    private RenderEventListener renderEventListener = new RenderEventListener() {
        @Override
        public void renderExtentChanged(RenderEvent e) {
            updateDisplay2DParams();
        }
    };
    
    @Override
    protected void prepareOutputGeometry() {
        if (outField == null) {
            return;
        }
        boolean newParams = irregularFieldGeometry == null
                || irregularFieldGeometry.getField() == null
                || !outField.isDataCompatibleWith(irregularFieldGeometry.getField());
        irregularFieldGeometry.setField(outField);
        irregularFieldGeometry.setIgnoreUpdate(true);

        if (newParams) {
            fieldDisplayParams = irregularFieldGeometry.getFieldDisplayParams();
            SwingInstancer.swingRun(new Runnable() {
                public void run() {
                    ui.getPresentation1DPanel().setInFieldDisplayData(outField, fieldDisplayParams);
                }
            });
            fieldDisplayParams.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent evt) {
                    show();
                }
            });
            irregularFieldGeometry.getCellSetGeometry(0).getParams().getDataMappingParams().addRenderEventListener(renderEventListener);
            irregularFieldGeometry.getCellSetGeometry(0).getParams().getRenderingParams().addRenderEventListener(renderEventListener);
            irregularFieldGeometry.getGeometryObj2DStruct().removeAllChildren();
            GeometryObject2DStruct isolinesStruct = new GeometryObject2DStruct(out2D);
            irregularFieldGeometry.getGeometryObj2DStruct().addChild(isolinesStruct);            
        }
        outObj.clearAllGeometry();
        outGroup = irregularFieldGeometry.getGeometry();
        irregularFieldGeometry.setIgnoreUpdate(false);
        outObj.addNode(outGroup);
        updateDisplay2DParams();
    }

    private void updateUI() {
        ignoreUI = true;
        computeUI.setInField(input);
        ignoreUI = false;
    }

    @Override
    public void onActive() {
        if (getInputFirstValue("inField") == null
                || ((VNField) getInputFirstValue("inField")).getField() == null) {
            return;
        }

        if (!fromUI) {
            outObj.clearAllGeometry();
            outGroup = null;
            input = ((VNField) getInputFirstValue("inField"));
            Field newInField = input.getField();

            if (newInField != null && (inField == null || !inField.isDataCompatibleWith(newInField))) {
                component = -1;
                for (int i = 0; i < newInField.getNData(); i++) {
                    if (newInField.getData(i).getVeclen() == 1) {
                        component = i;
                        break;
                    }
                }
                if (component == -1) {
                    return;
                }
                inField = newInField;
                outObj.setName(inField.getName());
            }
            inField = newInField;
            updateUI();
        }
        update();
        fromUI = false;
    }
}
