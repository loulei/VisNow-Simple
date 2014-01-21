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
package pl.edu.icm.visnow.engine.core;

import java.awt.Component;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.Vector;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;
import pl.edu.icm.visnow.application.application.Application;
import pl.edu.icm.visnow.engine.main.ModuleSaturation;
import pl.edu.icm.visnow.gui.widgets.CoveringLayerPanel;
import pl.edu.icm.visnow.lib.types.VNDataAcceptor;
import pl.edu.icm.visnow.lib.types.VNGeometryObject;
import pl.edu.icm.visnow.system.main.VisNow;
import pl.edu.icm.visnow.system.utils.usermessage.Level;
import pl.edu.icm.visnow.system.utils.usermessage.UserMessage;

/**
 * This class describes module instance from the user's point of view. It is a
 * template to be filled by the author of a module and provides most of the
 * methods that author will need. <br><br>
 * <b>IMPORTANT FUNCTIONS:</b><br> <a
 * href="#setProgress(float)">setProgress(float progress)</a><br>
 * <a href="#isAllowed()">isAllowed()</a><br><br>
 *
 * @author Hubert Orlik-Grzesik, University of Warsaw, ICM
 * @see pl.edu.icm.visnow.engine.main.ModuleBox
 */
public abstract class ModuleCore extends VolatileModuleCore
{

   private static final Logger LOGGER = Logger.getLogger(ModuleCore.class);
   protected CoveringLayerPanel coveringLayerPanel = new CoveringLayerPanel();

   private boolean forceFlag = false;
   
   protected ModuleCore()
   {
      coveringLayerPanel.setText("<html>Module has no<br/>required input data</html>");
      initInputs();
      initOutputs();
      initParameters();
   }

   //<editor-fold defaultstate="collapsed" desc=" [VAR] Ports ">
   //--------------------------------------------------------------------------
   /**
    * Inputs of the module.
    */
   protected Inputs inputs;
   /**
    * Outputs of the module.
    */
   protected Outputs outputs;
   /**
    * Parameters of the module.
    */
   protected Parameters parameters;

   /**
    * Access to the inputs of the module.
    *
    * @return Module inputs.
    */
   public Inputs getInputs()
   {
      return inputs;
   }

   /**
    * Access to the outputs of the module.
    *
    * @return Module outputs.
    */
   public Outputs getOutputs()
   {
      return outputs;
   }

   /**
    * Access to the registered parameters of the module.
    *
    * @return Registered module parameters.
    */
   public Parameters getParameters()
   {
      return parameters;
   }

   //--------------------------------------------------------------------------
   //</editor-fold>
   //<editor-fold defaultstate="collapsed" desc=" Obsluga portow ">
   //--------------------------------------------------------------------------
   /**
    * Returns first data object found on the input. This method doesn't
    * guarantee any particular order.
    *
    * @param name Name of the input.
    * @return One of data objects present on the input. Null if the input is
    * unlinked.
    */
   public final VNData getInputFirstData(String name)
   {
      if (getInputs() == null)
      {
         return null;
      }
      if (getInputs().getInput(name) == null)
      {
         return null;
      }
      return getInputs().getInput(name).getFirstData();
   }

   /**
    * Returns first value found on the input. This method doesn't guarantee any
    * particular order.
    *
    * @param name Name of the input.
    * @return Some of values found on the input. If the port is unlinked, this
    * method returns the default value.
    */
   public final Object getInputFirstValue(String name)
   {
      if (getInputs() == null)
      {
         return null;
      }
      if (getInputs().getInput(name) == null)
      {
         return null;
      }
      return getInputs().getInput(name).getFirstValue();
   }

   /**
    * Returns vector of all data objects present on the input.
    *
    * @param name Name of the input.
    * @return Vector of all present data objects. If the input is unlinked, it
    * is empty.
    */
   public final Vector<VNData> getInputDatas(String name)
   {
      if (getInputs() == null)
      {
         return null;
      }
      if (getInputs().getInput(name) == null)
      {
         return null;
      }
      return getInputs().getInput(name).getDatas();
   }

