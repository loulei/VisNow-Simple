//<editor-fold defaultstate="collapsed" desc=" COPYRIGHT AND LICENSE ">
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
//</editor-fold>

package pl.edu.icm.visnow.datasets;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.log4j.Logger;
import pl.edu.icm.visnow.datasets.cells.Tetra;
import pl.edu.icm.visnow.datasets.cells.SimplexPosition;
import pl.edu.icm.visnow.datasets.cells.Triangle;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;
import pl.edu.icm.visnow.lib.utils.RabinHashFunction;
import pl.edu.icm.visnow.lib.utils.numeric.NumericalMethods;

/**
 *
 * @author  Krzysztof S. Nowinski, University of Warsaw, ICM
 */
public abstract class Field  implements Serializable, DataContainer
{
    private static final Logger LOGGER = Logger.getLogger(Field.class);
   public static final int GENERAL        = 0;
   public static final int REGULAR        = 1;
   public static final int IRREGULAR      = 2;
   public static final int UNKNOWN        = -1;
   public static final int TIME_DATA_OK = 0;
   public static final int TIME_BEFORE_FIRST = 1;
   public static final int TIME_AFTER_LAST = 2;
   public static final int TIME_ENTRIES_BAD = -1;
   protected String name                  = " ";
   protected int nNodes                   = 0;
   protected int  nSpace                  = 0;
   protected FieldSchema schema;
   protected float[][] extents            = {{Float.MAX_VALUE,Float.MAX_VALUE,Float.MAX_VALUE},{-Float.MAX_VALUE,-Float.MAX_VALUE,-Float.MAX_VALUE}};
   protected float[][] physExts           = {{-.5f,-.5f,-.5f},{.5f,.5f,.5f}};
   protected TimeData<float[]> timeCoords = null;
   protected TimeData<boolean[]> timeMask = null;
   protected String timeUnit              = "";
   protected int lastValidFrame           = 0;
//   protected int currentFrame             = 0;
   protected float currentTime            = 0;
   protected float[] coords;
   protected boolean[] mask               = null;
   protected boolean[] transparencyMask   = null;
   protected long coordsTimestamp         = 0;
   protected long timestamp               = 0;
   protected long structTimestamp         = 0;
   protected long dataTimestamp           = 0;
   protected long coordsHash              = 0;
   protected long dataHash                = 0;
   protected GeoTreeNode geoTree          = null;
   protected float[][] cellExtents        = null;
   protected String[] axesNames           = null;
   protected ArrayList<List> timesteps       = null;
   protected ArrayList<DataArray> data       = new ArrayList<DataArray>();
   /**
    * trueDim <gt> 0 means that the field has cells of dimensions trueDim and is contained in
    * the x1,...,x trueDim subspace.
    *
    */
   protected int trueDim = -1;

   /** Creates a new instance of Field */
   public Field()
   {
      schema = new FieldSchema();
      timestamp = System.currentTimeMillis();
   }

   public String description()
   {
      StringBuffer s = new StringBuffer();
      s.append(name+": "+nNodes+" nodes,"+getNFrames()+" time frames\n");
      if (timeCoords == null)
         s.append("Field with implicit coords, "+nNodes+" nodes<p>");
      else
         s.append("Field with explicit coords, "+nNodes+" nodes<p>");
      s.append("Components:<p>");
      for (int i = 0; i < getNData(); i++)
         s.append("<p>"+getData(i).toString());
      return "<html>"+s+"</html>";
   }

   abstract public String shortDescription();

   /**
    * Used for easy check of field regularity type
    * @return field type - one of
    * <pre>
    * GENERAL(returning values at point),
    * REGULAR (structurally ans one- two- or threedimensional box),
    * IRREGULAR (with explicit cells grouped in  cell sets)
    * UNKNOWN
    * </pre>
    */
   abstract public int getType();

   @Override
   abstract public Field clone();
   abstract public Field cloneDeep();
   abstract public boolean isStructureCompatibleWith(Field f);
   abstract public IrregularField triangulate();

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * Getter for property schema.
    * @return Value of property schema.
    */
   public FieldSchema getSchema()
   {
      return schema;
   }

   /**
    * Setter for property schema.
    * @param schema New value of property schema.
    */
   public void setSchema(FieldSchema schema)
   {
      this.schema = schema;
   }

   @Override
   public DataSchema getDataSchema()
   {
      return schema;
   }

   /**
    * Getter for property nNodes.
    * @return Value of property nNodes.
    */
    @Override
   public int getNNodes()
   {
      return nNodes;
   }

   /**
    * Getter for property nSpace.
    * @return Value of property nSpace.
    */
   public int getNSpace()
   {
      return nSpace;
   }

   /**
    * Setter for property nSpace.
    * @param nSpace New value of property nSpace.
    */
   public void setNSpace(int nSpace)
   {
      this.nSpace = nSpace;
      structTimestamp = System.currentTimeMillis();
   }

