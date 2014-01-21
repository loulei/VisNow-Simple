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

package pl.edu.icm.visnow.lib.basic.readers.ReadOBJ;

import com.sun.j3d.loaders.IncorrectFormatException;
import com.sun.j3d.loaders.ParsingErrorException;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.geometry.GeometryInfo;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.IndexedTriangleArray;
import javax.media.j3d.Node;
import javax.media.j3d.Shape3D;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pl.edu.icm.visnow.datasets.CellArray;
import pl.edu.icm.visnow.datasets.CellSet;
import pl.edu.icm.visnow.datasets.IrregularField;
import pl.edu.icm.visnow.datasets.cells.Cell;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.engine.core.InputEgg;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.geometries.parameters.AbstractRenderingParams;
import pl.edu.icm.visnow.lib.templates.visualization.modules.IrregularOutFieldVisualizationModule;
import pl.edu.icm.visnow.lib.types.VNIrregularField;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;

/**
 * @author theki
 */
public class ReadOBJ extends IrregularOutFieldVisualizationModule
{

   public static InputEgg[] inputEggs = null;
   public static OutputEgg[] outputEggs = null;
   private GUI computeUI;
   private Params params;

   /**
    * Creates a new instance of Convolution
    */
   public ReadOBJ()
   {
      parameters = params = new Params();
      params.addChangeListener(new ChangeListener()
      {

         public void stateChanged(ChangeEvent e)
         {
            startAction();
         }
      });
      SwingInstancer.swingRunAndWait(new Runnable()
      {

         public void run()
         {
            computeUI = new GUI();
            computeUI.setParams(params);
            ui.addComputeGUI(computeUI);
            setPanel(ui);
         }
      });
   }

   @Override
   public void onActive()
   {

      String path = params.getObjPath();
      if (path == null)
      {
         System.out.println("path == null");
         return;
      }

      ObjectFile a = new ObjectFile();
      a.setFlags(ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY);

      Scene s = null;

      try
      {
         s = a.load(path);
      } catch (FileNotFoundException ex)
      {
         Logger.getLogger(ReadOBJ.class.getName()).log(Level.SEVERE, null, ex);
         return;
      } catch (IncorrectFormatException ex)
      {
         Logger.getLogger(ReadOBJ.class.getName()).log(Level.SEVERE, null, ex);
         return;
      } catch (ParsingErrorException ex)
      {
         Logger.getLogger(ReadOBJ.class.getName()).log(Level.SEVERE, null, ex);
         return;
      }

      if (s == null)
         return;
      
      GeometryInfo geomInfo = new GeometryInfo((GeometryArray)((Shape3D)s.getSceneGroup().getChild(0)).getGeometry());
      geomInfo.convertToIndexedTriangles();
      IndexedTriangleArray ita = (IndexedTriangleArray) geomInfo.getIndexedGeometryArray();
//      ita.setCapability(IndexedTriangleArray.ALLOW_COORDINATE_INDEX_READ);
//      ita.setCapability(IndexedTriangleArray.ALLOW_COORDINATE_READ);
      
      int n = ita.getVertexCount();
      outField = new IrregularField(n);
      outField.setNSpace(3);
      int count = ita.getIndexCount();
      int[] cells = new int[count];
      float[] coord = new float[n * 3];
      boolean[] orientations = new boolean[count / 3];
      for (int i = 0; i < orientations.length; i++)
         orientations[i] = true;
      ita.getCoordinates(0, coord);
      ita.getCoordinateIndices(0, cells);
      outField.setCoords(coord);
      CellArray ca = new CellArray(Cell.TRIANGLE, cells, orientations, null);
      CellSet cs = new CellSet("cells");
      cs.setCellArray(ca);
      //cs.generateExternFaces();
      cs.generateDisplayData(coord);
      outField.addCellSet(cs);
      float[] temp1 = new float[n];
      for (int i = 0; i < n; i++)
         temp1[i] = i;
      outField.addData(DataArray.create(temp1, 1, "dummy"));
      setOutputValue("outField", new VNIrregularField(outField));
      prepareOutputGeometry();
      irregularFieldGeometry.getFieldDisplayParams().setShadingMode(AbstractRenderingParams.FLAT_SHADED);
      show();

   }
   
   @Override
   public void onInitFinishedLocal() {
       if(isForceFlag()) 
           computeUI.activateOpenDialog();
   }
   
   @Override
   public boolean isGenerator() {
      return true;
   }
   
}