   /**
    * Returns vector of all values present on the input.
    *
    * @param name Name of the input.
    * @return Vector of all values. If the input is unlinked, it is empty.
    */
   public final Vector<Object> getInputValues(String name)
   {
      if (getInputs() == null)
      {
         return null;
      }
      if (getInputs().getInput(name) == null)
      {
         return null;
      }
      return getInputs().getInput(name).getValues();
   }

   //--------------------------------------------------------------------------
   /**
    * Sets the value on given output and mark it as fresh. Object class must be
    * accepted by the port.
    *
    * @param name Name of the output.
    * @param value Value to be set.
    * @return True if the value was correctly set. If any error occured (i.eggs.
    * the object class was incorrect) the method returns false.
    */
   public final boolean setOutputValue(String name, Object value)
   {
      if (getOutputs() == null)
      {
         return false;
      }
      if (getOutputs().getOutput(name) == null)
      {
         return false;
      }
      return getOutputs().getOutput(name).setValue(value);
   }
   //--------------------------------------------------------------------------
   //</editor-fold>
   //<editor-fold defaultstate="collapsed" desc=" [VAR] Module ">
   /**
    * ModuleBox that this ModuleCore is part of.
    */
   private ModuleBoxFace module;

   public ModuleBoxFace getModuleBoxEgg()
   {
      return module;
   }

   //TODO try to make it private
   /**
    * FOR INTERNAL USE ONLY
    */
   public final void setModuleBoxEgg(ModuleBoxFace module)
   {
      this.module = module;
      inputs.setModuleBox(module);
      outputs.setModuleBox(module);
   }

   public String getName()
   {
      if (module != null)
      {
         return module.getName();
      }
      return null;
   }
   //</editor-fold>
   //<editor-fold defaultstate="collapsed" desc=" [VAR] Progress, Permission ">
   //--------------------------------------------------------------------------
   /**
    * Float value in [0,1] representing progress level of module action. 0 is
    * begining, 1 is the end.
    */
   private float progress;

   /**
    * Returns the progress level of the module.
    *
    * @return Progress level, float number belonging to [0, 1].
    */
   public final float getProgress()
   {
      return progress;
   }

   /**
    * Sets the progress level of the module.
    *
    * @param progress Current progress level.
    * @return <span style="color: #ffffff; font-weight: bold;">
    * If this method returns false, the computation should be stopped
    * immediatelly.</span>
    * @see #isAllowed()
    */
   protected boolean setProgress(float progress)
   {
      /*
       * TODO __MOCNO__ oddzielic od ciezkiego GUI
       */
      this.progress = progress;
      if (getModuleBoxEgg() != null)
      {
         getModuleBoxEgg().setProgress(progress);
      }
      /*
       * TODO getModuleBoxEgg().fireProgressUpdate();
       */
      return isAllowed();
   }

   /**
    * The metod checks whether the computation was not cancelled. Since Java
    * currently doesn't support thread stopping, this method should be invoked
    * frequently (especially in heavy modules) to check whether the computation
    * was not interrupted.
    *
    * @return <span style="color: #ffffff; font-weight: bold;">
    * If this method returns false, the computation should be stopped
    * immediatelly.</span>
    */
   protected final boolean isAllowed()
   {//TODO
      return true;
   }

   /*
    * TODO: interrupting
    */
   //--------------------------------------------------------------------------
   //</editor-fold>
   //<editor-fold defaultstate="collapsed" desc=" [Var] CoreName ">
   //--------------------------------------------------------------------------
   /**
    * Object containing library information of the module.
    */
   private CoreName coreName;

   /**
    * Returns lightweight object sufficient to store and load instance of
    * current module. Doesn't store module name nor it's parameters.
    *
    * @return Object containing library information of the module.
    */
   public CoreName getCoreName()
   {
      return coreName;
   }

   /**
    * FOR INTERNAL USE ONLY.
    */
   public void setLibraryInfo(String libraryName, String className)
   {
      this.coreName = new CoreName(libraryName, className);
      //this.standardName = standardName;
   }

   public void setLibraryInfo(String libraryName, String libraryCoreName, String className)
   {
      this.coreName = new CoreName(libraryName, libraryCoreName, className);
      //this.standardName = standardName;
   }
   //--------------------------------------------------------------------------
   //</editor-fold>
   
