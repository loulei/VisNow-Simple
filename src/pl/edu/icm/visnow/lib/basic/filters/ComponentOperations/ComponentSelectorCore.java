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

package pl.edu.icm.visnow.lib.basic.filters.ComponentOperations;

import pl.edu.icm.visnow.datasets.Field;
import pl.edu.icm.visnow.datasets.RegularField;
import pl.edu.icm.visnow.datasets.dataarrays.DataArray;

/**
 *
 * @author Bartosz Borucki (babor@icm.edu.pl)
 * University of Warsaw, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class ComponentSelectorCore
{

   private Params params = null;
   private Field inField = null;
   protected RegularField outRegularField = null;
   protected Field outField = null;

   public ComponentSelectorCore()
   {
   }

   public void setData(Field inField, Field outField, Params p)
   {
      this.inField = inField;
      this.outField = outField;
      this.params = p;
   }


   void update()
   {
      if (inField == null || params == null)
      {
         outField = null;
         return;
      }
      int[] actions = params.getActions();
      float[] clampMin = params.getMin();
      float[] clampMax = params.getMax();

      if (params.isAddIndexComponent())
      {
         int[] newActions = new int[actions.length + 1];
         System.arraycopy(actions, 0, newActions, 0, actions.length);
         newActions[newActions.length - 1] = Params.NOOP;
         actions = newActions;
      }
      
      for (int iData = 0; iData < inField.getNData(); iData++)
         if (params.getRetain()[iData])
            outField.addData(inField.getData(iData));

      for (int iData = 0; iData < actions.length; iData++)
      {
         DataArray da = inField.getData(iData);
         if (!da.isSimpleNumeric() || da.getTimeData() == null)
            continue;
         if (actions[iData] == Params.NOOP && 
             clampMin[iData] <= da.getMinv() && clampMax[iData] >= da.getMaxv())
            continue;    
//data type changed, no clamping 
         if (clampMin[iData] <= da.getMinv() && clampMax[iData] >= da.getMaxv())
         {
            DataArray outDa = null;
            for (int timeStep = 0; timeStep < da.getNFrames(); timeStep++)
            {
               float time = da.getTime(timeStep);
               da.setCurrentFrame(timeStep);
               switch (actions[iData])
               {
                case Params.BYTE:
                    byte[] outb = da.getBData();
                    if (timeStep == 0)
                    {
                        outDa = DataArray.create(outb, da.getVeclen(), da.getName()+"_B", da.getUnit(), da.getUserData());
                        outField.addData(outDa);
                    }
                    else
                        outDa.addData(outb, time);
                    continue;   
               case Params.BYTE_NORMALIZED:
                  float minv = clampMin[iData];
                  float maxv = clampMax[iData];
                  float d = 255 / (maxv - minv);
                  byte[] outbd = null;
                  switch (da.getType())
                  {
                  case DataArray.FIELD_DATA_BYTE:
                     if (timeStep == 0)
                        outField.addData(da);
                     break;
                  case DataArray.FIELD_DATA_SHORT:
                     short[] sd = da.getSData();
                     outbd = new byte[sd.length];
                     for (int j = 0; j < outbd.length; j++)
                        outbd[j] = (byte) (0xff & (int) ((sd[j] - minv) * d));
                     break;
                  case DataArray.FIELD_DATA_INT:
                     int[] id = da.getIData();
                     outbd = new byte[id.length];
                     for (int j = 0; j < outbd.length; j++)
                        outbd[j] = (byte) (0xff & (int) ((id[j] - minv) * d));
                     break;
                  case DataArray.FIELD_DATA_FLOAT:
                     float[] fd = da.getFData();
                     outbd = new byte[fd.length];
                     for (int j = 0; j < outbd.length; j++)
                        outbd[j] = (byte) (0xff & (int) ((fd[j] - minv) * d));
                     break;
                  case DataArray.FIELD_DATA_DOUBLE:
                     double[] dd = da.getDData();
                     outbd = new byte[dd.length];
                     for (int j = 0; j < outbd.length; j++)
                        outbd[j] = (byte) (0xff & (int) ((dd[iData] - minv) * d));
                     break;
                  }
                  if (da.getType() != DataArray.FIELD_DATA_BYTE)
                  {
                     if (timeStep == 0)
                     {
                        outDa = DataArray.create(outbd, da.getVeclen(), da.getName()+"_B", da.getUnit(), da.getUserData());
                        outDa.setPhysMin(da.getPhysMin());
                        outDa.setPhysMax(da.getPhysMax());
                        outField.addData(outDa);
                     }
                     else
                        outDa.addData(outbd, time);
                  }
                  continue;
               case Params.SHORT:
                    short[] outs = da.getSData();
                    if (timeStep == 0)
                    {
                        outDa = DataArray.create(outs, da.getVeclen(), da.getName()+"_S", da.getUnit(), da.getUserData());
                        outField.addData(outDa);
                    }
                    else
                        outDa.addData(outs, time);
                    continue;   
               case Params.SHORT_NORMALIZED:
                  minv = clampMin[iData];
                  maxv = clampMax[iData];
                  d = ((float) Short.MAX_VALUE - Short.MIN_VALUE) / (maxv - minv);
                  short[] outsd = null;
                  switch (da.getType())
                  {
                  case DataArray.FIELD_DATA_BYTE:
                     byte[] bd = da.getBData();
                     outsd = new short[bd.length];
                     for (int j = 0; j < outsd.length; j++)
                        outsd[j] = (short) (0xff & bd[j]);
                     outField.addData(da);
                     break;
                  case DataArray.FIELD_DATA_SHORT:
                     if (timeStep == 0)
                        outField.addData(da);
                     break;
                  case DataArray.FIELD_DATA_INT:
                     int[] id = da.getIData();
                     outsd = new short[id.length];
                     for (int j = 0; j < outsd.length; j++)
                        outsd[j] = (short) ((id[j] - minv) * d + Short.MIN_VALUE);
                     break;
                  case DataArray.FIELD_DATA_FLOAT:
                     float[] fd = da.getFData();
                     outsd = new short[fd.length];
                     for (int j = 0; j < outsd.length; j++)
                        outsd[j] = (short) ((fd[j] - minv) * d + Short.MIN_VALUE);
                     break;
                  case DataArray.FIELD_DATA_DOUBLE:
                     double[] dd = da.getDData();
                     outsd = new short[dd.length];
                     for (int j = 0; j < outsd.length; j++)
                        outsd[j] = (short) ((dd[j] - minv) * d + Short.MIN_VALUE);
                     break;
                  }
                  if (da.getType() != DataArray.FIELD_DATA_SHORT)
                  {
                     if (timeStep == 0)
                     {
                        outDa = DataArray.create(outsd, da.getVeclen(), da.getName()+"_S", da.getUnit(), da.getUserData());
                        outDa.setPhysMin(da.getPhysMin());
                        outDa.setPhysMax(da.getPhysMax());
                        outField.addData(outDa);
                     }
                     else
                        outDa.addData(outsd, time);
                  }
                  continue;
               case Params.INT:
                    int[] outi = da.getIData();
                    if (timeStep == 0)
                    {
                        outDa = DataArray.create(outi, da.getVeclen(), da.getName()+"_I", da.getUnit(), da.getUserData());
                        outField.addData(outDa);
                    }
                    else
                        outDa.addData(outi, time);

                    continue;     
               case Params.FLOAT:
                    float[] outf = da.getFData();
                    if (timeStep == 0)
                    {
                        outDa = DataArray.create(outf, da.getVeclen(), da.getName()+"_F", da.getUnit(), da.getUserData());
                        outField.addData(outDa);
                    }
                    else
                        outDa.addData(outf, time);

                    continue; 
               case Params.DOUBLE:
                    double[] outdo = da.getDData();
                    if (timeStep == 0)
                    {
                        outDa = DataArray.create(outdo, da.getVeclen(), da.getName()+"_D", da.getUnit(), da.getUserData());
                        outField.addData(outDa);
                    }
                    else
                        outDa.addData(outdo, time);
                    continue; 
               case Params.LOG:
                  if (da.getVeclen() == 1 && da.getMinv() > 0)
                  {
                     float[] outd = da.getFData();
                     if (da.getType() == DataArray.FIELD_DATA_FLOAT)
                        outd = outd.clone();
                     for (int j = 0; j < outd.length; j++)
                        outd[j] = (float)Math.log(outd[j]);
                     outField.addData(DataArray.create(outd, 1, "log_" + da.getName()));
                  }
                  continue;
               case Params.ATAN:         
                  if (da.getVeclen() == 1)
                  {
                     float[] outd = da.getFData();
                     float md = Math.max(Math.abs(da.getMinv()), Math.abs(da.getMaxv())) / 10;
                     if (da.getType() == DataArray.FIELD_DATA_FLOAT)
                        outd = outd.clone();
                     for (int j = 0; j < outd.length; j++)
                        outd[j] = (float)Math.atan(outd[j] / md);
                     outField.addData(DataArray.create(outd, 1, "atan_" + da.getName()));
                  }
                  continue;
               }
            }
         }
         if (actions[iData] == Params.NOOP && 
             (clampMin[iData] > da.getMinv() || clampMax[iData] < da.getMaxv()))
         {
            DataArray outDa = null;

            for (int timeStep = 0; timeStep < da.getNFrames(); timeStep++)
            {
               float time = da.getTime(timeStep);
               da.setCurrentFrame(timeStep);
               int vlen = da.getVeclen();
               int n = da.getNData();
               float cm = clampMin[iData];
               float cx = clampMax[iData];
      // clamping data         
               switch (da.getType())
               {
               case DataArray.FIELD_DATA_BYTE:
                  byte[] outbd = da.getBData();
                  if (da.getType() == actions[iData])
                     outbd = outbd.clone();
                  for (int j = 0; j < outbd.length; j++)
                  {
                     int k = outbd[j] & 0xff;
                     k = (k > cx ? (int) cx : (k < cm ? (int) cm : k));
                     outbd[j] = (byte) (k & 0xff);
                  }
                  if (timeStep == 0)
                  {
                     outDa = DataArray.create(outbd, da.getVeclen(), da.getName()+"_B", da.getUnit(), da.getUserData());
                     outField.addData(outDa);
                  }
                  else
                     outDa.addData(outbd, time);
                  break;
               case DataArray.FIELD_DATA_SHORT:
                  short[] outsd = da.getSData();
                  if (da.getType() == actions[iData])
                     outsd = outsd.clone();
                  for (int j = 0; j < outsd.length; j++)
                  {
                     outsd[j] = (outsd[j] > cx ? (short) cx : (outsd[j] < cm ? (short) cm : outsd[j]));
                  }
                  if (timeStep == 0)
                  {
                     outDa = DataArray.create(outsd, da.getVeclen(), da.getName()+"_S", da.getUnit(), da.getUserData());
                     outField.addData(outDa);
                  }
                  else
                     outDa.addData(outsd, time);
                  break;
               case DataArray.FIELD_DATA_INT:
                  int[] outid = da.getIData();
                  if (da.getType() == actions[iData])
                     outid = outid.clone();
                  if (vlen == 1)
                     for (int j = 0; j < outid.length; j++)
                     {
                        outid[j] = (outid[j] > cx ? (int) cx : (outid[j] < cm ? (int) cm : outid[j]));
                     }
                  else
                  {
                     for (int j = 0; j < n; j++)
                     {
                        double t = 0;
                        for (int k = 0, l = vlen * k; k < vlen; k++, l++)
                           t += outid[l] * outid[l];
                        if (t <= cx * cx)
                           continue;
                        t = cx / Math.sqrt(t);
                        for (int k = 0, l = vlen * k; k < vlen; k++, l++)
                           outid[l] *= t;
                     }
                  }
                  if (timeStep == 0)
                  {
                     outDa = DataArray.create(outid, da.getVeclen(), da.getName()+"_I", da.getUnit(), da.getUserData());
                     outField.addData(outDa);
                  }
                  else
                     outDa.addData(outid, time);
                  break;
               case DataArray.FIELD_DATA_FLOAT:
                  float[] outfd = da.getFData();
                  if (da.getType() == actions[iData])
                     outfd = outfd.clone();
                  if (vlen == 1)
                     for (int j = 0; j < outfd.length; j++)
                     {
                        outfd[j] = (outfd[j] > cx ? cx : (outfd[j] < cm ? cm : outfd[j]));
                     }
                  else
                  {
                     for (int j = 0; j < n; j++)
                     {
                        double t = 0;
                        for (int k = 0, l = vlen * j; k < vlen; k++, l++)
                           t += outfd[l] * outfd[l];
                        if (t <= cx * cx)
                           continue;
                        t = cx / Math.sqrt(t);
                        for (int k = 0, l = vlen * j; k < vlen; k++, l++)
                           outfd[l] *= t;
                     }
                  }
                  if (timeStep == 0)
                  {
                     outDa = DataArray.create(outfd, da.getVeclen(), da.getName()+"_F", da.getUnit(), da.getUserData());
                     outField.addData(outDa);
                  }
                  else
                     outDa.addData(outfd, time);
                  break;
               case DataArray.FIELD_DATA_DOUBLE:
                  double[] outdd = da.getDData();
                  if (da.getType() == actions[iData])
                     outdd = outdd.clone();
                  if (vlen == 1)
                     for (int j = 0; j < outdd.length; j++)
                     {
                        outdd[j] = (outdd[j] > cx ? cx : (outdd[j] < cm ? cm : outdd[j]));
                     }
                  else
                  {
                     for (int j = 0; j < n; j++)
                     {
                        double t = 0;
                        for (int k = 0, l = vlen * k; k < vlen; k++, l++)
                           t += outdd[l] * outdd[l];
                        if (t <= cx * cx)
                           continue;
                        t = cx / Math.sqrt(t);
                        for (int k = 0, l = vlen * k; k < vlen; k++, l++)
                           outdd[l] *= t;
                     }
                  }
                  if (timeStep == 0)
                  {
                     outDa = DataArray.create(outdd, da.getVeclen(), da.getName()+"_D", da.getUnit(), da.getUserData());
                     outField.addData(outDa);
                  }
                  else
                     outDa.addData(outdd, time);
                  break;
               }
            }
         }
      }
   }
   
   Field getOutField()
   {
      return outField;
   }
}