   /**
    * Get the value of nFrames
    *
    * @return the value of nFrames
    */
   public int getNFrames()
   {
      int nFrames = -1;
      if (timeCoords != null)
         nFrames = timeCoords.size();
      if (timeMask != null && timeMask.size() > nFrames)
         nFrames = timeMask.size();
      for (DataArray dataArray : data)
         if (dataArray.getNFrames() > nFrames)
            nFrames = dataArray.getNFrames();
      return nFrames;
       //return getAllTimesteps().length
   }

   /**
    * Get the value of currentFrame
    *
    * @return the value of currentFrame
    */
//   public int getCurrentFrame()
//   {
//       return
//   }

   /**
    * Set the value of currentFrame
    *
    * @param currentFrame new value of currentFrame
    */
//   public void setCurrentFrame(int cFrame)
//   {
//      if (timeCoords != null)
//      {
//         timeCoords.setCurrentFrame(cFrame);
//         coords = timeCoords.getData();
//      }
//      if (timeMask != null)
//      {
//         timeMask.setCurrentFrame(cFrame);
//         mask = timeMask.getData();
//      }
//      for (DataArray dataArray : data)
//      {
//         dataArray.setCurrentFrame(cFrame);
//      }
//   }

   public float[] getNewTimestepCoords()
   {
      if (lastValidFrame < 0 && !timeCoords.isEmpty())
      {
         timeCoords.clear();
         lastValidFrame = -1;
      }
      float[] crds = new float[nNodes * nSpace];
      timeCoords.setData(crds, 0);
      return crds;
   }


   public float[][] getExtents()
   {
      return extents;
   }

   public float getDiameter()
   {
      double d = 0;
      for (int i = 0; i < extents[0].length; i++)
         d += (extents[1][i] - extents[0][i]) * (extents[1][i] - extents[0][i]);
      return (float)Math.sqrt(d) / 2;
   }

   public void setExtents(float[][] extents)
   {
      this.extents = extents;
   }

   /**
    * Returns number of components
    */
    @Override
   public int getNData()
   {
      return data.size();
   }

   protected void physExtsFromExts()
   {
      for (int i = 0; i < 2; i++)
         System.arraycopy(extents[i], 0, physExts[i], 0, nSpace);
   }

   public float[][] getPhysExts()
   {
      return physExts;
   }

   public void setPhysExts(float[][] physExts)
   {
      this.physExts = physExts;
   }

   @Override
   public DataArray getData(int i)
   {
      if (i<0 || i>=data.size())
         return null;
      return data.get(i);
   }

   public int[] getScalarDataIndices()
   {
       int scalarComponentsCount = 0;
       for (int i = 0; i < data.size(); i++) {
           if(data.get(i).getVeclen() == 1) {
               scalarComponentsCount++;
           }           
       }
       if(scalarComponentsCount == 0)
           return null;
       
       int[] out = new int[scalarComponentsCount];
       for (int i = 0, c = 0; i < data.size(); i++) {
           if(data.get(i).getVeclen() == 1) {
               out[c] = i;
               c++;
           }           
       }
       return out;
   }

   public int getFirstScalarComponentIndex() {
       for (int i = 0; i < data.size(); i++) {
           if(data.get(i).getVeclen() == 1) {
               return i;
           }           
       }
       return -1;
   }
   
   public DataArray getFirstScalarComponent() {
       int n = getFirstScalarComponentIndex();
       if(n == -1)
           return null;
       return data.get(n);
   }

   public int[] getVectorDataIndices()
   {
       int vectorComponentsCount = 0;
       for (int i = 0; i < data.size(); i++) {
           if(data.get(i).getVeclen() != 1) {
               vectorComponentsCount++;
           }           
       }
       if(vectorComponentsCount == 0)
           return null;
       
       int[] out = new int[vectorComponentsCount];
       for (int i = 0, c = 0; i < data.size(); i++) {
           if(data.get(i).getVeclen() != 1) {
               out[c] = i;
               c++;
           }           
       }
       return out;
   }

   public int getFirstVectorComponentIndex() {
       for (int i = 0; i < data.size(); i++) {
           if(data.get(i).getVeclen() != 1) {
               return i;
           }           
       }
       return -1;
   }

   public DataArray getFirstVectorComponent() {
       int n = getFirstVectorComponentIndex();
       if(n == -1)
           return null;
       return data.get(n);
   }
   
   public DataArray getData(String s)
   {
      for (DataArray dataArray : data)
         if (dataArray.getName().equals(s))
            return dataArray;
      return null;
   }


   public ArrayList<DataArray> getData()
   {
      return data;
   }

   public void setData(ArrayList<DataArray> data)
   {
      this.data = data;
   }

   /**
    * Get a read-only list of data arrays
    *
    * @return Data arrays
    */
   public List<DataArray> getDataArrays()
   {
      return Collections.unmodifiableList(data);
   }

   public void clearData()
   {
      data.clear();
      schema.getSchemasFromData(data);
   }