   public void setPanel(Component component)
   {
      coveringLayerPanel.addContent(component);
   }

   public Component getPanel()
   {
      return coveringLayerPanel;
   }

   //</editor-fold>
   //<editor-fold defaultstate="collapsed" desc=" [VAR] Application ">   
   private Application application = null;

   /**
    * @return the application
    */
   public Application getApplication()
   {
      return application;
   }

   /**
    * @param application the application to set
    */
   public void setApplication(Application application)
   {
      this.application = application;
   }
   //</editor-fold>


   //<editor-fold defaultstate="collapsed" desc=" EVENTS ">
   //--------------------------------------------------------------------------
   /**
    * Event invoked when the new computation wave is starting in the current
    * module. Currently not implemented.
    */
   public void onWaveStarting()
   {
   }

   /**
    * Event invoked when the computation wave started in the current module is
    * finished. Currently not implemented.
    */
   public void onWaveFinalizing()
   {
   }

   /**
    * Event invoked when new message is received by the engine element of the
    * module. Currently not implemented.
    *
    * @see pl.edu.icm.visnow.engine.main.ModuleElement
    */
   public void onMessageReceived()
   {
   }

   /**
    * Event invoked when the deactivated computation wave reaches current
    * module. Should be overriden when some preparations were made during the
    * notifying phase that should be cleaned up.
    *
    * @see Object TODO - engine description
    */
   public void onInactive()
   {
   }

   //--------------------------------------------------------------------------
   /**
    * Event invoked when new value is set on non-triggering input port.
    * Currently not implemented.
    */
   public void onNonTriggeringUpdate()
   {
   }

   /**
    * Currently not implemented.
    */
   public void onPropagateActionFromUpdatedOutputs()
   {
   }

   //--------------------------------------------------------------------------
   /**
    * Event invoked when new link is attached to the input.
    */
   public void onInputAttach(LinkFace link)
   {
   }

   /**
    * Event invoked when a link is detached from the input.
    */
   public void onInputDetach(LinkFace link)
   {
   }

   /**
    * Event invoked when new link is attached to the output.
    */
   public void onOutputAttach(LinkFace link)
   {
   }

   /**
    * Event invoked when a link is detached from the output.
    */
   public void onOutputDetach(LinkFace link)
   {
   }

   //--------------------------------------------------------------------------
   /**
    * Event invoked when the construction of the module was finished. Any
    * element of the core constructor that uses internal module elements and
    * therefore leads to errors when invoked too early may be safely transferred
    * to this method and then invoked properly.
    */
   public void onInitFinished()
   {
      coveringLayerPanel.overlay(true);
   }

   /**
    * Event invoked when deleting module. It is called after all of the links to
    * the module, but before the module itself is removed from the engine. All
    * necessary cleanups and garbage collecting should be preformed here.
    */
   public void onDelete()
   {
   }

   //--------------------------------------------------------------------------
   //</editor-fold>
   //<editor-fold defaultstate="collapsed" desc=" Action ">
   //--------------------------------------------------------------------------
   /**
    * Method used to manually start action in current module. Could be invoked
    * <b>once</b> after parameters change.
    */
   public final void startAction()
   {   //TODO: zmiana nazwy na start()
      getModuleBoxEgg().startAction();
   }

   /**
    * Method used to indicate an error in calculations and necessity to stop the
    * whole current wave. Not yet implemented.
    */
   public final void interrupt()
   {
   }

   //--------------------------------------------------------------------------
   //</editor-fold>
   /* IMPORTANT SECTION */
   //<editor-fold defaultstate="collapsed" desc=" onActive ">
   //--------------------------------------------------------------------------
   /**
    * The main method of the module. Module calculations and setting output
    * value should be performed here. Invoked when the active wave reaches the
    * module.
    *
    * @see Object TODO - engine description
    */
   public abstract void onActive();
   //--------------------------------------------------------------------------

   public void onLocalActive()
   {
      onActive();
   }
   //</editor-fold>

