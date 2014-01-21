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
package pl.edu.icm.visnow.lib.basic.readers.ReadSTL;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.j3d.loaders.stl.STLFileReader;
import pl.edu.icm.visnow.datasets.CellArray;
import pl.edu.icm.visnow.datasets.CellSet;
import pl.edu.icm.visnow.datasets.IrregularField;
import pl.edu.icm.visnow.datasets.cells.Cell;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.engine.core.InputEgg;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.lib.templates.visualization.modules.IrregularOutFieldVisualizationModule;
import pl.edu.icm.visnow.lib.types.VNIrregularField;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;

/**
 * @author theki
 */
public class ReadSTL extends IrregularOutFieldVisualizationModule {

    public static InputEgg[] inputEggs = null;
    public static OutputEgg[] outputEggs = null;
    private GUI computeUI;
    private Params params;

    /**
     * Creates a new instance of Convolution
     */
    public ReadSTL() {
        parameters = params = new Params();
        params.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                startAction();
            }
        });
        SwingInstancer.swingRunAndWait(new Runnable() {
            @Override
            public void run() {
                computeUI = new GUI();
                computeUI.setParams(params);
                ui.addComputeGUI(computeUI);
                setPanel(ui);
            }
        });
    }

    @Override
    public void onActive() {
        computeUI.setInfoText("");

        String path = params.getFilePath();
        if (path == null) {
            return;
        }

        outField = readSTL(params.getFilePath());
        if(outField != null)
            computeUI.setInfoText(outField.description());
        else
            computeUI.setInfoText("ERROR reading STL file");
        
        setOutputValue("outField", new VNIrregularField(outField));
        prepareOutputGeometry();
        show();

    }

    @Override
    public void onInitFinishedLocal() {
        if (isForceFlag()) {
            computeUI.activateOpenDialog();
        }
    }
    
   @Override
   public boolean isGenerator() {
      return true;
   }    

    public static IrregularField readSTL(String path) {
        if (path == null) {
            return null;
        }
        try {
            IrregularField out = null;
            File f = new File(path);
            if (!f.exists() || !f.canRead()) {
                return null;
            }


            //System.out.println("STL loader not implemented");
            STLFileReader reader = new STLFileReader(f);
            double[] normal = new double[3];
            double[][] vertices = new double[3][3];

            int nObjects = reader.getNumOfObjects();
            String[] objectNames = reader.getObjectNames();
            int[] nFacets = reader.getNumOfFacets();
            int[][] cellsIndices = new int[nObjects][];
            HashMap<Node, Integer> vertexMap = new HashMap<Node, Integer>();

            int nodeCounter = 0;
            for (int n = 0; n < nObjects; n++) {
                cellsIndices[n] = new int[3 * nFacets[n]];
                for (int cellIndex = 0; cellIndex < nFacets[n]; cellIndex++) {
                    if (!reader.getNextFacet(normal, vertices)) {
                        throw new Exception("ERROR: not enough facets in STL file.");
                    }
                    
                    for (int i = 0; i < 3; i++) {
                        float[] p = new float[3];
                        for (int j = 0; j < p.length; j++) {
                            p[j] = (float) vertices[i][j];                            
                        }
                        Node node = new Node(p);
                        if(!vertexMap.containsKey(node)) {
                            vertexMap.put(node, nodeCounter);
                            cellsIndices[n][3*cellIndex + i] =  nodeCounter;
                            nodeCounter++;
                        } else {
                            cellsIndices[n][3*cellIndex + i] =  vertexMap.get(node);
                        }
                    }
                }
            }
            reader.close();
            
            Set<Node> nodeSet = vertexMap.keySet();
            int nNodes = vertexMap.size();
            float[] coords = new float[3 * nNodes];
            Iterator<Node> nodeIterator = nodeSet.iterator();
            int i;
            while(nodeIterator.hasNext()) {
                Node n = nodeIterator.next();
                i = vertexMap.get(n);
                System.arraycopy(n.getCoords(), 0, coords, 3 * i, 3);
            }
            vertexMap.clear();
            
            vertexMap = null;
            Runtime.getRuntime().gc();
            
            out = new IrregularField(nNodes);
            out.setNSpace(3);
            out.setCoords(coords);
            byte[] dummy = new byte[nNodes];
            out.addData(DataArray.create(dummy, 1, "dummy"));
            for (int n = 0; n < nObjects; n++) {
                CellArray ca = new CellArray(Cell.TRIANGLE, cellsIndices[n], null, null);
                CellSet cs = new CellSet(objectNames[n]);
                cs.setCellArray(ca);
                out.addCellSet(cs);
                cs.generateDisplayData(coords);
            }
            return out;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static class Node {
        float[] p;
        
        public Node(float[] p) {
            this.p = p;
        }
        
        public float[] getCoords() {
            return p;
        }

        @Override
        public int hashCode() {
            return java.util.Arrays.hashCode(p);            
        }
        
        @Override
        public boolean equals(Object o) {
            if(o == null || p == null)
                return false;
            
            if(!(o instanceof Node))
                return false;
            
            Node on = (Node)o;
            float[] op = on.getCoords();
            if(p.length != op.length)
                return false;
            
            for (int i = 0; i < p.length; i++) {
                if(p[i] != op[i])
                    return false;
            }
            
            return true;
        }
        
        
    }
}