    @Override
   public void addData(DataArray dataArray)
   {
      //check naming
      if(this.getData(dataArray.getName()) != null) {
          dataArray.setName(dataArray.getName()+"_");
          this.addData(dataArray);
          return;          
      }       
      data.add(dataArray);
      schema.addDataArraySchema(dataArray.getSchema());
      dataTimestamp = System.currentTimeMillis();
   }

   public boolean removeData(DataArray dataArray) {
       if(data.contains(dataArray)) {
           schema.removeDataArraySchema(dataArray.getSchema());
           return data.remove(dataArray);
       } else {
           return false;
       }
   }

   public boolean removeData(int i) {
       if(i < 0 || i >= data.size())
           return false;

       schema.removeDataArraySchema(data.get(i).getSchema());
       data.remove(i);
       return true;
   }

    public void removeAllData() {
        while(this.getNData() > 0)
            removeData(this.getNData()-1);
    }

   
   public boolean removeData(String name) {
       DataArray da = this.getData(name);
       if(da != null)
           return this.removeData(da);
       else
           return false;
  }

    @Override
   public void setData(int i, DataArray dataArray)
   {
      if (i<0) return;
      if (i>=data.size())
         data.add(dataArray);
      else
         data.set(i,dataArray);
      schema.getSchemasFromData(data);
      dataTimestamp = System.currentTimeMillis();
   }

    @Override
   public void setData(int i, byte[] data0)
   {
      if (i>=data.size())
         setData(i, data0, "data"+data.size());
      else
         setData(i, data0, "data"+i);
      schema.getSchemasFromData(data);
      dataTimestamp = System.currentTimeMillis();
   }

    @Override
   public void setData(int i, byte[] data0, String name)
   {
      if (data0.length % nNodes != 0 || i<0) return;
      if (i>=data.size())
         data.add(DataArray.create(data0,data0.length/nNodes,name));
      else
         data.set(i, DataArray.create(data0,data0.length/nNodes,name));
      schema.getSchemasFromData(data);
      dataTimestamp = System.currentTimeMillis();
   }

    @Override
   public void setData(int i, short[] data0)
   {
      if (i>=data.size())
         setData(i, data0, "data"+data.size());
      else
         setData(i, data0, "data"+i);
      schema.getSchemasFromData(data);
      dataTimestamp = System.currentTimeMillis();
   }

    @Override
   public void setData(int i, short[] data0, String name)
   {
      if (data0.length % nNodes != 0 || i<0) return;
      if (i>=data.size())
         data.add(DataArray.create(data0,data0.length/nNodes,name));
      else
         data.set(i, DataArray.create(data0,data0.length/nNodes,name));
      schema.getSchemasFromData(data);
      dataTimestamp = System.currentTimeMillis();
   }

    @Override
   public void setData(int i, int[] data0)
   {
      if (i>=data.size())
         setData(i, data0, "data"+data.size());
      else
         setData(i, data0, "data"+i);
      schema.getSchemasFromData(data);
      dataTimestamp = System.currentTimeMillis();
   }

    @Override
   public void setData(int i, int[] data0, String name)
   {
      if (data0.length % nNodes != 0 || i<0) return;
      if (i>=data.size())
         data.add(DataArray.create(data0,data0.length/nNodes,name));
      else
         data.set(i, DataArray.create(data0,data0.length/nNodes,name));
      schema.getSchemasFromData(data);
      dataTimestamp = System.currentTimeMillis();
   }

    @Override
   public void setData(int i, float[] data0)
   {
      if (i>=data.size())
         setData(i, data0, "data"+data.size());
      else
         setData(i, data0, "data"+i);
      schema.getSchemasFromData(data);
      dataTimestamp = System.currentTimeMillis();
   }

    @Override
   public void setData(int i, float[] data0, String name)
   {
      if (data0.length % nNodes != 0 || i<0) return;
      if (i>=data.size())
         data.add(DataArray.create(data0,data0.length/nNodes,name));
      else
         data.set(i, DataArray.create(data0,data0.length/nNodes,name));
      schema.getSchemasFromData(data);
      dataTimestamp = System.currentTimeMillis();
   }

    @Override
   public void setData(int i, double[] data0)
   {
      if (i>=data.size())
         setData(i, data0, "data"+data.size());
      else
         setData(i, data0, "data"+i);
      schema.getSchemasFromData(data);
      dataTimestamp = System.currentTimeMillis();
   }

    @Override
   public void setData(int i, double[] data0, String name)
   {
      if (data0.length % nNodes != 0 || i<0) return;
      if (i>=data.size())
         data.add(DataArray.create(data0,data0.length/nNodes,name));
      else
         data.set(i, DataArray.create(data0,data0.length/nNodes,name));
      schema.getSchemasFromData(data);
      dataTimestamp = System.currentTimeMillis();
   }