   //<editor-fold defaultstate="collapsed" desc=" Port construction ">
   //--------------------------------------------------------------------------
   /**
    * Method used to initialize inputs of the module. By default, it creates
    * inputs from the given eggs.
    */
   @SuppressWarnings("unchecked")
   protected final void initInputs()
   {
      this.inputs = new Inputs(ModuleCore.getInputEggs(this.getClass().getName(), this.getClass().getClassLoader()));
   }

   /**
    * Method used to initialize outputs of the module. By default, it creates
    * outputs from the given eggs.
    */
   @SuppressWarnings("unchecked")
   protected final void initOutputs()
   {
      this.outputs = new Outputs(ModuleCore.getOutputEggs(this.getClass().getName(), this.getClass().getClassLoader()));
   }

   /**
    * Method used to initialize parameters of the module. By default, it creates
    * parameters from the given eggs.
    */
   @SuppressWarnings("unchecked")
   protected final void initParameters()
   {
      ParameterEgg[] parameterEggs = null;
      try
      {
         parameterEggs = (ParameterEgg[]) this.getClass().getMethod("getParameterEggs").invoke(this);
      } catch (NoSuchMethodException ex)
      {
         LOGGER.fatal(null, ex);
      } catch (SecurityException ex)
      {
         LOGGER.fatal(null, ex);
      } catch (IllegalAccessException ex)
      {
         LOGGER.fatal(null, ex);
      } catch (IllegalArgumentException ex)
      {
         LOGGER.fatal(null, ex);
      } catch (InvocationTargetException ex)
      {
         LOGGER.fatal(null, ex);
      }
      this.parameters = new Parameters(parameterEggs);
   }

   //protected boolean initInputs = true;
   //protected boolean initOutputs = true;
   //protected boolean initParameters = true;
   /**
    * Method used to initialize ports of the module. By default, it initializes
    * inputs, outputs and parameters.
    */
   public void initPorts()
   {
      //if(initInputs)
      initInputs();
      //if(initOutputs)
      initOutputs();
      //if(initParameters)
      initParameters();
   }

   //--------------------------------------------------------------------------
   //</editor-fold>
   //<editor-fold defaultstate="collapsed" desc=" Static port construction ">
   //--------------------------------------------------------------------------
   /**
    * Static method that gives the information about the inputs of the module.
    *
    * @return Table of eggs of the module inputs.
    */
   public static InputEgg[] getInputEggs()
   {
      return null;
   }

   /**
    * Static method that gives the information about the outputs of the module.
    *
    * @return Table of eggs of the module outputs.
    */
   public static OutputEgg[] getOutputEggs()
   {
      return null;
   }

   /**
    * Static method that gives the information about the parameters of the
    * module.
    *
    * @return Table of eggs of the module parameters.
    */
   public static ParameterEgg[] getParameterEggs()
   {
      return null;
   }

   //--------------------------------------------------------------------------
   //</editor-fold>
   //<editor-fold defaultstate="collapsed" desc=" Other ">
   //--------------------------------------------------------------------------
   /**
    * Method used to check if the module is ready to perform calculations. All
    * the non-standard conditions on the input state (such as dimension of the
    * input value) should be verified here. By default it allows performing
    * action (returns true).
    * <b> Currently not implemented. </b>
    *
    * @return True if the module is ready to perform its calculations.
    */
   public boolean isCoreReady()
   {
      return true;
   }

   /**
    * Checks whether the current module is a generator. Precisely, if it is
    * possible to start a valid action tree from this module, it should be
    * marked by overriding this function to return true. Particularly, this
    * should be performed in all the modules like readers, test objects, sample
    * generators etc.
    *
    * @return True, if it is possible to start a valid action tree from this
    * module.
    */
   public boolean isGenerator()
   {
      return false;
   }

   /**
    * Checks whether the current module is a viewer. Precisely, if it is
    * possible to sensibely finish an action tree with this module. This applies
    * for all viewers, loggers, export modules etc.
    *
    * @return True for viewers, false (by default) for other modules.
    */
   public boolean isViewer()
   {
      return false;
   }

