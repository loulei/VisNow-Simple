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

package pl.edu.icm.visnow.engine.core;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import pl.edu.icm.visnow.engine.exception.VNRuntimeException;

/**
 *
 * @author Hubert Orlik-Grzesik, University of Warsaw, ICM
 */
public class Parameters implements Iterable<Parameter>
{
   private static final boolean debug = false;// true;
   protected HashMap<String, Parameter> parameters;

   public HashMap<String, Parameter> getParameters()
   {
      return parameters;
   }

   public Parameter getParameter(String name)
   {
      return parameters.get(name);
   }

   public Object getValue(String name)
   {
      return parameters.get(name).getValue();
   }

   //TODO: unchecked?
   @SuppressWarnings("unchecked")
   public void setValue(String name, Object value)
   {
      try
      {
         parameters.get(name).setValue(value);
         fireParameterChanged(name);
      } catch (NullPointerException e)
      {
         //TODO: przechwycić?
         throw new VNRuntimeException(
                 200906171529L,
                 "NoSuchParameter: " + name,
                 null,
                 this,
                 Thread.currentThread());
      }
   }

   public Parameters(ParameterEgg[] parameterEggs)
   {
      parameters = new HashMap<String, Parameter>();
      if (parameterEggs != null)
         for (ParameterEgg egg : parameterEggs)
            parameters.put(egg.getName(), egg.hatch());
   }

   //<editor-fold defaultstate="collapsed" desc=" Write XML ">
   public String writeXML()
   {
      String ret = "";
      XStream xstream = new XStream();
      for (Parameter parameter : this)
      {
         ret += "<parameter name=\"" + parameter.getName() + "\">\n";
         ret += encode(xstream.toXML(parameter.getValue()));
         ret += "</parameter>\n";
      }
      return ret;
   }

   public boolean readXML(String str)
   {
      try
      {
         tryReadXML(str);
         return true;
      } catch (ParserConfigurationException ex)
      {
         System.out.println("parser exception"); //TODO: handling
      } catch (SAXException ex)
      {
         System.out.println("sax exception");
      } catch (IOException ex)
      {
         System.out.println("io exception");
      }
      return false;
   }

   public void tryReadXML(String str) throws ParserConfigurationException, SAXException, IOException
   {
      System.out.println("\nREADING!!!");
      XStream xstream = new XStream(new DomDriver());
      String xml = "<?xml version=\'1.0\' encoding=\'utf-8\'?>\n<everything>" + str + "</everything>";

      Node main = DocumentBuilderFactory.newInstance().
              newDocumentBuilder().
              parse(new InputSource(new StringReader(xml))).
              getDocumentElement();

      Vector<Node> paramNodes = new Vector<Node>();
      NodeList nl = main.getChildNodes();

      System.out.println("");
      for (int i = 0; i < nl.getLength(); ++i)
      {
         System.out.println("testing node - [" + nl.item(i).getNodeName() + "]");
         if (nl.item(i).getNodeName().equalsIgnoreCase("parameter"))
            paramNodes.add(nl.item(i));
      }

      System.out.println("");
      for (Node pn : paramNodes)
      {
         System.out.println("getting node - [" + pn.getAttributes().getNamedItem("name").getNodeValue() + "]");
         String par = pn.getAttributes().getNamedItem("name").getNodeValue();
         String val = decode(pn.getTextContent());
         Object value = xstream.fromXML(val);
         System.out.println("  val=[" + val + "]");
         if (value != null)
         {
            System.out.println("  val=[" + value.toString() + "]");
            this.setValue(par, value);
         }
      }

      System.out.println("");
      System.out.println("READ DONE!!!");
      System.out.println("");
   }

   private static String decode(String in)
   {
      String ret = "";
      StringTokenizer tokenizer = new StringTokenizer(in, "[]|", true);
      while (tokenizer.hasMoreTokens())
      {
         String next = tokenizer.nextToken();
         if (next.equals("["))
         {
            ret += "<";
            continue;
         }
         if (next.equals("]"))
         {
            ret += ">";
            continue;
         }
         if (next.equals("|"))
         {
            next = tokenizer.nextToken();
            switch (next.charAt(0))
            {
               case '{':
                  ret += "[";
                  break;
               case '}':
                  ret += "]";
                  break;
               case '+':
                  ret += "|";
                  break;
            }
            ret += next.substring(1);
            continue;
         }
         ret += next;
      }
      return ret;
   }

   private static String encode(String in)
   {
      String ret = "";
      StringTokenizer tokenizer = new StringTokenizer(in, "<>[]|", true);
      while (tokenizer.hasMoreTokens())
      {
         String next = tokenizer.nextToken();
         if (next.equals("|"))
         {
            ret += "|+";
            continue;
         }
         if (next.equals("<"))
         {
            ret += "[";
            continue;
         }
         if (next.equals(">"))
         {
            ret += "]";
            continue;
         }
         if (next.equals("["))
         {
            ret += "|{";
            continue;
         }
         if (next.equals("]"))
         {
            ret += "|}";
            continue;
         }
         ret += next;
      }
      return ret;
   }
   //</editor-fold>
   //<editor-fold defaultstate="collapsed" desc=" Active ">
   protected boolean active = true;

   public boolean isActive()
   {
      return active;
   }

   public void setActive(boolean active)
   {
      this.active = active;
      fireStateChanged();
   }

   public void setActiveValue(boolean active)
   {
      this.active = active;
   }
   //</editor-fold>

   //<editor-fold defaultstate="collapsed" desc=" Iterator ">
   public Iterator<Parameter> iterator()
   {
      return parameters.values().iterator();
   }
   //</editor-fold>
   //<editor-fold defaultstate="collapsed" desc=" Parameter change listeners ">
   private Vector<ParameterChangeListener> listeners = new Vector<ParameterChangeListener>();

   public synchronized void addParameterChangelistener(ParameterChangeListener listener)
   {
      listeners.add(listener);
   }

   public synchronized void removeParameterChangeListener(ParameterChangeListener listener)
   {
      listeners.remove(listener);
   }

   public void fireParameterChanged(String parameter)
   {
      for (ParameterChangeListener listener : listeners)
         listener.parameterChanged(parameter);
   }
   //</editor-fold>
   //<editor-fold defaultstate="collapsed" desc=" Know Change listeners ">
   /**
    * Utility field holding list of ChangeListeners.
    */
   protected transient ArrayList<ChangeListener> changeListenerList = new ArrayList<ChangeListener>();

   /**
    * Registers ChangeListener to receive events.
    * @param listener The listener to register.
    */
   public synchronized void addChangeListener(ChangeListener listener)
   {
      changeListenerList.add(listener);
   }

   /**
    * Removes ChangeListener from the list of listeners.
    * @param listener The listener to remove.
    */
   public synchronized void removeChangeListener(ChangeListener listener)
   {
      changeListenerList.remove(listener);
   }

   /**
    * Notifies all registered listeners about the event.
    *
    * @param object Parameter #1 of the <CODE>ChangeEvent<CODE> constructor.
    */
   public void fireStateChanged()
   {
      if (!active)
         return;
      ChangeEvent e = new ChangeEvent(this);
      for (int i = 0; i < changeListenerList.size(); i++) {
          changeListenerList.get(i).stateChanged(e);          
       }
   }
   //</editor-fold>
}