   public boolean[] getMask()
   {
      if (timeMask == null || timeMask.isEmpty())
         return null;
      if (mask == null)
         mask = timeMask.getData(currentTime);
      return mask;
   }

   public boolean[] getMaskFull()
   {
      if (timeMask == null || timeMask.isEmpty())
         return null;

      int nFrames = this.getNFrames();
      boolean[] out = new boolean[nNodes*nFrames];
      for (int i = 0; i < nFrames; i++) {
           boolean[] tmp = getMask(i);
           System.arraycopy(tmp, 0, out, i*nNodes, nNodes);
      }
      return out;
   }

   public boolean[] getMask(int frame)
   {
      if (timeMask == null || timeMask.isEmpty())
         return null;
      if (frame < 0)
         frame = 0;
      if (frame >= timeMask.size())
         frame = timeMask.size() - 1;
      return timeMask.get(frame);
   }

   public boolean[] getMaskTimeSlice(int node)
   {
       if(!isMask())
           return null;
       int nFrames = getNFrames();
       boolean[] out = new boolean[nFrames];
        for (int i = 0; i < out.length; i++) {
            out[i] = timeMask.get(i)[node];
        }
       return out;
   }

   public void setMask(boolean[] mask)
   {
      if (mask == null) {
          this.timeMask = null;
          return;
      }
      int nFrames = mask.length / nNodes;
      if (mask.length != nFrames * nNodes)
         return;
      if (this.timeMask == null)
         this.timeMask = new TimeData<boolean[]>();
      else
         this.timeMask.clear();
      this.mask = null;
      if (nFrames == 1)
      {
         timeMask.add(mask);
      } else
      {
         for (int i = 0; i < nFrames; i++)
         {
            boolean[] c = new boolean[nNodes];
            System.arraycopy(mask, i * nNodes, c, 0, nNodes);
            timeMask.add(c);
         }
      }
      updateExtents();
   }

   public float[] getMaskTimeline() {
       float[] out = new float[getNFrames()];
       for (int i = 0; i < out.length; i++) {
           out[i] = timeMask.getTime(i);
       }
       return out;
   }

   public void setMask(boolean[] mask, float[] time)
   {
      if (mask == null) {
          this.timeMask = null;
          return;
      }
      int nFrames = mask.length / nNodes;
      if (mask.length != nFrames * nNodes) return;
      if(time == null || time.length != nFrames) return;
      if (this.timeMask == null)
         this.timeMask = new TimeData<boolean[]>();
      else
         this.timeMask.clear();
      this.mask = null;
      if (nFrames == 1) {
         timeMask.add(mask);
      } else {
         for (int i = 0; i < nFrames; i++) {
            boolean[] c = new boolean[nNodes];
            System.arraycopy(mask, i * nNodes, c, 0, nNodes);
            timeMask.setData(c, time[i]);
         }
      }
      updateExtents();
   }

   public boolean isMask()
   {
      return getMask() != null;
   }

   public boolean[] getTransparencyMask()
   {
      return transparencyMask;
   }

   public void setTransparencyMask(boolean[] transparencyMask)
   {
      this.transparencyMask = transparencyMask;
   }

   public boolean isTransparencyMask()
   {
      return transparencyMask != null && transparencyMask.length == nNodes;
   }

   abstract public float[] getNodeCoords(int k);
   abstract public float[] getInterpolatedData(float[] point, int index);
   abstract public DataArray interpolateDataToMesh(Field mesh, DataArray da);

   public float[] getCoords(int frame)
   {
      if (timeCoords == null || timeCoords.isEmpty())
         return null;
      if (frame < 0)
         frame = 0;
      if (frame >= timeCoords.size())
         frame = timeCoords.size() - 1;
      return timeCoords.get(frame);
   }

   public double[] getCoordsDP(float time)
   {
      if (timeCoords == null || timeCoords.isEmpty())
         return null;
      double[] tc = new double[nNodes * nSpace];
      float[] c = timeCoords.getData(time);
      for (int i = 0; i < tc.length; i++)
         tc[i] = (double) c[i];
      return tc;
   }

   public float[] getCoords()
   {
      if (timeCoords == null || timeCoords.isEmpty())
         return null;
      if (coords == null) {
         coords = timeCoords.getData(currentTime);
         coordsHash = RabinHashFunction.hash(this.coords);
      }
      return coords;
   }

   public float[] getCoordsFull()
   {
      if (timeCoords == null || timeCoords.isEmpty())
         return null;

      int nFrames = this.getNFrames();
      float[] out = new float[nSpace*nNodes*nFrames];
      for (int i = 0; i < nFrames; i++) {
           float[] tmp = getCoords(i);
           System.arraycopy(tmp, 0, out, i*nNodes*nSpace, nNodes*nSpace);
      }
      return out;
   }


   public double[] getCoordsDP()
   {
      if (timeCoords == null || timeCoords.isEmpty())
         return null;
      return getCoordsDP(currentTime);
   }