   //--------------------------------------------------------------------------
   //</editor-fold>
   public static InputEgg[] getInputEggs(String classname, ClassLoader loader)
   {
      if (classname == null)
         return null;

      Class moduleClass;
      try
      {
         //moduleClass = ModuleCore.class.getClassLoader().loadClass(classname);
         moduleClass = loader.loadClass(classname);
      } catch (ClassNotFoundException ex)
      {
         System.err.println("ERROR loading InputEggs for class " + classname);
         return null;
      }
      if (moduleClass == null || moduleClass == java.lang.Class.class)
      {
         return null;
      }

      InputEgg[] e;
      try
      {
         try
         {
            e = (InputEgg[]) moduleClass.getField("inputEggs").get(null);
         } catch (NoSuchFieldException ex)
         {
            //jesli pole inputEggs jest private lub nie isnieje uzyj starej wersji
            Object o = Class.forName(classname).getMethod("getInputEggs", new Class[0]).invoke(null, new Object[0]);
            e = (InputEgg[]) o;
         }

         if (e == null)
         {
            //jesli pole inputEggs jest public lub nie istnieje sprobuj XML
            e = ModuleXMLReader.getInputEggsFromModuleXML(moduleClass.getPackage().getName(), loader);
            if (e != null)
            {
               //jesli udalo sie wczytac z XML ustaw inputEggs w klasie
               moduleClass.getField("inputEggs").set(null, e);
            } else
            {
               //jesli nie udalo sie wczttac z XML fallback do starej wersji
               Object o = Class.forName(classname).getMethod("getInputEggs", new Class[0]).invoke(null, new Object[0]);
               e = (InputEgg[]) o;
            }
         }
      } catch (ClassNotFoundException ex)
      {
         e = null;
      } catch (IOException ex)
      {
         e = null;
      } catch (IllegalAccessException ex)
      {
         e = null;
      } catch (IllegalArgumentException ex)
      {
         e = null;
      } catch (InvocationTargetException ex)
      {
         e = null;
      } catch (NoSuchFieldException ex)
      {
         e = null;
      } catch (NoSuchMethodException ex)
      {
         e = null;
      } catch (ParserConfigurationException ex)
      {
         e = null;
      } catch (SAXException ex)
      {
         e = null;
      } catch (SecurityException ex)
      {
         e = null;
      } catch (URISyntaxException ex)
      {
         e = null;
      }
      return e;
   }

   public static OutputEgg[] getOutputEggs(String classname, ClassLoader loader)
   {
      Class moduleClass = null;
      try
      {
         //moduleClass = ModuleCore.class.getClassLoader().loadClass(classname);
         moduleClass = loader.loadClass(classname);
      } catch (ClassNotFoundException ex)
      {
      }
      if (moduleClass == null || moduleClass == java.lang.Class.class)
      {
         return null;
      }

      OutputEgg[] eggs;
      try
      {
         try
         {
            eggs = (OutputEgg[]) moduleClass.getField("outputEggs").get(null);
         } catch (NoSuchFieldException ex)
         {
            //if outputEggs has private access use old version
            Method mth = Class.forName(classname).getMethod("getOutputEggs", new Class[0]);
            mth.setAccessible(true);
            Object obj = mth.invoke(null, new Object[0]);

            eggs = (OutputEgg[]) obj;
         }

         if (eggs == null)
         {
            //if outputEggs has public access try parsing XML description
            eggs = ModuleXMLReader.getOutputEggsFromModuleXML(moduleClass.getPackage().getName(), loader);
            if (eggs != null)
            {
               //if XML parsing succeeded add description
               moduleClass.getField("outputEggs").set(null, eggs);
            } else
            {
               //XML unreadable, fallback to old version
               Object obj = Class.forName(classname).getMethod("getOutputEggs", new Class[0]).invoke(null, new Object[0]);

               eggs = (OutputEgg[]) obj;
            }
         }
      } catch (ClassNotFoundException ex)
      {
         System.err.println("ERROR in outputs of module class " + classname + " - output port class not found: " + ex.getMessage());
         eggs = null;
      } catch (IOException ex)
      {
         eggs = null;
      } catch (IllegalAccessException ex)
      {
         eggs = null;
      } catch (IllegalArgumentException ex)
      {
         eggs = null;
      } catch (InvocationTargetException ex)
      {
         eggs = null;
      } catch (NoSuchFieldException ex)
      {
         eggs = null;
      } catch (NoSuchMethodException ex)
      {
         eggs = null;
      } catch (ParserConfigurationException ex)
      {
         eggs = null;
      } catch (SAXException ex)
      {
         eggs = null;
      } catch (SecurityException ex)
      {
         eggs = null;
      } catch (URISyntaxException ex)
      {
         eggs = null;
      }
      return eggs;
   }