   public TimeData<float[]> getAllCoords()
   {
      return timeCoords;
   }

   public void setCoords(TimeData<float[]> timeCoords)
   {
      this.timeCoords = timeCoords;
      if (timeCoords == null)
      {
         coords = null;
         coordsHash = -1;
         updateExtents();
         return;
      }
      updateExtents();
      timeCoords.setCurrentTime(currentTime);
      coords = timeCoords.getData();
      coordsHash = RabinHashFunction.hash(this.coords);
   }

   public TimeData<boolean[]> getTimeMask()
   {
      return timeMask;
   }

   public void setTimeMask(TimeData<boolean[]> timeMask)
   {
      this.timeMask = timeMask;
   }

   public void addCoords(float[] c)
   {
      if(timeCoords == null)
          timeCoords = new TimeData<float[]>();
      timeCoords.add(c);
      coords = c;
      coordsHash = RabinHashFunction.hash(this.coords);
      updateExtents();
   }

   public void addCoords(float[] c, float time)
   {
      if(timeCoords == null)
          timeCoords = new TimeData<float[]>();
      timeCoords.setData(c, time);
      //currentTime = time;
      updateExtents();
   }

   public void addMask(boolean[] m)
   {
      if(timeMask == null)
          timeMask = new TimeData<boolean[]>();
      timeMask.add(m);
      mask = m;
      updateExtents();
   }

   public void addMask(boolean[] m, float time)
   {
      if(timeMask == null)
          timeMask = new TimeData<boolean[]>();
      timeMask.setData(m, time);
      currentTime = time;
      updateExtents();
   }

   public void forceCurrentTime(float currentTime)
   {
      this.currentTime = currentTime;
      if (timeCoords != null && !timeCoords.isEmpty())
      {
         timeCoords.setCurrentTime(currentTime);
         coords = timeCoords.getData();
         coordsHash = RabinHashFunction.hash(this.coords);
      }
      if (timeMask != null && !timeMask.isEmpty())
      {
         timeMask.setCurrentTime(currentTime);
         mask = timeMask.getData();
      }
      for (DataArray dataArray : data)
         dataArray.setCurrentTime(currentTime);
   }

    public void setCurrentTime(float currentTime)
   {
      if (this.currentTime != currentTime)
         forceCurrentTime(currentTime);
      else
      {
         if (timeCoords != null && !timeCoords.isEmpty() && coords == null)
         {
            timeCoords.setCurrentTime(currentTime);
            coords = timeCoords.getData();
            coordsHash = RabinHashFunction.hash(this.coords);
         }
         if (timeMask != null && !timeMask.isEmpty() && mask == null)
         {
            timeMask.setCurrentTime(currentTime);
            mask = timeMask.getData();
         }
         for (DataArray dataArray : data)
            dataArray.setCurrentTime(currentTime);
      }
   }

  public float[] getTrajectory(int node)
   {
      if(timeCoords == null || timeCoords.isEmpty())
           return null;
      int nFrames = timeCoords.size();
      float[] tr = new float[nFrames * nSpace];
      for (int i = 0; i < nFrames; i++)
         for (int k = 0; k < nSpace; k++)
            tr[nSpace * i + k] = timeCoords.get(i)[nSpace * node + k];
      return tr;
   }

   public void updateExtents() {
       updateExtents(false);
   }

   public void updateExtents(boolean ignoreMask)
   {
       extents = new float[2][nSpace];
       if (timeCoords == null || timeCoords.isEmpty()) {
           return;
       }
       for (int i = 0; i < nSpace; i++) {
           extents[0][i] = Float.MAX_VALUE;
           extents[1][i] = -Float.MAX_VALUE;
       }

      float f;
      int nValid = 0;
      for (int k = 0; k < timeCoords.size(); k++)
      {
         boolean[] currentMask = null;
         if (!ignoreMask && timeMask != null)
            currentMask = timeMask.getData(timeCoords.getTime(k));
         float[] c = timeCoords.get(k);
         for (int i = 0; i < nNodes; i++)
         {
            if (currentMask != null && !currentMask[i])
               continue;  //skip invalid nodes
            nValid += 1;
            for (int j = 0; j < nSpace; j++)
            {
               f = c[i * nSpace + j];
               if (extents[0][j] > f) extents[0][j] = f;
               if (extents[1][j] < f) extents[1][j] = f;
            }
         }
      }
      if (nValid == 0)
         for (int i = 0; i < 3; i++)
         {
            extents[1][i] = 1;
            extents[0][i] = -1;
         }


       if( extents[0][0] == extents[1][0] &&
           extents[0][1] == extents[1][1] &&
           extents[0][2] == extents[1][2] )
       { // min == max
            extents[0][0] -= 0.5f;
            extents[1][0] += 0.5f;
            extents[0][1] -= 0.5f;
            extents[1][1] += 0.5f;
            extents[0][2] -= 0.5f;
            extents[1][2] += 0.5f;
       }

       physExtsFromExts();
   }

   public void clearCoords()
   {
      if (timeCoords != null)
         timeCoords.clear();
   }

   public void setCoords(float[] coords)
   {
       if(coords == null) 
       {
           this.timeCoords = null;
           this.coords = null;
           coordsHash = -1;
           updateExtents();
           return;
       }
       
      assert( coords.length % (nSpace * nNodes) == 0 );
      int nFrames = coords.length / (nSpace * nNodes);
      if (coords.length != nFrames * nSpace * nNodes) return;
      if (this.timeCoords == null)
         this.timeCoords = new TimeData<float[]>();
      else
         this.timeCoords.clear();
      this.coords = null;
      if(nFrames == 1) {
          timeCoords.add(coords);
      } else {
        for (int i = 0; i < nFrames; i++)
        {
            float[] c = new float[nSpace*nNodes];
            System.arraycopy(coords, i * nSpace*nNodes, c, 0, nSpace*nNodes);
            timeCoords.setData(c, i);
        }
      }
      this.coords = timeCoords.getData(0);
      coordsHash = RabinHashFunction.hash(this.coords);
      updateExtents();
   }

   public float[] getCoordsTimeline() {
       float[] out = new float[getNFrames()];
       for (int i = 0; i < out.length; i++) {
           out[i] = timeCoords.getTime(i);
       }
       return out;
   }

   public void setCoords(float[] coords, float[] time)
   {
       if(coords == null) {
           this.timeCoords = null;
           this.coords = null;
           coordsHash = -1;
           updateExtents();
           return;
       }
       
      int nFrames = coords.length / (nSpace * nNodes);
      if (coords.length != nFrames * nSpace * nNodes) return;
      if(time == null  || time.length != nFrames) return;
      if (this.timeCoords == null)
         this.timeCoords = new TimeData<float[]>();
      else
         this.timeCoords.clear();
      this.coords = null;
      if(nFrames == 1) {
          timeCoords.add(coords);
      } else {
        for (int i = 0; i < nFrames; i++)
        {
            float[] c = new float[nSpace*nNodes];
            System.arraycopy(coords, i * nSpace*nNodes, c, 0, nSpace*nNodes);
            timeCoords.setData(c, time[i]);
        }
      }
      this.coords = timeCoords.getData(currentTime);
      coordsHash = RabinHashFunction.hash(this.coords);
      updateExtents();
   }


   public void setCoordsDP(double[] coords)
   {
       if (coords == null) {
           this.timeCoords = null;
           this.coords = null;
           coordsHash = -1;
           updateExtents();
           return;
       }

       assert (coords.length % (nSpace * nNodes) == 0);
       int nFrames = coords.length / (nSpace * nNodes);
       if (coords.length != nFrames * nSpace * nNodes) {
           return;
       }
       if (this.timeCoords == null) {
           this.timeCoords = new TimeData<float[]>();
       } else {
           this.timeCoords.clear();
       }
       this.coords = null;
       for (int i = 0; i < nFrames; i++) {
           int k = i * nSpace * nNodes;
           float[] c = new float[nSpace * nNodes];
           for (int j = 0; j < c.length; j++) {
               c[j] = (float) coords[k + j];
           }
           timeCoords.setData(c, i);
       }
       this.coords = timeCoords.getData(currentTime);
       coordsHash = RabinHashFunction.hash(this.coords);
       updateExtents();
   }

   abstract public float[] getNormals();
   abstract public void setNormals(float[] normals);
   
   public boolean isDataCompatibleWith(Field f)
   {
      if (f == null)
         return false;
      return schema.isDataCompatibleWith(f.getSchema());
   }

   public boolean isFullyCompatibleWith(Field f)
   {
      if (f == null)
         return false;
      return schema.isDataCompatibleWith(f.getSchema(), true, true);
   }

   public boolean isDataCompatibleWith(FieldSchema s)
   {
      return schema.isDataCompatibleWith(s);
   }

   protected void updateCoordsTimestamp()
   {
      coordsTimestamp = System.currentTimeMillis();
   }

   public boolean coordsChangedSince(long timestamp)
   {
      return this.coordsTimestamp > timestamp;
   }

   protected void updateStructureTimestamp()
   {
      structTimestamp = System.currentTimeMillis();
   }

   public boolean structureChangedSince(long timestamp)
   {
      return this.structTimestamp > timestamp;
   }

   public boolean dataChangedSince(long timestamp)
   {
      return this.dataTimestamp > timestamp;
   }

    public long getCoordsHash()
    {
        return coordsHash;
    }

    public boolean coordsChanged(Field f)
    {
       return coordsHash != f.getCoordsHash();
    }

    public long getDataHash()
    {
      try
      {
          return RabinHashFunction.hash(data);
      } catch (IOException ex)
      {
          return 0;
      }
    }

    public boolean coordsHashChanged(Field f)
    {
       return coordsHash != f.getCoordsHash();
    }