   public static final String getHelpTopicID(String classname)
   {
      return classname.substring(0, classname.lastIndexOf("."));
   }

   /**
    * @return the forceFlag
    */
   public boolean isForceFlag()
   {
      return forceFlag;
   }

   protected boolean hideGUIwhenNoData = true;
   /**
    * 
    * @param hideGUIwhenNoData the hideGUIwhenNoData to set
    * if set to false, GUI is always exposed, even if no proper data is present
    */
   public void setHideGUIwhenNoData(boolean hideGUIwhenNoData)
   {
      this.hideGUIwhenNoData = hideGUIwhenNoData;
   }

   /**
    * @param forceFlag the forceFlag to set
    */
   public void setForceFlag(boolean forceFlag)
   {
      this.forceFlag = forceFlag;
   }


   protected void switchPanelToDummy()
   {
      if (hideGUIwhenNoData)
         coveringLayerPanel.overlay(true);
   }

   protected void switchPanelToGUI()
   {
         coveringLayerPanel.overlay(false);
   }

   public final void onSaturationChange(ModuleSaturation mSaturation, Input saturationReasonInput)
   {
      if (mSaturation == ModuleSaturation.wrongData)
      {
         String msg = "Wrong input data";
         StringBuilder s = new StringBuilder();
         s.append("<html><table>");
         if (saturationReasonInput != null)
         {
            VNDataAcceptor[] acceptors = saturationReasonInput.getVNDataAcceptors();
            Link saturationReasonLink = saturationReasonInput.getInputSaturationReasonLink();

            s.append("<tr><td colspan=2>Requires:</td><td colspan=2>Got:</td></tr>");

            s.append("<tr><td valign=top>&nbsp;&nbsp;</td><td valign=top>");
            if (acceptors != null && acceptors.length > 0)
            {
               for (int j = 0; j < acceptors.length; j++)
               {
                  s.append(acceptors[j].toHtmlString()).append("<br>");
                  if (j < acceptors.length - 1)
                  {
                     s.append("OR<br>");
                  }
               }
            }
            s.append("</td><td valign=top>&nbsp;&nbsp;</td><td valign=top>");
            if (saturationReasonLink != null)
            {
                    Object obj = saturationReasonLink.getOutput().getValue();
                    if(obj != null) {
                        s.append(obj.toString());                    
                    } else {
                        s.append("NULL");                    
                    }
            }
            s.append("</td></tr>");
         }
         s.append("</table></html>");

         String source = this.getName();
         if (saturationReasonInput != null)
         {
            source += "." + saturationReasonInput.getName();
         }
         coveringLayerPanel.overlay("<html>Wrong input data.<br>See below for details</html>");
         VisNow.get().userMessageSend(new UserMessage(
                 this.application.getTitle(),
                 source,
                 msg,
                 s.toString(),
                 Level.ERROR
                 ));
      }

      if (hideGUIwhenNoData
              && (mSaturation == ModuleSaturation.notLinked || mSaturation == ModuleSaturation.wrongData || mSaturation == ModuleSaturation.noData)
              && !this.isViewer()
              && !this.isGenerator()
              && !(this.getInputs() == null || this.getInputs().getInputs().isEmpty())
              && !(this.getOutputs() == null || this.getOutputs().getOutputs().isEmpty()))
      {
         this.switchPanelToDummy();
      } else
      {
         this.switchPanelToGUI();
      }
      onLocalSaturationChange(mSaturation);
   }

   public void onLocalSaturationChange(ModuleSaturation mSaturation)
   {
      if (mSaturation == ModuleSaturation.wrongData || mSaturation == ModuleSaturation.noData || mSaturation == ModuleSaturation.notLinked)
      {
         for (Output output : this.getOutputs())
         {
            if (output.getType() == VNGeometryObject.class)
            {
               continue;
            }
            output.setValue(null);
         }
      }
   }

}