    public boolean dataHashChanged(Field f)
    {
       return this.getDataHash() != f.getDataHash();
    }

   public GeoTreeNode getGeoTree()
   {
      return geoTree;
   }

   abstract public void createGeoTree();

   abstract public SimplexPosition getFieldCoords(float[] p);

   public int[][] getSimpleNumericDataSchema()
   {
      int nSNData = 0;
      for (int i = 0; i < data.size(); i++)
         if (data.get(i).isSimpleNumeric())
            nSNData += 1;
      int[][] sNDS = new int[nSNData][2];
      for (int i = 0, j = 0; i < data.size(); i++)
         if (data.get(i).isSimpleNumeric())
         {
            sNDS[j][0] = data.get(i).getType();
            sNDS[j][1] = data.get(i).getVeclen();
         }
      return sNDS;
   }

    @Override
    public String toString() {
        final String TAB = "    ";
        return "Field ( " + super.toString() + TAB + "data=" + this.data + TAB +
                   "nNodes=" + this.nNodes + TAB + "nSpace=" + this.nSpace + " )";
    }

    public String[] getAxesNames() {
        return axesNames;
    }

    public void setAxesNames(String[] axesNames) {
        if(axesNames != null && axesNames.length != nSpace) {
            this.axesNames = null;
            return;
        }
        this.axesNames = axesNames;
    }

   protected float[] bCoords(Tetra tet, float[] p)
   {
      if (tet == null || timeCoords == null || timeCoords.isEmpty())
         return null;
      float[] c = timeCoords.getData(currentTime);
      int[] verts = tet.getVertices();
      int l = 3 * verts[0];
      float[][] A  = new float[3][3];
      float[]   v0 = new float[3];
      float[]   b  = new float[3];
      for (int i = 0; i < 3; i++)
         v0[i] = c[l + i];
      for (int i = 0; i < 3; i++)
      {
         b[i] = p[i] - v0[i];
         for (int j = 0; j < 3; j++)
            A[i][j] = c[3 * verts[j + 1] + i] - v0[i];
      }
      float[] x = NumericalMethods.lsolve(A, b);
      if (x == null || x[0] < 0 || x[1] < 0 || x[2] < 0 || x[0] + x[1] + x[2] > 1)
         return null;
      float[] res = new float[4];
      System.arraycopy(x, 0, res, 1, 3);
      res[0] = 1 - (x[0] + x[1] + x[2]);
      return res;
   }

   protected float[] bCoords(Triangle triangle, float[] p)
   {
      if (triangle == null || timeCoords == null || timeCoords.isEmpty())
         return null;
      float[] c = timeCoords.getData(currentTime);
      int[] verts = triangle.getVertices();
      int l = nSpace * verts[0];
      float[][] A  = new float[2][2];
      float[]   v0 = new float[2];
      float[]   b  = new float[2];
      for (int i = 0; i < 2; i++)
         v0[i] = c[l + i];
      for (int i = 0; i < 2; i++)
      {
         b[i] = p[i] - v0[i];
         for (int j = 0; j < 2; j++)
            A[i][j] = c[nSpace * verts[j + 1] + i] - v0[i];
      }
      float[] x = NumericalMethods.lsolve(A, b);
      if (x == null || x[0] < 0 || x[1] < 0 || x[0] + x[1] > 1)
         return null;
      float[] res = new float[3];
      System.arraycopy(x, 0, res, 1, 2);
      res[0] = 1 - (x[0] + x[1]);
      return res;
   }

   public int getLastValidFrame()
   {
      return lastValidFrame;
   }

   public void setLastValidFrame(int lastValidFrame)
   {
      this.lastValidFrame = lastValidFrame;
   }

   public float[]  produceCoords(float time)
   {
      return timeCoords.produceData(time, DataArray.FIELD_DATA_FLOAT, nSpace * nNodes);
   }

   public float[] getCoords(float time)
   {
      return timeCoords.getData(time);
   }

   public boolean[]  produceMask(float time)
   {
      return timeMask.produceData(time, DataArray.FIELD_DATA_BOOLEAN, nNodes);
   }

   public boolean[] getMask(float time)
   {
      return timeMask.getData(time);
   }

   @Override
   public float getCurrentTime()
   {
      return currentTime;
   }

   public float getStartTime()
   {
      float t = Float.MAX_VALUE;
      if (timeCoords != null && !timeCoords.isEmpty() && timeCoords.getStartTime() < t)
         t = timeCoords.getStartTime();
      if (timeMask != null && !timeMask.isEmpty() && timeMask.getStartTime() < t)
         t = timeMask.getStartTime();
      for (DataArray dataArray : data)
         if (dataArray.getStartTime() < t)
            t = dataArray.getStartTime();
      return t;
   }

   public float getEndTime()
   {
      float t = -Float.MAX_VALUE;
      if (timeCoords != null && !timeCoords.isEmpty() && timeCoords.getEndTime() > t)
         t = timeCoords.getEndTime();
      if (timeMask != null && !timeMask.isEmpty() && timeMask.getEndTime() > t)
         t = timeMask.getEndTime();
      for (DataArray dataArray : data)
         if (dataArray.getEndTime() > t)
            t = dataArray.getEndTime();
      return t;
   }

   public boolean isCoordTimestep(float t)
   {
      return timeCoords != null && !timeCoords.isEmpty() && timeCoords.isTimestep(t);
   }

   public boolean isMaskTimestep(float t)
   {
      return timeMask != null && !timeMask.isEmpty() && timeMask.isTimestep(t);
   }

   public float[] getAllTimesteps()
   {
      Set<Float> tSteps = new HashSet<Float>();
      if (timeCoords != null && !timeCoords.isEmpty())
         for (Float t : timeCoords.getTimeSeries())
            tSteps.add(t);
      if (timeMask != null && !timeMask.isEmpty())
         for (Float t : timeMask.getTimeSeries())
            tSteps.add(t);
      for (DataArray da : data)
      {
         for (Float t : da.getTimeSeries())
            tSteps.add(t);
      }
      float[] timesteps = new float[tSteps.size()];
      int i = 0;
      for (Float t : tSteps)
      {
         timesteps[i] = t;
         i += 1;
      }
      Arrays.sort(timesteps);
      return timesteps;
   }

   public boolean isTimeDependant()
   {
      if (timeCoords != null && timeCoords.size() > 1)
          return true;
      if (timeMask != null && timeMask.size() > 1)
          return true;
      for (DataArray da : data)
      {
          if(da.isTimeDependant())
              return true;
      }
      return false;
   }

   public String getTimeUnit()
   {
      return timeUnit;
   }

   public void setTimeUnit(String timeUnit)
   {
      this.timeUnit = timeUnit;
   }

   public TimeData<float[]> getTimeCoords()
   {
      return timeCoords;
   }

   public boolean isCoords()
   {
      return timeCoords != null && !timeCoords.isEmpty();
   }

   public void setCoords(float[] c, float t)
   {
      if(timeCoords == null)
          this.timeCoords = new TimeData<float[]>();
      timeCoords.setData(c, t);
      if(t == currentTime)
          coordsHash = RabinHashFunction.hash(c);
      updateExtents();
   }

   public void setMask(boolean[] m, float t)
   {
       if(timeMask == null)
           this.timeMask = new TimeData<boolean[]>();

      timeMask.setData(m, t);
      updateExtents();
   }

   public void updateTimesteps()
   {

   }

   public ArrayList getTimesteps()
   {
      return timesteps;
   }

   public int getTrueDim()
   {
      return trueDim;
   }

   public boolean hasSimpleNumericComponent()
   {
      for (DataArray dataArray : data)
         if (dataArray.isSimpleNumeric())
            return true;
      return false;
   }

   public boolean hasProperVectorComponent()
   {
      checkPureDim();
      for (DataArray dataArray : data)
         if (dataArray.isSimpleNumeric() && dataArray.getVeclen() == trueDim)
            return true;
      return false;
   }

   abstract public void checkPureDim();


   protected void pCreateGeoTree(GeoTreeNode root, int maxThr)
   {
      int inProcess = 0;
      Thread[] threadPool = new Thread[maxThr];
      Queue<GeoTreeNode> processQueue = new LinkedBlockingQueue<GeoTreeNode>();
      GeoTreeNode[] nodesInProcess = new GeoTreeNode[maxThr];
      processQueue.add(root);
      while (!processQueue.isEmpty() || inProcess > 0)
      {
         inProcess = 0;
         for (int i = 0; i < nodesInProcess.length; i++)
         {
            if (threadPool[i] == null)
               continue;
            else if (threadPool[i].isAlive())
               inProcess += 1;
            else
            {
               GeoTreeNode lastProcessedNode = nodesInProcess[i];
               if (!lastProcessedNode.isFullySplit() && lastProcessedNode.getCells() == null)
               {
                  processQueue.add(lastProcessedNode.getNodeAbove());
                  processQueue.add(lastProcessedNode.getNodeBelow());
               }
               threadPool[i] = null;
               nodesInProcess[i] = null;
            }
         }
         if (inProcess < maxThr)
         {
            for (int i = 0; i < nodesInProcess.length && !processQueue.isEmpty(); i++)
               if (threadPool[i] == null)
            {
               GeoTreeNode nodeToProcess = processQueue.poll();
               nodesInProcess[i] = nodeToProcess;
               inProcess += 1;
               threadPool[i] = new Thread(nodeToProcess);
               threadPool[i].start();
            }
         }
         try
         {
            Thread.sleep(100);
         } catch (InterruptedException ex)
         {
             LOGGER.error("thread interrupted", ex);
         }
      }
   }
   
   public abstract int[] getIndices(int axis);
   public abstract float[] getFIndices(int axis);
   public abstract double[] getDIndices(int axis);

}
